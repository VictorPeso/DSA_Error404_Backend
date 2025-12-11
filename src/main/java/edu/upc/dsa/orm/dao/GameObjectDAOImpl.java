package edu.upc.dsa.orm.dao;

import edu.upc.dsa.models.GameObject;
import edu.upc.dsa.orm.FactorySession;
import edu.upc.dsa.orm.Session;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;

public class GameObjectDAOImpl {
    final static Logger logger = Logger.getLogger(GameObjectDAOImpl.class);

    public List<GameObject> getAllObjects() {
        Session session = null;
        List<GameObject> objectList = null;
        try {
            session = FactorySession.openSession();
            @SuppressWarnings("unchecked")
            List<GameObject> result = (List<GameObject>) (List<?>) session.findAll(GameObject.class, new HashMap<>());
            objectList = result;
        } catch (Exception e) {
            logger.error("Error al obtener todos los objetos", e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return objectList;
    }

    public GameObject getObjectById(String id) {
        Session session = null;
        GameObject object = null;
        try {
            session = FactorySession.openSession();
            object = (GameObject) session.get(GameObject.class, id);
            if (object == null) {
                logger.info("No se ha encontrado el objeto " + id);
            }
        } catch (Exception e) {
            logger.error("Error al buscar el objeto " + id, e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return object;
    }

    public String addObject(GameObject object) {
        Session session = null;
        try {
            session = FactorySession.openSession();
            session.save(object);
            logger.info("Objeto " + object.getNombre() + " guardado correctamente");
        } catch (Exception e) {
            logger.error("No se ha podido guardar el objeto " + object.getNombre(), e);
            throw new RuntimeException("Error al guardar objeto", e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return object.getId();
    }
}
