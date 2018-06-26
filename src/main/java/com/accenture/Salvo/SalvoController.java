package com.accenture.Salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;


//Este controlador se encarga de devolver informacion en forma de JSON cuando recibe un pedido
//en contraste con /rest/games, api/games devuelve solo la informacion que yo le digo que devuelva

@RestController
public class SalvoController {

    @Autowired //???, preguntar
    private GameRepository gameRepo;

    @Autowired
    private GamePlayerRepository gamePlayerRepo;

//    @RequestMapping("/api/games") //cuando el controlador recibe pedido de url con /api, se ejecuta este metodo
//    public List<Object> getAll() {
//        return gameRepo.findAll().stream()
//                .map(this::gameDTO)
//                .collect(toList());
//
//    }

   @RequestMapping("/api/game_view/{gp}")
   public Map<String,Object> findGamePlayer(@PathVariable Long gp){ //todo: ANOTACION, machear lo que esta dentro de llaves, sino no lo reconoce como variable
       //todo: encontrar el juego que tiene asociado el gameplayerID que me pasan

       GamePlayer gplayer  = gamePlayerRepo.findOne(gp);



       return gameDTO(gplayer.getGame(), gplayer);


   }

    private Map<String, Object> gameDTO(Game juego, GamePlayer gp) {
        Map<String, Object> mapa = new HashMap<>();

        //consigue un flatmap(o sea que su contenido se junta y se convierte en stream) de streams ??
        Stream<Salvo> SS = juego.getGamePlayers().stream().flatMap(g-> g.getSalvoes().stream());

        mapa.put("id", juego.getId());
        mapa.put("created", juego.getDate());
        mapa.put("gamePlayers", gamePlayerList(juego.getGamePlayers()));
        mapa.put("ships",procesarShips(gp.getShips()) );
        mapa.put("salvoes",SS.map(this::salvoDTO).collect(toList()));

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

    private List<Map> procesarShips(Set<Ship> ships){

       return ships.stream().map(this::shipDTO).collect(toList());
    }

    private Map<String,Object> shipDTO(Ship ship){

       Map<String,Object> mapa = new HashMap<>();

       mapa.put("type",ship.getType());
       mapa.put("locations", ship.getLocation());

       return mapa;
    }

    private Map<String,Object> salvoDTO(Salvo s){

        Map<String,Object> mapa = new HashMap<>();

        mapa.put("turn",s.getTurn());
        mapa.put("player",s.getGamePlayer().getPlayer().getId());
        mapa.put("locations",s.getLocation());

        return mapa;

    }




}





