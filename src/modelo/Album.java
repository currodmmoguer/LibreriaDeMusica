package modelo;
import java.time.LocalDate;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.Type;

@Entity
@Table(name="Album")
public class Album {

	@Id
	@Type(type="integer")
	@Column(name="Id")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	
	@Column(name="Nombre")
	private String nombre;
	
	@Column(name="IdArtista")
	@Type(type="integer")
	private Artista artista;
	
	@OneToMany
	@JoinColumn(name="IdAlbum")
	//@IndexColumn(name="idx")
	private List<Cancion> canciones;
	
	@Column(name="Publicacion")
	private LocalDate lanzamiento;
	
	public Album() {}
	
	public Album(String nombre, Artista artista, LocalDate publicacion) {
		super();
		this.nombre = nombre;
		this.artista = artista;
		this.lanzamiento = publicacion;
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
		return "Album [id=" + id + ", nombre=" + nombre + ", artista=" + artista + ", publicacion=" + lanzamiento + "]";
	}
	
	
}
