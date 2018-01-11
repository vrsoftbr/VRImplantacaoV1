package vrimplantacao.dao.financeiro;

import java.sql.ResultSet;
import java.sql.Statement;
import vrimplantacao.dao.LogTransacaoDAO;
import vrimplantacao.dao.ParametroDAO;
import vrimplantacao.vo.Formulario;
import vrimplantacao.vo.TipoTransacao;
import vrimplantacao.vo.notafiscal.ConciliacaoBancariaLancamentoVO;
import vrimplantacao.vo.notafiscal.ReceberDevolucaoItemVO;
import vrimplantacao.vo.notafiscal.ReceberDevolucaoVO;
import vrframework.classe.Conexao;
import vrframework.classe.Util;
import vrframework.classe.VRException;

public class ReceberDevolucaoDAO {

    public void excluir(long i_id) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        try {
            Conexao.begin();
            stm = Conexao.createStatement();

            sql = new StringBuilder();
            sql.append("SELECT pf.numerodocumento");
            sql.append(" FROM pagarfornecedorparceladevolucao pfpd");
            sql.append(" INNER JOIN pagarfornecedorparcela pfp ON pfp.id = pfpd.id_pagarfornecedorparcela");
            sql.append(" INNER JOIN pagarfornecedor pf ON pf.id = pfp.id_pagarfornecedor");
            sql.append(" WHERE pfpd.id_receberdevolucao = " + i_id);

            rst = stm.executeQuery(sql.toString());

            if (rst.next()) {
                throw new VRException("Esta nota possui uma devolução vinculada a uma parcela do documento " + rst.getInt("numerodocumento") + " e não pode ser estornada!");
            }

            stm.execute("DELETE FROM receberdevolucaoitem WHERE id_receberdevolucao = " + i_id);
            stm.execute("DELETE FROM receberdevolucao WHERE id = " + i_id);

            stm.close();
            Conexao.commit();

        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }
    
