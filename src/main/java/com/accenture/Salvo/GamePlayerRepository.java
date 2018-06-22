package com.accenture.Salvo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
interface GamePlayerRepository extends JpaRepository<GamePlayer, Long> {

}