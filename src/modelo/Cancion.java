package modelo;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotBlank;

@Entity
@Table(name = "Cancion")
public class Cancion implements Serializable {

	@Id
	@Type(type = "integer")
	@Column(name = "Id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	
	@Column(name = "Nombre")
	@NotBlank
	private String nombre;


	@ManyToMany(mappedBy = "canciones", fetch = FetchType.EAGER)
	@Cascade(CascadeType.SAVE_UPDATE)
	private List<Artista> artistas;
	
	@ManyToOne
	@JoinColumn(name = "IdAlbum")
	private Album album;

	@Column(name = "Duracion")
	@NotNull
	private LocalTime duracion;

	@Column(name = "Publicacion")
	@NotNull
	private LocalDate publicacion;
	
	@ManyToMany(fetch = FetchType.LAZY)
	@Cascade(CascadeType.SAVE_UPDATE)

	@JoinTable(name="PlaylistCancion", joinColumns = {@JoinColumn(name="IdCancion")}, inverseJoinColumns = {@JoinColumn(name="IdPlaylist")})
	private List<Playlist> playlists;
	
	@Enumerated(EnumType.STRING)
	private Genero genero;

	
	public Cancion() {}

	public Cancion(String nombre, List<Artista> artistas, Album album, LocalTime duración, LocalDate publicacion, Genero genero)
			throws ReproductorException {

		if (artistas.size() == 0)
			throw new ReproductorException("La canción debe tener un artista mínimo");

		this.nombre = nombre;
		this.artistas = artistas;
		this.album = album;
		this.duracion = duración;
		this.publicacion = publicacion;
		this.genero = genero;

	}

	public Cancion(String nombre, List<Artista> listaArtistas, LocalTime duracion, Genero genero) {
		
		this.nombre = nombre;
		this.artistas = listaArtistas;
		this.duracion = duracion;
		this.genero = genero;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public List<Artista> getArtistas() {
		return artistas;
	}

	public void setArtistas(List<Artista> artistas) {
		this.artistas = artistas;
	}

	public Album getAlbum() {
		return album;
	}

	public void setAlbum(Album album) {
		this.album = album;
	}

	public LocalTime getDuración() {
		return duracion;
	}

	public String mostrarDuracion() {
		return DateTimeFormatter.ISO_TIME.format(duracion);
	}

	public void setDuración(LocalTime duración) {
		this.duracion = duración;
	}

	public LocalDate getPublicacion() {
		return publicacion;
	}

	public String mostrarFechaPublicacion() {
		return DateTimeFormatter.ISO_DATE.format(publicacion);
	}

	public void setPublicacion(LocalDate publicacion) {
		this.publicacion = publicacion;
	}

	public int getId() {
		return id;
	}
	
	public String getNombreConArtistas() {
		StringBuilder sb = new StringBuilder(nombre + " - ");
		for(Artista a: artistas) {
			sb.append(a.getNombre() +", ");
		}
		sb.delete(sb.length() - 2, sb.length()); // Quita la ultima ,
		sb.append("\n");
		
		return sb.toString();
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
		Cancion other = (Cancion) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Cancion [id=" + id + ", nombre=" + nombre + ", artistas=" + artistas + ", album=" + album
				+ ", duración=" + duracion + ", publicacion=" + publicacion + "]";
	}

}
