package modelo;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotBlank;

@Entity
@Table(name="Album")
public class Album implements Serializable{

	@Id
	@Type(type="integer")
	@Column(name="Id")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	
	@Column(name="Nombre")
	@NotBlank
	private String nombre;
	
	@ManyToOne
	@JoinColumn(name="IdArtista")
	@Cascade(CascadeType.SAVE_UPDATE)
	@Valid
	private Artista artista;
	
	@OneToMany(fetch = FetchType.LAZY, orphanRemoval = true)
	@Cascade(CascadeType.ALL)
	@JoinColumn(name="IdAlbum")
	@Size(min=1)
	@Valid
	private List<Cancion> canciones;
	
	@Column(name="Publicacion")
	@NotNull
	private LocalDate lanzamiento;
	
	public Album() {}
	
	public Album(String nombre, Artista artista, List<Cancion> canciones, LocalDate publicacion) {
		super();
		this.nombre = nombre;
		this.artista = artista;
		this.canciones = canciones;
		this.lanzamiento = publicacion;
	}
	public Album(String nombre, Artista artista, LocalDate publicacion) {
		this.nombre = nombre;
		this.artista = artista;
		this.lanzamiento = publicacion;
		canciones = new ArrayList<>();
	}

	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public Artista getArtista() {
		return artista;
	}
	public void setArtista(Artista artista) {
		this.artista = artista;
	}
	public LocalDate getPublicacion() {
		return lanzamiento;
	}
	public void setPublicacion(LocalDate publicacion) {
		this.lanzamiento = publicacion;
	}
	
	public List<Cancion> getCanciones() {
		return canciones;
	}
	
	public void setCanciones(List<Cancion> canciones) {
		this.canciones = canciones;
	}
	/**
	 * Añade una canción a la lista
	 * @param c
	 */
	public void addCancion(Cancion c) {
		canciones.add(c);
	}
	
	public int getId() {
		return id;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Album other = (Album) obj;
		if (id != other.id)
			return false;
		return true;
	}
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(nombre);
		if (artista != null) 	//Si no se controla y no tiene artista salta NullPointerException
			sb.append(" - " + artista.getNombre());
		
		sb.append(" (" + getPublicacion() + ")\n");
		int pos = 1;
		
		// Muestra las canciones del album
		for (Cancion cancion : getCanciones()) {
			sb.append("\t" + pos + ". " + cancion.getNombre() +"\n");
			pos++;
		}
		return sb.toString();
	}
	


	

	
	
}
