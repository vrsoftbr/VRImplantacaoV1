package vrimplantacao2.dao.cadastro.produto2;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;
import vrframework.classe.Conexao;
import vrframework.classe.Util;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.parametro.Versao;
import vrimplantacao2.utils.collection.IDStack;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.MercadologicoVO;
import vrimplantacao2.vo.cadastro.ProdutoVO;
import vrimplantacao2.vo.enums.NaturezaReceitaVO;
import vrimplantacao2.vo.enums.NcmVO;

/**
 * Classe que faz a interface entre o sistema e o banco de dados.
 *
 * @author Leandro
 */
public class ProdutoDAO {

    private static final Logger LOG = Logger.getLogger(ProdutoDAO.class.getName());

    /**
     * Retorna um {@link IDStack} com todos os IDs disponíveis maiores que 0 e
     * menores que 10000 para produtos de balança.
     *
     * @return Pilha com os IDs para produtos de balança.
     * @throws Exception
     */
    public IDStack getIDsVagosBalanca() throws Exception {
        IDStack balanca = new IDStack();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT id from \n"
                    + "(SELECT id FROM generate_series(1, 9999)\n"
                    + "AS s(id) EXCEPT SELECT id FROM produto WHERE id <= 9999) AS codigointerno ORDER BY id desc"
            )) {
                while (rst.next()) {
                    balanca.add(rst.getInt("id"));
                }
            }
        }
        return balanca;
    }

    /**
     * Retorna um {@link IDStack} com todos os IDs disponíveis maiores que 9999
     * e menores que 1000000 para os produtos normais.
     *
     * @return Pilha com os IDs para produtos normais.
     * @throws Exception
     */
    public IDStack getIDsVagosNormais() throws Exception {
        IDStack normais = new IDStack();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT id from \n"
                    + "(SELECT id FROM generate_series(10000, 999999)\n"
                    + "AS s(id) EXCEPT SELECT id FROM produto WHERE id > 9999) AS codigointerno ORDER BY id desc"
            )) {
                while (rst.next()) {
                    normais.add(rst.getInt("id"));
                }
            }
        }
        return normais;
    }

    /**
     * Retorna um {@link Set} com todos os IDs cadastrados na tabela produtos.
     *
     * @return IDs cadastrados.
     * @throws Exception
     */
    public Set<Integer> getIDsCadastrados() throws Exception {
        Set<Integer> cadastrados = new TreeSet<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery("SELECT id from produto")) {
                while (rst.next()) {
                    cadastrados.add(rst.getInt("id"));
                }
            }
        }
        return cadastrados;
    }

    /**
     * Insere um {@link ProdutoVO} no banco de dados.
     *
     * @param vo {@link ProdutoVO} a ser incluso.
     * @throws Exception
     */
    public void salvar(ProdutoVO vo) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            sql.setTableName("produto");

            sql.put("id", vo.getId());
            sql.put("descricaocompleta", vo.getDescricaoCompleta());
            sql.put("qtdembalagem", vo.getQtdEmbalagem());
            sql.put("id_tipoembalagem", vo.getTipoEmbalagem().getId());
            sql.put("mercadologico1", vo.getMercadologico().getMercadologico1());
            sql.put("mercadologico2", vo.getMercadologico().getMercadologico2());
            sql.put("mercadologico3", vo.getMercadologico().getMercadologico3());
            sql.put("mercadologico4", vo.getMercadologico().getMercadologico4());
            sql.put("mercadologico5", vo.getMercadologico().getMercadologico5());
            sql.put("id_comprador", vo.getIdComprador());
            sql.put("custofinal", 0.0);
            sql.put("id_familiaproduto", vo.getFamiliaProduto() != null ? vo.getFamiliaProduto().getId() : -1, -1);
            sql.put("descricaoreduzida", vo.getDescricaoReduzida());
            sql.put("pesoliquido", vo.getPesoLiquido());
            sql.put("datacadastro", vo.getDatacadastro());
            sql.put("validade", vo.getValidade());
            sql.put("pesobruto", vo.getPesoBruto());
            sql.put("comprimentoembalagem", 0);
            sql.put("larguraembalagem", 0);
            sql.put("alturaembalagem", 0);
            sql.put("perda", 0.0);
            sql.put("margemminima", vo.getMargemMinima());
            sql.put("margem", vo.getMargem());
            sql.put("verificacustotabela", false);
            sql.put("percentualipi", 0.0);
            sql.put("percentualfrete", 0.0);
            sql.put("percentualencargo", 0.0);
            sql.put("percentualperda", 0.0);
            sql.put("percentualsubstituicao", 0.0);
            sql.put("descricaogondola", vo.getDescricaoGondola());
            sql.put("dataalteracao", new Date());
            sql.putNull("id_produtovasilhame");
            if (Versao.menorQue(3, 19,1, 65)) {
                sql.put("excecao", vo.getExcecao());
            }
            sql.put("id_tipomercadoria", 99);
            sql.put("sugestaopedido", true);
            sql.put("aceitamultiplicacaopdv", vo.isAceitaMultiplicacaoPDV());
            sql.put("id_fornecedorfabricante", vo.getIdFornecedorFabricante());
            sql.put("id_divisaofornecedor", 0);
            sql.put("id_tipopiscofins", vo.getPisCofinsDebito().getId());
            sql.put("sazonal", false);
            sql.put("consignado", false);
            {
                NcmVO ncm = vo.getNcm();
                if (ncm == null) {
                    ncm = new NcmVO();
                }
                sql.put("ncm1", ncm.getNcm1());
                sql.put("ncm2", ncm.getNcm2());
                sql.put("ncm3", ncm.getNcm3());
            }
            sql.put("ddv", 0);
            sql.put("permitetroca", true);
            sql.put("temperatura", 0);
            sql.put("id_tipoorigemmercadoria", 0);
            if (Versao.maiorQue(3, 18, 2)) {
                sql.put("id_tipoorigemmercadoriaentrada", 0);
            }
            sql.put("ipi", 0);
            sql.put("pesavel", vo.isPesavel());
            sql.put("id_tipopiscofinscredito", vo.getPisCofinsCredito().getId());
            sql.put("vendacontrolada", vo.isVendaControlada());
            sql.put("tiponaturezareceita", vo.getPisCofinsNaturezaReceita() != null ? vo.getPisCofinsNaturezaReceita().getCodigo() : null);
            sql.put("vendapdv", true);
            sql.put("conferido", false);
            sql.put("permitequebra", true);
            sql.put("permiteperda", true);
            sql.put("codigoanp", "");
            sql.put("impostomedionacional", 0);
            sql.put("impostomedioimportado", 0);
            sql.put("sugestaocotacao", true);
            sql.put("tara", 0.0);
            sql.put("utilizatabelasubstituicaotributaria", false);
            sql.put("id_tipolocaltroca", 0);
            sql.put("qtddiasminimovalidade", 0);
            sql.put("utilizavalidadeentrada", false);
            sql.put("impostomedioestadual", 0);
            sql.put("id_tipocompra", 0);
            sql.put("numeroparcela", 0);
            sql.put("id_tipoembalagemvolume", vo.getTipoEmbalagemVolume().getId());
            sql.put("volume", vo.getVolume());
            sql.put("id_normacompra", vo.getNormaCompra().getId());
            sql.putNull("lastro");
            sql.putNull("camadas");
            sql.put("promocaoauditada", false);
            sql.putNull("substituicaoestadual");
            sql.putNull("substituicaoestadualoutros");
            sql.putNull("substituicaoestadualexterior");
            sql.put("id_cest", vo.getCest() != null ? vo.getCest().getId() : null);
            sql.putNull("lastro");
            sql.putNull("margemminima");
            sql.putNull("margemmaxima");
            sql.put("permitedescontopdv", true);
            sql.put("verificapesopdv", false);
            sql.put("id_divisaofornecedor", vo.getIdDivisaoFornecedor());
            if (Versao.menorQue(3, 17, 10)) {
                sql.put("id_tipoproduto", 0);
                sql.put("fabricacaopropria", false);
            }

            try {
                stm.execute(sql.getInsert());
            } catch (Exception e) {
                throw e;
            }
        }
    }

    /**
     * Executa um update na tabela produtos.
     *
     * @param vo (@link ProdutoVO} com as informações a serem atualizadas.
     * @param opt Listagem que indica quais informações devem ser atualizadas.
     * @throws Exception
     */
    public void atualizar(ProdutoVO vo, Set<OpcaoProduto> opt) throws Exception {
        SQLBuilder sql = new SQLBuilder();
        sql.setTableName("produto");
        if (opt.contains(OpcaoProduto.MERCADOLOGICO)) {
            MercadologicoVO mercadologico = vo.getMercadologico();
            sql.put("mercadologico1", mercadologico.getMercadologico1());
            LOG.fine("Prod: " + vo.getId() + " Merc1: " + mercadologico.getMercadologico1());
            sql.put("mercadologico2", mercadologico.getMercadologico2());
            LOG.fine("Prod: " + vo.getId() + " Merc2: " + mercadologico.getMercadologico2());
            sql.put("mercadologico3", mercadologico.getMercadologico3());
            LOG.fine("Prod: " + vo.getId() + " Merc3: " + mercadologico.getMercadologico3());
            sql.put("mercadologico4", mercadologico.getMercadologico4());
            sql.put("mercadologico5", mercadologico.getMercadologico5());
        }
        if (opt.contains(OpcaoProduto.CEST)) {
            sql.put("id_cest", (vo.getCest() == null ? null : vo.getCest().getId()));
        }
        if (opt.contains(OpcaoProduto.DESC_COMPLETA)) {
            sql.put("descricaocompleta", vo.getDescricaoCompleta());
        }
        if (opt.contains(OpcaoProduto.DESC_REDUZIDA)) {
            sql.put("descricaoreduzida", vo.getDescricaoReduzida());
        }
        if (opt.contains(OpcaoProduto.DESC_GONDOLA)) {
            sql.put("descricaogondola", vo.getDescricaoGondola());
        }
        if (opt.contains(OpcaoProduto.QTD_EMBALAGEM_COTACAO)) {
            sql.put("qtdembalagem", vo.getQtdEmbalagem());
        }
        if (opt.contains(OpcaoProduto.PIS_COFINS)) {
            sql.put("id_tipopiscofins", vo.getPisCofinsDebito().getId());
            sql.put("id_tipopiscofinscredito", vo.getPisCofinsCredito().getId());
        }
        if (opt.contains(OpcaoProduto.PISCOFINS_LOJA)) {
            sql.put("id_tipopiscofins", vo.getPisCofinsDebito().getId());
            sql.put("id_tipopiscofinscredito", vo.getPisCofinsCredito().getId());
            NaturezaReceitaVO nat = vo.getPisCofinsNaturezaReceita();
            sql.put("tiponaturezareceita", nat != null ? nat.getCodigo() : null);
        }

        if (opt.contains(OpcaoProduto.NATUREZA_RECEITA)) {
            NaturezaReceitaVO nat = vo.getPisCofinsNaturezaReceita();
            sql.put("tiponaturezareceita", nat != null ? nat.getCodigo() : null);
        }
        if (opt.contains(OpcaoProduto.MARGEM_MINIMA)) {
            sql.put("margemminima", vo.getMargemMinima());
        }
        if (opt.contains(OpcaoProduto.MARGEM)) {
            sql.put("margem", vo.getMargem());
        }
        if (opt.contains(OpcaoProduto.VALIDADE)) {
            sql.put("validade", vo.getValidade());
        }
        if (opt.contains(OpcaoProduto.TIPO_EMBALAGEM_PRODUTO)) {
            sql.put("id_tipoembalagem", vo.getTipoEmbalagem().getId());
            sql.put("pesavel", vo.isPesavel());
        }
        if (opt.contains(OpcaoProduto.FAMILIA)) {
            if (vo.getFamiliaProduto() != null) {
                sql.put("id_familiaproduto", vo.getFamiliaProduto().getId());
            } else {
                sql.putNull("id_familiaproduto");
            }
        }
        if (opt.contains(OpcaoProduto.COMPRADOR_PRODUTO)) {
            sql.put("id_comprador", vo.getIdComprador());
        }
        if (opt.contains(OpcaoProduto.NCM)) {
            sql.put("ncm1", vo.getNcm().getNcm1());
            sql.put("ncm2", vo.getNcm().getNcm2());
            sql.put("ncm3", vo.getNcm().getNcm3());
        }
        if (opt.contains(OpcaoProduto.NCM_LOJA)) {
            sql.put("ncm1", vo.getNcm().getNcm1());
            sql.put("ncm2", vo.getNcm().getNcm2());
            sql.put("ncm3", vo.getNcm().getNcm3());
        }

        if (opt.contains(OpcaoProduto.SUGESTAO_COTACAO)) {
            sql.put("sugestaocotacao", vo.isSugestaoCotacao());
        }
        if (opt.contains(OpcaoProduto.SUGESTAO_PEDIDO)) {
            sql.put("sugestaopedido", vo.isSugestaoPedido());
        }
        if (opt.contains(OpcaoProduto.FABRICANTE)) {
            sql.put("id_fornecedorfabricante", vo.getIdFornecedorFabricante());
        }
        if (opt.contains(OpcaoProduto.DATA_CADASTRO)) {
            sql.put("datacadastro", vo.getDatacadastro());
        }
        if (opt.contains(OpcaoProduto.VENDA_PDV)) {
            sql.put("vendapdv", vo.isVendaPdv());
        }
        if (opt.contains(OpcaoProduto.DATA_ALTERACAO)) {
            sql.put("dataalteracao", vo.getDataAlteracao());
        }
        if (opt.contains(OpcaoProduto.PESO_BRUTO)) {
            sql.put("pesobruto", vo.getPesoBruto());
        }
        if (opt.contains(OpcaoProduto.PESO_LIQUIDO)) {
            sql.put("pesoliquido", vo.getPesoLiquido());
        }
        if (opt.contains(OpcaoProduto.ACEITA_MULTIPLICACAO_PDV)) {
            sql.put("aceitamultiplicacaopdv", vo.isAceitaMultiplicacaoPDV());
        }
        if (opt.contains(OpcaoProduto.DIVISAO_PRODUTO)) {
            sql.put("id_divisaofornecedor", vo.getIdDivisaoFornecedor());
        }
        if (opt.contains(OpcaoProduto.VOLUME_TIPO_EMBALAGEM)) {
            sql.put("id_tipoembalagemvolume", vo.getTipoEmbalagemVolume().getId());
        }
        if (opt.contains(OpcaoProduto.VOLUME_QTD)) {
            sql.put("volume", vo.getVolume());
        }
        if (opt.contains(OpcaoProduto.VENDA_CONTROLADA)) {
            sql.put("vendacontrolada", vo.isVendaControlada());
        }

        sql.setWhere("id = " + vo.getId());
        String strSql = sql.getUpdate();
        LOG.fine(strSql);
        try {
            if (!sql.isEmpty()) {
                try (Statement stm = Conexao.createStatement()) {
                    stm.execute(strSql);
                }
            }
        } catch (Exception e) {
            Util.exibirMensagem(sql.getUpdate(), "Erro");
            throw e;
        }
    }

    public Map<Integer, ProdutoVO> getProdutos() throws Exception {
        Map<Integer, ProdutoVO> result = new HashMap<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "id "
                    + "from produto "
            )) {
                while (rst.next()) {
                    ProdutoVO vo = new ProdutoVO();
                    vo.setId(rst.getInt("id"));
                    result.put(rst.getInt("id"), vo);
                }
            }
        }
        return result;
    }
}
