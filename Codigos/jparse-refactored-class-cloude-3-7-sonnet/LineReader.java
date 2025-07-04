package jparse;

import java.io.*;

final class LineReader extends Reader {

    // TRECHO REFATORADO: Extraída uma classe interna para gerenciar o cache de linhas
    /**
     * Classe responsável pelo gerenciamento do cache de linhas
     */
    private class LineCache {
        private static final int LINE_CACHE_SIZE = 5;
        private final String[] lineCache = new String[LINE_CACHE_SIZE];
        private int validLines;
        private int lineNumber = 1;
        private int cacheLine = 0;
        private int linePos = 0;
        
        /**
         * Inicializa o cache de linhas
         */
        public void initialize(BufferedReader reader) throws IOException {
            for (validLines = 0; validLines < LINE_CACHE_SIZE; validLines++) {
                final String line = reader.readLine();
                if (line == null)
                    break;
                lineCache[validLines] = line;
            }
        }
        
        /**
         * Obtém a linha atual
         */
        public String getCurrentLine() {
            return lineCache[cacheLine];
        }
        
        /**
         * Obtém uma linha específica do cache
         */
        public String getLine(final int lineNum) {
            final int index = lineNum - lineNumber;
            return (index < 0 || index >= validLines) ? null : lineCache[index];
        }
        
        /**
         * Avança para a próxima linha
         */
        public void advanceLine(BufferedReader reader) throws IOException {
            linePos = 0;
            if (cacheLine < validLines - 1) {
                cacheLine++;
            } else {
                rotateCacheAndReadNewLine(reader);
            }
        }
        
        /**
         * Remove a primeira linha do cache, rotaciona as demais e lê uma nova linha
         */
        private void rotateCacheAndReadNewLine(BufferedReader reader) throws IOException {
            final String newLine = reader.readLine();
            if (newLine == null) {
                validLines = 0;
            } else {
                for (int i = 0; i < validLines - 1; i++)
                    lineCache[i] = lineCache[i + 1];
                lineCache[validLines - 1] = newLine;
                lineNumber++;
            }
        }
        
        /**
         * Recarrega o cache a partir de uma posição marcada
         */
        public void reloadFromMark(BufferedReader reader, int markedLineNumber) throws IOException {
            for (int i = 0; i < LINE_CACHE_SIZE; i++) {
                lineCache[i] = reader.readLine();
            }
            validLines = LINE_CACHE_SIZE;
            lineNumber = markedLineNumber;
            cacheLine = 0;
            linePos = 0;
        }
        
        public int getLinePos() {
            return linePos;
        }
        
        public void setLinePos(int pos) {
            linePos = pos;
        }
        
        public void incrementLinePos() {
            linePos++;
        }
        
        public void advanceLinePos(int amount) {
            linePos += amount;
        }
        
        public int getValidLines() {
            return validLines;
        }
    }

    // TRECHO REFATORADO: Extraída uma classe interna para gerenciar marcação e restauração
    /**
     * Classe responsável pelo gerenciamento de marcações
     */
    private class MarkManager {
        private int markLineNumber = -1;
        private int markLinePos = -1;
        
        /**
         * Verifica se existe uma marcação
         */
        public boolean isMarked() {
            return markLineNumber >= 0;
        }
        
        /**
         * Cria uma marcação
         */
        public void setMark(int lineNumber, int cacheLine, int linePos) {
            markLineNumber = lineNumber + cacheLine;
            markLinePos = linePos;
        }
        
        /**
         * Limpa a marcação
         */
        public void clearMark() {
            markLineNumber = -1;
            markLinePos = -1;
        }
        
        /**
         * Verifica se a marcação está no cache atual
         */
        public boolean isMarkInCache(int lineNumber) {
            return markLineNumber >= lineNumber;
        }
        
        public int getMarkLineNumber() {
            return markLineNumber;
        }
        
        public int getMarkLinePos() {
            return markLinePos;
        }
    }
    
    private final LineCache lineCache;
    private final MarkManager markManager;
    private final BufferedReader buf;
    private boolean closed = false;

