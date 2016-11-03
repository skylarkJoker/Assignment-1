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
        //REPLACE THIS METHOD BODY WITH YOUR OWN CODE
    	try{
    		
    		//Get a set of all variables defined by a
    		Collection db = DataFlowAnalysis.definedBy(targetClassNode.name, targetMethod, a);
    		
    		//get a set of varibles used by b
    		Collection us = DataFlowAnalysis.usedBy(targetClassNode.name, targetMethod, b);
    		
    		System.out.println(db.size());
    		
    		for(Variable v : (Collection<Variable>)db){
    			    			
    			if(us.contains(v)){//here we check to see if any of the variables defined by a are used by b    				
    				return true; 
    			}
    		}
    	}
    	catch(Exception e)
    	{
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
        	
        	//Create dominance graphs
        	DominanceTreeGenerator dom = new DominanceTreeGenerator(myAwesomeGraph);
        	
        	
        	//Oh boy more graphs! get post dominator graph
        	Graph postDom = dom.postDominatorTree();
        	//System.out.println(postDom);
        	
        	//Lets get see what the children are up to
        	if(postDom.getSuccessors(aa).size() > 1){
        		System.out.println("Node: " + aa.toString() + "\n Successors: ");
        		for(Node n : postDom.getSuccessors(aa)){
        			System.out.println(n.toString());
        		}
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



