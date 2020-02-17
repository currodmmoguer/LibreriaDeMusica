package principal;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
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
		HibernateUtil.getSessionFactory(); // Inicia la sesión
		System.out.println("\nGESTIÓN DE BASE DE DATOS DE REPRODUCTOR MULTIMEDIA");
		int opc = 0;

		do {
			opc = Menus.menuPrincipal();
			tratarMenuPrincipal(opc);
		} while (opc != 5);

		HibernateUtil.closeSessionFactory(); // Cierra la sessión

	}

//Artista

	/**
	 * Da de alta en la base de datos un nuevo artista
	 */

	private static void altaArtista() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		ArtistaDAO dao = new ArtistaDAO(session);
		String nombre = Util.solicitarCadena("Introduce el nombre del artista: ");
		session.beginTransaction();

		try {
			if (dao.existeArtista(nombre)) // Comprueba si existe un artista con dicho nombre
				throw new ReproductorException("Ya existe un artista con nombre " + nombre);

			dao.guardar(new Artista(nombre));
			session.getTransaction().commit();
			System.out.println("Se ha guardadao el artista " + nombre + " correctamente.");
		} catch (ReproductorException e) {
			System.err.println(e.getMessage());
			session.getTransaction().rollback();
		} finally {
			session.close();
		}

	}

	/**
	 * Borra un artista de la base de datos. Pregunta si borrar sus canciones
	 * también
	 */
	private static void bajaArtista() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		ArtistaDAO daoArtista = new ArtistaDAO(session);
		CancionDAO daoCancion = new CancionDAO(session);
		List<Integer> listaIdCanciones = new ArrayList<>();
		Cancion cancion;
		session.beginTransaction();

		try {
			Artista artista = buscarArtista(session);

			// Si decide borrar el artista
			if (Util.solicitarSN("Seguro que quieres borrar el artista " + artista.getNombre() + "? (S/N)")) {

				for (Cancion c : artista.getCanciones()) {
					listaIdCanciones.add(c.getId()); // Añade el id de la canción a una lista para en caso de querer
														// borrar las canciones tener una referencia
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
			session.getTransaction().commit();
		} catch (ReproductorException e) {
			System.err.println(e.getMessage());
			session.getTransaction().rollback();
		} finally {
			session.close();
		}

	}

	/**
	 * Muestra por consola todos los artistas de la base de datos
	 */
	private static void mostrarTodosArtista() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		ArtistaDAO dao = new ArtistaDAO(session);
		List<Artista> artistas = dao.consultarArtistas(); // Obtiene todos los artistas de la db

		if (artistas.size() > 0) {
			System.out.println("Lista de artista:");
			System.out.println("ID\tNombre");
			artistas.stream().forEach(a -> System.out.println(a.getId() + "\t" + a.getNombre()));
		} else { // En caso que no exista artistas en la base de datos
			System.out.println("Aun no hay ningún artista.");
		}

		session.close();
	}

	/**
	 * Muestra todos los álbumes de un artista
	 * 
	 */
	private static void mostrarAlbumesArtista() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		AlbumDAO dao = new AlbumDAO(session);

		try {
			Artista artista = buscarArtista(session);
			List<Album> albunes = dao.obtenerAlbunesPorArtista(artista); // Obtiene la lista de álbumes

			if (albunes.size() == 0) // En caso de que no tenga ningun album
				throw new ReproductorException(artista.getNombre() + " no tiene álbumes.");

			System.out.println("Albunes de " + artista.getNombre() + ":");
			albunes.stream()
					.forEach(a -> System.out.println("- " + a.getNombre() + " (" + a.getPublicacion().getYear() + ")"));
		} catch (ReproductorException e) {
			System.err.println(e.getMessage());
		} finally {
			session.close();
		}

	}

	/**
	 * Cambia de nombre a un artista
	 * 
	 */
	private static void cambiarNombreArtista() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		ArtistaDAO dao = new ArtistaDAO(session);

		try {
			Artista artista = buscarArtista(session);
			String nombre = Util.solicitarCadena("Introduce el nuevo nombre para artista: ");

			if (dao.existeArtista(nombre)) // Comprueba que no exista un artista con el nuevo nombre
				throw new ReproductorException("Ya existe un artista con nombre " + nombre);

			session.beginTransaction();
			artista.cambiarNombre(nombre);
			dao.actualizar(artista);
			session.getTransaction().commit();
			System.out.println("Se ha modificado el nombre correctamente.");
		} catch (ReproductorException e) {
			System.err.println(e.getMessage());
			session.getTransaction().rollback();
		} finally {
			session.close();
		}

	}

	/**
	 * Muestra las canciones de un artista Hay que añadir la opcion de ordenar
	 * alfabéticamente, por fecha
	 * 
	 */
	private static void mostrarCancionesArtista() {
		Session session = HibernateUtil.getSessionFactory().openSession();

		try {
			Artista artista = buscarArtista(session);
			if (artista.getCanciones().size() == 0) // En caso de que dicho artista no tenga canciones asignada
				throw new ReproductorException("El artista " + artista.getNombre() + " aun no tiene canciones.");

			System.out.println("Canciones de " + artista.getNombre());
			artista.getCanciones().stream().forEach(c -> System.out.println("  - " + c.getNombre()));
		} catch (ReproductorException e) {
			System.err.println(e.getMessage());
		} finally {
			session.close();
		}

	}

