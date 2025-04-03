package cz.upce.boop.ex.db.core;

/**
 * Runtime exception for database operations.
 * This exception wraps any checked exceptions that might occur during database operations.
 */
public class DatabaseException extends RuntimeException {
    
    public DatabaseException(String message) {
        super(message);
    }
    
    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public DatabaseException(Throwable cause) {
        super(cause);
    }
}