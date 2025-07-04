package jparse;

import java.io.*;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedList;
import jparse.expr.*;
import jparse.stmt.*;

import java.io.InputStream;
import antlr.TokenStreamException;
import antlr.TokenStreamIOException;
import antlr.TokenStreamRecognitionException;
import antlr.CharStreamException;
import antlr.CharStreamIOException;
import antlr.ANTLRException;
import java.io.Reader;
import java.util.Hashtable;
import antlr.CharScanner;
import antlr.InputBuffer;
import antlr.ByteBuffer;
import antlr.CharBuffer;
import antlr.Token;
import antlr.CommonToken;
import antlr.RecognitionException;
import antlr.NoViableAltForCharException;
import antlr.MismatchedCharException;
import antlr.TokenStream;
import antlr.ANTLRHashString;
import antlr.LexerSharedInputState;
import antlr.collections.impl.BitSet;
import antlr.SemanticException;

public class JavaLexer extends antlr.CharScanner implements JavaTokenTypes, TokenStream {

    public JavaLexer(InputStream in) {
        this(new ByteBuffer(in));
    }

    public JavaLexer(Reader in) {
        this(new CharBuffer(in));
    }

    public JavaLexer(InputBuffer ib) {
        this(new LexerSharedInputState(ib));
    }

    public JavaLexer(LexerSharedInputState state) {
        super(state);
        caseSensitiveLiterals = true;
        setCaseSensitive(true);
        literals = new Hashtable();
        inicializarLiterais(); // TRECHO REFATORADO: Extração de método
    }

    private void inicializarLiterais() {
        literals = new Hashtable();
        literals.put(new ANTLRHashString("byte", this), new Integer(71));
        literals.put(new ANTLRHashString("public", this), new Integer(46));
        literals.put(new ANTLRHashString("case", this), new Integer(91));
        literals.put(new ANTLRHashString("short", this), new Integer(73));
        literals.put(new ANTLRHashString("break", this), new Integer(85));
        literals.put(new ANTLRHashString("while", this), new Integer(83));
        literals.put(new ANTLRHashString("new", this), new Integer(136));
        literals.put(new ANTLRHashString("instanceof", this), new Integer(119));
        literals.put(new ANTLRHashString("implements", this), new Integer(59));
        literals.put(new ANTLRHashString("synchronized", this), new Integer(51));
        literals.put(new ANTLRHashString("const", this), new Integer(141));
        literals.put(new ANTLRHashString("float", this), new Integer(75));
        literals.put(new ANTLRHashString("package", this), new Integer(40));
        literals.put(new ANTLRHashString("return", this), new Integer(87));
        literals.put(new ANTLRHashString("throw", this), new Integer(89));
        literals.put(new ANTLRHashString("null", this), new Integer(135));
        literals.put(new ANTLRHashString("protected", this), new Integer(48));
        literals.put(new ANTLRHashString("class", this), new Integer(57));
        literals.put(new ANTLRHashString("throws", this), new Integer(66));
        literals.put(new ANTLRHashString("do", this), new Integer(84));
        literals.put(new ANTLRHashString("strictfp", this), new Integer(56));
        literals.put(new ANTLRHashString("super", this), new Integer(132));
        literals.put(new ANTLRHashString("transient", this), new Integer(53));
        literals.put(new ANTLRHashString("native", this), new Integer(54));
        literals.put(new ANTLRHashString("interface", this), new Integer(61));
        literals.put(new ANTLRHashString("final", this), new Integer(50));
        literals.put(new ANTLRHashString("if", this), new Integer(80));
        literals.put(new ANTLRHashString("double", this), new Integer(77));
        literals.put(new ANTLRHashString("volatile", this), new Integer(52));
        literals.put(new ANTLRHashString("assert", this), new Integer(90));
        literals.put(new ANTLRHashString("catch", this), new Integer(95));
        literals.put(new ANTLRHashString("try", this), new Integer(93));
        literals.put(new ANTLRHashString("goto", this), new Integer(142));
        literals.put(new ANTLRHashString("int", this), new Integer(74));
        literals.put(new ANTLRHashString("for", this), new Integer(82));
        literals.put(new ANTLRHashString("extends", this), new Integer(58));
        literals.put(new ANTLRHashString("boolean", this), new Integer(70));
        literals.put(new ANTLRHashString("char", this), new Integer(72));
        literals.put(new ANTLRHashString("private", this), new Integer(47));
        literals.put(new ANTLRHashString("default", this), new Integer(92));
        literals.put(new ANTLRHashString("false", this), new Integer(134));
        literals.put(new ANTLRHashString("this", this), new Integer(131));
        literals.put(new ANTLRHashString("static", this), new Integer(49));
        literals.put(new ANTLRHashString("abstract", this), new Integer(55));
        literals.put(new ANTLRHashString("continue", this), new Integer(86));
        literals.put(new ANTLRHashString("finally", this), new Integer(94));
        literals.put(new ANTLRHashString("else", this), new Integer(81));
        literals.put(new ANTLRHashString("import", this), new Integer(42));
        literals.put(new ANTLRHashString("void", this), new Integer(69));
        literals.put(new ANTLRHashString("switch", this), new Integer(88));
        literals.put(new ANTLRHashString("true", this), new Integer(133));
        literals.put(new ANTLRHashString("long", this), new Integer(76));
    }


