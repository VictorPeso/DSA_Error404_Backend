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

    public static String createQueryINSERT_M2N(Object entity, Object entity2, String relationTable) {
        // INSERT INTO User (ID, lastName, firstName, address, city) VALUES (?, ?, ?, ?, ?)
        StringBuffer sb = new StringBuffer("INSERT INTO ");
        sb.append(relationTable);
        sb.append(" (");

        String [] fields1 = edu.upc.dsa.orm.util.ObjectHelper.getFields(entity);
        String [] fields2 = edu.upc.dsa.orm.util.ObjectHelper.getFields(entity2);

        sb.append(fields1[0]);
        sb.append(", ");
        sb.append(fields2[0]);
        sb.append(") VALUES (?, ?)");
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

    public static String createQueryUPDATE_M2N(Class theClass1, Class theClass2, String relationTable, String columnToUpdate, HashMap<String, String> params) {
        // Comenzamos el UPDATE de la tabla de relación
        StringBuilder sb = new StringBuilder("UPDATE " + relationTable + " SET " + columnToUpdate + "=? WHERE 1=1");

        // Agregamos las condiciones de relación con las tablas principales usando AND
        sb.append(" AND " + relationTable + "." + theClass1.getSimpleName().toLowerCase() + "_id = ?");
        sb.append(" AND " + relationTable + "." + theClass2.getSimpleName().toLowerCase() + "_id = ?");

        // Agregamos condiciones adicionales de params
        for (String key : params.keySet()) {
            sb.append(" AND " + key + "=?");
        }

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

    public static String createqueryFINDALL_M2N(Class theClass1, Class theClass2, String relationTable, HashMap<String, String> params) {

        // Comenzamos el SELECT desde las dos tablas y la tabla de relación
        StringBuilder sb = new StringBuilder("SELECT * FROM "
                + theClass1.getSimpleName() + ", "
                + theClass2.getSimpleName() + ", "
                + relationTable
                + " WHERE 1=1");

        // Agregamos las condiciones de relación entre las tablas usando AND
        sb.append(" AND " + theClass1.getSimpleName() + ".id = " + relationTable + "." + theClass1.getSimpleName().toLowerCase() + "_id");
        sb.append(" AND " + theClass2.getSimpleName() + ".id = " + relationTable + "." + theClass2.getSimpleName().toLowerCase() + "_id");

        // Agregamos los filtros dinámicos que vienen en params
        for (String key : params.keySet()) {
            sb.append(" AND " + key + "=?");
        }

        return sb.toString();
    }
}
