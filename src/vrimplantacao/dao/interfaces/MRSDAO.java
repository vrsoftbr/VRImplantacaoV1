package vrimplantacao.dao.interfaces;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Date;
import vrimplantacao.dao.cadastro.FornecedorDAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrframework.classe.VRException;
import vrimplantacao.classe.ConexaoMySQL;
import vrimplantacao.classe.Global;
import vrimplantacao.dao.cadastro.ClientePreferencialDAO;
import vrimplantacao.dao.cadastro.FamiliaProdutoDAO;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.dao.cadastro.MercadologicoDAO;
import vrimplantacao.dao.cadastro.NcmDAO;
import vrimplantacao.dao.cadastro.PlanoDAO;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.dao.cadastro.ProdutoDAO;
import vrimplantacao.dao.cadastro.ProdutoFornecedorDAO;
import vrimplantacao.dao.cadastro.ReceberChequeDAO;
import vrimplantacao.dao.cadastro.ReceberCreditoRotativoDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao.vo.vrimplantacao.ClientePreferencialVO;
import vrimplantacao.vo.vrimplantacao.CodigoAnteriorVO;
import vrimplantacao.vo.vrimplantacao.FamiliaProdutoVO;
import vrimplantacao.vo.vrimplantacao.MercadologicoVO;
import vrimplantacao.vo.vrimplantacao.FornecedorVO;
import vrimplantacao.vo.vrimplantacao.ProdutoFornecedorVO;
import vrimplantacao.vo.vrimplantacao.NcmVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAliquotaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAutomacaoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoComplementoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoVO;
import vrimplantacao.vo.vrimplantacao.ReceberChequeVO;
import vrimplantacao.vo.vrimplantacao.ReceberCreditoRotativoVO;

public class MRSDAO {

    // CARREGAMENTOS
    
    public List<ProdutoVO> carregarProdutosAnalise() throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        long codigoBarras;
        double precoVenda;
        String descricao, strCodigoBarras;
        
        try {
            stm = ConexaoMySQL.getConexao().createStatement();
            sql = new StringBuilder();
            sql.append("select cod_barras, nome, preco_venda\n" +
                        "  from mrs_cliente.produto");
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                precoVenda = 0;
                descricao = "";
                if ((rst.getString("cod_barras") != null) && (!rst.getString("cod_barras").trim().isEmpty())) {
                    strCodigoBarras = Utils.formataNumero(rst.getString("cod_barras").trim());
                    precoVenda = rst.getDouble("preco_venda");
                    
                    if ((rst.getString("nome") !=  null) && (!rst.getString("nome").trim().isEmpty())) {
                        descricao = Utils.acertarTexto(rst.getString("nome").trim());
                    } else {
                        descricao = "";
                    }

                    if (strCodigoBarras.length() > 14) {
                        codigoBarras = Long.parseLong(strCodigoBarras.substring(0, 14));
                    } else {
                        codigoBarras = Long.parseLong(strCodigoBarras);
                    }

                    if (codigoBarras > 999999) {
                        ProdutoVO oProduto = new ProdutoVO();
                        oProduto.codigoBarras = codigoBarras;
                        oProduto.descricaoCompleta = descricao;
                        oProduto.precoVenda = precoVenda;
                        vProduto.add(oProduto);
                    }
                }
            }
            stm.close();
            return vProduto;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void gerarArquivoAnalise(String i_arquivo, int idLoja, int opcao) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Produto...");
            vProduto = carregarProdutosAnalise();
            
