package vrimplantacao.dao.interfaces;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import vrframework.classe.ProgressBar;
import vrframework.classe.Util;
import vrimplantacao.classe.ConexaoDBF;
import vrimplantacao.classe.Global;
import vrimplantacao.dao.cadastro.FamiliaProdutoDAO;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.dao.cadastro.MercadologicoDAO;
import vrimplantacao.dao.cadastro.NcmDAO;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.dao.cadastro.ProdutoDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao.vo.vrimplantacao.CestVO;
import vrimplantacao.vo.vrimplantacao.ClientePreferencialVO;
import vrimplantacao.vo.vrimplantacao.CodigoAnteriorVO;
import vrimplantacao.vo.vrimplantacao.FamiliaProdutoVO;
import vrimplantacao.vo.vrimplantacao.MercadologicoVO;
import vrimplantacao.vo.vrimplantacao.NcmVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAliquotaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAutomacaoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoComplementoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoVO;
import vrimplantacao.vo.vrimplantacao.ReceberCreditoRotativoVO;

public class SicsDAO extends AbstractIntefaceDao {

    @Override
    public List<FamiliaProdutoVO> carregarFamiliaProduto() throws Exception {
        List<FamiliaProdutoVO> result = new ArrayList<>();
        
        try (Statement stm = ConexaoDBF.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select codsub, nomsub from subcod order by codsub "
            )) {
                while (rst.next()) {
                    FamiliaProdutoVO familiaVO = new FamiliaProdutoVO();
                    
                    int id = rst.getInt("codsub");
                    familiaVO.setId(id);
                    familiaVO.setIdLong(id);
                    familiaVO.setDescricao(rst.getString("nomsub"));
                    
                    result.add(familiaVO);
                }
            }
        }
        
        return result;
    }

    @Override
    public void importarFamiliaProduto() throws Exception {
        ProgressBar.setStatus("Carregando dados...Familia Produto...");
        List<FamiliaProdutoVO> vFamiliaProduto = carregarFamiliaProduto();

        FamiliaProdutoDAO dao = new FamiliaProdutoDAO();
        dao.gerarCodigo = true;
        dao.salvar(vFamiliaProduto);
    }
    
    
    
    @Override
    public List<MercadologicoVO> carregarMercadologico(int nivel) throws Exception {
        
        class SubFamilia {
            int id;
            String descricao;            
        }
        
        class Familia {
            int id;
            String descricao;
            List<SubFamilia> subitem = new ArrayList<>();
        }
        
        Map<Integer, Familia> familias = new LinkedHashMap<>();        
        
        try (Statement stm = ConexaoDBF.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select codfam, nomfam from familia order by codfam"
            )) {
                while (rst.next()) {
                    Familia fam = new Familia();
                    fam.id = rst.getInt("codfam");
                    fam.descricao = rst.getString("nomfam");
                    familias.put(fam.id, fam);
                }
            }
            
            try (ResultSet rst = stm.executeQuery(
                    "select subcod, subnom from subfam order by subcod"
            )) {
                while (rst.next()) {
                    SubFamilia sub = new SubFamilia();
                    String codigo = rst.getString("subcod");
                    int codigoFamilia = Integer.parseInt(codigo.substring(0, 2));
                    sub.id = Integer.parseInt(codigo);
                    sub.descricao = rst.getString("subnom");
                    if (familias.containsKey(codigoFamilia)) {
                        familias.get(codigoFamilia).subitem.add(sub);
                    }/* else {
                        Familia fam = new Familia();
                        fam.id = rst.getInt("subcod");
                        fam.descricao = rst.getString("subnom");
                        familias.put(fam.id, fam);
                    }*/
                }
            }
        }
        
        List<MercadologicoVO> result = new ArrayList<>();
        
        if (nivel == 1) {
            for (Familia fam: familias.values()) {                
                MercadologicoVO vo = new MercadologicoVO();
                vo.setMercadologico1(fam.id);
                vo.setDescricao(fam.descricao);
                vo.setNivel(nivel);
                result.add(vo);
            }
        } else if (nivel > 1) {
            for (Familia fam: familias.values()) {                
                if (fam.subitem.isEmpty()) {
                    MercadologicoVO vo = new MercadologicoVO();
                    vo.setMercadologico1(fam.id);
                    vo.setMercadologico2(1);
                    if (nivel > 2) {
                        vo.setMercadologico3(1);
                    }
                    vo.setDescricao(fam.descricao);
                    vo.setNivel(nivel);
                    result.add(vo);
                } else {
                    for (SubFamilia sub: fam.subitem) {
                        MercadologicoVO vo = new MercadologicoVO();
                        vo.setMercadologico1(fam.id);
                        vo.setMercadologico2(sub.id);
                        if (nivel > 2) {
                            vo.setMercadologico3(1);
                        }
                        vo.setDescricao(sub.descricao);
                        vo.setNivel(nivel);
                        result.add(vo);
                    }
                }
                
            }
        }
       
        return result;
    }

    @Override
    public void importarMercadologico() throws Exception {
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
    public Map<Long, ProdutoVO> carregarEanProduto(int idLojaVR, int idLojaCliente) throws Exception {
        Map<Long, ProdutoVO> result = new LinkedHashMap<>();
        
        try (Statement stm = ConexaoDBF.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select codigo, codbar, unid, qtemb from produto order by codigo"
            )) {
            
                while (rst.next()) {

                    ProdutoVO oProduto = new ProdutoVO();                                      
                    ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
                    oProduto.getvAutomacao().add(oAutomacao);
                    
                    oProduto.setIdDouble(rst.getInt("codigo"));  
                    oAutomacao.setCodigoBarras(Utils.stringToLong(rst.getString("codbar")));
                    oAutomacao.setIdTipoEmbalagem(Utils.converteTipoEmbalagem(rst.getString("unid")));
                    //oAutomacao.setQtdEmbalagem(rst.getInt("qtemb"));
                    oAutomacao.setQtdEmbalagem(1);
                    
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
    public void importarProduto(int idLojaVR, int idLojaCliente) throws Exception {
        ProgressBar.setStatus("Carregando dados...Produtos.....");
      
        Map<Double, ProdutoVO> aux = new LinkedHashMap<>();
        for (ProdutoVO vo: carregarListaDeProdutos(idLojaVR, idLojaCliente)) {
            Double id = vo.getIdDouble() > 0 ? vo.getIdDouble() : vo.getId();
            aux.put(id, vo);
        }

        List<LojaVO> vLoja = new LojaDAO().carregar();

        ProgressBar.setMaximum(aux.size());
        
        List<ProdutoVO> balanca = new ArrayList<>();
        List<ProdutoVO> normais = new ArrayList<>();
        for (ProdutoVO prod: aux.values()) {
            if (prod.eBalanca) {
                balanca.add(prod);
            } else {
                normais.add(prod);
            }
        }

        ProdutoDAO produto = new ProdutoDAO();
        produto.implantacaoExterna = true;
        produto.usarCodigoBalancaComoID = true;
        
        ProgressBar.setStatus("Carregando dados...Produtos de balança.....");
        ProgressBar.setMaximum(balanca.size());
        produto.salvar(balanca, idLojaVR, vLoja);
        
        ProgressBar.setStatus("Carregando dados...Produtos normais.....");
        ProgressBar.setMaximum(normais.size());
        produto.salvar(normais, idLojaVR, vLoja);
    }
    
    @Override
    public List<ProdutoVO> carregarListaDeProdutos(int idLojaVR, int idLojaCliente) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();

        try (Statement stm = ConexaoDBF.getConexao().createStatement()) {               
            try (ResultSet rst = stm.executeQuery(
                "Select\n" +
                "  p.codigo as id,\n" +
                "  p.descr as descricaocompleta,\n" +
                "  p.nomred as  descricaoreduzida,\n" +
                "  p.descr as descricaogondola,\n" +
                "  1 as id_situacaocadastro,\n" +
                "  p.dtultre as datacadastro,\n" +
                "  p.familia as merc1,\n" +
                "  p.subfam2 as merc2,\n" +
                "  p.cf as ncm,\n" +
                "  p.subcod as familia,\n" +
                "  p.margem,\n" +
                "  p.codbar,\n" +
                "  p.valid as validade,\n" +
                "  p.unid as id_tipoembalagem,\n" +
                "  p.qtemb as qtdembalagem,\n" +
                "  p.prvenda as preco,\n" +
                "  p.prcompra as custo,\n" +
                "  p.qtatual as estoque,\n" +
                "  p.estmin as estoquemin,\n" +
                "  p.estmax as estoquemax,\n" +
                "  trib.tribdr as aliq_impr,\n" +
                "  trib.aliq,\n" +
                "  trib.redicm\n" +
                "From\n" +
                "  PRODUTO p\n" +
                "  left join depto trib on p.depto = trib.coddep\n" +
                "order by p.codigo;"
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
                    Date datacadastro = rst.getDate("datacadastro");
                    if (datacadastro != null) {
                        oProduto.setDataCadastro(Util.formatDataGUI(datacadastro));
                    } else {
                        oProduto.setDataCadastro(Util.formatDataGUI(new Date(new java.util.Date().getTime())));
                    }

                    oProduto.setMercadologico1(rst.getInt("merc1"));
                    oProduto.setMercadologico2(rst.getInt("merc2"));
                    oProduto.setMercadologico3(1);
                    oProduto.setMercadologico4(0);
                    oProduto.setMercadologico5(0);
                    
                    String ncm = rst.getString("ncm");
                    if ((ncm != null)
                            && (!ncm.isEmpty())
                            && (ncm.trim().length() > 5)) {
                        NcmVO oNcm = new NcmDAO().validar(ncm.trim());

                        oProduto.setNcm1(oNcm.ncm1);
                        oProduto.setNcm2(oNcm.ncm2);
                        oProduto.setNcm3(oNcm.ncm3);
                    } else {
                        oProduto.setNcm1(9701);
                        oProduto.setNcm2(90);
                        oProduto.setNcm3(0);
                    }
                    
                    CestVO cest = new CestVO();
                    oProduto.setCest1(cest.getCest1());
                    oProduto.setCest2(cest.getCest2());
                    oProduto.setCest3(cest.getCest3());
                    

                    String familia = rst.getString("familia");
                    oProduto.setIdFamiliaProduto(familia != null ? (int) Utils.stringToDouble(familia) : -1);
                    //oProduto.setIdFamiliaProduto(-1);
                    oProduto.setMargem(rst.getDouble("margem"));
                    oProduto.setQtdEmbalagem(1);              
                    oProduto.setIdComprador(1);
                    oProduto.setIdFornecedorFabricante(1);
                    
                    String strCodigo = Utils.formataNumero(rst.getString("codbar"), "-2");                    
                    long codigoBarras;
                    if (strCodigo.startsWith("2")) {
                        int endIndex = strCodigo.length() - 1;
                        int startIndex = 1;
                        
                        codigoBarras = Long.parseLong(strCodigo.substring(startIndex, endIndex));
                    } else {
                        codigoBarras = Long.parseLong(strCodigo);
                    }
                    /**
                     * Aparentemente o sistema utiliza o próprio id para produtos de balança.
                     */ 
                    ProdutoBalancaVO produtoBalanca = null;
                    if (codigoBarras <= Integer.MAX_VALUE) {
                        produtoBalanca = produtosBalanca.get((int) codigoBarras);
                    } 
                    
                    if (produtoBalanca != null) {
                        oAutomacao.setCodigoBarras(codigoBarras);                          
                        oProduto.setValidade(produtoBalanca.getValidade() > 1 ? produtoBalanca.getValidade() : rst.getInt("validade"));
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
                        
                        oAutomacao.setCodigoBarras(-2);
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
                                   
                    double preco = rst.getDouble("preco");
                    oComplemento.setPrecoVenda(preco);
                    oComplemento.setPrecoDiaSeguinte(preco);
                    double custo = rst.getDouble("custo");
                    oComplemento.setCustoComImposto(custo);
                    oComplemento.setCustoSemImposto(custo);
                    oComplemento.setIdLoja(idLojaVR);
                    double estoque = rst.getDouble("estoque");
                    oComplemento.setIdSituacaoCadastro(oProduto.getIdSituacaoCadastro());
                    oComplemento.setEstoque(estoque);
                    oComplemento.setEstoqueMinimo(rst.getDouble("estoquemin"));
                    oComplemento.setEstoqueMaximo(rst.getDouble("estoquemax"));                   

                    int cst;
                    double aliq = rst.getDouble("aliq");
                    double red = rst.getDouble("redicm");
                    
                    String aliq_impr = rst.getString("aliq_impr");
                    switch(aliq_impr != null ? aliq_impr.substring(0,1) : "I") {
                        case "I": cst = 40; break;
                        case "F": cst = 60; break;
                        case "0": 
                            if (red > 0 ) {
                                cst = 20;
                            } else {
                                cst = 0;
                            }
                            break;
                        default: cst = 40;
                    }
                    
                    oAliquota.setIdEstado(35);                    
                    oAliquota.setIdAliquotaDebito(Utils.getAliquotaICMS("SP", cst, aliq, red));
                    oAliquota.setIdAliquotaCredito(Utils.getAliquotaICMS("SP", cst, aliq, red));
                    oAliquota.setIdAliquotaDebitoForaEstado(Utils.getAliquotaICMS("SP", cst, aliq, red));
                    oAliquota.setIdAliquotaCreditoForaEstado(Utils.getAliquotaICMS("SP", cst, aliq, red));
                    oAliquota.setIdAliquotaDebitoForaEstadoNF(Utils.getAliquotaICMS("SP", cst, aliq, red));
                    oAliquota.setIdAliquotaConsumidor(Utils.getAliquotaICMS("SP", cst, aliq, red, true));

                    
                    oCodigoAnterior.setCodigoanterior(oProduto.getId());
                    oCodigoAnterior.setMargem(oProduto.getMargem());
                    oCodigoAnterior.setPrecovenda(oComplemento.getPrecoVenda());
                    oCodigoAnterior.setBarras(-2);
                    oCodigoAnterior.setReferencia((int) oProduto.getId());
                    oCodigoAnterior.setNcm(ncm);
                    oCodigoAnterior.setId_loja(idLojaVR);
                    oCodigoAnterior.setPiscofinsdebito(-1);
                    oCodigoAnterior.setPiscofinscredito(-1);
                    oCodigoAnterior.setNaturezareceita(-1);
                    oCodigoAnterior.setRef_icmsdebito(aliq_impr);

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
    public List<ClientePreferencialVO> carregarCliente(int idLojaCliente) throws Exception {
        List<ClientePreferencialVO> result = new ArrayList<>();

        try (Statement stm = ConexaoDBF.getConexao().createStatement()) {                        
            try (ResultSet rst = stm.executeQuery(
                    "select * from CLIENTE order by codigo"
            )) {
                while (rst.next()) {                    
                    ClientePreferencialVO oClientePreferencial = new ClientePreferencialVO();

                    int id = rst.getInt("codigo");
                    oClientePreferencial.setId(id);
                    oClientePreferencial.setCodigoanterior(id);
                    oClientePreferencial.setNome(rst.getString("razao"));
                    oClientePreferencial.setEndereco(rst.getString("endereco"));
                    oClientePreferencial.setNumero(rst.getString("numero"));
                    oClientePreferencial.setBairro(rst.getString("bairro"));
                    String uf = Utils.acertarTexto(rst.getString("estado"));
                    String cidade = Utils.acertarTexto(rst.getString("cidade"));
                    oClientePreferencial.setId_estado(Utils.acertarTexto(uf).equals("") ? Global.idEstado : Utils.getEstadoPelaSigla(uf));
                    
                    oClientePreferencial.setId_municipio((Utils.retornarMunicipioIBGEDescricao(
                            Utils.acertarTexto(cidade), uf)  == 0 ? Global.idMunicipio : 
                            Utils.retornarMunicipioIBGEDescricao(
                            Utils.acertarTexto(cidade), uf)));
                    String estcivil = Utils.acertarTexto("civil");
                    switch (estcivil) {
                        case "S": oClientePreferencial.id_tipoestadocivil = 1; break;
                        case "C": oClientePreferencial.id_tipoestadocivil = 2; break;
                        case "O": oClientePreferencial.id_tipoestadocivil = 5; break;
                        case "V": oClientePreferencial.id_tipoestadocivil = 3; break;
                        default: oClientePreferencial.id_tipoestadocivil = 0;
                    }
                    
                    oClientePreferencial.setCep(Utils.formatCep(rst.getString("cep")));
                    oClientePreferencial.setTelefone(rst.getString("fone"));
                    oClientePreferencial.setNomepai(rst.getString("pai"));
                    oClientePreferencial.setNomemae(rst.getString("mae"));
                    oClientePreferencial.setInscricaoestadual(rst.getString("iest"));
                    oClientePreferencial.setCnpj(rst.getString("cgc"));
                    oClientePreferencial.setVencimentocreditorotativo(rst.getInt("dtpag"));
                    oClientePreferencial.setValorlimite(rst.getDouble("limite"));
                    oClientePreferencial.setId_tipoinscricao(String.valueOf(oClientePreferencial.getCnpj()).length() > 11 ? 0 : 1);
                    oClientePreferencial.setSexo(Utils.acertarTexto(rst.getString("sexo")).equals("F") ? 0 : 1);
                    oClientePreferencial.setDataresidencia("1990/01/01");
                    oClientePreferencial.setDatanascimento(rst.getDate("dtnasc"));
                    oClientePreferencial.setDatacadastro(rst.getDate("dtcad"));
                    oClientePreferencial.setEmail(rst.getString("email"));
                    oClientePreferencial.setFax(rst.getString("fax"));
                    
                    if ("L".equals(Utils.acertarTexto(rst.getString("libera")))) {
                        oClientePreferencial.setBloqueado(false);
                    } else {
                        oClientePreferencial.setBloqueado(true);
                    }
                    
                    oClientePreferencial.setId_situacaocadastro(1);
                    
                    oClientePreferencial.setObservacao("IMPORTADO VR");             

                    result.add(oClientePreferencial);
                }                
            }
        }
        return result;
    }   
    
    @Override
    public List<ReceberCreditoRotativoVO> carregarReceberCreditoRotativo(int idLojaVR, int idLojaCliente)
            throws Exception {
        List<ReceberCreditoRotativoVO> result = new ArrayList<>();

        try (Statement stm = ConexaoDBF.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select rcnro, data, rcven, rccli, rcval, rcvalpag, "
                            + "rcjur, rctotjur, rcdif, rcsta, obs, rcdatpag, "
                            + "rcemp, cupom, pedido, pdv from cadrec "
                            + "where rcsta = 0 order by data"                    
            )) {
                while (rst.next()) {
                    ReceberCreditoRotativoVO oReceber = new ReceberCreditoRotativoVO();
                    oReceber.setId_loja(idLojaVR);
                    oReceber.setId_clientepreferencial(rst.getInt("rccli"));
                    oReceber.setValor(rst.getDouble("rcval"));
                    oReceber.setDataemissao(rst.getDate("data"));
                    oReceber.setDatavencimento(rst.getDate("rcven"));
                    int pedido = rst.getInt("pedido");
                    oReceber.setNumerocupom(pedido);
                    oReceber.setEcf(rst.getInt("pdv"));
                    String obs = Utils.acertarTexto(rst.getString("obs"));
                    oReceber.setObservacao("IMPORTADO VR   " + obs);
                    result.add(oReceber);
                }
            }
        }
        return result;
    }

    public void importarFamiliaNosProdutos(int idLojaVR, int idLojaCliente) throws Exception {
        ProgressBar.setStatus("Carregando dados...Produtos...Corrigindo Família");
      
        Map<Double, ProdutoVO> aux = new LinkedHashMap<>();
        for (ProdutoVO vo: carregarListaDeProdutos(idLojaVR, idLojaCliente)) {
            Double id = vo.getIdDouble() > 0 ? vo.getIdDouble() : vo.getId();
            aux.put(id, vo);
        }
        ProdutoDAO produto = new ProdutoDAO();
        
        ProgressBar.setStatus("Carregando dados...");
        ProgressBar.setMaximum(aux.size());
        produto.corrigirInformacoes(new ArrayList<>(aux.values()), idLojaVR, idLojaCliente, ProdutoDAO.OpcaoProduto.FAMILIA_PRODUTO);
    }
}
