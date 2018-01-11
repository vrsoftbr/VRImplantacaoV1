package vrimplantacao.dao.interfaces;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrframework.classe.Util;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao.dao.cadastro.CestDAO;
import vrimplantacao.dao.cadastro.CodigoAnteriorDAO;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.dao.cadastro.NcmDAO;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.dao.cadastro.ProdutoDAO;
import vrimplantacao.dao.cadastro.ReceberChequeDAO;
import vrimplantacao.dao.cadastro.ReceberCreditoRotativoDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao.vo.vrimplantacao.CestVO;
import vrimplantacao.vo.vrimplantacao.ClientePreferencialVO;
import vrimplantacao.vo.vrimplantacao.CodigoAnteriorVO;
import vrimplantacao.vo.vrimplantacao.FamiliaProdutoVO;
import vrimplantacao.vo.vrimplantacao.FornecedorVO;
import vrimplantacao.vo.vrimplantacao.NcmVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAliquotaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAutomacaoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoComplementoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoVO;
import vrimplantacao.vo.vrimplantacao.ReceberChequeVO;
import vrimplantacao.vo.vrimplantacao.ReceberCreditoRotativoVO;
import vrimplantacao2.dao.cadastro.MercadologicoDAO;
import vrimplantacao2.parametro.Parametros;
import vrimplantacao2.utils.arquivo.LinhaArquivo;
import vrimplantacao2.utils.arquivo.planilha.Planilha;
import vrimplantacao2.utils.multimap.KeyList;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.MercadologicoVO;
import vrimplantacao2.vo.importacao.MercadologicoIMP;

public class GdoorDAO extends AbstractIntefaceDao { 

