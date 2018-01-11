/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao.vo.vrimplantacao;

import java.util.List;
import java.util.ArrayList;
import vrimplantacao.utils.Utils;

public class ProdutoVO {

    public double codigoAnterior = 0;
    public int id = 0;
    public String descricaoCompleta = "";
    public String descricaoCompletaAnterior = "";
    public int mercadologico1 = 0;
    public int mercadologico2 = 0;
    public int mercadologico3 = 0;
    public int mercadologico4 = 0;
    public int mercadologico5 = 0;
    public String descricaoMercadologico1 = "";
    public String descricaoMercadologico2 = "";
    public String descricaoMercadologico3 = "";
    public String descricaoMercadologico4 = "";
    public String descricaoMercadologico5 = "";
    public int idTipoEmbalagem = 0;
    public String tipoEmbalagem = "";
    public int qtdEmbalagem = 1;
    public int idComprador = 1;
    public String comprador = "";
    public double custoFinal = 0;
    public int idFamiliaProduto = -1;
    public String familiaProduto = "";
    public String descricaoReduzida = "";
    public double pesoLiquido = 0;
    public String dataCadastro = "";
    public int validade = 0;
    public int qtdDiasMinimoValidade = 0;
    public double pesoBruto = 0;
    public double tara = 0;
    public int comprimentoEmbalagem = 0;
    public int larguraEmbalagem = 0;
    public int alturaEmbalagem = 0;
    public double perda = 0;
    public double margem = 0;
    public boolean verificaCustoTabela = false;
    public double percentualIpi = 0;
    public double percentualFrete = 0;
    public double percentualEncargo = 0;
    public double percentualPerda = 0;
    public double percentualSubstituicao = 0;
    public String descricaoGondola = "";
    public String dataAlteracao = "";
    public int idProdutoVasilhame = -1;
    public String produtoVasilhame = "";
    public int ncm1 = 402;
    public int ncm2 = 99;
    public int ncm3 = 0;
    public int excecao = 0;
    public int idTipoMercadoria = -1;
    public boolean fabricacaoPropria = false;
    public boolean sugestaoCotacao = false;
    public boolean sugestaoPedido = false;
    public boolean aceitaMultiplicacaoPdv = false;
    public int idFornecedorFabricante = 1;
    public String fornecedorFabricante = "";
    public int idDivisaoFornecedor = 0;
    public int idTipoProduto = 0;
    public String tipoProduto = "";
    public int idTipoPisCofinsDebito = 1;
    public String tipoPisCofinsDebito = "";
    public int idTipoPisCofinsCredito = 13;
    public String tipoPisCofinsCredito = "";
    public List<ProdutoComplementoVO> vComplemento = new ArrayList();
    public List<ProdutoAliquotaVO> vAliquota = new ArrayList();
    public List<ProdutoAutomacaoVO> vAutomacao = new ArrayList();
    public List<ProdutoAutomacaoLojaVO> vAutomacaoLoja = new ArrayList<>();
    public List<CodigoAnteriorVO> vCodigoAnterior = new ArrayList();
    public List<ProdutosUnificacaoVO> vProdutosUnificacao = new ArrayList();    
    public boolean sazonal = false;
    public boolean consignado = false;
    public int ddv = 0;
    public boolean permiteTroca = false;
    public int temperatura = 0;
    public int idTipoOrigemMercadoria = 0;
    public String tipoOrigemMercadoria = "";
    public List<ProdutoAutomacaoVO> vAutomacaoExclusao = new ArrayList();
    public String aliquotaCredito = "";
    public double precoVenda = 0;
    public int idSituacaoCadastro = 0;
    public String situacaoCadastro = "";
    public String aliquotaDebito = "";
    public long codigoBarras = 0;
    public double custoComImposto = 0;
    public boolean descontinuado = false;
    public double ipi = 0;
    public boolean pesavel = false;
    public double desconto = 0;
    public String dataVencimento = "";
    public boolean vendaControlada = false;
    public int tipoNaturezaReceita = 999;
    public String tipoNaturezaReceitaDescricao = "";
    public boolean vendaPdv = false;
    public boolean conferido = false;
    public boolean permiteQuebra = false;
    public boolean permitePerda = false;
    public String codigoAnp = "";
    public double impostoMedioNacional = 0;
    public double impostoMedioImportado = 0;
    public double impostoMedioEstadual = 0;
    public boolean utilizaTabelaSubstituicaoTributaria = false;
    public boolean utilizaValidadeEntrada = false;
    public int idTipoLocalTroca = 0;
    public int idTipoCompra = 0;
    public int numeroParcela = 0;
    public int idProduto2 = 0;
    /* dados complementares */
    public boolean eBalanca = false;
    public int codigoBalanca = 0;
    public double idDouble = 0;
    public int pisCofinsDebitoAnt = -1;
    public int pisCofinsCreditoAnt = -1;
    public double idFamiliaProdutoDouble = 0;
    public int cest1 = -1;
    public int cest2 = -1;
    public int cest3 = -1;
    public int idCest = -1;
    public int idNormaReposicao = 1;
    
    
    
