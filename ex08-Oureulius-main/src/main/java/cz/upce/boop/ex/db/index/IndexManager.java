package cz.upce.boop.ex.db.index;

import cz.upce.boop.ex.db.core.DatabaseEntity;
import cz.upce.boop.ex.db.core.DatabaseException;
import cz.upce.boop.ex.db.core.PrimaryKey;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Manager for database indexes
 * @param <T> The entity type
 * @param <K> The primary key type
 */
public class IndexManager<T extends DatabaseEntity<K>, K extends PrimaryKey<?>> {
    
    private final List<Index<T, K, ?>> indexes;
    private final String indexDirPath;
    
    /**
     * Create a new index manager
     * @param indexDirPath The directory to store index files
     */
    public IndexManager(String indexDirPath) {
        this.indexes = new ArrayList<>();
        this.indexDirPath = indexDirPath;
        
        // Create the index directory if it doesn't exist
        File indexDir = new File(indexDirPath);
        if (!indexDir.exists()) {
            if (!indexDir.mkdirs()) {
                throw new DatabaseException("Failed to create index directory: " + indexDirPath);
            }
        }
    }
    
    /**
     * Create a unique index
     * @param <V> The indexed value type
     * @param name The name of the index
     * @param valueExtractor The function to extract the indexed value from an entity
     * @return The created index
     */
    public <V> Index<T, K, V> createUniqueIndex(String name, Function<T, V> valueExtractor) {
        // Check if an index with the same name already exists
        if (getIndex(name) != null) {
            throw new DatabaseException("Index with name '" + name + "' already exists");
        }
        
        Index<T, K, V> index = new UniqueIndex<>(name, valueExtractor);
        indexes.add(index);
        return index;
    }
    
    /**
     * Create a non-unique index
     * @param <V> The indexed value type
     * @param name The name of the index
     * @param valueExtractor The function to extract the indexed value from an entity
     * @return The created index
     */
    public <V> Index<T, K, V> createNonUniqueIndex(String name, Function<T, V> valueExtractor) {
        // Check if an index with the same name already exists
        if (getIndex(name) != null) {
            throw new DatabaseException("Index with name '" + name + "' already exists");
        }
        
        Index<T, K, V> index = new NonUniqueIndex<>(name, valueExtractor);
        indexes.add(index);
        return index;
    }
    
    /**
     * Create a multi-column index
     * @param name The name of the index
     * @param valueExtractor The function to extract the indexed values from an entity
     * @param unique Whether this index enforces uniqueness
     * @return The created index
     */
    public Index<T, K, List<?>> createMultiColumnIndex(String name, Function<T, List<?>> valueExtractor, boolean unique) {
        // Check if an index with the same name already exists
        if (getIndex(name) != null) {
            throw new DatabaseException("Index with name '" + name + "' already exists");
        }
        
        Index<T, K, List<?>> index = new MultiColumnIndex<>(name, valueExtractor, unique);
        indexes.add(index);
        return index;
    }
    
    /**
     * Get an index by name
     * @param <V> The indexed value type
     * @param name The name of the index
     * @return The index, or null if not found
     */
    @SuppressWarnings("unchecked")
    public <V> Index<T, K, V> getIndex(String name) {
        for (Index<T, K, ?> index : indexes) {
            if (index.getName().equals(name)) {
                return (Index<T, K, V>) index;
            }
        }
        return null;
    }
    
    /**
     * Remove an index by name
     * @param name The name of the index
     * @return true if the index was removed, false if not found
     */
    public boolean removeIndex(String name) {
        for (int i = 0; i < indexes.size(); i++) {
            if (indexes.get(i).getName().equals(name)) {
                indexes.remove(i);
                
                // Delete the index file if it exists
                File indexFile = new File(getIndexFilePath(name));
                if (indexFile.exists()) {
                    indexFile.delete();
                }
                
                return true;
            }
        }
        return false;
    }
    
    /**
     * Find primary keys of entities by indexed value
     * @param <V> The indexed value type
     * @param indexName The name of the index
     * @param value The value to search for
     * @return List of primary keys matching the value
     */
    public <V> List<K> findByIndexedValue(String indexName, V value) {
        Index<T, K, V> index = getIndex(indexName);
        if (index == null) {
            throw new DatabaseException("Index with name '" + indexName + "' not found");
        }
        
        return index.findByValue(value);
    }
    
    /**
     * Add an entity to all indexes
     * @param entity The entity to add
     * @throws DatabaseException If adding the entity violates a unique constraint
     */
    public void addEntityToIndexes(T entity) {
        for (Index<T, K, ?> index : indexes) {
            if (!addEntityToIndex(entity, index)) {
                throw new DatabaseException("Entity violates unique constraint for index '" + index.getName() + "'");
            }
        }
    }
    
    /**
     * Add an entity to a specific index
     * @param entity The entity to add
     * @param index The index to add the entity to
     * @return true if the entity was added successfully, false if it violates a unique constraint
     */
    @SuppressWarnings("unchecked")
    private <V> boolean addEntityToIndex(T entity, Index<T, K, ?> index) {
        return ((Index<T, K, V>) index).addEntity(entity);
    }
    
    /**
     * Remove an entity from all indexes
     * @param entity The entity to remove
     */
    public void removeEntityFromIndexes(T entity) {
        for (Index<T, K, ?> index : indexes) {
            removeEntityFromIndex(entity, index);
        }
    }
    
    /**
     * Remove an entity from a specific index
     * @param entity The entity to remove
     * @param index The index to remove the entity from
     */
    @SuppressWarnings("unchecked")
    private <V> void removeEntityFromIndex(T entity, Index<T, K, ?> index) {
        ((Index<T, K, V>) index).removeEntity(entity);
    }
    
    /**
     * Clear all indexes
     */
    public void clearIndexes() {
        for (Index<T, K, ?> index : indexes) {
            index.clear();
        }
    }
    
    /**
     * Save all indexes to files
     */
    public void saveIndexes() {
        for (Index<T, K, ?> index : indexes) {
            String indexFilePath = getIndexFilePath(index.getName());
            index.saveToFile(indexFilePath);
        }
    }
    
    /**
     * Load all indexes from files
     * @param entities The list of entities to reference
     */
    public void loadIndexes(List<T> entities) {
        for (Index<T, K, ?> index : indexes) {
            String indexFilePath = getIndexFilePath(index.getName());
            index.loadFromFile(indexFilePath, entities);
        }
    }
    
    /**
     * Get the file path for an index
     * @param indexName The name of the index
     * @return The file path
     */
    private String getIndexFilePath(String indexName) {
        return indexDirPath + File.separator + indexName + ".idx";
    }
    
    /**
     * Get all indexes
     * @return The list of indexes
     */
    public List<Index<T, K, ?>> getIndexes() {
        return new ArrayList<>(indexes);
    }
}