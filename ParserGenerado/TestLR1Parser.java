/**
 * Nombre del archivo: TestLR1Parser.java
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

public class TestLR1Parser {

	private final ArrayList<Produccion> producciones = new ArrayList();
	private final ArrayList<ItemTablaParseo> tablaParseo = new ArrayList();
	private String input;
	private Automata LR;
	private Errors errores = new Errors();

	public TestLR1Parser(String input){
		this.input=input;
		try
		{
			FileInputStream fileIn = new FileInputStream("automataLR.ser");
			ObjectInputStream in = new ObjectInputStream(fileIn);
			LR = (Automata) in.readObject();
			in.close();
			fileIn.close();
			 System.out.println(LR.getEstados());
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
		tablaParseo.add(new ItemTablaParseo(0,"a","shift",1));
		tablaParseo.add(new ItemTablaParseo(0,"b","shift",2));
		tablaParseo.add(new ItemTablaParseo(0,"S","goto",3));
		tablaParseo.add(new ItemTablaParseo(1,"H","goto",8));
		tablaParseo.add(new ItemTablaParseo(1,"K","goto",9));
		tablaParseo.add(new ItemTablaParseo(2,"H","goto",4));
		tablaParseo.add(new ItemTablaParseo(2,"K","goto",5));
		tablaParseo.add(new ItemTablaParseo(3,"$","accept",0));
		tablaParseo.add(new ItemTablaParseo(4,"e","shift",7));
		tablaParseo.add(new ItemTablaParseo(5,"d","shift",6));
		tablaParseo.add(new ItemTablaParseo(6,"$","r",2));
		tablaParseo.add(new ItemTablaParseo(7,"$","r",4));
		tablaParseo.add(new ItemTablaParseo(8,"d","shift",11));
		tablaParseo.add(new ItemTablaParseo(9,"e","shift",10));
		tablaParseo.add(new ItemTablaParseo(10,"$","r",3));
		tablaParseo.add(new ItemTablaParseo(11,"$","r",1));
	}
	public void generarProducciones(){
		producciones.add(new Produccion("Sp","S", new Item(0)));
		producciones.add(new Produccion("S","a H d", new Item(0)));
		producciones.add(new Produccion("S","b K d", new Item(0)));
		producciones.add(new Produccion("S","a K e", new Item(0)));
		producciones.add(new Produccion("S","b H e", new Item(0)));
		producciones.add(new Produccion("H","c", new Item(0)));
		producciones.add(new Produccion("K","c", new Item(0)));
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
		input += " $";
		Stack estados = new Stack();
		estados.push(0);
		int i = 0;
		boolean Goto = false;
		String[] parts = input.split(" ");
		ItemTablaParseo encontrado = null;
		String consumido = "";
		int cantParts = parts.length;
		String actualString ="";
		try{
			while(true){
				String ch = parts[i];
				int actual = (int)estados.peek();
				if (!Goto)
					encontrado = buscarItem(ch,actual);
				String op = (String)encontrado.getOperacion();
				if (op.equals("r"))
					op = "reduce";
				if (Goto)
					op ="goto";
				System.out.format("%32s%10s%10s", estados, consumido,op+encontrado.getNextEstado());
				System.out.println("");
				if (encontrado.getOperacion().equals("shift")){
					i++;
					actualString += parts[i];
					consumido = "";
						for (int b = 0;b+i<parts.length;b++){
							consumido += " "+ parts[b+i];
					}
					estados.push(encontrado.getNextEstado());
				}
				else if (encontrado.getOperacion().equals("r")&&!Goto){
				int cantidad = producciones.get((int)encontrado.getNextEstado()).getCuerpo().split(" ").length;
				if ( producciones.get((int)encontrado.getNextEstado()).getCuerpo().replaceAll("\\s", "").isEmpty()){
					cantidad--;
				}
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
					for (int j = 0;j<LR.getEstados().size();j++){
						if (j == buscarEstado){
							for(Transicion trans : (ArrayList<Transicion>)LR.getEstados().get(j).getTransiciones()){
								if (trans.getSimbolo().equals(transicion)){
									estadoEncontrado= LR.getEstados().indexOf(trans.getFin());
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
						consumido = "";
			for (int b = 0;b+i<parts.length;b++){
				consumido += " "+ parts[b+i];
			}
			System.out.println("La entrada no pudo parsearse.");
			System.out.println("Se parseo hasta: " + actualString);
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
		ArrayList<String> alfabeto = new ArrayList(LR.getAlfabeto());
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
