package com.accenture.Salvo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.Instant;
import java.util.*;


@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}


	//bean guarda una instancia para uso posterior, significa que se guarda hasat que empieza main y ahi se usa?
	@Bean
	public CommandLineRunner initData(PlayerRepository repository, GameRepository game, GamePlayerRepository gameplayer,
									  ShipRepository ship,SalvoRepository salvo) {
		//esto devuelve una instancia de commandlinerunner con un metodo run() que ejecuta los saves y se guarda luego
		//de inicializar tod

		return (args) -> {
			// save a couple of customers
			Player player1 = new Player("Jack", "Bauer", "jackbauer@gmail.com");
			Player player2 = new Player("Chloe", "O'Brian", "chloeobrian@gmail.com");
			Player player3 = new Player("Kim", "Bauer", "kimbauer@gmail.com");
			Player player4 = new Player("David", "Palmer", "davidpalmer@gmail.com");
			Player player5 = new Player("Michelle", "Dessler", "michelledessler@gmail.com");

			repository.save(player1);
			repository.save(player2);
			repository.save(player3);
			repository.save(player4);
			repository.save(player5);


			Game game1 = new Game(Date.from(Instant.now().plusSeconds(0)));
			Game game2 = new Game(Date.from(Instant.now().plusSeconds(3600)));
			Game game3 = new Game(Date.from(Instant.now().plusSeconds(7200)));

			game.save(game1);
			game.save(game2);
			game.save(game3);

			GamePlayer gamePlayer1 = new GamePlayer(player3, game2);
			GamePlayer gamePlayer4 = new GamePlayer(player1, game2);
			GamePlayer gamePlayer2 = new GamePlayer(player1, game1);
			GamePlayer gamePlayer3 = new GamePlayer(player5, game3);

			gameplayer.save(gamePlayer1);
			gameplayer.save(gamePlayer2);
			gameplayer.save(gamePlayer3);
			gameplayer.save(gamePlayer4);

//			new ArrayList<>(Arrays.asList("E1","F1","G1")) para hacerlo mas rapido
            List<String> lista1 = Arrays.asList("A1","B1","C1","D1","E1");
            List<String> lista2 = Arrays.asList("A2","A3","A4","A5");
            List<String> lista3 = Arrays.asList("A3","B3","C3");
            List<String> lista4 = Arrays.asList("A4","B4","C4");

			Ship carrier1 = new Ship("Carrier",lista1, gamePlayer1);
			Ship battleship1 = new Ship("Battleship",lista2, gamePlayer4);
            Ship submarine1 = new Ship("Submarine",lista3, gamePlayer4);
            Ship destroyer1 = new Ship("Destroyer",lista4, gamePlayer1);
            Ship patrolBoat1 = new Ship("PatrolBoat", new ArrayList<>(Arrays.asList("D3","E3")), gamePlayer1);

            ship.save(carrier1);
            ship.save(battleship1);
            ship.save(submarine1);
            ship.save(destroyer1);
            ship.save(patrolBoat1);

			Salvo salvo1 = new Salvo(1,new ArrayList<String>(Arrays.asList("A1")),gamePlayer4);
			Salvo salvo2 = new Salvo(2,new ArrayList<String>(Arrays.asList("B1")),gamePlayer4);
			Salvo salvo3 = new Salvo(3,new ArrayList<String>(Arrays.asList("C1")),gamePlayer4);
			Salvo salvo4 = new Salvo(4,new ArrayList<String>(Arrays.asList("E1")),gamePlayer4);

			salvo.save(salvo1);
			salvo.save(salvo2);
			salvo.save(salvo3);
			salvo.save(salvo4);




		};
	}
	//commandlinerunner se ejecuta antes de que empiece la aplicacion, tambien existe applicationrunner

	//una vez que termina de iniciar la aplicacion, se ejecuta cualquier commandlinerunner que haya sigo guardado
	//(para esto usamos bean)
	//

}


