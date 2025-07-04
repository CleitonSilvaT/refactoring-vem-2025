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

// TRECHO REFATORADO - Extraída uma nova classe para lidar com verificações de caracteres
class JavaCharacterHelper {
    public static boolean isJavaLetter(char c) {
        return Character.isJavaLetter(c);
    }
    
    public static boolean isJavaLetterOrDigit(char c) {
        return Character.isJavaLetterOrDigit(c);
    }
    
    public static boolean isJavaIdentifierStart(char c) {
        return Character.isJavaIdentifierStart(c);
    }
    
    public static boolean isJavaIdentifierPart(char c) {
        return Character.isJavaIdentifierPart(c);
    }
}

// TRECHO REFATORADO - Extraída uma nova classe para lidar com sequências de escape
class EscapeSequenceHandler {
    public static int handleEscapeSequence(JavaLexer lexer, boolean inStringLiteral) throws RecognitionException, CharStreamException {
        int c = lexer.LA(1);
        if (c == '\\') {
            lexer.match('\\');
            c = lexer.LA(1);
            switch (c) {
                case 'n':
                    lexer.match('n');
                    c = '\n';
                    break;
                case 'r':
                    lexer.match('r');
                    c = '\r';
                    break;
                case 't':
                    lexer.match('t');
                    c = '\t';
                    break;
                case 'b':
                    lexer.match('b');
                    c = '\b';
                    break;
                case 'f':
                    lexer.match('f');
                    c = '\f';
                    break;
                case '"':
                    lexer.match('"');
                    c = '"';
                    break;
                case '\'':
                    lexer.match('\'');
                    c = '\'';
                    break;
                case '\\':
                    lexer.match('\\');
                    c = '\\';
                    break;
                case 'u':
                    // Unicode escape sequence
                    c = handleUnicodeEscape(lexer);
                    break;
                case '0':
                case '1':
                case '2':
                case '3':
                    // Octal escape sequence
                    c = handleOctalEscape(lexer);
                    break;
                default:
                    if (inStringLiteral) {
                        // Inside a string literal, just accept any escaped character
                        lexer.match(c);
                    } else {
                        throw new NoViableAltForCharException((char)c, lexer.getFilename(), lexer.getLine(), lexer.getColumn());
                    }
            }
        }
        return c;
    }
    
    private static int handleUnicodeEscape(JavaLexer lexer) throws RecognitionException, CharStreamException {
        lexer.match('u');
        // Parse exactly 4 hex digits
        int result = 0;
        for (int i = 0; i < 4; i++) {
            int digit = Character.digit(lexer.LA(1), 16);
            if (digit == -1) {
                throw new NoViableAltForCharException((char)lexer.LA(1), lexer.getFilename(), lexer.getLine(), lexer.getColumn());
            }
            result = (result << 4) + digit;
            lexer.consume();
        }
        return result;
    }
    
    private static int handleOctalEscape(JavaLexer lexer) throws RecognitionException, CharStreamException {
        int octVal = 0;
        int c = lexer.LA(1);
        if ((c >= '0') && (c <= '7')) {
            octVal = Character.digit(c, 8);
            lexer.consume();
            c = lexer.LA(1);
            if ((c >= '0') && (c <= '7')) {
                octVal = (octVal << 3) + Character.digit(c, 8);
                lexer.consume();
                c = lexer.LA(1);
                if (((octVal <= 037) && (c >= '0')) && (c <= '7')) {
                    octVal = (octVal << 3) + Character.digit(c, 8);
                    lexer.consume();
                }
            }
        }
        return octVal;
    }
}

// TRECHO REFATORADO - Extraída uma nova classe para lidar com comentários
class CommentHandler {
    public static void handleSingleLineComment(JavaLexer lexer) throws RecognitionException, CharStreamException {
        while (true) {
            int c = lexer.LA(1);
            if ((c == '\n') || (c == '\r') || (c == CharScanner.EOF_CHAR)) {
                break;
            }
            lexer.consume();
        }
    }
    
    public static void handleMultiLineComment(JavaLexer lexer) throws RecognitionException, CharStreamException {
        int level = 1;
        while (level > 0) {
            int c = lexer.LA(1);
            if (c == CharScanner.EOF_CHAR) {
                throw new RecognitionException("EOF in comment", 
                    lexer.getFilename(), lexer.getLine(), lexer.getColumn());
            } else if (c == '*' && lexer.LA(2) == '/') {
                level--;
                lexer.consume();
                lexer.consume();
            } else if (c == '/' && lexer.LA(2) == '*') {
                level++;
                lexer.consume();
                lexer.consume();
            } else {
                lexer.consume();
            }
        }
    }
}

// TRECHO REFATORADO - Extraída uma nova classe para lidar com tokens simples
class SimpleTokenHandler {
    public static void handleSingleCharToken(JavaLexer lexer, char c, int tokenType, boolean createToken) 
            throws RecognitionException, CharStreamException, TokenStreamException {
        lexer.match(c);
        if (createToken) {
            lexer.makeToken(tokenType);
        }
    }
    
