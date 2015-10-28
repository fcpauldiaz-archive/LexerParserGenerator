/**
* Universidad Del Valle de Guatemala
* 23-sep-2015
* Pablo Díaz 13203
*/

package lexerparsergenerator;


import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;

/**
 * Clase que genera el analizador léxico en el 
 * lenguaje de Java
 * @author Pablo
 */
public class LexerGenerator implements RegexConstants{
    
    private final HashMap<Integer,String> cadena;
    private String nombreArchivo;
    private final HashMap<String, String> cadenaCompleta;
    private final HashMap<String, String> tokensExpr;
    private final TreeMap<String,String> keyMap;
    private final Stack pilaConcatenacion;
    private final Stack pilaAvanzada;
    private String ignoreSets = " ";
    private final String ANY = "[!-#]"+charOr+"[%-.]"+charOr+"[@-Z]"+charOr+"[^-z]";
    private final HashMap<String, Boolean> verKeywords;
   
    
    /**
     * Constructor
     * @param cadena HashMap con la cadena del input
     */
    public LexerGenerator(HashMap cadena){
        this.verKeywords = new HashMap();
        this.pilaConcatenacion = new Stack();
        this.keyMap = new TreeMap();
        this.tokensExpr = new HashMap();
        this.cadenaCompleta = new HashMap();
        this.pilaAvanzada = new Stack();
        this.cadena=cadena;
    }
    
    /**
     * Método para encontrar el nombre del archivo a generar
     */
    public void encontrarNombre(){
        
        for (Map.Entry<Integer, String> entry : cadena.entrySet()) {
            String value = entry.getValue();
            if (value.contains("COMPILER")){
                value = value.trim();
                int index = value.indexOf("R");
                this.nombreArchivo = value.substring(++index,value.length());
                this.nombreArchivo = this.nombreArchivo.trim();

            }

        }
    
    }
    
   /**
    * Genera la  estructura de la clase analizadora
    */
    public void generarClaseAnalizadora() {

        String scanner_total = (
            "/**"+"\n"+
            " * Nombre del archivo: "+this.nombreArchivo+".java"+"\n"+
            " * Universidad del Valle de Guatemala"+"\n"+
            " * Pablo Diaz 13203 " + "\n"+
            " * Descripción: Segundo proyecto. Generador de analizador léxico"+"\n"+
            "**/"+"\n"+
            ""+"\n"+
            "import java.util.Map;"+"\n"+
            "import java.util.HashSet;"+"\n"+
            "import java.util.Comparator;"+"\n"+
            "import java.util.ArrayList;"+"\n"+
            "import java.util.TreeMap;"+"\n"+
            "import java.util.HashMap;"+"\n"+
          
           
          
            ""+"\n"+
            "public class "+this.nombreArchivo+" {"+"\n"+
            ""+"\n"+
            "\t"+"private Simulacion sim = new Simulacion();"+"\n"+
            "\t"+"private ArrayList<Automata> automatas = new ArrayList();"+"\n"+
            "\t"+"private HashMap<Integer,String> input;"+"\n"+   
            "\t"+"private ArrayList keywords = new ArrayList();"+"\n");
           
            scanner_total +=
            "\t"+"private String ignoreSets = \""+ignoreSets.substring(0, ignoreSets.length()-1)+"\";"+"\n"+
            "\t"+"private ArrayList<Token> tokensAcumulados = new ArrayList();"+"\n"+    
            "\t"+"private ArrayList<Token> tokens = new ArrayList();"+"\n"+
            "\t"+"private String tk  = \"\";"+"\n"+
            "\t"+"private Errors errores = new Errors();"+"\n"+"\n"+
                
            "\t"+"public " + this.nombreArchivo+"(HashMap input){"+"\n"+
            "\t"+"\t"+"this.input=input;"+"\n"+
            "\t"+"\n"+
             
           "\t" + "}"    
                
        ;

        scanner_total += crearAutomatasTexto();
        scanner_total += generar();
        scanner_total += metodoRevisar();
       // scanner_total += keyWords();
       // scanner_total += ignoreWords();
        scanner_total+="\n"+"}";
        
        ReadFile fileCreator = new ReadFile();
        fileCreator.crearArchivo(scanner_total, nombreArchivo);
        
    }
    
    /**
     * Obtiene los sets a ignorar
     */
    public void ignoreSets(){
        for (Map.Entry<Integer, String> entry : cadena.entrySet()) {
               String value = entry.getValue();
               int lineaActual = entry.getKey();
               if (this.cadena.get(lineaActual).contains("IGNORE")){
                    if (value.contains("\'")){
                        value = value.replaceAll("\'", "");
                    }else if (value.contains("\"")){
                        value = value.replaceAll("\"","");
                    }
                    /*String whitespace = "\n"+
                            
                            "String regex_1 = "+  value.substring(6,value.indexOf("."))+";"+;
		AFNConstruct ThomsonAlgorithim_1 = new AFNConstruct(regex_1);
		
		Automata temp_1 = ThomsonAlgorithim_1.afnSimple(regex_1);
		temp_1.setTipo("WHITESPACE");
		automatas.add(temp_1);*/
                    tokensExpr.put("WHITESPACE", value.substring(6,value.indexOf(".")));
                    
                   ignoreSets = (value.substring(6,value.indexOf(".")));
                   
               }
        
        }
        System.out.println(ignoreSets);
    }
    
    /**
    * Método para pasara character y keywords a expresiones regulares
    */
    public void generarCharactersYKeywords(){
        ignoreSets();
        RegexConverter convert = new RegexConverter();
        cadenaCompleta.put("ANY", convert.abreviacionOr(ANY));
        
        for (Map.Entry<Integer, String> entry : cadena.entrySet()) {
            String value = entry.getValue();
            if (value.contains("CHARACTERS")){
                int lineaActual = entry.getKey();
                while(true){
                    lineaActual = avanzarLinea(lineaActual);
                    if (this.cadena.get(lineaActual).contains("KEYWORDS"))
                        break;
                    String valor = this.cadena.get(lineaActual);
                    
                    valor = valor.trim();
                    int index = valor.indexOf("=");
                    String ident = valor.substring(0,index);
                    String revisar  = valor.substring(++index,valor.length()-1);
                    revisar = revisar.trim();
                    revisar = crearCadenasOr(revisar);
                  //  System.out.println(ident);
                  //  System.out.println(revisar);
                    
                    
                    cadenaCompleta.put(ident.trim(), revisar);
                    

                    //System.out.println(cadenaCompleta);

                }

            }
            if (value.contains("KEYWORDS")&&!value.contains("EXCEPT")){
                int lineaActual = entry.getKey();
                lineaActual = avanzarLinea(lineaActual);
                while(true){
                    
                    if (this.cadena.get(lineaActual).contains("END")||this.cadena.get(lineaActual).contains("TOKENS"))
                        break;
                    String valor = this.cadena.get(lineaActual);
                    valor = valor.trim();
                    int index = valor.indexOf("=");
                    String ident = valor.substring(0,index);
                    String revisar  = valor.substring(++index,valor.length()-1);
                    revisar = revisar.trim();
                    //revisar = crearCadenasOr(revisar);
                    revisar = revisar.replaceAll("\"", "");
                   //tokensExpr.put(ident.trim(), revisar);
                   
                           
                    keyMap.put(ident.trim(),revisar);
                    tokensExpr.put(ident.trim(),revisar);
                            
                      
                    lineaActual = avanzarLinea(lineaActual);
                    
                }
            }

        }
        //System.out.println(cadenaCompleta);
        
    }
    
