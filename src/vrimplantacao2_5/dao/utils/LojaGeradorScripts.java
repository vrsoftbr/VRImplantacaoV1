/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2_5.dao.utils;

import vr.core.parametro.versao.Versao;
import vrframework.classe.Util;
import vrimplantacao.dao.DataProcessamentoDAO;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao.vo.loja.SituacaoCadastro;
import vrimplantacao2.utils.sql.SQLBuilder;

/**
 *
 * @author Desenvolvimento
 */
public class LojaGeradorScripts {

    private Versao versao = null;

    public LojaGeradorScripts(Versao versao) {
        this.versao = versao;
    }

    public SQLBuilder criarLoja(LojaVO i_loja) throws Exception {
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

    public String copiarProdutoComplemento(LojaVO i_loja) throws Exception {
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

    public String copiarFornecedorPrazo(LojaVO i_loja) throws Exception {
        String sql = "INSERT INTO fornecedorprazo("
                + "id_fornecedor, id_loja, id_divisaofornecedor, prazoentrega, prazovisita, prazoseguranca)"
                + "(SELECT id_fornecedor, " + i_loja.getId() + ", id_divisaofornecedor, prazoentrega, prazovisita, prazoseguranca \n"
                + "FROM fornecedorprazo WHERE id_loja = " + i_loja.getIdCopiarLoja() + ");";

        return sql;
    }

    public String copiarFornecedorPrazoPedido(LojaVO i_loja) throws Exception {
        String sql = "INSERT INTO fornecedorprazopedido(id_fornecedor, id_loja, \n"
                + "diasentregapedido,diasatualizapedidoparcial) \n"
                + "(SELECT id_fornecedor, " + i_loja.getId() + ", diasentregapedido,diasatualizapedidoparcial \n"
                + "FROM fornecedorprazopedido WHERE id_loja = " + i_loja.getIdCopiarLoja() + ");";

        return sql;
    }

    public String copiarParametroValor(LojaVO i_loja) throws Exception {
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

    public String copiarPdvFuncaoNivelOperador(LojaVO i_loja) throws Exception {
        String sql = "INSERT INTO pdv.funcaoniveloperador (id_loja, id_funcao, id_tiponiveloperador)\n"
                + "(SELECT " + i_loja.getId() + ", id_funcao, id_tiponiveloperador FROM pdv.funcaoniveloperador WHERE id_loja = " + i_loja.getIdCopiarLoja() + ")";

        return sql;
    }

    public String copiarPdvParametroValor(LojaVO i_loja) throws Exception {
        String sql = "INSERT INTO pdv.parametrovalor (id_loja,id_parametro,valor)\n"
                + "(SELECT " + i_loja.getId() + ",id_parametro,valor FROM pdv.parametrovalor WHERE id_loja = " + i_loja.getIdCopiarLoja() + ""
                + "AND id_parametro not in (67, 97))";

        return sql;
    }

    public String copiarParametroAgendaecebimento(LojaVO i_loja) {
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
                + "	parametroagendarecebimento\n"
                + "where id_loja = " + i_loja.getIdCopiarLoja();
        return sqlUpdateParametroAgendaRecebimento;
    }

    public String copiarPdvFinalizadoraConfiguracao(LojaVO i_loja) throws Exception {
        String sql = "INSERT INTO pdv.finalizadoraconfiguracao (id_loja,id_finalizadora,aceitatroco,aceitaretirada,aceitaabastecimento, \n"
                + "aceitarecebimento,utilizacontravale,retiradatotal,valormaximotroco,juros,tipomaximotroco,aceitaretiradacf,retiradatotalcf,utilizado)\n"
                + "(SELECT " + i_loja.getId() + ",id_finalizadora,aceitatroco,aceitaretirada,aceitaabastecimento,aceitarecebimento, \n"
                + "utilizacontravale,retiradatotal,valormaximotroco,juros,tipomaximotroco,aceitaretiradacf,retiradatotalcf,utilizado \n"
                + "FROM pdv.finalizadoraconfiguracao WHERE id_loja = " + i_loja.getIdCopiarLoja() + ")";

        return sql;
    }

    public SQLBuilder inserirDataProcessamento(LojaVO i_loja) throws Exception {
        SQLBuilder sql = new SQLBuilder();
        sql.setSchema("public");
        sql.setTableName("dataprocessamento");

        sql.put("id_loja", i_loja.getId());
        sql.put("data", Util.formatDataBanco(new DataProcessamentoDAO().get()));

        return sql;
    }

    public String copiaUsuarioPermissao(LojaVO i_loja) throws Exception {
        String sql = "insert into permissaoloja (id, id_loja,id_permissao)\n"
                + "select nextval('permissaoloja_id_seq')," + i_loja.getId() + ",id_permissao from permissaoloja "
                + " where id_loja = " + i_loja.getIdCopiarLoja();

        return sql;
    }

    public String copiaAcumuladorLayoutRetorno(LojaVO i_loja) throws Exception {
        String sql = "insert into pdv.acumuladorlayoutretorno ( id_acumuladorlayout ,id_acumulador , retorno , titulo )\n"
                + "(select (select max((id_acumuladorlayout)+1) from pdv.acumuladorlayoutretorno), id_acumulador , retorno , titulo from pdv.acumuladorlayoutretorno\n"
                + "group by id_acumulador, retorno, titulo )";

        return sql;
    }

    public String copiaAliquotaLayoutRetorno(LojaVO i_loja) throws Exception {
        String sql = "insert into pdv.aliquotalayoutretorno ( id_aliquotalayout ,id_aliquota , retorno , codigoleitura )\n"
                + "(select (select max((id_aliquotalayout)+1) from pdv.aliquotalayoutretorno ), id_aliquota , retorno , codigoleitura \n"
                + "from pdv.aliquotalayoutretorno ret\n"
                + "join pdv.aliquotalayout ali on ali.id = ret.id_aliquota\n"
                + "where ali.id_loja = " + i_loja.getId() + "\n"
                + "group by id_aliquota, retorno, codigoleitura )";

        return sql;
    }

    public String copiaFinalizadoraRetorno(LojaVO i_loja) throws Exception {
        String sql = "insert into pdv.finalizadoralayoutretorno ( id_finalizadoralayout ,id_finalizadora , retorno , utilizado )\n"
                + "(select max((id_finalizadoralayout)+1) , id_finalizadora , retorno , utilizado from pdv.finalizadoralayoutretorno\n"
                + "group by id_finalizadora, retorno, utilizado )";

        return sql;
    }

    public String copiaPdvAcumuladorLayout(LojaVO i_loja) throws Exception {
        String sql = "insert into pdv.acumuladorlayout (id,id_loja,descricao)\n"
                + "(select (select max((id)+1) from pdv.acumuladorlayout)," + i_loja.getId() + ",descricao from pdv.acumuladorlayout where id_loja = " + i_loja.getIdCopiarLoja() + " \n"
                + "group by id)";

        return sql;
    }

    public String copiaPdvFinalizadoraLayout(LojaVO i_loja) throws Exception {
        String sql = "insert into pdv.finalizadoralayout (id, id_loja, descricao)\n"
                + "(select (select max((id)+1) from pdv.finalizadoralayout)," + i_loja.getId() + ",descricao from pdv.finalizadoralayout where id_loja = " + i_loja.getIdCopiarLoja() + "\n"
                + "group by id)";

        return sql;
    }

    public String copiaPdvAliquotaLayout(LojaVO i_loja) throws Exception {
        String sql = "insert into pdv.aliquotalayout (id, id_loja, descricao)\n"
                + "select (select max((id)+1) from pdv.aliquotalayout)," + i_loja.getId() + ", descricao from pdv.aliquotalayout where id_loja = " + i_loja.getIdCopiarLoja() + "\n"
                + "group by id";

        return sql;
    }

    public String copiaEcf(LojaVO i_loja) throws Exception {
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

    public String copiarOperador(LojaVO i_loja) throws Exception {
        String sql = "insert into pdv.operador (id_loja ,matricula,nome,senha,codigo,id_tiponiveloperador,id_situacaocadastro)\n"
                + "select " + i_loja.getId() + ",matricula,nome,senha,codigo,id_tiponiveloperador,id_situacaocadastro from pdv.operador \n"
                + "where matricula != 500001 and id_loja = " + i_loja.getIdCopiarLoja();

        return sql;
    }

    public String inserirComprovante(LojaVO i_loja) throws Exception {
        String sql = "insert into comprovante select id, " + i_loja.getId() + " as id_loja, descricao, cabecalho, \n"
                + "detalhe, rodape from comprovante where id_loja = " + i_loja.getIdCopiarLoja();

        return sql;
    }

    public String inserirGrupoEconomicoLoja(LojaVO i_loja) throws Exception {
        String sql = "insert into contabilidade.grupoeconomicoloja\n"
                + "select distinct (select max(id) + 1	from contabilidade.grupoeconomicoloja) as id, id_grupoeconomico," + i_loja.getId() + ",false \n"
                + "from contabilidade.grupoeconomicoloja \n"
                + "where id_loja = " + i_loja.getIdCopiarLoja();

        return sql;
    }

    public String copiarPdvOperador(LojaVO i_loja) throws Exception {
        String sql = "insert into pdv.operador (id_loja, matricula,nome,senha,codigo,id_tiponiveloperador,id_situacaocadastro) \n"
                + "select " + i_loja.getId() + ", matricula, nome, senha, codigo, id_tiponiveloperador, id_situacaocadastro \n"
                + "from pdv.operador \n"
                + "where id_loja = " + i_loja.getIdCopiarLoja() + " "
                + "and matricula = 500001";

        return sql;
    }

    public SQLBuilder inserirNotaSaidaSequencia(LojaVO i_loja) throws Exception {
        SQLBuilder sql = new SQLBuilder();
        sql.setSchema("public");
        sql.setTableName("notasaidasequencia");

        sql.put("id_loja", i_loja.getId());
        sql.put("numerocontrole", 1);
        sql.put("serie", 1);

        return sql;
    }

    public String copiarTipoSaidaNotaSaidaSequencia(LojaVO i_loja) throws Exception {
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
}
