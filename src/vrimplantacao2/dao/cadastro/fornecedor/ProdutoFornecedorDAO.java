package vrimplantacao2.dao.cadastro.fornecedor;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import vr.core.parametro.versao.Versao;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.produto.ProdutoAnteriorDAO;
import vrimplantacao2.dao.cadastro.produto2.DivisaoDAO;
import vrimplantacao2.parametro.Parametros;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.ProdutoVO;
import vrimplantacao2.vo.cadastro.fornecedor.FornecedorVO;
import vrimplantacao2.vo.cadastro.fornecedor.ProdutoFornecedorCodigoExternoVO;
import vrimplantacao2.vo.cadastro.fornecedor.ProdutoFornecedorVO;
import vrimplantacao2.vo.cadastro.local.EstadoVO;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;

/**
 * @author Leandro
 */
public class ProdutoFornecedorDAO {

    private static final Logger LOG = Logger.getLogger(ProdutoFornecedorDAO.class.getName());

    private int idLojaVR = -1;
    private String importLoja = "";
    private String importSistema = "";

    public void setIdLojaVR(int idLojaVR) {
        this.idLojaVR = idLojaVR;
    }

    public void setImportLoja(String importLoja) {
        this.importLoja = importLoja;
    }

    public int getIdLojaVR() {
        return idLojaVR;
    }

    public String getImportLoja() {
        return importLoja;
    }

    public String getImportSistema() {
        return importSistema;
    }

    public void setImportSistema(String importSistema) {
        this.importSistema = importSistema;
    }

