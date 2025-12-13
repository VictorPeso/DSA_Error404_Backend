package edu.upc.dsa.orm;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;

public interface Session<E> {
    void save(Object entity); // Crud

    void close();

    Object get(Class theClass, Object ID); // cRud

    void update(Object object); // crUd

    void delete(Object object); // cruD // cR

    List<Object> findAll(Class theClass, HashMap params);

    List<Object> findAll_M2N(Class theClass, Class theClass2, String inter, HashMap params);

    void save_M2N(Object entity, Object entity2, String relationTable);

    boolean exists_M2N(String relationTable, String field1, Object value1, String field2, Object value2);

    void updateQuantity_M2N(String relationTable, String field1, Object value1, String field2, Object value2);

    Connection getConnection();
}
