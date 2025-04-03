package cz.upce.boop.ex.db.index;

import cz.upce.boop.ex.db.core.DatabaseEntity;
import cz.upce.boop.ex.db.core.PrimaryKey;

import java.util.List;
import java.util.function.Function;

/**
 * Interface for database indexes
 * @param <T> The entity type
 * @param <K> The primary key type
 * @param <V> The indexed value type
 */
public interface Index<T extends DatabaseEntity<K>, K extends PrimaryKey<?>, V> {
    
    /**
     * Get the name of the index
     * @return The index name
     */
    String getName();
    
    /**
     * Add an entity to the index
     * @param entity The entity to add
     * @return true if the entity was added successfully, false if it violates a unique constraint
     */
    boolean addEntity(T entity);
    
    /**
     * Remove an entity from the index
     * @param entity The entity to remove
     */
    void removeEntity(T entity);
    
    /**
     * Find primary keys of entities by indexed value
     * @param value The value to search for
     * @return List of primary keys matching the value
     */
    List<K> findByValue(V value);
    
    /**
     * Check if this index enforces uniqueness
     * @return true if the index is unique, false otherwise
     */
    boolean isUnique();
    
    /**
     * Get the function that extracts the indexed value from an entity
     * @return The value extractor function
     */
    Function<T, V> getValueExtractor();
    
    /**
     * Clear all entries from the index
     */
    void clear();
    
    /**
     * Save the index to a file
     * @param filePath The path to save the index to
     */
    void saveToFile(String filePath);
    
    /**
     * Load the index from a file
     * @param filePath The path to load the index from
     * @param entities The list to load entities 
     */
    void loadFromFile(String filePath, List<T> entities);
}