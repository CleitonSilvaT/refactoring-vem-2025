// TRECHO REFATORADO
package jparse;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class ClasspathManager {
    protected static final HashMap pkgMap = new HashMap();
    private static final String[] classPath;

    static {
        final ArrayList paths = new ArrayList();
        final char sep = File.pathSeparatorChar;

        explodeString(System.getProperty("env.class.path"), sep, paths);
        explodeString(System.getProperty("sun.boot.class.path"), sep, paths);
        explodeString(System.getProperty("java.class.path"), sep, paths);
        explodeString(System.getProperty("java.ext.dirs"), sep, paths);

        classPath = new String[paths.size()];
        paths.toArray(classPath);
    }

    private static void explodeString(final String s, final char c,
                                      final ArrayList list) {
        if (s == null || s.length() == 0)
            return;

        int start, end;
        for (start = end = 0; ; start = end + 1) {
            end = s.indexOf(c, start);
            if (end < 0)
                break;
            final String part = s.substring(start, end);
            if (!list.contains(part))
                list.add(part);
            start = end + 1;
        }
        final String part = s.substring(start);
        if (!list.contains(part))
            list.add(part);
    }

    public static File findFile(final String name, final boolean source) {
        final int index = name.lastIndexOf('.');
        final String pkgName = (index == -1) ? "" : name.substring(0, index);
        final File pkg = (File) pkgMap.get(pkgName);
        if (pkg != null) {
            final File file = new File(pkg, name.substring(index + 1));
            return (file.exists()) ? file : null;
        }

        final String realName = File.separator
                + name.replace('.', File.separatorChar)
                + (source ? ".java" : ".class");
        for (int i = 0; i < classPath.length; i++) {
            final File file = new File(classPath[i] + realName);
            if (file.exists()) {
                pkgMap.put(pkgName, file.getParentFile());
                return file;
            }
        }
        return null;
    }
}
