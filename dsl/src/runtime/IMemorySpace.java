package runtime;

public interface IMemorySpace {
    boolean bindValue(String name, Value value);
    Value resolve(String name);
    Value resolve(String name, boolean resolveInParent);
}
