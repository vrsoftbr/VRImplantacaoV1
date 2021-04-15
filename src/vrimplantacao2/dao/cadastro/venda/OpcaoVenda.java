package vrimplantacao2.dao.cadastro.venda;

/**
 * Opções para a importação da venda.
 * @author Leandro
 */
public enum OpcaoVenda {
    /**
     * Esta opção determina que a verificação dos códigos dos produtos será feita
     * através do código anterior armazenado na tabela implantacao.codant_produto.
     */
    IMPORTAR_POR_CODIGO_ANTERIOR, 
    /**
     * Determina que a verificação dos produtos será feita através dos EAN anterior deles.
     */
    IMPORTAR_POR_EAN_ANTERIOR,
    /**
     * Determina que a verificação dos produtos será feita através dos EAN atual deles.
     */
    IMPORTAR_POR_EAN_ATUAL,
    /**
     * Caso o produto não seja encontrado na venda, será utilizado um item padrão para importar.
     */
    UTILIZAR_ITEM_PADRAO,
    /**
     * Não elimina as vendas encontradas, apenas atualiza seu custo
     */
    ATUALIZAR_CUSTOS
}
