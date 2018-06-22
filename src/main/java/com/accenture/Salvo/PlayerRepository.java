//Un repository equivale a una tabla en una DB (basicamente es un contenedor de entities)
//Un repositorio JPA permite manejar las entidades que contiene

package com.accenture.Salvo;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;


// sacar class, esto es solo una interfaz

    @RepositoryRestResource
    public interface PlayerRepository extends JpaRepository<Player, Long> { //jpa repository te deja manipular la tabla?
        List<Player> findByUserName(String nombre); //esto crea un metodo para buscar por username, parte de spring

    }