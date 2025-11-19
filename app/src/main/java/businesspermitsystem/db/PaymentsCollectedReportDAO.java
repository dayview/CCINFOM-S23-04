package businesspermitsystem.db;

import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class PaymentsCollectedReportDAO {

    private final Connection conn;

    public PaymentsCollectedReportDAO() {
        this.conn = DatabaseConnector.connection;
        if (this.conn == null) {
            System.err.println("ERROR: Database connection not established.");
        }
    }

    public String generatePaymentsCollectedReport(int year) {
        StringBuilder sb = new StringBuilder();
        sb.append("==========================================================\n");
        sb.append("PAYMENTS COLLECTED REPORT - YEAR ").append(year).append("\n");
        sb.append("==========================================================\n\n");

        double totalAmount = getTotalAmount(year);
        double averagePayment = getAverageAmount(year);

        sb.append("TOTAL PAYMENTS COLLECTED: ").append(String.format("%.2f", totalAmount)).append("\n");
        sb.append("AVERAGE PAYMENT AMOUNT: ").append(String.format("%.2f", averagePayment)).append("\n\n");

        sb.append("> PAYMENTS BY PERMIT TYPE:\n");
        for (var entry : getPaymentsByPermitType(year).entrySet()) {
            sb.append("  - ").append(entry.getKey()).append(": ").append(String.format("%.2f", entry.getValue())).append("\n");
        }

        sb.append("\n> PAYMENTS BY MUNICIPALITY:\n");
        for (var entry : getPaymentsByMunicipality(year).entrySet()) {
            sb.append("  - ").append(entry.getKey()).append(": ").append(String.format("%.2f", entry.getValue())).append("\n");
        }

        sb.append("\n==========================================================\n");
        sb.append("END OF REPORT\n");

        return sb.toString();
    }

    private double getTotalAmount(int year) {
        try (PreparedStatement ps = conn.prepareStatement("SELECT SUM(amount_paid) FROM payment WHERE YEAR(payment_date)=?")) {
            ps.setInt(1, year);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException ignored) {}
        return 0;
    }

    private double getAverageAmount(int year) {
        try (PreparedStatement ps = conn.prepareStatement("SELECT AVG(amount_paid) FROM payment WHERE YEAR(payment_date)=?")) {
            ps.setInt(1, year);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException ignored) {}
        return 0;
    }

    private Map<String, Double> getPaymentsByPermitType(int year) {
        Map<String, Double> map = new LinkedHashMap<>();
        String sql = """
            SELECT pt.permit_name, SUM(p.amount_paid)
            FROM payment p
            JOIN permit_type pt ON p.permit_type_id = pt.permit_type_id
            WHERE YEAR(p.payment_date)=?
            GROUP BY pt.permit_name
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, year);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) map.put(rs.getString(1), rs.getDouble(2));
        } catch (SQLException ignored) {}
        return map;
    }

    private Map<String, Double> getPaymentsByMunicipality(int year) {
        Map<String, Double> map = new LinkedHashMap<>();
        String sql = """
            SELECT m.municipality_name, SUM(p.amount_paid)
            FROM payment p
            JOIN municipality m ON p.municipality_id = m.municipality_id
            WHERE YEAR(p.payment_date)=?
            GROUP BY m.municipality_name
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, year);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) map.put(rs.getString(1), rs.getDouble(2));
        } catch (SQLException ignored) {}
        return map;
    }
}
