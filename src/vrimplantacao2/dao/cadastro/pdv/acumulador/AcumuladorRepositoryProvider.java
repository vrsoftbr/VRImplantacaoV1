/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.cadastro.pdv.acumulador;

import vrframework.classe.Conexao;
import vrimplantacao2.vo.cadastro.pdv.acumulador.AcumuladorVO;

/**
 *
 * @author lucasrafael
 */
public class AcumuladorRepositoryProvider {

    private AcumuladorDAO acumuladorDAO;
    
    public AcumuladorRepositoryProvider() throws Exception {
        this.acumuladorDAO = new AcumuladorDAO();
    }
    
    public void salvar(AcumuladorVO vo) throws Exception {
        acumuladorDAO.salvar(vo);
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
}
