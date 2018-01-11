package vrimplantacao2.gui.component.mapatributacao;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.utils.sql.SQLUtils;
import vrimplantacao2.vo.enums.Icms;

/**
 *
 * @author Leandro
 */
public class MapaTributacaoDAO {

    /**
     * Cria a tabela implantacao.mapatributacao se não existir.
     *
     * @throws Exception
     */
    public void createTable() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(
                    "create table if not exists implantacao.mapatributacao(\n"
                    + "	sistema varchar not null,\n"
                    + "	agrupador varchar not null,\n"
                    + "	orig_id varchar not null,\n"
                    + "	orig_descricao varchar not null,\n"
                    + "	id_aliquota int,\n"
                    + "	primary key (sistema, agrupador, orig_id)\n"
                    + ");"
            );
        }
    }

    public List<MapaTributoVO> getMapa(String sistema, String agrupador) throws Exception {
        List<MapaTributoVO> result = new ArrayList<>();

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	mp.sistema,\n"
                    + "	mp.agrupador,\n"
                    + "	mp.orig_id,\n"
                    + "	mp.orig_descricao,\n"
                    + "	mp.id_aliquota,\n"
                    + "	a.descricao,\n"
                    + "	a.situacaotributaria,\n"
                    + "	a.porcentagem,\n"
                    + "	a.reduzido\n"
                    + "from\n"
                    + "	implantacao.mapatributacao mp\n"
                    + "	left join aliquota a on\n"
                    + "		mp.id_aliquota = a.id\n"
                    + "where	\n"
                    + "	mp.sistema = " + SQLUtils.stringSQL(sistema) + " and\n"
                    + "	mp.agrupador = " + SQLUtils.stringSQL(agrupador)
            )) {
                while (rst.next()) {
                    MapaTributoVO vo = new MapaTributoVO();
                    vo.setSistema(rst.getString("sistema"));
                    vo.setAgrupador(rst.getString("agrupador"));
                    vo.setOrigId(rst.getString("orig_id"));
                    vo.setOrigDescricao(rst.getString("orig_descricao"));
                    if (rst.getString("id_aliquota") != null) {
                        vo.setAliquota(
                            new Icms(
                                rst.getInt("id_aliquota"),
                                rst.getString("descricao"),
                                rst.getInt("situacaotributaria"),
                                rst.getInt("porcentagem"),
                                rst.getInt("reduzido")
                            )
                        );
                    }
                    result.add(vo);
                }
            }
        }

        return result;
    }

    public void gravarTributacaoOrigem(List<MapaTributoVO> tributos) throws Exception {
        try {
            Conexao.begin();

            try (Statement stm = Conexao.createStatement()) {
                for (MapaTributoVO vo : tributos) {
                    SQLBuilder sql = new SQLBuilder();
                    sql.setSchema("implantacao");
                    sql.setTableName("mapatributacao");
                    sql.put("sistema", vo.getSistema());
                    sql.put("agrupador", vo.getAgrupador());
                    sql.put("orig_id", vo.getOrigId());
                    sql.put("orig_descricao", vo.getOrigDescricao());
                    if (vo.getAliquota() != null) {
                        sql.put("id_aliquota", vo.getAliquota().getId());
                    }
                    try (ResultSet rst = stm.executeQuery(
                            "select\n"
                            + "	mp.sistema,\n"
                            + "	mp.agrupador,\n"
                            + "	mp.orig_id\n"
                            + "from\n"
                            + "	implantacao.mapatributacao mp\n"
                            + "where	\n"
                            + "	mp.sistema = " + SQLUtils.stringSQL(vo.getSistema()) + " and\n"
                            + "	mp.agrupador = " + SQLUtils.stringSQL(vo.getAgrupador()) + " and\n"
                            + "	mp.orig_id = " + SQLUtils.stringSQL(vo.getOrigId())
                    )) {
                        if (!rst.next()) {
                            stm.execute(sql.getInsert());
                        }
                    }
                }
            }

            Conexao.commit();
        } catch (Exception e) {
            Conexao.rollback();
            throw e;
        }
    }

    public List<Icms> getTributacaoVR(String texto) throws Exception {
        List<Icms> result = new ArrayList<>();
        if (texto == null) {
            texto = "";
        }
        texto = texto.trim().toUpperCase();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	id,\n"
                    + "	descricao,\n"
                    + "	situacaotributaria,\n"
                    + "	porcentagem,\n"
                    + "	reduzido	\n"
                    + "from \n"
                    + "	aliquota\n"
                    + "where\n"
                    + "	id_situacaocadastro = 1 and\n"
                    + "	(id::varchar = " + SQLUtils.stringSQL(texto) + " or \n"
                    + "	descricao like " + SQLUtils.stringSQL("%" + texto + "%") + ")\n"
                    + "order by\n"
                    + "	descricao\n"
                    + "limit 10"
            )) {
                while (rst.next()) {
                    Icms vo = new Icms(
                            rst.getInt("id"),
                            rst.getString("descricao"),
                            rst.getInt("situacaotributaria"),
                            rst.getDouble("porcentagem"),
                            rst.getDouble("reduzido"));
                    result.add(vo);
                }
            }
        }

        return result;
    }

    public void gravarMapa(MapaTributoVO mapa) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            sql.setSchema("implantacao");
            sql.setTableName("mapatributacao");
            if (mapa.getAliquota() != null) {
                sql.put("id_aliquota", mapa.getAliquota().getId());
            } else {
                sql.putNull("id_aliquota");
            }
            sql.setWhere("sistema = " + SQLUtils.stringSQL(mapa.getSistema()) + " and\n"
                    + "agrupador = " + SQLUtils.stringSQL(mapa.getAgrupador()) + " and\n"
                    + "orig_id = " + SQLUtils.stringSQL(mapa.getOrigId()));
            stm.execute(sql.getUpdate());
        }
    }

}
