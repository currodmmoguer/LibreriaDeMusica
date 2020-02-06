package modelo;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.persistence.CascadeType;
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

import org.hibernate.annotations.Type;

@Entity
@Table(name = "Cancion")
public class Cancion {

	@Id
	@Type(type = "integer")
	@Column(name = "Id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	
	@Column(name = "Nombre")
	//@NotBlank
	private String nombre;


	@ManyToMany(mappedBy = "canciones", fetch = FetchType.EAGER)
	private List<Artista> artistas;
	
	@ManyToOne
	@JoinColumn(name = "IdAlbum")
	private Album album;

	@Column(name = "Duracion")
	//@NotNull
	private LocalTime duración;

	@Column(name = "Publicacion")
	//@NotNull
	private LocalDate publicacion;
	
	@ManyToMany
	@JoinTable(name="PlaylistCancion", joinColumns = {@JoinColumn(name="IdCacion")}, inverseJoinColumns = {@JoinColumn(name="IdPlaylist")})
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
		this.duración = duración;
		this.publicacion = publicacion;
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
		return duración;
	}

	public String mostrarDuracion() {
		return DateTimeFormatter.ISO_TIME.format(duración);
	}

	public void setDuración(LocalTime duración) {
		this.duración = duración;
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
				+ ", duración=" + duración + ", publicacion=" + publicacion + "]";
	}

}
