package com.accenture.Salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
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

    @Autowired
    private SalvoRepository salvoRepo;

    @Autowired
    private ScoreRepository scoreRepo;

    // GAMES
    @RequestMapping("/games") //cuando el controlador recibe pedido de url con /api, se ejecuta este metodo
    public Map<String, Object> getAllGames(Authentication authentication) {
        Map<String, Object> mapa = new HashMap<>();
        if (isGuest(authentication)) {
            mapa.put(APIConstants.PLAYER, "guest");
        } else {
            mapa.put(APIConstants.PLAYER, procesarPlayerAuthentication(authentication));
        }
        mapa.put("games", procesarGames(procesarGameRepo()));

        return mapa;

    }

    private Map<String, Object> procesarPlayerAuthentication(Authentication authentication) {
        Map<String, Object> mapa = new HashMap<>();
        Player player = playerRepo.findByUserName(authentication.getName());

        mapa.put("id", player.getId());
        mapa.put("name", player.getUserName());

        return mapa;
    }

    private Stream<Game> procesarGameRepo() {

        return gameRepo.findAll().stream();

    }

    private List<Map> procesarGames(Stream<Game> gameStream) {
        return gameStream.map(this::gamesDTO).collect(toList());
    }

    private Map<String, Object> gamesDTO(Game juego) {

        Map<String, Object> mapa = new HashMap<>();

        mapa.put("id", juego.getId());
        mapa.put("created", juego.getCreationDate());
        mapa.put("gamePlayers", procesarGamePlayer(juego.getGamePlayers()));
        mapa.put("scores", scoreList(juego));

        return mapa;
    }

    private List<Object> procesarGamePlayer(Set<GamePlayer> gp) {

        return gp.stream().map(this::gamePlayerApiDTO).collect(toList());
    }

    private Map<String, Object> gamePlayerApiDTO(GamePlayer gp) {

        Map<String, Object> mapa = new HashMap<>();

        mapa.put("id", gp.getId());
        mapa.put(APIConstants.PLAYER, playerDTO(gp.getPlayer()));

        return mapa;

    }

    private List<Map> scoreList(Game juego) {

        return juego.getScore().stream().map(this::scoreDTO).collect(toList());

    }

    private Map<String, Object> scoreDTO(Score s) {

        Map<String, Object> mapa = new HashMap<>();

        mapa.put("playerID", s.getPlayer().getId());
        mapa.put("score", s.getScore());
        mapa.put("finishDate", s.getFinishDate());

        return mapa;

    }

    // GAME VIEW
    @RequestMapping("/game_view/{gp}")
    public ResponseEntity<Map<String, Object>> findGamePlayer(@PathVariable Long gp, Authentication authentication) { //ANOTACION, machear lo que esta dentro de llaves, sino no lo reconoce como variable

        GamePlayer gplayer = gamePlayerRepo.findOne(gp);
        Player player = playerRepo.findByUserName(authentication.getName());

        if (gplayer.getPlayer() == player) {


            return new ResponseEntity(gameViewDTO(gplayer), HttpStatus.OK);
        }
        return new ResponseEntity(createMap(APIConstants.ERROR, "GameplayerID is not equal")
                , HttpStatus.UNAUTHORIZED);
    }

    private Map<String, Object> gameViewDTO(GamePlayer gp) {
        Map<String, Object> mapa = new HashMap<>();

        mapa.put("id", gp.getGame().getId());
        mapa.put("created", gp.getGame().getCreationDate());
        mapa.put("gamePlayers", procesarGamePlayerView(gp.getGame().getGamePlayers()));
        mapa.put("ships", procesarShips(gp.getShips()));
        mapa.put("salvoes", procesarSalvos(gp.getGame().getGamePlayers())); //ya le paso el stream completo de salvos del game
        mapa.put("hits", hitsDTO(gp));
        mapa.put("gameState", checkGameState(gp));
        return mapa;
    }

    private GameState checkGameState(GamePlayer gamePlayer1) {
        int sumatoria;
        int sumatoria2;
        Game game;
        game = gamePlayer1.getGame();

        gamePlayer1.setGameState(GameState.WAIT);
        if (game.getGamePlayers().size() == 1) {
            gamePlayer1.setGameState(GameState.WAITINGFOROPP);
        }
        if (gamePlayer1.getShips().size() == 0) {
            gamePlayer1.setGameState(GameState.PLACESHIPS);
        }

        // Para los siguientes estados, chequeo que haya 2 jugadores en la partida (sino tira null pointer )
        if (gamePlayer1.getGame().getGamePlayers().size() > 1) {

            // Consigo gamePlayer2
            GamePlayer gamePlayer2 = new GamePlayer();
            for (GamePlayer gamePlayer : game.getGamePlayers()) {
                if (gamePlayer.getId() != gamePlayer1.getId()) gamePlayer2 = gamePlayer;
            }

            // Consigo sumatoria de hits de ambos players
            sumatoria = sumatoriaHits(gamePlayer1,gamePlayer2);
            sumatoria2 = sumatoriaHits(gamePlayer2,gamePlayer1);

            // Consigo ultimo turno
            int lastTurn;
            lastTurn = gamePlayer2.getSalvoes().size() + 1;
            if (gamePlayer1.getSalvoes().size() > gamePlayer2.getSalvoes().size())
                lastTurn = gamePlayer1.getSalvoes().size();

            if ((gamePlayer1.getSalvoes().size() < lastTurn) && (gamePlayer1.getShips().size() > 0)
                    && (gamePlayer2.getShips().size() > 0) && (sumatoria != 17)) {
                gamePlayer1.setGameState(GameState.PLAY);
            }

            // Chequeo de TIE WON LOST
            if ((sumatoria == 17 || sumatoria2 ==17) && gamePlayer1.getSalvoes().size() ==
                    gamePlayer2.getSalvoes().size()) {
                    if (sumatoria == 17 && sumatoria2 == 17) {
                        gamePlayer1.setGameState(GameState.TIE);
                        gamePlayer2.setGameState(GameState.TIE);
                        if(game.getScore().size() == 0) {
                            scoreRepo.save(new Score(game, gamePlayer1.getPlayer(), 0.5f));
                            scoreRepo.save(new Score(game, gamePlayer2.getPlayer(), 0.5f));
                        }
                    }
                    if(sumatoria > sumatoria2) {
                        gamePlayer1.setGameState(GameState.WON);
                        gamePlayer2.setGameState(GameState.LOST);
                        if(game.getScore().size() == 0) {
                            scoreRepo.save(new Score(game, gamePlayer1.getPlayer(), 1f));
                            scoreRepo.save(new Score(game, gamePlayer2.getPlayer(), 0f));
                        }
                    }
                    else{
                        gamePlayer2.setGameState(GameState.WON);
                        gamePlayer1.setGameState(GameState.LOST);
                        if(game.getScore().size() == 0) {
                            scoreRepo.save(new Score(game, gamePlayer2.getPlayer(), 1f));
                            scoreRepo.save(new Score(game, gamePlayer1.getPlayer(), 0f));
                        }
                    }

                    }
            }
        return gamePlayer1.getGameState();
    }

    private Map<String, Object> hitsDTO(GamePlayer gamePlayer1) {
        Map<String, Object> mapa = new HashMap<>();
        //Chequear si hay GamePlayer2
        if (gamePlayer1.getGame().getGamePlayers().size() == 2) {
            GamePlayer gamePlayer2 = new GamePlayer();
            for (GamePlayer gamePlayer : gamePlayer1.getGame().getGamePlayers()) {
                if (gamePlayer != gamePlayer1) {
                    gamePlayer2 = gamePlayer;
                }
            }

            mapa.put("self", selfopponentDTO(gamePlayer1, gamePlayer2));
            mapa.put("opponent", selfopponentDTO(gamePlayer2, gamePlayer1));
            return mapa;
        }
        mapa.put("self", selfopponentDTO(gamePlayer1, new GamePlayer()));
        mapa.put("opponent", selfopponentDTO(new GamePlayer(), gamePlayer1));
        return mapa;
    }

    private List<Map> selfopponentDTO(GamePlayer gamePlayer1, GamePlayer gamePlayer2) {
        List<Map> mapList = new ArrayList<>();
        Set<Salvo> gp2salvos = gamePlayer2.getSalvoes();
        // Ordeno para que se muestren bien los turnos
        List<Salvo> gp2OrderedSalvoes = gp2salvos.stream().sorted(Comparator.comparing(Salvo::getTurn)).collect(toList());

        int carrier = 0;
        int battleship = 0;
        int submarine = 0;
        int destroyer = 0;
        int patrolboat = 0;


        for (Salvo salvo : gp2OrderedSalvoes) { // Cada salvo
            Map<String, Object> damagesMap = new HashMap<>();
            int carrierHits = 0;
            int battleshipHits = 0;
            int submarineHits = 0;
            int destroyerHits = 0;
            int patrolboatHits = 0;
            Map<String, Object> mapa = new HashMap<>();
            List<String> hitLocations = new ArrayList<>();
            mapa.put("turn", salvo.getTurn());
            int missed = 0;


            for (String salvoLocation : salvo.getLocation()) { // Cada salvo location
                boolean hit = false;
                for (Ship ship : gamePlayer1.getShips()) { // Cada ship
                    for (String shipLocation : ship.getLocation()) { // Cada ship location
                        if (salvoLocation == shipLocation) {
                            hit = true;
                            hitLocations.add(salvoLocation);
                            switch (ship.getType()) {
                                case ShipTypeConstants.CARRIER:
                                    carrierHits++;
                                    carrier++;
                                    break;
                                case ShipTypeConstants.BATTLESHIP:
                                    battleship++;
                                    battleshipHits++;
                                    break;
                                case ShipTypeConstants.SUBMARINE:
                                    submarine++;
                                    submarineHits++;
                                    break;
                                case ShipTypeConstants.DESTROYER:
                                    destroyer++;
                                    destroyerHits++;
                                    break;
                                case ShipTypeConstants.PATROLBOAT:
                                    patrolboat++;
                                    patrolboatHits++;
                                    break;
                            }
                        }
                        if (hit) break;
                    }
                    if (hit) break;
                }
                if (!hit) missed++;
            }
            damagesMap.put("carrierHits", carrierHits);
            damagesMap.put("battleshipHits", battleshipHits);
            damagesMap.put("submarineHits", submarineHits);
            damagesMap.put("destroyerHits", destroyerHits);
            damagesMap.put("patrolboatHits", patrolboatHits);
            damagesMap.put(ShipTypeConstants.CARRIER, carrier);
            damagesMap.put(ShipTypeConstants.BATTLESHIP, battleship);
            damagesMap.put(ShipTypeConstants.SUBMARINE, submarine);
            damagesMap.put(ShipTypeConstants.DESTROYER, destroyer);
            damagesMap.put(ShipTypeConstants.PATROLBOAT, patrolboat);

            mapa.put("hitLocations", hitLocations);
            mapa.put("damages", damagesMap);
            mapa.put("missed", missed);

            mapList.add(mapa);
        }
        return mapList;
    }

    private List<Map> procesarGamePlayerView(Set<GamePlayer> set) {

        return set.stream().map(this::gamePlayerDTO).collect(toList());

    }

    private Map<String, Object> gamePlayerDTO(GamePlayer gp) {
        Map<String, Object> mapa = new HashMap<>();

        mapa.put("id", gp.getId());
        mapa.put("player", playerDTO(gp.getPlayer()));
        mapa.put("joinDate", gp.getJoinDate());

        return mapa;
    }

    private Map<String, Object> playerDTO(Player p) {

        Map<String, Object> mapa = new HashMap<>();

        mapa.put("id", p.getId());
        mapa.put("email", p.getUserName());

        return mapa;
    }

    private List<Map> procesarShips(Set<Ship> ships) {

        return ships.stream().map(this::shipDTO).collect(toList());
    }

    private Map<String, Object> shipDTO(Ship ship) {

        Map<String, Object> mapa = new HashMap<>();

        mapa.put("type", ship.getType());
        mapa.put("locations", ship.getLocation());

        return mapa;
    }

    private List<Map> procesarSalvos(Set<GamePlayer> gp) {

        Stream<Salvo> ss = gp.stream().flatMap(g -> g.getSalvoes().stream());

        return ss.map(this::salvoDTO).collect(toList());
    }

    private Map<String, Object> salvoDTO(Salvo s) {

        Map<String, Object> mapa = new HashMap<>();

        mapa.put("turn", s.getTurn());
        mapa.put("player", s.getGamePlayer().getPlayer().getId());
        mapa.put("locations", s.getLocation());

        return mapa;

    }

    // LeaderBoard JSON
    @RequestMapping("/leaderBoard")
    public List<Object> leaderBoard() {

        return playerScoreList();


    }


    private List<Object> playerScoreList() {

        return playerRepo.findAll().stream().map(this::playerScoreDTO).collect(toList());
    }

    private Map<String, Object> playerScoreDTO(Player p) {

        Map<String, Object> mapa = new HashMap<>();
        Map<String, Object> mapa2 = new HashMap<>();

        mapa2.put("name", p.getUserName());
        mapa2.put("score", mapa);

        mapa.put("total", playerTotalScore(p));
        mapa.put("won", playerWinScore(p));
        mapa.put("lost", playerLossScore(p));
        mapa.put("tied", playerTiesScore(p));

        return mapa2;

    }

    private double playerTotalScore(Player p) {

        double total = 0;
        total = p.getScore().stream().mapToDouble(Score::getScore).sum();
        return total;
    }

    private Long playerWinScore(Player p) {

        Long wins;
        wins = p.getScore().stream().filter(s -> s.getScore() == 1).count();
        return wins;


    }

    private Long playerLossScore(Player p) {

        Long loss;
        loss = p.getScore().stream().filter(s -> s.getScore() == 0).count();
        return loss;

    }

    private Long playerTiesScore(Player p) {

        Long ties;
        ties = p.getScore().stream().filter(s -> s.getScore() == 0.5).count();
        return ties;
    }

    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }

    // Se agrega path y method en este caso
    @RequestMapping(path = "/players", method = RequestMethod.POST)
    public ResponseEntity createPlayer(String username, String password) {

        if (username.isEmpty()) {
            return new ResponseEntity<>(APIConstants.USERNAME_EMPTY, HttpStatus.FORBIDDEN);
        }

        if (playerRepo.findByUserName(username) != null) {

            return new ResponseEntity<Map<String, Object>>(createMap(APIConstants.ERROR,
                    APIConstants.USERNAME_DUPLICATED), HttpStatus.FORBIDDEN);
        }

        playerRepo.save(new Player(username, password));
        return new ResponseEntity<Map<String, Object>>(createMap("username", username), HttpStatus.CREATED);
    }


    // Create games
    @RequestMapping(path = "/games", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createGame(Authentication authentication) {

        if (!isGuest(authentication)) {
            Player player = playerRepo.findByUserName(authentication.getName());
            Game game = new Game();
            game.setCreationDate(new Date());
            GamePlayer gamePlayer = new GamePlayer(player, game);
            gameRepo.save(game);
            gamePlayerRepo.save(gamePlayer);
            return new ResponseEntity(createMap(APIConstants.GAMEPLAYERID, gamePlayer.getId()), HttpStatus.CREATED);
        }
        return new ResponseEntity(createMap(APIConstants.ERROR, APIConstants.USERNOTLOGGED), HttpStatus.FORBIDDEN);
    }

    private Map<String, Object> createMap(String string, Object object) {

        Map<String, Object> mapa = new HashMap<>();
        mapa.put(string, object);
        return mapa;
    }

    // Join games
    @RequestMapping(path = "/game/{gameId}/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> joinGame(@PathVariable Long gameId, Authentication authentication) {

        if (isGuest(authentication)) {
            return new ResponseEntity<>(createMap(APIConstants.ERROR, APIConstants.USERNOTLOGGED), HttpStatus.UNAUTHORIZED);
        }

        Player player = playerRepo.findByUserName(authentication.getName());
        Game game = gameRepo.findOne(gameId);
        if (game == null) {
            return new ResponseEntity<>(createMap(APIConstants.ERROR, APIConstants.NO_GAME), HttpStatus.FORBIDDEN);
        }
        if (game.getGamePlayers().size() == 2) {
            return new ResponseEntity<>(createMap(APIConstants.ERROR, APIConstants.GAME_FULL), HttpStatus.FORBIDDEN);
        }
        GamePlayer gamePlayer = new GamePlayer(player, game);
        gamePlayerRepo.save(gamePlayer);

        return new ResponseEntity<>(createMap(APIConstants.GAMEPLAYERID, gamePlayer.getId()), HttpStatus.CREATED);

    }

    // POSTShips
    @RequestMapping(path = "/games/players/{gamePlayerId}/ships", method = RequestMethod.POST)
    public ResponseEntity<Object> createShips(@PathVariable Long gamePlayerId, @RequestBody List<Ship> ships,
                                              Authentication authentication) {

        if (isGuest(authentication)) {
            return new ResponseEntity<>(createMap(APIConstants.ERROR, APIConstants.USERNOTLOGGED), HttpStatus.UNAUTHORIZED);
        }

        GamePlayer gamePlayer = gamePlayerRepo.findOne(gamePlayerId);
        if (gamePlayer == null) {
            return new ResponseEntity<>(createMap(APIConstants.ERROR, APIConstants.GAMEPLAYER_NOT_EXISTS)
                    , HttpStatus.UNAUTHORIZED);
        }

        Player player = playerRepo.findByUserName(authentication.getName());
        if (gamePlayer.getPlayer() != player) {
            return new ResponseEntity<>(createMap(APIConstants.ERROR, APIConstants.PLAYER_MISMATCH)
                    , HttpStatus.UNAUTHORIZED);
        }

    if (gamePlayer.getShips().size() == 0) {
        gamePlayer.addShips(ships);
        gamePlayerRepo.save(gamePlayer);
        return new ResponseEntity<>(createMap(APIConstants.OK, APIConstants.SHIPS_PLACED), HttpStatus.CREATED);
    }else
        return new ResponseEntity<>(createMap(APIConstants.ERROR, APIConstants.SHIPS_ALREADY_PLACED)
                , HttpStatus.FORBIDDEN);

    }

    // POSTSalvoes
    @RequestMapping(path = "/games/players/{gamePlayerId}/salvoes", method = RequestMethod.POST)
    public ResponseEntity<Object> getSalvoes(@PathVariable Long gamePlayerId, @RequestBody Salvo salvo,
                                             Authentication authentication) {

        if (isGuest(authentication)) {
            return new ResponseEntity<>(createMap(APIConstants.ERROR, APIConstants.USERNOTLOGGED), HttpStatus.UNAUTHORIZED);
        }

        GamePlayer gamePlayer = gamePlayerRepo.findOne(gamePlayerId);
        if (gamePlayer == null) {
            return new ResponseEntity<>
                    (createMap(APIConstants.ERROR, APIConstants.GAMEPLAYER_NOT_EXISTS), HttpStatus.UNAUTHORIZED);
        }

        Player player = playerRepo.findByUserName(authentication.getName());
        if (gamePlayer.getPlayer() != player) {
            return new ResponseEntity<>
                    (createMap(APIConstants.ERROR, APIConstants.PLAYER_MISMATCH), HttpStatus.UNAUTHORIZED);
        }
        // Consigo gameplayer2 para hacer el chequeo de que no envien mas de 1 salvo por turn
        GamePlayer gamePlayer2 = new GamePlayer();
        if(gamePlayer.getGame().getGamePlayers().size() == 2) {
            for (GamePlayer gp : gamePlayer.getGame().getGamePlayers()) {
                if (gp.getId() != gamePlayer.getId()) {
                    gamePlayer2 = gp;
                }
            }
            int gp1salvoes = gamePlayer.getSalvoes().size();
            int gp2salvoes = gamePlayer2.getSalvoes().size();
            //
            if (gp1salvoes > gp2salvoes)
                return new ResponseEntity<>
                        (createMap(APIConstants.ERROR,APIConstants.SALVOES_FIRED), HttpStatus.UNAUTHORIZED);

        }


        int turn = gamePlayer.getSalvoes().size() + 1;

        gamePlayer.addSalvo(salvo,turn);
        gamePlayerRepo.save(gamePlayer);

        return new ResponseEntity<>(createMap(APIConstants.CREATED, APIConstants.SALVOES_CREATED), HttpStatus.CREATED);
    }

    private int sumatoriaHits(GamePlayer gamePlayer2, GamePlayer gamePlayer1) {
        int sumatoria = 0;
        Set<Salvo> gp2salvos = gamePlayer2.getSalvoes();
        for (Salvo salvo : gp2salvos) { // Cada salvo
            for (String salvoLocation : salvo.getLocation()) { // Cada salvo location
                boolean hit = false;
                for (Ship ship : gamePlayer1.getShips()) { // Cada ship
                    for (String shipLocation : ship.getLocation()) { // Cada ship location
                        if (salvoLocation == shipLocation) {
                            hit = true;
                            sumatoria++;
                        }
                        if (hit) break;
                    }
                    if (hit) break;
                }
            }
        }
        return sumatoria;

    }
}





