package edu.upc.dsa;

import edu.upc.dsa.exceptions.*;
import edu.upc.dsa.models.GameObject;
import edu.upc.dsa.models.Objects;
import edu.upc.dsa.models.User;
import edu.upc.dsa.models.UserGameObject;

import java.util.List;

public interface GameManager {

    public User LogIn(String username, String password) throws FailedLoginException;

    public User Register(String username, String password, String email) throws UserAlreadyExistsException;

    public GameObject addNewObjeto(String nombre, String descripcion, Objects tipo, int precio);

    public User purchaseObject(String username, String objectId) throws UserNotFoundException, ObjectNotFoundException, InsufficientFundsException;

    // Objetos
    public List<UserGameObject> getListObjects(String username) throws UserNotFoundException;

    public User addObjectToUser(String username, String objectId) throws UserNotFoundException, ObjectNotFoundException;

    public String getObjectId(String objectName) throws ObjectNotFoundException;

    public List<GameObject> getAllStoreObjects();

    // JUnit
    public int getNumberOfUsersRegistered();

    public User getUser(String username);

    public void clear();

    // Unity integration methods
    public void addCoinsToUser(String username, int amount) throws UserNotFoundException;

    public void updateUserProgress(String username, Integer actFrag, Integer bestScore) throws UserNotFoundException;
}
