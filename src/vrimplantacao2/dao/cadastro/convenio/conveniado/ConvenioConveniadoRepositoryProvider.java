package vrimplantacao2.dao.cadastro.convenio.conveniado;

import java.util.Map;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao2.dao.cadastro.convenio.empresa.EmpresaAnteriorDAO;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.convenio.conveniado.ConveniadoAnteriorVO;
import vrimplantacao2.vo.cadastro.convenio.conveniado.ConveniadoServicoVO;
import vrimplantacao2.vo.cadastro.convenio.conveniado.ConveniadoVO;
import vrimplantacao2.vo.cadastro.convenio.empresa.ConvenioEmpresaAnteriorVO;

/**
 *
 * @author Leandro
 */
public class ConvenioConveniadoRepositoryProvider {
    private final String sistema;
    private final String lojaOrigem;
    private final int lojaVR;
    private EmpresaAnteriorDAO empresaDAO;
    private ConveniadoDAO conveniadoDAO;
    private ConveniadoAnteriorDAO conveniadoAnteriorDAO;
    private ConveniadoServicoDAO conveniadoServicoDAO;

    public ConvenioConveniadoRepositoryProvider(String sistema, String lojaOrigem, int lojaVR) throws Exception {        
        this.sistema = sistema;
        this.lojaOrigem = lojaOrigem;
        this.lojaVR = lojaVR;
        this.empresaDAO = new EmpresaAnteriorDAO();
        this.conveniadoDAO = new ConveniadoDAO();
        this.conveniadoAnteriorDAO = new ConveniadoAnteriorDAO();
        this.conveniadoAnteriorDAO.createTable();
        this.conveniadoServicoDAO = new ConveniadoServicoDAO();
    }

    public String getSistema() {
        return sistema;
    }

    public String getLojaOrigem() {
        return lojaOrigem;
    }

    public int getLojaVR() {
        return lojaVR;
    }

    public void setStatus(String mensagem) throws Exception {
        ProgressBar.setStatus(mensagem);
    }

    public void begin() throws Exception {
        Conexao.begin();
    }

    public void commit() throws Exception {
        Conexao.commit();
    }

    public void rollback() throws Exception {
        Conexao.rollback();
    }

    public MultiMap<Long, Integer> getCnpjCadastrado() throws Exception {
        return conveniadoDAO.getCnpjCadastrado();
    }

    public ConveniadoIDStack getIds() throws Exception {
        return new ConveniadoIDStack();
    }

    public Map<String, ConveniadoAnteriorVO> getAnteriores() throws Exception {
        return conveniadoAnteriorDAO.getAnteriores(getSistema(), getLojaOrigem());
    }

    public void setMaximum(int size) throws Exception {
        ProgressBar.setMaximum(size);
    }

    public void next() throws Exception {
        ProgressBar.next();
    }

    public Map<String, ConvenioEmpresaAnteriorVO> getEmpresas() throws Exception {
        return empresaDAO.getAnteriores(sistema, lojaOrigem, true);
    }

    public void gravarConveniado(ConveniadoVO vo) throws Exception {
        conveniadoDAO.gravarConveniado(vo);
    }

    public void gravarConveniadoAnterior(ConveniadoAnteriorVO ant) throws Exception {
        conveniadoAnteriorDAO.gravarConveniadoAnterior(ant);
    }

    public void gravarConveniadoServico(ConveniadoServicoVO servico) throws Exception {
        conveniadoServicoDAO.gravarConveniadoServico(servico);
    }
    
}
