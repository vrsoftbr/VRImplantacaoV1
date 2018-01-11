package vrimplantacao.dao.cadastro;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import vrframework.classe.Conexao;
import vrframework.classe.Util;
import vrimplantacao.vo.vrimplantacao.MercadologicoAnteriorVO;
import vrimplantacao.vo.vrimplantacao.MercadologicoVO;

/**
 *
 * @author Leandro
 */
public class MercadologicoAnteriorDAO {
    
    private MercadologicoVO aAcertar = null;

    public void salvar(List<MercadologicoVO> mercadologicos, boolean limpar) throws Exception {
        Map<String, MercadologicoAnteriorVO> map = new LinkedHashMap<>();
        
        Conexao.begin();
        
        try {        
            try (Statement stm = Conexao.createStatement()) {       

                if (limpar) {
                    stm.executeUpdate("delete from implantacao.codigoanteriormercadologico");
                }
                
                Map<String, MercadologicoAnteriorVO> filter = getMercadologicoAnterior();

                for (MercadologicoVO merc: mercadologicos) {
                    MercadologicoAnteriorVO vo = new MercadologicoAnteriorVO(
                        merc.getMercadologico1(),
                        merc.getMercadologico2(),
                        merc.getMercadologico3(),
                        merc.getMercadologico4(),
                        merc.getMercadologico5(),
                        merc.getDescricao(),
                        merc.getNivel()
                    );
                    
                    if (!filter.containsKey(vo.toString())) { 
                        
                        filter.put(vo.toString(), vo);                     

                        stm.executeUpdate(
                            "insert into implantacao.codigoanteriormercadologico (\n" +
                            "	ant1,\n" +
                            "	ant2,\n" +
                            "	ant3,\n" +
                            "	ant4,\n" +
                            "	ant5,\n" +
                            "	merc1,\n" +
                            "	merc2,\n" +
                            "	merc3,\n" +
                            "	merc4,\n" +
                            "	merc5,\n" +
                            "	descricao,\n" +
                            "	nivel\n" +
                            ") values (\n" +
                            "	" + vo.mercAnterior1 + ",\n" +
                            "	" + vo.mercAnterior2 + ",\n" +
                            "	" + vo.mercAnterior3 + ",\n" +
                            "	" + vo.mercAnterior4 + ",\n" +
                            "	" + vo.mercAnterior5 + ",\n" +
                            "	" + vo.mercAtual1 + ",\n" +
                            "	" + vo.mercAtual2 + ",\n" +
                            "	" + vo.mercAtual3 + ",\n" +
                            "	" + vo.mercAtual4 + ",\n" +
                            "	" + vo.mercAtual5 + ",\n" +
                            "	'" + vo.descricao + "',\n" +
                            "	" + vo.nivel + "\n" +
                            ")"
                        );                       
                    }
                }
            }          
            
            reorganizaMercadologico();
            
            Conexao.commit();        
        } catch(Exception e) {
            Conexao.rollback();
            throw e;
        }
    }
    
    private boolean mercadologicoImportado = false;
    private boolean mercadologicoVerificado = false;

