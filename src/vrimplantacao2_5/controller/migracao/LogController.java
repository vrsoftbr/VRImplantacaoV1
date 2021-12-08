package vrimplantacao2_5.controller.migracao;

import java.util.Date;
import vrimplantacao2_5.classe.Global;
import vrimplantacao2_5.service.migracao.LogService;
import vrimplantacao2_5.vo.cadastro.LogVO;

/**
 *
 * @author guilhermegomes
 */
public class LogController {
    
    private final LogService service = new LogService();
    
    public void executar(
            int idTipoOperacao, 
            String dataHoraImportacao,
            int idLoja) {
        
        LogVO logVO = new LogVO();
        
        logVO.setIdTipoOperacao(idTipoOperacao);
        logVO.setIdUsuario(Global.getIdUsuario());
        logVO.setDataHoraTime(dataHoraImportacao);
        logVO.setIdLoja(idLoja);
        
        service.executar(logVO);
    }
}
