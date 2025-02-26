package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Importacao
 */
public class CadastraFacilDAO extends InterfaceDAO {

    public String id_loja;
    
    @Override
    public String getSistema() {
        //Modificado para integrar com Cadastra Facil e SysPdv
        return "SysPdv(SQLSERVER)" + id_loja;
    }
    
    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "    lpad(id_empresa, 4, '0') as id_empresa,\n" +
                    "    nome_razao\n" +
                    "from\n" +
                    "    empresa")) {
                while(rs.next()) {
                    result.add(new Estabelecimento(rs.getString("id_empresa"), rs.getString("nome_razao")));
                }
            }
        }
        return result;
    }
    
    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "    id_familia,\n" +
                    "    cod_familia,\n" +
                    "    nome_familia\n" +
                    "from "
                      + "familias")) {
                while(rs.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("id_familia"));
                    imp.setDescricao(rs.getString("nome_familia"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
    
    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "    id_familia,\n" +
                    "    cod_produto\n" +
                    "from\n" +
                    "    familias_produtos")) {
                while(rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("cod_produto"));
                    imp.setIdFamiliaProduto(rs.getString("id_familia"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
