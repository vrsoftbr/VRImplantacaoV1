package vrimplantacao2.dao.cadastro.fornecedor;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.utils.sql.SQLUtils;
import vrimplantacao2.vo.cadastro.fornecedor.FornecedorAnteriorVO;
import vrimplantacao2.vo.cadastro.fornecedor.FornecedorVO;
import vrimplantacao2.vo.cadastro.local.EstadoVO;

public class FornecedorAnteriorDAO {

    private MultiMap<String, FornecedorAnteriorVO> anteriores;
    
    public MultiMap<String, FornecedorAnteriorVO> getAnteriores() throws Exception {
        if (anteriores == null) {
            atualizarAnteriores();
        }
        return anteriores;
    }
    
    public void atualizarAnteriores() throws Exception {
        createTable();
        anteriores = new MultiMap<>(3);
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                "select\n" +
                "	ca.importsistema,\n" +
                "	ca.importloja,\n" +
                "	ca.importid,\n" +
                "	ca.codigoatual,\n" +
                "	f.razaosocial,\n" +
                "	f.nomefantasia,\n" +
                "	f.cnpj cnpj_cpf,\n" +
                "	ca.cnpj,\n" +
                "	ca.razao,\n" +
                "	ca.fantasia,\n" +
                "	f.id_estado,\n" +
                "	e.sigla uf_sigla,\n" +
                "	e.descricao uf_descricao\n" +
                "from \n" +
                "	implantacao.codant_fornecedor ca\n" +
                "	left join fornecedor f on ca.codigoatual = f.id\n" +
                "       left join estado e on f.id_estado = e.id\n" +
                "order by\n" +
                "	ca.importsistema,\n" +
                "	ca.importloja,\n" +
                "	ca.importid"
            )) {
                while (rst.next()) {
                    FornecedorAnteriorVO vo = new FornecedorAnteriorVO();
                    vo.setImportSistema(rst.getString("importsistema"));
                    vo.setImportLoja(rst.getString("importloja"));
                    vo.setImportId(rst.getString("importid"));
                    vo.setCnpjCpf(rst.getString("cnpj"));
                    vo.setRazao(rst.getString("razao"));
                    vo.setFantasia(rst.getString("fantasia"));
                    String codigoAtual = rst.getString("codigoatual");
                    if (codigoAtual != null && !"".equals(codigoAtual.trim())) {
                        FornecedorVO f = new FornecedorVO();
                        f.setId(rst.getInt("codigoatual"));
                        f.setRazaoSocial(rst.getString("razaosocial"));
                        f.setNomeFantasia(rst.getString("nomefantasia"));
                        EstadoVO uf = new EstadoVO(
                                rst.getInt("id_estado"), 
                                rst.getString("uf_sigla"), 
                                rst.getString("uf_descricao")
                        );
                        f.setEstado(uf);
                        vo.setCodigoAtual(f);                        
                    }
                    
                    anteriores.put(
                            vo, 
                            vo.getImportSistema(),
                            vo.getImportLoja(),
                            vo.getImportId()
                    );
                }
            }
        }
    }
    
    private void createTable() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(
                "do $$\n" +
                "declare\n" +
                "begin\n" +
                "	if not exists(select table_name from information_schema.tables where table_schema = 'implantacao' and table_name = 'codant_fornecedor') then\n" +
                "		create table implantacao.codant_fornecedor(\n" +
                "			importsistema varchar not null,\n" +
                "			importloja varchar not null,\n" +
                "			importid varchar not null,\n" +
                "			codigoatual integer,\n" +
                "			cnpj varchar(30),\n" +
                "			razao varchar,\n" +
                "			fantasia varchar,\n" +
                "			primary key (importsistema, importloja, importid)\n" +
                "		);\n" +
                "	end if;\n" +
                "end;\n" +
                "$$;"
            );
        }
    }

    public void salvar(FornecedorAnteriorVO vo) throws Exception {
        if (!getAnteriores().containsKey(
                vo.getImportSistema(),
                vo.getImportLoja(),
                vo.getImportId()
        )) {
            try (Statement stm = Conexao.createStatement()) {            
                SQLBuilder sql = new SQLBuilder();
                sql.setSchema("implantacao");
                sql.setTableName("codant_fornecedor");
                sql.put("importsistema", vo.getImportSistema());
                sql.put("importloja", vo.getImportLoja());
                sql.put("importid", vo.getImportId());
                if (vo.getCodigoAtual() != null) {
                    sql.put("codigoatual", vo.getCodigoAtual().getId());
                } else {
                    sql.putNull("codigoatual");
                }
                sql.put("cnpj", vo.getCnpjCpf());
                sql.put("razao", vo.getRazao());
                sql.put("fantasia", vo.getFantasia());
                
                stm.execute(sql.getInsert());
            }
        }
    }

    public void gravarFornecedorAnterior(FornecedorAnteriorVO vo) throws Exception {
        try (Statement stm = Conexao.createStatement()) {            
                SQLBuilder sql = new SQLBuilder();
                sql.setSchema("implantacao");
                sql.setTableName("codant_fornecedor");
                sql.put("importsistema", vo.getImportSistema());
                sql.put("importloja", vo.getImportLoja());
                sql.put("importid", vo.getImportId());
                if (vo.getCodigoAtual() != null) {
                    sql.put("codigoatual", vo.getCodigoAtual().getId());
                } else {
                    sql.putNull("codigoatual");
                }
                sql.put("cnpj", vo.getCnpjCpf());
                sql.put("razao", vo.getRazao());
                sql.put("fantasia", vo.getFantasia());
                
                stm.execute(sql.getInsert());
            }
    }

    /**
     * Retorna {@link Map} com IDs de fornecedores importados.
     * @param sistema Nome do sistema armazenado na tabela implantacao.codant_fornecedor.
     * @param loja Loja onde foram mapeados os resgistros da implantaca.codant_fornecedor.
     * @return {@link Map} com os IDs importados.
     * @throws Exception 
     */
    public Map<String, Integer> getFornecedoresImportados(String sistema, String loja) throws Exception {
        Map<String, Integer> result = new LinkedHashMap<>();
        
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	ant.importid,\n" +
                    "	ant.codigoatual\n" +
                    "from\n" +
                    "	implantacao.codant_fornecedor ant\n" +
                    "	join fornecedor f on ant.codigoatual = f.id\n" +
                    "where\n" +
                    "	importsistema = " + SQLUtils.stringSQL(sistema) + "\n" +
                    "	and importloja = " + SQLUtils.stringSQL(loja) + "\n" +
                    "order by\n" +
                    "	importid"
            )) {
                while (rst.next()) {
                    result.put(rst.getString("importid"), rst.getInt("codigoatual"));
                }
            }
        }
        
        return result;
    }

    public Integer getByIdAnterior(String sistema, String loja, String id) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select codigoatual from implantacao.codant_fornecedor where \n" +
                    "	importsistema = '" + sistema + "' and\n" +
                    "	importloja = '" + loja + "' and\n" +
                    "	importid = '" + id + "'"
            )) {
                if (rst.next()) {
                    return rst.getObject("codigoatual", Integer.class);
                }
            }
        }
        return null;
    }
    
}
