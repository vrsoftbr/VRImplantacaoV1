package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import vrimplantacao2.parametro.Parametros;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.parametro.Parametros;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Alan
 */
public class SBOnlineDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "SBOnline";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                new OpcaoProduto[]{
                    OpcaoProduto.MERCADOLOGICO_PRODUTO,
                    OpcaoProduto.MERCADOLOGICO_POR_NIVEL,
                    OpcaoProduto.MANTER_CODIGO_MERCADOLOGICO,
                    OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                    OpcaoProduto.FAMILIA_PRODUTO,
                    OpcaoProduto.FAMILIA,
                    OpcaoProduto.PRODUTOS,
                    OpcaoProduto.EAN,
                    OpcaoProduto.EAN_EM_BRANCO,
                    OpcaoProduto.DATA_CADASTRO,
                    OpcaoProduto.TIPO_EMBALAGEM_EAN,
                    OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                    OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                    OpcaoProduto.QTD_EMBALAGEM_EAN,
                    OpcaoProduto.PESAVEL,
                    OpcaoProduto.VALIDADE,
                    OpcaoProduto.DESC_COMPLETA,
                    OpcaoProduto.DESC_GONDOLA,
                    OpcaoProduto.DESC_REDUZIDA,
                    OpcaoProduto.ESTOQUE_MAXIMO,
                    OpcaoProduto.ESTOQUE_MINIMO,
                    OpcaoProduto.PRECO,
                    OpcaoProduto.CUSTO,
                    OpcaoProduto.CUSTO_COM_IMPOSTO,
                    OpcaoProduto.CUSTO_SEM_IMPOSTO,
                    OpcaoProduto.ESTOQUE_MAXIMO,
                    OpcaoProduto.ESTOQUE_MINIMO,
                    OpcaoProduto.ESTOQUE,
                    OpcaoProduto.ATIVO,
                    OpcaoProduto.NCM,
                    OpcaoProduto.CEST,
                    OpcaoProduto.PIS_COFINS,
                    OpcaoProduto.NATUREZA_RECEITA,
                    OpcaoProduto.ICMS,
                    OpcaoProduto.MARGEM,
                    OpcaoProduto.OFERTA,
                    OpcaoProduto.VOLUME_QTD
                }
        ));
    }

    @Override
    public Set<OpcaoFornecedor> getOpcoesDisponiveisFornecedor() {
        return new HashSet<>(Arrays.asList(
                OpcaoFornecedor.DADOS,
                OpcaoFornecedor.RAZAO_SOCIAL,
                OpcaoFornecedor.NOME_FANTASIA,
                OpcaoFornecedor.CNPJ_CPF,
                OpcaoFornecedor.INSCRICAO_ESTADUAL,
                OpcaoFornecedor.INSCRICAO_MUNICIPAL
        ));
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select cnpj, razao from firma"
            )) {
                while (rs.next()) {
                    result.add(new Estabelecimento(rs.getString("cnpj"), rs.getString("razao")));
                }
            }
        }

        return result;
    }

    public List<String> getNomeLojaCliente() throws Exception {
        List<String> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select fantasia from firma"
            )) {
                while (rst.next()) {
                    result.add(rst.getString("fantasia"));
                }
            }
        }

        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	 DISTINCT\n"
                    + "	 SitTribICMS descricao,\n"
                    + "	 SitTribICMS cst_saida,\n"
                    + "	 AliqICMS aliquota_saida,\n"
                    + "	 Reducao reducao_saida\n"
                    + "from\n"
                    + "	 CalcTributoUF icm \n"
                    + "	 left join UF u on icm.CodUF = u.CodIBGE \n"
                    + "where\n"
                    + "	 u.Sigla = '" + Parametros.get().getUfPadraoV2().getSigla() + "'"
                    + "group BY SitTribICMS,AliqICMS,Reducao"
            )) {
                while (rs.next()) {
                    String id = rs.getString("cst_saida") + "-" + rs.getString("aliquota_saida") + "-" + rs.getString("reducao_saida");
                    result.add(new MapaTributoIMP(
                            id,
                            id,
                            rs.getInt("cst_saida"),
                            rs.getDouble("aliquota_saida"),
                            rs.getDouble("reducao_saida")
                    ));
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
                    + "	f.CODIGO id,\n"
                    + "	f.RAZAO razao,\n"
                    + "	f.FANTASIA fantasia,\n"
                    + "	CURRENT_TIMESTAMP as datacadastro,\n"
                    + "	f.ENDERECO,\n"
                    + "	'0' as numero,\n"
                    + "	f.COMPLEMENTO,\n"
                    + "	f.BAIRRO,\n"
                    + "	f.CIDADE,\n"
                    + "	f.UF,\n"
                    + "	f.CEP,\n"
                    + "	isnull(f.DDD,'') + f.FONE fone1,\n"
                    + "	isnull(f.DDD,'') + f.CELULAR celular,\n"
                    + "	isnull(f.DDD,'') + f.FAX fax,\n"
                    + "	f.CNPJ,\n"
                    + "	f.INSC_ESTA inscricaoestadual,\n"
                    + "	f.EMAIL as observacao,\n"
                    + "	1 as id_situacaocadastro\n"
                    + "from\n"
                    + "	FORNECE f\n"
                    + "order by codigo"
            )) {
                while (rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setDatacadastro(rs.getDate("datacadastro"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));
                    imp.setTel_principal(rs.getString("fone1"));
                    imp.setCnpj_cpf(rs.getString("cnpj"));
                    imp.setIe_rg(rs.getString("inscricaoestadual"));
                    imp.setObservacao(rs.getString("observacao"));
                    imp.setAtivo(rs.getBoolean("id_situacaocadastro"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	c.CODIGO id,\n"
                    + "	c.NOME,\n"
                    + "	c.ENDERE,\n"
                    + "	c.NumLogr,\n"
                    + "	c.COMPLE,\n"
                    + "	c.BAIRRO,\n"
                    + "	c.ESTADO,\n"
                    + "	c.CIDADE,\n"
                    + "	c.CEP,\n"
                    + "	c.FONE,\n"
                    + "	c.INSCRG,\n"
                    + " case when c.CPF is null then c.CGC else c.CPF end cpf_cnpj,\n"
                    + "	c.SEXO,\n"
                    + "	c.EMAIL,\n"
                    + "	c.LIMITE,\n"
                    + "	c.FONECOMP1,\n"
                    + "	c.FONECOMP2,\n"
                    + "	c.FONECOMP3,\n"
                    + "	c.CELULA,\n"
                    + "	c.BloqueiaCheque,\n"
                    + "	c.BloqueiaFatura,\n"
                    + "	c.OBS,\n"
                    + "	c.EMAIL,\n"
                    + "	c.DTNASCI,\n"
                    + "	c.DTCADAS,\n"
                    + "	c.ATIVIDADE cargo,\n"
                    + "	c.CONJUGE\n"
                    + "from\n"
                    + "	Cliente c\n"
                    + "order by\n"
                    + "	c.CODIGO"
            )) {
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rs.getString("id"));
                    imp.setRazao(rs.getString("nome"));
                    imp.setEndereco(rs.getString("endere"));
                    imp.setNumero(rs.getString("numlogr"));
                    imp.setComplemento(rs.getString("comple"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setUf(rs.getString("estado"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setCep(rs.getString("cep"));
                    imp.setTelefone(rs.getString("fone"));
                    imp.setInscricaoestadual(rs.getString("inscrg"));
                    imp.setCnpj(rs.getString("cpf_cnpj"));
                    imp.setSexo("F".equals(rs.getString("SEXO")) ? TipoSexo.FEMININO : TipoSexo.MASCULINO);
                    imp.setEmail(rs.getString("email"));
                    imp.setLimiteCompra(rs.getDouble("limite"));
                    imp.setCelular(rs.getString("celula"));
                    imp.setPermiteCheque(rs.getBoolean("bloqueiacheque"));
                    imp.setPermiteCreditoRotativo(rs.getBoolean("bloqueiafatura"));
                    imp.setObservacao(rs.getString("obs"));
                    imp.setDataCadastro(rs.getDate("dtcadas"));
                    imp.setDataNascimento(rs.getDate("dtnasci"));
                    imp.setCargo(rs.getString("cargo"));
                    imp.setNomeConjuge(rs.getString("conjuge"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	r.CODIGO,\n"
                    + "	r.CLIENTE,\n"
                    + "	case\n"
                    + "	  when c.CPF is null then c.CGC\n"
                    + "	  else c.CPF\n"
                    + "	end cpf_cnpj,\n"
                    + "	r.EMISSAO,\n"
                    + "	r.JUROS,\n"
                    + "	r.VALPAGO,\n"
                    + "	r.NOTAPEDIDO,\n"
                    + "	r.FATURA,\n"
                    + "	r.OBS,\n"
                    + "	r.VENCIMENTO,\n"
                    + "	r.UltPag\n"
                    + "from\n"
                    + "	receber r\n"
                    + "join Cliente c on\n"
                    + "	r.CLIENTE = c.CODIGO\n"
                    + "where\n"
                    + "	r.BAIXA = 0\n"
                    + "order by\n"
                    + "	r.CODIGO"
            )) {
                while (rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();

                    imp.setId(rs.getString("codigo"));
                    imp.setIdCliente(rs.getString("cliente"));
                    imp.setCnpjCliente(rs.getString("cpf_cnpj"));
                    imp.setDataEmissao(rs.getDate("emissao"));
                    imp.setJuros(rs.getDouble("juros"));
                    imp.setValor(rs.getDouble("fatura"));
                    imp.setNumeroCupom(rs.getString("notapedido"));
                    imp.setObservacao(rs.getString("obs"));
                    imp.setDataVencimento(rs.getDate("vencimento"));

                    result.add(imp);
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
                    + "	g.CODGRUPO merc1,\n"
                    + "	g.NOMEGRUPO descmerc1,\n"
                    + "	sg.CODSUB merc2,\n"
                    + "	sg.NOMESUB descmerc2,\n"
                    + "	sg.CODSUB merc3,\n"
                    + "	sg.NOMESUB descmerc3\n"
                    + "from\n"
                    + "	Grupo g\n"
                    + "left join SubGrupo sg on g.CODGRUPO = sg.CODGRUPO"
            )) {
                while (rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();

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
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	CodigoBar ean,\n"
                    + "	Codigo id_produto,\n"
                    + "	Unidade id_tipoembalagem\n"
                    + "from\n"
                    + "	Produto p\n"
                    + "union\n"
                    + "select\n"
                    + "	ca.CodBar ean,\n"
                    + "	ca.CodProd id_produto,\n"
                    + "	p.Unidade id_tipoembalagem\n"
                    + "from\n"
                    + "	CodAdicional ca\n"
                    + "join Produto p on\n"
                    + "	ca.CodProd = p.Codigo"
            )) {
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id_produto"));
                    imp.setEan(rs.getString("ean"));
                    imp.setTipoEmbalagem(rs.getString("id_tipoembalagem"));

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
                    + "	p.Codigo id,\n"
                    + "	p.CodigoBar,\n"
                    + "	p.Descricao descricaocompleta,\n"
                    + "	p.Descricao descricaoreduzida,\n"
                    + "	p.Descricao descricaogondola,\n"
                    + "	p.Ativo id_situacaocadastro,\n"
                    + "	p.UltAltera datacadastro,\n"
                    + "	p.Grupo,\n"
                    + "	p.SubGrupo,\n"
                    + "	p.Marca,\n"
                    + "	p.CodGeneroProduto,\n"
                    + "	cl.Valor ncm,\n"
                    + "	cest.CEST,\n"
                    + "	p.MargemV margem,\n"
                    + "	p.Balanca,\n"
                    + "	p.DiasValidade validade,\n"
                    + "	p.Unidade id_tipoembalagem,\n"
                    + "	ct.SitTribPIS,\n"
                    + "	ct.SitTribPISEnt,\n"
                    + "	ct.NatRecPIS,\n"
                    + "	p.PVista preco,\n"
                    + "	p.Custo,\n"
                    + "	p.EstoqueMin,\n"
                    + "	p.Estoque,\n"
                    + "	ctuf.CodUF,\n"
                    + "	ctuf.SitTribICMS icms_cst,\n"
                    + "	ctuf.AliqICMS icms_aliq,\n"
                    + "	ctuf.Reducao icms_red\n"
                    + "from\n"
                    + "	Produto p \n"
                    + "	left join ClassFiscal cl on p.CodClassFiscal = cl.Codigo\n"
                    + "	left join TabelaCEST cest on p.CodCEST = cest.CodCEST\n"
                    + "	left join CalcTributo ct on ct.CodClassFiscal = cl.Codigo and ct.FinalidadeConfig != 'N'\n"
                    + "	left join CalcTributoUF ctuf on ct.CodCalcTributo = ctuf.CodCalcTributo and ctuf.CodUF = 35\n"
                    + "order by\n"
                    + "	id"
            )) {
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rs.getString("id"));
                    imp.setEan(rs.getString("codigobar"));
                    imp.setDescricaoCompleta(rs.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rs.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(rs.getString("descricaogondola"));
                    imp.setSituacaoCadastro(rs.getInt("id_situacaocadastro"));
                    imp.setDataCadastro(rs.getDate("datacadastro"));

                    imp.setCodMercadologico1(rs.getString("grupo"));
                    imp.setCodMercadologico2(rs.getString("subgrupo"));
                    imp.setCodMercadologico3(rs.getString("marca"));

                    imp.setNcm(rs.getString("ncm"));
                    imp.setCest(rs.getString("cest"));

                    imp.seteBalanca(rs.getBoolean("balanca"));
                    imp.setValidade(rs.getInt("validade"));
                    imp.setTipoEmbalagem(rs.getString("id_tipoembalagem"));

                    imp.setPiscofinsCstDebito(rs.getString("sittribpis"));
                    imp.setPiscofinsCstCredito(rs.getString("sittribpisent"));
                    imp.setPiscofinsNaturezaReceita(rs.getString("natrecpis"));

                    imp.setPrecovenda(rs.getDouble("preco"));
                    imp.setCustoSemImposto(rs.getDouble("custo"));
                    imp.setCustoComImposto(imp.getCustoSemImposto());

                    imp.setEstoque(rs.getDouble("estoque"));
                    imp.setEstoqueMinimo(rs.getDouble("estoquemin"));

                    imp.setIcmsCstSaida(rs.getInt("icms_cst"));
                    imp.setIcmsAliqSaida(rs.getDouble("icms_aliq"));
                    imp.setIcmsReducaoSaida(rs.getDouble("icms_red"));

                    result.add(imp);
                }
            }
        }
        return result;
    }
}
