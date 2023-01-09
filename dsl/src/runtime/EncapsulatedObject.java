package runtime;

import semanticAnalysis.types.AggregateType;
import semanticAnalysis.types.IType;
import semanticAnalysis.types.TypeBuilder;

import java.lang.reflect.Field;
import java.util.HashMap;

// TODO: does this also apply to single values? how would the setting of a value be implemented
//  otherwise? e.g. the value of an object from a function by member access..
//  ..
//  could we just use the object retrieved from the reflection-based lookup and pass it to the
//  value? does this preserve the "referenceness" of the object?
//  initially, that seems implausable, especially for POD types.. but after all, this is java, so
//  who knows? doc seems to suggest, that this won't work.. which means more reflection, yes
public class EncapsulatedObject implements IMemorySpace {
    private IMemorySpace parent;
    private Object innerObject;
    private AggregateType type;
    private HashMap<String, Field> typeMemberToField;
    private IEvironment environment;

    public EncapsulatedObject(Object innerObject, AggregateType type, MemorySpace parent, IEvironment environment) {
        assert innerObject.getClass().equals(type.getOriginalJavaClass());

        this.parent = parent;
        this.type = type;
        this.innerObject = innerObject;

        buildFieldMap(innerObject.getClass(), type);
    }

    private void buildFieldMap(Class<?> clazz, AggregateType type) {
        var nameMap = TypeBuilder.typeMemberNameToJavaFieldMap(clazz);

        for (var member : type.getSymbols()) {
            var fieldName = nameMap.get(member.getName());
            try {
                Field field = clazz.getDeclaredField(fieldName);
                typeMemberToField.put(member.getName(), field);
            } catch (NoSuchFieldException e) {
                // TODO: handle
            }
        }
    }

    @Override
    public boolean bindValue(String name, Value value) {
        return false;
    }

    @Override
    public Value resolve(String name) {
        // lookup name
        Field correspondingField = this.typeMemberToField.getOrDefault(name, null);
        if (correspondingField == null) {
            return Value.NONE;
        } else {
            // read field value
            correspondingField.setAccessible(true);
            try {
                var value = correspondingField.get(this.innerObject);
                // convert the read field value to a DSL 'Value'
                // this may require recursive creation of encapsulated objects,
                // if the field is a component for example
                var type = TypeBuilder.getDSLTypeForMember(value.getClass());
                if (type == null) {
                    var dslTypeName = TypeBuilder.getDSLName(value.getClass());
                    var typeFromGlobalScope = this.environment.getGlobalScope().resolve(dslTypeName);
                    if (typeFromGlobalScope instanceof IType) {
                        type = (IType)typeFromGlobalScope;
                        // if we reach this point, then the field in the actual java class
                        // has a representation in the dsl type system, which means
                        // that we should be able to just construct a new EncapsulatedObject
                        // around it -> which should be cached;
                    }
                }

                // but this type lookup requires access to all types from the symbol table

                // TODO: does this need to hold a reference to the whole environment now?
            } catch (IllegalAccessException e) {
                // TODO: handle
                return Value.NONE;
            }
        }
    }

    @Override
    public Value resolve(String name, boolean resolveInParent) {
        return null;
    }
}
