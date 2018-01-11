package vrimplantacao.dao.interfaces;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao.dao.cadastro.ClientePreferencialContatoDAO;
import vrimplantacao.dao.cadastro.ClientePreferencialDAO;
import vrimplantacao.dao.cadastro.FamiliaProdutoDAO;
import vrimplantacao.dao.cadastro.FornecedorContatoDAO;
import vrimplantacao.dao.cadastro.FornecedorDAO;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.dao.cadastro.MercadologicoDAO;
import vrimplantacao.dao.cadastro.NcmDAO;
import vrimplantacao.dao.cadastro.PlanoDAO;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.dao.cadastro.ProdutoDAO;
import vrimplantacao.dao.cadastro.ProdutoFornecedorDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.FornecedorContatoVO;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao.vo.vrimplantacao.ClientePreferencialContatoVO;
import vrimplantacao.vo.vrimplantacao.ClientePreferencialVO;
import vrimplantacao.vo.vrimplantacao.CodigoAnteriorVO;
import vrimplantacao.vo.vrimplantacao.FamiliaProdutoVO;
import vrimplantacao.vo.vrimplantacao.FornecedorVO;
import vrimplantacao.vo.vrimplantacao.MercadologicoVO;
import vrimplantacao.vo.vrimplantacao.NcmVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAliquotaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAutomacaoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoComplementoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoFornecedorVO;
import vrimplantacao.vo.vrimplantacao.ProdutoVO;
import vrimplantacao.vo.vrimplantacao.ReceberChequeVO;
import vrimplantacao.vo.vrimplantacao.ReceberCreditoRotativoVO;
import vrimplantacao.classe.Global;
import vrimplantacao.dao.cadastro.AgendaTelefoneDAO;
import vrimplantacao.dao.cadastro.OfertaDAO;
import vrimplantacao.dao.cadastro.ReceberCreditoRotativoDAO;
import vrimplantacao.vo.vrimplantacao.AgendaTelefoneVO;
import vrimplantacao.vo.vrimplantacao.OfertaVO;

public class DirectorDAO {

    //CARREGAMENTOS    
    private List<FamiliaProdutoVO> carregarFamiliaProdutoDirector() throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        List<FamiliaProdutoVO> vFamiliaProduto = new ArrayList<>();
        int id;
        String descricao;
        
        try {
            stm = ConexaoSqlServer.getConexao().createStatement();
            
            sql = new StringBuilder();
            sql.append("select distinct(f.DFcod_produto_origem) codigo, p.DFdescricao ");
            sql.append("from TBproduto_similar f ");
            sql.append("inner join TBitem_estoque p on p.DFcod_item_estoque = f.DFcod_produto_origem ");
            
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                if ((rst.getString("codigo") != null) &&
                        (!rst.getString("codigo").trim().isEmpty())) {
                
                    id = Integer.parseInt(Utils.formataNumero(rst.getString("codigo").trim()));
                    
                    if ((rst.getString("DFdescricao") != null) &&
                            (!rst.getString("DFdescricao").trim().isEmpty())) {
                        descricao = Utils.acertarTexto(rst.getString("DFdescricao").trim().replace("'", ""));
                    } else {
                        descricao = "";
                    }
                    
                    if (descricao.length() > 35) {
                        descricao = descricao.substring(0, 35);
                    }
                    
                    FamiliaProdutoVO oFamiliaProduto = new FamiliaProdutoVO();
                    oFamiliaProduto.id = id;
                    oFamiliaProduto.descricao = descricao;
                    vFamiliaProduto.add(oFamiliaProduto);                
                }
            }
            
