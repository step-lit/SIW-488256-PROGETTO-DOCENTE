package it.uniroma3.siw.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Utente;
import it.uniroma3.siw.repository.UtenteRepository;

@Service
public class UtenteService {
	
	@Autowired
	private UtenteRepository utenteRepository;
	
	public Utente findById(Long id) {
		return this.utenteRepository.findById(id).get();
	}
	
	public Utente save(Utente utente) {
		return utenteRepository.save(utente);
	}
	
	public boolean existsByEmail(String email) {
		return utenteRepository.findByEmail(email) != null;
	}
	
}
