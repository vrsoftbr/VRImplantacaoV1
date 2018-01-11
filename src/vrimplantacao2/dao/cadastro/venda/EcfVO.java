package vrimplantacao2.dao.cadastro.venda;

/**
 *
 * @author Leandro
 */
public class EcfVO {
    
    private int id;
    private int idLoja;
    private int idTipoMarca;
    private String marca;
    private int idTipoModelo;
    private String modelo;
    private int ecf;
    private String descricao;
    private String numeroSerie;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdLoja() {
        return idLoja;
    }

    public void setIdLoja(int idLoja) {
        this.idLoja = idLoja;
    }

    public int getIdTipoMarca() {
        return idTipoMarca;
    }

    public void setIdTipoMarca(int idTipoMarca) {
        this.idTipoMarca = idTipoMarca;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public int getIdTipoModelo() {
        return idTipoModelo;
    }

    public void setIdTipoModelo(int idTipoModelo) {
        this.idTipoModelo = idTipoModelo;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public int getEcf() {
        return ecf;
    }

    public void setEcf(int ecf) {
        this.ecf = ecf;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getNumeroSerie() {
        return numeroSerie;
    }

    public void setNumeroSerie(String numeroSerie) {
        this.numeroSerie = numeroSerie;
    }
    
    
    
}
