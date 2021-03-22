package vrimplantacao2.dao.cadastro.produto2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 * Organiza uma listagem de {@link ProdutoIMP}
 * @author Leandro
 */
public class Organizador {
    
    private static final Logger LOG = Logger.getLogger(Organizador.class.getName());
    
    private final OrganizadorNotifier notifier;
    private final Set<OpcaoProduto> opcoes;
    private final Set<Long> existentes = new HashSet<>();
    private final Set<Long> idsValidos = new HashSet<>();
    
    public Organizador(OrganizadorNotifier repository, Set<OpcaoProduto> opcoes) {
        this.notifier = repository;
        this.opcoes = opcoes;
    }
    
    public Organizador(OrganizadorNotifier repository) {
        this.notifier = repository;
        this.opcoes = new HashSet<>();
    }

    /**
     * Ordenação e validação da listagem de {@link ProdutoIMP}.
     * @param produtos Listade produtos a ser ordenada.
     * @return
     * @throws Exception 
     */
    public List<ProdutoIMP> organizarListagem(List<ProdutoIMP> produtos) throws Exception {
        existentes.clear();
        LOG.info("Organizando a listagem de produtos. Total: " + produtos.size());
        notifier.setNotify("Produtos - Organizando produtos", produtos.size());

        List<ProdutoIMP> filtrados = eliminarDuplicados(produtos);
        produtos.clear();
        System.gc();

        List<ProdutoIMP> balanca = separarProdutosBalanca(filtrados, opcoes.contains(OpcaoProduto.IMPORTAR_MANTER_BALANCA));
        List<ProdutoIMP> manterEAN = separarManterEAN(filtrados, opcoes.contains(OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS));
        List<ProdutoIMP> validos = separarIdsValidos(filtrados);
        
        List<ProdutoIMP> result = new ArrayList<>();
        
        result.addAll(balanca);
        result.addAll(manterEAN);
        result.addAll(validos);
        result.addAll(filtrados);
        
        filtrados.clear();
        balanca.clear();
        manterEAN.clear();
        validos.clear();
        System.gc();
        
        return result;
    }
    
    public List<ProdutoIMP> separarIdsValidos(List<ProdutoIMP> filtrados) {
        MultiMap<String, ProdutoIMP> ids = new MultiMap<>();
        List<ProdutoIMP> outros = new ArrayList<>();
        
        for (ProdutoIMP imp : filtrados) {
            long id;
            try {
                id = Integer.parseInt(imp.getImportId().trim());
            } catch (NumberFormatException ex) {
                outros.add(imp);
                continue;
            }
            
            if (!(id >= 1 && id <= 999999)) {
                outros.add(imp);
                continue;
            }
            
            if (idsValidos.contains(id)) {
                outros.add(imp);
                continue;                
            }
            
            ids.put(imp, imp.getImportId());
            idsValidos.add(id);
        }
        
        filtrados.clear();
        filtrados.addAll(outros);
        System.gc();
        
        return new ArrayList<>(ids.getSortedMap().values());
    }

    /**
     *Serão mantidos:<br>
     * * ManterEAN true no item ou no parâmetro<br>
     * * PLU convertido em número<br>
     * * PLU >= 1 <= 999999<br>
     * * PLU Não existente
     * @param filtrados
     * @param manterEan
     * @return 
     */
    public List<ProdutoIMP> separarManterEAN(List<ProdutoIMP> filtrados, boolean manterEan) {
        List<ProdutoIMP> outros = new ArrayList<>();
        MultiMap<String, ProdutoIMP> manter = new MultiMap<>();
        
        for (ProdutoIMP imp: filtrados) {
            boolean manterEanProduto = imp.isManterEAN() || manterEan;
            long plu;
            String strEan = imp.getEan() == null ? "" : imp.getEan().trim();
            
            //<editor-fold defaultstate="collapsed" desc="Manter o EAN">
            if (!manterEanProduto) {
                outros.add(imp);
                continue;
            }
            //</editor-fold>
            //<editor-fold defaultstate="collapsed" desc="Não pode converter EAN em número">
            try {
                plu = Long.parseLong(strEan);
            } catch (NumberFormatException ex) {
                outros.add(imp);
                continue;
            }
            //</editor-fold>
            //<editor-fold defaultstate="collapsed" desc="PLU inválido">
            if (!(plu >= 1 && plu <= 999999)) {
                outros.add(imp);
                continue;
            }
            //</editor-fold>
            //<editor-fold defaultstate="collapsed" desc="PLU já foi usado">
            if (existentes.contains(plu)) {
                outros.add(imp);
                continue;
            }
            //</editor-fold>
            manter.put(imp, imp.getEan());
            existentes.add(plu);
        }
        
        filtrados.clear();
        filtrados.addAll(outros);
        System.gc();
        
        return new ArrayList<>(manter.getSortedMap().values());
    }
    
