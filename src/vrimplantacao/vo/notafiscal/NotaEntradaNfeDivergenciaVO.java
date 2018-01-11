package vrimplantacao.vo.notafiscal;

import java.io.Serializable;

public class NotaEntradaNfeDivergenciaVO implements Serializable {

    public long codigoBarras = 0;
    public String codigoExterno = "";
    public int idProduto = 0;
    public String produto = "";
    public int idTipoEntrada = 0;
    public String tipoEntrada = "";
    public String divergencia = "";
    public int idFornecedor = 0;
    public int ncm1Nota = 0;
    public int ncm2Nota = 0;
    public int ncm3Nota = 0;
    public int ncm1Cadastro = 0;
    public int ncm2Cadastro = 0;
    public int ncm3Cadastro = 0;
    public int idTipoPisCofinsNota = 0;
    public String tipoPisCofinsNota = "";
    public int idTipoPisCofinsCadastro = 0;
    public String tipoPisCofinsCadastro = "";
    public int numeroNota = 0;
    public int idAliquotaNota = 0;
    public String aliquotaNota = "";
    public int idAliquotaCadastro = 0;
    public String aliquotaCadastro = "";
    public int csosnNota = 0;
    public int csosnCadastro = 0;

    public long getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(long codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    public String getCodigoExterno() {
        return codigoExterno;
    }

    public void setCodigoExterno(String codigoExterno) {
        this.codigoExterno = codigoExterno;
    }

    public int getCodigoProduto() {
        return idProduto;
    }

    public void setCodigoProduto(int codigoProduto) {
        this.idProduto = codigoProduto;
    }

    public String getDescricao() {
        return produto;
    }

    public void setDescricao(String descricao) {
        this.produto = descricao;
    }

    public String getDivergencia() {
        return divergencia;
    }

    public void setDivergencia(String divergencia) {
        this.divergencia = divergencia;
    }

    public int getCodigoFornecedor() {
        return idFornecedor;
    }

    public void setCodigoFornecedor(int codigoFornecedor) {
        this.idFornecedor = codigoFornecedor;
    }

    public int getIdTipoPisCofinsNota() {
        return idTipoPisCofinsNota;
    }

    public void setIdTipoPisCofinsNota(int idTipoPisCofinsNota) {
        this.idTipoPisCofinsNota = idTipoPisCofinsNota;
    }

    public String getTipoPisCofinsNota() {
        return tipoPisCofinsNota;
    }

    public void setTipoPisCofinsNota(String tipoPisCofinsNota) {
        this.tipoPisCofinsNota = tipoPisCofinsNota;
    }

    public int getIdTipoPisCofinsCadastro() {
        return idTipoPisCofinsCadastro;
    }

    public void setIdTipoPisCofinsCadastro(int idTipoPisCofinsCadastro) {
        this.idTipoPisCofinsCadastro = idTipoPisCofinsCadastro;
    }

    public String getTipoPisCofinsCadastro() {
        return tipoPisCofinsCadastro;
    }

    public void setTipoPisCofinsCadastro(String tipoPisCofinsCadastro) {
        this.tipoPisCofinsCadastro = tipoPisCofinsCadastro;
    }

    public int getIdAliquotaCadastro() {
        return idAliquotaCadastro;
    }

    public void setIdAliquotaCadastro(int idAliquotaCadastro) {
        this.idAliquotaCadastro = idAliquotaCadastro;
    }

    public String getaliquotaCadastro() {
        return aliquotaCadastro;
    }

    public void setaliquotaCadastro(String aliquotaCadastro) {
        this.aliquotaCadastro = aliquotaCadastro;
    }

    public int getIdAliquotaNota() {
        return idAliquotaNota;
    }

    public void setIdAliquotaNota(int idAliquotaNota) {
        this.idAliquotaNota = idAliquotaNota;
    }

    public String getaliquotaNota() {
        return aliquotaNota;
    }

    public void setaliquotaNota(String aliquotaNota) {
        this.aliquotaNota = aliquotaNota;
    }

    public int getNumeroNota() {
        return numeroNota;
    }

    public void setNumeroNota(int numeroNota) {
        this.numeroNota = numeroNota;
    }

    public int getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(int idProduto) {
        this.idProduto = idProduto;
    }

    public int getIdFornecedor() {
        return idFornecedor;
    }

    public void setIdFornecedor(int idFornecedor) {
        this.idFornecedor = idFornecedor;
    }

    public int getNcmNota1() {
        return ncm1Nota;
    }

    public void setNcmNota1(int ncmNota1) {
        this.ncm1Nota = ncmNota1;
    }

    public int getNcmNota2() {
        return ncm2Nota;
    }

    public void setNcmNota2(int ncmNota2) {
        this.ncm2Nota = ncmNota2;
    }

    public int getNcmNota3() {
        return ncm3Nota;
    }

    public void setNcmNota3(int ncmNota3) {
        this.ncm3Nota = ncmNota3;
    }

    public int getNcmCadastro1() {
        return ncm1Cadastro;
    }

    public void setNcmCadastro1(int ncmCadastro1) {
        this.ncm1Cadastro = ncmCadastro1;
    }

    public int getNcmCadastro2() {
        return ncm2Cadastro;
    }

    public void setNcmCadastro2(int ncmCadastro2) {
        this.ncm2Cadastro = ncmCadastro2;
    }

    public int getNcmCadastro3() {
        return ncm3Cadastro;
    }

    public void setNcmCadastro3(int ncmCadastro3) {
        this.ncm3Cadastro = ncmCadastro3;
    }

    public int getcsosnCadastro() {
        return csosnCadastro;
    }

    public void setcsosnCadastro(int csosnCadastro) {
        this.csosnCadastro = csosnCadastro;
    }

    public int getcsosnNota() {
        return csosnNota;
    }

    public void setcsosnNota(int csosnNota) {
        this.csosnNota = csosnNota;
    }
}