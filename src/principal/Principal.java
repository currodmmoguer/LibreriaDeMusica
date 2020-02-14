package principal;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.hibernate.Session;
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

	public static void main(String[] args) {
		HibernateUtil.getSessionFactory();
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

//Artista

	/**
	 * Da de alta en la base de datos un nuevo artista
	 * 
	 * @throws ReproductorException en caso de que ya exista un artista con ese
	 *                              nombre
	 */
	private static void altaArtista() throws ReproductorException {
		String nombre = Util.solicitarCadena("Introduce el nombre del artista: ");
		ArtistaDAO dao = new ArtistaDAO();

		if (dao.existeArtista(nombre)) // Comprueba si existe un artista con dicho nombre
			throw new ReproductorException("Ya existe un artista con nombre " + nombre);

		dao.guardar(new Artista(nombre));
	}

	/**
	 * Borra un artista de la base de datos. Pregunta si borrar sus canciones
	 * también
	 * 
	 * @throws ReproductorException
	 */
	private static void bajaArtista() throws ReproductorException {
		ArtistaDAO daoArtista = new ArtistaDAO();
		CancionDAO daoCancion = new CancionDAO();
		Artista artista = buscarArtista();
		List<Integer> listaIdCanciones = new ArrayList<>();
		Cancion cancion;

		if (Util.solicitarSN("Seguro que quieres borrar el artista " + artista.getNombre() + "? (S/N)")) { // Si decide
																											// borrar el
																											// artista
			for (Cancion c : artista.getCanciones()) {
				listaIdCanciones.add(c.getId()); // Añade el id de la canción a una lista
				c.getArtistas().remove(artista); // Elimina el artista de la canción
				daoCancion.actualizar(c); // Lo actualiza en la base de datos
			}
			artista.getCanciones().clear(); // Vacía la lista de canciones
			daoArtista.borrar(artista); // Borra el artista

			// En caso de que quisiera borrar las canciones, las borraria obtieniendo las
			// canciones por sus id anteriormente guardado en una lista
			if (Util.solicitarSN("¿Deseas borrar también sus canciones? (S/N)")) {
				for (Integer i : listaIdCanciones) {
					cancion = daoCancion.getCancion(i); // Obtiene el objeto cancion
					if (cancion.getArtistas().isEmpty()) {
						daoCancion.borrar(cancion); // Borra la canción de la base de datos
					}
				}
			}
		}
	}

	/**
	 * Muestra por consola todos los artistas de la base de datos
	 */
	private static void mostrarTodosArtista() {
		ArtistaDAO dao = new ArtistaDAO();
		List<Artista> artistas = dao.consultarArtistas(); // Obtiene todos los artistas de la db
		if (artistas.size() > 0) {
			System.out.println("ID\tArtista");
			artistas.stream().forEach(a -> System.out.println(a.getId() + "\t" + a.getNombre()));
		} else { // En caso que no exista artistas en la base de datos
			System.out.println("Aun no hay ningún artista.");
		}

	}

	/**
	 * Muestra todos los álbunes de un artista
	 * 
	 * @throws ReproductorException
	 */
	private static void mostrarAlbunesArtista() throws ReproductorException {
		Artista artista = buscarArtista(); // Obtiene el artista
		AlbumDAO dao = new AlbumDAO();
		List<Album> albunes = dao.obtenerAlbunesPorArtista(artista.getId()); // Obtiene la lista de álbunes

		if (albunes.size() == 0) // En caso de que no tenga ningun album
			throw new ReproductorException(artista.getNombre() + " no tiene álbunes.");

		albunes.stream()
				.forEach(a -> System.out.println("- " + a.getNombre() + " (" + a.getPublicacion().getYear() + ")"));
	}

	/**
	 * Cambia de nombre a un artista
	 * 
	 * @throws ReproductorException
	 */
	private static void cambiarNombreArtista() throws ReproductorException {
		ArtistaDAO dao = new ArtistaDAO();
		Artista artista = buscarArtista();
		String nombre = Util.solicitarCadena("Introduce el nuevo nombre para artista: ");

		if (dao.existeArtista(nombre))
			throw new ReproductorException("Ya existe un artista con nombre " + nombre);

		artista.cambiarNombre(nombre);
		dao.actualizar(artista);
	}

	/**
	 * Muestra las canciones de un artista Hay que añadir la opcion de ordenar
	 * alfabéticamente, por fecha
	 * 
	 * @throws ReproductorException
	 */
	private static void mostrarCancionesArtista() throws ReproductorException {
		Artista artista = buscarArtista();

		if (artista.getCanciones().size() == 0) // En caso de que dicho artista no tenga canciones asignada
			throw new ReproductorException("El artista " + artista.getNombre() + " aun no tiene canciones.");

		System.out.println("Canciones de " + artista.getNombre());
		artista.getCanciones().stream().forEach(c -> System.out.println("- " + c.getNombre()));
	}

//Cancion

	/**
	 * Inserta una nueva canción en la base de datos
	 * 
	 * @throws ReproductorException
	 */
	private static void altaCancion() throws ReproductorException {
		CancionDAO dao = new CancionDAO();
		dao.guardar(crearObjetoCancion());
	}

	/**
	 * Solicita una canción y la borra de la base de datos
	 * 
	 * @throws ReproductorException en caso de que no encuentre la canción
	 */
	private static void bajaCancion() throws ReproductorException {
		Cancion cancion = buscarCancion();
		if (Util.solicitarSN("Seguro que deseas borrar la canción " + cancion.getNombre() + "? (S/N)")) {
			borrarCancion(cancion);
		}
	}

	/**
	 * Por cada artista tiene que borrar dicha canción de su lista ya que es una
	 * relacio N:M y si no daría fallo por las constricciones
	 * 
	 * @param cancion
	 */
	private static void borrarCancion(Cancion cancion) {
		CancionDAO daoCancion = new CancionDAO();
		ArtistaDAO daoArtista = new ArtistaDAO();
		for (Artista artista : cancion.getArtistas()) {
			artista.borrarCancion(cancion);
			daoArtista.actualizar(artista); // Tiene que actualizarlo en la base de datos
		}
		cancion.getArtistas().clear(); // Vacía su lista de artistas
		daoCancion.borrar(cancion);
	}

	private static void consultarTodasCanciones() {
		CancionDAO dao = new CancionDAO();
		System.out.println("ID\tCanción");
		dao.obtenerTodasCanciones().stream().forEach(System.out::println);
	}

	/**
	 * Crea un objeto Cancion solicitando los datos por consola
	 * 
	 * @return canción
	 * @throws ReproductorException
	 */
	public static Cancion crearObjetoCancion() throws ReproductorException {
		String nombre = Util.solicitarCadena("Introduce el nombre de la cancion: ");
		Set<Artista> artistas = solicitarArtistasCancion();

		// Comprobar si algun artista tiene esa cancion ya creada

		LocalTime duracion = Util.solicitarHora("Introduce la duración de la canción (MM:SS): ");
		LocalDate publicacion = Util.solicitarFecha("Introduce la fecha de publicación (DD/MM/AAAA)");
		Genero genero = Util.solicitarGenero();
		Cancion cancion = new Cancion(nombre, artistas, duracion, publicacion, genero);

		// Al ser una relación N:M hay que añadir a cada artista la canción creada a su
		// lista de canciones
		for (Artista a : artistas) {
			a.getCanciones().add(cancion);
		}

		return cancion;
	}

	/**
	 * Muestra los 3 generos mas escuchados de todas las canciones de la base de
	 * datos
	 */
	private static void consultarGenerosMasEscuchados() {
		CancionDAO dao = new CancionDAO();
		int pos = 1;
		for (Object[] o : dao.obtenerGeneroMasEscuchado()) {
			if ((long) o[0] == 1) // Controla el singular y plural
				System.out.println(pos + ". " + o[1].toString() + " con una canción");
			else
				System.out.println(pos + ". " + o[1].toString() + " con " + o[0] + " canciones");
			pos++;
		}
	}

	/**
	 * Solicita una lista de artista para la creación de canciones
	 * 
	 * @return Set<Artista>
	 */
	private static Set<Artista> solicitarArtistasCancion() {
		Set<Artista> lista = new HashSet<Artista>();
		List<Artista> listaObtenidos;
		String nombre;
		ArtistaDAO daoArtista = new ArtistaDAO();
		int id;
		System.out.println("Introduce el/los artista/s de la cancion. ");

		do {
			nombre = Util.solicitarCadena("Introduce el nombre del artista. Pulsa intro para terminar.");

			if (!nombre.equals("")) { // En caso de que introduzca un nombre
				listaObtenidos = daoArtista.obtenerListaArtistasPorNombre(nombre);

				if (listaObtenidos.isEmpty()) { // En caso de que no encuentre ninguna coincidencia pregunta si se crea

					if (Util.solicitarSN("El artista " + nombre + " no existe. ¿Deseas crearlo? (S/N)"))
						// Añade un artista nuevo
						lista.add(new Artista(nombre));

				} else if (listaObtenidos.size() == 1) { // Si encuentra solo 1 artista
					// Comprueba que el nombre del obtenido es exactamente igual que el introducido
					if (listaObtenidos.get(0).getNombre().equalsIgnoreCase(nombre)) {
						lista.add(listaObtenidos.get(0));
					} else { // Si tiene alguna diferencia pregunta si es ese para asegurarse
								// ya que obtiene la lista mediante una expresión regular
						if (Util.solicitarSN(nombre + " no se encontró pero " + listaObtenidos.get(0).getNombre()
								+ " sí, ¿es el que estabas buscando? (S/N)"))
							lista.add(listaObtenidos.get(0));
					}
				} else { // Si encuentra mas de una coincidencia
					// Muestra una lista de todas las coincidencias de artistas
					daoArtista.obtenerListaArtistasPorNombre(nombre).stream()
							.forEach(a -> System.out.println(a.getId() + ". " + a.getNombre()));

					// Si quiere añadir un artista que se encuentre en la lista
					if (Util.solicitarSN("¿Se encuentra en la lista? (S/N)")) {
						id = Util.solicitarEntero("Introduce del id del artista: ");

						if (artistaEstaEnLista(id, listaObtenidos)) // Comprueba que el id introducido esté en la lista
																	// de artista obtenido por el nombre
							lista.add(daoArtista.getArtista(id));

					} else if (Util.solicitarSN("¿Deseas crearlo? (S/N)")) { // En caso de que el nombre introducido no
																				// esté en la lista pregunta si se desea
																				// crear
						lista.add(new Artista(nombre));
					}
				}
			}
		} while (!nombre.equals(""));

		return lista;
	}

// Album
	/**
	 * Guarda las canciones x3 Si se crea nuevas canciones no se guarda en la tabla
	 * nm
	 * 
	 * @throws ReproductorException
	 */
	private static void altaAlbum() throws ReproductorException {
		AlbumDAO daoAlbum = new AlbumDAO();

		String nombre = Util.solicitarCadena("Introduce el nombre del album: ");
		Artista artista = null;

		try {
			artista = buscarArtista();
		} catch (ReproductorException re) {
			System.out.println(re.getMessage());
			if (Util.solicitarSN("¿Deseas crearlo? (S/N)")) {
				artista = new Artista(Util.solicitarCadena("Introduce el nombre del artista: "));
			}
		}

		if (artista != null) {
			Album album = new Album(nombre, artista, solicitarCanciones(artista),
					Util.solicitarFecha("Introduce la fecha de publicación (DD/MM/AAAA)"));

			album.getCanciones().stream().forEach(c -> c.setPublicacion(album.getPublicacion()));
			Session session = HibernateUtil.getSessionFactory().openSession();
			// canciones = null;//Olvida canciones
			session.evict(artista);

//			for (Cancion c : album.getCanciones()) { // Añade la publicacion y el album a cada cancion del album
//				if (c.getPublicacion() == null)
//					c.setPublicacion(album.getPublicacion());
//				c.setAlbum(album);
//			}
			session.close();

			// artista.getAlbunes().add(album);
//			artista.addAlbum(album);
			daoAlbum.guardar(album);
		}

	}

	/**
	 * Borra un album de la base de datos
	 */
	private static void bajaAlbum() {
		AlbumDAO dao = new AlbumDAO();
		Album album;
		List<Album> albunes = dao.obtenerListaAlbumPorNombre(Util.solicitarCadena("Introduce el nombre del album: "));
		albunes.stream().forEach(a -> System.out.println(a.getId() + "\t" + a.getNombre())); // Recorre la lista
																								// obtenida
		int id = Util.solicitarEntero("Introduce el id del album que deseas borrar:");
		if (Util.solicitarSN("Seguro que deseas borrar el album? Se borraran también las canciones (S/N)")) {
			album = dao.getAlbum(id);
			for (Cancion c : album.getCanciones()) {
				borrarCancion(c);
			}
			dao.borrar(dao.getAlbum(id));
		}

	}

	/**
	 * Muestra todos los datos de un album que solicita por consola
	 * 
	 * @throws ReproductorException
	 */
	private static void consultarAlbum() throws ReproductorException {
		System.out.println(buscarAlbum().toString());
	}

	/**
	 * Muestra por consola todos los albunes de la base de datos
	 */
	private static void mostrarAlbunes() {
		AlbumDAO dao = new AlbumDAO();
		dao.obtenerTodosAlbunes().stream().forEach(System.out::println);
	}

	/**
	 * Solicita las canciones para añadirlas a un album Salta excepcion
	 * NonUniqueException porque se instancia mas de una vez la misma cancion
	 * 
	 * @param artista
	 * @return lista total de canciones
	 * @throws ReproductorException
	 */
	private static List<Cancion> solicitarCanciones(Artista artista) throws ReproductorException {
		CancionDAO daoCancion = new CancionDAO();
		List<Cancion> canciones = new LinkedList<Cancion>();
		List<Cancion> cancionesExistentes = daoCancion.obtenerCancionesDeUnArtistaSinAlbum(artista);
		int id;
		Cancion cancion = null;
		boolean seguir;
		// Session session = HibernateUtil.getSessionFactory().openSession();

		do {
			seguir = true;

			if (cancionesExistentes.isEmpty()) { // En caso de que el artista no tenga ninguna cancion con album
													// asignado lo crea directamente. Se pone dentro del bucle en caso
													// de que haya mas de uno muestre siempre la lista
				cancion = Util.crearObjetoCancionParaAlbum(artista);
			} else {
				System.out.println("Canciones de " + artista.getNombre());
				cancionesExistentes.stream().forEach(c -> System.out.println(c.getId() + "\t" + c.getNombre()));

				if (Util.solicitarSN("¿Se encuentra en la lista? (S/N)?")) {
					id = Util.solicitarEntero("Introduce el id de la canción: ");
					cancion = daoCancion.getCancion(id);
					cancionesExistentes.remove(cancion);
				} else {
					cancion = Util.crearObjetoCancionParaAlbum(artista);
				}

			}

			canciones.add(cancion);
			// session.evict(cancion);

			if (!Util.solicitarSN("¿Añadir más canciones? (S/N)"))
				seguir = false;

		} while (seguir);

//		for (Cancion c : cancionesExistentes) {
//			session.evict(c);
//		}

		// session.close();

		return canciones;
	}

// Playlist

	/**
	 * Da de alta una playlist en la base de datos
	 */
	private static void altaPlaylist() {
		PlaylistDAO dao = new PlaylistDAO();
		String nombre = Util.solicitarCadena("Introduce el nombre de la playlist: ");
		Playlist playlist = new Playlist(nombre,
				Util.solicitarCadena("Introduce la descripción para la playlist " + nombre));
		dao.guardar(playlist);
		System.out.println("Se ha creado correctamente la playlist " + nombre);
	}

	/**
	 * Elimina una playlist en la base de datos
	 * 
	 * @throws ReproductorException
	 */
	private static void bajaPlaylist() throws ReproductorException {
		PlaylistDAO dao = new PlaylistDAO();
		Playlist playlist = buscarPlaylist();
		if (Util.solicitarSN("Seguro que deseas borrar la playlist " + playlist.getNombre() + "? (S/N)"))
			dao.borrar(playlist);
	}

	/**
	 * Cambia el nombre a una playlist
	 * 
	 * @throws ReproductorException
	 */
	private static void cambiarNombrePlaylist() throws ReproductorException {
		PlaylistDAO dao = new PlaylistDAO();
		Playlist playlist = buscarPlaylist();
		playlist.cambiarNombre(Util.solicitarCadena("Introduce el nuevo nombre para la playlist: "));
		dao.actualizar(playlist);
	}

	/**
	 * Cambia la descripcion a una playlist
	 * 
	 * @throws ReproductorException
	 */
	private static void cambiarDescripcionPlaylist() throws ReproductorException {
		PlaylistDAO dao = new PlaylistDAO();
		Playlist playlist = buscarPlaylist();
		playlist.cambiarDescripcion(Util.solicitarCadena("Introduce la nueva descripción para la playlist: "));
		dao.actualizar(playlist);
	}

	/**
	 * Añade una canción a una playlist
	 * 
	 * @throws ReproductorException
	 */
	private static void añadirCanciones() throws ReproductorException {
		PlaylistDAO daoPlaylist = new PlaylistDAO();
		Playlist playlist = buscarPlaylist(); // Obtiene la playlist buscada
		System.out.println(playlist);
		Cancion c = buscarCancion();

		playlist.addCancion(c); // Añade la cancion al objeto playlist
		System.out.println(playlist);
		HibernateUtil.getSessionFactory().openSession().evict(c);
		daoPlaylist.actualizar(playlist); // Actualiza la playlist en la base de datos

	}

	/**
	 * Hay que controlar que si pones otro id salta nullpointer Controlar que cuando
	 * se borre la ultima cancion preguntar si eliminar tambien la playlist
	 * 
	 * @throws ReproductorException
	 */
	private static void eliminarCancionPlaylist() throws ReproductorException {
		PlaylistDAO daoPlaylist = new PlaylistDAO();
		CancionDAO daoCancion = new CancionDAO();
		Playlist playlist = buscarPlaylist();
		Cancion cancion;
		// Muestra todas las canciones de la playlist
		playlist.getCanciones().stream().forEach(c -> System.out.println(c.getId() + "\t" + c.getNombre()));
		// Obtiene la canción por ID
		cancion = daoCancion.getCancion(Util.solicitarEntero("Introduce el id de la canción que deseas eliminar: "));

		playlist.eliminarCancion(cancion);
		daoPlaylist.actualizar(playlist);
		System.out.println("Eliminado correctamente la canción " + cancion.getNombre() + " de " + playlist.getNombre());
	}

	/**
	 * Muestra por consola el nombre, descripción y duración de todas las playlists
	 */
	private static void mostrarTodasPlaylist() {
		PlaylistDAO dao = new PlaylistDAO();
		List<Playlist> lista = dao.obtenerTodasPlaylists();
		if (lista == null || lista.isEmpty())
			System.out.println("Aún no existen playlists.");
		else
			lista.stream().forEach(System.out::println);
	}

	/**
	 * Muestra todos los datos de una playlist que se solicita por consola
	 * 
	 * @throws ReproductorException
	 */
	private static void consultarPlaylist() throws ReproductorException {
		Playlist playlist = buscarPlaylist();

		// Nombre playlist y descripcion
		System.out.println(playlist.getNombre() + "\n" + playlist.getDescripcion());
		// Numero de canciones y la duración completa de la playlist
		System.out.println(
				"Total canciones: " + playlist.getCanciones().size() + "\tDuración: " + playlist.getDuracion());
		// Todas las canciones de la playlist
		playlist.getCanciones().stream().forEach(c -> System.out.println(c.getNombreConArtistas()));
	}

	/**
	 * Muestra el genero más escuchado de una playlist que se solicita por consola
	 * Si da tiempo, si el primero y el segundo es la misma cantidad ponerlo
	 * 
	 * @throws ReproductorException
	 */
	private static void consultarGeneroMasEscuchadoDePlaylist() throws ReproductorException {
		Playlist playlist = buscarPlaylist();
		PlaylistDAO dao = new PlaylistDAO();

		if (playlist.getCanciones().size() == 0)
			throw new ReproductorException("La playlist no tiene canciones.");

		Object[] lista = dao.obtenerGeneroMasEscuchado(playlist);

		System.out.println("El género más escuchado es " + lista[1] + " con un total de " + lista[0] + " canciones.");
	}

//Búsquedas

	/**
	 * Busca un album en la base de datos introduciendo su nombre mediante
	 * expresiones regulares
	 * 
	 * @return Album
	 * @throws ReproductorException
	 */
	private static Album buscarAlbum() throws ReproductorException {
		AlbumDAO dao = new AlbumDAO();
		Album album = null;
		String nombre;
		List<Album> listaAlbumnes;
		int id;
		do {
			nombre = Util.solicitarCadena("Introduce el nombre del album: ");
			listaAlbumnes = dao.obtenerListaAlbumPorNombre(nombre);

			if (listaAlbumnes.size() == 0)
				throw new ReproductorException("No se ha encontrado coincidencias.");

			// Comprueba las coincidencias obtenidad mediante la expresión regular
			if (listaAlbumnes.size() == 1) { // En caso de que solo encuentre una coincidencia
				if (!listaAlbumnes.get(0).getNombre().equalsIgnoreCase(nombre)) { // Si el nombre no coincide
																					// exactamente pregunta si el
																					// obtenido es el que quiere el
																					// usuario
					System.out.println("Se ha encontrado sólo " + listaAlbumnes.get(0).getNombre());
					if (Util.solicitarSN("¿Es éste album el que buscabas? (S/N)"))
						album = listaAlbumnes.get(0);
				} else { // Si el nombre es exactamente igual lo añade directamente si preguntar nada mas
					album = listaAlbumnes.get(0);
				}
			} else { // En caso que se encuentre varias coincidencias
				System.out.println("ID\tAlbum");
				listaAlbumnes.stream().forEach(
						a -> System.out.println(a.getId() + "\t" + a.getNombre() + " - " + a.getArtista().getNombre()));

				do {
					id = Util.solicitarEntero("Introduce el id del album que deseas ver: ");
				} while (!albumEstaEnLista(id, listaAlbumnes));

				album = dao.getAlbum(id);
			}
		} while (album == null);

		return album;
	}

	/**
	 * Comprueba si un album está en una lista
	 * 
	 * @param id    album a buscar
	 * @param lista de albunes a buscar
	 * @return si la encuentra o no
	 */
	private static boolean albumEstaEnLista(int id, List<Album> lista) {
		boolean encontrada = false;
		int pos = 0;

		while (!encontrada && pos < lista.size()) {
			if (lista.get(pos).getId() == id) {
				encontrada = true;
			}
			pos++;
		}
		return encontrada;
	}

	/**
	 * Busca una playlist en la base de datos introduciendo su nombre mediante
	 * expresiones regulares
	 * 
	 * @return
	 * @throws ReproductorException
	 */
	private static Playlist buscarPlaylist() throws ReproductorException {
		PlaylistDAO dao = new PlaylistDAO();
		Playlist playlist = null;
		String nombre;
		List<Playlist> listaPlaylists;
		int id;

		do {
			nombre = Util.solicitarCadena("Introduce el nombre de la playlist:");
			listaPlaylists = dao.buscarPlaylist(nombre);

			if (listaPlaylists.size() == 0)
				throw new ReproductorException("No se ha encontrado coincidencias de artista con ese nombre.");

			if (listaPlaylists.size() == 1) {
				if (!listaPlaylists.get(0).getNombre().equalsIgnoreCase(nombre)) {
					System.out.println(
							listaPlaylists.get(0).getNombre() + " - " + listaPlaylists.get(0).getDescripcion());

					if (Util.solicitarSN("¿Es esta playlist la que buscabas? (S/N)"))
						playlist = listaPlaylists.get(0);
				} else {
					playlist = listaPlaylists.get(0);
				}
			} else {
				System.out.println("ID\tPlaylist");
				listaPlaylists.stream().forEach(p -> System.out.println(p.getId() + "\t" + p.getNombre()));
				do {
					id = Util.solicitarEntero("Introduce el id de la playlist: ");
				} while (!playlistEstaEnLista(id, listaPlaylists));

				playlist = dao.getPlaylist(id);
			}
		} while (playlist == null);

		return playlist;
	}

	/**
	 * Comprueba si una playlist está en una lista
	 * 
	 * @param id    playlist a buscar
	 * @param lista de playlists a buscar
	 * @return si la encuentra o no
	 */
	private static boolean playlistEstaEnLista(int id, List<Playlist> lista) {
		boolean encontrada = false;
		int pos = 0;

		while (!encontrada && pos < lista.size()) {
			if (lista.get(pos).getId() == id) {
				encontrada = true;
			}
			pos++;
		}
		return encontrada;
	}

	/**
	 * Cuando haya un minimo, no preguntar nombre, sino mostrar lista de todos Si el
	 * nombre no coincide exactamente con el devuelto avisarlo
	 * 
	 * @throws ReproductorException
	 */
	private static Artista buscarArtista() throws ReproductorException {
		ArtistaDAO dao = new ArtistaDAO();
		Artista artista = null;
		String nombre;
		List<Artista> listaArtistas;
		int id;

		do {
			nombre = Util.solicitarCadena("Introduce el nombre del artista: ");
			listaArtistas = dao.obtenerListaArtistasPorNombre(nombre);

			if (listaArtistas.size() == 0)
				throw new ReproductorException("No se ha encontrado coincidencias de artistas con ese nombre.");

			if (listaArtistas.size() == 1) {
				if (!listaArtistas.get(0).getNombre().equalsIgnoreCase(nombre)) {

					if (Util.solicitarSN(nombre + " no se encontró pero " + listaArtistas.get(0).getNombre()
							+ " sí, ¿es el que estabas buscando? (S/N)"))
						artista = listaArtistas.get(0);
				} else {
					artista = listaArtistas.get(0);
				}
			} else {
				System.out.println("ID\tArtista");
				listaArtistas.stream().forEach(a -> System.out.println(a.getId() + ". " + a.getNombre()));
				do {
					id = Util.solicitarEntero("Introduce del id del artista: ");
				} while (!artistaEstaEnLista(id, listaArtistas));

				artista = dao.getArtista(id);
			}
		} while (artista == null);
		return artista;
	}

	/**
	 * Comprueba si un artista está en una lista
	 * 
	 * @param id    artista a buscar
	 * @param lista de artistas a buscar
	 * @return si la encuentra o no
	 */
	private static boolean artistaEstaEnLista(int id, List<Artista> lista) {
		boolean encontrada = false;
		int pos = 0;

		while (!encontrada && pos < lista.size()) {
			if (lista.get(pos).getId() == id) {
				encontrada = true;
			}
			pos++;
		}
		return encontrada;
	}

	/**
	 * Busca una canción en la base de datos introduciendo su nombre
	 * 
	 * @return la canción deseada
	 * @throws ReproductorException
	 */
	private static Cancion buscarCancion() throws ReproductorException {
		CancionDAO dao = new CancionDAO();
		Cancion cancion = null;
		String nombre;
		List<Cancion> listaCanciones;
		int id;

		do {
			nombre = Util.solicitarCadena("Introduce el nombre de la canción: ");
			listaCanciones = dao.obtenerCancionPorNombre(nombre); // Obtiene todas las coinciendias

			if (listaCanciones.size() == 0) // No encuentra ninguna
				throw new ReproductorException("No se ha encontrado coincidencias de canciones con ese nombre.");

			if (listaCanciones.size() == 1) { // Encuentra solo 1
				if (!listaCanciones.get(0).getNombre().equals(nombre)) { // Si no coincide exactamente con el nombre
					System.out.println(listaCanciones.get(0).getNombreConArtistas());
					if (Util.solicitarSN("¿Es la canción que deseas? (S/N)"))
						cancion = listaCanciones.get(0);
				} else {
					cancion = listaCanciones.get(0);
				}

			} else {
				System.out.println("ID\tCanciones");
				listaCanciones.stream().forEach(c -> System.out.println(c.getId() + "\t" + c.getNombreConArtistas()));
				do {
					id = Util.solicitarEntero("Introduce el id de la canción: ");
				} while (!cancionEstaEnLista(id, listaCanciones));

				cancion = dao.getCancion(id); // Obtiene la cancion por el id
			}
		} while (cancion == null);
		return cancion;
	}

	/**
	 * Comprueba si una canción está en una lista
	 * 
	 * @param id    canción a buscar
	 * @param lista de canciones a buscar
	 * @return si la encuentra o no
	 */
	private static boolean cancionEstaEnLista(int id, List<Cancion> lista) {
		boolean encontrada = false;
		int pos = 0;

		while (!encontrada && pos < lista.size()) {
			if (lista.get(pos).getId() == id) {
				encontrada = true;
			}
			pos++;
		}
		return encontrada;
	}

//Tratamiento de menus

	private static void tratarMenuCancion(int opc) throws ReproductorException {
		switch (opc) {
		case 1:
			altaCancion();
			break;
		case 2:
			bajaCancion();
			break;
		case 3:
			consultarGenerosMasEscuchados();
			break;
		case 4:
			consultarTodasCanciones();
			break;
		}
	}

	private static void tratarMenuArtista(int opc) throws ReproductorException {
		switch (opc) {
		case 1:
			altaArtista();
			break;
		case 2:
			bajaArtista();
			break;
		case 3:
			mostrarTodosArtista();
			break;
		case 4:
			mostrarAlbunesArtista();
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
			altaAlbum(); // Sin terminar
			break;
		case 2:
			bajaAlbum();
			break;
		case 3:
			consultarAlbum();
			break;
		case 4:
			mostrarAlbunes();
			break;
		}
	}

	private static void tratarMenuPlaylist(int opc) throws ReproductorException {
		switch (opc) {
		case 1:
			altaPlaylist();
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
			consultarGeneroMasEscuchadoDePlaylist();
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

}
