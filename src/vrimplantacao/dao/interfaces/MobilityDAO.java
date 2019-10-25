package vrimplantacao.dao.interfaces;

import java.sql.Date;
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
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao.dao.cadastro.ClientePreferencialDAO;
import vrimplantacao.dao.cadastro.FamiliaProdutoDAO;
import vrimplantacao.dao.cadastro.FornecedorDAO;
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

@Deprecated
public class MobilityDAO {
    
    //CARREGAMENTOS
    public List<FamiliaProdutoVO> carregarFamiliaProdutoMobility() throws Exception {

        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        List<FamiliaProdutoVO> vFamiliaProduto = new ArrayList<>();

        try {

            stm = ConexaoFirebird.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append(" SELECT ID,         ");
            sql.append("        S_DESCRICAO ");
            sql.append(" FROM FAMILIAS      ");          
            sql.append(" ORDER BY ID        ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                FamiliaProdutoVO oFamiliaProduto = new FamiliaProdutoVO();

                oFamiliaProduto.id = Integer.parseInt(rst.getString("ID"));
                oFamiliaProduto.descricao = util.acertarTexto(rst.getString("S_DESCRICAO").replace("'", "").trim());
                oFamiliaProduto.id_situacaocadastro = 1;
                oFamiliaProduto.codigoant = 0;

                vFamiliaProduto.add(oFamiliaProduto);
            }

            return vFamiliaProduto;

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }        
    
    public List<MercadologicoVO> carregarMercadologicoMobility(int nivel) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        List<MercadologicoVO> vMercadologico = new ArrayList<>();
        String descricao = "";
        int mercadologico1, mercadologico2, mercadologico3;

        try {

            stm = ConexaoFirebird.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append(" SELECT DISTINCT P.GRUPO, M1.S_DESCRICAO AS MERC1, ");
            sql.append("        P.DEPARTAMENTO, M2.S_DESCRICAO AS MERC2,   ");
            sql.append("        P.SESSAO, M3.S_DESCRICAO AS MERC3          ");                                  
            sql.append("   FROM PRODUTOS P                        ");  
            sql.append("  INNER JOIN GRUPOS M1 ON M1.ID = P.GRUPO ");
            sql.append("  INNER JOIN DEPARTAMENTOS M2 ON M2.ID = P.DEPARTAMENTO ");
            sql.append("  INNER JOIN SESSOES M3 ON M3.ID = P.SESSAO             ");
            sql.append("  ORDER BY P.GRUPO,  P.DEPARTAMENTO, P.SESSAO;          ");  

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                mercadologico1 = 0;
                mercadologico2 = 0;
                mercadologico3 = 0;
                
                MercadologicoVO oMercadologico = new MercadologicoVO();

                if (nivel == 1) {
                    mercadologico1 = Integer.parseInt(rst.getString("GRUPO"));
                    descricao = util.acertarTexto(rst.getString("MERC1").replace("'", "").trim());
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

                } else if (nivel == 2)  {
                    
                    mercadologico1 = Integer.parseInt(rst.getString("GRUPO"));
                    mercadologico2 = Integer.parseInt(rst.getString("DEPARTAMENTO"));
                    descricao = util.acertarTexto(rst.getString("MERC2").replace("'", "").trim());
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
                } else if (nivel == 3){
                    mercadologico1 = Integer.parseInt(rst.getString("GRUPO"));
                    mercadologico2 = Integer.parseInt(rst.getString("DEPARTAMENTO"));
                    mercadologico3 = Integer.parseInt(rst.getString("SESSAO"));
                    descricao = util.acertarTexto(rst.getString("MERC3").replace("'", "").trim());
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

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }        
    
    public Map<Integer, ProdutoVO> carregarProdutoMobility() throws Exception {
        Map<Integer, ProdutoVO> vProduto = new HashMap<>();
        StringBuilder sql = null;
        Statement stm = null, stmPostgres = null;
        ResultSet rst = null, rstPostgres = null;
        Utils util = new Utils();
        int idProduto, idTipoEmbalagem = 0, qtdEmbalagem, idTipoPisCofins, idTipoPisCofinsCredito, tipoNaturezaReceita,
               idAliquota, idFamilia, mercadologico1, mercadologico2, mercadologico3, idSituacaoCadastro, 
               ncm1, ncm2, ncm3, codigoBalanca, referencia = -1, validade=0;
        String descriaoCompleta, descricaoReduzida, descricaoGondola, ncmAtual, strCodigoBarras;
        boolean eBalanca, pesavel;
        long codigoBarras = 0;
        double margem, precoVenda, custo;
        try {
            
            Conexao.begin();
            
            stmPostgres = Conexao.createStatement();
            
            stm = ConexaoFirebird.getConexao().createStatement();
            
            sql = new StringBuilder();
            sql.append("SELECT P.CODIGO_INTERNO AS ID, ");
            sql.append("       COALESCE((SELECT FIRST 1 C.S_CODIGO FROM COD_AUXILIARES C WHERE C.ID_PRODUTO = P.ID),P.CODIGO_BARRAS) AS CODIGO_BARRAS, ");
            sql.append("       P.ATIVO, P.DESCRICAO, P.DESCRICAO_RESUMIDA, P.GRUPO, ");
            sql.append("       P.DEPARTAMENTO, P.SESSAO, ");
            sql.append("       (SELECT FIRST 1 F.ID FROM FAMILIAS F WHERE F.ID = P.FAMILIA) AS FAMILIA, ");
            sql.append("       P.EMBALAGEM, "); 
            sql.append("       P.ALIQUOTA, P.MARGEM, P.PRECO_CUSTO, P.PRECO_VENDA1, ");
            sql.append("       P.ESTOQUE_MAX, P.ESTOQUE_MIN, P.ESTOQUE_ATUAL,P.DATA_INCLUSAO, ");
            sql.append("       P.S_NCM, P.S_CST_PIS, P.S_CST_COFINS, P.UNIDADE, ");
            sql.append("       P.S_COD_CST_PIS_SAIDA,  P.S_COD_CST_PIS_ENTRADA, P.unidades_caixa, P.preco_compra ");
            sql.append("FROM PRODUTOS P ");
            sql.append("ORDER BY P.CODIGO_INTERNO ");            
            
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                ProdutoVO oProduto = new ProdutoVO();
                
                if ("0".equals(rst.getString("ATIVO").trim())) {
                    idSituacaoCadastro = 0;
                } else {
                    idSituacaoCadastro = 1;
                }
                
                eBalanca = false;
                codigoBalanca = -1;
                pesavel = false;
                idTipoEmbalagem = 0;
                
                sql = new StringBuilder();
                sql.append("select codigo, descricao, pesavel, validade ");
                sql.append("from implantacao.produtobalanca ");
                sql.append("where codigo = " + rst.getString("CODIGO_BARRAS").replace(".", ""));

                rstPostgres = stmPostgres.executeQuery(sql.toString());

                if (rstPostgres.next()) {
                    eBalanca = true;                         
                    idProduto = Integer.parseInt(rst.getString("ID").trim().replace(".", ""));               
                    codigoBalanca = rstPostgres.getInt("codigo");
                    validade = rstPostgres.getInt("validade"); 
                    if ("CX".equals(rst.getString("UNIDADE").trim())) {
                        pesavel = false;
                        idTipoEmbalagem = 1;
                    } else if ("KG".equals(rst.getString("UNIDADE").trim())) {
                        pesavel = false;
                        idTipoEmbalagem = 4;
                    } else if ("UN".equals(rst.getString("UNIDADE").trim())) {
                        pesavel = true;
                        idTipoEmbalagem = 0;
                    } else {
                        pesavel = false;
                        idTipoEmbalagem = 4;
                    }
                } else {
                    eBalanca = false;
                    pesavel = false;
                    idProduto = Integer.parseInt(rst.getString("ID").trim().replace(".", ""));
                    validade = 0;                     
                    if ("CX".equals(rst.getString("UNIDADE").trim())) {
                        idTipoEmbalagem = 1;
                    } else if ("KG".equals(rst.getString("UNIDADE").trim())) {
                        idTipoEmbalagem = 4;
                    } else if ("UN".equals(rst.getString("UNIDADE").trim())) {
                        idTipoEmbalagem = 0;
                    } else {
                        idTipoEmbalagem = 0;
                    }
                }
                
                if ((rst.getString("DESCRICAO") != null) &&
                        (!rst.getString("DESCRICAO").trim().isEmpty())) {
                    byte[] bytes = rst.getBytes("DESCRICAO");
                    String textoAcertado = new String(bytes, "ISO-8859-1");                                     
                    descriaoCompleta = util.acertarTexto(textoAcertado.replace("'", "").trim());                     
                } else {
                    descriaoCompleta = "";
                }
                
                if ((rst.getString("DESCRICAO_RESUMIDA") != null) &&
                        (!rst.getString("DESCRICAO_RESUMIDA").trim().isEmpty())) {
                    byte[] bytes = rst.getBytes("DESCRICAO_RESUMIDA");
                    String textoAcertado = new String(bytes, "ISO-8859-1");                                     
                    descricaoReduzida = util.acertarTexto(textoAcertado.replace("'", "").trim());                     
                } else {
                    descricaoReduzida = "";
                }
                
                descricaoGondola = descricaoReduzida;

                if (idTipoEmbalagem == 4) {
                    qtdEmbalagem = 1;
                } else {
                    qtdEmbalagem = (int) Double.parseDouble(rst.getString("UNIDADES_CAIXA").replace(",", ""));
                }
                
                if ((rst.getString("FAMILIA") != null) &&
                        (!rst.getString("FAMILIA").trim().isEmpty()) &&
                        (!"0".equals(rst.getString("FAMILIA").trim()))) {
                    idFamilia = Integer.parseInt(rst.getString("FAMILIA").trim().replace(".", ""));
                } else {
                    idFamilia = -1;
                }
                
                if ((rst.getString("GRUPO") != null) &&
                        (!rst.getString("GRUPO").trim().isEmpty())) {                    
                    mercadologico1 = Integer.parseInt(rst.getString("GRUPO"));
                } else {
                    mercadologico1 = 99;
                }
                if ((rst.getString("DEPARTAMENTO") != null) &&
                        (!rst.getString("DEPARTAMENTO").trim().isEmpty())) {                    
                    mercadologico2 = Integer.parseInt(rst.getString("DEPARTAMENTO"));
                } else {
                    mercadologico2 = 999;
                }
                if ((rst.getString("SESSAO") != null) &&
                        (!rst.getString("SESSAO").trim().isEmpty())) {                    
                    mercadologico3 = Integer.parseInt(rst.getString("SESSAO"));
                } else {
                    mercadologico3 = 99;
                }
                sql = new StringBuilder();
                sql.append("SELECT MERCADOLOGICO1, MERCADOLOGICO2, MERCADOLOGICO3 "); 
                sql.append("FROM MERCADOLOGICO                                    "); 
                sql.append("WHERE MERCADOLOGICO1 = "+mercadologico1); 
                sql.append("  AND MERCADOLOGICO2 = "+mercadologico2); 
                sql.append("  AND MERCADOLOGICO3 = "+mercadologico3);                
                rstPostgres = stmPostgres.executeQuery(sql.toString());                
                if (!rstPostgres.next()) {                
                    mercadologico1=38; // UTILIDADES DO SISTEMA ANTIGO
                    mercadologico2=0; 
                    mercadologico3=0;                
                }
                
                if ((rst.getString("S_NCM")!=null) && 
                        (!rst.getString("S_NCM").trim().isEmpty())){
                    ncmAtual = util.formataNumero(rst.getString("S_NCM"));
                    if ((ncmAtual != null)
                            && (!ncmAtual.isEmpty())
                            && (ncmAtual.length() > 5)) {
                        try{
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
                    
                    if ((rst.getString("CODIGO_BARRAS") != null) &&
                            (!rst.getString("CODIGO_BARRAS").trim().isEmpty())) {
                        
                        strCodigoBarras = rst.getString("CODIGO_BARRAS").replace(".", "").trim();
                        
                        if (String.valueOf(Long.parseLong(strCodigoBarras)).length() < 7) {                            
                            if (idProduto >= 10000) {
                                codigoBarras = util.gerarEan13(idProduto, true);
                            } else {
                                codigoBarras = util.gerarEan13(idProduto, false);
                            }
                        } else {
                            codigoBarras = Long.parseLong(rst.getString("CODIGO_BARRAS").trim());
                        }
                    }
                }
                
                if ((rst.getString("S_COD_CST_PIS_SAIDA") != null) &&
                        (!rst.getString("S_COD_CST_PIS_SAIDA").trim().isEmpty())) {
                    idTipoPisCofins = util.retornarPisCofinsDebito(Integer.parseInt(rst.getString("S_COD_CST_PIS_SAIDA").trim()));
                } else {
                    idTipoPisCofins = 0;
                }
                
                if ((rst.getString("S_COD_CST_PIS_ENTRADA") != null) &&
                        (!rst.getString("S_COD_CST_PIS_ENTRADA").trim().isEmpty())) {
                    idTipoPisCofinsCredito = util.retornarPisCofinsCredito(Integer.parseInt(rst.getString("S_COD_CST_PIS_ENTRADA").trim()));
                } else {
                    idTipoPisCofinsCredito = 12;
                }
                
                /*if ((rst.getString("NATREC") != null) &&
                        (!rst.getString("NATREC").trim().isEmpty())) {
                    tipoNaturezaReceita = util.retornarTipoNaturezaReceita(idTipoPisCofins, 
                            rst.getString("NATREC").trim());
                } else {*/
                    tipoNaturezaReceita = util.retornarTipoNaturezaReceita(idTipoPisCofins, "");
                //}
                
                if ((rst.getString("ALIQUOTA") != null) &&
                        (!rst.getString("ALIQUOTA").trim().isEmpty())) {
                    idAliquota = retornarAliquotaICMSMobility(rst.getString("ALIQUOTA").trim().toUpperCase(),"");
                } else {
                    idAliquota = 8;
                }
                
                if ((rst.getString("margem") != null) &&
                        (!rst.getString("margem").trim().isEmpty())) {
                    margem = Double.parseDouble(rst.getString("margem").replace(",", "."));
                } else {
                    margem = 0;
                }
                if ((rst.getString("preco_venda1") != null) &&
                        (!rst.getString("preco_venda1").trim().isEmpty())) {
                    precoVenda = Double.parseDouble(rst.getString("preco_venda1").replace(",", "."));
                } else {
                    precoVenda = 0;
                }
                if ((rst.getString("preco_compra") != null) &&
                        (!rst.getString("preco_compra").trim().isEmpty())) {
                    custo = Double.parseDouble(rst.getString("preco_compra").replace(",", "."));
                } else {
                    custo = 0;
                }                
               
                
                if (descriaoCompleta.length() > 60) {

                    descriaoCompleta = descriaoCompleta.substring(0, 60);
                }

                if (descricaoReduzida.length() > 22) {

                    descricaoReduzida = descricaoReduzida.substring(0, 22);
                }

                if (descricaoGondola.length() > 60) {

                    descricaoGondola = descricaoGondola.substring(0, 60);
                }
                
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
                
                ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                
                oComplemento.idSituacaoCadastro = idSituacaoCadastro;
                oComplemento.precoVenda = precoVenda;
                oComplemento.precoDiaSeguinte = precoVenda;
                oComplemento.custoComImposto = custo;
                oComplemento.custoSemImposto = custo;                
                
                oProduto.vComplemento.add(oComplemento);
                
                ProdutoAliquotaVO oAliquota = new ProdutoAliquotaVO();
                
                oAliquota.idEstado = 35;
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
                
                oCodigoAnterior.codigoanterior = idProduto;
                oCodigoAnterior.codigoatual = idProduto;
                oCodigoAnterior.barras = codigoBarras;
                
                oCodigoAnterior.naturezareceita = tipoNaturezaReceita;
                oCodigoAnterior.piscofinsdebito = idTipoPisCofins;
                oCodigoAnterior.piscofinscredito = idTipoPisCofinsCredito;
                oCodigoAnterior.ref_icmsdebito = String.valueOf(idAliquota);
                
                oCodigoAnterior.estoque = -1;
                oCodigoAnterior.e_balanca = eBalanca;
                oCodigoAnterior.codigobalanca = codigoBalanca;
                oCodigoAnterior.custosemimposto = -1;
                oCodigoAnterior.custocomimposto = -1;
                oCodigoAnterior.margem = -1;
                oCodigoAnterior.precovenda = -1;
                oCodigoAnterior.referencia = -1;
                oCodigoAnterior.ncm = String.valueOf(ncm1);
                
                oProduto.vCodigoAnterior.add(oCodigoAnterior);
                
                vProduto.put(idProduto, oProduto);
            }
            
            stmPostgres.close();
            Conexao.commit();
            return vProduto;
            
        } catch(Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public List<ClientePreferencialVO> carregarClienteMobility(int idLoja, int idLojaCliente) throws Exception {
            StringBuilder sql = null;
            Statement stm = null;
            ResultSet rst = null;
            Utils util = new Utils();
            List<ClientePreferencialVO> vClientePreferencial = new ArrayList<>();

            String nome, endereco , bairro, telefone,telefone2, inscricaoestadual, email, enderecoEmpresa, nomeConjuge,  
                   dataResidencia,  dataCadastro, numero, dataAniversario ; 
            int id_municipio = 0, id_estado, id_sexo, id_tipoinscricao, id, agente, id_situacaocadastro, Linha=0;
            Long cnpj, cep;
            double limite;

            try {
                stm = ConexaoFirebird.getConexao().createStatement();

                sql = new StringBuilder();
                sql.append(" select id, numero, ativo, nome, endereco,         ");
                sql.append("        complemento, bairro, cidade, uf, cep,      ");
                sql.append("        tipo_pessoa, cpf, cnpj, insc_estadual, rg, ");
                sql.append("        ddd, telefone, fax, celular, site,         ");
                sql.append("        data_cadastro, data_aniversario,           ");
                sql.append("        limite_credito, email,                     ");
                sql.append("        s_codigo_municipio, end_num, comentarios   ");
                sql.append(" from clientes                                     ");
                sql.append(" order by id                                       ");
                rst = stm.executeQuery(sql.toString());
                Linha=1;
                try{
                    while (rst.next()) {                    
                        ClientePreferencialVO oClientePreferencial = new ClientePreferencialVO();

                        id = rst.getInt("ID");
                        id_situacaocadastro = 1;
                        
                        if ((rst.getString("NOME")!=  null) &&
                                (!rst.getString("NOME").isEmpty())) {
                            byte[] bytes = rst.getBytes("NOME");
                            String textoAcertado = new String(bytes, "ISO-8859-1");                                     
                            nome = util.acertarTexto(textoAcertado.replace("'", "").trim());                     
                        } else {
                            nome = "SEM NOME VR "+id;
                        }
                        if ((rst.getString("ENDERECO")!=  null) &&
                            (!rst.getString("ENDERECO").isEmpty())) {                        
                            endereco            = util.acertarTexto(rst.getString("ENDERECO").replace("'", "").trim());
                        }else{
                            endereco            = "ENDERECO VR";                            
                        }
                        if (endereco.length()>50){
                            endereco = endereco.substring(0,50);
                        }
                        
                        if (rst.getString("BAIRRO")!=null){
                            bairro              = util.acertarTexto(rst.getString("BAIRRO").replace("'", "").trim());
                        }else{
                            bairro              = "";
                        }                        
                        
                        
                        if ((rst.getString("CIDADE")!=null) && (rst.getString("UF")!=null)){
                           id_estado           = util.retornarEstadoDescricao(rst.getString("UF"));     
                           if (id_estado==0){
                               id_estado=35; // ESTADO ESTADO DO CLIENTE
                           }
                           id_municipio        = util.retornarMunicipioIBGEDescricao(rst.getString("CIDADE").toString(),rst.getString("UF").toString());
                           if(id_municipio==0){
                               id_municipio=3529005;// CIDADE DO CLIENTE;
                           }
                        } else{
                            id_estado    = 35; // ESTADO ESTADO DO CLIENTE
                            id_municipio = 3529005; // CIDADE DO CLIENTE;                   
                        }
                        if (rst.getString("CEP")!=null){
                            cep                 = Long.parseLong(util.formataNumero(util.acertarTexto(rst.getString("CEP").replace("'", ""))));
                        }else
                            cep = Long.parseLong("0");
                        
                        if (rst.getString("TELEFONE")!=null){
                            telefone           = util.formataNumero(rst.getString("TELEFONE"));  
                        }else{
                            telefone           = "";
                        }
                        if (rst.getString("CELULAR")!=null){
                            telefone2           = util.formataNumero(rst.getString("CELULAR"));  
                        }else{
                            telefone2           = "";
                        }                        
                        if (rst.getString("END_NUM")!=null){
                            numero               = util.acertarTexto(rst.getString("END_NUM"));  
                            if(numero.length()>6){
                                numero = numero.substring(0,6);
                            }
                        }else{
                            numero               = "";                        
                        }
                        
                        if (rst.getString("EMAIL")!=null){
                            email               = util.acertarTexto(rst.getString("EMAIL"));  
                            if(email.length()>50){
                                email = email.substring(0,50);
                            }
                        }else{
                            email               = "";
                        }
                        
                        id_tipoinscricao = 1;
                        if (rst.getString("TIPO_PESSOA")!=null){                            
                            if ("F".equals(rst.getString("TIPO_PESSOA"))){                            
                                if ((rst.getString("RG")!=null)&&
                                        (!rst.getString("RG").trim().isEmpty())){                                    
                                    inscricaoestadual   = util.acertarTexto(rst.getString("RG"));    
                                    if (!inscricaoestadual.trim().isEmpty()){
                                        inscricaoestadual   = inscricaoestadual.replace(".","").replace("/", "").replace(",", "");  
                                        if (inscricaoestadual.length()>18){
                                            inscricaoestadual = inscricaoestadual.substring(0,18);
                                        }
                                    }else{
                                        inscricaoestadual = "ISENTO";                                        
                                    }
                                }else{
                                    inscricaoestadual   = "ISENTO";
                                }
                                if ((rst.getString("CPF")!=null)&&
                                        (!rst.getString("CPF").trim().isEmpty())){
                                    if (String.valueOf(Long.parseLong(rst.getString("CPF").trim())).length()>=11){
                                        cnpj                = Long.parseLong(util.formataNumero(util.acertarTexto(rst.getString("CPF").trim())));     
                                    }else{
                                        cnpj                = Long.parseLong(util.formataNumero(util.acertarTexto(rst.getString("ID"))));     
                                    }
                                }else{
                                    cnpj                = Long.parseLong(util.formataNumero(util.acertarTexto(rst.getString("ID"))));     
                                }                   
                                id_tipoinscricao = 1; // PESSOA FISICA                                
                            }else{
                                if ((rst.getString("INSC_ESTADUAL")!=null)&&
                                        (!rst.getString("INSC_ESTADUAL").trim().isEmpty())){
                                    inscricaoestadual   = util.acertarTexto(rst.getString("INSC_ESTADUAL"));    
                                    if (!inscricaoestadual.trim().isEmpty()){
                                        inscricaoestadual   = inscricaoestadual.replace(".","").replace("/", "").replace(",", "");  
                                        if (inscricaoestadual.length()>18){
                                            inscricaoestadual = inscricaoestadual.substring(0,18);
                                        }
                                    }else{
                                        inscricaoestadual = "ISENTO";                                        
                                    }
                                }else{
                                    inscricaoestadual   = "ISENTO";
                                }
                                if ((rst.getString("CNPJ").trim()!=null)&&
                                        (!rst.getString("CNPJ").trim().isEmpty())){
                                    if (String.valueOf(Long.parseLong(rst.getString("CNPJ").trim())).length()>=14){
                                        cnpj                = Long.parseLong(util.formataNumero(util.acertarTexto(rst.getString("CNPJ").trim())));     
                                    }else{
                                        cnpj                = Long.parseLong(util.formataNumero(util.acertarTexto(rst.getString("ID"))));     
                                    }
                                }else{
                                    cnpj                = Long.parseLong(util.formataNumero(util.acertarTexto(rst.getString("ID"))));     
                                }    
                                id_tipoinscricao = 0; // PESSOA JURIDICA
                            }
                        }else{
                            inscricaoestadual   = "ISENTO";                            
                            cnpj                = Long.parseLong(util.formataNumero(util.acertarTexto(rst.getString("ID"))));                                 
                        }
                        
                        /*if (rst.getString("SEXO")!=null){
                            if ((rst.getInt("SEXO")==0) || (rst.getInt("SEXO")==1)){
                                id_sexo             = rst.getInt("SEXO");     
                            }else{
                                id_sexo                = 0;
                            }
                        }else{*/
                            id_sexo                = 1;
                        //}
                            
                        if ((rst.getString("DATA_CADASTRO")!=null)&&
                                (!rst.getString("DATA_CADASTRO").isEmpty())){
                            dataCadastro                = rst.getString("DATA_CADASTRO");
                        }else{
                            dataCadastro                = "";
                        }
                        if ((rst.getString("DATA_ANIVERSARIO")!=null)&&
                                (!rst.getString("DATA_ANIVERSARIO").isEmpty())){
                            dataAniversario                = rst.getString("DATA_ANIVERSARIO");
                        }else{
                            dataAniversario                = null;
                        }          
                        
                        dataResidencia   = "1990/01/01";

                        if (id==1190){
                            Linha++; 
                        }else{
                            Linha++;
                        }
                        if (rst.getString("LIMITE_CREDITO")!=null){
                            limite           = rst.getDouble("LIMITE_CREDITO");  
                        }else{
                            limite           = 0;
                        }
                        
                        if (nome.length() > 40) {
                            nome = nome.substring(0, 40);
                        }

                        if (endereco.length() > 50) {
                            endereco = endereco.substring(0, 50);
                        }

                        oClientePreferencial.id = id;
                        oClientePreferencial.nome = nome;
                        oClientePreferencial.endereco = endereco;
                        oClientePreferencial.bairro = bairro;
                        oClientePreferencial.numero = numero;                        
                        oClientePreferencial.id_estado = id_estado;
                        oClientePreferencial.id_municipio = id_municipio;
                        oClientePreferencial.id_tipoinscricao = id_tipoinscricao;
                        oClientePreferencial.cep = cep;
                        oClientePreferencial.telefone = telefone;
                        oClientePreferencial.telefone2 = telefone2;                        
                        oClientePreferencial.inscricaoestadual = inscricaoestadual;
                        oClientePreferencial.cnpj = cnpj;
                        oClientePreferencial.sexo = id_sexo;
                        oClientePreferencial.dataresidencia = dataResidencia;
                        oClientePreferencial.datanascimento = dataAniversario;
                        oClientePreferencial.datacadastro = dataCadastro;
                        oClientePreferencial.email = email;
                        oClientePreferencial.valorlimite = limite;
                        oClientePreferencial.codigoanterior = id;
                        vClientePreferencial.add(oClientePreferencial);
                    }
                stm.close();
                } catch (Exception ex) {
                    if (Linha > 0) {
                        throw new VRException("Linha " + Linha + ": " + ex.getMessage());
                    } else {
                        throw ex;
                    }
                }
                return vClientePreferencial;
            } catch(SQLException | NumberFormatException ex) {

                throw ex;
            }
        }    

    public List<FornecedorVO> carregarFornecedorMobility() throws Exception {
            StringBuilder sql = null;
            Statement stm = null;
            ResultSet rst = null;
            Utils util = new Utils();
            List<FornecedorVO> vFornecedor = new ArrayList<>();

            String razaosocial, nomefantasia, obs, inscricaoestadual, endereco, 
                   bairro, datacadastro, numero, telefone, telefone2, email;
            int id, id_municipio = 0, id_estado, id_tipoinscricao, Linha=0;
            Long cnpj, cep;
            double pedidoMin;
            boolean ativo=true;

            try {
                stm = ConexaoFirebird.getConexao().createStatement();

                sql = new StringBuilder();
                sql.append("select id, i_numero, numero, ativo,       ");
                sql.append("       razao_social, nome_fantasia, insc_estadual, ");
                sql.append("       cnpj, endereco, cep, complemento,  ");
                sql.append("       bairro, cidade, uf, cpf, cnpj,s_tipo_fornecedor,      ");
                sql.append("       codigo_cidade, end_num, ddd, ddd2, ");
                sql.append("       telefone1, telefone2, celular, fax,");
                sql.append("       contato, email, observacoes, site, ");
                sql.append("       s_tipo_fornecedor, data_cadastro   ");
                sql.append("from fornecedores                         ");
                sql.append("order by id                               ");

                rst = stm.executeQuery(sql.toString());
                Linha=0;
                try{
                    while (rst.next()) {                    
                        FornecedorVO oFornecedor = new FornecedorVO();

                        id = rst.getInt("id");

                        Linha++; 
                        if (id==31){
                            Linha--;
                            Linha++;                        
                        }                    
                        if ((rst.getString("razao_social") != null)
                                && (!rst.getString("razao_social").isEmpty())) {
                           byte[] bytes = rst.getBytes("razao_social");
                           String textoAcertado = new String(bytes, "ISO-8859-1");                                     
                           razaosocial = util.acertarTexto(textoAcertado.replace("'", "").trim());                     
                        } else {
                            razaosocial = "RAZAO SOCIAL VR";
                        }

                        if ((rst.getString("nome_fantasia") != null)
                                && (!rst.getString("nome_fantasia").isEmpty())) {
                           byte[] bytes = rst.getBytes("nome_fantasia");
                           String textoAcertado = new String(bytes, "ISO-8859-1");                                     
                           nomefantasia = util.acertarTexto(textoAcertado.replace("'", "").trim());                     
                        } else {
                            nomefantasia = "NOME FANTASIA VR";
                        }

                        if ((rst.getString("s_tipo_fornecedor") != null)
                                && (rst.getString("s_tipo_fornecedor") == "F")){
                            id_tipoinscricao = 0;                            
                            if ((rst.getString("cpf") != null)
                                    && (!rst.getString("cpf").isEmpty())) {
                                cnpj = Long.parseLong(util.formataNumero(rst.getString("cpf").trim()));
                            } else {
                                cnpj = Long.parseLong(rst.getString("ID"));
                            }
                        }else{
                            id_tipoinscricao = 1;                                                        
                            if ((rst.getString("cnpj") != null)
                                    && (!rst.getString("cnpj").isEmpty())) {
                                cnpj = Long.parseLong(util.formataNumero(rst.getString("cnpj").trim()));
                            } else {
                                cnpj = Long.parseLong(rst.getString("ID"));
                            }                            
                        }

                        if ((rst.getString("INSC_ESTADUAL") != null)
                                && (!rst.getString("INSC_ESTADUAL").isEmpty())) {
                            inscricaoestadual = util.acertarTexto(rst.getString("INSC_ESTADUAL").replace("'", "").trim());
                        } else {
                            inscricaoestadual = "ISENTO";
                        }

                        if ((rst.getString("endereco") != null)
                                && (!rst.getString("endereco").isEmpty())) {
                            endereco = util.acertarTexto(rst.getString("endereco").replace("'", "").trim());
                        } else {
                            endereco = "";
                        }

                        if ((rst.getString("bairro") != null)
                                && (!rst.getString("bairro").isEmpty())) {
                            bairro = util.acertarTexto(rst.getString("bairro").replace("'", "").trim());
                        } else {
                            bairro = "";
                        }

                        if ((rst.getString("cep") != null)
                                && (!rst.getString("cep").isEmpty())) {
                            cep = Long.parseLong(util.formataNumero(rst.getString("cep").trim()));
                        } else {
                            cep = Long.parseLong("0");
                        }
                        
                        if ((rst.getString("end_num") != null)
                                && (!rst.getString("end_num").isEmpty())) {
                            numero = rst.getString("end_num").trim();
                            if (numero.length()>6){
                                numero = numero.substring(0,6);
                            }
                        } else {
                            numero = "";
                        }                        

                        if ((rst.getString("cidade") != null)
                                && (!rst.getString("cidade").isEmpty())) {

                            if ((rst.getString("uf") != null)
                                    && (!rst.getString("uf").isEmpty())) {

                                id_municipio = util.retornarMunicipioIBGEDescricao(util.acertarTexto(rst.getString("cidade").replace("'", "").trim()),
                                        util.acertarTexto(rst.getString("uf").replace("'", "").trim()));

                                if (id_municipio == 0) {
                                    id_municipio = 3525508;
                                }
                            }
                        } else {
                            id_municipio = 3525508;
                        }

                        if ((rst.getString("uf") != null)
                                && (!rst.getString("uf").isEmpty())) {
                            id_estado = util.retornarEstadoDescricao(util.acertarTexto(rst.getString("uf").replace("'", "").trim()));

                            if (id_estado == 0) {
                                id_estado = 35;
                            }
                        } else {
                            id_estado = 35;
                        }

                        if (rst.getString("TELEFONE1") != null) {
                            telefone = rst.getString("DDD")+""+rst.getString("TELEFONE1").trim();
                        } else {
                            telefone = "";
                        }
                        if (rst.getString("TELEFONE2") != null) {
                            telefone2 = rst.getString("DDD2")+""+rst.getString("TELEFONE2").trim();
                        } else {
                            telefone2 = "";
                        }                        
                        
                        if (rst.getString("EMAIL") != null) {
                            email = rst.getString("EMAIL").trim();
                        } else {
                            email = "";
                        }                          
                        
                        if (rst.getString("OBSERVACOES") != null) {
                            obs = rst.getString("OBSERVACOES").trim();
                        } else {
                            obs = "";
                        }

                        if (rst.getString("DATA_CADASTRO") != null) {
                            datacadastro = rst.getString("DATA_CADASTRO");
                        } else {
                            datacadastro = "";
                        }

                        if (rst.getString("ATIVO") != null) {
                            if (!"0".equals(rst.getString("ATIVO").trim())){
                                ativo = true;
                            }else{
                                ativo = false;    
                            }
                        } else {
                            ativo = true;
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

                        oFornecedor.codigoanterior = id;
                        oFornecedor.razaosocial = razaosocial;
                        oFornecedor.nomefantasia = nomefantasia;
                        oFornecedor.endereco = endereco;
                        oFornecedor.numero=numero;
                        oFornecedor.bairro = bairro;
                        oFornecedor.telefone=telefone;
                        oFornecedor.telefone2=telefone2;                        
                        oFornecedor.email=email;                        
                        oFornecedor.id_municipio = id_municipio;
                        oFornecedor.cep = cep;
                        oFornecedor.id_estado = id_estado;
                        oFornecedor.id_tipoinscricao = id_tipoinscricao;
                        oFornecedor.inscricaoestadual = inscricaoestadual;
                        oFornecedor.cnpj = cnpj;
                        oFornecedor.id_situacaocadastro = (ativo == true ?  1 : 0);                    
                        oFornecedor.observacao = obs;

                        vFornecedor.add(oFornecedor);
                    }
                } catch (Exception ex) {
                    if (Linha > 0) {
                        throw new VRException(rst.getInt("id")+"Linha " + Linha + ": " + ex.getMessage());
                    } else {
                        throw ex;
                    }
                }

                return vFornecedor;

            } catch(SQLException | NumberFormatException ex) {

                throw ex;
            }
    }   
    
    public List<ProdutoFornecedorVO> carregarProdutoFornecedorMobility() throws Exception {

        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        List<ProdutoFornecedorVO> vProdutoFornecedor = new ArrayList<>();
        int idFornecedor, idProduto;
        String codigoExterno;
        java.sql.Date dataAlteracao = new Date(new java.util.Date().getTime());

        try {

            stm = ConexaoFirebird.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("select F.S_SEQUENCIAL AS ID, F.id_fornecedor, P.codigo_interno AS ID_PRODUTO ");
            sql.append("from codigo_ref_fornecedor f ");
            sql.append("INNER JOIN PRODUTOS P ON ");
            sql.append("P.ID = F.id_produto          ");   

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                idFornecedor = rst.getInt("id_fornecedor");
                idProduto = rst.getInt("id_produto");

                if ((rst.getString("id") != null)
                        && (!rst.getString("id").isEmpty())) {
                    codigoExterno = util.acertarTexto(rst.getString("id").replace("'", "").trim());
                } else {
                    codigoExterno = "";
                }

                ProdutoFornecedorVO oProdutoFornecedor = new ProdutoFornecedorVO();

                oProdutoFornecedor.id_fornecedor = idFornecedor;
                oProdutoFornecedor.id_produto = idProduto;               
                oProdutoFornecedor.dataalteracao = dataAlteracao;
                oProdutoFornecedor.codigoexterno = codigoExterno;

                vProdutoFornecedor.add(oProdutoFornecedor);
            }

            return vProdutoFornecedor;
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    public Map<Integer, ProdutoVO> carregarCustoProdutoMobility(int idLoja, int idLojaCliente) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        Map<Integer, ProdutoVO> vProduto = new HashMap<>();
        int idProduto;
        double custo = 0;
        
        try {
            
            stm = ConexaoFirebird.getConexao().createStatement();
            
            sql = new StringBuilder();   
            sql.append("SELECT P.codigo_interno as id, P.preco_compra ");
            sql.append("FROM PRODUTOS P            ");
           
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                idProduto = Integer.parseInt(rst.getString("ID").replace(".", ""));
                custo = Double.parseDouble(rst.getString("PRECO_COMPRA").replace(",", "."));
                
                ProdutoVO oProduto = new ProdutoVO();
                oProduto.id = idProduto;
                
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
            
        } catch(SQLException | NumberFormatException ex) {
            
            throw ex;
        }
    }
       
    public List<ReceberChequeVO> carregarReceberChequeMobility(int id_loja, int id_lojaCliente) throws Exception {

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

            stm = ConexaoFirebird.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("SELECT c.cheque, c.ciccgc, c.client, c.bancox, c.agenci, c.contax, ");
            sql.append("c.valorx, c.dataxx, c.vencim, c.status, c.devol1, c.motdv1, c.devol2, c.motdv2, ");
            sql.append("c.reapre, c.quitad, c.codfor, c.nomfor, c.datfor, c.caixax, c.observ, c.seqdev, ");
            sql.append("c.datcad, c.usucad, c.datalt, c.usualt, c.cobran, c.datcob, c.entrad ");
            sql.append("FROM CHEQUES c ");             
            sql.append("WHERE c.FILIAL = "+String.valueOf(id_lojaCliente));                         

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                ReceberChequeVO oReceberCheque = new ReceberChequeVO();

                cpfCnpj = Long.parseLong(rst.getString("ciccgc").trim());
                
                if (String.valueOf(cpfCnpj).length() > 11) {
                    idTipoInscricao = 0;
                } else {
                    idTipoInscricao = 1;
                }
                
                idBanco = util.retornarBanco(Integer.parseInt(rst.getString("bancox").trim()));                

                if ((rst.getString("agenci") != null) &&
                        (!rst.getString("agenci").trim().isEmpty())) {
                    agencia = util.acertarTexto(rst.getString("agenci").trim().replace("'", ""));
                } else {
                    agencia = "";
                }
                
                if ((rst.getString("contax") != null) &&
                        (!rst.getString("contax").trim().isEmpty()))  {
                    conta = util.acertarTexto(rst.getString("contax").trim().replace("'", ""));
                } else {
                    conta = "";
                }
                
                if ((rst.getString("cheque") != null) &&
                        (!rst.getString("cheque").trim().isEmpty())) {
                    
                    cheque = Integer.parseInt(util.formataNumero(rst.getString("cheque")));
                    
                    if (String.valueOf(cheque).length() > 10) {
                        cheque = Integer.parseInt(String.valueOf(cheque).substring(0, 10));
                    }
                } else {
                    cheque = 0;
                }
                                      
                if ((rst.getString("dataxx") != null) &&
                        (!rst.getString("dataxx").trim().isEmpty())) {
                
                    dataemissao = rst.getString("dataxx").trim();
                } else {
                    dataemissao = "2016/02/01";
                }
                
                if ((rst.getString("vencim") != null) &&
                        (!rst.getString("vencim").trim().isEmpty())) {
                
                    datavencimento = rst.getString("vencim").trim();
                } else {
                    datavencimento = "2016/02/12";
                }
                
                if ((rst.getString("observ") != null) &&
                        (!rst.getString("observ").isEmpty())) {
                    nome = util.acertarTexto(rst.getString("observ").replace("'", "").trim());
                } else {
                    nome = "";
                }
                
                /*if ((rst.getString("chrinscrg") != null) &&
                        (!rst.getString("chrinscrg").isEmpty())) {
                    rg = util.acertarTexto(rst.getString("chrinscrg").trim().replace("'", ""));
                    
                    if (rg.length() > 20) {
                        rg = rg.substring(0, 20);
                    }
                } else {*/
                    rg = "";
                //}
                
                valor = Double.parseDouble(rst.getString("valorx"));
                numerocupom = 0;
                juros = 0;

                /*if ((rst.getString("chrobserv1") != null)
                        && (!rst.getString("chrobserv1").isEmpty())) {
                    observacao = util.acertarTexto(rst.getString("chrobserv1").replace("'", "").trim());
                } else {*/
                    observacao = "IMPORTADO VR";
                //}

                /*if ((rst.getString("chrtelefone") != null) &&
                        (!rst.getString("chrtelefone").isEmpty()) &&
                        (!"0".equals(rst.getString("chrtelefone").trim()))) {
                    telefone = util.formataNumero(rst.getString("chrtelefone"));
                } else {*/
                    telefone = "";
                //}
                    
                if (rst.getInt("status")==1){
                    id_tipoalinea = 0;
                } else if (rst.getInt("status")==2){
                    id_tipoalinea = 15;                    
                } else {
                    id_tipoalinea = 0;
                }
                
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
    
    public List<ReceberCreditoRotativoVO> carregarReceberClienteMobility(int id_loja, int id_lojaCliente) throws Exception {
        
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        List<ReceberCreditoRotativoVO> vReceberCreditoRotativo = new ArrayList<>();
        
        int id_cliente, numerocupom;
        double valor, juros;
        String observacao, dataemissao, datavencimento;
        long cnpj;
        
        try {
            
            stm = ConexaoFirebird.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("SELECT D.SEQUEN, D.CLIENT, D.DATAXX, D.VENCIM, D.BAIXAX, D.VALORX, ");
            sql.append("       D.VALPAG, STATUS, C.ciccgc AS CNPJ, D.DATCAD, D.OBSERV      ");  
            sql.append("  FROM DOCUMENTOS D                                    "); 
            sql.append("INNER JOIN CLIENTES C ON                                ");  
            sql.append("    C.CODIGO = D.CLIENT               ");  
            sql.append("WHERE D.STATUS = 1                                      "); // SOMENTE EM ABERTO
            sql.append("  AND D.FILIAL = "+String.valueOf(id_lojaCliente)); // SOMENTE EM ABERTO            
            sql.append("ORDER BY D.CLIENT, D.DATAXX, D.VENCIM                       ");  
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                ReceberCreditoRotativoVO oReceberCreditoRotativo = new ReceberCreditoRotativoVO();
                
                id_cliente = rst.getInt("CLIENT");                
                dataemissao = rst.getString("DATCAD");
                datavencimento = rst.getString("VENCIM");
                numerocupom = Integer.parseInt(util.formataNumero(rst.getString("SEQUEN")));
                valor = Double.parseDouble(rst.getString("VALORX"));
                juros = 0;
                
                if ((rst.getString("OBSERV") != null) &&
                        (!rst.getString("OBSERV").isEmpty())) {
                    observacao = util.acertarTexto(rst.getString("OBSERV").replace("'", ""));
                } else { 
                    observacao = "IMPORTADO VR";
                }
                
                if ((rst.getString("CNPJ")!=  null) &&
                            (!rst.getString("CNPJ").isEmpty())) {
                    cnpj = Long.parseLong(rst.getString("CNPJ").trim());
                }else{
                    cnpj = Long.parseLong("0");
                }
                
                oReceberCreditoRotativo.cnpjCliente = cnpj;
                oReceberCreditoRotativo.id_loja = id_loja;
                oReceberCreditoRotativo.dataemissao = dataemissao;
                oReceberCreditoRotativo.numerocupom = numerocupom;
                oReceberCreditoRotativo.valor = valor;
                oReceberCreditoRotativo.observacao = observacao;
                oReceberCreditoRotativo.id_clientepreferencial = id_cliente;
                oReceberCreditoRotativo.datavencimento = datavencimento;
                oReceberCreditoRotativo.valorjuros = juros;
                
                vReceberCreditoRotativo.add(oReceberCreditoRotativo);
                
            }
            
            return vReceberCreditoRotativo;
            
        } catch(SQLException | NumberFormatException ex) {
            
            throw ex;
        }
    }    

    public Map<Integer, ProdutoVO> carregarPrecoProdutoMobility(int idLoja, int id_lojaCliente) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        Map<Integer, ProdutoVO> vProduto = new HashMap<>();
        int idProduto;
        double preco = 0, margem = 0;
        
        try {
            
            stm = ConexaoFirebird.getConexao().createStatement();
            
            sql = new StringBuilder(); 
            sql.append("SELECT P.codigo_interno as id, P.preco_venda1, P.margem ");
            sql.append("FROM PRODUTOS P            ");
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                idProduto = Integer.parseInt(rst.getString("ID"));
                preco = rst.getDouble("PRECO_VENDA1");
                
                if ((rst.getString("MARGEM") != null) &&
                        !rst.getString("MARGEM").trim().isEmpty()) {
                    margem = Double.parseDouble(rst.getString("MARGEM").replace(",", "."));
                } else {
                    margem = 0;
                }
                
                ProdutoVO oProduto = new ProdutoVO();
                oProduto.id = idProduto;
                oProduto.margem = margem;
                
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
            
        } catch(SQLException | NumberFormatException ex) {
            
            throw ex;
        }
    }    
    
    public Map<Integer, ProdutoVO> carregarEstoqueProdutoMobility(int idLoja, int id_lojaCliente) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        Map<Integer, ProdutoVO> vProduto = new HashMap<>();
        int idProduto;
        double saldo = 0;
        
        try {
            stm = ConexaoFirebird.getConexao().createStatement();
            sql = new StringBuilder();         
            sql.append(" SELECT P.codigo_interno as id, P.ESTOQUE_ATUAL ");
            sql.append(" FROM PRODUTOS P            ");

            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                idProduto = Integer.parseInt(rst.getString("ID"));
                saldo = rst.getDouble("ESTOQUE_ATUAL");
                
                ProdutoVO oProduto = new ProdutoVO();
                oProduto.id = idProduto;
                
                ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                
                oComplemento.idLoja = idLoja;
                oComplemento.estoque = saldo;
                oProduto.vComplemento.add(oComplemento);                
                CodigoAnteriorVO oCodigoAnterior = new CodigoAnteriorVO();
                
                oCodigoAnterior.estoque = saldo;
                oCodigoAnterior.id_loja = idLoja;
                
                oProduto.vCodigoAnterior.add(oCodigoAnterior);
                
                vProduto.put(idProduto, oProduto);
                
            }
            
            return vProduto;
            
        } catch(SQLException | NumberFormatException ex) {
            
            throw ex;
        }
    }    
    
    public Map<Long, ProdutoVO> carregarCodigoBarras() throws SQLException {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        Map<Long, ProdutoVO> vProduto = new HashMap<>();
        int idProduto;
        long codigobarras;
        
        try {
            stm = ConexaoFirebird.getConexao().createStatement();
            sql = new StringBuilder();
            sql.append("select CAST(p.codigo_interno AS NUMERIC(14,0)) as codigo, c.s_codigo as barras ");
            sql.append("from cod_auxiliares c ");
            sql.append("inner join produtos p on ");
            sql.append("p.id = c.id_produto ");
            sql.append("union all ");
            sql.append("select CAST(p.codigo_interno AS NUMERIC(14,0)) as codigo, p.codigo_barras as barras ");
            sql.append("from produtos p ");
            sql.append("ORDER BY 1 ");
            // ORIGINAL       
            /*
            sql.append("select p.codigo_interno as codigo, c.s_codigo as barras ");
            sql.append("from cod_auxiliares c ");
            sql.append("inner join produtos p on ");
            sql.append("p.id = c.id_produto ");
            */ 
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                idProduto = Integer.parseInt(rst.getString("codigo"));

                if ((rst.getString("barras") != null) &&
                        (!rst.getString("barras").trim().isEmpty())) {
                    codigobarras = Long.parseLong(rst.getString("barras").replace(".", ""));
                } else {
                    codigobarras = 0;
                }
                
                //if (String.valueOf(codigobarras).length() >= 7) {
                
                    ProdutoVO oProduto = new ProdutoVO();
                    oProduto.id = idProduto;
                    ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
                    oAutomacao.codigoBarras = codigobarras;
                    oProduto.vAutomacao.add(oAutomacao);

                    vProduto.put(codigobarras, oProduto);
                ///}
                
            }
            
            return vProduto;
            
        } catch(SQLException | NumberFormatException ex) {
            
            throw ex;
        }
    }
    
    //IMPORTAÇÕES
    public void importarFamiliaProdutoMobility() throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Familia Produto...");
            List<FamiliaProdutoVO> vFamiliaProduto = carregarFamiliaProdutoMobility();

            new FamiliaProdutoDAO().salvar(vFamiliaProduto);

        } catch (Exception ex) {

            throw ex;
        }
    }    
    
