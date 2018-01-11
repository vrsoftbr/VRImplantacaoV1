package vrimplantacao2.dao.cadastro.financeiro.contaspagar;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.financeiro.PagarOutrasDespesasVencimentoVO;

/**
 *
 * @author Leandro
 */
public class PagarOutrasDespesasVencimentoDAO {
    
    private static final SimpleDateFormat FORMATER = new SimpleDateFormat("yyyy-MM-dd");

    public void gravar(PagarOutrasDespesasVencimentoVO vo) throws Exception {
        SQLBuilder sql = new SQLBuilder();
        sql.setTableName("pagaroutrasdespesasvencimento");
        sql.put("id_pagaroutrasdespesas", vo.getPagarOutrasDespesas().getId());
        sql.put("datavencimento", vo.getDataVencimento());
        sql.put("valor", vo.getValor());
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(sql.getInsert());
        }
    }

    public MultiMap<String, Void> getPagamentos() throws Exception {
        MultiMap<String, Void> result = new MultiMap<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	id_pagaroutrasdespesas,\n" +
                    "	datavencimento,\n" +
                    "	valor\n" +
                    "from\n" +
                    "	pagaroutrasdespesasvencimento\n" +
                    "order by\n" +
                    "	id_pagaroutrasdespesas,\n" +
                    "	datavencimento,\n" +
                    "	valor"
            )) {
                while (rst.next()) {
                    result.put(
                            null, 
                            rst.getString("id_pagaroutrasdespesas"),
                            FORMATER.format(rst.getDate("datavencimento")),
                            String.format("%.2f", rst.getDouble("valor"))
                    );
                }
            }
        }
        return result;
    }
    
}
