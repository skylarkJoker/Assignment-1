package util;

import br.usp.each.saeg.asm.defuse.DefUseAnalyzer;
import br.usp.each.saeg.asm.defuse.DefUseFrame;
import br.usp.each.saeg.asm.defuse.Variable;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;

/**
 * Created by neilwalkinshaw on 21/10/2016.
 */
public class DataFlowAnalysis {

    /**
     * Return the collection of variables that are used by the specified statement.
     * @param owner
     * @param mn
     * @param statement
     * @return
     * @throws AnalyzerException
     */
    public static Collection<Variable> usedBy(String owner, MethodNode mn, AbstractInsnNode statement) throws AnalyzerException {
        DefUseAnalyzer analyzer = new DefUseAnalyzer();
        analyzer.analyze(owner, mn);
        DefUseFrame[] frames = analyzer.getDefUseFrames();
        int index = mn.instructions.indexOf(statement);
        return frames[index].getUses();
    }

    /**
     * Return the collection of variables that are defined by the specified statement.
     * @param owner
     * @param mn
     * @param statement
     * @return
     * @throws AnalyzerException
     */
    public static Collection<Variable> definedBy(String owner, MethodNode mn, AbstractInsnNode statement) throws AnalyzerException {
        DefUseAnalyzer analyzer = new DefUseAnalyzer();
        analyzer.analyze(owner, mn);

        DefUseFrame[] frames = analyzer.getDefUseFrames();
        int index = mn.instructions.indexOf(statement);
        return frames[index].getDefinitions();
    }

    public static void main(String[] args) throws IOException {
        ClassNode cn = new ClassNode(Opcodes.ASM4);
        InputStream in=DataFlowAnalysis.class.getResourceAsStream("/util/util.DataFlowAnalysis.class");
        ClassReader classReader=new ClassReader(in);
        classReader.accept(cn, 0);
        for(MethodNode mn : (List<MethodNode>)cn.methods){

            for(int i = 0; i< mn.instructions.size(); i++) {
                try {
                    Collection<Variable> defs = DataFlowAnalysis.definedBy("/util/util.DataFlowAnalysis.class",mn,mn.instructions.get(i));
                    Collection<Variable> uses = DataFlowAnalysis.definedBy("/util/util.DataFlowAnalysis.class",mn,mn.instructions.get(i));
                } catch (AnalyzerException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

}
