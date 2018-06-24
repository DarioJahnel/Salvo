package com.accenture.Salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.*;
import static java.util.stream.Collectors.toList;


//Este controlador se encarga de devolver informacion en forma de JSON cuando recibe un pedido
//en contraste con /rest/games, api/games devuelve solo la informacion que yo le digo que devuelva

@RestController
public class SalvoController {

    @Autowired //???, preguntar
    private GameRepository gameRepo;

    @Autowired
    private GamePlayerRepository gamePlayerRepo;

    @RequestMapping("/api/games") //cuando el controlador recibe pedido de url con /api, se ejecuta este metodo
    public List<Object> getAll() {
        return gameRepo.findAll().stream()
                .map(this::gameDTO)
                .collect(toList());

    }

   /* @RequestMapping("/api/game_view/{gameId}") //Recibe una variable ingresada por el usuario o la app en forma de URL
    public Map<String, Object> findGame(@PathVariable Long gameId) {

        Game game = gameRepo.findOne(gameId);

        return gameDTO(game);


    }*/

   @RequestMapping("/api/game_view/{gamePlayerID}")
   public Map<String,Object> findGamePlayer(@PathVariable Long gamePlayerID){ //todo: ANOTACION, machear lo que esta dentro de llaves, sino no lo reconoce como variable
       //todo: encontrar el juego que tiene asociado el gameplayerID que me pasan

       GamePlayer gp  = gamePlayerRepo.findOne(gamePlayerID);

       return gameDTO(gp.getGame());


   }

    private Map<String, Object> gameDTO(Game juego) {
        Map<String, Object> mapa = new HashMap<>();

        mapa.put("id", juego.getId());
        mapa.put("created", juego.getDate());
        mapa.put("gamePlayers", gamePlayerList(juego.getGamePlayers()));

        return mapa;

    }

    private List<Map> gamePlayerList(Set<GamePlayer> set){

        return set.stream().map(this::gamePlayerDTO).collect(toList());

    }

    private Map<String, Object> gamePlayerDTO (GamePlayer gp){
        Map<String, Object> mapa = new HashMap<>();

        mapa.put("id",gp.getId());
        mapa.put("player", playerDTO(gp.getPlayer()));

        return mapa;
    }

    private Map<String, Object> playerDTO(Player p) {

        Map<String, Object> mapa = new HashMap<>();

        mapa.put("id",p.getId());
        mapa.put("email",p.getUserName());

        return mapa;
    }
}





