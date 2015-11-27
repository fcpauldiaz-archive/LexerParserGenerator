/**
 * Nombre del archivo: TestLR1Parser.java
 * Universidad del Valle de Guatemala
 * Pablo Diaz 13203 
 * Descripción: Tercer proyecto. Generador de ParserMain
**/

import java.util.Scanner;
import javax.swing.JOptionPane;

public class TestLR1ParserMain {

/**
* @param args the command line arguments
 */
	public static void main(String[] args) {
		// TODO code application logic here
		String input;
		Scanner keyboard = new Scanner(System.in);
		System.out.println("Ingrese text a parsear");
		input = JOptionPane.showInputDialog("Ingrese texto a parsear: ");
		TestLR1Parser objParser = new TestLR1Parser(input);
		objParser.procesoParseo(input);
	}
}
