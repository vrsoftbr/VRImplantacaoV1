package vrimplantacao2.gui.component.mapareformatributaria;

import java.util.List;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import vrframework.classe.Conexao;
import vrimplantacao2.vo.enums.Cst;
import vrimplantacao2.utils.sql.SQLUtils;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.gui.component.mapareformatributaria.cst.MapaReformaTributariaCstVO;

/**
 *
 * @author Wesley
 */
public class MapaReformaTributariaDAO {

    /**
     * Cria a tabela implantacao.reformatributaria_cst se não existir.
     *
     * @throws Exception
     */
    public void createTableReformaTributariaCst() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(
                    "CREATE TABLE IF NOT EXISTS implantacao.reformatributaria_cst(\n"
                    + "	sistema varchar NOT NULL,\n"
                    + "	loja varchar NOT NULL,\n"
                    + "	orig_id varchar NOT NULL,\n"
                    + "	orig_cst integer NOT NULL,\n"
                    + "	orig_descricao varchar NOT NULL,\n"
                    + "	id_cst int,\n"
                    + "	grupoibscbs bool DEFAULT false NOT NULL,\n"
                    + "	gruporeducao bool DEFAULT false NOT NULL,\n"
                    + "	grupodiferimento bool DEFAULT false NOT NULL,\n"
                    + "	grupotribregular bool DEFAULT false NOT NULL,\n"
                    + "	grupoibscbsmono bool DEFAULT false NOT NULL,\n"
                    + "	primary key (sistema, loja, orig_id)\n"
                    + ");"
            );
        }
    }

    /**
     * Retorna todas as CSTs da Reforma Tributaria no VR.
     *
     * @return
     * @throws Exception
     */
    public List<Cst> getReformaTributariaCstVR() throws Exception {
        List<Cst> result = new ArrayList<>();

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT 	\n"
                    + "	c.id AS id,\n"
                    + "	c.cst AS cst,\n"
                    + "	c.descricao AS descricao,\n"
                    + "	c.grupoibscbs,\n"
                    + "	c.gruporeducao,\n"
                    + "	c.grupodiferimento,\n"
                    + "	c.grupotribregular,\n"
                    + "	c.grupoibscbsmono \n"
                    + "FROM reformatributaria.cst c \n"
                    + "WHERE id_situacaocadastro = 1 \n"
                    + "ORDER BY c.cst::NUMERIC, c.descricao "
            )) {
                while (rst.next()) {
                    Cst vo = new Cst(
                            rst.getInt("id"),
                            rst.getInt("cst"),
                            rst.getString("descricao"),
                            rst.getBoolean("grupoibscbs"),
                            rst.getBoolean("gruporeducao"),
                            rst.getBoolean("grupodiferimento"),
                            rst.getBoolean("grupotribregular"),
                            rst.getBoolean("grupoibscbsmono")
                    );
                    result.add(vo);
                }
            }
        }

        return result;
    }

    public void gravarReformaTributariaCstOrigem(List<MapaReformaTributariaCstVO> reformaTributariaCst) throws Exception {
        MapaReformaTributariaCstVO rtCstVO = null;
        try {
            Conexao.begin();

            try (Statement stm = Conexao.createStatement()) {
                for (MapaReformaTributariaCstVO vo : reformaTributariaCst) {
                    rtCstVO = vo;
                    SQLBuilder sql = new SQLBuilder();
                    sql.setSchema("implantacao");
                    sql.setTableName("reformatributaria_cst");
                    sql.put("sistema", vo.getSistema());
                    sql.put("loja", vo.getLoja());
                    sql.put("orig_id", vo.getOrigId());
                    sql.put("orig_cst", vo.getOrigCst());
                    sql.put("orig_descricao", vo.getOrigDescricao());
                    sql.put("grupoibscbs", vo.isGrupoIbsCbs());
                    sql.put("gruporeducao", vo.isGrupoReducao());
                    sql.put("grupodiferimento", vo.isGrupoDiferimento());
                    sql.put("grupotribregular", vo.isGrupoTribRegular());
                    sql.put("grupoibscbsmono", vo.isGrupoIbsCbsMono());

                    if (vo.getCst() != null) {
                        sql.put("id_cst", vo.getCst().getId());
                    }
                    try (ResultSet rst = stm.executeQuery(
                            "select"
                            + "	rt.sistema,\n"
                            + "	rt.loja,\n"
                            + "	rt.orig_id\n"
                            + "from\n"
                            + "	implantacao.reformatributaria_cst rt\n"
                            + "where\n"
                            + "	rt.sistema = " + SQLUtils.stringSQL(rtCstVO.getSistema()) + " and\n"
                            + "	rt.loja = " + SQLUtils.stringSQL(rtCstVO.getLoja()) + " and\n"
                            + "	rt.orig_id = " + SQLUtils.stringSQL(rtCstVO.getOrigId())
                    )) {
                        if (!rst.next()) {
                            stm.execute(sql.getInsert());
                        }
                    }
                }
            }

            Conexao.commit();
        } catch (Exception e) {
            System.out.println("select"
                    + "	rt.sistema,\n"
                    + "	rt.loja,\n"
                    + "	rt.orig_id\n"
                    + "from\n"
                    + "	implantacao.reformatributaria_cst rt\n"
                    + "where\n"
                    + "	rt.sistema = " + SQLUtils.stringSQL(rtCstVO.getSistema()) + " and\n"
                    + "	rt.loja = " + SQLUtils.stringSQL(rtCstVO.getLoja()) + " and\n"
                    + "	rt.orig_id = " + SQLUtils.stringSQL(rtCstVO.getOrigId()));
            Conexao.rollback();
            throw e;
        }
    }

