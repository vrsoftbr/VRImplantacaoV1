package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import vrimplantacao.classe.ConexaoOracle;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

public class LinnerDAO extends InterfaceDAO {

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + "CODIGO \n"
                    + "FROM CADEMPRESA"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("CODIGO"), "LOJA " + rst.getString("CODIGO")));
                }
            }
        }
        return result;
    }

    @Override
    public String getSistema() {
        return "Linner";
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT "
                    + "CODIGO, "
                    + "CLASSE, "
                    + "GRUPO, "
                    + "CLASSIFICACAO NCM, "
                    + "NOME, "
                    + "REFERENCIA, "
                    + "NOMEREDUZIDO, "
                    + "NOMECOMPLEMENTAR, "
                    + "CAPACIDADE, "
                    + "UNIDADE, "
                    + "EAN BARRA, "
                    + "EAN, "
                    + "EAN13, "
                    + "DUM14, "
                    + "PESOLIQUIDO, "
                    + "PESOBRUTO, "
                    + "MINIMO, "
                    + "MAXIMO, "
                    + "REPOSICAO, "
                    + "CUSTOPRAZO, "
                    + "CUSTOVISTA, "
                    + "MARGEM, "
                    + "TABELA, "
                    + "SITUACAO CST, "
                    + "CADASTRO, "
                    + "VINCULO, "
                    + "CUSTOFINAL, "
                    + "DEPARTAMENTO, "
                    + "ATIVO, "
                    + "CUSTOTRANSFERENCIA, "
                    + "CUSTODEVOLUCAO, "
                    + "SETOR, "
                    + "ESTOQUE, "
                    + "SITUACAOPISCOFINSSAI, "
                    + "SITUACAOPISCOFINSENT, "
                    + "NATREC, "
                    + "SITUACAOSAI, "
                    + "SITUACAOSAIFE, "
                    + "CEST "
                    + "FROM CADPRODUTO "
                    + "UNION ALL "
                    + "SELECT "
                    + "CODIGO, "
                    + "CLASSE, "
                    + "GRUPO, "
                    + "CLASSIFICACAO NCM, "
                    + "NOME, "
                    + "REFERENCIA, "
                    + "NOMEREDUZIDO, "
                    + "NOMECOMPLEMENTAR, "
                    + "CAPACIDADE, "
                    + "UNIDADE, "
                    + "EAN, "
                    + "EAN13 BARRA, "
                    + "DUM14, "
                    + "EAN13, "                            
                    + "PESOLIQUIDO, "
                    + "PESOBRUTO, "
                    + "MINIMO, "
                    + "MAXIMO, "
                    + "REPOSICAO, "
                    + "CUSTOPRAZO, "
                    + "CUSTOVISTA, "
                    + "MARGEM, "
                    + "TABELA, "
                    + "SITUACAO CST, "
                    + "CADASTRO, "
                    + "VINCULO, "
                    + "CUSTOFINAL, "
                    + "DEPARTAMENTO, "
                    + "ATIVO, "
                    + "CUSTOTRANSFERENCIA, "
                    + "CUSTODEVOLUCAO, "
                    + "SETOR, "
                    + "ESTOQUE, "
                    + "SITUACAOPISCOFINSSAI, "
                    + "SITUACAOPISCOFINSENT, "
                    + "NATREC, "
                    + "SITUACAOSAI, "
                    + "SITUACAOSAIFE, "
                    + "CEST "
                    + "FROM CADPRODUTO "
                    + "ORDER BY CODIGO"
            )) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
                while (rst.next()) {
                    ProdutoBalancaVO produtoBalanca;
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("CODIGO").replace("-", ""));
                    imp.setDataCadastro(rst.getDate("CADASTRO"));
                    imp.setDescricaoCompleta(rst.getString("NOME"));
                    imp.setDescricaoReduzida(rst.getString("NOMEREDUZIDO"));
                    imp.setDescricaoGondola(rst.getString("NOMECOMPLEMENTAR"));
                    imp.setTipoEmbalagem(rst.getString("UNIDADE"));
                    imp.setPesoBruto(rst.getDouble("PESOBRUTO"));
                    imp.setPesoLiquido(rst.getDouble("PESOLIQUIDO"));
                    imp.setMargem(rst.getDouble("MARGEM"));
                    imp.setPrecovenda(rst.getDouble("TABELA"));
                    imp.setCustoComImposto(rst.getDouble("REPOSICAO"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    /*imp.setEstoqueMinimo(rst.getDouble("MINIMO"));
                    imp.setEstoqueMaximo(rst.getDouble("MAXIMO"));
                    imp.setEstoque(rst.getDouble("ESTOQUE"));*/
                    imp.setPiscofinsCstDebito(rst.getInt("SITUACAOPISCOFINSSAI"));
                    imp.setPiscofinsCstCredito(rst.getInt("SITUACAOPISCOFINSENT"));
                    imp.setPiscofinsNaturezaReceita(rst.getInt("NATREC"));
                    imp.setNcm(rst.getString("NCM"));
                    imp.setCest(rst.getString("CEST"));
                    imp.setIcmsCst(rst.getInt("CST"));
                    if ((rst.getString("ATIVO") != null)
                            && (!rst.getString("ATIVO").trim().isEmpty())) {
                        if ("X".equals(rst.getString("ATIVO").trim())) {
                            imp.setSituacaoCadastro(SituacaoCadastro.ATIVO);
                        } else {
                            imp.setSituacaoCadastro(SituacaoCadastro.EXCLUIDO);
                        }
                    } else {
                        imp.setSituacaoCadastro(SituacaoCadastro.EXCLUIDO);
                    }

                    long codigoProduto;
                    if (rst.getString("EAN").contains("00000002")) {
                        codigoProduto = Long.parseLong(rst.getString("EAN").substring(8, rst.getString("EAN").length() - 1));
                        if (codigoProduto <= Integer.MAX_VALUE) {
                            produtoBalanca = produtosBalanca.get((int) codigoProduto);
                        } else {
                            produtoBalanca = null;
                        }

                        if (produtoBalanca != null) {
                            imp.setEan(String.valueOf(codigoProduto));
                            imp.seteBalanca(true);
                            imp.setValidade(produtoBalanca.getValidade() > 1 ? produtoBalanca.getValidade() : 0);
                        } else {
                            imp.setValidade(0);
                            imp.seteBalanca(false);
                        }
                    } else if (rst.getString("EAN13").contains("00000002")) {
                        codigoProduto = Long.parseLong(rst.getString("EAN13").substring(8, rst.getString("EAN13").length() - 1));
                        if (codigoProduto <= Integer.MAX_VALUE) {
                            produtoBalanca = produtosBalanca.get((int) codigoProduto);
                        } else {
                            produtoBalanca = null;
                        }

                        if (produtoBalanca != null) {
                            imp.setEan(String.valueOf(codigoProduto));
                            imp.seteBalanca(true);
                            imp.setValidade(produtoBalanca.getValidade() > 1 ? produtoBalanca.getValidade() : 0);
                        } else {
                            imp.setValidade(0);
                            imp.seteBalanca(false);
                        }
                    } else {
                        imp.setEan(rst.getString("BARRA"));
                        imp.setValidade(0);
                        imp.seteBalanca(false);
                    }
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT "
                    + "CODIGO, "
                    + "TIPO, "
                    + "NOME, "
                    + "RAZAO, "
                    + "CEP, "
                    + "ENDERECO, "
                    + "NUMERO, "
                    + "BAIRRO, "
                    + "CADASTRO, "
                    + "FISJUR, "
                    + "IERG, "
                    + "CNPJCPF, "
                    + "FONE1, "
                    + "FONE2, "
                    + "CELULAR, "
                    + "FAX, "
                    + "EMAIL, "
                    + "REPRESENTANTE, "
                    + "REPCEP, "
                    + "REPENDERECO, "
                    + "REPNUMERO, "
                    + "REPBAIRRO, "
                    + "REPFONE1, "
                    + "REPFONE2, "
                    + "REPCELULAR, "
                    + "REPFAX, "
                    + "REPEMAIL, "
                    + "OBSERVACAO, "
                    + "CATPAGAMENTO, "
                    + "CONTA, "
                    + "OPBANCO, "
                    + "OPAGENCIA, "
                    + "OPCONTA, "
                    + "SPBBANCO, "
                    + "SPBAGENCIA, "
                    + "SPBCONTA, "
                    + "SPBCNPJCPF, "
                    + "SPBNOME, "
                    + "SENHA, "
                    + "OPCNPJCPF, "
                    + "TIPOPAGAMENTO, "
                    + "ATIVO, "
                    + "VINCULO, "
                    + "CROSSDOCK, "
                    + "TIPOSIMPLES, "
                    + "CODMUNICIPIO, "
                    + "CODPAIS, "
                    + "PEDIDOMINIMO, "
                    + "PRAZOENTREGA "
                    + "FROM CADFORNECEDOR"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("CODIGO"));
                    imp.setRazao(rst.getString("RAZAO"));
                    imp.setFantasia(rst.getString("NOME"));
                    imp.setDatacadastro(rst.getDate("CADASTRO"));
                    imp.setCep(rst.getString("CEP"));
                    imp.setEndereco(rst.getString("ENDERECO"));
                    imp.setNumero(rst.getString("NUMERO"));
                    imp.setBairro(rst.getString("BAIRRO"));
                    imp.setIe_rg(rst.getString("IERG"));
                    imp.setCnpj_cpf(Utils.formataNumero(rst.getString("CNPJCPF")));
                    imp.setTel_principal(rst.getString("FONE1"));
                    imp.setObservacao(rst.getString("OBSERVACAO"));
                    imp.setAtivo("X".equals(rst.getString("ATIVO")) ? true : false);

                    if ((rst.getString("FONE2") != null)
                            && (!rst.getString("FONE2").trim().isEmpty())) {
                        imp.addContato(
                                "1",
                                "TELEFONE 2",
                                rst.getString("FONE2"),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("CELULAR") != null)
                            && (!rst.getString("CELULAR").trim().isEmpty())) {
                        imp.addContato(
                                "2",
                                "CELULAR",
                                null,
                                rst.getString("CELULAR"),
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("FAX") != null)
                            && (!rst.getString("FAX").trim().isEmpty())) {
                        imp.addContato(
                                "3",
                                "FAX",
                                rst.getString("FAX"),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("EMAIL") != null)
                            && (!rst.getString("EMAIL").trim().isEmpty())) {
                        imp.addContato(
                                "4",
                                "EMAIL",
                                null,
                                null,
                                TipoContato.NFE,
                                (rst.getString("EMAIL") != null ? rst.getString("EMAIL").toLowerCase() : null)
                        );
                    }
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }
}
