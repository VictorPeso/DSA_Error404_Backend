package edu.upc.dsa.orm.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class QueryHelper {

    public static String createQueryINSERT(Object entity) {
        // INSERT INTO User (ID, lastName, firstName, address, city) VALUES (?, ?, ?, ?, ?)
        StringBuffer sb = new StringBuffer("INSERT INTO ");
        sb.append(entity.getClass().getSimpleName()).append(" ");
        sb.append("(");

        String [] fields = edu.upc.dsa.orm.util.ObjectHelper.getFields(entity);

        boolean first = true;
        for (String field: fields) {
            if (first){
                sb.append(field);
                first = false;
            }
            else {
                sb.append(", ").append(field);
            }
        }
        sb.append(") VALUES (");

        first = true;
        for (String field: fields) {
            if (first){
                sb.append("?");
                first = false;
            }
            else {
                sb.append(", ?");
            }
        }
        sb.append(")");
        return sb.toString();
    }

    public static String createQuerySELECT(Class c) {
        // SELECT * FROM Users WHERE ID = ?
        Object entity;
        StringBuffer sb = new StringBuffer();
        try {
            entity = c.getDeclaredConstructor().newInstance();

            String [] fields = edu.upc.dsa.orm.util.ObjectHelper.getFields(entity);

            sb.append("SELECT * FROM ").append(entity.getClass().getSimpleName());
            sb.append(" WHERE ");
            sb.append(fields[0]);
            sb.append(" = ?");

        } catch (Exception e) {
            throw new RuntimeException("Cannot instantiate class " + c.getSimpleName(), e);
        }

        return sb.toString();
    }

    public static String createQueryUPDATE(Object entity) {
        // UPDATE User SET lastName = ?, firstName = ?, address = ?, city = ? WHERE id = ?
        StringBuffer sb = new StringBuffer("UPDATE ");
        sb.append(entity.getClass().getSimpleName()).append(" ");
        sb.append("SET ");

        String [] fields = edu.upc.dsa.orm.util.ObjectHelper.getFields(entity);

        boolean first = true;
        for (String field: fields) {
            if (!field.equals(fields[0])) {
                if (first){
                    sb.append(field);
                    sb.append(" = ?");
                    first = false;
                }
                else {
                    sb.append(", ");
                    sb.append(field);
                    sb.append(" = ?");
                }
            }
        }
        sb.append(" WHERE ");
        sb.append(fields[0]);
        sb.append(" = ?");

        return sb.toString();
    }

    public static String createQueryDELETE(Object entity) {
        // DELETE FROM User WHERE id = ?
        String [] fields = edu.upc.dsa.orm.util.ObjectHelper.getFields(entity);

        StringBuffer sb = new StringBuffer();
        sb.append("DELETE FROM ").append(entity.getClass().getSimpleName());
        sb.append(" WHERE ");
        sb.append(fields[0]);
        sb.append(" = ?");

        return sb.toString();
    }

    public static String createqueryFINDALL(Class theClass, HashMap<String, String> params) {

        Set<Map.Entry<String, String>> set = params.entrySet();

        StringBuffer sb = new StringBuffer("SELECT * FROM "+theClass.getSimpleName()+" WHERE 1=1");
        for (String key: params.keySet()) {
            sb.append(" AND "+key+"=?");
        }
        return sb.toString();
    }

    public static String createqueryFINDALL_M2N(Class theClass1, Class theClass2, String relation_table, HashMap<String, String> params) {

        Set<Map.Entry<String, String>> set = params.entrySet();

        StringBuffer sb = new StringBuffer("SELECT * FROM "+theClass1.getSimpleName()+", " +theClass2.getSimpleName()+", " +relation_table+ " WHERE 1=1");
        for (String key: params.keySet()) {
            sb.append(" AND "+key+"=?");
        }
        return sb.toString();
    }
}
