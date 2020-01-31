package modelo;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

@Entity
@Table(name="Playlist")
public class Playlist {

	@Id
	@Type(type="integer")
	@Column(name="Id")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	@Column(name="Nombre")
	private String nombre;
	
	//@ManyToMany(mappedBy = "playlists")
	//private List<Cancion> canciones;
	
	public Playlist() {
		// TODO Auto-generated constructor stub
	}

	public Playlist(String nombre) {
		super();
		this.nombre = nombre;
		//canciones = new LinkedList<>();
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

//	public List<Cancion> getCanciones() {
//		return canciones;
//	}
//
//	public void setCanciones(List<Cancion> canciones) {
//		this.canciones = canciones;
//	}
//	
//	public void addCancion(Cancion cancion) {
//		canciones.add(cancion);
//	}

	public int getId() {
		return id;
	}
	
//	public LocalTime getDuracion() {
//		LocalTime tiempo = LocalTime.of(0, 0, 0);
//		int horas, minutos, segundos;
//		for (Cancion cancion : canciones) {
//			horas = cancion.getDuración().getHour();
//			minutos = cancion.getDuración().getMinute();
//			segundos = cancion.getDuración().getSecond();
//			tiempo = tiempo.plusHours(horas).plusMinutes(minutos).plusSeconds(segundos);
//		}
//		
//		return tiempo;
//	}

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
		return "Playlist [id=" + id + ", nombre=" + nombre + ", canciones="  + "]";
	}
	
	
	
	
	
}
