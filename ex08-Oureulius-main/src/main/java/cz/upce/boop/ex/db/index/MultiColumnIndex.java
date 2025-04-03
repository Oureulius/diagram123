package cz.upce.boop.ex.db.index;

import cz.upce.boop.ex.db.core.DatabaseEntity;
import cz.upce.boop.ex.db.core.PrimaryKey;
import cz.upce.boop.ex.db.index.entry.MultiColumnIndexEntry;
import cz.upce.boop.ex.db.index.entry.NonUniqueMultiColumnIndexEntry;
import cz.upce.boop.ex.db.index.entry.UniqueMultiColumnIndexEntry;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Implementation of a multi-column index
 * @param <T> The entity type
 * @param <K> The primary key type
 */
public class MultiColumnIndex<T extends DatabaseEntity<K>, K extends PrimaryKey<?>> 
        extends AbstractIndex<T, K, List<?>> {
    
    // List of index entries
    private final ArrayList<MultiColumnIndexEntry<K>> entries;
    
    /**
     * Create a new multi-column index
     * @param name The name of the index
     * @param valueExtractor The function to extract the indexed values from an entity
     * @param unique Whether this index enforces uniqueness
     */
    public MultiColumnIndex(String name, Function<T, List<?>> valueExtractor, boolean unique) {
        super(name, valueExtractor, unique);
        this.entries = new ArrayList<>();
    }
    
    @Override
    public boolean addEntity(T entity) {
        List<?> values = extractValue(entity);
        K id = entity.getId();
        
        if (isUnique()) {
            // Check if the values already exist in the index
            for (MultiColumnIndexEntry<K> entry : entries) {
                List<?> existingValues = entry.getValues();
                if (areListsEqual(existingValues, values)) {
                    return false; // Values already exist, violates uniqueness constraint
                }
            }
            
            // Add the new entry
            entries.add(new UniqueMultiColumnIndexEntry<>(values, id));
        } else {
            // Check if the values already exist in the index
            for (MultiColumnIndexEntry<K> entry : entries) {
                List<?> existingValues = entry.getValues();
                if (areListsEqual(existingValues, values)) {
                    // Values exist, add the primary key to the list
                    ((NonUniqueMultiColumnIndexEntry<K>) entry).addPrimaryKey(id);
                    return true;
                }
            }
            
            // Values don't exist, create a new entry
            ArrayList<K> ids = new ArrayList<>();
            ids.add(id);
            entries.add(new NonUniqueMultiColumnIndexEntry<>(values, ids));
        }
        
        return true;
    }
    
    @Override
    public void removeEntity(T entity) {
        List<?> values = extractValue(entity);
        K id = entity.getId();
        
        for (int i = 0; i < entries.size(); i++) {
            MultiColumnIndexEntry<K> entry = entries.get(i);
            List<?> existingValues = entry.getValues();
            
            if (areListsEqual(existingValues, values)) {
                if (isUnique()) {
                    UniqueMultiColumnIndexEntry<K> uniqueEntry = (UniqueMultiColumnIndexEntry<K>) entry;
                    K existingId = uniqueEntry.getPrimaryKey();
                    if (existingId.equals(id)) {
                        entries.remove(i);
                    }
                } else {
                    NonUniqueMultiColumnIndexEntry<K> nonUniqueEntry = (NonUniqueMultiColumnIndexEntry<K>) entry;
                    nonUniqueEntry.removePrimaryKey(id);
                    
                    // If the list is empty, remove the entry
                    if (nonUniqueEntry.isEmpty()) {
                        entries.remove(i);
                    }
                }
                return;
            }
        }
    }
    
    @Override
    public List<K> findByValue(List<?> values) {
        List<K> result = new ArrayList<>();
        
        for (MultiColumnIndexEntry<K> entry : entries) {
            List<?> existingValues = entry.getValues();
            
            if (areListsEqual(existingValues, values)) {
                if (isUnique()) {
                    UniqueMultiColumnIndexEntry<K> uniqueEntry = (UniqueMultiColumnIndexEntry<K>) entry;
                    result.add(uniqueEntry.getPrimaryKey());
                } else {
                    NonUniqueMultiColumnIndexEntry<K> nonUniqueEntry = (NonUniqueMultiColumnIndexEntry<K>) entry;
                    result.addAll(nonUniqueEntry.getPrimaryKeys());
                }
                break; // We found the matching values
            }
        }
        
        return result;
    }
    
    @Override
    public void clear() {
        entries.clear();
    }
    
    /**
     * Check if two lists have the same elements in the same order
     * @param list1 The first list
     * @param list2 The second list
     * @return true if the lists are equal, false otherwise
     */
    private boolean areListsEqual(List<?> list1, List<?> list2) {
        if (list1.size() != list2.size()) {
            return false;
        }
        
        for (int i = 0; i < list1.size(); i++) {
            Object obj1 = list1.get(i);
            Object obj2 = list2.get(i);
            
            if (obj1 == null) {
                if (obj2 != null) {
                    return false;
                }
            } else if (!obj1.equals(obj2)) {
                return false;
            }
        }
        
        return true;
    }
    
    @Override
    protected void saveEntries(ObjectOutputStream oos) throws IOException {
        // Write the number of entries
        oos.writeInt(entries.size());
        
        // Write each entry
        for (MultiColumnIndexEntry<K> entry : entries) {
            List<?> values = entry.getValues();
            
            // Write the number of values
            oos.writeInt(values.size());
            
            // Write each value
            for (Object value : values) {
                oos.writeObject(value);
            }
            
            if (isUnique()) {
                // Write the primary key
                UniqueMultiColumnIndexEntry<K> uniqueEntry = (UniqueMultiColumnIndexEntry<K>) entry;
                oos.writeObject(uniqueEntry.getPrimaryKey());
            } else {
                NonUniqueMultiColumnIndexEntry<K> nonUniqueEntry = (NonUniqueMultiColumnIndexEntry<K>) entry;
                ArrayList<K> ids = nonUniqueEntry.getPrimaryKeys();
                
                // Write the number of IDs
                oos.writeInt(ids.size());
                
                // Write each ID
                for (K id : ids) {
                    oos.writeObject(id);
                }
            }
        }
    }
    
    @Override
    protected void loadEntries(ObjectInputStream ois, List<T> entities) throws IOException, ClassNotFoundException {
        // Read the number of entries
        int entryCount = ois.readInt();
        
        // Read each entry
        for (int i = 0; i < entryCount; i++) {
            // Read the number of values
            int valueCount = ois.readInt();
            List<Object> values = new ArrayList<>(valueCount);
            
            // Read each value
            for (int j = 0; j < valueCount; j++) {
                Object value = ois.readObject();
                values.add(value);
            }
            
            if (isUnique()) {
                // Read the primary key
                K primaryKey = (K) ois.readObject();
                
                // Add the entry to the index
                entries.add(new UniqueMultiColumnIndexEntry<>(values, primaryKey));
            } else {
                // Read the number of IDs
                int idCount = ois.readInt();
                ArrayList<K> ids = new ArrayList<>(idCount);
                
                // Read each ID
                for (int j = 0; j < idCount; j++) {
                    K id = (K) ois.readObject();
                    ids.add(id);
                }
                
                // Add the entry to the index
                entries.add(new NonUniqueMultiColumnIndexEntry<>(values, ids));
            }
        }
    }
}