package edu.upc.dsa;

import edu.upc.dsa.models.GameObject;
import edu.upc.dsa.models.Objects;
import edu.upc.dsa.models.User;

import java.util.List;


public interface GameManager {

    public User LogIn(String username, String password) throws Exception;
    public User Register(String username, String password) throws Exception;
    public Object addNewObjeto(String nombre, String descripcion, Objects tipo);

    //Prueba: consultar lista de objetos de un usuario
    public List<GameObject> getListObjects(String username);
    public User addObjectToUser(String username, String objectId);


}
