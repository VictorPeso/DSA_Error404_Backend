package edu.upc.dsa.services;

import edu.upc.dsa.GameManager;
import edu.upc.dsa.GameManagerImpl;
import edu.upc.dsa.models.GameObject;
import edu.upc.dsa.models.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Api(value = "/game", description = "Game Promotion API for EETAC")
@Path("/game")
public class GameService {

    private GameManager gm;

    public GameService() {
        this.gm = GameManagerImpl.getInstance();

        // Datos de ejemplo iniciales
        gm.addNewObjeto("Espada", "Corta dragones");
        gm.addNewObjeto("Escudo", "Resistente al fuego");
        gm.addNewObjeto("Poción", "Recupera energía");

    }

    // ------------------- USUARIOS -------------------

    @POST
    @Path("/users/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Registrar nuevo usuario")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Usuario creado", response = User.class),
            @ApiResponse(code = 409, message = "Usuario ya existe")
    })
    public Response registerUser(@QueryParam("nombre") String nombre,
                                 @QueryParam("password") String password) {

        User u = gm.Register(nombre, password);
        GenericEntity<User> entity = new GenericEntity<User>(u) {};
        gm.setObject();
        return Response.status(Response.Status.CREATED).entity(entity).build();


    }

    @POST
    @Path("/users/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Login de usuario")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Login correcto", response = User.class),
            @ApiResponse(code = 401, message = "Credenciales incorrectas")
    })
    public Response login(@QueryParam("nombre") String nombre,
                          @QueryParam("password") String password) {
        try {
            User u = gm.LogIn(nombre, password);
            return Response.ok(u).build();
        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
        }
    }

    @GET
    @ApiOperation(value = "get all objects of a user")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successful", response = Object.class, responseContainer="List"),
    })
    @Path("users/oblectList")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTracks(@QueryParam("nombre") String nombre) {
        List<GameObject> o = this.gm.getListObjects(nombre);
        GenericEntity<List<GameObject>> entity = new GenericEntity<List<GameObject>>(o) {};
        return Response.status(201).entity(entity).build()  ;

    }
}

