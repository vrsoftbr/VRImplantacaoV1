package vrimplantacao.dao.cadastro;

import java.sql.ResultSet;
import java.sql.Statement;
import vrframework.classe.Conexao;
import vrframework.classe.VRException;

public class ModeloDAO {

    public String getEspecie(String i_modelo) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        stm = Conexao.createStatement();

        sql = new StringBuilder();
        sql.append("SELECT especie FROM modelo WHERE modelo = '" + i_modelo + "'");

        rst = stm.executeQuery(sql.toString());

        if (!rst.next()) {
            throw new VRException("Modelo " + i_modelo + " não encontrado!");
        }

        return rst.getString("especie");
    }
}
