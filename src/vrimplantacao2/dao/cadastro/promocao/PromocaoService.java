/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.cadastro.promocao;

/**
 *
 * @author Desenvolvimento
 */
public class PromocaoService {
    
    private PromocaoAnteriorDAO promocaoAnteriorDAO;

    public PromocaoService() {
        this.promocaoAnteriorDAO = new PromocaoAnteriorDAO();
    }
    
    public PromocaoService(PromocaoAnteriorDAO promocaoAnteriorDAO) {
        this.promocaoAnteriorDAO = promocaoAnteriorDAO;
    }
    
    public int existeConexaoMigrada(int idConexao, String sistema) throws Exception {
        return this.promocaoAnteriorDAO.getConexaoMigrada(idConexao, sistema);
    }
    
    public int verificaRegistro() throws Exception {
        return this.promocaoAnteriorDAO.verificaRegistro();
    }
    
    public boolean verificaMigracaoMultiloja(String lojaOrigem, String sistema, int idConexao) throws Exception {
        return this.promocaoAnteriorDAO.verificaMigracaoMultiloja(lojaOrigem, sistema, idConexao);
    }
    
    public String getLojaModelo(int idConexao, String sistema) throws Exception {
        return this.promocaoAnteriorDAO.getLojaModelo(idConexao, sistema);
    }
    
    public boolean verificaMultilojaMigrada(String lojaOrigem, String sistema, int idConexao) throws Exception {
        return this.promocaoAnteriorDAO.verificaMultilojaMigrada(lojaOrigem, sistema, idConexao);
    }

}