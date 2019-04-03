/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.cadastro.receita;

import java.util.Map;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao2.dao.cadastro.produto.ProdutoAnteriorDAO;
import vrimplantacao2.utils.collection.IDStack;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.receita.ReceitaAnteriorVO;
import vrimplantacao2.vo.cadastro.receita.ReceitaItemVO;
import vrimplantacao2.vo.cadastro.receita.ReceitaProdutoVO;
import vrimplantacao2.vo.cadastro.receita.ReceitaVO;

/**
 *
 * @author lucasrafael
 */
public class ReceitaRepositoryProvider {

    private final String sistema;
    private final String loja;
    private final int lojaVR;
    private final ProdutoAnteriorDAO produtoDAO;
    private final ReceitaAnteriorDAO anteriorDAO;
    private final ReceitaDAO receitaDAO;

    public ReceitaRepositoryProvider(String sistema, String loja, int lojaVR) throws Exception {
        this.sistema = sistema;
        this.loja = loja;
        this.lojaVR = lojaVR;
        this.produtoDAO = new ProdutoAnteriorDAO();
        this.anteriorDAO = new ReceitaAnteriorDAO();
        this.receitaDAO = new ReceitaDAO();
    }

    public ReceitaRepositoryProvider() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
    
    public void setMessage(String message) throws Exception {
        ProgressBar.setStatus(message);
    }
    
    public void setMessage(String message, int size) throws Exception {
        setMessage(message);
        ProgressBar.setMaximum(size);
    }
    
    public void setMessage() throws Exception {
        ProgressBar.next();
    }

    public Map<String, Integer> getProdutos() throws Exception {
        return produtoDAO.getAnteriores(sistema, loja);
    }

    public Map<String, ReceitaAnteriorVO> getAnteriores() throws Exception {
        return anteriorDAO.getAnteriores(sistema, loja);
    }
    
    public IDStack getIdsVagos() throws Exception {
        return receitaDAO.getIdsVagos(999999);
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
    
    public void gravar(ReceitaVO vo) throws Exception {
        receitaDAO.gravar(vo);
    }
    
    public void gravar(ReceitaAnteriorVO vo) throws Exception {
        anteriorDAO.gravar(vo);
    }
    
    public void gravarItem(ReceitaItemVO vo) throws Exception {
        receitaDAO.gravarItem(vo);
    }
    
    public void gravarProduto(ReceitaProdutoVO vo) throws Exception {
        receitaDAO.gravarProduto(vo);
    }
    
    public MultiMap<Integer, Void> getReceitas() throws Exception {
        return receitaDAO.getReceitas(lojaVR);
    }
    
}
