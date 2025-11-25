package edu.upc.dsa.models;

import java.util.ArrayList;
import java.util.List;

public class User {

    private String username;
    private String password;
    private String email;
    private int ActFrag;
    private int BestScore;
    private List<GameObject> myobjects;
    private int vidaInicial;
    private int monedas;

    public User() {
    }

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.ActFrag = 0;
        this.BestScore = 0;
        this.vidaInicial = 100;
        this.monedas = 100;
        myobjects = new ArrayList<GameObject>();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getActFrag() {
        return ActFrag;
    }

    public void setActFrag(int actFrag) {
        ActFrag = actFrag;
    }

    public int getBestScore() {
        return BestScore;
    }

    public void setBestScore(int bestScore) {
        BestScore = bestScore;
    }

    public List<GameObject> getMyobjects() {
        return this.myobjects;
    }

    public void setMyobjects(GameObject newobj) {
        this.myobjects.add(newobj);
    }

    public int getVidaInicial() {
        return vidaInicial;
    }

    public void setVidaInicial(int vidaInicial) {
        this.vidaInicial = vidaInicial;
    }

    public int getMonedas() {
        return monedas;
    }

    public void setMonedas(int monedas) {
        this.monedas = monedas;
    }

    public boolean CheckObject(GameObject newobj) {
        for (GameObject o : this.myobjects) {
            if (o.equals(newobj)) {
                return true;
            }
        }
        return false;
    }
}