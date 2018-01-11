package vrimplantacao.vo;

public class FiltroConsultaVO {

    public String alias = "";
    public String campo = "";
    public int idTipoFiltro = 0;
    public String tabela = "";
    public String campoId = "";
    public String campoDescricao = "";
    public int columns = 0;
    public Object oCampo = null;
    public String filtro = "";
    public String complementoCampo = "";

    /**
     *
     * @param i_alias Nome da exibição a tela do filtro
     * @param i_campo Campo da tabela
     * @param i_idTipoFiltro Tipo do filtro
     * @param i_tabela Nome da tabela
     * @param i_campoId ID da tabela
     * @param i_campoDescricao Nome do campo da tabela passado como o 2° parametro
     * @param i_columns Numero de caracteres
     */
    public FiltroConsultaVO(String i_alias, String i_campo, int i_idTipoFiltro, String i_tabela, String i_campoId, String i_campoDescricao, int i_columns, String i_filtro, String i_complementoCampo) {
        alias = i_alias;
        campo = i_campo;
        idTipoFiltro = i_idTipoFiltro;
        tabela = i_tabela;
        campoId = i_campoId;
        campoDescricao = i_campoDescricao;
        columns = i_columns;
        filtro = i_filtro;
        complementoCampo = i_complementoCampo;
    }

    public FiltroConsultaVO(String i_alias, String i_campo, int i_idTipoFiltro, String i_tabela, String i_campoId, String i_campoDescricao, int i_columns, String i_filtro) {
        this(i_alias, i_campo, i_idTipoFiltro, i_tabela, i_campoId, i_campoDescricao, i_columns, i_filtro, "");
    }
}
