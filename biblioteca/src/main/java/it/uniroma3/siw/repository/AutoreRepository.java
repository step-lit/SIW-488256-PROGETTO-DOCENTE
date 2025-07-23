package it.uniroma3.siw.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import it.uniroma3.siw.model.Autore;

public interface AutoreRepository extends CrudRepository<Autore, Long>{
	
	 @Query("SELECT a FROM Autore a LEFT JOIN FETCH a.immagine")
	 public List<Autore> findAllWithImmagine();

	 @Query("SELECT a FROM Autore a LEFT JOIN FETCH a.libri l LEFT JOIN FETCH l.immagini WHERE a.id = :id")
	 public Optional<Autore> findByIdWithDetails(Long id);
	
}
