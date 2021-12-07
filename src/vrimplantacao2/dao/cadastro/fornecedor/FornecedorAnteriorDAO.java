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
    
    public FornecedorAnteriorDAO() throws Exception {
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
                    return rst.getInt("codigoatual");
                }
            }
        }
        return null;
    }

    public int getConexaoMigrada(int idConexao, String sistema) throws Exception {
        int conexao = 0;
        
        try(Statement stm = Conexao.createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	id_conexao \n" +
                    "from \n" +
                    "	implantacao.codant_fornecedor\n" +
                    "where \n" +
                    "	importsistema = " + SQLUtils.stringSQL(sistema) + " and\n" +
                    "   id_conexao = " + idConexao + " limit 1")) {
                if (rs.next()) {
                    conexao = rs.getInt("id_conexao");
                }
            }
        }
        
        return conexao;
    }

    public int verificaRegistro() throws Exception {
        try(Statement stm = Conexao.createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	count(*) qtd \n" +
                    "from \n" +
                    "	implantacao.codant_fornecedor \n" +
                    "where \n" +
                    "	codigoatual in (select codigoatual from implantacao.codant_fornecedor limit 100)")) {
                if (rs.next()) {
                    return rs.getInt("qtd");
                }
            }
        }
        
        return 0;
    }

    public boolean verificaMigracaoMultiloja(String lojaOrigem, String sistema, int idConexao) throws Exception {
        boolean conexao = false;
        
        try(Statement stm = Conexao.createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	id_conexao \n" +
                    "from \n" +
                    "	implantacao.codant_fornecedor\n" +
                    "where \n" +
                    "	importsistema = " + SQLUtils.stringSQL(sistema) + " and\n" +
                    "   id_conexao = " + idConexao + " limit 1")) {
                if (rs.next()) {
                    conexao = true;
                }
            }
        }
        
        return conexao;
    }

    public void copiarCodantFornecedor(String sistema, String lojaModelo, String lojaNova) throws Exception {
        
        String sql = 
                "create temp table implantacao_lojas (sistema varchar, loja_modelo varchar, loja_nova varchar) on commit drop;\n" +
                "insert into implantacao_lojas values ('" + sistema + "', '" + lojaModelo + "', '" + lojaNova + "');\n" +
                "\n" +
                "do $$\n" +
                "declare\n" +
                "	r record;\n" +
                "begin\n" +
                "	for r in select * from implantacao_lojas\n" +
                "	loop\n" +
                "		if (exists(select table_name from information_schema.tables t where t.table_name = 'codant_clientepreferencial' and t.table_schema = 'implantacao')) then\n" +
                "			insert into implantacao.codant_clientepreferencial\n" +
                "			select\n" +
                "				r.sistema,\n" +
                "				r.loja_nova,\n" +
                "				id,\n" +
                "				codigoatual,\n" +
                "				cnpj,\n" +
                "				ie,\n" +
                "				nome,\n" +
                "				false\n" +
                "			from\n" +
                "				implantacao.codant_clientepreferencial\n" +
                "			where\n" +
                "				sistema = r.sistema and\n" +
                "				loja = r.loja_modelo and\n" +
                "				id in (\n" +
                "					select\n" +
                "						id\n" +
                "					from\n" +
                "						implantacao.codant_clientepreferencial\n" +
                "					where\n" +
                "						sistema = r.sistema and \n" +
                "						loja = r.loja_modelo\n" +
                "					except\n" +
                "					select\n" +
                "						id\n" +
                "					from\n" +
                "						implantacao.codant_clientepreferencial	\n" +
                "					where\n" +
                "						sistema = r.sistema and \n" +
                "						loja = r.loja_nova\n" +
                "				);\n" +
                "		end if;\n" +
                "\n" +
                "		--Clientes eventuais\n" +
                "		if (exists(select table_name from information_schema.tables t where t.table_name = 'codant_clienteeventual' and t.table_schema = 'implantacao')) then\n" +
                "			insert into implantacao.codant_clienteeventual\n" +
                "			select\n" +
                "				r.sistema,\n" +
                "				r.loja_nova,\n" +
                "				id,\n" +
                "				codigoatual,\n" +
                "				cnpj,\n" +
                "				ie,\n" +
                "				nome,\n" +
                "				false\n" +
                "			from\n" +
                "				implantacao.codant_clienteeventual\n" +
                "			where\n" +
                "				sistema = r.sistema and\n" +
                "				loja = r.loja_modelo and\n" +
                "				id in (\n" +
                "					select\n" +
                "						id\n" +
                "					from\n" +
                "						implantacao.codant_clienteeventual\n" +
                "					where\n" +
                "						sistema = r.sistema and \n" +
                "						loja = r.loja_modelo\n" +
                "					except\n" +
                "					select\n" +
                "						id\n" +
                "					from\n" +
                "						implantacao.codant_clienteeventual	\n" +
                "					where\n" +
                "						sistema = r.sistema and \n" +
                "						loja = r.loja_nova\n" +
                "				);\n" +
                "		end if;\n" +
                "\n" +
                "		--Produtos\n" +
                "\n" +
                "		insert into implantacao.codant_produto\n" +
                "		SELECT \n" +
                "			impsistema, \n" +
                "			r.loja_nova imploja,\n" +
                "			impid, \n" +
                "			descricao, \n" +
                "			codigoatual, \n" +
                "			piscofinscredito, \n" +
                "			piscofinsdebito, \n" +
                "			piscofinsnaturezareceita, \n" +
                "			icmscst, \n" +
                "			icmsaliq, \n" +
                "			icmsreducao, \n" +
                "			estoque, \n" +
                "			e_balanca, \n" +
                "			custosemimposto, \n" +
                "			custocomimposto, \n" +
                "			margem, \n" +
                "			precovenda, \n" +
                "			ncm, \n" +
                "			cest, \n" +
                "			contadorimportacao, \n" +
                "			novo\n" +
                "		FROM \n" +
                "			implantacao.codant_produto\n" +
                "		where\n" +
                "			impsistema = r.sistema and \n" +
                "			imploja = r.loja_modelo and\n" +
                "			impid in (\n" +
                "				select\n" +
                "					impid\n" +
                "				from\n" +
                "					implantacao.codant_produto\n" +
                "				where\n" +
                "					impsistema = r.sistema and \n" +
                "					imploja = r.loja_modelo\n" +
                "				except\n" +
                "				select\n" +
                "					impid\n" +
                "				from\n" +
                "					implantacao.codant_produto	\n" +
                "				where\n" +
                "					impsistema = r.sistema and \n" +
                "					imploja = r.loja_nova\n" +
                "			)\n" +
                "		order by impid;\n" +
                "\n" +
                "\n" +
                "		insert into implantacao.codant_ean \n" +
                "		SELECT \n" +
                "			ant.importsistema,\n" +
                "			r.loja_nova imploja,\n" +
                "			ant.importid, \n" +
                "			ant.ean,\n" +
                "			ant.qtdembalagem,\n" +
                "			ant.valor,\n" +
                "			ant.tipoembalagem\n" +
                "		FROM \n" +
                "			implantacao.codant_ean ant\n" +
                "			join (\n" +
                "			select\n" +
                "				importid,\n" +
                "				ean\n" +
                "			from\n" +
                "				implantacao.codant_ean\n" +
                "			where\n" +
                "				importsistema = r.sistema and \n" +
                "				importloja = r.loja_modelo\n" +
                "			except\n" +
                "			select\n" +
                "				importid,\n" +
                "				ean\n" +
                "			from\n" +
                "				implantacao.codant_ean	\n" +
                "			where\n" +
                "				importsistema = r.sistema and \n" +
                "				importloja = r.loja_nova\n" +
                "			) a on\n" +
                "			ant.importid = a.importid and\n" +
                "			ant.ean = a.ean\n" +
                "		where   \n" +
                "			ant.importsistema = r.sistema and \n" +
                "			ant.importloja = r.loja_modelo\n" +
                "			order by ant.importid;\n" +
                "\n" +
                "		if (exists(select table_name from information_schema.tables t where t.table_name = 'codant_fornecedor' and t.table_schema = 'implantacao')) then\n" +
                "			insert into implantacao.codant_fornecedor\n" +
                "			select\n" +
                "				importsistema,\n" +
                "				r.loja_nova,\n" +
                "				importid,\n" +
                "				codigoatual,\n" +
                "				cnpj,\n" +
                "				razao,\n" +
                "				fantasia\n" +
                "			from \n" +
                "				implantacao.codant_fornecedor\n" +
                "			where\n" +
                "				importsistema = r.sistema and \n" +
                "				importloja = r.loja_modelo and\n" +
                "				importid in (\n" +
                "					select\n" +
                "						importid\n" +
                "					from\n" +
                "						implantacao.codant_fornecedor\n" +
                "					where\n" +
                "						importsistema = r.sistema and \n" +
                "						importloja = r.loja_modelo\n" +
                "					except\n" +
                "					select\n" +
                "						importid\n" +
                "					from\n" +
                "						implantacao.codant_fornecedor	\n" +
                "					where\n" +
                "						importsistema = r.sistema and \n" +
                "						importloja = r.loja_nova\n" +
                "				);\n" +
                "		end if;\n" +
                "\n" +
                "		if (exists(select table_name from information_schema.tables t where t.table_name = 'codant_convenioempresa' and t.table_schema = 'implantacao')) then\n" +
                "			insert into implantacao.codant_convenioempresa\n" +
                "			select\n" +
                "				sistema,\n" +
                "				r.loja_nova loja,\n" +
                "				id,\n" +
                "				codigoatual,\n" +
                "				cnpj,\n" +
                "				razao\n" +
                "			from \n" +
                "				implantacao.codant_convenioempresa a\n" +
                "			where\n" +
                "				a.sistema = r.sistema and\n" +
                "				a.loja = r.loja_modelo and\n" +
                "				a.id in (\n" +
                "					select\n" +
                "						id\n" +
                "					from\n" +
                "						implantacao.codant_convenioempresa\n" +
                "					where\n" +
                "						sistema = r.sistema and \n" +
                "						loja = r.loja_modelo\n" +
                "					except\n" +
                "					select\n" +
                "						id\n" +
                "					from\n" +
                "						implantacao.codant_convenioempresa	\n" +
                "					where\n" +
                "						sistema = r.sistema and \n" +
                "						loja = r.loja_nova\n" +
                "				);\n" +
                "		end if;\n" +
                "\n" +
                "		if (exists(select table_name from information_schema.tables t where t.table_name = 'codant_conveniado' and t.table_schema = 'implantacao')) then\n" +
                "			insert into implantacao.codant_conveniado\n" +
                "			select\n" +
                "				sistema,\n" +
                "				r.loja_nova loja,\n" +
                "				id,\n" +
                "				codigoatual,\n" +
                "				cnpj,\n" +
                "				razao\n" +
                "			from \n" +
                "				implantacao.codant_conveniado a\n" +
                "			where\n" +
                "				a.sistema = r.sistema and\n" +
                "				a.loja = r.loja_modelo and\n" +
                "				a.id in (\n" +
                "					select\n" +
                "						id\n" +
                "					from\n" +
                "						implantacao.codant_conveniado\n" +
                "					where\n" +
                "						sistema = r.sistema and \n" +
                "						loja = r.loja_modelo\n" +
                "					except\n" +
                "					select\n" +
                "						id\n" +
                "					from\n" +
                "						implantacao.codant_conveniado	\n" +
                "					where\n" +
                "						sistema = r.sistema and \n" +
                "						loja = r.loja_nova\n" +
                "				);\n" +
                "		end if;\n" +
                "\n" +
                "		if (exists(select table_name from information_schema.tables t where t.table_name = 'mapatributacao' and t.table_schema = 'implantacao')) then\n" +
                "			insert into implantacao.mapatributacao\n" +
                "			select\n" +
                "				sistema,\n" +
                "				r.loja_nova loja,\n" +
                "				orig_id,\n" +
                "				orig_descricao,\n" +
                "				id_aliquota,\n" +
                "				orig_cst,\n" +
                "				orig_aliquota,\n" +
                "				orig_reduzido\n" +
                "			from \n" +
                "				implantacao.mapatributacao a\n" +
                "			where\n" +
                "				a.sistema = r.sistema and\n" +
                "				a.agrupador = r.loja_modelo and\n" +
                "				a.orig_id in (\n" +
                "					select\n" +
                "						orig_id\n" +
                "					from\n" +
                "						implantacao.mapatributacao\n" +
                "					where\n" +
                "						sistema = r.sistema and \n" +
                "						agrupador = r.loja_modelo\n" +
                "					except\n" +
                "					select\n" +
                "						orig_id\n" +
                "					from\n" +
                "						implantacao.mapatributacao	\n" +
                "					where\n" +
                "						sistema = r.sistema and \n" +
                "						agrupador = r.loja_nova\n" +
                "				);\n" +
                "		end if;\n" +
                "	end loop;     \n" +
                "end;\n" +
                "$$;\n";
        
        try(Statement stm = Conexao.createStatement()) {
            stm.execute(sql);
        }
    }

    public String getLojaModelo(int idConexao, String sistema) throws Exception {
        String loja = "";
        
        try(Statement stm = Conexao.createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	importloja\n" +
                    "from \n" +
                    "	implantacao.codant_fornecedor \n" +
                    "where \n" +
                    "	importsistema = '" + sistema + "' and \n" +
                    "	id_conexao = " + idConexao + "\n" +
                    "limit 1")) {
                if(rs.next()) {
                    loja = rs.getString("importloja");
                }
            }
        }
        
        return loja;
    }

    public boolean verificaMultilojaMigrada(String lojaOrigem, String sistema, int idConexao) throws Exception {
        boolean lojaJaMigrada = false;
        
        try(Statement stm = Conexao.createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	id_conexao \n" +
                    "from \n" +
                    "	implantacao.codant_fornecedor\n" +
                    "where \n" +
                    "	importsistema = " + SQLUtils.stringSQL(sistema) + " and\n" +
                    "   importloja = " + SQLUtils.stringSQL(lojaOrigem) + " and\n" +        
                    "   id_conexao = " + idConexao + " limit 1")) {
                if (rs.next()) {
                    lojaJaMigrada = true;
                }
            }
        }
        
        return lojaJaMigrada;
    }
    
}
