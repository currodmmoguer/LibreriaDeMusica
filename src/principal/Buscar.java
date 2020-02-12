package principal;

import java.util.List;

import dao.AlbumDAO;
import dao.ArtistaDAO;
import modelo.ReproductorException;

public class Buscar<T, K> {
	
	public T buscar(K dao) throws ReproductorException {
		T entidad = null;
		String nombre;
		List<T> lista = null;
		AlbumDAO albumDao = new AlbumDAO();
		ArtistaDAO artistaDao = new ArtistaDAO();
		
		
		do {
			nombre = Util.solicitarCadena("Introduce el nombre del " + entidad.getClass().getName() + ": ");
			if (dao instanceof AlbumDAO) {
				lista = (List<T>) albumDao.obtenerListaAlbumPorNombre(nombre);
			} else if (dao instanceof ArtistaDAO) {
				lista = (List<T>) artistaDao.obtenerListaArtistasPorNombre(nombre);
			}
			
			if (lista.size() == 0)
				throw new ReproductorException("No se ha encontrado coincidencias.");
			
			if (lista.size() == 1) {
				if (!lista.get(0).getN)
			}
				
		} while (entidad == null);
		return entidad;
	}

}
