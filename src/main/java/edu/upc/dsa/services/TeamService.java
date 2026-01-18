package edu.upc.dsa.services;

import edu.upc.dsa.models.dto.Team;
import edu.upc.dsa.models.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/teams")
public class TeamService {

    private List<Team> teams;
    // Static para mantener memoria tras peticiones
    private static Map<String, String> userTeamMap = new HashMap<>();

    public TeamService() {
        this.teams = new ArrayList<>();
        this.teams.add(new Team("Porxinos", "https://upload.wikimedia.org/wikipedia/commons/thumb/6/6b/Icecat1-300x300.svg/1200px-Icecat1-300x300.svg.png", 250));
        this.teams.add(new Team("Saiyans", "https://cdn.pixabay.com/photo/2017/07/11/15/51/kermit-2493979_1280.png", 200));
        this.teams.add(new Team("Jijantes", "https://upload.wikimedia.org/wikipedia/commons/9/99/Sample_User_Icon.png", 150));
    }

    @GET
    @Path("/ranking")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Team> getRanking() {
        return this.teams;
    }

    @PUT
    @Path("/join/{teamName}/{userName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response joinTeam(@PathParam("teamName") String teamName, @PathParam("userName") String userName) {

        Team targetTeam = null;
        for (Team t : this.teams) {
            // Ignoramos mayúsculas/minúsculas para evitar errores
            if (t.getName().equalsIgnoreCase(teamName)) {
                targetTeam = t;
                break;
            }
        }

        if (targetTeam == null) {
            return Response.status(404).build();
        }

        // Guardamos la relación usuario-equipo
        userTeamMap.put(userName, targetTeam.getName());
        return Response.status(200).entity(targetTeam).build();
    }

    @GET
    @Path("/user/{userName}/team")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMyTeamInfo(@PathParam("userName") String userName) {

        String teamName = userTeamMap.get(userName);

        // Si no está en el mapa, devolvemos 404
        if (teamName == null) {
            return Response.status(404).entity("Usuario no tiene equipo").build();
        }

        List<User> members = new ArrayList<>();

        for (Map.Entry<String, String> entry : userTeamMap.entrySet()) {
            if (entry.getValue().equals(teamName)) {
                // SOLUCIÓN DEFINITIVA:
                // Usamos el constructor con parámetros.
                // Al ver tu código User.java, este constructor pone automáticamente:
                // ActFrag=0, BestScore=0, vida=100, monedas=100.
                // Así es IMPOSIBLE que sea null.
                User u = new User(entry.getKey(), "hidden", "dummy@email.com");

                // Si quieres simular puntos distintos, usa setBestScore (que es un int en tu User)
                // u.setBestScore(150);

                members.add(u);
            }
        }

        TeamInfoResponse response = new TeamInfoResponse(teamName, members);
        return Response.status(200).entity(response).build();
    }

    // Clase para enviar la respuesta JSON compuesta
    public static class TeamInfoResponse {
        public String team;
        public List<User> members;

        public TeamInfoResponse() {}

        public TeamInfoResponse(String t, List<User> m) {
            this.team = t;
            this.members = m;
        }
    }
}