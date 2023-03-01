package vrimplantacao2_5.dao.sistema;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import static vr.core.utils.StringUtils.LOG;
import vrframework.classe.ProgressBar;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.parametro.Parametros;
import vrimplantacao2.vo.cadastro.financeiro.contareceber.OpcaoContaReceber;
import vrimplantacao2.vo.cadastro.oferta.TipoOfertaVO;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoEstadoCivil;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.enums.TipoVistaPrazo;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.ContaPagarVencimentoIMP;
import vrimplantacao2.vo.importacao.ContaReceberIMP;
import vrimplantacao2.vo.importacao.ConveniadoIMP;
import vrimplantacao2.vo.importacao.ConvenioEmpresaIMP;
import vrimplantacao2.vo.importacao.ConvenioTransacaoIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.OfertaIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;
import vrimplantacao2_5.dao.conexao.ConexaoMySQL;
import vrimplantacao2_5.dao.conexao.ConexaoOracle;
import vrimplantacao2_5.dao.conexao.ConexaoPostgres;

/**
 *
 * @author Wagner
 */
public class PrimeDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "Prime";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                OpcaoProduto.FAMILIA,
                OpcaoProduto.FAMILIA_PRODUTO,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                OpcaoProduto.QTD_EMBALAGEM_EAN,
                OpcaoProduto.ATIVO,
                OpcaoProduto.PESO_BRUTO,
                OpcaoProduto.PESO_LIQUIDO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.ESTOQUE_MINIMO,
                OpcaoProduto.MARGEM,
                OpcaoProduto.VENDA_PDV,
                OpcaoProduto.PRECO,
                OpcaoProduto.CUSTO,
                OpcaoProduto.CUSTO_COM_IMPOSTO,
                OpcaoProduto.CUSTO_SEM_IMPOSTO,
                OpcaoProduto.NCM,
                OpcaoProduto.EXCECAO,
                OpcaoProduto.CEST,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.NATUREZA_RECEITA,
                OpcaoProduto.ICMS,
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.MAPA_TRIBUTACAO,
                OpcaoProduto.FORCAR_ATUALIZACAO,
                OpcaoProduto.OFERTA
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
                OpcaoFornecedor.INSCRICAO_MUNICIPAL,
                OpcaoFornecedor.PAGAR_FORNECEDOR,
                OpcaoFornecedor.PRODUTO_FORNECEDOR,
                OpcaoFornecedor.OUTRAS_RECEITAS));
    }

    @Override
    public Set<OpcaoCliente> getOpcoesDisponiveisCliente() {
        return new HashSet<>(Arrays.asList(
                OpcaoCliente.CONTATOS,
                OpcaoCliente.DADOS,
                OpcaoCliente.DATA_CADASTRO,
                OpcaoCliente.DATA_NASCIMENTO,
                OpcaoCliente.ENDERECO,
                OpcaoCliente.RECEBER_CREDITOROTATIVO,
                OpcaoCliente.VALOR_LIMITE,
                OpcaoCliente.CONVENIO_EMPRESA,
                OpcaoCliente.CONVENIO_CONVENIADO,
                OpcaoCliente.CONVENIO_TRANSACAO,
                OpcaoCliente.RECEBER_CHEQUE,
                OpcaoCliente.FANTASIA,
                OpcaoCliente.RAZAO));
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "    'S-'||clat_simbicms as id,\n"
                    + "    clat_descricao as descricao,\n"
                    + "    clat_cst as cst,\n"
                    + "    clat_icms as icms,\n"
                    + "    clat_redbcicms as reducao\n"
                    + "from classtrib\n"
                    + "where clat_uf = '" + Parametros.get().getUfPadraoV2().getSigla() + "'\n"
                    + "and clat_es = 'S'\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(
                            rst.getString("id"),
                            rst.getString("descricao"),
                            rst.getInt("cst"),
                            rst.getDouble("icms"),
                            rst.getDouble("reducao")
                    ));
                }
            }

            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "    'E-'||clat_simbicms as id,\n"
                    + "    clat_descricao as descricao,\n"
                    + "    clat_cst as cst,\n"
                    + "    clat_icms as icms,\n"
                    + "    clat_redbcicms as reducao\n"
                    + "from classtrib\n"
                    + "where clat_uf = '" + Parametros.get().getUfPadraoV2().getSigla() + "'\n"
                    + "and clat_es = 'E'\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(
                            rst.getString("id"),
                            rst.getString("descricao"),
                            rst.getInt("cst"),
                            rst.getDouble("icms"),
                            rst.getDouble("reducao")
                    ));
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
                    "with n1 as (\n"
                    + "select \n"
                    + " cate_classificacao n1_id,\n"
                    + " cate_descricao descricao\n"
                    + "from categoriaprod n1\n"
                    + "where \n"
                    + " length(cate_classificacao) = 1\n"
                    + "), \n"
                    + "n2 as( \n"
                    + "select\n"
                    + " substring(cate_classificacao,1,1) n1_id,\n"
                    + " substring(cate_classificacao,2,3) n2_id,\n"
                    + " cate_descricao descricao\n"
                    + "from categoriaprod \n"
                    + "where \n"
                    + " length(cate_classificacao) = 3\n"
                    + " ),\n"
                    + " n3 as (\n"
                    + "select \n"
                    + " substring(cate_classificacao,1,1) n1_id,\n"
                    + " substring(cate_classificacao,2,2) n2_id,\n"
                    + " substring(cate_classificacao,4,5) n3_id,\n"
                    + " cate_descricao descricao\n"
                    + "from categoriaprod \n"
                    + "where \n"
                    + " length(cate_classificacao) = 5\n"
                    + " ),\n"
                    + " n4 as (\n"
                    + "select \n"
                    + " substring(cate_classificacao,1,1) n1_id,\n"
                    + " substring(cate_classificacao,2,2) n2_id,\n"
                    + " substring(substring(cate_classificacao,4,5),1,2) n3_id,\n"
                    + " substring(cate_classificacao,6,7) n4_id,\n"
                    + " cate_classificacao,\n"
                    + " cate_descricao descricao\n"
                    + "from categoriaprod \n"
                    + "where \n"
                    + " length(cate_classificacao) = 7\n"
                    + " )\n"
                    + " select \n"
                    + "  n1.n1_id,\n"
                    + "  n1.descricao desc1,\n"
                    + "  n2.n2_id,\n"
                    + "  n2.descricao desc2,\n"
                    + "  n3.n3_id,\n"
                    + "  n3.descricao desc3,\n"
                    + "  n4.n4_id,\n"
                    + "  n4.descricao desc4\n"
                    + " from n1\n"
                    + " left join n2 on n2.n1_id = n1.n1_id\n"
                    + " left join n3 on n3.n1_id = n2.n1_id and n3.n2_id = n2.n2_id\n"
                    + " left join n4 on n4.n1_id = n3.n1_id and n4.n2_id = n3.n2_id\n"
                    + " 	and n4.n3_id = n3.n3_id\n"
                    + " order by 1,3,5,7"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setMerc1ID(rst.getString("n1_id"));
                    imp.setMerc1Descricao(rst.getString("desc1"));
                    imp.setMerc2ID(rst.getString("n2_id"));
                    imp.setMerc2Descricao(rst.getString("desc2"));
                    imp.setMerc3ID(rst.getString("n3_id"));
                    imp.setMerc3Descricao(rst.getString("desc3"));
                    imp.setMerc4ID(rst.getString("n4_id"));
                    imp.setMerc4Descricao(rst.getString("desc4"));
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
                    + "    p.cadp_codigo as id,\n"
                    + "    p.cadp_balanca as balanca,\n"
                    + "    p.cadp_codigobarra as ean,\n"
                    + "    p.cadp_descricaounmedida as tipoembalagem,\n"
                    + "    p.cadp_situacao as situacaocadastro,\n"
                    + "    p.cadp_descricao as descricaocompleta,\n"
                    + "    p.cadp_descricaoreduzida as descricaoreduzida,\n"
                    + "    substring(g.cate_classificacao,1,1) n1,\n"
                    + "    substring(g.cate_classificacao,2,2) n2,\n"
                    + "    substring(substring(g.cate_classificacao,3,3),2,3) n3,\n"
                    + "    substring(substring(g.cate_classificacao,4,4),3,4) n4,\n"
                    + "    p.cadp_codcategoria,\n"
                    + "    p.cadp_categoria,\n"
                    + "    p.cadp_dtcadastro as datacadastro,\n"
                    + "    p.cadp_dtalteracao as dataalteracao,\n"
                    + "    p.cadp_codigoncm as ncm,\n"
                    + "    p.cadp_cest as cest,\n"
                    + "    p.cadp_cstpise as cstpisentrada,\n"
                    + "    p.cadp_cstpiss as cstpissaida,\n"
                    + "    'E-'||pe.cade_codclassificacaoe as icmsentrada,\n"
                    + "    cle.clat_cst as csticmsentrada,\n"
                    + "    cle.clat_icms as aliqicmsentrada,\n"
                    + "    cle.clat_redbcicms as redicmsentrada,\n"
                    + "    'S-'||pe.cade_codclassificacaos as icmssaida,\n"
                    + "    cls.clat_cst as csticmsentrada,\n"
                    + "    cls.clat_icms as aliqicmsentrada,\n"
                    + "    cls.clat_redbcicms as redicmsentrada,\n"
                    + "    pe.cade_estmin as estoqueminimo,\n"
                    + "    pe.cade_estmax as estoquemaximo,\n"
                    + "    pe.cade_qemb as qtdembalagem, \n"
                    + "    pe.cade_margemcontrib as margem,\n"
                    //                    + "    case \n"
                    //                    + "        pe.cade_oferta \n"
                    //                    + "	       when 'S' \n"
                    //                    + "	       then cade_prnormal\n"
                    //                    + "    else cade_prvenda end precovenda,\n"
                    + "    pe.cade_prvenda as precovenda,\n"
                    + "    pe.cade_ctnota as custo,\n"
                    + "    pe.cade_estoque2 as estoque, \n"
                    + "    p.cadp_vincpreco familiaid, \n"
                    + "    pe.cade_validade validade,\n"
                    + "    p.cadp_codreceita natreceita\n"
                    + "from cadprod p\n"
                    + "left join cadprodemp pe on pe.cade_codigo = p.cadp_codigo\n"
                    + "	and pe.cade_codempresa = '" + getLojaOrigem() + "'\n"
                    + "left join classtrib cle on cle.clat_codsimb = pe.cade_codclassificacaoe\n"
                    + "	and cle.clat_uf = '" + Parametros.get().getUfPadraoV2().getSigla() + "'\n"
                    + "	and cle.clat_es = 'E'\n"
                    + "left join classtrib cls on cls.clat_codsimb = pe.cade_codclassificacaos\n"
                    + "	and cls.clat_uf = '" + Parametros.get().getUfPadraoV2().getSigla() + "'\n"
                    + "	and cls.clat_es = 'S'\n"
                    + "left join categoriaprod g on g.cate_codigo  = p.cadp_codcategoria\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.seteBalanca(!"N".equals(rst.getString("balanca")));
                    imp.setEan(rst.getString("ean"));
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setDataAlteracao(rst.getDate("dataalteracao"));
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setEstoqueMaximo(rst.getDouble("estoquemaximo"));
                    imp.setValidade(rst.getInt("validade"));

                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setCustoComImposto(rst.getDouble("custo"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());

                    imp.setCodMercadologico1(rst.getString("n1"));
                    imp.setCodMercadologico2(rst.getString("n2"));
                    imp.setCodMercadologico3(rst.getString("n3"));
                    imp.setCodMercadologico4(rst.getString("n4"));

                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getString("cstpissaida"));
                    imp.setPiscofinsCstCredito(rst.getString("cstpisentrada"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("natreceita"));

                    imp.setIdFamiliaProduto(rst.getString("familiaid"));

                    imp.setIcmsDebitoId(rst.getString("icmssaida"));
                    imp.setIcmsDebitoForaEstadoId(imp.getIcmsDebitoId());
                    imp.setIcmsDebitoForaEstadoNfId(imp.getIcmsDebitoId());

                    imp.setIcmsCreditoId(rst.getString("icmsentrada"));
                    imp.setIcmsCreditoForaEstadoId(imp.getIcmsCreditoId());

                    imp.setIcmsConsumidorId(imp.getIcmsDebitoId());

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
                    + "    codb_codprod as idproduto,\n"
                    + "    codb_codbarra as ean\n"
                    + "from codigosbarra\n"
                    + "order by 1, 2"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("idproduto"));
                    imp.setEan(rst.getString("ean"));
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
                    "select \n"
                    + "   pren_codentidade fornecedorid,\n"
                    + "   pren_codprod produtoid,\n"
                    + "   pren_codigo codigoexterno\n"
                    + "from prodentidades"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setIdFornecedor(rst.getString("fornecedorid"));
                    imp.setIdProduto(rst.getString("produtoid"));
                    imp.setCodigoExterno(rst.getString("codigoexterno"));
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
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + " vipr_id id,\n"
                    + " vipr_descricao descricao \n"
                    + "from vincpreco"
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
    public List<OfertaIMP> getOfertas(Date dataTermino) throws Exception {
        List<OfertaIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	cade_codigo as idproduto, \n"
                    + "	cade_dtoferta as datainicio, \n"
                    + "	cade_dtoferta as datafim, \n"
                    + "	cade_prvenda as precooferta, \n"
                    + "	cade_prnormal as preconormal \n"
                    + "from cadprodemp \n"
                    + "where cade_oferta = 'S' \n"
                    + "and cade_dtoferta >= now() \n"
                    + "and cade_codempresa = '" + getLojaOrigem() + "'"
            )) {

                while (rs.next()) {
                    OfertaIMP imp = new OfertaIMP();
                    imp.setIdProduto(rs.getString("idproduto"));
                    imp.setDataInicio(rs.getDate("datainicio"));
                    imp.setDataFim(rs.getDate("datafim"));
                    imp.setPrecoNormal(rs.getDouble("preconormal"));
                    imp.setPrecoOferta(rs.getDouble("precooferta"));

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
                    "select \n"
                    + "	f.enti_codigo as id,\n"
                    + "	f.enti_razaosocial as razao,\n"
                    + "	f.enti_nome as fantasia,\n"
                    + "	f.enti_cnpjcpf as cnpj,\n"
                    + "	f.enti_inscricaoestadual as ie,\n"
                    + "	f.enti_inscricaomunicipal as im,\n"
                    + "	f.enti_fj as tipopessoa,\n"
                    + "	f.enti_endereco as endereco,\n"
                    + "	f.enti_numero as numero,\n"
                    + "	f.enti_complemento as complemento,\n"
                    + "	f.enti_bairro as bairro,\n"
                    + "	f.enti_municipio as municipio,\n"
                    + "	m.muni_ibge as municipioibge,\n"
                    + "	m.muni_nome as descricaomunicipio,\n"
                    + "	m.muni_uf as descricaouf,\n"
                    + "	f.enti_uf as uf,\n"
                    + "	f.enti_cep as cep,\n"
                    + "	f.enti_fone as telefone,\n"
                    + "	f.enti_email as email,\n"
                    + "	f.enti_fax as fax,\n"
                    + "	f.enti_celular as celular,\n"
                    + "	f.enti_datacadastro as datacadastro\n"
                    + "from entidades f \n"
                    + "left join municipios m on m.muni_codigo = f.enti_codmunicipio\n"
                    + "where enti_tipo like '%F%'\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("ie"));
                    imp.setInsc_municipal(rst.getString("im"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setIbge_municipio(rst.getInt("municipioibge"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setTel_principal(rst.getString("telefone"));
                    imp.setDatacadastro(rst.getDate("datacadastro"));
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
                    "select \n"
                    + "	c.enti_codigo as id,\n"
                    + "	c.enti_razaosocial as razao,\n"
                    + "	c.enti_nome as fantasia,\n"
                    + "	c.enti_cnpjcpf as cnpj,\n"
                    + "	c.enti_inscricaoestadual as ie,\n"
                    + "	c.enti_inscricaomunicipal as im,\n"
                    + "	c.enti_rg as rg,\n"
                    + "	c.enti_orgexp as orgaoemissor,\n"
                    + "	c.enti_fj as tipopessoa,\n"
                    + "	c.enti_endereco as endereco,\n"
                    + "	c.enti_numero as numero,\n"
                    + "	c.enti_complemento as complemento,\n"
                    + "	c.enti_bairro as bairro,\n"
                    + "	c.enti_municipio as municipio,\n"
                    + "	m.muni_ibge as municipioibge,\n"
                    + "	m.muni_nome as descricaomunicipio,\n"
                    + "	m.muni_uf as descricaouf,\n"
                    + "	c.enti_uf as uf,\n"
                    + "	c.enti_cep as cep,\n"
                    + "	c.enti_fone as telefone,\n"
                    + "	c.enti_email as email,\n"
                    + "	c.enti_fax as fax,\n"
                    + "	c.enti_celular as celular,\n"
                    + "	c.enti_datacadastro as datacadastro,\n"
                    + "	c.enti_sexo as sexo,\n"
                    + "	c.enti_naturalidade as naturalidade,\n"
                    + "	c.enti_nacionalidade as nacionalidade,\n"
                    + "	c.enti_celular as celular,\n"
                    + "	c.enti_estcivil as estadocivil,\n"
                    + "	c.enti_limitecrediario as valorlimite,\n"
                    + "	c.enti_datanasc as datanascimento,\n"
                    + "	c.enti_codsituacao as situacaocliente,\n"
                    + "	s.situ_codigo as codigosituacaocliente,\n"
                    + "	s.situ_descricao as descricaosituacaocliente\n"
                    + "from entidades c \n"
                    + "left join municipios m on m.muni_codigo = c.enti_codmunicipio\n"
                    + "left join situacoes s on s.situ_codigo = c.enti_codsituacao\n"
                    + "where enti_tipo like '%U%' OR enti_tipo like '%C%'\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("id"));
                    imp.setRazao(rst.getString("fantasia"));
                    imp.setFantasia(rst.getString("razao"));
                    imp.setCnpj(rst.getString("cnpj"));

                    if (rst.getString("rg") != null && !rst.getString("rg").trim().isEmpty()) {
                        imp.setInscricaoestadual(rst.getString("rg"));
                    } else {
                        imp.setInscricaoestadual(rst.getString("ie"));
                    }

                    imp.setInscricaoMunicipal(rst.getString("im"));
                    imp.setOrgaoemissor(rst.getString("orgaoemissor"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setMunicipioIBGE(rst.getString("municipioibge"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setTelefone(rst.getString("telefone"));
                    imp.setCelular(rst.getString("celular"));
                    imp.setEmail(rst.getString("email"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setDataNascimento(rst.getDate("datanascimento"));
                    imp.setValorLimite(rst.getDouble("valorlimite"));

                    if (rst.getString("sexo") != null && !rst.getString("sexo").trim().isEmpty()) {
                        imp.setSexo("F".equals(rst.getString("sexo")) ? TipoSexo.FEMININO : TipoSexo.MASCULINO);
                    }

                    if (rst.getString("estadocivil") != null && !rst.getString("estadocivil").trim().isEmpty()) {
                        switch (rst.getString("estadocivil")) {
                            case "S":
                                imp.setEstadoCivil(TipoEstadoCivil.CASADO);
                                break;
                            case "C":
                                imp.setEstadoCivil(TipoEstadoCivil.CASADO);
                                break;
                            default:
                                imp.setEstadoCivil(TipoEstadoCivil.NAO_INFORMADO);
                                break;
                        }
                    } else {
                        imp.setEstadoCivil(TipoEstadoCivil.NAO_INFORMADO);
                    }

                    imp.setBloqueado("Normal".equals(rst.getString("descricaosituacaocliente")) ? false : true);
                    imp.setPermiteCheque(true);
                    imp.setPermiteCreditoRotativo(true);
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
                    "select  \n"
                    + "    (r.pare_protocolo||'-'||r.pare_chave) as id,\n"
                    + "    r.pare_dtmvto as datamovimento,\n"
                    + "    r.pare_dtemissao as dataemissao,\n"
                    + "    r.pare_dtvcto as datavnecimento,\n"
                    + "    r.pare_parcela as numeroparcela,\n"
                    + "    r.pare_dcto as numerodocumento,\n"
                    + "    r.pare_valor as valorparcela,\n"
                    + "    r.pare_desconto as desconto,\n"
                    + "    r.pare_juros as juros,\n"
                    + "    r.pare_abatimentos as abatimentos,\n"
                    + "    r.pare_acrescimos as acrescimos,\n"
                    + "    r.pare_multa as multa,\n"
                    + "    r.pare_parcelas as totalparcelas,\n"
                    + "    r.pare_codentidade as idcliente,\n"
                    + "    f.enti_razaosocial as razao,\n"
                    + "    f.enti_nome as fantasia,\n"
                    + "    f.enti_cnpjcpf as cnpj,\n"
                    + "    r.pare_obs as observacao,\n"
                    + "    r.pare_complemento as complementoobs,\n"
                    + "    r.pare_pdv as ecf\n"
                    + "from pagrec r\n"
                    + "join entidades f on f.enti_codigo = r.pare_codentidade\n"
                    + "where r.pare_pr = 'R' and r.pare_situacao = 'P'\n"
                    + "and r.pare_dtbaixa is null\n"
                    + "and f.enti_tipo like '%C%'\n"
                    + "and r.pare_codempresa = '" + getLojaOrigem() + "'\n"
                    + "and r.pare_conta not in (112601,112602) \n"
                    + "order by 3"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("id"));
                    imp.setCnpjCliente(rst.getString("cnpj"));
                    imp.setIdCliente(rst.getString("idcliente"));
                    imp.setNumeroCupom(rst.getString("numerodocumento"));
                    imp.setParcela(rst.getInt("numeroparcela"));
                    imp.setValor(rst.getDouble("valorparcela"));
                    imp.setDataEmissao(rst.getDate("dataemissao"));
                    imp.setDataVencimento(rst.getDate("datavnecimento"));
                    //imp.setJuros(rst.getDouble("juros"));
                    //imp.setMulta(rst.getDouble("multa"));
                    imp.setEcf(rst.getString("ecf"));
                    imp.setObservacao(rst.getString("observacao") + " " + rst.getString("complementoobs"));
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
                    "select  \n"
                    + "    (p.pare_protocolo||'-'||pare_chave) as id,\n"
                    + "    p.pare_dtmvto as datamovimento,\n"
                    + "    p.pare_dtemissao as dataemissao,\n"
                    + "    p.pare_dtvcto as datavnecimento,\n"
                    + "    p.pare_parcela as numeroparcela,\n"
                    + "    p.pare_dcto as numerodocumento,\n"
                    + "    p.pare_valor as valor,\n"
                    + "    p.pare_desconto as desconto,\n"
                    + "    p.pare_juros as juros,\n"
                    + "    p.pare_abatimentos as abatimentos,\n"
                    + "    p.pare_acrescimos as acrescimos,\n"
                    + "    p.pare_multa as multa,\n"
                    + "    p.pare_parcelas as totalparcelas,\n"
                    + "    p.pare_codentidade as idfornecedor,\n"
                    + "    f.enti_razaosocial as razao,\n"
                    + "    f.enti_nome as fantasia,\n"
                    + "    f.enti_cnpjcpf as cnpj,\n"
                    + "    p.pare_obs as observacao,\n"
                    + "    p.pare_complemento as complementoobs\n"
                    + "from pagrec p\n"
                    + "join entidades f on f.enti_codigo = p.pare_codentidade\n"
                    + "where p.pare_pr = 'P'\n"
                    + "and p.pare_dtbaixa is null\n"
                    + "and p.pare_codempresa = '" + getLojaOrigem() + "'\n"
                    + "and p.pare_situacao = 'P'\n"
                    + "order by 3"
            )) {
                while (rst.next()) {
                    ContaPagarIMP imp = new ContaPagarIMP();
                    imp.setId(rst.getString("id"));
                    imp.setIdFornecedor(rst.getString("idfornecedor"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setNumeroDocumento(rst.getString("numerodocumento"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setDataEntrada(rst.getDate("datamovimento"));
                    imp.setDataEmissao(rst.getDate("dataemissao"));
                    imp.setObservacao(rst.getString("observacao") + " " + rst.getString("complementoobs"));

                    ContaPagarVencimentoIMP parc = imp.addVencimento(rst.getDate("datavnecimento"), imp.getValor());
                    parc.setNumeroParcela(rst.getInt("numeroparcela"));
                    parc.setObservacao(imp.getObservacao());

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ConvenioEmpresaIMP> getConvenioEmpresa() throws Exception {
        List<ConvenioEmpresaIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "  empr_codigo id,\n"
                    + "  empr_nome razao,\n"
                    + "  empr_cnpjcpf cpfcnpj,\n"
                    + "  empr_inscricaoestadual ie\n"
                    + "from empresas \n"
                    + "order by 1"
            )) {
                SimpleDateFormat format = new SimpleDateFormat("1yyMMdd");
                while (rst.next()) {
                    ConvenioEmpresaIMP imp = new ConvenioEmpresaIMP();

                    imp.setId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setCnpj(rst.getString("cpfcnpj"));
                    imp.setInscricaoEstadual(rst.getString("ie"));

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
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	c.enti_codigo as id,\n"
                    + "	c.enti_razaosocial as razao,\n"
                    + "	c.enti_nome as fantasia,\n"
                    + "	c.enti_cnpjcpf as cnpj,\n"
                    + "	c.enti_inscricaoestadual as ie,\n"
                    + "	c.enti_inscricaomunicipal as im,\n"
                    + "	c.enti_rg as rg,\n"
                    + "	c.enti_orgexp as orgaoemissor,\n"
                    + "	c.enti_fj as tipopessoa,\n"
                    + "	c.enti_endereco as endereco,\n"
                    + "	c.enti_numero as numero,\n"
                    + "	c.enti_complemento as complemento,\n"
                    + "	c.enti_bairro as bairro,\n"
                    + "	c.enti_municipio as municipio,\n"
                    + "	m.muni_ibge as municipioibge,\n"
                    + "	m.muni_nome as descricaomunicipio,\n"
                    + "	m.muni_uf as descricaouf,\n"
                    + "	c.enti_uf as uf,\n"
                    + "	c.enti_cep as cep,\n"
                    + "	c.enti_fone as telefone,\n"
                    + "	c.enti_email as email,\n"
                    + "	c.enti_fax as fax,\n"
                    + "	c.enti_celular as celular,\n"
                    + "	c.enti_datacadastro as datacadastro,\n"
                    + "	c.enti_sexo as sexo,\n"
                    + "	c.enti_naturalidade as naturalidade,\n"
                    + "	c.enti_nacionalidade as nacionalidade,\n"
                    + "	c.enti_celular as celular,\n"
                    + "	c.enti_estcivil as estadocivil,\n"
                    + "	c.enti_limitecrediario as valorlimite,\n"
                    + "	c.enti_datanasc as datanascimento,\n"
                    + "	c.enti_codsituacao as situacaocliente,\n"
                    + "	s.situ_codigo as codigosituacaocliente,\n"
                    + "	s.situ_descricao as descricaosituacaocliente,\n"
                    + "	c.enti_codempresafunc empresaid\n"
                    + "from entidades c \n"
                    + "left join municipios m on m.muni_codigo = c.enti_codmunicipio\n"
                    + "left join situacoes s on s.situ_codigo = c.enti_codsituacao\n"
                    + "where \n"
                    + "enti_tipo like '%U%'\n"
                    + "and \n"
                    + "enti_codempresafunc is not null\n"
                    + "and \n"
                    + "enti_codempresafunc <> ''\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    ConveniadoIMP imp = new ConveniadoIMP();
                    imp.setId(rst.getString("id"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setNome(rst.getString("razao"));
                    imp.setIdEmpresa(rst.getString("empresaid"));
                    imp.setConvenioLimite(rst.getDouble("valorlimite"));
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
                    "select  \n"
                    + "    (r.pare_protocolo||'-'||r.pare_chave) as id,\n"
                    + "    r.pare_dtmvto as datamovimento,\n"
                    + "    r.pare_dtemissao as dataemissao,\n"
                    + "    r.pare_dtvcto as datavnecimento,\n"
                    + "    r.pare_parcela as numeroparcela,\n"
                    + "    r.pare_dcto as numerodocumento,\n"
                    + "    r.pare_valor as valorparcela,\n"
                    + "    r.pare_desconto as desconto,\n"
                    + "    r.pare_juros as juros,\n"
                    + "    r.pare_abatimentos as abatimentos,\n"
                    + "    r.pare_acrescimos as acrescimos,\n"
                    + "    r.pare_multa as multa,\n"
                    + "    r.pare_parcelas as totalparcelas,\n"
                    + "    r.pare_codentidade as idcliente,\n"
                    + "    f.enti_razaosocial as razao,\n"
                    + "    f.enti_nome as fantasia,\n"
                    + "    f.enti_cnpjcpf as cnpj,\n"
                    + "    r.pare_obs as observacao,\n"
                    + "    r.pare_complemento as complementoobs,\n"
                    + "    r.pare_pdv as ecf\n"
                    + "from pagrec r\n"
                    + "join entidades f on f.enti_codigo = r.pare_codentidade\n"
                    + "where r.pare_pr = 'R'\n"
                    + "and r.pare_dtbaixa is null\n"
                    + "and r.pare_situacao = 'P'\n"
                    + "and r.pare_codempresa = '" + getLojaOrigem() + "'\n"
                    + "and f.enti_tipo like '%U%'\n"
                    + "order by 3"
            )) {
                SimpleDateFormat format = new SimpleDateFormat("1yyMMdd");
                while (rst.next()) {
                    ConvenioTransacaoIMP imp = new ConvenioTransacaoIMP();
                    imp.setId(rst.getString("id"));
                    imp.setIdConveniado(rst.getString("idCliente"));
                    imp.setEcf(rst.getString("ecf"));
                    imp.setNumeroCupom(rst.getString("numerodocumento"));
                    //imp.setDataHora(new Timestamp(format.parse(rst.getString("dataemissao")).getTime()));
                    imp.setDataHora(rst.getTimestamp("dataemissao"));
                    imp.setValor(rst.getDouble("valorparcela"));
                    imp.setObservacao(rst.getString("observacao") + " " + rst.getString("complementoobs"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ChequeIMP> getCheques() throws Exception {
        List<ChequeIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select  \n"
                    + "    (r.pare_protocolo||'-'||r.pare_chave) as id,\n"
                    + "    r.pare_dtmvto as datamovimento,\n"
                    + "    r.pare_dtemissao as dataemissao,\n"
                    + "    r.pare_dtvcto as datavnecimento,\n"
                    + "    r.pare_parcela as numeroparcela,\n"
                    + "    r.pare_dcto as numerodocumento,\n"
                    + "    r.pare_banco banco,\n"
                    + "    r.pare_agencia agencia,\n"
                    + "    r.pare_contaban conta,\n"
                    + "    r.pare_valor as valorparcela,\n"
                    + "    r.pare_parcelas as totalparcelas,\n"
                    + "    r.pare_codentidade as idcliente,\n"
                    + "    f.enti_razaosocial as nome,\n"
                    + "    f.enti_nome as fantasia,\n"
                    + "    r.pare_cnpjcpf as cnpj,\n"
                    + "    r.pare_obs as observacao,\n"
                    + "    r.pare_obs2,\n"
                    + "    r.pare_complemento as complementoobs,\n"
                    + "    r.pare_pdv as ecf,\n"
                    + "    r.pare_conta as tipo\n"
                    + "from pagrec r\n"
                    + "left join entidades f on f.enti_codigo = r.pare_codentidade\n"
                    + "where r.pare_pr = 'R'\n"
                    + "and r.pare_dtbaixa is null\n"
                    + "and r.pare_situacao = 'P'\n"
                    + "and r.pare_codempresa = '" + getLojaOrigem() + "'\n"
                    + "and r.pare_conta  in (112601,112602)\n"
                    + "order by 3"
            )) {
                while (rst.next()) {
                    ChequeIMP imp = new ChequeIMP();
                    imp.setId(rst.getString("id"));
                    imp.setCpf(rst.getString("cnpj"));
                    imp.setNumeroCheque(rst.getString("numerodocumento"));
                    imp.setBanco(rst.getInt("banco"));
                    imp.setAgencia(rst.getString("agencia"));
                    imp.setConta(rst.getString("conta"));
                    imp.setDate(rst.getDate("dataemissao"));
                    imp.setNumeroCupom(rst.getString("numerodocumento"));
                    imp.setValor(rst.getDouble("valorparcela"));
                    imp.setNome(rst.getString("nome"));
                    imp.setObservacao(rst.getString("observacao"));

                    if (rst.getInt("tipo") == 112601) {
                        imp.setVistaPrazo(TipoVistaPrazo.PRAZO);
                    } else {
                        imp.setVistaPrazo(TipoVistaPrazo.A_VISTA);
                    }

                    //imp.setAlinea(rst.getInt("alinea"));
                    //imp.setDataHoraAlteracao(rst.getTimestamp("dataHoraAlteracao"));
                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ContaReceberIMP> getContasReceber(Set<OpcaoContaReceber> opt) throws Exception {
        List<ContaReceberIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select  \n"
                    + "    (r.pare_protocolo||'-'||r.pare_chave) as id,\n"
                    + "    r.pare_dtmvto as datamovimento,\n"
                    + "    r.pare_dtemissao as dataemissao,\n"
                    + "    r.pare_dtvcto as datavnecimento,\n"
                    + "    r.pare_parcela as numeroparcela,\n"
                    + "    r.pare_dcto as numerodocumento,\n"
                    + "    r.pare_valor as valorparcela,\n"
                    + "    r.pare_desconto as desconto,\n"
                    + "    r.pare_juros as juros,\n"
                    + "    r.pare_abatimentos as abatimentos,\n"
                    + "    r.pare_acrescimos as acrescimos,\n"
                    + "    r.pare_multa as multa,\n"
                    + "    r.pare_parcelas as totalparcelas,\n"
                    + "    r.pare_codentidade as idfornecedor,\n"
                    + "    f.enti_razaosocial as razao,\n"
                    + "    f.enti_nome as fantasia,\n"
                    + "    f.enti_cnpjcpf as cnpj,\n"
                    + "    r.pare_obs as observacao,\n"
                    + "    r.pare_obs2,\n"
                    + "    r.pare_complemento as complementoobs,\n"
                    + "    r.pare_pdv as ecf\n"
                    + "from pagrec r\n"
                    + "join entidades f on f.enti_codigo = r.pare_codentidade\n"
                    + "where r.pare_pr = 'R'\n"
                    + "and r.pare_dtbaixa is null\n"
                    + "and r.pare_situacao = 'P'\n"
                    + "and r.pare_codempresa = '"+getLojaOrigem()+"'\n"
                    + "and f.enti_tipo like '%F%'\n"
                    + "and r.pare_conta not in (112601,112602)\n"
                    + "and f.enti_codigo in (149354,151076,156424)\n"
                    + "order by 3"
            )) {
                while (rst.next()) {
                    ContaReceberIMP imp = new ContaReceberIMP();

                    imp.setId(rst.getString("id"));
                    imp.setIdFornecedor(rst.getString("idfornecedor"));
                    imp.setDataEmissao(rst.getDate("dataemissao"));
                    imp.setDataVencimento(rst.getDate("datavnecimento"));
                    imp.setValor(rst.getDouble("valorparcela"));
                    imp.setObservacao(rst.getString("observacao") + " " + rst.getString("complementoobs"));
                    if (rst.getDouble("abatimentos") > 0) {
                        imp.add(imp.getId(), rst.getDouble("abatimentos"), 0, 0, 0, rst.getDate("datavnecimento"));
                    }

                    result.add(imp);
                }
            }
        }

        return result;
    }
}
