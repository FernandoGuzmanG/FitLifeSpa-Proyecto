package com.fitlifespa.microservice_tickets.hateoas;

import com.fitlifespa.microservice_tickets.controller.HistorialController;
import com.fitlifespa.microservice_tickets.model.Ticket;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class TicketModelAssembler implements RepresentationModelAssembler<Ticket, EntityModel<Ticket>> {

    @Override
    public EntityModel<Ticket> toModel(Ticket ticket) {
        return EntityModel.of(ticket,
                linkTo(methodOn(HistorialController.class).historial(ticket.getId(), null)).withRel("ver_historial"),
                linkTo(methodOn(HistorialController.class).responder(ticket.getId(), null, null)).withRel("responder")
        );
    }
}


