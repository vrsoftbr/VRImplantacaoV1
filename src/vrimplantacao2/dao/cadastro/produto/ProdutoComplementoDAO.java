package vrimplantacao2.dao.cadastro.produto;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Logger;
import vrframework.classe.Conexao;
import vrimplantacao2.parametro.Versao;
import vrimplantacao2.utils.MathUtils;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.ProdutoComplementoVO;
import vrimplantacao2.vo.cadastro.ProdutoVO;
import vrimplantacao2.vo.cadastro.oferta.OfertaVO;
import vrimplantacao2.vo.cadastro.oferta.SituacaoOferta;

public class ProdutoComplementoDAO {
    
    private static final Logger LOG = Logger.getLogger(ProdutoComplementoDAO.class.getName());
    
    private MultiMap<Integer, Integer> complementos;
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
                    "select\n" +
                    "	pc.id,\n" +
                    "	pc.id_loja,\n" +
                    "	pc.id_produto\n" +
                    "from\n" +
                    "	produtocomplemento pc"
            )) {
                while (rst.next()) {
                    complementos.put(rst.getInt("id"), rst.getInt("id_loja"), rst.getInt("id_produto"));
                }
            }
        }
    }

    public void salvar(Collection<ProdutoComplementoVO> values, boolean unificacao) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            for (ProdutoComplementoVO vo: values) {
                if (!getComplementos().containsKey(vo.getIdLoja(), vo.getProduto().getId())) {
                    SQLBuilder sql = new SQLBuilder();
                    sql.setTableName("produtocomplemento");
                    sql.put("id_produto", vo.getProduto().getId());
                    sql.put("prateleira", "");
                    sql.put("secao", "");
                    sql.put("estoqueminimo", vo.getEstoqueMinimo());
                    sql.put("estoquemaximo", vo.getEstoqueMaximo());
                    sql.put("valoripi", 0);
                    sql.putNull("dataultimopreco");
                    sql.putNull("dataultimaentrada");
                    sql.put("custosemimposto", vo.getCustoSemImposto());
                    sql.put("custocomimposto", vo.getCustoComImposto());
                    sql.put("custosemimpostoanterior", 0);
                    sql.put("custocomimpostoanterior", 0);
                    sql.put("precovenda", vo.getPrecoVenda());
                    sql.put("precovendaanterior", 0);
                    sql.put("precodiaseguinte", vo.getPrecoDiaSeguinte());
                    if (unificacao) {
                        sql.put("estoque", 0);
                    } else {
                        sql.put("estoque", vo.getEstoque());
                    }
                    sql.put("troca", vo.getTroca());
                    sql.put("emiteetiqueta", vo.isEmiteEtiqueta());
                    sql.put("custosemperdasemimposto", 0);
                    sql.put("custosemperdasemimpostoanterior", 0);
                    sql.put("customediocomimposto", 0);
                    sql.put("customediosemimposto", 0);
                    sql.put("id_aliquotacredito", vo.getIdAliquotaCredito());
                    sql.putNull("dataultimavenda");
                    sql.put("teclaassociada", 0);
                    sql.put("id_situacaocadastro", vo.getSituacaoCadastro().getId());
                    sql.put("id_loja", vo.getIdLoja());
                    sql.put("descontinuado", false);
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
                    if (Versao.maiorQue(3,17,9)) {
                        sql.put("id_tipoproduto", vo.getTipoProduto().getId());
                        sql.put("fabricacaopropria", vo.isFabricacaoPropria());
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
    }
    
    public void salvar(ProdutoComplementoVO vo, boolean unificacao) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            if (!getComplementos().containsKey(vo.getIdLoja(), vo.getProduto().getId())) {
                SQLBuilder sql = new SQLBuilder();
                sql.setTableName("produtocomplemento");
                sql.put("id_produto", vo.getProduto().getId());
                sql.put("prateleira", "");
                sql.put("secao", "");
                sql.put("estoqueminimo", vo.getEstoqueMinimo());
                sql.put("estoquemaximo", vo.getEstoqueMaximo());
                sql.put("valoripi", 0);
                sql.putNull("dataultimopreco");
                sql.putNull("dataultimaentrada");
                sql.put("custosemimposto", vo.getCustoSemImposto());
                sql.put("custocomimposto", vo.getCustoComImposto());
                sql.put("custosemimpostoanterior", 0);
                sql.put("custocomimpostoanterior", 0);
                sql.put("precovenda", vo.getPrecoVenda());
                sql.put("precovendaanterior", 0);
                sql.put("precodiaseguinte", vo.getPrecoDiaSeguinte());
                if (unificacao) {
                    sql.put("estoque", 0);
                } else {
                    sql.put("estoque", vo.getEstoque());
                }
                sql.put("troca", vo.getTroca());
                sql.put("emiteetiqueta", vo.isEmiteEtiqueta());
                sql.put("custosemperdasemimposto", 0);
                sql.put("custosemperdasemimpostoanterior", 0);
                sql.put("customediocomimposto", 0);
                sql.put("customediosemimposto", 0);
                sql.put("id_aliquotacredito", vo.getIdAliquotaCredito());
                sql.putNull("dataultimavenda");
                sql.put("teclaassociada", 0);
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
                if (Versao.maiorQue(3,17,9)) {
                    sql.put("id_tipoproduto", vo.getTipoProduto().getId());
                    sql.put("fabricacaopropria", vo.isFabricacaoPropria());
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

    public void atualizar(Collection<ProdutoComplementoVO> complementos, OpcaoProduto... opcoes) throws Exception {
        Set<OpcaoProduto> opt = new LinkedHashSet<>(Arrays.asList(opcoes));
        String oft = "";
        try (Statement stm = Conexao.createStatement()) {
            for (ProdutoComplementoVO vo: complementos) {
                SQLBuilder sql = new SQLBuilder();
                sql.setTableName("produtocomplemento");
                if (opt.contains(OpcaoProduto.PRECO)) {
                    OfertaVO oferta = getOfertas().get(vo.getIdLoja(), vo.getProduto().getId());
                    if (oferta == null) {
                        sql.put("precovenda", vo.getPrecoVenda());
                        sql.put("precodiaseguinte", vo.getPrecoDiaSeguinte());
                    } else {
                        sql.put("precovenda", oferta.getPrecoOferta());
                        if (oferta.getDataTermino().getTime() > new Date().getTime()) {
                            sql.put("precodiaseguinte", oferta.getPrecoOferta());
                        } else {
                            sql.put("precodiaseguinte", vo.getPrecoDiaSeguinte());
                        }                        
                        oft = "update oferta set preconormal = " + MathUtils.round(vo.getPrecoVenda(), 2) + " where id = " + oferta.getId();
                    }
                }
                if (opt.contains(OpcaoProduto.TIPO_PRODUTO)) {
                    sql.put("id_tipoproduto", vo.getTipoProduto().getId());
                }
                if (opt.contains(OpcaoProduto.FABRICACAO_PROPRIA)) {
                    sql.put("fabricacaopropria", vo.isFabricacaoPropria());
                }                
                if (opt.contains(OpcaoProduto.EMITE_ETIQUETA)) {
                    sql.put("emiteetiqueta", vo.isEmiteEtiqueta());
                }
                if (opt.contains(OpcaoProduto.CUSTO)) {
                    sql.put("custocomimposto", vo.getCustoComImposto());
                    sql.put("custosemimposto", vo.getCustoSemImposto());
                }
                if (opt.contains(OpcaoProduto.ESTOQUE)) {
                    sql.put("estoque", vo.getEstoque());
                }
                if (opt.contains(OpcaoProduto.TROCA)) {
                    sql.put("troca", vo.getTroca());
                }
                if (opt.contains(OpcaoProduto.ATIVO)) {
                    sql.put("id_situacaocadastro", vo.getSituacaoCadastro().getId());
                }
                if (opt.contains(OpcaoProduto.ICMS)) {
                    sql.put("id_aliquotacredito", vo.getIdAliquotaCredito());
                }
                if (opt.contains(OpcaoProduto.CUSTO)) {
                    sql.setWhere(
                            "id_produto = " + vo.getProduto().getId() + " and "
                            + "id_loja = " + vo.getIdLoja() + " and "
                            + "dataultimaentrada is null"
                    );
                } else {
                    sql.setWhere(
                            "id_produto = " + vo.getProduto().getId() + " and "
                            + "id_loja = " + vo.getIdLoja()
                    );
                }
                if (!sql.isEmpty()) {
                    stm.execute(sql.getUpdate());
                    if (!"".equals(oft)) {
                        stm.execute(oft);
                    }                    
                }
            }
        }
    }
    
    public void atualizar(ProdutoComplementoVO complemento, Set<OpcaoProduto> opt) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            String oft = "";
            sql.setTableName("produtocomplemento");
            if (opt.contains(OpcaoProduto.PRECO)) {
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
            if (opt.contains(OpcaoProduto.CUSTO)) {
                sql.put("custocomimposto", complemento.getCustoComImposto());
                sql.put("custosemimposto", complemento.getCustoSemImposto());
            }
            if (opt.contains(OpcaoProduto.CUSTO_COM_IMPOSTO)) {
                sql.put("custocomimposto", complemento.getCustoComImposto());
            }
            if (opt.contains(OpcaoProduto.CUSTO_SEM_IMPOSTO)) {
                sql.put("custosemimposto", complemento.getCustoSemImposto());
            }
            if (opt.contains(OpcaoProduto.ESTOQUE)) {
                if (opt.contains(OpcaoProduto.ATUALIZAR_SOMAR_ESTOQUE)) {
                    sql.putSql("estoque", String.format(Locale.US, "estoque + (%.2f)", complemento.getEstoque()));
                } else {
                    sql.put("estoque", complemento.getEstoque());
                }
            }
            if (opt.contains(OpcaoProduto.TROCA)) {
                sql.put("troca", complemento.getTroca());
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
            if (opt.contains(OpcaoProduto.FABRICACAO_PROPRIA)) {
                sql.put("fabricacaopropria", complemento.isFabricacaoPropria());
            }
            if (opt.contains(OpcaoProduto.EMITE_ETIQUETA)) {
                sql.put("emiteetiqueta", complemento.isEmiteEtiqueta());
            }
            if (opt.contains(OpcaoProduto.ICMS)) {
                sql.put("id_aliquotacredito", complemento.getIdAliquotaCredito());
            }
            if ((opt.contains(OpcaoProduto.CUSTO_COM_IMPOSTO))
                    || (opt.contains(OpcaoProduto.CUSTO_SEM_IMPOSTO))
                    || (opt.contains(OpcaoProduto.CUSTO))) {
                sql.setWhere(
                        "id_produto = " + complemento.getProduto().getId() + " and "
                        + "id_loja = " + complemento.getIdLoja() + " and "
                        + "dataultimaentrada is null"
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
                "select \n" +
                "	o.id, \n" +
                "	o.id_loja, \n" +
                "	o.id_produto, \n" +
                "	o.datainicio, \n" +
                "	o.datatermino, \n" +
                "	o.precooferta, \n" +
                "	o.preconormal, \n" +
                "	o.id_situacaooferta,\n" +
                "	p.descricaocompleta,\n" +
                "	p.descricaoreduzida,\n" +
                "	p.descricaogondola,\n" +
                "	o.id_situacaooferta\n" +
                "from \n" +
                "	oferta o\n" +
                "	join produto p on o.id_produto = p.id\n" +
                "where\n" +
                "	o.datainicio <= now()::date and\n" +
                "	o.datatermino >= now()::date and\n" +
                "	o.id_situacaooferta = 1\n" +
                "order by\n" +
                "	o.id_loja,\n" +
                "	o.id_produto"
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
                "insert into produtocomplemento (\n" +
                "	id_produto, \n" +
                "	prateleira, \n" +
                "	secao, \n" +
                "	estoqueminimo, \n" +
                "	estoquemaximo, \n" +
                "	valoripi, \n" +
                "	dataultimopreco, \n" +
                "	dataultimaentrada, \n" +
                "	custosemimposto, \n" +
                "	custocomimposto, \n" +
                "	custosemimpostoanterior, \n" +
                "	custocomimpostoanterior, \n" +
                "	precovenda, \n" +
                "	precovendaanterior, \n" +
                "	precodiaseguinte, estoque, troca, \n" +
                "	emiteetiqueta, \n" +
                "	custosemperdasemimposto, \n" +
                "	custosemperdasemimpostoanterior, \n" +
                "	customediocomimposto, \n" +
                "	customediosemimposto, \n" +
                "	id_aliquotacredito, \n" +
                "	dataultimavenda, \n" +
                "	teclaassociada, \n" +
                "	id_situacaocadastro, \n" +
                "	id_loja,\n" +
                "	descontinuado, \n" +
                "	quantidadeultimaentrada, \n" +
                "	centralizado, \n" +
                "	operacional, \n" +
                "	valoricmssubstituicao, \n" +
                "	dataultimaentradaanterior, \n" +
                "	cestabasica, \n" +
                "	customediocomimpostoanterior, \n" +
                "	customediosemimpostoanterior, \n" +
                "	id_tipopiscofinscredito, \n" +
                "	valoroutrassubstituicao)\n" +
                "SELECT\n" +
                "	id_produto, \n" +
                "	prateleira, \n" +
                "	secao, \n" +
                "	estoqueminimo, \n" +
                "	estoquemaximo, \n" +
                "	valoripi, \n" +
                "	dataultimopreco, \n" +
                "	dataultimaentrada, \n" +
                "	custosemimposto, \n" +
                "	custocomimposto, \n" +
                "	custosemimpostoanterior, \n" +
                "	custocomimpostoanterior, \n" +
                "	precovenda, \n" +
                "	precovendaanterior, \n" +
                "	precodiaseguinte, 0 estoque, troca, \n" +
                "	emiteetiqueta, \n" +
                "	custosemperdasemimposto, \n" +
                "	custosemperdasemimpostoanterior, \n" +
                "	customediocomimposto, \n" +
                "	customediosemimposto, \n" +
                "	id_aliquotacredito, \n" +
                "	dataultimavenda, \n" +
                "	teclaassociada, \n" +
                "	id_situacaocadastro, \n" +
                "	" + lojaNova + " id_loja,\n" +
                "	descontinuado, \n" +
                "	quantidadeultimaentrada, \n" +
                "	centralizado, \n" +
                "	operacional, \n" +
                "	valoricmssubstituicao, \n" +
                "	dataultimaentradaanterior, \n" +
                "	cestabasica, \n" +
                "	customediocomimpostoanterior, \n" +
                "	customediosemimpostoanterior, \n" +
                "	id_tipopiscofinscredito, \n" +
                "	valoroutrassubstituicao\n" +
                "FROM 	\n" +
                "	produtocomplemento pc\n" +
                "where\n" +
                "	id_loja = " + lojaModelo + " and\n" +
                "	not id_produto in (select id_produto from produtocomplemento where id_loja = " + lojaNova + ");"
            );
        }
    }

    public void criarEstoqueAnteriorTemporario(int lojaVR) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(
                    "create temp table tmp_estoque \n" +
                    "on commit drop \n" +
                    "as \n" +
                    "select \n" +
                    "	id, \n" +
                    "	id_produto, \n" +
                    "	estoque \n" +
                    "from\n" +
                    "	produtocomplemento\n" +
                    "where\n" +
                    "	id_loja = " + lojaVR + "\n" +
                    "order by\n" +
                    "	id"
            );
        }
    }

    public void gerarLogDeEstoqueViaTMP_ESTOQUE(int lojaVR) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(
                    "insert into logestoque (\n" +
                    "	id_loja, \n" +
                    "	id_produto, \n" +
                    "	quantidade, \n" +
                    "	id_tipomovimentacao, \n" +
                    "	datahora, \n" +
                    "	id_usuario, \n" +
                    "	observacao, \n" +
                    "	estoqueanterior,\n" +
                    "	estoqueatual, \n" +
                    "	id_tipoentradasaida, \n" +
                    "	custosemimposto, \n" +
                    "	custocomimposto, \n" +
                    "	datamovimento, \n" +
                    "	customediocomimposto, \n" +
                    "	customediosemimposto\n" +
                    ")\n" +                    
                    "select\n" +
                    "	pc.id_loja,\n" +
                    "	pc.id_produto,\n" +
                    "	(pc.estoque - coalesce(l.estoque, pc.estoque)) * (case when coalesce(l.estoque,pc.estoque) > pc.estoque then -1 else 1 end) quantidade,\n" +
                    "	1 id_tipomovimentacao,\n" +
                    "	current_timestamp datahora, \n" +
                    "	0 id_usuario, \n" +
                    "	'IMPORTACAO (VRIMPLANTACAO)' observacao,\n" +
                    "	coalesce(l.estoque, pc.estoque) estoqueanterior,\n" +
                    "	pc.estoque estoqueatual, \n" +
                    "	case \n" +
                    "	when coalesce(l.estoque, pc.estoque) < pc.estoque then 0 \n" +
                    "	when coalesce(l.estoque, pc.estoque) = pc.estoque then 2\n" +
                    "	else 1 end id_tipoentradasaida, \n" +
                    "	pc.custosemimposto, \n" +
                    "	pc.custocomimposto, \n" +
                    "	current_timestamp datamovimento, \n" +
                    "	pc.customediocomimposto, \n" +
                    "	pc.customediosemimposto \n" +
                    "from\n" +
                    "	produtocomplemento pc\n" +
                    "	left join tmp_estoque l on\n" +
                    "		pc.id_produto = l.id_produto\n" +
                    "where\n" +
                    "	pc.id_loja = " + lojaVR + "\n" +
                    "order by\n" +
                    "	id_produto"
            );
        }
    }
}
