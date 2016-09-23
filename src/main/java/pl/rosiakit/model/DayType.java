package pl.rosiakit.model;

/**
 * @author Arkadiusz Rosiak (http://www.rosiak.it)
 * @date 2016-09-23
 */
public enum DayType {
    WEEKDAY(8),
    SATURDAY(6),
    HOLIDAY(7);

    private int value;

    DayType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
