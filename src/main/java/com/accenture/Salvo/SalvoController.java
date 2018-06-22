package com.accenture.Salvo;

import org.springframework.beans.factory.annotation.Autowired;
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

    @RequestMapping("/api/games") //cuando el controlador recibe pedido de url con /api, se ejecuta este metodo
    public List<Object> getAll() {
        return gameRepo.findAll().stream()
                .map(this::gameDTO)
                .collect(toList());

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





