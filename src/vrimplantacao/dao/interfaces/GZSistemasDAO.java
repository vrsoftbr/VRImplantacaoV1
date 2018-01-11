package vrimplantacao.dao.interfaces;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import vrframework.classe.ProgressBar;
import vrframework.classe.Util;
import vrframework.remote.ItemComboVO;
import vrimplantacao.classe.ConexaoMySQL;
import vrimplantacao.classe.Global;
import vrimplantacao.dao.cadastro.CestDAO;
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
import vrimplantacao.dao.cadastro.ReceberCreditoRotativoDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao.vo.vrimplantacao.CestVO;
import vrimplantacao.vo.vrimplantacao.ClientePreferencialVO;
import vrimplantacao.vo.vrimplantacao.CodigoAnteriorVO;
import vrimplantacao.vo.vrimplantacao.FamiliaProdutoVO;
import vrimplantacao.vo.vrimplantacao.FornecedorVO;
import vrimplantacao.vo.vrimplantacao.MercadologicoVO;
import vrimplantacao.vo.vrimplantacao.NcmVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAliquotaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAutomacaoLojaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAutomacaoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoComplementoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoFornecedorVO;
import vrimplantacao.vo.vrimplantacao.ProdutoVO;
import vrimplantacao.vo.vrimplantacao.ReceberCreditoRotativoVO;

/*






* Aparentemente as informações principais do sistema ficam localizadas no schema "fiscal".

* Produtos se localizam na tabela "fiscal.cad_produto"

* Aparentemente o mercadológico é representado pela tabela "fiscal.cad_produto_estru" porém só há 3 itens nela.
A tabela que possui o mercadológico dos produtos é a "fiscal.tab_familia_produto"
Há um campo suspeito chamado grupo no cadastro de produto, verificar da onde vem essa informação. Verificar 
se o cliente irá querer a estrutura de mercadológico do VR.

* Código EAN possivelmente é um campo chamado "codigoa" na tabela "fiscal.cad_produto"

* Pode ser que a definição de pesavel ou pesavel unitario seja o campo "pesavel" na "fiscal.cad_produto"
S - Produto de kilo.
U - Unitário pesável.
N - Unitário.

* Familia de Produto: "fiscal.cad_familia_produto" na tabela "fiscal.cad_produto.FamiliaProduto"

* CEST: na tabela "fiscal.cad_produto.CEST"

* Unificar a tributação com o produto
select
  p.Codigo_operacaof,
  concat(trib.Codigo_cfop, trib.sequencia) trib,
  trib.Sit_Trib icms_cst,
  trib.Aliquota_icms icms_aliq,
  trib.Pct_Red_Calc_ICMS icms_reducao,
  trib.Pis_Cst piscofins_debito,
  trib.Codigo_ECF,
  trib.*
from
  fiscal.cad_produto p
  join fiscal.cad_operacaof trib on p.Codigo_operacaof = concat(trib.Codigo_cfop, trib.sequencia)
limit 1000
*/

public class GZSistemasDAO extends AbstractIntefaceDao{
    
