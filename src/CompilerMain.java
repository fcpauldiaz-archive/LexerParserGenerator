/**
* Universidad del Valle de Guatemala
* Pablo Diaz 13203
* http://www.regexplanet.com/advanced/java/index.html
* https://regex101.com/r/vW2pQ7/30
* http://stackoverflow.com/questions/632475/regex-to-pick-commas-outside-of-quotes
*/



import java.io.File;
import java.util.HashMap;



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
        File file = new File("cocol"+".txt");
        
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
     
      
       
       /* ParserSLRGenerator g = new ParserSLRGenerator(lexer.getProducciones(),lexer,generator.getNombreArchivo());
        g.constructLR();
        g.crearTablaParseo();
        //g.procesoParseo("(()())()");
        g.generarParser();
        g.serialize();*/
        
        ParserCanonicalLR LR = new ParserCanonicalLR(lexer.getProducciones(),lexer);
        LR.construirAutomata();
        
        }
    }

}
