package cz.upce.boop.ex.db.index.entry;

import cz.upce.boop.ex.db.core.PrimaryKey;

import java.io.Serializable;

/**
 * Base abstract class for index entries.
 * @param <V> The type of the indexed value
 * @param <K> The type of the primary key
 */
public abstract class IndexEntry<V, K extends PrimaryKey<?>> implements Serializable {
    private final V value;
    
    /**
     * Creates an index entry with the specified value
     * @param value The indexed value
     */
    protected IndexEntry(V value) {
        this.value = value;
    }
    
    /**
     * Gets the indexed value
     * @return The indexed value
     */
    public V getValue() {
        return value;
    }
    
    /**
     * Checks if this is a unique index entry
     * @return true if this is a unique index entry, false otherwise
     */
    public abstract boolean isUnique();
}