package vrimplantacao2.vo.enums;

/**
 * Opções de importação do fiscal.
 * @author Leandro
 */
public enum OpcaoFiscal {
    NOVOS, 
    USAR_IDPRODUTO, 
    USAR_EAN, 
    UTILIZAR_EANS_MENORES,     
    ALIQUOTA_CREDITO, 
    ALIQUOTA_CREDITO_FORA_ESTADO, 
    ALIQUOTA_DEBITO, 
    ALIQUOTA_DEBITO_FORA_ESTADO, 
    IVA, 
    IVA_AJUSTADO, 
    TIPO_IVA, 
    /**
     * Força a rotina a atualizar as pautas fiscais mesmo se alteradas pelo usuário.
     */
    FORCAR_ALTERACAO;
}
