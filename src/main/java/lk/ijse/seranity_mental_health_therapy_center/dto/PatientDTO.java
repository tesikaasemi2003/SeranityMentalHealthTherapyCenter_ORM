package lk.ijse.seranity_mental_health_therapy_center.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatientDTO {
    private String id;
    private String name;
    private String nic;
    private String email;
    private String phone;
    private String medicalHistory;
}