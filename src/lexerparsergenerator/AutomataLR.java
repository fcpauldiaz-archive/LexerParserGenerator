/**
* Universidad Del Valle de Guatemala
* 07-nov-2015
* Pablo Díaz 13203
*/

package lexerparsergenerator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Stack;
import java.util.TreeSet;

/**
 *
 * @author Pablo
 */
public class AutomataLR {
    
    private final ArrayList<Produccion> producciones;

    public AutomataLR(ArrayList<Produccion> producciones) {
        this.producciones = producciones;
    }
    
    
    
    public void construct() throws CloneNotSupportedException{
       HashSet closureInicial =  closure(producciones.get(0));
       System.out.println(closureInicial);
       Automata LR = new Automata();
       Estado inicial = new Estado(closureInicial);
       HashSet<HashSet<Produccion>> acumuladorEstados = new HashSet();
       LR.addEstados(inicial);
       LR.setInicial(inicial);
       Stack<Estado> pila = new Stack();
       pila.add(inicial);
        int dummy = 0;
       while (!pila.isEmpty()){
           if (dummy == 1)
               System.out.println("");
           Estado actual = pila.pop();
           HashSet<String> alfabeto = calcularAlfabeto(actual);
           for (String letra: alfabeto){
              ArrayList<Produccion> search = searchProductionCuerpo(letra, (HashSet)actual.getId());
              HashSet estadosNuevos = new HashSet();
              for (int j = 0;j<search.size();j++){
                  if ((int)search.get(j).getItem().getPosicion()==search.get(j).getCuerpo().replaceAll("\\s","").indexOf(letra)){
                      
                       Produccion modificar = (Produccion)search.get(j).clonar();
                      
                      modificar.getItem().setPosicion((int)modificar.getItem().getPosicion()+letra.length());
                      System.out.println(modificar);
                      estadosNuevos.add(modificar);
                  }
              }
              if (!acumuladorEstados.contains(estadosNuevos)&&!estadosNuevos.isEmpty()){
                acumuladorEstados.add(estadosNuevos);
                Estado nuevo = new Estado(estadosNuevos);
                actual.setTransiciones(new Transicion(actual,nuevo,letra));
                LR.addEstados(nuevo);
               pila.push(nuevo);
              }
           }
           dummy++;
           
       }
       
       
        System.out.println(LR);
        System.out.println(LR.getEstados().get(0).getTransiciones().get(0));
    }
    
    public HashSet calcularAlfabeto(Estado estadoActual){
        HashSet<Produccion> prod = (HashSet)estadoActual.getId();
        HashSet alfabeto = new HashSet();
        for (Produccion produccion : prod) {
            String[] parts = produccion.getCuerpo().split(" ");
            for (String part : parts) {
                if (!part.equals("$")) {
                    alfabeto.add(part);
                }
            }
        }
        return alfabeto;
    }
    
    public HashSet closure(Produccion I){
        HashSet resultado = new HashSet();
        resultado.add(I);
        String[] parts = I.getCuerpo().split(" ");
        if ((int)I.getItem().getPosicion()==0){
            if (!this.terminal(parts[0])){
                ArrayList<Produccion> innerProd = searchProductions(parts[0]);
                for (int j = 0;j<innerProd.size();j++){
                    resultado.addAll(closure(innerProd.get(j)));
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

}
