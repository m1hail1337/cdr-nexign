import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Call {
    private final String type;
    private final String startTime;
    private final String endTime;
    private final int callTime;
    private final double cost;

    public Call(String type, String startTime, String endTime, String tariff, int minutesCounter) throws ParseException {
        this.type = type;
        this.startTime = startTime;
        this.endTime = endTime;
        this.callTime = countCallTime(startTime, endTime);
        this.cost = countCallCost(tariff, minutesCounter);
    }

    public int getCallTime() {
        return callTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public double getCost() {
        return cost;
    }

    public String getType() {
        return type;
    }

    private int countCallTime(String startTime, String endTime) throws ParseException {
        DateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        Date startDate = format.parse(startTime);
        Date endDate = format.parse(endTime);
        return (int) Math.ceil((endDate.getTime() - startDate.getTime()) / 1000.0 / 60.0);  // minutes
    }

    // All values rounds to high direction (ex: 3.3 -> 4)
    private double countCallCost(String tariff, int minutesCounter) {
        switch (tariff) {
            case "06" -> {
                if (this.callTime + minutesCounter <= 300) {
                    return 0.0;
                } else if (minutesCounter < 300) {
                    return this.callTime - (300 - minutesCounter);
                } else {
                    return this.callTime;
                }
            }
            case "03" -> {
                return this.callTime * 1.5;
            }
            case "11" -> {
                if (this.type.equals("02")) {
                    return 0.0;
                } else if (this.callTime + minutesCounter <= 100) {
                    return this.callTime * 0.5;
                } else if (minutesCounter >= 100) {
                    return this.callTime * 1.5;
                } else {
                    return (100 - minutesCounter) * 0.5 + (this.callTime - (100 - minutesCounter)) * 1.5;
                }
            }
            default -> throw new RuntimeException("Unexpected tariff.");
        }
    }


}
