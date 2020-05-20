/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import org.apache.commons.codec.binary.Base64;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto.ProdutoAnteriorDAO;
import vrimplantacao2.dao.cadastro.produto2.ProdutoRepositoryProvider;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.OfertaIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author lucasrafael
 */
public class SolutionSuperaDAO extends InterfaceDAO {

    private static final Logger LOG = Logger.getLogger(SolutionSuperaDAO.class.getName());

    public String v_lojaMesmoId;
    public boolean gerarCodigoAtacado = false;
    private ProdutoRepositoryProvider repository = new ProdutoRepositoryProvider();
    public int idLojaVR;

    @Override
    public String getSistema() {
        return "SolutionSupera" + v_lojaMesmoId;
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
            OpcaoProduto.QTD_EMBALAGEM_COTACAO,
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
            OpcaoProduto.MARGEM,
            OpcaoProduto.ATACADO
        }));
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select codigo_emp as id, razaosocial from empresa ORDER BY 1"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("id"), rst.getString("razaosocial")));
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
                    + "m1.codigo_grp as merc1,\n"
                    + "m1.grupo as desc_merc1,\n"
                    + "m2.codigo_sgp as merc2,\n"
                    + "m2.subgrupo as desc_merc2,\n"
                    + "'1' as merc3,\n"
                    + "m2.subgrupo as desc_merc3\n"
                    + "from grupos m1\n"
                    + "inner join subgrupos m2 on m2.codigo_grp = m1.codigo_grp\n"
                    + "order by m1.codigo_grp, m2.codigo_sgp"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("merc1").trim());
                    imp.setMerc1Descricao(rst.getString("desc_merc1"));
                    imp.setMerc2ID(rst.getString("merc2").trim());
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
                    + "p.codigo_pro as id,\n"
                    + "p.codigo_grp as merc1,\n"
                    + "p.codigo_sgp as merc2,\n"
                    + "'1' as merc3,\n"
                    + "p.codigo_ean as ean,\n"
                    + "p.descricao as descricaoproduto,\n"
                    + "p.cod_ncm as ncm,\n"
                    + "p.custo_unitario as custo,\n"
                    + "p.preco_venda as preco,\n"
                    + "p.unidade_entrada as embcompra,\n"
                    + "p.unidade_venda as embvenda,\n"
                    + "p.quanti_embalagem as qtdembalagem,\n"
                    + "p.estoque,\n"
                    + "p.peso_bruto,\n"
                    + "p.peso_liquido,\n"
                    + "p.data_cadastro,\n"
                    + "p.status as situacaocadastro,\n"
                    + "p.margemlucro as margem1,\n"
                    + "p.margemlucro2 as margem2,\n"
                    + "p.estoque_max,\n"
                    + "p.produto_balanca as balanca,\n"
                    + "p.cod_nat_receita as naturezareceita,\n"
                    + "p.cest, \n"
                    + "p.cst_pis_saida,\n"
                    + "p.cst_pis_entrada,\n"
                    + "p.cst_icms_entrada_interno as cst_aliquota_credito,\n"
                    + "p.cst_icms_entrada_externo as cst_aliquota_credito_fora,\n"
                    + "p.cst_icms_saida_interno as cst_aliquota_debito,\n"
                    + "p.cst_icms_saida_externo as cst_aliquota_debito_fora,\n"
                    + "p.aliquota_icms_entrada_interno as aliquota_credito,\n"
                    + "p.aliquota_icms_entrada_externo as aliquota_credito_fora,\n"
                    + "p.aliquota_icms_saida_interno as aliquota_debito,\n"
                    + "p.aliquota_icms_saida_externo as aliquota_debito_fora\n"
                    + "from produtos p\n"
                    + "order by p.codigo_pro"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("ean"));
                    
                    if ((imp.getEan() != null) && (!imp.getEan().trim().isEmpty())) {
                        if (imp.getEan().trim().length() <= 6) {
                            imp.seteBalanca("T".equals(rst.getString("balanca")));
                            
                            if (imp.isBalanca()) {
                                imp.setManterEAN(false);
                            } else {
                                imp.setManterEAN(true);
                            }
                        } else {
                            imp.seteBalanca(false);
                        }
                    } else {
                        imp.seteBalanca("T".equals(rst.getString("balanca")));
                    }
                    
                    imp.setDescricaoCompleta(rst.getString("descricaoproduto"));
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setCodMercadologico1(rst.getString("merc1").trim());
                    imp.setCodMercadologico2(rst.getString("merc2").trim());
                    imp.setCodMercadologico3(rst.getString("merc3"));
                    imp.setTipoEmbalagemCotacao(rst.getString("embcompra"));
                    imp.setTipoEmbalagem(rst.getString("embvenda"));
                    imp.setQtdEmbalagemCotacao(rst.getInt("qtdembalagem"));
                    imp.setPesoBruto(rst.getDouble("peso_bruto"));
                    imp.setPesoLiquido(rst.getDouble("peso_liquido"));
                    imp.setDataCadastro(rst.getDate("data_cadastro"));
                    imp.setMargem(rst.getDouble("margem1"));
                    imp.setPrecovenda(rst.getDouble("preco"));
                    imp.setCustoComImposto(rst.getDouble("custo"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setEstoqueMaximo(rst.getDouble("estoque_max"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setSituacaoCadastro(rst.getString("situacaocadastro").contains("A") ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getString("cst_pis_saida"));
                    imp.setPiscofinsCstCredito(rst.getString("cst_pis_entrada"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("naturezareceita"));
                    imp.setIcmsCstSaida(rst.getInt("cst_aliquota_credito"));
                    imp.setIcmsAliqSaida(rst.getDouble("aliquota_debito"));
                    imp.setIcmsCstSaidaForaEstado(rst.getInt("cst_aliquota_debito_fora"));
                    imp.setIcmsAliqSaidaForaEstado(rst.getDouble("aliquota_debito_fora"));
                    imp.setIcmsCstSaidaForaEstadoNF(rst.getInt("cst_aliquota_debito_fora"));
                    imp.setIcmsAliqSaidaForaEstadoNF(rst.getDouble("aliquota_debito_fora"));
                    imp.setIcmsCstEntrada(rst.getInt("cst_aliquota_credito"));
                    imp.setIcmsAliqEntrada(rst.getDouble("aliquota_credito"));
                    imp.setIcmsCstEntradaForaEstado(rst.getInt("cst_aliquota_credito_fora"));
                    imp.setIcmsAliqEntradaForaEstado(rst.getDouble("aliquota_credito_fora"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos(OpcaoProduto opt) throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        if (opt == OpcaoProduto.ATACADO) {
            try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select\n"
                        + "pr.codigo_pro,\n"
                        + "pr.qtde,\n"
                        + "p.preco_venda as preconormal,\n"
                        + "pr.preco as precoatacado\n"
                        + "from produto_preco_reduzido pr\n"
                        + "inner join produtos p on p.codigo_pro = pr.codigo_pro and pr.qtde > 1"
                )) {
                    while (rst.next()) {
                        int codigoAtual = new ProdutoAnteriorDAO().getCodigoAnterior2(getSistema(), getLojaOrigem(), rst.getString("codigo_pro"));
                        if (codigoAtual > 0) {
                            ProdutoIMP imp = new ProdutoIMP();
                            imp.setImportLoja(getLojaOrigem());
                            imp.setImportSistema(getSistema());
                            imp.setImportId(rst.getString("codigo_pro"));
                            imp.setQtdEmbalagem(rst.getInt("qtde"));
                            imp.setEan(String.valueOf(idLojaVR) + "99999" + String.valueOf(codigoAtual));
                            imp.setPrecovenda(rst.getDouble("preconormal"));
                            imp.setAtacadoPreco(rst.getDouble("precoatacado"));
                            result.add(imp);
                        }
                    }
                }
            }
        }
        return result;
    }

    /*@Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "codigo_pro,\n"
                    + "qtde,\n"
                    + "preco\n"
                    + "from produto_preco_reduzido\n"
                    + "where qtde > 1"
            )) {
                while (rst.next()) {
                    int codigoAtual = new ProdutoAnteriorDAO().getCodigoAnterior2(getSistema(), getLojaOrigem(), rst.getString("codigo_pro"));
                    if (codigoAtual > 0) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("codigo_pro"));
                        imp.setQtdEmbalagem(rst.getInt("qtde"));
                        imp.setEan(String.valueOf(idLojaVR) + "99999" + String.valueOf(codigoAtual));
                        result.add(imp);
                    }
                }
            }
        }
        return result;
    }*/

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "f.codigo_for as id,\n"
                    + "f.codigo_cid as ibge_municipio,\n"
                    + "upper(m.municipio) as municipio,\n"
                    + "m.cod_uf_ibge,\n"
                    + "u.sigla as uf,\n"
                    + "f.razao_soc as razao,\n"
                    + "f.fantasia,\n"
                    + "f.endereco,\n"
                    + "f.numero, \n"
                    + "f.bairro,\n"
                    + "f.cep,\n"
                    + "f.contato,\n"
                    + "f.telefone,\n"
                    + "f.fax,\n"
                    + "f.cnpj,\n"
                    + "f.inscricao as ie_rg,\n"
                    + "f.situacao as situacaocadastro,\n"
                    + "f.emaill, \n"
                    + "f.representante,\n"
                    + "f.endereco_rep,\n"
                    + "f.numero_rep,\n"
                    + "f.bairro_rep,\n"
                    + "f.cidade_rep,\n"
                    + "f.cep_rep,\n"
                    + "f.estado_rep,\n"
                    + "f.telefone_rep, \n"
                    + "f.fax_rep,\n"
                    + "f.celular, \n"
                    + "f.emaill_rep\n"
                    + "from fornecedores f\n"
                    + "left join municipios_ibge m on m.cod_municipio_ibge = f.codigo_cid\n"
                    + "inner join uf_ibge u on u.cod_uf = m.cod_uf_ibge\n"
                    + "order by f.codigo_for"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("ie_rg"));
                    imp.setAtivo("A".equals(rst.getString("situacaocadastro")));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setCep(rst.getString("cep"));
                    imp.setIbge_municipio(rst.getInt("ibge_municipio"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setIbge_uf(rst.getInt("cod_uf_ibge"));
                    imp.setUf(rst.getString("uf"));
                    imp.setTel_principal(rst.getString("telefone"));

                    if ((rst.getString("representante") != null)
                            && (!rst.getString("representante").trim().isEmpty())) {
                        imp.setObservacao("REPRESENTANTE " + rst.getString("representante"));
                    }

                    if ((rst.getString("fax") != null)
                            && (!rst.getString("fax").trim().isEmpty())) {
                        imp.addContato(
                                "FAX",
                                rst.getString("fax"),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("emaill") != null)
                            && (!rst.getString("emaill").trim().isEmpty())) {
                        imp.addContato(
                                "EMAIL",
                                null,
                                null,
                                TipoContato.NFE,
                                rst.getString("emaill").toLowerCase()
                        );
                    }
                    if ((rst.getString("celular") != null)
                            && (!rst.getString("celular").trim().isEmpty())) {
                        imp.addContato(
                                "CELULAR",
                                null,
                                rst.getString("celular"),
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("telefone_rep") != null)
                            && (!rst.getString("telefone_rep").trim().isEmpty())) {
                        imp.addContato(
                                "TELEFONE REP",
                                rst.getString("telefone_rep"),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("fax_rep") != null)
                            && (!rst.getString("fax_rep").trim().isEmpty())) {
                        imp.addContato(
                                "FAX REP",
                                rst.getString("fax_rep"),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("emaill_rep") != null)
                            && (!rst.getString("emaill_rep").trim().isEmpty())) {
                        imp.addContato(
                                "EMAIL REP",
                                null,
                                null,
                                TipoContato.COMERCIAL,
                                rst.getString("emaill_rep").toLowerCase()
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
                    + "id_fornecedor,\n"
                    + "id_produto,\n"
                    + "id\n"
                    + "from produto_fornecedores\n"
                    + "order by id_fornecedor, id_produto"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rst.getString("id_produto"));
                    imp.setIdFornecedor(rst.getString("id_fornecedor"));
                    imp.setCodigoExterno(rst.getString("id"));
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
                    + "c.codigo_cli as id,\n"
                    + "c.codigo_cid as ibge_municipio,\n"
                    + "upper(m.municipio) as municipio,\n"
                    + "m.cod_uf_ibge,\n"
                    + "u.sigla as uf,\n"
                    + "c.cod_tipo_logradouro,\n"
                    + "c.codigo_bco as id_banco,\n"
                    + "c.razaosocial as razao,\n"
                    + "c.fantasia,\n"
                    + "c.endereco,\n"
                    + "c.bairro,\n"
                    + "c.cep, \n"
                    + "c.telefone, \n"
                    + "c.telefax as fax,\n"
                    + "c.contato, \n"
                    + "c.cnpjcpf,\n"
                    + "c.inscricao,\n"
                    + "c.identidade as ie_rg,\n"
                    + "c.endereco_cob,\n"
                    + "c.bairro_cob,\n"
                    + "c.cidade_cob,\n"
                    + "c.cep_cob,\n"
                    + "c.telefone_cob,\n"
                    + "c.telefax_cob,\n"
                    + "c.contato_cob,\n"
                    + "c.filiacao_pai as nome_pai,\n"
                    + "c.filiacao_mae as nome_mae,\n"
                    + "c.conjugue,\n"
                    + "c.cpf as cpf_conjuge,\n"
                    + "c.identidade_con as ierg_conjuge,\n"
                    + "c.data_cadastro,\n"
                    + "c.data_nascimento,\n"
                    + "c.status as situacaocadastro,\n"
                    + "c.observacao, \n"
                    + "c.emaill,\n"
                    + "c.celular, \n"
                    + "c.limite_cred,\n"
                    + "c.numero,\n"
                    + "c.complemento_endereco\n"
                    + "from clientes c\n"
                    + "left join municipios_ibge m on m.cod_municipio_ibge = c.codigo_cid\n"
                    + "inner join uf_ibge u on u.cod_uf = m.cod_uf_ibge\n"
                    + "order by c.codigo_cli"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj(rst.getString("cnpjcpf"));
                    imp.setInscricaoestadual(rst.getString("ie_rg"));
                    imp.setDataCadastro(rst.getDate("data_cadastro"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento_endereco"));
                    imp.setCep(rst.getString("cep"));
                    imp.setMunicipioIBGE(rst.getInt("ibge_municipio"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setUfIBGE(rst.getInt("cod_uf_ibge"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCobrancaEndereco(rst.getString("endereco_cob"));
                    imp.setCobrancaBairro(rst.getString("bairro_cob"));
                    imp.setCobrancaMunicipio(rst.getString("cidade_cob"));
                    imp.setCobrancaCep(rst.getString("cep_cob"));
                    imp.setNomePai(rst.getString("nome_pai"));
                    imp.setNomeMae(rst.getString("nome_mae"));
                    imp.setNomeConjuge(rst.getString("conjugue"));
                    imp.setDataNascimento(rst.getDate("data_nascimento"));
                    imp.setAtivo("A".equals(rst.getString("situacaocadastro")));
                    imp.setObservacao(rst.getString("observacao"));
                    imp.setTelefone(rst.getString("telefone"));
                    imp.setFax(rst.getString("fax"));
                    imp.setCelular(rst.getString("celular"));
                    imp.setEmail(rst.getString("emaill"));
                    imp.setValorLimite(rst.getDouble("limite_cred"));

                    if ((rst.getString("telefone_cob") != null)
                            && (!rst.getString("telefone_cob").trim().isEmpty())) {
                        imp.addContato(
                                "TELEFONE COB",
                                rst.getString("telefone_cob"),
                                null,
                                null,
                                null
                        );
                    }
                    if ((rst.getString("telefax_cob") != null)
                            && (!rst.getString("telefax_cob").trim().isEmpty())) {
                        imp.addContato(
                                "FAX COB",
                                rst.getString("telefax_cob"),
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

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "codigo_crc as id,\n"
                    + "titulo as cupom,\n"
                    + "codigo_cli as id_cliente,\n"
                    + "fatura as numerocupom,\n"
                    + "data_emi as emissao,\n"
                    + "data_ven as vencimento,\n"
                    + "valor_tit as valor,\n"
                    + "observacao\n"
                    + "from contasreceber\n"
                    + "where data_pgt is null\n"
                    + "order by data_emi asc"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("id"));
                    imp.setIdCliente(rst.getString("id_cliente"));
                    imp.setNumeroCupom(rst.getString("numerocupom"));
                    imp.setDataEmissao(rst.getDate("emissao"));
                    imp.setDataVencimento(rst.getDate("vencimento"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setObservacao(rst.getString("observacao"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<OfertaIMP> getOfertas(Date dataTermino) throws Exception {
        List<OfertaIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "codigo_pro,\n"
                    + "inicio,\n"
                    + "final,\n"
                    + "preco,\n"
                    + "promocao\n"
                    + "from promocaoprodutos\n"
                    + "where final >= '" + new SimpleDateFormat("yyyy-MM-dd").format(dataTermino) + "'"
            )) {
                while (rst.next()) {
                    OfertaIMP imp = new OfertaIMP();
                    imp.setIdProduto(rst.getString("codigo_pro"));
                    imp.setDataInicio(rst.getDate("inicio"));
                    imp.setDataFim(rst.getDate("final"));
                    imp.setPrecoNormal(rst.getDouble("preco"));
                    imp.setPrecoOferta(rst.getDouble("promocao"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

}
