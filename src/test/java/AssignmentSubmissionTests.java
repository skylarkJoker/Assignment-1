

import static org.junit.Assert.*;

import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import util.cfg.Node;

public class AssignmentSubmissionTests {

	AssignmentSubmission testSubmission;
	ClassNode targetClass;
	MethodNode targetMethod; 
	
	@Before
	public void setUp() throws Exception {
		testSubmission = new AssignmentSubmission("/java/lang/String.class","toCharArray()[C");
		targetClass = AssignmentSubmission.findClassNode("/java/lang/String.class");
		targetMethod = AssignmentSubmission.findMethodNode(targetClass, "toCharArray()[C");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testControlDependence() {
		AbstractInsnNode a, b;
		
		for(int i = 0; i < testSubmission.targetMethod.instructions.size(); i ++){
			for(int j = 0; j < testSubmission.targetMethod.instructions.size(); j ++){
				a = testSubmission.targetMethod.instructions.get(i);
				b = testSubmission.targetMethod.instructions.get(j);
				
				Node aa = new Node(a);
				Node bb = new Node(b);
				
				System.out.println(aa.toString()+"\t"+ bb.toString());
				System.out.println(testSubmission.isControlDependentUpon(a, b));
			}
		}
				
	}

}
