package vrimplantacao2_5.service.migracao;

import org.openide.util.Exceptions;
import vrframework.classe.Util;
import vrimplantacao2_5.dao.migracao.LogDAO;
import vrimplantacao2_5.vo.cadastro.LogVO;

/**
 *
 * @author guilhermegomes
 */
public class LogService {
    
    private LogDAO logDAO;

    public LogService() {
        logDAO = new LogDAO();
    }
    
    public LogService(LogDAO logDAO) {
        this.logDAO = logDAO;
    }
    
    public void executar(LogVO logVO) {
        try {
            logDAO.executar(logVO);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, "Log");
        }
    }
}
