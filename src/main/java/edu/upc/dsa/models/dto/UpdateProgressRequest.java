package edu.upc.dsa.models.dto;

public class UpdateProgressRequest {
    private String username;
    private Integer actFrag;
    private Integer bestScore;

    public UpdateProgressRequest() {
    }

    public UpdateProgressRequest(String username, Integer actFrag, Integer bestScore) {
        this.username = username;
        this.actFrag = actFrag;
        this.bestScore = bestScore;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getActFrag() {
        return actFrag;
    }

    public void setActFrag(Integer actFrag) {
        this.actFrag = actFrag;
    }

    public Integer getBestScore() {
        return bestScore;
    }

    public void setBestScore(Integer bestScore) {
        this.bestScore = bestScore;
    }
}
