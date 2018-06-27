package com.accenture.Salvo;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Score {

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

    private float total;
    private float score;
    private int won;
    private int lost;
    private int tied;

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

    public float getTotal() {
        return total;
    }

    public int getWon() {
        return won;
    }

    public int getLost() {
        return lost;
    }

    public int getTied() {
        return tied;
    }

    public Score(Game juego, Player p, float score) {

        this.game = juego;
        this.player = p;
        this.score = score;

        this.finishDate = new Date();

    }

    public Score() {
    }

    public float getScore() {
        return score;
    }


}
