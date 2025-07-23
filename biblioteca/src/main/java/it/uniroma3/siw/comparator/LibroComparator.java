package it.uniroma3.siw.comparator;

import java.util.Comparator;

import it.uniroma3.siw.model.Libro;

public class LibroComparator implements Comparator<Libro>{

	public LibroComparator() {};
	
	@Override
	public int compare(Libro l1, Libro l2) {
		if( !l1.getTitle().equals(l2.getTitle()) ) {
			return l1.getTitle().compareTo(l2.getTitle());
		}
		return l1.getYearOfPublication().compareTo(l2.getYearOfPublication());
	}
	
}
