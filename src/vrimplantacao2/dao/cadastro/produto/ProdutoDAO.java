package vrimplantacao2.dao.cadastro.produto;

import java.sql.Statement;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.produto2.ProdutoRepository;
import vrimplantacao2.dao.cadastro.produto2.ProdutoRepositoryProvider;
import vrimplantacao2.utils.multimap.KeyList;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.MercadologicoVO;
import vrimplantacao2.vo.cadastro.ProdutoAnteriorVO;
import vrimplantacao2.vo.cadastro.ProdutoAutomacaoVO;
import vrimplantacao2.vo.cadastro.ProdutoComplementoVO;
import vrimplantacao2.vo.cadastro.ProdutoVO;
import vrimplantacao2.vo.enums.NaturezaReceitaVO;
import vrimplantacao2.vo.enums.NcmVO;
import vrimplantacao2.vo.enums.TipoEmbalagem;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Leandro
 */
public class ProdutoDAO {
    private boolean manterCodigoDeBalanca = false;
    private boolean duplicarProdutoDeBalanca = false;
    private int idLojaVR = 1;
    private String importSistema = null;
    private String importLoja = null;
    private ProdutoRepositoryProvider Produtoprovider = new ProdutoRepositoryProvider();

    public void setIdLojaVR(int idLojaVR) {
        this.idLojaVR = idLojaVR;
    }

    public int getIdLojaVR() {
        return idLojaVR;
    }

    public void setImportSistema(String importSistema) {
        this.importSistema = importSistema;
        anteriorDAO.setImportSistema(importSistema);
    }

    public String getImportSistema() {
        return importSistema;
    }

    public String getImportLoja() {
        return importLoja;
    }
    
    
    
    public void setImportLoja(String importLoja) {
        this.importLoja = importLoja;
        anteriorDAO.setImportLoja(importLoja);
    }

    public boolean isManterCodigoDeBalanca() {
        return manterCodigoDeBalanca;
    }

    public boolean isDuplicarProdutoDeBalanca() {
        return duplicarProdutoDeBalanca;
    }

    public void setDuplicarProdutoDeBalanca(boolean duplicarProdutoDeBalanca) {
        this.duplicarProdutoDeBalanca = duplicarProdutoDeBalanca;
    }
        
    private final ProdutoAnteriorDAO anteriorDAO = new ProdutoAnteriorDAO();
    private MultiMap<String, ProdutoAnteriorVO> getCodigoAnterior() throws Exception {
        return anteriorDAO.getCodigoAnterior();
    }
    
