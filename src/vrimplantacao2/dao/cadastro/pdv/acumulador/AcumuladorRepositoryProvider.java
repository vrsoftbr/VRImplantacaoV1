/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.cadastro.pdv.acumulador;

import vrframework.classe.Conexao;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.pdv.acumulador.AcumuladorLayoutRetornoVO;
import vrimplantacao2.vo.cadastro.pdv.acumulador.AcumuladorLayoutVO;
import vrimplantacao2.vo.cadastro.pdv.acumulador.AcumuladorVO;

/**
 *
 * @author lucasrafael
 */
public class AcumuladorRepositoryProvider {

    private AcumuladorDAO acumuladorDAO;
    private AcumuladorLayoutDAO acumuladorLayoutDAO;
    private AcumuladorLayoutRetornoDAO acumuladorLayoutRetornoDAO;
    private int lojaVR;

    public AcumuladorRepositoryProvider() throws Exception {
        acumuladorDAO = new AcumuladorDAO();
        acumuladorLayoutDAO = new AcumuladorLayoutDAO();
        acumuladorLayoutRetornoDAO = new AcumuladorLayoutRetornoDAO();        
    }

    public void delete() throws Exception {
        acumuladorDAO.delete();
    }
    
    public void salvar(AcumuladorVO vo) throws Exception {
        acumuladorDAO.salvar(vo);
    }

    public void salvar(AcumuladorLayoutVO vo) throws Exception {
        acumuladorLayoutDAO.salvar(vo);
    }

    public void salvar(AcumuladorLayoutRetornoVO vo) throws Exception {
        acumuladorLayoutRetornoDAO.salvar(vo);
    }
    
    public MultiMap<Integer, AcumuladorVO> getAcumuladores() throws Exception {
        return acumuladorDAO.getAcumuladores();
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

    public int getLojaVR() {
        return lojaVR;
    }

    public void setLojaVR(int lojaVR) {
        this.lojaVR = lojaVR;
    }
}
