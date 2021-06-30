package it.polito.tdp.imdb.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.imdb.db.ImdbDAO;

public class Model {

	private ImdbDAO dao;
	private SimpleWeightedGraph<Actor, DefaultWeightedEdge> grafo;
	private Map<Integer, Actor> idMap;

	public Model() {
		this.dao = new ImdbDAO();
		this.idMap = new HashMap<Integer, Actor>();
	}
	
	public List<String> getGenres() {
		return dao.listAllGenres();
	}
	
	public void creaGrafo(String genere) {
		grafo = new SimpleWeightedGraph<Actor, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		dao.listAllActors(genere, idMap);
		Graphs.addAllVertices(grafo, idMap.values());
		
		for(Adiacenza a : dao.getArchi(genere, idMap)) {
			if(grafo.getEdge(a.getA1() ,a.getA2()) == null) {
				Graphs.addEdgeWithVertices(grafo, a.getA1(), a.getA2(), a.getPeso());
			}
		}
	}
	
	public int getVertici() {
		return grafo.vertexSet().size();
	}
	
	public int getArchi() {
		return grafo.edgeSet().size();
	}
	
	public List<Actor> getActor(){
		List<Actor> result = new ArrayList<Actor>();
		for(Actor a : grafo.vertexSet())
		result.add(a);
		Collections.sort(result, new ComparatoreAttori());
		return result;
	}
	
	class ComparatoreAttori  implements Comparator<Actor>{

		@Override
		public int compare(Actor a1, Actor a2) {
			if(a1.lastName.compareTo(a2.lastName)>0)
			    return 1;
			else 
				return -1;
		}
	}
	
	public List<Actor> attoriSimili(Actor a){
		ConnectivityInspector<Actor, DefaultWeightedEdge> ci = new ConnectivityInspector<Actor, DefaultWeightedEdge>(grafo);
		List<Actor> result = new ArrayList<>(ci.connectedSetOf(a));
		result.remove(a);
		
		Collections.sort(result, new ComparatoreAdiacenza());
		
		return result;	
		
	}
	
	class ComparatoreAdiacenza  implements Comparator<Actor>{

		@Override
		public int compare(Actor a1, Actor a2) {
			return a1.lastName.compareTo(a2.lastName);
			  
		}
	}
}
