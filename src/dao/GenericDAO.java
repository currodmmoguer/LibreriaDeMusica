package dao;

import org.hibernate.Session;

import modelo.HibernateUtil;

public class GenericDAO<T> {
	
	/**
	 * Guarda una entidad en la base de datos
	 * @param entidad
	 * @return
	 */
	public T guardar(T entidad) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		session.save(entidad);
		session.getTransaction().commit();
		session.close();
		return entidad;
	}
	
	/**
	 * Elimina una entidad de la base de datos
	 * @param entidad
	 */
	public void borrar(T entidad) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		session.delete(entidad);
		session.getTransaction().commit();
		session.close();
	}
	
	/**
	 * Actualiza una entidad en la base de datos
	 * @param entidad
	 */
	public void actualizar(T entidad) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		session.update(entidad);
		session.getTransaction().commit();
		session.close();
	}
	

	

}
