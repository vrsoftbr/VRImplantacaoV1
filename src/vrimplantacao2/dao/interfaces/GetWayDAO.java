/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.interfaces;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrframework.remote.ItemComboVO;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao.dao.cadastro.BancoDAO;
import vrimplantacao.dao.cadastro.FornecedorDAO;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao2.dao.cadastro.devolucao.receber.ReceberDevolucaoDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.venda.OpcaoVenda;
import vrimplantacao2.dao.cadastro.verba.receber.ReceberVerbaDAO;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoEstadoCivil;
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
import vrimplantacao2.vo.cadastro.financeiro.ReceberDevolucaoVO;
import vrimplantacao2.vo.cadastro.financeiro.ReceberVerbaVO;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;

/**
 *
 * @author lucasrafael
 */
public class GetWayDAO extends InterfaceDAO {
    
    private static final Logger LOG = Logger.getLogger(GetWayDAO.class.getName());

    public int v_tipoDocumento;
    public int v_tipoDocumentoCheque;

    @Override
    public String getSistema() {
        return "GetWay";
    }

    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	CODLOJA id,\n" +
                    "	descricao\n" +
                    "from\n" +
                    "	LOJA\n" +
                    "order by\n" +
                    "	id"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("id"), rst.getString("descricao")));
                }
            }
        }
        
        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "CODFAMILIA, "
                    + "descricao "
                    + "from "
                    + "FAMILIA"
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("CODFAMILIA"));
                    imp.setDescricao(rst.getString("descricao"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select CRECEITA.CODCRECEITA AS MERC1, "
                    + "coalesce(CRECEITA.DESCRICAO,'DIVERSOS') AS  DESCRICAO_M1, "
                    + "coalesce(GRUPO.CODGRUPO, 1) AS MERC2, "
                    + "coalesce(GRUPO.DESCRICAO, CRECEITA.DESCRICAO) AS  DESCRICAO_M2, "
                    + "coalesce(CATEGORIA.CODCATEGORIA, 1) AS MERC3,  "
                    + "coalesce(CATEGORIA.DESCRICAO,GRUPO.DESCRICAO) as DESCRICAO_M3 "
                    + "from "
                    + "CRECEITA "
                    + "left join GRUPO on GRUPO.CODCRECEITA = CRECEITA.CODCRECEITA "
                    + "left join CATEGORIA on CATEGORIA.CODGRUPO = GRUPO.CODGRUPO "
                    + "order by CRECEITA.CODCRECEITA, GRUPO.CODGRUPO, CATEGORIA.CODCATEGORIA "
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("MERC1"));
                    imp.setMerc2ID(rst.getString("MERC2"));
                    imp.setMerc3ID(rst.getString("MERC3"));
                    imp.setMerc1Descricao(rst.getString("DESCRICAO_M1"));
                    imp.setMerc2Descricao(rst.getString("DESCRICAO_M2"));
                    imp.setMerc3Descricao(rst.getString("DESCRICAO_M3"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> vResult = new ArrayList<>();
        int cstSaida, cstEntrada;
        double valIcmsCredito;
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "fam.codfamilia, "
                    + "prod.VALIDADE, "
                    + "prod.codtrib, "
                    + "prod.codprod, "
                    + "prod.descricao, "
                    + "prod.desc_pdv, "
                    + "coalesce(merc.codcreceita, 1)as MERC1, "
                    + "coalesce(merc.codgrupo, 1) as MERC2, "
                    + "coalesce(merc.codcategoria, 1) as MERC3, "
                    + "prod.codaliq, "
                    + "prod.barra ean, "
                    + "prod.unidade, "
                    + "prod.estoque, "
                    + "prod.preco_cust, "
                    + "prod.preco_unit, "
                    + "prod.margem_bruta, "
                    + "prod.margem_param, "
                    + "prod.codaliq_nf, "
                    + "prod.obs, "
                    + "prod.dtaltera, "
                    + "prod.dtinclui, "
                    + "prod.qtd_emb, "
                    + "prod.preco_especial, "
                    + "prod.cst_pisentrada, "
                    + "prod.cst_pissaida, "
                    + "prod.cst_cofinsentrada, "
                    + "prod.cst_cofinssaida, "
                    + "prod.nat_rec, "
                    + "prod.generoitem_sef2, "
                    + "prod.aliquota_ibpt, "
                    + "prod.aliquota_ibptest, "
                    + "prod.aliquota_ibptmun, "
                    + "prod.codncm, "
                    + "prod.ATIVO, "
                    + "prod.CODCEST, "
                    + "prod.QTD_EMBVENDA, "
                    + "Prod.CODALIQ ALIQ_DEBITO, "
                    + "A.VALORTRIB VAL_DEBITO, "
                    + "Prod.CODTRIB CST_SAIDA, "
                    + "Prod.CODALIQ_NF ALIQ_DEBITO_FORA_ESTADO, "
                    + "A2.VALORTRIB VAL_DEBITO_FORA_ESTADO, "
                    + "A.REDUCAO, "
                    + "Prod.CODTRIB_ENT CST_ENTRADA, "
                    + "Prod.PER_REDUC_ENT REDUCAO_CREDITO, "
                    + "PROD.ULTICMSCRED, "
                    + "PROD.CODCEST, "
                    + "prod.DTINCLUI,"
                    + "prod.DESATIVACOMPRA "
                    + "from produtos prod "
                    + "inner join ALIQUOTA_ICMS A on A.CODALIQ = PROD.CODALIQ "
                    + "INNER JOIN ALIQUOTA_ICMS A2 ON A2.CODALIQ = PROD.CODALIQ_NF "
                    + "left outer join CATEGORIA merc ON merc.CODCRECEITA = prod.codcreceita "
                    + " and merc.CODGRUPO = prod.codgrupo and merc.CODCATEGORIA = prod.codcategoria "
                    + "left outer join PROD_FAMILIA fam ON fam.CODPROD = prod.CODPROD and prod.codprod > 0 "
                    + "union all"
                    + " select "
                    + "fam.codfamilia, "
                    + "prod.VALIDADE, "
                    + "prod.codtrib, "
                    + "prod.codprod, "
                    + "prod.descricao, "
                    + "prod.desc_pdv, "
                    + "coalesce(merc.codcreceita, 1)as MERC1, "
                    + "coalesce(merc.codgrupo, 1) as MERC2, "
                    + "coalesce(merc.codcategoria, 1) as MERC3, "
                    + "prod.codaliq, "
                    + "BAR.BARRA ean, "
                    + "prod.unidade, "
                    + "prod.estoque, "
                    + "prod.preco_cust, "
                    + "prod.preco_unit, "
                    + "prod.margem_bruta, "
                    + "prod.margem_param, "
                    + "prod.codaliq_nf, "
                    + "prod.obs, "
                    + "prod.dtaltera, "
                    + "prod.dtinclui, "
                    + "prod.qtd_emb, "
                    + "prod.preco_especial, "
                    + "prod.cst_pisentrada, "
                    + "prod.cst_pissaida, "
                    + "prod.cst_cofinsentrada, "
                    + "prod.cst_cofinssaida, "
                    + "prod.nat_rec, "
                    + "prod.generoitem_sef2, "
                    + "prod.aliquota_ibpt, "
                    + "prod.aliquota_ibptest, "
                    + "prod.aliquota_ibptmun, "
                    + "prod.codncm, "
                    + "prod.ATIVO, "
                    + "prod.CODCEST, "
                    + "prod.QTD_EMBVENDA, "
                    + "Prod.CODALIQ ALIQ_DEBITO, "
                    + "A.VALORTRIB VAL_DEBITO, "
                    + "Prod.CODTRIB CST_SAIDA, "
                    + "Prod.CODALIQ_NF ALIQ_DEBITO_FORA_ESTADO, "
                    + "A2.VALORTRIB VAL_DEBITO_FORA_ESTADO, "
                    + "A.REDUCAO, "
                    + "Prod.CODTRIB_ENT CST_ENTRADA, "
                    + "Prod.PER_REDUC_ENT REDUCAO_CREDITO, "
                    + "PROD.ULTICMSCRED, "
                    + "PROD.CODCEST, "
                    + "prod.DTINCLUI, "
                    + "prod.DESATIVACOMPRA "
                    + "from produtos prod "
                    + "inner join ALIQUOTA_ICMS A on A.CODALIQ = PROD.CODALIQ "
                    + "INNER JOIN ALIQUOTA_ICMS A2 ON A2.CODALIQ = PROD.CODALIQ_NF "
                    + "left outer join CATEGORIA merc ON merc.CODCRECEITA = prod.codcreceita "
                    + "and merc.CODGRUPO = prod.codgrupo and merc.CODCATEGORIA = prod.codcategoria "
                    + "left outer join PROD_FAMILIA fam ON fam.CODPROD = prod.CODPROD and prod.codprod > 0 "
                    + "inner join ALTERNATIVO BAR on BAR.CODPROD = PROD.CODPROD and LEN(BAR.BARRA) > 6 "
                    + "order by prod.codprod "
            )) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
                while (rst.next()) {
                    cstSaida = Integer.parseInt(Utils.formataNumero(rst.getString("CST_SAIDA")));
                    cstEntrada = Integer.parseInt(Utils.formataNumero(rst.getString("CST_ENTRADA")));

                    if (cstSaida > 9) {
                        cstSaida = Integer.parseInt(String.valueOf(cstSaida).substring(0, 2));
                    }
                    if (cstEntrada > 9) {
                        cstEntrada = Integer.parseInt(String.valueOf(cstEntrada).substring(0, 2));
                    }

                    valIcmsCredito = 0;

                    if ((Integer.parseInt(Utils.formataNumero(rst.getString("CST_ENTRADA"))) == 0)
                            && (rst.getDouble("ULTICMSCRED") == 0.0)) {
                        valIcmsCredito = rst.getDouble("VAL_DEBITO_FORA_ESTADO");
                    } else {
                        valIcmsCredito = rst.getDouble("ULTICMSCRED");
                    }

                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("codprod"));
                    imp.setEan(rst.getString("ean"));
                    imp.setDescricaoCompleta(rst.getString("descricao"));
                    imp.setDescricaoReduzida(rst.getString("desc_pdv"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setIdFamiliaProduto(rst.getString("codfamilia"));
                    imp.setCodMercadologico1(rst.getString("MERC1"));
                    imp.setCodMercadologico2(rst.getString("MERC2"));
                    imp.setCodMercadologico3(rst.getString("MERC3"));
                    imp.setSituacaoCadastro(("S".equals(rst.getString("ATIVO")) ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO));
                    imp.setTipoEmbalagem(rst.getString("unidade"));
                    imp.setQtdEmbalagem(rst.getInt("QTD_EMBVENDA") == 0 ? 1 : rst.getInt("QTD_EMBVENDA"));
                    imp.setDataCadastro(rst.getDate("dtinclui"));
                    imp.setMargem(rst.getDouble("margem_param"));
                    imp.setPrecovenda(rst.getDouble("preco_unit"));
                    imp.setCustoComImposto(rst.getDouble("preco_cust"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setNcm(rst.getString("codncm"));
                    imp.setCest(rst.getString("CODCEST"));
                    imp.setPiscofinsCstDebito(rst.getInt("cst_pissaida"));
                    imp.setPiscofinsCstCredito(rst.getInt("cst_pisentrada"));
                    imp.setPiscofinsNaturezaReceita(Integer.parseInt(Utils.formataNumero(rst.getString("NAT_REC"))));

                    imp.setIcmsCstSaida(cstSaida);
                    imp.setIcmsAliqSaida(rst.getDouble("VAL_DEBITO"));
                    imp.setIcmsReducaoSaida(rst.getDouble("REDUCAO"));

                    if (rst.getDouble("REDUCAO_CREDITO") == 33.33) {
                        valIcmsCredito = 18;
                    } else if (rst.getDouble("REDUCAO_CREDITO") == 52) {
                        valIcmsCredito = 25;
                    } else if (rst.getDouble("REDUCAO_CREDITO") == 41.67) {
                        valIcmsCredito = 12;
                    } else if (rst.getDouble("REDUCAO_CREDITO") == 61.11) {
                        valIcmsCredito = 18;
                    }

                    imp.setIcmsCstEntrada(cstEntrada);
                    imp.setIcmsAliqEntrada(valIcmsCredito);
                    imp.setIcmsReducaoEntrada(rst.getDouble("REDUCAO_CREDITO"));

                    if ((rst.getString("ean") != null)
                            && (!rst.getString("ean").trim().isEmpty())
                            && (rst.getString("ean").trim().length() >= 4)
                            && (rst.getString("ean").trim().length() <= 6)) {
                        ProdutoBalancaVO produtoBalanca;
                        long codigoProduto;
                        codigoProduto = Long.parseLong(imp.getEan());
                        if (codigoProduto <= Integer.MAX_VALUE) {
                            produtoBalanca = produtosBalanca.get((int) codigoProduto);
                        } else {
                            produtoBalanca = null;
                        }
                        if (produtoBalanca != null) {
                            imp.seteBalanca(true);
                            imp.setValidade(produtoBalanca.getValidade() > 1 ? produtoBalanca.getValidade() : rst.getInt("VALIDADE"));
                        } else {
                            imp.setValidade(0);
                            imp.seteBalanca(false);
                        }
                    }
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT "
                    + "CODFORNEC, RAZAO, FANTASIA, ENDERECO, NUMERO, BAIRRO, "
                    + "CIDADE, ESTADO, CEP, TELEFONE, FAX, EMAIL, CELULAR, FONE1, "
                    + "CONTATO, IE, CNPJ_CPF, AGENCIA, BANCO, CONTA,  DTCAD, "
                    + "VALOR_COMPRA, ATIVO, OBS "
                    + "FROM "
                    + "FORNECEDORES "
                    + "order by codfornec "
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("CODFORNEC"));
                    imp.setRazao(rst.getString("RAZAO"));
                    imp.setFantasia(rst.getString("FANTASIA"));
                    imp.setEndereco(rst.getString("ENDERECO"));
                    imp.setNumero(rst.getString("NUMERO"));
                    imp.setBairro(rst.getString("BAIRRO"));
                    imp.setMunicipio(rst.getString("CIDADE"));
                    imp.setUf(rst.getString("ESTADO"));
                    imp.setCep(rst.getString("CEP"));
                    imp.setCnpj_cpf(rst.getString("CNPJ_CPF"));
                    imp.setIe_rg(rst.getString("IE"));
                    imp.setTel_principal(rst.getString("TELEFONE"));
                    imp.setAtivo("S".equals(rst.getString("ATIVO")));
                    imp.setObservacao(rst.getString("OBS"));
                    imp.setDatacadastro(rst.getDate("DTCAD"));
                    if ((rst.getString("FAX") != null)
                            && (!rst.getString("FAX").trim().isEmpty())) {
                        imp.addContato(
                                "1",
                                "FAX",
                                rst.getString("FAX"),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("EMAIL") != null)
                            && (!rst.getString("EMAIL").trim().isEmpty())) {
                        imp.addContato(
                                "2",
                                "EMAIL",
                                null,
                                null,
                                TipoContato.COMERCIAL,
                                rst.getString("EMAIL").toLowerCase()
                        );
                    }
                    if ((rst.getString("CELULAR") != null)
                            && (!rst.getString("CELULAR").trim().isEmpty())) {
                        imp.addContato(
                                "3",
                                "CELULAR",
                                null,
                                rst.getString("CELULAR"),
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("FONE1") != null)
                            && (!rst.getString("FONE1").trim().isEmpty())) {
                        imp.addContato(
                                "4",
                                "TELEFONE",
                                rst.getString("FONE1"),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("CONTATO") != null)
                            && (!rst.getString("CONTATO").trim().isEmpty())) {
                        imp.addContato(
                                "5",
                                "CONTATO",
                                null,
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }

                    try (Statement stm2 = ConexaoSqlServer.getConexao().createStatement()) {
                        try (ResultSet rst2 = stm2.executeQuery(
                                "select f.CODFORNEC, cp.CODCONDPAGTO, cp.DESCRICAO, cp.NPARCELAS\n"
                                + "from FORNECEDORES f\n"
                                + "inner join CONDPAGTO cp on cp.CODCONDPAGTO = f.CODCONDPAGTO \n"
                                + "where f.CODFORNEC = " + imp.getImportId()
                                + "order by f.CODFORNEC, cp.CODCONDPAGTO"
                        )) {
                            int contador = 1;
                            if (rst2.next()) {
                                int numParcelas = rst2.getInt("NPARCELAS");
                                String descricao = Utils.formataNumeroParcela(rst2.getString("DESCRICAO").replace("//", "/").trim());
                                String[] cods = descricao.split("\\/");
                                if (numParcelas > 0) {

                                } else {
                                    if (!descricao.contains("/")) {
                                        if ("0".equals(Utils.formataNumero(descricao))) {
                                            imp.addPagamento(String.valueOf(contador), 1);
                                        } else {
                                            imp.addPagamento(String.valueOf(contador), Integer.parseInt(Utils.formataNumero(descricao).trim()));
                                        }

                                        System.out.println("Sem barra " + descricao + " Forn " + imp.getImportId() + "Tam " + cods.length);
                                    } else {
                                        System.out.println("Sem barra " + descricao + " Forn " + imp.getImportId() + "Tam " + cods.length);
                                        for (int i = 0; i < cods.length; i++) {
                                            if (!"".equals(cods[i])) {
                                                switch (i) {
                                                    case 0:
                                                        imp.addPagamento(String.valueOf(i), Integer.parseInt(cods[i]));
                                                        break;
                                                    case 1:
                                                        imp.addPagamento(String.valueOf(i), Integer.parseInt(cods[i]));
                                                        break;
                                                    case 2:
                                                        imp.addPagamento(String.valueOf(i), Integer.parseInt(cods[i]));
                                                        break;
                                                    case 3:
                                                        imp.addPagamento(String.valueOf(i), Integer.parseInt(cods[i]));
                                                        break;
                                                    case 4:
                                                        imp.addPagamento(String.valueOf(i), Integer.parseInt(cods[i]));
                                                        break;
                                                    case 5:
                                                        imp.addPagamento(String.valueOf(i), Integer.parseInt(cods[i]));
                                                        break;
                                                    case 6:
                                                        imp.addPagamento(String.valueOf(i), Integer.parseInt(cods[i]));
                                                        break;
                                                    case 7:
                                                        imp.addPagamento(String.valueOf(i), Integer.parseInt(cods[i]));
                                                        break;
                                                    case 8:
                                                        imp.addPagamento(String.valueOf(i), Integer.parseInt(cods[i]));
                                                        break;
                                                    case 9:
                                                        imp.addPagamento(String.valueOf(i), Integer.parseInt(cods[i]));
                                                        break;
                                                    case 10:
                                                        imp.addPagamento(String.valueOf(i), Integer.parseInt(cods[i]));
                                                        break;
                                                    case 11:
                                                        imp.addPagamento(String.valueOf(i), Integer.parseInt(cods[i]));
                                                        break;
                                                    case 12:
                                                        imp.addPagamento(String.valueOf(i), Integer.parseInt(cods[i]));
                                                        break;
                                                    case 13:
                                                        imp.addPagamento(String.valueOf(i), Integer.parseInt(cods[i]));
                                                        break;
                                                    case 14:
                                                        imp.addPagamento(String.valueOf(i), Integer.parseInt(cods[i]));
                                                        break;
                                                    case 15:
                                                        imp.addPagamento(String.valueOf(i), Integer.parseInt(cods[i]));
                                                        break;
                                                    case 16:
                                                        imp.addPagamento(String.valueOf(i), Integer.parseInt(cods[i]));
                                                        break;
                                                    case 17:
                                                        imp.addPagamento(String.valueOf(i), Integer.parseInt(cods[i]));
                                                        break;
                                                    case 18:
                                                        imp.addPagamento(String.valueOf(i), Integer.parseInt(cods[i]));
                                                        break;
                                                    case 19:
                                                        imp.addPagamento(String.valueOf(i), Integer.parseInt(cods[i]));
                                                        break;
                                                    case 20:
                                                        imp.addPagamento(String.valueOf(i), Integer.parseInt(cods[i]));
                                                        break;
                                                    case 21:
                                                        imp.addPagamento(String.valueOf(i), Integer.parseInt(cods[i]));
                                                        break;
                                                    case 22:
                                                        imp.addPagamento(String.valueOf(i), Integer.parseInt(cods[i]));
                                                        break;
                                                    case 23:
                                                        imp.addPagamento(String.valueOf(i), Integer.parseInt(cods[i]));
                                                        break;
                                                    case 24:
                                                        imp.addPagamento(String.valueOf(i), Integer.parseInt(cods[i]));
                                                        break;
                                                }
                                            }
                                        }
                                    }
                                }
                                contador++;
                            }
                        }
                    }
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "PF.CODFORNEC, PF.CODPROD, PF.CODREF, "
                    + "PF.QTD_EMB, PF.DATAREF "
                    + "from "
                    + "PRODREF PF "
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdFornecedor(rst.getString("CODFORNEC"));
                    imp.setIdProduto(rst.getString("CODPROD"));
                    imp.setQtdEmbalagem(rst.getDouble("QTD_EMB"));
                    imp.setCodigoExterno(rst.getString("CODREF"));
                    imp.setDataAlteracao(rst.getDate("DATAREF"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT "
                    + "CASE COALESCE(PESSOA,'F') WHEN 'F' THEN 1 ELSE 0 END AS PESSOA, "
                    + "CODCLIE, "
                    + "RAZAO, "
                    + "ENDERECO, "
                    + "BAIRRO, "
                    + "CIDADE, "
                    + "ESTADO, "
                    + "CEP, "
                    + "NUMERO, "
                    + "CNPJ_CPF, "
                    + "TELEFONE, "
                    + "RG,"
                    + "IE, "
                    + "FONE1, "
                    + "FONE2, "
                    + "EMAIL, "
                    + "DTANIVER, "
                    + "coalesce(LIMITECRED,0) LIMITECRED, "
                    + "coalesce(RENDA,0) RENDA, "
                    + "CARGO, "
                    + "EMPRESA, "
                    + "FONE_EMP, "
                    + "CASE ATIVO WHEN 'S' THEN 1 ELSE 0 END AS ATIVO, "
                    + "ESTADOCIVIL, "
                    + "CASE SEXO WHEN 'F' THEN 1 ELSE 2 END AS SEXO, "
                    + "NOMEPAI, "
                    + "NOMEMAE, "
                    + "DTALTERA, "
                    + "CELULAR, "
                    + "NOMECONJUGE, "
                    + "CARGOCONJUGE, "
                    + "CPF_CONJUGE, "
                    + "RG_CONJUGE, "
                    + "coalesce(RENDACONJUGE,0) as RENDACONJUGE, "
                    + "DTCAD, "
                    + "CASE ESTADOCIVIL WHEN 'S' THEN 1 "
                    + "WHEN 'C' THEN 2 "
                    + "WHEN 'V' THEN 3 "
                    + "WHEN 'A' THEN 4 "
                    + "WHEN 'O' THEN 5 ELSE 0 END AS ESTADOCIVILNOVO, "
                    + "COMPLEMENTO+' '+CONTATO+' '+REF1_NOME+' '+REF2_NOME+' '+FONE1 AS OBS, "
                    + "BLOQCARTAO "
                    + "FROM "
                    + "CLIENTES "
                    + "where "
                    + "CODCLIE >= 1 "
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("CODCLIE"));
                    imp.setRazao(rst.getString("RAZAO"));
                    imp.setEndereco(rst.getString("ENDERECO"));
                    imp.setBairro(rst.getString("BAIRRO"));
                    imp.setNumero(rst.getString("NUMERO"));
                    imp.setMunicipio(rst.getString("CIDADE"));
                    imp.setUf(rst.getString("ESTADO"));
                    imp.setCep(rst.getString("CEP"));
                    imp.setDataCadastro(rst.getDate("DTCAD"));
                    imp.setCnpj(rst.getString("CNPJ_CPF"));
                    if ((rst.getString("RG") != null)
                            && (!rst.getString("RG").trim().isEmpty())) {
                        imp.setInscricaoestadual(rst.getString("RG"));
                    } else if ((rst.getString("IE") != null)
                            && (!rst.getString("IE").trim().isEmpty())) {
                        imp.setInscricaoestadual(rst.getString("IE"));
                    } else {
                        imp.setInscricaoestadual("ISENTO");
                    }
                    imp.setTelefone(rst.getString("TELEFONE"));
                    imp.setCelular(rst.getString("CELULAR"));
                    imp.setEmail(rst.getString("EMAIL"));
                    imp.setNomePai(rst.getString("NOMEPAI"));
                    imp.setNomeMae(rst.getString("NOMEMAE"));
                    imp.setNomeConjuge(rst.getString("NOMECONJUGE"));
                    imp.setDataNascimento(rst.getDate("DTANIVER"));
                    imp.setValorLimite(rst.getDouble("LIMITECRED"));
                    imp.setEmpresa(rst.getString("EMPRESA"));
                    imp.setEmpresaTelefone(rst.getString("FONE_EMP"));
                    imp.setCargo(rst.getString("CARGO"));
                    imp.setSalario(rst.getDouble("RENDA"));
                    imp.setObservacao(rst.getString("OBS"));
                    imp.setAtivo("1".equals(rst.getString("ATIVO")));
                    if ((rst.getString("BLOQCARTAO") != null)
                            && (!rst.getString("BLOQCARTAO").trim().isEmpty())) {
                        if ("N".equals(rst.getString("BLOQCARTAO").trim())) {
                            imp.setPermiteCheque(true);
                            imp.setPermiteCreditoRotativo(true);
                            imp.setBloqueado(false);
                        } else {
                            imp.setPermiteCheque(false);
                            imp.setPermiteCreditoRotativo(false);
                            imp.setBloqueado(true);
                        }
                    } else {
                        imp.setPermiteCheque(false);
                        imp.setPermiteCreditoRotativo(false);
                        imp.setBloqueado(true);
                    }
                    imp.setSexo("1".equals(rst.getString("SEXO")) ? TipoSexo.FEMININO : TipoSexo.MASCULINO);
                    if ((rst.getString("ESTADOCIVILNOVO") != null)
                            && (!rst.getString("ESTADOCIVILNOVO").trim().isEmpty())) {
                        if (null != rst.getString("ESTADOCIVILNOVO")) {
                            switch (rst.getString("ESTADOCIVILNOVO")) {
                                case "1":
                                    imp.setEstadoCivil(TipoEstadoCivil.SOLTEIRO);
                                    break;
                                case "2":
                                    imp.setEstadoCivil(TipoEstadoCivil.CASADO);
                                    break;
                                case "3":
                                    imp.setEstadoCivil(TipoEstadoCivil.VIUVO);
                                    break;
                                case "4":
                                    imp.setEstadoCivil(TipoEstadoCivil.AMAZIADO);
                                    break;
                                case "5":
                                    imp.setEstadoCivil(TipoEstadoCivil.OUTROS);
                                    break;
                                default:
                                    imp.setEstadoCivil(TipoEstadoCivil.OUTROS);
                                    break;
                            }
                        }
                    } else {
                        imp.setEstadoCivil(TipoEstadoCivil.OUTROS);
                    }

                    if ((rst.getString("FONE1") != null)
                            && (!rst.getString("FONE1").trim().isEmpty())) {
                        imp.addContato(
                                "1",
                                "FONE 1",
                                rst.getString("FONE1"),
                                null,
                                null
                        );
                    }
                    if ((rst.getString("FONE2") != null)
                            && (!rst.getString("FONE2").trim().isEmpty())) {
                        imp.addContato(
                                "1",
                                "FONE 2",
                                rst.getString("FONE2"),
                                null,
                                null
                        );
                    }
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT "
                    + "CODRECEBER AS ID, "
                    + "CLIENTES.CNPJ_CPF, "
                    + "CODRECEBER, NUMTIT, "
                    + "RECEBER.CODCLIE, "
                    + "NOTAECF, "
                    + "DTVENCTO, "
                    + "DTEMISSAO, "
                    + "DTPAGTO, "
                    + "coalesce(VALOR,0) VALOR, "
                    + "coalesce(VALORJUROS,0) VALORJUROS, "
                    + "OBS "
                    + "FROM "
                    + "RECEBER "
                    + "INNER JOIN CLIENTES ON CLIENTES.CODCLIE = RECEBER.CODCLIE "
                    + "where UPPER(SITUACAO) = 'AB' "
                    + "and RECEBER.CODTIPODOCUMENTO = " + v_tipoDocumento + " "
                    + "and RECEBER.CODLOJA = " + getLojaOrigem() + " "
                    + "order by DTEMISSAO"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("ID"));
                    imp.setIdCliente(rst.getString("CODCLIE"));
                    imp.setDataEmissao(rst.getDate("DTEMISSAO"));
                    imp.setDataVencimento(rst.getDate("DTVENCTO"));
                    imp.setValor(rst.getDouble("VALOR"));
                    imp.setNumeroCupom(rst.getString("NOTAECF"));
                    imp.setObservacao(rst.getString("OBS"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ChequeIMP> getCheques() throws Exception {
        List<ChequeIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT "
                    + "CODRECEBER AS ID, "
                    + "CLIENTES.CNPJ_CPF, "
                    + "CLIENTES.RAZAO, "
                    + "CLIENTES.RG, "
                    + "CODRECEBER, "
                    + "NUMTIT, "
                    + "RECEBER.CODCLIE, "
                    + "RECEBER.NUMCHEQUE, "
                    + "RECEBER.CODTIPODOCUMENTO, "
                    + "NOTAECF, "
                    + "DTVENCTO, "
                    + "DTEMISSAO, "
                    + "DTPAGTO, "
                    + "coalesce(VALOR,0) VALOR, "
                    + "coalesce(VALORJUROS,0) VALORJUROS, "
                    + "OBS, "
                    + "CLIENTES.TELEFONE, "
                    + "RECEBER.CODBANCO, "
                    + "RECEBER.AGENCIA, "
                    + "RECEBER.CONTACORR "
                    + "FROM RECEBER "
                    + "INNER JOIN CLIENTES ON "
                    + "CLIENTES.CODCLIE = RECEBER.CODCLIE "
                    + "where UPPER(SITUACAO) = 'AB' "
                    + "and RECEBER.CODLOJA = " + getLojaOrigem() + " "
                    + "and RECEBER.CODTIPODOCUMENTO IN (" + v_tipoDocumentoCheque + ") "
                    + "order by DTEMISSAO "
            )) {
                while (rst.next()) {
                    int idBanco = new BancoDAO().getId(rst.getInt("CODBANCO"));
                    ChequeIMP imp = new ChequeIMP();
                    imp.setId(rst.getString("ID"));
                    imp.setDate(rst.getDate("DTEMISSAO"));
                    imp.setDataDeposito(rst.getDate("DTVENCTO"));
                    imp.setNumeroCupom(rst.getString("NOTAECF"));
                    imp.setNumeroCheque(rst.getString("NUMCHEQUE"));
                    imp.setAgencia(rst.getString("AGENCIA"));
                    imp.setConta(rst.getString("CONTACORR"));
                    imp.setTelefone(rst.getString("TELEFONE"));
                    imp.setCpf(rst.getString("CNPJ_CPF"));
                    imp.setNome(rst.getString("RAZAO"));
                    imp.setTelefone(rst.getString("RG"));
                    imp.setObservacao(rst.getString("OBS"));
                    imp.setValor(rst.getDouble("VALOR"));
                    imp.setBanco(idBanco);

                    if ((v_tipoDocumentoCheque == 5)
                            || (v_tipoDocumentoCheque == 13)) {
                        imp.setAlinea(11);
                    } else {
                        imp.setAlinea(0);
                    }

                    if (v_tipoDocumentoCheque == 5) {
                        imp.setIdLocalCobranca(1);
                    } else if (v_tipoDocumentoCheque == 13) {
                        imp.setIdLocalCobranca(2);
                    }
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    public List<ItemComboVO> getTipoDocumento() throws Exception {
        List<ItemComboVO> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select CODTIPODOCUMENTO, DESCRICAO from TIPODOCUMENTO order by CODTIPODOCUMENTO"
            )) {
                while (rst.next()) {
                    result.add(new ItemComboVO(rst.getInt("CODTIPODOCUMENTO"),
                            rst.getString("CODTIPODOCUMENTO") + " - "
                            + rst.getString("DESCRICAO")));
                }
            }
        }
        return result;
    }

    @Override
    public List<ContaPagarIMP> getContasPagar() throws Exception {
        List<ContaPagarIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT "
                    + "CODPAGAR, "
                    + "CODFORNEC, "
                    + "NOTA, "
                    + "VALOR, "
                    + "DTVENCTO, "
                    + "DTEMISSAO, "
                    + "OBS, "
                    + "OBS2, "
                    + "VALORPAGO, "
                    + "DTPAGTO, "
                    + "DTENTRADA "
                    + "FROM PAGAR "
                    + "where CODLOJA = " + getLojaOrigem() + " "
                    + "and DTPAGTO IS NULL "
                    + "order by DTEMISSAO "
            )) {
                while (rst.next()) {
                    ContaPagarIMP imp = new ContaPagarIMP();
                    imp.setId(rst.getString("CODPAGAR"));
                    imp.setIdFornecedor(rst.getString("CODFORNEC"));
                    imp.setNumeroDocumento(rst.getString("NOTA"));
                    imp.setValor(rst.getDouble("VALOR"));
                    imp.setDataEmissao(rst.getDate("DTEMISSAO"));
                    imp.setDataEntrada(rst.getDate("DTENTRADA"));
                    imp.setDataHoraAlteracao(rst.getTimestamp("DTENTRADA"));
                    imp.setObservacao((rst.getString("OBS") == null ? "" : rst.getString("OBS")) + " "
                            + (rst.getString("OBS2") == null ? "" : rst.getString("OBS2")));
                    imp.addVencimento(rst.getDate("DTVENCTO"), imp.getValor());
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    public void importarReceberDevolucao(int idLojaVR) throws Exception {
        List<ReceberDevolucaoVO> vResult = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados ReceberDevolucao...");
            vResult = getReceberDevolucao();
            if (!vResult.isEmpty()) {
                new ReceberDevolucaoDAO().salvar(vResult, idLojaVR);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    public List<ReceberDevolucaoVO> getReceberDevolucao() throws Exception {
        List<ReceberDevolucaoVO> vResult = new ArrayList<>();
        int idFornecedor;
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT "
                    + "CODRECEBER AS ID, "
                    + "CLIENTES.CNPJ_CPF, "
                    + "CODRECEBER, NUMTIT, "
                    + "RECEBER.CODCLIE, "
                    + "NOTAECF, "
                    + "DTVENCTO, "
                    + "DTEMISSAO, "
                    + "DTPAGTO, "
                    + "coalesce(VALOR,0) VALOR, "
                    + "coalesce(VALORJUROS,0) VALORJUROS, "
                    + "OBS "
                    + "FROM "
                    + "RECEBER "
                    + "INNER JOIN CLIENTES ON CLIENTES.CODCLIE = RECEBER.CODCLIE "
                    + "where UPPER(SITUACAO) = 'AB' "
                    + "and RECEBER.CODTIPODOCUMENTO = " + v_tipoDocumento + " "
                    + "and RECEBER.CODLOJA = " + getLojaOrigem() + " "
                    + "order by DTEMISSAO"
            )) {
                while (rst.next()) {
                    String obs = "";
                    if ((rst.getString("CNPJ_CPF") != null)
                            && (!rst.getString("CNPJ_CPF").trim().isEmpty())) {

                        idFornecedor = new FornecedorDAO().getIdByCnpj(Long.parseLong(Utils.formataNumero(rst.getString("CNPJ_CPF"))));
                        if (idFornecedor != -1) {
                            ReceberDevolucaoVO imp = new ReceberDevolucaoVO();
                            imp.setIdFornecedor(idFornecedor);
                            if ((rst.getString("NOTAECF") != null)
                                    && (!rst.getString("NOTAECF").trim().isEmpty())) {
                                if (rst.getString("NOTAECF").trim().length() > 9) {
                                    obs = "NOTAECF " + rst.getString("NOTAECF");
                                } else {
                                    imp.setNumeroNota(Integer.parseInt(Utils.formataNumero(rst.getString("NOTAECF"))));
                                }
                            } else {
                                imp.setNumeroNota(0);
                            }
                            imp.setDataemissao(rst.getDate("DTEMISSAO"));
                            imp.setDatavencimento(rst.getDate("DTVENCTO"));
                            imp.setValor(rst.getDouble("VALOR"));
                            imp.setObservacao("IMPORTADO VR " + (rst.getString("OBS") == null ? "" : rst.getString("OBS").trim())
                                    + " " + (rst.getString("NUMTIT") == null ? "" : rst.getString("NUMTIT").trim()) + " " + obs);
                            vResult.add(imp);
                        }
                    }
                }
            }
        }
        return vResult;
    }

    public void importarReceberVerba(int idLojaVR) throws Exception {
        List<ReceberVerbaVO> vResult = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados ReceberVerba...");
            vResult = getReceberVerba();
            if (!vResult.isEmpty()) {
                new ReceberVerbaDAO().salvar(vResult, idLojaVR);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    public List<ReceberVerbaVO> getReceberVerba() throws Exception {
        List<ReceberVerbaVO> vResult = new ArrayList<>();
        int idFornecedor;
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT "
                    + "CLIENTES.RAZAO, "
                    + "CODRECEBER AS ID, "
                    + "CLIENTES.CNPJ_CPF, "
                    + "CLIENTES.RG, "
                    + "CLIENTES.IE, "
                    + "CODRECEBER, NUMTIT, "
                    + "RECEBER.CODCLIE, "
                    + "NOTAECF, "
                    + "DTVENCTO, "
                    + "DTEMISSAO, "
                    + "DTPAGTO, "
                    + "coalesce(VALOR,0) VALOR, "
                    + "coalesce(VALORJUROS,0) VALORJUROS, "
                    + "OBS, "
                    + "CLIENTES.TELEFONE "
                    + "FROM "
                    + "RECEBER "
                    + "INNER JOIN CLIENTES ON CLIENTES.CODCLIE = RECEBER.CODCLIE "
                    + "where UPPER(SITUACAO) = 'AB' "
                    + "and RECEBER.CODTIPODOCUMENTO = " + v_tipoDocumento + " "
                    + "and RECEBER.CODLOJA = " + getLojaOrigem() + " "
                    + "order by DTEMISSAO"
            )) {
                while (rst.next()) {
                    if ((rst.getString("CNPJ_CPF") != null)
                            && (!rst.getString("CNPJ_CPF").trim().isEmpty())) {

                        idFornecedor = new FornecedorDAO().getIdByCnpj(Long.parseLong(Utils.formataNumero(rst.getString("CNPJ_CPF"))));
                        if (idFornecedor != -1) {
                            ReceberVerbaVO imp = new ReceberVerbaVO();
                            imp.setIdFornecedor(idFornecedor);
                            imp.setDataemissao(rst.getDate("DTEMISSAO"));
                            imp.setDatavencimento(rst.getDate("DTVENCTO"));
                            imp.setValor(rst.getDouble("VALOR"));
                            imp.setRepresentante(rst.getString("RAZAO") == null ? "" : rst.getString("RAZAO").trim());
                            imp.setTelefone((rst.getString("TELEFONE") == null ? "" : rst.getString("TELEFONE").trim()));
                            imp.setCpfRepresentante(Long.parseLong(Utils.formataNumero("CNPJ_CPF")));
                            if ((rst.getString("RG") != null)
                                    && (!rst.getString("RG").trim().isEmpty())) {
                                imp.setRgRepresentante(rst.getString("RG").trim());
                            } else if ((rst.getString("IE") != null)
                                    && (!rst.getString("IE").trim().isEmpty())) {
                                imp.setRgRepresentante(rst.getString("IE").trim());
                            } else {
                                imp.setRgRepresentante("");
                            }
                            imp.setObservacao("IMPORTADO VR " + (rst.getString("OBS") == null ? "" : rst.getString("OBS").trim()) + " "
                                    + (rst.getString("NUMTIT") == null ? "" : rst.getString("NUMTIT").trim()));
                            vResult.add(imp);
                        }
                    }
                }
            }
        }
        return vResult;
    }

    public void importarProdutosGetWay(String i_arquio) throws Exception {
        int linha = 0;
        Statement stm = null;
        StringBuilder sql = null;
        try {
            Conexao.begin();
            stm = Conexao.createStatement();
            WorkbookSettings settings = new WorkbookSettings();
            settings.setEncoding("CP1250");
            Workbook arquivo = Workbook.getWorkbook(new File(i_arquio), settings);
            Sheet[] sheets = arquivo.getSheets();

            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;
                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;
                    if (linha == 1) {
                        continue;
                    }

                    Cell cellPr_codInt = sheet.getCell(0, i);
                    Cell cellPr_cBarra = sheet.getCell(1, i);
                    Cell cell_Pr_nome = sheet.getCell(2, i);

                    sql = new StringBuilder();
                    sql.append("insert into implantacao.produtos_getway ("
                            + "codprod, "
                            + "barras, "
                            + "descricao) "
                            + "values ("
                            + "'" + cellPr_codInt.getContents().trim() + "' ,"
                            + "lpad('" + cellPr_cBarra.getContents().trim() + "', 14, '0'), "
                            + "'" + Utils.acertarTexto(cell_Pr_nome.getContents().trim()) + "')");
                    stm.execute(sql.toString());
                    System.out.println(i);
                }
            }
            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }   

    
    private static class ClienteCaixa {

        String codCaixa;
        String data;
        String coo;
        String codClie;
        String razao;
        String endereco;
        String numero;
        String complemento;
        String bairro;
        String cidade;
        String estado;
        String cep;
        String cnpj;
    }
    
    /*@Override
    public List<VendaIMP> getVendas(Set<OpcaoVenda> opcoes) throws Exception {
        List<VendaIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            MultiMap<String, ClienteCaixa> clientesVenda = new MultiMap<>();
            
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	cc.CODCAIXA,\n" +
                    "	cc.DATA,\n" +
                    "	cc.COO,\n" +
                    "	cc.CODCLIE,\n" +
                    "	c.RAZAO,\n" +
                    "	c.ENDERECO,\n" +
                    "	c.NUMERO,\n" +
                    "	c.COMPLEMENTO,\n" +
                    "	c.BAIRRO,\n" +
                    "	c.CIDADE,\n" +
                    "	c.ESTADO,\n" +
                    "	c.CEP,\n" +
                    "	c.CNPJ_CPF\n" +
                    "from\n" +
                    "	caixacliente cc\n" +
                    "	left join CLIENTES c on cast(c.CODCLIE as int) = cast(cc.CODCLIE as int)\n" +
                    "where\n" +
                    "	cc.CODLOJA = " + getLojaOrigem()
            )) {
                while (rst.next()) {
                    ClienteCaixa cl = new ClienteCaixa();
                    
                    cl.codCaixa = rst.getString("CODCAIXA");
                    cl.data = rst.getString("DATA");
                    cl.coo = rst.getString("COO");
                    cl.codClie = rst.getString("CODCLIE");
                    cl.razao = Utils.acertarTexto(rst.getString("RAZAO"));
                    cl.endereco = Utils.acertarTexto(rst.getString("ENDERECO"));
                    cl.numero = Utils.acertarTexto(rst.getString("NUMERO"));
                    cl.complemento = Utils.acertarTexto(rst.getString("COMPLEMENTO"));
                    cl.bairro = Utils.acertarTexto(rst.getString("BAIRRO"));
                    cl.cidade = Utils.acertarTexto(rst.getString("CIDADE"));
                    cl.estado = Utils.acertarTexto(rst.getString("ESTADO"));
                    cl.cep = Utils.acertarTexto(rst.getString("CEP"));
                    cl.cnpj = Utils.acertarTexto(rst.getString("CNPJ_CPF"));
                    
                    clientesVenda.put(
                            cl,
                            cl.codCaixa,
                            cl.data,
                            cl.coo
                    );
                }
            }
            try (ResultSet rst = stm.executeQuery(
                    "select	\n" +
                    "	vi.CODCAIXA ecf,\n" +
                    "	vi.coo numerocupom,\n" +
                    "	vi.data,\n" +
                    "	vi.ccf,\n" +
                    "	COUNT(*),\n" +
                    "	min(vi.HORA) horainicio,\n" +
                    "	max(vi.HORA) horatermino,\n" +
                    "	sum(coalesce(vi.TOTITEM, 0)) subTotalImpressora,\n" +
                    "	sum(coalesce(vi.DESCITEM, 0)) desconto,\n" +
                    "	sum(coalesce(vi.ACRESCITEM, 0)) acrescimo\n" +
                    "from\n" +
                    "	CAIXAGERAL vi\n" +
                    "where \n" +
                    "	vi.TIPOLANCTO = ''\n" +
                    "	and	vi.data > current_timestamp - 540\n" +
                    "	and	vi.atualizado = 'S'\n" +
                    "	and (vi.flgrupo = 'S' or vi.flgrupo = 'N')\n" +
                    "	and vi.CODLOJA = " + getLojaOrigem() + "\n" +
                    "group by	\n" +
                    "	vi.CODCAIXA,\n" +
                    "	vi.coo,\n" +
                    "	vi.data,\n" +
                    "	vi.ccf\n" +
                    "order by	\n" +
                    "	data,\n" +
                    "	vi.CODCAIXA,\n" +
                    "	vi.coo"
            )) {
                while (rst.next()) {
                    VendaIMP imp = new VendaIMP();
                    
                    StringBuilder id = new StringBuilder();
                    id
                            .append(rst.getString("ecf")).append("-")
                            .append(rst.getString("numerocupom")).append("-")
                            .append(rst.getString("data")).append("-")
                            .append(rst.getString("ccf"));
                    
                    imp.setId(id.toString());
                    imp.setEcf(rst.getInt("ecf"));
                    
                    ClienteCaixa cli = clientesVenda.get(
                            rst.getString("ecf"),
                            rst.getString("data"),
                            rst.getString("numerocupom")
                    );
                    
                    if (cli != null) {
                        imp.setIdClientePreferencial(cli.codClie);
                        imp.setNomeCliente(cli.razao);
                        imp.setEnderecoCliente(
                                cli.endereco + 
                                (!"".equals(cli.numero) ? "," + cli.numero : "" ) + 
                                (!"".equals(cli.complemento) ? "," + cli.complemento : "") +
                                (!"".equals(cli.bairro) ? "," + cli.bairro : "") + 
                                (!"".equals(cli.cidade) ? "," + cli.cidade : "") +
                                (!"".equals(cli.estado) ? " - " + cli.estado : "") +
                                (!"".equals(cli.cep) ? "," + cli.cep : "")
                        );
                        imp.setCpf(cli.cnpj);
                    }
                    
                    imp.setNumeroCupom(rst.getInt("numerocupom"));
                    imp.setData(rst.getDate("data"));
                    imp.setHoraInicio(rst.getTime("horainicio"));
                    imp.setHoraTermino(rst.getTime("horatermino"));
                    imp.setSubTotalImpressora(rst.getDouble("subTotalImpressora"));
                    imp.setValorDesconto(rst.getDouble("desconto"));
                    imp.setValorAcrescimo(rst.getDouble("acrescimo"));
                    
                    result.add(imp);
                }
            }
        }

        return result;
    }*/

    private Date dataInicioVenda;
    private Date dataTerminoVenda;

    public void setDataInicioVenda(Date dataInicioVenda) {
        this.dataInicioVenda = dataInicioVenda;
    }

    public void setDataTerminoVenda(Date dataTerminoVenda) {
        this.dataTerminoVenda = dataTerminoVenda;
    }
    
    @Override
    public Iterator<VendaIMP> getVendaIterator() throws Exception {
        return new VendaIterator(getLojaOrigem(), dataInicioVenda, dataTerminoVenda);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new VendaItemIterator(getLojaOrigem(), dataInicioVenda, dataTerminoVenda);
    }
    
    private static class VendaIterator implements Iterator<VendaIMP> {
        
        private final static SimpleDateFormat FORMAT = new SimpleDateFormat("dd/MM/yyyy");
        
        private Statement stm = ConexaoSqlServer.getConexao().createStatement();
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
                        String id = rst.getString("numerocupom") + "-" + rst.getString("ecf") + "-" + rst.getString("data");
                        if (!uk.add(id)) {
                            LOG.warning("Venda " + id + " já existe na listagem");
                        }
                        next.setId(id);
                        next.setNumeroCupom(Utils.stringToInt(rst.getString("numerocupom")));
                        next.setEcf(Utils.stringToInt(rst.getString("ecf")));
                        next.setData(rst.getDate("data"));
                        next.setIdClientePreferencial(rst.getString("idclientepreferencial"));
                        String horaInicio = timestampDate.format(rst.getDate("data")) + " " + rst.getString("horainicio");
                        String horaTermino = timestampDate.format(rst.getDate("data")) + " " + rst.getString("horatermino");
                        next.setHoraInicio(timestamp.parse(horaInicio));
                        next.setHoraTermino(timestamp.parse(horaTermino));
                        next.setCancelado(rst.getBoolean("cancelado"));
                        next.setSubTotalImpressora(rst.getDouble("subtotalimpressora"));
                        next.setCpf(rst.getString("cpf"));
                        next.setValorDesconto(rst.getDouble("desconto"));
                        next.setValorAcrescimo(rst.getDouble("acrescimo"));
                        next.setNumeroSerie(rst.getString("numeroserie"));
                        next.setModeloImpressora(rst.getString("modelo"));
                        next.setNomeCliente(rst.getString("nomecliente"));                        
                        String endereco = 
                                Utils.acertarTexto(rst.getString("endereco")) + "," +
                                Utils.acertarTexto(rst.getString("numero")) + "," +
                                Utils.acertarTexto(rst.getString("complemento")) + "," +
                                Utils.acertarTexto(rst.getString("bairro")) + "," +
                                Utils.acertarTexto(rst.getString("cidade")) + "-" +
                                Utils.acertarTexto(rst.getString("estado")) + "," +
                                Utils.acertarTexto(rst.getString("cep"));
                        next.setEnderecoCliente(endereco);
                    }
                }
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }        
        
        public VendaIterator (String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {
            this.sql = 
                    "select\n" +
                    "    cx.coo as numerocupom,\n" +
                    "    cx.codcaixa as ecf,\n" +
                    "    cx.data as data,\n" +
                    "    cx.cliente as idclientepreferencial,\n" +
                    "    min(cx.hora) as horainicio,\n" +
                    "    max(cx.hora) as horatermino,\n" +
                    "    min(case when cx.cancelado = 'N' then 0 else 1 end) as cancelado,\n" +
                    //"    sum(cx.totitem) as subtotalimpressora,\n" +
                    "    sum(cx.totitem - isnull(cx.descitem,0) + isnull(cx.acrescitem, 0)) as subtotalimpressora,\n" +
                    "    cl.cnpj_cpf cpf,\n" +
                    "    sum(isnull(cx.descitem,0)) desconto,\n" +
                    "    sum(isnull(cx.acrescitem, 0)) acrescimo,\n" +
                    "    pdv.NUM_SERIE numeroserie,\n" +
                    "    pdv.IMP_MODELO modelo,\n" +
                    "    pdv.IMP_MARCA marca,\n" +
                    "    cl.razao nomecliente,\n" +
                    "    cl.endereco,\n" +
                    "    cl.numero,\n" +
                    "    cl.complemento,\n" +
                    "    cl.bairro,\n" +
                    "    cl.cidade,\n" +
                    "    cl.estado,\n" +
                    "    cl.cep\n" +
                    "from\n" +
                    "    caixageral as cx\n" +
                    "    join PRODUTOS pr on cx.codprod = pr.codprod\n" +
                    "    left join creceita c on pr.codcreceita = c.codcreceita\n" +
                    "    left join clientes cl on cx.cliente = cast(cl.codclie as varchar(20))\n" +
                    "    left join parampdv pdv on cx.codloja = pdv.CODLOJA and cx.codcaixa = pdv.CODCAIXA\n" +
                    "where\n" +
                    "    cx.tipolancto = '' and\n" +
                    "    (cx.data between convert(datetime, '" + FORMAT.format(dataInicio) + "', 103) and convert(datetime, '" + FORMAT.format(dataTermino) + "', 103)) and\n" +
                    "    cx.codloja = " + idLojaCliente + " and\n" +
                    "    cx.atualizado = 'S' and\n" +
                    "    (cx.flgrupo = 'S' or cx.flgrupo = 'N')\n" +
                    "group by\n" +
                    "	 cx.coo,\n" +
                    "    cx.codcaixa,\n" +
                    "    cx.data,\n" +
                    "    cx.cliente,\n" +
                    "	 cl.cnpj_cpf,\n" +
                    "    pdv.NUM_SERIE,\n" +
                    "    pdv.IMP_MODELO,\n" +
                    "    pdv.IMP_MARCA,\n" +
                    "    cl.razao,\n" +
                    "    cl.endereco,\n" +
                    "    cl.numero,\n" +
                    "    cl.complemento,\n" +
                    "    cl.bairro,\n" +
                    "    cl.cidade,\n" +
                    "    cl.estado,\n" +
                    "    cl.cep\n" +
                    "order by\n" +
                    "    data, numerocupom";
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

        private final static SimpleDateFormat FORMAT = new SimpleDateFormat("dd/MM/yyyy");
        
        private Statement stm = ConexaoSqlServer.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaItemIMP next;
        private Set<String> uk = new HashSet<>();
        
        private void obterNext() {
            try {
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaItemIMP();
                        String id = rst.getString("numerocupom") + "-" + rst.getString("ecf") + "-" + rst.getString("data");

                        next.setId(rst.getString("id"));
                        next.setVenda(id);
                        next.setProduto(rst.getString("produto"));
                        next.setDescricaoReduzida(rst.getString("descricao"));
                        next.setQuantidade(rst.getDouble("quantidade"));
                        next.setPrecoVenda(rst.getDouble("unitario"));
                        next.setCancelado(rst.getBoolean("cancelado"));
                        next.setValorDesconto(rst.getDouble("desconto"));
                        next.setValorAcrescimo(rst.getDouble("acrescimo"));
                        next.setCodigoBarras(rst.getString("codigobarras"));
                        next.setUnidadeMedida(rst.getString("unidade"));
                        
                        String trib = rst.getString("codaliq_venda");
                        if (trib == null || "".equals(trib)) {
                            trib = rst.getString("codaliq_produto");
                        }
                        
                        obterAliquota(next, trib);
                    }
                }
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }        

        /**
         * Método temporario, desenvolver um mapeamento eficiente da tributação.
         * @param item
         * @throws SQLException 
         */
        public void obterAliquota(VendaItemIMP item, String icms) throws SQLException {
            /*
            TA	7.00	ALIQUOTA 07%
            TB	12.00	ALIQUOTA 12%
            TC	18.00	ALIQUOTA 18%
            TD	25.00	ALIQUOTA 25%
            TE	11.00	ALIQUOTA 11%
            I	0.00	ISENTO
            F	0.00	SUBST TRIBUTARIA
            N	0.00	NAO INCIDENTE
            */
            int cst;
            double aliq;
            switch (icms) {
                case "TA":
                    cst = 0;
                    aliq = 7;
                    break;
                case "TB":
                    cst = 0;
                    aliq = 12;
                    break;
                case "TC":
                    cst = 0;
                    aliq = 18;
                    break;
                case "TD":
                    cst = 0;
                    aliq = 25;
                    break;
                case "TE":
                    cst = 0;
                    aliq = 11;
                    break;
                case "F":
                    cst = 60;
                    aliq = 0;
                    break;
                case "N":
                    cst = 41;
                    aliq = 0;
                    break;
                default:
                    cst = 40;
                    aliq = 0;
                    break;
            }
            item.setIcmsCst(cst);
            item.setIcmsAliq(aliq);
        }
        
        public VendaItemIterator (String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {
            this.sql = 
                    "select\n" +
                    "    cx.id,\n" +
                    "    cx.coo as numerocupom,\n" +
                    "    cx.codcaixa as ecf,\n" +
                    "    cx.data as data,\n" +
                    "    cx.codprod as produto,\n" +
                    "    pr.DESC_PDV as descricao,    \n" +
                    "    isnull(cx.qtd, 0) as quantidade,\n" +
                    "    isnull(cx.valorunit, 0) as unitario,\n" +
                    "    case when cx.cancelado = 'N' then 0 else 1 end as cancelado,\n" +
                    "    isnull(cx.descitem, 0) as desconto,\n" +
                    "    isnull(cx.acrescitem, 0) as acrescimo,\n" +
                    "    cx.barra codigobarras,\n" +
                    "    pr.unidade,\n" +
                    "    cx.codaliq codaliq_venda,\n" +
                    "    pr.codaliq codaliq_produto,\n" +
                    "    ic.DESCRICAO trib_desc\n" +
                    "from\n" +
                    "    caixageral as cx\n" +
                    "    join PRODUTOS pr on cx.codprod = pr.codprod\n" +
                    "    left join creceita c on pr.codcreceita = c.codcreceita\n" +
                    "    left join clientes cl on cx.cliente = cast(cl.codclie as varchar(20))\n" +
                    "    left join ALIQUOTA_ICMS ic on pr.codaliq = ic.codaliq\n" +
                    "where\n" +
                    "    cx.tipolancto = '' and\n" +
                    "    (cx.data between convert(datetime, '" + FORMAT.format(dataInicio) + "', 103) and convert(datetime, '" + FORMAT.format(dataTermino) + "', 103)) and\n" +
                    "    c.apura = 'S' and\n" +
                    "    cx.codloja = " + idLojaCliente + " and\n" +
                    "    pr.ativo = 'S' and\n" +
                    "    cx.atualizado = 'S' and\n" +
                    "    (cx.flgrupo = 'S' or cx.flgrupo = 'N')";
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
