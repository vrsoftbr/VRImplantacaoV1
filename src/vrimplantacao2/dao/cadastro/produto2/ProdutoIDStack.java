package vrimplantacao2.dao.cadastro.produto2;

import java.util.Set;
import java.util.logging.Logger;
import vrimplantacao2.utils.collection.IDStack;

/**
 * Classe que gerencia as pilhas de IDs disponíveis para utilizar nos produtos.
 * @author Leandro
 */
public class ProdutoIDStack {
    
    private static final Logger LOG = Logger.getLogger(ProdutoIDStack.class.getName());

    private IDStack balanca;
    private IDStack normais;
    private Set<Integer> cadastrados;
    private final ProdutoIDStackProvider provider;

    public ProdutoIDStack(ProdutoIDStackProvider provider) throws Exception {
        this.provider = provider;
        updateStacks();
    }

    /**
     * Atualiza a listagem dos IDs disponíveis.
     * @throws Exception 
     */
    public final void updateStacks() throws Exception {
        if (balanca != null) {
            balanca.clear();
        }
        if (normais != null) {
            normais.clear();
        }
        if (cadastrados != null) {
            cadastrados.clear();
        }
        
        //Aciona o Garbage Collector para limpar a memória.
        System.gc();
        
        balanca = provider.getIDsVagosBalanca();
        normais = provider.getIDsVagosNormais();
        cadastrados = provider.getIDsCadastrados();
    }
    
    /**
     * <p>&nbsp;&nbsp;Analisa o ID informado, verificando se é válido (Número 
     * inteiro maior que 0 e menor que 1000000) e se não está cadastrado.</p>
     * <p>&nbsp;&nbsp;Se o número informado atender as exigências ele é 
     * retornado liberando o ID para uso, senão um novo ID é gerado de acordo 
     * com o parâmetro eBalanca (de 0 até 9999 para produtos de balanca e de 
     * 10000 até 999999 para normais).</p>
     * @param strID ID anterior do produto para ser validado.
     * @param eBalanca se o produto é ou não de balança.
     * @return ID convertido em {@link Integer} se for válido ou um novo ID;
     */
    public int obterID(String strID, boolean eBalanca) {
        StringBuilder rep = new StringBuilder();
        try {
            boolean gerarID = false;
            int id = -1;
            try {
                //Tenta converter o ID em número.
                long temp = Long.parseLong(strID);
                //Caso o ID seja menor que 1 ou maior que 999999, então um novo id é gerado.
                if (temp >= 1 && temp <= 999999) {
                    id = (int) temp;
                    //Caso o ID já esteja cadastrado, então um novo ID é gerado.
                    if (cadastrados.contains(id)) {
                        gerarID = true;
                        rep.append("01|Id existente");
                    }                
                } else {
                    gerarID = true;
                    rep.append("01|Id fora do intervalo permitido");
                }
            } catch (NumberFormatException e) {
                //se não for possível converter, gera um novo ID.
                gerarID = true;
                rep.append("01|Id inválido");
            }

            if (gerarID) {
                if (balanca.isEmpty()) {
                    balanca = normais;
                }
                if (eBalanca) {
                    id = (int) balanca.pop();
                    normais.remove((long) id);
                    cadastrados.add(id);
                } else {
                    id = (int) normais.pop();
                    balanca.remove((long) id);
                    cadastrados.add(id);
                }
                rep.append("02|IdAnterior: ").append(strID).append(" idGerado: ").append(id);
            } else {
                if (id < 10000) {
                    balanca.remove((long) id);
                } else {
                    normais.remove((long) id);
                }
                cadastrados.add(id);
                rep.append("02|Id ").append(id).append(" disponível");
            }
            LOG.finest(rep.toString());
            return id;
        } catch (Exception ex) {
            LOG.severe(rep.toString());
            throw ex;
        }
    }
    
    public boolean isIdCadastrado(int id) {
        return cadastrados.contains(id);
    }

}
