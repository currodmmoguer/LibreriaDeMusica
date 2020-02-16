package modelo;

public enum Genero {
	LATINO,
	POP,
	DANCE,
	FLAMENCO,
	HIPHOP,
	ROCK,
	JAZZ,
	REGGAE,
	SOUL,
	CLASICA,
	METAL,
	BLUES,
	OTRO;
	
	/**
	 * Obtiene un elemento del enumerado por su posición
	 * @param int posición
	 * @return Genero
	 */
	public static Genero getGenero(int pos) {
		Genero[] generos = Genero.values();
		return generos[pos];
	}
	
	
}
