package lk.ijse.seranity_mental_health_therapy_center.bo.custom;

import lk.ijse.seranity_mental_health_therapy_center.bo.SuperBO;
import lk.ijse.seranity_mental_health_therapy_center.entity.User;

import java.util.List;

public interface UserBO extends SuperBO {
    boolean saveUser(User user) throws Exception;
    boolean updateUser(User user) throws Exception;
    boolean deleteUser(String userId) throws Exception;
    User searchUser(String userId) throws Exception;
    List<User> getAllUsers() throws Exception;
    User getUserByUsername(String username) throws Exception;
}