package vrimplantacao2.dao.interfaces.linear;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import vrimplantacao.classe.ConexaoMySQL;
import vrimplantacao2.dao.cadastro.venda.MultiStatementIterator;
import vrimplantacao2.vo.importacao.VendaItemIMP;

/**
 *
 * @author leandro
 */
public class LinearVendaItemIterator extends MultiStatementIterator<VendaItemIMP> {

    public LinearVendaItemIterator(String idLojas, Date dataInicial, Date dataTermino) {
        super(
                new LinearVendaItemNextBuild(),
                new LinearVendaItemStatementBuilder()
        );
        if (dataInicial == null) throw new NullPointerException("Informe a data inicial");
        if (dataTermino == null) throw new NullPointerException("Informe a data final");
    }
    
    private static class LinearVendaItemNextBuild implements NextBuilder<VendaItemIMP> {
        @Override
        public VendaItemIMP makeNext(ResultSet rst) throws Exception {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }        
    }
    
    private static class LinearVendaItemStatementBuilder implements StatementBuilder {
        @Override
        public Statement makeStatement() throws Exception {
            return ConexaoMySQL.getConexao().createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        }        
    }
    
}
