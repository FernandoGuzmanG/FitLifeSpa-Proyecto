package com.fitlifespa.microservice_usuarios.hateoas;

import com.fitlifespa.microservice_usuarios.controller.UsuariosController;
import com.fitlifespa.microservice_usuarios.model.Usuario;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class UsuarioModelAssembler implements RepresentationModelAssembler<Usuario, EntityModel<Usuario>> {

    @Override
    public EntityModel<Usuario> toModel(Usuario usuario) {
        return EntityModel.of(usuario,
                linkTo(methodOn(UsuariosController.class).actualizarUsuario(null, usuario.getIdUsuario(), null)).withRel("actualizar_datos"),
                linkTo(methodOn(UsuariosController.class).activarUsuario(usuario.getIdUsuario(), null)).withRel("activar"),
                linkTo(methodOn(UsuariosController.class).desactivarUsuario(usuario.getIdUsuario(), null)).withRel("desactivar"),
                linkTo(methodOn(UsuariosController.class).cambiarRolUsuario(usuario.getIdUsuario(), "{ROL}", null)).withRel("cambiar_rol"),
                linkTo(methodOn(UsuariosController.class).eliminarUsuario(usuario.getIdUsuario(), null)).withRel("eliminar")
        );
    }
}

