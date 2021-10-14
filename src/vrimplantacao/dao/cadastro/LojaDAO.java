package vrimplantacao.dao.cadastro;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vr.core.parametro.versao.Versao;
import vrframework.classe.Conexao;
import vrframework.classe.Util;
import vrimplantacao.dao.CodigoInternoDAO;
import vrimplantacao.dao.DataProcessamentoDAO;
import vrimplantacao.vo.loja.LojaFiltroConsultaVO;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao.vo.loja.SituacaoCadastro;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.utils.sql.SQLBuilder;

public class LojaDAO {

    private Versao versao = Versao.createFromConnectionInterface(Conexao.getConexao());
    
    public List<LojaVO> consultar(LojaFiltroConsultaVO i_filtro) throws Exception {
        List<LojaVO> result = new ArrayList();

        String sql = "SELECT "
                + "     lj.*, sc.descricao AS situacaocadastro,\n"
                + "     r.descricao AS regiao\n"
                + "FROM loja AS lj\n"
                + "INNER JOIN situacaocadastro AS sc ON sc.id = lj.id_situacaocadastro\n"
                + "INNER JOIN regiao AS r ON r.id = lj.id_regiao\n"
                + "WHERE 1 = 1";

        if (i_filtro.getId() > -1) {
            sql = sql + " AND lj.id = " + i_filtro.getId();
        }
        
        if (!i_filtro.getDescricao().trim().equals("")) {
            sql = sql + " AND " + Util.getGoogle("lj.descricao", i_filtro.getDescricao());
        }
        
        if (i_filtro.getOrdenacao().isEmpty()) {
            sql = sql + " ORDER BY lj.descricao";
        } else {
            sql = sql + " ORDER BY " + i_filtro.getOrdenacao() + ", lj.descricao";
        }
        
        if (i_filtro.getLimite() > 0) {
            sql = sql + " LIMIT " + i_filtro.getLimite();
        }

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    sql
            )) {
                while (rst.next()) {
                    LojaVO oLoja = new LojaVO();
                    oLoja.setId(rst.getInt("id"));
                    oLoja.setDescricao(rst.getString("descricao"));
                    oLoja.setIdFornecedor(rst.getInt("id_fornecedor"));
                    oLoja.setNomeServidor(rst.getString("nomeservidor"));
                    oLoja.setServidorCentral(rst.getBoolean("servidorcentral"));
                    oLoja.setIdSituacaoCadastro(rst.getInt("id_situacaocadastro"));
                    oLoja.setSituacaoCadastro(rst.getString("situacaocadastro"));
                    oLoja.setIdRegiao(rst.getInt("id_regiao"));
                    oLoja.setGeraConcentrador(rst.getBoolean("geraconcentrador"));
                    oLoja.setRegiao(rst.getString("regiao"));

                    result.add(oLoja);
                }
            }
        }
        return result;
    }

    public LojaVO carregar(int i_id) throws Exception {
        LojaVO oLoja = new LojaVO();

        String sql = "SELECT * FROM loja WHERE id = " + i_id;

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    sql
            )) {
                if (rst.next()) {
                    oLoja.setId(rst.getInt("id"));
                    oLoja.setDescricao(rst.getString("descricao"));
                    oLoja.setIdFornecedor(rst.getInt("id_fornecedor"));
                    oLoja.setNomeServidor(rst.getString("nomeservidor"));
                    oLoja.setServidorCentral(rst.getBoolean("servidorcentral"));
                    oLoja.setIdRegiao(rst.getInt("id_regiao"));
                    oLoja.setGeraConcentrador(rst.getBoolean("geraconcentrador"));
                }
            }
        }
        return oLoja;
    }

    public List<LojaVO> carregar() throws Exception {
        List<LojaVO> result = new ArrayList<>();

        String sql = "SELECT * FROM loja ORDER BY loja ASC";

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    sql
            )) {
                while (rst.next()) {
                    LojaVO oLoja = new LojaVO();
                    oLoja.setId(rst.getInt("id"));
                    oLoja.setDescricao(rst.getString("descricao"));

                    result.add(oLoja);
                }
            }
            return result;
        }
    }

    public LojaVO carregar2(int i_id) throws Exception {
        LojaVO oLoja = new LojaVO();
        
        String sql = "SELECT * FROM loja WHERE id = " + i_id;
        
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    sql
            )) {
                if (rst.next()) {
                    oLoja.setId(rst.getInt("id"));
                    oLoja.setDescricao(rst.getString("descricao"));
                    oLoja.setIdFornecedor(rst.getInt("id_fornecedor"));
                    oLoja.setNomeServidor(rst.getString("nomeservidor"));
                    oLoja.setServidorCentral(rst.getBoolean("servidorcentral"));
                    oLoja.setIdRegiao(rst.getInt("id_regiao"));
                    oLoja.setGeraConcentrador(rst.getBoolean("geraconcentrador"));
                }
            }
        }
        
        return oLoja;
    }

    private boolean isLojaExiste(LojaVO i_loja) throws Exception {
        String sql = "SELECT id FROM loja WHERE id = " + i_loja.getId();
        
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    sql
            )) {
                return rst.next();
            }
        }
    }
    
    private SQLBuilder criarLoja(LojaVO i_loja) throws Exception {
        SQLBuilder sql = new SQLBuilder();
        sql.setSchema("public");
        sql.setTableName("loja");

        sql.put("id", i_loja.getId());
        sql.put("descricao", i_loja.getDescricao());
        sql.put("id_fornecedor", i_loja.getIdFornecedor());
        sql.put("id_situacaocadastro", SituacaoCadastro.ATIVO.getId());
        sql.put("nomeservidor", i_loja.getNomeServidor());
        sql.put("servidorcentral", i_loja.isServidorCentral());
        sql.put("id_regiao", i_loja.getIdRegiao());
        sql.put("geraconcentrador", i_loja.isGeraConcentrador());

        return sql;
    }
    
    public void salvarNovo(LojaVO i_loja) throws Exception {

        if (!isLojaExiste(i_loja)) {

            try (Statement stm = Conexao.createStatement()) {

                /* criar loja */
                stm.execute(criarLoja(i_loja).getInsert());
                
                /* cópia da tabela produtocomplemento */
                stm.execute(copiarProdutoComplemento(i_loja));

                /* cópia da tabela fornecedorprazo */
                stm.execute(copiarFornecedorPrazo(i_loja));

                /* cópia da tabela fornecedorprazopedido */
                stm.execute(copiarFornecedorPrazoPedido(i_loja));

                /*cópia da tabela parametrovalor */
                stm.execute(copiarParametroValor(i_loja));

                /*cópia da tabela pdv.funcaoniveloperador */
                stm.execute(copiarPdvFuncaoNivelOperador(i_loja));

                /* cópia da tabela pdv.parametrovalor */
                stm.execute(copiarPdvParametroValor(i_loja));

                /* update campo valor na tabela pdv.parametrovalor */
                updateValorPdvParametroValor(i_loja);
                
                /* cópia da tabela pdv.cartaolayout */
                if (copiarPdvCartaoLayout(i_loja) != null && !copiarPdvCartaoLayout(i_loja).isEmpty()) {
                    stm.execute(copiarPdvCartaoLayout(i_loja).getInsert());
                }

                /* cópia da tabela pdv.balancaetiquetalayout */
                if (copiarPdvBalancaEtiquetaLayout(i_loja) != null && !copiarPdvBalancaEtiquetaLayout(i_loja).isEmpty()) {
                    stm.execute(copiarPdvBalancaEtiquetaLayout(i_loja).getInsert());
                }
            }
        } else {
            SQLBuilder sql = new SQLBuilder();
            sql.setSchema("public");
            sql.setTableName("loja");

            sql.put("descricao", i_loja.getDescricao());
            sql.put("id_fornecedor", i_loja.getIdFornecedor());
            sql.put("id_situacaocadastro", SituacaoCadastro.ATIVO.getId());
            sql.put("nomeservidor", i_loja.getNomeServidor());
            sql.put("servidorcentral", i_loja.isServidorCentral());
            sql.put("id_regiao", i_loja.getIdRegiao());
            sql.put("geraconcentrador", i_loja.isGeraConcentrador());

            sql.setWhere("id = " + i_loja.getId());

            if (!sql.isEmpty()) {
                try (Statement stmUpdate = Conexao.createStatement()) {
                    stmUpdate.execute(sql.getUpdate());
                }
            }
        }
    }
    
    private String copiarProdutoComplemento(LojaVO i_loja) throws Exception {
        String sql = "INSERT INTO produtocomplemento ("
                + "id_produto, prateleira, secao, estoqueminimo, estoquemaximo, valoripi, dataultimopreco, \n"
                + "dataultimaentrada, custosemimposto, custocomimposto, custosemimpostoanterior, custocomimpostoanterior, precovenda, precovendaanterior, \n"
                + "precodiaseguinte, estoque, troca, emiteetiqueta, custosemperdasemimposto, custosemperdasemimpostoanterior, customediocomimposto, \n"
                + "customediosemimposto, id_aliquotacredito, dataultimavenda, teclaassociada, id_situacaocadastro, id_loja, descontinuado, \n"
                + "quantidadeultimaentrada, centralizado, operacional, valoricmssubstituicao, dataultimaentradaanterior, cestabasica, valoroutrassubstituicao, id_tipocalculoddv \n";

        if (versao.igualOuMaiorQue(3, 17, 10)) {
            sql = sql + ", id_tipoproduto, fabricacaopropria ";
        }
        if (versao.igualOuMaiorQue(3, 21)) {
            sql = sql + ", dataprimeiraentrada ";
        }
        if (versao.igualOuMaiorQue(4)) {
            sql = sql + ", margem, margemminima, margemmaxima ";
        }

        sql = sql + ")";

        sql = sql + " (SELECT id_produto, prateleira, secao, estoqueminimo, estoquemaximo, valoripi, null, null, " + (i_loja.isCopiaCusto() ? "custosemimposto" : "0") + ","
                + " " + (i_loja.isCopiaCusto() ? "custocomimposto" : "0") + ", 0, 0, " + (i_loja.isCopiaPrecoVenda() ? "precovenda" : "0") + ","
                + "  0, precodiaseguinte, 0, 0, emiteetiqueta, 0, 0, 0, 0, id_aliquotacredito,"
                + " null, teclaassociada, id_situacaocadastro, " + i_loja.id + ", descontinuado, 0, centralizado, operacional,"
                + " valoricmssubstituicao, null, cestabasica, 0, 3";

        if (versao.igualOuMaiorQue(3, 17, 10)) {
            sql = sql + ", 0, false";
        }
        if (versao.igualOuMaiorQue(3, 21)) {
            sql = sql + ", dataprimeiraentrada ";
        }
        if (versao.igualOuMaiorQue(4)) {
            sql = sql + (i_loja.isCopiaMargem() ? ", margem" : ", 0");
            sql = sql + (i_loja.isCopiaMargem() ? ", margemminima" : ", 0");
            sql = sql + (i_loja.isCopiaMargem() ? ", margemmaxima" : ", 0");
        }

        sql = sql + " from produtocomplemento where id_loja = " + i_loja.getIdCopiarLoja() + ")";
        
        return sql;
    }
    
    private String copiarFornecedorPrazo(LojaVO i_loja) throws Exception {
        String sql = "INSERT INTO fornecedorprazo("
                + "id_fornecedor, id_loja, id_divisaofornecedor, prazoentrega, prazovisita, prazoseguranca)"
                + "(SELECT id_fornecedor, " + i_loja.getId() + ", id_divisaofornecedor, prazoentrega, prazovisita, prazoseguranca \n"
                + "FROM fornecedorprazo WHERE id_loja = " + i_loja.getIdCopiarLoja() + ");";
        
        return sql;
    }
    
    private String copiarFornecedorPrazoPedido(LojaVO i_loja) throws Exception {
        String sql = "INSERT INTO fornecedorprazopedido(id_fornecedor, id_loja, \n"
                + "diasentregapedido,diasatualizapedidoparcial) \n"
                + "(SELECT id_fornecedor, " + i_loja.getId() + ", diasentregapedido,diasatualizapedidoparcial \n"
                + "FROM fornecedorprazopedido WHERE id_loja = " + i_loja.getIdCopiarLoja() + ");";
        
        return sql;
    }
    
    private String copiarParametroValor(LojaVO i_loja) throws Exception {
        String sql = "insert into parametrovalor (\n"
                + "	id_loja,\n"
                + "	id_parametro,\n"
                + "	valor\n"
                + ") \n"
                + "select\n"
                + "	" + i_loja.getId() + ",\n"
                + "	id_parametro,\n"
                + "	valor\n"
                + "from\n"
                + "	parametrovalor\n"
                + "where\n"
                + "	id_loja = " + i_loja.getIdCopiarLoja() + "\n"
                + "	and id_parametro not in (456, 485, 486)";

        return sql;
    }

    private String copiarPdvFuncaoNivelOperador(LojaVO i_loja) throws Exception {
        String sql = "INSERT INTO pdv.funcaoniveloperador (id_loja, id_funcao, id_tiponiveloperador)\n"
                + "(SELECT " + i_loja.getId() + ", id_funcao, id_tiponiveloperador FROM pdv.funcaoniveloperador WHERE id_loja = " + i_loja.getIdCopiarLoja() + ")";

        return sql;
    }

    private String copiarPdvParametroValor(LojaVO i_loja) throws Exception {
        String sql = "INSERT INTO pdv.parametrovalor (id_loja,id_parametro,valor)\n"
                + "(SELECT " + i_loja.getId() + ",id_parametro,valor FROM pdv.parametrovalor WHERE id_loja = " + i_loja.getIdCopiarLoja() + ""
                + "AND id_parametro not in (67, 97))";

        return sql;
    }
    
    private void updateValorPdvParametroValor(LojaVO i_loja) throws Exception {
        SQLBuilder sql = new SQLBuilder();
        sql.setSchema("pdv");
        sql.setTableName("parametrovalor");
        
        sql.put("valor", i_loja.getId());
        
        sql.setWhere("id_loja = " + i_loja.getId() + " and id_parametro = 99");
        
        if (!sql.isEmpty()) {
            try (Statement stmUpdate = Conexao.createStatement()) {
                stmUpdate.execute(sql.getUpdate());
            }
        }        
    }
    
    private SQLBuilder copiarPdvCartaoLayout(LojaVO i_loja) throws Exception {
        String sql = "SELECT * FROM pdv.cartaolayout WHERE id_loja = " + i_loja.getIdCopiarLoja();
        SQLBuilder sqlInsert = null;
        
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    sql
            )) {
                while (rst.next()) {
                    int proximoId = new CodigoInternoDAO().get("pdv.cartaolayout");
                    
                    sqlInsert = new SQLBuilder();
                    sqlInsert.setSchema("pdv");
                    sqlInsert.setTableName("cartaolayout");
                    
                    sqlInsert.put("id", proximoId);
                    sqlInsert.put("id_loja", i_loja.getId());
                    sqlInsert.put("id_tipocartao", rst.getInt("id_tipocartao"));
                    sqlInsert.put("posicao", rst.getInt("posicao"));
                    sqlInsert.put("tamanho", rst.getInt("tamanho"));
                    sqlInsert.put("id_tipocartaocampo", rst.getInt("id_tipocartaocampo"));                    
                }
            }
        }
        
        return sqlInsert;
    }
    
    private SQLBuilder copiarPdvBalancaEtiquetaLayout(LojaVO i_loja) throws Exception {
        String sql = "SELECT * FROM pdv.balancaetiquetalayout WHERE id_loja = " + i_loja.getIdCopiarLoja();
        SQLBuilder sqlInsert = null;
        
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    sql
            )) {
                while (rst.next()) {
                    sqlInsert = new SQLBuilder();
                    int proximoId = new CodigoInternoDAO().get("pdv.balancaetiquetalayout");
                    
                    sqlInsert.setSchema("pdv");
                    sqlInsert.setTableName("balancaetiquetalayout");
                    
                    sqlInsert.put("id", proximoId);
                    sqlInsert.put("id_loja", i_loja.getId());                    
                    sqlInsert.put("id_tipobalancaetiqueta", rst.getInt("id_tipobalancaetiqueta"));
                    sqlInsert.put("id_tipobalancoetiquetacampo", rst.getInt("id_tipobalancoetiquetacampo"));
                    sqlInsert.put("iniciopeso", rst.getInt("iniciopeso"));
                    sqlInsert.put("tamanhopeso", rst.getInt("tamanhopeso"));
                    sqlInsert.put("iniciopreco", rst.getInt("iniciopreco"));
                    sqlInsert.put("tamanhopreco", rst.getInt("tamanhopreco"));                    
                }
            }
        }
        
        return sqlInsert;
    }
    
    public void salvar(LojaVO i_loja) throws Exception {
        Statement stm = null;
        Statement stm2 = null;
        Statement stm3 = null;
        StringBuilder sql = null;
        ResultSet rst = null;
        ResultSet rst2 = null;
        ResultSet rst3 = null;
        Versao versao = Versao.createFromConnectionInterface(Conexao.getConexao());

        try {
            Conexao.begin();
            stm = Conexao.createStatement();
            stm2 = Conexao.createStatement();
            stm3 = Conexao.createStatement();

            rst = stm.executeQuery("SELECT id FROM loja WHERE id = " + i_loja.id);

            if (!rst.next()) {
                sql = new StringBuilder();
                sql.append("INSERT INTO loja (id, descricao, id_fornecedor, id_situacaocadastro, nomeservidor, servidorcentral, id_regiao, geraconcentrador) VALUES (");
                sql.append(i_loja.id + ",");
                sql.append("'" + i_loja.descricao + "',");
                sql.append(i_loja.idFornecedor + ",");
                sql.append(SituacaoCadastro.ATIVO.getId() + ",");
                sql.append("'" + i_loja.nomeServidor + "',");
                sql.append(i_loja.servidorCentral + ",");
                sql.append(i_loja.idRegiao + ",");
                sql.append(i_loja.geraConcentrador + ")");

                stm.execute(sql.toString());

                sql = new StringBuilder();
                sql.append("INSERT INTO produtocomplemento (id_produto, prateleira, secao, estoqueminimo, estoquemaximo, valoripi, dataultimopreco,");
                sql.append(" dataultimaentrada, custosemimposto, custocomimposto, custosemimpostoanterior, custocomimpostoanterior, precovenda, precovendaanterior,");
                sql.append(" precodiaseguinte, estoque, troca, emiteetiqueta, custosemperdasemimposto, custosemperdasemimpostoanterior, customediocomimposto,");
                sql.append(" customediosemimposto, id_aliquotacredito, dataultimavenda, teclaassociada, id_situacaocadastro, id_loja, descontinuado,");
                sql.append(" quantidadeultimaentrada, centralizado, operacional, valoricmssubstituicao, dataultimaentradaanterior, cestabasica, valoroutrassubstituicao,");
                sql.append("id_tipocalculoddv");
                if (versao.igualOuMaiorQue(3,17,10)) {
                    sql.append(", id_tipoproduto, fabricacaopropria");
                }
                sql.append(")");
                sql.append(" (SELECT id_produto, prateleira, secao, estoqueminimo, estoquemaximo, valoripi, null, null, " + (i_loja.copiaCusto ? "custosemimposto" : "0") + ",");
                sql.append(" " + (i_loja.copiaCusto ? "custocomimposto" : "0") + ", 0, 0, " + (i_loja.copiaPrecoVenda ? "precovenda" : "0") + ",");
                sql.append("  0, precodiaseguinte, 0, 0, emiteetiqueta, 0, 0, 0, 0, id_aliquotacredito,");
                sql.append(" null, teclaassociada, id_situacaocadastro, " + i_loja.id + ", descontinuado, 0, centralizado, operacional,");
                sql.append(" valoricmssubstituicao, null, cestabasica, 0, 3");
                if (versao.igualOuMaiorQue(3,17,10)) {
                    sql.append(", 0, false");
                }
                sql.append(" from produtocomplemento where id_loja = " + i_loja.idCopiarLoja + ")");

                stm.execute(sql.toString());

                sql = new StringBuilder();
                sql.append("INSERT INTO fornecedorprazo(id_fornecedor, id_loja, id_divisaofornecedor,");
                sql.append(" prazoentrega,prazovisita, prazoseguranca)");
                sql.append(" (SELECT id_fornecedor, " + i_loja.id + ", id_divisaofornecedor, prazoentrega, prazovisita, prazoseguranca ");
                sql.append(" FROM fornecedorprazo WHERE id_loja = " + i_loja.idCopiarLoja + ");");

                stm.execute(sql.toString());

                sql = new StringBuilder();
                sql.append("INSERT INTO fornecedorprazopedido(id_fornecedor, id_loja,");
                sql.append(" diasentregapedido,diasatualizapedidoparcial)");
                sql.append(" (SELECT id_fornecedor, " + i_loja.id + ", diasentregapedido,diasatualizapedidoparcial ");
                sql.append(" FROM fornecedorprazopedido WHERE id_loja = " + i_loja.idCopiarLoja + ");");
                stm.execute(sql.toString());

                stm.execute(
                        "insert into parametrovalor (\n" +
                        "	id_loja,\n" +
                        "	id_parametro,\n" +
                        "	valor\n" +
                        ") \n" +
                        "select\n" +
                        "	" + i_loja.id + ",\n" +
                        "	id_parametro,\n" +
                        "	valor\n" +
                        "from\n" +
                        "	parametrovalor\n" +
                        "where\n" +
                        "	id_loja = " + i_loja.idCopiarLoja + "\n" +
                        "	and id_parametro not in (456, 485, 486)"
                );
                
                sql = new StringBuilder();
                sql.append("INSERT INTO pdv.funcaoniveloperador (id_loja, id_funcao, id_tiponiveloperador)");
                sql.append(" (SELECT " + i_loja.id + ", id_funcao, id_tiponiveloperador FROM pdv.funcaoniveloperador WHERE id_loja = " + i_loja.idCopiarLoja + ")");

                stm.execute(sql.toString());

                sql = new StringBuilder();
                sql.append("INSERT INTO pdv.parametrovalor (id_loja,id_parametro,valor)");
                sql.append(" (SELECT " + i_loja.id + ",id_parametro,valor FROM pdv.parametrovalor WHERE id_loja = " + i_loja.idCopiarLoja + "");
                sql.append("    AND id_parametro not in (67, 97))");

                stm.execute(sql.toString());

                sql = new StringBuilder();
                sql.append("UPDATE pdv.parametrovalor SET valor = " + i_loja.id);
                sql.append(" WHERE id_loja = " + i_loja.id);
                sql.append(" AND id_parametro = 99");

                stm.execute(sql.toString());

                sql = new StringBuilder();
                sql.append("SELECT * FROM pdv.cartaolayout WHERE id_loja = " + i_loja.idCopiarLoja);

                rst2 = stm.executeQuery(sql.toString());

                while (rst2.next()) {
                    int proximoId = new CodigoInternoDAO().get("pdv.cartaolayout");

                    sql = new StringBuilder();
                    sql.append("INSERT INTO pdv.cartaolayout (id, id_loja, id_tipocartao, posicao, tamanho, id_tipocartaocampo)");
                    sql.append(" VALUES (");
                    sql.append(proximoId + ",");
                    sql.append(i_loja.id + ",");
                    sql.append(rst2.getInt("id_tipocartao") + ",");
                    sql.append(rst2.getInt("posicao") + ",");
                    sql.append(rst2.getInt("tamanho") + ",");
                    sql.append(rst2.getInt("id_tipocartaocampo") + ")");

                    stm2.execute(sql.toString());
                }

                sql = new StringBuilder();
                sql.append("SELECT * FROM pdv.balancaetiquetalayout WHERE id_loja = " + i_loja.idCopiarLoja);

                rst2 = stm.executeQuery(sql.toString());

                while (rst2.next()) {
                    int proximoId = new CodigoInternoDAO().get("pdv.balancaetiquetalayout");

                    sql = new StringBuilder();
                    sql.append("INSERT INTO pdv.balancaetiquetalayout (id, id_loja, id_tipobalancaetiqueta, id_tipobalancoetiquetacampo, iniciopeso,");
                    sql.append(" tamanhopeso, iniciopreco, tamanhopreco) VALUES (");
                    sql.append(proximoId + ",");
                    sql.append(i_loja.id + ",");
                    sql.append(rst2.getInt("id_tipobalancaetiqueta") + ",");
                    sql.append(rst2.getInt("id_tipobalancoetiquetacampo") + ",");
                    sql.append(rst2.getInt("iniciopeso") + ",");
                    sql.append(rst2.getInt("tamanhopeso") + ",");
                    sql.append(rst2.getInt("iniciopreco") + ",");
                    sql.append(rst2.getInt("tamanhopreco") + ")");

                    stm2.execute(sql.toString());
                }

                if (i_loja.copiaTecladoLayout) {
                    sql = new StringBuilder();
                    sql.append("SELECT * FROM pdv.tecladolayout WHERE id_loja = " + i_loja.idCopiarLoja);

                    rst2 = stm.executeQuery(sql.toString());

                    while (rst2.next()) {
                        int proximoIdTecladoLayout = new CodigoInternoDAO().get("pdv.tecladolayout");

                        sql = new StringBuilder();
                        sql.append("INSERT INTO pdv.tecladolayout (id, id_loja, descricao) VALUES (");
                        sql.append(proximoIdTecladoLayout + ",");
                        sql.append(i_loja.id + ",");
                        sql.append("'" + rst2.getString("descricao") + "')");

                        stm2.execute(sql.toString());

                        sql = new StringBuilder();
                        sql.append("SELECT * FROM pdv.tecladolayoutfuncao AS tlf");
                        sql.append(" INNER JOIN pdv.tecladolayout AS tl ON tl.id = tlf.id_tecladolayout");
                        sql.append(" WHERE tl.id_loja = " + i_loja.idCopiarLoja);
                        sql.append(" AND tl.id = " + rst2.getInt("id"));

                        rst3 = stm2.executeQuery(sql.toString());

                        while (rst3.next()) {
                            int proximoIdLayoutFuncao = new CodigoInternoDAO().get("pdv.tecladolayoutfuncao");

                            sql = new StringBuilder();
                            sql.append("INSERT INTO pdv.tecladolayoutfuncao (id, id_tecladolayout, codigoretorno, id_funcao) VALUES (");
                            sql.append(proximoIdLayoutFuncao + ",");
                            sql.append(proximoIdTecladoLayout + ",");
                            sql.append(rst3.getInt("codigoretorno") + ",");
                            sql.append(rst3.getInt("id_funcao") + ")");

                            stm3.execute(sql.toString());
                        }
                    }
                }

                sql = new StringBuilder();
                sql.append("INSERT INTO pdv.finalizadoraconfiguracao (id_loja,id_finalizadora,aceitatroco,aceitaretirada,aceitaabastecimento,");
                sql.append(" aceitarecebimento,utilizacontravale,retiradatotal,valormaximotroco,juros,tipomaximotroco,aceitaretiradacf,retiradatotalcf,utilizado)");
                sql.append(" (SELECT " + i_loja.id + ",id_finalizadora,aceitatroco,aceitaretirada,aceitaabastecimento,aceitarecebimento,");
                sql.append(" utilizacontravale,retiradatotal,valormaximotroco,juros,tipomaximotroco,aceitaretiradacf,retiradatotalcf,utilizado FROM pdv.finalizadoraconfiguracao WHERE id_loja = " + i_loja.idCopiarLoja + ")");

                stm.execute(sql.toString());

                sql = new StringBuilder();
                sql.append("INSERT INTO dataprocessamento (id_loja, data) VALUES (");
                sql.append(i_loja.id + ",");
                sql.append("'" + Util.formatDataBanco(new DataProcessamentoDAO().get()) + "')");

                stm.execute(sql.toString());

                sql = new StringBuilder();
                sql.append("insert into comprovante select id, " + i_loja.id + " as id_loja, descricao, cabecalho, ");
                sql.append("detalhe, rodape from comprovante where id_loja = " + i_loja.idCopiarLoja);

                stm.execute(sql.toString());

                sql = new StringBuilder();
                sql.append("insert into pdv.operador (id_loja, matricula,nome,senha,codigo,id_tiponiveloperador,id_situacaocadastro) ");
                sql.append("select " + i_loja.id + ", matricula, nome, senha, codigo, id_tiponiveloperador, id_situacaocadastro ");
                sql.append("from pdv.operador ");
                sql.append("where id_loja = " + i_loja.idCopiarLoja + " ");
                sql.append("and matricula = 500001 ");

                stm.execute(sql.toString());
                
                sql =  new StringBuilder();
                sql.append("insert into notasaidasequencia (id_loja, numerocontrole, serie) values (" + i_loja.id + ", 1, 1)");
                stm.execute(sql.toString());
                
                sql = new StringBuilder();
                sql.append("insert into tiposaidanotasaidasequencia (id_loja, id_tiposaida, id_notasaidasequencia) \n" +
                            "select\n" +
                                i_loja.id + ", \n" +
                            "	t.id_tiposaida, \n" +
                            "	(select id from notasaidasequencia where id_loja = " + i_loja.id + ") id  \n" +
                            "from  \n" +
                            "	tiposaidanotasaidasequencia t\n" +
                            "where  \n" +
                            "   t.id_notasaidasequencia in "
                                + " (select\n" +
                                        " min(n.id)\n" +
                                   " from\n" +
                                        " notasaidasequencia n\n" +
                                   " join\n" +
                                        " loja l on l.id = n.id_loja\n" +
                                   " where\n" +
                                        " l.id_situacaocadastro = 1)");
                
                stm.execute(sql.toString());
                
            } else {
                sql = new StringBuilder();
                sql.append("UPDATE loja SET");
                sql.append(" descricao = '" + i_loja.descricao + "',");
                sql.append(" id_fornecedor = " + i_loja.idFornecedor + ",");
                sql.append(" id_situacaocadastro = " + SituacaoCadastro.ATIVO.getId() + ",");
                sql.append(" nomeservidor = '" + i_loja.nomeServidor + "',");
                sql.append(" servidorcentral = " + i_loja.servidorCentral + ",");
                sql.append(" id_regiao = " + i_loja.idRegiao + ",");
                sql.append(" geraconcentrador = " + i_loja.geraConcentrador);
                sql.append(" WHERE id = " + i_loja.id);

                stm.execute(sql.toString());
            }

            stm.close();
            stm2.close();
            stm3.close();
            Conexao.commit();

        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public boolean isLoja(int i_idLoja) throws Exception {
        String sql = "SELECT id from loja where id = " + i_idLoja;
        
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    sql
            )) {
                return rst.next();
            }
        }
    }

    public boolean isFornecedor(int i_idFornecedor) throws Exception {
        String sql = "SELECT id FROM loja WHERE id_fornecedor = " + i_idFornecedor;

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    sql
            )) {
                return rst.next();
            }
        }
    }

    public int getId(int i_idFornecedor) throws Exception {
        String sql = "SELECT id FROM loja WHERE id_fornecedor = " + i_idFornecedor;

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    sql
            )) {
                if (rst.next()) {
                    return rst.getInt("id");
                } else {
                    return 0;
                }
            }
        }
    }

    public int getIdFornecedor(int i_idLoja) throws Exception {
        String sql = "SELECT id_fornecedor FROM loja WHERE id = " + i_idLoja;

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    sql
            )) {
                if (rst.next()) {
                    return rst.getInt("id_fornecedor");
                } else {
                    return 0;
                }
            }
        }
    }

    public List<Estabelecimento> getLojasVR() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	l.id,\n"
                    + "	l.descricao,\n"
                    + "	f.nomefantasia,\n"
                    + "	f.razaosocial \n"
                    + "from \n"
                    + "	loja l \n"
                    + "inner join fornecedor f on l.id_fornecedor = f.id where l.id_situacaocadastro = 1\n"
                    + "order by\n"
                    + "	l.id")) {
                while (rs.next()) {
                    result.add(new Estabelecimento(rs.getString("id"), rs.getString("descricao")));
                }
            }
        }
        return result;
    }
}
