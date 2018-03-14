package vrimplantacao2.dao.cadastro.produto;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;
import vrframework.classe.Conexao;
import vrimplantacao2.vo.cadastro.oferta.OfertaVO;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.ProdutoComplementoVO;
import vrimplantacao2.vo.cadastro.ProdutoVO;
import vrimplantacao2.vo.cadastro.oferta.SituacaoOferta;

public class ProdutoComplementoDAO {
    
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
                    sql.put("troca", 0);
                    sql.put("emiteetiqueta", true);
                    sql.put("custosemperdasemimposto", 0);
                    sql.put("custosemperdasemimpostoanterior", 0);
                    sql.put("customediocomimposto", 0);
                    sql.put("customediosemimposto", 0);
                    sql.put("id_aliquotacredito", 0);
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
                sql.put("troca", 0);
                sql.put("emiteetiqueta", true);
                sql.put("custosemperdasemimposto", 0);
                sql.put("custosemperdasemimpostoanterior", 0);
                sql.put("customediocomimposto", 0);
                sql.put("customediosemimposto", 0);
                sql.put("id_aliquotacredito", 0);
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
                    }
                }
                if (opt.contains(OpcaoProduto.CUSTO)) {
                    sql.put("custocomimposto", vo.getCustoComImposto());
                    sql.put("custosemimposto", vo.getCustoSemImposto());
                }
                if (opt.contains(OpcaoProduto.ESTOQUE)) {
                    sql.put("estoque", vo.getEstoque());
                }
                if (opt.contains(OpcaoProduto.ATIVO)) {
                    sql.put("id_situacaocadastro", vo.getSituacaoCadastro().getId());
                }
                sql.setWhere(
                        "id_produto = " + vo.getProduto().getId() + " and " +
                        "id_loja = " + vo.getIdLoja()
                );
                if (!sql.isEmpty()) {
                    stm.execute(sql.getUpdate());
                }
            }
        }
    }
    
    public void atualizar(ProdutoComplementoVO complemento, Set<OpcaoProduto> opt) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
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
                sql.put("estoque", complemento.getEstoque());
            }
            if (opt.contains(OpcaoProduto.ATIVO)) {
                sql.put("id_situacaocadastro", complemento.getSituacaoCadastro().getId());
            }
            if (opt.contains(OpcaoProduto.DESCONTINUADO)) {
                sql.put("descontinuado", complemento.isDescontinuado());
            }
            if ((opt.contains(OpcaoProduto.CUSTO_COM_IMPOSTO))
                    || (opt.contains(OpcaoProduto.CUSTO_SEM_IMPOSTO))) {
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
                stm.execute(sql.getUpdate());
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
}
