package vrimplantacao2.dao.cadastro.produto2;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.utils.multimap.KeyList;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 * Organiza uma listagem de {@link ProdutoIMP}
 * @author Leandro
 */
public class Organizador {
    
    private static final Logger LOG = Logger.getLogger(Organizador.class.getName());
    
    private final ProdutoRepository repository;
    
    public Organizador(ProdutoRepository repository) {
        this.repository = repository;
    }

    /**
     * Ordenação e validação da listagem de {@link ProdutoIMP}.
     * @param produtos Listade produtos a ser ordenada.
     * @return
     * @throws Exception 
     */
    public List<ProdutoIMP> organizarListagem(List<ProdutoIMP> produtos) throws Exception {
        LOG.info("Organizando a listagem de produtos. Total: " + produtos.size());
        repository.setNotify("Produtos - Organizando produtos", produtos.size());
        MultiMap<String, ProdutoIMP> result = new MultiMap<>();
        MultiMap<String, ProdutoIMP> balanca = new MultiMap<>();
        MultiMap<String, ProdutoIMP> normais = new MultiMap<>();
        MultiMap<String, ProdutoIMP> manterEAN = new MultiMap<>();
        
        {
            List<ProdutoIMP> filtrados = eliminarDuplicados(produtos);
            separarBalancaNormaisManterEAN(
                    filtrados,
                    balanca,
                    normais,
                    manterEAN
            );            
            filtrados.clear();
            produtos.clear();
            System.gc();
        }
        
        if (repository.getOpcoes().contains(OpcaoProduto.IMPORTAR_RESETAR_BALANCA)) {
            //Listagem para os produtos de balança com PLUs válidos.
            MultiMap<String, ProdutoIMP> validos = new MultiMap<>();
            //Listagem para os produtos de balança com PLUs inválidos
            MultiMap<String, ProdutoIMP> invalidos = new MultiMap<>();
            //Separando os produtos e análisando o PLU.
            for (KeyList<String> keys : normais.keySet()) {
                ProdutoIMP imp = normais.get(keys);
                try {
                    int id = Integer.parseInt(imp.getImportId());
                    if (id >= 10000 && id <= 999999) {
                        validos.put(imp, keys);
                    } else {
                        invalidos.put(imp, keys);
                    }
                } catch (NumberFormatException e) {
                    invalidos.put(imp, keys);
                } 
            }
            //Ordenando as listagtem.
            balanca = balanca.getSortedMap();
            validos = validos.getSortedMap();
            invalidos = invalidos.getSortedMap();
            //Reorganizando os produtos e ordenando a listagem           
            normais.clear();
            for (ProdutoIMP prod : validos.values()) {
                normais.put(prod, prod.getImportSistema(), prod.getImportLoja(), prod.getImportId(), prod.getEan());
            }
            for (ProdutoIMP prod : invalidos.values()) {
                normais.put(prod, prod.getImportSistema(), prod.getImportLoja(), prod.getImportId());
            }
            
            validos.clear();
            invalidos.clear();
        } else if (repository.getOpcoes().contains(OpcaoProduto.IMPORTAR_MANTER_BALANCA)) {
            
            /**
            * Se optar por manter os códigos dos produtos de balança entra neste if.
            */
            
            //Listagem para os produtos de balança com PLUs válidos.
            MultiMap<String, ProdutoIMP> validos = new MultiMap<>();
            //Listagem para os produtos de balança com PLUs inválidos
            MultiMap<String, ProdutoIMP> invalidos = new MultiMap<>();
            //Separando os produtos e análisando o PLU.
            for (KeyList<String> keys : balanca.keySet()) {
                ProdutoIMP imp = balanca.get(keys);
                long ean = Utils.stringToLong(imp.getEan());
                String[] chave = new String[]{imp.getImportSistema(), imp.getImportLoja(), imp.getImportId(), imp.getEan()};//String.valueOf(ean)};
                if (ean >= 0 && ean <= 999999) {
                    validos.put(imp, chave);
                } else {
                    invalidos.put(imp, chave);
                }
            }
            //Ordenando as listagtem.
            validos = validos.getSortedMap();
            invalidos = invalidos.getSortedMap();
            //Reorganizando os produtos e ordenando a listagem           
            balanca.clear();
            for (ProdutoIMP prod : validos.values()) {
                balanca.put(prod, prod.getImportSistema(), prod.getImportLoja(), prod.getImportId(), prod.getEan());
            }
            for (ProdutoIMP prod : invalidos.values()) {
                balanca.put(prod, prod.getImportSistema(), prod.getImportLoja(), prod.getImportId());
            }
            
            validos.clear();
            invalidos.clear();
        }
        
        
        if (!manterEAN.isEmpty()) {
            /**
            * Tratamento dos produtos que forçam o manter o EAN.
            */

            //Listagem para os produtos de balança com PLUs válidos.
            MultiMap<String, ProdutoIMP> validos = new MultiMap<>();
            //Listagem para os produtos de balança com PLUs inválidos
            MultiMap<String, ProdutoIMP> invalidos = new MultiMap<>();
            //Separando os produtos e análisando o PLU.
            for (KeyList<String> keys : manterEAN.keySet()) {
                ProdutoIMP imp = manterEAN.get(keys);
                long ean = Utils.stringToLong(imp.getEan());
                String[] chave = new String[]{imp.getImportSistema(), imp.getImportLoja(), imp.getImportId(), imp.getEan()};//String.valueOf(ean)};
                if (ean >= 0 && ean <= 999999) {
                    validos.put(imp, chave);
                } else {
                    invalidos.put(imp, chave);
                }
            }
            //Ordenando as listagtem.
            validos = validos.getSortedMap();
            invalidos = invalidos.getSortedMap();
            //Reorganizando os produtos e ordenando a listagem           
            manterEAN.clear();
            for (ProdutoIMP prod : validos.values()) {
                manterEAN.put(prod, prod.getImportSistema(), prod.getImportLoja(), prod.getImportId(), prod.getEan());
            }
            //Aqueles com código EAN inválido é colocado com os normais para receber uma nova numeração e o manter
            //Código é desmarcado
            for (ProdutoIMP prod : invalidos.values()) {
                normais.put(prod, prod.getImportSistema(), prod.getImportLoja(), prod.getImportId());
            }

            validos.clear();
            invalidos.clear();
        }
            
        LOG.fine("QTDs Balanca: " + balanca.size() + "|Normais: " + normais.size());
        for (ProdutoIMP produto : balanca.values()) {
            String[] chave = new String[]{produto.getImportSistema(), produto.getImportLoja(), produto.getImportId(), produto.getEan()};
            result.put(produto, chave);
        }
        for (ProdutoIMP produto : manterEAN.values()) {
            String[] chave = new String[]{produto.getImportSistema(), produto.getImportLoja(), produto.getImportId(), produto.getEan()};
            result.put(produto, chave);
        }
        for (ProdutoIMP produto : normais.values()) {
            String[] chave = new String[]{produto.getImportSistema(), produto.getImportLoja(), produto.getImportId(), produto.getEan()};
            result.put(produto, chave);
        }
        return new ArrayList<>(result.values());
    }

