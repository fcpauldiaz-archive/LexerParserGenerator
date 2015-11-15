/**
* Universidad Del Valle de Guatemala
* 07-nov-2015
* Pablo Díaz 13203
*/

package lexerparsergenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;

/**
 *
 * @author Pablo
 */
public class AutomataLR {
    
    private final ArrayList<Produccion> producciones;
    private final ArrayList<ItemTablaParseo> tablaParseo;
    private Automata LR;

    public AutomataLR(ArrayList<Produccion> producciones) {
        //producciones leidas en la gramática
        this.producciones = producciones;
        this.tablaParseo = new ArrayList();
    }
    
    
    /**
     * Método que construye el autómata LR
     */
    public void constructLR(){
       HashSet closureInicial =  closure(producciones.get(0));//se empieza por closure del símbolo inicial.
       
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
           LR.getAlfabeto().addAll(alfabeto);
           for (String letra: alfabeto){//hacer transiciones con cada letra de los cuerpos.
                //buscar las transiciones de cada cuerpo
              
                ArrayList<Produccion> search = searchProductionCuerpo(letra, (HashSet)actual.getId());
                HashSet estadosNuevos = new HashSet();
                for (Produccion search1 : search) {
                    /*
                    *revisar cada ocurrencia de la letra.
                    * este ciclo arregla el problema cuando ocurre más de una vez
                    * un símbolo. Es necesario eliminar temporalmente los espacios 
                    * para poder encontrar el item correcto.
                    */
                   for (int index = search1.getCuerpo().replaceAll("\\s", "").indexOf(letra);
                        index >= 0;
                        index =  search1.getCuerpo().replaceAll("\\s", "").indexOf(letra,index+1))
                   {
                        if ((int) search1.getItem().getPosicion() == index) {
                            Produccion modificar = (Produccion) search1.clonar();
                            System.out.println("colocando");
                            System.out.println(search1);
                            System.out.println((int)modificar.getItem().getPosicion()+letra.length());
                            System.out.println(search1.getCuerpo().split(" ").length);
                            modificar.getItem().setPosicion((int)modificar.getItem().getPosicion()+1);
                           
                            estadosNuevos.addAll(closure(modificar));
                        }
                       
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
       
        LR.setTipo("LR(0)");
        System.out.println(LR);
        crearArchivos(LR, 0, 0, "Automata LR(0)");
        
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
                        if (!p1.getCabeza().equals(p2.getCabeza())&&
                            !p1.getCuerpo().equals(p2.getCuerpo())&&
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
                if (!part.equals("$")) {//se omite el símbolo de dólar.
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
        String[] parts = I.getCuerpo().split(" ");
        //falta arreglar este closure para cualquier produccion
        //tengo que buscar el no-terminal actual del item
        //System.out.println(I);
        //System.out.println(I.getItem().getPosicion());
       //si el item es el inmediato
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
    public ArrayList<Produccion> searchProductionCuerpo(String cuerpo,HashSet<Produccion> productions){
        ArrayList<Produccion> prod = new ArrayList();
        for (Produccion buscarEnProd: productions){
            String[] parts = buscarEnProd.getCuerpo().split(" ");
            for (int j=0;j<parts.length;j++){
               if (parts[j].equals(cuerpo))
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
        
        for (int i = 0; i < LR.getEstados().size();i++){
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
            for (Produccion product : products) {
                if ((int)product.getItem().getPosicion() == product.getCuerpo().replaceAll("\\s", "").length()){
                    //hacer reduccion con todos el alfabeto.
                    for (String letra : alfabeto){
                        if (terminal(letra)||letra.equals("$")){
                            int indiceBuscado = this.indexOf(product);
                            if (indiceBuscado != -1){
                                tablaParseo.add(new ItemTablaParseo(i,letra,"r",indiceBuscado));
                            }
                        }
                        
                    }
                }if ((int)product.getItem().getPosicion() == product.getCuerpo().replaceAll("\\s", "").indexOf("$")){
                     tablaParseo.add(new ItemTablaParseo(i,"$","accept",1));
                }
                
            }
            
        }
        String acum = "\t" + " ";
         ArrayList<String> alfabeto = new ArrayList(LR.getAlfabeto());
          alfabeto.add("$");
        for (String letra: alfabeto){
           acum += letra + "\t";
        }
        System.out.println(acum);
        String tabla = "";
        int anterior = 0;
       for (int k = 0;k<tablaParseo.size();k++){
           
           if (k == 0){
               tabla += (int)tablaParseo.get(k).getActualEstado();
           }
           boolean espacio =false;
       
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

}
