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
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao.classe.Global;
import vrimplantacao.dao.cadastro.CestDAO;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.dao.cadastro.NcmDAO;
import vrimplantacao.dao.cadastro.ProdutoDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao.vo.vrimplantacao.CestVO;
import vrimplantacao.vo.vrimplantacao.ClientePreferencialVO;
import vrimplantacao.vo.vrimplantacao.CodigoAnteriorVO;
import vrimplantacao.vo.vrimplantacao.FornecedorVO;
import vrimplantacao.vo.vrimplantacao.NcmVO;
import vrimplantacao.vo.vrimplantacao.OfertaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAliquotaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAutomacaoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoComplementoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoFornecedorVO;
import vrimplantacao.vo.vrimplantacao.ProdutoVO;
import vrimplantacao.vo.vrimplantacao.ReceberCreditoRotativoVO;

public class SofgceDAO extends AbstractIntefaceDao {
    
    private int getIdPisCofinsSaida(int index) {
        /* Table tblIndicadorPisCofins
        1	1-Tributado
        2	2-Monofasico
        3	3-Aliq Zero
        4	4-Subs.Tributária
        5	5-Isento
        6	6-Sem Incidencia
        7	7-Suspenso
        8	8-Presumido                
        */        
        switch (index) {
            case 1: return 0;
            case 2: return 3;
            case 3: return 7;
            case 4: return 2;
            case 5: return 1;
            case 6: return 8;
            default: return 9;
        }
    }

