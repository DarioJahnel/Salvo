//Una entity equivale a una fila de una DB

package com.accenture.Salvo;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;


//prestar atencion a mayusculas

//uso entity para guardar en una DB una ID automatica
//The annotation @Entity tells Spring to create a person table for this class.
//Usar entity deshabilita el uso normal de una clase java, to-do codigo que use la clase tiene que estar conectado a una DB
@Entity
public class Player {

    //Propiedades
    //The annotation @Id says that the id instance variable holds the database key for this class.
    //poniendo @id tambien persisten todos los otros atributos
    //If there are fields that should not be saved, e.g., because they hold temporary scratch data, annotate them with @Transient.
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) //id generacion automatica
    private Long Id;
    private String firstName;
    private String lastName;
    private String userName;
    private String password;

    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER)
    Set<GamePlayer> gamePlayers;

    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER)
    Set<Score> score;


    //Constructor, to-do lo que este aca se ejecuta cuando se crea una nueva instancia del objeto Player
    //es necesario cuando se usa @entity

    public Player() {
    }

    public Player(String user, String password) {
        this.userName = user;
        this.password = password;
    }

    public Player(String first, String last, String user, String password) {
        this.firstName = first;
        this.lastName = last;
        this.userName = user;
        this.password = password;
    }


    //Getters y setters
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    //getGames, para esto necesite hacer un getGame en GamePlayer
    @JsonIgnore //como este metodo referencia a games y en game hay un metodo que referencia a players se usa
    //jsonignore en uno de los dos para evitar recursion
    public List<Game> getGames() {

        return gamePlayers.stream().map(g -> g.getGame()).collect(toList());
    }


    public Long getId(){return Id;}

    public Set<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    @JsonIgnore
    public Set<Score> getScore() {
        return score;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
