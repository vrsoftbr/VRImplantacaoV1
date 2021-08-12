package vrimplantacao2_5.controller.cadastro;

import java.util.List;
import vrimplantacao2_5.gui.cadastro.ConsultaBancoDadosGUI;
import vrimplantacao2_5.service.cadastro.BancoDadosService;
import vrimplantacao2_5.vo.cadastro.BancoDadosVO;

/**
 *
 * @author Desenvolvimento
 */
public class BancoDadosController {
    
    private final BancoDadosService bancoDadosService = new BancoDadosService();
    private ConsultaBancoDadosGUI consultaBancoDadosGUI;
    private List<BancoDadosVO> bancoDados = null;
    
    public BancoDadosController() {
        
    }
    
    public BancoDadosController(ConsultaBancoDadosGUI consultaBancoDadosGUI) {
        this.consultaBancoDadosGUI = consultaBancoDadosGUI;
    }
    
    public void salvar(String nome) throws Exception {
        bancoDadosService.salvar(nome);
    }
    
    public void consultar() throws Exception {
        bancoDadosService.consultar();
        consultaBancoDadosGUI.consultar();
    }
    
    public List<BancoDadosVO> getBancoDados() {
        return bancoDados;
    }
}
