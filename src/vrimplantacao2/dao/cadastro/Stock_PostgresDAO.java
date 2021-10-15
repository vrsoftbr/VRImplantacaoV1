package vrimplantacao2.dao.cadastro;

import vrimplantacao2.dao.interfaces.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import vrimplantacao.classe.ConexaoPostgres;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.OfertaIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

public class Stock_PostgresDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "Stock";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                new OpcaoProduto[]{
                    OpcaoProduto.FAMILIA,
                    OpcaoProduto.FAMILIA_PRODUTO,
                    OpcaoProduto.MERCADOLOGICO_PRODUTO,
                    OpcaoProduto.MERCADOLOGICO,
                    OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                    OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                    OpcaoProduto.PRODUTOS,
                    OpcaoProduto.EAN,
                    OpcaoProduto.EAN_EM_BRANCO,
                    OpcaoProduto.DATA_CADASTRO,
                    OpcaoProduto.TIPO_EMBALAGEM_EAN,
                    OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                    OpcaoProduto.PESAVEL,
                    OpcaoProduto.VALIDADE,
                    OpcaoProduto.DESC_COMPLETA,
                    OpcaoProduto.DESC_GONDOLA,
                    OpcaoProduto.DESC_REDUZIDA,
                    OpcaoProduto.ESTOQUE_MAXIMO,
                    OpcaoProduto.ESTOQUE_MINIMO,
                    OpcaoProduto.PRECO,
                    OpcaoProduto.CUSTO,
                    OpcaoProduto.ESTOQUE,
                    OpcaoProduto.ATIVO,
                    OpcaoProduto.NCM,
                    OpcaoProduto.CEST,
                    OpcaoProduto.PIS_COFINS,
                    OpcaoProduto.NATUREZA_RECEITA,
                    OpcaoProduto.ICMS,
                    OpcaoProduto.ICMS_SAIDA,
                    OpcaoProduto.ICMS_SAIDA_FORA_ESTADO,
                    OpcaoProduto.ICMS_SAIDA_NF,
                    OpcaoProduto.ICMS_ENTRADA,
                    OpcaoProduto.ICMS_CONSUMIDOR,
                    OpcaoProduto.ICMS_ENTRADA_FORA_ESTADO,
                    OpcaoProduto.PAUTA_FISCAL,
                    OpcaoProduto.PAUTA_FISCAL_PRODUTO,
                    OpcaoProduto.EXCECAO,
                    OpcaoProduto.MARGEM,
                    OpcaoProduto.MAPA_TRIBUTACAO,
                    OpcaoProduto.OFERTA
                }
        ));
    }

    @Override
    public Set<OpcaoFornecedor> getOpcoesDisponiveisFornecedor() {
        return new HashSet<>(Arrays.asList(
                OpcaoFornecedor.DADOS,
                OpcaoFornecedor.RAZAO_SOCIAL,
                OpcaoFornecedor.NOME_FANTASIA,
                OpcaoFornecedor.CNPJ_CPF,
                OpcaoFornecedor.INSCRICAO_ESTADUAL,
                OpcaoFornecedor.INSCRICAO_MUNICIPAL,
                OpcaoFornecedor.TELEFONE,
                OpcaoFornecedor.CEP,
                OpcaoFornecedor.PRODUTO_FORNECEDOR
        ));
    }

    @Override
    public Set<OpcaoCliente> getOpcoesDisponiveisCliente() {
        return new HashSet<>(Arrays.asList(
                OpcaoCliente.DADOS,
                OpcaoCliente.TELEFONE,
                OpcaoCliente.CEP,
                OpcaoCliente.RECEBER_CREDITOROTATIVO
        ));
    }

    private String getAliquotaKey(String cst, double aliq, double red) throws SQLException {
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
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	triid as id,\n"
                    + "	tridestributo as descricao,\n"
                    + "	tricstcsosnsaida as csticms,\n"
                    + "	trivalortributacao as aliquotaicms,\n"
                    + "	0 as reducaoicms\n"
                    + "FROM\n"
                    + "	tbtributacoes"
            )) {
                while (rs.next()) {
                    String id = getAliquotaKey(
                            rs.getString("csticms"),
                            rs.getDouble("aliquotaicms"),
                            rs.getDouble("reducaoicms")
                    );
                    result.add(new MapaTributoIMP(
                            id,
                            id,
                            Utils.stringToInt(rs.getString("csticms")),
                            rs.getDouble("aliquotaicms"),
                            rs.getDouble("reducaoicms")
                    ));
                }
            }
        }
        return result;
    }

    public List<Estabelecimento> getLojaCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT \n"
                    + "	empid as id,\n"
                    + "	empnome as descricao\n"
                    + "FROM\n"
                    + "	tbempresas"
            )) {
                while (rs.next()) {
                    result.add(new Estabelecimento(rs.getString("id"), rs.getString("descricao")));
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
                    "SELECT\n"
                    + "	depid as cod_mercadologico1,\n"
                    + "	depdesdepartamento as mercadologico1,\n"
                    + "	depid as cod_mercadologico2,\n"
                    + "	depdesdepartamento as mercadologico2,\n"
                    + "	depid as cod_mercadologico3,\n"
                    + "	depdesdepartamento as mercadologico3\n"
                    + "FROM\n"
                    + "	tbGrupos\n"
                    + "ORDER by 1,3,5"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("cod_mercadologico1"));
                    imp.setMerc1Descricao(rst.getString("mercadologico1"));
                    imp.setMerc2ID(rst.getString("cod_mercadologico2"));
                    imp.setMerc2Descricao(rst.getString("mercadologico2"));
                    imp.setMerc3ID(rst.getString("cod_mercadologico3"));
                    imp.setMerc3Descricao(rst.getString("mercadologico3"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	\"Id\" as id,\n"
                    + "	\"Descricao\" as descricao\n"
                    + "from dbo.\"Familia\"\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
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

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	p.\"Id\" as id,\n"
                    + "	p.\"EAN\" as ean,\n"
                    + "	p.\"Descricao\" as descricaocompleta,\n"
                    + "	p.\"DescricaoReduzida\" as descricaoreduzida,\n"
                    + "	p.\"Unidade\" as tipoembalagem,\n"
                    + "	p.\"DeBalanca\" as balanca,\n"
                    + "	p.\"BalancaValidade\" as validade,\n"
                    + "	p.\"Unitario\",\n"
                    + "	p.\"Volume\",\n"
                    + "	p.\"Peso\" as peso,\n"
                    + "	p.\"FkDepartamento\" as mercadologico1,\n"
                    + "	p.\"FkSecao\" as mercadologico2,\n"
                    + "	p.\"FkCategoria\" as mercadologico3,\n"
                    + "	p.\"FkSubCategoria\" as mercadologico4,\n"
                    + "	p.\"FkFamilia\" as idfamiliaproduto,\n"
                    + "	p.\"DtCadastro\" as datacadastro,\n"
                    + "	p.\"Ativo\" as situacaocadastro,\n"
                    + "	p.\"NCM\" as ncm,\n"
                    + "	p.\"Cest\" as cest,\n"
                    + "	p.\"TribPIS\" as cstpiscofinssaida,\n"
                    + "	p.\"TribPISEntrada\" as cstpiscofinsentrada,\n"
                    + "	p.\"TribICMS\" as csticms,\n"
                    + "	p.\"AliqICMS\" as aliquotaicms,\n"
                    + "	p.\"ReducaoBC\" as reducaoicms,\n"
                    + "	p.\"Pauta\" as pauta,\n"
                    + "	p.\"MVA\" as mva,\n"
                    + "	pl.\"VlVenda\" as precovenda,\n"
                    + "	pl.\"CustoCompra\" as custo,\n"
                    + "	pl.\"MargemCadastrada\" as margem,\n"
                    + "	pl.\"MargemMinima\" as margemminima,\n"
                    + "	pl.\"EstoqueAtual\" as estoque,\n"
                    + "	pl.\"EstoqueMinimo\" as estoqueminimo,\n"
                    + "	pl.\"EstoqueMaximo\" as estoquemaximo\n"
                    + "from dbo.\"Produto\" p\n"
                    + "left join dbo.\"ProdutoMultiLoja\" pl on pl.\"Produto_Id\" = p.\"Id\"\n"
                    + "and pl.\"FkLoja\" = " + getLojaOrigem() + "\n"
                    + "order by 2"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("ean"));
                    imp.seteBalanca(rst.getBoolean("balanca"));
                    imp.setValidade(rst.getInt("validade"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    imp.setIdFamiliaProduto(rst.getString("idfamiliaproduto"));
                    imp.setCodMercadologico1(rst.getString("mercadologico1"));
                    imp.setCodMercadologico2(rst.getString("mercadologico2"));
                    imp.setCodMercadologico3(rst.getString("mercadologico3"));
                    imp.setCodMercadologico4(rst.getString("mercadologico4"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setSituacaoCadastro(rst.getBoolean("situacaocadastro") ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setEstoqueMaximo(rst.getDouble("estoquemaximo"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setMargemMinima(rst.getDouble("margemminima"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setCustoComImposto(rst.getDouble("custo"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getString("cstpiscofinssaida"));
                    imp.setPiscofinsCstCredito(rst.getString("cstpiscofinsentrada"));

                    String idIcms = getAliquotaKey(
                            rst.getString("csticms"),
                            rst.getDouble("aliquotaicms"),
                            rst.getDouble("reducaoicms")
                    );

                    imp.setIcmsDebitoId(idIcms);
                    imp.setIcmsDebitoForaEstadoId(idIcms);
                    imp.setIcmsDebitoForaEstadoNfId(idIcms);
                    imp.setIcmsCreditoId(idIcms);
                    imp.setIcmsCreditoForaEstadoId(idIcms);
                    imp.setIcmsConsumidorId(idIcms);

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
                    + "	\"Produto_Id\" as idproduto,\n"
                    + "	\"EAN\" as ean,\n"
                    + "	\"Quantidade\" as qtdembalagem\n"
                    + "from dbo.\"EanAfiliado\"\n"
                    + "order by 1, 2"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("idproduto"));
                    imp.setEan(rst.getString("ean"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
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
                    "SELECT\n"
                    + "	forId as id,\n"
                    + "	forDesFornecedor as razao,\n"
                    + "	forAbrFornecedor as fantasia,\n"
                    + "	forCGC as cnpj,\n"
                    + "	forInsc as ie,\n"
                    + "	forEndereco as endereco,\n"
                    + "	fornro as numero,\n"
                    + "	forcomplemento as complemento,\n"
                    + "	forBairro as bairro,\n"
                    + "	forCidade as cidade,\n"
                    + "	forEstado as uf,\n"
                    + "	forCEP as cep,\n"
                    + "	forEmail as email,\n"
                    + "	forTelefone as fone,\n"
                    + "	forFax as fax,\n"
                    + "	forObservacao as obs\n"
                    + "FROM\n"
                    + "	tbFornecedores"
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
                    imp.setAtivo(rst.getBoolean("ativo"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setIbge_municipio(rst.getInt("municipioibge"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setTel_principal(rst.getString("telefone"));

                    if (rst.getString("contato") != null && !rst.getString("contato").trim().isEmpty()) {
                        imp.setObservacao("CONTATO - " + rst.getString("contato"));
                    }

                    if (rst.getString("email") != null && !rst.getString("email").trim().isEmpty()) {
                        imp.addEmail("EMAIL", rst.getString("email").toLowerCase(), TipoContato.COMERCIAL);
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
                    + "	p.\"Id\" as idproduto,\n"
                    + "	pf.\"FkProduto\",\n"
                    + "	pf.\"FkEntidade\" as idfornecedor,\n"
                    + "	pf.\"CodigoEntidade\" as codigoexterno,\n"
                    + "	pf.\"DtReferencia\"  as dataalteracao\n"
                    + "from dbo.\"Referencia\" pf\n"
                    + "join dbo.\"Produto\" p on p.\"EAN\" = pf.\"FkProduto\"\n"
                    + "order by 2, 1"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setIdProduto(rst.getString("idproduto"));
                    imp.setIdFornecedor(rst.getString("idfornecedor"));
                    imp.setCodigoExterno(rst.getString("codigoexterno"));
                    imp.setDataAlteracao(rst.getDate("dataalteracao"));
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
                    "SELECT\n"
                    + "    cliid as id,\n"
                    + "    clinomcliente as nome,\n"
                    + "    clifantasia as fantasia,\n"
                    + "    clidatanascimento as data_nasc,\n"
                    + "    clicpf_cgc as cpfcnpj,\n"
                    + "    clirg_ie as rg_ie,\n"
                    + "    cliendereco as endereco,\n"
                    + "    clinro as numero,\n"
                    + "    clibairro as bairro,\n"
                    + "    clicomplemento as complemento,\n"
                    + "    clicidade as cidade,\n"
                    + "    cliestado as uf,\n"
                    + "    clicep as cep,\n"
                    + "    clitelefone as telefone,\n"
                    + "    cliemail as email,\n"
                    + "    clifax as celular,\n"
                    + "    clinomeempresa as empresa,\n"
                    + "    clitelefonetrabalho as fone_empresa,\n"
                    + "    clidesde as data_cadastro,\n"
                    + "    cliflagbloqueado as bloqueado,\n"
                    + "    clilimitecompra as limite,\n"
                    + "    clidiavencconta as dia_vencimento,\n"
                    + "    clipai as nomepai,\n"
                    + "    climae as nomemae,\n"
                    + "    cliconjuge as nome_conju,\n"
                    + "    clicpfconjuge as cpf_conju,\n"
                    + "    clinascconjuge as nasc_conju,\n"
                    + "    clitelefone as observacao\n"
                    + "FROM\n"
                    + "    tbclientes\n"
                    + "ORDER BY 1"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setInscricaoestadual(rst.getString("inscricaoestadual"));
                    imp.setInscricaoMunicipal(rst.getString("inscricaomunicipal"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setDataNascimento(rst.getDate("datanascimento"));
                    imp.setAtivo(rst.getBoolean("ativo"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setMunicipioIBGE(rst.getInt("municipioibge"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setTelefone(rst.getString("telefone"));
                    imp.setEmail(rst.getString("email"));
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
                    "SELECT\n"
                    + "	crpNroLancamento as id,\n"
                    + "	crpDataLancamento as data_emissao,\n"
                    + "	crpNumDocumento as nrpcupom,\n"
                    + "	crpCodCliente as id_cliente,\n"
                    + "	crpCliente_Fornecedor as nome_cliente,\n"
                    + "	crpVencimentoConta as data_vencimento,\n"
                    + "	crpValorLancamento as valor,\n"
                    + "	crpDesLancamento as obs\n"
                    + "FROM \n"
                    + "	tbcontasreceberpagar\n"
                    + "WHERE\n"
                    + "	crpDataPagamento is NULL\n"
                    + "ORDER BY\n"
                    + "	crpNroLancamento"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("id"));
                    imp.setIdCliente(rst.getString("idcliente"));
                    imp.setDataEmissao(rst.getDate("dataemissao"));
                    imp.setDataVencimento(rst.getDate("datavencimento"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setJuros(rst.getDouble("juros"));
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
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	\"FkProduto\" as idproduto,\n"
                    + "	\"VlVenda\" as preconormal,\n"
                    + "	\"VlPromocao\" as precooferta,\n"
                    + "	\"DtPromocaoDe\" as datainicio,\n"
                    + "	\"DtPromocaoAte\" as datafim\n"
                    + "from dbo.\"ProdutoMultiLoja\"\n"
                    + "where \"VlPromocao\" > 0 \n"
                    + "and \"DtPromocaoDe\" >= '2021-01-01'"
            )) {

                while (rs.next()) {
                    OfertaIMP imp = new OfertaIMP();
                    imp.setIdProduto(rs.getString("idproduto"));
                    imp.setDataInicio(rs.getDate("datainicio"));
                    imp.setDataFim(rs.getDate("datafim"));
                    imp.setPrecoNormal(rs.getDouble("preconormal"));
                    imp.setPrecoOferta(rs.getDouble("precooferta"));

                    result.add(imp);

                }
            }
        }
        return result;
    }
}