    public void importarMercadologicoMobility() throws Exception {

        List<MercadologicoVO> vMercadologico = new ArrayList<>();

        try {

            ProgressBar.setStatus("Carregando dados...Mercadologico...");
            vMercadologico = carregarMercadologicoMobility(1);
            new MercadologicoDAO().salvar(vMercadologico, true);

            vMercadologico = carregarMercadologicoMobility(2);
            new MercadologicoDAO().salvar(vMercadologico, false);

            vMercadologico = carregarMercadologicoMobility(3);
            new MercadologicoDAO().salvar(vMercadologico, false);

        } catch (Exception ex) {

            throw ex;
        }
    }   

    public void importarClientePreferencialMobility(int idLoja, int idLojaCliente) throws Exception {

            try {
                ProgressBar.setStatus("Carregando dados...Clientes...");
                List<ClientePreferencialVO> vClientePreferencial = carregarClienteMobility(idLoja, idLojaCliente);
                new PlanoDAO().salvar(idLoja);
                new ClientePreferencialDAO().salvar(vClientePreferencial, idLoja, idLojaCliente);

            } catch (Exception ex) {

                throw ex;
            }
        }  

    public void importarFornecedorMobility() throws Exception {

            try {

                ProgressBar.setStatus("Carregando dados...Fornecedor...");
                List<FornecedorVO> vFornecedor = carregarFornecedorMobility();

                new FornecedorDAO().salvar(vFornecedor);

            } catch (Exception ex) {

                throw ex;
            }
        }

