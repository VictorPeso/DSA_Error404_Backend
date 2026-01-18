package edu.upc.dsa;

import edu.upc.dsa.models.*;
import edu.upc.dsa.models.Objects;
import edu.upc.dsa.orm.dao.UserDAOImpl;
import edu.upc.dsa.orm.dao.GameObjectDAOImpl;
import edu.upc.dsa.orm.Session;
import edu.upc.dsa.exceptions.*;
import edu.upc.dsa.util.ValidationUtils;
import org.apache.log4j.Logger;
import org.mindrot.jbcrypt.BCrypt;

import java.util.*;

public class GameManagerImpl implements GameManager {

    private static class SingletonHolder {
        private static final GameManager INSTANCE = new GameManagerImpl();
    }

    protected Map<String, GameObject> registred_objects;
    protected Map<String, GameObject> objectsById;
    protected List<GameObject> objects;
    protected UserDAOImpl dao;
    protected GameObjectDAOImpl objectDAO;
    protected List<Evento> eventos;
    protected Map<String, Set<String>> registrosEvento;
    final static Logger logger = Logger.getLogger(GameManagerImpl.class);

    private GameManagerImpl() {
        this.registred_objects = new java.util.concurrent.ConcurrentHashMap<>();
        this.objectsById = new java.util.concurrent.ConcurrentHashMap<>();
        this.objects = new java.util.concurrent.CopyOnWriteArrayList<>();
        this.dao = new UserDAOImpl();
        this.objectDAO = new GameObjectDAOImpl();
        this.eventos = new LinkedList<>();
        this.registrosEvento = new HashMap<>();

        eventos.add(new Evento("1", "Evento 1", "Torneo individual", "16-12-2025", "20-12-2025", ""));
        eventos.add(new Evento("2", "Evento 2", "Torneo por equipos", "16-12-2025", "20-12-2025", ""));

        loadObjectsFromDatabase();
    }

    public static GameManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private void loadObjectsFromDatabase() {
        List<GameObject> dbObjects = objectDAO.getAllObjects();
        if (dbObjects != null) {
            for (GameObject obj : dbObjects) {
                this.objects.add(obj);
                this.registred_objects.put(obj.getNombre(), obj);
                this.objectsById.put(obj.getId(), obj);
            }
        }
    }

    @Override
    public User LogIn(String username, String password) throws FailedLoginException {
        ValidationUtils.validateNotEmpty(username, "username");
        ValidationUtils.validateNotEmpty(password, "password");

        username = username.toLowerCase();
        User u = dao.getUser(username);

        if (u == null || !BCrypt.checkpw(password, u.getPassword())) {
            throw new FailedLoginException("Usuario o contraseña incorrectas");
        }

        return u;
    }

