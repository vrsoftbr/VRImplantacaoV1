package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import static vr.core.utils.StringUtils.LOG;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.ProdutoAutomacaoVO;
import vrimplantacao2_5.dao.conexao.ConexaoPostgres;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.cadastro.financeiro.contareceber.OpcaoContaReceber;
import vrimplantacao2.vo.cadastro.receita.OpcaoReceitaBalanca;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.ContaReceberIMP;
import vrimplantacao2.vo.importacao.ConveniadoIMP;
import vrimplantacao2.vo.importacao.ConvenioEmpresaIMP;
import vrimplantacao2.vo.importacao.ConvenioTransacaoIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.ReceitaBalancaIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;

/**
 *
 * @author Alan
 */
public class SincDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "WebSac";
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "codestabelec codigo, \n"
                    + "razaosocial descricao \n"
                    + "from estabelecimento\n"
                    + "order by codestabelec"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("codigo"), rst.getString("descricao")));
                }
            }
        }
        return result;
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.MAPA_TRIBUTACAO,
                OpcaoProduto.MERCADOLOGICO_POR_NIVEL,
                OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.MERCADOLOGICO_POR_NIVEL_REPLICAR,
                OpcaoProduto.FAMILIA,
                OpcaoProduto.FAMILIA_PRODUTO,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                OpcaoProduto.QTD_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.PESO_BRUTO,
                OpcaoProduto.PESO_LIQUIDO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.TROCA,
                OpcaoProduto.MARGEM,
                OpcaoProduto.CUSTO,
                OpcaoProduto.PRECO,
                OpcaoProduto.ATIVO,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.NATUREZA_RECEITA,
                OpcaoProduto.ICMS,
                OpcaoProduto.ATACADO,
                OpcaoProduto.PAUTA_FISCAL,
                OpcaoProduto.PAUTA_FISCAL_PRODUTO,
                OpcaoProduto.SUGESTAO_COTACAO,
                OpcaoProduto.COMPRADOR,
                OpcaoProduto.COMPRADOR_PRODUTO,
                OpcaoProduto.OFERTA,
                OpcaoProduto.VENDA_CONTROLADA,
                OpcaoProduto.NORMA_REPOSICAO,
                OpcaoProduto.TIPO_PRODUTO,
                OpcaoProduto.FABRICACAO_PROPRIA,
                OpcaoProduto.NCM,
                OpcaoProduto.CEST,
                OpcaoProduto.MAPA_TRIBUTACAO,
                OpcaoProduto.NUTRICIONAL,
                OpcaoProduto.RECEITA_BALANCA,
                OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.VOLUME_QTD,
                OpcaoProduto.VOLUME_TIPO_EMBALAGEM,
                OpcaoProduto.VENDA_PDV,
                OpcaoProduto.PDV_VENDA
        ));
    }

    @Override
    public Set<OpcaoFornecedor> getOpcoesDisponiveisFornecedor() {
        return new HashSet<>(Arrays.asList(
                OpcaoFornecedor.CEP,
                OpcaoFornecedor.DADOS,
                OpcaoFornecedor.CONTATOS,
                OpcaoFornecedor.ENDERECO,
                OpcaoFornecedor.CONDICAO_PAGAMENTO,
                OpcaoFornecedor.PAGAR_FORNECEDOR,
                OpcaoFornecedor.SITUACAO_CADASTRO,
                OpcaoFornecedor.TIPO_EMPRESA,
                OpcaoFornecedor.PRODUTO_FORNECEDOR,
                OpcaoFornecedor.OBSERVACAO
        ));
    }

    @Override
    public Set<OpcaoCliente> getOpcoesDisponiveisCliente() {
        return new HashSet<>(Arrays.asList(
                OpcaoCliente.DADOS,
                OpcaoCliente.ENDERECO,
                OpcaoCliente.CONTATOS,
                OpcaoCliente.BLOQUEADO,
                OpcaoCliente.SITUACAO_CADASTRO,
                OpcaoCliente.DATA_CADASTRO,
                OpcaoCliente.DATA_NASCIMENTO,
                OpcaoCliente.VENCIMENTO_ROTATIVO,
                OpcaoCliente.RECEBER_CREDITOROTATIVO,
                OpcaoCliente.OUTRAS_RECEITAS));
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select distinct\n"
                    + "	codcst||'-'||round(aliqicms,2)||'-'||round(aliqredicms,2) as id,\n"
                    + "	codcst||'-'||round(aliqicms,2)||'-'||round(aliqredicms,2) as descricao,\n"
                    + "	codcst cst_icms,\n"
                    + "	aliqicms aliq_icms,\n"
                    + "	aliqredicms red_icms	\n"
                    + "from\n"
                    + "	classfiscal\n"
                    + "order by 1"
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(
                            rs.getString("id"),
                            rs.getString("descricao"),
                            rs.getInt("cst_icms"),
                            rs.getDouble("aliq_icms"),
                            rs.getDouble("red_icms"))
                    );
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + " m1.coddepto cod_m1,\n"
                    + " m1.nome desc_m1,\n"
                    + " m2.codgrupo cod_m2,\n"
                    + " m2.descricao desc_m2,\n"
                    + " m3.codsubgrupo cod_m3,\n"
                    + " m3.descricao desc_m3\n"
                    + "from departamento m1\n"
                    + " inner join grupoprod m2 on m2.coddepto = m1.coddepto\n"
                    + " inner join subgrupo m3 on m3.codgrupo = m2.codgrupo\n"
                    + "order by m1.coddepto, m2.codgrupo, m3.codsubgrupo"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("cod_m1"));
                    imp.setMerc1Descricao(rst.getString("desc_m1"));
                    imp.setMerc2ID(rst.getString("cod_m2"));
                    imp.setMerc2Descricao(rst.getString("desc_m2"));
                    imp.setMerc3ID(rst.getString("cod_m3"));
                    imp.setMerc3Descricao(rst.getString("desc_m3"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	codsimilar,\n"
                    + "	descricao\n"
                    + "from \n"
                    + "	simprod")) {
                while (rs.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());

                    imp.setImportId(rs.getString("codsimilar"));
                    imp.setDescricao(rs.getString("descricao"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + " codproduto, \n"
                    + " codean, \n"
                    + " quantidade \n"
                    + "from produtoean \n"
                    + "order by codproduto"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("codproduto"));
                    imp.setEan(rst.getString("codean"));
                    imp.setQtdEmbalagem(rst.getInt("quantidade"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "p.codproduto,\n"
                    + "p.descricao,\n"
                    + "p.descricaofiscal,\n"
                    + "pe.codean ean,\n"
                    + "p.coddepto,\n"
                    + "p.codgrupo,\n"
                    + "p.codsubgrupo,\n"
                    + "p.codsimilar,\n"
                    + "p.estminimo,\n"
                    + "p.estmaximo,\n"
                    + "est.sldatual estoque,\n"
                    + "p.pesoliq,\n"
                    + "p.pesobruto,\n"
                    + "p.pesado,\n"
                    + "p.foralinha,\n"
                    + "p.qtdeetiq,\n"
                    + "p.diasvalidade,\n"
                    + "p.pesounid,\n"
                    + "p.vasilhame,\n"
                    + "p.codvasilhame,\n"
                    + "p.qtdeunidadepreco qtde_volume,\n"
                    + "case when vol.descricao = 'LITRO' then 'LT' else vol.sigla end tipo_volume,\n"
                    + "p.codfamilia,\n"
                    + "p.custotab,\n"
                    + "p.custorep,\n"
                    + "p.precoatc,\n"
                    + "p.precovrj,\n"
                    + "p.margematc,\n"
                    + "p.margemvrj,\n"
                    + "p.datainclusao,\n"
                    + "p.custorep,\n"
                    + "p.altura,\n"
                    + "p.largura,\n"
                    + "p.enviarecommerce,\n"
                    + "p.comprimento,\n"
                    + "p.cest,\n"
                    + "case when p.pesado = 'S' and p.pesounid = 'P' then 'KG' else uv.sigla end as embalagem,\n"
                    + "ev.quantidade as qtdembalagem,\n"
                    + "uc.sigla emb_compra,\n"
                    + "ec.quantidade qtde_compra,\n"
                    + "pcs.codcst cstpiscofinssaida,\n"
                    + "pce.codcst cstpiscofinsentrada,\n"
                    + "p.natreceita,\n"
                    + "ncm.codigoncm,\n"
                    + "cf.codcst||'-'||round(cf.aliqicms,2)||'-'||round(cf.aliqredicms,2) id_icms,\n"
                    + "cf.descricao icmsdesc,\n"
                    + "cf.codcst as icmscst,\n"
                    + "cf.aliqicms as icmsaliq,\n"
                    + "cf.aliqredicms as icmsred\n"
                    + "from produto p \n"
                    + "left join produtoean pe on p.codproduto = pe.codproduto\n"
                    + "left join produtoestab est on p.codproduto = est.codproduto and est.codestabelec = " + getLojaOrigem() + "\n"
                    + "left join embalagem ev on ev.codembal = p.codembalvda\n"
                    + "left join unidade uv on uv.codunidade = ev.codunidade\n"
                    + "left join embalagem ec on ec.codembal = p.codembalcpa\n"
                    + "left join unidade uc on ec.codunidade = uc.codunidade\n"
                    + "left join unidade vol on vol.codunidade = p.codunidadepreco\n"
                    + "left join piscofins pcs on pcs.codpiscofins = p.codpiscofinssai\n"
                    + "left join piscofins pce on pce.codpiscofins = p.codpiscofinsent\n"
                    + "left join ncm on ncm.idncm = p.idncm\n"
                    + "left join classfiscal cf on cf.codcf = p.codcfpdv\n"
                    + "order by p.codproduto"
            )) {
                Map<Integer, vrimplantacao2.vo.cadastro.ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("codproduto"));
                    imp.seteBalanca("S".equals(rst.getString("pesado")));

                    imp.setEan(rst.getString("ean"));
                    imp.setValidade(rst.getInt("diasvalidade"));

                    ProdutoBalancaVO bal = produtosBalanca.get(Utils.stringToInt(imp.getImportId(), -2));

                    if (bal != null) {
                        imp.seteBalanca(true);
                        imp.setTipoEmbalagem("P".equals(bal.getPesavel()) ? "KG" : "UN");
                        imp.setValidade(bal.getValidade() > 1
                                ? bal.getValidade() : rst.getInt("diasvalidade"));
                        imp.setEan(imp.getImportId());
                    }

                    imp.setTipoEmbalagemCotacao(rst.getString("emb_compra"));
                    imp.setQtdEmbalagemCotacao(rst.getInt("qtde_compra"));
                    imp.setTipoEmbalagem(rst.getString("embalagem"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    imp.setDescricaoCompleta(rst.getString("descricaofiscal"));
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setCodMercadologico1(rst.getString("coddepto"));
                    imp.setCodMercadologico2(rst.getString("codgrupo"));
                    imp.setCodMercadologico3(rst.getString("codsubgrupo"));
                    imp.setIdFamiliaProduto(rst.getString("codsimilar"));
                    imp.setTipoEmbalagemVolume(rst.getString("tipo_volume"));
                    imp.setVolume(rst.getDouble("qtde_volume"));
                    imp.setEstoqueMinimo(rst.getDouble("estminimo"));
                    imp.setEstoqueMaximo(rst.getDouble("estmaximo"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setPesoLiquido(rst.getDouble("pesoliq"));
                    imp.setPesoBruto(rst.getDouble("pesobruto"));
                    imp.setPrecovenda(rst.getDouble("precovrj"));
                    imp.setCustoComImposto(rst.getDouble("custorep"));
                    imp.setCustoSemImposto(rst.getDouble("custotab"));
                    imp.setMargem(rst.getDouble("margemvrj"));
                    imp.setDataCadastro(rst.getDate("datainclusao"));
                    imp.setNcm(rst.getString("codigoncm"));
                    imp.setCest(rst.getString("cest"));

                    imp.setIcmsDebitoId(rst.getString("id_icms"));
                    imp.setIcmsCreditoId(rst.getString("id_icms"));
                    imp.setIcmsConsumidorId(imp.getIcmsDebitoId());
                    imp.setIcmsDebitoForaEstadoNfId(imp.getIcmsDebitoId());
                    imp.setIcmsDebitoForaEstadoId(imp.getIcmsDebitoId());
                    imp.setIcmsCreditoForaEstadoId(imp.getIcmsDebitoId());

                    imp.setPiscofinsCstDebito(rst.getString("cstpiscofinssaida"));
                    imp.setPiscofinsCstCredito(rst.getString("cstpiscofinsentrada"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("natreceita"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos(OpcaoProduto opcao) throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        if (opcao == OpcaoProduto.ESTOQUE) {
            try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "with estoque as (\n"
                        + "	select\n"
                        + "	  	max(datalog) as data,\n"
                        + "	  	codproduto\n"
                        + "	from\n"
                        + "		produtoestab\n"
                        + "	where\n"
                        + "		codestabelec = " + getLojaOrigem() + " group by codproduto)\n"
                        + "select\n"
                        + "	pe.codproduto, pe.sldatual estoque, pe.datalog \n"
                        + "from\n"
                        + "	produtoestab pe\n"
                        + "inner join estoque e on e.codproduto = pe.codproduto and pe.datalog = e.data\n"
                        + "where\n"
                        + "	codestabelec = " + getLojaOrigem()
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());

                        imp.setImportId(rst.getString("codproduto"));
                        imp.setEstoque(rst.getDouble("estoque"));

                        result.add(imp);
                    }
                }

                return result;
            }
        }

        return null;
    }

    @Override
    public List<ReceitaBalancaIMP> getReceitaBalanca(Set<OpcaoReceitaBalanca> opt) throws Exception {

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	r.codreceita id,\n"
                    + "	p.descricao descritivo,\n"
                    + "	componentes receita,\n"
                    + "	p.codproduto produto\n"
                    + "from\n"
                    + "	produto p\n"
                    + "	left join receita r on p.codreceita = r.codreceita \n"
                    + "order by 1"
            )) {
                Map<String, ReceitaBalancaIMP> receitas = new HashMap<>();

                while (rst.next()) {
                    ReceitaBalancaIMP imp = receitas.get(rst.getString("id"));

                    if (imp == null) {
                        imp = new ReceitaBalancaIMP();
                        imp.setId(rst.getString("id"));
                        imp.setDescricao(rst.getString("descritivo"));
                        imp.setReceita(rst.getString("receita"));
                        receitas.put(imp.getId(), imp);
                    }

                    imp.getProdutos().add(rst.getString("produto"));
                }

                return new ArrayList<>(receitas.values());
            }
        }
    }

    @Override
    public List<ConvenioEmpresaIMP> getConvenioEmpresa() throws Exception {
        List<ConvenioEmpresaIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	codcliente id,\n"
                    + "	case when cpfcnpj is null then codcliente::varchar else cpfcnpj end cnpj,\n"
                    + "	rgie ie,\n"
                    + "	razaosocial razao,\n"
                    + "	enderfat endereco,\n"
                    + "	numerofat numero,\n"
                    + "	complementofat complemento,\n"
                    + "	bairrofat bairro,\n"
                    + "	codcidadefat cidade,\n"
                    + "	uffat uf,\n"
                    + "	cepfat cep,\n"
                    + "	fonefat telefone,\n"
                    + "	observacao\n"
                    + "from\n"
                    + "	cliente c\n"
                    + "where\n"
                    + "	convenio = 'S'"
            )) {
                while (rst.next()) {
                    ConvenioEmpresaIMP imp = new ConvenioEmpresaIMP();

                    imp.setId(rst.getString("id"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setInscricaoEstadual(rst.getString("ie"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setTelefone(rst.getString("telefone"));
                    imp.setObservacoes(rst.getString("observacao"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ConveniadoIMP> getConveniado() throws Exception {
        List<ConveniadoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	codcliente id_cliente,\n"
                    + "	nome,\n"
                    + "	codempresa id_empresa,\n"
                    + "	cpfcnpj cpf_cnpj,\n"
                    + "	case when codstatus = 1 then 1 else 0 end status,\n"
                    + " case when codstatus = 3 then 1 else 0 end bloqueado,\n"
                    + "	coalesce(limite1, 0) as limite,\n"
                    + "	observacao\n"
                    + "from\n"
                    + "	cliente\n"
                    + "where\n"
                    + "	codempresa is not null\n"
                    + "order by 1"
            )) {
                while (rs.next()) {
                    ConveniadoIMP imp = new ConveniadoIMP();
                    imp.setId(rs.getString("id_cliente"));
                    imp.setNome(rs.getString("nome"));
                    imp.setIdEmpresa(rs.getString("id_empresa"));
                    imp.setCnpj(rs.getString("cpf_cnpj"));
                    imp.setConvenioLimite(rs.getDouble("limite"));
                    imp.setLojaCadastro(Integer.parseInt(getLojaOrigem()));
                    imp.setBloqueado(rs.getBoolean("bloqueado"));
                    imp.setSituacaoCadastro(rs.getInt("status") == 1 ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    imp.setObservacao(rs.getString("observacao"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ConvenioTransacaoIMP> getConvenioTransacao() throws Exception {
        List<ConvenioTransacaoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	codlancto id,\n"
                    + "	codparceiro id_conveniado,\n"
                    + "	case when numnotafis is null then codlancto else numnotafis end documento,\n"
                    + "	dtemissao emissao,\n"
                    + "	valorliquido valor,\n"
                    + "	l.observacao\n"
                    + "from\n"
                    + "	lancamento l\n"
                    + "	join cliente c on c.codcliente = l.codparceiro and c.codempresa is not null\n"
                    + "where\n"
                    + "	codestabelec = " + getLojaOrigem() + "\n"
                    + "	and status = 'A'\n"
                    + " and tipoparceiro = 'C'\n"
                    + "	and codfinaliz = '006'\n"
                    + "order by codlancto"
            )) {
                while (rst.next()) {
                    ConvenioTransacaoIMP imp = new ConvenioTransacaoIMP();

                    imp.setId(rst.getString("id"));
                    imp.setIdConveniado(rst.getString("id_conveniado"));
                    imp.setNumeroCupom(rst.getString("documento"));
                    imp.setDataHora(rst.getTimestamp("emissao"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setObservacao(rst.getString("observacao"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    ""
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("codfornec"));
                    imp.setCnpj_cpf(rst.getString("cpfcnpj"));
                    imp.setRazao(rst.getString("razaosocial"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setIe_rg(rst.getString("rgie"));
                    imp.setInsc_municipal(rst.getString("inscmunicipal"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setCep(rst.getString("cep"));
                    imp.setMunicipio(rst.getString("nomecidade"));
                    imp.setIbge_municipio(rst.getInt("cidadeibge"));
                    imp.setUf(rst.getString("uf"));
                    imp.setDatacadastro(rst.getDate("datainclusao"));
                    imp.setTel_principal(rst.getString("fone"));
                    imp.setObservacao(imp.getObservacao() + rst.getString("observacao"));

                    if ((rst.getString("contato1") != null)
                            && (!rst.getString("contato1").trim().isEmpty())) {
                        imp.addContato(
                                rst.getString("contato1"),
                                rst.getString("fone1"),
                                null,
                                TipoContato.COMERCIAL,
                                rst.getString("email1")
                        );
                    }

                    if ((rst.getString("contato2") != null)
                            && (!rst.getString("contato2").trim().isEmpty())) {
                        imp.addContato(
                                rst.getString("contato2"),
                                rst.getString("fone2"),
                                null,
                                TipoContato.COMERCIAL,
                                rst.getString("email2")
                        );
                    }

                    if ((rst.getString("contato3") != null)
                            && (!rst.getString("contato3").trim().isEmpty())) {
                        imp.addContato(
                                rst.getString("contato3"),
                                rst.getString("fone3"),
                                null,
                                TipoContato.COMERCIAL,
                                rst.getString("email3")
                        );
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
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	codfornec,\n"
                    + "	pf.codproduto,\n"
                    + "	reffornec,\n"
                    + "	round(e.quantidade,2) quantidade\n"
                    + "from\n"
                    + "	produto p\n"
                    + "	join embalagem e on p.codembalcpa = e.codembal\n"
                    + "	join prodfornec pf on p.codproduto = pf.codproduto\n"
                    + "order by 1,2"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setIdProduto(rst.getString("codproduto"));
                    imp.setIdFornecedor(rst.getString("codfornec"));
                    imp.setCodigoExterno(rst.getString("reffornec"));
                    imp.setQtdEmbalagem(rst.getDouble("quantidade"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    // Outras Receitas de Fornecedores
    @Override
    public List<ContaReceberIMP> getContasReceber(Set<OpcaoContaReceber> opt) throws Exception {
        List<ContaReceberIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	codlancto id,\n"
                    + "	codparceiro id_fornecedor,\n"
                    + "	dtemissao emissao,\n"
                    + "	valorliquido valor,\n"
                    + "	dtvencto vencimento,\n"
                    + "	observacao\n"
                    + "from\n"
                    + "	lancamento\n"
                    + "where\n"
                    + "	codestabelec = " + getLojaOrigem() + "\n"
                    + "	and tipoparceiro = 'F'\n"
                    + "	and pagrec = 'R'\n"
                    + "	and status = 'A'\n"
                    + "order by codlancto"
            )) {
                while (rs.next()) {
                    ContaReceberIMP imp = new ContaReceberIMP();

                    imp.setId(rs.getString("id"));
                    imp.setIdFornecedor(rs.getString("id_fornecedor"));
                    imp.setDataEmissao(rs.getDate("emissao"));
                    imp.setDataVencimento(rs.getDate("vencimento"));
                    imp.setValor(rs.getDouble("valor"));
                    imp.setObservacao(rs.getString("observacao"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ContaPagarIMP> getContasPagar() throws Exception {
        List<ContaPagarIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	codlancto id,\n"
                    + "	codparceiro id_fornecedor,\n"
                    + "	numnotafis||'-'||parcela documento,\n"
                    + "	dtemissao emissao,\n"
                    + "	dtentrada entrada,\n"
                    + "	dtvencto vencimento,\n"
                    + "	valorparcela valor,\n"
                    + " case \n"
                    + "	  when valordescto > 0 then 'DESCONTO DE '||valordescto||' - '||observacao\n"
                    + "	  when valoracresc > 0 then ' ACRESCIMO DE '||valoracresc||' - '||observacao \n"
                    + "	else observacao\n"
                    + "	end observacao\n"
                    + "from\n"
                    + "	lancamento\n"
                    + "where\n"
                    + "	codestabelec = " + getLojaOrigem() + "\n"
                    + "	and tipoparceiro = 'F'\n"
                    + " and pagrec = 'P'\n"
                    + "	and status = 'A'\n"
                    + "order by codlancto"
            )) {
                while (rst.next()) {
                    ContaPagarIMP imp = new ContaPagarIMP();

                    imp.setId(rst.getString("id"));
                    imp.setIdFornecedor(rst.getString("id_fornecedor"));
                    imp.setNumeroDocumento(rst.getString("documento"));
                    imp.setDataEmissao(rst.getDate("emissao"));
                    imp.setDataEntrada(rst.getDate("entrada"));
                    imp.addVencimento(rst.getDate("vencimento"), rst.getDouble("valor"), rst.getString("observacao"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	ncad_cgcocpf_2 id,\n"
                    + "	case\n"
                    + "		when length(ncad_cgcocpf_2::varchar) > 9 then ncad_cgcocpf_2::varchar\n"
                    + "		else replace(replace (case\n"
                    + "			when ncad_fantasi_2 like 'CPF%' then ncad_fantasi_2\n"
                    + "		end,\n"
                    + "		'CPF',''),':','')\n"
                    + "	end cpf_cnpj,\n"
                    + "	trim(replace (case when ncad_fantasi_2 like 'RG%' then ncad_fantasi_2 end, 'RG', '')) rg_ie,\n"
                    + "	ncad_nomecli_2 razao,\n"
                    + "	ncad_endere1_2 endereco,\n"
                    + "	ncad_numeros_2 numero,\n"
                    + "	ncad_complem_2 complemento,\n"
                    + "	ncad_bairros_2 bairro,\n"
                    + "	ncad_numecep_2 cep,\n"
                    + "	ncad_siglest_2 uf,\n"
                    + "	ncad_dddfone_2 ddd_fone,\n"
                    + "	ncad_telefon_2 fone,\n"
                    + "	ncad_dddcelu_2 ddd_celular,\n"
                    + "	ncad_celular_2 celular,\n"
                    + "	case when ncad_credsus_2 = 2 then 1 else 0 end bloqueado,\n"
                    + "	ncad_datsusp_2 data_bloqueio,\n"
                    + "	ncad_sexocli_2 sexo,\n"
                    + "	ncad_datnasc_2 data_nasc,\n"
                    + "	case ncad_estcivi_2\n"
                    + "	  when 'S' then 1\n"
                    + "	  when 'C' then 2\n"
                    + "	  when 'D' then 6\n"
                    + "	  when 'V' then 3\n"
                    + "	  else 1\n"
                    + "	end estadocivil,\n"
                    + "	ncad_conjuge_2 conjuge,\n"
                    + "	ncad_nasconj_2 nasc_conjuge,\n"
                    + "	ncad_datincl_2 data_cadastro,\n"
                    + "	'COD_CONT: '||ncad_codcont_2 ||'  '||ncad_fantasi_2||'  '||ncad_referen_2 as observacao\n"
                    + "from\n"
                    + "	sincad\n"
                    + "where\n"
                    + "	ncad_tipocad_2 = 'C'\n"
                    + "order by ncad_cgcocpf_2"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rst.getString("id"));
                    imp.setCnpj(rst.getString("cpf_cnpj"));
                    imp.setInscricaoestadual(rst.getString("rg_ie"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("razao"));
                    
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setCep(rst.getString("cep"));
//                    imp.setMunicipioIBGE(rst.getInt("cidade"));
                    imp.setUf(rst.getString("uf"));

                    imp.setTelefone(rst.getString("ddd_fone") + rst.getString("fone"));
                    imp.setCelular(rst.getString("ddd_celular") + rst.getString("celular"));
                    imp.setBloqueado("1".equals(rst.getString("bloqueado")));
                    imp.setAtivo("0".equals(rst.getString("bloqueado")));
                    
                    imp.setDataNascimento(rst.getDate("data_nasc"));
                    if ((rst.getString("sexo") != null)
                            && (!rst.getString("sexo").trim().isEmpty())) {
                        if ("M".equals(rst.getString("sexo"))) {
                            imp.setSexo(TipoSexo.MASCULINO);
                        } else {
                            imp.setSexo(TipoSexo.FEMININO);
                        }
                    } else {
                        imp.setSexo(TipoSexo.MASCULINO);
                    }

                    imp.setEstadoCivil(rst.getString("estadocivil"));
                    imp.setNomeConjuge(rst.getString("conjuge"));
                    imp.setDataNascimentoConjuge(rst.getDate("nasc_conjuge"));
                    imp.setDataCadastro(rst.getDate("data_cadastro"));
//                    imp.setValorLimite(rst.getDouble("limite"));
                    
                    imp.setObservacao(rst.getString("observacao"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "l.codlancto as id,\n"
                    + "l.parcela,\n"
                    + "l.codparceiro as codcliente,\n"
                    + "l.valorparcela as valor,\n"
                    + "l.valorliquido,\n"
                    + "l.valorjuros,\n"
                    + "l.valordescto,\n"
                    + "l.valoracresc,\n"
                    + "l.dtemissao as dataemissao,\n"
                    + "l.dtvencto as datavencimento,\n"
                    + "l.numnotafis as numerocupom\n"
                    + "from lancamento l\n"
                    + "where l.pagrec = 'R' \n"
                    + "and l.status = 'A' \n"
                    + "and l.tipoparceiro = 'C'\n"
                    + "and l.codestabelec = " + getLojaOrigem()
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();

                    imp.setId(rst.getString("id"));
                    imp.setIdCliente(rst.getString("codcliente"));
                    imp.setDataEmissao(rst.getDate("dataemissao"));
                    imp.setDataVencimento(rst.getDate("datavencimento"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setNumeroCupom(rst.getString("numerocupom"));
                    imp.setJuros(rst.getDouble("valorjuros"));

                    result.add(imp);
                }
            }
            return result;
        }
    }

    @Override
    public List<ChequeIMP> getCheques() throws Exception {
        List<ChequeIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	codcheque id,\n"
                    + "	dtemissao emissao,\n"
                    + "	valorcheque valor,\n"
                    + "	numcheque nro_cheque,\n"
                    + "	b.codoficial banco,\n"
                    + "	b.agencia,\n"
                    + "	f.rgie,\n"
                    + "	f.cpfcnpj,\n"
                    + "	nominal nome,\n"
                    + "	f.fone1 telefone,\n"
                    + "	c.observacao\n"
                    + "from\n"
                    + "	cheque c\n"
                    + "	join banco b on b.codbanco = c.codbanco\n"
                    + "	left join fornecedor f on f.razaosocial like c.nominal\n"
                    + "where\n"
                    + "	c.codestabelec = " + getLojaOrigem()
            )) {
                while (rst.next()) {
                    ChequeIMP imp = new ChequeIMP();

                    imp.setId(rst.getString("id"));
                    imp.setDate(rst.getDate("emissao"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setNumeroCheque(rst.getString("nro_cheque"));
                    imp.setBanco(rst.getInt("banco"));
                    imp.setAgencia(rst.getString("agencia"));
                    imp.setRg(rst.getString("rgie"));
                    imp.setCpf(rst.getString("cpfcnpj"));
                    imp.setNome(rst.getString("nome"));
                    imp.setTelefone(rst.getString("telefone"));
                    imp.setObservacao(rst.getString("observacao"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    private List<ProdutoAutomacaoVO> getDigitoVerificador() throws Exception {
        List<ProdutoAutomacaoVO> result = new ArrayList<>();

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "  v.id,\n"
                    + "  v.codigobarras,\n"
                    + "  p.id_tipoembalagem \n"
                    + "from implantacao.produto_verificador v\n"
                    + "join produto p on p.id = v.id"
            )) {
                while (rst.next()) {
                    ProdutoAutomacaoVO vo = new ProdutoAutomacaoVO();
                    vo.setIdproduto(rst.getInt("id"));
                    vo.setIdTipoEmbalagem(rst.getInt("id_tipoembalagem"));
                    vo.setCodigoBarras(gerarEan13(Long.parseLong(rst.getString("id")), true));
                    result.add(vo);
                }
            }
        }

        return result;
    }

    public void importarDigitoVerificador() throws Exception {
        List<ProdutoAutomacaoVO> result = new ArrayList<>();
        ProgressBar.setStatus("Carregar Produtos...");
        try {
            result = getDigitoVerificador();

            if (!result.isEmpty()) {
                gravarCodigoBarrasDigitoVerificador(result);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    private void gravarCodigoBarrasDigitoVerificador(List<ProdutoAutomacaoVO> vo) throws Exception {

        Conexao.begin();
        Statement stm, stm2 = null;
        ResultSet rst = null;

        stm = Conexao.createStatement();
        stm2 = Conexao.createStatement();

        String sql = "";
        ProgressBar.setStatus("Gravando Código de Barras...");
        ProgressBar.setMaximum(vo.size());

        try {

            for (ProdutoAutomacaoVO i_vo : vo) {

                sql = "select codigobarras from produtoautomacao where codigobarras = " + i_vo.getCodigoBarras();
                rst = stm.executeQuery(sql);

                if (!rst.next()) {
                    sql = "insert into produtoautomacao ("
                            + "id_produto, "
                            + "codigobarras, "
                            + "id_tipoembalagem, "
                            + "qtdembalagem) "
                            + "values ("
                            + i_vo.getIdproduto() + ", "
                            + i_vo.getCodigoBarras() + ", "
                            + i_vo.getIdTipoEmbalagem() + ", 1);";
                    stm2.execute(sql);
                } else {
                    sql = "insert into implantacao.produtonaogerado ("
                            + "id_produto, "
                            + "codigobarras) "
                            + "values ("
                            + i_vo.getIdproduto() + ", "
                            + i_vo.getCodigoBarras() + ");";
                    stm2.execute(sql);
                }
                ProgressBar.next();
            }

            stm.close();
            stm2.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public long gerarEan13(long i_codigo, boolean i_digito) throws Exception {
        String codigo = String.format("%012d", i_codigo);

        int somaPar = 0;
        int somaImpar = 0;

        for (int i = 0; i < 12; i += 2) {
            somaImpar += Integer.parseInt(String.valueOf(codigo.charAt(i)));
            somaPar += Integer.parseInt(String.valueOf(codigo.charAt(i + 1)));
        }

        int soma = somaImpar + (3 * somaPar);
        int digito = 0;
        boolean verifica = false;
        int calculo = 0;

        do {
            calculo = soma % 10;

            if (calculo != 0) {
                digito += 1;
                soma += 1;
            }
        } while (calculo != 0);

        if (i_digito) {
            return Long.parseLong(codigo + digito);
        } else {
            return Long.parseLong(codigo);
        }
    }

    private Date dataInicioVenda;
    private Date dataTerminoVenda;

    @Override
    public Iterator<VendaIMP> getVendaIterator() throws Exception {
        return new SincDAO.VendaIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new SincDAO.VendaItemIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
    }

    public void setDataInicioVenda(Date dataInicioVenda) {
        this.dataInicioVenda = dataInicioVenda;
    }

    public void setDataTerminoVenda(Date dataTerminoVenda) {
        this.dataTerminoVenda = dataTerminoVenda;
    }

    private static class VendaIterator implements Iterator<VendaIMP> {

        public final static SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");

        private Statement stm = ConexaoPostgres.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaIMP next;
        private Set<String> uk = new HashSet<>();

        private void obterNext() {
            try {
                SimpleDateFormat timestampDate = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat timestamp = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaIMP();
                        String id = rst.getString("id_venda");
                        if (!uk.add(id)) {
                            LOG.warning("Venda " + id + " já existe na listagem");
                        }
                        next.setId(id);
                        next.setNumeroCupom(Utils.stringToInt(rst.getString("numerocupom")));
                        next.setEcf(Utils.stringToInt(rst.getString("ecf")));
                        next.setData(rst.getDate("data"));

                        String horaInicio = timestampDate.format(rst.getDate("data")) + " " + rst.getString("hora");
                        String horaTermino = timestampDate.format(rst.getDate("data")) + " " + rst.getString("hora");
                        next.setHoraInicio(timestamp.parse(horaInicio));
                        next.setHoraTermino(timestamp.parse(horaTermino));
                        next.setValorDesconto(rst.getDouble("desconto"));
                        next.setValorAcrescimo(rst.getDouble("acrescimo"));
                        next.setSubTotalImpressora(rst.getDouble("total"));
                        next.setCancelado(rst.getBoolean("cancelado"));
                    }
                }
            } catch (SQLException | ParseException ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {

            String strDataInicio = new SimpleDateFormat("yyyy-MM-dd").format(dataInicio);
            String strDataTermino = new SimpleDateFormat("yyyy-MM-dd").format(dataTermino);
            this.sql
                    = "select\n"
                    + "	idcupom id_venda,\n"
                    + "	case when seqecf is null or seqecf = '000000' then idcupom::varchar else seqecf end numerocupom,\n"
                    + "	numeroecf ecf,\n"
                    + "	dtmovto as data,\n"
                    + "	hrmovto hora,\n"
                    + "	totaldesconto desconto,\n"
                    + "	totalacrescimo acrescimo,\n"
                    + "	totalliquido total,\n"
                    + "	case when status = 'C' then 1 else 0 end cancelado\n"
                    + "from\n"
                    + "	cupom v\n"
                    + "where\n"
                    + "	codestabelec = " + idLojaCliente + "\n"
                    + "	and dtmovto between '" + strDataInicio + "' and '" + strDataTermino + "'\n"
                    + "order by 4,5";
            LOG.log(Level.FINE, "SQL da venda: " + sql);
            rst = stm.executeQuery(sql);
        }

        @Override
        public boolean hasNext() {
            obterNext();
            return next != null;
        }

        @Override
        public VendaIMP next() {
            obterNext();
            VendaIMP result = next;
            next = null;
            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }
    }

    private static class VendaItemIterator implements Iterator<VendaItemIMP> {

        private Statement stm = ConexaoPostgres.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaItemIMP next;

        private void obterNext() {
            try {
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaItemIMP();

                        next.setVenda(rst.getString("id_venda"));
                        next.setId(rst.getString("id_item"));
                        next.setProduto(rst.getString("produto"));
                        next.setUnidadeMedida(rst.getString("unidade"));
                        next.setCodigoBarras(rst.getString("codigobarras"));
                        next.setDescricaoReduzida(rst.getString("descricao"));
                        next.setQuantidade(rst.getDouble("quantidade"));
                        next.setPrecoVenda(rst.getDouble("precovenda"));
                        next.setTotalBruto(rst.getDouble("total"));
                        next.setValorAcrescimo(rst.getDouble("acrescimo"));
                        next.setValorDesconto(rst.getDouble("desconto"));
                        next.setCancelado(rst.getBoolean("cancelado"));

                    }
                }
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaItemIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {
            this.sql
                    = "with ean as(\n"
                    + "select distinct on (codproduto)\n"
                    + "	codproduto,\n"
                    + "	codean\n"
                    + "from\n"
                    + "	produtoean\n"
                    + "group by codproduto, codean)\n"
                    + "select\n"
                    + "	v.idcupom id_venda,\n"
                    + "	vi.codmovimento id_item,\n"
                    + "	vi.codproduto produto,\n"
                    + "	u.sigla unidade,\n"
                    + "	codean codigobarras,\n"
                    + "	p.descricao,\n"
                    + "	vi.quantidade,\n"
                    + "	vi.preco precovenda,\n"
                    + "	vi.valortotal total,\n"
                    + "	vi.acrescimo,\n"
                    + "	vi.desconto,\n"
                    + "	case when vi.status = 'C' then 1 else 0 end cancelado\n"
                    + "from\n"
                    + "	cupom v\n"
                    + "	join itcupom vi on v.idcupom = vi.idcupom\n"
                    + "	join produto p on p.codproduto = vi.codproduto\n"
                    + "	join ean on ean.codproduto = p.codproduto\n"
                    + "	join movimento m on vi.codmovimento = m.codmovimento\n"
                    + "	join unidade u on u.codunidade = m.codunidade\n"
                    + "where\n"
                    + "	v.codestabelec = " + idLojaCliente + "\n"
                    + "	and v.dtmovto between '" + VendaIterator.FORMAT.format(dataInicio) + "' AND '" + VendaIterator.FORMAT.format(dataTermino) + "'\n"
                    + "order by 1, 2";
            LOG.log(Level.FINE, "SQL da venda: " + sql);
            rst = stm.executeQuery(sql);
        }

        @Override
        public boolean hasNext() {
            obterNext();
            return next != null;
        }

        @Override
        public VendaItemIMP next() {
            obterNext();
            VendaItemIMP result = next;
            next = null;
            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }
    }
}
