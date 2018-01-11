package vrimplantacao.dao.cadastro;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrframework.classe.Util;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.ReceberChequeHistoricoVO;
import vrimplantacao.vo.vrimplantacao.ReceberChequeItemVO;
import vrimplantacao.vo.vrimplantacao.ReceberChequeVO;
import vrimplantacao2.utils.sql.SQLBuilder;


public class ReceberChequeDAO {
    
    public boolean alterarCadastroCliente = false;
    
    public void salvar(List<ReceberChequeVO> vReceberCheque, int idLoja) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        
        try {
            Conexao.begin();
            
            stm = Conexao.createStatement();
            
            ProgressBar.setMaximum(vReceberCheque.size());
            ProgressBar.setStatus("Importar Receber Cheque...");
            
            for (ReceberChequeVO i_receberCheque : vReceberCheque) {
                
                sql = new StringBuilder();
                sql.append("INSERT INTO recebercheque( ");
                sql.append("id_loja, cpf, numerocheque, id_banco, agencia, conta, data, ");
                sql.append("id_plano, numerocupom, ecf, valor, datadeposito, lancamentomanual, ");
                sql.append("rg, telefone, nome, observacao, id_situacaorecebercheque, id_tipolocalcobranca, ");
                sql.append("cmc7, datadevolucao, id_tipoalinea, id_tipoinscricao, dataenviocobranca, ");
                sql.append("valorpagarfornecedor, id_boleto, operadorclientebloqueado, operadorexcedelimite, ");
                sql.append("operadorproblemacheque, operadorchequebloqueado, valorjuros, ");
                sql.append("id_tipovistaprazo, justificativa, valoracrescimo, valorinicial) ");
                sql.append("VALUES ( ");
                sql.append(idLoja+",");
                sql.append(i_receberCheque.cpf+",");
                sql.append(i_receberCheque.numerocheque+",");
                sql.append(i_receberCheque.id_banco+",");
                sql.append("'"+i_receberCheque.agencia+"',");
                sql.append("'"+i_receberCheque.conta+"',");
                sql.append("'"+i_receberCheque.data+"',");
                sql.append(i_receberCheque.id_plano+",");
                sql.append(i_receberCheque.numerocupom+",");
                sql.append(i_receberCheque.ecf+",");
                sql.append(i_receberCheque.valor+",");
                sql.append("'"+i_receberCheque.datadeposito+"',");
                sql.append(i_receberCheque.lancamentomanual+",");
                sql.append("'"+i_receberCheque.rg+"',");
                sql.append("'"+i_receberCheque.telefone+"',");
                sql.append("'"+i_receberCheque.nome+"',");
                sql.append("'"+i_receberCheque.observacao+"',");
                sql.append(i_receberCheque.id_situacaorecebercheque+",");
                sql.append(i_receberCheque.id_tipolocalcobranca+",");
                sql.append("'"+i_receberCheque.cmc7+"',");
                sql.append((i_receberCheque.datadevolucao == "" ? null : "'"+i_receberCheque.datadevolucao+"'")+",");
                sql.append(i_receberCheque.id_tipoalinea+", ");
                sql.append(i_receberCheque.id_tipoinscricao+", ");
                sql.append(i_receberCheque.dataenviocobranca+",");
                sql.append(i_receberCheque.valorpagarfornecedor+",");
                sql.append((i_receberCheque.id_boleto == 0 ? null : i_receberCheque.id_boleto)+",");
                sql.append("'"+i_receberCheque.operadorclientebloqueado+"',");
                sql.append("'"+i_receberCheque.operadorexcedelimite+"',");
                sql.append("'"+i_receberCheque.operadorproblemacheque+"',");
                sql.append("'"+i_receberCheque.operadorchequebloqueado+"',");
                sql.append(i_receberCheque.valorjuros+",");
                sql.append(i_receberCheque.id_tipovistaprazo+",");
                sql.append("'"+i_receberCheque.justificativa+"',");
                sql.append(i_receberCheque.valoracrescimo+",");
                sql.append(i_receberCheque.valorinicial);                
                sql.append(");");
                stm.execute(sql.toString());
                
                if (alterarCadastroCliente) {
                    sql = new StringBuilder();
                    sql.append("update clientepreferencial set bloqueado = true ");
                    sql.append("where id = " + i_receberCheque.idCliente + ";");
                    stm.execute(sql.toString());
                }
                
                ProgressBar.next();
            }
            stm.close();
            Conexao.commit();
        } catch(Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void salvarComCondicao(List<ReceberChequeVO> vReceberCheque, int idLoja) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        
        try {
            Conexao.begin();
            
            stm = Conexao.createStatement();
            
            ProgressBar.setMaximum(vReceberCheque.size());
            ProgressBar.setStatus("Importar Receber Cheque Condição...");
            
            for (ReceberChequeVO i_receberCheque : vReceberCheque) {
                sql = new StringBuilder();
                sql.append("select * from recebercheque ");
                sql.append(" where id_loja = " + idLoja + " ");
                sql.append("   and cpf = " + i_receberCheque.cpf + " ");
                sql.append("   and numerocheque = " + i_receberCheque.numerocheque + " ");
                sql.append("   and ecf = " + i_receberCheque.ecf + " ");
                sql.append("   and valor = " + i_receberCheque.valor + " ");
                sql.append("   and data = '" + i_receberCheque.data + "' ");
                sql.append("   and datadeposito = '" + i_receberCheque.datadeposito+ "' ");
                sql.append("   and nome = '" + i_receberCheque.nome + "' ");
                rst = stm.executeQuery(sql.toString());
                
                if (!rst.next()) {
                    sql = new StringBuilder();
                    sql.append("INSERT INTO recebercheque( ");
                    sql.append("id_loja, cpf, numerocheque, id_banco, agencia, conta, data, ");
                    sql.append("id_plano, numerocupom, ecf, valor, datadeposito, lancamentomanual, ");
                    sql.append("rg, telefone, nome, observacao, id_situacaorecebercheque, id_tipolocalcobranca, ");
                    sql.append("cmc7, datadevolucao, id_tipoalinea, id_tipoinscricao, dataenviocobranca, ");
                    sql.append("valorpagarfornecedor, id_boleto, operadorclientebloqueado, operadorexcedelimite, ");
                    sql.append("operadorproblemacheque, operadorchequebloqueado, valorjuros, ");
                    sql.append("id_tipovistaprazo, justificativa, valoracrescimo, valorinicial) ");
                    sql.append("VALUES ( ");
                    sql.append(idLoja + ",");
                    sql.append(i_receberCheque.cpf + ",");
                    sql.append(i_receberCheque.numerocheque + ",");
                    sql.append(i_receberCheque.id_banco + ",");
                    sql.append("'" + i_receberCheque.agencia + "',");
                    sql.append("'" + i_receberCheque.conta + "',");
                    sql.append("'" + i_receberCheque.data + "',");
                    sql.append(i_receberCheque.id_plano + ",");
                    sql.append(i_receberCheque.numerocupom + ",");
                    sql.append(i_receberCheque.ecf + ",");
                    sql.append(i_receberCheque.valor + ",");
                    sql.append("'" + i_receberCheque.datadeposito + "',");
                    sql.append(i_receberCheque.lancamentomanual + ",");
                    sql.append("'" + i_receberCheque.rg + "',");
                    sql.append("'" + i_receberCheque.telefone + "',");
                    sql.append("'" + i_receberCheque.nome + "',");
                    sql.append("'" + i_receberCheque.observacao + "',");
                    sql.append(i_receberCheque.id_situacaorecebercheque + ",");
                    sql.append(i_receberCheque.id_tipolocalcobranca + ",");
                    sql.append("'" + i_receberCheque.cmc7 + "',");
                    sql.append((i_receberCheque.datadevolucao == "" ? null : "'" + i_receberCheque.datadevolucao + "'") + ",");
                    sql.append(i_receberCheque.id_tipoalinea + ", ");
                    sql.append(i_receberCheque.id_tipoinscricao + ", ");
                    sql.append(i_receberCheque.dataenviocobranca + ",");
                    sql.append(i_receberCheque.valorpagarfornecedor + ",");
                    sql.append((i_receberCheque.id_boleto == 0 ? null : i_receberCheque.id_boleto) + ",");
                    sql.append("'" + i_receberCheque.operadorclientebloqueado + "',");
                    sql.append("'" + i_receberCheque.operadorexcedelimite + "',");
                    sql.append("'" + i_receberCheque.operadorproblemacheque + "',");
                    sql.append("'" + i_receberCheque.operadorchequebloqueado + "',");
                    sql.append(i_receberCheque.valorjuros + ",");
                    sql.append(i_receberCheque.id_tipovistaprazo + ",");
                    sql.append("'" + i_receberCheque.justificativa + "',");
                    sql.append(i_receberCheque.valoracrescimo + ",");
                    sql.append(i_receberCheque.valorinicial);
                    sql.append(");");
                    stm.execute(sql.toString());

                    if (alterarCadastroCliente) {
                        sql = new StringBuilder();
                        sql.append("update clientepreferencial set bloqueado = true ");
                        sql.append("where id = " + i_receberCheque.idCliente + ";");
                        stm.execute(sql.toString());
                    }
                }
                
                ProgressBar.next();
            }
            stm.close();
            Conexao.commit();
        } catch(Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }
    
    public void salvar2(List<ReceberChequeVO> vReceberCheque, int idLoja) throws Exception {
        Conexao.begin();
        String statm = "";
        try {            
            try (Statement stm = Conexao.createStatement()) {
                Set<Integer> bancos = new LinkedHashSet<>();
                try (ResultSet rst = stm.executeQuery(
                        "select id from banco order by id"
                )) {
                    while (rst.next()) {
                        bancos.add(rst.getInt("id"));
                    }
                }                
                
                ProgressBar.setMaximum(vReceberCheque.size());
                ProgressBar.setStatus("Importar Receber Cheque Condição Loja " + idLoja);
                System.out.println("QTD: " + vReceberCheque.size());
                for (ReceberChequeVO i_receberCheque : vReceberCheque) {
                    statm = "select id from recebercheque \n" +
                            " where id_loja = " + idLoja + "\n" +
                            "   and cpf = " + i_receberCheque.cpf + " " +
                            "   and numerocheque = " + i_receberCheque.numerocheque + " " +
                            "   and valor = " + i_receberCheque.valor + " " +
                            "   and data = '" + i_receberCheque.data + "' " +
                            "   and datadeposito = '" + i_receberCheque.datadeposito+ "' ";
                    try (ResultSet rst = stm.executeQuery(statm)) {
                    

                        if (!rst.next()) {
                            SQLBuilder sql = new SQLBuilder();
                            sql.setTableName("recebercheque");
                            sql.put("id_loja", idLoja);
                            sql.put("cpf", i_receberCheque.cpf);
                            sql.put("numerocheque", i_receberCheque.numerocheque);
                            if (bancos.contains(i_receberCheque.getId_banco())) {
                                sql.put("id_banco", i_receberCheque.id_banco);
                            } else {
                                sql.put("id_banco", 999);
                            }
                            sql.put("agencia", i_receberCheque.agencia);
                            sql.put("conta", i_receberCheque.conta);
                            sql.put("data", i_receberCheque.data);
                            sql.put("id_plano", i_receberCheque.id_plano);
                            sql.put("numerocupom", i_receberCheque.numerocupom);
                            sql.put("ecf", i_receberCheque.ecf);
                            sql.put("valor", i_receberCheque.valor);
                            sql.put("datadeposito", i_receberCheque.datadeposito);
                            sql.put("lancamentomanual", i_receberCheque.lancamentomanual);
                            sql.put("rg", i_receberCheque.rg);
                            sql.put("telefone", i_receberCheque.telefone);
                            sql.put("nome", i_receberCheque.nome);
                            sql.put("observacao", i_receberCheque.observacao);
                            sql.put("id_situacaorecebercheque", i_receberCheque.id_situacaorecebercheque);
                            sql.put("id_tipolocalcobranca", i_receberCheque.id_tipolocalcobranca);
                            sql.put("cmc7", i_receberCheque.cmc7);
                            if ("".equals(i_receberCheque.datadevolucao)) {
                                sql.putNull("datadevolucao");
                            } else {
                                sql.put("datadevolucao", i_receberCheque.datadevolucao);
                            }
                            sql.put("id_tipoalinea", i_receberCheque.id_tipoalinea);
                            sql.put("id_tipoinscricao", i_receberCheque.id_tipoinscricao);
                            sql.put("dataenviocobranca", i_receberCheque.dataenviocobranca);
                            sql.put("valorpagarfornecedor", i_receberCheque.valorpagarfornecedor);
                            sql.put("id_boleto", (i_receberCheque.id_boleto == 0 ? null : i_receberCheque.id_boleto));
                            sql.put("operadorclientebloqueado", i_receberCheque.operadorclientebloqueado);
                            sql.put("operadorexcedelimite", i_receberCheque.operadorexcedelimite);
                            sql.put("operadorproblemacheque", i_receberCheque.operadorproblemacheque);
                            sql.put("operadorchequebloqueado", i_receberCheque.operadorchequebloqueado);
                            sql.put("valorjuros", i_receberCheque.valorjuros);
                            sql.put("id_tipovistaprazo", i_receberCheque.id_tipovistaprazo);
                            sql.put("justificativa", i_receberCheque.justificativa);
                            sql.put("valoracrescimo", i_receberCheque.valoracrescimo);
                            sql.put("valorinicial", i_receberCheque.valorinicial);
                            sql.getReturning().add("id");
                            
                            statm = sql.getInsert();
                            try (Statement stm2 = Conexao.createStatement()) {
                                try (ResultSet rst2 = stm2.executeQuery(
                                        statm
                                )) {
                                    if (rst2.next()) {
                                        i_receberCheque.setId(rst2.getInt("id"));
                                        
                                        try (Statement stm3 = Conexao.createStatement()) {
                                            double totalPago = 0;
                                            for (ReceberChequeItemVO baixa: i_receberCheque.getvBaixa()) {
                                                Date dataCheque;
                                                if (!"".equals(i_receberCheque.getData()) && i_receberCheque.getData() != null) {
                                                    dataCheque = new SimpleDateFormat("yyyy-MM-dd").parse(i_receberCheque.getData());
                                                } else {
                                                    dataCheque = new Date();
                                                }
                                                SQLBuilder sqlBaixa = new SQLBuilder();
                                                sqlBaixa.setTableName("receberchequeitem");
                                                sqlBaixa.put("id_recebercheque", i_receberCheque.getId());
                                                sqlBaixa.put("valor", baixa.getValor());
                                                sqlBaixa.put("valordesconto", baixa.getValordesconto());
                                                sqlBaixa.put("valorjuros", baixa.getValorjuros());
                                                sqlBaixa.put("valormulta", baixa.getValormulta());
                                                sqlBaixa.put("valortotal", baixa.getValortotal());
                                                sqlBaixa.put("databaixa", baixa.getDatabaixa() != null ? baixa.getDatabaixa() : dataCheque);
                                                sqlBaixa.put("datapagamento", baixa.getDatapagamento()!= null ? baixa.getDatapagamento() : dataCheque);
                                                sqlBaixa.put("observacao", baixa.getObservacao());
                                                sqlBaixa.put("id_banco", baixa.getId_banco());
                                                sqlBaixa.put("agencia", baixa.getAgencia());
                                                sqlBaixa.put("conta", baixa.getConta());
                                                sqlBaixa.put("id_tiporecebimento", baixa.getId_tiporecebimento());
                                                    
                                                if (baixa.getId_pagarfornecedorparcela() > 0) {
                                                    sqlBaixa.put("id_pagarfornecedorparcela", baixa.getId_pagarfornecedorparcela());
                                                }
                                                if (baixa.getId_conciliacaobancarialancamento() > 0) {
                                                    sqlBaixa.put("id_conciliacaobancarialancamento", baixa.getId_conciliacaobancarialancamento());
                                                }
                                                if (baixa.getId_receberchequepagamento() > 0) {
                                                    sqlBaixa.put("id_receberchequepagamento", baixa.getId_receberchequepagamento());
                                                }
                                                if (baixa.getId_recebercaixa() > 0) {
                                                    sqlBaixa.put("id_recebercaixa", baixa.getId_recebercaixa());
                                                }
                                                sqlBaixa.put("id_usuario", baixa.getId_usuario());
                                                sqlBaixa.put("id_loja", idLoja);
                                                statm = sqlBaixa.getInsert();
                                                stm3.execute(statm);  
                                                totalPago += baixa.getValor() + baixa.getValorjuros() + baixa.getValormulta() - baixa.getValordesconto();
                                            }
                                            
                                            for (ReceberChequeHistoricoVO hist: i_receberCheque.getvHistorico()) {   
                                                SQLBuilder sqlHist = new SQLBuilder();
                                                sqlHist.setTableName("receberchequehistorico");
                                                sqlHist.put("id_recebercheque", i_receberCheque.getId());
                                                sqlHist.put("datahora", Utils.timestampSQL(hist.getDatahora()));
                                                sqlHist.put("id_tipoalinea", hist.getId_tipoalinea());
                                                sqlHist.put("id_usuario", hist.getId_usuario());
                                                statm = sqlHist.getInsert();
                                                stm3.execute(statm);   
                                            }
                                            if (totalPago >= i_receberCheque.getValor()) {
                                                statm = "update recebercheque \n" +
                                                        "set id_situacaorecebercheque = 1,\n" +
                                                        " id_tipoalinea = 0\n" +
                                                        " where id = " + i_receberCheque.getId();
                                                stm3.execute(statm);
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        ProgressBar.next();
                    }
                }
            }
            Conexao.commit();
        } catch (Exception e) {
            Conexao.rollback();
            System.out.println(statm);
            Util.exibirMensagemErro(new Exception(statm), "ERRO");
            throw e;
        }
    }

    public void salvarComCondicao2(List<ReceberChequeVO> vReceberCheque, int idLoja) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        
        try {
            Conexao.begin();
            
            stm = Conexao.createStatement();
            
            ProgressBar.setMaximum(vReceberCheque.size());
            ProgressBar.setStatus("Importar Receber Cheque Condição Loja "+idLoja);
            
            for (ReceberChequeVO i_receberCheque : vReceberCheque) {
                sql = new StringBuilder();
                sql.append("select * from recebercheque ");
                sql.append(" where id_loja = " + idLoja + " ");
                sql.append("   and cpf = " + i_receberCheque.cpf + " ");
                sql.append("   and numerocheque = " + i_receberCheque.numerocheque + " ");
                sql.append("   and valor = " + i_receberCheque.valor + " ");
                sql.append("   and data = '" + i_receberCheque.data + "' ");
                sql.append("   and datadeposito = '" + i_receberCheque.datadeposito+ "' ");
                rst = stm.executeQuery(sql.toString());
                
                if (!rst.next()) {
                    sql = new StringBuilder();
                    sql.append("INSERT INTO recebercheque( ");
                    sql.append("id_loja, cpf, numerocheque, id_banco, agencia, conta, data, ");
                    sql.append("id_plano, numerocupom, ecf, valor, datadeposito, lancamentomanual, ");
                    sql.append("rg, telefone, nome, observacao, id_situacaorecebercheque, id_tipolocalcobranca, ");
                    sql.append("cmc7, datadevolucao, id_tipoalinea, id_tipoinscricao, dataenviocobranca, ");
                    sql.append("valorpagarfornecedor, id_boleto, operadorclientebloqueado, operadorexcedelimite, ");
                    sql.append("operadorproblemacheque, operadorchequebloqueado, valorjuros, ");
                    sql.append("id_tipovistaprazo, justificativa, valoracrescimo, valorinicial) ");
                    sql.append("VALUES ( ");
                    sql.append(idLoja + ",");
                    sql.append(i_receberCheque.cpf + ",");
                    sql.append(i_receberCheque.numerocheque + ",");
                    sql.append(i_receberCheque.id_banco + ",");
                    sql.append("'" + i_receberCheque.agencia + "',");
                    sql.append("'" + i_receberCheque.conta + "',");
                    sql.append("'" + i_receberCheque.data + "',");
                    sql.append(i_receberCheque.id_plano + ",");
                    sql.append(i_receberCheque.numerocupom + ",");
                    sql.append(i_receberCheque.ecf + ",");
                    sql.append(i_receberCheque.valor + ",");
                    sql.append("'" + i_receberCheque.datadeposito + "',");
                    sql.append(i_receberCheque.lancamentomanual + ",");
                    sql.append("'" + i_receberCheque.rg + "',");
                    sql.append("'" + i_receberCheque.telefone + "',");
                    sql.append("'" + i_receberCheque.nome + "',");
                    sql.append("'" + i_receberCheque.observacao + "',");
                    sql.append(i_receberCheque.id_situacaorecebercheque + ",");
                    sql.append(i_receberCheque.id_tipolocalcobranca + ",");
                    sql.append("'" + i_receberCheque.cmc7 + "',");
                    sql.append((i_receberCheque.datadevolucao == "" ? null : "'" + i_receberCheque.datadevolucao + "'") + ",");
                    sql.append(i_receberCheque.id_tipoalinea + ", ");
                    sql.append(i_receberCheque.id_tipoinscricao + ", ");
                    sql.append(i_receberCheque.dataenviocobranca + ",");
                    sql.append(i_receberCheque.valorpagarfornecedor + ",");
                    sql.append((i_receberCheque.id_boleto == 0 ? null : i_receberCheque.id_boleto) + ",");
                    sql.append("'" + i_receberCheque.operadorclientebloqueado + "',");
                    sql.append("'" + i_receberCheque.operadorexcedelimite + "',");
                    sql.append("'" + i_receberCheque.operadorproblemacheque + "',");
                    sql.append("'" + i_receberCheque.operadorchequebloqueado + "',");
                    sql.append(i_receberCheque.valorjuros + ",");
                    sql.append(i_receberCheque.id_tipovistaprazo + ",");
                    sql.append("'" + i_receberCheque.justificativa + "',");
                    sql.append(i_receberCheque.valoracrescimo + ",");
                    sql.append(i_receberCheque.valorinicial);
                    sql.append(");");
                    stm.execute(sql.toString());
                }
                
                ProgressBar.next();
            }
            stm.close();
            Conexao.commit();
        } catch(Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }
    
    public void importarQuitacaoChequeSHI(List<ReceberChequeVO> v_cheques, int idLojaVR) throws Exception {
        //Organizar a listagem, separando cheque e seus pagamentos em Maps
        Map<String, ReceberChequeVO> cheques = new LinkedHashMap<>();
        Map<String, List<ReceberChequeItemVO>> baixas = new LinkedHashMap<>();
        Map<String, List<ReceberChequeHistoricoVO>> historicos = new LinkedHashMap<>();
        
        for (ReceberChequeVO cheque: v_cheques) {
            //Garante que o cheque é único
            cheques.put(cheque.getChave(), cheque);            
            
            List<ReceberChequeItemVO> aux;
            if (!baixas.containsKey(cheque.getChave())) {
                aux = new ArrayList<>();
                baixas.put(cheque.getChave(), aux);
            } else {
                aux = baixas.get(cheque.getChave());
            }            
            for (ReceberChequeItemVO baixa: cheque.getvBaixa()) {
                aux.add(baixa);
            }   
            
            List<ReceberChequeHistoricoVO> hist;
            if (!historicos.containsKey(cheque.getChave())) {
                hist = new ArrayList<>();
                historicos.put(cheque.getChave(), hist);
            } else {
                hist = historicos.get(cheque.getChave());
            }            
            for (ReceberChequeHistoricoVO historico: cheque.getvHistorico()) {
                hist.add(historico);
            }
        }
        
        Conexao.begin();
        
        //Select para eliminar as alterações....
        //update recebercheque rc1 set id_tipoalinea = rc2.id_tipoalinea, id_situacaorecebercheque = rc2.id_situacaorecebercheque 
        //from (select * from implantacao.bkp_recebercheque) rc2
        //where rc1.id = rc2.id;
        //delete from receberchequeitem where not id in (select id from implantacao.bkp_receberchequeitem);
        //delete from receberchequehistorico where not id in (select id from implantacao.bkp_receberchequehistorico);
        
        ProgressBar.setStatus("Criando tabelas de backup....");
        Statement makeTables = Conexao.createStatement();
        makeTables.execute("drop table if exists implantacao.bkp_receberchequehistorico;");
        makeTables.execute("drop table if exists implantacao.bkp_receberchequeitem;");
        makeTables.execute("drop table if exists implantacao.bkp_recebercheque;");
        makeTables.execute("create table if not exists implantacao.bkp_recebercheque as select * from recebercheque order by id;");
        makeTables.execute("create table if not exists implantacao.bkp_receberchequeitem as select * from receberchequeitem order by id;");
        makeTables.execute("create table if not exists implantacao.bkp_receberchequehistorico as select * from receberchequehistorico order by id;");
        
        ProgressBar.setStatus("Importando baixas do cheque....");
        try {
            //Localizar o cheque, obter o id;
            ProgressBar.setMaximum(cheques.size());
            for (ReceberChequeVO cheque: cheques.values()) {
                ReceberChequeVO existente = obterCheque(
                        cheque.getCpf(),
                        cheque.getId_banco(),
                        cheque.getAgencia(),
                        cheque.getConta(),
                        cheque.getNumerocheque(),
                        cheque.getValor(),
                        new SimpleDateFormat("yyyy-MM-dd").parse(cheque.getData()) 
                );
                //Se localizar o cheque executa.
                if (existente != null) {
                    //Verifica qual o valor total já baixado no cheque existente.
                    double totalBaixado = 0;
                    for (ReceberChequeItemVO baixaExist: existente.getvBaixa()) {
                        totalBaixado += baixaExist.getValor();
                    }
                    boolean baixaExistente = totalBaixado > 0;
                    //Somente se não houver baixa alguma no cheque existente entra no if
                    if (!baixaExistente) {
                        double totalPago = 0;
                        StringBuilder sql = new StringBuilder();

                        //Incluir as baixas
                        for (ReceberChequeItemVO baixa: baixas.get(cheque.getChave())) {
                            Date dataCheque;
                            if (!"".equals(cheque.getData()) && cheque.getData() != null) {
                                dataCheque = new SimpleDateFormat("yyyy-MM-dd").parse(cheque.getData());
                            } else {
                                dataCheque = new Date();
                            }
                            //Gravar a baixa
                            String str = "INSERT INTO receberchequeitem(\n" +
                                "    id_recebercheque, \n" +
                                "    valor, \n" +
                                "    valordesconto, \n" +
                                "    valorjuros, \n" +
                                "    valormulta, \n" +
                                "    valortotal, \n" +
                                "    databaixa, \n" +
                                "    datapagamento, \n" +
                                "    observacao, \n" +
                                "    id_banco, \n" +
                                "    agencia, \n" +
                                "    conta, \n" +
                                "    id_tiporecebimento, \n" +
                                "    id_pagarfornecedorparcela, \n" +
                                "    id_conciliacaobancarialancamento, \n" +
                                "    id_receberchequepagamento, \n" +
                                "    id_recebercaixa, \n" +
                                "    id_usuario, \n" +
                                "    id_loja \n" +
                                ") VALUES (\n" +
                                "    " + existente.getId() + ", \n" +
                                "    " + baixa.getValor() + ", \n" +
                                "    " + baixa.getValordesconto() + ", \n" +
                                "    " + baixa.getValorjuros() + ", \n" +
                                "    " + baixa.getValormulta() + ", \n" +
                                "    " + baixa.getValortotal() + ", \n" +
                                "    " + Utils.dateSQL(baixa.getDatabaixa() != null ? baixa.getDatabaixa() : dataCheque) + ", \n" +
                                "    " + Utils.dateSQL(baixa.getDatapagamento()!= null ? baixa.getDatapagamento() : dataCheque) + ", \n" +
                                "    " + Utils.quoteSQL(baixa.getObservacao()) + ", \n" +
                                "    " + baixa.getId_banco() + ", \n" +
                                "    " + Utils.quoteSQL(baixa.getAgencia()) + ", \n" +
                                "    " + Utils.quoteSQL(baixa.getConta()) + ", \n" +
                                "    " + baixa.getId_tiporecebimento() + ", \n" +
                                "    " + Utils.longIntSQL(baixa.getId_pagarfornecedorparcela(), -1) + ", \n" +
                                "    " + Utils.longIntSQL(baixa.getId_conciliacaobancarialancamento(), -1) + ", \n" +
                                "    " + Utils.longIntSQL(baixa.getId_receberchequepagamento(), -1) + ", \n" +
                                "    " + Utils.longIntSQL(baixa.getId_recebercaixa(), -1)  + ", \n" +
                                "    " + baixa.getId_usuario() + ", \n" +
                                "    " + idLojaVR + "\n" +
                                ");"  ; 
                            sql.append(
                                str
                            );
                            totalPago += baixa.getValor() + baixa.getValorjuros() + baixa.getValormulta() - baixa.getValordesconto();
                        }
                        
                        for (ReceberChequeHistoricoVO hist: cheque.getvHistorico()) {
                            boolean existe = false;
                            for (ReceberChequeHistoricoVO histExist: existente.getvHistorico()) {                                
                                if (histExist.getDatahora().equals(hist.getDatahora()) &&
                                    histExist.getId_tipoalinea() == hist.getId_tipoalinea()) {
                                    existe = true;
                                }                                
                            }
                            if (!existe) {
                                sql.append("insert into receberchequehistorico ")
                                        .append("(id_recebercheque, datahora, id_tipoalinea, id_usuario) values ")
                                        .append("(")
                                        .append(existente.getId()).append(", ")
                                        .append(Utils.timestampSQL(hist.getDatahora())).append(", ")
                                        .append(hist.getId_tipoalinea()).append(", ")
                                        .append(hist.getId_usuario())
                                        .append(");");
                            }
                        }

                        //Atualizo o cheque se o valor da quitação for igual ao total
                        if (totalPago >= cheque.getValor()) {
                            sql.append("update recebercheque ")
                                    .append("set id_situacaorecebercheque = 1,")
                                    .append(" id_tipoalinea = 0")
                                    .append(" where id = ")
                                    .append(existente.getId())
                                    .append(";"); 
                        }

                        Conexao.createStatement().execute(sql.toString());
                    }
                }
                ProgressBar.next();
            }
            
            Conexao.commit();
        } catch (Exception e) {
            Conexao.rollback();
            throw e;
        }        
    }

    private ReceberChequeVO obterCheque(long cnpf, int id_banco, String agencia, String conta, int numerocheque, double valor, Date data) throws Exception {
        //TODO: Completar este método.
        try (Statement stm = Conexao.createStatement()) {
            String sql = "SELECT \n" +
                    "	*\n" +
                    "FROM \n" +
                    "	public.recebercheque\n" +
                    "where\n" +
                    "   cpf = " + Utils.longIntSQL(cnpf, 0) + " and\n" +
                    "	id_banco = " + Utils.longIntSQL(id_banco, 0) + " and\n" +
                    "	agencia = " + Utils.quoteSQL(agencia) + " and\n" +
                    "	conta = " + Utils.quoteSQL(conta) + " and\n" +
                    "	trunc(valor,2) = trunc(" + valor + ",2) and\n" +
                    "	data = " + Utils.dateSQL(data) + " and\n" +
                    "	numerocheque = " + Utils.longIntSQL(numerocheque, 0);
            try (ResultSet rst = stm.executeQuery(
                    sql
            )) {
                if (rst.next()) {
                    ReceberChequeVO cheque = new ReceberChequeVO();
                    cheque.setId(rst.getInt("id"));
                    //TODO: Preencher as outras informações do cheque
                    try (Statement stm2 = Conexao.createStatement()) {
                        try (ResultSet rst2 = stm2.executeQuery(
                                "select * from receberchequeitem where id_recebercheque = " + cheque.getId()
                        )) {
                            while (rst2.next()) {
                                //TODO: Preenche os dados da baixa do cheque.
                                ReceberChequeItemVO baixa = new ReceberChequeItemVO();
                                baixa.setId(rst2.getInt("id"));
                                baixa.setId_recebercheque(rst.getLong("id"));
                                baixa.setId_banco(rst2.getInt("id_banco"));
                                baixa.setAgencia(rst2.getString("agencia"));
                                baixa.setConta(rst2.getString("conta"));
                                baixa.setValor(rst2.getDouble("valor"));
                                baixa.setId_loja(rst2.getInt("id_loja"));
                                baixa.setId_tiporecebimento(rst2.getInt("id_tiporecebimento"));
                                cheque.getvBaixa().add(baixa);
                            }
                        }
                        
                        try (ResultSet rst2 = stm2.executeQuery(
                                "select * from receberchequehistorico where id_recebercheque = " + cheque.getId() + " order by datahora"
                        )) {
                            while (rst2.next()) {
                                ReceberChequeHistoricoVO hist = new ReceberChequeHistoricoVO();
                                hist.setId(rst2.getInt("id"));
                                hist.setId_recebercheque(rst.getLong("id"));
                                hist.setDatahora(rst2.getTimestamp("datahora"));
                                hist.setId_recebercheque(rst2.getInt("id_recebercheque"));
                                hist.setId_tipoalinea(rst2.getInt("id_tipoalinea"));
                                hist.setId_usuario(rst2.getInt("id_usuario"));
                                cheque.getvHistorico().add(hist);
                            }
                        }
                    }
                    return cheque;
                }
            }
        }
        return null;
    }

}
