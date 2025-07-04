//class extracted from CompiledMethod

package jparse;

import java.lang.reflect.Modifier;

public class AccessibilityChecker { // TRECHO REFATORADO

    private final CompiledMethod method;

    public AccessibilityChecker(final CompiledMethod method) {
        this.method = method;
    }

    public boolean isAccessible(final Type caller) {
        final int mod = method.getModifiers();
        if (Modifier.isPublic(mod)) return true;

        final Type myType = method.getDeclaringClass();
        if (Modifier.isProtected(mod)) {
            return myType.getPackage().equals(caller.getPackage()) || myType.superClassOf(caller);
        }

        if (Modifier.isPrivate(mod)) {
            for (Type t = caller; t != null; t = t.getDeclaringClass())
                if (t == myType) return true;
            return false;
        }

        return myType.getPackage().equals(caller.getPackage());
    }
}