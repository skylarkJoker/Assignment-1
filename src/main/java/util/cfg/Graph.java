package util.cfg;

import org.jgrapht.graph.ClassBasedEdgeFactory;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedMultigraph;

import java.util.*;

public class Graph {

	protected DirectedMultigraph<Node, DefaultEdge> graph;
	protected Map<DefaultEdge, Boolean> decisions;
	
	public Graph(){
        Node.sNextId = 1;
		decisions = new HashMap<DefaultEdge, Boolean>();
		graph = new DirectedMultigraph<Node, DefaultEdge>(new ClassBasedEdgeFactory<Node, DefaultEdge>(DefaultEdge.class));

	}
	
	public void addNode(Node n){
		graph.addVertex(n);
	}

	public void addEdge(Node a, Node b) {
		graph.addEdge(a,b);
		updateDecisions(a);
	}

	private void updateDecisions(Node a) {
		boolean decision = false;

		if(graph.outgoingEdgesOf(a).size()>1) {
			decision = true;
		}
		for(DefaultEdge outgoing : graph.outgoingEdgesOf(a)) {
			decisions.put(outgoing, decision);
		}

	}

	public boolean decisionEdge(Node from, Node to){
		DefaultEdge cfgEdge = graph.getEdge(from,to);
		if(cfgEdge == null){
			return false;
		}
		else{
			return decisions.get(cfgEdge);
		}
	}


	public Set<Node> getPredecessors(Node a){
		Set<Node> preds = new HashSet<Node>();
		for(DefaultEdge de : graph.incomingEdgesOf(a)){
			preds.add(graph.getEdgeSource(de));
		}
		return preds;
	}
	
	public Set<Node> getSuccessors(Node a){

		Set<Node> succs = new HashSet<Node>();
		for(DefaultEdge de : graph.outgoingEdgesOf(a)){
			succs.add(graph.getEdgeTarget(de));
		}
		return succs;
	}
	
	public Set<Node> getNodes(){
		return graph.vertexSet();
	}
	
	public Node getEntry(){
		for(Node n : getNodes()){
			if(graph.incomingEdgesOf(n).isEmpty())
				return n;
		}
		return null;
	}
	
	public Node getExit(){
		for(Node n : getNodes()){
			if(graph.outgoingEdgesOf(n).isEmpty())
				return n;
		}
		return null;
	}
	
	public String toString(){
		String dotString = "digraph cfg{\n";
		for (Node node : getNodes()) {
			for (Node succ: getSuccessors(node)) {
				dotString+=node.toString()+"->"+succ.toString()+"\n";
			}
		}
		dotString+="}";
		return dotString;
	}


	/**
	 * Return all transitive successors of m.
	 * @param m
	 * @return
     */
	public Collection<Node> allSuccessors(Node m){
		return transitiveSuccessors(m, new HashSet<Node>());
	}

	private Collection<Node> transitiveSuccessors(Node m, Set<Node> done){
		Collection<Node> successors = new HashSet<Node>();
		for(Node n : getSuccessors(m)){
			if(!done.contains(n)) {
				successors.add(n);
				done.add(n);
			}
			successors.addAll(transitiveSuccessors(n, done));
		}
		return successors;
	}

}
