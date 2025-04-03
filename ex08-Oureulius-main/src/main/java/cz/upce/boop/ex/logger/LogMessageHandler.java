package cz.upce.boop.ex.logger;

@FunctionalInterface
public interface LogMessageHandler {
    public void handle(LogMessage logMessage);
}
