package vrimplantacao2_5.service.migracao;

import vrimplantacao2.dao.cadastro.usuario.UsuarioAnteriorDAO;

/**
 *
 * @author Wesley
 */
public class UsuarioService {
    
    private UsuarioAnteriorDAO usuarioAnteriorDAO;
    
    public UsuarioService() throws Exception {
        this.usuarioAnteriorDAO = new UsuarioAnteriorDAO();
    }
    
    public int existeConexaoMigrada(int idConexao, String sistema) throws Exception {
        return this.usuarioAnteriorDAO.getConexaoMigrada(idConexao, sistema);
    }
    
    public int verificaRegistro() throws Exception {
        return this.usuarioAnteriorDAO.verificaRegistro();
    }
    
    public boolean verificaMigracaoMultiloja(String lojaOrigem, String sistema, int idConexao) throws Exception {
        return this.usuarioAnteriorDAO.verificaMigracaoMultiloja(lojaOrigem, sistema, idConexao);
    }
    
    public void copiarCodantUsuario(String sistema, String lojaModelo, String lojaNova) throws Exception {
        this.usuarioAnteriorDAO.copiarCodantUsuario(sistema, lojaModelo, lojaNova);
    }
    
    public String getLojaModelo(int idConexao, String sistema) throws Exception {
        return this.usuarioAnteriorDAO.getLojaModelo(idConexao, sistema);
    }
    
    public String getImpSistemaInicial() throws Exception {
        return this.usuarioAnteriorDAO.getImpSistema();
    }
}
