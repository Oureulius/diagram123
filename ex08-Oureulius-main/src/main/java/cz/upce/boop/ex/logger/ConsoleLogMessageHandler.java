package cz.upce.boop.ex.logger;

public class ConsoleLogMessageHandler implements LogMessageHandler {

    @Override
    public void handle(LogMessage logMessage) {
        System.out.println(logMessage);
    }
    
}
