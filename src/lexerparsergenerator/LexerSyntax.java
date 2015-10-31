/**
* Universidad Del Valle de Guatemala
* 11-sep-2015
* Pablo Díaz 13203
*/

package lexerparsergenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;
import java.util.TreeSet;

/**
 * Clase para revisar la estructura de un archivo de CocoL
 * @author Pablo
 */
public class LexerSyntax implements RegexConstants{
    
    
    private final HashMap<Integer,String> cadena;
    private final String espacio = charAbrirParentesis  +" "+charCerrarParentesis+charKleene;
    private final String ANY = this.espacio+"[ -.]"+charOr+"'"+charOr+"[@-z]"+this.espacio;
    private boolean output = true;
    
    private Automata letter_;
    private Automata digit_;
    private Automata ident_;
    private Automata string_;
    private Automata character_;
    private Automata number_;
    private Automata basicSet_;
    private Automata igual_;
    private Automata plusOrMinus_;
    private Automata espacio_;
    private Automata basicChar_;
    
    private Automata compiler_;
    private Automata end_;
    private final Simulacion sim;
    private final Stack compare = new Stack();
    private boolean union = false;
    private int indexAutomata=-1;
    private HashSet returnGlobal = new HashSet();
    private ArrayList<ArrayList<Produccion>> arrayGlobal = new ArrayList();
    private ArrayList<Automata> generador = new ArrayList();
    private ArrayList<Produccion> producciones = new ArrayList();
    
    
 
    public LexerSyntax(HashMap cadena){
        this.sim = new Simulacion();
        this.cadena=cadena;
    }
    
    /**
     * Método para definir los mini autómatas para comparar
     * las expresiones regulares
     */
    public void vocabulario(){
        RegexConverter convert = new RegexConverter();
        
        String regex = convert.infixToPostfix(ANY);
        AFNConstruct ThomsonAlgorithim = new AFNConstruct(regex);
        ThomsonAlgorithim.construct();
        letter_ = ThomsonAlgorithim.getAfn();
       
        
       /* regex = convert.infixToPostfix(espacio+"[A-Z]"+espacio);
        ThomsonAlgorithim = new AFNConstruct(regex);
        ThomsonAlgorithim.construct();
        Automata letterMayuscula_ = ThomsonAlgorithim.getAfn();
        letter_ =  ThomsonAlgorithim.union(letter_, letterMayuscula_);*/
       
       
        //letter_ = ThomsonAlgorithim.concatenacion(letter_, espacio_);
        //letter_ = ThomsonAlgorithim.concatenacion(espacio_, letter_);
        letter_.setTipo("Letra");
       
        
        regex = convert.infixToPostfix(charAbrirParentesis+" "+
                charCerrarParentesis+charKleene);
        ThomsonAlgorithim.setRegex(regex);
        ThomsonAlgorithim.construct();
        espacio_  = ThomsonAlgorithim.getAfn();
        espacio_.setTipo("espacio");
        
        //System.out.println(letter_);
        regex = convert.infixToPostfix(espacio+"[0-9]"+espacio);
        ThomsonAlgorithim.setRegex(regex);
        ThomsonAlgorithim.construct();
        digit_ = ThomsonAlgorithim.getAfn();
       // digit_ = ThomsonAlgorithim.concatenacion(digit_, espacio_);
        //digit_ = ThomsonAlgorithim.concatenacion(espacio_, digit_);
        digit_.setTipo("digit");
        
        
       
        Automata digitKleene = ThomsonAlgorithim.cerraduraKleene(digit_);
        //System.out.println(numberKleene);
        number_ = ThomsonAlgorithim.concatenacion(digit_, digitKleene);
        number_.setTipo("número");
        Automata letterOrDigit = ThomsonAlgorithim.union(letter_, digit_);
        //System.out.println(letterOrDigit);
        Automata letterOrDigitKleene = ThomsonAlgorithim.cerraduraKleene(letterOrDigit);
       // System.out.println(letterOrDigitKleene);
        ident_ = ThomsonAlgorithim.concatenacion(letterOrDigitKleene, letter_);
        ident_.setTipo("identificador");
        
       
       // System.out.println(ident_);
        Automata ap1 = ThomsonAlgorithim.afnSimple("\"");
        Automata ap2 = ThomsonAlgorithim.afnSimple("\"");
        Automata stringKleene = ThomsonAlgorithim.union(number_, letter_);
        string_ = ThomsonAlgorithim.cerraduraKleene(stringKleene);
        string_ = ThomsonAlgorithim.concatenacion(ap1, string_);
        string_ = ThomsonAlgorithim.concatenacion(string_,ap2);
        
        regex = convert.infixToPostfix("\\"+charOr+"\""+charOr+"\'");
        ThomsonAlgorithim = new AFNConstruct(regex);
        ThomsonAlgorithim.construct();
        Automata specialChars = ThomsonAlgorithim.getAfn();
        string_ = ThomsonAlgorithim.union(string_, specialChars);
        
        
        string_.setTipo("string");
      
         
        
        
        Automata apch1 = ThomsonAlgorithim.afnSimple("\'");
        Automata apch2 = ThomsonAlgorithim.afnSimple("\'");
        character_ = ThomsonAlgorithim.union(number_, letter_);
        character_ = ThomsonAlgorithim.concatenacion(apch1, character_);
        character_ = ThomsonAlgorithim.concatenacion(character_,apch2);
         
        regex = convert.infixToPostfix("CHR(");
        ThomsonAlgorithim = new AFNConstruct(regex);
        ThomsonAlgorithim.construct();
        Automata leftChar = ThomsonAlgorithim.getAfn();
        
       
        Automata rigthChar = ThomsonAlgorithim.afnSimple(")");
        leftChar = ThomsonAlgorithim.concatenacion(number_, leftChar);
       
        Automata innerChar = ThomsonAlgorithim.concatenacion(rigthChar, leftChar);
       
        character_ = ThomsonAlgorithim.union(character_,innerChar);
        character_.setTipo("character");
      
       
        
       
        
        
        Automata pointChar = ThomsonAlgorithim.afnSimple(".");
        Automata pointChar2 = ThomsonAlgorithim.afnSimple(".");
        pointChar = ThomsonAlgorithim.concatenacion(pointChar, pointChar2);
        pointChar = ThomsonAlgorithim.concatenacion(pointChar, espacio_);
        pointChar = ThomsonAlgorithim.concatenacion(espacio_, pointChar);
        basicChar_ = ThomsonAlgorithim.concatenacion(character_, pointChar);
        basicChar_ = ThomsonAlgorithim.concatenacion(basicChar_,character_);
        basicChar_.setTipo("Basic Char");
        
        basicSet_ = ThomsonAlgorithim.union(string_, ident_);
        basicSet_ = ThomsonAlgorithim.union(basicSet_, basicChar_);
        basicSet_.setTipo("Basic Set");
        
        regex = convert.infixToPostfix(espacio+"="+espacio);
        ThomsonAlgorithim.setRegex(regex);
        ThomsonAlgorithim.construct();
        igual_  = ThomsonAlgorithim.getAfn();
        igual_.setTipo("=");
      
        
        Automata plus = ThomsonAlgorithim.afnSimple("+");
        Automata minus = ThomsonAlgorithim.afnSimple("-");
        plusOrMinus_ = ThomsonAlgorithim.union(plus, minus);
        plusOrMinus_.setTipo("(+|-)");
        
       
       
        regex = convert.infixToPostfix("COMPILER");
        ThomsonAlgorithim.setRegex(regex);
        ThomsonAlgorithim.construct();
        compiler_ = ThomsonAlgorithim.getAfn();
        compiler_.setTipo("\"COMPILER\"");
        regex = convert.infixToPostfix("END");
        ThomsonAlgorithim.setRegex(regex);
        ThomsonAlgorithim.construct();
        end_ = ThomsonAlgorithim.getAfn();
        end_.setTipo("\"END\"");
       
    
          
        
      

    }
    
