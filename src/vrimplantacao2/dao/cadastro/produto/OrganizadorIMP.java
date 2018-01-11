package vrimplantacao2.dao.cadastro.produto;

import java.util.ArrayList;
import java.util.List;
import vrframework.classe.ProgressBar;
import vrimplantacao.utils.Utils;
import vrimplantacao2.utils.multimap.KeyList;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Leandro
 */
public class OrganizadorIMP {
    private final ProdutoDAO produtoDAO;
    
    public OrganizadorIMP(ProdutoDAO produtoDAO) {
        this.produtoDAO = produtoDAO;
    }

    /**
     * Pega a listagem filtrada de produtos e verifica se os Ids são números inteiros
     * válidos (de 1 à 999999) e ordena a lista colocando os válidos primeiros (que manterão o
     * id) e os inválidos depois (por que será necessário gerar um novo código
     * para eles).
     * @param filtrados listagem dos produtos filtrados.
     * @throws java.lang.Exception
     */
    public void organizarIds(MultiMap<String, ProdutoIMP> filtrados) throws Exception {
        ProgressBar.setStatus("Produtos - Organizando e filtrando...");
        ProgressBar.setMaximum(filtrados.size());
        
        List<ProdutoIMP> idsValidos = new ArrayList<>();
        List<ProdutoIMP> idsInvalidos = new ArrayList<>();
        for (ProdutoIMP imp : filtrados.getSortedMap().values()) {
            try {
                int id = Integer.parseInt(imp.getImportId());
                if (id >= 1 && id <= 999999) {
                    idsValidos.add(imp);
                } else {
                    idsInvalidos.add(imp);
                }
            } catch (NumberFormatException e) {
                idsInvalidos.add(imp);
            }
        }
        filtrados.clear();
        for (ProdutoIMP produto : idsValidos) {
            String[] chave = new String[]{produto.getImportSistema(), produto.getImportLoja(), produto.getImportId(), produto.getEan()};
            filtrados.put(produto, chave);
            ProgressBar.next();
        }
        for (ProdutoIMP produto : idsInvalidos) {
            String[] chave = new String[]{produto.getImportSistema(), produto.getImportLoja(), produto.getImportId(), produto.getEan()};
            filtrados.put(produto, chave);
            ProgressBar.next();
        }
    }

    /**
     * Ordenação e validação da listagem de {@link ProdutoIMP}.
     * @param produtos Listade produtos a ser ordenada.
     * @return
     * @throws Exception 
     */
    public MultiMap<String, ProdutoIMP> organizarListagem(List<ProdutoIMP> produtos) throws Exception {
        ProgressBar.setStatus("Produtos - Organizando produtos");
        MultiMap<String, ProdutoIMP> result = new MultiMap<>();
        MultiMap<String, ProdutoIMP> balanca = new MultiMap<>();
        MultiMap<String, ProdutoIMP> normais = new MultiMap<>();
        
        {
            MultiMap<String, ProdutoIMP> filtrados = eliminarDuplicados(produtos);
            organizarIds(filtrados);
            separarBalancaENormais(filtrados, balanca, normais);
            
            filtrados.clear();
        }
        
        /**
         * Se optar por manter os códigos dos produtos de balança entra neste if.
         */
        if (produtoDAO.isManterCodigoDeBalanca()) {
            //Listagem para os produtos de balança com PLUs válidos.
            MultiMap<String, ProdutoIMP> validos = new MultiMap<>(3);
            //Listagem para os produtos de balança com PLUs inválidos
            MultiMap<String, ProdutoIMP> invalidos = new MultiMap<>(3);
            //Separando os produtos e análisando o PLU.
            for (KeyList<String> keys : balanca.keySet()) {
                ProdutoIMP imp = balanca.get(keys);
                long ean = Utils.stringToLong(imp.getEan());
                String[] chave = new String[]{imp.getImportSistema(), imp.getImportLoja(), String.valueOf(ean)};
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
        for (ProdutoIMP produto : balanca.values()) {
            String[] chave = new String[]{produto.getImportSistema(), produto.getImportLoja(), produto.getImportId(), produto.getEan()};
            result.put(produto, chave);
        }
        for (ProdutoIMP produto : normais.values()) {
            String[] chave = new String[]{produto.getImportSistema(), produto.getImportLoja(), produto.getImportId(), produto.getEan()};
            result.put(produto, chave);
        }
        return result;
    }

    /**
     * Elimina produtos duplicados.
     * @param produtos Listagem de produtos.
     * @return Map filtrado.
     * @throws java.lang.Exception
     */
    public MultiMap<String, ProdutoIMP> eliminarDuplicados(List<ProdutoIMP> produtos) throws Exception {
        ProgressBar.setStatus("Produtos - Eliminando duplicados...");
        MultiMap<String, ProdutoIMP> result = new MultiMap<>();
        for (ProdutoIMP produto : produtos) {
            if (
                    !produtoDAO.getImportSistema().equals(produto.getImportSistema()) ||
                    !produtoDAO.getImportLoja().equals(produto.getImportLoja())
            ) {
                continue;
            }
            String[] chave = new String[]{produto.getImportSistema(), produto.getImportLoja(), produto.getImportId(), produto.getEan()};
            result.put(produto, chave);
        }
        return result;
    }

    /**
     * Pega o map de produtos e separa entre balança e normais.
     * @param filtrados
     * @param balanca
     * @param normais
     * @throws java.lang.Exception
     */
    public void separarBalancaENormais(MultiMap<String, ProdutoIMP> filtrados, MultiMap<String, ProdutoIMP> balanca, MultiMap<String, ProdutoIMP> normais) throws Exception {
        ProgressBar.setStatus("Produtos - Separando balan\u00e7a e normais...");
        for (ProdutoIMP produto : filtrados.values()) {
            String[] chave = new String[]{produto.getImportSistema(), produto.getImportLoja(), produto.getImportId(), produto.getEan()};
            long ean = Utils.stringToLong(produto.getEan());
            if (ean >= 1 && ean <= 999999 && produto.isBalanca()) {
                balanca.put(produto, chave);
            } else {
                normais.put(produto, chave);
            }
        }
    }
    
}
