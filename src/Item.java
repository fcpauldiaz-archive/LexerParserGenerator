/**
* Universidad Del Valle de Guatemala
* 07-nov-2015
* Pablo Díaz 13203
*/



import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author Pablo
 * @param <T>
 */
public class Item<T> implements Serializable{
    
    private T posicion;
    private boolean kernel;

    public Item(T posicion, boolean kernel) {
        this.posicion = posicion;
        this.kernel = kernel;
    }

    public Item(T posicion) {
        this.posicion = posicion;
    }

    public T getPosicion() {
        return posicion;
    }

    public void setPosicion(T posicion) {
        this.posicion = posicion;
    }

    public boolean isKernel() {
        return kernel;
    }

    public void setKernel(boolean kernel) {
        this.kernel = kernel;
    }

    @Override
    public String toString() {
        return "Item{" + "posicion=" + posicion + ", kernel=" + kernel + '}';
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + Objects.hashCode(this.posicion);
        return hash;
    }

     
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Item<?> other = (Item<?>) obj;
        if (!Objects.equals(this.posicion, other.posicion)) {
            return false;
        }
        return true;
    }
    
    public Item clonar(){
        return new Item(getPosicion(),isKernel());
    }
  

}