    /**
     * Método que lee el archivo de Cocol/R y
     * crea las expresionres de los tokens
     */
    public void generarTokens(){
         for (Map.Entry<Integer, String> entry : cadena.entrySet()) {
            String value = entry.getValue();
            if (value.contains("TOKENS")){
                int lineaActual = entry.getKey();
                while(true){
                    lineaActual = avanzarLinea(lineaActual);
                    if (this.cadena.get(lineaActual).contains("END")||this.cadena.get(lineaActual).contains("IGNORE"))
                        break;
                    String valor = this.cadena.get(lineaActual);
                    
                     
                    
                    valor = valor.trim();
                    int index = valor.indexOf("=");
                    String ident = valor.substring(0,index);
                    String revisar  = valor.substring(++index,valor.length()-1);
                    if (revisar.contains("EXCEPT")){
                        revisar = revisar.substring(0,revisar.indexOf("EXCEPT")).trim();
                        verKeywords.put(ident, Boolean.TRUE);
                    }
                    revisar = revisar.trim();
                    
                    System.out.println("");
                      System.out.println(ident);
                     System.out.println(revisar);      
                    revisar = revisar.replaceAll("\\{", charAbrirParentesis+"");
                    revisar = revisar.replaceAll("\\}", charCerrarParentesis+""+charKleene);
                    revisar = revisar.replaceAll("(\\[)(?=(?:[^\"']|[\"|'][^\"']*\")*)", charAbrirParentesis+"");
                    revisar = revisar.replaceAll("(\\])(?=(?:[^\"']|[\"|'][^\"']*\")*)",charCerrarParentesis+"" +charInt);
                    revisar = revisar.replaceAll("\\|",charOr+"");
                    revisar = formatRegex(revisar);
                     System.out.println(revisar);
                    for (Map.Entry<String, String> entryRegex : cadenaCompleta.entrySet()) {
                        
                        if (revisar.contains(entryRegex.getKey())){
                            revisar = revisar.replaceAll(entryRegex.getKey().trim(), entryRegex.getValue());
                        }
                        
                    }
                    System.out.println("enmedio");
                    System.out.println(revisar);
                    revisar = fixString(revisar);
                    
                    //revisar = revisar.replaceAll("\\((?=(?:[^\"']|[\"|'][^\"']*\")*$)","");
                    
                    /* if (revisar.startsWith("\"")||revisar.startsWith("\\")||revisar.startsWith("\'")){
                        revisar = revisar.replaceAll("\\((?!(?:[^\"]*\"[^\"]*\")*[^\"]*$)", charAbrirParentesis+"");
                        revisar = revisar.replaceAll("\\)(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", charCerrarParentesis+"");
                     }
                     else{
                        revisar = revisar.replaceAll("\\((?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)",charAbrirParentesis+"");
                        revisar = revisar.replaceAll("\\)(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)",charCerrarParentesis+"");
                     }            
                    */
                    // revisar = revisar.replaceAll("\\)(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)",charCerrarParentesis+"");
                    //revisar = revisar.replaceAll("\\((?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)",charAbrirParentesis+"");
                    //revisar = revisar.replaceAll("\\)(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)",charCerrarParentesis+"");
                    
                    //revisar = revisar.replaceAll("'(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)","");
                    //revisar = revisar.replaceAll("\"", "");
                   // revisar = revisar.replaceAll("(\")(?=(?:[^\"']|[\"|'][^\"']*\")*$)","");
                   // revisar = revisar.replaceAll("\'", "");
                    //revisar = revisar.replaceAll("\\\"", "");
                   /* System.out.println(revisar);
                    revisar = poner(revisar);
                    System.out.println(revisar);
                    System.out.println("quitando");
                    revisar = quitar(revisar);
                    revisar = poner2(revisar);
                    revisar = quitar2(revisar);*/
                 //quitar parentesis
                 //quitar doble quotes
                 //quitar simple quotes
                   
                    
                  
                    revisar = revisar.replaceAll("\\s","");
                    //System.out.println(ident);
                   // System.out.println(revisar);
                    tokensExpr.put(ident.trim(), revisar);
                    
                    System.out.println(revisar);
                    

                }

            }
         }
       // System.out.println(tokensExpr);
    }
    
    /**
     * quitar parentesis, double quotes, simple quotes
     * @param eval
     * @return String con formato para autómatas
     */
    public String formatRegex(String eval){
        String returnString="";
        
        for (int i =0;i<eval.length();i++){
            Character ch = eval.charAt(i);
            
            if (i>0){
                if (ch=='('&&eval.charAt(i-1)!='\"'){
                    ch = charAbrirParentesis;
                }
                if (i>1){
                    if (ch=='\"'&&(eval.charAt(i-1)!='\\')){
                        continue;
                    }
                    if (ch=='\"'&&eval.charAt(i-2)=='\\'){
                        continue;
                    }
                }
                if (ch=='\''&&(eval.charAt(i-1)!='\\')&&eval.charAt(i-1)!='\"')
                    continue;
                
            }
            if (i==0){
                if (ch=='(')
                    ch = charAbrirParentesis;
                if (ch=='\"')
                    continue;
            }
            if (i+1<eval.length()){
                if (ch==')'&&eval.charAt(i+1)!='\"'){
                    ch = charCerrarParentesis;
                }
            }
            else{
                if (ch==')')
                    ch = charCerrarParentesis;
            }
            returnString += ch;   
        }
        
        
        return returnString;
    }
    
    /**
     * Método para agregar los caracteres de escape que lo necesiten
     * @param eval
     * @return String modificado con las correciones necesarias
     */
    public String fixString(String eval){
        String returnString = "";
      
        for (int i=0;i<eval.length();i++){
             String pos = "";
            Character ch = eval.charAt(i);
            if (ch == '\"'){//agregar escape chars en caso de que no lo tenga
                if (eval.charAt(i-1)!='\\'){
                    pos="\\";
                }
            }
            if (i+1<eval.length()){//eliminar dos caracteres or seguidos
                if (ch==charOr&&eval.charAt(i+1)==charOr)
                    continue;
            }
           
            returnString += pos +ch;
        }
        
        return returnString;
    }
    
    
    public String quitar(String eval){
        String returnString ="";
        char[] charArray = eval.toCharArray();
        for (int i=0;i<charArray.length;i++){
           
          
            if (i>1 && i<eval.length()-3){
                if (charArray[i]=='"'&&(((charArray[i-1]=='\\')||charArray[i+1]!='\"'||charArray[i+1]=='\''))&&charArray[i+1]!='"')
                    continue;
               
            }
            
            if (i<=1){
                if (charArray[i+1]=='\''&&eval.charAt(i)=='\"')
                    continue;
            }
            if (i>=charArray.length-3){
                if (charArray[i]=='"'&&charArray[i-1]!='\\')
                    continue;
            }
            returnString +=charArray[i];
        }
        
        return returnString;
    }
    public String poner(String eval){
        String returnString ="";
        for (int i =0;i<eval.length();i++){
            Character ch = eval.charAt(i);
            String pos ="";
            String pos2 = "";
            String pos1 = "";
            if (i>0){
                if ((ch=='\''||ch=='\\'||ch=='"')&&eval.charAt(i-1)==charOr&&eval.charAt(i+1)==charOr){
                    pos="\\";
                    pos1="\"";
                    pos2=pos1;
                }
            }
            returnString += pos1+pos+ch+pos2;
        }
        return returnString;
    }
    public String poner2(String eval){
        String returnString ="";
        for (int i =0;i<eval.length();i++){
            Character ch = eval.charAt(i);
            String pos ="";
            String pos2 = "";
            String pos1 = "";
            if (i>0&&i<eval.length()-2){
                if ((ch=='\''||ch=='\"')&&eval.charAt(i-1)!='\\'&&!(eval.charAt(i+2)=='\''||eval.charAt(i-2)=='\'')){
                    pos="\\";
                   // pos1="\"";
                   // pos2=pos1;
                }
            }
            if (i==0){
                if (eval.charAt(i+1)!='\\'&&(ch=='\''||ch=='\"')){
                    pos="\\";
                    /*pos1="\"";
                    pos2=pos1;*/
                }
            }
            if (i>=eval.length()-2){
                if (eval.charAt(i-1)!='\\'&&ch=='\''&&eval.charAt(i-1)!='*'){
                    pos="\\";
                    /*pos1="\"";
                    pos2=pos1;*/
                }
            }
        
          returnString += pos+ch;
        }
        return returnString;
        
    }
    
