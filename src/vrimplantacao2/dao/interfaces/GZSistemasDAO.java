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
import java.util.logging.Logger;
import vrimplantacao.classe.ConexaoMySQL;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author lucasrafael
 */
public class GZSistemasDAO extends InterfaceDAO implements MapaTributoProvider {

    private static final Logger LOG = Logger.getLogger(HipcomDAO.class.getName());

    @Override
    public String getSistema() {
        return "GZSistemas";
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "codigo,\n"
                    + "descricao,\n"
                    + "st as cst,\n"
                    + "aliquota,\n"
                    + "reducao\n"
                    + "from mercodb.tributa\n"
                    + "order by codigo"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(
                            rst.getString("codigo"),
                            rst.getString("descricao"),
                            rst.getInt("cst"),
                            rst.getDouble("aliquota"),
                            rst.getDouble("reducao")
                    ));
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
                    "select distinct\n"
                    + "e.grupo merc1, g.descricao merc1_desc,\n"
                    + "e.depto merc2, d.descricao merc2_desc\n"
                    + "from mercodb.estoque e\n"
                    + "inner join mercodb.grupo g on g.codigo = e.grupo\n"
                    + "inner join mercodb.depto d on d.codigo = e.depto\n"
                    + "where e.depto is not null\n"
                    + "  and e.grupo is not null\n"
                    + "order by e.grupo, e.depto"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("merc1"));
                    imp.setMerc1Descricao(rst.getString("merc1_desc"));
                    imp.setMerc2ID(rst.getString("merc2"));
                    imp.setMerc2Descricao(rst.getString("merc2_desc"));
                    imp.setMerc3ID("1");
                    imp.setMerc3Descricao(rst.getString("merc2_desc"));
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
                    + "e.cdprod,\n"
                    + "e.codbarra,\n"
                    + "e.descricao,\n"
                    + "e.descpdv,\n"
                    + "e.unidade as unidadevenda,\n"
                    + "e.embalagem as unidadecompra,\n"
                    + "e.produtoflv,\n"
                    + "e.setor,\n"
                    + "e.validade,\n"
                    + "e.pesobru,\n"
                    + "e.pesoliq,\n"
                    + "e.cadastro,\n"
                    + "e.depto,\n"
                    + "e.grupo,\n"
                    + "e.cfiscal as ncm,\n"
                    + "e.cest,\n"
                    + "e.stcofins,\n"
                    + "e.stpis,\n"
                    + "e.stcofinsen,\n"
                    + "e.stpisen,\n"
                    + "e.tributa,\n"
                    + "t.codigo as codtrib,\n"
                    + "t.st codTrib,\n"
                    + "e.st trib,\n"
                    + "t.descricao descTrib,\n"
                    + "t.aliquota,\n"
                    + "t.reducao,\n"
                    + "s.precovenda,\n"
                    + "s.perclucro,\n"
                    + "s.precocusto,\n"
                    + "s.estminimo,\n"
                    + "s.estmaximo,\n"
                    + "s.quant as estoque,\n"
                    + "s.situacao\n"
                    + "from mercodb.estoque e\n"
                    + "left join mercodb.tributa t on t.codigo = e.tributa\n"
                    + "left join mercodb.saldos s on s.cdprod = e.cdprod\n"
                    + "where s.loja = " + getLojaOrigem() + "\n"
                    + "order by e.cdprod"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("cdprod"));
                    imp.setEan(rst.getString("codbarra"));
                    imp.seteBalanca(rst.getInt("setor") > 0);
                    imp.setValidade(rst.getInt("validade"));
                    imp.setDescricaoCompleta(rst.getString("descricao"));
                    imp.setDescricaoReduzida(rst.getString("descpdv"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setDataCadastro(rst.getDate("cadastro"));
                    imp.setPesoBruto(rst.getDouble("pesobru"));
                    imp.setPesoLiquido(rst.getDouble("pesoliq"));
                    imp.setCodMercadologico1(rst.getString("grupo"));
                    imp.setCodMercadologico2(rst.getString("depto"));
                    imp.setCodMercadologico3("1");
                    imp.setMargem(rst.getDouble("perclucro"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setCustoComImposto(rst.getDouble("precocusto"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setEstoqueMinimo(rst.getDouble("estminimo"));
                    imp.setEstoqueMaximo(rst.getDouble("estmaximo"));
                    imp.setSituacaoCadastro("A".equals("situacao") ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getString("stpis"));
                    imp.setPiscofinsCstCredito(rst.getString("stpisen"));
                    imp.setIcmsDebitoId(rst.getString("codtrib"));
                    imp.setIcmsCreditoId(rst.getString("codtrib"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "cdprod,\n"
                    + "codbarra,\n"
                    + "multiplos\n"
                    + "from mercodb.barrarel"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("cdprod"));
                    imp.setEan(rst.getString("codbarra"));
                    imp.setQtdEmbalagem(rst.getInt("multiplos"));
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
                    + "codigo,\n"
                    + "razsoc,\n"
                    + "nomfan,\n"
                    + "tipoender,\n"
                    + "numero,\n"
                    + "ender,\n"
                    + "complemen,\n"
                    + "ibge,\n"
                    + "bairro,\n"
                    + "munic,\n"
                    + "estado,\n"
                    + "cep,\n"
                    + "dddtel,\n"
                    + "telefone,\n"
                    + "dddfax,\n"
                    + "telefax,\n"
                    + "contato,\n"
                    + "dddcon,\n"
                    + "telcon,\n"
                    + "cgc,\n"
                    + "insest,\n"
                    + "email,\n"
                    + "endwww\n"
                    + "from mercodb.credor\n"
                    + "order by codigo"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("codigo"));
                    imp.setRazao(rst.getString("razsoc"));
                    imp.setFantasia(rst.getString("nomfan"));
                    imp.setCnpj_cpf(rst.getString("cgc"));
                    imp.setIe_rg(rst.getString("insest"));
                    imp.setEndereco((rst.getString("tipoender") + rst.getString("ender")).trim());
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemen"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setCep(rst.getString("cep"));
                    imp.setIbge_municipio(rst.getInt("ibge"));
                    imp.setMunicipio(rst.getString("munic"));
                    imp.setUf(rst.getString("estado"));
                    imp.setTel_principal((rst.getString("dddtel") + rst.getString("telefone")).trim());

                    if ((rst.getString("telefax") != null)
                            && (!rst.getString("telefax").trim().isEmpty())) {
                        imp.addContato(
                                "FAX",
                                (rst.getString("dddfax") + rst.getString("telefax")).trim(),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("telcon") != null)
                            && (!rst.getString("telcon").trim().isEmpty())) {
                        imp.addContato(
                                rst.getString("contato"),
                                (rst.getString("dddcon") + rst.getString("telcon")).trim(),
                                null,
                                TipoContato.COMERCIAL,
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
                    if ((rst.getString("endwww") != null)
                            && (!rst.getString("endwww").trim().isEmpty())) {
                        imp.addContato(
                                "SITE",
                                null,
                                null,
                                TipoContato.COMERCIAL,
                                rst.getString("endwww").toLowerCase()
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
                    "select\n"
                    + "cdprod,\n"
                    + "cdfornec,\n"
                    + "codigo,\n"
                    + "porcaixa\n"
                    + "from mercodb.estforns\n"
                    + "where cdprod is not null\n"
                    + "and cdfornec is not null"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rst.getString("cdprod"));
                    imp.setIdFornecedor(rst.getString("cdfornec"));
                    imp.setCodigoExterno(rst.getString("codigo"));
                    imp.setQtdEmbalagem(rst.getInt("porcaixa"));
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
