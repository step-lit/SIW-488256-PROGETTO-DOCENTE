package it.uniroma3.siw.comparator;

import java.util.Comparator;

import it.uniroma3.siw.model.Autore;

public class AutoriComparator implements Comparator<Autore>{
	
	public AutoriComparator() {};
	
	@Override
	public int compare(Autore a1, Autore a2) {
		
		if( !a1.getSurname().equals(a2.getSurname()) ) {
			return a1.getSurname().compareTo(a2.getSurname());
		}
		if( !a1.getName().equals(a2.getName()) ) {
			return a1.getName().compareTo(a2.getName());
		}
		if( !a1.getDateOfBirth().equals(a2.getDateOfBirth()) ) {
			return a1.getDateOfBirth().compareTo(a2.getDateOfBirth());
		}
		if( a1.getDateOfDeath() == null && a2.getDateOfDeath() == null) {
			return 0;
		}
		if(a1.getDateOfDeath() == null) {
			return -1;
		}
		if(a2.getDateOfDeath() == null) {
			return 1;
		}
		return a1.getDateOfDeath().compareTo(a2.getDateOfDeath());
		
	}
	
}
