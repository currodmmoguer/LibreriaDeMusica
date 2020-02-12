package dao;

import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;
import modelo.Artista;
import modelo.HibernateUtil;

public class ArtistaDAO extends GenericDAO<Artista>{
	
	/**
	 * Obtiene un artista indicando su id
	 * @param id
	 * @return Artista
	 */
	public Artista getArtista(int id) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Artista artista = (Artista) session.get(Artista.class, id);
		session.close();
		return artista;
	}
	
	/**
	 * Comprueba si un artista existe por su nombre
	 * @param nombre
	 * @return si existe
	 */
	public boolean existeArtista(String nombre) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Query query = session.createQuery("SELECT a FROM Artista a WHERE Nombre='"+nombre+"'");
		boolean existe = (query.list().isEmpty() ? false : true);	//Comprueba si está vacía la lista
		session.close();
		return existe;
	}
	
	/**
	 * Obtiene una lista de artista usando expresiones regulares
	 * @param nombre
	 * @return List<Artista>
	 */
	public List<Artista> obtenerListaArtistasPorNombre(String nombre){
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<Artista> artistas = session.createQuery("SELECT a FROM Artista a WHERE Nombre LIKE '%" + nombre + "%'").list();
		session.close();
		return artistas;
	}
	
//	public boolean tieneCancion(String nombreArtista, String nombreCancion) {
//		Session session = HibernateUtil.getSessionFactory().openSession();
//		Query query = session.createQuery("SELECT c FROM Cancion c, Artista a, ArtistaCancion ac WHERE ac.IdArtista=a.Id and ac.IdCancion=c.Id AND a.Nombre='"+nombreArtista+"' AND c.Nombre='" + nombreCancion + "'");
//		boolean existe = false;
//		if (query.list().size() != 0)
//			existe = true;
//		session.close();
//		return existe;
//	}
	

	
	/**
	 * Obtiene una lista de todos los artistas de la base de datos
	 * @return List<Artista>
	 */
	public List<Artista> consultarArtistas(){
		List<Artista> lista;
		Session session = HibernateUtil.getSessionFactory().openSession();
		Query query = session.createQuery("SELECT a FROM Artista a");
		lista = query.list();
		session.close();
		return lista;
	}
	

//	public int totalArtistas() {
//		Session session = HibernateUtil.getSessionFactory().openSession();
//		Query query = session.createQuery("SELECT COUNT(a) FROM Artista a");
//		int total = (int) query.list().get(0);
//		session.close();
//		return total;
//	}

	


}
