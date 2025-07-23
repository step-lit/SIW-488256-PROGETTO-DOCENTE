package it.uniroma3.siw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Utente;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.UtenteService;
import jakarta.validation.Valid;

@Controller
public class AuthController {
	
	@Autowired
	private CredentialsService credentialsService;
	@Autowired
	private UtenteService utenteService;
	@Autowired
	private PasswordEncoder passwordEncoder;

	@GetMapping("/register") 
	public String newRegister(Model model){
		model.addAttribute("utente", new Utente());
		model.addAttribute("credentials", new Credentials());
		
		return "register.html";
	}
	
	@PostMapping("/register")
	public String completeRegister(@Valid @ModelAttribute("utente") Utente utente, BindingResult utenteBindingResult,
								   @Valid @ModelAttribute("credentials") Credentials credentials, BindingResult credentialsBindingResult,
								   RedirectAttributes redirectAttributes, Model model) {
		
		boolean errore = false;
		
		//se esiste già un paziente con l'email inserita
		if(utenteService.existsByEmail(utente.getEmail())) {
			model.addAttribute("erroreMail", "Errore: l'email inserita è già esistente.");
			errore = true;
		}
		
		//se l'username appartiene già a qualche utente
		if (credentialsService.findByUsername(credentials.getUsername()) != null) {
			model.addAttribute("erroreUsername", "Errore: l'username inserito è già esistente.");
			errore = true;
        }
		
		if (utenteBindingResult.hasErrors() || credentialsBindingResult.hasErrors()) {
			model.addAttribute("errore", "Errore generico: controllare i campi inseriti.");
			errore = true;
        }
		
		if (errore) {
			return "register.html";
		}
		
		Utente savedUtente = this.utenteService.save(utente);
		credentials.setUtente(savedUtente);
		credentials.setRole(Credentials.USER_ROLE);
		credentials.setPassword(this.passwordEncoder.encode(credentials.getPassword()));
		this.credentialsService.save(credentials);
		
		redirectAttributes.addFlashAttribute("successRegister", "Registrazione effettuata con successo.");
		return "redirect:/login";
	}
	
	@GetMapping("/login")
	public String login(Model model) {
        return "login.html";
    }
	
}

