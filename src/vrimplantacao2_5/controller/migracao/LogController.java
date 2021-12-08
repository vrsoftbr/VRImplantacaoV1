package vrimplantacao2_5.controller.migracao;

import org.joda.time.LocalDateTime;
import vrimplantacao2_5.service.migracao.LogService;
import vrimplantacao2_5.vo.cadastro.LogVO;

/**
 *
 * @author guilhermegomes
 */
public class LogController {
    
    private final LogService service = new LogService();
    
    public void executar(int idTipoOperacao, int idUsuario, java.sql.Date dataHoraImportacao) {
        LogVO logVO = new LogVO();
        
        logVO.setIdTipoOperacao(idTipoOperacao);
        logVO.setIdUsuario(idUsuario);
        logVO.setDataHora(dataHoraImportacao);
        
        service.executar(logVO);
    }
}
