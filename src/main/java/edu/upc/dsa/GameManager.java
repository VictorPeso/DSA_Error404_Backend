package edu.upc.dsa;

import edu.upc.dsa.models.GameObject;
import edu.upc.dsa.models.Objects;
import edu.upc.dsa.models.User;

import java.util.List;

public interface GameManager {

    public User LogIn(String username, String password) throws Exception;

    public User Register(String username, String password, String email) throws Exception;

    public Object addNewObjeto(String nombre, String descripcion, Objects tipo, int precio);

    public User purchaseObject(String username, String objectId) throws Exception;

    // Objetos
    public List<GameObject> getListObjects(String username);

    public User addObjectToUser(String username, String objectId);

    public String getObjectId(String objectName);

    public List<GameObject> getAllStoreObjects();

    // JUnit
    public int getNumberOfUsersRegistered();

    public User getUser(String username);
}
