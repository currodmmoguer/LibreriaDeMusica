package principal;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
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

		HibernateUtil.closeSessionFactory();

	}

	private static void prueba() {
		System.out.println("entra");
		PlaylistDAO dao = new PlaylistDAO();

		for (Playlist p : dao.buscarPlaylist("lay")) {
			System.out.println(p);
		}
	}

//Artista
	private static void nuevoArtista() throws ReproductorException {
		crearArtista();

	}

	private static void crearArtista() throws ReproductorException {
		String nombre = Util.solicitarCadena("Introduce el nombre del artista: ");
		ArtistaDAO dao = new ArtistaDAO();
		if (dao.obtenerArtistaPorNombre(nombre) != null)
			throw new ReproductorException("Ya existe un artista con nombre " + nombre);

		Artista artista = new Artista(nombre);
		dao.guardar(artista);

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
	
	private static void borrarArtista(Artista artista) {
		ArtistaDAO dao = new ArtistaDAO();
		Cancion cancion;

		Iterator<Cancion> it = artista.getCanciones().iterator();
		while (it.hasNext()) {
			cancion = it.next();
			// borrarCancion(cancion);
		}
		artista.getCanciones().clear();

		dao.borrar(artista);

	}
	
	/**
	 * Muesta las canciones de un artista Hay que añadir la opcion de ordenar
	 * alfabéticamente, por fecha Posibiliad de mostrar si se parecen o no el nombre
	 * del artista (regex)
	 * 
	 * @throws ReproductorException
	 */
	private static void mostrarCancionesArtista() throws ReproductorException {
		ArtistaDAO daoArtista = new ArtistaDAO();
		List<Artista> listaArtistas = daoArtista
				.obtenerListaArtistasPorNombre(Util.solicitarCadena("Introduce el nombre del artista: "));
		Artista artista = buscarArtista();

		for (Cancion c : artista.getCanciones()) {
			System.out.println(c.getNombre());
		}

	}

	private static void bajaArtista() throws ReproductorException {
		ArtistaDAO dao = new ArtistaDAO();
		String nombre = Util.solicitarCadena("Introduce el nombre del artista: ");

		char opc;
		Artista artista = buscarArtista();

		opc = Util.solicitarSN("Seguro que quieres borrar el artista " + nombre + "? (S/N)");

		if (opc == 'S') {
			borrarArtista(artista);
		}

	}

//Cancion

	private static void nuevaCancion() throws ReproductorException {
		CancionDAO dao = new CancionDAO();
		Cancion cancion = crearObjetoCancion();
		dao.guardar(cancion);

	}
	
	/**
	 * No deja borrarlo
	 * 
	 * @throws ReproductorException
	 */
	private static void bajaCancion() throws ReproductorException {
		CancionDAO dao = new CancionDAO();
		String nombre = Util.solicitarCadena("Introduce el nombre de la cancion");
		char opc;
		List<Cancion> lista = dao.obtenerCancionPorNombre(nombre);
		Cancion cancion = buscarCancion();

		opc = Util.solicitarSN("Seguro que deseas borrar la canción " + cancion.getNombre() + "? (S/N)");

		if (opc == 'S') {
			borrarCancion(cancion);

		}

	}
	
	private static void borrarCancion(Cancion cancion) {
		CancionDAO daoCancion = new CancionDAO();
		ArtistaDAO daoArtista = new ArtistaDAO();

		for (Artista artista : cancion.getArtistas()) {
			artista.borrarCancion(cancion);
			daoArtista.actualizar(artista);
		}
		cancion.getArtistas().clear();
		daoCancion.borrar(cancion);

	}
	
	/**
	 * Salta excepcion al añadir cuando ya hay alguna (preguntar merge)
	 * 
	 * @throws ReproductorException
	 */
	private static void añadirCanciones() throws ReproductorException {
		CancionDAO daoCancion = new CancionDAO();
		PlaylistDAO daoPlaylist = new PlaylistDAO();
		Playlist playlist = buscarPlaylist();
		Cancion cancion = buscarCancion();

		playlist.addCancion(cancion);

		daoPlaylist.actualizar(playlist);

	}

	private static Cancion crearObjetoCancion() throws ReproductorException {

		String nombre = Util.solicitarCadena("Introduce el nombre de la cancion");
		ArrayList<Artista> artistas = solicitarArtistasCancion();

		Album album = solicitarAlbum();
		LocalTime duracion = solicitarDuracion();
		LocalDate publicacion = solicitarPublicacion();
		Genero genero = solicitarGenero();
		Cancion cancion = new Cancion(nombre, artistas, album, duracion, publicacion, genero);
		for (Artista a : artistas) {
			a.getCanciones().add(cancion);
		}

		return cancion;
	}

	private static Cancion crearObjetoCancionParaAlbum(Artista a) throws ReproductorException {

		String nombre = Util.solicitarCadena("Introduce el nombre de la cancion");
		List<Artista> listaArtistas = new ArrayList<Artista>();
		listaArtistas.add(a);
		// Album album = solicitarAlbum();
		LocalTime duracion = solicitarDuracion();
		// LocalDate publicacion = solicitarPublicacion();
		Genero genero = solicitarGenero();
		Cancion cancion = new Cancion(nombre, listaArtistas, duracion, genero);
		return cancion;
	}

	private static Genero solicitarGenero() {

		int pos = 1;
		for (Genero g : Genero.values()) {
			System.out.println(pos + ". " + g.toString());
			pos++;
		}

		int posGenero = Util.solicitarEntero("Introduce la posición del genero deseado.");
		Genero genero = Genero.getGenero(posGenero - 1);

		return genero;
	}

	private static LocalDate solicitarPublicacion() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		LocalDate publicacion;
		String str = Util.solicitarCadena("Introduce la fecha de publicación (DD/MM/AAAA)");
		publicacion = LocalDate.parse(str, formatter);
		return publicacion;
	}

	private static LocalTime solicitarDuracion() {
		LocalTime duracion;
		String str = Util.solicitarCadena("Introduce la duración de la canción (MM:SS): ");
		duracion = LocalTime.parse(str);
		return duracion;
	}

	private static Album solicitarAlbum() {
		Album album = null;
		char opcion = Util.solicitarSN("Pertenece a un album? (S/N)");
		if (opcion == 'S') {
			String nombre = Util.solicitarCadena("Introduce el nombre del album: ");
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
			nombre = Util.solicitarCadena("Introduce el nombre del artista");
			if (!nombre.equals("")) {
				artista = daoArtista.obtenerArtistaPorNombre(nombre);
				System.out.println("Artista-> " + artista);
				if (artista != null) {
					lista.add(artista);
				} else {
					opcion = Util.solicitarSN("El artista " + nombre + " no existe. ¿Deseas crearlo? (S/N)");
					if (opcion == 'S') {
						artista = daoArtista.guardar(new Artista(nombre));
						lista.add(artista);
					}

				}
			}

		} while (!nombre.equals(""));

		return lista;
	}

	private static void nuevoAlbum() throws ReproductorException {
		AlbumDAO daoAlbum = new AlbumDAO();

		String nombre = Util.solicitarCadena("Introduce el nombre del album: ");
		Artista artista = buscarArtista();

		List<Cancion> canciones = solicitarCanciones(artista);
		LocalDate publicacion = solicitarPublicacion();
		Album album = new Album(nombre, artista, canciones, publicacion);

		for (Cancion c : album.getCanciones()) { // Añade la publicacion y el album a cada cancion del album
			if (c.getPublicacion() == null)
				c.setPublicacion(album.getPublicacion());
			c.setAlbum(album);
		}

		daoAlbum.guardar(album);

	}

	private static List<Cancion> solicitarCanciones(Artista artista) throws ReproductorException {
		CancionDAO daoCancion = new CancionDAO();
		List<Cancion> canciones = new LinkedList<Cancion>();
		List<Cancion> cancionesExistentes = daoCancion.obtenerCancionesDeUnArtista(artista);
		char opc;
		int id;
		Cancion cancion = null;
		boolean correcto, seguir;

		System.out.println("Canciones de " + artista.getNombre());
		cancionesExistentes.stream().forEach(c -> System.out.println(c.getId() + "\t" + c.getNombre()));

		do {
			correcto = true;
			seguir = true;
			System.out.println("Añadir canciones");
			opc = Util.solicitarSN("¿Se encuentra en la lista? (S/N)?");

			if (opc == 'S') {
				id = Util.solicitarEntero("Introduce el id de la canción: ");
				cancion = daoCancion.obtenerCancionPorId(id);
			} else if (opc == 'N') {
				cancion = crearObjetoCancionParaAlbum(artista);

			} else {
				correcto = false;
			}

			if (correcto)
				canciones.add(cancion);

			opc = Util.solicitarSN("¿Añadir más canciones? (S/N)");

			if (opc == 'N')
				seguir = false;

		} while (seguir);

		return canciones;
	}

	private static void nuevaPlaylist() {
		String nombre = Util.solicitarCadena("Introduce el nombre de la playlist: ");
		String descripcion = Util.solicitarCadena("Introduce la descripción para la playlist " + nombre);

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

	









	private static void bajaAlbum() {
		AlbumDAO dao = new AlbumDAO();
		String nombre = Util.solicitarCadena("Introduce el nombre del album: ");
		List<Album> albunes = dao.obtenerListaAlbumPorNombre(nombre);

		for (Album album : albunes) { // Hacerlo con lambda
			System.out.println(album.getId() + "\t" + album.getNombre());
		}
		int id = Util.solicitarEntero("Introduce el id del album que deseas borrar:");

		// Preguntar si deseas borrar las canciones o no
		dao.borrar(dao.consultarAlbum(id));
	}



	private static void eliminarCancionPlaylist() throws ReproductorException {
		PlaylistDAO daoPlaylist = new PlaylistDAO();
		CancionDAO daoCancion = new CancionDAO();
		Playlist playlist = buscarPlaylist();
		Cancion cancion;
		int id;

		playlist.getCanciones().stream().forEach(c -> System.out.println(c.getId() + "\t" + c.getNombre()));
		id = Util.solicitarEntero("Introduce el id de la canción que deseas eliminar: ");
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
		artista.cambiarNombre(Util.solicitarCadena("Introduce el nuevo nombre para artista: "));
		dao.actualizar(artista);
	}

	private static void consultarPlaylist() throws ReproductorException {
		StringBuilder sb = new StringBuilder();
		Playlist playlist = buscarPlaylist();

		for (Cancion c : playlist.getCanciones()) {
			sb.append(c.getNombre() + " - ");
			for (Artista a : c.getArtistas()) {
				sb.append(a.getNombre() + ", ");
			}
			sb.delete(sb.length() - 2, sb.length()); // Quita la ultima ,
			sb.append("\n");
		}
		System.out.println(playlist.getNombre() + "\n" + playlist.getDescripcion());
		System.out.println(
				"Total canciones: " + playlist.getCanciones().size() + "\tDuración: " + playlist.getDuracion());
		System.out.println(sb.toString());

	}

	private static void cambiarNombrePlaylist() throws ReproductorException {
		PlaylistDAO dao = new PlaylistDAO();
		Playlist playlist = buscarPlaylist();
		playlist.cambiarNombre(Util.solicitarCadena("Introduce el nuevo nombre para la playlist: "));
		dao.actualizar(playlist);
	}

	private static void cambiarDescripcionPlaylist() throws ReproductorException {
		PlaylistDAO dao = new PlaylistDAO();
		Playlist playlist = buscarPlaylist();
		playlist.cambiarDescripcion(Util.solicitarCadena("Introduce la nueva descripción para la playlist: "));
		dao.actualizar(playlist);
	}

	private static void bajaPlaylist() throws ReproductorException {
		PlaylistDAO dao = new PlaylistDAO();
		String nombre = Util.solicitarCadena("Introduce el nombre de la playlist: ");
		List<Playlist> lista = dao.buscarPlaylist(nombre);
		char opc;
		Playlist playlist = buscarPlaylist();

		opc = Util.solicitarSN("Seguro que deseas borrar la playlist " + playlist.getNombre() + "? (S/N)");
		if (opc == 'S')
			dao.borrar(playlist);

	}

	private static void consultarAlbum() throws ReproductorException {
		Album album = buscarAlbum();
		System.out.println(
				album.getNombre() + ", " + album.getArtista().getNombre() + " (" + album.getPublicacion() + ")");
		int pos = 1;
		for (Cancion cancion : album.getCanciones()) {
			System.out.println(pos + ". " + cancion.getNombre());
			pos++;
		}
	}

//Búsquedas

	/**
	 * Busca un album en la base de datos introduciendo su nombre
	 * @return
	 * @throws ReproductorException
	 */
	private static Album buscarAlbum() throws ReproductorException {
		AlbumDAO dao = new AlbumDAO();
		Album album = null;
		char opc;
		String nombre;
		List<Album> listaAlbumnes;

		do {
			nombre = Util.solicitarCadena("Introduce el nombre del album: ");
			listaAlbumnes = dao.obtenerListaAlbumPorNombre(nombre);
			if (listaAlbumnes.size() == 0)
				throw new ReproductorException("No se ha encontrado coincidencias.");

			if (listaAlbumnes.size() == 1) {
				if (!listaAlbumnes.get(0).getNombre().equalsIgnoreCase(nombre)) {
					System.out.println("Se ha encontrado sólo " + listaAlbumnes.get(0).getNombre() + " de "
							+ listaAlbumnes.get(0).getArtista().getNombre());
					opc = Util.solicitarSN("¿Es éste album el que buscabas? (S/N)");
					if (opc == 'S')
						album = listaAlbumnes.get(0);
				}
				album = listaAlbumnes.get(0);
			}

			else {
				for (Album a : listaAlbumnes) {
					System.out.println(a.getId() + "\t" + a.getNombre());
				}
				System.out.println("Introduce el id del album que deseas ver: ");
				album = dao.consultarAlbum(Integer.parseInt(teclado.nextLine()));

			}
		} while (album == null);
		return album;

	}

	/**
	 * Busca una playlist en la base de datos
	 * 
	 * @return
	 * @throws ReproductorException
	 */
	private static Playlist buscarPlaylist() throws ReproductorException {
		PlaylistDAO dao = new PlaylistDAO();
		Playlist playlist = null;
		int id;
		char opc;
		String nombre;
		List<Playlist> listaPlaylists;

		do {
			nombre = Util.solicitarCadena("Introduce el nombre de la playlist:");
			listaPlaylists = dao.buscarPlaylist(nombre);

			if (listaPlaylists.size() == 0)
				throw new ReproductorException("No se ha encontrado coincidencias de artista con ese nombre.");

			if (listaPlaylists.size() == 1) {
				if (!listaPlaylists.get(0).getNombre().equalsIgnoreCase(nombre)) {
					System.out.println(listaPlaylists.get(0).getNombre() + " - " + listaPlaylists.get(0).getDescripcion());
					opc = Util.solicitarSN("¿Es esta playlist la que buscabas? (S/N)");
					if (opc == 'S')
						playlist = listaPlaylists.get(0);
				} else {
					playlist = listaPlaylists.get(0);
				}

			} else {
				for (Playlist p : listaPlaylists) {
					System.out.println(p.getId() + "\t" + p.getNombre());
				}
				id = Util.solicitarEntero("Introduce el id de la playlist: ");
				playlist = dao.getPlaylist(id);
			}
		} while (playlist == null);
		return playlist;
	}

	/**
	 * Cuando haya un minimo, no preguntar nombre, sino mostrar lista de todos Si el
	 * nombre no coincide exactamente con el devuelto avisarlo
	 * 
	 * @throws ReproductorException
	 */
	private static Artista buscarArtista() throws ReproductorException {
		ArtistaDAO dao = new ArtistaDAO();
		int id;
		char opc;
		Artista artista = null;
		String nombre;
		List<Artista> listaArtistas;
		
		do {
			nombre = Util.solicitarCadena("Introduce el nombre del artista: ");
			listaArtistas = dao.obtenerListaArtistasPorNombre(nombre);

			if (listaArtistas.size() == 0)
				throw new ReproductorException("No se ha encontrado coincidencias de artistas con ese nombre.");

			if (listaArtistas.size() == 1) {
				if (!listaArtistas.get(0).getNombre().equalsIgnoreCase(nombre)) {
					opc = Util.solicitarSN(nombre + " no se encontró pero " + listaArtistas.get(0).getNombre()
							+ " sí, ¿es el que estabas buscando? (S/N)");
					if (opc == 'S')
						artista = listaArtistas.get(0);
				} else {
					artista = listaArtistas.get(0);
				}
			} else {
				for (Artista a : listaArtistas) {
					System.out.println(a.getId() + ". " + a.getNombre());
				}
				id = Util.solicitarEntero("Introduce del id del artista: ");
				artista = dao.getArtista(id);
			}
		} while (artista == null);
		return artista;
	}

	/**
	 * Busca una canción en la base de datos introduciendo su nombre
	 * 
	 * @return la canción deseada
	 * @throws ReproductorException
	 */
	private static Cancion buscarCancion() throws ReproductorException {
		CancionDAO dao = new CancionDAO();
		int id;
		Cancion cancion = null;
		char opc;
		String nombre;
		List<Cancion> listaCanciones;

		do {
			nombre = Util.solicitarCadena("Introduce el nombre de la canción: ");
			listaCanciones = dao.obtenerCancionPorNombre(nombre); // Obtiene todas las coinciendias

			if (listaCanciones.size() == 0) // No encuentra ninguna
				throw new ReproductorException("No se ha encontrado coincidencias de canciones con ese nombre.");

			if (listaCanciones.size() == 1) { // Encuentra solo 1
				if (!listaCanciones.get(0).getNombre().equals(nombre)) { // Si no coincide exactamente con el nombre
					System.out.println(listaCanciones.get(0).getNombreConArtistas());
					opc = Util.solicitarSN("¿Es la canción que deseas? (S/N)");
					if (opc == 'S')
						cancion = listaCanciones.get(0);
				} else {
					cancion = listaCanciones.get(0);
				}

			} else {
				for (Cancion c : listaCanciones) { // Muestra todas las coincidencias
					System.out.print(c.getId() + ". " + c.getNombreConArtistas());
				}
				id = Util.solicitarEntero("Introduce el id de la canción: ");
				cancion = dao.obtenerCancionPorId(id); // Obtiene la cancion por el id
			}
		} while (cancion == null);
		return cancion;
	}

//Tratamiento de menus

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
			bajaArtista(); // Sin hacer
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
			consultarAlbum();
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
			consultarGeneroMasEscuchadoDePlaylist();
			break;
		}
	}

	private static void consultarGeneroMasEscuchadoDePlaylist() throws ReproductorException {
		Playlist playlist = buscarPlaylist();
		PlaylistDAO dao = new PlaylistDAO();
		
		List<Object[]> lista = dao.obtenerGeneroMasEscuchado(playlist);
		
		for(Object[] o: lista) {
			System.out.println(o[0] + " - " + o[1]);
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

//Util



}
