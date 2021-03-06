package com.accenture.Salvo;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;


@Entity
public class Game {

    //Propiedades
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO) //id generacion automatica
    private Long id;

    private Date creationDate;

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
    Set<GamePlayer> gamePlayers;

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
    Set<Score> score;




    //Constructores
    public Game(){}

    public Game(Date fechaahora){
        creationDate = fechaahora;

    }



    //Getters y setters
    public void setDate(Date fecha){
        this.creationDate = fecha;

    }

    public List<Player> getPlayers() {

        return gamePlayers.stream().map(g -> g.getPlayer()).collect(toList());
    }

    public Long getId(){return id;}

    public Set<GamePlayer> getGamePlayers() {return this.gamePlayers;}

    public Date getCreationDate() {
        return creationDate;
    }

    public Set<Score> getScore() {
        return score;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
}
