package it.uniroma3.siw.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.model.Autore;
import it.uniroma3.siw.model.Libro;
import it.uniroma3.siw.repository.AutoreRepository;

@Service
public class AutoreService {
	
	@Autowired
	private AutoreRepository autoreRepository;
	
	@Transactional(readOnly = true)
    public List<Autore> findAllWithImmagine() {
        return this.autoreRepository.findAllWithImmagine();
    }
	
	@Transactional(readOnly = true)
	public Iterable<Autore> findAll() {
		return autoreRepository.findAll();
	}
	
	@Transactional(readOnly = true)
    public Autore findByIdWithDetails(Long id) {
        return this.autoreRepository.findByIdWithDetails(id).orElse(null);
    }
	
	@Transactional(readOnly = true)
	public Autore findById(Long id) {
		return autoreRepository.findById(id).orElse(null);
	}
	
	@Transactional(readOnly = true)
	public Iterable<Autore> findAllById(List<Long> ids) {
		return autoreRepository.findAllById(ids);
	}
	
	@Transactional
	public void deleteById(Long id) {
        Autore autore = this.findByIdWithDetails(id);
        
        if (autore != null) {
            for (Libro libro : autore.getLibri()) {
                libro.getAutori().remove(autore);
            }

            autoreRepository.delete(autore);
        }
    }
	
	@Transactional
	public void save(Autore autore) {
        this.autoreRepository.save(autore);
    }
	
	
}
