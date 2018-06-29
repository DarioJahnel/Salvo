package com.accenture.Salvo;


import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Ship {



    //Propiedades
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long Id;


    //poniendo esta anotacion hago una relacion 1(ships) a muchos (locations) sin necesitar crear una clase
    //esto crea una lista de objetos embebidos (data usada solo en el objeto que la contiene, ships)
    @ElementCollection
    @Column(name = "locations ")
    private List<String> location = new ArrayList<>();

    private String type;

    //un ship tiene 1 gameplayer, 1 gameplayer tiene muchos ships
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="gamePlayer_id")
    private GamePlayer gamePlayer;


    //Constructores
    public Ship() {

    }

    public Ship(String tipo, List<String> locations, GamePlayer gp) {

        this.type = tipo;
        this.location = locations;
        this.gamePlayer = gp;

    }



    //Getters y setters
    public Long getId() {
        return Id;
    }

    public List<String> getLocation() {
        return location;
    }

    public String getType() {
        return type;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }




}
