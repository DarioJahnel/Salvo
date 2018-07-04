package com.accenture.Salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;


//Este controlador se encarga de devolver informacion en forma de JSON cuando recibe un pedido
//en contraste con /rest/games, api/games devuelve solo la informacion que yo le digo que devuelva

@RestController
@RequestMapping("/api")
public class SalvoController {

    @Autowired //???, preguntar
    private GameRepository gameRepo;

    @Autowired
    private GamePlayerRepository gamePlayerRepo;

    @Autowired
    private PlayerRepository playerRepo;


    // GAMES
    @RequestMapping("/games") //cuando el controlador recibe pedido de url con /api, se ejecuta este metodo
    public Map<String,Object> getAllGames(Authentication authentication) {
        Map<String,Object> mapa = new HashMap<>();
        if(isGuest(authentication)== true){mapa.put("player", "guest");}
        else{
            mapa.put("player",procesarPlayerAuthentication(authentication));
        }
        mapa.put("games", procesarGames(procesarGameRepo()));

        return mapa;

    }

    private Map<String,Object> procesarPlayerAuthentication(Authentication authentication){
        Map<String,Object> mapa = new HashMap<>();
        Player player = playerRepo.findByUserName(authentication.getName());

        mapa.put("id", player.getId());
        mapa.put("name", player.getUserName());

        return mapa;
    }
    private Stream<Game> procesarGameRepo(){

        return gameRepo.findAll().stream();

    }

    private List<Map> procesarGames(Stream<Game> gameStream){
        return gameStream.map(this::GamesDTO).collect(toList());
    }

    private Map<String,Object> GamesDTO(Game juego){

        Map<String,Object> mapa = new HashMap<>();

        mapa.put("id", juego.getId());
        mapa.put("created", juego.getCreationDate());
        mapa.put("gamePlayers", procesarGamePlayer(juego.getGamePlayers()));
        mapa.put("scores", scoreList(juego));

        return mapa;
    }

    private List<Object> procesarGamePlayer(Set<GamePlayer> gp){

        return gp.stream().map(this::gamePlayerApiDTO).collect(toList());
    }

    private Map<String,Object> gamePlayerApiDTO(GamePlayer gp){

        Map<String,Object> mapa = new HashMap<>();

        mapa.put("id", gp.getId());
        mapa.put("player",playerDTO(gp.getPlayer()));

        return mapa;

    }

    private List<Map> scoreList(Game juego){

       return juego.getScore().stream().map(this::scoreDTO).collect(toList());

    }

    private Map<String,Object> scoreDTO(Score s){

        Map<String,Object> mapa = new HashMap<>();

        mapa.put("playerID",s.getPlayer().getId());
        mapa.put("score",s.getScore());
        mapa.put("finishDate",s.getFinishDate());

        return mapa;

    }

    // GAME VIEW
    @RequestMapping("/game_view/{gp}")
   public Map<String,Object> findGamePlayer(@PathVariable Long gp){ //todo: ANOTACION, machear lo que esta dentro de llaves, sino no lo reconoce como variable
       //todo: encontrar el juego que tiene asociado el gameplayerID que me pasan

       GamePlayer gplayer  = gamePlayerRepo.findOne(gp);



       return game_viewDTO(gplayer);


   }

    private Map<String, Object> game_viewDTO(GamePlayer gp) {
        Map<String, Object> mapa = new HashMap<>();



        mapa.put("id", gp.getGame().getId());
        mapa.put("created", gp.getGame().getCreationDate());
        mapa.put("gamePlayers", procesarGamePlayerView(gp.getGame().getGamePlayers()));
        mapa.put("ships",procesarShips(gp.getShips()) );
        mapa.put("salvoes",procesarSalvos(gp.getGame().getGamePlayers())); //ya le paso el stream completo de salvos del game

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

    private List<Map> procesarSalvos(Set<GamePlayer> gp){

       Stream<Salvo> SS = gp.stream().flatMap(g-> g.getSalvoes().stream());

       return SS.map(this::salvoDTO).collect(toList());
    }
    private Map<String,Object> salvoDTO(Salvo s){

        Map<String,Object> mapa = new HashMap<>();

        mapa.put("turn",s.getTurn());
        mapa.put("player",s.getGamePlayer().getPlayer().getId());
        mapa.put("locations",s.getLocation());

        return mapa;

    }

    // LeaderBoard JSON
    @RequestMapping("/leaderBoard")
    public List<Object> leaderBoard(){

        return playerScoreList();


    }


    private List<Object> playerScoreList(){

        return playerRepo.findAll().stream().map(this::playerScoreDTO).collect(toList());
    }

    private Map<String,Object> playerScoreDTO (Player p){

        Map<String,Object> mapa = new HashMap<>();
        Map<String,Object> mapa2 = new HashMap<>();

        mapa2.put("name",p.getUserName());
        mapa2.put("score", mapa);

        mapa.put("total",playerTotalScore(p));
        mapa.put("won",playerWinScore(p));
        mapa.put("lost",playerLossScore(p));
        mapa.put("tied" ,playerTiesScore(p));

        return mapa2;

    }

    private double playerTotalScore(Player p){

        double total = 0;
        total = p.getScore().stream().mapToDouble(s-> s.getScore()).sum();
        return total;
    }

    private Long playerWinScore(Player p) {

        Long wins;
        wins = p.getScore().stream().filter(s -> s.getScore() == 1).count();
        return wins;


    }

    private Long playerLossScore(Player p){

        Long loss;
        loss = p.getScore().stream().filter(s -> s.getScore() == 0).count();
        return loss;

    }

    private Long playerTiesScore(Player p){

        Long ties;
        ties = p.getScore().stream().filter(s -> s.getScore() == 0.5).count();
        return ties;
    }

    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }

    // Se agrega path y method en este caso
    @RequestMapping(path = "/players", method = RequestMethod.POST)
    public ResponseEntity createPlayer(String username, String password){

        if (username.isEmpty()) {
            return new ResponseEntity<>("No name given", HttpStatus.FORBIDDEN);
        }

        if(playerRepo.findByUserName(username) != null){

            return new ResponseEntity<Map<String,Object>>(crearMapa("error","name in use"),HttpStatus.FORBIDDEN);
        }

            playerRepo.save(new Player(username,password));
            return new ResponseEntity<Map<String,Object>>(crearMapa("username",username),HttpStatus.CREATED);
    }

    private Map<String,Object> crearMapa(String string, Object object){

        Map<String,Object> mapa = new HashMap<>();
        mapa.put(string,object);
        return mapa;
    }
}





