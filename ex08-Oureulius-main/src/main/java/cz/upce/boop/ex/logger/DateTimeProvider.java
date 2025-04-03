package cz.upce.boop.ex.logger;

import java.time.LocalDateTime;

@FunctionalInterface
public interface DateTimeProvider {

    public LocalDateTime getDateTime();
}
