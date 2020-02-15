package dao;

import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;
import modelo.HibernateUtil;
import modelo.Playlist;

public class PlaylistDAO extends GenericDAO<Playlist> {

	
	public PlaylistDAO(Session session) {
		super(session);
	}

	/**
	 * Devuelve una lista con todas las coincidencias que encuentre
	 * @param nombre de la Playlist 
	 * @return lista con todas las coincidencias
	 */
	public List<Playlist> buscarPlaylist(String nombre){
		List<Playlist> lista;
		Query query = session.createQuery("SELECT p FROM Playlist p WHERE Nombre LIKE '%" + nombre + "%'");
		lista = query.list();
		return lista;
	}
	
	/**
	 * Obtiene una playlist indicando su id
	 * @param id
	 * @return Playlist
	 */
	public Playlist getPlaylist(int id) {
		Playlist playlist = (Playlist) session.get(Playlist.class, id);
		return playlist;
	}
	
	/**
	 * Obtiene toda las playlist de la base de datos
	 * @return List<Playlist>
	 */
	public List<Playlist> obtenerTodasPlaylists(){
		List<Playlist> lista = session.createQuery("SELECT p FROM Playlist p").list();
		return lista;
	}
	
	/**
	 * Obtiene una lista en orden descendiente de la cantidad de canciones por cada genero que tiene una playlist
	 * @param playlist
	 * @return List<Object[]>
	 */
	public Object[] obtenerGeneroMasEscuchado(Playlist playlist){
		Query query = session.createQuery("SELECT COUNT(c) as cantidad, c.genero "
				+ "FROM Cancion c "
				+ "JOIN c.playlists p "
				+ "WHERE p.id= :idPlaylist "
				+ "GROUP BY c.genero "
				+ "ORDER BY cantidad DESC").setMaxResults(1);
		query.setParameter("idPlaylist", playlist.getId());
		Object[] genero = (Object[]) query.list().get(0);
		return genero;
	}

}
