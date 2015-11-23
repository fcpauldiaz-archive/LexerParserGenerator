/**
 * Nombre del archivo: MiniTinyParser.java
 * Universidad del Valle de Guatemala
 * Pablo Diaz 13203 
 * Descripción: Tercer proyecto. Generador de Parser
**/

import java.util.HashSet;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Stack;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


public class MiniTinyParser {

	private final ArrayList<Produccion> producciones = new ArrayList();
	private final ArrayList<ItemTablaParseo> tablaParseo = new ArrayList();
	private String input;
	private Automata SLR;
	private Errors errores = new Errors();

	public MiniTinyParser(String input){
		
		this.input=input;
		try
		{
			FileInputStream fileIn = new FileInputStream("automata.ser");
			ObjectInputStream in = new ObjectInputStream(fileIn);
			SLR = (Automata) in.readObject();
			in.close();
			fileIn.close();
			 System.out.println(SLR.getEstados());
		}catch(IOException i)
		{
			i.printStackTrace();
		}catch(ClassNotFoundException c)
		{
			System.out.println("Automata class not found");
			c.printStackTrace();
		}
	
		generarItems();
		generarProducciones();
	}
	public void generarItems(){
		tablaParseo.add(new ItemTablaParseo(0,"stmtSeq","goto",1));
		tablaParseo.add(new ItemTablaParseo(0,"id","shift",2));
		tablaParseo.add(new ItemTablaParseo(0,"stmt","goto",3));
		tablaParseo.add(new ItemTablaParseo(0,"assign_stmt","goto",4));
		tablaParseo.add(new ItemTablaParseo(1,"pc","shift",27));
		tablaParseo.add(new ItemTablaParseo(2,"assign_op","shift",5));
		tablaParseo.add(new ItemTablaParseo(5,"op","shift",6));
		tablaParseo.add(new ItemTablaParseo(5,"simpleExp","goto",7));
		tablaParseo.add(new ItemTablaParseo(5,"number","shift",8));
		tablaParseo.add(new ItemTablaParseo(5,"term","goto",9));
		tablaParseo.add(new ItemTablaParseo(5,"id","shift",10));
		tablaParseo.add(new ItemTablaParseo(5,"exp","goto",12));
		tablaParseo.add(new ItemTablaParseo(5,"factor","goto",11));
		tablaParseo.add(new ItemTablaParseo(6,"op","shift",6));
		tablaParseo.add(new ItemTablaParseo(6,"simpleExp","goto",7));
		tablaParseo.add(new ItemTablaParseo(6,"number","shift",8));
		tablaParseo.add(new ItemTablaParseo(6,"term","goto",9));
		tablaParseo.add(new ItemTablaParseo(6,"id","shift",10));
		tablaParseo.add(new ItemTablaParseo(6,"exp","goto",25));
		tablaParseo.add(new ItemTablaParseo(6,"factor","goto",11));
		tablaParseo.add(new ItemTablaParseo(7,"res","shift",17));
		tablaParseo.add(new ItemTablaParseo(7,"lt","shift",18));
		tablaParseo.add(new ItemTablaParseo(7,"sum","shift",19));
		tablaParseo.add(new ItemTablaParseo(7,"eq","shift",20));
		tablaParseo.add(new ItemTablaParseo(9,"mul","shift",14));
		tablaParseo.add(new ItemTablaParseo(9,"div","shift",13));
		tablaParseo.add(new ItemTablaParseo(13,"op","shift",6));
		tablaParseo.add(new ItemTablaParseo(13,"number","shift",8));
		tablaParseo.add(new ItemTablaParseo(13,"id","shift",10));
		tablaParseo.add(new ItemTablaParseo(13,"factor","goto",16));
		tablaParseo.add(new ItemTablaParseo(14,"op","shift",6));
		tablaParseo.add(new ItemTablaParseo(14,"number","shift",8));
		tablaParseo.add(new ItemTablaParseo(14,"id","shift",10));
		tablaParseo.add(new ItemTablaParseo(14,"factor","goto",15));
		tablaParseo.add(new ItemTablaParseo(17,"op","shift",6));
		tablaParseo.add(new ItemTablaParseo(17,"number","shift",8));
		tablaParseo.add(new ItemTablaParseo(17,"term","goto",24));
		tablaParseo.add(new ItemTablaParseo(17,"id","shift",10));
		tablaParseo.add(new ItemTablaParseo(17,"factor","goto",11));
		tablaParseo.add(new ItemTablaParseo(18,"op","shift",6));
		tablaParseo.add(new ItemTablaParseo(18,"simpleExp","goto",23));
		tablaParseo.add(new ItemTablaParseo(18,"number","shift",8));
		tablaParseo.add(new ItemTablaParseo(18,"term","goto",9));
		tablaParseo.add(new ItemTablaParseo(18,"id","shift",10));
		tablaParseo.add(new ItemTablaParseo(18,"factor","goto",11));
		tablaParseo.add(new ItemTablaParseo(19,"op","shift",6));
		tablaParseo.add(new ItemTablaParseo(19,"number","shift",8));
		tablaParseo.add(new ItemTablaParseo(19,"term","goto",22));
		tablaParseo.add(new ItemTablaParseo(19,"id","shift",10));
		tablaParseo.add(new ItemTablaParseo(19,"factor","goto",11));
		tablaParseo.add(new ItemTablaParseo(20,"op","shift",6));
		tablaParseo.add(new ItemTablaParseo(20,"simpleExp","goto",21));
		tablaParseo.add(new ItemTablaParseo(20,"number","shift",8));
		tablaParseo.add(new ItemTablaParseo(20,"term","goto",9));
		tablaParseo.add(new ItemTablaParseo(20,"id","shift",10));
		tablaParseo.add(new ItemTablaParseo(20,"factor","goto",11));
		tablaParseo.add(new ItemTablaParseo(21,"res","shift",17));
		tablaParseo.add(new ItemTablaParseo(21,"sum","shift",19));
		tablaParseo.add(new ItemTablaParseo(22,"mul","shift",14));
		tablaParseo.add(new ItemTablaParseo(22,"div","shift",13));
		tablaParseo.add(new ItemTablaParseo(23,"res","shift",17));
		tablaParseo.add(new ItemTablaParseo(23,"sum","shift",19));
		tablaParseo.add(new ItemTablaParseo(24,"mul","shift",14));
		tablaParseo.add(new ItemTablaParseo(24,"div","shift",13));
		tablaParseo.add(new ItemTablaParseo(25,"cp","shift",26));
		tablaParseo.add(new ItemTablaParseo(27,"id","shift",2));
		tablaParseo.add(new ItemTablaParseo(27,"stmt","goto",28));
		tablaParseo.add(new ItemTablaParseo(27,"assign_stmt","goto",4));
	}
	public void generarProducciones(){
		producciones.add(new Produccion("program","stmtSeq $", new Item(0)));
		producciones.add(new Produccion("stmtSeq","stmtSeq pc stmt", new Item(0)));
		producciones.add(new Produccion("stmtSeq","stmt", new Item(0)));
		producciones.add(new Produccion("stmt","assign_stmt", new Item(0)));
		producciones.add(new Produccion("assign_stmt","id assign_op exp", new Item(0)));
		producciones.add(new Produccion("exp","simpleExp lt simpleExp", new Item(0)));
		producciones.add(new Produccion("exp","simpleExp eq simpleExp", new Item(0)));
		producciones.add(new Produccion("exp","simpleExp", new Item(0)));
		producciones.add(new Produccion("simpleExp","simpleExp sum term", new Item(0)));
		producciones.add(new Produccion("simpleExp","simpleExp res term", new Item(0)));
		producciones.add(new Produccion("simpleExp","term", new Item(0)));
		producciones.add(new Produccion("term","term mul factor", new Item(0)));
		producciones.add(new Produccion("term","term div factor", new Item(0)));
		producciones.add(new Produccion("term","factor", new Item(0)));
		producciones.add(new Produccion("factor","op exp cp", new Item(0)));
		producciones.add(new Produccion("factor","number", new Item(0)));
		producciones.add(new Produccion("factor","id", new Item(0)));
	}
	public String determinarOperacion(String letra){
		if (terminal(letra)){
			return "shift";
		}
		if (letra.contains("$"))
			return "accept";
		return "goto";
	}

