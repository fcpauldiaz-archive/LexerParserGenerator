/**
* Universidad Del Valle de Guatemala
* Pablo Díaz 13203
* 29/07/2015
*/




import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Conversión de una xregular infix a postfix
 * @author GMenard
 * Reference: https://gist.github.com/gmenard/6161825
 * Modified by
 * @author fcpauldiaz
 * @since 29/07/2015
 * Includes abreviatures to reduce operators
 */
public class RegexConverter {
    
    /** Mapa de precedencia de los operadores. */
	private final Map<Character, Integer> precedenciaOperadores;
    public final char charKleene = '∞';
    public final char charConcat = '∆';
    public final char charAbrirParentesis = '≤';
    public final char charCerrarParentesis = '≥';
    public final char charOr = '∫';
    public final char charPlus = '∩';
    public final char charInt = 'Ω';
    
        //constructor
	public RegexConverter()
        {
		Map<Character, Integer> map = new HashMap<>();
		map.put(charAbrirParentesis, 1); // parentesis
		map.put(charOr, 2); // Union o or
		map.put(charConcat, 3); // explicit concatenation operator
		map.put(charKleene, 4); // kleene
		map.put(charPlus, 4); // positivo
		precedenciaOperadores = Collections.unmodifiableMap(map);
        //(((!∫@∫#)∫(%∫&∫)∫(*∫+∫,∫-∫.)∫(@∫A∫B∫C∫D∫E∫F∫G∫H∫I∫J∫K∫L∫M∫N∫O∫P∫Q∫R∫S∫T∫U∫V∫W∫X∫Y∫Z)∫(^∫_∫`∫a∫b∫c∫d∫e∫f∫g∫h∫i∫j∫k∫l∫m∫n∫o∫p∫q∫r∫s∫t∫u∫v∫w∫x∫y      
	};
        
	/**
	 * Obtener la precedencia del caracter
	 * 
	 * @param c character
	 * @return corresponding precedence
	 */
	private Integer getPrecedencia(Character c) {
		Integer precedencia = precedenciaOperadores.get(c);
                //si obtiene un valor nulo retrona 6 por default
		return precedencia == null ? 6 : precedencia;
	}

        
        /**
         * Insertar caracter en una posicion deseada
         * @param s string deseado
         * @param pos indice del caracter
         * @param ch caracter  o String deseado
         * @return nuevo string con el caracter deseado
         */
        private String insertCharAt(String s, int pos, Object ch){
            return s.substring(0,pos)+ch+s.substring(pos+1);
        }
        /**
         * Agregar caracter en la posicion deseada (no elimina el caracter anterior)
         * @param s string deseado
         * @param pos posicion del caracter
         * @param ch caracter deseado
         * @return nuevo string con el caracter agregado
         */
        private String appendCharAt(String s, int pos, Object ch){
            String val = s.substring(pos,pos+1);
            return s.substring(0,pos)+val+ch+s.substring(pos+1);
            
        }
        