            if (!vProduto.isEmpty()) {
                new ProdutoDAO().gerarArquivoAnalise(i_arquivo, vProduto, idLoja, opcao);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    private List<FamiliaProdutoVO> carregarFamiliaProduto() throws Exception {
        List<FamiliaProdutoVO> vFamiliaProduto = new ArrayList<>();
        Utils util = new Utils();
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        int idFamilia;
        String descricao;

        try {
            stm = ConexaoMySQL.getConexao().createStatement();
            sql = new StringBuilder();
            sql.append("SELECT DISTINCT ID, DESCRICAO FROM PRODUTOFAMILIA ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                idFamilia = Integer.parseInt(rst.getString("ID").trim());
                descricao = util.acertarTexto(rst.getString("DESCRICAO").trim().replace("'", ""));

                if (descricao.length() > 40) {
                    descricao = descricao.substring(0, 40);
                }

                FamiliaProdutoVO oFamilia = new FamiliaProdutoVO();
                oFamilia.id = idFamilia;
                oFamilia.descricao = descricao;
                oFamilia.codigoant = idFamilia;

                vFamiliaProduto.add(oFamilia);
            }

            return vFamiliaProduto;
        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<MercadologicoVO> carregarMercadologico(int nivel) throws Exception {

        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        List<MercadologicoVO> vMercadologico = new ArrayList<>();
        String descricao = "";

        try {

            stm = ConexaoMySQL.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append(" select g.id_grupo cod_m1, g.grupo desc_m1,             ");
            sql.append("        coalesce(s.id_sub_grupo, g.id_grupo) cod_m2, s.sub_grupo desc_m2 ");
            sql.append("   from mrs.produto_grupo_sub s                         ");
            sql.append("  right join mrs.produto_grupo g on                     ");
            sql.append("        g.grupo = s.grupo                               ");
            sql.append("  order by cod_m1, cod_m2                               ");
            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                MercadologicoVO oMercadologico = new MercadologicoVO();

                if (nivel == 1) {
                    descricao = Utils.acertarTexto(rst.getString("DESC_M1").replace("'", ""));

                    if (descricao.length() > 35) {
                       descricao = descricao.substring(0, 35);
                    }

                    oMercadologico.mercadologico1 = rst.getInt("COD_M1");
                    oMercadologico.mercadologico2 = 0;
                    oMercadologico.mercadologico3 = 0;
                    oMercadologico.mercadologico4 = 0;
                    oMercadologico.mercadologico5 = 0;
                    oMercadologico.descricao = descricao;
                    oMercadologico.nivel = nivel;

                } else if (nivel == 2) {

                    if ((rst.getString("DESC_M2") != null) &&
                            (!rst.getString("DESC_M2").trim().isEmpty())) {
                        descricao = Utils.acertarTexto(rst.getString("DESC_M2").replace("'", ""));
                    } else {
                        descricao = Utils.acertarTexto(rst.getString("DESC_M1").replace("'", ""));
                    }
                    
                    if (descricao.length() > 35) {
                        descricao = descricao.substring(0, 35);
                    }

                    oMercadologico.mercadologico1 = rst.getInt("COD_M1");
                    oMercadologico.mercadologico2 = rst.getInt("COD_M2");
                    oMercadologico.mercadologico3 = 0;
                    oMercadologico.mercadologico4 = 0;
                    oMercadologico.mercadologico5 = 0;
                    oMercadologico.descricao = descricao;
                    oMercadologico.nivel = nivel;
                } else if (nivel == 3) {

                    if ((rst.getString("DESC_M2") != null) &&
                            (!rst.getString("DESC_M2").trim().isEmpty())) {
                        descricao = Utils.acertarTexto(rst.getString("DESC_M2").replace("'", ""));
                    } else {
                        descricao = Utils.acertarTexto(rst.getString("DESC_M1").replace("'", ""));
                    }

                    if (descricao.length() > 35) {

                        descricao = descricao.substring(0, 35);
                    }
                    
                    oMercadologico.mercadologico1 = rst.getInt("COD_M1");
                    oMercadologico.mercadologico2 = rst.getInt("COD_M2");
                    oMercadologico.mercadologico3 = rst.getInt("COD_M2");
                    oMercadologico.mercadologico4 = 0;
                    oMercadologico.mercadologico5 = 0;
                    oMercadologico.descricao = descricao;
                    oMercadologico.nivel = nivel;
                }

                vMercadologico.add(oMercadologico);
            }
            stm.close();
            return vMercadologico;

        } catch (Exception ex) {

            throw ex;
        }
    }

    private List<ProdutoVO> carregarProduto() throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        StringBuilder sql = null;
        Statement stm = null, stmPG = null;
        ResultSet rst = null, rstPG = null;
        int idProduto, idTipoEmbalagem, qtdEmbalagem, idSituacaoCadastro = 1,
                idTipoPisCofinsDebito, idTipoPisCofinsCredito, tipoNaturezaReceita, validade,
                idFamilia = -1, codigoBalanca, mercadologico1, mercadologico2, mercadologico3,
                ncm1, ncm2, ncm3, aliquotaICMS=8;
        String descricaoCompleta, descricaoReduzida, descricaoGondola, dataCadastro = "",
                strCodigoBarras, strNcm = "", strBarrasAnterior = "";
        boolean pesavel, eBalanca;
        double pesoLiq = 0, pesoBruto = 0, margem = 0, custo = 0, precoVenda = 0;
        long codigoBarras = -2;
        
        Conexao.begin();
        stmPG = Conexao.createStatement();
        
        sql = new StringBuilder();
        sql.append("SELECT min(MERCADOLOGICO1) as MERCADOLOGICO1 FROM MERCADOLOGICO ");
        rstPG = stmPG.executeQuery(sql.toString());        
        if (rstPG.next()){
            Global.mercadologicoPadrao1 = rstPG.getInt("MERCADOLOGICO1");
            Global.mercadologicoPadrao2 = 1;
            Global.mercadologicoPadrao3 = 1; 
        }
        try {
            stm = ConexaoMySQL.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("select * from mrs.produto order by id_produto");
            
            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                idProduto = Integer.parseInt(rst.getString("id_produto").trim());

                if ((rst.getString("cod_barras") != null)
                        && (!rst.getString("cod_barras").trim().isEmpty())) {

                    if (("2".equals(rst.getString("cod_barras").trim().substring(0, 1)))
                            && ("00".equals(rst.getString("cod_barras").trim().substring(
                                            rst.getString("cod_barras").trim().length() - 2)))) {

                        sql = new StringBuilder();
                        sql.append("select codigo, descricao, pesavel, validade ");
                        sql.append("from implantacao.produtobalanca ");
                        sql.append("where codigo = " + 
                                Double.parseDouble(rst.getString("cod_barras").substring(1, 
                                        rst.getString("cod_barras").trim().length() -2)));

                        rstPG = stmPG.executeQuery(sql.toString());

                        if (rstPG.next()) {
                            eBalanca = true;
                            codigoBalanca = rstPG.getInt("codigo");
                            validade = rstPG.getInt("validade");

                            if ("P".equals(rstPG.getString("pesavel").trim())) {
                                idTipoEmbalagem = 4;
                                pesavel = false;
                            } else {
                                idTipoEmbalagem = 0;
                                pesavel = true;
                            }
                        } else {
                            pesavel = false;
                            eBalanca = false;
                            codigoBalanca = -1;
                            validade = 0;
                            if ("KG".equals(rst.getString("produto_unidade").trim())) {
                                idTipoEmbalagem = 4;
                            } else if ("LT".equals(rst.getString("produto_unidade").trim())) {
                                idTipoEmbalagem = 9;
                            } else if ("UN".equals(rst.getString("produto_unidade").trim())) {
                                idTipoEmbalagem = 0;
                            } else if ("CX".equals(rst.getString("produto_unidade").trim())) {
                                idTipoEmbalagem = 1;
                            } else if ("FD".equals(rst.getString("produto_unidade").trim())) {
                                idTipoEmbalagem = 5;
                            } else if ("PC".equals(rst.getString("produto_unidade").trim())) {
                                idTipoEmbalagem = 3;
                            } else {
                                idTipoEmbalagem = 0;
                            }
                        }
                    } else {                        
                        pesavel = false;
                        eBalanca = false;
                        codigoBalanca = -1;
                        validade = 0;
                        if ("KG".equals(rst.getString("produto_unidade").trim())) {
                            idTipoEmbalagem = 4;
                        } else if ("LT".equals(rst.getString("produto_unidade").trim())) {
                            idTipoEmbalagem = 9;
                        } else if ("UN".equals(rst.getString("produto_unidade").trim())) {
                            idTipoEmbalagem = 0;
                        } else if ("CX".equals(rst.getString("produto_unidade").trim())) {
                            idTipoEmbalagem = 1;
                        } else if ("FD".equals(rst.getString("produto_unidade").trim())) {
                            idTipoEmbalagem = 5;
                        } else if ("PC".equals(rst.getString("produto_unidade").trim())) {
                            idTipoEmbalagem = 3;
                        } else {
                            idTipoEmbalagem = 0;
                        }                        
                    }
                } else {                    
                    pesavel = false;
                    eBalanca = false;
                    codigoBalanca = -1;
                    validade = 0;
                    if ("KG".equals(rst.getString("produto_unidade").trim())) {
                        idTipoEmbalagem = 4;
                    } else if ("LT".equals(rst.getString("produto_unidade").trim())) {
                        idTipoEmbalagem = 9;
                    } else if ("UN".equals(rst.getString("produto_unidade").trim())) {
                        idTipoEmbalagem = 0;
                    } else if ("CX".equals(rst.getString("produto_unidade").trim())) {
                        idTipoEmbalagem = 1;
                    } else if ("FD".equals(rst.getString("produto_unidade").trim())) {
                        idTipoEmbalagem = 5;
                    } else if ("PC".equals(rst.getString("produto_unidade").trim())) {
                        idTipoEmbalagem = 3;
                    } else {
                        idTipoEmbalagem = 0;
                    }
                }                
                
                qtdEmbalagem       = 1;
                idSituacaoCadastro = 1;                    

                if ((rst.getString("NOME") != null)
                        && (!rst.getString("NOME").trim().isEmpty())) {
                    byte[] bytes = rst.getBytes("NOME");
                    String textoAcertado = new String(bytes, "ISO-8859-1");
                    descricaoCompleta = Utils.acertarTexto(textoAcertado.replace("'", "").trim());
                } else {
                    descricaoCompleta = "PRODUTO SEM DESCRICAO";
                }
                descricaoReduzida = descricaoCompleta;
                descricaoGondola  = descricaoCompleta;

                if ((rst.getString("CODIGO") != null)
                        && (!rst.getString("CODIGO").trim().isEmpty())) {

                    strNcm = Utils.formataNumero(rst.getString("CODIGO").trim());

                    NcmVO oNcm = new NcmDAO().validar(strNcm);

                    ncm1 = oNcm.ncm1;
                    ncm2 = oNcm.ncm2;
                    ncm3 = oNcm.ncm3;

                } else {
                    ncm1 = 402;
                    ncm2 = 99;
                    ncm3 = 0;
                }

                if ((rst.getString("ID_GRUPO") != null)
                        && (!rst.getString("ID_GRUPO").trim().isEmpty())
                        && (rst.getString("ID_SUBGRUPO") != null)
                        && (!rst.getString("ID_SUBGRUPO").trim().isEmpty())) {
                    mercadologico1 = Integer.parseInt(rst.getString("ID_GRUPO").trim());
                    mercadologico2 = Integer.parseInt(rst.getString("ID_SUBGRUPO").trim());
                    mercadologico3 = Integer.parseInt(rst.getString("ID_SUBGRUPO").trim());
                } else {
                    mercadologico1 = Global.mercadologicoPadrao1;
                    mercadologico2 = Global.mercadologicoPadrao1;
                    mercadologico3 = Global.mercadologicoPadrao1;
                }

                if (eBalanca) {
                    codigoBarras = idProduto;
                } else {
                    
                    // USAR ASSIM APENAS PARA IMPORTAR A LANCHONETE DO CONFIANÇA... 
                    // DEPOIS COMENTAR E USAR O METODO ABAIXO.
                    // ---****---                    
                    //strCodigoBarras = util.formataNumero(rst.getString("COD_BARRAS").replace(".", "").trim());                    
                    //codigoBarras = Long.parseLong(strCodigoBarras);                    
                    // ---****---
                    
                    if ((rst.getString("COD_BARRAS") != null)
                            && (!rst.getString("COD_BARRAS").trim().isEmpty())) {

                        strCodigoBarras = Utils.formataNumero(rst.getString("COD_BARRAS").trim());
                        
                        if (strCodigoBarras.length() < 7) {
                            codigoBarras = -2;
                        } else {
                            if (("8".equals(strCodigoBarras.trim().substring(0, 1))) &&
                                    (strCodigoBarras.length() > 14)) {
                                strCodigoBarras = strCodigoBarras.substring(1, 15);
                            } else if (!"8".equals(strCodigoBarras.trim().substring(0, 1)) &&
                                    (strCodigoBarras.length() == 20)) {
                                strCodigoBarras = strCodigoBarras.substring(0, 13);
                            } else if ((!"8".equals(strCodigoBarras.trim().substring(0, 1))) &&
                                    (strCodigoBarras.length() >= 21)) {
                                strCodigoBarras = strCodigoBarras.substring(0, 14);
                            } else {
                                codigoBarras = Long.parseLong(strCodigoBarras);
                            }
                        }
                    } else {
                        codigoBarras = -2;
                    }
                }
                
                idFamilia = -1;  
                idTipoPisCofinsDebito   = 1;
                idTipoPisCofinsCredito  = 13;
                tipoNaturezaReceita     = Utils.retornarTipoNaturezaReceita(idTipoPisCofinsDebito, "");                                            

                if ((rst.getString("aliquota") != null)
                        && (!rst.getString("aliquota").trim().isEmpty())) {
                    aliquotaICMS = retornarAliquotaICMS(rst.getString("aliquota"));
                } else {
                    aliquotaICMS = 8;
                }
                
                /*if ((rst.getString("PRECOCUSTO") != null)
                        && (!rst.getString("PRECOCUSTO").trim().isEmpty())) {
                    custo = Double.parseDouble(rst.getString("PRECOCUSTO").trim());
                } else {*/
                    custo = 0;
                //}

                if ((rst.getString("PRECO_VENDA") != null)
                        && (!rst.getString("PRECO_VENDA").trim().isEmpty())) {
                    precoVenda = Double.parseDouble(rst.getString("PRECO_VENDA").trim());
                } else {
                    precoVenda = 0;
                }
                pesoLiq = 0;                
                pesoBruto = 0;                    

                if ((rst.getString("PER_LUCRO") != null)
                        && (!rst.getString("PER_LUCRO").trim().isEmpty())) {
                    margem = Double.parseDouble(rst.getString("PER_LUCRO").trim());
                } else {
                    margem = 0;
                }

                if (descricaoCompleta.length() > 60) {
                    descricaoCompleta = descricaoCompleta.substring(0, 60);
                }

                if (descricaoReduzida.length() > 22) {
                    descricaoReduzida = descricaoReduzida.substring(0, 22);
                }

                if (descricaoGondola.length() > 60) {
                    descricaoGondola = descricaoGondola.substring(0, 60);
                }

                ProdutoVO oProduto = new ProdutoVO();
                oProduto.id = idProduto;
                oProduto.descricaoCompleta = descricaoCompleta;
                oProduto.descricaoReduzida = descricaoReduzida;
                oProduto.descricaoGondola = descricaoGondola;
                oProduto.idTipoEmbalagem = idTipoEmbalagem;
                oProduto.qtdEmbalagem = qtdEmbalagem;
                oProduto.idTipoPisCofinsDebito = idTipoPisCofinsDebito;
                oProduto.idTipoPisCofinsCredito = idTipoPisCofinsCredito;
                oProduto.tipoNaturezaReceita = tipoNaturezaReceita;
                oProduto.pesavel = pesavel;
                oProduto.mercadologico1 = mercadologico1;
                oProduto.mercadologico2 = mercadologico2;
                oProduto.mercadologico3 = mercadologico3;
                oProduto.ncm1 = ncm1;
                oProduto.ncm2 = ncm2;
                oProduto.ncm3 = ncm3;
                oProduto.idFamiliaProduto = idFamilia;
                oProduto.idFornecedorFabricante = 1;
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
                oProduto.permitePerda = true;
                oProduto.utilizaTabelaSubstituicaoTributaria = false;
                oProduto.utilizaValidadeEntrada = false;
                oProduto.margem = margem;
                oProduto.validade = validade;
                oProduto.dataCadastro = dataCadastro;
                oProduto.pesoLiquido = pesoLiq;
                oProduto.pesoBruto = pesoBruto;

                ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                oComplemento.idSituacaoCadastro = idSituacaoCadastro;
                oComplemento.custoComImposto = custo;
                oComplemento.custoSemImposto = custo;
                oComplemento.precoDiaSeguinte = precoVenda;
                oComplemento.precoVenda = precoVenda;
                oProduto.vComplemento.add(oComplemento);

                ProdutoAliquotaVO oAliquota = new ProdutoAliquotaVO();
                oAliquota.idEstado = Global.idEstado;
                oAliquota.idAliquotaDebito = aliquotaICMS;
                oAliquota.idAliquotaCredito = aliquotaICMS;
                oAliquota.idAliquotaDebitoForaEstado = aliquotaICMS;
                oAliquota.idAliquotaCreditoForaEstado = aliquotaICMS;
                oAliquota.idAliquotaDebitoForaEstadoNF = aliquotaICMS;
                oProduto.vAliquota.add(oAliquota);

                ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
                oAutomacao.codigoBarras = codigoBarras;
                oAutomacao.qtdEmbalagem = qtdEmbalagem;
                oAutomacao.idTipoEmbalagem = idTipoEmbalagem;
                oProduto.vAutomacao.add(oAutomacao);

                CodigoAnteriorVO oAnterior = new CodigoAnteriorVO();
                oAnterior.codigoanterior = idProduto;

                if ((rst.getString("COD_BARRAS") != null)
                        && (!rst.getString("COD_BARRAS").trim().isEmpty())) {
                    
                    strBarrasAnterior = Utils.formataNumero(rst.getString("COD_BARRAS").trim());
                    
                    if (("8".equals(strBarrasAnterior.trim().substring(0, 1))) &&
                            (strBarrasAnterior.trim().length() > 14)) {
                        strBarrasAnterior = strBarrasAnterior.substring(1, 15);
                    } else if ((!"8".equals(strBarrasAnterior.trim().substring(0, 1))) &&
                            (strBarrasAnterior.trim().length() > 14)) {
                        strBarrasAnterior = strBarrasAnterior.substring(0, 14);
                    } else {
                        strBarrasAnterior = strBarrasAnterior;
                    }
                    
                    oAnterior.barras = Long.parseLong(strBarrasAnterior);
                } else {
                    oAnterior.barras = -1;
                }

                if ((rst.getString("aliquota") != null)
                        && (!rst.getString("aliquota").trim().isEmpty())) {
                    oAnterior.ref_icmsdebito = rst.getString("aliquota").trim();
                } else {
                    oAnterior.ref_icmsdebito = "";
                }
                
                oAnterior.custocomimposto = custo;
                oAnterior.custosemimposto = custo;

                if ((rst.getString("CODIGO") != null)
                        && (!rst.getString("CODIGO").trim().isEmpty())) {
                    oAnterior.ncm = strNcm.trim();
                } else {
                    oAnterior.ncm = "";
                }

                oAnterior.e_balanca = eBalanca;
                oAnterior.codigobalanca = codigoBalanca;

                oProduto.vCodigoAnterior.add(oAnterior);

                vProduto.add(oProduto);

            }

            Conexao.commit();
            return vProduto;
        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<ProdutoVO> carregarMercadologicoProduto() throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        StringBuilder sql = null;
        Statement stm = null, stmPG = null;
        ResultSet rst = null, rstPG = null;
        int idProduto = 0, 
            mercadologico1 = 0, 
            mercadologico2 = 0, 
            mercadologico3 = 0;
        
        stmPG = Conexao.createStatement();
        sql = new StringBuilder();
        sql.append("SELECT min(MERCADOLOGICO1) as MERCADOLOGICO1 FROM MERCADOLOGICO ");
        rstPG = stmPG.executeQuery(sql.toString());        
        if (rstPG.next()){
            Global.mercadologicoPadrao1 = rstPG.getInt("MERCADOLOGICO1");
            Global.mercadologicoPadrao2 = 1;
            Global.mercadologicoPadrao3 = 1; 
        }
        
        try {
            stm = ConexaoMySQL.getConexao().createStatement();
            sql = new StringBuilder();
            sql.append("select id_produto, ID_GRUPO, ID_SUBGRUPO ");
            sql.append("from mrs.produto ");
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                idProduto = Integer.parseInt(rst.getString("id_produto").trim());

                if ((rst.getString("ID_GRUPO") != null)
                        && (!rst.getString("ID_GRUPO").trim().isEmpty())
                        && (rst.getString("ID_SUBGRUPO") != null)
                        && (!rst.getString("ID_SUBGRUPO").trim().isEmpty())) {
                    mercadologico1 = Integer.parseInt(rst.getString("ID_GRUPO").trim());
                    mercadologico2 = Integer.parseInt(rst.getString("ID_SUBGRUPO").trim());
                    mercadologico3 = Integer.parseInt(rst.getString("ID_SUBGRUPO").trim());
                } else {
                    mercadologico1 = Global.mercadologicoPadrao1;
                    mercadologico2 = Global.mercadologicoPadrao1;
                    mercadologico3 = Global.mercadologicoPadrao1;
                }
            
                ProdutoVO oProduto = new ProdutoVO();
                oProduto.id = idProduto;
                oProduto.mercadologico1 = mercadologico1;
                oProduto.mercadologico2 = mercadologico2;
                oProduto.mercadologico3 = mercadologico3;
                vProduto.add(oProduto);
            }
            
            stm.close();
            stmPG.close();
            return vProduto;
        } catch(Exception ex) {
            throw ex;
        }
    }
    
