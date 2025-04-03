package cz.upce.boop.ex.db.serializer;

import cz.upce.boop.ex.db.core.DatabaseException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * Record representing a serialized entity as a collection of key-value pairs.
 *
 * @param pairs The list of key-value pairs that make up the entity
 */
public record SerializedEntity(List<KeyValuePair> pairs) {

    /**
     * Creates a new SerializedEntity with the given pairs
     *
     * @param pairs The list of key-value pairs
     */
    public SerializedEntity {
        pairs = Collections.unmodifiableList(new ArrayList<>(pairs));
    }

    /**
     * Creates an empty SerializedEntity
     *
     * @return A new SerializedEntity with no pairs
     */
    public static SerializedEntity empty() {
        return new SerializedEntity(Collections.emptyList());
    }

    /**
     * Creates a new SerializedEntity builder
     *
     * @return A new builder
     */
    public static Builder builder() {
        return new Builder();
    }

    public SerializedEntity extractValue(String key, Consumer<String> consumer) {
        consumer.accept(getValue(key));
        return this;
    }

    public SerializedEntity extractIntValue(String key, Consumer<Integer> consumer) {
        consumer.accept(getIntValue(key));
        return this;
    }

    public SerializedEntity extractOptionalValue(String key, Consumer<String> consumer) {
        if (containsKey(key)) {
            consumer.accept(getValue(key));
        }
        return this;
    }

    public SerializedEntity extractOptionalIntValue(String key, Consumer<Integer> consumer) {
        if (containsKey(key)) {
            consumer.accept(getIntValue(key));
        }
        return this;
    }

    /**
     * Finds a value by key in the serialized entity
     *
     * @param key The key to search for
     * @return The value associated with the key, or null if not found
     */
    public String getValue(String key) {
        for (KeyValuePair pair : pairs) {
            if (pair.key().equals(key)) {
                return pair.value();
            }
        }
        return null;
    }

    /**
     * Checks if the serialized entity contains a key
     *
     * @param key The key to check for
     * @return True if the key exists, false otherwise
     */
    public boolean containsKey(String key) {
        return getValue(key) != null;
    }

    /**
     * Gets an integer value from the serialized entity
     *
     * @param key The key to search for
     * @return The integer value associated with the key
     * @throws DatabaseException if the value is not a valid integer
     */
    public int getIntValue(String key) {
        String value = getValue(key);
        if (value == null) {
            throw new DatabaseException("Key not found: " + key);
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new DatabaseException("Invalid integer format for key: " + key, e);
        }
    }

    /**
     * Converts the serialized entity to a string representation
     *
     * @return The string representation of the entity
     */
    public String toFileContent() {
        StringBuilder sb = new StringBuilder();
        for (KeyValuePair pair : pairs) {
            sb.append(pair.toString()).append("\n");
        }
        return sb.toString();
    }

    /**
     * Creates a SerializedEntity from a string representation
     *
     * @param content The string representation of the entity
     * @return The deserialized entity
     */
    public static SerializedEntity fromFileContent(String content) {
        List<KeyValuePair> pairs = new ArrayList<>();
        String[] lines = content.split("\n");

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }

            KeyValuePair pair = KeyValuePair.fromString(line);
            if (pair != null) {
                pairs.add(pair);
            }
        }

        return new SerializedEntity(pairs);
    }

    /**
     * Builder class for SerializedEntity
     */
    public static class Builder {

        private final List<KeyValuePair> pairs = new ArrayList<>();

        /**
         * Adds a key-value pair to the builder
         *
         * @param key The key
         * @param value The value
         * @return The builder
         */
        public Builder add(String key, String value) {
            pairs.add(new KeyValuePair(key, value));
            return this;
        }

        /**
         * Adds a key-value pair to the builder with an integer value
         *
         * @param key The key
         * @param value The integer value
         * @return The builder
         */
        public Builder add(String key, int value) {
            return add(key, String.valueOf(value));
        }

        public Builder add(String key, Object value) {
            return add(key, value.toString());
        }

        /**
         * Builds the SerializedEntity
         *
         * @return The built SerializedEntity
         */
        public SerializedEntity build() {
            return new SerializedEntity(pairs);
        }
    }
}
