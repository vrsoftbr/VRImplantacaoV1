package vrimplantacao2.utils.arquivo;

import java.util.Iterator;
import java.util.List;

/**
 * Classe abstrata para facilitar a introdução de novos formatos de arquivo.
 * @author Leandro
 */
public abstract class AbstractArquivo implements Arquivo {
    
    /**
     * Listagem dos cabeçalhos.
     */
    protected List<String> cabecalho;
    /**
     * Linhas do arquivo.
     */
    protected List<LinhaArquivo> dados;

    @Override
    public List<String> getCabecalho() {
        return cabecalho;
    }

    @Override
    public List<LinhaArquivo> getDados() {
        return dados;
    }    

    @Override
    public Iterator<LinhaArquivo> iterator() {
        return dados.iterator();
    }

    @Override
    public int qtdLinhas() {
        return dados.size();
    }
    
}
