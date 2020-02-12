package principal;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
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

	private static Scanner teclado = new Scanner(System.in);

	public static void main(String[] args) {

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
	 * Sin terminar Borra un artista de la base de datos. No borra sus canciones.
	 * --Poder preguntarlo, salta constrainf violation
	 * 
	 * @throws ReproductorException
	 */
	private static void bajaArtista() throws ReproductorException {
		ArtistaDAO daoArtista = new ArtistaDAO();
		CancionDAO daoCancion = new CancionDAO();
		Artista artista = buscarArtista();

		if (Util.solicitarSN("Seguro que quieres borrar el artista " + artista.getNombre() + "? (S/N)")) { // Si decide
																											// borrar el
																											// artista
			if (Util.solicitarSN("¿Deseas borrar también sus canciones? (S/N)")) { // Si decide borrar sus canciones
				for (Cancion c : artista.getCanciones()) {
					artista.borrarCancion(c);
					c.setArtistas(null);
					daoCancion.borrar(c);
				}
			} else {
				daoArtista.borrar(artista);
			}
		}
	}

//	private static void borrarArtista(Artista artista) {
//	ArtistaDAO dao = new ArtistaDAO();
//	Cancion cancion;
//
//	Iterator<Cancion> it = artista.getCanciones().iterator();
//	while (it.hasNext()) {
//		cancion = it.next();
//		// borrarCancion(cancion);
//	}
//	artista.getCanciones().clear();
//
//	dao.borrar(artista);
//
//}
//

	/**
	 * Muestra por consola todos los artistas de la base de datos
	 */
	private static void mostrarTodosArtista() {
		ArtistaDAO dao = new ArtistaDAO();
		List<Artista> artistas = dao.consultarArtistas(); // Obtiene todos los artistas de la db
		if (artistas.size() > 0) {
			System.out.println("Artistas:");
			artistas.stream().forEach(a -> System.out.println("- " + a.getNombre()));
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

		albunes.stream().forEach(a -> System.out.println(a.getNombre())); // Lo muestra por consola los álbunes
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

		artista.getCanciones().stream().forEach(c -> System.out.println(c.getNombre()));
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
	 * Borra la canción de la base de datos
	 * 
	 * @throws ReproductorException en caso de que no encuentre la canción
	 */
	private static void bajaCancion() throws ReproductorException {
		CancionDAO daoCancion = new CancionDAO();
		ArtistaDAO daoArtista = new ArtistaDAO();
		Cancion cancion = buscarCancion();

		if (Util.solicitarSN("Seguro que deseas borrar la canción " + cancion.getNombre() + "? (S/N)")) {
			// Por cada artista tiene que borrar dicha canción de su lista
			// ya que es una relacio N:M y si no daría fallo por las constricciones
			for (Artista artista : cancion.getArtistas()) {
				artista.borrarCancion(cancion);
				daoArtista.actualizar(artista); // Tiene que actualizarlo en la base de datos
			}
			cancion.getArtistas().clear(); // Vacía su lista de artistas
			daoCancion.borrar(cancion);
		}
	}

	/**
	 * Crea un objeto Cancion solicitando los datos por consola
	 * 
	 * @return canción
	 * @throws ReproductorException
	 */
	private static Cancion crearObjetoCancion() throws ReproductorException {
		String nombre = Util.solicitarCadena("Introduce el nombre de la cancion: ");
		ArrayList<Artista> artistas = solicitarArtistasCancion();

		// Comprobar si algun artista tiene esa cancion ya creada

		LocalTime duracion = Util.solicitarHora("Introduce la duración de la canción (MM:SS): ");
		LocalDate publicacion = Util.solicitarFecha("Introduce la fecha de publicación (DD/MM/AAAA)");
		Genero genero = solicitarGenero();
		Cancion cancion = new Cancion(nombre, artistas, duracion, publicacion, genero);

		// Al ser una relación N:M hay que añadir a cada artista la canción creada a su
		// lista de canciones
		for (Artista a : artistas) {
			a.getCanciones().add(cancion);
		}

		return cancion;
	}

	/**
	 * Crea un objeto canción para añadir a un album. La diferencia con el anterior
	 * es que no solicita ni album ni fecha de publicación.
	 * 
	 * @param Artista
	 * @return Canción
	 * @throws ReproductorException
	 */
	private static Cancion crearObjetoCancionParaAlbum(Artista a) throws ReproductorException {

		String nombre = Util.solicitarCadena("Introduce el nombre de la cancion");
		List<Artista> listaArtistas = new ArrayList<Artista>();
		listaArtistas.add(a);
		LocalTime duracion = Util.solicitarHora("Introduce la duración de la canción (MM:SS): ");
		Genero genero = solicitarGenero();
		Cancion cancion = new Cancion(nombre, listaArtistas, duracion, genero);
		listaArtistas.get(0).getCanciones().add(cancion);
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
	 * Solicita el genero que puede tener una canción para su posterior asignación
	 * 
	 * @return genero
	 */
	private static Genero solicitarGenero() {
		int pos = 1;

		for (Genero g : Genero.values()) { // Recorre el enumerado de genero y lo muestra
			System.out.println(pos + ". " + g.toString());
			pos++;
		}

		int posGenero = Util.solicitarEnteroEnRango("Introduce la posición del genero deseado.", 1,
				Genero.values().length);
		return Genero.getGenero(posGenero - 1); // Obtiene el genero por su posición
	}

	/**
	 * Solicita una lista de artista para la creación de canciones
	 * 
	 * @return List<Artista>
	 */
	private static ArrayList<Artista> solicitarArtistasCancion() {
		ArrayList<Artista> lista = new ArrayList<Artista>();
		List<Artista> listaObtenidos;
		String nombre;
		ArtistaDAO daoArtista = new ArtistaDAO();

		System.out.println("Introduce el/los artista/s de la cancion. ");
		do {
			nombre = Util.solicitarCadena("Introduce el nombre del artista. Pulsa intro para terminar.");
			if (!nombre.equals("")) { // Si introduce algo
				listaObtenidos = daoArtista.obtenerListaArtistasPorNombre(nombre);
				if (listaObtenidos.size() > 0) { // En caso de que encuentre artistas con dicho nombre
					// Muestra una lista de todas las coincidencias de artistas
					daoArtista.obtenerListaArtistasPorNombre(nombre).stream()
							.forEach(a -> System.out.println(a.getId() + ". " + a.getNombre()));
					if (Util.solicitarSN("¿Se encuentra en la lista? (S/N)")) { // Si quiere añadir un artista que se
																				// encuentre en la lista
						lista.add(daoArtista.getArtista(Util.solicitarEntero("Introduce del id del artista: ")));
					} else {
						if (Util.solicitarSN("El artista " + nombre + " no existe. ¿Deseas crearlo? (S/N)"))
							lista.add(new Artista(nombre));
					}
				} else { // Si no encuentra coincidencias con el nombre puesto pregunta directamente si
							// se crea o no
					if (Util.solicitarSN("El artista " + nombre + " no existe. ¿Deseas crearlo? (S/N)"))
						lista.add(new Artista(nombre));
				}
			}
		} while (!nombre.equals("")); // Termina en caso que cuando solicite el artista le de a intro y no haya
										// escrito nada
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
		Artista artista = buscarArtista();
		List<Cancion> canciones = solicitarCanciones(artista);
		LocalDate publicacion = Util.solicitarFecha("Introduce la fecha de publicación (DD/MM/AAAA)");

		Album album = new Album(nombre, artista, canciones, publicacion);
		Session session = HibernateUtil.getSessionFactory().openSession();
		for (Cancion c : album.getCanciones()) { // Añade la publicacion y el album a cada cancion del album
			if (c.getPublicacion() == null)
				c.setPublicacion(album.getPublicacion());
			c.setAlbum(album);
			session.evict(c);
		}
		session.close();

		// artista.getAlbunes().add(album);
//		artista.addAlbum(album);
		daoAlbum.guardar(album);

	}

	/**
	 * Borra un album de la base de datos
	 */
	private static void bajaAlbum() {
		AlbumDAO dao = new AlbumDAO();
		List<Album> albunes = dao.obtenerListaAlbumPorNombre(Util.solicitarCadena("Introduce el nombre del album: "));
		albunes.stream().forEach(album -> System.out.println(album.getId() + "\t" + album.getNombre())); // Recorre la
																											// lista
																											// obtenida
		int id = Util.solicitarEntero("Introduce el id del album que deseas borrar:");
		if (Util.solicitarSN("Seguro que deseas borrar el album? (S/N)"))
			dao.borrar(dao.getAlbum(id));
	}

	/**
	 * Muestra todos los datos de un album que solicita por consola
	 * 
	 * @throws ReproductorException
	 */
	private static void consultarAlbum() throws ReproductorException {
		Album album = buscarAlbum();
		// Muestra los datos del album
		System.out.println(
				album.getNombre() + ", " + album.getArtista().getNombre() + " (" + album.getPublicacion() + ")");
		int pos = 1;
		// Muestra las canciones del album
		for (Cancion cancion : album.getCanciones()) {
			System.out.println(pos + ". " + cancion.getNombre());
			pos++;
		}
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
				cancion = crearObjetoCancionParaAlbum(artista);
			} else {
				System.out.println("Canciones de " + artista.getNombre());
				cancionesExistentes.stream().forEach(c -> System.out.println(c.getId() + "\t" + c.getNombre()));

				if (Util.solicitarSN("¿Se encuentra en la lista? (S/N)?")) {
					id = Util.solicitarEntero("Introduce el id de la canción: ");
					cancion = daoCancion.getCancion(id);
					cancionesExistentes.remove(cancion);
				} else {
					cancion = crearObjetoCancionParaAlbum(artista);
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
		playlist.addCancion(buscarCancion()); // Añade la cancion al objeto playlist
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

		playlist.getCanciones().stream().forEach(c -> System.out.println(c.getId() + "\t" + c.getNombre())); // Muestra
																												// todas
																												// las
																												// canciones
																												// de la
																												// playlist
		cancion = daoCancion
				.getCancion(Util.solicitarEntero("Introduce el id de la canción que deseas eliminar: ")); // Obtiene
																													// la
																													// canción
																													// por
																													// ID
		playlist.eliminarCancion(cancion);
		daoPlaylist.actualizar(playlist);
		System.out.println("Eliminado correctamente la canción " + cancion.getNombre() + " de " + playlist.getNombre());
	}

	/**
	 * Muestra por consola el nombre, descripción y duración de todas las playlists
	 */
	private static void mostrarTodasPlaylist() {
		PlaylistDAO dao = new PlaylistDAO();
		dao.obtenerTodasPlaylists().stream().forEach(p -> System.out
				.println(p.getNombre() + "\n\t" + p.getDescripcion() + "\n\tDuración: " + p.getDuracion() + "\n"));
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

		List<Object[]> lista = dao.obtenerGeneroMasEscuchado(playlist);

		System.out.println("El género más escuchado es " + lista.get(0)[1] + " con un total de " + lista.get(0)[0]
				+ " canciones.");
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
					System.out.println("Se ha encontrado sólo " + listaAlbumnes.get(0).getNombre() + " de "
							+ listaAlbumnes.get(0).getArtista().getNombre());
					if (Util.solicitarSN("¿Es éste album el que buscabas? (S/N)"))
						album = listaAlbumnes.get(0);
				} else { // Si el nombre es exactamente igual lo añade directamente si preguntar nada mas
					album = listaAlbumnes.get(0);
				}
			} else { // En caso que se encuentre varias coincidencias
				System.out.println("ID\tAlbum");
				listaAlbumnes.stream().forEach(
						a -> System.out.println(a.getId() + "\t" + a.getNombre() + " - " + a.getArtista().getNombre()));
				album = dao.getAlbum(Util.solicitarEntero("Introduce el id del album que deseas ver: "));
			}
		} while (album == null);

		return album;
	}
	

	/**
	 * Busca una playlist en la base de datos introduciendo su nombre mediante expresiones regulares
	 * 
	 * @return
	 * @throws ReproductorException
	 */
	private static Playlist buscarPlaylist() throws ReproductorException {
		PlaylistDAO dao = new PlaylistDAO();
		Playlist playlist = null;
		String nombre;
		List<Playlist> listaPlaylists;

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
				playlist = dao.getPlaylist(Util.solicitarEntero("Introduce el id de la playlist: "));
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
					
					if (Util.solicitarSN(nombre + " no se encontró pero " + listaArtistas.get(0).getNombre()
							+ " sí, ¿es el que estabas buscando? (S/N)"))
						artista = listaArtistas.get(0);
				} else {
					artista = listaArtistas.get(0);
				}
			} else {
				System.out.println("ID\tArtista");
				listaArtistas.stream().forEach(a -> System.out.println(a.getId() + ". " + a.getNombre()));
				artista = dao.getArtista(Util.solicitarEntero("Introduce del id del artista: "));
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
		Cancion cancion = null;
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
					if (Util.solicitarSN("¿Es la canción que deseas? (S/N)"))
						cancion = listaCanciones.get(0);
				} else {
					cancion = listaCanciones.get(0);
				}

			} else {
				System.out.println("ID\tCanciones");
				listaCanciones.stream().forEach(c -> System.out.println(c.getId() + ". " + c.getNombreConArtistas()));
				cancion = dao.getCancion(Util.solicitarEntero("Introduce el id de la canción: ")); // Obtiene la cancion por el id
			}
		} while (cancion == null);
		return cancion;
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
			break;
		case 10:
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
