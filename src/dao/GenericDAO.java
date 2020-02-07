package dao;

import org.hibernate.Session;

import modelo.HibernateUtil;

public class GenericDAO<T> {
	
	public void guardar(T entidad) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		session.save(entidad);
		session.getTransaction().commit();
		session.close();
	}
	
	public  void borrar(T entidad) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		session.delete(entidad);
		session.getTransaction().commit();
		session.close();
	}
	
	public void actualizar(T entidad) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		session.update(entidad);
		session.getTransaction().commit();
		session.close();
	}
	

}