    @Override
    public List<FamiliaProdutoVO> carregarFamiliaProduto() throws SQLException {
        List<FamiliaProdutoVO> result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select codigo, descricao "
                            + "from cad_familia_produto"
            )) {
                while (rst.next()) {
                    FamiliaProdutoVO oFamiliaProduto = new FamiliaProdutoVO();
                    oFamiliaProduto.setId(rst.getInt("codigo"));
                    oFamiliaProduto.setDescricao(rst.getString("descricao"));
                    oFamiliaProduto.setCodigoant(rst.getInt("codigo"));
                    result.add(oFamiliaProduto);
                }
            }
        }
        
        return result;
    }
        
    @Override
    public void importarFamiliaProduto() throws Exception{
        List<FamiliaProdutoVO> vFamiliaProduto;

        ProgressBar.setStatus("Carregando dados...Familia Produto...");
        vFamiliaProduto = carregarFamiliaProduto();
        FamiliaProdutoDAO dao = new FamiliaProdutoDAO();
        dao.salvar(vFamiliaProduto);
    }
    
    @Override
    public List<MercadologicoVO> carregarMercadologico(int nivel) throws Exception {
        List<MercadologicoVO> result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select distinct e.depto, d.descricao descM1, "
                            + "e.grupo, g.descricao descM2 "
                            + "from mercodb.estoque e "
                            + "inner join mercodb.depto d on d.codigo = e.depto "
                            + "inner join mercodb.grupo g on g.codigo = e.grupo "
                            + "where e.depto is not null "
                            + "and e.depto > 0 "
                            + "order by e.depto, e.grupo "
            )) {
                while (rst.next()) {
                    MercadologicoVO mercadologico = new MercadologicoVO();
                    mercadologico.setMercadologico1(rst.getInt("depto"));
                    mercadologico.setDescricao(rst.getString("descM1").trim());
                    if (nivel > 1) {
                        mercadologico.setMercadologico2(rst.getInt("grupo"));
                        
                        if ((rst.getString("descM2") != null) &&
                                (!rst.getString("descM2").trim().isEmpty())) {
                            mercadologico.setDescricao(rst.getString("descM2"));
                        } else {
                            mercadologico.setDescricao(rst.getString("descM1").trim());
                        }
                    }
                    if (nivel > 2) {
                        mercadologico.setMercadologico3(1);
                        
                        if ((rst.getString("descM2") != null) &&
                                (!rst.getString("descM2").trim().isEmpty())) {
                            mercadologico.setDescricao(rst.getString("descM2").trim());
                        } else {
                            mercadologico.setDescricao(rst.getString("descM1").trim());
                        }
                    }
                    mercadologico.setNivel(nivel);
                    
                    result.add(mercadologico);
                }
            }
        }
        return result;
    }   
    
    @Override
    public void importarMercadologico() throws Exception{
        List<MercadologicoVO> vMercadologico;

        ProgressBar.setStatus("Carregando dados...Mercadologico...");
        MercadologicoDAO dao = new MercadologicoDAO();

        vMercadologico = carregarMercadologico(1);
        dao.salvar(vMercadologico, true);

        vMercadologico = carregarMercadologico(2);
        dao.salvar(vMercadologico, false);

        vMercadologico = carregarMercadologico(3);
        dao.salvar(vMercadologico, false);
        
        dao.salvarMax();
    }
    
    @Override
    public List<ProdutoVO> carregarListaDeProdutos(int idLojaVR, int idLojaCliente) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        int cstSaida, cstEntrada;
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {   
            try (ResultSet rst = stm.executeQuery(
                    "select e.cdprod, e.codbarra, e.descricao, e.descpdv, e.unidade, e.validade, "
                            + "e.depto, e.grupo, t.st codTrib, t.descricao descTrib, t.aliquota, t.reducao, "
                            + "e.tributa, e.st trib, e.cfiscal, e.stcofins, e.stpis, e.stcofinsen, e.stpisen, e.cest "
                            + "from mercodb.estoque e "
                            + "inner join mercodb.tributa t on t.codigo = e.tributa "
            )) {                
                //Obtem os produtos de balança
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
                while (rst.next()) {      
                    
                    cstSaida = rst.getInt("codTrib");
                    cstEntrada = rst.getInt("codTrib");
                    
                    if (cstSaida > 9) {
                        cstSaida = Integer.parseInt(String.valueOf(cstSaida).substring(0, 2));
                    }

                    if (cstEntrada > 9) {
                        cstEntrada = Integer.parseInt(String.valueOf(cstEntrada).substring(0, 2));
                    }                    
                    
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
                                      
                    oProduto.setIdDouble(rst.getDouble("cdprod"));
                    oProduto.setDescricaoCompleta(rst.getString("descricao").trim());
                    oProduto.setDescricaoReduzida(rst.getString("descpdv").trim());
                    oProduto.setDescricaoGondola(rst.getString("descricao").trim());
                    oProduto.setIdSituacaoCadastro(1);

                    oProduto.setDataCadastro(Util.formatDataGUI(new Date(new java.util.Date().getTime())));

                    oProduto.setMercadologico1(rst.getInt("depto"));
                    oProduto.setMercadologico2(rst.getInt("grupo"));
                    oProduto.setMercadologico3(1);
                    oProduto.setMercadologico4(0);
                    oProduto.setMercadologico5(0);
                    
                    if ((rst.getString("cfiscal") != null)
                            && (!rst.getString("cfiscal").isEmpty())
                            && (rst.getString("cfiscal").trim().length() > 5)) {
                        NcmVO oNcm = new NcmDAO().validar(rst.getString("cfiscal").trim());

                        oProduto.setNcm1(oNcm.ncm1);
                        oProduto.setNcm2(oNcm.ncm2);
                        oProduto.setNcm3(oNcm.ncm3);
                    } else {
                        oProduto.setNcm1(402);
                        oProduto.setNcm2(99);
                        oProduto.setNcm3(0);
                    }
                    
                    
                    if ((rst.getString("cest") != null)
                            && (!rst.getString("cest").trim().isEmpty())) {
                        CestVO cest = CestDAO.parse(Utils.formataNumero(rst.getString("cest").trim()));
                        oProduto.setCest1(cest.getCest1());
                        oProduto.setCest2(cest.getCest2());
                        oProduto.setCest3(cest.getCest3());
                    } else {
                        oProduto.setCest1(-1);
                        oProduto.setCest2(-1);
                        oProduto.setCest3(-1);
                    }

                    oProduto.setIdFamiliaProduto(-1);
                    oProduto.setMargem(0);
                    oProduto.setQtdEmbalagem(1);              
                    oProduto.setIdComprador(1);
                    oProduto.setIdFornecedorFabricante(1);
                    
                    long codigoProduto;                    
                    codigoProduto = Long.parseLong(Utils.formataNumero(Utils.formataNumero(rst.getString("codbarra").trim())));
                    
                    /**
                     * Aparentemente o sistema utiliza o próprio id para produtos de balança.
                     */ 
                    ProdutoBalancaVO produtoBalanca;
                    if (codigoProduto <= Integer.MAX_VALUE) {
                        produtoBalanca = produtosBalanca.get((int) codigoProduto);
                    } else {
                        produtoBalanca = null;
                    }
                    if (produtoBalanca != null) {
                        oAutomacao.setCodigoBarras((long) oProduto.getIdDouble());                          
                        oProduto.setValidade(produtoBalanca.getValidade() > 1 ? produtoBalanca.getValidade() : rst.getInt("validade"));
                        oCodigoAnterior.setE_balanca(true);
                        
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
                        oCodigoAnterior.setE_balanca(false);
                        
                        if (Long.parseLong(Utils.formataNumero(rst.getString("codbarra").trim())) >= 1000000) {
                            oAutomacao.setCodigoBarras(Long.parseLong(Utils.formataNumero(rst.getString("codbarra").trim())));
                        } else {
                            oAutomacao.setCodigoBarras(-2);
                        }
                        oAutomacao.setIdTipoEmbalagem(Utils.converteTipoEmbalagem(rst.getString("Unidade").trim()));
                        
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
                    oProduto.setPesoBruto(0);
                    oProduto.setPesoLiquido(0);
                    
                    oProduto.setIdTipoPisCofinsDebito(retornarPisCofinsDebito(rst.getInt("stpis")));
                    oProduto.setIdTipoPisCofinsCredito(retornarPisCofinsCredito(rst.getInt("stpisen")));                    
                    oProduto.setTipoNaturezaReceita(Utils.retornarTipoNaturezaReceita(oProduto.idTipoPisCofinsDebito, ""));
                    
                    oComplemento.setPrecoVenda(0);
                    oComplemento.setPrecoDiaSeguinte(0);
                    oComplemento.setCustoComImposto(0);
                    oComplemento.setCustoSemImposto(0);
                    oComplemento.setIdLoja(idLojaVR);
                    oComplemento.setIdSituacaoCadastro(1);
                    oComplemento.setEstoque(0);
                    oComplemento.setEstoqueMinimo(0);
                    oComplemento.setEstoqueMaximo(0);                   
                    
                    oAliquota.setIdEstado(Global.idEstado);
                    if (oAliquota.getIdEstado() == 0) {oAliquota.setIdEstado(35);}
                    oAliquota.setIdAliquotaDebito(Utils.getAliquotaIcms(cstSaida, rst.getDouble("aliquota"), rst.getDouble("reducao")));
                    oAliquota.setIdAliquotaCredito(Utils.getAliquotaIcms(cstEntrada, rst.getDouble("aliquota"), rst.getDouble("reducao")));
                    oAliquota.setIdAliquotaDebitoForaEstado(Utils.getAliquotaIcms(cstSaida, rst.getDouble("aliquota"), rst.getDouble("reducao")));
                    oAliquota.setIdAliquotaCreditoForaEstado(Utils.getAliquotaIcms(cstEntrada, rst.getDouble("aliquota"), rst.getDouble("reducao")));
                    oAliquota.setIdAliquotaDebitoForaEstadoNF(Utils.getAliquotaIcms(cstSaida, rst.getDouble("aliquota"), rst.getDouble("reducao")));
                    
                    oCodigoAnterior.setCodigoanterior(oProduto.getIdDouble());
                    oCodigoAnterior.setMargem(oProduto.getMargem());
                    oCodigoAnterior.setPrecovenda(oComplemento.getPrecoVenda());
                    oCodigoAnterior.setBarras(Long.parseLong(Utils.formataNumero(rst.getString("codbarra").trim())));
                    oCodigoAnterior.setReferencia((int) oProduto.getId());
                    oCodigoAnterior.setNcm(rst.getString("cfiscal").trim());
                    oCodigoAnterior.setId_loja(idLojaVR);
                    oCodigoAnterior.setPiscofinsdebito(rst.getInt("stpis"));
                    oCodigoAnterior.setPiscofinscredito(rst.getInt("stpisen"));
                    oCodigoAnterior.setNaturezareceita(-1);
                    oCodigoAnterior.setRef_icmsdebito(rst.getString("codTrib").trim());

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
    public void importarProduto(int idLojaVR, int idLojaCliente) throws Exception {
        ProdutoDAO produto = new ProdutoDAO();

        ProgressBar.setStatus("Carregando dados...Produtos.....");
        List<ProdutoVO> vProdutos = carregarListaDeProdutos(idLojaVR, idLojaCliente);

        List<LojaVO> vLoja = new LojaDAO().carregar();

        ProgressBar.setMaximum(vProdutos.size());

        produto.implantacaoExterna = true;
        produto.salvar(vProdutos, idLojaVR, vLoja);
    }
    
    @Override
    public List<ItemComboVO> carregarLojasCliente() throws Exception {
        List<ItemComboVO> itens = new ArrayList<>();
           itens.add(new ItemComboVO(1, "LOJA 01"));
        return itens;
    }
    
    public List<ProdutoVO> carregarListaDeMargemProduto(int idLojaVR, int idLojaCliente) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                "select cdprod, perclucro "
                        + "from mercodb.saldos "
                        + "where loja = " + idLojaCliente
            )) {
            
                while (rst.next()) {
                    ProdutoVO oProduto = new ProdutoVO();                                         
                    oProduto.setIdDouble(rst.getDouble("cdprod"));
                    oProduto.setMargem(rst.getDouble("perclucro"));
                    
                    if (oProduto.getMargem() == 0) {
                        oProduto.recalcularMargem();
                    }
                    
                    vProduto.add(oProduto);
                }
            }
        }
        
        return vProduto;
    }
    
    public void importarListaMargemProduto(int idLojaVR, int idLojaCliente) throws Exception {
        ProdutoDAO produto = new ProdutoDAO();

        ProgressBar.setStatus("Carregando dados...Margem Produto...");
        List<ProdutoVO> vProduto = carregarListaDeMargemProduto(idLojaVR, idLojaCliente);
        if (!vProduto.isEmpty()) {
            produto.alterarMargemProduto(vProduto, idLojaVR);
        }
    }

    public List<ProdutoVO> carregarListaDePrecoProduto(int idLojaVR, int idLojaCliente) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                "select cdprod, precovenda "
                        + "from mercodb.saldos "
                        + "where loja = " + idLojaCliente
            )) {
            
                while (rst.next()) {
                    ProdutoVO oProduto = new ProdutoVO();
                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                    oProduto.getvComplemento().add(oComplemento);                      
                    oProduto.setIdDouble(rst.getDouble("cdprod"));
                    oComplemento.setPrecoVenda(rst.getDouble("precovenda"));
                    oComplemento.setPrecoDiaSeguinte(rst.getDouble("precovenda"));
                    oComplemento.setIdLoja(idLojaVR);

                    vProduto.add(oProduto);
                }
            }
        }
        
        return vProduto;
    }
    
    public void importarListaPrecoProduto(int idLojaVR, int idLojaCliente) throws Exception {
        ProdutoDAO produto = new ProdutoDAO();

        ProgressBar.setStatus("Carregando dados...Preço Produto...Loja " + idLojaVR + "...");
        List<ProdutoVO> vProduto = carregarListaDePrecoProduto(idLojaVR, idLojaCliente);
        if (!vProduto.isEmpty()) {
            produto.alterarPrecoProduto(vProduto, idLojaVR);
        }
    }

    public List<ProdutoVO> carregarListaDeCustoProduto(int idLojaVR, int idLojaCliente) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                "select cdprod, precocusto "
                        + "from mercodb.saldos "
                        + "where loja = " + idLojaCliente
            )) {
            
                while (rst.next()) {
                    ProdutoVO oProduto = new ProdutoVO();
                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                    oProduto.getvComplemento().add(oComplemento);                      
                    oProduto.setIdDouble(rst.getDouble("cdprod"));
                    oComplemento.setCustoComImposto(rst.getDouble("precocusto"));
                    oComplemento.setCustoSemImposto(rst.getDouble("precocusto"));
                    oComplemento.setIdLoja(idLojaVR);

                    vProduto.add(oProduto);
                }
            }
        }
        
        return vProduto;
    }
    
    public void importarListaCustoProduto(int idLojaVR, int idLojaCliente) throws Exception {
        ProdutoDAO produto = new ProdutoDAO();

        ProgressBar.setStatus("Carregando dados...Custo Produto...Loja " + idLojaVR + "...");
        List<ProdutoVO> vProduto = carregarListaDeCustoProduto(idLojaVR, idLojaCliente);
        if (!vProduto.isEmpty()) {
            produto.alterarCustoProduto(vProduto, idLojaVR);
        }
    }
    
    public List<ProdutoVO> carregarListaDeSituacaoCadastroProduto(int idLojaVR, int idLojaCliente) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                "select cdprod, situacao "
                        + "from mercodb.saldos "
                        + "where loja = " + idLojaCliente
            )) {
            
                while (rst.next()) {
                    ProdutoVO oProduto = new ProdutoVO();
                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                    oProduto.getvComplemento().add(oComplemento);                      
                    
                    oProduto.setIdDouble(rst.getDouble("cdprod"));
                    
                    if ("A".equals(rst.getString("situacao"))) {
                        oComplemento.setIdSituacaoCadastro(1);
                    } else {
                        oComplemento.setIdSituacaoCadastro(0);
                    }
                    
                    oComplemento.setIdLoja(idLojaVR);
                    vProduto.add(oProduto);
                }
            }
        }
        
        return vProduto;
    }

    public void importarListaSituacaoCadastroProduto(int idLojaVR, int idLojaCliente) throws Exception {
        ProdutoDAO produto = new ProdutoDAO();

        ProgressBar.setStatus("Carregando dados...Situação Cadastro Produto...Loja " + idLojaVR + "...");
        List<ProdutoVO> vProduto = carregarListaDeSituacaoCadastroProduto(idLojaVR, idLojaCliente);
        if (!vProduto.isEmpty()) {
            produto.alterarSituacaoCadastroProduto(vProduto, idLojaVR);
        }
    }

    public List<ProdutoVO> carregarListaDeEstoqueProduto(int idLojaVR, int idLojaCliente) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                "select cdprod, estminimo, estmaximo, quant "
                        + "from mercodb.saldos "
                        + "where loja = " + idLojaCliente
            )) {
            
                while (rst.next()) {
                    ProdutoVO oProduto = new ProdutoVO();
                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                    oProduto.getvComplemento().add(oComplemento);                      
                    
                    oProduto.setIdDouble(rst.getDouble("cdprod"));
                    oComplemento.setEstoque(rst.getDouble("quant"));
                    oComplemento.setEstoqueMinimo(rst.getDouble("estminimo"));
                    oComplemento.setEstoqueMaximo(rst.getDouble("estmaximo"));
                    
                    oComplemento.setIdLoja(idLojaVR);
                    vProduto.add(oProduto);
                }
            }
        }
        
        return vProduto;
    }
    
    public void importarListaDeEstoqueProduto(int idLojaVR, int idLojaCliente) throws Exception {
        ProdutoDAO produto = new ProdutoDAO();

        ProgressBar.setStatus("Carregando dados...Estoque Produto...Loja " + idLojaVR + "...");
        List<ProdutoVO> vProduto = carregarListaDeEstoqueProduto(idLojaVR, idLojaCliente);
        if (!vProduto.isEmpty()) {
            produto.alterarEstoqueProduto(vProduto, idLojaVR);
        }
    }

    public List<ProdutoVO> carregarListaDeCodigoBarraProduto(int idLojaVR, int idLojaCliente) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                "select cdprod, codbarra, multiplos, termvenda "
                        + "from mercodb.barrarel"
            )) {
            
                while (rst.next()) {
                    ProdutoVO oProduto = new ProdutoVO();
                    ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
                    ProdutoAutomacaoLojaVO oAutomacaoLoja = new ProdutoAutomacaoLojaVO();
                    oProduto.getvAutomacaoLoja().add(oAutomacaoLoja);
                    oProduto.getvAutomacao().add(oAutomacao);
                    
                    oProduto.setIdDouble(rst.getDouble("cdprod"));
                    oAutomacaoLoja.codigobarras = Long.parseLong(Utils.formataNumero(rst.getString("codbarra").trim()));
                    oAutomacaoLoja.qtdEmbalagem = (int) rst.getDouble("multiplos");
                    oAutomacaoLoja.precovenda = rst.getDouble("termvenda");
                    oAutomacaoLoja.id_loja = idLojaVR;
                    
                    oAutomacao.codigoBarras = Long.parseLong(Utils.formataNumero(rst.getString("codbarra").trim()));
                    oAutomacao.qtdEmbalagem = (int) rst.getDouble("multiplos");
                    
                    vProduto.add(oProduto);
                }
            }
        }
        
        return vProduto;
    }
    
    public void importarListaDeCodigoBarrasProduto(int idLojaVR, int idLojaCliente) throws Exception {
        ProdutoDAO produto = new ProdutoDAO();

        ProgressBar.setStatus("Carregando dados...Código Barras Produto...Loja " + idLojaVR + "...");
        List<ProdutoVO> vProduto = carregarListaDeCodigoBarraProduto(idLojaVR, idLojaCliente);
        if (!vProduto.isEmpty()) {
            produto.automacaoLoja = true;
            produto.addCodigoBarras(vProduto);
        }
    }
    
    @Override
    public List<FornecedorVO> carregarFornecedor() throws Exception {
        List<FornecedorVO> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select codigo, razsoc, nomfan, tipoender, numero, ender, complemen, ibge, bairro, "
                            + "munic, estado, cep, dddtel, telefone, dddfax, telefax, contato, dddcon, "
                            + "telcon, cgc, insest, email, endwww "
                            + "from mercodb.credor "
                            + "order by razsoc "
            )) {
                while (rst.next()) {
                    FornecedorVO oFornecedor = new FornecedorVO();
                    
                    Date datacadastro;
                    datacadastro = new Date(new java.util.Date().getTime()); 
                    
                    oFornecedor.setId(rst.getInt("codigo"));
                    oFornecedor.setCodigoanterior(rst.getInt("codigo"));
                    oFornecedor.setDatacadastro(datacadastro);
                    oFornecedor.setRazaosocial(rst.getString("razsoc").trim());
                    
                    if ((rst.getString("nomfan") != null) &&
                            (!rst.getString("nomfan").trim().isEmpty())) {
                        oFornecedor.setNomefantasia(rst.getString("nomfan").trim());
                    } else {
                        oFornecedor.setNomefantasia(rst.getString("razsoc").trim());
                    }
                    
                    oFornecedor.setEndereco((rst.getString("tipoender") == null ? "" : rst.getString("tipoender").trim()) + (rst.getString("ender") == null ? "0000000000" : rst.getString("ender")));
                    
                    if ((rst.getString("numero") != null) &&
                            (!rst.getString("numero").trim().isEmpty())) {
                        oFornecedor.setNumero(rst.getString("numero").trim());
                    } else {
                        oFornecedor.setNumero("");
                    }
                    
                    if ((rst.getString("complemen") != null) &&
                            (!rst.getString("complemen").trim().isEmpty())) {
                        oFornecedor.setComplemento(rst.getString("complemen").trim());
                    } else {
                        oFornecedor.setComplemento("");
                    }
                    
                    if ((rst.getString("bairro") != null) &&
                            (!rst.getString("bairro").trim().isEmpty())) {
                        oFornecedor.setBairro(rst.getString("bairro").trim());
                    } else {
                        oFornecedor.setBairro("");
                    }
                    
                    if (rst.getInt("ibge") != 0) {
                        oFornecedor.setId_municipio(Utils.stringToInt(rst.getString("ibge")));
                    } else {
                        oFornecedor.setId_municipio(Utils.retornarMunicipioIBGEDescricao(
                                Utils.acertarTexto(rst.getString("munic")), rst.getString("estado").toUpperCase()));
                    }
                    
                    oFornecedor.setCep(Utils.stringToLong((rst.getString("cep") == null ? "0" : rst.getString("cep").trim()), 0));                    
                    
                    if ((rst.getString("estado") != null) &&
                            (!rst.getString("estado").trim().isEmpty())) {
                        oFornecedor.setId_estado(Utils.getEstadoPelaSigla(rst.getString("estado").trim()));
                    } else {
                        oFornecedor.setId_estado(Global.idEstado);
                    }
                    
                    oFornecedor.setEnderecocobranca("");
                    oFornecedor.setNumerocobranca("");
                    oFornecedor.setBairrocobranca("");
                    oFornecedor.setId_municipiocobranca(Utils.stringToInt("-1"));
                    oFornecedor.setCepcobranca(0);
                    oFornecedor.setId_estadocobranca(0);
                    
                    oFornecedor.setTelefone((rst.getString("dddtel") == null ? "" : rst.getString("dddtel").trim())
                            +  (rst.getString("telefone") == null ? "" : rst.getString("telefone").trim()));
                    oFornecedor.setInscricaoestadual(( rst.getString("insest") == null ? "" : rst.getString("insest") ) );
                    oFornecedor.setCnpj(Utils.stringToLong((rst.getString("cgc") == null ? "0" : rst.getString("cgc").trim()), 0));
                    oFornecedor.setId_tipoinscricao(String.valueOf(oFornecedor.getCnpj()).length() > 11 ? 0 : 1);
                    
                    
                    oFornecedor.setObservacao("");
                    oFornecedor.setTelefone2((rst.getString("dddcon") == null ? "" : rst.getString("dddcon").trim()) + (rst.getString("telcon") == null ? "" : rst.getString("telcon").trim()));
                    oFornecedor.setFax((rst.getString("dddfax") == null ? "" : rst.getString("dddfax").trim()) + (rst.getString("telcon") == null ? "" : rst.getString("telefax")));
                    oFornecedor.setEmail((rst.getString("email") == null ? "" : rst.getString("email")));
                    oFornecedor.setId_situacaocadastro(1);
                    
                    result.add(oFornecedor);
                }
            }
        }
        
        return result;
    }
    
    @Override
    public void importarFornecedor() throws Exception{
        ProgressBar.setStatus("Carregando dados...Fornecedor...");
        List<FornecedorVO> vFornecedor = carregarFornecedor();

        if (Global.compararCnpj) {
            new FornecedorDAO().salvarCnpj(vFornecedor);
        } else {
            new FornecedorDAO().salvar(vFornecedor);
        }
    }

    @Override
    public List<ProdutoFornecedorVO> carregarProdutoFornecedor() throws Exception {
        List<ProdutoFornecedorVO> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select cdprod, cdfornec, codigo, porcaixa "
                            + "from mercodb.estforns "
                            + "where cdprod is not null "
                            + "and cdfornec is not null "
            )) {
                while (rst.next()) {
                    Date dataAlteracao;
                    dataAlteracao = new Date(new java.util.Date().getTime()); 
                    ProdutoFornecedorVO oProdutoFornecedor = new ProdutoFornecedorVO();
                    oProdutoFornecedor.setId_fornecedor(rst.getInt("cdfornec"));
                    oProdutoFornecedor.setId_produtoDouble(Double.parseDouble(Utils.formataNumero(rst.getString("cdprod"))));
                    oProdutoFornecedor.setCodigoexterno((rst.getString("codigo") == null ? "" : rst.getString("codigo").trim()));
                    oProdutoFornecedor.setQtdembalagem((int) rst.getDouble("porcaixa"));
                    oProdutoFornecedor.setDataalteracao(dataAlteracao);
                    result.add(oProdutoFornecedor);
                }
            }
        }
        
        return result;
    }

    @Override
    public void importarProdutoFornecedor() throws Exception{
        ProgressBar.setStatus("Carregando dados...Produto Fornecedor...");
        List<ProdutoFornecedorVO> vProdutoFornecedor = carregarProdutoFornecedor();

        new ProdutoFornecedorDAO().salvar2(vProdutoFornecedor);
    }
    
    public void importarAcertarEnderecoFornecedor() throws Exception{
        ProgressBar.setStatus("Carregando dados...Endereço Fornecedor...");
        List<FornecedorVO> vFornecedor = carregarFornecedor();

        if (Global.compararCnpj) {
            new FornecedorDAO().salvarCnpj(vFornecedor);
        } else {
            new FornecedorDAO().acertarEndereco(vFornecedor);
        }
    }

    public void importarAcertarTelefoneFornecedor() throws Exception{
        ProgressBar.setStatus("Carregando dados...Telefone Fornecedor...");
        List<FornecedorVO> vFornecedor = carregarFornecedor();

        if (Global.compararCnpj) {
            new FornecedorDAO().salvarCnpj(vFornecedor);
        } else {
            new FornecedorDAO().alterarTelefone(vFornecedor);
        }
    }
    
    @Override
    public List<ClientePreferencialVO> carregarCliente(int idLojaCliente) throws Exception {
        List<ClientePreferencialVO> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {                        
            try (ResultSet rst = stm.executeQuery(
                    "select codigo, razsoc, nomfan, tipoender, ender, numero, complemen, ibge, bairro, "
                            + "munic, estado, cep, telcom, dddtel, telefone, dddfax, telefax, insest, cgc, "
                            + "dtnasc, obs, email, endwww, emptrab, endemp, bairroemp, municemp, estadoemp, "
                            + "cepemp, dddemp, telemp, cargo, profissao, estcivil, sexo, saldo "
                            + "from mercodb.clientes "
            )) {
                while (rst.next()) {                    
                    ClientePreferencialVO oClientePreferencial = new ClientePreferencialVO();

                    oClientePreferencial.setId(rst.getInt("codigo"));
                    oClientePreferencial.setCodigoanterior(rst.getInt("codigo"));
                    oClientePreferencial.setNome(rst.getString("razsoc"));
                    oClientePreferencial.setEndereco((rst.getString("tipoender") == null ? "" : rst.getString("tipoender").trim()) +  
                            (rst.getString("ender") == null ? "" : rst.getString("ender").trim()));
                    oClientePreferencial.setNumero((rst.getString("numero") == null ? "" : rst.getString("numero").trim()));
                    oClientePreferencial.setComplemento((rst.getString("complemen") == null ? "" : rst.getString("complemen").trim()));
                    oClientePreferencial.setBairro((rst.getString("bairro") == null ? "" : rst.getString("bairro").trim()));
                    oClientePreferencial.setId_estado(rst.getString("estado") == null ? Global.idEstado : Utils.retornarEstadoDescricao(rst.getString("estado")));
                    
                    if (rst.getInt("ibge") != 0) {
                        oClientePreferencial.setId_municipio(rst.getInt("ibge") == 0 ? Global.idMunicipio : rst.getInt("ibge"));
                    } else {
                        oClientePreferencial.setId_municipio(Utils.retornarMunicipioIBGEDescricao(
                                (rst.getString("munic") == null ? "" : rst.getString("munic").trim()), (rst.getString("estado") == null ? "" : rst.getString("estado"))));
                    }
                    
                    oClientePreferencial.setCep(Utils.stringToInt(rst.getString("cep") == null ? String.valueOf(Global.Cep) : rst.getString("cep")));
                    oClientePreferencial.setTelefone((rst.getString("dddtel").trim() == null ? "" : rst.getString("dddtel").trim()) +  
                            (rst.getString("telefone") == null ? "000000000" : rst.getString("telefone").trim()));
                    
                    oClientePreferencial.setCelular("");
                    oClientePreferencial.setInscricaoestadual(rst.getString("insest") == null ? "" : rst.getString("insest").trim());
                    oClientePreferencial.setCnpj((rst.getString("cgc") == null ? "-1" : rst.getString("cgc").trim()));
                    oClientePreferencial.setId_tipoinscricao(String.valueOf(oClientePreferencial.getCnpj()).length() > 11 ? 0 : 1);
                    oClientePreferencial.setSexo(1);
                    oClientePreferencial.setDataresidencia("1990/01/01");
                    oClientePreferencial.setDatacadastro("");
                    oClientePreferencial.setEmail((rst.getString("email") == null ? "" : rst.getString("email")));
                    oClientePreferencial.setValorlimite(rst.getDouble("saldo"));
                    oClientePreferencial.setFax((rst.getString("dddfax") == null ? "" : rst.getString("dddfax").trim()) + 
                            (rst.getString("telefax") == null ? "" : rst.getString("telefax").trim()));
                    
                    oClientePreferencial.setBloqueado(false);
                    oClientePreferencial.setId_situacaocadastro(1);
                    
                    oClientePreferencial.setTelefone2("");
                    oClientePreferencial.setObservacao((rst.getString("obs") == null ? "" : rst.getString("obs").trim()));
                    oClientePreferencial.setDatanascimento(rst.getString("dtnasc"));
                    oClientePreferencial.setEmpresa((rst.getString("emptrab") == null ? "" : rst.getString("emptrab").trim()));
                    oClientePreferencial.setEnderecoempresa((rst.getString("endemp") == null ? "" : rst.getString("endemp").trim()));
                    oClientePreferencial.setBairroempresa((rst.getString("bairroemp") == null ? "" : rst.getString("bairroemp").trim()));
                    oClientePreferencial.setId_municipioempresa(Utils.retornarMunicipioIBGEDescricao(
                                (rst.getString("municemp") == null ? "" : rst.getString("municemp").trim()), (rst.getString("estadoemp") == null ? "" : rst.getString("estadoemp"))));
                    oClientePreferencial.setCepempresa(Utils.stringToInt(rst.getString("cepemp") == null ? String.valueOf(Global.Cep) : rst.getString("cepemp")));
                    oClientePreferencial.setTelefoneempresa((rst.getString("dddemp") == null ? "" : rst.getString("dddemp").trim()) + 
                            (rst.getString("telemp") == null ? "" : rst.getString("telemp").trim()));
                    oClientePreferencial.setCargo((rst.getString("cargo") == null ? "" : rst.getString("cargo").trim()));                    
                    oClientePreferencial.setOrgaoemissor(null);                  
                    result.add(oClientePreferencial);
                }                
            }
        }
        return result;
    }   
    
    @Override
    public void importarClientePreferencial(int idLojaVR, int idLojaCliente) throws Exception{
        ProgressBar.setStatus("Carregando dados...Cliente Preferencial...");
        List<ClientePreferencialVO> vClientePreferencial = carregarCliente(idLojaCliente);
        new PlanoDAO().salvar(idLojaVR);
        
        new ClientePreferencialDAO().salvar(vClientePreferencial, idLojaVR, idLojaCliente);
    }
    
    @Override
    public List<ReceberCreditoRotativoVO> carregarReceberCreditoRotativo(int idLojaVR, int idLojaCliente)
            throws Exception {
        List<ReceberCreditoRotativoVO> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select cdcliente, caixa, nrdoc, (valor - valrec) valorConta, "
                            + "emissao, vencto, obs "
                            + "from mercodb.contrec "
                            + "where receb is null "
                            + "and loja = " + idLojaCliente + " "
                            + "and tipodoc = 'C' "
                            + "union all "
                            + "select cdcliente, caixa, nrdoc, (valor - valrec) valorConta, "
                            + "emissao, vencto, obs "
                            + "from mercodb.contrec "
                            + "where valrec < valor "
                            + "and loja = " + idLojaCliente + " "
                            + "and tipodoc = 'C' "                    
            )) {
                while (rst.next()) {
                    ReceberCreditoRotativoVO oReceber = new ReceberCreditoRotativoVO();
                    oReceber.setId_loja(idLojaVR);
                    oReceber.setId_clientepreferencial(rst.getInt("cdcliente"));
                    oReceber.setValor(rst.getDouble("valorConta"));
                    oReceber.setDataemissao(rst.getString("emissao").trim());
                    oReceber.setDatavencimento(rst.getString("vencto").trim());
                    oReceber.setNumerocupom(rst.getInt("nrdoc"));
                    oReceber.setEcf(rst.getInt("caixa"));
                    oReceber.setObservacao((rst.getString("obs") == null ? "" : rst.getString("obs").trim()));
                    result.add(oReceber);
                }
            }
        }
        return result;
    }
    
    @Override
    public void importarReceberCreditoRotativo(int idLojaVR, int idLojaCliente) throws Exception{
        ProgressBar.setStatus("Carregando dados...Receber Credito Rotativo...");
        List<ReceberCreditoRotativoVO> vReceberCreditoRotativo = carregarReceberCreditoRotativo(idLojaVR, idLojaCliente);
        
        new ReceberCreditoRotativoDAO().salvarComCodicao(vReceberCreditoRotativo, idLojaVR);
    }
    
    /********************************************/
    private int retornarPisCofinsDebito(int cst) {
        int retorno = 1;
        
        if ((cst == 1) || (cst == 50)) {
            retorno = 0;
        } else if ((cst == 2) || (cst == 60)) {
            retorno = 5;
        } else if ((cst == 3) || (cst == 51)) {
            retorno = 6;
        } else if ((cst == 4) || (cst == 70)) {
            retorno = 3;
        } else if ((cst == 5) || (cst == 75)) {
            retorno = 2;
        } else if ((cst == 6) || (cst == 73)) {
            retorno = 7;
        } else if ((cst == 7) || (cst == 71)) {
            retorno = 1;
        } else if ((cst == 8) || (cst == 74)) {
            retorno = 8;
        } else if ((cst == 49) || (cst == 98) || (cst == 99)) {
            retorno = 9;
        }
        
        return retorno;
    }

    private int retornarPisCofinsCredito(int cst) {
        int retorno = 13;
        
        if ((cst == 1) || (cst == 50)) {
            retorno = 12;
        } else if ((cst == 2) || (cst == 60)) {
            retorno = 17;
        } else if ((cst == 3) || (cst == 51)) {
            retorno = 18;
        } else if ((cst == 4) || (cst == 70)) {
            retorno = 15;
        } else if ((cst == 5) || (cst == 75)) {
            retorno = 14;
        } else if ((cst == 6) || (cst == 73)) {
            retorno = 19;
        } else if ((cst == 7) || (cst == 71)) {
            retorno = 13;
        } else if ((cst == 8) || (cst == 74)) {
            retorno = 20;
        } else if ((cst == 49) || (cst == 98) || (cst == 99)) {
            retorno = 21;
        }
        
        return retorno;
    }
}
