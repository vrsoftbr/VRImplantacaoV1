package vrimplantacao.dao.cadastro;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vr.core.parametro.versao.Versao;
import vrimplantacao.classe.Global;
import vrframework.classe.Conexao;
import vrimplantacao.vo.loja.LojaVO;
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
                    "select\n"
                    + "	ecf.id,\n"
                    + "	ecf.id_loja,\n"
                    + "	ecf.id_tipomarca,\n"
                    + "	marca.descricao marca,\n"
                    + "	ecf.id_tipomodelo,\n"
                    + "	modelo.descricao modelo,\n"
                    + "	ecf.ecf,\n"
                    + "	ecf.descricao,\n"
                    + "	ecf.numeroserie\n"
                    + "from\n"
                    + "	pdv.ecf ecf\n"
                    + "	join pdv.tipomarca marca on ecf.id_tipomarca = marca.id\n"
                    + "	join pdv.tipomodelo modelo on ecf.id_tipomodelo = modelo.id\n"
                    + "where\n"
                    + "	rtrim(ecf.numeroserie) = " + SQLUtils.stringSQL(numeroSerie) + " and\n"
                    + "	ecf.id_loja = " + idLojaVR
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

    public List<String> carregarCopiaEcfLayout(LojaVO i_loja) throws Exception {
        String sql = "select \n"
                + " distinct ec.id  as id_ecf,\n"
                + " t.id as id_teclado ,\n"
                + " f.id as id_finalizadora ,\n"
                + " a.id as id_acumaldor,\n"
                + " a2.id as id_aliquotalayout,\n"
                + " e.regracalculo ,\n"
                + " e.arredondamentoabnt \n"
                + " from pdv.ecf ec\n"
                + " left join pdv.tecladolayout t on t.id_loja = ec.id_loja and ec.id_loja = " + i_loja.idCopiarLoja +  "\n"
                + " left join pdv.finalizadoralayout f on f.id_loja = ec.id_loja  and ec.id_loja = " + i_loja.idCopiarLoja +  "\n"
                + " left join pdv.acumuladorlayout a on a.id_loja = ec.id_loja and ec.id_loja = " + i_loja.idCopiarLoja +  "\n"
                + " left join pdv.aliquotalayout a2 on a2.id_loja = ec.id_loja and ec.id_loja = " + i_loja.idCopiarLoja +  "\n"
                + " left join pdv.ecflayout e  on e.id_ecf = ec.id and ec.id_loja = " + i_loja.idCopiarLoja + "\n"
                        + " where ec.id_loja = " + i_loja.idCopiarLoja;
        String sqlInsert = null;
        List<String> listaDeInserts = new ArrayList<>();

        int proximoId = captaUltimoIdEcf() + 1;

        if (proximoId == -1) {
            System.out.println("Erro em PdvBalancaLayoutDAO, provávelmente não há dados na tabela pdv.balancaetiquetalayout.");
            throw new Exception("Erro ao Copiar pdv.balancaetiquetalayout, provávelmente não há dados na tabela");
        }

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    sql
            )) {
                while (rst.next()) {
                    sqlInsert = "insert into pdv.ecflayout (id, id_ecf,id_tecladolayout,id_finalizadoralayout,id_acumuladorlayout,id_aliquotalayout,regracalculo,arredondamentoabnt) values ("
                            + proximoId++ + ", "
                            + rst.getInt("id_ecf") + ", "
                            + rst.getInt("id_teclado") + ", "
                            + rst.getInt("id_finalizadora") + ", "
                            + rst.getInt("id_acumaldor") + ", "
                            + rst.getInt("id_aliquotalayout") + ", '"
                            + rst.getString("regracalculo") + "', "
                            + rst.getBoolean("arredondamentoabnt") + ");";
                    listaDeInserts.add(sqlInsert);
                    sqlInsert = null;
                }
            }
        }
        return listaDeInserts;
    }

    private int captaUltimoIdEcf() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select  max(id) id\n"
                    + "	from pdv.ecflayout "
            )) {
                while (rst.next()) {
                    return rst.getInt("id");
                }
            }
        }
        return -2;
    }
}
