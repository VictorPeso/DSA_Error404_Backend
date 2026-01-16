package edu.upc.dsa.services;

import edu.upc.dsa.GameManager;
import edu.upc.dsa.GameManagerImpl;
import edu.upc.dsa.models.Evento;
import edu.upc.dsa.models.RegistroEventoRequest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Api(value = "/eventos", description = "Eventos API")
@Path("/eventos")
public class EventoService {

    private GameManager gm;

    public EventoService() {
        this.gm = GameManagerImpl.getInstance();
    }

    @GET
    @ApiOperation(value = "Obtener todos los eventos", response = Evento.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEventos() {
        List<Evento> eventos = gm.getEventos();
        return Response.status(200).entity(eventos).build();
    }

    @POST
    @Path("/{id}/register")
    @ApiOperation(value = "Registrar usuario en un evento")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Registrado"),
            @ApiResponse(code = 400, message = "Faltan datos"),
            @ApiResponse(code = 409, message = "Ya registrado")
    })
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(@PathParam("id") String eventoId, RegistroEventoRequest req) {
        if (req == null || req.getUserId() == null || req.getUserId().isEmpty()) {
            return Response.status(400).entity("Falta userId").build();
        }
        boolean ok = gm.registerEvento(req.getUserId(), eventoId);
        if (!ok) {
            return Response.status(409).entity("Ya registrado").build();
        }
        return Response.status(200).build();
    }
}
