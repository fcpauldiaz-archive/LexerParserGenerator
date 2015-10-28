/**
 * Nombre del archivo: Lexer.java
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

public class Lexer {

	private Simulacion sim = new Simulacion();
	private ArrayList<Automata> automatas = new ArrayList();
	private HashMap<Integer,String> input;
	private ArrayList keywords = new ArrayList();
	private String ignoreSets = " ";
	private ArrayList<Token> tokensAcumulados = new ArrayList();
	private ArrayList<Token> tokens = new ArrayList();
	private String tk  = "";
	private Errors errores = new Errors();

	public Lexer(HashMap input){
		this.input=input;
	
	}
	public void automatas(){

		AFNConstruct ThomsonAlgorithim_0 = new AFNConstruct();
		Automata temp_0 =ThomsonAlgorithim_0.afnSimple("-");
		temp_0.setTipo("minus");
		automatas.add(temp_0);

		RegexConverter convert_1= new RegexConverter();
		String regex_1 = convert_1.infixToPostfix("--");
		AFNConstruct ThomsonAlgorithim_1 = new AFNConstruct(regex_1);
		ThomsonAlgorithim_1.construct();
		Automata temp_1 = ThomsonAlgorithim_1.getAfn();
		temp_1.setTipo("dec");
		automatas.add(temp_1);

		AFNConstruct ThomsonAlgorithim_2 = new AFNConstruct();
		Automata temp_2 =ThomsonAlgorithim_0.afnSimple("(");
		temp_2.setTipo("lpar");
		automatas.add(temp_2);

		AFNConstruct ThomsonAlgorithim_3 = new AFNConstruct();
		Automata temp_3 =ThomsonAlgorithim_0.afnSimple(".");
		temp_3.setTipo("dot");
		automatas.add(temp_3);

		RegexConverter convert_4= new RegexConverter();
		String regex_4 = convert_4.infixToPostfix("while");
		AFNConstruct ThomsonAlgorithim_4 = new AFNConstruct(regex_4);
		ThomsonAlgorithim_4.construct();
		Automata temp_4 = ThomsonAlgorithim_4.getAfn();
		temp_4.setTipo("while");
		automatas.add(temp_4);

		RegexConverter convert_5= new RegexConverter();
		String regex_5 = convert_5.infixToPostfix("float");
		AFNConstruct ThomsonAlgorithim_5 = new AFNConstruct(regex_5);
		ThomsonAlgorithim_5.construct();
		Automata temp_5 = ThomsonAlgorithim_5.getAfn();
		temp_5.setTipo("float");
		automatas.add(temp_5);

		AFNConstruct ThomsonAlgorithim_6 = new AFNConstruct();
		Automata temp_6 =ThomsonAlgorithim_0.afnSimple("~");
		temp_6.setTipo("tilde");
		automatas.add(temp_6);

		RegexConverter convert_7= new RegexConverter();
		String regex_7 = convert_7.infixToPostfix("≤≤≤≤≤0≥≥∫≤≤1∫2∫3∫4∫5∫6∫7∫8∫9≥≥≥≥∫≤≤A∫B∫C∫D∫E∫F∫a∫b∫c∫d∫e∫f≥≥≥≤hex≤≤≤0≥≥∫≤≤1∫2∫3∫4∫5∫6∫7∫8∫9≥≥≥≥∞(H)");
		AFNConstruct ThomsonAlgorithim_7 = new AFNConstruct(regex_7);
		ThomsonAlgorithim_7.construct();
		Automata temp_7 = ThomsonAlgorithim_7.getAfn();
		temp_7.setTipo("hexnumber");
		automatas.add(temp_7);

		RegexConverter convert_8= new RegexConverter();
		String regex_8 = convert_8.infixToPostfix("long");
		AFNConstruct ThomsonAlgorithim_8 = new AFNConstruct(regex_8);
		ThomsonAlgorithim_8.construct();
		Automata temp_8 = ThomsonAlgorithim_8.getAfn();
		temp_8.setTipo("long");
		automatas.add(temp_8);

		RegexConverter convert_9= new RegexConverter();
		String regex_9 = convert_9.infixToPostfix("≤≤≤0≥≥∫≤≤1∫2∫3∫4∫5∫6∫7∫8∫9≥≥≥≤≤≤≤0≥≥∫≤≤1∫2∫3∫4∫5∫6∫7∫8∫9≥≥≥≥∞∫≤≤≤0≥≥∫≤≤1∫2∫3∫4∫5∫6∫7∫8∫9≥≥≥≤≤≤≤0≥≥∫≤≤1∫2∫3∫4∫5∫6∫7∫8∫9≥≥≥≥∞.≤≤≤≤0≥≥∫≤≤1∫2∫3∫4∫5∫6∫7∫8∫9≥≥≥≥∞≤E≤+∫-≥Ω≤≤≤0≥≥∫≤≤1∫2∫3∫4∫5∫6∫7∫8∫9≥≥≥≤≤≤≤0≥≥∫≤≤1∫2∫3∫4∫5∫6∫7∫8∫9≥≥≥≥∞≥Ω");
		AFNConstruct ThomsonAlgorithim_9 = new AFNConstruct(regex_9);
		ThomsonAlgorithim_9.construct();
		Automata temp_9 = ThomsonAlgorithim_9.getAfn();
		temp_9.setTipo("number");
		automatas.add(temp_9);

		AFNConstruct ThomsonAlgorithim_10 = new AFNConstruct();
		Automata temp_10 =ThomsonAlgorithim_0.afnSimple("!");
		temp_10.setTipo("not");
		automatas.add(temp_10);

		RegexConverter convert_11= new RegexConverter();
		String regex_11 = convert_11.infixToPostfix(".≤≤≤0≥≥∫≤≤1∫2∫3∫4∫5∫6∫7∫8∫9≥≥≥≤≤≤≤0≥≥∫≤≤1∫2∫3∫4∫5∫6∫7∫8∫9≥≥≥≥∞≤≤e∫E≥≤+∫-≥Ω≤≤≤0≥≥∫≤≤1∫2∫3∫4∫5∫6∫7∫8∫9≥≥≥≤≤≤≤0≥≥∫≤≤1∫2∫3∫4∫5∫6∫7∫8∫9≥≥≥≥∞≥Ω≤F∫f∫D∫d≥Ω∫≤≤≤0≥≥∫≤≤1∫2∫3∫4∫5∫6∫7∫8∫9≥≥≥≤≤≤≤0≥≥∫≤≤1∫2∫3∫4∫5∫6∫7∫8∫9≥≥≥≥∞≤.≤≤≤≤0≥≥∫≤≤1∫2∫3∫4∫5∫6∫7∫8∫9≥≥≥≥∞≤≤e∫E≥≤+∫-≥Ω≤≤≤0≥≥∫≤≤1∫2∫3∫4∫5∫6∫7∫8∫9≥≥≥≤≤≤≤0≥≥∫≤≤1∫2∫3∫4∫5∫6∫7∫8∫9≥≥≥≥∞≥Ω≤F∫f∫D∫d≥Ω∫≤e∫E≥≤+∫-≥Ω≤≤≤0≥≥∫≤≤1∫2∫3∫4∫5∫6∫7∫8∫9≥≥≥≤≤≤≤0≥≥∫≤≤1∫2∫3∫4∫5∫6∫7∫8∫9≥≥≥≥∞≤F∫f∫D∫d≥Ω∫F∫f∫D∫d≥");
		AFNConstruct ThomsonAlgorithim_11 = new AFNConstruct(regex_11);
		ThomsonAlgorithim_11.construct();
		Automata temp_11 = ThomsonAlgorithim_11.getAfn();
		temp_11.setTipo("floatLit");
		automatas.add(temp_11);

		AFNConstruct ThomsonAlgorithim_12 = new AFNConstruct();
		Automata temp_12 =ThomsonAlgorithim_0.afnSimple("  ");
		temp_12.setTipo("WHITESPACE");
		automatas.add(temp_12);

		RegexConverter convert_13= new RegexConverter();
		String regex_13 = convert_13.infixToPostfix("≤≤0≥∫≤1∫2∫3∫4∫5∫6∫7∫8∫9≥≤≤≤≤0≥≥∫≤≤1∫2∫3∫4∫5∫6∫7∫8∫9≥≥≥≥∞∫≤0x∫0X≥≤≤≤≤≤0≥≥∫≤≤1∫2∫3∫4∫5∫6∫7∫8∫9≥≥≥≥∫≤≤A∫B∫C∫D∫E∫F∫a∫b∫c∫d∫e∫f≥≥≥≤≤≤≤≤≤0≥≥∫≤≤1∫2∫3∫4∫5∫6∫7∫8∫9≥≥≥≥∫≤≤A∫B∫C∫D∫E∫F∫a∫b∫c∫d∫e∫f≥≥≥≥∞∫0≤≤≤0≥≥∫≤≤1∫2∫3∫4∫5∫6∫7≥≥≥≤≤≤≤0≥≥∫≤≤1∫2∫3∫4∫5∫6∫7≥≥≥≥∞≥≤l∫L≥Ω");
		AFNConstruct ThomsonAlgorithim_13 = new AFNConstruct(regex_13);
		ThomsonAlgorithim_13.construct();
		Automata temp_13 = ThomsonAlgorithim_13.getAfn();
		temp_13.setTipo("intLit");
		automatas.add(temp_13);

		AFNConstruct ThomsonAlgorithim_14 = new AFNConstruct();
		Automata temp_14 =ThomsonAlgorithim_0.afnSimple(")");
		temp_14.setTipo("rpar");
		automatas.add(temp_14);

		RegexConverter convert_15= new RegexConverter();
		String regex_15 = convert_15.infixToPostfix("≤≤A∫B∫C∫D∫E∫F∫G∫H∫I∫J∫K∫L∫M∫N∫O∫P∫Q∫R∫S∫T∫U∫V∫W∫X∫Y∫Z≥∫≤a∫b∫c∫d∫e∫f∫g∫h∫i∫j∫k∫l∫m∫n∫o∫p∫q∫r∫s∫t∫u∫v∫w∫x∫y∫z≥∫+≥≤_≥∞*");
		AFNConstruct ThomsonAlgorithim_15 = new AFNConstruct(regex_15);
		ThomsonAlgorithim_15.construct();
		Automata temp_15 = ThomsonAlgorithim_15.getAfn();
		temp_15.setTipo("ident1");
		automatas.add(temp_15);

		RegexConverter convert_16= new RegexConverter();
		String regex_16 = convert_16.infixToPostfix("if");
		AFNConstruct ThomsonAlgorithim_16 = new AFNConstruct(regex_16);
		ThomsonAlgorithim_16.construct();
		Automata temp_16 = ThomsonAlgorithim_16.getAfn();
		temp_16.setTipo("if");
		automatas.add(temp_16);

		RegexConverter convert_17= new RegexConverter();
		String regex_17 = convert_17.infixToPostfix("class");
		AFNConstruct ThomsonAlgorithim_17 = new AFNConstruct(regex_17);
		ThomsonAlgorithim_17.construct();
		Automata temp_17 = ThomsonAlgorithim_17.getAfn();
		temp_17.setTipo("class");
		automatas.add(temp_17);

		RegexConverter convert_18= new RegexConverter();
		String regex_18 = convert_18.infixToPostfix("++");
		AFNConstruct ThomsonAlgorithim_18 = new AFNConstruct(regex_18);
		ThomsonAlgorithim_18.construct();
		Automata temp_18 = ThomsonAlgorithim_18.getAfn();
		temp_18.setTipo("inc");
		automatas.add(temp_18);

		RegexConverter convert_19= new RegexConverter();
		String regex_19 = convert_19.infixToPostfix("new");
		AFNConstruct ThomsonAlgorithim_19 = new AFNConstruct(regex_19);
		ThomsonAlgorithim_19.construct();
		Automata temp_19 = ThomsonAlgorithim_19.getAfn();
		temp_19.setTipo("new");
		automatas.add(temp_19);

		AFNConstruct ThomsonAlgorithim_20 = new AFNConstruct();
		Automata temp_20 =ThomsonAlgorithim_0.afnSimple("{");
		temp_20.setTipo("lbrace");
		automatas.add(temp_20);

		RegexConverter convert_21= new RegexConverter();
		String regex_21 = convert_21.infixToPostfix("static");
		AFNConstruct ThomsonAlgorithim_21 = new AFNConstruct(regex_21);
		ThomsonAlgorithim_21.construct();
		Automata temp_21 = ThomsonAlgorithim_21.getAfn();
		temp_21.setTipo("static");
		automatas.add(temp_21);

		RegexConverter convert_22= new RegexConverter();
		String regex_22 = convert_22.infixToPostfix("void");
		AFNConstruct ThomsonAlgorithim_22 = new AFNConstruct(regex_22);
		ThomsonAlgorithim_22.construct();
		Automata temp_22 = ThomsonAlgorithim_22.getAfn();
		temp_22.setTipo("void");
		automatas.add(temp_22);

		AFNConstruct ThomsonAlgorithim_23 = new AFNConstruct();
		Automata temp_23 =ThomsonAlgorithim_0.afnSimple("}");
		temp_23.setTipo("rbrace");
		automatas.add(temp_23);

		AFNConstruct ThomsonAlgorithim_24 = new AFNConstruct();
		Automata temp_24 =ThomsonAlgorithim_0.afnSimple("]");
		temp_24.setTipo("rbrack");
		automatas.add(temp_24);

		RegexConverter convert_25= new RegexConverter();
		String regex_25 = convert_25.infixToPostfix("byte");
		AFNConstruct ThomsonAlgorithim_25 = new AFNConstruct(regex_25);
		ThomsonAlgorithim_25.construct();
		Automata temp_25 = ThomsonAlgorithim_25.getAfn();
		temp_25.setTipo("byte");
		automatas.add(temp_25);

		RegexConverter convert_26= new RegexConverter();
		String regex_26 = convert_26.infixToPostfix("double");
		AFNConstruct ThomsonAlgorithim_26 = new AFNConstruct(regex_26);
		ThomsonAlgorithim_26.construct();
		Automata temp_26 = ThomsonAlgorithim_26.getAfn();
		temp_26.setTipo("double");
		automatas.add(temp_26);

		RegexConverter convert_27= new RegexConverter();
		String regex_27 = convert_27.infixToPostfix("false");
		AFNConstruct ThomsonAlgorithim_27 = new AFNConstruct(regex_27);
		ThomsonAlgorithim_27.construct();
		Automata temp_27 = ThomsonAlgorithim_27.getAfn();
		temp_27.setTipo("false");
		automatas.add(temp_27);

		RegexConverter convert_28= new RegexConverter();
		String regex_28 = convert_28.infixToPostfix("this");
		AFNConstruct ThomsonAlgorithim_28 = new AFNConstruct(regex_28);
		ThomsonAlgorithim_28.construct();
		Automata temp_28 = ThomsonAlgorithim_28.getAfn();
		temp_28.setTipo("this");
		automatas.add(temp_28);

		AFNConstruct ThomsonAlgorithim_29 = new AFNConstruct();
		Automata temp_29 =ThomsonAlgorithim_0.afnSimple("[");
		temp_29.setTipo("lbrack");
		automatas.add(temp_29);

		RegexConverter convert_30= new RegexConverter();
		String regex_30 = convert_30.infixToPostfix("'≤≤≤!∫\"∫#≥∫≤%∫&≥∫≤(∫)∫*∫+∫,∫-∫.≥∫≤@∫A∫B∫C∫D∫E∫F∫G∫H∫I∫J∫K∫L∫M∫N∫O∫P∫Q∫R∫S∫T∫U∫V∫W∫X∫Y∫Z≥∫≤^∫_∫`∫a∫b∫c∫d∫e∫f∫g∫h∫i∫j∫k∫l∫m∫n∫o∫p∫q∫r∫s∫t∫u∫v∫w∫x∫y∫z≥≥∫\\≤b∫t∫n∫f∫r∫\"∫\'∫\\∫u≤u≥∞≤≤≤≤≤0≥≥∫≤≤1∫2∫3∫4∫5∫6∫7∫8∫9≥≥≥≥∫≤≤A∫B∫C∫D∫E∫F∫a∫b∫c∫d∫e∫f≥≥≥≤≤≤≤≤0≥≥∫≤≤1∫2∫3∫4∫5∫6∫7∫8∫9≥≥≥≥∫≤≤A∫B∫C∫D∫E∫F∫a∫b∫c∫d∫e∫f≥≥≥≤≤≤≤≤0≥≥∫≤≤1∫2∫3∫4∫5∫6∫7∫8∫9≥≥≥≥∫≤≤A∫B∫C∫D∫E∫F∫a∫b∫c∫d∫e∫f≥≥≥≤≤≤≤≤0≥≥∫≤≤1∫2∫3∫4∫5∫6∫7∫8∫9≥≥≥≥∫≤≤A∫B∫C∫D∫E∫F∫a∫b∫c∫d∫e∫f≥≥≥∫≤≤≤0≥≥∫≤≤1∫2∫3≥≥≥≤≤≤≤0≥≥∫≤≤1∫2∫3∫4∫5∫6∫7≥≥≥≥Ω≤≤≤≤0≥≥∫≤≤1∫2∫3∫4∫5∫6∫7≥≥≥≥Ω∫≤≤≤0≥≥∫≤≤1∫2∫3∫4∫5∫6∫7≥≥≥≤≤≤≤0≥≥∫≤≤1∫2∫3∫4∫5∫6∫7≥≥≥≥Ω≥≥'");
		AFNConstruct ThomsonAlgorithim_30 = new AFNConstruct(regex_30);
		ThomsonAlgorithim_30.construct();
		Automata temp_30 = ThomsonAlgorithim_30.getAfn();
		temp_30.setTipo("charLit");
		automatas.add(temp_30);

		RegexConverter convert_31= new RegexConverter();
		String regex_31 = convert_31.infixToPostfix("int");
		AFNConstruct ThomsonAlgorithim_31 = new AFNConstruct(regex_31);
		ThomsonAlgorithim_31.construct();
		Automata temp_31 = ThomsonAlgorithim_31.getAfn();
		temp_31.setTipo("int");
		automatas.add(temp_31);

		AFNConstruct ThomsonAlgorithim_32 = new AFNConstruct();
		Automata temp_32 =ThomsonAlgorithim_0.afnSimple("+");
		temp_32.setTipo("plus");
		automatas.add(temp_32);

		RegexConverter convert_33= new RegexConverter();
		String regex_33 = convert_33.infixToPostfix("super");
		AFNConstruct ThomsonAlgorithim_33 = new AFNConstruct(regex_33);
		ThomsonAlgorithim_33.construct();
		Automata temp_33 = ThomsonAlgorithim_33.getAfn();
		temp_33.setTipo("super");
		automatas.add(temp_33);

		RegexConverter convert_34= new RegexConverter();
		String regex_34 = convert_34.infixToPostfix("\"≤≤≤!∫#≥∫≤%∫&≥∫≤(∫)∫*∫+∫,∫-∫.≥∫≤@∫A∫B∫C∫D∫E∫F∫G∫H∫I∫J∫K∫L∫M∫N∫O∫P∫Q∫R∫S∫T∫U∫V∫W∫X∫Y∫Z≥∫≤^∫_∫`∫a∫b∫c∫d∫e∫f∫g∫h∫i∫j∫k∫l∫m∫n∫o∫p∫q∫r∫s∫t∫u∫v∫w∫x∫y∫z≥≥∫\\≤b∫t∫n∫f∫r∫\"∫\'∫\\∫u≤u≥∞≤≤≤≤≤0≥≥∫≤≤1∫2∫3∫4∫5∫6∫7∫8∫9≥≥≥≥∫≤≤A∫B∫C∫D∫E∫F∫a∫b∫c∫d∫e∫f≥≥≥≤≤≤≤≤0≥≥∫≤≤1∫2∫3∫4∫5∫6∫7∫8∫9≥≥≥≥∫≤≤A∫B∫C∫D∫E∫F∫a∫b∫c∫d∫e∫f≥≥≥≤≤≤≤≤0≥≥∫≤≤1∫2∫3∫4∫5∫6∫7∫8∫9≥≥≥≥∫≤≤A∫B∫C∫D∫E∫F∫a∫b∫c∫d∫e∫f≥≥≥≤≤≤≤≤0≥≥∫≤≤1∫2∫3∫4∫5∫6∫7∫8∫9≥≥≥≥∫≤≤A∫B∫C∫D∫E∫F∫a∫b∫c∫d∫e∫f≥≥≥∫≤≤≤0≥≥∫≤≤1∫2∫3≥≥≥≤≤≤≤0≥≥∫≤≤1∫2∫3∫4∫5∫6∫7≥≥≥≥Ω≤≤≤≤0≥≥∫≤≤1∫2∫3∫4∫5∫6∫7≥≥≥≥Ω∫≤≤≤0≥≥∫≤≤1∫2∫3∫4∫5∫6∫7≥≥≥≤≤≤≤0≥≥∫≤≤1∫2∫3∫4∫5∫6∫7≥≥≥≥Ω≥≥∞\"");
		AFNConstruct ThomsonAlgorithim_34 = new AFNConstruct(regex_34);
		ThomsonAlgorithim_34.construct();
		Automata temp_34 = ThomsonAlgorithim_34.getAfn();
		temp_34.setTipo("stringLit");
		temp_34.setExceptKeywords(true);
		automatas.add(temp_34);

		AFNConstruct ThomsonAlgorithim_35 = new AFNConstruct();
		Automata temp_35 =ThomsonAlgorithim_0.afnSimple(",");
		temp_35.setTipo("comma");
		automatas.add(temp_35);

		RegexConverter convert_36= new RegexConverter();
		String regex_36 = convert_36.infixToPostfix("boolean");
		AFNConstruct ThomsonAlgorithim_36 = new AFNConstruct(regex_36);
		ThomsonAlgorithim_36.construct();
		Automata temp_36 = ThomsonAlgorithim_36.getAfn();
		temp_36.setTipo("boolean");
		automatas.add(temp_36);

		RegexConverter convert_37= new RegexConverter();
		String regex_37 = convert_37.infixToPostfix("null");
		AFNConstruct ThomsonAlgorithim_37 = new AFNConstruct(regex_37);
		ThomsonAlgorithim_37.construct();
		Automata temp_37 = ThomsonAlgorithim_37.getAfn();
		temp_37.setTipo("null");
		automatas.add(temp_37);

		RegexConverter convert_38= new RegexConverter();
		String regex_38 = convert_38.infixToPostfix("≤≤A∫B∫C∫D∫E∫F∫G∫H∫I∫J∫K∫L∫M∫N∫O∫P∫Q∫R∫S∫T∫U∫V∫W∫X∫Y∫Z≥∫≤a∫b∫c∫d∫e∫f∫g∫h∫i∫j∫k∫l∫m∫n∫o∫p∫q∫r∫s∫t∫u∫v∫w∫x∫y∫z≥∫+≥≤≤≤A∫B∫C∫D∫E∫F∫G∫H∫I∫J∫K∫L∫M∫N∫O∫P∫Q∫R∫S∫T∫U∫V∫W∫X∫Y∫Z≥∫≤a∫b∫c∫d∫e∫f∫g∫h∫i∫j∫k∫l∫m∫n∫o∫p∫q∫r∫s∫t∫u∫v∫w∫x∫y∫z≥∫+≥≥∞");
		AFNConstruct ThomsonAlgorithim_38 = new AFNConstruct(regex_38);
		ThomsonAlgorithim_38.construct();
		Automata temp_38 = ThomsonAlgorithim_38.getAfn();
		temp_38.setTipo("letter");
		temp_38.setExceptKeywords(true);
		automatas.add(temp_38);

		RegexConverter convert_39= new RegexConverter();
		String regex_39 = convert_39.infixToPostfix("char");
		AFNConstruct ThomsonAlgorithim_39 = new AFNConstruct(regex_39);
		ThomsonAlgorithim_39.construct();
		Automata temp_39 = ThomsonAlgorithim_39.getAfn();
		temp_39.setTipo("char");
		automatas.add(temp_39);

		RegexConverter convert_40= new RegexConverter();
		String regex_40 = convert_40.infixToPostfix("final");
		AFNConstruct ThomsonAlgorithim_40 = new AFNConstruct(regex_40);
		ThomsonAlgorithim_40.construct();
		Automata temp_40 = ThomsonAlgorithim_40.getAfn();
		temp_40.setTipo("final");
		automatas.add(temp_40);

		RegexConverter convert_41= new RegexConverter();
		String regex_41 = convert_41.infixToPostfix("true");
		AFNConstruct ThomsonAlgorithim_41 = new AFNConstruct(regex_41);
		ThomsonAlgorithim_41.construct();
		Automata temp_41 = ThomsonAlgorithim_41.getAfn();
		temp_41.setTipo("true");
		automatas.add(temp_41);

		AFNConstruct ThomsonAlgorithim_42 = new AFNConstruct();
		Automata temp_42 =ThomsonAlgorithim_0.afnSimple(":");
		temp_42.setTipo("colon");
		automatas.add(temp_42);

		RegexConverter convert_43= new RegexConverter();
		String regex_43 = convert_43.infixToPostfix("short");
		AFNConstruct ThomsonAlgorithim_43 = new AFNConstruct(regex_43);
		ThomsonAlgorithim_43.construct();
		Automata temp_43 = ThomsonAlgorithim_43.getAfn();
		temp_43.setTipo("short");
		automatas.add(temp_43);

		AFNConstruct ThomsonAlgorithim_44 = new AFNConstruct();
		Automata temp_44 =ThomsonAlgorithim_0.afnSimple("=");
		temp_44.setTipo("igual");
		automatas.add(temp_44);
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
		if (!(Boolean)tks.get(0)){
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