    /**
     * Crea una cadena con operaciones or en cada caracter
     * En caso de haber concatenaciones calcula si tiene que
     * ser por la izquierda o derecha
     * @param cadena a evaluar
     * @return String con la cadena modificada
     */
    public String crearCadenasOr(String cadena){
        
        String or = "";
        
        or = cadenasOrLista(cadena);
        if (!or.isEmpty()&&!cadena.contains("+")){
            //System.out.println(or);
            return or;
        }
        if (cadena.equals("'+'")){
            //System.out.println(cadena);
            return "+";
        }
        if (cadena.equals("'-'")){
            //System.out.println(cadena);
            return "-";
        }
        if ((cadena.startsWith("\"")||cadena.startsWith("\'"))&&(!cadena.contains("+"))){
             
            try{
               
            cadena=  cadena.substring(cadena.indexOf("\"")+1,cadena.lastIndexOf("\""));
           
            }catch(Exception e){}
            
             try{
                
            cadena=  cadena.substring(cadena.indexOf("\'")+1,cadena.lastIndexOf("\'"));
           
            }catch(Exception e){}
            
            for (int i = 0;i<cadena.length();i++){
                Character c = cadena.charAt(i);
               
                    
                        if (c=='\\'){
                           
                            or += c;
                            or += cadena.charAt(i+1);
                            i++;
                           
                        }
                         if (c == '$'){
                            or += "\\" + c;
                        }
                        else if (i<=cadena.length()-2){
                            or += c;
                            or += charOr;
                        }            
                        else if (i>cadena.length()-2)
                            or +=c;
                        

                
            }
           
            
           
        }
        else {
            or = cadena;
            if(cadena.contains("+")&&!cadena.contains("-")){
                if ((cadena.contains("\"")||cadena.contains("\'"))&&!cadena.contains("..")){
                int cantidadConcatenaciones = count(cadena,'+');
                if (cantidadConcatenaciones>1){
                   // System.out.println(cadena.lastIndexOf("+"));
                    //System.out.println(cadena.substring(0, cadena.indexOf("+", cadena.indexOf("+") + 1)));
                    pilaConcatenacion.push(cadena.substring(cadena.indexOf("+", cadena.indexOf("+") + 1)));
                   // System.out.println(pilaConcatenacion);
                }
               // System.out.println(cantidadConcatenaciones);
                int preIndex=0;
                if (cadena.contains("\""))
                     preIndex = cadena.indexOf(("\""));
                 if (cadena.contains("\'"))
                     preIndex = cadena.indexOf(("\'"));
                String w = cadena.substring(preIndex+1);
                int postIndex = w.length()-1;
                if (w.contains("\""))
                    postIndex = w.indexOf(("\""));
                if (w.contains("\'"))
                    postIndex = w.indexOf(("\'"));
                String wFinal = cadena.substring(preIndex,preIndex+postIndex+2);
                
                //System.out.println(wFinal);
                String cadenaOr = crearCadenasOr(wFinal);
                //calcular si se concatena a la izquierda o derecha
                int lado = calcularConcatenacion(or);
                if (lado == -1){
                    
                    or = charAbrirParentesis+buscarExpr(or) +charCerrarParentesis+charOr+charAbrirParentesis+ cadenaOr+charCerrarParentesis;
                }
                else if (lado == 1){
                    
                    or = charAbrirParentesis+cadenaOr +charCerrarParentesis+charOr+charAbrirParentesis+ buscarExpr(or)+charCerrarParentesis;
                }
                
                while (!pilaConcatenacion.isEmpty()){
                    String faltante = (String)pilaConcatenacion.pop();
                    cantidadConcatenaciones = count(faltante,'+');
                    if (cantidadConcatenaciones>1){
                        pilaConcatenacion.push(faltante.substring(faltante.indexOf("+", faltante.indexOf("+") + 1)));
                        faltante = (faltante.substring(0, faltante.indexOf("+", faltante.indexOf("+") + 1)));

                    }
                        or = concatenacion(or,faltante);
                }
                }
                else if (!cadena.contains("..")){
                    int cantidadConcatenaciones = count(cadena,'+');
                    if (cantidadConcatenaciones>1){
                        // System.out.println(cadena.lastIndexOf("+"));
                         //System.out.println(cadena.substring(0, cadena.indexOf("+", cadena.indexOf("+") + 1)));
                             pilaConcatenacion.push(cadena.substring(cadena.indexOf("+", cadena.indexOf("+") + 1)));
                        // System.out.println(pilaConcatenacion);
                    }
                    String subcadena = cadena.substring(0,cadena.indexOf("+"));
                    String ident1 = buscarExpr(subcadena);
                    String subcadena2 = cadena.substring(cadena.indexOf("+")+1);
                    String ident2 = buscarExpr(subcadena2);
                     int lado = calcularConcatenacion(or);
                if (lado == -1){
                    
                    or = charAbrirParentesis+ident1 +charCerrarParentesis+charOr+charAbrirParentesis+ ident2+charCerrarParentesis;
                }
                else if (lado == 1){
                    
                    or = charAbrirParentesis+ident2 +charCerrarParentesis+charOr+charAbrirParentesis+ ident1+charCerrarParentesis;
                }
                  while (!pilaConcatenacion.isEmpty()){
                    String faltante = (String)pilaConcatenacion.pop();
                    cantidadConcatenaciones = count(faltante,'+');
                    if (cantidadConcatenaciones>1){
                        pilaConcatenacion.push(faltante.substring(faltante.indexOf("+", faltante.indexOf("+") + 1)));
                        faltante = (faltante.substring(0, faltante.indexOf("+", faltante.indexOf("+") + 1)));

                    }
                    else
                         faltante = (faltante.substring(faltante.indexOf("+")+1));
                    or = concatenacionIdent(or,faltante);
                }
                    
                }
                else {
                    int cantidadConcatenaciones = count(cadena,'+');
                     String subcadena2="";
                    if (cantidadConcatenaciones>1){
                        // System.out.println(cadena.lastIndexOf("+"));
                         //System.out.println(cadena.substring(0, cadena.indexOf("+", cadena.indexOf("+") + 1)));
                             pilaConcatenacion.push(cadena.substring(cadena.indexOf("+", cadena.indexOf("+") + 1)));
                        // System.out.println(pilaConcatenacion);
                              subcadena2 = cadena.substring(cadena.indexOf("+")+1,cadena.indexOf("+", cadena.indexOf("+") + 1));
                    }
                    else
                          subcadena2 = cadena.substring(cadena.indexOf("+")+1);
                    String subcadena = cadena.substring(0,cadena.indexOf("+"));
                  
                    String list1 = cadenasOrLista(subcadena);
                    String list2 = cadenasOrLista(subcadena2);
                    if (list1.isEmpty())
                        list1 = crearCadenasOr(subcadena);
                    if (list2.isEmpty())
                        list2 = crearCadenasOr(subcadena2.trim());
                    or = list1+charOr+list2; 
                      while (!pilaConcatenacion.isEmpty()){
                    String faltante = (String)pilaConcatenacion.pop();
                    cantidadConcatenaciones = count(faltante,'+');
                    if (cantidadConcatenaciones>1){
                        pilaConcatenacion.push(faltante.substring(faltante.indexOf("+", faltante.indexOf("+") + 1)));
                        faltante = (faltante.substring(faltante.indexOf("+")+1, faltante.indexOf("+", faltante.indexOf("+") + 1)));

                    }
                    else
                         faltante = (faltante.substring(faltante.indexOf("+")+1));
                    or =  or +charOr+crearCadenasOr(faltante.trim());
                    
                }
                    
                    return charAbrirParentesis+or+charCerrarParentesis;
                }
            }else if (cadena.contains("-")&&!cadena.contains("+")){
                if ((cadena.contains("\"")||cadena.contains("\'"))&&!cadena.contains("..")){
                   
                    int cantidadConcatenaciones = count(cadena,'-');
                    String subcadena2="";
                    if (cantidadConcatenaciones>1){
                       // System.out.println(cadena.lastIndexOf("+"));
                        //System.out.println(cadena.substring(0, cadena.indexOf("+", cadena.indexOf("+") + 1)));
                        pilaConcatenacion.push(cadena.substring(cadena.indexOf("-", cadena.indexOf("-") + 1)));
                       // System.out.println(pilaConcatenacion);
                         subcadena2 = cadena.substring(cadena.indexOf("-")+1,cadena.indexOf("-", cadena.indexOf("-") + 1));
                    }
                     else
                            subcadena2 = cadena.substring(cadena.indexOf("-")+1);
                    String subcadena = cadena.substring(0,cadena.indexOf("-"));
                   // System.out.println(cantidadConcatenaciones);
                    int preIndex=0;
                    String expr  =  buscarExpr(subcadena2);
                    subcadena2 = subcadena2.trim();
                        
                    if (!expr.isEmpty()){
                        expr = expr.replaceAll(charAbrirParentesis+"", "");
                        expr = expr.replaceAll(charCerrarParentesis+"", "");
                        subcadena2 = expr;
                    }
                    if (subcadena2.startsWith("\""))
                        subcadena2 = subcadena2.substring(1);
                    if (subcadena2.endsWith("\""))
                        subcadena2 =subcadena2.substring(0,subcadena2.length()-1);
                    else if (subcadena2.startsWith("\'")){
                        subcadena2 = subcadena2.substring(1);
                    if (subcadena2.endsWith("\'"))
                        subcadena2 =subcadena2.substring(0,subcadena2.length()-1);
                    }
                    /*String tiene =  buscarExpr(subcadena);
                    String quitar = cadenaOr;*/
                    or = buscarExpr(subcadena);
                    int indexQuitar=0;
                    if (or.contains(subcadena2)){
                       or = or.replaceAll(subcadena2, "");
                    }
                    or = balancear(or);
                    while (!pilaConcatenacion.isEmpty()){
                        String faltante = (String)pilaConcatenacion.pop();
                        cantidadConcatenaciones = count(faltante,'-');
                        if (cantidadConcatenaciones>1){
                            pilaConcatenacion.push(faltante.substring(faltante.indexOf("-", faltante.indexOf("-") + 1)));
                            faltante = (faltante.substring(faltante.indexOf("-")+1, faltante.indexOf("-", faltante.indexOf("-") + 1)));

                        }  else
                             faltante = (faltante.substring(faltante.indexOf("-")+1));
                        expr  =  buscarExpr(faltante);
                        faltante = faltante.trim();
                        
                        if (!expr.isEmpty()){
                            expr = expr.replaceAll(charAbrirParentesis+"", "");
                            expr = expr.replaceAll(charCerrarParentesis+"", "");
                            faltante = expr;
                        }
                       
                        if (faltante.startsWith("\'"))
                            faltante = faltante.substring(1);
                        
                        if (faltante.endsWith("\'"))
                            faltante =faltante.substring(0,faltante.length()-1);
                        
                        else if (faltante.startsWith("\"")){
                            faltante = faltante.substring(1);
                            if (faltante.endsWith("\""))
                                faltante =faltante.substring(0,faltante.length()-1);
                        }
                        if (or.contains((faltante))){
                            or = or.replaceAll(faltante,"");
                           // or = or.replaceAll(faltante,"");
                           
                        }
                        System.out.println(or);
                        or = balancear(or);
                        System.out.println(or);
                    }
                } 
            }else if (cadena.contains("+")&&cadena.contains("-")){
             String mutador="";
             String original="";
              int indexOperadoresPlus = cadena.indexOf("+");
              int indexOperadoresMin  =  cadena.indexOf("-");
              if (indexOperadoresPlus<indexOperadoresMin){
                  mutador = cadena.substring(0,indexOperadoresPlus);
                  original = cadena.substring(indexOperadoresPlus);
              }
              else{
                  mutador = cadena.substring(0,indexOperadoresMin);
                  original = cadena.substring(indexOperadoresMin);
              }
              pilaAvanzada.push(original);
              while (!pilaAvanzada.isEmpty()){
                  String actual = (String)pilaAvanzada.pop();
                  
              }
              
            }
            
        }
        
        return charAbrirParentesis+or+charCerrarParentesis;
     }
    
