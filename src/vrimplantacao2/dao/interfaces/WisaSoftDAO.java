package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoFornecedor;
import vrimplantacao2.vo.importacao.FornecedorContatoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

public class WisaSoftDAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "WisaSoft";
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> vResult = new ArrayList<>();
        boolean eBalanca;
        int cst;
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT SUP001.SUP001, SUP001.DESCRICAO, SUP001.DESCRICAO_ETIQUETA,\n"
                    + "    SUP001.DESCRICAO_REDUZIDA, SUP001.EAN, SUP001.DATA_CADASTRO,\n"
                    + "    SUP002.SIGLA, SUP008.SALDO, SUP008.CUSTO, SUP008.VENDA,\n"
                    + "    SUP008.MARGEMSUG as MARGEM, SUP090.DIGITOS, SUP009.DESCRICAO AS UNIDADE,\n"
                    + "    SUP009.MULTIPLICADOR AS QTDEMBALAGEM, SUP001.PIS, SUP002.descricao DESC_ICMS,\n"
                    + "    SUP002.percentual, SUP001.reducao_base, sup090.aliq_estadual, sup090.aliq_municipal,\n"
                    + "    SUP090.cod_cest, SUP008.ativo,\n"
                    + "        CASE SUP001.SUP108\n"
                    + "            WHEN 1 THEN 4\n"
                    + "            WHEN 6 THEN 7\n"
                    + "            WHEN 7 THEN 5\n"
                    + "            WHEN 8 THEN 1\n"
                    + "        END AS CSTPISCOFINSDEBITO,\n"
                    + "        CASE SUP001.SUP108\n"
                    + "            WHEN 1 THEN 70\n"
                    + "            WHEN 6 THEN 71\n"
                    + "            WHEN 7 THEN 75\n"
                    + "            WHEN 8 THEN 50\n"
                    + "        END AS CSTPISCOFINSCREDITO,\n"
                    + "        SUP090.codigonatrec AS NATCODIGO\n"
                    + "FROM SUP001\n"
                    + "    INNER JOIN SUP002 ON SUP002.SUP002 = SUP001.SUP002\n"
                    + "    INNER JOIN SUP008 ON SUP008.SUP001 = SUP001.SUP001\n"
                    + "    INNER JOIN SUP009 ON SUP009.SUP009 = SUP001.sup009_venda\n"
                    + "    LEFT JOIN SUP090 ON SUP090.SUP090 = SUP001.SUP090\n"
                    + "    LEFT JOIN SUP040 ON SUP040.SUP001 = SUP001.SUP001\n"
                    + "    left join sup013 on sup013.sup001 = sup001.sup001\n"
                    + "union all\n"
                    + "SELECT SUP001.SUP001, SUP001.DESCRICAO, SUP001.DESCRICAO_ETIQUETA,\n"
                    + "    SUP001.DESCRICAO_REDUZIDA, SUP013.EAN, SUP001.DATA_CADASTRO,\n"
                    + "    SUP002.SIGLA, SUP008.SALDO, SUP008.CUSTO, SUP008.VENDA,\n"
                    + "    SUP008.MARGEMSUG as MARGEM, SUP090.DIGITOS, SUP009.DESCRICAO AS UNIDADE,\n"
                    + "    SUP009.MULTIPLICADOR AS QTDEMBALAGEM, SUP001.PIS, SUP002.descricao DESC_ICMS,\n"
                    + "    SUP002.percentual, SUP001.reducao_base, sup090.aliq_estadual, sup090.aliq_municipal,\n"
                    + "    SUP090.cod_cest, SUP008.ativo,\n"
                    + "    CASE SUP001.SUP108\n"
                    + "        WHEN 1 THEN 4\n"
                    + "        WHEN 6 THEN 7\n"
                    + "        WHEN 7 THEN 5\n"
                    + "        WHEN 8 THEN 1\n"
                    + "    END AS CSTPISCOFINSDEBITO,\n"
                    + "    CASE SUP001.SUP108\n"
                    + "        WHEN 1 THEN 70\n"
                    + "        WHEN 6 THEN 71\n"
                    + "        WHEN 7 THEN 75\n"
                    + "        WHEN 8 THEN 50\n"
                    + "    END AS CSTPISCOFINSCREDITO,\n"
                    + "    SUP090.codigonatrec AS NATCODIGO\n"
                    + "FROM SUP001\n"
                    + "    INNER JOIN SUP002 ON SUP002.SUP002 = SUP001.SUP002\n"
                    + "    INNER JOIN SUP008 ON SUP008.SUP001 = SUP001.SUP001\n"
                    + "    INNER JOIN SUP009 ON SUP009.SUP009 = SUP001.sup009_venda\n"
                    + "    LEFT JOIN SUP090 ON SUP090.SUP090 = SUP001.SUP090\n"
                    + "    LEFT JOIN SUP040 ON SUP040.SUP001 = SUP001.SUP001\n"
                    + "    left join sup013 on sup013.sup001 = sup001.sup001\n"
                    + "ORDER BY 1"
            )) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
                while (rst.next()) {

                    long codigoProduto;

                    if ((rst.getString("ean") != null)
                            && (!rst.getString("ean").trim().isEmpty())) {
                        codigoProduto = Long.parseLong(Utils.formataNumero(rst.getString("ean").trim()));
                    } else {
                        codigoProduto = -1;
                    }

                    ProdutoBalancaVO produtoBalanca;
                    if (codigoProduto <= Integer.MAX_VALUE) {
                        produtoBalanca = produtosBalanca.get((int) codigoProduto);
                    } else {
                        produtoBalanca = null;
                    }
                    if (produtoBalanca != null) {
                        eBalanca = true;
                    } else {
                        eBalanca = false;
                    }

                    if ((rst.getString("DESC_ICMS") != null)
                            && (!rst.getString("DESC_ICMS").trim().isEmpty())) {
                        if ("SUBS. TRIBUTARIA".contains(rst.getString("DESC_ICMS"))) {
                            cst = 60;
                        } else if ("ISENTO".contains(rst.getString("DESC_ICMS"))) {
                            cst = 40;
                        } else if ("NAO TRIBUT".contains(rst.getString("DESC_ICMS"))) {
                            cst = 41;
                        } else if ("DIFE".contains(rst.getString("DESC_ICMS"))) {
                            cst = 51;
                        } else if ("OUTR".contains(rst.getString("DESC_ICMS"))) {
                            cst = 90;
                        } else {
                            cst = 0;
                        }
                    } else {
                        cst = 90;
                    }

                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("SUP001"));
                    imp.setEan(rst.getString("EAN"));
                    imp.setSituacaoCadastro("S".equals(rst.getString("ativo")) ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    imp.seteBalanca(eBalanca);
                    imp.setTipoEmbalagem(rst.getString("UNIDADE"));
                    imp.setQtdEmbalagem(rst.getInt("QTDEMBALAGEM"));
                    imp.setDataCadastro(rst.getDate("DATA_CADASTRO"));
                    imp.setDescricaoCompleta(rst.getString("DESCRICAO"));
                    imp.setDescricaoReduzida(rst.getString("DESCRICAO_REDUZIDA"));
                    imp.setDescricaoGondola(rst.getString("DESCRICAO_ETIQUETA"));
                    imp.setPiscofinsCstDebito(rst.getInt("CSTPISCOFINSDEBITO"));
                    imp.setPiscofinsCstCredito(rst.getInt("CSTPISCOFINSCREDITO"));
                    imp.setPiscofinsNaturezaReceita(rst.getInt("NATCODIGO"));
                    imp.setNcm(rst.getString("DIGITOS"));
                    imp.setCest(rst.getString("COD_CEST"));
                    imp.setMargem(rst.getDouble("MARGEM"));
                    imp.setCustoComImposto(rst.getDouble("CUSTO"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setPrecovenda(rst.getDouble("VENDA"));
                    imp.setIcmsCst(cst);
                    imp.setIcmsAliq(rst.getDouble("percentual"));
                    imp.setIcmsReducao(0);
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select sup010.sup010, sup010.sup118, sup010.razaosocial, sup010.fantasia,\n"
                    + "        sup010.endereco, sup010.numero, sup010.bairro, sup010.complemento,\n"
                    + "        sup010.cep, sup010.cgc, sup010.inscricao, sup010.inscricaosubst,\n"
                    + "        sup010.telefone, sup010.fax, sup010.telefone_gratis, sup010.email,\n"
                    + "        sup010.site, sup010.obs,sup010.pessoa, sup010.codsimples,\n"
                    + "        sup010.recolhe_guia_st, sup010.distribuidor, sup010.dtcadastro,\n"
                    + "        sup010.hrcadastro, sup010.inscmunicipal, sup010.ativo, sup010.dtalteracao,\n"
                    + "        sup010.usuario, sup010.sup999, sup010.contribuinteicms, sup010.ivaajustado,\n"
                    + "        sup010.modelonf, sup010.simplesnacional, sup118.nome as cidade, sup118.uf\n"
                    + "  from sup010\n"
                    + " inner join sup118  on sup118.sup118 = sup010.sup118\n"
                    + " order by sup010.sup010"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("sup010"));
                    imp.setRazao(rst.getString("razaosocial"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setCep(Utils.formataNumero(rst.getString("cep")));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCnpj_cpf(Utils.formataNumero(rst.getString("cgc")));
                    imp.setIe_rg(rst.getString("inscricao"));
                    imp.setTel_principal(Utils.formataNumero(rst.getString("telefone")));

                    if (Utils.stringToLong(rst.getString("fax")) > 0) {
                        FornecedorContatoIMP cont = new FornecedorContatoIMP();
                        cont.setImportSistema(getSistema());
                        cont.setImportLoja(getLojaOrigem());
                        cont.setImportId("1");
                        cont.setNome("FAX");
                        cont.setTelefone(Utils.stringLong(rst.getString("fax")));
                    }

                    if (Utils.stringToLong(rst.getString("telefone_gratis")) > 0) {
                        FornecedorContatoIMP cont = new FornecedorContatoIMP();
                        cont.setImportSistema(getSistema());
                        cont.setImportLoja(getLojaOrigem());
                        cont.setImportId("2");
                        cont.setNome("FAX");
                        cont.setTelefone(Utils.stringLong(rst.getString("telefone_gratis")));
                    }
                    if ((rst.getString("email") != null)
                            && (!rst.getString("email").trim().isEmpty())) {
                        FornecedorContatoIMP cont = imp.getContatos().make("2");
                        cont.setImportSistema(getSistema());
                        cont.setImportLoja(getLojaOrigem());
                        cont.setImportId("2");
                        cont.setNome("EMAIL");
                        cont.setEmail(rst.getString("fax"));
                    }
                    if ((rst.getString("site") != null)
                            && (!rst.getString("site").trim().isEmpty())) {
                        FornecedorContatoIMP cont = imp.getContatos().make("3");
                        cont.setImportSistema(getSistema());
                        cont.setImportLoja(getLojaOrigem());
                        cont.setImportId("3");
                        cont.setNome("SITE");
                        cont.setEmail(rst.getString("site"));
                    }

                    imp.setObservacao(rst.getString("obs") == null ? "" : rst.getString("obs"));
                    imp.setTipoFornecedor(("S".equals(rst.getString("distribuidor")) ? TipoFornecedor.DISTRIBUIDOR : TipoFornecedor.INDUSTRIA));
                    imp.setDatacadastro(rst.getDate("dtcadastro"));
                    imp.setAtivo(("S".equals(rst.getString("ativo"))));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select SUP001 AS PRODUTO,\n"
                    + "        sup010 AS FORNECEDOR,\n"
                    + "        REFERENCIA\n"
                    + "  from sup016 PF"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setIdFornecedor(rst.getString("FORNECEDOR"));
                    imp.setIdProduto(rst.getString("PRODUTO"));
                    imp.setCodigoExterno(rst.getString("REFERENCIA"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }
}
