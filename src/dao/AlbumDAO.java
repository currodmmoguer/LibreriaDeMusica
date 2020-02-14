package dao;

import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;
import modelo.Album;
import modelo.HibernateUtil;

public class AlbumDAO extends GenericDAO<Album> {

	
	/**
	 * Obtiene una lista de albunes cuyo nombre contenga lo indicado
	 * @param nombre del album
	 * @return List<Album>
	 */
	public List<Album> obtenerListaAlbumPorNombre(String nombre){
		Session session = HibernateUtil.getSessionFactory().openSession();
		Query query = session.createQuery("SELECT a FROM Album a WHERE Nombre LIKE'%"+nombre+"%'");
		List<Album> albunes = query.list();
		session.close();
		return albunes;
	}
	
	/**
	 * Obtiene todos los albunes de un artista
	 * @param id del artista indicado
	 * @return List<Album>
	 */
	public List<Album> obtenerAlbunesPorArtista(int id){
		List<Album> lista;
		Session session = HibernateUtil.getSessionFactory().openSession();
		Query query = session.createQuery("SELECT a FROM Album a WHERE IdArtista='"+id+"' ORDER BY Publicacion DESC");
		lista = query.list();
		session.close();
		return lista;
	}
	
	/**
	 * Obtiene un album indicando su id
	 * @param id
	 * @return Album
	 */
	public Album getAlbum(int id) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Album album = (Album) session.get(Album.class, id);
		session.close();
		return album;
	}
	
	public List<Album> obtenerTodosAlbunes(){
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<Album> lista = session.createQuery("SELECT a FROM Album a").list();
		session.close();
		return lista;
	}
	

}
