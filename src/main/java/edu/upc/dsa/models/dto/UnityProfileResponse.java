package edu.upc.dsa.models.dto;

import edu.upc.dsa.models.User;
import java.util.List;

public class UnityProfileResponse {
    private User user;
    private List<GameObjectDTO> objects;

    public UnityProfileResponse() {
    }

    public UnityProfileResponse(User user, List<GameObjectDTO> objects) {
        this.user = user;
        this.objects = objects;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<GameObjectDTO> getObjects() {
        return objects;
    }

    public void setObjects(List<GameObjectDTO> objects) {
        this.objects = objects;
    }
}
