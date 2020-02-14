package dao;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.hibernate.Session;
import modelo.HibernateUtil;

public class GenericDAO<T> {

	/**
	 * Guarda una entidad en la base de datos
	 * 
	 * @param entidad
	 * @return
	 */
	public T guardar(T entidad) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		try {
			session.beginTransaction();
			session.save(entidad);
			session.getTransaction().commit();
		} catch (ConstraintViolationException cve) {
			session.getTransaction().rollback();
			System.out.println("Error al guardar");
			for(ConstraintViolation cv : cve.getConstraintViolations()) {
				System.out.println("Campo: " + cv.getPropertyPath());
			}
		} finally {
			session.close();
		}

		return entidad;
	}

	/**
	 * Elimina una entidad de la base de datos
	 * 
	 * @param entidad
	 */
	public void borrar(T entidad) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		try {
			session.beginTransaction();
			session.delete(entidad);
			session.getTransaction().commit();
		} catch (ConstraintViolationException cve) {
			session.getTransaction().rollback();
			System.out.println("Error al borrar");
		} finally {
			session.close();
		}
	}

	/**
	 * Actualiza una entidad en la base de datos
	 * 
	 * @param entidad
	 */
	public void actualizar(T entidad) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		try {
			session.beginTransaction();
			session.update(entidad);
			session.getTransaction().commit();
		} catch (ConstraintViolationException cve) {
			session.getTransaction().rollback();
			System.out.println("Error al actualizar");
		} finally {
			session.close();
		}

	}

}
