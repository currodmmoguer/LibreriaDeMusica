package dao;

import java.util.List;
import org.hibernate.Session;
import modelo.Album;
import modelo.Artista;

public class AlbumDAO extends GenericDAO<Album> {

	
	public AlbumDAO(Session session) {
		super(session);
	}

	/**
	 * Obtiene una lista de álbunes cuyo nombre del álbum contenga lo indicado por parámetro
	 * @param String nombre del album buscado
	 * @return List<Album> lista de albunes que coincidan
	 */
	public List<Album> obtenerListaAlbumPorNombre(String nombre){
		return session.createQuery("SELECT a FROM Album a WHERE Nombre LIKE '%"+nombre+"%'").list();
	}
	
	/**
	 * Obtiene todos los álbunes de un artista indicado
	 * @param Artista
	 * @return List<Album> lista de todos su albunes ordenado por fecha descendiente
	 */
	public List<Album> obtenerAlbunesPorArtista(Artista artista){
		return session.createQuery("SELECT a FROM Album a WHERE IdArtista='"+artista.getId()+"' ORDER BY Publicacion DESC").list();
	}
	
	/**
	 * Obtiene un álbum indicando su id por parámetro
	 * @param id del album
	 * @return Album
	 */
	public Album getAlbum(int id) {
		return (Album) session.get(Album.class, id);
	}
	
	
	/**
	 * Obtiene todos los álbunes de la base de datos ordenadors por su publicación
	 * @return List<Album>
	 */
	public List<Album> obtenerTodosAlbunes(){
		return session.createQuery("SELECT a FROM Album a ORDER BY Publicacion DESC").list();
	}
	

}
