package vrimplantacao2_5.vo.enums;

/**
 *
 * @author guilhermegomes
 */
public enum EUnidade {
    
    VR_MATRIZ(1, "VR SOFTWARE"),
    VR_BAURU(2, "UNIDADE BAURU"),
    VR_BELEM(3, "UNIDADE BELÉM"),
    VR_FLORIANOPOLIS(4, "UNIDADE FLORIANÓPOLIS"),
    VR_FORTALEZA(5, "UNIDADE FORTALEZA"),
    VR_GOIANIA(6, "UNIDADE GOIÂNIA"),
    VR_RECIFE(7, "UNIDADE RECIFE"),
    VR_RIO(8, "UNIDADE RIO DE JANEIRO"),
    VR_SALVADOR(9, "UNIDADE BAHIA"),
    VR_SAO_PAULO(10, "UNIDADE SÃO PAULO"),
    VR_SAO_PAULO_ZL(11, "UNIDADE SÃO PAULO ZONA LESTE"),
    VR_UBERLANDIA(12, "UNIDADE MINAS GERAIS"),
    VR_RIBEIRAPRETO(13, "UNIDADE RIBEIRAO PRETO");
    
    private int id;
    private String nome;

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }
    
    public void setId(int id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    private EUnidade(int id, String nome) {
        this.id = id;
        this.nome = nome;
    }
    
    EUnidade() {}
    
    public static EUnidade getById(int id) {
        for (EUnidade st: values()) {
            if (st.getId() == id) {
                return st;
            }
        }
        return null;
    }
}
