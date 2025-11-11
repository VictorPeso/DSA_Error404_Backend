package edu.upc.dsa;

import edu.upc.dsa.models.User;


public interface GameManager {

    public User LogIn(String username, String password) throws Exception;
    public User Register(String username, String password);

}
