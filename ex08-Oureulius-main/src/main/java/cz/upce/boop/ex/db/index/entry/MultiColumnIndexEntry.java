package cz.upce.boop.ex.db.index.entry;

import cz.upce.boop.ex.db.core.PrimaryKey;

import java.util.List;

/**
 * Base class for multi-column index entries.
 * @param <K> The type of the primary key
 */
public abstract class MultiColumnIndexEntry<K extends PrimaryKey<?>> extends IndexEntry<List<?>, K> {
    
    /**
     * Creates a multi-column index entry with the specified values
     * @param values The list of indexed values
     */
    protected MultiColumnIndexEntry(List<?> values) {
        super(values);
    }
    
    /**
     * Gets the list of indexed values
     * @return The list of indexed values
     */
    public List<?> getValues() {
        return getValue();
    }
}