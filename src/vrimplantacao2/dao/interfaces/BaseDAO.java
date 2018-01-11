package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoAccess;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.utils.MathUtils;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Leandro
 */
public class BaseDAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "Base";
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        
        try (Statement stm = ConexaoAccess.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select CNPJ, Nome from Empresa order by CNPJ"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("CNPJ"), rst.getString("Nome")));
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoAccess.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n" +
                    "	Objetos.Objeto, \n" +
                    "	Objetos.DtCadastro, \n" +
                    "	Objetos.Nome, \n" +
                    "	Objetos.Setor, \n" +
                    "	Objetos.QtdNoPC, \n" +
                    "	Objetos.EstCritico, \n" +
                    "	Objetos.Estoque, \n" +
                    "	Objetos.LucroPerc, \n" +
                    "	Objetos.VrCompra, \n" +
                    "	Objetos.VrVenda\n" +
                    "FROM \n" +
                    "	Objetos\n" +
                    "WHERE\n" +
                    "   not Objetos.Objeto is null\n" +
                    "ORDER BY \n" +
                    "	Objetos.Objeto;"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("Objeto"));
                    if (imp.getImportId() != null && imp.getImportId().length() < 19) {
                        imp.setEan(imp.getImportId());
                    }
                    imp.setQtdEmbalagemCotacao(rst.getInt("QtdNoPC"));
                    imp.setDataCadastro(rst.getDate("DtCadastro"));
                    imp.setDescricaoCompleta(rst.getString("Nome"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setCodMercadologico1(rst.getString("Setor"));
                    imp.setEstoqueMinimo(rst.getDouble("EstCritico"));
                    imp.setEstoque(rst.getDouble("Estoque"));
                    imp.setMargem(rst.getDouble("LucroPerc"));
                    imp.setCustoComImposto(MathUtils.round(rst.getDouble("VrCompra"), 2));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setPrecovenda(rst.getDouble("VrVenda"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }
    
    
    
}
