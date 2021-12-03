package vrimplantacao2_5.service.migracao;

import vrimplantacao2.dao.cadastro.fornecedor.FornecedorAnteriorDAO;

/**
 *
 * @author Lucas Santos
 */
public class FornecedorService {
    
    private FornecedorAnteriorDAO fornecedorAnteriorDAO;
    
    public FornecedorService() throws Exception {
        this.fornecedorAnteriorDAO = new FornecedorAnteriorDAO();
    }
    
    public FornecedorService(FornecedorAnteriorDAO fornecedorAnteriorDAO) {
        this.fornecedorAnteriorDAO = fornecedorAnteriorDAO;
    }
    
    public int existeConexaoMigrada(int idConexao, String sistema) throws Exception {
        return this.fornecedorAnteriorDAO.getConexaoMigrada(idConexao, sistema);
    }
    
    public int verificaRegistro() throws Exception {
        return this.fornecedorAnteriorDAO.verificaRegistro();
    }
    
    public boolean verificaMigracaoMultiloja(String lojaOrigem, String sistema, int idConexao) throws Exception {
        return this.fornecedorAnteriorDAO.verificaMigracaoMultiloja(lojaOrigem, sistema, idConexao);
    }
    
    public void copiarCodantFornecedor(String sistema, String lojaModelo, String lojaNova) throws Exception {
        this.fornecedorAnteriorDAO.copiarCodantFornecedor(sistema, lojaModelo, lojaNova);
    }
    
    public String getLojaModelo(int idConexao, String sistema) throws Exception {
        return this.fornecedorAnteriorDAO.getLojaModelo(idConexao, sistema);
    }

    public boolean verificaMultilojaMigrada(String lojaOrigem, String sistema, int idConexao) throws Exception {
        return this.fornecedorAnteriorDAO.verificaMultilojaMigrada(lojaOrigem, sistema, idConexao);
    }
}