    /**
     * Executa um recálculo da margem de venda do produto.
     */
    public void recalcularMargem() {
        double preco, custo;
        if (this.vComplemento != null && !this.vComplemento.isEmpty()) {
            custo = this.vComplemento.get(0).getCustoComImposto();
            preco = this.vComplemento.get(0).getPrecoVenda();
        } else {
            custo = this.custoComImposto;
            preco = this.precoVenda;
        }
        
        if (custo > 0 && preco > 0) {
            this.margem = (( custo / preco ) * 100);     
        } else {
            this.margem = 0;
        }
    }
    
    /**
     * Valida as descrições do produto e as reajusta.
     */
    public void reajustarDescricoes() {
        if (getDescricaoReduzida() == null || getDescricaoReduzida().trim().equals("")) {
            setDescricaoReduzida(getDescricaoCompleta());
        }
        
        if (getDescricaoGondola() == null || getDescricaoGondola().trim().equals("")) {
            setDescricaoGondola(getDescricaoCompleta());
        }
    }

    public void setIdSituacaoCadastro(int idSituacaoCadastro) {
        this.idSituacaoCadastro = idSituacaoCadastro;
    }

    public int getIdSituacaoCadastro() {
        return idSituacaoCadastro;
    }
    
    public String getDescricaoMercadologico1() {
        return descricaoMercadologico1;
    }

    public void setDescricaoMercadologico1(String descricaoMercadologico1) {
        this.descricaoMercadologico1 = descricaoMercadologico1;
    }

    public String getDescricaoMercadologico2() {
        return descricaoMercadologico2;
    }

    public void setDescricaoMercadologico2(String descricaoMercadologico2) {
        this.descricaoMercadologico2 = descricaoMercadologico2;
    }

    public String getDescricaoMercadologico3() {
        return descricaoMercadologico3;
    }

    public void setDescricaoMercadologico3(String descricaoMercadologico3) {
        this.descricaoMercadologico3 = descricaoMercadologico3;
    }

    public String getDescricaoMercadologico4() {
        return descricaoMercadologico4;
    }

    public void setDescricaoMercadologico4(String descricaoMercadologico4) {
        this.descricaoMercadologico4 = descricaoMercadologico4;
    }

    public String getDescricaoMercadologico5() {
        return descricaoMercadologico5;
    }

    public void setDescricaoMercadologico5(String descricaoMercadologico5) {
        this.descricaoMercadologico5 = descricaoMercadologico5;
    }

    public double getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescricaoCompleta() {
        return descricaoCompleta;
    }

    public void setDescricaoCompleta(String descricaoCompleta) {
        this.descricaoCompleta = Utils.acertarTexto(descricaoCompleta, 60, "PRODUTO SEM DESCRICAO");
    }

