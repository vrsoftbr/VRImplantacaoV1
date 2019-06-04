package vrimplantacao2.utils.arquivo;

import java.util.Map;
import vrimplantacao2.utils.arquivo.csv.ArquivoCSV;
import vrimplantacao2.utils.arquivo.planilha.Planilha;

/**
 * Esta enumeração cria objetos da classe {@link Arquivo} de acordo com a extensão.
 * @author Leandro
 */
public enum ArquivoFactory {
    /**
     * Arquivo no formato (*.csv).
     */
    CSV {
        @Override
        public Arquivo load(String arquivo, Map<String, String> opcoes) throws Exception {
            String delimiter = ";";
            boolean quoteString = true;
            char stringQuote = '\"';
            if (opcoes != null) {
                delimiter = opcoes.get("delimiter") != null ? opcoes.get("delimiter") : ";";
                quoteString = "true".equalsIgnoreCase(opcoes.get("quoteString"));                
                stringQuote = (opcoes.get("quote") != null ? opcoes.get("quote") : "\"").charAt(0);
            }
            return new ArquivoCSV(arquivo, delimiter.charAt(0), quoteString, stringQuote);
        }
    },
    /**
     * Arquivo do Excel 97-2003 (*.xls).
     */
    XLS {
        @Override
        public Arquivo load(String arquivo, Map<String, String> opcoes) throws Exception {            
            String dateFormat = "yyyy-MM-dd";
            String timeFormat = "hh:mm:ss";
            if (opcoes != null) {
                dateFormat = opcoes.get("dateformat") != null ? opcoes.get("dateformat") : "yyyy-MM-ddd";
                timeFormat = opcoes.get("timeformat") != null ? opcoes.get("timeformat") : "hh:mm:ss";
            }
            return new Planilha(arquivo, dateFormat, timeFormat);
        }
    };

    /**
     * Factory Method utilizado para fabricar {@link Arquivo}.
     * @param arquivo Local e nome do arquivo.
     * @param opcoes Opções para abrir os arquivos.
     * @return Arquivo transformado ou null caso não seja de um formato válido.
     * @throws Exception 
     */
    public static Arquivo getArquivo(String arquivo, Map<String, String> opcoes) throws Exception {
        if (arquivo.toLowerCase().endsWith(".xls")) {
            return XLS.load(arquivo, opcoes);
        } else  {
            return CSV.load(arquivo, opcoes);
        } 
    }

    /**
     * Factory Method utilizado para fabricar {@link Arquivo}.
     * @param arquivo Local e nome do arquivo.
     * @param opcoes Opções para abrir os arquivos.
     * @return Arquivo transformado ou null caso não seja de um formato válido.
     * @throws Exception 
     */
    public abstract Arquivo load(String arquivo, Map<String, String> opcoes) throws Exception;
  
}
