/**
* Universidad Del Valle de Guatemala
* 07-nov-2015
* Pablo Díaz 13203
*/



import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Stack;

/**
 *
 * @author Pablo
 */
public class ParserSLRGenerator {
    
    private final ArrayList<Produccion> producciones;
    private final ArrayList<ItemTablaParseo> tablaParseo;
    private LexerSyntax syntax;
    private Automata LR;
    private String nombreArchivo;
    private ArrayList globalClosure = new ArrayList();

    public ParserSLRGenerator(ArrayList<Produccion> producciones,LexerSyntax syntax,String nombreArchivo) {
        //producciones leidas en la gramática
        this.producciones = producciones;
        this.tablaParseo = new ArrayList();
        this.syntax = syntax;
        this.nombreArchivo = nombreArchivo;
    }
    
    
    /**
     * Método que construye el autómata LR
     */
    public void constructLR(){
       //agregar simbolo $ si no lo contiene
        for (int z = 0 ;z < producciones.size();z++){
            System.out.println(producciones.get(z));
        }
       if (!producciones.get(0).getCuerpo().contains("$"))
           producciones.get(0).setCuerpo(producciones.get(0).getCuerpo()+" $");
       
       //se empieza por closure del símbolo inicial.
       HashSet closureInicial =  closure(producciones.get(0));
       
       LR = new Automata();
       //crear y agregar el estado inicial al autómata
       Estado inicial = new Estado(closureInicial);
       LR.addEstados(inicial);
       LR.setInicial(inicial);
       //variable para no guardar los estados creados temporalmente.
       HashSet<HashSet<Produccion>> acumuladorEstados = new HashSet();
       Stack<Estado> pila = new Stack();
       pila.add(inicial);
      
       while (!pila.isEmpty()){
          
           Estado actual = pila.pop();
           HashSet<String> alfabeto = calcularAlfabeto(actual);
           //System.out.println(alfabeto);
           LR.getAlfabeto().addAll(alfabeto);
           for (String letra: alfabeto){//hacer transiciones con cada letra de los cuerpos.
                //buscar las transiciones de cada cuerpo
                //System.out.println(LR);
                ArrayList<Produccion> search = searchProductionCuerpo(letra, (HashSet)actual.getId());
                HashSet estadosNuevos = new HashSet();
                for (Produccion search1 : search) {
                    /*
                    *revisar cada ocurrencia de la letra.
                    * este ciclo arregla el problema cuando ocurre más de una vez
                    * un símbolo. Es necesario eliminar temporalmente los espacios 
                    * para poder encontrar el item correcto.
                    */
                    String[] parts = search1.getCuerpo().split(" ");
                    int index = 0;
                    for (String part : parts)
                        
                    {
                       if (part.equals(letra)){
                           //int index =  Arrays.asList(parts).indexOf(letra);
                     
                        if ((int) search1.getItem().getPosicion() == index) {
                            Produccion modificar = (Produccion) search1.clonar();
                          
                            //System.out.println("colocando");
                            //System.out.println(search1);
                            //System.out.println((int)modificar.getItem().getPosicion()+letra.length());
                            //System.out.println(search1.getCuerpo().split(" ").length);
                            
                            modificar.getItem().setPosicion((int)modificar.getItem().getPosicion()+1);
                            System.out.println(modificar);
                           
                            estadosNuevos.addAll(closure(modificar));
                            globalClosure.clear();
                        }
                       }
                       index++;
                   }
                }
                if (!acumuladorEstados.contains(estadosNuevos)&&!estadosNuevos.isEmpty()){
                  acumuladorEstados.add(estadosNuevos);
                  Estado nuevo = new Estado(estadosNuevos);
                  actual.setTransiciones(new Transicion(actual,nuevo,letra));
                  LR.addEstados(nuevo);
                  pila.push(nuevo);
                }
                //condición para estados repetidos
                else if (acumuladorEstados.contains(estadosNuevos)&&!estadosNuevos.isEmpty()){
                   actual.setTransiciones(new Transicion(actual,
                           findEstado(LR.getEstados(),new ArrayList(estadosNuevos))
                           ,letra));
                }
              
           }
        
           
           
       }
       
        LR.setTipo("SLR");
        System.out.println(LR);
        crearArchivos(LR, 0, 0, "Automata SLR");
        
    }
       /**
        * Método para buscar un estado en el autómata
        * @param estados estados del autómata
        * @param buscar conjunto de producciones que hacen un estados
        * @return Estado encontrado
        */
       public Estado findEstado(ArrayList<Estado> estados, ArrayList<Produccion> buscar){
           for (int i = 0;i<estados.size();i++){
               Estado a = estados.get(i);
               HashSet<Produccion> convert = (HashSet<Produccion>)a.getId();
               ArrayList<Produccion> inner = new ArrayList(convert);
               if (inner.size()==buscar.size()){
                   boolean estadoCompleto = true;
                    for (int j = 0;j<inner.size();j++){
                        Produccion p1 = inner.get(j);
                        Produccion p2 = buscar.get(j);
                        if (!p1.getCabeza().equals(p2.getCabeza())||
                            !p1.getCuerpo().equals(p2.getCuerpo())||
                            !p1.getItem().getPosicion().equals(p2.getItem().getPosicion()))
                            estadoCompleto = false;
                        

                    }
                    if (estadoCompleto)
                        return a;
               }
           }
           return null;
       }
       
