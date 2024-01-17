package vrimplantacao2_5.dao.sistema;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.enums.TipoIndicadorIE;
import vrimplantacao2.vo.enums.TipoInscricao;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2_5.dao.conexao.ConexaoFirebird;

/**
 *
 * @author Bruno
 */
public class ResulthBusinessDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "ResulthBusiness";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                OpcaoProduto.QTD_EMBALAGEM_EAN,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.FAMILIA,
                OpcaoProduto.FAMILIA_PRODUTO,
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.ATIVO,
                OpcaoProduto.PESO_BRUTO,
                OpcaoProduto.PESO_LIQUIDO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.TROCA,
                OpcaoProduto.MARGEM,
                OpcaoProduto.VENDA_PDV,
                OpcaoProduto.PDV_VENDA,
                OpcaoProduto.PRECO,
                OpcaoProduto.CUSTO,
                OpcaoProduto.CUSTO_COM_IMPOSTO,
                OpcaoProduto.CUSTO_SEM_IMPOSTO,
                OpcaoProduto.NCM,
                OpcaoProduto.CEST,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.NATUREZA_RECEITA,
                OpcaoProduto.ICMS,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.ATUALIZAR_SOMAR_ESTOQUE,
                OpcaoProduto.OFERTA,
                OpcaoProduto.DESCONTINUADO,
                OpcaoProduto.VOLUME_QTD,
                OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                OpcaoProduto.FABRICANTE
        ));
    }

    @Override
    public Set<OpcaoFornecedor> getOpcoesDisponiveisFornecedor() {
        return new HashSet<>(Arrays.asList(
                OpcaoFornecedor.ENDERECO,
                OpcaoFornecedor.DADOS,
                OpcaoFornecedor.CONTATOS,
                OpcaoFornecedor.SITUACAO_CADASTRO,
                OpcaoFornecedor.TIPO_EMPRESA,
                OpcaoFornecedor.PAGAR_FORNECEDOR,
                OpcaoFornecedor.PRODUTO_FORNECEDOR
        ));
    }

    @Override
    public Set<OpcaoCliente> getOpcoesDisponiveisCliente() {
        return new HashSet<>(Arrays.asList(
                OpcaoCliente.DADOS,
                OpcaoCliente.ENDERECO,
                OpcaoCliente.CONTATOS,
                OpcaoCliente.DATA_CADASTRO,
                OpcaoCliente.TIPO_INSCRICAO,
                OpcaoCliente.DATA_NASCIMENTO,
                OpcaoCliente.INSCRICAO_ESTADUAL,
                OpcaoCliente.VENCIMENTO_ROTATIVO,
                OpcaoCliente.RECEBER_CREDITOROTATIVO,
                OpcaoCliente.VALOR_LIMITE,
                OpcaoCliente.CLIENTE_EVENTUAL
        ));
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "DISTINCT\n"
                    + "CODTRIBUT00 || '-' || CAST (ALIQICMSREG00 AS varchar(10))|| '-' || CAST(baseicmsreg00 AS varchar(10)) AS id ,\n"
                    + "'CST ' || CODTRIBUT00 || ' ALIQ ' || CAST(ALIQICMSREG00 AS varchar(10)) || ' RED ' || CAST(baseicmsreg00 AS varchar(10)) AS descricao,\n"
                    + "COALESCE(NULLIF(CODTRIBUT00, ''), '0') AS cst,\n"
                    + "ALIQICMSREG00 AS aliq,\n"
                    + "baseicmsreg00 AS red\n"
                    + "FROM\n"
                    + "produto"
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(
                            rs.getString("id"),
                            rs.getString("descricao"),
                            Integer.parseInt(rs.getString("cst")),
                            rs.getDouble("aliq"),
                            rs.getDouble("red"))
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
                    + "g.CODGRUPO AS merc1,\n"
                    + "g.DESCRICAO AS descmerc1,\n"
                    + "s.CODSUBGRUPO AS merc2,\n"
                    + "s.DESCRICAO AS descmerc2\n"
                    + "FROM GRUPROD g \n"
                    + "JOIN SUBGRUP s ON s.CODGRUPO = g.CODGRUPO "
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());

                    imp.setMerc1ID(rst.getString("merc1"));
                    imp.setMerc1Descricao(rst.getString("descmerc1"));
                    imp.setMerc2ID(rst.getString("merc2"));
                    imp.setMerc2Descricao(rst.getString("descmerc2"));
                    imp.setMerc3ID(imp.getMerc2ID());
                    imp.setMerc3Descricao(imp.getMerc2Descricao());

                    result.add(imp);
                }
            }
        }
        return result;
    }

    /*
    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    ""
            )) {
                while (rs.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rs.getString("id_familia"));
                    imp.setDescricao(rs.getString("familia"));

                    result.add(imp);
                }
            }
        }
        return result;
    }
     */
    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	CODPROD AS id_produto,\n"
                    + "	REFERENCIA AS ean,\n"
                    + "	EMBALAGEM AS qtdembalagem,\n"
                    + "	UNIDADESAIDA AS tipo_embalagem\n"
                    + "FROM\n"
                    + "	PRODUTO p "
            )) {
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rs.getString("id_produto"));
                    imp.setEan(rs.getString("ean"));
                    imp.setQtdEmbalagem(rs.getInt(1));
                    imp.setTipoEmbalagem(rs.getString("tipo_embalagem"));

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
                    + "	p.CODPROD AS id,\n"
                    + "	CASE \n"
                    + "	WHEN char_LENGTH(p.REFERENCIA) <=7 THEN substring(p.REFERENCIA FROM 2 FOR 7) ELSE p.REFERENCIA END  AS ean,\n"
                    + "	p.DESCRICAO AS descricao,\n"
                    + "	p.UNIDADEENT AS tipo_emb,\n"
                    + "	c.ESTOQUE  AS estoque,\n"
                    + "	p.preco AS precovenda,\n"
                    + "	c.PRECOCUSTO  AS precocusto,\n"
                    + "	p.CODGRUPO AS merc1,\n"
                    + "	p.CODSUBGRUPO AS merc2,\n"
                    + "	CASE\n"
                    + "	WHEN p.ATIVO = 'S' THEN 1\n"
                    + "	ELSE  0\n"
                    + "	END ativo,\n"
                    + "	p.DT_CADASTRO AS data_cad,\n"
                    + "	p.DATA_ULT_ALTERACAO AS data_alt,\n"
                    + "	p.ESTMINIMO AS est_min,\n"
                    + "	p.ESTMAXIMO AS est_max,\n"
                    + "	p.PESO as peso_bruto,\n"
                    + "	fisc.CODIGONCM as ncm,\n"
                    + "	CAST (p.CODTRIBUT00 AS integer) || '-' || round(p.ALIQICMSREG00, 2) || '-' || round(p.baseicmsreg00, 2) AS id_icms,\n"
                    + "	pd.COFINS_CST AS pis_cofins,\n"
                    + " CODCEST AS cest,\n"
                    + "	p.CODNATRECEITA AS nat_rec\n"
                    + "	FROM\n"
                    + "	PRODUTO p\n"
                    + "	JOIN COMPPROD c ON c.CODPROD = p.CODPROD \n"
                    + "	JOIN CLASFISC fisc ON fisc.CODCLASFIS = p.CODCLASFIS \n"
                    + "	JOIN PRODUTODETALHE pd ON pd.CODPROD = p.CODPROD"
            )) {
                Map<Integer, vrimplantacao2.vo.cadastro.ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("ean"));

                    ProdutoBalancaVO bal = produtosBalanca.get(Utils.stringToInt(rst.getString("ean"), -2));

                    if (bal != null) {
                        imp.seteBalanca(true);
                        imp.setTipoEmbalagem("P".equals(bal.getPesavel()) ? "KG" : "UN");
                        imp.setEan(String.valueOf(bal.getCodigo()));
                    }

                    imp.setDescricaoCompleta(rst.getString("descricao"));
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setTipoEmbalagem(rst.getString("tipo_emb"));
                    imp.setQtdEmbalagem(rst.getInt(1));
                    // imp.seteBalanca(rst.getBoolean("e_balanca"));

                    imp.setCustoComImposto(rst.getDouble("precocusto"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setPrecovenda(rst.getDouble("precovenda"));

                    imp.setCodMercadologico1(rst.getString("merc1"));
                    imp.setCodMercadologico2(rst.getString("merc2"));
                    imp.setCodMercadologico3(imp.getCodMercadologico2());

                    imp.setSituacaoCadastro(rst.getInt("ativo"));
                    imp.setDataCadastro(rst.getDate("data_cad"));
                    imp.setDataAlteracao(rst.getDate("data_alt"));
                    imp.setEstoqueMinimo(rst.getDouble("est_min"));
                    imp.setEstoqueMaximo(rst.getDouble("est_max"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setValidade(0);
                    imp.setPesoBruto(rst.getDouble("peso_bruto"));

                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));

                    String idIcmsDebito = rst.getString("id_icms");

                    imp.setIcmsDebitoId(idIcmsDebito);
                    imp.setIcmsConsumidorId(idIcmsDebito);
                    imp.setIcmsDebitoForaEstadoId(idIcmsDebito);
                    imp.setIcmsDebitoForaEstadoNfId(idIcmsDebito);

                    imp.setIcmsCreditoId(idIcmsDebito);
                    imp.setIcmsCreditoForaEstadoId(idIcmsDebito);

                    imp.setPiscofinsCstDebito(rst.getString("pis_cofins"));
                    imp.setPiscofinsNaturezaReceita(Utils.stringToInt(rst.getString("nat_rec")));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	CODFORNEC AS id,\n"
                    + "	nome AS razao,\n"
                    + "	NOMEFANTASIA AS fantasia,\n"
                    + "	CGCCPF AS cpf_cnpj,\n"
                    + "	INSCEST AS rg_ie,\n"
                    + "	ENDERECO AS endereco,\n"
                    + "	NUMERO AS numero,	\n"
                    + "	c.CIDADE AS cidade,\n"
                    + "	f.BAIRRO AS bairro,\n"
                    + "	c.ESTADO AS uf,\n"
                    + "	f.CEP AS cep,\n"
                    + "	CASE \n"
                    + "	WHEN ATIVO = 'S' THEN 1\n"
                    + "	ELSE 0\n"
                    + "	END ativo,\n"
                    + "	OBSERVACAO AS obs,\n"
                    + "	fone AS telefone\n"
                    + "FROM\n"
                    + "	FORNECE f \n"
                    + "	JOIN CIDADES c ON c.CODCIDADE = f.CODCIDADE "
            )) {
                while (rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setCnpj_cpf(rs.getString("cpf_cnpj"));
                    imp.setIe_rg(rs.getString("rg_ie"));

                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));

                    imp.setAtivo(rs.getBoolean("ativo"));
                    imp.setObservacao(rs.getString("obs"));
                    imp.setTel_principal(rs.getString("telefone"));

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
            try (ResultSet rs = stm.executeQuery(
                    ""
            )) {
                while (rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setIdFornecedor(rs.getString("id_fornecedor"));
                    imp.setIdProduto(rs.getString("id_produto"));
                    imp.setCodigoExterno(rs.getString("cod_externo"));
                    imp.setQtdEmbalagem(rs.getDouble("qtde_emb"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    /*
    @Override
    public List<ContaPagarIMP> getContasPagar() throws Exception {
        List<ContaPagarIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    ""
            )) {
                while (rst.next()) {
                    ContaPagarIMP imp = new ContaPagarIMP();

                    imp.setId(rst.getString("id"));
                    imp.setIdFornecedor(rst.getString("id_fornecedor"));
                    imp.setNumeroDocumento(rst.getString("documento"));
                    imp.setDataEmissao(rst.getDate("emissao"));
                    imp.setDataEntrada(imp.getDataEmissao());
                    imp.addVencimento(rst.getDate("vencimento"), rst.getDouble("valor"), rst.getString("observacao"));

                    result.add(imp);
                }
            }
        }

        return result;
    }
     */
    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	CODCLIENTE AS id,\n"
                    + "	NOME AS razao,\n"
                    + "	NOMEFANTASIA AS fantasia,\n"
                    + "	CGCCPF AS cpf,\n"
                    + "	INSCEST AS rg_ie,\n"
                    + "	ENDERECO AS endereco,\n"
                    + "	NUMERO AS numero,\n"
                    + "	COMPLEMENTO AS complemento,\n"
                    + "	BAIRRO ,\n"
                    + "	c2.CIDADE AS cidade,\n"
                    + "	c2.ESTADO  AS uf,\n"
                    + "	CEP ,\n"
                    + "	DT_CADASTRO AS data_cad,\n"
                    + "	CASE \n"
                    + "	WHEN ATIVO = 'S'THEN 1 ELSE 0\n"
                    + "	END AS ativo,\n"
                    + "	FONE ,\n"
                    + "	EMAIL ,\n"
                    + "	CASE WHEN PESSOA_FJ = 'J' THEN 0 ELSE 1 END AS tipoinscricao ,\n"
                    + "	OBSERVACAO \n"
                    + "FROM\n"
                    + "	CLIENTE c\n"
                    + "	JOIN CIDADES c2 ON c.CODCIDADE = c2.CODCIDADE \n"
            //        + "WHERE CODCLIENTE = '00000016'"
            )) {
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setCnpj(rs.getString("cpf"));
                    imp.setInscricaoestadual(rs.getString("rg_ie"));

                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));

                    imp.setDataCadastro(rs.getDate("data_cad"));
                    imp.setAtivo(rs.getBoolean("ativo"));
                    imp.setTelefone(rs.getString("FONE"));
                    imp.setObservacao(rs.getString("OBSERVACAO"));
                    imp.setEmail(rs.getString("email"));
                    imp.setTipoInscricao(rs.getInt("tipoinscricao") == 0 ? TipoInscricao.JURIDICA : TipoInscricao.FISICA);

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
                    + "	d.NUMORD AS id,\n"
                    + "	d.NUMDOCORIG AS cupom,\n"
                    + "	d.CODCLIENTE AS codcli,	\n"
                    + "	c.CGCCPF AS cpf,\n"
                    + "	1 AS ecf,\n"
                    + "	d.VALORDOCTO AS valor,\n"
                    + "	d.DT_EMISSAO AS emissao,\n"
                    + "	d.DT_VENCIMENTO AS vencimento,\n"
                    + "	d.OBSERVACAO AS obs\n"
                    + "FROM\n"
                    + "	docurec d\n"
                    + "JOIN cliente c ON d.CODCLIENTE = c.CODCLIENTE \n"
                    + "WHERE VALORPAGO = 0 "
            )) {
                while (rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();

                    imp.setId(rs.getString("id"));
                    imp.setNumeroCupom(Utils.formataNumero(rs.getString("cupom")));
                    imp.setIdCliente(rs.getString("codcli"));
                    imp.setCnpjCliente(rs.getString("cpf"));
                    imp.setEcf(rs.getString("ecf"));
                    imp.setValor(rs.getDouble("valor"));
                    imp.setDataEmissao(rs.getDate("emissao"));
                    imp.setDataVencimento(rs.getDate("vencimento"));
                    imp.setObservacao(rs.getString("obs"));

                    result.add(imp);
                }
            }
        }
        return result;
    }
}
