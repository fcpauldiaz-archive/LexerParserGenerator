/**
* Universidad Del Valle de Guatemala
* 02-oct-2015
* Pablo Díaz 13203
*/



/**
 * Clase para mostrar diferentes tipos de errores
 * @author Pablo
 */
public class Errors {
    
    private int count = 0;
   
    public Errors(){
        
    }
    /**
     * Ocurre cuando la expresión está mal ingresada, parentésis, etc.
     * @param line
     * @param msg 
     */
    public void SynErr(int line,  String msg){
        System.out.println("");
        System.out.println("Error Sintáctico " + "en la línea " +line);
        System.out.println(">>>   " + msg);
        System.out.println("");
        count++;
        
    }
    /**
     * Ocurre cuando se no reconoce un token
     * @param line
     * @param col
     * @param msg 
     */
    public void LexErr(int line, int col, String msg,String noRekon){
        System.out.println("");
        System.out.println("No reconocido " + noRekon);
        System.out.println("Error Léxico " + "en la línea " +line + ": "+msg);
        String padded = String.format("%-"+(col+27+Integer.toString(line).length())+"s", "");
        System.out.println(padded+"^");
       
            
     
        count++;
        
    }
    /**
     * Ocurre cuando se realiza un operación 
     * @param line linea del archivo
     * @param msg mensaje indicativo
     */
    public void SemErr(int line, String msg){
        System.out.println("");
        System.out.println("Error semántico " + "en la línea " +line);
        System.out.println(">>>   " + msg);
        System.out.println("");
        count++;
        
        
    }
   /**
    * Muestra advertencia de sintaxis que no conlleva a error semántico.
    * @param line linea del archivo
    * @param msg mensaje indicativo
    */
    public void Warning(int line, String msg){
        System.out.println("");
        System.out.println("Advertencia " + "en la línea " +line);
        System.out.println(">>>   " + msg );
        System.out.println("");
        count++;
        
    }
    /**
     * Obtener contador de errores
     * @return entero
     */
    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
  
    

}
