package vrimplantacao2.dao.cadastro.financeiro.recebercaixa;

import java.util.Map;
import java.util.Set;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao2.vo.cadastro.financeiro.recebimentocaixa.RecebimentoCaixaAnteriorVO;
import vrimplantacao2.vo.cadastro.financeiro.recebimentocaixa.RecebimentoCaixaVO;

/**
 *
 * @author Leandro
 */
public class ReceberCaixaRepositoryProvider {
    private final String sistema;
    private final String agrupador;
    private final int lojaVR;
    private ReceberCaixaDAO dao = new ReceberCaixaDAO();
    private ReceberCaixaAnteriorDAO anteriorDao;

    public ReceberCaixaRepositoryProvider(String sistema, String agrupador, int lojaVR) throws Exception {
        this.anteriorDao = new ReceberCaixaAnteriorDAO();
        this.sistema = sistema;
        this.agrupador = agrupador;
        this.lojaVR = lojaVR;        
    }

    public String getSistema() {
        return sistema;
    }

    public String getAgrupador() {
        return agrupador;
    }

    public int getLojaVR() {
        return lojaVR;
    }

    public Map<String, RecebimentoCaixaAnteriorVO> getAnteriores() throws Exception {
        return anteriorDao.getAnteriores(getSistema(), getAgrupador());
    }

    public Map<String, Integer> getMapaTipoRecebivel() throws Exception {
        return dao.getMapaTipoRecebivel(getSistema(), getAgrupador());
    }

    public void notificar() throws Exception {
        ProgressBar.next();
    }
    
    public void notificar(String mensagem) throws Exception {
        ProgressBar.setStatus(mensagem);
    }

    public void notificar(String mensagem, int size) throws Exception {
        notificar(mensagem);
        ProgressBar.setMaximum(size);
    }

    public void gravarRecebimentoCaixa(RecebimentoCaixaVO vo) throws Exception {
        dao.gravarRecebimentoCaixa(vo);
    }

    public void gravarRecebimentoCaixaAnterior(RecebimentoCaixaAnteriorVO anterior) throws Exception {
        anteriorDao.gravarRecebimentoCaixaAnterior(anterior);
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

    public void atualizaRecebimentoCaixaAnterior(RecebimentoCaixaAnteriorVO anterior) throws Exception {
        anteriorDao.atualizaRecebimentoCaixaAnterior(anterior);
    }

    public void atualizarRecebimentoCaixa(RecebimentoCaixaVO vo, Set<OpcaoRecebimentoCaixa> opt) throws Exception {
        dao.atualizarRecebimentoCaixa(vo, opt);
    }


}
