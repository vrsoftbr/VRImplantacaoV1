package vrimplantacao2_5.controller.cadastro.sistemabancodados;

import java.util.List;
import vrimplantacao2_5.gui.cadastro.sistemabancodados.ConsultaSistemaBancoDadosGUI;
import vrimplantacao2_5.service.cadastro.bancodados.BancoDadosService;
import vrimplantacao2_5.service.cadastro.sistema.SistemaService;
import vrimplantacao2_5.service.cadastro.sistemabancodados.SistemaBancoDadosService;
import vrimplantacao2_5.vo.cadastro.BancoDadosVO;
import vrimplantacao2_5.vo.cadastro.SistemaBancoDadosVO;
import vrimplantacao2_5.vo.cadastro.SistemaVO;

/**
 *
 * @author Desenvolvimento
 */
public class SistemaBancoDadosController {

    private final SistemaBancoDadosService sistemaBanbcoDadosService = new SistemaBancoDadosService();
    private final BancoDadosService bancoDadosService = new BancoDadosService();
    private final SistemaService sistemaService = new SistemaService();
    private ConsultaSistemaBancoDadosGUI consultaSistemaBancoDadosGUI;
    private List<SistemaBancoDadosVO> sistemaBancoDadosVO = null;
    private List<BancoDadosVO> bancoDadosVO = null;
    private List<SistemaVO> sistemaVO = null;
    
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
    
    public List<BancoDadosVO> getBancoDados() throws Exception {
        return this.bancoDadosVO = bancoDadosService.getBancoDados();
    }
    
    public List<SistemaVO> getSistemas() throws Exception {
        return this.sistemaVO = sistemaService.getSistema();
    }
    
    public void inserir(SistemaBancoDadosVO vo) throws Exception {
        sistemaBanbcoDadosService.inserir(vo);
    }
    
    public void alterar(SistemaBancoDadosVO vo) throws Exception {
        sistemaBanbcoDadosService.alterar(vo);
    }
}
