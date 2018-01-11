/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao.dao.cadastro;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao.vo.vrimplantacao.PagarOutrasDespesasVO;
import vrimplantacao.vo.vrimplantacao.PagarOutrasDespesasVencimentoVO;
import vrimplantacao2.utils.sql.SQLUtils;

/**
 *
 * @author lucasrafael
 */
public class PagarOutrasDespesasDAO {
    
    public void salvar(List<PagarOutrasDespesasVO> v_pagarOutrasDespesas) throws Exception {
        salvar(null, null, v_pagarOutrasDespesas);
    }

    public void salvar(String sistema, String loja, List<PagarOutrasDespesasVO> v_pagarOutrasDespesas) throws Exception {

        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null, rst2 = null;
        java.sql.Date dataEmissao, dataVencimento;
        int idFornecedor = 1;
        boolean achou = false;
        try {

            Conexao.begin();

            stm = Conexao.createStatement();

            ProgressBar.setMaximum(v_pagarOutrasDespesas.size());
            ProgressBar.setStatus("Importando Outras Despesas...");

            for (PagarOutrasDespesasVO i_pagarOutrasDespesas : v_pagarOutrasDespesas) {

                achou = false;
                
                sql = new StringBuilder();
                sql.append("select f.id from fornecedor f ");
                sql.append("inner join implantacao.codant_fornecedor ant ");
                sql.append("on ant.codigoatual = f.id ");
                if (sistema != null && loja != null) {
                    sql.append("where ant.importsistema = ").append(SQLUtils.stringSQL(sistema)).append("\n");
                    sql.append("and ant.importloja = ").append(SQLUtils.stringSQL(loja)).append("\n");
                    sql.append("and ant.importid = ").append(SQLUtils.stringSQL(String.valueOf(i_pagarOutrasDespesas.id_fornecedor)));
                } else {
                    sql.append("where ant.importid = ").append(SQLUtils.stringSQL(String.valueOf(i_pagarOutrasDespesas.id_fornecedor)));
                }

                rst = stm.executeQuery(sql.toString());

                if (rst.next()) {
                    idFornecedor = rst.getInt("id");
                }

                sql = new StringBuilder();
                sql.append("select * from pagaroutrasdespesas "
                        + "where id_fornecedor = " + idFornecedor + " "
                        + "and numerodocumento = " + i_pagarOutrasDespesas.numerodocumento + " "
                        + "and dataemissao = '" + i_pagarOutrasDespesas.dataemissao + "' "
                        + "and valor = " + i_pagarOutrasDespesas.valor + " "
                        + "and id_loja = " + i_pagarOutrasDespesas.id_loja);
                rst = stm.executeQuery(sql.toString());
                if (rst.next()) {
                    achou = true;
                }

                if (!achou) {
                    sql = new StringBuilder();
                    sql.append("select * from pagarfornecedor "
                            + "where id_fornecedor = " + idFornecedor + " "
                            + "and numerodocumento = " + i_pagarOutrasDespesas.numerodocumento + " "
                            + "and dataemissao = '" + i_pagarOutrasDespesas.dataemissao + "' "
                            + "and valor = " + i_pagarOutrasDespesas.valor + " "
                            + "and id_loja = " + i_pagarOutrasDespesas.id_loja);
                    rst = stm.executeQuery(sql.toString());
                    if (rst.next()) {
                        achou = true;
                    }
                }

                if (!achou) {
                    sql = new StringBuilder();
                    sql.append("select * from notadespesa "
                            + "where id_fornecedor = " + idFornecedor + " "
                            + "and numeronota = " + i_pagarOutrasDespesas.numerodocumento + " "
                            + "and dataemissao = '" + i_pagarOutrasDespesas.dataemissao + "' "
                            + "and valortotal = " + i_pagarOutrasDespesas.valor + " "
                            + "and id_loja = " + i_pagarOutrasDespesas.id_loja);
                    rst = stm.executeQuery(sql.toString());
                    if (rst.next()) {
                        achou = true;
                    }
                }

                if (!achou) {
                    sql = new StringBuilder();
                    sql.append("INSERT INTO pagaroutrasdespesas(");
                    sql.append("id_fornecedor, numerodocumento, id_tipoentrada, dataemissao, ");
                    sql.append("dataentrada, valor, id_situacaopagaroutrasdespesas, id_loja, ");
                    sql.append("observacao, id_tipopiscofins) ");
                    sql.append("VALUES(");
                    sql.append(idFornecedor + ",");
                    sql.append(i_pagarOutrasDespesas.numerodocumento + ",");
                    sql.append(i_pagarOutrasDespesas.id_tipoentrada + ",");

                    if (i_pagarOutrasDespesas.dataemissao.isEmpty()) {
                        dataEmissao = new java.sql.Date(new java.util.Date().getTime());
                        sql.append("'" + dataEmissao + "', ");
                    } else {
                        sql.append("'" + i_pagarOutrasDespesas.dataemissao + "',");
                    }

                    if (i_pagarOutrasDespesas.dataentrada.isEmpty()) {
                        dataEmissao = new java.sql.Date(new java.util.Date().getTime());
                        sql.append("'" + dataEmissao + "', ");
                    } else {
                        sql.append("'" + i_pagarOutrasDespesas.dataentrada + "',");
                    }

                    sql.append(i_pagarOutrasDespesas.valor + ",");
                    sql.append(i_pagarOutrasDespesas.id_situacaopagaroutrasdespesas + ",");
                    sql.append(i_pagarOutrasDespesas.id_loja + ",");
                    sql.append("'" + i_pagarOutrasDespesas.observacao + "',");
                    sql.append(i_pagarOutrasDespesas.id_tipopiscofins + ");");
                    stm.execute(sql.toString());

                    for (PagarOutrasDespesasVencimentoVO oPagarOutrasDespesasVencimento : i_pagarOutrasDespesas.vPagarOutrasDespesasVencimento) {
                        sql = new StringBuilder();
                        sql.append("select max(id) as id from pagaroutrasdespesas ");
                        rst2 = stm.executeQuery(sql.toString());

                        if (rst2.next()) {
                            sql = new StringBuilder();
                            sql.append("INSERT INTO pagaroutrasdespesasvencimento(");
                            sql.append("id_pagaroutrasdespesas, datavencimento, valor) ");
                            sql.append("VALUES ( ");
                            sql.append(rst2.getInt("id") + ",");

                            if (oPagarOutrasDespesasVencimento.datavencimento.isEmpty()) {
                                dataVencimento = new java.sql.Date(new java.util.Date().getTime());
                                sql.append("'" + dataVencimento + "', ");
                            } else {
                                sql.append("'" + oPagarOutrasDespesasVencimento.datavencimento + "',");
                            }

                            sql.append(oPagarOutrasDespesasVencimento.valor + ");");
                            stm.execute(sql.toString());
                        }
                    }
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

    public void salvar2(List<PagarOutrasDespesasVO> v_pagarOutrasDespesas) throws Exception {

        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null, rst2 = null;
        java.sql.Date dataEmissao, dataVencimento;
        int idFornecedor = 1;
        boolean achou = false;
        try {

            Conexao.begin();

            stm = Conexao.createStatement();

            ProgressBar.setMaximum(v_pagarOutrasDespesas.size());
            ProgressBar.setStatus("Importando Outras Despesas...");

            for (PagarOutrasDespesasVO i_pagarOutrasDespesas : v_pagarOutrasDespesas) {

                achou = false;
                idFornecedor = i_pagarOutrasDespesas.id_fornecedor;

                sql = new StringBuilder();
                sql.append("select * from pagaroutrasdespesas "
                        + "where id_fornecedor = " + idFornecedor + " "
                        + "and numerodocumento = " + i_pagarOutrasDespesas.numerodocumento + " "
                        + "and dataemissao = '" + i_pagarOutrasDespesas.dataemissao + "' "
                        + "and valor = " + i_pagarOutrasDespesas.valor + " "
                        + "and id_loja = " + i_pagarOutrasDespesas.id_loja);
                rst = stm.executeQuery(sql.toString());
                if (rst.next()) {
                    achou = true;
                }

                if (!achou) {
                    sql = new StringBuilder();
                    sql.append("select * from pagarfornecedor "
                            + "where id_fornecedor = " + idFornecedor + " "
                            + "and numerodocumento = " + i_pagarOutrasDespesas.numerodocumento + " "
                            + "and dataemissao = '" + i_pagarOutrasDespesas.dataemissao + "' "
                            + "and valor = " + i_pagarOutrasDespesas.valor + " "
                            + "and id_loja = " + i_pagarOutrasDespesas.id_loja);
                    rst = stm.executeQuery(sql.toString());
                    if (rst.next()) {
                        achou = true;
                    }
                }

                if (!achou) {
                    sql = new StringBuilder();
                    sql.append("select * from notadespesa "
                            + "where id_fornecedor = " + idFornecedor + " "
                            + "and numeronota = " + i_pagarOutrasDespesas.numerodocumento + " "
                            + "and dataemissao = '" + i_pagarOutrasDespesas.dataemissao + "' "
                            + "and valortotal = " + i_pagarOutrasDespesas.valor + " "
                            + "and id_loja = " + i_pagarOutrasDespesas.id_loja);
                    rst = stm.executeQuery(sql.toString());
                    if (rst.next()) {
                        achou = true;
                    }
                }

                if (!achou) {
                    sql = new StringBuilder();
                    sql.append("INSERT INTO pagaroutrasdespesas(");
                    sql.append("id_fornecedor, numerodocumento, id_tipoentrada, dataemissao, ");
                    sql.append("dataentrada, valor, id_situacaopagaroutrasdespesas, id_loja, ");
                    sql.append("observacao, id_tipopiscofins) ");
                    sql.append("VALUES(");
                    sql.append(idFornecedor + ",");
                    sql.append(i_pagarOutrasDespesas.numerodocumento + ",");
                    sql.append(i_pagarOutrasDespesas.id_tipoentrada + ",");

                    if (i_pagarOutrasDespesas.dataemissao.isEmpty()) {
                        dataEmissao = new java.sql.Date(new java.util.Date().getTime());
                        sql.append("'" + dataEmissao + "', ");
                    } else {
                        sql.append("'" + i_pagarOutrasDespesas.dataemissao + "',");
                    }

                    if (i_pagarOutrasDespesas.dataentrada.isEmpty()) {
                        dataEmissao = new java.sql.Date(new java.util.Date().getTime());
                        sql.append("'" + dataEmissao + "', ");
                    } else {
                        sql.append("'" + i_pagarOutrasDespesas.dataentrada + "',");
                    }

                    sql.append(i_pagarOutrasDespesas.valor + ",");
                    sql.append(i_pagarOutrasDespesas.id_situacaopagaroutrasdespesas + ",");
                    sql.append(i_pagarOutrasDespesas.id_loja + ",");
                    sql.append("'" + i_pagarOutrasDespesas.observacao + "',");
                    sql.append(i_pagarOutrasDespesas.id_tipopiscofins + ");");
                    stm.execute(sql.toString());

                    for (PagarOutrasDespesasVencimentoVO oPagarOutrasDespesasVencimento : i_pagarOutrasDespesas.vPagarOutrasDespesasVencimento) {
                        sql = new StringBuilder();
                        sql.append("select max(id) as id from pagaroutrasdespesas ");
                        rst2 = stm.executeQuery(sql.toString());

                        if (rst2.next()) {
                            sql = new StringBuilder();
                            sql.append("INSERT INTO pagaroutrasdespesasvencimento(");
                            sql.append("id_pagaroutrasdespesas, datavencimento, valor) ");
                            sql.append("VALUES ( ");
                            sql.append(rst2.getInt("id") + ",");

                            if (oPagarOutrasDespesasVencimento.datavencimento.isEmpty()) {
                                //dataVencimento = new java.sql.Date(new java.util.Date().getTime());
                                sql.append("'" + oPagarOutrasDespesasVencimento.dataVencimento + "', ");
                            } else {
                                sql.append("'" + oPagarOutrasDespesasVencimento.datavencimento + "',");
                            }

                            sql.append(oPagarOutrasDespesasVencimento.valor + ");");
                            stm.execute(sql.toString());
                        }
                    }
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
