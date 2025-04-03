package cz.upce.boop.ex.logger;

public enum LogMessageSeverity {
    Debug(0),
    Information(1),
    Warning(2),
    Error(3);

    private final int level;

    private LogMessageSeverity(int level) {
        this.level = level;
    }

    public boolean isLessOrEqualSeverityThan(LogMessageSeverity other) {
        return level <= other.level;
    }

}
