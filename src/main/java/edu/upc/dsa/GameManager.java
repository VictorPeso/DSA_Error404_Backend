package edu.upc.dsa;

import edu.upc.dsa.exceptions.*;
import edu.upc.dsa.models.*;

import java.util.List;

public interface GameManager {

    User LogIn(String username, String password) throws FailedLoginException;

    User Register(String username, String password, String email) throws UserAlreadyExistsException;

    GameObject addNewObjeto(String nombre, String descripcion, Objects tipo, int precio);

    User purchaseObject(String username, String objectId) throws UserNotFoundException, ObjectNotFoundException, InsufficientFundsException;

    List<UserGameObject> getListObjects(String username) throws UserNotFoundException;

    User addObjectToUser(String username, String objectId) throws UserNotFoundException, ObjectNotFoundException;

    String getObjectId(String objectName) throws ObjectNotFoundException;

    List<GameObject> getAllStoreObjects();

    int getNumberOfUsersRegistered();

    User getUser(String username);

    void clear();

    List<Evento> getEventos();

    boolean registerEvento(String userId, String eventoId);
}
