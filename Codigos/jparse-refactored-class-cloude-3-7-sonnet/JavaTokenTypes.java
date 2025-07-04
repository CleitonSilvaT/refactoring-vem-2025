package jparse;

import java.io.*;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedList;
import jparse.expr.*;
import jparse.stmt.*;

// TRECHO REFATORADO
// Interface principal, agora contendo apenas os tokens fundamentais e
// estendendo interfaces específicas para cada categoria de token
public interface JavaTokenTypes extends 
        BasicTokenTypes,
        NodeTokenTypes,
        LiteralTokenTypes,
        OperatorTokenTypes,
        StatementTokenTypes,
        SymbolTokenTypes,
        ModifierTokenTypes,
        MiscTokenTypes {
    
    // Tokens fundamentais mantidos aqui porque são essenciais ou não se encaixam em categorias
    int EOF = 1;
    int NULL_TREE_LOOKAHEAD = 3;
}

// TRECHO REFATORADO
// Interface extraída para tokens relacionados a estruturas básicas de programa
interface BasicTokenTypes {
    int FILE = 4;
    int VARIABLE_DEFS = 5;
    int MODIFIERS = 6;
    int ARRAY_DECLARATOR = 7;
    int TYPE = 8;
    int EXTENDS_CLAUSE = 9;
    int OBJBLOCK = 10;
    int IMPLEMENTS_CLAUSE = 11;
    int CTOR_DEF = 12;
    int METHOD_DEF = 13;
    int INSTANCE_INIT = 14;
    int VARIABLE_DEF = 15;
    int ARRAY_INIT = 16;
    int PARAMETERS = 17;
    int PARAMETER_DEF = 18;
}

// TRECHO REFATORADO
// Interface extraída para tokens relacionados a nós da árvore sintática
interface NodeTokenTypes {
    int SLIST = 19;
    int TYPE_STAT = 20;
    int EXPRESSION_STAT = 21;
    int LABELED_STAT = 22;
    int EMPTY_STAT = 23;
    int CASE_GROUP = 24;
    int FOR_INIT = 25;
    int FOR_CONDITION = 26;
    int FOR_ITERATOR = 27;
    int ELIST = 28;
    int CONCAT_ASSIGN = 29;
    int CONCATENATION = 30;
    int UNARY_MINUS = 31;
    int UNARY_PLUS = 32;
    int TYPECAST = 33;
    int INDEX_OP = 34;
    int METHOD_CALL = 35;
    int CONSTRUCTOR_CALL = 36;
    int POST_INC = 37;
    int POST_DEC = 38;
    int PAREN_EXPR = 39;
}

// TRECHO REFATORADO
// Interface extraída para tokens relacionados a literais e tipos primitivos
interface LiteralTokenTypes {
    int LITERAL_void = 69;
    int LITERAL_boolean = 70;
    int LITERAL_byte = 71;
    int LITERAL_char = 72;
    int LITERAL_short = 73;
    int LITERAL_int = 74;
    int LITERAL_float = 75;
    int LITERAL_long = 76;
    int LITERAL_double = 77;
    int LITERAL_this = 131;
    int LITERAL_super = 132;
    int LITERAL_true = 133;
    int LITERAL_false = 134;
    int LITERAL_null = 135;
    int LITERAL_new = 136;
    int NUM_INT = 137;
    int CHAR_LITERAL = 138;
    int STRING_LITERAL = 139;
    int NUM_FLOAT = 140;
}

// TRECHO REFATORADO
// Interface extraída para tokens relacionados a operadores
interface OperatorTokenTypes {
    int ASSIGN = 78;
    int PLUS_ASSIGN = 96;
    int MINUS_ASSIGN = 97;
    int STAR_ASSIGN = 98;
    int DIV_ASSIGN = 99;
    int MOD_ASSIGN = 100;
    int SR_ASSIGN = 101;
    int BSR_ASSIGN = 102;
    int SL_ASSIGN = 103;
    int BAND_ASSIGN = 104;
    int BXOR_ASSIGN = 105;
    int BOR_ASSIGN = 106;
    int QUESTION = 107;
    int LOR = 108;
    int LAND = 109;
    int BOR = 110;
    int BXOR = 111;
    int BAND = 112;
    int NOT_EQUAL = 113;
    int EQUAL = 114;
    int LT = 115;
    int GT = 116;
    int LE = 117;
    int GE = 118;
    int SL = 120;
    int SR = 121;
    int BSR = 122;
    int PLUS = 123;
    int MINUS = 124;
    int STAR = 45;
    int DIV = 125;
    int MOD = 126;
    int INC = 127;
    int DEC = 128;
    int BNOT = 129;
    int LNOT = 130;
}

// TRECHO REFATORADO
// Interface extraída para tokens relacionados a palavras-chave de declarações
interface StatementTokenTypes {
    int LITERAL_package = 40;
    int LITERAL_import = 42;
    int LITERAL_class = 57;
    int LITERAL_extends = 58;
    int LITERAL_implements = 59;
    int LITERAL_interface = 61;
    int LITERAL_throws = 66;
    int LITERAL_if = 80;
    int LITERAL_else = 81;
    int LITERAL_for = 82;
    int LITERAL_while = 83;
    int LITERAL_do = 84;
    int LITERAL_break = 85;
    int LITERAL_continue = 86;
    int LITERAL_return = 87;
    int LITERAL_switch = 88;
    int LITERAL_throw = 89;
    int LITERAL_assert = 90;
    int LITERAL_case = 91;
    int LITERAL_default = 92;
    int LITERAL_try = 93;
    int LITERAL_finally = 94;
    int LITERAL_catch = 95;
    int LITERAL_instanceof = 119;
}

