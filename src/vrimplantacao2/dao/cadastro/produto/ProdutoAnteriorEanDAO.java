package vrimplantacao2.dao.cadastro.produto;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collection;
import vrframework.classe.Conexao;
import vrimplantacao.utils.Utils;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.utils.sql.SQLUtils;
import vrimplantacao2.vo.cadastro.ProdutoAnteriorEanVO;
import vrimplantacao2.vo.cadastro.ProdutoAnteriorVO;

public class ProdutoAnteriorEanDAO {

    private MultiMap<String, ProdutoAnteriorEanVO> eans;
    
    public void addEans(ProdutoAnteriorVO anterior) throws Exception {
        anterior.getEans().clear();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                "select\n" +
                "	importsistema, \n" +
                "	importloja, \n" +
                "	importid,\n" +
                "	ean,\n" +
                "	qtdembalagem,\n" +
                "	valor,\n" +
                "	tipoembalagem\n" +
                "from \n" +
                "	implantacao.codant_ean\n" +
                "where\n" +
                "       importsistema = " + SQLUtils.stringSQL(anterior.getImportSistema()) + " and\n" +
                "       importloja = " + SQLUtils.stringSQL(anterior.getImportLoja()) + " and\n" +
                "       importid = " + SQLUtils.stringSQL(anterior.getImportId()) + "\n" +
                "order by \n" +
                "	importsistema, \n" +
                "	importloja, \n" +
                "	importid,\n" +
                "	ean"
            )) {
                while (rst.next()) {
                    ProdutoAnteriorEanVO vo = new ProdutoAnteriorEanVO();
                    vo.setImportSistema(rst.getString("importsistema"));
                    vo.setImportLoja(rst.getString("importloja"));
                    vo.setImportId(rst.getString("importid"));
                    vo.setEan(rst.getString("ean"));
                    vo.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    vo.setValor(rst.getDouble("valor"));
                    vo.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    anterior.getEans().put(
                        vo,
                        vo.getImportSistema(),
                        vo.getImportLoja(),
                        vo.getImportId(),
                        vo.getEan()
                    );
                }
            }
        }
    }
    
    public MultiMap<String, ProdutoAnteriorEanVO> getEansAnteriores() throws Exception {
        if (eans == null) {
            atualizarEans();
        }
        return eans;
    }
    
    public void atualizarEans() throws Exception {
        createTable();
        eans = new MultiMap<>(4);
        
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                "select\n" +
                "	importsistema, \n" +
                "	importloja, \n" +
                "	importid,\n" +
                "	ean,\n" +
                "	qtdembalagem,\n" +
                "	valor,\n" +
                "	tipoembalagem\n" +
                "from \n" +
                "	implantacao.codant_ean\n" +
                "order by \n" +
                "	importsistema, \n" +
                "	importloja, \n" +
                "	importid,\n" +
                "	ean"
            )) {
                while (rst.next()) {
                    ProdutoAnteriorEanVO vo = new ProdutoAnteriorEanVO();
                    vo.setImportSistema(rst.getString("importsistema"));
                    vo.setImportLoja(rst.getString("importloja"));
                    vo.setImportId(rst.getString("importid"));
                    vo.setEan(rst.getString("ean"));
                    vo.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    vo.setValor(rst.getDouble("valor"));
                    vo.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    eans.put(vo,
                        vo.getImportSistema(),
                        vo.getImportLoja(),
                        vo.getImportId(),
                        vo.getEan()
                    );
                }
            }
        }
    }

    public void salvar(Collection<ProdutoAnteriorEanVO> values) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            for (ProdutoAnteriorEanVO anterior : values) {
                if (!getEansAnteriores().containsKey(
                        anterior.getImportSistema(),
                        anterior.getImportLoja(),
                        anterior.getImportId(),
                        anterior.getEan())
                ) {
                     SQLBuilder sql = new SQLBuilder();
                     sql.setSchema("implantacao");
                     sql.setTableName("codant_ean");
                     sql.put("importsistema", anterior.getImportSistema());
                     sql.put("importloja", anterior.getImportLoja());
                     sql.put("importid", anterior.getImportId());
                     sql.put("ean", anterior.getEan());
                     sql.put("qtdembalagem", anterior.getQtdEmbalagem());
                     sql.put("valor", anterior.getValor());
                     sql.put("tipoembalagem", anterior.getTipoEmbalagem());
                     
                     stm.execute(sql.getInsert());
                     eans.put(anterior,
                        anterior.getImportSistema(),
                        anterior.getImportLoja(),
                        anterior.getImportId(),
                        anterior.getEan()
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
                "	if not exists(select table_name from information_schema.tables where table_schema = 'implantacao' and table_name = 'codant_ean') then\n" +
                "		create table implantacao.codant_ean (\n" +
                "			importsistema varchar not null,\n" +
                "			importloja varchar not null,\n" +
                "			importid varchar not null,\n" +
                "			ean varchar not null,\n" +
                "			qtdembalagem integer default 1 not null,\n" +
                "			valor numeric(10,3) default 0 not null,\n" +
                "			tipoembalagem varchar(20) default 'UN' not null,\n" +
                "			primary key (importsistema, importloja, importid, ean)\n" +
                "		);\n" +
                "		raise notice 'tabela criada';\n" +
                "	end if;\n" +
                "end;\n" +
                "$$;"
            );
        }
    }

    public void salvar(ProdutoAnteriorEanVO anterior, String obsImportacaoEan) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            sql.setSchema("implantacao");
            sql.setTableName("codant_ean");            
            sql.put("qtdembalagem", anterior.getQtdEmbalagem());
            sql.put("valor", anterior.getValor());
            sql.put("tipoembalagem", 
                    anterior.getTipoEmbalagem() != null ? 
                    Utils.acertarTexto(anterior.getTipoEmbalagem()) : 
                    "");
            
            if (!getEansAnteriores().containsKey(
                    anterior.getImportSistema(),
                    anterior.getImportLoja(),
                    anterior.getImportId(),
                    anterior.getEan())
            ) {
                sql.put("importsistema", anterior.getImportSistema());
                sql.put("importloja", anterior.getImportLoja());
                sql.put("importid", anterior.getImportId());
                sql.put("ean", anterior.getEan() != null ? anterior.getEan() : "");
                sql.put("obsimportacao", obsImportacaoEan);
                
                stm.execute(sql.getInsert());
                eans.put(anterior,
                   anterior.getImportSistema(),
                   anterior.getImportLoja(),
                   anterior.getImportId(),
                   anterior.getEan()
                );
            } else {
                sql.setWhere(
                        "importsistema = " + SQLUtils.stringSQL(anterior.getImportSistema()) + " and " +
                        "importloja = " + SQLUtils.stringSQL(anterior.getImportLoja()) + " and " +
                        "importid = " + SQLUtils.stringSQL(anterior.getImportId()) + " and " +
                        "ean = " + SQLUtils.stringSQL(anterior.getEan())
                );
                stm.execute(sql.getUpdate());
            }
        }
    }
    
    
    
}