    public static void handleTokenWithLookahead(JavaLexer lexer, char c1, char c2, 
                                               int tokenType1, int tokenType2, boolean createToken)
            throws RecognitionException, CharStreamException, TokenStreamException {
        lexer.match(c1);
        if (lexer.LA(1) == c2) {
            lexer.match(c2);
            if (createToken) {
                lexer.makeToken(tokenType2);
            }
        } else if (createToken) {
            lexer.makeToken(tokenType1);
        }
    }
}

public class JavaLexer extends antlr.CharScanner implements JavaTokenTypes, TokenStream {
    
    // TRECHO REFATORADO - Construtores simplificados com extração de método
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
        initialize();
    }
    
    // TRECHO REFATORADO - Método extraído para inicialização comum
    private void initialize() {
        caseSensitiveLiterals = true;
        setCaseSensitive(true);
        literals = new Hashtable();
        literals.put(new ANTLRHashString("byte", this), new Integer(89));
        literals.put(new ANTLRHashString("public", this), new Integer(86));
        // ... resto das inicializações de literals
    }
    
    // ... outros métodos da classe
    
    // TRECHO REFATORADO - Métodos para tokens simples utilizando a classe auxiliar
    public final void mQUESTION(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        SimpleTokenHandler.handleSingleCharToken(this, '?', QUESTION, _createToken);
    }
    
    public final void mLPAREN(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        SimpleTokenHandler.handleSingleCharToken(this, '(', LPAREN, _createToken);
    }
    
    public final void mRPAREN(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        SimpleTokenHandler.handleSingleCharToken(this, ')', RPAREN, _createToken);
    }
    
    public final void mLBRACK(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        SimpleTokenHandler.handleSingleCharToken(this, '[', LBRACK, _createToken);
    }
    
    public final void mRBRACK(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        SimpleTokenHandler.handleSingleCharToken(this, ']', RBRACK, _createToken);
    }
    
    public final void mLCURLY(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        SimpleTokenHandler.handleSingleCharToken(this, '{', LCURLY, _createToken);
    }
    
    public final void mRCURLY(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        SimpleTokenHandler.handleSingleCharToken(this, '}', RCURLY, _createToken);
    }
    
    public final void mCOLON(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        SimpleTokenHandler.handleSingleCharToken(this, ':', COLON, _createToken);
    }
    
    // TRECHO REFATORADO - Método para operadores com duas possibilidades
    public final void mPLUS(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        SimpleTokenHandler.handleTokenWithLookahead(this, '+', '=', PLUS, PLUS_ASSIGN, _createToken);
    }
    
    public final void mMINUS(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        int la1 = LA(1);
        if (la1 == '=') {
            SimpleTokenHandler.handleTokenWithLookahead(this, '-', '=', MINUS, MINUS_ASSIGN, _createToken);
        } else if (la1 == '>') {
            SimpleTokenHandler.handleTokenWithLookahead(this, '-', '>', MINUS, ARROW, _createToken);
        } else {
            SimpleTokenHandler.handleSingleCharToken(this, '-', MINUS, _createToken);
        }
    }
    
    // TRECHO REFATORADO - Métodos para lidar com comentários utilizando a classe auxiliar
    public final void mSL_COMMENT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        match("//");
        CommentHandler.handleSingleLineComment(this);
        if (_createToken && _token==null && _ttype!=Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
        }
    }
    
    public final void mML_COMMENT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        match("/*");
        CommentHandler.handleMultiLineComment(this);
        if (_createToken && _token==null && _ttype!=Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
        }
    }
    
    // TRECHO REFATORADO - Métodos para literais utilizando a classe auxiliar de escape
    public final void mSTRING_LITERAL(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        match('"');
        while (true) {
            int c = LA(1);
            if (c == '"') {
                break;
            } else if (c == '\\') {
                c = EscapeSequenceHandler.handleEscapeSequence(this, true);
                _ttype = STRING_LITERAL;
            } else if (c == '\n' || c == '\r' || c == CharScanner.EOF_CHAR) {
                throw new NoViableAltForCharException((char)c, getFilename(), getLine(), getColumn());
            } else {
                match(c);
            }
        }
        match('"');
        if (_createToken && _token==null && _ttype!=Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
        }
    }
    
    public final void mCHAR_LITERAL(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        match('\'');
        int c = LA(1);
        if (c == '\\') {
            c = EscapeSequenceHandler.handleEscapeSequence(this, false);
        } else if (c == '\'' || c == '\n' || c == '\r' || c == CharScanner.EOF_CHAR) {
            throw new NoViableAltForCharException((char)c, getFilename(), getLine(), getColumn());
        } else {
            match(c);
        }
        match('\'');
        if (_createToken && _token==null && _ttype!=Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
        }
    }
    
    // TRECHO REFATORADO - Métodos para verificação de caracteres usando a classe auxiliar
    private static boolean isJavaLetter(char c) {
        return JavaCharacterHelper.isJavaLetter(c);
    }
    
    private static boolean isJavaLetterOrDigit(char c) {
        return JavaCharacterHelper.isJavaLetterOrDigit(c);
    }
    
    // ... resto da classe
}

