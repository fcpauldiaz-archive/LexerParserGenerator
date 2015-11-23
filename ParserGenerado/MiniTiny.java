/**
 * Nombre del archivo: MiniTiny.java
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

public class MiniTiny {

	private Simulacion sim = new Simulacion();
	private ArrayList<Automata> automatas = new ArrayList();
	private HashMap<Integer,String> input;
	private ArrayList keywords = new ArrayList();
	private String ignoreSets = " |	";
	private ArrayList<Token> tokensAcumulados = new ArrayList();
	private ArrayList<Token> tokens = new ArrayList();
	private String tk  = "";
	private Errors errores = new Errors();

	public MiniTiny(HashMap input){
		this.input=input;
	
	}
	public void automatas(){

		AFNConstruct ThomsonAlgorithim_0 = new AFNConstruct();
		Automata temp_0 =ThomsonAlgorithim_0.afnSimple("/");
		temp_0.setTipo("div");
		automatas.add(temp_0);

		RegexConverter convert_1= new RegexConverter();
		String regex_1 = convert_1.infixToPostfix("≤0∫1∫2∫3∫4∫5∫6∫7∫8∫9≥≤≤0∫1∫2∫3∫4∫5∫6∫7∫8∫9≥≥∞");
		AFNConstruct ThomsonAlgorithim_1 = new AFNConstruct(regex_1);
		ThomsonAlgorithim_1.construct();
		Automata temp_1 = ThomsonAlgorithim_1.getAfn();
		temp_1.setTipo("number");
		automatas.add(temp_1);

		AFNConstruct ThomsonAlgorithim_2 = new AFNConstruct();
		Automata temp_2 =ThomsonAlgorithim_0.afnSimple("-");
		temp_2.setTipo("res");
		automatas.add(temp_2);

		AFNConstruct ThomsonAlgorithim_3 = new AFNConstruct();
		Automata temp_3 =ThomsonAlgorithim_0.afnSimple("(");
		temp_3.setTipo("op");
		automatas.add(temp_3);

		AFNConstruct ThomsonAlgorithim_4 = new AFNConstruct();
		Automata temp_4 =ThomsonAlgorithim_0.afnSimple(";");
		temp_4.setTipo("pc");
		automatas.add(temp_4);

		RegexConverter convert_5= new RegexConverter();
		String regex_5 = convert_5.infixToPostfix(":=");
		AFNConstruct ThomsonAlgorithim_5 = new AFNConstruct(regex_5);
		ThomsonAlgorithim_5.construct();
		Automata temp_5 = ThomsonAlgorithim_5.getAfn();
		temp_5.setTipo("assignop");
		automatas.add(temp_5);

		AFNConstruct ThomsonAlgorithim_6 = new AFNConstruct();
		Automata temp_6 =ThomsonAlgorithim_0.afnSimple("*");
		temp_6.setTipo("mul");
		automatas.add(temp_6);

		AFNConstruct ThomsonAlgorithim_7 = new AFNConstruct();
		Automata temp_7 =ThomsonAlgorithim_0.afnSimple("<");
		temp_7.setTipo("lt");
		automatas.add(temp_7);

		AFNConstruct ThomsonAlgorithim_8 = new AFNConstruct();
		Automata temp_8 =ThomsonAlgorithim_0.afnSimple("+");
		temp_8.setTipo("sum");
		automatas.add(temp_8);

		RegexConverter convert_9= new RegexConverter();
		String regex_9 = convert_9.infixToPostfix("≤a∫b∫c∫d∫e∫f∫g∫h∫i∫j∫k∫l∫m∫n∫o∫p∫q∫r∫s∫t∫u∫v∫w∫x∫y∫z∫A∫B∫C∫D∫E∫F∫G∫H∫I∫J∫K∫L∫M∫N∫O∫P∫Q∫R∫S∫T∫U∫V∫W∫X∫Y∫Z≥≤≤a∫b∫c∫d∫e∫f∫g∫h∫i∫j∫k∫l∫m∫n∫o∫p∫q∫r∫s∫t∫u∫v∫w∫x∫y∫z∫A∫B∫C∫D∫E∫F∫G∫H∫I∫J∫K∫L∫M∫N∫O∫P∫Q∫R∫S∫T∫U∫V∫W∫X∫Y∫Z≥≥∞");
		AFNConstruct ThomsonAlgorithim_9 = new AFNConstruct(regex_9);
		ThomsonAlgorithim_9.construct();
		Automata temp_9 = ThomsonAlgorithim_9.getAfn();
		temp_9.setTipo("id");
		automatas.add(temp_9);

		AFNConstruct ThomsonAlgorithim_10 = new AFNConstruct();
		Automata temp_10 =ThomsonAlgorithim_0.afnSimple("=");
		temp_10.setTipo("eq");
		automatas.add(temp_10);

		AFNConstruct ThomsonAlgorithim_11 = new AFNConstruct();
		Automata temp_11 =ThomsonAlgorithim_0.afnSimple(")");
		temp_11.setTipo("cp");
		automatas.add(temp_11);
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
