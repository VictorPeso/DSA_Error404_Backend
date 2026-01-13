package edu.upc.dsa.orm.util;

import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ObjectHelper {
    final static Logger logger = Logger.getLogger(ObjectHelper.class);

    public static String[] getFields(Object entity) {
        Class theClass = entity.getClass();
        Field[] fields = theClass.getDeclaredFields();

        List<String> fieldList = new ArrayList<>();
        for (Field f : fields) {
            if (!java.lang.reflect.Modifier.isTransient(f.getModifiers())) {
                fieldList.add(f.getName());
            }
        }

        return fieldList.toArray(new String[0]);
    }

    public static void setter(Object instance, String propertyName, Object value) {
        String setterName = "NotCalledSetterMethod";
        try {
            // Construimos el nombre del setter: set + Propiedad
            setterName = "set" + Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);

            // Class<?> clazz = instance.getClass();
            // Class<?> paramType = value.getClass();

            // Obtenim la classe i el mètode getter
            Method setter = instance.getClass().getMethod(setterName, value.getClass());

            // Invocamos el setter
            setter.invoke(instance, value);

            // logger.info("UPDATED " + instance.getClass().getSimpleName() + " TO " +
            // instance.toString());

        } catch (NoSuchMethodException e) {
            throw new RuntimeException(
                    "No existe el setter '" + setterName + "' con parámetro de tipo "
                            + value.getClass().getName(),
                    e);

        } catch (IllegalAccessException e) {
            throw new RuntimeException(
                    "No tienes acceso para invocar el setter '" + setterName + "'", e);

        } catch (InvocationTargetException e) {
            Throwable causa = e.getCause();
            throw new RuntimeException(
                    "El setter '" + setterName + "' lanzó una excepción interna: "
                            + causa.getMessage(),
                    causa);
        }
    }

    public static Object getter(Object instance, String propertyName) {
        String getterName = "NotCalledGetterMethod";
        try {
            getterName = "get" + Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);

            // Obtenim la classe i el mètode getter
            Method getter = instance.getClass().getMethod(getterName);

            Object value = getter.invoke(instance);
            if (value != null && value.getClass().isEnum()) {
                return value.toString();
            }
            return value;

        } catch (NoSuchMethodException e) {
            throw new RuntimeException(
                    "No existe el getter '" + getterName + "'", e);

        } catch (IllegalAccessException e) {
            throw new RuntimeException(
                    "No tienes acceso para invocar el getter '" + getterName + "'", e);

        } catch (InvocationTargetException e) {
            Throwable causa = e.getCause();
            throw new RuntimeException(
                    "El getter '" + getterName + "' lanzó una excepción interna: "
                            + causa.getMessage(),
                    causa);
        }
    }
}