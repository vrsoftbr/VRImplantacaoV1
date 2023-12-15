package vrimplantacao2.vo.importacao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import vrimplantacao2.vo.enums.SituacaoCadastro;

/**
 * Classe de importação de nutricionais.
 * @author Leandro
 */
public class NutricionalIMP {
    
    private String id;// integer NOT NULL,
    private String descricao;// character varying(20) NOT NULL,
    private SituacaoCadastro situacaoCadastro = SituacaoCadastro.ATIVO;// integer NOT NULL,
    private int caloria;// integer NOT NULL,
    private double carboidrato;// numeric(10,1) NOT NULL,
    private boolean carboidratoInferior;// boolean NOT NULL,
    private double proteina;// numeric(10,1) NOT NULL,
    private boolean proteinaInferior;// boolean NOT NULL,
    private double gordura;// numeric(10,1) NOT NULL,
    private double gorduraSaturada;// numeric(10,1) NOT NULL,
    private double gorduraTrans;// numeric(10,1) NOT NULL,
    private boolean colesterolInferior;// boolean NOT NULL,
    private double fibra;// numeric(10,1) NOT NULL,
    private boolean fibraInferior;// boolean NOT NULL,
    private double calcio;// numeric(10,1) NOT NULL,
    private double ferro;// numeric(10,1) NOT NULL,
    private double sodio;// numeric(10,1) NOT NULL,
    private int percentualCaloria;// integer NOT NULL,
    private int percentualCarboidrato;// integer NOT NULL,
    private int percentualProteina;// integer NOT NULL,
    private int percentualGordura;// integer NOT NULL,
    private int percentualGorduraSaturada;// integer NOT NULL,
    private int percentualFibra;// integer NOT NULL,
    private int percentualCalcio;// integer NOT NULL,
    private int percentualFerro;// integer NOT NULL,
    private int percentualSodio;// integer NOT NULL,
    private String porcao;// character varying(35) NOT NULL,
    private int idTipoMedida = -1;
    private int medidaInteira = 1;
    private int id_tipomedidadecimal = 0;
    private int Id_tipounidadeporcao = 4;
    private double acucaresadicionados = 0;
    private double acucarestotais = 0;
    private final List<String> mensagemAlergico = new ArrayList<>();// character varying(168),
    private final Set<String> produtos = new HashSet<>();
    private int quantidade = 0;
    
    /**
     * Executa um cálculo para definir as porcentagens do nutricional.
     */
    public void calcularPorcentagens() {
        double total = caloria + carboidrato + proteina + gordura + gorduraSaturada + fibra + calcio + ferro + sodio;
        
        percentualCaloria = Math.round((float)((caloria * 100) / total));
        percentualCarboidrato = Math.round((float)((carboidrato * 100) / total));
        percentualProteina = Math.round((float)((proteina * 100) / total));
        percentualGordura = Math.round((float)((gordura * 100) / total));
        percentualGorduraSaturada = Math.round((float)((gorduraSaturada * 100) / total));
        percentualFibra = Math.round((float)((fibra * 100) / total));
        percentualCalcio = Math.round((float)((calcio * 100) / total));
        percentualFerro = Math.round((float)((ferro * 100) / total));
        percentualSodio = Math.round((float)((sodio * 100) / total));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        if (descricao.length() > 20) {
            this.descricao = descricao.substring(0, 20);
        } else {
            this.descricao = descricao;
        }
    }

    public SituacaoCadastro getSituacaoCadastro() {
        return situacaoCadastro;
    }

    public void setSituacaoCadastro(SituacaoCadastro situacaoCadastro) {
        this.situacaoCadastro = situacaoCadastro != null ? situacaoCadastro : SituacaoCadastro.ATIVO;
    }

    public int getCaloria() {
        return caloria;
    }

    public void setCaloria(int caloria) {
        this.caloria = caloria;
    }

    public double getCarboidrato() {
        return carboidrato;
    }

    public void setCarboidrato(double carboidrato) {
        this.carboidrato = carboidrato;
    }

    public boolean isCarboidratoInferior() {
        return carboidratoInferior;
    }

    public void setCarboidratoInferior(boolean carboidratoInferior) {
        this.carboidratoInferior = carboidratoInferior;
    }

    public double getProteina() {
        return proteina;
    }

    public void setProteina(double proteina) {
        this.proteina = proteina;
    }

    public boolean isProteinaInferior() {
        return proteinaInferior;
    }

    public void setProteinaInferior(boolean proteinaInferior) {
        this.proteinaInferior = proteinaInferior;
    }

    public double getGordura() {
        return gordura;
    }

    public void setGordura(double gordura) {
        this.gordura = gordura;
    }

    public double getGorduraSaturada() {
        return gorduraSaturada;
    }

