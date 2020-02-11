package dao;

import java.util.LinkedList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import modelo.Artista;
import modelo.Cancion;
import modelo.HibernateUtil;

public class CancionDAO extends GenericDAO<Cancion> {
	
	public List<Cancion> obtenerCancionPorNombre(String nombre) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		
		Query query = session.createQuery("SELECT c FROM Cancion c WHERE Nombre LIKE'%" + nombre + "%'");
		List<Cancion> lista = query.list();
		session.close();
		
		return lista;
	}
	
	public Cancion obtenerCancionPorId(int id) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Cancion cancion = (Cancion) session.get(Cancion.class, id);
		session.close();
		
		return cancion;
	}
	
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
	
	public List<Cancion> obtenerCancionesDeUnArtistaSinAlbum(Artista artista){
		String hql = "SELECT c FROM Cancion c "
				+ "JOIN c.artistas a "
				+ "WHERE a.id = " + artista.getId()
				+ " AND c.album is null";
		Session session = HibernateUtil.getSessionFactory().openSession();
		Query query = session.createQuery(hql);
		List<Cancion> lista = query.list();
		session.close();
		return lista;
	}
}
