import org.junit.Before;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;

import br.usp.each.saeg.asm.defuse.Variable;
import util.DataFlowAnalysis;
import util.DominanceTreeGenerator;
import util.cfg.CFGExtractor;
import util.cfg.Graph;
import util.cfg.Node;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * This should be the entry-point of your programming submission.
 *
 * You may add as many of your own classes and packages as you like to this project structure.
 *
 */
public class AssignmentSubmission implements Slicer {

	ClassNode targetClassNode;
	MethodNode targetMethod;
	Graph cfg;
	
	public AssignmentSubmission(String targetClass, String methodSignature){
		targetClassNode = findClassNode(targetClass);
		targetMethod = findMethodNode(targetClassNode, methodSignature);
		try{
			cfg = CFGExtractor.getCFG(targetClassNode.name, targetMethod);
		} catch (AnalyzerException e) {
			// Failed to extract CFG
			e.printStackTrace();
		}
	}
	
	public static ClassNode findClassNode(String targetClass){
		ClassNode target = new ClassNode(Opcodes.ASM4);
        InputStream in=CFGExtractor.class.getResourceAsStream(targetClass);
        ClassReader classReader;
		try {
			classReader = new ClassReader(in);
			classReader.accept(target, 0);
		} catch (IOException e1) {
			// Fail to read class file.
			e1.printStackTrace();
		} 
		return target;
	}
	
	/**
	 * Find the method with the given methodSignature, belonging to the targetClass.
	 * @param targetClass
	 * @param methodSignature
	 * @return
	 */
	public static MethodNode findMethodNode(ClassNode targetClass, String methodSignature){
		for(MethodNode mn : (List<MethodNode>)targetClass.methods){
	        	String signature = mn.name+mn.desc;
	        	if(!signature.equals(methodSignature))
	        		continue;
	        	else
	        		return mn;
		}
		return null; //Method signature not found.
	}

    /**
     * Returns true if there is a data dependence relation from a to b.
     *
     * In other words, a variable is assigned a value at a, and that value is
     * subsequently used at b, without any intervening definitions of that variable.
     *
     * @param a
     * @param b
     * @return
     */
    @Override
    public boolean isDataDepence(AbstractInsnNode a, AbstractInsnNode b) {
       
    	Node aa = new Node(a);
    	Node bb = new Node(b);
    	
    	//System.out.println("Node A(Instruction)"+aa.toString());
    	//System.out.println("Node B(Instruction)"+bb.toString());
    	
  
    	try{
    		//new control flow graph!
        	Graph myAwesomeGraph = CFGExtractor.getCFG(targetClassNode.name, targetMethod);
        	DominanceTreeGenerator dom = new DominanceTreeGenerator(myAwesomeGraph);
        	
        	
        	//Get a dominance graph so we can check which instructions possibly defines variables
        	Graph dominanceGraph = dom.dominatorTree();
        	//System .out.println(dominanceGraph);
        	
        	
        	Node temp = bb;//creating a node to help traverse the dominance graph
        	List<Node> s = new LinkedList<Node>();
        	
        	/* here we get the path from b to a, 
        		the nodes between a and b are queried to determine if any variables @ a were redefined*/
        	while(!temp.equals(aa)){
        		for(Node n : dominanceGraph.getPredecessors(temp)){
        			s.add(n);
        			temp = n;
        			}
        	}
        	
    		//Get a set of all variables defined by a
    		Collection<Variable> db = DataFlowAnalysis.definedBy(targetClassNode.name, targetMethod, a);
    		
    		//get a set of variables used by b
    		Collection<Variable> us = DataFlowAnalysis.usedBy(targetClassNode.name, targetMethod, b);
    		
    		for(Variable v : (Collection<Variable>)db){
    			if(us.contains(v)){//if a variable from a was used in b...
    				for(Node n : s){
    					//check b's predecessors for any redefinitions
    					Collection<Variable> d2 = DataFlowAnalysis.definedBy(targetClassNode.name, targetMethod, n.getInstruction());
    					if(!d2.contains(v)){
    						//No redefinitions were found! b is data dependent on a! hurray!
    						return true;
    					}
    				}
    			}
    		}
    	}
    	catch(Exception e)
    	{
    		System.out.print("Welp...");
    		e.printStackTrace();
    	}
    	
    	
        return false;
    }

