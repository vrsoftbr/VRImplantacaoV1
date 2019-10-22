package vrimplantacao2.dao.cadastro.notafiscal;

/**
 * Opções de importação das notas fiscais.
 * @author Leandro
 */
public enum OpcaoNotaFiscal {
    /**
     * Se a nota fiscal a ser importada, existir no VR, ela é excluida para ser
     * reimportada, ao invés de pular a nota.
     */
    IMP_EXCLUIR_NOTAS_EXISTENTES, 
    /**
     * Como a {@link #IMP_EXCLUIR_NOTAS_EXISTENTES} porém só eliminas as notas
     * importadas pelo VRImplantação (Notas presentes na implantacao.codant_notafiscal).
     */
    IMP_EXCLUIR_NOTAS_EXISTENTES_IMPORTADAS,
    /**
     * Caso uma nota fiscal já exista, a rotina irá apagar e reimportar os itens
     * da nota além de atualizar o total dela.
     */
    IMP_REIMPORTAR_ITENS_DE_NOTAS_EXISTENTES
    
}