    public Map<Double, ProdutoVO> carregarCustoProduto(int idLoja, int idLojaCliente) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        Map<Double, ProdutoVO> vProduto = new HashMap<>();
        double custo = 0, idProduto;

        try {

            stm = ConexaoMySQL.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("select * from tblsaldoestoque");             

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                idProduto = Double.parseDouble(rst.getString("id_produto").replace(".", ""));

                if ((rst.getString("custo_unitario") != null)
                        && (!rst.getString("custo_unitario").trim().isEmpty())) {
                    custo = Double.parseDouble(rst.getString("custo_unitario").replace(",", "."));
                } else {
                    custo = 0;
                }

                ProdutoVO oProduto = new ProdutoVO();
                oProduto.idDouble = idProduto;

                ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();

                oComplemento.idLoja = idLoja;
                oComplemento.custoComImposto = custo;
                oComplemento.custoSemImposto = custo;

                oProduto.vComplemento.add(oComplemento);

                CodigoAnteriorVO oCodigoAnterior = new CodigoAnteriorVO();

                oCodigoAnterior.custocomimposto = custo;
                oCodigoAnterior.custosemimposto = custo;
                oCodigoAnterior.id_loja = idLoja;

                oProduto.vCodigoAnterior.add(oCodigoAnterior);

                vProduto.put(idProduto, oProduto);
            }

