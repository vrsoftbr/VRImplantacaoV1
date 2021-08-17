package vrimplantacao2_5.controller.cadastro.sistemabancodados;

import java.util.List;
import vrimplantacao2_5.gui.cadastro.sistemabancodados.ConsultaSistemaBancoDadosGUI;
import vrimplantacao2_5.service.cadastro.sistemabancodados.SistemaBancoDadosService;
import vrimplantacao2_5.vo.cadastro.SistemaBancoDadosVO;

/**
 *
 * @author Desenvolvimento
 */
public class SistemaBancoDadosController {

    private final SistemaBancoDadosService sistemaBanbcoDadosService = new SistemaBancoDadosService();
    private ConsultaSistemaBancoDadosGUI consultaSistemaBancoDadosGUI;
    private List<SistemaBancoDadosVO> sistemaBancoDadosVO = null;
    
    public SistemaBancoDadosController() {
        
    }
    
    public SistemaBancoDadosController(ConsultaSistemaBancoDadosGUI consultaSistemaBancoDadosGUI) {
        this.consultaSistemaBancoDadosGUI = consultaSistemaBancoDadosGUI;
    }
    
    public void consultar(SistemaBancoDadosVO vo) throws Exception {
        this.sistemaBancoDadosVO = sistemaBanbcoDadosService.consultar(vo);
        consultaSistemaBancoDadosGUI.consultar();
    }
    
    public List<SistemaBancoDadosVO> getSistemaBancoDados() {
        return this.sistemaBancoDadosVO;
    }    
}
