package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.joda.time.Interval;
import vrframework.classe.ProgressBar;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.parametro.Parametros;
import vrimplantacao2.vo.cadastro.mercadologico.MercadologicoNivelIMP;
import vrimplantacao2.vo.cadastro.oferta.SituacaoOferta;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorContatoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.OfertaIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Leandro
 */
public class SigmaDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                new OpcaoProduto[]{
                    OpcaoProduto.MERCADOLOGICO_POR_NIVEL,
                    OpcaoProduto.MERCADOLOGICO_POR_NIVEL_REPLICAR,
                    OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                    OpcaoProduto.MERCADOLOGICO_PRODUTO,
                    OpcaoProduto.FAMILIA,
                    OpcaoProduto.FAMILIA_PRODUTO,
                    OpcaoProduto.PRODUTOS,
                    OpcaoProduto.DATA_CADASTRO,
                    OpcaoProduto.EAN,
                    OpcaoProduto.EAN_EM_BRANCO,
                    OpcaoProduto.TIPO_EMBALAGEM_EAN,
                    OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                    OpcaoProduto.VALIDADE,
                    OpcaoProduto.DESC_COMPLETA,
                    OpcaoProduto.DESC_GONDOLA,
                    OpcaoProduto.DESC_REDUZIDA,
                    OpcaoProduto.PESO_BRUTO,
                    OpcaoProduto.PESO_LIQUIDO,
                    OpcaoProduto.ESTOQUE_MAXIMO,
                    OpcaoProduto.ESTOQUE_MINIMO,
                    OpcaoProduto.ESTOQUE,
                    OpcaoProduto.MARGEM,
                    OpcaoProduto.CUSTO_SEM_IMPOSTO,
                    OpcaoProduto.CUSTO_COM_IMPOSTO,
                    OpcaoProduto.PRECO,
                    OpcaoProduto.ATIVO,
                    OpcaoProduto.NCM,
                    OpcaoProduto.CEST,
                    OpcaoProduto.PIS_COFINS,
                    OpcaoProduto.NATUREZA_RECEITA,
                    OpcaoProduto.ICMS,
                    OpcaoProduto.NUTRICIONAL,
                    OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                    OpcaoProduto.OFERTA,
                    OpcaoProduto.MAPA_TRIBUTACAO
                }
        ));
    }

    @Override
    public List<MercadologicoNivelIMP> getMercadologicoPorNivel() throws Exception {
        Map<String, MercadologicoNivelIMP> n1 = new LinkedHashMap<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {

            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    cod_grupo,\n"
                    + "    descricao\n"
                    + "from\n"
                    + "    grupo\n"
                    + "order by\n"
                    + "    1"
            )) {
                while (rst.next()) {
                    n1.put(
                            rst.getString("cod_grupo"),
                            new MercadologicoNivelIMP(
                                    rst.getString("cod_grupo"),
                                    rst.getString("descricao")
                            )
                    );
                }
            }
            try (ResultSet rst = stm.executeQuery(
                    "select distinct\n"
                    + "    p.cod_grupo mercadologico1,\n"
                    + "    g.descricao desc_mercadologico1,\n"
                    + "    coalesce(p.cod_subgrupo, '') mercadologico2,\n"
                    + "    sg.descricao desc_mercadologico2\n"
                    + "from\n"
                    + "    PRODUTO p\n"
                    + "    join grupo g on\n"
                    + "        p.cod_grupo = g.cod_grupo\n"
                    + "    join grupo_sub sg on\n"
                    + "        p.cod_subgrupo = sg.cod_gruposub\n"
                    + "union\n"
                    + "select\n"
                    + "    g.cod_grupo mercadologico1,\n"
                    + "    g.descricao desc_mercadologico1,\n"
                    + "    sb.cod_gruposub mercadologico2,\n"
                    + "    sb.descricao desc_mercadologico2\n"
                    + "from\n"
                    + "    grupo g\n"
                    + "    join grupo_sub sb on\n"
                    + "        sb.cod_grupo = g.cod_grupo\n"
                    + "order by\n"
                    + "    1, 3"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP imp = n1.get(rst.getString("mercadologico1"));

                    if (!"".equals(rst.getString("mercadologico2"))) {
                        imp.addFilho(
                                rst.getString("mercadologico2"),
                                rst.getString("desc_mercadologico2")
                        );
                    }
                }
            }
        }

        return new ArrayList<>(n1.values());
    }

    @Override
    public List<OfertaIMP> getOfertas(Date datatermino) throws Exception {
        List<OfertaIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    i.cod_produto,\n"
                    + "    i.preco_venda,\n"
                    + "    i.preco_promocional,\n"
                    + "    o.data_inicial,\n"
                    + "    o.data_final\n"
                    + "from promocoes_produto i\n"
                    + "join promocoes o\n"
                    + "    on o.cod_promocao = i.cod_campanha"
            //+ " and o.data_final >= 'now'"
            )) {
                while (rst.next()) {
                    OfertaIMP imp = new OfertaIMP();
                    imp.setIdProduto(rst.getString("cod_produto"));
                    imp.setDataInicio(rst.getDate("data_inicial"));
                    imp.setDataFim(rst.getDate("data_final"));
                    imp.setPrecoNormal(rst.getDouble("preco_venda"));
                    imp.setPrecoOferta(rst.getDouble("preco_promocional"));
                    imp.setSituacaoOferta(SituacaoOferta.ATIVO);
                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    f.cod_familia,\n"
                    + "    f.descricao\n"
                    + "from\n"
                    + "    familia f\n"
                    + "order by\n"
                    + "    f.cod_familia,\n"
                    + "    f.descricao"
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("cod_familia"));
                    imp.setDescricao(rst.getString("descricao"));
                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        int cont1 = 0, cont2 = 0;
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    p.cod_produto id,   \n"
                    + "    p.data_cadastro datacadastro,\n"
                    + "    p.ean,\n"
                    + "    1 as qtdEmbalagem,\n"
                    + "    un.unidade tipoEmbalagem,\n"
                    + "    case un.unidade when 'KG' then 'S' else 'N' end eBalanca,\n"
                    + "    coalesce(p.val_balanca, 0) validade,\n"
                    + "    p.descricao descricaoCompleta,\n"
                    + "    coalesce(p.descricao_abreviada, p.descricao) descricaoReduzida,\n"
                    + "    p.descricao descricaoGondola,\n"
                    + "    g.cod_grupo codMercadologico1,\n"
                    + "    sg.cod_gruposub codMercadologico2,\n"
                    + "    p.cod_familia idFamiliaProduto,\n"
                    + "    p.peso_bruto pesoBruto,\n"
                    + "    p.peso_liquido pesoLiquido, \n"
                    + "    p.estoque_maximo estoqueMaximo,\n"
                    + "    p.estoque_minimo estoqueMinimo,\n"
                    + "    est.saldo_atual estoque,\n"
                    + "    p.margem_1 margem,\n"
                    + "    p.preco_reposicao custoSemImposto,\n"
                    + "    p.preco_custo custoComImposto,\n"
                    + "    p.valor_tabela_1 precovenda,\n"
                    + "    case when upper(p.situacao) = 'I' then 0 else 1 end situacaoCadastro,\n"
                    + "    p.conta_ncm ncm,\n"
                    + "    p.cest,\n"
                    + "    pis_deb.cst piscofinscstdebito,\n"
                    + "    pis_cred.cst piscofinscstcredito,\n"
                    + "    null as piscofinsNaturezaReceita,\n"
                    + "    icms.cod_classificacao id_icms,\n"                            
                    + "    icms.cod_classificacao icms_cst,\n"
                    + "    icms.aliq_icms_i icms_aliq,\n"
                    + "    0 icms_reducao\n"
                    + "from\n"
                    + "    produto p\n"
                    + "    left join unidade_medida un on\n"
                    + "        p.cod_unidade = un.cod_unidade\n"
                    + "    left join vestoque /*OU SD_ESTOQUE*/ est on\n"
                    + "        p.cod_produto = est.cod_produto and\n"
                    + "        est.cod_empresa = " + getLojaOrigem().split("-")[0] + "\n"
                    + "    left join cst_pis_saida pis_deb on\n"
                    + "        p.cod_tp_aliq_piscofins = pis_deb.codigo\n"
                    + "    left join cst_pis_entrada pis_cred on\n"
                    + "        p.cod_tp_aliq_piscofins = pis_cred.codigo\n"
                    + "    left join classificacao_fiscal icms on\n"
                    + "        p.cod_classificacao = icms.cod_classificacao\n"
                    + "    left join grupo g on\n"
                    + "        p.cod_grupo = g.cod_grupo\n"
                    + "    left join grupo_sub sg on\n"
                    + "        p.cod_subgrupo = sg.cod_gruposub\n"
                    + "order by\n"
                    + "    p.cod_produto"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setEan(rst.getString("ean"));
                    imp.setQtdEmbalagem(rst.getInt("qtdEmbalagem"));
                    imp.setTipoEmbalagem(rst.getString("tipoEmbalagem"));
                    imp.seteBalanca(!"N".equals(rst.getString("eBalanca")));
                    imp.setValidade(rst.getInt("validade"));
                    imp.setCodMercadologico1(rst.getString("codMercadologico1"));
                    imp.setCodMercadologico2(rst.getString("codMercadologico2"));
                    imp.setDescricaoCompleta(rst.getString("descricaoCompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoReduzida"));
                    imp.setDescricaoGondola(rst.getString("descricaoGondola"));
                    imp.setIdFamiliaProduto(rst.getString("idFamiliaProduto"));
                    imp.setPesoBruto(rst.getDouble("pesoBruto"));
                    imp.setPesoLiquido(rst.getDouble("pesoLiquido"));
                    imp.setEstoqueMaximo(rst.getDouble("estoqueMaximo"));
                    imp.setEstoqueMinimo(rst.getDouble("estoqueMinimo"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setCustoSemImposto(rst.getDouble("custoSemImposto"));
                    imp.setCustoComImposto(rst.getDouble("custoSemImposto"));
                    //imp.setCustoComImposto(rst.getDouble("custoComImposto"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setSituacaoCadastro(SituacaoCadastro.getById(rst.getInt("situacaoCadastro")));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getInt("piscofinscstdebito"));
                    imp.setPiscofinsCstCredito(rst.getInt("piscofinscstcredito"));
                    imp.setPiscofinsNaturezaReceita(rst.getInt("piscofinsNaturezaReceita"));
                    imp.setIcmsDebitoId(rst.getString("id_icms"));
                    imp.setIcmsDebitoForaEstadoId(rst.getString("id_icms"));
                    imp.setIcmsDebitoForaEstadoNfId(rst.getString("id_icms"));
                    imp.setIcmsCreditoId(rst.getString("id_icms"));
                    imp.setIcmsCreditoForaEstadoId(rst.getString("id_icms"));
                    imp.setIcmsCreditoId(rst.getString("id_icms"));
                    imp.setIcmsConsumidorId(rst.getString("id_icms"));
                    imp.setManterEAN(Utils.stringToLong(imp.getEan()) <= 999999);

                    result.add(imp);

                    cont1++;
                    cont2++;

                    if (cont2 >= 1000) {
                        cont2 = 0;
                        ProgressBar.setStatus("Carregando os produtos..." + cont1);
                    }

                }
            }
        }

        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    c.cod_cliente id,\n"
                    + "    c.razao_social razao,\n"
                    + "    coalesce(c.fantasia, c.razao_social) fantasia,\n"
                    + "    c.cnpj_cpf cnpj,\n"
                    + "    c.insc_estadual ie_rg,\n"
                    + "    c.insc_municipal,\n"
                    + "    case coalesce(c.situacao,'L') when 'L' then 1 else 0 end ativo,\n"
                    + "\n"
                    + "    c.logradouro endereco,\n"
                    + "    c.numero,\n"
                    + "    c.complemento,\n"
                    + "    c.bairro,\n"
                    + "    c.ibge ibge_municipio,\n"
                    + "    c.cep,\n"
                    + "\n"
                    + "    c.logradouro cob_endereco,\n"
                    + "    c.numero cob_numero,\n"
                    + "    c.complemento cob_complemento,\n"
                    + "    c.bairro cob_bairro,\n"
                    + "    c.ibge cob_ibge_municipio,\n"
                    + "    c.cep cob_cep,\n"
                    + "\n"
                    + "    c.fone_1 tel_principal,\n"
                    + "    c.data_cadastro,\n"
                    + "    c.observacao,\n"
                    + "\n"
                    + "    coalesce(trim(c.fone_2),'') fone_2,\n"
                    + "    coalesce(trim(c.celular),'') celular,\n"
                    + "    coalesce(trim(c.fax),'') fax,\n"
                    + "    coalesce(trim(c.email),'') email\n"
                    + "from\n"
                    + "    cliente c\n"
                    + "where\n"
                    + "    (c.tipocad = 'F' or c.tipocad is null or\n"
                    + "    c.cnpj_cpf in (select distinct cnpj_cpf from cdprodfor)) and\n"
                    + "    not c.razao_social is null\n"
                    + "order by\n"
                    + "    c.cod_cliente"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("ie_rg"));
                    imp.setInsc_municipal(rst.getString("insc_municipal"));
                    imp.setAtivo(rst.getBoolean("ativo"));

                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setIbge_municipio(rst.getInt("ibge_municipio"));
                    imp.setCep(rst.getString("cep"));

                    imp.setCob_endereco(rst.getString("cob_endereco"));
                    imp.setCob_numero(rst.getString("cob_numero"));
                    imp.setCob_complemento(rst.getString("cob_complemento"));
                    imp.setCob_bairro(rst.getString("cob_bairro"));
                    imp.setCob_ibge_municipio(rst.getInt("cob_ibge_municipio"));
                    imp.setCob_cep(rst.getString("cob_cep"));

                    imp.setTel_principal(rst.getString("tel_principal"));
                    imp.setDatacadastro(rst.getDate("data_cadastro"));
                    imp.setObservacao(rst.getString("observacao"));

                    String fone2 = rst.getString("fone_2");
                    if (!"".equals(fone2)) {
                        FornecedorContatoIMP cont = imp.getContatos().make("1");
                        cont.setImportId("1");
                        cont.setNome("FONE 2");
                        cont.setTipoContato(TipoContato.COMERCIAL);
                        cont.setTelefone(fone2);
                    }

                    String celular = rst.getString("celular");
                    if (!"".equals(celular)) {
                        FornecedorContatoIMP cont = imp.getContatos().make("2");
                        cont.setImportId("2");
                        cont.setNome("CELULAR");
                        cont.setTipoContato(TipoContato.COMERCIAL);
                        cont.setCelular(celular);
                    }

                    String fax = rst.getString("fax");
                    if (!"".equals(fax)) {
                        FornecedorContatoIMP cont = imp.getContatos().make("3");
                        cont.setImportId("3");
                        cont.setNome("FAX");
                        cont.setTipoContato(TipoContato.COMERCIAL);
                        cont.setTelefone(fax);
                    }

                    String email = rst.getString("email");
                    if (!"".equals(email)) {
                        FornecedorContatoIMP cont = imp.getContatos().make("4");
                        cont.setImportId("4");
                        cont.setNome("EMAIL");
                        cont.setTipoContato(TipoContato.COMERCIAL);
                        cont.setEmail(email);
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
                    + "    f.cod_cliente id_fornecedor,\n"
                    + "    p.cod_produto id_produto,\n"
                    + "    pf.cdprod codigoexterno\n"
                    + "from\n"
                    + "    cdprodfor pf\n"
                    + "    join cliente f on\n"
                    + "        f.cnpj_cpf = pf.cnpj_cpf\n"
                    + "    join produto p on\n"
                    + "        p.ean = pf.ean"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setIdFornecedor(rst.getString("id_fornecedor"));
                    imp.setIdProduto(rst.getString("id_produto"));
                    imp.setCodigoExterno(rst.getString("codigoexterno"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ClienteIMP> getClientesPreferenciais() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    c.cod_cliente id,\n" +
                    "    c.razao_social nome,\n" +
                    "    c.logradouro res_endereco,\n" +
                    "    c.numero res_numero,\n" +
                    "    c.complemento res_complemento,\n" +
                    "    c.bairro res_bairro,\n" +
                    "    c.municipio res_municipio,\n" +
                    "    c.uf res_uf,\n" +
                    "    c.cep res_cep,\n" +
                    "    c.fone_1 fone1,\n" +
                    "    trim(coalesce(c.fone_2,'')) fone2,\n" +
                    "    c.celular,\n" +
                    "    c.insc_estadual inscricaoestadual,\n" +
                    "    c.cnpj_cpf cnpj,\n" +
                    "    1 sexo,\n" +
                    "    c.dias_carencia prazodias,\n" +
                    "    c.email,\n" +
                    "    c.data_cadastro datacadastro,\n" +
                    "    c.limite_credito limite,\n" +
                    "    case c.situacao when 'B' then 1 else 0 end bloqueado,\n" +
                    "    c.obs observacao,\n" +
                    "    c.data_nascimento datanascimento,\n" +
                    "    null nomePai,\n" +
                    "    null nomeMae,\n" +
                    "    null empresa,\n" +
                    "    null telEmpresa,\n" +
                    "    null cargo,\n" +
                    "    0 salario,\n" +
                    "    0 estadoCivil,\n" +
                    "    null conjuge,\n" +
                    "    c.orgao orgaoemissor\n" +
                    "from\n" +
                    "    cliente c\n" +
                    "where\n" +
                    "    not c.razao_social is null\n" +
                    "order by\n" +
                    "    c.cod_cliente"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rst.getString("id"));
                    imp.setRazao(rst.getString("nome"));
                    imp.setFantasia(rst.getString("nome"));
                    imp.setEndereco(rst.getString("res_endereco"));
                    imp.setNumero(rst.getString("res_numero"));
                    imp.setComplemento(rst.getString("res_complemento"));
                    imp.setBairro(rst.getString("res_bairro"));
                    imp.setMunicipio(rst.getString("res_municipio"));
                    imp.setUf(rst.getString("res_uf"));
                    imp.setCep(rst.getString("res_cep"));
                    imp.setTelefone(rst.getString("fone1"));
                    if (!"".equals(rst.getString("fone2"))) {
                        imp.addContato("1", "FONE2", rst.getString("fone2"), "", "");
                    }
                    imp.setCelular(rst.getString("celular"));
                    imp.setInscricaoestadual(rst.getString("inscricaoestadual"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setPrazoPagamento(rst.getInt("prazodias"));
                    imp.setEmail(rst.getString("email"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setValorLimite(rst.getDouble("limite"));
                    imp.setBloqueado(rst.getBoolean("bloqueado"));
                    imp.setObservacao(rst.getString("observacao"));
                    imp.setDataNascimento(rst.getDate("datanascimento"));
                    imp.setOrgaoemissor(rst.getString("orgaoemissor"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();

        int cont = 0;
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    r.codigo id,\n" +
                    "    r.codcliente id_clientepreferencial,\n" +
                    "    c.cnpj_cpf cnpj,\n" +
                    "    r.dataemissao emissao,\n" +
                    "    r.historico,\n" +
                    "    r.saldo valor,\n" +
                    "    r.JUROS,\n" +
                    "    r.MORA,\n" +
                    "    r.datavencimento venc,\n" +
                    "    r.datapagamento datapag,\n" +
                    "    r.valorrecebido,\n" +
                    "    r.documento cupom\n" +
                    "from\n" +
                    "    receber r\n" +
                    "    join cliente c on r.codcliente = c.cod_cliente\n" +
                    "where\n" +
                    "    r.saldo > 0"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    
                    imp.setId(rst.getString("id"));
                    imp.setIdCliente(rst.getString("id_clientepreferencial"));
                    imp.setCnpjCliente(rst.getString("cnpj"));
                    imp.setDataEmissao(rst.getDate("emissao"));
                    imp.setNumeroCupom(rst.getString("cupom"));
                    imp.setValor(rst.getDouble("valor"));
                    long venc = rst.getDate("venc").getTime();
                    long now = new Date().getTime();
                    if (now > venc) {
                        Interval i = new Interval(venc, now);
                        imp.setJuros((rst.getDouble("valor") * (rst.getDouble("mora") / 100)) * i.toDuration().getStandardDays());
                    }
                    imp.setDataVencimento(rst.getDate("venc"));
                    imp.setObservacao(rst.getString("historico"));

                    result.add(imp);

                    cont++;
                    ProgressBar.setStatus("Carregando créditorotativo..." + cont);
                }
            }
        }

        return result;
    }

    @Override
    public String getSistema() {
        return "Sigma";
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    e.cod_empresa,\n"
                    + "    e.razao_social\n"
                    + "from\n"
                    + "    empresa e\n"
                    + "order by\n"
                    + "    e.cod_empresa"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("cod_empresa"), rst.getString("razao_social")));
                }
            }
        }
        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "    cf.cod_classificacao as id,\n"
                    + "    cf.aliq_icms_i as aliquota,\n"
                    + "    cf.base_icms_i as reducao,\n"
                    + "    cf.descricao as descricao\n"
                    + "from classificacao_fiscal cf\n"
                    + "where cf.uf = '" + Parametros.get().getUfPadraoV2().getSigla() + "'\n"
                    + "order by cf.cod_classificacao"
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(
                            rs.getString("id"),
                            rs.getString("descricao"),
                            rs.getInt("id"),
                            rs.getDouble("aliquota"),
                            rs.getDouble("reducao")));
                }
            }
        }
        return result;
    }

}