    public void setGorduraSaturada(double gorduraSaturada) {
        this.gorduraSaturada = gorduraSaturada;
    }

    public double getGorduraTrans() {
        return gorduraTrans;
    }

    public void setGorduraTrans(double gorduraTrans) {
        this.gorduraTrans = gorduraTrans;
    }

    public boolean isColesterolInferior() {
        return colesterolInferior;
    }

    public void setColesterolInferior(boolean colesterolInferior) {
        this.colesterolInferior = colesterolInferior;
    }

    public double getFibra() {
        return fibra;
    }

    public void setFibra(double fibra) {
        this.fibra = fibra;
    }

    public boolean isFibraInferior() {
        return fibraInferior;
    }

    public void setFibraInferior(boolean fibraInferior) {
        this.fibraInferior = fibraInferior;
    }

    public double getCalcio() {
        return calcio;
    }

    public void setCalcio(double calcio) {
        this.calcio = calcio;
    }

    public double getFerro() {
        return ferro;
    }

    public void setFerro(double ferro) {
        this.ferro = ferro;
    }

    public double getSodio() {
        return sodio;
    }

    public void setSodio(double sodio) {
        this.sodio = sodio;
    }

    public int getPercentualCaloria() {
        return percentualCaloria;
    }

    public void setPercentualCaloria(int percentualCaloria) {
        this.percentualCaloria = percentualCaloria;
    }

    public int getPercentualCarboidrato() {
        return percentualCarboidrato;
    }

    public void setPercentualCarboidrato(int percentualCarboidrato) {
        this.percentualCarboidrato = percentualCarboidrato;
    }

    public int getPercentualProteina() {
        return percentualProteina;
    }

    public void setPercentualProteina(int percentualProteina) {
        this.percentualProteina = percentualProteina;
    }

    public int getPercentualGordura() {
        return percentualGordura;
    }

    public void setPercentualGordura(int percentualGordura) {
        this.percentualGordura = percentualGordura;
    }

    public int getPercentualGorduraSaturada() {
        return percentualGorduraSaturada;
    }

    public void setPercentualGorduraSaturada(int percentualGorduraSaturada) {
        this.percentualGorduraSaturada = percentualGorduraSaturada;
    }

    public int getPercentualFibra() {
        return percentualFibra;
    }

    public void setPercentualFibra(int percentualFibra) {
        this.percentualFibra = percentualFibra;
    }

    public int getPercentualCalcio() {
        return percentualCalcio;
    }

    public void setPercentualCalcio(int percentualCalcio) {
        this.percentualCalcio = percentualCalcio;
    }

    public int getPercentualFerro() {
        return percentualFerro;
    }

    public void setPercentualFerro(int percentualFerro) {
        this.percentualFerro = percentualFerro;
    }

    public int getPercentualSodio() {
        return percentualSodio;
    }

    public void setPercentualSodio(int percentualSodio) {
        this.percentualSodio = percentualSodio;
    }

    public String getPorcao() {
        return porcao;
    }

    public void setPorcao(String porcao) {
        this.porcao = porcao;
    }

    public List<String> getMensagemAlergico() {
        return mensagemAlergico;
    }

    public Set<String> getProdutos() {
        return produtos;
    }

    public int getIdTipoMedida() {
        return idTipoMedida;
    }

    public void setIdTipoMedida(int idTipoMedida) {
        this.idTipoMedida = (idTipoMedida - 1);
    }

    public int getMedidaInteira() {
        return medidaInteira;
    }

    public void setMedidaInteira(int medidaInteira) {
        this.medidaInteira = medidaInteira;
    }
    
    public void addMensagemAlergico(String mensagem) {
        getMensagemAlergico().add(mensagem);
    }
    
    public void addProduto(String produtoId) {
        getProdutos().add(produtoId);
    }

    public int getId_tipomedidadecimal() {
        return id_tipomedidadecimal;
    }

    public void setId_tipomedidadecimal(int id_tipomedidadecimal) {
        this.id_tipomedidadecimal = id_tipomedidadecimal;
    }

    public int getId_tipounidadeporcao() {
        return Id_tipounidadeporcao;
    }

    public void setId_tipounidadeporcao(int Id_tipounidadeporcao) {
        this.Id_tipounidadeporcao = Id_tipounidadeporcao;
    }    

    public double getAcucaresadicionados() {
        return acucaresadicionados;
    }

    public void setAcucaresadicionados(double acucaresadicionados) {
        this.acucaresadicionados = acucaresadicionados;
    }

    public double getAcucarestotais() {
        return acucarestotais;
    }

    public void setAcucarestotais(double acucarestotais) {
        this.acucarestotais = acucarestotais;
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
    
    
}
