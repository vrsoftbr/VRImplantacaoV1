/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.interfaces.dellasta;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import vrimplantacao2.dao.cadastro.venda.MultiStatementIterator;
import vrimplantacao2.utils.sql.SQLUtils;
import vrimplantacao2.vo.importacao.VendaItemIMP;
import vrimplantacao2_5.dao.conexao.ConexaoFirebird;

/**
 *
 * @author Michael
 */
public class PrismaFlexVendaItemIterator extends MultiStatementIterator<VendaItemIMP> {

    public PrismaFlexVendaItemIterator(Date dataInicial, Date dataTermino) {
        super(
                new PrismaFlexVendaItemNextBuild(),
                new PrismaFlexVendaItemStatementBuilder()
        );
        if (dataInicial == null) {
            throw new NullPointerException("Informe a data inicial");
        }
        if (dataTermino == null) {
            throw new NullPointerException("Informe a data final");
        }
        //SP_12_2020     
        for (SQLUtils.Intervalo intervalo : SQLUtils.intervalosDiarios(dataInicial, dataTermino)) {
            this.addStatement(getFullSQL(intervalo));
        }
    }

    private static final SimpleDateFormat TABLE_NAME_DATE = new SimpleDateFormat("ddMMyyyy");

    private String getNomeTabela(Date dataInicial) {
        return String.format("ITENS%s", TABLE_NAME_DATE.format(dataInicial));
    }

    private String getFullSQL(SQLUtils.Intervalo intervalo) {
        return "SELECT\n"
                + "	CASE \n"
                + "    	WHEN v.VENREFCX LIKE 'SORTIDAO_SRV'\n"
                + "    	THEN 50911440|| v.VENNUMECF || v.VENNCUPOM\n"
                + "    	ELSE v.VENREFCX || v.VENNUMECF || v.VENNCUPOM\n"
                + "    END id_venda,\n"
                + "	VENREFCX || VENNUMPDV || VENNUITEM id_item,\n"
                + "	VENNUITEM nroitem,\n"
                + " PROCODIGO produto,\n"
                + "	UPPER(PROUNIDME) unidade,\n"
                + "	VENCBARRA codigobarras,\n"
                + "	VENPRODUT descricao,\n"
                + "	VENQUANTI quantidade,\n"
                + "	VENPRECOU precovenda\n"
                + "FROM\n"
                + "	" + getNomeTabela(intervalo.dataInicial) + " v\n"
                + "WHERE \n"
                + " v.EMPCODIGO = 1\n"
                + "AND v.VENCANCELADO = 'N'\n"
                + "ORDER BY 1";
    }

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public static String formatID(int id, int numerocupom, int nritem, Date data) {
        return String.format("%d-%d-%d-%s", id, numerocupom, nritem, DATE_FORMAT.format(data));
    }

    private static class PrismaFlexVendaItemNextBuild implements NextBuilder<VendaItemIMP> {

        int conti = 0;

        @Override
        public VendaItemIMP makeNext(ResultSet rs) throws Exception {
            VendaItemIMP v = new VendaItemIMP();
            
            String id = rs.getString("id_venda");
            v.setVenda(id);
            v.setId(rs.getString("id_item") + conti++);
            v.setSequencia(rs.getInt("nroitem"));
            v.setProduto(rs.getString("produto"));
            v.setDescricaoReduzida(rs.getString("descricao"));
            v.setQuantidade(rs.getDouble("quantidade"));
            v.setPrecoVenda(rs.getDouble("precovenda"));
            //v.setCancelado(rs.getBoolean("cancelada"));
            //v.setValorDesconto(rs.getDouble("Desconto"));
            v.setCodigoBarras(rs.getString("codigobarras"));
            v.setUnidadeMedida(rs.getString("unidade"));

            return v;
        }

    }

    private static class PrismaFlexVendaItemStatementBuilder implements StatementBuilder {

        @Override
        public Statement makeStatement() throws Exception {
            return ConexaoFirebird.getConexao().createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        }
    }
}
