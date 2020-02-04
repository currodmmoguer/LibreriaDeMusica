package principal;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

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
		
		//prueba();
		int opc = 0;
		do {
			try {
				opc = menuPrincipal();
				tratarMenuPrincipal(opc);
			} catch (ReproductorException e) {
				System.out.println(e.getMessage());
			}
		} while (opc != 5);

		// HibernateUtil.closeSessionFactory();

	}





	private static void prueba() {

		
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
					if (opcion == 'n') {
						daoArtista.guardar(new Artista(nombre));
					}

				}
			}

		} while (!nombre.equals(""));

		return lista;
	}
	
	private static void nuevoAlbum() {
		ArtistaDAO daoArtista = new ArtistaDAO();
		String nombre = solicitarCadena("Introduce el nombre del album: ");
		Artista artista = daoArtista.obtenerArtistaPorNombre(solicitarCadena("Introduce el nombre del artista: "));
		// Comprobar si no existe
		List<Cancion> canciones = solicitarCanciones(artista);

	}

	private static List<Cancion> solicitarCanciones(Artista artista) {
		ArrayList<Cancion> canciones = new ArrayList<Cancion>();
		String nombreCancion;
		int pos = 1;
		for (Cancion c : artista.getCanciones()) {
			System.out.println(pos + ". " + c.getNombre());
		}
		
		//Hay que preguntar que introduza el numero de cada cancion
		return null;
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
		for(Playlist p: playlists) {
			System.out.println(p.getId() + "\t" + p.getNombre());
		}
		
	}


	/**
	 * Muesta las canciones de un artista
	 * Hay que añadir la opcion de ordenar alfabéticamente, por fecha
	 * Posibiliad de mostrar si se parecen o no el nombre del artista (regex)
	 */
	private static void mostrarCancionesArtista() {
		ArtistaDAO daoArtista = new ArtistaDAO();
		Artista artista = daoArtista.obtenerArtistaPorNombre(solicitarCadena("Introduce el nombre del artista: "));
		System.out.println(artista);
		for (Cancion c : artista.getCanciones()) {
			System.out.println(c);
		}
		
	}


	private static void mostrarTodosArtista() {
		ArtistaDAO dao = new ArtistaDAO();
		List<Artista> artistas = dao.consultarArtistas();
		System.out.println("ID\tNombre");
		for(Artista a: artistas) {
			System.out.println(a.getId() + "\t" + a.getNombre());
		}
		
	}
	
	private static void bajaCancion() throws ReproductorException {
		CancionDAO dao = new CancionDAO();
		String nombre = solicitarCadena("Introduce el nombre de la cancion");
		char opc;
		List<Cancion> lista = dao.obtenerCancionPorNombre(nombre);
		Cancion cancion;
		if (lista.size() == 0 || lista == null)	//No encuentra la cancion
			throw new ReproductorException("No existe ninguna canción con ese nombre.");
		
		if (lista.size() == 1) {	//Solo hay una coincidencia
			opc = solicitarSN("Seguro que quieres borrar la cancion " + nombre + "? (S/N)");
			
			if (opc == 'S')
				dao.borrar(lista.get(0));
		
		} else {	//Mas de 1 coincidencia
			for (Cancion c : lista) {	//Muestra todas las canciones
				System.out.println(c.getId() + "\t" + c.getNombre());
			}
			System.out.println("Introduce el id de la canción que deseas eliminar: ");
			cancion = dao.obtenerCancionPorId(Integer.parseInt(teclado.nextLine()));
			
			opc = solicitarSN("Seguro que deseas borrar la canción " + cancion.getNombre() + "? (S/N)");
			
			if (opc == 'S') {
				dao.borrar(cancion);
			}
		}
		
		
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
			for (Playlist p: lista) {
				System.out.println(p.getId() + "\t" + p.getNombre());
			}
			System.out.println("Introduce el id de la playlist que deseas eliminar: ");
			playlist = dao.getPlaylist(Integer.parseInt(teclado.nextLine()));
			
			opc = solicitarSN("Seguro que deseas borrar la playlist " + playlist.getNombre() + "? (S/N)");
			if (opc == 'S')
				dao.borrar(playlist);
			
		}
		
	}





	private static void tratarMenuPlaylist(int opc) {
		switch (opc) {
		case 1:
			break;
		case 2:
			break;
		case 3:
			break;
		case 4:
			break;
		}
	}



	private static void tratarMenuActualizaciones(int opc) {
		int eleccion;
		switch (opc) {
		case 1:
			break;
		case 2:
			eleccion = menuPlaylist();
			tratarMenuPlaylist(eleccion);
			break;
		}
	}










	
	private static int menuActualizaciones() {
		int opc;
		System.out.println("1. Cambiar nombre a artista");
		System.out.println("2. Editar playlist");
		System.out.println("3. Volver");

		do {
			System.out.print("Inroduce opcion: ");
			opc = Integer.parseInt(teclado.nextLine());
		} while (opc < 1 || opc > 3);
		return opc;
	}
	
	private static int menuPlaylist() {
		int opc;

		System.out.println("1. Cambiar nombre");
		System.out.println("2. Cambiar descripcion");
		System.out.println("3. Añadir canciones");
		System.out.println("4. Eliminar canciones");
		System.out.println("5. Volver");

		do {
			System.out.print("Inroduce opcion: ");
			opc = Integer.parseInt(teclado.nextLine());
		} while (opc < 1 || opc > 5);

		return opc;
	}
	

	
	private static int menuBajas() {
		int opc;

		System.out.println("1. Eliminar cancion");
		System.out.println("2. Eliminar artista");
		System.out.println("3. Eliminar album");
		System.out.println("4. Eliminar playlist");
		System.out.println("5. Volver");
		do {
			System.out.print("Inroduce opcion: ");
			opc = Integer.parseInt(teclado.nextLine());
		} while (opc < 1 || opc > 5);
		return opc;
	}
	
	private static void tratarMenuBajas(int opc) throws ReproductorException {
		switch (opc) {
		case 1:
			bajaCancion();
			break;
		case 2:
			break;
		case 3:
			break;
		case 4:
			bajaPlaylist();
			break;
		}
	}













	private static int menuAltas() {
		int opc;
		System.out.println("1. Nuevo artista");
		System.out.println("2. Nueva canción");
		System.out.println("3. Nuevo album");
		System.out.println("4. Nueva playlis");
		System.out.println("5. Volver");
		do {
			System.out.print("Inroduce opcion: ");
			opc = Integer.parseInt(teclado.nextLine());
		} while (opc < 1 || opc > 5);
		return opc;
	}
	
	private static void tratarMenuAltas(int opc) throws ReproductorException {
		switch (opc) {
		case 1:
			nuevoArtista();
			break;
		case 2:
			nuevaCancion(); // No guarda artista
			break;
		case 3:
			nuevoAlbum();
			break;
		case 4:
			nuevaPlaylist();
			break;
		}
	}
	
	private static int menuConsultas() {
		int opc;

		System.out.println("1. Consultar todos los artistas");
		System.out.println("2. Consultar todas las playlist");
		System.out.println("3. Consultar los albunes de un artista");
		System.out.println("4. Consultar todas las canciones de un artista"); // Ordenado Fecha, Alfabetico
		System.out.println("5. Consultar album"); // Por nombre o id
		System.out.println("6. Consultar playlist"); // Por nombre o id
		System.out.println("7. Consultar playlist que contenga una cancion");
		System.out.println("8. Consultar cual es el genero más escuchado de una playlist");
		System.out.println("X. Volver");

		do {
			System.out.print("Inroduce opcion: ");
			opc = Integer.parseInt(teclado.nextLine());
		} while (opc < 1 || opc > 9);
		return opc;
	}
	
	private static void tratarMenuConsultas(int opc) {
		switch (opc) {
		case 1:
			mostrarTodosArtista();
			break;
		case 2:
			mostrarTodasPlaylist();
			break;
		case 3:
			break;
		case 4:
			mostrarCancionesArtista();
			break;
		case 5:
			break;
		case 6:
			break;
		case 7:
			break;
		case 8:
			break;
		}
	}





	private static int menuPrincipal() {
		int opc;

		System.out.println("\nGESTIÓN DE BASE DE DATOS DE REPRODUCTOR MULTIMEDIA");
		System.out.println("1. Consultas");
		System.out.println("2. Altas");
		System.out.println("3. Bajas");
		System.out.println("4. Actualizaciones");
		System.out.println("5. Salir");

		do {
			System.out.print("Inroduce opcion: ");
			opc = Integer.parseInt(teclado.nextLine());
		} while (opc < 1 || opc > 5);

		return opc;
	}


	private static void tratarMenuPrincipal(int opc) throws ReproductorException {
		int eleccion;
		switch (opc) {
		case 1:
			eleccion = menuConsultas();
			tratarMenuConsultas(eleccion);
			break;
		case 2:
			eleccion = menuAltas();
			tratarMenuAltas(eleccion);
			break;
		case 3:
			eleccion = menuBajas();
			tratarMenuBajas(eleccion);
			break;
		case 4:
			eleccion = menuActualizaciones();
			tratarMenuActualizaciones(eleccion);
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
