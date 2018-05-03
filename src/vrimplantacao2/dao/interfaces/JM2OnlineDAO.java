package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Leandro
 */
public class JM2OnlineDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "JM2Online";
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select id, e_razaoSocial from Empresas order by id"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("id"), rst.getString("e_razaoSocial")));
                }
            }
        }
        
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	g.id merc1,\n" +
                    "	g.descricao merc1_desc,\n" +
                    "	sg.id merc2,\n" +
                    "	sg.descricao merc2_desc\n" +
                    "from\n" +
                    "	ProdutosSubGrupos sg\n" +
                    "	join ProdutosGrupos g on\n" +
                    "		sg.idGrupo = g.id\n" +
                    "order by\n" +
                    "	merc1_desc, merc2_desc"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setMerc1ID(rst.getString("merc1"));
                    imp.setMerc1Descricao(rst.getString("merc1_desc"));
                    imp.setMerc2ID(rst.getString("merc2"));
                    imp.setMerc2Descricao(rst.getString("merc2_desc"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select id, descricao from ProdutosFamilias order by 1"
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setDescricao(rst.getString("descricao"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	p.codigo id,\n" +
                    "	p.id codigosped,\n" +
                    "	p.dataInicio datacadastro,\n" +
                    "	coalesce(ean.ean, p.codigo) ean,\n" +
                    "	coalesce(ean.qtdembalagem, 1) qtdembalagem,\n" +
                    "	case p.balanca\n" +
                    "	when 'P' then 'KG'\n" +
                    "	else unidade end tipoEmbalagem,\n" +
                    "	case when p.balanca in ('U', 'P') then 1\n" +
                    "	else 0 end pesavel,\n" +
                    "	p.descricaoInterna descricaocompleta,\n" +
                    "	p.descricaoDaNotaFiscal descricaoreduzida,\n" +
                    "	p.idGrupo merc1,\n" +
                    "	p.idSubGrupo merc2,\n" +
                    "	nullif(p.idFamilia, -1) id_familia,\n" +
                    "	p.peso pesoliquido,\n" +
                    "	p.pesoB pesobruto,\n" +
                    "	p.saldoAtual estoque,\n" +
                    "	pr.margem1 margem,\n" +
                    "	p.precoUltimaCompra custocomimposto,\n" +
                    "	p.precoUltimaCompraLiquido custosemimposto,\n" +
                    "	pr.preco1 preco,\n" +
                    "	p.statusProduto status,\n" +
                    "	p.classificacaoFiscal ncm,\n" +
                    "	nullif(p.codigoCEST,-1) cest,\n" +
                    "	p.situacaoTributaria icms_cst,\n" +
                    "	p.aliquotaICMS icms_aliquota,\n" +
                    "	p.reducaoBaseICMS icms_reduzido,\n" +
                    "	pis.tipoPisCofins piscofinsSaida,\n" +
                    "	p.idFornecedor codigofabricante\n" +
                    "from\n" +
                    "	Produtos p\n" +
                    "	join Empresas emp on emp.id = " + getLojaOrigem() + "\n" +
                    "	left join ProdutosEmpresas pe on pe.idProduto = p.id and pe.idEmpresa = emp.id\n" +
                    "	join (select * from ProdutosPrecos where (ativoTabela = 'A' or ativoNormal = 'A' or ativoTabela = 'A')) pr on pr.codigoProduto = p.codigo and pr.idEmpresa = emp.id\n" +
                    "	left join (\n" +
                    "		select \n" +
                    "			p.id idProduto,\n" +
                    "			nullif(ltrim(rtrim(p.codigoDeBarras)),'') ean,\n" +
                    "			1 qtdembalagem\n" +
                    "		from\n" +
                    "			Produtos p\n" +
                    "		where\n" +
                    "			not nullif(ltrim(rtrim(codigoDeBarras)),'') is null\n" +
                    "		union\n" +
                    "		select\n" +
                    "			ean.idProduto,\n" +
                    "			ean.codigoDeBarras ean,\n" +
                    "			ean.quantidadeCB qtdembalagem\n" +
                    "		from\n" +
                    "			ProdutosCodigosDeBarras ean\n" +
                    "	) ean on ean.idProduto = p.id\n" +
                    "	left join ImpostosPDVPISCOFINS pis on pis.codigo = substring(p.impostoPDV,1,1)\n" +
                    "where\n" +
                    "	p.dataFinal is null\n" +
                    "order by\n" +
                    "	p.codigo;"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    boolean isBalanca = rst.getBoolean("pesavel");
                    String ean;
                    
                    if (isBalanca && rst.getString("ean") != null & rst.getString("ean").matches("0*2{1}[0-9]*0{1}")) {
                        String e = Utils.stringLong(rst.getString("ean"));
                        ean = e.substring(1, e.length() - 1); 
                    } else {
                        ean = rst.getString("ean");
                    }
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setEan(ean);
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    imp.setTipoEmbalagem(rst.getString("tipoEmbalagem"));
                    imp.seteBalanca(isBalanca);
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoGondola(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setCodMercadologico1(rst.getString("merc1"));
                    imp.setCodMercadologico2(rst.getString("merc2"));
                    imp.setIdFamiliaProduto(rst.getString("id_familia"));
                    imp.setPesoLiquido(rst.getDouble("pesoliquido"));
                    imp.setPesoBruto(rst.getDouble("pesobruto"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setPrecovenda(rst.getDouble("preco"));
                    imp.setSituacaoCadastro("I".equals(rst.getString("status")) ? 0 : 1);
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setIcmsCst(Utils.stringToInt(rst.getString("icms_cst")));
                    imp.setIcmsAliq(rst.getDouble("icms_aliquota"));
                    imp.setIcmsReducao(rst.getDouble("icms_reduzido"));
                    imp.setPiscofinsCstDebito(rst.getString("piscofinsSaida"));
                    imp.setFornecedorFabricante(rst.getString("codigofabricante"));
                    imp.setCodigoSped(rst.getString("codigosped"));
                                        
                    result.add(imp);
                }
            }
        }
        
        return result;
    }
    
}
