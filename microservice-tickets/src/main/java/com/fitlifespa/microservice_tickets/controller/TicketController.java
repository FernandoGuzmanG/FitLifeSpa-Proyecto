package com.fitlifespa.microservice_tickets.controller;

import com.fitlifespa.microservice_tickets.hateoas.TicketModelAssembler;
import com.fitlifespa.microservice_tickets.model.Ticket;
import com.fitlifespa.microservice_tickets.security.RoleValidator;
import com.fitlifespa.microservice_tickets.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    @Autowired
    private TicketService ticketService;
    @Autowired
    private RoleValidator roleValidator;
    @Autowired
    private TicketModelAssembler ticketModelAssembler;



    @Operation(
            summary = "Crear un nuevo ticket",
            description = "Este endpoint permite al CLIENTE crear un nuevo ticket especificando la descripción y el motivo del problema."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Ticket creado exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Ticket.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No se encontró el motivo especificado",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acceso denegado: el usuario no tiene el rol requerido",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Acceso denegado: token no válido",
                    content = @Content
            )
    })
    @PostMapping("/crear")
    public ResponseEntity<EntityModel<Ticket>> crearTicket(
            @RequestBody
            @Parameter(description = "Ticket a crear. Solo se requiere la descripción y el ID del motivo", required = true)
            Ticket ticket,

            @Parameter(hidden = true)
            HttpServletRequest request
    ) {
        roleValidator.requireRole(request, "CLIENTE");
        Long idUsuario = roleValidator.getUserId(request);

        try {
            Ticket nuevoTicket = ticketService.crearTicket(ticket.getDescripcion(), idUsuario, ticket.getMotivo().getId());

            EntityModel<Ticket> ticketModel = ticketModelAssembler.toModel(nuevoTicket);

            return ResponseEntity.status(HttpStatus.CREATED).body(ticketModel);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }



    @Operation(
            summary = "Obtener tickets del cliente autenticado",
            description = "Este endpoint permite al CLIENTE obtener una lista de sus propios tickets registrados en el sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de tickets del cliente obtenida exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Ticket.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "El cliente no tiene tickets registrados",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acceso denegado: el usuario no tiene el rol requerido",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Acceso denegado: token no válido",
                    content = @Content
            )
    })
    @GetMapping("/mis-tickets/cliente")
    public ResponseEntity<CollectionModel<EntityModel<Ticket>>> misTicketsCliente(
            @Parameter(hidden = true)
            HttpServletRequest request
    ) {
        roleValidator.requireRole(request, "CLIENTE");
        Long idUsuario = roleValidator.getUserId(request);
        List<Ticket> tickets = ticketService.listarTicketsPorCliente(idUsuario);

        if (tickets.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<EntityModel<Ticket>> ticketModels = tickets.stream()
                .map(ticketModelAssembler::toModel)
                .toList();

        CollectionModel<EntityModel<Ticket>> collectionModel = CollectionModel.of(ticketModels);

        return ResponseEntity.ok(collectionModel);
    }



    @Operation(
            summary = "Obtener tickets asignados al soporte autenticado",
            description = "Este endpoint permite al usuario con rol SOPORTE obtener una lista de los tickets que tiene asignados."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de tickets asignados al soporte obtenida exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Ticket.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "El soporte no tiene tickets asignados",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acceso denegado: el usuario no tiene el rol requerido",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Acceso denegado: token no válido",
                    content = @Content
            )
    })
    @GetMapping("/mis-tickets/soporte")
    public ResponseEntity<CollectionModel<EntityModel<Ticket>>> misTicketsSoporte(
            @Parameter(hidden = true)
            HttpServletRequest request
    ) {
        roleValidator.requireRole(request, "SOPORTE");
        Long idUsuario = roleValidator.getUserId(request);
        List<Ticket> tickets = ticketService.listarTicketsPorSoporte(idUsuario);

        if (tickets.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<EntityModel<Ticket>> ticketModels = tickets.stream()
                .map(ticketModelAssembler::toModel)
                .map(ticketModel -> {
                    Ticket ticket = ticketModel.getContent();
                    if (ticket != null) {
                        ticketModel.add(linkTo(
                                methodOn(TicketController.class).cambiarEstado(ticket.getId(), "NUEVO_ESTADO", null)
                        ).withRel("cambiar_estado"));
                    }
                    return ticketModel;
                })
                .toList();

        CollectionModel<EntityModel<Ticket>> collectionModel = CollectionModel.of(ticketModels);

        return ResponseEntity.ok(collectionModel);
    }



    @Operation(
            summary = "Obtener tickets de un cliente específico",
            description = "Este endpoint permite al usuario con rol SOPORTE obtener todos los tickets registrados por un cliente en particular, especificando su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de tickets del cliente obtenida exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Ticket.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "El cliente no tiene tickets registrados",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acceso denegado: el usuario no tiene el rol requerido",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Acceso denegado: token no válido",
                    content = @Content
            )
    })
    @GetMapping("/cliente/{idUsuario}")
    public ResponseEntity<CollectionModel<EntityModel<Ticket>>> verTicketsPorCliente(
            @Parameter(description = "ID del cliente del cual se desean obtener los tickets", example = "101", required = true)
            @PathVariable Long idUsuario,

            @Parameter(hidden = true)
            HttpServletRequest request
    ) {
        roleValidator.requireRole(request, "SOPORTE");

        List<Ticket> tickets = ticketService.listarTicketsPorCliente(idUsuario);

        if (tickets.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<EntityModel<Ticket>> ticketModels = tickets.stream()
                .map(ticketModelAssembler::toModel)
                .map(ticketModel -> {
                    Ticket ticket = ticketModel.getContent();
                    if (ticket != null) {
                        ticketModel.add(linkTo(
                                methodOn(TicketController.class).cambiarEstado(ticket.getId(), "NUEVO_ESTADO", null)
                        ).withRel("cambiar_estado"));

                        ticketModel.add(linkTo(
                                methodOn(TicketController.class).asignar(ticket.getId(), null)
                        ).withRel("asignar"));
                    }
                    return ticketModel;
                })
                .toList();

        CollectionModel<EntityModel<Ticket>> collectionModel = CollectionModel.of(ticketModels);

        return ResponseEntity.ok(collectionModel);
    }



    @Operation(
            summary = "Obtener tickets asignados a un soporte específico",
            description = "Este endpoint permite al usuario con rol SOPORTE obtener todos los tickets que han sido asignados a un soporte en particular, especificando su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de tickets del soporte obtenida exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Ticket.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "El soporte no tiene tickets asignados",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acceso denegado: el usuario no tiene el rol requerido",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Acceso denegado: token no válido",
                    content = @Content
            )
    })
    @GetMapping("/soporte/{idUsuario}")
    public ResponseEntity<CollectionModel<EntityModel<Ticket>>> verTicketsPorSoporte(
            @Parameter(description = "ID del soporte del cual se desean obtener los tickets", example = "205", required = true)
            @PathVariable Long idUsuario,

            @Parameter(hidden = true)
            HttpServletRequest request
    ) {
        roleValidator.requireRole(request, "SOPORTE");
        List<Ticket> tickets = ticketService.listarTicketsPorSoporte(idUsuario);

        if (tickets.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<EntityModel<Ticket>> ticketModels = tickets.stream()
                .map(ticketModelAssembler::toModel)
                .map(ticketModel -> {
                    Ticket ticket = ticketModel.getContent();
                    if (ticket != null) {
                        ticketModel.add(linkTo(
                                methodOn(TicketController.class).cambiarEstado(ticket.getId(), "NUEVO_ESTADO", null)
                        ).withRel("cambiar_estado"));

                        ticketModel.add(linkTo(
                                methodOn(TicketController.class).asignar(ticket.getId(), null)
                        ).withRel("asignar"));
                    }
                    return ticketModel;
                })
                .toList();

        CollectionModel<EntityModel<Ticket>> collectionModel = CollectionModel.of(ticketModels);

        return ResponseEntity.ok(collectionModel);
    }



    @Operation(
            summary = "Obtener todos los tickets del sistema",
            description = "Este endpoint permite al usuario con rol SOPORTE obtener todos los tickets registrados en el sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista completa de tickets obtenida exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Ticket.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "No hay tickets registrados en el sistema",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acceso denegado: el usuario no tiene el rol requerido",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Acceso denegado: token no válido",
                    content = @Content
            )
    })
    @GetMapping("/todos")
    public ResponseEntity<CollectionModel<EntityModel<Ticket>>> listarTodos(
            @Parameter(hidden = true)
            HttpServletRequest request
    ) {
        roleValidator.requireRole(request, "SOPORTE");
        List<Ticket> tickets = ticketService.listarTodos();

        if (tickets.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<EntityModel<Ticket>> ticketModels = tickets.stream()
                .map(ticketModelAssembler::toModel)
                .map(ticketModel -> {
                    Ticket ticket = ticketModel.getContent();
                    if (ticket != null) {
                        ticketModel.add(linkTo(
                                methodOn(TicketController.class).cambiarEstado(ticket.getId(), "NUEVO_ESTADO", null)
                        ).withRel("cambiar_estado"));

                        ticketModel.add(linkTo(
                                methodOn(TicketController.class).asignar(ticket.getId(), null)
                        ).withRel("asignar"));
                    }
                    return ticketModel;
                })
                .toList();

        CollectionModel<EntityModel<Ticket>> collectionModel = CollectionModel.of(ticketModels);

        return ResponseEntity.ok(collectionModel);
    }



    @Operation(
            summary = "Cambiar el estado de un ticket",
            description = "Este endpoint permite al SOPORTE cambiar el estado de un ticket asignado."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Estado del ticket actualizado correctamente",
                    content = @Content(schema = @Schema(implementation = Ticket.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No se encontró el ticket con el ID proporcionado",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acceso denegado: el usuario no tiene el rol requerido",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Acceso denegado: token no válido",
                    content = @Content
            )
    })
    @PutMapping("/{id}/estado/{estado}")
    public ResponseEntity<EntityModel<Ticket>> cambiarEstado(
            @Parameter(description = "ID del ticket", example = "7", required = true)
            @PathVariable Long id,

            @Parameter(description = "Nuevo estado para el ticket", example = "CERRADO", required = true)
            @PathVariable String estado,

            @Parameter(hidden = true)
            HttpServletRequest request
    ) {
        roleValidator.requireRole(request, "SOPORTE");

        try {
            Ticket actualizado = ticketService.cambiarEstado(id, estado.toUpperCase());

            EntityModel<Ticket> ticketModel = ticketModelAssembler.toModel(actualizado);
            ticketModel.add(linkTo(methodOn(TicketController.class).asignar(id, null)).withRel("asignar"));

            return ResponseEntity.ok(ticketModel);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }



    @Operation(
            summary = "Asignar un ticket al soporte autenticado",
            description = "Este endpoint permite al SOPORTE asignarse un ticket especificado por su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Ticket asignado exitosamente",
                    content = @Content(schema = @Schema(implementation = Ticket.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No se encontró el ticket con el ID proporcionado",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acceso denegado: el usuario no tiene el rol requerido",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Acceso denegado: token no válido",
                    content = @Content
            )
    })
    @PutMapping("/{id}/asignar")
    public ResponseEntity<EntityModel<Ticket>> asignar(
            @Parameter(description = "ID del ticket a asignar", example = "10", required = true)
            @PathVariable Long id,

            @Parameter(hidden = true)
            HttpServletRequest request
    ) {
        roleValidator.requireRole(request, "SOPORTE");
        Long idSoporte = roleValidator.getUserId(request);

        try {
            Ticket ticketAsignado = ticketService.asignarSoporte(id, idSoporte);

            EntityModel<Ticket> ticketModel = ticketModelAssembler.toModel(ticketAsignado);

            return ResponseEntity.ok(ticketModel);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

}
