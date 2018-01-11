package vrimplantacao.dao.cadastro;

import java.sql.ResultSet;
import java.sql.Statement;
import vrimplantacao.classe.Global;
import vrframework.classe.Conexao;
import vrimplantacao2.dao.cadastro.venda.EcfVO;
import vrimplantacao2.utils.sql.SQLUtils;

public class EcfDAO {

    public int get(String i_numeroSerie) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        stm = Conexao.createStatement();

        sql = new StringBuilder();
        sql.append(" SELECT ecf ");
        sql.append(" FROM pdv.ecf ");
        sql.append(" WHERE RTRIM(numeroserie) = '" + i_numeroSerie + "'");
        sql.append(" AND id_loja =" + Global.idLoja);

        rst = stm.executeQuery(sql.toString());

        if (rst.next()) {
            return rst.getInt("ecf");
        } else {
            return -1;
        }
    }
    
    public int get(String i_numeroSerie, int idLojaVR) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        stm = Conexao.createStatement();

        sql = new StringBuilder();
        sql.append(" SELECT ecf ");
        sql.append(" FROM pdv.ecf ");
        sql.append(" WHERE RTRIM(numeroserie) = '" + i_numeroSerie + "'");
        sql.append(" AND id_loja = " + idLojaVR);

        rst = stm.executeQuery(sql.toString());

        if (rst.next()) {
            return rst.getInt("ecf");
        } else {
            return -1;
        }
    }
    
    public EcfVO getEcf(String numeroSerie, int idLojaVR) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	ecf.id,\n" +
                    "	ecf.id_loja,\n" +
                    "	ecf.id_tipomarca,\n" +
                    "	marca.descricao marca,\n" +
                    "	ecf.id_tipomodelo,\n" +
                    "	modelo.descricao modelo,\n" +
                    "	ecf.ecf,\n" +
                    "	ecf.descricao,\n" +
                    "	ecf.numeroserie\n" +
                    "from\n" +
                    "	pdv.ecf ecf\n" +
                    "	join pdv.tipomarca marca on ecf.id_tipomarca = marca.id\n" +
                    "	join pdv.tipomodelo modelo on ecf.id_tipomodelo = modelo.id\n" +
                    "where\n" +
                    "	rtrim(ecf.numeroserie) = " + SQLUtils.stringSQL(numeroSerie) + " and\n" +
                    "	ecf.id_loja = " + idLojaVR
            )) {
                if (rst.next()) {
                    EcfVO ecf = new EcfVO();
                    
                    ecf.setId(rst.getInt("id"));
                    ecf.setIdLoja(rst.getInt("id_loja"));
                    ecf.setIdTipoMarca(rst.getInt("id_tipomarca"));
                    ecf.setMarca(rst.getString("marca"));
                    ecf.setIdTipoModelo(rst.getInt("id_tipomodelo"));
                    ecf.setModelo(rst.getString("modelo"));
                    ecf.setEcf(rst.getInt("ecf"));
                    ecf.setDescricao(rst.getString("descricao"));
                    ecf.setNumeroSerie(rst.getString("numeroserie"));
                    
                    return ecf;
                }
            }
        }
        return null;
    }
}
