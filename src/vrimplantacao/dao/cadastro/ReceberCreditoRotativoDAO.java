package vrimplantacao.dao.cadastro;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrframework.classe.VRException;
import vrimplantacao.classe.file.Log;
import vrimplantacao.classe.file.LogAdicional;
import vrimplantacao.classe.file.LogFileType;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.ClientePreferencialVO;
import vrimplantacao.vo.vrimplantacao.ReceberCreditoRotativoVO;

public class ReceberCreditoRotativoDAO {

    ////////////////// SALVAR PADRÃO
    public void salvarComIdCliente(List<ReceberCreditoRotativoVO> v_receberrotativo, int idLojaVR) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        java.sql.Date data = new java.sql.Date(new java.util.Date().getTime());

        try {

            Conexao.begin();
            stm = Conexao.createStatement();
            ProgressBar.setMaximum(v_receberrotativo.size());
            ProgressBar.setStatus("Importar Receber Credito Rotativo Loja"+idLojaVR+"...");

            for (ReceberCreditoRotativoVO i_receberCreditoRotativo : v_receberrotativo) {
                sql = new StringBuilder();
                sql.append("INSERT INTO recebercreditorotativo( ");
                sql.append("id_loja, dataemissao, numerocupom, ecf, valor, lancamentomanual, ");
                sql.append("observacao, id_situacaorecebercreditorotativo, id_clientepreferencial, ");
                sql.append("datavencimento, matricula, parcela, valorjuros, id_boleto, id_tipolocalcobranca, ");
                sql.append("valormulta, justificativa, exportado) ");
                sql.append("VALUES ( ");
                sql.append(idLojaVR + ", ");

                if ((i_receberCreditoRotativo.dataemissao != null)
                        && (!i_receberCreditoRotativo.dataemissao.trim().isEmpty())) {

                    sql.append("'" + i_receberCreditoRotativo.dataemissao + "', ");
                } else {
                    sql.append("'" + data + "', ");
                }

                sql.append(i_receberCreditoRotativo.numerocupom + ", ");

                sql.append((i_receberCreditoRotativo.ecf == 0 ? null : i_receberCreditoRotativo.ecf) + ", ");
                sql.append(i_receberCreditoRotativo.valor + ", " + i_receberCreditoRotativo.lancamentomanual + ", ");
                
                
                sql.append("'" + i_receberCreditoRotativo.observacao + "', " 
                        + i_receberCreditoRotativo.id_situacaorecebercreditorotativo + ", ");
                sql.append(i_receberCreditoRotativo.getId_clientepreferencial() + ", ");

                if ((i_receberCreditoRotativo.datavencimento != null)
                        && (!i_receberCreditoRotativo.datavencimento.trim().isEmpty())) {

                    sql.append("'" + i_receberCreditoRotativo.datavencimento + "', ");
                } else {
                    sql.append("'" + data + "', ");
                }

                sql.append(i_receberCreditoRotativo.matricula + ", " + i_receberCreditoRotativo.parcela + ", " + i_receberCreditoRotativo.valorjuros + ", ");
                sql.append("NULL, " + i_receberCreditoRotativo.id_tipolocalcobranca + ", " + i_receberCreditoRotativo.valormulta + ", '" + i_receberCreditoRotativo.justificativa + "', ");
                sql.append(i_receberCreditoRotativo.exportado + ");");
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

    public void salvar(List<ReceberCreditoRotativoVO> v_receberrotativo, int idLojaVR, boolean salvarBaixa) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        java.sql.Date data = new java.sql.Date(new java.util.Date().getTime());

        try {

            Conexao.begin();

            stm = Conexao.createStatement();

            ProgressBar.setMaximum(v_receberrotativo.size());
            ProgressBar.setStatus("Importar Receber Credito Rotativo Novo...");

            for (ReceberCreditoRotativoVO i_receberCreditoRotativo : v_receberrotativo) {
                sql = new StringBuilder();
                sql.append("select c.id from clientepreferencial c ");
                sql.append("inner join implantacao.codigoanteriorcli ant on ant.codigoatual = c.id ");
                if (i_receberCreditoRotativo.idClientePreferencialLong > 0) {
                    sql.append("where ant.codigoanterior =" + i_receberCreditoRotativo.idClientePreferencialLong);
                } else {
                    sql.append("where ant.codigoanterior =" + i_receberCreditoRotativo.id_clientepreferencial);
                }
                sql.append("  and id_loja = " + idLojaVR + " ");
                //sql.append(" and novo = true ");
                rst = stm.executeQuery(sql.toString());

                if (rst.next()) {
                    sql = new StringBuilder();
                    sql.append("INSERT INTO recebercreditorotativo( ");
                    sql.append("id_loja, dataemissao, numerocupom, ecf, valor, lancamentomanual, ");
                    sql.append("observacao, id_situacaorecebercreditorotativo, id_clientepreferencial, ");
                    sql.append("datavencimento, matricula, parcela, valorjuros, id_boleto, id_tipolocalcobranca, ");
                    sql.append("valormulta, justificativa, exportado) ");
                    sql.append("VALUES ( ");
                    sql.append(/*i_receberCreditoRotativo.id_loja*/idLojaVR + ", ");

                    if ((i_receberCreditoRotativo.dataemissao != null)
                            && (!i_receberCreditoRotativo.dataemissao.trim().isEmpty())) {

                        sql.append("'" + i_receberCreditoRotativo.dataemissao + "', ");
                    } else {
                        sql.append("'" + data + "', ");
                    }

                    sql.append(i_receberCreditoRotativo.numerocupom + ", ");

                    sql.append((i_receberCreditoRotativo.ecf == 0 ? null : i_receberCreditoRotativo.ecf) + ", ");
                    sql.append(i_receberCreditoRotativo.valor + ", " + i_receberCreditoRotativo.lancamentomanual + ", ");
                    sql.append("'" + i_receberCreditoRotativo.observacao + "', " + i_receberCreditoRotativo.id_situacaorecebercreditorotativo + ", ");
                    sql.append(rst.getInt("id") + ", ");

                    if ((i_receberCreditoRotativo.datavencimento != null)
                            && (!i_receberCreditoRotativo.datavencimento.trim().isEmpty())) {

                        sql.append("'" + i_receberCreditoRotativo.datavencimento + "', ");
                    } else {
                        sql.append("'" + data + "', ");
                    }

                    sql.append(i_receberCreditoRotativo.matricula + ", " + i_receberCreditoRotativo.parcela + ", " + i_receberCreditoRotativo.valorjuros + ", ");
                    sql.append("NULL, " + i_receberCreditoRotativo.id_tipolocalcobranca + ", " + i_receberCreditoRotativo.valormulta + ", '" + i_receberCreditoRotativo.justificativa + "', ");
                    sql.append(i_receberCreditoRotativo.exportado + ") returning id;");

                    //Executa o sql e retorna i id incluso
                    try (ResultSet rst2 = stm.executeQuery(sql.toString())) {
                        if (rst2.next()) {
                            if (salvarBaixa && i_receberCreditoRotativo.valorPago > 0) {
                                String dataPagamento = i_receberCreditoRotativo.dataPagamento != null ? i_receberCreditoRotativo.dataPagamento : i_receberCreditoRotativo.getDatavencimento();

                                sql = new StringBuilder();
                                sql.append("INSERT INTO recebercreditorotativoitem( ");
                                sql.append("id_recebercreditorotativo, valor, valordesconto, valormulta, ");
                                sql.append("valortotal, databaixa, datapagamento, observacao, id_banco, agencia, ");
                                sql.append("conta, id_tiporecebimento, id_usuario, id_loja, id_recebercheque, ");
                                sql.append("id_recebercaixa, id_conciliacaobancarialancamento) ");
                                sql.append("VALUES ( ");
                                sql.append(rst2.getInt("id") + ",");
                                sql.append(i_receberCreditoRotativo.valorPago + ",");
                                sql.append("0, 0, ");
                                sql.append(i_receberCreditoRotativo.valor + ",");
                                sql.append(Utils.quoteSQL(dataPagamento) + ", ");
                                sql.append(Utils.quoteSQL(dataPagamento) + ", ");
                                sql.append(Utils.quoteSQL(i_receberCreditoRotativo.observacao) + ", ");
                                sql.append("804, '', '', 1, 0, ");
                                sql.append(idLojaVR + ", ");
                                sql.append("NULL, NULL, NULL); ");

                                stm.execute(sql.toString());
                            }
                        }
                    }

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

    public void salvar(List<ReceberCreditoRotativoVO> v_receberrotativo, int id_loja) throws Exception {
        salvar(v_receberrotativo, id_loja, false);
    }

    public void salvarComCodicao(List<ReceberCreditoRotativoVO> v_receberrotativo, int idLojaVR) throws Exception {
        StringBuilder sql = null;
        Statement stm = null, stm2 = null;
        ResultSet rst = null, rst2 = null;
        java.sql.Date data = new java.sql.Date(new java.util.Date().getTime());

        try {

            Conexao.begin();

            stm = Conexao.createStatement();
            stm2 = Conexao.createStatement();

            ProgressBar.setMaximum(v_receberrotativo.size());
            ProgressBar.setStatus("Importar Receber Credito Rotativo Com Condição...");

            try (Log log = new Log("ROTATIVO.html", "Relatório da importação do crédito rotativo", LogFileType.HTML)) {
                log.setHeader("Importação de crédito rotativo");
                log.setFooter("Fim da importação de crédito rotativo");

                for (ReceberCreditoRotativoVO i_receberCreditoRotativo : v_receberrotativo) {
                    LogAdicional logAdic = new LogAdicional();
                    logAdic.put("ID Cliente", i_receberCreditoRotativo.getId_clientepreferencial());
                    logAdic.put("Emissao", i_receberCreditoRotativo.getDataemissao());
                    logAdic.put("Vencimento", i_receberCreditoRotativo.getDatavencimento());
                    logAdic.put("Valor", i_receberCreditoRotativo.getValor());
                    logAdic.put("Cupom", i_receberCreditoRotativo.getNumerocupom());
                    logAdic.put("Observacao", i_receberCreditoRotativo.getObservacao());

                    sql = new StringBuilder();
                    sql.append("select c.id from clientepreferencial c ");
                    sql.append("inner join implantacao.codigoanteriorcli ant on ant.codigoatual = c.id ");
                    if (i_receberCreditoRotativo.id_clientepreferencial > 0) {
                        sql.append("where ant.codigoanterior =" + i_receberCreditoRotativo.id_clientepreferencial);
                    } else {
                        sql.append("where ant.codigoanterior =" + i_receberCreditoRotativo.idClientePreferencialLong);
                    }
                    //sql.append("  and id_loja = " + idLojaVR + " ");
                    //sql.append(" and novo = true ");
                    rst = stm.executeQuery(sql.toString());

                    if (rst.next()) {

                        sql = new StringBuilder();
                        sql.append("select id from recebercreditorotativo ");
                        sql.append("where numerocupom = " + i_receberCreditoRotativo.numerocupom + " ");
                        sql.append("  and dataemissao = '" + i_receberCreditoRotativo.dataemissao + "' ");
                        sql.append("  and datavencimento = '" + i_receberCreditoRotativo.datavencimento + "' ");
                        sql.append("  and valor = " + i_receberCreditoRotativo.valor + " ");
                        sql.append("  and id_clientepreferencial = " + rst.getString("id") + " ");
                        sql.append("  and id_loja = " + idLojaVR/*i_receberCreditoRotativo.id_loja*/);
                        rst2 = stm2.executeQuery(sql.toString());

                        if (!rst2.next()) {

                            sql = new StringBuilder();
                            sql.append("INSERT INTO recebercreditorotativo( ");
                            sql.append("id_loja, dataemissao, numerocupom, ecf, valor, lancamentomanual, ");
                            sql.append("observacao, id_situacaorecebercreditorotativo, id_clientepreferencial, ");
                            sql.append("datavencimento, matricula, parcela, valorjuros, id_boleto, id_tipolocalcobranca, ");
                            sql.append("valormulta, justificativa, exportado) ");
                            sql.append("VALUES ( ");
                            sql.append(idLojaVR /*i_receberCreditoRotativo.id_loja*/ + ", ");

                            if ((i_receberCreditoRotativo.dataemissao != null)
                                    && (!i_receberCreditoRotativo.dataemissao.trim().isEmpty())) {

                                sql.append("'" + i_receberCreditoRotativo.dataemissao + "', ");
                            } else {
                                sql.append("'" + data + "', ");
                            }

                            sql.append(i_receberCreditoRotativo.numerocupom + ", ");

                            sql.append((i_receberCreditoRotativo.ecf == 0 ? null : i_receberCreditoRotativo.ecf) + ", ");
                            sql.append(i_receberCreditoRotativo.valor + ", " + i_receberCreditoRotativo.lancamentomanual + ", ");
                            sql.append("'" + i_receberCreditoRotativo.observacao + "', " + i_receberCreditoRotativo.id_situacaorecebercreditorotativo + ", ");
                            sql.append(rst.getInt("id") + ", ");

                            if ((i_receberCreditoRotativo.datavencimento != null)
                                    && (!i_receberCreditoRotativo.datavencimento.trim().isEmpty())) {

                                sql.append("'" + i_receberCreditoRotativo.datavencimento + "', ");
                            } else {
                                sql.append("'" + data + "', ");
                            }

                            sql.append(i_receberCreditoRotativo.matricula + ", " + i_receberCreditoRotativo.parcela + ", " + i_receberCreditoRotativo.valorjuros + ", ");
                            sql.append("NULL, " + i_receberCreditoRotativo.id_tipolocalcobranca + ", " + i_receberCreditoRotativo.valormulta + ", '" + i_receberCreditoRotativo.justificativa + "', ");
                            sql.append(i_receberCreditoRotativo.exportado + ");");

                            stm.execute(sql.toString());
                        } else {
                            log.addLog("ROTATIVO", "Rotativo ja existente", logAdic);
                        }
                    } else {
                        log.addLog("ROTATIVO", "Rotativo nao encontrado", logAdic);
                    }

                    ProgressBar.next();
                }
            }

            stm.close();
            stm2.close();
            Conexao.commit();

        } catch (Exception ex) {

            Conexao.rollback();
            throw ex;
        }
    }

    public void salvarComCnpj(List<ReceberCreditoRotativoVO> v_receberrotativo, int idLojaVR) throws Exception {
        salvarComCnpj(v_receberrotativo, idLojaVR, true);
    }
    
    public void salvarComCnpj(List<ReceberCreditoRotativoVO> v_receberrotativo, int idLojaVR, boolean salvarBaixa) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        java.sql.Date data = new java.sql.Date(new java.util.Date().getTime());

        try {

            Conexao.begin();

            stm = Conexao.createStatement();

            ProgressBar.setMaximum(v_receberrotativo.size());
            ProgressBar.setStatus("Importar Receber Credito Rotativo com Cpf...");

            for (ReceberCreditoRotativoVO i_receberCreditoRotativo : v_receberrotativo) {
                sql = new StringBuilder();
                sql.append("select c.id from clientepreferencial c ");
                sql.append("where cnpj = " + i_receberCreditoRotativo.cnpjCliente);

                rst = stm.executeQuery(sql.toString());

                if (rst.next()) {
                    sql = new StringBuilder();
                    sql.append("INSERT INTO recebercreditorotativo( ");
                    sql.append("id_loja, dataemissao, numerocupom, ecf, valor, lancamentomanual, ");
                    sql.append("observacao, id_situacaorecebercreditorotativo, id_clientepreferencial, ");
                    sql.append("datavencimento, matricula, parcela, valorjuros, id_boleto, id_tipolocalcobranca, ");
                    sql.append("valormulta, justificativa, exportado) ");
                    sql.append("VALUES ( ");
                    sql.append(idLojaVR + ", ");

                    if ((i_receberCreditoRotativo.dataemissao != null)
                            && (!i_receberCreditoRotativo.dataemissao.trim().isEmpty())) {

                        sql.append("'" + i_receberCreditoRotativo.dataemissao + "', ");
                    } else {
                        sql.append("'" + data + "', ");
                    }

                    sql.append(i_receberCreditoRotativo.numerocupom + ", ");

                    sql.append((i_receberCreditoRotativo.ecf == 0 ? null : i_receberCreditoRotativo.ecf) + ", ");
                    sql.append(i_receberCreditoRotativo.valor + ", " + i_receberCreditoRotativo.lancamentomanual + ", ");
                    sql.append("'" + i_receberCreditoRotativo.observacao + "', " + i_receberCreditoRotativo.id_situacaorecebercreditorotativo + ", ");
                    sql.append(rst.getInt("id") + ", ");

                    if ((i_receberCreditoRotativo.datavencimento != null)
                            && (!i_receberCreditoRotativo.datavencimento.trim().isEmpty())) {

                        sql.append("'" + i_receberCreditoRotativo.datavencimento + "', ");
                    } else {
                        sql.append("'" + data + "', ");
                    }

                    sql.append(i_receberCreditoRotativo.matricula + ", " + i_receberCreditoRotativo.parcela + ", " + i_receberCreditoRotativo.valorjuros + ", ");
                    sql.append("NULL, " + i_receberCreditoRotativo.id_tipolocalcobranca + ", " + i_receberCreditoRotativo.valormulta + ", '" + i_receberCreditoRotativo.justificativa + "', ");
                    sql.append(i_receberCreditoRotativo.exportado + ") returning id;");
                    
                    //Executa o sql e retorna i id incluso
                    try (ResultSet rst2 = stm.executeQuery(sql.toString())) {
                        if (rst2.next()) {
                            if (salvarBaixa && i_receberCreditoRotativo.valorPago > 0) {
                                String dataPagamento = i_receberCreditoRotativo.dataPagamento != null ? i_receberCreditoRotativo.dataPagamento : i_receberCreditoRotativo.getDatavencimento();
                                
                                sql = new StringBuilder();
                                sql.append("INSERT INTO recebercreditorotativoitem( ");
                                sql.append("id_recebercreditorotativo, valor, valordesconto, valormulta, ");
                                sql.append("valortotal, databaixa, datapagamento, observacao, id_banco, agencia, ");
                                sql.append("conta, id_tiporecebimento, id_usuario, id_loja, id_recebercheque, ");
                                sql.append("id_recebercaixa, id_conciliacaobancarialancamento) ");
                                sql.append("VALUES ( ");
                                sql.append(rst2.getInt("id") + ",");
                                sql.append(i_receberCreditoRotativo.valorPago + ",");
                                sql.append("0, 0, ");
                                sql.append(i_receberCreditoRotativo.valor + ",");
                                sql.append(Utils.quoteSQL(dataPagamento) + ", ");
                                sql.append(Utils.quoteSQL(dataPagamento) + ", ");
                                sql.append(Utils.quoteSQL(i_receberCreditoRotativo.observacao) + ", ");
                                sql.append("804, '', '', 1, 0, ");
                                sql.append(idLojaVR + ", ");
                                sql.append("NULL, NULL, NULL); ");

                                stm.execute(sql.toString());
                            }
                        }
                    }

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

    public void salvarContaBaixada(List<ReceberCreditoRotativoVO> v_receberCreditoRotativo, int idLoja) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null, rst2 = null;
        java.sql.Date data = new java.sql.Date(new java.util.Date().getTime());

        try {

            Conexao.begin();
            stm = Conexao.createStatement();

            ProgressBar.setMaximum(v_receberCreditoRotativo.size());
            ProgressBar.setStatus("Importando Receber Crédito Rotativo Baixado...");

            for (ReceberCreditoRotativoVO i_receberCreditoRotativo : v_receberCreditoRotativo) {

                sql = new StringBuilder();
                sql.append("SELECT c.id FROM clientepreferencial c ");
                sql.append("inner join implantacao.codigoanteriorcli ant on ant.codigoatual = c.id ");
                sql.append("where ant.codigoanterior = " + i_receberCreditoRotativo.id_clientepreferencial + " ");
                sql.append("and ant.id_loja = " + idLoja);

                rst = stm.executeQuery(sql.toString());

                if (rst.next()) {

                    i_receberCreditoRotativo.id_situacaorecebercreditorotativo = 1;

                    sql = new StringBuilder();
                    sql.append("INSERT INTO recebercreditorotativo( ");
                    sql.append("id_loja, dataemissao, numerocupom, ecf, valor, lancamentomanual, ");
                    sql.append("observacao, id_situacaorecebercreditorotativo, id_clientepreferencial, ");
                    sql.append("datavencimento, matricula, parcela, valorjuros, id_boleto, id_tipolocalcobranca, ");
                    sql.append("valormulta, justificativa, exportado) ");
                    sql.append("VALUES ( ");
                    sql.append(i_receberCreditoRotativo.id_loja + ", ");

                    if ((i_receberCreditoRotativo.dataemissao != null)
                            && (!i_receberCreditoRotativo.dataemissao.trim().isEmpty())) {

                        sql.append("'" + i_receberCreditoRotativo.dataemissao + "', ");
                    } else {
                        sql.append("'" + data + "', ");
                    }

                    sql.append(i_receberCreditoRotativo.numerocupom + ", ");

                    sql.append((i_receberCreditoRotativo.ecf == 0 ? null : i_receberCreditoRotativo.ecf) + ", ");
                    sql.append(i_receberCreditoRotativo.valor + ", " + i_receberCreditoRotativo.lancamentomanual + ", ");
                    sql.append("'" + i_receberCreditoRotativo.observacao + "', " + i_receberCreditoRotativo.id_situacaorecebercreditorotativo + ", ");
                    // CLIENTE VINDO DO SQL
                    sql.append(rst.getInt("id") + ", ");
                    // CLIENTE VINDO DO SQL                    
                    if ((i_receberCreditoRotativo.datavencimento != null)
                            && (!i_receberCreditoRotativo.datavencimento.trim().isEmpty())) {

                        sql.append("'" + i_receberCreditoRotativo.datavencimento + "', ");
                    } else {
                        sql.append("'" + data + "', ");
                    }

                    sql.append(i_receberCreditoRotativo.matricula + ", " + i_receberCreditoRotativo.parcela + ", " + i_receberCreditoRotativo.valorjuros + ", ");
                    sql.append("NULL, " + i_receberCreditoRotativo.id_tipolocalcobranca + ", " + i_receberCreditoRotativo.valormulta + ", '" + i_receberCreditoRotativo.justificativa + "', ");
                    sql.append(i_receberCreditoRotativo.exportado + ");");

                    stm.execute(sql.toString());

                    sql = new StringBuilder();
                    sql.append("select max(id) as idReceber from recebercreditorotativo where id_loja = " + idLoja + ";");

                    rst2 = stm.executeQuery(sql.toString());

                    if (rst2.next()) {

                        sql = new StringBuilder();
                        sql.append("INSERT INTO recebercreditorotativoitem( ");
                        sql.append("id_recebercreditorotativo, valor, valordesconto, valormulta, ");
                        sql.append("valortotal, databaixa, datapagamento, observacao, id_banco, agencia, ");
                        sql.append("conta, id_tiporecebimento, id_usuario, id_loja, id_recebercheque, ");
                        sql.append("id_recebercaixa, id_conciliacaobancarialancamento) ");
                        sql.append("VALUES ( ");
                        sql.append(rst2.getInt("idReceber") + ",");
                        sql.append(i_receberCreditoRotativo.valorPago + ",");
                        sql.append("0, 0, ");
                        sql.append(i_receberCreditoRotativo.valor + ",");
                        sql.append("'" + i_receberCreditoRotativo.dataPagamento + "', ");
                        sql.append("'" + i_receberCreditoRotativo.dataPagamento + "', ");
                        sql.append("'" + i_receberCreditoRotativo.observacao + "', ");
                        sql.append("804, '', '', 1, 0, ");
                        sql.append(idLoja + ", ");
                        sql.append("NULL, NULL, NULL); ");

                        stm.execute(sql.toString());

                    }
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

    //////////////////////
    //////////////// SALVAR PROGRAMAS
    public void salvarMilenio(List<ReceberCreditoRotativoVO> v_receberrotativo, int id_loja) throws Exception {

        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;

        try {

            Conexao.begin();

            stm = Conexao.createStatement();

            ProgressBar.setMaximum(v_receberrotativo.size());
            ProgressBar.setStatus("Importar Receber Credito Rotativo...");

            for (ReceberCreditoRotativoVO i_receberCreditoRotativo : v_receberrotativo) {

                sql = new StringBuilder();
                sql.append("SELECT c.id FROM clientepreferencial c ");
                sql.append("where c.cnpj = " + i_receberCreditoRotativo.cnpjCliente);
                //sql.append("INNER JOIN implantacao.codigoanteriorcli ant ");
                //sql.append("ON ant.codigoanterior = c.id ");
                //sql.append("WHERE ant.codigoagente = " + i_receberCreditoRotativo.id_clientepreferencial+" ");
                //sql.append("AND ant.id_loja = 1 ");

                rst = stm.executeQuery(sql.toString());

                if (rst.next()) {

                    sql = new StringBuilder();
                    sql.append("INSERT INTO recebercreditorotativo( ");
                    sql.append("id_loja, dataemissao, numerocupom, ecf, valor, lancamentomanual, ");
                    sql.append("observacao, id_situacaorecebercreditorotativo, id_clientepreferencial, ");
                    sql.append("datavencimento, matricula, parcela, valorjuros, id_boleto, id_tipolocalcobranca, ");
                    sql.append("valormulta, justificativa, exportado) ");
                    sql.append("VALUES ( ");
                    sql.append(i_receberCreditoRotativo.id_loja + ", '" + i_receberCreditoRotativo.dataemissao + "', " + i_receberCreditoRotativo.numerocupom + ", ");
                    sql.append("NULL, " + i_receberCreditoRotativo.valor + ", " + i_receberCreditoRotativo.lancamentomanual + ", ");
                    sql.append("'" + i_receberCreditoRotativo.observacao + "', " + i_receberCreditoRotativo.id_situacaorecebercreditorotativo + ", ");
                    sql.append(rst.getInt("id") + ", '" + i_receberCreditoRotativo.datavencimento + "', ");
                    sql.append(i_receberCreditoRotativo.matricula + ", " + i_receberCreditoRotativo.parcela + ", " + i_receberCreditoRotativo.valorjuros + ", ");
                    sql.append("NULL, " + i_receberCreditoRotativo.id_tipolocalcobranca + ", " + i_receberCreditoRotativo.valormulta + ", '" + i_receberCreditoRotativo.justificativa + "', ");
                    sql.append(i_receberCreditoRotativo.exportado + ");");

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

    public void salvarJMaster(List<ReceberCreditoRotativoVO> v_receberrotativo, int id_loja) throws Exception {

        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        File f = new File("C:\\vr\\importacao\\cliente_n_encontrado.txt");
        FileWriter fw = new FileWriter(f);
        BufferedWriter bw = new BufferedWriter(fw);

        try {

            Conexao.begin();

            stm = Conexao.createStatement();

            ProgressBar.setMaximum(v_receberrotativo.size());
            ProgressBar.setStatus("Importar Receber Credito Rotativo...");

            for (ReceberCreditoRotativoVO i_receberCreditoRotativo : v_receberrotativo) {

                sql = new StringBuilder();
                sql.append("SELECT c.id FROM clientepreferencial c ");
                sql.append("WHERE c.id = " + i_receberCreditoRotativo.id_clientepreferencial);

                rst = stm.executeQuery(sql.toString());

                if (rst.next()) {

                    sql = new StringBuilder();
                    sql.append("INSERT INTO recebercreditorotativo( ");
                    sql.append("id_loja, dataemissao, numerocupom, ecf, valor, lancamentomanual, ");
                    sql.append("observacao, id_situacaorecebercreditorotativo, id_clientepreferencial, ");
                    sql.append("datavencimento, matricula, parcela, valorjuros, id_boleto, id_tipolocalcobranca, ");
                    sql.append("valormulta, justificativa, exportado) ");
                    sql.append("VALUES ( ");
                    sql.append(i_receberCreditoRotativo.id_loja + ", '" + i_receberCreditoRotativo.dataemissao + "', " + i_receberCreditoRotativo.numerocupom + ", ");
                    sql.append("NULL, " + i_receberCreditoRotativo.valor + ", " + i_receberCreditoRotativo.lancamentomanual + ", ");
                    sql.append("'" + i_receberCreditoRotativo.observacao + "', " + i_receberCreditoRotativo.id_situacaorecebercreditorotativo + ", ");
                    sql.append(rst.getInt("id") + ", '" + i_receberCreditoRotativo.datavencimento + "', ");
                    sql.append(i_receberCreditoRotativo.matricula + ", " + i_receberCreditoRotativo.parcela + ", " + i_receberCreditoRotativo.valorjuros + ", ");
                    sql.append("NULL, " + i_receberCreditoRotativo.id_tipolocalcobranca + ", " + i_receberCreditoRotativo.valormulta + ", '" + i_receberCreditoRotativo.justificativa + "', ");
                    sql.append(i_receberCreditoRotativo.exportado + ");");

                    stm.execute(sql.toString());

                } else {
                    bw.write(i_receberCreditoRotativo.id_clientepreferencial + ";");
                    bw.newLine();

                }

                ProgressBar.next();
            }

            stm.close();
            Conexao.commit();

        } catch (Exception ex) {
            bw.flush();
            bw.close();
            Conexao.rollback();
            throw ex;
        }
    }

    public void salvarShi(List<ReceberCreditoRotativoVO> v_receberrotativo, int id_loja) throws Exception {

        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;

        try {

            Conexao.begin();

            stm = Conexao.createStatement();

            ProgressBar.setMaximum(v_receberrotativo.size());
            ProgressBar.setStatus("Importar Receber Credito Rotativo...");

            for (ReceberCreditoRotativoVO i_receberCreditoRotativo : v_receberrotativo) {

                sql = new StringBuilder();
                sql.append("SELECT c.id FROM clientepreferencial c ");
                sql.append("where c.id = " + i_receberCreditoRotativo.id_clientepreferencial);

                rst = stm.executeQuery(sql.toString());

                if (rst.next()) {

                    sql = new StringBuilder();
                    sql.append("INSERT INTO recebercreditorotativo( ");
                    sql.append("id_loja, dataemissao, numerocupom, ecf, valor, lancamentomanual, ");
                    sql.append("observacao, id_situacaorecebercreditorotativo, id_clientepreferencial, ");
                    sql.append("datavencimento, matricula, parcela, valorjuros, id_boleto, id_tipolocalcobranca, ");
                    sql.append("valormulta, justificativa, exportado) ");
                    sql.append("VALUES ( ");
                    sql.append(i_receberCreditoRotativo.id_loja + ", '" + i_receberCreditoRotativo.dataemissao + "', " + i_receberCreditoRotativo.numerocupom + ", ");
                    sql.append("NULL, " + i_receberCreditoRotativo.valor + ", " + i_receberCreditoRotativo.lancamentomanual + ", ");
                    sql.append("'" + i_receberCreditoRotativo.observacao + "', " + i_receberCreditoRotativo.id_situacaorecebercreditorotativo + ", ");
                    sql.append(rst.getInt("id") + ", '" + i_receberCreditoRotativo.datavencimento + "', ");
                    sql.append(i_receberCreditoRotativo.matricula + ", " + i_receberCreditoRotativo.parcela + ", " + i_receberCreditoRotativo.valorjuros + ", ");
                    sql.append("NULL, " + i_receberCreditoRotativo.id_tipolocalcobranca + ", " + i_receberCreditoRotativo.valormulta + ", '" + i_receberCreditoRotativo.justificativa + "', ");
                    sql.append(i_receberCreditoRotativo.exportado + ");");

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

    public void salvarSysPdvComIdCnpjNome(List<ReceberCreditoRotativoVO> v_receberrotativo, int id_loja) throws Exception {

        StringBuilder sql = null;
        Statement stm = null, stm2 = null, stm3 = null;
        ResultSet rst = null, rst2 = null, rst3 = null;

        try {

            Conexao.begin();

            stm = Conexao.createStatement();
            stm2 = Conexao.createStatement();
            stm3 = Conexao.createStatement();

            ProgressBar.setMaximum(v_receberrotativo.size());
            ProgressBar.setStatus("Importar Receber Credito Rotativo...Loja " + id_loja + "...");

            for (ReceberCreditoRotativoVO i_receberCreditoRotativo : v_receberrotativo) {

                sql = new StringBuilder();
                sql.append("select c.id from clientepreferencial c ");
                sql.append("inner join implantacao.codigoanteriorcli ant on ant.codigoatual = c.id ");
                sql.append("where ant.codigoanterior = " + i_receberCreditoRotativo.id_clientepreferencial + " ");
                sql.append("and ant.id_loja = " + id_loja + ";");

                rst = stm.executeQuery(sql.toString());

                if (rst.next()) {

                    sql = new StringBuilder();
                    sql.append("INSERT INTO recebercreditorotativo( ");
                    sql.append("id_loja, dataemissao, numerocupom, ecf, valor, lancamentomanual, ");
                    sql.append("observacao, id_situacaorecebercreditorotativo, id_clientepreferencial, ");
                    sql.append("datavencimento, matricula, parcela, valorjuros, id_boleto, id_tipolocalcobranca, ");
                    sql.append("valormulta, justificativa, exportado) ");
                    sql.append("VALUES ( ");
                    sql.append(i_receberCreditoRotativo.id_loja + ", '" + i_receberCreditoRotativo.dataemissao + "', " + i_receberCreditoRotativo.numerocupom + ", ");
                    sql.append("NULL, " + i_receberCreditoRotativo.valor + ", " + i_receberCreditoRotativo.lancamentomanual + ", ");
                    sql.append("'" + i_receberCreditoRotativo.observacao + "', " + i_receberCreditoRotativo.id_situacaorecebercreditorotativo + ", ");
                    sql.append(rst.getInt("id") + ", '" + i_receberCreditoRotativo.datavencimento + "', ");
                    sql.append(i_receberCreditoRotativo.matricula + ", " + i_receberCreditoRotativo.parcela + ", " + i_receberCreditoRotativo.valorjuros + ", ");
                    sql.append("NULL, " + i_receberCreditoRotativo.id_tipolocalcobranca + ", " + i_receberCreditoRotativo.valormulta + ", '" + i_receberCreditoRotativo.justificativa + "', ");
                    sql.append(i_receberCreditoRotativo.exportado + ");");

                    stm.execute(sql.toString());
                } else {

                    if (i_receberCreditoRotativo.cnpjCliente != -1) {

                        sql = new StringBuilder();
                        sql.append("select id from clientepreferencial ");
                        sql.append("where cnpj = " + i_receberCreditoRotativo.cnpjCliente);

                        rst2 = stm2.executeQuery(sql.toString());

                        if (rst2.next()) {

                            sql = new StringBuilder();
                            sql.append("INSERT INTO recebercreditorotativo( ");
                            sql.append("id_loja, dataemissao, numerocupom, ecf, valor, lancamentomanual, ");
                            sql.append("observacao, id_situacaorecebercreditorotativo, id_clientepreferencial, ");
                            sql.append("datavencimento, matricula, parcela, valorjuros, id_boleto, id_tipolocalcobranca, ");
                            sql.append("valormulta, justificativa, exportado) ");
                            sql.append("VALUES ( ");
                            sql.append(i_receberCreditoRotativo.id_loja + ", '" + i_receberCreditoRotativo.dataemissao + "', " + i_receberCreditoRotativo.numerocupom + ", ");
                            sql.append("NULL, " + i_receberCreditoRotativo.valor + ", " + i_receberCreditoRotativo.lancamentomanual + ", ");
                            sql.append("'" + i_receberCreditoRotativo.observacao + "', " + i_receberCreditoRotativo.id_situacaorecebercreditorotativo + ", ");
                            sql.append(rst2.getInt("id") + ", '" + i_receberCreditoRotativo.datavencimento + "', ");
                            sql.append(i_receberCreditoRotativo.matricula + ", " + i_receberCreditoRotativo.parcela + ", " + i_receberCreditoRotativo.valorjuros + ", ");
                            sql.append("NULL, " + i_receberCreditoRotativo.id_tipolocalcobranca + ", " + i_receberCreditoRotativo.valormulta + ", '" + i_receberCreditoRotativo.justificativa + "', ");
                            sql.append(i_receberCreditoRotativo.exportado + ");");

                            stm2.execute(sql.toString());

                        } else {

                            if (!i_receberCreditoRotativo.nomeCliente.trim().isEmpty()) {

                                sql = new StringBuilder();
                                sql.append("select id from clientepreferencial ");
                                sql.append("where nome '" + i_receberCreditoRotativo.nomeCliente + "'");

                                rst3 = stm3.executeQuery(sql.toString());

                                if (rst3.next()) {
                                    sql = new StringBuilder();
                                    sql.append("INSERT INTO recebercreditorotativo( ");
                                    sql.append("id_loja, dataemissao, numerocupom, ecf, valor, lancamentomanual, ");
                                    sql.append("observacao, id_situacaorecebercreditorotativo, id_clientepreferencial, ");
                                    sql.append("datavencimento, matricula, parcela, valorjuros, id_boleto, id_tipolocalcobranca, ");
                                    sql.append("valormulta, justificativa, exportado) ");
                                    sql.append("VALUES ( ");
                                    sql.append(i_receberCreditoRotativo.id_loja + ", '" + i_receberCreditoRotativo.dataemissao + "', " + i_receberCreditoRotativo.numerocupom + ", ");
                                    sql.append("NULL, " + i_receberCreditoRotativo.valor + ", " + i_receberCreditoRotativo.lancamentomanual + ", ");
                                    sql.append("'" + i_receberCreditoRotativo.observacao + "', " + i_receberCreditoRotativo.id_situacaorecebercreditorotativo + ", ");
                                    sql.append(rst3.getInt("id") + ", '" + i_receberCreditoRotativo.datavencimento + "', ");
                                    sql.append(i_receberCreditoRotativo.matricula + ", " + i_receberCreditoRotativo.parcela + ", " + i_receberCreditoRotativo.valorjuros + ", ");
                                    sql.append("NULL, " + i_receberCreditoRotativo.id_tipolocalcobranca + ", " + i_receberCreditoRotativo.valormulta + ", '" + i_receberCreditoRotativo.justificativa + "', ");
                                    sql.append(i_receberCreditoRotativo.exportado + ");");

                                    stm3.execute(sql.toString());
                                }
                            }
                        }
                    }
                }
                ProgressBar.next();
            }

            stm.close();
            stm2.close();
            stm3.close();
            Conexao.commit();

        } catch (Exception ex) {

            Conexao.rollback();
            throw ex;
        }
    }

    public void acertarCreditoRotativoMilenio(List<ReceberCreditoRotativoVO> v_receberrotativo, int id_loja) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;

        try {

            Conexao.begin();

            stm = Conexao.createStatement();

            ProgressBar.setMaximum(v_receberrotativo.size());
            ProgressBar.setStatus("Acertar Crédito Rotativo Milenio...");

            for (ReceberCreditoRotativoVO i_receberCreditoRotativo : v_receberrotativo) {
                sql = new StringBuilder();
                sql.append("select cr.id from recebercreditorotativo cr ");
                sql.append("inner join clientepreferencial c on c.id = cr.id_clientepreferencial ");
                sql.append("where cr.dataemissao = '" + i_receberCreditoRotativo.dataemissao + "' ");
                sql.append("  and cr.datavencimento = '" + i_receberCreditoRotativo.datavencimento + "' ");
                sql.append("  and c.cnpj = " + i_receberCreditoRotativo.cnpjCliente + " ");
                sql.append("  and cr.numerocupom = " + i_receberCreditoRotativo.numerocupom + " ");
                sql.append("  and cr.id_loja = " + id_loja + " ");
                sql.append("  and cr.id_situacaorecebercreditorotativo = 0 ");
                rst = stm.executeQuery(sql.toString());

                if (rst.next()) {
                    sql = new StringBuilder();
                    sql.append("update recebercreditorotativo set ");
                    sql.append("valor = " + i_receberCreditoRotativo.valor + ", ");
                    sql.append("valorjuros = " + i_receberCreditoRotativo.valorjuros + " ");
                    sql.append("where id = " + rst.getInt("id") + ";");
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

    public void alterarCreditoRotativoPeloNomeCliente(List<ClientePreferencialVO> v_clientePreferencial, int id_loja) throws Exception {

        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        int Linha = 0;
        String Erro = "";

        try {
            Conexao.begin();

            stm = Conexao.createStatement();

            ProgressBar.setMaximum(v_clientePreferencial.size());
            ProgressBar.setStatus("Acertando Crédito Rotativo...Nome Cliente...");

            for (ClientePreferencialVO i_clientePreferencial : v_clientePreferencial) {

                sql = new StringBuilder();
                sql.append("SELECT c.id, c.cnpj from clientepreferencial c ");
                sql.append("inner join implantacao.codigoanteriorcli ant on ant.codigoatual = c.id ");
                sql.append("WHERE c.nome like '%" + i_clientePreferencial.nome + "%' ");
                sql.append("and c.id = " + i_clientePreferencial.id + " ");
                sql.append("and ant.id_loja = " + id_loja);
                rst = stm.executeQuery(sql.toString());

                if (rst.next()) {

                    sql = new StringBuilder();
                    sql.append("update recebercreditorotativo set ");
                    sql.append("id_clientepreferencial = " + rst.getInt("id") + " ");
                    sql.append("where id_clientepreferencial in (");
                    sql.append("select codigoatual from implantacao.codigoanteriorcli ");
                    sql.append("where codigoanterior = " + rst.getInt("id") + " ");
                    sql.append("and id_loja = " + id_loja + " ");
                    sql.append("and codigoatual <> codigoanterior);");
                    sql.append("update clientepreferencial set ");
                    sql.append("id_situacaocadastro = 0 ");
                    sql.append("where id in (select codigoatual ");
                    sql.append("from implantacao.codigoanteriorcli ");
                    sql.append("where codigoanterior = " + rst.getString("id") + " ");
                    sql.append("and codigoatual <> codigoanterior ");
                    sql.append("and id_loja = " + id_loja + ");");
                    Erro = sql.toString();
                    stm.execute(sql.toString());

                }
                ProgressBar.next();
            }

            stm.close();
            Conexao.commit();

        } catch (Exception ex) {
            Conexao.rollback();
            if (Linha > 0) {
                throw new VRException(" SQL: " + Erro + " Cliente: " + Linha + " " + ex.getMessage());
            } else {
                throw ex;
            }
        }
    }

}
