/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author lucasrafael
 */
public class DestroDAO extends InterfaceDAO {

    public String v_estadoIcms = "";

    @Override
    public String getSistema() {
        return "Destro";
    }

    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "filial_chave id,\n"
                    + "f_raz descricao\n"
                    + "from filial\n"
                    + "order by filial_chave"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("id"), rst.getString("descricao")));
                }
            }
        }
        return result;
    }
    
    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "P.ESTITEM_CHAVE id,\n"
                    + "P.I_BAR,\n"
                    + "P.I_DES,\n"
                    + "P.UNIDADE_UNV_CHAVE,\n"
                    + "P.I_QUN,\n"
                    + "P.I_PUN,\n"
                    + "P.I_DRD,\n"
                    + "P.I_ICMS,\n"
                    + "IC.ICMS_COD_CHAVE,\n"
                    + "P.CST_ICMS_SAIDA,\n"
                    + "P.ctribut_b_chave,\n"
                    + "IC.I_PER,\n"
                    + "IC.i_prb,\n"
                    + "P.DATA_INCLUSAO,\n"
                    + "P.DATA_ALTERACAO,\n"
                    + "P.NCM,\n"
                    + "p.cest,\n"
                    + "P.PESOLIQ,\n"
                    + "P.PESOBRUTO,\n"
                    + "P.PIS_SAIDA_SITTRIB,\n"
                    + "P.PIS_ENTRADA_SITTRIB,\n"
                    + "P.NATREC,\n"
                    + "I.FILIAL_CHAVE,\n"
                    + "I.T_CUS,\n"
                    + "T_PVL,\n"
                    + "I.secao_chave\n"
                    + "FROM ESTITEM p\n"
                    + "INNER JOIN item I ON I.estitem_chave  = P.estitem_chave\n"
                    + "INNER JOIN ICMS IC ON IC.ICMS_COD_CHAVE = P.i_icms and I.estitem_chave  = P.estitem_chave\n"
                    + "where IC.estado_chave = '" + v_estadoIcms + "'\n"
                    + "and i.filial_chave = " + getLojaOrigem() + "\n"
                    + "ORDER BY P.estitem_chave"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("I_BAR"));
                    imp.setDescricaoCompleta(rst.getString("I_DES"));
                    imp.setDescricaoReduzida(rst.getString("I_DRD"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setTipoEmbalagem(rst.getString("UNIDADE_UNV_CHAVE"));
                    imp.setQtdEmbalagem(rst.getInt("I_QUN"));
                    imp.setDataCadastro(rst.getDate("DATA_INCLUSAO") == null ? rst.getDate("DATA_ALTERACAO") : rst.getDate("DATA_INCLUSAO"));
                    imp.setPesoBruto(rst.getDouble("PESOBRUTO"));
                    imp.setPesoLiquido(rst.getDouble("PESOLIQ"));
                    imp.setNcm(rst.getString("NCM"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getString("PIS_SAIDA_SITTRIB"));
                    imp.setPiscofinsCstCredito(rst.getString("PIS_ENTRADA_SITTRIB"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("NATREC"));
                    imp.setIcmsCst(rst.getDouble("i_prb") > 0 ? 20 : rst.getInt("CST_ICMS_SAIDA"));
                    imp.setIcmsAliq(rst.getDouble("I_PER"));
                    imp.setIcmsReducao(rst.getDouble("i_prb"));
                    
                    if ((Double.parseDouble(Utils.formataNumero(rst.getString("i_bar"))) < 10000) &&
                            (Double.parseDouble(Utils.formataNumero(rst.getString("i_bar"))) > 0)) {
                        imp.seteBalanca(true);
                    } else {
                        imp.seteBalanca(false);
                    }
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos(OpcaoProduto opcao) throws Exception {
        if (opcao == OpcaoProduto.CUSTO) {
            List<ProdutoIMP> result = new ArrayList<>();
            try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "WITH\n"
                        + "mov AS(\n"
                        + "    select m.estitem_chave, MAX(movestoq_chave) as movi\n"
                        + "    FROM movestoq as m\n"
                        + "    where m.filial_chave in (" + getLojaOrigem() + ")\n"
                        + "    group by m.estitem_chave\n"
                        + ")\n"
                        + "select\n"
                        + "    m.estitem_chave id_produto,\n"
                        + "    m.preco_custo custo\n"
                        + "from movestoq as m\n"
                        + "inner join mov on mov.estitem_chave = m.estitem_chave AND mov.movi = m.movestoq_chave\n"
                        + "where m.filial_chave in (" + getLojaOrigem() + ")"
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("id_produto"));
                        imp.setCustoComImposto(rst.getDouble("custo"));
                        imp.setCustoSemImposto(rst.getDouble("custo"));
                        result.add(imp);
                    }
                }
                return result;
            }
        } else if (opcao == OpcaoProduto.PRECO) {
            List<ProdutoIMP> result = new ArrayList<>();
            try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "WITH\n"
                        + "mov AS(\n"
                        + "    select m.estitem_chave, MAX(movestoq_chave) as movi\n"
                        + "    FROM movestoq as m\n"
                        + "    where m.filial_chave in (" + getLojaOrigem() + ")\n"
                        + "    group by m.estitem_chave\n"
                        + ")\n"
                        + "select\n"
                        + "    m.estitem_chave id_produto,\n"
                        + "    m.preco_venda venda\n"
                        + "from movestoq as m\n"
                        + "inner join mov on mov.estitem_chave = m.estitem_chave AND mov.movi = m.movestoq_chave\n"
                        + "where m.filial_chave in (" + getLojaOrigem() + ")"
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("id_produto"));
                        imp.setPrecovenda(rst.getDouble("venda"));
                        result.add(imp);
                    }
                }
                return result;
            }
        } else if (opcao == OpcaoProduto.ESTOQUE) {
            List<ProdutoIMP> result = new ArrayList<>();
            try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "WITH\n"
                        + "mov AS(\n"
                        + "    select m.estitem_chave, MAX(movestoq_chave) as movi\n"
                        + "    FROM movestoq as m\n"
                        + "    where m.filial_chave in (" + getLojaOrigem() + ")\n"
                        + "    group by m.estitem_chave\n"
                        + ")\n"
                        + "select\n"
                        + "    m.estitem_chave id_produto,\n"
                        + "    m.quantidade estoque\n"
                        + "from movestoq as m\n"
                        + "inner join mov on mov.estitem_chave = m.estitem_chave AND mov.movi = m.movestoq_chave\n"
                        + "where m.filial_chave in (" + getLojaOrigem() + ")"
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("id_produto"));
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
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "estitem_chave id_produto,\n"
                    + "codbarr_chave_bar codigobarras\n"
                    + "from CODBARR\n"
                    + "where cast(codbarr_chave_bar as numeric(14,0)) > 999999"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id_produto"));
                    imp.setEan(rst.getString("codigobarras"));
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