	public void procesoParseo(String input){
		imprimirTabla();
		input += "$";
		Stack estados = new Stack();
		estados.push(0);
		int i = 0;
		boolean Goto = false;
		try{
			while(true){
				Character ch = input.charAt(i);
				int actual = (int)estados.peek();
				ItemTablaParseo encontrado = buscarItem(ch.toString(),actual);
				String op = (String)encontrado.getOperacion();
				if (op.equals("r"))
					op = "reduce";
				if (Goto)
					op ="goto";
				System.out.format("%32s%10s%10s", estados, input.substring(i),op);
				System.out.println("");
				if (encontrado.getOperacion().equals("shift")){
					i++;
					estados.push(encontrado.getNextEstado());
				}
				else if (encontrado.getOperacion().equals("r")&&!Goto){
				int cantidad = producciones.get((int)encontrado.getNextEstado()).getCuerpo().replaceAll("\\s", "").length();
				while(cantidad>0){
					estados.pop();
					cantidad--;
				}
				Goto = true;
				}
				else if (Goto){
					int buscarEstado = (int)estados.peek();
					String transicion = producciones.get((int)encontrado.getNextEstado()).getCabeza();
					int estadoEncontrado = -1;
					for (int j = 0;j<SLR.getEstados().size();j++){
						if (j == buscarEstado){
							for(Transicion trans : (ArrayList<Transicion>)SLR.getEstados().get(j).getTransiciones()){
								if (trans.getSimbolo().equals(transicion)){
									estadoEncontrado= SLR.getEstados().indexOf(trans.getFin());
								}
							}
						}
					}
				estados.push(estadoEncontrado);
				Goto = false;
				}
				if (encontrado.getOperacion().equals("accept"))
					break;
			}
		}catch(Exception e){
			System.out.println("La entrada no pudo parsearse.");
			System.out.println("Se parseo hasta: " + input.substring(0,i));
			System.out.println("Faltó parsear: " + input.substring(i));
		}
	}
	public ItemTablaParseo buscarItem(String simbolo, int estado){
		for (int i = 0;i<tablaParseo.size();i++){
			if ((int)tablaParseo.get(i).getActualEstado() == estado &&
				tablaParseo.get(i).getSimbolo().equals(simbolo))
				return tablaParseo.get(i);
		}
		return null;
	}
	/**
	 * Método que dice si es terminal o no el símobolo
	 * @param simbolo
	 * @return true/false
	 */
	public boolean terminal(String simbolo){
		return searchProductions(simbolo).isEmpty();
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
	public void imprimirTabla(){
		String acum = "\t" + " ";
		ArrayList<String> alfabeto = new ArrayList(SLR.getAlfabeto());
		alfabeto.add("$");
		for (String letra: alfabeto){
			acum += letra + "\t";
		}
		String tabla = "";
		int anterior = 0;
		for (int k = 0;k<tablaParseo.size();k++){
			if (k == 0){
			tabla += (int)tablaParseo.get(k).getActualEstado();
			}
			int espacio =1;
			espacio += Math.abs(k-alfabeto.indexOf(tablaParseo.get(k).getSimbolo()))%6;
			tabla += tablaParseo.get(k).toString(espacio);
			if (k+1<tablaParseo.size()){
				if ((int)tablaParseo.get(k+1).getActualEstado()!=anterior){
					tabla += "\n"+(int)tablaParseo.get(k+1).getActualEstado();
					anterior = (int)tablaParseo.get(k+1).getActualEstado();
				}
			}
		}
		System.out.println(tabla);
		System.out.println("");
	}

}
