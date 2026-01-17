package edu.upc.dsa.services;

import edu.upc.dsa.models.dto.Team;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
public class TeamService {


    @Path("/teams")
    public class TeamsService {

        @GET
        @Path("/ranking")
        @Produces(MediaType.APPLICATION_JSON)
        public List<Team> getRanking() {
            List<Team> teams = new ArrayList<>();
            // Añadimos datos "dummy" (falsos) como pide el enunciado
            teams.add(new Team("Porxinos", "https://upload.wikimedia.org/wikipedia/commons/thumb/6/6b/Icecat1-300x300.svg/1200px-Icecat1-300x300.svg.png", 250));
            teams.add(new Team("Saiyans", "https://cdn.pixabay.com/photo/2017/07/11/15/51/kermit-2493979_1280.png", 200));
            teams.add(new Team("Jijantes", "https://upload.wikimedia.org/wikipedia/commons/9/99/Sample_User_Icon.png", 150));

            return teams;
        }
    }
}
