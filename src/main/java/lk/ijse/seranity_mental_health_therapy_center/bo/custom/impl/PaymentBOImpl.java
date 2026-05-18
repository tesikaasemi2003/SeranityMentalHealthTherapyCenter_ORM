package lk.ijse.seranity_mental_health_therapy_center.bo.custom.impl;

import lk.ijse.seranity_mental_health_therapy_center.bo.custom.PaymentBO;
import lk.ijse.seranity_mental_health_therapy_center.dao.DAOFactory;
import lk.ijse.seranity_mental_health_therapy_center.dao.DAOTypes;
import lk.ijse.seranity_mental_health_therapy_center.dao.custom.PaymentDAO;
import lk.ijse.seranity_mental_health_therapy_center.entity.Payment;
import lk.ijse.seranity_mental_health_therapy_center.bo.exception.PaymentProcessingException;

import java.util.List;

public class PaymentBOImpl implements PaymentBO {

    private final PaymentDAO paymentDAO =
            (PaymentDAO) DAOFactory.getInstance().getDAO(DAOTypes.PAYMENT);

    @Override
    public boolean savePayment(Payment payment) throws Exception {
        // Invalid amount check
        if (payment.getAmount() <= 0) {
            throw new PaymentProcessingException(
                    "Invalid payment amount: " + payment.getAmount() + ". Amount must be greater than 0."
            );
        }
        // Registration not linked
        if (payment.getRegistration() == null) {
            throw new PaymentProcessingException(
                    "Payment must be linked to a valid registration."
            );
        }
        return paymentDAO.save(payment);
    }

    @Override
    public boolean updatePayment(Payment payment) throws Exception {
        return paymentDAO.update(payment);
    }

    @Override
    public boolean deletePayment(String paymentId) throws Exception {
        return paymentDAO.delete(paymentId);
    }

    @Override
    public Payment searchPayment(String paymentId) throws Exception {
        return paymentDAO.search(paymentId);
    }

    @Override
    public List<Payment> getAllPayments() throws Exception {
        return paymentDAO.getAll();
    }

    @Override
    public String generateNextPaymentId() throws Exception {
        return paymentDAO.generateNextId();
    }
}