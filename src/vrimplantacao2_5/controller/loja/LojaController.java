package vrimplantacao2_5.controller.loja;

import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao2_5.service.loja.LojaService;

/**
 *
 * @author Desenvolvimento
 */
public class LojaController {
    
    private final LojaService lojaService = new LojaService();
    
    public void salvarNovo(LojaVO vo) throws Exception {
        lojaService.salvaNovo(vo);
    }
}
