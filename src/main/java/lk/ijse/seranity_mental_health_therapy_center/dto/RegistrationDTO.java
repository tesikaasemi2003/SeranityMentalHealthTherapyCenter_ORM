package lk.ijse.seranity_mental_health_therapy_center.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationDTO {
    private String id;
    private LocalDate registrationDate;
    private double amountPaid;
    private String patientId;    // FK reference
    private String programId;    // FK reference
}