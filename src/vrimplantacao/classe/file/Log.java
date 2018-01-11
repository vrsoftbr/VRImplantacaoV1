package vrimplantacao.classe.file;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Leandro.Caires
 */
public class Log implements Closeable {
    
    
    private String nomeArquivo;
    private String titulo;
    private String header;
    private String footer;
    private LogFileType fileFormat;

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getFooter() {
        return footer;
    }

    public void setFooter(String footer) {
        this.footer = footer;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public LogFileType getFileFormat() {
        return fileFormat;
    }

    public void setFileFormat(LogFileType fileFormat) {
        this.fileFormat = fileFormat;
    }

    @Override
    public void close() throws IOException {
        try (FileWriter fw = new FileWriter(nomeArquivo, false)) {
            try (BufferedWriter bw = new BufferedWriter(fw)) {
                LogFormat format = fileFormat.getFormat(nomeArquivo, titulo);
                
                format.fileStart(bw);
                if (header != null && !"".equals(header.trim())) {
                    format.formatHeader(bw, header);
                }      
                format.formatLinhas(bw, linhas);
                if (footer != null && !"".equals(footer.trim())) {
                    format.formatFooter(bw, footer);
                }     
                format.fileEnd(bw);
                bw.flush();
                linhas.clear();
            }
        }
    }

    public void addLog(String categoria, String descricao, LogAdicional observacao) {
        LogLine line = new LogLine();
        line.categoria = categoria;
        line.descricao = descricao;
        line.observacao = observacao;
        line.data = new Timestamp(new java.util.Date().getTime());
        linhas.add(line);
    }
    
    
    private final List<LogLine> linhas = new ArrayList<>();
    
    public Log(String nomeArquivo, String titulo, LogFileType fileFormat) {
        this.nomeArquivo = nomeArquivo;
        this.titulo = titulo;
        this.fileFormat = fileFormat;
    }
    
}
