package vrimplantacao.dao.cadastro;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao.vo.vrimplantacao.LogEstoqueVO;

public class LogEstoqueDAO {

    public void salvar(List<LogEstoqueVO> v_logEstoque) throws Exception {
        StringBuilder sql = null;
        Statement stm = null, stm2 = null, stm3 = null;
        ResultSet rst = null, rst3 = null;
        int cont;

        try {
            Conexao.begin();
            stm = Conexao.createStatement();
            stm2 = Conexao.createStatement();
            stm3 = Conexao.createStatement();
            ProgressBar.setMaximum(v_logEstoque.size());
            ProgressBar.setStatus("Atualizando estoque...");
            for (LogEstoqueVO i_logEstoque : v_logEstoque) {
                cont = 0;
                sql = new StringBuilder();
                sql.append("select id, quantidade, estoqueanterior, estoqueatual, id_tipoentradasaida from logestoque "
                        + "where id_produto = " + i_logEstoque.getId_produto() + ""
                        + "and id_loja = " + i_logEstoque.getId_loja() + ""
                        + "order by id ");
                rst = stm.executeQuery(sql.toString());
                while (rst.next()) {

                    if (cont == 0) {
                        if (rst.getInt("id_tipoentradasaida") == 0) {
                            sql = new StringBuilder();
                            sql.append("insert into logestoque2 (");
                            sql.append("id, id_tipoentradasaida, id_loja, id_produto, quantidade, estoqueanterior, estoqueatual) ");
                            sql.append("values (");
                            sql.append(rst.getInt("id") + ", ");
                            sql.append(rst.getInt("id_tipoentradasaida") + ", ");
                            sql.append(i_logEstoque.getId_loja() + ", ");
                            sql.append(i_logEstoque.getId_produto() + ", ");
                            sql.append(rst.getDouble("quantidade") + ", ");
                            sql.append(i_logEstoque.getEstoqueanterior() + ", ");
                            sql.append(i_logEstoque.getEstoqueanterior() + " + (" + rst.getDouble("quantidade") + ")");
                            sql.append(") returning estoqueatual");
                        } else if (rst.getInt("id_tipoentradasaida") == 1) {
                            sql = new StringBuilder();
                            sql.append("insert into logestoque2 (");
                            sql.append("id, id_tipoentradasaida, id_loja, id_produto, quantidade, estoqueanterior, estoqueatual) ");
                            sql.append("values (");
                            sql.append(rst.getInt("id") + ", ");
                            sql.append(rst.getInt("id_tipoentradasaida") + ", ");
                            sql.append(i_logEstoque.getId_loja() + ", ");
                            sql.append(i_logEstoque.getId_produto() + ", ");
                            sql.append(rst.getDouble("quantidade") + ", ");
                            sql.append(i_logEstoque.getEstoqueanterior() + ", ");
                            sql.append(i_logEstoque.getEstoqueanterior() + " - (" + rst.getDouble("quantidade") + ")");
                            sql.append(") returning estoqueatual;");
                        }

                        try (ResultSet rst2 = stm2.executeQuery(sql.toString())) {
                            if (rst2.next()) {
                                sql = new StringBuilder();
                                sql.append("update produtocomplemento set "
                                        + "estoque = " + rst2.getDouble("estoqueatual") + " "
                                        + "where id_loja = " + i_logEstoque.getId_loja() + ""
                                        + " and id_produto = " + i_logEstoque.getId_produto() + ";");
                                stm2.execute(sql.toString());
                            }
                        }
                    } else {

                        sql = new StringBuilder();
                        sql.append("select estoque from produtocomplemento "
                                + "where id_loja = " + i_logEstoque.getId_loja() + " "
                                + "and id_produto = " + i_logEstoque.getId_produto());
                        rst3 = stm3.executeQuery(sql.toString());

                        if (rst3.next()) {
                            if (rst.getInt("id_tipoentradasaida") == 0) {
                                sql = new StringBuilder();
                                sql.append("insert into logestoque2 (");
                                sql.append("id, id_tipoentradasaida, id_loja, id_produto, quantidade, estoqueanterior, estoqueatual) ");
                                sql.append("values (");
                                sql.append(rst.getInt("id") + ", ");
                                sql.append(rst.getInt("id_tipoentradasaida") + ", ");
                                sql.append(i_logEstoque.getId_loja() + ", ");
                                sql.append(i_logEstoque.getId_produto() + ", ");
                                sql.append(rst.getDouble("quantidade") + ", ");
                                sql.append(rst3.getDouble("estoque") + ", ");
                                sql.append(rst3.getDouble("estoque") + " + (" + rst.getDouble("quantidade") + ")");
                                sql.append(") returning estoqueatual;");
                            } else if (rst.getInt("id_tipoentradasaida") == 1) {
                                sql = new StringBuilder();
                                sql.append("insert into logestoque2 (");
                                sql.append("id, id_tipoentradasaida, id_loja, id_produto, quantidade, estoqueanterior, estoqueatual) ");
                                sql.append("values (");
                                sql.append(rst.getInt("id") + ", ");
                                sql.append(rst.getInt("id_tipoentradasaida") + ", ");
                                sql.append(i_logEstoque.getId_loja() + ", ");
                                sql.append(i_logEstoque.getId_produto() + ", ");
                                sql.append(rst.getDouble("quantidade") + ", ");
                                sql.append(rst3.getDouble("estoque") + ", ");
                                sql.append(rst3.getDouble("estoque") + " - (" + rst.getDouble("quantidade") + ")");
                                sql.append(") returning estoqueatual;");
                            }

                            try (ResultSet rst2 = stm2.executeQuery(sql.toString())) {
                                if (rst2.next()) {
                                    sql = new StringBuilder();
                                    sql.append("update produtocomplemento set "
                                            + "estoque = " + rst2.getDouble("estoqueatual") + " "
                                            + "where id_loja = " + i_logEstoque.getId_loja() + ""
                                            + " and id_produto = " + i_logEstoque.getId_produto() + ";");
                                    stm2.execute(sql.toString());
                                }
                            }
                        }
                    }
                    cont = cont + 1;
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
}
