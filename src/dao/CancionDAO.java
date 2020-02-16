package dao;

import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;
import modelo.Artista;
import modelo.Cancion;

public class CancionDAO extends GenericDAO<Cancion> {
	
	public CancionDAO(Session session) {
		super(session);
	}

	/**
	 * Obtiene una lista de canciones cuyo nombre de la canción contenga lo indicado por parámetro
	 * @param String nombre de la canción
	 * @return List<Cancion>
	 */
	public List<Cancion> obtenerCancionPorNombre(String nombre) {
		return session.createQuery("SELECT c FROM Cancion c WHERE Nombre LIKE '%" + nombre + "%'").list();
	}
	
	/**
	 * Obtiene una canción indicando su id
	 * @param int ID de la canción
	 * @return Canción
	 */
	public Cancion getCancion(int id) {
		return (Cancion) session.get(Cancion.class, id);
	}
	
	/**
	 * Obtiene una lista de todas las canciones de la base de datos
	 * @return List<Cancion>
	 */
	public List<Cancion> obtenerTodasCanciones(){
		return session.createQuery("SELECT c FROM Cancion c").list();
	}
	
	/**
	 * Obtiene los 3 géneros musicales que mas hay en el reproductor
	 * @return List<Object[2]> [0]=cantidad, [1]=nombre del género
	 */
	public List<Object[]> obtenerGeneroMasEscuchado(){
		Query query = session.createQuery("SELECT COUNT(c) as cantidad, c.genero "
										+ "FROM Cancion c "
										+ "GROUP BY c.genero "
										+ "ORDER BY cantidad DESC").setMaxResults(3);
		return query.list();
	}
	
	/**
	 * Obtiene todas las canciones de un artista que no tiene un álbum asignado
	 * @param artista
	 * @return
	 */
	public List<Cancion> obtenerCancionesDeUnArtistaSinAlbum(Artista artista){
		String hql = "SELECT c FROM Cancion c"
				+ " JOIN c.artistas a"
				+ " WHERE a.id = " + artista.getId()
				+ " AND c.album is null";
		return session.createQuery(hql).list();
	}
}
