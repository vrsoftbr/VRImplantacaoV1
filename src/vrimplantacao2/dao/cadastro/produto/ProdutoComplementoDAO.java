package vrimplantacao2.dao.cadastro.produto;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import vr.core.parametro.versao.Versao;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.MathUtils;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.LogProdutoComplementoVO;
import vrimplantacao2.vo.cadastro.ProdutoComplementoVO;
import vrimplantacao2.vo.cadastro.ProdutoVO;
import vrimplantacao2.vo.cadastro.oferta.OfertaVO;
import vrimplantacao2.vo.cadastro.oferta.SituacaoOferta;

public class ProdutoComplementoDAO {

    private static final Logger LOG = Logger.getLogger(ProdutoComplementoDAO.class.getName());

    private MultiMap<Integer, Integer> complementos;
    private final Versao versao = Versao.createFromConnectionInterface(Conexao.getConexao());

    public MultiMap<Integer, Integer> getComplementos() throws Exception {
        if (complementos == null) {
            atualizarComplementos();
        }
        return complementos;
    }

    public void atualizarComplementos() throws Exception {
        complementos = new MultiMap<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	pc.id,\n"
                    + "	pc.id_loja,\n"
                    + "	pc.id_produto\n"
                    + "from\n"
                    + "	produtocomplemento pc"
            )) {
                while (rst.next()) {
                    complementos.put(rst.getInt("id"), rst.getInt("id_loja"), rst.getInt("id_produto"));
                }
            }
        }
    }

    public void salvar(ProdutoComplementoVO vo, boolean unificacao) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            if (!getComplementos().containsKey(vo.getIdLoja(), vo.getProduto().getId())) {
                SQLBuilder sql = new SQLBuilder();
                sql.setTableName("produtocomplemento");
                sql.put("id_produto", vo.getProduto().getId());
                sql.put("prateleira", vo.getPrateleira());
                sql.put("secao", vo.getSetor());
                sql.put("estoqueminimo", vo.getEstoqueMinimo());
                sql.put("estoquemaximo", vo.getEstoqueMaximo());
                sql.put("valoripi", 0);
                sql.putNull("dataultimopreco");
                sql.putNull("dataultimaentrada");
                sql.put("custosemimposto", vo.getCustoSemImposto());
                sql.put("custocomimposto", vo.getCustoComImposto());
                sql.put("custosemimpostoanterior", vo.getCustoAnteriorSemImposto());
                sql.put("custocomimpostoanterior", vo.getCustoAnteriorSemImposto());
                sql.put("precovenda", vo.getPrecoVenda());
                sql.put("precovendaanterior", 0);
                sql.put("dataprimeiraentrada", vo.getDataPrimeiraAlteracao());
                sql.put("precodiaseguinte", vo.getPrecoDiaSeguinte());
                
                if (versao.igualOuMaiorQue(4)) {
                    sql.put("margemminima", vo.getMargemMinima());
                    sql.put("margemmaxima", vo.getMargemMaxima());
                    sql.put("margem", vo.getMargem());
                }
                
                if (unificacao) {
                    sql.put("estoque", 0);
                } else {
                    sql.put("estoque", vo.getEstoque());
                }
                sql.put("troca", vo.getTroca());
                sql.put("emiteetiqueta", vo.isEmiteEtiqueta());
                sql.put("custosemperdasemimposto", 0);
                sql.put("custosemperdasemimpostoanterior", 0);
                sql.put("customediocomimposto", vo.getCustoMedioComImposto());
                sql.put("customediosemimposto", vo.getCustoMedioSemImposto());
                sql.put("id_aliquotacredito", vo.getIdAliquotaCredito());
                sql.putNull("dataultimavenda");
                sql.put("teclaassociada", vo.getTeclaassociada());
                sql.put("id_situacaocadastro", vo.getSituacaoCadastro().getId());
                sql.put("id_loja", vo.getIdLoja());
                sql.put("descontinuado", vo.isDescontinuado());
                sql.put("quantidadeultimaentrada", 0);
                sql.put("centralizado", false);
                sql.put("operacional", 0);
                sql.put("valoricmssubstituicao", 0);
                sql.putNull("dataultimaentradaanterior");
                sql.put("cestabasica", 0);
                sql.put("customediocomimpostoanterior", 0);
                sql.put("customediosemimpostoanterior", 0);
                sql.put("id_tipopiscofinscredito", vo.getProduto().getPisCofinsCredito().getId());
                sql.put("valoroutrassubstituicao", 0);
                if (versao.maiorQue(3, 17, 9)) {
                    sql.put("id_tipoproduto", vo.getTipoProduto().getId());
                    sql.put("fabricacaopropria", vo.isFabricacaoPropria());
                }
                sql.put("id_normareposicao", vo.getNormaReposicao().getId());
                if(versao.igualOuMaiorQue(4, 2,0)){
                    sql.put("validade", vo.getValidade());
                }
                sql.getReturning().add("id");

                try (ResultSet rst = stm.executeQuery(
                        sql.getInsert()
                )) {
                    if (rst.next()) {
                        vo.setId(rst.getInt("id"));
                    }
                }
                getComplementos().put(vo.getId(), vo.getIdLoja(), vo.getProduto().getId());
            }
        }
    }

    public void salvarLogCusto(LogProdutoComplementoVO vo) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();

            sql.setTableName("logcusto");

            sql.put("id_loja", vo.getIdLoja());
            sql.put("id_produto", vo.getProduto().getId());
            sql.put("custosemimposto", vo.getCustoSemImposto());
            sql.put("custosemimpostoanterior", vo.getCustoAnteriorSemImposto());
            sql.put("custocomimposto", vo.getCustoComImposto());
            sql.put("custocomimpostoanterior", vo.getCustoAnteriorSemImposto());
            sql.put("datahora", vo.getDataHora());
            sql.put("datamovimento", vo.getDataMovimento());
            sql.put("customediosemimposto", 0);
            sql.put("customediosemimpostoanterior", 0);
            sql.put("customediocomimposto", 0);
            sql.put("customediocomimpostoanterior", 0);
            sql.put("id_usuario", 0);
            sql.put("observacao", vo.getObservacao());
            sql.put("valoripi", 0);
            sql.put("valoricmssubstituicao", 0);
            sql.put("valoricms", 0);
            sql.put("valorpiscofins", 0);
            sql.put("valoracrescimo", 0);
            sql.put("valoracrescimoimposto", 0);
            sql.put("custonota", 0);
            sql.put("percentualperda", 0);
            sql.put("valordesconto", 0);
            sql.put("valordescontoimposto", 0);
            sql.put("valorbonificacao", 0);
            sql.put("valorverba", 0);
            sql.put("valoroutrassubstituicao", 0);
            sql.put("valordespesafrete", 0);
            sql.put("valorfcp", 0);
            sql.put("valorfcpsubstituicao", 0);

            stm.execute(sql.getInsert());
        }
    }

    Set<Integer> custoAjustadoPeloUsuario = null;
    Set<Integer> precoAjustadoPeloUsuario = null;

    public void atualizar(ProdutoComplementoVO complemento, Set<OpcaoProduto> opt) throws Exception {
        try (Statement stm = Conexao.createStatement()) {

            if (custoAjustadoPeloUsuario == null) {
                custoAjustadoPeloUsuario = new HashSet<>();
                try (ResultSet rst = stm.executeQuery(
                        "select distinct id_produto from logcusto where id_loja = " + complemento.getIdLoja() + " and observacao not like '%VRIMPLANTACAO%'\n"
                        + "union\n"
                        + "select distinct id_produto from notaentradaitem join notaentrada on id_notaentrada = notaentrada.id where id_loja = " + complemento.getIdLoja()
                )) {
                    while (rst.next()) {
                        custoAjustadoPeloUsuario.add(rst.getInt("id_produto"));
                    }
                }
            }
            
            if (precoAjustadoPeloUsuario == null) {
                precoAjustadoPeloUsuario = new HashSet<>();
                try (ResultSet rst = stm.executeQuery(
                        "select distinct id_produto from logpreco where id_loja = " + complemento.getIdLoja() + " and observacao not like '%VRIMPLANTACAO%'"
                )) {
                    while (rst.next()) {
                        precoAjustadoPeloUsuario.add(rst.getInt("id_produto"));
                    }
                }
            }

            SQLBuilder sql = new SQLBuilder();
            String oft = "";
            sql.setTableName("produtocomplemento");
            
            boolean atualizarPreco = !precoAjustadoPeloUsuario.contains(complemento.getProduto().getId())
                    || opt.contains(OpcaoProduto.FORCAR_ATUALIZACAO);
            if (!atualizarPreco) {
                log("PRECO ATUALIZANDO PELO USUARIO","ID:" + complemento.getProduto().getId());
            }
            if (opt.contains(OpcaoProduto.PRECO) && atualizarPreco) {
                OfertaVO oferta = getOfertas().get(complemento.getIdLoja(), complemento.getProduto().getId());
                if (oferta == null) {
                    sql.put("precovenda", complemento.getPrecoVenda());
                    sql.put("precodiaseguinte", complemento.getPrecoDiaSeguinte());
                } else {
                    sql.put("precovenda", oferta.getPrecoOferta());
                    if (oferta.getDataTermino().getTime() > new Date().getTime()) {
                        sql.put("precodiaseguinte", oferta.getPrecoOferta());
                    } else {
                        sql.put("precodiaseguinte", complemento.getPrecoDiaSeguinte());
                    }
                    oft = "update oferta set preconormal = " + MathUtils.round(complemento.getPrecoVenda(), 2) + " where id = " + oferta.getId();
                }
            }
            boolean atualizarCusto
                    = !custoAjustadoPeloUsuario.contains(complemento.getProduto().getId())
                    || opt.contains(OpcaoProduto.FORCAR_ATUALIZACAO);
            if (!atualizarCusto) {
                log("PRODUTO ATUALIZADO PELO USUARIO", "ID:" + complemento.getProduto().getId());
            }
            if (opt.contains(OpcaoProduto.CUSTO) && atualizarCusto) {
                sql.put("custocomimposto", complemento.getCustoComImposto());
                sql.put("custosemimposto", complemento.getCustoSemImposto());
                sql.put("custocomimpostoanterior", complemento.getCustoAnteriorComImposto());
                sql.put("custosemimpostoanterior", complemento.getCustoAnteriorSemImposto());
                sql.put("customediocomimposto", complemento.getCustoMedioComImposto());
                sql.put("customediosemimposto", complemento.getCustoMedioSemImposto());
                
                gerarLogCusto(complemento);
            }
            if (opt.contains(OpcaoProduto.CUSTO_COM_IMPOSTO) && atualizarCusto) {
                sql.put("custocomimposto", complemento.getCustoComImposto());
                sql.put("custocomimpostoanterior", complemento.getCustoAnteriorComImposto());
                sql.put("customediocomimposto", complemento.getCustoMedioComImposto());
                
                gerarLogCusto(complemento);
            }
            if (opt.contains(OpcaoProduto.CUSTO_SEM_IMPOSTO) && atualizarCusto) {
                sql.put("custosemimposto", complemento.getCustoSemImposto());
                sql.put("custosemimpostoanterior", complemento.getCustoAnteriorSemImposto());
                sql.put("customediosemimposto", complemento.getCustoMedioSemImposto());
                
                gerarLogCusto(complemento);
            }
            if (opt.contains(OpcaoProduto.CUSTO_ANTERIOR) && atualizarCusto) {
                sql.put("custosemimpostoanterior", complemento.getCustoAnteriorSemImposto());
                sql.put("custocomimpostoanterior", complemento.getCustoAnteriorComImposto());
            }
            if (versao.igualOuMaiorQue(4)) {
                if (opt.contains(OpcaoProduto.MARGEM)) {
                    sql.put("margem", complemento.getMargem());
                }
                if (opt.contains(OpcaoProduto.MARGEM_MAXIMA)) {
                    sql.put("margemmaxima", complemento.getMargemMaxima());
                }
                if (opt.contains(OpcaoProduto.MARGEM_MINIMA)) {
                    sql.put("margemminima", complemento.getMargemMinima());
                }   
            }
            if (opt.contains(OpcaoProduto.ESTOQUE)) {
                if (opt.contains(OpcaoProduto.ATUALIZAR_SOMAR_ESTOQUE)) {
                    sql.putSql("estoque", String.format(Locale.US, "estoque + (%.2f)", complemento.getEstoque()));
                } else {
                    sql.put("estoque", complemento.getEstoque());
                }
            }
            if (opt.contains(OpcaoProduto.TROCA)) {
                if (opt.contains(OpcaoProduto.ATUALIZAR_SOMAR_ESTOQUE)) {
                    sql.putSql("troca", String.format(Locale.US, "troca + (%.2f)", complemento.getTroca()));
                } else {
                    sql.put("troca", complemento.getTroca());
                }                
            }
            if (opt.contains(OpcaoProduto.ESTOQUE_MINIMO)) {
                sql.put("estoqueminimo", complemento.getEstoqueMinimo());
            }
            if (opt.contains(OpcaoProduto.ESTOQUE_MAXIMO)) {
                sql.put("estoquemaximo", complemento.getEstoqueMaximo());
            }
            if (opt.contains(OpcaoProduto.ATIVO)) {
                sql.put("id_situacaocadastro", complemento.getSituacaoCadastro().getId());
            }
            if (opt.contains(OpcaoProduto.DESCONTINUADO)) {
                sql.put("descontinuado", complemento.isDescontinuado());
            }
            if (opt.contains(OpcaoProduto.TIPO_PRODUTO)) {
                sql.put("id_tipoproduto", complemento.getTipoProduto().getId());
            }
            if (opt.contains(OpcaoProduto.TIPO_ATACADO)) {
                iniciaTipoAtacado(complemento);
                sql.put("id_tipodescontoatacado", complemento.getTipoAtacado().getId());
            }
            if (opt.contains(OpcaoProduto.FABRICACAO_PROPRIA)) {
                sql.put("fabricacaopropria", complemento.isFabricacaoPropria());
            }
            if (opt.contains(OpcaoProduto.EMITE_ETIQUETA)) {
                sql.put("emiteetiqueta", complemento.isEmiteEtiqueta());
            }
            if (opt.contains(OpcaoProduto.ICMS)) {
                sql.put("id_aliquotacredito", complemento.getIdAliquotaCredito());
            }
            if (opt.contains(OpcaoProduto.NORMA_REPOSICAO)) {
                sql.put("id_normareposicao", complemento.getNormaReposicao().getId());
            }
            if (opt.contains(OpcaoProduto.SECAO)) {
                sql.put("secao", complemento.getSetor());
            }
            if (opt.contains(OpcaoProduto.PRATELEIRA)) {
                sql.put("prateleira", complemento.getPrateleira());
            }
            if (opt.contains(OpcaoProduto.TECLA_ASSOCIADA)) {
                sql.put("teclaassociada", complemento.getTeclaassociada());
            }
            if(opt.contains(OpcaoProduto.VALIDADE) && versao.igualOuMaiorQue(4,2,2)){
                sql.put("validade", complemento.getValidade());
            }
            if ((opt.contains(OpcaoProduto.CUSTO_COM_IMPOSTO))
                    || (opt.contains(OpcaoProduto.CUSTO_SEM_IMPOSTO))
                    || (opt.contains(OpcaoProduto.CUSTO))) {
                sql.setWhere(
                        "id_produto = " + complemento.getProduto().getId() + " and "
                        + "id_loja = " + complemento.getIdLoja()
                );
            } else {
                sql.setWhere(
                        "id_produto = " + complemento.getProduto().getId() + " and "
                        + "id_loja = " + complemento.getIdLoja()
                );
            }
            if (!sql.isEmpty()) {
                String sq = sql.getUpdate();
                LOG.finer(sq);
                stm.execute(sq);
                if (!"".equals(oft)) {
                    stm.execute(oft);
                }
            }
        }
    }

    private void gerarLogCusto(ProdutoComplementoVO complemento) throws Exception {
        ProdutoComplementoVO custoAnt
                = getCustoProduto(complemento.getIdLoja(), complemento.getProduto().getId());
        LogProdutoComplementoVO logCusto = new LogProdutoComplementoVO();

        if (custoAnt != null) {
            logCusto.setIdLoja(complemento.getIdLoja());
            logCusto.setProduto(complemento.getProduto());
            logCusto.setCustoAnteriorComImposto(custoAnt.getCustoComImposto());
            logCusto.setCustoAnteriorSemImposto(custoAnt.getCustoSemImposto());
            logCusto.setCustoComImposto(complemento.getCustoComImposto());
            logCusto.setCustoSemImposto(complemento.getCustoSemImposto());
            logCusto.setObservacao("ALTERADO VRIMPLANTACAO");
            logCusto.setDataMovimento(custoAnt.getDataMovimento());
            logCusto.setDataHora(custoAnt.getDataHora());

            salvarLogCusto(logCusto);
        }
    }

    private MultiMap<Integer, OfertaVO> ofertas;

    private MultiMap<Integer, OfertaVO> getOfertas() throws Exception {
        if (ofertas == null) {
            atualizaOfertas();
        }
        return ofertas;
    }

    private void atualizaOfertas() throws Exception {
        ofertas = new MultiMap<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	o.id, \n"
                    + "	o.id_loja, \n"
                    + "	o.id_produto, \n"
                    + "	o.datainicio, \n"
                    + "	o.datatermino, \n"
                    + "	o.precooferta, \n"
                    + "	o.preconormal, \n"
                    + "	o.id_situacaooferta,\n"
                    + "	p.descricaocompleta,\n"
                    + "	p.descricaoreduzida,\n"
                    + "	p.descricaogondola,\n"
                    + "	o.id_situacaooferta\n"
                    + "from \n"
                    + "	oferta o\n"
                    + "	join produto p on o.id_produto = p.id\n"
                    + "where\n"
                    + "	o.datainicio <= now()::date and\n"
                    + "	o.datatermino >= now()::date and\n"
                    + "	o.id_situacaooferta = 1\n"
                    + "order by\n"
                    + "	o.id_loja,\n"
                    + "	o.id_produto"
            )) {
                while (rst.next()) {
                    OfertaVO oferta = new OfertaVO();
                    oferta.setId(rst.getInt("id"));
                    oferta.setIdLoja(rst.getInt("id_loja"));
                    ProdutoVO prod = new ProdutoVO();
                    prod.setId(rst.getInt("id_produto"));
                    prod.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    prod.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    prod.setDescricaoGondola(rst.getString("descricaogondola"));
                    oferta.setProduto(prod);
                    oferta.setDataInicio(rst.getDate("datainicio"));
                    oferta.setDataTermino(rst.getDate("datatermino"));
                    oferta.setPrecoOferta(rst.getDouble("precooferta"));
                    oferta.setPrecoNormal(rst.getDouble("preconormal"));
                    oferta.setSituacaoOferta(SituacaoOferta.getById(rst.getInt("id_situacaooferta")));

                    ofertas.put(oferta, oferta.getIdLoja(), oferta.getProduto().getId());
                }
            }
        }
    }

    public void copiarProdutoComplemento(int lojaModelo, int lojaNova) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(
                    "insert into produtocomplemento (\n"
                    + "	id_produto, \n"
                    + "	prateleira, \n"
                    + "	secao, \n"
                    + "	estoqueminimo, \n"
                    + "	estoquemaximo, \n"
                    + "	valoripi, \n"
                    + "	dataultimopreco, \n"
                    + "	dataultimaentrada, \n"
                    + "	custosemimposto, \n"
                    + "	custocomimposto, \n"
                    + "	custosemimpostoanterior, \n"
                    + "	custocomimpostoanterior, \n"
                    + "	precovenda, \n"
                    + "	precovendaanterior, \n"
                    + "	precodiaseguinte, estoque, troca, \n"
                    + "	emiteetiqueta, \n"
                    + "	custosemperdasemimposto, \n"
                    + "	custosemperdasemimpostoanterior, \n"
                    + "	customediocomimposto, \n"
                    + "	customediosemimposto, \n"
                    + "	id_aliquotacredito, \n"
                    + "	dataultimavenda, \n"
                    + "	teclaassociada, \n"
                    + "	id_situacaocadastro, \n"
                    + "	id_loja,\n"
                    + "	descontinuado, \n"
                    + "	quantidadeultimaentrada, \n"
                    + "	centralizado, \n"
                    + "	operacional, \n"
                    + "	valoricmssubstituicao, \n"
                    + "	dataultimaentradaanterior, \n"
                    + "	cestabasica, \n"
                    + "	customediocomimpostoanterior, \n"
                    + "	customediosemimpostoanterior, \n"
                    + "	id_tipopiscofinscredito, \n"
                    + "	valoroutrassubstituicao,\n"
                    + "	id_normareposicao)\n"
                    + "SELECT\n"
                    + "	id_produto, \n"
                    + "	prateleira, \n"
                    + "	secao, \n"
                    + "	estoqueminimo, \n"
                    + "	estoquemaximo, \n"
                    + "	valoripi, \n"
                    + "	dataultimopreco, \n"
                    + "	dataultimaentrada, \n"
                    + "	custosemimposto, \n"
                    + "	custocomimposto, \n"
                    + "	custosemimpostoanterior, \n"
                    + "	custocomimpostoanterior, \n"
                    + "	precovenda, \n"
                    + "	precovendaanterior, \n"
                    + "	precodiaseguinte, 0 estoque, troca, \n"
                    + "	emiteetiqueta, \n"
                    + "	custosemperdasemimposto, \n"
                    + "	custosemperdasemimpostoanterior, \n"
                    + "	customediocomimposto, \n"
                    + "	customediosemimposto, \n"
                    + "	id_aliquotacredito, \n"
                    + "	dataultimavenda, \n"
                    + "	teclaassociada, \n"
                    + "	id_situacaocadastro, \n"
                    + "	" + lojaNova + " id_loja,\n"
                    + "	descontinuado, \n"
                    + "	quantidadeultimaentrada, \n"
                    + "	centralizado, \n"
                    + "	operacional, \n"
                    + "	valoricmssubstituicao, \n"
                    + "	dataultimaentradaanterior, \n"
                    + "	cestabasica, \n"
                    + "	customediocomimpostoanterior, \n"
                    + "	customediosemimpostoanterior, \n"
                    + "	id_tipopiscofinscredito, \n"
                    + "	valoroutrassubstituicao,\n"
                    + "	id_normareposicao\n"
                    + "FROM 	\n"
                    + "	produtocomplemento pc\n"
                    + "where\n"
                    + "	id_loja = " + lojaModelo + " and\n"
                    + "	not id_produto in (select id_produto from produtocomplemento where id_loja = " + lojaNova + ");"
            );
        }
    }

    public void criarEstoqueAnteriorTemporario(int lojaVR) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(
                    "create temp table tmp_estoque \n"
                    + "on commit drop \n"
                    + "as \n"
                    + "select \n"
                    + "	id, \n"
                    + "	id_produto, \n"
                    + "	estoque \n"
                    + "from\n"
                    + "	produtocomplemento\n"
                    + "where\n"
                    + "	id_loja = " + lojaVR + "\n"
                    + "order by\n"
                    + "	id"
            );
        }
    }

    public void gerarLogDeEstoqueViaTMP_ESTOQUE(int lojaVR) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(
                    "insert into logestoque (\n"
                    + "	id_loja, \n"
                    + "	id_produto, \n"
                    + "	quantidade, \n"
                    + "	id_tipomovimentacao, \n"
                    + "	datahora, \n"
                    + "	id_usuario, \n"
                    + "	observacao, \n"
                    + "	estoqueanterior,\n"
                    + "	estoqueatual, \n"
                    + "	id_tipoentradasaida, \n"
                    + "	custosemimposto, \n"
                    + "	custocomimposto, \n"
                    + "	datamovimento, \n"
                    + "	customediocomimposto, \n"
                    + "	customediosemimposto\n"
                    + ")\n"
                    + "select\n"
                    + "	pc.id_loja,\n"
                    + "	pc.id_produto,\n"
                    + "	(pc.estoque - coalesce(l.estoque, pc.estoque)) * (case when coalesce(l.estoque,pc.estoque) > pc.estoque then -1 else 1 end) quantidade,\n"
                    + "	1 id_tipomovimentacao,\n"
                    + "	current_timestamp datahora, \n"
                    + "	0 id_usuario, \n"
                    + "	'IMPORTACAO (VRIMPLANTACAO)' observacao,\n"
                    + "	coalesce(l.estoque, pc.estoque) estoqueanterior,\n"
                    + "	pc.estoque estoqueatual, \n"
                    + "	case \n"
                    + "	when coalesce(l.estoque, pc.estoque) < pc.estoque then 0 \n"
                    + "	when coalesce(l.estoque, pc.estoque) = pc.estoque then 2\n"
                    + "	else 1 end id_tipoentradasaida, \n"
                    + "	pc.custosemimposto, \n"
                    + "	pc.custocomimposto, \n"
                    + "	current_timestamp datamovimento, \n"
                    + "	pc.customediocomimposto, \n"
                    + "	pc.customediosemimposto \n"
                    + "from\n"
                    + "	produtocomplemento pc\n"
                    + "	left join tmp_estoque l on\n"
                    + "		pc.id_produto = l.id_produto\n"
                    + "where\n"
                    + "	pc.id_loja = " + lojaVR + "\n"
                    + "order by\n"
                    + "	id_produto"
            );
        }
    }

    public Map<Integer, Double> getCustoProduto(int idLojaVR) throws Exception {
        Map<Integer, Double> result = new HashMap<>();

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select id_produto, custocomimposto from produtocomplemento where id_loja = " + idLojaVR
            )) {
                while (rst.next()) {
                    result.put(rst.getInt("id_produto"), rst.getDouble("custocomimposto"));
                }
            }
        }

        return result;
    }

    public ProdutoComplementoVO getCustoProduto(int idLojaVR, int idProduto) throws Exception {
        ProdutoComplementoVO vo = null;
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	id_produto,\n"
                    + "	id_loja,\n"
                    + "	custosemimposto,\n"
                    + "	custocomimposto,\n"
                    + " current_date datamovimento,\n"
                    + " current_timestamp datahora\n"        
                    + "from\n"
                    + "	produtocomplemento p\n"
                    + "where\n"
                    + "	id_loja = " + idLojaVR + " and \n"
                    + "	id_produto = " + idProduto
            )) {
                while (rst.next()) {
                    vo = new ProdutoComplementoVO();
                    ProdutoVO prod = new ProdutoVO();

                    prod.setId(rst.getInt("id_produto"));
                    vo.setProduto(prod);
                    vo.setIdLoja(rst.getInt("id_loja"));
                    vo.setCustoComImposto(rst.getDouble("custocomimposto"));
                    vo.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    vo.setDataMovimento(rst.getDate("datamovimento"));
                    vo.setDataHora(rst.getTimestamp("datahora"));
                }
            }
        }

        return vo;
    }

    boolean logCriado = false;

    private void log(String titulo, String info) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            if (!logCriado) {
                stm.execute(
                        "create table if not exists implantacao.log_produtocomplemento (\n"
                        + "   id serial not null primary key,\n"
                        + "   data timestamp not null default current_timestamp,\n"
                        + "   titulo varchar not null,\n"
                        + "   info varchar\n"
                        + ");"
                );
                logCriado = true;
            }
            SQLBuilder sql = new SQLBuilder();
            sql.setSchema("implantacao");
            sql.setTableName("log_produtocomplemento");
            sql.put("titulo", titulo);
            sql.put("info", info);
            stm.execute(sql.getInsert());
        }
    }
    
    private void iniciaTipoAtacado(ProdutoComplementoVO vo) throws Exception {
        try(Statement stm = Conexao.createStatement()) {
            stm.execute("update produtocomplemento "
                    + "set id_tipodescontoatacado = null "
                    + "where id_produto = " + vo.getProduto().getId() + ""
                    + " and id_loja = " + vo.getIdLoja());
        }
    }

    public void criarEstoqueTrocaAnteriorTemporario(int lojaVR) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(
                    "create temp table tmp_troca \n"
                    + "on commit drop \n"
                    + "as \n"
                    + "select \n"
                    + "	id, \n"
                    + "	id_produto, \n"
                    + "	troca \n"
                    + "from\n"
                    + "	produtocomplemento\n"
                    + "where\n"
                    + "	id_loja = " + lojaVR + "\n"
                    + "order by\n"
                    + "	id"
            );
        }
    }

    public void gerarLogDeTrocaViaTMP_TROCA(int lojaVR) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(
                    "insert into logtroca (\n" +
                    "	id_loja,\n" +
                    "	id_produto,\n" +
                    "	quantidade,\n" +
                    "	datahora,\n" +
                    "	id_usuario,\n" +
                    "	estoqueanterior,\n" +
                    "	estoqueatual,\n" +
                    "	id_tipoentradasaida,\n" +
                    "	datamovimento,\n" +
                    "	id_motivotroca,\n" +
                    "	observacaotroca\n" +
                    ")\n" +
                    "select\n" +
                    "	pc.id_loja,\n" +
                    "	pc.id_produto,\n" +
                    "	(pc.troca - coalesce(l.troca, pc.troca)) * (case when coalesce(l.troca,pc.troca) > pc.troca then -1 else 1 end) quantidade,\n" +
                    "	current_timestamp datahora, \n" +
                    "	0 id_usuario, \n" +
                    "	coalesce(l.troca, pc.troca) estoqueanterior,\n" +
                    "	pc.troca estoqueatual, \n" +
                    "	case \n" +
                    "	when coalesce(l.troca, pc.troca) < pc.troca then 0 \n" +
                    "	when coalesce(l.troca, pc.troca) = pc.troca then 2\n" +
                    "	else 1 end id_tipoentradasaida, \n" +
                    "	current_timestamp datamovimento, \n" +
                    "	(select id from tipomotivotroca t where descricao like '%IMPORTA%' and id_situacaocadastro = 1 limit 1) id_tipotroca,\n" +
                    "	'IMPORTACAO (VRIMPLANTACAO)' observacaotroca\n" +
                    "from\n" +
                    "	produtocomplemento pc\n" +
                    "	left join tmp_troca l on\n" +
                    "		pc.id_produto = l.id_produto\n" +
                    "where\n" +
                    "	pc.id_loja = " + lojaVR + "\n" +
                    "	and coalesce(l.troca, pc.troca) != pc.troca\n" +
                    "order by\n" +
                    "	id_produto;"
            );
        }
    }
}
