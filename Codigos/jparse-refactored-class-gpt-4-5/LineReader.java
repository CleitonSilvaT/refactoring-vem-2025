package jparse;

import java.io.*;

final class LineReader extends Reader {

    private static final int LINE_CACHE_SIZE = 5;
    private final String[] lineCache = new String[LINE_CACHE_SIZE];
    private int validLines;
    private int lineNumber = 1;
    private int cacheLine = 0;
    private int linePos = 0;
    private final BufferedReader buf;
    private boolean closed = false;
    private int markLineNumber = -1;
    private int markLinePos = -1;

    LineReader(final String fileName) throws IOException {
        this(new FileReader(fileName));
    }

    LineReader(final File file) throws IOException {
        this(new FileReader(file));
    }

    LineReader(final FileDescriptor fd) throws IOException {
        this(new FileReader(fd));
    }

    private LineReader(final FileReader reader) throws IOException {
        buf = new BufferedReader(reader);
        preencherCacheInicial(); // TRECHO REFATORADO: Extração de método
    }

    // TRECHO REFATORADO: Extração do método preencherCacheInicial
    private void preencherCacheInicial() throws IOException {
        for (validLines = 0; validLines < LINE_CACHE_SIZE; validLines++) {
            final String line = buf.readLine();
            if (line == null)
                break;
            lineCache[validLines] = line;
        }
    }

    private void lineDone() throws IOException {
        linePos = 0;
        if (cacheLine < validLines - 1) {
            cacheLine++;
        } else {
            rotacionarCache(); // TRECHO REFATORADO: Extração de método
        }
    }

    // TRECHO REFATORADO: Extração do método rotacionarCache
    private void rotacionarCache() throws IOException {
        final String newLine = buf.readLine();
        if (newLine == null) {
            validLines = 0;
        } else {
            for (int i = 0; i < validLines - 1; i++)
                lineCache[i] = lineCache[i + 1];
            lineCache[validLines - 1] = newLine;
            lineNumber++;
        }
    }

    String getLine(final int lineNum) {
        final int index = lineNum - lineNumber;
        return (index < 0 || index >= validLines) ? null : lineCache[index];
    }

    public int read() throws IOException {
        if (closed)
            throw new IOException("Stream closed");
        if (validLines == 0)
            return -1;
        final String line = lineCache[cacheLine];
        final char retChar;
        if (linePos < line.length()) {
            retChar = line.charAt(linePos++);
        } else {
            retChar = '\n';
            lineDone();
        }
        return retChar;
    }

    public int read(final char[] cbuf, final int off, final int len)
            throws IOException {

        if (closed)
            throw new IOException("Stream closed");
        if (validLines == 0)
            return -1;
        int chars = 0;
        while (chars < len && validLines != 0) {
            final String line = lineCache[cacheLine];
            final int length = line.length();
            final int availChars = length + 1 - linePos;
            if (chars + availChars < len) {
                line.getChars(linePos, length, cbuf, off + chars);
                chars += length;
                cbuf[off + chars++] = '\n';
                lineDone();
            } else {
                final int lastIndex = len - chars + linePos;
                line.getChars(linePos, lastIndex, cbuf, off + chars);
                linePos += len - chars;
                chars = len;
            }
        }
        return chars;
    }

    public boolean ready() throws IOException {
        if (closed)
            throw new IOException("Stream closed");
        return validLines != 0;
    }

    public boolean markSupported() {
        return true;
    }

    public void mark(final int readAheadLimit) throws IOException {
        if (closed)
            throw new IOException("Stream closed");
        buf.mark(readAheadLimit);
        markLineNumber = lineNumber + cacheLine;
        markLinePos = linePos;
    }

    public void reset() throws IOException {
        if (closed)
            throw new IOException("Stream closed");
        if (markLineNumber < 0)
            throw new IOException("Stream not marked");
        if (markLineNumber >= lineNumber) {
            cacheLine = markLineNumber - lineNumber;
            linePos = markLinePos;
        } else {
            recarregarCacheAPartirDoMarcador(); // TRECHO REFATORADO: Extração de método
        }
        markLineNumber = markLinePos = -1;
    }

    // TRECHO REFATORADO: Extração do método recarregarCacheAPartirDoMarcador
    private void recarregarCacheAPartirDoMarcador() throws IOException {
        buf.reset();
        for (int i = 0; i < LINE_CACHE_SIZE; i++) {
            lineCache[i] = buf.readLine();
        }
        validLines = LINE_CACHE_SIZE;
        lineNumber = markLineNumber;
        cacheLine = 0;
        linePos = 0;
    }

    public void close() throws IOException {
        closed = true;
        buf.close();
    }
}

/**
 * 1) Oportunidades de Refatoração Encontradas:
Extração de Método:

O construtor privado da classe está realizando inicialização do cache com um loop de leitura das linhas. Este comportamento pode ser extraído para um método próprio (preencherCacheInicial()), aumentando a legibilidade.

O método lineDone() possui uma lógica interna para rotação das linhas do cache, que pode ser claramente separada em um método (rotacionarCache()).

O método reset() contém um trecho que reconstrói completamente o cache ao resetar para uma marcação anterior, podendo ser extraído para recarregarCacheAPartirDoMarcador().

Extração de Classe:

Não foi encontrada uma clara justificativa para extração de uma nova classe. A classe LineReader mantém coesão suficiente para lidar apenas com leitura e cache das linhas, não apresentando responsabilidades adicionais que justifiquem essa extração (Fowler, 2018).

2) Classe Refatorada com Comentários:

3) Justificativa das Refatorações:
preencherCacheInicial(): Extrair essa responsabilidade melhora a clareza do construtor, seguindo o princípio de métodos pequenos e com uma única responsabilidade (Fowler, 2018; Marco Tulio, 2020).

rotacionarCache(): Clarifica a intenção do método lineDone(), removendo detalhes internos que obscurecem o fluxo principal.

recarregarCacheAPartirDoMarcador(): Separa uma operação específica do método reset(), tornando mais explícito o seu funcionamento, aumentando a facilidade de manutenção e entendimento do fluxo.

Estas refatorações fortalecem a clareza, isolando detalhes específicos em métodos com nomes descritivos, reduzindo duplicações internas e facilitando futuras alterações (Marco Tulio, 2020).

4) Resumo das Alterações:
Total de refatorações realizadas: 3

Extração de Método: 3

Extração de Classe: 0 (não foram identificadas necessidades claras)
 */