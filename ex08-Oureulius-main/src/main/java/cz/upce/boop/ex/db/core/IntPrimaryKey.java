package cz.upce.boop.ex.db.core;

public class IntPrimaryKey implements PrimaryKey<Integer> {

    private final int value;

    public IntPrimaryKey(int value) {
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return value;
    }

    @Override
    public String toFileName() {
        return Integer.toString(value);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + this.value;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final IntPrimaryKey other = (IntPrimaryKey) obj;
        return this.value == other.value;
    }

    @Override
    public String toString() {
        return "IntPrimaryKey{" + "value=" + value + '}';
    }

}
