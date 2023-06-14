/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2_5.dao.sistema;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao2_5.dao.conexao.ConexaoFirebird;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Bruno
 * 
 * SISTEMA REFATORADO DA 2.0 PARA 2.5 E NÃO TESTADO FAVOR CONFERIR OS METODOS
 */
public class Atenas2_5DAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "Atenas";
    }

    public List<Estabelecimento> getLojasCliente() throws SQLException {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "    codigo,\n"
                    + "    fantasia\n"
                    + "from\n"
                    + "    c999999")) {
                while (rs.next()) {
                    result.add(new Estabelecimento(rs.getString("codigo"), rs.getString("fantasia")));
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "    codigo,\n"
                    + "    grupo\n"
                    + "from\n"
                    + "    c000017")) {
                while (rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rs.getString("codigo"));
                    imp.setMerc1Descricao(rs.getString("grupo"));
                    imp.setMerc2ID(rs.getString("codigo"));
                    imp.setMerc2Descricao(rs.getString("grupo"));
                    imp.setMerc3ID(rs.getString("codigo"));
                    imp.setMerc3Descricao(rs.getString("grupo"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "    codigo,\n"
                    + "    codbarra,\n"
                    + "    usa_balanca,\n"
                    + "    validade,\n"
                    + "    situacao,\n"
                    + "    produto,\n"
                    + "    unidade,\n"
                    + "    data_cadastro,\n"
                    + "    codgrupo merc1,\n"
                    + "    precocusto,\n"
                    + "    precovenda,\n"
                    + "    e.estoque_atual estoque,\n"
                    + "    estoqueminimo,\n"
                    + "    classificacao_fiscal ncm,\n"
                    + "    cst,\n"
                    + "    situacao_tributaria,\n"
                    + "    csosn,\n"
                    + "    aliquota,\n"
                    + "    cest\n"
                    + "from\n"
                    + "    c000025 p\n"
                    + "left join c000100 e on p.codigo = e.codproduto\n"
                    + "order by\n"
                    + "    p.codigo")) {
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("codigo"));
                    imp.setEan(rs.getString("codbarra"));
                    imp.setDescricaoCompleta(Utils.acertarTexto(rs.getString("produto")));
                    imp.setDescricaoReduzida(Utils.acertarTexto(rs.getString("produto")));
                    imp.setDescricaoGondola(Utils.acertarTexto(rs.getString("produto")));
                    imp.seteBalanca(rs.getInt("usa_balanca") == 1);
                    //imp.setValidade(rs.getInt("validade"));
                    imp.setSituacaoCadastro(1);
                    imp.setTipoEmbalagem(rs.getString("unidade"));
                    imp.setDataCadastro(rs.getDate("data_cadastro"));
                    imp.setCodMercadologico1(rs.getString("merc1"));
                    imp.setCodMercadologico2(rs.getString("merc1"));
                    imp.setCodMercadologico3(rs.getString("merc1"));
                    imp.setPrecovenda(rs.getDouble("precovenda"));
                    imp.setCustoComImposto(rs.getDouble("precocusto"));
                    imp.setCustoSemImposto(rs.getDouble("precocusto"));
                    imp.setEstoque(rs.getDouble("estoque"));
                    imp.setEstoqueMinimo(rs.getDouble("estoqueminimo"));
                    imp.setNcm(rs.getString("ncm"));
                    imp.setCest(rs.getString("cest"));
                    imp.setIcmsCst(rs.getInt("cst"));
                    imp.setIcmsAliq(rs.getDouble("aliquota"));

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
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "    codigo,\n"
                    + "    codfornecedor,\n"
                    + "    codigo externo\n"
                    + "from\n"
                    + "    c000025\n"
                    + "where\n"
                    + "    codfornecedor is not null and codfornecedor != ''")) {
                while (rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rs.getString("codigo"));
                    imp.setIdFornecedor(rs.getString("codfornecedor"));
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
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "    codigo,\n"
                    + "    nome,\n"
                    + "    fantasia,\n"
                    + "    cnpj,\n"
                    + "    ie,\n"
                    + "    endereco,\n"
                    + "    numero,\n"
                    + "    data,\n"
                    + "    bairro,\n"
                    + "    cidade,\n"
                    + "    uf,\n"
                    + "    cep,\n"
                    + "    complemento,\n"
                    + "    telefone1,\n"
                    + "    contato1,\n"
                    + "    celular1,\n"
                    + "    email\n"
                    + "from\n"
                    + "    c000009\n"
                    + "order by\n"
                    + "    codigo")) {
                while (rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("codigo"));
                    imp.setRazao(Utils.acertarTexto(rs.getString("nome")));
                    imp.setFantasia(Utils.acertarTexto(rs.getString("fantasia")));
                    imp.setCnpj_cpf(rs.getString("cnpj"));
                    imp.setIe_rg(rs.getString("ie"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setTel_principal(rs.getString("telefone1"));
                    if (rs.getString("celular1") != null) {
                        imp.addContato("1",
                                rs.getString("contato1") != null ? rs.getString("contato1") : "CONTATO",
                                null,
                                rs.getString("celular1"),
                                TipoContato.COMERCIAL, null);
                    }
                    if (rs.getString("email") != null) {
                        imp.addContato("2", "EMAIL", null, null, TipoContato.NFE, rs.getString("email"));
                    }

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
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
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
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

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
