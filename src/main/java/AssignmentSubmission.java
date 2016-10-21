import org.objectweb.asm.tree.AbstractInsnNode;

import java.util.List;

/**
 * This should be the entry-point of your programming submission.
 *
 * You may add as many of your own classes and packages as you like to this project structure.
 *
 */
public class AssignmentSubmission implements Slicer {


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
        //REPLACE THIS METHOD BODY WITH YOUR OWN CODE
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
