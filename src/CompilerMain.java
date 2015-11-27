/**
* Universidad del Valle de Guatemala
* Pablo Diaz 13203
* http://www.regexplanet.com/advanced/java/index.html
* https://regex101.com/r/vW2pQ7/30
* http://stackoverflow.com/questions/632475/regex-to-pick-commas-outside-of-quotes
*/



import java.io.File;
import java.util.HashMap;
import java.util.Scanner;



/**
 *
 * @author Pablo
 */
public class CompilerMain {

    public static String EPSILON = "ε";
    public static char EPSILON_CHAR = EPSILON.charAt(0);
    public static Errors errores = new Errors();
    /**
     * @param args the command line arguments
     * 
     */
    public static void main(String[] args) {
        // TODO code application logic here
         // TODO code application logic here
        ReadFile read = new ReadFile();
        File file = new File("MiniTiny (SLR)"+".txt");
        
          HashMap cocol = read.leerArchivo(file);
        
       
        LexerSyntax lexer = new LexerSyntax(cocol);
        lexer.vocabulario();
        lexer.construct(cocol);
        
       
        
        if (lexer.getOutput()){
            System.out.println("");
            System.out.println("Generando Analizador Léxico....");
            LexerGenerator generator = new LexerGenerator(cocol);
            generator.encontrarNombre();
            generator.generarCharactersYKeywords();
            generator.generarTokens();
            generator.generarClaseAnalizadora();
            generator.generarMain();
            generator.generarClaseToken();
            System.out.println("");
            System.out.println("Ejecute el Main de la carpeta generador para probar el input");
        
            System.out.println("Cantidad Errores: " + errores.getCount());



            ParserSLRGenerator SLR = new ParserSLRGenerator(lexer.getProducciones(),lexer,generator.getNombreArchivo());
            ParserCanonicalLR LR = new ParserCanonicalLR(lexer.getProducciones(),lexer,generator.getNombreArchivo());
            System.out.println("Seleccione el tipo de parser");
            System.out.println("1. SLR");
            System.out.println("2. LR(1)");
            Scanner keyboard = new Scanner(System.in);
            System.out.println("Ingrese su opcion:");
            String teclado = keyboard.nextLine();
            if (teclado.equals("1")){
                SLR.constructLR();
                SLR.crearTablaParseo();
               // SLR.procesoParseo("id assign_op number");
                SLR.generarParser();
                SLR.serialize();
            }
            else if (teclado.equals("2")){
                LR.construirAutomata();
                LR.crearTablaParseo();
                LR.procesoParseo("a c d");
                LR.generarParser();
                LR.serialize();
            }
            else
                System.out.println("Opción inválida. Orale");
        }
       
    }

}
