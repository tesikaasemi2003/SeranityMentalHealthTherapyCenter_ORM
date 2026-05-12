package lk.ijse.seranity_mental_health_therapy_center.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private String id;
    private String username;
    private String password;
    private String role; // "ADMIN" or "RECEPTIONIST"
}