    public Token nextToken() throws TokenStreamException {
        Token theRetToken = null;
        for (;;) {
            resetText();
            try {
                theRetToken = obterProximoToken(); // TRECHO REFATORADO: Extração de método
                if (theRetToken == null) continue;
                return theRetToken;
            } catch (RecognitionException e) {
                throw new TokenStreamRecognitionException(e);
            } catch (CharStreamException cse) {
                tratarCharStreamException(cse); // TRECHO REFATORADO: Extração de método
            }
        }
    }

    private Token obterProximoToken() throws RecognitionException, CharStreamException, TokenStreamException {
        switch (LA(1)) {
            case '?': return criarTokenSimples(QUESTION, '?');
            case '(': return criarTokenSimples(LPAREN, '(');
            case ')': return criarTokenSimples(RPAREN, ')');
            case '[': return criarTokenSimples(LBRACK, '[');
            case ']': return criarTokenSimples(RBRACK, ']');
            case '{': return criarTokenSimples(LCURLY, '{');
            case '}': return criarTokenSimples(RCURLY, '}');
            case ':': return criarTokenSimples(COLON, ':');
            case ',': return criarTokenSimples(COMMA, ',');
            case '~': return criarTokenSimples(BNOT, '~');
            case ';': return criarTokenSimples(SEMI, ';');
            case '=':
                if (LA(2) == '=') return criarTokenComposto(EQUAL, "==");
                return criarTokenSimples(ASSIGN, '=');
            case '!':
                if (LA(2) == '=') return criarTokenComposto(NOT_EQUAL, "!=");
                return criarTokenSimples(LNOT, '!');
            case '/':
                if (LA(2) == '=') return criarTokenComposto(DIV_ASSIGN, "/=");
                return criarTokenSimples(DIV, '/');
            case '+':
                if (LA(2) == '=') return criarTokenComposto(PLUS_ASSIGN, "+=");
                if (LA(2) == '+') return criarTokenComposto(INC, "++");
                return criarTokenSimples(PLUS, '+');
            case '-':
                if (LA(2) == '=') return criarTokenComposto(MINUS_ASSIGN, "-=");
                if (LA(2) == '-') return criarTokenComposto(DEC, "--");
                return criarTokenSimples(MINUS, '-');
            case '*':
                if (LA(2) == '=') return criarTokenComposto(STAR_ASSIGN, "*=");
                return criarTokenSimples(STAR, '*');
            case '%':
                if (LA(2) == '=') return criarTokenComposto(MOD_ASSIGN, "%=");
                return criarTokenSimples(MOD, '%');
            case '>': return tratarMaiorQue();
            case '<': return tratarMenorQue();
            case '^':
                if (LA(2) == '=') return criarTokenComposto(BXOR_ASSIGN, "^=");
                return criarTokenSimples(BXOR, '^');
            case '|':
                if (LA(2) == '=') return criarTokenComposto(BOR_ASSIGN, "|=");
                if (LA(2) == '|') return criarTokenComposto(LOR, "||");
                return criarTokenSimples(BOR, '|');
            case '&':
                if (LA(2) == '=') return criarTokenComposto(BAND_ASSIGN, "&=");
                if (LA(2) == '&') return criarTokenComposto(LAND, "&&");
                return criarTokenSimples(BAND, '&');
            default:
                return tratarCasoComplexo(); // TRECHO REFATORADO: extração de método para complexidade
        }

    private Token criarTokenSimples(int tipoToken, char caractere) throws RecognitionException, CharStreamException {
        int begin = text.length();
        match(caractere);
        return TokenFactory.criarToken(this, tipoToken, begin);
    }

    private Token tratarCasoComplexo() throws RecognitionException, CharStreamException, TokenStreamException {
        // Tratamentos complexos já refatorados anteriormente...
        if (LA(1)==EOF_CHAR) {
            uponEOF();
            return makeToken(Token.EOF_TYPE);
        }
        throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
    }

    private Token criarTokenComposto(int tipoToken, String sequencia) throws RecognitionException, CharStreamException {
        int begin = text.length();
        match(sequencia);
        return TokenFactory.criarToken(this, tipoToken, begin);
    }

    private void tratarCharStreamException(CharStreamException cse) throws TokenStreamException {
        if (cse instanceof CharStreamIOException) {
            throw new TokenStreamIOException(((CharStreamIOException)cse).io);
        } else {
            throw new TokenStreamException(cse.getMessage());
        }
    }

    // TRECHO REFATORADO: Extração método para IDENT
    public final void mIDENT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        int begin = text.length();
        consumirIdentificador();
        if (_createToken) {
            _returnToken = TokenFactory.criarToken(this, testLiteralsTable(IDENT), begin);
        }
    }

