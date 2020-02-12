package dao;

import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;
import modelo.Artista;
import modelo.Cancion;
import modelo.HibernateUtil;

public class CancionDAO extends GenericDAO<Cancion> {
	
	/**
	 * Obtiene una lista de canciones indicando el nombre con expresiones regulares
	 * @param nombre
	 * @return List<Cancion>
	 */
	public List<Cancion> obtenerCancionPorNombre(String nombre) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Query query = session.createQuery("SELECT c FROM Cancion c WHERE Nombre LIKE '%" + nombre + "%'");
		List<Cancion> lista = query.list();
		session.close();
		return lista;
	}
	
	/**
	 * Obtiene una canción indicando su id
	 * @param id
	 * @return Canción
	 */
	public Cancion getCancion(int id) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Cancion cancion = (Cancion) session.get(Cancion.class, id);
		session.close();
		return cancion;
	}
	
	/**
	 * Obtiene los 3 géneros musicales más hay
	 * @return
	 */
	public List<Object[]> obtenerGeneroMasEscuchado(){
		Session session = HibernateUtil.getSessionFactory().openSession();
		Query query = session.createQuery("SELECT COUNT(c) as cantidad, c.genero "
										+ "FROM Cancion c "
										+ "GROUP BY c.genero "
										+ "ORDER BY cantidad DESC").setMaxResults(3);
		List<Object[]> canciones = query.list();
		session.close();
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
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<Cancion> lista = session.createQuery(hql).list();
		session.close();
		return lista;
	}
}
