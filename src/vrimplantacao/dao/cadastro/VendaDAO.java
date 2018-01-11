package vrimplantacao.dao.cadastro;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao.vo.vrimplantacao.VendasVO;

public class VendaDAO {

    public void salvar(List<VendasVO> v_Venda, int idLoja/*, int mes, int ano, String descMes*/) throws Exception {
        StringBuilder sql = null;
        Statement stm = null, stm2 = null;
        ResultSet rst = null, rst2 = null;

        try {
            Conexao.begin();
            stm = Conexao.createStatement();
            stm2 = Conexao.createStatement();
            ProgressBar.setMaximum(v_Venda.size());
            ProgressBar.setStatus("Importando Vendas...");

            for (VendasVO i_Venda : v_Venda) {
                sql = new StringBuilder();
                sql.append("select p.id from produto p ");
                sql.append("inner join implantacao.codigoanterior ant ");
                sql.append("on ant.codigoatual = p.id ");
                sql.append("where ant.codigoanterior = " + i_Venda.id_produto + " ");
                sql.append("and ant.id_loja = " + idLoja);
                rst = stm.executeQuery(sql.toString());

                if (rst.next()) {

                    sql = new StringBuilder();
                    sql.append("select * from venda ");
                    sql.append("where id_produto = " + rst.getInt("id") + " ");
                    sql.append("and data = '" + i_Venda.data + "' ");
                    sql.append("and id_loja = " + idLoja + " ");
                    sql.append("and precovenda = " + Math.round(i_Venda.precovenda) + " ");
                    sql.append("and custocomimposto = " + i_Venda.custocomimposto + " ");
                    sql.append("and cupomfiscal = " + i_Venda.cupomfiscal);
                    rst2 = stm2.executeQuery(sql.toString());

                    if (rst2.next()) {
                        sql = new StringBuilder();
                        sql.append("update venda set ");
                        sql.append("valortotal = valortotal + " + i_Venda.valortotal + ", ");
                        sql.append("quantidade = quantidade + " + i_Venda.quantidade + ", ");
                        sql.append("precovenda = " + i_Venda.precovenda + " ");
                        sql.append("where id_loja = " + idLoja + " ");
                        sql.append("and id_produto = " + rst.getInt("id") + " ");
                        sql.append("and data = '" + i_Venda.data + "' ");
                        sql.append("and precovenda = " + Math.round(i_Venda.precovenda) + " ");
                        sql.append("and custocomimposto = " + i_Venda.custocomimposto + " ");
                        sql.append("and cupomfiscal = " + i_Venda.cupomfiscal);
                        stm.execute(sql.toString());
                    } else {
                        sql = new StringBuilder();
                        sql.append("INSERT INTO venda( ");
                        sql.append("id_loja, id_produto, data, precovenda, quantidade, id_comprador, ");
                        sql.append("custocomimposto, piscofins, operacional, icmscredito, icmsdebito, ");
                        sql.append("valortotal, custosemimposto, oferta, perda, customediosemimposto, ");
                        sql.append("customediocomimposto, piscofinscredito, cupomfiscal) ");
                        sql.append("VALUES ( ");
                        sql.append(idLoja + ", ");
                        sql.append(rst.getInt("id") + ", ");
                        sql.append("'" + i_Venda.data + "', ");
                        sql.append(i_Venda.precovenda + ", ");
                        sql.append(i_Venda.quantidade + ", ");
                        sql.append(i_Venda.id_comprador + ", ");
                        sql.append(i_Venda.custocomimposto + ", ");
                        sql.append(i_Venda.piscofins + ", ");
                        sql.append(i_Venda.operacional + ", ");
                        sql.append(i_Venda.icmscredito + ", ");
                        sql.append(i_Venda.icmsdebito + ", ");
                        sql.append(i_Venda.valortotal + ", ");
                        sql.append(i_Venda.custosemimposto + ", ");
                        sql.append(i_Venda.oferta + ", ");
                        sql.append(i_Venda.perda + ", ");
                        sql.append(i_Venda.customediosemimposto + ", ");
                        sql.append(i_Venda.customediosemimposto + ", ");
                        sql.append(i_Venda.piscofinscredito + ", ");
                        sql.append(i_Venda.cupomfiscal);
                        sql.append(");");
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

    public void salvarJJR(List<VendasVO> v_Venda, int idLoja) throws Exception {
        StringBuilder sql = null;
        Statement stm = null, stm2 = null;
        ResultSet rst = null, rst2 = null;

        try {
            Conexao.begin();
            stm = Conexao.createStatement();
            stm2 = Conexao.createStatement();
            ProgressBar.setMaximum(v_Venda.size());
            ProgressBar.setStatus("Importando Vendas...");

            for (VendasVO i_Venda : v_Venda) {
                sql = new StringBuilder();
                sql.append("select * from venda ");
                sql.append("where id_produto = " + i_Venda.id_produto + " ");
                sql.append("and data = '" + i_Venda.data + "' ");
                sql.append("and id_loja = " + idLoja + " ");
                sql.append("and precovenda = " + Math.round(i_Venda.precovenda) + " ");
                sql.append("and custocomimposto = " + i_Venda.custocomimposto + " ");
                sql.append("and cupomfiscal = " + i_Venda.cupomfiscal);
                rst2 = stm2.executeQuery(sql.toString());

                if (rst2.next()) {
                    sql = new StringBuilder();
                    sql.append("update venda set ");
                    sql.append("valortotal = valortotal + " + i_Venda.valortotal + ", ");
                    sql.append("quantidade = quantidade + " + i_Venda.quantidade + ", ");
                    sql.append("precovenda = " + i_Venda.precovenda + " ");
                    sql.append("where id_loja = " + idLoja + " ");
                    sql.append("and id_produto = " + i_Venda.id_produto + " ");
                    sql.append("and data = '" + i_Venda.data + "' ");
                    sql.append("and precovenda = " + Math.round(i_Venda.precovenda) + " ");
                    sql.append("and custocomimposto = " + i_Venda.custocomimposto + " ");
                    sql.append("and cupomfiscal = " + i_Venda.cupomfiscal);
                    stm.execute(sql.toString());
                } else {
                    sql = new StringBuilder();
                    sql.append("INSERT INTO venda( ");
                    sql.append("id_loja, id_produto, data, precovenda, quantidade, id_comprador, ");
                    sql.append("custocomimposto, piscofins, operacional, icmscredito, icmsdebito, ");
                    sql.append("valortotal, custosemimposto, oferta, perda, customediosemimposto, ");
                    sql.append("customediocomimposto, piscofinscredito, cupomfiscal) ");
                    sql.append("VALUES ( ");
                    sql.append(idLoja + ", ");
                    sql.append(i_Venda.id_produto + ", ");
                    sql.append("'" + i_Venda.data + "', ");
                    sql.append(i_Venda.precovenda + ", ");
                    sql.append(i_Venda.quantidade + ", ");
                    sql.append(i_Venda.id_comprador + ", ");
                    sql.append(i_Venda.custocomimposto + ", ");
                    sql.append(i_Venda.piscofins + ", ");
                    sql.append(i_Venda.operacional + ", ");
                    sql.append(i_Venda.icmscredito + ", ");
                    sql.append(i_Venda.icmsdebito + ", ");
                    sql.append(i_Venda.valortotal + ", ");
                    sql.append(i_Venda.custosemimposto + ", ");
                    sql.append(i_Venda.oferta + ", ");
                    sql.append(i_Venda.perda + ", ");
                    sql.append(i_Venda.customediosemimposto + ", ");
                    sql.append(i_Venda.customediosemimposto + ", ");
                    sql.append(i_Venda.piscofinscredito + ", ");
                    sql.append(i_Venda.cupomfiscal);
                    sql.append(");");
                    stm.execute(sql.toString());
                }

                ProgressBar.next();
            }
            stm.close();
            stm2.close();
            Conexao.commit();
        } catch (Exception ex) {
            System.out.println(sql.toString());
            Conexao.rollback();
            throw ex;
        }
    }
}
