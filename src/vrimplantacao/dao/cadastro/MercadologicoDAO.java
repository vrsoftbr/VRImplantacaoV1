package vrimplantacao.dao.cadastro;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao.dao.CodigoInternoDAO;
import vrimplantacao.vo.vrimplantacao.MercadologicoVO;

/**
 * @author lucasrafael
 */
public class MercadologicoDAO {

    public MercadologicoVO getMercadologicoByMercad2Mercad3(int mercad2, int mercad3) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select mercadologico1, mercadologico2, mercadologico3, "
                            + "mercadologico4, mercadologico5 "
                            + "from mercadologico "
                            + "where nivel = 3 "
                            + "and mercadologico2 = "+mercad2+" "
                            + "and mercadologico3 = "+mercad3
            )) {
                if (rst.next()) {
                    MercadologicoVO vo = new MercadologicoVO();
                    vo.setMercadologico1(rst.getInt("mercadologico1"));
                    vo.setMercadologico2(rst.getInt("mercadologico2"));
                    vo.setMercadologico3(rst.getInt("mercadologico3"));
                    vo.setMercadologico4(rst.getInt("mercadologico4"));
                    vo.setMercadologico5(rst.getInt("mercadologico5"));
                    return vo;
                } else {
                    MercadologicoVO vo = new MercadologicoVO();
                    vo.setMercadologico1(0);
                    vo.setMercadologico2(0);
                    vo.setMercadologico3(0);
                    vo.setMercadologico4(0);
                    vo.setMercadologico5(0);
                    return vo;
                }
            }
        }
    }
    
    public MercadologicoVO getMercadologicoByDescricao(String i_descricao) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select mercadologico1, mercadologico2, mercadologico3, "
                            + "mercadologico4, mercadologico5 "
                            + "from mercadologico "
                            + "where nivel = 3 "
                            + "and descricao like '"+i_descricao+"'"
            )) {
                if (rst.next()) {
                    MercadologicoVO vo = new MercadologicoVO();
                    vo.setMercadologico1(rst.getInt("mercadologico1"));
                    vo.setMercadologico2(rst.getInt("mercadologico2"));
                    vo.setMercadologico3(rst.getInt("mercadologico3"));
                    vo.setMercadologico4(rst.getInt("mercadologico4"));
                    vo.setMercadologico5(rst.getInt("mercadologico5"));
                    return vo;
                } else {
                    MercadologicoVO vo = new MercadologicoVO();
                    vo.setMercadologico1(0);
                    vo.setMercadologico2(0);
                    vo.setMercadologico3(0);
                    vo.setMercadologico4(0);
                    vo.setMercadologico5(0);
                    return vo;
                }
            }            
        }        
    }
    
    public static MercadologicoVO getMaxMercadologico() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select * from mercadologico where mercadologico1 = "
                    + "(select max(mercadologico1) from mercadologico) "
                    + "and mercadologico2 = 1 and mercadologico3 = 1"
            )) {
                if (rst.next()) {
                    MercadologicoVO vo = new MercadologicoVO();
                    vo.setMercadologico1(rst.getInt("mercadologico1"));
                    vo.setMercadologico2(rst.getInt("mercadologico2"));
                    vo.setMercadologico3(rst.getInt("mercadologico3"));
                    vo.setMercadologico4(rst.getInt("mercadologico4"));
                    vo.setMercadologico5(rst.getInt("mercadologico5"));
                    vo.setNivel(rst.getInt("nivel"));
                    vo.setDescricao(rst.getString("descricao"));
                    return vo;
                } else {
                    throw new Exception("A tabela de mercadológico esta vazia");
                }
            }
        }
    }

    public static MercadologicoVO getMercadologicoAAcertar() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select * from mercadologico where descricao like '%ACERTAR%' and nivel = 3 "
            )) {
                if (rst.next()) {
                    MercadologicoVO vo = new MercadologicoVO();
                    vo.setMercadologico1(rst.getInt("mercadologico1"));
                    vo.setMercadologico2(rst.getInt("mercadologico2"));
                    vo.setMercadologico3(rst.getInt("mercadologico3"));
                    vo.setMercadologico4(rst.getInt("mercadologico4"));
                    vo.setMercadologico5(rst.getInt("mercadologico5"));
                    vo.setNivel(rst.getInt("nivel"));
                    vo.setDescricao(rst.getString("descricao"));
                    return vo;
                } else {
                    throw new Exception("A tabela de mercadológico esta vazia");
                }
            }
        }
    }

    public boolean temNivel4 = false;
    public boolean temNivel5 = false;

    /**
     * Gera uma listagem com todos os mercadológicos cadastrados no sistema.
     *
     * @return Listagem dos mercadológicos cadastrados.
     * @throws Exception
     */
    public Map<String, MercadologicoVO> carregarMercadologicos() throws Exception {
        Map<String, MercadologicoVO> mercadologicos = new LinkedHashMap<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	id, \n"
                    + "	mercadologico1, \n"
                    + "	mercadologico2, \n"
                    + "	mercadologico3, \n"
                    + "	mercadologico4, \n"
                    + "	mercadologico5, \n"
                    + "	nivel, \n"
                    + "	descricao \n"
                    + "from mercadologico order by \n"
                    + "	mercadologico1, \n"
                    + "	mercadologico2, \n"
                    + "	mercadologico3, \n"
                    + "	mercadologico4, \n"
                    + "	mercadologico5"
            )) {
                while (rst.next()) {
                    MercadologicoVO vo = new MercadologicoVO();

                    vo.setId(rst.getInt("id"));
                    vo.setMercadologico1(rst.getInt("mercadologico1"));
                    vo.setMercadologico2(rst.getInt("mercadologico2"));
                    vo.setMercadologico3(rst.getInt("mercadologico3"));
                    vo.setMercadologico4(rst.getInt("mercadologico4"));
                    vo.setMercadologico5(rst.getInt("mercadologico5"));
                    vo.setNivel(rst.getInt("nivel"));
                    vo.setDescricao(rst.getString("descricao"));

                    mercadologicos.put(vo.getChaveUnica(), vo);
                }
            }
        }
        return mercadologicos;
    }

    public MercadologicoVO carregar() throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;
        MercadologicoVO oMercadologico = null;

        try {
            stm = Conexao.createStatement();

            sql = new StringBuilder();

            sql.append("SELECT * FROM mercadologico WHERE UPPER(descricao)=UPPER('A ACERTAR') ORDER BY nivel DESC LIMIT 1");

            rst = stm.executeQuery(sql.toString());

            if (rst.next()) {
                oMercadologico = new MercadologicoVO();

                oMercadologico.descricao = rst.getString("descricao");
                oMercadologico.mercadologico1 = rst.getInt("mercadologico1");
                oMercadologico.mercadologico2 = rst.getInt("mercadologico2");
                oMercadologico.mercadologico3 = rst.getInt("mercadologico3");
                oMercadologico.mercadologico4 = rst.getInt("mercadologico4");
                oMercadologico.mercadologico5 = rst.getInt("mercadologico5");
                oMercadologico.nivel = rst.getInt("nivel");

            }

            if (oMercadologico == null) {

                sql = new StringBuilder();

                sql.append("SELECT mercadologico1 from (SELECT id FROM generate_series(1, (SELECT COALESCE(MAX(mercadologico1), 0) + 1 FROM  mercadologico)) AS s(id) EXCEPT SELECT mercadologico1 FROM mercadologico) AS codigointerno ORDER BY mercadologico1 LIMIT 1");
                rst = stm.executeQuery(sql.toString());
                rst.next();

                int mercadologico1 = rst.getInt("mercadologico1");

                sql = new StringBuilder();

                sql.append("INSERT INTO mercadologico (descricao, mercadologico1, mercadologico2, mercadologico3, mercadologico4, mercadologico5, nivel)");
                sql.append(" VALUES(");
                sql.append(" 'A ACERTAR',");
                sql.append(" " + mercadologico1 + ",");
                sql.append(" 0,");
                sql.append(" 0,");
                sql.append(" 0,");
                sql.append(" 0,");
                sql.append(" 1");
                sql.append(" )");

                stm.execute(sql.toString());

                sql.append("INSERT INTO mercadologico (descricao, mercadologico1, mercadologico2, mercadologico3, mercadologico4, mercadologico5, nivel)");
                sql.append(" VALUES(");
                sql.append(" 'A ACERTAR',");
                sql.append(" " + mercadologico1 + ",");
                sql.append(" 1,");
                sql.append(" 0,");
                sql.append(" 0,");
                sql.append(" 0,");
                sql.append(" 2");
                sql.append(" )");

                stm.execute(sql.toString());

                sql.append("INSERT INTO mercadologico (descricao, mercadologico1, mercadologico2, mercadologico3, mercadologico4, mercadologico5, nivel)");
                sql.append(" VALUES(");
                sql.append(" 'A ACERTAR',");
                sql.append(" " + mercadologico1 + ",");
                sql.append(" 1,");
                sql.append(" 1,");
                sql.append(" 0,");
                sql.append(" 0,");
                sql.append(" 3");
                sql.append(" )");

                stm.execute(sql.toString());

                sql.append("INSERT INTO mercadologico (descricao, mercadologico1, mercadologico2, mercadologico3, mercadologico4, mercadologico5, nivel)");
                sql.append(" VALUES(");
                sql.append(" 'A ACERTAR',");
                sql.append(" " + mercadologico1 + ",");
                sql.append(" 1,");
                sql.append(" 1,");
                sql.append(" 1,");
                sql.append(" 0,");
                sql.append(" 4");
                sql.append(" )");

                stm.execute(sql.toString());

                sql.append("INSERT INTO mercadologico (descricao, mercadologico1, mercadologico2, mercadologico3, mercadologico4, mercadologico5, nivel)");
                sql.append(" VALUES(");
                sql.append(" 'A ACERTAR',");
                sql.append(" " + mercadologico1 + ",");
                sql.append(" 1,");
                sql.append(" 1,");
                sql.append(" 1,");
                sql.append(" 1,");
                sql.append(" 5");
                sql.append(" )");

                stm.execute(sql.toString());

                oMercadologico = new MercadologicoVO();

                oMercadologico.descricao = "A ACERTAR";
                oMercadologico.mercadologico1 = mercadologico1;
                oMercadologico.mercadologico2 = 1;
                oMercadologico.mercadologico3 = 1;
                oMercadologico.mercadologico4 = 1;
                oMercadologico.mercadologico5 = 1;
                oMercadologico.nivel = 5;

            }

            return oMercadologico;

        } catch (Exception e) {
            Conexao.rollback();
        } finally {
            Conexao.destruir(null, stm, rst);
        }

        return null;
    }

    public static boolean existeMercadologico(int mercadologico1, int mercadologico2, int mercadologico3,
            int mercadologico4, int mercadologico5) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select * from mercadologico "
                    + "where mercadologico1 = " + mercadologico1 + " "
                    + "and mercadologico2 = " + mercadologico2 + " "
                    + "and mercadologico3 = " + mercadologico3 + " "
                    + "and mercadologico4 = " + mercadologico4 + " "
                    + "and mercadologico5 = " + mercadologico5 + ";"
            )) {
                return rst.next();
            }
        }
    }
    
    public void salvar(List<MercadologicoVO> v_mercadologico, boolean limpar) throws Exception {
        salvar(v_mercadologico, limpar, true);
    }

    public void salvar(List<MercadologicoVO> v_mercadologico, boolean limpar, boolean UsaMercadologicoAnterior) throws Exception {

        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        MercadologicoAnteriorDAO anteriorDAO = new MercadologicoAnteriorDAO();

        try {

            Conexao.begin();

            //Gravo o mercadologico anterior
            if (UsaMercadologicoAnterior) {
                anteriorDAO.salvar(v_mercadologico, limpar);
            }

            stm = Conexao.createStatement();

            ProgressBar.setMaximum(v_mercadologico.size());

            ProgressBar.setStatus("Importando Mercadologico...");

            if (limpar) {
                sql = new StringBuilder();
                sql.append("DELETE FROM mercadologico;");
                stm.execute(sql.toString());
            }

            Map<String, MercadologicoVO> cadastrados = carregarMercadologicos();

            for (MercadologicoVO i_mercadologico : v_mercadologico) {
                MercadologicoVO merc;
                if (UsaMercadologicoAnterior) {
                    merc = anteriorDAO.makeMercadologico(
                            i_mercadologico.getMercadologico1(),
                            i_mercadologico.getMercadologico2(),
                            i_mercadologico.getMercadologico3(),
                            i_mercadologico.getMercadologico4(),
                            i_mercadologico.getMercadologico5()
                    );
                } else {
                    merc = i_mercadologico;
                }

                if (!cadastrados.containsKey(merc.getChaveUnica())) {
                    sql = new StringBuilder();
                    sql.append("INSERT INTO mercadologico (");
                    sql.append("mercadologico1, mercadologico2, mercadologico3, ");
                    sql.append("mercadologico4, mercadologico5, nivel, descricao) ");
                    sql.append("VALUES (");
                    sql.append(merc.getMercadologico1() + ", ");
                    sql.append(merc.getMercadologico2() + ", ");
                    sql.append(merc.getMercadologico3() + ", ");
                    sql.append(merc.getMercadologico4() + ", ");
                    sql.append(merc.getMercadologico5() + ", ");
                    sql.append(merc.getNivel() + ", ");
                    sql.append("'" + merc.getDescricao() + "');");

                    stm.execute(sql.toString());

                    cadastrados.put(merc.getChaveUnica(), merc);
                }

                ProgressBar.next();
            }
            Conexao.commit();
            stm.close();

        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void salvarMax() throws Exception {

        StringBuilder sql = null;
        Statement stm = null, stmPG = null;
        ResultSet rst = null, rstPG = null;
        int mercadologico1, mercadologico2, mercadologico3, mercadologico4, mercadologico5;
        String descricao = "";
        int maxMercadologicoAnterior = 1;

        try {

            Conexao.begin();

            stmPG = Conexao.createStatement();

            try (ResultSet rst2 = stmPG.executeQuery("select coalesce(max(ant1) + 1, 1) merc from implantacao.codigoanteriormercadologico")) {
                if (rst2.next()) {
                    maxMercadologicoAnterior = rst2.getInt("merc");
                }
            }

            sql = new StringBuilder();
            sql.append("select max(mercadologico1) as mercadologico1 ");
            sql.append("from mercadologico ");
            sql.append("where mercadologico1 < 990");

            rstPG = stmPG.executeQuery(sql.toString());

            if (rstPG.next()) {

                mercadologico1 = rstPG.getInt("mercadologico1") + 1;
                mercadologico2 = 1;
                mercadologico3 = 1;
                mercadologico4 = 1;
                mercadologico5 = 1;
                descricao = "ACERTAR";

                sql = new StringBuilder();
                sql.append("insert into mercadologico (");
                sql.append("mercadologico1, mercadologico2, mercadologico3, mercadologico4, mercadologico5, nivel, descricao) ");
                sql.append("values (");
                sql.append(mercadologico1 + ", ");
                sql.append("0, ");
                sql.append("0, ");
                sql.append("0, 0, 1, ");
                sql.append("'" + descricao + "');");
                stmPG.execute(sql.toString());
                stmPG.executeUpdate(
                        "insert into implantacao.codigoanteriormercadologico \n"
                        + "(ant1,ant2,ant3,ant4,ant5,merc1,merc2,merc3,merc4,merc5,descricao,nivel)\n"
                        + "values (" + maxMercadologicoAnterior + ",0,0,0,0," + mercadologico1 + ",0,0,0,0,'" + descricao + "',1)"
                );

                sql = new StringBuilder();
                sql.append("insert into mercadologico (");
                sql.append("mercadologico1, mercadologico2, mercadologico3, mercadologico4, mercadologico5, nivel, descricao) ");
                sql.append("values (");
                sql.append(mercadologico1 + ", ");
                sql.append(mercadologico2 + ", ");
                sql.append("0, ");
                sql.append("0, ");
                sql.append("0, ");
                sql.append("2, ");
                sql.append("'" + descricao + "');");
                stmPG.execute(sql.toString());
                stmPG.executeUpdate(
                        "insert into implantacao.codigoanteriormercadologico \n"
                        + "(ant1,ant2,ant3,ant4,ant5,merc1,merc2,merc3,merc4,merc5,descricao,nivel)\n"
                        + "values (" + maxMercadologicoAnterior + "," + mercadologico2 + ",0,0,0," + mercadologico1 + "," + mercadologico2 + ",0,0,0,'" + descricao + "',2)"
                );

                sql = new StringBuilder();
                sql.append("insert into mercadologico (");
                sql.append("mercadologico1, mercadologico2, mercadologico3, mercadologico4, mercadologico5, nivel, descricao) ");
                sql.append("values (");
                sql.append(mercadologico1 + ", ");
                sql.append(mercadologico2 + ", ");
                sql.append(mercadologico3 + ", ");
                sql.append("0, ");
                sql.append("0, ");
                sql.append("3, ");
                sql.append("'" + descricao + "');");
                stmPG.execute(sql.toString());
                stmPG.executeUpdate(
                        "insert into implantacao.codigoanteriormercadologico \n"
                        + "(ant1,ant2,ant3,ant4,ant5,merc1,merc2,merc3,merc4,merc5,descricao,nivel)\n"
                        + "values (" + maxMercadologicoAnterior + "," + mercadologico2 + "," + mercadologico3 + ",0,0," + mercadologico1 + "," + mercadologico2 + "," + mercadologico3 + ",0,0,'" + descricao + "',3)"
                );

                if (temNivel4) {
                    sql = new StringBuilder();
                    sql.append("insert into mercadologico (");
                    sql.append("mercadologico1, mercadologico2, mercadologico3, mercadologico4, mercadologico5, nivel, descricao) ");
                    sql.append("values (");
                    sql.append(mercadologico1 + ", ");
                    sql.append(mercadologico2 + ", ");
                    sql.append(mercadologico3 + ", ");
                    sql.append(mercadologico4 + ", ");
                    sql.append("0, ");
                    sql.append("4, ");
                    sql.append("'" + descricao + "');");
                    stmPG.execute(sql.toString());
                    stmPG.executeUpdate(
                            "insert into implantacao.codigoanteriormercadologico \n"
                            + "(ant1,ant2,ant3,ant4,ant5,merc1,merc2,merc3,merc4,merc5,descricao,nivel)\n"
                            + "values (" + maxMercadologicoAnterior + "," + mercadologico2 + "," + mercadologico3 + "," + mercadologico4 + ",0," + mercadologico1 + "," + mercadologico2 + "," + mercadologico3 + "," + mercadologico4 + ",0,'" + descricao + "',4)"
                    );
                }
                if (temNivel5) {
                    sql = new StringBuilder();
                    sql.append("insert into mercadologico (");
                    sql.append("mercadologico1, mercadologico2, mercadologico3, mercadologico4, mercadologico5, nivel, descricao) ");
                    sql.append("values (");
                    sql.append(mercadologico1 + ", ");
                    sql.append(mercadologico2 + ", ");
                    sql.append(mercadologico3 + ", ");
                    sql.append(mercadologico4 + ", ");
                    sql.append(mercadologico5 + ", ");
                    sql.append("5, ");
                    sql.append("'" + descricao + "');");
                    stmPG.execute(sql.toString());
                    stmPG.executeUpdate(
                            "insert into implantacao.codigoanteriormercadologico \n"
                            + "(ant1,ant2,ant3,ant4,ant5,merc1,merc2,merc3,merc4,merc5,descricao,nivel)\n"
                            + "values (" + maxMercadologicoAnterior + "," + mercadologico2 + "," + mercadologico3 + "," + mercadologico4 + "," + mercadologico5 + "," + mercadologico1 + "," + mercadologico2 + "," + mercadologico3 + "," + mercadologico4 + "," + mercadologico5 + ",'" + descricao + "',5)"
                    );
                }

            }

            stmPG.close();
            Conexao.commit();

        } catch (Exception ex) {

            Conexao.rollback();
            throw ex;
        }
    }

    public void salvarMaior3Digitos(List<MercadologicoVO> vMercadologico, boolean limpar) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null, rst2 = null, rst3 = null;
        int mercadologico1 = 0, mercadologico2 = 0, mercadologico3, cont = 0,
                mercadologicoAux = 0, merc1Aux = 0;

        try {

            Conexao.begin();

            stm = Conexao.createStatement();

            ProgressBar.setMaximum(vMercadologico.size());

            ProgressBar.setStatus("Importando Mercadologico...");

            mercadologicoAux = 0;

            cont = 1;

            for (MercadologicoVO i_mercadologico : vMercadologico) {

                if (i_mercadologico.nivel == 1) {

                    if (mercadologicoAux != i_mercadologico.mercadologico1) {
                        mercadologico1 = new CodigoInternoDAO().gerarCodigoMercadologico1();

                        sql = new StringBuilder();
                        sql.append("INSERT INTO mercadologico (");
                        sql.append("mercadologico1, mercadologico2, mercadologico3, ");
                        sql.append("mercadologico4, mercadologico5, nivel, descricao) ");
                        sql.append("VALUES (");
                        sql.append(mercadologico1 + ", ");
                        sql.append(i_mercadologico.mercadologico2 + ", ");
                        sql.append(i_mercadologico.mercadologico3 + ", ");
                        sql.append(i_mercadologico.mercadologico4 + ", ");
                        sql.append(i_mercadologico.mercadologico5 + ", ");
                        sql.append(i_mercadologico.nivel + ", ");
                        sql.append("'" + i_mercadologico.descricao + "');");

                        sql.append("INSERT INTO implantacao.codigoanterior_mercadologico( ");
                        sql.append("mercadologico1_anterior, mercadologico2_anterior, mercadologico3_anterior, ");
                        sql.append("mercadologico4_anterior, mercadologico5_anterior, mercadologico1_atual, ");
                        sql.append("mercadologico2_atual, mercadologico3_atual, mercadologico4_atual, ");
                        sql.append("mercadologico5_atual, nivel) ");
                        sql.append("VALUES ( ");
                        sql.append(i_mercadologico.mercadologico1 + ", ");
                        sql.append(i_mercadologico.mercadologico2 + ", ");
                        sql.append(i_mercadologico.mercadologico3 + ", ");
                        sql.append(i_mercadologico.mercadologico4 + ", ");
                        sql.append(i_mercadologico.mercadologico5 + ", ");
                        sql.append(mercadologico1 + ", ");
                        sql.append(i_mercadologico.mercadologico2 + ", ");
                        sql.append(i_mercadologico.mercadologico3 + ", ");
                        sql.append(i_mercadologico.mercadologico4 + ", ");
                        sql.append(i_mercadologico.mercadologico5 + ", ");
                        sql.append(i_mercadologico.nivel);
                        sql.append(");");

                        stm.execute(sql.toString());
                        Conexao.commit();

                    }

                    mercadologicoAux = i_mercadologico.mercadologico1;

                } else if (i_mercadologico.nivel == 2) {

                    if (mercadologicoAux != i_mercadologico.mercadologico2) {

                        sql = new StringBuilder();
                        sql.append("select distinct m.mercadologico1 from mercadologico m ");
                        sql.append("inner join implantacao.codigoanterior_mercadologico ant ");
                        sql.append("on ant.mercadologico1_atual = m.mercadologico1 ");
                        sql.append("where ant.mercadologico1_anterior = " + i_mercadologico.mercadologico1 + " ");
                        sql.append("and ant.nivel = 1 ");

                        rst = stm.executeQuery(sql.toString());

                        if (rst.next()) {
                            mercadologico1 = rst.getInt("mercadologico1");

                            if (merc1Aux != mercadologico1) {
                                cont = 1;
                            }

                            mercadologico2 = cont;

                            sql = new StringBuilder();
                            sql.append("SELECT * from mercadologico ");
                            sql.append("WHERE mercadologico1 = " + mercadologico1);
                            sql.append("  AND mercadologico2 = " + mercadologico2);
                            rst2 = stm.executeQuery(sql.toString());

                            if (!rst2.next()) {
                                sql = new StringBuilder();
                                sql.append("INSERT INTO mercadologico (");
                                sql.append("mercadologico1, mercadologico2, mercadologico3, ");
                                sql.append("mercadologico4, mercadologico5, nivel, descricao) ");
                                sql.append("VALUES (");
                                sql.append(mercadologico1 + ", ");
                                sql.append(mercadologico2 + ", ");
                                sql.append(i_mercadologico.mercadologico3 + ", ");
                                sql.append(i_mercadologico.mercadologico4 + ", ");
                                sql.append(i_mercadologico.mercadologico5 + ", ");
                                sql.append(i_mercadologico.nivel + ", ");
                                sql.append("'" + i_mercadologico.descricao + "');");

                                sql.append("INSERT INTO implantacao.codigoanterior_mercadologico( ");
                                sql.append("mercadologico1_anterior, mercadologico2_anterior, mercadologico3_anterior, ");
                                sql.append("mercadologico4_anterior, mercadologico5_anterior, mercadologico1_atual, ");
                                sql.append("mercadologico2_atual, mercadologico3_atual, mercadologico4_atual, ");
                                sql.append("mercadologico5_atual, nivel) ");
                                sql.append("VALUES( ");
                                sql.append(i_mercadologico.mercadologico1 + ", ");
                                sql.append(i_mercadologico.mercadologico2 + ", ");
                                sql.append(i_mercadologico.mercadologico3 + ", ");
                                sql.append(i_mercadologico.mercadologico4 + ", ");
                                sql.append(i_mercadologico.mercadologico5 + ", ");
                                sql.append(mercadologico1 + ", ");
                                sql.append(mercadologico2 + ", ");
                                sql.append(i_mercadologico.mercadologico3 + ", ");
                                sql.append(i_mercadologico.mercadologico4 + ", ");
                                sql.append(i_mercadologico.mercadologico5 + ", ");
                                sql.append(i_mercadologico.nivel);
                                sql.append(");");

                                stm.execute(sql.toString());
                                Conexao.commit();
                            }

                            merc1Aux = mercadologico1;
                        }
                    }

                    cont = cont + 1;
                    mercadologicoAux = i_mercadologico.mercadologico2;

                } else if (i_mercadologico.nivel == 3) {

                    sql = new StringBuilder();
                    sql.append("select m.mercadologico1, m.mercadologico2 from mercadologico m ");
                    sql.append("inner join implantacao.codigoanterior_mercadologico ant ");
                    sql.append("on ant.mercadologico1_atual = m.mercadologico1 and ant.mercadologico2_atual = m.mercadologico2 ");
                    sql.append("where ant.mercadologico1_anterior = " + i_mercadologico.mercadologico1 + " ");
                    sql.append("and ant.mercadologico2_anterior = " + i_mercadologico.mercadologico2 + " ");
                    sql.append("and ant.nivel = 2 ");

                    rst = stm.executeQuery(sql.toString());

                    if (rst.next()) {

                        mercadologico1 = rst.getInt("mercadologico1");
                        mercadologico2 = rst.getInt("mercadologico2");

                        sql = new StringBuilder();
                        sql.append("SELECT * from mercadologico ");
                        sql.append("WHERE mercadologico1 = " + mercadologico1);
                        sql.append("  AND mercadologico2 = " + mercadologico2);
                        sql.append("  AND mercadologico3 = " + i_mercadologico.mercadologico3);

                        rst = stm.executeQuery(sql.toString());

                        if (!rst.next()) {
                            sql = new StringBuilder();
                            sql.append("INSERT INTO mercadologico (");
                            sql.append("mercadologico1, mercadologico2, mercadologico3, ");
                            sql.append("mercadologico4, mercadologico5, nivel, descricao) ");
                            sql.append("VALUES (");
                            sql.append(mercadologico1 + ", ");
                            sql.append(mercadologico2 + ", ");
                            sql.append(i_mercadologico.mercadologico3 + ", ");
                            sql.append(i_mercadologico.mercadologico4 + ", ");
                            sql.append(i_mercadologico.mercadologico5 + ", ");
                            sql.append(i_mercadologico.nivel + ", ");
                            sql.append("'" + i_mercadologico.descricao + "');");

                            sql.append("INSERT INTO implantacao.codigoanterior_mercadologico( ");
                            sql.append("mercadologico1_anterior, mercadologico2_anterior, mercadologico3_anterior, ");
                            sql.append("mercadologico4_anterior, mercadologico5_anterior, mercadologico1_atual, ");
                            sql.append("mercadologico2_atual, mercadologico3_atual, mercadologico4_atual, ");
                            sql.append("mercadologico5_atual, nivel) ");
                            sql.append("VALUES ( ");
                            sql.append(i_mercadologico.mercadologico1 + ", ");
                            sql.append(i_mercadologico.mercadologico2 + ", ");
                            sql.append(i_mercadologico.mercadologico3 + ", ");
                            sql.append(i_mercadologico.mercadologico4 + ", ");
                            sql.append(i_mercadologico.mercadologico5 + ", ");
                            sql.append(mercadologico1 + ", ");
                            sql.append(mercadologico2 + ", ");
                            sql.append(i_mercadologico.mercadologico3 + ", ");
                            sql.append(i_mercadologico.mercadologico4 + ", ");
                            sql.append(i_mercadologico.mercadologico5 + ", ");
                            sql.append(i_mercadologico.nivel);
                            sql.append(");");

                            stm.execute(sql.toString());
                            Conexao.commit();
                        }
                    }
                }

                ProgressBar.next();
            }

        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void salvar2(List<MercadologicoVO> v_mercadologico, boolean limpar) throws Exception {

        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;

        try {

            Conexao.begin();

            stm = Conexao.createStatement();

            ProgressBar.setMaximum(v_mercadologico.size());

            ProgressBar.setStatus("Importando Mercadologico...");

            if (limpar) {
                sql = new StringBuilder();
                sql.append("DELETE FROM mercadologico where mercadologico1 not in (select mercadologico1 from produto); "
                        + "DELETE FROM implantacao.codigoanteriormercadologico; ");
                stm.execute(sql.toString());
            }

            for (MercadologicoVO i_mercadologico : v_mercadologico) {

                if (i_mercadologico.nivel == 1) {

                    sql = new StringBuilder();
                    sql.append("SELECT * from mercadologico ");
                    sql.append("WHERE mercadologico1 = " + i_mercadologico.mercadologico1);
                    rst = stm.executeQuery(sql.toString());

                    if (!rst.next()) {
                        sql = new StringBuilder();
                        sql.append("INSERT INTO mercadologico (");
                        sql.append("mercadologico1, mercadologico2, mercadologico3, ");
                        sql.append("mercadologico4, mercadologico5, nivel, descricao) ");
                        sql.append("VALUES (");
                        sql.append(i_mercadologico.mercadologico1 + ", ");
                        sql.append(i_mercadologico.mercadologico2 + ", ");
                        sql.append(i_mercadologico.mercadologico3 + ", ");
                        sql.append(i_mercadologico.mercadologico4 + ", ");
                        sql.append(i_mercadologico.mercadologico5 + ", ");
                        sql.append(i_mercadologico.nivel + ", ");
                        sql.append("'" + i_mercadologico.descricao + "');");
                        stm.execute(sql.toString());
                        Conexao.commit();
                    }
                } else if (i_mercadologico.nivel == 2) {

                    sql = new StringBuilder();
                    sql.append("SELECT * from mercadologico ");
                    sql.append("WHERE mercadologico1 = " + i_mercadologico.mercadologico1);
                    sql.append(" AND mercadologico2 = " + i_mercadologico.mercadologico2);
                    rst = stm.executeQuery(sql.toString());

                    if (!rst.next()) {
                        sql = new StringBuilder();
                        sql.append("INSERT INTO mercadologico (");
                        sql.append("mercadologico1, mercadologico2, mercadologico3, ");
                        sql.append("mercadologico4, mercadologico5, nivel, descricao) ");
                        sql.append("VALUES (");
                        sql.append(i_mercadologico.mercadologico1 + ", ");
                        sql.append(i_mercadologico.mercadologico2 + ", ");
                        sql.append(i_mercadologico.mercadologico3 + ", ");
                        sql.append(i_mercadologico.mercadologico4 + ", ");
                        sql.append(i_mercadologico.mercadologico5 + ", ");
                        sql.append(i_mercadologico.nivel + ", ");
                        sql.append("'" + i_mercadologico.descricao + "');");

                        stm.execute(sql.toString());
                        Conexao.commit();
                    }
                } else if (i_mercadologico.nivel == 3) {

                    sql = new StringBuilder();
                    sql.append("SELECT * from mercadologico ");
                    sql.append("WHERE mercadologico1 = " + i_mercadologico.mercadologico1);
                    sql.append(" AND mercadologico2 = " + i_mercadologico.mercadologico2);
                    sql.append(" AND mercadologico3 = " + i_mercadologico.mercadologico3);
                    rst = stm.executeQuery(sql.toString());

                    if (!rst.next()) {
                        sql = new StringBuilder();
                        sql.append("INSERT INTO mercadologico (");
                        sql.append("mercadologico1, mercadologico2, mercadologico3, ");
                        sql.append("mercadologico4, mercadologico5, nivel, descricao) ");
                        sql.append("VALUES (");
                        sql.append(i_mercadologico.mercadologico1 + ", ");
                        sql.append(i_mercadologico.mercadologico2 + ", ");
                        sql.append(i_mercadologico.mercadologico3 + ", ");
                        sql.append(i_mercadologico.mercadologico4 + ", ");
                        sql.append(i_mercadologico.mercadologico5 + ", ");
                        sql.append(i_mercadologico.nivel + ", ");
                        sql.append("'" + i_mercadologico.descricao + "');");

                        stm.execute(sql.toString());
                        Conexao.commit();
                    }
                }

                ProgressBar.next();
            }

            stm.close();
            Conexao.commit();

        } catch (Exception ex) {

            //Conexao.rollback();
            throw ex;
        }
    }

    public void salvarDestro(List<MercadologicoVO> v_mercadologico, boolean limpar) throws Exception {

        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;

        try {

            Conexao.begin();

            stm = Conexao.createStatement();

            ProgressBar.setMaximum(v_mercadologico.size());

            ProgressBar.setStatus("Importando Mercadologico...");

            if (limpar) {
                sql = new StringBuilder();
                sql.append("DELETE FROM mercadologico;");
                stm.execute(sql.toString());
            }

            for (MercadologicoVO i_mercadologico : v_mercadologico) {

                if (i_mercadologico.nivel == 1) {

                    i_mercadologico.mercadologico1 = new CodigoInternoDAO().gerarCodigoMercadologico1();

                    sql = new StringBuilder();
                    sql.append("SELECT * from mercadologico ");
                    sql.append("WHERE mercadologico1 = " + i_mercadologico.mercadologico1);
                    rst = stm.executeQuery(sql.toString());

                    if (!rst.next()) {
                        sql = new StringBuilder();
                        sql.append("INSERT INTO mercadologico (");
                        sql.append("mercadologico1, mercadologico2, mercadologico3, ");
                        sql.append("mercadologico4, mercadologico5, nivel, descricao) ");
                        sql.append("VALUES (");
                        sql.append(i_mercadologico.mercadologico1 + ", ");
                        sql.append(i_mercadologico.mercadologico2 + ", ");
                        sql.append(i_mercadologico.mercadologico3 + ", ");
                        sql.append(i_mercadologico.mercadologico4 + ", ");
                        sql.append(i_mercadologico.mercadologico5 + ", ");
                        sql.append(i_mercadologico.nivel + ", ");
                        sql.append("'" + i_mercadologico.descricao + "');");

                        sql.append("INSERT INTO implantacao.codigoanteriormercadologico( ");
                        sql.append("ant1, ant2, ant3, ant4, ant5, merc1, merc2, merc3, merc4, merc5, ");
                        sql.append("descricao, nivel, str_mercadologico1) ");
                        sql.append("VALUES ( ");
                        sql.append("0, 0, 0, 0, 0, ");
                        sql.append(i_mercadologico.mercadologico1 + ", ");
                        sql.append("0, 0, 0, 0, ");
                        sql.append("'" + i_mercadologico.descricao + "', ");
                        sql.append(i_mercadologico.nivel + ", ");
                        sql.append("'" + i_mercadologico.strMercadologico1 + "' ");
                        sql.append(");");

                        stm.execute(sql.toString());
                        Conexao.commit();
                    }
                } else if (i_mercadologico.nivel == 2) {

                    sql = new StringBuilder();
                    sql.append("select merc1 ");
                    sql.append("from implantacao.codigoanteriormercadologico ");
                    sql.append("where str_mercadologico1 = '" + i_mercadologico.strMercadologico1 + "'");

                    rst = stm.executeQuery(sql.toString());

                    if (rst.next()) {
                        i_mercadologico.setMercadologico1(rst.getInt("merc1"));
                    }

                    sql = new StringBuilder();
                    sql.append("SELECT * from mercadologico ");
                    sql.append("WHERE mercadologico1 = " + i_mercadologico.mercadologico1);
                    sql.append(" AND mercadologico2 = " + i_mercadologico.mercadologico2);
                    rst = stm.executeQuery(sql.toString());

                    if (!rst.next()) {
                        sql = new StringBuilder();
                        sql.append("INSERT INTO mercadologico (");
                        sql.append("mercadologico1, mercadologico2, mercadologico3, ");
                        sql.append("mercadologico4, mercadologico5, nivel, descricao) ");
                        sql.append("VALUES (");
                        sql.append(i_mercadologico.mercadologico1 + ", ");
                        sql.append(i_mercadologico.mercadologico2 + ", ");
                        sql.append(i_mercadologico.mercadologico3 + ", ");
                        sql.append(i_mercadologico.mercadologico4 + ", ");
                        sql.append(i_mercadologico.mercadologico5 + ", ");
                        sql.append(i_mercadologico.nivel + ", ");
                        sql.append("'" + i_mercadologico.descricao + "');");

                        sql.append("INSERT INTO implantacao.codigoanteriormercadologico( ");
                        sql.append("ant1, ant2, ant3, ant4, ant5, merc1, merc2, merc3, merc4, merc5, ");
                        sql.append("descricao, nivel, str_mercadologico1) ");
                        sql.append("VALUES ( ");
                        sql.append("0, 0, 0, 0, 0, ");
                        sql.append(i_mercadologico.mercadologico1 + ", ");
                        sql.append(i_mercadologico.mercadologico2 + ", ");
                        sql.append("0, 0, 0, ");
                        sql.append("'" + i_mercadologico.descricao + "', ");
                        sql.append(i_mercadologico.nivel + ", ");
                        sql.append("'" + i_mercadologico.strMercadologico1 + "' ");
                        sql.append(");");

                        stm.execute(sql.toString());
                        Conexao.commit();
                    }
                } else if (i_mercadologico.nivel == 3) {

                    sql = new StringBuilder();
                    sql.append("select merc1 ");
                    sql.append("from implantacao.codigoanteriormercadologico ");
                    sql.append("where str_mercadologico1 = '" + i_mercadologico.strMercadologico1 + "'");

                    rst = stm.executeQuery(sql.toString());

                    if (rst.next()) {
                        i_mercadologico.setMercadologico1(rst.getInt("merc1"));
                    }

                    sql = new StringBuilder();
                    sql.append("SELECT * from mercadologico ");
                    sql.append("WHERE mercadologico1 = " + i_mercadologico.mercadologico1);
                    sql.append(" AND mercadologico2 = " + i_mercadologico.mercadologico2);
                    sql.append(" AND mercadologico3 = " + i_mercadologico.mercadologico3);
                    rst = stm.executeQuery(sql.toString());

                    if (!rst.next()) {
                        sql = new StringBuilder();
                        sql.append("INSERT INTO mercadologico (");
                        sql.append("mercadologico1, mercadologico2, mercadologico3, ");
                        sql.append("mercadologico4, mercadologico5, nivel, descricao) ");
                        sql.append("VALUES (");
                        sql.append(i_mercadologico.mercadologico1 + ", ");
                        sql.append(i_mercadologico.mercadologico2 + ", ");
                        sql.append(i_mercadologico.mercadologico3 + ", ");
                        sql.append(i_mercadologico.mercadologico4 + ", ");
                        sql.append(i_mercadologico.mercadologico5 + ", ");
                        sql.append(i_mercadologico.nivel + ", ");
                        sql.append("'" + i_mercadologico.descricao + "');");

                        sql.append("INSERT INTO implantacao.codigoanteriormercadologico( ");
                        sql.append("ant1, ant2, ant3, ant4, ant5, merc1, merc2, merc3, merc4, merc5, ");
                        sql.append("descricao, nivel, str_mercadologico1) ");
                        sql.append("VALUES ( ");
                        sql.append("0, 0, 0, 0, 0, ");
                        sql.append(i_mercadologico.mercadologico1 + ", ");
                        sql.append(i_mercadologico.mercadologico2 + ", ");
                        sql.append(i_mercadologico.mercadologico3 + ", ");
                        sql.append("0, 0, ");
                        sql.append("'" + i_mercadologico.descricao + "', ");
                        sql.append(i_mercadologico.nivel + ", ");
                        sql.append("'" + i_mercadologico.strMercadologico1 + "' ");
                        sql.append(");");

                        stm.execute(sql.toString());
                        Conexao.commit();
                    }
                }

                sql = new StringBuilder();
                sql.append("INSERT INTO implantacao.codigoanteriormercadologico( ");
                sql.append("ant1, ant2, ant3, ant4, ant5, merc1, merc2, merc3, merc4, merc5, ");
                sql.append("descricao, nivel, str_mercadologico1) ");
                sql.append("VALUES ( ");
                sql.append("0, 0, 0, 0, 0, ");
                sql.append(i_mercadologico.mercadologico1 + ", ");
                sql.append("0, 0, 0, 0, ");
                sql.append("'" + i_mercadologico.descricao + "', ");
                sql.append(i_mercadologico.nivel + ", ");
                sql.append("'" + i_mercadologico.strMercadologico1 + "' ");
                sql.append(");");

                stm.execute(sql.toString());

                ProgressBar.next();
            }

            stm.close();
            Conexao.commit();

        } catch (Exception ex) {

            //Conexao.rollback();
            throw ex;
        }
    }

    public List<MercadologicoVO> carregarMercadologicoParaMapeamento() throws Exception {
        List<MercadologicoVO> result = new ArrayList<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "       m3.id,\n"
                    + "       m3.mercadologico1,\n"
                    + "       m3.mercadologico2,\n"
                    + "       m3.mercadologico3,\n"
                    + "       m3.mercadologico4,\n"
                    + "       m3.mercadologico5,\n"
                    + "       m3.nivel,\n"
                    + "       m3.id || ' - ' || m1.descricao || ' - ' || m2.descricao || ' - ' || m3.descricao as descricao\n"
                    + "from \n"
                    + "	mercadologico m3\n"
                    + "	join mercadologico m1 on m3.mercadologico1 = m1.mercadologico1 and m1.nivel = 1\n"
                    + "	join mercadologico m2 on m3.mercadologico1 = m2.mercadologico1 and m3.mercadologico2 = m2.mercadologico2 and m2.nivel = 2\n"
                    + "where m3.nivel >= (select max(nivel) from mercadologico)\n"
                    + "order by\n"
                    + "       m1.descricao,\n"
                    + "       m2.descricao,\n"
                    + "       m3.descricao"
            )) {
                while (rst.next()) {
                    MercadologicoVO vo = new MercadologicoVO();

                    vo.setId(rst.getInt("id"));
                    vo.setMercadologico1(rst.getInt("mercadologico1"));
                    vo.setMercadologico2(rst.getInt("mercadologico2"));
                    vo.setMercadologico3(rst.getInt("mercadologico3"));
                    vo.setMercadologico4(rst.getInt("mercadologico4"));
                    vo.setMercadologico5(rst.getInt("mercadologico5"));
                    vo.setNivel(rst.getInt("nivel"));
                    vo.descricao = rst.getString("descricao");

                    result.add(vo);
                }
            }
        }
        return result;
    }

    public void updateDescricao(List<MercadologicoVO> v_mercadologico, int nivel) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;

        try {
            Conexao.begin();
            stm = Conexao.createStatement();
            
            if (nivel == 1) {
                ProgressBar.setStatus("Acertando descricao mercadologico1...");
            } else if (nivel == 2) {
                ProgressBar.setStatus("Acertando descricao mercadologico2...");
            } else if (nivel == 3) {
                ProgressBar.setStatus("Acertando descricao mercadologico3...");
            }
            
            ProgressBar.setMaximum(v_mercadologico.size());

            for (MercadologicoVO i_mercadologico : v_mercadologico) {
                sql = new StringBuilder();

                if (nivel == 1) {
                    sql.append("update mercadologico set "
                            + "descricao = '" + i_mercadologico.getDescricao() + "' "
                            + "where mercadologico1 = " + i_mercadologico.getMercadologico1() + " "
                            + "and nivel = 1; ");
                } else if (nivel == 2) {
                    sql.append("update mercadologico set "
                            + "descricao = '" + i_mercadologico.getDescricao() + "' "
                            + "where mercadologico2 = " + i_mercadologico.getMercadologico2() + " "
                            + "and nivel = 2; ");
                } else if (nivel == 3) {
                    sql.append("update mercadologico set "
                            + "descricao = '" + i_mercadologico.getDescricao() + "' "
                            + "where mercadologico3 = " + i_mercadologico.getMercadologico3() + " "
                            + "and nivel = 3; ");
                }
                stm.execute(sql.toString());
                ProgressBar.next();
            }
            
            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }
    
    public void completarMercadologico() throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        
        try {
            Conexao.begin();
            stm = Conexao.createStatement();
            sql = new StringBuilder();
            sql.append("insert into mercadologico("
                    + "mercadologico1, mercadologico2, mercadologico3, mercadologico4, mercadologico5, "
                    + "descricao, nivel) "
                    + "(select mercadologico1, 1, mercadologico3, mercadologico4, mercadologico5, "
                    + "descricao, 2 from mercadologico "
                    + "where mercadologico1 not in (select mercadologico1 from mercadologico where nivel = 2));"
                    
                    + "insert into mercadologico("
                    + "mercadologico1, mercadologico2, mercadologico3, mercadologico4, mercadologico5, "
                    + "descricao, nivel) "
                    + "(select mercadologico1, mercadologico2, 1, mercadologico4, mercadologico5, "
                    + "descricao, 3 from mercadologico "
                    + "where mercadologico1 not in (select mercadologico1 from mercadologico where nivel = 3 ));");
            stm.execute(sql.toString());
            
            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.begin();
            throw ex;
        }
    }
}
