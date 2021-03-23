package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author guilhermegomes
 */
public class FuturaDAO extends InterfaceDAO implements MapaTributoProvider {

    public String complemento = "";
    
    @Override
    public String getSistema() {
        return "Futura" + complemento;
    }
    
    public List<Estabelecimento> getLojaCliente() throws SQLException {
        List<Estabelecimento> result = new ArrayList<>();
        
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "SELECT \n" +
                    "	id,\n" +
                    "	FANTASIA,\n" +
                    "	CNPJ_CPF \n" +
                    "FROM\n" +
                    "	CADASTRO c\n" +
                    "WHERE \n" +
                    "	FK_EMPRESA IS null")) {
                while(rs.next()) {
                    result.add(new Estabelecimento(
                                        rs.getString("id"), 
                                        rs.getString("fantasia")));
                }
            }
        }
        
        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "SELECT \n" +
                    "	gr.id idgrupo,\n" +
                    "	gr.DESCRICAO descgrupo,\n" +
                    "	sb.ID idsubgrupo,\n" +
                    "	sb.DESCRICAO descsubgrupo\n" +
                    "FROM\n" +
                    "	PRODUTO_GRUPO gr\n" +
                    "LEFT JOIN PRODUTO_SUBGRUPO sb \n" +
                    "		ON gr.ID = sb.FK_PRODUTO_GRUPO")) {
                while(rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setMerc1ID(rs.getString("idgrupo"));
                    imp.setMerc1Descricao(rs.getString("descgrupo"));
                    imp.setMerc2ID(rs.getString("idsubgrupo"));
                    imp.setMerc2Descricao(rs.getString("descsubgrupo"));
                    imp.setMerc3ID("1");
                    imp.setMerc3Descricao(imp.getMerc2Descricao());
                    
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
                    "SELECT \n" +
                    "	p.id,\n" +
                    "	p.DESCRICAO,\n" +
                    "	pu.SIGLA unidade,\n" +
                    "	pcb.CODIGO_BARRA ean,\n" +
                    "	p.UTILIZA_BALANCA_PESO,\n" +
                    "	p.VALIDADE_DIAS,\n" +
                    "	pg.id merc1,\n" +
                    "	pg.DESCRICAO descmerc1,\n" +
                    "	ps.id merc2,\n" +
                    "	ps.DESCRICAO descmerc2,\n" +
                    "	p.STATUS,\n" +
                    "	est.estoque,\n" +
                    "	p.ESTOQUE_MAXIMO,\n" +
                    "	pe.ESTOQUE_MINIMO,\n" +
                    "	p.PESO_BRUTO,\n" +
                    "	p.peso_liquido,\n" +
                    "	cf.CLASSIFICACAO ncm,\n" +
                    "	p.DATA_CADASTRO,\n" +
                    "	p.CUSTO,\n" +
                    "	p.CUSTO_MEDIO,\n" +
                    "	p.CUSTO_ULTIMO,\n" +
                    "	pp.LUCRO margem,\n" +
                    "	pp.VALOR preco,\n" +
                    "	icm.DESCRICAO icms,\n" +
                    "   cf.ICMS_PERCENTUAL_SP icmssp,\n" +        
                    "	ce.CODIGO cest,\n" +
                    "	pf.PIS_CST,\n" +
                    "	pf.COFINS_CST\n" +
                    "FROM \n" +
                    "	PRODUTO p\n" +
                    "LEFT JOIN PRODUTO_UNIDADE pu ON p.id = pu.ID\n" +
                    "LEFT JOIN produto_preco pp ON p.id = pp.FK_PRODUTO \n" +
                    "LEFT JOIN PRODUTO_CODIGO_BARRA pcb ON p.id = pcb.FK_PRODUTO\n" +
                    "LEFT JOIN PRODUTO_ESTOQUE pe ON p.id = pe.FK_PRODUTO \n" +
                    "LEFT JOIN CLASSIFICACAO_FISCAL cf ON p.FK_CLASSIFICACAO_FISCAL = cf.ID \n" +
                    "LEFT JOIN CEST ce ON p.FK_CEST = ce.ID\n" +
                    "LEFT JOIN PRODUTO_FISCAL pf ON p.id = pf.FK_PRODUTO\n" +
                    "LEFT JOIN ICMS icm ON pf.FK_ICMS = icm.ID\n" +
                    "LEFT JOIN PRODUTO_SUBGRUPO ps ON p.FK_PRODUTO_SUBGRUPO = ps.ID\n" +
                    "LEFT JOIN PRODUTO_GRUPO pg ON ps.FK_PRODUTO_GRUPO = pg.ID\n" +
                    "LEFT JOIN \n" +
                    "	(SELECT\n" +
                    "		PedIt.FK_PRODUTO,\n" +
                    "		COALESCE(sum(\n" +
                    "               CASE WHEN (TpPed.ESTOQUE_ENTRADA_SAIDA = 'E') THEN \n" +
                    "                   PedIt.QUANTIDADE \n" +
                    "               WHEN (TpPed.ESTOQUE_ENTRADA_SAIDA = 'S') THEN \n" +
                    "                   (PedIt.QUANTIDADE * (-1)) ELSE 0 END),\n" +
                    "		0) estoque\n" +
                    "	FROM\n" +
                    "		PEDIDO_ITEM PedIt\n" +
                    "	INNER JOIN PEDIDO Ped ON\n" +
                    "		(Ped.ID = PedIt.FK_PEDIDO)\n" +
                    "	INNER JOIN TIPO_PEDIDO TpPed ON\n" +
                    "		(TpPed.ID = Ped.FK_TIPO_PEDIDO)\n" +
                    "	WHERE\n" +
                    "		TpPed.GERA_ESTOQUE = 'S' AND ((Ped.FK_EMPRESA = " + getLojaOrigem() + ") OR (Ped.FK_EMPRESA = 0)) AND \n" +
                    "		(((TpPed.STATUS_TP_PEDIDO = 0) AND \n" +
                    "		(Ped.STATUS IN (2, 4))) OR ((TpPed.STATUS_TP_PEDIDO = 1) AND \n" +
                    "		(Ped.STATUS <> 3)))\n" +
                    "	GROUP BY \n" +
                    "		1) est ON p.ID = est.fk_produto\n" +
                    "WHERE \n" +
                    "	pp.FK_TABELA_PRECO = 1 AND	\n" +
                    "	pe.FK_EMPRESA = " + getLojaOrigem())) {
                while(rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("id"));
                    imp.setDescricaoCompleta(rs.getString("DESCRICAO"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setTipoEmbalagem(rs.getString("unidade"));
                    imp.setEan(rs.getString("ean"));
                    imp.setCodMercadologico1(rs.getString("merc1"));
                    imp.setCodMercadologico2(rs.getString("merc2"));
                    imp.setCodMercadologico3("1");
                    imp.setSituacaoCadastro(rs.getInt("status") == 0 ? 1 : 0);
                    imp.setEstoque(rs.getDouble("estoque"));
                    imp.setEstoqueMaximo(rs.getDouble("ESTOQUE_MAXIMO"));
                    imp.setEstoqueMinimo(rs.getDouble("ESTOQUE_MINIMO"));
                    imp.setPesoBruto(rs.getDouble("PESO_BRUTO"));
                    imp.setPesoLiquido(rs.getDouble("peso_liquido"));
                    imp.setNcm(rs.getString("ncm"));
                    imp.setCest(rs.getString("cest"));
                    imp.setDataCadastro(rs.getDate("data_cadastro"));
                    imp.setCustoComImposto(rs.getDouble("custo"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setPrecovenda(rs.getDouble("preco"));
                    imp.setMargem(rs.getDouble("margem"));
                    imp.setPiscofinsCstDebito(rs.getString("pis_cst"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "SELECT \n" +
                    "	id,\n" +
                    "	DATA_CADASTRO,\n" +
                    "	CNPJ_CPF,\n" +
                    "	INSCRICAO_RG,\n" +
                    "	INSCRICAO_MUNICIPAL,\n" +
                    "	RAZAO_SOCIAL,\n" +
                    "	FANTASIA,\n" +
                    "	e_mail,\n" +
                    "	OBSERVACAO,\n" +
                    "	status\n" +
                    "FROM \n" +
                    "	cadastro\n" +
                    "WHERE 	\n" +
                    "	CHK_FORNECEDOR = 'S'")) {
                while(rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("id"));
                    imp.setDatacadastro(rs.getDate("data_cadastro"));
                    imp.setIe_rg(rs.getString("INSCRICAO_RG"));
                    imp.setInsc_municipal(rs.getString("INSCRICAO_MUNICIPAL"));
                    imp.setRazao(rs.getString("razao_social"));
                    imp.setFantasia(rs.getString("fantasia"));
                    
                    String email = rs.getString("e_mail");
                    
                    if(email != null && !"".equals(email)) {
                        imp.addContato("1", "EMAIL", null, null, TipoContato.NFE, email);
                    }
                    
                    imp.setAtivo(rs.getInt("status") == 1);
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "SELECT \n" +
                    "	FK_FORNECEDOR idfornecedor,\n" +
                    "	FK_PRODUTO idproduto,\n" +
                    "	NRO_FABRICANTE codigoexterno\n" +
                    "FROM \n" +
                    "	PRODUTO_FORNECEDOR pf\n" +
                    "ORDER BY \n" +
                    "	1, 2")) {
                while(rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setIdFornecedor(rs.getString("idfornecedor"));
                    imp.setIdProduto(rs.getString("idproduto"));
                    imp.setCodigoExterno(rs.getString("codigoexterno"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
