package jparse;

import java.lang.reflect.Array;
import java.util.ArrayList;

public final class ReflectionUtil {

    // TRECHO REFATORADO
    public static CompiledConstructor[] extractConstructors(Class cls) {
        final java.lang.reflect.Constructor[] realConstrs = cls.getDeclaredConstructors();
        CompiledConstructor[] constrs = new CompiledConstructor[realConstrs.length];
        for (int i = 0; i < realConstrs.length; i++)
            constrs[i] = new CompiledConstructor(realConstrs[i]);
        return constrs;
    }

    // TRECHO REFATORADO
    public static CompiledMethod[] extractMethods(Class cls) {
        final java.lang.reflect.Method[] realMeths = cls.getDeclaredMethods();
        CompiledMethod[] meths = new CompiledMethod[realMeths.length];
        for (int i = 0; i < realMeths.length; i++)
            meths[i] = new CompiledMethod(realMeths[i]);
        return meths;
    }

    // TRECHO REFATORADO
    public static Class createArrayClass(Class originalClass, int dims) {
        Class cls = originalClass;
        for (int i = 0; i < dims; i++)
            cls = Array.newInstance(cls, 1).getClass();
        return cls;
    }

    // Mais métodos auxiliares extraídos, exemplos simplificados:

    // TRECHO REFATORADO
    public static boolean isNullAssignable(Type type, Class destClass) {
        return type == null && !destClass.isPrimitive();
    }

    // TRECHO REFATORADO
    public static boolean isPrimitiveAssignable(Type type, Class destClass) {
        return type != null && type.isPrimitive() && destClass.isPrimitive();
    }

    // TRECHO REFATORADO
    public static boolean checkPrimitiveCompatibility(CompiledType type, Class destClass) {
        final Class tClass = type.theClass;
        if (destClass == void.class || tClass == void.class)
            return false;
        if (destClass == double.class)
            return true;
        if (destClass == float.class)
            return tClass != double.class;
        if (destClass == long.class)
            return tClass != double.class && tClass != float.class;
        if (destClass == int.class)
            return tClass != double.class && tClass != float.class && tClass != long.class;
        if (destClass == short.class)
            return tClass == byte.class || tClass == short.class;
        if (destClass == char.class)
            return tClass == byte.class || tClass == char.class;
        return tClass == byte.class;
    }

    // demais métodos omitidos para brevidade
}