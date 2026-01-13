package edu.upc.dsa;

import edu.upc.dsa.models.Objects;
import edu.upc.dsa.models.User;
import edu.upc.dsa.models.GameObject;
import edu.upc.dsa.models.UserGameObject;
import edu.upc.dsa.orm.dao.UserDAOImpl;
import edu.upc.dsa.orm.dao.GameObjectDAOImpl;
import edu.upc.dsa.orm.Session;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GameManagerImpl implements GameManager {
    private static GameManager instance;
    
    // key: object name
    protected Map<String, GameObject> registred_objects;
    protected List<GameObject> objects;
    protected UserDAOImpl dao;
    protected GameObjectDAOImpl objectDAO;
    final static Logger logger = Logger.getLogger(GameManagerImpl.class);

    private GameManagerImpl() {
        this.registred_objects = new HashMap<>();
        this.objects = new LinkedList<>();
        this.dao = new UserDAOImpl();
        this.objectDAO = new GameObjectDAOImpl();

        // Cargar objetos de la base de datos
        loadObjectsFromDatabase();
    }

    private void loadObjectsFromDatabase() {
        logger.info("Cargando objetos desde la base de datos...");
        List<GameObject> dbObjects = objectDAO.getAllObjects();
        if (dbObjects != null) {
            for (GameObject obj : dbObjects) {
                this.objects.add(obj);
                this.registred_objects.put(obj.getNombre(), obj);
            }
            logger.info("Cargados " + dbObjects.size() + " objetos desde la BD");
        }
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

        User u = dao.getUser(username);

        if (u == null || !u.getPassword().equals(password)) {
            logger.error("Usuario o contraseña incorrectas");
            throw new Exception("Usuario o contraseña incorrectas");
        }

        logger.info("Sesión iniciada correctamente para " + username);
        return u;
    }

    public User Register(String username, String password, String email) throws Exception {
        username = username.toLowerCase();
        email = email.toLowerCase();
        logger.info("Intentando registrar usuario: " + username);
        
        User existingUser = dao.getUser(username);
        if (existingUser != null) {
            logger.error("Error al registrar: el usuario '" + username + "' ya existe en la base de datos.");
            throw new Exception("El usuario ya existe");
        }
        User u = new User(username, password, email);
        dao.addUser(u);

        logger.info("Registrado correctamente");
        return u;
    }

    @Override
    public GameObject addNewObjeto(String nombre, String descripcion, Objects tipo, int precio) {
        logger.info("Nuevo objeto " + nombre + " " + descripcion + "creado");
        GameObject o = new GameObject(nombre, descripcion, tipo, precio);
        
        // Guardar en la base de datos
        try {
            objectDAO.addObject(o);
        } catch (Exception e) {
            logger.error("No se ha podido guardar el objeto en la BD", e);
        }
        
        this.objects.add(o);
        this.registred_objects.put(nombre, o);
        logger.info("Nuevo objeto " + nombre + " " + descripcion + " " + o.getId() + " " + "creado correctamente");
        return o;
    }

    @Override
    public List<UserGameObject> getListObjects(String username) {
        logger.info("Obtener todos los objetos del usuario  " + username);
        User u = dao.getUser(username);
        if (u == null)
            return null;
        
        List<UserGameObject> list = dao.getObjectsbyUser(u);
        return list;
    }

    @Override
    public User addObjectToUser(String username, String objectId) {
        User u = dao.getUser(username);
        GameObject o = this.getStoreObject(objectId);

        if (u == null || o == null)
            return null;

        dao.buyItem(u, o);
        
        return u;
    }

    public User purchaseObject(String username, String objectId) throws Exception {
        logger.info("Añadiendo objeto " + objectId + " al usuario " + username);

        User u = dao.getUser(username);
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

        dao.updateUser(u);

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
        return dao.getUser(username);
    }

    @Override
    public int getNumberOfUsersRegistered() {
        Session session = null;
        try {
            session = edu.upc.dsa.orm.FactorySession.openSession();
            List<Object> users = session.findAll(User.class, new HashMap<>());
            return users != null ? users.size() : 0;
        } catch (Exception e) {
            logger.error("Error al contar usuarios", e);
            return 0;
        } finally {
            if (session != null) session.close();
        }
    }
    
    @Override
    public void clear() {
        try (java.sql.Connection conn = edu.upc.dsa.orm.DBUtils.getConnection()) {
            java.sql.DatabaseMetaData meta = conn.getMetaData();
            logger.info("Limpiando DB: " + meta.getURL() + " con usuario " + meta.getUserName());
            
            // solo en testeo - ESTO BORRA TODA LA BASE DE DATOS
            /*
            try (java.sql.Statement st = conn.createStatement()) {
                st.executeUpdate("SET FOREIGN_KEY_CHECKS = 0;");
                int rowsUG = st.executeUpdate("DELETE FROM User_GameObject;");
                int rowsU = st.executeUpdate("DELETE FROM User;");
                int rowsG = st.executeUpdate("DELETE FROM GameObject;");
                st.executeUpdate("SET FOREIGN_KEY_CHECKS = 1;");
                
                logger.info("Tablas limpiadas. Filas borradas: User_GameObject=" + rowsUG + ", User=" + rowsU + ", GameObject=" + rowsG);
            }
            */
            
            this.registred_objects.clear();
            this.objects.clear();
            logger.info("Estado interno de GameManager limpiado");
        } catch (Exception e) {
            logger.error("Error al limpiar GameManager", e);
        }
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