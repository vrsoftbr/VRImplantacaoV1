/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.cadastro.pdv.ecf;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Michael
 */
public class EcfPdvVO {

    private DateTimeFormatter formatador = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");;
    private int id = 0;
    private int id_loja = 1;
    private int ecf = 999;
    private String descricao = "BALCAO";
    private int id_tipomarca = 27;
    private int id_tipomodelo = 2;
    private int id_situacaocadastro = 0;
    private String numeroserie = String.valueOf(id);
    private String mfadicional = "N";
    private int numerousuario = 1;
    private String tipoecf = "ECF-IF";
    private String versaosb = "00.00.00";
    private String datahoragravacaosb = LocalDateTime.now().format(formatador);
    private String datahoracadastro = LocalDateTime.now().format(formatador);
    private boolean incidenciadesconto = false;
    private int versaobiblioteca = 0;
    private boolean geranfpaulista = false;
    private int id_tipoestado = 0;
    private String versao = "";
    private LocalDateTime datamovimento = null;
    private boolean cargagdata = false;
    private boolean cargaparam = false;
    private boolean cargalayout = false;
    private boolean cargaimagem = false;
    private int id_tipolayoutnotapaulista = 2;
    private boolean touch = false;
    private boolean alteradopaf = false;
    private LocalDateTime horamovimento = null;
    private int id_tipoemissor = 9;
    private int id_modelopdv = 0;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_loja() {
        return id_loja;
    }

    public void setId_loja(int id_loja) {
        this.id_loja = id_loja;
    }

    public int getEcf() {
        return ecf;
    }

    public void setEcf(int ecf) {
        this.ecf = ecf;
    }

    public String getDescricao() {
        if (getEcf() == 0 || getEcf() == 99 || getEcf() == 999){
        return descricao;
        } else return "ECF " + getEcf();
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getId_tipomarca() {
        return id_tipomarca;
    }

    public void setId_tipomarca(int id_tipomarca) {
        this.id_tipomarca = id_tipomarca;
    }

    public int getId_tipomodelo() {
        return id_tipomodelo;
    }

    public void setId_tipomodelo(int id_tipomodelo) {
        this.id_tipomodelo = id_tipomodelo;
    }

    public int getId_situacaocadastro() {
        return id_situacaocadastro;
    }

    public void setId_situacaocadastro(int id_situacaocadastro) {
        this.id_situacaocadastro = id_situacaocadastro;
    }

    public String getNumeroserie() {
        return numeroserie;
    }

    public void setNumeroserie(String numeroserie) {
        this.numeroserie = numeroserie;
    }

    public String getMfadicional() {
        return mfadicional;
    }

    public void setMfadicional(String mfadicional) {
        this.mfadicional = mfadicional;
    }

    public int getNumerousuario() {
        return numerousuario;
    }

    public void setNumerousuario(int numerousuario) {
        this.numerousuario = numerousuario;
    }

    public String getTipoecf() {
        return tipoecf;
    }

    public void setTipoecf(String tipoecf) {
        this.tipoecf = tipoecf;
    }

    public String getVersaosb() {
        return versaosb;
    }

    public void setVersaosb(String versaosb) {
        this.versaosb = versaosb;
    }

    public String getDatahoragravacaosb() {
        return datahoragravacaosb;
    }

    public void setDatahoragravacaosb(LocalDateTime datahoragravacaosb) {
        this.datahoragravacaosb = datahoragravacaosb.format(formatador);
    }

    public String getDatahoracadastro() {
        return datahoracadastro;
    }

    public void setDatahoracadastro(LocalDateTime datahoracadastro) {
        this.datahoracadastro = datahoracadastro.format(formatador);
    }

    public boolean isIncidenciadesconto() {
        return incidenciadesconto;
    }

    public void setIncidenciadesconto(boolean incidenciadesconto) {
        this.incidenciadesconto = incidenciadesconto;
    }

    public int getVersaobiblioteca() {
        return versaobiblioteca;
    }

    public void setVersaobiblioteca(int versaobiblioteca) {
        this.versaobiblioteca = versaobiblioteca;
    }

    public boolean isGeranfpaulista() {
        return geranfpaulista;
    }

    public void setGeranfpaulista(boolean geranfpaulista) {
        this.geranfpaulista = geranfpaulista;
    }

    public int getId_tipoestado() {
        return id_tipoestado;
    }

    public void setId_tipoestado(int id_tipoestado) {
        this.id_tipoestado = id_tipoestado;
    }

    public String getVersao() {
        return versao;
    }

    public void setVersao(String versao) {
        this.versao = versao;
    }

    public LocalDateTime getDatamovimento() {
        return datamovimento;
    }

    public void setDatamovimento(LocalDateTime datamovimento) {
        this.datamovimento = datamovimento;
    }

    public boolean isCargagdata() {
        return cargagdata;
    }

    public void setCargagdata(boolean cargagdata) {
        this.cargagdata = cargagdata;
    }

    public boolean isCargaparam() {
        return cargaparam;
    }

    public void setCargaparam(boolean cargaparam) {
        this.cargaparam = cargaparam;
    }

    public boolean isCargalayout() {
        return cargalayout;
    }

    public void setCargalayout(boolean cargalayout) {
        this.cargalayout = cargalayout;
    }

    public boolean isCargaimagem() {
        return cargaimagem;
    }

    public void setCargaimagem(boolean cargaimagem) {
        this.cargaimagem = cargaimagem;
    }

    public int getId_tipolayoutnotapaulista() {
        return id_tipolayoutnotapaulista;
    }

    public void setId_tipolayoutnotapaulista(int id_tipolayoutnotapaulista) {
        this.id_tipolayoutnotapaulista = id_tipolayoutnotapaulista;
    }

    public boolean isTouch() {
        return touch;
    }

    public void setTouch(boolean touch) {
        this.touch = touch;
    }

    public boolean isAlteradopaf() {
        return alteradopaf;
    }

    public void setAlteradopaf(boolean alteradopaf) {
        this.alteradopaf = alteradopaf;
    }

    public LocalDateTime getHoramovimento() {
        return horamovimento;
    }

    public void setHoramovimento(LocalDateTime horamovimento) {
        this.horamovimento = horamovimento;
    }

    public int getId_tipoemissor() {
        return id_tipoemissor;
    }

    public void setId_tipoemissor(int id_tipoemissor) {
        this.id_tipoemissor = id_tipoemissor;
    }

    public int getId_modelopdv() {
        return id_modelopdv;
    }

    public void setId_modelopdv(int id_modelopdv) {
        this.id_modelopdv = id_modelopdv;
    }    

}