package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import vrimplantacao.utils.Utils;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.parametro.Parametros;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Desenvolvimento
 */
public class EcoCentauroDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "Eco Centauro";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                OpcaoProduto.FAMILIA,
                OpcaoProduto.FAMILIA_PRODUTO,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                OpcaoProduto.QTD_EMBALAGEM_EAN,
                OpcaoProduto.ATIVO,
                OpcaoProduto.PESO_BRUTO,
                OpcaoProduto.PESO_LIQUIDO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.ESTOQUE_MINIMO,
                OpcaoProduto.MARGEM,
                OpcaoProduto.VENDA_PDV,
                OpcaoProduto.PRECO,
                OpcaoProduto.CUSTO,
                OpcaoProduto.CUSTO_COM_IMPOSTO,
                OpcaoProduto.CUSTO_SEM_IMPOSTO,
                OpcaoProduto.NCM,
                OpcaoProduto.EXCECAO,
                OpcaoProduto.CEST,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.NATUREZA_RECEITA,
                OpcaoProduto.ICMS,
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.MAPA_TRIBUTACAO
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
                OpcaoFornecedor.PRODUTO_FORNECEDOR
        ));
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT DISTINCT\n"
                    + "    icm.vendacsf1 AS cst_saida,\n"
                    + "    icm.vendaicms1 AS aliquota_saida,\n"
                    + "    icm.vendareducao1 AS reducao_saida\n"
                    + "FROM TESTPRODUTOGERAL pg\n"
                    + "JOIN TESTICMS icm ON icm.produto = pg.codigo\n"
                    + "    AND icm.empresa = " + getLojaOrigem() + "\n"
                    + "    AND icm.estado = '" + Parametros.get().getUfPadraoV2().getSigla() + "'"
            )) {
                while (rs.next()) {
                    String id = rs.getString("cst_saida") + "-" + rs.getString("aliquota_saida") + "-" + rs.getString("reducao_saida");
                    result.add(new MapaTributoIMP(
                            id,
                            id
                    )
                    );
                }
            }

            try (ResultSet rs = stm.executeQuery(
                    "SELECT DISTINCT\n"
                    + "    icm.compracsf AS cst_entrada,\n"
                    + "    icm.compraicms AS aliquota_entrada,\n"
                    + "    icm.comprareducao AS reducao_entrada\n"
                    + "FROM TESTPRODUTOGERAL pg\n"
                    + "JOIN TESTICMS icm ON icm.produto = pg.codigo\n"
                    + "    AND icm.empresa = " + getLojaOrigem() + "\n"
                    + "    AND icm.estado = '" + Parametros.get().getUfPadraoV2().getSigla() + "'"
            )) {
                while (rs.next()) {
                    String id = rs.getString("cst_entrada") + "-" + rs.getString("aliquota_entrada") + "-" + rs.getString("reducao_entrada");
                    result.add(new MapaTributoIMP(
                            id,
                            id
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
                    "SELECT\n"
                    + "    codigo,\n"
                    + "    nomefantasia,\n"
                    + "    cpfcnpj\n"
                    + "FROM TGEREMPRESA\n"
                    + "ORDER BY 1"
            )) {
                while (rst.next()) {
                    result.add(
                            new Estabelecimento(
                                    rst.getString("codigo"),
                                    rst.getString("nomefantasia") + "-" + rst.getString("cpfcnpj")
                            )
                    );
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
                    "SELECT\n"
                    + "    m1.codigo AS merc1,\n"
                    + "    m1.descricao AS desc_merc1,\n"
                    + "    m2.subgrupo AS merc2,\n"
                    + "    m2.descricao AS desc_merc2\n"
                    + "FROM TESTGRUPO m1\n"
                    + "LEFT JOIN TESTSUBGRUPO m2 ON m2.grupo = m1.codigo\n"
                    + "WHERE m1.empresa = '" + getLojaOrigem() + "'\n"
                    + "AND m2.empresa = '" + getLojaOrigem() + "'\n"
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
                    imp.setMerc3ID("1");
                    imp.setMerc3Descricao(imp.getMerc2Descricao());
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
                    "SELECT\n"
                    + "    pg.codigo AS id,\n"
                    + "    pg.codigobarra AS ean,\n"
                    + "    pg.descricao AS descricaocompleta,\n"
                    + "    pg.descricaoreduzida AS descricaoreduzida,\n"
                    + "    pg.descricaograde AS descricaogondola,\n"
                    + "    pg.embalagem AS tipoembalagem,\n"
                    + "    pg.qtdeembalagem AS qtdembalagem,\n"
                    + "    pg.pesobruto AS pesobruto,\n"
                    + "    pg.pesoliquido AS pesoliquido,\n"
                    + "    pg.datacadastro AS datacadastro,\n"
                    + "    pg.classificacaofiscal AS ncm,\n"
                    + "    p.custofabrica AS custosemimposto,\n"
                    + "    p.custofinal AS custocomimposto,\n"
                    + "    p.margemlucro AS margem,\n"
                    + "    p.prpraticado AS precovenda,\n"
                    + "    p.estoqueminimo AS estoqueminimo,\n"
                    + "    p.estoquemaximo AS estoquemaximo,\n"
                    + "    p.estdisponivel AS estoque,\n"
                    + "    p.grupo AS mercaologico1,\n"
                    + "    p.subgrupo AS mercadologico2,\n"
                    + "    1 AS mercadologico3,\n"
                    + "    CASE p.ativo WHEN 'S' THEN 1 ELSE 0 END situacaocadastro\n"
                    + "FROM TESTPRODUTOGERAL pg\n"
                    + "LEFT JOIN TESTPRODUTO p ON p.produto = pg.codigo\n"
                    + "    AND p.empresa = " + getLojaOrigem() + "\n"
                    + "    AND p.ativo = 'S'\n"        
                    + "    AND coalesce(p.estdisponivel, 0) > 0 \n"
                    + "ORDER BY 2"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("ean"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(rst.getString("descricaogondola"));
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    imp.setPesoBruto(rst.getDouble("pesobruto"));
                    imp.setPesoLiquido(rst.getDouble("pesoliquido"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setCodMercadologico1(rst.getString("mercaologico1"));
                    imp.setCodMercadologico2(rst.getString("mercadologico2"));
                    imp.setCodMercadologico3(rst.getString("mercadologico3"));
                    imp.setSituacaoCadastro(rst.getInt("situacaocadastro"));
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setEstoqueMaximo(rst.getDouble("estoquemaximo"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setNcm(rst.getString("ncm"));

                    result.add(imp);
                }
            }
        }
        return result;
    }
    
    @Override
    public List<ProdutoIMP> getProdutos(OpcaoProduto opt) throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        if (opt == OpcaoProduto.CEST) {
            try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "SELECT\n"
                        + "    pg.codigo AS idproduto,\n"
                        + "    ce.cest\n"
                        + "FROM TESTPRODUTOGERAL pg\n"
                        + "JOIN TESTCEST ce ON ce.idcest = pg.idcest"
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("idproduto"));
                        imp.setCest(rst.getString("cest"));
                        result.add(imp);
                    }
                }
                return result;
            }
        }
        
        if (opt == OpcaoProduto.ICMS) {
            try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "SELECT\n"
                        + "    pg.codigo AS idproduto,\n"
                        + "    icm.compracsf AS cst_entrada,\n"
                        + "    icm.compraicms AS aliquota_entrada,\n"
                        + "    icm.comprareducao AS reducao_entrada,\n"
                        + "    icm.vendacsf1 AS cst_saida,\n"
                        + "    icm.vendaicms1 AS aliquota_saida,\n"
                        + "    icm.vendareducao1 AS reducao_saida\n"
                        + "FROM TESTPRODUTOGERAL pg\n"
                        + "JOIN TESTICMS icm ON icm.produto = pg.codigo\n"
                        + "    AND icm.empresa = " + getLojaOrigem() + "\n"
                        + "    AND icm.estado = '" + Parametros.get().getUfPadraoV2().getSigla() + "'"
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("idproduto"));

                        String idIcmsDebito, idIcmsCredito;

                        idIcmsDebito = rst.getString("cst_saida") + "-" + rst.getString("aliquota_saida") + "-" + rst.getString("reducao_saida");
                        idIcmsCredito = rst.getString("cst_entrada") + "-" + rst.getString("aliquota_entrada") + "-" + rst.getString("reducao_entrada");

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

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "    f.codigo AS id,\n"
                    + "    f.nome AS razao,\n"
                    + "    f.fantasia AS fantasia,\n"
                    + "    f.cpfcnpj AS cnpj,\n"
                    + "    f.rgie AS inscricaoestadual,\n"
                    + "    f.endereco,\n"
                    + "    f.numeroendereco AS numero,\n"
                    + "    f.complemento,\n"
                    + "    f.bairro,\n"
                    + "    cid.nome AS municipio,\n"
                    + "    cid.estado AS uf,\n"
                    + "    f.cep,\n"
                    + "    f.fone AS telefone,\n"
                    + "    f.fax,\n"
                    + "    f.homepage AS email2,\n"
                    + "    f.email,\n"
                    + "    f.fonecomervend,\n"
                    + "    f.foneresidvend,\n"
                    + "    f.fonetelevendas,\n"
                    + "    f.celularvend,\n"
                    + "    f.emailvend,\n"
                    + "    f.datacadastro,\n"
                    + "    f.observacao,\n"
                    + "    f.contribuinte,\n"
                    + "    CASE f.ativo WHEN 'S' THEN 1 ELSE 0 END ativo\n"
                    + "FROM tpagfornecedor f\n"
                    + "LEFT JOIN tgercidade cid ON cid.codigo = f.cidade\n"
                    + "ORDER BY 1"
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
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setAtivo(rst.getInt("ativo") == 1);
                    imp.setTel_principal(rst.getString("telefone"));
                    imp.setDatacadastro(rst.getDate("datacadastro"));
                    imp.setObservacao(rst.getString("observacao"));
                    result.add(imp);
                    
                    if ((rst.getString("email") != null)
                            && (!rst.getString("email").trim().isEmpty())) {
                        imp.addEmail("EMAIL", rst.getString("email").toLowerCase(), TipoContato.COMERCIAL);
                    }

                    if ((rst.getString("fonecomervend") != null)
                            && (!rst.getString("fonecomervend").trim().isEmpty())) {
                        imp.addTelefone("COMERCIAL", rst.getString("fonecomervend"));
                    }

                    if ((rst.getString("foneresidvend") != null)
                            && (!rst.getString("foneresidvend").trim().isEmpty())) {
                        imp.addTelefone("VENDAS", rst.getString("foneresidvend"));
                    }

                    if ((rst.getString("fonetelevendas") != null)
                            && (!rst.getString("fonetelevendas").trim().isEmpty())) {
                        imp.addTelefone("TELEVENDAS", rst.getString("fonetelevendas"));
                    }

                    if ((rst.getString("celularvend") != null)
                            && (!rst.getString("celularvend").trim().isEmpty())) {
                        imp.addCelular("CELULAR VENDAS", rst.getString("celularvend"));
                    }

                    if ((rst.getString("emailvend") != null)
                            && (!rst.getString("emailvend").trim().isEmpty())) {
                        imp.addEmail("EMAIL VENDAS", rst.getString("emailvend").toLowerCase(), TipoContato.COMERCIAL);
                    }
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
                    "SELECT\n"
                    + "    fornecedor AS idfornecedor,\n"
                    + "    produto AS idproduto,\n"
                    + "    ultcompraqtde AS qtdembalagem,\n"
                    + "    ultcompradata AS dataalteracao,\n"
                    + "    codnofornecedor AS codigoexterno\n"
                    + "FROM testfornecproduto\n"
                    + "ORDER BY 1, 2"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rst.getString("idproduto"));
                    imp.setIdFornecedor(rst.getString("idfornecedor"));
                    imp.setCodigoExterno(rst.getString("codigoexterno"));
                    imp.setQtdEmbalagem(rst.getDouble("qtdembalagem"));
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
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	c.codigo id,\n"
                    + "	c.nome,\n"
                    + "	FANTASIA,\n"
                    + "	CPFCNPJ,\n"
                    + "	RGIE,\n"
                    + "	INSCRICAOMUNICIPAL im,\n"
                    + "	PESSOA tipopessoa,\n"
                    + "	ENDERECO,\n"
                    + "	NUMEROENDERECO numero,\n"
                    + "	BAIRRO,\n"
                    + "	COMPLEMENTO,\n"
                    + "	m.nome CIDADE,\n"
                    + " m.ESTADO uf,"
                    + "	CEP,\n"
                    + "	datacadastro data_cadastro,\n"
                    + "	CASE WHEN bloqueado = 'S' THEN 1 ELSE 0 END BLOQUEADO,\n"
                    + "	LIMITE,\n"
                    + "	FONE telefone,\n"
                    + "	FAX,\n"
                    + "	FONECELULAR celular,\n"
                    + "	EMAIL,\n"
                    + "	OBS \n"
                    + "FROM\n"
                    + "	TRECCLIENTEGERAL c\n"
                    + "LEFT JOIN TGERCIDADE m ON m.CODIGO = c.CIDADE \n"
                    + "ORDER BY 1"
            )) {
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rs.getString("id"));
                    imp.setRazao(rs.getString("nome"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setCnpj(rs.getString("CPFCNPJ"));
                    imp.setInscricaoestadual(rs.getString("RGIE"));
                    imp.setInscricaoMunicipal(rs.getString("im"));
                    imp.setBloqueado(rs.getBoolean("bloqueado"));

                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));

                    imp.setDataCadastro(rs.getDate("data_cadastro"));
                    imp.setBloqueado(rs.getBoolean("bloqueado"));
                    imp.setValorLimite(rs.getDouble("limite"));

                    imp.setTelefone(rs.getString("telefone"));
                    imp.setFax(rs.getString("fax"));
                    imp.setCelular(rs.getString("celular"));
                    imp.setEmail(rs.getString("email"));
                    imp.setObservacao(rs.getString("obs"));

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
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	cr.EMPRESA||cr.CLIENTE||cr.MESANO id,\n"
                    + "	cr.EMPRESA||cr.CLIENTE||cr.MESANO numerocupom,\n"
                    + "	CREDIARIO valor\n"
                    + "	cr.CLIENTE codcli,\n"
                    + " cpfcnpj cnpjcliente,\n"
                    + "	1 AS ecf,\n"
                    + "	CAST(cr.DATAHORAALTERACAO AS date) emissao,\n"
                    + "	CAST(cr.DATAHORAALTERACAO AS date) vencimento,\n"
                    + "FROM\n"
                    + "	TRECDEBITOMENSAL cr\n"
                    + " LEFT JOIN TRECCLIENTEGERAL c ON c.CODIGO = cr.CLIENTE\n"
                    + "WHERE\n"
                    + "	EMPRESA = " + getLojaOrigem() + ""
            )) {
                while (rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rs.getString("id"));
                    imp.setNumeroCupom(Utils.formataNumero(rs.getString("numerocupom")));
                    imp.setValor(rs.getDouble("valor"));
                    imp.setIdCliente(rs.getString("codcli"));
                    imp.setCnpjCliente(Utils.formataNumero(rs.getString("cnpjcliente")));
                    imp.setEcf(rs.getString("ecf"));
                    imp.setDataEmissao(rs.getDate("emissao"));
                    imp.setDataVencimento(rs.getDate("vencimento"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
