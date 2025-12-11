package edu.upc.dsa.orm;


import edu.upc.dsa.orm.DBUtils;

import java.sql.Connection;
import java.sql.DriverManager;

public class FactorySession {

    public static Session openSession() {
        Connection conn = getConnection();
        Session session = new SessionImpl(conn);
        return session;
    }



    public static Connection getConnection()  {
        String db = DBUtils.getDb();
        String host = DBUtils.getDbHost();
        String port = DBUtils.getDbPort();
        String user = DBUtils.getDbUser();
        String pass = DBUtils.getDbPasswd();


        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:mariadb://"+host+":"+port+"/"+
                    db+"?user="+user+"&password="+pass);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return connection;
    }

    public static Session openSession(String url, String user, String password) {
        String db = DBUtils.getDb();
        String port = DBUtils.getDbPort();


        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:mariadb://"+url+":"+port+"/"+
                    db+"?user="+user+"&password="+password);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Session session = new SessionImpl(connection);
        return session;
    }
}
