package principal;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

import dao.AlbumDAO;
import dao.ArtistaDAO;
import dao.CancionDAO;
import dao.PlaylistDAO;
import modelo.Album;
import modelo.Artista;
import modelo.Cancion;
import modelo.Genero;
import modelo.HibernateUtil;
import modelo.Playlist;
import modelo.ReproductorException;

public class Principal {

	private static Scanner teclado = new Scanner(System.in);

	public static void main(String[] args) {
		// SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

		// prueba();
		int opc = 0;
		do {
			try {
				opc = Menus.menuPrincipal();
				tratarMenuPrincipal(opc);
			} catch (ReproductorException e) {
				System.out.println(e.getMessage());
			}
		} while (opc != 5);

		// HibernateUtil.closeSessionFactory();

	}

	private static void prueba() {
		System.out.println("entra");
		PlaylistDAO dao = new PlaylistDAO();

		for (Playlist p : dao.buscarPlaylist("lay")) {
			System.out.println(p);
		}
	}

	private static void nuevoArtista() throws ReproductorException {
		crearArtista();

	}

	private static void crearArtista() throws ReproductorException {
		String nombre = solicitarCadena("Introduce el nombre del artista: ");
		ArtistaDAO dao = new ArtistaDAO();
		if (dao.obtenerArtistaPorNombre(nombre) != null)
			throw new ReproductorException("Ya existe un artista con nombre " + nombre);

		Artista artista = new Artista(nombre);
		dao.guardar(artista);

	}

	private static String solicitarCadena(String msg) {
		System.out.println(msg);
		String nombre = teclado.nextLine();
		// q no este vacia
		return nombre;
	}

	private static void nuevaCancion() throws ReproductorException {
		CancionDAO dao = new CancionDAO();
		Cancion cancion = crearObjetoCancion();
		dao.guardar(cancion);

	}

	private static Cancion crearObjetoCancion() throws ReproductorException {

		String nombre = solicitarCadena("Introduce el nombre de la cancion");
		ArrayList<Artista> artistas = solicitarArtistasCancion();
		System.out.println(artistas);
		Album album = solicitarAlbum();
		LocalTime duracion = solicitarDuracion();
		LocalDate publicacion = solicitarPublicacion();
		Genero genero = solicitarGenero();
		Cancion cancion = new Cancion(nombre, artistas, album, duracion, publicacion, genero);
		for (Artista a : artistas) {
			a.getCanciones().add(cancion);
		}
		System.out.println(cancion);
		return cancion;
	}

	private static Genero solicitarGenero() {

		int pos = 1;
		for (Genero g : Genero.values()) {
			System.out.println(pos + ". " + g.toString());
			pos++;
		}

		int posGenero = solicitarEntero("Introduce la posición del genero deseado.");
		Genero genero = Genero.getGenero(posGenero - 1);

		return genero;
	}

