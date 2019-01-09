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

/**
 *
 * @author lucasrafael
 */
public class SiaCriareDbfDAO extends InterfaceDAO {

    public String i_arquivo;

    @Override
    public String getSistema() {
        return "SiaCriareDbf";
    }

    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        ConexaoDBF.abrirConexao(i_arquivo);

        try (Statement stm = ConexaoDBF.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "CODEMP, DESCRICAO, CNPJ "
                    + "from empresa"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("CODEMP"), rst.getString("DESCRICAO") + " " + rst.getString("CNPJ")));
                }
            }
        }

        return result;
    }
}
