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
    IMPORTAR_EXCLUIR_NOTAS_EXISTES
}
