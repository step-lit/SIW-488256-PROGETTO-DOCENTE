package it.uniroma3.siw.service;

import java.util.List;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import it.uniroma3.siw.model.Libro;
import it.uniroma3.siw.model.Autore;
import it.uniroma3.siw.repository.LibroRepository;


@Service
public class LibroService {
	
	@Autowired
	private LibroRepository libroRepository;
	
	@Transactional
    public List<Libro> findAllForCatalogo() {
        List<Libro> libri = (List<Libro>) this.libroRepository.findAll();
        
        for (Libro libro : libri) {
            Hibernate.initialize(libro.getImmagini());
            Hibernate.initialize(libro.getRecensioni());
        }
        return libri;
    }
	
	@Transactional
    public Libro findByIdWithDetails(Long id) {
        Libro libro = this.libroRepository.findById(id).orElse(null);
        if (libro != null) {
            Hibernate.initialize(libro.getImmagini());
            Hibernate.initialize(libro.getRecensioni());
            Hibernate.initialize(libro.getAutori());
        }
        return libro;
    }
	
	@Transactional
	public Iterable<Libro> findAllById(List<Long> ids) {
		return libroRepository.findAllById(ids);
	}
	
	@Transactional
	public void deleteById(Long id) {
        Libro libro = this.findByIdWithDetails(id);
        
        if (libro != null) {
            for (Autore autore : libro.getAutori()) {
                autore.getLibri().remove(libro);
            }
            libroRepository.delete(libro);
        }
    }
	
	@Transactional
	public void save(Libro libro) {
        this.libroRepository.save(libro);
    }
	
}