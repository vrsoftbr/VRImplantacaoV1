package vrimplantacao2.vo.cadastro;

import java.util.Date;
import vrimplantacao2.vo.enums.SituacaoCadastro;

public class ContaContabilVO {

    private Integer id;
    private String descricao;
    private Integer conta1;
    private Integer conta2;
    private Integer conta3;
    private Integer conta4;
    private Integer conta5;
    private Integer nivel;
    private SituacaoCadastro id_situacaoCadastro;
    private String contaReduzida;
    private Date data;
    private boolean dmpl;
    private boolean contaCompensacao;
    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return the descricao
     */
    public String getDescricao() {
        return descricao;
    }

    /**
     * @param descricao the descricao to set
     */
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    /**
     * @return the conta1
     */
    public Integer getConta1() {
        return conta1;
    }

    /**
     * @param conta1 the conta1 to set
     */
    public void setConta1(Integer conta1) {
        this.conta1 = conta1;
    }

    /**
     * @return the conta2
     */
    public Integer getConta2() {
        return conta2;
    }

    /**
     * @param conta2 the conta2 to set
     */
    public void setConta2(Integer conta2) {
        this.conta2 = conta2;
    }

    /**
     * @return the conta3
     */
    public Integer getConta3() {
        return conta3;
    }

    /**
     * @param conta3 the conta3 to set
     */
    public void setConta3(Integer conta3) {
        this.conta3 = conta3;
    }

    /**
     * @return the conta4
     */
    public Integer getConta4() {
        return conta4;
    }

    /**
     * @param conta4 the conta4 to set
     */
    public void setConta4(Integer conta4) {
        this.conta4 = conta4;
    }

    /**
     * @return the conta5
     */
    public Integer getConta5() {
        return conta5;
    }

    /**
     * @param conta5 the conta5 to set
     */
    public void setConta5(Integer conta5) {
        this.conta5 = conta5;
    }

    /**
     * @return the nivel
     */
    public Integer getNivel() {
        return nivel;
    }

    /**
     * @param nivel the nivel to set
     */
    public void setNivel(Integer nivel) {
        this.nivel = nivel;
    }

    /**
     * @return the contaReduzida
     */
    public String getContaReduzida() {
        return contaReduzida;
    }

    /**
     * @param contaReduzida the contaReduzida to set
     */
    public void setContaReduzida(String contaReduzida) {
        this.contaReduzida = contaReduzida;
    }

    /**
     * @return the data
     */
    public Date getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(Date data) {
        this.data = data;
    }

    /**
     * @return the dmpl
     */
    public boolean isDmpl() {
        return dmpl;
    }

    /**
     * @param dmpl the dmpl to set
     */
    public void setDmpl(boolean dmpl) {
        this.dmpl = dmpl;
    }

    /**
     * @return the contaCompensacao
     */
    public boolean isContaCompensacao() {
        return contaCompensacao;
    }

    /**
     * @param contaCompensacao the contaCompensacao to set
     */
    public void setContaCompensacao(boolean contaCompensacao) {
        this.contaCompensacao = contaCompensacao;
    }

    /**
     * @return the id_situacaoCadastro
     */
    public SituacaoCadastro getId_situacaoCadastro() {
        return id_situacaoCadastro;
    }

    /**
     * @param id_situacaoCadastro the id_situacaoCadastro to set
     */
    public void setId_situacaoCadastro(SituacaoCadastro id_situacaoCadastro) {
        this.id_situacaoCadastro = id_situacaoCadastro;
    }
    
}
