package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import vrframework.classe.ProgressBar;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto.ProdutoAnteriorDAO;
import vrimplantacao2.utils.MathUtils;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorContatoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

public class InteragemDAO extends InterfaceDAO {

    public String i_arquivoXLS;
    private String complemento = "";

    public void setComplemento(String complemento) {
        this.complemento = complemento == null ? "" : complemento.trim();
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + "f.CODFIL,"
                    + "f.NOMFIL \n"
                    + "FROM TABFIL f"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("CODFIL"), "LOJA " + rst.getString("NOMFIL")));
                }
            }
        }
        return result;
    }

    @Override
    public String getSistema() {
        if (complemento.isEmpty()) {
            return "Interagem";
        } else {
            return "Interagem - " + complemento;
        }
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "distinct p.codpro as id,\n"
                    + "coalesce(p.codbarun, p.codpro) as ean,\n"
                    + "1 as qtdembalagem,\n"
                    + "p.unidade as unidade,\n"
                    + "p.balanca as balanca,\n"
                    + "coalesce(p.descpro, '') as descricaocompleta,\n"
                    + "coalesce(p.descpro, '') as descricaoreduzida,\n"
                    + "coalesce(p.descpro, '') as descricaogondola,\n"
                    + "0 as cod_mercadologico1,\n"
                    + "'ACERTAR' as mercadologico1,\n"
                    + "0 as cod_mercadologico2,\n"
                    + "'ACERTAR' as mercadologico2,\n"
                    + "0 as cod_mercadologico3,\n"
                    + "'ACERTAR' as mercadologico3,\n"
                    + "'' as cod_mercadologico4,\n"
                    + "'' as mercadologico4,\n"
                    + "'' as cod_mercadologico5,\n"
                    + "'' as mercadologico5,\n"
                    + "'' as id_familiaproduto,\n"
                    + "'' as familiaproduto,\n"
                    + "p.pesobruto as pesobruto,\n"
                    + "p.pesoliquido as pesoliquido,\n"
                    + "p.rgdata as datacadastro,\n"
                    + "coalesce(p.diasvenc, 0) as validade,\n"
                    + "coalesce(f.marglucva, 0) as margem,\n"
                    + "0 as estoquemaximo,\n"
                    + "0 as estoqueminimo,\n"
                    + "coalesce(f.qtdpro, 0) as estoque,\n"
                    + "coalesce(f.ultprcompra, 0) as custocomimposto,\n"
                    + "coalesce(f.ultprcompra, 0) as custosemimposto,\n"
                    + "coalesce(f.prvapro, 0) as precovenda,\n"
                    + "case p.stprod when 'A' then 'S' else 'N' end ativo,\n"
                    + "coalesce(p.clasfiscal, '') as ncm,\n"
                    + "coalesce(p.cest, '') as cest,\n"
                    + "coalesce(i.piscst, 1) as piscofins_cst_debito,\n"
                    + "coalesce(i.piscst, 1) as piscofins_cst_credito,\n"
                    + "'' as piscofins_natureza_receita,\n"
                    + "coalesce(p.cst, 0) as icms_cst,\n"
                    + "coalesce(p.icms, 0) as icms_aliquota,\n"
                    + "0 as icms_reduzido\n"
                    + "from tabpro p\n"
                    + "left join tabproimp i on i.codpro = p.codpro\n"
                    + "left join TABPROFIL f on f.codpro = p.codpro\n"
                    + "where p.stprod = 'A'\n"
                    + "and f.codfil = " + getLojaOrigem()
            )) {
                int contador = 1;
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(Utils.formataNumero(rst.getString("id")));
                    imp.setEan(Utils.formataNumero(rst.getString("ean")));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setDescricaoGondola(imp.getDescricaoGondola());
                    imp.setTipoEmbalagem(rst.getString("unidade"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    imp.setMargem(MathUtils.trunc(rst.getDouble("margem"), 2));
                    imp.setPesoBruto(MathUtils.trunc(rst.getDouble("pesobruto"), 2));
                    imp.setPesoLiquido(MathUtils.trunc(rst.getDouble("pesoliquido"), 2));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(Integer.parseInt(Utils.formataNumero(rst.getString("piscofins_cst_debito"))));
                    imp.setPiscofinsCstCredito(Integer.parseInt(Utils.formataNumero(rst.getString("piscofins_cst_credito"))));
                    imp.setSituacaoCadastro(("S".equals(rst.getString("ativo")) ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO));
                    String venda = rst.getString("precovenda");
                    
                    if(venda.length() > 11) {
                        imp.setPrecovenda(0);
                    } else {
                        imp.setPrecovenda(rst.getDouble("precovenda"));
                    }

                    imp.setCustoComImposto(MathUtils.trunc(rst.getDouble("custocomimposto"), 2));
                    imp.setCustoSemImposto(MathUtils.trunc(rst.getDouble("custocomimposto"), 2));
                    imp.setEstoque(MathUtils.trunc(rst.getDouble("estoque"), 2));
                    imp.setIcmsCst(Integer.parseInt(Utils.formataNumero(rst.getString("icms_cst"))));
                    imp.setIcmsCstSaida(Integer.parseInt(Utils.formataNumero(rst.getString("icms_cst"))));
                    imp.setIcmsAliq(rst.getDouble("icms_aliquota"));
                    imp.setIcmsAliqSaida(rst.getDouble("icms_aliquota"));
                    imp.setIcmsReducao(rst.getDouble("icms_reduzido"));

                    if ((rst.getString("ean") != null)
                            && (!rst.getString("ean").trim().isEmpty())
                            && (rst.getString("ean").trim().length() <= 6)) {
                        ProdutoBalancaVO produtoBalanca;
                        long codigoProduto;
                        codigoProduto = Long.parseLong(imp.getEan());
                        if (codigoProduto <= Integer.MAX_VALUE) {
                            produtoBalanca = produtosBalanca.get((int) codigoProduto);
                        } else {
                            produtoBalanca = null;
                        }
                        if (produtoBalanca != null) {
                            imp.seteBalanca(true);
                            imp.setValidade(produtoBalanca.getValidade() > 1 ? produtoBalanca.getValidade() : rst.getInt("validade"));
                        } else {
                            imp.setValidade(rst.getInt("validade"));
                            imp.seteBalanca(true);
                        }
                    }

                    vResult.add(imp);
                    ProgressBar.setStatus("Carregando dados...Produtos..." + contador);
                    contador++;
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "p.codpro id_produto,\n"
                    + "p.codigo codigobarras,\n"
                    + "p.qtdun qtdembalagem\n"
                    + "from tabprocod p"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id_produto"));
                    imp.setEan(rst.getString("codigobarras"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ProdutoIMP> getEANsAtacado() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        String codigoBarras;
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                        "select\n"
                        + "a.codpreco, "
                        + "a.codprod, "
                        + "a.quantmin, "
                        + "a.prvapro precoatacao, "
                        + "p.prvapro precovenda\n"
                        + "from tabpreitem a\n"
                        + "inner join tabprofil p on p.codpro = a.codprod\n"
                        + "where a.quantmin > 1\n"
                        + "and a.prvapro < p.prvapro\n"
                        + "and p.codfil = " + getLojaOrigem() + "\n"
                        + "order by a.codprod"
            )) {
                while (rst.next()) {
                    int codigoAtual = new ProdutoAnteriorDAO().getCodigoAnterior2(getSistema(), getLojaOrigem(), rst.getString("codprod"));
                    
                    codigoBarras = rst.getString("codpreco") + "999999" + String.valueOf(codigoAtual);
                    
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("codprod"));
                    imp.setEan(codigoBarras);
                    imp.setQtdEmbalagem(rst.getInt("quantmin"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setAtacadoPreco(rst.getDouble("precoatacao"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos(OpcaoProduto opt) throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        String codigoBarras;

        if (opt == OpcaoProduto.ATACADO) {
            try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select\n"
                        + "a.codpreco, "
                        + "a.codprod, "
                        + "a.quantmin, "
                        + "a.prvapro precoatacao, "
                        + "p.prvapro precovenda\n"
                        + "from tabpreitem a\n"
                        + "inner join tabprofil p on p.codpro = a.codprod\n"
                        + "where a.quantmin > 1\n"
                        + "and a.prvapro < p.prvapro\n"
                        + "and p.codfil = " + getLojaOrigem() + "\n"
                        + "order by a.codprod"
                )) {
                    while (rst.next()) {
                        int codigoAtual = new ProdutoAnteriorDAO().getCodigoAnterior2(getSistema(), getLojaOrigem(), rst.getString("codprod"));

                        codigoBarras = rst.getString("codpreco") + "999999" + String.valueOf(codigoAtual);
                        
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("codprod"));
                        imp.setEan(codigoBarras);
                        imp.setQtdEmbalagem(rst.getInt("quantmin"));
                        imp.setPrecovenda(rst.getDouble("precovenda"));
                        imp.setAtacadoPreco(rst.getDouble("precoatacao"));
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
        List<FornecedorIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select codfor, nomfor, fanfor, endfor, baifor, pontoref, cidade,\n"
                    + "        uf, cep, nrendfor, fone1, fone2, fax,  email, contato, cnpj, inscest,\n"
                    + "        tpfornec, situacao, obs, tppessoa, represent01, fonerepre01,\n"
                    + "        represent02, fonerepre02, represent03, fonerepre03,\n"
                    + "        represent04, fonerepre04\n"
                    + "  from tabfor"
            )) {
                int contador = 1;
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("codfor"));
                    imp.setRazao(rst.getString("nomfor"));
                    imp.setFantasia(rst.getString("fanfor"));
                    imp.setEndereco(rst.getString("endfor"));
                    imp.setBairro(rst.getString("baifor"));
                    imp.setComplemento(rst.getString("pontoref"));
                    imp.setMunicipio(rst.getString("cidade").toUpperCase());
                    imp.setUf(rst.getString("uf").toUpperCase());
                    imp.setCep(rst.getString("cep"));
                    imp.setNumero(rst.getString("nrendfor"));
                    imp.setAtivo(("A".equals(rst.getString("situacao"))));
                    imp.setCnpj_cpf(Utils.formataNumero(rst.getString("cnpj")));
                    imp.setIe_rg(rst.getString("inscest"));
                    imp.setTel_principal(rst.getString("fone1"));

                    if (Utils.stringToLong(rst.getString("fone2")) > 0) {
                        FornecedorContatoIMP cont = new FornecedorContatoIMP();
                        cont.setImportSistema(getSistema());
                        cont.setImportLoja(getLojaOrigem());
                        cont.setImportId("1");
                        cont.setNome("TELEFONE 2");
                        cont.setTelefone(Utils.stringLong(rst.getString("fone2")));
                    }
                    if (Utils.stringToLong(rst.getString("fax")) > 0) {
                        FornecedorContatoIMP cont = new FornecedorContatoIMP();
                        cont.setImportSistema(getSistema());
                        cont.setImportLoja(getLojaOrigem());
                        cont.setImportId("2");
                        cont.setNome("FAX");
                        cont.setTelefone(Utils.stringLong(rst.getString("fax")));
                    }
                    if ((rst.getString("email") != null)
                            && (!rst.getString("email").trim().isEmpty())) {
                        FornecedorContatoIMP cont = imp.getContatos().make("2");
                        cont.setImportSistema(getSistema());
                        cont.setImportLoja(getLojaOrigem());
                        cont.setImportId("3");
                        cont.setNome("EMAIL");
                        cont.setEmail(rst.getString("email"));
                    }
                    if ((rst.getString("contato") != null)
                            && (!rst.getString("contato").trim().isEmpty())) {
                        FornecedorContatoIMP cont = imp.getContatos().make("3");
                        cont.setImportSistema(getSistema());
                        cont.setImportLoja(getLojaOrigem());
                        cont.setImportId("3");
                        cont.setNome("CONTATO");
                        cont.setEmail(rst.getString("contato"));
                    }
                    vResult.add(imp);
                    ProgressBar.setStatus("Carregando dados...Fornecedores..." + contador);
                    contador++;
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    distinct pf.codfor,\n" +
                    "    pf.codpro,\n" +
                    "    pf.codigo,\n" +
                    "    coalesce(fator.unidade, 'UN') unidade,\n" +
                    "    coalesce(fator.fator, 1) qtd\n" +
                    "from\n" +
                    "    tabprofor pf\n" +
                    "left join\n" +
                    "    (select\n" +
                    "        codpro,\n" +
                    "        codfor,\n" +
                    "        fator,\n" +
                    "        unidade\n" +
                    "    from\n" +
                    "        tabproforund\n" +
                    "    where\n" +
                    "        fator > 1) fator on (pf.codpro = fator.codpro) and\n" +
                    "        pf.codfor = fator.codfor\n" +
                    "order by\n" +
                    "    pf.codfor, pf.codpro"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rst.getString("codpro"));
                    imp.setIdFornecedor(rst.getString("codfor"));
                    imp.setCodigoExterno(rst.getString("codigo"));
                    imp.setQtdEmbalagem(rst.getDouble("qtd"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }
    
    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "    c.codcli id,\n" +
                    "    c.nomcli razao,\n" +
                    "    c.fancli fantasia,\n" +
                    "    c.dtcadastro,\n" +
                    "    c.dtnasc dtnascimento,\n" +
                    "    c.endcli endereco,\n" +
                    "    c.baicli bairro,\n" +
                    "    c.nrendcli numero,\n" +
                    "    c.cep,\n" +
                    "    c.cidade,\n" +
                    "    case when uf = '' then 'PA' else uf end as uf,\n" +
                    "    c.pontoref referencia,\n" +
                    "    c.fone1,\n" +
                    "    c.fone2,\n" +
                    "    c.fax,\n" +
                    "    c.email,\n" +
                    "    c.contato,\n" +
                    "    c.cgc cnpj,\n" +
                    "    c.inscest ie,\n" +
                    "    c.estcivil estadocivil,\n" +
                    "    cast((case sexo when '' then 0 else sexo end) as integer) sexo,\n" +
                    "    c.nmpai nomepai,\n" +
                    "    c.nmmae nomemae,\n" +
                    "    c.vlmtcli limite,\n" +
                    "    c.obs,\n" +
                    "    c.diaspag,\n" +
                    "    c.nmconjuge\n" +        
                    "from\n" +
                    "    tabcli c\n" +
                    "order by\n" +
                    "    cast(c.codcli as integer)")) {
                while(rs.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setCnpj(rs.getString("cnpj"));
                    imp.setInscricaoestadual(rs.getString("ie"));
                    if(rs.getString("fantasia") == null && "".equals(rs.getString("fantasia"))) {
                        imp.setFantasia(rs.getString("razao"));
                    } else {
                        imp.setFantasia(rs.getString("fantasia"));
                    }
                    imp.setDataCadastro(rs.getDate("dtcadastro"));
                    imp.setDataNascimento(rs.getDate("dtnascimento"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setCep(rs.getString("cep"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("uf"));
                    imp.setTelefone(rs.getString("fone1"));
                    if(rs.getString("fone2") != null && !"".equals(rs.getString("fone2").trim())) {
                        imp.addContato("1", "TELEFONE 2", rs.getString("fone2"), "", "");
                    }
                    imp.setFax(rs.getString("fax"));
                    imp.setEmail(rs.getString("email"));
                    if(rs.getString("contato") != null && !"".equals(rs.getString("contato").trim())) {
                        imp.addContato("2", "CONTATO", rs.getString("contato"), "", "");
                    }
                    imp.setSexo(rs.getInt("sexo") == 0 ? TipoSexo.MASCULINO : TipoSexo.FEMININO);
                    imp.setNomePai(rs.getString("nomepai"));
                    imp.setNomeMae(rs.getString("nomemae"));
                    imp.setValorLimite(rs.getDouble("limite"));
                    if(rs.getString("obs") != null && !"".equals(rs.getString("obs").trim())) {
                        imp.setObservacao(rs.getString("obs"));
                    }
                    imp.copiarEnderecoParaCobranca();
                    imp.setPermiteCheque(true);
                    imp.setPermiteCreditoRotativo(true);
                    imp.setPrazoPagamento(rs.getInt("diaspag"));
                    imp.setNomeConjuge(rs.getString("nmconjuge"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
    
    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "    r.codtit id,\n" +
                    "    r.codcli idcliente,\n" +
                    "    c.cgc cnpj,\n" +
                    "    r.nrnota documento,\n" +
                    "    r.nomcli razao,\n" +
                    "    r.dtemitit emissao,\n" +
                    "    r.dtventit vencimento,\n" +
                    "    r.dtpagtit pagamento,\n" +
                    "    r.vlduptit valor,\n" +
                    "    r.vlabatit valorabatido,\n" +
                    "    r.vlpagtit valorpago,\n" +
                    "    r.obstit observacao\n" +
                    "from\n" +
                    "    titulor r\n" +
                    "join tabcli c on r.codcli = c.codcli\n" +
                    "where\n" +
                    "    r.dtpagtit is null or (r.vlpagtit < r.vlduptit)\n" +
                    "order by\n" +
                    "    r.dtemitit")) {
                while(rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rs.getString("id"));
                    imp.setIdCliente(rs.getString("idcliente"));
                    imp.setCnpjCliente(rs.getString("cnpj"));
                    imp.setDataEmissao(rs.getDate("emissao"));
                    imp.setDataVencimento(rs.getDate("vencimento"));
                    imp.setNumeroCupom(rs.getString("documento"));
                    imp.setValor(rs.getDouble("valorabatido"));
                    imp.setObservacao(rs.getString("observacao"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
