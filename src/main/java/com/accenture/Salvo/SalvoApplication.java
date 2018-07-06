package com.accenture.Salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
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
									  ShipRepository ship,SalvoRepository salvo, ScoreRepository score) {
		//esto devuelve una instancia de commandlinerunner con un metodo run() que ejecuta los saves y se guarda luego
		//de inicializar tod

		return (args) -> {
			// save a couple of customers
			Player jack = new Player("j.bauer@ctu.gov", "24");
			Player chloe = new Player(" c.obrian@ctu.gov", "42");
			Player kim = new Player("kim_bauer@gmail.com", "kb");
			Player david = new Player("davidpalmer@gmail.com","dp");
			Player miche = new Player("michelledessler@gmail.com","md");
			Player tony = new Player("t.almeida@ctu.gov","mole");

			repository.save(jack);
			repository.save(chloe);
			repository.save(kim);
			repository.save(david);
			repository.save(miche);
			repository.save(tony);


			Game game1 = new Game(Date.from(Instant.now().plusSeconds(0)));
			Game game2 = new Game(Date.from(Instant.now().plusSeconds(3600)));
			Game game3 = new Game(Date.from(Instant.now().plusSeconds(7200)));
			Game game4 = new Game(Date.from(Instant.now().plusSeconds(0)));
			Game game5 = new Game(Date.from(Instant.now().plusSeconds(3600)));
			Game game6 = new Game(Date.from(Instant.now().plusSeconds(7200)));
			Game game7 = new Game(Date.from(Instant.now().plusSeconds(0)));
			Game game8 = new Game(Date.from(Instant.now().plusSeconds(3600)));


			game.save(game1);
			game.save(game2);
			game.save(game3);
			game.save(game4);
			game.save(game5);
			game.save(game6);
			game.save(game7);
			game.save(game8);

			GamePlayer gamePlayer1 = new GamePlayer(jack, game1);
			GamePlayer gamePlayer2 = new GamePlayer(chloe, game1);

			GamePlayer gamePlayer3 = new GamePlayer(jack, game2);
			GamePlayer gamePlayer4 = new GamePlayer(chloe, game2);

			GamePlayer gamePlayer5 = new GamePlayer(chloe, game3);
			GamePlayer gamePlayer6 = new GamePlayer(tony, game3);

			GamePlayer gamePlayer7 = new GamePlayer(chloe, game4);
			GamePlayer gamePlayer8 = new GamePlayer(jack, game4);

			GamePlayer gamePlayer9 = new GamePlayer(tony, game5);
			GamePlayer gamePlayer10 = new GamePlayer(jack, game5);

			GamePlayer gamePlayer11 = new GamePlayer(kim, game6);

			GamePlayer gamePlayer12 = new GamePlayer(tony, game7);

			GamePlayer gamePlayer13 = new GamePlayer(kim, game8);
			GamePlayer gamePlayer14 = new GamePlayer(tony, game8);



			gameplayer.save(gamePlayer1);
			gameplayer.save(gamePlayer2);
			gameplayer.save(gamePlayer3);
			gameplayer.save(gamePlayer4);
			gameplayer.save(gamePlayer5);
			gameplayer.save(gamePlayer6);
			gameplayer.save(gamePlayer7);
			gameplayer.save(gamePlayer8);
			gameplayer.save(gamePlayer9);
			gameplayer.save(gamePlayer10);
			gameplayer.save(gamePlayer11);
			gameplayer.save(gamePlayer12);
			gameplayer.save(gamePlayer13);
			gameplayer.save(gamePlayer14);

//			new ArrayList<>(Arrays.asList("E1","F1","G1")) para hacerlo mas rapido
			List<Ship> shipList = new ArrayList<>();
			//creating ships
			Ship ship1 = new Ship();
			Ship ship2 = new Ship();
			Ship ship3 = new Ship();
			Ship ship4 = new Ship();
			Ship ship5 = new Ship();
			Ship ship6 = new Ship();
			Ship ship7 = new Ship();
			Ship ship8 = new Ship();
			Ship ship9 = new Ship();
			Ship ship10 = new Ship();
			Ship ship11 = new Ship();
			Ship ship12 = new Ship();
			Ship ship13 = new Ship();
			Ship ship14 = new Ship();
			Ship ship15 = new Ship();
			Ship ship16 = new Ship();
			Ship ship17 = new Ship();
			Ship ship18 = new Ship();
			Ship ship19 = new Ship();
			Ship ship20 = new Ship();
			Ship ship21 = new Ship();
			Ship ship22 = new Ship();
			Ship ship23 = new Ship();
			Ship ship24 = new Ship();
			Ship ship25 = new Ship();
			Ship ship26 = new Ship();
			Ship ship27 = new Ship();
			//setting ship stats
			String[] locations1 = {"H2","H3","H4"};
			ship1.setType("Destroyer");
			ship1.addLocations(locations1);
			String[] locations2 = {"E1","F1","G1"};
			ship2.setType("Submarine");
			ship2.addLocations(locations2);
			String[] locations3 = {"B4","B5"};
			ship3.setType("Patrol Boat");
			ship3.addLocations(locations3);
			String[] locations4 = {"B5","C5","D5"};
			ship4.setType("Destroyer");
			ship4.addLocations(locations4);
			String[] locations5 = {"F1","F2"};
			ship5.setType("Patrol Boat");
			ship5.addLocations(locations5);
			String[] locations6 = {"B5","C5","D5"};
			ship6.setType("Destoyer");
			ship6.addLocations(locations6);
			String[] locations7 = {"C6","C7"};
			ship7.setType("Patrol Boat");
			ship7.addLocations(locations7);
			String[] locations8 = {"A2","A3","A4"};
			ship8.setType("Submarine");
			ship8.addLocations(locations8);
			String[] locations9 = {"G6","H6"};
			ship9.setType("Patrol Boat");
			ship9.addLocations(locations9);
			String[] locations10 = {"B5","C5","D5"};
			ship10.setType("Destroyer");
			ship10.addLocations(locations10);
			String[] locations11 = {"C6","C7"};
			ship11.setType("Patrol Boat");
			ship11.addLocations(locations11);
			String[] locations12 = {"A2","A3","A4"};
			ship12.setType("Submarine");
			ship12.addLocations(locations12);
			String[] locations13 = {"G6","H6"};
			ship13.setType("Patrol Boat");
			ship13.addLocations(locations13);
			String[] locations14 = {"B5","C5","D5"};
			ship14.setType("Destroyer");
			ship14.addLocations(locations14);
			String[] locations15 = {"C6","C7"};
			ship15.setType("Patrol Boat");
			ship15.addLocations(locations15);
			String[] locations16 = {"A2","A3","A4"};
			ship16.setType("Submarine");
			ship16.addLocations(locations16);
			String[] locations17 = {"G6","H6"};
			ship17.setType("Patrol Boat");
			ship17.addLocations(locations17);
			String[] locations18 = {"B5","C5","D5"};
			ship18.setType("Destroyer");
			ship18.addLocations(locations18);
			String[] locations19 = {"C6","C7"};
			ship19.setType("Patrol Boat");
			ship19.addLocations(locations19);
			String[] locations20 = {"A2","A3","A4"};
			ship20.setType("Submarine");
			ship20.addLocations(locations20);
			String[] locations21 = {"G6","H6"};
			ship21.setType("Patrol Boat");
			ship21.addLocations(locations21);
			String[] locations22 = {"B5","C5","D5"};
			ship22.setType("Destroyer");
			ship22.addLocations(locations22);
			String[] locations23 = {"C6","C7"};
			ship23.setType("Patrol Boat");
			ship23.addLocations(locations23);
			String[] locations24 = {"B5","C5","D5"};
			ship24.setType("Destoyer");
			ship24.addLocations(locations24);
			String[] locations25 = {"C6","C7"};
			ship25.setType("Patrol Boat");
			ship25.addLocations(locations25);
			String[] locations26 = {"A2","A3","A4"};
			ship26.setType("Submarine");
			ship26.addLocations(locations26);
			String[] locations27 = {"G6","H6"};
			ship27.setType("Patrol Boat");
			ship27.addLocations(locations27);

			ship1	.setGamePlayer(	gamePlayer1	);
			ship2	.setGamePlayer(	gamePlayer1	);
			ship3	.setGamePlayer(	gamePlayer1	);
			ship4	.setGamePlayer(	gamePlayer2	);
			ship5	.setGamePlayer(	gamePlayer2	);
			ship6	.setGamePlayer(	gamePlayer3	);
			ship7	.setGamePlayer(	gamePlayer3	);
			ship8	.setGamePlayer(	gamePlayer4	);
			ship9	.setGamePlayer(	gamePlayer4	);
			ship10	.setGamePlayer(	gamePlayer5	);
			ship11	.setGamePlayer(	gamePlayer5	);
			ship12	.setGamePlayer(	gamePlayer6	);
			ship13	.setGamePlayer(	gamePlayer6	);
			ship14	.setGamePlayer(	gamePlayer7	);
			ship15	.setGamePlayer(	gamePlayer7	);
			ship16	.setGamePlayer(	gamePlayer8	);
			ship17	.setGamePlayer(	gamePlayer8	);
			ship18	.setGamePlayer(	gamePlayer9	);
			ship19	.setGamePlayer(	gamePlayer9	);
			ship20	.setGamePlayer(	gamePlayer10	);
			ship21	.setGamePlayer(	gamePlayer10	);
			ship22	.setGamePlayer(	gamePlayer11	);
			ship23	.setGamePlayer(	gamePlayer11	);
			ship24	.setGamePlayer(	gamePlayer13	);
			ship25	.setGamePlayer(	gamePlayer13	);
			ship26	.setGamePlayer(	gamePlayer14	);
			ship27	.setGamePlayer(	gamePlayer14	);



			ship.save(ship1);
			ship.save(ship2);
			ship.save(ship3);
			ship.save(ship4);
			ship.save(ship5);
			ship.save(ship6);
			ship.save(ship7);
			ship.save(ship8);
			ship.save(ship9);
			ship.save(ship10);
			ship.save(ship11);
			ship.save(ship12);
			ship.save(ship13);
			ship.save(ship14);
			ship.save(ship15);
			ship.save(ship16);
			ship.save(ship17);
			ship.save(ship18);
			ship.save(ship19);
			ship.save(ship20);
			ship.save(ship21);
			ship.save(ship22);
			ship.save(ship23);
			ship.save(ship24);
			ship.save(ship25);
			ship.save(ship26);
			ship.save(ship27);

			Salvo salvo1 = new Salvo(1,new ArrayList<String>(Arrays.asList("B5", "C5", "F1")),gamePlayer1);
			Salvo salvo2 = new Salvo(1,new ArrayList<String>(Arrays.asList("B4", "B5", "B6")),gamePlayer2);
			Salvo salvo3 = new Salvo(2,new ArrayList<String>(Arrays.asList("F2", "D5")),gamePlayer1);
			Salvo salvo4 = new Salvo(2,new ArrayList<String>(Arrays.asList("E1", "H3", "A2")),gamePlayer2);

			Salvo salvo5 = new Salvo(1,new ArrayList<String>(Arrays.asList("A2", "A4", "G6")),gamePlayer3);
			Salvo salvo6 = new Salvo(1,new ArrayList<String>(Arrays.asList("B5", "D5", "C7")),gamePlayer4);
			Salvo salvo7 = new Salvo(2,new ArrayList<String>(Arrays.asList("A3", "H6")),gamePlayer3);
			Salvo salvo8 = new Salvo(2,new ArrayList<String>(Arrays.asList("C5", "C6")),gamePlayer4);

			Salvo salvo9 = new Salvo(1,new ArrayList<String>(Arrays.asList("G6", "H6", "A4")),gamePlayer5);
			Salvo salvo10 = new Salvo(1,new ArrayList<String>(Arrays.asList("H1", "H2", "H3")),gamePlayer6);
			Salvo salvo11 = new Salvo(2,new ArrayList<String>(Arrays.asList("A2", "A3", "D8")),gamePlayer5);
			Salvo salvo12 = new Salvo(2,new ArrayList<String>(Arrays.asList("E1", "F2", "G3")),gamePlayer6);

			Salvo salvo13 = new Salvo(1,new ArrayList<String>(Arrays.asList("A3", "A4", "F7")),gamePlayer7);
			Salvo salvo14 = new Salvo(2,new ArrayList<String>(Arrays.asList("B5", "C6", "H1")),gamePlayer8);
			Salvo salvo15 = new Salvo(2,new ArrayList<String>(Arrays.asList("A2", "G6", "H6")),gamePlayer7);
			Salvo salvo16 = new Salvo(2,new ArrayList<String>(Arrays.asList("C5", "C7", "D5")),gamePlayer8);

			Salvo salvo17 = new Salvo(1,new ArrayList<String>(Arrays.asList("A1", "A2", "A3")),gamePlayer9);
			Salvo salvo18 = new Salvo(1,new ArrayList<String>(Arrays.asList("B5", "B6", "C7")),gamePlayer10);
			Salvo salvo19 = new Salvo(2,new ArrayList<String>(Arrays.asList("G6", "G7", "G8")),gamePlayer9);
			Salvo salvo20 = new Salvo(2,new ArrayList<String>(Arrays.asList("C6", "D6", "E6")),gamePlayer10);
			Salvo salvo21 = new Salvo(3,new ArrayList<String>(Arrays.asList("H1", "H8")),gamePlayer9);

			salvo.save(salvo1);
			salvo.save(salvo2);
			salvo.save(salvo3);
			salvo.save(salvo4);
			salvo.save(salvo5);
			salvo.save(salvo6);
			salvo.save(salvo7);
			salvo.save(salvo8);
			salvo.save(salvo9);
			salvo.save(salvo10);
			salvo.save(salvo11);
			salvo.save(salvo12);
			salvo.save(salvo13);
			salvo.save(salvo14);
			salvo.save(salvo15);
			salvo.save(salvo16);
			salvo.save(salvo17);
			salvo.save(salvo18);
			salvo.save(salvo19);
			salvo.save(salvo20);
			salvo.save(salvo21);

			Score score1 = new Score(game1,jack,1f);
			Score score2 = new Score(game1,chloe,0f);
			Score score3 = new Score(game2,jack,0.5f);
			Score score4 = new Score(game2,chloe,0.5f);
			Score score5 = new Score(game3,chloe,1f);
			Score score6 = new Score(game3,tony,0f);
			Score score7 = new Score(game4,chloe,0.5f);
			Score score8 = new Score(game4,tony,0.5f);

			score.save(score1);
			score.save(score2);
			score.save(score3);
			score.save(score4);
			score.save(score5);
			score.save(score6);
			score.save(score7);
			score.save(score8);





		};
	}
	//commandlinerunner se ejecuta antes de que empiece la aplicacion, tambien existe applicationrunner

	//una vez que termina de iniciar la aplicacion, se ejecuta cualquier commandlinerunner que haya sigo guardado
	//(para esto usamos bean)
	//

}

