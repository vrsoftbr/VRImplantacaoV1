package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Importacao
 */
public class ControlXDAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "ControlX";
    }

    public List<Estabelecimento> getLojasCliente() throws SQLException {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	nCodigo id,\n"
                    + "	cDescricao fantasia\n"
                    + "from\n"
                    + "	tabFilial"
            )) {
                while (rs.next()) {
                    result.add(new Estabelecimento(rs.getString("id"), rs.getString("fantasia")));
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	distinct\n"
                    + "	p.grupo cod_merc1,\n"
                    + "	g.NOME mercadologico1,\n"
                    + "	p.SubGrupo cod_merc2,\n"
                    + "	sg.NOME mercadologico2\n"
                    + "from\n"
                    + "	tabproduto p\n"
                    + "join tabgrupo g on p.Grupo = g.GRUPO\n"
                    + "join TabSubGru sg on p.SubGrupo = sg.ACESSO\n"
                    + "order by\n"
                    + "	1, 3")) {
                while (rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rs.getString("cod_merc1"));
                    imp.setMerc1Descricao(rs.getString("mercadologico1"));
                    imp.setMerc2ID(rs.getString("cod_merc2"));
                    imp.setMerc2Descricao(rs.getString("mercadologico2"));
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
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	p.acesso id,\n"
                    + "	p.REFERENCIA ean,\n"
                    + "	p.descricao descricaocompleta,\n"
                    + "	case when coalesce(p.descpeq, p.descricao) = ''\n"
                    + "	then p.descricao else coalesce(p.descpeq, p.descricao) \n"
                    + "	end descricaoreduzida,\n"
                    + "	coalesce(p.qtdminima, 0) qtdminima,\n"
                    + "	coalesce(p.qtdmaxima, 0) qtdmaxima,\n"
                    + "	e.nQtdAtual estoque,\n"
                    + "	p.UNIDVENDA unidadevenda,\n"
                    + "	p.UNIDCOMPRA unidadecompra,\n"
                    + "	case when p.nQtdEmbalagem = 0 or p.nQtdEmbalagem is null\n"
                    + "	then 1 else p.nQtdEmbalagem end  qtdembalagem,\n"
                    + "	p.dDataCad datacadastro,\n"
                    + "	p.cSituacaoTribFora id_aliquota_debito_nf_fe,\n"
                    + "	p.cSituacaoTributaria id_aliquota_debito,\n"
                    + "	p.nIcms_Fora icms_fora_estado,\n"
                    + " p.nIcms_No_Estado icms_dentro_estado,\n"        
                    + "	p.nCST cst,\n"
                    + "	p.Situacao situacaocadastro,\n"
                    + "	p.preco,\n"
                    + "	c.cValor ultimo_preco,\n"
                    + "	coalesce(p.nCustoNacional, 0) custocomimposto,\n"
                    + "	coalesce(p.pesobruto, 0) pesobruto,\n"
                    + "	coalesce(p.pesoliquido, 0) pesoliquido,\n"
                    + "	p.grupo merc1,\n"
                    + "	p.subgrupo merc2,\n"
                    + "	p.cBalanca isBalanca,\n"
                    + "	p.cValidade validade,\n"
                    + "	p.cNCM ncm,\n"
                    + "	coalesce(p.nCest, 0) cest,\n"
                    + "	p.nCstCofins cst_cofins_debito,\n"
                    + "	p.nCstCofins_Entrada cst_cofins_credito,\n"
                    + "	p.nCstPis cst_pis_debito,\n"
                    + "	p.nCstPis_Entrada cst_pis_credito,\n"
                    + "	p.nCstNatREC naturezareceita\n"
                    + "from\n"
                    + "	tabproduto p\n"
                    + "left join tabestqatual e on p.acesso = e.nProduto\n"
                    + "left join (select \n"
                    + "			    nacesso,\n"
                    + "				cvalor,\n"
                    + "				nproduto \n"
                    + "			from \n"
                    + "				tabcustos) c on p.acesso = c.nProduto\n"
                    + "where\n"
                    + "	c.nAcesso in (select \n"
                    + "					max(nAcesso) \n"
                    + "				  from \n"
                    + "					tabcustos \n"
                    + "				  where \n"
                    + "					tabcustos.nproduto = p.acesso)\n"
                    + "order by\n"
                    + "	1"
            )) {
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setEan(rs.getString("ean"));
                    imp.seteBalanca("T".equals(rs.getString("isBalanca")));
                    imp.setValidade(Utils.stringToInt(rs.getString("validade")));
                    imp.setDescricaoCompleta(Utils.acertarTexto(rs.getString("descricaocompleta")));
                    imp.setDescricaoGondola(Utils.acertarTexto(imp.getDescricaoCompleta()));
                    imp.setDescricaoReduzida(rs.getString("descricaoreduzida"));
                    imp.setEstoqueMinimo(rs.getDouble("qtdminima"));
                    imp.setEstoqueMaximo(rs.getDouble("qtdmaxima"));
                    imp.setEstoque(rs.getDouble("estoque"));
                    imp.setTipoEmbalagem(rs.getString("unidadevenda"));
                    imp.setTipoEmbalagemCotacao(rs.getString("unidadecompra"));
                    imp.setQtdEmbalagem(rs.getInt("qtdembalagem"));
                    imp.setDataCadastro(rs.getDate("datacadastro"));
                    imp.setSituacaoCadastro("A".equals(rs.getString("situacaocadastro")) ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    imp.setPrecovenda(rs.getDouble("preco"));
                    imp.setCustoComImposto(rs.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setPesoBruto(rs.getDouble("pesobruto"));
                    imp.setPesoLiquido(rs.getDouble("pesoliquido"));
                    imp.setCodMercadologico1(rs.getString("merc1"));
                    imp.setCodMercadologico2(rs.getString("merc2"));
                    imp.setCodMercadologico3("1");

                    //Icms Débito
                    imp.setIcmsCstSaida(rs.getInt("cst"));
                    imp.setIcmsAliqSaida(rs.getDouble("icms_dentro_estado"));
                    imp.setIcmsReducaoSaida(0);
                    
                    //Icms Débito Fora Estado
                    imp.setIcmsCstSaidaForaEstado(rs.getInt("cst"));
                    imp.setIcmsAliqSaidaForaEstado(rs.getDouble("icms_fora_estado"));
                    imp.setIcmsReducaoSaidaForaEstado(0);

                    //Icms Crédito
                    imp.setIcmsCstEntrada(rs.getInt("cst"));
                    imp.setIcmsAliqEntrada(rs.getDouble("icms_dentro_estado"));
                    imp.setIcmsReducaoEntrada(0);
                    
                    //Icms Crédito Fora Estado
                    imp.setIcmsCstEntrada(rs.getInt("cst"));
                    imp.setIcmsAliqEntradaForaEstado(rs.getDouble("icms_fora_estado"));
                    imp.setIcmsReducaoEntradaForaEstado(0);

                    imp.setNcm(rs.getString("ncm"));
                    imp.setCest(rs.getString("cest"));
                    imp.setPiscofinsCstCredito(rs.getString("cst_cofins_credito"));
                    imp.setPiscofinsCstDebito(rs.getString("cst_cofins_debito"));
                    imp.setPiscofinsNaturezaReceita(rs.getString("naturezareceita"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	acesso id,\n"
                    + "	situacao,\n"
                    + "	cgc cnpj,\n"
                    + "	insc_est,\n"
                    + "	nome razao,\n"
                    + "	NomeFantasia,\n"
                    + "	endereco,\n"
                    + "	bairro,\n"
                    + "	cep,\n"
                    + "	cidade,\n"
                    + "	estado,\n"
                    + "	fone,\n"
                    + "	fax,\n"
                    + "	contato,\n"
                    + "	nPrazoEntr prazoentrega,\n"
                    + "	cCondPagto condicaopagamento,\n"
                    + "	cObs obs,\n"
                    + "	cvendedor vendedor\n"
                    + "from	\n"
                    + "	TabFornecedor\n"
                    + "order by	\n"
                    + "	acesso"
            )) {
                while (rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setAtivo("A".equals(rs.getString("situacao")));
                    imp.setCnpj_cpf(rs.getString("cnpj"));
                    imp.setIe_rg(rs.getString("insc_est"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("nomefantasia"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setCep(rs.getString("cep"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("estado"));
                    imp.setTel_principal(rs.getString("fone"));

                    if (rs.getString("fax") != null && !"".equals(rs.getString("fax"))) {
                        imp.addContato("1", "FAX", rs.getString("fax"), null, TipoContato.NFE, null);
                    }

                    if (rs.getString("contato") != null && !"".equals(rs.getString("contato"))) {
                        imp.addContato("2", rs.getString("contato"), null, null, TipoContato.NFE, null);
                    }

                    String vendedor = "";
                    if (rs.getString("vendedor") != null && !"".equals(rs.getString("vendedor"))) {
                        vendedor = " Vendedor: " + rs.getString("vendedor");
                    }

                    imp.setObservacao(vendedor);

                    if (rs.getString("obs") != null && !"".equals(rs.getString("obs"))) {
                        imp.setObservacao(rs.getString("obs") + vendedor);
                    }

                    imp.setPrazoEntrega(rs.getInt("prazoentrega"));
                    imp.addCondicaoPagamento(Utils.stringToInt(rs.getString("condicaopagamento")));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	nAc_produto id_produto,\n"
                    + "	nAc_Fornecedor id_fornecedor,\n"
                    + "	cSituacao situacao,\n"
                    + "	cRef_Fab codigoexterno\n"
                    + "from	\n"
                    + "	TabRelaci_ProdFornec\n"
                    + "order by\n"
                    + "	2, 1"
            )) {
                while (rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rs.getString("id_produto"));
                    imp.setIdFornecedor(rs.getString("id_fornecedor"));
                    imp.setCodigoExterno(rs.getString("codigoexterno"));

                    result.add(imp);
                }
            }
        }
        return result;
    }
}
