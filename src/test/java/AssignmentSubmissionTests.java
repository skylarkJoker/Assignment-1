

import static org.junit.Assert.*;

import java.util.List;
import java.util.Random;
import java.util.Set;

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
		testSubmission = new AssignmentSubmission("/java/lang/String.class","trim()Ljava/lang/String;");
		targetClass = AssignmentSubmission.findClassNode("/java/lang/String.class");
		targetMethod = AssignmentSubmission.findMethodNode(targetClass, "trim()Ljava/lang/String;");
	}

	@After
	public void tearDown() throws Exception {
	}

	//@Test
	public void testControlDependence() {
	
			AbstractInsnNode a, b;
		a = testSubmission.targetMethod.instructions.get(15);
		b = testSubmission.targetMethod.instructions.get(20);
				
		System.out.println(testSubmission.isControlDependentUpon(a, b));	
	}
	
	@Test
	public void testDataDependence(){
	

		AbstractInsnNode a, b;	
		a = testSubmission.targetMethod.instructions.get(1);
		b = testSubmission.targetMethod.instructions.get(3);
				
		System.out.println(testSubmission.isDataDepence(a, b));

	}

}