@Configuration
class GlobalAuthConfig extends GlobalAuthenticationConfigurerAdapter {

    @Autowired
    PlayerRepository playerRepo;

    @Override
    public void init(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(inputName-> {
            Player player = playerRepo.findByUserName(inputName);
            if (player != null) {
                return new User(player.getUserName(), player.getPassword(),
                        AuthorityUtils.createAuthorityList("USER"));
            } else {
                throw new UsernameNotFoundException("Unknown user: " + inputName);
            }
        });
    }
}

@EnableWebSecurity
@Configuration
class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                //si pongo api con acceso admin, no me anda porque tira los request con el usuario actual
                .antMatchers("/rest/**").hasAuthority("ADMIN")
                .antMatchers("/api/game_view*").hasAuthority("USER")
                .antMatchers("/web/game_2.html*").hasAuthority("USER")
				.antMatchers("/api/**").permitAll()
				.antMatchers("/web/**").permitAll()
				.anyRequest().authenticated();

		// Configuro el login, los parametros que recibe (JSON) y su URL
        http.formLogin()
                .usernameParameter("username")
                .passwordParameter("password")
                .loginPage("/api/login");

        // Configuro logout, su URL
        http.logout().logoutUrl("/api/logout");



        // turn off checking for CSRF tokens
        http.csrf().disable();

        // esto de aca abajo se usa para evitar enviar HTML al browser (y triggerear correctamente el login)

        // if user is not authenticated, just send an authentication failure response
        http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

        // if login is successful, just clear the flags asking for authentication
        http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));

        // if login fails, just send an authentication failure response
        http.formLogin().failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

        // if logout is successful, just send a success response
        http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());
    }


    private void clearAuthenticationAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        }
    }
}


