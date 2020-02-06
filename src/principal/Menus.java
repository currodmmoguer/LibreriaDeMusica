package principal;

import java.util.Scanner;

public class Menus {
	
	private static Scanner teclado = new Scanner(System.in);

	protected static int menuPrincipal() {
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
	
	protected static int menuConsultas() {
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
	
	protected static int menuAltas() {
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
	
	protected static int menuBajas() {
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
	
	protected static int menuPlaylist() {
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
	
	protected static int menuActualizaciones() {
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
	
}
