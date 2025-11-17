package businesspermitsystem.services;

import businesspermitsystem.models.*;
import businesspermitsystem.db.*;

public class PermitRenewalService {
    private PermitDAO permitDAO;
    private BusinessDAO businessDAO;
    private PermitRenewalApplicationDAO renewalDAO;
    private PaymentDAO paymentDAO;
    private InspectionDAO inspectionDAO;

    public PermitRenewalService(PermitDAO permitDAO, BusinessDAO businessDAO, 
            PermitRenewalApplicationDAO renewalDAO, PaymentDAO paymentDAO, InspectionDAO inspectionDAO) {
                this.permitDAO = new PermitDAO();
                this.businessDAO = new BusinessDAO();
                this.renewalDAO = new PermitRenewalApplicationDAO();
                this.paymentDAO = new PaymentDAO();
                this.inspectionDAO = new InspectionDAO();
    }


}
