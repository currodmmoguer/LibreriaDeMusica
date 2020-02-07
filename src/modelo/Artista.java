package modelo;
import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
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

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.Type;

@Entity
@Table(name="Artista")
public class Artista implements Serializable {
	
	@Id
	@Type(type="integer")
	@Column(name="Id")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	@Column(name="Nombre")
	//@NotBlank
	private String nombre;
	
	@OneToMany
	@JoinColumn(name="IdArtista")
	@IndexColumn(name="idx")
	private List<Album> albunes;
	
	@ManyToMany(fetch = FetchType.EAGER)
	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	@JoinTable(name="ArtistaCancion", joinColumns = {@JoinColumn(name="IdArtista")}, inverseJoinColumns = {@JoinColumn(name="IdCancion")})
	private Set<Cancion> canciones;
	
	public Artista() {
		// TODO Auto-generated constructor stub
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
	
	public void cambiarNombre(String nombre) throws ReproductorException {
		if (nombre == null || nombre.length() == 0)
			throw new ReproductorException("No puedes dejar el nombre vacío.");
		
		this.setNombre(nombre);
		System.out.println("Se ha modificado el nombre correctamente.");
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

	@Override
	public String toString() {
		return "Artista [id=" + id + ", nombre=" + nombre + "]";
	}
	
	
	
	


}
