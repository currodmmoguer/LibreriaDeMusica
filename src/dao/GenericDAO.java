package dao;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.hibernate.Session;
import modelo.HibernateUtil;
import modelo.ReproductorException;

public class GenericDAO<T> {
	Session session;
	
	public GenericDAO(Session session) {
		this.session = session;
	}

	/**
	 * Guarda una entidad en la base de datos
	 * 
	 * @param entidad
	 * @return
	 * @throws ReproductorException 
	 */
	public T guardar(T entidad) throws ReproductorException {
		try {
			session.save(entidad);
		} catch (ConstraintViolationException cve) {
			for(ConstraintViolation cv : cve.getConstraintViolations()) {
				System.err.println("El campo " + cv.getPropertyPath() + " " + cv.getMessage());
			}
			throw new ReproductorException("Error al guardar");
		}

		return entidad;
	}
	
	

	/**
	 * Elimina una entidad de la base de datos
	 * 
	 * @param entidad
	 * @throws ReproductorException 
	 */
	public void borrar(T entidad) throws ReproductorException {
		try {
			session.delete(entidad);
		} catch (ConstraintViolationException cve) {
			for(ConstraintViolation cv : cve.getConstraintViolations()) {
				System.err.println("El campo " + cv.getPropertyPath() + " " + cv.getMessage());
			}
			throw new ReproductorException("Error al borrar");
		}
	}

	/**
	 * Actualiza una entidad en la base de datos
	 * 
	 * @param entidad
	 * @throws ReproductorException 
	 */
	public void actualizar(T entidad) throws ReproductorException {
		try {
			session.update(entidad);
		} catch (ConstraintViolationException cve) {
			for(ConstraintViolation cv : cve.getConstraintViolations()) {
				System.err.println("El campo " + cv.getPropertyPath() + " " + cv.getMessage());
			}
			throw new ReproductorException("Error al acutalizar");
		} 

	}
	
	public void merge(T entidad) {
		try {
			session.merge(entidad);
		} catch (ConstraintViolationException cve) {
			session.getTransaction().rollback();
			System.out.println("Error al actualizar");
		} 

	}

}
