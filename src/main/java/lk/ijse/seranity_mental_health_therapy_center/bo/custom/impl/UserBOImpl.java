package lk.ijse.seranity_mental_health_therapy_center.bo.custom.impl;

import lk.ijse.seranity_mental_health_therapy_center.bo.custom.UserBO;
import lk.ijse.seranity_mental_health_therapy_center.dao.DAOFactory;
import lk.ijse.seranity_mental_health_therapy_center.dao.DAOTypes;
import lk.ijse.seranity_mental_health_therapy_center.dao.custom.UserDAO;
import lk.ijse.seranity_mental_health_therapy_center.entity.User;

import java.util.List;

public class UserBOImpl implements UserBO {

    private final UserDAO userDAO =
            (UserDAO) DAOFactory.getInstance().getDAO(DAOTypes.USER);

    @Override
    public boolean saveUser(User user) throws Exception {
        return userDAO.save(user);
    }

    @Override
    public boolean updateUser(User user) throws Exception {
        return userDAO.update(user);
    }

    @Override
    public boolean deleteUser(String userId) throws Exception {
        return userDAO.delete(userId);
    }

    @Override
    public User searchUser(String userId) throws Exception {
        return userDAO.search(userId);
    }

    @Override
    public List<User> getAllUsers() throws Exception {
        return userDAO.getAll();
    }

    @Override
    public User getUserByUsername(String username) throws Exception {
        return userDAO.getUserByUsername(username);
    }
}