package vrimplantacao2.utils.arquivo.csv;

import java.util.ArrayList;
import java.util.List;

/**
 * Processador de String de uma linha no formato de arquivos CSV.
 * @author Leandro
 */
public class CsvTokenner {
    
    /**
     * Caracter utilizado para identificar valor String no CSV.
     */
    private final char quote;
    /**
     * Caracter utilizado para identificar delimitador de campo no CSV.
     */
    private final char delimiter;
    /**
     * Indica se está processando um quote.
     */
    private boolean emQuote = false;
    /**
     * Indica se nenhuma letra foi inclusa na palavra. Necessário para o 
     * processamento de quotes.
     */
    private boolean palavraVazia = true;
    /**
     * Lista com os valores encontrados.
     */
    private List<String> lista;
    /**
     * Contrutor da palavra.
     */
    private StringBuilder palavra = new StringBuilder("");

    /**
     * Cria um Tokenner para processar linhas de arquivos CSV.
     * @param delimiter delimitador dos campos.
     * @param quote delimitador de Strings.
     */
    public CsvTokenner(char delimiter, char quote) {
        this.delimiter = delimiter;
        this.quote = quote;
    }
    
    /**
     * Processa uma String de arquivo CSV e separa os valores em um {@link List}.
     * @param string Linha a ser processada.
     * @return {@link List} com os valores separados.
     */
    public List<String> make(String string) {
        lista = new ArrayList<>();
        
        char lastChar = 0;
        if (string != null) {
            for (char ch: string.toCharArray()) {                
                
                if (ch == quote) {
                    processarQuote(ch);
                } else if (ch == delimiter) {
                    processarDelimiter(ch);
                } else {
                    processarChar(ch);
                }
                lastChar = ch;
            }
            
            if (!"".equals(palavra.toString()) || lastChar == delimiter) {
                gravarPalavra();
            }            
        }
        return lista;
    }

    /**
     * Analisa e processa o quote.
     * @param ch 
     */
    private void processarQuote(char ch) {
        if (palavraVazia) {
            emQuote = true;
            palavraVazia = false;
        } else {
            if (emQuote) {
                emQuote = false;
            } else {
                processarChar(ch);
            }
        }
    }

    /**
     * Analisa e processa delimitador.
     * @param ch 
     */
    private void processarDelimiter(char ch) {
        if (emQuote) {
            processarChar(ch);
        } else {
            gravarPalavra();
        }
    }

    /**
     * Inclui na palavra o char informado.
     * @param ch 
     */
    private void processarChar(char ch) {
        palavra.append(ch);
        palavraVazia = false;
    }

    /**
     * Grava a palavra na lista e reinicia as variaveis necessárias.
     */
    private void gravarPalavra() {
        lista.add(palavra.toString());
        emQuote = false;
        palavraVazia = true;
        palavra.delete(0, palavra.length());
    }
    
}
