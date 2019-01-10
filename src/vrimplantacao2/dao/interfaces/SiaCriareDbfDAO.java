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
}
