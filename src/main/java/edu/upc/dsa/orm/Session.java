package edu.upc.dsa.orm;

import java.util.HashMap;
import java.util.List;

public interface Session<E> {
    void save(Object entity);                                           // Crud
    void close();
    Object get(Class theClass, Object ID);                                 // cRud
    void update(Object object);                                         // crUd
    void delete(Object object);                                         // cruD     // cR
    List<Object> findAll(Class theClass, HashMap params);
}
