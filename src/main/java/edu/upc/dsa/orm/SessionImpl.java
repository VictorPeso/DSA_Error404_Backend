package edu.upc.dsa.orm;

import edu.upc.dsa.orm.Session;
import edu.upc.dsa.orm.util.ObjectHelper;
import edu.upc.dsa.orm.util.QueryHelper;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SessionImpl implements Session {
    private final Connection conn;
    private boolean inTransaction = false;

    public SessionImpl(Connection conn) {
        this.conn = conn;
    }

    public void save(Object entity) {
        String insertQuery = QueryHelper.createQueryINSERT(entity);
        
        try (PreparedStatement pstm = conn.prepareStatement(insertQuery)) {
            int i = 1;
            for (String field : ObjectHelper.getFields(entity)) {
                pstm.setObject(i++, ObjectHelper.getter(entity, field));
            }
            pstm.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return this.conn;
    }

    public Object get(Class theClass, Object ID) {
        String selectQuery = QueryHelper.createQuerySELECT(theClass);
        Object o = null;

        try (PreparedStatement pstm = conn.prepareStatement(selectQuery)) {
            pstm.setObject(1, ID);

            try (ResultSet res = pstm.executeQuery()) {
                if (res.next()) {
                    o = theClass.newInstance();
                    ResultSetMetaData rsmd = res.getMetaData();
                    int numColumns = rsmd.getColumnCount();
                    for (int i = 1; i <= numColumns; i++) { 
                        String key = rsmd.getColumnName(i);
                        Object value = res.getObject(i);
                        ObjectHelper.setter(o, key, value);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return o;
    }

    public void update(Object object) {
        String updateQuery = QueryHelper.createQueryUPDATE(object);

        try (PreparedStatement pstm = conn.prepareStatement(updateQuery)) {
            String[] fields = ObjectHelper.getFields(object);
            int i = 1;

            // Primero los campos que van en el SET
            for (String field : fields) {
                if (!field.equals(fields[0])) {
                    pstm.setObject(i++, ObjectHelper.getter(object, field));
                }
            }

            // Finalmente el valor del ID para el WHERE
            pstm.setObject(i, ObjectHelper.getter(object, fields[0]));

            pstm.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(Object object) {
        String deleteQuery = QueryHelper.createQueryDELETE(object);

        try (PreparedStatement pstm = conn.prepareStatement(deleteQuery)) {
            String idValue = ObjectHelper.getFields(object)[0];
            pstm.setObject(1, ObjectHelper.getter(object, idValue));

            pstm.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Object> findAll(Class theClass, HashMap params) {
        String selectQuery = QueryHelper.createqueryFINDALL(theClass, params);
        List<Object> resultList = new ArrayList<>();

        try (PreparedStatement pstm = conn.prepareStatement(selectQuery)) {
            int index = 1;

            for (Object key : params.keySet()) {
                pstm.setObject(index, params.get(key));
                index++;
            }

            try (ResultSet res = pstm.executeQuery()) {
                ResultSetMetaData rsmd = res.getMetaData();
                int numColumns = rsmd.getColumnCount();

                while (res.next()) {
                    Object o = theClass.newInstance();
                    for (int i = 1; i <= numColumns; i++) {
                        String colName = rsmd.getColumnName(i);
                        Object value = res.getObject(i);
                        ObjectHelper.setter(o, colName, value);
                    }
                    resultList.add(o);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return resultList;
    }

    public List<Object> findAll_M2N(Class theClass, Class theClass2, String inter, HashMap params) {
        String selectQuery = QueryHelper.createqueryFINDALL_M2N(theClass, theClass2, inter, params);
        List<Object> resultList = new ArrayList<>();

        try (PreparedStatement pstm = conn.prepareStatement(selectQuery)) {
            int index = 1;

            for (Object key : params.keySet()) {
                pstm.setObject(index, params.get(key));
                index++;
            }

            try (ResultSet res = pstm.executeQuery()) {
                ResultSetMetaData rsmd = res.getMetaData();
                int numColumns = rsmd.getColumnCount();

                while (res.next()) {
                    Object o = theClass2.newInstance();
                    for (int i = 1; i <= numColumns; i++) {
                        String colName = rsmd.getColumnName(i);
                        Object value = res.getObject(i);
                        ObjectHelper.setter(o, colName, value);
                    }
                    resultList.add(o);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return resultList;
    }

    public void save_M2N(Object entity, Object entity2, String relationTable) {
        String insertQuery = QueryHelper.createQueryINSERT_M2N(entity, entity2, relationTable);

        try (PreparedStatement pstm = conn.prepareStatement(insertQuery)) {
            String[] fields1 = edu.upc.dsa.orm.util.ObjectHelper.getFields(entity);
            String[] fields2 = edu.upc.dsa.orm.util.ObjectHelper.getFields(entity2);

            pstm.setObject(1, ObjectHelper.getter(entity, fields1[0]));
            pstm.setObject(2, ObjectHelper.getter(entity2, fields2[0]));

            pstm.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean exists_M2N(String relationTable, String field1, Object value1, String field2, Object value2) {
        String query = QueryHelper.createQueryEXISTS_M2N(relationTable, field1, field2);
        
        try (PreparedStatement pstm = conn.prepareStatement(query)) {
            pstm.setObject(1, value1);
            pstm.setObject(2, value2);
            
            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void updateQuantity_M2N(String relationTable, String field1, Object value1, String field2, Object value2) {
        String query = QueryHelper.createQueryUPDATE_QUANTITY_M2N(relationTable, field1, field2);
        
        try (PreparedStatement pstm = conn.prepareStatement(query)) {
            pstm.setObject(1, value1);
            pstm.setObject(2, value2);
            pstm.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void beginTransaction() {
        try {
            if (!inTransaction) {
                conn.setAutoCommit(false);
                inTransaction = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void commit() {
        try {
            if (inTransaction) {
                conn.commit();
                conn.setAutoCommit(true);
                inTransaction = false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void rollback() {
        try {
            if (inTransaction) {
                conn.rollback();
                conn.setAutoCommit(true);
                inTransaction = false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
