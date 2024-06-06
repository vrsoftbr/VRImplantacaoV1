package vrimplantacao2.dao.interfaces;

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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import static vr.core.utils.StringUtils.LOG;
import vrimplantacao2_5.dao.conexao.ConexaoPostgres;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.vo.importacao.ReceitaIMP;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto.ProdutoAnteriorDAO;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.cadastro.mercadologico.MercadologicoNivelIMP;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoEmpresa;
import vrimplantacao2.vo.enums.TipoEstadoCivil;
import vrimplantacao2.vo.enums.TipoFornecedor;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.ContaPagarVencimentoIMP;
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

/**
 *
 * @author Leandro
 */
public class RPInfoDAO extends InterfaceDAO implements MapaTributoProvider {

    public boolean importarFuncionario = false;
    public boolean gerarCodigoAtacado = true;
    public int idLojaVR = 1;
    public boolean removeDigitoEAN = false;
    public boolean utilizarCustoNota = false;

    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select unid_codigo, unid_reduzido from unidades order by 1"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("unid_codigo"), rst.getString("unid_reduzido")));
                }
            }
        }

        return result;
    }

    @Override
    public String getSistema() {
        return "RPINFO";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(new OpcaoProduto[]{
            /*OpcaoProduto.MERCADOLOGICO_POR_NIVEL,*/
            OpcaoProduto.MERCADOLOGICO_PRODUTO,
            OpcaoProduto.MERCADOLOGICO,
            OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
            OpcaoProduto.FAMILIA_PRODUTO,
            OpcaoProduto.FAMILIA,
            OpcaoProduto.IMPORTAR_MANTER_BALANCA,
            OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
            OpcaoProduto.PRODUTOS,
            OpcaoProduto.EAN,
            OpcaoProduto.EAN_EM_BRANCO,
            OpcaoProduto.OFERTA,
            OpcaoProduto.ATIVO,
            OpcaoProduto.CEST,
            OpcaoProduto.CUSTO,
            OpcaoProduto.PRECO,
            OpcaoProduto.ESTOQUE_MINIMO,
            OpcaoProduto.ESTOQUE_MAXIMO,
            OpcaoProduto.ESTOQUE,
            OpcaoProduto.DATA_ALTERACAO,
            OpcaoProduto.DATA_CADASTRO,
            OpcaoProduto.DESC_COMPLETA,
            OpcaoProduto.DESC_GONDOLA,
            OpcaoProduto.DESC_REDUZIDA,
            OpcaoProduto.MARGEM,
            OpcaoProduto.QTD_EMBALAGEM_EAN,
            OpcaoProduto.QTD_EMBALAGEM_COTACAO,
            OpcaoProduto.TIPO_EMBALAGEM_EAN,
            OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
            OpcaoProduto.VOLUME_TIPO_EMBALAGEM,
            OpcaoProduto.VOLUME_QTD,
            OpcaoProduto.NCM,
            OpcaoProduto.PESO_BRUTO,
            OpcaoProduto.PESO_LIQUIDO,
            OpcaoProduto.DESCONTINUADO,
            OpcaoProduto.PESAVEL,
            OpcaoProduto.ICMS,
            OpcaoProduto.PIS_COFINS,
            OpcaoProduto.NATUREZA_RECEITA,
            OpcaoProduto.ATACADO,
            OpcaoProduto.RECEITA,
            OpcaoProduto.SECAO,
            OpcaoProduto.PRATELEIRA,
            OpcaoProduto.OFERTA,
            OpcaoProduto.FABRICANTE
        }));
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
                OpcaoFornecedor.PRODUTO_FORNECEDOR,
                OpcaoFornecedor.TIPO_FORNECEDOR,
                OpcaoFornecedor.PAGAR_FORNECEDOR
        ));
    }

    @Override
    public Set<OpcaoCliente> getOpcoesDisponiveisCliente() {
        return new HashSet<>(Arrays.asList(
                OpcaoCliente.CONVENIO_EMPRESA,
                OpcaoCliente.CONVENIO_CONVENIADO,
                OpcaoCliente.CONVENIO_TRANSACAO,
                OpcaoCliente.DADOS,
                OpcaoCliente.CNPJ,
                OpcaoCliente.INSCRICAO_ESTADUAL,
                OpcaoCliente.RAZAO,
                OpcaoCliente.FANTASIA,
                OpcaoCliente.SITUACAO_CADASTRO,
                OpcaoCliente.ENDERECO,
                OpcaoCliente.NUMERO,
                OpcaoCliente.COMPLEMENTO,
                OpcaoCliente.BAIRRO,
                OpcaoCliente.MUNICIPIO,
                OpcaoCliente.UF,
                OpcaoCliente.CEP,
                OpcaoCliente.ESTADO_CIVIL,
                OpcaoCliente.CONTATOS,
                OpcaoCliente.DATA_CADASTRO,
                OpcaoCliente.DATA_NASCIMENTO,
                OpcaoCliente.EMPRESA,
                OpcaoCliente.ENDERECO_EMPRESA,
                OpcaoCliente.BAIRRO_EMPRESA,
                OpcaoCliente.COMPLEMENTO_EMPRESA,
                OpcaoCliente.MUNICIPIO_EMPRESA,
                OpcaoCliente.UF_EMPRESA,
                OpcaoCliente.CEP_EMPRESA,
                OpcaoCliente.TELEFONE_EMPRESA,
                OpcaoCliente.DATA_ADMISSAO,
                OpcaoCliente.CARGO,
                OpcaoCliente.SALARIO,
                OpcaoCliente.NOME_CONJUGE,
                OpcaoCliente.DATA_NASCIMENTO_CONJUGE,
                OpcaoCliente.NOME_PAI,
                OpcaoCliente.NOME_MAE,
                OpcaoCliente.TELEFONE,
                OpcaoCliente.CELULAR,
                OpcaoCliente.EMAIL,
                OpcaoCliente.RECEBER_CREDITOROTATIVO));
    }

    @Override
    public List<OfertaIMP> getOfertas(Date dataTermino) throws Exception {
        List<OfertaIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "with loja as (\n"
                    + "	select unid_codigo id, unid_uf uf from unidades where unid_codigo = '" + getLojaOrigem() + "'\n"
                    + ")\n"
                    + "select\n"
                    + "	p.prod_codigo idproduto,\n"
                    + "	coalesce(un.prun_dtinioferta, current_date) datainicio,\n"
                    + "	un.prun_dtoferta datatermino,\n"
                    + "	un.prun_prnormal preconormal,\n"
                    + "	un.prun_prvenda precooferta\n"
                    + "from\n"
                    + "	produtos p\n"
                    + "	join loja on true\n"
                    + "	left join produn un on\n"
                    + "		p.prod_codigo = un.prun_prod_codigo and\n"
                    + "		un.prun_unid_codigo = loja.id\n"
                    + "where\n"
                    + "	un.prun_oferta = 'S' and\n"
                    + "   un.prun_dtoferta > current_date\n"
                    + "order by\n"
                    + "	id"
            /*
                    "select \n" +
                    "	a.agof_datai datainicio,\n" +
                    "	a.agof_dataf datatermino,\n" +
                    "	i.prag_prod_codigo idproduto,\n" +
                    "	i.prag_precooferta precooferta,\n" +
                    "	i.prag_prnormal preconormal,\n" +
                    "	i.prag_custo precocusto\n" +
                    "from \n" +
                    "	agof a\n" +
                    "join\n" +
                    "	pragof i on a.agof_codigo = i.prag_agof_codigo\n" +
                    "where \n" +
                    "	a.agof_dataf > current_date and\n" +
                    "	a.agof_unidades like '%" + getLojaOrigem() + "%'")) {
                    "	a.agof_dataf >= current_date and\n" +
                    "	a.agof_unidades like '%" + getLojaOrigem() + "%'"
             */
            )) {
                while (rs.next()) {
                    OfertaIMP imp = new OfertaIMP();

                    imp.setDataInicio(rs.getDate("datainicio"));
                    imp.setDataFim(rs.getDate("datatermino"));
                    imp.setIdProduto(rs.getString("idproduto"));
                    imp.setPrecoNormal(rs.getDouble("preconormal"));
                    imp.setPrecoOferta(rs.getDouble("precooferta"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoNivelIMP> getMercadologicoPorNivel() throws Exception {
        Map<String, MercadologicoNivelIMP> merc = new LinkedHashMap<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "grup_classificacao merc1, \n"
                    + "grup_descricao merc1_desc\n"
                    + "from grupos\n"
                    + "where char_length(grup_classificacao) <= 2\n"
                    + "order by grup_classificacao"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP imp = new MercadologicoNivelIMP();
                    imp.setId(rst.getString("merc1"));
                    imp.setDescricao(rst.getString("merc1_desc"));
                    merc.put(imp.getId(), imp);
                }
            }

            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "substring(grup_classificacao, 1, 1) merc1, \n"
                    + "substring(grup_classificacao, 2, 2) merc2, \n"
                    + "grup_descricao merc2_desc\n"
                    + "from grupos\n"
                    + "where char_length(grup_classificacao) = 3\n"
                    + "order by grup_classificacao"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP merc1 = merc.get(rst.getString("merc1"));
                    if (merc1 != null) {
                        merc1.addFilho(
                                rst.getString("merc2"),
                                rst.getString("merc2_desc")
                        );
                    }
                }
            }

            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "substring(grup_classificacao, 1, 1) merc1, \n"
                    + "substring(grup_classificacao, 2, 2) merc2, \n"
                    + "substring(grup_classificacao, 4, 2) merc3,\n"
                    + "grup_descricao merc3_desc\n"
                    + "from grupos\n"
                    + "where char_length(grup_classificacao) = 5\n"
                    + "order by grup_classificacao"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP merc1 = merc.get(rst.getString("merc1"));
                    if (merc1 != null) {
                        MercadologicoNivelIMP merc2 = merc1.getNiveis().get(rst.getString("merc2"));
                        if (merc2 != null) {
                            merc2.addFilho(
                                    rst.getString("merc3"),
                                    rst.getString("merc3_desc")
                            );
                        }
                    }
                }
            }

            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "substring(grup_classificacao, 1, 1) merc1, \n"
                    + "substring(grup_classificacao, 2, 2) merc2, \n"
                    + "substring(grup_classificacao, 4, 2) merc3,\n"
                    + "substring(grup_classificacao, 6, 2) merc4,\n"
                    + "grup_descricao merc4_desc\n"
                    + "from grupos\n"
                    + "where char_length(grup_classificacao) = 7\n"
                    + "order by grup_classificacao"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP merc1 = merc.get(rst.getString("merc1"));
                    if (merc1 != null) {
                        MercadologicoNivelIMP merc2 = merc1.getNiveis().get(rst.getString("merc2"));
                        if (merc2 != null) {
                            MercadologicoNivelIMP merc3 = merc2.getNiveis().get(rst.getString("merc3"));
                            if (merc3 != null) {
                                merc3.addFilho(
                                        rst.getString("merc4"),
                                        rst.getString("merc4_desc")
                                );
                            }
                        }
                    }
                }
            }
        }
        return new ArrayList<>(merc.values());
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select distinct\n"
                    + "	d.dpto_codigo merc1,\n"
                    + "	d.dpto_descricao merc1_desc,\n"
                    + "	g.grup_codigo merc2,\n"
                    + "	g.grup_descricao merc2_desc\n"
                    + "from\n"
                    + "	produtos p\n"
                    + "	join departamentos d on p.prod_dpto_codigo = d.dpto_codigo\n"
                    + "	join grupos g on p.prod_grup_codigo = g.grup_codigo\n"
                    + "order by\n"
                    + "	1, 3"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setMerc1ID(rst.getString("merc1"));
                    imp.setMerc1Descricao(rst.getString("merc1_desc"));
                    imp.setMerc2ID(rst.getString("merc2"));
                    imp.setMerc2Descricao(rst.getString("merc2_desc"));
                    imp.setMerc3ID("1");
                    imp.setMerc3Descricao(rst.getString("merc2_desc"));
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
                    "select prod_codpreco, prod_descricao from produtos where prod_codpreco != 0 order by prod_codpreco"
            )) {
                ProdutoParaFamiliaHelper gerador = new ProdutoParaFamiliaHelper(result);
                while (rst.next()) {
                    gerador.gerarFamilia(rst.getString("prod_codpreco"), rst.getString("prod_descricao"));
                }
            }
        }

        return result;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        if (gerarCodigoAtacado) {
            List<ProdutoIMP> result = new ArrayList<>();

            try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select \n"
                        + "prun_prod_codigo,\n"
                        + "prun_prvenda,\n"
                        + "prun_fatorpr3,\n"
                        + "prun_prvenda3,\n"
                        + "prun_emb\n"
                        + "from produn \n"
                        + "where prun_unid_codigo = '" + getLojaOrigem() + "'\n"
                        + "and prun_fatorpr3 > 1\n"
                        + "and prun_prvenda3 > 0"
                )) {
                    while (rst.next()) {
                        int codigoAtual = new ProdutoAnteriorDAO().getCodigoAnterior2(getSistema(), getLojaOrigem(), rst.getString("prun_prod_codigo"));

                        if (codigoAtual > 0) {

                            ProdutoIMP imp = new ProdutoIMP();
                            imp.setImportLoja(getLojaOrigem());
                            imp.setImportSistema(getSistema());
                            imp.setImportId(rst.getString("prun_prod_codigo"));
                            imp.setEan(String.valueOf(idLojaVR) + "99999" + String.valueOf(codigoAtual));
                            imp.setTipoEmbalagem(rst.getString("prun_emb"));
                            imp.setQtdEmbalagem(rst.getInt("prun_fatorpr3"));
                            result.add(imp);
                        }
                    }
                }
            }

            return result;
        }
        return super.getEANs();
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "with loja as (\n"
                    + "	select unid_codigo id, unid_uf uf from unidades where unid_codigo = '" + getLojaOrigem() + "'\n"
                    + ")\n"
                    + "select \n"
                    + "	distinct on (tr.trib_codigo)\n"
                    + "	tr.trib_codigo id_tributacao,\n"
                    + "	tr.trib_descricao descricao,\n"
                    + "	tr.trib_codnf cst,\n"
                    + "	tr.trib_icms aliquota,\n"
                    + "	tr.trib_redbc reducao,\n"
                    + "	coalesce(tr.trib_fcpaliq, 0) fcp\n"
                    + "from\n"
                    + "	tributacao tr\n"
                    + "	join loja on true\n"
                    + "where\n"
                    + "	tr.trib_uforigem = loja.uf\n"
                    + "	and tr.trib_mvtos like '%EVP%'\n"
                    + "order by\n"
                    + "	tr.trib_codigo, tr.trib_cstpis desc"
            )) {
                while (rst.next()) {
                    result.add(
                            new MapaTributoIMP(
                                    rst.getString("id_tributacao"),
                                    rst.getString("descricao"),
                                    rst.getInt("cst"),
                                    rst.getDouble("aliquota"),
                                    rst.getDouble("reducao")
                            )
                    );
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
                    "with loja as (\n"
                    + "	select unid_codigo id, unid_uf uf from unidades where unid_codigo = '" + getLojaOrigem() + "'\n"
                    + "),\n"
                    + "nf2019 as (\n"
                    + "	select\n"
                    + "		distinct on\n"
                    + "		(mprd_prod_codigo) mprd_prod_codigo,\n"
                    + "		mprd_datamvto,\n"
                    + "		(mprd_prcompra / case when mprd_qtde = 0 then 1 else mprd_qtde end) as custocompra,\n"
                    + "		(mprd_prcompra / case when mprd_qtde = 0 then 1 else mprd_qtde end) * (mprd_alsubtribinf / 100) valoricms,\n"
                    + "		(mprd_prcompra / case when mprd_qtde = 0 then 1 else mprd_qtde end) * (mprd_aliqpis / 100) valorpis,\n"
                    + "		(mprd_prcompra / case when mprd_qtde = 0 then 1 else mprd_qtde end) * (mprd_aliqcofins / 100) valorcofins,\n"
                    + "		(mprd_prcompra / case when mprd_qtde = 0 then 1 else mprd_qtde end) * (mprd_subtrib / 100) valorst,\n"
                    + "		(mprd_prcompra / case when mprd_qtde = 0 then 1 else mprd_qtde end) * (mprd_percipi / 100) valoripi\n"
                    + "	from\n"
                    + "		movprodd0119 m\n"
                    + "		join loja on loja.id = m.mprd_unid_codigo\n"
                    + "	where\n"
                    + "		m.mprd_dcto_tipo like '%EAQ%'\n"
                    + "	order by\n"
                    + "		mprd_prod_codigo,\n"
                    + "		mprd_datamvto desc\n"
                    + "),\n"
                    + "nf2020 as (\n"
                    + "	select\n"
                    + "		distinct on\n"
                    + "		(mprd_prod_codigo) mprd_prod_codigo,\n"
                    + "		mprd_datamvto,\n"
                    + "		(mprd_prcompra / case when mprd_qtde = 0 then 1 else mprd_qtde end) as custocompra,\n"
                    + "		(mprd_prcompra / case when mprd_qtde = 0 then 1 else mprd_qtde end) * (mprd_alsubtribinf / 100) valoricms,\n"
                    + "		(mprd_prcompra / case when mprd_qtde = 0 then 1 else mprd_qtde end) * (mprd_aliqpis / 100) valorpis,\n"
                    + "		(mprd_prcompra / case when mprd_qtde = 0 then 1 else mprd_qtde end) * (mprd_aliqcofins / 100) valorcofins,\n"
                    + "		(mprd_prcompra / case when mprd_qtde = 0 then 1 else mprd_qtde end) * (mprd_subtrib / 100) valorst,\n"
                    + "		(mprd_prcompra / case when mprd_qtde = 0 then 1 else mprd_qtde end) * (mprd_percipi / 100) valoripi\n"
                    + "	from\n"
                    + "		movprodd0120 m\n"
                    + "		join loja on loja.id = m.mprd_unid_codigo\n"
                    + "	where\n"
                    + "		m.mprd_dcto_tipo like '%EAQ%'\n"
                    + "	order by\n"
                    + "		mprd_prod_codigo,\n"
                    + "		mprd_datamvto desc	\n"
                    + "),\n"
                    + "nf2021 as (\n"
                    + "	select\n"
                    + "		distinct on\n"
                    + "		(mprd_prod_codigo) mprd_prod_codigo,\n"
                    + "		mprd_datamvto,\n"
                    + "		(mprd_prcompra / case when mprd_qtde = 0 then 1 else mprd_qtde end) as custocompra,\n"
                    + "		(mprd_prcompra / case when mprd_qtde = 0 then 1 else mprd_qtde end) * (mprd_alsubtribinf / 100) valoricms,\n"
                    + "		(mprd_prcompra / case when mprd_qtde = 0 then 1 else mprd_qtde end) * (mprd_aliqpis / 100) valorpis,\n"
                    + "		(mprd_prcompra / case when mprd_qtde = 0 then 1 else mprd_qtde end) * (mprd_aliqcofins / 100) valorcofins,\n"
                    + "		(mprd_prcompra / case when mprd_qtde = 0 then 1 else mprd_qtde end) * (mprd_subtrib / 100) valorst,\n"
                    + "		(mprd_prcompra / case when mprd_qtde = 0 then 1 else mprd_qtde end) * (mprd_percipi / 100) valoripi\n"
                    + "	from\n"
                    + "		movprodd0121 m\n"
                    + "		join loja on loja.id = m.mprd_unid_codigo\n"
                    + "	where\n"
                    + "		m.mprd_dcto_tipo like '%EAQ%'\n"
                    + "	order by\n"
                    + "		mprd_prod_codigo,\n"
                    + "		mprd_datamvto desc	\n"
                    + "),\n"
                    + "nf as (\n"
                    + "	select\n"
                    + "	distinct on (mprd_prod_codigo)\n"
                    + "		*\n"
                    + "	from\n"
                    + "		(\n"
                    + "			select * from nf2019\n"
                    + "			union all\n"
                    + "			select * from nf2020\n"
                    + "                 union all\n"
                    + "                 select * from nf2021\n"
                    + "		) a\n"
                    + "	order by\n"
                    + "		mprd_prod_codigo,\n"
                    + "		mprd_datamvto desc	\n"
                    + "),\n"
                    + "custo as (\n"
                    + "	select\n"
                    + "		nf.mprd_prod_codigo,	\n"
                    + "		nf.custocompra - nf.valoricms - nf.valorpis - nf.valorcofins custosemimposto_nf,\n"
                    + "		nf.custocompra + nf.valorst + nf.valoripi custocomimposto_nf\n"
                    + "	from\n"
                    + "		nf\n"
                    + "	order by\n"
                    + "		nf.mprd_prod_codigo\n"
                    + "),\n"
                    + "piscofins_s as (\n"
                    + " select \n"
                    + "		distinct on (tr.trib_codigo)\n"
                    + "		tr.trib_codigo id_tributacao,\n"
                    + "		tr.trib_cstpis cstpiscofins,\n"
                    + "		tr.trib_natpiscof naturezareceita\n"
                    + "	from\n"
                    + "		tributacao tr\n"
                    + "		join simbologias s on\n"
                    + "			tr.trib_simb = s.simb_codigo\n"
                    + "	where\n"
                    + "		tr.trib_uforigem = (select unid_uf from unidades where unid_codigo = '" + getLojaOrigem() + "')\n"
                    + "		and tr.trib_ufdestino = (select unid_uf from unidades where unid_codigo = '" + getLojaOrigem() + "')\n"
                    + "		and tr.trib_mvtos like '%EVD%'\n"
                    + "		and s.simb_imposto = 'C'\n"
                    + "	order by\n"
                    + "		tr.trib_codigo, tr.trib_data desc, tr.trib_cstpis desc\n"
                    + "),\n"
                    + "cest as (\n"
                    + "	select\n"
                    + "		distinct on (id_produto)\n"
                    + "		prau_prod_codigo id_produto,\n"
                    + "		prau_cest cest\n"
                    + "	from\n"
                    + "		prodaux p\n"
                    + ")\n"
                    + "select\n"
                    + "	p.prod_codigo id,\n"
                    + "	p.prod_datacad datacadastro,\n"
                    + "	p.prod_dataalt dataalteracao,\n"
                    + "	ean.ean,\n"
                    + "	ean.qtdembalagem,\n"
                    + "	p.prod_qemb embalagemcotacao,\n"
                    + "	case\n"
                    + "	when p.prod_balanca = 'P' then 'KG'\n"
                    + "	when p.prod_balanca = 'U' then 'UN'\n"
                    + "	when ean.qtdembalagem = 1 then 'UN'\n"
                    + "	else un.prun_emb end unidade,\n"
                    + "	case \n"
                    + "	when p.prod_balanca in ('P', 'U') then 1\n"
                    + "	else 0 end e_balanca,\n"
                    + "	coalesce(un.prun_validade, 0) validade,\n"
                    + "	p.prod_descricao || ' ' || coalesce(p.prod_complemento, '') descricaocompletacomplemento,\n"
                    + "	p.prod_descricao descricaocompleta,        \n"
                    + "	p.prod_descrpdvs descricaoreduzida,\n"
                    + "	p.prod_dpto_codigo merc1,\n"
                    + "	p.prod_grup_codigo merc2,\n"
                    + "	p.prod_codpreco id_familia,\n"
                    + "	p.prod_peso pesobruto,\n"
                    + "	p.prod_pesoliq pesoliquido,\n"
                    + " p.prod_funcao emabalagemfuncao,\n"
                    + "	p.prod_extra8 fatoretiqueta,"
                    + "	un.prun_estmin estoqueminimo,\n"
                    + "	un.prun_estmax estoquemaximo,\n"
                    + "	un.prun_estoque1 + un.prun_estoque2 + un.prun_estoque3 + un.prun_estoque4 + un.prun_estoque5 estoque,\n"
                    + "	un.prun_prultcomp,\n"
                    + "	un.prun_ctcompra custosemimposto,\n"
                    + "	un.prun_prultcomp custocomimposto,\n"
                    + " un.prun_prultcomp custo,\n"
                    + "	coalesce(custo.custosemimposto_nf, un.prun_ctcompra, 0) custosemimposto_nf,\n"
                    + "	coalesce(custo.custocomimposto_nf, un.prun_prultcomp, 0) custocomimposto_nf,\n"
                    + "	un.prun_margem margem,\n"
                    + "	un.prun_prvenda precovenda,\n"
                    + "	case un.prun_ativo when 'S' then 1 else 0 end situacaocadastro,\n"
                    + "	case un.prun_bloqueado when 'N' then 0 else 1 end descontinuado,\n"
                    + "	p.prod_codigoncm ncm,\n"
                    + "	cest.cest,\n"
                    + "	un.prun_setor setor,\n"
                    + "	un.prun_setordep departamento,\n"
                    + "	piscofins_s.cstpiscofins piscofins_s,\n"
                    + "	piscofins_s.naturezareceita piscofins_natrec,\n"
                    + "	p.prod_trib_codigo id_icms,\n"
                    + "	p.prod_forn_codigo\n"
                    + "from\n"
                    + "	produtos p\n"
                    + "	join loja on true\n"
                    + "	left join cest on\n"
                    + "		p.prod_codigo = cest.id_produto\n"
                    + "	left join produn un on\n"
                    + "		p.prod_codigo = un.prun_prod_codigo and\n"
                    + "		un.prun_unid_codigo = loja.id\n"
                    + "	left join (\n"
                    + "		select\n"
                    + "			prod_codigo id,\n"
                    + "			prod_codbarras ean,\n"
                    + "			prod_funcao unidade,\n"
                    + "			1 qtdembalagem,\n"
                    + "			nullif(regexp_replace(prod_codbarras, '[^0-9]*',''),'')::bigint longean,\n"
                    + "			'prod_codbarras' tipo\n"
                    + "		from\n"
                    + "			produtos\n"
                    + "		where			\n"
                    + "			nullif(regexp_replace(prod_codbarras, '[^0-9]*',''),'')::bigint > 0\n"
                    + "		union\n"
                    + "		select\n"
                    + "			prod_codigo id,\n"
                    + "			prod_codcaixa ean,\n"
                    + "			prod_emb unidade,\n"
                    + "			prod_qemb qtdembalagem,\n"
                    + "			nullif(regexp_replace(prod_codcaixa, '[^0-9]*',''),'')::bigint longean,\n"
                    + "			'prod_codcaixa' tipo\n"
                    + "		from\n"
                    + "			produtos\n"
                    + "		where			\n"
                    + "			nullif(regexp_replace(prod_codcaixa, '[^0-9]*',''),'')::bigint > 0\n"
                    + "		union\n"
                    + "		select\n"
                    + "			cbal_prod_codigo id,\n"
                    + "			cbal_prod_codbarras ean,\n"
                    + "			'UN' unidade,\n"
                    + "			coalesce(nullif(cbalt.cbal_fatoremb,0),1) qtdembalagem,\n"
                    + "			nullif(regexp_replace(cbal_prod_codbarras, '[^0-9]*',''),'')::bigint longean,\n"
                    + "			'cbal_prod_codbarras' tipo\n"
                    + "		from\n"
                    + "			cbalt\n"
                    + "		where\n"
                    + "			nullif(regexp_replace(cbal_prod_codbarras, '[^0-9]*',''),'')::bigint > 999999\n"
                    + "	) ean on ean.id = p.prod_codigo\n"
                    + "	left join piscofins_s on\n"
                    + "		piscofins_s.id_tributacao = p.prod_trib_codigo\n"
                    + "	left join custo on\n"
                    + "		custo.mprd_prod_codigo = p.prod_codigo\n"
                    + "order by\n"
                    + "	longean"
            )) {
                Map<Integer, ProdutoBalancaVO> balanca = new ProdutoBalancaDAO().getProdutosBalanca();
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setDataAlteracao(rst.getDate("dataalteracao"));

                    long codigoProduto;
                    codigoProduto = Long.parseLong(Utils.stringLong(rst.getString("ean")));
                    String pBalanca = String.valueOf(codigoProduto);

                    ProdutoBalancaVO bal;
                    if (removeDigitoEAN) {
                        bal = balanca.get(Utils.stringToInt(pBalanca.substring(0, pBalanca.length() - 1), -2));
                    } else {
                        bal = balanca.get(Utils.stringToInt(pBalanca, -2));
                    }

                    if (bal != null) {
                        imp.setEan(String.valueOf(bal.getCodigo()));
                        imp.setQtdEmbalagem(1);
                        imp.setTipoEmbalagem("U".equals(bal.getPesavel()) ? "UN" : "KG");
                        imp.setValidade(bal.getValidade());
                    } else {
                        if (rst.getInt("e_balanca") == 1) {
                            if (pBalanca.length() < 7) {
                                imp.seteBalanca(true);
                                imp.setEan(rst.getString("ean"));
                                if (removeDigitoEAN) {
                                    imp.setEan(pBalanca.substring(0, pBalanca.length() - 1));
                                }
                            } else {
                                imp.seteBalanca(false);
                                imp.setEan(rst.getString("ean"));
                            }
                        } else {
                            imp.seteBalanca(false);
                            imp.setEan(rst.getString("ean"));
                        }
                    }

                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    imp.setQtdEmbalagemCotacao(rst.getInt("embalagemcotacao"));
                    imp.setTipoEmbalagem(rst.getString("unidade"));
                    imp.setValidade(rst.getInt("validade"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompletacomplemento"));
                    imp.setDescricaoGondola(rst.getString("descricaoreduzida"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setCodMercadologico1(rst.getString("merc1"));
                    imp.setCodMercadologico2(rst.getString("merc2"));
                    imp.setCodMercadologico3("1");
                    imp.setIdFamiliaProduto(rst.getString("id_familia"));
                    imp.setPesoBruto(rst.getDouble("pesobruto"));
                    imp.setPesoLiquido(rst.getDouble("pesoliquido"));
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setEstoqueMinimo(rst.getDouble("estoquemaximo"));
                    imp.setTipoEmbalagemVolume(rst.getString("emabalagemfuncao"));
                    imp.setVolume(rst.getDouble("fatoretiqueta"));

                    imp.setEstoque(rst.getDouble("estoque"));
                    if (utilizarCustoNota) {
                        imp.setCustoSemImposto(rst.getDouble("custosemimposto_nf"));
                        imp.setCustoComImposto(rst.getDouble("custocomimposto_nf"));
                    } else {
                        imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                        imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    }
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setSituacaoCadastro(rst.getBoolean("situacaocadastro") ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    imp.setDescontinuado(rst.getInt("descontinuado") == 1);
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getString("piscofins_s"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("piscofins_natrec"));

                    imp.setIcmsConsumidorId(rst.getString("id_icms"));
                    imp.setIcmsDebitoId(rst.getString("id_icms"));
                    imp.setIcmsDebitoForaEstadoId(rst.getString("id_icms"));
                    imp.setIcmsDebitoForaEstadoNfId(rst.getString("id_icms"));
                    imp.setIcmsCreditoId(rst.getString("id_icms"));
                    imp.setIcmsCreditoForaEstadoId(rst.getString("id_icms"));

                    imp.setFornecedorFabricante(rst.getString("prod_forn_codigo"));

                    if (rst.getString("setor") != null && !"".equals(rst.getString("setor"))) {
                        if (rst.getString("setor").length() > 2) {
                            imp.setSetor(rst.getString("setor").trim().substring(0, 2));
                        } else {
                            imp.setSetor(rst.getString("setor").trim());
                        }
                    }
                    if (rst.getString("departamento") != null && !"".equals(rst.getString("departamento"))) {
                        if (rst.getString("departamento").length() > 3) {
                            imp.setPrateleira(rst.getString("departamento").trim().substring(0, 3));
                        } else {
                            imp.setPrateleira(rst.getString("departamento").trim());
                        }
                    }

                    long ean = Utils.stringToLong(imp.getEan(), -2);

                    imp.setManterEAN(ean <= 999999 && ean > 0 && !imp.isBalanca());

                    result.add(imp);
                }
            }
        }

        return result;
    }

    //<editor-fold defaultstate="collapsed" desc="Rotina de produtos OLD">
    /*@Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	p.prod_codigo id,\n" +
                    "	p.prod_datacad datacadastro,\n" +
                    "	p.prod_dataalt dataalteracao,\n" +
                    "	ean.ean,\n" +
                    "	ean.qtdembalagem,\n" +
                    "	p.prod_qemb embalagemcotacao,\n" +
                    "	case\n" +
                    "	when p.prod_balanca = 'P' then 'KG'\n" +
                    "	when p.prod_balanca = 'U' then 'UN'\n" +
                    "	when ean.qtdembalagem = 1 then 'UN'\n" +
                    "	else un.prun_emb end unidade,\n" +
                    "	case \n" +
                    "	when p.prod_balanca in ('P', 'U') then 1\n" +
                    "	else 0 end e_balanca,\n" +
                    "	coalesce(un.prun_validade, 0) validade,\n" +
                    "	p.prod_descricao || ' ' || coalesce(p.prod_complemento, '') descricaocompletacomplemento,\n" +
                    "	p.prod_descricao descricaocompleta,        \n" +
                    "	p.prod_descrpdvs descricaoreduzida,\n" +
                    "	p.prod_dpto_codigo merc1,\n" +
                    "	p.prod_grup_codigo merc2,\n" +
                    "	p.prod_codpreco id_familia,\n" +
                    "	p.prod_peso pesobruto,\n" +
                    "	p.prod_pesoliq pesoliquido,\n" +
                    "	un.prun_estmin estoqueminimo,\n" +
                    "	un.prun_estmax estoquemaximo,\n" +
                    "	un.prun_estoque1 + un.prun_estoque2 + un.prun_estoque3 + un.prun_estoque4 + un.prun_estoque5 estoque,\n" +
                    "	un.prun_prultcomp,\n" +
                    "	un.prun_ctcompra custosemimposto,\n" +
                    "	--un.prun_ctfiscal custocomimposto,\n" +
                    "	un.prun_prultcomp custocomimposto,\n" +
                    "	un.prun_margem margem,\n" +
                    "	un.prun_prvenda precovenda,\n" +
                    "	case un.prun_ativo when 'S' then 1 else 0 end situacaocadastro,\n" +
                    "	case un.prun_bloqueado when 'N' then 0 else 1 end descontinuado,\n" +
                    "	p.prod_codigoncm ncm,\n" +
                    "	ax.prau_cest cest,\n" +
                    "	tr.trib_codigo id_tributacao,\n" +
                    "	tr.trib_data,\n" +
                    "	tr.trib_codnf cst,\n" +
                    "	tr.trib_icms icms,\n" +
                    "	tr.trib_redbc icmsreducao,\n" +
                    "	tr.trib_cstpis cstpiscofins,\n" +
                    "	tr.trib_natpiscof naturezareceita,\n" +
                    "	un.prun_setor setor,\n" +
                    "	un.prun_setordep departamento\n" +
                    "from\n" +
                    "	produtos p\n" +
                    "	left join prodaux ax on ax.prau_prod_codigo = p.prod_codigo\n" +
                    "	left join produn un on p.prod_codigo = un.prun_prod_codigo\n" +
                    "	left join (\n" +
                    "		select\n" +
                    "			prod_codigo id,\n" +
                    "			prod_codbarras ean,\n" +
                    "			prod_funcao unidade,\n" +
                    "			1 qtdembalagem\n" +
                    "		from\n" +
                    "			produtos\n" +
                    "		union\n" +
                    "		select\n" +
                    "			prod_codigo id,\n" +
                    "			prod_codcaixa ean,\n" +
                    "			prod_emb unidade,\n" +
                    "			prod_qemb qtdembalagem\n" +
                    "		from\n" +
                    "			produtos\n" +
                    "		where\n" +
                    "			nullif(trim(prod_codcaixa),'') is not null\n" +
                    "	) ean on ean.id = p.prod_codigo\n" +
                    "	left join tributacao tr on (p.prod_trib_codigo = tr.trib_codigo)\n" +
                    "where\n" +
                    "	un.prun_unid_codigo = '" + getLojaOrigem() + "' and\n" +
                    "	tr.trib_mvtos like '%EVP%' and\n" +
                    "	tr.trib_unidades like '%" + getLojaOrigem() + "%' and\n" +
                    "	tr.trib_uforigem = 'SP'\n" +
                    "order by\n" +
                    "	id"
            )) {
                Map<Integer, ProdutoBalancaVO> balanca = new ProdutoBalancaDAO().getProdutosBalanca();
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setDataAlteracao(rst.getDate("dataalteracao"));
                    
                    long codigoProduto;
                    codigoProduto = Long.parseLong(Utils.stringLong(rst.getString("ean")));
                    String pBalanca = String.valueOf(codigoProduto);
                    
                    ProdutoBalancaVO bal;
                    if (removeDigitoEAN) {
                        bal = balanca.get(Utils.stringToInt(pBalanca.substring(0, pBalanca.length() - 1), -2));
                    } else {
                        bal = balanca.get(Utils.stringToInt(pBalanca, -2));
                    }
                    
                    if (bal != null) {
                        imp.setEan(String.valueOf(bal.getCodigo()));
                        imp.setQtdEmbalagem(1);
                        imp.setTipoEmbalagem("U".equals(bal.getPesavel()) ? "UN": "KG");
                        imp.setValidade(bal.getValidade());
                    } else {
                        if (rst.getInt("e_balanca") == 1) {
                            if (pBalanca.length() < 7) {
                                imp.seteBalanca(true);
                                imp.setEan(rst.getString("ean"));
                                if (removeDigitoEAN) {
                                    imp.setEan(pBalanca.substring(0, pBalanca.length() - 1));
                                }
                            } else {
                                imp.seteBalanca(false);
                                imp.setEan(rst.getString("ean"));
                            }
                        } else {
                            imp.seteBalanca(false);
                            imp.setEan(rst.getString("ean"));
                        }
                    }
                    
                    if(imp.getEan() != null && !"".equals(imp.getEan()) && imp.getEan().length() < 7) {
                        imp.setManterEAN(true);
                    }
                    
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    imp.setQtdEmbalagemCotacao(rst.getInt("embalagemcotacao"));
                    imp.setTipoEmbalagem(rst.getString("unidade"));
                    imp.setValidade(rst.getInt("validade"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoGondola(rst.getString("descricaoreduzida"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setCodMercadologico1(rst.getString("merc1"));
                    imp.setCodMercadologico2(rst.getString("merc2"));
                    imp.setCodMercadologico3("1");
                    imp.setIdFamiliaProduto(rst.getString("id_familia"));
                    imp.setPesoBruto(rst.getDouble("pesobruto"));
                    imp.setPesoLiquido(rst.getDouble("pesoliquido"));
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setEstoqueMinimo(rst.getDouble("estoquemaximo"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setSituacaoCadastro(rst.getBoolean("situacaocadastro") ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    imp.setDescontinuado(rst.getBoolean("descontinuado"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getString("cstpiscofins"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("naturezareceita"));
                    
                    //imp.setIcmsConsumidorId(rst.getString("id_icms"));
                    //imp.setIcmsDebitoId(rst.getString("id_icms"));
                    //imp.setIcmsDebitoForaEstadoId(rst.getString("id_icms"));
                    //imp.setIcmsDebitoForaEstadoNfId(rst.getString("id_icms"));
                    //imp.setIcmsCreditoId(rst.getString("id_icms"));
                    //imp.setIcmsCreditoForaEstadoId(rst.getString("id_icms"));
                    
                    if (rst.getString("setor") != null && !"".equals(rst.getString("setor"))) {
                        if (rst.getString("setor").length() > 2) {
                            imp.setSetor(rst.getString("setor").trim().substring(0, 2));
                        } else {
                            imp.setSetor(rst.getString("setor").trim());
                        }
                    }
                    if (rst.getString("departamento") != null && !"".equals(rst.getString("departamento"))) {
                        if (rst.getString("departamento").length() > 3) {
                            imp.setPrateleira(rst.getString("departamento").trim().substring(0, 3));
                        } else {
                            imp.setPrateleira(rst.getString("departamento").trim());
                        }
                    }

                    result.add(imp);
                }
            }
        }

        return result;
    }*/
    //</editor-fold>
    @Override
    public List<ProdutoIMP> getProdutos(OpcaoProduto opt) throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        if (opt == OpcaoProduto.ATACADO) {
            try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select \n"
                        + "prun_prod_codigo,\n"
                        + "prun_prvenda,\n"
                        + "prun_fatorpr3,\n"
                        + "prun_prvenda3,\n"
                        + "prun_emb\n"
                        + "from produn \n"
                        + "where prun_unid_codigo = '" + getLojaOrigem() + "'\n"
                        + "and prun_fatorpr3 > 1\n"
                        + "and prun_prvenda3 > 0"
                )) {
                    while (rst.next()) {
                        int codigoAtual = new ProdutoAnteriorDAO().getCodigoAnterior2(getSistema(), getLojaOrigem(), rst.getString("prun_prod_codigo"));

                        if (codigoAtual > 0) {

                            ProdutoIMP imp = new ProdutoIMP();
                            imp.setImportLoja(getLojaOrigem());
                            imp.setImportSistema(getSistema());
                            imp.setImportId(rst.getString("prun_prod_codigo"));
                            imp.setEan(String.valueOf(idLojaVR) + "99999" + String.valueOf(codigoAtual));
                            imp.setTipoEmbalagem(rst.getString("prun_emb"));
                            imp.setQtdEmbalagem(rst.getInt("prun_fatorpr3"));
                            imp.setPrecovenda(rst.getDouble("prun_prvenda"));
                            imp.setAtacadoPreco(rst.getDouble("prun_prvenda3"));
                            result.add(imp);
                        }
                    }
                }
            }
            return result;
        }

        /*if (opt == OpcaoProduto.MERCADOLOGICO) {
            try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select \n"
                        + "p.prod_codigo,\n"
                        + "substring(g.grup_classificacao, 1, 1) merc1,\n"
                        + "substring(g.grup_classificacao, 2, 2) merc2,\n"
                        + "substring(g.grup_classificacao, 4, 2) merc3,\n"
                        + "substring(g.grup_classificacao, 6, 2) merc4\n"
                        + "from produtos p\n"
                        + "inner join grupos g on g.grup_codigo = p.prod_grup_codigo"
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("prod_codigo"));
                        imp.setCodMercadologico1(rst.getString("merc1"));
                        imp.setCodMercadologico2(rst.getString("merc2"));
                        imp.setCodMercadologico3(rst.getString("merc3"));
                        imp.setCodMercadologico4(rst.getString("merc4"));
                        result.add(imp);
                    }
                }
            }
            return result;
        }*/
        return null;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        String sqlPessoa = "";

        if (importarFuncionario) {
            sqlPessoa = "select \n"
                    + "	func_codigo id,\n"
                    + "	func_nome razaosocial,\n"
                    + "	'' nomefantasia,\n"
                    + "	'' inscmun,\n"
                    + "	'N' forn_status,\n"
                    + "	func_nasci,\n"
                    + "	m.muni_nome municipio,\n"
                    + "   m.muni_codigoibge municipioIBGE,\n"
                    + "	f.func_endereco endereco,\n"
                    + "	f.func_endereconumero numero,\n"
                    + "	f.func_enderecocompl complemento,\n"
                    + "	m.muni_uf uf,\n"
                    + "	f.func_bairro bairro,\n"
                    + "	f.func_cep cep,\n"
                    + "	f.func_telefone forn_fone,\n"
                    + "	f.func_rg ierg,\n"
                    + "	f.func_cpf cnpjcpf,\n"
                    + "	f.func_dtcad datacadastro,\n"
                    + "	f.func_email email,\n"
                    + "	f.func_admissao,\n"
                    + "	f.func_celular celular,\n"
                    + "	f.func_limiteconv,\n"
                    + "	f.func_limitecheque,\n"
                    + "	f.func_limitediario,\n"
                    + "	'' forn_foneindustria,\n"
                    + "	'' forn_fax,\n"
                    + "	'' forn_faxindustria,\n"
                    + "	'' forn_obspedidos,\n"
                    + "	'' forn_obstrocas,\n"
                    + "	'P' tipofornecedor,\n"
                    + "	0::integer prazo_entrega,\n"
                    + "	null forma_pagamento\n"
                    + "from\n"
                    + "	funcionarios f\n"
                    + "left join municipios m on m.muni_codigo = f.func_muni_codigo\n"
                    + "where\n"
                    + "	f.func_unid_codigo = '" + getLojaOrigem() + "'";
        } else {
            sqlPessoa = "select\n"
                    + "	f.forn_codigo id,\n"
                    + "	f.forn_nome nomefantasia,\n"
                    + "	f.forn_razaosocial razaosocial,\n"
                    + "	f.forn_cnpjcpf cnpjcpf,\n"
                    + "	f.forn_inscricaoestadual ierg,\n"
                    + "	f.forn_inscricaomunicipal inscmun,\n"
                    + "	f.forn_status,\n"
                    + "	f.forn_endereco endereco,\n"
                    + "	f.forn_endereconumero numero,\n"
                    + "	f.forn_enderecocompl complemento,\n"
                    + "	f.forn_enderecoind,\n"
                    + "	f.forn_bairro bairro,\n"
                    + "	m.muni_codigoibge municipioIBGE,\n"
                    + "	m.muni_nome municipio,\n"
                    + "	m.muni_uf uf,\n"
                    + "	f.forn_cep cep,\n"
                    + "	f.forn_fone,\n"
                    + "	f.forn_foneindustria,\n"
                    + "	f.forn_fax,\n"
                    + "	f.forn_faxindustria,\n"
                    + "	f.forn_email email,\n"
                    + "	f.forn_datacad datacadastro,\n"
                    + "	f.forn_obspedidos,\n"
                    + "	f.forn_obstrocas,\n"
                    + "	coalesce(f.forn_caractrib,'') tipofornecedor,\n"
                    + "	fc.rfor_pzentrega prazo_entrega,\n"
                    + "	fc.rfor_pzrecebimento prazo_recebimento,\n"
                    + "	fp.fpgt_prazos forma_pagamento\n"
                    + "from\n"
                    + "	fornecedores f\n"
                    + "	left join municipios m on\n"
                    + "	f.forn_muni_codigo = m.muni_codigo\n"
                    + "	left join regforn fc on f.forn_codigo = fc.rfor_forn_codigo\n"
                    + "	left join fpgto fp on fc.rfor_fpgt_codigo = fp.fpgt_codigo\n"
                    + "order by\n"
                    + "	id";
        }

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(sqlPessoa)) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razaosocial"));
                    imp.setFantasia(rst.getString("nomefantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpjcpf"));
                    imp.setIe_rg(rst.getString("ierg"));
                    imp.setInsc_municipal(rst.getString("inscmun"));
                    imp.setAtivo(!"S".equals(rst.getString("forn_status")));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setIbge_municipio(rst.getInt("municipioIBGE"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setTel_principal(rst.getString("forn_fone"));
                    imp.addTelefone("TEL INDUSTRIA", rst.getString("forn_foneindustria"));
                    imp.addTelefone("FAX", rst.getString("forn_fax"));
                    imp.addTelefone("FAX INDUSTRIA", rst.getString("forn_faxindustria"));
                    imp.setDatacadastro(rst.getDate("datacadastro"));
                    imp.setObservacao(rst.getString("forn_obspedidos") + " " + rst.getString("forn_obstrocas"));
                    /*if (rst.getString("email") != null && !"".equals(rst.getString("email"))) {
                     if(rst.getString("email").length() > 50) {
                     imp.addContato("1", "EMAIL", null, null, TipoContato.COMERCIAL, rst.getString("email").substring(0, 50));
                     } else {
                     imp.addContato("1", "EMAIL", null, null, TipoContato.COMERCIAL, rst.getString("email"));
                     }
                     }*/
                    switch (rst.getString("tipofornecedor").trim()) {
                        case "A":
                            imp.setTipoFornecedor(TipoFornecedor.DISTRIBUIDOR);
                            imp.setTipoEmpresa(TipoEmpresa.LUCRO_REAL);
                            break;
                        case "D":
                            imp.setTipoFornecedor(TipoFornecedor.DISTRIBUIDOR);
                            imp.setTipoEmpresa(TipoEmpresa.LUCRO_REAL);
                            break;
                        case "E":
                            imp.setTipoFornecedor(TipoFornecedor.PRESTADOR);
                            imp.setTipoEmpresa(TipoEmpresa.LUCRO_REAL);
                            break;
                        case "K":
                            imp.setTipoFornecedor(TipoFornecedor.DISTRIBUIDOR);
                            imp.setTipoEmpresa(TipoEmpresa.LUCRO_REAL);
                            break;
                        case "P":
                            imp.setTipoFornecedor(TipoFornecedor.PRESTADOR);
                            imp.setTipoEmpresa(TipoEmpresa.LUCRO_REAL);
                            break;
                        case "R":
                            imp.setProdutorRural();
                            break;
                        case "S":
                            imp.setTipoFornecedor(TipoFornecedor.INDUSTRIA);
                            imp.setTipoEmpresa(TipoEmpresa.ME_SIMPLES);
                            break;
                        case "F":
                            imp.setTipoFornecedor(TipoFornecedor.DISTRIBUIDOR);
                            imp.setTipoEmpresa(TipoEmpresa.LUCRO_REAL);
                            break;
                        case "O":
                            imp.setTipoFornecedor(TipoFornecedor.DISTRIBUIDOR);
                            imp.setTipoEmpresa(TipoEmpresa.SOCIEDADE_CIVIL);
                            break;
                        default:
                            imp.setTipoFornecedor(TipoFornecedor.INDUSTRIA);
                            imp.setTipoEmpresa(TipoEmpresa.LUCRO_REAL);
                            break;
                    }

                    imp.setPrazoEntrega(rst.getInt("prazo_entrega"));
                    imp.setCondicaoPagamento(Utils.stringToInt(rst.getString("forma_pagamento")));

                    addContatoFornecedor(imp);

                    imp.addDivisao(imp.getImportId(), 0, imp.getPrazoEntrega(), 0);

                    result.add(imp);
                }
            }
        }

        return result;
    }

    public void addContatoFornecedor(FornecedorIMP imp) throws SQLException {
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	cfor_codigo id,\n"
                    + "	cfor_forn_codigo id_fornecedor,\n"
                    + "	cfor_nome contato,\n"
                    + "	cfor_funcao funcao,\n"
                    + " cfor_fone telefone,\n"
                    + "	cfor_celular celular,\n"
                    + "	cfor_email email\n"
                    + "from \n"
                    + "	contforn \n"
                    + "where \n"
                    + "	cfor_forn_codigo = " + imp.getImportId())) {
                while (rs.next()) {
                    imp.addContato(rs.getString("id"),
                            rs.getString("contato"),
                            rs.getString("telefone"),
                            rs.getString("celular"),
                            TipoContato.NFE,
                            rs.getString("email"));
                }
            }
        }
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	c.clie_codigo id,\n"
                    + "	c.clie_cnpjcpf cnpjcpf,\n"
                    + "	c.clie_rgie inscricaoestadual,\n"
                    + "	c.clie_orgexprg orgaoemissor,\n"
                    + "	c.clie_razaosocial razaosocial,\n"
                    + "	c.clie_nome nomefantasia,\n"
                    + "	c.clie_status,\n"
                    + "	c.clie_situacao,\n"
                    + "	c.clie_endres endereco,\n"
                    + "	c.clie_endresnumero numero,\n"
                    + "	c.clie_endrescompl complemento,\n"
                    + "	c.clie_bairrores bairro,\n"
                    + "	mr.muni_codigoibge municipioIBGE,\n"
                    + "	mr.muni_nome municipio,\n"
                    + "	mr.muni_uf uf,\n"
                    + "	c.clie_cepres cep,\n"
                    + "	c.clie_estadocivil estadocivil,\n"
                    + "	c.clie_dtcad datacadastro,\n"
                    + " c.clie_dtnasc datanascimento,\n"
                    + "	c.clie_sexo sexo,\n"
                    + "	c.clie_empresa empresa,\n"
                    + "	c.clie_endcom com_endereco,\n"
                    + "	c.clie_endcomnumero com_numero,\n"
                    + "	c.clie_endcomcompl com_compl,\n"
                    + "	c.clie_bairrocom com_bairro,\n"
                    + "	mc.muni_codigoibge com_municipioIBGE,\n"
                    + "	mc.muni_nome com_municipio,\n"
                    + "	mc.muni_uf com_uf,\n"
                    + "	c.clie_cepcom com_cep,\n"
                    + "	c.clie_foneres,\n"
                    + "	c.clie_fonecel,\n"
                    + "	c.clie_fonecom,\n"
                    + "	c.clie_fonecelcom,\n"
                    + "	c.clie_email,\n"
                    + "	c.clie_emailnfe,\n"
                    + "	c.clie_dtadmissao dataadmissao,\n"
                    + "	c.clie_funcao cargo,\n"
                    + "	c.clie_rendacomprovada renda,\n"
                    + "	c.clie_limiteconv,\n"
                    + " c.clie_limitecheque,\n"
                    + "	c.clie_obs observacao,\n"
                    + "	c.clie_diavenc diavencimento,\n"
                    + "	c.clie_sitconv permitecreditorotativo,\n"
                    + "	c.clie_sitcheque permitecheque,\n"
                    + "	c.clie_senhapdv senhapdv,\n"
                    + " c.clie_descrestadocivil civil,\n"
                    + " c.clie_contacontabil,\n"
                    + "	c.clie_contagerencial\n"
                    + "from\n"
                    + "	clientes c\n"
                    + "	left join municipios mr on\n"
                    + "		c.clie_muni_codigo_res = mr.muni_codigo\n"
                    + "	left join municipios mc on\n"
                    + "		c.clie_muni_codigo_com = mc.muni_codigo\n"
                    + "order by\n"
                    + "	id")) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rst.getString("id"));
                    imp.setCnpj(rst.getString("cnpjcpf"));
                    imp.setInscricaoestadual(rst.getString("inscricaoestadual"));
                    imp.setOrgaoemissor(rst.getString("orgaoemissor"));
                    imp.setRazao(rst.getString("razaosocial"));
                    imp.setFantasia(rst.getString("nomefantasia"));
                    imp.setAtivo(!"S".equals(rst.getString("clie_status")));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipioIBGE(rst.getInt("municipioIBGE"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setEstadoCivil(rst.getString("estadocivil"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setDataNascimento(rst.getDate("datanascimento"));
                    imp.setSexo("F".equals(rst.getString("sexo")) ? TipoSexo.FEMININO : TipoSexo.MASCULINO);
                    imp.setEmpresa(rst.getString("empresa"));
                    imp.setEmpresaEndereco(rst.getString("com_endereco"));
                    imp.setEmpresaNumero(rst.getString("com_numero"));
                    imp.setEmpresaComplemento(rst.getString("com_compl"));
                    imp.setEmpresaBairro(rst.getString("com_bairro"));
                    imp.setEmpresaMunicipioIBGE(rst.getInt("com_municipioIBGE"));
                    imp.setEmpresaMunicipio(rst.getString("com_municipio"));
                    imp.setEmpresaUf(rst.getString("com_uf"));
                    imp.setEmpresaCep(rst.getString("com_cep"));
                    imp.setTelefone(rst.getString("clie_foneres"));
                    imp.setCelular(rst.getString("clie_fonecel"));
                    imp.setEmpresaTelefone(rst.getString("clie_fonecom"));
                    imp.addCelular("CEL COMERCIAL", rst.getString("clie_fonecelcom"));
                    imp.addEmail(rst.getString("clie_email"), TipoContato.COMERCIAL);
                    imp.addEmail(rst.getString("clie_emailnfe"), TipoContato.NFE);
                    imp.setDataAdmissao(rst.getDate("dataadmissao"));
                    imp.setCargo(rst.getString("cargo"));
                    imp.setSalario(rst.getDouble("renda"));
                    imp.setValorLimite(rst.getDouble("clie_limiteconv") == 0 ? rst.getDouble("clie_limitecheque") : rst.getDouble("clie_limiteconv"));
                    if (imp.getValorLimite() > 0) {
                        imp.setPermiteCheque(true);
                        imp.setPermiteCreditoRotativo(true);
                    }
                    imp.setObservacao2(rst.getString("observacao"));
                    imp.setDiaVencimento(rst.getInt("diavencimento"));
                    //imp.setPermiteCreditoRotativo("S".equals(rst.getString("permitecreditorotativo")));
                    //imp.setPermiteCheque("S".equals(rst.getString("permitecheque")));
                    imp.setSenha(Integer.valueOf(Utils.formataNumero(rst.getString("senhapdv"))));
                    imp.setBloqueado("I".equals(rst.getString("clie_situacao")));

                    if (rst.getString("civil") != null && !"".equals(rst.getString("civil"))) {
                        String estCivil = rst.getString("civil");
                        switch (estCivil.toUpperCase().trim()) {
                            case "SOLTEIRO":
                                imp.setEstadoCivil(TipoEstadoCivil.SOLTEIRO);
                                break;
                            case "CASADO":
                                imp.setEstadoCivil(TipoEstadoCivil.CASADO);
                                break;
                            case "DIVORCIADO":
                                imp.setEstadoCivil(TipoEstadoCivil.DIVORCIADO);
                                break;
                            case "VIÚVO":
                                imp.setEstadoCivil(TipoEstadoCivil.VIUVO);
                                break;
                            default:
                                imp.setEstadoCivil(TipoEstadoCivil.NAO_INFORMADO);
                                break;
                        }
                    }

                    if (rst.getString("clie_contacontabil") != null && rst.getString("clie_contagerencial") != null) {
                        imp.setPermiteCreditoRotativo(true);
                        imp.setPermiteCheque(true);
                    }

                    result.add(imp);
                }
            }
            if (importarFuncionario) {
                try (ResultSet rst = stm.executeQuery(
                        "select\n"
                        + "	cast(c.clie_codigo as varchar) id,\n"
                        + "	c.clie_cnpjcpf cnpjcpf,\n"
                        + "	c.clie_rgie inscricaoestadual,\n"
                        + "	c.clie_orgexprg orgaoemissor,\n"
                        + "	c.clie_razaosocial razaosocial,\n"
                        + "	c.clie_nome nomefantasia,\n"
                        + "	c.clie_status,\n"
                        + "	c.clie_situacao,\n"
                        + "	c.clie_endres endereco,\n"
                        + "	c.clie_endresnumero numero,\n"
                        + "	c.clie_endrescompl complemento,\n"
                        + "	c.clie_bairrores bairro,\n"
                        + "	mr.muni_codigoibge municipioIBGE,\n"
                        + "	mr.muni_nome municipio,\n"
                        + "	mr.muni_uf uf,\n"
                        + "	c.clie_cepres cep,\n"
                        + "	c.clie_estadocivil estadocivil,\n"
                        + "	c.clie_dtcad datacadastro,\n"
                        + "   	c.clie_dtnasc datanascimento,\n"
                        + "	c.clie_sexo sexo,\n"
                        + "	c.clie_empresa empresa,\n"
                        + "	c.clie_endcom com_endereco,\n"
                        + "	c.clie_endcomnumero com_numero,\n"
                        + "	c.clie_endcomcompl com_compl,\n"
                        + "	c.clie_bairrocom com_bairro,\n"
                        + "	mc.muni_codigoibge com_municipioIBGE,\n"
                        + "	mc.muni_nome com_municipio,\n"
                        + "	mc.muni_uf com_uf,\n"
                        + "	c.clie_cepcom com_cep,\n"
                        + "	c.clie_foneres,\n"
                        + "	c.clie_fonecel,\n"
                        + "	c.clie_fonecom,\n"
                        + "	c.clie_fonecelcom,\n"
                        + "	c.clie_email,\n"
                        + "	c.clie_emailnfe,\n"
                        + "	c.clie_dtadmissao dataadmissao,\n"
                        + "	c.clie_funcao cargo,\n"
                        + "	c.clie_rendacomprovada renda,\n"
                        + "	c.clie_limiteconv,\n"
                        + "	c.clie_limitecheque,\n"
                        + "	c.clie_obs observacao,\n"
                        + "	c.clie_diavenc diavencimento,\n"
                        + "	c.clie_sitconv permitecreditorotativo,\n"
                        + "	c.clie_sitcheque permitecheque,\n"
                        + "	c.clie_senhapdv senhapdv\n"
                        + "from\n"
                        + "	clientes c\n"
                        + "	left join municipios mr on\n"
                        + "		c.clie_muni_codigo_res = mr.muni_codigo\n"
                        + "	left join municipios mc on\n"
                        + "		c.clie_muni_codigo_com = mc.muni_codigo\n"
                        + "union all\n"
                        + "select \n"
                        + "	'FUN' || '' || cast(f.func_codigo as varchar) as id,\n"
                        + "	f.func_cpf cnpjcpf,\n"
                        + "	f.func_rg inscricaoestadual,\n"
                        + "	f.func_rgexp orgaoemissor,\n"
                        + "	f.func_nome razaosocial,\n"
                        + "	'' nomefantasia,\n"
                        + "	'N' clie_status,\n"
                        + "	'N' clie_situacao,\n"
                        + "	f.func_endereco endereco,\n"
                        + "	f.func_endereconumero numero,\n"
                        + "	f.func_enderecocompl complemento,\n"
                        + "	f.func_bairro bairro,\n"
                        + "	mr.muni_codigoibge municipioIBGE,\n"
                        + "	mr.muni_nome municipio,\n"
                        + "	mr.muni_uf uf,\n"
                        + "	f.func_cep cep,\n"
                        + "	f.func_estcivil estadocivil,\n"
                        + "	f.func_dtcad datacadastro,\n"
                        + "	f.func_nasci datanascimento,\n"
                        + "	f.func_sexo sexo,\n"
                        + "	'' empresa,\n"
                        + "	'' com_endereco,\n"
                        + "	'' com_numero,\n"
                        + "	'' com_compl,\n"
                        + "	'' com_bairro,\n"
                        + "	0 com_municipioIBGE,\n"
                        + "	'' com_municipio,\n"
                        + "	'' com_uf,\n"
                        + "	0::varchar com_cep,\n"
                        + "	f.func_telefone clie_foneres,\n"
                        + "	f.func_celular clie_fonecel,\n"
                        + "	0::varchar clie_fonecom,\n"
                        + "	0::varchar clie_fonecelcom,\n"
                        + "	f.func_email clie_email,\n"
                        + "	'' clie_emailnfe,\n"
                        + "	f.func_admissao dataadmissao,\n"
                        + " 	'' cargo,\n"
                        + " 	f.func_salbase renda,\n"
                        + " 	f.func_limiteconv clie_limiteconv,\n"
                        + " 	f.func_limitecheque clie_limitecheque,\n"
                        + " 	f.func_observacao observacao,\n"
                        + " 	0 diavencimento,\n"
                        + " 	'N' permitecreditorotativo,\n"
                        + " 	'N' permitecheque,\n"
                        + " 	f.func_senha senhapdv\n"
                        + "from \n"
                        + "	funcionarios f\n"
                        + "left join municipios mr on\n"
                        + "		f.func_muni_codigo = mr.muni_codigo\n"
                        + "where f.func_unid_codigo = '" + getLojaOrigem() + "'\n"
                        + "order by\n"
                        + "	1"
                )) {
                    while (rst.next()) {
                        ClienteIMP imp = new ClienteIMP();

                        imp.setId(rst.getString("id"));
                        imp.setCnpj(rst.getString("cnpjcpf"));
                        imp.setInscricaoestadual(rst.getString("inscricaoestadual"));
                        imp.setOrgaoemissor(rst.getString("orgaoemissor"));
                        imp.setRazao(rst.getString("razaosocial"));
                        imp.setFantasia(rst.getString("nomefantasia"));
                        imp.setAtivo(!"S".equals(rst.getString("clie_status")));
                        imp.setEndereco(rst.getString("endereco"));
                        imp.setNumero(rst.getString("numero"));
                        imp.setComplemento(rst.getString("complemento"));
                        imp.setBairro(rst.getString("bairro"));
                        imp.setMunicipioIBGE(rst.getInt("municipioIBGE"));
                        imp.setMunicipio(rst.getString("municipio"));
                        imp.setUf(rst.getString("uf"));
                        imp.setCep(rst.getString("cep"));
                        imp.setEstadoCivil(rst.getString("estadocivil"));
                        imp.setDataCadastro(rst.getDate("datacadastro"));
                        imp.setDataNascimento(rst.getDate("datanascimento"));
                        imp.setSexo("F".equals(rst.getString("sexo")) ? TipoSexo.FEMININO : TipoSexo.MASCULINO);
                        imp.setEmpresa(rst.getString("empresa"));
                        imp.setEmpresaEndereco(rst.getString("com_endereco"));
                        imp.setEmpresaNumero(rst.getString("com_numero"));
                        imp.setEmpresaComplemento(rst.getString("com_compl"));
                        imp.setEmpresaBairro(rst.getString("com_bairro"));
                        imp.setEmpresaMunicipioIBGE(rst.getInt("com_municipioIBGE"));
                        imp.setEmpresaMunicipio(rst.getString("com_municipio"));
                        imp.setEmpresaUf(rst.getString("com_uf"));
                        imp.setEmpresaCep(rst.getString("com_cep"));
                        imp.setTelefone(rst.getString("clie_foneres"));
                        imp.setCelular(rst.getString("clie_fonecel"));
                        imp.setEmpresaTelefone(rst.getString("clie_fonecom"));
                        imp.addCelular("CEL COMERCIAL", rst.getString("clie_fonecelcom"));
                        imp.addEmail(rst.getString("clie_email"), TipoContato.COMERCIAL);
                        imp.addEmail(rst.getString("clie_emailnfe"), TipoContato.NFE);
                        imp.setDataAdmissao(rst.getDate("dataadmissao"));
                        imp.setCargo(rst.getString("cargo"));
                        imp.setSalario(rst.getDouble("renda"));
                        imp.setValorLimite(rst.getDouble("clie_limiteconv") == 0 ? rst.getDouble("clie_limitecheque") : rst.getDouble("clie_limiteconv"));
                        imp.setObservacao2(rst.getString("observacao"));
                        imp.setDiaVencimento(rst.getInt("diavencimento"));
                        imp.setPermiteCreditoRotativo("S".equals(rst.getString("permitecreditorotativo")));
                        imp.setPermiteCheque("S".equals(rst.getString("permitecheque")));
                        imp.setSenha(Integer.valueOf(Utils.formataNumero(rst.getString("senhapdv"))));
                        imp.setBloqueado("I".equals(rst.getString("clie_situacao")));

                        result.add(imp);
                    }
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
                    "select\n"
                    + "	unid_codigo id,\n"
                    + "	unid_cnpj cnpj,\n"
                    + "	unid_inscricaoestadual ie,\n"
                    + "	unid_razaosocial razao,\n"
                    + "	unid_endereco endereco,\n"
                    + "	unid_endereconumero numero,\n"
                    + "	unid_enderecocompl complemento,\n"
                    + "	unid_bairro bairro,\n"
                    + "	unid_municipio cidade,\n"
                    + "	unid_uf uf,\n"
                    + "	unid_cep cep,\n"
                    + "	unid_fone telefone\n"
                    + "from\n"
                    + "	unidades\n"
                    + "order by 1"
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
                    + "	func_unid_codigo id_empresa,\n"
                    + "	func_cpf cpfcnpj,\n"
                    + "	func_codigo id_conveniado,\n"
                    + "	func_limiteconv limite,\n"
                    + "	func_nome nome_conveniado,\n"
                    + "case when func_situacao in ('05','06','08') then 1 else 0 end bloqueado\n"
                    + "from\n"
                    + "	funcionarios\n"
                    + "where\n"
                    + "	func_unid_codigo = '" + getLojaOrigem() + "'\n"
                    + "order by 1"
            )) {
                while (rs.next()) {
                    ConveniadoIMP imp = new ConveniadoIMP();

                    imp.setIdEmpresa(rs.getString("id_empresa"));
                    imp.setCnpj(rs.getString("cpfcnpj"));
                    imp.setId(rs.getString("id_conveniado"));
                    imp.setConvenioLimite(rs.getDouble("limite"));
                    imp.setNome(rs.getString("nome_conveniado"));
                    imp.setBloqueado(rs.getBoolean("bloqueado"));

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
            try (ResultSet rs = stm.executeQuery(
                    /*"select \n"
                    + "	pfin_pger_conta conta,\n"
                    + "   pfin_unid_codigo unidade,\n"
                    + "	pfin_operacao id,\n"
                    + "	pfin_dataemissao emissao,\n"
                    + "	pfin_datavcto vencimento,\n"
                    + "	pfin_pdvs_codigo ecf,\n"
                    + "	pfin_codentidade::varchar idcliente,\n"
                    + "	c.clie_razaosocial razao,\n"
                    + "	c.clie_cnpjcpf cnpj,\n"
                    + "	pfin_complemento observacao,\n"
                    + "	pfin_numerodcto cupom,\n"
                    + "	pfin_parcela parcela,\n"
                    + "	pfin_valor valor,\n"
                    + "	pfin_baixaparcial valorpago,\n"
                    + "	pfin_juros juros,\n"
                    + "	pfin_multa multa\n"
                    + "from \n"
                    + "	pendfin\n"
                    + "join clientes c on (pendfin.pfin_codentidade = c.clie_codigo)\n"
                    + "where\n"
                    + "	pfin_unid_codigo = '" + getLojaOrigem() + "' and\n"
                    + "	pfin_pr = 'R' and\n"
                    + "	pfin_status = 'P' and\n"
                    + "   pfin_catentidade = 'C' and\n"
                    + "   not pfin_pger_conta in (117301, 112501) and \n"
                    + "   c.clie_tipo = 'F'"*/
                    "select\n"
                    + "	pfin_operacao id,\n"
                    + "	pfin_dataemissao emissao,\n"
                    + "	pfin_datavcto vencimento,\n"
                    + "	pfin_pdvs_codigo ecf,\n"
                    + "	pfin_codentidade id_conveniado,\n"
                    + "	pfin_cupom cupom,\n"
                    + "	pfin_valor valor,\n"
                    + "	pfin_observacao observacao\n"
                    + "from\n"
                    + "	pendfin ct\n"
                    + "	join funcionarios f on f.func_codigo = ct.pfin_codentidade\n"
                    + "where\n"
                    + "	pfin_unid_codigo = '" + getLojaOrigem() + "'\n"
                    + "	and pfin_contabaixa = 0\n"
                    + "	and pfin_fpgt_codigo = ''\n"
                    + "order by 1,2")) {

                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                while (rs.next()) {
                    ConvenioTransacaoIMP imp = new ConvenioTransacaoIMP();

                    imp.setId(rs.getString("id"));
                    imp.setDataMovimento(rs.getDate("emissao"));
                    imp.setDataHora(new Timestamp(format.parse(imp.getDataMovimento() + " 00:00:00").getTime()));
                    imp.setEcf(rs.getString("ecf"));
                    imp.setIdConveniado(rs.getString("id_conveniado"));
                    imp.setNumeroCupom(rs.getString("cupom"));
                    imp.setValor(rs.getDouble("valor"));
                    imp.setObservacao(rs.getString("observacao"));

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
                    + "	pf.pfor_forn_codigo id_fornecedor,\n"
                    + "	pf.pfor_prod_codigo id_produto,\n"
                    + "	pf.pfor_codigo codigoexterno,\n"
                    + "	pf.pfor_emb unidade,\n"
                    + "	case pf.pfor_qemb when 0 then 1 else pf.pfor_qemb end qemb\n"
                    + "from\n"
                    + "	prodfor pf\n"
                    + "order by\n"
                    + "	1, 2"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setIdFornecedor(rst.getString("id_fornecedor"));
                    imp.setIdProduto(rst.getString("id_produto"));
                    imp.setCodigoExterno(rst.getString("codigoexterno"));
                    imp.setQtdEmbalagem(rst.getDouble("qemb"));

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
            if (importarFuncionario) {
                try (ResultSet rs = stm.executeQuery(
                        "select \n"
                        + "	pfin_operacao id,\n"
                        + "	pfin_dataemissao emissao,\n"
                        + "	pfin_datavcto vencimento,\n"
                        + "	pfin_pdvs_codigo ecf,\n"
                        + "	pfin_codentidade::varchar idcliente,\n"
                        + "	c.clie_razaosocial razao,\n"
                        + "	c.clie_cnpjcpf cnpj,\n"
                        + "	pfin_complemento observacao,\n"
                        + "	pfin_numerodcto cupom,\n"
                        + "	pfin_parcela parcela,\n"
                        + "	pfin_valor valor\n"
                        + "from \n"
                        + "	pendfin\n"
                        + "	left join clientes c on (pendfin.pfin_codentidade = c.clie_codigo)\n"
                        + "where\n"
                        + "	pfin_unid_codigo = '" + getLojaOrigem() + "' and\n"
                        + "	pfin_pr = 'R' and\n"
                        + "	pfin_status = 'P' and\n"
                        + "	pfin_pger_conta in (112152) \n"
                        + "union all \n"
                        + "select \n"
                        + "	pfin_operacao id,\n"
                        + "	pfin_dataemissao emissao,\n"
                        + "	pfin_datavcto vencimento,\n"
                        + "	pfin_pdvs_codigo ecf,\n"
                        + "	'FUN' || '' || pfin_codentidade::varchar idcliente,\n"
                        + "	f.func_nome razao,\n"
                        + "	f.func_cpf cnpj,\n"
                        + "	pfin_complemento observacao,\n"
                        + "	pfin_numerodcto cupom,\n"
                        + "	pfin_parcela parcela,\n"
                        + "	pfin_valor valor\n"
                        + "from \n"
                        + "	pendfin\n"
                        + "	left join funcionarios f on (pendfin.pfin_codentidade = f.func_codigo)\n"
                        + "where\n"
                        + "	pfin_unid_codigo = '" + getLojaOrigem() + "' and\n"
                        + "	pfin_pr = 'R' and\n"
                        + "	pfin_status = 'P' and\n"
                        + "	pfin_pger_conta in (112152) ")) {
                    while (rs.next()) {
                        CreditoRotativoIMP imp = new CreditoRotativoIMP();

                        imp.setId(rs.getString("id"));
                        imp.setDataEmissao(rs.getDate("emissao"));
                        imp.setDataVencimento(rs.getDate("vencimento"));
                        imp.setEcf(rs.getString("ecf"));
                        imp.setIdCliente(rs.getString("idcliente"));
                        imp.setCnpjCliente(rs.getString("cnpj"));
                        imp.setParcela(rs.getInt("parcela"));
                        imp.setValor(rs.getDouble("valor"));
                        imp.setNumeroCupom(rs.getString("cupom"));

                        incluirLancamentos(imp);

                        result.add(imp);
                    }
                }
            } else {
                try (ResultSet rs = stm.executeQuery(
                        "select \n"
                        + "	pfin_pger_conta conta,\n"
                        + "       pfin_unid_codigo unidade,\n"
                        + "	pfin_operacao id,\n"
                        + "	pfin_dataemissao emissao,\n"
                        + "	pfin_datavcto vencimento,\n"
                        + "	pfin_pdvs_codigo ecf,\n"
                        + "	pfin_codentidade::varchar idcliente,\n"
                        + "	c.clie_razaosocial razao,\n"
                        + "	c.clie_cnpjcpf cnpj,\n"
                        + "	pfin_complemento observacao,\n"
                        + "	pfin_numerodcto cupom,\n"
                        + "	pfin_parcela parcela,\n"
                        + "	pfin_valor valor,\n"
                        + "	pfin_baixaparcial valorpago,\n"
                        + "	pfin_juros juros,\n"
                        + "	pfin_multa multa\n"
                        + "from \n"
                        + "	pendfin\n"
                        + "join clientes c on (pendfin.pfin_codentidade = c.clie_codigo)\n"
                        + "where\n"
                        + "	pfin_unid_codigo = '" + getLojaOrigem() + "' and\n"
                        + "	pfin_pr = 'R' and\n"
                        + "	pfin_status = 'P' and\n"
                        + "     pfin_catentidade = 'C' and\n"
                        + "     not pfin_pger_conta in (117301, 112501) and\n"
                        + "     c.clie_tipo = 'J'"
                )) {
                    while (rs.next()) {
                        CreditoRotativoIMP imp = new CreditoRotativoIMP();

                        imp.setId(rs.getString("id"));
                        imp.setDataEmissao(rs.getDate("emissao"));
                        imp.setDataVencimento(rs.getDate("vencimento"));
                        imp.setEcf(rs.getString("ecf"));
                        imp.setIdCliente(rs.getString("idcliente"));
                        imp.setCnpjCliente(rs.getString("cnpj"));
                        imp.setParcela(rs.getInt("parcela"));
                        imp.setValor(rs.getDouble("valor"));
                        //imp.setJuros(rs.getDouble("juros"));
                        //imp.setMulta(rs.getDouble("multa"));
                        imp.setNumeroCupom(rs.getString("cupom"));
                        String format = String.format("LOJA %s CONTA %s OBS %s",
                                rs.getString("unidade"),
                                rs.getString("conta"),
                                rs.getString("observacao")
                        );
                        System.out.println(format);
                        imp.setObservacao(format);

                        double valorPago = rs.getDouble("valorpago");
                        if (valorPago > 0) {
                            imp.addPagamento(
                                    rs.getString("id"),
                                    valorPago,
                                    0,
                                    0,
                                    imp.getDataEmissao(),
                                    ""
                            );
                        }

                        result.add(imp);
                    }
                }
            }

        }
        return result;
    }

    @Override
    public List<ContaPagarIMP> getContasPagar() throws Exception {
        List<ContaPagarIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	pg.pfin_transacao,\n"
                    + "	pg.pfin_operacao,\n"
                    + "	case\n"
                    + "		when pfin_codentidade is null then 908924\n" /* <<-- ID_FORNECEDOR da LOJA MATRIZ */
                    + "		else pfin_codentidade\n"
                    + "	end  id_fornecedor,\n"
                    + "	pg.pfin_numerodcto numerodocumento,\n"
                    + " pg.pfin_descontos desconto,\n"
                    + "	pg.pfin_dataemissao dataemissao,\n"
                    + "	pg.pfin_datalcto dataentrada,\n"
                    + "	pg.pfin_valor valor,\n"
                    + "	pg.pfin_datavcto vencimento,\n"
                    + "	pg.pfin_parcela parcela,\n"
                    + "	pg.pfin_observacao observacao,\n"
                    + "	pg.pfin_banco banco,\n"
                    + "	pg.pfin_agencia agencia,\n"
                    + "	pg.pfin_espe_codigo id_especie\n"
                    + "from\n"
                    + "	pendfin pg\n"
                    + "    join planoger pl on\n"
                    + "    	pg.pfin_pger_conta = pl.pger_conta\n"
                    + "    left join fornecedores f on\n"
                    + "    	pg.pfin_codentidade = f.forn_codigo\n"
                    + "where\n"
                    + "    pg.pfin_status in ('P', 'G')\n"
                    + "    and pg.pfin_pr = 'P'\n"
                    + "    and pg.pfin_catentidade in ('F','N')\n"
                    + "    and (case when pg.pfin_seqbaixa = 0 then null else pg.pfin_seqbaixa end) is null\n"
                    + "    and pg.pfin_unid_codigo = '" + getLojaOrigem() + "'\n"
                    + "order by 1, 2"
            )) {
                while (rs.next()) {
                    ContaPagarIMP imp = new ContaPagarIMP();

                    imp.setId(String.format("%s-%s", rs.getString("pfin_transacao"), rs.getString("pfin_operacao")));
                    imp.setIdFornecedor(rs.getString("id_fornecedor"));
                    imp.setNumeroDocumento(rs.getString("numerodocumento"));
                    imp.setDataEmissao(rs.getDate("dataemissao"));
                    imp.setDataEntrada(rs.getDate("dataentrada"));

                    double desconto = rs.getDouble("desconto");

                    imp.setValor(rs.getDouble("valor") - desconto);
                    imp.setVencimento(rs.getDate("vencimento"));

                    String observacao = "";

                    if (rs.getString("observacao") != null
                            && !rs.getString("observacao").isEmpty()
                            && desconto > 0) {
                        observacao = rs.getString("observacao")
                                + " - Valor total: " + rs.getDouble("valor") + " desconto(R$): " + desconto;
                    } else if (rs.getString("observacao") == null
                            && desconto > 0) {
                        observacao = "Valor total: " + rs.getDouble("valor") + " desconto(R$): " + desconto;
                    } else if (rs.getString("observacao").isEmpty()
                            && desconto > 0) {
                        observacao = "Valor total: " + rs.getDouble("valor") + " desconto(R$): " + desconto;
                    }

                    ContaPagarVencimentoIMP parc = imp.addVencimento(rs.getDate("vencimento"),
                            rs.getDouble("valor") - desconto, observacao);

                    parc.setNumeroParcela(rs.getInt("parcela"));
                    parc.setId_banco(Utils.stringToInt(rs.getString("banco")));
                    parc.setAgencia(rs.getString("agencia"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    private void incluirLancamentos(CreditoRotativoIMP imp) throws Exception {
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "pfin_operacao id,\n"
                    + "pfin_dataemissao datapagamento,\n"
                    + "pfin_multa multa,\n"
                    + "pfin_descontos desconto,\n"
                    + "pfin_baixaparcial valor\n"
                    + "from pendfin\n"
                    + "where pfin_operacao = '" + imp.getId() + "'"
            )) {
                while (rst.next()) {
                    imp.addPagamento(
                            rst.getString("id"),
                            rst.getDouble("valor"),
                            rst.getDouble("desconto"),
                            rst.getDouble("multa"),
                            rst.getDate("datapagamento"),
                            "IMPORTADO VR"
                    );
                }
            }
        }
    }

    @Override
    public List<ChequeIMP> getCheques() throws Exception {
        List<ChequeIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "pfin_operacao id,\n"
                    + "pfin_dataemissao emissao,\n"
                    + "pfin_datavcto vencimento,\n"
                    + "pfin_pdvs_codigo ecf,\n"
                    + "pfin_codentidade idcliente,\n"
                    + "c.clie_razaosocial razao,\n"
                    + "c.clie_cnpjcpf cnpj,\n"
                    + "c.clie_rgie as rg,\n"
                    + "c.clie_foneres as telefone,\n"
                    + "pfin_complemento observacao,\n"
                    + "pfin_numerodcto cupom,\n"
                    + "pfin_parcela parcela,\n"
                    + "pfin_valor valor\n"
                    + "from pendfin\n"
                    + "left join clientes c on (pendfin.pfin_codentidade = c.clie_codigo)\n"
                    + "where\n"
                    + "pfin_unid_codigo = '" + getLojaOrigem() + "' \n"
                    + "and pfin_pr = 'R' \n"
                    + "and pfin_status = 'P' \n"
                    + "and pfin_pger_conta in (112501)"
            )) {
                while (rst.next()) {
                    ChequeIMP imp = new ChequeIMP();
                    imp.setId(rst.getString("id"));
                    imp.setDate(rst.getDate("emissao"));
                    imp.setDataDeposito(rst.getDate("vencimento"));
                    imp.setEcf(rst.getString("ecf"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setNome(rst.getString("razao"));
                    imp.setCpf(rst.getString("cnpj"));
                    imp.setRg(rst.getString("rg"));
                    imp.setTelefone(rst.getString("telefone"));
                    imp.setObservacao(rst.getString("observacao"));
                    imp.setAlinea(0);
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ReceitaIMP> getReceitas() throws Exception {
        List<ReceitaIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	rp.indc_prod_codigo id_produto,\n"
                    + "	rp.indc_descricao descricao,\n"
                    + "	rp.indc_rendimento rendimento,\n"
                    + "	rp.indc_datacusto datacusto\n"
                    + "from\n"
                    + "	industc rp\n"
                    + "order by\n"
                    + "	rp.indc_prod_codigo")) {
                while (rs.next()) {
                    ReceitaIMP imp = new ReceitaIMP();

                    imp.setImportloja(getLojaOrigem());
                    imp.setImportsistema(getSistema());
                    imp.setImportid(rs.getString("id_produto"));
                    imp.setDescricao(rs.getString("descricao"));
                    imp.setRendimento(rs.getDouble("rendimento"));
                    imp.setIdproduto(rs.getString("id_produto"));
                    imp.setId_situacaocadastro(SituacaoCadastro.ATIVO);

                    addProdutoReceita(imp);

                    result.add(imp);
                }
            }
        }
        return result;
    }

    private void addProdutoReceita(ReceitaIMP imp) throws SQLException {
        Set<String> produtos = new HashSet<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	rp.indc_prod_codigo id_produto,\n"
                    + "	rp.indc_descricao descricao,\n"
                    + "	rp.indc_rendimento rendimento,\n"
                    + "	rp.indc_datacusto datacusto,\n"
                    + "	ri.indd_cod2 id_produto_producao,\n"
                    + "	pr.prod_descricao descricao_producao,\n"
                    + "	ri.indd_qreceita qtdproducao,\n"
                    + " ri.indd_qcomercializacao qtdproduto,\n"
                    + "	ri.indd_unid_codigo,\n"
                    + " ri.indd_fase fase\n"
                    + "from\n"
                    + "	industc rp\n"
                    + "join industd ri on rp.indc_prod_codigo = ri.indd_cod1\n"
                    + "join produtos pr on ri.indd_cod2 = pr.prod_codigo\n"
                    + "where\n"
                    + "   rp.indc_prod_codigo = " + imp.getIdproduto() + "\n"
                    + "order by\n"
                    + "	rp.indc_prod_codigo")) {
                while (rs.next()) {
                    produtos.add(rs.getString("id_produto_producao"));
                    imp.setQtdembalagemreceita(rs.getInt("qtdproducao"));
                    imp.setQtdembalagemproduto(rs.getInt("qtdproduto"));
                }
            }
        }
        imp.setProdutos(produtos);
    }

    /* Migração de vendas */
    private Date dataInicioVenda;
    private Date dataTerminoVenda;
    private String tabelaVenda;

    public void setDataInicioVenda(Date dataInicioVenda) {
        this.dataInicioVenda = dataInicioVenda;
    }

    public void setDataTerminoVenda(Date dataTerminoVenda) {
        this.dataTerminoVenda = dataTerminoVenda;
    }

    public void setTabelaVenda(String tabelaVenda) {
        this.tabelaVenda = tabelaVenda;
    }

    public String getTabelaVenda() {
        return this.tabelaVenda;
    }

    @Override
    public Iterator<VendaIMP> getVendaIterator() throws Exception {
        return new RPInfoDAO.VendaIterator(getLojaOrigem(), dataInicioVenda, dataTerminoVenda, getTabelaVenda());
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new RPInfoDAO.VendaItemIterator(getLojaOrigem(), dataInicioVenda, dataTerminoVenda, getTabelaVenda());
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
                SimpleDateFormat timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                if (next == null) {
                    if (rst.next()) {
                        next = new VendaIMP();
                        String id = rst.getString("id");

                        if (!uk.add(id)) {
                            LOG.warning("Venda " + id + " já existe na listagem");
                        }

                        next.setId(id);
                        next.setNumeroCupom(rst.getInt("cupom"));
                        next.setEcf(rst.getInt("ecf"));
                        next.setData(rst.getDate("datavenda"));
                        String horaInicio = timestampDate.format(rst.getDate("datavenda")) + " 00:00:00";
                        String horaTermino = timestampDate.format(rst.getDate("datavenda")) + " 00:00:00";
                        next.setHoraInicio(timestamp.parse(horaInicio));
                        next.setHoraTermino(timestamp.parse(horaTermino));
                        next.setSubTotalImpressora(rst.getDouble("valor"));
                        next.setValorDesconto(rst.getDouble("desconto"));
                        next.setCancelado(rst.getBoolean("cancelado"));
                    }
                }
            } catch (SQLException | ParseException ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaIterator(String idLojaCliente, Date dataInicio, Date dataTermino, String tabelaVenda) throws Exception {
            this.sql
                    = "select\n"
                    + "  vdet_transacao||'.'||vdet_cupom||'.'||vdet_hora||'.'||vdet_pdv||'0' as id,\n"
                    + "  vdet_cupom||0 as cupom,\n"
                    + "  0 cancelado,\n"
                    + "  vdet_datamvto datavenda,\n"
                    + "  vdet_pdv||vdet_hora ecf,\n"
                    + "  vdet_unid_codigo,\n"
                    + "  sum(vdet_qtde) quantidade,\n"
                    + "  sum(vdet_valor) valor,\n"
                    + "  sum(vdet_valordesc) desconto\n"
                    + "from vdadet" + tabelaVenda + "\n"
                    + "where vdet_status in ('N') and vdet_unid_codigo = '" + idLojaCliente + "'\n"
                    + "group by 1,2,3,4,5,6";

//                  VENDAS DO BANCO WRPDV
            /*"select \n"
                    + " v.tvd_unidade ||'-'||v.tvd_pdv || v.tvd_cupom ||v.tvd_operador id,\n"
                    + " v.tvd_pdv ecf,\n"
                    + " v.tvd_cupom cupom,\n"
                    + " cast (v.tvd_data_emissao as date) as datavenda,\n"
                    + " cast (v.tvd_data_emissao as time) as hora\n"
                    + "from\n"
                    + "	tab_venda_" + tabelaVenda + " v\n"
                    + "	left join tab_venda_" + tabelaVenda + " f on v.tvd_unidade = f.tvd_unidade and v.tvd_cupom = f.tvd_cupom and f.tvd_tipo_reg = 'FINN'\n"
                    + "where\n"
                    + "	v.tvd_unidade = '" + idLojaCliente + "'\n"
                    + "	and v.tvd_tipo_reg = 'INFN'\n"
                    + "	and split_part(f.tvd_registro,'|',3) is not null\n"
                    + "	and split_part(f.tvd_registro,'|',5) != '7000'\n"
                    + "group by 1,2,3,4,5\n"
                    + "order by 3, 4, 5";*/
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

                        next.setId(rst.getString("id"));
                        next.setVenda(rst.getString("idvenda"));
                        next.setProduto(rst.getString("idproduto"));
                        next.setQuantidade(rst.getDouble("quantidade"));
                        next.setSequencia(rst.getInt("sequencial"));
                        next.setPrecoVenda(rst.getDouble("precovenda"));
                        next.setTotalBruto(rst.getDouble("valortotal"));
                        next.setCancelado(rst.getBoolean("cancelado"));
                        next.setValorDesconto(rst.getDouble("desconto"));
                        next.setUnidadeMedida(rst.getString("unidade"));
                        next.setIcmsCst(rst.getInt("csticms"));
                        next.setIcmsAliq(rst.getDouble("aliqicms"));
                        next.setIcmsReduzido(0);
                    }
                }
            } catch (SQLException ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaItemIterator(String idLojaCliente, Date dataInicio, Date dataTermino, String tabelaVenda) throws Exception {
            this.sql
                    = "select distinct\n"
                    + "  vdet_transacao||'.'||vdet_cupom||'.'||vdet_hora||'.'||vdet_sequencial||'.'||vdet_pdv as id,\n"
                    + "  vdet_transacao||'.'||vdet_cupom||'.'||vdet_hora||'.'||vdet_pdv||'0' as idvenda,\n"
                    + "  vdet_cupom as cupom,\n"
                    + "  case when vdet_status = 'D' then 1 else 0 end cancelado,\n"
                    + "  vdet_pdv ecf,\n"
                    + "  vdet_unid_codigo loja,\n"
                    + "  vdet_prod_codigo idproduto,\n"
                    + "  un.prun_emb unidade,\n"
                    + "  vdet_sequencial sequencial,\n"
                    + "  vdet_qtde quantidade,\n"
                    + "  (vdet_valor + vdet_valordesc) valortotal,\n"
                    + "  vdet_valordesc desconto,\n"
                    + "  trunc((coalesce(vdet_valor, 0) / coalesce(vdet_qtde, 1)), 2) precovenda,\n"
                    + "  vdet_icms aliqicms,\n"
                    + "  vdet_cst csticms\n"
                    + "from vdadet" + tabelaVenda + " vi\n"
                    + "left join produn un on vi.vdet_prod_codigo = un.prun_prod_codigo and un.prun_unid_codigo = '" + idLojaCliente + "'\n"
                    + "where vdet_status in ('N', 'D') and vdet_unid_codigo = '" + idLojaCliente + "'";

//                  VENDAS DO BANCO WRPDV
            /*"select distinct\n"
                    + "	v.tvd_unidade ||'-'|| v.tvd_pdv || v.tvd_cupom ||v.tvd_operador idvenda,\n"
                    + "	v.tvd_unidade ||'-'|| v.tvd_cupom || '-' || tvd_cpseq::int-1 || '-' || v.tvd_operador || tvd_data_hora  id,\n"
                    + "	tvd_cupom cupom,\n"
                    + "	tvd_cpseq::int -1 sequencial,\n"
                    + "	split_part(tvd_registro,'|',11)::bigint as idproduto,\n"
                    + "	cast (split_part(tvd_registro,'|',4) as real)/1000 as quantidade,\n"
                    + "	cast (split_part(tvd_registro,'|',3) as real)/100 as precovenda,\n"
                    + "	cast(cast (split_part(tvd_registro,'|',4) as bigint) * cast (split_part(tvd_registro,'|',3) as bigint) as real) /100000 as valortotal\n"
                    + "from\n"
                    + "	tab_venda_" + tabelaVenda + " v \n"
                    + "where\n"
                    + "	tvd_unidade = '" + idLojaCliente + "'\n"
                    + "	and tvd_tipo_reg = 'VITN'\n"
                    + " and v.tvd_unidade ||'-'|| v.tvd_cupom || '-' || tvd_cpseq::int-1 || '-' || v.tvd_operador || tvd_data_hora is not null \n"
                    + "	order by 1,2,3,4"*/
            LOG.log(Level.FINE, "SQL da venda: " + sql);
            System.out.println(sql);
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
