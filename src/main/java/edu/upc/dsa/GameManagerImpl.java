package edu.upc.dsa;

import edu.upc.dsa.models.User;
import edu.upc.dsa.models.GameObject;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GameManagerImpl implements GameManager {
    private static GameManager instance;
    //protected List<User> registred_users;
    protected Map<String, User> registred_users;
    protected List<GameObject> objects;
    final static Logger logger = Logger.getLogger(GameManagerImpl.class);

    private GameManagerImpl() {
        this.registred_users = new HashMap<>();
        this.objects = new LinkedList<>();
    }

    public static GameManager getInstance() {
        if (instance==null) instance = new GameManagerImpl();
        return instance;
    }


    @Override
    public User LogIn(String username, String password) throws Exception {
        logger.info("Iniciando sesión "+username);
        User u = registred_users.get(username);
        if (u == null || !u.getPassword().equals(password)) {
            logger.error("Usuario o contraseña incorrectas");
            throw new Exception("Usuario o contraseña incorrectas");
        }
        logger.info("Sesión iniciada");
        return u;
    }

    @Override
    public User Register(String username, String password) throws Exception {
        logger.info("registrar el usuario " + username);
        if (registred_users.containsKey(username)) {
            logger.error("el usuario " + username + " ya existe");
            return registred_users.get(username);
        }
        User u = new User(username, password);
        this.registred_users.put(username, u);
        logger.info("Registrado correctamente");
        return u;
    }

    @Override
    public GameObject addNewObjeto(String nombre, String descripcion) {
        GameObject o = new GameObject(nombre, descripcion);
        this.objects.add(o);
        return o;
    }

    @Override
    public List<GameObject> getListObjects(String username) {
        User u = registred_users.get(username);
        List<GameObject> list = u.getMyobjects();
        return list;
    }

    @Override
    public User setObject(String username) {
        logger.info("Añadiendo objetos por defecto al usuario " + username);

        User u = this.registred_users.get(username);
        if (u == null) {
            logger.error("Usuario no encontrado: " + username);
            return null;
        }

        // Solo añadir objetos si el usuario no tiene ninguno aún
        if (u.getMyobjects().isEmpty()) {
            for (GameObject o : this.objects) {
                u.setMyobjects(o);
            }
            logger.info("Objetos por defecto añadidos al usuario " + username);
        } else {
            logger.info("El usuario " + username + " ya tiene objetos asignados, no se añaden de nuevo.");
        }

        return null;
    }


}
