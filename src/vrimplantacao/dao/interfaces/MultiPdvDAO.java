/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao.dao.interfaces;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.dao.cadastro.NcmDAO;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.dao.cadastro.ProdutoDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao.vo.vrimplantacao.CodigoAnteriorVO;
import vrimplantacao.vo.vrimplantacao.NcmVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAliquotaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAutomacaoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoComplementoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoVO;

/**
 *
 * @author lucasrafael
 */
public class MultiPdvDAO {
    
    public void importarProdutoBalanca(String arquivo, int opcao) throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Produtos de Balanca...");
            List<ProdutoBalancaVO> vProdutoBalanca = new ProdutoBalancaDAO().carregar(arquivo, opcao);

            new ProdutoBalancaDAO().salvar(vProdutoBalanca);
        } catch (Exception ex) {

            throw ex;
        }
    }
    
    public void importarProdutos(String i_arquivo, int i_idLojaDestino) throws Exception {
        try {
            
            ProgressBar.setStatus("Carregando dados para importação...");
            List<ProdutoVO> vProduto = carregarProdutos(i_arquivo);
            List<LojaVO> vLoja = new LojaDAO().carregar();
                       
            ProgressBar.setMaximum(vProduto.size());

            ProdutoDAO produtoDAO = new ProdutoDAO();
            produtoDAO.implantacaoExterna = true;
            produtoDAO.salvar(vProduto, i_idLojaDestino, vLoja);
            
        } catch (Exception e) {
            throw e;
        }
    }

    private List<ProdutoVO> carregarProdutos(String i_arquivo) throws Exception {
        List<ProdutoVO> vProdutoOrigem = new ArrayList<>();
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;        
        double precovenda, custoComImposto, custoSemImposto;        
        int idProduto, referencia, codigoBalanca, idTipoEmbalagem, validade = 0,
            ncm1, ncm2, ncm3, idAliquota, idTipoPisCofins, idTipoPisCofinsCredito,
            tipoNaturezaReceita;
        long codigoBarras;
        boolean eBalanca, pesavel;
        String linha = "", descricaoCompleta, descriacaoReduzida, descricaoGondola, strTipoEmbalagem,
               strNcm, strCodigoBarras, strAliquota, strPisCofins, strValorAliquota;
        BufferedReader br = null;
        Utils util = new Utils();
        
        try {
            
            br = new BufferedReader(new InputStreamReader(new FileInputStream(i_arquivo), "UTF-8"));
            
            Conexao.begin();
            
            stm = Conexao.createStatement();
            
            while ((linha = br.readLine()) != null) {
                
                                
                idProduto = Integer.parseInt(linha.substring(0, 13));
                descricaoCompleta = util.acertarTexto(linha.substring(20, 57).replace("'", "").trim());
                strTipoEmbalagem = util.acertarTexto(linha.substring(102, 104).trim());
                strNcm = util.formataNumero(linha.substring(105, 112).trim());
                strCodigoBarras = util.formataNumero(linha.substring(167, 185).trim());
                strAliquota = linha.substring(190, 203).trim();
                strPisCofins = linha.substring(linha.length() - 4);                
                strValorAliquota = linha.substring(113, 130).trim();
                
                if (!strValorAliquota.trim().isEmpty()) {
                    strValorAliquota = strValorAliquota.substring(0, strValorAliquota.length()-3);
                }
                
                eBalanca = false;
                codigoBalanca = 0;
                referencia = -1;
                pesavel = false;
                
                sql = new StringBuilder();
                sql.append("select codigo, descricao, pesavel, validade ");
                sql.append("from implantacao.produtobalanca ");
                sql.append("where codigo = " + idProduto);
                
                rst = stm.executeQuery(sql.toString());
                
                if (rst.next()) {
                    eBalanca = true;
                    codigoBalanca = rst.getInt("codigo");
                    validade = rst.getInt("validade");
                    
                    if ("P".equals(rst.getString("pesavel"))) {
                        idTipoEmbalagem = 4;
                        pesavel = false;
                    } else {
                        idTipoEmbalagem = 0;
                        pesavel = true;
                    }
                } else {
                    
                    validade = 0;
                    
                    if ("KG".equals(strTipoEmbalagem)) {
                        idTipoEmbalagem = 4;
                    } else {
                        idTipoEmbalagem = 0;
                    }
                }
                
                descriacaoReduzida = descricaoCompleta;
                descricaoGondola = descricaoCompleta;
                
                if (strNcm.length() > 5) {
                    NcmVO oNcm = new NcmDAO().validar(strNcm);

                    ncm1 = oNcm.ncm1;
                    ncm2 = oNcm.ncm2;
                    ncm3 = oNcm.ncm3;                    
                } else {
                    ncm1 = 402;
                    ncm2 = 99;
                    ncm3 = 0;                    
                }

                
                idAliquota = retornarIcms(strAliquota, strValorAliquota);
                
                if (!strPisCofins.trim().isEmpty()) {
                    idTipoPisCofins = retornaPisCofinsDebito(strPisCofins);
                    idTipoPisCofinsCredito = retornaPisCofinsCredito(strPisCofins);
                    tipoNaturezaReceita = util.retornarTipoNaturezaReceita(idTipoPisCofins, "");
                } else {
                    idTipoPisCofins = 1;
                    idTipoPisCofinsCredito = 13;
                    tipoNaturezaReceita = 999;
                }
                
                // codigobarras
                if (eBalanca) {
                    codigoBarras = idProduto;
                } else {
                    if (idProduto >= 10000) {
                        
                        if (strCodigoBarras.length() < 7) {
                            
                            codigoBarras = util.gerarEan13(idProduto, true);
                            
                        } else {
                            
                            codigoBarras = Long.parseLong(strCodigoBarras);
                        }
                    } else {
                        
                        if (strCodigoBarras.length() < 7) {
                            
                            codigoBarras = util.gerarEan13(idProduto, false);
                        } else {
                            codigoBarras = Long.parseLong(strCodigoBarras);
                        }
                    }
                }
                
                if (descricaoCompleta.length() > 60) {
                    descricaoCompleta = descricaoCompleta.substring(0, 60);
                }
                
                if (descriacaoReduzida.length() > 22) {
                    descriacaoReduzida = descriacaoReduzida.substring(0, 22);
                }
                
                if (descricaoGondola.length() > 60) {
                    descricaoGondola = descricaoGondola.substring(0, 60);
                }
                
                ProdutoVO oProduto = new ProdutoVO();                
                oProduto.id = idProduto;
                oProduto.descricaoCompleta = descricaoCompleta;
                oProduto.descricaoReduzida = descriacaoReduzida;
                oProduto.descricaoGondola = descricaoGondola;
                oProduto.idTipoEmbalagem = idTipoEmbalagem;
                oProduto.qtdEmbalagem = 1;
                oProduto.pesavel = pesavel;
                oProduto.mercadologico1 = 14;
                oProduto.mercadologico2 = 1;
                oProduto.mercadologico3 = 1;
                oProduto.validade = validade;
                oProduto.ncm1 = ncm1;
                oProduto.ncm2 = ncm2;
                oProduto.ncm3 = ncm3;                
                oProduto.idFamiliaProduto = -1;
                oProduto.sugestaoPedido = true;
                oProduto.aceitaMultiplicacaoPdv = true;
                oProduto.sazonal = false;
                oProduto.fabricacaoPropria = false;
                oProduto.consignado = false;
                oProduto.ddv = 0;
                oProduto.permiteTroca = true;
                oProduto.vendaControlada = false;
                oProduto.vendaPdv = true;
                oProduto.conferido = true;
                oProduto.permiteQuebra = true;
                oProduto.idComprador = 1;
                oProduto.idFornecedorFabricante = 1;
                oProduto.idTipoPisCofinsDebito = idTipoPisCofins;
                oProduto.idTipoPisCofinsCredito = idTipoPisCofinsCredito;
                oProduto.tipoNaturezaReceita = tipoNaturezaReceita;
                
                ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                oComplemento.idSituacaoCadastro = 1;
                oProduto.vComplemento.add(oComplemento);
                
                ProdutoAliquotaVO oAliquota = new ProdutoAliquotaVO();
                oAliquota.idEstado = 33;
                oAliquota.idAliquotaDebito = idAliquota;
                oAliquota.idAliquotaCredito = idAliquota;
                oAliquota.idAliquotaDebitoForaEstado = idAliquota;
                oAliquota.idAliquotaCreditoForaEstado = idAliquota;
                oAliquota.idAliquotaDebitoForaEstadoNF = idAliquota;
                oProduto.vAliquota.add(oAliquota);
                                
                ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();                
                oAutomacao.codigoBarras = codigoBarras;
                oAutomacao.idTipoEmbalagem = idTipoEmbalagem;
                oAutomacao.qtdEmbalagem = 1;                
                oProduto.vAutomacao.add(oAutomacao);
                
                CodigoAnteriorVO oCodigoAnterior = new CodigoAnteriorVO();                
                oCodigoAnterior.codigoanterior = idProduto;
                oCodigoAnterior.barras = Long.parseLong(strCodigoBarras);
                oCodigoAnterior.piscofinsdebito = Integer.parseInt(strPisCofins);
                oCodigoAnterior.piscofinscredito = Integer.parseInt(strPisCofins);
                oCodigoAnterior.ref_icmsdebito = strAliquota+" "+strValorAliquota;
                oCodigoAnterior.e_balanca = eBalanca;
                oCodigoAnterior.codigobalanca = codigoBalanca;
                oCodigoAnterior.ncm = strNcm;                
                oProduto.vCodigoAnterior.add(oCodigoAnterior);
                
                vProdutoOrigem.add(oProduto);
                
                //System.out.println(idProduto+" "+descricaoCompleta+" "+strTipoEmbalagem+" "+strNcm+" "
                //+strCodigoBarras+" "+strAliquota+" "+strPisCofins+" "+strValorAliquota);
            }

            Conexao.commit();
            return vProdutoOrigem;       

        } catch (Exception e) {
            throw e;
        }
    }
    
    private int retornaPisCofinsDebito(String cst) {
        int retorno = 1;

        if ("5050".equals(cst)) {
            retorno = 0;
        } else if ("7171".equals(cst)) {
            retorno = 1;
        } else if ("7575".equals(cst)) {
            retorno = 2;
        } else if ("7070".equals(cst)) {
            retorno = 3;
        } else if ("6060".equals(cst)) {
            retorno = 5;
        } else if ("5151".equals(cst)) {
            retorno = 6;
        } else if ("7373".equals(cst)) {
            retorno = 7;
        } else if ("7474".equals(cst)) {
            retorno = 8;
        } else if ("9999".equals(cst)) {
            retorno = 9;
        } else {
            retorno = 1;
        }
        
        return retorno;
    }

    private int retornaPisCofinsCredito(String cst) {
        int retorno = 1;

        if ("5050".equals(cst)) {
            retorno = 12;
        } else if ("7171".equals(cst)) {
            retorno = 13;
        } else if ("7575".equals(cst)) {
            retorno = 14;
        } else if ("7070".equals(cst)) {
            retorno = 15;
        } else if ("6060".equals(cst)) {
            retorno = 17;
        } else if ("5151".equals(cst)) {
            retorno = 18;
        } else if ("7373".equals(cst)) {
            retorno = 19;
        } else if ("7474".equals(cst)) {
            retorno = 20;
        } else if ("9999".equals(cst)) {
            retorno = 21;
        } else {
            retorno = 1;
        }
        
        return retorno;
    }
    
    private int retornarIcms(String codigo, String valAliquota) {
        
        int retorno = 8;
        
        if ("F".equals(codigo)) {
            retorno = 7;
        } else if ("I".equals(codigo)) {
            retorno = 6;
        } else if ("T".equals(codigo) &&
                ("7".equals(valAliquota))) {
            retorno = 0;
        } else if ("T".equals(codigo) &&
                ("8".equals(valAliquota))) {
            retorno = 18;
        } else if ("T".equals(codigo) &&
                ("19".equals(valAliquota))) {
            retorno = 19;
        } else if ("T".equals(codigo) &&
                ("26".equals(valAliquota))) {
            retorno = 20;
        } else {
            retorno = 8;
        }
        
        return retorno;
    }
}
