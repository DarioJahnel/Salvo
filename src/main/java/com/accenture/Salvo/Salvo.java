package com.accenture.Salvo;


import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Salvo {



    //Propiedades
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private int turn;

    @ElementCollection
    @Column(name = "locations ")
    private List<String> location = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="gamePlayer_id")
    private GamePlayer gamePlayer;


    //Constructores
    public Salvo(int turn, List<String> location, GamePlayer gp){

        this.turn = turn;
        this.location = location;
        this.gamePlayer = gp;

    }

    public Salvo(){}




    //Getters y setters
    public Long getId() {
        return id;
    }

    public int getTurn() {
        return turn;
    }

    public List<String> getLocation() {
        return location;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public void setLocation(List<String> location) {
        this.location = location;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }
}