    /**
     * Returns true if a is dependent upon b and false otherwise.
     *
     * In other words, returns true if b represents a conditional instruction that
     * determines whether or not b will execute (following the definition of control
     * dependence discussed in lectures).
     *
     * @param a
     * @param b
     * @return
     */
    

    @Override
    public boolean isControlDependentUpon(AbstractInsnNode a, AbstractInsnNode b) {
    	
    	//Instantiate nodes from abstract nodes(also makes it easier to get a signature)
    	Node aa = new Node(a);
    	Node bb = new Node(b);
    	
    	//create the start node for the augmented graph
    	Node start = new Node("start");
    	
    	try
    	{
    		//new control flow graph!
        	Graph myAwesomeGraph = CFGExtractor.getCFG(targetClassNode.name, targetMethod);
        	
          	
        	//add that start node and point it to entry and exit nodes(Augmented graph)
        	myAwesomeGraph.addNode(start);
        	myAwesomeGraph.addEdge(start, myAwesomeGraph.getEntry());
        	myAwesomeGraph.addEdge(start, myAwesomeGraph.getExit());
        	//System.out.println("****************CFG******************");
        	//System.out.println(myAwesomeGraph);
        	
        	
        	//calculate dominance from graph
        	DominanceTreeGenerator dom = new DominanceTreeGenerator(myAwesomeGraph);
        	
        	
        	//Oh boy more graphs! get post dominance graph        	
        	Graph postDom = dom.postDominatorTree();
        	//System.out.println("****************PDT******************");
        	//System.out.println(postDom);
        	
        	
        	//here we grab the least common ancestor(lca) of a and b
        	Node lca = postDom.getLeastCommonAncestor(aa, bb);
        	
        	//if (a,b) is not a branch or a post dominates b, abort. a would not be control dependent on b
        	if(!myAwesomeGraph.isDecisionEdge(bb, aa) || postDom.getSuccessors(bb).contains(aa)){
        		return false;
        	}
        	
        	
        	/*
        	 * System.out.println("Node A(Instruction)"+aa.toString());
        	System.out.println("Node B(Instruction)"+bb.toString());        	
        	System.out.println("The least common ancestor of (a,b) is "+lca.toString()+"\n\n");
        	*/
        	
        	//get the successors to lca. gonna cycle through them
        	Set<Node> s = postDom.getSuccessors(lca);
        	
        	
        	Collection<Node> c = null;
        	
        	
        	//to select nodes from a to least common ancestor(lca), determine which branch from lca reaches a
        	for(Node n : s){
        		c = postDom.getTransitiveSuccessors(n);
        		if(c.contains(aa)){//when we get the branch break
        			c.add(n);
        			break;
        		}
        	}
        	
        	//If the least common ancestor is b, include the ancestor itself in the
        	//selection.
        	if(lca.equals(bb)){
        		c.add(lca);
        	}
        	
        	//Create container for control dependence graph
        	Graph ctrlDepGraph = new Graph();
        	
        	//Add node b
        	ctrlDepGraph.addNode(bb);
        	
        	for(Node n : c){
        		//Now we create edges from b to each node in our collection
        		ctrlDepGraph.addNode(n);
        		ctrlDepGraph.addEdge(bb, n);
        	}
        	//Now we have a control dependence graph!
        	
        	//System.out.println("****************CDG For (a,b)******************");
        	//System.out.println(ctrlDepGraph+"\n\n");
        	
        	
        	if(ctrlDepGraph.getSuccessors(bb).contains(aa)){
        		return true;//yay a is control dependent on b! That's a good thing right?
        	}
    	}
    	catch(Exception e){
    		System.out.print("Welp...");
    		e.printStackTrace();
    	}
    	
        return false;
    }


    /**
     * Should return a backward slice on the criterion statement (for all variables).
     * @param criterion
     * @return
     */
    @Override
    public List<AbstractInsnNode> backwardSlice(AbstractInsnNode criterion) {
        //REPLACE THIS METHOD BODY WITH YOUR OWN CODE
        return null;
    }
}