    /**
    * Revisar segundo archivo de input
    * puede ser todo en una línea o diferentes líneas
    * @param cadena 
    */
    public  void check(HashMap<Integer,String> cadena){
       
       for (Map.Entry<Integer, String> entry : cadena.entrySet()) {
        Integer key = entry.getKey();
        String value = entry.getValue();
        String[] parts = value.split(" ");
        for (int i= 0;i<parts.length;i++){
            this.checkIndividualAutomata(parts[i], generador,key);
        }
        
    
}
       
   }
   
    /**
     * Revisar formato de la primera línea
     * ScannerSpecification
     * ParserSpecification
     * @param cadena HashMap de la cadena y sus números de línea
     */
    public void construct(HashMap<Integer,String> cadena){
        int lineaActual = 1;
        int index = 0;
       // ArrayList res = checkExpression("Cocol = \"COMPILER\""+this.espacio,lineaActual,index);
      
        //ArrayList res_1 = checkAutomata(this.compiler_,lineaActual,index);
        
        //int index2  = returnArray(res_1);
        if (!this.cadena.get(lineaActual).contains("COMPILER"))
            System.out.println("No tiene la palabra COMPILER");
        else
            index = this.cadena.get(lineaActual).indexOf("R")+1;
        
        ArrayList res2 = checkAutomata(this.ident_,lineaActual,index);
        //System.out.println(res2.get(1));
        
        //ScannerSpecification
        
        ArrayList scan = scannerSpecification(lineaActual);
        if (scan.isEmpty())
            output=false;
        
        //ParserSpecification
     
       ArrayList parser = parserSpecification((int)scan.get(0));
       
        //END File
        lineaActual = (int)parser.get(0);
       int index4 = 0;
       if (!this.cadena.get(lineaActual).contains("END"))
            System.out.println("No tiene la palabra END");
        else
            index4 = this.cadena.get(lineaActual).indexOf("D")+1;
        
        
        ArrayList res4 = checkAutomata(this.ident_,lineaActual,index4);
      
        //revisar identificadores
        if (!res4.isEmpty()&&!res2.isEmpty()){
            if (!res4.get(1).toString().trim().equals(res2.get(1).toString().trim())){
                LexerParserGenerator.errores.SynErr(lineaActual, "Los identificadores no coinciden");
                System.out.println( res4.get(1).toString().trim()+ " y "+ res2.get(1).toString().trim() +" no coinciden");
                output=false;
            }
         
        }
        
        
    }
    /**
     * Método que revisa el parserSpecification
     * @param lineaActual
     * @return Array[0] = lineaActual, Arrau[1] = true/false
     */
    public ArrayList parserSpecification(int lineaActual){
        while (true){
          boolean res2 = production(lineaActual);
          if (!res2){
            break;
            
          }
          lineaActual = avanzarLinea(lineaActual);
       }
        ArrayList returnArray = new ArrayList();
        returnArray.add(lineaActual);
        returnArray.add(true);
        return returnArray;
         
    }
    /**
     * Método que revisar producciones individuales
     * @param lineaActual
     * @return true si fue aceptado / false si no fue aceptada la sintaxis
     */
    public boolean production(int lineaActual){
        if (this.cadena.get(lineaActual).contains("END")||this.cadena.get(lineaActual).contains("IGNORE"))
            return false;
        String cadenaActual = this.cadena.get(lineaActual);
          //revisr identificador y símbolo '='
        try{
            String antesIgual = cadenaActual.substring(0, cadenaActual.indexOf("=")-1);
           
            int countOpen = this.count(antesIgual, '<');
            int countClose = this.count(antesIgual, '>');
            int openCount = this.count(antesIgual, '(');
            int closeClount = this.count(antesIgual, ')');
            
            if (countOpen > 1 || countClose > 1|| openCount > 1|| closeClount > 1){
                LexerParserGenerator.errores.SynErr(lineaActual, "Hay un error de <> o ()");
                return false;
            }
            
            int indexSearch = cadenaActual.indexOf("=")-1;
            while (cadenaActual.substring(0, indexSearch).endsWith(" "))
                indexSearch--;
            
            int indexAttribute = cadenaActual.substring(0, indexSearch).indexOf("<.");
            int indexSemAction = cadenaActual.substring(0, indexSearch).indexOf("(.");
            
            if (indexAttribute > indexSemAction && indexSemAction != -1)
                return false;
            
            if (indexAttribute != -1)
                indexSearch=indexAttribute;
            
            if (indexAttribute == -1 && indexSemAction != -1)
                indexSearch= indexSemAction;
            
            while (cadenaActual.substring(0, indexSearch).endsWith(" "))
                indexSearch--;
            
            boolean identifier = checkAutomata(this.ident_,cadenaActual.substring(0,indexSearch));
            ///  int index1  = returnArray(identifier);
              if (!identifier){
                   return false;
              }
            
            
                         
        }catch(Exception e){
            LexerParserGenerator.errores.SynErr(lineaActual, "falta un =");
            return false;
        }
        
        int indexSearch = cadenaActual.indexOf("=")+1;
        
        boolean expr = expression(lineaActual, cadenaActual.substring(indexSearch));
        
        if (!this.cadena.get(lineaActual).contains("|")){
            producciones.add(new Produccion(cadenaActual.substring(0,indexSearch-2),
                    cadenaActual.substring(indexSearch,
                    cadenaActual.indexOf(".")-1)
            ));
        }
        else{
            Stack<String> pilaOR = new Stack();
            int cantOr = count(cadenaActual,'|');
            String cadenaRevisar =  cadenaActual.substring(indexSearch,cadenaActual.indexOf(".")-1);
            pilaOR.push(cadenaRevisar);
            while (!pilaOR.isEmpty()){
                cadenaRevisar = pilaOR.pop();
                if ( cadenaRevisar.contains("|")){
                    producciones.add(new Produccion(cadenaActual.substring(0,indexSearch-2),
                            cadenaRevisar.substring(0,cadenaRevisar.indexOf("|"))
                    ));
                    //System.out.println(cadenaRevisar);
                    if (!cadenaRevisar.isEmpty())
                        pilaOR.push(cadenaRevisar.substring(cadenaRevisar.indexOf("|")+1));
                }
                else{
                producciones.add(new Produccion(cadenaActual.substring(0,indexSearch-2),
                            cadenaRevisar
                ));
                }
               
                
            }
            
        }
        
        return expr;
    }
    
