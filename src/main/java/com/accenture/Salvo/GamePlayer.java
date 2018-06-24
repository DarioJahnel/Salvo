package com.accenture.Salvo;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
public class GamePlayer {

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

    @OneToMany(mappedBy="gamePlayer", fetch=FetchType.EAGER)
    Set<Ship> ships = new HashSet<>();

    private Date creationDate;

    public GamePlayer(){}

    public GamePlayer(Player jugador, Game partida){

        this.player = jugador; //trate de hacer carpetas y quedo este error, pero no pasa nada??
        this.game = partida;
        this.creationDate = new Date();
    }

    public Game getGame(){return game;}

    public Player getPlayer(){return player;}

    public Long getId(){return Id;}

<<<<<<< HEAD
=======
    public GamePlayer getGamePlayer(){return this;}

    public Set<Ship> getShips() {
        return ships;
    }
>>>>>>> Ship-ShipRepoTest
}
