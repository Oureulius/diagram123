package cz.upce.boop.ex.logger;

import java.time.LocalDateTime;

public class LocalNowDateTimeProvider implements DateTimeProvider {

    @Override
    public LocalDateTime getDateTime() {
        return LocalDateTime.now();
    }

}