    private void verificarMercadologico() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery("select count(*) cont from implantacao.codigoanteriormercadologico")) {
                if (rst.next()) {
                    mercadologicoImportado = rst.getInt("cont") > 0;
                } else {
                    mercadologicoImportado = false;
                }
                mercadologicoVerificado = true;
            }
        }
    }
    
    public MercadologicoVO makeMercadologico(int mercadologico1, int mercadologico2, int mercadologico3, int mercadologico4, int mercadologico5) throws Exception {
        
        try (Statement stm = Conexao.createStatement()) {
            
            MercadologicoAnteriorVO ant = new MercadologicoAnteriorVO(mercadologico1, mercadologico2, mercadologico3, mercadologico4, mercadologico5, "", -1);
            
            if (!mercadologicoVerificado) {
                verificarMercadologico();
            }
            
            if (mercadologicoImportado) {
            
                try (ResultSet rst = stm.executeQuery(
                        "select\n" +
                        "	merc1,\n" +
                        "	merc2,\n" +
                        "	merc3,\n" +
                        "	merc4,\n" +
                        "	merc5,\n" +
                        "	descricao,\n" +
                        "	nivel\n" +
                        "from\n" +
                        "	implantacao.codigoanteriormercadologico\n" +
                        "where\n" +
                        "	ant1 = " + ant.mercAnterior1+ " and\n" +
                        "	ant2 = " + ant.mercAnterior2+ " and\n" +
                        "	ant3 = " + ant.mercAnterior3+ " and\n" +
                        "	ant4 = " + ant.mercAnterior4+ " and\n" +
                        "	ant5 = " + ant.mercAnterior5)
                ) {
                    if (rst.next()) {
                        
                        MercadologicoVO vo = new MercadologicoVO();
                        vo.mercadologico1 = rst.getInt("merc1");
                        vo.mercadologico2 = rst.getInt("merc2");
                        vo.mercadologico3 = rst.getInt("merc3");
                        vo.mercadologico4 = rst.getInt("merc4");
                        vo.mercadologico5 = rst.getInt("merc5");
                        vo.descricao = rst.getString("descricao");
                        vo.nivel = rst.getInt("nivel");

                        return vo;
                    } else {
                        return MercadologicoDAO.getMaxMercadologico();
                       //throw new Exception("Erro ao importar Mercadológio: " + mercadologico1 + "," + mercadologico2 + "," + mercadologico3 + "," + mercadologico4 + "," + mercadologico5);
                    }
                }            
            } else {
                if (aAcertar == null) {
                    aAcertar = MercadologicoDAO.getMaxMercadologico();
                }
                MercadologicoVO vo = new MercadologicoVO();
                vo.mercadologico1 = aAcertar.mercadologico1;
                vo.mercadologico2 = aAcertar.mercadologico2;
                vo.mercadologico3 = aAcertar.mercadologico3;
                vo.mercadologico4 = aAcertar.mercadologico4;
                vo.mercadologico5 = aAcertar.mercadologico5;
                vo.descricao = aAcertar.descricao;
                vo.nivel = aAcertar.nivel;
                return vo;
            }
        }        
    }
    
    /**
     * Retorna a listagem do mercadologico anterior.
     * @return
     * @throws Exception 
     */
    public Map<String, MercadologicoAnteriorVO> getMercadologicoAnterior() throws Exception{
        Map<String, MercadologicoAnteriorVO> result = new LinkedHashMap<>();
        try (Statement stm = Conexao.createStatement()) {            
            //Ajusta mercadologico 1
            try (ResultSet rst = stm.executeQuery(
                    "select ant1,ant2,ant3,ant4,ant5,merc1,merc2,merc3,merc4,merc5,descricao,nivel from implantacao.codigoanteriormercadologico order by ant1,ant2,ant3,ant4,ant5"
            )) {
                while (rst.next()) {
                    MercadologicoAnteriorVO ant = new MercadologicoAnteriorVO(
                        rst.getInt("ant1"), 
                        rst.getInt("ant2"), 
                        rst.getInt("ant3"), 
                        rst.getInt("ant4"), 
                        rst.getInt("ant5"), 
                        rst.getString("descricao"), 
                        rst.getInt("nivel")
                    );
                    result.put(ant.toString(), ant);
                }
            }            
        }
        return result;
    }
    
    /**
     * Acione este método para gerar o mercadológico novamente.
     * @throws Exception 
     */
    public void reorganizaMercadologico() throws Exception{
        Map<String, MercadologicoAnteriorVO> anteriores = getMercadologicoAnterior();
        
        int merc1 = 0,merc2 = 0,merc3 = 0,merc4 = 0,merc5 = 0;
        int mercAnt1 = 0,mercAnt2 = 0,mercAnt3 = 0,mercAnt4 = 0,mercAnt5 = 0;
        
        for (MercadologicoAnteriorVO ant: anteriores.values()) {
            if (ant.mercAnterior1 != mercAnt1) {
                merc1++;
                merc2 = 0;
                merc3 = 0;
                merc4 = 0;
                merc5 = 0;
            } 
            if (ant.mercAnterior2 != mercAnt2 && ant.mercAnterior2 != 0) {
                merc2++;
                merc3 = 0;
                merc4 = 0;
                merc5 = 0;
            } 
            if (ant.mercAnterior3 != mercAnt3 && ant.mercAnterior3 != 0) {
                merc3++;
                merc4 = 0;
                merc5 = 0;
            } 
            if (ant.mercAnterior4 != mercAnt4 && ant.mercAnterior4 != 0) {
                merc4++;
                merc5 = 0;
            } 
            if (ant.mercAnterior5 != mercAnt5 && ant.mercAnterior5 != 0) {
                merc5++;
            }            
            
            ant.mercAtual1 = merc1;
            ant.mercAtual2 = merc2;
            ant.mercAtual3 = merc3;
            ant.mercAtual4 = merc4;
            ant.mercAtual5 = merc5;
            
            mercAnt1 = ant.mercAnterior1;
            mercAnt2 = ant.mercAnterior2;
            mercAnt3 = ant.mercAnterior3;
            mercAnt4 = ant.mercAnterior4;
            mercAnt5 = ant.mercAnterior5;
        }      

        Conexao.begin();
        try {
            try (Statement stm = Conexao.createStatement()) {            
                for (MercadologicoAnteriorVO vo: anteriores.values()) {
                    stm.executeUpdate(
                        "update implantacao.codigoanteriormercadologico set\n"
                            + "merc1 = " + vo.mercAtual1 + ",\n"
                            + "merc2 = " + vo.mercAtual2 + ",\n"
                            + "merc3 = " + vo.mercAtual3 + ",\n"
                            + "merc4 = " + vo.mercAtual4 + ",\n"
                            + "merc5 = " + vo.mercAtual5 + "\n"
                        + "where\n"
                            + "ant1 = " + vo.mercAnterior1 + " and\n"
                            + "ant2 = " + vo.mercAnterior2 + " and\n"
                            + "ant3 = " + vo.mercAnterior3 + " and\n"
                            + "ant4 = " + vo.mercAnterior4 + " and\n"
                            + "ant5 = " + vo.mercAnterior5 + ""
                    );
                }
            }
            Conexao.commit();
        } catch(Exception e) {
            Conexao.rollback();
            throw  e;
        }
    }

    public MercadologicoVO makeMercadologicoIntegracao(int mercadologico1, int mercadologico2, int mercadologico3, int mercadologico4, int mercadologico5) throws Exception {
        
        try (Statement stm = Conexao.createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select mercadologico1 merc1, "
                                + "mercadologico2 merc2, "
                                + "mercadologico3 merc3, "
                                + "mercadologico4 merc4, "
                                + "mercadologico5 merc5, "
                                + "descricao, "
                                + "nivel from mercadologico "
                                + "where descricao like '%ACERTAR%' "
                                + "and nivel = 3")
                ) {
                    if (rst.next()) {
                        
                        MercadologicoVO vo = new MercadologicoVO();
                        vo.mercadologico1 = rst.getInt("merc1");
                        vo.mercadologico2 = rst.getInt("merc2");
                        vo.mercadologico3 = rst.getInt("merc3");
                        vo.mercadologico4 = rst.getInt("merc4");
                        vo.mercadologico5 = rst.getInt("merc5");
                        vo.descricao = rst.getString("descricao");
                        vo.nivel = rst.getInt("nivel");

                        return vo;
                    } else {
                        return MercadologicoDAO.getMaxMercadologico();
                       //throw new Exception("Erro ao importar Mercadológio: " + mercadologico1 + "," + mercadologico2 + "," + mercadologico3 + "," + mercadologico4 + "," + mercadologico5);
                    }
                }            
        }        
    }
}
