package vrimplantacao2_5.controller.cadastro.unidade;

import java.util.List;
import vrimplantacao2_5.gui.cadastro.unidade.ConsultaUnidadeGUI;
import vrimplantacao2_5.service.cadastro.unidade.UnidadeService;
import vrimplantacao2_5.vo.cadastro.UnidadeVO;

/**
 *
 * @author Desenvolvimento
 */
public class UnidadeController {

    private final UnidadeService unidadeService = new UnidadeService();
    private ConsultaUnidadeGUI consultaUnidadeGUI = null;
    private List<UnidadeVO> unidadeVO = null;
    
    public UnidadeController() {
        
    }
    
    public UnidadeController(ConsultaUnidadeGUI consultaUnidadeGUI) {
        this.consultaUnidadeGUI = consultaUnidadeGUI;
    }
    
    public void inserir(UnidadeVO vo) throws Exception {
        this.unidadeService.inserir(vo);
    }
    
    public void alterar(UnidadeVO vo) throws Exception {
        this.unidadeService.alterar(vo);
    }
    
    public void consultar(UnidadeVO vo) throws Exception {
        this.unidadeVO = unidadeService.consultar(vo);
        consultaUnidadeGUI.consultar();
    }
    
    public List<UnidadeVO> getUnidade() {
        return unidadeVO;
    }
}
