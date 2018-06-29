package com.accenture.Salvo;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Score {


    //Propiedades
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long Id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id")
    private Game game;

    private Date finishDate;

    private double score;



    //Constructores
    public Score(Game juego, Player p, float score) {

        this.game = juego;
        this.player = p;
        this.score = score;

        this.finishDate = new Date();

    }

    public Score() {
    }



    //Getters y setters
    public Long getId() {
        return Id;
    }

    public Player getPlayer() {
        return player;
    }

    public Game getGame() {
        return game;
    }

    public Date getFinishDate() {
        return finishDate;
    }

    public double getScore() {
        return score;
    }




}
