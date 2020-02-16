package dao;

import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;
import modelo.Artista;

public class ArtistaDAO extends GenericDAO<Artista> {

	public ArtistaDAO(Session session) {
		super(session);
	}

	/**
	 * Obtiene un artista indicando su id
	 * 
	 * @param id
	 * @return Artista
	 */
	public Artista getArtista(int id) {
		return (Artista) session.get(Artista.class, id);
	}

	/**
	 * Indica si existe un artista con un nombre en la base de datos
	 * 
	 * @param String nombre del artista buscado
	 * @return boolean si existe o no
	 */
	public boolean existeArtista(String nombre) {
		Query query = session.createQuery("SELECT a FROM Artista a WHERE Nombre='" + nombre + "'");
		boolean existe = (query.list().isEmpty() ? false : true); // Comprueba si está vacía la lista
		return existe;
	}

	/**
	 * Obtiene una lista de artistas cuyo nombre del artista contenga lo indicado
	 * por parámetro
	 * 
	 * @param String nombre del artista
	 * @return List<Artista> lista de todas las coincidencias
	 */
	public List<Artista> obtenerListaArtistasPorNombre(String nombre) {
		return session.createQuery("SELECT a FROM Artista a WHERE Nombre LIKE '%" + nombre + "%'").list();
	}

	/**
	 * Obtiene una lista de todos los artistas de la base de datos
	 * 
	 * @return List<Artista>
	 */
	public List<Artista> consultarArtistas() {
		return session.createQuery("SELECT a FROM Artista a").list();
	}

}
