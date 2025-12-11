package edu.upc.dsa;

import edu.upc.dsa.models.Objects;
import edu.upc.dsa.models.User;
import edu.upc.dsa.models.GameObject;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GameManagerImpl implements GameManager {
    private static GameManager instance;
    // protected List<User> registred_users;
    // key: username
    protected Map<String, User> registred_users;
    // key: object name
    protected Map<String, GameObject> registred_objects;
    protected List<GameObject> objects;
    final static Logger logger = Logger.getLogger(GameManagerImpl.class);

    private GameManagerImpl() {
        this.registred_users = new HashMap<>();
        this.registred_objects = new HashMap<>();
        this.objects = new LinkedList<>();
    }

    public static GameManager getInstance() {
        if (instance == null)
            instance = new GameManagerImpl();
        return instance;
    }

    @Override
    public User LogIn(String username, String password) throws Exception {
        username = username.toLowerCase();
        logger.info("Iniciando sesión " + username);
        User u = registred_users.get(username);
        if (u == null || !u.getPassword().equals(password)) {
            logger.error("Usuario o contraseña incorrectas");
            throw new Exception("Usuario o contraseña incorrectas");
        }
        logger.info("Sesión iniciada");
        return u;
    }

    public User Register(String username, String password, String email) throws Exception {
        username = username.toLowerCase();
        email = email.toLowerCase();
        logger.info("registrar el usuario " + username + " " + email);
        if (registred_users.containsKey(username)) {
            logger.error("el usuario " + username + " ya existe");
            throw new Exception("El usuario ya existe");
        }
        for (User existingUser : registred_users.values()) {
            if (existingUser.getEmail().equalsIgnoreCase(email)) {
                logger.error("el correo " + email + " ya esta registrado");
                throw new Exception("El correo ya está registrado");
            }
        }

        User u = new User(username, password, email);
        this.registred_users.put(username, u);
        logger.info("Registrado correctamente");
        return u;
    }

    @Override
    public GameObject addNewObjeto(String nombre, String descripcion, Objects tipo, int precio) {
        logger.info("Nuevo objeto " + nombre + " " + descripcion + "creado");
        GameObject o = new GameObject(nombre, descripcion, tipo, precio);
        this.objects.add(o);
        this.registred_objects.put(nombre, o);
        logger.info("Nuevo objeto " + nombre + " " + descripcion + " " + o.getId() + " " + "creado correctamente");
        return o;
    }

    @Override
    public List<GameObject> getListObjects(String username) {
        logger.info("Obtener todos los objetos del usuario  " + username);
        User u = registred_users.get(username);
        if (u == null)
            return null;
        List<GameObject> list = u.getMyobjects();
        return list;
    }

    @Override
    public User addObjectToUser(String username, String objectId) {
        User u = this.registred_users.get(username);
        GameObject o = this.getStoreObject(objectId);

        if (u == null || o == null)
            return null;

        if (u.CheckObject(o)) {
            logger.info("El usuario ya tiene el objeto");

            // temporal hasta nueva solucion
            u.setMyobjects(o);
        } else {
            u.setMyobjects(o);
        }
        return u;
    }

    public User purchaseObject(String username, String objectId) throws Exception {
        logger.info("Añadiendo objeto " + objectId + " al usuario " + username);

        User u = this.registred_users.get(username);

        GameObject o = this.getStoreObject(objectId);

        if (u == null) {
            logger.error("Usuario no encontrado: " + username);

            return null;
        }

        if (o == null) {
            logger.error("Objeto no encontrado en la tienda: " + objectId);

            return null;
        }

        if (u.getMonedas() < o.getPrecio()) {
            throw new Exception("Saldo insuficiente");
        }

        int nuevoSaldo = u.getMonedas() - o.getPrecio();
        u.setMonedas(nuevoSaldo);

        logger.info("Compra realizada. Nuevo saldo: " + nuevoSaldo);
        return this.addObjectToUser(username, objectId);
    }

    @Override
    public String getObjectId(String objectName) {
        logger.info("Obtener el objeto id de " + objectName);
        GameObject o = this.registred_objects.get(objectName);
        return o.getId();
    }

    @Override
    public User getUser(String username) {
        User u = this.registred_users.get(username);
        return u;
    }

    // ------------------------------------------------
    public int getNumberOfUsersRegistered() {
        int i = registred_users.size();
        return i;
    }

    public GameObject getStoreObject(String id) {
        logger.info("Buscando objeto en la lista de objetos: " + id);

        for (GameObject o : registred_objects.values()) {
            if (o.getId().equals(id)) {
                return o;
            }
        }
        logger.error("Objeto " + id + " no encontrado en la lista de objeto");
        return null;
    }

    @Override
    public List<GameObject> getAllStoreObjects() {
        logger.info("Obteniendo todos los objetos de la tienda");
        return this.objects;
    }
}