    public void inputCal(){
        for (int i = 0;i<producciones.size();i++){
            System.out.println(producciones.get(i));
        } 
       
        System.out.println("1. First");
        System.out.println("2. Follow");
        Scanner keyboard = new Scanner(System.in);
        System.out.println(">>");
        int input = 0;
        try {
            input = keyboard.nextInt();
        }catch(Exception e){
            System.out.println("Debe ingresar un número");
        }
        
      
       
            if (input==1){
                try
                {   
                    System.out.println("Ingrese cadena de símbolos separada por espacios");
                    String inputS = keyboard.next();
                    
                    if (revisarSimboloGramatica(inputS))
                        System.out.println(first(inputS));
                    else
                        System.out.println("El símblo ingresado no pertenece a la gramática");
                        

                } catch(StackOverflowError e){
                    System.out.println("Error: Gramática recursiva");
                }
            }
            if (input ==2){
                System.out.println("Ingrese un símbolo no-terminal");
                String inputS = keyboard.next();
                if (revisarSimboloGramatica(inputS))
                    System.out.println(follow(inputS));
                else
                    System.out.println("El símbolo ingresado no pertenece a la gramática");
            }
        
   
       
    }
    
    public boolean terminal(String cadena){
        return searchProductions(cadena).isEmpty();
    }
   
    
    public TreeSet first(String input){
        input = input.trim();
        TreeSet returnArray = new TreeSet();
        if (specificProduction(input,EPSILON)){
            returnArray.add(EPSILON);
        }
        if (terminal(input)){
          returnArray.add(input);
          return returnArray;
        }
       
        if (!terminal(input)){
            ArrayList<Produccion> prods = searchProductions(input);
            for (int i = 0;i<prods.size();i++){
                String[] parts = prods.get(i).getCuerpo().split(" ");
                for (int j=0;j<parts.length;j++){
                    String part = parts[j];
                    returnArray.addAll(first(part));
                    if (!returnArray.isEmpty())
                        break;
                }
            }
            
        }
        
        return returnArray;    
    }
    public HashSet follow(String input){
        HashSet returnArray = new HashSet();
        if (input.equals(simboloInicial().getCabeza()))
           returnArray.add("$");
       
        ArrayList<Produccion> productions = searchProductionCuerpo(input);
       
        for (int k = 0;k<arrayGlobal.size();k++){
          
           if (arrayGlobal.get(k).equals(productions))
               return new HashSet();
        }
       
         
        arrayGlobal.add(productions);
        for (int i = 0; i <productions.size();i++){
            String cuerpo = productions.get(i).getCuerpo();
            String beta = cuerpo.substring(cuerpo.indexOf(input)+input.length());
            TreeSet first = first(beta);
            
            
            
            if (!beta.isEmpty()){
                first.remove(EPSILON);
                returnArray.addAll(first);
            }
            if (beta.isEmpty() || first(beta).contains(EPSILON))
                returnArray.addAll(follow(productions.get(i).getCabeza()));
            
           
            
        }
       
       return returnArray;
    }
    public boolean specificProduction(String cabeza, String cuerpo){
        for (int i = 0;i<producciones.size();i++){
            if (producciones.get(i).getCabeza().equals(cabeza)&&producciones.get(i).getCuerpo().equals(cuerpo))
                return true;
        }
        return false;
    }
    
    public boolean revisarSimboloGramatica(String input){
        for (int i = 0; i< producciones.size();i++){
            if (producciones.get(i).getCabeza().contains(input)||
                producciones.get(i).getCuerpo().contains(input))
                return true;
        }
        return false;
    }
    
    public Produccion simboloInicial(){
       return producciones.get(0);
    }
    
    /**
     * devuleve un array con las producciones que tengan al input como cabeza
     * @param cabeza
     * @return 
     */
    public ArrayList<Produccion> searchProductions(String cabeza){
        ArrayList<Produccion> prod = new ArrayList();
        for (int i = 0 ;i<producciones.size();i++){
            if (producciones.get(i).getCabeza().equals(cabeza))
                prod.add(producciones.get(i));
        }
        return prod;
    }
    
