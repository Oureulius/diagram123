package cz.upce.boop.ex.db.core;

public interface EntitySerializer<T extends DatabaseEntity<?>> {
    /**
     * Serialize an entity to a string
     * @param entity The entity to serialize
     * @return The serialized entity as a string
     * @throws DatabaseException if an error occurs during serialization
     */
    String serialize(T entity);
    
    /**
     * Deserialize a string to an entity
     * @param data The string to deserialize
     * @return The deserialized entity
     * @throws DatabaseException if an error occurs during deserialization
     */
    T deserialize(String data);
}