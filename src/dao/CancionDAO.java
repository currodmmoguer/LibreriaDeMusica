package dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import modelo.Cancion;
import modelo.HibernateUtil;

public class CancionDAO extends GenericDAO<Cancion> {
	
	public List<Cancion> obtenerCancionPorNombre(String nombre) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		
		Query query = session.createQuery("SELECT c FROM Cancion c WHERE Nombre='" + nombre + "'");
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
}
