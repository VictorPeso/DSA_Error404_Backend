package edu.upc.dsa.orm.dao;

import edu.upc.dsa.models.User;
import edu.upc.dsa.models.GameObject;
import edu.upc.dsa.models.UserGameObject;
import edu.upc.dsa.orm.FactorySession;
import edu.upc.dsa.orm.Session;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserDAOImpl implements UserDAO {
    final static Logger logger = Logger.getLogger(UserDAOImpl.class);

    public String addUser(User user) {
        Session session = null;
        try {
            session = FactorySession.openSession();
            session.save(user);
            logger.info("Usuario " + user.getUsername() + " registrado correctamente");
        } catch (Exception e) {
            logger.error("No se ha podido registrar el usuario " + user.getUsername(), e);
            throw new RuntimeException("Error al registrar usuario", e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return user.getUsername();
    }

    public User getUser(String username) {
        Session session = null;
        User user = null;
        try {
            session = FactorySession.openSession();
            user = (User) session.get(User.class, username);
            if (user == null) {
                logger.info("No se ha encontrado el usuario " + username);
            }
        } catch (Exception e) {
            logger.error("Error al buscar el usuario " + username, e);
        } finally {
            if (session != null) {
                session.close();
            }
        }

        return user;
    }

    public void updateUser(User user) {
        Session session = null;
        try {
            session = FactorySession.openSession();
            updateUser(session, user);
        } catch (Exception e) {
            logger.info("No se ha podido actualizar el usuario " + user.getUsername());
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    // session externa para transacciones
    public void updateUser(Session session, User user) {
        session.update(user);
    }

    public void deleteUser(User user) {
        Session session = null;
        try {
            session = FactorySession.openSession();
            session.delete(user);
        } catch (Exception e) {
            logger.info("No se ha podido eliminar el usuario " + user.getUsername());
        } finally {
            session.close();
        }

    }

    public List<UserGameObject> getObjectsbyUser(User user) {
        Session session = null;
        List<UserGameObject> objectList = new ArrayList<>();
        Connection conn = null;
        try {
            session = FactorySession.openSession();
            conn = session.getConnection();

            String query = "SELECT GameObject.*, User_GameObject.cantidad " +
                    "FROM GameObject, User_GameObject " +
                    "WHERE User_GameObject.username = ? " +
                    "AND GameObject.id = User_GameObject.id";

            PreparedStatement pstm = conn.prepareStatement(query);
            pstm.setString(1, user.getUsername());
            ResultSet rs = pstm.executeQuery();

            while (rs.next()) {
                GameObject obj = new GameObject();
                obj.setId(rs.getString("id"));
                obj.setNombre(rs.getString("nombre"));
                obj.setDescripcion(rs.getString("descripcion"));
                obj.setTipo(rs.getString("tipo"));
                obj.setPrecio(rs.getInt("precio"));

                Integer cantidad = rs.getInt("cantidad");

                UserGameObject ugo = new UserGameObject(obj, cantidad);
                objectList.add(ugo);
            }

        } catch (Exception e) {
            logger.error("Error al obtener objetos del usuario " + user.getUsername(), e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return objectList;
    }

    public String buyItem(User user, GameObject obj) {
        Session session = null;
        try {
            session = FactorySession.openSession();
            return buyItem(session, user, obj);
        } catch (Exception e) {
            logger.error("Error al comprar objeto " + obj.getId(), e);
            throw new RuntimeException("Error al comprar objeto", e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    // session externa para transacciones
    public String buyItem(Session session, User user, GameObject obj) {
        try {
            // Verificar si el usuario ya tiene el objeto
            if (userHasObject(session, user.getUsername(), obj.getId())) {
                // Incrementar cantidad
                incrementObjectQuantity(session, user.getUsername(), obj.getId());
                logger.info("Cantidad incrementada para objeto " + obj.getId());
            } else {
                // Insertar nuevo
                session.save_M2N(user, obj, "User_GameObject");
                logger.info("Objeto " + obj.getId() + " comprado por primera vez");
            }
            return user.getUsername();
        } catch (Exception e) {
            logger.error("Error al comprar objeto " + obj.getId(), e);
            throw new RuntimeException("Error al comprar objeto", e);
        }
    }

    private boolean userHasObject(Session session, String username, String objectId) {
        try {
            return session.exists_M2N("User_GameObject", "username", username, "id", objectId);
        } catch (Exception e) {
            logger.error("Error al verificar objeto", e);
        }
        return false;
    }

    private void incrementObjectQuantity(Session session, String username, String objectId) {
        try {
            session.updateQuantity_M2N("User_GameObject", "username", username, "id", objectId);
        } catch (Exception e) {
            logger.error("Error al incrementar cantidad", e);
            throw new RuntimeException("Error al incrementar cantidad", e);
        }
    }
}
