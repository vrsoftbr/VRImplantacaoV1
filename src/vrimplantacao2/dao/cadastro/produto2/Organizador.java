package vrimplantacao2.dao.cadastro.produto2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    private final Set<Long> existentes = new HashSet<>();
    private final Set<Long> idsValidos = new HashSet<>();
    
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
        existentes.clear();
        LOG.info("Organizando a listagem de produtos. Total: " + produtos.size());
        repository.setNotify("Produtos - Organizando produtos", produtos.size());
        MultiMap<String, ProdutoIMP> result = new MultiMap<>();
        MultiMap<String, ProdutoIMP> balanca = new MultiMap<>();
        MultiMap<String, ProdutoIMP> normais = new MultiMap<>();
        MultiMap<String, ProdutoIMP> manterEAN = new MultiMap<>();
        
        {
            List<ProdutoIMP> filtrados = eliminarDuplicados(produtos);
            
            List<ProdutoIMP> bal = separarProdutosBalanca(filtrados, repository.getOpcoes().contains(OpcaoProduto.IMPORTAR_MANTER_BALANCA));
            List<ProdutoIMP> manEAN = separarManterEAN(filtrados, repository.getOpcoes().contains(OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS));
            List<ProdutoIMP> normaisComEAN = separarIdsValidos(filtrados);
            List<ProdutoIMP> invalidos = filtrados;
            
            separarBalancaNormaisManterEAN(
                    filtrados,
                    balanca,
                    normais,
                    manterEAN
            );            
            filtrados.clear();
            produtos.clear();
            System.gc();
        }/*
        
        List<ProdutoIMP> resultado = new ArrayList<>();
        resultado.addAll(tratarProdutosBalanca(balanca));
        resultado.addAll(tratarManterEAN(manterEAN));
        resultado.addAll(tratarNormais(normais));
        
        return resultado;*/
        
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
        MultiMap<String, ProdutoIMP>
                validos = new MultiMap<>(),
                invalidos = new MultiMap<>();
        
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
                if (manterBalanca && vaiParaBalanca) {
                    invalidos.put(imp, codigo);
                } else {
                    outros.add(imp);
                }
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
                if (vaiParaBalanca && manterBalanca) {
                    invalidos.put(imp, codigo);
                } else {
                    outros.add(imp);
                }
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
                    if (manterBalanca) {
                        invalidos.put(imp, codigo);
                    } else {
                        outros.add(imp);
                    }
                }
            } else {
                outros.add(imp);
            }
            //</editor-fold>
            
        }
        
        balanca.addAll(validos.getSortedMap().values());
        balanca.addAll(invalidos.getSortedMap().values());
        
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
        repository.setNotify("Produtos - Separando balança, eansMenores e normais...", 0);
        boolean isManterEAN = repository.getOpcoes().contains(OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS);
                
        for (ProdutoIMP produto : filtrados) {
            long ean = Utils.stringToLong(produto.getEan(), -2);
            String unidade = Utils.acertarTexto(produto.getTipoEmbalagem(), 2, "UN");
            boolean isPLU = ean >= 1 && ean <= 999999;
            //Verificar se é produto de balança
            
            //Verificar se é produto bazar hortfrut (EAN <= 999999)
            //Verificar se é produto normal
           
            
            
            /*
            
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
            }*/
        }
    }
    
}
