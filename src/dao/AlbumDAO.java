package dao;

import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;
import modelo.Album;
import modelo.Artista;
import modelo.Cancion;
import modelo.HibernateUtil;

public class AlbumDAO extends GenericDAO<Album> {

	
	public AlbumDAO(Session session) {
		super(session);
	}

	/**
	 * Obtiene una lista de álbunes cuyo nombre contenga lo indicado por parámetro
	 * @param nombre del album
	 * @return List<Album>
	 */
	public List<Album> obtenerListaAlbumPorNombre(String nombre){
		Query query = session.createQuery("SELECT a FROM Album a WHERE Nombre LIKE'%"+nombre+"%'");
		List<Album> albunes = query.list();
		return albunes;
	}
	
	/**
	 * Obtiene todos los álbunes de un artista
	 * @param id del artista indicado
	 * @return List<Album>
	 */
	public List<Album> obtenerAlbunesPorArtista(Artista artista){
		List<Album> lista;
		Query query = session.createQuery("SELECT a FROM Album a WHERE IdArtista='"+artista.getId()+"' ORDER BY Publicacion DESC");
		lista = query.list();
		return lista;
	}
	
	/**
	 * Obtiene un album indicando su id por parámetro
	 * @param id
	 * @return Album
	 */
	public Album getAlbum(int id) {
		Album album = (Album) session.get(Album.class, id);
		return album;
	}
	
	public List<Cancion> obtenerCancionesAlbum(int id) {
		List<Cancion> lista = session.createQuery("SELECT c FROM Cancion c WHERE IdAlbum="+id).list();
		return lista;
	}
	
	public List<Album> obtenerTodosAlbunes(){
		List<Album> lista = session.createQuery("SELECT a FROM Album a").list();
		return lista;
	}
	

}
