package edu.upc.dsa.orm.dao;

import edu.upc.dsa.models.User;
import edu.upc.dsa.models.UserGameObject;

import java.util.List;

public interface UserDAO {
    String addUser(User user);

    User getUser(String username);

    void updateUser(User user);

    void deleteUser(User user);

    List<UserGameObject> getObjectsbyUser(User user);

    void removeObjectFromUser(String username, String objectId);

    void updateObjectQuantity(String username, String objectId, int newQuantity);

    // Optional methods - uncomment if needed:
    // public List<User> getUsers();
    // public List<User> getEmployeeByDept(int deptId);
}

