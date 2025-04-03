package cz.upce.boop.ex;

import cz.upce.boop.ex.db.core.StringPrimaryKey;
import cz.upce.boop.ex.db.index.IndexedFileEntityRepository;
import java.io.File;
import java.util.List;

public class Ex08 {

    public static void main(String[] args) {

        System.out.println("""
--------------------------------------------------------------------------------
               !! Popis zadání se nachází v souboru README.md !!
--------------------------------------------------------------------------------
        """);

        // Create a repository for User entities
        IndexedFileEntityRepository<User, StringPrimaryKey> repository = new IndexedFileEntityRepository<>("db" + File.separator + "users", new UserSerializer());
        repository.createUniqueIndex("unique_username", User::getUsername);
        repository.createNonUniqueIndex("country", User::getCountry);

        // Clear any existing data
        clearDatabase(repository);

        // Create some users
        User user1 = new User("1", "john_doe", "John", "Doe", "USA", 30);
        User user2 = new User("2", "jane_smith", "Jane", "Smith", "Canada", 28);
        User user3 = new User("3", "bob_johnson", "Bob", "Johnson", "USA", 35);
        User user4 = new User("4", "alice_brown", "Alice", "Brown", "UK", 25);
        User user5 = new User("5", "charlie_davis", "Charlie", "Davis", "Canada", 28);

        // Save the users
        repository.save(user1);
        repository.save(user2);
        repository.save(user3);
        repository.save(user4);
        repository.save(user5);

        System.out.println("findByIndexedValue - Canada");
        var foundUsers = repository.findByIndexedValue("country", "Canada");
        for (User user : foundUsers) {
            System.out.println(user);
        }
        System.out.println();

        System.out.println("delete - 4");
        repository.delete(new StringPrimaryKey("4")); // alice_brown
        System.out.println();

        System.out.println("update - 5 - country Czech Republic");
        user5.setCountry("Czech Republic");
        repository.update(user5);
        System.out.println();

        System.out.println("findByIndexedValue - Canada");
        foundUsers = repository.findByIndexedValue("country", "Canada");
        for (User user : foundUsers) {
            System.out.println(user);
        }
        System.out.println();

        System.out.println("findByIndexedValue - Czech Republic");
        foundUsers = repository.findByIndexedValue("country", "Czech Republic");
        for (User user : foundUsers) {
            System.out.println(user);
        }
        System.out.println();
        
        System.out.println("findAll");
        foundUsers = repository.findAll();
        for (User user : foundUsers) {
            System.out.println(user);
        }
        System.out.println();
    }

    private static void clearDatabase(IndexedFileEntityRepository<User, StringPrimaryKey> repository) {
        List<User> allUsers = repository.findAll();
        for (User user : allUsers) {
            repository.delete(user.getId());
        }
    }

}
