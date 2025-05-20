/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2_5.dao.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
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

    Date date = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

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
        if (versao.igualOuMaiorQue(4, 2, 0)) {
            sql = sql + ", validade";
        }

        sql = sql + ")";

        sql = sql + " (SELECT id_produto, prateleira, secao, estoqueminimo, estoquemaximo, valoripi, null, null, " + (i_loja.isCopiaCusto() ? "custosemimposto" : "0") + ","
                + " " + (i_loja.isCopiaCusto() ? "custocomimposto" : "0") + ", 0, 0, " + (i_loja.isCopiaPrecoVenda() ? "precovenda" : "0") + ","
                + "  0, precodiaseguinte, 0, 0, emiteetiqueta, 0, 0, 0, 0, id_aliquotacredito,"
                + " null, teclaassociada, id_situacaocadastro, " + i_loja.id + ", descontinuado, 0, centralizado, operacional,"
                + " valoricmssubstituicao, null, 0, 0, 3";

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
        if (versao.igualOuMaiorQue(4, 2, 0)) {
            sql = sql + ", validade";
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
                + " AND id_parametro not in (67, 97))";

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

    public String inserirDataPdvConsistencia(LojaVO i_loja) throws Exception {
        String sql
                = "INSERT INTO pdv.consistencia (data, id_loja) \n"
                + "VALUES (CURRENT_DATE - INTERVAL '1 day', " + i_loja.getId() + ");";

        return sql;
    }

    public String copiaUsuarioPermissao(LojaVO i_loja) throws Exception {
        String sql = "insert into permissaoloja (id, id_loja,id_permissao)\n"
                + "select nextval('permissaoloja_id_seq')," + i_loja.getId() + ",id_permissao from permissaoloja "
                + " where id_loja = " + i_loja.getIdCopiarLoja();

        return sql;
    }

    public String copiarConfiguracaoSped(LojaVO i_loja) throws Exception {
        String sql = "DO $$\n"
                + "    BEGIN\n"
                + "        INSERT INTO sped.configuracaoloja (id_loja, id_contabilista)\n"
                + "        SELECT " + i_loja.getId() + ", id_contabilista FROM sped.configuracaoloja WHERE id_loja = " + i_loja.getIdCopiarLoja() + ";\n"
                + "\n"
                + "        INSERT INTO sped.configuracao (\n"
                + "            id_loja, tipocontribuicao, codigoincidencia, metodoapropriacao, tipoapuracao,\n"
                + "            aluguel, leasing, valorpis, valorcofins, codigopiscumulativo, codigopisnaocumulativo,\n"
                + "            codigocofinscumulativo, codigocofinsnaocumulativo, id_contabilista, id_planocontareferencial\n"
                + "        )\n"
                + "        SELECT\n"
                + "            " + i_loja.getId() + ", tipocontribuicao, codigoincidencia, metodoapropriacao, tipoapuracao,\n"
                + "            aluguel, leasing, valorpis, valorcofins, codigopiscumulativo, codigopisnaocumulativo,\n"
                + "            codigocofinscumulativo, codigocofinsnaocumulativo, id_contabilista, id_planocontareferencial\n"
                + "        FROM sped.configuracao\n"
                + "        WHERE id_loja = " + i_loja.getIdCopiarLoja() + ";\n"
                + "    END\n"
                + "    $$;";
        return sql;
    }

    public String copiaAcumuladorLayoutRetorno(LojaVO i_loja) throws Exception {
        String sql = "INSERT INTO pdv.acumuladorlayoutretorno (id_acumuladorlayout, id_acumulador, retorno, titulo )\n"
                + "SELECT\n"
                + "(SELECT id FROM pdv.acumuladorlayout WHERE id_loja = " + i_loja.getId() + ") AS id_acumuladorlayout,\n"
                + "id_acumulador, \n"
                + "retorno, \n"
                + "titulo\n"
                + "FROM pdv.acumuladorlayoutretorno a \n"
                + "LEFT JOIN pdv.acumuladorlayout al ON al.id = a.id_acumuladorlayout \n"
                + "WHERE al.id_loja = " + i_loja.getIdCopiarLoja() + ";";

        return sql;
    }

    public String copiaAliquotaLayoutRetorno(LojaVO i_loja) throws Exception {
        String sql = "INSERT INTO pdv.aliquotalayoutretorno (id_aliquotalayout, id_aliquota, retorno, codigoleitura)\n"
                + "SELECT \n"
                + "    (SELECT id FROM pdv.aliquotalayout WHERE id_loja  = " + i_loja.getId() + ") AS id_aliquotalayout,\n"
                + "    id_aliquota,\n"
                + "    retorno,\n"
                + "    codigoleitura\n"
                + "FROM \n"
                + "    pdv.aliquotalayoutretorno a\n"
                + "    LEFT JOIN pdv.aliquotalayout al ON al.id = a.id_aliquotalayout \n"
                + "WHERE \n"
                + "    al.id_loja = " + i_loja.getIdCopiarLoja() + ";";

        return sql;
    }

    public String copiaFinalizadoraRetorno(LojaVO i_loja) throws Exception {
        String sql = "INSERT INTO pdv.finalizadoralayoutretorno ( id_finalizadoralayout ,id_finalizadora , retorno , utilizado )\n"
                + "SELECT \n"
                + "(SELECT id FROM pdv.finalizadoralayout f\n"
                + "WHERE id_loja = " + i_loja.getId() + ") AS id_finalizadoralayout, \n"
                + "id_finalizadora, \n"
                + "retorno, \n"
                + "utilizado \n"
                + "FROM pdv.finalizadoralayoutretorno f\n"
                + " LEFT JOIN pdv.finalizadoralayout fl ON fl.id = f.id_finalizadoralayout \n"
                + "WHERE fl.id_loja = " + i_loja.getIdCopiarLoja() + ";";

        return sql;
    }

    public String copiaPdvAcumuladorLayout(LojaVO i_loja) throws Exception {
        String sql = "insert into pdv.acumuladorlayout (id,id_loja,descricao)\n"
                + " select (select max(id) from pdv.acumuladorlayout) + row_number()over()," + i_loja.getId() + ",descricao \n"
                + "from pdv.acumuladorlayout where id_loja = " + i_loja.getIdCopiarLoja();

        return sql;
    }

    public String copiaPdvFinalizadoraLayout(LojaVO i_loja) throws Exception {
        String sql = "insert into pdv.finalizadoralayout (id, id_loja, descricao)\n"
                + "(select (select max(id) from pdv.finalizadoralayout) + row_number() over()," + i_loja.getId() + ",descricao \n"
                + "from pdv.finalizadoralayout where id_loja = 1\n"
                + "group by id)";

        return sql;
    }

    public String copiaPdvAliquotaLayout(LojaVO i_loja) throws Exception {
        String sql = "insert into pdv.aliquotalayout (id, id_loja, descricao)\n"
                + "select (select max((id)+1) from pdv.aliquotalayout)," + i_loja.getId() + ", descricao from pdv.aliquotalayout where id_loja = " + i_loja.getIdCopiarLoja() + "\n"
                + "group by id";

        return sql;
    }

    public String copiaPdvCartaoLayout(LojaVO i_loja) throws Exception {
        String sql
                = "BEGIN;\n"
                + "DELETE FROM pdv.cartaolayout WHERE id_loja = " + i_loja.getId() + ";\n"
                + "INSERT INTO pdv.cartaolayout (id, id_loja, id_tipocartao, posicao, tamanho, id_tipocartaocampo)\n"
                + "SELECT (SELECT COALESCE(MAX(id), 0) FROM pdv.cartaolayout) + \n"
                + "       ROW_NUMBER() OVER (), \n"
                + "       " + i_loja.getId() + ", \n"
                + "       id_tipocartao, \n"
                + "       posicao, \n"
                + "       tamanho, \n"
                + "       id_tipocartaocampo \n"
                + "FROM pdv.cartaolayout \n"
                + "WHERE id_loja = " + i_loja.getIdCopiarLoja() + ";\n"
                + "COMMIT;";

        return sql;
    }

    public String copiaReceitaLoja(LojaVO i_loja) throws Exception {
        String sql = "insert into receitaloja (id, id_receita, id_loja)\n"
                + "select nextval('receitaloja_id_seq'),id_receita ," + i_loja.getId() + " from receitaloja where id_loja = " + i_loja.getIdCopiarLoja();

        return sql;
    }

    public String copiaReceitaToledoLoja(LojaVO i_loja) throws Exception {
        String sql = "insert into receitatoledoloja (id, id_receitatoledo, id_loja)\n"
                + "select nextval('receitatoledoloja_id_seq') , id_receitatoledo ," + i_loja.getId() + " from receitatoledoloja where id_loja =  " + i_loja.getIdCopiarLoja();
        return sql;
    }

    public String copiarPagarFornecedorLoja(LojaVO i_loja) throws Exception {

        String sql = " do $$\n"
                + "   declare \n"
                + "      v_id_pagar integer;\n"
                + "      v_id_loja integer;\n"
                + "      cb record;\n"
                + "   begin\n"
                + "   	  v_id_loja = " + i_loja.getId() + ";\n"
                + "   for cb in (\n"
                + "	select distinct\n"
                + "	 pf.id, \n"
                + "	 v_id_loja, \n"
                + "	 pf.id_fornecedor, \n"
                + "	 pf.id_tipoentrada, \n"
                + "	 pf.numerodocumento,\n"
                + "	 pf.dataentrada, \n"
                + "	 pf.dataemissao, \n"
                + "	 pf.valor, \n"
                + "	 pf.id_notadespesa, \n"
                + "	 pf.id_notaentrada, \n"
                + "	 pf.id_transferenciaentrada,\n"
                + "	 pf.id_pagaroutrasdespesas, \n"
                + "	 pf.id_geracaoretencaotributo, \n"
                + "	 pf.id_escritasaldo\n"
                + "	from pagarfornecedor pf \n"
                + "	join pagarfornecedorparcela pap on pap.id_pagarfornecedor = pf.id\n"
                + "	where\n"
                + "	 pap.id_situacaopagarfornecedorparcela = 0\n"
                + "	 and \n"
                + " 	 pf.id_loja = " + i_loja.getIdCopiarLoja() + "\n"
                + "   ) loop\n"
                + "   	\n"
                + "	insert into pagarfornecedor (id_loja, id_fornecedor, id_tipoentrada, numerodocumento,\n"
                + "	dataentrada, dataemissao, valor, id_notadespesa, id_notaentrada, id_transferenciaentrada,\n"
                + "	id_pagaroutrasdespesas, id_geracaoretencaotributo, id_escritasaldo)\n"
                + "	values( \n"
                + "	 cb.v_id_loja,\n"
                + "	 cb.id_fornecedor,\n"
                + "	 cb.id_tipoentrada, \n"
                + "	 cb.numerodocumento,\n"
                + " 	 cb.dataentrada,\n"
                + "	 cb.dataemissao,\n"
                + "	 cb.valor,\n"
                + "	 cb.id_notadespesa,\n"
                + "	 cb.id_notaentrada,\n"
                + "	 cb.id_transferenciaentrada,\n"
                + "	 cb.id_pagaroutrasdespesas,\n"
                + "	 cb.id_geracaoretencaotributo,\n"
                + "	 cb.id_escritasaldo\n"
                + "	) returning id into v_id_pagar;\n"
                + "	insert into pagarfornecedorparcela(id_pagarfornecedor,numeroparcela,datavencimento,datapagamento,valor,observacao,\n"
                + "		id_situacaopagarfornecedorparcela,id_tipopagamento,datapagamentocontabil,id_banco,\n"
                + "		agencia,conta,numerocheque,conferido,valoracrescimo,id_contacontabilfinanceiro,\n"
                + "		id_conciliacaobancarialancamento,exportado,datahoraalteracao,id_lojabaixa\n"
                + "		)\n"
                + "		select\n"
                + "		 v_id_pagar,\n"
                + "		 pap.numeroparcela,\n"
                + "		 pap.datavencimento,\n"
                + "		 pap.datapagamento,\n"
                + "		 pap.valor,\n"
                + "		 pap.observacao,\n"
                + "		 pap.id_situacaopagarfornecedorparcela,\n"
                + "		 pap.id_tipopagamento,\n"
                + "		 pap.datapagamentocontabil,\n"
                + "		 pap.id_banco,\n"
                + "		 pap.agencia,\n"
                + "		 pap.conta,\n"
                + "		 pap.numerocheque,\n"
                + "		 pap.conferido,\n"
                + "		 pap.valoracrescimo,\n"
                + "		 pap.id_contacontabilfinanceiro,\n"
                + "		 pap.id_conciliacaobancarialancamento,\n"
                + "		 pap.exportado,\n"
                + "		 pap.datahoraalteracao,\n"
                + "		 pap.id_lojabaixa\n"
                + "		from pagarfornecedor pf\n"
                + " 		join pagarfornecedorparcela pap on pap.id_pagarfornecedor = pf.id\n"
                + " 		 where \n"
                + " 		  pap.id_situacaopagarfornecedorparcela = 0\n"
                + " 		  and \n"
                + "		  pf.id = cb.id;\n"
                + "   end loop;\n"
                + "   \n"
                + "end;\n"
                + "$$";
        return sql;
    }

    public String copiarOutrasDespesas(LojaVO i_loja) throws Exception {

        String sql = "INSERT INTO pagaroutrasdespesas\n"
                + "(id_fornecedor, numerodocumento, id_tipoentrada, dataemissao, dataentrada, \n"
                + "valor, id_situacaopagaroutrasdespesas, id_loja, observacao, id_tipopiscofins, \n"
                + "datahoraalteracao, pendenciaworkflow)\n"
                + "select id_fornecedor, numerodocumento, id_tipoentrada, dataemissao, dataentrada, \n"
                + "valor, id_situacaopagaroutrasdespesas, " + i_loja.getId() + ", observacao, id_tipopiscofins, \n"
                + "datahoraalteracao, pendenciaworkflow\n"
                + "from pagaroutrasdespesas p\n"
                + "where p.id_loja = " + i_loja.getIdCopiarLoja() + " and p.id_situacaopagaroutrasdespesas = 0;";

        return sql;
    }

    //validar versao
    public String copiaEcf(LojaVO i_loja) throws Exception {
        String sql = "insert into pdv.ecf (\n"
                + "	id_loja ,\n"
                + "	ecf ,\n"
                + "	descricao ,\n"
                + "	id_tipomarca ,\n"
                + "	id_tipomodelo ,\n"
                + "	id_situacaocadastro ,\n"
                + "	numeroserie ,\n"
                + "	mfadicional ,\n"
                + "	numerousuario ,\n"
                + "	tipoecf ,\n"
                + "	versaosb ,\n"
                + "	datahoragravacaosb ,\n"
                + "	datahoracadastro ,\n"
                + "	incidenciadesconto ,\n"
                + "	versaobiblioteca ,\n"
                + "	geranfpaulista ,\n"
                + "	id_tipoestado ,\n"
                + "	versao ,\n"
                + "	datamovimento ,\n"
                + "	cargagdata ,\n"
                + "	cargaparam ,\n"
                + "	cargalayout ,\n"
                + "	cargaimagem ,\n"
                + "	id_tipolayoutnotapaulista ,\n"
                + "	touch ,\n"
                + "	alteradopaf ,\n"
                + "	horamovimento ,\n"
                + "	id_tipoemissor ,\n"
                + "	id_modelopdv \n";
        if (versao.igualOuMaiorQue(4, 1, 39)) {
            sql = sql + " ,utilizavrconcentradorapi ,\n"
                    + " pixclientid) \n";
        } else {
            sql = sql + ")";
        }
        sql = sql
                + "	select \n"
                + "	" + i_loja.getId() + " ,\n"
                + "     ecf,\n"
                + "     descricao,\n"
                + "     id_tipomarca,\n"
                + "     id_tipomodelo,\n"
                + "     id_situacaocadastro,\n"
                + "     numeroserie,\n"
                + "     mfadicional,\n"
                + "     numerousuario,\n"
                + "     tipoecf,\n"
                + "     versaosb,\n"
                + "     datahoragravacaosb,\n"
                + "     datahoracadastro,\n"
                + "     incidenciadesconto,\n"
                + "     versaobiblioteca,\n"
                + "     geranfpaulista,\n"
                + "     id_tipoestado,\n"
                + "     versao,\n"
                + "     datamovimento,\n"
                + "     cargagdata,\n"
                + "     cargaparam,\n"
                + "     cargalayout,\n"
                + "     cargaimagem,\n"
                + "     id_tipolayoutnotapaulista,\n"
                + "     touch,\n"
                + "     alteradopaf,\n"
                + "     horamovimento,\n"
                + "     id_tipoemissor,\n"
                + "     id_modelopdv\n";
        if (versao.igualOuMaiorQue(4, 1, 39)) {
            sql = sql + " ,utilizavrconcentradorapi ,\n"
                    + " pixclientid \n";
        }
        sql = sql + " from pdv.ecf \n"
                + "     where id_loja = " + i_loja.getIdCopiarLoja();

        return sql;
    }

    /*
    public String copiaEcf_41(LojaVO i_loja) throws Exception {
        String sql = "insert into pdv.ecf (\n"
                + "	id_loja ,\n"
                + "	ecf ,\n"
                + "	descricao ,\n"
                + "	id_tipomarca ,\n"
                + "	id_tipomodelo ,\n"
                + "	id_situacaocadastro ,\n"
                + "	numeroserie ,\n"
                + "	mfadicional ,\n"
                + "	numerousuario ,\n"
                + "	tipoecf ,\n"
                + "	versaosb ,\n"
                + "	datahoragravacaosb ,\n"
                + "	datahoracadastro ,\n"
                + "	incidenciadesconto ,\n"
                + "	versaobiblioteca ,\n"
                + "	geranfpaulista ,\n"
                + "	id_tipoestado ,\n"
                + "	versao ,\n"
                + "	datamovimento ,\n"
                + "	cargagdata ,\n"
                + "	cargaparam ,\n"
                + "	cargalayout ,\n"
                + "	cargaimagem ,\n"
                + "	id_tipolayoutnotapaulista ,\n"
                + "	touch ,\n"
                + "	alteradopaf ,\n"
                + "	horamovimento ,\n"
                + "	id_tipoemissor ,\n"
                + "	id_modelopdv ,\n"
                + "	utilizavrconcentradorapi ,\n"
                + "	pixclientid )\n"
                + "	select \n"
                + "	" + i_loja.getId() + " ,\n"
                + "	ecf ,\n"
                + "	descricao ,\n"
                + "	id_tipomarca ,\n"
                + "	id_tipomodelo ,\n"
                + "	id_situacaocadastro ,\n"
                + "	numeroserie ,\n"
                + "	mfadicional ,\n"
                + "	numerousuario ,\n"
                + "	tipoecf ,\n"
                + "	versaosb ,\n"
                + "	datahoragravacaosb ,\n"
                + "	datahoracadastro ,\n"
                + "	incidenciadesconto ,\n"
                + "	versaobiblioteca ,\n"
                + "	geranfpaulista ,\n"
                + "	id_tipoestado ,\n"
                + "	versao ,\n"
                + "	datamovimento ,\n"
                + "	cargagdata ,\n"
                + "	cargaparam ,\n"
                + "	cargalayout ,\n"
                + "	cargaimagem ,\n"
                + "	id_tipolayoutnotapaulista ,\n"
                + "	touch ,\n"
                + "	alteradopaf ,\n"
                + "	horamovimento ,\n"
                + "	id_tipoemissor ,\n"
                + "	id_modelopdv ,\n"
                + "	utilizavrconcentradorapi ,\n"
                + "	pixclientid \n"
                + "     from pdv.ecf \n"
                + "	where id_loja = " + i_loja.getIdCopiarLoja();

        return sql;
    }*/
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

    public String copiaOferta(LojaVO i_loja) throws Exception {
        String sql
                = "insert into oferta (\n"
                + "	id_loja,\n"
                + "	id_produto,\n"
                + "	datainicio,\n"
                + "	datatermino,\n"
                + "	precooferta,\n"
                + "	preconormal,\n"
                + "	id_situacaooferta,\n"
                + "	id_tipooferta,\n"
                + "	precoimediato,\n"
                + "	ofertafamilia,\n"
                + "	ofertaassociado,\n"
                + "	controle,\n"
                + "	aplicapercentualprecoassociado,\n"
                + "	encerraoferta,\n"
                + "	encerraofertaitens,\n"
                + "	bloquearvenda ,\n"
                + "	bloquearvendaitens)\n"
                + "	select \n"
                + "	" + i_loja.getId() + ", id_produto, datainicio, datatermino, precooferta,preconormal, id_situacaooferta ,id_tipooferta, \n"
                + "	precoimediato , ofertafamilia , ofertaassociado, controle , aplicapercentualprecoassociado , \n"
                + "	encerraoferta , encerraofertaitens , bloquearvenda , bloquearvendaitens \n"
                + "	from oferta\n"
                + "	where id_loja = " + i_loja.idCopiarLoja + "\n"
                + "	and datatermino >= '" + sdf.format(date) + "'";

        return sql;

    }

    public String validaOferta(LojaVO i_loja) throws Exception {
        String sql
                = "select * from oferta where id_loja =" + i_loja.getId();

        return sql;
    }

    public String copiaPromocao(LojaVO i_loja) throws Exception {
        String sql
                = "insert into promocao (id,id_loja,descricao,datainicio,datatermino,pontuacao,quantidade,qtdcupom,id_situacaocadastro,id_tipopromocao,\n"
                + "	valor,controle,id_tipopercentualvalor,id_tipoquantidade,aplicatodos,cupom,valordesconto,valorreferenteitenslista,verificaprodutosauditados,\n"
                + "	datalimiteresgatecupom,id_tipopercentualvalordesconto,valorpaga,desconsideraritem,qtdlimite,somenteclubevantagens,diasexpiracao,\n"
                + "	utilizaquantidadeproporcional,desconsideraprodutoemoferta) \n"
                + "	select (select max(id) from promocao) + row_number() over(), \n"
                + "	" + i_loja.getId() + ",descricao,datainicio,datatermino,pontuacao,quantidade,qtdcupom,id_situacaocadastro,id_tipopromocao,valor,controle,id_tipopercentualvalor,\n"
                + "	id_tipoquantidade,aplicatodos,cupom,valordesconto,valorreferenteitenslista,verificaprodutosauditados,datalimiteresgatecupom,id_tipopercentualvalordesconto,\n"
                + "	valorpaga,desconsideraritem,qtdlimite,somenteclubevantagens,diasexpiracao,utilizaquantidadeproporcional,desconsideraprodutoemoferta\n"
                + "	from promocao where id_loja = " + i_loja.idCopiarLoja + "\n"
                + "	and datatermino >= '" + sdf.format(date) + "'";
        return sql;
    }

    public String copiaPromocaoItem(LojaVO i_loja) throws Exception {
        String sql
                = "insert into promocaoitem (id_promocao, id_produto, precovenda) \n"
                + " select \n"
                + " p2.id,\n"
                + " pi.id_produto,\n"
                + " pi.precovenda \n"
                + " from promocao p\n"
                + " join promocaoitem pi on p.id = pi.id_promocao \n"
                + " join promocao p2 on p.descricao = p2.descricao and p2.id_loja = " + i_loja.getId() + " \n"
                + " where \n"
                + " p.id_loja = " + i_loja.idCopiarLoja + "\n"
                + " and p.datatermino >= '" + sdf.format(date) + "'";
        return sql;
    }

    public String copiaPromocaoFinalizadora(LojaVO i_loja) throws Exception {
        String sql
                = "insert into promocaofinalizadora (id_promocao, id_finalizadora)\n"
                + "select\n"
                + "p3.id ,\n"
                + "p2.id_finalizadora \n"
                + "from promocao p \n"
                + "join promocaofinalizadora p2 on p2.id_promocao = p.id \n"
                + "join promocao p3 on p3.descricao = p.descricao and p3.id_loja = " + i_loja.getId() + "\n"
                + "where p.id_loja = " + i_loja.idCopiarLoja + "\n"
                + "and p.datatermino >= '" + sdf.format(date) + "'";
        return sql;
    }

    public String copiaPromocaoDesconto(LojaVO i_loja) throws Exception {
        String sql
                = "insert into promocaodesconto  (id_promocao, id_produto, desconto,qtdelimite) \n"
                + " select \n"
                + " p2.id,\n"
                + " pd.id_produto,\n"
                + " pd.desconto,\n"
                + " pd.qtdelimite\n"
                + " from promocao p\n"
                + " join promocaodesconto pd on p.id = pd.id_promocao \n"
                + " join promocao p2 on p.descricao = p2.descricao and p2.id_loja =  " + i_loja.getId() + "\n"
                + " where \n"
                + " p.id_loja = " + i_loja.idCopiarLoja + "\n"
                + " and p.datatermino >= '" + sdf.format(date) + "'";
        return sql;
    }

    public String validaPromocao(LojaVO i_loja) throws Exception {
        String sql = "select * from promocao where id_loja = " + i_loja.getId();

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

    public String insereLojaPdvHistoricoVenda(LojaVO i_loja) throws Exception {
        String sql = "INSERT INTO vrhistoricovenda.configuracaoloja(id_loja, novaconsulta, ultimaconsulta)"
                + " VALUES (" + i_loja.getId() + " , true, NULL);";

        return sql;
    }
}