    /**
     * Elimina produtos duplicados.
     * @param produtos Listagem de produtos.
     * @return Map filtrado.
     * @throws java.lang.Exception
     */
    public List<ProdutoIMP> eliminarDuplicados(List<ProdutoIMP> produtos) throws Exception {
        LOG.info("Eliminando produtos duplicados");
        repository.setNotify("Produtos - Eliminando duplicados...", 0);
        MultiMap<String, ProdutoIMP> result = new MultiMap<>();
        for (ProdutoIMP produto : produtos) {
            result.put(produto, produto.getImportId(), produto.getEan());
        }
        return new ArrayList<>(result.values());
    }

    /**
     * Pega o map de produtos e separa entre balança e normais.
     * @param filtrados
     * @param balanca
     * @param normais
     * @throws java.lang.Exception
     */
    public void separarBalancaNormaisManterEAN(List<ProdutoIMP> filtrados, MultiMap<String, ProdutoIMP> balanca, MultiMap<String, ProdutoIMP> normais, MultiMap<String, ProdutoIMP> manterEAN) throws Exception {
        repository.setNotify("Produtos - Separando balan\u00e7a e normais...", 0);
        boolean isManterEAN = repository.getOpcoes().contains(OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS);
                
        for (ProdutoIMP produto : filtrados) {
            String[] chave = null;
            if (produto.isBalanca() && (repository.getOpcoes().contains(OpcaoProduto.IMPORTAR_RESETAR_BALANCA))) {
                chave = new String[]{produto.getImportSistema(), produto.getImportLoja(), produto.getDescricaoCompleta(), produto.getImportId(), produto.getEan()};
            } else {
                chave = new String[]{produto.getImportSistema(), produto.getImportLoja(), produto.getImportId(), produto.getEan()};                                
            }
            
            long ean = Utils.stringToLong(produto.getEan());
            String un = Utils.acertarTexto(produto.getTipoEmbalagem(), 2);
            if (ean >= 1 && ean <= 999999 && (produto.isBalanca() || ("KG".equals(un != null ? un.toUpperCase() : "UN")))) {
                balanca.put(produto, chave);
            } else if (ean >= 1 && ean <= 999999 && (produto.isManterEAN() || isManterEAN)) {
                manterEAN.put(produto, chave);
            } else {
                normais.put(produto, chave);
            }
        }
    }

    private List<ProdutoIMP> tratarProdutosBalanca(List<ProdutoIMP> balanca) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private List<ProdutoIMP> tratarManterEAN(List<ProdutoIMP> manterEAN) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private List<ProdutoIMP> tratarNormais(List<ProdutoIMP> normais) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
