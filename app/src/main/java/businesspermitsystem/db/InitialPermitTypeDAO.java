package businesspermitsystem.db;

import businesspermitsystem.models.InitialPermitTypeModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InitialPermitTypeDAO {

    private Connection connection;

    public InitialPermitTypeDAO() {
        this.connection = DatabaseConnector.connection;
    }

    public List<InitialPermitTypeModel> getAllPermitTypes() {
        List<InitialPermitTypeModel> permitTypes = new ArrayList<>();

        String query = """
        SELECT pt.permit_type_id,
               pt.permit_name,
               fs.base_fee,
               fs.surcharge_rule,
               fs.validity_months,
               pt.document_requirements,
               pt.fee_schedule_id
        FROM permit_type pt
        JOIN fee_schedule fs ON pt.fee_schedule_id = fs.fee_schedule_id
        """;

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                permitTypes.add(new InitialPermitTypeModel(
                        rs.getInt("permit_type_id"),
                        rs.getString("permit_name"),
                        rs.getBigDecimal("base_fee"),
                        rs.getString("surcharge_rule"),
                        rs.getInt("validity_months"),
                        rs.getString("document_requirements"),
                        rs.getInt("fee_schedule_id")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return permitTypes;
    }

    public InitialPermitTypeModel getPermitTypeByID(int permitTypeID) {
        String query = """
        SELECT pt.permit_type_id,
               pt.permit_name,
               fs.base_fee,
               fs.surcharge_rule,
               fs.validity_months,
               pt.document_requirements,
               pt.fee_schedule_id
        FROM permit_type pt
        JOIN fee_schedule fs ON pt.fee_schedule_id = fs.fee_schedule_id
        WHERE pt.permit_type_id = ?
        """;

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, permitTypeID);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new InitialPermitTypeModel(
                            rs.getInt("permit_type_id"),
                            rs.getString("permit_name"),
                            rs.getBigDecimal("base_fee"),
                            rs.getString("surcharge_rule"),
                            rs.getInt("validity_months"),
                            rs.getString("document_requirements"),
                            rs.getInt("fee_schedule_id")
                    );
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean addPermitType(InitialPermitTypeModel permitType) {
        String queryFS = "INSERT INTO fee_schedule (base_fee, surcharge_rule, validity_months, document_requirements) VALUES (?, ?, ?, ?)";
        String queryPT = "INSERT INTO permit_type (permit_name, fee_schedule_id, document_requirements, validity_months) VALUES (?, ?, ?, ?)";

        try (PreparedStatement psFS = connection.prepareStatement(queryFS, Statement.RETURN_GENERATED_KEYS)) {

            psFS.setBigDecimal(1, permitType.getBaseFee());
            psFS.setString(2, permitType.getSurchargeRule());
            psFS.setInt(3, permitType.getValidityMonths());
            psFS.setString(4, permitType.getDocumentRequirements());
            psFS.executeUpdate();

            ResultSet keys = psFS.getGeneratedKeys();
            if (keys.next()) {
                int feeScheduleId = keys.getInt(1);

                try (PreparedStatement psPT = connection.prepareStatement(queryPT)) {
                    psPT.setString(1, permitType.getPermitName());
                    psPT.setInt(2, feeScheduleId);
                    psPT.setString(3, permitType.getDocumentRequirements());
                    psPT.setInt(4, permitType.getValidityMonths());
                    return psPT.executeUpdate() > 0;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean updatePermitType(InitialPermitTypeModel permitType) {
        String queryFS = "UPDATE fee_schedule SET base_fee = ?, surcharge_rule = ?, validity_months = ?, document_requirements = ? WHERE fee_schedule_id = ?";
        String queryPT = "UPDATE permit_type SET permit_name = ?, document_requirements = ?, validity_months = ? WHERE permit_type_id = ?";

        try (PreparedStatement psFS = connection.prepareStatement(queryFS)) {

            psFS.setBigDecimal(1, permitType.getBaseFee());
            psFS.setString(2, permitType.getSurchargeRule());
            psFS.setInt(3, permitType.getValidityMonths());
            psFS.setString(4, permitType.getDocumentRequirements());
            psFS.setInt(5, permitType.getFeeScheduleId());
            psFS.executeUpdate();

            try (PreparedStatement psPT = connection.prepareStatement(queryPT)) {
                psPT.setString(1, permitType.getPermitName());
                psPT.setString(2, permitType.getDocumentRequirements());
                psPT.setInt(3, permitType.getValidityMonths());
                psPT.setInt(4, permitType.getPermitTypeId());
                return psPT.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean deletePermitType(int permitTypeID, int feeScheduleId) {
        String queryPT = "DELETE FROM permit_type WHERE permit_type_id = ?";
        String queryFS = "DELETE FROM fee_schedule WHERE fee_schedule_id = ?";

        try (PreparedStatement psPT = connection.prepareStatement(queryPT)) {
            psPT.setInt(1, permitTypeID);
            psPT.executeUpdate();

            try (PreparedStatement psFS = connection.prepareStatement(queryFS)) {
                psFS.setInt(1, feeScheduleId);
                return psFS.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}
