package edu.upc.dsa;

import edu.upc.dsa.models.User;
import edu.upc.dsa.models.GameObject;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GameManagerImpl implements GameManager {
    private static GameManager instance;
    //protected List<User> registred_users;
    protected Map<String, User> registred_users;
    protected List<GameObject> objects;
    final static Logger logger = Logger.getLogger(GameManagerImpl.class);

    private GameManagerImpl() {
        this.registred_users = new HashMap<>();
        this.objects = new LinkedList<>();
    }

    public static GameManager getInstance() {
        if (instance==null) instance = new GameManagerImpl();
        return instance;
    }


    @Override
    public User LogIn(String username, String password) throws Exception {
        User u = registred_users.get(username);
        if (u == null || !u.getPassword().equals(password)) {
            throw new Exception("Usuario o contraseña incorrectas");
        }
        return u;
    }

    @Override
    public User Register(String username, String password) {
        User u = new User(username, password);
        this.registred_users.put(username, u);
        return u;
    }

    @Override
    public GameObject addNewObjeto(String nombre, String descripcion) {
        GameObject o = new GameObject(nombre, descripcion);
        this.objects.add(o);
        return o;
    }

    @Override
    public List<GameObject> getListObjects(String username) {
        User u = registred_users.get(username);
        List<GameObject> list = u.getMyobjects();
        return list;
    }

    @Override
    public User setObject() {

        for (int i = 0; i<this.registred_users.size(); i++) {
            User u =  this.registred_users.get(this.registred_users.keySet().toArray()[i]);
            //añadir todos los objetos de GameObject
            for(GameObject o : objects){
                u.setMyobjects(o);
            }
        }


        return null;
    }


}
