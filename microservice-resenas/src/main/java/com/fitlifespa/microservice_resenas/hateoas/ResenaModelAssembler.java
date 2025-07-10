package com.fitlifespa.microservice_resenas.hateoas;

import com.fitlifespa.microservice_resenas.controller.ResenaController;
import com.fitlifespa.microservice_resenas.model.Resena;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class ResenaModelAssembler implements RepresentationModelAssembler<Resena, EntityModel<Resena>> {

    @Override
    public EntityModel<Resena> toModel(Resena resena) {
        return EntityModel.of(resena,
                linkTo(methodOn(ResenaController.class).crearResena(null, null)).withRel("crear"),
                linkTo(methodOn(ResenaController.class).modificarResena(resena.getId(), resena, null)).withRel("modificar"),
                linkTo(methodOn(ResenaController.class).eliminarResena(resena.getId(), null)).withRel("eliminar")
        );
    }
}