    // TRECHO REFATORADO: Extraído método para criação do FileReader
    /**
     * Cria um FileReader a partir de diferentes fontes
     */
    private static FileReader createFileReader(Object source) throws IOException {
        if (source instanceof String) {
            return new FileReader((String) source);
        } else if (source instanceof File) {
            return new FileReader((File) source);
        } else if (source instanceof FileDescriptor) {
            return new FileReader((FileDescriptor) source);
        } else {
            throw new IllegalArgumentException("Tipo de fonte inválido");
        }
    }

    // TRECHO REFATORADO: Construtores unificados
    /**
     * Cria um novo LineReader a partir de um arquivo
     */
    LineReader(final String fileName) throws IOException {
        this(createFileReader(fileName));
    }

    /**
     * Cria um novo LineReader a partir de um objeto File
     */
    LineReader(final File file) throws IOException {
        this(createFileReader(file));
    }

    /**
     * Cria um novo LineReader a partir de um descritor de arquivo
     */
    LineReader(final FileDescriptor fd) throws IOException {
        this(createFileReader(fd));
    }

    /**
     * Inicialização interna do LineReader
     */
    private LineReader(final FileReader reader) throws IOException {
        lineCache = new LineCache();
        markManager = new MarkManager();
        buf = new BufferedReader(reader);
        lineCache.initialize(buf);
    }

    /**
     * Finaliza a leitura de uma linha
     */
    private void lineDone() throws IOException {
        lineCache.advanceLine(buf);
    }

    /**
     * Obtém uma linha específica do arquivo
     */
    String getLine(final int lineNum) {
        return lineCache.getLine(lineNum);
    }

    // TRECHO REFATORADO: Método read() simplificado
    public int read() throws IOException {
        if (closed)
            throw new IOException("Stream closed");
        if (lineCache.getValidLines() == 0)
            return -1;
            
        final String line = lineCache.getCurrentLine();
        final char retChar;
        
        if (lineCache.getLinePos() < line.length()) {
            retChar = line.charAt(lineCache.getLinePos());
            lineCache.incrementLinePos();
        } else {
            retChar = '\n';
            lineDone();
        }
        return retChar;
    }

    // TRECHO REFATORADO: Método read(char[], int, int) dividido em partes menores
    public int read(final char[] cbuf, final int off, final int len)
        throws IOException {

        if (closed)
            throw new IOException("Stream closed");
        if (lineCache.getValidLines() == 0)
            return -1;
            
        return readIntoBuffer(cbuf, off, len);
    }
    
    // TRECHO REFATORADO: Método extraído para processar a leitura no buffer
    private int readIntoBuffer(char[] cbuf, int off, int len) throws IOException {
        int chars = 0;
        while (chars < len && lineCache.getValidLines() != 0) {
            final String line = lineCache.getCurrentLine();
            final int length = line.length();
            final int availChars = length + 1 - lineCache.getLinePos();  // +1 for newline
            
            if (chars + availChars < len) {
                chars = readEntireLine(cbuf, off, chars, line, length);
            } else {
                chars = readPartialLine(cbuf, off, chars, len, line);
            }
        }
        return chars;
    }
    
    // TRECHO REFATORADO: Método extraído para ler uma linha inteira
    private int readEntireLine(char[] cbuf, int off, int chars, String line, int length) 
            throws IOException {
        line.getChars(lineCache.getLinePos(), length, cbuf, off + chars);
        chars += length - lineCache.getLinePos();
        cbuf[off + chars++] = '\n';
        lineDone();
        return chars;
    }
    
    // TRECHO REFATORADO: Método extraído para ler parte de uma linha
    private int readPartialLine(char[] cbuf, int off, int chars, int len, String line) {
        final int lastIndex = len - chars + lineCache.getLinePos();
        line.getChars(lineCache.getLinePos(), lastIndex, cbuf, off + chars);
        lineCache.advanceLinePos(len - chars);
        return len;
    }

    public boolean ready() throws IOException {
        if (closed)
            throw new IOException("Stream closed");
        return lineCache.getValidLines() != 0;
    }

    public boolean markSupported() {
        return true;
    }

    public void mark(final int readAheadLimit) throws IOException {
        if (closed)
            throw new IOException("Stream closed");
        buf.mark(readAheadLimit);
        markManager.setMark(lineCache.lineNumber, lineCache.cacheLine, lineCache.getLinePos());
    }

