package jparse;


public interface Method extends HasExceptions {

    public Type getDeclaringClass();
    public String getName();
    public int getModifiers();
    public Type getReturnType();
    public Type[] getParameterTypes();
    public Type[] getExceptionTypes();
    public boolean isAccessible(Type caller);
    public boolean match(String name, Type[] params, Type caller);
    public boolean match(Type[] params, Type caller);
    public Method bestMatch(Method meth);
    public boolean exactMatch(Method meth);
}
