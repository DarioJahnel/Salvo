package com.accenture.Salvo;

import com.sun.org.apache.xerces.internal.xs.StringList;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}


	//bean guarda una instancia para uso posterior, significa que se guarda hasat que empieza main y ahi se usa?
	@Bean
	public CommandLineRunner initData(PlayerRepository repository, GameRepository game, GamePlayerRepository gameplayer, ShipRepository ship) {
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
            List<String> lista1 = Arrays.asList("A1","B1");
            List<String> lista2 = Arrays.asList("A2","B2");
            List<String> lista3 = Arrays.asList("A3","B3");
            List<String> lista4 = Arrays.asList("A4","B4");

			Ship barco1 = new Ship("Destructor",lista1, gamePlayer1);
			Ship barco2 = new Ship("Velero",lista2, gamePlayer2);
            Ship barco3 = new Ship("Mojarrita",lista3, gamePlayer3);
            Ship barco4 = new Ship("Cangrejo",lista4, gamePlayer1);

            ship.save(barco1);
            ship.save(barco2);
            ship.save(barco3);
            ship.save(barco4);


//			Set<Ship> barcoLista1 = new HashSet<>();
//			barcoLista1.add(barco1);
//			barcoLista1.add(barco3);
//			barcoLista1.add(barco4);
//
//			Set<Ship> barcoLista2 = new HashSet<>();
//			barcoLista1.add(barco2);
//			barcoLista1.add(barco1);
//			barcoLista1.add(barco3);
//
//			Set<Ship> barcoLista3 = new HashSet<>();
//			barcoLista1.add(barco1);
//			barcoLista1.add(barco3);
//			barcoLista1.add(barco2);



		};
	}
	//commandlinerunner se ejecuta antes de que empiece la aplicacion, tambien existe applicationrunner

	//una vez que termina de iniciar la aplicacion, se ejecuta cualquier commandlinerunner que haya sigo guardado
	//(para esto usamos bean)
	//

}


