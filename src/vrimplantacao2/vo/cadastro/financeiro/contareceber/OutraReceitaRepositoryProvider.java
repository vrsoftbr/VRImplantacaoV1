package vrimplantacao2.vo.cadastro.financeiro.contareceber;

import java.util.Map;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;

/**
 * 
 * @author Leandro
 */
public class OutraReceitaRepositoryProvider {
    
    private final String sistema;
    private final String loja;
    private final int lojaVR;

    public OutraReceitaRepositoryProvider(String sistema, String loja, int lojaVR) throws Exception {
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

    public void setStatus() throws Exception {
        ProgressBar.next();
    }

    public void setStatus(String message) throws Exception {
        ProgressBar.setStatus(message);
    }
    
    public void setStatus(String message, int size) throws Exception {
        ProgressBar.setStatus(message);
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

    Map<String, Integer> getFornecedores() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    Map<String, ContaReceberAnteriorVO> getAnteriores() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    void gravar(OutraReceitaVO vo) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    void gravar(ContaReceberAnteriorVO anterior) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
