/**
* Universidad Del Valle Guatemala
* Pablo Díaz 13203
* Descripción: Clase que implementa el algoritmo de Thomson
*/



import java.util.ArrayList;
import java.util.HashSet;
import java.util.Stack;

/**
 * Clase que implementa el algoritmo de Thomson
 * @author Pablo
 * @param <T>
 */
public class AFNConstruct<T> {
    
   
    private Automata afn;
    private String regex;
    public final char charKleene = '∞';
    public final char charConcat = '∆';
    public final char charAbrirParentesis = '≤';
    public final char charCerrarParentesis = '≥';
    public final char charOr = '∫';
    public final char charPlus = '∩';
    public final char charInt = 'Ω';
    


    public AFNConstruct(String regex) {
        this.regex = regex;
        
    }

    public AFNConstruct() {  
        
    }

    
   
    /**
     * Metodo que construye el autómata
     * Utiliza la expresion regular enviada
     */
    public void construct(){
        try {
        Stack pilaAFN = new Stack();
        //Crea un automata por cada operacion
        for (Character c : this.regex.toCharArray()) {
            switch(c){
                case charKleene:
                     Automata kleene = cerraduraKleene((Automata) pilaAFN.pop());
                     pilaAFN.push(kleene);
                     this.afn=kleene;
                    break;
                case charConcat:
                    Automata concat_param1 = (Automata)pilaAFN.pop();
                    Automata concat_param2 = (Automata)pilaAFN.pop();
                    Automata concat_result = concatenacion(concat_param1,concat_param2);
                   
                    pilaAFN.push(concat_result);
                    this.afn=concat_result;
                    break;
                    
                case charOr:
                    
                    Automata union_param1 = (Automata)pilaAFN.pop();
                    Automata union_param2 = (Automata)pilaAFN.pop();
                    Automata union_result = union(union_param1,union_param2);
                   
                    
                    pilaAFN.push(union_result);
                   
                    this.afn = union_result;
                    break;
                    
                default:
                    //crear un automata con cada simbolo
                    Automata simple = afnSimple((T) Character.toString(c));
                    pilaAFN.push(simple);
                    this.afn=simple;
                    
                    
            }
        }
        this.afn.createAlfabeto(regex);
        this.afn.setTipo("AFN");
        
        
        }catch(Exception e){
            System.out.println("Expresión mal ingresada");
        }
    
                
    }
    /**
     * Meotodo para crear un AFN simple con un simbolo
     * @param simboloRegex
     * @return AFN
     */
    public Automata afnSimple(T simboloRegex)
    {
        Automata automataFN = new Automata();
        //definir los nuevos estados
        Estado inicial = new Estado(0);
        Estado aceptacion = new Estado(1);
        //crear una transicion unica con el simbolo
        Transicion tran = new Transicion(inicial, aceptacion,  simboloRegex);
        inicial.setTransiciones(tran);
        //agrega los estados creados
        automataFN.addEstados(inicial);
        automataFN.addEstados(aceptacion);
        //colocar los estados iniciales y de acpetacion
        automataFN.setEstadoInicial(inicial);
        automataFN.addEstadosAceptacion(aceptacion);
        automataFN.setLenguajeR(simboloRegex+"");
        return automataFN;
       
    }   
    /**
     * Metodo unario para crear un automataFN con cerradura de Kleene *
     * @param automataFN
     * @return AFN
     */
    public Automata cerraduraKleene(Automata automataFN)
    {
        Automata afn_kleene = new Automata();
        
        //se crea un nuevo estado inicial
        Estado nuevoInicio = new Estado(0);
        afn_kleene.addEstados(nuevoInicio);
        afn_kleene.setEstadoInicial(nuevoInicio);
        
        //agregar todos los estados intermedio
        for (int i=0; i < automataFN.getEstados().size(); i++) {
            Estado tmp = (Estado) automataFN.getEstados().get(i);
            tmp.setId(i + 1);
            afn_kleene.addEstados(tmp);
        }
        
        //Se crea un nuevo estado de aceptacion
        Estado nuevoFin = new Estado(automataFN.getEstados().size() + 1);
        afn_kleene.addEstados(nuevoFin);
        afn_kleene.addEstadosAceptacion(nuevoFin);
        
        //definir estados clave para realizar la cerraduras
        Estado anteriorInicio = automataFN.getEstadoInicial();
        
        ArrayList<Estado> anteriorFin    = automataFN.getEstadosAceptacion();
        
        // agregar transiciones desde el nuevo estado inicial
        nuevoInicio.getTransiciones().add(new Transicion(nuevoInicio, anteriorInicio, resultadoGeneradorMain.EPSILON));
        nuevoInicio.getTransiciones().add(new Transicion(nuevoInicio, nuevoFin, resultadoGeneradorMain.EPSILON));
        
        // agregar transiciones desde el anterior estado final
        for (int i =0; i<anteriorFin.size();i++){
            anteriorFin.get(i).getTransiciones().add(new Transicion(anteriorFin.get(i), anteriorInicio,resultadoGeneradorMain.EPSILON));
            anteriorFin.get(i).getTransiciones().add(new Transicion(anteriorFin.get(i), nuevoFin, resultadoGeneradorMain.EPSILON));
        }
        afn_kleene.setAlfabeto(automataFN.getAlfabeto());
        afn_kleene.setLenguajeR(automataFN.getLenguajeR());
        return afn_kleene;
    }
    /**
     * Metodo binario para concatenar dos automatas
     * @param AFN1
     * @param AFN2
     * @return AFN
     */
   public Automata concatenacion(Automata AFN1, Automata AFN2){
       
       Automata afn_concat = new Automata();
            
        //se utiliza como contador para los estados del nuevo automata
        int i=0;
        //agregar los estados del primer automata
        for (i=0; i < AFN2.getEstados().size(); i++) {
            Estado tmp = (Estado) AFN2.getEstados().get(i);
            tmp.setId(i);
            //se define el estado inicial
            if (i==0){
                afn_concat.setEstadoInicial(tmp);
            }
            //cuando llega al último, concatena el ultimo con el primero del otro automata con un epsilon
            if (i == AFN2.getEstados().size()-1){
                //se utiliza un ciclo porque los estados de aceptacion son un array
                for (int k = 0;k<AFN2.getEstadosAceptacion().size();k++)
                {
                    tmp.setTransiciones(new Transicion((Estado) AFN2.getEstadosAceptacion().get(k),AFN1.getEstadoInicial(),resultadoGeneradorMain.EPSILON));
                }
            }
            afn_concat.addEstados(tmp);

        }
        //termina de agregar los estados y transiciones del segundo automata
        for (int j =0;j<AFN1.getEstados().size();j++){
            Estado tmp = (Estado) AFN1.getEstados().get(j);
            tmp.setId(i);

            //define el ultimo con estado de aceptacion
            if (AFN1.getEstados().size()-1==j)
                afn_concat.addEstadosAceptacion(tmp);
             afn_concat.addEstados(tmp);
            i++;
        }
       
        HashSet alfabeto = new HashSet();
        alfabeto.addAll(AFN1.getAlfabeto());
        alfabeto.addAll(AFN2.getAlfabeto());
        afn_concat.setAlfabeto(alfabeto);
        afn_concat.setLenguajeR(AFN1.getLenguajeR()+" " + AFN2.getLenguajeR()); 
        
       return afn_concat;
   }
   