    /**
     * Crea las listas con los caracteres deseados, desde char hasta otro char
     * @param cadena
     * @return 
     */
    public String cadenasOrLista(String cadena){
        String or ="";
       
        if (cadena.contains("CHR")||cadena.contains("..")){
                if (cadena.contains("CHR")){
                int empieza = Integer.parseInt(cadena.substring(cadena.indexOf("(")+1,cadena.indexOf(")")));
                int termina = Integer.parseInt(cadena.substring(cadena.lastIndexOf("(")+1,cadena.lastIndexOf(")")));
                
               
                RegexConverter convert = new RegexConverter();
                or = convert.abreviacionOr("["+(char)(empieza)+"-"+(char)(termina)+"]");
                }
                else{
                    String empieza = (cadena.substring(cadena.indexOf("\'")+1,cadena.indexOf("\'", cadena.indexOf("\'") + 1)));
                    
                    String termina = (cadena.substring(cadena.lastIndexOf("\'")-1,cadena.lastIndexOf("\'")));
                    RegexConverter convert = new RegexConverter();
                    or =  convert.abreviacionOr("["+(empieza)+"-"+(termina)+"]");
                }
              
            }
        return or;
    }
    
    public String concatenacionIdent(String anterior, String actual){
        return anterior + charOr + buscarExpr(actual);
    }
    
    /**
     * Método auxiliar que se llama cuando hay más de una concatenación
     * @param anterior String con lo ya concatenado
     * @param actual String con lo que falta concatenar
     * @return String con expresión regular
     */ 
    public String concatenacion(String anterior, String actual){
            String resultado = "";
            String cadenaOr=anterior;
            if (actual.contains("\""))
                cadenaOr = crearCadenasOr(actual);
             if (actual.contains("\'"))
                cadenaOr = crearCadenasOr(actual);
            //calcular si se concatena a la izquierda o derecha
            int lado = calcularConcatenacion(actual);
            if (lado == -1){

                resultado = charAbrirParentesis+buscarExpr(actual) +charCerrarParentesis+charOr+charAbrirParentesis+ cadenaOr+charCerrarParentesis;
            }
            else if (lado == 1){

                resultado = charAbrirParentesis+cadenaOr +charCerrarParentesis+charOr+charAbrirParentesis+ buscarExpr(actual)+charCerrarParentesis;
            }
        return resultado;
     }
    
    /**
     * Método para avanzar de línea, busca la línea que actual
     * @param lineaActual
     * @return lineaActual
     */
    public Integer avanzarLinea(int lineaActual){
       while (true){
           if (this.cadena.containsKey(++lineaActual))
               return lineaActual;
       }
    }
    
    /**
     * Método para calcular la posición a concatenar
     * @param str
     * @return 
     */
    public int calcularConcatenacion(String str){
        int posicion = 0;
        int posConc = str.indexOf("+")+1;
        String ident = buscarIdent(str);
        int indexIdent = str.indexOf(ident) + ident.length();
        if (indexIdent<posConc)
            return -1;
        else if (indexIdent>posConc)
            return 1;
        
        return posicion;
    }
    
    /**
     * Método para buscar un identificador en el archivo
     * @param search identificador a buscar
     * @return Devuelve el identificador
     */
    public String buscarIdent(String search){
        String res = "";
        for (Map.Entry<String, String> entry : cadenaCompleta.entrySet()) {
            String value = entry.getKey();
            if (search.contains(value)){
                return value;
                // res = entry.getValue();
                
                
            }

        }
        Errors errores = new Errors();
        errores.WarningIdent("identificador no declarado");
        //System.out.println("Identificador no declarado "+ "\n");
        return res;
        
    }
    
    /**
     * Método para buscar los identificadores de keywords
     * @param search
     * @return true si fue encontrado
     */
    public boolean buscarIdentKey(String search){
       
        for (Map.Entry<String, Boolean> entry : verKeywords.entrySet()) {
            String value = entry.getKey();
            if (value.contains(search)){
                return true;
                // res = entry.getValue();
                
                
            }

        }
      
        return false;
        
    }
    
    /**
     * Método para buscar un identificador en el archivo
     * @param search identificador a buscar
     * @return devuelve expresión regular asociada al identificador
     */
    public String buscarExpr(String search){
        String res = "";
        for (Map.Entry<String, String> entry : cadenaCompleta.entrySet()) {
            String value = entry.getKey();
            if (search.trim().contains(value)){
                return entry.getValue();
                
                
                
            }

        }
        return res;
     }
    
    public String balancear(String subcadena){
        String subcadenaBal = "";
        for (int i = 0;i<subcadena.length();i++){
            Character ch = subcadena.charAt(i);
            if (i+1<subcadena.length()){
                if (ch != subcadena.charAt(i+1)){
                    subcadenaBal += ch;
                }
            }else
                subcadenaBal += ch;
        }
        return subcadenaBal;
    }
 