    /**
     * devuleve un array con las producciones que tengan al input como cuerpo.
     * @param cuerpo
     * @return 
     */
    public ArrayList<Produccion> searchProductionCuerpo(String cuerpo){
        ArrayList<Produccion> prod = new ArrayList();
        for (int i = 0 ;i<producciones.size();i++){
            String[] parts = producciones.get(i).getCuerpo().split(" ");
            for (int j=0;j<parts.length;j++){
               if (parts[j].equals(cuerpo))
                prod.add(producciones.get(i));
            }
           
        }
        return prod;
    }
    
    
     public boolean expression(int lineaActual,String cadenaRevisar){
        String antesRevisar = cadenaRevisar;
        int countOpen = this.count(antesRevisar, '<');
        int countClose = this.count(antesRevisar, '>');
        int openCount = this.count(antesRevisar, '(');
        int closeClount = this.count(antesRevisar, ')');

            if (countOpen !=  countClose || openCount != closeClount){
                LexerParserGenerator.errores.SynErr(lineaActual, "Están desbalanceados los <> o los ()");
                return false;
            }
        
        
        
        cadenaRevisar = cadenaRevisar.replaceAll("\\{", charAbrirParentesis+"");
        cadenaRevisar = cadenaRevisar.replaceAll("\\}", charCerrarParentesis+""+charKleene);
        cadenaRevisar = cadenaRevisar.replaceAll("\\[", charAbrirParentesis+"");
        cadenaRevisar = cadenaRevisar.replaceAll("\\]",charCerrarParentesis+"" +charInt);
        cadenaRevisar = cadenaRevisar.replaceAll("\\|",charOr+"");
        cadenaRevisar = cadenaRevisar.replaceAll("\\(",charAbrirParentesis+"");
        cadenaRevisar = cadenaRevisar.replaceAll("\\)",charCerrarParentesis+"");
        
       
        String regex;
        
        RegexConverter convert = new RegexConverter();
        regex = convert.infixToPostfix(cadenaRevisar);
       
        if (regex.isEmpty()){
            LexerParserGenerator.errores.SynErr(lineaActual, "Expresión mal ingresada"+"\n" + antesRevisar);
            return false;
        }  
        
      
         
     return true;   
    }
    
    /**
     * Método para revisar el ScannerSpecification
     * @param lineaActual línea actual del archivo leído
     * @return ArrayList [0] = index, [1] = cadena. 
     *   ["CHARACTERS" {SetDecl}]
     *   ["KEYWORDS"   {KeyWordDecl}]
     *   ["TOKENS"     {TokenDecl}]
     *   {WhiteSpaceDecl}.
     */
    public ArrayList<String> scannerSpecification(int lineaActual)
    {
        int returnIndex=0;
        String returnString= "";
        
        //[CHARACTERS]
        lineaActual = avanzarLinea(lineaActual);
       
        if (!this.cadena.get(lineaActual).contains("CHARACTERS")){
            LexerParserGenerator.errores.SynErr(lineaActual, "No contiene la palabra CHARACTERS");
            return new ArrayList();
        }
        lineaActual = avanzarLinea(lineaActual);
         //["Characters" = {SetDecl}
        while (true){
            boolean res2 = setDeclaration(lineaActual);
            if (!res2){
                lineaActual = retrocederLinea(lineaActual);
                break;
                
            }
            lineaActual = avanzarLinea(lineaActual);
        }
        //[KEYWRORDS]
        lineaActual = avanzarLinea(lineaActual);
         if (!this.cadena.get(lineaActual).contains("KEYWORDS")){
            LexerParserGenerator.errores.SynErr(lineaActual, " No contiene la palabra KEYWORDS");
            return new ArrayList();
        }
        lineaActual = avanzarLinea(lineaActual);
        //[KEYWORDS = {KeyWordDeclaration}]
        while (true){
            boolean keywords = keywordDeclaration(lineaActual);
            if (!keywords){
                lineaActual = retrocederLinea(lineaActual);
                break;
            }
            lineaActual = avanzarLinea(lineaActual);
            
        }
        
         lineaActual = avanzarLinea(lineaActual);
         if (!this.cadena.get(lineaActual).contains("TOKENS")){
            LexerParserGenerator.errores.SynErr(lineaActual, "NO Contiene la palabra TOKENS");
            return new ArrayList();
        }
        
        //[TOKEN DECLARATION]
        
        lineaActual = avanzarLinea(lineaActual);
        while(true){
            boolean token = tokenDeclaration(lineaActual);
            if (token)
                lineaActual = avanzarLinea(lineaActual);
            else
                break;
        }
        
        
        //[WHITESPACE DELCARATION]
        if (cadena.get(lineaActual).contains("IGNORE")){
            //lineaActual = avanzarLinea(lineaActual);
            //whitespaceDecl
            while (true){
            boolean space = whiteSpaceDeclaration(lineaActual);
                if (space)
                    lineaActual = avanzarLinea(lineaActual);
                else{
                    break;
                }
            }
        }
        
        
        
        
       lineaActual = avanzarLinea(lineaActual);
              
        ArrayList outputScan = new ArrayList();
        outputScan.add(lineaActual);
        outputScan.add(true);
       return outputScan;
    }
    