    @Override
    public List<FornecedorVO> carregarFornecedor() throws Exception {
        List<FornecedorVO> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(                    
                "select\n" +
                "    f.codigo id,\n" +
                "    null as datacadastro,\n" +
                "    f.nome razao,\n" +
                "    f.fantasia,\n" +
                "    f.endereco,\n" +
                "    f.numero,\n" +
                "    f.complemento,\n" +
                "    f.bairro,\n" +
                "    f.cidade,\n" +
                "    f.uf,\n" +
                "    f.cep,\n" +
                "    f.telefone fone1,\n" +
                "    null as fone2,\n" +
                "    f.celular,\n" +
                "    f.ie_rg inscricaoestadual,\n" +
                "    f.cnpj_cnpf cnpj,\n" +
                "    f.observacoes,\n" +
                "    f.email,\n" +
                "    f.fax,\n" +
                "    case upper(f.situacao) when 'INATIVO' then 0 else 1 end as id_situacaocadastro\n" +
                "from\n" +
                "    fornecedor f\n" +
                "order by\n" +
                "    f.codigo"
            )) {
                while (rst.next()) {
                    FornecedorVO oFornecedor = new FornecedorVO();
                    
                    Date datacadastro;
                    
                    if ((rst.getString("datacadastro") != null)
                            && (!rst.getString("datacadastro").isEmpty())) {
                        datacadastro = rst.getDate("datacadastro");                    
                    } else {
                        datacadastro = new Date(new java.util.Date().getTime()); 
                    }

                    oFornecedor.setId(rst.getInt("id"));
                    oFornecedor.setDatacadastro(datacadastro);
                    oFornecedor.setCodigoanterior(rst.getInt("id"));
                    oFornecedor.setRazaosocial(rst.getString("razao"));
                    oFornecedor.setNomefantasia(rst.getString("fantasia"));
                    oFornecedor.setEndereco(rst.getString("endereco"));
                    oFornecedor.setNumero(rst.getString("numero"));
                    oFornecedor.setComplemento(rst.getString("complemento"));
                    oFornecedor.setBairro(rst.getString("bairro"));
                    oFornecedor.setId_municipio(Utils.retornarMunicipioIBGEDescricao(rst.getString("cidade"), rst.getString("uf")));                     
                    oFornecedor.setId_estado(Utils.getEstadoPelaSigla(rst.getString("uf")));
                    oFornecedor.setCep(Utils.stringToLong(rst.getString("cep"), 0));
                    oFornecedor.setTelefone(rst.getString("fone1"));
                    oFornecedor.setTelefone2(rst.getString("fone2"));
                    oFornecedor.setCelular(rst.getString("celular"));
                    oFornecedor.setInscricaoestadual(rst.getString("inscricaoestadual"));
                    oFornecedor.setCnpj(Utils.stringToLong(rst.getString("cnpj"), 0));
                    oFornecedor.setId_tipoinscricao(String.valueOf(oFornecedor.getCnpj()).length() > 11 ? 0 : 1);
                    oFornecedor.setObservacao("IMPORTADO VR  " + rst.getString("observacoes"));
                    oFornecedor.setEmail(rst.getString("email"));
                    oFornecedor.setFax(rst.getString("fax"));
                    oFornecedor.setId_situacaocadastro(rst.getInt("id_situacaocadastro"));
                    oFornecedor.setId_tipoindicadorie();
                    
                    result.add(oFornecedor);
                }
            }
        }
        
        return result;
    }
    
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select descricao from grupos g order by descricao"
            )) {
                while (rst.next()) {                    
                    MercadologicoIMP helper = new MercadologicoIMP();
                    
                    String[] merc = rst.getString("descricao").split(":");

                    helper.setImportSistema("GDOOR");
                    helper.setImportLoja("1");

                    helper.setMerc1ID(merc[0]);
                    helper.setMerc1Descricao(merc[0]);

                    if (merc.length > 1) {
                        helper.setMerc2ID(merc[1]);
                        helper.setMerc2Descricao(merc[1]);
                    }

                    if (merc.length > 2) {
                        helper.setMerc3ID(merc[2]);
                        helper.setMerc3Descricao(merc[2]);
                    }

                    result.add(helper);                   
                }
            }
        }
        
        return result;        
    }
    
    
    
    public List<FamiliaProdutoVO> carregarFamiliaProdutoGdoor() throws Exception {

        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        List<FamiliaProdutoVO> vFamiliaProduto = new ArrayList<>();
        String descricao;

        try {

            stm = ConexaoFirebird.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("select descricao ");
            sql.append("from familias order by descricao ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                if ((rst.getString("descricao") != null)
                        && (!rst.getString("descricao").trim().isEmpty())) {
                    descricao = util.acertarTexto(rst.getString("descricao").replace("'", "").trim());
                } else {
                    descricao = "";
                }

                FamiliaProdutoVO oFamiliaProduto = new FamiliaProdutoVO();

                oFamiliaProduto.id = -1;
                oFamiliaProduto.descricao = descricao;
                oFamiliaProduto.id_situacaocadastro = 1;
                oFamiliaProduto.codigoant = 0;

                vFamiliaProduto.add(oFamiliaProduto);
            }

            return vFamiliaProduto;

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }    
   
    public Map<Integer, ProdutoVO> carregarProdutoGdoor() throws Exception {
        Map<Integer, ProdutoVO> vProduto = new HashMap<>();
        StringBuilder sql = null;
        Statement stm = null, stmPostgres = null;
        ResultSet rst = null, rstPostgres = null;
        Utils util = new Utils();
        int idTipoEmbalagem = 0, qtdEmbalagem, idTipoPisCofins, idTipoPisCofinsCredito, tipoNaturezaReceita,
               idAliquota, idFamilia, mercadologico1, mercadologico2, mercadologico3, idSituacaoCadastro, 
               ncm1, ncm2, ncm3, codigoBalanca, referencia = -1, idProduto, validade;
        String descriaoCompleta, descricaoReduzida, descricaoGondola, ncmAtual, strCodigoBarras;
        boolean eBalanca, pesavel = false;
        long codigoBarras = 0;
        double precoVenda, custo, margem, codigoAnterior = 0, estoque;
        
        try {
            
            Conexao.begin();
            
            stmPostgres = Conexao.createStatement();
            
            stm = ConexaoFirebird.getConexao().createStatement();
            
            sql = new StringBuilder();
            sql.append("select codigo, barras, descricao, und, familia, grupo, ");
            sql.append("peso, qtd_saldo, preco_custo, margem_lucro, preco_venda, ");
            sql.append("cod_ncm, situacao, data_cadastro from estoque ");
            
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                ProdutoVO oProduto = new ProdutoVO();
                
                if ((rst.getString("situacao") != null) &&
                        (!rst.getString("situacao").trim().isEmpty())) {
                    if ("Ativo".equals(rst.getString("situacao").trim())) {
                        idSituacaoCadastro = 1;
                    } else {
                        idSituacaoCadastro = 0;
                    }
                } else {
                    idSituacaoCadastro = 1;
                }
                                
                codigoAnterior = Double.parseDouble(rst.getString("codigo").trim().replace(".", ""));
                idProduto = Integer.parseInt(rst.getString("codigo").trim().replace(".", ""));
                
                referencia = -1;
                
                sql = new StringBuilder();
                sql.append("select codigo, descricao, pesavel, validade ");
                sql.append("from implantacao.produtobalanca ");
                sql.append("where codigo = " + rst.getString("codigo").replace(".", ""));

                rstPostgres = stmPostgres.executeQuery(sql.toString());

                if (rstPostgres.next()) {

                    eBalanca = true;
                    codigoBalanca = rstPostgres.getInt("codigo");
                    validade = rstPostgres.getInt("validade");

                    if ("U".equals(rstPostgres.getString("pesavel").trim())) {
                        pesavel = true;
                        idTipoEmbalagem = 0;
                    } else if ("P".equals(rstPostgres.getString("pesavel").trim())) {
                        pesavel = false;
                        idTipoEmbalagem = 4;
                    }
                } else {
                    codigoBalanca = -1;
                    eBalanca = false;
                    pesavel = false;
                    validade = 0;
                    
                    if ("KG".equals(rst.getString("und").trim())) {
                        idTipoEmbalagem = 4;
                    } else if ("UN".equals(rst.getString("und").trim())) {
                        idTipoEmbalagem = 0;
                    } else {
                        idTipoEmbalagem = 0;
                    }
                }
                
                if ((rst.getString("descricao") != null) &&
                        (!rst.getString("descricao").trim().isEmpty())) {
                    byte[] bytes = rst.getBytes("descricao");
                    String textoAcertado = new String(bytes, "ISO-8859-1");                                     
                    descriaoCompleta = util.acertarTexto(textoAcertado.replace("'", "").trim());                     
                } else {
                    descriaoCompleta = "";
                }
                
                if ((rst.getString("descricao") != null) &&
                        (!rst.getString("descricao").trim().isEmpty())) {
                    byte[] bytes = rst.getBytes("descricao");
                    String textoAcertado = new String(bytes, "ISO-8859-1");                                     
                    descricaoReduzida = util.acertarTexto(textoAcertado.replace("'", "").trim());                     
                } else {
                    descricaoReduzida = "";
                }
                
                descricaoGondola = descricaoReduzida;

                if (idTipoEmbalagem == 4) {
                    qtdEmbalagem = 1;
                } else {
                    qtdEmbalagem = 1;
                }
                
                idFamilia = -1;
                
                mercadologico1 = 14;
                mercadologico2 = 1;
                mercadologico3 = 1;
                
                if ((rst.getString("cod_ncm") != null) &&
                        (!rst.getString("cod_ncm").isEmpty()) &&
                        (rst.getString("cod_ncm").trim().length() > 5)) {
                    
                    ncmAtual = util.formataNumero(rst.getString("cod_ncm").trim());
                    
                    NcmVO oNcm = new NcmDAO().validar(ncmAtual);
                    
                    ncm1 = oNcm.ncm1;    
                    ncm2 = oNcm.ncm2;
                    ncm3 = oNcm.ncm3;
                    
                } else {
                    ncm1 = 402;
                    ncm2 = 99;
                    ncm3 = 0;
                }
                                
                if (eBalanca == true) {
                    codigoBarras = Long.parseLong(String.valueOf(idProduto));
                } else {
                    
                    if ((rst.getString("barras") != null) &&
                            (!rst.getString("barras").trim().isEmpty())) {
                        
                        strCodigoBarras = util.formataNumero(rst.getString("barras").replace(".", "").trim());
                        
                        if (String.valueOf(Long.parseLong(strCodigoBarras)).length() < 7) {                            
                            if (idProduto >= 10000) {
                                codigoBarras = util.gerarEan13(idProduto, true);
                            } else {
                                codigoBarras = util.gerarEan13(idProduto, false);
                            }
                        } else {
                            codigoBarras = Long.parseLong(util.formataNumero(rst.getString("barras").trim()));
                        }
                    } else {
                        codigoBarras = -1;
                    }
                }
                
                idTipoPisCofins = 0;
                
                idTipoPisCofinsCredito = 12;
                
                tipoNaturezaReceita = -1;
                
                idAliquota = 8;
                                
                if ((rst.getString("preco_venda") != null) &&
                        (!rst.getString("preco_venda").trim().isEmpty())) {
                    precoVenda = Double.parseDouble(rst.getString("preco_venda").replace(",", "."));
                } else {
                    precoVenda = 0;
                }
                
                if ((rst.getString("preco_custo") != null) &&
                        (!rst.getString("preco_custo").trim().isEmpty())) {
                    custo = Double.parseDouble(rst.getString("preco_custo").replace(",", "."));
                } else {
                    custo = 0;
                }
                
                if ((rst.getString("margem_lucro") != null) &&
                        (!rst.getString("margem_lucro").trim().isEmpty())) {
                    margem = Double.parseDouble(rst.getString("margem_lucro").replace(",", "."));
                } else {
                    margem = 0;
                }
                
                if ((rst.getString("qtd_saldo") != null) &&
                        (!rst.getString("qtd_saldo").isEmpty())) {
                    estoque = Double.parseDouble(rst.getString("qtd_saldo").replace(",", "."));
                } else {
                    estoque = 0;
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
                oProduto.margem = margem;
                oProduto.validade = validade;
                
                ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                
                oComplemento.idSituacaoCadastro = idSituacaoCadastro;
                oComplemento.precoVenda = precoVenda;
                oComplemento.precoDiaSeguinte = precoVenda;
                oComplemento.custoComImposto = custo;
                oComplemento.custoSemImposto = custo;
                oComplemento.estoque = estoque;
                
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
                
                oCodigoAnterior.codigoanterior = codigoAnterior;
                oCodigoAnterior.codigoatual = idProduto;
                
                if((rst.getString("barras")!=null) && (!rst.getString("barras").trim().isEmpty())){
                   oCodigoAnterior.barras = Long.parseLong(util.formataNumero(rst.getString("barras").replace(".", "").trim()));
                } else {
                   oCodigoAnterior.barras = 0; 
                }
                
                oCodigoAnterior.naturezareceita = tipoNaturezaReceita;                
                oCodigoAnterior.piscofinsdebito = -1;                
                oCodigoAnterior.piscofinscredito = -1;                
                oCodigoAnterior.ref_icmsdebito = "";                
                oCodigoAnterior.estoque = estoque;
                oCodigoAnterior.e_balanca = eBalanca;
                oCodigoAnterior.codigobalanca = codigoBalanca;
                oCodigoAnterior.custosemimposto = custo;
                oCodigoAnterior.custocomimposto = custo;
                oCodigoAnterior.margem = margem;
                oCodigoAnterior.precovenda = precoVenda;
                oCodigoAnterior.referencia = -1;
                
                if ((rst.getString("cod_ncm") != null) && (!rst.getString("cod_ncm").trim().isEmpty())) {
                    oCodigoAnterior.ncm = rst.getString("cod_ncm").trim().replace(".", "");
                } else {
                    oCodigoAnterior.ncm = "";
                }
                
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

    public Map<Long, ProdutoVO> carregarCodigoBarras() throws SQLException {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        Map<Long, ProdutoVO> vProduto = new HashMap<>();
        double idProduto;
        long codigobarras;
        Utils util = new Utils();
        
        try {
            
            stm = ConexaoFirebird.getConexao().createStatement();
            
            sql = new StringBuilder();            
            sql.append("select procodaux, procod "); 
            sql.append("from produtoaux ");

            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                idProduto = Double.parseDouble(rst.getString("procod"));

                if ((rst.getString("procodaux") != null) &&
                        (!rst.getString("procodaux").trim().isEmpty())) {
                    codigobarras = Long.parseLong(util.formataNumero(rst.getString("procodaux").replace(".", "").trim()));
                } else {
                    codigobarras = 0;
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
            
        } catch(SQLException | NumberFormatException ex) {
            
            throw ex;
        }
    }
    
    //IMPORTAÇÕES
    
    @Override
    public void importarMercadologico() throws Exception {
        ProgressBar.setStatus("Carregando dados do mercadológico...");
        List<MercadologicoIMP> mercadologicos = getMercadologicos();
        vrimplantacao2.dao.cadastro.MercadologicoDAO dao = new vrimplantacao2.dao.cadastro.MercadologicoDAO();
        dao.salvar(mercadologicos);
    }   

    public void importarProduto6(int id_loja) throws Exception {
        
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();
        
        try {
            
            ProgressBar.setStatus("Carregando dados...Produtos...");
            Map<Integer, ProdutoVO> vProdutoGDoor = carregarProdutoGdoor();
            
            List<LojaVO> vLoja = new LojaDAO().carregar();
            
            ProgressBar.setMaximum(vProdutoGDoor.size());
            
            for (Integer keyId : vProdutoGDoor.keySet()) {
                
                ProdutoVO oProduto = vProdutoGDoor.get(keyId);

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
    
    public void importarChequeReceber(int id_loja, int id_lojaCliente) throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Cheque Receber...");
            List<ReceberChequeVO> vReceberCheque = carregarReceberCheque(id_loja, id_lojaCliente);

            new ReceberChequeDAO().salvar(vReceberCheque,id_loja);

        } catch (Exception ex) {

            throw ex;
        }
    }      
    
    @Override
    public List<ProdutoVO> carregarListaDeProdutos(int idLojaVR, int idLojaCliente) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {   
            try (ResultSet rst = stm.executeQuery(
                "select\n" +
                "    p.codigo id,\n" +
                "    p.descricao descricaocompleta,\n" +
                "    p.descricao descricaoreduzida,\n" +
                "    p.descricao descricaogondola,\n" +
                "    case upper(p.situacao) when 'INATIVO' then 0 else 1 end as id_situacaocadastro,\n" +
                "    p.data_cadastro datacadastro,\n" +
                "    14 mercadologico1,\n" +
                "    1 mercadologico2,\n" +
                "    1 mercadologico3,\n" +
                "    p.cod_ncm ncm,\n" +
                "    p.cod_cest cest,\n" +
                "    p.familia id_familia,\n" +
                "    p.margem_lucro margem,\n" +
                "    p.barras ean,\n" +
                "    p.validade_dias validade,\n" +
                "    p.und id_tipoembalagem,\n" +
                "    p.peso pesobruto,\n" +
                "    p.peso pesoliquido,\n" +
                "    p.pis_codigo piscofins_cst_sai,\n" +
                "    p.pise_codigo piscofins_cst_ent,\n" +
                "    '' as piscofins_natrec,\n" +
                "    p.preco_venda preco,\n" +
                "    p.preco_custo custocomimposto,\n" +
                "    p.preco_custo custosemimposto,\n" +
                "    p.qtd estoque,\n" +
                "    p.qtd_ideal  minimo,\n" +
                "    0 maximo,\n" +
                "    40 icms_cst,\n" +
                "    0 icms_aliq,\n" +
                "    0 icms_reducao\n" +
                "from\n" +
                "    estoque p\n" +
                "order by\n" +
                "    p.codigo"
            )) {
                
                //Obtem os produtos de balança
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
                while (rst.next()) {      
                    //Instancia o produto
                    ProdutoVO oProduto = new ProdutoVO();
                    //Prepara as variáveis
                    ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
                    CodigoAnteriorVO oCodigoAnterior = new CodigoAnteriorVO();
                    ProdutoAliquotaVO oAliquota = new ProdutoAliquotaVO();
                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                    //Inclui elas nas listas
                    oProduto.getvAutomacao().add(oAutomacao);
                    oProduto.getvCodigoAnterior().add(oCodigoAnterior);
                    oProduto.getvAliquota().add(oAliquota);
                    oProduto.getvComplemento().add(oComplemento);  
                                      
                    oProduto.setId(rst.getInt("id"));
                    oProduto.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    oProduto.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    oProduto.setDescricaoGondola(rst.getString("descricaogondola"));
                    oProduto.setIdSituacaoCadastro(rst.getInt("id_situacaocadastro"));
                    if (rst.getString("datacadastro") != null) {
                        oProduto.setDataCadastro(Util.formatDataGUI(rst.getDate("datacadastro")));
                    } else {
                        oProduto.setDataCadastro(Util.formatDataGUI(new Date(new java.util.Date().getTime())));
                    }

                    oProduto.setMercadologico1(rst.getInt("mercadologico1"));
                    oProduto.setMercadologico2(rst.getInt("mercadologico2"));
                    oProduto.setMercadologico3(rst.getInt("mercadologico3"));
                    oProduto.setMercadologico4(0);
                    oProduto.setMercadologico5(0);
                    
                    if ((rst.getString("ncm") != null)
                            && (!rst.getString("ncm").isEmpty())
                            && (rst.getString("ncm").trim().length() > 5)) {
                        NcmVO oNcm = new NcmDAO().validar(rst.getString("ncm").trim());

                        oProduto.setNcm1(oNcm.ncm1);
                        oProduto.setNcm2(oNcm.ncm2);
                        oProduto.setNcm3(oNcm.ncm3);
                    }
                    
                    CestVO cest = CestDAO.parse(rst.getString("cest"));                    
                    oProduto.setCest1(cest.getCest1());
                    oProduto.setCest2(cest.getCest2());
                    oProduto.setCest3(cest.getCest3());
                    

                    oProduto.setFamiliaProduto(rst.getString("id_familia"));
                    oProduto.setMargem(rst.getDouble("margem"));
                    oProduto.setQtdEmbalagem(1);              
                    oProduto.setIdComprador(1);
                    oProduto.setIdFornecedorFabricante(1);
                    
                    long codigoBarra = Utils.stringToLong(rst.getString("ean"), -2);
      
                    ProdutoBalancaVO produtoBalanca;
                    if (codigoBarra <= Integer.MAX_VALUE) {
                        produtoBalanca = produtosBalanca.get((int) codigoBarra);
                    } else {
                        produtoBalanca = null;
                    }
                    if (produtoBalanca != null) {
                        oAutomacao.setCodigoBarras((long) oProduto.getId());                          
                        oProduto.setValidade(produtoBalanca.getValidade() > 1 ? produtoBalanca.getValidade() : rst.getInt(""));
                        oProduto.eBalanca = true;
                        
                        if ("P".equals(produtoBalanca.getPesavel())) {
                            oAutomacao.setIdTipoEmbalagem(4);
                            oProduto.setPesavel(false);
                        } else {
                            oAutomacao.setIdTipoEmbalagem(0);
                            oProduto.setPesavel(true);
                        }
                        
                        oCodigoAnterior.setCodigobalanca(produtoBalanca.getCodigo());
                        oCodigoAnterior.setE_balanca(true);
                    } else {                                                
                        oProduto.setValidade(rst.getInt("validade"));
                        oProduto.setPesavel(false); 
                        oProduto.eBalanca = false;
                        
                        oAutomacao.setCodigoBarras(codigoBarra);
                        oAutomacao.setIdTipoEmbalagem(Utils.converteTipoEmbalagem(rst.getString("id_tipoembalagem")));
                        
                        oCodigoAnterior.setCodigobalanca(0);
                        oCodigoAnterior.setE_balanca(false);
                    }                    
                    oAutomacao.setQtdEmbalagem(1);
                                        
                    oProduto.setIdTipoEmbalagem(oAutomacao.getIdTipoEmbalagem());                    
                                     
                    
                    oProduto.setSugestaoPedido(true);
                    oProduto.setAceitaMultiplicacaoPdv(true);
                    oProduto.setSazonal(false);
                    oProduto.setFabricacaoPropria(false);
                    oProduto.setConsignado(false);
                    oProduto.setDdv(0);
                    oProduto.setPermiteTroca(true);
                    oProduto.setVendaControlada(false);
                    oProduto.setVendaPdv(true);
                    oProduto.setConferido(true);
                    oProduto.setPermiteQuebra(true);   
                    oProduto.setPesoBruto(rst.getDouble("pesobruto"));
                    oProduto.setPesoLiquido(rst.getDouble("pesoliquido"));
                    
                    oProduto.setIdTipoPisCofinsDebito(Utils.retornarPisCofinsDebito(Utils.stringToInt(rst.getString("piscofins_cst_sai"))));
                    oProduto.setIdTipoPisCofinsCredito(Utils.retornarPisCofinsCredito(Utils.stringToInt(rst.getString("piscofins_cst_ent"))));
                    oProduto.setTipoNaturezaReceita(Utils.retornarTipoNaturezaReceita(oProduto.idTipoPisCofinsDebito, rst.getString("piscofins_natrec")));
                    
                    oComplemento.setPrecoVenda(rst.getDouble("preco"));
                    oComplemento.setPrecoDiaSeguinte(rst.getDouble("preco"));
                    oComplemento.setCustoComImposto(rst.getDouble("custocomimposto"));
                    oComplemento.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    oComplemento.setIdLoja(idLojaVR);
                    oComplemento.setIdSituacaoCadastro(oProduto.getIdSituacaoCadastro());
                    oComplemento.setEstoque(rst.getDouble("estoque"));
                    oComplemento.setEstoqueMinimo(rst.getDouble("minimo"));
                    oComplemento.setEstoqueMaximo(rst.getDouble("maximo"));                   

                    String uf = Parametros.get().getUfPadrao().getSigla();
                    oAliquota.setIdEstado(Utils.getEstadoPelaSigla(uf));
                    oAliquota.setIdAliquotaDebito(Utils.getAliquotaICMS(uf, rst.getInt("icms_cst"), rst.getDouble("icms_aliq"), rst.getDouble("icms_reducao")));
                    oAliquota.setIdAliquotaCredito(Utils.getAliquotaICMS(uf, rst.getInt("icms_cst"), rst.getDouble("icms_aliq"), rst.getDouble("icms_reducao")));
                    oAliquota.setIdAliquotaDebitoForaEstado(Utils.getAliquotaICMS(uf, rst.getInt("icms_cst"), rst.getDouble("icms_aliq"), rst.getDouble("icms_reducao")));
                    oAliquota.setIdAliquotaCreditoForaEstado(Utils.getAliquotaICMS(uf, rst.getInt("icms_cst"), rst.getDouble("icms_aliq"), rst.getDouble("icms_reducao")));
                    oAliquota.setIdAliquotaDebitoForaEstadoNF(Utils.getAliquotaICMS(uf, rst.getInt("icms_cst"), rst.getDouble("icms_aliq"), rst.getDouble("icms_reducao")));

                    
                    oCodigoAnterior.setCodigoanterior(rst.getLong("id"));
                    oCodigoAnterior.setCodigoAnteriorStr(String.valueOf((int)oProduto.getId()));
                    oCodigoAnterior.setMargem(oProduto.getMargem());
                    oCodigoAnterior.setPrecovenda(oComplemento.getPrecoVenda());
                    oCodigoAnterior.setBarras(Utils.stringToLong(rst.getString("ean"), -2));
                    oCodigoAnterior.setReferencia((int) oProduto.getId());
                    oCodigoAnterior.setNcm(rst.getString("ncm"));
                    oCodigoAnterior.setId_loja(idLojaVR);
                    oCodigoAnterior.setPiscofinsdebito(Utils.stringToInt(rst.getString("piscofins_cst_sai")));
                    oCodigoAnterior.setPiscofinscredito(Utils.stringToInt(rst.getString("piscofins_cst_ent")));
                    oCodigoAnterior.setNaturezareceita(Utils.stringToInt(rst.getString("piscofins_natrec")));
                    oCodigoAnterior.setRef_icmsdebito(rst.getString("icms_cst"));

                    //Encerramento produto
                    if (oProduto.getMargem() == 0) {
                        oProduto.recalcularMargem();
                    }
                    
                    vProduto.add(oProduto);
                }                
            }
        } 
        
        return vProduto;
    }
    
    @Override
    public void importarPrecoProduto(int idLojaVR, int idLojaCliente) throws Exception {
        ProgressBar.setStatus("Carregando dados...Produtos...Preço...");

        Map<Double, ProdutoVO> aux = new LinkedHashMap<>();        
        for (ProdutoVO vo: carregarListaDeProdutos(idLojaVR, idLojaCliente)) {
            Double id = vo.getIdDouble() > 0 ? vo.getIdDouble() : vo.getId();
            aux.put(id, vo);
        }
        
        ProgressBar.setMaximum(aux.size());

        ProdutoDAO produto = new ProdutoDAO();
        produto.implantacaoExterna = true;
        produto.alterarPrecoProdutoRapido(new ArrayList(aux.values()), idLojaVR);
    }
    
    @Override
    public void importarProduto(int idLojaVR, int idLojaCliente) throws Exception {
        ProgressBar.setStatus("Carregando dados...Produtos.....");

        Map<Double, ProdutoVO> aux = new LinkedHashMap<>();        
        for (ProdutoVO vo: carregarListaDeProdutos(idLojaVR, idLojaCliente)) {
            Double id = vo.getIdDouble() > 0 ? vo.getIdDouble() : vo.getId();
            aux.put(id, vo);
        }

        List<LojaVO> vLoja = new LojaDAO().carregar();

        ProgressBar.setMaximum(aux.size());

        ProdutoDAO produto = new ProdutoDAO();
        produto.setImportSistema("GDOOR");
        produto.setImportLoja("" + idLojaCliente);
        produto.implantacaoExterna = true;
        produto.salvar(new ArrayList<>(aux.values()), idLojaVR, vLoja);
    }
    
    @Override
    public List<FamiliaProdutoVO> carregarFamiliaProduto() throws Exception {
        List<FamiliaProdutoVO> result = new ArrayList<>();
        
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select descricao from familias order by descricao"
            )) {
                while (rst.next()) {
                    FamiliaProdutoVO familiaVO = new FamiliaProdutoVO();
                    
                    familiaVO.setId(-1);
                    familiaVO.setIdLong(-1);
                    familiaVO.setDescricao(rst.getString("descricao"));
                    
                    familiaVO.setImpSistema("GDOOR");
                    familiaVO.setImpLoja("1");
                    familiaVO.setImpId(familiaVO.getDescricao());
                    
                    result.add(familiaVO);
                }
            }
        }
        
        return result;
    }
    
    @Override
    public Map<Long, ProdutoVO> carregarEanProduto(int idLojaVR, int idLojaCliente) throws Exception {
        Map<Long, ProdutoVO> result = new LinkedHashMap<>();
        
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    p.codigo id_produto,\n" +
                    "    p.barras ean,\n" +
                    "    p.und id_tipoembalagem,\n" +
                    "    1 as qtdembalagem\n" +
                    "from\n" +
                    "    estoque p\n" +
                    "order by\n" +
                    "    p.codigo"
            )) {
            
                while (rst.next()) {

                    ProdutoVO oProduto = new ProdutoVO();                                      
                    ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
                    oProduto.getvAutomacao().add(oAutomacao);
                    
                    oProduto.setIdDouble(rst.getInt("id_produto"));  
                    oAutomacao.setCodigoBarras(Utils.stringToLong(rst.getString("ean")));
                    oAutomacao.setIdTipoEmbalagem(Utils.converteTipoEmbalagem(rst.getString("id_tipoembalagem")));
                    oAutomacao.setQtdEmbalagem(rst.getInt("qtdEmbalagem"));
                    
                    String ean = String.valueOf(oAutomacao.getCodigoBarras());
                    if ((ean.length() >= 7) &&
                        (ean.length() <= 14)) {                                             
                        result.put(oAutomacao.getCodigoBarras(), oProduto);
                    }                    
                }                 
            }
        }
            
        return result;
    }
    
    @Override
    public List<ClientePreferencialVO> carregarCliente(int idLojaCliente) throws Exception {
        List<ClientePreferencialVO> vClientePreferencial = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {                    
            try (ResultSet rst = stm.executeQuery(               
                "select\n" +
                "    c.codigo id,\n" +
                "    c.nome,\n" +
                "    c.endereco res_endereco,\n" +
                "    c.numero res_numero,\n" +
                "    c.complemento res_complemento,\n" +
                "    c.bairro res_bairro,\n" +
                "    c.cidade res_cidade,\n" +
                "    c.uf res_uf,\n" +
                "    c.cep res_cep,\n" +
                "    c.telefone fone1,\n" +
                "    c.celular,\n" +
                "    c.fax,\n" +
                "    c.ie_rg inscricaoestadual,\n" +
                "    c.cnpj_cnpf cnpj,\n" +
                "    1 as sexo,\n" +
                "    c.dia_de_acerto prazodias,\n" +
                "    c.email,\n" +
                "    c.cadastro datacadastro,\n" +
                "    c.limite_credito limitepreferencial,\n" +
                "    case upper(c.situacao) when 'INATIVO' then 1 else 0 end as bloqueado,\n" +
                "    c.observacoes,\n" +
                "    c.pai nomepai,\n" +
                "    c.mae nomemae,\n" +
                "    null empresa,\n" +
                "    null telempresa,\n" +
                "    c.profissao cargo,\n" +
                "    c.renda salario,\n" +
                "    case\n" +
                "        when upper(c.est_civil) containing 'CASAD' then 2\n" +
                "        when upper(c.est_civil) containing 'SOLT' then 1\n" +
                "        else 0 end as estadocivil,\n" +
                "    C.conjuge,\n" +
                "    null orgaoemissor,\n" +
                "    c.nascimento datanascimento\n" +
                "from\n" +
                "    cliente c\n" +
                "where\n" +
                "    c.codigo > 0\n" +
                "order by\n" +
                "    c.codigo"
            )) {
                while (rst.next()) {                    
                    ClientePreferencialVO oClientePreferencial = new ClientePreferencialVO();

                    oClientePreferencial.setId(rst.getInt("id"));
                    oClientePreferencial.setCodigoanterior(rst.getInt("id"));
                    oClientePreferencial.setNome(rst.getString("nome"));
                    oClientePreferencial.setEndereco(rst.getString("res_endereco"));
                    oClientePreferencial.setNumero(rst.getString("res_numero"));
                    oClientePreferencial.setComplemento(rst.getString("res_complemento"));
                    oClientePreferencial.setBairro(rst.getString("res_bairro"));
                    oClientePreferencial.setId_municipio(Utils.retornarMunicipioIBGEDescricao(rst.getString("res_cidade"), rst.getString("res_uf")));                     
                    oClientePreferencial.setId_estado(Utils.getEstadoPelaSigla(rst.getString("res_uf")));
                    oClientePreferencial.setCep(rst.getString("res_cep"));
                    oClientePreferencial.setTelefone(rst.getString("fone1"));                    
                    oClientePreferencial.setCelular(rst.getString("celular"));
                    oClientePreferencial.setInscricaoestadual(rst.getString("inscricaoestadual"));
                    oClientePreferencial.setCnpj(rst.getString("cnpj"));
                    if (String.valueOf(oClientePreferencial.getCnpj()).length() < 8) {
                        oClientePreferencial.setCnpj(-1);
                    }
                    oClientePreferencial.setSexo(rst.getInt("sexo"));
                    oClientePreferencial.setVencimentocreditorotativo(rst.getInt("PRAZODIAS"));
                    oClientePreferencial.setEmail(rst.getString("email"));
                    oClientePreferencial.setDataresidencia("1990/01/01");
                    oClientePreferencial.setDatacadastro(rst.getDate("datacadastro"));
                    oClientePreferencial.setEmail(rst.getString("email"));
                    oClientePreferencial.setValorlimite(rst.getDouble("limitepreferencial"));
                    oClientePreferencial.setBloqueado(rst.getBoolean("bloqueado"));
                    oClientePreferencial.setId_situacaocadastro(1);
                    oClientePreferencial.setObservacao("IMPORTADO VR  :" + rst.getString("observacoes"));
                    oClientePreferencial.setDatanascimento(rst.getDate("datanascimento"));
                    oClientePreferencial.setNomepai(rst.getString("nomePai"));
                    oClientePreferencial.setNomemae(rst.getString("nomeMae"));
                    oClientePreferencial.setEmpresa(rst.getString("empresa"));
                    oClientePreferencial.setTelefoneempresa(rst.getString("telEmpresa"));
                    oClientePreferencial.setCargo(rst.getString("cargo"));
                    oClientePreferencial.setId_tipoinscricao(String.valueOf(oClientePreferencial.getCnpj()).length() > 11 ? 0 : 1);
                    oClientePreferencial.setSalario(rst.getDouble("salario"));
                    oClientePreferencial.setId_tipoestadocivil(rst.getInt("estadoCivil"));
                    oClientePreferencial.setNomeconjuge(rst.getString("conjuge"));
                    oClientePreferencial.setOrgaoemissor(rst.getString("ORGAOEMISSOR"));                   

                    vClientePreferencial.add(oClientePreferencial);
                }                
            }
        }
        return vClientePreferencial;
    }  
    
    public List<ReceberCreditoRotativoVO> carregarReceberCreditoRotativo(int idLojaVR, String arquivo) throws Exception {
        List<ReceberCreditoRotativoVO> result = new ArrayList<>();
        
        Pattern p = Pattern.compile("CUPOM.*[0-9]");
        for (LinhaArquivo rst: new Planilha(arquivo)) {
            ReceberCreditoRotativoVO oReceberCreditoRotativo = new ReceberCreditoRotativoVO();

            oReceberCreditoRotativo.setId_clientepreferencial(rst.getInt("Cod.Cliente"));
            //oReceberCreditoRotativo.setCnpjCliente(Utils.stringToLong(rst.getString("")));
            oReceberCreditoRotativo.setId_loja(idLojaVR);
            oReceberCreditoRotativo.setDataemissao(new Date(rst.getData("Emissao").getTime()));
            oReceberCreditoRotativo.setDatavencimento(new Date(rst.getData("Vencimento").getTime()));
            String cupom = Utils.acertarTexto(rst.getString("Historico"));
            if (cupom.matches("CUPOM.*[0-9]/g")) {
                oReceberCreditoRotativo.setNumerocupom(Utils.stringToInt(cupom.replaceAll(cupom, cupom)));
            } else {
                oReceberCreditoRotativo.setNumerocupom(Utils.stringToInt(rst.getString("Historico")));
            }
            oReceberCreditoRotativo.setValor(rst.getDouble("Valor"));
            oReceberCreditoRotativo.setValormulta(0);
            oReceberCreditoRotativo.setValorjuros(rst.getDouble("Juro por atraso"));
            oReceberCreditoRotativo.setObservacao("IMPORTADO VR - DOCUMENTO: " + rst.getString("Documento") + " - " + rst.getString("Historico"));

            result.add(oReceberCreditoRotativo);                
        }
        
        return result;
    }
 

    public void importarReceberCreditoRotativo(int idLojaVR, String arquivo) throws Exception {
        try {
            ProgressBar.setStatus("Carregando dados...Receber Cliente...");
            List<ReceberCreditoRotativoVO> vReceberCliente = carregarReceberCreditoRotativo(idLojaVR, arquivo);

            new ReceberCreditoRotativoDAO().salvar(vReceberCliente, idLojaVR, true);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarProdutoMercadologico() throws Exception {
        ProgressBar.setStatus("Carregando dados...Produto mercadológico...");
        MercadologicoDAO dao = new MercadologicoDAO();
        MercadologicoVO aAcertar = dao.getAAcertar();
        MultiMap<Integer, MercadologicoVO> result = new MultiMap<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                "select\n" +
                "    cast(codigo as integer) codigo,\n" +
                "    grupo\n" +
                "from\n" +
                "    estoque\n" +
                "order by\n" +
                "    codigo"
            )) {
                while (rst.next()) {
                    String[] merc = rst.getString("grupo").split(":");
                    String[] chave = new String[]{
                        "GDOOR",
                        "1",
                        merc[0],
                        merc.length > 1 ? merc[1] : "",
                        merc.length > 2 ? merc[2] : "",
                        "",
                        ""
                    };
                    MercadologicoVO mercadologico = dao.getMercadologico(chave);
                    result.put(mercadologico, rst.getInt("codigo"));
                }
            }
        }
        
        
        
        try {
            Conexao.begin();
            ProgressBar.setStatus("Corrigindo....");
            ProgressBar.setMaximum(result.size());
            Map<Double, CodigoAnteriorVO> anterior = new CodigoAnteriorDAO().carregarCodigoAnterior();
            try (Statement stm = Conexao.createStatement()) {
                for (KeyList<Integer> keys: result.keySet()) {
                    MercadologicoVO merc = result.get(keys);
                    int id = keys.get(0);

                    CodigoAnteriorVO ant = anterior.get((double) id);
                    if (ant != null) {
                        stm.execute(
                                "update produto set " +
                                "mercadologico1 = " + merc.getMercadologico1() + ", " +
                                "mercadologico2 = " + merc.getMercadologico2() + ", " +
                                "mercadologico3 = " + merc.getMercadologico3() + ", " +
                                "mercadologico4 = " + merc.getMercadologico4() + ", " +
                                "mercadologico5 = " + merc.getMercadologico5() + " " +
                                "where id = " + (int) ant.getCodigoatual()
                        );
                    }
                    ProgressBar.next();
                }
            }
            
            Conexao.commit();
        } catch (Exception e) {
            Conexao.rollback();
            throw e;
        }
    }
    
}