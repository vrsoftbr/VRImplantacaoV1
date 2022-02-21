/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2_5.service.migracao;

import vrimplantacao2.dao.cadastro.cliente.ClienteEventualAnteriorDAO;

/**
 *
 * @author Lucas Santos
 */
public class ClienteEventualService {
    
    private ClienteEventualAnteriorDAO clienteAnteriorDAO;
    
    public ClienteEventualService() {
        this.clienteAnteriorDAO = new ClienteEventualAnteriorDAO();
    }
    
    public ClienteEventualService(ClienteEventualAnteriorDAO clienteAnteriorDAO) {
        this.clienteAnteriorDAO = clienteAnteriorDAO;
    }
    
    public int existeConexaoMigrada(int idConexao, String sistema) throws Exception {
        return this.clienteAnteriorDAO.getConexaoMigrada(idConexao, sistema);
    }

    public int verificaRegistro() throws Exception {
        return this.clienteAnteriorDAO.verificaRegistro();
    }
    
    public boolean verificaMigracaoMultiloja(String lojaOrigem, String sistema, int idConexao) throws Exception {
        return this.clienteAnteriorDAO.verificaMigracaoMultiloja(lojaOrigem, sistema, idConexao);
    }
    
    public void copiarCodantClienteEventual(String sistema, String lojaModelo, String lojaNova) throws Exception {
        this.clienteAnteriorDAO.copiarCodantClienteEventual(sistema, lojaModelo, lojaNova);
    }
    
    public String getLojaModelo(int idConexao, String sistema) throws Exception {
        return this.clienteAnteriorDAO.getLojaModelo(idConexao, sistema);
    }
    
    public boolean verificaMultilojaMigrada(String lojaOrigem, String sistema, int idConexao) throws Exception {
        return this.clienteAnteriorDAO.verificaMultilojaMigrada(lojaOrigem, sistema, idConexao);
    }
    
     public String getImpSistemaInicial() throws Exception {
        return this.clienteAnteriorDAO.getImpSistema();
    }
}
