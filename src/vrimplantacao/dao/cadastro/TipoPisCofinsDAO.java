package vrimplantacao.dao.cadastro;

import java.sql.ResultSet;
import java.sql.Statement;
import vrimplantacao.vo.cadastro.TipoPisCofinsVO;
import vrframework.classe.Conexao;
import vrframework.classe.Util;
import vrframework.classe.VRException;

public class TipoPisCofinsDAO {

    public TipoPisCofinsVO carregar(int i_id) throws Exception {
        Statement stm = null;
        StringBuilder sql = null;
        ResultSet rst = null;

        stm = Conexao.createStatement();

        sql = new StringBuilder();
        sql.append("SELECT * FROM tipopiscofins WHERE id = " + i_id);

        rst = stm.executeQuery(sql.toString());

        if (!rst.next()) {
            throw new VRException("Tipo PIS/COFINS " + i_id + " não encontrado!");
        }

        TipoPisCofinsVO oTipoPisCofins = new TipoPisCofinsVO();
        oTipoPisCofins.id = rst.getInt("id");
        oTipoPisCofins.descricao = rst.getString("descricao");
        oTipoPisCofins.cst = rst.getInt("cst");
        oTipoPisCofins.valorPis = rst.getDouble("valorpis");
        oTipoPisCofins.valorCofins = rst.getDouble("valorcofins");

        stm.close();

        return oTipoPisCofins;
    }

    public double getPisCofins(int i_id) throws Exception {
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

    public int getId(int i_cst, double i_valorPis, double i_valorCofins) throws Exception {
        Statement stm = null;
        StringBuilder sql = null;
        ResultSet rst = null;

        stm = Conexao.createStatement();

        sql = new StringBuilder();
        sql.append("SELECT id FROM tipopiscofins WHERE cst = " + i_cst + " AND valorpis = " + i_valorPis + " AND valorcofins = " + i_valorCofins);

        rst = stm.executeQuery(sql.toString());

        if (rst.next()) {
            return rst.getInt("id");
        } else {
            return -1;
        }
    }
    
    public int getCstDebito(int i_idTipoPisCofinsDebito) throws Exception {
        Statement stm = null;
        StringBuilder sql = null;
        ResultSet rst = null;

        stm = Conexao.createStatement();

        sql = new StringBuilder();
        sql.append("SELECT cst");
        sql.append(" FROM tipopiscofins");
        sql.append(" WHERE id = " + i_idTipoPisCofinsDebito);

        rst = stm.executeQuery(sql.toString());

        if (rst.next()) {
            return rst.getInt("cst");
        } else {
            return -1;
        }
    }
    
    public int getId(int i_cst) throws Exception {
        Statement stm = null;
        StringBuilder sql = null;
        ResultSet rst = null;

        stm = Conexao.createStatement();

        sql = new StringBuilder();
        sql.append("SELECT id FROM tipopiscofins WHERE cst = " + i_cst);

        rst = stm.executeQuery(sql.toString());

        if (rst.next()) {
            return rst.getInt("id");
        } else {
            return -1;
        }
    }
}
