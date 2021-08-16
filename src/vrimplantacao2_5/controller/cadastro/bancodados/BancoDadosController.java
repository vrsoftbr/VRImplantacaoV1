package vrimplantacao2_5.controller.cadastro.bancodados;

import java.util.List;
import vrimplantacao2_5.gui.cadastro.bancodados.ConsultaBancoDadosGUI;
import vrimplantacao2_5.service.cadastro.bancodados.BancoDadosService;
import vrimplantacao2_5.vo.cadastro.BancoDadosVO;

/**
 *
 * @author Desenvolvimento
 */
public class BancoDadosController {
    
    private final BancoDadosService bancoDadosService = new BancoDadosService();
    private ConsultaBancoDadosGUI consultaBancoDadosGUI = null;
    private List<BancoDadosVO> bancoDados = null;
    
    public BancoDadosController() {
        
    }
    
    public BancoDadosController(ConsultaBancoDadosGUI consultaBancoDadosGUI) {
        this.consultaBancoDadosGUI = consultaBancoDadosGUI;
    }
    
    public void salvar(BancoDadosVO vo) throws Exception {
        bancoDadosService.salvar(vo);
    }
    
    public void alterar(BancoDadosVO vo) throws Exception {
        bancoDadosService.alterar(vo);
    }
    
    public void consultar(String nome) throws Exception {
        this.bancoDados = bancoDadosService.consultar(nome);
        consultaBancoDadosGUI.consultar();
    }
    
    public List<BancoDadosVO> getBancoDados() {
        return bancoDados;
    }
}
