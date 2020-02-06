package dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import modelo.Cancion;
import modelo.HibernateUtil;
import modelo.Playlist;

public class PlaylistDAO extends GenericDAO<Playlist> {

	
	/**
	 * Devuelve una lista con todas las coincidencias que encuentre
	 * @param nombre de la Playlist 
	 * @return lista con todas las coincidencias
	 */
	public List<Playlist> buscarPlaylist(String nombre){
		List<Playlist> lista;
		String expresion = "^[a-zA-Z0-9 ]*" + nombre + "[a-zA-Z0-9 ]*";
		Session session = HibernateUtil.getSessionFactory().openSession();
		Query query = session.createQuery("SELECT p FROM Playlist p WHERE Nombre LIKE '%" + nombre + "%'");
		lista = query.list();
		
		session.close();
		return lista;
	}
	
	public Playlist getPlaylist(int id) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Playlist playlist = (Playlist) session.get(Playlist.class, id);
		session.close();
		return playlist;
	}
	
	public List<Playlist> obtenerTodasPlaylists(){
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<Playlist> lista = session.createQuery("SELECT p FROM Playlist p").list();
		session.close();
		return lista;
	}
	public void addCancion(Playlist playlist, Cancion cancion) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		
		
		session.clear();
	}
}
