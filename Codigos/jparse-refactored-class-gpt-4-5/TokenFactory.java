// TRECHO REFATORADO: Classe auxiliar extraída (da classe JavaLExer) para criar tokens de maneira padronizada
public class TokenFactory {

    public static Token criarToken(JavaLexer lexer, int ttype, int begin) {
        Token token = lexer.makeToken(ttype);
        token.setText(new String(lexer.text.getBuffer(), begin, lexer.text.length() - begin));
        return token;
    }
}