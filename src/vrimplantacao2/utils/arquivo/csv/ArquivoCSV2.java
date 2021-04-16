package vrimplantacao2.utils.arquivo.csv;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.openide.util.Exceptions;
import vrimplantacao2.utils.arquivo.DefaultLinha;
import vrimplantacao2.utils.arquivo.LinhaArquivo;

/**
 * Executa a leitura de arquivos texto.
 * @author Leandro
 */
public class ArquivoCSV2 implements Closeable, Iterator<LinhaArquivo>, Iterable<LinhaArquivo> {
    
    private FileInputStream input;
    private BufferedReader scanner;
    private List<String> cabecalho;
    private CsvTokenner tokener;

    public ArquivoCSV2(String arquivo, char delimiter, char stringQuote) throws IOException {
        this.input = new FileInputStream(arquivo);
        this.scanner = new BufferedReader(new InputStreamReader(input));
        this.tokener = new CsvTokenner(delimiter, stringQuote);
        String ln = scanner.readLine();            
        if (ln != null) {            
            this.cabecalho = tokener.make(ln);
        } else {
            this.cabecalho = new ArrayList<>();
        }
    }

    @Override
    public void close() throws IOException {
        if (this.scanner != null) {
            this.scanner.close();
        }
        if (this.input != null) {
            this.input.close();
        }
    }

    private LinhaArquivo linha = null;
    
    public LinhaArquivo get() {
        return linha;
    }
    
    private void processarLinha() throws IOException {
        if ( linha == null ) {
            
            String ln = this.scanner.readLine();

            if (ln != null) {

                List<String> values = tokener.make(ln);

                linha = new DefaultLinha();
                for (int i = 0; i < cabecalho.size(); i++) {
                    String value;
                    if (i < values.size()) {
                        value = values.get(i);
                    } else {
                        value = "";
                    }
                    String name = cabecalho.get(i);
                    
                    if(name.contains("\"")) {
                        name = name.replace("\"", "");
                    }

                    linha.putString(name, value);
                }

            }
        
        }
         
    }

    @Override
    public boolean hasNext() {
        try {
            processarLinha();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return linha != null;
    }

    @Override
    public LinhaArquivo next() {
        try {
            processarLinha();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        LinhaArquivo aux = linha;
        linha = null;
        return aux;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Operação não suportada.");
    }

    @Override
    public Iterator<LinhaArquivo> iterator() {
        return this;
    }
    
}
