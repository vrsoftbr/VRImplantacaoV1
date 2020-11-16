package vrimplantacao2.dao.interfaces.linear;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import vrimplantacao.classe.ConexaoMySQL;
import vrimplantacao2.dao.cadastro.venda.MultiStatementIterator;
import vrimplantacao2.vo.importacao.VendaIMP;

/**
 *
 * @author leandro
 */
public class LinearVendaIterator extends MultiStatementIterator<VendaIMP> {

    public LinearVendaIterator(String idLojas, Date dataInicial, Date dataTermino) {        
        super(
                new LinearNextBuilder(),
                new LinearStatementBuilder()
        );
        if (dataInicial == null) throw new NullPointerException("Informe a data inicial");
        if (dataTermino == null) throw new NullPointerException("Informe a data final");
    }
    
    private String getFullSQL(String idLojaCliente) throws Exception {

        return
            "select\n" +
            "	v.id_cupom id,\n" +
            "	v.numero_cupom,\n" +
            "	v.caixa,\n" +
            "	v.`data`,\n" +
            "	min(v.hora) horainicio,\n" +
            "	max(v.hora) horafim,\n" +
            "	min(case when v.cupom_cancelado = 'S' then 1 else 0 end) cancelado,\n" +
            "	sum(v.valor_total) subtotalimpressora,\n" +
            "	sum(v.valor_acrescimo_cupom) valoracrescimo,\n" +
            "	sum(v.valor_desconto_cupom) valordesconto,\n" +
            "	v.serie_aparelho numeroserie,\n" +
            "	v.tipo_fiscal modeloimpressora\n" +
            "from\n" +
            "	view_vendas_pdv_antiga v\n" +
            "where\n" +
            "	v.`data` >= '{DATA_INICIO}' and\n" +
            "	v.`data` <= '{DATA_TERMINO}' and\n" +
            "	v.loja = " + idLojaCliente + "\n" +
            "group by\n" +
            "	v.id_cupom,\n" +
            "	v.numero_cupom,\n" +
            "	v.caixa,\n" +
            "	v.`data`,\n" +
            "	v.serie_aparelho,\n" +
            "	v.tipo_fiscal";
    }
    
    private static class LinearNextBuilder implements NextBuilder<VendaIMP> {

        @Override
        public VendaIMP makeNext(ResultSet rst) throws Exception {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
        
    }

    private static class LinearStatementBuilder implements StatementBuilder {

        public LinearStatementBuilder() {
        }

        @Override
        public Statement makeStatement() throws Exception {
            return ConexaoMySQL.getConexao().createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        }
    }
    
}
