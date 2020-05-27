package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vrimplantacao.classe.ConexaoPostgres;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.vo.importacao.ReceitaIMP;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto.ProdutoAnteriorDAO;
import vrimplantacao2.vo.cadastro.mercadologico.MercadologicoNivelIMP;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoEmpresa;
import vrimplantacao2.vo.enums.TipoFornecedor;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Leandro
 */
public class RPInfoDAO extends InterfaceDAO {

    public boolean importarFuncionario = false;
    public boolean gerarCodigoAtacado = true;
    public int idLojaVR = 1;
    public boolean removeDigitoEAN = false;

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
        return "RPInfo";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(new OpcaoProduto[]{
            OpcaoProduto.MERCADOLOGICO_POR_NIVEL,
            OpcaoProduto.MERCADOLOGICO_PRODUTO,
            OpcaoProduto.MERCADOLOGICO,
            OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
            OpcaoProduto.FAMILIA_PRODUTO,
            OpcaoProduto.FAMILIA,
            OpcaoProduto.IMPORTAR_MANTER_BALANCA,
            OpcaoProduto.PRODUTOS,
            OpcaoProduto.EAN,
            OpcaoProduto.EAN_EM_BRANCO,
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
            OpcaoProduto.NCM,
            OpcaoProduto.PESO_BRUTO,
            OpcaoProduto.PESO_LIQUIDO,
            OpcaoProduto.DESCONTINUADO,
            OpcaoProduto.PESAVEL,
            OpcaoProduto.ICMS,
            OpcaoProduto.PIS_COFINS,
            OpcaoProduto.ATACADO,
            OpcaoProduto.RECEITA,
            OpcaoProduto.SECAO,
            OpcaoProduto.PRATELEIRA
        }));
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
        List<ProdutoIMP> result = new ArrayList<>();

        if (!gerarCodigoAtacado) {
            try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
                try (ResultSet rs = stm.executeQuery(
                        "select\n"
                        + "	cbal_prod_codigo id,\n"
                        + "	cbal_prod_codbarras ean,\n"
                        + "	null unidade,\n"
                        + "	coalesce(nullif(cbalt.cbal_fatoremb,0),1) qtdembalagem\n"
                        + "from\n"
                        + "	cbalt")) {
                    while (rs.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rs.getString("id"));
                        imp.setEan(rs.getString("ean"));
                        imp.setTipoEmbalagem("UN");
                        imp.setQtdEmbalagem(rs.getInt("qtdembalagem"));

                        result.add(imp);
                    }
                }
            }
        } else {
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
        }
        return result;
    }

    @Override
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
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setDataAlteracao(rst.getDate("dataalteracao"));
                    if (rst.getInt("e_balanca") == 1) {
                        long codigoProduto;
                        codigoProduto = Long.parseLong(rst.getString("ean"));
                        String pBalanca = String.valueOf(codigoProduto);

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
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    imp.setQtdEmbalagemCotacao(rst.getInt("embalagemcotacao"));
                    //imp.seteBalanca(rst.getBoolean("e_balanca"));
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
                    imp.setIcmsAliq(rst.getDouble("icms"));
                    imp.setIcmsReducao(rst.getDouble("icmsreducao"));
                    imp.setIcmsCst(rst.getInt("cst"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("naturezareceita"));
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
    }

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

        if (opt == OpcaoProduto.MERCADOLOGICO) {
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
        }

        return null;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
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
                    + "	f.forn_caractrib tipofornecedor,\n"
                    + "	fc.rfor_pzentrega prazo_entrega,\n"
                    + "	fc.rfor_pzrecebimento prazo_recebimento,\n"
                    + "	fp.fpgt_prazos forma_pagamento\n"
                    + "from\n"
                    + "	fornecedores f\n"
                    + "	left join municipios m on\n"
                    + "		f.forn_muni_codigo = m.muni_codigo\n"
                    + "	left join regforn fc on f.forn_codigo = fc.rfor_forn_codigo\n"
                    + "	left join fpgto fp on fc.rfor_fpgt_codigo = fp.fpgt_codigo\n"
                    + "order by\n"
                    + "	id"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setFantasia(rst.getString("razaosocial"));
                    imp.setRazao(rst.getString("nomefantasia"));
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
                            imp.setTipoFornecedor(TipoFornecedor.INDUSTRIA);
                            imp.setTipoEmpresa(TipoEmpresa.LUCRO_REAL);
                            break;
                        case "D":
                            imp.setTipoFornecedor(TipoFornecedor.DISTRIBUIDOR);
                            imp.setTipoEmpresa(TipoEmpresa.LUCRO_REAL);
                            break;
                        case "P":
                            imp.setProdutorRural();
                            break;
                        case "S":
                            imp.setTipoFornecedor(TipoFornecedor.DISTRIBUIDOR);
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
                            imp.setTipoFornecedor(TipoFornecedor.ATACADO);
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
                    + "	c.clie_senhapdv senhapdv\n"
                    + "from\n"
                    + "	clientes c\n"
                    + "	left join municipios mr on\n"
                    + "		c.clie_muni_codigo_res = mr.muni_codigo\n"
                    + "	left join municipios mc on\n"
                    + "		c.clie_muni_codigo_com = mc.muni_codigo\n"
                    + "order by\n"
                    + "	id"
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
                        + "left join clientes c on (pendfin.pfin_codentidade = c.clie_codigo)\n"
                        + "where\n"
                        + "	pfin_unid_codigo = '" + getLojaOrigem() + "' and\n"
                        + "	pfin_pr = 'R' and\n"
                        + "	pfin_status = 'P' and\n"
                        + "	pfin_pger_conta in (112201, 112202, 112203, 112204, 112205, 112206, 112207, 112208, 112209) "
                        + "union all "
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
                        + "left join funcionarios f on (pendfin.pfin_codentidade = f.func_codigo)\n"
                        + "where\n"
                        + "	pfin_unid_codigo = '" + getLojaOrigem() + "' and\n"
                        + "	pfin_pr = 'R' and\n"
                        + "	pfin_status = 'P'")) {
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
                        + "left join clientes c on (pendfin.pfin_codentidade = c.clie_codigo)\n"
                        + "where\n"
                        + "	pfin_unid_codigo = '" + getLojaOrigem() + "' and\n"
                        + "	pfin_pr = 'R' and\n"
                        + "	pfin_status = 'P' and\n"
                        + "	pfin_pger_conta in (112101, 112201, 112202, 112203, 112204, 112205, 112206, 112207, 112208, 112209)")) {
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

                        result.add(imp);
                    }
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
}
