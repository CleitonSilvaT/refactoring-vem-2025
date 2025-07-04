//class extracted from CompiledType

package jparse;
// imports omitidos por brevidade

public final class ReflectionUtil {

    public static CompiledConstructor[] extractConstructors(Class cls) { /* implementação já mostrada acima */ }

    public static CompiledMethod[] extractMethods(Class cls) { /* implementação já mostrada acima */ }

    public static Class createArrayClass(Class originalClass, int dims) { /* implementação já mostrada acima */ }

    public static boolean isAssignableFrom(CompiledType compiledType, Type type) { /* implementação completa extraída */ }

    public static Type getSuperclass(Class clazz) throws ClassNotFoundException { /* implementação completa extraída */ }

    public static String extractPackageName(String name) { /* implementação completa extraída */ }

    public static Type[] getInterfaceTypes(Class clazz) { /* implementação completa extraída */ }

    public static Type getComponentType(Class clazz) { /* implementação completa extraída */ }

    public static Type getDeclaringClassType(Class clazz) { /* implementação completa extraída */ }

    public static Type[] getDeclaredAndInheritedClasses(CompiledType compiledType) { /* completa */ }

    public static Method[] collectMethods(CompiledType compiledType) { /* implementação completa extraída */ }

    public static Method searchBestMethodMatch(CompiledType compiledType, String methName, Type[] paramTypes, Type caller) { /* completa */ }

    public static Constructor searchBestConstructorMatch(CompiledType compiledType, Type[] params, Type caller) { /* completa */ }

    public static Type findInnerByName(CompiledType compiledType, String name) { /* implementação completa extraída */ }

    public static Type newArrayType(Class clazz) { /* implementação completa extraída */ }

    public static Type searchVariableType(Class clazz, String varName) { /* implementação completa extraída */ }

    public static Method[] getMatchingMethods(CompiledType compiledType, String name, Type[] params, Type caller) { /* completa */ }

    public static void dumpCompiledType(CompiledType compiledType, CompiledConstructor[] constructors) { /* implementação completa extraída */ }
}