    /**
     * Método para calcular el número de ocurrencias de un character
     * @param s string completo 
     * @param c character a calcular ocurrencias
     * @return 
     */
    public  int count( final String s, final char c ) {
        final char[] chars = s.toCharArray();
        int count = 0;
        for(int i=0; i<chars.length; i++) {
          if (chars[i] == c) {
            count++;
          }
          if (i+1<chars.length){
              if (chars[i]=='\''&&chars[i+1]=='+'&&chars[i+2]=='\'')
                  count--;
        }
        }
        return count;
    }
   
    /**
     * Método para exportar los autómatas a texto plano
     * @return Devuelve un string con los autómatass
     */
    public String crearAutomatasTexto(){
       String afn = "";
       afn += "\n";
       afn += "\tpublic void automatas(){";
       afn += "\n";
       int counter = 0;
       for (Map.Entry<String, String> entry : tokensExpr.entrySet()) {
            String value = entry.getValue();
            String key = entry.getKey();
            if (value.length()>1&&!value.trim().isEmpty()){
                afn += "\n"+
                     "\t"+"\t" + "RegexConverter convert_"+counter+"= new RegexConverter();"+"\n"+
                     "\t"+"\t" +"String regex_"+counter+" = convert_"+counter+".infixToPostfix("+"\""+value+"\""+");"+"\n"+
                     "\t"+"\t" +"AFNConstruct ThomsonAlgorithim_"+counter+" = new AFNConstruct(regex_"+counter+");"+"\n"+
                     "\t"+"\t" +"ThomsonAlgorithim_"+counter+".construct();"+"\n"+
                     "\t"+"\t" +"Automata temp_"+counter+ " = ThomsonAlgorithim_"+counter+".getAfn();"+"\n"+
                     "\t"+ "\t" +"temp_"+counter+".setTipo("+"\""+key+"\""+");"+"\n";
                       
                      if (buscarIdentKey(key)){
                          afn += "\t"+ "\t"+"temp_"+counter+".setExceptKeywords(true);"+"\n";
                      }
                     afn+="\t"+"\t" +"automatas.add(temp_"+counter+ ");"+"\n";
                     
              
            }else{
                 afn += "\n"+
                     
                     "\t"+"\t" +"AFNConstruct ThomsonAlgorithim_"+counter+" = new AFNConstruct();"+"\n"+
                     "\t"+"\t" +"Automata temp_"+counter+ " ="+"ThomsonAlgorithim_0"+".afnSimple("+"\""+value+"\""+");"+"\n"+
                     "\t"+ "\t" +"temp_"+counter+".setTipo("+"\""+key+"\""+");"+"\n"+
                     "\t"+"\t" +"automatas.add(temp_"+counter+ ");"+"\n";
                
            }
            counter++;
        }
       afn += "\t}";
       
       
       return afn;
   }
    
    /**
     * Métodos auxiliares para revisar autómatas
     * @return string con los métodos
     */
    public String generar(){
      String metodoVer= "\n"+
     "\t"+" /**" +"\n "+
     "\t"+"* Método para revisar que tipo de sub autómata es aceptado por una "+"\n"+
     "\t"+"* expresión regular"+"\n"+
     "\t"+"* @param regex expresión regular a comparar"+"\n"+
     "\t"+"* @param conjunto arreglo de autómatas"+"\n"+
     "\t"+"*/"+"\n"+
    "\t"+"public void checkIndividualAutomata(String regex, ArrayList<Automata> conjunto,int lineaActual){"+"\n"+
        
            "\t"+"\t"+"ArrayList<Boolean> resultado = new ArrayList();"+"\n"+
               
            "\t"+"\t"+"ArrayList tks = tokenMasLargo(regex, conjunto, lineaActual);"+"\n"+
            "\t"+"\t"+"if (!(Boolean)tks.get(0)&&!((String)tks.get(1)).isEmpty()){"+"\n"+
                "\t"+"\t"+"\t"+"errores.LexErr(lineaActual,regex.indexOf((String)tks.get(1)),regex,(String)tks.get(1));"+"\n"+
            "\t"+"\t"+"}"+"\n"+
              
    "\t"+"}"+"\n"+
    
    "\t"+"/**"+"\n"+
    "\t"+" * Método que devuelve las posiciones en las que el valor que tiene "+"\n"+
    "\t"+" * en cada posicion es true"+"\n"+
    "\t"+" * @param bool arreglo de booleanos"+"\n"+
    "\t"+" * @return arreglo de enteros"+"\n"+
    "\t"+" */"+"\n"+
    "\t"+"public ArrayList<Integer>  checkBoolean(ArrayList<Boolean> bool){"+"\n"+
        "\t"+"\t"+"ArrayList<Integer> posiciones = new ArrayList();"+"\n"+
       
        "\t"+"\t"+"for (int i = 0;i<bool.size();i++){"+"\n"+
            "\t"+"\t"+"\t"+"if (bool.get(i))"+"\n"+
                "\t"+"\t"+"\t"+"\t"+"posiciones.add(i);"+"\n"+
        "\t"+"\t"+"}"+"\n"+
        "\t"+"\t"+"return posiciones;"+"\n"+
        
    "\t"+"}"+"\n";
        return metodoVer;
    }
    