            return vProduto;

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }

    private List<ProdutoVO> carregarMargemProduto() throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        int idProduto;
        double precoVenda = 0, margem = 0, custo = 0;

        try {
            stm = ConexaoMySQL.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("SELECT ID_PRODUTO, PER_LUCRO AS MARGEM FROM PRODUTO");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                idProduto = Integer.parseInt(rst.getString("ID_PRODUTO").trim());

                if ((rst.getString("MARGEM") != null)
                        && (!rst.getString("MARGEM").trim().isEmpty())) {
                    margem = Double.parseDouble(rst.getString("MARGEM").trim());
                } else {
                    if ((custo > 0) && (precoVenda > 0)) {
                        margem = (custo / precoVenda) * 100;
                    } else {
                        margem = 0;
                    }
                }

                ProdutoVO oProduto = new ProdutoVO();
                oProduto.id = idProduto;
                oProduto.margem = margem;

                vProduto.add(oProduto);

            }
            return vProduto;
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    private List<ProdutoVO> carregarPrecoProduto() throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        int idProduto;
        double precoVenda = 0;

        try {
            stm = ConexaoMySQL.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("SELECT ID_PRODUTO, PRECO_VENDA FROM PRODUTO");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                idProduto = Integer.parseInt(rst.getString("ID_PRODUTO").trim());

                if ((rst.getString("PRECO_VENDA") != null)
                        && (!rst.getString("PRECO_VENDA").trim().isEmpty())) {
                    precoVenda = Double.parseDouble(rst.getString("PRECO_VENDA").trim());
                } else {
                    precoVenda = 0;
                }

                ProdutoVO oProduto = new ProdutoVO();
                oProduto.id = idProduto;

                ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                oComplemento.precoVenda = precoVenda;
                oComplemento.precoDiaSeguinte = precoVenda;
                oProduto.vComplemento.add(oComplemento);

                vProduto.add(oProduto);

            }
            return vProduto;
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    private List<ProdutoVO> carregarNaturezaReceita() throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        int idProduto, idTipoPisCofinsDebito,tipoNaturezaReceita;
        double precoVenda = 0, margem = 0, custo = 0;
        Utils util = new Utils();
        try {
            stm = ConexaoMySQL.getConexao().createStatement();
            sql = new StringBuilder();
            sql.append("SELECT IDPRODUTO, NATUPC, PISCOFINS FROM PRODUTO WHERE NATUPC IS NOT NULL AND NATUPC <> '' ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                idProduto = Integer.parseInt(rst.getString("IDPRODUTO").trim());

                idTipoPisCofinsDebito   = retornarPisCofinsDebito(rst.getString("piscofins"));
                if ((rst.getString("natupc").trim()!=null) && 
                        (!rst.getString("natupc").trim().isEmpty())){
                    tipoNaturezaReceita     = util.retornarTipoNaturezaReceita(idTipoPisCofinsDebito, rst.getString("natupc").trim());                    
                }else{
                    tipoNaturezaReceita     = 999;                                        
                }
                
                ProdutoVO oProduto = new ProdutoVO();
                oProduto.id = idProduto;
                oProduto.tipoNaturezaReceita = tipoNaturezaReceita;
                vProduto.add(oProduto);
            }
            return vProduto;
        } catch (Exception ex) {
            throw ex;
        }
    }    
    
    private List<ProdutoVO> carregarPisCofins() throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        int idProduto, idTipoPisCofinsDebito, idTipoPisCofinsCredito, tipoNaturezaReceita;
        Utils util = new Utils();
        try {
            stm = ConexaoMySQL.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append(" select distinct p.id_produto, p.pis_cst ");
            sql.append(" from produto_pis p ");
            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                idProduto = Integer.parseInt(rst.getString("ID_PRODUTO").trim());

                idTipoPisCofinsDebito    = retornarPisCofinsDebito(rst.getInt("pis_cst"));
                idTipoPisCofinsCredito   = retornarPisCofinsCredito(rst.getInt("pis_cst"));
                tipoNaturezaReceita      = Utils.retornarTipoNaturezaReceita(idTipoPisCofinsDebito, "");                    
                
                ProdutoVO oProduto = new ProdutoVO();
                oProduto.id = idProduto;
                oProduto.idTipoPisCofinsDebito  = idTipoPisCofinsDebito;                      
                oProduto.idTipoPisCofinsCredito = idTipoPisCofinsCredito;
                oProduto.tipoNaturezaReceita    = tipoNaturezaReceita;
                
                CodigoAnteriorVO oAnterior = new CodigoAnteriorVO();
                oAnterior.piscofinsdebito = rst.getInt("pis_cst");
                oAnterior.piscofinscredito = rst.getInt("pis_cst");
                oProduto.vCodigoAnterior.add(oAnterior);
                
                vProduto.add(oProduto);
            }
            return vProduto;
        } catch (Exception ex) {
            throw ex;
        }
    }        

    private List<ProdutoVO> carregarEstoqueProduto(int idLoja) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        int idProduto;
        double estoque;

        try {
            stm = ConexaoMySQL.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("select * from tblsaldoestoque");  
            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                idProduto = Integer.parseInt(rst.getString("id_produto").trim());

                if ((rst.getString("saldo") != null)
                        && (!rst.getString("saldo").trim().isEmpty())) {
                    estoque = Double.parseDouble(rst.getString("saldo").trim());
                } else {
                    estoque = 0;
                }

                ProdutoVO oProduto = new ProdutoVO();
                oProduto.id = idProduto;

                ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                oComplemento.idLoja = idLoja;
                oComplemento.estoque = estoque;
                oProduto.vComplemento.add(oComplemento);

                vProduto.add(oProduto);
            }

            return vProduto;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public List<ClientePreferencialVO> carregarClientePreferencial(int idLoja, int idLojaCliente) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        List<ClientePreferencialVO> vClientePreferencial = new ArrayList<>();

        String nome, endereco, bairro, telefone1, inscricaoestadual, email, enderecoEmpresa, bairroEmpresa, nomeConjuge,
                dataResidencia, dataCadastro, numero, complemento, dataNascimento, nomePai, nomeMae,
                telefone2 = "", fax = "", observacao = "", empresa = "", telEmpresa = "", cargo = "",
                conjuge = "", orgaoExp = "";
        int id_municipio = 0, id_estado, id_sexo, id_tipoinscricao = 1, id, id_situacaocadastro, Linha = 0,
                estadoCivil = 0;
        long cnpj, cep;
        double limite, salario;
        boolean bloqueado;

        try {
            stm = ConexaoMySQL.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("select * from cliente_fisico");           
            rst = stm.executeQuery(sql.toString());
            Linha = 1;
            try {
                while (rst.next()) {
                    ClientePreferencialVO oClientePreferencial = new ClientePreferencialVO();

                    id = rst.getInt("CLI_CODIGO");
                    dataResidencia = "2000/01/01";
                    
                    if ((rst.getString("BLOQUEADO") != null)
                            && (!rst.getString("BLOQUEADO").trim().isEmpty())) {
                        if (rst.getInt("BLOQUEADO")==0){
                            id_situacaocadastro = 1;
                        }else{
                            id_situacaocadastro = 0;                            
                        }
                    } else {
                        id_situacaocadastro = 1;
                    }

                    if ((util.formataNumero(rst.getString("CPF")) != null)
                            && (!util.formataNumero(rst.getString("CPF")).isEmpty())) {
                        cnpj = Long.parseLong(util.formataNumero(rst.getString("CPF").trim()));
                    } else {
                        cnpj = Long.parseLong(rst.getString("CLI_CODIGO"));
                    }

                    if ((util.formataNumero(rst.getString("CPF")) != null)
                            && (!util.formataNumero(rst.getString("CPF")).trim().isEmpty())) {
                        if (util.formataNumero(rst.getString("CPF")).trim().length()<=11) {                        
                            id_tipoinscricao = 1;
                        } else {
                            id_tipoinscricao = 0;
                        } 
                    } else {
                        id_tipoinscricao = 1;
                    }

                    if ((rst.getString("NOME") != null)
                            && (!rst.getString("NOME").trim().isEmpty())) {
                        byte[] bytes = rst.getBytes("NOME");
                        String textoAcertado = new String(bytes, "ISO-8859-1");
                        nome = util.acertarTexto(textoAcertado.replace("'", "").trim().replace(".", "").trim());
                        if ("".equals(nome)){
                            nome = "SEM NOME VR " + id;                            
                        }
                    } else {
                        nome = "SEM NOME VR " + id;
                    }

                    if ((rst.getString("ENDERECO") != null)
                            && (!rst.getString("ENDERECO").trim().isEmpty())) {
                        endereco = util.acertarTexto(rst.getString("ENDERECO").replace("'", "").trim());
                    } else {
                        endereco = "";
                    }

                    if ((rst.getString("BAIRRO") != null)
                            && (!rst.getString("BAIRRO").trim().isEmpty())) {
                        bairro = util.acertarTexto(rst.getString("BAIRRO").trim().replace("'", ""));
                    } else {
                        bairro = "";
                    }

                    if ((rst.getString("TELEFONE_1") != null)
                            && (!rst.getString("TELEFONE_1").trim().isEmpty())) {
                        telefone1 = util.formataNumero(rst.getString("TELEFONE_1").trim());
                    } else {
                        telefone1 = "0";
                    }
                    if ((rst.getString("TELEFONE_2") != null)
                            && (!rst.getString("TELEFONE_2").trim().isEmpty())) {
                        telefone1 = util.formataNumero(rst.getString("TELEFONE_2").trim());
                    } else {
                        telefone1 = "0";
                    }
                    
                    if ((util.formataNumero(rst.getString("CEP")) != null)
                            && (!util.formataNumero(rst.getString("CEP")).trim().isEmpty())) {
                        cep = Long.parseLong(util.formataNumero(rst.getString("CEP").trim()));
                    } else {
                        cep = 0;
                    }

                    if ((rst.getString("CIDADE") != null)
                            && (!rst.getString("CIDADE").trim().isEmpty())) {
                        if ((rst.getString("UF") != null)
                                && (!rst.getString("UF").trim().isEmpty())) {
                            id_municipio = util.retornarMunicipioIBGEDescricao(util.acertarTexto(rst.getString("CIDADE").trim().replace("'", "").toUpperCase()),
                                    rst.getString("UF").trim().replace("'", ""));
                            if (id_municipio == 0) {
                                id_municipio = Global.idMunicipio;
                            }
                        } else {
                            id_municipio = Global.idMunicipio;
                        }
                    } else {
                        id_municipio = Global.idMunicipio;
                    }

                    if ((rst.getString("UF") != null)
                            && (!rst.getString("UF").trim().isEmpty())) {
                        id_estado = util.retornarEstadoDescricao(
                                rst.getString("UF").trim().replace("'", "").toUpperCase());

                        if (id_estado == 0) {
                            id_estado = Global.idEstado;
                        }
                    } else {
                        id_estado = Global.idEstado;
                    }
                    
                    if ((rst.getString("NUMERO") != null)
                            && (!rst.getString("NUMERO").trim().isEmpty())) {
                        numero = util.acertarTexto(rst.getString("NUMERO"));
                    } else {
                        numero = "0";
                    }

                    complemento = "";

                    if ((rst.getString("LIMITE_CREDITO") != null)
                            && (!rst.getString("LIMITE_CREDITO").trim().isEmpty())) {
                        limite = Double.parseDouble(rst.getString("LIMITE_CREDITO"));
                    } else {
                        limite = 0;
                    }

                    if ((rst.getString("RG") != null)
                            && (!rst.getString("RG").trim().isEmpty())) {
                        inscricaoestadual = util.acertarTexto(rst.getString("RG").trim());
                        inscricaoestadual = inscricaoestadual.replace("'", "").replace("-", "").replace(".", "");
                    } else {
                        inscricaoestadual = "ISENTO";
                    }

                    dataCadastro = "";

                    bloqueado = false;
                    nomePai = "";
                    nomeMae = "";
                    observacao = "";
                    email = "";
                    estadoCivil = 0;
                    id_sexo = 1;
                    cargo = "";
                    salario = 0;
                    orgaoExp = "";

                    if (nome.length() > 40) {
                        nome = nome.substring(0, 40);
                    }

                    if (conjuge.length() > 25) {
                        conjuge = conjuge.substring(0, 25);
                    }

                    if (endereco.length() > 40) {
                        endereco = endereco.substring(0, 40);
                    }

                    if (bairro.length() > 30) {
                        bairro = bairro.substring(0, 30);
                    }

                    if (String.valueOf(cep).length() > 8) {
                        cep = Long.parseLong(String.valueOf(cep).substring(0, 8));
                    }

                    if (telefone1.length() > 14) {
                        telefone1 = telefone1.substring(0, 14);
                    }

                    if (telefone2.length() > 14) {
                        telefone2 = telefone2.substring(0, 14);
                    }

                    if (String.valueOf(cnpj).length() > 14) {
                        cnpj = Long.parseLong(String.valueOf(cnpj).substring(0, 14));
                    }

                    if (inscricaoestadual.length() > 18) {
                        inscricaoestadual = inscricaoestadual.substring(0, 18);
                    }

                    if (complemento.length() > 30) {
                        complemento = complemento.substring(0, 30);
                    }

                    if (email.length() > 50) {
                        email = email.substring(0, 50);
                    }

                    if (observacao.length() > 80) {
                        observacao = observacao.substring(0, 80);
                    }

                    if (cargo.length() > 25) {
                        cargo = cargo.substring(0, 25);
                    }

                    if (empresa.length() > 35) {
                        empresa = empresa.substring(0, 35);
                    }

                    //if (bairroEmpresa.length() > 30) {
                        //bairroEmpresa = bairroEmpresa.substring(0, 30);
                        bairroEmpresa = "";
                    //}

                    //if (enderecoEmpresa.length() > 30) {
                        //enderecoEmpresa = enderecoEmpresa.substring(0, 30);
                        enderecoEmpresa = "";
                    //}

                    oClientePreferencial.id = id;
                    oClientePreferencial.nome = nome;
                    oClientePreferencial.endereco = endereco;
                    oClientePreferencial.bairro = bairro;
                    oClientePreferencial.id_estado = id_estado;
                    oClientePreferencial.id_municipio = id_municipio;
                    oClientePreferencial.cep = cep;
                    oClientePreferencial.telefone = telefone1;
                    oClientePreferencial.inscricaoestadual = inscricaoestadual;
                    oClientePreferencial.cnpj = cnpj;
                    oClientePreferencial.sexo = id_sexo;
                    oClientePreferencial.dataresidencia = dataResidencia;
                    oClientePreferencial.datacadastro = dataCadastro;
                    oClientePreferencial.email = email;
                    oClientePreferencial.valorlimite = limite;
                    oClientePreferencial.codigoanterior = id;
                    oClientePreferencial.fax = fax;
                    oClientePreferencial.bloqueado = bloqueado;
                    oClientePreferencial.id_situacaocadastro = id_situacaocadastro;
                    oClientePreferencial.celular = telefone2;
                    oClientePreferencial.observacao = observacao;
                    oClientePreferencial.nomepai = nomePai;
                    oClientePreferencial.nomemae = nomeMae;
                    oClientePreferencial.empresa = empresa;
                    oClientePreferencial.telefoneempresa = telEmpresa;
                    oClientePreferencial.numero = numero;
                    oClientePreferencial.cargo = cargo;
                    oClientePreferencial.enderecoempresa = enderecoEmpresa;
                    oClientePreferencial.id_tipoinscricao = id_tipoinscricao;
                    oClientePreferencial.salario = salario;
                    oClientePreferencial.id_tipoestadocivil = estadoCivil;
                    oClientePreferencial.nomeconjuge = conjuge;
                    oClientePreferencial.orgaoemissor = orgaoExp;
                    oClientePreferencial.enderecoempresa = enderecoEmpresa;
                    oClientePreferencial.bairroempresa = enderecoEmpresa;
                    oClientePreferencial.complemento = complemento;

                    vClientePreferencial.add(oClientePreferencial);
                }
                stm.close();
            } catch (Exception ex) {
                throw ex;
            }
            return vClientePreferencial;
        } catch (SQLException | NumberFormatException ex) {
            throw ex;
        }
    }

    public List<FornecedorVO> carregarFornecedor() throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        List<FornecedorVO> vFornecedor = new ArrayList<>();

        String razaosocial, nomefantasia, obs, inscricaoestadual, endereco, bairro, datacadastro,
                numero = "", complemento = "", telefone = "", telefone2 = "", telefone3 = "", email = "", fax = "";
        int id, id_municipio = 0, id_estado, id_tipoinscricao, Linha = 0;
        long cnpj, cep;
        boolean ativo = true;

        try {
            stm = ConexaoMySQL.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("SELECT * FROM FORNECEDOR ");
            rst = stm.executeQuery(sql.toString());

            Linha = 0;

            try {
                while (rst.next()) {
                    FornecedorVO oFornecedor = new FornecedorVO();

                    id = rst.getInt("FOR_CODIGO");

                    Linha = id;                        
                    if (id==817){
                        Linha = id;                        
                    }

                    if ((rst.getString("RAZAO_SOCIAL") != null)
                            && (!rst.getString("RAZAO_SOCIAL").isEmpty())) {
                        byte[] bytes = rst.getBytes("RAZAO_SOCIAL");
                        String textoAcertado = new String(bytes, "ISO-8859-1");
                        razaosocial = Utils.acertarTexto(textoAcertado.replace("'", "").trim());
                    } else {
                        razaosocial = "";
                    }

                    if ((rst.getString("NOME_FANTASIA") != null)
                            && (!rst.getString("NOME_FANTASIA").isEmpty())) {
                        byte[] bytes = rst.getBytes("NOME_FANTASIA");
                        String textoAcertado = new String(bytes, "ISO-8859-1");
                        nomefantasia = Utils.acertarTexto(textoAcertado.replace("'", "").trim());
                    } else {
                        nomefantasia = "";
                    }

                    if ((Utils.formataNumero(rst.getString("CNPJ_CPF")) != null)
                            && (!Utils.formataNumero(rst.getString("CNPJ_CPF")).isEmpty())) {
                        if (Utils.formataNumero(rst.getString("CNPJ_CPF").toString()).length()>11){
                            id_tipoinscricao = 0;                        
                        }else{
                            id_tipoinscricao = 1;                                                
                        }
                        cnpj = Long.parseLong(Utils.formataNumero(rst.getString("CNPJ_CPF").trim()));
                    } else {
                        cnpj = -1;
                        id_tipoinscricao = 0;                                                                        
                    }

                    if ((rst.getString("IE_RG") != null)
                            && (!rst.getString("IE_RG").isEmpty())) {
                        inscricaoestadual = Utils.acertarTexto(rst.getString("IE_RG").replace("'", "").trim());
                    } else {
                        inscricaoestadual = "ISENTO";
                    }

                    if ((rst.getString("ENDERECO") != null)
                            && (!rst.getString("ENDERECO").isEmpty())) {
                        endereco = Utils.acertarTexto(rst.getString("ENDERECO").replace("'", "").trim());
                    } else {
                        endereco = "";
                    }

                    if ((rst.getString("BAIRRO") != null)
                            && (!rst.getString("BAIRRO").isEmpty())) {
                        bairro = Utils.acertarTexto(rst.getString("BAIRRO").replace("'", "").trim());
                    } else {
                        bairro = "";
                    }

                    if (rst.getString("CEP")!=null){
                        String CEPAUX = Utils.formataNumero(rst.getString("CEP").trim().replace("/", "").replace("-", "").replace(".", ""));
                        if ((CEPAUX != null)
                                && (!CEPAUX.isEmpty())) {
                            cep = Long.parseLong(CEPAUX);

                        } else {
                            cep = Long.parseLong("0");
                        }
                    }else{
                        cep = Long.parseLong("0");                        
                    }

                    if ((rst.getString("CIDADE") != null)
                            && (!rst.getString("CIDADE").isEmpty())) {
                        if ((rst.getString("UF") != null)
                                && (!rst.getString("UF").isEmpty())) {
                            id_municipio = util.retornarMunicipioIBGEDescricao(Utils.acertarTexto(rst.getString("CIDADE").replace("'", "").trim()),
                                    Utils.acertarTexto(rst.getString("UF").replace("'", "").trim()));
                            if (id_municipio == 0) {
                                id_municipio = Global.idMunicipio;
                            }
                        }
                    } else {
                        id_municipio = Global.idMunicipio;
                    }

                    if ((rst.getString("UF") != null)
                            && (!rst.getString("UF").isEmpty())) {
                        id_estado = Utils.retornarEstadoDescricao(Utils.acertarTexto(rst.getString("UF").replace("'", "").trim()));

                        if (id_estado == 0) {
                            id_estado = Global.idEstado;
                        }
                    } else {
                        id_estado = Global.idEstado;
                    }

                    if (rst.getString("DATA_CADASTRO") != null) {
                        datacadastro = rst.getString("DATA_CADASTRO").trim();
                    } else {
                        datacadastro = "";
                    }
                    ativo = true;

                    /*if ((rst.getString("CNPJ_CPF") != null)
                            && (!rst.getString("CNPJ_CPF").trim().isEmpty())) {
                        if (rst.getString("CNPJ_CPF").length() <= 11){
                            id_tipoinscricao = 1;
                        } else {
                            id_tipoinscricao = 0;
                        }
                    } else {
                        id_tipoinscricao = 0;
                    }*/

                    if ((rst.getString("NUMERO") != null)
                            && (!rst.getString("NUMERO").trim().isEmpty())) {
                        numero = util.acertarTexto(rst.getString("NUMERO").trim().replace("'", ""));
                        if (numero.length()>6){
                            numero = numero.substring(0,6);
                        }
                    } else {
                        numero = "0";
                    }

                    complemento = "";

                    if ((rst.getString("TELEFONE") != null)
                            && (!rst.getString("TELEFONE").trim().isEmpty())) {
                        telefone = util.formataNumero(rst.getString("TELEFONE").trim());
                    } else {
                        telefone = "0";
                    }

                    if ((rst.getString("FAX") != null)
                            && (!rst.getString("FAX").trim().isEmpty())) {
                        telefone2 = util.formataNumero(rst.getString("FAX").trim());
                    } else {
                        telefone2 = "0";
                    }

                    if ((rst.getString("EMAIL") != null)
                            && (!rst.getString("EMAIL").trim().isEmpty())
                            && (rst.getString("EMAIL").contains("@"))) {
                        email = util.acertarTexto(rst.getString("EMAIL").replace("'", ""));
                    } else {
                        email = "";
                    }

                    if (razaosocial.length() > 40) {
                        razaosocial = razaosocial.substring(0, 40);
                    }

                    if (nomefantasia.length() > 30) {
                        nomefantasia = nomefantasia.substring(0, 30);
                    }

                    if (endereco.length() > 40) {
                        endereco = endereco.substring(0, 40);
                    }

                    if (complemento.length() > 30) {
                        complemento = complemento.substring(0, 30);
                    }

                    if (bairro.length() > 30) {
                        bairro = bairro.substring(0, 30);
                    }

                    if (String.valueOf(cep).length() > 8) {
                        cep = Long.parseLong(String.valueOf(cep).substring(0, 8));
                    }

                    if (String.valueOf(cnpj).length() > 14) {
                        cnpj = Long.parseLong(String.valueOf(cnpj).substring(0, 14));
                    }

                    if (inscricaoestadual.length() > 20) {
                        inscricaoestadual = inscricaoestadual.substring(0, 20);
                    }

                    if (telefone.length() > 14) {
                        telefone = telefone.substring(0, 14);
                    }

                    if (email.length() > 50) {
                        email = email.substring(0, 50);
                    }

                    obs = "";
                    oFornecedor.codigoanterior = id;
                    oFornecedor.razaosocial = razaosocial;
                    oFornecedor.nomefantasia = nomefantasia;
                    oFornecedor.endereco = endereco;
                    oFornecedor.bairro = bairro;
                    oFornecedor.numero = numero;
                    oFornecedor.id_municipio = id_municipio;
                    oFornecedor.cep = cep;
                    oFornecedor.id_estado = id_estado;
                    oFornecedor.id_tipoinscricao = id_tipoinscricao;
                    oFornecedor.inscricaoestadual = inscricaoestadual;
                    oFornecedor.cnpj = cnpj;
                    oFornecedor.id_situacaocadastro = (ativo == true ? 1 : 0);
                    oFornecedor.observacao = obs;
                    oFornecedor.complemento = complemento;
                    oFornecedor.telefone = telefone;
                    oFornecedor.telefone2 = telefone2;
                    oFornecedor.telefone3 = telefone3;
                    oFornecedor.email = email;
                    oFornecedor.fax = fax;

                    vFornecedor.add(oFornecedor);
                }
            } catch (Exception ex) {
                if (Linha > 0) {
                    throw new VRException("Linha " + Linha + ": " + ex.getMessage());
                } else {
                    throw ex;
                }
            }
            return vFornecedor;
        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }

    public List<ProdutoFornecedorVO> carregarProdutoFornecedor() throws Exception {

        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        List<ProdutoFornecedorVO> vProdutoFornecedor = new ArrayList<>();
        int idFornecedor, idProduto, qtdEmbalagem;
        String codigoExterno;
        java.sql.Date dataAlteracao = new Date(new java.util.Date().getTime());

        try {

            stm = ConexaoMySQL.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("select id_produto, codigo, ");
            sql.append("id_fornecedor, quantidade ");
            sql.append("from mrs.produto_codigo_fornecedor ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                idFornecedor = rst.getInt("id_fornecedor");
                idProduto = rst.getInt("id_produto");
                qtdEmbalagem = rst.getInt("quantidade");
                
                if ((rst.getString("codigo") != null) &&
                        (!rst.getString("codigo").trim().isEmpty())) {
                    codigoExterno = Utils.acertarTexto(rst.getString("codigo").trim().replace("'", ""));
                } else {
                    codigoExterno = "";
                }
                

                ProdutoFornecedorVO oProdutoFornecedor = new ProdutoFornecedorVO();

                oProdutoFornecedor.id_fornecedor = idFornecedor;
                oProdutoFornecedor.id_produto = idProduto;
                oProdutoFornecedor.qtdembalagem = qtdEmbalagem;
                oProdutoFornecedor.dataalteracao = dataAlteracao;
                oProdutoFornecedor.codigoexterno = codigoExterno;

                vProdutoFornecedor.add(oProdutoFornecedor);
            }

            return vProdutoFornecedor;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public List<ReceberChequeVO> carregarReceberCheque(int id_loja, int id_lojaCliente) throws Exception {

        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null, rst2 = null;
        Utils util = new Utils();
        List<ReceberChequeVO> vReceberCheque = new ArrayList<>();

        int numerocupom, idBanco, cheque, idTipoInscricao, id_tipoalinea;
        double valor, juros;
        long cpfCnpj;
        String observacao = "", dataemissao = "", datavencimento = "",
                agencia, conta, nome, rg, telefone;

        try {

            stm = ConexaoMySQL.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("SELECT BANCO, AGENCIA, CONTA, NUMERO, ");
            sql.append("       VALOR, CPFCNPJ, DATAEMI, DATAVENC ");
            sql.append("FROM CHEQUE ");
            sql.append("WHERE DATASAI IS NULL ");
            sql.append("ORDER BY DATAVENC ");             

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                ReceberChequeVO oReceberCheque = new ReceberChequeVO();

                cpfCnpj = Long.parseLong(util.formataNumero(rst.getString("cpfcnpj").trim()));

                if (String.valueOf(cpfCnpj).length() > 11) {
                    idTipoInscricao = 0;
                } else {
                    idTipoInscricao = 1;
                }

                if ((rst.getString("AGENCIA") != null)
                        && (!rst.getString("AGENCIA").trim().isEmpty())) {
                    idBanco = util.retornarBanco(Integer.parseInt(util.formataNumero(util.acertarTexto(rst.getString("BANCO").trim()))));
                }else{
                    idBanco = 999;
                }

                if ((rst.getString("AGENCIA") != null)
                        && (!rst.getString("AGENCIA").trim().isEmpty())) {
                    agencia = util.acertarTexto(rst.getString("AGENCIA").trim().replace("'", ""));
                } else {
                    agencia = "";
                }

                conta = "";

                if ((rst.getString("NUMERO") != null)
                        && (!rst.getString("NUMERO").trim().isEmpty())) {

                    cheque = Integer.parseInt(util.formataNumero(rst.getString("NUMERO")));

                    if (String.valueOf(cheque).length() > 10) {
                        cheque = Integer.parseInt(String.valueOf(cheque).substring(0, 10));
                    }
                } else {
                    cheque = 0;
                }

                if ((rst.getString("DATAEMI") != null)
                        && (!rst.getString("DATAEMI").trim().isEmpty())) {
                    dataemissao = rst.getString("DATAEMI").trim();
                } else {
                    dataemissao = "2016/01/01";
                }

                if ((rst.getString("DATAVENC") != null)
                        && (!rst.getString("DATAVENC").trim().isEmpty())) {

                    datavencimento = rst.getString("DATAVENC").trim();
                } else {
                    datavencimento = "2016/12/01";
                }

                nome = "";
                rg = "";

                valor = Double.parseDouble(rst.getString("VALOR"));
                numerocupom = 0;
                juros = 0;

                observacao = "IMPORTADO VR";
                telefone = "";

                id_tipoalinea = 0;

                oReceberCheque.id_loja = id_loja;
                oReceberCheque.id_tipoalinea = id_tipoalinea;
                oReceberCheque.data = dataemissao;
                oReceberCheque.datadeposito = datavencimento;
                oReceberCheque.cpf = cpfCnpj;
                oReceberCheque.numerocheque = cheque;
                oReceberCheque.id_banco = idBanco;
                oReceberCheque.agencia = agencia;
                oReceberCheque.conta = conta;
                oReceberCheque.numerocupom = numerocupom;
                oReceberCheque.valor = valor;
                oReceberCheque.observacao = observacao;
                oReceberCheque.rg = rg;
                oReceberCheque.telefone = telefone;
                oReceberCheque.nome = nome;
                oReceberCheque.id_tipoinscricao = idTipoInscricao;
                oReceberCheque.datadeposito = datavencimento;
                oReceberCheque.valorjuros = juros;
                oReceberCheque.valorinicial = valor;

                vReceberCheque.add(oReceberCheque);

            }

            return vReceberCheque;

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }

    public List<ReceberCreditoRotativoVO> carregarReceberCreditoRotativo(int id_loja, int id_lojaCliente) throws Exception {

        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        List<ReceberCreditoRotativoVO> vReceberCreditoRotativo = new ArrayList<>();
        int id_cliente, numerocupom, ecf=0;
        double valor;
        String observacao, dataemissao, datavencimento;

        try {

            stm = ConexaoMySQL.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("SELECT ID_CLIENTE, DATA_EMISSAO, DATA_VENCIMENTO, SALDO ");
            sql.append("FROM FATURA ");
            sql.append("WHERE SALDO > 0 ");            

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {
                ecf++;
                ReceberCreditoRotativoVO oReceberCreditoRotativo = new ReceberCreditoRotativoVO();

                id_cliente = rst.getInt("ID_CLIENTE");
                dataemissao = rst.getString("DATA_EMISSAO").substring(0, 10).trim();
                datavencimento = rst.getString("DATA_VENCIMENTO").substring(0, 10).trim();
                numerocupom = ecf;
                valor = Double.parseDouble(rst.getString("SALDO"));
                observacao = "IMPORTADO VR";
                
                oReceberCreditoRotativo.id_clientepreferencial = id_cliente;
                oReceberCreditoRotativo.id_loja = id_loja;
                oReceberCreditoRotativo.dataemissao = dataemissao;
                oReceberCreditoRotativo.numerocupom = numerocupom;
                oReceberCreditoRotativo.valor = valor;
                oReceberCreditoRotativo.ecf = ecf;
                oReceberCreditoRotativo.observacao = observacao;
                oReceberCreditoRotativo.id_clientepreferencial = id_cliente;
                oReceberCreditoRotativo.datavencimento = datavencimento;
                vReceberCreditoRotativo.add(oReceberCreditoRotativo);
            }

            return vReceberCreditoRotativo;

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }

    public Map<Long, ProdutoVO> carregarCodigoBarrasEmBranco() throws SQLException, Exception {
        StringBuilder sql = null;
        Statement stmPostgres = null;
        ResultSet rst;
        Map<Long, ProdutoVO> vProduto = new HashMap<>();
        int qtdeEmbalagem;
        double idProduto=0;
        long codigobarras=-1;

        try {

            stmPostgres = Conexao.createStatement();

            sql = new StringBuilder();
            sql.append("select id, id_tipoembalagem ");
            sql.append(" from produto p ");
            sql.append(" where not exists(select pa.id from produtoautomacao pa where pa.id_produto = p.id) ");

            rst = stmPostgres.executeQuery(sql.toString());

            while (rst.next()) {

                idProduto    = Double.parseDouble(rst.getString("id"));
                
                if ((rst.getInt("id_tipoembalagem") == 4) || (idProduto<=9999)) {
                    codigobarras = Utils.gerarEan13((int) idProduto, false);
                } else {
                    codigobarras = Utils.gerarEan13((int) idProduto, true);
                }
                
                qtdeEmbalagem = 1;

                ProdutoVO oProduto = new ProdutoVO();
                oProduto.id = (int) idProduto;
                ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
                oAutomacao.idTipoEmbalagem = rst.getInt("id_tipoembalagem");
                oAutomacao.codigoBarras = codigobarras;
                oAutomacao.qtdEmbalagem = qtdeEmbalagem;
                oProduto.vAutomacao.add(oAutomacao);
                vProduto.put(codigobarras, oProduto);
            }

            return vProduto;

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }
    
    public List<ProdutoVO> carregarCodigoBarras() throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        long codigoBarras;
        int idProduto;
        String strCodigoBarras = "";
        
        try {
            stm = ConexaoMySQL.getConexao().createStatement();
            
            sql = new StringBuilder();
            sql.append("select id_produto, cod_barras ");
            sql.append("from mrs.produto ");
            sql.append("where char_length(cod_barras) > 14 ");
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                if ((rst.getString("cod_barras") != null) &&
                        (!rst.getString("cod_barras").trim().isEmpty())) {
                    strCodigoBarras = Utils.formataNumero(rst.getString("cod_barras").trim());
                    
                    
                    if (strCodigoBarras.length() > 19) {
                        idProduto = Integer.parseInt(rst.getString("id_produto").trim());                            
                        strCodigoBarras = strCodigoBarras.substring(strCodigoBarras.length() -7);
                        codigoBarras = Long.parseLong(strCodigoBarras);
                        
                        
                        ProdutoVO oProduto = new ProdutoVO();
                        oProduto.id = idProduto;
                        
                        ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
                        oAutomacao.codigoBarras = codigoBarras;
                        oProduto.vAutomacao.add(oAutomacao);

                        vProduto.add(oProduto);
                    }
                }
            }
            
            stm.close();
            return vProduto;
        } catch(Exception ex) {
            throw ex;
        }
    }

    public List<ProdutoVO> carregarCodigoBarras2() throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        long codigoBarras;
        int idProduto;
        String strCodigoBarras = "";
        
        try {
            stm = ConexaoMySQL.getConexao().createStatement();
            
            sql = new StringBuilder();
            sql.append("select id_produto, cod_barras ");
            sql.append("from mrs.produto ");
            sql.append("where char_length(cod_barras) > 14 ");
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                if ((rst.getString("cod_barras") != null) &&
                        (!rst.getString("cod_barras").trim().isEmpty())) {
                    strCodigoBarras = Utils.formataNumero(rst.getString("cod_barras").trim());
                    
                    
                    if (strCodigoBarras.length() >=14) {
                        
                        idProduto = Integer.parseInt(rst.getString("id_produto").trim());                            

                        if (("8".equals(strCodigoBarras.trim().substring(0, 1)))
                                && (strCodigoBarras.length() > 14)) {
                            strCodigoBarras = strCodigoBarras.substring(1, 15);
                        } else if (!"8".equals(strCodigoBarras.trim().substring(0, 1))
                                && (strCodigoBarras.length() == 20)) {
                            strCodigoBarras = strCodigoBarras.substring(0, 13);
                        } else if ((!"8".equals(strCodigoBarras.trim().substring(0, 1)))
                                && (strCodigoBarras.length() >= 21)) {
                            strCodigoBarras = strCodigoBarras.substring(0, 14);
                        } else {
                            codigoBarras = Long.parseLong(strCodigoBarras);
                        }
                        
                        codigoBarras = Long.parseLong(strCodigoBarras);
                        
                        ProdutoVO oProduto = new ProdutoVO();
                        oProduto.id = idProduto;
                        
                        ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
                        oAutomacao.codigoBarras = codigoBarras;
                        oProduto.vAutomacao.add(oAutomacao);

                        vProduto.add(oProduto);
                    }
                }
            }
            
            stm.close();
            return vProduto;
        } catch(Exception ex) {
            throw ex;
        }
    }
    

    // IMPORTAÇÕES
    public void importarProdutoBalanca(String arquivo, int opcao) throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Produtos de Balanca...");
            List<ProdutoBalancaVO> vProdutoBalanca = new ProdutoBalancaDAO().carregar(arquivo, opcao);

            new ProdutoBalancaDAO().salvar(vProdutoBalanca);
        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarFamiliaProduto() throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Familia Produto...");
            List<FamiliaProdutoVO> vFamiliaProduto = carregarFamiliaProduto();

            FamiliaProdutoDAO familiaProduto = new FamiliaProdutoDAO();
            familiaProduto.verificarDescricao = false;
            familiaProduto.salvar(vFamiliaProduto);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarMercadologico() throws Exception {

        List<MercadologicoVO> vMercadologico = new ArrayList<>();

        try {

            ProgressBar.setStatus("Carregando dados...Mercadologico 1...");
            vMercadologico = carregarMercadologico(1);
            new MercadologicoDAO().salvar(vMercadologico, false);

            ProgressBar.setStatus("Carregando dados...Mercadologico 2...");
            vMercadologico = carregarMercadologico(2);
            new MercadologicoDAO().salvar(vMercadologico, false);

            ProgressBar.setStatus("Carregando dados...Mercadologico 3...");
            vMercadologico = carregarMercadologico(3);
            new MercadologicoDAO().salvar(vMercadologico, false);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarProduto(int idLojaDestino) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Produtos...");

            vProdutoNovo = carregarProduto();

            List<LojaVO> vLoja = new LojaDAO().carregar();

            ProdutoDAO produtoDAO = new ProdutoDAO();
            produtoDAO.implantacaoExterna = true;
            produtoDAO.salvar(vProdutoNovo, idLojaDestino, vLoja);

        } catch (Exception ex) {
            throw ex;
        }
    }

    public void importarCustoProduto(int id_loja, int id_lojaCliente) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        try {

            ProgressBar.setStatus("Carregando dados...Produtos...Custo...");
            Map<Double, ProdutoVO> vCustoProduto = carregarCustoProduto(id_loja, id_lojaCliente);

            List<LojaVO> vLoja = new LojaDAO().carregar();

            ProgressBar.setMaximum(vCustoProduto.size());

            for (Double keyId : vCustoProduto.keySet()) {

                ProdutoVO oProduto = vCustoProduto.get(keyId);

                vProdutoNovo.add(oProduto);

                ProgressBar.next();
            }

            produto.alterarCustoProduto(vProdutoNovo, id_loja);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarMargemProduto(int idLojaDestino) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();

        try {
            ProgressBar.setStatus("Carregando dados...Maegem Produtos...");

            vProdutoNovo = carregarMargemProduto();

            new ProdutoDAO().alterarMargemProduto(vProdutoNovo, idLojaDestino);
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    public void importarPrecoProduto(int idLojaDestino) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();

        try {
            ProgressBar.setStatus("Carregando dados...Preço Produtos...");

            vProdutoNovo = carregarPrecoProduto();

            new ProdutoDAO().alterarPrecoProduto(vProdutoNovo, idLojaDestino);
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    public void importarNaturezaReceita(int idLojaDestino) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();

        try {
            ProgressBar.setStatus("Carregando dados...Natureza Receita...");

            vProdutoNovo = carregarNaturezaReceita();

            new ProdutoDAO().alterarNaturezaReceita(vProdutoNovo);
        } catch (Exception ex) {
            throw ex;
        }
    }    
    
    public void importarPisCofins(int idLojaDestino) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();

        try {
            ProgressBar.setStatus("Carregando dados...Natureza Receita...");

            vProdutoNovo = carregarPisCofins();

            new ProdutoDAO().alterarPisCofinsProduto(vProdutoNovo);
        } catch (Exception ex) {
            throw ex;
        }
    }        

    public void importarEstoqueProduto(int idLoja) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Estoque...Produtos...");

            vProduto = carregarEstoqueProduto(idLoja);

            new ProdutoDAO().alterarEstoqueProduto(vProduto, idLoja);

        } catch (Exception ex) {
            throw ex;
        }
    }

    public void importarClientePreferencial(int idLoja, int idLojaCliente, boolean deletar) throws Exception {
        try {

            ProgressBar.setStatus("Carregando dados...Clientes...");
            List<ClientePreferencialVO> vClientePreferencial = carregarClientePreferencial(idLoja, idLojaCliente);
            new PlanoDAO().salvar(idLoja);
            new ClientePreferencialDAO().salvar(vClientePreferencial, idLoja, idLojaCliente,deletar);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarFornecedor() throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Fornecedor...");
            List<FornecedorVO> vFornecedor = carregarFornecedor();

            new FornecedorDAO().salvar(vFornecedor);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarProdutoFornecedor() throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Produto Fornecedor...");
            List<ProdutoFornecedorVO> vProdutoFornecedor = carregarProdutoFornecedor();

            new ProdutoFornecedorDAO().salvar(vProdutoFornecedor);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarChequeReceber(int id_loja, int id_lojaCliente) throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Cheque Receber...");
            List<ReceberChequeVO> vReceberCheque = carregarReceberCheque(id_loja, id_lojaCliente);

            new ReceberChequeDAO().salvar(vReceberCheque, id_loja);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarReceberCreditoRotativo(int idLoja, int idLojaCliente) throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Receber Cliente...");
            List<ReceberCreditoRotativoVO> vReceberCliente = carregarReceberCreditoRotativo(idLoja, idLojaCliente);

            new ReceberCreditoRotativoDAO().salvar(vReceberCliente, idLoja);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarCodigoBarraEmBranco() throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        try {

            ProgressBar.setStatus("Carregando dados...Produtos...Codigo Barras...");
            Map<Long, ProdutoVO> vCodigoBarra = carregarCodigoBarrasEmBranco();

            ProgressBar.setMaximum(vCodigoBarra.size());

            for (Long keyId : vCodigoBarra.keySet()) {

                ProdutoVO oProduto = vCodigoBarra.get(keyId);

                vProdutoNovo.add(oProduto);

                ProgressBar.next();
            }

            produto.addCodigoBarrasEmBranco(vProdutoNovo);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarCodigoBarra() throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        try {

            ProgressBar.setStatus("Carregando dados...Produtos...Codigo Barras...");
            vProdutoNovo = carregarCodigoBarras();

            produto.addCodigoBarras(vProdutoNovo);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarCodigoBarra2() throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        try {

            ProgressBar.setStatus("Carregando dados...Produtos...Codigo Barras...");
            vProdutoNovo = carregarCodigoBarras2();

            produto.addCodigoBarras(vProdutoNovo);

        } catch (Exception ex) {

            throw ex;
        }
    }
    
    public void importarMercadologicoProduto() throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Produto Mercadológico...");
            
            vProduto = carregarMercadologicoProduto();
            if (!vProduto.isEmpty()) {
                new ProdutoDAO().alterarMercadologicoProduto(vProduto);
            }
        } catch(Exception ex) {
            throw ex;
        }
    }
    
    // FUNÇÕES
    public int retornarPisCofinsDebito(String csttipopiscofins) {
        int retorno;

        if ("1".equals(csttipopiscofins)) {
            retorno = 0;
        } else if (("2".equals(csttipopiscofins)) ||
                   ("3".equals(csttipopiscofins))){
            retorno = 1;
        } else if ("4".equals(csttipopiscofins)) {
            retorno = 3;
        } else if ("5".equals(csttipopiscofins)) {
            retorno = 7;
        } else if (("6".equals(csttipopiscofins)) ||
                   ("7".equals(csttipopiscofins)) ||                
                   ("8".equals(csttipopiscofins)) ||                                
                   ("10".equals(csttipopiscofins)) ){
            retorno = 8;
        } else if ("9".equals(csttipopiscofins)) {
            retorno = 2;
        } else {
            retorno = 8;
        }

        return retorno;
    }
    
    public int retornarPisCofinsDebito(int csttipopiscofins) {
        int retorno;

        if (csttipopiscofins == 1) {
            retorno = 0;
        } else if (csttipopiscofins == 2) {
            retorno = 5;
        } else if (csttipopiscofins == 3) {
            retorno = 6;
        } else if (csttipopiscofins == 4) {
            retorno = 3;
        } else if (csttipopiscofins == 5) {
            retorno = 2;
        } else if (csttipopiscofins == 6) {
            retorno = 7;
        } else if (csttipopiscofins == 7) {
            retorno = 1;
        } else if (csttipopiscofins == 8) {
            retorno = 8;
        } else if ((csttipopiscofins == 49) ||
                (csttipopiscofins == 99)) {
            retorno = 9;
        } else {
            retorno = 1;
        }

        return retorno;
    }
    
    public int retornarPisCofinsCredito(int csttipopiscofins) {
        int retorno;

        if (csttipopiscofins == 1) {
            retorno = 12;
        } else if (csttipopiscofins == 2) {
            retorno = 17;
        } else if (csttipopiscofins == 3) {
            retorno = 18;
        } else if (csttipopiscofins == 4) {
            retorno = 15;
        } else if (csttipopiscofins == 5) {
            retorno = 14;
        } else if (csttipopiscofins == 6) {
            retorno = 19;
        } else if (csttipopiscofins == 7) {
            retorno = 13;
        } else if (csttipopiscofins == 8) {
            retorno = 20;
        } else if ((csttipopiscofins == 49) ||
                (csttipopiscofins == 99)) {
            retorno = 21;
        } else {
            retorno = 13;
        }

        return retorno;
    }
    
    private int retornarAliquotaICMS(String codTrib) {
        int retorno = 8;
        switch (codTrib) {
            case "FF":
                retorno = 7;
                break;
            case "II":
                retorno = 6;
                break;
            case "18,00":
                retorno = 2;
                break;
            case "12,00":
                retorno = 1;
                break;
            case "25,00":
                retorno = 3;
                break;
            case "7,00":
                retorno = 0;
                break;
            case "NN":
                retorno = 17;
                break;
            default:
                retorno = 8;
                break;
        }
        return retorno;
    }    
}