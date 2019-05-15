/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao.vo.vrimplantacao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import vrimplantacao.utils.Utils;

public class NutricionalToledoVO {
    private int id = 0;
    private String descricao = "";
    private int id_situacaocadastro = 1;
    private int caloria = 0;
    private double carboidrato = 0;
    private boolean carboidratoinferior = false;
    private double proteina = 0;
    private boolean proteinainferior = false;
    private double gordura = 0;
    private double gordurasaturada = 0;
    private double gorduratrans = 0;
    private boolean colesterolinferior = false;
    private double fibra = 0;
    private boolean fibrainferior = false;
    private double calcio = 0;
    private double ferro = 0;
    private double sodio = 0;
    private int percentualcaloria = 0;
    private int percentualcarboidrato = 0;
    private int percentualproteina = 0;
    private int percentualgordura = 0;
    private int percentualgordurasaturada = 0;
    private int percentualfibra = 0;
    private int percentualcalcio = 0;
    private int percentualferro = 0;
    private int percentualsodio = 0;
    private int quantidade = 0;
    private int id_tipounidadeporcao = 0;
    private int medidainteira = 0;
    private int id_tipomedidadecimal = 0;
    private int id_tipomedida = 0;
    private List<String> mensagemAlergico = new ArrayList<>(15);
    private Set<String> idProdutos = new HashSet<>();
    public List<NutricionalToledoItemVO> vNutricionalToledoItem = new ArrayList<>();
    private int idProduto = 0;

    public Set<String> getIdProdutos() {
        return idProdutos;
    }

    public void addProduto(String id) {
        this.idProdutos.add(id);
    }
    
    public List<String> getMensagemAlergico() {
        return mensagemAlergico;
    }

