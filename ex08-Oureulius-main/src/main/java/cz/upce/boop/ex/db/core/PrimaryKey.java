package cz.upce.boop.ex.db.core;

import java.io.Serializable;

public interface PrimaryKey<T extends Comparable<? super T>> extends Comparable<PrimaryKey<T>>, Serializable {

    T getValue();

    String toFileName();

    @Override
    default int compareTo(PrimaryKey<T> other) {
        return this.getValue().compareTo(other.getValue());
    }
}