    public int getMercadologico1() {
        return mercadologico1;
    }

    public void setMercadologico1(int mercadologico1) {
        this.mercadologico1 = mercadologico1;
    }

    public int getMercadologico2() {
        return mercadologico2;
    }

    public void setMercadologico2(int mercadologico2) {
        this.mercadologico2 = mercadologico2;
    }

    public int getMercadologico3() {
        return mercadologico3;
    }

    public void setMercadologico3(int mercadologico3) {
        this.mercadologico3 = mercadologico3;
    }

    public int getMercadologico4() {
        return mercadologico4;
    }

    public void setMercadologico4(int mercadologico4) {
        this.mercadologico4 = mercadologico4;
    }

    public int getMercadologico5() {
        return mercadologico5;
    }

    public void setMercadologico5(int mercadologico5) {
        this.mercadologico5 = mercadologico5;
    }

    public int getIdTipoEmbalagem() {
        return idTipoEmbalagem;
    }

    public void setIdTipoEmbalagem(int idTipoEmbalagem) {
        this.idTipoEmbalagem = idTipoEmbalagem;
    }

    public String getTipoEmbalagem() {
        return tipoEmbalagem;
    }

    public void setTipoEmbalagem(String tipoEmbalagem) {
        this.tipoEmbalagem = tipoEmbalagem;
    }

    public int getQtdEmbalagem() {
        return qtdEmbalagem;
    }

    public void setQtdEmbalagem(int qtdEmbalagem) {
        this.qtdEmbalagem = qtdEmbalagem;
    }

    public int getIdComprador() {
        return idComprador;
    }

    public void setIdComprador(int idComprador) {
        this.idComprador = idComprador;
    }

    public String getComprador() {
        return comprador;
    }

    public void setComprador(String comprador) {
        this.comprador = comprador;
    }

    public double getCustoFinal() {
        return custoFinal;
    }

    public void setCustoFinal(double custoFinal) {
        this.custoFinal = custoFinal;
    }

    public int getIdFamiliaProduto() {
        return idFamiliaProduto;
    }

    public void setIdFamiliaProduto(int idFamiliaProduto) {
        this.idFamiliaProduto = idFamiliaProduto;
    }

    public String getFamiliaProduto() {
        return familiaProduto;
    }

    public void setFamiliaProduto(String familiaProduto) {
        this.familiaProduto = familiaProduto;
    }

    public String getDescricaoReduzida() {
        return descricaoReduzida;
    }

    public void setDescricaoReduzida(String descricaoReduzida) {
        this.descricaoReduzida = Utils.acertarTexto(descricaoReduzida, 22, "PRODUTO SEM DESCRICAO");
    }

    public double getPesoLiquido() {
        return pesoLiquido;
    }

    public void setPesoLiquido(double pesoLiquido) {
        this.pesoLiquido = pesoLiquido;
    }

    public String getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(String dataCadastro) {
        if ((dataCadastro != null)
                && (!dataCadastro.trim().isEmpty())) {
            this.dataCadastro = dataCadastro.substring(0, 10).replace("-", "/");
        } else {
            this.dataCadastro = "";
        }
    }

    public int getValidade() {
        return validade;
    }

    public void setValidade(int validade) {
        this.validade = validade;
    }

    public double getPesoBruto() {
        return pesoBruto;
    }

    public void setPesoBruto(double pesoBruto) {
        this.pesoBruto = pesoBruto;
    }

    public int getComprimentoEmbalagem() {
        return comprimentoEmbalagem;
    }

    public void setComprimentoEmbalagem(int comprimentoEmbalagem) {
        this.comprimentoEmbalagem = comprimentoEmbalagem;
    }

    public int getLarguraEmbalagem() {
        return larguraEmbalagem;
    }

    public void setLarguraEmbalagem(int larguraEmbalagem) {
        this.larguraEmbalagem = larguraEmbalagem;
    }

