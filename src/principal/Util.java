package principal;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
		boolean correcto;
		do {
			try {
				System.out.println(msg);
				entero = Integer.parseInt(teclado.nextLine());
				correcto = true;
			} catch (NumberFormatException e) {
				System.out.println("Ha introducido un dato incorrecto. Vuelve a introducirlo");
				correcto = false;
			}
		} while (!correcto);
		return entero;
	}

	/**
	 * Solicita un numero entero entre 2 numeros
	 * 
	 * @param msg Texto que se muestra solicitando el dato
	 * @param min Número mínimo del rango
	 * @param max Número máximo del rango
	 * @return El número entero solicitado
	 */
	public static int solicitarEnteroEnRango(String msg, int min, int max) {
		int entero = 0;
		boolean correcto;
		do {
			try {
				System.out.println(msg);
				entero = Integer.parseInt(teclado.nextLine());
				correcto = (entero >= min && entero <= max) ? true : false;
			} catch (NumberFormatException e) {
				System.out.println("Ha introducido un dato incorrecto. Vuelve a introducirlo");
				correcto = false;
			}
		} while (!correcto);
		return entero;
	}

	/**
	 * Solicita un caracter S/N
	 * 
	 * @param Texto que se muestra solicitando el dato
	 * @return el caracter leido
	 */
	public static boolean solicitarSN(String msg) {
		boolean correcto, retorno;
		char c;
		do {
			correcto = true;
			System.out.println(msg);
			c = teclado.nextLine().toUpperCase().charAt(0);
			if (c != 'S' && c != 'N') { // Comprueba que se introduzca S o N
				correcto = false;
				System.out.println("Debes introducir 'S' o 'N' exclusivamente.");
			}
		} while (!correcto);
		
		retorno = (c == 'S') ? true : false;
		return retorno;
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
		} while (nombre == null);

		return nombre;
	}

	/**
	 * Solicita un objeto LocalTime usando el formato hh:mm
	 * 
	 * @param Texto que solicita objeto
	 * @return LocalTime
	 */
	public static LocalTime solicitarHora(String msg) {
		LocalTime duracion = null;
		boolean correcto;
		String strDuracion;

		do {
			try {
				strDuracion = solicitarCadena(msg);
				duracion = LocalTime.parse(strDuracion);
				correcto = true;
			} catch (DateTimeParseException e) {
				System.out.println("Error. Debes introducir la hora en formato MM:SS. Vuelve a intentarlo.");
				correcto = false;
			}
		} while (!correcto);
		return duracion;
	}

	/**
	 * Solicita una fecha en formato dd/MM/yyyy
	 * @param Texto que solicita el objeto
	 * @return LocalDate
	 */
	public static LocalDate solicitarFecha(String msg) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		LocalDate publicacion = null;
		boolean correcto;
		String strFecha;

		do {
			try {
				strFecha = solicitarCadena(msg);
				publicacion = LocalDate.parse(strFecha, formatter);
				correcto = true;
			} catch (DateTimeParseException e) {
				System.out.println("Error. Debes introducir la fecha en formato DD/MM/AAAA. Vuelve a intentarlo");
				correcto = false;
			}
		} while (!correcto);
		return publicacion;
	}

}
