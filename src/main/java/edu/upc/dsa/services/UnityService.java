package edu.upc.dsa.services;

import edu.upc.dsa.GameManager;
import edu.upc.dsa.GameManagerImpl;
import edu.upc.dsa.exceptions.UserNotFoundException;
import edu.upc.dsa.exceptions.ObjectNotFoundException;
import edu.upc.dsa.models.GameObject;
import edu.upc.dsa.models.User;
import edu.upc.dsa.models.UserGameObject;
import edu.upc.dsa.models.dto.AddCoinsRequest;
import edu.upc.dsa.models.dto.AddObject;
import edu.upc.dsa.models.dto.GameObjectDTO;
import edu.upc.dsa.models.dto.UpdateProgressRequest;
import edu.upc.dsa.models.dto.UpdateObjectQuantityRequest;
import edu.upc.dsa.models.dto.UnityProfileResponse;

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

@Api(value = "/game/unity", description = "Unity Game Integration API")
@Path("/game/unity")
public class UnityService {

    private GameManager gm;

    public UnityService() {
        this.gm = GameManagerImpl.getInstance();
    }

    // ------------------- AÑADIR MONEDAS -------------------
    @POST
    @Path("/add-coins")
    @ApiOperation(value = "Añadir monedas a un usuario desde Unity")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Monedas añadidas correctamente", response = User.class),
            @ApiResponse(code = 404, message = "Usuario no encontrado"),
            @ApiResponse(code = 400, message = "Datos inválidos")
    })
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addCoins(AddCoinsRequest request) {
        if (request.getUsername() == null || request.getAmount() == null) {
            return Response.status(400).entity("Faltan username o amount").build();
        }

        if (request.getAmount() <= 0) {
            return Response.status(400).entity("La cantidad debe ser positiva").build();
        }

        try {
            gm.addCoinsToUser(request.getUsername(), request.getAmount());
            User updatedUser = gm.getUser(request.getUsername());
            return Response.status(200).entity(updatedUser).build();
        } catch (UserNotFoundException e) {
            return Response.status(404).entity(e.getMessage()).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity("Error interno del servidor").build();
        }
    }

    // ------------------- OBTENER PERFIL -------------------
    @GET
    @Path("/profile/{username}")
    @ApiOperation(value = "Obtener perfil completo del usuario para Unity")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Perfil obtenido", response = UnityProfileResponse.class),
            @ApiResponse(code = 404, message = "Usuario no encontrado")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProfile(@PathParam("username") String username) {
        try {
            User user = gm.getUser(username);
            if (user == null) {
                return Response.status(404).entity("Usuario no encontrado").build();
            }

            // Obtener objetos del usuario
            List<UserGameObject> userObjects = gm.getListObjects(username);
            List<GameObjectDTO> objectDTOs = new ArrayList<>();

            for (UserGameObject ugo : userObjects) {
                GameObject obj = ugo.getGameObject();
                GameObjectDTO dto = new GameObjectDTO(
                        obj.getId(),
                        obj.getNombre(),
                        obj.getDescripcion(),
                        obj.getTipo(),
                        obj.getPrecio(),
                        ugo.getCantidad());
                objectDTOs.add(dto);
            }

            UnityProfileResponse response = new UnityProfileResponse(user, objectDTOs);
            return Response.status(200).entity(response).build();

        } catch (UserNotFoundException e) {
            return Response.status(404).entity(e.getMessage()).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity("Error interno del servidor").build();
        }
    }

    // ------------------- ACTUALIZAR PROGRESO -------------------
    @POST
    @Path("/update-progress")
    @ApiOperation(value = "Actualizar progreso del jugador (ActFrag, BestScore)")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Progreso actualizado", response = User.class),
            @ApiResponse(code = 404, message = "Usuario no encontrado"),
            @ApiResponse(code = 400, message = "Datos inválidos")
    })
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateProgress(UpdateProgressRequest request) {
        if (request.getUsername() == null) {
            return Response.status(400).entity("Falta username").build();
        }

        try {
            gm.updateUserProgress(request.getUsername(), request.getActFrag(), request.getBestScore());
            User updatedUser = gm.getUser(request.getUsername());
            return Response.status(200).entity(updatedUser).build();
        } catch (UserNotFoundException e) {
            return Response.status(404).entity(e.getMessage()).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity("Error interno del servidor").build();
        }
    }

    // ------------------- BOSS LOOT (Drop de jefe - gratis) -------------------
    @POST
    @Path("/boss-loot")
    @ApiOperation(value = "Añadir objeto gratis por drop de jefe (sin coste)")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Objeto añadido", response = User.class),
            @ApiResponse(code = 404, message = "Usuario u Objeto no encontrado"),
            @ApiResponse(code = 400, message = "Datos inválidos")
    })
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addBossLoot(AddObject request) {
        if (request.getNombre() == null || request.getObjectId() == null) {
            return Response.status(400).entity("Faltan username (nombre) o objectId").build();
        }

        try {
            // Este método añade el objeto SIN cobrar monedas (perfecto para drops)
            User updatedUser = gm.addObjectToUser(request.getNombre(), request.getObjectId());
            return Response.status(200).entity(updatedUser).build();
        } catch (UserNotFoundException | ObjectNotFoundException e) {
            return Response.status(404).entity(e.getMessage()).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity("Error interno del servidor").build();
        }
    }

    @POST
    @Path("/update-object-quantity")
    @ApiOperation(value = "Actualizar cantidad de un objeto del usuario (para pociones usadas)")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Cantidad actualizada", response = User.class),
            @ApiResponse(code = 404, message = "Usuario u Objeto no encontrado"),
            @ApiResponse(code = 400, message = "Datos inválidos")
    })
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateObjectQuantity(UpdateObjectQuantityRequest request) {
        if (request.getUsername() == null || request.getObjectId() == null || request.getNewQuantity() == null) {
            return Response.status(400).entity("Faltan username, objectId o newQuantity").build();
        }

        if (request.getNewQuantity() < 0) {
            return Response.status(400).entity("La cantidad no puede ser negativa").build();
        }

        try {
            gm.updateObjectQuantity(request.getUsername(), request.getObjectId(), request.getNewQuantity());
            User updatedUser = gm.getUser(request.getUsername());
            return Response.status(200).entity(updatedUser).build();
        } catch (UserNotFoundException | ObjectNotFoundException e) {
            return Response.status(404).entity(e.getMessage()).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity("Error interno del servidor").build();
        }
    }
}
