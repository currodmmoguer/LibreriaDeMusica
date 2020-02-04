package modelo;

public enum Genero {
	LATINO,
	POP,
	DANCE,
	HIPHOP,
	ROCK,
	JAZZ,
	REGGAE,
	SOUL,
	CLASICA,
	METAL,
	BLUES;
	
	public static Genero getGenero(int pos) {
		Genero[] generos = Genero.values();
		
		return generos[pos];
	}
	
	public static Genero getGenero(String nombre) {
		Genero genero = null;
		int pos = 0;
		while (genero != null && pos < Genero.values().length) {
			if (Genero.values()[pos].toString().equalsIgnoreCase(nombre)) {
				genero = Genero.values()[pos];
			}
			pos++;
		}
		
		return genero;
			
	}
	
}
