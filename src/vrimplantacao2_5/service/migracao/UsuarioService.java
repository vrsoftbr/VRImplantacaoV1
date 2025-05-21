package vrimplantacao2_5.service.migracao;

import vrimplantacao2.dao.cadastro.fornecedor.FornecedorAnteriorDAO;
import vrimplantacao2.dao.cadastro.usuario.UsuarioAnteriorDAO;

/**
 *
 * @author Lucas Santos
 */
public class UsuarioService {
    
    private UsuarioAnteriorDAO usuarioAnteriorDAO;
    
//    public UsuarioService() throws Exception {
//        this.fornecedorAnteriorDAO = new FornecedorAnteriorDAO();
//    }
//    
//    public UsuarioService(FornecedorAnteriorDAO fornecedorAnteriorDAO) {
//        this.fornecedorAnteriorDAO = fornecedorAnteriorDAO;
//    }
    
    public int existeConexaoMigrada(int idConexao, String sistema) throws Exception {
        return this.usuarioAnteriorDAO.getConexaoMigrada(idConexao, sistema);
    }
    
    public int verificaRegistro() throws Exception {
        return this.usuarioAnteriorDAO.verificaRegistro();
    }
    
//    public boolean verificaMigracaoMultiloja(String lojaOrigem, String sistema, int idConexao) throws Exception {
//        return this.fornecedorAnteriorDAO.verificaMigracaoMultiloja(lojaOrigem, sistema, idConexao);
//    }
//    
//    public void copiarCodantFornecedor(String sistema, String lojaModelo, String lojaNova) throws Exception {
//        this.fornecedorAnteriorDAO.copiarCodantFornecedor(sistema, lojaModelo, lojaNova);
//    }
//    
//    public String getLojaModelo(int idConexao, String sistema) throws Exception {
//        return this.fornecedorAnteriorDAO.getLojaModelo(idConexao, sistema);
//    }
//
//    public boolean verificaMultilojaMigrada(String lojaOrigem, String sistema, int idConexao) throws Exception {
//        return this.fornecedorAnteriorDAO.verificaMultilojaMigrada(lojaOrigem, sistema, idConexao);
//    }
    
    public String getImpSistemaInicial() throws Exception {
        return this.usuarioAnteriorDAO.getImpSistema();
    }
}
