package vrimplantacao.dao.financeiro;

import java.sql.ResultSet;
import java.sql.Statement;
import vrimplantacao.dao.LogTransacaoDAO;
import vrimplantacao.dao.cadastro.BancoDAO;
import vrimplantacao.dao.cadastro.ContaContabilFinanceiroDAO;
import vrimplantacao.vo.Formulario;
import vrimplantacao.vo.TipoTransacao;
import vrimplantacao.vo.notafiscal.ConciliacaoBancariaLancamentoVO;
import vrimplantacao.vo.notafiscal.ConciliacaoBancariaSaldoVO;
import vrimplantacao.vo.notafiscal.ConciliacaoBancariaVO;
import vrimplantacao.vo.notafiscal.ContabilidadeVO;
import vrimplantacao.vo.notafiscal.ReceberDevolucaoItemVO;
import vrimplantacao.vo.notafiscal.ReceberVendaPrazoItemVO;
import vrimplantacao.vo.notafiscal.SituacaoConciliacaoBancaria;
import vrframework.classe.Conexao;
import vrframework.classe.Util;
import vrframework.classe.VRException;

public class ConciliacaoBancariaDAO {

    public void excluirLancamentoDevolucaoItem(ReceberDevolucaoItemVO i_receberDevolucaoItem) throws Exception {
        Statement stm = null;

        stm = Conexao.createStatement();

        stm.execute("UPDATE conciliacaobancarialancamento SET id_receberdevolucaoitem = NULL WHERE id_receberdevolucaoitem = " + i_receberDevolucaoItem.id);

        stm.execute("DELETE FROM conciliacaobancarialancamento WHERE id_receberdevolucaoitem = " + i_receberDevolucaoItem.id);

        stm.close();
    }