    public void importarProdutoMobility(int id_loja) throws Exception {
        
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();
        
        try {
            
            ProgressBar.setStatus("Carregando dados...Produtos...");
            Map<Integer, ProdutoVO> vProdutoMilenio = carregarProdutoMobility();
            
            List<LojaVO> vLoja = new LojaDAO().carregar();
            
            ProgressBar.setMaximum(vProdutoMilenio.size());
            
            for (Integer keyId : vProdutoMilenio.keySet()) {
                
                ProdutoVO oProduto = vProdutoMilenio.get(keyId);

                oProduto.idProdutoVasilhame = -1;
                oProduto.excecao = -1;
                oProduto.idTipoMercadoria = -1;

                vProdutoNovo.add(oProduto);
                
                
                ProgressBar.next();
            }
            
            produto.implantacaoExterna = true;
            produto.salvar(vProdutoNovo, id_loja, vLoja);
            
        } catch(Exception ex) {
            
            throw ex;
        }
    }
    
    public void importarCustoProdutoMobility(int id_loja, int id_lojaCliente) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();
                
        try {
            
            ProgressBar.setStatus("Carregando dados...Produtos...Custo...");
            Map<Integer, ProdutoVO> vCustoProduto = carregarCustoProdutoMobility(id_loja, id_lojaCliente);
            
            List<LojaVO> vLoja = new LojaDAO().carregar();
            
            ProgressBar.setMaximum(vCustoProduto.size());
            
            for (Integer keyId : vCustoProduto.keySet()) {
                
                ProdutoVO oProduto = vCustoProduto.get(keyId);

                vProdutoNovo.add(oProduto);
                
                ProgressBar.next();
            }
            
            produto.alterarCustoProduto(vProdutoNovo, id_loja);
            
        } catch(Exception ex) {
            
            throw ex;
        }        
    }
    
    public void importarPrecoProdutoMobility(int id_loja, int id_lojaCliente) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();
                
        try {
            
            ProgressBar.setStatus("Carregando dados...Produtos...Preço...");
            Map<Integer, ProdutoVO> vPrecoProduto = carregarPrecoProdutoMobility(id_loja, id_lojaCliente);
            
            List<LojaVO> vLoja = new LojaDAO().carregar();
            
            ProgressBar.setMaximum(vPrecoProduto.size());
            
            for (Integer keyId : vPrecoProduto.keySet()) {
                
                ProdutoVO oProduto = vPrecoProduto.get(keyId);

                vProdutoNovo.add(oProduto);
                
                ProgressBar.next();
            }
            
            produto.alterarPrecoProduto(vProdutoNovo, id_loja);
            
        } catch(Exception ex) {
            
            throw ex;
        }        
    }
    
    public void importarEstoqueProdutoMobility(int id_loja, int id_lojaCliente) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();
                
        try {
            
            ProgressBar.setStatus("Carregando dados...Produtos...Estoque...");
            Map<Integer, ProdutoVO> vEstoqueProduto = carregarEstoqueProdutoMobility(id_loja, id_lojaCliente);
            
            List<LojaVO> vLoja = new LojaDAO().carregar();
            
            ProgressBar.setMaximum(vEstoqueProduto.size());
            
            for (Integer keyId : vEstoqueProduto.keySet()) {
                
                ProdutoVO oProduto = vEstoqueProduto.get(keyId);

                vProdutoNovo.add(oProduto);
                
                ProgressBar.next();
            }
            
            produto.alterarEstoqueProduto(vProdutoNovo, id_loja);
            
        } catch(Exception ex) {
            
            throw ex;
        }        
    }    

    public void importarCodigoBarra() throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();
                
        try {
            
            ProgressBar.setStatus("Carregando dados...Produtos...CodigoBarra...");
            Map<Long, ProdutoVO> vCodigoBarras = carregarCodigoBarras();
            
            List<LojaVO> vLoja = new LojaDAO().carregar();
            
            ProgressBar.setMaximum(vCodigoBarras.size());
            
            for (Long keyId : vCodigoBarras.keySet()) {
                
                ProdutoVO oProduto = vCodigoBarras.get(keyId);

                vProdutoNovo.add(oProduto);
                
                ProgressBar.next();
            }
                        
            
            produto.addCodigoBarras(vProdutoNovo);
            
        } catch(Exception ex) {
            
            throw ex;
        }        
    }    
    
    public void importarChequeReceberMobility(int id_loja, int id_lojaCliente) throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Cheque Receber...");
            List<ReceberChequeVO> vReceberCheque = carregarReceberChequeMobility(id_loja, id_lojaCliente);

            new ReceberChequeDAO().salvar(vReceberCheque,id_loja);

        } catch (Exception ex) {

            throw ex;
        }
    }      
    
    public void importarProdutoFornecedorMobility() throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Produto Fornecedor...");
            List<ProdutoFornecedorVO> vProdutoFornecedor = carregarProdutoFornecedorMobility();

            new ProdutoFornecedorDAO().salvar(vProdutoFornecedor);

        } catch (Exception ex) {

            throw ex;
        }
    }
    
    public void importarReceberClienteMobility(int idLoja, int idLojaCliente) throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Receber Cliente...");
            List<ReceberCreditoRotativoVO> vReceberCliente = carregarReceberClienteMobility(idLoja, idLojaCliente);

            new ReceberCreditoRotativoDAO().salvar(vReceberCliente, idLoja);

        } catch (Exception ex) {

            throw ex;
        }
    }
    
    public void importarProdutoBalanca(String arquivo, int opcao) throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Produtos de Balanca...");
            List<ProdutoBalancaVO> vProdutoBalanca = new ProdutoBalancaDAO().carregar(arquivo, opcao);

            new ProdutoBalancaDAO().salvar(vProdutoBalanca);
        } catch (Exception ex) {

            throw ex;
        }
    }
    
    
    // FUNÇÕES
    private int retornarAliquotaICMSMobility(String codTrib, String descTrib) {

        int retorno;

        if ("1".equals(codTrib)) {
            retorno = 0;
        }else if ("2".equals(codTrib)){
            retorno = 1;        
        }else if ("3".equals(codTrib)) {
            retorno = 2;
        }else if ("4".equals(codTrib)){
            retorno = 3;        
        }else if (("14".equals(codTrib))||("16".equals(codTrib))){
            retorno = 7;        
        }else if ("15".equals(codTrib)){
            retorno = 6;        
        }else{
            retorno = 8;
        }
        
        return retorno;
    }    
    
    public void corrigirClienteDuplicado() throws Exception {

        try {
            ProgressBar.setStatus("Corrigindo dados...Cliente Duplicados...");
            new ClientePreferencialDAO().corrigirClienteDuplicado();
        } catch (Exception ex) {

            throw ex;
        }
    }
}