/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import vrframework.classe.Conexao;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.OpcaoFiscal;
import vrimplantacao2.vo.enums.TipoIva;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.PautaFiscalIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Lucas
 */
public class MerceariaSeneDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "MerceariaSene";
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "distinct icms_saida "
                    + "from implantacao.produtoplanilha_sene"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(
                            rst.getString("icms_saida"),
                            rst.getString("icms_saida")
                    ));
                }
            }

            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "distinct icms_entrada "
                    + "from implantacao.produtoplanilha_sene"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(
                            rst.getString("icms_entrada"),
                            rst.getString("icms_entrada")
                    ));
                }
            }

            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "distinct icms_pdv "
                    + "from implantacao.produtoplanilha_sene"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(
                            rst.getString("icms_pdv"),
                            rst.getString("icms_pdv")
                    ));
                }
            }
        }
        return result;
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                new OpcaoProduto[]{
                    OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                    OpcaoProduto.MERCADOLOGICO_PRODUTO,
                    OpcaoProduto.MERCADOLOGICO,
                    OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                    OpcaoProduto.FAMILIA_PRODUTO,
                    OpcaoProduto.FAMILIA,
                    OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                    OpcaoProduto.MANTER_DESCRICAO_PRODUTO,
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
                    OpcaoProduto.PRECO,
                    OpcaoProduto.CUSTO,
                    OpcaoProduto.CUSTO_COM_IMPOSTO,
                    OpcaoProduto.CUSTO_SEM_IMPOSTO,
                    OpcaoProduto.ESTOQUE,
                    OpcaoProduto.ATIVO,
                    OpcaoProduto.NCM,
                    OpcaoProduto.CEST,
                    OpcaoProduto.PIS_COFINS,
                    OpcaoProduto.NATUREZA_RECEITA,
                    OpcaoProduto.ICMS,
                    OpcaoProduto.PAUTA_FISCAL,
                    OpcaoProduto.PAUTA_FISCAL_PRODUTO,
                    OpcaoProduto.MARGEM,
                    OpcaoProduto.EXCECAO,
                    OpcaoProduto.MAPA_TRIBUTACAO
                }
        ));
    }
    
    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	plu as id,\n"
                    + "	ean,\n"
                    + "	descricao,\n"
                    + "	fora_linha,\n"
                    + "	saneamento,\n"
                    + "	piscofins_saida,\n"
                    + "	piscofins_entrada,\n"
                    + "	naturezareceita,\n"
                    + "	ncm,\n"
                    + "	cest,\n"
                    + "	iva,\n"
                    + "	icms_saida,\n"
                    + "	icms_entrada,\n"
                    + "	icms_pdv\n"
                    + "from implantacao.produtoplanilha_sene"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("ean"));
                    imp.setDescricaoCompleta(rst.getString("descricao"));
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    //imp.setDataCadastro(rst.getDate("saneamento"));
                    imp.setNcm(Utils.formataNumero(rst.getString("ncm")));
                    imp.setCest(Utils.formataNumero(rst.getString("cest")));
                    imp.setPiscofinsCstDebito(Utils.formataNumero(rst.getString("piscofins_saida")));
                    imp.setPiscofinsCstCredito(Utils.formataNumero(rst.getString("piscofins_saida")));
                    imp.setPiscofinsNaturezaReceita(rst.getString("naturezareceita"));
                    imp.setIcmsDebitoId(rst.getString("icms_saida"));
                    imp.setIcmsDebitoForaEstadoId(rst.getString("icms_saida"));
                    imp.setIcmsDebitoForaEstadoNfId(rst.getString("icms_saida"));
                    imp.setIcmsCreditoId(rst.getString("icms_entrada"));
                    imp.setIcmsCreditoForaEstadoId(rst.getString("icms_entrada"));
                    imp.setIcmsConsumidorId(rst.getString("icms_pdv"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos(OpcaoProduto opt) throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        if (opt == OpcaoProduto.EXCECAO) {
            try (Statement stm = Conexao.createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select \n"
                        + "plu as id, \n"
                        + "	ncm,\n"
                        + "	icms_entrada,\n"
                        + "	icms_saida,\n"
                        + "	iva\n"
                        + "from implantacao.produtoplanilha_sene \n"
                        + "where iva != '0'"
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("id"));
                        imp.setPautaFiscalId(Utils.formataNumero(rst.getString("ncm"))
                                + rst.getString("icms_entrada")
                                + rst.getString("icms_saida")
                                + rst.getString("iva"));

                        result.add(imp);
                    }
                }
            }
            return result;
        }

        return null;
    }

    @Override
    public List<PautaFiscalIMP> getPautasFiscais(Set<OpcaoFiscal> opcoes) throws Exception {
        List<PautaFiscalIMP> result = new ArrayList<>();

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select distinct \n"
                    + "	ncm,\n"
                    + "	icms_entrada,\n"
                    + "	icms_saida,\n"
                    + "	iva\n"
                    + "from implantacao.produtoplanilha_sene \n"
                    + "where iva != '0'"
            )) {
                while (rst.next()) {
                    PautaFiscalIMP imp = new PautaFiscalIMP();
                    imp.setId(Utils.formataNumero(rst.getString("ncm"))
                            + rst.getString("icms_entrada")
                            + rst.getString("icms_saida")
                            + rst.getString("iva")
                    );
                    imp.setTipoIva(TipoIva.PERCENTUAL);
                    imp.setIva(Double.parseDouble(rst.getString("iva").replace(",", ".")));
                    imp.setIvaAjustado(imp.getIva());
                    imp.setNcm(Utils.formataNumero(rst.getString("ncm")));

                    /* icms credito */
                    if ("F 0% CST 60".equals(rst.getString("icms_entrada"))) {
                        imp.setAliquotaCredito(60, 0, 0);
                        imp.setAliquotaCreditoForaEstado(60, 0, 0);
                    } else if ("F 12% CST 10".equals(rst.getString("icms_entrada"))) {
                        imp.setAliquotaCredito(0, 12, 0);
                        imp.setAliquotaCreditoForaEstado(0, 12, 0);
                    } else if ("F 12% CST 70 41,67".equals(rst.getString("icms_entrada"))) {
                        imp.setAliquotaCredito(20, 12, 41.67);
                        imp.setAliquotaCreditoForaEstado(20, 12, 41.67);
                    } else if ("F 18% CST 10".equals(rst.getString("icms_entrada"))) {
                        imp.setAliquotaCredito(0, 18, 0);
                        imp.setAliquotaCreditoForaEstado(0, 18, 0);
                    } else if ("F 18% CST 70 33,33".equals(rst.getString("icms_entrada"))) {
                        imp.setAliquotaCredito(20, 18, 33.33);
                        imp.setAliquotaCreditoForaEstado(20, 18, 33.33);
                    } else if ("F 18% CST 70 61,11".equals(rst.getString("icms_entrada"))) {
                        imp.setAliquotaCredito(20, 18, 61.11);
                        imp.setAliquotaCreditoForaEstado(20, 18, 61.11);
                    } else if ("F 22% CST 10".equals(rst.getString("icms_entrada"))) {
                        imp.setAliquotaCredito(0, 22, 0);
                        imp.setAliquotaCreditoForaEstado(0, 22, 0);
                    } else if ("F 25% CST 10".equals(rst.getString("icms_entrada"))) {
                        imp.setAliquotaCredito(0, 25, 0);
                        imp.setAliquotaCreditoForaEstado(0, 25, 0);
                    } else if ("F 25% CST 70".equals(rst.getString("icms_credito"))) {
                        imp.setAliquotaCredito(0, 25, 0);
                        imp.setAliquotaCreditoForaEstado(0, 25, 0);
                    } else if ("F 32% CST 10".equals(rst.getString("icms_credito"))) {
                        imp.setAliquotaCredito(0, 32, 0);
                        imp.setAliquotaCreditoForaEstado(0, 32, 0);
                    } else if ("I 0% CST 40".equals(rst.getString("icms_entrada"))) {
                        imp.setAliquotaCredito(40, 0, 0);
                        imp.setAliquotaCreditoForaEstado(40, 0, 0);
                    } else if ("N 0% CST 41".equals(rst.getString("icms_entrdada"))) {
                        imp.setAliquotaCredito(41, 0, 0);
                        imp.setAliquotaCreditoForaEstado(41, 0, 0);
                    } else if ("R 18% 61,11".equals(rst.getString("icms_entrada"))) {
                        imp.setAliquotaCredito(20, 18, 61.11);
                        imp.setAliquotaCreditoForaEstado(20, 18, 61.11);
                    } else if ("T 07% CST 00".equals(rst.getString("icms_entrada"))) {
                        imp.setAliquotaCredito(0, 7, 0);
                        imp.setAliquotaCreditoForaEstado(0, 7, 0);
                    } else if ("T 12% CST 00".equals(rst.getString("icms_entrada"))) {
                        imp.setAliquotaCredito(0, 12, 0);
                        imp.setAliquotaCreditoForaEstado(0, 12, 0);
                    } else if ("T 18% CST 00".equals(rst.getString("icms_entrada"))) {
                        imp.setAliquotaCredito(0, 18, 0);
                        imp.setAliquotaCreditoForaEstado(0, 18, 0);
                    } else {
                        imp.setAliquotaCredito(40, 0, 0);
                        imp.setAliquotaCreditoForaEstado(40, 0, 0);
                    }

                    /* aliquto débito */
                    if ("F 0% CST 60".equals(rst.getString("icms_saida"))) {
                        imp.setAliquotaDebito(60, 0, 0);
                        imp.setAliquotaDebitoForaEstado(60, 0, 0);
                    } else {
                        imp.setAliquotaDebito(40, 0, 0);
                        imp.setAliquotaDebitoForaEstado(40, 0, 0);
                    }

                    result.add(imp);
                }
            }
        }
        return result;
    }
}
