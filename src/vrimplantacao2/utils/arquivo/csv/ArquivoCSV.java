package vrimplantacao2.utils.arquivo.csv;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao2.utils.arquivo.AbstractArquivo;
import vrimplantacao2.utils.arquivo.DefaultLinha;

/**
 *
 * @author Leandro
 */
public class ArquivoCSV extends AbstractArquivo {
    
    private char stringQuote = '\"';

    public void setStringQuote(char stringQuote) {
        this.stringQuote = stringQuote;
    }
    
    /**
     * ("([a-Z|0-9| |""]*)"|( )*|)(;)0-1
     * @param ln
     * @param delimiter
     * @param stringQuote
     * @return 
     * @throws vrimplantacao2.utils.arquivo.csv.ArquivoCSV.CSVException 
     */    
    public static List<String> tratarString(String ln, char delimiter, char stringQuote) throws CSVException {
        List<String> result = new ArrayList<>();
        if (ln == null) {
            ln = "";
        }
        StringBuilder valor = new StringBuilder();
        char[] chars = ln.toCharArray();
        boolean 
               emAspas = false,
               emSegundaAspas = false;
        //""""sad";"
        for (char ch: chars) {
            if (emAspas) {                
                if (stringQuote == ch) {
                    emAspas = false;
                    if (!emSegundaAspas) {
                        emSegundaAspas = true;
                    }            
                } else {
                    valor.append(ch);
                }
            } else { 
                if ( delimiter == ch) {
                    result.add(valor.toString());
                    valor = new StringBuilder();
                    emAspas = false;
                    emSegundaAspas = false;
                } else if (stringQuote == ch) {
                    if (emSegundaAspas) {
                        valor.append("\"");
                        emSegundaAspas = false;
                    }
                    emAspas = true;                    
                } else {
                    if (emSegundaAspas) {
                        throw new CSVException(stringQuote + " não fechada \"" + ln + "\"");
                    }
                    valor.append(ch);
                    emSegundaAspas = false;
                }
            }
        }
        
        if (valor.length() > 0) {
            result.add(valor.toString());
        }
        
        return result;
    }

    public ArquivoCSV(String arquivo, char delimiter, boolean quoteString, char stringQuote) throws Exception {
        try (FileReader fr = new FileReader(arquivo)) {
            try (BufferedReader br = new BufferedReader(fr)) {
                
                cabecalho = new ArrayList<>();
                dados = new ArrayList<>();
                
                for (String valor : tratarString(br.readLine(), delimiter, stringQuote)) {
                    cabecalho.add(valor.trim());
                }
                
                for (String ln = br.readLine(); ln != null; ln = br.readLine()) {        
                    DefaultLinha linha = new DefaultLinha();   
                    List<String> columns = tratarString(ln, delimiter, stringQuote);
                    for (int j = 0; (j < columns.size() && j < cabecalho.size()); j++) {
                        String valor = columns.get(j).trim();                     
                        linha.putString(cabecalho.get(j), valor);
                    }
                    dados.add(linha);         
                }
            }
        }
    }
    
    public static class CSVException extends Exception{ 

        public CSVException() {
        }

        public CSVException(String message) {
            super(message);
        }

        public CSVException(String message, Throwable cause) {
            super(message, cause);
        }

        public CSVException(Throwable cause) {
            super(cause);
        }

        public CSVException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }
    
    }
}
