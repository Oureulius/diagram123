package cz.upce.boop.ex.logger;

@FunctionalInterface
public interface Logger {

    public void logMessage(LogMessageSeverity severity, String message);
    
}