    @Override
    public List<ItemComboVO> carregarLojasCliente() throws Exception {
        List<ItemComboVO> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	IDSetor id,\n" +
                    "	cast(IDSetor as varchar(10)) + ' - ' + Setor descricao\n" +
                    "from \n" +
                    "	tblSetor \n" +
                    "order by IDSetor"
            )) {
                while (rst.next()) {
                    result.add(new ItemComboVO(rst.getInt("id"), rst.getString("descricao")));
                }
            }        
        }        
        return result;
    }

    @Override
    public List<ProdutoVO> carregarListaDeProdutos(int idLojaVR, int idLojaCliente) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {               
            try (ResultSet rst = stm.executeQuery(
                "select m.IDEstado, uf.estSigla Sigla \n" +
                "from \n" +
                "	tblEmpresa e \n" +
                "	join tblMunicipio m on e.IDMunicipio = m.IDMunicipio\n" +
                "	join tblEstado uf on m.IDEstado = uf.IDEstado"
            )) {
                if (rst.next()) {
                    Global.idEstado = rst.getInt("IDEstado");
                    Global.ufEstado = rst.getString("Sigla");
                }
            }
            try (ResultSet rst = stm.executeQuery(
                "declare @idsetor integer;\n" +
                "\n" +
                "set @idsetor = " + idLojaCliente + ";\n" +
                "\n" +
                "select\n" +
                "	p.IDProduto id,\n" +
                "	p.ProdDescricao descricaocompleta,\n" +
                "	p.ProdDescricao descricaoreduzida,\n" +
                "	p.ProdDescricao descricaogondola,\n" +
                "	case p.ProdInativo when 1 then 0 else 1 end as id_situacaocadastro,\n" +
                "	p.ProdDtCadastro datacadastro,\n" +
                "	p.IDSecao mercadologico1,\n" +
                "	p.IDProdutoGrupo mercadologico2,\n" +
                "	1 as mercadologico3,\n" +
                "	ncm.IDNCM ncm,\n" +
                "	fisco.IDCEST cest,\n" +
                "	-2 as ean,\n" +
                "	p.ProdEnviarParaBalanca e_balanca,\n" +
                "	0 validade,\n" +
                "	un.Unidade id_tipoembalagem,\n" +
                "	p.ProdSetPesoBruto pesobruto,\n" +
                "	p.ProdSetPesoLiquido pesoliquido,\n" +
                "	natop.IDCstPisPf piscofins_cst_sai,\n" +
                "	natop.IDCstPisPj piscofins_cst_ent,\n" +
                "	ncm.IDNatReceitaPisCofins piscofins_natrec,\n" +
                "	p.ProdTabPVenda preco,\n" +
                "	p.ProdSetPCusto custocomimposto,\n" +
                "	p.ProdSetPCusto custosemimposto,\n" +
                "	p.ProdFisQuant estoque,\n" +
                "	p.ProdSetQuantMinima minimo,\n" +
                "	natop.IDCstIcmsDE1 icms_cst,\n" +
                "	natop.IcmsAliquotaDE1 icms_aliq,\n" +
                "	natop.IcmsReducaoBcDE1 icms_reducao,\n" +
                "	fisco.IDIndicadorIcms,\n" +
                "	fisco.IDIndicadorPisCofins,\n" +
                "	fisco.IDEcfAliquota\n" +
                "from \n" +
                "	tblproduto p\n" +
                "	left join tblprodutofiscal fisco on \n" +
                "		p.IDProduto = fisco.IDProduto \n" +
                "		and fisco.IDSetor = @idsetor\n" +
                "	left join tblncm ncm on \n" +
                "		fisco.IDNCM = ncm.IDNCM\n" +
                "	left join tblunidade un on \n" +
                "		p.IDUnidadeComercial = un.IDUnidade\n" +
                "	left join tblProdutoFiscalNatzOp natop on \n" +
                "		natop.idproduto = p.idproduto \n" +
                "		and natop.IDSetor = @idsetor \n" +
                "		and IDNaturezaOperacao in (950)\n" +
                "	left join tblProdutoCustos custo on\n" +
                "		p.IDProduto = custo.IDProduto and\n" +
                "		custo.IDSetor = @idsetor\n" +
                "order by\n" +
                "	p.IDProduto"
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
                    String strIdProduto = Utils.formataNumero(rst.getString("id"));
                                      
                    oProduto.setIdDouble(Double.parseDouble(strIdProduto));
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
                    } else {
                        oProduto.setNcm1(9701);
                        oProduto.setNcm2(90);
                        oProduto.setNcm3(0);
                    }
                    
                    CestVO cest = CestDAO.parse(rst.getString("cest"));                    
                    oProduto.setCest1(cest.getCest1());
                    oProduto.setCest2(cest.getCest2());
                    oProduto.setCest3(cest.getCest3());
                    

                    oProduto.setIdFamiliaProduto(-1);
                    //oProduto.setMargem(rst.getDouble(""));
                    oProduto.setQtdEmbalagem(1);              
                    oProduto.setIdComprador(1);
                    oProduto.setIdFornecedorFabricante(1);
                    

                    if (rst.getInt("e_balanca") == 1 && strIdProduto.startsWith("2")) {
                        long idBalanca = Long.parseLong(strIdProduto.substring(1, 5));
                        
                        oAutomacao.setCodigoBarras(idBalanca);
                        oProduto.setValidade(rst.getInt("validade"));
                        oProduto.eBalanca = true;
                        
                        oAutomacao.setIdTipoEmbalagem(Utils.converteTipoEmbalagem(rst.getString("id_tipoembalagem")));
                        
                        oCodigoAnterior.setCodigobalanca((int) idBalanca);
                        oCodigoAnterior.setE_balanca(true);
                    } else {
                        oAutomacao.setCodigoBarras(-2);                                      
                        oProduto.setValidade(rst.getInt("validade"));
                        oProduto.setPesavel(false); 
                        oProduto.eBalanca = false;
                        
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
                    
                    oProduto.setIdTipoPisCofinsDebito(getIdPisCofinsSaida(rst.getInt("IDIndicadorPisCofins")));
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
                    //oComplemento.setEstoqueMaximo(rst.getDouble("maximo"));                   

                    oAliquota.setIdEstado(Global.idEstado);
                    oAliquota.setIdAliquotaDebito(Utils.getAliquotaICMS(Global.ufEstado, rst.getInt("icms_cst"), rst.getDouble("icms_aliq"), rst.getDouble("icms_reducao")));
                    oAliquota.setIdAliquotaCredito(Utils.getAliquotaICMS(Global.ufEstado, rst.getInt("icms_cst"), rst.getDouble("icms_aliq"), rst.getDouble("icms_reducao")));
                    oAliquota.setIdAliquotaDebitoForaEstado(Utils.getAliquotaICMS(Global.ufEstado, rst.getInt("icms_cst"), rst.getDouble("icms_aliq"), rst.getDouble("icms_reducao")));
                    oAliquota.setIdAliquotaCreditoForaEstado(Utils.getAliquotaICMS(Global.ufEstado, rst.getInt("icms_cst"), rst.getDouble("icms_aliq"), rst.getDouble("icms_reducao")));
                    oAliquota.setIdAliquotaDebitoForaEstadoNF(Utils.getAliquotaICMS(Global.ufEstado, rst.getInt("icms_cst"), rst.getDouble("icms_aliq"), rst.getDouble("icms_reducao")));

                    
                    oCodigoAnterior.setCodigoanterior(Double.parseDouble(strIdProduto));
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
        
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	p.IDProduto id_produto,\n" +
                    "	case when coalesce(ltrim(rtrim(p.IDProdutoCB)),'') = '' then p.IDProduto else p.IDProdutoCB end as ean,\n" +
                    "	un.Unidade id_tipoembalagem\n" +
                    "from\n" +
                    "	tblProduto p\n" +
                    "	left join tblunidade un on \n" +
                    "       p.IDUnidadeComercial = un.IDUnidade\n" +
                    "order by\n" +
                    "	p.IDProduto"
            )) {
            
                while (rst.next()) {

                    ProdutoVO oProduto = new ProdutoVO();                                      
                    ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
                    oProduto.getvAutomacao().add(oAutomacao);
                    
                    oProduto.setIdDouble(Utils.stringToDouble(rst.getString("id_produto")));  
                    oAutomacao.setCodigoBarras((long) Utils.stringToDouble(rst.getString("ean")));
                    oAutomacao.setIdTipoEmbalagem(Utils.converteTipoEmbalagem(rst.getString("id_tipoembalagem")));
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
        List<ProdutoVO> listaDeProdutos = carregarListaDeProdutos(idLojaVR, idLojaCliente);
        for (ProdutoVO vo: listaDeProdutos) {
            Double id = vo.getIdDouble() > 0 ? vo.getIdDouble() : vo.getId();
            aux.put(id, vo);
        }
        
        List<ProdutoVO> eBalanca = new ArrayList<>();
        List<ProdutoVO> eComum = new ArrayList<>();
        for (ProdutoVO vo: aux.values()) {
            if (vo.eBalanca) {
                eBalanca.add(vo);
            } else {
                eComum.add(vo);
            }
        }

        List<LojaVO> vLoja = new LojaDAO().carregar();

        ProgressBar.setMaximum(aux.size());

        ProdutoDAO produto = new ProdutoDAO();
        produto.implantacaoExterna = true;
        produto.usarCodigoBalancaComoID = true;
        ProgressBar.setStatus("Gravando balança.....");
        produto.salvar(eBalanca, idLojaVR, vLoja);
        ProgressBar.setStatus("Gravando comuns.....");
        produto.salvar(eComum, idLojaVR, vLoja);
    }    
    
    @Override
    public List<OfertaVO> carregarOfertas(int idLojaVR, int idLojaCliente) throws Exception{
        List<OfertaVO> ofertas = new ArrayList<>();
        
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	p.IDProduto id_produto,\n" +
                    "	cast('27/10/2016' as datetime) datainicio,\n" +
                    "	case when p.ProdSetTerminoPromocao is null then cast('8/11/2016' as datetime) else p.ProdSetTerminoPromocao end as datatermino,\n" +
                    "	p.ProdTabPPromocao precooferta\n" +
                    "from\n" +
                    "	tblProduto p\n" +
                    "where\n" +
                    "	p.ProdSetPromocao = 1"
            )) {
                while (rst.next()) {
                    OfertaVO vo = new OfertaVO();
                    vo.setId_loja(idLojaVR);
                    vo.setId_produtoDouble(rst.getDouble("id_produto"));
                    vo.setDatainicio(rst.getString("datainicio"));
                    vo.setDatatermino(rst.getString("datatermino"));
                    vo.setPrecooferta(rst.getDouble("precooferta"));
                    ofertas.add(vo);
                }
            }
        }
        
        return ofertas;
    }
    
    @Override
    public List<ClientePreferencialVO> carregarCliente(int idLojaCliente) throws Exception {
        List<ClientePreferencialVO> vClientePreferencial = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {                    
            try (ResultSet rst = stm.executeQuery(               
                "select\n" +
                "	c.IDCliente id,\n" +
                "	c.ClientDescricao nome,\n" +
                "	c.ClientEndereco res_endereco,\n" +
                "	c.ClientNumero res_numero,\n" +
                "	null as res_complemento,\n" +
                "	c.ClientBairro res_bairro,\n" +
                "	c.IDMunicipio res_cidade,\n" +
                "	mun.IDEstado res_uf,\n" +
                "	c.ClientCEP res_cep,\n" +
                "	c.ClientTelefone fone1,\n" +
                "	c.ClientTelefoneTrab fone2,\n" +
                "	c.ClientCelular celular,\n" +
                "	c.ClientRGIE inscricaoestadual,\n" +
                "	c.ClientCPFCNPJ cnpj,\n" +
                "	1 as sexo,\n" +
                "	case c.ClientEstadoCivil when 0 then 1 when 1 then 2 when 2 then 3 else 0 end as estadoCivil,\n" +
                "	0 as prazodias,\n" +
                "	c.ClientEmail email,\n" +
                "	c.ClientDtCadastro datacadastro,\n" +
                "	c.ClientLimiteDeCredito limitepreferencial,\n" +
                "	coalesce(c.ClientInativo,0) bloqueado,\n" +
                "	c.ClientDtNasc datanascimento,\n" +
                "	c.ClientPai nomepai,\n" +
                "	c.ClientMae nomemae,\n" +
                "	c.ClientEmpresa empresa,\n" +
                "	null as telEmpresa,\n" +
                "	c.ClientCargo cargo,\n" +
                "	0 as salario,\n" +
                "	c.ClientConjugue conjuge,\n" +
                "	null as orgaoemissor\n" +
                "from\n" +
                "	tblCliente c \n" +
                "	left join tblMunicipio mun on c.IDMunicipio = mun.IDMunicipio\n" +
                "order by\n" +
                "	c.IDCliente"
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
                    oClientePreferencial.setTelefone2(rst.getString("fone2"));
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
                    oClientePreferencial.setObservacao("IMPORTADO VR");
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
        
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try ( ResultSet rst = stm.executeQuery(
                    "select * from \n" +
                    "	(select \n" +
                    "		lanc.IDCliente,\n" +
                    "		cli.ClientCPFCNPJ,\n" +
                    "		lanc.LancCreditDt emissao,\n" +
                    "		ecf.EcfNumero ecf,\n" +
                    "		cupom.VendCpfCOO numeroCupom,\n" +
                    "		parc.ParcCreditVencimento vencimento,\n" +
                    "		parc.ParcCreditValor - coalesce((select sum(AmortCreditValorPago) from tblAmortizacaoCredito pag\n" +
                    "			where \n" +
                    "				parc.IDLancCredito = pag.IDLancCredito and\n" +
                    "				parc.IDParcela = pag.IDParcela),0) restante\n" +
                    "	from \n" +
                    "		tblParcelaCredito parc\n" +
                    "		join tblLancCredito lanc on parc.IDLancCredito = lanc.IDLancCredito\n" +
                    "		join tblCliente cli on lanc.IDCliente = cli.IDCliente\n" +
                    "		left join tblVenda v on v.IDVenda = lanc.IDVenda\n" +
                    "		left join tblVendaCupomFiscal cupom on v.IDVenda = cupom.IDVenda\n" +
                    "		left join tblEcfIdentificacao ecf on cupom.IDEcfSerie = ecf.IDEcfSerie\n" +
                    "	) rot\n" +
                    "where\n" +
                    "	rot.restante > 0\n" +
                    "order by rot.emissao"
            )) {
                while (rst.next()) {
                    ReceberCreditoRotativoVO oReceberCreditoRotativo = new ReceberCreditoRotativoVO();

                    oReceberCreditoRotativo.setId_clientepreferencial(rst.getInt("IDCliente"));
                    oReceberCreditoRotativo.setCnpjCliente(Utils.stringToLong(rst.getString("ClientCPFCNPJ")));
                    oReceberCreditoRotativo.setId_loja(idLojaCliente);
                    oReceberCreditoRotativo.setDataemissao(rst.getDate("emissao"));
                    oReceberCreditoRotativo.setEcf(rst.getInt("ecf"));
                    oReceberCreditoRotativo.setNumerocupom(Utils.stringToInt(rst.getString("numeroCupom")));
                    oReceberCreditoRotativo.setValor(rst.getDouble("restante"));
                    oReceberCreditoRotativo.setObservacao("IMPORTADO VR");
                    oReceberCreditoRotativo.setDatavencimento(rst.getDate("vencimento"));

                    result.add(oReceberCreditoRotativo);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<FornecedorVO> carregarFornecedor() throws Exception {
        List<FornecedorVO> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                "select m.IDEstado, e.IDMunicipio \n" +
                "from \n" +
                "	tblEmpresa e \n" +
                "	join tblMunicipio m on e.IDMunicipio = m.IDMunicipio\n" +
                "	join tblEstado uf on m.IDEstado = uf.IDEstado"
            )) {
                if (rst.next()) {
                    Global.idEstado = rst.getInt("IDEstado");
                    Global.idMunicipio = rst.getInt("IDMunicipio");
                }
            }
            
            try (ResultSet rst = stm.executeQuery(                    
                "select\n" +
                "	f.IDFornecedor id,\n" +
                "	f.FornDtCadastro datacadastro,\n" +
                "	f.FornDescricao razao,\n" +
                "	case when ltrim(rtrim(coalesce(f.FornNomeFantasia,''))) != '' then f.FornNomefantasia else f.FornDescricao end fantasia,\n" +
                "	f.FornEndereco endereco,\n" +
                "	f.FornNumero numero,\n" +
                "	f.FornComplemento complemento,\n" +
                "	f.FornBairro bairro,\n" +
                "	f.IDMunicipio id_municipio,\n" +
                "	mun.IDEstado id_uf,\n" +
                "	f.FornCEP cep,\n" +
                "	f.FornTelefone fone1,\n" +
                "	f.FornCelular celular,\n" +
                "	f.FornFax fax,\n" +
                "	f.FornRGIE inscricaoestadual,\n" +
                "	f.FornCPFCNPJ cnpj,\n" +
                "	f.FornObs observacao,\n" +
                "	f.FornEMail email,\n" +
                "	coalesce(f.FornInativo, 0) bloqueado\n" +
                "from\n" +
                "	tblFornecedor f\n" +
                "	left join tblMunicipio mun on f.IDMunicipio = mun.IDMunicipio\n" +
                "order by\n" +
                "	f.IDFornecedor"
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
                    int id_municipio = Utils.stringToInt(rst.getString("id_municipio"));                     
                    oFornecedor.setId_municipio(id_municipio > 0 ? id_municipio : Global.idMunicipio);                     
                    int id_uf = Utils.stringToInt(rst.getString("id_uf"));
                    oFornecedor.setId_estado(id_uf > 0 ? id_uf : Global.idEstado);
                    oFornecedor.setCep(Utils.stringToLong(rst.getString("cep"), 0));
                    oFornecedor.setTelefone(rst.getString("fone1"));
                    oFornecedor.setCelular(rst.getString("celular"));
                    oFornecedor.setInscricaoestadual(rst.getString("inscricaoestadual"));
                    oFornecedor.setCnpj(Utils.stringToLong(rst.getString("cnpj"), 0));
                    oFornecedor.setId_tipoinscricao(String.valueOf(oFornecedor.getCnpj()).length() > 11 ? 0 : 1);
                    oFornecedor.setObservacao("IMPORTADO VR - " + rst.getString("observacao"));
                    oFornecedor.setEmail(rst.getString("email"));
                    oFornecedor.setBloqueado(rst.getInt("bloqueado") == 1);
                    oFornecedor.setId_situacaocadastro(1);
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
        
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                "select m.IDEstado, e.IDMunicipio \n" +
                "from \n" +
                "	tblEmpresa e \n" +
                "	join tblMunicipio m on e.IDMunicipio = m.IDMunicipio\n" +
                "	join tblEstado uf on m.IDEstado = uf.IDEstado"
            )) {
                if (rst.next()) {
                    Global.idEstado = rst.getInt("IDEstado");
                    Global.idMunicipio = rst.getInt("IDMunicipio");
                }
            }
            
            try (ResultSet rst = stm.executeQuery(
                "select\n" +
                "	IDProduto id_produto,\n" +
                "	IDFornecedor id_fornecedor,\n" +
                "	IDFornecedorProduto codigoexterno\n" +
                "from\n" +
                "	tblProdutoFornecedor\n" +
                "order by\n" +
                "	IDProduto,\n" +
                "	IDFornecedor"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorVO vo = new ProdutoFornecedorVO();
                    vo.setId_produtoDouble(Utils.stringToDouble(rst.getString("id_produto")));
                    vo.setId_fornecedor(rst.getInt("id_fornecedor"));
                    vo.setId_fornecedorDouble(rst.getInt("id_fornecedor"));
                    vo.setId_estado(Global.idEstado);
                    vo.setCodigoexterno(rst.getString("codigoexterno"));
                    Calendar cal = new GregorianCalendar();
                    vo.setDataalteracao(new Date(cal.getTimeInMillis()));
                    result.add(vo);
                }
            }        
        }
        
        return result;
    }
    
}
