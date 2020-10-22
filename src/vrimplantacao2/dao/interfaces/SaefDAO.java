package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.importacao.ClienteIMP;
//import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Alan
 */
public class SaefDAO extends InterfaceDAO {

    public String v_lojaMesmoId;

    @Override
    public String getSistema() {
        return "Saef" + v_lojaMesmoId;
    }

    public List<Estabelecimento> getLojas() throws SQLException {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	cdConfiguracao id,\n"
                    + "	nmEmpresa razao\n"
                    + "from\n"
                    + "	Configuracao")) {
                while (rs.next()) {
                    result.add(new Estabelecimento(rs.getInt("id") + "", rs.getString("razao")));
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "     g.cdgrupo as m1,\n"
                    + "     nmgrupo as m1desc,\n"
                    + "     cdsubgrupo as m2,\n"
                    + "     nmsubgrupo as m2desc,\n"
                    + "     cdsubgrupo as m3,\n"
                    + "     nmsubgrupo as m3desc\n"
                    + "from\n"
                    + "     Grupo g\n"
                    + "     left join SubGrupo sg\n"
                    + "		on g.cdgrupo = sg.cdgrupo \n"
                    + "order by 1,3,5")) {
                while (rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rs.getString("m1"));
                    imp.setMerc1Descricao(rs.getString("m1desc"));
                    imp.setMerc2ID(rs.getString("m2"));
                    imp.setMerc2Descricao(rs.getString("m2desc"));
                    imp.setMerc3ID(rs.getString("m3"));
                    imp.setMerc3Descricao(rs.getString("M3desc"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

        @Override
    public List<ProdutoIMP> getProdutos(OpcaoProduto opt) throws Exception {

        if (opt == OpcaoProduto.ATACADO) {
            List<ProdutoIMP> Result = new ArrayList<>();
            try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
                try (ResultSet rs = stm.executeQuery(
                        "select  \n"
                        + "	e.codprod id_produto, \n"
                        + "	rtrim(e.barra_emb) ean, \n"
                        + "	e.qtd qtdembalagem,\n"
                        + "	e.preco_unit precoAtacado,\n"
                        + "	p.PRECO_UNIT precoVenda\n"
                        + "from \n"
                        + "	EMBALAGENS e, PRODUTOS p\n"
                        + "where \n"
                        + "	e.CODPROD = p.CODPROD and\n"
                        + "	barra_emb is not null and\n"
                        + "	coalesce(e.PRECO_UNIT, 0) > 0\n"
                        + "order by 1"
                )) {
                    while (rs.next()) {
                        ProdutoIMP imp = new ProdutoIMP();

                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rs.getString("id_produto"));
                        imp.setEan(rs.getString("ean"));
                        imp.setQtdEmbalagem(rs.getInt("qtdembalagem"));
                        imp.setAtacadoPreco(rs.getDouble("precoAtacado"));
                        imp.setPrecovenda(rs.getDouble("precoVenda"));

                        Result.add(imp);
                    }
                }
            }
            return Result;
        }

        return null;
    }
    
    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select DISTINCT \n"
                    + "	cdproduto importId,\n"
                    + "	dtalteracao dataCadastro,\n"
                    + "	dtalteracao dataAlteracao,\n"
                    + "	cdFabricante ean,\n"
                    + "	cean ean2,\n"
                    + "	dsunidade tipoEmbalagem,\n"
                    + "	CASE WHEN LEN(cdFabricante) <= 7 and cdFabricante != '' and dsunidade = 'KG' THEN 1 else 0 END ebalanca,\n"
                    + "	nrdiasvalidade validade,\n"
                    + "	nmproduto descricaoCompleta,\n"
                    + "	dsprodutonota descricaoReduzida,\n"
                    + "	dsprodutonota descricaoGondola,\n"
                    + "	cdgrupo Mercadologico1,\n"
                    + "	cdsubgrupo Mercadologico2,\n"
                    + "	cdsubgrupo Mercadologico3,\n"
                    + "	nrqtdmaxima estoqueMaximo,\n"
                    + "	nrqtdminima estoqueMinimo,\n"
                    + "	nrqtdreal estoque,\n"
                    + "	nrmargem margem,\n"
                    + "	ROUND(vlcompra,2) custoSemImposto,\n"
                    + "	ROUND(nrcustofinal_v,2) custoComImposto,\n"
                    + "	vlcustomedio custoMedio,\n"
                    + "	ROUND(vlpreco,2) precovenda,\n"
                    + "	CASE WHEN dsativo = 'S' THEN 1 else 0 end situacaoCadastro,\n"
                    + "	dsmercosul ncm,\n"
                    + "	SUBSTRING(cf.dsPis,1,2) piscofinsCstDebito,\n"
                    + "	SUBSTRING(cf.dsPis,1,2) piscofinsCstCredito,\n"
                    + "	COALESCE(dsCodigoNaturezaReceita,'') natreceita,\n"
                    + "	dsSituacaoTributaria icmsCstSaida,\n"
                    + "	a.vlvalor icmsAliqSaida,\n"
                    + "	a.nrreducao icmsReducaoSaida,\n"
                    + "	dsSituacaoTributaria icmsCstConsumidor,\n"
                    + "	a.vlvalor icmsAliqConsumidor,\n"
                    + "	a.nrreducao icmsReducaoConsumidor,\n"
                    + "	vlatacado atacadoPreco,\n"
                    + "	nrmargematacado atacadoPorcentagem\n"
                    + "from produto p\n"
                    + "     left join Aliquotas a on p.dsCodtributacao = a.dsCodtributacao\n"
                    + "     left join ClFiscal cf on cf.cdclassificacao = p.dsmercosul\n"
                    + "     left join tb_cest cest on cf.idcest = cest.idcest\n"
                    + "where\n"
                    + "	p.dsAtivo = 'S'\n"
                    + "	and cdFabricante != ''"
                )) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("importid"));
                    imp.setDataCadastro(rs.getDate("datacadastro"));
                    imp.setDataAlteracao(rs.getDate("dataalteracao"));

                    long longEAN = Utils.stringToLong(imp.getEan(), -2);
                    String strEAN = String.valueOf(longEAN);

                    if (strEAN.startsWith("2") && strEAN.length() == 7) {
                        final String eanBal = strEAN.substring(1);
                        ProdutoBalancaVO bal = produtosBalanca.get(Utils.stringToInt(eanBal, -2));
                        if (bal != null) {
                            imp.setEan(String.valueOf(bal.getCodigo()));
                            imp.seteBalanca(true);
                            imp.setValidade(bal.getValidade());
                            imp.setTipoEmbalagem(bal.getPesavel().equals("U") ? "UN" : "KG");
                        } else {
                            imp.setEan(rs.getString("ean"));
                            imp.seteBalanca(rs.getInt("ebalanca") == 1);
                            imp.setTipoEmbalagem(rs.getString("tipoembalagem"));
                            imp.setValidade(rs.getInt("validade"));
                        }
                    } else {
                        imp.setEan(rs.getString("ean"));
                        imp.seteBalanca(rs.getInt("ebalanca") == 1);
                        imp.setTipoEmbalagem(rs.getString("tipoembalagem"));
                        imp.setValidade(rs.getInt("validade"));
                    }

                    imp.setDescricaoCompleta(Utils.acertarTexto(rs.getString("descricaocompleta")));
                    imp.setDescricaoReduzida(Utils.acertarTexto(rs.getString("descricaoReduzida")));
                    imp.setDescricaoGondola(Utils.acertarTexto(rs.getString("descricaoGondola")));
                    imp.setCodMercadologico1(rs.getString("Mercadologico1"));
                    imp.setCodMercadologico2(rs.getString("Mercadologico2"));
                    imp.setCodMercadologico3(rs.getString("Mercadologico3"));
                    imp.setEstoqueMaximo(rs.getDouble("estoquemaximo"));
                    imp.setEstoqueMinimo(rs.getDouble("estoqueminimo"));
                    imp.setEstoque(rs.getDouble("estoque"));
                    imp.setMargem(rs.getDouble("margem"));
                    imp.setCustoSemImposto(rs.getDouble("custosemimposto"));
                    imp.setCustoComImposto(rs.getDouble("custocomimposto"));
                    imp.setCustoMedio(rs.getDouble("custoMedio"));
                    imp.setPrecovenda(rs.getDouble("precovenda"));

                    imp.setSituacaoCadastro(rs.getInt("situacaocadastro") == 1 ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    imp.setNcm(rs.getString("ncm"));
                    
                    imp.setPiscofinsCstCredito(rs.getInt("piscofinscstcredito"));
                    imp.setPiscofinsCstDebito(rs.getInt("piscofinsCstDebito"));
                    imp.setPiscofinsNaturezaReceita(rs.getString("natreceita"));
                    
                    imp.setIcmsCstSaida(rs.getInt("icmsCstSaida"));
                    imp.setIcmsAliqSaida(rs.getDouble("icmsAliqSaida"));
                    imp.setIcmsReducaoSaida(rs.getDouble("icmsreducaosaida"));
                    
                    imp.setIcmsCstConsumidor(rs.getInt("icmsCstConsumidor"));
                    imp.setIcmsAliqConsumidor(rs.getDouble("icmsAliqConsumidor"));
                    imp.setIcmsReducaoConsumidor(rs.getDouble("icmsReducaoConsumidor"));
                    
                    imp.setAtacadoPreco(rs.getDouble("atacadoPreco"));
                    imp.setAtacadoPorcentagem(rs.getDouble("atacadoPorcentagem"));
                                        
                    /*
                    imp.setPesoBruto(rs.getDouble("pesobruto"));
                    imp.setPesoLiquido(rs.getDouble("pesoliquido"));
                    imp.setCustoAnteriorComImposto(rs.getDouble("custoAnteriorComImposto"));
                    imp.setCest(rs.getString("cest"));
                    imp.setIcmsCstEntrada(rs.getInt("icmscstentrada"));
                    imp.setIcmsAliqEntrada(rs.getDouble("icmsaliqentrada"));
                    imp.setIcmsReducaoEntrada(rs.getDouble("icmsreducaoentrada"));
                    */
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    /*@Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    " select distinct\n"
                    + "     CodFamilia,\n"
                    + "     DescricaoFamilia\n"
                    + " from CadProdutos\n"
                    + "     order by CodFamilia")) {
                while (rs.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rs.getString("CodFamilia"));
                    imp.setDescricao(rs.getString("DescricaoFamilia"));

                    result.add(imp);
                }
            }
        }
        return result;
    }*/

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "     cdFornecedor idfornecedor,\n"
                    + "     cdProduto idproduto,\n"
                    + "     codigo codigoexterno\n"
                    + "from codFornecedor cf")) {
                while (rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setIdFornecedor(rs.getString("idfornecedor"));
                    imp.setIdProduto(rs.getString("idproduto"));
                    imp.setCodigoExterno(rs.getString("codigoexterno"));
                    //imp.setQtdEmbalagem(rs.getDouble("qtdEmbalagem"));

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
            try (ResultSet rs = stm.executeQuery(
                    // SOMENTE CLIENTES COM CPF/CNPJ VÁLIDOS
                    "select\n"
                    + "	pf.cdPessoa id,\n"
                    + "	dsCPF cpf,\n"
                    + "	nmpessoa razao,\n"
                    + "	nmpessoa fantasia,\n"
                    + "	case when dsativo = 'S' then 1 else 0 end ativo,\n"
                    + "	nmLogradouro endereco,\n"
                    + "	nrNumero numero,\n"
                    + "	dsComplemento complemento,\n"
                    + "	dsbairro bairro,\n"
                    + "	cdmunicipio municipioIBGE,\n"
                    + "	dscidade cidade,\n"
                    + "	dsuf uf,\n"
                    + "	dsCEP cep,\n"
                    + "	dtNascimento dataNascimento,\n"
                    + "	dtcadastro dataCadastro,\n"
                    + "	dslimcredito valorLimite,\n"
                    + "	nrdiavenc diaVencimento,\n"
                    + "	nmtelefone telefone,\n"
                    + "	dsemail email\n"
                    + "from\n"
                    + "	P_Fisica pf\n"
                    + "	left join Pessoa p on p.cdPessoa = pf.cdPessoa \n"
                    + "	left join Endereco e on e.cdPessoa = p.cdpessoa\n"
                    + "	left join Cliente c on c.cdpessoa = p.cdpessoa\n"
                    + "	left join Telefone t on t.cdPessoa  = p.cdPessoa \n"
                    + "where\n"
                    + "	pf.dsCPF <> '' and pf.dsCPF is not NULL \n")) {
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rs.getString("id"));
                    imp.setCnpj(rs.getString("cpf"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setAtivo(rs.getBoolean("ativo"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipioIBGE(rs.getString("municipioIBGE"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));
                    imp.setDataNascimento(rs.getDate("datanascimento"));
                    imp.setDataCadastro(rs.getDate("datacadastro"));
                    imp.setValorLimite(rs.getDouble("valorlimite"));
                    imp.setDiaVencimento(rs.getInt("diavencimento"));
                    imp.setTelefone(rs.getString("telefone"));
                    imp.setEmail(rs.getString("email"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select  \n"
                    + "	pj.cdPessoa importId,\n"
                    + "	nmRazao razao,\n"
                    + "	nmpessoa fantasia,\n"
                    + "	CGC cnpj_cpf,\n"
                    + "	dsInscricaoEstadual ie_rg,\n"
                    + "	dssuframa suframa,\n"
                    + "	case when dsativo = 'S' then 1 else 0 end ativo,\n"
                    + "	nmLogradouro endereco,\n"
                    + "	nrNumero numero,\n"
                    + "	dsComplemento complemento,\n"
                    + "	dsbairro bairro,\n"
                    + "	cdmunicipio ibge_municipio,\n"
                    + "	dscidade municipio,\n"
                    + "	dsuf uf,\n"
                    + "	dsCEP cep,\n"
                    + "	nmtelefone tel_principal,\n"
                    + "	dtcadastro datacadastro\n"
                    + "from\n"
                    + "	P_Juridica pj \n"
                    + "	left join Pessoa p on p.cdPessoa = pj.cdPessoa \n"
                    + "	left join Endereco e on e.cdPessoa = p.cdpessoa\n"
                    + "	left join Cliente c on c.cdpessoa = p.cdpessoa\n"
                    + "	left join Telefone t on t.cdPessoa  = p.cdPessoa\n"
                    + "order by 1")) {
                while (rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rs.getString("importId"));
                    imp.setRazao(Utils.acertarTexto(rs.getString("razao")));
                    imp.setFantasia(Utils.acertarTexto(rs.getString("fantasia")));
                    imp.setCnpj_cpf(rs.getString("cnpj_cpf"));
                    imp.setIe_rg(rs.getString("ie_rg"));
                    imp.setSuframa(rs.getString("suframa"));
                    imp.setAtivo(rs.getBoolean("ativo"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setIbge_municipio(rs.getInt("ibge_municipio"));
                    imp.setMunicipio(rs.getString("municipio"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));
                    imp.setTel_principal(rs.getString("tel_principal"));
                    imp.setDatacadastro(rs.getDate("datacadastro"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    /*@Override
     public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
     List<CreditoRotativoIMP> result = new ArrayList<>();
     try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
     try (ResultSet rs = stm.executeQuery(
     "select\n"
     + "    codigo,\n"
     + "    codvenda,\n"
     + "    codcaixa,\n"
     + "    codcliente,\n"
     + "    data_emissao,\n"
     + "    data_vencimento,\n"
     + "    valor_original,\n"
     + "    documento\n"
     + "from\n"
     + "    c000049\n"
     + "where\n"
     + "    situacao = 1 and\n"
     + "    data_pagamento is null\n"
     + "order by\n"
     + "    data_emissao")) {
     while (rs.next()) {
     CreditoRotativoIMP imp = new CreditoRotativoIMP();
     imp.setId(rs.getString("codigo"));
     imp.setNumeroCupom(rs.getString("codvenda"));
     imp.setEcf(rs.getString("codcaixa"));
     imp.setIdCliente(rs.getString("codcliente"));
     imp.setDataEmissao(rs.getDate("data_emissao"));
     imp.setDataVencimento(rs.getDate("data_vencimento"));
     imp.setValor(rs.getDouble("valor_original"));
     imp.setObservacao("Doc.: " + rs.getString("documento"));

     result.add(imp);
     }
     }
     }
     return result;
     }*/
}
