/**
* Universidad Del Valle de Guatemala
* 14-nov-2015
* Pablo Díaz 13203
*/

package lexerparsergenerator;

/**
 *
 * @author Pablo
 */
public class ItemTablaParseo<T> {

    private T actualEstado;
    private T simbolo;
    private T operacion;
    private T nextEstado;

    public ItemTablaParseo(T actualEstado, T simbolo, T operacion) {
        this.actualEstado = actualEstado;
        this.simbolo = simbolo;
        this.operacion = operacion;
    }

   
    
    public ItemTablaParseo(T actualEstado, T simbolo, T operacion, T nextEstado) {
        this.actualEstado = actualEstado;
        this.simbolo = simbolo;
        this.operacion = operacion;
        this.nextEstado = nextEstado;
    }
    

    public T getActualEstado() {
        return actualEstado;
    }

    public void setActualEstado(T actualEstado) {
        this.actualEstado = actualEstado;
    }

    public T getSimbolo() {
        return simbolo;
    }

    public void setSimbolo(T simbolo) {
        this.simbolo = simbolo;
    }

    public T getNextEstado() {
        return nextEstado;
    }

    public void setNextEstado(T nextEstado) {
        this.nextEstado = nextEstado;
    }

    public T getOperacion() {
        return operacion;
    }

    public void setOperacion(T operacion) {
        this.operacion = operacion;
    }

    @Override
    public String toString() {
        String returnString;
        returnString = "\t" + operacion + nextEstado;
        
        
        return returnString;
    }
     
    public String toString(boolean espacio) {
        String returnString ="";
        
        
        returnString = "\t" + operacion + nextEstado;
        if (espacio)
            returnString += "\t";
        
        
        return returnString;
    }
    
    
    
}
