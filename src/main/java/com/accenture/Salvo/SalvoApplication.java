package com.accenture.Salvo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.Instant;
import java.util.Date;


@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}


	//bean guarda una instancia para uso posterior, significa que se guarda hasat que empieza main y ahi se usa?
	@Bean
	public CommandLineRunner initData(PlayerRepository repository, GameRepository game, GamePlayerRepository gameplayer) {
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
			GamePlayer gamePlayer2 = new GamePlayer(player1, game1);
			GamePlayer gamePlayer3 = new GamePlayer(player5, game3);

			gameplayer.save(gamePlayer1);
			gameplayer.save(gamePlayer2);
			gameplayer.save(gamePlayer3);
		};
	}
	//commandlinerunner se ejecuta antes de que empiece la aplicacion, tambien existe applicationrunner

	//una vez que termina de iniciar la aplicacion, se ejecuta cualquier commandlinerunner que haya sigo guardado
	//(para esto usamos bean)
	//

}


