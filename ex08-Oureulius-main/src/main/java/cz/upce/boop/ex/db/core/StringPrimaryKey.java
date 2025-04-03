package cz.upce.boop.ex.db.core;

public class StringPrimaryKey implements PrimaryKey<String> {

    private final String value;

    public StringPrimaryKey(String value) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("Primary key value cannot be null or empty");
        }
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String toFileName() {
        // Nahrazení neplatných znaků pro název souboru
        return value.replaceAll("[\\\\/:*?\"<>|]", "_") + ".txt";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StringPrimaryKey that = (StringPrimaryKey) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }
}
