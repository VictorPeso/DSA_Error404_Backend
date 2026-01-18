package edu.upc.dsa;

import edu.upc.dsa.models.Objects;
import edu.upc.dsa.models.User;
import edu.upc.dsa.models.GameObject;
import edu.upc.dsa.models.UserGameObject;
import edu.upc.dsa.orm.dao.UserDAOImpl;
import edu.upc.dsa.orm.dao.GameObjectDAOImpl;
import edu.upc.dsa.orm.Session;
import edu.upc.dsa.exceptions.*;
import edu.upc.dsa.util.ValidationUtils;
import org.apache.log4j.Logger;
import org.mindrot.jbcrypt.BCrypt;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GameManagerImpl implements GameManager {
    private static class SingletonHolder {
        private static final GameManager INSTANCE = new GameManagerImpl();
    }

    // key: object name
    protected Map<String, GameObject> registred_objects;
    protected Map<String, GameObject> objectsById;
    protected List<GameObject> objects;
    protected UserDAOImpl dao;
    protected GameObjectDAOImpl objectDAO;
    final static Logger logger = Logger.getLogger(GameManagerImpl.class);

    private GameManagerImpl() {
        this.registred_objects = new java.util.concurrent.ConcurrentHashMap<>();
        this.objectsById = new java.util.concurrent.ConcurrentHashMap<>();
        this.objects = new java.util.concurrent.CopyOnWriteArrayList<>();
        this.dao = new UserDAOImpl();
        this.objectDAO = new GameObjectDAOImpl();

        loadObjectsFromDatabase();
    }

    private void loadObjectsFromDatabase() {
        logger.info("Cargando objetos desde la base de datos...");
        List<GameObject> dbObjects = objectDAO.getAllObjects();
        if (dbObjects != null) {
            for (GameObject obj : dbObjects) {
                this.objects.add(obj);
                this.registred_objects.put(obj.getNombre(), obj);
                this.objectsById.put(obj.getId(), obj); // Indexar por ID
            }
            logger.info("Cargados " + dbObjects.size() + " objetos desde la BD");
        }
    }

    public static GameManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public User LogIn(String username, String password) throws FailedLoginException {
        ValidationUtils.validateNotEmpty(username, "username");
        ValidationUtils.validateNotEmpty(password, "password");

        username = username.toLowerCase();
        logger.info("Intento de login: " + username);

        User u = dao.getUser(username);

        if (u == null || !BCrypt.checkpw(password, u.getPassword())) {
            logger.error("Credenciales inválidas para: " + username);
            throw new FailedLoginException("Usuario o contraseña incorrectas");
        }

        logger.info("Login exitoso: " + username);
        return u;
    }

    public User Register(String username, String password, String email) throws UserAlreadyExistsException {
        ValidationUtils.validateUsername(username);
        ValidationUtils.validatePassword(password);
        ValidationUtils.validateEmail(email);

        username = username.toLowerCase();
        email = email.toLowerCase();
        logger.info("Intentando registrar usuario: " + username);

        User existingUser = dao.getUser(username);
        if (existingUser != null) {
            logger.error("Error al registrar: el usuario '" + username + "' ya existe en la base de datos.");
            throw new UserAlreadyExistsException("El usuario '" + username + "' ya existe");
        }

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12)); // factor 12
        logger.info("Contraseña hasheada con BCrypt para usuario: " + username);

        User u = new User(username, hashedPassword, email);
        dao.addUser(u);

        logger.info("Usuario registrado correctamente: " + username);
        return u;
    }

    @Override
    public GameObject addNewObjeto(String nombre, String descripcion, Objects tipo, int precio) {
        ValidationUtils.validateNotEmpty(nombre, "nombre");
        ValidationUtils.validatePositive(precio, "precio");

        if (registred_objects.containsKey(nombre)) {
            logger.error("Objeto ya existe: " + nombre);
            throw new IllegalArgumentException("Ya existe un objeto con ese nombre: " + nombre);
        }

        logger.info("Creando nuevo objeto: " + nombre);
        GameObject o = new GameObject(nombre, descripcion, tipo, precio);

        try {
            objectDAO.addObject(o);

            this.objects.add(o);
            this.registred_objects.put(nombre, o);
            this.objectsById.put(o.getId(), o);

            logger.info("Objeto creado correctamente: " + nombre + " (ID: " + o.getId() + ")");
            return o;

        } catch (Exception e) {
            logger.error("Error al crear objeto en BD: " + nombre, e);
            throw new RuntimeException("Error al crear objeto", e);
        }
    }

    @Override
    public List<UserGameObject> getListObjects(String username) throws UserNotFoundException {
        ValidationUtils.validateNotEmpty(username, "username");

        logger.info("Obtener todos los objetos del usuario  " + username);
        User u = dao.getUser(username);

        if (u == null) {
            logger.error("Usuario no encontrado: " + username);
            throw new UserNotFoundException("Usuario no encontrado: " + username);
        }

        List<UserGameObject> list = dao.getObjectsbyUser(u);
        return list != null ? list : new java.util.ArrayList<>();
    }

    @Override
    public User addObjectToUser(String username, String objectId)
            throws UserNotFoundException, ObjectNotFoundException {
        ValidationUtils.validateNotEmpty(username, "username");
        ValidationUtils.validateNotEmpty(objectId, "objectId");

        User u = dao.getUser(username);
        if (u == null) {
            logger.error("Usuario no encontrado: " + username);
            throw new UserNotFoundException("Usuario no encontrado: " + username);
        }

        GameObject o = this.getStoreObject(objectId);
        if (o == null) {
            logger.error("Objeto no encontrado: " + objectId);
            throw new ObjectNotFoundException("Objeto no encontrado: " + objectId);
        }

        dao.buyItem(u, o);
        logger.info("Objeto añadido: " + objectId + " a usuario " + username);
        return u;
    }

    public User purchaseObject(String username, String objectId)
            throws UserNotFoundException, ObjectNotFoundException, InsufficientFundsException {
        ValidationUtils.validateNotEmpty(username, "username");
        ValidationUtils.validateNotEmpty(objectId, "objectId");

        logger.info("Iniciando transacción de compra: objeto " + objectId + " para usuario " + username);

        Session session = null;
        try {
            // Abrir sesión única para toda la transacción
            session = edu.upc.dsa.orm.FactorySession.openSession();
            session.beginTransaction();

            User u = dao.getUser(username);
            if (u == null) {
                throw new UserNotFoundException("Usuario no encontrado: " + username);
            }

            GameObject o = getStoreObject(objectId);
            if (o == null) {
                throw new ObjectNotFoundException("Objeto no encontrado: " + objectId);
            }

            if (u.getMonedas() < o.getPrecio()) {
                logger.warn("Saldo insuficiente para usuario " + username);
                throw new InsufficientFundsException("Saldo insuficiente");
            }

            int nuevoSaldo = u.getMonedas() - o.getPrecio();
            u.setMonedas(nuevoSaldo);
            dao.updateUser(session, u);

            dao.buyItem(session, u, o);

            session.commit();

            logger.info("Compra completada exitosamente. Nuevo saldo: " + nuevoSaldo);
            return u;

        } catch (UserNotFoundException | ObjectNotFoundException | InsufficientFundsException e) {
            // Rollback en errores
            if (session != null) {
                session.rollback();
                logger.warn("Transacción revertida: " + e.getMessage());
            }
            throw e;

        } catch (Exception e) {
            // Rollback en errores inesperados
            if (session != null) {
                session.rollback();
                logger.error("Transacción revertida por error inesperado", e);
            }
            throw new RuntimeException("Error al procesar compra", e);

        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public String getObjectId(String objectName) throws ObjectNotFoundException {
        ValidationUtils.validateNotEmpty(objectName, "objectName");

        logger.info("Obtener el objeto id de " + objectName);
        GameObject o = this.registred_objects.get(objectName);

        if (o == null) {
            logger.error("Objeto no encontrado: " + objectName);
            throw new ObjectNotFoundException("Objeto no encontrado: " + objectName);
        }

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
            if (session != null)
                session.close();
        }
    }

    @Override
    public void clear() {
        try (java.sql.Connection conn = edu.upc.dsa.orm.DBUtils.getConnection()) {
            java.sql.DatabaseMetaData meta = conn.getMetaData();
            logger.info("Limpiando DB: " + meta.getURL() + " con usuario " + meta.getUserName());

            // solo en testeo - ESTO BORRA TODA LA BASE DE DATOS
            try (java.sql.Statement st = conn.createStatement()) {
                st.executeUpdate("SET FOREIGN_KEY_CHECKS = 0;");
                int rowsUG = st.executeUpdate("DELETE FROM User_GameObject;");
                int rowsU = st.executeUpdate("DELETE FROM User;");
                int rowsG = st.executeUpdate("DELETE FROM GameObject;");
                st.executeUpdate("SET FOREIGN_KEY_CHECKS = 1;");

                logger.info("Tablas limpiadas. Filas borradas: User_GameObject=" + rowsUG + ", User=" + rowsU
                        + ", GameObject=" + rowsG);
            }

            this.registred_objects.clear();
            this.objectsById.clear();
            this.objects.clear();
            logger.info("Estado interno de GameManager limpiado");
        } catch (Exception e) {
            logger.error("Error al limpiar GameManager", e);
        }
    }

    public GameObject getStoreObject(String id) {
        if (id == null || id.trim().isEmpty()) {
            logger.warn("Búsqueda de objeto con ID nulo o vacío");
            return null;
        }

        logger.info("Buscando objeto en la lista de objetos: " + id);
        GameObject o = objectsById.get(id);

        if (o == null) {
            logger.error("Objeto " + id + " no encontrado en la lista de objetos");
        }

        return o;
    }

    @Override
    public List<GameObject> getAllStoreObjects() {
        logger.info("Obteniendo todos los objetos de la tienda");
        return this.objects;
    }

    // ==================== UNITY INTEGRATION METHODS ====================

    @Override
    public void addCoinsToUser(String username, int amount) throws UserNotFoundException {
        ValidationUtils.validateNotEmpty(username, "username");
        ValidationUtils.validatePositive(amount, "amount");

        username = username.toLowerCase();
        logger.info("Añadiendo " + amount + " monedas al usuario: " + username);

        User u = dao.getUser(username);
        if (u == null) {
            logger.error("Usuario no encontrado: " + username);
            throw new UserNotFoundException("Usuario no encontrado: " + username);
        }

        int newBalance = u.getMonedas() + amount;
        u.setMonedas(newBalance);
        dao.updateUser(u);

        logger.info("Monedas actualizadas para " + username + ". Nuevo saldo: " + newBalance);
    }

    @Override
    public void updateUserProgress(String username, Integer actFrag, Integer bestScore)
            throws UserNotFoundException {
        ValidationUtils.validateNotEmpty(username, "username");

        username = username.toLowerCase();
        logger.info("Actualizando progreso del usuario: " + username);

        User u = dao.getUser(username);
        if (u == null) {
            logger.error("Usuario no encontrado: " + username);
            throw new UserNotFoundException("Usuario no encontrado: " + username);
        }

        boolean updated = false;

        if (actFrag != null) {
            u.setActFrag(actFrag);
            logger.info("ActFrag actualizado a: " + actFrag + " para usuario " + username);
            updated = true;
        }

        if (bestScore != null && bestScore > u.getBestScore()) {
            u.setBestScore(bestScore);
            logger.info("BestScore actualizado a: " + bestScore + " para usuario " + username);
            updated = true;
        } else if (bestScore != null) {
            logger.info("BestScore no actualizado (" + bestScore + " no es mayor que el actual: " + u.getBestScore() + ")");
        }

        if (updated) {
            dao.updateUser(u);
            logger.info("Progreso guardado correctamente para " + username);
        } else {
            logger.info("No hubo cambios en el progreso para " + username);
        }
    }
}