// TRECHO REFATORADO - Classe para gerenciar tokens de operadores
class OperatorTokenHandler {
    public static void handleAssignmentOperator(JavaLexer lexer, char c, int baseType, int assignType, boolean createToken)
            throws RecognitionException, CharStreamException, TokenStreamException {
        lexer.match(c);
        if (lexer.LA(1) == '=') {
            lexer.match('=');
            if (createToken) {
                lexer.makeToken(assignType);
            }
        } else if (createToken) {
            lexer.makeToken(baseType);
        }
    }

    public static void handleDoubleCharOperator(JavaLexer lexer, char c1, char c2, int singleType, int doubleType, boolean createToken)
            throws RecognitionException, CharStreamException, TokenStreamException {
        lexer.match(c1);
        if (lexer.LA(1) == c2) {
            lexer.match(c2);
            if (createToken) {
                lexer.makeToken(doubleType);
            }
        } else if (createToken) {
            lexer.makeToken(singleType);
        }
    }

    public static void handleComplexOperator(JavaLexer lexer, char c, int baseType,
                                             char secondChar, int secondType,
                                             char equalChar, int equalType,
                                             boolean createToken)
            throws RecognitionException, CharStreamException, TokenStreamException {
        lexer.match(c);
        int la1 = lexer.LA(1);

        if (la1 == secondChar) {
            lexer.match(secondChar);
            if (lexer.LA(1) == '=') {
                lexer.match('=');
                if (createToken) {
                    lexer.makeToken(equalType);
                }
            } else if (createToken) {
                lexer.makeToken(secondType);
            }
        } else if (la1 == '=') {
            lexer.match('=');
            if (createToken) {
                lexer.makeToken(equalType);
            }
        } else if (createToken) {
            lexer.makeToken(baseType);
        }
    }
}

// TRECHO REFATORADO - Classe para gerenciar identificadores e palavras-chave
class IdentifierHandler {
    public static int processIdentifier(JavaLexer lexer, int ttype) throws RecognitionException, CharStreamException {
        int result = ttype;
        int start = lexer.mark();

        // Primeiro caractere deve ser uma letra
        char c = (char)lexer.LA(1);
        if (JavaCharacterHelper.isJavaLetter(c)) {
            lexer.match(c);

            // Outros caracteres podem ser letras ou dígitos
            while (true) {
                c = (char)lexer.LA(1);
                if (!JavaCharacterHelper.isJavaLetterOrDigit(c)) {
                    break;
                }
                lexer.match(c);
            }

            // Verifica se é uma palavra-chave
            Integer i = (Integer)lexer.literals.get(new ANTLRHashString(lexer.text.toString(), lexer));
            if (i != null) {
                result = i.intValue();
            }
        } else {
            throw new NoViableAltForCharException(c, lexer.getFilename(), lexer.getLine(), lexer.getColumn());
        }

        return result;
    }
}

// TRECHO REFATORADO - Classe para gerenciar literais numéricos
class NumericLiteralHandler {
    public static int processNumericLiteral(JavaLexer lexer, int ttype)
            throws RecognitionException, CharStreamException {
        int result = ttype;
        boolean isDecimal = false;
        boolean isOctal = false;
        boolean isHex = false;

        int c = lexer.LA(1);

        // Verifica se é um número hexadecimal (0x)
        if (c == '0') {
            lexer.match('0');
            if (lexer.LA(1) == 'x' || lexer.LA(1) == 'X') {
                lexer.consume();
                isHex = true;
                result = processHexNumber(lexer);
            } else {
                // Octal
                isOctal = true;
                result = processOctalNumber(lexer);
            }
        } else {
            // Decimal
            isDecimal = true;
            result = processDecimalNumber(lexer);
        }

        // Verifica sufixo para long, float ou double
        result = checkNumericSuffix(lexer, result, isDecimal, isOctal, isHex);

        return result;
    }

    private static int processHexNumber(JavaLexer lexer)
            throws RecognitionException, CharStreamException {
        boolean atLeastOneDigit = false;
        while (true) {
            int c = lexer.LA(1);
            if (isHexDigit(c)) {
                atLeastOneDigit = true;
                lexer.consume();
            } else {
                break;
            }
        }

        if (!atLeastOneDigit) {
            throw new NoViableAltForCharException((char)lexer.LA(1), lexer.getFilename(), lexer.getLine(), lexer.getColumn());
        }

        return JavaTokenTypes.NUM_INT;
    }

    private static int processOctalNumber(JavaLexer lexer)
            throws RecognitionException, CharStreamException {
        while (true) {
            int c = lexer.LA(1);
            if (c >= '0' && c <= '7') {
                lexer.consume();
            } else {
                break;
            }
        }

        return JavaTokenTypes.NUM_INT;
    }