    // TRECHO REFATORADO: Método reset() simplificado
    public void reset() throws IOException {
        if (closed)
            throw new IOException("Stream closed");
        if (!markManager.isMarked())
            throw new IOException("Stream not marked");
            
        resetToMark();
        markManager.clearMark();
    }
    
    // TRECHO REFATORADO: Método extraído para restaurar para uma marcação
    private void resetToMark() throws IOException {
        if (markManager.isMarkInCache(lineCache.lineNumber)) {
            resetToMarkInCache();
        } else {
            resetToMarkOutsideCache();
        }
    }
    
    // TRECHO REFATORADO: Método extraído para restaurar para uma marcação no cache
    private void resetToMarkInCache() {
        lineCache.cacheLine = markManager.getMarkLineNumber() - lineCache.lineNumber;
        lineCache.setLinePos(markManager.getMarkLinePos());
    }
    
    // TRECHO REFATORADO: Método extraído para restaurar para uma marcação fora do cache
    private void resetToMarkOutsideCache() throws IOException {
        buf.reset();
        lineCache.reloadFromMark(buf, markManager.getMarkLineNumber());
    }

    public void close() throws IOException {
        closed = true;
        buf.close();
    }
}

/**
 * ## 1) Oportunidades de refatoração encontradas
 * 1. **Duplicação nos construtores**: Os construtores `LineReader(String)`, `LineReader(File)` e `LineReader(FileDescriptor)` contêm lógica duplicada, todos delegando para o construtor privado `LineReader(FileReader)`.
 * 2. **Método `read(char[], int, int)` complexo**: Este método é longo e faz muitas operações, o que dificulta a compreensão e manutenção.
 * 3. **Gerenciamento do cache de linhas**: A lógica de gerenciamento do cache de linhas (rotação e atualização) está espalhada por diferentes métodos como `lineDone()` e `reset()`.
 * 4. **Responsabilidades misturadas**: A classe possui muitas responsabilidades - leitura de arquivo, gerenciamento de cache, controle de posição de leitura, marcação e restauração de posições.
 *
 * ## 2) Classe refatorada com os devidos comentários
 *
 * ## 3) Justificativa das refatorações
 * 1. **Extração de Classes**:
 *     - **LineCache**: Extraí essa classe interna para encapsular toda a lógica relacionada ao gerenciamento do cache de linhas. Segundo Martin Fowler, isso ajuda a melhorar a coesão, agrupando métodos que atuam sobre a mesma estrutura de dados.
 *     - **MarkManager**: Extraí essa classe para gerenciar a marcação e restauração de posições. Seguindo os princípios do livro Engenharia de Software Moderna, isso melhora a Separação de Responsabilidades.
 *
 * 2. **Extração de Métodos**:
 *     - **createFileReader**: Método extraído para remover a duplicação nos construtores, seguindo o princípio DRY (Don't Repeat Yourself) mencionado por Fowler.
 *     - **readIntoBuffer, readEntireLine, readPartialLine**: O método `read(char[], int, int)` foi dividido em partes menores e mais focadas, melhorando a legibilidade e facilitando a manutenção, como recomendado na Engenharia de Software Moderna.
 *     - **resetToMark, resetToMarkInCache, resetToMarkOutsideCache**: O método `reset()` foi dividido em partes menores, cada uma com uma responsabilidade clara.
 *
 * 3. **Encapsulamento de comportamentos**:
 *     - Os métodos das classes extraídas encapsulam comportamentos específicos, como gerenciamento do cache e marcação, reduzindo o acoplamento entre diferentes partes do código.
 *
 * 4. **Melhoria da legibilidade**:
 *     - Todos os métodos extraídos têm nomes descritivos que refletem o que fazem, seguindo as recomendações de Martin Fowler para nomes de métodos.
 *     - Os comentários em português explicam o propósito de cada classe e método extraído.
 *
 * ## 4) Resumo das alterações
 * - **Quantidade total de refatorações realizadas**: 12
 * - **Extração de Método**: 10
 *     - createFileReader
 *     - readIntoBuffer
 *     - readEntireLine
 *     - readPartialLine
 *     - resetToMark
 *     - resetToMarkInCache
 *     - resetToMarkOutsideCache
 *     - (Além de vários métodos internos nas classes extraídas)
 *
 * - **Extração de Classe**: 2
 *     - LineCache
 *     - MarkManager
 */