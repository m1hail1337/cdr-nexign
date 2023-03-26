import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


public class Application {
    private static final String
            BORDER = "----------------------------------------------------------------------------\n";
    private static final String
            ATTRIBUTES = "| Call Type |   Start Time        |     End Time        | Duration | Cost  |\n";

    public static void main(String[] args) {
        File cdr = new File("src/main/resources/cdr.txt");
        try {
            Scanner scanner = new Scanner(cdr);
            Set<Report> reports = new HashSet<>();
            while (scanner.hasNext()) {
                String[] record = scanner.nextLine().split(", ");
                String type = record[0];
                String number = record[1];
                String startTime = record[2];
                String endTime = record[3];
                String tariff = record[4];
                Report report = new Report(number, tariff);
                if (!reports.contains(new Report(number, tariff))) {
                    reports.add(report);
                }
                for (Report rep : reports) {
                    if (rep.getNumber().equals(number)) {
                        report = rep;
                        }
                    }
                Call call = new Call(type, startTime, endTime, tariff, report.getMinutesCounter());
                report.getCalls().add(call);
                report.setMinutesCounter(report.getMinutesCounter() + call.getCallTime());
            }
            scanner.close();

            for (Report report : reports) {
                createReportFile(report);
            }

            System.out.println("Reports generated!");

        } catch (IOException e) {
            throw new RuntimeException("File with data not found or problems with generating reports.");
        } catch (ParseException e) {
            throw new RuntimeException("Illegal Data format of Call.");
        }
    }

    private static void createReportFile(Report report) throws IOException, ParseException {
        FileOutputStream file = new FileOutputStream(String.format("./reports/%s.txt", report.getNumber()));
        file.write(String.format("Tariff index: %s\n", report.getTariff()).getBytes());
        file.write(BORDER.getBytes());
        file.write(String.format("Report for phone number %s:\n", report.getNumber()).getBytes());
        file.write(BORDER.getBytes());
        file.write(ATTRIBUTES.getBytes());
        file.write(BORDER.getBytes());
        List<Call> incoming = new ArrayList<>();
        List<Call> outgoing = new ArrayList<>();
        report.getCalls().forEach(call -> {
            if (call.getType().equals("01")) {
                incoming.add(call);
            } else {
                outgoing.add(call);
            }
        });

        incoming.sort(Comparator.comparing(Call::getStartTime));
        outgoing.sort(Comparator.comparing(Call::getStartTime));
        BigDecimal totalCost = report.getTariff().equals("06") ?
                new BigDecimal("100.00") : new BigDecimal("0.00");

        for (Call call : incoming) {
            writeCall(call, file);
            totalCost = totalCost.add(call.getCost());
        }
        for (Call call : outgoing) {
            writeCall(call, file);
            totalCost = totalCost.add(call.getCost());
        }

        file.write(BORDER.getBytes());
        String totalCostString = String.format("%.2f rubles", totalCost).replace(",", ".");
        file.write(String.format(
                "|                                           Total Cost: |%17s |\n", totalCostString).getBytes());
        file.write(BORDER.getBytes());
        file.close();
    }

    private static String buildFormattedDate(String date) {
        return date.substring(0,4) + "-" + date.substring(4,6)  + "-" + date.substring(6,8) + " " +
                date.substring(8,10) + ":" + date.substring(10,12) + ":" + date.substring(12,14);
    }

    private static String buildDuration (String start, String end) throws ParseException {
        DateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        Date startDate = format.parse(start);
        Date endDate = format.parse(end);
        double totalSeconds = Math.ceil((endDate.getTime() - startDate.getTime()) / 1000.0);
        int hours = (int) totalSeconds / 3600;
        int minutes = (int) (totalSeconds % 3600) / 60;
        int seconds = (int) totalSeconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    private static void writeCall (Call call, FileOutputStream file) throws ParseException, IOException {
        String type = call.getType();
        String startDate = buildFormattedDate(call.getStartTime());
        String endDate = buildFormattedDate(call.getEndTime());
        String duration = buildDuration(call.getStartTime(), call.getEndTime());
        String cost = String.format("%.2f", call.getCost()).replace(",", ".");
        file.write((String.format(
                "|%7s    |%20s |%20s |%9s |%6s |\n",
                type, startDate, endDate, duration, cost)).getBytes());
    }
}

