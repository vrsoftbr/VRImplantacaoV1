package vrimplantacao2.dao.cadastro.produto;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao2.utils.collection.IDStack;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.utils.multimap.KeyList;
import vrimplantacao2.vo.cadastro.ProdutoAutomacaoVO;
import vrimplantacao2.vo.cadastro.ProdutoVO;
import vrimplantacao2.vo.enums.TipoEmbalagem;

/**
 *
 * @author Leandro
 */
class TratadorProduto {
    
    private final ProdutoDAO produtoDAO;
    private Stack<Integer> idsBalanca;
    private IDStack idsNormaisVagos;
    private Set<Integer> idsExistentes;

    TratadorProduto(ProdutoDAO produtoDAO) {        
        this.produtoDAO = produtoDAO;
    }
    
    MultiMap<String, ProdutoVO> tratarListagem(MultiMap<String, ProdutoVO> convertidos) throws Exception {
        MultiMap<String, ProdutoVO> result = new MultiMap<>();

        if (produtoDAO.isManterCodigoDeBalanca() && produtoDAO.isDuplicarProdutoDeBalanca()) {
            //TODO: Criar rotina para duplicar produtos de balança com mais de um EAN.
        }

        ProgressBar.setStatus("Produtos - Tratando...");
        ProgressBar.setMaximum(convertidos.size());
        for (KeyList<String> keys: convertidos.keySet()) {
            ProdutoVO vo = convertidos.get(keys);

            tratarEans(vo);

            tratarId(vo);

            /*if (vo.getEans().isEmpty()) {
                ProdutoAutomacaoVO automacao = vo.getEans().make((long) vo.getId());
                automacao.setCodigoBarras(vo.getId());
                automacao.setPesoBruto(0);
                automacao.setQtdEmbalagem(1);
                automacao.setTipoEmbalagem(vo.getTipoEmbalagem());
                automacao.setDun14(false);
            }*/
            
            

            result.put(vo, keys);

            ProgressBar.next();
        }  
        
        return result;
    }
    
    MultiMap<String, ProdutoVO> tratarListagemUnificacao(MultiMap<String, ProdutoVO> convertidos) throws Exception {
        MultiMap<String, ProdutoVO> result = new MultiMap<>();
        
        ProgressBar.setStatus("Produtos - Unificação - Tratando...");
        ProgressBar.setMaximum(convertidos.size());
        for (KeyList<String> keys: convertidos.keySet()) {
            ProdutoVO vo = convertidos.get(keys);

            tratarEansUnificacao(vo);

            tratarIdUnificacao(vo);

            result.put(vo, keys);

            ProgressBar.next();
        }
        
        
        
        return result;
    }

    /*
    
        long ean = Utils.stringToLong(imp.getEan());
        //Se o produto for KILO, mas possuir um EAN válido ele é convertido em UN
        if (ean > 999999 && TipoEmbalagem.KG.equals(vo.getTipoEmbalagem())) {
            vo.setTipoEmbalagem(TipoEmbalagem.UN);
            vo.setPesavel(false);
        }
        //</editor-fold>
    
    */
    /*
        for (ProdutoVO vo : result.values()) {
            
        }
    */

    private void tratarId(ProdutoVO vo) throws Exception {
        boolean gerarId = false;
        boolean eBalanca = vo.isPesavel() || TipoEmbalagem.KG.equals(vo.getTipoEmbalagem());
        try {
            if (produtoDAO.isManterCodigoDeBalanca() && eBalanca && !vo.getEans().isEmpty()) {                
                long ean = new ArrayList<>(vo.getEans().values()).get(0).getCodigoBarras();
                if (ean <= 999999) {
                    vo.setId((int) ean);
                }
            }
            if (eBalanca) {
                vo.getEans().clear();
            }
            if (vo.getId() < 1 || vo.getId() > 999999) {
                gerarId = true;
            }
        } catch (NumberFormatException e) {
            gerarId = true;
        }
        if (getIdsExistentes().contains(vo.getId())) {
            gerarId = true;
        }
        
        if (gerarId) {
            if (eBalanca) {
                vo.setId((int) getIdsVagosBalanca().pop());
                getIdsVagosNormais().remove((long) vo.getId());
                getIdsExistentes().add(vo.getId());
            } else {
                vo.setId((int) getIdsVagosNormais().pop());
                getIdsVagosBalanca().remove((Integer) vo.getId());
                getIdsExistentes().add(vo.getId());
            }
        } else {
            if (vo.getId() < 10000) {
                getIdsVagosBalanca().remove((Integer) vo.getId());
            } else {
                getIdsVagosNormais().remove((long) vo.getId());
            }
            getIdsExistentes().add(vo.getId());  
        }
    }
    
    private void tratarEans(ProdutoVO vo) throws Exception {
        if (vo.isPesavel() || TipoEmbalagem.KG.equals(vo.getTipoEmbalagem())) {
            boolean contemEAN = false;
            List<Long> eansInvalidos = new ArrayList<>();
            for (ProdutoAutomacaoVO ean : vo.getEans().values()) {
                contemEAN |= ean.getCodigoBarras() > 999999;
                if (ean.getCodigoBarras() <= 999999) {
                    eansInvalidos.add(ean.getCodigoBarras());
                }
            }
            //Se houver EAN válido transforma o produto em unitário
            if (contemEAN) {
                vo.setPesavel(false);
                vo.setTipoEmbalagem(TipoEmbalagem.UN);
                for (Long ean : eansInvalidos) {
                    vo.getEans().remove(ean);
                }
            }
        } else {
            /**
             * Se um produto não for de balança e seu EAN for menor que 10000
             * (Valor reservado para produtos de balança), remove o EAN da 
             * listagem
             */
            List<ProdutoAutomacaoVO> invalidos = new ArrayList<>();
            for (ProdutoAutomacaoVO ean : vo.getEans().values()) {
                if (ean.getCodigoBarras() < 10000) {
                    invalidos.add(ean);
                }
            }
            for (ProdutoAutomacaoVO ean : invalidos) {
                vo.getEans().remove(ean.getCodigoBarras());
            }
        }
    }
    
