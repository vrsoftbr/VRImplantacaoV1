package vrimplantacao2.dao.cadastro.convenio.receber;

import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao2.dao.cadastro.convenio.conveniado.ConveniadoAnteriorDAO;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.convenio.conveniado.ConveniadoAnteriorVO;
import vrimplantacao2.vo.cadastro.convenio.transacao.ConvenioTransacaoAnteriorVO;
import vrimplantacao2.vo.cadastro.convenio.transacao.ConvenioTransacaoVO;

/**
 *
 * @author Leandro
 */
public class ConvenioReceberRepositoryProvider {
    private final String sistema;
    private final String lojaOrigem;
    private final int lojaVR;
    private ConvenioTransacaoDAO convenioTransacaoDAO;
    private ConveniadoAnteriorDAO conveniadosDAO;
    private ConvenioTransacaoAnteriorDAO convenioTransacaoAnteriorDAO;

    public ConvenioReceberRepositoryProvider(String sistema, String lojaOrigem, int lojaVR) throws Exception {
        this.sistema = sistema;
        this.lojaOrigem = lojaOrigem;
        this.lojaVR = lojaVR;
        this.convenioTransacaoDAO = new ConvenioTransacaoDAO();
        this.conveniadosDAO = new ConveniadoAnteriorDAO();
        this.convenioTransacaoAnteriorDAO = new ConvenioTransacaoAnteriorDAO();
        this.convenioTransacaoAnteriorDAO.createTable();
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

    public MultiMap<String, ConvenioTransacaoAnteriorVO> getAnteriores() throws Exception {
        return convenioTransacaoAnteriorDAO.getAnteriores(getSistema(), getLojaOrigem());
    }

    public MultiMap<String, ConveniadoAnteriorVO> getConveniados() throws Exception {
        return conveniadosDAO.getAnteriores(getSistema(), getLojaOrigem());
    }

    public void next() throws Exception {
        ProgressBar.next();
    }

    public void setMaximum(int size) throws Exception {
        ProgressBar.setMaximum(size);
    }

    public void gravarTransacao(ConvenioTransacaoVO vo) throws Exception {
        convenioTransacaoDAO.gravarTransacao(vo);
    }

    public void gravarTransacaoAnterior(ConvenioTransacaoAnteriorVO anterior) throws Exception {
        convenioTransacaoAnteriorDAO.gravarTransacaoAnterior(anterior);
    }
    
}
