package com.accenture.Salvo;

import org.aspectj.weaver.patterns.HasMemberTypePatternForPerThisMatching;
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

    @RequestMapping("/api/games") //cuando el controlador recibe pedido de url con /api, se ejecuta este metodo
    public Map<String,Object> getAll() {
        Map<String,Object> mapa = new HashMap<>();

        mapa.put("player", null);
        mapa.put("games",procesarGameRepoApi());
        return mapa;

    }

    private List<Map> procesarGameRepoApi(){

        Stream<Game> gameStream = gameRepo.findAll().stream();

        return procesarGamesApi(gameStream);
    }

    private List<Map> procesarGamesApi(Stream<Game> gameStream){
        return gameStream.map(this::apiGamesDTO).collect(toList());
    }

    private Map<String,Object> apiGamesDTO(Game juego){

        Map<String,Object> mapa = new HashMap<>();

        mapa.put("id", juego.getId());
        mapa.put("created", juego.getCreationDate());
        mapa.put("gamePlayers", procesarGamePlayerApi(juego.getGamePlayers()));

        return mapa;
    }

    private List<Object> procesarGamePlayerApi(Set<GamePlayer> gp){

        return gp.stream().map(this::gamePlayerApiDTO).collect(toList());
    }

    private Map<String,Object> gamePlayerApiDTO(GamePlayer gp){

        Map<String,Object> mapa = new HashMap<>();

        mapa.put("id", gp.getId());
        mapa.put("player",playerDTO(gp.getPlayer()));

        return mapa;

    }
    @RequestMapping("/api/game_view/{gp}")
   public Map<String,Object> findGamePlayer(@PathVariable Long gp){ //todo: ANOTACION, machear lo que esta dentro de llaves, sino no lo reconoce como variable
       //todo: encontrar el juego que tiene asociado el gameplayerID que me pasan

       GamePlayer gplayer  = gamePlayerRepo.findOne(gp);



       return game_viewDTO(gplayer.getGame(), gplayer);


   }

    private Map<String, Object> game_viewDTO(Game juego, GamePlayer gp) {
        Map<String, Object> mapa = new HashMap<>();

        //consigue un flatmap(o sea que su contenido se junta y se convierte en stream) de streams ??
        Stream<Salvo> SS = juego.getGamePlayers().stream().flatMap(g-> g.getSalvoes().stream());

        mapa.put("id", juego.getId());
        mapa.put("created", juego.getCreationDate());
        mapa.put("gamePlayers", procesarGamePlayerView(juego.getGamePlayers()));
        mapa.put("ships",procesarShips(gp.getShips()) );
        mapa.put("salvoes",procesarSalvos(SS)); //ya le paso el stream completo de salvos del game

        return mapa;

    }


    private List<Map> procesarGamePlayerView(Set<GamePlayer> set){

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

    private List<Map> procesarSalvos(Stream<Salvo> SS){

       return SS.map(this::salvoDTO).collect(toList());
    }
    private Map<String,Object> salvoDTO(Salvo s){

        Map<String,Object> mapa = new HashMap<>();

        mapa.put("turn",s.getTurn());
        mapa.put("player",s.getGamePlayer().getPlayer().getId());
        mapa.put("locations",s.getLocation());

        return mapa;

    }




}





