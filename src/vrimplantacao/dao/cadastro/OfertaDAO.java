package vrimplantacao.dao.cadastro;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao.vo.vrimplantacao.OfertaVO;

public class OfertaDAO {

    public void salvar(List<OfertaVO> v_oferta, int idLoja) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;

        try {
            Conexao.begin();
            stm = Conexao.createStatement();
            ProgressBar.setMaximum(v_oferta.size());
            ProgressBar.setStatus("Importando Oferta de Produto...");

            for (OfertaVO i_oferta : v_oferta) {

                sql = new StringBuilder();
                sql.append("select p.id, pc.precovenda ");
                sql.append("from produto p ");
                sql.append("inner join implantacao.codigoanterior ant on ant.codigoatual = p.id ");
                sql.append("inner join produtocomplemento pc on pc.id_produto = p.id ");

                if (i_oferta.id_produto > 0) {
                    sql.append("where ant.codigoanterior = " + i_oferta.id_produto);
                } else {
                    sql.append("where ant.codigoanterior::varchar = '" + String.format("%.0f", i_oferta.id_produtoDouble) + "'");
                }

                rst = stm.executeQuery(sql.toString());

                if (rst.next()) {

                    sql = new StringBuilder();
                    sql.append("INSERT INTO oferta( ");
                    sql.append("id_loja, id_produto, datainicio, datatermino, precooferta, ");
                    sql.append("preconormal, id_situacaooferta, id_tipooferta, precoimediato, ofertafamilia,");
                    sql.append("ofertaassociado, controle) ");
                    sql.append("VALUES (");
                    sql.append(i_oferta.id_loja + ", ");
                    sql.append(rst.getInt("id") + ", ");
                    sql.append("'" + i_oferta.datainicio + "', ");
                    sql.append("'" + i_oferta.datatermino + "', ");
                    sql.append(i_oferta.precooferta + ", ");
                    //
                    sql.append(rst.getDouble("precovenda") + ", ");
                    sql.append(i_oferta.id_situacaooferta + ", ");
                    sql.append(i_oferta.id_tipooferta + ", ");
                    sql.append(i_oferta.precoimediato + ", ");
                    sql.append(i_oferta.ofertafamilia + ", ");
                    //                
                    sql.append(i_oferta.ofertaassociado + ", ");
                    sql.append(i_oferta.controle + " ");
                    sql.append(" ) ");
                    stm.execute(sql.toString());

                }

                ProgressBar.next();
            }

            stm.close();
            Conexao.commit();

        } catch (Exception ex) {

            Conexao.rollback();
            throw ex;
        }
    }

    public void salvar(List<OfertaVO> v_oferta, int idLoja, String impLoja) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;

        try {
            Conexao.begin();
            stm = Conexao.createStatement();
            ProgressBar.setMaximum(v_oferta.size());
            ProgressBar.setStatus("Importando Oferta de Produto...");

            for (OfertaVO i_oferta : v_oferta) {

                sql = new StringBuilder();
                sql.append("select p.id, pc.precovenda ");
                sql.append("from produto p ");
                sql.append("inner join implantacao.codant_produto ant on ant.codigoatual = p.id ");
                sql.append("inner join produtocomplemento pc on pc.id_produto = p.id ");
                sql.append("where ant.imploja = '" + impLoja + "' ");

                if (i_oferta.id_produto > 0) {
                    sql.append("and ant.impid::integer = " + i_oferta.id_produto);
                } else {
                    sql.append("and ant.impid = '" + String.format("%.0f", i_oferta.id_produtoDouble) + "'");
                }

                rst = stm.executeQuery(sql.toString());
                if (rst.next()) {
                    sql = new StringBuilder();
                    sql.append("INSERT INTO oferta( ");
                    sql.append("id_loja, id_produto, datainicio, datatermino, precooferta, ");
                    sql.append("preconormal, id_situacaooferta, id_tipooferta, precoimediato, ofertafamilia,");
                    sql.append("ofertaassociado, controle) ");
                    sql.append("VALUES (");
                    sql.append(i_oferta.id_loja + ", ");
                    sql.append(rst.getInt("id") + ", ");
                    sql.append("'" + i_oferta.datainicio + "', ");
                    sql.append("'" + i_oferta.datatermino + "', ");
                    sql.append(i_oferta.precooferta + ", ");
                    sql.append(rst.getDouble("precovenda") + ", ");
                    sql.append(i_oferta.id_situacaooferta + ", ");
                    sql.append(i_oferta.id_tipooferta + ", ");
                    sql.append(i_oferta.precoimediato + ", ");
                    sql.append(i_oferta.ofertafamilia + ", ");
                    sql.append(i_oferta.ofertaassociado + ", ");
                    sql.append(i_oferta.controle + " ");
                    sql.append(" ) ");
                    stm.execute(sql.toString());
                }
                ProgressBar.next();
            }
            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }
    
    public void salvar2(List<OfertaVO> v_oferta, int idLoja, String impLoja) throws Exception {
        StringBuilder sql = null, sql2 = null;
        Statement stm = null, stm2 = null;
        ResultSet rst = null, rst2 = null;

        try {
            Conexao.begin();
            stm = Conexao.createStatement();
            stm2 = Conexao.createStatement();
            ProgressBar.setMaximum(v_oferta.size());
            ProgressBar.setStatus("Importando Oferta de Produto...");

            for (OfertaVO i_oferta : v_oferta) {

                sql = new StringBuilder();
                sql.append("select p.id, pc.precovenda ");
                sql.append("from produto p ");
                sql.append("inner join implantacao.codant_produto ant on ant.codigoatual = p.id ");
                sql.append("inner join produtocomplemento pc on pc.id_produto = p.id ");
                sql.append("where ant.imploja = '" + impLoja + "' ");
                sql.append("and ant.impid = '" + i_oferta.getIdProduto() + "'");

                rst = stm.executeQuery(sql.toString());
                if (rst.next()) {
                    
                    sql2 = new StringBuilder();
                    sql2.append("select * from oferta "
                            + "where id_produto = " + rst.getInt("id") + " "
                            + "and datatermino = '" + i_oferta.datatermino + "' "
                            + "and id_loja = " + i_oferta.id_loja);
                    rst2 = stm2.executeQuery(sql2.toString());
                    if (!rst2.next()) {
                        sql = new StringBuilder();
                        sql.append("INSERT INTO oferta( ");
                        sql.append("id_loja, id_produto, datainicio, datatermino, precooferta, ");
                        sql.append("preconormal, id_situacaooferta, id_tipooferta, precoimediato, ofertafamilia,");
                        sql.append("ofertaassociado, controle) ");
                        sql.append("VALUES (");
                        sql.append(i_oferta.id_loja + ", ");
                        sql.append(rst.getInt("id") + ", ");
                        sql.append("'" + i_oferta.datainicio + "', ");
                        sql.append("'" + i_oferta.datatermino + "', ");
                        sql.append(i_oferta.precooferta + ", ");
                        sql.append(rst.getDouble("precovenda") + ", ");
                        sql.append(i_oferta.id_situacaooferta + ", ");
                        sql.append(i_oferta.id_tipooferta + ", ");
                        sql.append(i_oferta.precoimediato + ", ");
                        sql.append(i_oferta.ofertafamilia + ", ");
                        sql.append(i_oferta.ofertaassociado + ", ");
                        sql.append(i_oferta.controle + " ");
                        sql.append(" ) ");
                        stm.execute(sql.toString());
                    }
                }
                ProgressBar.next();
            }
            stm.close();
            stm2.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }
    
    public void salvarUnificacao(List<OfertaVO> v_oferta, int idLoja) throws Exception {

        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;

        try {
            Conexao.begin();
            stm = Conexao.createStatement();
            ProgressBar.setMaximum(v_oferta.size());
            ProgressBar.setStatus("Importando Oferta de Produto...");

            for (OfertaVO i_oferta : v_oferta) {

                sql = new StringBuilder();
                sql.append("select p.id, pc.precovenda ");
                sql.append("from produto p ");
                sql.append("inner join produtocomplemento pc on pc.id_produto = p.id ");
                sql.append("where p.id = " + i_oferta.id_produto);
                rst = stm.executeQuery(sql.toString());

                if (rst.next()) {

                    sql = new StringBuilder();
                    sql.append("INSERT INTO oferta( ");
                    sql.append("id_loja, id_produto, datainicio, datatermino, precooferta, ");
                    sql.append("preconormal, id_situacaooferta, id_tipooferta, precoimediato, ofertafamilia,");
                    sql.append("ofertaassociado, controle) ");
                    sql.append("VALUES (");
                    sql.append(idLoja + ", ");
                    sql.append(rst.getInt("id") + ", ");
                    sql.append("'" + i_oferta.datainicio + "', ");
                    sql.append("'" + i_oferta.datatermino + "', ");
                    sql.append(i_oferta.precooferta + ", ");
                    //
                    sql.append(rst.getDouble("precovenda") + ", ");
                    sql.append(i_oferta.id_situacaooferta + ", ");
                    sql.append(i_oferta.id_tipooferta + ", ");
                    sql.append(i_oferta.precoimediato + ", ");
                    sql.append(i_oferta.ofertafamilia + ", ");
                    //                
                    sql.append(i_oferta.ofertaassociado + ", ");
                    sql.append(i_oferta.controle + " ");
                    sql.append(" ) ");
                    stm.execute(sql.toString());
                }
                ProgressBar.next();
            }

            stm.close();
            Conexao.commit();

        } catch (Exception ex) {

            Conexao.rollback();
            throw ex;
        }
    }
}