    /**
     * Método que revisar la sintaxis de token declaration
     * @param lineaActual
     * @return 
     */
    public boolean tokenDeclaration(int lineaActual){
        
        if (this.cadena.get(lineaActual).contains("END")||this.cadena.get(lineaActual).contains("IGNORE")||
                this.cadena.get(lineaActual).contains("PRODUCTIONS"))
            return false;
        
          //revisr identificador y símbolo '='
        try{
            int indexSearch = this.cadena.get(lineaActual).indexOf("=")-1;
             while (this.cadena.get(lineaActual).substring(0, indexSearch).contains(" "))
                indexSearch--;
           
            boolean identifier = checkAutomata(this.ident_,this.cadena.get(lineaActual).substring(0,indexSearch));
            ///  int index1  = returnArray(identifier);
              if (!identifier){
                   return false;
              }
           
        }catch(Exception e){}
        
          //revisar string
        try{
            int indexSearch = this.cadena.get(lineaActual).indexOf("=")+1;
            String cadenaRevisar = this.cadena.get(lineaActual).substring(indexSearch);
            while(cadenaRevisar.startsWith(" "))
                cadenaRevisar = this.cadena.get(lineaActual).substring(++indexSearch);
            if (cadenaRevisar.substring(0, cadenaRevisar.length()).contains("."))
                cadenaRevisar = cadenaRevisar.substring(0, cadenaRevisar.length()-1);
            else{    
               LexerParserGenerator.errores.Warning(lineaActual, "NO contiene punto al final");
                
            }
           
            if (cadenaRevisar.contains("EXCEPT"))
                cadenaRevisar = cadenaRevisar.substring(0,cadenaRevisar.indexOf("EXCEPT")).trim();
            boolean tkExpr = tokenExpr(lineaActual, cadenaRevisar);
            
            return tkExpr;
         }catch(Exception e){
              LexerParserGenerator.errores.SemErr(lineaActual, this.cadena.get(lineaActual));
             this.output=false;
         }
        return false;
    }
    
    /**
     * Se verifica la sintaxis de los tokens mediantes expresiones regulares
     * Se modifican los simbolos ingresados a los símbolos de los 
     * autómatas
     * @param lineaActual
     * @param cadenaRevisar
     * @return true si no da errores, false si lo tiene error de sintaxis.
     */
    public boolean tokenExpr(int lineaActual,String cadenaRevisar){
        String antesRevisar = cadenaRevisar;
        cadenaRevisar = cadenaRevisar.replaceAll("\\{", charAbrirParentesis+"");
        cadenaRevisar = cadenaRevisar.replaceAll("\\}", charCerrarParentesis+""+charKleene);
        cadenaRevisar = cadenaRevisar.replaceAll("\\[", charAbrirParentesis+"");
        cadenaRevisar = cadenaRevisar.replaceAll("\\]",charCerrarParentesis+"" +charInt);
        cadenaRevisar = cadenaRevisar.replaceAll("\\|",charOr+"");
        cadenaRevisar = cadenaRevisar.replaceAll("\\(",charAbrirParentesis+"");
        cadenaRevisar = cadenaRevisar.replaceAll("\\)",charCerrarParentesis+"");
        
       
        String regex;
        
        RegexConverter convert = new RegexConverter();
        regex = convert.infixToPostfix(cadenaRevisar);
       
        if (regex.isEmpty()){
            LexerParserGenerator.errores.SynErr(lineaActual, "Expresión mal ingresada"+"\n" + antesRevisar);
            return false;
        }    
         
     return true;   
    }
    
    /**
     *  TokenTerm { '|' TokenTerm }.
     * @param lineaActual
     * @param cadenaRevisar
     * @return 
     */
    public boolean tokenExpr2(int lineaActual, String cadenaRevisar){
        boolean tknExpr = false;
        String subcadena = cadenaRevisar;
        String subcadenaRevisar ="";
        int lastIndex = cadenaRevisar.length();
        if (cadenaRevisar.contains("EXCEPT")){
            lastIndex = cadenaRevisar.indexOf("EXCEPT")-1;
        }
        String subCadena = cadenaRevisar;
       while (subCadena.contains("{")){
            subCadena= subCadena.substring(subCadena.indexOf("{")+1, subCadena.indexOf("}"));
            System.out.println(subCadena.contains("|"));
       }
        ArrayList reslt=null;
         boolean firstResult=false;
        if (!subcadena.contains("|")){
            reslt = tokenTerm(lineaActual, cadenaRevisar);
            firstResult = (boolean)reslt.get(0);
            cadenaRevisar =  (String)reslt.get(1);
        }
       
        if (firstResult)
             return true;
        
           
        ArrayList<String> parts = new ArrayList();
        for (int j = 0;j<cadenaRevisar.length();j++){
            subCadena = cadenaRevisar;
            while (subCadena.contains("[")){
                subCadena= subCadena.substring(subCadena.indexOf("[")+1, subCadena.indexOf("]"));
                System.out.println(subCadena.contains("|"));
                parts.add(subcadena);
                 
            }
            
        }
        
        
         for (String part : parts) {
             //System.out.println(parts[i]);
             tknExpr = (boolean)tokenTerm(lineaActual, part.trim()).get(0);
             if (!tknExpr)
                 return false;
         }
          
            
        
        
         return true;
    }
    
    public ArrayList tokenTerm(int lineaActual, String cadenaRevisar){
        int indexCortar = 0;
        int indexActual=0;
       
        String sub ="";
        Stack pilaTokens = new Stack();
        pilaTokens.push(cadenaRevisar);
        while(!pilaTokens.isEmpty()){
             int indexFin = 0;
            String actual = (String)pilaTokens.pop();
            if (actual.isEmpty())
                break;
            if (actual.contains("{"))
                indexCortar = actual.indexOf("{");
            
            else if (actual.contains("\"")){
                indexCortar = actual.indexOf("\"");
            }
            else if (actual.contains("\'")){
                indexCortar = actual.indexOf("\'");
            }
            else if (actual.contains("(")){
                indexCortar = actual.indexOf("(");
            }
            else if (actual.contains("[")){
                indexCortar = actual.indexOf("[");
            }
            
            if (indexCortar != 0){
                pilaTokens.push((actual.substring(indexCortar)));
                actual = actual.substring(0,indexCortar);
                
            }
            if (indexCortar == 0&&actual.contains("}")){
                indexFin = actual.lastIndexOf("}");
                
            }
            else if (indexCortar == 0&&actual.contains("\"")){
                indexFin = actual.lastIndexOf("\"");
                
            }
            else if (indexCortar == 0&&actual.contains(")")){
                indexFin = actual.lastIndexOf(")");
                
            }
            else if (indexCortar == 0&&actual.contains("]")){
                indexFin = actual.lastIndexOf("]");
                
            }
           
            
            
            if (indexFin != 0 && indexCortar ==0){
                pilaTokens.push(actual.substring(indexFin+1));
                actual = actual.substring(indexCortar,indexFin+1);
            }
            
            
            
            boolean res = tokenFactor(lineaActual,actual);
            System.out.println(res);
            if (!res){
                ArrayList a= new ArrayList();
                a.add(false);
                a.add(cadenaRevisar);
                return a;
                
            }
                
                
                   
        }
        ArrayList a= new ArrayList();
        a.add(true);
        a.add("");
       return  a;
        
    }
    
