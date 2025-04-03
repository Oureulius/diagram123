package cz.upce.boop.ex.db.index.entry;

import cz.upce.boop.ex.db.core.PrimaryKey;

/**
 * Represents a unique index entry with a single primary key.
 * @param <V> The type of the indexed value
 * @param <K> The type of the primary key
 */
public class UniqueIndexEntry<V, K extends PrimaryKey<?>> extends IndexEntry<V, K> {
    private final K primaryKey;
    
    /**
     * Creates a unique index entry with a single primary key
     * @param value The indexed value
     * @param primaryKey The primary key
     */
    public UniqueIndexEntry(V value, K primaryKey) {
        super(value);
        this.primaryKey = primaryKey;
    }
    
    /**
     * Gets the primary key for this unique index entry
     * @return The primary key
     */
    public K getPrimaryKey() {
        return primaryKey;
    }
    
    @Override
    public boolean isUnique() {
        return true;
    }
}