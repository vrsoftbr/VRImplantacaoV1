package vrimplantacao2.dao.cadastro;

/**
 * Classe que representa a loja do sistema a ser importado.
 * @author Leandro
 */
public class Estabelecimento {
    public String cnpj;
    public String razao;

    public Estabelecimento(String cnpj, String razao) {
        this.cnpj = cnpj;
        this.razao = razao;
    }

    @Override
    public String toString() {
        return this.cnpj + " - " + this.razao;
    }
}