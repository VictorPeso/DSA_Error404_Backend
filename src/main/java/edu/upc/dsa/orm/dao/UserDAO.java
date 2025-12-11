package edu.upc.dsa.orm.dao;

import edu.upc.dsa.models.*;

import java.util.List;

public interface UserDAO {

    public String addUser(User user);

    public User getUser(String username);

    public void updateUser(User user);

    public void deleteEmployee(User user);

    // Optional methods - uncomment if needed:
    // public List<User> getUsers();
    // public List<User> getEmployeeByDept(int deptId);
}
