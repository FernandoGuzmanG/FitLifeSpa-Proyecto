package com.fitlifespa.membresia.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fitlifespa.membresia.Model.Membresia;

@Repository
public interface MembresiaRepository extends JpaRepository<Membresia, Long>{
    @Query(value = "SELECT * FROM membresia where id_usuario= :idUsuario", nativeQuery = true)
    List<Membresia> findByUsuarioId(@Param("idUsuario") Long idUsuario);

    @Query("UPDATE Membresia m SET m.activo = false WHERE m.id = :id")
    void desactivarMembresia(@Param("id") Long id);

    




}
