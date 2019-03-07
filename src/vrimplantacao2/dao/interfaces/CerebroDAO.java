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
import vrframework.classe.Conexao;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
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
public class CerebroDAO extends InterfaceDAO {

    public String complSistema = "";
    public String tipoDocumento = "";

    @Override
    public String getSistema() {
        if ((complSistema != null) && (!complSistema.trim().isEmpty())) {
            return "Cerebro" + complSistema;
        } else {
            return "Cerebro";
        }
    }

    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select codigo_empresa, descricao, cpf_cnpj from empresas\n"
                    + "order by codigo_empresa"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("codigo_empresa"), rst.getString("descricao")));
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "m1.codigo_grupo as merc1,\n"
                    + "m1.descricao as desc_merc1,\n"
                    + "coalesce(m2.codigo_subgrupo, '1') as merc2,\n"
                    + "coalesce(m2.descricao, m1.descricao) as desc_merc2,\n"
                    + "'1' as merc3,\n"
                    + "coalesce(m2.descricao, m1.descricao) as desc_merc3\n"
                    + "from grupos_produto m1\n"
                    + "left join subgrupos_produto m2\n"
                    + "    on m2.codigo_grupo = m1.codigo_grupo\n"
                    + "order by m1.codigo_grupo,  m2.codigo_subgrupo"
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

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "p.codigo_produto, \n"
                    + "p.pesavel,\n"
                    + "p.codigo_barra,\n"
                    + "p.codigo_grupo,\n"
                    + "p.codigo_subgrupo, \n"
                    + "p.descricao, \n"
                    + "p.preco_venda,\n"
                    + "p.custo_final,\n"
                    + "p.multiplica_entrada as qtdembalagem_pro,\n"
                    + "upper(p.unidade_entrada) as tipoembalagem_pro,\n"
                    + "p.unidade_saida,\n"
                    + "p.multiplica_saida as qtdembalagem_ean,"
                    + "p.validade,\n"
                    + "p.peso,\n"
                    + "p.codigo_tributo,\n"
                    + "p.status,\n"
                    + "p.cst,\n"
                    + "t.codigo_tributo as cod_trib,\n"
                    + "t.descricao as icms_desc,\n"
                    + "t.cst_icms as cst_icms_saida,\n"
                    + "t.icms_saida as icms_saida,\n"
                    + "t.reducao_saida as red_saida,\n"
                    + "t.cst_icms_ent as cst_icms_ent,\n"
                    + "t.icms_entrada as icms_ent,\n"
                    + "t.reducao_entrada as red_ent,\n"
                    + "p.cst_pis,\n"
                    + "p.cst_cofins,\n"
                    + "p.cst_pis_ent,\n"
                    + "p.cst_cofins_ent,\n"
                    + "p.ncm, \n"
                    + "p.cest, \n"
                    + "t.tipo, \n"
                    + "t.icms_vip as aliq_consumidor \n"
                    + "from produtos p\n"
                    + "left join tributos t on t.codigo_tributo = p.codigo_tributo\n"
                    + "order by codigo_produto"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("codigo_produto"));
                    imp.setEan(rst.getString("codigo_barra"));
                    imp.seteBalanca("T".equals(rst.getString("pesavel")));
                    imp.setValidade(rst.getInt("validade"));
                    imp.setSituacaoCadastro(rst.getInt("status") == 1 ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    imp.setDescricaoCompleta(rst.getString("descricao"));
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setTipoEmbalagem((rst.getString("tipoembalagem_pro").replace("]", "")));
                    imp.setQtdEmbalagemCotacao(rst.getInt("qtdembalagem_pro"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem_ean"));
                    imp.setCodMercadologico1(rst.getString("codigo_grupo"));
                    imp.setCodMercadologico2(rst.getString("codigo_subgrupo"));
                    imp.setCodMercadologico3("1");
                    imp.setPrecovenda(rst.getDouble("preco_venda"));
                    imp.setCustoComImposto(rst.getDouble("custo_final"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getString("cst_pis"));
                    imp.setPiscofinsCstCredito(rst.getString("cst_cofins_ent"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "codigo_produto,\n"
                    + "codigo_barra,\n"
                    + "quantidade\n"
                    + "from produtos_codigo\n"
                    + "order by codigo_produto"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("codigo_produto"));
                    imp.setEan(rst.getString("codigo_barra"));
                    imp.setQtdEmbalagem(rst.getInt("quantidade"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos(OpcaoProduto opt) throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        if (opt == OpcaoProduto.TIPO_EMBALAGEM_EAN) {
            try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select "
                        + "codigo_produto,\n"
                        + "codigo_barra,\n"
                        + "unidade_saida\n"
                        + "from produtos"
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("codigo_produto"));
                        imp.setEan(rst.getString("codigo_barra"));
                        imp.setTipoEmbalagem(rst.getString("unidade_saida"));
                        result.add(imp);
                    }
                }
            }
            return result;
        }

        if (opt == OpcaoProduto.ESTOQUE) {
            try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "with mov as\n"
                        + "(\n"
                        + "  select codigo_produto, max(ultimo_movimento) as data\n"
                        + "  from produtos_saldo\n"
                        + "  where codigo_almoxarifado = 1\n"
                        + "  group by codigo_produto\n"
                        + ")\n"
                        + "select e.codigo_produto,\n"
                        + "       e.saldo_atual,\n"
                        + "       e.saldo_minimo,\n"
                        + "       e.saldo_maximo,\n"
                        + "       e.ultimo_movimento\n"
                        + "from produtos_saldo e\n"
                        + "inner join mov as mov2 on mov2.codigo_produto = e.codigo_produto\n"
                        + "where e.codigo_almoxarifado = 1"
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("codigo_produto"));
                        imp.setEstoque(rst.getDouble("saldo_atual"));
                        imp.setEstoqueMaximo(rst.getDouble("saldo_maximo"));
                        imp.setEstoqueMinimo(rst.getDouble("saldo_minimo"));
                        result.add(imp);
                    }
                }
                return result;
            }
        }

        if (opt == OpcaoProduto.ICMS_SAIDA) {
            try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select\n"
                        + " p.codigo_produto, \n"
                        + " p.codigo_barra,\n"
                        + " t.tipo as tipo_trib,\n"
                        + " coalesce(t.icms_vip, 0) icms_cons\n"
                        + "from produtos p\n"
                        + "left join tributos t on t.codigo_tributo = p.codigo_tributo"
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("codigo_produto"));

                        if (null != rst.getString("tipo_trib")) {
                            switch (rst.getString("tipo_trib")) {
                                case "T":
                                    imp.setIcmsCstSaida(0);
                                    break;
                                case "N":
                                    imp.setIcmsCstSaida(41);
                                    break;
                                case "I":
                                    imp.setIcmsCstSaida(40);
                                    break;
                                case "F":
                                    imp.setIcmsCstSaida(60);
                                    break;
                                default:
                                    imp.setIcmsCstSaida(40);
                                    break;
                            }
                        }

                        imp.setIcmsAliqSaida(rst.getDouble("icms_cons"));
                        imp.setIcmsReducaoSaida(0);
                        result.add(imp);
                    }
                }
                return result;
            }
        }
        
        if (opt == OpcaoProduto.ICMS_SAIDA_FORA_ESTADO) {
            try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select\n"
                        + " p.codigo_produto, \n"
                        + " p.codigo_barra,\n"
                        + " tr.codigo_natureza_op as op,\n"
                        + " tr.cst_icms icms_cst,\n"
                        + " coalesce(tr.percentual_icms, 0) icms_aliq,\n"
                        + " coalesce(tr.perc_reducaoicms, 0) icms_red,\n "
                        + " t.tipo as tipo_trib, \n"
                        + " coalesce(t.icms_vip, 0) icms_cons\n"
                        + "from produtos p\n"
                        + "left join tributos t on t.codigo_tributo = p.codigo_tributo\n"
                        + "inner join tributacoes tr on tr.codigo_tributo = t.codigo_tributo\n"
                        + "    and tr.codigo_natureza_op = 1\n"
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("codigo_produto"));

                        if ((rst.getInt("icms_cst") == 0) &&
                                (rst.getDouble("icms_aliq") == 0)) {
                            
                            imp.setIcmsCstSaidaForaEstado(90);
                            imp.setIcmsAliqSaidaForaEstado(0);
                            imp.setIcmsReducaoSaidaForaEstado(0);
                            
                            imp.setIcmsCstSaidaForaEstadoNF(90);
                            imp.setIcmsAliqSaidaForaEstadoNF(0);
                            imp.setIcmsReducaoSaidaForaEstadoNF(0);
                            
                        } else if ((rst.getInt("icms_cst") == 20) &&
                                (rst.getDouble("icms_aliq") == 0) &&
                                (rst.getDouble("icms_red") == 0)) {

                            imp.setIcmsCstSaidaForaEstado(90);
                            imp.setIcmsAliqSaidaForaEstado(0);
                            imp.setIcmsReducaoSaidaForaEstado(0);
                            
                            imp.setIcmsCstSaidaForaEstadoNF(90);
                            imp.setIcmsAliqSaidaForaEstadoNF(0);
                            imp.setIcmsReducaoSaidaForaEstadoNF(0);
                            
                        } else if ((rst.getInt("icms_cst") == 10)
                                || (rst.getInt("icms_cst") == 40)
                                || (rst.getInt("icms_cst") == 41)
                                || (rst.getInt("icms_cst") == 50)
                                || (rst.getInt("icms_cst") == 51)
                                || (rst.getInt("icms_cst") == 60)
                                || (rst.getInt("icms_cst") == 70)
                                || (rst.getInt("icms_cst") == 90)) {

                            imp.setIcmsCstSaidaForaEstado(rst.getInt("icms_cst"));
                            imp.setIcmsAliqSaidaForaEstado(0);
                            imp.setIcmsReducaoSaidaForaEstado(0);
                            
                            imp.setIcmsCstSaidaForaEstadoNF(rst.getInt("icms_cst"));
                            imp.setIcmsAliqSaidaForaEstadoNF(0);
                            imp.setIcmsReducaoSaidaForaEstadoNF(0);
                            
                        } else {
                            
                            imp.setIcmsCstSaidaForaEstado(rst.getInt("icms_cst"));
                            imp.setIcmsAliqSaidaForaEstado(rst.getDouble("icms_aliq"));
                            imp.setIcmsReducaoSaidaForaEstado(rst.getDouble("icms_red"));
                            
                            imp.setIcmsCstSaidaForaEstadoNF(rst.getInt("icms_cst"));
                            imp.setIcmsAliqSaidaForaEstadoNF(rst.getDouble("icms_aliq"));
                            imp.setIcmsReducaoSaidaForaEstadoNF(rst.getDouble("icms_red"));
                            
                        }
                        result.add(imp);
                    }
                }
                return result;
            }
        }

        if (opt == OpcaoProduto.ICMS_ENTRADA) {
            try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select\n"
                        + "p.codigo_produto, \n"
                        + "p.codigo_barra,\n"
                        + "p.descricao,\n"
                        + "tr.codigo_natureza_op as op,\n"
                        + "tr.cst_icms icms_cst,\n"
                        + "coalesce(tr.percentual_icms, 0) icms_aliq,\n"
                        + "coalesce(tr.perc_reducaoicms, 0) icms_red,\n"
                        + "tr.cfop,\n"
                        + "cf.operacao_fiscal,\n"
                        + "cf.descricao as desc_cfop,\n"
                        + "cf.tipo as tipo_cfop\n"
                        + "from produtos p\n"
                        + "left join tributos t on t.codigo_tributo = p.codigo_tributo\n"
                        + "inner join tributacoes tr on tr.codigo_tributo = t.codigo_tributo\n"
                        + "    and tr.codigo_natureza_op = 2\n"
                        + "inner join cfop cf on cf.operacao_fiscal = tr.cfop and cf.tipo = 'D'"
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("codigo_produto"));
                        
                        if ((rst.getInt("icms_cst") == 0) &&
                                (rst.getDouble("icms_aliq") == 0)) {
                            
                            imp.setIcmsCstEntrada(90);
                            imp.setIcmsAliqEntrada(0);
                            imp.setIcmsReducaoEntrada(0);
                            
                        } else if ((rst.getInt("icms_cst") == 20) &&
                                (rst.getDouble("icms_aliq") == 0) &&
                                (rst.getDouble("icms_red") == 0)) {

                            imp.setIcmsCstEntrada(90);
                            imp.setIcmsAliqEntrada(0);
                            imp.setIcmsReducaoEntrada(0);
                            
                        } else if ((rst.getInt("icms_cst") == 10)
                                || (rst.getInt("icms_cst") == 40)
                                || (rst.getInt("icms_cst") == 41)
                                || (rst.getInt("icms_cst") == 50)
                                || (rst.getInt("icms_cst") == 51)
                                || (rst.getInt("icms_cst") == 60)
                                || (rst.getInt("icms_cst") == 70)
                                || (rst.getInt("icms_cst") == 90)) {
                            
                            imp.setIcmsCstEntrada(rst.getInt("icms_cst"));
                            imp.setIcmsAliqEntrada(0);
                            imp.setIcmsReducaoEntrada(0);
                            
                        } else {
                            
                            imp.setIcmsCstEntrada(rst.getInt("icms_cst"));
                            imp.setIcmsAliqEntrada(rst.getDouble("icms_aliq"));
                            imp.setIcmsReducaoEntrada(rst.getDouble("icms_red"));
                        }
                        
                        imp.setIcmsCstEntradaForaEstado(imp.getIcmsCstEntrada());
                        imp.setIcmsAliqEntradaForaEstado(imp.getIcmsAliqEntrada());
                        imp.setIcmsReducaoEntradaForaEstado(imp.getIcmsReducaoEntrada());
                        result.add(imp);
                    }
                }
            }
            return result;
        }
        if (opt == OpcaoProduto.ICMS_ENTRADA_FORA_ESTADO) {
            try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select\n"
                        + "p.codigo_produto, \n"
                        + "p.codigo_barra,\n"
                        + "p.descricao,\n"
                        + "tr.codigo_natureza_op as op,\n"
                        + "tr.cst_icms icms_cst,\n"
                        + "coalesce(tr.percentual_icms, 0) icms_aliq,\n"
                        + "coalesce(tr.perc_reducaoicms, 0) icms_red,\n"
                        + "tr.cfop,\n"
                        + "cf.operacao_fiscal,\n"
                        + "cf.descricao as desc_cfop,\n"
                        + "cf.tipo as tipo_cfop\n"
                        + "from produtos p\n"
                        + "left join tributos t on t.codigo_tributo = p.codigo_tributo\n"
                        + "inner join tributacoes tr on tr.codigo_tributo = t.codigo_tributo\n"
                        + "    and tr.codigo_natureza_op = 2\n"
                        + "inner join cfop cf on cf.operacao_fiscal = tr.cfop and cf.tipo = 'F'"
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("codigo_produto"));
                        
                        
                        if ((rst.getInt("icms_cst") == 0) &&
                                (rst.getDouble("icms_aliq") == 0)) {
                            
                            imp.setIcmsCstEntradaForaEstado(90);
                            imp.setIcmsAliqEntradaForaEstado(0);
                            imp.setIcmsReducaoEntradaForaEstado(0);
                            
                        } else if ((rst.getInt("icms_cst") == 20) &&
                                (rst.getDouble("icms_aliq") == 0) &&
                                (rst.getDouble("icms_red") == 0)) {

                            imp.setIcmsCstEntradaForaEstado(90);
                            imp.setIcmsAliqEntradaForaEstado(0);
                            imp.setIcmsReducaoEntradaForaEstado(0);
                            
                        } else if ((rst.getInt("icms_cst") == 10)
                                || (rst.getInt("icms_cst") == 40)
                                || (rst.getInt("icms_cst") == 41)
                                || (rst.getInt("icms_cst") == 50)
                                || (rst.getInt("icms_cst") == 51)
                                || (rst.getInt("icms_cst") == 60)
                                || (rst.getInt("icms_cst") == 70)
                                || (rst.getInt("icms_cst") == 90)) {
                            
                            imp.setIcmsCstEntradaForaEstado(rst.getInt("icms_cst"));
                            imp.setIcmsAliqEntradaForaEstado(0);
                            imp.setIcmsReducaoEntradaForaEstado(0);
                            
                        } else {
                            
                            imp.setIcmsCstEntradaForaEstado(rst.getInt("icms_cst"));
                            imp.setIcmsAliqEntradaForaEstado(rst.getDouble("icms_aliq"));
                            imp.setIcmsReducaoEntradaForaEstado(rst.getDouble("icms_red"));
                        }
                        result.add(imp);
                    }
                }
            }
            return result;
        }

        if (opt == OpcaoProduto.ICMS_CONSUMIDOR) {
            try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select\n"
                        + " p.codigo_produto, \n"
                        + " p.codigo_barra,\n"
                        + " t.tipo as tipo_trib,\n"
                        + " coalesce(t.icms_vip, 0) icms_cons\n"
                        + "from produtos p\n"
                        + "left join tributos t on t.codigo_tributo = p.codigo_tributo"
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("codigo_produto"));

                        if (null != rst.getString("tipo_trib")) {
                            switch (rst.getString("tipo_trib")) {
                                case "T":
                                    imp.setIcmsCstConsumidor(String.valueOf(0));
                                    break;
                                case "N":
                                    imp.setIcmsCstConsumidor(String.valueOf(41));
                                    break;
                                case "I":
                                    imp.setIcmsCstConsumidor(String.valueOf(40));
                                    break;
                                case "F":
                                    imp.setIcmsCstConsumidor(String.valueOf(60));
                                    break;
                                default:
                                    imp.setIcmsCstConsumidor(String.valueOf(40));
                                    break;
                            }
                        }

                        imp.setIcmsAliqConsumidor(rst.getDouble("icms_cons"));
                        imp.setIcmsReducaoConsumidor(0);
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
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "codigo_fornecedor,\n"
                    + "descricao,\n"
                    + "razao_social,\n"
                    + "nome_fantasia,\n"
                    + "endereco,\n"
                    + "numero,\n"
                    + "bairro,\n"
                    + "cidade,\n"
                    + "estado,\n"
                    + "cep,\n"
                    + "cpf_cnpj,\n"
                    + "inscricao_estadual,\n"
                    + "contato,\n"
                    + "telefone,\n"
                    + "telefone2,\n"
                    + "fax,\n"
                    + "celular,\n"
                    + "email,\n"
                    + "observacao,\n"
                    + "prazo_pagto,\n"
                    + "data_cadastro,\n"
                    + "desativado\n"
                    + "from fornecedores\n"
                    + "order by codigo_fornecedor"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("codigo_fornecedor"));
                    imp.setRazao(rst.getString("razao_social"));
                    imp.setFantasia(rst.getString("nome_fantasia"));
                    imp.setCnpj_cpf(rst.getString("cpf_cnpj"));
                    imp.setIe_rg(rst.getString("inscricao_estadual"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("estado"));
                    imp.setCep(rst.getString("cep"));
                    imp.setObservacao(rst.getString("observacao"));
                    imp.setDatacadastro(rst.getDate("data_cadastro"));
                    imp.setAtivo("F".equals(rst.getString("desativado")));
                    imp.setTel_principal(rst.getString("telefone"));

                    if ((rst.getString("telefone2") != null)
                            && (!rst.getString("telefone2").trim().isEmpty())) {
                        imp.addContato(
                                "TELEFONE 2",
                                rst.getString("telefone2"),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("fax") != null)
                            && (!rst.getString("fax").trim().isEmpty())) {
                        imp.addContato(
                                "FAX",
                                rst.getString("fax"),
                                null,
                                TipoContato.NFE,
                                null
                        );
                    }
                    if ((rst.getString("celular") != null)
                            && (!rst.getString("celular").trim().isEmpty())) {
                        imp.addContato(
                                "CELULAR",
                                null,
                                rst.getString("celular"),
                                TipoContato.NFE,
                                null
                        );
                    }
                    if ((rst.getString("email") != null)
                            && (!rst.getString("email").trim().isEmpty())) {
                        imp.addContato(
                                "EMAIL",
                                null,
                                null,
                                TipoContato.NFE,
                                rst.getString("email").toLowerCase()
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

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "codigo_fornecedor,\n"
                    + "codigo_produto\n"
                    + "ultima_data,\n"
                    + "ultimo_custo,\n"
                    + "referencia\n"
                    + "from produtos_fornecedor\n"
                    + "order by codigo_fornecedor, codigo_produto"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rst.getString("codigo_produto"));
                    imp.setIdFornecedor(rst.getString("codigo_fornecedor"));
                    imp.setCodigoExterno(rst.getString("referencia"));
                    //imp.setDataAlteracao(rst.getDate("ultima_data"));
                    imp.setCustoTabela(rst.getDouble("ultimo_custo"));
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
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "c.codigo_cliente,\n"
                    + "c.descricao,\n"
                    + "c.razao_social, \n"
                    + "c.nome_fantasia,\n"
                    + "c.endereco,\n"
                    + "c.numero,\n"
                    + "c.cep,\n"
                    + "c.bairro,\n"
                    + "c.cidade,\n"
                    + "c.estado,\n"
                    + "c.telefone1,\n"
                    + "c.telefone2,\n"
                    + "c.fax, \n"
                    + "c.celular,\n"
                    + "c.email,\n"
                    + "c.data_cadastro,\n"
                    + "c.data_nascimento,\n"
                    + "c.cpf_cnpj,\n"
                    + "c.inscricao_estadual,\n"
                    + "c.rg,\n"
                    + "c.rg_orgao,\n"
                    + "c.rg_expedicao,\n"
                    + "c.ponto_referencia,\n"
                    + "c.contato,\n"
                    + "c.cobranca_endereco,\n"
                    + "c.cobranca_bairro,\n"
                    + "c.cobranca_cidade,\n"
                    + "c.cobranca_estado,\n"
                    + "c.cobranca_cep,\n"
                    + "c.cobranca_telefone,\n"
                    + "c.nome_mae,\n"
                    + "c.nome_pai,\n"
                    + "c.naturalidade,\n"
                    + "c.trabalho_local,\n"
                    + "c.trabalho_tempo,\n"
                    + "c.trabalho_salario,\n"
                    + "c.trabalho_telefone,\n"
                    + "c.observacao,\n"
                    + "c.limite_credito,\n"
                    + "c.limite_convenio,\n"
                    + "c.status_credito,\n"
                    + "c.entrega_endereco,\n"
                    + "c.entrega_bairro,\n"
                    + "c.entrega_cidade,\n"
                    + "c.entrega_estado,\n"
                    + "c.entrega_cep,\n"
                    + "c.entrega_telefone,\n"
                    + "c.cod_banco,\n"
                    + "c.num_agencia,\n"
                    + "c.numagencia_dv,\n"
                    + "c.num_conta,\n"
                    + "c.numconta_dv,\n"
                    + "c.sexo, \n"
                    + "c.profissao, \n"
                    + "c.status_credito as status, \n"
                    + "c.status_credito \n"
                    + "from clientes c\n"
                    + "order by c.codigo_cliente"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("codigo_cliente"));
                    imp.setRazao(rst.getString("razao_social") == null ? rst.getString("descricao") : rst.getString("razao_social"));
                    imp.setFantasia(rst.getString("nome_fantasia") == null ? rst.getString("descricao") : rst.getString("nome_fantasia"));
                    imp.setCnpj(rst.getString("cpf_cnpj"));
                    imp.setInscricaoestadual(rst.getString("inscricao_estadual") == null ? rst.getString("rg") : rst.getString("inscricao_estadual"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setCep(rst.getString("cep"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("estado"));
                    imp.setTelefone(rst.getString("telefone1"));
                    imp.setFax(rst.getString("fax"));
                    imp.setCelular(rst.getString("celular"));
                    imp.setEmail(rst.getString("email"));
                    imp.setDataCadastro(rst.getDate("data_cadastro"));
                    imp.setDataNascimento(rst.getDate("data_nascimento"));
                    imp.setOrgaoemissor(rst.getString("rg_orgao"));
                    imp.setCobrancaEndereco(rst.getString("cobranca_endereco"));
                    imp.setCobrancaBairro(rst.getString("cobranca_bairro"));
                    imp.setCobrancaMunicipio(rst.getString("cobranca_cidade"));
                    imp.setCobrancaUf(rst.getString("cobranca_estado"));
                    imp.setCobrancaCep(rst.getString("cobranca_cep"));
                    imp.setNomePai(rst.getString("nome_pai"));
                    imp.setNomeMae(rst.getString("nome_mae"));
                    imp.setPermiteCheque(true);
                    imp.setPermiteCreditoRotativo(true);
                    imp.setEmpresa(rst.getString("trabalho_local"));
                    imp.setSalario(rst.getDouble("trabalho_salario"));
                    imp.setCargo(rst.getString("profissao"));
                    imp.setValorLimite(rst.getDouble("limite_credito") < 1 ? rst.getDouble("limite_convenio") : rst.getDouble("limite_credito"));
                    imp.setObservacao(rst.getString("observacao"));
                    imp.setAtivo("A".equals(rst.getString("status").trim()) ? true : false);
                    imp.setBloqueado("C".equals(rst.getString("status_credito")));

                    if ((rst.getString("ponto_referencia") != null)
                            && (!rst.getString("ponto_referencia").trim().isEmpty())) {
                        imp.setObservacao2("PONTO REFERENCIA - " + rst.getString("ponto_referencia") + " ");
                    }
                    if ((rst.getString("naturalidade") != null)
                            && (!rst.getString("naturalidade").trim().isEmpty())) {
                        imp.setObservacao2(imp.getObservacao2() + "NATURALIDADE - " + rst.getString("naturalidade"));
                    }

                    if ((rst.getString("telefone2") != null)
                            && (!rst.getString("telefone2").trim().isEmpty())) {
                        imp.addContato(
                                "1",
                                "TELEFONE 2",
                                rst.getString("telefone2"),
                                null,
                                null
                        );
                    }
                    if ((rst.getString("cobranca_telefone") != null)
                            && (!rst.getString("cobranca_telefone").trim().isEmpty())) {
                        imp.addContato(
                                "2",
                                "TEL COBRANCA",
                                rst.getString("cobranca_telefone"),
                                null,
                                null
                        );
                    }
                    if ((rst.getString("trabalho_telefone") != null)
                            && (!rst.getString("trabalho_telefone").trim().isEmpty())) {
                        imp.addContato(
                                "3",
                                "TEL TRABALHO",
                                rst.getString("trabalho_telefone"),
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

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "sequencial_cpr,\n"
                    + "codigo_cliente,\n"
                    + "documento,\n"
                    + "data_emissao,\n"
                    + "data_vencimento,\n"
                    + "valor,\n"
                    + "juros,\n"
                    + "desconto,\n"
                    + "observacao,\n"
                    + "valor_emaberto\n"
                    + "from MOVIMENTOS_CPR\n"
                    + "where codigo_empresa = " + getLojaOrigem() + "\n"
                    + "and codigo_tipodocumento in (" + tipoDocumento + ")\n"
                    + "and status in ('N', 'P')"
            )) {

                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("sequencial_cpr"));
                    imp.setDataEmissao(rst.getDate("data_emissao"));
                    imp.setDataVencimento(rst.getDate("data_vencimento"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setNumeroCupom(rst.getString("documento"));
                    imp.setJuros(rst.getDouble("juros"));
                    imp.setIdCliente(rst.getString("codigo_cliente"));
                    imp.setObservacao(rst.getString("observacao"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    public List<String> getTipoDocumentos() throws Exception {
        List<String> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select (codigo_tipodocumento ||' - '|| descricao) as documento "
                    + "from tipos_documento "
                    + "where clientefornecedor <> 'F' "
                    + "order by codigo_tipodocumento"
            )) {
                while (rst.next()) {
                    result.add(rst.getString("documento"));
                }
            }
        }
        return result;
    }
}
