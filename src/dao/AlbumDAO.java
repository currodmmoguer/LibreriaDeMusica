package dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import modelo.Album;
import modelo.Artista;
import modelo.HibernateUtil;

public class AlbumDAO extends GenericDAO<Album> {

	public Album obtenerAlbumPorNombre(String nombre) {
		Album album = null;
		Session session = HibernateUtil.getSessionFactory().openSession();
		Query query = (Query) session.createQuery("SELECT a FROM Album a WHERE Nombre='"+nombre+"'");
		if (query.list().size()>0)
			album = (Album) query.list().get(0);
		
		session.close();
		return album;
	}
	
	public List<Album> obtenerListaAlbumPorNombre(String nombre){
		Session session = HibernateUtil.getSessionFactory().openSession();
		Query query = session.createQuery("SELECT a FROM Album a WHERE Nombre LIKE'%"+nombre+"%'");
		List<Album> albunes = query.list();
		session.close();
		return albunes;
	}
	
	public List<Album> obtenerAlbunesPorArtista(int id){
		List<Album> lista;
		Session session = HibernateUtil.getSessionFactory().openSession();
		Query query = session.createQuery("SELECT a FROM Album a WHERE IdArtista='"+id+"'");
		lista = query.list();
		session.close();
		return lista;
	}
	
	public Album consultarAlbum(int id) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Album album = (Album) session.get(Album.class, id);
		session.close();
		return album;
	}
	
	public Album consultarAlbum(String nombre) {
		Album album = null;
		Session session = HibernateUtil.getSessionFactory().openSession();
		Query query = session.createQuery("SELECT a FROM Album a WHERE Nombre='" + nombre + "'");
		if (query.list().size() > 0)
			album = (Album) query.list().get(0);
		session.close();
		return album;
	}
	

}
