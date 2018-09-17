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
import vrimplantacao2.vo.importacao.FornecedorContatoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

public class InteragemDAO extends InterfaceDAO {

    public String i_arquivoXLS;
    public String id_loja;

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
        if ((id_loja != null) && (!id_loja.trim().isEmpty())) {
            return "Interagem" + id_loja;
        } else {
            return "Interagem";
        }
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "p.codpro as id,\n"
                    + "coalesce(p.codbarun, '0') as ean,\n"
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
                    imp.setEan(rst.getString("ean"));
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
                    imp.setPrecovenda(MathUtils.trunc(rst.getDouble("precovenda"), 2));
                    imp.setCustoComImposto(MathUtils.trunc(rst.getDouble("custocomimposto"), 2));
                    imp.setCustoSemImposto(MathUtils.trunc(rst.getDouble("custocomimposto"), 2));
                    imp.setEstoque(MathUtils.trunc(rst.getDouble("estoque"), 2));
                    imp.setIcmsCst(Integer.parseInt(Utils.formataNumero(rst.getString("icms_cst"))));
                    imp.setIcmsAliq(rst.getDouble("icms_aliquota"));
                    imp.setIcmsReducao(rst.getDouble("icms_reduzido"));

                    if ((rst.getString("ean") != null)
                            && (!rst.getString("ean").trim().isEmpty())
                            && (rst.getString("ean").trim().length() >= 4)
                            && (rst.getString("ean").trim().length() <= 6)
                            && ("S".equals(rst.getString("balanca").trim()))) {
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
                            imp.setValidade(0);
                            imp.seteBalanca(false);
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
                    + "        uf, cep, fone1, fone2, fax,  email, contato, cnpj, inscest,\n"
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
                    imp.setAtivo(("A".equals(rst.getString("situacao"))));
                    imp.setCnpj_cpf(Utils.formataNumero(rst.getString("cnpj")));
                    imp.setIe_rg(rst.getString("inscest"));

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
                    "select codfor, codpro, codigo\n"
                    + "  from tabprofor"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rst.getString("codpro"));
                    imp.setIdFornecedor(rst.getString("codfor"));
                    imp.setCodigoExterno(rst.getString("codigo"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }
}
