package edu.upc.dsa.services;

import edu.upc.dsa.GameManager;
import edu.upc.dsa.GameManagerImpl;
import edu.upc.dsa.models.GameObject;
import edu.upc.dsa.models.User;

import edu.upc.dsa.models.dto.Credentials;
import edu.upc.dsa.models.dto.AddObject;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

import static edu.upc.dsa.models.Objects.*;

@Api(value = "/game", description = "Game Promotion API for EETAC")
@Path("/game")
public class GameService {

    private GameManager gm;

    public GameService() {
        this.gm = GameManagerImpl.getInstance();

        // if (this.gm.shopSize() == 0) {
        gm.addNewObjeto("Espada", "Corta dragones", ESPADA);
        gm.addNewObjeto("Escudo", "Resistente al fuego", ESCUDO);
        gm.addNewObjeto("Pocion", "Recupera energía", POCION);
        // }
    }

    // ------------------- USUARIOS -------------------

    @POST
    @Path("/users/register")
    @ApiOperation(value = "Registrar nuevo usuario")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Usuario creado", response = User.class),
            @ApiResponse(code = 409, message = "Usuario ya existe"),
            @ApiResponse(code = 400, message = "Faltan datos")
    })
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerUser(Credentials credentials) {

        if (credentials.getNombre() == null || credentials.getPassword() == null) {
            return Response.status(400).entity("Faltan nombre o password").build();
        }

        try {
            User u = gm.Register(credentials.getNombre(), credentials.getPassword());
            return Response.status(Response.Status.CREATED).entity(u).build();
        } catch (Exception e) {
            return Response.status(Response.Status.CONFLICT).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/users/login")
    @ApiOperation(value = "Login de usuario")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Login correcto", response = User.class),
            @ApiResponse(code = 401, message = "Credenciales incorrectas")
    })
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(Credentials credentials) {
        if (credentials.getNombre() == null || credentials.getPassword() == null) {
            return Response.status(400).entity("Faltan nombre o password").build();
        }

        try {
            User u = gm.LogIn(credentials.getNombre(), credentials.getPassword());
            return Response.ok(u).build();
        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
        }
    }

    // ------------------- TIENDA -------------------

    @GET
    @ApiOperation(value = "Obtener lista de objetos de un usuario")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful", response = GameObject.class, responseContainer="List"),
            @ApiResponse(code = 404, message = "Usuario no encontrado")
    })
    @Path("/users/objects/list")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserObjects(@QueryParam("nombre") String nombre) {

        List<GameObject> objects = this.gm.getListObjects(nombre);

        if (objects == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Usuario no encontrado").build();
        }

        GenericEntity<List<GameObject>> entity = new GenericEntity<List<GameObject>>(objects) {};
        return Response.status(Response.Status.OK).entity(entity).build();
    }

    @POST
    @ApiOperation(value = "Añadir un objeto a un usuario")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Objeto añadido", response = User.class),
            @ApiResponse(code = 404, message = "Usuario u Objeto no encontrado"),
            @ApiResponse(code = 400, message = "Faltan datos")
    })
    @Path("/users/objects/add")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addObjectToUser(AddObject request) {

        if (request.getNombre() == null || request.getObjectId() == null) {
            return Response.status(400).entity("Falta 'nombre' o 'id del objeto'").build();
        }

        User updatedUser = this.gm.addObjectToUser(request.getNombre(), request.getObjectId());

        if (updatedUser == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Usuario u Objeto no encontrado").build();
        }

        return Response.status(Response.Status.OK).entity(updatedUser).build();
    }
}