    public String metodoRevisar() {
        String res = "\n"+
        "\t"+"public void revisar(){"+"\n"+

        "\t"+"\t"+"for (Map.Entry<Integer, String> entry : input.entrySet()) {"+"\n"+
            "\t"+ "\t"+"\t"+"Integer key = entry.getKey();"+"\n"+
            "\t"+"\t"+"\t"+"String value = entry.getValue();"+"\n"+
            "\t"+"\t"+"\t"+"String[] parts = value.split(ignoreSets);"+"\n"+
            "\t"+"\t"+"\t"+"tokens.clear();"+"\n"+ 
             "\t"+"\t"+"\t"+"tk=\"\";"+"\n"+    
            "\t"+"\t"+"\t"+"for (String part : parts) {"+"\n"+
               
                        
                    "\t"+"\t"+"\t"+" this.checkIndividualAutomata(part + \"\", automatas, key);"+"\n"+
                       
            "\t"+"\t"+"\t"+"}"+"\n"+
               "\t"+"\t"+"System.out.println(tokens);"+"\n"+
                 "\t"+"\t"+"tokensAcumulados.addAll(tokens);"+"\n"+
        "\t"+"\t"+"}"+"\n"+
                
               
              
    "\t"+"}"+"\n"+"\n"+"\n";
        
        res+=
        "\t"+ "/**"+"\n"+
        "\t"+" * Método para reconocer los tokens más simples"+"\n"+
        "\t"+" * @param regex"+"\n"+
        "\t"+" * @param conjuntoAut"+"\n"+
        "\t"+" * @return boolean true si se crea un token, false si no es aceptado por ninguno."+"\n"+
        "\t"+" */"+"\n"+
        "\t"+"public boolean tokenSimple(String regex,ArrayList<Automata> conjuntoAut){"+"\n"+
                "\t"+"\t"+"TreeMap<String,Automata> aceptados = new TreeMap(new Comparator<String>() {"+"\n"+
                "\t"+"\t"+"@Override"+"\n"+
                "\t"+"\t"+"public int compare(String o1, String o2) {"+"\n"+
                    "\t"+"\t"+"\t"+"Integer a1 = o1.length();"+"\n"+
                    "\t"+"\t"+"\t"+"Integer a2 = o2.length();"+"\n"+
                    "\t"+"\t"+"\t"+"return a2-a1;"+"\n"+
                "\t"+"\t"+"}"+"\n"+
            "\t"+"\t"+"});"+"\n"+"\n"+   
        
            "\t"+"\t"+"for (Automata automata: conjuntoAut){"+"\n"+
                    "\t"+"\t"+"\t"+"if (sim.simular(regex,automata)){"+"\n"+
                     "\t"+"\t"+"\t"+"\t"+"aceptados.put(regex, automata);"+"\n"+"\n"+
                       
                    "\t"+"\t"+"\t"+"}"+"\n"+
                "\t"+"\t"+"}"+"\n"+
            "\t"+"\t"+"if (!aceptados.isEmpty()){" +"\n"+
                    "\t"+"\t"+"\t"+"tokens.add(new Token(aceptados.firstEntry().getValue().getTipo(),aceptados.firstKey(),aceptados.firstEntry().getValue().isExceptKeywords()));"+"\n"+"\n"+
                  
                    "\t"+"\t"+"\t"+"return true;"+"\n"+
            "\t"+"\t"+"}"+"\n"+"\n"+
            
            
            "\t"+"\t"+"return false;"+"\n"+
        "\t"+"}"+"\n"+"\n";
                
        res+=
        "\t"+"/**"+"\n"+
	"\t"+"* Método para simular y reconocer el caracter más largo"+"\n"+
	"\t"+"* "+"\n"+
	"\t"+"* @param regex recibe la cadena a simular"+"\n"+
        "\t"+"* @param conjuntoAut "+"\n"+
        "\t"+"* @param lineaActual linea del regex en el archivo "+"\n"+       
        "\t"+"* @return boolean si es falso no fue reconocido nada"+"\n"+
	"\t"+"*/"+"\n"+
	"\t"+"public ArrayList tokenMasLargo(String regex, ArrayList<Automata> conjuntoAut, int lineaActual)"+"\n"+
	"\t"+"{   "+"\n"+
               "\t"+"\t"+"boolean tokenSimple = tokenSimple(regex,conjuntoAut);"+"\n"+"\n"+
               "\t"+"\t"+"if (tokenSimple){"+"\n"+
                "\t"+"\t"+"\t"+"ArrayList casoBase = new ArrayList();"+"\n"+
                "\t"+"\t"+"\t"+"casoBase.add(true);"+"\n"+
                "\t"+"\t"+"\t"+"casoBase.add(regex);"+"\n"+
               "\t"+"\t"+ "\t"+"return casoBase;" +"\n"+"\n" +
                "\t"+"\t"+"}"+"\n"+
                
               "\t"+"\t"+"while (tokens.isEmpty()||!regex.isEmpty()){"+"\n"+"\n"+
               
               "\t"+ "\t"+"\t"+"if (!tokenSimple(regex,conjuntoAut)){"+"\n"+"\n"+
                    "\t"+"\t"+"\t"+"\t"+"try{"+"\n"+

                       "\t"+"\t"+"\t"+"\t"+"\t"+"tk = regex.substring(regex.length()-1)+tk;"+"\n"+"\n"+

                       "\t"+"\t"+"\t"+"\t"+"\t"+"ArrayList returnBool = tokenMasLargo(regex.substring(0,regex.length()-1),conjuntoAut, lineaActual);"+"\n"+"\n"+

                       "\t"+"\t"+"\t"+"\t"+"\t"+"if (!(Boolean)returnBool.get(0)){"+"\n"+
                          "\t"+"\t"+ "\t"+"\t"+"\t"+"\t"+"ArrayList casoRecursivo = new ArrayList();"+"\n"+
                         "\t"+"\t"+ "\t"+"\t"+"\t"+"\t"+"casoRecursivo.add(false);"+"\n"+
                         "\t"+"\t"+ "\t"+"\t"+"\t"+"\t"+"casoRecursivo.add((String)returnBool.get(1));"+"\n"+
                          "\t"+"\t"+ "\t"+"\t"+"\t"+"\t"+"return casoRecursivo;"+"\n"+
                        "\t"+"\t"+ "\t"+"\t"+"\t"+"}"+"\n"+
                       "\t"+"\t"+"\t"+"\t"+"\t"+"if ((Boolean)returnBool.get(0)){"+"\n"+
                           "\t"+"\t"+"\t"+"\t"+"\t"+"\t"+"regex = tk;"+"\n"+
                           "\t"+"\t"+"\t"+"\t"+"\t"+"\t"+"tk = \"\";"+"\n"+
                       "\t"+"\t"+"\t"+"\t"+"\t"+"}"+"\n"+
                    "\t"+"\t"+"\t"+"\t"+"}catch(Exception e){"+"\n"+"\n"+
                         "\t"+"\t"+"\t"+"\t"+"\t"+"ArrayList casoRecursivo = new ArrayList();"+"\n"+
                    "\t"+"\t"+"\t"+"\t"+"\t"+"casoRecursivo.add(false);"+"\n"+
                    "\t"+"\t"+"\t"+"\t"+"\t"+"casoRecursivo.add(tk);"+"\n"+
                        "\t"+"\t"+"\t"+"\t"+"\t"+"return casoRecursivo;"+"\n"+
                    "\t"+"\t"+"\t"+"\t"+"}//cierra catch"+"\n"+
                "\t"+"\t"+"\t"+"}//cierra !if"+"\n"+
                   "\t"+"\t"+"\t"+"else{"+"\n"+
               "\t"+"\t"+"\t"+" ArrayList casoRecusivo = new ArrayList();"+"\n"+
                "\t"+"\t"+"\t"+"casoRecusivo.add(true);"+"\n"+
                "\t"+"\t"+"\t"+"return casoRecusivo;"+"\n"+
                "\t"+"\t"+"}"+"\n"+
                
            "\t"+"\t"+"}//cierra while"+"\n"+"\n"+
            "\t"+"\t"+ "if (regex.isEmpty()&&!tk.isEmpty()){"+"\n"+
                "\t"+"\t"+"\t"+" ArrayList casoRecusivo = new ArrayList();"+"\n"+
                "\t"+"\t"+"\t"+"  casoRecusivo.add(false);"+"\n"+
                "\t"+"\t"+"\t"+"casoRecusivo.add(tk);"+"\n"+
                "\t"+"\t"+"\t"+" return casoRecusivo;"+"\n"+
            "\t"+"\t"+"}"+"\n"+"\n"+ 
         "\t"+"\t"+"ArrayList casoRecusivo = new ArrayList();"+"\n"+"\n"+
        "\t"+"\t"+"casoRecusivo.add(false);"+"\n"+"\n"+
        "\t"+"\t"+"casoRecusivo.add(regex);"+"\n"+"\n"+
        "\t"+"\t"+"return  casoRecusivo;"+"\n"+"\n"+
	"\t"+"}";
        return res;
    }
    
    public String ignoreWords(){
        String words = "\n"+
        "\t"+"public void ignoreWords(){"+"\n";
        
         words+="\t"+"\t"+"}"+"\n";
        
         
        return words;
    }
    /**
     * Método para generar Main del analizador
     */
    public void generarMain(){
        String res = "\n"+
        
        
        "import java.util.HashMap;"+"\n"+
        "import javax.swing.JFileChooser;"+"\n"+
        "import java.io.File;"+"\n"+

        "/**"+"\n"+
         "*"+"\n"+
         "* @author Pablo"+"\n"+
         "*/"+"\n"+
        "public class "+ "resultadoGenerador" +"Main"+" {"+"\n"+
    
            "public static String EPSILON = \"ε\";"+"\n"+
            "public static char EPSILON_CHAR = EPSILON.charAt(0);"+"\n"+
    

    "/**"+"\n"+
     "* @param args the command line arguments"+"\n"+
    " */"+"\n"+
    "public static void main(String[] args) {"+"\n"+
        "\t"+ "// TODO code application logic here"+"\n"+
        "\t"+"ReadFile read = new ReadFile();"+"\n"+
        "\t"+"File file = new File(\"input\"+\".txt\");"+"\n"+
       // "\t"+"JFileChooser chooser = new JFileChooser();"+"\n"+
       // "\t"+"int returnVal = chooser.showOpenDialog(null);"+"\n"+
        //"\t"+"if(returnVal == JFileChooser.APPROVE_OPTION) {"+"\n"+
        
          //      "\t"+"\t"+"file = chooser.getSelectedFile();"+"\n"+
        //"\t"+"}"+"\n"+
            
         "\t"+"HashMap input = read.leerArchivo(file);"+"\n"+
                
        "\t"+this.nombreArchivo+" resGenerator = new "+this.nombreArchivo+"(input);"+"\n"+
        "\t"+"resGenerator.automatas();"+"\n"+
      
        "\t"+"resGenerator.revisar();"+"\n"+
        
     
        
      
        
        "\t"+"}"+"\n"+

    "}"+"\n";
        
        ReadFile fileCreator = new ReadFile();
        fileCreator.crearArchivo(res, "resultadoGeneradorMain");
        
    }
    
