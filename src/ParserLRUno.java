
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
public class ParserLRUno {
    
    private final ArrayList<Produccion> producciones;
    private LexerSyntax syntax;
    private ArrayList globalClosure = new ArrayList();
    private Automata LR;
    
    public ParserLRUno(ArrayList<Produccion> producciones,LexerSyntax syntax) {
        this.producciones = producciones;
        this.syntax = syntax;
    }

    public void construirAutomata(){
        //primero poner dolar como lookahead al inicial
        producciones.get(0).setLookahead("$");
        //se empieza por closure del símbolo inicial.
        HashSet closureInicial =  closure(producciones.get(0));
        ArrayList closureModificado = generarNuevasProducciones(new ArrayList(closureInicial));
        System.out.println(closureInicial);
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
                          
                            estadosNuevos.addAll(new HashSet(generarNuevasProducciones(new ArrayList(closure(modificar)))));
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
     * Método que dice si es terminal o no el símobolo
     * @param simbolo
     * @return true/false
     */
    public boolean terminal(String simbolo){
        return searchProductions(simbolo).isEmpty();
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

}