    public void salvar(ReceberDevolucaoVO i_recebimento) throws Exception {
        Statement stm = null;
        StringBuilder sql = null;
        ResultSet rst = null;

        try {
            Conexao.begin();
            stm = Conexao.createStatement();

            rst = stm.executeQuery("SELECT id FROM receberdevolucao WHERE id = " + i_recebimento.id);

            if (rst.next()) {
                sql = new StringBuilder();
                sql.append("UPDATE receberdevolucao SET");
                sql.append(" id_fornecedor = " + i_recebimento.idFornecedor + ",");
                sql.append(" numeronota = " + i_recebimento.numeroNota + ",");
                sql.append(" dataemissao = '" + Util.formatDataBanco(i_recebimento.dataEmissao) + "',");
                sql.append(" datavencimento = '" + Util.formatDataBanco(i_recebimento.dataVencimento) + "',");
                sql.append(" valor = " + i_recebimento.valor + ",");
                sql.append(" observacao = '" + i_recebimento.observacao + "',");
                sql.append(" id_situacaoreceberdevolucao = " + i_recebimento.idSituacaoReceberDevolucao + ",");
                sql.append(" id_tipolocalcobranca = " + i_recebimento.idTipoLocalCobranca + ",");
                sql.append(" id_tipodevolucao = " + i_recebimento.idTipoDevolucao + ",");
                sql.append(" id_loja = " + i_recebimento.idLoja + ",");
                sql.append(" valorpagarfornecedor = " + i_recebimento.valorPagarFornecedor + ",");
                sql.append(" id_notasaida = " + (i_recebimento.idNotaSaida == -1 ? "NULL" : i_recebimento.idNotaSaida) + ",");
                sql.append(" numeroparcela = " + i_recebimento.numeroParcela);
                sql.append(" WHERE id = " + i_recebimento.id);

                stm.execute(sql.toString());

                new LogTransacaoDAO().gerar(Formulario.FINANCEIRO_RECEBIMENTO_DEVOLUCAO, TipoTransacao.ALTERACAO, i_recebimento.idFornecedor, "", i_recebimento.id);

            } else {
                sql = new StringBuilder();
                sql.append("INSERT INTO receberdevolucao (id_loja, id_fornecedor, numeronota, dataemissao, datavencimento, valor, observacao,");
                sql.append(" id_situacaoreceberdevolucao, id_tipolocalcobranca, id_tipodevolucao, lancamentomanual,");
                sql.append(" id_notasaida, valorpagarfornecedor, numeroparcela, exportado) VALUES (");
                sql.append(i_recebimento.idLoja + ", ");
                sql.append(i_recebimento.idFornecedor + ", ");
                sql.append(i_recebimento.numeroNota + ", ");
                sql.append("'" + Util.formatDataBanco(i_recebimento.dataEmissao) + "', ");
                sql.append("'" + Util.formatDataBanco(i_recebimento.dataVencimento) + "', ");
                sql.append(i_recebimento.valor + ", ");
                sql.append("'" + i_recebimento.observacao + "', ");
                sql.append(i_recebimento.idSituacaoReceberDevolucao + ", ");
                sql.append(i_recebimento.idTipoLocalCobranca + ", ");
                sql.append(i_recebimento.idTipoDevolucao + ", ");
                sql.append(i_recebimento.lancamentoManual + ", ");
                sql.append((i_recebimento.idNotaSaida == -1 ? "NULL" : i_recebimento.idNotaSaida) + ", ");
                sql.append("0, ");
                sql.append(i_recebimento.numeroParcela + ", ");
                sql.append(i_recebimento.exportado + ")");

                stm.execute(sql.toString());

                rst = stm.executeQuery("SELECT CURRVAL('receberdevolucao_id_seq') AS id");
                rst.next();

                i_recebimento.id = rst.getLong("id");

                new LogTransacaoDAO().gerar(Formulario.FINANCEIRO_RECEBIMENTO_DEVOLUCAO, TipoTransacao.INCLUSAO, i_recebimento.idFornecedor, "", i_recebimento.id);
            }

            //estorna conciliacao bancaria
            for (ReceberDevolucaoItemVO oItemExclusao : i_recebimento.vItemExclusao) {
                new ConciliacaoBancariaDAO().excluirLancamentoDevolucaoItem(oItemExclusao);
            }

            //salva item
            stm.execute("DELETE FROM receberdevolucaoitem WHERE id_receberdevolucao = " + i_recebimento.id);

            for (ReceberDevolucaoItemVO oItem : i_recebimento.vItem) {
                sql = new StringBuilder();
                sql.append("INSERT INTO receberdevolucaoitem (id_receberdevolucao, id_lojabaixa, valor, valordesconto,");
                sql.append(" valorjuros, valormulta, valortotal, datapagamento, databaixa, observacao, id_tiporecebimento, id_banco, agencia, conta,");
                sql.append(" id_pagarfornecedorparcela) VALUES (");
                sql.append(i_recebimento.id + ", ");
                sql.append(oItem.idLojaBaixa + ", ");
                sql.append(oItem.valor + ", ");
                sql.append(oItem.valorDesconto + ", ");
                sql.append(oItem.valorJuros + ", ");
                sql.append(oItem.valorMulta + ", ");
                sql.append(oItem.valorTotal + ", ");
                sql.append("'" + Util.formatDataBanco(oItem.dataPagamento) + "', ");
                sql.append("'" + Util.formatDataBanco(oItem.dataBaixa) + "', ");
                sql.append("'" + oItem.observacao + "', ");
                sql.append(oItem.idTipoRecebimento + ", ");
                sql.append((oItem.idBanco == -1 ? "NULL" : oItem.idBanco) + ", ");
                sql.append("'" + oItem.agencia + "', ");
                sql.append("'" + oItem.conta + "', ");
                sql.append((oItem.idPagarFornecedorParcela == -1 ? "NULL" : oItem.idPagarFornecedorParcela) + ")");

                stm.execute(sql.toString());

                rst = stm.executeQuery("SELECT CURRVAL('receberdevolucaoitem_id_seq') AS id");
                rst.next();

                oItem.id = rst.getLong("id");

                if (!oItem.conciliado && oItem.idBanco != -1) {
                    String observacao = i_recebimento.idFornecedor + " - " + i_recebimento.fornecedor;

                    if (!oItem.observacao.isEmpty()) {
                        observacao += ", " + oItem.observacao;
                    }

                    ConciliacaoBancariaLancamentoVO oLancamento = new ConciliacaoBancariaLancamentoVO();
                    oLancamento.idContaContabilFinanceiro = new ParametroDAO().get(72).getInt();
                    oLancamento.observacao = observacao;
                    oLancamento.valorCredito = oItem.valorTotal;
                    oLancamento.idReceberDevolucaoItem = oItem.id;

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
}