    public void generarSimulacion(){
        String simulacion = "";
        simulacion += "\n"+
            " /**" + "\n"+
            "* Universidad Del Valle de Guatemala"+
            "* 07-ago-2015" + "\n"+
            "* Pablo Díaz 13203" +"\n"+
            "*/" +"\n"+



            "import java.io.File;"+"\n"+
            "import java.io.FileWriter;"+"\n"+
            "import java.io.IOException;"+"\n"+
            "import java.util.ArrayList;"+"\n"+
            "import java.util.HashSet;"+"\n"+
            "import java.util.Iterator;"+"\n"+
            "import java.util.Stack;"+"\n"+

            "/**"+"\n"+
            " * Clase para utilizar el metodo de move, e-closure y simulacion de"+"\n"+
            " * un automata"+"\n"+
            " * Incluye también un método para generar archivos DOT"+"\n"+
            " * @author Pablo"+"\n"+
            " */"+"\n"+
            "public class Simulacion {"+"\n"+

                "\t"+"private String resultado;"+"\n"+
               "\t"+"private ArrayList caracteresIgnorar = new ArrayList();"+"\n"+
                "\t"+"private Estado inicial_;"+"\n"+
               "\t"+"public Simulacion(){"+"\n"+
               "\t"+"\t"+"caracteresIgnorar.add(resultadoGeneradorMain.EPSILON);"+"\n";
              
                simulacion+="\t"+"}"+"\n"+
    
           
                
            "\t"+"public HashSet<Estado> eClosure(Estado eClosureEstado){"+"\n"+
                "\t"+"\t"+"Stack<Estado> pilaClosure = new Stack();"+"\n"+
                "\t"+"\t"+"Estado actual = eClosureEstado;"+"\n"+
                "\t"+"\t"+"actual.getTransiciones();"+"\n"+
                "\t"+"\t"+"HashSet<Estado> resultado = new HashSet();"+"\n"+

                "\t"+"\t"+"pilaClosure.push(actual);"+"\n"+
                "\t"+"\t"+"while(!pilaClosure.isEmpty()){"+"\n"+
                    "\t"+"\t"+"\t"+"actual = pilaClosure.pop();"+"\n"+

                    "\t"+"\t"+"\t"+"for (Transicion t: (ArrayList<Transicion>)actual.getTransiciones()){"+"\n"+

                        "\t"+"\t"+"\t"+"\t"+"if ((caracteresIgnorar.contains(t.getSimbolo()))&&!resultado.contains(t.getFin())){"+"\n"+
                            "\t"+"\t"+"\t"+"\t"+"\t"+"resultado.add(t.getFin());"+"\n"+
                            "\t"+"\t"+"\t"+"\t"+"\t"+"pilaClosure.push(t.getFin());"+"\n"+
                        "\t"+"\t"+"\t"+"\t"+"}"+"\n"+
                    "\t"+"\t"+"\t"+"}"+"\n"+
                "\t"+"\t"+"}"+"\n"+
                "\t"+"\t"+"resultado.add(eClosureEstado); //la operacion e-Closure debe tener el estado aplicado"+"\n"+
                "\t"+"\t"+"return resultado;"+"\n"+
            "\t"+"}"+"\n"+

            "\t"+"public HashSet<Estado> move(HashSet<Estado> estados, String simbolo){"+"\n"+

                "\t"+"\t"+"HashSet<Estado> alcanzados = new HashSet();"+"\n"+
                "\t"+"\t"+"Iterator<Estado> iterador = estados.iterator();"+"\n"+
                "\t"+"\t"+"while (iterador.hasNext()){"+"\n"+

                    "\t"+"\t"+"\t"+"for (Transicion t: (ArrayList<Transicion>)iterador.next().getTransiciones()){"+"\n"+
                        "\t"+"\t"+"\t"+"Estado siguiente = t.getFin();"+"\n"+
                        "\t"+"\t"+"\t"+"String simb = (String) t.getSimbolo();"+"\n"+
                        "\t"+"\t"+"\t"+ "if (simb.equals(simbolo)){"+"\n"+
                        "\t"+"\t"+"\t"+"\t"+"alcanzados.add(siguiente);"+"\n"+
                        "\t"+"\t"+"\t"+"}"+"\n"+

                    "\t"+"\t"+"\t"+"}"+"\n"+

                "\t"+"\t"+"}"+"\n"+
                "\t"+"\t"+"return alcanzados;"+"\n"+

            "\t"+"}"+"\n"+

            "\t"+"public Estado move(Estado estado, String simbolo){"+"\n"+
               "\t"+"\t"+ "ArrayList<Estado> alcanzados = new ArrayList();"+"\n"+

               "\t"+"\t"+ "for (Transicion t: (ArrayList<Transicion>)estado.getTransiciones()){"+"\n"+
                    "\t"+"\t"+"\t"+"Estado siguiente = t.getFin();"+"\n"+
                    "\t"+"\t"+"\t"+"String simb = (String) t.getSimbolo();"+"\n"+

                    "\t"+"\t"+"\t"+"if (simb.equals(simbolo)&&!alcanzados.contains(siguiente)){"+"\n"+
                        "\t"+"\t"+"\t"+"\t"+"alcanzados.add(siguiente);"+"\n"+
                    "\t"+"\t"+"\t"+"}"+"\n"+

                "\t"+"\t"+"}"+"\n"+

                "\t"+"\t"+"return alcanzados.get(0);"+"\n"+
            "\t"+"}"+"\n"+

            "\t"+"/**"+"\n"+
            "\t"+" * Método para simular un automata sin importar si es determinista o no deterministas"+"\n"+
            "\t"+" * "+"\n"+
            "\t"+" * @param regex recibe la cadena a simular "+"\n"+
            "\t"+" * @param automata recibe el automata a ser simulado"+"\n"+
            "\t"+" */"+"\n"+
            "\t"+"public ArrayList simular(String regex, Automata automata)"+"\n"+
            "\t"+"{"+"\n"+
                "\t"+"\t"+ " String returnString = \"\";"+"\n"+
                "\t"+"\t"+" String returnString2 = \"\";"+"\n"+
                "\t"+"\t"+" ArrayList returnArray = new ArrayList();"+"\n"+
                "\t"+"\t"+" Estado final_ = null;"+"\n"+
                "\t"+"\t"+"Estado inicial = automata.getEstadoInicial();"+"\n"+
                "\t"+"\t"+"ArrayList<Estado> estados = automata.getEstados();"+"\n"+
                "\t"+"\t"+"ArrayList<Estado> aceptacion = new ArrayList(automata.getEstadosAceptacion());"+"\n"+

                "\t"+"\t"+"HashSet<Estado> conjunto = eClosure(inicial);"+"\n"+
                "\t"+"\t"+"char last = ' ';\n" +
                "\t"+"\t"+"int currentState = 0;\n" +
                "\t"+"\t"+"int finalState = 0;\n" +
                "\t"+"\t"+"int init = 0;"+"\n"+
                "\t"+"\t"+"for (Character ch: regex.toCharArray()){"+"\n"+
                       /* "\t"+"\t"+"\t"+"if (ch == ' '){"+"\n"+
                        "\t"+"\t"+"\t"+"\t"+"currentState++;"+"\n"+
                        "\t"+"\t"+"\t"+" break;"+"\n"+
                        "\t"+"\t"+"\t"+"}"+"\n"+*/
                    "\t"+"\t"+"\t"+"conjunto = move(conjunto,ch.toString());"+"\n"+
                    "\t"+"\t"+"\t"+"HashSet<Estado> temp = new HashSet();"+"\n"+
                    "\t"+"\t"+"\t"+"Iterator<Estado> iter = conjunto.iterator();"+"\n"+

                    "\t"+"\t"+"\t"+"while (iter.hasNext()){"+"\n"+
                       "\t"+"\t"+"\t"+"\t"+"Estado siguiente = iter.next();"+"\n"+
                       "\t"+"\t"+"\t"+"\t"+"/**"+"\n"+
                       "\t"+"\t"+"\t"+"\t"+" * En esta parte es muy importante el metodo addAll"+"\n"+
                       "\t"+"\t"+"\t"+"\t"+" * porque se tiene que agregar el eClosure de todo el conjunto"+"\n"+
                       "\t"+"\t"+"\t"+"\t"+" * resultante del move y se utiliza un hashSet temporal porque"+"\n"+
                       "\t"+"\t"+"\t"+"\t"+" * no se permite la mutacion mientras se itera"+"\n"+
                       "\t"+"\t"+"\t"+"\t"+" */"+"\n"+
                        "\t"+"\t"+"\t"+"\t"+"\t"+"temp.addAll(eClosure(siguiente)); "+"\n"+

                    "\t"+"\t"+"\t"+"}"+"\n"+
                    "\t"+"\t"+"\t"+"if (conjunto.isEmpty())"+"\n"+
                    "\t"+"\t"+"\t"+"\t"+"returnString = (regex.substring(init,finalState));"+"\n"+
                    "\t"+"\t"+"\t"+" if (temp.contains(aceptacion.get(0))){"+"\n"+
                    "\t"+"\t"+"\t"+"\t"+"finalState++;"+"\n"+
                    "\t"+"\t"+"\t"+"}"+"\n"+
                    "\t"+"\t"+"\t"+"currentState++;"+"\n"+
                    "\t"+"\t"+"\t"+"conjunto=temp;"+"\n"+


                "\t"+"\t"+"}"+"\n"+
                        
                "\t"+"\t"+"if (returnString.isEmpty())"+"\n"+
                "\t"+"\t"+"\t"+"returnString = (regex.substring(init,finalState));"+"\n"+
                "\t"+"\t"+" returnString2 = regex.substring(currentState);"+"\n"+
                "\t"+"\t"+" returnArray.add(returnString);"+"\n"+
                "\t"+"\t"+"returnArray.add(returnString2);"+"\n"+
               "\t"+"\t"+ " return returnArray;"+"\n"+


               /* "\t"+"\t"+"boolean res = false;"+"\n"+

                "\t"+"\t"+"for (Estado estado_aceptacion : aceptacion){"+"\n"+
                    "\t"+"\t"+"\t"+"if (conjunto.contains(estado_aceptacion)){"+"\n"+

                        "\t"+"\t"+"\t"+"\t"+"res = true;"+"\n"+
                    "\t"+"\t"+"\t"+"}"+"\n"+
                "\t"+"\t"+"}"+"\n"+
                "\t"+"\t"+"if (res){"+"\n"+
                    //System.out.println("Aceptado");
                    //this.resultado = "Aceptado";
                    "\t"+"\t"+"\t"+"return true;"+"\n"+
                "\t"+"\t"+"}"+"\n"+
                "\t"+"\t"+"else{"+"\n"+
                    //System.out.println("NO Aceptado");
                    // this.resultado = "No Aceptado";
                    "\t"+"\t"+"\t"+"return false;"+"\n"+
                "\t"+"\t"+"}"+"\n"+
            "\t"+"}"+"\n"+

            "\t"+"public String getResultado() {"+"\n"+
                    "\t"+"\t"+"return resultado;"+"\n"+
                "\t"+"\t"+"}"+"\n"+*/
            "\t"+"}"+"\n"+"\t"+"}";



        ReadFile fileCreator = new ReadFile();
        fileCreator.crearArchivo(simulacion, "Simulacion");
        
        
    }
    
