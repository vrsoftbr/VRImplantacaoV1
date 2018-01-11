package vrimplantacao2.dao.cadastro.financeiro.contaspagar;

import java.sql.ResultSet;
import java.sql.Statement;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.utils.sql.SQLUtils;
import vrimplantacao2.vo.cadastro.financeiro.ContaPagarAnteriorVO;
import vrimplantacao2.vo.cadastro.financeiro.PagarOutrasDespesasVO;

/**
 *
 * @author Leandro
 */
public class ContaPagarAnteriorDAO {

    
    public void createTable() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(
                    "create table if not exists implantacao.codant_contapagar (\n" +
                    "	sistema varchar not null,\n" +
                    "	agrupador varchar not null,\n" +
                    "	id varchar not null,\n" +
                    "	id_fornecedor varchar not null,\n" +
                    "	codigoatual integer,\n" +
                    "	emissao date,\n" +
                    "	vencimento date,\n" +
                    "	documento varchar,\n" +
                    "	valor numeric default 0 not null,\n" +
                    "	primary key (sistema, agrupador, id)\n" +
                    ")"
            );
        }
    }    
    
    
    public MultiMap<String, ContaPagarAnteriorVO> getAnteriores(String sistema, String agrupador) throws Exception {
        MultiMap<String, ContaPagarAnteriorVO> result = new MultiMap<>();
        
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n" +
                    "	sistema, \n" +
                    "	agrupador, \n" +
                    "	id, \n" +
                    "	id_fornecedor, \n" +
                    "	codigoatual, \n" +
                    "	emissao, \n" +
                    "   vencimento, \n" +
                    "   documento,\n" +
                    "   valor\n" +
                    "FROM \n" +
                    "	implantacao.codant_contapagar cp\n" +
                    "where\n" +
                    "	sistema = " + SQLUtils.stringSQL(sistema) + " and\n" +
                    "	agrupador = " + SQLUtils.stringSQL(agrupador) + "\n" +
                    "order by\n" +
                    "	sistema, \n" +
                    "	agrupador, \n" +
                    "	id"
            )) {
                while (rst.next()) {
                    ContaPagarAnteriorVO ant = new ContaPagarAnteriorVO();
                    
                    ant.setSistema(rst.getString("sistema"));
                    ant.setAgrupador(rst.getString("agrupador"));
                    ant.setId(rst.getString("id"));
                    ant.setId_fornecedor(rst.getString("id_fornecedor"));
                    if (rst.getString("codigoatual") != null) {
                        ant.setCodigoAtual(new PagarOutrasDespesasVO());
                        ant.getCodigoAtual().setId(rst.getInt("codigoatual"));
                    }
                    ant.setDataEmissao(rst.getDate("emissao"));
                    ant.setDataVencimento(rst.getDate("vencimento"));
                    ant.setDocumento(rst.getString("documento"));
                    ant.setValor(rst.getDouble("valor"));
                    
                    result.put(
                            ant, 
                            ant.getSistema(),
                            ant.getAgrupador(),
                            ant.getId()
                    );
                }
            }
        }
        
        return result;
    }

    public void atualizar(ContaPagarAnteriorVO anterior) throws Exception {
        SQLBuilder sql = new SQLBuilder();
        sql.setSchema("implantacao");
        sql.setTableName("codant_contapagar");
        sql.setWhere(
                "sistema = " + SQLUtils.stringSQL(anterior.getSistema()) + " and\n" +
                "agrupador = " + SQLUtils.stringSQL(anterior.getAgrupador()) + " and\n" +
                "id = " + SQLUtils.stringSQL(anterior.getId())
        );
        if (anterior.getCodigoAtual() != null) {
            sql.put("codigoatual", anterior.getCodigoAtual().getId());
        }
        sql.put("id_fornecedor", anterior.getId_fornecedor());
        sql.put("emissao", anterior.getDataEmissao());
        sql.put("vencimento", anterior.getDataVencimento());
        sql.put("documento", anterior.getDocumento());
        sql.put("valor", anterior.getValor());
        
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(sql.getUpdate());
        }
    }

    public void gravar(ContaPagarAnteriorVO anterior) throws Exception {
        SQLBuilder sql = new SQLBuilder();
        sql.setSchema("implantacao");
        sql.setTableName("codant_contapagar");
        sql.put("sistema", anterior.getSistema());
        sql.put("agrupador", anterior.getAgrupador());
        sql.put("id", anterior.getId());
        if (anterior.getCodigoAtual() != null) {
            sql.put("codigoatual", anterior.getCodigoAtual().getId());
        }
        sql.put("id_fornecedor", anterior.getId_fornecedor());
        sql.put("emissao", anterior.getDataEmissao());
        sql.put("vencimento", anterior.getDataVencimento());
        sql.put("documento", anterior.getDocumento());
        sql.put("valor", anterior.getValor());
        
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(sql.getInsert());
        }
    }
    
}
