package vrimplantacao2.dao.repositories.produto;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import vrframework.classe.ProgressBar;
import vrimplantacao2.dao.repositories.Recorder;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author leandro
 */
public class ProdutoRepositoryProvider2 {
    
    private static final Logger LOG = Logger.getLogger(ProdutoRepositoryProvider2.class.getName());
    
    private final String sistema;
    private final String lojaOrigem;
    private final int lojaVR;
    private final Logger log;
    
    public ProdutoRepositoryProvider2(String sistema, String lojaOrigem, int lojaVR, Logger log) {
        this.sistema = sistema;
        this.lojaOrigem = lojaOrigem;
        this.lojaVR = lojaVR;
        this.log = log;
    }

    public String getSistema() {
        return sistema;
    }

    public String getLojaOrigem() {
        return lojaOrigem;
    }

    public int getLojaVR() {
        return lojaVR;
    }

    void setMessage(String mensagem) {
        try {
            ProgressBar.setStatus(mensagem);
            log.info(mensagem);
        } catch (Exception ex) {
            log.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
            throw new RuntimeException(ex.getLocalizedMessage(), ex);
        }
    }

    public boolean isListaVazia() {
        return true;
    }

    public Recorder<ProdutoIMP> getProdutoRecorder() {
        return new ProdutoRecorder();
    }
    
}
