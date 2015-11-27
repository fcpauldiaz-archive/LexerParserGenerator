/**
 * Nombre del archivo: TestLR1.java
 * Universidad del Valle de Guatemala
 * Pablo Diaz 13203 
 * Descripción: Segundo proyecto. Generador de analizador léxico
**/

import java.util.Map;
import java.util.HashSet;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.HashMap;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class TestLR1 {

	private Simulacion sim = new Simulacion();
	private ArrayList<Automata> automatas = new ArrayList();
	private HashMap<Integer,String> input;
	private ArrayList keywords = new ArrayList();
	private String ignoreSets = " |	";
	private ArrayList<Token> tokensAcumulados = new ArrayList();
	private ArrayList<Token> tokens = new ArrayList();
	private String tk  = "";
	private Errors errores = new Errors();

	public TestLR1(HashMap input){
		this.input=input;
	
	}
	public void automatas(){

		AFNConstruct ThomsonAlgorithim_0 = new AFNConstruct();
		Automata temp_0 =ThomsonAlgorithim_0.afnSimple("a");
		temp_0.setTipo("a");
		automatas.add(temp_0);

		AFNConstruct ThomsonAlgorithim_1 = new AFNConstruct();
		Automata temp_1 =ThomsonAlgorithim_0.afnSimple("b");
		temp_1.setTipo("b");
		automatas.add(temp_1);

		AFNConstruct ThomsonAlgorithim_2 = new AFNConstruct();
		Automata temp_2 =ThomsonAlgorithim_0.afnSimple("c");
		temp_2.setTipo("c");
		automatas.add(temp_2);

		AFNConstruct ThomsonAlgorithim_3 = new AFNConstruct();
		Automata temp_3 =ThomsonAlgorithim_0.afnSimple("d");
		temp_3.setTipo("d");
		automatas.add(temp_3);
	}
	 /**
 	* Método para revisar que tipo de sub autómata es aceptado por una 
	* expresión regular
	* @param regex expresión regular a comparar
	* @param conjunto arreglo de autómatas
	*/
	public void checkIndividualAutomata(String regex, ArrayList<Automata> conjunto,int lineaActual){
		ArrayList<Boolean> resultado = new ArrayList();
		ArrayList tks = tokenMasLargo(regex, conjunto, lineaActual);
		if (!(Boolean)tks.get(0)&&!((String)tks.get(1)).isEmpty()){
			errores.LexErr(lineaActual,regex.indexOf((String)tks.get(1)),regex,(String)tks.get(1));
		}
	}
	/**
	 * Método que devuelve las posiciones en las que el valor que tiene 
	 * en cada posicion es true
	 * @param bool arreglo de booleanos
	 * @return arreglo de enteros
	 */
	public ArrayList<Integer>  checkBoolean(ArrayList<Boolean> bool){
		ArrayList<Integer> posiciones = new ArrayList();
		for (int i = 0;i<bool.size();i++){
			if (bool.get(i))
				posiciones.add(i);
		}
		return posiciones;
	}

	public void revisar(){
		for (Map.Entry<Integer, String> entry : input.entrySet()) {
			Integer key = entry.getKey();
			String value = entry.getValue();
			String[] parts = value.split(ignoreSets);
			tokens.clear();
			tk="";
			for (String part : parts) {
			 this.checkIndividualAutomata(part + "", automatas, key);
			}
		System.out.println(tokens);
		tokensAcumulados.addAll(tokens);
		}
	try
	{
		FileOutputStream fileOut =
		new FileOutputStream("../ParserGenerado/Tokens.ser");
		ObjectOutputStream out = new ObjectOutputStream(fileOut);
		out.writeObject(tokensAcumulados);
		out.close();
		fileOut.close();
		System.out.printf("Serialized data is saved in /ParserGenerado/Tokens.ser");
	}catch(IOException i)
	{
		i.printStackTrace();
	}
	}


	/**
	 * Método para reconocer los tokens más simples
	 * @param regex
	 * @param conjuntoAut
	 * @return boolean true si se crea un token, false si no es aceptado por ninguno.
	 */
	public boolean tokenSimple(String regex,ArrayList<Automata> conjuntoAut){
		TreeMap<String,Automata> aceptados = new TreeMap(new Comparator<String>() {
		@Override
		public int compare(String o1, String o2) {
			Integer a1 = o1.length();
			Integer a2 = o2.length();
			return a2-a1;
		}
		});

		for (Automata automata: conjuntoAut){
			if (sim.simular(regex,automata)){
				aceptados.put(regex, automata);

			}
		}
		if (!aceptados.isEmpty()){
			tokens.add(new Token(aceptados.firstEntry().getValue().getTipo(),aceptados.firstKey(),aceptados.firstEntry().getValue().isExceptKeywords()));

			return true;
		}

		return false;
	}

	/**
	* Método para simular y reconocer el caracter más largo
	* 
	* @param regex recibe la cadena a simular
	* @param conjuntoAut 
	* @param lineaActual linea del regex en el archivo 
	* @return boolean si es falso no fue reconocido nada
	*/
	public ArrayList tokenMasLargo(String regex, ArrayList<Automata> conjuntoAut, int lineaActual)
	{   
		boolean tokenSimple = tokenSimple(regex,conjuntoAut);

		if (tokenSimple){
			ArrayList casoBase = new ArrayList();
			casoBase.add(true);
			casoBase.add(regex);
			return casoBase;

		}
		while (tokens.isEmpty()||!regex.isEmpty()){

			if (!tokenSimple(regex,conjuntoAut)){

				try{
					tk = regex.substring(regex.length()-1)+tk;

					ArrayList returnBool = tokenMasLargo(regex.substring(0,regex.length()-1),conjuntoAut, lineaActual);

					if (!(Boolean)returnBool.get(0)){
						ArrayList casoRecursivo = new ArrayList();
						casoRecursivo.add(false);
						casoRecursivo.add((String)returnBool.get(1));
						return casoRecursivo;
					}
					if ((Boolean)returnBool.get(0)){
						regex = tk;
						tk = "";
					}
				}catch(Exception e){

					ArrayList casoRecursivo = new ArrayList();
					casoRecursivo.add(false);
					casoRecursivo.add(tk);
					return casoRecursivo;
				}//cierra catch
			}//cierra !if
			else{
			 ArrayList casoRecusivo = new ArrayList();
			casoRecusivo.add(true);
			return casoRecusivo;
		}
		}//cierra while

		if (regex.isEmpty()&&!tk.isEmpty()){
			 ArrayList casoRecusivo = new ArrayList();
			  casoRecusivo.add(false);
			casoRecusivo.add(tk);
			 return casoRecusivo;
		}

		ArrayList casoRecusivo = new ArrayList();

		casoRecusivo.add(false);

		casoRecusivo.add(regex);

		return  casoRecusivo;

	}
}
