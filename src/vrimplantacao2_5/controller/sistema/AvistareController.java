/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2_5.controller.sistema;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.interfaces.AvistareDAO;
import vrimplantacao2_5.controller.interfaces.InterfaceController;
import vrimplantacao2_5.vo.checks.migracao.OpcoesMigracaoVO;
import vrimplantacao2_5.vo.sistema.AvistareVO;

/**
 *
 * @author Desenvolvimento
 */
public class AvistareController extends InterfaceController {

    private OpcoesMigracaoVO opcoesMigracaoVO = null;
    private AvistareDAO dao = null;
    private final String SISTEMA = "Avistare";
    private String complementoSistema = "";
    
    public AvistareController() {}
    
    public AvistareController(OpcoesMigracaoVO opcoesMigracaoVO, AvistareDAO dao) {
        this.dao = dao;
        this.opcoesMigracaoVO = opcoesMigracaoVO;
    }
    
    @Override
    public String getSistema() {
        return (!"".equals(complementoSistema) ? this.complementoSistema + "-" : "") + SISTEMA;
    }
    
    public String getComplementoSistema() {
        return this.complementoSistema;
    }
    
    public void setComplementoSistema(String complementoSistema) {
        this.complementoSistema = complementoSistema == null ? "" : complementoSistema.trim();
    }
    
    public void setAvistare(AvistareVO avistareVO, String lojaOrigem) {
        dao.avistareVO = avistareVO;
        dao.setLojaCliente(lojaOrigem);
    }
    
    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                new OpcaoProduto[]{
                    OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                    OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                    OpcaoProduto.PRODUTOS,
                    OpcaoProduto.EAN,
                    OpcaoProduto.EAN_EM_BRANCO,
                    OpcaoProduto.DESC_COMPLETA,                    
                    OpcaoProduto.DESC_REDUZIDA,
                    OpcaoProduto.DESC_GONDOLA,
                    OpcaoProduto.PESO_BRUTO,
                    OpcaoProduto.PESO_LIQUIDO,
                    OpcaoProduto.DATA_CADASTRO,
                    OpcaoProduto.MARGEM,
                    OpcaoProduto.CUSTO,
                    OpcaoProduto.CUSTO_SEM_IMPOSTO,
                    OpcaoProduto.CUSTO_COM_IMPOSTO,
                    OpcaoProduto.PRECO,
                    OpcaoProduto.ESTOQUE,
                    OpcaoProduto.ESTOQUE_MINIMO,
                    OpcaoProduto.ESTOQUE_MAXIMO,
                    OpcaoProduto.NCM,
                    OpcaoProduto.ATIVO,
                    OpcaoProduto.CEST,
                    OpcaoProduto.PIS_COFINS,
                    OpcaoProduto.ICMS,
                    OpcaoProduto.ICMS_SAIDA,
                    OpcaoProduto.ICMS_SAIDA_FORA_ESTADO,
                    OpcaoProduto.ICMS_SAIDA_NF,
                    OpcaoProduto.ICMS_ENTRADA,
                    OpcaoProduto.ICMS_ENTRADA_FORA_ESTADO,
                    OpcaoProduto.ICMS_CONSUMIDOR,
                    OpcaoProduto.VOLUME_QTD,
                    OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                    OpcaoProduto.TIPO_EMBALAGEM_EAN
                }
        ));
    }    
}
