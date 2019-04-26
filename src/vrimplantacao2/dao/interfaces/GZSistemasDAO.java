/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import vrimplantacao.classe.ConexaoMySQL;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
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

    private static final Logger LOG = Logger.getLogger(GZSistemasDAO.class.getName());

    @Override
    public String getSistema() {
        return "GZSistemas";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(new OpcaoProduto[]{
            OpcaoProduto.MERCADOLOGICO,
            OpcaoProduto.MAPA_TRIBUTACAO,
            OpcaoProduto.FAMILIA_PRODUTO,
            OpcaoProduto.FAMILIA,
            OpcaoProduto.PRODUTOS,
            OpcaoProduto.IMPORTAR_MANTER_BALANCA,
            OpcaoProduto.DATA_CADASTRO,
            OpcaoProduto.DATA_ALTERACAO,
            OpcaoProduto.EAN,
            OpcaoProduto.EAN_EM_BRANCO,
            OpcaoProduto.TIPO_EMBALAGEM_EAN,
            OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
            OpcaoProduto.PESAVEL,
            OpcaoProduto.VALIDADE,
            OpcaoProduto.DESC_COMPLETA,
            OpcaoProduto.DESC_GONDOLA,
            OpcaoProduto.ATIVO,
            OpcaoProduto.DESC_REDUZIDA,
            OpcaoProduto.MERCADOLOGICO_PRODUTO,
            OpcaoProduto.PESO_BRUTO,
            OpcaoProduto.PESO_LIQUIDO,
            OpcaoProduto.ESTOQUE_MINIMO,
            OpcaoProduto.ESTOQUE_MAXIMO,
            OpcaoProduto.ESTOQUE,
            OpcaoProduto.CUSTO,
            OpcaoProduto.PRECO,
            OpcaoProduto.NCM,
            OpcaoProduto.CEST,
            OpcaoProduto.PIS_COFINS,
            OpcaoProduto.NATUREZA_RECEITA,
            OpcaoProduto.ICMS,            
            OpcaoProduto.ICMS_SAIDA,
            OpcaoProduto.ICMS_SAIDA_FORA_ESTADO,
            OpcaoProduto.ICMS_ENTRADA,
            OpcaoProduto.ICMS_ENTRADA_FORA_ESTADO,
            OpcaoProduto.USAR_CONVERSAO_ALIQUOTA_COMPLETA,
            OpcaoProduto.MARGEM
        }));
    }

    public ArrayList<Estabelecimento> getLojasCliente() throws Exception {
        ArrayList<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select codigo, nomfan, cgc from mercodb.lojas order by codigo"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("codigo"), rst.getString("nomfan") + " - " + rst.getString("cgc")));
                }
            }
        }

        return result;
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
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();
        
        ProdutoParaFamiliaHelper helper = new ProdutoParaFamiliaHelper(result);
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select distinct\n" +
                    "  eq.codigo,\n" +
                    "  es.descricao\n" +
                    "from\n" +
                    "  equivale eq\n" +
                    "  join estoque es on\n" +
                    "    eq.cdprod = es.cdprod\n" +
                    "order by\n" +
                    "  eq.codigo"
            )) {
                while (rst.next()) {
                    helper.gerarFamilia(rst.getString("codigo"), rst.getString("descricao"));
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
                    + "s.natreceita,\n"
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
                    imp.setTipoEmbalagem(rst.getString("unidadevenda").trim());
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
                    imp.setSituacaoCadastro(rst.getString("situacao").contains("A") ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getString("stpis"));
                    imp.setPiscofinsCstCredito(rst.getString("stpisen"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("natreceita"));
                    imp.setIcmsDebitoId(rst.getString("codtrib"));
                    imp.setIcmsCreditoId(rst.getString("codtrib"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos(OpcaoProduto opt) throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        if (opt == OpcaoProduto.FAMILIA) {
            try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select\n"
                        + "codigo,\n"
                        + "cdprod\n"
                        + "from mercodb.equivale \n"
                        + "order by codigo"
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("cdprod"));
                        imp.setIdFamiliaProduto(rst.getString("codigo"));
                        result.add(imp);
                    }
                }
                return result;
            }            
        }
        
        if (opt == OpcaoProduto.ICMS_SAIDA) {
            try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select\n"
                        + "cdprod,\n"
                        + "tributa\n"
                        + "from mercodb.esttrib\n"
                        + "where uf = 'SP'\n"
                        + "and loja = " + getLojaOrigem()
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("cdprod"));
                        imp.setIcmsDebitoId(rst.getString("tributa"));
                        result.add(imp);
                    }
                }
                return result;
            }
        }

        if (opt == OpcaoProduto.ICMS_ENTRADA) {
            try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select\n"
                        + "cdprod,\n"
                        + "tributa\n"
                        + "from mercodb.esttrib\n"
                        + "where uf = 'SP'\n"
                        + "and loja = " + getLojaOrigem()
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("cdprod"));
                        imp.setIcmsCreditoId(rst.getString("tributa"));
                        result.add(imp);
                    }
                }
                return result;
            }
        }
        
        if (opt == OpcaoProduto.ICMS_SAIDA_FORA_ESTADO) {
            try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select\n"
                        + "cdprod,\n"
                        + "tributa\n"
                        + "from mercodb.esttrib\n"
                        + "where uf <> 'SP'\n"
                        + "and loja = " + getLojaOrigem()
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("cdprod"));
                        imp.setIcmsDebitoId(rst.getString("tributa"));
                        result.add(imp);
                    }
                }
                return result;
            }
        }
        
        if (opt == OpcaoProduto.ICMS_ENTRADA_FORA_ESTADO) {
            try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select\n"
                        + "cdprod,\n"
                        + "tributa\n"
                        + "from mercodb.esttrib\n"
                        + "where uf <> 'SP'\n"
                        + "and loja = " + getLojaOrigem()
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("cdprod"));
                        imp.setIcmsCreditoId(rst.getString("tributa"));
                        result.add(imp);
                    }
                }
                return result;
            }
        }
        
        return null;
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

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "codigo,\n"
                    + "razsoc,\n"
                    + "nomfan,\n"
                    + "tipoender,\n"
                    + "ender,\n"
                    + "numero,\n"
                    + "complemen,\n"
                    + "ibge,\n"
                    + "bairro,\n"
                    + "munic,\n"
                    + "estado,\n"
                    + "cep,\n"
                    + "telcom,\n"
                    + "dddtel,\n"
                    + "telefone,\n"
                    + "dddfax,\n"
                    + "telefax,\n"
                    + "insest,\n"
                    + "cgc,\n"
                    + "dtnasc,\n"
                    + "obs,\n"
                    + "email,\n"
                    + "endwww,\n"
                    + "emptrab,\n"
                    + "endemp,\n"
                    + "bairroemp,\n"
                    + "municemp,\n"
                    + "estadoemp,\n"
                    + "cepemp,\n"
                    + "dddemp,\n"
                    + "telemp,\n"
                    + "cargo,\n"
                    + "profissao,\n"
                    + "estcivil,\n"
                    + "sexo,\n"
                    + "saldo\n"
                    + "from mercodb.clientes\n"
                    + "order by codigo"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("codigo"));
                    imp.setRazao(rst.getString("razsoc"));
                    imp.setFantasia(rst.getString("nomfan"));
                    imp.setCnpj(rst.getString("cgc"));
                    imp.setInscricaoestadual(rst.getString("insest"));
                    imp.setEndereco((rst.getString("tipoender") + rst.getString("ender")).trim());
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemen"));
                    imp.setCep(rst.getString("cep"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("munic"));
                    imp.setMunicipioIBGE(rst.getInt("ibge"));
                    imp.setTelefone((rst.getString("dddtel") + rst.getString("telefone")).trim());
                    imp.setFax((rst.getString("dddfax") + rst.getString("telefax")).trim());
                    imp.setEmail(rst.getString("email"));
                    imp.setDataNascimento(rst.getDate("dtnasc"));
                    imp.setObservacao(rst.getString("obs"));
                    imp.setEmpresa(rst.getString("emptrab"));
                    imp.setEmpresaEndereco(rst.getString("endemp"));
                    imp.setEmpresaBairro(rst.getString("bairroemp"));
                    imp.setEmpresaMunicipio(rst.getString("municemp"));
                    imp.setEmpresaUf(rst.getString("estadoemp"));
                    imp.setEmpresaCep(rst.getString("cepemp"));
                    imp.setEmpresaTelefone((rst.getString("dddemp") + rst.getString("telemp")).trim());
                    imp.setCargo(rst.getString("cargo"));
                    imp.setValorLimite(rst.getDouble("saldo"));
                    imp.setSexo("M".equals(rst.getString("sexo")) ? TipoSexo.MASCULINO : TipoSexo.FEMININO);

                    if ((rst.getString("endwww") != null)
                            && (!rst.getString("endwww").trim().isEmpty())) {
                        imp.addContato(
                                "SITE",
                                null,
                                null,
                                null,
                                rst.getString("endwww").toLowerCase()
                        );
                    }
                    if ((rst.getString("telcom") != null)
                            && (!rst.getString("telcom").trim().isEmpty())) {
                        imp.addContato(
                                "TEL COMERCIAL",
                                rst.getString("telcom"),
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
                    "select\n"
                    + "id,\n"
                    + "cdcliente,\n"
                    + "caixa,\n"
                    + "nrdoc,\n"
                    + "(valor - valrec) valorConta,\n"
                    + "emissao, "
                    + "vencto, "
                    + "obs\n"
                    + "from mercodb.contrec\n"
                    + "where receb is null\n"
                    + "and loja = " + getLojaOrigem()
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("id"));
                    imp.setIdCliente(rst.getString("cdcliente"));
                    imp.setEcf(rst.getString("caixa"));
                    imp.setNumeroCupom(rst.getString("nrdoc"));
                    imp.setValor(rst.getDouble("valorConta"));
                    imp.setDataEmissao(rst.getDate("emissao"));
                    imp.setDataVencimento(rst.getDate("vencto"));
                    imp.setObservacao(rst.getString("obs"));
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
