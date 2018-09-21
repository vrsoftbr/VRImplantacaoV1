/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.cadastro.fiscal.inventario;

import java.util.Date;
import java.util.Map;
import vrframework.classe.Conexao;
import vrimplantacao.dao.cadastro.AliquotaDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributacaoDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoVO;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.fiscal.inventario.InventarioVO;
import vrimplantacao2.vo.cadastro.fiscal.inventario.InventarioAnteriorVO;
import vrimplantacao2.vo.cadastro.fiscal.inventario.ProdutoInventario;

/**
 *
 * @author lucasrafael
 */
public class InventarioRepositoryProvider {

    private String sistema;
    private String lojaOrigem;
    private int lojaVR;
    private int idProduto;
    private Date data;
    private InventarioDAO inventarioDAO;
    private InventarioAnteriorDAO inventarioAnteriorDAO; 
    private MapaTributacaoDAO mapaDao;
    private AliquotaDAO aliquotaDAO;
    
    public InventarioRepositoryProvider(String sistema, String loja, int idLovaVR) throws Exception {
        this.sistema = sistema;
        this.lojaOrigem = loja;
        this.lojaVR = idLovaVR;
        this.mapaDao = new MapaTributacaoDAO();
        this.inventarioDAO = new InventarioDAO();
        this.inventarioAnteriorDAO = new InventarioAnteriorDAO();
        this.aliquotaDAO = new AliquotaDAO();
    }

    public Map<String, ProdutoInventario> getProdutosInventario() throws Exception {
        return inventarioDAO.getProdutosInventario(getSistema(), getLojaOrigem());
    }

    public MultiMap<Comparable, Integer> getAliquotas() throws Exception {
        return aliquotaDAO.getIdAliquotasPorCstAliqReduz();
    }

    public Map<String, MapaTributoVO> getMapaAliquotas() throws Exception {
        return mapaDao.getMapaAsMap(getSistema(), getLojaOrigem());
    }

    public String getSistema() {
        return sistema;
    }

    public void setSistema(String sistema) {
        this.sistema = sistema;
    }

    public String getLojaOrigem() {
        return lojaOrigem;
    }

    public void setLojaOrigem(String lojaOrigem) {
        this.lojaOrigem = lojaOrigem;
    }

    public int getLojaVR() {
        return lojaVR;
    }

    public void setLojaVR(int lojaVR) {
        this.lojaVR = lojaVR;
    }
    
    //<editor-fold defaultstate="collapsed" desc="Operação de banco">
    public void begin() throws Exception {
        Conexao.begin();
    }
    
    public void commit() throws Exception {
        Conexao.commit();
    }
    
    public void rollback() throws Exception {
        Conexao.rollback();
    }
    //</editor-fold>

    public Map<String, InventarioAnteriorVO> getAnteriores() throws Exception {
        return inventarioAnteriorDAO.getAnteriores(getSistema(), getLojaOrigem());
    }
    
    public void salvar(InventarioVO vo) throws Exception {
        inventarioDAO.salvar(vo);
    }

    public void atualizar(InventarioVO vo) throws Exception {
        inventarioDAO.atualizar(vo);
    }
    
    public void salvarAnterior(InventarioAnteriorVO vo) throws Exception {
        inventarioAnteriorDAO.gravar(vo);
    }
    
    public int getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(int idProduto) {
        this.idProduto = idProduto;
    }
    
}