//Cancion

	/**
	 * Inserta una nueva canción en la base de datos
	 * 
	 */
	private static void altaCancion() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		CancionDAO dao = new CancionDAO(session);

		try {
			session.beginTransaction();
			dao.guardar(crearObjetoCancion(session));
			session.getTransaction().commit();
			System.out.println("Se ha guardado la canción correctamente");
		} catch (ReproductorException e) {
			System.err.println(e.getMessage());
			session.getTransaction().rollback();
		} finally {
			session.close();
		}

	}

	/**
	 * Solicita una canción y la borra de la base de datos
	 * 
	 */
	private static void bajaCancion() {
		Session session = HibernateUtil.getSessionFactory().openSession();

		try {
			Cancion cancion = buscarCancion(session);
			session.beginTransaction();

			if (Util.solicitarSN("Seguro que deseas borrar la canción " + cancion.getNombre() + "? (S/N)"))
				borrarCancion(session, cancion);

			session.getTransaction().commit();
			System.out.println("Se ha borrado correctamente la canción");
		} catch (ReproductorException e) {
			System.err.println(e.getMessage());
			session.getTransaction().rollback();
		} finally {
			session.close();
		}

	}

	/**
	 * Muestra todas las canciones de la base de datos
	 */
	private static void consultarTodasCanciones() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		CancionDAO dao = new CancionDAO(session);
		List<Cancion> lista = dao.obtenerTodasCanciones();

		if (lista.isEmpty()) {
			System.out.println("No existen canciones en la base de datos.");
		} else {
			System.out.println("ID\tCanción");
			lista.stream().forEach(System.out::println);
		}

		session.close();
	}

	/**
	 * Muestra los 3 generos mas escuchados de todas las canciones de la base de
	 * datos
	 */
	private static void consultarGenerosMasEscuchados() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		CancionDAO dao = new CancionDAO(session);
		List<Object[]> lista = dao.obtenerGeneroMasEscuchado();
		int pos = 1;

		if (lista.isEmpty()) {
			System.out.println("No hay canciones todavía.");
		} else {
			System.out.println("Géneros más escuchados en la librería:");
			for (Object[] o : lista) {
				if ((long) o[0] == 1) // Controla el singular y plural
					System.out.println(pos + ". " + o[1].toString() + " con una canción");
				else
					System.out.println(pos + ". " + o[1].toString() + " con " + o[0] + " canciones");
				pos++;
			}
		}
		session.close();
	}

	/**
	 * Crea un objeto Cancion solicitando los datos por consola
	 * 
	 * @param session
	 * 
	 * @return canción
	 * @throws ReproductorException
	 */
	private static Cancion crearObjetoCancion(Session session) throws ReproductorException {
		String nombre = Util.solicitarCadena("Introduce el nombre de la cancion: ");
		Set<Artista> artistas = solicitarArtistasCancion(session);
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
	 * Por cada artista tiene que borrar dicha canción de su lista ya que es una
	 * relación N:M y si no daría fallo por las constricciones
	 * 
	 * @param session
	 * @param cancion
	 * @throws ReproductorException
	 */
	private static void borrarCancion(Session session, Cancion cancion) throws ReproductorException {
		CancionDAO daoCancion = new CancionDAO(session);
		ArtistaDAO daoArtista = new ArtistaDAO(session);
		for (Artista artista : cancion.getArtistas()) { // A cada artista de la canción le borra la canción
			artista.borrarCancion(cancion);
			daoArtista.actualizar(artista); // Tiene que actualizarlo en la base de datos
		}
		cancion.getArtistas().clear(); // Vacía su lista de artistas
		daoCancion.borrar(cancion);
	}

	/**
	 * Solicita una lista de artista para la creación de canciones
	 * 
	 * @param session
	 * 
	 * @return Set<Artista>
	 */
	private static Set<Artista> solicitarArtistasCancion(Session session) {
		Set<Artista> lista = new HashSet<Artista>();
		List<Artista> listaObtenidos;
		String nombre;
		ArtistaDAO daoArtista = new ArtistaDAO(session);
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

					} else if (Util.solicitarSN(nombre + " no se encontró pero " + listaObtenidos.get(0).getNombre()
							+ " sí, ¿es el que estabas buscando? (S/N)")) {
						// Si tiene alguna diferencia pregunta si es ese para asegurarse ya que obtiene
						// la lista mediante una expresión regular
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

					} else if (Util.solicitarSN("¿Deseas crearlo? (S/N)")) {
						// En caso de que el nombre introducido no esté en la lista pregunta si se desea
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
	 * Crea un album en la base de datos
	 */
	private static void altaAlbum() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		AlbumDAO daoAlbum = new AlbumDAO(session);
		ArtistaDAO daoArtista = new ArtistaDAO(session);
		try {
			session.beginTransaction();
			Album album = crearObjetoAlbum(session);
			// Añade la lista canciones al álbum
			album.setCanciones(crearListaCancionesDeArtista(album.getArtista(), album.getPublicacion(), session));
			daoArtista.actualizar(album.getArtista());
			daoAlbum.guardar(album);
			session.getTransaction().commit();
			System.out.println("Se ha creado el album " + album.getNombre() + " de " + album.getArtista().getNombre()
					+ " correctamente");
		} catch (ReproductorException e) {
			System.err.println(e.getMessage());
			session.getTransaction().rollback();
		} finally {
			session.close();
		}

	}

	/**
	 * Borra un album de la base de datos
	 * 
	 * @throws ReproductorException
	 */
	private static void bajaAlbum() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		AlbumDAO dao = new AlbumDAO(session);
		try {
			Album album = buscarAlbum(session);
			// Pregunta para asegurarse si borrar el álbum
			if (Util.solicitarSN("Seguro que deseas borrar el album? Se borraran también las canciones (S/N)")) {
				// Borra primero todas las canciones de la base de datos
				for (Cancion c : album.getCanciones()) {
					borrarCancion(session, c);
				}
				dao.borrar(album);
			}

			session.getTransaction().commit();
			System.out.println("Se ha borrado el album correctamente");
		} catch (ReproductorException e) {
			System.err.println(e.getMessage());
			session.getTransaction().rollback();
		} finally {
			session.close();
		}

	}

	/**
	 * Crea una lista de canciondes de un artista indicado por parámetro
	 * 
	 * @param Artista   de las canciones
	 * @param LocalDate fecha de publicación de la canción (misma del álbum)
	 * @param session
	 * @return List<Cancion> lista de canciones creadas
	 * @throws ReproductorException
	 */
	private static List<Cancion> crearListaCancionesDeArtista(Artista artista, LocalDate publicación, Session session)
			throws ReproductorException {

		List<Cancion> listaCanciones = new ArrayList<Cancion>();
		CancionDAO daoCancion = new CancionDAO(session);
		Cancion cancion = null;
		int id;

		// Esta lista se crea para mostrar la lista de canciones que tiene el artista
		// sin álbun
		// asignado para que pueda asignarlo también
		List<Cancion> listaCancionesArtista = daoCancion.obtenerCancionesDeUnArtistaSinAlbum(artista);

		do {
			if (listaCancionesArtista.isEmpty()) { // Si la lista del artista está vacía crea un objeto canción
													// directamente
				cancion = crearObjetoCancionParaAlbum(session, artista);
			} else {
				// Muestra la lista de canciones sin álbun del artista
				listaCancionesArtista.stream().forEach(c -> System.out.println(c.getId() + ". " + c.getNombre()));

				if (Util.solicitarSN("¿Añadir de la lista(S/N)")) {
					id = Util.solicitarEntero("Introduce el id de la canción: ");

					if (cancionEstaEnLista(id, listaCancionesArtista)) { // Comprueba si el id introducido está en la
																			// lista
						// Esta comprobación se realiza para que en caso de que introduzca un id y haya
						// otra canción
						// con ese id no lo introduzca en el álbum
						cancion = daoCancion.getCancion(id);
						listaCancionesArtista.remove(cancion);
					} else {
						System.out.println("Error. Has introducido un id incorrecto.");
					}
				} else { // En caso de que no quiera añadir ninguno de la lista crea una nueva canción
					cancion = crearObjetoCancionParaAlbum(session, artista);
				}
			}

			listaCanciones.add(cancion); // Añade la canción escogida a la lista
			cancion.setPublicacion(publicación); // Le añade la publicación del album
			daoCancion.guardar(cancion); // Guarda la canción en la base de datos
			session.evict(cancion); // Se olvida de la canción

		} while (Util.solicitarSN("¿Añadir más canciones? (S/N)")); // Se repite mientras el usuario decida añadir más
																	// canciones

		return listaCanciones;
	}

	/**
	 * Crea un objeto tipo álbum
	 * 
	 * @param session
	 * @return Album
	 * @throws ReproductorException en caso de que no encuentre el artista
	 */
	private static Album crearObjetoAlbum(Session session) throws ReproductorException {
		String nombre = Util.solicitarCadena("Introduce el nombre del album: ");
		Artista artista = buscarArtista(session);
		LocalDate publicacion = Util.solicitarFecha("Introduce la fecha de publicación (DD/MM/AAAA)");
		Album album = new Album(nombre, artista, publicacion);
		return album;
	}

	/**
	 * Muestra todos los datos de un album que solicita por consola
	 * 
	 * @throws ReproductorException
	 */
	private static void consultarAlbum() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Album album;
		try {
			album = buscarAlbum(session);
			System.out.println(album.toString());
		} catch (ReproductorException e) {
			System.out.println(e.getMessage());
		} finally {
			session.close();
		}

	}

	/**
	 * Muestra por consola todos los albunes de la base de datos
	 */
	private static void mostrarAlbumes() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		AlbumDAO dao = new AlbumDAO(session);
		dao.obtenerTodosAlbunes().stream().forEach(System.out::println);
		session.close();
	}

	/**
	 * Crea un objeto canción para añadir a un album. La diferencia con el anterior
	 * es que no solicita ni album ni fecha de publicación.
	 * 
	 * @param Artista
	 * @return Canción
	 * @throws ReproductorException
	 */
	private static Cancion crearObjetoCancionParaAlbum(Session session, Artista artista) throws ReproductorException {
		CancionDAO daoCancion = new CancionDAO(session);
		String nombre = Util.solicitarCadena("Introduce el nombre de la cancion");
		Set<Artista> listaArtistas = new HashSet<Artista>();
		listaArtistas.add(artista);
		LocalTime duracion = Util.solicitarHora("Introduce la duración de la canción (MM:SS): ");
		Genero genero = Util.solicitarGenero();
		Cancion cancion = new Cancion(nombre, listaArtistas, duracion, genero);
		listaArtistas.forEach(a -> a.getCanciones().add(cancion)); // Añade la canción a la lista de canciones del
																	// artista
		artista.addCancion(cancion);
		daoCancion.guardar(cancion);
		return cancion;
	}

