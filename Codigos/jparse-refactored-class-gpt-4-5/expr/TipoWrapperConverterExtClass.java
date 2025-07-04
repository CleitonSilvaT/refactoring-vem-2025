//class extracted from TypecastAST

package jparse.expr;

// TRECHO REFATORADO - Classe extraída dedicada exclusivamente à conversão de números para tipos wrappers adequados
public class TipoWrapperConverter {

    public static Object converter(Number num, Type theType) {
        if (theType == Type.byteType)
            return Byte.valueOf(num.byteValue());
        if (theType == Type.shortType)
            return Short.valueOf(num.shortValue());
        if (theType == Type.intType)
            return Integer.valueOf(num.intValue());
        if (theType == Type.longType)
            return Long.valueOf(num.longValue());
        if (theType == Type.floatType)
            return Float.valueOf(num.floatValue());
        return Double.valueOf(num.doubleValue());
    }
}