    @Override
    public User Register(String username, String password, String email) throws UserAlreadyExistsException {
        ValidationUtils.validateUsername(username);
        ValidationUtils.validatePassword(password);
        ValidationUtils.validateEmail(email);

        username = username.toLowerCase();
        email = email.toLowerCase();

        User existingUser = dao.getUser(username);
        if (existingUser != null) {
            throw new UserAlreadyExistsException("El usuario '" + username + "' ya existe");
        }

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));
        User u = new User(username, hashedPassword, email);
        dao.addUser(u);
        return u;
    }

    @Override
    public GameObject addNewObjeto(String nombre, String descripcion, Objects tipo, int precio) {
        ValidationUtils.validateNotEmpty(nombre, "nombre");
        ValidationUtils.validatePositive(precio, "precio");

        if (registred_objects.containsKey(nombre)) {
            throw new IllegalArgumentException("Ya existe un objeto con ese nombre: " + nombre);
        }

        GameObject o = new GameObject(nombre, descripcion, tipo, precio);
        objectDAO.addObject(o);

        this.objects.add(o);
        this.registred_objects.put(nombre, o);
        this.objectsById.put(o.getId(), o);

        return o;
    }

    @Override
    public List<UserGameObject> getListObjects(String username) throws UserNotFoundException {
        ValidationUtils.validateNotEmpty(username, "username");

        User u = dao.getUser(username);
        if (u == null) {
            throw new UserNotFoundException("Usuario no encontrado: " + username);
        }

        List<UserGameObject> list = dao.getObjectsbyUser(u);
        return list != null ? list : new ArrayList<>();
    }

    @Override
    public User addObjectToUser(String username, String objectId)
            throws UserNotFoundException, ObjectNotFoundException {

        ValidationUtils.validateNotEmpty(username, "username");
        ValidationUtils.validateNotEmpty(objectId, "objectId");

        User u = dao.getUser(username);
        if (u == null) {
            throw new UserNotFoundException("Usuario no encontrado: " + username);
        }

        GameObject o = getStoreObject(objectId);
        if (o == null) {
            throw new ObjectNotFoundException("Objeto no encontrado: " + objectId);
        }

        dao.buyItem(u, o);
        return u;
    }

    @Override
    public User purchaseObject(String username, String objectId)
            throws UserNotFoundException, ObjectNotFoundException, InsufficientFundsException {

        ValidationUtils.validateNotEmpty(username, "username");
        ValidationUtils.validateNotEmpty(objectId, "objectId");

        Session session = null;
        try {
            session = edu.upc.dsa.orm.FactorySession.openSession();
            session.beginTransaction();

            User u = dao.getUser(username);
            if (u == null)
                throw new UserNotFoundException("Usuario no encontrado: " + username);

            GameObject o = getStoreObject(objectId);
            if (o == null)
                throw new ObjectNotFoundException("Objeto no encontrado: " + objectId);

            if (u.getMonedas() < o.getPrecio())
                throw new InsufficientFundsException("Saldo insuficiente");

            u.setMonedas(u.getMonedas() - o.getPrecio());
            dao.updateUser(session, u);
            dao.buyItem(session, u, o);

            session.commit();
            return u;

        } catch (UserNotFoundException | ObjectNotFoundException | InsufficientFundsException e) {
            if (session != null)
                session.rollback();
            throw e;
        } catch (Exception e) {
            if (session != null)
                session.rollback();
            throw new RuntimeException("Error al procesar compra", e);
        } finally {
            if (session != null)
                session.close();
        }
    }

    @Override
    public String getObjectId(String objectName) throws ObjectNotFoundException {
        ValidationUtils.validateNotEmpty(objectName, "objectName");

        GameObject o = this.registred_objects.get(objectName);
        if (o == null) {
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
        } finally {
            if (session != null)
                session.close();
        }
    }

    @Override
    public void clear() {
        try (java.sql.Connection conn = edu.upc.dsa.orm.DBUtils.getConnection()) {
            try (java.sql.Statement st = conn.createStatement()) {
                st.executeUpdate("SET FOREIGN_KEY_CHECKS = 0;");
                st.executeUpdate("DELETE FROM User_GameObject;");
                st.executeUpdate("DELETE FROM User;");
                st.executeUpdate("DELETE FROM GameObject;");
                st.executeUpdate("SET FOREIGN_KEY_CHECKS = 1;");
            }
            this.registred_objects.clear();
            this.objectsById.clear();
            this.objects.clear();
        } catch (Exception e) {
        }
    }

    @Override
    public List<Evento> getEventos() {
        return eventos;
    }

    @Override
    public boolean registerEvento(String userId, String eventoId) {
        if (userId == null || eventoId == null)
            return false;

        Set<String> inscritos = registrosEvento.get(eventoId);
        if (inscritos == null) {
            inscritos = new HashSet<>();
            registrosEvento.put(eventoId, inscritos);
        }
        return inscritos.add(userId);
    }

    public GameObject getStoreObject(String id) {
        if (id == null || id.trim().isEmpty())
            return null;
        return objectsById.get(id);
    }

    @Override
    public List<GameObject> getAllStoreObjects() {
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
            logger.info(
                    "BestScore no actualizado (" + bestScore + " no es mayor que el actual: " + u.getBestScore() + ")");
        }

        if (updated) {
            dao.updateUser(u);
            logger.info("Progreso guardado correctamente para " + username);
        } else {
            logger.info("No hubo cambios en el progreso para " + username);
        }
    }

    @Override
    public void updateObjectQuantity(String username, String objectId, int newQuantity)
            throws UserNotFoundException, ObjectNotFoundException {
        ValidationUtils.validateNotEmpty(username, "username");
        ValidationUtils.validateNotEmpty(objectId, "objectId");

        username = username.toLowerCase();
        logger.info(
                "Actualizando cantidad de objeto '" + objectId + "' a " + newQuantity + " para usuario: " + username);

        User u = dao.getUser(username);
        if (u == null) {
            logger.error("Usuario no encontrado: " + username);
            throw new UserNotFoundException("Usuario no encontrado: " + username);
        }

        GameObject obj = objectsById.get(objectId);
        if (obj == null) {
            logger.error("Objeto no encontrado: " + objectId);
            throw new ObjectNotFoundException("Objeto no encontrado: " + objectId);
        }

        if (newQuantity <= 0) {
            dao.removeObjectFromUser(username, objectId);
            logger.info("Objeto '" + objectId + "' eliminado del inventario de " + username);
        } else {
            dao.updateObjectQuantity(username, objectId, newQuantity);
            logger.info("Cantidad de objeto '" + objectId + "' actualizada a " + newQuantity + " para " + username);
        }
    }
}