         /**
         * Metodo para abreviar el operador ? 
         * equivalente a |€
         * @param regex expresion regular
         * @return expresion regular modificada sin el operador ?
         */
        public String abreviaturaInterrogacion(String regex)
        {   
            for (int i = 0; i<regex.length();i++){
                Character ch = regex.charAt(i);
                 
                if (ch.equals(charInt))
                {
                    if (regex.charAt(i-1) == charCerrarParentesis)
                    {
                        regex = insertCharAt(regex,i,charOr+""+resultadoGeneradorMain.EPSILON+charCerrarParentesis);
                        
                        int j =i;
                        while (j!=0)
                        {
                            if (regex.charAt(j)==charAbrirParentesis)
                            {
                                break;
                            }
                            
                        j--;
                        
                        }
                        
                        regex=appendCharAt(regex,j,charAbrirParentesis);
                         
                    }
                    else
                    {
                        regex = insertCharAt(regex,i,charOr+resultadoGeneradorMain.EPSILON+charCerrarParentesis);
                        regex = insertCharAt(regex,i-1,charAbrirParentesis+""+regex.charAt(i-1));
                    }
                }
            }
            //regex = balancearParentesis(regex);
            return regex;
        }
      
        
        /**
         * Método para contar los parentesis izquierdos '('
         * @param regex String expresion regular
         * @return int contador
         */
        private int parentesisIzq (String regex){
            int P1=0;
            for (int i = 0;i<regex.length();i++){
                Character ch = regex.charAt(i);
                if (ch.equals(charAbrirParentesis)){
                    P1++;
                }
              
            }
            return P1;
        }
        /**
         * Método para contar los parentesis derechos ')'
         * @param regex String expresion regular
         * @return int contador 
         */
        private int parentesisDer (String regex){
            int P1=0;
             for (int i = 0;i<regex.length();i++){
                Character ch = regex.charAt(i);
                if (ch.equals(charCerrarParentesis)){
                    P1++;
                }
            }
            return P1;
        }
        /**
         * Método para balancear parentesis en caso de que esté mal ingresada
         * la expresión regular
         * @param regex String expresión regular
         * @return String expresion regular modificada
         */
        private String balancearParentesis(String regex){
            //corregir parentesis de la expresion en caso que no esten balanceados
            int P1 = parentesisIzq(regex);
            int P2 = parentesisDer(regex);
            
            
            while(P1 != P2){
                if (P1>P2)
                    regex +=charCerrarParentesis;
                if (P2>P1)
                    regex =charAbrirParentesis + regex;
                P1 = parentesisIzq(regex);
                P2 = parentesisDer(regex);
            }
            return regex;
        }
        
        /**
         * Método para abreviar el operador de cerradura positiva
         * @param regex expresion regular (string)
         * @return expresion regular modificada sin el operador +
         */
        public String abreviaturaCerraduraPositiva(String regex){
            //sirve para buscar el '(' correcto cuando  hay () en medio
            // de la cerradura positiva
            int compare = 0; 
            
            for (int i = 0; i<regex.length();i++){
                 Character ch = regex.charAt(i);
                 
                if (ch.equals(charPlus))
                {
                    //si hay un ')' antes de un operador
                    //significa que hay que buscar el '(' correspondiente
                    if (regex.charAt(i-1) == charCerrarParentesis){
                        
                        int fixPosicion = i;
                        
                        while (fixPosicion != -1)
                        {
                            if (regex.charAt(fixPosicion)==charCerrarParentesis)
                            {
                               compare++;
                               
                            }
                            
                            if (regex.charAt(fixPosicion)==charAbrirParentesis)
                            {
                                
                                compare--;
                                if (compare ==0)
                                    break;
                            }
                            
                            
                        fixPosicion--;
                        
                        }
                      
                        String regexAb = regex.substring(fixPosicion,i);
                        regex = insertCharAt(regex,i,regexAb+charKleene);
                        
                      
                    }
                    //si no hay parentesis, simplemente se inserta el caracter
                    else
                    {
                        regex = insertCharAt(regex,i,regex.charAt(i-1)+charKleene);
                    }
                    
                   
                }
                
            }
           
            //regex = balancearParentesis(regex);
            
            return regex;
        }
	/**
	 * 
         * Transformar una expresión regular insertando un punto '.' explicitamente
         * como operador de concatenación.
         * @param regex String
         * @return regexExplicit String 
	 */
	public  String formatRegEx(String regex) {
        regex = regex.trim();
        regex = abreviaturaCerraduraPositiva(regex);
        regex = abreviaturaInterrogacion(regex);
		String  regexExplicit = new String();
		List<Character> operadores = Arrays.asList(charOr, charPlus, charKleene);
		List<Character> operadoresBinarios = Arrays.asList(charOr);
                
                
                //recorrer la cadena
		for (int i = 0; i < regex.length(); i++)
                {
                    Character c1 = regex.charAt(i);
                   
                    if (i + 1 < regex.length()) 
                    {
                        
                        Character c2 = regex.charAt(i + 1);
                        
                        regexExplicit += c1;
                        
                        //mientras la cadena no incluya operadores definidos, será una concatenación implicita
                        if (!c1.equals(charAbrirParentesis) && !c2.equals(charCerrarParentesis) && !operadores.contains(c2) && !operadoresBinarios.contains(c1))
                        {
                            regexExplicit += this.charConcat;
                           
                        }
                        
                    }
		}
		regexExplicit += regex.charAt(regex.length() - 1);
                

		return regexExplicit;
	}
        
