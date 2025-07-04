package jparse.expr;

import antlr.Token;
import jparse.JavaAST;
import jparse.JavaTokenTypes;
import jparse.Type;
import jparse.VarList;

public class IdentifierAST extends ExpressionAST implements JavaTokenTypes {

    // TRECHO REFATORADO: Extração de Classe - criação de uma classe para gerenciar a identidade
    private IdentifierIdentity identity;

    public IdentifierAST() {
        super();
        // TRECHO REFATORADO: Inicialização da classe extraída
        identity = new IdentifierIdentity();
    }

    public IdentifierAST(final Token token) {
        super(token);
        // TRECHO REFATORADO: Inicialização da classe extraída com o texto do token
        identity = new IdentifierIdentity(getText());
    }

    public final void setName(String theName) {
        // TRECHO REFATORADO: Delegação para a classe extraída
        identity.setName(theName);
    }

    public final String getName() {
        // TRECHO REFATORADO: Delegação para a classe extraída
        return identity.getName();
    }

    public final void setMethod() {
        // TRECHO REFATORADO: Método extraído com semântica mais clara
        markAsMethodIdentifier();
    }

    // TRECHO REFATORADO: Extração de Método - para tornar mais claro o que está sendo feito
    private void markAsMethodIdentifier() {
        identity.setIsMethod(true);
    }

    public int compareTo(final IdentifierAST ident) {
        // TRECHO REFATORADO: Extração de Método - delegação da comparação
        return compareIdentifiers(ident);
    }

    // TRECHO REFATORADO: Extração de Método - lógica de comparação entre identificadores
    private int compareIdentifiers(final IdentifierAST ident) {
        return identity.getName().compareTo(ident.identity.getName());
    }

    // TRECHO REFATORADO: Adaptação do método para trabalhar com a nova estrutura
    protected Type computeType() {
        // TRECHO REFATORADO: Delegação para métodos específicos baseados na natureza do identificador
        if (identity.isMethod()) {
            return resolveMethodReturnType();
        } else {
            return resolveVariableType();
        }
    }

    // TRECHO REFATORADO: Extração de Método - separando a resolução de tipos por categoria
    private Type resolveMethodReturnType() {
        JavaAST parent = getFirstParent();
        if (parent != null) {
            return parent.getEnvironment().lookupMethodReturnType(identity.getName());
        }
        return Type.UNKNOWN_TYPE;
    }

    // TRECHO REFATORADO: Extração de Método - separando a resolução de tipos por categoria
    private Type resolveVariableType() {
        JavaAST parent = getFirstParent();
        if (parent != null) {
            return parent.getEnvironment().lookupVariableType(identity.getName());
        }
        return Type.UNKNOWN_TYPE;
    }

    protected Type[] computeExceptions() {
        return noTypes;
    }

    // TRECHO REFATORADO: Adaptação do método para trabalhar com a nova estrutura
    protected Object computeValue() {
        // TRECHO REFATORADO: Extração de Método - delegação para método especializado
        return evaluateIdentifierValue();
    }

    // TRECHO REFATORADO: Extração de Método - avaliação do valor de um identificador
    private Object evaluateIdentifierValue() {
        if (identity.isMethod()) {
            return null;  // Métodos não têm valor avaliável diretamente
        }
        
        JavaAST parent = getFirstParent();
        if (parent != null) {
            return parent.getEnvironment().lookupVariableValue(identity.getName());
        }
        return null;
    }

    // TRECHO REFATORADO: Adaptação do método para trabalhar com a nova estrutura
    public VarList getVarList() {
        // TRECHO REFATORADO: Extração de Método - delegação para método especializado
        return collectAssociatedVariables();
    }
    
    // TRECHO REFATORADO: Extração de Método - coleta de variáveis associadas ao identificador
    private VarList collectAssociatedVariables() {
        JavaAST parent = getFirstParent();
        if (parent != null) {
            if (identity.isMethod()) {
                return parent.getEnvironment().lookupMethodVariables(identity.getName());
            } else {
                return parent.getEnvironment().lookupAssociatedVariables(identity.getName());
            }
        }
        return VarList.EMPTY_LIST;
    }