//    public Map<String, MapaReformaTributariaCstVO> getMapaAsMap(String sistema, String loja) throws Exception {
//        Map<String, MapaReformaTributariaCstVO> result = new HashMap<>();
//        
//        for (MapaReformaTributariaCstVO t: getMapa(sistema, loja)) {
//            result.put(t.getOrigId(), t);
//        }
//        
//        return result;
//    }
    public List<MapaReformaTributariaCstVO> getMapa(String sistema, String loja) throws Exception {
        List<MapaReformaTributariaCstVO> result = new ArrayList<>();

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	rt.sistema,\n"
                    + "	rt.loja,\n"
                    + "	rt.orig_id,\n"
                    + "	rt.orig_cst,\n"
                    + "	rt.orig_descricao,\n"
                    + "	rt.grupoibscbs AS orig_grupo_ibscbs,\n"
                    + "	rt.gruporeducao AS orig_grupo_reducao,\n"
                    + "	rt.grupodiferimento AS orig_grupo_diferimento,\n"
                    + "	rt.grupotribregular AS orig_grupo_trib_regular,\n"
                    + "	rt.grupoibscbsmono AS orig_grupo_ibscbs_mono,\n"
                    + "	rt.id_cst,\n"
                    + "	c.cst,\n"
                    + "	c.descricao,\n"
                    + "	c.grupoibscbs,\n"
                    + "	c.gruporeducao,\n"
                    + "	c.grupodiferimento,\n"
                    + "	c.grupotribregular,\n"
                    + "	c.grupoibscbsmono \n"
                    + "FROM implantacao.reformatributaria_cst rt \n"
                    + "LEFT JOIN reformatributaria.cst c ON rt.id_cst = c.id \n"
                    + "WHERE rt.sistema = " + SQLUtils.stringSQL(sistema) + " AND \n"
                    + "rt.loja = " + SQLUtils.stringSQL(loja) + " \n"
                    + "ORDER BY id_cst NULLS FIRST, id_cst, rt.orig_cst::NUMERIC, rt.orig_descricao "
            )) {
                while (rst.next()) {

                    MapaReformaTributariaCstVO vo = new MapaReformaTributariaCstVO(
                            rst.getString("sistema"),
                            rst.getString("loja"),
                            rst.getString("orig_id"),
                            rst.getInt("orig_cst"),
                            rst.getString("orig_descricao"),
                            rst.getBoolean("orig_grupo_ibscbs"),
                            rst.getBoolean("orig_grupo_reducao"),
                            rst.getBoolean("orig_grupo_diferimento"),
                            rst.getBoolean("orig_grupo_trib_regular"),
                            rst.getBoolean("orig_grupo_ibscbs_mono")
                    );

                    if (rst.getString("id_cst") != null) {
                        vo.setCst(
                                new Cst(
                                        rst.getInt("id_cst"),
                                        rst.getInt("cst"),
                                        rst.getString("descricao"),
                                        rst.getBoolean("grupoibscbs"),
                                        rst.getBoolean("gruporeducao"),
                                        rst.getBoolean("grupodiferimento"),
                                        rst.getBoolean("grupotribregular"),
                                        rst.getBoolean("grupoibscbsmono")
                                )
                        );
                    }
                    result.add(vo);
                }
            }
        }

        return result;
    }

    public List<Cst> getCstVR(String texto) throws Exception {

        List<Cst> result = new ArrayList<>();

        if (texto == null) {
            texto = "";
        }

        texto = texto.trim();

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + " id, \n"
                    + " cst, \n"
                    + " descricao, \n"
                    + " grupoibscbs, \n"
                    + " gruporeducao, \n"
                    + " grupodiferimento, \n"
                    + " grupotribregular, \n"
                    + " grupoibscbsmono \n"
                    + "FROM reformatributaria.cst \n"
                    + "WHERE cst::varchar LIKE " + SQLUtils.stringSQL(texto + "%")
                    + " AND id_situacaocadastro = 1 \n"
                    + " ORDER BY cst \n"
                    + "LIMIT 10"
            )) {

                while (rst.next()) {
                    result.add(new Cst(
                            rst.getInt("id"),
                            rst.getInt("cst"),
                            rst.getString("descricao"),
                            rst.getBoolean("grupoibscbs"),
                            rst.getBoolean("gruporeducao"),
                            rst.getBoolean("grupodiferimento"),
                            rst.getBoolean("grupotribregular"),
                            rst.getBoolean("grupoibscbsmono")
                    ));
                }
            }
        }

        return result;
    }

    public void gravarMapa(MapaReformaTributariaCstVO mapa) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            sql.setSchema("implantacao");
            sql.setTableName("reformatributaria_cst");
            if (mapa.getCst() != null) {
                sql.put("id_cst", mapa.getCst().getId());
            } else {
                sql.putNull("id_cst");
            }
            sql.setWhere("sistema = " + SQLUtils.stringSQL(mapa.getSistema()) + " and\n"
                    + "loja = " + SQLUtils.stringSQL(mapa.getLoja()) + " and\n"
                    + "orig_id = " + SQLUtils.stringSQL(mapa.getOrigId()));
            stm.execute(sql.getUpdate());
        }
    }
