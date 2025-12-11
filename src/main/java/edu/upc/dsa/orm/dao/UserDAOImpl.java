package edu.upc.dsa.orm.dao;

import edu.upc.dsa.GameManagerImpl;
import edu.upc.dsa.orm.FactorySession;
import edu.upc.dsa.orm.Session;
import edu.upc.dsa.models.*;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;

public class UserDAOImpl implements UserDAO {
    final static Logger logger = Logger.getLogger(UserDAOImpl.class);

    public String addUser (User user) {
        Session session = null;
        try {
            session = FactorySession.openSession();
            session.save(user);
        }
        catch (Exception e) {
            logger.info("No se ha podido registrar el usuario " + user.getUsername());
        }
        finally {
            session.close();
        }

        return user.getUsername();
    }


    public User getUser (String username) {
        Session session = null;
        User user = null;
        try {
            session = FactorySession.openSession();
            user = (User) session.get(User.class, username);
        }
        catch (Exception e) {
            logger.info("No se ha encontrado el usuario " + user.getUsername());
        }
        finally {
            session.close();
        }

        return user;
    }

    public void updateUser(User user) {
        Session session = null;
        try {
            session = FactorySession.openSession();
            session.update(user);
        }
        catch (Exception e) {
            logger.info("No se ha podido actualizar el usuario " + user.getUsername());
        }
        finally {
            session.close();
        }
    }

    public void deleteEmployee(User user) {
        Session session = null;
        try {
            session = FactorySession.openSession();
            session.delete(user);
        }
        catch (Exception e) {
            logger.info("No se ha podido eliminar el usuario " + user.getUsername());
        }
        finally {
            session.close();
        }

    }


//
//    public List<User> getEmployees() {
//        Session session = null;
//        List<User> employeeList=null;
//        try {
//            session = FactorySession.openSession();
//            employeeList = session.findAll(User.class);
//        }
//        catch (Exception e) {
//            // LOG
//        }
//        finally {
//            session.close();
//        }
//        return employeeList;
//    }
//
//
//    public List<User> getEmployeeByDept(int deptID) {
//
//        // SELECT e.name, d.name FROM Employees e, DEpt d WHERE e.deptId = d.ID AND e.edat>87 AND ........
//
////        Connection c =
//
//        Session session = null;
//        List<User> employeeList=null;
//        try {
//            session = FactorySession.openSession();
//
//
//            HashMap<String, Integer> params = new HashMap<String, Integer>();
//            params.put("deptID", deptID);
//
//            employeeList = session.findAll(User.class, params);
//        }
//        catch (Exception e) {
//            // LOG
//        }
//        finally {
//            session.close();
//        }
//        return employeeList;
//    }
//
//    /*
//
//    public void customQuery(xxxx) {
//        Session session = null;
//        List<Employee> employeeList=null;
//        try {
//            session = FactorySession.openSession();
//            Connection c = session.getConnection();
//            c.createStatement("SELECT * ")
//
//        }
//*/

}
