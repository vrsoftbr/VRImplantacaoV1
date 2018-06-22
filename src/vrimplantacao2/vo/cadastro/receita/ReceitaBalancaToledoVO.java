package vrimplantacao2.vo.cadastro.receita;

import vrimplantacao.utils.Utils;
import vrimplantacao2.vo.enums.SituacaoCadastro;

/**
 *
 * @author Leandro
 */
public class ReceitaBalancaToledoVO {
    private int id;// integer NOT NULL,
    private String descricao;// character varying(30) NOT NULL,
    private String observacao;// character varying(100) NOT NULL,
    private SituacaoCadastro situacaoCadastro = SituacaoCadastro.ATIVO;//id_situacaocadastro integer NOT NULL,    
    private String receitaLinha1 = "";// character varying(56) NOT NULL,
    private String receitaLinha2 = "";// character varying(56) NOT NULL,
    private String receitaLinha3 = "";// character varying(56) NOT NULL,
    private String receitaLinha4 = "";// character varying(56) NOT NULL,
    private String receitaLinha5 = "";// character varying(56) NOT NULL,
    private String receitaLinha6 = "";// character varying(56) NOT NULL,
    private String receitaLinha7 = "";// character varying(56) NOT NULL,
    private String receitaLinha8 = "";// character varying(56) NOT NULL,
    private String receitaLinha9 = "";// character varying(56) NOT NULL,
    private String receitaLinha10 = "";// character varying(56) NOT NULL,
    private String receitaLinha11 = "";// character varying(56) NOT NULL,
    private String receitaLinha12 = "";// character varying(56) NOT NULL,
    private String receitaLinha13 = "";// character varying(56) NOT NULL,
    private String receitaLinha14 = "";// character varying(56) NOT NULL,
    private String receitaLinha15 = "";// character varying(56) NOT NULL,

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = Utils.acertarTexto(descricao, 30, "SEM DESCRICAO");
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = Utils.acertarTexto(observacao, 100, "");
    }

    public SituacaoCadastro getSituacaoCadastro() {
        return situacaoCadastro;
    }

    public void setSituacaoCadastro(SituacaoCadastro situacaoCadastro) {
        this.situacaoCadastro = situacaoCadastro != null ? situacaoCadastro : SituacaoCadastro.ATIVO;
    }

    private String formataLinha(String linha) {
        return Utils.acertarTextoMultiLinha(linha, 56, "").replace("?", "");
    }
    
    public void setReceitaLinha1(String linha) {
        this.receitaLinha1 = formataLinha(linha);
    }
    public void setReceitaLinha2(String linha) {
        this.receitaLinha2 = formataLinha(linha);
    }
    public void setReceitaLinha3(String linha) {
        this.receitaLinha3 = formataLinha(linha);
    }
    public void setReceitaLinha4(String linha) {
        this.receitaLinha4 = formataLinha(linha);
    }
    public void setReceitaLinha5(String linha) {
        this.receitaLinha5 = formataLinha(linha);
    }
    public void setReceitaLinha6(String linha) {
        this.receitaLinha6 = formataLinha(linha);
    }
    public void setReceitaLinha7(String linha) {
        this.receitaLinha7 = formataLinha(linha);
    }
    public void setReceitaLinha8(String linha) {
        this.receitaLinha8 = formataLinha(linha);
    }
    public void setReceitaLinha9(String linha) {
        this.receitaLinha9 = formataLinha(linha);
    }
    public void setReceitaLinha10(String linha) {
        this.receitaLinha10 = formataLinha(linha);
    }
    public void setReceitaLinha11(String linha) {
        this.receitaLinha11 = formataLinha(linha);
    }
    public void setReceitaLinha12(String linha) {
        this.receitaLinha12 = formataLinha(linha);
    }
    public void setReceitaLinha13(String linha) {
        this.receitaLinha13 = formataLinha(linha);
    }
    public void setReceitaLinha14(String linha) {
        this.receitaLinha14 = formataLinha(linha);
    }
    public void setReceitaLinha15(String linha) {
        this.receitaLinha15 = formataLinha(linha);
    }

    public String getReceitaLinha1() {
        return receitaLinha1;
    }

    public String getReceitaLinha2() {
        return receitaLinha2;
    }

    public String getReceitaLinha3() {
        return receitaLinha3;
    }

    public String getReceitaLinha4() {
        return receitaLinha4;
    }

    public String getReceitaLinha5() {
        return receitaLinha5;
    }

    public String getReceitaLinha6() {
        return receitaLinha6;
    }

    public String getReceitaLinha7() {
        return receitaLinha7;
    }

    public String getReceitaLinha8() {
        return receitaLinha8;
    }

    public String getReceitaLinha9() {
        return receitaLinha9;
    }

    public String getReceitaLinha10() {
        return receitaLinha10;
    }

    public String getReceitaLinha11() {
        return receitaLinha11;
    }

    public String getReceitaLinha12() {
        return receitaLinha12;
    }

    public String getReceitaLinha13() {
        return receitaLinha13;
    }

    public String getReceitaLinha14() {
        return receitaLinha14;
    }

    public String getReceitaLinha15() {
        return receitaLinha15;
    }

    
    
}
