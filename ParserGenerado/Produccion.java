/**
* Universidad Del Valle de Guatemala
* 29-oct-2015
* Pablo Díaz 13203
*/



import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author Pablo
 * @param <T>
 */
public class Produccion<T> implements Serializable{

    private T cabeza;
    private T cuerpo;
    private Item item;
    
    public Produccion(){
        
    }
    
    
    public Produccion(T cabeza, T cuerpo) {
        this.cabeza = cabeza;
        this.cuerpo = cuerpo;
        this.item = new Item(0);
        
    }

    public Produccion(T cabeza, T cuerpo, Item item) {
        this.cabeza = cabeza;
        this.cuerpo = cuerpo;
        this.item = item.clonar();
    }
    
    

    public String getCabeza() {
        return cabeza.toString().trim();
    }

    public void setCabeza(T cabeza) {
        this.cabeza = cabeza;
    }

    public String getCuerpo() {
        return cuerpo.toString().trim();
    }

    public void setCuerpo(T cuerpo) {
        this.cuerpo = cuerpo;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + Objects.hashCode(this.cabeza);
        hash = 23 * hash + Objects.hashCode(this.cuerpo);
        hash = 23 * hash + Objects.hashCode(this.item);
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
        final Produccion<?> other = (Produccion<?>) obj;
        if (!Objects.equals(this.cabeza, other.cabeza)) {
            return false;
        }
        if (!Objects.equals(this.cuerpo, other.cuerpo)) {
            return false;
        }
        if (!Objects.equals(this.item, other.item)) {
            return false;
        }
        return true;
    }

   
   

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }
    
    public Produccion clonar(){
        return new Produccion(getCabeza(),getCuerpo(),getItem());
    }
    
    public boolean isRecursivaIzquierda(){
        String[] parts = cuerpo.toString().split(" ");
        return cabeza.toString().trim().equals(parts[0].trim());
    }
    
    
    
    @Override
    public String toString() {
        return cabeza + " => " + cuerpoItem();
    }
    
    public String cuerpoItem(){
        String returnString = "";
        for (int i = 0;i<cuerpo.toString().replaceAll("\\s", "").length();i++){
            Character ch = cuerpo.toString().replaceAll("\\s", "").charAt(i);
            if (i == (int)item.getPosicion()){
                returnString += "•";
            }
            returnString += ch;
        }
       
        if (cuerpo.toString().length()-countSpaces(cuerpo.toString())==(int)item.getPosicion())
            returnString += "•";
        
        return returnString;
    }
    public int countSpaces(String string) {
        int spaces = 0;
        for(int i = 0; i < string.length(); i++) {
            spaces += (Character.isWhitespace(string.charAt(i))) ? 1 : 0;
        }
        return spaces;
    }
}

