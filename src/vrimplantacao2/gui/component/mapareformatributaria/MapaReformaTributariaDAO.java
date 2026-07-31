package vrimplantacao2.gui.component.mapareformatributaria;

import java.util.List;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.sql.PreparedStatement;
import vrframework.classe.Conexao;
import vrimplantacao2.vo.enums.Cst;
import vrimplantacao2.vo.enums.NcmVO;
import vrimplantacao2.utils.sql.SQLUtils;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.enums.ClassificacaoTributaria;
import vrimplantacao2.vo.enums.ClassificacaoTributariaNcm;
import vrimplantacao2.gui.component.mapareformatributaria.cst.MapaReformaTributariaCstVO;
import vrimplantacao2.gui.component.mapareformatributaria.classificacaotributaria.MapaReformaTributariaClassificacaoVO;
import vrimplantacao2.gui.component.mapareformatributaria.classificacaotributariancm.MapaReformaTributariaClassificacaoNcmVO;

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
     * Cria a tabela implantacao.reformatributaria_classificacao se não existir.
     *
     * @throws Exception
     */
    public void createTableReformaTributariaClassificacao() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(
                    "CREATE TABLE IF NOT EXISTS implantacao.reformatributaria_classificacao(\n"
                    + "	sistema varchar NOT NULL,\n"
                    + "	loja varchar NOT NULL,\n"
                    + "	orig_id varchar NOT NULL,\n"
                    + " orig_id_cst_ibs_cbs varchar NOT NULL,\n"
                    + "	orig_cclasstrib varchar NOT NULL,\n"
                    + "	orig_descricao varchar NOT NULL,\n"
                    + "	id_classificacao int,\n"
                    + "	reducao numeric(11, 2) NULL,\n"
                    + "	diferimento numeric(11, 2) NULL,\n"
                    + "	fundamentacaolegal varchar NULL,\n"
                    + "	aliquotazero bool DEFAULT false NULL,\n"
                    + "	primary key (sistema, loja, orig_id)\n"
                    + ");"
            );
        }
    }

    /**
     * Cria a tabela implantacao.reformatributaria_classificacaoncm se não
     * existir.
     *
     * @throws Exception
     */
    public void createTableReformaTributariaClassificacaoNcm() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(
                    "CREATE TABLE IF NOT EXISTS implantacao.reformatributaria_classificacaoncm(\n"
                    + "	sistema varchar NOT NULL,\n"
                    + "	loja varchar NOT NULL,\n"
                    + "	orig_id varchar NOT NULL,\n"
                    + " orig_id_classificacao varchar NOT NULL,\n"
                    + "	orig_ncm1 numeric(4, 0) NOT NULL,\n"
                    + "	orig_ncm2 numeric(2, 0) NOT NULL,\n"
                    + "	orig_ncm3 numeric(2, 0) NOT NULL,\n"
                    + " id_classificacao_tributacao_ncm numeric(6) NULL,\n"
                    + "	id_classificacao numeric(6) NULL,\n"
                    + "	id_ncm numeric(6) NULL,\n"
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

    public void gravarReformaTributariaClassificacaoOrigem(List<MapaReformaTributariaClassificacaoVO> reformaTributariaClassificacao) throws Exception {
        MapaReformaTributariaClassificacaoVO rtClassificacaoVO = null;
        try {
            Conexao.begin();

            try (Statement stm = Conexao.createStatement()) {
                for (MapaReformaTributariaClassificacaoVO vo : reformaTributariaClassificacao) {
                    rtClassificacaoVO = vo;
                    SQLBuilder sql = new SQLBuilder();
                    sql.setSchema("implantacao");
                    sql.setTableName("reformatributaria_classificacao");
                    sql.put("sistema", vo.getSistema());
                    sql.put("loja", vo.getLoja());
                    sql.put("orig_id", vo.getOrigId());
                    sql.put("orig_id_cst_ibs_cbs", vo.getOrigIdCstIbsCbs());
                    sql.put("orig_cclasstrib", vo.getOrigCclasstrib());
                    sql.put("orig_descricao", vo.getOrigDescricao());
                    sql.put("reducao", vo.getOrigReducao());
                    sql.put("diferimento", vo.getOrigDiferimento());
                    sql.put("fundamentacaolegal", vo.getOrigFundamentacaoLegal());
                    sql.put("aliquotazero", vo.isOrigAliquotaZero());

                    if (vo.getClassificacao() != null) {
                        sql.put("id_cst", vo.getClassificacao().getId());
                    }
                    try (ResultSet rst = stm.executeQuery(
                            "select"
                            + "	rt.sistema,\n"
                            + "	rt.loja,\n"
                            + "	rt.orig_id\n"
                            + "from\n"
                            + "	implantacao.reformatributaria_classificacao rt\n"
                            + "where\n"
                            + "	rt.sistema = " + SQLUtils.stringSQL(rtClassificacaoVO.getSistema()) + " and\n"
                            + "	rt.loja = " + SQLUtils.stringSQL(rtClassificacaoVO.getLoja()) + " and\n"
                            + "	rt.orig_id = " + SQLUtils.stringSQL(rtClassificacaoVO.getOrigId())
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
                    + "	implantacao.reformatributaria_classificacao rt\n"
                    + "where\n"
                    + "	rt.sistema = " + SQLUtils.stringSQL(rtClassificacaoVO.getSistema()) + " and\n"
                    + "	rt.loja = " + SQLUtils.stringSQL(rtClassificacaoVO.getLoja()) + " and\n"
                    + "	rt.orig_id = " + SQLUtils.stringSQL(rtClassificacaoVO.getOrigId()));
            Conexao.rollback();
            throw e;
        }
    }

    public void gravarReformaTributariaClassificacaoNcmOrigem(List<MapaReformaTributariaClassificacaoNcmVO> reformaTributariaClassificacaoNcm) throws Exception {
        MapaReformaTributariaClassificacaoNcmVO rtClassificacaoNcmVO = null;
        try {
            Conexao.begin();

            try (Statement stm = Conexao.createStatement()) {
                for (MapaReformaTributariaClassificacaoNcmVO vo : reformaTributariaClassificacaoNcm) {
                    rtClassificacaoNcmVO = vo;
                    SQLBuilder sql = new SQLBuilder();
                    sql.setSchema("implantacao");
                    sql.setTableName("reformatributaria_classificacaoncm");
                    sql.put("sistema", vo.getSistema());
                    sql.put("loja", vo.getLoja());
                    sql.put("orig_id", vo.getOrigId());
                    sql.put("orig_id_classificacao", vo.getOrigIdClassificacao());
                    sql.put("orig_ncm1", vo.getOrigNcm1());
                    sql.put("orig_ncm2", vo.getOrigNcm2());
                    sql.put("orig_ncm3", vo.getOrigNcm3());

                    if (vo.getClassificacaoNcm() != null) {
                        sql.put("id_classificacao", vo.getClassificacaoNcm().getId());
                        sql.put("id_ncm", vo.getClassificacaoNcm().getIdNcm());
                    }
                    try (ResultSet rst = stm.executeQuery(
                            "select"
                            + "	rt.sistema,\n"
                            + "	rt.loja,\n"
                            + "	rt.orig_id\n"
                            + "from\n"
                            + "	implantacao.reformatributaria_classificacaoncm rt\n"
                            + "where\n"
                            + "	rt.sistema = " + SQLUtils.stringSQL(rtClassificacaoNcmVO.getSistema()) + " and\n"
                            + "	rt.loja = " + SQLUtils.stringSQL(rtClassificacaoNcmVO.getLoja()) + " and\n"
                            + "	rt.orig_id = " + SQLUtils.stringSQL(rtClassificacaoNcmVO.getOrigId())
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
                    + "	implantacao.reformatributaria_classificacaoncm rt\n"
                    + "where\n"
                    + "	rt.sistema = " + SQLUtils.stringSQL(rtClassificacaoNcmVO.getSistema()) + " and\n"
                    + "	rt.loja = " + SQLUtils.stringSQL(rtClassificacaoNcmVO.getLoja()) + " and\n"
                    + "	rt.orig_id = " + SQLUtils.stringSQL(rtClassificacaoNcmVO.getOrigId()));
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
    public List<MapaReformaTributariaCstVO> getMapaCst(String sistema, String loja) throws Exception {
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

    public List<MapaReformaTributariaClassificacaoVO> getMapaClassificacao(String sistema, String loja) throws Exception {
        List<MapaReformaTributariaClassificacaoVO> result = new ArrayList<>();

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	rc.sistema,\n"
                    + "	rc.loja,\n"
                    + "	rc.orig_id,\n"
                    + "	rc.orig_id_cst_ibs_cbs,\n"
                    + "	rc.reducao AS orig_reducao,\n"
                    + "	rc.diferimento AS orig_diferimento,\n"
                    + "	rc.orig_cclasstrib,\n"
                    + "	rc.orig_descricao,\n"
                    + "	rc.fundamentacaolegal AS orig_fundamentacaolegal,\n"
                    + "	rc.aliquotazero AS orig_aliquotazero,\n"
                    + "	c.id,\n"
                    + "	c.reducao ,\n"
                    + "	c.diferimento ,\n"
                    + "	c.id_cstibscbs ,\n"
                    + "	c.cclasstrib ,\n"
                    + "	c.descricao ,\n"
                    + "	c.fundamentacaolegal,\n"
                    + "	c.aliquotazero \n"
                    + "FROM implantacao.reformatributaria_classificacao rc \n"
                    + "LEFT JOIN reformatributaria.classificacaotributaria c ON rc.id_classificacao = c.id \n"
                    + "WHERE rc.sistema = " + SQLUtils.stringSQL(sistema) + " AND \n"
                    + "rc.loja = " + SQLUtils.stringSQL(loja) + " \n"
                    + "ORDER BY id NULLS FIRST, id, rc.orig_cclasstrib ::NUMERIC, rc.orig_descricao "
            )) {
                while (rst.next()) {

                    MapaReformaTributariaClassificacaoVO vo = new MapaReformaTributariaClassificacaoVO(
                            rst.getString("sistema"),
                            rst.getString("loja"),
                            rst.getString("orig_id"),
                            rst.getString("orig_id_cst_ibs_cbs"),
                            rst.getDouble("orig_reducao"),
                            rst.getDouble("orig_diferimento"),
                            rst.getString("orig_cclasstrib"),
                            rst.getString("orig_descricao"),
                            rst.getString("orig_fundamentacaolegal"),
                            rst.getBoolean("orig_aliquotazero")
                    );

                    if (rst.getString("id") != null) {
                        vo.setClassificacao(
                                new ClassificacaoTributaria(
                                        rst.getInt("id"),
                                        rst.getDouble("reducao"),
                                        rst.getDouble("diferimento"),
                                        rst.getInt("id_cstibscbs"),
                                        rst.getString("cclasstrib"),
                                        rst.getString("descricao"),
                                        rst.getString("fundamentacaolegal"),
                                        rst.getBoolean("aliquotazero")
                                )
                        );
                    }
                    result.add(vo);
                }
            }
        }

        return result;
    }

    public List<MapaReformaTributariaClassificacaoNcmVO> getMapaClassificacaoNcm(String sistema, String loja, boolean exibirTodas) throws Exception {
        List<MapaReformaTributariaClassificacaoNcmVO> result = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append(
                "SELECT \n"
                + "    rc.sistema,\n"
                + "    rc.loja,\n"
                + "    rc.orig_id,\n"
                + "    rc.orig_id_classificacao,\n"
                + "    rc.orig_ncm1,\n"
                + "    rc.orig_ncm2,\n"
                + "    rc.orig_ncm3,\n"
                + "    rc.id_classificacao_tributacao_ncm,\n"
                + "    c.id,\n"
                + "    c.id_classificacao,\n"
                + "    c.id_ncm,\n"
                + "    c.ncm1,\n"
                + "    c.ncm2,\n"
                + "    c.ncm3\n"
                + "FROM implantacao.reformatributaria_classificacaoncm rc\n"
                + "LEFT JOIN reformatributaria.classificacaotributariancm c\n"
                + "       ON rc.id_classificacao_tributacao_ncm = c.id\n"
                + "WHERE rc.sistema = " + SQLUtils.stringSQL(sistema) + "\n"
                + "  AND rc.loja = " + SQLUtils.stringSQL(loja) + "\n"
        );

        if (!exibirTodas) {
            sql.append("  AND rc.id_classificacao_tributacao_ncm IS NULL\n");
        }

        sql.append(
                "ORDER BY id NULLS FIRST, id, rc.orig_id::NUMERIC, rc.id_classificacao"
        );

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(sql.toString())) {
                while (rst.next()) {

                    MapaReformaTributariaClassificacaoNcmVO vo = new MapaReformaTributariaClassificacaoNcmVO(
                            rst.getString("sistema"),
                            rst.getString("loja"),
                            rst.getString("orig_id"),
                            rst.getString("orig_id_classificacao"),
                            rst.getInt("orig_ncm1"),
                            rst.getInt("orig_ncm2"),
                            rst.getInt("orig_ncm3")
                    );

                    if (rst.getString("id_classificacao_tributacao_ncm") != null) {
                        vo.setClassificacaoNcm(
                                new ClassificacaoTributariaNcm(
                                        rst.getInt("id"),
                                        rst.getInt("id_classificacao"),
                                        rst.getInt("id_ncm"),
                                        rst.getInt("ncm1"),
                                        rst.getInt("ncm2"),
                                        rst.getInt("ncm3")
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

    public List<ClassificacaoTributaria> getClassificacoesVR(String classificacao, String reducao, String diferimento) throws Exception {

        List<ClassificacaoTributaria> result = new ArrayList<>();

        StringBuilder sql = new StringBuilder();

        sql.append(
                "SELECT\n"
                + " id,\n"
                + " cclasstrib,\n"
                + " descricao,\n"
                + " reducao,\n"
                + " diferimento,\n"
                + " aliquotazero,\n"
                + " id_cstibscbs,\n"
                + " fundamentacaolegal\n"
                + "FROM reformatributaria.classificacaotributaria\n"
                + "WHERE id_situacaocadastro = 1\n"
        );

        List<Object> parametros = new ArrayList<>();

        if (classificacao != null && !classificacao.trim().isEmpty()) {
            sql.append(" AND cclasstrib ILIKE ? \n");
            parametros.add("%" + classificacao.trim() + "%");
        }

        if (reducao != null) {
            sql.append(" AND reducao::text LIKE ? ");
            parametros.add(reducao + "%");
        }

        if (reducao != null) {
            sql.append(" AND diferimento::text LIKE ? ");
            parametros.add(diferimento + "%");
        }

        sql.append(" ORDER BY cclasstrib::NUMERIC, reducao, diferimento LIMIT 10");

        try (PreparedStatement stm = Conexao.prepareStatement(sql.toString())) {

            for (int i = 0; i < parametros.size(); i++) {
                stm.setObject(i + 1, parametros.get(i));
            }

            try (ResultSet rst = stm.executeQuery()) {

                while (rst.next()) {
                    result.add(new ClassificacaoTributaria(
                            rst.getInt("id"),
                            rst.getDouble("reducao"),
                            rst.getDouble("diferimento"),
                            rst.getInt("id_cstibscbs"),
                            rst.getString("cclasstrib"),
                            rst.getString("descricao"),
                            rst.getString("fundamentacaolegal"),
                            rst.getBoolean("aliquotazero")
                    ));
                }
            }
        }

        return result;
    }

    public void gravarMapaCst(MapaReformaTributariaCstVO mapa) throws Exception {
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

    public void gravarMapaClassificacao(MapaReformaTributariaClassificacaoVO mapa) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            sql.setSchema("implantacao");
            sql.setTableName("reformatributaria_classificacao");
            if (mapa.getClassificacao() != null) {
                sql.put("id_classificacao", mapa.getClassificacao().getId());
            } else {
                sql.putNull("id_classificacao");
            }
            sql.setWhere("sistema = " + SQLUtils.stringSQL(mapa.getSistema()) + " and\n"
                    + "loja = " + SQLUtils.stringSQL(mapa.getLoja()) + " and\n"
                    + "orig_id = " + SQLUtils.stringSQL(mapa.getOrigId()));
            stm.execute(sql.getUpdate());
        }
    }

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

    public void vincularClassificacoes(String sistema, String loja) throws Exception {

        try (Statement st = Conexao.createStatement()) {

            st.execute(
                    "WITH classificacao AS ( "
                    + "    SELECT "
                    + "        id, "
                    + "        id_cstibscbs, "
                    + "        cclasstrib, "
                    + "        reducao, "
                    + "        diferimento, "
                    + "        aliquotazero "
                    + "    FROM reformatributaria.classificacaotributaria "
                    + "    WHERE id_situacaocadastro = 1 "
                    + "), mapa AS ( "
                    + "    SELECT "
                    + "        m.sistema, "
                    + "        m.loja, "
                    + "        m.orig_id, "
                    + "        ct.id AS id_classificacao "
                    + "    FROM implantacao.reformatributaria_classificacao m "
                    + "    JOIN implantacao.reformatributaria_cst rc "
                    + "        ON rc.sistema = m.sistema "
                    + "       AND rc.loja = m.loja "
                    + "       AND rc.orig_id = m.orig_id_cst_ibs_cbs "
                    + "    JOIN classificacao ct "
                    + "        ON ct.id_cstibscbs = rc.id_cst "
                    + "       AND ct.cclasstrib = m.orig_cclasstrib "
                    + "       AND COALESCE(ct.reducao,0) = COALESCE(m.reducao,0) "
                    + "       AND COALESCE(ct.diferimento,0) = COALESCE(m.diferimento,0) "
                    + "       AND COALESCE(ct.aliquotazero,false) = COALESCE(m.aliquotazero,false) "
                    + ") "
                    + "UPDATE implantacao.reformatributaria_classificacao a "
                    + "SET id_classificacao = mapa.id_classificacao "
                    + "FROM mapa "
                    + "WHERE a.sistema = mapa.sistema "
                    + "  AND a.loja = mapa.loja "
                    + "  AND a.orig_id = mapa.orig_id "
                    + "  AND a.sistema = '" + sistema + "' "
                    + "  AND a.loja = '" + loja + "' "
                    + "  AND a.id_classificacao IS NULL"
            );

        }
    }

    public void vincularClassificacoesNcm(String sistema, String loja, int lojaVR) throws Exception {

        try (Statement st = Conexao.createStatement()) {

            st.execute(
                    "WITH mapa AS (\n"
                    + "    SELECT\n"
                    + "        m.sistema,\n"
                    + "        m.loja,\n"
                    + "        m.orig_id,\n"
                    + "        c.id_classificacao,\n"
                    + "        n.id AS id_ncm,\n"
                    + "        ctn.id AS id_classificacao_tributacao_ncm\n"
                    + "    FROM implantacao.reformatributaria_classificacaoncm m\n"
                    + "\n"
                    + "    JOIN implantacao.reformatributaria_classificacao c\n"
                    + "         ON c.sistema = m.sistema\n"
                    + "        AND c.loja    = m.loja\n"
                    + "        AND c.orig_id = m.orig_id_classificacao\n"
                    + "\n"
                    + "    JOIN public.ncm n\n"
                    + "              ON n.ncm1 = m.orig_ncm1 \n"
                    + "              AND n.ncm2 = m.orig_ncm2 \n"
                    + "              AND n.ncm3 = m.orig_ncm3 \n"
                    + "\n"
                    + "    LEFT JOIN reformatributaria.classificacaotributariancm ctn\n"
                    + "           ON ctn.id_classificacao = c.id_classificacao\n"
                    + "           AND ctn.id_ncm = n.id \n"
                    + "           AND ctn.id_loja = " + lojaVR
                    + ")\n"
                    + "\n"
                    + "UPDATE implantacao.reformatributaria_classificacaoncm a\n"
                    + "SET\n"
                    + "    id_classificacao = mapa.id_classificacao,\n"
                    + "    id_ncm = mapa.id_ncm,\n"
                    + "    id_classificacao_tributacao_ncm = mapa.id_classificacao_tributacao_ncm\n"
                    + "FROM mapa \n"
                    + "WHERE a.sistema = mapa.sistema \n"
                    + "  AND a.loja    = mapa.loja\n"
                    + "  AND a.orig_id = mapa.orig_id\n"
                    + "  AND a.sistema = '" + sistema + "' \n"
                    + "  AND a.loja    = '" + loja + "' \n"
                    + "  AND (a.id_classificacao_tributacao_ncm IS NULL OR a.id_ncm IS NULL);"
            );

        }
    }

    public int getClassificacaoTributariaByImpid(String sistema, String loja, String impid) throws Exception {

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT id_classificacao "
                    + "FROM implantacao.reformatributaria_classificacao "
                    + "WHERE sistema = " + SQLUtils.stringSQL(sistema)
                    + " AND loja = " + SQLUtils.stringSQL(loja)
                    + " AND orig_id = " + SQLUtils.stringSQL(impid)
            )) {

                if (rst.next()) {
                    return rst.getInt("id_classificacao");
                }
            }
        }

        return 0;
    }

    public NcmVO getNcm(int ncm1, int ncm2, int ncm3) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + " id,\n"
                    + " ncm1,\n"
                    + " ncm2,\n"
                    + " ncm3,\n"
                    + " descricao\n"
                    + "from \n"
                    + " ncm \n"
                    + "where \n"
                    + " nivel = 3 \n"
                    + " and ncm1 = " + ncm1
                    + " and ncm2 = " + ncm2
                    + " and ncm3 = " + ncm3
                    + " order by ncm1, ncm2, ncm3"
            )) {

                if (rst.next()) {
                    NcmVO ncm = new NcmVO();
                    ncm.setId(rst.getInt("id"));
                    ncm.setNcm1(rst.getInt("ncm1"));
                    ncm.setNcm2(rst.getInt("ncm2"));
                    ncm.setNcm3(rst.getInt("ncm3"));
                    ncm.setDescricao(rst.getString("descricao"));

                    return ncm;
                }
            }
        }
        return null;
    }

    public List<NcmVO> getNcms(Integer ncm1, Integer ncm2, Integer ncm3) throws Exception {

        List<NcmVO> result = new ArrayList<>();

        StringBuilder sql = new StringBuilder();

        sql.append("SELECT ");
        sql.append(" id, ncm1, ncm2, ncm3, descricao ");
        sql.append("FROM ncm ");
        sql.append("WHERE nivel = 3 ");

        if (ncm1 != null) {
            sql.append(" AND ncm1 = ").append(ncm1);
        }

        if (ncm2 != null) {
            sql.append(" AND ncm2 = ").append(ncm2);
        }

        if (ncm3 != null) {
            sql.append(" AND ncm3 = ").append(ncm3);
        }

        sql.append(" ORDER BY ncm1, ncm2, ncm3 ");

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(sql.toString())) {

                while (rst.next()) {

                    NcmVO ncm = new NcmVO();

                    ncm.setId(rst.getInt("id"));
                    ncm.setNcm1(rst.getInt("ncm1"));
                    ncm.setNcm2(rst.getInt("ncm2"));
                    ncm.setNcm3(rst.getInt("ncm3"));
                    ncm.setDescricao(rst.getString("descricao"));

                    result.add(ncm);
                }
            }
        }

        return result;
    }
}
