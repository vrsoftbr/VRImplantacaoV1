package vrimplantacao2.dao.cadastro.promocao;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao2.dao.cadastro.produto2.ProdutoDAO;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.pdv.promocao.PromocaoAnteriorVO;
import vrimplantacao2.vo.cadastro.pdv.promocao.PromocaoVO;
import vrimplantacao2.vo.importacao.PromocaoIMP;

/**
 *
 * @author Leandro
 */
public class PromocaoRepositoryProvider {
    
    private final String sistema;
    private final String lojaOrigem;
    private final int lojaVR;
    private PromocaoDAO promocaoDAO;
    private PromocaoAnteriorDAO anteriorDAO;
    private int idConexao = 0;

    public PromocaoRepositoryProvider(String sistema, String lojaOrigem, int lojaVR, int idConexao) throws Exception {
        this.sistema = sistema;        
        this.lojaOrigem = lojaOrigem;
        this.lojaVR = lojaVR;
        this.promocaoDAO = new PromocaoDAO();
        this.anteriorDAO = new PromocaoAnteriorDAO();
        this.anteriorDAO.createTable();        
    }

    public void setIdConexao(int idConexao) {
        this.idConexao = idConexao;
    }
    
    public int getIdConexao() {
        return idConexao;
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

    public void setMaximo(int size) throws Exception {
        ProgressBar.setMaximum(size);
    }

    public void setStatus(String mensagem) throws Exception {
        ProgressBar.setStatus(mensagem);
    }

    public MultiMap<String, PromocaoAnteriorVO> getAnteriores() throws Exception {
        return anteriorDAO.getAnteriores(getSistema(), getLojaOrigem());
    }

    public void gravarPromocao(PromocaoVO promo) throws Exception {
        promocaoDAO.salvar(promo);
    }

    public void gravarPromocaoAnterior(PromocaoAnteriorVO anterior) throws Exception {
        anteriorDAO.salvar(anterior);
    }
    
    void getPromocaoItens() throws Exception {
        promocaoDAO.getPromocaoItens();
    }

    void gravarPromocaoItens(PromocaoAnteriorVO anterior) throws Exception {
        promocaoDAO.salvarPromocaoItens(anterior);
    }

    List<PromocaoIMP> getItens() throws Exception {
        List<PromocaoIMP> itens = promocaoDAO.getPromocaoItens();
        return itens;
    }

    List<PromocaoIMP> getFinalizadora() throws Exception {
        List<PromocaoIMP> finalizadora = promocaoDAO.getFinalizadora();
        return finalizadora;
    }

    void gravarPromocaoFinalizadora(PromocaoAnteriorVO finalizadora) throws Exception {
       promocaoDAO.salvarFinalizadora(finalizadora);
    }
}