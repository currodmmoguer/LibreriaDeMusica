package dao;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.hibernate.Session;
import modelo.ReproductorException;

public class GenericDAO<T> {
	Session session;

	public GenericDAO(Session session) {
		this.session = session;
	}

	/**
	 * Guarda una entidad en la base de datos
	 * 
	 * @param T entidad a guardar
	 * @return entidad guardada
	 * @throws ReproductorException En caso que salte excepción por las validaciones
	 */
	public T guardar(T entidad) throws ReproductorException {
		try {
			session.save(entidad);
		} catch (ConstraintViolationException cve) {
			for (ConstraintViolation cv : cve.getConstraintViolations()) {	//Por cada fallo en las validaciones, muestra el campo y el mensaje de error
				System.err.println("El campo " + cv.getPropertyPath() + " " + cv.getMessage());
			}
			throw new ReproductorException("Error al guardar");	//Salta la excepción del reproductor para que la transacción termine y haga rollback
		}

		return entidad;
	}

	/**
	 * Elimina una entidad de la base de datos
	 * 
	 * @param T entidad a borrar
	 * @throws ReproductorException En caso que salte excepción por las validaciones
	 */
	public void borrar(T entidad) throws ReproductorException {
		try {
			session.delete(entidad);
		} catch (ConstraintViolationException cve) {
			for (ConstraintViolation cv : cve.getConstraintViolations()) {	//Por cada fallo en las validaciones, muestra el campo y el mensaje de error
				System.err.println("El campo " + cv.getPropertyPath() + " " + cv.getMessage());
			}
			throw new ReproductorException("Error al borrar");	//Salta la excepción del reproductor para que la transacción termine y haga rollback
		}
	}

	/**
	 * Actualiza una entidad en la base de datos
	 * 
	 * @param T entidad a actualizar
	 * @throws ReproductorException En caso que sale excepción por las validaciones<
	 */
	public void actualizar(T entidad) throws ReproductorException {
		try {
			session.update(entidad);
		} catch (ConstraintViolationException cve) {
			for (ConstraintViolation cv : cve.getConstraintViolations()) {	//Por cada fallo en las validaciones, muestra el campo y el mensaje de error
				System.err.println("El campo " + cv.getPropertyPath() + " " + cv.getMessage());
			}
			throw new ReproductorException("Error al acutalizar");	//Salta la excepción del reproductor para que la transacción termine y haga rollback
		}

	}

}
