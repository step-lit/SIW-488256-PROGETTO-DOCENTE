package it.uniroma3.siw.model;

import java.util.Objects;
import java.util.Set;

import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.JoinColumn;
import jakarta.validation.constraints.NotNull;


@Entity
public class Libro {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@NotNull
	private String title;
	
	@NotNull
	private Integer yearOfPublication;
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "libro_id")
	@OrderBy("id ASC")
	@BatchSize(size = 20)
	private List<Immagine> immagini = new ArrayList<>();
	
	@OneToMany(mappedBy="libro", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("id DESC")
	@BatchSize(size = 20)
	private List<Recensione> recensioni = new ArrayList<>();
	
	@ManyToMany(mappedBy="libri")
	private Set<Autore> autori = new HashSet<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getYearOfPublication() {
		return yearOfPublication;
	}

	public void setYearOfPublication(Integer yearOfPublication) {
		this.yearOfPublication = yearOfPublication;
	}

	public List<Immagine> getImmagini() {
		return immagini;
	}
		
	public void setImmagini(List<Immagine> immagini) { 
		this.immagini = immagini;
	}

	public List<Recensione> getRecensioni() { 
		return this.recensioni;
	}

	public void setRecensioni(List<Recensione> recensioni) {
		this.recensioni = recensioni;
	}

	public Set<Autore> getAutori() {
		return this.autori;
	}

	public void setAutori(Set<Autore> autori) {
		this.autori = autori;
	}
	
	public String getCoverImage() {
	    if (this.immagini != null && !this.immagini.isEmpty()) {
	        return this.immagini.get(0).toBase64Image();
	    }
	    
	    return "/images/default-cover.png"; 
	}
	
	public double getAverageVote() {
        if (this.recensioni == null || this.recensioni.isEmpty()) {
            return 0.0;
        }
        return this.recensioni.stream()
                              .mapToInt(Recensione::getVote)
                              .average()
                              .orElse(0.0);
    }
	
	public int getReviewCount() {
        if (this.recensioni == null) {
            return 0;
        }
        return this.recensioni.size();
    }
	
	@Override
	public int hashCode() {
		return Objects.hash(title, yearOfPublication);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Libro other = (Libro) obj;
		return Objects.equals(title, other.title) && Objects.equals(yearOfPublication, other.yearOfPublication);
	}
	
}
