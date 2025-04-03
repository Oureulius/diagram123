package cz.upce.boop.ex;

import cz.upce.boop.ex.db.core.DatabaseEntity;
import cz.upce.boop.ex.db.core.StringPrimaryKey;

/**
 * Example User entity for demonstrating indexing.
 */
public class User implements DatabaseEntity<StringPrimaryKey> {

    private StringPrimaryKey id;
    private String username;
    private String firstName;
    private String lastName;
    private String country;
    private int age;

    public User() {
    }

    public User(String id, String username, String firstName, String lastName, String country, int age) {
        this.id = new StringPrimaryKey(id);
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.country = country;
        this.age = age;
    }

    @Override
    public StringPrimaryKey getId() {
        return id;
    }

    @Override
    public void setId(StringPrimaryKey id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", country='" + country + '\'' +
                ", age=" + age +
                '}';
    }
}