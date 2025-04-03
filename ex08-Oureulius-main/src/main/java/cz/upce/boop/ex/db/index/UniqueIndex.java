package cz.upce.boop.ex.db.index;

import cz.upce.boop.ex.db.core.DatabaseEntity;
import cz.upce.boop.ex.db.core.PrimaryKey;
import cz.upce.boop.ex.db.index.entry.UniqueIndexEntry;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Implementation of a unique index
 * @param <T> The entity type
 * @param <K> The primary key type
 * @param <V> The indexed value type
 */
public class UniqueIndex<T extends DatabaseEntity<K>, K extends PrimaryKey<?>, V> 
        extends AbstractIndex<T, K, V> {
    
    // List of index entries
    private final ArrayList<UniqueIndexEntry<V, K>> entries;
    
    /**
     * Create a new unique index
     * @param name The name of the index
     * @param valueExtractor The function to extract the indexed value from an entity
     */
    public UniqueIndex(String name, Function<T, V> valueExtractor) {
        super(name, valueExtractor, true);
        this.entries = new ArrayList<>();
    }
    
    @Override
    public boolean addEntity(T entity) {
        V value = extractValue(entity);
        
        // Check if the value already exists in the index
        for (UniqueIndexEntry<V, K> entry : entries) {
            V existingValue = entry.getValue();
            if (existingValue == null ? value == null : existingValue.equals(value)) {
                return false; // Value already exists, violates uniqueness constraint
            }
        }
        
        // Add the new entry
        entries.add(new UniqueIndexEntry<>(value, entity.getId()));
        return true;
    }
    
    @Override
    public void removeEntity(T entity) {
        V value = extractValue(entity);
        K id = entity.getId();
        
        for (int i = 0; i < entries.size(); i++) {
            UniqueIndexEntry<V, K> entry = entries.get(i);
            V existingValue = entry.getValue();
            K existingId = entry.getPrimaryKey();
            
            if ((existingValue == null ? value == null : existingValue.equals(value)) 
                    && existingId.equals(id)) {
                entries.remove(i);
                return;
            }
        }
    }
    
    @Override
    public List<K> findByValue(V value) {
        List<K> result = new ArrayList<>();
        
        for (UniqueIndexEntry<V, K> entry : entries) {
            V existingValue = entry.getValue();
            if (existingValue == null ? value == null : existingValue.equals(value)) {
                result.add(entry.getPrimaryKey());
                break; // Since this is a unique index, we can stop after finding one match
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
        for (UniqueIndexEntry<V, K> entry : entries) {
            oos.writeObject(entry.getValue()); // Value
            oos.writeObject(entry.getPrimaryKey()); // Primary key
        }
    }
    
    @Override
    protected void loadEntries(ObjectInputStream ois, List<T> entities) throws IOException, ClassNotFoundException {
        // Read the number of entries
        int entryCount = ois.readInt();
        
        // Read each entry
        for (int i = 0; i < entryCount; i++) {
            V value = (V) ois.readObject();
            K primaryKey = (K) ois.readObject();
            
            // Add the entry to the index
            entries.add(new UniqueIndexEntry<>(value, primaryKey));
        }
    }
}