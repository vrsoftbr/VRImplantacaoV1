/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.cadastro.pdv.operador;

import vrframework.classe.Conexao;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.pdv.operador.OperadorVO;

/**
 *
 * @author lucasrafael
 */
public class OperadorRepositoryProvider {

    private int lojaVR;
    private OperadorDAO operadorDAO;

    public OperadorRepositoryProvider(int lojaVR) throws Exception {
        this.lojaVR = lojaVR;
        this.operadorDAO = new OperadorDAO();
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
    
    public MultiMap<String, OperadorVO> getOperadores() throws Exception {
        return operadorDAO.getOperadores(this.lojaVR);
    }
    
    public void salvar(OperadorVO operador) throws Exception {
        operadorDAO.salvar(operador);
    }
}