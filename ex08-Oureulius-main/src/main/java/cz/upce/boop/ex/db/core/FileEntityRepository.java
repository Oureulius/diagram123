package cz.upce.boop.ex.db.core;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class FileEntityRepository<T extends DatabaseEntity<K>, K extends PrimaryKey<?>>
        implements EntityRepository<T, K> {

    protected final File databaseDir;
    protected final EntitySerializer<T> serializer;

    public FileEntityRepository(String dirPath, EntitySerializer<T> serializer) {
        this.databaseDir = new File(dirPath);
        if (!databaseDir.exists()) {
            if (!databaseDir.mkdirs()) {
                throw new DatabaseException("Failed to create directory: " + dirPath);
            }
        }
        this.serializer = serializer;
    }

    @Override
    public T findById(K id) {
        File file = new File(databaseDir, id.toFileName());
        if (!file.exists()) {
            return null;
        }

        return readFromFile(file);
    }

    @Override
    public List<T> findAll() {
        List<T> result = new ArrayList<>();
        File[] files = databaseDir.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    result.add(readFromFile(file));
                }
            }
        }

        return result;
    }

    @Override
    public void save(T entity) {
        if (entity.getId() == null) {
            throw new IllegalArgumentException("Entity ID cannot be null");
        }

        File file = new File(databaseDir, entity.getId().toFileName());
        if (file.exists()) {
            throw new DatabaseException("Entity with ID " + entity.getId() + " already exists");
        }

        writeToFile(file, entity);
    }

    @Override
    public void update(T entity) {
        if (entity.getId() == null) {
            throw new IllegalArgumentException("Entity ID cannot be null");
        }

        File file = new File(databaseDir, entity.getId().toFileName());
        if (!file.exists()) {
            throw new DatabaseException("Entity with ID " + entity.getId() + " does not exist");
        }

        writeToFile(file, entity);
    }

    @Override
    public void delete(K id) {
        File file = new File(databaseDir, id.toFileName());
        if (!file.exists()) {
            throw new DatabaseException("Entity with ID " + id + " does not exist");
        }

        if (!file.delete()) {
            throw new DatabaseException("Failed to delete entity with ID " + id);
        }
    }

    @Override
    public List<T> findByCondition(Predicate<T> condition) {
        List<T> all = findAll();
        List<T> result = new ArrayList<>();

        for (T entity : all) {
            if (condition.test(entity)) {
                result.add(entity);
            }
        }

        return result;
    }

    protected void writeToFile(File file, T entity) {
        String serialized = serializer.serialize(entity);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(serialized);
        } catch (IOException e) {
            throw new DatabaseException("Error writing entity with ID " + entity.getId(), e);
        }
    }

    private T readFromFile(File file) {
        try {
            StringBuilder content = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
            }

            T entity = serializer.deserialize(content.toString());
            return entity;
        } catch (IOException e) {
            throw new DatabaseException("Error reading file: " + file.getName(), e);
        }
    }
}
