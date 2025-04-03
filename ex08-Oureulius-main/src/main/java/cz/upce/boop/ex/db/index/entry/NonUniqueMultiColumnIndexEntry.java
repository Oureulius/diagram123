package cz.upce.boop.ex.db.index.entry;

import cz.upce.boop.ex.db.core.PrimaryKey;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a non-unique multi-column index entry with multiple primary keys.
 * @param <K> The type of the primary key
 */
public class NonUniqueMultiColumnIndexEntry<K extends PrimaryKey<?>> extends MultiColumnIndexEntry<K> {
    private final ArrayList<K> primaryKeys;
    
    /**
     * Creates a non-unique multi-column index entry with a list of primary keys
     * @param values The list of indexed values
     * @param primaryKeys The list of primary keys
     */
    public NonUniqueMultiColumnIndexEntry(List<?> values, ArrayList<K> primaryKeys) {
        super(values);
        this.primaryKeys = primaryKeys;
    }
    
    /**
     * Gets the list of primary keys for this non-unique index entry
     * @return The list of primary keys
     */
    public ArrayList<K> getPrimaryKeys() {
        return primaryKeys;
    }
    
    /**
     * Adds a primary key to this non-unique index entry
     * @param primaryKey The primary key to add
     */
    public void addPrimaryKey(K primaryKey) {
        if (!primaryKeys.contains(primaryKey)) {
            primaryKeys.add(primaryKey);
        }
    }
    
    /**
     * Removes a primary key from this non-unique index entry
     * @param primaryKey The primary key to remove
     * @return true if the primary key was removed, false otherwise
     */
    public boolean removePrimaryKey(K primaryKey) {
        return primaryKeys.remove(primaryKey);
    }
    
    /**
     * Checks if this non-unique index entry has no primary keys
     * @return true if there are no primary keys, false otherwise
     */
    public boolean isEmpty() {
        return primaryKeys.isEmpty();
    }
    
    @Override
    public boolean isUnique() {
        return false;
    }
}