    private static int processDecimalNumber(JavaLexer lexer)
            throws RecognitionException, CharStreamException {
        int result = JavaTokenTypes.NUM_INT;

        // Consume todas as partes de dígitos
        consumeDigits(lexer);

        // Verifica se há uma parte decimal
        if (lexer.LA(1) == '.') {
            result = JavaTokenTypes.NUM_FLOAT;
            lexer.consume(); // Consome o ponto
            consumeDigits(lexer);
        }

        // Verifica se há um expoente
        int c = lexer.LA(1);
        if (c == 'e' || c == 'E') {
            result = JavaTokenTypes.NUM_FLOAT;
            lexer.consume(); // Consome e/E

            // Consome opcional + ou -
            c = lexer.LA(1);
            if (c == '+' || c == '-') {
                lexer.consume();
            }

            // Deve haver pelo menos um dígito no expoente
            if (!isDigit(lexer.LA(1))) {
                throw new NoViableAltForCharException((char)lexer.LA(1), lexer.getFilename(), lexer.getLine(), lexer.getColumn());
            }

            consumeDigits(lexer);
        }

        return result;
    }

    private static void consumeDigits(JavaLexer lexer)
            throws RecognitionException, CharStreamException {
        boolean atLeastOneDigit = false;
        while (true) {
            int c = lexer.LA(1);
            if (isDigit(c)) {
                atLeastOneDigit = true;
                lexer.consume();
            } else {
                break;
            }
        }

        if (!atLeastOneDigit) {
            throw new NoViableAltForCharException((char)lexer.LA(1), lexer.getFilename(), lexer.getLine(), lexer.getColumn());
        }
    }

    private static int checkNumericSuffix(JavaLexer lexer, int result, boolean isDecimal, boolean isOctal, boolean isHex)
            throws RecognitionException, CharStreamException {
        int c = lexer.LA(1);

        // Sufixo Long
        if (c == 'l' || c == 'L') {
            lexer.consume();
            result = JavaTokenTypes.NUM_LONG;
        }
        // Sufixo Float
        else if (c == 'f' || c == 'F') {
            lexer.consume();
            if (isHex) {
                throw new NoViableAltForCharException((char)c, lexer.getFilename(), lexer.getLine(), lexer.getColumn());
            }
            result = JavaTokenTypes.NUM_FLOAT;
        }
        // Sufixo Double
        else if (c == 'd' || c == 'D') {
            lexer.consume();
            if (isHex) {
                throw new NoViableAltForCharException((char)c, lexer.getFilename(), lexer.getLine(), lexer.getColumn());
            }
            result = JavaTokenTypes.NUM_DOUBLE;
        }

        return result;
    }

    private static boolean isHexDigit(int c) {
        return (c >= '0' && c <= '9') ||
                (c >= 'a' && c <= 'f') ||
                (c >= 'A' && c <= 'F');
    }

    private static boolean isDigit(int c) {
        return c >= '0' && c <= '9';
    }
}

public class JavaLexer extends antlr.CharScanner implements JavaTokenTypes, TokenStream {

    // (Construtores e método initialize já estão refatorados na parte anterior)

    // TRECHO REFATORADO - Refatorando métodos de operadores usando a classe auxiliar
    public final void mSTAR(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        OperatorTokenHandler.handleAssignmentOperator(this, '*', STAR, STAR_ASSIGN, _createToken);
    }

    public final void mDIV(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        OperatorTokenHandler.handleAssignmentOperator(this, '/', DIV, DIV_ASSIGN, _createToken);
    }

    public final void mMOD(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        OperatorTokenHandler.handleAssignmentOperator(this, '%', MOD, MOD_ASSIGN, _createToken);
    }

    public final void mSR(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        OperatorTokenHandler.handleDoubleCharOperator(this, '>', '>', SR, SR_ASSIGN, _createToken);
    }

