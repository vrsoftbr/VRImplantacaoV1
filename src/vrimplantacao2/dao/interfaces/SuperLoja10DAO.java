/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoMySQL;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
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
public class SuperLoja10DAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "SuperLoja10";
    }

    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "EMPRESA, \n"
                    + "CERT_NOME\n"
                    + "from EMPRESAS\n"
                    + "order by EMPRESA"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("EMPRESA"), rst.getString("CERT_NOME")));
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select GRUPO, DESCRICAO from GRUPO_ESTOQUE order by GRUPO"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("GRUPO"));
                    imp.setMerc1Descricao(rst.getString("DESCRICAO"));
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

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "     p.PRODUTO,\n"
                    + "     p.DESCRICAO,\n"
                    + "     p.REDUZIDO,\n"
                    + "     p.UNIDADEMEDIDA,\n"
                    + "     p.GRUPOESTOQUE,\n"
                    + "     p.NCM,\n"
                    + "     p.TIPOTRIBUTACAO,\n"
                    + "     p.EAN13,\n"
                    + "     p.INATIVO,,\n"
                    + "     p.PESO_BRUTO,\n"
                    + "     p.PESO_LIQUIDO,\n"
                    + "     p.EMBALAGEM,\n"
                    + "     p.CADASTRO,\n"
                    + "     p.TRIBUTACAO_PIS_COFINS,\n"
                    + "     pis.PIS_CST,\n"
                    + "     pis.ENTRADA_PIS_CST,\n"
                    + "     pis.PIS_DETALHEMENTO as NATUREZARECEITA,\n"
                    + "     p.PRECO_CUSTO,\n"
                    + "     p.ICMS_CST,\n"
                    + "     p.ICMS_ALIQUOTA,\n"
                    + "     p.VALIDADE,\n"
                    + "     p.ESTOQUE_MINIMO,\n"
                    + "     p.PESAVEL,\n"
                    + "     p.CEST,\n"
                    + "     pre.VALOR as PRECOVENDA,\n"
                    + "     pre.MARKUP as MARGEM,\n"
                    + "     est.QUANTIDADE as ESTOQUE\n"
                    + "from PRODUTOS p\n"
                    + "left join TRIBUTACAO_PIS_COFINS pis on pis.TIPO = p.TRIBUTACAO_PIS_COFINS\n"
                    + "left join PRODUTOS_PRECOS pre on pre.PRODUTO = p.PRODUTO\n"
                    + "left join ESTOQUE_PRODUTOS est on est.PRODUTO = p.PRODUTO\n"
                    + "     and est.EMPRESA = " + getLojaOrigem() + "\n"
                    + "order by p.PRODUTO"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("PRODUTO"));
                    imp.setEan(rst.getString("EAN13"));
                    imp.setTipoEmbalagem(rst.getString("UNIDADEMEDIDA"));
                    imp.setQtdEmbalagem(rst.getInt("EMBALAGEM"));
                    imp.seteBalanca(rst.getInt("PESAVEL") == 1);
                    imp.setValidade(rst.getInt("VALIDADE"));
                    imp.setDescricaoCompleta(rst.getString("DESCRICAO"));
                    imp.setDescricaoReduzida(rst.getString("REDUZIDO"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setDataCadastro(rst.getDate("CADASTRO"));
                    imp.setCodMercadologico1(rst.getString("GRUPOESTOQUE"));
                    imp.setCodMercadologico2("1");
                    imp.setCodMercadologico3("1");
                    imp.setPesoBruto(rst.getDouble("PESO_BRUTO"));
                    imp.setPesoLiquido(rst.getDouble("PESO_LIQUIDO"));
                    imp.setSituacaoCadastro(rst.getInt("INATIVO") == 0 ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    imp.setMargem(rst.getDouble("MARGEM"));
                    imp.setPrecovenda(rst.getDouble("PRECOVENDA"));
                    imp.setCustoComImposto(rst.getDouble("PRECO_CUSTO"));
                    imp.setEstoqueMinimo(rst.getDouble("ESTOQUE_MINIMO"));
                    imp.setEstoque(rst.getDouble("ESTOQUE"));
                    imp.setNcm(rst.getString("NCM"));
                    imp.setCest(rst.getString("CEST"));
                    imp.setPiscofinsCstDebito(rst.getString("PIS_CST"));
                    imp.setPiscofinsCstCredito(rst.getString("ENTRADA_PIS_CST"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("NATUREZARECEITA"));
                    imp.setIcmsCst(rst.getInt("ICMS_CST"));
                    imp.setIcmsAliq(rst.getDouble("ICMS_ALIQUOTA"));
                    imp.setIcmsReducao(0);
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "f.FORNECEDOR as ID,  \n"
                    + "p.INSCRICAO as CNPJ,\n"
                    + "p.NOME,\n"
                    + "p.LOGRADOURO,\n"
                    + "p.ENDERECO,\n"
                    + "p.NUM,\n"
                    + "p.BAIRRO,\n"
                    + "p.CODMUNICIPIO,\n"
                    + "p.CIDADE,\n"
                    + "p.UF,\n"
                    + "p.CEP,\n"
                    + "p.FONE,\n"
                    + "p.EMAIL,\n"
                    + "p.COMPLEMENTO,\n"
                    + "p.IE,\n"
                    + "p.IDENTIDADE_NUMERO,\n"
                    + "p.IDENTIDADE_ORGAO,\n"
                    + "p.IDENTIDADE_EMISSAO,\n"
                    + "p.IDENTIDADE_UF,\n"
                    + "p.CADASTRO,\n"
                    + "p.INSCRICAO_RURAL,\n"
                    + "p.INSCRICAO_MUNICIPAL,\n"
                    + "p.PAIS,\n"
                    + "p.CELULAR,\n"
                    + "p.RAZAO_SOCIAL,\n"
                    + "pt.TELEFONE,\n"
                    + "pt.CONTATO_NOME,\n"
                    + "f.INATIVO,\n"
                    + "f.DIA_FIXO_PAGAMENTO,\n"
                    + "f.PRODUTOR_RURAL\n"
                    + "from pessoas p\n"
                    + "inner JOIN FORNECEDORES f on f.INSCRICAO = p.INSCRICAO\n"
                    + "left join PESSOAS_TELEFONE pt on pt.INSCRICAO = p.INSCRICAO\n"
                    + "order by p.INSCRICAO"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("ID"));
                    imp.setRazao(rst.getString("RAZAO_SOCIAL"));
                    imp.setFantasia(rst.getString("NOME"));
                    imp.setCnpj_cpf(rst.getString("CNPJ"));
                    imp.setIe_rg(rst.getString("IE"));
                    imp.setInsc_municipal(rst.getString("INSCRICAO_MUNICIPAL"));
                    imp.setEndereco(rst.getString("LOGRADOURO") + " " + rst.getString("ENDERECO"));
                    imp.setNumero(rst.getString("NUM"));
                    imp.setBairro(rst.getString("BAIRRO"));
                    imp.setIbge_municipio(rst.getInt("CODMUNICIPIO"));
                    imp.setMunicipio(rst.getString("CIDADE"));
                    imp.setUf(rst.getString("CEP"));
                    imp.setComplemento(rst.getString("COMPLEMENTO"));
                    imp.setTel_principal(rst.getString("FONE"));
                    imp.setAtivo(rst.getInt("INATIVO") == 0 ? true : false);

                    if (rst.getInt("PRODUTOR_RURAL") == 1) {
                        imp.setProdutorRural();
                    }

                    if ((rst.getString("TELEFONE") != null)
                            && (!rst.getString("TELEFONE").trim().isEmpty())) {
                        imp.addContato(
                                "TELEFONE",
                                rst.getString("TELEFONE"),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("EMAIL") != null)
                            && (!rst.getString("EMAIL").trim().isEmpty())) {
                        imp.addContato(
                                "EMAIL",
                                null,
                                null,
                                TipoContato.NFE,
                                rst.getString("EMAIL").toLowerCase()
                        );
                    }
                    if ((rst.getString("CELULAR") != null)
                            && (!rst.getString("CELULAR").trim().isEmpty())) {
                        imp.addContato(
                                "CELULAR",
                                null,
                                rst.getString("CELULAR"),
                                TipoContato.COMERCIAL,
                                null
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

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "FORNECEDOR,\n"
                    + "PRODUTO,\n"
                    + "CODIGO as CODIGOEXTERNO\n"
                    + "from FORNECEDORES_PRODUTO"
            )) {
                ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                imp.setImportLoja(getLojaOrigem());
                imp.setImportSistema(getSistema());
                imp.setIdProduto(rst.getString("PRODUTO"));
                imp.setIdFornecedor(rst.getString("FORNECEDOR"));
                imp.setCodigoExterno(rst.getString("CODIGOEXTERNO"));
                result.add(imp);
            }
        }
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select  \n"
                    + "c.CLIENTE as ID,\n"
                    + "p.INSCRICAO as CNPJ,\n"
                    + "p.NOME,\n"
                    + "p.LOGRADOURO,\n"
                    + "p.ENDERECO,\n"
                    + "p.NUM,\n"
                    + "p.BAIRRO,\n"
                    + "p.CODMUNICIPIO,\n"
                    + "p.CIDADE,\n"
                    + "p.UF,\n"
                    + "p.CEP,\n"
                    + "p.FONE,\n"
                    + "p.EMAIL,\n"
                    + "p.COMPLEMENTO,\n"
                    + "p.IE,\n"
                    + "p.IDENTIDADE_NUMERO,\n"
                    + "p.IDENTIDADE_ORGAO,\n"
                    + "p.IDENTIDADE_EMISSAO,\n"
                    + "p.IDENTIDADE_UF,\n"
                    + "p.CADASTRO,\n"
                    + "p.INSCRICAO_RURAL,\n"
                    + "p.INSCRICAO_MUNICIPAL,\n"
                    + "p.ESTADO_CIVIL,\n"
                    + "p.NOME_MAE,\n"
                    + "p.NOME_PAI,\n"
                    + "p.PAIS,\n"
                    + "p.NASCIMENTO,\n"
                    + "p.CELULAR,\n"
                    + "p.RAZAO_SOCIAL,\n"
                    + "pt.TELEFONE,\n"
                    + "pt.CONTATO_NOME,\n"
                    + "c.INATIVO,\n"
                    + "c.LIMITE_CREDITO,\n"
                    + "c.OBSERVACOES,\n"
                    + "c.DIA_FIXO_PAGAMENTO\n"
                    + "from pessoas p\n"
                    + "inner JOIN CLIENTES c on c.INSCRICAO = p.INSCRICAO\n"
                    + "left join PESSOAS_TELEFONE pt on pt.INSCRICAO = p.INSCRICAO\n"
                    + "order by p.INSCRICAO;"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("ID"));
                    imp.setRazao(rst.getString("RAZAO_SOCIAL"));
                    imp.setFantasia(rst.getString("NOME"));
                    imp.setCnpj(rst.getString("CNPJ"));
                    imp.setInscricaoestadual(rst.getString("IE"));
                    imp.setInscricaoMunicipal(rst.getString("INSCRICAO_MUNICIPAL"));
                    imp.setEndereco(rst.getString("LOGRADOURO") + " " + rst.getString("ENDERECO"));
                    imp.setNumero(rst.getString("NUM"));
                    imp.setBairro(rst.getString("BAIRRO"));
                    imp.setMunicipioIBGE(rst.getInt("CODMUNICIPIO"));
                    imp.setMunicipio(rst.getString("UF"));
                    imp.setCep(rst.getString("CEP"));
                    imp.setComplemento(rst.getString("COMPLEMENTO"));
                    imp.setTelefone(rst.getString("FONE"));
                    imp.setEmail(rst.getString("EMAIL"));
                    imp.setCelular(rst.getString("CELULAR"));
                    imp.setDataCadastro(rst.getDate("CADASTRO"));
                    imp.setNomeMae(rst.getString("NOME_MAE"));
                    imp.setNomePai(rst.getString("NOME_PAI"));
                    imp.setDataNascimento(rst.getDate("NASCIMENTO"));
                    imp.setAtivo(rst.getInt("INATIVO") == 0 ? true : false);
                    imp.setObservacao(rst.getString("OBSERVACOES"));
                    imp.setValorLimite(rst.getDouble("LIMITE_CREDITO"));
                    imp.setDiaVencimento(rst.getInt("DIA_FIXO_PAGAMENTO"));

                    if ((rst.getString("TELEFONE") != null)
                            && (!rst.getString("TELEFONE").trim().isEmpty())) {
                        imp.addContato(
                                "TELEFONE",
                                rst.getString("TELEFONE"),
                                null,
                                null,
                                null
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

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select  \n"
                    + "REGISTRO,\n"
                    + "NUMERO,\n"
                    + "CLIENTE,\n"
                    + "EMISSAO,\n"
                    + "VENCIMENTO,\n"
                    + "VALOR,\n"
                    + "VALOR_LIQUIDO,\n"
                    + "JURO,\n"
                    + "DESCONTO,\n"
                    + "MULTA,\n"
                    + "OBSERVACOES\n"
                    + "from CONTAS_RECEBER \n"
                    + "where PAGAMENTO = '' \n"
                    + "and EMPRESA = " + getLojaOrigem()
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("REGISTRO"));
                    imp.setNumeroCupom(rst.getString("NUMERO"));
                    imp.setIdCliente(rst.getString("CLIENTE"));
                    imp.setDataEmissao(rst.getDate("EMISSAO"));
                    imp.setDataVencimento(rst.getDate("VENCIMENTO"));
                    imp.setValor(rst.getDouble("VALOR_LIQUIDO"));
                    imp.setJuros(rst.getDouble("JURO"));
                    imp.setObservacao(rst.getString("OBSERVACOES"));
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
