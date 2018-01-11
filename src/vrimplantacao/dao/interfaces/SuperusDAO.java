package vrimplantacao.dao.interfaces;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import vrframework.classe.ProgressBar;
import vrframework.classe.Util;
import vrframework.remote.ItemComboVO;
import vrimplantacao.classe.ConexaoOracle;
import vrimplantacao.classe.Global;
import vrimplantacao.dao.cadastro.CestDAO;
import vrimplantacao.dao.cadastro.ClientePreferencialDAO;
import vrimplantacao.dao.cadastro.MercadologicoDAO;
import vrimplantacao.dao.cadastro.NcmDAO;
import vrimplantacao.dao.cadastro.PlanoDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.CestVO;
import vrimplantacao.vo.vrimplantacao.ClientePreferencialVO;
import vrimplantacao.vo.vrimplantacao.CodigoAnteriorVO;
import vrimplantacao.vo.vrimplantacao.FamiliaProdutoVO;
import vrimplantacao.vo.vrimplantacao.FornecedorVO;
import vrimplantacao.vo.vrimplantacao.MercadologicoVO;
import vrimplantacao.vo.vrimplantacao.NcmVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAliquotaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAutomacaoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoComplementoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoFornecedorVO;
import vrimplantacao.vo.vrimplantacao.ProdutoVO;
import vrimplantacao.vo.vrimplantacao.ReceberCreditoRotativoVO;

/**
 *
 * @author Leandro
 */
public class SuperusDAO extends AbstractIntefaceDao {
    public boolean corrigirIdNovoMilenio = false;
    public int idEmpresa = -1;

