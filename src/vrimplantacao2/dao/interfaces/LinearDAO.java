package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoMySQL;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Importacao
 */
public class LinearDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "Linear";
    }
    
    public List<Estabelecimento> getLojasCliente() throws SQLException {
        List<Estabelecimento> result = new ArrayList<>();
        
        try(Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "SELECT \n" +
                    "	emp_codigo AS id,\n" +
                    "	emp_razao AS razao,\n" +
                    "	emp_fantasia AS fantasia,\n" +
                    "	emp_cgc AS cnpj\n" +
                    "FROM empresa\n" +
                    "ORDER BY 1")) {
                while(rs.next()) {
                    result.add(new Estabelecimento(rs.getString("id"), rs.getString("fantasia")));
                }
            }
        }
        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "SELECT \n" +
                    "	codigo,\n" +
                    "	codpdv,\n" +
                    "	descricao,\n" +
                    "	valor icms,\n" +
                    "	cst,\n" +
                    "	reducao\n" +
                    "FROM \n" +
                    "	icms\n" +
                    "ORDER BY\n" +
                    "	codigo")) {
                while(rs.next()) {
                    result.add(new MapaTributoIMP(rs.getString("codigo"), rs.getString("descricao")));
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "SELECT \n" +
                    "	DISTINCT \n" +
                    "	f.TAB_COD merc1,\n" +
                    "	f.TAB_DESC descmerc1,\n" +
                    "	d.TAB_COD merc2,\n" +
                    "	d.TAB_DESC descmerc2,\n" +
                    "	s.tab_cod merc3,\n" +
                    "	s.tab_desc descmerc3\n" +
                    "FROM\n" +
                    "	es1p pr\n" +
                    "JOIN st_familia f ON pr.es1_familia = f.TAB_COD\n" +
                    "JOIN st_departamento d ON pr.es1_departamento = d.TAB_COD\n" +
                    "JOIN st_secao s ON pr.es1_secao = s.tab_cod")) {
                while(rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rs.getString("merc1"));
                    imp.setMerc1Descricao(rs.getString("descmerc1"));
                    imp.setMerc2ID(rs.getString("merc2"));
                    imp.setMerc2Descricao(rs.getString("descmerc2"));
                    imp.setMerc3ID(rs.getString("merc3"));
                    imp.setMerc3Descricao(rs.getString("descmerc3"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "SELECT\n" +
                    "	pr.es1_cod id,\n" +
                    "	pr.es1_codbarra ean,\n" +
                    "	pr.es1_desc descricaocompleta,\n" +
                    "	pr.es1_compl descricaoreduzida,\n" +
                    "	pr.es1_descetiqueta descricaogondola,\n" +
                    "	pc.Es1_UM unidade,\n" +
                    "	pc.ES1_QEMBV qtdembalagem,\n" +
                    "	pc.Es1_UM2 unidadecompra,\n" +
                    "	pc.es1_qembc qtdembalagemcompra,\n" +
                    "	pr.es1_familia merc1,\n" +
                    "	pr.es1_departamento merc2,\n" +
                    "	pr.es1_secao merc3,\n" +
                    "	pr.es1_nbm ncm,\n" +
                    "	pc.es1_cest cest,\n" +
                    "	pc.Es1_Ativo situacao,\n" +
                    "	pc.ES1_DTCAD cadastro,\n" +
                    "	pc.ES1_TRIBUTACAO idicms,\n" +
                    "	pc.es1_margemcom margem,\n" +
                    "	pc.es1_prvarejo preco,\n" +
                    "	pc.es1_prcusto custo,\n" +
                    "	pc.es1_classfiscal,\n" +
                    "	e.quantidade_atual estoque,\n" +
                    "	pc.Es1_ESTMINIMO estoqueminimo,\n" +
                    "	pc.Es1_ESTMAXIMO estoquemaximo,\n" +
                    "	pc.ES1_PESAVEL pesavel,\n" +
                    "	pc.ES1_BALANCA balanca,\n" +
                    "	pc.es1_pesol pesoliquido,\n" +
                    "	pc.es1_pesob pesobruto,\n" +
                    "	pc.es1_cstpis cstpis,\n" +
                    "	pc.es1_cstcofins cstcofins,\n" +
                    "	pc.pis_natreceita naturezareceita\n" +
                    "FROM\n" +
                    "	es1p pr\n" +
                    "JOIN es1 pc ON pr.es1_cod = pc.ES1_COD\n" +
                    "LEFT JOIN estoques e ON pc.ES1_COD = e.es1_cod AND\n" +
                    "	pc.es1_empresa = e.es1_empresa\n" +
                    "WHERE \n" +
                    "	pc.es1_empresa = " + getLojaOrigem())) {
                while(rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setEan(rs.getString("ean"));
                    imp.seteBalanca(rs.getInt("balanca") == 1);
                    imp.setDescricaoCompleta(rs.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rs.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(rs.getString("descricaogondola"));
                    imp.setTipoEmbalagem(rs.getString("unidade"));
                    imp.setQtdEmbalagem(rs.getInt("qtdembalagem"));
                    imp.setTipoEmbalagemCotacao(rs.getString("unidadecompra"));
                    imp.setQtdEmbalagemCotacao(rs.getInt("qtdembalagemcompra"));
                    imp.setCodMercadologico1(rs.getString("merc1"));
                    imp.setCodMercadologico2(rs.getString("merc2"));
                    imp.setCodMercadologico3(rs.getString("merc3"));
                    imp.setNcm(rs.getString("ncm"));
                    imp.setCest(rs.getString("cest"));
                    imp.setSituacaoCadastro(rs.getInt("situacao"));
                    imp.setDataCadastro(rs.getDate("cadastro"));
                    imp.setIcmsDebitoId(rs.getString("idicms"));
                    imp.setIcmsDebitoForaEstadoId(imp.getIcmsDebitoId());
                    imp.setIcmsDebitoForaEstadoNfId(imp.getIcmsDebitoId());
                    imp.setIcmsConsumidorId(imp.getIcmsDebitoId());
                    imp.setIcmsCreditoId(imp.getIcmsDebitoId());
                    imp.setIcmsCreditoForaEstadoId(imp.getIcmsDebitoId());
                    imp.setMargem(rs.getDouble("margem"));
                    imp.setPrecovenda(rs.getDouble("preco"));
                    imp.setCustoComImposto(rs.getDouble("custo"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setEstoque(rs.getDouble("estoque"));
                    imp.setEstoqueMinimo(rs.getDouble("estoqueminimo"));
                    imp.setEstoqueMaximo(rs.getDouble("estoquemaximo"));
                    imp.setPesoBruto(rs.getDouble("pesobruto"));
                    imp.setPesoLiquido(rs.getDouble("pesoliquido"));
                    imp.setPiscofinsCstDebito(rs.getString("cstpis"));
                    imp.setPiscofinsNaturezaReceita(rs.getString("naturezareceita"));
                    
                    long ean = Utils.stringToLong(imp.getEan());
                    
                    if(imp.getEan() != null && !imp.getEan().equals("") && imp.isBalanca() == false) {
                        if(String.valueOf(ean).length() < 7) {
                            imp.setManterEAN(true);
                        }
                    }
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "SELECT \n" +
                    "	es1_cod idproduto,\n" +
                    "	cg2_cod idfornecedor,\n" +
                    "	es1_codforn codigoexterno\n" +
                    "FROM \n" +
                    "	es1i")) {
                while(rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rs.getString("idproduto"));
                    imp.setIdFornecedor(rs.getString("idfornecedor"));
                    imp.setCodigoExterno(rs.getString("codigoexterno"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
