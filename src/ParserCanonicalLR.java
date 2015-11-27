
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Stack;
import java.util.TreeSet;

/**
* Universidad Del Valle de Guatemala
* 23-nov-2015
* Pablo Díaz 13203
*/

/**
 *
 * @author Pablo
 */
public class ParserCanonicalLR {
    
    private final ArrayList<Produccion> producciones;
    private LexerSyntax syntax;
    private ArrayList globalClosure = new ArrayList();
    private Automata LR;
    private HashSet<Produccion> globalActual;
    private ArrayList<ItemTablaParseo> tablaParseo;
    
    public ParserCanonicalLR(ArrayList<Produccion> producciones,LexerSyntax syntax) {
        this.tablaParseo = new ArrayList();
        this.producciones = producciones;
        this.syntax = syntax;
    }

    public void construirAutomata(){
        //primero poner dolar como lookahead al inicial
        producciones.get(0).setLookahead("$");
        //se empieza por closure del símbolo inicial.
        HashSet closureInicial =  closureInicial(producciones.get(0));
        HashSet closureModificado =  new HashSet(generarNuevasProducciones(new ArrayList(closureInicial)));
        System.out.println(closureModificado);
        LR = new Automata();
       //crear y agregar el estado inicial al autómata
       Estado inicial = new Estado(closureModificado);
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
                          
                            estadosNuevos.addAll(new HashSet(generarNuevasProducciones(new ArrayList(preClosure(modificar,actual)))));
                            //estadosNuevos.addAll(closure(modificar));
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
                  System.out.println(LR);
                }
                //condición para estados repetidos
                else if (acumuladorEstados.contains(estadosNuevos)&&!estadosNuevos.isEmpty()){
                   actual.setTransiciones(new Transicion(actual,
                           findEstado(LR.getEstados(),new ArrayList(estadosNuevos))
                           ,letra));
                }
              
           }
        
           
       }
        System.out.println(LR);
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
    
    public ArrayList<Produccion> generarNuevasProducciones(ArrayList<Produccion> arreglar ){
        int i = 0;
        ArrayList<Produccion> copy = new ArrayList();
        for (Produccion p: arreglar){
            String[] look = p.getLookahead().toString().split(" ");
            if (look.length>1){
               for (String lk: look){
                  if (!lk.isEmpty()){
                   copy.add(new Produccion(arreglar.get(i).getCabeza(),
                           arreglar.get(i).getCuerpo(),
                           arreglar.get(i).getItem(),
                           lk
                   ));
                  }
               }
              //arreglar.remove(i);
            }
            i++;
        }
       
        ArrayList copyA = new ArrayList(arreglar);
        for (Produccion p: arreglar){
            String[] look = p.getLookahead().toString().split(" ");
            if (look.length>1){
         
              copyA.remove(p);
            }
            
        }
        copy.addAll(copyA);
        return copy;
    }
    public HashSet preClosure(Produccion I,Estado actual){
        HashSet<Produccion> ac = (HashSet)actual.getId();
        globalActual = ac;
        return closure(I);
    }
     /**
     * Método que realiza el algoritmo de Closure sobre una producción.
     * @param I 
     * @return Conjunto con las producciones computadas.
     */
    public HashSet closure(Produccion I){
        HashSet resultado = new HashSet();
        resultado.add(I);
        //calcular X y Beta
        String beta = "";
        if ((int)I.getItem().getPosicion()+1>=I.getCuerpo().split(" ").length)
            beta = "";
        else
            beta = I.getCuerpo().split(" ")[(int)I.getItem().getPosicion()+1];
        System.out.println(I.getLookahead());
        TreeSet<String> nuevoLookahead = syntax.first(beta+(String)I.getLookahead());
        String lookaheadString = "";
        for (String st : nuevoLookahead){
            lookaheadString += " "+st;
        }
       
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
                ArrayList<Produccion> innerProd = searchProductionsSpecific(parts[(int)I.getItem().getPosicion()], new ArrayList(globalActual));//busca las producciones
                innerProd.remove(I);
                for (int j = 0;j<innerProd.size();j++){
                    Produccion pnew = innerProd.get(j).clonar();
                    pnew.setLookahead(lookaheadString);
                    resultado.addAll(closure(pnew));//busca recursivamente.
                }
            }
        }
        return resultado;
    }
     /**
     * Método que realiza el algoritmo de Closure sobre una producción.
     * @param I 
     * @return Conjunto con las producciones computadas.
     */
    public HashSet closureInicial(Produccion I){
        HashSet resultado = new HashSet();
        resultado.add(I);
        //calcular X y Beta
        String beta = "";
        if ((int)I.getItem().getPosicion()+1==I.getCuerpo().split(" ").length)
            beta = "";
        else
            beta = I.getCuerpo().split(" ")[(int)I.getItem().getPosicion()+1];
        System.out.println(I.getLookahead());
        TreeSet<String> nuevoLookahead = syntax.first(beta+(String)I.getLookahead());
        String lookaheadString = "";
        for (String st : nuevoLookahead){
            lookaheadString += " "+st;
        }
       
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
                    innerProd.get(j).setLookahead(lookaheadString);
                    resultado.addAll(closureInicial(innerProd.get(j)));//busca recursivamente.
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
     * devuleve un array con las producciones que tengan al input como cabeza
     * @param cabeza
     * @param prods
     * @return 
     */
    public ArrayList<Produccion> searchProductionsSpecific(String cabeza,ArrayList<Produccion> prods){
        ArrayList<Produccion> prod = new ArrayList();
        for (int i = 0 ;i<prods.size();i++){
            if (prods.get(i).getCabeza().equals(cabeza))
                prod.add(prods.get(i));
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
    
       /**
     * devuleve un array con las producciones que tengan al input como cuerpo.
     * @param letra
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
    
    public String determinarOperacion(String letra){
        if (terminal(letra)){
            return "shift";
        }
        if (letra.contains("$"))
            return "accept";
        return "goto";
    }
    
    public void crearTablaParseo(){
       
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
               
                if ((int)product.getItem().getPosicion() == product.getCuerpo().split(" ").length){
                   
                    int indiceBuscado = this.indexOf(product);
                            
                    if (indiceBuscado != -1){
                        //HashSet resultadoFollow = syntax.follow(producciones.get(indiceBuscado).getCabeza());
                        //syntax.getArrayGlobal().clear();
                       tablaParseo.add(new ItemTablaParseo(i,(String)product.getLookahead(),"r",indiceBuscado));
                        
                    }
                    cantidadReduce++;
                   
                } else{
                    cantidadShift++;
                  
                } 
                String[] parts = product.getCuerpo().split(" ");
               
                int indexOfDolar = indexString(parts,"$");
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
        for (int h = 0;h<tablaParseo.size();h++){
            if (tablaParseo.get(h).getOperacion().equals("r")){
                if ((int)tablaParseo.get(h).getNextEstado()==0){
                    tablaParseo.get(h).setOperacion("accept");
                    
                }
            }
                
        }
        
        
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
