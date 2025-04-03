package cz.upce.boop.ex.logger;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MemoryLoggerWithHandlers implements Logger {

    private record HandlerSeverityPair(LogMessageSeverity severity, LogMessageHandler handler) {

    }

    private final List<LogMessage> loggedMessages;
    private final List<HandlerSeverityPair> handlers;
    private final DateTimeProvider dateTimeProvider;

    public MemoryLoggerWithHandlers(DateTimeProvider dateTimeProvider) {
        if (dateTimeProvider == null) {
            throw new IllegalArgumentException("dateTimeProvider is null");
        }

        loggedMessages = new ArrayList<>();
        handlers = new ArrayList<>();

        this.dateTimeProvider = dateTimeProvider;
    }

    @Override
    public void logMessage(LogMessageSeverity severity, String message) {
        if (message == null) {
            throw new IllegalArgumentException("message is null");
        }

        // construct log message
        LocalDateTime datetime = dateTimeProvider.getDateTime();
        LogMessage log = new LogMessage(datetime, severity, message);

        // add to memory storage
        loggedMessages.add(log);

        // call all relevant handlers
        for (HandlerSeverityPair pair : handlers) {
            if (pair.severity.isLessOrEqualSeverityThan(severity)) {
                pair.handler.handle(log);
            }
        }
    }

    public void addLogMessageHandler(LogMessageSeverity minimalSeverity, LogMessageHandler handler) {
        if (handler == null) {
            throw new IllegalArgumentException("handler is null");
        }

        handlers.add(new HandlerSeverityPair(minimalSeverity, handler));
    }

    public List<LogMessage> getLoggedMessages() {
        return loggedMessages;
    }

    public LogMessage getLastLogMessage() {
        return loggedMessages.get(loggedMessages.size() - 1);
    }
}
