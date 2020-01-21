package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Importacao
 */
public class AtenasSQLSERVERDAO extends InterfaceDAO {
    public String v_lojaMesmoId;

    @Override
    public String getSistema() {
        return "Atenas";
    }

    public List<Estabelecimento> getLojasCliente() throws SQLException {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "     codigo cod_empresa,\n"
                    + "     nomefantasia descricao\n"
                    + "from CADempresa")) {
                while (rs.next()) {
                    result.add(new Estabelecimento(rs.getString("cod_empresa"), rs.getString("descricao")));
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
                    + "     codcategoria Merc1ID, \n"
                    + "     descricaocategoria Merc1Descricao,\n"
                    + "     CodSubCategoria Merc2ID, \n"
                    + "     DescricaoSubCategoria Merc2Descricao,\n"
                    + "     CodMarca Merc3ID,\n"
                    + "     DescricaoMarca Merc3Descricao\n"
                    + "from CadProdutos")) {
                while (rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rs.getString("Merc1ID"));
                    imp.setMerc1Descricao(rs.getString("Merc1Descricao"));
                    imp.setMerc2ID(rs.getString("Merc2ID"));
                    imp.setMerc2Descricao(rs.getString("Merc2Descricao"));
                    imp.setMerc3ID(rs.getString("Merc3ID"));
                    imp.setMerc3Descricao(rs.getString("Merc3Descricao"));

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
            try (ResultSet rs = stm.executeQuery(
                      "select \n"
                    + "     p.codigo importid,\n"
                    + "     data_cadastro datacadastro,\n"
                    + "     alterado_em dataalteracao,\n"
                    + "     codigobarras ean,\n"
                    + "     unidade tipoembalagem,\n"
                    + "     case when balanca = 'NAO' then 0 else 1 end ebalanca,\n"
                    + "     p.Descricao descricaocompleta,\n"
                    + "     descricaoredusida descricaoreduzida,\n"
                    + "     p.Descricao descricaogondola,\n"
                    + "     codcategoria codmercadologico1,\n"
                    + "     codsubcategoria codmercadologico2,\n"
                    + "     codmarca codmercadologico3,\n"
                    + "     codfamilia idfamiliaproduto,\n"
                    + "     peso_bruto pesobruto,\n"
                    + "     peso_liquido pesoliquido,\n"
                    + "     estoqueminimo,\n"
                    + "     estoqueatual estoque,\n"
                    + "     margemlucro margem,\n"
                    + "     precocusto custosemimposto,\n"
                    + "     precocustofinal custocomimposto,\n"
                    + "     precovenda,\n"
                    + "     case when oculto = 'NAO' then 1 else 0 end situacaocadastro,\n"
                    + "     classificacaofiscal ncm,\n"
                    + "     c.cest cest,\n"
                    + "     cst_pis piscofinscstdebito,\n"
                    + "     cst_pis_entrada piscofinscstcredito,\n"
                    + "     cod_nat_receita piscofinsnaturezareceita,\n"
                    + "     ali.cst icmscstsaida,\n"
                    + "     ali.porcentagem icmsaliqsaida,\n"
                    + "     ali.redusidade icmsreducaosaida\n"
                    + "from cadprodutos p\n"
                    + "		left join cadaliquotasicms ali\n"
                    + "		on p.icmsentrada = ali.descricao\n"
                    + "		left join icms_cest c\n"
                    + "		on p.icms_cest = c.codigo\n"
                    + "	order by p.codigo")) {
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("importid"));
                    imp.setDataCadastro(rs.getDate("datacadastro"));
                    imp.setDataAlteracao(rs.getDate("dataalteracao"));
                    imp.setEan(rs.getString("ean"));
                    imp.setTipoEmbalagem(rs.getString("tipoembalagem"));
                    imp.seteBalanca(rs.getBoolean("ebalanca"));
                    imp.setDescricaoCompleta(Utils.acertarTexto(rs.getString("descricaocompleta")));
                    imp.setDescricaoReduzida(Utils.acertarTexto(rs.getString("descricaoreduzida")));
                    imp.setDescricaoGondola(Utils.acertarTexto(rs.getString("descricaogondola")));
                    imp.setCodMercadologico1(rs.getString("codmercadologico1"));
                    imp.setCodMercadologico2(rs.getString("codmercadologico2"));
                    imp.setCodMercadologico3(rs.getString("codmercadologico3"));
                    imp.setIdFamiliaProduto(rs.getString("idfamiliaproduto"));
                    imp.setPesoBruto(rs.getDouble("pesobruto"));
                    imp.setPesoLiquido(rs.getDouble("pesoliquido"));
                    imp.setEstoqueMinimo(rs.getDouble("estoqueminimo"));
                    imp.setEstoque(rs.getDouble("estoque"));
                    imp.setMargem(rs.getDouble("margem"));
                    imp.setCustoComImposto(rs.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rs.getDouble("custosemimposto"));
                    imp.setPrecovenda(rs.getDouble("precovenda"));
                    imp.setSituacaoCadastro(rs.getInt("situacaocadastro"));
                    imp.setNcm(rs.getString("ncm"));
                    imp.setCest(rs.getString("cest"));
                    imp.setPiscofinsCstDebito(rs.getInt("piscofinscstdebito"));
                    imp.setPiscofinsCstCredito(rs.getInt("piscofinscstcredito"));
                    imp.setPiscofinsNaturezaReceita(rs.getInt("piscofinsnaturezareceita"));
                    imp.setIcmsCstSaida(rs.getInt("icmscstsaida"));
                    imp.setIcmsAliqSaida(rs.getDouble("icmsaliqsaida"));
                    imp.setIcmsReducaoSaida(rs.getDouble("icmsreducaosaida"));

                    //imp.setValidade(rs.getInt("validade"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
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
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "     codigo,\n"
                    + "     CodigoFornecedor,\n"
                    + "     codigo externo\n"
                    + "from CadProdutos\n"
                    + "     where CodigoFornecedor != 0")) {
                while (rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setIdProduto(rs.getString("codigo"));
                    imp.setIdFornecedor(rs.getString("CodigoFornecedor"));
                    imp.setCodigoExterno(rs.getString("externo"));

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
                    "select\n"
                    + "     codigo importId,\n"
                    + "     rasaosocialnome razao,\n"
                    + "     nomefantasia fantasia,\n"
                    + "     cnpjcpf cnpj_cpf,\n"
                    + "     ierg ie_rg,\n"
                    + "     endereco,\n"
                    + "     numero,\n"
                    + "     complementoendereco,\n"
                    + "     bairro,\n"
                    + "     codigo_municipio ibge_municipio,\n"
                    + "     cidade municipio,\n"
                    + "     uf,\n"
                    + "     cep,\n"
                    + "     telefone tel_principal,\n"
                    + "     datacadastro,\n"
                    + "     obs observacao,\n"
                    + "     VENDEDORNOME,\n"
                    + "     VENDEDORTELEFONE,\n"
                    + "     VENDEDORCELULAR,\n"
                    + "     VENDEDOREMAIL\n"
                    + "from CADFORNECEDORES\n"
                    + "     order by 1")) {
                while (rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rs.getString("importId"));
                    imp.setRazao(Utils.acertarTexto(rs.getString("razao")));
                    imp.setFantasia(Utils.acertarTexto(rs.getString("fantasia")));
                    imp.setCnpj_cpf(rs.getString("cnpj_cpf"));
                    imp.setIe_rg(rs.getString("ie_rg"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complementoendereco"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("municipio"));
                    imp.setIbge_municipio(rs.getInt("ibge_municipio"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));
                    imp.setTel_principal(rs.getString("tel_principal"));
                    imp.setDatacadastro(rs.getDate("datacadastro"));
                    imp.setObservacao(rs.getString("observacao"));

                    imp.addContato("1", "VENDEDORNOME", "VENDEDORTELEFONE", "VENDEDORCELULAR", TipoContato.NFE, rs.getString("VENDEDOREMAIL"));

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
                    "select\n"
                    + "    codigo,\n"
                    + "    nome,\n"
                    + "    rg,\n"
                    + "    cpf,\n"
                    + "    estadocivil,\n"
                    + "    profissao,\n"
                    + "    empresa,\n"
                    + "    renda,\n"
                    + "    limite,\n"
                    + "    data_cadastro,\n"
                    + "    ref2,\n"
                    + "    nascimento,\n"
                    + "    sexo,\n"
                    + "    apelido,\n"
                    + "    endereco,\n"
                    + "    bairro,\n"
                    + "    cidade,\n"
                    + "    numero,\n"
                    + "    uf,\n"
                    + "    cep,\n"
                    + "    complemento,\n"
                    + "    situacao,\n"
                    + "    telefone1,\n"
                    + "    telefone2,\n"
                    + "    telefone3,\n"
                    + "    celular,\n"
                    + "    email,\n"
                    + "    condpgto\n"
                    + "from\n"
                    + "    c000007\n"
                    + "order by\n"
                    + "    nome")) {
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rs.getString("codigo"));
                    imp.setRazao(rs.getString("nome"));
                    imp.setFantasia(rs.getString("apelido"));
                    imp.setInscricaoestadual(rs.getString("rg"));
                    imp.setCnpj(rs.getString("cpf"));
                    imp.setValorLimite(rs.getDouble("limite"));
                    imp.setDataCadastro(rs.getDate("data_cadastro"));
                    /*String data = rs.getString("nascimento").trim();
                     if(data.matches("[0-9]{2}/[0-9]{2}/[0-9]{4}")) {
                     imp.setDataNascimento(rs.getDate("nascimento"));
                     }*/
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setAtivo(rs.getInt("situacao") == 1);
                    imp.setTelefone(rs.getString("telefone1"));
                    imp.setCelular(rs.getString("celular"));
                    imp.setEmail(rs.getString("email") == null ? "" : rs.getString("email"));
                    imp.setObservacao(rs.getString("condpgto") == null ? "" : "Cond. Pagto: " + rs.getString("condpgto"));

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
    }

    public Iterable<Estabelecimento> getLojas() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
