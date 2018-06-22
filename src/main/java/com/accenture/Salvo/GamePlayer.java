package com.accenture.Salvo;

import javax.persistence.*;
import java.util.Date;

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

    private Date creationDate;

    public GamePlayer(){}

    public GamePlayer(Player jugador, Game partida){

        this.player = jugador;
        this.game = partida;
        this.creationDate = new Date();
    }

    public Game getGame(){return game;}

    public Player getPlayer(){return player;}

    public Long getId(){return Id;}

    public GamePlayer getGamePlayer(){return this;}


}