// TRECHO REFATORADO
// Interface extraída para tokens relacionados a símbolos
interface SymbolTokenTypes {
    int SEMI = 41;
    int IDENT = 43;
    int DOT = 44;
    int COMMA = 60;
    int LCURLY = 62;
    int RCURLY = 63;
    int LPAREN = 64;
    int RPAREN = 65;
    int LBRACK = 67;
    int RBRACK = 68;
    int COLON = 79;
}

// TRECHO REFATORADO
// Interface extraída para tokens relacionados a modificadores
interface ModifierTokenTypes {
    int LITERAL_public = 46;
    int LITERAL_private = 47;
    int LITERAL_protected = 48;
    int LITERAL_static = 49;
    int LITERAL_final = 50;
    int LITERAL_synchronized = 51;
    int LITERAL_volatile = 52;
    int LITERAL_transient = 53;
    int LITERAL_native = 54;
    int LITERAL_abstract = 55;
    int LITERAL_strictfp = 56;
}

// TRECHO REFATORADO
// Interface extraída para tokens diversos que não se encaixam nas outras categorias
interface MiscTokenTypes {
    int CONST = 141;
    int GOTO = 142;
    int WS = 143;
    int SL_COMMENT = 144;
    int ML_COMMENT = 145;
    int ESC = 146;
    int HEX_DIGIT = 147;
    int EXPONENT = 148;
    int FLOAT_SUFFIX = 149;
}

/**
 * ## 1) Oportunidades de refatoração encontradas
 * Após analisar o código, identifiquei os seguintes problemas que podem ser resolvidos com refatorações:
 * 1. **Coesão limitada**: A interface contém uma grande quantidade de constantes (tokens) sem organização lógica por categorias, o que dificulta a manutenção.
 * 2. **Ausência de categorização**: Os tokens poderiam ser agrupados por categorias funcionais (operadores, tipos primitivos, palavras-chave, etc.).
 * 3. **Interface excessivamente grande**: Com quase 150 constantes, a interface viola o princípio de responsabilidade única, dificultando sua manutenção.
 *
 * ## 2) Código refatorado com os devidos comentários
 *
 * ## 3) Justificativa das refatorações
 * ### Extração de Classes (Interfaces)
 * Realizei a **Extração de Classe** para dividir a interface original em várias interfaces menores e mais coesas, cada uma representando uma categoria específica de tokens. Esta refatoração se baseia nos seguintes princípios:
 * 1. **Princípio da Responsabilidade Única (SRP)**: Cada interface agora tem uma única responsabilidade - gerenciar um tipo específico de token.
 * 2. **Coesão Aumentada**: As constantes relacionadas estão agrupadas logicamente, tornando o código mais compreensível e mais fácil de manter.
 * 3. **Interface Segregation Principle (ISP)**: Os clientes agora podem importar apenas as interfaces de tokens de que precisam, em vez de importar tudo.
 *
 * A interface principal `JavaTokenTypes` agora estende todas as interfaces específicas, mantendo a compatibilidade com o código existente, mas oferecendo uma estrutura mais modular.
 * ### Organização por Categorias
 * A categorização dos tokens em interfaces separadas segue o conceito de **Alta Coesão** discutido por Martin Fowler, onde elementos relacionados são agrupados. As categorias escolhidas representam grupos lógicos de tokens com funções semelhantes no parser:
 * - `BasicTokenTypes`: Elementos estruturais básicos de um programa Java
 * - `NodeTokenTypes`: Nós específicos na árvore sintática
 * - `LiteralTokenTypes`: Literais e tipos primitivos
 * - `OperatorTokenTypes`: Operadores (aritméticos, lógicos, etc.)
 * - `StatementTokenTypes`: Palavras-chave relacionadas a declarações
 * - `SymbolTokenTypes`: Símbolos da sintaxe (pontuação, etc.)
 * - `ModifierTokenTypes`: Modificadores de acesso e comportamento
 * - `MiscTokenTypes`: Tokens diversos que não se encaixam nas outras categorias
 *
 * ## 4) Resumo das alterações
 * ### Quantidade total de refatorações realizadas:
 * - **8 refatorações**
 *
 * ### Divisão por tipo:
 * - **Extração de Classe (Interface)**: 8 extrações
 *     - Extraída a interface `BasicTokenTypes`
 *     - Extraída a interface `NodeTokenTypes`
 *     - Extraída a interface `LiteralTokenTypes`
 *     - Extraída a interface `OperatorTokenTypes`
 *     - Extraída a interface `StatementTokenTypes`
 *     - Extraída a interface `SymbolTokenTypes`
 *     - Extraída a interface `ModifierTokenTypes`
 *     - Extraída a interface `MiscTokenTypes`
 *
 * - **Extração de Método**: 0 extrações (não aplicável a interfaces, que contêm apenas constantes)
 *
 * Estas refatorações resultaram em uma estrutura mais modular e de fácil manutenção, sem alterar o comportamento do código. A interface principal `JavaTokenTypes` continua fornecendo todos os mesmos tokens, mas agora com uma estrutura interna melhor organizada.
 */