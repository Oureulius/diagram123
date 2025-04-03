package cz.upce.boop.ex;

import cz.upce.boop.ex.db.core.StringPrimaryKey;
import cz.upce.boop.ex.db.serializer.SerializedEntity;
import cz.upce.boop.ex.db.serializer.TextSerializer;

/**
 * Serializer for User entities.
 */
public class UserSerializer extends TextSerializer<User> {

    @Override
    protected SerializedEntity objectToSerializedEntity(User entity) {
        return new SerializedEntity.Builder()
                .add("id", entity.getId())
                .add("firstName", entity.getFirstName())
                .add("lastName", entity.getLastName())
                .add("username", entity.getUsername())
                .add("country", entity.getCountry())
                .add("age", entity.getAge())
                .build();
    }

    @Override
    protected User serializedEntityToObject(SerializedEntity serialized) {
        User user = new User();
        
        serialized
                .extractValue("id", id -> user.setId(new StringPrimaryKey(id)))
                .extractValue("firstName", user::setFirstName)
                .extractValue("lastName", user::setLastName)
                .extractValue("username", user::setUsername)
                .extractValue("country", user::setCountry)
                .extractIntValue("age", user::setAge);
        
        return user;
    }

   
}