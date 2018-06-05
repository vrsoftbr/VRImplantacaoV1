/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.cadastro.fiscal.inventario;

import vrframework.classe.Conexao;
import vrimplantacao2.vo.cadastro.fiscal.inventario.InventarioVO;

/**
 *
 * @author lucasrafael
 */
public class InventarioRepositoryProvider {

    private String sistema;
    private String lojaOrigem;
    private int lojaVR;
    private InventarioDAO inventarioDAO;

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
    
    public void salvar(InventarioVO vo) throws Exception {
        inventarioDAO.salvar(vo);
    }
    
}
