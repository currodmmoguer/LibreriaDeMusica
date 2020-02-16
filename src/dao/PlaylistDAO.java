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
	 * Obtiene una lista de playlists cuyo nombre de la playlist contenga lo indicado por parámetro
	 * @param String nombre de la Playlist 
	 * @return List<Playlist> lista con todas las coincidencias
	 */
	public List<Playlist> buscarPlaylist(String nombre){
		return session.createQuery("SELECT p FROM Playlist p WHERE Nombre LIKE '%" + nombre + "%'").list();
	}
	
	/**
	 * Obtiene una playlist indicando su id
	 * @param int ID
	 * @return Playlist
	 */
	public Playlist getPlaylist(int id) {
		return (Playlist) session.get(Playlist.class, id);
	}
	
	/**
	 * Obtiene toda las playlist de la base de datos
	 * @return List<Playlist>
	 */
	public List<Playlist> obtenerTodasPlaylists(){
		return session.createQuery("SELECT p FROM Playlist p").list();
	}
	
	/**
	 * Obtiene el género musical que mas canciones tiene una playlist
	 * @param Playlist
	 * @return Object[2] [0]=cantidad, [1]=nombre del género
	 */
	public Object[] obtenerGeneroMasEscuchado(Playlist playlist){
		Query query = session.createQuery("SELECT COUNT(c) as cantidad, c.genero "
											+ "FROM Cancion c "
											+ "JOIN c.playlists p "
											+ "WHERE p.id= :idPlaylist "
											+ "GROUP BY c.genero "
											+ "ORDER BY cantidad DESC").setMaxResults(1);
		query.setParameter("idPlaylist", playlist.getId());
		return (Object[]) query.list().get(0);
	}

}
