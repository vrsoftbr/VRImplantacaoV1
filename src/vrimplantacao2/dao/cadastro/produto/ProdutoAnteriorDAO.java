package vrimplantacao2.dao.cadastro.produto;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import vrframework.classe.Conexao;
import vrframework.classe.Util;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.utils.sql.SQLUtils;
import vrimplantacao2.vo.cadastro.ProdutoAnteriorVO;
import vrimplantacao2.vo.cadastro.ProdutoVO;

public class ProdutoAnteriorDAO {
    
    private MultiMap<String, ProdutoAnteriorVO> codigoAnterior;
    private final ProdutoAnteriorEanDAO eanAnteriorDAO = new ProdutoAnteriorEanDAO();
    private String importSistema = "";
    private String importLoja = "";
    private boolean carregarTodosOsAnteriores = false;

    public ProdutoAnteriorDAO() {
    }
    
    public ProdutoAnteriorDAO(boolean carregarTodosOsAnteriores) {
        this.carregarTodosOsAnteriores = carregarTodosOsAnteriores;    
    }

    public void setImportSistema(String importSistema) {
        this.importSistema = importSistema;
    }

    public String getImportSistema() {
        return importSistema;
    }

    public String getImportLoja() {
        return importLoja;
    }

    public void setImportLoja(String importLoja) {
        this.importLoja = importLoja;
    }
    
    public void clearAnteriores() {
        codigoAnterior = null;
    }
    
    public MultiMap<String, ProdutoAnteriorVO> getCodigoAnterior() throws Exception {
        if (codigoAnterior == null) {
            atualizarCodigoAnterior();
        }
        return codigoAnterior;
    }
    
