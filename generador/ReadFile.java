/**
* Universidad Del Valle de Guatemala
* 11-sep-2015
* Pablo Díaz 13203
*/



import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import javax.swing.JFileChooser;

/**
 *
 * @author Pablo
 */
public class ReadFile {
    
     public HashMap leerArchivo(File archivo){
      
        int contador=0;
        int tamaño=0;
        String input="";
        BufferedReader br = null;
 
        try {

               
               /* JFileChooser chooser = new JFileChooser();
                int status = chooser.showOpenDialog(null);
                if(status !=JFileChooser.APPROVE_OPTION){
                    System.out.println("\tNo se seleccionó ningún archivo");
                    return null;
                }
                 *///----------------------------------------------------------------------
                //File file = new File(nombre+".txt");
                br = new BufferedReader(new FileReader(archivo.getAbsoluteFile()));
                String sCurrentLine;
               
               int cantidadLineas=1;
             
               HashMap<Integer,String> detailString = new HashMap();
               while ((sCurrentLine = br.readLine()) != null) {
                   
                    
                    
                    
                    if (!sCurrentLine.equals("")){
                        input+=sCurrentLine+"\r\n";
                        detailString.put(cantidadLineas, sCurrentLine);
                    }
                    cantidadLineas++;
                
                }
             
                
           // System.out.println("antes input");
            //System.out.println(input);
        
        //System.out.println(detailString);
        //return input;
        return detailString;
        } catch (IOException e) {
               
        } finally {
                try {
                        if (br != null)br.close();
                } catch (IOException ex) {
                        ex.printStackTrace();
                }
        }
        return null;
        
    }
     public void crearArchivo(String output,String nombreArchivo){
        try {
            
                
                //output += "\r\n"+"\r\n"+"\r\n"+leerArchivo();
                File file;
                File dummy = new File("");
                String path = dummy.getAbsolutePath();
              
               
                file = new File(path+"/generador/"+nombreArchivo+".java");
                // if FileCreator doesnt exists, then create it
               
                
               
                FileWriter fw = new FileWriter(file);
                BufferedWriter bw = new BufferedWriter(fw);
               
                bw.write(output+"\r\n");
            

                bw.close();

                System.out.println("Se ha creado el archivo exitosamente");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
