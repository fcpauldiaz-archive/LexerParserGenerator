/**
 * Nombre del archivo: TestLR1Parser.java
 * Universidad del Valle de Guatemala
 * Pablo Diaz 13203 
 * Descripción: Tercer proyecto. Generador de ParserMain
**/

import java.io.File;
import java.util.HashMap;

public class TestLR1ParserMain {

/**
* @param args the command line arguments
 */
	public static void main(String[] args) {
		// TODO code application logic here
ReadFile read = new ReadFile();
File file = new File("inputParser"+".txt");
HashMap input = read.leerArchivo(file);
		TestLR1Parser objParser = new TestLR1Parser(input);
objParser.revisarArchivo();
	}
}
