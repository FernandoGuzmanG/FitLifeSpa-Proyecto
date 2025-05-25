package com.fitlife.servicios.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fitlife.servicios.Model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long>{
    @Query(value = "SELECT * FROM usuario WHERE correo = :correo", nativeQuery = true)
    Usuario findByCorreo(@Param("correo") String correo);

    @Query(value = "SELECT * FROM usuario WHERE rut = :rut", nativeQuery = true)
    Usuario findByRut(@Param("rut") String rut);

    @Query(value = "SELECT * FROM usuario WHERE nom_usuario = :nomUsuario", nativeQuery = true)
    Usuario findByUsername(@Param("nomUsuario") String nomUsuario);

}
