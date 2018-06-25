package vrimplantacao2.dao.cadastro.produto2.associado;

import java.util.Map;
import java.util.Set;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao2.vo.cadastro.associado.AssociadoItemVO;
import vrimplantacao2.vo.cadastro.associado.AssociadoVO;

/**
 *
 * @author Leandro
 */
public class AssociadoRepositoryProvider {
    
    private final String sistema;
    private final String loja;
    private final int lojaVR;

    public AssociadoRepositoryProvider(String sistema, String loja, int lojaVR) {
        this.sistema = sistema;
        this.loja = loja;
        this.lojaVR = lojaVR;
    }

    public String getSistema() {
        return sistema;
    }

    public String getLoja() {
        return loja;
    }

    public int getLojaVR() {
        return lojaVR;
    }

    public void begin() throws Exception {
        Conexao.begin();
    }

    public void setStatus(String mensagem) throws Exception {
        ProgressBar.setStatus(mensagem);
    }

    public void setStatus(String mensagem, int size) throws Exception {
        setStatus(mensagem);
        ProgressBar.setMaximum(size);
    }
    
    public void setStatus() throws Exception {
        ProgressBar.next();
    }

    public void commit() throws Exception {
        Conexao.commit();
    }

    public void rollback() throws Exception {
        Conexao.rollback();
    }

    Map<String, Integer> getProdutosAnteriores() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    Map<Integer, AssociadoVO> getAssociadosExistentes() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    void gravar(AssociadoVO vo) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    void atualizar(AssociadoVO vo, Set<OpcaoAssociado> opt) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    void gravar(AssociadoItemVO vItem) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