	private static LocalDate solicitarPublicacion() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		LocalDate publicacion;
		String str = solicitarCadena("Introduce la fecha de publicación de la cancion (DD/MM/AAAA)");
		publicacion = LocalDate.parse(str, formatter);
		return publicacion;
	}

	private static LocalTime solicitarDuracion() {
		LocalTime duracion;
		String str = solicitarCadena("Introduce la duración de la canción (MM:SS): ");
		duracion = LocalTime.parse(str);
		return duracion;
	}

	private static Album solicitarAlbum() {
		Album album = null;
		char opcion = solicitarSN("Pertenece a un album? (S/N)");
		if (opcion == 'S') {
			String nombre = solicitarCadena("Introduce el nombre del album: ");
			// Obtener album, tener cuidado con mismo nombre
		}

		return album;
	}

	private static ArrayList<Artista> solicitarArtistasCancion() {
		ArrayList<Artista> lista = new ArrayList<Artista>();
		String nombre;
		ArtistaDAO daoArtista = new ArtistaDAO();
		Artista artista;
		char opcion;

		System.out.println("Introduce el/los artista/s de la cancion. Pulsa intro para terminar.");
		do {
			nombre = solicitarCadena("Introduce el nombre del artista");
			if (!nombre.equals("")) {
				artista = daoArtista.obtenerArtistaPorNombre(nombre);
				System.out.println("Artista-> " + artista);
				if (artista != null) {
					lista.add(artista);
				} else {
					opcion = solicitarSN("El artista " + nombre + " no existe. ¿Deseas crearlo? (S/N)");
					if (opcion == 'S') {
						artista = daoArtista.guardar(new Artista(nombre));
						lista.add(artista);
					}

				}
			}

		} while (!nombre.equals(""));

		return lista;
	}

	/**
	 * Cuando haya un minimo, no preguntar nombre, sino mostrar lista de todos
	 * Si el nombre no coincide exactamente con el devuelto avisarlo
	 * 
	 * @throws ReproductorException
	 */
	private static Artista buscarArtista() throws ReproductorException {
		ArtistaDAO dao = new ArtistaDAO();
		String nombre = solicitarCadena("Introduce el nombre del artista: ");
		List<Artista> artistas = dao.obtenerListaArtistasPorNombre(nombre);
		int id;
		Artista artista;
		if (artistas.size() == 0)
			throw new ReproductorException("No se ha encontrado coincidencias de artistas con ese nombre.");

		if (artistas.size() == 1) {
			artista = artistas.get(0);
		} else {
			for (Artista a : artistas) {
				System.out.println(a.getId() + ". " + a.getNombre());
			}
			id = solicitarEntero("Introduce del id del artista: ");
			artista = dao.getArtista(id);
		}

		return artista;
	}
	
	private static void nuevoAlbum() throws ReproductorException {
		ArtistaDAO daoArtista = new ArtistaDAO();
		String nombre = solicitarCadena("Introduce el nombre del album: ");
		Artista artista = buscarArtista();

		
		
		
		//List<Cancion> canciones = solicitarCanciones(artista);

	}

	private static List<Cancion> solicitarCanciones(Artista artista) {
		ArrayList<Cancion> canciones = new ArrayList<Cancion>();
		int pos = 1;
		int opc;
		
		do {
			opc = Menus.menuAddCancionAlbum();
			tratarMenuAddCancionAlbum(opc);
		} while (opc != 3);
		
		for (Cancion c : artista.getCanciones()) {
			System.out.println(pos + ". " + c.getNombre());
		}

		// Hay que preguntar que introduza el numero de cada cancion
		return null;
	}

	private static void tratarMenuAddCancionAlbum(int opc) {
		// TODO Auto-generated method stub
		
	}

	private static void nuevaPlaylist() {
		String nombre = solicitarCadena("Introduce el nombre de la playlist: ");
		String descripcion = solicitarCadena("Introduce la descripción para la playlist " + nombre);

		Playlist playlist = new Playlist(nombre, descripcion);
		PlaylistDAO dao = new PlaylistDAO();

		dao.guardar(playlist);
		System.out.println("Se ha creado correctamente la playlist " + nombre);
	}

	private static void mostrarTodasPlaylist() {
		PlaylistDAO dao = new PlaylistDAO();
		List<Playlist> playlists = dao.obtenerTodasPlaylists();
		System.out.println("ID\tNombre");
		for (Playlist p : playlists) {
			System.out.println(p.getId() + "\t" + p.getNombre());
		}

	}



	private static void mostrarTodosArtista() {
		ArtistaDAO dao = new ArtistaDAO();
		List<Artista> artistas = dao.consultarArtistas();
		if (artistas.size() > 0) {
			System.out.println("ID\tNombre");
			for (Artista a : artistas) {
				System.out.println(a.getId() + "\t" + a.getNombre());
			}
		} else {
			System.out.println("Aun no hay ningún artista.");
		}

	}

	/**
	 * No deja borrarlo
	 * 
	 * @throws ReproductorException
	 */
	private static void bajaCancion() throws ReproductorException {
		CancionDAO dao = new CancionDAO();
		String nombre = solicitarCadena("Introduce el nombre de la cancion");
		char opc;
		List<Cancion> lista = dao.obtenerCancionPorNombre(nombre);
		Cancion cancion;
		if (lista.size() == 0 || lista == null) // No encuentra la cancion
			throw new ReproductorException("No existe ninguna canción con ese nombre.");

		if (lista.size() == 1) { // Solo hay una coincidencia
			opc = solicitarSN("Seguro que quieres borrar la cancion " + nombre + "? (S/N)");

			if (opc == 'S')
				borrarCancion(lista.get(0));

		} else { // Mas de 1 coincidencia
			for (Cancion c : lista) { // Muestra todas las canciones
				System.out.println(c.getId() + "\t" + c.getNombre());
			}
			System.out.println("Introduce el id de la canción que deseas eliminar: ");
			cancion = dao.obtenerCancionPorId(Integer.parseInt(teclado.nextLine()));

			opc = solicitarSN("Seguro que deseas borrar la canción " + cancion.getNombre() + "? (S/N)");

			if (opc == 'S') {
				borrarCancion(cancion);
				
			}
		}

	}

	private static void borrarCancion(Cancion cancion) {
		CancionDAO daoCancion = new CancionDAO();
		ArtistaDAO daoArtista = new ArtistaDAO();
		
		for(Artista artista: cancion.getArtistas()) {
			artista.borrarCancion(cancion);
			daoArtista.actualizar(artista);
		}
		cancion.getArtistas().clear();
		daoCancion.borrar(cancion);
		
	}
	
	private static void borrarArtista(Artista artista) {
		ArtistaDAO dao = new ArtistaDAO();
		Cancion cancion;
		
		Iterator<Cancion> it = artista.getCanciones().iterator();
		while (it.hasNext()) {
			cancion = it.next();
			//borrarCancion(cancion);
		}
		artista.getCanciones().clear();

		dao.borrar(artista);
		
	}
	/**
	 * Muesta las canciones de un artista Hay que añadir la opcion de ordenar
	 * alfabéticamente, por fecha Posibiliad de mostrar si se parecen o no el nombre
	 * del artista (regex)
	 * @throws ReproductorException 
	 */
	private static void mostrarCancionesArtista() throws ReproductorException {
		ArtistaDAO daoArtista = new ArtistaDAO();
		List<Artista> listaArtistas = daoArtista.obtenerListaArtistasPorNombre(solicitarCadena("Introduce el nombre del artista: "));
		Artista artista;
		if (listaArtistas.size() == 0)
			throw new ReproductorException("No se ha encontrado coincidencias.");
		
		System.out.println("Canciones:");
		if (listaArtistas.size() == 1) {
			for(Cancion c: listaArtistas.get(0).getCanciones()) {
				System.out.println(c.getNombre());
			}
		} else {
			for(Artista a: listaArtistas) {
				System.out.println(a.getId() + "\t" + a.getNombre());
			}
			System.out.println("Introduce el id del artista que deseas ver sus canciones: ");
			artista = daoArtista.getArtista(Integer.parseInt(teclado.nextLine()));
			
			for (Cancion c: artista.getCanciones()) {
				System.out.println(c.getNombre());
			}
		}
		
		


	}

	private static void bajaArtista() throws ReproductorException {
		ArtistaDAO dao = new ArtistaDAO();
		String nombre = solicitarCadena("Introduce el nombre del artista: ");
		List<Artista> listaArtista = dao.obtenerListaArtistasPorNombre(nombre);
		char opc;
		Artista artista;
		if (listaArtista.size() == 0)
			throw new ReproductorException("No se ha encontrado coincidencias.");
		
		if (listaArtista.size() == 1) {
			opc = solicitarSN("Seguro que quieres borrar el artista " + nombre + "? (S/N)");
			
			if (opc == 'S') {
				borrarArtista(listaArtista.get(0));
			}
		} else {
			for(Artista a: listaArtista) {
				System.out.println(a.getId() + "\t" + a.getNombre());
			}
			System.out.println("Introduce el id del artista que deseas eliminar: ");
			artista = dao.getArtista(Integer.parseInt(teclado.nextLine()));
			
			opc = solicitarSN("Seguro que quieres borrar el artista " + nombre + "? (S/N)");
			if (opc == 'S') {
				borrarArtista(artista);
			}
		}
	}

	private static void bajaAlbum() {
		AlbumDAO dao = new AlbumDAO();
		String nombre = solicitarCadena("Introduce el nombre del album: ");
		List<Album> albunes = dao.obtenerListaAlbumPorNombre(nombre);

		for (Album album : albunes) { // Hacerlo con lambda
			System.out.println(album.getId() + "\t" + album.getNombre());
		}
		int id = solicitarEntero("Introduce el id del album que deseas borrar:");

		// Preguntar si deseas borrar las canciones o no
		dao.borrar(dao.consultarAlbum(id));
	}

	private static void bajaPlaylist() throws ReproductorException {
		PlaylistDAO dao = new PlaylistDAO();
		String nombre = solicitarCadena("Introduce el nombre de la playlist: ");
		List<Playlist> lista = dao.buscarPlaylist(nombre);
		char opc;
		Playlist playlist;

		if (lista.size() == 0 || lista == null)
			throw new ReproductorException("No se ha encontrado ninguna playlist con ese nombre");

		if (lista.size() == 1) {
			opc = solicitarSN("Seguro que deseas borrar la playlist " + nombre + "? (S/N)");
			if (opc == 'S')
				dao.borrar(lista.get(0));
		} else {
			for (Playlist p : lista) {
				System.out.println(p.getId() + "\t" + p.getNombre());
			}
			System.out.println("Introduce el id de la playlist que deseas eliminar: ");
			playlist = dao.getPlaylist(Integer.parseInt(teclado.nextLine()));

			opc = solicitarSN("Seguro que deseas borrar la playlist " + playlist.getNombre() + "? (S/N)");
			if (opc == 'S')
				dao.borrar(playlist);

		}

	}

	/**
	 * Salta excepcion al añadir cuando ya hay alguna (preguntar merge)
	 */
	private static void añadirCanciones() {
		CancionDAO daoCancion = new CancionDAO();
		PlaylistDAO daoPlaylist = new PlaylistDAO();
		System.out.println("Introduce id de la playlist:");
		Playlist playlist = daoPlaylist.getPlaylist(Integer.parseInt(teclado.nextLine()));
		System.out.println("Introduce id de la canción: ");
		Cancion cancion = daoCancion.obtenerCancionPorId(Integer.parseInt(teclado.nextLine()));

		playlist.addCancion(cancion);

		daoPlaylist.actualizar(playlist);

	}
	
	private static void eliminarCancionPlaylist() throws ReproductorException {
		PlaylistDAO daoPlaylist = new PlaylistDAO();
		CancionDAO daoCancion = new CancionDAO();
		Playlist playlist = buscarPlaylist();
		Cancion cancion;
		int id;
		
		playlist.getCanciones().stream().forEach(c -> System.out.println(c.getId() +"\t"+c.getNombre()));
		id = solicitarEntero("Introduce el id de la canción que deseas eliminar: ");
		cancion = daoCancion.obtenerCancionPorId(id);
		playlist.eliminarCancion(cancion);
		
		daoPlaylist.actualizar(playlist);
		System.out.println("Eliminado correctamente la canción " + cancion.getNombre() + " de " + playlist.getNombre());
	}



	private static void consultarGenerosMasEscuchados() {
		CancionDAO dao = new CancionDAO();
		List<Object[]> canciones = dao.obtenerGeneroMasEscuchado();

		for (Object[] o : canciones) {
			System.out.println(o[0] + "\t" + o[1].toString());
		}

	}

	private static void cambiarNombreArtista() throws ReproductorException {
		ArtistaDAO dao = new ArtistaDAO();
		Artista artista = buscarArtista();
		artista.cambiarNombre(solicitarCadena("Introduce el nuevo nombre para artista: "));
		dao.actualizar(artista);
	}



	/**
	 * Si coincide el regex pero no el nombre y solo hay 1 lo pilla
	 * tiene que comprobar que el nombre sea exacatemente igual
	 * ej: ppp, si pongo p y no hay mas me la coje directamente esa
	 * @return
	 * @throws ReproductorException
	 */
	private static Playlist buscarPlaylist() throws ReproductorException {
		PlaylistDAO dao = new PlaylistDAO();
		String nombre = solicitarCadena("Introduce el nombre de la playlist:");
		List<Playlist> playlists = dao.buscarPlaylist(nombre);
		Playlist playlist;
		int id;

		if (playlists.size() == 0)
			throw new ReproductorException("No se ha encontrado coincidencias de artista con ese nombre.");

		if (playlists.size() == 1) {
			playlist = playlists.get(0);

		} else {
			for (Playlist p : playlists) {
				System.out.println(p.getId() + "\t" + p.getNombre());
			}
			id = solicitarEntero("Introduce el id de la playlist: ");
			playlist = dao.getPlaylist(id);
		}
		return playlist;
	}
	
	private static void consultarPlaylist() throws ReproductorException {
		StringBuilder sb = new StringBuilder();
		Playlist playlist = buscarPlaylist();
		
		for (Cancion c : playlist.getCanciones()) {
			sb.append(c.getNombre() + " - ");
			for (Artista a : c.getArtistas()) {
				sb.append(a.getNombre() + ", ");
			}
			sb.delete(sb.length() - 2, sb.length()); //Quita la ultima ,
			sb.append("\n");
		}
		System.out.println(playlist.getNombre() + "\n" + playlist.getDescripcion());
		System.out.println("Total canciones: " + playlist.getCanciones().size() + "\tDuración: " + playlist.getDuracion());
		System.out.println(sb.toString());
		
	}

	private static void cambiarNombrePlaylist() throws ReproductorException {
		PlaylistDAO dao = new PlaylistDAO();
		Playlist playlist = buscarPlaylist();
		playlist.cambiarNombre(solicitarCadena("Introduce el nuevo nombre para la playlist: "));
		dao.actualizar(playlist);
	}
	
	
	private static void cambiarDescripcionPlaylist() throws ReproductorException {
		PlaylistDAO dao = new PlaylistDAO();
		Playlist playlist = buscarPlaylist();
		playlist.cambiarDescripcion(solicitarCadena("Introduce la nueva descripción para la playlist: "));
		dao.actualizar(playlist);
	}
	
	private static void tratarMenuCancion(int opc) throws ReproductorException {
		switch (opc) {
		case 1:
			nuevaCancion(); // No guarda artista
			break;
		case 2:
			bajaCancion();
			break;
		case 3:
			consultarGenerosMasEscuchados();
			break;
		}
	}

	private static void tratarMenuArtista(int opc) throws ReproductorException {
		switch (opc) {
		case 1:
			nuevoArtista();
			break;
		case 2:
			bajaArtista(); //Sin hacer
			break;
		case 3:
			mostrarTodosArtista();
			break;
		case 4:
			
			break;
		case 5:
			mostrarCancionesArtista();
			break;
		case 6:
			cambiarNombreArtista();
			break;
		}
	}



	private static void tratarMenuAlbum(int opc) throws ReproductorException {
		switch (opc) {
		case 1:
			nuevoAlbum(); // Sin terminar
			break;
		case 2:
			bajaAlbum();
			break;
		case 3:
			break;
		}
	}

	private static void tratarMenuPlaylist(int opc) throws ReproductorException {
		switch (opc) {
		case 1:
			nuevaPlaylist();
			break;
		case 2:
			bajaPlaylist();
			break;
		case 3:
			cambiarNombrePlaylist();
			break;
		case 4:
			cambiarDescripcionPlaylist();
			break;
		case 5:
			añadirCanciones();
			break;
		case 6:
			eliminarCancionPlaylist();
			break;
		case 7:
			mostrarTodasPlaylist();
			break;
		case 8:
			consultarPlaylist();
			break;
		case 9:
			break;
		case 10:
			break;
		}
	}







	private static void tratarMenuPrincipal(int opc) throws ReproductorException {
		int eleccion;
		switch (opc) {
		case 1:
			eleccion = Menus.menuCancion();
			tratarMenuCancion(eleccion);
			break;
		case 2:
			eleccion = Menus.menuArtistas();
			tratarMenuArtista(eleccion);
			break;
		case 3:
			eleccion = Menus.menuAlbum();
			tratarMenuAlbum(eleccion);
			break;
		case 4:
			eleccion = Menus.menuPlaylist();
			tratarMenuPlaylist(eleccion);
			break;
		}
	}

	private static int solicitarEntero(String msg) {
		System.out.println(msg);
		int entero = Integer.parseInt(teclado.nextLine());
		return entero;
	}

	public static char solicitarSN(String msg) {
		System.out.println(msg);
		char c = teclado.nextLine().toUpperCase().charAt(0);
		return c;
	}

}
