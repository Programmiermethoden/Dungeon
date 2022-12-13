package runtime.nativeFunctions;

import interpreter.DSLInterpreter;
import java.util.List;
import parser.AST.Node;
import symboltable.BuiltInType;
import symboltable.ICallable;
import symboltable.IScope;
import symboltable.ScopedSymbol;
import symboltable.Symbol;

// TODO: how to enable semantic analysis for this? e.g. parameter-count, etc.
public class NativePrint extends ScopedSymbol implements ICallable {
    public NativePrint(IScope parentScope) {
        super("print", parentScope, BuiltInType.intType);

        // bind parameters
        Symbol param = new Symbol("param", this, BuiltInType.stringType);
        this.Bind(param);
    }

    @Override
    public Object call(DSLInterpreter interperter, List<Node> parameters) {
        assert parameters != null && parameters.size() > 0;
        try {
            String paramAsString = (String) parameters.get(0).accept(interperter);
            System.out.println(paramAsString);
        } catch (ClassCastException ex) {
            // TODO: handle
        }
        return null;
    }

    @Override
    public ICallable.Type getCallableType() {
        return ICallable.Type.Native;
    }
}
