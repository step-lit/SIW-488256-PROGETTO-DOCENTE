package it.uniroma3.siw.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import it.uniroma3.siw.model.Utente;

public interface UtenteRepository extends CrudRepository<Utente, Long>{
	
	@Query(value = "SELECT * FROM utente WHERE email = :emailUtente", nativeQuery = true )
	public Utente findByEmail(@Param("emailUtente") String email);
	
}