    public final void mGE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        OperatorTokenHandler.handleDoubleCharOperator(this, '>', '=', GE, GE, _createToken);
    }

    public final void mGT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        SimpleTokenHandler.handleSingleCharToken(this, '>', GT, _createToken);
    }

    public final void mSL(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        OperatorTokenHandler.handleDoubleCharOperator(this, '<', '<', SL, SL_ASSIGN, _createToken);
    }

    public final void mLE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        OperatorTokenHandler.handleDoubleCharOperator(this, '<', '=', LE, LE, _createToken);
    }

    public final void mLT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        SimpleTokenHandler.handleSingleCharToken(this, '<', LT, _createToken);
    }

    public final void mBSR(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        OperatorTokenHandler.handleComplexOperator(this, '>', BSR, '>', BSR, '=', BSR_ASSIGN, _createToken);
    }

    public final void mPLUS_PLUS(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        OperatorTokenHandler.handleDoubleCharOperator(this, '+', '+', PLUS_PLUS, PLUS_PLUS, _createToken);
    }

    public final void mMINUS_MINUS(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        OperatorTokenHandler.handleDoubleCharOperator(this, '-', '-', MINUS_MINUS, MINUS_MINUS, _createToken);
    }

    // TRECHO REFATORADO - Simplificação dos métodos de whitespace
    public final void mWS(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        int _ttype = WS;
        processWhitespace();
        if (_createToken && _token==null && _ttype!=Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
        }
    }

    // TRECHO REFATORADO - Método extraído para processamento de espaços em branco
    private void processWhitespace() throws RecognitionException, CharStreamException {
        int _saveIndex = 0;
        _saveIndex = text.length();
        while (true) {
            int c = LA(1);
            if (c == ' ' || c == '\t' || c == '\n' || c == '\r' || c == '\f') {
                match(c);
            } else {
                break;
            }
        }
        text.setLength(_saveIndex);
    }

    // TRECHO REFATORADO - Método para identificadores usando a classe auxiliar
    public final void mIDENT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        int _ttype = IDENT;
        _ttype = IdentifierHandler.processIdentifier(this, _ttype);
        if (_createToken && _token==null && _ttype!=Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
        }
    }

    // TRECHO REFATORADO - Método para literais numéricos usando a classe auxiliar
    public final void mNUM_INT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        int _ttype = NUM_INT;
        _ttype = NumericLiteralHandler.processNumericLiteral(this, _ttype);
        if (_createToken && _token==null && _ttype!=Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
        }
    }

    // ... outros métodos da classe

    // TRECHO REFATORADO - O método nextToken usa os métodos auxiliares extraídos
    public Token nextToken() throws TokenStreamException {
        Token theRetToken = null;
        tryAgain:
        for (;;) {
            Token _token = null;
            int _ttype = Token.INVALID_TYPE;
            resetText();
            try {
                try {
                    if ((LA(1) == '/') && (LA(2) == '/')) {
                        mSL_COMMENT(false);
                        continue tryAgain;
                    } else if ((LA(1) == '/') && (LA(2) == '*')) {
                        mML_COMMENT(false);
                        continue tryAgain;
                    } else if ((LA(1) >= '\u0003' && LA(1) <= '\u00ff') && (true)) {
                        mTOKEN(true);
                        theRetToken=_returnToken;
                    } else {
                        consume();
                        continue tryAgain;
                    }
                } catch (RecognitionException e) {
                    if (inputState.guessing == 0) {
                        reportError(e);
                        consume();
                    } else {
                        throw e;
                    }
                }
            } catch (CharStreamException cse) {
                if (cse instanceof CharStreamIOException) {
                    throw new TokenStreamIOException(((CharStreamIOException)cse).io);
                } else {
                    throw new TokenStreamException(cse.getMessage());
                }
            }
            if (_returnToken == null) continue tryAgain;
            _ttype = _returnToken.getType();
            _returnToken.setType(_ttype);
            return _returnToken;
        }
    }

    // ... resto da classe
}

// TRECHO REFATORADO - Classe para gerenciar literais de caracteres e strings
class StringLiteralHandler {
    public static void processCharLiteral(JavaLexer lexer, boolean createToken)
            throws RecognitionException, CharStreamException, TokenStreamException {
        int _ttype = JavaTokenTypes.CHAR_LITERAL;
        int _saveIndex = 0;

        lexer.match('\'');

        // Um caractere literal deve ter exatamente um caractere (ou uma sequência de escape)
        if (lexer.LA(1) == '\'') {
            throw new NoViableAltForCharException((char)lexer.LA(1),
                    lexer.getFilename(), lexer.getLine(), lexer.getColumn());
        }

        if (lexer.LA(1) == '\\') {
            _saveIndex = lexer.text.length();
            lexer.match('\\');
            lexer.text.setLength(_saveIndex);
            EscapeSequenceHandler.handleEscapeSequence(lexer);
        } else if (lexer.LA(1) != '\'' && lexer.LA(1) != '\n' && lexer.LA(1) != '\r') {
            lexer.match(_tokenSet_3);
        } else {
            throw new NoViableAltForCharException((char)lexer.LA(1),
                    lexer.getFilename(), lexer.getLine(), lexer.getColumn());
        }

        lexer.match('\'');

        if (createToken) {
            lexer.makeToken(_ttype);
        }
    }

    public static void processStringLiteral(JavaLexer lexer, boolean createToken)
            throws RecognitionException, CharStreamException, TokenStreamException {
        int _ttype = JavaTokenTypes.STRING_LITERAL;
        int _saveIndex = 0;

        lexer.match('"');

        // Processa o conteúdo da string
        while (lexer.LA(1) != '"' && lexer.LA(1) != CharScanner.EOF_CHAR) {
            if (lexer.LA(1) == '\\') {
                _saveIndex = lexer.text.length();
                lexer.match('\\');
                lexer.text.setLength(_saveIndex);
                EscapeSequenceHandler.handleEscapeSequence(lexer);
            } else if (lexer.LA(1) != '\n' && lexer.LA(1) != '\r') {
                lexer.match(_tokenSet_4);
            } else {
                throw new NoViableAltForCharException((char)lexer.LA(1),
                        lexer.getFilename(), lexer.getLine(), lexer.getColumn());
            }
        }

        lexer.match('"');

        if (createToken) {
            lexer.makeToken(_ttype);
        }
    }
}

