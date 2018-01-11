package vrimplantacao.vo.vrimplantacao;

import vrimplantacao.utils.Utils;

public class AtacadoVendedorVO {
    private int id;
    private String nome = "";
    private int id_situacaocadastro = 1;
    private double percentual = 0;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = ("".equals(nome) ? "VENDEDOR SEM NOME" : Utils.acertarTexto(nome));
        if (this.nome.length() > 30) {
            this.nome = this.nome.substring(0, 30);
        }
    }

    public int getId_situacaocadastro() {
        return id_situacaocadastro;
    }

    public void setId_situacaocadastro(int id_situacaocadastro) {
        this.id_situacaocadastro = id_situacaocadastro;
    }

    public double getPercentual() {
        return percentual;
    }

    public void setPercentual(double percentual) {
        this.percentual = percentual;
    }
}