package runtime;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

// TODO: also use this for object-instantiation?
// TODO: does this need to be specialized for function memory space -> just try it
public class MemorySpace implements IMemorySpace {
    public static MemorySpace NONE;
    private final HashMap<String, Value> values = new HashMap<>();
    private final MemorySpace parent;

    /**
     * Constructor
     *
     * @param parent parent MemorySpace
     */
    public MemorySpace(MemorySpace parent) {
        this.parent = parent;
    }

    /** Constructor, parent will be set to NONE */
    public MemorySpace() {
        this.parent = NONE;
    }

    // TODO: should binding really be part of the memory space?
    //  the memory space is basically a storage for values and should not implement
    //  this behaviour itself -> would also simplify making this an interface, which
    //  would be helpful to also make an encapsulated java object a memory space
    //  (which pipes value-access to the actual fields in the java objects)
    /*
    /**
     * Bind a new {@link Value} in this memory space from a {@link Symbol} and will set it's
     * underlying value to the default value for it's type. This will also store the symbols idx in
     * the {@link Value} object. If a Value with the name already exists, no new Value will be
     * created.
     *
     * @param symbol the {@link Symbol} to create and bind a new {@link Value} from
     * @return True, if creation and binding succeeded, false otherwise.
     */
    /*public boolean bindFromSymbol(Symbol symbol) {
        var symbolName = symbol.getName();
        if (symbol.equals(Symbol.NULL)) {
            return false;
        }
        if (values.containsKey(symbolName)) {
            return false;
        } else if (!(symbol instanceof IType)) {
            var value = createDefaultValue(symbol.getDataType(), symbol.getIdx());
            values.put(symbolName, value);
            return true;
        }
        return false;
    }*/

    /*private Value createDefaultValue(IType type, int symbolIdx) {
        if (type.getTypeKind().equals(IType.Kind.Basic)) {
            Object internalValue = Value.getDefaultValue(type);
            return new Value(type, internalValue, symbolIdx);
        } else {
            AggregateValue value = new AggregateValue((AggregateType) type, symbolIdx, this);
            for (var member : ((AggregateType) type).getSymbols()) {
                value.getMemorySpace().bindFromSymbol(member);
            }
            return value;
        }
    }*/

    /*
    public boolean bind(String name, IType dataType, int symbolIdx) {
        if (values.containsKey(name)) {
            return false;
        } else {
            // TODO: needs to be able to create default values for aggregateTypes -> needs reference
            // to environment
            var defaultValue = createDefaultValue(dataType, symbolIdx);
            var val = new Value(dataType, defaultValue, symbolIdx);
            values.put(name, val);
            return true;
        }
    }
     */

    /**
     * Returns all stored Values
     *
     * @return A Set containing all entries from the value Map
     */
    public Set<Map.Entry<String, Value>> getAllValues() {
        return values.entrySet();
    }

    @Override
    public boolean bindValue(String name, Value value) {
        if (value.equals(Value.NONE)) {
            return false;
        }
        if (values.containsKey(name)) {
            return false;
        } else {
            values.put(name, value);
            return true;
        }
    }

    /**
     * Lookup passed name and return the stored {@link Value}, if one exists. If no Value was found
     * in own stored values, resolve it in the parent {@link MemorySpace}
     *
     * @param name The name to resolve
     * @return The resolved {@link Value} or Value.NONE, if the name could not be resolved
     */
    @Override
    public Value resolve(String name) {
        return this.resolve(name, true);
    }

    /**
     * Lookup passed name and return the stored {@link Value}, if one exists.
     *
     * @param name The name to resolve
     * @param resolveInParent if set to true, and no {@link Value} could be found in own stored
     *     values, the name will be resolved in the parent {@link MemorySpace}
     * @return The resolved {@link Value} or Value.NONE, if the name could not be resolved
     */
    @Override
    public Value resolve(String name, boolean resolveInParent) {
        if (this.values.containsKey(name)) {
            return this.values.get(name);
        } else if (this.parent != NONE && resolveInParent) {
            return this.parent.resolve(name, true);
        } else {
            return Value.NONE;
        }
    }
}