//    
//    public Map<String, Integer> getAliquotaPorId(String sistema, String loja) throws Exception {
//        Map<String, Integer> result = new HashMap<>();
//        
//        try (Statement stm = Conexao.createStatement()) {
//            try (ResultSet rst = stm.executeQuery(
//                    "select\n" +
//                    "	orig_id id,\n" +
//                    "	id_aliquota\n" +
//                    "from\n" +
//                    "	implantacao.mapatributacao\n" +
//                    "where\n" +
//                    "	sistema = '" + sistema + "' and\n" +
//                    "	agrupador = '" + loja + "'"
//            )) {
//                while (rst.next()) {
//                    result.put(rst.getString("id"), rst.getInt("id_aliquota"));
//                }
//            }
//        }
//        
//        return result;
//    }

    public void vincularCsts(String sistema, String loja) throws Exception {
        try (Statement st = Conexao.createStatement()) {
            st.execute(
                    "with cst as ( "
                    + "   select "
                    + "       id, "
                    + "       cst, "
                    + "       grupoibscbs, "
                    + "       gruporeducao, "
                    + "       grupodiferimento, "
                    + "       grupotribregular, "
                    + "       grupoibscbsmono "
                    + "   from reformatributaria.cst "
                    + "   where id_situacaocadastro = 1 "
                    + "), "
                    + "cst2 as ( "
                    + "   select "
                    + "       m.*, "
                    + "       ( "
                    + "           select c.id "
                    + "           from cst c "
                    + "           where c.cst = lpad(m.orig_cst::text, 3, '0') "
                    + "             and c.grupoibscbs = m.grupoibscbs "
                    + "             and c.gruporeducao = m.gruporeducao "
                    + "             and c.grupodiferimento = m.grupodiferimento "
                    + "             and c.grupotribregular = m.grupotribregular "
                    + "             and c.grupoibscbsmono = m.grupoibscbsmono "
                    + "           limit 1 "
                    + "       ) id "
                    + "   from implantacao.reformatributaria_cst m "
                    + ") "
                    + "update implantacao.reformatributaria_cst a set "
                    + "   id_cst = b.id "
                    + "from cst2 b "
                    + "where a.sistema = b.sistema "
                    + "  and a.loja = b.loja "
                    + "  and a.orig_id = b.orig_id "
                    + "  and a.sistema = '" + sistema + "' "
                    + "  and a.loja = '" + loja + "' "
                    + "  and a.id_cst is null "
                    + "  and b.id is not null"
            );
        }
    }
}
