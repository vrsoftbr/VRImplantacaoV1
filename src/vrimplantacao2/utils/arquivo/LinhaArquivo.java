package vrimplantacao2.utils.arquivo;

import java.sql.Time;
import java.util.Date;

/**
 * Esta interface representa uma linha de um arquivo.
 * @author Leandro
 */
public interface LinhaArquivo {
    /**
     * Retorna o valor de um campo na forma de {@link String}.
     * @param campo Nome do campo a ser localizado.
     * @return Valor do campo ou null caso não encontrado.
     */
    public String getString(String campo);
    /**
     * Inclui uma {@link String} no campo informado.
     * @param campo Nome do campo onde será inclusa a informação.
     * @param contents Conteúdo do campo.
     */
    public void putString(String campo, String contents);
    
    /**
     * Retorna o valor de um campo na forma de {@link Integer}.
     * @param campo Nome do campo a ser localizado.
     * @return Valor do campo ou 0 caso não encontrado.
     */
    public int getInt(String campo);
    
    /**
     * Inclui um {@link Integer} no campo informado.
     * @param campo Nome do campo onde será inclusa a informação.
     * @param contents Conteúdo do campo.
     */
    public void putInt(String campo, int contents);
    
    /**
     * Retorna o valor de um campo na forma de {@link Double}.
     * @param campo Nome do campo a ser localizado.
     * @return Valor do campo ou 0 caso não encontrado.
     */
    public double getDouble(String campo);
    
    /**
     * Inclui um {@link Double} no campo informado.
     * @param campo Nome do campo onde será inclusa a informação.
     * @param contents Conteúdo do campo.
     */
    public void putDouble(String campo, double contents);
    
    /**
     * Retorna o valor de um campo na forma de {@link Date}.
     * @param campo Nome do campo a ser localizado.
     * @return Valor do campo ou null caso não encontrado.
     */
    public Date getData(String campo);
    
    /**
     * Inclui um {@link Date} no campo informado.
     * @param campo Nome do campo onde será inclusa a informação.
     * @param contents Conteúdo do campo.
     */
    public void putData(String campo, Date contents);

    /**
     * Informa se uma determinada coluna existe.
     * @param string Nome da coluna.
     * @return True se coluna existir ou False caso contrário.
     */
    public boolean existsColumn(String string);
    
    /**
     * Retorna o valor do campo como Data
     * @param campo Campo a ser retornado.
     * @return Valor encontrado.
     */
    public Time getTime(String campo);
    
    /**
     * Inclui um {@link Time} no campo informado.
     * @param campo Nome do campo onde será inclusa a informação.
     * @param contents Conteúdo do campo.
     */
    public void putTime(String campo, Time contents);
    
    /**
     * Inclui um boolean no campo informado.
     * @param campo Nome do campo onde será inclusa a informação.
     * @param contents Conteúdo do campo.
     */
    public void putBoolean(String campo, boolean contents);
    
    /**
     * Retorna um campo no formato boolean.
     * @param campo Campo a ser retornado.
     * @return Valor encontrado. 
     */
    public boolean getBoolean(String campo);

    public Object get(String string);
    
}
