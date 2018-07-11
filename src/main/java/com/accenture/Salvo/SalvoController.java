package com.accenture.Salvo;

import org.apache.tomcat.util.http.parser.HttpParser;
import org.omg.CosNaming.NamingContextExtPackage.StringNameHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.*;

import javax.security.sasl.AuthorizeCallback;
import javax.xml.stream.Location;
import java.util.*;
import java.util.stream.Collectors;
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

    @Autowired
    private ShipRepository shipRepo;

    @Autowired
    private SalvoRepository salvoRepo;


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
   public ResponseEntity<Map<String,Object>> findGamePlayer(@PathVariable Long gp,Authentication authentication){ //ANOTACION, machear lo que esta dentro de llaves, sino no lo reconoce como variable

       GamePlayer gplayer  = gamePlayerRepo.findOne(gp);
       Player player = playerRepo.findByUserName(authentication.getName());

       if(gplayer.getPlayer() == player){


           return new ResponseEntity(game_viewDTO(gplayer),HttpStatus.OK);
       }
        return new ResponseEntity(crearMapa("error", "GameplayerID is not equal"),HttpStatus.UNAUTHORIZED);
   }

    private Map<String, Object> game_viewDTO(GamePlayer gp) {
        Map<String, Object> mapa = new HashMap<>();

        mapa.put("id", gp.getGame().getId());
        mapa.put("created", gp.getGame().getCreationDate());
        mapa.put("gamePlayers", procesarGamePlayerView(gp.getGame().getGamePlayers()));
        mapa.put("ships",procesarShips(gp.getShips()) );
        mapa.put("salvoes", procesarSalvos(gp.getGame().getGamePlayers())); //ya le paso el stream completo de salvos del game
        mapa.put("hits", hitsDTO(gp));
        mapa.put("gameState", checkGameState(gp));
        return mapa;
        }

    private GameState checkGameState(GamePlayer gamePlayer1){

        Game game = new Game();
        game = gamePlayer1.getGame();
        GameState previousP1 = gamePlayer1.getGameState();

        gamePlayer1.setGameState(GameState.WAIT);
        if(game.getGamePlayers().size() == 1){
            gamePlayer1.setGameState(GameState.WAITINGFOROPP);
        }
        if(gamePlayer1.getShips().size() == 0){
            gamePlayer1.setGameState(GameState.PLACESHIPS);
        }
        // Para los siguientes estados, chequeo que haya 2 jugadores en la partida (sino tira null pointer )
        if(gamePlayer1.getGame().getGamePlayers().size() > 1) {
            // Consigo gamePlayer2
            GamePlayer gamePlayer2 = new GamePlayer();
            for (GamePlayer gamePlayer : game.getGamePlayers()) {
                if (gamePlayer.getId() != gamePlayer1.getId()) gamePlayer2 = gamePlayer;
            }
            int lastTurn;
            lastTurn = gamePlayer2.getSalvoes().size() + 1;
            if (gamePlayer1.getSalvoes().size() > gamePlayer2.getSalvoes().size())
                lastTurn = gamePlayer1.getSalvoes().size();
            if (gamePlayer1.getSalvoes().size() < lastTurn && gamePlayer1.getShips().size() > 0 && gamePlayer2.getShips().size() > 0) {
                gamePlayer1.setGameState(GameState.PLAY);
            }
            //        if(gamePlayer1.getSalvoes().size() > gamePlayer2.getSalvoes().size()){
            //            gamePlayer1.setGameState(GameState.WAIT);
            //        }
            if (gamePlayer2.getGameState() == GameState.PROCESSING) {
                if (previousP1 == GameState.PROCESSING) {
                    gamePlayer1.setGameState(GameState.TIE);
                    gamePlayer2.setGameState(GameState.TIE);
                }
                if (previousP1 == GameState.WAIT){
                    gamePlayer2.setGameState(GameState.PROCESSING);
                    return gamePlayer1.getGameState();
                }
                gamePlayer1.setGameState(GameState.LOST);
                gamePlayer2.setGameState(GameState.WON);
            }
            if(previousP1 == GameState.PROCESSING){
                if(gamePlayer2.getGameState() == GameState.PROCESSING){
                    gamePlayer1.setGameState(GameState.TIE);
                    gamePlayer2.setGameState(GameState.TIE);
                }
                if (gamePlayer2.getGameState() == GameState.WAIT){
                    gamePlayer1.setGameState(GameState.PROCESSING);
                    return gamePlayer1.getGameState();
                }

                gamePlayer1.setGameState(GameState.WON);
                gamePlayer2.setGameState(GameState.LOST);

            }
        }
        return gamePlayer1.getGameState();
    }

    private Map<String,Object> hitsDTO(GamePlayer gamePlayer1){
        Map<String, Object> mapa = new HashMap<>();
        Boolean self; //uso para chequear si es self u opponent
        //Chequear si hay GamePlayer2
        if (gamePlayer1.getGame().getGamePlayers().size() == 2) {
            GamePlayer gamePlayer2 = new GamePlayer();
            for (GamePlayer gamePlayer: gamePlayer1.getGame().getGamePlayers()){
                if(gamePlayer != gamePlayer1){
                    gamePlayer2 = gamePlayer;
                }
            }

            mapa.put("self", selfopponentDTO(gamePlayer1, gamePlayer2, self = true));
            mapa.put("opponent", selfopponentDTO(gamePlayer2, gamePlayer1, self = false));
            return mapa;
        }
        mapa.put("self", selfopponentDTO(gamePlayer1, new GamePlayer(), self = true));
        mapa.put("opponent", selfopponentDTO(new GamePlayer(), gamePlayer1, self = false));
        return mapa;
    }

    private List<Map> selfopponentDTO(GamePlayer gamePlayer1, GamePlayer gamePlayer2,Boolean self){
            List<Map> mapList = new ArrayList<>();
            Set<Salvo> gp2salvos = gamePlayer2.getSalvoes();
            int carrier = 0, battleship = 0, submarine = 0, destroyer = 0, patrolboat = 0;


            for (Salvo salvo : gp2salvos) { // Cada salvo
                Map<String, Object> damagesMap = new HashMap<>();
                int carrierHits = 0, battleshipHits = 0, submarineHits = 0, destroyerHits = 0, patrolboatHits = 0, missed = 0;
                Map<String, Object> mapa = new HashMap<>();
                List<String> hitLocations = new ArrayList<>();
                mapa.put("turn", salvo.getTurn());
                missed = 0;


                for (String salvoLocation : salvo.getLocation()) { // Cada salvo location
                    boolean hit = false;
                    for (Ship ship : gamePlayer1.getShips()) { // Cada ship
                        for (String shipLocation : ship.getLocation()) { // Cada ship location
                            if (salvoLocation == shipLocation) {
                                hit = true;
                                hitLocations.add(salvoLocation);
                                switch (ship.getType()) {
                                    case "carrier":
                                        carrierHits++;
                                        carrier++;
                                        break;
                                    case "battleship":
                                        battleship++;
                                        battleshipHits++;
                                        break;
                                    case "submarine":
                                        submarine++;
                                        submarineHits++;
                                        break;
                                    case "destroyer":
                                        destroyer++;
                                        destroyerHits++;
                                        break;
                                    case "patrolboat":
                                        patrolboat++;
                                        patrolboatHits++;
                                        break;
                                }
                            }
                        if(hit == true) break;
                        }
                    if(hit == true) break;
                    }
                    if (!hit) missed++;
                }
                damagesMap.put("carrierHits", carrierHits);
                damagesMap.put("battleshipHits", battleshipHits);
                damagesMap.put("submarineHits", submarineHits);
                damagesMap.put("destroyerHits", destroyerHits);
                damagesMap.put("patrolboatHits", patrolboatHits);
                damagesMap.put("carrier", carrier);
                damagesMap.put("battleship", battleship);
                damagesMap.put("submarine", submarine);
                damagesMap.put("destroyer", destroyer);
                damagesMap.put("patrolboat", patrolboat);

                mapa.put("hitLocations", hitLocations);
                mapa.put("damages", damagesMap);
                mapa.put("missed", missed);

                if ((carrier == 5) && (battleship == 4) && (submarine == 3) && (destroyer == 3) && (patrolboat == 2)) {
                    if(self) {
                        gamePlayer1.setGameState(GameState.PROCESSING);
                    }
                    if(!self){
                        gamePlayer2.setGameState(GameState.PROCESSING);
                    }
                }

                mapList.add(mapa);

            }


            return mapList;
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


    // Create games
    @RequestMapping(path = "/games",method = RequestMethod.POST)
    public ResponseEntity<Map<String,Object>> createGame(Authentication authentication){

        if(isGuest(authentication) == false){
            Player player = playerRepo.findByUserName(authentication.getName());
            Game game = new Game();
            game.setCreationDate(new Date());
            GamePlayer gamePlayer = new GamePlayer(player,game);
            gameRepo.save(game);
            gamePlayerRepo.save(gamePlayer);
            return new ResponseEntity(crearMapa("gpid",gamePlayer.getId()),HttpStatus.CREATED);
        }
        return new ResponseEntity(crearMapa("Error","User is not logged in"),HttpStatus.FORBIDDEN);
    }
    private Map<String,Object> crearMapa(String string, Object object){

        Map<String,Object> mapa = new HashMap<>();
        mapa.put(string,object);
        return mapa;
    }

    // Join games
    @RequestMapping(path = "/game/{gameId}/players",method = RequestMethod.POST)
    public ResponseEntity<Map<String,Object>>joinGame(@PathVariable Long gameId,Authentication authentication){

        if(isGuest(authentication)){
            return new ResponseEntity<>(crearMapa("Error","Player not logged in"),HttpStatus.UNAUTHORIZED);}

        Player player = playerRepo.findByUserName(authentication.getName());
        Game game = gameRepo.findOne(gameId);
        if(game == null){
            return new ResponseEntity<>(crearMapa("Error","No such game"),HttpStatus.FORBIDDEN);
        }
        if(game.getGamePlayers().size() == 2){
            return new ResponseEntity<>(crearMapa("Error","Game is full"),HttpStatus.FORBIDDEN);
        }
        GamePlayer gamePlayer = new GamePlayer(player,game);
        gamePlayerRepo.save(gamePlayer);

        return new ResponseEntity<>(crearMapa("gpid",gamePlayer.getId()),HttpStatus.CREATED);

    }

    // POSTShips
    @RequestMapping(path = "/games/players/{gamePlayerId}/ships",method = RequestMethod.POST)
    public ResponseEntity<Object> createShips(@PathVariable Long gamePlayerId, @RequestBody List<Ship> ships,
                                                       Authentication authentication){

        if(isGuest(authentication)){
            return new ResponseEntity<>(crearMapa("Error","User not logged in"), HttpStatus.UNAUTHORIZED);
        }

        GamePlayer gamePlayer = gamePlayerRepo.findOne(gamePlayerId);
        if(gamePlayer == null){
            return new ResponseEntity<>(crearMapa("Error","Gameplayer doesn't exist"), HttpStatus.UNAUTHORIZED);
        }

        Player player = playerRepo.findByUserName(authentication.getName());
        if(gamePlayer.getPlayer()!= player){
            return new ResponseEntity<>(crearMapa("Error","Player mismatch"), HttpStatus.UNAUTHORIZED);
        }

        //ToDo: Insertar codigo para chequear si ya tiene ships colocados


        for (int i=0; i< ships.size(); i++){

            Ship ship = ships.get(i);
            ship.setGamePlayer(gamePlayer);
            shipRepo.save(ship);

        }

        return new ResponseEntity<>(crearMapa("OK","Ships placed"),HttpStatus.CREATED);

    }
    // POSTSalvoes
    @RequestMapping(path = "/games/players/{gamePlayerId}/salvoes",method = RequestMethod.POST)
    public ResponseEntity<Object> getSalvoes(@PathVariable Long gamePlayerId, @RequestBody Salvo salvo,
                                           Authentication authentication){

        if(isGuest(authentication)){
            return new ResponseEntity<>(crearMapa("Error","User not logged in"), HttpStatus.UNAUTHORIZED);
        }

        GamePlayer gamePlayer = gamePlayerRepo.findOne(gamePlayerId);
        if(gamePlayer == null){
            return new ResponseEntity<>(crearMapa("Error","Gameplayer doesn't exist"), HttpStatus.UNAUTHORIZED);
        }

        Player player = playerRepo.findByUserName(authentication.getName());
        if(gamePlayer.getPlayer()!= player){
            return new ResponseEntity<>(crearMapa("Error","Player mismatch"), HttpStatus.UNAUTHORIZED);
        }

        //ToDo: Insertar codigo para chequear si ya tiene salvoes enviados este turno recordar lo que le dije a damian


        int turno = gamePlayer.getSalvoes().size() + 1;
        salvo.setGamePlayer(gamePlayer);
        salvo.setTurn(turno);
        salvoRepo.save(salvo);

        return new ResponseEntity<>(crearMapa("CREATED","Salvoes created"),HttpStatus.CREATED);
    }

}





