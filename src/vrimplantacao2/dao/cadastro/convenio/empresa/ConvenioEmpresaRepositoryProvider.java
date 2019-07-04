package vrimplantacao2.dao.cadastro.convenio.empresa;

import java.util.Map;
import java.util.Set;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao2.dao.cadastro.local.MunicipioDAO;
import vrimplantacao2.parametro.Parametros;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.convenio.empresa.ConvenioEmpresaAnteriorVO;
import vrimplantacao2.vo.cadastro.convenio.empresa.ConvenioEmpresaVO;
import vrimplantacao2.vo.cadastro.local.MunicipioVO;

/**
 *
 * @author Leandro
 */
public class ConvenioEmpresaRepositoryProvider {
    private final String sistema;
    private final String lojaOrigem;
    private final int lojaVR;
    private EmpresaDAO empresaDAO;
    private EmpresaAnteriorDAO empresaAnteriorDAO;
    private MunicipioDAO municipioDAO;

    public ConvenioEmpresaRepositoryProvider(String sistema, String lojaOrigem, int lojaVR) throws Exception {
        this.sistema = sistema;
        this.lojaOrigem = lojaOrigem;
        this.lojaVR = lojaVR;
        this.empresaDAO = new EmpresaDAO();
        this.empresaAnteriorDAO = new EmpresaAnteriorDAO();
        this.empresaAnteriorDAO.createTable();
        this.municipioDAO = new MunicipioDAO();
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

    public void setMaximum(int size) throws Exception {
        ProgressBar.setMaximum(size);
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

    public void next() throws Exception {
        ProgressBar.next();
    }

    public Set<Long> getCnpjExistentes() throws Exception {
        return empresaDAO.getCnpjExistentes();
    }

    public ConvenioEmpresaIDStack getIds() throws Exception {
        return new ConvenioEmpresaIDStack();
    }

    public Map<String, ConvenioEmpresaAnteriorVO> getAnteriores() throws Exception {
        return empresaAnteriorDAO.getAnteriores(getSistema(), getLojaOrigem());
    }

    public MunicipioVO getMunicipio(int ibgeMunicipio) throws Exception {
        return municipioDAO.getMunicipio(ibgeMunicipio);
    }
    
    public MunicipioVO getMunicipio(String municipio, String uf) throws Exception {
        return municipioDAO.getMunicipio(municipio, uf);
    }

    public MunicipioVO getMunicipioPadrao() {
        return Parametros.get().getMunicipioPadrao2();
    }

    public void gravarEmpresa(ConvenioEmpresaVO vo) throws Exception {
        empresaDAO.gravarEmpresa(vo);
    }

    public void gravarEmpresaAnterior(ConvenioEmpresaAnteriorVO anterior) throws Exception {
        empresaAnteriorDAO.gravarEmpresaAnterior(anterior);
    }
    
}