    @Override
    public List<ItemComboVO> carregarLojasCliente() throws Exception {
        List<ItemComboVO> result = new ArrayList<>();
        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select l.codigo id, l.codigo || ' - ' || p.nome descricao from pessoas p join loja l on l.codigo = p.codigo order by l.codigo"
            )) {
                while (rst.next()) {
                    result.add(new ItemComboVO(rst.getInt("id"), rst.getString("descricao")));
                }
            }
        }
        return result;
    }
    
    
    
    @Override
    public List<FamiliaProdutoVO> carregarFamiliaProduto() throws Exception {
        List<FamiliaProdutoVO> result = new ArrayList<>();
        
        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "  p.codigorea id,\n" +
                    "  p2.nome1 descricao\n" +
                    "from\n" +
                    "(select p.codigorea from produtos p where p.codigorea > 0 group by codigorea) p\n" +
                    "join produtos p2 on p2.codigo = p.codigorea\n" +
                    "order by\n" +
                    "  p.codigorea"
            )) {
                while (rst.next()) {
                    FamiliaProdutoVO familiaVO = new FamiliaProdutoVO();
                    
                    familiaVO.setId(rst.getInt("id"));
                    familiaVO.setIdLong(rst.getLong("id"));
                    familiaVO.setDescricao(rst.getString("descricao"));
                    
                    result.add(familiaVO);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<MercadologicoVO> carregarMercadologico(int nivel) throws Exception {
        List<MercadologicoVO> result = new ArrayList<>();
        
        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "  s.codigo merc1,\n" +
                    "  s.nome merc1_desc,\n" +
                    "  coalesce(g.codigo,1) merc2,\n" +
                    "  coalesce(g.nome, s.nome) merc2_desc,\n" +
                    "  coalesce(sg.CODIGO,1) merc3,\n" +
                    "  coalesce(sg.nome, coalesce(g.nome, s.nome)) merc3_desc\n" +
                    "from \n" +
                    "  SETOR s\n" +
                    "  left join GRUPO g on g.SETOR = s.CODIGO\n" +
                    "  left join SUBGRUPO sg on g.CODIGO = sg.GRUPO and s.CODIGO = sg.SETOR\n" +
                    "order by\n" +
                    "  s.codigo"
            )) {
                while (rst.next()) {
                    MercadologicoVO merc = new MercadologicoVO();
                    merc.setMercadologico1(rst.getInt("merc1"));
                    merc.setDescricao(rst.getString("merc1_desc"));
                    if (nivel > 1) {
                        merc.setMercadologico2(rst.getInt("merc2"));
                        merc.setDescricao(rst.getString("merc2_desc"));
                    }
                    if (nivel > 2) {
                        merc.setMercadologico3(rst.getInt("merc3"));
                        merc.setDescricao(rst.getString("merc3_desc"));
                    }
                    merc.setNivel(nivel);
                    
                    result.add(merc);
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

        try (Statement stm = ConexaoOracle.createStatement()) {   
            String uf = "SP";
            
            try (ResultSet rst = stm.executeQuery("select estado from pessoas where codigo = " + idLojaCliente)) {
                if (rst.next()) {
                  uf = rst.getString("estado");
                }
            }
            
            try (ResultSet rst = stm.executeQuery(
                "select\n" +
                "  p.codigo id,\n" +
                "  p.nome descricaocompleta,\n" +
                "  p.nome2 descricaoreduzida,\n" +
                "  p.nome descricaogondola,\n" +
                "  case p.inativo when 'S' then 0 else 1 end as id_situacaocadastro,\n" +
                "  p.flaginc datacadastro,\n" +
                "  p.setor mercadologico1,\n" +
                "  p.grupo mercadologico2,\n" +
                "  p.subgrupo mercadologico3,\n" +
                "  ncm.nome ncm,\n" +
                "  null as cest,\n" +
                "  case p.CODIGOREA when 0 then null else p.CODIGOREA end as id_familia,\n" +
                "  preco.lucro margem,\n" +
                "  case p.pesvar when 'S' then 1 else 0 end as e_balanca,\n" +
                "  -2 as ean,\n" +
                "  1 as qtdemabalagem,\n" +
                "  p.numerodiasvalidade validade,\n" +
                "  case when p.pesvar = 'S' and p.tipo = 'P' then 4 else 0 end as id_tipoembalagem,\n" +
                "  case when p.pesvar = 'S' and p.tipo = 'U' then 1 else 0 end pesavel,\n" +
                "  p.pesoliquido,\n" +
                "  p.peso pesobruto,\n" +
                "  tp.cst_pis piscofins_cst_sai,\n" +
                "  tp.cstpisent piscofins_cst_ent,\n" +
                "  tc.codigo piscofins_natrec,\n" +
                "  preco.precotabelavenda preco,\n" +
                "  preco.custobruto custosemimposto,\n" +
                "  preco.custoliquido custocomimposto,\n" +
                "  estoq.quantidade estoque,\n" +
                "  estoq.minimo,\n" +
                "  estoq.maximo,\n" +
                "  icms.cst icms_cst,\n" +
                "  icms.ALIQUOTA icms_aliq,\n" +
                "  icms.REDUCAO icms_reducao\n" +
                "from\n" +
                "  produtos p\n" +
                "  join produtos_impostos imp on p.codigo = imp.codigo\n" +
                "  left join classificacao ncm on imp.classificacao = ncm.codigo\n" +
                "  join produtos_precos preco on p.codigo = preco.codigo and preco.loja =  " + idLojaCliente + " \n" +
                "  join (select a.codigoprodutostipos, a.cstpisent, a.cst_pis, a.TabelaCodigo from \n" +
                "          produtos_tipos_vigencia a\n" +
                "          join (select \n" +
                "              codigoprodutostipos, \n" +
                "              max(iniciovigencia) iniciovigencia\n" +
                "            from produtos_tipos_vigencia group by codigoprodutostipos) b\n" +
                "            on a.codigoprodutostipos = b.codigoprodutostipos and\n" +
                "            a.iniciovigencia = b.iniciovigencia) tp on p.tipoproduto = tp.codigoprodutostipos\n" +
                "  left join tabela_codigo tc on tp.TabelaCodigo = tc.Chave\n" +
                "  join produtos_estoque estoq on estoq.CODIGO = p.codigo and estoq.loja =  " + idLojaCliente + " \n" +
                "  join aliquota icms on imp.icms = icms.codigo\n" +
                "order by\n" +
                "  e_balanca desc, p.codigo"
            )) {
                
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
                                      
                    oProduto.setIdDouble(rst.getDouble("id"));
                    oProduto.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    oProduto.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    oProduto.setDescricaoGondola(rst.getString("descricaogondola"));
                    oProduto.setIdSituacaoCadastro(rst.getInt("id_Situacaocadastro"));
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
                    

                    oProduto.setIdFamiliaProduto(rst.getString("id_familia") != null ? rst.getInt("id_familia") : -1);
                    oProduto.setMargem(rst.getDouble("margem"));
                    oProduto.setQtdEmbalagem(1);              
                    oProduto.setIdComprador(1);
                    oProduto.setIdFornecedorFabricante(1);
                    

                    if (rst.getBoolean("e_balanca") && rst.getLong("ean") <= 999999) {
                        oProduto.setValidade(rst.getInt("validade"));
                        oProduto.eBalanca = true;                                       
                        
                        oAutomacao.setCodigoBarras(-1); 
                        oAutomacao.setIdTipoEmbalagem(rst.getInt("id_tipoembalagem"));
                        oProduto.setPesavel(rst.getInt("id_tipoembalagem") != 4);
                        
                        oCodigoAnterior.setCodigobalanca(rst.getInt("ean"));
                        oCodigoAnterior.setE_balanca(true);
                    } else {                                                
                        oProduto.setValidade(rst.getInt("validade"));
                        oProduto.setPesavel(false); 
                        oProduto.eBalanca = false;
                        
                        oAutomacao.setCodigoBarras(-2); 
                        oAutomacao.setIdTipoEmbalagem(rst.getInt("id_tipoembalagem"));
                        
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
                    
                    oProduto.setIdTipoPisCofinsDebito(Utils.retornarPisCofinsDebito(rst.getInt("piscofins_cst_sai")));
                    oProduto.setIdTipoPisCofinsCredito(Utils.retornarPisCofinsCredito(rst.getInt("piscofins_cst_ent")));
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

                    
                    oAliquota.setIdEstado(Utils.getEstadoPelaSigla(uf));
                    oAliquota.setIdAliquotaDebito(Utils.getAliquotaICMS(uf, rst.getInt("icms_cst"), rst.getDouble("icms_aliq"), rst.getDouble("icms_reducao")));
                    oAliquota.setIdAliquotaCredito(Utils.getAliquotaICMS(uf, rst.getInt("icms_cst"), rst.getDouble("icms_aliq"), rst.getDouble("icms_reducao")));
                    oAliquota.setIdAliquotaDebitoForaEstado(Utils.getAliquotaICMS(uf, rst.getInt("icms_cst"), rst.getDouble("icms_aliq"), rst.getDouble("icms_reducao")));
                    oAliquota.setIdAliquotaCreditoForaEstado(Utils.getAliquotaICMS(uf, rst.getInt("icms_cst"), rst.getDouble("icms_aliq"), rst.getDouble("icms_reducao")));
                    oAliquota.setIdAliquotaDebitoForaEstadoNF(Utils.getAliquotaICMS(uf, rst.getInt("icms_cst"), rst.getDouble("icms_aliq"), rst.getDouble("icms_reducao")));

                    
                    oCodigoAnterior.setCodigoanterior(rst.getLong("id"));
                    oCodigoAnterior.setMargem(oProduto.getMargem());
                    oCodigoAnterior.setPrecovenda(oComplemento.getPrecoVenda());
                    oCodigoAnterior.setBarras(rst.getLong("ean"));
                    oCodigoAnterior.setReferencia((int) oProduto.getId());
                    oCodigoAnterior.setNcm(rst.getString("ncm"));
                    oCodigoAnterior.setId_loja(idLojaVR);
                    oCodigoAnterior.setPiscofinsdebito(rst.getInt("piscofins_cst_sai"));
                    oCodigoAnterior.setPiscofinscredito(rst.getInt("piscofins_cst_ent"));
                    oCodigoAnterior.setNaturezareceita(rst.getInt("piscofins_natrec"));
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
    public Map<Long, ProdutoVO> carregarEanProduto(int idLojaVR, int idLojaCliente) throws Exception {
        Map<Long, ProdutoVO> result = new LinkedHashMap<>();
        
        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "  p.codigo id,\n" +
                    "  case p.pesvar when 'S' then 1 else 0 end as e_balanca,\n" +
                    "  case when p.pesvar = 'S' and p.tipo = 'P' then 4 else 0 end as id_tipoembalagem,\n" +
                    "  case when p.pesvar = 'S' and p.tipo = 'U' then 1 else 0 end pesavel,\n" +
                    "  ean.codbarra ean,\n" +
                    "  ean.quantidade qtdembalagem,\n" +
                    "  p.numerodiasvalidade validade\n" +
                    "from\n" +
                    "  produtos p\n" +
                    "  join produtos_ean ean on p.codigo = ean.codigo and ean.codbarra > 0\n" +
                    "order by\n" +
                    "  e_balanca desc, p.codigo"
            )) {
            
                while (rst.next()) {

                    ProdutoVO oProduto = new ProdutoVO();                                      
                    ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
                    oProduto.getvAutomacao().add(oAutomacao);
                    
                    oProduto.setIdDouble(rst.getDouble("id"));  
                    oAutomacao.setCodigoBarras(Utils.stringToLong(rst.getString("ean")));
                    oAutomacao.setIdTipoEmbalagem(Utils.converteTipoEmbalagem(rst.getString("id_tipoembalagem")));
                    oAutomacao.setQtdEmbalagem(rst.getInt("qtdembalagem") < 1 ? 1 : rst.getInt("qtdembalagem"));
                    
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
    public List<FornecedorVO> carregarFornecedor() throws Exception {
        List<FornecedorVO> result = new ArrayList<>();

        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery("select estado, CODIBGEDV from pessoas where codigo = " + idEmpresa)) {
                if (rst.next()) {
                    Global.idEstado = Utils.getEstadoPelaSigla(rst.getString("estado"));
                    if (rst.getInt("CODIBGEDV") > 0) {
                        Global.idMunicipio = rst.getInt("CODIBGEDV");
                    }
                }
            }
            
            try (ResultSet rst = stm.executeQuery(                    
                "select\n" +
                "  p.codigo id,\n" +
                "  p.FLAGINC datacadastro,\n" +
                "  p.razao,\n" +
                "  p.nome fantasia,\n" +
                "  coalesce(p.logradouro, p.endereco) endereco,\n" +
                "  coalesce(p.NRO,'0') numero,\n" +
                "  p.COMPLEMENTO,\n" +
                "  p.BAIRRO,\n" +
                "  p.CIDADE,\n" +
                "  p.CODIBGEDV,\n" +
                "  p.ESTADO,\n" +
                "  p.CEP,\n" +
                "  trim((select case when coalesce(trim(ddd),'') = '' then telefone else ddd||telefone end as asd from telefones where codigo = p.codigo and lower(tipo) = 'residencial' and rownum = 1 )) fone1,\n" +
                "  trim((select case when coalesce(trim(ddd),'') = '' then telefone else ddd||telefone end as asd from telefones where codigo = p.codigo and lower(tipo) = 'comercial' and rownum = 1)) fone2,\n" +
                "  trim((select case when coalesce(trim(ddd),'') = '' then telefone else ddd||telefone end as asd from telefones where codigo = p.codigo and lower(tipo) = 'celular' and rownum = 1)) celular,\n" +
                "  coalesce(coalesce(case p.IE when 'ISENTO' then null else p.IE end, p.rg),'ISENTO') inscricaoestadual,\n" +
                "  coalesce(p.cnpj, p.cpf) cnpj,\n" +
                "  f.OBSERVACAO,\n" +
                "  f.OBSERVACAOPEDIDO,\n" +
                "  f.EMAIL,\n" +
                "  case when f.FORADELINHA = 'S' then 0 else 1 end as id_situacaocadastro\n" +
                "from\n" +
                "  PESSOAS p\n" +
                "  join fornecedor f on p.codigo = f.codigo\n" +
                "order by p.codigo"
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
                    if (rst.getInt("CODIBGEDV") > 0) {
                        oFornecedor.setId_municipio(rst.getInt("CODIBGEDV"));
                    } else {
                        oFornecedor.setId_municipio(Utils.retornarMunicipioIBGEDescricao(rst.getString("cidade"), rst.getString("estado")));
                    }                    
                    oFornecedor.setId_estado(Utils.getEstadoPelaSigla(rst.getString("estado")));
                    oFornecedor.setCep(Utils.stringToLong(rst.getString("cep"), 0));
                    if (rst.getString("fone1") != null && !"".equals(rst.getString("fone1"))) {
                        oFornecedor.setTelefone(rst.getString("fone1"));
                        oFornecedor.setTelefone2(rst.getString("fone2"));
                    } else {
                        oFornecedor.setTelefone(rst.getString("fone2"));
                    }
                    oFornecedor.setCelular(rst.getString("celular"));
                    oFornecedor.setInscricaoestadual(rst.getString("inscricaoestadual"));
                    oFornecedor.setCnpj(Utils.stringToLong(rst.getString("cnpj"), 0));
                    oFornecedor.setId_tipoinscricao(String.valueOf(oFornecedor.getCnpj()).length() > 11 ? 0 : 1);
                    oFornecedor.setObservacao("IMPORTADO VR");
                    oFornecedor.setEmail(rst.getString("email"));
                    oFornecedor.setId_situacaocadastro(rst.getInt("id_situacaocadastro"));
                    oFornecedor.setId_tipoindicadorie();
                    
                    result.add(oFornecedor);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ProdutoFornecedorVO> carregarProdutoFornecedor() throws Exception {
        List<ProdutoFornecedorVO> result = new ArrayList<>();
        
        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery("select estado from pessoas where codigo = 1")) {
                if (rst.next()) {
                  Global.idEstado = Utils.getEstadoPelaSigla(rst.getString("estado"));
                }
            }
            try (ResultSet rst = stm.executeQuery(
                "select\n" +
                "  pr.codigo id_produto,\n" +
                "  pr.fornecedor id_fornecedor,\n" +
                "  pr.referencia,\n" +
                "  coalesce(ean.quantidade, 1) quantidade,\n" +
                "  coalesce(ean.embalagem, 'UN') embalagem\n" +
                "from \n" +
                "  PRODUTOS_REFERENCIAS pr\n" +
                "  left join (select \n" +
                "      CODIGO, embalagem, sum(QUANTIDADE) quantidade \n" +
                "    from \n" +
                "      PRODUTOS_EAN \n" +
                "    where \n" +
                "      comprapadrao = 'S' \n" +
                "    group by \n" +
                "      codigo, embalagem) ean on ean.codigo = pr.codigo\n" +
                "order by \n" +
                "  pr.codigo,\n" +
                "  pr.fornecedor"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorVO vo = new ProdutoFornecedorVO();
                    vo.setId_produto(rst.getInt("id_produto"));
                    vo.setId_produtoDouble(rst.getInt("id_produto"));
                    vo.setId_fornecedor(rst.getInt("id_fornecedor"));
                    vo.setId_fornecedorDouble(rst.getInt("id_fornecedor"));
                    vo.setId_estado(Global.idEstado);
                    vo.setCodigoexterno(rst.getString("referencia"));
                    vo.idTipoEmbalagem = Utils.converteTipoEmbalagem(rst.getString("embalagem"));
                    vo.setQtdembalagem((int) Utils.truncar2(rst.getDouble("quantidade"), 0));
                    Calendar cal = new GregorianCalendar();
                    vo.setDataalteracao(new Date(cal.getTimeInMillis()));
                    result.add(vo);
                }
            }        
        }
        
        return result;
    }

    @Override
    public List<ClientePreferencialVO> carregarCliente(int idLojaCliente) throws Exception {
        List<ClientePreferencialVO> vClientePreferencial = new ArrayList<>();

        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(               
                "select\n" +
                "  p.codigo id,\n" +
                "  p.NOME nome,\n" +
                "  p.ENDERECO res_endereco,\n" +
                "  p.NRO res_numero,\n" +
                "  p.COMPLEMENTO res_complemento,\n" +
                "  p.BAIRRO res_bairro,\n" +
                "  p.ESTADO res_uf,\n" +
                "  p.CIDADE res_cidade,\n" +
                "  p.CODIBGEDV,\n" +
                "  p.CEP res_cep,\n" +
                "  trim((select case when coalesce(trim(ddd),'') = '' then telefone else ddd||telefone end as asd from telefones where codigo = p.codigo and lower(tipo) = 'residencial' and rownum = 1 )) fone1,\n" +
                "  trim((select case when coalesce(trim(ddd),'') = '' then telefone else ddd||telefone end as asd from telefones where codigo = p.codigo and lower(tipo) = 'comercial' and rownum = 1)) fone2,\n" +
                "  trim((select case when coalesce(trim(ddd),'') = '' then telefone else ddd||telefone end as asd from telefones where codigo = p.codigo and lower(tipo) = 'celular' and rownum = 1)) celular,\n" +
                "  coalesce(coalesce(case p.IE when 'ISENTO' then null else p.IE end, p.rg),'ISENTO') inscricaoestadual,\n" +
                "  coalesce(p.cnpj, p.cpf) cnpj,\n" +
                "  case when c.SEXO = 'F' then 0 else 1 end as sexo,\n" +
                "  p.FLAGINC datacadastro,\n" +
                "  p.EMAIL,\n" +
                "  c.PRAZODIAS,\n" +
                "  c.limite,\n" +
                "  case when c.LISTANEGRA = 'S' then 1 else 0 end as bloqueado,\n" +
                "  c.NASCIMENTO datanascimento,\n" +
                "  c.NOMEPAI,\n" +
                "  c.nomemae,\n" +
                "  c.limite,\n" +
                "  c.EMPRESA,\n" +
                "  c.TELEFONEEMPRESA telempresa,\n" +
                "  c.SALARIO,\n" +
                "  c.PROFISSAO cargo,\n" +
                "  c.ESTADOCIVIL,\n" +
                "  c.CONJUGE,\n" +
                "  c.ORGAOEMISSOR\n" +
                "from\n" +
                "  PESSOAS p\n" +
                "  join CLIENTES c on p.codigo = c.codigo\n" +
                "order by p.codigo"
            )) {
                while (rst.next()) {                    
                    ClientePreferencialVO oClientePreferencial = new ClientePreferencialVO();

                    if (corrigirIdNovoMilenio) {
                        int id = rst.getInt("id");
                        if (id >= 700) {
                            oClientePreferencial.setId(9999999);
                        } else {
                            oClientePreferencial.setId(id);
                        }
                    } else {
                        oClientePreferencial.setId(rst.getInt("id"));
                    }
                    oClientePreferencial.setCodigoanterior(rst.getInt("id"));
                    oClientePreferencial.setNome(rst.getString("nome"));
                    oClientePreferencial.setEndereco(rst.getString("res_endereco"));
                    oClientePreferencial.setNumero(rst.getString("res_numero"));
                    oClientePreferencial.setComplemento(rst.getString("res_complemento"));
                    oClientePreferencial.setBairro(rst.getString("res_bairro"));
                    if (rst.getInt("CODIBGEDV") > 0) {
                        oClientePreferencial.setId_municipio(rst.getInt("CODIBGEDV"));
                    } else {
                        oClientePreferencial.setId_municipio(Utils.retornarMunicipioIBGEDescricao(rst.getString("res_cidade"), rst.getString("res_uf")));
                    }                 
                    oClientePreferencial.setId_estado(Utils.getEstadoPelaSigla(rst.getString("res_uf")));
                    oClientePreferencial.setCep(rst.getString("res_cep"));
                    oClientePreferencial.setCelular(rst.getString("celular"));                    
                    if (rst.getString("fone1") != null && !"".equals(rst.getString("fone1"))) {
                        oClientePreferencial.setTelefone(rst.getString("fone1"));
                        oClientePreferencial.setTelefone2(rst.getString("fone2"));
                    } else {
                        oClientePreferencial.setTelefone(rst.getString("fone2"));
                    } 
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
                    oClientePreferencial.setValorlimite(rst.getDouble("limite"));
                    oClientePreferencial.setBloqueado(rst.getBoolean("bloqueado"));
                    oClientePreferencial.setId_situacaocadastro(1);
                    oClientePreferencial.setObservacao("IMPORTADO VR  - limite: " + String.format("%.02f", rst.getFloat("limite")));
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

    @Override
    public List<ReceberCreditoRotativoVO> carregarReceberCreditoRotativo(int idLojaVR, int idLojaCliente) throws Exception {
        List<ReceberCreditoRotativoVO> result = new ArrayList<>();
        
        try (Statement stm = ConexaoOracle.createStatement()) {
            try ( ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "  ROT.CODIGO id_clientepreferencial,\n" +
                    "  coalesce(c.cnpj, c.cpf) cnpj,\n" +
                    "  ROT.LOJA id_loja,\n" +
                    "  ROT.EMISSAO dataemissao,\n" +
                    "  rot.cupom,\n" +
                    "  ROT.VALOR,\n" +
                    "  ROT.HISTORICO observacao,\n" +
                    "  ROT.VENCIMENTO,\n" +
                    "  ROT.PDV ecf\n" +
                    "FROM RECEBER_CONTAS  ROT  \n" +
                    "INNER JOIN PESSOAS C ON C.CODIGO = ROT.CODIGO\n" +
                    "where rot.chaverecebimento = 0 and rot.loja = " + idLojaCliente
            )) {
                while (rst.next()) {
                    ReceberCreditoRotativoVO oReceberCreditoRotativo = new ReceberCreditoRotativoVO();

                    oReceberCreditoRotativo.setId_clientepreferencial(rst.getInt("id_clientepreferencial"));
                    oReceberCreditoRotativo.setCnpjCliente(Utils.stringToLong(rst.getString("cnpj")));
                    oReceberCreditoRotativo.setId_loja(idLojaCliente);
                    oReceberCreditoRotativo.setDataemissao(rst.getDate("dataemissao"));
                    oReceberCreditoRotativo.setNumerocupom(Utils.stringToInt(rst.getString("cupom")));
                    oReceberCreditoRotativo.setValor(rst.getDouble("VALOR"));
                    oReceberCreditoRotativo.setObservacao("IMPORTADO VR " + rst.getString("observacao"));
                    oReceberCreditoRotativo.setDatavencimento(rst.getDate("VENCIMENTO"));
                    oReceberCreditoRotativo.setEcf(Utils.stringToInt(rst.getString("ecf")));

                    result.add(oReceberCreditoRotativo);
                }
            }
        }
        
        return result;
    }

    @Override
    public void importarClientePreferencial(int idLojaVR, int idLojaCliente) throws Exception {
        ProgressBar.setStatus("Carregando dados...Cliente Preferencial...");
        List<ClientePreferencialVO> vClientePreferencial = carregarCliente(idLojaCliente);
        new PlanoDAO().salvar(idLojaVR);
        ClientePreferencialDAO dao = new ClientePreferencialDAO();
        if (corrigirIdNovoMilenio) {
            dao.manterID = true;
            dao.utilizarCodigoAnterior = true;
        }
        dao.salvar(vClientePreferencial, idLojaVR, idLojaCliente);
    }

    public void importarLimiteCliente(int idLojaVR, int idLojaCliente) throws Exception {
        ProgressBar.setStatus("Carregando dados...Limite...");
        List<ClientePreferencialVO> vClientePreferencial = carregarCliente(idLojaCliente);
        new PlanoDAO().salvar(idLojaVR);
        ClientePreferencialDAO dao = new ClientePreferencialDAO();
        dao.alterarValorLimte(vClientePreferencial);
    }
    
}