    public boolean tokenFactor(int lineaActual, String cadenaRevisar){
       boolean symb = symbol(lineaActual, cadenaRevisar.trim());
       
       if (symb)
           return true;
       
        int index1 = cadenaRevisar.indexOf("{");
        int index2 = cadenaRevisar.indexOf("[");
        int index3 = cadenaRevisar.indexOf("(");
                
       
       if (index1>index2&&index1>index3){
           cadenaRevisar = cadenaRevisar.substring(cadenaRevisar.indexOf("{"));
           cadenaRevisar = cadenaRevisar.replaceFirst("\\{","");
           cadenaRevisar = replaceLast(cadenaRevisar,"}","");
           boolean tkFactor1 = tokenExpr(lineaActual,cadenaRevisar);
           
            if (tkFactor1)
                return tkFactor1;
       }
       if (index2>index1&&index2>index3){
           cadenaRevisar = cadenaRevisar.substring(cadenaRevisar.indexOf("{"));
           cadenaRevisar = cadenaRevisar.replaceFirst("\\[","");
           cadenaRevisar = replaceLast(cadenaRevisar,"]","");
           boolean tkFactor1 = tokenExpr(lineaActual,cadenaRevisar);
           
            if (tkFactor1)
                return tkFactor1;
       }
       if (index3>index1&&index3>index2){
           cadenaRevisar = cadenaRevisar.substring(cadenaRevisar.indexOf("{"));
           cadenaRevisar = cadenaRevisar.replaceFirst("\\(","");
           cadenaRevisar = replaceLast(cadenaRevisar,")","");
           boolean tkFactor1 = tokenExpr(lineaActual,cadenaRevisar);
           
            if (tkFactor1)
                return tkFactor1;
       }
           
      
        return false;
    }
    
    public boolean symbol(int lineaActual, String cadenaRevisar){
        boolean idnt = checkAutomata(ident_,cadenaRevisar);
        boolean str = checkAutomata(string_,cadenaRevisar);
        boolean chr = checkAutomata(character_,cadenaRevisar);
        
         if (idnt||str||chr)
            return true;
         
        
        
        return false;
    }
    
    /**
     * Método que representa el whitespace declaration
     * @param lineaActual
     * @return 
     */
    public boolean whiteSpaceDeclaration(int lineaActual){
        return this.cadena.get(lineaActual).contains("IGNORE");
    }
    
    /**
     * Método para revisar las keywords
     * @param lineaActual
     * @return devuelve true si fue aceptada la estructra false contrario
     */
    public boolean keywordDeclaration(int lineaActual){
        
        if (this.cadena.get(lineaActual).contains("TOKENS") ||this.cadena.get(lineaActual).contains("IGNORE")||
                this.cadena.get(lineaActual).contains("END")){
            return false;
        }
        
        //revisr identificador y símbolo '='
        try{
            int indexSearch = this.cadena.get(lineaActual).indexOf("=")-1;
             while (this.cadena.get(lineaActual).substring(0, indexSearch).contains(" "))
                indexSearch--;
           
            boolean identifier = checkAutomata(this.ident_,this.cadena.get(lineaActual).substring(0,indexSearch));
            ///  int index1  = returnArray(identifier);
              if (!identifier){
                  return false;
              }
           
        }catch(Exception e){}
        
        
         //revisar string
        try{
            int indexSearch = this.cadena.get(lineaActual).indexOf("=")+1;
            String cadenaRevisar = this.cadena.get(lineaActual).substring(indexSearch);
            while(cadenaRevisar.startsWith(" "))
                cadenaRevisar = this.cadena.get(lineaActual).substring(++indexSearch);
            if (cadenaRevisar.substring(0, cadenaRevisar.length()).contains("."))
                cadenaRevisar = cadenaRevisar.substring(0, cadenaRevisar.length()-1);
            else{    
               LexerParserGenerator.errores.SynErr(lineaActual, "No contiene punto al final");
            }
            boolean resSet = checkAutomata(this.string_,cadenaRevisar);
         }catch(Exception e){
             LexerParserGenerator.errores.SemErr(lineaActual, "expresión mal ingresada");
             this.output=false;
         }
        
        //si lelga hasta aquí la cadena es válida
        return true;
    }
    
    
    /**
     * Método para revisar SetDeclaration
     * @param lineaActual
     * @return boolean con el estado de aceptacion
     * SetDecl = ident '=' Set '.'.
     * 
     */
    public boolean setDeclaration(int lineaActual){
        
        if (this.cadena.get(lineaActual).contains("END")||this.cadena.get(lineaActual).contains("KEYWORDS")||
                this.cadena.get(lineaActual).contains("TOKENS"))
            return false;
        //revisar identificador
        //ArrayList res1 = checkExpression(this.ident,lineaActual,0);
        
        try{
            int indexSearch = this.cadena.get(lineaActual).indexOf("=")-1;
            while (this.cadena.get(lineaActual).substring(0, indexSearch).contains(" "))
                indexSearch--;
           
            boolean identifier = checkAutomata(this.ident_,this.cadena.get(lineaActual).substring(0,indexSearch));
            ///  int index1  = returnArray(identifier);
              if (!identifier){
                  return false;
              }
        }catch(Exception e){
            LexerParserGenerator.errores.SynErr(lineaActual, "No contiene = ");
            this.output=false;
        }
        
      
        
        //revisar '='
        //ArrayList res2 = checkExpression(this.espacio+'='+this.espacio,lineaActual,index1);
        
         try{
            int indexSearch = this.cadena.get(lineaActual).indexOf("=");
            int indexSearch2 = indexSearch + 1;
            boolean equals = checkAutomata(this.igual_,this.cadena.get(lineaActual).substring(indexSearch, indexSearch2));
            //int index2 = returnArray(equals);
            if (!equals)
                return false;
        }catch(Exception e){
            LexerParserGenerator.errores.SynErr(lineaActual, "No contiene = ");
            this.output=false;
        }
     
        
        //revisar Set
         try{
            int indexSearch = this.cadena.get(lineaActual).indexOf("=")+1;
            String cadenaRevisar = this.cadena.get(lineaActual).substring(indexSearch);
            while(cadenaRevisar.startsWith(" "))
                cadenaRevisar = this.cadena.get(lineaActual).substring(++indexSearch);
            if (cadenaRevisar.substring(0, cadenaRevisar.length()).contains("."))
                cadenaRevisar = cadenaRevisar.substring(0, cadenaRevisar.length()-1);
            else{
                  LexerParserGenerator.errores.Warning(lineaActual, "No contiene punto al final ");
                
            }
            boolean resSet = set(lineaActual,cadenaRevisar);
         }catch(Exception e){
             this.output=false;
         }
        //ArrayList resSet = set(lineaActual,index2+index1);
      
       // if (resSet.isEmpty())
        //    return new ArrayList();
        //int index3 = returnArray(resSet);
        
       //revisar '.'
       // ArrayList res4 = checkExpression(this.espacio+"."+this.espacio,lineaActual,index3);
        //if (res4.isEmpty())
          //  return new ArrayList();
        
        
        return true;
    }
    
