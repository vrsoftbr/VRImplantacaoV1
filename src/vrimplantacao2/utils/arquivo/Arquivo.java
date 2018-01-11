package vrimplantacao2.utils.arquivo;

import java.util.List;

/**
 * Interface que representa um arquivo texto para leitura, cuja a primeira linha
 * é o cabeçalho.
 * @author Leandro
 */
public interface Arquivo extends Iterable<LinhaArquivo> {
    /**
     * Retorna o cabeçalho do arquivo;
     * @return Lista com o cabeçalho.
     */
    public List<String> getCabecalho();
    
    /**
     * Retorna uma listagem com as linhas do arquivo.
     * @return List com as linhas.
     */
    public List<LinhaArquivo> getDados();
    
    /**
     * Retorna a quantidade total de linhas.
     * @return quantidade de linhas do arquivo.
     */
    public int qtdLinhas();
}
