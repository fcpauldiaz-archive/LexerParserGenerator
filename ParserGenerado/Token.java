/**
* Universidad Del Valle de Guatemala
* 05-oct-2015
* Pablo Díaz 13203
*/
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.TreeMap;
import java.io.Serializable;
/**
 *
 * @author Pablo
 * @param <T>
 */
public class Token<T> implements Serializable {
	private T id;
	private T lexema;
	private ArrayList keywords = new ArrayList();
	private HashSet<Token> tokens = new HashSet();
	private TreeMap<String,String> keyMap = new TreeMap();
	public Token(T id, T lexema,boolean revisarKey) {
		if (revisarKey)
			keyWords();
		ArrayList var = revisarKeywords(id,lexema);
		this.id = (T) var.get(0);
		this.lexema = (T) var.get(1);
	}
	public T getId() {
		return id;
	}
	public void setId(T id) {
		this.id = id;
	}
	public T getLexema() {
		return lexema;
	}
	public void setLexema(T lexema) {
		this.lexema = lexema;
	}
	public ArrayList revisarKeywords(T id, T lexema){
		ArrayList returnArray = new ArrayList();
		if (keyMap.containsKey((String)lexema)){
			returnArray.add(keyMap.get((String)lexema));
			returnArray.add(lexema);
			return returnArray;
		}
		returnArray.add(id);
		returnArray.add(lexema);
		return returnArray;
	}
	public void keyWords(){
	}
	@Override
	public String toString() {
		return "<" + id + ", \"" + lexema + "\">";
	}
	@Override
	public int hashCode() {
		int hash = 3;
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
		final Token<?> other = (Token<?>) obj;
		if (!Objects.equals(this.id, other.id)) {
			return false;
		}
		if (!Objects.equals(this.lexema, other.lexema)) {
			return false;
		}
			return false;
	}
}