    public void adicionar(String i_data, int i_idBanco, String i_agencia, String i_conta, ConciliacaoBancariaLancamentoVO i_lancamento) throws Exception {
        Statement stm = null;

        try {
            Conexao.begin();
            stm = Conexao.createStatement();

            //carrega conciliacao bancaria
            long idConciliacao = verificar(i_data, i_idBanco, i_agencia, i_conta);

            ConciliacaoBancariaVO oConciliacao = new ConciliacaoBancariaVO();

            if (idConciliacao > 0) {
                oConciliacao = carregar(idConciliacao);

            } else {
                oConciliacao.data = i_data;
                oConciliacao.idBanco = i_idBanco;
                oConciliacao.agencia = i_agencia;
                oConciliacao.conta = i_conta;
                oConciliacao.idSituacaoConciliacaoBancaria = SituacaoConciliacaoBancaria.NAO_FINALIZADO.getId();
            }

            //adiciona lancamento
            int idContaFiscalLancamento = new ContaContabilFinanceiroDAO().getIdContaContabilFiscal(i_lancamento.idContaContabilFinanceiro);
            int idContaFiscalBanco = new BancoDAO().getIdContaContabilFiscal(i_idBanco, i_agencia, i_conta);

            if (i_lancamento.valorDebito > 0) {
                i_lancamento.idContaContabilFiscalCredito = idContaFiscalBanco;
                i_lancamento.idContaContabilFiscalDebito = idContaFiscalLancamento;

            } else {
                i_lancamento.idContaContabilFiscalCredito = idContaFiscalLancamento;
                i_lancamento.idContaContabilFiscalDebito = idContaFiscalBanco;
            }

            //salva conciliacao            
            salvar(oConciliacao, i_lancamento);

            //adiciona contabilidade
            ContabilidadeVO oContabilidade = new ContabilidadeVO();
            oContabilidade.data = oConciliacao.data;
            oContabilidade.idHistoricoPadrao = -1;
            oContabilidade.idContaContabilFiscalCredito = i_lancamento.idContaContabilFiscalCredito;
            oContabilidade.idContaContabilFiscalDebito = i_lancamento.idContaContabilFiscalDebito;
            oContabilidade.idLoja = -1;
            oContabilidade.valor = i_lancamento.valorDebito + i_lancamento.valorCredito;
            oContabilidade.complemento = i_lancamento.observacao;

            //commit
            stm.close();
            Conexao.commit();

        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public long verificar(String i_data, int i_idBanco, String i_agencia, String i_conta) throws Exception {
        Statement stm = null;
        StringBuilder sql = null;
        ResultSet rst = null;

        stm = Conexao.createStatement();

        sql = new StringBuilder();
        sql.append("SELECT id FROM conciliacaobancaria");
        sql.append(" WHERE data = '" + Util.formatDataBanco(i_data) + "'");
        sql.append(" AND id_banco = " + i_idBanco);
        sql.append(" AND agencia = '" + i_agencia + "'");
        sql.append(" AND conta = '" + i_conta + "'");

        rst = stm.executeQuery(sql.toString());

        long id = 0;

        if (rst.next()) {
            id = rst.getLong("id");
        }

        stm.close();

        return id;
    }

    public ConciliacaoBancariaVO carregar(long i_id) throws Exception {
        Statement stm = null;
        StringBuilder sql = null;
        ResultSet rst = null;

        stm = Conexao.createStatement();

        rst = stm.executeQuery("SELECT * FROM conciliacaobancaria WHERE id = " + i_id);

        if (!rst.next()) {
            throw new VRException("Conciliação bancária " + i_id + " não encontrada!");
        }

        ConciliacaoBancariaVO oConciliacao = new ConciliacaoBancariaVO();
        oConciliacao.id = rst.getLong("id");
        oConciliacao.data = Util.formatDataGUI(rst.getDate("data"));
        oConciliacao.idBanco = rst.getInt("id_banco");
        oConciliacao.agencia = rst.getString("agencia");
        oConciliacao.conta = rst.getString("conta");
        oConciliacao.idSituacaoConciliacaoBancaria = rst.getInt("id_situacaoconciliacaobancaria");
        oConciliacao.saldo = rst.getDouble("saldo");

        sql = new StringBuilder();
        sql.append("SELECT cbl.id, cbl.id_contacontabilfinanceiro, cfn.descricao AS contacontabilfinanceiro, cbl.cnpj, cbl.observacao, cbl.valordebito,");
        sql.append(" cbl.valorcredito, cbl.id_tipoinscricao, cbl.id_contacontabilfiscaldebito, cbl.id_contacontabilfiscalcredito,");
        sql.append(" ccfc.id AS contareduzidacredito, ccfd.id AS contareduzidadebito, cbl.id_pagarfornecedorparcela,");
        sql.append(" cbl.id_recebercaixaitem, cbl.id_receberchequeitem, cbl.id_recebercontratoitem, cbl.id_receberconveniadoitem,");
        sql.append(" cbl.id_recebercreditorotativoitem, cbl.id_receberdevolucaoitem, cbl.id_recebervendaprazoitem, cbl.id_receberverbaitem,");
        sql.append(" cbl.id_receberoutrasreceitasitem");
        sql.append(" FROM conciliacaobancarialancamento AS cbl");
        sql.append(" INNER JOIN contacontabilfinanceiro AS cfn ON cfn.id = id_contacontabilfinanceiro");
        sql.append(" LEFT JOIN contacontabilfiscal AS ccfc ON ccfc.id = cbl.id_contacontabilfiscalcredito");
        sql.append(" LEFT JOIN contacontabilfiscal AS ccfd ON ccfd.id = cbl.id_contacontabilfiscaldebito");
        sql.append(" WHERE id_conciliacaobancaria = " + oConciliacao.id);
        sql.append(" ORDER BY cbl.id");

        rst = stm.executeQuery(sql.toString());

        while (rst.next()) {
            ConciliacaoBancariaLancamentoVO oLancamento = new ConciliacaoBancariaLancamentoVO();
            oLancamento.id = rst.getLong("id");
            oLancamento.idContaContabilFinanceiro = rst.getInt("id_contacontabilfinanceiro");
            oLancamento.contaContabilFinanceiro = rst.getString("contacontabilfinanceiro");
            oLancamento.idTipoInscricao = rst.getInt("id_tipoinscricao");
            oLancamento.cnpj = rst.getLong("cnpj");
            oLancamento.observacao = rst.getString("observacao");
            oLancamento.valorDebito = rst.getDouble("valordebito");
            oLancamento.valorCredito = rst.getDouble("valorcredito");
            oLancamento.idContaContabilFiscalDebito = rst.getObject("id_contacontabilfiscaldebito") == null ? -1 : rst.getInt("id_contacontabilfiscaldebito");
            oLancamento.idContaContabilFiscalCredito = rst.getObject("id_contacontabilfiscalcredito") == null ? -1 : rst.getInt("id_contacontabilfiscalcredito");
            oLancamento.contaReduzidaDebito = rst.getObject("contareduzidadebito") == null ? -1 : rst.getInt("contareduzidadebito");
            oLancamento.contaReduzidaCredito = rst.getObject("contareduzidacredito") == null ? -1 : rst.getInt("contareduzidacredito");
            oLancamento.idPagarFornecedorParcela = rst.getObject("id_pagarfornecedorparcela") == null ? -1 : rst.getInt("id_pagarfornecedorparcela");
            oLancamento.idReceberCaixaItem = rst.getObject("id_recebercaixaitem") == null ? -1 : rst.getInt("id_recebercaixaitem");
            oLancamento.idReceberChequeItem = rst.getObject("id_receberchequeitem") == null ? -1 : rst.getInt("id_receberchequeitem");
            oLancamento.idReceberContratoItem = rst.getObject("id_recebercontratoitem") == null ? -1 : rst.getInt("id_recebercontratoitem");
            oLancamento.idReceberConveniadoItem = rst.getObject("id_receberconveniadoitem") == null ? -1 : rst.getInt("id_receberconveniadoitem");
            oLancamento.idReceberCreditoRotativoItem = rst.getObject("id_recebercreditorotativoitem") == null ? -1 : rst.getInt("id_recebercreditorotativoitem");
            oLancamento.idReceberDevolucaoItem = rst.getObject("id_receberdevolucaoitem") == null ? -1 : rst.getInt("id_receberdevolucaoitem");
            oLancamento.idReceberVendaPrazoItem = rst.getObject("id_recebervendaprazoitem") == null ? -1 : rst.getInt("id_recebervendaprazoitem");
            oLancamento.idReceberVerbaItem = rst.getObject("id_receberverbaitem") == null ? -1 : rst.getInt("id_receberverbaitem");
            oLancamento.idReceberOutrasReceitasItem = rst.getObject("id_receberoutrasreceitasitem") == null ? -1 : rst.getInt("id_receberoutrasreceitasitem");

            oConciliacao.vLancamento.add(oLancamento);
        }

        stm.close();

        return oConciliacao;
    }

    public void salvar(ConciliacaoBancariaVO i_conciliacao, ConciliacaoBancariaLancamentoVO i_lancamento) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        try {
            Conexao.begin();
            stm = Conexao.createStatement();

            //verifica finalizado
            sql = new StringBuilder();
            sql.append("SELECT id_situacaoconciliacaobancaria FROM conciliacaobancaria WHERE id = " + i_conciliacao.id);

            rst = stm.executeQuery(sql.toString());

            if (rst.next()) {
                if (rst.getInt("id_situacaoconciliacaobancaria") == SituacaoConciliacaoBancaria.FINALIZADO.getId()) {
                    throw new VRException("A conciliação bancária já foi finalizada nesta data!");
                }
            }

            //exclui lançamento alteracao
            if (i_lancamento.id != 0) {
                excluirLancamento(i_conciliacao, i_lancamento);
            }

            //verifica saldo
            ConciliacaoBancariaSaldoVO oSaldo = getSaldo(i_conciliacao);

            oSaldo.saldo += i_lancamento.valorCredito - i_lancamento.valorDebito;

            //salva escopo
            sql = new StringBuilder();
            sql.append("SELECT id FROM conciliacaobancaria WHERE id = " + i_conciliacao.id);

            rst = stm.executeQuery(sql.toString());

            if (rst.next()) {
                sql = new StringBuilder();
                sql.append("UPDATE conciliacaobancaria SET");
                sql.append(" saldo = " + oSaldo.saldo);
                sql.append(" WHERE id = " + i_conciliacao.id);

                stm.execute(sql.toString());

            } else {
                sql = new StringBuilder();
                sql.append("INSERT INTO conciliacaobancaria (data, id_banco, agencia, conta, id_situacaoconciliacaobancaria, saldo) VALUES (");
                sql.append("'" + Util.formatDataBanco(i_conciliacao.data) + "', ");
                sql.append(i_conciliacao.idBanco + ", ");
                sql.append("'" + i_conciliacao.agencia + "', ");
                sql.append("'" + i_conciliacao.conta + "', ");
                sql.append(SituacaoConciliacaoBancaria.NAO_FINALIZADO.getId() + ", ");
                sql.append(oSaldo.saldo + ")");

                stm.execute(sql.toString());

                rst = stm.executeQuery("SELECT CURRVAL('conciliacaobancaria_id_seq') AS id");
                rst.next();

                i_conciliacao.id = rst.getLong("id");
            }

            //salva lancamento
            rst = stm.executeQuery("SELECT id FROM conciliacaobancarialancamento WHERE id = " + i_lancamento.id);

            String observacao = "DATA: " + i_conciliacao.data + ", BANCO: " + i_conciliacao.idBanco + ", AGENCIA: " + i_conciliacao.agencia + ", CONTA: " + i_conciliacao.conta + ", EVENTO: "
                    + i_lancamento.idContaContabilFinanceiro + ", DEBITO: " + Util.formatDecimal2(i_lancamento.valorDebito) + ", CREDITO: " + Util.formatDecimal2(i_lancamento.valorCredito);

            if (!rst.next()) {
                sql = new StringBuilder();
                sql.append("INSERT INTO conciliacaobancarialancamento (id_conciliacaobancaria, id_contacontabilfinanceiro, cnpj, observacao, valordebito,");
                sql.append(" valorcredito, id_tipoinscricao, id_contacontabilfiscaldebito, id_contacontabilfiscalcredito,");
                sql.append(" id_pagarfornecedorparcela, id_recebercaixaitem, id_receberchequeitem, id_recebercontratoitem, id_receberconveniadoitem,");
                sql.append(" id_recebercreditorotativoitem, id_receberdevolucaoitem, id_recebervendaprazoitem, id_receberverbaitem,");
                sql.append(" id_receberoutrasreceitasitem) VALUES (");
                sql.append(i_conciliacao.id + ", ");
                sql.append(i_lancamento.idContaContabilFinanceiro + ", ");
                sql.append(i_lancamento.cnpj + ", ");
                sql.append("'" + Util.substring(i_lancamento.observacao, 0, 280) + "', ");
                sql.append(i_lancamento.valorDebito + ", ");
                sql.append(i_lancamento.valorCredito + ", ");
                sql.append(i_lancamento.idTipoInscricao + ", ");
                sql.append((i_lancamento.idContaContabilFiscalDebito == -1 ? "NULL" : i_lancamento.idContaContabilFiscalDebito) + ", ");
                sql.append((i_lancamento.idContaContabilFiscalCredito == -1 ? "NULL" : i_lancamento.idContaContabilFiscalCredito) + ",");
                sql.append((i_lancamento.idPagarFornecedorParcela == -1 ? "NULL" : i_lancamento.idPagarFornecedorParcela) + ", ");
                sql.append((i_lancamento.idReceberCaixaItem == -1 ? "NULL" : i_lancamento.idReceberCaixaItem) + ", ");
                sql.append((i_lancamento.idReceberChequeItem == -1 ? "NULL" : i_lancamento.idReceberChequeItem) + ", ");
                sql.append((i_lancamento.idReceberContratoItem == -1 ? "NULL" : i_lancamento.idReceberContratoItem) + ", ");
                sql.append((i_lancamento.idReceberConveniadoItem == -1 ? "NULL" : i_lancamento.idReceberConveniadoItem) + ", ");
                sql.append((i_lancamento.idReceberCreditoRotativoItem == -1 ? "NULL" : i_lancamento.idReceberCreditoRotativoItem) + ", ");
                sql.append((i_lancamento.idReceberDevolucaoItem == -1 ? "NULL" : i_lancamento.idReceberDevolucaoItem) + ", ");
                sql.append((i_lancamento.idReceberVendaPrazoItem == -1 ? "NULL" : i_lancamento.idReceberVendaPrazoItem) + ", ");
                sql.append((i_lancamento.idReceberVerbaItem == -1 ? "NULL" : i_lancamento.idReceberVerbaItem) + ", ");
                sql.append((i_lancamento.idReceberOutrasReceitasItem == -1 ? "NULL" : i_lancamento.idReceberOutrasReceitasItem) + ")");

                stm.execute(sql.toString());

                rst = stm.executeQuery("SELECT CURRVAL('conciliacaobancarialancamento_id_seq') AS id");
                rst.next();

                i_lancamento.id = rst.getLong("id");

                if (i_lancamento.transferencia) {
                    ConciliacaoBancariaLancamentoVO oTransferencia = new ConciliacaoBancariaLancamentoVO();
                    oTransferencia.idContaContabilFinanceiro = i_lancamento.idContaContabilFinanceiro;
                    oTransferencia.observacao = i_lancamento.observacao;
                    oTransferencia.valorCredito = i_lancamento.valorDebito;
                    oTransferencia.valorDebito = i_lancamento.valorCredito;

                    adicionar(i_conciliacao.data, i_lancamento.idBanco, i_lancamento.agencia, i_lancamento.conta, oTransferencia);

                    i_lancamento.transferencia = false;
                }

                new LogTransacaoDAO().gerar(Formulario.FINANCEIRO_CONCILIACAO_BANCARIA, TipoTransacao.INCLUSAO, 0, observacao, i_conciliacao.id);

            } else {
                sql = new StringBuilder();
                sql.append("UPDATE conciliacaobancarialancamento SET");
                sql.append(" id_contacontabilfinanceiro = " + i_lancamento.idContaContabilFinanceiro + ", ");
                sql.append(" cnpj = " + i_lancamento.cnpj + ", ");
                sql.append(" observacao = '" + Util.substring(i_lancamento.observacao, 0, 280) + "', ");
                sql.append(" valordebito = " + i_lancamento.valorDebito + ", ");
                sql.append(" valorcredito = " + i_lancamento.valorCredito + ", ");
                sql.append(" id_tipoinscricao = " + i_lancamento.idTipoInscricao + ", ");
                sql.append(" id_contacontabilfiscaldebito = " + (i_lancamento.idContaContabilFiscalDebito == -1 ? "NULL" : i_lancamento.idContaContabilFiscalDebito) + ", ");
                sql.append(" id_contacontabilfiscalcredito = " + (i_lancamento.idContaContabilFiscalCredito == -1 ? "NULL" : i_lancamento.idContaContabilFiscalCredito));
                sql.append(" WHERE id = " + i_lancamento.id);

                stm.execute(sql.toString());

                new LogTransacaoDAO().gerar(Formulario.FINANCEIRO_CONCILIACAO_BANCARIA, TipoTransacao.ALTERACAO, 0, observacao, i_conciliacao.id);
            }

            //atualiza saldo posterior
            atualizarSaldo(i_conciliacao, oSaldo.saldo);

            //commit
            stm.close();
            Conexao.commit();

        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void excluirLancamento(ConciliacaoBancariaVO i_conciliacao, ConciliacaoBancariaLancamentoVO i_lancamento) throws Exception {
        Statement stm = null;
        StringBuilder sql = null;

        try {
            Conexao.begin();
            stm = Conexao.createStatement();

            stm.execute("DELETE FROM conciliacaobancarialancamento WHERE id = " + i_lancamento.id);

            //atualiza saldo
            ConciliacaoBancariaSaldoVO oSaldo = getSaldo(i_conciliacao);

            sql = new StringBuilder();
            sql.append("UPDATE conciliacaobancaria SET");
            sql.append(" saldo = " + oSaldo.saldo);
            sql.append(" WHERE id = " + i_conciliacao.id);

            stm.execute(sql.toString());

            //atualiza saldo posterior
            atualizarSaldo(i_conciliacao, oSaldo.saldo);

            //grava log transacao
            String observacao = "DATA: " + i_conciliacao.data + ", BANCO: " + i_conciliacao.idBanco + ", AGENCIA: " + i_conciliacao.agencia + ", CONTA: " + i_conciliacao.conta + ", EVENTO: "
                    + i_lancamento.idContaContabilFinanceiro + ", DEBITO: " + Util.formatDecimal2(i_lancamento.valorDebito) + ", CREDITO: " + Util.formatDecimal2(i_lancamento.valorCredito);

            new LogTransacaoDAO().gerar(Formulario.FINANCEIRO_CONCILIACAO_BANCARIA, TipoTransacao.EXCLUSAO, 0, observacao, i_conciliacao.id);

            stm.close();
            Conexao.commit();

        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public ConciliacaoBancariaSaldoVO getSaldo(ConciliacaoBancariaVO i_conciliacao) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        stm = Conexao.createStatement();

        ConciliacaoBancariaSaldoVO oSaldo = new ConciliacaoBancariaSaldoVO();

        //obtem lancamentos
        sql = new StringBuilder();
        sql.append("SELECT COALESCE(SUM(cbl.valordebito), 0) AS valordebito, COALESCE(SUM(cbl.valorcredito), 0) AS valorcredito");
        sql.append(" FROM conciliacaobancaria AS cb");
        sql.append(" LEFT JOIN conciliacaobancarialancamento AS cbl ON cbl.id_conciliacaobancaria = cb.id");
        sql.append(" WHERE data = '" + Util.formatDataBanco(i_conciliacao.data) + "'");
        sql.append(" AND id_banco = " + i_conciliacao.idBanco);
        sql.append(" AND agencia = '" + i_conciliacao.agencia + "'");
        sql.append(" AND conta = '" + i_conciliacao.conta + "'");

        rst = stm.executeQuery(sql.toString());

        if (rst.next()) {
            oSaldo.valorDebito = rst.getDouble("valordebito");
            oSaldo.valorCredito = rst.getDouble("valorcredito");
        }

        //verifica saldo anterior
        sql = new StringBuilder();
        sql.append("SELECT saldo FROM conciliacaobancaria");
        sql.append(" WHERE id_banco = " + i_conciliacao.idBanco);
        sql.append(" AND agencia = '" + i_conciliacao.agencia + "'");
        sql.append(" AND conta = '" + i_conciliacao.conta + "'");
        sql.append(" AND data = (SELECT MAX(data) AS data FROM conciliacaobancaria");
        sql.append(" WHERE data < '" + Util.formatDataBanco(i_conciliacao.data) + "'");
        sql.append(" AND id_banco = " + i_conciliacao.idBanco);
        sql.append(" AND agencia = '" + i_conciliacao.agencia + "'");
        sql.append(" AND conta = '" + i_conciliacao.conta + "')");

        rst = stm.executeQuery(sql.toString());

        if (rst.next()) {
            oSaldo.saldoAnterior = rst.getDouble("saldo");
        }

        oSaldo.saldo = oSaldo.saldoAnterior - oSaldo.valorDebito + oSaldo.valorCredito;

        return oSaldo;
    }

    private void atualizarSaldo(ConciliacaoBancariaVO i_conciliacao, double i_saldoAtual) throws Exception {
        Statement stm = null;
        Statement stmSaldo = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        stm = Conexao.createStatement();
        stmSaldo = Conexao.createStatement();

        double saldo = i_saldoAtual;

        sql = new StringBuilder();
        sql.append("SELECT cb.id, COALESCE(SUM(cbl.valordebito), 0) AS valordebito, COALESCE(SUM(cbl.valorcredito), 0) AS valorcredito");
        sql.append(" FROM conciliacaobancaria cb");
        sql.append(" LEFT JOIN conciliacaobancarialancamento cbl ON cbl.id_conciliacaobancaria = cb.id");
        sql.append(" WHERE cb.id_banco = " + i_conciliacao.idBanco);
        sql.append(" AND cb.agencia = '" + i_conciliacao.agencia + "'");
        sql.append(" AND cb.conta = '" + i_conciliacao.conta + "'");
        sql.append(" AND cb.data > '" + Util.formatDataBanco(i_conciliacao.data) + "'");
        sql.append(" GROUP BY cb.id, cb.data");
        sql.append(" ORDER BY cb.data");

        rst = stm.executeQuery(sql.toString());

        while (rst.next()) {
            saldo += rst.getDouble("valorcredito") - rst.getDouble("valordebito");

            stmSaldo.execute("UPDATE conciliacaobancaria SET saldo = " + saldo + " WHERE id = " + rst.getLong("id"));
        }

        stm.close();
        stmSaldo.close();
    }

    public void excluirLancamentoVendaPrazoItem(ReceberVendaPrazoItemVO i_receberVendaPrazoItem) throws Exception {
        Statement stm = null;

        stm = Conexao.createStatement();

        stm.execute("UPDATE conciliacaobancarialancamento SET id_recebervendaprazoitem = NULL WHERE id_recebervendaprazoitem = " + i_receberVendaPrazoItem.id);

        stm.execute("DELETE FROM conciliacaobancarialancamento WHERE id_recebervendaprazoitem = " + i_receberVendaPrazoItem.id);

        stm.close();
    }
}