// Playlist

	/**
	 * Da de alta una playlist en la base de datos
	 * 
	 */
	private static void altaPlaylist() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		PlaylistDAO dao = new PlaylistDAO(session);
		String nombre = Util.solicitarCadena("Introduce el nombre de la playlist: ");
		Playlist playlist = new Playlist(nombre,
				Util.solicitarCadena("Introduce la descripción para la playlist " + nombre));
		session.beginTransaction();
		try {
			dao.guardar(playlist);
			System.out.println("Se ha creado correctamente la playlist " + nombre);
			session.getTransaction().commit();
		} catch (ReproductorException e) {
			System.err.println(e.getMessage());
			session.getTransaction().rollback();
		} finally {
			session.close();
		}

	}

	/**
	 * Elimina una playlist en la base de datos
	 * 
	 */
	private static void bajaPlaylist() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		PlaylistDAO dao = new PlaylistDAO(session);
		session.beginTransaction();
		try {
			Playlist playlist = buscarPlaylist(session);
			if (Util.solicitarSN("Seguro que deseas borrar la playlist " + playlist.getNombre() + "? (S/N)"))
				dao.borrar(playlist);
			session.getTransaction().commit();
			System.out.println("Se ha borrado la playlist correctamente");
		} catch (ReproductorException e) {
			System.err.println(e.getMessage());
			session.getTransaction().rollback();
		} finally {
			session.close();
		}

	}

	/**
	 * Cambia el nombre a una playlist
	 * 
	 */
	private static void cambiarNombrePlaylist() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		PlaylistDAO dao = new PlaylistDAO(session);
		session.beginTransaction();
		try {
			Playlist playlist = buscarPlaylist(session);
			
			playlist.cambiarNombre(Util.solicitarCadena("Introduce el nuevo nombre para la playlist: "));
			dao.actualizar(playlist);
			session.getTransaction().commit();
			System.out.println("Se ha cambiado el nombre de la playlist correctamente");
		} catch (ReproductorException e) {
			System.err.println(e.getMessage());
			session.getTransaction().rollback();
		} finally {
			session.close();
		}

	}

	/**
	 * Cambia la descripcion a una playlist
	 * 
	 */
	private static void cambiarDescripcionPlaylist() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		PlaylistDAO dao = new PlaylistDAO(session);
		try {
			Playlist playlist = buscarPlaylist(session);
			session.beginTransaction();
			playlist.cambiarDescripcion(Util.solicitarCadena("Introduce la nueva descripción para la playlist: "));
			dao.actualizar(playlist);
			session.getTransaction().commit();
			System.out.println("Se ha cambiado la descripción de la playlist correctamente.");
		} catch (ReproductorException e) {
			System.err.println(e.getMessage());
			session.getTransaction().rollback();
		} finally {
			session.close();
		}

	}

	/**
	 * Añade una canción a una playlist
	 */
	private static void añadirCancionPlaylist() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		PlaylistDAO daoPlaylist = new PlaylistDAO(session);
		try {
			session.beginTransaction();
			Playlist playlist = buscarPlaylist(session);
			Cancion c = buscarCancion(session);
			playlist.addCancion(c); // Añade la cancion al objeto playlist
			// Actualiza la playlist en la base de datos
			daoPlaylist.actualizar(playlist);
			session.getTransaction().commit();
		} catch (ReproductorException e) {
			System.err.println(e.getMessage());
			session.getTransaction().rollback();
		} finally {
			session.close();
		}

	}

	/**
	 * Elimina una canción de una playlist
	 */
	private static void eliminarCancionPlaylist() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		PlaylistDAO daoPlaylist = new PlaylistDAO(session);
		CancionDAO daoCancion = new CancionDAO(session);
		try {
			session.beginTransaction();
			Playlist playlist = buscarPlaylist(session);

			// Muestra todas las canciones de la playlist
			playlist.getCanciones().stream().forEach(c -> System.out.println(c.getId() + "\t" + c.getNombre()));
			// Obtiene la canción por ID
			Cancion cancion = daoCancion
					.getCancion(Util.solicitarEntero("Introduce el id de la canción que deseas eliminar: "));
			if (cancion != null) {
				if (cancionEstaEnLista(cancion.getId(), playlist.getCanciones())) {	//Comprueba que el id que se introduce esté en la lista
					playlist.eliminarCancion(cancion);
					daoPlaylist.actualizar(playlist);
					session.getTransaction().commit();
					System.out.println(
							"Eliminado correctamente la canción " + cancion.getNombre() + " de " + playlist.getNombre());
				} else {
					System.out.println("El id introducido no se encontraba en la lista de canciones");
				}
			} else {
				System.out.println("El id introducido no se encontraba en la lista de canciones");
			}


		} catch (ReproductorException e) {
			System.err.println(e.getMessage());
			session.getTransaction().rollback();
		} finally {
			session.close();
		}

	}

	/**
	 * Muestra por consola el nombre, descripción y duración de todas las playlists
	 */
	private static void mostrarTodasPlaylist() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		PlaylistDAO dao = new PlaylistDAO(session);
		List<Playlist> lista = dao.obtenerTodasPlaylists();
		
		if (lista == null || lista.isEmpty())
			System.out.println("Aún no existen playlists.");
		else
			lista.stream().forEach(System.out::println);
		
		session.close();
	}

	/**
	 * Muestra todos los datos de una playlist que se solicita por consola
	 * 
	 */
	private static void consultarPlaylist() {
		Session session = HibernateUtil.getSessionFactory().openSession();

		try {
			Playlist playlist = buscarPlaylist(session);
			// Nombre playlist y descripcion
			System.out.println(playlist.getNombre() + "\n" + playlist.getDescripcion());
			// Numero de canciones y la duración completa de la playlist
			System.out.println(
					"Total canciones: " + playlist.getCanciones().size() + "\tDuración: " + playlist.getDuracion());
			// Todas las canciones de la playlist
			playlist.getCanciones().stream().forEach(c -> System.out.println(" " + c.getNombreConArtistas()));
		} catch (ReproductorException e) {
			System.err.println(e.getMessage());
		} finally {
			session.close();
		}
		
	}

	/**
	 * Muestra el genero más escuchado de una playlist que se solicita por consola
	 * 
	 */
	private static void consultarGeneroMasEscuchadoDePlaylist() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		PlaylistDAO dao = new PlaylistDAO(session);
		try {
			Playlist playlist = buscarPlaylist(session);
			if (playlist.getCanciones().size() == 0)
				throw new ReproductorException("La playlist no tiene canciones.");

			Object[] lista = dao.obtenerGeneroMasEscuchado(playlist);

			System.out
					.println("El género más escuchado es " + lista[1] + " con un total de " + lista[0] + " canciones.");
		} catch (ReproductorException e) {
			System.err.println(e.getMessage());
		} finally {
			session.close();
		}

		
	}

