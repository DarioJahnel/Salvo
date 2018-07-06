package com.accenture.Salvo;

import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
public class GamePlayer {


    //Propiedades
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long Id;


    //cada gameplayer tiene 1 player y 1 game

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id")
    private Game game;

    @OneToMany(mappedBy="gamePlayer",fetch=FetchType.EAGER)
    Set<Ship> ships;

    @OneToMany(mappedBy="gamePlayer",fetch=FetchType.EAGER)
    Set<Salvo> salvoes;

    private Date creationDate;

    private GameState gameState;



    //Constructores
    public GamePlayer(){}

    public GamePlayer(Player jugador, Game partida){

        this.player = jugador;
        this.game = partida;
        this.creationDate = new Date();
//        this.ships.addAll(barcos);
    }



    //Getters y setters
    public Game getGame(){return game;}

    public Player getPlayer(){return player;}

    public Long getId(){return Id;}

    public Set<Ship> getShips() {
        return ships;
    }

    public Set<Salvo> getSalvoes() {
        return salvoes;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }
}