    public void salvar(List<ProdutoIMP> produtos) throws Exception {

        System.gc();
        MultiMap<String, ProdutoVO> tratados;
        {
            //Organiza a lista de ProdutoIMP e transforma em MultiMap.
            MultiMap<String, ProdutoIMP> organizados = 
                    new OrganizadorIMP(this)
                            .organizarListagem(produtos);
            //Converte a listagem de ProdutoIMP em ProdutoVO.
            MultiMap<String, ProdutoVO> convertidos = 
                    new ConversorProduto(this)
                            .converterListagem(organizados);
            //Faz tratamento de id e eans na listagem de ProdutoVO.
            tratados = new TratadorProduto(this)
                    .tratarListagem(convertidos);
        }
                
        ProgressBar.setStatus("Produtos - Gravando...");
        ProgressBar.setMaximum(tratados.size());
        try {
            Conexao.begin(); 
            
            for (KeyList<String> keys: tratados.keySet()) {
                String[] chave = new String[] {
                    keys.get(0),
                    keys.get(1),
                    keys.get(2)
                };
                ProdutoVO vo = tratados.get(chave);
                
                if (!getCodigoAnterior().containsKey(chave)) {
                    gravarProduto(vo);
                    complementoDAO.salvar(vo.getComplementos().values(), false);
                    aliquotaDAO.salvar(idLojaVR, vo.getAliquotas().values());
                    anteriorDAO.salvar(vo.getCodigosAnteriores().values());
                } else {                        
                    vo.setId(getCodigoAnterior().get(chave).getCodigoAtual().getId());
                }

                for (ProdutoAutomacaoVO ean: vo.getEans().values()) {
                    if (!automacaoDAO.getEansCadastrados().containsKey(ean.getCodigoBarras())) {
                        automacaoDAO.salvar(ean);
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

    private void gravarProduto(ProdutoVO vo) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            sql.setTableName("produto");
            
            
            sql.put("id", vo.getId());
            sql.put("descricaocompleta", vo.getDescricaoCompleta());
            sql.put("qtdembalagem", 1);
            sql.put("id_tipoembalagem", vo.getTipoEmbalagem().getId());
            sql.put("mercadologico1", vo.getMercadologico().getMercadologico1());
            sql.put("mercadologico2", vo.getMercadologico().getMercadologico2());
            sql.put("mercadologico3", vo.getMercadologico().getMercadologico3());
            sql.put("mercadologico4", vo.getMercadologico().getMercadologico4());
            sql.put("mercadologico5", vo.getMercadologico().getMercadologico5());
            sql.put("id_comprador", 1);
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
            sql.put("excecao", 0);
            sql.put("id_tipomercadoria", 99);
            sql.put("sugestaopedido", true);
            sql.put("aceitamultiplicacaopdv", true);
            sql.put("id_fornecedorfabricante", 1);
            sql.put("id_divisaofornecedor", 0);
            sql.put("id_tipoproduto", 0);
            sql.put("id_tipopiscofins", vo.getPisCofinsDebito().getId());
            sql.put("sazonal", false);
            sql.put("fabricacaopropria", false);
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
            sql.put("ipi", 0);
            sql.put("pesavel", vo.isPesavel());
            sql.put("id_tipopiscofinscredito", vo.getPisCofinsCredito().getId());
            sql.put("vendacontrolada", false);
            sql.put("tiponaturezareceita", vo.getPisCofinsNaturezaReceita() != null ? vo.getPisCofinsNaturezaReceita().getCodigo() : null);
            sql.put("vendapdv", true);
            sql.put("conferido", false);
            sql.put("permitequebra", true);
            sql.put("permiteperda", true);
            sql.put("codigoanp", "");
            sql.put("impostomedionacional", 0);
            sql.put("impostomedioimportado", 0);
            sql.put("sugestaocotacao", false);
            sql.put("tara", 0.0);
            sql.put("utilizatabelasubstituicaotributaria", false);
            sql.put("id_tipolocaltroca", 0);
            sql.put("qtddiasminimovalidade", 0);
            sql.put("utilizavalidadeentrada", false);
            sql.put("impostomedioestadual", 0);
            sql.put("id_tipocompra", 0);
            sql.put("numeroparcela", 0);
            sql.put("id_tipoembalagemvolume", vo.getTipoEmbalagem().getId());
            sql.put("volume", 1.0);
            sql.put("id_normacompra", vo.getNormaCompra().getId());
            sql.putNull("lastro");
            sql.putNull("camadas");
            sql.put("promocaoauditada", false);
            sql.putNull("substituicaoestadual");
            sql.putNull("substituicaoestadualoutros");
            sql.putNull("substituicaoestadualexterior");
            sql.put("id_cest", vo.getCest() != null ? vo.getCest().getId() : null);
            sql.put("id_normareposicao", vo.getNormaReposicao().getId());
            sql.putNull("lastroreposicao");
            sql.putNull("camadasreposicao");
            sql.putNull("margemminima");
            sql.putNull("margemmaxima");
            sql.put("permitedescontopdv", true);
            sql.put("verificapesopdv", false);
            
            try {
                stm.execute(sql.getInsert());
            } catch (Exception e) {
                throw e;
            }
        }
    }

    public void setManterCodigoDeBalanca(boolean manterCodigoDeBalanca) {
        this.manterCodigoDeBalanca = manterCodigoDeBalanca;
    }
    
    private final ProdutoComplementoDAO complementoDAO = new ProdutoComplementoDAO();
    final ProdutoAutomacaoDAO automacaoDAO = new ProdutoAutomacaoDAO();
    private final ProdutoAliquotaDAO aliquotaDAO = new ProdutoAliquotaDAO();

    public void salvarEAN(List<ProdutoIMP> produtos, Set<OpcaoProduto> opcoes) throws Exception {
        try {
            
            boolean importarMenoresQue7Digitos = opcoes.contains(OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS);
            
            Conexao.begin();
            ProgressBar.setStatus("Produtos - Gravando EAN...");
            ProgressBar.setMaximum(produtos.size());
            for (ProdutoIMP imp: produtos) {
                ProdutoAnteriorVO anterior = getCodigoAnterior().get(
                        imp.getImportSistema(),
                        imp.getImportLoja(),
                        imp.getImportId()
                );
                
                //Se o produto foi localizado executa
                if (anterior != null) {
                    long ean13 = Utils.stringToLong(imp.getEan());
                    if ((!importarMenoresQue7Digitos && ean13 > 999999) && (!Produtoprovider.automacao().cadastrado(ean13))) {
                        //ProdutoAutomacaoVO automacao = prodRepository.converterEAN(imp, ean13, unidade);
                        //automacao.setProduto(anterior.getCodigoAtual());
                        //Produtoprovider.automacao().salvar(automacao);
                        automacaoDAO.salvar(imp, anterior);
                    }
                    //if (ean13 > 999999) {
                    //  automacaoDAO.salvar(imp, anterior);  
                    //}
                }
                ProgressBar.next();
            }
            Conexao.commit();
        } catch (Exception e) {
            Conexao.rollback();
            throw e;
        }
    }
    
    public void salvarEANemBranco() throws Exception {
        automacaoDAO.salvarEansEmBranco();
    }      

    public void apagarProdutos() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(
                    "begin transaction;\n" +
                    "delete from administracaopreco;\n" +
                    "delete from pagarfornecedorparcelaabatimento;\n" +
                    "delete from pagarfornecedorparcela;\n" +
                    "delete from pagarfornecedor;\n" +
                    "delete from escritaitem;\n" +
                    "delete from escrita;\n" +
                    "delete from notaentradadesconto;\n" +
                    "delete from notaentradadivergencia;\n" +
                    "delete from notaentradavencimento;\n" +
                    "delete from notaentradaitem;\n" +
                    "delete from notaentrada;\n" +
                    "delete from notaentradanfe;\n" +
                    "delete from notaentradadivergenciaimpostonfe;\n" +
                    "delete from produtofornecedorcodigoexterno;\n" +
                    "delete from produtofornecedor;\n" +
                    "delete from sped.produtoalteracao;\n" +
                    "delete from pdv.vendafinalizadora;\n" +
                    "delete from pdv.vendaitem;\n" +
                    "delete from pdv.venda;\n" +
                    "delete from logestoque;\n" +
                    "delete from logpreco;\n" +
                    "delete from logcusto;\n" +
                    "delete from emissoretiqueta.logproduto;\n" +
                    "delete from implantacao.codigoanterior;\n" +
                    "delete from produtoautomacao;\n" +
                    "delete from produtoaliquota;\n" +
                    "delete from produtocomplemento;\n" +
                    "delete from produto;\n" +
                    "delete from mercadologico;\n" +
                    "delete from familiaproduto;\n" +
                    "drop table if exists implantacao.codant_familiaproduto;\n" +
                    "drop table if exists implantacao.codant_mercadologico;\n" +
                    "drop table if exists implantacao.codant_produto;\n" +
                    "drop table if exists implantacao.codant_ean;\n" +
                    "alter sequence administracaopreco_id_seq restart with 1;\n" +
                    "alter sequence pagarfornecedorparcelaabatimento_id_seq restart with 1;\n" +
                    "alter sequence pagarfornecedorparcela_id_seq  restart with 1;\n" +
                    "alter sequence pagarfornecedor_id_seq  restart with 1;\n" +
                    "alter sequence escritaitem_id_seq1 restart with 1;\n" +
                    "alter sequence escrita_id_seq  restart with 1;\n" +
                    "alter sequence notaentradadesconto_id_seq restart with 1;\n" +
                    "alter sequence notaentradadivergencia_id_seq  restart with 1;\n" +
                    "alter sequence notaentradavencimento_id_seq restart with 1;\n" +
                    "alter sequence notaentrada_id_seq restart with 1;\n" +
                    "alter sequence notaentradaitem_id_seq restart with 1;\n" +
                    "alter sequence notaentradanfe_id_seq restart with 1;\n" +
                    "alter sequence notaentradadivergenciaimpostonfe_id_seq restart with 1;\n" +
                    "alter sequence produtofornecedorcodigoexterno_id_seq restart with 1;\n" +
                    "alter sequence produtofornecedor_id_seq restart with 1;\n" +
                    "alter sequence sped.produtoalteracao_id_seq restart with 1;\n" +
                    "alter sequence pdv.vendafinalizadora_id_seq restart with 1;\n" +
                    "alter sequence pdv.vendaitem_id_seq restart with 1;\n" +
                    "alter sequence pdv.venda_id_seq restart with 1;\n" +
                    "alter sequence logestoque_id_seq restart with 1;\n" +
                    "alter sequence logpreco_id_seq restart with 1;\n" +
                    "alter sequence logcusto_id_seq restart with 1;\n" +
                    "alter sequence emissoretiqueta.logproduto_id_seq restart with 1;\n" +
                    "alter sequence produto_id_seq restart with 1;\n" +
                    "alter sequence produtoautomacao_id_seq restart with 1;\n" +
                    "alter sequence produtoaliquota_id_seq restart with 1;\n" +
                    "alter sequence produtocomplemento_id_seq restart with 1;\n" +
                    "alter sequence mercadologico_id_seq restart with 1;\n" +
                    "alter sequence familiaproduto_id_seq restart with 1;\n" +
                    "commit;"
            );
        }
    }
}