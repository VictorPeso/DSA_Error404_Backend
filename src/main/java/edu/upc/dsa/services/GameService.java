package edu.upc.dsa.services;

import edu.upc.dsa.GameManager;
import edu.upc.dsa.GameManagerImpl;
import edu.upc.dsa.exceptions.*;
import edu.upc.dsa.models.GameObject;
import edu.upc.dsa.models.User;
import edu.upc.dsa.models.UserGameObject;
import edu.upc.dsa.models.Evento;
import edu.upc.dsa.models.dto.Credentials;
import edu.upc.dsa.models.dto.AddObject;
import edu.upc.dsa.models.dto.RegisterCredentials;
import edu.upc.dsa.models.dto.UserDTO;
import edu.upc.dsa.models.dto.GameObjectDTO;
import edu.upc.dsa.models.dto.RegistroEventoRequest;
import edu.upc.dsa.models.dto.UserEventDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

import static edu.upc.dsa.models.Objects.*;

@Api(value = "/game", description = "Game Promotion API for EETAC")
@Path("/game")
public class GameService {

    private GameManager gm;

    public GameService() {
        this.gm = GameManagerImpl.getInstance();
    }

    @GET
    @Path("/events")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Obtener lista de eventos", response = Evento.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK")
    })
    public Response getEvents() {
        List<Evento> events = this.gm.getEventos();
        GenericEntity<List<Evento>> entity = new GenericEntity<List<Evento>>(events) {};
        return Response.status(Response.Status.OK).entity(entity).build();
    }

    @POST
    @Path("/events/{id}/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Inscribir usuario en un evento")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 400, message = "Faltan datos"),
            @ApiResponse(code = 409, message = "No se ha podido inscribir")
    })
    public Response registerEvento(@PathParam("id") String id, RegistroEventoRequest request) {
        if (request == null || request.getUserId() == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Falta userId").build();
        }
        boolean ok = this.gm.registerEvento(request.getUserId(), id);
        if (!ok) {
            return Response.status(Response.Status.CONFLICT).entity("No se ha podido inscribir").build();
        }
        return Response.status(Response.Status.OK).build();
    }

    @POST
    @Path("/users/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerUser(RegisterCredentials credentials) {
        if (credentials.getNombre() == null || credentials.getPassword() == null || credentials.getEmail() == null) {
            return Response.status(400).entity("Faltan datos obligatorios").build();
        }
        try {
            User u = gm.Register(credentials.getNombre(), credentials.getPassword(), credentials.getEmail());
            UserDTO userDTO = new UserDTO(u.getUsername(), null, u.getEmail());
            return Response.status(Response.Status.CREATED).entity(userDTO).build();
        } catch (UserAlreadyExistsException e) {
            return Response.status(Response.Status.CONFLICT).entity(e.getMessage()).build();
        } catch (IllegalArgumentException e) {
            return Response.status(400).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(500).entity("Error interno del servidor").build();
        }
    }

    @POST
    @Path("/users/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(Credentials credentials) {
        if (credentials.getNombre() == null || credentials.getPassword() == null) {
            return Response.status(400).entity("Faltan nombre o password").build();
        }
        try {
            User u = gm.LogIn(credentials.getNombre(), credentials.getPassword());
            return Response.ok(u).build();
        } catch (FailedLoginException e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(500).entity("Error interno del servidor").build();
        }
    }

    @GET
    @Path("/users/{nombre}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserProfile(@PathParam("nombre") String nombre) {
        User u = this.gm.getUser(nombre);
        if (u == null) {
            return Response.status(404).entity("Usuario no encontrado").build();
        }
        return Response.status(200).entity(u).build();
    }

    @GET
    @Path("/shop/objects")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllShopObjects() {
        List<GameObject> objects = this.gm.getAllStoreObjects();
        GenericEntity<List<GameObject>> entity = new GenericEntity<List<GameObject>>(objects) {};
        return Response.status(200).entity(entity).build();
    }

    @GET
    @Path("/users/objects/list")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserObjects(@QueryParam("nombre") String nombre) {
        try {
            List<UserGameObject> userObjects = this.gm.getListObjects(nombre);
            List<GameObjectDTO> flattenedObjects = new ArrayList<>();
            for (UserGameObject ugo : userObjects) {
                GameObject obj = ugo.getGameObject();
                GameObjectDTO dto = new GameObjectDTO(
                        obj.getId(),
                        obj.getNombre(),
                        obj.getDescripcion(),
                        obj.getTipo(),
                        obj.getPrecio(),
                        ugo.getCantidad());
                flattenedObjects.add(dto);
            }
            GenericEntity<List<GameObjectDTO>> entity = new GenericEntity<List<GameObjectDTO>>(flattenedObjects) {};
            return Response.status(Response.Status.OK).entity(entity).build();
        } catch (UserNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(500).entity("Error interno del servidor").build();
        }
    }

    @POST
    @Path("/users/objects/add")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addObjectToUser(AddObject request) {
        if (request.getNombre() == null || request.getObjectId() == null) {
            return Response.status(400).entity("Falta 'nombre' o 'id del objeto'").build();
        }
        try {
            User updatedUser = this.gm.addObjectToUser(request.getNombre(), request.getObjectId());
            return Response.status(Response.Status.OK).entity(updatedUser).build();
        } catch (UserNotFoundException | ObjectNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(500).entity("Error interno del servidor").build();
        }
    }

    @POST
    @Path("/users/objects/buy")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response buyObject(AddObject request) {
        if (request.getNombre() == null || request.getObjectId() == null) {
            return Response.status(400).entity("Falta nombre o objectId").build();
        }
        try {
            User updatedUser = this.gm.purchaseObject(request.getNombre(), request.getObjectId());
            return Response.status(200).entity(updatedUser).build();
        } catch (InsufficientFundsException e) {
            return Response.status(402).entity(e.getMessage()).build();
        } catch (UserNotFoundException | ObjectNotFoundException e) {
            return Response.status(404).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(500).entity("Error interno del servidor").build();
        }
    }

    @GET
    @Path("/events/{id}/users")
    @Produces(MediaType.APPLICATION_JSON)
    public List<UserEventDTO> getUsersOfEvent(@PathParam("id") String id) {

        System.out.println("MINIMO2 | Solicitando usuarios del evento: " + id);

        List<UserEventDTO> lista = new ArrayList<>();

        lista.add(new UserEventDTO(
                "Manolo", "Lama Lorenzo", "https://cdn.pixabay.com/photo/2017/07/11/15/51/kermit-2493979_1280.png"
        ));

        lista.add(new UserEventDTO(
                "Arnau", "Fernandez Garcia", "https://cdn.pixabay.com/photo/2016/01/10/18/59/cookie-monster-1132275_1280.jpg"
        ));

        lista.add(new UserEventDTO("Miguel", "Lopez Alonso", "https://cdn.pixabay.com/photo/2017/07/11/15/51/kermit-2493979_1280.png"
        ));

        return lista;
    }
}

