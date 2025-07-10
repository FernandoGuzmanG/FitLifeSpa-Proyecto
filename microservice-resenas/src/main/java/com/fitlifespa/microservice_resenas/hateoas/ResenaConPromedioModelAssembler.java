package com.fitlifespa.microservice_resenas.hateoas;

import com.fitlifespa.microservice_resenas.controller.ResenaController;
import com.fitlifespa.microservice_resenas.model.Resena;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ResenaConPromedioModelAssembler implements RepresentationModelAssembler<Resena, EntityModel<Resena>> {

    @Override
    public EntityModel<Resena> toModel(Resena resena) {
        return EntityModel.of(resena,
                linkTo(methodOn(ResenaController.class).promedioPorServicio(resena.getIdServicio(), null)).withRel("ver_promedio"),
                        linkTo(methodOn(ResenaController.class).banear(resena.getIdServicio(), null)).withRel("banear_reseña"),
                        linkTo(methodOn(ResenaController.class).desbanear(resena.getIdServicio(), null)).withRel("desbanear_reseña")
        );
    }
}