//Búsquedas

	/**
	 * Busca un album en la base de datos introduciendo su nombre mediante
	 * expresiones regulares
	 * 
	 * @return Album
	 * @throws ReproductorException
	 */
	private static Album buscarAlbum(Session session) throws ReproductorException {
		AlbumDAO dao = new AlbumDAO(session);
		Album album = null;
		String nombre;
		List<Album> listaAlbumnes;
		int id;
		do {
			nombre = Util.solicitarCadena("Introduce el nombre del album: ");
			listaAlbumnes = dao.obtenerListaAlbumPorNombre(nombre);

			if (nombre.isEmpty())	
				throw new ReproductorException("Error, has dejado el nombre del album en blanco");

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
	private static Playlist buscarPlaylist(Session session) throws ReproductorException {
		PlaylistDAO dao = new PlaylistDAO(session);
		Playlist playlist = null;
		String nombre;
		List<Playlist> listaPlaylists;
		int id;

		do {
			nombre = Util.solicitarCadena("Introduce el nombre de la playlist:");
			listaPlaylists = dao.buscarPlaylist(nombre);

			if (nombre.isEmpty())
				throw new ReproductorException("Error, has dejado el nombre de la playlist en blanco");

			if (listaPlaylists.size() == 0)
				throw new ReproductorException("No se ha encontrado coincidencias de playlist con ese nombre.");

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
	private static Artista buscarArtista(Session session) throws ReproductorException {
		ArtistaDAO dao = new ArtistaDAO(session);
		Artista artista = null;
		String nombre;
		List<Artista> listaArtistas;
		int id;

		do {
			nombre = Util.solicitarCadena("Introduce el nombre del artista: ");
			listaArtistas = dao.obtenerListaArtistasPorNombre(nombre);

			if (nombre.isEmpty())
				throw new ReproductorException("Error, has dejado el nombre del artista en blanco");

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
	private static Cancion buscarCancion(Session session) throws ReproductorException {
		CancionDAO dao = new CancionDAO(session);
		Cancion cancion = null;
		String nombre;
		List<Cancion> listaCanciones;
		int id;

		do {
			nombre = Util.solicitarCadena("Introduce el nombre de la canción: ");
			listaCanciones = dao.obtenerCancionPorNombre(nombre); // Obtiene todas las coinciendias

			if (nombre.isEmpty())
				throw new ReproductorException("Error, has dejado el nombre del artista en blanco");

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

	private static void tratarMenuCancion(int opc) {
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

	private static void tratarMenuArtista(int opc) {
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
			mostrarAlbumesArtista();
			break;
		case 5:
			mostrarCancionesArtista();
			break;
		case 6:
			cambiarNombreArtista();
			break;
		}
	}

	private static void tratarMenuAlbum(int opc) {
		switch (opc) {
		case 1:
			altaAlbum();
			break;
		case 2:
			bajaAlbum();
			break;
		case 3:
			consultarAlbum();
			break;
		case 4:
			mostrarAlbumes();
			break;
		}
	}

	private static void tratarMenuPlaylist(int opc) {
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
			añadirCancionPlaylist();
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

	private static void tratarMenuPrincipal(int opc) {
		int eleccion = 0;
		switch (opc) {
		case 1:
			do {
				eleccion = Menus.menuCancion();
				tratarMenuCancion(eleccion);
			} while (eleccion != 5);
			break;
		case 2:
			do {
				eleccion = Menus.menuArtistas();
				tratarMenuArtista(eleccion);
			} while (eleccion != 7);
			break;
		case 3:
			do {
				eleccion = Menus.menuAlbum();
				tratarMenuAlbum(eleccion);
			} while (eleccion != 5);
			break;
		case 4:
			do {
				eleccion = Menus.menuPlaylist();
				tratarMenuPlaylist(eleccion);
			} while (eleccion != 10);
			break;
		}
	}

}
