package cz.upce.boop.ex.db.index;

import cz.upce.boop.ex.db.core.DatabaseEntity;
import cz.upce.boop.ex.db.core.DatabaseException;
import cz.upce.boop.ex.db.core.PrimaryKey;

import java.io.*;
import java.util.List;
import java.util.function.Function;

/**
 * Abstract base class for index implementations
 *
 * @param <T> The entity type
 * @param <K> The primary key type
 * @param <V> The indexed value type
 */
public abstract class AbstractIndex<T extends DatabaseEntity<K>, K extends PrimaryKey<?>, V>
        implements Index<T, K, V> {

    protected final String name;
    protected final Function<T, V> valueExtractor;
    protected final boolean unique;

    /**
     * Create a new abstract index
     *
     * @param name The name of the index
     * @param valueExtractor The function to extract the indexed value from an
     * entity
     * @param unique Whether this index enforces uniqueness
     */
    public AbstractIndex(String name, Function<T, V> valueExtractor, boolean unique) {
        this.name = name;
        this.valueExtractor = valueExtractor;
        this.unique = unique;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isUnique() {
        return unique;
    }

    @Override
    public Function<T, V> getValueExtractor() {
        return valueExtractor;
    }

    /**
     * Extract the indexed value from an entity
     *
     * @param entity The entity
     * @return The indexed value
     */
    protected V extractValue(T entity) {
        return valueExtractor.apply(entity);
    }

    @Override
    public void saveToFile(String filePath) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            // Save index metadata
            oos.writeUTF(name);
            oos.writeBoolean(unique);

            // Save index entries (to be implemented by subclasses)
            saveEntries(oos);
        } catch (IOException e) {
            throw new DatabaseException("Error saving index to file: " + filePath, e);
        }
    }

    /**
     * Save index entries to the output stream
     *
     * @param oos The output stream
     * @throws IOException If an I/O error occurs
     */
    protected abstract void saveEntries(ObjectOutputStream oos) throws IOException;

    @Override
    public void loadFromFile(String filePath, List<T> entities) {
        // Clear existing index entries
        clear();

        File file = new File(filePath);
        if (!file.exists()) {
            // If the index file doesn't exist, rebuild the index from entities
            for (T entity : entities) {
                addEntity(entity);
            }
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            // Read index metadata
            String indexName = ois.readUTF();
            boolean indexUnique = ois.readBoolean();

            // Verify index metadata
            if (!indexName.equals(name) || indexUnique != unique) {
                throw new DatabaseException("Index metadata mismatch for index: " + name);
            }

            // Load index entries (to be implemented by subclasses)
            loadEntries(ois, entities);
        } catch (IOException | ClassNotFoundException e) {
            throw new DatabaseException("Error loading index from file: " + filePath, e);
        }
    }

    /**
     * Load index entries from the input stream
     *
     * @param ois The input stream
     * @param entities The list of entities to reference
     * @throws IOException If an I/O error occurs
     * @throws ClassNotFoundException If a class cannot be found
     */
    protected abstract void loadEntries(ObjectInputStream ois, List<T> entities)
            throws IOException, ClassNotFoundException;
}