    private void tratarIdUnificacao(ProdutoVO vo) throws Exception {
        boolean gerarId = false;
        boolean eBalanca = vo.isPesavel() || TipoEmbalagem.KG.equals(vo.getTipoEmbalagem());
        try {
            if (vo.getEans().isEmpty()) {
                vo.setId(NAO_INCLUIR);
            } else {
                if (vo.getId() < 1 || vo.getId() > 999999) {
                    gerarId = true;
                }
            }
        } catch (NumberFormatException e) {
            gerarId = true;
        }
        
        if (vo.getId() > 0 || gerarId) {
            if (getIdsExistentes().contains(vo.getId())) {
                gerarId = true;
            }

            if (gerarId) {
                if (eBalanca) {
                    vo.setId((int) getIdsVagosBalanca().pop());
                    getIdsVagosNormais().remove((long) vo.getId());
                    getIdsExistentes().add(vo.getId());
                } else {
                    vo.setId((int) getIdsVagosNormais().pop());
                    getIdsVagosBalanca().remove((Integer) vo.getId());
                    getIdsExistentes().add(vo.getId());
                }
            } else {
                if (vo.getId() < 10000) {
                    getIdsVagosBalanca().remove((Integer) vo.getId());
                } else {
                    getIdsVagosNormais().remove((long) vo.getId());
                }
                getIdsExistentes().add(vo.getId());  
            }
        }
    }
    public static final int NAO_INCLUIR = -3;
    
    private void tratarEansUnificacao(ProdutoVO vo) throws Exception {
        if (vo.isPesavel() || TipoEmbalagem.KG.equals(vo.getTipoEmbalagem())) {
            boolean contemEAN = false;
            for (ProdutoAutomacaoVO ean : vo.getEans().values()) {
                contemEAN |= ean.getCodigoBarras() > 999999;
            }
            //Se houver EAN válido transforma o produto em unitário
            if (contemEAN) {
                vo.setPesavel(false);
                vo.setTipoEmbalagem(TipoEmbalagem.UN);
            }
        } 

        List<ProdutoAutomacaoVO> invalidos = new ArrayList<>();
        for (ProdutoAutomacaoVO ean : vo.getEans().values()) {
            if (ean.getCodigoBarras() <= 999999) {
                invalidos.add(ean);
            }
        }
        for (ProdutoAutomacaoVO ean : invalidos) {
            vo.getEans().remove(ean.getCodigoBarras());
        }
        
        //Se o primeiro EAN já existir, coloca um id
    }

    
    /**
     * Retorna um {@link Set} com todos os IDs.
     * @return
     * @throws Exception
     */
    public Set<Integer> getIdsExistentes() throws Exception {
        if (idsExistentes == null) {
            atualizaIdsExistentes();
        }
        return idsExistentes;
    }
    
    /**
     * Atualiza a listagem de Ids existentes.
     * @throws Exception 
     */
    public void atualizaIdsExistentes() throws Exception {
        idsExistentes = new LinkedHashSet<>();
        try (final Statement stm = Conexao.createStatement()) {
            try (final ResultSet rst = stm.executeQuery("select id from produto order by id")) {
                while (rst.next()) {
                    idsExistentes.add(rst.getInt("id"));
                }
            }
        }
    }

    /**
     * Retorna uma {@link Stack} com os IDs vagos para produtos de balança.
     * @return IDs vagos para produtos de balança.
     * @throws Exception
     */
    public Stack<Integer> getIdsVagosBalanca() throws Exception {
        if (idsBalanca == null) {
            atualizarIdsVagosBalanca();
        }
        return idsBalanca;
    }

    /**
     * Retorna uma {@link Stack} com os IDs vagos para produtos que NÃO são de balança.
     * @return IDs vagos para produtos que NÃO são de balança.
     * @throws Exception
     */
    public IDStack getIdsVagosNormais() throws Exception {
        if (idsNormaisVagos == null) {
            atualizarIdsVagosNormais();
        }
        return idsNormaisVagos;
    }

    private void atualizarIdsVagosBalanca() throws Exception {
        idsBalanca = new Stack<>();
        try (final Statement stm = Conexao.createStatement()) {
            try (final ResultSet rst = stm.executeQuery("SELECT id from \n" + "(SELECT id FROM generate_series(1, 10000)\n" + "AS s(id) EXCEPT SELECT id FROM produto WHERE id <= 9999) AS codigointerno ORDER BY id desc")) {
                while (rst.next()) {
                    idsBalanca.add(rst.getInt("id"));
                }
            }
        }
    }

    private void atualizarIdsVagosNormais() throws Exception {
        idsNormaisVagos = new IDStack();
        try (final Statement stm = Conexao.createStatement()) {
            try (final ResultSet rst = stm.executeQuery("SELECT id from \n" + "(SELECT id FROM generate_series(10000, 999999)\n" + "AS s(id) EXCEPT SELECT id FROM produto WHERE id > 9999) AS codigointerno ORDER BY id desc")) {
                while (rst.next()) {
                    int id = rst.getInt("id");
                    idsNormaisVagos.add(id);
                }
            }
        }
    }

}
