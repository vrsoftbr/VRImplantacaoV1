package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import vrframework.classe.Conexao;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author lucasrafael
 */
public class CorrecaoImpostosSuperControle_SuperServerDAO extends InterfaceDAO implements MapaTributoProvider {

    private String complemento = "";

    public void setComplemento(String complemento) {
        this.complemento = complemento == null ? "" : complemento.trim();
    }

    @Override
    public String getSistema() {
        return "SuperServer" + ("".equals(complemento) ? "" : " - " + complemento);
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.MAPA_TRIBUTACAO,
                OpcaoProduto.NCM,
                OpcaoProduto.CEST,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.NATUREZA_RECEITA,
                OpcaoProduto.ICMS,
                OpcaoProduto.ICMS_ENTRADA,
                OpcaoProduto.ICMS_ENTRADA_FORA_ESTADO,
                OpcaoProduto.ICMS_SAIDA,
                OpcaoProduto.ICMS_SAIDA_FORA_ESTADO,
                OpcaoProduto.ICMS_SAIDA_NF,
                OpcaoProduto.ICMS_CONSUMIDOR
        ));
    }

    private String getAliquotaKeyDebito(String cst, double aliq, double red) throws Exception {
        return String.format(
                "%s-%.2f-%.2f",
                cst,
                aliq,
                red
        );
    }

    private String getAliquotaKeyCredito(String cst, double aliq, double red) throws Exception {
        return String.format(
                "%s-%.2f-%.2f",
                cst,
                aliq,
                red
        );
    }
    
    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select distinct \n"
                    + "tributacao as descricao, \n"
                    + "	cst::int as cst_icms,\n"
                    + "	icmssaida aliq_icms,\n"
                    + "	reducaosaida red_icms \n"
                    + "from implantacao.produtoscorrecaofiscal\n"
                    + "order by cst::int"
            )) {
                while (rst.next()) {
                    String id = getAliquotaKeyDebito(
                            rst.getString("cst_icms"),
                            Double.parseDouble(rst.getString("aliq_icms").replace(",", ".")),
                            Double.parseDouble(rst.getString("red_icms").replace(",", "."))
                    );
                    result.add(
                            new MapaTributoIMP(
                                    id,
                                    rst.getString("descricao"),
                                    rst.getInt("cst_icms"),
                                    Double.parseDouble(rst.getString("aliq_icms").replace(",", ".")),
                                    Double.parseDouble(rst.getString("red_icms").replace(",", "."))
                            )
                    );
                }
            }

            try (ResultSet rst = stm.executeQuery(
                    "select distinct \n"
                    + "tributacao as descricao, \n"
                    + "	cst::int as cst_icms,\n"
                    + "	icmsentrada aliq_icms,\n"
                    + "	reducaoentrada red_icms\n"
                    + "from implantacao.produtoscorrecaofiscal\n"
                    + "order by cst::int"
            )) {
                while (rst.next()) {
                    String id = getAliquotaKeyCredito(
                            rst.getString("cst_icms"),
                            Double.parseDouble(rst.getString("aliq_icms").replace(",", ".")),
                            Double.parseDouble(rst.getString("red_icms").replace(",", "."))
                    );
                    result.add(
                            new MapaTributoIMP(
                                    id,
                                    rst.getString("descricao"),
                                    rst.getInt("cst_icms"),
                                    Double.parseDouble(rst.getString("aliq_icms").replace(",", ".")),
                                    Double.parseDouble(rst.getString("red_icms").replace(",", "."))
                            )
                    );
                }
            }
        }
        return result;
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "id, \n"
                    + "(descricaoLoja + ' - ' + cnpjLoja) as descricao\n"
                    + "from MultiLoja.Loja\n"
                    + "where fkCliente = 1\n"
                    + "order by id"
            )) {
                while (rst.next()) {
                    result.add(
                            new Estabelecimento(rst.getString("id"), rst.getString("descricao"))
                    );
                }
            }
        }
        return result;
    }

    /* O TRECHO ABAIXO FOI DESENVOLVIDO PARA FAZER O ACERTO FISCAL DOS PRODUTOS, FOI FORNECIDO UMA PLANILHA POR PARTE DO CLIENTE 
        ESSA PLANILHA SALVEI NO BANCO DE DADOS 
    
    ASS.: LUCAS SANTOS
    
     */
    @Override
    public List<ProdutoIMP> getProdutos(OpcaoProduto opt) throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        if (opt == OpcaoProduto.NCM) {
            try (Statement stm = Conexao.createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select \n"
                        + "barras, \n"
                        + "ncm \n"
                        + "from implantacao.produtoscorrecaofiscal "
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("barras"));
                        imp.setNcm(rst.getString("ncm"));
                        result.add(imp);
                    }
                }
                return result;
            }
        }

        if (opt == OpcaoProduto.CEST) {
            try (Statement stm = Conexao.createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select \n"
                        + "barras, "
                        + "cest \n"
                        + "from implantacao.produtoscorrecaofiscal "
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("barras"));
                        imp.setCest(rst.getString("cest"));
                        result.add(imp);
                    }
                }
                return result;
            }
        }

        if (opt == OpcaoProduto.PIS_COFINS) {
            try (Statement stm = Conexao.createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select \n"
                        + "barras, "
                        + "cstpiscofinsentrada, \n"
                        + "cstpiscofinssaida \n"
                        + "from implantacao.produtoscorrecaofiscal "
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("barras"));
                        imp.setPiscofinsCstCredito(rst.getString("cstpiscofinsentrada"));
                        imp.setPiscofinsCstDebito(rst.getInt("cstpiscofinssaida"));
                        result.add(imp);
                    }
                }
                return result;
            }
        }

        if (opt == OpcaoProduto.NATUREZA_RECEITA) {
            try (Statement stm = Conexao.createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select \n"
                        + "barras, "
                        + "cstpiscofinsentrada, \n"
                        + "cstpiscofinssaida, \n"
                        + "naturezadereceita\n"
                        + "from implantacao.produtoscorrecaofiscal "
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("barras"));
                        imp.setPiscofinsCstCredito(rst.getString("cstpiscofinsentrada"));
                        imp.setPiscofinsCstDebito(rst.getInt("cstpiscofinssaida"));
                        imp.setPiscofinsNaturezaReceita(rst.getString("naturezadereceita"));
                        result.add(imp);
                    }
                }
                return result;
            }
        }

        if (opt == OpcaoProduto.ICMS) {
            try (Statement stm = Conexao.createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select \n"
                        + "barras, "
                        + "cst::int as icms_cst, \n"
                        + "icmssaida as icms_saida, \n"
                        + "reducaosaida as reducao_saida, \n"
                        + "icmsentrada as icms_entrada, \n"
                        + "reducaoentrada as reducao_entrada \n"
                        + "from implantacao.produtoscorrecaofiscal "
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("barras"));
                        
                        String idIcmsDebito = getAliquotaKeyDebito(
                                rst.getString("icms_cst"),
                                Double.parseDouble(rst.getString("icms_saida").replace(",", ".")) ,
                                Double.parseDouble(rst.getString("reducao_saida").replace(",", ".")) 
                        );

                        String idIcmsCredito = getAliquotaKeyCredito(
                                rst.getString("icms_cst"),
                                Double.parseDouble(rst.getString("icms_entrada").replace(",", ".")) ,
                                Double.parseDouble(rst.getString("reducao_entrada").replace(",", ".")) 
                        );
                        
                        imp.setIcmsDebitoId(idIcmsDebito);
                        imp.setIcmsDebitoForaEstadoId(idIcmsDebito);
                        imp.setIcmsDebitoForaEstadoNfId(idIcmsDebito);
                        
                        imp.setIcmsCreditoId(idIcmsCredito);
                        imp.setIcmsCreditoForaEstadoId(idIcmsCredito);
                        
                        imp.setIcmsConsumidorId(idIcmsDebito);

                        result.add(imp);
                    }
                }
                return result;
            }
        }
        return null;
    }

}
