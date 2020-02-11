package principal;

import java.util.Scanner;

public class Menus {

	private static Scanner teclado = new Scanner(System.in);

	protected static int menuPrincipal() {
		int opc = 0;
		try {
			System.out.println("\nGESTIÓN DE BASE DE DATOS DE REPRODUCTOR MULTIMEDIA");
			System.out.println("1. Canciones");
			System.out.println("2. Artistas");
			System.out.println("3. Albunes");
			System.out.println("4. Playlists");
			System.out.println("5. Salir");

			do {
				System.out.print("Inroduce opcion: ");
				opc = Integer.parseInt(teclado.nextLine());
			} while (opc < 1 || opc > 5);
		} catch (NumberFormatException e) {
			System.out.println("Has introducido un dato incorrecto. Vuelve a intentarlo");
		}
		return opc;
	}

	protected static int menuArtistas() {
		int opc = 0;
		try {
			System.out.println("1. Alta artista");
			System.out.println("2. Borrar artista");
			System.out.println("3. Consultar todos los artista");
			System.out.println("4. Consultar los albunes de un artista");
			System.out.println("5. Consultar las canciones de un artista"); // Ordenado por fecha, alfabeticamente
			System.out.println("6. Cambiar nombre");
			System.out.println("7. Volver");

			do {

				System.out.print("Inroduce opcion: ");
				opc = Integer.parseInt(teclado.nextLine());

			} while (opc < 1 || opc > 7);
		} catch (NumberFormatException e) {
			System.out.println("Has introducido un dato incorrecto. Vuelve a intentarlo");
		}
		return opc;

	}

	protected static int menuCancion() {
		int opc = 0;
		try {
			System.out.println("1. Alta canción");
			System.out.println("2. Borrar canción");
			System.out.println("3. Consultar el género más escuchado");
			System.out.println("4. Volver");

			do {
				System.out.print("Inroduce opcion: ");
				opc = Integer.parseInt(teclado.nextLine());
			} while (opc < 1 || opc > 4);
		} catch (NumberFormatException e) {
			System.out.println("Has introducido un dato incorrecto. Vuelve a intentarlo");
		}
		return opc;

	}

	protected static int menuAlbum() {
		int opc = 0;
		try {
			System.out.println("1. Alta album");
			System.out.println("2. Borrar album");
			System.out.println("3. Consultar album");
			System.out.println("4. Volver");

			do {
				System.out.print("Inroduce opcion: ");
				opc = Integer.parseInt(teclado.nextLine());
			} while (opc < 1 || opc > 4);
		} catch (NumberFormatException e) {
			System.out.println("Has introducido un dato incorrecto. Vuelve a intentarlo");
		}
		return opc;

	}

	protected static int menuPlaylist() {
		int opc = 0;
		try {
			System.out.println("1. Alta playlist");
			System.out.println("2. Borrar playlist");
			System.out.println("3. Cambiar nombre");
			System.out.println("4. Cambiar descripcion");
			System.out.println("5. Añadir canciones");
			System.out.println("6. Eliminar cancion");
			System.out.println("7. Consultar todas las playlist");
			System.out.println("8. Consultar playlist");
			System.out.println("9. Consultar playlist que contengan una canción");
			System.out.println("10. Consultar cual es el género más escuchado de una playlist");
			System.out.println("11. Volver");

			do {
				System.out.print("Inroduce opcion: ");
				opc = Integer.parseInt(teclado.nextLine());
			} while (opc < 1 || opc > 11);
		} catch (NumberFormatException e) {
			System.out.println("Has introducido un dato incorrecto. Vuelve a intentarlo");
		}
		return opc;
	}



}
