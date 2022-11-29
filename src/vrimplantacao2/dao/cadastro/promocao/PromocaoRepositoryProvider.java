/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.cadastro.promocao;

import java.util.List;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao2.dao.cadastro.promocao.PromocaoDAO;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.pdv.promocao.PromocaoAnteriorVO;
import vrimplantacao2.vo.cadastro.pdv.promocao.PromocaoVO;
import vrimplantacao2.vo.importacao.PromocaoIMP;

/**
 *
 * @author Michael
 */
public class PromocaoRepositoryProvider {
   
    private final String sistema;
    private final String lojaOrigem;
    private final int lojaVR;
    private PromocaoDAO promocaoDAO;
    private PromocaoAnteriorDAO promocaoAnteriorDAO;

    public PromocaoRepositoryProvider(String sistema, String lojaOrigem, int lojaVR) throws Exception {
        this.sistema = sistema;        
        this.lojaOrigem = lojaOrigem;
        this.lojaVR = lojaVR;
        this.promocaoDAO = new PromocaoDAO();
        this.promocaoAnteriorDAO = new PromocaoAnteriorDAO();
        this.promocaoAnteriorDAO.createTable();        
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
        return promocaoAnteriorDAO.getAnteriores(getSistema(), getLojaOrigem());
    }

    List<PromocaoIMP> getFinalizadora() throws Exception {
        return promocaoDAO.getFinalizadora();
    }

    List<PromocaoIMP> getItens() throws Exception {
        return promocaoDAO.getPromocaoItens();
    }

    void gravarPromocao(PromocaoVO promo) throws Exception {
        promocaoDAO.salvar(promo);
    }

    void gravarPromocaoAnterior(PromocaoAnteriorVO anterior) throws Exception {
        promocaoAnteriorDAO.salvar(anterior);
    }

    void getPromocaoItens() throws Exception {
        List<PromocaoIMP> promocaoItens = promocaoDAO.getPromocaoItens();
    }

    void gravarPromocaoItens(PromocaoAnteriorVO anterior) throws Exception {
        promocaoDAO.salvarPromocaoItens(anterior);
    }

    void gravarPromocaoFinalizadora(PromocaoAnteriorVO finalizadora) throws Exception {
         promocaoDAO.salvarFinalizadora(finalizadora);
    }

    void limparCodantPromocao(String lojaOrigem, String sistema) throws Exception {
        promocaoDAO.limparCodantPromocao(lojaOrigem, sistema);
    }

    void limparPromocao(String lojaOrigem, String sistema) throws Exception {
        promocaoDAO.limparPromocao(lojaOrigem, sistema);
    }
}