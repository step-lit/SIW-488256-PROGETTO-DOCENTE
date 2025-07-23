package it.uniroma3.siw.controller;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.uniroma3.siw.model.Autore;
import it.uniroma3.siw.model.Immagine;
import it.uniroma3.siw.service.AutoreService;
import it.uniroma3.siw.comparator.AutoriComparator;

@Controller
@RequestMapping("/autori")
public class AutoreController {
	
	@Autowired
	private AutoreService autoreService;
	
	@GetMapping
	public String getAutori(Model model) {
		List<Autore> listaAutori = (List<Autore>) this.autoreService.findAllWithImmagine();
		Collections.sort(listaAutori, new AutoriComparator());
		model.addAttribute("autori", listaAutori);
		return "autori.html";
	}
	
	@GetMapping("/aggiungi-autore")
    public String getAggiungiAutore(Model model) {
        model.addAttribute("autore", new Autore());
        return "aggiungi-autore.html";
    }
	
	@PostMapping("/aggiungi-autore")
    public String postAggiungiAutore(@ModelAttribute("autore") Autore autore,
                                     @RequestParam("imageFile") MultipartFile imageFile,
                                     RedirectAttributes redirectAttributes, Model model) {
        try {
            if (imageFile != null && !imageFile.isEmpty()) {
            	Immagine img = new Immagine();
                img.setContentType(imageFile.getContentType());
                img.setData(imageFile.getBytes());
                autore.setImmagine(img);
            }
            
            this.autoreService.save(autore);
            
            redirectAttributes.addFlashAttribute("successAggiungiAutore", "L'autore è stato aggiunto correttamente nel sistema.");
            return "redirect:/autori";
            
        } catch (IOException e) {
        	e.printStackTrace();
            model.addAttribute("errore", "Errore durante il salvataggio dell'immagine.");
            return "aggiungi-autore.html";
        } 
    }
	
	@GetMapping("/visualizza-autore/{autoreId}")
    public String getDettagliAutore(@PathVariable("autoreId") Long autoreId, Model model) {
        Autore autore = this.autoreService.findByIdWithDetails(autoreId);
        
        if (autore == null) {
        	model.addAttribute("erroreAutore", "Errore: l'autore è presente nel catalogo.");
            return "redirect:/autori";
        }
        model.addAttribute("autore", autore);
        return "visualizza-autore.html";
    }
	
	@GetMapping("/cancella-autore/{id}")
    public String cancellaAutore(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
		this.autoreService.deleteById(id);
            
		redirectAttributes.addFlashAttribute("successCancellaAutore", "L'autore è stato cancellato correttamente.");
        return "redirect:/autori";
    }
        
	@GetMapping("/modifica-autore/{id}")
    public String getModificaAutoreForm(@PathVariable("id") Long id, Model model) {
        Autore autore = autoreService.findByIdWithDetails(id);
        if (autore == null) {
        	model.addAttribute("erroreAutore", "Errore: l'autore da modificare non è stato trovato.");
            return "redirect:/autori";
        }
        model.addAttribute("autore", autore);
        return "modifica-autore.html";
    }
	
	@PostMapping("/modifica-autore/{id}")
    public String postModificaAutore(@PathVariable("id") Long id, @ModelAttribute("autore") Autore autoreModificato, 
    								 @RequestParam("imageFile") MultipartFile imageFile, Model model, RedirectAttributes redirectAttributes) {
		
		Autore autoreAttuale = this.autoreService.findById(id);
		
	    if (autoreAttuale == null) {
	        redirectAttributes.addFlashAttribute("erroreAutore", "Errore: l'autore da modificare non è stato trovato.");
	        return "redirect:/autori";
	    }

	    autoreAttuale.setName(autoreModificato.getName());
	    autoreAttuale.setSurname(autoreModificato.getSurname());
	    autoreAttuale.setDateOfBirth(autoreModificato.getDateOfBirth());
	    autoreAttuale.setDateOfDeath(autoreModificato.getDateOfDeath());
	    autoreAttuale.setNationality(autoreModificato.getNationality());

	    try {
	        if (imageFile != null && !imageFile.isEmpty()) {
	            Immagine nuovaImmagine = new Immagine();
	            nuovaImmagine.setContentType(imageFile.getContentType());
	            nuovaImmagine.setData(imageFile.getBytes());
	            autoreAttuale.setImmagine(nuovaImmagine); 
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	        model.addAttribute("autore", autoreAttuale);
	        model.addAttribute("errore", "Errore: l'aggiornamento dell'immagine non è avvenuto correttamente.");
	        return "modifica-autore.html";
	    }

	    this.autoreService.save(autoreAttuale);

	    redirectAttributes.addFlashAttribute("successModificaAutore", "L'autore è stato modificato correttamente.");
	    return "redirect:/autori/visualizza-autore/" + autoreAttuale.getId();
     
    }
}
