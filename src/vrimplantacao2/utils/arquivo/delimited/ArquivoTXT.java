package vrimplantacao2.utils.arquivo.delimited;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import org.openide.util.Exceptions;
import vrimplantacao2.utils.arquivo.DefaultLinha;
import vrimplantacao2.utils.arquivo.LinhaArquivo;

/**
 * Executa a leitura de arquivos texto.
 * @author Leandro
 */
public class ArquivoTXT implements Closeable, Iterator<LinhaArquivo>, Iterable<LinhaArquivo> {
    
    private FileInputStream input;
    private BufferedReader scanner;

    public ArquivoTXT(String arquivo) throws IOException {
        this.input = new FileInputStream(arquivo);
        this.scanner = new BufferedReader(new InputStreamReader(input));
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
                linha = new DefaultLinha();
                linha.putString("", ln);
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
