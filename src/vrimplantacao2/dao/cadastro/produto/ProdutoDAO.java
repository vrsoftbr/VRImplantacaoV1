package vrimplantacao2.dao.cadastro.produto;

import java.sql.Statement;
import java.util.List;
import java.util.Set;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.produto2.ProdutoRepositoryProvider;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.ProdutoAnteriorVO;
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

    public void setManterCodigoDeBalanca(boolean manterCodigoDeBalanca) {
        this.manterCodigoDeBalanca = manterCodigoDeBalanca;
    }
    
    final ProdutoAutomacaoDAO automacaoDAO = new ProdutoAutomacaoDAO();

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