package it.uniroma3.siw.security;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import it.uniroma3.siw.model.Credentials;

@Configuration
@EnableWebSecurity
public class AuthConfiguration {
	
    @Autowired
	private DataSource dataSource;

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.jdbcAuthentication()
			.dataSource(dataSource)
			.usersByUsernameQuery("SELECT username, password, 1 as enabled FROM credentials WHERE username=?")
			.authoritiesByUsernameQuery("SELECT username, role FROM credentials WHERE username=?");
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(); //di default la strength è 10
	}
	
	@Bean
	protected SecurityFilterChain configure(final HttpSecurity httpSecurity) throws Exception {

	    httpSecurity
	    .csrf(csrf -> csrf.disable())
	    .cors(cors -> cors.disable())
	    .authorizeHttpRequests(authorize -> authorize
	    // Regole di autorizzazione
	    .requestMatchers(HttpMethod.GET, "/", "/index", "/contatti", "/register", "/login", "/libri/**", "/autori/**", "/css/**", "/images/**", "/favicon.ico").permitAll()
	    .requestMatchers(HttpMethod.POST, "/login", "/register").permitAll()
	    // Tutte le altre richieste richiedono autenticazione
	    .anyRequest().authenticated()
	    )

	    .formLogin(formLogin -> formLogin
	    // Configurazione del form di login
	    .loginPage("/login")
	    .permitAll()
	    .defaultSuccessUrl("/index", true)
	    .failureUrl("/login?error=true")
	    )

	    .logout(logout -> logout
	    // Configurazione del logout
	    // Di default, il logout viene attivato da una richiesta POST a /logout
	    .logoutSuccessUrl("/")
	    .invalidateHttpSession(true)
	    .deleteCookies("JSESSIONID")
	    .clearAuthentication(true)
	    );

	    return httpSecurity.build();
	}
	
}