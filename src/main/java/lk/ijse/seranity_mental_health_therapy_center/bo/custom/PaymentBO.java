package lk.ijse.seranity_mental_health_therapy_center.bo.custom;

import lk.ijse.seranity_mental_health_therapy_center.bo.SuperBO;
import lk.ijse.seranity_mental_health_therapy_center.entity.Payment;

import java.util.List;

public interface PaymentBO extends SuperBO {
    boolean savePayment(Payment payment) throws Exception;
    boolean updatePayment(Payment payment) throws Exception;
    boolean deletePayment(String paymentId) throws Exception;
    Payment searchPayment(String paymentId) throws Exception;
    List<Payment> getAllPayments() throws Exception;
    String generateNextPaymentId() throws Exception;
}