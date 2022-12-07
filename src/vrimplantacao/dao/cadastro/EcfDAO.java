package vrimplantacao.dao.cadastro;

import java.sql.ResultSet;
import java.sql.Statement;
import vr.core.parametro.versao.Versao;
import vrimplantacao.classe.Global;
import vrframework.classe.Conexao;
import vrimplantacao2.dao.cadastro.pdv.ecf.EcfPdvVO;
import vrimplantacao2.dao.cadastro.venda.EcfVO;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.utils.sql.SQLUtils;

public class EcfDAO {
    
    private final Versao versao = Versao.createFromConnectionInterface(Conexao.getConexao());

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

    public void salvarECFPdv(EcfPdvVO ecf) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            sql.setTableName("ecf");
            sql.setSchema("pdv");
            sql.put("id", ecf.getId());
            sql.put("id_loja", ecf.getId_loja());
            sql.put("ecf", ecf.getEcf());
            sql.put("descricao", ecf.getDescricao());
            sql.put("id_tipomarca", ecf.getId_tipomarca());
            sql.put("id_tipomodelo", ecf.getId_tipomodelo());
            sql.put("id_situacaocadastro", ecf.getId_situacaocadastro());
            sql.put("numeroserie", ecf.getNumeroserie());
            sql.put("mfadicional", ecf.getMfadicional());
            sql.put("numerousuario", ecf.getNumerousuario());
            sql.put("tipoecf", ecf.getTipoecf());
            sql.put("versaosb", ecf.getVersaosb());
            sql.put("datahoragravacaosb", ecf.getDatahoragravacaosb());
            sql.put("datahoracadastro", ecf.getDatahoracadastro());
            sql.put("incidenciadesconto", ecf.isIncidenciadesconto());
            sql.put("versaobiblioteca", ecf.getVersaobiblioteca());
            sql.put("geranfpaulista", ecf.isGeranfpaulista());
            sql.put("id_tipoestado", ecf.getId_tipoestado());
            sql.put("versao", ecf.getVersao());
            sql.put("datamovimento", ecf.getDatamovimento());
            sql.put("cargagdata", ecf.isCargagdata());
            sql.put("cargaparam", ecf.isCargaparam());
            sql.put("cargalayout", ecf.isCargalayout());
            sql.put("cargaimagem", ecf.isCargaimagem());
            sql.put("id_tipolayoutnotapaulista", ecf.getId_tipolayoutnotapaulista());
            sql.put("touch", ecf.isTouch());
            sql.put("alteradopaf", ecf.isAlteradopaf());
            sql.put("horamovimento", ecf.getHoramovimento());
            sql.put("id_tipoemissor", ecf.getId_tipoemissor());
            //versao.getVersao();
            //sql.put("id_modelopdv", ecf.getId_modelopdv());
            try {
                stm.execute(sql.getInsert());
            } catch (Exception a) {
                try {
                    stm.execute(sql.getUpdate());
                    System.out.println(sql.getInsert());
                } catch (Exception e) {
                    System.out.println(sql.getInsert());
                    e.printStackTrace();
                    throw e;
                }
            }
        }
    }
}
