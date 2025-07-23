package it.uniroma3.siw.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
	
	@GetMapping({"/", "/index"})
	public String getIndex(Model model) {
        return "index.html";
    }
	
	@GetMapping({"/contatti"})
	public String getContatti(Model model) {
        return "contatti.html";
    }
	
}
