package vrimplantacao2_5.controller.cadastro.sistema;

import java.util.List;
import vrimplantacao2_5.gui.cadastro.sistema.ConsultaSistemaGUI;
import vrimplantacao2_5.service.cadastro.sistema.SistemaService;
import vrimplantacao2_5.vo.cadastro.SistemaVO;

/**
 *
 * @author Desenvolvimento
 */
public class SistemaController {

    private final SistemaService sistemaService = new SistemaService();
    private ConsultaSistemaGUI consultaSistemaGUI = null;
    private List<SistemaVO> sistema = null;
    
    public SistemaController() {
        
    }
    
    public SistemaController(ConsultaSistemaGUI consultaSistemaGUI) {
        this.consultaSistemaGUI = consultaSistemaGUI;
    }
    
    public void inserir(SistemaVO vo) throws Exception {
        sistemaService.inserir(vo);
    }
    
    public void alterar(SistemaVO vo) throws Exception {
        sistemaService.alterar(vo);
    }
    
    public void consultar(String nome) throws Exception {
        this.sistema = sistemaService.consultar(nome);
        consultaSistemaGUI.consultar();
    }

    public List<SistemaVO> getSistema() {
        return sistema;
    }
    
    public List<SistemaVO> getSistema(int idSistema) throws Exception {
        return sistemaService.getSistema(idSistema);
    }

    public int getProximoId() throws Exception {
        return sistemaService.getProximoId();
    }
}