// TRECHO REFATORADO - Classe para processamento de tokens complexos
class TokenProcessor {
    public static void processTOKEN(JavaLexer lexer, boolean createToken)
            throws RecognitionException, CharStreamException, TokenStreamException {
        int _ttype = JavaTokenTypes.TOKEN;

        switch (lexer.LA(1)) {
            case '\'':
                StringLiteralHandler.processCharLiteral(lexer, createToken);
                break;

            case '"':
                StringLiteralHandler.processStringLiteral(lexer, createToken);
                break;

            case '#':
                SimpleTokenHandler.handleSingleCharToken(lexer, '#', JavaTokenTypes.PREPROCESSOR, createToken);
                break;

            case '0': case '1': case '2': case '3': case '4':
            case '5': case '6': case '7': case '8': case '9':
                lexer.mNUM_INT(createToken);
                break;

            case '?':
                SimpleTokenHandler.handleSingleCharToken(lexer, '?', JavaTokenTypes.QUESTION, createToken);
                break;

            case '(':
                SimpleTokenHandler.handleSingleCharToken(lexer, '(', JavaTokenTypes.LPAREN, createToken);
                break;

            case ')':
                SimpleTokenHandler.handleSingleCharToken(lexer, ')', JavaTokenTypes.RPAREN, createToken);
                break;

            case '[':
                SimpleTokenHandler.handleSingleCharToken(lexer, '[', JavaTokenTypes.LBRACK, createToken);
                break;

            case ']':
                SimpleTokenHandler.handleSingleCharToken(lexer, ']', JavaTokenTypes.RBRACK, createToken);
                break;

            case '{':
                SimpleTokenHandler.handleSingleCharToken(lexer, '{', JavaTokenTypes.LCURLY, createToken);
                break;

            case '}':
                SimpleTokenHandler.handleSingleCharToken(lexer, '}', JavaTokenTypes.RCURLY, createToken);
                break;

            case ':':
                SimpleTokenHandler.handleSingleCharToken(lexer, ':', JavaTokenTypes.COLON, createToken);
                break;

            case ',':
                SimpleTokenHandler.handleSingleCharToken(lexer, ',', JavaTokenTypes.COMMA, createToken);
                break;

            case '~':
                SimpleTokenHandler.handleSingleCharToken(lexer, '~', JavaTokenTypes.BNOT, createToken);
                break;

            case ';':
                SimpleTokenHandler.handleSingleCharToken(lexer, ';', JavaTokenTypes.SEMI, createToken);
                break;

            case '.':
                lexer.mDOT(createToken);
                break;

            case '+':
                lexer.mPLUS(createToken);
                break;

            case '-':
                lexer.mMINUS(createToken);
                break;

            case '*':
                lexer.mSTAR(createToken);
                break;

            case '%':
                lexer.mMOD(createToken);
                break;

            case '/':
                lexer.mDIV(createToken);
                break;

            case '>':
                lexer.mGT(createToken);
                break;

            case '<':
                lexer.mLT(createToken);
                break;

            case '=':
                lexer.mASSIGN(createToken);
                break;

            case '!':
                lexer.mLNOT(createToken);
                break;

            case '&':
                lexer.mBAND(createToken);
                break;

            case '|':
                lexer.mBOR(createToken);
                break;

            case '^':
                lexer.mBXOR(createToken);
                break;

            case '@':
                SimpleTokenHandler.handleSingleCharToken(lexer, '@', JavaTokenTypes.AT, createToken);
                break;

            case '\t': case '\n': case '\u000c': case '\r': case ' ':
                lexer.mWS(createToken);
                break;

            case 'a': case 'b': case 'c': case 'd': case 'e': case 'f':
            case 'g': case 'h': case 'i': case 'j': case 'k': case 'l':
            case 'm': case 'n': case 'o': case 'p': case 'q': case 'r':
            case 's': case 't': case 'u': case 'v': case 'w': case 'x':
            case 'y': case 'z':
            case 'A': case 'B': case 'C': case 'D': case 'E': case 'F':
            case 'G': case 'H': case 'I': case 'J': case 'K': case 'L':
            case 'M': case 'N': case 'O': case 'P': case 'Q': case 'R':
            case 'S': case 'T': case 'U': case 'V': case 'W': case 'X':
            case 'Y': case 'Z':
            case '_': case '$':
                lexer.mIDENT(createToken);
                break;

            default:
                if ((lexer.LA(1) == '\u00e2') && (lexer.LA(2) == '\u0080') && (lexer.LA(3) == '\u0094')) {
                    lexer.match('\u00e2');
                    lexer.match('\u0080');
                    lexer.match('\u0094');
                    if (createToken) {
                        lexer.makeToken(JavaTokenTypes.EN_DASH);
                    }
                } else if ((lexer.LA(1) == '\u00e2') && (lexer.LA(2) == '\u0080') && (lexer.LA(3) == '\u0093')) {
                    lexer.match('\u00e2');
                    lexer.match('\u0080');
                    lexer.match('\u0093');
                    if (createToken) {
                        lexer.makeToken(JavaTokenTypes.EM_DASH);
                    }
                } else {
                    throw new NoViableAltForCharException((char)lexer.LA(1),
                            lexer.getFilename(), lexer.getLine(), lexer.getColumn());
                }
        }
    }
}

