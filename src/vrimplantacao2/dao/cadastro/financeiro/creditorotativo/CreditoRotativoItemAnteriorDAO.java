package vrimplantacao2.dao.cadastro.financeiro.creditorotativo;

import java.sql.ResultSet;
import java.sql.Statement;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.utils.sql.SQLUtils;
import vrimplantacao2.vo.cadastro.cliente.rotativo.CreditoRotativoItemAnteriorVO;

/**
 *
 * @author Leandro
 */
public class CreditoRotativoItemAnteriorDAO {
    
    public void createTable() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(
                    "create table if not exists implantacao.codant_recebercreditorotativoitem (\n" +
                    "	sistema varchar not null,\n" +
                    "	loja varchar not null,\n" +
                    "	id_creditorotativo varchar not null,\n" +
                    "	id varchar not null,\n" +
                    "	codigoatual integer,\n" +
                    "	valor numeric(10,4),\n" +
                    "	valordesconto numeric(10,4),\n" +
                    "	valormulta numeric(10,4),\n" +
                    "	datapagamento date,\n" +
                    "	primary key (sistema, loja, id_creditorotativo, id)\n" +
                    ");"
            );
        }
    }

    public MultiMap<String, CreditoRotativoItemAnteriorVO> getBaixasAnteriores(String sistema, String loja) throws Exception {
        MultiMap<String, CreditoRotativoItemAnteriorVO> result = new MultiMap<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	sistema,\n" +
                    "	loja,\n" +
                    "	id_creditorotativo,\n" +
                    "	id,\n" +
                    "	codigoatual,\n" +
                    "	valor,\n" +
                    "	valordesconto,\n" +
                    "	valormulta,\n" +
                    "	datapagamento\n" +
                    "from \n" +
                    "	implantacao.codant_recebercreditorotativoitem\n" +
                    (sistema != null && loja != null ?                        
                        "where\n" +
                        "	sistema = " + SQLUtils.stringSQL(sistema) + " and\n" +
                        "	loja = " + SQLUtils.stringSQL(loja) + "\n" :
                        "") +
                    "order by\n" +
                    "	sistema,\n" +
                    "	loja,\n" +
                    "	id_creditorotativo,\n" +
                    "	id"
            )) {
                while (rst.next()) {
                    CreditoRotativoItemAnteriorVO ant = new CreditoRotativoItemAnteriorVO();
                    
                    ant.setSistema(rst.getString("sistema"));
                    ant.setLoja(rst.getString("loja"));
                    ant.setIdCreditoRotativo(rst.getString("id_creditorotativo"));
                    ant.setId(rst.getString("id"));                    
                    ant.setCodigoAtual(rst.getInt("codigoatual"));
                    ant.setValor(rst.getDouble("valor"));
                    ant.setValorDesconto(rst.getDouble("valordesconto"));
                    ant.setValorMulta(rst.getDouble("valormulta"));
                    ant.setDataPagamento(rst.getDate("datapagamento"));
                    
                    result.put(
                            ant,
                            ant.getSistema(),
                            ant.getLoja(),
                            ant.getIdCreditoRotativo(),
                            ant.getId()
                    );
                }
            }
        }
        return result;
    }

    public void gravarRotativoItemAnterior(CreditoRotativoItemAnteriorVO parcAnt) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            sql.setSchema("implantacao");
            sql.setTableName("codant_recebercreditorotativoitem");
            sql.put("sistema", parcAnt.getSistema());
            sql.put("loja", parcAnt.getLoja());
            sql.put("id_creditorotativo", parcAnt.getIdCreditoRotativo());
            sql.put("id", parcAnt.getId());
            sql.put("codigoatual", parcAnt.getCodigoAtual(), 0);
            sql.put("valor", parcAnt.getValor());
            sql.put("valordesconto", parcAnt.getValorDesconto());
            sql.put("valormulta", parcAnt.getValorMulta());
            sql.put("datapagamento", parcAnt.getDataPagamento());
            stm.execute(
                    sql.getInsert()
            );
        }
    }

    
    
}
