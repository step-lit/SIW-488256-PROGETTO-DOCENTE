package it.uniroma3.siw.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;

import java.util.Arrays;
import java.util.Base64;

@Entity
public class Immagine {
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
	
    private String contentType;
	
    @Lob 
    private byte[] data;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
    
	public String toBase64Image() {
        if (this.data != null && this.contentType != null) {
            return "data:" + this.contentType + ";base64," + Base64.getEncoder().encodeToString(this.data);
        }
        return ""; 
    }
	
	@Override
    public int hashCode() {
        return Arrays.hashCode(data);
    }

	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Immagine immagine = (Immagine) o;
        return Arrays.equals(data, immagine.data);
    }

	
	
}
