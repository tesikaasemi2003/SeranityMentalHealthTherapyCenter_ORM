package lk.ijse.seranity_mental_health_therapy_center.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDTO {
    private String id;
    private LocalDate paymentDate;
    private double amount;
    private String status; // "PAID", "PENDING"
    private String registrationId; // FK reference
}