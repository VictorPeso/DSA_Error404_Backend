package edu.upc.dsa.models;

import java.util.ArrayList;
import java.util.List;

public class User {

    String username;
    String password;
    int ActFrag;
    int BestScore;
    List<GameObject> myobjects;

    public User() {}

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.ActFrag = 0;
        this.BestScore = 0;
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

    public List<GameObject> getMyobjects() {return myobjects;}

    public void setMyobjects(GameObject newobj) {this.myobjects.add(newobj);}

//    @Override
//    public String toString() {
//        return "Track [id="+id+", title=" + title + ", singer=" + singer +"]";
//    }



}