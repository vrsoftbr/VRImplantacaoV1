package vrimplantacao.dao;

import java.sql.ResultSet;
import java.sql.Statement;
import vrframework.classe.Conexao;
import vrframework.classe.Util;
import vrframework.classe.VRException;

public class PisCofinsDAO {

    public double get(int i_id) throws Exception {
        return Util.round(getPis(i_id) + getCofins(i_id), 2);
    }

    public double getPis(int i_id) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        stm = Conexao.createStatement();

        sql = new StringBuilder();
        sql.append("SELECT valorpis - (valorpis * reduzidocredito / 100) AS valorpis FROM tipopiscofins WHERE id = " + i_id);

        rst = stm.executeQuery(sql.toString());

        if (!rst.next()) {
            throw new VRException("Tipo PIS/COFINS " + i_id + " não encontrado!");
        }

        return Util.round(rst.getDouble("valorpis"), 2);
    }

    public double getCofins(int i_id) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        stm = Conexao.createStatement();

        sql = new StringBuilder();
        sql.append("SELECT valorcofins - (valorcofins * reduzidocredito / 100) AS valorcofins FROM tipopiscofins WHERE id = " + i_id);

        rst = stm.executeQuery(sql.toString());

        if (!rst.next()) {
            throw new VRException("Tipo PIS/COFINS " + i_id + " não encontrado!");
        }

        return Util.round(rst.getDouble("valorcofins"), 2);
    }
}