    public int getAlturaEmbalagem() {
        return alturaEmbalagem;
    }

    public void setAlturaEmbalagem(int alturaEmbalagem) {
        this.alturaEmbalagem = alturaEmbalagem;
    }

    public double getPerda() {
        return perda;
    }

    public void setPerda(double perda) {
        this.perda = perda;
    }

    public double getMargem() {
        return margem;
    }

    public void setMargem(double margem) {
        this.margem = margem;
    }

    public boolean isVerificaCustoTabela() {
        return verificaCustoTabela;
    }

    public void setVerificaCustoTabela(boolean verificaCustoTabela) {
        this.verificaCustoTabela = verificaCustoTabela;
    }

    public double getPercentualIpi() {
        return percentualIpi;
    }

    public void setPercentualIpi(double percentualIpi) {
        this.percentualIpi = percentualIpi;
    }

    public double getPercentualFrete() {
        return percentualFrete;
    }

    public void setPercentualFrete(double percentualFrete) {
        this.percentualFrete = percentualFrete;
    }

    public double getPercentualEncargo() {
        return percentualEncargo;
    }

    public void setPercentualEncargo(double percentualEncargo) {
        this.percentualEncargo = percentualEncargo;
    }

    public double getPercentualPerda() {
        return percentualPerda;
    }

    public void setPercentualPerda(double percentualPerda) {
        this.percentualPerda = percentualPerda;
    }

    public double getPercentualSubstituicao() {
        return percentualSubstituicao;
    }

    public void setPercentualSubstituicao(double percentualSubstituicao) {
        this.percentualSubstituicao = percentualSubstituicao;
    }

    public String getDescricaoGondola() {
        return descricaoGondola;
    }

    public void setDescricaoGondola(String descricaoGondola) {
        this.descricaoGondola = Utils.acertarTexto(descricaoGondola, 60, "PRODUTO SEM DESCRICAO");
    }

    public String getDataAlteracao() {
        return dataAlteracao;
    }

    public void setDataAlteracao(String dataAlteracao) {
        this.dataAlteracao = dataAlteracao;
    }

    public int getIdProdutoVasilhame() {
        return idProdutoVasilhame;
    }

    public void setIdProdutoVasilhame(int idProdutoVasilhame) {
        this.idProdutoVasilhame = idProdutoVasilhame;
    }

    public String getProdutoVasilhame() {
        return produtoVasilhame;
    }

    public void setProdutoVasilhame(String produtoVasilhame) {
        this.produtoVasilhame = produtoVasilhame;
    }

    public int getNcm1() {
        return ncm1;
    }

    public void setNcm1(int ncm1) {
        this.ncm1 = ncm1;
    }

    public int getNcm2() {
        return ncm2;
    }

    public void setNcm2(int ncm2) {
        this.ncm2 = ncm2;
    }

    public int getNcm3() {
        return ncm3;
    }

    public void setNcm3(int ncm3) {
        this.ncm3 = ncm3;
    }

    public int getExcecao() {
        return excecao;
    }

    public void setExcecao(int excecao) {
        this.excecao = excecao;
    }

    public int getIdTipoMercadoria() {
        return idTipoMercadoria;
    }

    public void setIdTipoMercadoria(int idTipoMercadoria) {
        this.idTipoMercadoria = idTipoMercadoria;
    }

    public boolean isFabricacaoPropria() {
        return fabricacaoPropria;
    }

    public void setFabricacaoPropria(boolean fabricacaoPropria) {
        this.fabricacaoPropria = fabricacaoPropria;
    }

    public boolean isSugestaoPedido() {
        return sugestaoPedido;
    }

    public void setSugestaoPedido(boolean SugestaoPedido) {
        this.sugestaoPedido = SugestaoPedido;
    }

    public boolean isSugestaoCotacao() {
        return sugestaoCotacao;
    }

