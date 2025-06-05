package vrimplantacao2_5.controller.migracao;

import java.util.List;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2_5.classe.Global;
import vrimplantacao2_5.service.migracao.LogAtualizacaoService;
import vrimplantacao2_5.service.migracao.LogService;
import vrimplantacao2_5.vo.cadastro.LogVO;

/**
 *
 * @author guilhermegomes
 */
public class LogController {
    
    private final LogService service = new LogService();
    private final LogAtualizacaoService serviceLogAtualizacao = new LogAtualizacaoService();
    
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

    public void executarLogAtualizacao(List<ProdutoIMP> organizados, String sistema, String impLoja, Integer lojaAtual) throws Exception {
        serviceLogAtualizacao.converteLogAtualizacao(organizados, sistema, impLoja, lojaAtual);
    }
}
