package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author lucasrafael
 */
public class ArtSystemDAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "ArtSystem";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet(Arrays.asList(new OpcaoProduto[]{
            OpcaoProduto.IMPORTAR_MANTER_BALANCA,
            OpcaoProduto.PRODUTOS,
            OpcaoProduto.ATIVO,
            OpcaoProduto.DESC_COMPLETA,
            OpcaoProduto.DESC_GONDOLA,
            OpcaoProduto.DESC_REDUZIDA,
            OpcaoProduto.DATA_CADASTRO,
            OpcaoProduto.EAN,
            OpcaoProduto.EAN_EM_BRANCO,
            OpcaoProduto.TIPO_EMBALAGEM_EAN,
            OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
            OpcaoProduto.CUSTO,
            OpcaoProduto.MARGEM,
            OpcaoProduto.PRECO,
            OpcaoProduto.ESTOQUE,
            OpcaoProduto.PESAVEL,
            OpcaoProduto.NCM,
            OpcaoProduto.CEST,
            OpcaoProduto.ICMS,
            OpcaoProduto.PIS_COFINS,
            OpcaoProduto.NATUREZA_RECEITA,
            OpcaoProduto.ATACADO,
            OpcaoProduto.VALIDADE,
            OpcaoProduto.MERCADOLOGICO,
            OpcaoProduto.MERCADOLOGICO_PRODUTO,
        }));
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "LOJNID_ENT as codigo, \n"
                    + "LOJCCODLOJ as codloja,  \n"
                    + "LOJCEMLASU as nome\n"
                    + "from dbo.ASENTLOJ"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(
                            rst.getString("codigo"),
                            rst.getString("codloja") + " - " + rst.getString("nome")
                    ));
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	DEPNID_DEP as merc1, \n"
                    + "	DEPCDESCRI as desc_merc1\n"
                    + "from dbo.ASPRODEP\n"
                    + "where DEPNID_PAI is null\n"
                    + "order by DEPNID_DEP"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("merc1"));
                    imp.setMerc1Descricao(rst.getString("desc_merc1"));
                    imp.setMerc2ID("1");
                    imp.setMerc2Descricao(imp.getMerc1Descricao());
                    imp.setMerc3ID("1");
                    imp.setMerc3Descricao(imp.getMerc1Descricao());
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select  \n"
                    + "	p.PRONID_PRO,\n"
                    + "	p.PROCCODPRO as id,\n"
                    + "	ean.EANCCODEAN as ean,\n"
                    + "	ean.EANNQTDEMB as qtdemb_ean,\n"
                    + "	p.PROCDESCRI as descricaocompleta,\n"
                    + "	p.PROCDESRES as descricaoresumida,\n"
                    + "	p.PROCCODNCM as ncm,\n"
                    + "	p.PROCCDCEST as cest,\n"
                    + "	inc.INCCCSTSAI as pis_saida,\n"
                    + "	inc.INCCCSTENT as pis_entrada,\n"
                    + "	icm.FIGCCODCST as cst,\n"
                    + "	icm.FIGNTRIALI as aliquota, \n"
                    + "	icm.FIGNTRIRED as reducao,\n"
                    + "	p.PRODDATCAD as datacadastro,\n"
                    + "	p.PRONPESBRU as peso_bruto,\n"
                    + "	p.PRONPESLIQ as peso_liquido,\n"
                    + "	est.ESTNESTATU as estoque,\n"
                    + "	est.ESTNESTMAX as estoque_maximo,\n"
                    + "	est.ESTNESTMIN as estoque_minimo,\n"
                    + "	pre.PRENCUSREP as custo,\n"
                    + "	pre.PRENVDAMRG as margem,\n"
                    + "	pre.PRENVDAATU as precovenda\n"
                    + "from dbo.ASPROPRO p\n"
                    + "left join dbo.ASPROEAN ean on ean.EANNID_PRO = p.PRONID_PRO\n"
                    + "left join dbo.ASPROEST est on est.ESTNID_PRO = p.PRONID_PRO and est.ESTNID_LOJ = " + getLojaOrigem() + "\n"
                    + "left join dbo.ASPROPRE pre on pre.PRENID_PRO = p.PRONID_PRO and pre.PRENID_LOJ = " + getLojaOrigem() + "\n"
                    + "left join dbo.ASPROINC inc on inc.INCNID_ING = pre.PRENID_INC and INCNID_INT = 121\n"
                    + "left join dbo.ASPROFIG icm on icm.FIGNIDFIGU = pre.PRENIDFIGU"
            )) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
                while (rst.next()) {
                    if ((rst.getString("id") != null)
                            && (!rst.getString("id").trim().isEmpty())) {

                        ProdutoIMP imp = new ProdutoIMP();
                        ProdutoBalancaVO produtoBalanca;

                        String codigoBalanca = rst.getString("id").substring(0, rst.getString("id").trim().length() - 1);

                        long codigoProduto;
                        codigoProduto = Long.parseLong(codigoBalanca);
                        if (codigoProduto <= Integer.MAX_VALUE) {
                            produtoBalanca = produtosBalanca.get((int) codigoProduto);
                        } else {
                            produtoBalanca = null;
                        }

                        if (produtoBalanca != null) {
                            imp.seteBalanca(true);
                            imp.setValidade(produtoBalanca.getValidade() > 1 ? produtoBalanca.getValidade() : 0);
                        } else {
                            imp.seteBalanca(false);
                        }

                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("id"));

                        if (imp.isBalanca()) {
                            imp.setEan(codigoBalanca);
                        } else {
                            imp.setEan(rst.getString("ean"));
                        }

                        imp.setQtdEmbalagem(rst.getInt("qtdemb_ean"));
                        imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                        imp.setDescricaoReduzida(rst.getString("descricaoresumida"));
                        imp.setDescricaoGondola(imp.getDescricaoCompleta());
                        imp.setDataCadastro(rst.getDate("datacadastro"));
                        imp.setPesoBruto(rst.getDouble("peso_bruto"));
                        imp.setPesoLiquido(rst.getDouble("peso_liquido"));
                        imp.setNcm(rst.getString("ncm"));
                        imp.setCest(rst.getString("cest"));
                        imp.setPiscofinsCstDebito(rst.getString("pis_saida"));
                        imp.setPiscofinsCstCredito(rst.getString("pis_entrada"));
                        imp.setIcmsCst(rst.getString("cst"));
                        imp.setIcmsAliq(rst.getDouble("aliquota"));
                        imp.setIcmsReducao(rst.getDouble("reducao"));
                        imp.setMargem(rst.getDouble("margem"));
                        imp.setPrecovenda(rst.getDouble("precovenda"));
                        imp.setCustoComImposto(rst.getDouble("custo"));
                        imp.setCustoSemImposto(rst.getDouble("custo"));
                        imp.setEstoque(rst.getDouble("estoque"));
                        imp.setEstoqueMinimo(rst.getDouble("estoque_minimo"));
                        imp.setEstoqueMaximo(rst.getDouble("estoque_maximo"));
                        result.add(imp);
                    }
                }
            }
            return result;
        }
    }

    @Override
    public List<ProdutoIMP> getProdutos(OpcaoProduto opt) throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        if (opt == OpcaoProduto.MERCADOLOGICO) {
            try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select \n"
                        + " p.PROCCODPRO as idproduto,\n"
                        + " (p.PRONID_DEP - 4) as merc\n"
                        + "from dbo.ASPROPRO p \n"
                        + "inner join dbo.ASPRODEP d on d.DEPNID_DEP = (p.PRONID_DEP - 4)"
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("idproduto"));
                        imp.setCodMercadologico1(rst.getString("merc"));
                        imp.setCodMercadologico2("1");
                        imp.setCodMercadologico3("1");
                        result.add(imp);
                    }
                }
                return result;
            }            
        }
        return null;
    }
    
    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	f.ENTNID_ENT as id,\n"
                    + "	f.ENTCCODCPF as cnpj,\n"
                    + "	f.ENTCCOD_RG as ie_rg,\n"
                    + "	f.ENTCNOMENT as razao,\n"
                    + "	f.ENTCAPELID as fantasia,\n"
                    + " cep.CEPCCODCEP as cep,\n"
                    + "	cep.CEPCDESCRI as endereco,\n"
                    + "	f.ENTNENDNUM as numero,\n"
                    + "	f.ENTCENDCOM as complemento,\n"
                    + "	f.ENTCE_MAIL as email,\n"
                    + "	bai.BAICDESCRI as bairro,\n"
                    + "	mun.MUNCDESCRI as municipio,\n"
                    + "	mun.MUNCCODIBG as municipio_ibge,\n"
                    + "	mun.MUNCCOD_UF as uf,\n"
                    + "	tel.TELNCODDDD as ddd,\n"
                    + "	tel.TELNNUMTEL as telefone,\n"
                    + "	tel.TELMOBSERV as tel_contato,\n"
                    + "	obs.OBSMOBSERV as observacao\n"
                    + "from dbo.ASENTENT f\n"
                    + "left join dbo.ASCEPCEP cep on cep.CEPNID_CEP = f.ENTNID_CEP\n"
                    + "left join dbo.ASCEPBAI bai on bai.BAINID_BAI = cep.CEPNID_BAI\n"
                    + "left join dbo.ASCEPMUN mun on mun.MUNNID_MUN = bai.BAINID_MUN\n"
                    + "left join dbo.ASENTTEL tel on tel.TELNID_ENT = f.ENTNID_ENT\n"
                    + "left join dbo.ASENTOBS obs on obs.OBSNID_ENT = f.ENTNID_ENT and obs.OBSCTIPCAD = 'FNC'\n"
                    + "where f.ENTNID_ENT not in (select CLINID_ENT from dbo.ASENTCLI)"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("ie_rg"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setEndereco("RUA " + rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setCep(rst.getString("cep"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setIbge_municipio(rst.getInt("municipio_ibge"));
                    imp.setUf(rst.getString("uf"));
                    imp.setTel_principal(rst.getString("ddd") + rst.getString("telefone"));
                    imp.setObservacao(rst.getString("observacao"));

                    if ((rst.getString("telefone") != null)
                            && (!rst.getString("telefone").trim().isEmpty())) {
                        imp.addTelefone(
                                rst.getString("tel_contato") != null ? rst.getString("tel_contato") : "TELEFONE",
                                rst.getString("ddd") + rst.getString("telefone")
                        );
                    }
                    
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	pro.PROCCODPRO as idproduto,\n"
                    + "	pf.REFNID_FNC as idfornecedor,\n"
                    + "	pf.REFCCODREF as codigoexterno,\n"
                    + "	pf.REFNQTDEMB as qtdembalagem,\n"
                    + "	pf.REFDDATALT as datalateracao\n"
                    + "from dbo.ASPROREF pf\n"
                    + "inner join dbo.ASPROPRO pro on pf.REFNID_PRO = pro.PRONID_PRO"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rst.getString("idproduto"));
                    imp.setIdFornecedor(rst.getString("idfornecedor"));
                    imp.setCodigoExterno(rst.getString("codigoexterno"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    imp.setDataAlteracao(rst.getDate("datalateracao"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	f.ENTNID_ENT as id,\n"
                    + "	f.ENTCCODCPF as cnpj,\n"
                    + "	f.ENTCCOD_RG as ie_rg,\n"
                    + "	f.ENTCNOMENT as razao,\n"
                    + "	f.ENTCAPELID as fantasia,\n"
                    + "	cep.CEPCCODCEP as cep,\n"
                    + "	cep.CEPCDESCRI as endereco,\n"
                    + "	cepCob.CEPCCODCEP as cep_cob,\n"
                    + "	cepCob.CEPCDESCRI as endereco_cob,\n"
                    + "	cepEnt.CEPCCODCEP as cep_ent,\n"
                    + "	cepEnt.CEPCDESCRI as endereco_ent,\n"
                    + "	cli.CLINCOBNUM as numero_cob,\n"
                    + "	cli.CLICCOBCOM as complemento_cob,\n"
                    + "	cli.CLINETRNUM as numero_ent,\n"
                    + "	cli.CLICETRCOM as complemento_ent,\n"
                    + "	f.ENTNENDNUM as numero,\n"
                    + "	f.ENTCENDCOM as complemento,\n"
                    + "	f.ENTCE_MAIL as email,\n"
                    + "	bai.BAICDESCRI as bairro,\n"
                    + "	baiCob.BAICDESCRI as bairro_cob,\n"
                    + "	baiEnt.BAICDESCRI as bairro_ent,\n"
                    + "	mun.MUNCDESCRI as municipio,\n"
                    + "	mun.MUNCCODIBG as municipio_ibge,\n"
                    + "	mun.MUNCCOD_UF as uf,\n"
                    + "	munCob.MUNCDESCRI as municipio_cob,\n"
                    + "	munCob.MUNCCODIBG as municipio_ibge_cob,\n"
                    + "	munCob.MUNCCOD_UF as uf_cob,\n"
                    + "	munEnt.MUNCDESCRI as municipio_ent,\n"
                    + "	munEnt.MUNCCODIBG as municipio_ibge_ent,\n"
                    + "	munEnt.MUNCCOD_UF as uf_ent,	\n"
                    + "	tel.TELNCODDDD as ddd,\n"
                    + "	tel.TELNNUMTEL as telefone,\n"
                    + "	tel.TELMOBSERV as tel_contato,\n"
                    + "	obs.OBSMOBSERV as observacao,\n"
                    + "	cli.CLIDNASABE as nascimento,\n"
                    + "	cli.CLIDDATCAD as cadastro,\n"
                    + "	cli.CLINLIMCPR as valor_limite,\n"
                    + "	cli.CLINCVNSAL as salario,\n"
                    + "	cli.CLINCVNLIM as limite_convenio,\n"
                    + "	cli.CLICSEXTIP as sexo,\n"
                    + " cli.CLINID_STA as status\n"
                    + "from dbo.ASENTENT f\n"
                    + "left join dbo.ASCEPCEP cep on cep.CEPNID_CEP = f.ENTNID_CEP\n"
                    + "left join dbo.ASCEPBAI bai on bai.BAINID_BAI = cep.CEPNID_BAI\n"
                    + "left join dbo.ASCEPMUN mun on mun.MUNNID_MUN = bai.BAINID_MUN\n"
                    + "left join dbo.ASENTTEL tel on tel.TELNID_ENT = f.ENTNID_ENT\n"
                    + "left join dbo.ASENTOBS obs on obs.OBSNID_ENT = f.ENTNID_ENT and obs.OBSCTIPCAD = 'CLI'\n"
                    + "inner join dbo.ASENTCLI cli on cli.CLINID_ENT = f.ENTNID_ENT\n"
                    + "left join dbo.ASCEPCEP cepCob on cepCob.CEPNID_CEP = cli.CLINID_COB\n"
                    + "left join dbo.ASCEPBAI baiCob on baiCob.BAINID_BAI = cepCob.CEPNID_BAI\n"
                    + "left join dbo.ASCEPMUN munCob on munCob.MUNNID_MUN = baiCob.BAINID_MUN\n"
                    + "left join dbo.ASCEPCEP cepEnt on cepEnt.CEPNID_CEP = cli.CLINID_ETR\n"
                    + "left join dbo.ASCEPBAI baiEnt on baiEnt.BAINID_BAI = cepEnt.CEPNID_BAI\n"
                    + "left join dbo.ASCEPMUN munEnt on munEnt.MUNNID_MUN = baiEnt.BAINID_MUN"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setInscricaoestadual(rst.getString("ie_rg"));
                    imp.setEndereco("RUA " + rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setCep(rst.getString("cep"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setMunicipioIBGE(rst.getInt("municipio_ibge"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCobrancaEndereco(rst.getString("endereco_cob"));
                    imp.setCobrancaNumero(rst.getString("numero_cob"));
                    imp.setCobrancaCep(rst.getString("cep_cob"));
                    imp.setCobrancaBairro(rst.getString("bairro_cob"));
                    imp.setCobrancaMunicipio(rst.getString("municipio_cob"));
                    imp.setCobrancaMunicipioIBGE(rst.getInt("municipio_ibge_cob"));
                    imp.setCobrancaUf(rst.getString("uf_cob"));
                    imp.setTelefone(rst.getString("ddd") + rst.getString("telefone"));
                    imp.setEmail(rst.getString("email") == null ? "" : rst.getString("email").toLowerCase());
                    imp.setObservacao(rst.getString("observacao"));
                    imp.setObservacao2(rst.getString("observacao"));
                    imp.setDataNascimento(rst.getDate("nascimento"));
                    imp.setDataCadastro(rst.getDate("cadastro"));
                    imp.setSexo("M".equals(rst.getString("sexo")) ? TipoSexo.MASCULINO : TipoSexo.FEMININO);
                    imp.setValorLimite(rst.getDouble("valor_limite"));
                    imp.setSalario(rst.getDouble("salario"));
                    imp.setPermiteCreditoRotativo(true);
                    imp.setPermiteCheque(true);
                    
                    if (null != rst.getString("status")) switch (rst.getString("status")) {
                        case "232":
                            imp.setBloqueado(true);
                            break;
                        case "233":
                            imp.setBloqueado(false);
                            break;
                        case "263":
                            imp.setBloqueado(true);
                            break;
                        default:
                            imp.setBloqueado(false);
                            break;
                    }
                    
                    if ((rst.getString("telefone") != null)
                            && (!rst.getString("telefone").trim().isEmpty())) {
                        imp.addTelefone(
                                rst.getString("tel_contato") != null ? rst.getString("tel_contato") : "TELEFONE",
                                rst.getString("ddd") + rst.getString("telefone")
                        );
                    }
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	f.FINNID_FIN as id,\n"
                    + "	f.FINNID_ENT as idcliente,\n"
                    + "	f.FINC_NFNUM as numerocupom,\n"
                    + "	f.FINDDATEMI as dataemissao,\n"
                    + "	i.FVLDVENVIG as datavencimento,\n"
                    + "	i.FVLNVAlBRU as valor,\n"
                    + "	i.FVLCDESCRI as observacao\n"
                    + "from [As_FIN].dbo.ASFINFIN f\n"
                    + "inner join [As_FIN].dbo.ASFINFVL i on i.FVLNID_FIN = f.FINNID_FIN\n"
                    + "where f.FINCTIPENT = 'CLI'\n"
                    + "and i.FVLDDATPAG is null\n"
                    + "and i.FVLNID_PFI = 2\n"
                    + "and f.FINNID_LOJ = " + getLojaOrigem() + "\n"
                    + "order by f.FINDDATEMI desc"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("id"));
                    imp.setIdCliente(rst.getString("idcliente"));
                    imp.setDataEmissao(rst.getDate("dataemissao"));
                    imp.setDataVencimento(rst.getDate("datavencimento"));
                    imp.setNumeroCupom(rst.getString("numerocupom"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setObservacao(rst.getString("observacao"));
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
