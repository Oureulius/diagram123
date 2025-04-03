package cz.upce.boop.ex.db.core;

public interface DatabaseEntity<K extends PrimaryKey<?>> {

    K getId();

    void setId(K id);
}
