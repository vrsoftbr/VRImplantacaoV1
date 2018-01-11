package vrimplantacao.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrframework.classe.ProgressBar;
import vrframework.remote.ItemComboVO;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao.dao.cadastro.ReceberCreditoRotativoDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.ClientePreferencialVO;
import vrimplantacao.vo.vrimplantacao.MunicipioVO;
import vrimplantacao.vo.vrimplantacao.ReceberCreditoRotativoVO;
import vrimplantacao2.dao.cadastro.LocalDAO;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.parametro.Parametros;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 * @author Leandro
 */
public class ActiveDAO extends AbstractIntefaceDao {
    
    private final InterfaceDAO produtoDao = new InterfaceDAO() {
        @Override
        public String getSistema() {
            return "ACTIVE";
        }

        @Override
        public List<MercadologicoIMP> getMercadologicos() throws Exception {
            List<MercadologicoIMP> result = new ArrayList<>();
            
            try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select\n" +
                        "    g.codgrupo merc1,\n" +
                        "    g.descr merc1_desc,\n" +
                        "    s.codsubgrupoi merc2,\n" +
                        "    s.descr merc2_desc\n" +
                        "from\n" +
                        "    tbgrupo g\n" +
                        "    left join tbsubgrupo s on s.codgrupoi = g.codgrupo\n" +
                        "order by\n" +
                        "    g.codgrupo"
                )) {
                    while (rst.next()) {
                        MercadologicoIMP merc = new MercadologicoIMP();
                        merc.setImportSistema(getSistema());
                        merc.setImportLoja(getLojaOrigem());
                        merc.setMerc1ID(rst.getString("merc1"));
                        merc.setMerc1Descricao(rst.getString("merc1_desc"));
                        merc.setImportLoja(getLojaOrigem());
                        merc.setMerc2ID(rst.getString("merc2"));
                        merc.setMerc2Descricao(rst.getString("merc2_desc"));
                        
                        result.add(merc);
                    }
                }
            }
            
            return result;
        }

        @Override
        public List<ProdutoIMP> getProdutos() throws Exception {
            List<ProdutoIMP> result = new ArrayList<>();
            
            String sql = 
                "select\n" +
                "    p.coditem id,\n" +
                "    p.codbarra codigobarras,\n" +
                "    upper(substring(un.uni from 1 for 2)) unidade,\n" +
                "    case upper(p.exportabalanca) when 'S' then 'S' else 'N' end as balanca,\n" +
                "    p.descricao descricaocompleta,\n" +
                "    p.descabrev descricaoreduzida,\n" +
                "    p.descricao descricaogondola,\n" +
                "    p.codgrupoi cod_mercadologico1,\n" +
                "    p.codsubgrupo cod_mercadologico2,\n" +
                "    '' as id_familiaproduto,\n" +
                "    p.pesobruto,\n" +
                "    p.pesoliquido,\n" +
                "    coalesce(p.dtcadastro, current_timestamp) datacadastro,\n" +
                "    0 as validade,\n" +
                "    p.lucro margem,\n" +
                "    0 as estoquemaximo,\n" +
                "    p.qdtmin estoqueminimo,\n" +
                "    p.qtdest estoque,\n" +
                "    p.valorcompra custosemimposto,\n" +
                "    p.valorcompra custocomimposto,\n" +
                "    p.valor precovenda,\n" +
                "    case when upper(p.ativo) = 'S' then 'S' else 'N' end as ativo,\n" +
                "    p.cprod ncm,\n" +
                "    p.cest,\n" +
                "    p.cstconfins piscofins_cst_debito,\n" +
                "    p.cstcofinsentrada piscofins_cst_credito,\n" +
                "    null as piscofins_natureza_receita,\n" +
                "    p.codicms\n" +
                "from\n" +
                "    tbitem p\n" +
                "    left join tbuni un on p.uni = un.coduni\n" +
                "order by\n" +
                "    p.coditem";
            
            try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(sql)) {
                    int cont = 1;
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                    
                        imp.setImportSistema(getSistema());
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportId(rst.getString("id"));
                        imp.setEan(rst.getString("codigobarras"));
                        imp.setTipoEmbalagem(rst.getString("unidade"));
                        imp.seteBalanca("S".equals(rst.getString("balanca")));
                        imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                        imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                        imp.setDescricaoGondola(rst.getString("descricaogondola"));
                        imp.setCodMercadologico1(rst.getString("cod_mercadologico1"));
                        imp.setCodMercadologico2(rst.getString("cod_mercadologico2"));
                        imp.setIdFamiliaProduto(rst.getString("id_familiaproduto"));
                        imp.setPesoBruto(rst.getDouble("pesobruto"));
                        imp.setPesoLiquido(rst.getDouble("pesoliquido"));
                        imp.setDataCadastro(rst.getDate("datacadastro"));
                        imp.setValidade(rst.getInt("validade"));
                        imp.setMargem(rst.getDouble("margem"));
                        imp.setEstoqueMaximo(rst.getDouble("estoquemaximo"));
                        imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                        imp.setEstoque(rst.getDouble("estoque"));
                        imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                        imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                        imp.setPrecovenda(rst.getDouble("precovenda"));
                        if ("N".equals(rst.getString("ativo"))) {
                            imp.setSituacaoCadastro(SituacaoCadastro.EXCLUIDO);
                        } else {
                            imp.setSituacaoCadastro(SituacaoCadastro.ATIVO);
                        }
                        imp.setNcm(rst.getString("ncm"));
                        imp.setCest(rst.getString("cest"));
                        imp.setPiscofinsCstDebito(rst.getInt("piscofins_cst_debito"));
                        imp.setPiscofinsCstCredito(rst.getInt("piscofins_cst_credito"));
                        imp.setPiscofinsNaturezaReceita(rst.getInt("piscofins_natureza_receita"));
                        int icmsCst, icmsAliquota;
                        String trib = rst.getString("codicms") != null ? rst.getString("codicms") : "";
                        switch (trib) {                            
                            case "07":
                                icmsCst = 0;
                                icmsAliquota = 7;
                            break;
                            case "12":
                                icmsCst = 0;
                                icmsAliquota = 12;
                            break;
                            case "17":
                                icmsCst = 0;
                                icmsAliquota = 17;
                            break;
                            case "18":
                                icmsCst = 0;
                                icmsAliquota = 18;
                            break;
                            case "27":
                                icmsCst = 0;
                                icmsAliquota = 27;
                            break;
                            case "II":
                                icmsCst = 40;
                                icmsAliquota = 0;
                            break;
                            case "NN":
                                icmsCst = 41;
                                icmsAliquota = 0;
                            break;
                            default:
                                icmsCst = 60;
                                icmsAliquota = 0;
                            ;break;
                        }
                        imp.setIcmsCst(icmsCst);
                        imp.setIcmsAliq(icmsAliquota);
                        imp.setIcmsReducao(0);


                        ProgressBar.setStatus("Convertendo em IMP.... " + cont);
                        cont++;

                        result.add(imp);
                    }
                }
            }
            
            return result;
        }

        /*@Override
        public List<ProdutoIMP> getEANs() throws Exception {
            List<ProdutoIMP> result = new ArrayList<>();
            
            String sql = 
                    "select\n" +
                    "    p.coditem id,\n" +
                    "    trim(p.codbarra) codigobarras,\n" +
                    "    upper(substring(un.uni from 1 for 2)) unidade,\n" +
                    "    coalesce(p.pesobruto,0) pesobruto\n" +
                    "from\n" +
                    "    tbitem p\n" +
                    "    left join tbuni un on p.uni = un.coduni\n" +
                    "where\n" +
                    "    trim(p.codbarra) != ''\n" +
                    "union\n" +
                    "select\n" +
                    "    p.coditem id,\n" +
                    "    trim(p.codb) codigobarras,\n" +
                    "    upper(substring(un.uni from 1 for 2)) unidade,\n" +
                    "    coalesce(p.pesobruto,0) pesobruto\n" +
                    "from\n" +
                    "    tbitem p\n" +
                    "    left join tbuni un on p.uni = un.coduni\n" +
                    "where\n" +
                    "    trim(p.codb) != ''";
            
            try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(sql)) {
                    int cont = 1;
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                    
                        imp.setImportSistema(getSistema());
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportId(rst.getString("id"));
                        imp.setEan(rst.getString("codigobarras"));
                        imp.setTipoEmbalagem(rst.getString("unidade"));
                        imp.setQtdEmbalagem(1);
                        imp.setPrecovenda(0);

                        result.add(imp);
                    }
                }
            }
            
            return result;
        }*/

        @Override
        public List<FornecedorIMP> getFornecedores() throws Exception {
            List<FornecedorIMP> result = new ArrayList<>();
            
            try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select\n" +
                        "    f.codfor id,\n" +
                        "    f.razaosocial razao,\n" +
                        "    f.nomefantasia fantasia,\n" +
                        "    f.cgc cnpj_cpf,\n" +
                        "    f.idinsc ie_rg,\n" +
                        "    case upper(f.ativo) when 'S' then 'S' else 'N' end as ativo,\n" +
                        "    f.endereco,\n" +
                        "    0 as numero,\n" +
                        "    f.compl complemento,\n" +
                        "    f.bairro,\n" +
                        "    f.codmunicipioibge id_municipio,\n" +
                        "    f.cep,\n" +
                        "    f.fone tel_principal,\n" +
                        "    f.dtcad datacadastro,\n" +
                        "    f.obs observacao\n" +
                        "from\n" +
                        "    tfor f\n" +
                        "order by\n" +
                        "    f.codfor"
                )) {
                    while (rst.next()) {
                        FornecedorIMP imp = new FornecedorIMP();
                        
                        imp.setImportSistema(getSistema());
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportId(rst.getString("id"));
                        imp.setRazao(rst.getString("razao"));
                        imp.setFantasia(rst.getString("fantasia"));
                        imp.setCnpj_cpf(rst.getString("cnpj_cpf"));
                        imp.setIe_rg(rst.getString("ie_rg"));
                        imp.setAtivo("S".equals(rst.getString("ativo")));
                        imp.setEndereco(rst.getString("endereco"));
                        imp.setComplemento(rst.getString("complemento"));
                        imp.setBairro(rst.getString("bairro"));
                        imp.setIbge_municipio(rst.getInt("id_municipio"));
                        imp.setCep(rst.getString("cep"));
                        imp.setTel_principal(rst.getString("tel_principal"));
                        imp.setDatacadastro(rst.getTimestamp("datacadastro"));
                        imp.setObservacao(rst.getString("observacao"));
                        
                        result.add(imp);
                    }
                }
            }
            
            return result;
        }

        @Override
        public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
            List<ProdutoFornecedorIMP> result = new ArrayList<>();
        
            try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    pf.codfori id_fornecedor,\n" +
                    "    pf.coditemi id_produto,\n" +
                    "    pf.codprodfor codigoexterno\n" +
                    "from\n" +
                    "    tbforprod pf\n" +
                    "order by\n" +
                    "    pf.codfori,\n" +
                    "    pf.coditemi,\n" +
                    "    pf.codprodfor"
                )) {
                    while (rst.next()) {
                        ProdutoFornecedorIMP vo = new ProdutoFornecedorIMP();
                        vo.setImportSistema(getSistema());
                        vo.setImportLoja(getLojaOrigem());
                        vo.setIdProduto(rst.getString("id_produto"));
                        vo.setIdFornecedor(rst.getString("id_fornecedor"));
                        vo.setCodigoExterno(rst.getString("codigoexterno"));
                        result.add(vo);
                    }
                }        
            }

            return result;
        }
        
        
        
    };
    

    public InterfaceDAO getProdutoDao() {
        return produtoDao;
    }

    public List<ItemComboVO> getLojasCliente() throws Exception {
        List<ItemComboVO> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                "select\n" +
                "    cast(CODEMP as integer) ID,\n" +
                "    CODEMP || ' - ' || RAZAOSOCIAL DESCRICAO\n" +
                "from\n" +
                "    TBEMPRESA\n" +
                "order by\n" +
                "    ID"
            )) {
                while (rst.next()) {
                    result.add(new ItemComboVO(rst.getInt("id"), rst.getString("descricao")));
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
                "    c.codcli id,\n" +
                "    c.nome,\n" +
                "    c.endereco res_endereco,\n" +
                "    c.n_endereco res_numero,\n" +
                "    c.compl res_complemento,\n" +
                "    c.bairro res_bairro,\n" +
                "    c.codmunicipioibge id_cidade,\n" +
                "    c.cep res_cep,\n" +
                "    c.fone fone1,\n" +
                "    c.celular,\n" +
                "    case when trim(coalesce(c.cgc,'')) = '' then c.rg else c.idinsc end as inscricaoestadual,\n" +
                "    case when trim(coalesce(c.cgc,'')) = '' then c.cpf else c.cgc end as cnpj,\n" +
                "    1 as sexo,\n" +
                "    c.email,\n" +
                "    c.dtcad datacadastro,\n" +
                "    coalesce(c.ativo,'S') bloqueado,\n" +
                "    c.dtnascimento datanascimento,   \n" +
                "    c.limcredito limite,\n" +
                "    c.saldocredito,\n" +
                "    c.renda salario,\n" +
                "    case c.estadocivil when 'C' then 2 when 'S' then 1 else 0 end estadocivil\n" +
                "from\n" +
                "    tcli c\n" +
                "where\n" +
                "    c.codcli > 0\n" +
                "order by\n" +
                "    c.codcli"
            )) {
                LocalDAO localDAO = new LocalDAO();
                while (rst.next()) {                    
                    ClientePreferencialVO oClientePreferencial = new ClientePreferencialVO();

                    oClientePreferencial.setId(rst.getInt("id"));
                    oClientePreferencial.setCodigoanterior(rst.getInt("id"));
                    oClientePreferencial.setNome(rst.getString("nome"));
                    oClientePreferencial.setEndereco(rst.getString("res_endereco"));
                    oClientePreferencial.setNumero(rst.getString("res_numero"));
                    oClientePreferencial.setComplemento(rst.getString("res_complemento"));
                    oClientePreferencial.setBairro(rst.getString("res_bairro"));                        
                    MunicipioVO municipio = localDAO.getMunicipio(rst.getInt("id_cidade"));
                    if (municipio != null) {
                        oClientePreferencial.setId_municipio(municipio.getId());
                        oClientePreferencial.setId_estado(municipio.getIdEstado());
                    } else {
                        oClientePreferencial.setId_municipio(Parametros.get().getMunicipioPadrao2().getId());
                        oClientePreferencial.setId_estado(Parametros.get().getMunicipioPadrao2().getEstado().getId());
                    }
                    oClientePreferencial.setCep(rst.getString("res_cep"));
                    oClientePreferencial.setTelefone(rst.getString("fone1"));
                    oClientePreferencial.setCelular(rst.getString("celular"));
                    oClientePreferencial.setInscricaoestadual(rst.getString("inscricaoestadual"));
                    oClientePreferencial.setCnpj(rst.getString("cnpj"));
                    if (String.valueOf(oClientePreferencial.getCnpj()).length() < 8) {
                        oClientePreferencial.setCnpj(-1);
                    }
                    oClientePreferencial.setSexo(rst.getInt("sexo"));
                    oClientePreferencial.setEmail(rst.getString("email"));
                    oClientePreferencial.setDataresidencia("1990/01/01");
                    oClientePreferencial.setDatacadastro(rst.getDate("datacadastro"));
                    oClientePreferencial.setEmail(rst.getString("email"));
                    oClientePreferencial.setValorlimite(rst.getDouble("limite"));
                    oClientePreferencial.setBloqueado("N".equals(rst.getString("bloqueado")));
                    oClientePreferencial.setId_situacaocadastro(1);
                    oClientePreferencial.setObservacao("IMPORTADO VR  - limite: " + String.format("%.02f", rst.getFloat("limite")));
                    oClientePreferencial.setDatanascimento(rst.getDate("datanascimento"));
                    oClientePreferencial.setId_tipoinscricao(String.valueOf(oClientePreferencial.getCnpj()).length() > 11 ? 0 : 1);
                    oClientePreferencial.setSalario(rst.getDouble("salario"));
                    oClientePreferencial.setId_tipoestadocivil(rst.getInt("estadoCivil"));              

                    vClientePreferencial.add(oClientePreferencial);
                }                
            }
        }
        return vClientePreferencial;
    }

    @Override
    public List<ReceberCreditoRotativoVO> carregarReceberCreditoRotativo(int idLojaVR, int idLojaCliente) throws Exception {
        List<ReceberCreditoRotativoVO> result = new ArrayList<>();
        
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try ( ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    r.codclii,\n" +
                    "    case when trim(coalesce(c.cgc,'')) = '' then c.cpf else c.cgc end as cnpj,\n" +
                    "    r.dtemissao,\n" +
                    "    v.ncupon,\n" +
                    "    r.valor,\n" +
                    "    r.obs,\n" +
                    "    r.dtvenc,\n" +
                    "    r.dtpag,\n" +
                    "    r.valorrec\n" +
                    "from\n" +
                    "    tbcr r\n" +
                    "    join tcli c on r.codclii = c.codcli\n" +
                    "    join tbvenda v on r.nvendai = v.nvenda\n" +
                    "where\n" +
                    "    r.valor > r.valorrec"
            )) {
                while (rst.next()) {
                    ReceberCreditoRotativoVO oReceberCreditoRotativo = new ReceberCreditoRotativoVO();

                    oReceberCreditoRotativo.setId_clientepreferencial(rst.getInt("codclii"));
                    oReceberCreditoRotativo.setCnpjCliente(Utils.stringToLong(rst.getString("cnpj")));
                    oReceberCreditoRotativo.setId_loja(idLojaVR);
                    oReceberCreditoRotativo.setDataemissao(rst.getDate("dtemissao"));
                    oReceberCreditoRotativo.setNumerocupom(Utils.stringToInt(rst.getString("ncupon")));
                    oReceberCreditoRotativo.setValor(rst.getDouble("valor"));
                    oReceberCreditoRotativo.setObservacao("IMPORTADO VR " + rst.getString("obs"));
                    oReceberCreditoRotativo.setDatavencimento(rst.getDate("dtvenc"));
                    oReceberCreditoRotativo.setDataPagamento(rst.getDate("dtpag"));
                    oReceberCreditoRotativo.setValorPago(rst.getDouble("valorrec"));

                    result.add(oReceberCreditoRotativo);
                }
            }
        }
        
        return result;
    }

    @Override
    public void importarReceberCreditoRotativo(int idLoja, int idLojaCliente) throws Exception {
        try {
            ProgressBar.setStatus("Carregando dados...Receber Cliente...");
            List<ReceberCreditoRotativoVO> vReceberCliente = carregarReceberCreditoRotativo(idLoja, idLojaCliente);

            new ReceberCreditoRotativoDAO().salvar(vReceberCliente, idLoja, true);

        } catch (Exception ex) {

            throw ex;
        }
    }
    
    
    
}
