package vrimplantacao.classe.file;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author Leandro.Caires
 */
public interface LogFormat {

    public void fileStart(BufferedWriter bw) throws IOException;

    public void formatHeader(BufferedWriter bw, String header) throws IOException;

    public void formatLinhas(BufferedWriter bw, List<LogLine> linhas) throws IOException;

    public void formatFooter(BufferedWriter bw, String footer) throws IOException;

    public void fileEnd(BufferedWriter bw) throws IOException;
            
    public String convertLogAdicional(LogAdicional observacao);
    
}
