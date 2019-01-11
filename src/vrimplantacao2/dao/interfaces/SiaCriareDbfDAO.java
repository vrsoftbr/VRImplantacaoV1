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
import vrframework.classe.ProgressBar;
import vrimplantacao.classe.ConexaoDBF;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author lucasrafael
 */
public class SiaCriareDbfDAO extends InterfaceDAO implements MapaTributoProvider {

    public String i_arquivo;

    @Override
    public String getSistema() {
        return "SiaCriareDbf";
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList();
        ConexaoDBF.abrirConexao(i_arquivo);

        try (Statement stmt = ConexaoDBF.getConexao().createStatement()) {
            try (ResultSet rs = stmt.executeQuery(
                    "select "
                    + "CODALIQ, CST, ALIQUOTA "
                    + "from aliquotas "
                    + "order by CODALIQ"
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(
                            rs.getString("CODALIQ"),
                            "CST. " + rs.getString("CST") + " ALIQ. " + rs.getString("ALIQUOTA")));
                }
            }
        }
        return result;
    }

    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        ConexaoDBF.abrirConexao(i_arquivo);

        try (Statement stm = ConexaoDBF.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "CODEMP, DESCRICAO, CNPJ "
                    + "from empresa "
                    + "order by CODEMP"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(
                            rst.getString("CODEMP"),
                            rst.getString("DESCRICAO") + " " + rst.getString("CNPJ")));
                }
            }
        }

        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();
        ConexaoDBF.abrirConexao(i_arquivo);

        try (Statement stm = ConexaoDBF.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "CODFAM, DESCRICAO "
                    + "from familias "
                    + "order by CODFAM"
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("CODFAM"));
                    imp.setDescricao(rst.getString("DESCRICAO"));
                    result.add(imp);
                }
            }
            return result;
        }
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        ConexaoDBF.abrirConexao(i_arquivo);

        try (Statement stm = ConexaoDBF.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "m1.CODGRUPO as merc1, m1.DESCRICAO as desc_merc1, "
                    + "m2.CODCAT as merc2, m2.DESCRICAO as desc_merc2, "
                    + "m3.CODFAM as merc3, m3.DESCRICAO as desc_merc3 "
                    + "from produtos p "
                    + "left join grupos m1 on m1.CODGRUPO = p.GRUPO "
                    + "left join categorias m2 on m2.CODCAT = p.CATEGORIA "
                    + "left join familias m3 on m3.CODFAM = p.FAMILIA "
                    + "order by m1.CODGRUPO, m2.CODCAT, m3.CODFAM"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("merc1"));
                    imp.setMerc1Descricao(rst.getString("desc_merc1"));
                    imp.setMerc2ID(rst.getString("merc2"));
                    imp.setMerc2Descricao(rst.getString("desc_merc2"));
                    imp.setMerc3ID(rst.getString("merc3"));
                    imp.setMerc3Descricao(rst.getString("desc_merc3"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        ConexaoDBF.abrirConexao(i_arquivo);

        try (Statement stm = ConexaoDBF.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "p.CODITEM, "
                    + "p.GRUPO, "
                    + "p.DESCRICAO, "
                    + "p.ABREVIA, "
                    + "p.CUSTO, "
                    + "p.UNITARIO, "
                    + "p.BALANCA, "
                    + "p.ALIQUOTASA as ICMS, "
                    + "p.UNIDADE, "
                    + "p.FAMILIA, "
                    + "p.CODBARRA, "
                    + "p.NCM, "
                    + "p.CATEGORIA, "
                    + "p.ATIVO, "
                    + "p.PESO_LIQUI, "
                    + "p.PESO_BRUTO, "
                    + "(p.QTDEMBALAG / 1000) as QTDEMB, "
                    + "p.PIS, "
                    + "p.COFINS, "
                    + "p.MARKDOWN, "
                    + "p.CEST "
                    + "from produtos p"
            )) {
                int cont = 0;
                while (rst.next()) {
                    if (rst.getString("CODITEM") != null) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("CODITEM"));
                        imp.setEan(rst.getString("CODBARRA"));
                        imp.setDescricaoCompleta(rst.getString("DESCRICAO"));
                        imp.setDescricaoReduzida(rst.getString("ABREVIA"));
                        imp.setDescricaoGondola(imp.getDescricaoCompleta());
                        imp.setTipoEmbalagem(rst.getString("UNIDADE"));
                        imp.setQtdEmbalagem(rst.getInt("QTDEMB"));
                        imp.seteBalanca("S".equals(rst.getString("BALANCA")));
                        imp.setIdFamiliaProduto(rst.getString("FAMILIA"));
                        imp.setMargem(rst.getDouble("MARKDOWN"));
                        imp.setPrecovenda(rst.getDouble("UNITARIO") / 1000);
                        imp.setCustoComImposto(rst.getDouble("CUSTO") / 1000);
                        imp.setCustoSemImposto(imp.getCustoComImposto());
                        imp.setNcm(rst.getString("NCM"));
                        imp.setCest(rst.getString("CEST"));
                        imp.setPiscofinsCstDebito(rst.getString("PIS"));
                        imp.setPiscofinsCstCredito(rst.getString("COFINS"));
                        imp.setIcmsDebitoId(rst.getString("ICMS"));
                        imp.setIcmsCreditoId(rst.getString("ICMS"));
                        result.add(imp);

                        cont++;

                        ProgressBar.setStatus(String.valueOf(cont));
                    }
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos(OpcaoProduto opt) throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        ConexaoDBF.abrirConexao(i_arquivo);

        if (opt == OpcaoProduto.PRECO) {
            try (Statement stm = ConexaoDBF.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select "
                        + "ID_PRODUTO, "
                        + "UNITARIO "
                        + "from estoque "
                        + "where ID_EMPRESA = " + getLojaOrigem()
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("ID_PRODUTO"));
                        imp.setPrecovenda(rst.getDouble("UNITARIO") / 1000);
                        result.add(imp);
                    }
                }
            }
            return result;
        }

        if (opt == OpcaoProduto.CUSTO) {
            try (Statement stm = ConexaoDBF.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select "
                        + "ID_PRODUTO, "
                        + "CUSTO "
                        + "from estoque "
                        + "where ID_EMPRESA = " + getLojaOrigem()
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("ID_PRODUTO"));
                        imp.setCustoComImposto(rst.getDouble("CUSTO") / 1000);
                        imp.setCustoSemImposto(imp.getCustoComImposto());
                        result.add(imp);
                    }
                }
            }
            return result;
        }

        if (opt == OpcaoProduto.ESTOQUE) {
            try (Statement stm = ConexaoDBF.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select "
                        + "ID_PRODUTO, "
                        + "QTD "
                        + "from estoque "
                        + "where ID_EMPRESA = " + getLojaOrigem()
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("ID_PRODUTO"));
                        imp.setEstoque(rst.getDouble("QTD") / 1000);
                        result.add(imp);
                    }
                }
            }
            return result;
        }

        return null;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        ConexaoDBF.abrirConexao(i_arquivo);

        try (Statement stm = ConexaoDBF.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "CODEAN, "
                    + "CODPROD "
                    + "from codigos_ean"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("CODPROD"));
                    imp.setEan(rst.getString("CODEAN"));
                    result.add(imp);
                }
            }
        }
        return result;
    }
    
    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        ConexaoDBF.abrirConexao(i_arquivo);
        
        try (Statement stm = ConexaoDBF.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "CODIGOCLI, "
                    + "NOMECLI, "
                    + "BAIRROCLI, "
                    + "CIDADECLI, "
                    + "ESTADOCLI, "
                    + "CEPCLI, "
                    + "FONECLI, "
                    + "FAXCLI, "
                    + "CPFCGC, "
                    + "IDENINSC, "
                    + "EMAIL, "
                    + "ENDERCLI, "
                    + "BANCO, "
                    + "CONTA, "
                    + "AGENCIA, "
                    + "ATIVO, "
                    + "RAZAO, "
                    + "ID_CIDADE, "
                    + "NUMERO "
                    + "from clientes "
                    + "where TIPO = 'F'"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("CODIGOCLI"));
                    imp.setRazao(rst.getString("RAZAO"));
                    imp.setFantasia(rst.getString("NOMECLI"));
                }
            }
        }
        return null;
    }
}
