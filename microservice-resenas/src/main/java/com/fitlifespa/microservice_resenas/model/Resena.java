package com.fitlifespa.microservice_resenas.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fitlifespa.microservice_resenas.model.Enum.EstadoResena;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "resena")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad que representa una reseña realizada por un usuario sobre un servicio")
public class Resena {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único de la reseña", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Column(nullable = false)
    @Schema(description = "Fecha en que se realizó la reseña", example = "2025-06-24", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDate fechaResena;

    @Column(length = 500)
    @Schema(description = "Comentario escrito por el usuario (máximo 500 caracteres)", example = "Muy buen servicio, lo recomiendo.")
    private String comentario;

    @Column(nullable = false)
    @Schema(description = "ID del usuario que realiza la reseña", example = "42", accessMode = Schema.AccessMode.READ_ONLY)
    private Long idUsuario;

    @Column(nullable = false)
    @Schema(description = "ID del servicio al que pertenece esta reseña", example = "101")
    private Long idServicio;

    @JsonIgnore
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Schema(description = "Estado de la reseña (DESBANEADO, BANEADO)", example = "DESBANEADO", accessMode = Schema.AccessMode.READ_ONLY)
    private EstadoResena estado;

    @Column(nullable = false)
    @Schema(description = "Calificación otorgada por el usuario (de 1.0 a 5.0)", example = "4.5")
    private Double calificacion;
}
