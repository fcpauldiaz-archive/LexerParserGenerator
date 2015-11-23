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
    private T lookahead;
    
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

    public Produccion(T cabeza, T cuerpo, Item item, T lookahead) {
        this.cabeza = cabeza;
        this.cuerpo = cuerpo;
        this.item = item;
        this.lookahead = lookahead;
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

    public T getLookahead() {
        return lookahead;
    }

    public void setLookahead(T lookahead) {
        this.lookahead = lookahead;
    }
    
    
    
    @Override
    public String toString() {
        if (lookahead == null)
            return cabeza + " => " + cuerpoItem();
        return cabeza + " => " + cuerpoItem()+":"+lookahead;
    }
    
    public String cuerpoItem(){
        String returnString = "";
        String[] parts = this.cuerpo.toString().split(" ");
        for (int i = 0;i<parts.length;i++){
            if (i == (int)item.getPosicion())
                    returnString += "•";
            if (!parts[i].isEmpty()){
                returnString += parts[i].replace("\\s", "") + " ";
               
            }
            
        }
        if (cuerpo.toString().length()-countSpaces(cuerpo.toString())==(int)item.getPosicion())
            returnString = cuerpo.toString()+"•";
        
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

