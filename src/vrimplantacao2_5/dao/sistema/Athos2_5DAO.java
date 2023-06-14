/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2_5.dao.sistema;

/**
 *
 * @author Bruno
 *
 * SISTEMA REFATORADO DA 2.0 PARA 2.5 E AINDA NÃO TESTADO FAVOR VALIDAR OS
 * METODOS
 */
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import vrimplantacao.classe.ConexaoPostgres;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.parametro.Parametros;
import vrimplantacao2.vo.enums.OpcaoFiscal;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoEstadoCivil;
import vrimplantacao2.vo.enums.TipoIva;
import vrimplantacao2.vo.enums.TipoProduto;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.PautaFiscalIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Lucas
 */
public class Athos2_5DAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "ATHOS";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                new OpcaoProduto[]{
                    OpcaoProduto.MERCADOLOGICO_PRODUTO,
                    OpcaoProduto.MERCADOLOGICO,
                    OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                    OpcaoProduto.FAMILIA_PRODUTO,
                    OpcaoProduto.FAMILIA,
                    OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                    OpcaoProduto.MANTER_DESCRICAO_PRODUTO,
                    OpcaoProduto.PRODUTOS,
                    OpcaoProduto.EAN,
                    OpcaoProduto.EAN_EM_BRANCO,
                    OpcaoProduto.DATA_CADASTRO,
                    OpcaoProduto.TIPO_EMBALAGEM_EAN,
                    OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                    OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                    OpcaoProduto.QTD_EMBALAGEM_EAN,
                    OpcaoProduto.PESAVEL,
                    OpcaoProduto.VALIDADE,
                    OpcaoProduto.DESC_COMPLETA,
                    OpcaoProduto.DESC_GONDOLA,
                    OpcaoProduto.DESC_REDUZIDA,
                    OpcaoProduto.ESTOQUE_MAXIMO,
                    OpcaoProduto.ESTOQUE_MINIMO,
                    OpcaoProduto.PRECO,
                    OpcaoProduto.CUSTO,
                    OpcaoProduto.CUSTO_COM_IMPOSTO,
                    OpcaoProduto.CUSTO_SEM_IMPOSTO,
                    OpcaoProduto.ESTOQUE,
                    OpcaoProduto.ATIVO,
                    OpcaoProduto.NCM,
                    OpcaoProduto.CEST,
                    OpcaoProduto.PIS_COFINS,
                    OpcaoProduto.NATUREZA_RECEITA,
                    OpcaoProduto.ICMS,
                    OpcaoProduto.PAUTA_FISCAL,
                    OpcaoProduto.PAUTA_FISCAL_PRODUTO,
                    OpcaoProduto.MARGEM,
                    OpcaoProduto.OFERTA,
                    OpcaoProduto.MAPA_TRIBUTACAO,
                    OpcaoProduto.EXCECAO,
                    OpcaoProduto.DESCONTINUADO,
                    OpcaoProduto.ATACADO
                }
        ));
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "idempresa as id, "
                    + "(nomefantasia||' - '||cnpj) as descricao\n"
                    + "from empresa order by 1"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("id"), rst.getString("descricao")));
                }
            }
        }

        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select distinct tributacao as cst, icms as aliquota from produto order by 1"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(
                            rst.getString("cst") + " " + rst.getString("aliquota"),
                            rst.getString("cst") + " " + rst.getString("aliquota"),
                            rst.getInt("cst"),
                            0,
                            0
                    ));
                }
            }

            try (ResultSet rst = stm.executeQuery(
                    "select distinct tributacaonfe as cst, icmsnfe as aliquota from produto order by 1"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(
                            rst.getString("cst") + " " + rst.getString("aliquota"),
                            rst.getString("cst") + " " + rst.getString("aliquota"),
                            rst.getInt("cst"),
                            0,
                            0
                    ));
                }
            }
        }

        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	m1.idsetor as merc1,\n"
                    + "	m1.nome as desc_merc1,\n"
                    + "	coalesce(m2.idgrupo, 1) as merc2,\n"
                    + "	coalesce(m2.nome, m1.nome) as desc_merc2,\n"
                    + "	'1' as merc3,\n"
                    + "	coalesce(m2.nome, m1.nome) as desc_merc3\n"
                    + "from produto_setor m1\n"
                    + "left join produto_grupo m2\n"
                    + "	on m2.idsetor = m1.idsetor\n"
                    + "order by 1, 3"
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

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	p.idproduto as id,\n"
                    + "	p.codigobarra1 as ean,\n"
                    + "	un.sigla as tipoembalagem,\n"
                    + "	p.pesanabalanca as balanca,\n"
                    + "	p.descricaoproduto as descricaocompleta,\n"
                    + "	p.descricaocurta as descricaoreduzida,\n"
                    + "	p.statusproduto as situacaocadastro,\n"
                    + "	p.idsetor as merc1,\n"
                    + "	coalesce(p.idgrupo, 1) as merc2,\n"
                    + "	coalesce(p.idsubgrupo, 1) as merc3,\n"
                    + "	p.controlaestoque,\n"
                    + "	p.vendeproduto,\n"
                    + "	p.valorvenda1 as precovenda,\n"
                    + "	p.margemvenda1 as margem,\n"
                    + "	p.icms,\n"
                    + "	p.tributacao,\n"
                    + "	p.quantidadecaixa as qtdembalagemcotaacao,\n"
                    + "	p.valorcustounitario as custo,\n"
                    + "	p.custorealunitario as custoreal,\n"
                    + "	p.estoquemaximo,\n"
                    + "	p.estoqueminimo,\n"
                    + "	p.pesobruto,\n"
                    + "	p.pesoliquido,	\n"
                    + "	p.pesaporquilo,\n"
                    + "	p.validadeproduto as validade,\n"
                    + "	p.datacadastro,\n"
                    + "	p.dataultimaalteracao,\n"
                    + "	p.estoqueloja as estoque,\n"
                    + "	p.ncm,\n"
                    + "	p.piscst,\n"
                    + "	p.cofinscst,\n"
                    + "	p.iva,\n"
                    + "	p.cest,\n"
                    + "	p.icmsnfe,\n"
                    + "	p.tributacaonfe,\n"
                    + " p.tipoproduto, \n"
                    + " p.icms, \n"
                    + " p.tributacao, \n"
                    + " p.icmsnfe, \n"
                    + " p.tributacaonfe \n"
                    + "from produto p\n"
                    + "left join produto_unidade un \n"
                    + "	on un.idunidade = p.idunidade\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("ean"));
                    imp.seteBalanca(rst.getBoolean("balanca"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    imp.setQtdEmbalagemCotacao(rst.getInt("qtdembalagemcotaacao"));
                    imp.setSituacaoCadastro(rst.getBoolean("situacaocadastro") == true ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    imp.setTipoProduto(rst.getBoolean("tipoproduto") == true ? TipoProduto.MERCADORIA_REVENDA : TipoProduto.SERVICOS);
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setCodMercadologico1(rst.getString("merc1"));
                    imp.setCodMercadologico2(rst.getString("merc2"));
                    imp.setCodMercadologico3(rst.getString("merc3"));
                    imp.setPesoBruto(rst.getDouble("pesobruto"));
                    imp.setPesoLiquido(rst.getDouble("pesoliquido"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setCustoComImposto(rst.getDouble("custo"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setEstoqueMaximo(rst.getDouble("estoquemaximo"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getString("piscst"));
                    imp.setPiscofinsCstCredito(rst.getString("cofinscst"));
                    imp.setIcmsDebitoId(rst.getString("tributacaonfe") + " " + rst.getString("icmsnfe"));
                    imp.setIcmsDebitoForaEstadoId(rst.getString("tributacaonfe") + " " + rst.getString("icmsnfe"));
                    imp.setIcmsDebitoForaEstadoNfId(rst.getString("tributacaonfe") + " " + rst.getString("icmsnfe"));
                    imp.setIcmsCreditoId(rst.getString("tributacaonfe") + " " + rst.getString("icmsnfe"));
                    imp.setIcmsCreditoForaEstadoId(rst.getString("tributacaonfe") + " " + rst.getString("icmsnfe"));
                    imp.setIcmsConsumidorId(rst.getString("tributacao") + " " + rst.getString("icms"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	p.idproduto as id,\n"
                    + "	p.codigobarra2 as ean,\n"
                    + "	un.sigla as tipoembalagem\n"
                    + "from produto p \n"
                    + "left join produto_unidade un \n"
                    + "	on un.idunidade = p.idunidade\n"
                    + "where codigobarra2 != ''	\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("ean"));
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos(OpcaoProduto opt) throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        if (opt == OpcaoProduto.EXCECAO) {
            try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select idproduto, \n"
                        + "	ncm, \n"
                        + "	tributacaonfe, \n"
                        + "	icmsnfe,\n"
                        + "	iva \n"
                        + "from produto \n"
                        + "where coalesce(iva, 0) > 0"
                )) {
                    while (rst.next()) {

                        String idPautaFiscal = rst.getString("ncm") + "-"
                                + rst.getString("tributacaonfe") + "-"
                                + rst.getString("icmsnfe") + "-"
                                + rst.getString("iva");

                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("idproduto"));
                        imp.setPautaFiscalId(idPautaFiscal);
                        result.add(imp);
                    }
                }
                return result;
            }
        }

        return null;
    }

    @Override
    public List<PautaFiscalIMP> getPautasFiscais(Set<OpcaoFiscal> opcoes) throws Exception {
        List<PautaFiscalIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select distinct \n"
                    + "	ncm, \n"
                    + "	tributacaonfe, \n"
                    + "	icmsnfe,\n"
                    + "	iva \n"
                    + "from produto \n"
                    + "where coalesce(iva, 0) > 0"
            )) {
                while (rst.next()) {

                    double aliquotaDebito = 0;
                    double aliquotaCredito = 0;

                    if (!Utils.encontrouLetraCampoNumerico(rst.getString("icmsnfe"))) {
                        aliquotaDebito = Double.parseDouble(rst.getString("icmsnfe").replace(",", "."));
                        aliquotaCredito = Double.parseDouble(rst.getString("icmsnfe").replace(",", "."));
                    }

                    PautaFiscalIMP imp = new PautaFiscalIMP();
                    imp.setId(rst.getString("ncm") + "-"
                            + rst.getString("tributacaonfe") + "-"
                            + rst.getString("icmsnfe") + "-"
                            + rst.getString("iva")
                    );

                    imp.setTipoIva(TipoIva.PERCENTUAL);
                    imp.setNcm(rst.getString("ncm"));
                    imp.setIva(rst.getDouble("iva"));
                    imp.setIvaAjustado(imp.getIva());
                    imp.setUf(Parametros.get().getUfPadraoV2().getSigla());

                    imp.setAliquotaDebito(0, aliquotaDebito, 0);
                    imp.setAliquotaDebitoForaEstado(0, aliquotaDebito, 0);
                    imp.setAliquotaCredito(0, aliquotaCredito, 0);
                    imp.setAliquotaCreditoForaEstado(0, aliquotaCredito, 0);

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	f.idfornecedor as id,\n"
                    + "	fj.razaosocial as razao,\n"
                    + "	fj.nomefantasia as fantasia,\n"
                    + "	fj.ie as inscricaoestadual,\n"
                    + "	fj.cnpj,\n"
                    + "	fj.idcnae,\n"
                    + "	fj.im as inscricaomunicipal,\n"
                    + "	f.datacadastro,\n"
                    + "	fe.logradouro as endereco,\n"
                    + "	fe.numero,\n"
                    + "	fe.complemento,\n"
                    + "	fe.bairro,\n"
                    + "	fe.cidade,\n"
                    + "	fe.uf,\n"
                    + "	fe.cep,	\n"
                    + "	f.statusfornecedor as situacaocadastro,\n"
                    + "	f.observacao,\n"
                    + "	f.emailfornecedor,\n"
                    + "	(f.dddtelefoneempresa||telefoneempresa) as telefone\n"
                    + "from fornecedor f\n"
                    + "join fornecedor_juridico fj\n"
                    + "	on fj.idfornecedor = f.idfornecedor\n"
                    + "left join fornecedor_endereco fe\n"
                    + "	on fe.idfornecedor = f.idfornecedor"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("inscricaoestadual"));
                    imp.setInsc_municipal(rst.getString("inscricaomunicipal"));
                    imp.setDatacadastro(rst.getDate("datacadastro"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setTel_principal(rst.getString("telefone"));
                    imp.setAtivo(rst.getBoolean("situacaocadastro"));
                    imp.setObservacao(rst.getString("observacao"));

                    if ((rst.getString("emailfornecedor") != null)
                            && (!rst.getString("emailfornecedor").trim().isEmpty())) {
                        imp.addEmail("EMAIL", rst.getString("emailfornecedor"), TipoContato.NFE);
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

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	idfornecedor,\n"
                    + "	idproduto,\n"
                    + "	referencia\n"
                    + "from referencia_fornecedor_produto\n"
                    + "order by 1, 2"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rst.getString("idproduto"));
                    imp.setIdFornecedor(rst.getString("idfornecedor"));
                    imp.setCodigoExterno(rst.getString("referencia"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	c.idcliente as id,\n"
                    + "	cf.nome,\n"
                    + "	cf.cpf,\n"
                    + "	cf.rg,\n"
                    + "	ce.logradouro as endereco,\n"
                    + "	ce.numero,\n"
                    + "	ce.complemento,\n"
                    + "	ce.bairro,\n"
                    + "	ce.cidade,\n"
                    + "	ce.uf,\n"
                    + "	ce.cep,	\n"
                    + "	ce.cobranca_logradouro as endereco_cobranca,\n"
                    + "	ce.cobranca_numero as numero_cobranca,\n"
                    + "	ce.cobranca_complemento as complemento_cobranca,\n"
                    + "	ce.cobranca_bairro as bairro_cobranca,\n"
                    + "	ce.cobranca_cidade as cidade_cobranca,\n"
                    + "	ce.cobranca_uf as uf_cobranca,\n"
                    + "	ce.cobranca_cep as cep_cobranca,\n"
                    + "	cf.orgaoemissorrg,\n"
                    + "	cf.ufemissorrg,\n"
                    + "	cf.dataemissaorg,\n"
                    + "	upper(cf.estadocivil) as estadocivil,\n"
                    + "	upper(cf.sexo) as sexo,\n"
                    + "	cf.datanascimento,\n"
                    + "	c.datacadastro,\n"
                    + "	c.statuscliente,\n"
                    + "	c.bloqueaprazo,\n"
                    + "	c.emailcliente,\n"
                    + "	c.limitecredito,\n"
                    + "	c.telefoneempresa\n"
                    + "from cliente c\n"
                    + "join cliente_fisico cf on cf.idcliente = c.idcliente\n"
                    + "left join cliente_endereco ce on ce.idcliente = c.idcliente\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("id"));
                    imp.setRazao(rst.getString("nome"));
                    imp.setFantasia(imp.getRazao());
                    imp.setCnpj(rst.getString("cpf"));
                    imp.setInscricaoestadual(rst.getString("rg"));
                    imp.setOrgaoemissor(rst.getString("orgaoemissorrg"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setCobrancaEndereco(rst.getString("endereco_cobranca"));
                    imp.setCobrancaNumero(rst.getString("numero_cobranca"));
                    imp.setCobrancaComplemento(rst.getString("complemento_cobranca"));
                    imp.setCobrancaBairro(rst.getString("bairro_cobranca"));
                    imp.setCobrancaMunicipio(rst.getString("cidade_cobranca"));
                    imp.setCobrancaUf(rst.getString("uf_cobranca"));
                    imp.setCobrancaCep(rst.getString("cep_cobranca"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setDataNascimento(rst.getDate("datanascimento"));
                    imp.setValorLimite(rst.getDouble("limitecredito"));
                    imp.setAtivo(rst.getBoolean("statuscliente"));
                    imp.setBloqueado(rst.getBoolean("bloqueaprazo"));
                    imp.setEmail(rst.getString("emailcliente"));
                    imp.setTelefone(rst.getString("telefoneempresa"));

                    if (imp.getValorLimite() > 0) {
                        imp.setPermiteCreditoRotativo(true);
                        imp.setPermiteCheque(true);
                    }

                    if ((rst.getString("estadocivil") != null)
                            && (!rst.getString("estadocivil").trim().isEmpty())) {

                        if (null == rst.getString("estadocivil").trim()) {
                            imp.setEstadoCivil(TipoEstadoCivil.NAO_INFORMADO);
                        } else {
                            switch (rst.getString("estadocivil").trim()) {
                                case "S":
                                    imp.setEstadoCivil(TipoEstadoCivil.SOLTEIRO);
                                    break;
                                case "C":
                                    imp.setEstadoCivil(TipoEstadoCivil.CASADO);
                                    break;
                                default:
                                    imp.setEstadoCivil(TipoEstadoCivil.NAO_INFORMADO);
                                    break;
                            }
                        }
                    }

                    if ((rst.getString("sexo") != null)
                            && (!rst.getString("sexo").trim().isEmpty())) {

                        if ("M".equals(rst.getString("sexo").trim())) {
                            imp.setSexo(TipoSexo.MASCULINO);
                        } else {
                            imp.setSexo(TipoSexo.FEMININO);
                        }
                    }

                    result.add(imp);
                }
            }

            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	c.idcliente as id,\n"
                    + "	cj.razaosocial as razao,\n"
                    + "	cj.nomefantasia as fantasia,\n"
                    + "	cj.ie,\n"
                    + "	cj.cnpj,\n"
                    + "	cj.im,\n"
                    + "	cj.responsavel_nome,\n"
                    + "	cj.responsavel_dddcelular,\n"
                    + "	cj.responsavel_celular,\n"
                    + "	cj.responsavel_email,\n"
                    + "	cj.responsavel_nascimento,\n"
                    + "	cj.responsavel_cpf,\n"
                    + "	cj.responsavel_rg,\n"
                    + "	ce.logradouro as endereco,\n"
                    + "	ce.numero,\n"
                    + "	ce.complemento,\n"
                    + "	ce.bairro,\n"
                    + "	ce.cidade,\n"
                    + "	ce.uf,\n"
                    + "	ce.cep,	\n"
                    + "	ce.cobranca_logradouro as endereco_cobranca,\n"
                    + "	ce.cobranca_numero as numero_cobranca,\n"
                    + "	ce.cobranca_complemento as complemento_cobranca,\n"
                    + "	ce.cobranca_bairro as bairro_cobranca,\n"
                    + "	ce.cobranca_cidade as cidade_cobranca,\n"
                    + "	ce.cobranca_uf as uf_cobranca,\n"
                    + "	ce.cobranca_cep as cep_cobranca,\n"
                    + "	c.datacadastro,\n"
                    + "	c.statuscliente,\n"
                    + "	c.bloqueaprazo,\n"
                    + "	c.emailcliente,\n"
                    + "	c.limitecredito,\n"
                    + "	c.telefoneempresa\n"
                    + "from cliente c\n"
                    + "join cliente_juridico cj on cj.idcliente = c.idcliente\n"
                    + "left join cliente_endereco ce on ce.idcliente = c.idcliente\n"
                    + "order by 2"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setInscricaoestadual(rst.getString("ie"));
                    imp.setInscricaoMunicipal(rst.getString("im"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setCobrancaEndereco(rst.getString("endereco_cobranca"));
                    imp.setCobrancaNumero(rst.getString("numero_cobranca"));
                    imp.setCobrancaComplemento(rst.getString("complemento_cobranca"));
                    imp.setCobrancaBairro(rst.getString("bairro_cobranca"));
                    imp.setCobrancaMunicipio(rst.getString("cidade_cobranca"));
                    imp.setCobrancaUf(rst.getString("uf_cobranca"));
                    imp.setCobrancaCep(rst.getString("cep_cobranca"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    //imp.setDataNascimento(rst.getDate("datanascimento"));
                    imp.setValorLimite(rst.getDouble("limitecredito"));
                    imp.setAtivo(rst.getBoolean("statuscliente"));
                    imp.setBloqueado(rst.getBoolean("bloqueaprazo"));
                    imp.setEmail(rst.getString("emailcliente"));
                    imp.setTelefone(rst.getString("telefoneempresa"));

                    /*if ((rst.getString("estadocivil") != null)
                            && (!rst.getString("estadocivil").trim().isEmpty())) {

                        if (null == rst.getString("estadocivil").trim()) {
                            imp.setEstadoCivil(TipoEstadoCivil.NAO_INFORMADO);
                        } else {
                            switch (rst.getString("estadocivil").trim()) {
                                case "S":
                                    imp.setEstadoCivil(TipoEstadoCivil.SOLTEIRO);
                                    break;
                                case "C":
                                    imp.setEstadoCivil(TipoEstadoCivil.CASADO);
                                    break;
                                default:
                                    imp.setEstadoCivil(TipoEstadoCivil.NAO_INFORMADO);
                                    break;
                            }
                        }
                    }

                    if ((rst.getString("sexo") != null)
                            && (!rst.getString("sexo").trim().isEmpty())) {

                        if ("M".equals(rst.getString("sexo").trim())) {
                            imp.setSexo(TipoSexo.MASCULINO);
                        } else {
                            imp.setSexo(TipoSexo.FEMININO);
                        }
                    }*/
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select  \n"
                    + "	idcontareceber as id,\n"
                    + "	idcliente,\n"
                    + "	numerotitulo as numerocupom,\n"
                    + "	dataemissao,\n"
                    + "	datavencimento, \n"
                    + "	valor\n"
                    + "from conta_receber \n"
                    + "where statusconta in ('VEN', 'AVC')"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("id"));
                    imp.setIdCliente(rst.getString("idcliente"));
                    imp.setDataEmissao(rst.getDate("dataemissao"));
                    imp.setDataVencimento(rst.getDate("datavencimento"));
                    imp.setValor(rst.getDouble("valor"));

                    if ((rst.getString("numerocupom") != null)
                            && (!rst.getString("numerocupom").trim().isEmpty())
                            && (rst.getString("numerocupom").contains("-"))) {

                        imp.setNumeroCupom(rst.getString("numerocupom").substring(0, rst.getString("numerocupom").indexOf("-")));
                    }

                    result.add(imp);
                }
            }
        }
        return result;
    }
}
