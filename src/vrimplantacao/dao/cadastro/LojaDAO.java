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
import vrimplantacao2_5.vo.cadastro.TecladoLayoutFuncaoVO;
import vrimplantacao2_5.vo.cadastro.TecladoLayoutVO;

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

    public boolean isLojaExiste(LojaVO i_loja) throws Exception {
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

    public void salvar(LojaVO i_loja) throws Exception {

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
            atualizarValorPdvParametroValor(i_loja);

            /* cópia da tabela pdv.cartaolayout */
            if (copiarPdvCartaoLayout(i_loja) != null && !copiarPdvCartaoLayout(i_loja).isEmpty()) {
                stm.execute(copiarPdvCartaoLayout(i_loja).getInsert());
            }

            /* cópia da tabela pdv.balancaetiquetalayout */
            if (copiarPdvBalancaEtiquetaLayout(i_loja) != null && !copiarPdvBalancaEtiquetaLayout(i_loja).isEmpty()) {
                stm.execute(copiarPdvBalancaEtiquetaLayout(i_loja).getInsert());
            }

            /* cópia tabela pdv.tecladolayout  e pdv.tecladolayoutfuncao */
            if (i_loja.isCopiaTecladoLayout()) {
                copiarPdvTecladoLayout(i_loja);
                copiarPdvTecladoLayoutFuncao(i_loja);
            }

            /* cópia da tabela pdv.finalizadoraconfiguracao */
            stm.execute(copiarPdvFinalizadoraConfiguracao(i_loja));

            /* inserir tabela dataprocessamento */
            stm.execute(inserirDataProcessamento(i_loja).getInsert());

            /* inserir tabela comprovante */
            stm.execute(inserirComprovante(i_loja));

            /* cópia da tabela pdv.operador */
            stm.execute(copiarPdvOperador(i_loja));

            /* inserir tabela notasaidasequencia */
            stm.execute(inserirNotaSaidaSequencia(i_loja).getInsert());

            /* cópia tabela tiposaidanotasaidasequencia */
            stm.execute(copiarTipoSaidaNotaSaidaSequencia(i_loja));

            if (i_loja.isCopiaEcf() == true) {

                stm.execute(copiaEcf(i_loja));
                stm.execute(copiaPdvAcumuladorLayout(i_loja));
                stm.execute(copiaPdvFinalizadoraLayout(i_loja));
                stm.execute(copiaPdvAliquotaLayout(i_loja));
                stm.execute(copiaAliquotaLayoutRetorno(i_loja));
                stm.execute(copiaAcumuladorLayoutRetorno(i_loja));
                stm.execute(copiaFinalizadoraRetorno(i_loja));
                stm.execute(copiaPdvEcfLayout(i_loja));
            }

            if (i_loja.isCopiaOperador() == true) {
                stm.execute(copiarOperador(i_loja));
            }

            if (i_loja.isCopiaUsuario() == true) {
                stm.execute(copiaUsuarioPermissao(i_loja));
            }

            if (versao.igualOuMaiorQue(4, 1, 39)) {
                /* cópia tabela parametroagendarecebimento */
                stm.execute(copiarParametroAgendaecebimento(i_loja));
            }
            //  stm.execute(copiaEcf(i_loja));
        }
    }

    public void atualizarLoja(LojaVO i_loja) throws Exception {
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

    private void atualizarValorPdvParametroValor(LojaVO i_loja) throws Exception {
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

    public List<TecladoLayoutVO> getTecladoLayout(LojaVO i_loja) throws Exception {
        List<TecladoLayoutVO> result = new ArrayList<>();

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + "     id, \n"
                    + "     id_loja, \n"
                    + "     descricao \n"
                    + "FROM pdv.tecladolayout \n"
                    + "WHERE id_loja = " + i_loja.getIdCopiarLoja()
            )) {
                while (rst.next()) {
                    TecladoLayoutVO vo = new TecladoLayoutVO();
                    vo.setIdTecladoLayoutCopiado(rst.getInt("id"));
                    vo.setDescricao(rst.getString("descricao"));
                    result.add(vo);
                }
            }
            return result;
        }
    }

    private String copiarParametroAgendaecebimento(LojaVO i_loja) {
        String sqlUpdateParametroAgendaRecebimento = " insert\n"
                + "	into\n"
                + "	parametroagendarecebimento (dia_semana,\n"
                + "	horario_inicio,\n"
                + "	horario_termino,\n"
                + "	tempo_recebimento,\n"
                + "	quantidade_docas,\n"
                + "	id_loja)\n"
                + "select\n"
                + "	dia_semana,\n"
                + "	horario_inicio,\n"
                + "	horario_termino,\n"
                + "	tempo_recebimento,\n"
                + "	quantidade_docas,\n"
                + "	" + i_loja.getId() + " id_loja\n"
                + "from\n"
                + "	parametroagendarecebimento";
        return sqlUpdateParametroAgendaRecebimento;
    }

    public class ProximoIdTecladoLayoutVO {

        public int proximoIdTecladoLayout;
    }

    public List<ProximoIdTecladoLayoutVO> proximoIdTecladoLayoutVO = new ArrayList<>();

    public void copiarPdvTecladoLayout(LojaVO i_loja) throws Exception {

        List<TecladoLayoutVO> tecladoLayoutVO = getTecladoLayout(i_loja);

        try (Statement stm = Conexao.createStatement()) {
            for (TecladoLayoutVO vo : tecladoLayoutVO) {

                ProximoIdTecladoLayoutVO i_idTecladoLayoutVO = new ProximoIdTecladoLayoutVO();

                int proximoIdTecladoLayout = new CodigoInternoDAO().get("pdv.tecladolayout");

                SQLBuilder sqlTecladoLayout = new SQLBuilder();
                sqlTecladoLayout.setSchema("pdv");
                sqlTecladoLayout.setTableName("tecladolayout");

                sqlTecladoLayout.put("id", proximoIdTecladoLayout);
                sqlTecladoLayout.put("id_loja", i_loja.getId());
                sqlTecladoLayout.put("descricao", vo.getDescricao());

                i_idTecladoLayoutVO.proximoIdTecladoLayout = proximoIdTecladoLayout;
                proximoIdTecladoLayoutVO.add(i_idTecladoLayoutVO);

                stm.execute(sqlTecladoLayout.getInsert());
            }
        }
    }

    public List<TecladoLayoutFuncaoVO> getPdvTecladoLayoutFuncao(LojaVO i_loja) throws Exception {
        List<TecladoLayoutFuncaoVO> result = new ArrayList<>();

        List<TecladoLayoutVO> tecladoLayout = getTecladoLayout(i_loja);

        try (Statement stm = Conexao.createStatement()) {

            for (int i = 0; i < tecladoLayout.size(); i++) {
                TecladoLayoutVO tecladoLayoutVO = tecladoLayout.get(i);
                try (ResultSet rst = stm.executeQuery(
                        "SELECT \n"
                        + "     tl.id as idtecladolayout, \n"
                        + "     tlf.codigoretorno, \n"
                        + "     tlf.id_funcao \n"
                        + "FROM pdv.tecladolayoutfuncao AS tlf \n"
                        + "INNER JOIN pdv.tecladolayout AS tl ON tl.id = tlf.id_tecladolayout \n"
                        + "WHERE tl.id_loja = " + i_loja.getIdCopiarLoja() + "\n"
                        + "AND tl.id = " + tecladoLayoutVO.getIdTecladoLayoutCopiado()
                )) {
                        ProximoIdTecladoLayoutVO i_proximoIdTecladoLayoutVO = proximoIdTecladoLayoutVO.get(i);
                    while (rst.next()) {

                        TecladoLayoutFuncaoVO vo = new TecladoLayoutFuncaoVO();
                        vo.setIdTecladoLayout(i_proximoIdTecladoLayoutVO.proximoIdTecladoLayout);
                        vo.setCodigoRetorno(rst.getInt("codigoretorno"));
                        vo.setIdFuncao(rst.getInt("id_funcao"));

                        result.add(vo);

                    }
                }
            }
        }
        return result;
    }

    public void copiarPdvTecladoLayoutFuncao(LojaVO i_loja) throws Exception {

        List<TecladoLayoutFuncaoVO> tecladoLayoutFuncao = getPdvTecladoLayoutFuncao(i_loja);

        try (Statement stm = Conexao.createStatement()) {
            for (TecladoLayoutFuncaoVO vo : tecladoLayoutFuncao) {
                int proximoIdLayoutFuncao = new CodigoInternoDAO().get("pdv.tecladolayoutfuncao");

                SQLBuilder sqlTecladoLayoutFuncao = new SQLBuilder();
                sqlTecladoLayoutFuncao.setSchema("pdv");
                sqlTecladoLayoutFuncao.setTableName("tecladolayoutfuncao");

                sqlTecladoLayoutFuncao.put("id", proximoIdLayoutFuncao);
                sqlTecladoLayoutFuncao.put("id_tecladolayout", vo.getIdTecladoLayout());
                sqlTecladoLayoutFuncao.put("codigoretorno", vo.getCodigoRetorno());
                sqlTecladoLayoutFuncao.put("id_funcao", vo.getIdFuncao());

                stm.execute(sqlTecladoLayoutFuncao.getInsert());
            }
        }
    }

    private String copiarPdvFinalizadoraConfiguracao(LojaVO i_loja) throws Exception {
        String sql = "INSERT INTO pdv.finalizadoraconfiguracao (id_loja,id_finalizadora,aceitatroco,aceitaretirada,aceitaabastecimento, \n"
                + "aceitarecebimento,utilizacontravale,retiradatotal,valormaximotroco,juros,tipomaximotroco,aceitaretiradacf,retiradatotalcf,utilizado)\n"
                + "(SELECT " + i_loja.getId() + ",id_finalizadora,aceitatroco,aceitaretirada,aceitaabastecimento,aceitarecebimento, \n"
                + "utilizacontravale,retiradatotal,valormaximotroco,juros,tipomaximotroco,aceitaretiradacf,retiradatotalcf,utilizado \n"
                + "FROM pdv.finalizadoraconfiguracao WHERE id_loja = " + i_loja.getIdCopiarLoja() + ")";

        return sql;
    }

    private SQLBuilder inserirDataProcessamento(LojaVO i_loja) throws Exception {
        SQLBuilder sql = new SQLBuilder();
        sql.setSchema("public");
        sql.setTableName("dataprocessamento");

        sql.put("id_loja", i_loja.getId());
        sql.put("data", Util.formatDataBanco(new DataProcessamentoDAO().get()));

        return sql;
    }

    private String copiaUsuarioPermissao(LojaVO i_loja) throws Exception {
        String sql = "insert into permissaoloja (id, id_loja,id_permissao)\n"
                + "select nextval('permissaoloja_id_seq')," + i_loja.getId() + ",id_permissao from permissaoloja ";

        return sql;
    }

    private String copiaAcumuladorLayoutRetorno(LojaVO i_loja) throws Exception {
        String sql = "insert into pdv.acumuladorlayoutretorno ( id_acumuladorlayout ,id_acumulador , retorno , titulo )\n"
                + "(select max((id_acumuladorlayout)+1) , id_acumulador , retorno , titulo from pdv.acumuladorlayoutretorno\n"
                + "group by id_acumulador, retorno, titulo )";

        return sql;
    }

    private String copiaAliquotaLayoutRetorno(LojaVO i_loja) throws Exception {
        String sql = "insert into pdv.aliquotalayoutretorno ( id_aliquotalayout ,id_aliquota , retorno , codigoleitura )\n"
                + "(select max((id_aliquotalayout)+1) , id_aliquota , retorno , codigoleitura from pdv.aliquotalayoutretorno\n"
                + "group by id_aliquota, retorno, codigoleitura )";

        return sql;
    }

    private String copiaFinalizadoraRetorno(LojaVO i_loja) throws Exception {
        String sql = "insert into pdv.finalizadoralayoutretorno ( id_finalizadoralayout ,id_finalizadora , retorno , utilizado )\n"
                + "(select max((id_finalizadoralayout)+1) , id_finalizadora , retorno , utilizado from pdv.finalizadoralayoutretorno\n"
                + "group by id_finalizadora, retorno, utilizado )";

        return sql;
    }

    private String copiaPdvAcumuladorLayout(LojaVO i_loja) throws Exception {
        String sql = "insert into pdv.acumuladorlayout (id,id_loja,descricao)\n"
                + "(select max((id)+1)," + i_loja.getId() + ",descricao from pdv.acumuladorlayout where id_loja = " + i_loja.getIdCopiarLoja() + " \n"
                + "group by id)";

        return sql;
    }

    private String copiaPdvFinalizadoraLayout(LojaVO i_loja) throws Exception {
        String sql = "insert into pdv.finalizadoralayout (id, id_loja, descricao)\n"
                + "(select max((id)+1)," + i_loja.getId() + ",descricao from pdv.finalizadoralayout where id_loja = " + i_loja.getIdCopiarLoja() + "\n"
                + "group by id)";

        return sql;
    }

    private String copiaPdvAliquotaLayout(LojaVO i_loja) throws Exception {
        String sql = "insert into pdv.aliquotalayout (id, id_loja, descricao)\n"
                + "select max((id)+1) ," + i_loja.getId() + ", descricao from pdv.aliquotalayout where id_loja = " + i_loja.getIdCopiarLoja() + "\n"
                + "group by id";

        return sql;
    }

    private String copiaEcf(LojaVO i_loja) throws Exception {
        String sql = "insert into pdv.ecf (ID_LOJA,ecf,descricao,id_tipomarca,id_tipomodelo,id_situacaocadastro,numeroserie,\n"
                + "	mfadicional,numerousuario ,tipoecf,versaosb,datahoragravacaosb,datahoracadastro,incidenciadesconto,\n"
                + "	versaobiblioteca,geranfpaulista,id_tipoestado,versao,datamovimento,cargagdata,cargaparam,cargalayout,\n"
                + "	cargaimagem,id_tipolayoutnotapaulista,touch,alteradopaf,horamovimento,id_tipoemissor,id_modelopdv) \n"
                + "	select " + i_loja.getId() + " , ecf,descricao,id_tipomarca,id_tipomodelo,id_situacaocadastro,'999'||length(tipoecf||versaosb)+row_number() over(),\n"
                + "	mfadicional,numerousuario ,tipoecf,versaosb,datahoragravacaosb,datahoracadastro,incidenciadesconto,\n"
                + "	versaobiblioteca,geranfpaulista,id_tipoestado,versao,datamovimento,cargagdata,cargaparam,cargalayout,\n"
                + "	cargaimagem,id_tipolayoutnotapaulista,touch,alteradopaf,horamovimento,id_tipoemissor,id_modelopdv\n"
                + "	from pdv.ecf \n"
                + "	where id_loja = " + i_loja.getIdCopiarLoja();

        return sql;
    }

    private String copiaPdvEcfLayout(LojaVO i_loja) throws Exception {
        String sql = "insert into pdv.ecflayout (id, id_ecf,id_tecladolayout,id_finalizadoralayout,id_acumuladorlayout,id_aliquotalayout,regracalculo,arredondamentoabnt)\n"
                + "select \n"
                + " nextval('pdv.ecflayout_id_seq') , \n"
                + " (select id from pdv.ecf where id_loja = " + i_loja.getId() + ") as id_ecf,\n"
                + " (select id from pdv.tecladolayout where id_loja = " + i_loja.getId() + ") as id_teclado,\n"
                + " (select id from pdv.finalizadoralayout where id_loja = " + i_loja.getId() + ") as id_finalizadora,\n"
                + " (select id from pdv.acumuladorlayout where id_loja = " + i_loja.getId() + ") as id_acumaldor,\n"
                + " (select id from pdv.aliquotalayout where id_loja = " + i_loja.getId() + ") as id_aliquotalayout,\n"
                + " regracalculo ,\n"
                + " arredondamentoabnt \n"
                + " from pdv.ecflayout ecf  \n"
                + " join pdv.ecf e on e.id = ecf.id_ecf"
                + " where e.id_loja = " + i_loja.getIdCopiarLoja();

        return sql;

    }

    private String copiarOperador(LojaVO i_loja) throws Exception {
        String sql = "insert into pdv.operador (id_loja ,matricula,nome,senha,codigo,id_tiponiveloperador,id_situacaocadastro)\n"
                + "select " + i_loja.getId() + ",matricula,nome,senha,codigo,id_tiponiveloperador,id_situacaocadastro from pdv.operador \n"
                + "where matricula != 500001 and id_loja = " + i_loja.getIdCopiarLoja();

        return sql;
    }

    private String inserirComprovante(LojaVO i_loja) throws Exception {
        String sql = "insert into comprovante select id, " + i_loja.getId() + " as id_loja, descricao, cabecalho, \n"
                + "detalhe, rodape from comprovante where id_loja = " + i_loja.getIdCopiarLoja();

        return sql;
    }

    private String copiarPdvOperador(LojaVO i_loja) throws Exception {
        String sql = "insert into pdv.operador (id_loja, matricula,nome,senha,codigo,id_tiponiveloperador,id_situacaocadastro) \n"
                + "select " + i_loja.getId() + ", matricula, nome, senha, codigo, id_tiponiveloperador, id_situacaocadastro \n"
                + "from pdv.operador \n"
                + "where id_loja = " + i_loja.getIdCopiarLoja() + " "
                + "and matricula = 500001";

        return sql;
    }

    private SQLBuilder inserirNotaSaidaSequencia(LojaVO i_loja) throws Exception {
        SQLBuilder sql = new SQLBuilder();
        sql.setSchema("public");
        sql.setTableName("notasaidasequencia");

        sql.put("id_loja", i_loja.getId());
        sql.put("numerocontrole", 1);
        sql.put("serie", 1);

        return sql;
    }

    private String copiarTipoSaidaNotaSaidaSequencia(LojaVO i_loja) throws Exception {
        String sql = "insert into tiposaidanotasaidasequencia (id_loja, id_tiposaida, id_notasaidasequencia) \n"
                + "select\n"
                + i_loja.getId() + ", \n"
                + "	t.id_tiposaida, \n"
                + "	(select id from notasaidasequencia where id_loja = " + i_loja.getId() + ") id  \n"
                + "from  \n"
                + "	tiposaidanotasaidasequencia t\n"
                + "where  \n"
                + "   t.id_notasaidasequencia in "
                + " (select\n"
                + " min(n.id)\n"
                + " from\n"
                + " notasaidasequencia n\n"
                + " join\n"
                + " loja l on l.id = n.id_loja\n"
                + " where\n"
                + " l.id_situacaocadastro = 1)";

        return sql;
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

    public List<LojaVO> getLojasVRMapeada() throws Exception {
        List<LojaVO> result = new ArrayList<>();

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	l.id,\n"
                    + "	l.descricao,\n"
                    + "	f.nomefantasia,\n"
                    + "	f.razaosocial\n"
                    + "from\n"
                    + "	loja l\n"
                    + "inner join fornecedor f on\n"
                    + "	l.id_fornecedor = f.id\n"
                    + "where\n"
                    + "	l.id_situacaocadastro = 1 and \n"
                    + "	l.id not in (select distinct id_lojadestino from implantacao2_5.conexaoloja)\n"
                    + "order by\n"
                    + "	l.id")) {
                while (rs.next()) {
                    LojaVO vo = new LojaVO();

                    vo.setId(rs.getInt("id"));
                    vo.setDescricao(rs.getString("descricao"));

                    result.add(vo);
                }
            }
        }
        return result;
    }

}
