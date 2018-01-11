package vrimplantacao.dao.financeiro;

import java.sql.ResultSet;
import java.sql.Statement;
import vrimplantacao.dao.LogTransacaoDAO;
import vrimplantacao.dao.PostgresDAO;
import vrimplantacao.vo.Formulario;
import vrimplantacao.vo.TipoTransacao;
import vrimplantacao.vo.notafiscal.ConciliacaoBancariaLancamentoVO;
import vrimplantacao.vo.notafiscal.ReceberVendaPrazoItemVO;
import vrimplantacao.vo.notafiscal.ReceberVendaPrazoVO;
import vrframework.classe.Conexao;
import vrframework.classe.Util;
import vrframework.classe.VRException;

public class ReceberVendaPrazoDAO {

    public void excluir(long i_id) throws Exception {
        Statement stm = null;

        try {
            Conexao.begin();
            stm = Conexao.createStatement();

            if (new PostgresDAO().tabelaExiste("atacado.venda")) {
                stm.execute("UPDATE atacado.venda SET id_recebervendaprazo = NULL WHERE id_recebervendaprazo = " + i_id);
            }

            if (new PostgresDAO().tabelaExiste("limerrede.venda")) {
                stm.execute("UPDATE limerrede.venda SET id_recebervendaprazo = NULL WHERE id_recebervendaprazo = " + i_id);
            }

            stm.execute("DELETE FROM recebervendaprazoitem WHERE id_recebervendaprazo = " + i_id);
            stm.execute("DELETE FROM recebervendaprazo WHERE id = " + i_id);

            stm.close();
            Conexao.commit();

        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void salvar(ReceberVendaPrazoVO i_recebimento) throws Exception {
        Statement stm = null;
        StringBuilder sql = null;
        ResultSet rst = null;

        try {
            Conexao.begin();
            stm = Conexao.createStatement();

            rst = stm.executeQuery("SELECT id FROM recebervendaprazo WHERE id = " + i_recebimento.id);

            if (rst.next()) {
                sql = new StringBuilder();
                sql.append("UPDATE recebervendaprazo SET");
                sql.append(" id_clienteeventual = " + i_recebimento.idClienteEventual + ",");
                sql.append(" dataemissao = '" + Util.formatDataBanco(i_recebimento.dataEmissao) + "',");
                sql.append(" datavencimento = '" + Util.formatDataBanco(i_recebimento.dataVencimento) + "',");
                sql.append(" valor = " + i_recebimento.valor + ",");
                sql.append(" valorliquido = " + i_recebimento.valorLiquido + ",");
                sql.append(" valorjuros = " + i_recebimento.valorJuros + ",");
                sql.append(" observacao = '" + i_recebimento.observacao + "',");
                sql.append(" id_situacaorecebervendaprazo = " + i_recebimento.idSituacaoReceberVendaPrazo + ",");
                sql.append(" id_tipolocalcobranca = " + i_recebimento.idTipoLocalCobranca + ",");
                sql.append(" numeronota = " + i_recebimento.numeroNota + ",");
                sql.append(" id_loja = " + i_recebimento.idLoja + ",");
                sql.append(" impostorenda = " + i_recebimento.impostoRenda + ",");
                sql.append(" pis = " + i_recebimento.pis + ",");
                sql.append(" cofins = " + i_recebimento.cofins + ",");
                sql.append(" csll = " + i_recebimento.csll + ",");
                sql.append(" id_tiposaida = " + i_recebimento.idTipoSaida + ",");
                sql.append(" id_notasaida = " + (i_recebimento.idNotaSaida == -1 ? "NULL" : i_recebimento.idNotaSaida) + ", ");
                sql.append(" numeroparcela = " + i_recebimento.numeroParcela);
                sql.append(" WHERE id = " + i_recebimento.id);

                stm.execute(sql.toString());

                new LogTransacaoDAO().gerar(Formulario.FINANCEIRO_RECEBIMENTO_VENDA_PRAZO, TipoTransacao.ALTERACAO, i_recebimento.idClienteEventual, "", i_recebimento.id);

            } else {
                sql = new StringBuilder();
                sql.append("INSERT INTO recebervendaprazo (id_loja, numeronota, id_clienteeventual, dataemissao, datavencimento, impostorenda, pis,");
                sql.append(" cofins, csll, valor, valorliquido, id_tipolocalcobranca, id_tiposaida, observacao, id_situacaorecebervendaprazo,");
                sql.append(" lancamentomanual, id_notasaida, numeroparcela, exportado) VALUES (");
                sql.append(i_recebimento.idLoja + ", ");
                sql.append(i_recebimento.numeroNota + ", ");
                sql.append(i_recebimento.idClienteEventual + ", ");
                sql.append("'" + Util.formatDataBanco(i_recebimento.dataEmissao) + "', ");
                sql.append("'" + Util.formatDataBanco(i_recebimento.dataVencimento) + "', ");
                sql.append(i_recebimento.impostoRenda + ", ");
                sql.append(i_recebimento.pis + ", ");
                sql.append(i_recebimento.cofins + ", ");
                sql.append(i_recebimento.csll + ", ");
                sql.append(i_recebimento.valor + ", ");
                sql.append(i_recebimento.valorLiquido + ", ");
                //sql.append(i_recebimento.valorJuros + ", ");
                sql.append(i_recebimento.idTipoLocalCobranca + ", ");
                sql.append("'" + i_recebimento.idTipoSaida + "', ");
                sql.append("'" + i_recebimento.observacao + "', ");
                sql.append(i_recebimento.idSituacaoReceberVendaPrazo + ", ");
                sql.append(i_recebimento.lancamentoManual + ", ");
                sql.append((i_recebimento.idNotaSaida == -1 ? "NULL" : i_recebimento.idNotaSaida) + ", ");
                sql.append(i_recebimento.numeroParcela + ", ");
                sql.append(i_recebimento.exportado + ")");

                stm.execute(sql.toString());

                rst = stm.executeQuery("SELECT CURRVAL('recebervendaprazo_id_seq') AS id");
                rst.next();

                i_recebimento.id = rst.getLong("id");

                new LogTransacaoDAO().gerar(Formulario.FINANCEIRO_RECEBIMENTO_VENDA_PRAZO, TipoTransacao.INCLUSAO, i_recebimento.idClienteEventual, "", i_recebimento.id);
            }

            //estorna conciliacao bancaria
            for (ReceberVendaPrazoItemVO oItemExclusao : i_recebimento.vItemExclusao) {
                new ConciliacaoBancariaDAO().excluirLancamentoVendaPrazoItem(oItemExclusao);
            }

            stm.execute("DELETE FROM recebervendaprazoitem WHERE id_recebervendaprazo = " + i_recebimento.id);

            for (ReceberVendaPrazoItemVO oItem : i_recebimento.vItem) {
                sql = new StringBuilder();
                sql.append("INSERT INTO recebervendaprazoitem (id_recebervendaprazo, datapagamento, databaixa,");
                sql.append(" valor, valordesconto, valormulta, valortotal, observacao, id_banco, agencia, conta, id_tiporecebimento) VALUES (");
                sql.append(i_recebimento.id + ", ");
                sql.append("'" + Util.formatDataBanco(oItem.dataPagamento) + "', ");
                sql.append("'" + Util.formatDataBanco(oItem.dataBaixa) + "', ");
                sql.append(oItem.valor + ", ");
                sql.append(oItem.valorDesconto + ", ");
                //sql.append(oItem.valorJuros + ", ");
                sql.append(oItem.valorMulta + ", ");
                sql.append(oItem.valorTotal + ", ");
                sql.append("'" + oItem.observacao + "', ");
                sql.append((oItem.idBanco == -1 ? "NULL" : oItem.idBanco) + ", ");
                sql.append("'" + oItem.agencia + "', ");
                sql.append("'" + oItem.conta + "', ");
                sql.append(oItem.idTipoRecebimento + ")");

                stm.execute(sql.toString());

                rst = stm.executeQuery("SELECT CURRVAL('recebervendaprazoitem_id_seq') AS id");
                rst.next();

                oItem.id = rst.getInt("id");

                if (!oItem.conciliado && oItem.idBanco != -1) {
                    String observacao = i_recebimento.idClienteEventual + " - " + i_recebimento.clienteEventual;

                    if (!oItem.observacao.isEmpty()) {
                        observacao += ", " + oItem.observacao;
                    }

                    ConciliacaoBancariaLancamentoVO oLancamento = new ConciliacaoBancariaLancamentoVO();
                    oLancamento.idContaContabilFinanceiro = new ReceberVendaPrazoDAO().getIdContaContabilFinanceiro(i_recebimento.idTipoSaida);
                    oLancamento.observacao = observacao;
                    oLancamento.valorCredito = oItem.valorTotal;
                    oLancamento.idReceberVendaPrazoItem = oItem.id;

                    new ConciliacaoBancariaDAO().adicionar(oItem.dataBaixa, oItem.idBanco, oItem.agencia, oItem.conta, oLancamento);

                    oItem.conciliado = true;
                }
            }

            i_recebimento.vItemExclusao.clear();

            stm.close();
            Conexao.commit();

        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public int getIdContaContabilFinanceiro(int i_idTipoSaida) throws Exception {
        ResultSet rst = null;
        Statement stm = null;

        stm = Conexao.createStatement();

        rst = stm.executeQuery("SELECT id_contacontabilfinanceirorecebimento FROM recebervendaprazotiposaidaconfiguracao WHERE id_tiposaida = " + i_idTipoSaida);

        if (!rst.next()) {
            throw new VRException("Evento financeiro não configurado para este tipo de nota!");
        }

        return rst.getInt("id_contacontabilfinanceirorecebimento");
    }
}
