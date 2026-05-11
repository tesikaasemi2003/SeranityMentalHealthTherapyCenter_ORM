package lk.ijse.seranity_mental_health_therapy_center.dao.custom;

import lk.ijse.seranity_mental_health_therapy_center.dao.CrudDAO;
import lk.ijse.seranity_mental_health_therapy_center.entity.User;

public interface UserDAO extends CrudDAO<User> {

    // Username ගෙන් user search කරන්න (login සඳහා)
    User searchByUsername(String username) throws Exception;

    // Username already exists ද check කරන්න
    boolean isUsernameExists(String username) throws Exception;
}