    public void setSugestaoCotacao(boolean sugestaoCotacao) {
        this.sugestaoCotacao = sugestaoCotacao;
    }

    public boolean isAceitaMultiplicacaoPdv() {
        return aceitaMultiplicacaoPdv;
    }

    public void setAceitaMultiplicacaoPdv(boolean aceitaMultiplicacaoPdv) {
        this.aceitaMultiplicacaoPdv = aceitaMultiplicacaoPdv;
    }

    public int getIdFornecedorFabricante() {
        return idFornecedorFabricante;
    }

    public void setIdFornecedorFabricante(int idFornecedorFabricante) {
        this.idFornecedorFabricante = idFornecedorFabricante;
    }

    public String getFornecedorFabricante() {
        return fornecedorFabricante;
    }

    public void setFornecedorFabricante(String fornecedorFabricante) {
        this.fornecedorFabricante = fornecedorFabricante;
    }

    public int getIdDivisaoFornecedor() {
        return idDivisaoFornecedor;
    }

    public void setIdDivisaoFornecedor(int idDivisaoFornecedor) {
        this.idDivisaoFornecedor = idDivisaoFornecedor;
    }

    public int getIdTipoProduto() {
        return idTipoProduto;
    }

    public void setIdTipoProduto(int idTipoProduto) {
        this.idTipoProduto = idTipoProduto;
    }

    public String getTipoProduto() {
        return tipoProduto;
    }

    public void setTipoProduto(String tipoProduto) {
        this.tipoProduto = tipoProduto;
    }

    public int getIdTipoPisCofins() {
        return idTipoPisCofinsDebito;
    }

    public void setIdTipoPisCofinsDebito(int idTipoPisCofins) {
        this.idTipoPisCofinsDebito = idTipoPisCofins;
    }

    public String getTipoPisCofins() {
        return tipoPisCofinsDebito;
    }

    public void setTipoPisCofins(String tipoPisCofins) {
        this.tipoPisCofinsDebito = tipoPisCofins;
    }

    public List<ProdutoComplementoVO> getvComplemento() {
        return vComplemento;
    }

    public void setvComplemento(List<ProdutoComplementoVO> vComplemento) {
        this.vComplemento = vComplemento;
    }

    public List<ProdutoAliquotaVO> getvAliquota() {
        return vAliquota;
    }

    public void setvAliquota(List<ProdutoAliquotaVO> vAliquota) {
        this.vAliquota = vAliquota;
    }

    public List<ProdutoAutomacaoVO> getvAutomacao() {
        return vAutomacao;
    }

    public void setvAutomacao(List<ProdutoAutomacaoVO> vAutomacao) {
        this.vAutomacao = vAutomacao;
    }

    public List<ProdutoAutomacaoLojaVO> getvAutomacaoLoja() {
        return vAutomacaoLoja;
    }

    public void setvAutomacaoLoja(List<ProdutoAutomacaoLojaVO> vAutomacaoLoja) {
        this.vAutomacaoLoja = vAutomacaoLoja;
    }
    
    public boolean isSazonal() {
        return sazonal;
    }

    public void setSazonal(boolean sazonal) {
        this.sazonal = sazonal;
    }

    public boolean isConsignado() {
        return consignado;
    }

    public void setConsignado(boolean consignado) {
        this.consignado = consignado;
    }

    public int getDdv() {
        return ddv;
    }

    public void setDdv(int ddv) {
        this.ddv = ddv;
    }

    public boolean isPermite() {
        return permiteTroca;
    }

    public void setPermiteTroca(boolean permiteTroca) {
        this.permiteTroca = permiteTroca;
    }

    public int getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(int temperatura) {
        this.temperatura = temperatura;
    }

    public int getIdTipoOrigemMercadoria() {
        return idTipoOrigemMercadoria;
    }

    public void setIdTipoOrigemMercadoria(int idTipoOrigemMercadoria) {
        this.idTipoOrigemMercadoria = idTipoOrigemMercadoria;
    }

