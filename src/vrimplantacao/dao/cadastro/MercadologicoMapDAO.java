package vrimplantacao.dao.cadastro;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import vrframework.classe.Conexao;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.MercadologicoMapaVO;
import vrimplantacao.vo.vrimplantacao.MercadologicoVO;

/**
 *
 * @author Leandro
 */
public class MercadologicoMapDAO {
    
    private Map<String, MercadologicoMapaVO> existentes;

    private void carregarMercadologico() throws Exception {
        existentes = new LinkedHashMap<>();
        
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                "select id_sistema, id_loja, id_item, descricao, id_mercadologico from implantacao.mapademercadologico"
            )) {
                while (rst.next()) {
                    MercadologicoMapaVO mapa = new MercadologicoMapaVO(
                            rst.getString("id_sistema"), 
                            rst.getString("id_loja"), 
                            rst.getString("id_item"), 
                            rst.getString("descricao"), 
                            rst.getString("id_mercadologico") != null ? rst.getInt("id_mercadologico") : null);
                    existentes.put(mapa.getKey(), mapa);
                }
            }
        }
    }

    public Map<String, MercadologicoMapaVO> getExistentes() throws Exception {
        if (existentes == null) {
            carregarMercadologico();
        }
        return existentes;
    }
    

    private String getUpdateSQL(MercadologicoMapaVO mapa, boolean manterMapeamento) throws Exception {
        MercadologicoMapaVO existente = getExistentes().get(mapa.getKey());
        if (existente != null) {                
            String sql =
                "update implantacao.mapademercadologico set\n" +
                "  descricao = " + Utils.quoteSQL(mapa.getDescricao()) + "\n";
                if (!manterMapeamento) {
                    sql += "  ,id_mercadologico = " + (mapa.getMercadologicoVR() != null ?
                            Utils.longIntSQL(mapa.getMercadologicoVR(), 0) :
                            "null") + "\n";
                }
                sql += "where\n" +
                "  id_sistema = " + Utils.quoteSQL(existente.getSistemaId()) + " and\n" +
                "  id_loja = " + Utils.quoteSQL(existente.getLojaId()) + " and\n" +
                "  id_item = " + Utils.quoteSQL(existente.getMercadologicoId());
            return sql;
            
        } else {
            
            String sql =
                "insert into implantacao.mapademercadologico\n" +
                "(\n" +
                "  id_sistema,\n" +
                "  id_loja,\n" +
                "  id_item,\n" +
                "  descricao,\n" +
                "  id_mercadologico\n" +
                ") values (\n" +
                "  " + Utils.quoteSQL(mapa.getSistemaId()) + ",\n" +
                "  " + Utils.quoteSQL(mapa.getLojaId()) + ",\n" +
                "  " + Utils.quoteSQL(mapa.getMercadologicoId()) + ",\n" +
                "  " + Utils.quoteSQL(mapa.getDescricao()) + ",\n" +
                "  " + (mapa.getMercadologicoVR() != null ?
                    Utils.longIntSQL(mapa.getMercadologicoVR(), 0) :
                    "null") + "\n" +
                ")";
            //Util.exibirMensagem(sql, "");
            return sql;
        }
    }

    public void salvar(MercadologicoMapaVO mapa) throws Exception{
        Conexao.begin();
        try {
            Conexao.createStatement().executeUpdate(getUpdateSQL(mapa, false));
            existentes.put(mapa.getKey(), mapa);
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public List<MercadologicoMapaVO> mapearMercadologico(List<MercadologicoMapaVO> mercadologicoCliente) throws Exception {
        List<MercadologicoMapaVO> result = new ArrayList<>();
        for (MercadologicoMapaVO vo: mercadologicoCliente) {
            MercadologicoMapaVO aux = getExistentes().get(vo.getKey());            
            if (aux != null) {
                vo.setMercadologicoVR(aux.getMercadologicoVR());
            }
            result.add(vo);
        }
        return result;
    }

    public List<MercadologicoMapaVO> carregarMapa() throws Exception {
        List<MercadologicoMapaVO> result = new ArrayList<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                "select\n" +
                "	id_sistema,\n" +
                "	id_loja,\n" +
                "	id_item,\n" +
                "	descricao,\n" +
                "	id_mercadologico\n" +
                "from \n" +
                "	implantacao.mapademercadologico\n" +
                "order by\n" +
                "	id_sistema,\n" +
                "	id_loja,\n" +
                "	id_item"
            )) {
                while (rst.next()) {
                    MercadologicoMapaVO vo = new MercadologicoMapaVO();
                    vo.setSistemaId(rst.getString("id_sistema"));
                    vo.setLojaId(rst.getString("id_loja"));
                    vo.setMercadologicoId(rst.getString("id_item"));
                    vo.setDescricao(rst.getString("descricao"));
                    if (rst.getInt("id_mercadologico") != 0) {
                        vo.setMercadologicoVR(rst.getInt("id_mercadologico"));
                    }
                    result.add(vo);
                }
            }
        }
        return result;
    }

    public void salvar(List<MercadologicoMapaVO> vMapa, boolean manterMapeamento) throws Exception {
        Conexao.begin();
        try {
            Map<String, MercadologicoMapaVO> aux = new LinkedHashMap<>();
            for (MercadologicoMapaVO mapa: vMapa) {
                Conexao.createStatement().executeUpdate(getUpdateSQL(mapa, manterMapeamento));
                aux.put(mapa.getKey(), mapa);
            }            
            Conexao.commit();
            existentes.putAll(aux);
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public Map<String, MercadologicoVO> getMercadologicosMapeados() throws Exception {
        Map<String, MercadologicoVO> result = new LinkedHashMap<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	mp.id_sistema,\n" +
                    "	mp.id_loja,\n" +
                    "	mp.id_item,\n" +
                    "	mp.id_mercadologico,\n" +
                    "	m.mercadologico1,\n" +
                    "	m.mercadologico2,\n" +
                    "	m.mercadologico3,\n" +
                    "	m.mercadologico4,\n" +
                    "	m.mercadologico5,\n" +
                    "	m.descricao,\n" +
                    "	m.nivel\n" +
                    "from\n" +
                    "	implantacao.mapademercadologico mp\n" +
                    "	join mercadologico m on mp.id_mercadologico = m.id \n" +
                    "order by \n" +
                    "	id_sistema, \n" +
                    "	id_loja, \n" +
                    "	id_item"
            )) {
                while (rst.next()) {
                    MercadologicoVO merc = new MercadologicoVO();
                    merc.setId(rst.getInt("id_mercadologico"));
                    merc.setDescricao(rst.getString("descricao"));
                    merc.setMercadologico1(rst.getInt("mercadologico1"));
                    merc.setMercadologico2(rst.getInt("mercadologico2"));
                    merc.setMercadologico3(rst.getInt("mercadologico3"));
                    merc.setMercadologico4(rst.getInt("mercadologico4"));
                    merc.setMercadologico5(rst.getInt("mercadologico5"));
                    merc.setNivel(rst.getInt("nivel"));
                    result.put(rst.getString("id_sistema") + "-" + rst.getString("id_loja") + "-" + rst.getString("id_item"), merc);
                }
            }
        }        
        return result;
    }
    
}