    public void addMensagemAlergico(String mensagem) {
        mensagem = Utils.acertarTexto(mensagem);
        if (!"".equals(mensagem)) {
            if (mensagem.length() <= 56) {
                this.mensagemAlergico.add(mensagem);
            } else {
                while (!mensagem.equals("")) {
                    int length = mensagem.length() >= 56 ? 56 : mensagem.length();
                    String str = mensagem.substring(0, length);
                    mensagem = mensagem.substring(length, mensagem.length());
                    this.mensagemAlergico.add(str);
                }
            }
        }
    }
    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
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
        this.descricao = Utils.acertarTexto(descricao, 20);
    }

    /**
     * @return the id_situacaocadastro
     */
    public int getId_situacaocadastro() {
        return id_situacaocadastro;
    }

    /**
     * @param id_situacaocadastro the id_situacaocadastro to set
     */
    public void setId_situacaocadastro(int id_situacaocadastro) {
        this.id_situacaocadastro = id_situacaocadastro;
    }

    /**
     * @return the caloria
     */
    public int getCaloria() {
        return caloria;
    }

    /**
     * @param caloria the caloria to set
     */
    public void setCaloria(int caloria) {
        this.caloria = caloria;
    }

    /**
     * @return the carboidrato
     */
    public double getCarboidrato() {
        return carboidrato;
    }

    /**
     * @param carboidrato the carboidrato to set
     */
    public void setCarboidrato(double carboidrato) {
        this.carboidrato = carboidrato;
    }

    /**
     * @return the carboidratoinferior
     */
    public boolean isCarboidratoinferior() {
        return carboidratoinferior;
    }

    /**
     * @param carboidratoinferior the carboidratoinferior to set
     */
    public void setCarboidratoinferior(boolean carboidratoinferior) {
        this.carboidratoinferior = carboidratoinferior;
    }

    /**
     * @return the proteina
     */
    public double getProteina() {
        return proteina;
    }

    /**
     * @param proteina the proteina to set
     */
    public void setProteina(double proteina) {
        this.proteina = proteina;
    }

    /**
     * @return the proteinainferior
     */
    public boolean isProteinainferior() {
        return proteinainferior;
    }

    /**
     * @param proteinainferior the proteinainferior to set
     */
    public void setProteinainferior(boolean proteinainferior) {
        this.proteinainferior = proteinainferior;
    }

    /**
     * @return the gordura
     */
    public double getGordura() {
        return gordura;
    }

    /**
     * @param gordura the gordura to set
     */
    public void setGordura(double gordura) {
        this.gordura = gordura;
    }

    /**
     * @return the gordurasaturada
     */
    public double getGordurasaturada() {
        return gordurasaturada;
    }

    /**
     * @param gordurasaturada the gordurasaturada to set
     */
    public void setGordurasaturada(double gordurasaturada) {
        this.gordurasaturada = gordurasaturada;
    }

    /**
     * @return the gorduratrans
     */
    public double getGorduratrans() {
        return gorduratrans;
    }

    /**
     * @param gorduratrans the gorduratrans to set
     */
    public void setGorduratrans(double gorduratrans) {
        this.gorduratrans = gorduratrans;
    }

    /**
     * @return the colesterolinferior
     */
    public boolean isColesterolinferior() {
        return colesterolinferior;
    }

    /**
     * @param colesterolinferior the colesterolinferior to set
     */
    public void setColesterolinferior(boolean colesterolinferior) {
        this.colesterolinferior = colesterolinferior;
    }

    /**
     * @return the fibra
     */
    public double getFibra() {
        return fibra;
    }

    /**
     * @param fibra the fibra to set
     */
    public void setFibra(double fibra) {
        this.fibra = fibra;
    }

    /**
     * @return the fibrainferior
     */
    public boolean isFibrainferior() {
        return fibrainferior;
    }

    /**
     * @param fibrainferior the fibrainferior to set
     */
    public void setFibrainferior(boolean fibrainferior) {
        this.fibrainferior = fibrainferior;
    }

    /**
     * @return the calcio
     */
    public double getCalcio() {
        return calcio;
    }

    /**
     * @param calcio the calcio to set
     */
    public void setCalcio(double calcio) {
        this.calcio = calcio;
    }

    /**
     * @return the ferro
     */
    public double getFerro() {
        return ferro;
    }

    /**
     * @param ferro the ferro to set
     */
    public void setFerro(double ferro) {
        this.ferro = ferro;
    }

    /**
     * @return the sodio
     */
    public double getSodio() {
        return sodio;
    }

    /**
     * @param sodio the sodio to set
     */
    public void setSodio(double sodio) {
        this.sodio = sodio;
    }

    /**
     * @return the percentualcaloria
     */
    public int getPercentualcaloria() {
        return percentualcaloria;
    }

    /**
     * @param percentualcaloria the percentualcaloria to set
     */
    public void setPercentualcaloria(int percentualcaloria) {
        this.percentualcaloria = percentualcaloria;
    }

    /**
     * @return the percentualcarboidrato
     */
    public int getPercentualcarboidrato() {
        return percentualcarboidrato;
    }

    /**
     * @param percentualcarboidrato the percentualcarboidrato to set
     */
    public void setPercentualcarboidrato(int percentualcarboidrato) {
        this.percentualcarboidrato = percentualcarboidrato;
    }

    /**
     * @return the percentualproteina
     */
    public int getPercentualproteina() {
        return percentualproteina;
    }

    /**
     * @param percentualproteina the percentualproteina to set
     */
    public void setPercentualproteina(int percentualproteina) {
        this.percentualproteina = percentualproteina;
    }

    /**
     * @return the percentualgordura
     */
    public int getPercentualgordura() {
        return percentualgordura;
    }

    /**
     * @param percentualgordura the percentualgordura to set
     */
    public void setPercentualgordura(int percentualgordura) {
        this.percentualgordura = percentualgordura;
    }

    /**
     * @return the percentualgordurasaturada
     */
    public int getPercentualgordurasaturada() {
        return percentualgordurasaturada;
    }

    /**
     * @param percentualgordurasaturada the percentualgordurasaturada to set
     */
    public void setPercentualgordurasaturada(int percentualgordurasaturada) {
        this.percentualgordurasaturada = percentualgordurasaturada;
    }

    /**
     * @return the percentualfibra
     */
    public int getPercentualfibra() {
        return percentualfibra;
    }

    /**
     * @param percentualfibra the percentualfibra to set
     */
    public void setPercentualfibra(int percentualfibra) {
        this.percentualfibra = percentualfibra;
    }

    /**
     * @return the percentualcalcio
     */
    public int getPercentualcalcio() {
        return percentualcalcio;
    }

    /**
     * @param percentualcalcio the percentualcalcio to set
     */
    public void setPercentualcalcio(int percentualcalcio) {
        this.percentualcalcio = percentualcalcio;
    }

    /**
     * @return the percentualferro
     */
    public int getPercentualferro() {
        return percentualferro;
    }

    /**
     * @param percentualferro the percentualferro to set
     */
    public void setPercentualferro(int percentualferro) {
        this.percentualferro = percentualferro;
    }

    /**
     * @return the percentualsodio
     */
    public int getPercentualsodio() {
        return percentualsodio;
    }

    /**
     * @param percentualsodio the percentualsodio to set
     */
    public void setPercentualsodio(int percentualsodio) {
        this.percentualsodio = percentualsodio;
    }

    /**
     * @return the quantidade
     */
    public int getQuantidade() {
        return quantidade;
    }

    /**
     * @param quantidade the quantidade to set
     */
    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    /**
     * @return the id_tipounidadeporcao
     */
    public int getId_tipounidadeporcao() {
        return id_tipounidadeporcao;
    }

    /**
     * @param id_tipounidadeporcao the id_tipounidadeporcao to set
     */
    public void setId_tipounidadeporcao(int id_tipounidadeporcao) {
        this.id_tipounidadeporcao = id_tipounidadeporcao;
    }

    /**
     * @return the medidainteira
     */
    public int getMedidainteira() {
        return medidainteira;
    }

    /**
     * @param medidainteira the medidainteira to set
     */
    public void setMedidainteira(int medidainteira) {
        this.medidainteira = medidainteira;
    }

    /**
     * @return the id_tipomedidadecimal
     */
    public int getId_tipomedidadecimal() {
        return id_tipomedidadecimal;
    }

    /**
     * @param id_tipomedidadecimal the id_tipomedidadecimal to set
     */
    public void setId_tipomedidadecimal(int id_tipomedidadecimal) {
        this.id_tipomedidadecimal = id_tipomedidadecimal;
    }

    /**
     * @return the id_tipomedida
     */
    public int getId_tipomedida() {
        return id_tipomedida;
    }

    /**
     * @param id_tipomedida the id_tipomedida to set
     */
    public void setId_tipomedida(int id_tipomedida) {
        this.id_tipomedida = id_tipomedida;
    }

    public int getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(int idProduto) {
        this.idProduto = idProduto;
    }
}