    /**
     * Método para revisar el Set
     * @param regex string a revisar
     * @param lineaActual del archivo
     * @return boolean (true aceptado, false contrario)
     * Set = BasicSet (('+'|'-') BasicSet)*.
     */
    public boolean set(int lineaActual,String regex){
        int index = 0;
        
        String revisar =regex;
        //Set = BasicSet
        if (regex.contains("+"))
            revisar = regex.substring(0,regex.indexOf("+"));
        else if (regex.contains("-"))
            revisar = regex.substring(0,regex.indexOf("-"));
        
        boolean basic = basicSet(lineaActual,revisar);
        
        if (!basic)
            return false;
        
        
        
       
        if (regex.contains("+")){
           String[] parts = regex.split("\\+");
            for (String part : parts) {
                //System.out.println(parts[i]);
                checkAutomata(this.basicSet_, part);
            }
          
            
        }
        
        return true;
    }
    
    public boolean revisarRecursive(String regex, int lineaActual){
        String revisar;
        while(true){
            if (regex.contains("+"))
                revisar = regex.substring(regex.indexOf("+"),regex.indexOf("+")+1);
            else
                break;
            boolean bl = checkAutomata(this.plusOrMinus_,revisar);
            if (!bl)
                break;
                
            regex = regex.substring(regex.indexOf("+")+1);
            if (regex.contains("+")){
                if (revisarRecursive(regex,lineaActual))
                    regex = regex.substring(lineaActual);
            }
            boolean b = basicSet(lineaActual,regex.trim());
            if (!b)
                return false;



        }
       return true;
    }
    
    /**
     * 
     * @param lineaActual 
     * @param regex string a revisar
     * @return booelan con el estado de aceptacion
     * BasicSet = string | ident | Char [ "..." Char].
     */
    public boolean basicSet(int lineaActual,String regex){
        //ArrayList<String> cadenas = new ArrayList();
      
        //BasicSet = {string}
        
       // ident | string | "CHR(number).."CHR"(number).
       boolean resBasicSet = this.checkAutomata(this.basicSet_,regex.trim());
      // boolean resBasicSet = bSet(lineaActual,regex);
       /*if (!resBasicSet.isEmpty()){
            cadenas.add((String)resBasicSet.get(1));

        }*/
        
       if (!resBasicSet)
             LexerParserGenerator.errores.SynErr(lineaActual, "No fue reconocido " + regex);
        
       
        return resBasicSet;
        
    }
    
    public boolean bSet(int lineaActual, String regex){
    
        String letter = "abcdefghlmnopqrstuvgwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890!\".$%&/()=?¿\'+-[]-´Ç-.,";
         boolean string= false;
        if (count(regex,'\"')==2)
            string =true;
            
       
        if (string)
            return true;
        
        boolean ident =false;
        boolean ident2 = false;
        if (count(regex,'\"')==0)
            ident =true;
        if (count(regex,'\'')==0)
            ident2 =true;
        if ((ident&&ident2))
            return true;
        boolean charT = basicChar(lineaActual,regex);
        if (charT)
            return true;
        
        
        
        return false;
    }
    
    public boolean basicChar(int lineaActual, String regex){
         String letterChar = "abcdefghlmnopqrstuvgwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890!\".$%&/()=?¿+-[]-´Ç-., ";
         if (regex.startsWith("\'"))
             regex = regex.replaceAll("\'", "");
         if (letterChar.contains(regex))
             return true;
         if (regex.contains("CHR(012345679)"))
             return true;
        return false;
    }
    
    /**
     * Método para revisar si un character
     * @param lineaActual
     * @param lastIndex
     * @return 
     */
    public ArrayList<String> Char(int lineaActual,int lastIndex){
        
        ArrayList res = checkAutomata(this.character_,lineaActual,lastIndex);
        if (!res.isEmpty()){
           
            return res;
        }
        
        return new ArrayList();
    }
    /**
     * Revisasr si el archivo no tiene errores
     */
    
    /**
     * Devuelve el estado final del archivo
     * @return
     */
    public boolean getOutput(){
        if (output){
            System.out.println("Archivo Cocol/R Aceptado");
            return true;
        }
        else{
            System.out.println("Archivo  Cocol/R no aceptado, tiene errores de estructura");
            return false;
        }
    }
    
    /**
     * Método para obtener el index guardado en un array
     * que sirve para hacer un substring
     * @param param es un ArrayList
     * @return integer
     */
    public int returnArray(ArrayList param){
        if (!param.isEmpty()){
            //System.out.println(param.get(1));
            return (int)param.get(0);
        }
        //el cero representa que no se corta la cadena
        return 0;
    }
    
