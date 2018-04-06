/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.cadastro.pdv.operador;

import java.util.Map;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.pdv.operador.OperadorAnteriorVO;
import vrimplantacao2.vo.cadastro.pdv.operador.OperadorVO;

/**
 *
 * @author lucasrafael
 */
public class OperadorRepositoryProvider {

    private String sistema;
    private String lojaOrigem;
    private int lojaVR;
    private OperadorDAO operadorDAO;
    private OperadorAnteriorDAO anterioresDAO;

    public OperadorRepositoryProvider() throws Exception {
        this.operadorDAO = new OperadorDAO();
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
    
    public OperadorIDStack getOperadorIDStack(int iniciarEm) {
        return new OperadorIDStack(iniciarEm);
    }
    
    public MultiMap<String, OperadorAnteriorVO> getAnteriores() throws Exception {
        return anterioresDAO.getAnterior(sistema, lojaOrigem);
    }
    
    public void salvar(OperadorAnteriorVO anterior) throws Exception {
        anterioresDAO.salvar(anterior);
    }
    
    public void salvar(OperadorVO operador) throws Exception {
        operadorDAO.salvar(operador);
    }
}