package it.uniroma3.siw.controller;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Immagine;
import it.uniroma3.siw.model.Libro;
import it.uniroma3.siw.model.Utente;
import it.uniroma3.siw.service.AutoreService;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.LibroService;
import it.uniroma3.siw.comparator.LibroComparator;

@Controller
@RequestMapping("/libri")
public class LibroController {
	
	@Autowired
	private LibroService libroService;
	
	@Autowired
	private AutoreService autoreService;
	
	@Autowired
	private CredentialsService credentialsService;
	
	@GetMapping
	public String getLibri(Model model) {
		List<Libro> listaLibri = (List<Libro>) this.libroService.findAllForCatalogo();
		Collections.sort(listaLibri, new LibroComparator());
		model.addAttribute("libri", listaLibri);
		return "libri.html";
	}
	
	@GetMapping("/aggiungi-libro")
	public String getAggiungiLibro(Model model) {
		model.addAttribute("libro", new Libro());
		model.addAttribute("autori", (List<Autore>) this.autoreService.findAll());
		return "aggiungi-libro.html";
	}
	
	@PostMapping("/aggiungi-libro")
	public String postAggiungiLibro(@ModelAttribute("libro") Libro libro, @RequestParam("imageFiles") List<MultipartFile> imageFiles, 
									@RequestParam(value = "autoriSelezionati", required = false) List<Long> autoriIds,
									RedirectAttributes redirectAttributes, Model model) {
		
		try {
			Set<Immagine> immagini = new LinkedHashSet<>();
			for(MultipartFile file : imageFiles) {
				if (!file.isEmpty()) {
					Immagine img = new Immagine();
	                img.setContentType(file.getContentType());
	                img.setData(file.getBytes());
	                immagini.add(img);
	            }
			}
            
			libro.getImmagini().clear(); 
	        libro.getImmagini().addAll(immagini);
			
			if (autoriIds != null && !autoriIds.isEmpty()) {
			    List<Autore> autoriSelezionatiList = (List<Autore>) this.autoreService.findAllById(autoriIds);
			    Set<Autore> autoriSelezionati = new HashSet<>(autoriSelezionatiList);

			    for (Autore autore : autoriSelezionati) {
			        autore.getLibri().add(libro);
			    }
			    libro.setAutori(autoriSelezionati);
			}
			
			this.libroService.save(libro);
			
            redirectAttributes.addFlashAttribute("successAggiungiLibro", "Il libro è stato aggiunto correttamente nel sistema.");
            return "redirect:/libri";

        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("errore", "Errore: non è stato possibile inserire il libro nel catalogo.");
            model.addAttribute("autori", (List<Autore>) this.autoreService.findAll());
            return "aggiungi-libro.html";
        }
    }
	
	@GetMapping("/visualizza-libro/{libroId}")
	public String getDettagliLibro(@PathVariable("libroId") Long libroId, Model model) {
		Libro libro = this.libroService.findByIdWithDetails(libroId);
		
		if (libro == null) {
			model.addAttribute("erroreLibro", "Errore: libro non presente nella collezione.");
	        return "redirect:/libri"; 
	    }
		
		model.addAttribute("libro", libro);
		
		
		/* variabile booleana per verificare se l'utente ha già inserito una recensione per il libro.
		 * viene utilizzata dallo script (presente nel file html) per visualizzare il messaggio di pop-up
		 */
		boolean isRecensitoByUtente = false;
	    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

	    if (principal instanceof UserDetails) {
	        UserDetails userDetails = (UserDetails) principal;
	        
	        model.addAttribute("userDetails", userDetails);
	        
	        Credentials credentials = credentialsService.findByUsername(userDetails.getUsername());
	        if (credentials != null) {
	            Utente utenteCorrente = credentials.getUtente();
	            
	            isRecensitoByUtente = libro.getRecensioni().stream()
	                                  .anyMatch(recensione -> recensione.getUtente().equals(utenteCorrente));
	        }
	    }
	    model.addAttribute("isRecensitoByUtente", isRecensitoByUtente);
		
		return "visualizza-libro.html";
	}
	
	@GetMapping("/cancella-libro/{id}")
	public String cancellaLibro(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
		this.libroService.deleteById(id);
		
		redirectAttributes.addFlashAttribute("successCancellaLibro", "Il libro è stato cancellato correttamente.");
		return "redirect:/libri";
	}
	
	@GetMapping("/modifica-libro/{id}")
	public String getModificaLibro(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
	    Libro libro = this.libroService.findByIdWithDetails(id);
	    if (libro == null) {
	        redirectAttributes.addFlashAttribute("errore", "Errore: il libro da modificare non è stato trovato.");
	        return "redirect:/libri";
	    }
	    
	    model.addAttribute("libro", libro);
	    model.addAttribute("autori", (List<Autore>) this.autoreService.findAll()); 
	    
	    return "modifica-libro.html";
	}
	
	@PostMapping("/modifica-libro/{id}")
	public String postModificaLibro(@PathVariable("id") Long id,
	                                @ModelAttribute("libro") Libro libroModificato,
	                                @RequestParam("imageFiles") List<MultipartFile> imageFiles,
	                                @RequestParam(value = "autoriSelezionati", required = false) List<Long> autoriIds,
	                                Model model, RedirectAttributes redirectAttributes) {
	    
	    Libro libroAttuale = this.libroService.findByIdWithDetails(id);
	    
	    if (libroAttuale == null) {
	        redirectAttributes.addFlashAttribute("erroreAutore", "Errore: impossibile aggiornare: libro non trovato.");
	        return "redirect:/libri";
	    }

	    libroAttuale.setTitle(libroModificato.getTitle());
	    libroAttuale.setYearOfPublication(libroModificato.getYearOfPublication());

	    try {
	        boolean hasNewFiles = imageFiles.stream().anyMatch(file -> !file.isEmpty());
	        
	        if (hasNewFiles) {
	        	Set<Immagine> immagini = new LinkedHashSet<>();
	        	
	            for (MultipartFile file : imageFiles) {
	                if (!file.isEmpty()) {
	                	Immagine img = new Immagine();
	                    img.setContentType(file.getContentType());
	                    img.setData(file.getBytes());
	                    immagini.add(img);
	                }
	            }
	            libroAttuale.getImmagini().clear();
	            libroAttuale.getImmagini().addAll(immagini);
	            
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	        model.addAttribute("errore", "Errore: l'aggiornamento delle immagini non è avvenuto correttamente.");
	        model.addAttribute("libro", libroAttuale); 
	        model.addAttribute("allAutori", (List<Autore>) this.autoreService.findAll());
	        return "modifica-libro.html";
	    }

	    for (Autore autore : libroAttuale.getAutori()) {
	        autore.getLibri().remove(libroAttuale);
	    }
	    libroAttuale.getAutori().clear(); 

	    if (autoriIds != null && !autoriIds.isEmpty()) {
	        List<Autore> autoriSelezionatiList = (List<Autore>) this.autoreService.findAllById(autoriIds);
	        Set<Autore> autoriSelezionati = new HashSet<>(autoriSelezionatiList);

	        for (Autore autore : autoriSelezionati) {
	            autore.getLibri().add(libroAttuale);
	        }
	        libroAttuale.setAutori(autoriSelezionati);
	    }

	    this.libroService.save(libroAttuale);

	    redirectAttributes.addFlashAttribute("successModificaLibro", "Il libro è stato modificato correttamente.");
	    return "redirect:/libri/visualizza-libro/" + id;
	}
	
}
