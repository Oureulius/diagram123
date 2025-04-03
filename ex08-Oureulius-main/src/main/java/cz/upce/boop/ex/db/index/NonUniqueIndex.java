package cz.upce.boop.ex.db.index;

import cz.upce.boop.ex.db.core.DatabaseEntity;
import cz.upce.boop.ex.db.core.PrimaryKey;
import cz.upce.boop.ex.db.index.entry.NonUniqueIndexEntry;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Implementation of a non-unique index
 * @param <T> The entity type
 * @param <K> The primary key type
 * @param <V> The indexed value type
 */
public class NonUniqueIndex<T extends DatabaseEntity<K>, K extends PrimaryKey<?>, V> 
        extends AbstractIndex<T, K, V> {
    
    // List of index entries
    private final ArrayList<NonUniqueIndexEntry<V, K>> entries;
    
    /**
     * Create a new non-unique index
     * @param name The name of the index
     * @param valueExtractor The function to extract the indexed value from an entity
     */
    public NonUniqueIndex(String name, Function<T, V> valueExtractor) {
        super(name, valueExtractor, false);
        this.entries = new ArrayList<>();
    }
    
    @Override
    public boolean addEntity(T entity) {
        V value = extractValue(entity);
        K id = entity.getId();
        
        // Check if the value already exists in the index
        for (NonUniqueIndexEntry<V, K> entry : entries) {
            V existingValue = entry.getValue();
            if (existingValue == null ? value == null : existingValue.equals(value)) {
                // Value exists, add the primary key to the list
                entry.addPrimaryKey(id);
                return true;
            }
        }
        
        // Value doesn't exist, create a new entry
        ArrayList<K> ids = new ArrayList<>();
        ids.add(id);
        entries.add(new NonUniqueIndexEntry<>(value, ids));
        return true;
    }
    
    @Override
    public void removeEntity(T entity) {
        V value = extractValue(entity);
        K id = entity.getId();
        
        for (int i = 0; i < entries.size(); i++) {
            NonUniqueIndexEntry<V, K> entry = entries.get(i);
            V existingValue = entry.getValue();
            
            if (existingValue == null ? value == null : existingValue.equals(value)) {
                entry.removePrimaryKey(id);
                
                // If the list is empty, remove the entry
                if (entry.isEmpty()) {
                    entries.remove(i);
                }
                return;
            }
        }
    }
    
    @Override
    public List<K> findByValue(V value) {
        List<K> result = new ArrayList<>();
        
        for (NonUniqueIndexEntry<V, K> entry : entries) {
            V existingValue = entry.getValue();
            if (existingValue == null ? value == null : existingValue.equals(value)) {
                result.addAll(entry.getPrimaryKeys());
                break; // We found the matching value
            }
        }
        
        return result;
    }
    
    @Override
    public void clear() {
        entries.clear();
    }
    
    @Override
    protected void saveEntries(ObjectOutputStream oos) throws IOException {
        // Write the number of entries
        oos.writeInt(entries.size());
        
        // Write each entry
        for (NonUniqueIndexEntry<V, K> entry : entries) {
            oos.writeObject(entry.getValue()); // Value
            
            ArrayList<K> ids = entry.getPrimaryKeys();
            
            // Write the number of IDs
            oos.writeInt(ids.size());
            
            // Write each ID
            for (K id : ids) {
                oos.writeObject(id);
            }
        }
    }
    
    @Override
    protected void loadEntries(ObjectInputStream ois, List<T> entities) throws IOException, ClassNotFoundException {
        // Read the number of entries
        int entryCount = ois.readInt();
        
        // Read each entry
        for (int i = 0; i < entryCount; i++) {
            V value = (V) ois.readObject();
            
            // Read the number of IDs
            int idCount = ois.readInt();
            ArrayList<K> ids = new ArrayList<>(idCount);
            
            // Read each ID
            for (int j = 0; j < idCount; j++) {
                K id = (K) ois.readObject();
                ids.add(id);
            }
            
            // Add the entry to the index
            entries.add(new NonUniqueIndexEntry<>(value, ids));
        }
    }
}