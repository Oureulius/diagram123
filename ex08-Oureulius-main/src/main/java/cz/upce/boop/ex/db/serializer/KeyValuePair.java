package cz.upce.boop.ex.db.serializer;

/**
 * Record representing a key-value pair for serialization purposes.
 * @param key The key of the pair
 * @param value The value of the pair
 */
public record KeyValuePair(String key, String value) {
    
    @Override
    public String toString() {
        return key + "=" + value;
    }
    
    /**
     * Creates a KeyValuePair from a string in the format "key=value"
     * @param line The string to parse
     * @return A new KeyValuePair or null if the format is invalid
     */
    public static KeyValuePair fromString(String line) {
        if (line == null || line.isEmpty()) {
            return null;
        }
        
        int equalsPos = line.indexOf('=');
        if (equalsPos > 0) {
            String key = line.substring(0, equalsPos);
            String value = line.substring(equalsPos + 1);
            return new KeyValuePair(key, value);
        }
        
        return null;
    }
}