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
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.venda.MultiStatementIterator;
import vrimplantacao2.utils.sql.SQLUtils;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2_5.dao.conexao.ConexaoFirebird;

/**
 *
 * @author Michael
 */
public class PrismaFlexVendaIterator extends MultiStatementIterator<VendaIMP> {

    public PrismaFlexVendaIterator(Date dataInicial, Date dataTermino) {
        super(
                new PrismaFlexNextBuilder(),
                new PrismaFlexStatementBuilder()
        );
        if (dataInicial == null) {
            throw new NullPointerException("Informe a data inicial");
        }
        if (dataTermino == null) {
            throw new NullPointerException("Informe a data final");
        }
        //CP_12_2020
        for (SQLUtils.Intervalo intervalo : SQLUtils.intervalosDiarios(dataInicial, dataTermino)) {
            this.addStatement(getFullSQL(intervalo));
        }
    }

    private static final SimpleDateFormat TABLE_NAME_DATE = new SimpleDateFormat("ddMMyyyy");
    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    //private static final SimpleDateFormat FORMATA_HORA = new SimpleDateFormat("HH:mm:ss");

    private String getNomeTabela(Date dataInicial) {
        return String.format("ITENS%s", TABLE_NAME_DATE.format(dataInicial));
    }

    private String getFullSQL(SQLUtils.Intervalo intervalo) {
        return "SELECT\n"
                + "	DISTINCT \n"
                + "    CASE \n"
                + "    	WHEN v.VENREFCX LIKE 'SORTIDAO_SRV'\n"
                + "    	THEN 50911440|| v.VENNUMECF || v.VENNCUPOM\n"
                + "    	ELSE v.VENREFCX || v.VENNUMECF || v.VENNCUPOM\n"
                + "    END id_venda,\n"
                + "	v.VENNUMECF ecf,\n"
                + "	substring(v.VENNCUPOM from 3) cupom,\n"
                + "	v.VENDATA data\n"
                + "FROM\n"
                + "	" + getNomeTabela(intervalo.dataInicial) + " v\n"
                + "WHERE \n"
                + " v.EMPCODIGO = 1\n"
                + "AND v.VENCANCELADO = 'N'\n"
                + "ORDER BY 1";
    }

    public static String formatID(int numerocupom, int id, int ecf, Date data) {
        return String.format("%d-%d-%d-%s", numerocupom, id, ecf, format.format(data));
    }

    private static class PrismaFlexNextBuilder implements MultiStatementIterator.NextBuilder<VendaIMP> {

        int contC = 0;

        @Override
        public VendaIMP makeNext(ResultSet rs) throws Exception {
            VendaIMP v = new VendaIMP();
            
            String id = rs.getString("id_venda");
            v.setId(id);
            v.setNumeroCupom(Utils.stringToInt(rs.getString("cupom") + String.valueOf(contC++)));
            v.setEcf(rs.getInt("ecf"));
            v.setData(rs.getDate("data"));
            //v.setHoraInicio(FORMATA_HORA.parse(rs.getString("hora")));
            //v.setCancelado(rs.getBoolean("cancelada"));

            return v;
        }
    }

    private static class PrismaFlexStatementBuilder implements MultiStatementIterator.StatementBuilder {

        @Override
        public Statement makeStatement() throws Exception {
            return ConexaoFirebird.getConexao().createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        }
    }
}
