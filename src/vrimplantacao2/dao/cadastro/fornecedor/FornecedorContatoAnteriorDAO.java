package vrimplantacao2.dao.cadastro.fornecedor;

import java.sql.ResultSet;
import java.sql.Statement;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.fornecedor.FornecedorContatoAnteriorVO;
import vrimplantacao2.vo.cadastro.fornecedor.FornecedorContatoVO;

/**
 * Classe responsável por acessar a tabela implantacao.codant_fornecedorcontato.
 * @author Leandro
 * @version 1.0
 */
public class FornecedorContatoAnteriorDAO {

    /**
     * Retorna uma listagem com os códigos anteriores dos contatos armazenados
     * no banco de dados.
     * @return Listagem dos códigos anteriores do contato.
     * @throws Exception 
     */
    public MultiMap<String, FornecedorContatoAnteriorVO> getAnteriores() throws Exception {
        createTable();
        MultiMap<String, FornecedorContatoAnteriorVO> result = new MultiMap<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	ant.importSistema,\n" +
                    "	ant.importLoja,\n" +
                    "	ant.importFornecedorId,\n" +
                    "	ant.importId,\n" +
                    "	ant.codigoatual,\n" +
                    "	fc.nome\n" +
                    "from\n" +
                    "	implantacao.codant_fornecedorcontato ant\n" +
                    "	left join fornecedorcontato fc on ant.codigoatual = fc.id\n" +
                    "order by\n" +
                    "	ant.importSistema,\n" +
                    "	ant.importLoja,\n" +
                    "	ant.importFornecedorId,\n" +
                    "	ant.importId"
            )) {
                while (rst.next()) {
                    FornecedorContatoAnteriorVO ant = new FornecedorContatoAnteriorVO();
                    
                    FornecedorContatoVO cont = null;
                    if (rst.getString("codigoatual") != null) {
                        cont = new FornecedorContatoVO();
                        cont.setId(rst.getInt("codigoatual"));
                        cont.setNome(rst.getString("nome"));       
                    }
                    
                    ant.setCodigoAtual(cont);
                    ant.setImportSistema(rst.getString("importSistema"));
                    ant.setImportLoja(rst.getString("importLoja"));
                    ant.setImportFornecedorId(rst.getString("importFornecedorId"));
                    ant.setImportId(rst.getString("importId"));
                    
                    result.put(ant,
                            ant.getImportSistema(),
                            ant.getImportLoja(),
                            ant.getImportFornecedorId(),
                            ant.getImportId()
                    );
                }
            }
        }
        
        return result;
    }

    /**
     * Cria a tabela no banco de dados.
     * @throws Exception 
     */
    private void createTable() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(
                "create table if not exists implantacao.codant_fornecedorcontato (\n" +
                "	importSistema varchar not null,\n" +
                "	importLoja varchar not null,\n" +
                "	importFornecedorId varchar not null,\n" +
                "	importId varchar not null,\n" +
                "	codigoAtual integer,\n" +
                "	primary key (importSistema, importLoja, importFornecedorId, importId)\n" +
                ");"
            );
        }
    }

    /**
     * Grava o contato anterior no banco de dados.
     * @param vo Contato a ser gravado.
     * @throws Exception 
     */
    public void salvar(FornecedorContatoAnteriorVO vo) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            sql.setSchema("implantacao");
            sql.setTableName("codant_fornecedorcontato");             
            sql.put("importSistema", vo.getImportSistema());
            sql.put("importLoja", vo.getImportLoja());
            sql.put("importFornecedorId", vo.getImportFornecedorId());
            sql.put("importId", vo.getImportId());
            sql.put("codigoAtual", vo.getCodigoAtual() != null ? vo.getCodigoAtual().getId() : null);
            stm.execute(sql.getInsert());            
        }
    }
    
}
