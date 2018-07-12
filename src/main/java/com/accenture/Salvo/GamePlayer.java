package com.accenture.Salvo;


import javax.persistence.*;
import java.util.*;

@Entity
public class GamePlayer {


    //Propiedades
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;


    //cada gameplayer tiene 1 player y 1 game

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id")
    private Game game;

    // Cascade se usa para que hibernate guarde lo necesario cuando se guarda gameplayer, en este caso salvoes y ships
    @OneToMany(mappedBy="gamePlayer",fetch=FetchType.EAGER,cascade = CascadeType.ALL)
    Set<Ship> ships;

    @OneToMany(mappedBy="gamePlayer",fetch=FetchType.EAGER,cascade = CascadeType.ALL)
    Set<Salvo> salvoes;

    private Date joinDate;

    private GameState gameState;



    //Constructores
    public GamePlayer(){


        this.salvoes = Collections.emptySet();
    }

    public GamePlayer(Player jugador, Game partida){

        this.player = jugador;
        this.game = partida;
        this.joinDate = new Date();
    }



    //Getters y setters
    public Game getGame(){return game;}

    public Player getPlayer(){return player;}

    public Long getId(){return id;}

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

    public Date getJoinDate() {
        return joinDate;
    }

    public void addShips(List<Ship> ships){
        for (Ship ship: ships){
            ship.setGamePlayer(this);
            this.ships.add(ship);
        }
    }

    public void addSalvo(Salvo salvo, int turn){
        salvo.setGamePlayer(this);
        salvo.setTurn(turn);
        this.salvoes.add(salvo);
    }
}
