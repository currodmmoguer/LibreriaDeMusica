package principal;

import java.util.Scanner;

public class Util {
	
	private static Scanner teclado = new Scanner(System.in);
	
	/**
	 * Solicita un numero entero
	 * 
	 * @param Texto que se muestra solicitando el dato
	 * @return el numero entero solicitado
	 */
	public static int solicitarEntero(String msg) {
		int entero = 0;
		try {
			System.out.println(msg);
			entero = Integer.parseInt(teclado.nextLine());
		} catch (NumberFormatException e) {
			System.out.println("Ha introducido un dato incorrecto. Vuelve a introducirlo");
		}
		return entero;
	}

	/**
	 * Solicita un caracter S/N
	 * 
	 * @param Texto que se muestra solicitando el dato
	 * @return el caracter leido
	 */
	public static char solicitarSN(String msg) {
		boolean correcto;
		char c;
		do {
			correcto = true;
			System.out.println(msg);
			c = teclado.nextLine().toUpperCase().charAt(0);
			if (c != 'S' || c != 'N') { // Comprueba que se introduzca S o N
				correcto = false;
				System.out.println("Debes introducir 'S' o 'N' exclusivamente.");
			}
		} while (!correcto);
		return c;
	}

	/**
	 * Solicita una cadena de texto
	 * 
	 * @param Texto que se muestra solicitando la cadena
	 * @return cadena de texto
	 */
	public static String solicitarCadena(String msg) {
		String nombre;
		do {
			System.out.println(msg);
			nombre = teclado.nextLine();
		} while (nombre.length() == 0 || nombre == null);

		return nombre;
	}

}