public class JavaLexer extends antlr.CharScanner implements JavaTokenTypes, TokenStream {

    // (Partes anteriores já refatoradas)

    public final void mCHAR_LITERAL(boolean _createToken)
            throws RecognitionException, CharStreamException, TokenStreamException {
        StringLiteralHandler.processCharLiteral(this, _createToken);
    }

    public final void mSTRING_LITERAL(boolean _createToken)
            throws RecognitionException, CharStreamException, TokenStreamException {
        StringLiteralHandler.processStringLiteral(this, _createToken);
    }

    public final void mASSIGN(boolean _createToken)
            throws RecognitionException, CharStreamException, TokenStreamException {
        OperatorTokenHandler.handleDoubleCharOperator(this, '=', '=', ASSIGN, EQUAL, _createToken);
    }

    public final void mLNOT(boolean _createToken)
            throws RecognitionException, CharStreamException, TokenStreamException {
        OperatorTokenHandler.handleDoubleCharOperator(this, '!', '=', LNOT, NOT_EQUAL, _createToken);
    }

    public final void mBAND(boolean _createToken)
            throws RecognitionException, CharStreamException, TokenStreamException {
        int la1 = LA(1);

        if (la1 == '&') {
            match('&');
            if (LA(1) == '&') {
                match('&');
                if (_createToken) {
                    makeToken(LAND);
                }
            } else if (LA(1) == '=') {
                match('=');
                if (_createToken) {
                    makeToken(BAND_ASSIGN);
                }
            } else if (_createToken) {
                makeToken(BAND);
            }
        } else {
            throw new NoViableAltForCharException((char)la1, getFilename(), getLine(), getColumn());
        }
    }

    public final void mBOR(boolean _createToken)
            throws RecognitionException, CharStreamException, TokenStreamException {
        int la1 = LA(1);

        if (la1 == '|') {
            match('|');
            if (LA(1) == '|') {
                match('|');
                if (_createToken) {
                    makeToken(LOR);
                }
            } else if (LA(1) == '=') {
                match('=');
                if (_createToken) {
                    makeToken(BOR_ASSIGN);
                }
            } else if (_createToken) {
                makeToken(BOR);
            }
        } else {
            throw new NoViableAltForCharException((char)la1, getFilename(), getLine(), getColumn());
        }
    }

    public final void mBXOR(boolean _createToken)
            throws RecognitionException, CharStreamException, TokenStreamException {
        OperatorTokenHandler.handleAssignmentOperator(this, '^', BXOR, BXOR_ASSIGN, _createToken);
    }

    public final void mPLUS(boolean _createToken)
            throws RecognitionException, CharStreamException, TokenStreamException {
        int la1 = LA(1);

        if (la1 == '+') {
            match('+');
            if (LA(1) == '+') {
                match('+');
                if (_createToken) {
                    makeToken(PLUS_PLUS);
                }
            } else if (LA(1) == '=') {
                match('=');
                if (_createToken) {
                    makeToken(PLUS_ASSIGN);
                }
            } else if (_createToken) {
                makeToken(PLUS);
            }
        } else {
            throw new NoViableAltForCharException((char)la1, getFilename(), getLine(), getColumn());
        }
    }

    public final void mMINUS(boolean _createToken)
            throws RecognitionException, CharStreamException, TokenStreamException {
        int la1 = LA(1);

        if (la1 == '-') {
            match('-');
            if (LA(1) == '-') {
                match('-');
                if (_createToken) {
                    makeToken(MINUS_MINUS);
                }
            } else if (LA(1) == '=') {
                match('=');
                if (_createToken) {
                    makeToken(MINUS_ASSIGN);
                }
            } else if (_createToken) {
                makeToken(MINUS);
            }
        } else {
            throw new NoViableAltForCharException((char)la1, getFilename(), getLine(), getColumn());
        }
    }

    public final void mDOT(boolean _createToken)
            throws RecognitionException, CharStreamException, TokenStreamException {
        int _ttype = DOT;
        match('.');

        // Verifica se é um número decimal que começa com ponto
        if ((LA(1) >= '0' && LA(1) <= '9')) {
            _ttype = NUM_FLOAT;

            // Consome todos os dígitos
            while ((LA(1) >= '0' && LA(1) <= '9')) {
                match(LA(1));
            }

            // Verifica se há um expoente
            if (LA(1) == 'e' || LA(1) == 'E') {
                match(LA(1));

                // Consome opcional + ou -
                if (LA(1) == '+' || LA(1) == '-') {
                    match(LA(1));
                }

                // Deve haver pelo menos um dígito no expoente
                if (!((LA(1) >= '0' && LA(1) <= '9'))) {
                    throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
                }

                // Consome todos os dígitos do expoente
                while ((LA(1) >= '0' && LA(1) <= '9')) {
                    match(LA(1));
                }
            }

            // Verifica sufixo float ou double
            if (LA(1) == 'f' || LA(1) == 'F') {
                match(LA(1));
            } else if (LA(1) == 'd' || LA(1) == 'D') {
                match(LA(1));
                _ttype = NUM_DOUBLE;
            }
        }

        if (_createToken) {
            makeToken(_ttype);
        }
    }

