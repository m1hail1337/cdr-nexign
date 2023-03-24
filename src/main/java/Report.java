import java.util.ArrayList;
import java.util.List;

public class Report {
    private final String tariff;
    private int minutesCounter;     // Every minute in Call round to high value (ex: 3m 6sec -> 4m)
    private final String number;
    private final List<Call> calls = new ArrayList<>();

    public Report(String number, String tariff) {
        this.minutesCounter = 0;
        this.number = number;
        this.tariff = tariff;
    }

    public String getNumber() {
        return number;
    }
    public List<Call> getCalls() {
        return calls;
    }

    public int getMinutesCounter() {
        return minutesCounter;
    }

    public String getTariff() {
        return tariff;
    }
    public void setMinutesCounter(int minutes) {
        this.minutesCounter = minutes;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        Report other = (Report) obj;
        return this.number.equals(other.getNumber());
    }

    @Override
    public int hashCode() {
        final long number4hash = Long.parseLong(this.number);
        return (int) (number4hash ^ (number4hash >>> 32));
    }
}
