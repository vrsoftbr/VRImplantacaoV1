package vrimplantacao2_5.dao.sistema;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao2_5.dao.conexao.ConexaoFirebird;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

public class ASoft2_5DAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "ASoft";
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT CODIGO, DESCRICAO\n"
                    + "FROM SETORES\n"
                    + "ORDER BY CODIGO"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("CODIGO"));
                    imp.setMerc1Descricao(rst.getString("DESCRICAO"));
                    imp.setMerc2ID("1");
                    imp.setMerc2Descricao(imp.getMerc1Descricao());
                    imp.setMerc3ID("1");
                    imp.setMerc3Descricao(imp.getMerc1Descricao());
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }
    
    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    ""
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(
                            rs.getString("codigo"),
                            rs.getString("descricao"),
                            0,
                            rs.getDouble("cst"),
                            0));
                }
            }
        }

        return result;
    }


    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> vResult = new ArrayList<>();
        int cst;
        double aliquota, reducao;
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT P.CODBAR, P.DESCRICAO, P.SALDO, P.PRECOCUSTO, P.PRECOVENDA,\n"
                    + "P.QTDEMB, P.UNIDADE, P.LUCRO, P.PESUNI, P.SETOR, P.GRUPO, P.SUBGRU,\n"
                    + "P.ICMS, P.NCM, P.ALIQUOTA, P.REDUZ, P.DATCAD, P.PISCST, P.COFINSCST,\n"
                    + "P.CEST, P.CST\n"
                    + "FROM PRODUTOS P"
            )) {
                while (rst.next()) {
                    cst = 0;
                    aliquota = 0;
                    reducao = 0;
                    if ((rst.getString("ICMS") != null)
                            && (!rst.getString("ICMS").trim().isEmpty())) {
                        if (null != rst.getString("ICMS").trim()) {
                            switch (rst.getString("ICMS").trim()) {
                                case "FF":
                                    cst = 60;
                                    break;
                                case "II":
                                    cst = 40;
                                    break;
                                case "01":
                                    cst = 0;
                                    aliquota = 7;
                                    reducao = 0;
                                    break;
                            }
                        }
                    } else {
                        cst = 90;
                    }
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("CODBAR"));
                    imp.setEan(rst.getString("CODBAR"));
                    imp.setDescricaoCompleta(rst.getString("DESCRICAO"));
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setCodMercadologico1(rst.getString("SETOR"));
                    imp.setCodMercadologico2("1");
                    imp.setCodMercadologico3("1");
                    imp.setDataCadastro(rst.getDate("DATCAD"));
                    imp.seteBalanca(("P".equals(rst.getString("PESUNI"))));
                    imp.setTipoEmbalagem(("P".equals(rst.getString("PESUNI")) ? "KG" : "UN"));
                    imp.setQtdEmbalagem(rst.getInt("QTDEMB"));
                    imp.setMargem(rst.getDouble("LUCRO"));
                    imp.setPrecovenda(rst.getDouble("PRECOVENDA"));
                    imp.setCustoComImposto(rst.getDouble("PRECOCUSTO"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setEstoque(rst.getDouble("SALDO"));
                    imp.setNcm(rst.getString("NCM"));
                    imp.setCest(rst.getString("CEST"));
                    imp.setIcmsCst(cst);
                    imp.setIcmsAliq(aliquota);
                    imp.setIcmsReducao(reducao);
                    imp.setPiscofinsCstDebito(Integer.parseInt(Utils.formataNumero(rst.getString("PISCST"))));
                    imp.setPiscofinsCstCredito(Integer.parseInt(Utils.formataNumero(rst.getString("COFINSCST"))));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> vResult = new ArrayList<>();
        return null;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> vResut = new ArrayList<>();
        return null;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT C.CODIGO, C.CODFORMAPAG, C.NOME, C.ENDERECO, C.BAIRRO, C.ESTADO,\n"
                    + "C.DTNASCIMENTO, C.UF, C.CGC, C.CPF, C.IE, C.PONTOS, C.SALDO, C.LIMITE,\n"
                    + "C.TELEFONE, C.CELULAR, C.EMAIL, C.OBSERVACOES, C.BLOQUE, C.PAI, C.MAE,\n"
                    + "C.CIDADE, C.CEP, C.DATCAD, C.LOCTRAB, C.CARGO, C.ADMISSAO, F.DESCRICAO\n"
                    + "FROM CLIENTES C\n"
                    + "LEFT JOIN FORMPAG F ON F.CODIGO = C.CODFORMAPAG\n"
                    + "ORDER BY C.CODIGO"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("CODIGO"));
                    imp.setRazao(rst.getString("NOME"));
                    imp.setFantasia(rst.getString("NOME"));
                    imp.setInscricaoestadual(rst.getString("IE"));

                    if ((rst.getString("CGC") != null)
                            && (!rst.getString("CGC").trim().isEmpty())) {
                        imp.setCnpj(rst.getString("CGC"));
                    } else {
                        imp.setCnpj(rst.getString("CPF"));
                    }

                    imp.setEndereco(rst.getString("ENDERECO"));
                    imp.setBairro(rst.getString("BAIRRO"));
                    imp.setMunicipio(rst.getString("CIDADE"));
                    imp.setUf(rst.getString("UF"));
                    imp.setCep(rst.getString("CEP"));
                    imp.setDataCadastro(rst.getDate("DATCAD"));
                    imp.setDataNascimento((rst.getDate("DTNASCIMENTO") == null ? null : rst.getDate("DTNASCIMENTO")));
                    imp.setValorLimite(rst.getDouble("LIMITE"));
                    imp.setTelefone(rst.getString("TELEFONE"));
                    imp.setCelular(rst.getString("CELULAR"));
                    imp.setEmail(rst.getString("EMAIL"));
                    imp.setObservacao(rst.getString("OBSERVACOES"));
                    imp.setBloqueado(("S".equals(rst.getString("BLOQUE")) ? true : false));
                    imp.setNomePai(rst.getString("PAI"));
                    imp.setNomeMae(rst.getString("MAE"));
                    imp.setEmpresa(rst.getString("LOCTRAB"));
                    imp.setCargo(rst.getString("CARGO"));

                    if ((imp.getValorLimite() > 0)
                            || ("N".equals(rst.getString("BLOQUE")))) {
                        imp.setPermiteCreditoRotativo(true);
                        imp.setPermiteCheque(true);
                    } else {
                        imp.setPermiteCreditoRotativo(false);
                        imp.setPermiteCheque(false);
                    }

                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT NUMERO, PARCELA, CODCLIENTE,\n"
                    + "DATAPED, DATVEN, DESCONTO, TOTAL\n"
                    + "FROM PEDIDOS\n"
                    + "WHERE DATPAG IS NULL "
                    + "AND NCHEQUE IS NULL"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("NUMERO"));
                    imp.setNumeroCupom(rst.getString("NUMERO"));
                    imp.setParcela(rst.getInt("PARCELA"));
                    imp.setIdCliente(rst.getString("CODCLIENTE"));
                    imp.setDataEmissao(rst.getDate("DATAPED"));
                    imp.setDataVencimento(rst.getDate("DATVEN"));
                    imp.setValor(rst.getDouble("TOTAL"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ChequeIMP> getCheques() throws Exception {
        List<ChequeIMP> vResult = new ArrayList<>();
        String observacao;
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT NUMERO, PARCELA, CODCLIENTE,\n"
                    + "DATAPED, DATVEN, DESCONTO, TOTAL, NCHEQUE,\n"
                    + "BANCO, NOMECHQ, TELEFONE, RGCHQ, CPFCHQ\n"
                    + "FROM PEDIDOS\n"
                    + "WHERE DATPAG IS NULL\n"
                    + "AND NCHEQUE IS NOT NULL"
            )) {
                while (rst.next()) {
                    observacao = (rst.getString("BANCO") == null ? "" : "BANCO " + rst.getString("BANCO"));
                    ChequeIMP imp = new ChequeIMP();
                    imp.setId(rst.getString("NUMERO"));
                    imp.setNumeroCupom(rst.getString("NUMERO"));
                    imp.setDate(rst.getDate("DATAPED"));
                    imp.setValor(rst.getDouble("TOTAL"));
                    imp.setObservacao(observacao);
                    imp.setNumeroCheque(rst.getString("NCHEQUE"));
                    imp.setNome(rst.getString("NOMECHQ"));
                    imp.setTelefone(rst.getString("TELEFONE"));
                    imp.setRg(rst.getString("RGCHQ"));
                    imp.setCpf(rst.getString("CPFCHQ"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }
}
