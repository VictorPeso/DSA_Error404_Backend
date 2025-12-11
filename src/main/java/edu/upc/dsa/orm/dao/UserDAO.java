package edu.upc.dsa.orm.dao;

import edu.upc.dsa.models.*;

import java.util.List;

public interface UserDAO {

    String addUser(User user);
    User getUser(String username);
    void updateUser(User user);
    void deleteUser(User user);
    List<GameObject> getObjectsbyUser(User user);

    // Optional methods - uncomment if needed:
    // public List<User> getUsers();
    // public List<User> getEmployeeByDept(int deptId);
}
