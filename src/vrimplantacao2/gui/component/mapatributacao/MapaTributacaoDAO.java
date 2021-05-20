package vrimplantacao2.gui.component.mapatributacao;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.utils.sql.SQLUtils;
import vrimplantacao2.vo.enums.Icms;
import vrimplantacao2.vo.importacao.MapaTributoIMP;

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
                    + "	orig_cst integer,\n"
                    + "	orig_aliquota numeric(11,2),\n"
                    + "	orig_reduzido numeric(13,3),\n"
                    + "	orig_fcp numeric(13,3),\n"
                    + "	orig_desonerado boolean,\n"
                    + "	orig_porcentagemdesonerado numeric(13,3),\n"
                    + "	primary key (sistema, agrupador, orig_id)\n"
                    + ");"
            );
        }
    }

    public Map<String, MapaTributoVO> getMapaAsMap(String sistema, String agrupador) throws Exception {
        Map<String, MapaTributoVO> result = new HashMap<>();
        
        for (MapaTributoVO t: getMapa(sistema, agrupador)) {
            result.put(t.getOrigId(), t);
        }
        
        return result;
    }
    
    @Deprecated
    public List<MapaTributoVO> getMapa(String sistema, String agrupador) throws Exception {
        List<MapaTributoVO> result = new ArrayList<>();

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	mp.sistema,\n"
                    + "	mp.agrupador,\n"
                    + "	mp.orig_id,\n"
                    + "	mp.orig_descricao,\n"
                    + "	mp.orig_cst,\n"
                    + "	mp.orig_aliquota,\n"
                    + "	mp.orig_reduzido,\n"
                    + "	mp.orig_fcp,\n"
                    + "	mp.orig_desonerado,\n"
                    + "	mp.orig_porcentagemdesonerado,\n"
                    + "	mp.id_aliquota,\n"
                    + "	a.descricao,\n"
                    + "	a.situacaotributaria,\n"
                    + "	a.porcentagem,\n"
                    + "	a.reduzido,\n"
                    + "	a.porcentagemfcp,\n"
                    + "	a.icmsdesonerado,\n"
                    + "	a.percentualicmsdesonerado\n"
                    + "from\n"
                    + "	implantacao.mapatributacao mp\n"
                    + "	left join aliquota a on\n"
                    + "		mp.id_aliquota = a.id\n"
                    + "where	\n"
                    + "	mp.sistema = " + SQLUtils.stringSQL(sistema) + " and\n"
                    + "	mp.agrupador = " + SQLUtils.stringSQL(agrupador) + "\n"
                    + "order by mp.id_aliquota desc, mp.orig_id"
            )) {
                while (rst.next()) {
                    MapaTributoVO vo = new MapaTributoVO();
                    vo.setSistema(rst.getString("sistema"));
                    vo.setAgrupador(rst.getString("agrupador"));
                    vo.setOrigId(rst.getString("orig_id"));
                    vo.setOrigDescricao(rst.getString("orig_descricao"));
                    vo.setOrigCst(rst.getInt("orig_cst"));
                    vo.setOrigAliquota(rst.getDouble("orig_aliquota"));
                    vo.setOrigReduzido(rst.getDouble("orig_reduzido"));
                    vo.setOrigFcp(rst.getDouble("orig_fcp"));
                    vo.setOrigDesonerado(rst.getBoolean("orig_desonerado"));
                    vo.setOrigPorcentagemDesonerado(rst.getDouble("orig_porcentagemdesonerado"));
                    if (rst.getString("id_aliquota") != null) {
                        vo.setAliquota(
                            new Icms(
                                rst.getInt("id_aliquota"),
                                rst.getString("descricao"),
                                rst.getInt("situacaotributaria"),
                                rst.getDouble("porcentagem"),
                                rst.getDouble("reduzido"),
                                rst.getDouble("porcentagemfcp"),
                                rst.getBoolean("icmsdesonerado"),
                                rst.getDouble("percentualicmsdesonerado")
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
        MapaTributoVO maVO = null;
        try {
            Conexao.begin();

            try (Statement stm = Conexao.createStatement()) {
                for (MapaTributoVO vo : tributos) {
                    maVO = vo;
                    SQLBuilder sql = new SQLBuilder();
                    sql.setSchema("implantacao");
                    sql.setTableName("mapatributacao");
                    sql.put("sistema", vo.getSistema());
                    sql.put("agrupador", vo.getAgrupador());
                    sql.put("orig_id", vo.getOrigId());
                    sql.put("orig_descricao", vo.getOrigDescricao());
                    sql.put("orig_cst", vo.getOrigCst());
                    sql.put("orig_aliquota", vo.getOrigAliquota());
                    sql.put("orig_reduzido", vo.getOrigReduzido());
                    sql.put("orig_fcp", vo.getOrigFcp());
                    sql.put("orig_desonerado", vo.isOrigDesonerado());
                    sql.put("orig_porcentagemdesonerado", vo.getOrigPorcentagemDesonerado());
                    if (vo.getAliquota() != null) {
                        sql.put("id_aliquota", vo.getAliquota().getId());
                    }
                    try (ResultSet rst = stm.executeQuery(
                            "select"
                            + "	mp.sistema,\n"
                            + "	mp.agrupador,\n"
                            + "	mp.orig_id\n"
                            + "from\n"
                            + "	implantacao.mapatributacao mp\n"
                            + "where\n"
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
            System.out.println("select"
                            + "	mp.sistema,\n"
                            + "	mp.agrupador,\n"
                            + "	mp.orig_id\n"
                            + "from\n"
                            + "	implantacao.mapatributacao mp\n"
                            + "where\n"
                            + "	mp.sistema = " + SQLUtils.stringSQL(maVO.getSistema()) + " and\n"
                            + "	mp.agrupador = " + SQLUtils.stringSQL(maVO.getAgrupador()) + " and\n"
                            + "	mp.orig_id = " + SQLUtils.stringSQL(maVO.getOrigId()));
            Conexao.rollback();
            throw e;
        }
    }
    
    /**
     * Retorna todas as tributações ativas no VR.
     * @return
     * @throws Exception 
     */
    public List<Icms> getTributacaoVR() throws Exception {
        List<Icms> result = new ArrayList<>();

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	id,\n" +
                    "	descricao,\n" +
                    "	situacaotributaria,\n" +
                    "	porcentagem,\n" +
                    "	case\n" +
                    "		when situacaotributaria in (20, 70) then reduzido\n" +
                    "		else 0\n" +
                    "	end reduzido\n" +
                    "from \n" +
                    "	aliquota\n" +
                    "where\n" +
                    "	id_situacaocadastro = 1\n" +
                    "order by\n" +
                    "	descricao"
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

    public List<Icms> getTributacaoVR(String texto) throws Exception {
        List<Icms> result = new ArrayList<>();
        if (texto == null) {
            texto = "";
        }
        texto = texto.trim().toUpperCase();
        boolean somenteId = texto.startsWith("@");
        texto = texto.substring(1);
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	id,\n"
                    + "	descricao,\n"
                    + "	situacaotributaria,\n"
                    + "	porcentagem,\n"
                    + "	reduzido,\n"
                    + "	porcentagemfcp,\n"
                    + "	icmsdesonerado,\n"
                    + "	percentualicmsdesonerado\n"
                    + "from \n"
                    + "	aliquota\n"
                    + "where\n"
                    + "	id_situacaocadastro = 1 and\n"
                    + "	(id::varchar = " + SQLUtils.stringSQL(texto) + "\n"
                    + (somenteId ? " " : "	or descricao like " + SQLUtils.stringSQL("%" + texto + "%") ) + ")\n"
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
                            rst.getDouble("reduzido"),
                            rst.getDouble("porcentagemfcp"),
                            rst.getBoolean("icmsdesonerado"),
                            rst.getDouble("percentualicmsdesonerado")
                    );
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
    
    public Map<String, Integer> getAliquotaPorId(String sistema, String loja) throws Exception {
        Map<String, Integer> result = new HashMap<>();
        
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	orig_id id,\n" +
                    "	id_aliquota\n" +
                    "from\n" +
                    "	implantacao.mapatributacao\n" +
                    "where\n" +
                    "	sistema = '" + sistema + "' and\n" +
                    "	agrupador = '" + loja + "'"
            )) {
                while (rst.next()) {
                    result.put(rst.getString("id"), rst.getInt("id_aliquota"));
                }
            }
        }
        
        return result;
    }

    public void vincularAliquotas(String sistema, String agrupador) throws Exception {
        try (Statement st = Conexao.createStatement()) {
            st.execute(
                    "with aliq as (\n" +
                    "	select\n" +
                    "		id,\n" +
                    "		descricao,\n" +
                    "		id_situacaocadastro,\n" +
                    "		situacaotributaria,\n" +
                    "		porcentagem,\n" +
                    "		case when reduzido = 100 then 0 else reduzido end reduzido,\n" +
                    "		icmsdesonerado,\n" +
                    "		porcentagemfcp,\n" +
                    "		percentualicmsdesonerado\n" +
                    "	from\n" +
                    "		aliquota\n" +
                    "),\n" +
                    "al2 as (\n" +
                    "	select\n" +
                    "		m.*,\n" +
                    "		(\n" +
                    "			select id \n" +
                    "			from aliq where\n" +
                    "			aliq.id_situacaocadastro = 1 and\n" +
                    "			aliq.situacaotributaria = \n" +
                    "				(case\n" +
                    "					when m.orig_cst in (10,30,70) then 60\n" +
                    "					else m.orig_cst\n" +
                    "				end) and\n" +
                    "			coalesce(aliq.porcentagem,0) =\n" +
                    "				(case\n" +
                    "					when m.orig_cst in (10,30,40,60,70) then 0\n" +
                    "					else m.orig_aliquota\n" +
                    "				end) and\n" +
                    "			coalesce(aliq.reduzido,0) = \n" +
                    "				(case\n" +
                    "					when m.orig_cst != 20 then 0\n" +
                    "					else m.orig_reduzido\n" +
                    "				end) and\n" +
                    "			coalesce(aliq.porcentagemfcp,0) = m.orig_fcp and\n" +
                    "			coalesce(aliq.icmsdesonerado,false) = m.orig_desonerado and\n" +
                    "			coalesce(aliq.percentualicmsdesonerado,0) = m.orig_porcentagemdesonerado\n" +
                    "			limit 1\n" +
                    "		)\n" +
                    "	from\n" +
                    "		implantacao.mapatributacao m\n" +
                    ")\n" +
                    "update implantacao.mapatributacao a set\n" +
                    "	id_aliquota = b.id\n" +
                    "from\n" +
                    "	al2 b\n" +
                    "where\n" +
                    "	a.sistema = b.sistema and\n" +
                    "	a.agrupador = b.agrupador and\n" +
                    "	a.orig_id = b.orig_id and\n" +
                    "	a.sistema = '" + sistema + "' and\n" +
                    "	a.agrupador = '" + agrupador + "' and\n" +
                    "	a.id_aliquota is null and \n" +
                    "	not b.id is null\n" +
                    "		"
            );
        }
    }

}
