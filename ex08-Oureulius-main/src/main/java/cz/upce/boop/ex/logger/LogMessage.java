package cz.upce.boop.ex.logger;

import java.time.LocalDateTime;

public record LogMessage(
        LocalDateTime occurence,
        LogMessageSeverity severity,
        String message) {

}