    public List<ProdutoIMP> separarProdutosBalanca(List<ProdutoIMP> filtrados, boolean manterBalanca) {
        List<ProdutoIMP> 
                balanca = new ArrayList<>(),
                outros = new ArrayList<>();
        MultiMap<String, ProdutoIMP> validos = new MultiMap<>();
        
        for (ProdutoIMP imp: filtrados) {
            
            String unidade = Utils.acertarTexto(imp.getTipoEmbalagem(), 2, "UN");
            boolean isKilo = "KG".equals(unidade);
            boolean isPesavel = imp.isBalanca();
            boolean vaiParaBalanca = isKilo || isPesavel;            
            String codigo = manterBalanca ? imp.getEan() : imp.getImportId();
            codigo = codigo == null ? "" : codigo.trim();            
            long plu;
            
            //<editor-fold defaultstate="collapsed" desc="Verifica se é possível converter o código em número">
            
            //-456 = OK
            //0001 = OK
            // 25 = OK
            //7891000100103 = OK
            //A2 = NOT OK
            //2 5 = NOT OK
            
            try {
                plu = Long.parseLong(codigo);
            } catch (NumberFormatException ex) {
                outros.add(imp);
                continue;
            }
            //</editor-fold>
            
            //<editor-fold defaultstate="collapsed" desc="Elimina códigos EANs">
            // Se for EAN, automaticamente é eliminado da lista de balança,
            // Produtos com EANs válidos são unitários.
            
            //-456 = OK
            //0001 = OK
            // 25 = OK
            //7891000100103 = NOT OK
            
            long ean = Utils.stringToLong(imp.getEan(), -2);
            if (plu > 999999 || ean > 999999) {
                outros.add(imp);
                continue;
            }
            //</editor-fold>
            
            //<editor-fold defaultstate="collapsed" desc="Elimina códigos menores que 1">
            
            //-456 = NOT OK
            //0001 = OK
            // 25 = OK
            
            if (plu < 1) {
                outros.add(imp);
                continue;
            }
            //</editor-fold>
            
            //<editor-fold defaultstate="collapsed" desc="Se for para balança entra nos válidos, senão é descartado">
            
            //0001 = OK
            // 25 = OK
            if (vaiParaBalanca) {
                if (!existentes.contains(plu)) {
                    validos.put(imp, codigo);
                    existentes.add(plu);
                    idsValidos.add(plu);
                } else {
                    outros.add(imp);
                }
            } else {
                outros.add(imp);
            }
            //</editor-fold>
            
        }
        
        balanca.addAll(validos.getSortedMap().values());
        
        //Remove os produtos de balança da listagem inicial
        filtrados.clear();
        filtrados.addAll(outros);
        System.gc();
        
        return balanca;
    }

    /**
     * Elimina produtos duplicados.
     * @param produtos Listagem de produtos.
     * @return Map filtrado.
     * @throws java.lang.Exception
     */
    public List<ProdutoIMP> eliminarDuplicados(List<ProdutoIMP> produtos) throws Exception {
        LOG.info("Eliminando produtos duplicados");
        notifier.setNotify("Produtos - Eliminando duplicados...", 0);
        MultiMap<String, ProdutoIMP> result = new MultiMap<>();
        for (ProdutoIMP produto : produtos) {
            result.put(produto, produto.getImportId(), produto.getEan());
        }
        return new ArrayList<>(result.values());
    }
    
    public static interface OrganizadorNotifier {
        public void setNotify(String message, int count) throws Exception;
    }
}
