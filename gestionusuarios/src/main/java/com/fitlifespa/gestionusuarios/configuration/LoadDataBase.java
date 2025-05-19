package com.fitlifespa.gestionusuarios.configuration;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fitlifespa.gestionusuarios.model.Estado;
import com.fitlifespa.gestionusuarios.model.Rol;
import com.fitlifespa.gestionusuarios.model.Usuario;
import com.fitlifespa.gestionusuarios.model.Enum.NombreRol;
import com.fitlifespa.gestionusuarios.repository.EstadoRepository;
import com.fitlifespa.gestionusuarios.repository.RolRepository;
import com.fitlifespa.gestionusuarios.repository.UsuarioRepository;

@Configuration
public class LoadDataBase {
    @Bean
    CommandLineRunner initDatabase(RolRepository rolRepo, UsuarioRepository userRepo, EstadoRepository estadoRepo ){
         return args ->{
            //si las tablas estan vacias - no hay registros en las tablas
            if(rolRepo.count() == 0 && userRepo.count() == 0){
                //cargamos los roles por defecto
                Rol administrador = new Rol();
                administrador.setNombreRol(NombreRol.ADMINISTRADOR);
                rolRepo.save(administrador);

                Rol coordinador = new Rol();
                coordinador.setNombreRol(NombreRol.COORDINADOR);
                rolRepo.save(coordinador);

                Rol entrenador = new Rol();
                entrenador.setNombreRol(NombreRol.ENTRENADOR);
                rolRepo.save(entrenador);

                Rol soporte = new Rol();
                soporte.setNombreRol(NombreRol.SOPORTE);
                rolRepo.save(soporte);

                Rol cliente = new Rol();
                cliente.setNombreRol(NombreRol.CLIENTE);
                rolRepo.save(cliente);

                Estado estadoActivo = new Estado();
                estadoActivo.setNombreEstado("ACTIVO");
                estadoRepo.save(estadoActivo);

                Estado estadoInactivo = new Estado();
                estadoInactivo.setNombreEstado("INACTIVO");
                estadoRepo.save(estadoInactivo);
                
                
                //cargar usuarios por defecto o iniciales
                userRepo.save(new Usuario(null,"21.612.392-1", "Fernando", "Alonso", "Guzman", "Gonzalez", "ferguzq@gmail.com", "ferguzg", "123456", administrador, estadoActivo));
                userRepo.save(new Usuario(null,"11.902.989-9", "Matías", "Flow", "Samaniego", "Rizz", "fer.guzmang@duocuc.cl", "matiflowfn", "123456", cliente, estadoActivo));

                System.out.println("Datos iniciales Cargados");
            }
            else{
                System.out.println("Datos ya existentes. No se cargaron nuevos datos");
            }
        };
    } 
}