    /**
     * Método binario para unir dos automatas | 
     * @param AFN1
     * @param AFN2
     * @return AFN
     */
    public Automata union(Automata AFN1, Automata AFN2){
        Automata afn_union = new Automata();
        //se crea un nuevo estado inicial
        Estado nuevoInicio = new Estado(0);
        //se crea una transicion del nuevo estado inicial al primer automata
        nuevoInicio.setTransiciones(new Transicion(nuevoInicio,AFN2.getEstadoInicial(),resultadoGeneradorMain.EPSILON));

        afn_union.addEstados(nuevoInicio);
        afn_union.setEstadoInicial(nuevoInicio);
        int i=0;//llevar el contador del identificador de estados
        //agregar los estados del segundo automata
        for (i=0; i < AFN1.getEstados().size(); i++) {
            Estado tmp = (Estado) AFN1.getEstados().get(i);
            tmp.setId(i + 1);
            afn_union.addEstados(tmp);
        }
        //agregar los estados del primer automata
        for (int j=0; j < AFN2.getEstados().size(); j++) {
            Estado tmp = (Estado) AFN2.getEstados().get(j);
            tmp.setId(i + 1);
            afn_union.addEstados(tmp);
            i++;
        }
        
        //se crea un nuevo estado final
        Estado nuevoFin = new Estado(AFN1.getEstados().size() +AFN2.getEstados().size()+ 1);
        afn_union.addEstados(nuevoFin);
        afn_union.addEstadosAceptacion(nuevoFin);
        
       
        Estado anteriorInicio = AFN1.getEstadoInicial();
        ArrayList<Estado> anteriorFin    = AFN1.getEstadosAceptacion();
        ArrayList<Estado> anteriorFin2    = AFN2.getEstadosAceptacion();
        
        // agregar transiciones desde el nuevo estado inicial
        nuevoInicio.getTransiciones().add(new Transicion(nuevoInicio, anteriorInicio, resultadoGeneradorMain.EPSILON));
        
         // agregar transiciones desde el anterior AFN 1 al estado final
        for (int k =0; k<anteriorFin.size();k++)
            anteriorFin.get(k).getTransiciones().add(new Transicion(anteriorFin.get(k), nuevoFin, resultadoGeneradorMain.EPSILON));
         // agregar transiciones desde el anterior AFN 2 al estado final
        for (int k =0; k<anteriorFin.size();k++)
            anteriorFin2.get(k).getTransiciones().add(new Transicion(anteriorFin2.get(k),nuevoFin,resultadoGeneradorMain.EPSILON));
        
        HashSet alfabeto = new HashSet();
        alfabeto.addAll(AFN1.getAlfabeto());
        alfabeto.addAll(AFN2.getAlfabeto());
        afn_union.setAlfabeto(alfabeto);
        afn_union.setLenguajeR(AFN1.getLenguajeR()+" " + AFN2.getLenguajeR()); 
        return afn_union;
    }
    
    
    public Automata getAfn() {
        return this.afn;
    }

    public void setAfn(Automata afn) {
        this.afn = afn;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }
     
    
    
    

}
