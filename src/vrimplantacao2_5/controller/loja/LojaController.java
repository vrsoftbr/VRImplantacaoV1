package vrimplantacao2_5.controller.loja;

import java.util.List;
import vrimplantacao.vo.loja.LojaFiltroConsultaVO;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao2_5.service.loja.LojaService;

/**
 *
 * @author Desenvolvimento
 */
public class LojaController {
    
    private final LojaService lojaService = new LojaService();
    
    public void salvar(LojaVO vo) throws Exception {
        lojaService.salvar(vo);
    }
    
    public List<LojaVO> consultar(LojaFiltroConsultaVO i_filtro) throws Exception {
        return lojaService.consultar(i_filtro);
    }
    
    public LojaVO carregar(int i_id) throws Exception {
        return lojaService.carregar(i_id);
    }

    public void deletarLoja(LojaVO oLoja) throws Exception {
        lojaService.deletarLoja(oLoja);
    }
}