    // TRECHO REFATORADO: Extração de Método - obtenção do primeiro pai na árvore AST
    private JavaAST getFirstParent() {
        if (getParent() instanceof JavaAST) {
            return (JavaAST) getParent();
        }
        return null;
    }

    public String toString() {
        // TRECHO REFATORADO: Delegação para a classe extraída
        return identity.getName();
    }
    
    // TRECHO REFATORADO: Classe extraída para encapsular o conceito de identidade de um identificador
    private static class IdentifierIdentity {
        private String name;
        private boolean isMethod = false;
        
        public IdentifierIdentity() {
            this.name = "";
        }
        
        public IdentifierIdentity(String name) {
            this.name = name;
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public boolean isMethod() {
            return isMethod;
        }
        
        public void setIsMethod(boolean isMethod) {
            this.isMethod = isMethod;
        }
    }
}


/**
 * # Refatoração da classe IdentifierAST
 * ## 1) Oportunidades de refatoração encontradas
 * Após analisar a classe `IdentifierAST`, identifiquei as seguintes oportunidades de refatoração:
 * 1. Os atributos `name` e `isMethod` estão muito simplificados e podem ser encapsulados em uma classe separada.
 * 2. Os métodos associados ao gerenciamento do nome e comportamento do identificador podem ser reorganizados.
 * 3. Os métodos de computação de tipo, valor e variáveis podem ser refatorados para melhorar a clareza.
 *
 * ## 2) Classe refatorada com os devidos comentários
 *
 * ## 3) Justificativa das refatorações
 * ### Extração de Classe
 * A criação da classe `IdentifierIdentity` segue o princípio de Responsabilidade Única (SRP) de Martin Fowler. A classe original `IdentifierAST` estava gerenciando dois conceitos: a representação de um nó AST e a identidade do identificador. Ao extrair a identidade para uma classe separada, tornamos o código mais coeso e mais fácil de manter.
 * ### Extração de Método
 * Vários métodos foram extraídos para tornar o código mais claro e modular:
 * 1. `markAsMethodIdentifier()` - Torna mais claro o propósito da operação `setMethod()`.
 * 2. `compareIdentifiers()` - Encapsula a lógica de comparação entre identificadores.
 * 3. `resolveMethodReturnType()` e `resolveVariableType()` - Separam a lógica de resolução de tipos por categoria.
 * 4. `evaluateIdentifierValue()` - Modulariza o processo de avaliação de valores.
 * 5. `collectAssociatedVariables()` - Encapsula a lógica de coleta de variáveis associadas.
 * 6. `getFirstParent()` - Extraído para reutilização em vários métodos.
 *
 * Essas extrações seguem a recomendação de Fowler sobre métodos com nomes que expressem claramente sua intenção e que realizem uma única função bem definida, tornando o código mais legível e fácil de manter.
 * ## 4) Resumo das alterações
 * - **Quantidade total de refatorações realizadas**: 12
 * - **Divisão por tipo**:
 *     - **Extração de Classe**: 1 (IdentifierIdentity)
 *     - **Extração de Método**: 11
 *         - markAsMethodIdentifier
 *         - compareIdentifiers
 *         - resolveMethodReturnType
 *         - resolveVariableType
 *         - evaluateIdentifierValue
 *         - collectAssociatedVariables
 *         - getFirstParent
 *         - métodos delegados na classe extraída: getName, setName, isMethod, setIsMethod
 *
 * As refatorações realizadas melhoraram significativamente a organização do código, aplicando princípios de responsabilidade única e encapsulamento. A classe original agora está mais focada em sua responsabilidade primária como um nó AST, enquanto a classe extraída gerencia o conceito específico de identidade do identificador. Os métodos são mais claros, com responsabilidades bem definidas, e todas as funcionalidades foram adaptadas à nova estrutura.
 */