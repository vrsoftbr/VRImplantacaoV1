package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.mercadologico.MercadologicoNivelIMP;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Leandro
 */
public class ZpfDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "Zpf";
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select e.codigo, e.codigo||' - '||e.razao_social descricao from empresa e order by e.codigo"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("codigo"), rst.getString("descricao")));
                }
            }
        }

        return result;
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_POR_NIVEL,
                OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.FAMILIA,
                OpcaoProduto.FAMILIA_PRODUTO,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.IMPORTAR_RESETAR_BALANCA,
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.DATA_ALTERACAO,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.ESTOQUE_MAXIMO,
                OpcaoProduto.ESTOQUE_MINIMO,
                OpcaoProduto.CUSTO,
                OpcaoProduto.PRECO,
                OpcaoProduto.ATIVO,
                OpcaoProduto.DESCONTINUADO,
                OpcaoProduto.NCM,
                OpcaoProduto.CEST,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.NATUREZA_RECEITA,
                OpcaoProduto.ICMS,
                OpcaoProduto.MAPA_TRIBUTACAO
        ));
    }

    @Override
    public List<MercadologicoNivelIMP> getMercadologicoPorNivel() throws Exception {
        List<MercadologicoNivelIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select codigo, descricao from grupo order by 1"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP imp = new MercadologicoNivelIMP();
                    imp.setId(rst.getString("codigo"));
                    imp.setDescricao(rst.getString("descricao"));

                    addMercadologicoNivel2(imp);

                    result.add(imp);
                }
            }
        }

        return result;
    }

    private void addMercadologicoNivel2(MercadologicoNivelIMP imp) throws Exception {
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select codigo, descricao from subgrupo where grupo = " + imp.getId() + " order by 1"
            )) {
                while (rst.next()) {
                    imp.addFilho(rst.getString("codigo"), rst.getString("descricao"));
                }
            }
        }
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    codigo,\n"
                    + "    descricao\n"
                    + "from\n"
                    + "    linha\n"
                    + "order by\n"
                    + "    codigo"
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("codigo"));
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

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    p.codigo id,\n"
                    + "    p.data_inclusao datacadastro,\n"
                    + "    p.data_ultima_alteracao dataalteracao,\n"
                    + "    case upper(p.balanca) when 'S' then p.codbalanca else coalesce(ean.codigo_barra, p.codbalanca) end ean,\n"
                    + "    case when coalesce(p.qtde_caixa, 1) <= 0 then 1 else coalesce(p.qtde_caixa, 1) end qtdembalagemcotacao,\n"
                    + "    p.linha,\n"
                    + "    p.unidade,\n"
                    + "    p.balanca,\n"
                    + "    coalesce(p.validade, 0) validade,\n"
                    + "    p.descricao descricaocompleta,\n"
                    + "    p.grupo merc1,\n"
                    + "    p.subgrupo merc2,\n"
                    + "    p.peso_bruto,\n"
                    + "    p.peso_liquido,\n"
                    + "    p.estoque_maximo,\n"
                    + "    p.estoque_minimo,\n"
                    + "    p.estoque_atual,\n"
                    + "    p.valor_custo custosemimposto,\n"
                    + "    p.valor_venda precovenda,\n"
                    + "    case p.fora_linha when 'S' then 0 else 1 end situacaocadastro,\n"
                    + "    case p.inativo when 'S' then 1 else 0 end descontinuado,\n"
                    + "    p.classificacao_fiscal ncm,\n"
                    + "    cest.cest,\n"
                    + "    p.cst_pis_cf piscofins_cst_saida,\n"
                    + "    coalesce(sg.nat_rec, g.nat_rec) piscofins_natrec,\n"
                    + "    p.tributacao id_icms\n"
                    + "from\n"
                    + "    produtos p\n"
                    + "    left join cod_barras ean on\n"
                    + "        p.codigo = ean.produto\n"
                    + "    left join cest on\n"
                    + "        p.id_cest = cest.codigo\n"
                    + "    left join grupo g on\n"
                    + "        p.grupo = g.codigo\n"
                    + "    left join subgrupo sg on\n"
                    + "        p.subgrupo = sg.codigo\n"
                    + "order by\n"
                    + "    p.codigo"
            )) {
                while (rst.next()) {

                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setDataAlteracao(rst.getDate("dataalteracao"));
                    imp.setEan(rst.getString("ean"));
                    imp.setQtdEmbalagemCotacao(rst.getInt("qtdembalagemcotacao"));
                    imp.setTipoEmbalagem(rst.getString("unidade"));
                    imp.seteBalanca("S".equals(rst.getString("balanca")));
                    imp.setValidade(rst.getInt("validade"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoGondola(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaocompleta"));
                    imp.setCodMercadologico1(rst.getString("merc1"));
                    imp.setCodMercadologico2(rst.getString("merc2"));
                    imp.setIdFamiliaProduto(rst.getString("linha"));
                    imp.setPesoBruto(rst.getDouble("peso_bruto"));
                    imp.setPesoLiquido(rst.getDouble("peso_liquido"));
                    imp.setEstoqueMaximo(rst.getDouble("estoque_maximo"));
                    imp.setEstoqueMinimo(rst.getDouble("estoque_minimo"));
                    imp.setEstoque(rst.getDouble("estoque_atual"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setCustoComImposto(rst.getDouble("custosemimposto"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setSituacaoCadastro(rst.getInt("situacaocadastro"));
                    imp.setDescontinuado(rst.getBoolean("descontinuado"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getString("piscofins_cst_saida"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("piscofins_natrec"));
                    imp.setIcmsDebitoId(rst.getString("id_icms"));
                    imp.setIcmsCreditoId(rst.getString("id_icms"));

                    result.add(imp);

                }
            }
        }

        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    codigo,\n"
                    + "    descricao,\n"
                    + "    porc_icms\n"
                    + "from\n"
                    + "    tributacao\n"
                    + "order by\n"
                    + "    codigo"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(
                            rst.getString("codigo"),
                            rst.getString("descricao"),
                            0,
                            rst.getDouble("porc_icms"),
                            0
                    ));
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
                    + "    f.codigo id,\n"
                    + "    f.razao_social razao,  \n"
                    + "    f.nome_fantasia fantasia,\n"
                    + "    f.cnpj,                  \n"
                    + "    f.inscricao_estadual ie,  \n"
                    + "    f.inscricao_suframa suframa,  \n"
                    + "    f.inscricao_municipal,  \n"
                    + "    case coalesce(upper(f.inativo),'N') when 'S' then 0 else 1 end situacaocadastro,\n"
                    + "    f.endereco,\n"
                    + "    f.numero,   \n"
                    + "    f.complemento,\n"
                    + "    f.bairro,\n"
                    + "    cd.cod_municipio municipio_ibge,\n"
                    + "    cd.cod_estado estado_ibge,\n"
                    + "    f.cep,  \n"
                    + "    f.fone,\n"
                    + "    f.contato,\n"
                    + "    f.fax,\n"
                    + "    f.email,\n"
                    + "    f.celular,\n"
                    + "    f.data_inclusao datacadastro,\n"
                    + "    f.data_ultima_alteracao dataalteracao,\n"
                    + "    f.observacao\n"
                    + "from\n"
                    + "    fornecedores f\n"
                    + "    left join cidades cd on f.cidade = cd.codigo\n"
                    + "order by\n"
                    + "    f.codigo"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("ie"));
                    imp.setSuframa(rst.getString("suframa"));
                    imp.setInsc_municipal(rst.getString("inscricao_municipal"));
                    imp.setAtivo(rst.getBoolean("situacaocadastro"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setIbge_municipio(rst.getInt("municipio_ibge"));
                    imp.setIbge_uf(rst.getInt("estado_ibge"));
                    imp.setCep(rst.getString("cep"));
                    imp.setTel_principal(rst.getString("fone"));
                    imp.addTelefone("FAX", rst.getString("fax"));
                    imp.addEmail("E-MAIL", rst.getString("email"), TipoContato.COMERCIAL);
                    imp.addCelular("CELULAR", rst.getString("celular"));
                    imp.setDatacadastro(rst.getDate("datacadastro"));
                    imp.setObservacao(rst.getString("observacao"));

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
                    + "fornecedor,\n"
                    + "produto,\n"
                    + "codfornecedor\n"
                    + "from produtos_fornecedores\n"
                    + "order by fornecedor,  produto"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rst.getString("produto"));
                    imp.setIdFornecedor(rst.getString("fornecedor"));
                    imp.setCodigoExterno(rst.getString("codfornecedor"));
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
                    + "    c.codigo id,\n"
                    + "    c.cpf_cnpj cnpj,\n"
                    + "    coalesce(c.inscricao_estadual, c.rg) inscricao_estadual,\n"
                    + "    c.nome_razao_social razao,\n"
                    + "    coalesce(c.nome_fantasia, c.nome_razao_social) fantasia,\n"
                    + "    c.inativo,\n"
                    + "    c.bloqueado,\n"
                    + "    c.endereco,\n"
                    + "    c.numero,\n"
                    + "    c.complemento,\n"
                    + "    c.bairro,\n"
                    + "    cd.cod_municipio ibge_municipio,\n"
                    + "    c.cep,\n"
                    + "    c.estado_civil,\n"
                    + "    c.data_nascimento,\n"
                    + "    c.data_cadastro,\n"
                    + "    c.local_trabalho,\n"
                    + "    c.endereco_trabalho,\n"
                    + "    cdtrb.cod_municipio ibge_municipio_trabalho,\n"
                    + "    c.telefone_trabalho empresa_telefone,\n"
                    + "    c.data_admissao,\n"
                    + "    c.simples_nacional,\n"
                    + "    c.produtor_rural,\n"
                    + "    c.profissao,\n"
                    + "    c.renda_mensal salario,\n"
                    + "    c.limite_credito,\n"
                    + "    c.limite_utilizado,\n"
                    + "    c.nome_pai,\n"
                    + "    c.nome_mae,\n"
                    + "    c.pontuacao_inicial,\n"
                    + "    c.fone,\n"
                    + "    c.fax,\n"
                    + "    c.endereco_cobranca,\n"
                    + "    c.numero_cobranca,\n"
                    + "    c.complemento_cobranca,\n"
                    + "    c.bairro_cobranca,\n"
                    + "    cdcob.cod_municipio ibge_municipio_cobranca,\n"
                    + "    c.cep_cobranca,\n"
                    + "    c.inscricao_municipal\n"
                    + "from\n"
                    + "    clientes c\n"
                    + "    left join cidades cd on\n"
                    + "        c.cidade = cd.codigo \n"
                    + "    left join cidades cdtrb on\n"
                    + "        c.cidade_trabalho = cdtrb.codigo \n"
                    + "    left join cidades cdcob on\n"
                    + "        c.cidade_cobranca = cdcob.codigo\n"
                    + "order by\n"
                    + "    c.codigo"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rst.getString("id"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setInscricaoestadual(rst.getString("inscricao_estadual"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setAtivo(!"S".equals(rst.getString("inativo")));
                    imp.setBloqueado("S".equals(rst.getString("bloqueado")));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipioIBGE(rst.getInt("ibge_municipio"));
                    imp.setCep(rst.getString("cep"));
                    imp.setEstadoCivil(rst.getString("estado_civil"));
                    imp.setDataNascimento(rst.getDate("data_nascimento"));
                    imp.setDataCadastro(rst.getDate("data_cadastro"));
                    imp.setEmpresa(rst.getString("local_trabalho"));
                    imp.setEmpresaEndereco(rst.getString("endereco_trabalho"));
                    imp.setEmpresaMunicipioIBGE(rst.getInt("ibge_municipio_trabalho"));
                    imp.setEmpresaTelefone(rst.getString("empresa_telefone"));
                    imp.setDataAdmissao(rst.getDate("data_admissao"));
                    imp.setCargo(rst.getString("profissao"));
                    imp.setSalario(rst.getDouble("salario"));
                    imp.setNomePai(rst.getString("nome_pai"));
                    imp.setNomeMae(rst.getString("nome_mae"));
                    imp.setTelefone(rst.getString("fone"));
                    imp.setFax(rst.getString("fax"));
                    imp.setCobrancaEndereco(rst.getString("endereco_cobranca"));
                    imp.setCobrancaNumero(rst.getString("numero_cobranca"));
                    imp.setCobrancaComplemento(rst.getString("complemento_cobranca"));
                    imp.setCobrancaBairro(rst.getString("bairro_cobranca"));
                    imp.setCobrancaMunicipioIBGE(rst.getInt("ibge_municipio_cobranca"));
                    imp.setCobrancaCep(rst.getString("cep_cobranca"));
                    imp.setInscricaoMunicipal(rst.getString("inscricao_municipal"));
                    imp.setValorLimite(rst.getDouble("limite_credito"));

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
                    "Select \n"
                    + "    P.Empresa, \n"
                    + "    P.Documento, \n"
                    + "    P.Serie, \n"
                    + "    P.Tipo_MOvto,\n"
                    + "    r.data dataemissao,\n"
                    + "    P.Parcela, \n"
                    + "    P.Vencimento, \n"
                    + "    P.Valor,\n"
                    + "    coalesce(sum(b.valor_recebido+coalesce(b.Descontos,0)-coalesce(b.juros,0)),0) Valor_pago,\n"
                    + "    R.cliente, \n"
                    + "    c.cpf_cnpj,\n"
                    + "    C.fisica_juridica\n"
                    + "From\n"
                    + "    Contas_Receber R\n"
                    + "    Left Join Parcelas_Receber P on \n"
                    + "        (p.empresa=r.empresa and p.documento=r.documento and p.serie=r.serie and p.tipo_movto=r.tipo_movto)\n"
                    + "    Left Join baixas_parcelas_receber b on \n"
                    + "        (p.empresa = b.empresa and p.documento=b.documento and p.serie=b.serie and p.tipo_movto=b.tipo_movto and p.parcela=b.parcela)\n"
                    + "    INNER JOIN CLIENTES C ON (C.codigo = R.cliente)\n"
                    + "where\n"
                    + "    r.empresa = " + getLojaOrigem() + "\n"
                    + "Group by\n"
                    + "    P.Empresa, \n"
                    + "    P.Documento, \n"
                    + "    P.Serie, \n"
                    + "    P.Tipo_Movto,  \n"
                    + "    r.data,\n"
                    + "    P.Parcela, \n"
                    + "    P.Vencimento, \n"
                    + "    P.Valor, \n"
                    + "    R.cliente,        \n"
                    + "    c.cpf_cnpj,\n"
                    + "    C.fisica_juridica\n"
                    + "having\n"
                    + "    Round(coalesce(sum(b.valor_recebido+coalesce(b.Descontos,0)-coalesce(b.juros,0)),0),2) < Round(coalesce(p.valor,0),2)"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();

                    imp.setId(
                            rst.getString("Empresa") + "-"
                            + rst.getString("Documento") + "-"
                            + rst.getString("Serie") + "-"
                            + rst.getString("Tipo_MOvto") + "-"
                            + rst.getString("Parcela")
                    );
                    imp.setNumeroCupom(rst.getString("Documento"));
                    imp.setObservacao("SERIE: " + rst.getString("serie") + " DOCUMENTO: " + rst.getString("documento"));
                    imp.setDataEmissao(rst.getDate("dataemissao"));
                    imp.setParcela(rst.getInt("Parcela"));
                    imp.setDataVencimento(rst.getDate("Vencimento"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setIdCliente(rst.getString("cliente"));
                    imp.setCnpjCliente(rst.getString("cpf_cnpj"));
                    if (rst.getDouble("Valor_pago") > 0) {
                        imp.addPagamento(
                                rst.getString("Empresa") + "-"
                                + rst.getString("Documento") + "-"
                                + rst.getString("Serie") + "-"
                                + rst.getString("Tipo_MOvto"),
                                rst.getDouble("Valor_pago"),
                                0,
                                0,
                                rst.getDate("dataemissao"),
                                ""
                        );
                    }

                    result.add(imp);
                }
            }
        }

        return result;
    }

}
