package vrimplantacao2.dao.cadastro.produto;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import vr.core.parametro.versao.Versao;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao.utils.Utils;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.ProdutoAnteriorEanVO;
import vrimplantacao2.vo.cadastro.ProdutoAnteriorVO;
import vrimplantacao2.vo.cadastro.ProdutoAutomacaoVO;
import vrimplantacao2.vo.cadastro.ProdutoVO;
import vrimplantacao2.vo.enums.TipoEmbalagem;
import vrimplantacao2.vo.importacao.ProdutoIMP;

public class ProdutoAutomacaoDAO {

    private Map<Long, Integer> eansCadastrados;

    public Map<Long, Integer> getEansCadastrados() throws Exception {
        if (eansCadastrados == null) {
            atualizaEansCadastrados();
        }
        return eansCadastrados;
    }

    public Set<Long> getEansCadastradosAtacado(int idLoja) throws Exception {
        Set<Long> result = new HashSet<>();

        if (Versao.createFromConnectionInterface(Conexao.getConexao()).menorQue(3, 18, 1)) {
            try (Statement stm = Conexao.createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select codigobarras from produtoautomacaoloja where id_loja = " + idLoja + "\n"
                        + "union\n"
                        + "select codigobarras from produtoautomacaodesconto where id_loja = " + idLoja + "\n"
                        + "order by codigobarras"
                )) {
                    while (rst.next()) {
                        result.add(rst.getLong("codigobarras"));
                    }
                }
            }
        } else {
            try (Statement stm = Conexao.createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select codigobarras from produtoautomacaodesconto where id_loja = " + idLoja + "\n"
                        + "order by codigobarras"
                )) {
                    while (rst.next()) {
                        result.add(rst.getLong("codigobarras"));
                    }
                }
            }
        }

        return result;
    }

    public void atualizaEansCadastrados() throws Exception {
        eansCadastrados = new LinkedHashMap<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select codigobarras, id_produto from produtoautomacao order by codigobarras"
            )) {
                while (rst.next()) {
                    eansCadastrados.put(rst.getLong("codigobarras"), rst.getInt("id_produto"));
                }
            }
        }
    }

    public void salvar(ProdutoAutomacaoVO vo) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            if (!getEansCadastrados().containsKey(vo.getCodigoBarras())) {
                SQLBuilder sql = new SQLBuilder();
                sql.setTableName("produtoautomacao");
                sql.put("id_produto", vo.getProduto().getId());
                sql.put("codigobarras", vo.getCodigoBarras());
                sql.put("qtdembalagem", vo.getQtdEmbalagem());
                sql.put("id_tipoembalagem", vo.getTipoEmbalagem().getId());
                sql.put("pesobruto", vo.getPesoBruto());
                sql.put("dun14", vo.isDun14());
                sql.getReturning().add("id");

                try (ResultSet rst = stm.executeQuery(
                        sql.getInsert()
                )) {
                    if (rst.next()) {
                        vo.setId(rst.getInt("id"));
                    }
                }
                getEansCadastrados().put(vo.getCodigoBarras(), vo.getProduto().getId());
            }
        }
    }

    public void salvar(ProdutoIMP imp, ProdutoAnteriorVO anterior) throws Exception {
        ProdutoVO produto = anterior.getCodigoAtual();
        long ean = Utils.stringToLong(imp.getEan());

        if (produto == null) {
            System.out.println("IMPID: " + anterior.getImportId() + " NAO ENCONTRADO!");
        } else {
            ProdutoAutomacaoVO automacao = produto.getEans().make(ean);

            automacao.setCodigoBarras(ean);
            automacao.setDun14(String.valueOf(ean).length() > 13);
            automacao.setPesoBruto(imp.getPesoBruto());
            automacao.setQtdEmbalagem(imp.getQtdEmbalagem());
            automacao.setTipoEmbalagem(TipoEmbalagem.getTipoEmbalagem(imp.getTipoEmbalagem()));

            ProdutoAnteriorEanVO antEan = anterior.getEans().make(
                    imp.getImportSistema(),
                    imp.getImportLoja(),
                    imp.getImportId(),
                    imp.getEan()
            );

            antEan.setEan(imp.getEan());
            antEan.setQtdEmbalagem(imp.getQtdEmbalagem());
            antEan.setTipoEmbalagem(imp.getTipoEmbalagem());
            antEan.setValor(imp.getPrecovenda());

            salvar(automacao);
            if (!eanAnteriorDAO.getEansAnteriores().containsKey(
                    antEan.getImportSistema(),
                    antEan.getImportLoja(),
                    antEan.getImportId(),
                    antEan.getEan()
            )) {
                eanAnteriorDAO.salvar(antEan, "");
            }
        }
    }

    public void salvarEANAnterior(ProdutoIMP imp, ProdutoAnteriorVO anterior) throws Exception {
        ProdutoVO produto = anterior.getCodigoAtual();

        if (produto == null) {
            System.out.println("IMPID: " + anterior.getImportId() + " NAO ENCONTRADO!");
        } else {
            ProdutoAnteriorEanVO antEan = anterior.getEans().make(
                    imp.getImportSistema(),
                    imp.getImportLoja(),
                    imp.getImportId(),
                    imp.getEan()
            );

            antEan.setEan(imp.getEan());
            antEan.setQtdEmbalagem(imp.getQtdEmbalagem());
            antEan.setTipoEmbalagem(imp.getTipoEmbalagem());
            antEan.setValor(imp.getPrecovenda());

            if (!eanAnteriorDAO.getEansAnteriores().containsKey(
                    antEan.getImportSistema(),
                    antEan.getImportLoja(),
                    antEan.getImportId(),
                    antEan.getEan()
            )) {
                eanAnteriorDAO.salvar(antEan, "");
            }
        }
    }
    
    private final ProdutoAnteriorEanDAO eanAnteriorDAO = new ProdutoAnteriorEanDAO();

    /**
     * Gera EAN para produtos que não os possuem.
     *
     * @throws java.lang.Exception
     */
    public void salvarEansEmBranco() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            Collection<ProdutoVO> values = carregarCodigoBarrasEmBranco().values();
            ProgressBar.setStatus("Incluindo produtos com EAN em branco");
            ProgressBar.setMaximum(values.size());
            for (ProdutoVO produto : values) {
                for (ProdutoAutomacaoVO ean : produto.getEans().values()) {
                    //Se o ean não estiver cadastrado para outro produto
                    if (!getEansCadastrados().containsKey(ean.getCodigoBarras())) {
                        SQLBuilder sql = new SQLBuilder();
                        sql.setTableName("produtoautomacao");
                        sql.put("id_produto", produto.getId());
                        sql.put("codigobarras", ean.getCodigoBarras());
                        sql.put("qtdembalagem", ean.getQtdEmbalagem());
                        sql.put("id_tipoembalagem", ean.getTipoEmbalagem().getId());

                        stm.execute(sql.getInsert());
                    }
                }
                ProgressBar.next();
            }
        }
    }

    /**
     * Carrega uma listagem com os códigos em branco.
     *
     * @return
     * @throws Exception
     */
    private Map<Long, ProdutoVO> carregarCodigoBarrasEmBranco() throws Exception {
        Map<Long, ProdutoVO> vProduto = new HashMap<>();

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select id, id_tipoembalagem, pesavel from produto p where not exists(select pa.id from produtoautomacao pa where pa.id_produto = p.id)"
            )) {
                while (rst.next()) {
                    int idProduto = rst.getInt("id");
                    long codigobarras = idProduto;

                    ProdutoVO oProduto = new ProdutoVO();
                    oProduto.setId(idProduto);
                    ProdutoAutomacaoVO oAutomacao = oProduto.getEans().make(codigobarras);
                    oAutomacao.setTipoEmbalagem(TipoEmbalagem.getTipoEmbalagem(rst.getInt("id_tipoembalagem")));
                    oAutomacao.setCodigoBarras(codigobarras);
                    oAutomacao.setQtdEmbalagem(1);

                    vProduto.put(codigobarras, oProduto);
                }

                return vProduto;
            }
        }
    }

    public void atualizar(ProdutoAutomacaoVO automacao, Set<OpcaoProduto> opt) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            sql.setTableName("produtoautomacao");
            if (opt.contains(OpcaoProduto.TIPO_EMBALAGEM_EAN)) {
                sql.put("id_tipoembalagem", automacao.getTipoEmbalagem().getId());
            }
            if (opt.contains(OpcaoProduto.QTD_EMBALAGEM_EAN)) {
                sql.put("qtdembalagem", automacao.getQtdEmbalagem());
            }
            sql.setWhere("id_produto=" + automacao.getProduto().getId() + " and codigobarras=" + automacao.getCodigoBarras());
            if (!sql.isEmpty()) {
                stm.execute(sql.getUpdate());
            }
        }
    }

    public boolean getEanById(long ean, int prod) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select * from produtoautomacao where codigobarras = " + ean + " and id_produto = " + prod
            )) {
                if (rst.next()) {
                    return true;
                } else {
                    return false;
                }
            }
        }
    }

    public Map<Long, Integer> getProdutosByEan() throws Exception {
        Map<Long, Integer> result = new HashMap<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select id_produto, codigobarras from produtoautomacao"
            )) {
                while (rst.next()) {
                    result.put(
                            rst.getLong("codigobarras"),
                            rst.getInt("id_produto")
                    );
                }
            }
        }
        return result;
    }
}
