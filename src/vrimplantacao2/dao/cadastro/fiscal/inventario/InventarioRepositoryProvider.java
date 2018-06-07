/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.cadastro.fiscal.inventario;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import vrframework.classe.Conexao;
import vrimplantacao.vo.vrimplantacao.EstadoVO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributacaoDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoVO;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.fiscal.inventario.InventarioVO;
import vrimplantacao2.vo.cadastro.fiscal.inventario.InventarioVOIMP;
import vrimplantacao2.vo.enums.Icms;

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
    private Tributo tributo = new Tributo();
    
    public class Tributo {
        
        private MapaTributacaoDAO mapaDao = new MapaTributacaoDAO();
        
        private EstadoVO ufLoja = null;
        public EstadoVO getUf(int idVrLoja) throws Exception {
            if ( ufLoja == null ) {
                try (Statement stm = Conexao.createStatement()) {
                    try (ResultSet rst = stm.executeQuery(
                            "select \n" +
                            "	e.id,\n" +
                            "	e.sigla,\n" +
                            "	e.descricao\n" +
                            "from \n" +
                            "	loja l \n" +
                            "	join fornecedor f on \n" +
                            "		l.id_fornecedor = f.id \n" +
                            "	join estado e on\n" +
                            "		f.id_estado = e.id\n" +
                            "where l.id = " + idVrLoja
                    )) {
                        if (rst.next()) {
                            EstadoVO uf = new EstadoVO();
                            uf.setId(rst.getInt("id"));
                            uf.setSigla(rst.getString("sigla"));
                            uf.setDescricao(rst.getString("descricao"));
                            ufLoja = uf;                            
                        }
                    }
                }
            }
            
            return ufLoja;
        }
        
        private Map<String, Icms> icms;
        public Icms getAliquotaByMapaId(String icmsId) throws Exception {
            if (icms == null) {
                icms = new HashMap<>();
                for (MapaTributoVO vo: mapaDao.getMapa(getSistema(), getLojaOrigem())) {
                    if (vo.getAliquota() != null) {
                        icms.put(vo.getOrigId(), vo.getAliquota());
                    }
                }
            }
            
            Icms icm = icms.get(icmsId);
            if (icm == null) {
                icm = Icms.getIsento();
            }
            
            return icm;
        }
        
    }
    
    public Tributo tributo() {
        return tributo;
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

    public MultiMap<String, InventarioVOIMP> getInventarios() throws Exception {
        return inventarioDAO.getInventario(this.lojaVR, this.getIdProduto(), this.data);
    }
    
    public void salvar(InventarioVO vo) throws Exception {
        inventarioDAO.salvar(vo);
    }

    public void atualizar(InventarioVO vo) throws Exception {
        inventarioDAO.atualizar(vo);
    }
    
    public int getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(int idProduto) {
        this.idProduto = idProduto;
    }
    
}
