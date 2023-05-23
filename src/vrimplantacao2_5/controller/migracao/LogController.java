package vrimplantacao2_5.controller.migracao;

import java.util.List;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2_5.classe.Global;
import vrimplantacao2_5.service.migracao.LogPrecoService;
import vrimplantacao2_5.service.migracao.LogService;
import vrimplantacao2_5.vo.cadastro.LogVO;

/**
 *
 * @author guilhermegomes
 */
public class LogController {
    
    private final LogService service = new LogService();
    private final LogPrecoService serviceLogPreco = new LogPrecoService();
    
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

    public void executarLogPreco(List<ProdutoIMP> organizados, String sistema, String loja) throws Exception {
        serviceLogPreco.converteLogPreco(organizados, sistema, loja);
    }
}
