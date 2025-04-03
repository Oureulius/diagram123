package cz.upce.boop.ex.db.core;

import java.util.List;
import java.util.function.Predicate;

public interface EntityRepository<T extends DatabaseEntity<K>, K extends PrimaryKey<?>> {

    /**
     * Find an entity by its primary key
     *
     * @param id The primary key of the entity to find
     * @return The entity or null if not found
     * @throws DatabaseException if an error occurs during the operation
     */
    T findById(K id);

    default boolean exists(K id) {
        return findById(id) != null;
    }

    /**
     * Find all entities in the repository
     *
     * @return A list of all entities
     * @throws DatabaseException if an error occurs during the operation
     */
    List<T> findAll();

    /**
     * Save a new entity to the repository
     *
     * @param entity The entity to save
     * @throws DatabaseException if an error occurs during the operation or if
     * the entity already exists
     */
    void save(T entity);

    /**
     * Update an existing entity in the repository
     *
     * @param entity The entity to update
     * @throws DatabaseException if an error occurs during the operation or if
     * the entity doesn't exist
     */
    void update(T entity);

    default void saveOrUpdate(T entity) {
        if (exists(entity.getId())) {
            update(entity);
        } else {
            save(entity);
        }
    }

    /**
     * Delete an entity from the repository
     *
     * @param id The primary key of the entity to delete
     * @throws DatabaseException if an error occurs during the operation or if
     * the entity doesn't exist
     */
    void delete(K id);

    /**
     * Find entities that match the given condition
     *
     * @param condition The condition to match
     * @return A list of matching entities
     * @throws DatabaseException if an error occurs during the operation
     */
    List<T> findByCondition(Predicate<T> condition);
}
