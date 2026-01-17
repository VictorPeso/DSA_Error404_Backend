package edu.upc.dsa.services;

// 1. LOS IMPORTS SIEMPRE VAN ARRIBA, FUERA DE LA CLASE
import edu.upc.dsa.models.dto.Team;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

// 2. LA ANOTACIÓN DEL PATH
@Path("/teams")
public class TeamService { // 3. AQUÍ EMPIEZA LA CLASE

    @GET
    @Path("/ranking")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Team> getRanking() {
        List<Team> teams = new ArrayList<>();

        teams.add(new Team("Porxinos", "https://upload.wikimedia.org/wikipedia/commons/thumb/6/6b/Icecat1-300x300.svg/1200px-Icecat1-300x300.svg.png", 250));
        teams.add(new Team("Saiyans", "https://cdn.pixabay.com/photo/2017/07/11/15/51/kermit-2493979_1280.png", 200));
        teams.add(new Team("Jijantes", "https://upload.wikimedia.org/wikipedia/commons/9/99/Sample_User_Icon.png", 150));

        return teams;
    }
}