    public List<ProdutoAutomacaoVO> getvAutomacaoExclusao() {
        return vAutomacaoExclusao;
    }

    public void setvAutomacaoExclusao(List<ProdutoAutomacaoVO> vAutomacaoExclusao) {
        this.vAutomacaoExclusao = vAutomacaoExclusao;
    }

    public String getAliquotaCredito() {
        return aliquotaCredito;
    }

    public void setAliquotaCredito(String aliquotaCredito) {
        this.aliquotaCredito = aliquotaCredito;
    }

    public double getPrecoVenda() {
        return precoVenda;
    }

    public void setPrecoVenda(double precoVenda) {
        this.precoVenda = precoVenda;
    }

    public String getSituacaoCadastro() {
        return situacaoCadastro;
    }

    public void setSituacaoCadastro(String situacaoCadastro) {
        this.situacaoCadastro = situacaoCadastro;
    }

    public String getAliquotaDebito() {
        return aliquotaDebito;
    }

    public void setAliquotaDebito(String aliquotaDebito) {
        this.aliquotaDebito = aliquotaDebito;
    }

    public long getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(long codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    public double getCustoComImposto() {
        return custoComImposto;
    }

    public void setCustoComImposto(double custoComImposto) {
        this.custoComImposto = custoComImposto;
    }

    public double getCodigoAnterior() {
        return codigoAnterior;
    }

    public void setCodigoAnterior(double codigoAnterior) {
        this.codigoAnterior = codigoAnterior;
    }

    public void setPesavel(boolean pesavel) {
        this.pesavel = pesavel;
    }

    public boolean isPesavel() {
        return pesavel;
    }

    public int getIdTipoPisCofinsCredito() {
        return idTipoPisCofinsCredito;
    }

    public void setIdTipoPisCofinsCredito(int idTipoPisCofinsCredito) {
        this.idTipoPisCofinsCredito = idTipoPisCofinsCredito;
    }

    public boolean isVendaControlada() {
        return vendaControlada;
    }

    public void setVendaControlada(boolean vendaControlada) {
        this.vendaControlada = vendaControlada;
    }

    public boolean isVendaPdv() {
        return vendaPdv;
    }

    public void setVendaPdv(boolean vendaPdv) {
        this.vendaPdv = vendaPdv;
    }

    public boolean isConferido() {
        return conferido;
    }

    public void setConferido(boolean conferido) {
        this.conferido = conferido;
    }

    public boolean isPermiteQuebra() {
        return permiteQuebra;
    }

    public void setPermiteQuebra(boolean permiteQuebra) {
        this.permiteQuebra = permiteQuebra;
    }

    public int getTipoNaturezaReceita() {
        return tipoNaturezaReceita;
    }

    public void setTipoNaturezaReceita(int tipoNaturezaReceita) {
        this.tipoNaturezaReceita = tipoNaturezaReceita;
    }

    public List<CodigoAnteriorVO> getvCodigoAnterior() {
        return vCodigoAnterior;
    }

    public int getCest1() {
        return cest1;
    }

    public void setCest1(int cest1) {
        this.cest1 = cest1;
    }

    public int getCest2() {
        return cest2;
    }

    public void setCest2(int cest2) {
        this.cest2 = cest2;
    }

    public int getCest3() {
        return cest3;
    }

    public void setCest3(int cest3) {
        this.cest3 = cest3;
    }

    public int getIdCest() {
        return idCest;
    }

    public void setIdCest(int idCest) {
        this.idCest = idCest;
    }
    
    public double getIdDouble() {
        return idDouble;
    }
    
    public void setIdDouble(double idDouble) {
        this.idDouble = idDouble;
    }

    public int getIdNormaReposicao() {
        return idNormaReposicao;
    }

    public void setIdNormaReposicao(int idNormaReposicao) {
        this.idNormaReposicao = idNormaReposicao;
    }
    
}