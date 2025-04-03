package cz.upce.boop.ex.logger;

public class EmptyLogger implements Logger{

    @Override
    public void logMessage(LogMessageSeverity severity, String message) {
    }
    
}
