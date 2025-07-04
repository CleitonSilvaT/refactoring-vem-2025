package jparse.stmt;

import antlr.Token;
import antlr.collections.AST;
import java.util.ArrayList;
import jparse.ModifierAST;
import jparse.Type;
import jparse.VarList;
import jparse.expr.TypeAST;
import jparse.expr.VarAST;

/**
 * Um nó AST que representa uma cláusula catch
 */
public final class CatchAST extends StatementAST {

    private VarAST param;
    private CompoundAST body;

    public CatchAST(final Token token) {
        super(token);
    }

    public void parseComplete() {
        super.parseComplete();
        // TRECHO REFATORADO: Extraído método para processar o parâmetro da cláusula catch
        processParameterNode();
        // TRECHO REFATORADO: Extraído método para processar o corpo da cláusula catch
        processBodyNode();
    }

    // TRECHO REFATORADO: Novo método extraído para processar o parâmetro catch
    private void processParameterNode() {
        final AST theParam = getFirstChild().getNextSibling();
        context.mods = (ModifierAST)theParam.getFirstChild();
        context.type = (TypeAST)context.mods.getNextSibling();
        param = (VarAST)context.type.getNextSibling();
        param.parseComplete();
    }

    // TRECHO REFATORADO: Novo método extraído para processar o corpo do catch
    private void processBodyNode() {
        final AST theParam = getFirstChild().getNextSibling();
        body = (CompoundAST)theParam.getNextSibling().getNextSibling();
        body.parseComplete();
    }

    protected Type[] computeExceptions() {
        return body.getExceptionTypes();
    }

    Type[] removeCaughtException(final Type[] list) {
        final Type theCatch = param.retrieveType();
        // TRECHO REFATORADO: Extraída a lógica de filtragem para um método específico
        return filterExceptions(list, theCatch);
    }

    // TRECHO REFATORADO: Novo método para encapsular a lógica de filtragem de exceções
    private Type[] filterExceptions(final Type[] exceptions, final Type catchType) {
        final ArrayList newList = new ArrayList();
        for (int i = 0; i < exceptions.length; i++) {
            if (!catchType.isAssignableFrom(exceptions[i])) {
                newList.add(exceptions[i]);
            }
        }
        final Type[] retList = new Type[newList.size()];
        newList.toArray(retList);
        return retList;
    }

    protected StatementAST[] computeControl() {
        return body.nextControlPoints();
    }

    public VarList getVarList() {
        return new VarList(body.getVarList(), param);
    }

    public VarAST getParameter() {
        return param;
    }

    public CompoundAST getBody() {
        return body;
    }
}

/**
 *
 * # 1) Oportunidades de refatoração encontradas
 * Após analisar o código da classe `CatchAST`, foram identificadas as seguintes oportunidades de refatoração:
 * 1. **Método parseComplete()** - O método é grande e realiza múltiplas operações. Pode ser dividido em métodos menores com responsabilidades específicas.
 * 2. **Método removeCaughtException()** - Este método contém lógica de filtragem de exceções que pode ser extraída para melhorar a legibilidade e permitir reutilização.
 * 3. **Coesão de responsabilidades** - A classe está bem definida, mas algumas operações internas poderiam ser extraídas para melhorar a organização do código.
 *
 * # 2) Classe refatorada com os devidos comentários
 *
 * # 3) Justificativa das refatorações
 * 1. **Extração do método processParameterNode()**:
 *     - Esta refatoração foi aplicada seguindo o princípio de "Extração de Método" conforme descrito por Martin Fowler. O método original `parseComplete()` realizava múltiplas responsabilidades. Extrair o processamento do parâmetro para um método separado torna o código mais legível e facilita a manutenção.
 *     - Cada método agora tem uma única responsabilidade, seguindo o princípio SRP (Single Responsibility Principle).
 *
 * 2. **Extração do método processBodyNode()**:
 *     - Similar à justificativa anterior, esta extração torna o código mais modular e mais fácil de entender, já que cada método agora realiza uma única tarefa específica.
 *     - A extração reduz o tamanho do método original, aumentando a clareza do código.
 *
 * 3. **Extração do método filterExceptions()**:
 *     - O método `removeCaughtException()` continha uma lógica específica para filtragem de exceções que podia ser isolada. Esta refatoração permite reutilização dessa lógica se necessário em outras partes do código.
 *     - A extração deste método também melhora a legibilidade, tornando mais claro o propósito de cada parte do código.
 *     - Conforme mencionado por Marco Tulio na Engenharia de Software Moderna, métodos curtos e com propósitos bem definidos são mais fáceis de testar e manter.
 *
 * # 4) Resumo das alterações
 * - **Quantidade total de refatorações realizadas**: 3
 * - **Divisão por tipo**:
 *     - Extração de Método: 3 (processParameterNode, processBodyNode, filterExceptions)
 *     - Extração de Classe: 0
 *
 * As refatorações realizadas mantiveram o comportamento original do código, mas melhoraram significativamente sua estrutura através da aplicação do princípio de "Extração de Método". Cada método extraído agora tem uma responsabilidade bem definida, o que facilita futuras manutenções e torna o código mais fácil de entender. O código mantém a mesma funcionalidade, mas com uma organização interna mais clara e coesa.
 */