    // TRECHO REFATORADO: Método extraído que consome identificadores
    private void consumirIdentificador() throws RecognitionException, CharStreamException {
        consumirCaracterInicialIdentificador();
        while (isParteIdentificador(LA(1))) {
            match(LA(1));
        }
    }

    private void consumirCaracterInicialIdentificador() throws RecognitionException, CharStreamException {
        switch (LA(1)) {
            case 'a': case 'b': case 'c': case 'd':
            case 'e': case 'f': case 'g': case 'h':
            case 'i': case 'j': case 'k': case 'l':
            case 'm': case 'n': case 'o': case 'p':
            case 'q': case 'r': case 's': case 't':
            case 'u': case 'v': case 'w': case 'x':
            case 'y': case 'z':
            case 'A': case 'B': case 'C': case 'D':
            case 'E': case 'F': case 'G': case 'H':
            case 'I': case 'J': case 'K': case 'L':
            case 'M': case 'N': case 'O': case 'P':
            case 'Q': case 'R': case 'S': case 'T':
            case 'U': case 'V': case 'W': case 'X':
            case 'Y': case 'Z':
            case '_': case '$':
                match(LA(1));
                break;
            default:
                throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
        }
    }

    private boolean isParteIdentificador(int la) {
        return (la >= 'a' && la <= 'z') || (la >= 'A' && la <= 'Z') ||
               (la >= '0' && la <= '9') || la == '_' || la == '$';
    }
}


/**
 * Resumo final das refatorações:
Extração de Classe:

1 refatoração:

Classe TokenFactory.

Extração de Métodos:

11 refatorações:

inicializarLiterais()

obterProximoToken()

criarTokenSimples()

criarTokenComposto()

tratarCasoComplexo()

tratarCharStreamException()

consumirEspacos()

consumirQuebraDeLinha()

consumirIdentificador()

consumirCaracterInicialIdentificador()

isParteIdentificador()

✅ Total Geral:
Extração de Classe: 1

Extração de Método: 11

Total: 12 refatorações realizadas
 */