    private void createTable() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(
                    "do $$\n" +
                    "declare\n" +
                    "begin\n" +
                    "	if not exists(select table_name from information_schema.tables where table_schema = 'implantacao' and table_name = 'codant_produto') then\n" +
                    "		CREATE TABLE implantacao.codant_produto\n" +
                    "                    (\n" +
                    "                      impsistema character varying NOT NULL,\n" +
                    "                      imploja character varying NOT NULL,\n" +
                    "                      impid character varying NOT NULL,\n" +
                    "                      descricao varchar,\n" +
                    "                      codigoatual integer,\n" +
                    "                      piscofinscredito integer,\n" +
                    "                      piscofinsdebito integer,\n" +
                    "                      piscofinsnaturezareceita integer,\n" +
                    "                      icmscst integer,\n" +
                    "                      icmsaliq numeric(14,4),\n" +
                    "                      icmsreducao numeric(14,4),\n" +
                    "                      estoque numeric(14,4),\n" +
                    "                      e_balanca boolean,\n" +
                    "                      custosemimposto numeric(13,4),\n" +
                    "                      custocomimposto numeric(13,4),\n" +
                    "                      margem numeric(11,2),\n" +
                    "                      precovenda numeric(11,2),\n" +
                    "                      ncm character varying(15),\n" +
                    "                      cest varchar(15),\n" +
                    "                      contadorimportacao integer not null default 0,\n" +
                    "                      novo boolean default false not null,\n" +
                    "                      codigosped varchar,\n" +
                    "                      primary key (impsistema, imploja, impid),\n" +
                    "                      unique (impsistema, imploja, codigosped)\n" +
                    "                );\n" +
                    "		raise notice 'tabela criada';\n" +
                    "	end if;\n" +
                    "end;\n" +
                    "$$;"
            );
        }
    }

    public int getCodigoAnterior2(String sistema, String loja, String id) throws Exception {
        int retorno = -1;
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                "SELECT \n" +
                "	ant.impsistema, \n" +
                "	ant.imploja, \n" +
                "	ant.impid, \n" +
                "	ant.descricao, \n" +
                "	p.descricaocompleta, \n" +
                "	p.descricaoreduzida, \n" +
                "	p.descricaogondola, \n" +
                "	ant.codigoatual, \n" +
                "	ant.piscofinscredito, \n" +
                "	ant.piscofinsdebito, \n" +
                "	ant.piscofinsnaturezareceita, \n" +
                "	ant.icmscst, \n" +
                "	ant.icmsaliq, \n" +
                "	ant.icmsreducao, \n" +
                "	ant.estoque, \n" +
                "	ant.e_balanca, \n" +
                "	ant.custosemimposto, \n" +
                "	ant.custocomimposto, \n" +
                "	ant.margem, \n" +
                "	ant.precovenda, \n" +
                "	ant.ncm, \n" +
                "	ant.cest,\n" +
                "	ant.contadorimportacao,\n" +
                "	ant.novo\n" +
                "FROM \n" +
                "	implantacao.codant_produto ant\n" +
                "	left join produto p on ant.codigoatual = p.id\n"
                    + "where \n"
                    + " ant.impsistema = " + SQLUtils.stringSQL(sistema) + " "
                    + " and ant.imploja = " + SQLUtils.stringSQL(loja) + " "
                    + "and ant.impid = " + SQLUtils.stringSQL(id)
            )) {
                if (rst.next()) {
                    retorno = rst.getInt("codigoatual");
                } else {
                    retorno = -1;
                }
            }
        }
        return retorno;
    }

    public int getCodigoAnterior2ByEAN(String sistema, String loja, String id) throws Exception {
        int retorno = -1;
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                "SELECT \n" +
                "	ant.impsistema, \n" +
                "	ant.imploja, \n" +
                "	ant.impid, \n" +
                "	ant.descricao, \n" +
                "	p.descricaocompleta, \n" +
                "	p.descricaoreduzida, \n" +
                "	p.descricaogondola, \n" +
                "	ant.codigoatual, \n" +
                "	ant.piscofinscredito, \n" +
                "	ant.piscofinsdebito, \n" +
                "	ant.piscofinsnaturezareceita, \n" +
                "	ant.icmscst, \n" +
                "	ant.icmsaliq, \n" +
                "	ant.icmsreducao, \n" +
                "	ant.estoque, \n" +
                "	ant.e_balanca, \n" +
                "	ant.custosemimposto, \n" +
                "	ant.custocomimposto, \n" +
                "	ant.margem, \n" +
                "	ant.precovenda, \n" +
                "	ant.ncm, \n" +
                "	ant.cest,\n" +
                "	ant.contadorimportacao,\n" +
                "	ant.novo\n" +
                "FROM \n" +
                "	implantacao.codant_produto ant\n" +
                "	left join produto p on ant.codigoatual = p.id\n"
                    + "where \n"
                    + " ant.impsistema = " + SQLUtils.stringSQL(sistema) + " "
                    + " and ant.imploja = " + SQLUtils.stringSQL(loja) + " "
                    + "and ant.impid = lpad(" + SQLUtils.stringSQL(id)+", 14, '0')"
            )) {
                if (rst.next()) {
                    retorno = rst.getInt("codigoatual");
                } else {
                    retorno = -1;
                }
            }
        }
        return retorno;
    }
    
    public String getCodigoAnterior3(String sistema, String loja, String id) throws Exception {
        String retorno = "-1";
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT "
                    + "impid "
                    + "FROM \n"
                    + "	implantacao.codant_produto ant\n"
                    + "where \n"
                    + " ant.impsistema = " + SQLUtils.stringSQL(sistema) + " "
                    + " and ant.imploja = " + SQLUtils.stringSQL(loja) + " "
                    + "and ant.impid = " + SQLUtils.stringSQL(id)
            )) {
                if (rst.next()) {
                    retorno = rst.getString("impid");
                } else {
                    retorno = "-1";
                }
            }
        }
        return retorno;
    }
    
    public String getCodigoAnteriorEAN(String sistema, String loja, String ean) throws Exception {
        String retorno = "-1";
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT "
                    + "importid "
                    + "FROM \n"
                    + "	implantacao.codant_ean ant\n"
                    + "where \n"
                    + " ant.importsistema = " + SQLUtils.stringSQL(sistema) + " "
                    + " and ant.importloja = " + SQLUtils.stringSQL(loja) + " "
                    + "and ant.ean = " + SQLUtils.stringSQL(ean)
            )) {
                if (rst.next()) {
                    retorno = rst.getString("importid");
                } else {
                    retorno = "-1";
                }
            }
        }
        return retorno;
    }

    public String getCodigoAnteriorCpGestor(String ean) throws Exception {
        String retorno = "-1";
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select pr_codint "
                    + "from implantacao.produtos_tokleve "
                    + "where pr_cbarra = lpad('" + ean + "', 14, '0')"
            )) {
                if (rst.next()) {
                    retorno = rst.getString("pr_codint");
                } else {
                    retorno = "-1";
                }
            }
        }
        return retorno;
    }
    
    public void atualizarCodigoAnterior() throws Exception {
        codigoAnterior = new MultiMap<>(3);
        createTable();
        try (Statement stm = Conexao.createStatement()){
            try (ResultSet rst = stm.executeQuery(
                "SELECT \n" +
                "	ant.impsistema, \n" +
                "	ant.imploja, \n" +
                "	ant.impid, \n" +
                "	ant.descricao, \n" +
                "	p.descricaocompleta, \n" +
                "	p.descricaoreduzida, \n" +
                "	p.descricaogondola, \n" +
                "	ant.codigoatual, \n" +
                "	ant.piscofinscredito, \n" +
                "	ant.piscofinsdebito, \n" +
                "	ant.piscofinsnaturezareceita, \n" +
                "	ant.icmscst, \n" +
                "	ant.icmsaliq, \n" +
                "	ant.icmsreducao, \n" +
                "	ant.estoque, \n" +
                "	ant.e_balanca, \n" +
                "	ant.custosemimposto, \n" +
                "	ant.custocomimposto, \n" +
                "	ant.margem, \n" +
                "	ant.precovenda, \n" +
                "	ant.ncm, \n" +
                "	ant.cest,\n" +
                "	ant.contadorimportacao,\n" +
                "	ant.novo\n" +
                "FROM \n" +
                "	implantacao.codant_produto ant\n" +
                "	left join produto p on ant.codigoatual = p.id\n" +
                (!carregarTodosOsAnteriores ? 
                    "where \n" +
                    "       ant.impsistema = " + SQLUtils.stringSQL(getImportSistema()) + " and\n" +
                    "       ant.imploja = " + SQLUtils.stringSQL(getImportLoja()) + "\n" :
                    ""
                ) +
                "order by\n" +
                "	ant.impsistema, \n" +
                "	ant.imploja, \n" +
                "	ant.impid"
            )) {
                int cont = 1;
                while (rst.next()) {
                    ProdutoAnteriorVO vo = new ProdutoAnteriorVO();
                    
                    vo.setImportSistema(rst.getString("impsistema"));
                    vo.setImportLoja(rst.getString("imploja"));
                    vo.setImportId(rst.getString("impid"));
                    vo.setDescricao(rst.getString("descricao"));
                    ProdutoVO produto = null;
                    if (rst.getString("codigoatual") != null) {
                        produto = new ProdutoVO();
                        produto.setId(rst.getInt("codigoatual"));
                        produto.setDescricaoCompleta("descricaocompleta");
                        produto.setDescricaoCompleta("descricaogondola");
                        produto.setDescricaoCompleta("descricaoreduzida");
                    }
                    vo.setCodigoAtual(produto);
                    vo.setPisCofinsCredito(rst.getInt("piscofinscredito"));
                    vo.setPisCofinsDebito(rst.getInt("piscofinsdebito"));
                    vo.setPisCofinsNaturezaReceita(rst.getInt("piscofinsnaturezareceita"));
                    vo.setIcmsCst(rst.getInt("icmscst"));
                    vo.setIcmsAliq(rst.getDouble("icmsaliq"));
                    vo.setIcmsReducao(rst.getDouble("icmsreducao"));
                    vo.setEstoque(rst.getDouble("estoque"));
                    vo.seteBalanca(rst.getBoolean("e_balanca"));
                    vo.setCustosemimposto(rst.getDouble("custosemimposto"));
                    vo.setCustocomimposto(rst.getDouble("custocomimposto"));
                    vo.setPrecovenda(rst.getDouble("precovenda"));
                    vo.setNcm(rst.getString("ncm"));
                    vo.setCest(rst.getString("cest"));
                    vo.setNovo(rst.getBoolean("novo"));
                    vo.setContadorImportacao(rst.getInt("contadorimportacao"));                    
                    eanAnteriorDAO.addEans(vo);                    
                    codigoAnterior.put(vo, vo.getChave());
                    cont++;
                }
            }
        }
    }
    
    private int contador = -1;
    private void obtemContador() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select coalesce(max(contadorimportacao) + 1,1) contador from implantacao.codant_produto"
            )) {
                rst.next();
                contador = rst.getInt("contador");
            }
        }
    }

    public void salvar(Collection<ProdutoAnteriorVO> values) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            for (ProdutoAnteriorVO vo: values) {
                
                if (!getCodigoAnterior().containsKey(
                    vo.getImportSistema(),
                    vo.getImportLoja(),
                    vo.getImportId()
                )) {
                    SQLBuilder sql = new SQLBuilder();
                    sql.setTableName("codant_produto");
                    sql.setSchema("implantacao");
                    sql.put("impsistema", vo.getImportSistema());
                    sql.put("imploja", vo.getImportLoja());
                    sql.put("impid", vo.getImportId());
                    sql.put("descricao", vo.getDescricao());
                    sql.put("codigoatual", (vo.getCodigoAtual() != null ? vo.getCodigoAtual().getId() : null));
                    sql.put("piscofinscredito", vo.getPisCofinsCredito());
                    sql.put("piscofinsdebito", vo.getPisCofinsDebito());
                    sql.put("piscofinsnaturezareceita", vo.getPisCofinsNaturezaReceita(), -1);
                    sql.put("icmscst", vo.getIcmsCst());
                    sql.put("icmsaliq", vo.getIcmsAliq());
                    sql.put("icmsreducao", vo.getIcmsReducao());
                    sql.put("estoque", vo.getEstoque());
                    sql.put("e_balanca", vo.iseBalanca());
                    sql.put("custosemimposto", vo.getCustosemimposto());
                    sql.put("custocomimposto", vo.getCustocomimposto());
                    sql.put("margem", vo.getMargem());
                    sql.put("precovenda", vo.getPrecovenda());
                    sql.put("ncm", vo.getNcm());
                    sql.put("cest", vo.getCest());
                    sql.put("novo", vo.isNovo());
                    sql.put("codigosped", vo.getCodigoSped());
                    if (contador == -1) {
                        obtemContador();
                    }
                    sql.put("contadorimportacao", contador);

                    try {
                        stm.execute(sql.getInsert());
                    } catch (Exception e) {
                        Util.exibirMensagem(sql.getInsert(), "");
                    }
                    eanAnteriorDAO.salvar(vo.getEans().values());
                    

                    codigoAnterior.put(
                            vo, 
                            vo.getImportSistema(),
                            vo.getImportLoja(),
                            vo.getImportId()
                    );
                } else {
                    eanAnteriorDAO.salvar(vo.getEans().values());
                }
            }
        }
    }

    public void salvar(ProdutoAnteriorVO anterior) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            sql.setTableName("codant_produto");
            sql.setSchema("implantacao");
            sql.put("impsistema", anterior.getImportSistema());
            sql.put("imploja", anterior.getImportLoja());
            sql.put("impid", anterior.getImportId());
            sql.put("descricao", anterior.getDescricao());
            sql.put("codigoatual", (anterior.getCodigoAtual() != null ? anterior.getCodigoAtual().getId() : null));
            sql.put("piscofinscredito", anterior.getPisCofinsCredito());
            sql.put("piscofinsdebito", anterior.getPisCofinsDebito());
            sql.put("piscofinsnaturezareceita", anterior.getPisCofinsNaturezaReceita(), -1);
            sql.put("icmscst", anterior.getIcmsCst());
            sql.put("icmsaliq", anterior.getIcmsAliq());
            sql.put("icmsreducao", anterior.getIcmsReducao());
            sql.put("estoque", anterior.getEstoque());
            sql.put("e_balanca", anterior.iseBalanca());
            sql.put("custosemimposto", anterior.getCustosemimposto());
            sql.put("custocomimposto", anterior.getCustocomimposto());
            sql.put("margem", anterior.getMargem());
            sql.put("precovenda", anterior.getPrecovenda());
            sql.put("ncm", anterior.getNcm());
            sql.put("cest", anterior.getCest());
            sql.put("novo", anterior.isNovo());
            sql.put("codigosped", anterior.getCodigoSped());
            if (contador == -1) {
                obtemContador();
            }
            sql.put("contadorimportacao", contador);

            try {
                stm.execute(sql.getInsert());
                getCodigoAnterior().put(
                        anterior,
                        anterior.getImportSistema(),
                        anterior.getImportLoja(),
                        anterior.getImportId());
            } catch (Exception e) {
                sql.setFormatarSQL(true);
                Util.exibirMensagem(sql.getInsert(), "");
                throw e;
            }                
        }
    }

    public Map<String, ProdutoAnteriorVO> getCodigoAnterior(String sistema, String loja) throws Exception {
        Map<String, ProdutoAnteriorVO> result = new LinkedHashMap<>();
        createTable();
        try (Statement stm = Conexao.createStatement()){
            try (ResultSet rst = stm.executeQuery(
                "SELECT \n" +
                "	ant.impsistema, \n" +
                "	ant.imploja, \n" +
                "	ant.impid, \n" +
                "	ant.descricao, \n" +
                "	p.descricaocompleta, \n" +
                "	p.descricaoreduzida, \n" +
                "	p.descricaogondola, \n" +
                "	ant.codigoatual, \n" +
                "	ant.piscofinscredito, \n" +
                "	ant.piscofinsdebito, \n" +
                "	ant.piscofinsnaturezareceita, \n" +
                "	ant.icmscst, \n" +
                "	ant.icmsaliq, \n" +
                "	ant.icmsreducao, \n" +
                "	ant.estoque, \n" +
                "	ant.e_balanca, \n" +
                "	ant.custosemimposto, \n" +
                "	ant.custocomimposto, \n" +
                "	ant.margem, \n" +
                "	ant.precovenda, \n" +
                "	ant.ncm, \n" +
                "	ant.cest,\n" +
                "	ant.contadorimportacao,\n" +
                "	ant.novo\n" +
                "FROM \n" +
                "	implantacao.codant_produto ant\n" +
                "	left join produto p on ant.codigoatual = p.id\n" +
                "where \n" +
                "       ant.impsistema = " + SQLUtils.stringSQL(getImportSistema()) + " and\n" +
                "       ant.imploja = " + SQLUtils.stringSQL(getImportLoja()) + "\n" +
                "order by\n" +
                "	ant.impsistema, \n" +
                "	ant.imploja, \n" +
                "	ant.impid"
            )) {
                while (rst.next()) {
                    ProdutoAnteriorVO vo = new ProdutoAnteriorVO();
                    
                    vo.setImportSistema(rst.getString("impsistema"));
                    vo.setImportLoja(rst.getString("imploja"));
                    vo.setImportId(rst.getString("impid"));
                    vo.setDescricao(rst.getString("descricao"));
                    ProdutoVO produto = null;
                    if (rst.getString("codigoatual") != null) {
                        produto = new ProdutoVO();
                        produto.setId(rst.getInt("codigoatual"));
                        produto.setDescricaoCompleta("descricaocompleta");
                        produto.setDescricaoCompleta("descricaogondola");
                        produto.setDescricaoCompleta("descricaoreduzida");
                    }
                    vo.setCodigoAtual(produto);
                    vo.setPisCofinsCredito(rst.getInt("piscofinscredito"));
                    vo.setPisCofinsDebito(rst.getInt("piscofinsdebito"));
                    vo.setPisCofinsNaturezaReceita(rst.getInt("piscofinsnaturezareceita"));
                    vo.setIcmsCst(rst.getInt("icmscst"));
                    vo.setIcmsAliq(rst.getDouble("icmsaliq"));
                    vo.setIcmsReducao(rst.getDouble("icmsreducao"));
                    vo.setEstoque(rst.getDouble("estoque"));
                    vo.seteBalanca(rst.getBoolean("e_balanca"));
                    vo.setCustosemimposto(rst.getDouble("custosemimposto"));
                    vo.setCustocomimposto(rst.getDouble("custocomimposto"));
                    vo.setPrecovenda(rst.getDouble("precovenda"));
                    vo.setNcm(rst.getString("ncm"));
                    vo.setCest(rst.getString("cest"));
                    vo.setNovo(rst.getBoolean("novo"));
                    vo.setContadorImportacao(rst.getInt("contadorimportacao"));
                    
                    result.put(vo.getImportId(), vo);
                }
            }
        }
        
        return result;
    }
  
}