            stm.close();
            return vFamiliaProduto;
        } catch(Exception ex) {
            throw ex;
        }
    }
    
    public List<MercadologicoVO> carregarMercadologicoSuperServer(int nivel) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        List<MercadologicoVO> vMercadologico = new ArrayList<>();
        String descricao = "";
        int mercadologico1, mercadologico2, mercadologico3;

        try {

            stm = ConexaoSqlServer.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("select m1.id as codM1, m1.nomeCategoria as descM1, ");
            sql.append("m2.id as codM2, m2.nomeCategoria as descM2, ");
            sql.append("1 codM3, m2.nomeCategoria as descM3 ");
            sql.append("from CadProduto.Categoria m1 ");
            sql.append("left join CadProduto.SubCategoria m2 on m2.fkCategoria = m1.id ");
            sql.append("order by codM1, codM2 ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                mercadologico1 = 0;
                mercadologico2 = 0;
                mercadologico3 = 0;

                MercadologicoVO oMercadologico = new MercadologicoVO();

                if (nivel == 1) {
                    mercadologico1 = Integer.parseInt(rst.getString("codM1"));

                    if ((rst.getString("descM1") != null)
                            && (!rst.getString("descM1").trim().isEmpty())) {
                        descricao = Utils.acertarTexto(rst.getString("descM1").replace("'", "").trim());
                    } else {
                        descricao = "";
                    }

                    if (descricao.length() > 35) {

                        descricao = descricao.substring(0, 35);
                    }

                    oMercadologico.mercadologico1 = mercadologico1;
                    oMercadologico.mercadologico2 = 0;
                    oMercadologico.mercadologico3 = 0;
                    oMercadologico.mercadologico4 = 0;
                    oMercadologico.mercadologico5 = 0;
                    oMercadologico.descricao = descricao;
                    oMercadologico.nivel = nivel;

                } else if ((nivel == 2)) {

                    mercadologico1 = Integer.parseInt(rst.getString("codM1"));

                    if ((rst.getString("codM2") != null)
                            && (!rst.getString("codM2").trim().isEmpty())) {

                        mercadologico2 = Integer.parseInt(rst.getString("codM2"));
                    } else {
                        mercadologico2 = 1;
                    }

                    if ((rst.getString("descM2") != null)
                            && (!rst.getString("descM2").trim().isEmpty())) {
                        descricao = Utils.acertarTexto(rst.getString("descM2").replace("'", "").trim());
                    } else {
                        descricao = Utils.acertarTexto(rst.getString("descM1").trim().replace("'", ""));
                    }

                    if (descricao.length() > 35) {

                        descricao = descricao.substring(0, 35);
                    }

                    oMercadologico.mercadologico1 = mercadologico1;
                    oMercadologico.mercadologico2 = mercadologico2;
                    oMercadologico.mercadologico3 = 0;
                    oMercadologico.mercadologico4 = 0;
                    oMercadologico.mercadologico5 = 0;
                    oMercadologico.descricao = descricao;
                    oMercadologico.nivel = nivel;
                } else if ((nivel == 3)) {
                    mercadologico1 = Integer.parseInt(rst.getString("codM1"));

                    if ((rst.getString("codM2") != null)
                            && (!rst.getString("codM2").trim().isEmpty())) {

                        mercadologico2 = Integer.parseInt(rst.getString("codM2"));
                    } else {
                        mercadologico2 = 1;
                    }

                    if ((rst.getString("codM3") != null)
                            && (!rst.getString("codM3").trim().isEmpty())) {
                        mercadologico3 = Integer.parseInt(rst.getString("codM3"));
                    } else {
                        mercadologico3 = 1;
                    }

                    if ((rst.getString("descM3") != null)
                            && (!rst.getString("descM3").trim().isEmpty())) {
                        descricao = Utils.acertarTexto(rst.getString("descM3").replace("'", "").trim());
                    } else {
                        if ((rst.getString("descM2") != null)
                                && (!rst.getString("descM2").trim().isEmpty())) {

                            descricao = Utils.acertarTexto(rst.getString("descM2").replace("'", "").trim());
                        } else {

                            descricao = Utils.acertarTexto(rst.getString("descM1").replace("'", "").trim());
                        }
                    }

                    if (descricao.length() > 35) {

                        descricao = descricao.substring(0, 35);
                    }

                    oMercadologico.mercadologico1 = mercadologico1;
                    oMercadologico.mercadologico2 = mercadologico2;
                    oMercadologico.mercadologico3 = mercadologico3;
                    oMercadologico.mercadologico4 = 0;
                    oMercadologico.mercadologico5 = 0;
                    oMercadologico.descricao = descricao;
                    oMercadologico.nivel = nivel;
                }

                vMercadologico.add(oMercadologico);
            }

            return vMercadologico;

        } catch (Exception ex) {

            throw ex;
        }
    }

    public Map<Integer, ProdutoVO> carregarProdutoDirector() throws Exception {
        Map<Integer, ProdutoVO> vProduto = new HashMap<>();
        StringBuilder sql = null;
        Statement stm = null, stmPostgres = null;
        ResultSet rst = null, rstPostgres = null;
        Utils util = new Utils();
        int idTipoEmbalagem = 0, qtdEmbalagem, idTipoPisCofins, idTipoPisCofinsCredito, tipoNaturezaReceita,
                idAliquota, idFamilia, mercadologico1, mercadologico2, mercadologico3, idSituacaoCadastro,
                ncm1, ncm2, ncm3, codigoBalanca, referencia = -1, idProduto, validade;
        String descriaoCompleta, descricaoReduzida, descricaoGondola, ncmAtual, strCodigoBarras, dataCadastro = "";
        boolean eBalanca, pesavel = false;
        long codigoBarras = 0;
        double precoVenda, custo, margem, codigoAnterior = 0, pesoBruto, pesoLiquido;

        try {

            stmPostgres = Conexao.createStatement();

            stm = ConexaoSqlServer.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("select p.DFcod_item_estoque, p.DFdescricao, p.DFdescricao_resumida, ");
            sql.append("p.DFdescricao_resumida, p.DFpeso_liquido, p.DFativo_inativo, ");
            sql.append("p.DFdata_cadastro, f.DFcod_produto_origem, ");
            sql.append("pc.DFclassificacao, pc.DFcod_classificacao_fiscal, ");
            sql.append("pc.DFcod_cst_pis, pc.DFcod_cst_cofins, ");
            sql.append("pc.DFcod_cst_pis_entrada, pc.DFcod_cst_cofins_entrada, ");
            sql.append("c.DFcodigo_barra, u.DFdescricao tipoEmbalagem ");
            sql.append("from TBitem_estoque p ");
            sql.append("left join TBproduto_similar f on f.DFcod_produto_similar = p.DFcod_item_estoque ");
            sql.append("left join TBitem_estoque_atacado_varejo pc ");
            sql.append("    on pc.DFcod_item_estoque_atacado_varejo = p.DFcod_item_estoque ");
            sql.append("inner join TBunidade_item_estoque un on un.DFcod_item_estoque = p.DFcod_item_estoque ");
            sql.append("inner join TBcodigo_barra c on c.DFid_unidade_item_estoque = un.DFid_unidade_item_estoque ");
            sql.append("inner join TBunidade u on u.DFcod_unidade = un.DFcod_unidade ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {
                    
                    idProduto = Integer.parseInt(Utils.formataNumero(rst.getString("DFcod_item_estoque").trim()));
                    codigoAnterior = Double.parseDouble(Utils.formataNumero(rst.getString("DFcod_item_estoque").trim()));
                    
                    sql = new StringBuilder();
                    sql.append("select codigo, descricao, pesavel, validade ");
                    sql.append("from implantacao.produtobalanca ");
                    sql.append("where cast(codigo as numeric(14,0)) = " + idProduto);

                    rstPostgres = stmPostgres.executeQuery(sql.toString());

                    if (rstPostgres.next()) {

                        eBalanca = true;
                        codigoBalanca = rstPostgres.getInt("codigo");
                        validade = rstPostgres.getInt("validade");
                        
                        if (null != rstPostgres.getString("pesavel").trim()) {
                            switch (rstPostgres.getString("pesavel").trim()) {
                                case "U":
                                    pesavel = true;
                                    idTipoEmbalagem = 0;
                                    break;
                                case "P":
                                    pesavel = false;
                                    idTipoEmbalagem = 4;
                                    break;
                            }
                        }

                    } else {
                        codigoBalanca = -1;
                        eBalanca = false;
                        pesavel = false;
                        validade = 0;

                        if (null != rst.getString("tipoEmbalagem").trim()) {
                            switch (rst.getString("tipoEmbalagem").trim()) {
                                case "KG":
                                    idTipoEmbalagem = 4;
                                    break;
                                case "UN":
                                    idTipoEmbalagem = 0;
                                    break;
                                case "CX":
                                    idTipoEmbalagem = 1;
                                    break;
                                default:
                                    idTipoEmbalagem = 0;
                                    break;
                            }
                        }
                    }                    
                    
                    dataCadastro = "";
                    
                    if ((rst.getString("DFativo_inativo") != null) &&
                            (!rst.getString("DFativo_inativo").trim().isEmpty())) {
                        if ("0".equals(rst.getString("DFativo_inativo").trim())) {
                            idSituacaoCadastro = 0;
                        } else {
                            idSituacaoCadastro = 1;
                        }
                    } else {
                        idSituacaoCadastro = 1;
                    }
                    
                    if ((rst.getString("DFdescricao") != null) &&
                            (!rst.getString("DFdescricao").trim().isEmpty())) {
                        descriaoCompleta = Utils.acertarTexto(rst.getString("DFdescricao").trim().replace("'", ""));
                    } else {
                        descriaoCompleta = "";
                    }
                    
                    if ((rst.getString("DFdescricao_resumida") != null) &&
                            (!rst.getString("DFdescricao_resumida").trim().isEmpty())) {
                        descricaoReduzida = Utils.acertarTexto(rst.getString("DFdescricao_resumida").trim().replace("'", ""));
                    } else {
                        descricaoReduzida = "";
                    }
                    
                    descricaoGondola = descriaoCompleta;
                    
                    if ((rst.getString("DFcod_produto_origem") != null) &&
                            (!rst.getString("DFcod_produto_origem").trim().isEmpty())) {
                        
                        idFamilia = Integer.parseInt(Utils.formataNumero(rst.getString("DFcod_produto_origem").trim()));
                        
                        sql = new StringBuilder();
                        sql.append("select id from familiaproduto ");
                        sql.append("where id = " + idFamilia);
                        
                        rstPostgres = stmPostgres.executeQuery(sql.toString());
                        
                        if (!rstPostgres.next()) {
                            idFamilia = -1;
                        }
                    } else {
                        idFamilia = -1;
                    }
                    
                    mercadologico1 = 14;
                    mercadologico2 = 1;
                    mercadologico3 = 1;

                    if ((rst.getString("DFcod_classificacao_fiscal") != null)
                            && (!rst.getString("DFcod_classificacao_fiscal").trim().isEmpty())
                            && (rst.getString("DFcod_classificacao_fiscal").trim().length() > 5)) {

                        ncmAtual = Utils.formataNumero(rst.getString("DFcod_classificacao_fiscal").trim());
                        if ((ncmAtual != null)
                                && (!ncmAtual.isEmpty())
                                && (ncmAtual.length() > 5)) {
                            try {
                                NcmVO oNcm = new NcmDAO().validar(ncmAtual);
                                ncm1 = oNcm.ncm1;
                                ncm2 = oNcm.ncm2;
                                ncm3 = oNcm.ncm3;
                            } catch (Exception ex) {
                                ncm1 = 402;
                                ncm2 = 99;
                                ncm3 = 0;
                            }
                        } else {
                            ncm1 = 402;
                            ncm2 = 99;
                            ncm3 = 0;
                        }
                    } else {
                        ncm1 = 402;
                        ncm2 = 99;
                        ncm3 = 0;
                    }
                    
                    if (eBalanca == true) {
                        codigoBarras = Long.parseLong(String.valueOf(idProduto));
                    } else {
                        
                        
                        if ((rst.getString("DFcodigo_barra") != null) &&
                                (!rst.getString("DFcodigo_barra").trim().isEmpty())) {
                            
                            if (rst.getString("DFcodigo_barra").trim().length() >= 7) {
                                codigoBarras = Long.parseLong(Utils.formataNumero(rst.getString("DFcodigo_barra").trim()));
                                
                                if (String.valueOf(codigoBarras).length() > 14) {
                                    codigoBarras = Long.parseLong(String.valueOf(codigoBarras).substring(0, 14));
                                }
                            }
                        } else {
                            codigoBarras = -2;
                        }
                    }
                    
                    if ((rst.getString("DFcod_cst_pis") != null)
                            && (!rst.getString("DFcod_cst_pis").trim().isEmpty())) {
                        idTipoPisCofins = Utils.retornarPisCofinsDebito(Integer.parseInt(rst.getString("DFcod_cst_pis").trim()));
                    } else {
                        idTipoPisCofins = 1;
                    }

                    if ((rst.getString("DFcod_cst_pis_entrada") != null)
                            && (!rst.getString("DFcod_cst_pis_entrada").trim().isEmpty())) {

                        idTipoPisCofinsCredito = Utils.retornarPisCofinsCredito(Integer.parseInt(rst.getString("DFcod_cst_pis_entrada").trim()));
                    } else {
                        idTipoPisCofinsCredito = 13;
                    }

                    //if ((rst.getString("NaturezaReceitaPisCofins") != null)
                    //        && (!rst.getString("NaturezaReceitaPisCofins").trim().isEmpty())) {
                    //    tipoNaturezaReceita = Utils.retornarTipoNaturezaReceita(idTipoPisCofins,
                    //            rst.getString("NaturezaReceitaPisCofins").trim());
                    //} else {
                        tipoNaturezaReceita = Utils.retornarTipoNaturezaReceita(idTipoPisCofins, "");
                    //}

                    /*if ((rst.getString("tribICMS") != null) &&
                            (!rst.getString("tribICMS").trim().isEmpty()) &&
                            (rst.getString("aliqICMS") != null) &&
                            (!rst.getString("aliqICMS").trim().isEmpty())) {

                        if ((rst.getString("reducaoBaseCalculo") != null) &&
                                (!rst.getString("reducaoBaseCalculo").trim().isEmpty())) {
                            
                            idAliquota = retornarIcmsKairos(rst.getString("tribICMS").trim(),
                                    rst.getString("aliqICMS").trim(),
                                    rst.getString("reducaoBaseCalculo").trim());
                            
                        } else {
                            idAliquota = retornarIcmsKairos(rst.getString("tribICMS").trim(),
                                    rst.getString("aliqICMS").trim(), "0.00");
                            
                        }
                        
                    } else {*/
                        idAliquota = 8;
                    //}   
                    
                    /*if ((rst.getString("margemDesejada") != null)
                            && (!rst.getString("margemDesejada").trim().isEmpty())) {
                        margem = Double.parseDouble(rst.getString("margemDesejada"));
                    } else {*/
                        margem = 0;
                    //}

                    precoVenda = 0;
                    custo = 0;
                    pesoBruto = 0;
                    
                    if ((rst.getString("DFpeso_liquido") != null) &&
                            (!rst.getString("DFpeso_liquido").trim().isEmpty())) {
                        pesoLiquido = Double.parseDouble(rst.getString("DFpeso_liquido").trim());
                    } else {
                        pesoLiquido = 0;
                    }                    
                    
                    qtdEmbalagem = 1;

                    if (descriaoCompleta.length() > 60) {

                        descriaoCompleta = descriaoCompleta.substring(0, 60);
                    }

                    if (descricaoReduzida.length() > 22) {

                        descricaoReduzida = descricaoReduzida.substring(0, 22);
                    }

                    if (descricaoGondola.length() > 60) {

                        descricaoGondola = descricaoGondola.substring(0, 60);
                    }

                    ProdutoVO oProduto = new ProdutoVO();
                    
                    oProduto.id = idProduto;
                    oProduto.descricaoCompleta = descriaoCompleta;
                    oProduto.descricaoReduzida = descricaoReduzida;
                    oProduto.descricaoGondola = descricaoGondola;
                    oProduto.idTipoEmbalagem = idTipoEmbalagem;
                    oProduto.qtdEmbalagem = qtdEmbalagem;
                    oProduto.idTipoPisCofinsDebito = idTipoPisCofins;
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
                    oProduto.validade = validade;
                    oProduto.margem = margem;
                    oProduto.pesoBruto = pesoBruto;
                    oProduto.pesoLiquido = pesoLiquido;
                    oProduto.dataCadastro = dataCadastro;
                    oProduto.eBalanca = eBalanca;

                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();

                    oComplemento.idSituacaoCadastro = idSituacaoCadastro;
                    oComplemento.precoVenda = precoVenda;
                    oComplemento.precoDiaSeguinte = precoVenda;
                    oComplemento.custoComImposto = custo;
                    oComplemento.custoSemImposto = custo;

                    oProduto.vComplemento.add(oComplemento);

                    ProdutoAliquotaVO oAliquota = new ProdutoAliquotaVO();

                    oAliquota.idEstado = Global.idEstado;
                    oAliquota.idAliquotaDebito = idAliquota;
                    oAliquota.idAliquotaCredito = idAliquota;
                    oAliquota.idAliquotaDebitoForaEstado = idAliquota;
                    oAliquota.idAliquotaCreditoForaEstado = idAliquota;
                    oAliquota.idAliquotaDebitoForaEstadoNF = idAliquota;

                    oProduto.vAliquota.add(oAliquota);
                    
                    ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();

                    oAutomacao.codigoBarras = codigoBarras;
                    oAutomacao.qtdEmbalagem = qtdEmbalagem;
                    oAutomacao.idTipoEmbalagem = idTipoEmbalagem;

                    oProduto.vAutomacao.add(oAutomacao);

                    CodigoAnteriorVO oCodigoAnterior = new CodigoAnteriorVO();

                    oCodigoAnterior.codigoanterior = codigoAnterior;

                    if ((rst.getString("DFcodigo_barra") != null) &&
                            (!rst.getString("DFcodigo_barra").trim().isEmpty())) {
                        oCodigoAnterior.barras = Long.parseLong(Utils.formataNumero(rst.getString("DFcodigo_barra").trim()));
                    } else {
                        oCodigoAnterior.barras = -1;
                    }

                    if ((rst.getString("DFcod_cst_pis") != null)
                            && (!rst.getString("DFcod_cst_pis").trim().isEmpty())) {
                        oCodigoAnterior.piscofinsdebito = Integer.parseInt(rst.getString("DFcod_cst_pis").trim());
                    } else {
                        oCodigoAnterior.piscofinsdebito = -1;
                    }

                    if ((rst.getString("DFcod_cst_pis_entrada") != null)
                            && (!rst.getString("DFcod_cst_pis_entrada").trim().isEmpty())) {
                        oCodigoAnterior.piscofinscredito = Integer.parseInt(rst.getString("DFcod_cst_pis_entrada").trim());
                    } else {
                        oCodigoAnterior.piscofinscredito = -1;
                    }

                    /*if ((rst.getString("naturezaReceitaPisCofins") != null)
                            && (!rst.getString("naturezaReceitaPisCofins").trim().isEmpty())) {
                        oCodigoAnterior.naturezareceita = Integer.parseInt(rst.getString("naturezaReceitaPisCofins").trim());
                    } else {*/
                        oCodigoAnterior.naturezareceita = -1;
                    //}

                    /*if ((rst.getString("tribICMS") != null)
                            && (!rst.getString("tribICMS").trim().isEmpty())) {
                        oCodigoAnterior.ref_icmsdebito = Utils.acertarTexto(rst.getString("tribICMS").trim().replace("'", ""));
                    } else {*/
                        oCodigoAnterior.ref_icmsdebito = "";
                    //}

                    oCodigoAnterior.estoque = -1;
                    oCodigoAnterior.e_balanca = eBalanca;
                    oCodigoAnterior.codigobalanca = codigoBalanca;
                    oCodigoAnterior.custosemimposto = -1;
                    oCodigoAnterior.custocomimposto = -1;
                    oCodigoAnterior.margem = -1;
                    oCodigoAnterior.precovenda = -1;
                    oCodigoAnterior.referencia = -1;

                    if ((rst.getString("DFcod_classificacao_fiscal") != null)
                            && (!rst.getString("DFcod_classificacao_fiscal").trim().isEmpty())) {
                        oCodigoAnterior.ncm = rst.getString("DFcod_classificacao_fiscal").trim();
                    } else {
                        oCodigoAnterior.ncm = "";
                    }

                    oProduto.vCodigoAnterior.add(oCodigoAnterior);

                    vProduto.put(idProduto, oProduto);
                    
                }


            stm.close();
            stmPostgres.close();
            return vProduto;

        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public Map<Integer, ProdutoVO> carregarProdutoDirectorSemBarras() throws Exception {
        Map<Integer, ProdutoVO> vProduto = new HashMap<>();
        StringBuilder sql = null;
        Statement stm = null, stmPostgres = null;
        ResultSet rst = null, rstPostgres = null;
        Utils util = new Utils();
        int idTipoEmbalagem = 0, qtdEmbalagem, idTipoPisCofins, idTipoPisCofinsCredito, tipoNaturezaReceita,
                idAliquota, idFamilia, mercadologico1, mercadologico2, mercadologico3, idSituacaoCadastro,
                ncm1, ncm2, ncm3, codigoBalanca, referencia = -1, idProduto, validade;
        String descriaoCompleta, descricaoReduzida, descricaoGondola, ncmAtual, strCodigoBarras, dataCadastro = "";
        boolean eBalanca, pesavel = false;
        long codigoBarras = 0;
        double precoVenda, custo, margem, codigoAnterior = 0, pesoBruto, pesoLiquido;

        try {

            stmPostgres = Conexao.createStatement();

            stm = ConexaoSqlServer.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("select p.DFcod_item_estoque, p.DFdescricao, ");
            sql.append("p.DFdescricao_resumida, p.DFpeso_liquido, p.DFativo_inativo, ");
            sql.append("p.DFdata_cadastro, f.DFcod_produto_origem, ");
            sql.append("pc.DFclassificacao, pc.DFcod_classificacao_fiscal, ");
            sql.append("pc.DFcod_cst_pis, pc.DFcod_cst_cofins, ");
            sql.append("pc.DFcod_cst_pis_entrada, pc.DFcod_cst_cofins_entrada, ");
            sql.append("u.DFdescricao tipoEmbalagem ");
            sql.append("from TBitem_estoque p ");
            sql.append("left join TBproduto_similar f on f.DFcod_produto_similar = p.DFcod_item_estoque ");
            sql.append("left join TBitem_estoque_atacado_varejo pc ");
            sql.append("    on pc.DFcod_item_estoque_atacado_varejo = p.DFcod_item_estoque ");
            sql.append("inner join TBunidade_item_estoque un on un.DFcod_item_estoque = p.DFcod_item_estoque ");
            sql.append("inner join TBunidade u on u.DFcod_unidade = un.DFcod_unidade ");
            sql.append("where u.DFdescricao in ('KG', 'UN') ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {
                    
                    idProduto = Integer.parseInt(Utils.formataNumero(rst.getString("DFcod_item_estoque").trim()));
                    codigoAnterior = Double.parseDouble(Utils.formataNumero(rst.getString("DFcod_item_estoque").trim()));
                    
                    sql = new StringBuilder();
                    sql.append("select codigo, descricao, pesavel, validade ");
                    sql.append("from implantacao.produtobalanca ");
                    sql.append("where cast(codigo as numeric(14,0)) = " + idProduto);

                    rstPostgres = stmPostgres.executeQuery(sql.toString());

                    if (rstPostgres.next()) {

                        eBalanca = true;
                        codigoBalanca = rstPostgres.getInt("codigo");
                        validade = rstPostgres.getInt("validade");
                        
                        if (null != rstPostgres.getString("pesavel").trim()) {
                            switch (rstPostgres.getString("pesavel").trim()) {
                                case "U":
                                    pesavel = true;
                                    idTipoEmbalagem = 0;
                                    break;
                                case "P":
                                    pesavel = false;
                                    idTipoEmbalagem = 4;
                                    break;
                            }
                        }

                    } else {
                        codigoBalanca = -1;
                        eBalanca = false;
                        pesavel = false;
                        
                        validade = 0;

                        if (null != rst.getString("tipoEmbalagem").trim()) {
                            switch (rst.getString("tipoEmbalagem").trim()) {
                                case "KG":
                                    idTipoEmbalagem = 4;
                                    break;
                                case "UN":
                                    idTipoEmbalagem = 0;
                                    break;
                                default:
                                    idTipoEmbalagem = 0;
                                    break;
                            }
                        }
                    }                    
                    
                    dataCadastro = "";
                    
                    if ((rst.getString("DFativo_inativo") != null) &&
                            (!rst.getString("DFativo_inativo").trim().isEmpty())) {
                        if ("0".equals(rst.getString("DFativo_inativo").trim())) {
                            idSituacaoCadastro = 0;
                        } else {
                            idSituacaoCadastro = 1;
                        }
                    } else {
                        idSituacaoCadastro = 1;
                    }
                    
                    if ((rst.getString("DFdescricao") != null) &&
                            (!rst.getString("DFdescricao").trim().isEmpty())) {
                        descriaoCompleta = Utils.acertarTexto(rst.getString("DFdescricao").trim().replace("'", ""));
                    } else {
                        descriaoCompleta = "";
                    }
                    
                    if ((rst.getString("DFdescricao_resumida") != null) &&
                            (!rst.getString("DFdescricao_resumida").trim().isEmpty())) {
                        descricaoReduzida = Utils.acertarTexto(rst.getString("DFdescricao_resumida").trim().replace("'", ""));
                    } else {
                        descricaoReduzida = "";
                    }
                    
                    descricaoGondola = descriaoCompleta;
                    
                    if ((rst.getString("DFcod_produto_origem") != null) &&
                            (!rst.getString("DFcod_produto_origem").trim().isEmpty())) {
                        
                        idFamilia = Integer.parseInt(Utils.formataNumero(rst.getString("DFcod_produto_origem").trim()));
                        
                        sql = new StringBuilder();
                        sql.append("select id from familiaproduto ");
                        sql.append("where id = " + idFamilia);
                        
                        rstPostgres = stmPostgres.executeQuery(sql.toString());
                        
                        if (!rstPostgres.next()) {
                            idFamilia = -1;
                        }
                    } else {
                        idFamilia = -1;
                    }
                    
                    mercadologico1 = 14;
                    mercadologico2 = 1;
                    mercadologico3 = 1;

                    if ((rst.getString("DFcod_classificacao_fiscal") != null)
                            && (!rst.getString("DFcod_classificacao_fiscal").trim().isEmpty())
                            && (rst.getString("DFcod_classificacao_fiscal").trim().length() > 5)) {

                        ncmAtual = Utils.formataNumero(rst.getString("DFcod_classificacao_fiscal").trim());
                        if ((ncmAtual != null)
                                && (!ncmAtual.isEmpty())
                                && (ncmAtual.length() > 5)) {
                            try {
                                NcmVO oNcm = new NcmDAO().validar(ncmAtual);
                                ncm1 = oNcm.ncm1;
                                ncm2 = oNcm.ncm2;
                                ncm3 = oNcm.ncm3;
                            } catch (Exception ex) {
                                ncm1 = 402;
                                ncm2 = 99;
                                ncm3 = 0;
                            }
                        } else {
                            ncm1 = 402;
                            ncm2 = 99;
                            ncm3 = 0;
                        }
                    } else {
                        ncm1 = 402;
                        ncm2 = 99;
                        ncm3 = 0;
                    }
                    
                    if (eBalanca == true) {
                        codigoBarras = Long.parseLong(String.valueOf(idProduto));
                    } else {
                        codigoBarras = -2;
                    }
                    
                    if ((rst.getString("DFcod_cst_pis") != null)
                            && (!rst.getString("DFcod_cst_pis").trim().isEmpty())) {
                        idTipoPisCofins = Utils.retornarPisCofinsDebito(Integer.parseInt(rst.getString("DFcod_cst_pis").trim()));
                    } else {
                        idTipoPisCofins = 1;
                    }

                    if ((rst.getString("DFcod_cst_pis_entrada") != null)
                            && (!rst.getString("DFcod_cst_pis_entrada").trim().isEmpty())) {

                        idTipoPisCofinsCredito = Utils.retornarPisCofinsCredito(Integer.parseInt(rst.getString("DFcod_cst_pis_entrada").trim()));
                    } else {
                        idTipoPisCofinsCredito = 13;
                    }

                    //if ((rst.getString("NaturezaReceitaPisCofins") != null)
                    //        && (!rst.getString("NaturezaReceitaPisCofins").trim().isEmpty())) {
                    //    tipoNaturezaReceita = Utils.retornarTipoNaturezaReceita(idTipoPisCofins,
                    //            rst.getString("NaturezaReceitaPisCofins").trim());
                    //} else {
                        tipoNaturezaReceita = Utils.retornarTipoNaturezaReceita(idTipoPisCofins, "");
                    //}

                    /*if ((rst.getString("tribICMS") != null) &&
                            (!rst.getString("tribICMS").trim().isEmpty()) &&
                            (rst.getString("aliqICMS") != null) &&
                            (!rst.getString("aliqICMS").trim().isEmpty())) {

                        if ((rst.getString("reducaoBaseCalculo") != null) &&
                                (!rst.getString("reducaoBaseCalculo").trim().isEmpty())) {
                            
                            idAliquota = retornarIcmsKairos(rst.getString("tribICMS").trim(),
                                    rst.getString("aliqICMS").trim(),
                                    rst.getString("reducaoBaseCalculo").trim());
                            
                        } else {
                            idAliquota = retornarIcmsKairos(rst.getString("tribICMS").trim(),
                                    rst.getString("aliqICMS").trim(), "0.00");
                            
                        }
                        
                    } else {*/
                        idAliquota = 8;
                    //}   
                    
                    /*if ((rst.getString("margemDesejada") != null)
                            && (!rst.getString("margemDesejada").trim().isEmpty())) {
                        margem = Double.parseDouble(rst.getString("margemDesejada"));
                    } else {*/
                        margem = 0;
                    //}

                    precoVenda = 0;
                    custo = 0;
                    pesoBruto = 0;
                    
                    if ((rst.getString("DFpeso_liquido") != null) &&
                            (!rst.getString("DFpeso_liquido").trim().isEmpty())) {
                        pesoLiquido = Double.parseDouble(rst.getString("DFpeso_liquido").trim());
                    } else {
                        pesoLiquido = 0;
                    }                    
                    
                    qtdEmbalagem = 1;

                    if (descriaoCompleta.length() > 60) {

                        descriaoCompleta = descriaoCompleta.substring(0, 60);
                    }

                    if (descricaoReduzida.length() > 22) {

                        descricaoReduzida = descricaoReduzida.substring(0, 22);
                    }

                    if (descricaoGondola.length() > 60) {

                        descricaoGondola = descricaoGondola.substring(0, 60);
                    }

                    ProdutoVO oProduto = new ProdutoVO();
                    
                    oProduto.id = idProduto;
                    oProduto.descricaoCompleta = descriaoCompleta;
                    oProduto.descricaoReduzida = descricaoReduzida;
                    oProduto.descricaoGondola = descricaoGondola;
                    oProduto.idTipoEmbalagem = idTipoEmbalagem;
                    oProduto.qtdEmbalagem = qtdEmbalagem;
                    oProduto.idTipoPisCofinsDebito = idTipoPisCofins;
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
                    oProduto.validade = validade;
                    oProduto.margem = margem;
                    oProduto.pesoBruto = pesoBruto;
                    oProduto.pesoLiquido = pesoLiquido;
                    oProduto.dataCadastro = dataCadastro;
                    oProduto.eBalanca = eBalanca;

                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();

                    oComplemento.idSituacaoCadastro = idSituacaoCadastro;
                    oComplemento.precoVenda = precoVenda;
                    oComplemento.precoDiaSeguinte = precoVenda;
                    oComplemento.custoComImposto = custo;
                    oComplemento.custoSemImposto = custo;

                    oProduto.vComplemento.add(oComplemento);

                    ProdutoAliquotaVO oAliquota = new ProdutoAliquotaVO();

                    oAliquota.idEstado = Global.idEstado;
                    oAliquota.idAliquotaDebito = idAliquota;
                    oAliquota.idAliquotaCredito = idAliquota;
                    oAliquota.idAliquotaDebitoForaEstado = idAliquota;
                    oAliquota.idAliquotaCreditoForaEstado = idAliquota;
                    oAliquota.idAliquotaDebitoForaEstadoNF = idAliquota;

                    oProduto.vAliquota.add(oAliquota);
                    
                    ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();

                    oAutomacao.codigoBarras = codigoBarras;
                    oAutomacao.qtdEmbalagem = qtdEmbalagem;
                    oAutomacao.idTipoEmbalagem = idTipoEmbalagem;

                    oProduto.vAutomacao.add(oAutomacao);

                    CodigoAnteriorVO oCodigoAnterior = new CodigoAnteriorVO();

                    oCodigoAnterior.codigoanterior = codigoAnterior;

                    oCodigoAnterior.barras = -1;

                    if ((rst.getString("DFcod_cst_pis") != null)
                            && (!rst.getString("DFcod_cst_pis").trim().isEmpty())) {
                        oCodigoAnterior.piscofinsdebito = Integer.parseInt(rst.getString("DFcod_cst_pis").trim());
                    } else {
                        oCodigoAnterior.piscofinsdebito = -1;
                    }

                    if ((rst.getString("DFcod_cst_pis_entrada") != null)
                            && (!rst.getString("DFcod_cst_pis_entrada").trim().isEmpty())) {
                        oCodigoAnterior.piscofinscredito = Integer.parseInt(rst.getString("DFcod_cst_pis_entrada").trim());
                    } else {
                        oCodigoAnterior.piscofinscredito = -1;
                    }

                    /*if ((rst.getString("naturezaReceitaPisCofins") != null)
                            && (!rst.getString("naturezaReceitaPisCofins").trim().isEmpty())) {
                        oCodigoAnterior.naturezareceita = Integer.parseInt(rst.getString("naturezaReceitaPisCofins").trim());
                    } else {*/
                        oCodigoAnterior.naturezareceita = -1;
                    //}

                    /*if ((rst.getString("tribICMS") != null)
                            && (!rst.getString("tribICMS").trim().isEmpty())) {
                        oCodigoAnterior.ref_icmsdebito = Utils.acertarTexto(rst.getString("tribICMS").trim().replace("'", ""));
                    } else {*/
                        oCodigoAnterior.ref_icmsdebito = "";
                    //}

                    oCodigoAnterior.estoque = -1;
                    oCodigoAnterior.e_balanca = eBalanca;
                    oCodigoAnterior.codigobalanca = codigoBalanca;
                    oCodigoAnterior.custosemimposto = -1;
                    oCodigoAnterior.custocomimposto = -1;
                    oCodigoAnterior.margem = -1;
                    oCodigoAnterior.precovenda = -1;
                    oCodigoAnterior.referencia = -1;

                    if ((rst.getString("DFcod_classificacao_fiscal") != null)
                            && (!rst.getString("DFcod_classificacao_fiscal").trim().isEmpty())) {
                        oCodigoAnterior.ncm = rst.getString("DFcod_classificacao_fiscal").trim();
                    } else {
                        oCodigoAnterior.ncm = "";
                    }

                    oProduto.vCodigoAnterior.add(oCodigoAnterior);

                    vProduto.put(idProduto, oProduto);
                    
                }


            stm.close();
            stmPostgres.close();
            return vProduto;

        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public List<ProdutoVO> carregarIcmsProdutoDirector() throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        List<ProdutoVO> vProduto = new ArrayList<>();
        int idProduto, idAliquota;
        
        try {
            
            stm = ConexaoSqlServer.getConexao().createStatement();
            
            sql = new StringBuilder();
            sql.append("WITH ");
            sql.append("produto AS( ");
            sql.append("    select p.DFcod_item_estoque, ");
            sql.append("    MIN(DFid_unidade_item_estoque) as item ");
            sql.append("    FROM VWpreco as p ");
            sql.append("    group by p.DFcod_item_estoque ");
            sql.append(") ");
            sql.append("select p.DFcod_item_estoque, ");
            sql.append("p.DFaliquota_icms, p.DFpercentual_reducao, ");
            sql.append("p.DFcod_tributacao ");
            sql.append("from VWpreco p ");
            sql.append("inner join produto p2 on p2.DFcod_item_estoque = p.DFcod_item_estoque ");
            sql.append("and p2.item = p.DFid_unidade_item_estoque ");
            
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                idProduto = Integer.parseInt(rst.getString("DFcod_item_estoque").trim());
                
                if ((rst.getString("DFaliquota_icms") != null) &&
                        (!rst.getString("DFaliquota_icms").trim().isEmpty()) &&
                        (rst.getString("DFpercentual_reducao") != null) &&
                        (!rst.getString("DFpercentual_reducao").trim().isEmpty()) &&
                        (rst.getString("DFcod_tributacao") != null) &&
                        (!rst.getString("DFcod_tributacao").trim().isEmpty())) {
                    
                    idAliquota = retornarIcmsDirector(Integer.parseInt(rst.getString("DFcod_tributacao").trim()), 
                            Double.parseDouble(rst.getString("DFaliquota_icms")), 
                            Double.parseDouble(rst.getString("DFpercentual_reducao").trim()));
                } else {
                    idAliquota = 8;
                }
                
                ProdutoVO oProduto = new ProdutoVO();
                oProduto.id = idProduto;
                
                ProdutoAliquotaVO oAliquota = new ProdutoAliquotaVO();
                oAliquota.idAliquotaDebito = idAliquota;
                oAliquota.idAliquotaCredito = idAliquota;
                oAliquota.idAliquotaDebitoForaEstado = idAliquota;
                oAliquota.idAliquotaCreditoForaEstado = idAliquota;
                oAliquota.idAliquotaDebitoForaEstadoNF = idAliquota;
                oProduto.vAliquota.add(oAliquota);
                
                CodigoAnteriorVO oAnterior = new CodigoAnteriorVO();
                
                if ((rst.getString("DFcod_tributacao") != null) &&
                        (!rst.getString("DFcod_tributacao").trim().isEmpty()) &&
                        (!"00".equals(rst.getString("DFcod_tributacao").trim()))) {
                    
                    oAnterior.ref_icmsdebito = rst.getString("DFcod_tributacao").trim();
                } else {
                    
                    if ((rst.getString("DFaliquota_icms") != null) &&
                            (!rst.getString("DFaliquota_icms").trim().isEmpty())) {
                        oAnterior.ref_icmsdebito = rst.getString("DFaliquota_icms").trim();
                    } else {
                        oAnterior.ref_icmsdebito = "";
                    }
                }
                
                if (oAnterior.ref_icmsdebito.length() > 5) {
                    oAnterior.ref_icmsdebito = oAnterior.ref_icmsdebito.substring(0, 5);
                }
                
                oProduto.vCodigoAnterior.add(oAnterior);
                
                vProduto.add(oProduto);
            }
            
            return vProduto;
        } catch(Exception ex) {
            throw ex;
        }
    }
    
    public Map<Double, ProdutoVO> carregarPrecoProdutoDirector(int idLoja, int id_lojaCliente) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        Map<Double, ProdutoVO> vProduto = new HashMap<>();
        double preco = 0, idProduto;

        try {

            stm = ConexaoSqlServer.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("select * from vw_item_mercadologic ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                idProduto = Double.parseDouble(
                        Utils.formataNumero(rst.getString("DFcod_item_estoque")));

                if ((rst.getString("DFpreco_venda") != null)
                        && (!rst.getString("DFpreco_venda").trim().isEmpty())) {
                    preco = Double.parseDouble(rst.getString("DFpreco_venda"));
                } else {
                    preco = 0;
                }
                
                ProdutoVO oProduto = new ProdutoVO();
                oProduto.idDouble = idProduto;

                ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();

                oComplemento.idLoja = idLoja;
                oComplemento.precoVenda = preco;
                oComplemento.precoDiaSeguinte = preco;

                oProduto.vComplemento.add(oComplemento);

                CodigoAnteriorVO oCodigoAnterior = new CodigoAnteriorVO();

                oCodigoAnterior.precovenda = preco;
                oCodigoAnterior.id_loja = idLoja;

                oProduto.vCodigoAnterior.add(oCodigoAnterior);

                vProduto.put(idProduto, oProduto);

            }

            return vProduto;

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }
    
    private List<OfertaVO> carregarOferta(int id_loja, int id_lojaCliente) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        List<OfertaVO> vOferta = new ArrayList<>();
        int idProduto = 0;
        String dataInicio, dataTermino;
        double precoOferta = 0;
        
        try {
            
            stm = ConexaoSqlServer.getConexao().createStatement();
            
            sql = new StringBuilder();
            sql.append("select DFcod_item_estoque, DFpreco, DFdata_inicial, DFdata_final ");
            sql.append("from VWpromocao where DFdata_final >= GETDATE() + 1 ");
            sql.append("and DFcod_empresa = " + id_lojaCliente);
            
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                idProduto = Integer.parseInt(rst.getString("DFcod_item_estoque").trim());
                dataInicio = rst.getString("DFdata_inicial").trim().substring(0, 10).replace("-", "/");
                dataTermino = rst.getString("DFdata_final").trim().substring(0, 10).replace("-", "/");
                precoOferta = Double.parseDouble(rst.getString("DFpreco").trim());
                
                OfertaVO oOferta = new OfertaVO();
                oOferta.id_produto = idProduto;
                oOferta.id_loja = id_lojaCliente;
                oOferta.precooferta = precoOferta;
                oOferta.datainicio = dataInicio;
                oOferta.datatermino = dataTermino;
                vOferta.add(oOferta);
                
            }
            
            return vOferta;
        } catch(Exception ex) {
            throw ex;
        }        
    }

    public Map<Double, ProdutoVO> carregarMargemProdutoDirector(int idLoja, int id_lojaCliente) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        Map<Double, ProdutoVO> vProduto = new HashMap<>();
        double idProduto, margem;

        try {

            stm = ConexaoSqlServer.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("WITH ");
            sql.append("produto AS( ");
            sql.append("    select p.DFcod_item_estoque, ");
            sql.append("    MIN(DFid_unidade_item_estoque) as item ");
            sql.append("    FROM VWpreco as p ");
            sql.append("    group by p.DFcod_item_estoque ");
            sql.append(") ");
            sql.append("select p.DFcod_item_estoque, ");
            sql.append("p.DFmargem_lucro ");
            sql.append("from VWpreco p ");
            sql.append("inner join produto p2 on p2.DFcod_item_estoque = p.DFcod_item_estoque ");
            sql.append("and p2.item = p.DFid_unidade_item_estoque ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                idProduto = Double.parseDouble(
                        Utils.formataNumero(rst.getString("DFcod_item_estoque")));
                
                if ((rst.getString("DFmargem_lucro") != null) &&
                        (!rst.getString("DFmargem_lucro").trim().isEmpty())) {
                    margem = Double.parseDouble(rst.getString("DFmargem_lucro").trim());
                } else {
                    margem = 0;
                }

                ProdutoVO oProduto = new ProdutoVO();
                oProduto.idDouble = idProduto;
                oProduto.margem = margem;


                vProduto.put(idProduto, oProduto);

            }

            return vProduto;

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }
    
    public Map<Double, ProdutoVO> carregarCustoProdutoDirector(int idLoja, int idLojaCliente) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        Map<Double, ProdutoVO> vProduto = new HashMap<>();
        double custo = 0, idProduto;

        try {

            stm = ConexaoSqlServer.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("select * from vw_item_mercadologic ");
            
            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                idProduto = Double.parseDouble(
                        Utils.formataNumero(rst.getString("DFcod_item_estoque")));

                if ((rst.getString("DFpreco_custo") != null)
                        && (!rst.getString("DFpreco_custo").trim().isEmpty())) {
                    custo = Double.parseDouble(rst.getString("DFpreco_custo"));
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

    public Map<Double, ProdutoVO> carregarEstoqueProdutoDirector(int idLoja, int id_lojaCliente) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        Map<Double, ProdutoVO> vProduto = new HashMap<>();
        double saldo = 0, idProduto;
        String strSaldo = "";

        try {
            stm = ConexaoSqlServer.getConexao().createStatement();
            sql = new StringBuilder();

            /*sql.append("WITH ");
            sql.append("	produto AS ( ");
            sql.append("	    select p.DFcod_item_estoque, ");
            sql.append("	           MAX(r.DFdata_ultima_alteracao) as data ");
            sql.append("          from TBitem_estoque p ");
            sql.append("	      left join TBresumo_estoque r on r.DFid_unidade_item_estoque = p.DFunidade_controle ");
            sql.append("	     where r.DFcod_empresa = " + id_lojaCliente + " ");
            sql.append("	       and r.DFid_tipo_estoque = 2 ");
            sql.append("         group by DFcod_item_estoque ");
            sql.append("    ) ");
            sql.append("select p.DFcod_item_estoque, ");
            sql.append("       r.DFquantidade_Atual, ");
            sql.append("	   r.DFdata_ultima_alteracao ");
            sql.append("  from TBitem_estoque p ");
            sql.append("  left join TBresumo_estoque r on r.DFid_unidade_item_estoque = p.DFunidade_controle ");
            sql.append(" inner join produto p2 on p2.data = r.DFdata_ultima_alteracao ");
            sql.append(" where p2.DFcod_item_estoque = p.DFcod_item_estoque ");
            sql.append("   and r.DFcod_empresa = " + id_lojaCliente + " ");
            sql.append("   and r.DFid_tipo_estoque = 2 ");
            sql.append(" order by p.DFcod_item_estoque ");*/
            
            sql.append("select * from vw_item_mercadologic ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                idProduto = Double.parseDouble(rst.getString("DFcod_item_estoque"));

                if ((rst.getString("DFestoque") != null)
                        && (!rst.getString("DFestoque").trim().isEmpty())) {
                    saldo = Double.parseDouble(rst.getString("DFestoque"));
                } else {
                    saldo = 0;
                }

                if (saldo != 0) {
                    strSaldo = rst.getString("DFsinal") + String.valueOf(saldo);
                } else {
                    strSaldo = String.valueOf(saldo);
                }
                
                
                ProdutoVO oProduto = new ProdutoVO();
                oProduto.idDouble = idProduto;

                ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();

                oComplemento.idLoja = idLoja;
                oComplemento.estoque = Double.parseDouble(strSaldo);
                oProduto.vComplemento.add(oComplemento);
                CodigoAnteriorVO oCodigoAnterior = new CodigoAnteriorVO();

                oCodigoAnterior.estoque = saldo;
                oCodigoAnterior.id_loja = idLoja;

                oProduto.vCodigoAnterior.add(oCodigoAnterior);

                vProduto.put(idProduto, oProduto);

            }

            return vProduto;

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }

    public Map<Long, ProdutoVO> carregarCodigoBarrasDirector() throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        Map<Long, ProdutoVO> vProduto = new HashMap<>();
        double idProduto;
        long codigobarras;

        try {

            stm = ConexaoSqlServer.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("select DFcod_item_estoque, DFcodigo_barra ");
            sql.append("from VWcodigo_barra ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                idProduto = Double.parseDouble(Utils.formataNumero(rst.getString("DFcod_item_estoque")));

                if ((rst.getString("DFcodigo_barra") != null)
                        && (!rst.getString("DFcodigo_barra").trim().isEmpty())) {
                    codigobarras = Long.parseLong(Utils.formataNumero(rst.getString("DFcodigo_barra")));
                } else {
                    codigobarras = -1;
                }

                if (String.valueOf(codigobarras).length() >= 7) {

                    if (String.valueOf(codigobarras).length() > 14) {
                        codigobarras = Long.parseLong(String.valueOf(codigobarras).substring(0, 14));
                    }

                    ProdutoVO oProduto = new ProdutoVO();

                    oProduto.idDouble = idProduto;

                    ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();

                    oAutomacao.codigoBarras = codigobarras;

                    oProduto.vAutomacao.add(oAutomacao);

                    vProduto.put(codigobarras, oProduto);
                }

            }

            return vProduto;

        } catch (Exception ex) {

            throw ex;
        }
    }

    private List<ProdutoVO> carregarDataCadastroProdutoDirector() throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        double idProduto = 0;
        String dataCadastro = "";
        
        try {
            stm = ConexaoSqlServer.getConexao().createStatement();
            
            sql = new StringBuilder();
            sql.append("select p.DFcod_item_estoque, p.DFdata_cadastro ");
            sql.append("from TBitem_estoque p ");
            
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                idProduto = Double.parseDouble(Utils.formataNumero(rst.getString("DFcod_item_estoque").trim()));
                
                if ((rst.getString("DFdata_cadastro") != null) &&
                        (!rst.getString("DFdata_cadastro").trim().isEmpty())) {
                    
                    dataCadastro = rst.getString("DFdata_cadastro").trim().substring(0, 10).replace(".", "/").replace("-", "/");
                    
                } else {
                    dataCadastro = "";
                }
                
                ProdutoVO oProduto = new ProdutoVO();
                oProduto.idDouble = idProduto;
                oProduto.dataCadastro = dataCadastro;
                
                vProduto.add(oProduto);
            }
            
            return vProduto;
        } catch(Exception ex) {
            throw ex;
        }
    }
    
    private List<ProdutoVO> carregarCestProdutoDirector() throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        List<ProdutoVO> vProduto = new ArrayList<>();
        double idProduto = 0;
        int ncm1 = 0, ncm2 = 0, ncm3 = 0, 
            cest1 = 0, cest2 = 0, cest3 = 0;
        String ncm = "";
        
        try {
            
            stm = ConexaoSqlServer.getConexao().createStatement();
            
            sql = new StringBuilder();
            sql.append("select ");
            sql.append("       DFcod_item_estoque_atacado_varejo, ");
            sql.append("       DFclassificacao, ");
            sql.append("       DFcod_classificacao_fiscal, ");
            sql.append("       DFcod_cest ");
            sql.append("  from TBitem_estoque_atacado_varejo ");
            sql.append(" where DFcod_cest is not null ");
            sql.append("   and DFcod_cest <> 0 ");
            sql.append(" order by DFcod_cest asc ");
            
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                idProduto = Double.parseDouble(rst.getString("DFcod_item_estoque_atacado_varejo"));
                
                cest1 = -1; cest2 = -1; cest3 = -1;
                
                if ((rst.getString("DFcod_classificacao_fiscal") != null) &&
                        (!rst.getString("DFcod_classificacao_fiscal").trim().isEmpty())) {
                    
                    ncm = Utils.formataNumero(rst.getString("DFcod_classificacao_fiscal").trim());
                    
                    if (ncm.length() >= 8) {
                        ncm1 = Integer.parseInt(ncm.substring(0, 4));
                        ncm2 = Integer.parseInt(ncm.substring(4, 6));
                        ncm3 = Integer.parseInt(ncm.substring(6, 8));
                    } else if (ncm.length() == 7) {
                        ncm1 = Integer.parseInt(ncm.substring(0, 3));
                        ncm2 = Integer.parseInt(ncm.substring(3, 5));
                        ncm3 = Integer.parseInt(ncm.substring(5, 7));
                    } else if (ncm.length() == 6) {
                        ncm1 = Integer.parseInt(ncm.substring(0, 2));
                        ncm2 = Integer.parseInt(ncm.substring(2, 4));
                        ncm3 = Integer.parseInt(ncm.substring(4, 6));
                    }                    
                } else {
                    ncm1 = -1;
                    ncm2 = -1;
                    ncm3 = -1;
                }
                
                if ((rst.getString("DFcod_cest") != null) &&
                        (!rst.getString("DFcod_cest").trim().isEmpty())) {
                    
                    if (rst.getString("DFcod_cest").trim().length() == 5) {
                        
                        cest1 = Integer.parseInt(rst.getString("DFcod_cest").trim().substring(0, 1));
                        cest2 = Integer.parseInt(rst.getString("DFcod_cest").trim().substring(1, 3));
                        cest3 = Integer.parseInt(rst.getString("DFcod_cest").trim().substring(3, 5));
                                
                    } else if (rst.getString("DFcod_cest").trim().length() == 6) {

                        cest1 = Integer.parseInt(rst.getString("DFcod_cest").trim().substring(0, 1));
                        cest2 = Integer.parseInt(rst.getString("DFcod_cest").trim().substring(1, 4));
                        cest3 = Integer.parseInt(rst.getString("DFcod_cest").trim().substring(4, 6));
                        
                    } else if (rst.getString("DFcod_cest").trim().length() == 7) {

                        cest1 = Integer.parseInt(rst.getString("DFcod_cest").trim().substring(0, 2));
                        cest2 = Integer.parseInt(rst.getString("DFcod_cest").trim().substring(2, 5));
                        cest3 = Integer.parseInt(rst.getString("DFcod_cest").trim().substring(5, 7));
                        
                    }
                    
                } else {
                    cest1 = -1;
                    cest2 = -1;
                    cest3 = -1;
                }
                
                System.out.print("cest1: " + cest1 + " cest2: " + cest2 + " cest3: " + cest3 + ";");
                System.out.print("");
                
                ProdutoVO oProduto = new ProdutoVO();
                oProduto.idDouble = idProduto;
                oProduto.cest1 = cest1;
                oProduto.cest2 = cest2;
                oProduto.cest3 = cest3;
                oProduto.ncm1 = ncm1;
                oProduto.ncm2 = ncm2;
                oProduto.ncm3 = ncm3;
                
                vProduto.add(oProduto);
            }
            
            return vProduto;
        } catch(Exception ex) {
            throw ex;
        }        
    }
    
    public List<FornecedorVO> carregarFornecedorDirector() throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        List<FornecedorVO> vFornecedor = new ArrayList<>();

        String razaosocial, nomefantasia, obs, inscricaoestadual, endereco, bairro, datacadastro,
                numero = "", complemento = "", telefone = "", email = "", fax = "", orgaoExp = "",
                logradouro = "";
        int id, id_municipio = 0, id_estado, id_tipoinscricao = 0, Linha = 0;
        long cnpj, cep;
        boolean ativo = true, utilizaNfe = false;

        try {
            stm = ConexaoSqlServer.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("select f.DFcod_fornecedor, f.DFnome, f.DFnome_fantasia, f.DFcgc, f.DFinscr_estadual, ");
            sql.append("f.DFfisico_juridico, f.DFobservacao, f.DFdata_cadastro, f.DFid_cep_logradouro, ");
            sql.append("f.DFcomplemento_endereco, f.DFutiliza_nfe, ");
            sql.append("cep.DFcod_cep, tlog.DFdescricao logradouro, logr.DFdescricao rua, logr.DFcomplemento, ");
            sql.append("bai.DFdescricao bairro, loc.DFcod_uf, loc.DFdescricao cidade, ");
            sql.append("(select top(1) DFtelefone ");
            sql.append("   from TBcontato_fornecedor ");
            sql.append("  where DFcod_fornecedor = f.DFcod_fornecedor) as telefone ");
            sql.append("from TBfornecedor f ");
            sql.append("left join TBcep_logradouro cep on cep.DFid_cep_logradouro = f.DFid_cep_logradouro ");
            sql.append("left join TBlogradouro logr on logr.DFid_logradouro = cep.DFid_logradouro ");
            sql.append("left join TBtipo_logradouro tlog on tlog.DFcod_tipo_logradouro = logr.DFcod_tipo_logradouro ");
            sql.append("left join TBbairro bai on bai.DFid_bairro = logr.DFid_bairro ");
            sql.append("left join TBlocalidade loc on loc.DFcod_localidade = bai.DFcod_localidade ");
            sql.append("where f.DFcgc not like '%00000000000000%' ");
            sql.append("order by f.DFnome ");

            rst = stm.executeQuery(sql.toString());

            Linha = 0;

            while (rst.next()) {
                FornecedorVO oFornecedor = new FornecedorVO();

                if ((rst.getString("DFnome") != null)
                        && (!rst.getString("DFnome").isEmpty())) {
                    byte[] bytes = rst.getBytes("DFnome");
                    String textoAcertado = new String(bytes, "ISO-8859-1");
                    razaosocial = Utils.acertarTexto(textoAcertado.replace("'", "").trim());
                } else {
                    razaosocial = "SEM RAZAO SOCIAL";
                }

                if ((rst.getString("DFnome_fantasia") != null)
                        && (!rst.getString("DFnome_fantasia").isEmpty())) {
                    byte[] bytes = rst.getBytes("DFnome_fantasia");
                    String textoAcertado = new String(bytes, "ISO-8859-1");
                    nomefantasia = Utils.acertarTexto(textoAcertado.replace("'", "").trim());
                } else {
                    nomefantasia = razaosocial;
                }

                if ((rst.getString("DFcgc") != null)
                        && (!rst.getString("DFcgc").isEmpty()) &&
                        (!"00000000000000".equals(rst.getString("DFcgc").trim()))) {
                    cnpj = Long.parseLong(Utils.formataNumero(rst.getString("DFcgc").trim()));
                } else {
                    cnpj = -1;
                }

                if ((rst.getString("DFfisico_juridico") != null) &&
                        (!rst.getString("DFfisico_juridico").trim().isEmpty())) {
                    
                    if ("J".equals(rst.getString("DFfisico_juridico").trim())) {
                        id_tipoinscricao = 0;
                    } else {
                        id_tipoinscricao = 1;
                    }
                } else {
                    id_tipoinscricao = 0;
                }
                
                if ((rst.getString("DFinscr_estadual") != null)
                        && (!rst.getString("DFinscr_estadual").isEmpty())) {
                    inscricaoestadual = Utils.acertarTexto(rst.getString("DFinscr_estadual").replace("'", "").trim());
                } else {
                    inscricaoestadual = "ISENTO";
                }

                if ((rst.getString("logradouro") != null) &&
                        (!rst.getString("logradouro").trim().isEmpty())) {
                    logradouro = Utils.acertarTexto(rst.getString("logradouro").trim().replace("'", ""));
                } else {
                    logradouro = "";
                }
                
                if ((rst.getString("rua") != null) &&
                        (!rst.getString("rua").trim().isEmpty())) {
                    endereco = logradouro +" "+ Utils.acertarTexto(rst.getString("rua").trim().replace("'", ""));
                } else {
                    endereco = "";
                }

                if ((rst.getString("bairro") != null)
                        && (!rst.getString("bairro").isEmpty())) {
                    bairro = Utils.acertarTexto(rst.getString("bairro").replace("'", "").trim());
                } else {
                    bairro = "";
                }

                if ((rst.getString("DFcod_cep") != null)
                        && (!rst.getString("DFcod_cep").isEmpty())) {
                    cep = Long.parseLong(Utils.formataNumero(rst.getString("DFcod_cep").trim()));
                } else {
                    cep = Long.parseLong(String.valueOf(Global.Cep));
                }

                if ((rst.getString("cidade") != null)
                        && (!rst.getString("cidade").isEmpty())) {

                    if ((rst.getString("DFcod_uf") != null)
                            && (!rst.getString("DFcod_uf").isEmpty())) {

                        id_municipio = util.retornarMunicipioIBGEDescricao(Utils.acertarTexto(rst.getString("cidade").replace("'", "").trim()),
                                Utils.acertarTexto(rst.getString("DFcod_uf").replace("'", "").trim()));

                        if (id_municipio == 0) {
                            id_municipio = Global.idMunicipio;
                        }
                    }
                } else {
                    id_municipio = Global.idMunicipio;
                }

                if ((rst.getString("DFcod_uf") != null)
                        && (!rst.getString("DFcod_uf").isEmpty())) {
                    id_estado = Utils.retornarEstadoDescricao(Utils.acertarTexto(rst.getString("DFcod_uf").replace("'", "").trim()));

                    if (id_estado == 0) {
                        id_estado = Global.idEstado;
                    }
                } else {
                    id_estado = Global.idEstado;
                }

                if (rst.getString("DFobservacao") != null) {
                    obs = Utils.acertarTexto(rst.getString("DFobservacao").trim().replace("'", ""));
                } else {
                    obs = "";
                }

                if ((rst.getString("DFdata_cadastro") != null)
                        && (!rst.getString("DFdata_cadastro").trim().isEmpty())) {
                    datacadastro = rst.getString("DFdata_cadastro").trim().substring(0, 10).replace("-", "/");
                } else {
                    datacadastro = "";
                }

                ativo = true;

                if ((rst.getString("DFcomplemento_endereco") != null)
                        && (!rst.getString("DFcomplemento_endereco").trim().isEmpty()) &&
                        (!"S/N".equals(rst.getString("DFcomplemento_endereco").trim()))) {
                    numero = Utils.acertarTexto(rst.getString("DFcomplemento_endereco").trim().replace("'", ""));
                } else {
                    numero = "0";
                }

                if ((rst.getString("DFcomplemento") != null)
                        && (!rst.getString("DFcomplemento").trim().isEmpty())) {
                    complemento = Utils.acertarTexto(rst.getString("DFcomplemento").replace("'", "").trim());
                } else {
                    complemento = "";
                }

                if ((rst.getString("telefone") != null)
                        && (!rst.getString("telefone").trim().isEmpty())) {
                    telefone = Utils.formataNumero(rst.getString("telefone").trim());
                } else {
                    telefone = "0";
                }

                if ((rst.getString("DFutiliza_nfe") != null) &&
                        (!rst.getString("DFutiliza_nfe").trim().isEmpty())) {
                    
                    utilizaNfe = "1".equals(rst.getString("DFutiliza_nfe").trim());
                } else {
                    utilizaNfe = false;
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
                
                if (numero.length() > 6) {
                    numero = numero.substring(0, 6);
                }
                
                if (complemento.length() > 30) {
                    complemento = complemento.substring(0, 30);
                }

                oFornecedor.codigoanterior = Long.parseLong(Utils.formataNumero(rst.getString("DFcod_fornecedor").trim()));
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
                oFornecedor.email = email;
                oFornecedor.fax = fax;
                oFornecedor.utilizanfe = utilizaNfe;

                vFornecedor.add(oFornecedor);
            }
            return vFornecedor;

        } catch (Exception ex) {
            throw ex;
        }
    }

    public List<FornecedorContatoVO> carregarFornecedorContatoDirector() throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        List<FornecedorContatoVO> vFornecedorContato = new ArrayList<>();
        long idFornecedor = 0;
        String telefone = "", email = "", contato = "", fax = "", celular = "";
        
        try {
            
            stm = ConexaoSqlServer.getConexao().createStatement();
            
            sql = new StringBuilder();
            sql.append("select DFcod_fornecedor, DFtelefone, DFfax, DFcontato, DFe_mail, ");
            sql.append("DFtelefone_celular ");
            sql.append("from TBcontato_fornecedor ");
            
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                idFornecedor = Long.parseLong(rst.getString("DFcod_fornecedor").trim());
                
                if ((rst.getString("DFtelefone") != null) &&
                        (!rst.getString("DFtelefone").trim().isEmpty())) {
                    telefone = Utils.formataNumero(rst.getString("DFtelefone").trim());
                } else {
                    telefone = "";
                }
                
                if ((rst.getString("DFcontato") != null) &&
                        (!rst.getString("DFcontato").trim().isEmpty())) {
                    contato = Utils.acertarTexto(rst.getString("DFcontato").trim().replace("'", ""));
                } else {
                    contato = "CONTATO";
                }
                
                if ((rst.getString("DFe_mail") != null) &&
                        (!rst.getString("DFe_mail").trim().isEmpty()) &&
                        (rst.getString("DFe_mail").contains("@"))) {
                    email = Utils.acertarTexto(rst.getString("DFe_mail").trim().replace("'", ""));
                    email = email.toLowerCase();
                } else {
                    email = "";
                }
                
                if ((rst.getString("DFtelefone_celular") != null) &&
                        (!rst.getString("DFtelefone_celular").trim().isEmpty())) {
                    celular = Utils.formataNumero(rst.getString("DFtelefone_celular").trim());
                } else {
                    celular = "";
                }
                
                if ((rst.getString("DFfax") != null) &&
                        (!rst.getString("DFfax").trim().isEmpty())) {
                    fax = Utils.formataNumero(rst.getString("DFfax").trim());
                } else {
                    fax = "";
                }
                
                FornecedorContatoVO oFornecedorContato = new FornecedorContatoVO();
                oFornecedorContato.setNome(contato);
                oFornecedorContato.setIdFornecedorAnterior(idFornecedor);
                oFornecedorContato.setTelefone(telefone);
                oFornecedorContato.setCelular(celular);
                oFornecedorContato.setEmail(email);
                oFornecedorContato.setFax(fax);
                
                vFornecedorContato.add(oFornecedorContato);
            }
            
            return vFornecedorContato;
        } catch(Exception ex) {
            throw ex;
        }
    }
    
    private List<FornecedorVO> carregarSituacaoCadastroFornecedor() throws Exception {
        
        StringBuilder sql = new StringBuilder();
        Statement stm = null;
        ResultSet rst = null;
        int idSituacaoCadastro = 0, idFornecedor = 0;        
        String observacao = "";
        List<FornecedorVO> vFornecedor = new ArrayList<>();
        
        try {
            
            stm = ConexaoSqlServer.getConexao().createStatement();
            
            sql = new StringBuilder();
            sql.append("select DFcod_fornecedor, DFdata_inativacao ");
            sql.append("from TBfornecedor  ");
            
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                observacao = "";
                idFornecedor = Integer.parseInt(rst.getString("DFcod_fornecedor"));
                
                if ((rst.getString("DFdata_inativacao") != null) &&
                        (!rst.getString("DFdata_inativacao").trim().isEmpty())) {
                    idSituacaoCadastro = 0;
                    
                    observacao = " => DATA INATIVACAO.: " + rst.getString("DFdata_inativacao");
                    
                } else {
                    idSituacaoCadastro = 1;
                }
                
                FornecedorVO oFornecedor = new FornecedorVO();
                oFornecedor.id = idFornecedor;
                oFornecedor.id_situacaocadastro = idSituacaoCadastro;
                oFornecedor.observacao = observacao;
                vFornecedor.add(oFornecedor);
            }
            
            stm.close();
            return vFornecedor;
            
        } catch(Exception ex) {
            throw ex;
        }
    }


    
    public List<ProdutoFornecedorVO> carregarProdutoFornecedorDirector() throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        List<ProdutoFornecedorVO> vProdutoFornecedor = new ArrayList<>();
        double idProduto, idFornecedor;
        String codigoExterno;
        java.sql.Date dataAlteracao = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

        try {

            stm = ConexaoSqlServer.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("select DFcod_fornecedor, DFcod_item_estoque, DFpart_number ");
            sql.append("from TBfornecedor_item ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                idFornecedor = Double.parseDouble(rst.getString("DFcod_fornecedor").trim());
                idProduto = Double.parseDouble(rst.getString("DFcod_item_estoque"));

                if ((rst.getString("DFpart_number") != null)
                        && (!rst.getString("DFpart_number").trim().isEmpty())) {
                    codigoExterno = Utils.acertarTexto(rst.getString("DFpart_number").replace("'", "").trim());
                } else {
                    codigoExterno = "";
                }
                
                dataAlteracao = new Date(new java.util.Date().getTime());

                ProdutoFornecedorVO oProdutoFornecedor = new ProdutoFornecedorVO();

                oProdutoFornecedor.id_fornecedorDouble = idFornecedor;
                oProdutoFornecedor.id_produtoDouble = idProduto;
                oProdutoFornecedor.dataalteracao = dataAlteracao;
                oProdutoFornecedor.codigoexterno = codigoExterno;

                vProdutoFornecedor.add(oProdutoFornecedor);
            }

            return vProdutoFornecedor;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public List<ClientePreferencialVO> carregarClienteDirector(int idLoja, int idLojaCliente) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        List<ClientePreferencialVO> vClientePreferencial = new ArrayList<>();

        String nome, endereco, bairro, telefone1, inscricaoestadual, email, enderecoEmpresa, nomeConjuge,
                dataResidencia, dataCadastro, numero, complemento, dataNascimento, nomePai, nomeMae,
                telefone2 = "", fax = "", observacao = "", empresa = "", telEmpresa = "", cargo = "",
                conjuge = "", orgaoExp = "", observacao2 = "", logradouro = "", celular = "";
        int id_municipio = 0, id_estado, id_sexo, id_tipoinscricao = 1, id, id_situacaocadastro, Linha = 0,
                estadoCivil = 0;
        long cnpj, cep;
        double limite, salario;
        boolean bloqueado;
        //DecimalFormat df = new DecimalFormat("#.00");

        try {
            stm = ConexaoSqlServer.getConexao().createStatement();

            sql = new StringBuilder();

            sql.append("select c.DFcod_cliente, c.DFnome, c.DFnome_fantasia, c.DFcnpj_cpf, c.DFinscr_estadual, ");
            sql.append("c.DFdata_cadastro, c.DFfisico_juridico, c.DFobservacao,  tpCli.DFdescricao, ");
            sql.append("c.DFlimite_credito, DFbloqueado, c.DFdata_inativacao, c.DFcarteira_identidade, ");
            sql.append("cepLog.DFcod_cep, endCli.DFcomplemento_endereco, tlogr.DFdescricao logradouro, ");
            sql.append("tLog.DFdescricao rua, bai.DFdescricao bairro, loc.DFcod_uf, loc.DFdescricao cidade, ");
            sql.append("(select top(1) DFtelefone ");
            sql.append("from TBcontato_cliente ");
            sql.append("where DFcod_cliente = c.DFcod_cliente ");
            sql.append("and DFtelefone is not null ");
            sql.append("and DFtelefone <> '') as telefone, ");
            sql.append("(select top(1) DFtelefone_celular ");
            sql.append("from TBcontato_cliente ");
            sql.append("where DFcod_cliente = c.DFcod_cliente ");
            sql.append("and DFtelefone_celular is not null ");
            sql.append("and DFtelefone_celular <> '') as celular, ");
            sql.append("(select top(1) DFe_mail ");
            sql.append("from TBcontato_cliente ");
            sql.append("where DFcod_cliente = c.DFcod_cliente ");
            sql.append("and DFe_mail is not null ");
            sql.append("and DFe_mail <> '' ) as email ");
            sql.append("from TBcliente c ");
            sql.append("left join TBtipo_cliente tpCli on tpCli.DFid_tipo_cliente =  c.DFid_tipo_cliente ");
            sql.append("left join TBendereco_cliente endCli on endCli.DFcod_cliente = c.DFcod_cliente and endCli.DFtipo_endereco = 'N' ");
            sql.append("left join TBcep_logradouro cepLog on cepLog.DFid_cep_logradouro = endCli.DFid_cep_logradouro ");
            sql.append("left join TBlogradouro tLog on tLog.DFid_logradouro = cepLog.DFid_logradouro ");
            sql.append("left join TBtipo_logradouro tlogr on tlogr.DFcod_tipo_logradouro = tLog.DFcod_tipo_logradouro ");
            sql.append("left join TBbairro bai on bai.DFid_bairro = tLog.DFid_bairro ");
            sql.append("left join TBlocalidade loc on loc.DFcod_localidade = bai.DFcod_localidade ");
            sql.append("order by c.DFcod_cliente ");

            rst = stm.executeQuery(sql.toString());
            Linha = 1;
            try {
                while (rst.next()) {
                    ClientePreferencialVO oClientePreferencial = new ClientePreferencialVO();

                    id = rst.getInt("DFcod_cliente");

                    if ((rst.getString("DFdata_inativacao") != null)
                            && (!rst.getString("DFdata_inativacao").trim().isEmpty())) {
                        id_situacaocadastro = 0;
                    } else {
                        id_situacaocadastro = 1;
                    }

                    dataResidencia = "1990/01/01";

                    if ((rst.getString("DFfisico_juridico") != null)
                            && (!rst.getString("DFfisico_juridico").trim().isEmpty())) {

                        if ("F".equals(rst.getString("DFfisico_juridico").trim())) {
                            id_tipoinscricao = 1;
                        } else {
                            id_tipoinscricao = 0;
                        }
                    } else {
                        id_tipoinscricao = 1;
                    }

                    if ((rst.getString("DFcnpj_cpf") != null)
                            && (!rst.getString("DFcnpj_cpf").trim().isEmpty())
                            && (!rst.getString("DFcnpj_cpf").contains("00000000"))) {
                        cnpj = Long.parseLong(Utils.formataNumero(rst.getString("DFcnpj_cpf").trim()));

                    } else {
                        cnpj = -1;
                    }

                    if ((rst.getString("DFnome") != null)
                            && (!rst.getString("DFnome").trim().isEmpty())) {
                        byte[] bytes = rst.getBytes("DFnome");
                        String textoAcertado = new String(bytes, "ISO-8859-1");
                        nome = Utils.acertarTexto(textoAcertado.replace("'", "").trim());
                    } else {

                        if ((rst.getString("DFnome_fantasia") != null)
                                && (!rst.getString("DFnome_fantasia").trim().isEmpty())) {
                            byte[] bytes = rst.getBytes("DFnome_fantasia");
                            String textoAcertado = new String(bytes, "ISO-8859-1");
                            nome = Utils.acertarTexto(textoAcertado.replace("'", "").trim());
                        } else {
                            nome = "SEM NOME VR " + id;
                        }
                    }

                    if ((rst.getString("logradouro") != null)
                            && (!rst.getString("logradouro").trim().isEmpty())) {
                        logradouro = Utils.acertarTexto(rst.getString("logradouro").trim());
                    } else {
                        logradouro = "";
                    }

                    if ((rst.getString("rua") != null)
                            && (!rst.getString("rua").trim().isEmpty())) {
                        endereco = logradouro + " " + Utils.acertarTexto(rst.getString("rua").replace("'", "").trim());
                    } else {
                        endereco = "";
                    }

                    if ((rst.getString("bairro") != null)
                            && (!rst.getString("bairro").trim().isEmpty())) {
                        bairro = Utils.acertarTexto(rst.getString("bairro").trim().replace("'", ""));
                    } else {
                        bairro = "";
                    }

                    if ((rst.getString("DFcomplemento_endereco") != null)
                            && (!rst.getString("DFcomplemento_endereco").trim().isEmpty())
                            && (!util.encontrouLetraCampoNumerico(rst.getString("DFcomplemento_endereco").trim()))) {
                        numero = Utils.acertarTexto(rst.getString("DFcomplemento_endereco").trim().replace("'", ""));
                    } else {
                        numero = "0";
                    }

                    if ((rst.getString("DFcomplemento_endereco") != null)
                            && (!rst.getString("DFcomplemento_endereco").trim().isEmpty())) {
                        complemento = Utils.acertarTexto(rst.getString("DFcomplemento_endereco").trim().replace("'", ""));
                    } else {
                        complemento = "";
                    }

                    if ((rst.getString("telefone") != null)
                            && (!rst.getString("telefone").trim().isEmpty())) {
                        telefone1 = Utils.formataNumero(rst.getString("telefone").trim());
                    } else {
                        telefone1 = "0";
                    }

                    if ((rst.getString("DFcod_cep") != null)
                            && (!rst.getString("DFcod_cep").trim().isEmpty())) {
                        cep = Long.parseLong(Utils.formataNumero(rst.getString("DFcod_cep").trim()));
                    } else {
                        cep = 0;
                    }

                    if ((rst.getString("cidade") != null)
                            && (!rst.getString("cidade").trim().isEmpty())
                            && (rst.getString("DFcod_uf") != null)
                            && (!rst.getString("DFcod_uf").trim().isEmpty())) {

                        id_municipio = util.retornarMunicipioIBGEDescricao(Utils.acertarTexto(rst.getString("cidade").trim().replace("'", "")),
                                rst.getString("DFcod_uf").trim().replace("'", ""));

                        if (id_municipio == 0) {
                            id_municipio = Global.idMunicipio;
                        }
                    } else {
                        id_municipio = Global.idMunicipio;
                    }

                    if ((rst.getString("DFcod_uf") != null)
                            && (!rst.getString("DFcod_uf").trim().isEmpty())) {
                        id_estado = Utils.retornarEstadoDescricao(
                                Utils.acertarTexto(rst.getString("DFcod_uf").trim().replace("'", "")));

                        if (id_estado == 0) {
                            id_estado = Global.idEstado;
                        }
                    } else {
                        id_estado = Global.idEstado;
                    }

                    if ((rst.getString("DFlimite_credito") != null)
                            && (!rst.getString("DFlimite_credito").trim().isEmpty())) {

                        limite = rst.getDouble("DFlimite_credito");

                        if (limite == 1000000000) {
                            limite = 100000000;
                            observacao2 = "VALOR LIMITE SISTEMA DIRECTOR: "
                                    + rst.getString("DFlimite_credito") + " = VALOR LIMITE VR: " + limite + ", VALOR SISTEMA DIRECTOR NÃO SUPORTADO PELO VR.";

                        }

                    } else {
                        limite = 0;
                    }

                    if ((rst.getString("DFinscr_estadual") != null)
                            && (!rst.getString("DFinscr_estadual").trim().isEmpty())) {
                        inscricaoestadual = Utils.acertarTexto(rst.getString("DFinscr_estadual").trim());
                        inscricaoestadual = inscricaoestadual.replace("'", "");
                        inscricaoestadual = inscricaoestadual.replace("-", "");
                        inscricaoestadual = inscricaoestadual.replace(".", "");
                    } else {
                        inscricaoestadual = "ISENTO";
                    }

                    if ((rst.getString("DFdata_cadastro") != null)
                            && (!rst.getString("DFdata_cadastro").trim().isEmpty())) {
                        dataCadastro = rst.getString("DFdata_cadastro").substring(0, 10).trim().replace("-", "/");
                    } else {
                        dataCadastro = "";
                    }

                    dataNascimento = null;

                    if ((rst.getString("DFbloqueado") != null)
                            && (!rst.getString("DFbloqueado").trim().isEmpty())) {
                        bloqueado = true;
                    } else {
                        bloqueado = false;
                    }

                    nomePai = "";

                    nomeMae = "";

                    telefone2 = "";

                    fax = "";

                    if ((rst.getString("DFobservacao") != null)
                            && (!rst.getString("DFobservacao").trim().isEmpty())) {
                        observacao = Utils.acertarTexto(rst.getString("DFobservacao").replace("'", "").trim());
                    } else {
                        observacao = "";
                    }

                    if ((rst.getString("email") != null)
                            && (!rst.getString("email").trim().isEmpty())
                            && (rst.getString("email").contains("@"))) {
                        email = Utils.acertarTexto(rst.getString("email").trim());
                        email = email.toLowerCase();
                    } else {
                        email = "";
                    }

                    if ((rst.getString("celular") != null)
                            && (!rst.getString("celular").trim().isEmpty())) {
                        celular = Utils.formataNumero(rst.getString("celular").trim());
                    } else {
                        celular = "";
                    }
                    /*if ((rst.getString("Sexo") != null)
                     && (!rst.getString("Sexo").trim().isEmpty())) {
                     if ("F".equals(rst.getString("Sexo").trim())) {
                     id_sexo = 0;
                     } else {
                     id_sexo = 1;
                     }
                     } else {*/
                    id_sexo = 1;
                    //}

                    empresa = "";

                    telEmpresa = "";

                    cargo = "";

                    enderecoEmpresa = "";

                    salario = 0;

                    /*if ((rst.getString("EstadoCivil") != null)
                     && (!rst.getString("EstadoCivil").trim().isEmpty())) {
                     if (null != rst.getString("EstadoCivil").trim()) 
                     switch (rst.getString("EstadoCivil").trim()) {
                     case "C":
                     estadoCivil = 2;
                     break;
                     case "S":
                     estadoCivil = 1;
                     break;
                     case "O":
                     estadoCivil = 5;
                     break;
                     }
                     } else {*/
                    estadoCivil = 0;
                    //}

                    conjuge = "";

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

                    if (celular.length() > 14) {
                        celular = celular.substring(0, 14);
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

                    if (numero.length() > 6) {
                        numero = numero.substring(0, 6);
                    }

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
                    oClientePreferencial.celular = celular;
                    oClientePreferencial.bloqueado = bloqueado;
                    oClientePreferencial.id_situacaocadastro = id_situacaocadastro;
                    oClientePreferencial.telefone2 = telefone2;
                    oClientePreferencial.observacao = observacao;
                    oClientePreferencial.observacao2 = observacao2;
                    oClientePreferencial.datanascimento = dataNascimento;
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

    public List<ClientePreferencialContatoVO> carregarClientePreferencialContatoDirector() throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        List<ClientePreferencialContatoVO> vClientePreferencialContato = new ArrayList<>();
        long idCliente = 0;
        String telefone = "", email = "", contato = "", fax = "", celular = "";

        try {

            stm = ConexaoSqlServer.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("select DFcod_cliente, DFtelefone, DFfax, DFcontato, DFe_mail, ");
            sql.append("DFtelefone_celular ");
            sql.append("from TBcontato_cliente ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                idCliente = Long.parseLong(rst.getString("DFcod_cliente").trim());

                if ((rst.getString("DFtelefone") != null)
                        && (!rst.getString("DFtelefone").trim().isEmpty())) {
                    telefone = Utils.formataNumero(rst.getString("DFtelefone").trim());
                } else {
                    telefone = "";
                }

                if ((rst.getString("DFcontato") != null)
                        && (!rst.getString("DFcontato").trim().isEmpty())) {
                    contato = Utils.acertarTexto(rst.getString("DFcontato").trim().replace("'", ""));
                } else {
                    contato = "CONTATO";
                }

                if ((rst.getString("DFe_mail") != null)
                        && (!rst.getString("DFe_mail").trim().isEmpty())
                        && (rst.getString("DFe_mail").contains("@"))) {
                    email = Utils.acertarTexto(rst.getString("DFe_mail").trim().replace("'", ""));
                    email = email.toLowerCase();
                } else {
                    email = "";
                }

                if ((rst.getString("DFtelefone_celular") != null)
                        && (!rst.getString("DFtelefone_celular").trim().isEmpty())) {
                    celular = Utils.formataNumero(rst.getString("DFtelefone_celular").trim());
                } else {
                    celular = "";
                }

                if ((rst.getString("DFfax") != null)
                        && (!rst.getString("DFfax").trim().isEmpty())) {
                    fax = Utils.formataNumero(rst.getString("DFfax").trim());
                } else {
                    fax = "";
                }

                ClientePreferencialContatoVO oClientePreferencialContato = new ClientePreferencialContatoVO();
                oClientePreferencialContato.setNome(contato);
                oClientePreferencialContato.setIdClientePreferencialAnterior(idCliente);
                oClientePreferencialContato.setTelefone(telefone);
                oClientePreferencialContato.setCelular(celular);
                oClientePreferencialContato.setEmail(email);

                vClientePreferencialContato.add(oClientePreferencialContato);
            }

            return vClientePreferencialContato;
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

        int numerocupom, idBanco = 0, cheque, idTipoInscricao = 0, id_tipoalinea, ecf = 0;
        double valor, juros;
        long cpfCnpj = 0;
        String observacao = "", dataemissao = "", datavencimento = "",
                agencia = null, conta = null, nome, rg, telefone;

        try {

            stm = ConexaoSqlServer.getConexao().createStatement();

            sql = new StringBuilder();
            /*sql.append("SELECT c.cheque, c.ciccgc, c.client, c.bancox, c.agenci, c.contax, ");
             sql.append("c.valorx, c.dataxx, c.vencim, c.status, c.devol1, c.motdv1, c.devol2, c.motdv2, ");
             sql.append("c.reapre, c.quitad, c.codfor, c.nomfor, c.datfor, c.caixax, c.observ, c.seqdev, ");
             sql.append("c.datcad, c.usucad, c.datalt, c.usualt, c.cobran, c.datcob, c.entrad ");
             sql.append("FROM CHEQUES c ");             
             sql.append("WHERE c.FILIAL = "+String.valueOf(id_lojaCliente));                         */

            sql.append("select R.CXANUM, R.CTRNUM, R.CTRDATEMI, R.CTRDATVNC, ");
            sql.append("R.CTROBS, R.CTRNUMBCO, C.CLIDES, C.CLICPFCGC, ");
            sql.append("R.CTRVLRDEV, C.CLIRGCGF, C.CLITEL, C.CLIPFPJ ");
            sql.append("from CONTARECEBER R ");
            sql.append("INNER JOIN CLIENTE C ON C.CLICOD = R.CLICOD ");
            sql.append("where FZDCOD in ('002', '003') ");
            sql.append("and CTRDATPGT is null ");
            sql.append("or CTRVLRPAG < CTRVLRDEV ");
            sql.append("and R.CTRVLRDEV > 0 ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                if ((rst.getString("CLICPFCGC") != null)
                        && (!rst.getString("CLICPFCGC").trim().isEmpty())) {
                    cpfCnpj = Long.parseLong(rst.getString("CLICPFCGC").trim());
                } else {
                    cpfCnpj = 0;
                }

                if ((rst.getString("CLIPFPJ") != null)
                        && (!rst.getString("CLIPFPJ").trim().isEmpty())) {

                    if ("J".equals(rst.getString("CLIPFPJ").trim())) {
                        idTipoInscricao = 0;
                    } else if ("F".equals(rst.getString("CLIPFPJ").trim())) {
                        idTipoInscricao = 1;
                    }
                } else {
                    idTipoInscricao = 1;
                }

                idBanco = 804;

                //if ((rst.getString("agenci") != null) &&
                //        (!rst.getString("agenci").trim().isEmpty())) {
                //    agencia = util.acertarTexto(rst.getString("agenci").trim().replace("'", ""));
                //} else {
                agencia = "";
                //}

                //if ((rst.getString("contax") != null) &&
                //        (!rst.getString("contax").trim().isEmpty()))  {
                //    conta = util.acertarTexto(rst.getString("contax").trim().replace("'", ""));
                //} else {
                conta = "";

                //if ((rst.getString("cheque") != null) &&
                //        (!rst.getString("cheque").trim().isEmpty())) {
                //    
                //    cheque = Integer.parseInt(util.formataNumero(rst.getString("cheque")));
                //    
                //    if (String.valueOf(cheque).length() > 10) {
                //        cheque = Integer.parseInt(String.valueOf(cheque).substring(0, 10));
                //    }
                //} else {
                cheque = 0;
                //}

                if ((rst.getString("CTRDATEMI") != null)
                        && (!rst.getString("CTRDATEMI").trim().isEmpty())) {

                    dataemissao = rst.getString("CTRDATEMI").trim().substring(0, 10)/*.replace("-", "/")*/;
                } else {
                    dataemissao = "2016/05/20";
                }

                if ((rst.getString("CTRDATVNC") != null)
                        && (!rst.getString("CTRDATVNC").trim().isEmpty())) {

                    datavencimento = rst.getString("CTRDATVNC").trim().substring(0, 10)/*.replace("-", "/")*/;
                } else {
                    datavencimento = "2016/06/20";
                }

                if ((rst.getString("CLIDES") != null)
                        && (!rst.getString("CLIDES").isEmpty())) {
                    nome = util.acertarTexto(rst.getString("CLIDES").replace("'", "").trim());
                } else {
                    nome = "";
                }

                if ((rst.getString("CLIRGCGF") != null)
                        && (!rst.getString("CLIRGCGF").isEmpty())) {
                    rg = util.acertarTexto(rst.getString("CLIRGCGF").trim().replace("'", ""));

                    if (rg.length() > 20) {
                        rg = rg.substring(0, 20);
                    }
                } else {
                    rg = "";
                }

                valor = Double.parseDouble(rst.getString("CTRVLRDEV"));

                if ((rst.getString("CTRNUM") != null)
                        && (!rst.getString("CTRNUM").trim().isEmpty())) {

                    if (rst.getString("CTRNUM").trim().length() > 9) {
                        numerocupom = Integer.parseInt(util.formataNumero(rst.getString("CTRNUM").substring(0, 9)));
                    } else {
                        numerocupom = Integer.parseInt(util.formataNumero(rst.getString("CTRNUM").trim()));
                    }
                } else {
                    numerocupom = 0;
                }

                juros = 0;

                if ((rst.getString("CTROBS") != null)
                        && (!rst.getString("CTROBS").isEmpty())) {
                    observacao = util.acertarTexto(rst.getString("CTROBS").replace("'", "").trim());
                } else {
                    observacao = "IMPORTADO VR";
                }

                if ((rst.getString("CLITEL") != null)
                        && (!rst.getString("CLITEL").isEmpty())
                        && (!"0".equals(rst.getString("CLITEL").trim()))) {
                    telefone = util.formataNumero(rst.getString("CLITEL"));
                } else {
                    telefone = "";
                }

                //if (rst.getInt("status")==1){
                id_tipoalinea = 0;
                //} else if (rst.getInt("status")==2){
                //    id_tipoalinea = 15;                    
                //} else {
                //    id_tipoalinea = 0;
                //}

                if ((rst.getString("CXANUM") != null)
                        && (!rst.getString("CXANUM").trim().isEmpty())) {
                    ecf = Integer.parseInt(Utils.formataNumero(rst.getString("CXANUM").trim()));
                } else {
                    ecf = 0;
                }

                ReceberChequeVO oReceberCheque = new ReceberChequeVO();
                oReceberCheque.id_loja = id_loja;
                oReceberCheque.ecf = ecf;
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

    public List<ReceberCreditoRotativoVO> carregarReceberCreditoRotativoDirector(int id_loja, int id_lojaCliente) throws Exception {

        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        List<ReceberCreditoRotativoVO> vReceberCreditoRotativo = new ArrayList<>();

        int id_cliente, numerocupom, ecf;
        double valor, juros;
        String observacao, dataemissao, datavencimento;

        try {

            stm = ConexaoSqlServer.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("select DFid_titulo_receber, DFcod_empresa, DFcod_cliente, DFnumero_titulo, ");
            sql.append("DFdata_emissao, DFdata_vencimento, DFvalor, DFobservacao ");
            sql.append("from TBtitulo_receber ");
            sql.append("where DFid_titulo_receber not in (select DFid_titulo_receber ");
            sql.append("from TBtitulo_baixado_receber) ");
            sql.append("and DFcod_empresa = " + id_lojaCliente);

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                ReceberCreditoRotativoVO oReceberCreditoRotativo = new ReceberCreditoRotativoVO();

                id_cliente = rst.getInt("DFcod_cliente");
                dataemissao = rst.getString("DFdata_emissao").substring(0, 10).trim();
                datavencimento = rst.getString("DFdata_vencimento").substring(0, 10).trim();

                if ((rst.getString("DFid_titulo_receber") != null)
                        && (!rst.getString("DFid_titulo_receber").trim().isEmpty())) {

                    numerocupom = Integer.parseInt(util.formataNumero(rst.getString("DFid_titulo_receber").trim()));
                } else {
                    numerocupom = 0;
                }

                valor = Double.parseDouble(rst.getString("DFvalor"));
                juros = 0;

                ecf = 0;

                if ((rst.getString("DFobservacao") != null)
                        && (!rst.getString("DFobservacao").isEmpty())) {
                    observacao = Utils.acertarTexto(rst.getString("DFobservacao").replace("'", "").trim());

                    if ((rst.getString("DFnumero_titulo") != null)
                            && (!rst.getString("DFnumero_titulo").trim().isEmpty())) {

                        observacao = observacao + " => NUMERO DO TITULO: " + Utils.acertarTexto(rst.getString("DFnumero_titulo").trim().replace("'", ""));
                    }
                } else {
                    observacao = "IMPORTADO VR";
                    if ((rst.getString("DFnumero_titulo") != null)
                            && (!rst.getString("DFnumero_titulo").trim().isEmpty())) {

                        observacao = observacao + " => NUMERO DO TITULO: " + Utils.acertarTexto(rst.getString("DFnumero_titulo").trim().replace("'", ""));
                    }

                }

                oReceberCreditoRotativo.id_clientepreferencial = id_cliente;
                oReceberCreditoRotativo.id_loja = id_loja;
                oReceberCreditoRotativo.dataemissao = dataemissao;
                oReceberCreditoRotativo.numerocupom = numerocupom;
                oReceberCreditoRotativo.valor = valor;
                oReceberCreditoRotativo.ecf = ecf;
                oReceberCreditoRotativo.observacao = observacao;
                oReceberCreditoRotativo.id_clientepreferencial = id_cliente;
                oReceberCreditoRotativo.datavencimento = datavencimento;
                oReceberCreditoRotativo.valorjuros = juros;

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
        double idProduto = 0;
        long codigobarras = -1;
        Utils util = new Utils();

        try {

            stmPostgres = Conexao.createStatement();

            sql = new StringBuilder();
            sql.append("select id, id_tipoembalagem ");
            sql.append(" from produto p ");
            sql.append(" where not exists(select pa.id from produtoautomacao pa where pa.id_produto = p.id) ");

            rst = stmPostgres.executeQuery(sql.toString());

            while (rst.next()) {

                idProduto = Double.parseDouble(rst.getString("id"));

                if ((rst.getInt("id_tipoembalagem") == 4) || (idProduto <= 9999)) {
                    codigobarras = util.gerarEan13((int) idProduto, false);
                } else {
                    codigobarras = util.gerarEan13((int) idProduto, true);
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
    
    public List<AgendaTelefoneVO> carregarAgendaClientePreferencial(int idLoja) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        List<AgendaTelefoneVO> vAgendaTelefone = new ArrayList<>();        
        String telefone = "", email = "", contato = "", 
               fax = "", celular = "", nome = "";
        double idCliente = 0;        

        try {

            stm = ConexaoSqlServer.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("select DFcod_cliente, DFtelefone, DFfax, DFcontato, DFe_mail, ");
            sql.append("DFtelefone_celular ");
            sql.append("from TBcontato_cliente ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                idCliente = Long.parseLong(rst.getString("DFcod_cliente").trim());

                if ((rst.getString("DFtelefone") != null)
                        && (!rst.getString("DFtelefone").trim().isEmpty())) {
                    telefone = Utils.formataNumero(rst.getString("DFtelefone").trim());
                } else {
                    telefone = "";
                }

                if ((rst.getString("DFcontato") != null)
                        && (!rst.getString("DFcontato").trim().isEmpty())) {
                    contato = Utils.acertarTexto(rst.getString("DFcontato").trim().replace("'", ""));
                } else {
                    contato = "CONTATO";
                }

                if ((rst.getString("DFe_mail") != null)
                        && (!rst.getString("DFe_mail").trim().isEmpty())
                        && (rst.getString("DFe_mail").contains("@"))) {
                    email = Utils.acertarTexto(rst.getString("DFe_mail").trim().replace("'", ""));
                    email = email.toLowerCase();
                } else {
                    email = "";
                }

                if ((rst.getString("DFtelefone_celular") != null)
                        && (!rst.getString("DFtelefone_celular").trim().isEmpty())) {
                    celular = Utils.formataNumero(rst.getString("DFtelefone_celular").trim());
                } else {
                    celular = "";
                }

                if ((rst.getString("DFfax") != null)
                        && (!rst.getString("DFfax").trim().isEmpty())) {
                    fax = Utils.formataNumero(rst.getString("DFfax").trim());
                } else {
                    fax = "";
                }

                if (!telefone.trim().isEmpty()) {
                    contato = telefone;
                } else if (!fax.trim().isEmpty()) {
                    contato = fax;
                } else if (!celular.trim().isEmpty()) {
                    contato = celular;
                }
                
                contato = contato.trim();
                
                if (contato.length() > 14) {
                    contato = contato.substring(0, 14);
                }
                
                if (!contato.trim().isEmpty()) {
                    AgendaTelefoneVO oAgendaCliente = new AgendaTelefoneVO();
                    oAgendaCliente.setIdCliente(idCliente);
                    oAgendaCliente.setId_loja(idLoja);
                    oAgendaCliente.setNome(nome);
                    oAgendaCliente.setTelefone(contato);
                    oAgendaCliente.setEmpresa(fax + " " + celular);
                    oAgendaCliente.setEmail(email);
                    oAgendaCliente.setId_tipotelefone(4);
                    vAgendaTelefone.add(oAgendaCliente);
                }
            }

            return vAgendaTelefone;
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    private List<AgendaTelefoneVO> carregarAgendaTelefoneFornecedor(int id_loja, int id_lojaCliente) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        List<AgendaTelefoneVO> vAgendaTelefone = new ArrayList<>();
        double idFornecedor;
        String nome = "", email = "", 
                telefone = "", celular = "", fax = "", contato = "";
        
        try {
            stm = ConexaoSqlServer.getConexao().createStatement();
            sql = new StringBuilder();
            sql.append("select c.DFcod_fornecedor, f.DFnome, c.DFtelefone, c.DFfax,  ");
            sql.append("       c.DFcontato, c.DFe_mail, c.DFtelefone_celular ");
            sql.append("  from TBcontato_fornecedor c ");
            sql.append(" inner join TBfornecedor f on f.DFcod_fornecedor = c.DFcod_fornecedor ");
            
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                idFornecedor = Double.parseDouble(rst.getString("DFcod_fornecedor").trim());
                nome = Utils.acertarTexto(rst.getString("DFnome").trim().replace("'", ""));
                
                if ((rst.getString("DFtelefone") != null) &&
                        (!rst.getString("DFtelefone").trim().isEmpty()) &&
                        (!"00000000000".equals(rst.getString("DFtelefone").trim()))) {
                    telefone = Utils.formataNumero(rst.getString("DFtelefone").trim());
                } else {
                    telefone = "";
                }
                
                if ((rst.getString("DFfax") != null) &&
                        (!rst.getString("DFfax").trim().isEmpty()) &&
                        (!"00000000000".equals(rst.getString("DFfax").trim()))) {
                    fax = Utils.formataNumero(rst.getString("DFfax").trim());
                } else {
                    fax = "";
                }
                
                if ((rst.getString("DFtelefone_celular") != null) &&
                        (!rst.getString("DFtelefone_celular").trim().isEmpty()) &&
                        (!"00000000000".equals(rst.getString("DFtelefone_celular").trim()))) {
                    celular = Utils.formataNumero(rst.getString("DFtelefone_celular").trim());
                } else {
                    celular = "";
                }
                
                if ((rst.getString("DFe_mail") != null) &&
                        (!rst.getString("DFe_mail").trim().isEmpty()) &&
                        (rst.getString("DFe_mail").contains("@"))) {
                    email = Utils.acertarTexto(rst.getString("DFe_mail").trim().replace("'", ""));
                    email = email.toLowerCase();
                } else {
                    email = "";
                }
                
                if (!telefone.trim().isEmpty()) {
                    contato = telefone;
                } else if (!fax.trim().isEmpty()) {
                    contato = fax;
                } else if (!celular.trim().isEmpty()) {
                    contato = celular;
                }
                
                contato = contato.trim();
                
                if (contato.length() > 14) {
                    contato = contato.substring(0, 14);
                }
                
                if (!contato.trim().isEmpty()) {
                    AgendaTelefoneVO oAgendaFornecedor = new AgendaTelefoneVO();
                    oAgendaFornecedor.setIdFornecedor(idFornecedor);
                    oAgendaFornecedor.setId_loja(id_loja);
                    oAgendaFornecedor.setNome(nome);
                    oAgendaFornecedor.setTelefone(contato);
                    oAgendaFornecedor.setEmpresa(fax + " " + celular);
                    oAgendaFornecedor.setEmail(email);
                    oAgendaFornecedor.setId_tipotelefone(5);
                    vAgendaTelefone.add(oAgendaFornecedor);
                }
            }
            
            stm.close();
            return vAgendaTelefone;
        } catch(Exception ex) {
            throw ex;
        }
    }

    //IMPORTAÇÕES
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
            List<FamiliaProdutoVO> vFamiliaProduto = carregarFamiliaProdutoDirector();

            new FamiliaProdutoDAO().salvar(vFamiliaProduto);
        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarMercadologico() throws Exception {

        List<MercadologicoVO> vMercadologico = new ArrayList<>();

        try {

            ProgressBar.setStatus("Carregando dados...Mercadologico...");
            vMercadologico = carregarMercadologicoSuperServer(1);
            new MercadologicoDAO().salvar(vMercadologico, true);

            vMercadologico = carregarMercadologicoSuperServer(2);
            new MercadologicoDAO().salvar(vMercadologico, false);

            vMercadologico = carregarMercadologicoSuperServer(3);
            new MercadologicoDAO().salvar(vMercadologico, false);

            new MercadologicoDAO().salvarMax();

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarProduto(int id_loja) throws Exception {

        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        try {

            ProgressBar.setStatus("Carregando dados...Produtos...");
            Map<Integer, ProdutoVO> vProdutoSysPdv = carregarProdutoDirector();

            List<LojaVO> vLoja = new LojaDAO().carregar();

            ProgressBar.setMaximum(vProdutoSysPdv.size());

            for (Integer keyId : vProdutoSysPdv.keySet()) {

                ProdutoVO oProduto = vProdutoSysPdv.get(keyId);

                oProduto.idProdutoVasilhame = -1;
                oProduto.excecao = -1;
                oProduto.idTipoMercadoria = -1;

                vProdutoNovo.add(oProduto);

                ProgressBar.next();
            }

            produto.implantacaoExterna = true;
            produto.salvar(vProdutoNovo, id_loja, vLoja);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarProdutoSemBarras(int id_loja) throws Exception {

        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        try {

            ProgressBar.setStatus("Carregando dados...Produtos sem código barras...");
            Map<Integer, ProdutoVO> vProduto = carregarProdutoDirectorSemBarras();

            List<LojaVO> vLoja = new LojaDAO().carregar();

            ProgressBar.setMaximum(vProduto.size());

            for (Integer keyId : vProduto.keySet()) {

                ProdutoVO oProduto = vProduto.get(keyId);

                oProduto.idProdutoVasilhame = -1;
                oProduto.excecao = -1;
                oProduto.idTipoMercadoria = -1;

                vProdutoNovo.add(oProduto);

                ProgressBar.next();
            }

            produto.implantacaoExterna = true;
            produto.salvar(vProdutoNovo, id_loja, vLoja);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarPrecoProduto(int id_loja, int id_lojaCliente) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        try {

            ProgressBar.setStatus("Carregando dados...Produtos...Preço...");
            Map<Double, ProdutoVO> vPrecoProduto = carregarPrecoProdutoDirector(id_loja, id_lojaCliente);

            List<LojaVO> vLoja = new LojaDAO().carregar();

            ProgressBar.setMaximum(vPrecoProduto.size());

            for (Double keyId : vPrecoProduto.keySet()) {

                ProdutoVO oProduto = vPrecoProduto.get(keyId);

                vProdutoNovo.add(oProduto);

                ProgressBar.next();
            }

            produto.alterarPrecoProduto(vProdutoNovo, id_loja);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarMargemProduto(int id_loja, int id_lojaCliente) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        try {

            ProgressBar.setStatus("Carregando dados...Produtos...Margem...");
            Map<Double, ProdutoVO> vPrecoProduto = carregarMargemProdutoDirector(id_loja, id_lojaCliente);

            List<LojaVO> vLoja = new LojaDAO().carregar();

            ProgressBar.setMaximum(vPrecoProduto.size());

            for (Double keyId : vPrecoProduto.keySet()) {

                ProdutoVO oProduto = vPrecoProduto.get(keyId);

                vProdutoNovo.add(oProduto);

                ProgressBar.next();
            }

            produto.alterarMargemProduto(vProdutoNovo, id_loja);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarCustoProduto(int id_loja, int id_lojaCliente) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        try {

            ProgressBar.setStatus("Carregando dados...Produtos...Custo...");
            Map<Double, ProdutoVO> vCustoProduto = carregarCustoProdutoDirector(id_loja, id_lojaCliente);

            List<LojaVO> vLoja = new LojaDAO().carregar();

            ProgressBar.setMaximum(vCustoProduto.size());

            for (Double keyId : vCustoProduto.keySet()) {

                ProdutoVO oProduto = vCustoProduto.get(keyId);

                vProdutoNovo.add(oProduto);

                ProgressBar.next();
            }

            produto.alterarCustoProdutoRapido(vProdutoNovo, id_loja);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarEstoqueProduto(int id_loja, int id_lojaCliente) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        try {

            ProgressBar.setStatus("Carregando dados...Produtos...Estoque...");
            Map<Double, ProdutoVO> vEstoqueProduto = carregarEstoqueProdutoDirector(id_loja, id_lojaCliente);

            ProgressBar.setMaximum(vEstoqueProduto.size());

            for (Double keyId : vEstoqueProduto.keySet()) {

                ProdutoVO oProduto = vEstoqueProduto.get(keyId);

                vProdutoNovo.add(oProduto);

                ProgressBar.next();
            }

            produto.alterarEstoqueProduto(vProdutoNovo, id_loja);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarCodigoBarra() throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        try {

            ProgressBar.setStatus("Carregando dados...Produtos...Codigo Barras...");
            Map<Long, ProdutoVO> vCodigoBarra = carregarCodigoBarrasDirector();

            ProgressBar.setMaximum(vCodigoBarra.size());

            for (Long keyId : vCodigoBarra.keySet()) {

                ProdutoVO oProduto = vCodigoBarra.get(keyId);

                vProdutoNovo.add(oProduto);

                ProgressBar.next();
            }

            produto.addCodigoBarras(vProdutoNovo);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarDataCadastroProduto() throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        try {

            ProgressBar.setStatus("Carregando dados...Data Cadastro Produto...");
            vProduto = carregarDataCadastroProdutoDirector();

            if (!vProduto.isEmpty()) {
                new ProdutoDAO().altertarDataCadastroProdutoGdoor(vProduto);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void importarIcmsProduto() throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Icms Produtos...");
            vProduto = carregarIcmsProdutoDirector();

            if (!vProduto.isEmpty()) {
                new ProdutoDAO().alterarICMSProduto(vProduto);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void importarFornecedor() throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Fornecedor...");
            List<FornecedorVO> vFornecedor = carregarFornecedorDirector();

            new FornecedorDAO().salvar(vFornecedor);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarEnderecoFornecedor() throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Endereço Fornecedor...");
            List<FornecedorVO> vFornecedor = carregarFornecedorDirector();

            new FornecedorDAO().acertarEndereco(vFornecedor);

        } catch (Exception ex) {

            throw ex;
        }
    }
    
    public void importarFornecedorContato() throws Exception {
        try {

            ProgressBar.setStatus("Carregando dados...Contatos Fornecedores...");

            List<FornecedorContatoVO> v_fornecedorContato = carregarFornecedorContatoDirector();

            if (!v_fornecedorContato.isEmpty()) {
                new FornecedorContatoDAO().salvar(v_fornecedorContato);
            }

        } catch (Exception ex) {
            throw ex;
        }
    }

    public void importarProdutoFornecedor() throws Exception {
        try {

            ProgressBar.setStatus("Carregando dados...Produto Fornecedor...");
            List<ProdutoFornecedorVO> vProdutoFornecedor = carregarProdutoFornecedorDirector();

            new ProdutoFornecedorDAO().salvar(vProdutoFornecedor);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarClientePreferencial(int idLoja, int idLojaCliente) throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Clientes...");
            List<ClientePreferencialVO> vClientePreferencial = carregarClienteDirector(idLoja, idLojaCliente);
            new PlanoDAO().salvar(idLoja);
            new ClientePreferencialDAO().salvar(vClientePreferencial, idLoja, idLojaCliente);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarEndereClientePreferencial(int idLoja, int idLojaCliente) throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Endereco Clientes...");
            List<ClientePreferencialVO> vClientePreferencial = carregarClienteDirector(idLoja, idLojaCliente);
            new PlanoDAO().salvar(idLoja);
            new ClientePreferencialDAO().acertarEndereco(vClientePreferencial);

        } catch (Exception ex) {

            throw ex;
        }
    }
    
    public void importarClientePreferencialContato() throws Exception {
        try {

            ProgressBar.setStatus("Carregando dados...Contatos Cliente Preferencial...");

            List<ClientePreferencialContatoVO> v_clientePreferencialContato = carregarClientePreferencialContatoDirector();

            if (!v_clientePreferencialContato.isEmpty()) {
                new ClientePreferencialContatoDAO().salvar(v_clientePreferencialContato);
            }

        } catch (Exception ex) {
            throw ex;
        }
    }

    public void importarReceberCreditoRotativo(int idLoja, int idLojaCliente) throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Receber Cliente...");
            List<ReceberCreditoRotativoVO> vReceberCliente = carregarReceberCreditoRotativoDirector(idLoja, idLojaCliente);

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

    public void importarCestProduto() throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();

        try {
            ProgressBar.setStatus("Carregando dados...Código CEST...");
            vProduto = carregarCestProdutoDirector();

            if (!vProduto.isEmpty()) {
                new ProdutoDAO().alterarCestProduto(vProduto);
            }

        } catch (Exception ex) {
            throw ex;
        }
    }

    public void importarSituacaoCadastroFornecedor() throws Exception {
        List<FornecedorVO> vFornecedor = new ArrayList<>();
        try {
            vFornecedor = carregarSituacaoCadastroFornecedor();
            
            if (!vFornecedor.isEmpty()) {
                new FornecedorDAO().alterarSitucaoCadastro(vFornecedor);
            }
        } catch(Exception ex) {
            throw ex;
        }
    }
    
    public void importarOferta(int id_loja, int id_lojaCliente) throws Exception {
        List<OfertaVO> vOferta = new ArrayList<>();
        try {
            vOferta = carregarOferta(id_loja, id_lojaCliente);
            
            if (!vOferta.isEmpty()) {
                new OfertaDAO().salvar(vOferta, id_loja);
            }
        } catch(Exception ex) {
            throw ex;
        }        
    }
    
    public void importarAgendaFornecedor(int id_loja, int id_lojaCliente) throws Exception {
        List<AgendaTelefoneVO> vAgendaFornecedor = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Agenda Fornecedor...");
            vAgendaFornecedor = carregarAgendaTelefoneFornecedor(id_loja, id_lojaCliente);
            
            if (!vAgendaFornecedor.isEmpty()) {
                new AgendaTelefoneDAO().salvar(vAgendaFornecedor, id_lojaCliente);
            }
        } catch(Exception ex) {
            throw ex;
        }
    }
    
    public void importarAgendaCliente(int id_loja, int id_lojaCliente) throws Exception {
        List<AgendaTelefoneVO> vAgendaCliente = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Agenda Cliente...");
            vAgendaCliente = carregarAgendaClientePreferencial(id_loja);
            
            if (!vAgendaCliente.isEmpty()) {
                new AgendaTelefoneDAO().salvar(vAgendaCliente, id_lojaCliente);
            }
        } catch(Exception ex) {
            throw ex;
        }
    }
    
    // métodos Director

    private int retornarIcmsDirector(Integer cstTrib, double valor, double reducao) {
        int retorno = 8;

        if (cstTrib == 0) {
            if (valor == 7.0) {
                retorno = 0;
            } else if (valor == 12.0) {
                retorno = 1;
            } else if (valor == 18.0) {
                retorno = 2;
            } else if (valor == 25.0) {
                retorno = 3;
            } else if (valor == 20.0) {
                retorno = 23;
            } else if (valor == 8.0) {
                retorno = 24;
            } else if (valor == 13.0) {
                retorno = 25;
            } else if (valor == 19.0) {
                retorno = 26;
            } else if (valor == 26.0) {
                retorno = 27;
            } else if (valor == 31.0) {
                retorno = 28;
            } else if (valor == 35.0) {
                retorno = 29;
            } else if (valor == 36.0) {
                retorno = 30;
            } else if (valor == 37.0) {
                retorno = 31;
            } else if (valor == 9.0) {
                retorno = 32;
            } else if (valor == 14.0) {
                retorno = 33;
            } else if (valor == 27.0) {
                retorno = 34;
            } else if (valor == 4.0) {
                retorno = 36;
            }
        } else if (cstTrib == 20) {
            if (reducao == 0.0) {
                if (valor == 7.0) {
                    retorno = 0;
                } else if (valor == 12.0) {
                    retorno = 1;
                } else if (valor == 18.0) {
                    retorno = 2;
                } else if (valor == 25.0) {
                    retorno = 3;
                } else if (valor == 20.0) {
                    retorno = 23;
                } else if (valor == 8.0) {
                    retorno = 24;
                } else if (valor == 13.0) {
                    retorno = 25;
                } else if (valor == 19.0) {
                    retorno = 26;
                } else if (valor == 26.0) {
                    retorno = 27;
                } else if (valor == 31.0) {
                    retorno = 28;
                } else if (valor == 35.0) {
                    retorno = 29;
                } else if (valor == 36.0) {
                    retorno = 30;
                } else if (valor == 37.0) {
                    retorno = 31;
                } else if (valor == 9.0) {
                    retorno = 32;
                } else if (valor == 14.0) {
                    retorno = 33;
                } else if (valor == 27.0) {
                    retorno = 34;
                } else if (valor == 4.0) {
                    retorno = 36;
                }
            } else {
                if ((valor == 18.0) && (reducao == 61.11)) {
                    retorno = 4;
                } else if ((valor == 12.0) && (reducao == 41.67)) {
                    retorno = 5;
                } else if ((valor == 18.0) && (reducao == 33.33)) {
                    retorno = 9;
                } else if ((valor == 25.0) && (reducao == 52.00)) {
                    retorno = 10;
                } else if ((valor == 12.0) && (reducao == 10.49)) {
                    retorno = 11;
                } else if ((valor == 25.0) && (reducao == 10.49)) {
                    retorno = 12;
                } else if ((valor == 20.0) && (reducao == 55.0)) {
                    retorno = 38;
                } else if ((valor == 20.0) && (reducao == 30.0)) {
                    retorno = 39;
                } else if ((valor == 18.0) && (reducao == 61.12)) {
                    retorno = 40;
                } else if ((valor == 20.0) && (reducao == 100.0)) {
                    retorno = 41;
                } else if ((valor == 7.0) && (reducao == 100.0)) {
                    retorno = 42;
                } else if ((valor == 19.0) && (reducao == 63.15)) {
                    retorno = 43;
                } else if ((valor == 12.0) && (reducao == 35.83)) {
                    retorno = 44;
                } else if ((valor == 12.0) && (reducao == 64.16)) {
                    retorno = 45;
                }
            }
        } else if (cstTrib == 40) {
            retorno = 6;
        } else if (cstTrib == 41) {
            retorno = 17;
        } else if (cstTrib == 50) {
            retorno = 13;
        } else if (cstTrib == 51) {
            retorno = 16;
        } else if (cstTrib == 60) {
            retorno = 7;
        } else if (cstTrib == 90) {
            retorno = 8;
        }

        return retorno;
    }
}
