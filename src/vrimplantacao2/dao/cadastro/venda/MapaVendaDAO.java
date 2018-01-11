package vrimplantacao2.dao.cadastro.venda;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.utils.sql.SQLUtils;

/**
 *
 * @author Leandro
 */
public class MapaVendaDAO {
    
    private static final Logger LOG = Logger.getLogger(MapaVendaDAO.class.getName());
    
    private final String sistema;
    private final String loja;

    public MapaVendaDAO(String sistema, String loja) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(
                    "create table if not exists implantacao.mapavenda(\n" +
                    "	sistema varchar not null,\n" +
                    "	loja varchar not null,\n" +
                    "	codigo varchar not null,\n" +
                    "	ean varchar not null,\n" +
                    "	descricao varchar not null,\n" +
                    "	codigoatual integer,\n" +
                    "	novo boolean,\n" +
                    "	primary key(sistema, loja, codigo)\n" +
                    ")"
            );
        }
        this.sistema = sistema;
        this.loja = loja;
    }
    
    private Map<String, Integer> produtosPorMapeamento = null;
    public Integer getProdutoPorMapeamento(String codigoItem) throws Exception {
        if (produtosPorMapeamento == null) {
            LOG.info("Carregando mapeamento de produto na venda");
            produtosPorMapeamento = new HashMap<>();
            try (Statement stm = Conexao.createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select\n" +
                        "	mp.codigo,\n" +
                        "	mp.codigoatual\n" +
                        "from\n" +
                        "	implantacao.mapavenda mp\n" +
                        "	join produto p on mp.codigoatual = p.id\n" +
                        "where\n" +
                        "	mp.sistema = " + SQLUtils.stringSQL(sistema) + " and\n" +
                        "	mp.loja = " + SQLUtils.stringSQL(loja)
                )) {
                    while (rst.next()) {                        
                        produtosPorMapeamento.put(rst.getString("codigo"), rst.getInt("codigoatual"));
                    }
                }
            }
            LOG.info("Mapeamento de produto na venda concluído");
        }
        return produtosPorMapeamento.get(codigoItem);
    }

    public void gravar(String produto, String codigoBarras, String descricaoReduzida) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            
            try (ResultSet rst = stm.executeQuery(
                    "select ean from implantacao.mapavenda where\n"
                            + "sistema = " + SQLUtils.stringSQL(sistema) + "\n"
                            + "and loja = " + SQLUtils.stringSQL(loja) + "\n"
                            + "and codigo = " + SQLUtils.stringSQL(produto)
            )) {
            
                if (!rst.next()) {
                    
                    SQLBuilder sql = new SQLBuilder();

                    sql.setSchema("implantacao");
                    sql.setTableName("mapavenda");
                    sql.put("sistema", sistema);
                    sql.put("loja", loja);
                    sql.put("ean", codigoBarras);
                    sql.put("codigo", produto);
                    sql.put("descricao", descricaoReduzida);

                    stm.execute(sql.getInsert());
            
                }
            }
        }
    }
    
    
    
}
