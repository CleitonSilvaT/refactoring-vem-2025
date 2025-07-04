package jparse;

import antlr.CommonASTWithHiddenTokens;
import antlr.Token;
import java.io.OutputStreamWriter;
import java.io.IOException;

public class JavaAST extends CommonASTWithHiddenTokens {

    protected static final Type[] noTypes = new Type[0];
    protected static SymbolTable currSymTable;
    protected static CompileContext context;

    public final SymbolTable symTable;
    public final FileAST topLevel;
    public final TypeAST typeAST;

    private final TokenPrinter tokenPrinter; // TRECHO REFATORADO - Extração de Classe

    public JavaAST() {
        super();
        symTable = currSymTable;
        topLevel = FileAST.currFile;
        typeAST = TypeAST.currType;
        tokenPrinter = new TokenPrinter(this);
    }

    public JavaAST(final SymbolTable table) {
        super();
        symTable = currSymTable = table;
        topLevel = FileAST.currFile;
        typeAST = TypeAST.currType;
        tokenPrinter = new TokenPrinter(this);
    }

    public JavaAST(final Token token) {
        super(token);
        symTable = currSymTable;
        topLevel = FileAST.currFile;
        typeAST = TypeAST.currType;
        tokenPrinter = new TokenPrinter(this);
    }

    public JavaAST(final Token token, final SymbolTable table) {
        super(token);
        symTable = currSymTable = table;
        topLevel = FileAST.currFile;
        typeAST = TypeAST.currType;
        tokenPrinter = new TokenPrinter(this);
    }

    public void print(final OutputStreamWriter output) {
        tokenPrinter.printNodeWithHiddenTokens(output); // TRECHO REFATORADO - Extração de Método
    }

    public void printHiddenAfter(final OutputStreamWriter output) {
        tokenPrinter.printHiddenAfter(output); // TRECHO REFATORADO - Extração de Método
    }

    public void printHiddenBefore(final OutputStreamWriter output) {
        tokenPrinter.printHiddenBefore(output); // TRECHO REFATORADO - Extração de Método
    }

    public void parseComplete() {
        for (JavaAST a = (JavaAST)getFirstChild(); a != null; a = (JavaAST)a.getNextSibling()) {
            a.parseComplete();
        }
    }
}

/**
 * ### 1) Oportunidades de refatoração encontradas
 * A classe `JavaAST` apresenta as seguintes oportunidades claras para refatoração:
 * - Código duplicado nos métodos: `print`, `printHiddenAfter` e `printHiddenBefore`. Todos compartilham lógica semelhante relacionada à impressão de tokens ocultos.
 * - Mistura de responsabilidade: gerenciamento da construção de nós da AST e a lógica de impressão estão presentes numa única classe.
 *
 * ### 2) Classe refatorada com os devidos comentários
 *
 * ### 3) Justificativa das refatorações
 * - **Extração de Classe (`TokenPrinter`)**: Conforme explicado por Martin Fowler (2018), quando identificamos grupos de métodos que têm uma lógica fortemente relacionada e claramente distinta da responsabilidade principal da classe original, temos um candidato ideal para realizar "Extract Class". Neste caso, a lógica de impressão de tokens ocultos foi deslocada para uma nova classe `TokenPrinter`, mantendo a classe `JavaAST` focada apenas nas suas responsabilidades primárias (gestão da estrutura da AST).
 * - **Extração de Métodos (`printNodeWithHiddenTokens`, `printHiddenAfter`, `printHiddenBefore`)**: Esta refatoração elimina duplicações evidentes e melhora a clareza do código. De acordo com Marco Tulio, transformar blocos repetidos de código em métodos dedicados simplifica o entendimento e manutenção, reduzindo a complexidade cognitiva envolvida ao lidar com funções maiores.
 *
 * Desta forma, separando responsabilidades e extraindo funcionalidades repetidas, reduzimos o acoplamento das funcionalidades dentro da classe `JavaAST`.
 * ### 4) Resumo das alterações:
 * - **Total de refatorações realizadas**: 4
 * - **Extração de Método**: 3 (um método para cada método de impressão)
 * - **Extração de Classe**: 1 (Classe `TokenPrinter`)
 */