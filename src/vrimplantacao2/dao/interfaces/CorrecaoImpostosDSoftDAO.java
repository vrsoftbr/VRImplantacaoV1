package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import vrframework.classe.Conexao;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author lucasrafael
 */
public class CorrecaoImpostosDSoftDAO extends InterfaceDAO implements MapaTributoProvider {

    private String complemento = "";

    public void setComplemento(String complemento) {
        this.complemento = complemento == null ? "" : complemento.trim();
    }

    @Override
    public String getSistema() {
        return "DSoft" + ("".equals(complemento) ? "" : " - " + complemento);
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
                    "select distinct\n"
                    + "	coalesce(cst_icms_saida, '0') cst_icms_saida,\n"
                    + "	coalesce(aliquota_icms_saida, '0') aliquota_icms_saida,\n"
                    + "	coalesce(reducao_icms_saida, '0') reducao_icms_saida\n"
                    + "from implantacao.produtos_fiscais\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    String id = getAliquotaKeyDebito(
                            rst.getString("cst_icms_saida"),
                            Double.parseDouble(rst.getString("aliquota_icms_saida").replace(",", ".")),
                            Double.parseDouble(rst.getString("reducao_icms_saida").replace(",", "."))
                    );
                    result.add(
                            new MapaTributoIMP(
                                    id,
                                    id,
                                    rst.getInt("cst_icms_saida"),
                                    Double.parseDouble(rst.getString("aliquota_icms_saida").replace(",", ".")),
                                    Double.parseDouble(rst.getString("reducao_icms_saida").replace(",", "."))
                            )
                    );
                }
            }

            try (ResultSet rst = stm.executeQuery(
                    "select distinct\n"
                    + "	coalesce(cst_icms_entrada, '0') cst_icms_entrada,\n"
                    + "	coalesce(aliquota_icms_entrada, '0') aliquota_icms_entrada,\n"
                    + "	coalesce(reducao_icms_entrada, '0') reducao_icms_entrada\n"
                    + "from implantacao.produtos_fiscais\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    String id = getAliquotaKeyCredito(
                            rst.getString("cst_icms_entrada"),
                            Double.parseDouble(rst.getString("aliquota_icms_entrada").replace(",", ".")),
                            Double.parseDouble(rst.getString("reducao_icms_entrada").replace(",", "."))
                    );
                    result.add(
                            new MapaTributoIMP(
                                    id,
                                    id,
                                    rst.getInt("cst_icms_entrada"),
                                    Double.parseDouble(rst.getString("aliquota_icms_entrada").replace(",", ".")),
                                    Double.parseDouble(rst.getString("reducao_icms_entrada").replace(",", "."))
                            )
                    );
                }
            }
        }
        return result;
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    codigo,\n"
                    + "    nome,\n"
                    + "    cgc as cnpj\n"
                    + "from empresa\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(
                            rst.getString("codigo"), rst.getString("nome")));
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
                        + "codigo, \n"
                        + "ncm \n"
                        + "from implantacao.produtos_fiscais "
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("codigo"));
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
                        + "codigo, "
                        + "cest \n"
                        + "from implantacao.produtos_fiscais "
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("codigo"));
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
                        + "	codigo, \n"
                        + "	cst_pis_d,\n"
                        + "	cst_pis_c\n"
                        + "from implantacao.produtos_fiscais"
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("codigo"));
                        imp.setPiscofinsCstDebito(rst.getString("cst_pis_d"));
                        imp.setPiscofinsCstCredito(rst.getString("cst_pis_c"));                        
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
                        + "	codigo, \n"
                        + "	cst_pis_d,\n"
                        + "	cst_pis_c,\n"
                        + "     natureza_piscofins\n"        
                        + "from implantacao.produtos_fiscais"
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("codigo"));
                        imp.setPiscofinsCstDebito(rst.getString("cst_pis_d"));
                        imp.setPiscofinsCstCredito(rst.getString("cst_pis_c"));                        
                        imp.setPiscofinsNaturezaReceita(rst.getString("natureza_piscofins"));
                        result.add(imp);
                    }
                }
                return result;
            }
        }

        if (opt == OpcaoProduto.ICMS) {
            try (Statement stm = Conexao.createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select\n"
                        + "	codigo,\n"
                        + "	coalesce(cst_icms_saida, '0') cst_icms_saida,\n"
                        + "	coalesce(aliquota_icms_saida, '0') aliquota_icms_saida,\n"
                        + "	coalesce(reducao_icms_saida, '0') reducao_icms_saida,\n"
                        + "	coalesce(cst_icms_entrada, '0') cst_icms_entrada,\n"
                        + "	coalesce(aliquota_icms_entrada, '0') aliquota_icms_entrada,\n"
                        + "	coalesce(reducao_icms_entrada, '0') reducao_icms_entrada	\n"
                        + "from implantacao.produtos_fiscais	\n"
                        + "order by 1"
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("codigo"));

                        String idIcmsDebito = getAliquotaKeyDebito(
                                rst.getString("cst_icms_saida"),
                                Double.parseDouble(rst.getString("aliquota_icms_saida").replace(",", ".")),
                                Double.parseDouble(rst.getString("reducao_icms_saida").replace(",", "."))
                        );

                        String idIcmsCredito = getAliquotaKeyCredito(
                                rst.getString("cst_icms_entrada"),
                                Double.parseDouble(rst.getString("aliquota_icms_entrada").replace(",", ".")),
                                Double.parseDouble(rst.getString("reducao_icms_entrada").replace(",", "."))
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