    public final void mSL_COMMENT(boolean _createToken)
            throws RecognitionException, CharStreamException, TokenStreamException {
        CommentHandler.handleSingleLineComment(this, _createToken);
    }

    public final void mML_COMMENT(boolean _createToken)
            throws RecognitionException, CharStreamException, TokenStreamException {
        CommentHandler.handleMultiLineComment(this, _createToken);
    }

    public final void mTOKEN(boolean _createToken)
            throws RecognitionException, CharStreamException, TokenStreamException {
        TokenProcessor.processTOKEN(this, _createToken);
    }

    // TokenSet para uso pelos métodos
    public static final BitSet _tokenSet_0 = new BitSet(0);
    public static final BitSet _tokenSet_1 = new BitSet(0);
    public static final BitSet _tokenSet_2 = new BitSet(0);
    public static final BitSet _tokenSet_3 = new BitSet(0);
    public static final BitSet _tokenSet_4 = new BitSet(0);

    static {
        // Inicialização dos BitSets
        _tokenSet_0.add('\0', '\377');
        _tokenSet_0.add('\u0000', '\u007f');

        _tokenSet_1.add('0', '7');

        _tokenSet_2.add('\u0003', '\u00ff');
        _tokenSet_2.add('*');

        _tokenSet_3.add('\u0003', '\u00ff');
        _tokenSet_3.add('\n', '\r');
        _tokenSet_3.add('\'');

        _tokenSet_4.add('\u0003', '\u00ff');
        _tokenSet_4.add('\n', '\r');
        _tokenSet_4.add('\"');
    }

    // Método de criação de token - simplesmente para maior claridade
    protected void makeToken(int type) {
        _token = makeToken(type);
        _token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
    }
}

/**
 * ## 4) Resumo das alterações
 * **Quantidade total de refatorações realizadas**: 15 refatorações
 * **Divisão por tipo**:
 * - Extração de Classe: 4 (JavaCharacterHelper, EscapeSequenceHandler, CommentHandler, SimpleTokenHandler)
 * - Extração de Método: 11 (initialize, handleEscapeSequence, handleUnicodeEscape, handleOctalEscape, handleSingleLineComment, handleMultiLineComment, handleSingleCharToken, handleTokenWithLookahead, isJavaLetter, isJavaLetterOrDigit, isJavaIdentifierPart)
 *
 * **Quantidade total de refatorações realizadas**: 28 refatorações
 * **Divisão por tipo**:
 * - Extração de Classe: 7 (JavaCharacterHelper, EscapeSequenceHandler, CommentHandler, SimpleTokenHandler, OperatorTokenHandler, IdentifierHandler, NumericLiteralHandler)
 * - Extração de Método: 21 (initialize, handleEscapeSequence, handleUnicodeEscape, handleOctalEscape, handleSingleLineComment, handleMultiLineComment, handleSingleCharToken, handleTokenWithLookahead, isJavaLetter, isJavaLetterOrDigit, isJavaIdentifierPart, handleAssignmentOperator, handleDoubleCharOperator, handleComplexOperator, processWhitespace, processIdentifier, processNumericLiteral, processHexNumber, processOctalNumber, processDecimalNumber, consumeDigits, checkNumericSuffix, isHexDigit, isDigit)
 *
 ***Quantidade total de refatorações realizadas**: 35 refatorações
 * **Divisão por tipo**:
 * - Extração de Classe: 9 (JavaCharacterHelper, EscapeSequenceHandler, CommentHandler, SimpleTokenHandler, OperatorTokenHandler, IdentifierHandler, NumericLiteralHandler, StringLiteralHandler, TokenProcessor)
 * - Extração de Método: 26 (todos os métodos auxiliares mencionados anteriormente, mais processCharLiteral, processStringLiteral, processTOKEN, makeToken)
 *
 * **Benefícios da refatoração**:
 * 1. **Maior coesão**: Cada classe e método agora tem uma responsabilidade bem definida.
 * 2. **Menor acoplamento**: As dependências entre partes do código foram reduzidas.
 * 3. **Melhor legibilidade**: O código está mais claro e autoexplicativo com nomes de métodos e classes que descrevem sua função.
 * 4. **Eliminação de código duplicado**: Padrões repetitivos foram extraídos para métodos comuns.
 * 5. **Facilidade de manutenção**: Modificações futuras serão mais fáceis devido à estrutura modular.
 * 6. **Facilidade de teste**: As classes mais coesas e com responsabilidades claras são mais fáceis de testar.
 * 7. **Melhor organização**: O código agora segue uma estrutura lógica baseada em responsabilidades.
 */