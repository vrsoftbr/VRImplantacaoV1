package vrimplantacao2.gui.component.mapatiporecebiveis;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.utils.sql.SQLUtils;
import vrimplantacao2.vo.cadastro.financeiro.TipoRecebivelVO;

/**
 * Classe que executa operações de banco na tabela de mapeamento de TipoRecebivel.
 * @author Leandro
 */
public class MapaTipoRecebivelDAO {

    /**
     * Executa um comando para criar a tabela de mapeamento.
     * @throws Exception 
     */
    public void createTable() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(
                    "create table if not exists implantacao.mapatiporecebivel(\n" +
                    "	sistema varchar not null,\n" +
                    "	agrupador varchar not null,\n" +
                    "	id varchar not null,\n" +
                    "	descricao varchar not null,\n" +
                    "	codigoatual integer,\n" +
                    "	primary key (sistema, agrupador, id)\n" +
                    ");"
            );
        }
    }

    public void gravarTipoRecebivelOrigem(List<MapaTipoRecebivelVO> convertido) throws Exception {
        try {
            Conexao.begin();

            try (Statement stm = Conexao.createStatement()) {
                for (MapaTipoRecebivelVO vo : convertido) {
                    SQLBuilder sql = new SQLBuilder();
                    sql.setSchema("implantacao");
                    sql.setTableName("mapatiporecebivel");
                    sql.put("sistema", vo.getSistema());
                    sql.put("agrupador", vo.getAgrupador());
                    sql.put("id", vo.getId());
                    sql.put("descricao", vo.getDescricao());
                    if (vo.getCodigoatual()!= null) {
                        sql.put("codigoatual", vo.getCodigoatual().getId());
                    }
                    try (ResultSet rst = stm.executeQuery(
                            "select\n"
                            + "	mp.sistema,\n"
                            + "	mp.agrupador,\n"
                            + "	mp.id\n"
                            + "from\n"
                            + "	implantacao.mapatiporecebivel mp\n"
                            + "where	\n"
                            + "	mp.sistema = " + SQLUtils.stringSQL(vo.getSistema()) + " and\n"
                            + "	mp.agrupador = " + SQLUtils.stringSQL(vo.getAgrupador()) + " and\n"
                            + "	mp.id = " + SQLUtils.stringSQL(vo.getId())
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

    public List<MapaTipoRecebivelVO> getMapa(String sistema, String agrupador) throws Exception {
        List<MapaTipoRecebivelVO> result = new ArrayList<>();
        
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n" +
                    "	ant.sistema,\n" +
                    "	ant.agrupador,\n" +
                    "	ant.id,\n" +
                    "	ant.descricao,\n" +
                    "	tr.id vrid,\n" +
                    "	tr.descricao vrdescricao,\n" +
                    "	tr.percentual vrpercentual\n" +
                    "from \n" +
                    "	implantacao.mapatiporecebivel ant\n" +
                    "	left join tiporecebivel tr on\n" +
                    "		ant.codigoatual = tr.id\n" +
                    "where\n" +
                    "	ant.sistema = " + SQLUtils.stringSQL(sistema) + " and\n" +
                    "	ant.agrupador = " + SQLUtils.stringSQL(agrupador) + "\n" +
                    "order by\n" +
                    "	ant.descricao"
            )) {
                while (rst.next()) {
                    MapaTipoRecebivelVO vo = new MapaTipoRecebivelVO();
                    vo.setSistema(sistema);
                    vo.setAgrupador(agrupador);
                    vo.setId(rst.getString("id"));
                    vo.setDescricao(rst.getString("descricao"));
                    if (rst.getString("vrid") != null) {
                        TipoRecebivelVO rc = new TipoRecebivelVO();
                        rc.setId(rst.getInt("vrid"));
                        rc.setDescricao(rst.getString("vrdescricao"));
                        rc.setPercentual(rst.getDouble("vrpercentual"));
                        vo.setCodigoatual(rc);
                    }
                    result.add(vo);
                }
            }
        }
        
        return result;
    }

    List<TipoRecebivelVO> getTipoRecebiveis(String text) throws Exception {
        List<TipoRecebivelVO> result = new ArrayList<>();
        
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	id,\n" +
                    "	descricao,\n" +
                    "	percentual\n" +
                    "from \n" +
                    "	tiporecebivel\n" +
                    "where\n" +
                    "	(descricao like upper('%" + text + "%') or\n" +
                    "	id::varchar = " + SQLUtils.stringSQL(text) + ") and\n" +
                    "	id_situacaocadastro = 1\n" +
                    "order by\n" +
                    "	descricao\n" +
                    "limit 10"
            )) {
                while (rst.next()) {
                    TipoRecebivelVO vo = new TipoRecebivelVO();
                    vo.setId(rst.getInt("id"));
                    vo.setDescricao(rst.getString("descricao"));
                    vo.setPercentual(rst.getDouble("percentual"));
                    result.add(vo);
                }
            }
        }
        
        return result;
    }

    void gravarMapa(MapaTipoRecebivelVO map) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            sql.setSchema("implantacao");
            sql.setTableName("mapatiporecebivel");
            if (map.getCodigoatual()!= null) {
                sql.put("codigoatual", map.getCodigoatual().getId());
            } else {
                sql.putNull("codigoatual");
            }
            sql.setWhere("sistema = " + SQLUtils.stringSQL(map.getSistema()) + " and\n"
                    + "agrupador = " + SQLUtils.stringSQL(map.getAgrupador()) + " and\n"
                    + "id = " + SQLUtils.stringSQL(map.getId()));
            stm.execute(sql.getUpdate());
        }
    }

    void gravarTipoRecebivel(TipoRecebivelVO tipoRecebivel) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            sql.setTableName("tiporecebivel");
            sql.putSql("id", "(select coalesce(max(id) + 1,1) from tiporecebivel)");
            sql.put("descricao", tipoRecebivel.getDescricao());
            sql.put("percentual", tipoRecebivel.getPercentual());
            sql.putNull("id_tipotef");
            sql.putNull("id_tipoticket");
            sql.put("gerarecebimento", false);
            sql.putNull("id_contacontabilfiscaldebito");
            sql.putNull("id_contacontabilfiscalcredito");
            sql.putNull("id_historicopadrao");
            sql.put("id_situacaocadastro", 0);
            sql.put("id_tipovistaprazo", 0);
            sql.putNull("id_tipocartaotef");
            sql.putNull("id_fornecedor");
            sql.put("tef", false);
            sql.putNull("id_tiporecebimento");
            sql.put("contabiliza", false);
            sql.put("id_contacontabilfinanceiro", 1);
            sql.getReturning().add("id");
            
            try (ResultSet rst = stm.executeQuery(
                    sql.getInsert()
            )) {
                while (rst.next()) {
                    tipoRecebivel.setId(rst.getInt("id"));
                }
            }
        }
    }
    
}
