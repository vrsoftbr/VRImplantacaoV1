package vrimplantacao2.dao.cadastro.financeiro.recebercaixa;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.utils.sql.SQLUtils;
import vrimplantacao2.vo.cadastro.financeiro.recebimentocaixa.RecebimentoCaixaAnteriorVO;
import vrimplantacao2.vo.cadastro.financeiro.recebimentocaixa.RecebimentoCaixaVO;

/**
 * Classe que controla as operações na tabela implantacao.codant_recebercaixa.
 * @author Leandro
 */
public class ReceberCaixaAnteriorDAO {

    public ReceberCaixaAnteriorDAO() throws Exception {
        createTable();
    }
    
    private void createTable() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(
                    "create table if not exists implantacao.codant_recebercaixa (\n" +
                    "	sistema varchar not null,\n" +
                    "	agrupador varchar not null,\n" +
                    "	id varchar not null,\n" +
                    "	codigoatual integer,\n" +
                    "	id_tiporecebivel varchar not null,\n" +
                    "	emissao date not null,\n" +
                    "	vencimento date not null,\n" +
                    "	valor numeric not null,\n" +
                    "	primary key (sistema, agrupador, id)\n" +
                    ");"
            );
        }
    }

    public Map<String, RecebimentoCaixaAnteriorVO> getAnteriores(String sistema, String agrupador) throws Exception {
        Map<String, RecebimentoCaixaAnteriorVO> result = new LinkedHashMap<>();
        
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	ant.sistema,\n" +
                    "	ant.agrupador,\n" +
                    "	ant.id,\n" +
                    "	ant.codigoatual,\n" +
                    "	ant.id_tiporecebivel,\n" +
                    "	ant.emissao,\n" +
                    "	ant.vencimento,\n" +
                    "	ant.valor\n" +
                    "from \n" +
                    "	implantacao.codant_recebercaixa ant\n" +
                    "order by\n" +
                    "	ant.sistema,\n" +
                    "	ant.agrupador,\n" +
                    "	ant.id"
            )) {
                while (rst.next()) {
                    RecebimentoCaixaAnteriorVO ant = new RecebimentoCaixaAnteriorVO();
                    
                    ant.setSistema(rst.getString("sistema"));
                    ant.setAgrupador(rst.getString("agrupador"));
                    ant.setId(rst.getString("id"));
                    if (rst.getString("codigoatual") != null) {
                        RecebimentoCaixaVO vo = new RecebimentoCaixaVO();
                        vo.setId(rst.getInt("codigoatual"));
                        ant.setCodigoAtual(vo);
                    }
                    ant.setDataEmissao(rst.getDate("emissao"));
                    ant.setVencimento(rst.getDate("vencimento"));
                    ant.setIdTipoRecebivel(rst.getString("id_tiporecebivel"));
                    ant.setValor(rst.getDouble("valor"));
                    
                    result.put(ant.getId(), ant);
                }
            }
        }
        
        return result;
    }

    public void gravarRecebimentoCaixaAnterior(RecebimentoCaixaAnteriorVO anterior) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            
            sql.setSchema("implantacao");
            sql.setTableName("codant_recebercaixa");
            sql.put("sistema", anterior.getSistema());
            sql.put("agrupador", anterior.getAgrupador());
            sql.put("id", anterior.getId());
            if (anterior.getCodigoAtual() != null) {
                sql.put("codigoatual", anterior.getCodigoAtual().getId());
            }
            sql.put("id_tiporecebivel", anterior.getIdTipoRecebivel());
            sql.put("emissao", anterior.getDataEmissao());
            sql.put("vencimento", anterior.getVencimento());
            sql.put("valor", anterior.getValor());
            
            stm.execute(sql.getInsert());
        }
    }

    public void atualizaRecebimentoCaixaAnterior(RecebimentoCaixaAnteriorVO anterior) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            
            sql.setSchema("implantacao");
            sql.setTableName("codant_recebercaixa");
            if (anterior.getCodigoAtual() != null) {
                sql.put("codigoatual", anterior.getCodigoAtual().getId());
            }
            sql.put("id_tiporecebivel", anterior.getIdTipoRecebivel());
            sql.put("emissao", anterior.getDataEmissao());
            sql.put("vencimento", anterior.getVencimento());
            sql.put("valor", anterior.getValor());
            sql.setWhere(
                    "sistema = " + SQLUtils.stringSQL(anterior.getSistema()) + " and\n" +
                    "agrupador = " + SQLUtils.stringSQL(anterior.getAgrupador()) + " and\n" +
                    "id = " + SQLUtils.stringSQL(anterior.getId())
            );
            
            stm.execute(sql.getUpdate());
        }
    }
    
}
