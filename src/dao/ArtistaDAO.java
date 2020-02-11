package dao;



import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import modelo.Artista;
import modelo.HibernateUtil;

public class ArtistaDAO extends GenericDAO<Artista>{
	
	public Artista getArtista(int id) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Artista artista = (Artista) session.get(Artista.class, id);
		session.close();
		return artista;
	}
	
	public Artista obtenerArtistaPorNombre(String nombre) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Artista artista = null;
		Query query = (Query) session.createQuery("SELECT a FROM Artista a WHERE Nombre='"+nombre+"'");
		if (query.list().size()>0)
			artista = (Artista) query.list().get(0);
		session.close();
		return artista;
	}
	
	public List<Artista> obtenerListaArtistasPorNombre(String nombre){
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<Artista> artistas = session.createQuery("SELECT a FROM Artista a WHERE Nombre LIKE '%" + nombre + "%'").list();
		session.close();
		return artistas;
	}
	
	public boolean tieneCancion(String nombreArtista, String nombreCancion) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Query query = session.createQuery("SELECT c FROM Cancion c, Artista a, ArtistaCancion ac WHERE ac.IdArtista=a.Id and ac.IdCancion=c.Id AND a.Nombre='"+nombreArtista+"' AND c.Nombre='" + nombreCancion + "'");
		boolean existe = false;
		if (query.list().size() != 0)
			existe = true;
		session.close();
		return existe;
	}
	
	
	
	public List<Artista> consultarArtistas(){
		List<Artista> lista;
		Session session = HibernateUtil.getSessionFactory().openSession();
		Query query = session.createQuery("SELECT a FROM Artista a");
		lista = query.list();
		session.close();
		
		return lista;
	}
	
	/**
	 * Sin probrar
	 * @return
	 */
	public int totalArtistas() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Query query = session.createQuery("SELECT COUNT(a) FROM Artista a");
		int total = (int) query.list().get(0);
		session.close();
		return total;
	}

	


}
