package it.uniroma3.siw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Libro;
import it.uniroma3.siw.model.Recensione;
import it.uniroma3.siw.model.Utente;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.LibroService;
import it.uniroma3.siw.service.RecensioneService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/recensioni")
public class RecensioneController {

	@Autowired 
	private LibroService libroService;
	
    @Autowired 
    private RecensioneService recensioneService;
    
    @Autowired 
    private CredentialsService credentialsService;
    
    @GetMapping("/aggiungi-recensione/{libroId}")
    public String getAggiungiRecensione(@PathVariable("libroId") Long libroId, Model model) {
    	Libro libro = libroService.findByIdWithDetails(libroId);
        if (libro == null) {
            return "redirect:/libri";
        }
        model.addAttribute("libro", libro);
        model.addAttribute("recensione", new Recensione());
        return "aggiungi-recensione.html";
    }
    
    @PostMapping("/aggiungi-recensione/{libroId}")
    public String postAggiungiRecensione(@PathVariable("libroId") Long libroId,
                                         @Valid @ModelAttribute("recensione") Recensione recensione,
                                         BindingResult bindingResult, RedirectAttributes redirectAttributes,
                                         Model model) {
        
        Libro libro = libroService.findByIdWithDetails(libroId);
        if (libro == null) {
            return "redirect:/libri";
        }
        
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Credentials credentials = credentialsService.findByUsername(userDetails.getUsername());
        
        Utente utente = credentials.getUtente();
        
        recensione.setUtente(utente);
        recensione.setLibro(libro);

        if (libro.getRecensioni().contains(recensione)) {
            bindingResult.reject("erroreRecensioneDoppia", "Errore: hai già scritto una recensione per questo libro.");
        }
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("libro", libro);
            return "aggiungi-recensione.html";
        }

        utente.getRecensioni().add(recensione);
        this.recensioneService.save(recensione);
        
        redirectAttributes.addFlashAttribute("successAggiungiRecensione", "La recensione è stata aggiunta correttamente per questo libro.");
        return "redirect:/libri/visualizza-libro/" + libroId;
    }
    
    @GetMapping("/cancella-recensione/{recensioneId}/{libroId}")
    public String getCancellaRecensione(@PathVariable("recensioneId") Long recensioneId,
    									@PathVariable("libroId") Long libroId,RedirectAttributes redirectAttributes) {
    	
    	this.recensioneService.deleteById(recensioneId);
    	
    	redirectAttributes.addFlashAttribute("successCancellaRecensione", "La recensione è stata cancellata correttamente.");
    	return "redirect:/libri/visualizza-libro/" + libroId;
    }


}
