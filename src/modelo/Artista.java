package modelo;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.Valid;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotBlank;

@Entity
@Table(name = "Artista")
public class Artista implements Serializable {

	@Id
	@Type(type = "integer")
	@Column(name = "Id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	@Column(name = "Nombre")
	@NotBlank
	private String nombre;

	@OneToMany(fetch = FetchType.LAZY)
	@Cascade(CascadeType.SAVE_UPDATE)
	@JoinColumn(name = "IdArtista")
	@Valid
	private Set<Album> albunes;

	@ManyToMany(fetch = FetchType.LAZY)
	@Cascade(CascadeType.SAVE_UPDATE)
	@JoinTable(name = "ArtistaCancion", joinColumns = { @JoinColumn(name = "IdArtista") }, inverseJoinColumns = {
			@JoinColumn(name = "IdCancion") })
	@Valid
	private Set<Cancion> canciones;

	public Artista() {
	}

	public Artista(String nombre) {
		super();

		this.nombre = nombre;
		this.canciones = new HashSet<Cancion>();
	}

	public Artista(String nombre, Set<Cancion> canciones) {
		super();
		this.nombre = nombre;
		this.canciones = canciones;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public int getId() {
		return id;
	}

	public Set<Cancion> getCanciones() {
		return canciones;
	}

	public Set<Album> getAlbunes() {
		return this.albunes;
	}

	/**
	 * Cambia el nombre del artista
	 * 
	 * @param String nombre
	 * @throws ReproductorException en caso de que no se le pase nada por parámetro
	 */
	public void cambiarNombre(String nombre) throws ReproductorException {
		if (nombre == null || nombre.length() == 0)
			throw new ReproductorException("No puedes dejar el nombre vacío.");

		this.setNombre(nombre);

	}

	/**
	 * Añade la canción a la lista de canciones
	 * @param Cancion
	 */
	public void addCancion(Cancion cancion) {
		canciones.add(cancion);
	}

	/**
	 * Borra una canción de la lista
	 * 
	 * @param Cancion
	 */
	public void borrarCancion(Cancion cancion) {
		canciones.remove(cancion);
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
		Artista other = (Artista) obj;
		if (id != other.id)
			return false;
		return true;
	}


}
