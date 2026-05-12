package lk.ijse.seranity_mental_health_therapy_center.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TherapyProgramDTO {
    private String id;
    private String name;
    private String description;
    private int durationWeeks;
    private double fee;
    private String therapistId; // FK reference
}