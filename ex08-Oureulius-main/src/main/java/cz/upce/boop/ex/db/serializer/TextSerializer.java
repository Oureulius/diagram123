package cz.upce.boop.ex.db.serializer;

import cz.upce.boop.ex.db.core.DatabaseEntity;
import cz.upce.boop.ex.db.core.EntitySerializer;

public abstract class TextSerializer<T extends DatabaseEntity<?>> implements EntitySerializer<T> {

    /**
     * Convert an entity to a SerializedEntity
     *
     * @param entity The entity to convert
     * @return The SerializedEntity representation
     */
    protected abstract SerializedEntity objectToSerializedEntity(T entity);

    /**
     * Convert a SerializedEntity to an entity
     *
     * @param serialized The SerializedEntity to convert
     * @return The entity
     */
    protected abstract T serializedEntityToObject(SerializedEntity serialized);

    @Override
    public String serialize(T entity) {
        SerializedEntity serialized = objectToSerializedEntity(entity);

        return serialized.toFileContent();
    }

    @Override
    public T deserialize(String data) {
        SerializedEntity serialized = SerializedEntity.fromFileContent(data);

        return serializedEntityToObject(serialized);
    }
}
