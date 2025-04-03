package cz.upce.boop.ex.db.index;

import cz.upce.boop.ex.db.core.DatabaseEntity;
import cz.upce.boop.ex.db.core.DatabaseException;
import cz.upce.boop.ex.db.core.EntitySerializer;
import cz.upce.boop.ex.db.core.FileEntityRepository;
import cz.upce.boop.ex.db.core.PrimaryKey;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Extension of FileEntityRepository with indexing support
 *
 * @param <T> The entity type
 * @param <K> The primary key type
 */
public class IndexedFileEntityRepository<T extends DatabaseEntity<K>, K extends PrimaryKey<?>>
        extends FileEntityRepository<T, K> {

    private final IndexManager<T, K> indexManager;

    /**
     * Create a new indexed file entity repository
     *
     * @param dirPath The directory to store entity files
     * @param serializer The serializer for entities
     */
    public IndexedFileEntityRepository(String dirPath, EntitySerializer<T> serializer) {
        super(dirPath, serializer);
        this.indexManager = new IndexManager<>(dirPath + File.separator + "indexes");
    }

    /**
     * Create a unique index
     *
     * @param <V> The indexed value type
     * @param name The name of the index
     * @param valueExtractor The function to extract the indexed value from an
     * entity
     * @return The created index
     */
    public <V> Index<T, K, V> createUniqueIndex(String name, Function<T, V> valueExtractor) {
        Index<T, K, V> index = indexManager.createUniqueIndex(name, valueExtractor);

        // Add all existing entities to the index
        List<T> entities = findAll();
        for (T entity : entities) {
            if (!index.addEntity(entity)) {
                // If adding an entity fails, remove the index and throw an exception
                indexManager.removeIndex(name);
                throw new DatabaseException("Failed to create unique index '" + name
                        + "': duplicate values found in existing entities");
            }
        }

        // Save the index
        indexManager.saveIndexes();

        return index;
    }

    /**
     * Create a non-unique index
     *
     * @param <V> The indexed value type
     * @param name The name of the index
     * @param valueExtractor The function to extract the indexed value from an
     * entity
     * @return The created index
     */
    public <V> Index<T, K, V> createNonUniqueIndex(String name, Function<T, V> valueExtractor) {
        Index<T, K, V> index = indexManager.createNonUniqueIndex(name, valueExtractor);

        // Add all existing entities to the index
        List<T> entities = findAll();
        for (T entity : entities) {
            index.addEntity(entity);
        }

        // Save the index
        indexManager.saveIndexes();

        return index;
    }

    /**
     * Create a multi-column index
     *
     * @param name The name of the index
     * @param valueExtractor The function to extract the indexed values from an
     * entity
     * @param unique Whether this index enforces uniqueness
     * @return The created index
     */
    public Index<T, K, List<?>> createMultiColumnIndex(String name, Function<T, List<?>> valueExtractor, boolean unique) {
        Index<T, K, List<?>> index = indexManager.createMultiColumnIndex(name, valueExtractor, unique);

        // Add all existing entities to the index
        List<T> entities = findAll();
        for (T entity : entities) {
            if (!index.addEntity(entity)) {
                // If adding an entity fails, remove the index and throw an exception
                indexManager.removeIndex(name);
                throw new DatabaseException("Failed to create unique multi-column index '" + name
                        + "': duplicate values found in existing entities");
            }
        }

        // Save the index
        indexManager.saveIndexes();

        return index;
    }

    /**
     * Get an index by name
     *
     * @param <V> The indexed value type
     * @param name The name of the index
     * @return The index, or null if not found
     */
    public <V> Index<T, K, V> getIndex(String name) {
        return indexManager.getIndex(name);
    }

    /**
     * Remove an index by name
     *
     * @param name The name of the index
     * @return true if the index was removed, false if not found
     */
    public boolean removeIndex(String name) {
        return indexManager.removeIndex(name);
    }

    /**
     * Find entities by indexed value
     *
     * @param <V> The indexed value type
     * @param indexName The name of the index
     * @param value The value to search for
     * @return List of entities matching the value
     */
    public <V> List<T> findByIndexedValue(String indexName, V value) {
        List<K> primaryKeys = indexManager.findByIndexedValue(indexName, value);
        List<T> result = new ArrayList<>();

        for (K primaryKey : primaryKeys) {
            T entity = findById(primaryKey);
            if (entity != null) {
                result.add(entity);
            }
        }

        return result;
    }

    @Override
    public void save(T entity) {
        // Check if the entity violates any unique constraints
        if (!indexManager.getIndexes().isEmpty()) {
            for (Index<T, K, ?> index : indexManager.getIndexes()) {
                if (index.isUnique()) {
                    Object value = index.getValueExtractor().apply(entity);
                    List<K> existingKeys = findByIndexedValueInternal(index, value);

                    if (!existingKeys.isEmpty()) {
                        throw new DatabaseException("Entity violates unique constraint for index '"
                                + index.getName() + "'");
                    }
                }
            }
        }

        // Save the entity
        super.save(entity);

        // Add the entity to all indexes
        indexManager.addEntityToIndexes(entity);

        // Save the indexes
        indexManager.saveIndexes();
    }

    @Override
    public void update(T entity) {
        // Get the existing entity
        T existingEntity = findById(entity.getId());
        if (existingEntity == null) {
            throw new DatabaseException("Entity with ID " + entity.getId() + " does not exist");
        }

        // Remove the existing entity from all indexes
        indexManager.removeEntityFromIndexes(existingEntity);

        // Check if the updated entity violates any unique constraints
        if (!indexManager.getIndexes().isEmpty()) {
            for (Index<T, K, ?> index : indexManager.getIndexes()) {
                if (index.isUnique()) {
                    Object value = index.getValueExtractor().apply(entity);
                    List<K> existingKeys = findByIndexedValueInternal(index, value);

                    if (!existingKeys.isEmpty() && !existingKeys.get(0).equals(entity.getId())) {
                        // Re-add the existing entity to all indexes
                        indexManager.addEntityToIndexes(existingEntity);

                        throw new DatabaseException("Updated entity violates unique constraint for index '"
                                + index.getName() + "'");
                    }
                }
            }
        }

        // Update the entity
        super.update(entity);

        // Add the updated entity to all indexes
        indexManager.addEntityToIndexes(entity);

        // Save the indexes
        indexManager.saveIndexes();
    }

    @Override
    public void delete(K id) {
        // Get the entity
        T entity = findById(id);
        if (entity == null) {
            throw new DatabaseException("Entity with ID " + id + " does not exist");
        }

        // Remove the entity from all indexes
        indexManager.removeEntityFromIndexes(entity);

        // Delete the entity
        super.delete(id);

        // Save the indexes
        indexManager.saveIndexes();
    }

    /**
     * Find primary keys by indexed value using a specific index
     *
     * @param <V> The indexed value type
     * @param index The index to use
     * @param value The value to search for
     * @return List of primary keys matching the value
     */
    @SuppressWarnings("unchecked")
    private <V> List<K> findByIndexedValueInternal(Index<T, K, ?> index, Object value) {
        return ((Index<T, K, V>) index).findByValue((V) value);
    }

    /**
     * Load all indexes from files
     */
    public void loadIndexes() {
        List<T> entities = findAll();
        indexManager.loadIndexes(entities);
    }

    /**
     * Get the index manager
     *
     * @return The index manager
     */
    public IndexManager<T, K> getIndexManager() {
        return indexManager;
    }
}