    /**
     * Revisar si la expresión regular cumple el autómata especificado
     * @param param Automata comparador
     * @param lineaActual lineaActual del archivo
     * @param index índice para recortar el string
     * @return un arreglo: en la posicion cero el último indíce analizado y
     * en la posicion 1 el último string analizado
     */
    public ArrayList checkAutomata(Automata param,int lineaActual, int index){
       
        String cadena_revisar = this.cadena.get(lineaActual).substring(index);
       
        int preIndex = 0;
        try{
          
            while (cadena_revisar.startsWith(" ")){
                preIndex++;
                cadena_revisar = cadena_revisar.substring(preIndex, cadena_revisar.length());
            }
            if (cadena_revisar.contains(" "))
                cadena_revisar = cadena_revisar.substring(0, cadena_revisar.indexOf(" ")+1);
        }catch(Exception e){}
        try{
               cadena_revisar = cadena_revisar.substring(0, cadena_revisar.lastIndexOf("."));
        }catch(Exception e){}
        
        ArrayList resultado = new ArrayList();
        
        boolean returnValue=sim.simular(cadena_revisar.trim(), param);
        
        
      
        
        if (returnValue){
            resultado.add(cadena_revisar.length()+preIndex);
            resultado.add(cadena_revisar);
            return resultado;
        }
        else{
            if (!cadena_revisar.isEmpty()){
                System.out.println("Error en la linea " + lineaActual + ": la cadena " + cadena_revisar + " es no es:" + param.getTipo());
                this.output=false;
            }
            
            
        }
        
        
        return resultado;
        
    }
    
    /**
     * Sobrecarga del método para revisar los autómatas en una expresión
     * @param param
     * @param regex
     * @return true aceptado, false si no es acepatada la cadena
     */
    public boolean checkAutomata(Automata param, String regex){
        return sim.simular(regex, param);
    }
    
    /**
     * Método auxiliar para generar autómatas dependiendo de lo que se ingrese.
     * @param cadena 
     */
    public void crearAutomata(String cadena){
        if (cadena.startsWith("\"")){
            String or = "";
             cadena=  cadena.replace("\"", "");
            for (int i = 0;i<cadena.length();i++){
                Character c = cadena.charAt(i);
               
                    if (c != ' '){
                   
                        if (i<=cadena.length()-2)
                            or += c +"|";
                        if (i>cadena.length()-2)
                            or +=c;

                }
            }
            

            RegexConverter convert = new RegexConverter();
            String regex = convert.infixToPostfix("("+or+")+");
           
            AFNConstruct ThomsonAlgorithim = new AFNConstruct(regex);
            ThomsonAlgorithim.construct();
            Automata temp = ThomsonAlgorithim.getAfn();
           

            if (union==true){
                if (indexAutomata!=-1)
                    temp = ThomsonAlgorithim.concatenacion(temp, generador.get(indexAutomata));
            }
            temp.setTipo((String)this.compare.pop());
           
           
            union=false;
            generador.add(temp);
        }
        else{
            indexAutomata = buscarAFN(cadena.trim());
            System.out.println(indexAutomata);
            union=true;
        }
        
    }
    
    /**
     * Método para buscar un autómata dependiendo de su tipo
     * @param tipo
     * @return devuleve el indice en el arreglo.
     */
    public int buscarAFN(String tipo){
        for (int i = 0;i<generador.size();i++){
            if (generador.get(i).getTipo().equals(tipo))
                return i;
        }
        return -1;
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
     * Método para retroceder de línea, busca la línea anterior actual
     * @param lineaActual
     * @return lineaActual
     */
    public Integer retrocederLinea(int lineaActual){
       while (true){
           if (this.cadena.containsKey(--lineaActual))
               return lineaActual;
       }
    }
    
    /**
     * Método para revisar que tipo de sub autómata es aceptado por una 
     * expresión regular
     * @param regex expresión regular a comparar
     * @param conjunto arreglo de autómatas
     */
    public void checkIndividualAutomata(String regex, ArrayList<Automata> conjunto,int lineaActual){
        
        ArrayList<Boolean> resultado = new ArrayList();
       
            
            for (int j = 0;j<conjunto.size();j++){
                resultado.add(sim.simular(regex, conjunto.get(j)));
               
            }
           
            ArrayList<Integer> posiciones = checkBoolean(resultado);
            //resultado.clear();
            
           
            for (int k = 0;k<posiciones.size();k++){
                
                System.out.println(regex+ ": " + conjunto.get(posiciones.get(k)).getTipo());
            }
            if (posiciones.isEmpty()){
               System.out.println("Error línea archivo " + lineaActual +" : "+regex+ " no fue reconocido");
            }
        
    }
    
    /**
     * Método que devuelve las posiciones en las que el valor que tiene 
     * en cada posicion es true
     * @param bool arreglo de booleanos
     * @return arreglo de enteros
     */
    public ArrayList<Integer>  checkBoolean(ArrayList<Boolean> bool){
        ArrayList<Integer> posiciones = new ArrayList();
       
        for (int i = 0;i<bool.size();i++){
            if (bool.get(i))
                posiciones.add(i);
        }
        return posiciones;
        
    }
    
    /**
     * Método para crear un conjunto de autómatas
     * @return arreglo de autómatas
     */
    public ArrayList<Automata> conjuntoAutomatas(){
        ArrayList<Automata> conjunto = new ArrayList();
        conjunto.add(this.letter_);
        conjunto.add(this.digit_);
        conjunto.add(this.number_);
        conjunto.add(this.ident_);
        conjunto.add(this.string_);
        conjunto.add(this.character_);
        conjunto.add(this.plusOrMinus_);
        conjunto.add(this.igual_);
        conjunto.add(this.basicSet_);
        conjunto.add(this.espacio_);
        
        return conjunto;
        
        
        
    }

    public ArrayList<Automata> getGenerador() {
        return generador;
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
        
        
        }
        return count;
    }
    
    /**
     * Método para remplazar la última ocurrencia de un caracter
     * @param string
     * @param toReplace
     * @param replacement
     * @return string modificado
     */
    public  String replaceLast(String string, String toReplace, String replacement) {
        int pos = string.lastIndexOf(toReplace);
        if (pos > -1) {
            return string.substring(0, pos)
                 + replacement
                 + string.substring(pos + toReplace.length(), string.length());
        } else {
            return string;
        }
    }

    public ArrayList<Produccion> getProducciones() {
        return producciones;
    }
    
}