        public String abreviacionOr(String regex){
            String resultado = new String();
            try{        
            for (int i=0;i<regex.length();i++){
                Character ch = regex.charAt(i);
                if (ch =='[' ){
                    if (regex.charAt(i+2)=='-'){
                        int inicio = regex.charAt(i+1);
                        int fin = regex.charAt(i+3);
                        resultado +=charAbrirParentesis;
                        for (int j = 0;j<=fin-inicio;j++)
                        {
                            if (j==(fin-inicio))
                                resultado+= Character.toString((char)(inicio+j));
                            else
                             resultado+= Character.toString((char)(inicio+j))+charOr;
                        }
                        resultado +=charCerrarParentesis;
                        i=i+4;
                    }
                    else{
                        resultado +=ch;
                    }
                }
                else{
                    resultado+=ch;
                }
                
            }
            } catch (Exception e){
                System.out.println("Error en la conversión " + regex);
                resultado = " ";
            }
            
            return resultado;
        }
        
          public String abreviacionAnd(String regex){
            String resultado = new String();
           try{         
            for (int i=0;i<regex.length();i++){
                Character ch = regex.charAt(i);
                if (ch =='[' ){
                    if (regex.charAt(i+2)==this.charConcat){
                        int inicio = regex.charAt(i+1);
                        int fin = regex.charAt(i+3);
                        resultado +=charAbrirParentesis;
                        for (int j = 0;j<=fin-inicio;j++)
                        {
                           
                            resultado+= Character.toString((char)(inicio+j));
                        }
                        resultado +=charCerrarParentesis;
                        i=i+4;
                    }
                }
                else{
                    resultado+=ch;
                }
                //System.out.println(resultado);
            }
           }catch (Exception e){
               System.out.println("Error en la conversion "+regex);
               resultado = "(a|b)*abb";
           }
            return resultado;
        }
        
        
        /**
	 * Convertir una expresión regular de notación infix a postfix 
	 * con el algoritmo de Shunting-yard. 
	 * 
	 * @param regex notacion infix 
	 * @return notacion postfix 
	 */
	public  String infixToPostfix(String regex) {
        
         if (regex.length()<=2)
            return regex;

		String postfix = new String();
        //regex = abreviacionOr(regex);
        //regex = abreviacionAnd(regex);
		Stack<Character> stack = new Stack<>();
      
		String formattedRegEx = formatRegEx(regex);
        
		for (int i = 0;i<formattedRegEx.length();i++) {
            Character c = formattedRegEx.charAt(i);
			switch (c) {
                case charAbrirParentesis:
					stack.push(c);
					break;

				case charCerrarParentesis:
					while (!stack.peek().equals(charAbrirParentesis)) {
						postfix += stack.pop();
					}
					stack.pop();
					break;

				default:
					while (stack.size() > 0) 
                                        {
						Character peekedChar = stack.peek();

						Integer peekedCharPrecedence = getPrecedencia(peekedChar);
						Integer currentCharPrecedence = getPrecedencia(c);

						if (peekedCharPrecedence >= currentCharPrecedence) 
                                                {
							postfix += stack.pop();
                                                       
						} 
                        else 
                        {
							break;
						}
					}
					stack.push(c);
					break;
			}

		}

		while (stack.size() > 0)
			postfix += stack.pop();

		return postfix;
	}

}