    public void generarClaseToken(){
        String token =""+
      
        "/**"+"\n"+
        "* Universidad Del Valle de Guatemala"+"\n"+
        "* 05-oct-2015"+"\n"+
        "* Pablo Díaz 13203"+"\n"+
        "*/"+"\n"+

        
        "import java.util.ArrayList;"+"\n"+
        "import java.util.HashSet;"+"\n"+
        "import java.util.Objects;"+"\n"+
        "import java.util.TreeMap;"+"\n"+

        "/**"+"\n"+
        " *"+"\n"+
        " * @author Pablo"+"\n"+
        " * @param <T>"+"\n"+
        " */"+"\n"+
        "public class Token<T> {"+"\n"+

            "\t"+"private T id;"+"\n"+
            "\t"+"private T lexema;"+"\n"+
            "\t"+"private ArrayList keywords = new ArrayList();"+"\n"+
            "\t"+"private HashSet<Token> tokens = new HashSet();"+"\n"+
            "\t"+"private TreeMap<String,String> keyMap = new TreeMap();"+"\n"+

            "\t"+"public Token(T id, T lexema,boolean revisarKey) {"+"\n"+
                "\t"+"\t"+"if (revisarKey)"+"\n"+
                "\t"+"\t"+"\t"+"keyWords();"+"\n"+
                "\t"+"\t"+"ArrayList var = revisarKeywords(id,lexema);"+"\n"+
                "\t"+"\t"+"this.id = (T) var.get(0);"+"\n"+
                "\t"+"\t"+"this.lexema = (T) var.get(1);"+"\n"+
            "\t"+"}"+"\n"+

            "\t"+"public T getId() {"+"\n"+
                "\t"+"\t"+"return id;"+"\n"+
            "\t"+"}"+"\n"+

            "\t"+"public void setId(T id) {"+"\n"+
                "\t"+"\t"+"this.id = id;"+"\n"+
            "\t"+"}"+"\n"+

            "\t"+"public T getLexema() {"+"\n"+
                "\t"+"\t"+"return lexema;"+"\n"+
            "\t"+"}"+"\n"+

            "\t"+"public void setLexema(T lexema) {"+"\n"+
                "\t"+"\t"+"this.lexema = lexema;"+"\n"+
            "\t"+"}"+"\n"+

            "\t"+"public ArrayList revisarKeywords(T id, T lexema){"+"\n"+
                "\t"+"\t"+"ArrayList returnArray = new ArrayList();"+"\n"+

                "\t"+"\t"+"if (keyMap.containsKey((String)lexema)){"+"\n"+
                    "\t"+"\t"+"\t"+"returnArray.add(keyMap.get((String)lexema));"+"\n"+
                    "\t"+"\t"+"\t"+"returnArray.add(lexema);"+"\n"+
                    "\t"+"\t"+"\t"+"return returnArray;"+"\n"+
                "\t"+"\t"+"}"+"\n"+

                "\t"+"\t"+"returnArray.add(id);"+"\n"+
                "\t"+"\t"+"returnArray.add(lexema);"+"\n"+
                "\t"+"\t"+"return returnArray;"+"\n"+
            "\t"+"}"+"\n"+

           "\t"+ "public void keyWords(){"+"\n";
                for (Map.Entry<String, String> entry : keyMap.entrySet()) {
                     token+= "\t"+"\t"+"keyMap.put(\""+entry.getValue()+"\",\""+entry.getKey()+"\");"+"\n";
               
            }
            token+="\t"+"}"+"\n";
            token+="\t"+"@Override"+"\n"+
            "\t"+"public String toString() {"+"\n"+
                //  "\t"+"\t"+"return \"<\" +id +\">\";"+"\n"+
                "\t"+"\t"+"return \"<\" + id + \", \\\"\" + lexema + \"\\\">\";"+"\n"+
           "\t"+ "}"+"\n"+

            "\t"+"@Override"+"\n"+
            "\t"+"public int hashCode() {"+"\n"+
                "\t"+"\t"+"int hash = 3;"+"\n"+
                "return hash;"+"\n"+
            "\t"+"}"+"\n"+

            "\t"+"@Override"+"\n"+
            "\t"+"public boolean equals(Object obj) {"+"\n"+
                "\t"+"\t"+"if (obj == null) {"+"\n"+
                   "\t"+"\t"+"\t"+ "return false;"+"\n"+
                "\t"+"\t"+"}"+"\n"+
                "\t"+"\t"+"if (getClass() != obj.getClass()) {"+"\n"+
                    "\t"+"\t"+"\t"+"return false;"+"\n"+
                "\t"+"\t"+"}"+"\n"+
                "\t"+"\t"+"final Token<?> other = (Token<?>) obj;"+"\n"+
                "\t"+"\t"+"if (!Objects.equals(this.id, other.id)) {"+"\n"+
                    "\t"+"\t"+"\t"+"return false;"+"\n"+
                "\t"+"\t"+"}"+"\n"+
                "\t"+"\t"+"if (!Objects.equals(this.lexema, other.lexema)) {"+"\n"+
                    "\t"+"\t"+"\t"+"return false;"+"\n"+
                "\t"+"\t"+"}"+"\n"+
                "\t"+"\t"+"\t"+"return false;"+"\n"+
            "\t"+"}"+"\n"+



        "}"+"\n";
        ReadFile fileCreator = new ReadFile();
        fileCreator.crearArchivo(token, "Token");
            
            
    }
    
}
