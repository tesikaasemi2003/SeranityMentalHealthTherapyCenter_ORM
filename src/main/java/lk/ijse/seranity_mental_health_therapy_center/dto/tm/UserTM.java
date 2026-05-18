package lk.ijse.seranity_mental_health_therapy_center.dto.tm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserTM {
    private String id;
    private String username;
    private String role;

}