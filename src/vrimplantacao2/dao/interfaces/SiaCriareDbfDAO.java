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
import vrimplantacao.classe.ConexaoDBF;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author lucasrafael
 */
public class SiaCriareDbfDAO extends InterfaceDAO implements MapaTributoProvider {

    public String i_arquivo;

    @Override
    public String getSistema() {
        return "SiaCriareDbf";
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList();
        ConexaoDBF.abrirConexao(i_arquivo);

        try (Statement stmt = ConexaoDBF.getConexao().createStatement()) {
            try (ResultSet rs = stmt.executeQuery(
                    "select "
                    + "CODALIQ, CST, ALIQUOTA "
                    + "from aliquotas "
                    + "order by CODALIQ"
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(
                            rs.getString("CODALIQ"),
                            "CST. " + rs.getString("CST") + " ALIQ. " + rs.getString("ALIQUOTA")));
                }
            }
        }
        return result;
    }

    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        ConexaoDBF.abrirConexao(i_arquivo);

        try (Statement stm = ConexaoDBF.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "CODEMP, DESCRICAO, CNPJ "
                    + "from empresa "
                    + "order by CODEMP"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(
                            rst.getString("CODEMP"),
                            rst.getString("DESCRICAO") + " " + rst.getString("CNPJ")));
                }
            }
        }

        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();
        ConexaoDBF.abrirConexao(i_arquivo);

        try (Statement stm = ConexaoDBF.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "CODFAM, DESCRICAO "
                    + "from familias "
                    + "order by CODFAM"
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("CODFAM"));
                    imp.setDescricao(rst.getString("DESCRICAO"));
                    result.add(imp);
                }
            }
            return result;
        }
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        ConexaoDBF.abrirConexao(i_arquivo);

        try (Statement stm = ConexaoDBF.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "m1.CODGRUPO as merc1, m1.DESCRICAO as desc_merc1, "
                    + "m2.CODCAT as merc2, m2.DESCRICAO as desc_merc2, "
                    + "m3.CODFAM as merc3, m3.DESCRICAO as desc_merc3 "
                    + "from produtos p "
                    + "left join grupos m1 on m1.CODGRUPO = p.GRUPO "
                    + "left join categorias m2 on m2.CODCAT = p.CATEGORIA "
                    + "left join familias m3 on m3.CODFAM = p.FAMILIA "
                    + "order by m1.CODGRUPO, m2.CODCAT, m3.CODFAM"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("merc1"));
                    imp.setMerc1Descricao(rst.getString("desc_merc1"));
                    imp.setMerc2ID(rst.getString("merc2"));
                    imp.setMerc2Descricao(rst.getString("desc_merc2"));
                    imp.setMerc3ID(rst.getString("merc3"));
                    imp.setMerc3Descricao(rst.getString("desc_merc3"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        ConexaoDBF.abrirConexao(i_arquivo);

        try (Statement stm = ConexaoDBF.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "CODITEM "
                    //+ "GRUPO, "
                    /*+ "DESCRICAO, "
                    + "ABREVIA, "
                    + "(CUSTO / 1000) as CUSTO, "
                    + "(UNITARIO / 1000) as PRECO, "
                    + "BALANCA, "
                    + "ALIQUOTASA as ICMS, "
                    + "UNIDADE, "
                    + "FAMILIA, "
                    + "CODBARRA, "
                    + "NCM, "
                    + "CATEGORIA, "
                    + "ATIVO, "
                    + "PESO_LIQUI, "
                    + "PESO_BRUTO, "
                    + "(QTDEMBALAG / 1000) as QTDEMB, "
                    + "PIS, "
                    + "COFINS, "
                    + "MARKDOWN, "
                    + "CEST "*/
                    + "from produtos "
                    + "order by CODITEM"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("CODITEM"));
                    /*imp.setEan(rst.getString("CODBARRA"));
                    imp.setDescricaoCompleta(rst.getString("DESCRICAO"));
                    imp.setDescricaoReduzida(rst.getString("ABREVIA"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setTipoEmbalagem(rst.getString("UNIDADE"));
                    imp.setQtdEmbalagem(rst.getInt("QTDEMB"));
                    imp.seteBalanca("S".equals(rst.getString("BALANCA")));
                    imp.setMargem(rst.getDouble("MARKDOWN"));
                    imp.setPrecovenda(rst.getDouble("PRECO"));
                    imp.setCustoComImposto(rst.getDouble("CUSTO"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setNcm(rst.getString("NCM"));
                    imp.setCest(rst.getString("CEST"));
                    imp.setPiscofinsCstDebito(rst.getString("PIS"));
                    imp.setPiscofinsCstCredito(rst.getString("COFINS"));
                    imp.setIcmsDebitoId(rst.getString("ICMS"));
                    imp.setIcmsCreditoId(rst.getString("ICMS"));*/
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
