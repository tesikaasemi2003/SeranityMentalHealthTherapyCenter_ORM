package lk.ijse.seranity_mental_health_therapy_center.dao.custom;

import lk.ijse.seranity_mental_health_therapy_center.dao.CrudDAO;
import lk.ijse.seranity_mental_health_therapy_center.entity.Payment;

import java.util.List;

public interface PaymentDAO extends CrudDAO<Payment> {

    // Pending payments ගන්න
    List<Payment> getPendingPayments() throws Exception;

    // Registration ID ගෙන් payments ගන්න
    List<Payment> getPaymentsByRegistrationId(String registrationId) throws Exception;
}