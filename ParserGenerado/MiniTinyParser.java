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
		tablaParseo.add(new ItemTablaParseo(1,"$","accept",1));
		tablaParseo.add(new ItemTablaParseo(2,"assign_op","shift",5));
		tablaParseo.add(new ItemTablaParseo(3,"pc","r",2));
		tablaParseo.add(new ItemTablaParseo(3,"$","r",2));
		tablaParseo.add(new ItemTablaParseo(4,"pc","r",3));
		tablaParseo.add(new ItemTablaParseo(4,"$","r",3));
		tablaParseo.add(new ItemTablaParseo(4,"Seq","r",3));
		tablaParseo.add(new ItemTablaParseo(5,"op","shift",6));
		tablaParseo.add(new ItemTablaParseo(5,"simpleExp","goto",7));
		tablaParseo.add(new ItemTablaParseo(5,"number","shift",8));
		tablaParseo.add(new ItemTablaParseo(5,"term","goto",9));
		tablaParseo.add(new ItemTablaParseo(5,"id","shift",10));
		tablaParseo.add(new ItemTablaParseo(5,"exp","goto",11));
		tablaParseo.add(new ItemTablaParseo(5,"factor","goto",12));
		tablaParseo.add(new ItemTablaParseo(6,"op","shift",6));
		tablaParseo.add(new ItemTablaParseo(6,"simpleExp","goto",7));
		tablaParseo.add(new ItemTablaParseo(6,"number","shift",8));
		tablaParseo.add(new ItemTablaParseo(6,"term","goto",9));
		tablaParseo.add(new ItemTablaParseo(6,"id","shift",10));
		tablaParseo.add(new ItemTablaParseo(6,"exp","goto",25));
		tablaParseo.add(new ItemTablaParseo(6,"factor","goto",12));
		tablaParseo.add(new ItemTablaParseo(7,"res","shift",17));
		tablaParseo.add(new ItemTablaParseo(7,"lt","shift",18));
		tablaParseo.add(new ItemTablaParseo(7,"sum","shift",19));
		tablaParseo.add(new ItemTablaParseo(7,"eq","shift",20));
		tablaParseo.add(new ItemTablaParseo(7,"pc","r",7));
		tablaParseo.add(new ItemTablaParseo(7,"$","r",7));
		tablaParseo.add(new ItemTablaParseo(7,"cp","r",7));
		tablaParseo.add(new ItemTablaParseo(7,"Seq","r",7));
		tablaParseo.add(new ItemTablaParseo(8,"div","r",15));
		tablaParseo.add(new ItemTablaParseo(8,"res","r",15));
		tablaParseo.add(new ItemTablaParseo(8,"pc","r",15));
		tablaParseo.add(new ItemTablaParseo(8,"$","r",15));
		tablaParseo.add(new ItemTablaParseo(8,"mul","r",15));
		tablaParseo.add(new ItemTablaParseo(8,"lt","r",15));
		tablaParseo.add(new ItemTablaParseo(8,"sum","r",15));
		tablaParseo.add(new ItemTablaParseo(8,"eq","r",15));
		tablaParseo.add(new ItemTablaParseo(8,"cp","r",15));
		tablaParseo.add(new ItemTablaParseo(8,"Seq","r",15));
		tablaParseo.add(new ItemTablaParseo(9,"mul","shift",14));
		tablaParseo.add(new ItemTablaParseo(9,"div","shift",13));
		tablaParseo.add(new ItemTablaParseo(9,"res","r",10));
		tablaParseo.add(new ItemTablaParseo(9,"pc","r",10));
		tablaParseo.add(new ItemTablaParseo(9,"$","r",10));
		tablaParseo.add(new ItemTablaParseo(9,"lt","r",10));
		tablaParseo.add(new ItemTablaParseo(9,"sum","r",10));
		tablaParseo.add(new ItemTablaParseo(9,"eq","r",10));
		tablaParseo.add(new ItemTablaParseo(9,"cp","r",10));
		tablaParseo.add(new ItemTablaParseo(9,"Seq","r",10));
		tablaParseo.add(new ItemTablaParseo(10,"div","r",16));
		tablaParseo.add(new ItemTablaParseo(10,"res","r",16));
		tablaParseo.add(new ItemTablaParseo(10,"pc","r",16));
		tablaParseo.add(new ItemTablaParseo(10,"$","r",16));
		tablaParseo.add(new ItemTablaParseo(10,"mul","r",16));
		tablaParseo.add(new ItemTablaParseo(10,"lt","r",16));
		tablaParseo.add(new ItemTablaParseo(10,"sum","r",16));
		tablaParseo.add(new ItemTablaParseo(10,"eq","r",16));
		tablaParseo.add(new ItemTablaParseo(10,"cp","r",16));
		tablaParseo.add(new ItemTablaParseo(10,"Seq","r",16));
		tablaParseo.add(new ItemTablaParseo(11,"pc","r",4));
		tablaParseo.add(new ItemTablaParseo(11,"$","r",4));
		tablaParseo.add(new ItemTablaParseo(11,"Seq","r",4));
		tablaParseo.add(new ItemTablaParseo(12,"div","r",13));
		tablaParseo.add(new ItemTablaParseo(12,"res","r",13));
		tablaParseo.add(new ItemTablaParseo(12,"pc","r",13));
		tablaParseo.add(new ItemTablaParseo(12,"$","r",13));
		tablaParseo.add(new ItemTablaParseo(12,"mul","r",13));
		tablaParseo.add(new ItemTablaParseo(12,"lt","r",13));
		tablaParseo.add(new ItemTablaParseo(12,"sum","r",13));
		tablaParseo.add(new ItemTablaParseo(12,"eq","r",13));
		tablaParseo.add(new ItemTablaParseo(12,"cp","r",13));
		tablaParseo.add(new ItemTablaParseo(12,"Seq","r",13));
		tablaParseo.add(new ItemTablaParseo(13,"op","shift",6));
		tablaParseo.add(new ItemTablaParseo(13,"number","shift",8));
		tablaParseo.add(new ItemTablaParseo(13,"id","shift",10));
		tablaParseo.add(new ItemTablaParseo(13,"factor","goto",16));
		tablaParseo.add(new ItemTablaParseo(14,"op","shift",6));
		tablaParseo.add(new ItemTablaParseo(14,"number","shift",8));
		tablaParseo.add(new ItemTablaParseo(14,"id","shift",10));
		tablaParseo.add(new ItemTablaParseo(14,"factor","goto",15));
		tablaParseo.add(new ItemTablaParseo(15,"div","r",11));
		tablaParseo.add(new ItemTablaParseo(15,"res","r",11));
		tablaParseo.add(new ItemTablaParseo(15,"pc","r",11));
		tablaParseo.add(new ItemTablaParseo(15,"$","r",11));
		tablaParseo.add(new ItemTablaParseo(15,"mul","r",11));
		tablaParseo.add(new ItemTablaParseo(15,"lt","r",11));
		tablaParseo.add(new ItemTablaParseo(15,"sum","r",11));
		tablaParseo.add(new ItemTablaParseo(15,"eq","r",11));
		tablaParseo.add(new ItemTablaParseo(15,"cp","r",11));
		tablaParseo.add(new ItemTablaParseo(15,"Seq","r",11));
		tablaParseo.add(new ItemTablaParseo(16,"div","r",12));
		tablaParseo.add(new ItemTablaParseo(16,"res","r",12));
		tablaParseo.add(new ItemTablaParseo(16,"pc","r",12));
		tablaParseo.add(new ItemTablaParseo(16,"$","r",12));
		tablaParseo.add(new ItemTablaParseo(16,"mul","r",12));
		tablaParseo.add(new ItemTablaParseo(16,"lt","r",12));
		tablaParseo.add(new ItemTablaParseo(16,"sum","r",12));
		tablaParseo.add(new ItemTablaParseo(16,"eq","r",12));
		tablaParseo.add(new ItemTablaParseo(16,"cp","r",12));
		tablaParseo.add(new ItemTablaParseo(16,"Seq","r",12));
		tablaParseo.add(new ItemTablaParseo(17,"op","shift",6));
		tablaParseo.add(new ItemTablaParseo(17,"number","shift",8));
		tablaParseo.add(new ItemTablaParseo(17,"term","goto",24));
		tablaParseo.add(new ItemTablaParseo(17,"id","shift",10));
		tablaParseo.add(new ItemTablaParseo(17,"factor","goto",12));
		tablaParseo.add(new ItemTablaParseo(18,"op","shift",6));
		tablaParseo.add(new ItemTablaParseo(18,"simpleExp","goto",23));
		tablaParseo.add(new ItemTablaParseo(18,"number","shift",8));
		tablaParseo.add(new ItemTablaParseo(18,"term","goto",9));
		tablaParseo.add(new ItemTablaParseo(18,"id","shift",10));
		tablaParseo.add(new ItemTablaParseo(18,"factor","goto",12));
		tablaParseo.add(new ItemTablaParseo(19,"op","shift",6));
		tablaParseo.add(new ItemTablaParseo(19,"number","shift",8));
		tablaParseo.add(new ItemTablaParseo(19,"term","goto",22));
		tablaParseo.add(new ItemTablaParseo(19,"id","shift",10));
		tablaParseo.add(new ItemTablaParseo(19,"factor","goto",12));
		tablaParseo.add(new ItemTablaParseo(20,"op","shift",6));
		tablaParseo.add(new ItemTablaParseo(20,"simpleExp","goto",21));
		tablaParseo.add(new ItemTablaParseo(20,"number","shift",8));
		tablaParseo.add(new ItemTablaParseo(20,"term","goto",9));
		tablaParseo.add(new ItemTablaParseo(20,"id","shift",10));
		tablaParseo.add(new ItemTablaParseo(20,"factor","goto",12));
		tablaParseo.add(new ItemTablaParseo(21,"res","shift",17));
		tablaParseo.add(new ItemTablaParseo(21,"sum","shift",19));
		tablaParseo.add(new ItemTablaParseo(21,"pc","r",6));
		tablaParseo.add(new ItemTablaParseo(21,"$","r",6));
		tablaParseo.add(new ItemTablaParseo(21,"cp","r",6));
		tablaParseo.add(new ItemTablaParseo(21,"Seq","r",6));
		tablaParseo.add(new ItemTablaParseo(22,"mul","shift",14));
		tablaParseo.add(new ItemTablaParseo(22,"div","shift",13));
		tablaParseo.add(new ItemTablaParseo(22,"res","r",8));
		tablaParseo.add(new ItemTablaParseo(22,"pc","r",8));
		tablaParseo.add(new ItemTablaParseo(22,"$","r",8));
		tablaParseo.add(new ItemTablaParseo(22,"lt","r",8));
		tablaParseo.add(new ItemTablaParseo(22,"sum","r",8));
		tablaParseo.add(new ItemTablaParseo(22,"eq","r",8));
		tablaParseo.add(new ItemTablaParseo(22,"cp","r",8));
		tablaParseo.add(new ItemTablaParseo(22,"Seq","r",8));
		tablaParseo.add(new ItemTablaParseo(23,"res","shift",17));
		tablaParseo.add(new ItemTablaParseo(23,"sum","shift",19));
		tablaParseo.add(new ItemTablaParseo(23,"pc","r",5));
		tablaParseo.add(new ItemTablaParseo(23,"$","r",5));
		tablaParseo.add(new ItemTablaParseo(23,"cp","r",5));
		tablaParseo.add(new ItemTablaParseo(23,"Seq","r",5));
		tablaParseo.add(new ItemTablaParseo(24,"mul","shift",14));
		tablaParseo.add(new ItemTablaParseo(24,"div","shift",13));
		tablaParseo.add(new ItemTablaParseo(24,"res","r",9));
		tablaParseo.add(new ItemTablaParseo(24,"pc","r",9));
		tablaParseo.add(new ItemTablaParseo(24,"$","r",9));
		tablaParseo.add(new ItemTablaParseo(24,"lt","r",9));
		tablaParseo.add(new ItemTablaParseo(24,"sum","r",9));
		tablaParseo.add(new ItemTablaParseo(24,"eq","r",9));
		tablaParseo.add(new ItemTablaParseo(24,"cp","r",9));
		tablaParseo.add(new ItemTablaParseo(24,"Seq","r",9));
		tablaParseo.add(new ItemTablaParseo(25,"cp","shift",26));
		tablaParseo.add(new ItemTablaParseo(26,"div","r",14));
		tablaParseo.add(new ItemTablaParseo(26,"res","r",14));
		tablaParseo.add(new ItemTablaParseo(26,"pc","r",14));
		tablaParseo.add(new ItemTablaParseo(26,"$","r",14));
		tablaParseo.add(new ItemTablaParseo(26,"mul","r",14));
		tablaParseo.add(new ItemTablaParseo(26,"lt","r",14));
		tablaParseo.add(new ItemTablaParseo(26,"sum","r",14));
		tablaParseo.add(new ItemTablaParseo(26,"eq","r",14));
		tablaParseo.add(new ItemTablaParseo(26,"cp","r",14));
		tablaParseo.add(new ItemTablaParseo(26,"Seq","r",14));
		tablaParseo.add(new ItemTablaParseo(27,"id","shift",2));
		tablaParseo.add(new ItemTablaParseo(27,"stmt","goto",28));
		tablaParseo.add(new ItemTablaParseo(27,"assign_stmt","goto",4));
		tablaParseo.add(new ItemTablaParseo(28,"pc","r",1));
		tablaParseo.add(new ItemTablaParseo(28,"$","r",1));
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
				while (!SLR.getAlfabeto().contains(ch)){//si i llega al input.length() significa que no pertenece al alfabeto
					i++;
					if (i != input.length())
						ch = input.charAt(i);
					else
						break;
				}
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