    /**
     * Método que devuelve todos los símbolos que pertenecen a un estado
     * @param estadoActual
     * @return Conjunto con Strings
     */
    public HashSet calcularAlfabeto(Estado estadoActual){
       
        HashSet<Produccion> prod = (HashSet)estadoActual.getId();
        HashSet alfabeto = new HashSet();
        for (Produccion produccion : prod) {
            String[] parts = produccion.getCuerpo().split(" ");
            for (String part : parts) {
               // System.out.println(part);
                if (!part.equals("$")&&!part.equals("ε")) {//se omite el símbolo de dólar.
                    alfabeto.add(part);
                }
            }
        }
        return alfabeto;
    }
    /**
     * Método que realiza el algoritmo de Closure sobre una producción.
     * @param I 
     * @return Conjunto con las producciones computadas.
     */
    public HashSet closure(Produccion I){
        HashSet resultado = new HashSet();
        resultado.add(I);
        String rev = I.getCuerpo().substring((int)I.getItem().getPosicion());
        String[] revArray = rev.split(" ");
        String[] parts = I.getCuerpo().split(" ");
        //falta arreglar este closure para cualquier produccion
        //tengo que buscar el no-terminal actual del item
        //System.out.println(I);
        //System.out.println(I.getItem().getPosicion());
       //si el item es el inmediato
        if (globalClosure.contains(I))
            return new HashSet();
        globalClosure.add(I);
        if ((int)I.getItem().getPosicion()<parts.length){
            if (!this.terminal(parts[(int)I.getItem().getPosicion()])){//si no es terminal
                ArrayList<Produccion> innerProd = searchProductions(parts[(int)I.getItem().getPosicion()]);//busca las producciones
                innerProd.remove(I);
                for (int j = 0;j<innerProd.size();j++){
                    resultado.addAll(closure(innerProd.get(j)));//busca recursivamente.
                }
            }
        }
        return resultado;
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
      /**
     * devuleve un array con las producciones que tengan al input como cuerpo.
     * @param cuerpo
     * @param productions
     * @return 
     */
    public ArrayList<Produccion> searchProductionCuerpo(String letra,HashSet<Produccion> productions){
        ArrayList<Produccion> prod = new ArrayList();
        for (Produccion buscarEnProd: productions){
            String[] parts = buscarEnProd.getCuerpo().split(" ");
            for (int j=0;j<parts.length;j++){
               if (parts[j].equals(letra))
                prod.add(buscarEnProd);
            }
           
        }
        return prod;
    }
    
      /**
     * Método que dice si es terminal o no el símobolo
     * @param simbolo
     * @return true/false
     */
    public boolean terminal(String simbolo){
        return searchProductions(simbolo).isEmpty();
    }
    
     public void crearArchivos(Automata tipoAutomata, double tiempoCreacion, double tiempoSimulacion, String tipo){
        
        FileCreator creadorArchivo = new FileCreator();
        //Simulacion generadorGrafico = new Simulacion();
        
        creadorArchivo.crearArchivo(tipoAutomata.toString(), tiempoCreacion, tiempoSimulacion, tipo);
        
       // generadorGrafico.generarDOT(tipo, tipoAutomata);
        
    }
     public ArrayList<ItemTablaParseo> reducciones(Estado SLR){
         ArrayList<ItemTablaParseo> itemsNuevos = new ArrayList();
         
         
         return itemsNuevos;
     }
     
    public void crearTablaParseo(){
        quitarEpsilon();
        String error = "";
        String errorShift = "";
        String errorReduce = "";
         HashSet ver3 = new HashSet();
        for (int i = 0; i < LR.getEstados().size();i++){
            if (i == 5){
                System.out.println("");
            }
            Estado estadoActual = LR.getEstados().get(i);
            ArrayList<String> alfabeto = new ArrayList(LR.getAlfabeto());
            alfabeto.add("$");
            for (String letra : alfabeto) {
                for (Transicion trans: (ArrayList<Transicion>)estadoActual.getTransiciones()){
                    if (trans.getSimbolo().equals(letra)){
                        
                        tablaParseo.add(new ItemTablaParseo(i,letra,determinarOperacion(letra),LR.getEstados().indexOf(trans.getFin())));
                        
                    }
                }
                
            }
            HashSet<Produccion> products = (HashSet<Produccion>) estadoActual.getId();
            int cantidadReduce = 0;
            int cantidadShift = 0;
            System.out.println(products);
            for (Produccion product : products) {
               
                if ((int)product.getItem().getPosicion() == product.getCuerpo().split(" ").length
                      ||product.getCuerpo().isEmpty() ){
                   
                    int indiceBuscado = this.indexOf(product);
                            
                    if (indiceBuscado != -1){
                        HashSet resultadoFollow = syntax.follow(producciones.get(indiceBuscado).getCabeza());
                        syntax.getArrayGlobal().clear();
                        
                        for (String letra2: (HashSet<String>)resultadoFollow){
                            if (!letra2.isEmpty())
                                tablaParseo.add(new ItemTablaParseo(i,letra2,"r",indiceBuscado));
                        }
                        
                    }
                    cantidadReduce++;
                   
                } else{
                    cantidadShift++;
                  
                } 
                
                 int indexOfDolar = indexString(product.getCuerpo().split(" "),"$");
                if (indexOfDolar == (int)product.getItem().getPosicion()){
                     tablaParseo.add(new ItemTablaParseo(i,"$","accept",1));
                }
                
            }
            if (cantidadReduce>1 && cantidadShift <1)
               error = ("Error reduce/reduce");
            else if (cantidadReduce>=1 && cantidadShift>=1){
                int es = 0;
                ArrayList ver = new ArrayList();
                ArrayList ver2 = new ArrayList();
               
                int in = -1;
                for (int k = 0; k<tablaParseo.size();k++){
                    ItemTablaParseo it = tablaParseo.get(k);
                    
                    if ((int)it.getActualEstado()==es){
                        if (!ver.contains(it.getSimbolo())){
                            ver.add(it.getSimbolo());
                            ver2.add(it);
                        }
                        else{
                            in = ver.indexOf(it.getSimbolo());
                            ver3.add(ver2.get(in));
                            ver3.add(it);
                        }
                        
                    }
                    else{
                        es++;
                        ver.clear();
                        ver2.clear();
                    }
                    
                }
              
                error = ("Error shift"+errorShift+"/reduce"+errorReduce);
            }
            
        }
        String acum = "\t" + " ";
         ArrayList<String> alfabeto = new ArrayList(LR.getAlfabeto());
          alfabeto.add("$");
        for (String letra: alfabeto){
           acum += letra + "\t";
        }
        // System.out.println(acum);
        String tabla = "";
        int anterior = 0;
        for (int k = 0;k<tablaParseo.size();k++){
           
           if (k == 0){
               tabla += (int)tablaParseo.get(k).getActualEstado();
           }
           int espacio =1;
           //System.out.println(k);
           //System.out.println(alfabeto.indexOf(tablaParseo.get(k).getSimbolo()));
           espacio += Math.abs(k-alfabeto.indexOf(tablaParseo.get(k).getSimbolo()))%6;
            //System.out.println(espacio);
            //  System.out.println("");
           tabla += tablaParseo.get(k).toString(espacio);
           
            if (k+1<tablaParseo.size()){
                if ((int)tablaParseo.get(k+1).getActualEstado()!=anterior){
                    tabla += "\n"+(int)tablaParseo.get(k+1).getActualEstado();
                    anterior = (int)tablaParseo.get(k+1).getActualEstado();
                }
            }
           
       }
        System.out.println(tabla);
        System.out.println("");
        System.out.println(tablaParseo.size());
        ArrayList<ItemTablaParseo> conv = new ArrayList(ver3);
        String[] parts = new String[conv.size()];
         for (int j = 0 ;j<parts.length;j++)
          parts[j] = "";
        for (int j = 0;j<conv.size();j++){
            String op = (String)conv.get(j).getOperacion();
            if (op.equals("r"))
                op = "reduce";
           parts[(int)conv.get(j).getActualEstado()] += op+(int)conv.get(j).getNextEstado()+"/";
        }
        for (int j = 0 ;j<parts.length;j++)
            if (!parts[j].isEmpty())
                System.out.println("Estado " + j +" : "+parts[j]);
    }
    
    public void quitarEpsilon(){
        for (int i = 0;i<this.LR.getEstados().size();i++){
            HashSet<Produccion> inn = (HashSet)LR.getEstados().get(i).getId();
            ArrayList<Produccion> inner = new ArrayList(inn);
            for (int j = 0;j< inner.size();j++){
                if (inner.get(j).getCuerpo().contains("ε"))
                    inner.get(j).setCuerpo("");
            }
           
        }
    }
    
    public boolean revisarLetras(String letra, int index){
        for (int i = 0 ;i<this.tablaParseo.size();i++){
            if ((int)tablaParseo.get(i).getActualEstado() == index &&
                tablaParseo.get(i).getSimbolo().equals(letra))
                return false;
        }
        return true;
    }
    
    public int indexOf(Produccion productions){
        int index = -1;
        for (int i = 0;i<producciones.size();i++){
           
            if (producciones.get(i).getCabeza().equals(productions.getCabeza())
                &&
                producciones.get(i).getCuerpo().equals(productions.getCuerpo())
                &&
                producciones.get(i).getItem().getPosicion().equals(0)){
                return i;
            }
           
        }
        
        
        return index;
    }
    
    public ItemTablaParseo buscarItem(String simbolo, int estado){
        for (int i = 0;i<tablaParseo.size();i++){
            if ((int)tablaParseo.get(i).getActualEstado() == estado &&
                tablaParseo.get(i).getSimbolo().equals(simbolo))
                return tablaParseo.get(i);
        }
        return null;
    }
    
    public void procesoParseo(String input){
        input += " $";
        Stack estados = new Stack();
        estados.push(0);
        int i = 0;
        boolean Goto = false;
        String[] parts = input.split(" ");
         ItemTablaParseo encontrado = null;
         String consumido = "";
         String actualString ="";
         int cantParts = parts.length;
        try{
            while(true){
               String ch = parts[i];
              
               
                int actual = (int)estados.peek();
               
                if (!Goto)
                    encontrado = buscarItem(ch,actual);
                String op = (String)encontrado.getOperacion();
                if (op.equals("r"))
                    op = "reduce";
                if (Goto)
                    op = "goto";
                
                System.out.format("%32s%10s%10s", estados, consumido,op+""+encontrado.getNextEstado());
                System.out.println("");

                //op += encontrado.getNextEstado();

                if (encontrado.getOperacion().equals("shift")){
                    i++;
                    actualString += parts[i];
                    consumido = "";
                    for (int b = 0;b+i<parts.length;b++){
                        consumido += " "+ parts[b+i];
                    }
                    estados.push(encontrado.getNextEstado());
                }
                else if (encontrado.getOperacion().equals("r")&&!Goto){
                   // System.out.println("i1 " + producciones.get((int)encontrado.getNextEstado()).getCuerpo().replaceAll("\\s", "").length());
                    //System.out.println( producciones.get((int)encontrado.getNextEstado()).getCuerpo().split(" ").length);
                    int cantidad = producciones.get((int)encontrado.getNextEstado()).getCuerpo().split(" ").length;
                    if ( producciones.get((int)encontrado.getNextEstado()).getCuerpo().replaceAll("\\s", "").isEmpty()){
                        cantidad--;
                        
                    }

                    while(cantidad>0){
                        estados.pop();
                        cantidad--;
                    }
                    Goto = true;
                }
                else if (Goto){
                    int buscarEstado = (int)estados.peek();
                    String transicion = producciones.get((int)encontrado.getNextEstado()).getCabeza();
                    int estadoEncontrado = -1;
                    for (int j = 0;j<LR.getEstados().size();j++){
                        if (j == buscarEstado){
                            for(Transicion trans : (ArrayList<Transicion>)LR.getEstados().get(j).getTransiciones()){
                                if (trans.getSimbolo().equals(transicion)){
                                    estadoEncontrado= LR.getEstados().indexOf(trans.getFin());

                                }
                            }
                        }
                    }
                    estados.push(estadoEncontrado);
                    Goto = false;
                }




                 if (encontrado.getOperacion().equals("accept"))
                    break;


            }
        }catch(Exception e){
            consumido = "";
            for (int b = 0;b+i<parts.length;b++){
                consumido += " "+ parts[b+i];
            }
            System.out.println("La entrada no pudo parsearse.");
            System.out.println("Se parseo hasta: " + actualString);
            System.out.println("Faltó parsear: " + consumido);
            
        }
    }
    
    
    
    public String determinarOperacion(String letra){
        if (terminal(letra)){
            return "shift";
        }
        if (letra.contains("$"))
            return "accept";
        return "goto";
    }
     
        /**
        * Método para buscar un estado en el autómata
        * @param estados estados del autómata
        * @param buscar conjunto de producciones que hacen un estados
        * @return Estado encontrado
        */
       public int findEstadoIndex(ArrayList<Estado> estados, ArrayList<Produccion> buscar){
           for (int i = 0;i<estados.size();i++){
               Estado a = estados.get(i);
               HashSet<Produccion> convert = (HashSet<Produccion>)a.getId();
               ArrayList<Produccion> inner = new ArrayList(convert);
               if (inner.size()==buscar.size()){
                   boolean estadoCompleto = true;
                    for (int j = 0;j<inner.size();j++){
                        Produccion p1 = inner.get(j);
                        Produccion p2 = buscar.get(j);
                        if (!p1.getCabeza().equals(p2.getCabeza())&&
                            !p1.getCuerpo().equals(p2.getCuerpo())&&
                            !p1.getItem().getPosicion().equals(p2.getItem().getPosicion()))
                            estadoCompleto = false;
                        

                    }
                    if (estadoCompleto)
                        return i;
               }
           }
           return -1;
       }
       
     
     
     
    public void arreglarRecursionInmediata(){
        int[] indices = new int[producciones.size()];
        for (int i = 0;i<this.producciones.size();i++){
            if (this.producciones.get(i).isRecursivaIzquierda()){
                if (searchProductions(producciones.get(i).getCabeza()).size()>1)
                    indices[i] = i;
            }
        }
        ArrayList<Produccion> nuevas = new ArrayList();
        for (int j = 0;j<indices.length;j++){
            ArrayList<Produccion> prods = searchProductions(producciones.get(indices[j]).getCabeza());
            for (int k = 0;k<prods.size();k++){
                Produccion p = prods.get(k);
                //Produccion nueva = new Produccion(p.getCabeza(),);
                
            }
            //producciones.remove(indices[j]);
        }
    }
    /**
    * Genera la  estructura de la clase analizadora
    */
    public void generarParser() {

        String scanner_total = (
            "/**"+"\n"+
            " * Nombre del archivo: "+this.nombreArchivo+"Parser.java"+"\n"+
            " * Universidad del Valle de Guatemala"+"\n"+
            " * Pablo Diaz 13203 " + "\n"+
            " * Descripción: Tercer proyecto. Generador de Parser"+"\n"+
            "**/"+"\n"+
            ""+"\n"+
           
            "import java.util.HashSet;"+"\n"+
            "import java.util.ArrayList;"+"\n"+
            "import java.util.TreeMap;"+"\n"+
            "import java.util.Stack;"+"\n"+
            "import java.io.FileInputStream;"+"\n"+
            "import java.io.FileOutputStream;"+"\n"+
            "import java.io.IOException;"+"\n"+
            "import java.io.ObjectInputStream;"+"\n"+
            "import java.io.ObjectOutputStream;"+"\n"+
            "import java.util.Map;"+"\n"+
            "import java.util.HashMap;"+"\n"+
          
           
          
            ""+"\n"+
            "public class "+this.nombreArchivo+"Parser {"+"\n"+
            ""+"\n"+
            "\t"+"private final ArrayList<Produccion> producciones = new ArrayList();"+"\n"+
            "\t"+"private final ArrayList<ItemTablaParseo> tablaParseo = new ArrayList();"+"\n"+
            "\t"+"private HashMap<Integer,String> input;"+"\n"+   
            "\t"+"private Automata SLR;"+"\n");
           
            scanner_total +=
       
            "\t"+"private Errors errores = new Errors();"+"\n"+"\n"+
                
            "\t"+"public " + this.nombreArchivo+"Parser(HashMap input){"+"\n"+
            "\t"+"\t"+"this.input=input;"+"\n"+
                            
          
            "\t"+"\t"+  "try"+"\n"+
            "\t"+"\t"+  "{"+"\n"+
            "\t"+"\t"+"\t"+    "FileInputStream fileIn = new FileInputStream(\"automata.ser\");"+"\n"+
            "\t"+"\t"+"\t"+     "ObjectInputStream in = new ObjectInputStream(fileIn);"+"\n"+
            "\t"+"\t"+"\t"+     "SLR = (Automata) in.readObject();"+"\n"+
            "\t"+"\t"+"\t"+    "in.close();"+"\n"+
            "\t"+"\t"+"\t"+    "fileIn.close();"+"\n"+
            "\t"+"\t"+"\t"+    " System.out.println(SLR.getEstados());"+"\n"+
            "\t"+"\t"+ "}catch(IOException i)"+"\n"+
            "\t"+"\t"+ "{"+"\n"+
            "\t"+"\t"+"\t"+   "i.printStackTrace();"+"\n"+

            "\t"+"\t"+"}catch(ClassNotFoundException c)"+"\n"+
            "\t"+"\t"+"{"+"\n"+
            "\t"+"\t"+"\t"+  "System.out.println(\"Automata class not found\");"+"\n"+
            "\t"+"\t"+"\t"+  "c.printStackTrace();"+"\n"+

            "\t"+"\t"+ "}"+"\n"+
            "\t"+"\n"+
            "\t"+"\t"+"generarItems();"+"\n"+
            "\t"+"\t"+"generarProducciones();"+"\n"+
                    
                    
           "\t" + "}"+"\n"    
                
        ;

        //scanner_total += crearAutomatasTexto();
        //scanner_total += generar();
        scanner_total += copiarTablaParseo();
        scanner_total += copiarProducciones();
        scanner_total += generarMetodosParseo();
       // scanner_total += ignoreWords();
        scanner_total+="\n"+"}";
        
        ReadFile fileCreator = new ReadFile();
        fileCreator.crearArchivoParser(scanner_total, nombreArchivo+"Parser");
        this.crearMainParser();
        
    }
    
    public String copiarTablaParseo(){
        String res = "";
        res += "\t"+"public void generarItems(){"+"\n";
        for (int i = 0;i<tablaParseo.size();i++){
            ItemTablaParseo itemActual = tablaParseo.get(i);
            res += "\t"+"\t" + "tablaParseo.add(new ItemTablaParseo("+itemActual.getActualEstado()+","
                    +"\""+itemActual.getSimbolo()+"\",\""+itemActual.getOperacion()+"\","+itemActual.getNextEstado()+"));"+"\n";
        }
        res+= "\t"+"}"+"\n";
        return res;
    }
    
    public String copiarProducciones(){
        String res = "";
        res += "\t"+"public void generarProducciones(){"+"\n";
        for (int i = 0;i<producciones.size();i++){
           Produccion p = producciones.get(i);
           res += "\t"+"\t" + "producciones.add(new Produccion(\""+p.getCabeza()+"\",\""+p.getCuerpo()+"\", new Item("+p.getItem().getPosicion()+")));"+"\n";
        }
        res+= "\t"+"}"+"\n";
        return res;
    }
    
    public String generarMetodosParseo(){
        String res = "";
      
        
          
    res +=
        "\t"+"public String determinarOperacion(String letra){"+"\n"+
        "\t"+"\t"+"if (terminal(letra)){"+"\n"+
            "\t"+"\t"+"\t"+"return \"shift\";"+"\n"+
        "\t"+"\t"+"}"+"\n"+
        "\t"+"\t"+"if (letra.contains(\"$\"))"+"\n"+
           "\t"+"\t"+"\t"+ "return \"accept\";"+"\n"+
        "\t"+"\t"+"return \"goto\";"+"\n"+
        "\t"+"}"+"\n"+"\n";
       res += "\t"+"public void revisarArchivo(){"+"\n"+
            	"\t"+"\t"+"for (Map.Entry<Integer, String> entry : input.entrySet()) {"+"\n"+
			"\t"+"\t"+"\t"+"Integer key = entry.getKey();"+"\n"+
			"\t"+"\t"+"\t"+"String value = entry.getValue();"+"\n"+
			"\t"+"\t"+"\t"+"procesoParseo(value,key);"+"\n"+
		"\t"+"\t"+"}"+"\n"+
           "\t"+ "}"+"\n";   
    res += 
         "\t"+"public void procesoParseo(String input, int lineaActual){"+"\n"+
          "\t"+ "\t"+"imprimirTabla();"+"\n"+
           "\t"+"\t"+"boolean aceptado = false;"+"\n"+
         "\t"+ "\t"+"input += \" $\";"+"\n"+
         "\t"+ "\t"+"Stack estados = new Stack();"+"\n"+
         "\t"+ "\t"+"estados.push(0);"+"\n"+
         "\t"+ "\t"+"int i = 0;"+"\n"+
         "\t"+ "\t"+"boolean Goto = false;"+"\n"+
         "\t"+ "\t"+"String[] parts = input.split(\" \");"+"\n"+
         "\t"+ "\t"+"ItemTablaParseo encontrado = null;"+"\n"+
         "\t"+ "\t"+"String consumido = \"\";"+"\n"+
         "\t"+ "\t"+"int cantParts = parts.length;"+"\n"+
         "\t"+ "\t"+"String actualString =\"\";"+"\n"+
         "\t"+ "\t"+"try{"+"\n"+
         "\t"+"\t"+ "\t"+"while(true){"+"\n"+
             "\t"+"\t"+ "\t"+ "\t"+"String ch = parts[i];"+"\n"+
        

            "\t"+ "\t"+ "\t"+ "\t"+"int actual = (int)estados.peek();"+"\n"+
             "\t"+ "\t"+ "\t"+ "\t"+"if (!Goto)"+"\n"+
                    "\t"+ "\t"+ "\t"+ "\t"+"\t"+"encontrado = buscarItem(ch,actual);"+"\n"+

             
             "\t"+"\t"+ "\t"+ "\t"+"String op = (String)encontrado.getOperacion();"+"\n"+
             "\t"+"\t"+ "\t"+ "\t"+"if (op.equals(\"r\"))"+"\n"+
                 "\t"+"\t"+ "\t"+ "\t"+ "\t"+"op = \"reduce\";"+"\n"+
             "\t"+"\t"+ "\t"+ "\t"+"if (Goto)"+"\n"+
                 "\t"+"\t"+ "\t"+ "\t"+ "\t"+"op =\"goto\";"+"\n"+
              "\t"+"\t"+"\t"+ "\t"+"System.out.format(\"%32s%10s%10s\", estados, consumido,op+encontrado.getNextEstado());"+"\n"+
             "\t"+"\t"+ "\t"+ "\t"+"System.out.println(\"\");"+"\n"+
            
            //op += encontrado.getNextEstado();
            
            "\t"+"\t"+"\t"+"\t"+ "if (encontrado.getOperacion().equals(\"shift\")){"+"\n"+
                "\t"+"\t"+"\t"+"\t"+"\t"+"i++;"+"\n"+
                 "\t"+"\t"+"\t"+"\t"+"\t"+"actualString += parts[i];"+"\n"+
                 "\t"+"\t"+"\t"+"\t"+"\t"+"consumido = \"\";"+"\n"+
                    "\t"+"\t"+"\t"+"\t"+"\t"+"\t"+"for (int b = 0;b+i<parts.length;b++){"+"\n"+
                       "\t"+"\t"+ "\t"+"\t"+"\t"+"\t"+"\t"+"consumido += \" \"+ parts[b+i];"+"\n"+
                "\t"+"\t"+"\t"+"\t"+"\t"+"}"+"\n"+
                "\t"+"\t"+"\t"+"\t"+"\t"+"estados.push(encontrado.getNextEstado());"+"\n"+
            "\t"+"\t"+"\t"+"\t"+"}"+"\n"+
            "\t"+"\t"+"\t"+"\t"+"else if (encontrado.getOperacion().equals(\"r\")&&!Goto){"+"\n"+
                "\t"+"\t"+"\t"+"\t"+"int cantidad = producciones.get((int)encontrado.getNextEstado()).getCuerpo().split(\" \").length;"+"\n"+
                 "\t"+"\t"+"\t"+"\t"+"if ( producciones.get((int)encontrado.getNextEstado()).getCuerpo().replaceAll(\"\\\\s\", \"\").isEmpty()){"+"\n"+
                        "\t"+"\t"+"\t"+"\t"+"\t"+"cantidad--;"+"\n"+
                        
                    "\t"+"\t"+"\t"+"\t"+"}"+"\n"+

                "\t"+"\t"+"\t"+"\t"+"while(cantidad>0){"+"\n"+
                    "\t"+"\t"+"\t"+"\t"+"\t"+"estados.pop();"+"\n"+
                    "\t"+"\t"+"\t"+"\t"+"\t"+"cantidad--;"+"\n"+
                "\t"+"\t"+"\t"+"\t"+"}"+"\n"+
                "\t"+"\t"+"\t"+"\t"+"Goto = true;"+"\n"+
            "\t"+"\t"+"\t"+"\t"+"}"+"\n"+
            "\t"+"\t"+"\t"+"\t"+"else if (Goto){"+"\n"+
                "\t"+"\t"+"\t"+"\t"+"\t"+"int buscarEstado = (int)estados.peek();"+"\n"+
                "\t"+"\t"+"\t"+"\t"+"\t"+"String transicion = producciones.get((int)encontrado.getNextEstado()).getCabeza();"+"\n"+
                "\t"+"\t"+"\t"+"\t"+"\t"+"int estadoEncontrado = -1;"+"\n"+
                "\t"+"\t"+"\t"+"\t"+"\t"+"for (int j = 0;j<SLR.getEstados().size();j++){"+"\n"+
                    "\t"+"\t"+"\t"+"\t"+"\t"+"\t"+"if (j == buscarEstado){"+"\n"+
                        "\t"+"\t"+"\t"+"\t"+"\t"+"\t"+"\t"+"for(Transicion trans : (ArrayList<Transicion>)SLR.getEstados().get(j).getTransiciones()){"+"\n"+
                           "\t"+"\t"+ "\t"+"\t"+"\t"+"\t"+"\t"+"\t"+"if (trans.getSimbolo().equals(transicion)){"+"\n"+
                               "\t"+"\t"+"\t"+ "\t"+"\t"+"\t"+"\t"+"\t"+"\t"+"estadoEncontrado= SLR.getEstados().indexOf(trans.getFin());"+"\n"+
                                
                            "\t"+"\t"+"\t"+"\t"+"\t"+"\t"+"\t"+"\t"+"}"+"\n"+
                        "\t"+"\t"+"\t"+"\t"+"\t"+"\t"+"\t"+"}"+"\n"+
                    "\t"+"\t"+"\t"+"\t"+"\t"+"\t"+"}"+"\n"+
                "\t"+"\t"+"\t"+"\t"+"\t"+"}"+"\n"+
               "\t"+"\t"+"\t"+"\t"+"estados.push(estadoEncontrado);"+"\n"+
               "\t"+"\t"+"\t"+"\t"+"Goto = false;"+"\n"+
           "\t"+ "\t"+"\t"+"\t"+"}"+"\n"+
           
           "\t"+"\t"+ "\t"+"\t"+"if (encontrado.getOperacion().equals(\"accept\")){"+"\n"+
            "\t"+ "\t"+ "\t"+"\t"+"\t"+"System.out.println(\"Entrada aceptada en la linea: \"+lineaActual);"+"\n"+
              "\t"+ "\t"+ "\t"+"\t"+"\t"+"break;"+"\n"+
              "\t"+ "\t"+ "\t"+"\t"+"}"+"\n"+
            
           
            
        "\t"+"\t"+"\t"+"}"+"\n"+
        "\t"+"\t"+"}catch(Exception e){"+"\n"+
            "\t"+"\t"+"\t"+"\t"+"\t"+"\t"+"consumido = \"\";"+"\n"+
            "\t"+"\t"+"\t"+"for (int b = 0;b+i<parts.length;b++){"+"\n"+
                "\t"+"\t"+"\t"+"\t"+"consumido += \" \"+ parts[b+i];"+"\n"+
            "\t"+"\t"+"\t"+"}"+"\n"+
            "\t"+"\t"+"\t"+"System.out.println(\"La entrada: \"+ input +\"no pudo parsearse en la linea: \"+lineaActual);"+"\n"+
            "\t"+"\t"+"\t"+"System.out.println(\"Se parseo hasta: \" + actualString);"+"\n"+
            "\t"+"\t"+"\t"+"System.out.println(\"Faltó parsear: \" + consumido);"+"\n"+
            
       "\t"+"\t"+ "}"+"\n"+
    "\t"+"}"+"\n";
        
        res+=  
        "\t"+"public ItemTablaParseo buscarItem(String simbolo, int estado){"+"\n"+
        "\t"+"\t"+"for (int i = 0;i<tablaParseo.size();i++){"+"\n"+
            "\t"+"\t"+"\t"+"if ((int)tablaParseo.get(i).getActualEstado() == estado &&"+"\n"+
                "\t"+"\t"+"\t"+"\t"+"tablaParseo.get(i).getSimbolo().equals(simbolo))"+"\n"+
                "\t"+"\t"+"\t"+"\t"+"return tablaParseo.get(i);"+"\n"+
       "\t"+"\t"+ "}"+"\n"+
       "\t"+"\t"+ "return null;"+"\n"+
    "\t"+"}"+"\n";
        
        res += 
     "\t"+"/**"+"\n"+
     "\t"+" * Método que dice si es terminal o no el símobolo"+"\n"+
     "\t"+" * @param simbolo"+"\n"+
     "\t"+" * @return true/false"+"\n"+
     "\t"+" */"+"\n"+
     "\t"+"public boolean terminal(String simbolo){"+"\n"+
         "\t"+ "\t"+"return searchProductions(simbolo).isEmpty();"+"\n"+
    "\t"+"}"+"\n";
        
    res += 
    "\t"+ "/**"+"\n"+
    "\t"+ "* devuleve un array con las producciones que tengan al input como cabeza"+"\n"+
    "\t"+ "* @param cabeza"+"\n"+
    "\t"+ "* @return "+"\n"+
    "\t"+ "*/"+"\n"+
   "\t"+ "public ArrayList<Produccion> searchProductions(String cabeza){"+"\n"+
        "\t"+"\t"+"ArrayList<Produccion> prod = new ArrayList();"+"\n"+
        "\t"+"\t"+"for (int i = 0 ;i<producciones.size();i++){"+"\n"+
            "\t"+"\t"+"\t"+"if (producciones.get(i).getCabeza().equals(cabeza))"+"\n"+
                "\t"+"\t"+"\t"+"prod.add(producciones.get(i));"+"\n"+
        "\t"+"\t"+"}"+"\n"+
        "\t"+"\t"+"return prod;"+"\n"+
    "\t"+"}"+"\n";
    
        res += 
               "\t"+"public void imprimirTabla(){"+"\n"+
                 "\t"+"\t"+"String acum = \"\\t\" + \" \";"+"\n"+
                 "\t"+"\t"+"ArrayList<String> alfabeto = new ArrayList(SLR.getAlfabeto());"+"\n"+
                 "\t"+"\t"+"alfabeto.add(\"$\");"+"\n"+
                 "\t"+"\t"+"for (String letra: alfabeto){"+"\n"+
                    "\t"+"\t"+"\t"+"acum += letra + \"\\t\";"+"\n"+
                 "\t"+"\t"+"}"+"\n"+
                
               "\t"+"\t"+"String tabla = \"\";"+"\n"+
               "\t"+"\t"+"int anterior = 0;"+"\n"+
               "\t"+"\t"+"for (int k = 0;k<tablaParseo.size();k++){"+"\n"+

                "\t"+"\t"+"\t"+"if (k == 0){"+"\n"+
                      "\t"+"\t"+"\t"+"tabla += (int)tablaParseo.get(k).getActualEstado();"+"\n"+
                  "\t"+"\t"+"\t"+"}"+"\n"+
                  "\t"+"\t"+"\t"+"int espacio =1;"+"\n"+
                  //System.out.println(k);
                  //System.out.println(alfabeto.indexOf(tablaParseo.get(k).getSimbolo()));
                  "\t"+"\t"+"\t"+"espacio += Math.abs(k-alfabeto.indexOf(tablaParseo.get(k).getSimbolo()))%6;"+"\n"+
                   //System.out.println(espacio);
                   //  System.out.println("");
                  "\t"+"\t"+"\t"+"tabla += tablaParseo.get(k).toString(espacio);"+"\n"+

                   "\t"+"\t"+"\t"+"if (k+1<tablaParseo.size()){"+"\n"+
                       "\t"+"\t"+"\t"+"\t"+"if ((int)tablaParseo.get(k+1).getActualEstado()!=anterior){"+"\n"+
                           "\t"+"\t"+"\t"+"\t"+"\t"+"tabla += \"\\n\"+(int)tablaParseo.get(k+1).getActualEstado();"+"\n"+
                           "\t"+"\t"+"\t"+"\t"+"\t"+"anterior = (int)tablaParseo.get(k+1).getActualEstado();"+"\n"+
                       "\t"+"\t"+"\t"+"\t"+"}"+"\n"+
                   "\t"+"\t"+"\t"+"}"+"\n"+

              "\t"+"\t"+"}"+"\n"+
               "\t"+"\t"+"System.out.println(tabla);"+"\n"+
               "\t"+"\t"+"System.out.println(\"\");"+"\n"+
             "\t" + "}"+"\n";
    
        return res;
    }
    
    
    public void crearMainParser(){
       String scanner_total = (
            "/**"+"\n"+
            " * Nombre del archivo: "+this.nombreArchivo+"Parser.java"+"\n"+
            " * Universidad del Valle de Guatemala"+"\n"+
            " * Pablo Diaz 13203 " + "\n"+
            " * Descripción: Tercer proyecto. Generador de ParserMain"+"\n"+
            "**/"+"\n"+
            ""+"\n"+
            ""+"import java.io.File;"+"\n"+
            "import java.util.HashMap;"+"\n"+
            ""+"\n"+
            "public class "+this.nombreArchivo+"ParserMain {"+"\n"+
            ""+"\n"
            );
       scanner_total +=   "/**"+"\n"+
            "* @param args the command line arguments"+"\n"+
           " */"+"\n"+
           "\t"+"public static void main(String[] args) {"+"\n"+
               "\t"+"\t"+ "// TODO code application logic here"+"\n"+
                "ReadFile read = new ReadFile();"+"\n"+
		"File file = new File(\"inputParser\"+\".txt\");"+"\n"+
		"HashMap input = read.leerArchivo(file);"+"\n"+
		
               "\t"+"\t" + this.nombreArchivo+"Parser"+" objParser " + "= new " + this.nombreArchivo+"Parser(input);"+"\n"+
                "objParser.revisarArchivo();"+"\n"+
               "\t"+"}"+"\n"+
               "}"
               ;
         ReadFile fileCreator = new ReadFile();
        fileCreator.crearArchivoParser(scanner_total, nombreArchivo+"ParserMain");
    }
    
    public void serialize(){
        try
        {
         FileOutputStream fileOut =
         new FileOutputStream("ParserGenerado/automata.ser");
         ObjectOutputStream out = new ObjectOutputStream(fileOut);
         out.writeObject(LR);
         out.close();
         fileOut.close();
         System.out.println("Serialized data is saved in /ParserGenerado/automata.ser");
        }catch(IOException i)
        {
            i.printStackTrace();
        }
         
      
     
    }
     public int indexString(String[] TYPES,String search){
      
        int index = -1;
        for (int i=0;i<TYPES.length;i++) {
            if (TYPES[i].equals(search)) {
                index = i;
                break;
            }
        }
        return index;
    }
}
