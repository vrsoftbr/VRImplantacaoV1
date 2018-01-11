package vrimplantacao.dao.financeiro;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import vrimplantacao.classe.Global;
import vrimplantacao.vo.notafiscal.ContratoLancamentoVO;
import vrframework.classe.Conexao;
import vrframework.classe.Util;
import vrframework.classe.VRException;

public class ContratoLancamentoDAO {

    public void excluir(long i_id) throws Exception {
        Statement stm = null;
        StringBuilder sql = null;
        ResultSet rst = null;

        try {
            Conexao.begin();
            stm = Conexao.createStatement();

            sql = new StringBuilder();
            sql.append("SELECT id FROM recebercontratolancamento");
            sql.append(" WHERE id_contratolancamento = " + i_id);

            rst = stm.executeQuery(sql.toString());

            if (rst.next()) {
                throw new VRException("O contrato possui recebimentos e não pode ser excluído!");
            }

            sql = new StringBuilder();
            sql.append("DELETE FROM contratolancamento WHERE id = " + i_id);

            stm.execute(sql.toString());

            stm.close();
            Conexao.commit();

        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }
    
    public void adicionar(ContratoLancamentoVO i_lancamento, List<ContratoLancamentoVO> i_vLancamento) throws Exception {
        boolean achou = false;

        for (ContratoLancamentoVO oLancamento : i_vLancamento) {
            if (oLancamento.idTipoAcordo == i_lancamento.idTipoAcordo && oLancamento.percentual == i_lancamento.percentual) {
                oLancamento.valorAcordo += i_lancamento.valorAcordo;
                oLancamento.valorBaseCalculo += i_lancamento.valorBaseCalculo;

                achou = true;
                break;
            }
        }

        if (!achou) {
            i_vLancamento.add(i_lancamento);
        }
    }

    public void salvar(ContratoLancamentoVO i_lancamento) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        try {
            Conexao.begin();
            stm = Conexao.createStatement();

            sql = new StringBuilder();
            sql.append("INSERT INTO contratolancamento (id_loja, id_contrato, datalancamento, id_tipoacordo, percentual, valoracordo,");
            sql.append(" id_tiporecebimento, finalizado, id_notaentrada, id_tipocontrato, valorbasecalculo, id_notadespesa, id_notasaida) VALUES (");
            sql.append(Global.idLoja + ", ");
            sql.append(i_lancamento.idContrato + ", ");
            sql.append("'" + Util.formatDataBanco(i_lancamento.dataLancamento) + "', ");
            sql.append(i_lancamento.idTipoAcordo + ", ");
            sql.append(i_lancamento.percentual + ", ");
            sql.append(i_lancamento.valorAcordo + ", ");
            sql.append(i_lancamento.idTipoRecebimento + ", ");
            sql.append(i_lancamento.finalizado + ", ");
            sql.append((i_lancamento.idNotaEntrada == -1 ? "NULL" : i_lancamento.idNotaEntrada) + ", ");
            sql.append(i_lancamento.idTipoContrato + ", ");
            sql.append(i_lancamento.valorBaseCalculo + ", ");
            sql.append((i_lancamento.idNotaDespesa == -1 ? "NULL" : i_lancamento.idNotaDespesa) + ", ");
            sql.append((i_lancamento.idNotaSaida == -1 ? "NULL" : i_lancamento.idNotaSaida) + ")");

            stm.execute(sql.toString());

            rst = stm.executeQuery("SELECT CURRVAL('contratolancamento_id_seq') AS id");
            rst.next();

            i_lancamento.id = rst.getLong("id");

            stm.close();
            Conexao.commit();

        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }
}
