
import java.util.HashMap;
import javax.swing.JFileChooser;
import java.io.File;
/**
*
* @author Pablo
*/
public class resultadoGeneradorMain {
public static String EPSILON = "ε";
public static char EPSILON_CHAR = EPSILON.charAt(0);
/**
* @param args the command line arguments
 */
public static void main(String[] args) {
	// TODO code application logic here
	ReadFile read = new ReadFile();
	File file = new File("input"+".txt");
	HashMap input = read.leerArchivo(file);
	MiniTiny resGenerator = new MiniTiny(input);
	resGenerator.automatas();
	resGenerator.revisar();
	}
}

