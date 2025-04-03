package cz.upce.boop.ex.db.index.entry;

import cz.upce.boop.ex.db.core.PrimaryKey;

import java.util.List;

/**
 * Represents a unique multi-column index entry with a single primary key.
 * @param <K> The type of the primary key
 */
public class UniqueMultiColumnIndexEntry<K extends PrimaryKey<?>> extends MultiColumnIndexEntry<K> {
    private final K primaryKey;
    
    /**
     * Creates a unique multi-column index entry with a single primary key
     * @param values The list of indexed values
     * @param primaryKey The primary key
     */
    public UniqueMultiColumnIndexEntry(List<?> values, K primaryKey) {
        super(values);
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