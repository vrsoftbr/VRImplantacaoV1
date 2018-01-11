package vrimplantacao2.dao.cadastro.cheque;

import java.sql.ResultSet;
import java.sql.Statement;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.cliente.cheque.ChequeVO;

/**
 *
 * @author Leandro
 */
public class ChequeDAO {

    public void gravarCheque(ChequeVO ch) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            sql.setTableName("recebercheque");
            sql.getReturning().add("id");
            sql.put("id_loja", ch.getId_loja());
            sql.put("cpf", ch.getCpf());
            sql.put("numerocheque", ch.getNumeroCheque());
            sql.put("id_banco", ch.getId_banco());
            sql.put("agencia", ch.getAgencia());
            sql.put("conta", ch.getConta());
            sql.put("data", ch.getData());
            sql.put("id_plano", ch.getId_plano(), 0);
            sql.put("numerocupom", ch.getNumeroCupom());
            sql.put("ecf", ch.getEcf());
            sql.put("valor", ch.getValor());
            sql.put("datadeposito", ch.getDataDeposito());
            sql.put("lancamentomanual", ch.isLancamentoManual());
            sql.put("rg", ch.getRg());
            sql.put("telefone", ch.getTelefone());
            sql.put("nome", ch.getNome());
            sql.put("observacao", ch.getObservacao());
            sql.put("id_situacaorecebercheque", ch.getSituacaoCheque().getId());
            sql.put("id_tipoLocalCobranca", ch.getTipoLocalCobranca());
            sql.put("cmc7", ch.getCmc7());
            sql.put("dataDevolucao", ch.getDataDevolucao());
            sql.put("id_tipoalinea", ch.getTipoAlinea().getId());
            sql.put("id_tipoinscricao", ch.getTipoInscricao().getId());
            sql.put("dataEnvioCobranca", ch.getDataEnvioCobranca());
            sql.put("valorPagarFornecedor", ch.getValorPagarFornecedor());
            sql.put("id_boleto", ch.getId_boleto(), 0);
            sql.put("operadorClienteBloqueado", ch.getOperadorClienteBloqueado());
            sql.put("operadorExcedeLimite", ch.getOperadorExcedeLimite());
            sql.put("operadorProblemaCheque", ch.getOperadorProblemaCheque());
            sql.put("operadorChequeBloqueado", ch.getOperadorChequeBloqueado());
            sql.put("valorjuros", ch.getValorJuros());
            sql.put("id_tipovistaprazo", ch.getTipoVistaPrazo().getId());
            sql.put("justificativa", ch.getJustificativa());
            sql.put("valoracrescimo", ch.getValorAcrescimo());
            sql.put("valorinicial", ch.getValorInicial());
            sql.put("dataHoraAlteracao", ch.getDataHoraAlteracao());
            sql.put("operadorClienteNaoCadastrado", ch.getOperadorClienteNaoCadastrado());
       
            try (ResultSet rst = stm.executeQuery(
                    sql.getInsert()
            )) {
                rst.next();
                ch.setId(rst.getInt("id"));
            }
        }
    }
    
}