    /**
     * Grava uma listagem de Produtos Fornecedores.
     *
     * @param produtos
     * @param opt
     * @throws Exception
     */
    public void salvar(List<ProdutoFornecedorIMP> produtos, Set<OpcaoProdutoFornecedor> opt) throws Exception {
        Versao versao = Versao.createFromConnectionInterface(Conexao.getConexao());
        //Caregando as listas.
        MultiMap<Integer, ProdutoFornecedorVO> produtoFornecedorExistentes = new MultiMap<>();
        MultiMap<Integer, ProdutoFornecedorVO> produtoFornecedorPreLancamentoExistentes = new MultiMap<>();
        MultiMap<String, Void> codigoExternoExistentes = new MultiMap<>();
        FornecedorAnteriorDAO fornAntDAO = new FornecedorAnteriorDAO();
        ProdutoAnteriorDAO prodAntDAO = new ProdutoAnteriorDAO();
        prodAntDAO.setImportSistema(importSistema);
        prodAntDAO.setImportLoja(importLoja);

        Map<String, Map.Entry<String, Integer>> divisoes = new DivisaoDAO().getAnteriores(importSistema, importLoja);

        System.gc();

        //<editor-fold defaultstate="collapsed" desc="Carrega as listagens">
        ProgressBar.setStatus("Produto Fornecedor...Carregando listagens...");
        int idUfEmpresa = 0;
        {
            try (Statement stm = Conexao.createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select\n"
                        + "	id,\n"
                        + "	id_produto,\n"
                        + "	id_fornecedor,\n"
                        + "	id_estado,\n"
                        + "	custotabela,\n"
                        + "	codigoexterno\n"
                        + "from\n"
                        + "	produtofornecedor\n"
                        + "order by\n"
                        + "	id"
                )) {
                    while (rst.next()) {
                        ProdutoFornecedorVO vo = new ProdutoFornecedorVO();
                        vo.setId(rst.getInt("id"));
                        EstadoVO uf = new EstadoVO();
                        uf.setId(rst.getInt("id_estado"));
                        ProdutoVO prod = new ProdutoVO();
                        prod.setId(rst.getInt("id_produto"));
                        FornecedorVO forn = new FornecedorVO();
                        forn.setId(rst.getInt("id_fornecedor"));
                        vo.setEstado(uf);
                        vo.setProduto(prod);
                        vo.setFornecedor(forn);
                        vo.setCustoTabela(rst.getDouble("custotabela"));
                        vo.setCodigoExterno(rst.getString("codigoexterno"));
                        produtoFornecedorExistentes.put(vo, rst.getInt("id_fornecedor"), rst.getInt("id_produto"), rst.getInt("id_estado"));
                    }
                }

                try (ResultSet rst = stm.executeQuery(
                        "select\n"
                        + "	id,\n"
                        + "	id_produto,\n"
                        + "	id_fornecedor,\n"
                        + "	id_estado,\n"
                        + "	custotabela\n"
                        + "from\n"
                        + "	produtofornecedorprelancamento\n"
                        + "order by\n"
                        + "	id"
                )) {
                    while (rst.next()) {
                        ProdutoFornecedorVO vo = new ProdutoFornecedorVO();
                        vo.setId(rst.getInt("id"));
                        EstadoVO uf = new EstadoVO();
                        uf.setId(rst.getInt("id_estado"));
                        ProdutoVO prod = new ProdutoVO();
                        prod.setId(rst.getInt("id_produto"));
                        FornecedorVO forn = new FornecedorVO();
                        forn.setId(rst.getInt("id_fornecedor"));
                        vo.setEstado(uf);
                        vo.setProduto(prod);
                        vo.setFornecedor(forn);
                        vo.setCustoTabela(rst.getDouble("custotabela"));
                        produtoFornecedorPreLancamentoExistentes.put(vo, rst.getInt("id_fornecedor"), rst.getInt("id_produto"), rst.getInt("id_estado"));
                    }
                }

                try (ResultSet rst = stm.executeQuery(
                        "select\n"
                        + "	f.id_estado\n"
                        + "from\n"
                        + "	loja l\n"
                        + "	join fornecedor f on\n"
                        + "		l.id_fornecedor = f.id\n"
                        + "where\n"
                        + "	l.id = " + getIdLojaVR()
                )) {
                    if (rst.next()) {
                        idUfEmpresa = rst.getInt("id_estado");
                    }
                }
            }
        }
        System.gc();

        {
            try (Statement stm = Conexao.createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select distinct\n"
                        + "	pf.id_fornecedor,\n"
                        + "	pf.id_estado,\n"
                        + "	pf.codigoexterno\n"
                        + "from \n"
                        + "	produtofornecedor pf\n"
                        + "union\n"
                        + "select distinct\n"
                        + "	pf.id_fornecedor,\n"
                        + "	pf.id_estado,\n"
                        + "	pfe.codigoexterno\n"
                        + "from \n"
                        + "	produtofornecedorcodigoexterno pfe\n"
                        + "	join produtofornecedor pf on\n"
                        + "		pfe.id_produtofornecedor = pf.id\n"
                        + "order by codigoexterno"
                )) {
                    while (rst.next()) {
                        codigoExternoExistentes.put(
                                null,
                                rst.getString("id_fornecedor"),
                                rst.getString("id_estado"),
                                rst.getString("codigoexterno")
                        );
                    }
                }
            }
        }
        System.gc();
        /**
         * Obtem o custo e a quantidade da embalagem dos.
         */
        class ProdutoCustoEmbalagem {

            public int id = 0;
            public double custoSemImposto = 0;
            public int qtdEmbalagem = 0;
        }
        Map<Integer, ProdutoCustoEmbalagem> custos = new LinkedHashMap<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + "	p.id, \n"
                    + "	pc.custocomimposto, \n"
                    + "	p.qtdembalagem \n"
                    + "FROM \n"
                    + "	produto p\n"
                    + "	join produtocomplemento pc on pc.id_produto = p.id\n"
                    + "WHERE pc.id_loja = " + getIdLojaVR()
            )) {
                while (rst.next()) {
                    ProdutoCustoEmbalagem pce = new ProdutoCustoEmbalagem();
                    pce.id = rst.getInt("id");
                    pce.custoSemImposto = rst.getDouble("custocomimposto");
                    pce.qtdEmbalagem = rst.getInt("qtdembalagem");
                    custos.put(pce.id, pce);
                }
            }
        }

        ProgressBar.setStatus("Produto Fornecedor...Carregando listagens...Fornecedores...");
        fornAntDAO.atualizarAnteriores();
        ProgressBar.setStatus("Produto Fornecedor...Carregando listagens...Produtos...");
        prodAntDAO.atualizarCodigoAnterior();
        //</editor-fold>

        LOG.fine(
                "Totalizadores:\r\n"
                + " - Total de itens: " + produtos.size() + "\r\n"
                + " - Produto Fornecedores Existentes: " + produtoFornecedorExistentes.size() + "\r\n"
                + " - Códigos Externos Existentes: " + codigoExternoExistentes.size() + "\r\n"
                + " - Custos: " + custos.size()
        );

        LOG.fine(
                "Totalizadores:\r\n"
                + " - Total de itens: " + produtos.size() + "\r\n"
                + " - Produto Fornecedores Pre Lancamento Existentes: " + produtoFornecedorExistentes.size() + "\r\n"
                + " - Custos: " + custos.size()
        );

        System.gc();

        ProgressBar.setStatus("Produto Fornecedor...Gravando...");
        ProgressBar.setMaximum(produtos.size());
        try {
            Conexao.begin();

            for (ProdutoFornecedorIMP imp : produtos) {
                //Impede a importação de códigos externos em branco.
                //if (imp.getCodigoExterno() == null || imp.getCodigoExterno().trim().equals("")) {
                //    ProgressBar.next();
                //    continue;
                //}
                FornecedorVO fornecedor = null;
                if (fornAntDAO.getAnteriores().containsKey(
                        imp.getImportSistema(),
                        imp.getImportLoja(),
                        imp.getIdFornecedor())) {
                    fornecedor = fornAntDAO.getAnteriores().get(
                            imp.getImportSistema(),
                            imp.getImportLoja(),
                            imp.getIdFornecedor()
                    ).getCodigoAtual();
                }
                ProdutoVO produto = null;
                if (prodAntDAO.getCodigoAnterior().containsKey(
                        imp.getImportSistema(),
                        imp.getImportLoja(),
                        imp.getIdProduto())) {
                    produto = prodAntDAO.getCodigoAnterior().get(
                            imp.getImportSistema(),
                            imp.getImportLoja(),
                            imp.getIdProduto()
                    ).getCodigoAtual();
                }

                LOG.finer(
                        "Fornecedor ID: " + imp.getIdFornecedor() + " - " + String.valueOf(fornecedor) + "\r\n"
                        + "Produto ID: " + imp.getIdProduto() + " - " + String.valueOf(produto) + "\r\n"
                );

                /**
                 * Se o Fornecedor existir E Se o Produto existir E Se o Código
                 * Externo NÃO existir
                 */
                if (fornecedor != null
                        && produto != null) {
                    if (imp.getCodigoExterno() != null && !"".equals(imp.getCodigoExterno().trim())) {

                        if (!codigoExternoExistentes.containsKey(
                                String.valueOf(fornecedor.getId()),
                                String.valueOf(produto.getId()),
                                String.valueOf(idUfEmpresa),
                                Utils.acertarTexto(imp.getCodigoExterno(), 50)
                        )) {
                            ProdutoFornecedorVO produtoFornecedor = produtoFornecedorExistentes.get(
                                    fornecedor.getId(),
                                    produto.getId(),
                                    Parametros.get().getUfPadraoV2().getId()
                            );
                            String codigoExterno;
                            /**
                             * Se NÃO existir ProdutoFornecedorVO para este
                             * Fornecedor e este Produto
                             */
                            if (produtoFornecedor == null) {

                                //<editor-fold defaultstate="collapsed" desc="Convertendo o ProdutoFornecedor">
                                produtoFornecedor = new ProdutoFornecedorVO();
                                produtoFornecedor.setFornecedor(fornecedor);
                                produtoFornecedor.setProduto(produto);
                                produtoFornecedor.setEstado(Parametros.get().getUfPadraoV2());
                                //Mapeamento da divisão do fornecedor
                                Map.Entry<String, Integer> divisao = divisoes.get(imp.getIdDivisaoFornecedor());
                                if (divisao != null) {
                                    produtoFornecedor.setIdDivisaoFornecedor(divisao.getValue());
                                }
                                if (imp.getDataAlteracao() != null) {
                                    produtoFornecedor.setDataAlteracao(imp.getDataAlteracao());
                                }
                                ProdutoCustoEmbalagem custoTabela = custos.get(produto.getId());

                                if (imp.getCustoTabela() > 0) {
                                    produtoFornecedor.setCustoTabela(imp.getCustoTabela());
                                } else {
                                    if (custoTabela != null) {
                                        produtoFornecedor.setCustoTabela(custoTabela.custoSemImposto);
                                    }
                                }
                                
                                produtoFornecedor.setQtdEmbalagem(imp.getQtdEmbalagem());
                                /*
                                if (imp.getQtdEmbalagem() > 1) {
                                } else {
                                    if (custoTabela != null) {
                                        produtoFornecedor.setQtdEmbalagem(custoTabela.qtdEmbalagem);
                                    }
                                }*/
                                produtoFornecedor.setCodigoExterno(imp.getCodigoExterno());
                                produtoFornecedor.setPesoEmbalagem(imp.getPesoEmbalagem());
                                produtoFornecedor.setFatorEmbalagem(imp.getFatorEmbalagem());
                                codigoExterno = produtoFornecedor.getCodigoExterno();
                                //</editor-fold>

                                //<editor-fold defaultstate="collapsed" desc="Gravando o ProdutoFornecedorVO">
                                SQLBuilder sql = new SQLBuilder();
                                sql.setTableName("produtofornecedor");
                                sql.put("id_produto", produtoFornecedor.getProduto().getId());
                                sql.put("id_fornecedor", produtoFornecedor.getFornecedor().getId());
                                sql.put("id_estado", produtoFornecedor.getEstado().getId());
                                sql.put("custotabela", produtoFornecedor.getCustoTabela());
                                sql.put("codigoexterno", produtoFornecedor.getCodigoExterno());
                                sql.put("qtdembalagem", produtoFornecedor.getQtdEmbalagem());
                                sql.put("dataalteracao", produtoFornecedor.getDataAlteracao());
                                sql.put("desconto", 0);
                                sql.put("tipoipi", 0);
                                sql.put("ipi", 0);
                                sql.put("tipobonificacao", 0);
                                sql.put("bonificacao", 0);
                                sql.put("tipoverba", 0);
                                sql.put("verba", 0);
                                sql.put("custoinicial", produtoFornecedor.getCustoTabela());
                                sql.put("tipodesconto", 0);
                                sql.put("pesoembalagem", produtoFornecedor.getPesoEmbalagem());
                                sql.put("id_tipopiscofins", 0);
                                sql.putNull("csosn");
                                sql.put("fatorembalagem", produtoFornecedor.getFatorEmbalagem());
                                sql.put("id_divisaofornecedor", produtoFornecedor.getIdDivisaoFornecedor());
                                sql.getReturning().add("id");

                                try (Statement stm = Conexao.createStatement()) {
                                    try (ResultSet rst = stm.executeQuery(
                                            sql.getInsert()
                                    )) {
                                        while (rst.next()) {
                                            produtoFornecedor.setId(rst.getInt("id"));
                                        }
                                    }
                                }
                                //</editor-fold>

                                //<editor-fold defaultstate="collapsed" desc="Inclui na listagem produtoFornecedorExistentes">
                                produtoFornecedorExistentes.put(
                                        produtoFornecedor,
                                        produtoFornecedor.getFornecedor().getId(),
                                        produtoFornecedor.getProduto().getId(),
                                        Parametros.get().getUfPadraoV2().getId()
                                );
                                //</editor-fold>
                            } else {

                                //<editor-fold defaultstate="collapsed" desc="Convertendo o ProdutoFornecedor">
                                ProdutoFornecedorCodigoExternoVO vo = new ProdutoFornecedorCodigoExternoVO();
                                vo.setCodigoExterno(imp.getCodigoExterno());
                                vo.setProdutoFornecedor(produtoFornecedor);
                                vo.setQtdEmbalagem(Math.round((float) imp.getQtdEmbalagem()));
                                vo.setPesoEmbalagem(imp.getPesoEmbalagem());
                                vo.setFatorEmbalagem(imp.getFatorEmbalagem());
                                codigoExterno = vo.getCodigoExterno();
                                if (vo.getCodigoExterno().equals("33532") || vo.getCodigoExterno().equals("113402"))
                                    System.out.println(vo);
                                //<editor-fold defaultstate="collapsed" desc="Gravando o ProdutoFornecedorCodigoExternoVO">
                                SQLBuilder sql = new SQLBuilder();
                                sql.setTableName("produtofornecedorcodigoexterno");
                                sql.put("id_produtofornecedor", vo.getProdutoFornecedor().getId());
                                sql.put("codigoexterno", vo.getCodigoExterno());
                                if (versao.igualOuMaiorQue(4)) {
                                    sql.put("qtdembalagem", vo.getQtdEmbalagem());
                                    sql.put("pesoembalagem", vo.getPesoEmbalagem());
                                    sql.put("fatorembalagem", vo.getFatorEmbalagem());
                                }
                                //TODO: VERIFICAR A VERDADEIRA NECESSIDADE DE RETORNAR O ID :(
                                sql.getReturning().add("id");

                                try (Statement stm = Conexao.createStatement()) {
                                    try (ResultSet rst = stm.executeQuery(
                                            sql.getInsert()
                                    )) {
                                        while (rst.next()) {
                                            vo.setId(rst.getInt("id"));
                                        }
                                    }
                                }
                                //</editor-fold>
                            }
                            //Inclui aos códigos existentes.
                            codigoExternoExistentes.put(
                                    null,
                                    String.valueOf(fornecedor.getId()),
                                    String.valueOf(produto.getId()),
                                    String.valueOf(idUfEmpresa),
                                    codigoExterno
                            );
                            ProgressBar.next();
                        } else {
                            SQLBuilder sql = new SQLBuilder();

                            sql.setSchema("public");
                            sql.setTableName("produtofornecedor");
                            if (opt.contains(OpcaoProdutoFornecedor.DIVISAO_FORNECEDOR)) {
                                //Mapeamento da divisão do fornecedor
                                Map.Entry<String, Integer> divisao = divisoes.get(imp.getIdDivisaoFornecedor());
                                if (divisao != null) {
                                    sql.put("id_divisaofornecedor", divisao.getValue());
                                }
                            }
                            sql.setWhere(
                                    "id_fornecedor = " + fornecedor.getId() + " and\n"
                                    + "id_produto = " + produto.getId() + " and\n"
                                    + "id_estado = " + idUfEmpresa
                            );

                            if (!sql.isEmpty()) {
                                try (Statement stm = Conexao.createStatement()) {
                                    stm.execute(sql.getUpdate());
                                }
                            }
                        }
                    } else {
                        ProdutoFornecedorVO produtoFornecedorPreLancamento = produtoFornecedorPreLancamentoExistentes.get(
                                fornecedor.getId(),
                                produto.getId(),
                                Parametros.get().getUfPadraoV2().getId()
                        );

                        if (produtoFornecedorPreLancamento == null) {
                            //<editor-fold defaultstate="collapsed" desc="Convertendo o ProdutoFornecedor">
                            produtoFornecedorPreLancamento = new ProdutoFornecedorVO();
                            produtoFornecedorPreLancamento.setFornecedor(fornecedor);
                            produtoFornecedorPreLancamento.setProduto(produto);
                            produtoFornecedorPreLancamento.setEstado(Parametros.get().getUfPadraoV2());
                            //Mapeamento da divisão do fornecedor
                            Map.Entry<String, Integer> divisao = divisoes.get(imp.getIdDivisaoFornecedor());
                            if (divisao != null) {
                                produtoFornecedorPreLancamento.setIdDivisaoFornecedor(divisao.getValue());
                            }
                            if (imp.getDataAlteracao() != null) {
                                produtoFornecedorPreLancamento.setDataAlteracao(imp.getDataAlteracao());
                            }
                            ProdutoCustoEmbalagem custoTabela = custos.get(produto.getId());

                            if (imp.getCustoTabela() > 0) {
                                produtoFornecedorPreLancamento.setCustoTabela(imp.getCustoTabela());
                            } else {
                                if (custoTabela != null) {
                                    produtoFornecedorPreLancamento.setCustoTabela(custoTabela.custoSemImposto);
                                }
                            }
                            if (imp.getQtdEmbalagem() > 1) {
                                produtoFornecedorPreLancamento.setQtdEmbalagem(imp.getQtdEmbalagem());
                            } else {
                                if (custoTabela != null) {
                                    produtoFornecedorPreLancamento.setQtdEmbalagem(custoTabela.qtdEmbalagem);
                                }
                            }
                            produtoFornecedorPreLancamento.setCodigoExterno(imp.getCodigoExterno());
                            produtoFornecedorPreLancamento.setPesoEmbalagem(imp.getPesoEmbalagem());
                            produtoFornecedorPreLancamento.setFatorEmbalagem(imp.getFatorEmbalagem());
                            //</editor-fold>

                            //<editor-fold defaultstate="collapsed" desc="Gravando o ProdutoFornecedorVO">
                            SQLBuilder sql = new SQLBuilder();
                            sql.setTableName("produtofornecedorprelancamento");
                            sql.put("id_produto", produtoFornecedorPreLancamento.getProduto().getId());
                            sql.put("id_fornecedor", produtoFornecedorPreLancamento.getFornecedor().getId());
                            sql.put("id_estado", produtoFornecedorPreLancamento.getEstado().getId());
                            sql.put("custotabela", produtoFornecedorPreLancamento.getCustoTabela());
                            sql.put("qtdembalagem", produtoFornecedorPreLancamento.getQtdEmbalagem());
                            sql.put("dataalteracao", produtoFornecedorPreLancamento.getDataAlteracao());
                            sql.put("desconto", 0);
                            sql.put("tipoipi", 0);
                            sql.put("ipi", 0);
                            sql.put("tipobonificacao", 0);
                            sql.put("bonificacao", 0);
                            sql.put("tipoverba", 0);
                            sql.put("verba", 0);
                            sql.put("custoinicial", produtoFornecedorPreLancamento.getCustoTabela());
                            sql.put("tipodesconto", 0);
                            //sql.put("pesoembalagem", produtoFornecedorPreLancamento.getPesoEmbalagem());
                            //sql.put("id_tipopiscofins", 0);
                            //sql.putNull("csosn");
                            //sql.put("fatorembalagem", produtoFornecedorPreLancamento.getFatorEmbalagem());
                            sql.put("id_divisaofornecedor", produtoFornecedorPreLancamento.getIdDivisaoFornecedor());
                            sql.getReturning().add("id");

                            try (Statement stm = Conexao.createStatement()) {
                                try (ResultSet rst = stm.executeQuery(
                                        sql.getInsert()
                                )) {
                                    while (rst.next()) {
                                        produtoFornecedorPreLancamento.setId(rst.getInt("id"));
                                    }
                                }
                            }
                            //</editor-fold>

                            //<editor-fold defaultstate="collapsed" desc="Inclui na listagem produtoFornecedorExistentes">
                            produtoFornecedorPreLancamentoExistentes.put(
                                    produtoFornecedorPreLancamento,
                                    produtoFornecedorPreLancamento.getFornecedor().getId(),
                                    produtoFornecedorPreLancamento.getProduto().getId(),
                                    Parametros.get().getUfPadraoV2().getId()
                            );
                            //</editor-fold>
                        }
                    }
                }
                ProgressBar.next();
            }
            Conexao.commit();
        } catch (Exception e) {
            Conexao.rollback();
            throw e;
        }
    }

    public void atualizarProdutoFornecedor(ProdutoFornecedorVO vo, Set<OpcaoProdutoFornecedor> opcao) throws Exception {

        if (opcao != null) {
            try (Statement stm = Conexao.createStatement()) {
                SQLBuilder sql = new SQLBuilder();
                sql.setTableName("produtofornecedor");
                if (opcao.contains(OpcaoProdutoFornecedor.IPI)) {
                    sql.put("ipi", vo.getIpi());
                    sql.put("tipoipi", vo.getTipoIpi());
                }
                if (opcao.contains(OpcaoProdutoFornecedor.QTDEMBALAGEM)) {
                    sql.put("qtdembalagem", vo.getQtdEmbalagem());
                }
                sql.setWhere("id_produto = " + vo.getProduto().getId()
                        + " and id_fornecedor = " + vo.getFornecedor().getId());
                stm.execute(sql.getUpdate());
            }
        }
    }
}
