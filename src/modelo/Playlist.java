package modelo;
import java.io.Serializable;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.Session;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotBlank;

@Entity
@Table(name="Playlist")
public class Playlist implements Serializable{

	@Id
	@Type(type="integer")
	@Column(name="Id")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	@Column(name="Nombre")
	@NotBlank
	private String nombre;
	
	@Column(name="Descripcion")
	private String descripcion;
	

	@ManyToMany(fetch = FetchType.EAGER)
	@Cascade(CascadeType.SAVE_UPDATE)
	@Fetch(value = FetchMode.SUBSELECT)
	@JoinTable(name="PlaylistCancion", joinColumns = {@JoinColumn(name="IdPlaylist")}, inverseJoinColumns = {@JoinColumn(name="IdCancion")})
	private List<Cancion> canciones;
	
	public Playlist() {
		// TODO Auto-generated constructor stub
	}

	public Playlist(String nombre, String descripcion) {
		super();
		this.nombre = nombre;
		this.descripcion = descripcion;
		canciones = new LinkedList<>();
	}
	
	public Playlist(String nombre, String descripcion, LinkedList<Cancion> canciones) {
		super();
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.canciones = canciones;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	public String getDescripcion() {
		return descripcion;
	}
	
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public List<Cancion> getCanciones() {
		return canciones;
	}

	public void setCanciones(LinkedList<Cancion> canciones) {
		this.canciones = canciones;
	}
	
	/**
	 * Añade canción 
	 * @param cancion
	 * @return
	 * @throws ReproductorException
	 */
	public boolean addCancion(Cancion cancion) throws ReproductorException {
		boolean contiene = true;
		LinkedList<Cancion> canciones = convertirALinked();
		if (!canciones.contains(cancion)) {
			canciones.push(cancion);
			contiene = false;
		} else {
			throw new ReproductorException("La playlist ya contiene la canción escogida.");
		}
		this.canciones = canciones;
		
		return contiene;
	}
	
	/**
	 * Elimina una canción de la lista
	 * @param cancion
	 * @return
	 */
	public boolean eliminarCancion(Cancion cancion) {
		boolean encontrada = false;
		int contador = 0;
		while (!encontrada && contador < canciones.size()) {
			if (canciones.get(contador).equals(cancion)) {
				encontrada = true;
				canciones.remove(contador);
			}
			contador++;
		}
		return encontrada;
	}
	
	/**
	 * Cambia el nombre de la playlist
	 * @param nombre
	 * @throws ReproductorException
	 */
	public void cambiarNombre(String nombre) throws ReproductorException {
		if (nombre == null || nombre.length() == 0) 
			throw new ReproductorException("No puedes dejar el nombre vacío.");
		
		this.setNombre(nombre);
		System.out.println("Se ha cambiado el nombre de la playlist correctamente.");
		
	}
	
	/**
	 * Cambia la descripción de la playlist
	 * @param descripcion
	 */
	public void cambiarDescripcion(String descripcion) {
		this.setDescripcion(descripcion);
		System.out.println("Se ha cambiado la descripción de la playlist correctamente.");
	}
	
	public LinkedList<Cancion> convertirALinked() {
		LinkedList<Cancion> lista = new LinkedList<Cancion>();
		
		for (Cancion c : this.getCanciones()) {
			lista.add(c);
		}
		
		return lista;
	}
	


	public int getId() {
		return id;
	}
	
	/**
	 * Obtiene la duración total de la playlist
	 * @return
	 */
	public LocalTime getDuracion() {
		LocalTime tiempo = LocalTime.of(0, 0, 0);
		int horas, minutos, segundos;
		for (Cancion cancion : canciones) {
			horas = cancion.getDuración().getHour();
			minutos = cancion.getDuración().getMinute();
			segundos = cancion.getDuración().getSecond();
			tiempo = tiempo.plusHours(horas).plusMinutes(minutos).plusSeconds(segundos);
		}
		
		return tiempo;
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
		Playlist other = (Playlist) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Playlist [id=" + id + ", nombre=" + nombre + ", canciones="  + canciones.toString() + "]";
	}
	
	
	
	
	
}
