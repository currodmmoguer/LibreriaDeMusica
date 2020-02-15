package dao;

import java.util.LinkedList;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;
import modelo.Artista;
import modelo.Cancion;
import modelo.HibernateUtil;

public class CancionDAO extends GenericDAO<Cancion> {
	
	public CancionDAO(Session session) {
		super(session);
	}

	/**
	 * Obtiene una lista de canciones indicando el nombre con expresiones regulares
	 * @param nombre
	 * @return List<Cancion>
	 */
	public List<Cancion> obtenerCancionPorNombre(String nombre) {
		Query query = session.createQuery("SELECT c FROM Cancion c WHERE Nombre LIKE '%" + nombre + "%'");
		List<Cancion> lista = query.list();
		return lista;
	}
	
	/**
	 * Obtiene una canción indicando su id
	 * @param id
	 * @return Canción
	 */
	public Cancion getCancion(int id) {
		Cancion cancion = (Cancion) session.get(Cancion.class, id);
		return cancion;
	}
	
	public List<Cancion> obtenerTodasCanciones(){
		List<Cancion> lista = session.createQuery("SELECT c FROM Cancion c").list();
		return lista;
	}
	
	/**
	 * Obtiene los 3 géneros musicales más hay
	 * @return
	 */
	public List<Object[]> obtenerGeneroMasEscuchado(){
		Query query = session.createQuery("SELECT COUNT(c) as cantidad, c.genero "
										+ "FROM Cancion c "
										+ "GROUP BY c.genero "
										+ "ORDER BY cantidad DESC").setMaxResults(3);
		List<Object[]> canciones = query.list();
		return canciones;
	}
	
	/**
	 * Obtiene todas las canciones de un artista que no tiene album asignado
	 * @param artista
	 * @return
	 */
	public List<Cancion> obtenerCancionesDeUnArtistaSinAlbum(Artista artista){
		String hql = "SELECT c FROM Cancion c"
				+ " JOIN c.artistas a"
				+ " WHERE a.id = " + artista.getId()
				+ " AND c.album is null";
		List<Cancion> lista = session.createQuery(hql).list();
		return lista;
	}
}
