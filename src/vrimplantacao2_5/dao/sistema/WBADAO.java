package vrimplantacao2_5.dao.sistema;

import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2_5.dao.conexao.ConexaoFirebird;

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
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/*
 *
 * @author Alan
 *
 */
public class WBADAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "WBA";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.QTD_EMBALAGEM_EAN,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.ATIVO,
                OpcaoProduto.PESO_BRUTO,
                OpcaoProduto.PESO_LIQUIDO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.MARGEM,
                OpcaoProduto.VENDA_CONTROLADA,
                OpcaoProduto.PRECO,
                OpcaoProduto.CUSTO,
                OpcaoProduto.CUSTO_COM_IMPOSTO,
                OpcaoProduto.CUSTO_SEM_IMPOSTO,
                OpcaoProduto.NCM,
                OpcaoProduto.CEST,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.ICMS,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                OpcaoProduto.ATUALIZAR_SOMAR_ESTOQUE,
                OpcaoProduto.OFERTA,
                OpcaoProduto.VOLUME_QTD,
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_PRODUTO
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
                OpcaoFornecedor.SITUACAO_CADASTRO,
                OpcaoFornecedor.ENDERECO,
                OpcaoFornecedor.NUMERO,
                OpcaoFornecedor.COMPLEMENTO,
                OpcaoFornecedor.BAIRRO,
                OpcaoFornecedor.MUNICIPIO,
                OpcaoFornecedor.UF,
                OpcaoFornecedor.CEP,
                OpcaoFornecedor.DATA_CADASTRO,
                OpcaoFornecedor.OBSERVACAO,
                OpcaoFornecedor.PRODUTO_FORNECEDOR));
    }

    @Override
    public Set<OpcaoCliente> getOpcoesDisponiveisCliente() {
        return new HashSet<>(Arrays.asList(
                OpcaoCliente.DADOS,
                OpcaoCliente.CNPJ,
                OpcaoCliente.INSCRICAO_ESTADUAL,
                OpcaoCliente.RAZAO,
                OpcaoCliente.FANTASIA,
                OpcaoCliente.SITUACAO_CADASTRO,
                OpcaoCliente.ENDERECO,
                OpcaoCliente.NUMERO,
                OpcaoCliente.COMPLEMENTO,
                OpcaoCliente.BAIRRO,
                OpcaoCliente.MUNICIPIO,
                OpcaoCliente.UF,
                OpcaoCliente.CEP,
                OpcaoCliente.ESTADO_CIVIL,
                OpcaoCliente.CONTATOS,
                OpcaoCliente.DATA_CADASTRO,
                OpcaoCliente.DATA_NASCIMENTO,
                OpcaoCliente.EMPRESA,
                OpcaoCliente.ENDERECO_EMPRESA,
                OpcaoCliente.BAIRRO_EMPRESA,
                OpcaoCliente.COMPLEMENTO_EMPRESA,
                OpcaoCliente.MUNICIPIO_EMPRESA,
                OpcaoCliente.UF_EMPRESA,
                OpcaoCliente.CEP_EMPRESA,
                OpcaoCliente.TELEFONE_EMPRESA,
                OpcaoCliente.DATA_ADMISSAO,
                OpcaoCliente.CARGO,
                OpcaoCliente.SALARIO,
                OpcaoCliente.NOME_CONJUGE,
                OpcaoCliente.DATA_NASCIMENTO_CONJUGE,
                OpcaoCliente.NOME_PAI,
                OpcaoCliente.NOME_MAE,
                OpcaoCliente.TELEFONE,
                OpcaoCliente.CELULAR,
                OpcaoCliente.EMAIL,
                OpcaoCliente.RECEBER_CREDITOROTATIVO));
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	DISTINCT\n"
                    + "	CAST (CASE\n"
                    + "	 WHEN i.NF_CODICMS LIKE '%F%' THEN '60'\n"
                    + "	 WHEN i.NF_CODICMS LIKE '%I%' THEN '40'\n"
                    + "	 WHEN i.NF_CODICMS LIKE '%N%' THEN '41'\n"
                    + "	 WHEN i.NF_CODICMS IS NULL THEN '40'\n"
                    + "	 WHEN i.NF_CODICMS = '' AND p.ICMS > 0 THEN '00'\n"
                    + "	 WHEN i.NF_CODICMS LIKE '%T%' AND p.REDUZICMS > 0 THEN '20'\n"
                    + "	 WHEN i.NF_CODICMS LIKE '%T%' AND p.REDUZICMS = 0 THEN '00' \n"
                    + "	 ELSE 40\n"
                    + "	END AS integer) AS cst,\n"
                    + "	p.ICMS AS aliq,\n"
                    + "	p.REDUZICMS red\n"
                    + "FROM\n"
                    + "	CTPROD p\n"
                    + "LEFT JOIN\n"
                    + "	WBA_CTPROD i ON\n"
                    + "	p.CODIGO = i.CODIGO"
            )) {
                while (rs.next()) {
                    String id = rs.getString("cst") + "-" + rs.getString("aliq") + "-" + rs.getString("red");
                    result.add(new MapaTributoIMP(
                            id,
                            id,
                            rs.getInt("cst"),
                            rs.getDouble("aliq"),
                            rs.getDouble("red")));
                }
            }
        }

        return result;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	TRIM(CODIGO) idproduto,\n"
                    + "	SUBSTRING(TRIM(CODIGO) FROM 1 FOR 14) ean,\n"
                    + "	1 AS qtdembalagem,\n"
                    + "	NOME\n"
                    + "FROM\n"
                    + "	CTPROD\n"
                    + "WHERE\n"
                    + "	codigo <> ''\n"
                    + "ORDER BY 1"
            )) {
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("idproduto"));
                    imp.setEan(rs.getString("ean"));
                    imp.setQtdEmbalagem(rs.getInt("qtdembalagem"));

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
                    + "	TRIM(p.CODIGO) id,\n"
                    + "	SUBSTRING(TRIM(p.CODIGO) FROM 1 FOR 14) ean,\n"
                    + "	TRIM(p.NOME) descricaocompleta,\n"
                    + "	COALESCE (p.UNIDADE,'UN') UNIDADE,\n"
                    + "	p.DATA_CT data_cadastro,\n"
                    + "	p.DTATUALIZ data_alteracao,\n"
                    + "	p.CUSTO custocomimposto,\n"
                    + "	p.CUSTO custosemimposto,\n"
                    + "	p.ESTMINIMO estoquemin,\n"
                    + "	e.estoque_fisico estoque,\n"
                    + "	CAST (CASE\n"
                    + "	  WHEN i.NF_CODICMS LIKE '%F%' THEN '60'\n"
                    + "	  WHEN i.NF_CODICMS LIKE '%I%' THEN '40'\n"
                    + "	  WHEN i.NF_CODICMS LIKE '%N%' THEN '41'\n"
                    + "	  WHEN i.NF_CODICMS IS NULL THEN '40'\n"
                    + "	  WHEN i.NF_CODICMS = '' AND p.ICMS > 0 THEN '00'\n"
                    + "	  WHEN i.NF_CODICMS LIKE '%T%' AND p.REDUZICMS > 0 THEN '20'\n"
                    + "	  WHEN i.NF_CODICMS LIKE '%T%' AND p.REDUZICMS = 0 THEN '00' \n"
                    + "	  ELSE 40\n"
                    + "	END AS integer)||'-'||p.ICMS||'-'||p.REDUZICMS id_icms,\n"
                    + "	p.MARGEMFIXA margem,\n"
                    + "	p.NF_CFISCAL ncm,\n"
                    + "	CASE \n"
                    + "	  WHEN p.PESAVEL = 'S' THEN 1\n"
                    + "	  ELSE 0\n"
                    + "	END e_balanca,\n"
                    + "	p.PRECOVENDA,\n"
                    + "	p.SETOR merc1,\n"
                    + "	p.SETOR merc2,\n"
                    + "	p.SETOR merc3,\n"
                    + "	p.PESO pesobruto,\n"
                    + "	CASE COALESCE(p.INATIVO,0) WHEN 'S' THEN 0 ELSE 1 END situacaocadastro\n"
                    + "FROM\n"
                    + "	CTPROD p\n"
                    + "	LEFT JOIN CTPROD_FILIAL f ON p.CODIGO = f.CODIGO\n"
                    + "	LEFT JOIN CTPROD_ESTOQUE e ON e.CODIGO = p.CODIGO AND f.FILIAL = e.FILIAL\n"
                    + "	LEFT JOIN WBA_CTPROD i ON p.CODIGO = i.CODIGO\n"
                    + "WHERE\n"
                    + "	f.FILIAL = " + getLojaOrigem() + ""
            )) {
                Map<Integer, vrimplantacao2.vo.cadastro.ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("ean"));

                    ProdutoBalancaVO bal = produtosBalanca.get(Utils.stringToInt(imp.getImportId(), -2));

                    if (bal != null) {
                        imp.seteBalanca(true);
                        imp.setTipoEmbalagem("P".equals(bal.getPesavel()) ? "KG" : "UN");
                        imp.setEan(imp.getImportId());
                    }

                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setTipoEmbalagem(rst.getString("unidade"));

                    imp.setDataCadastro(rst.getDate("data_cadastro"));
                    imp.setDataAlteracao(rst.getDate("data_alteracao"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setEstoqueMinimo(rst.getDouble("estoquemin"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setNcm(rst.getString("ncm"));

                    imp.setIcmsDebitoId(rst.getString("id_icms"));
                    imp.setIcmsDebitoForaEstadoId(imp.getIcmsDebitoId());
                    imp.setIcmsDebitoForaEstadoNfId(imp.getIcmsDebitoId());
                    imp.setIcmsCreditoId(imp.getIcmsDebitoId());
                    imp.setIcmsCreditoForaEstadoId(imp.getIcmsDebitoId());
                    imp.setIcmsConsumidorId(imp.getIcmsDebitoId());

                    imp.setCodMercadologico1(rst.getString("merc1"));
                    imp.setCodMercadologico2(rst.getString("merc2"));
                    imp.setCodMercadologico3(rst.getString("merc3"));

                    imp.setSituacaoCadastro(rst.getInt("situacaocadastro"));
                    imp.setPesoBruto(rst.getInt("pesobruto"));

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
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	CODIGO id,\n"
                    + "	NOME razao,\n"
                    + "	FANTASIA,\n"
                    + "	CNPJ cnpj_cpf,\n"
                    + "	IE ie_rg,\n"
                    + "	CASE COALESCE(INATIVO,0) WHEN 0 THEN 1 ELSE 0 END ativo,\n"
                    + "	ENDER endereco,\n"
                    + "	NUMERO,\n"
                    + "	COMPL complemento,\n"
                    + "	BAIRRO,\n"
                    + "	CIDADE,\n"
                    + "	ESTADO,\n"
                    + "	CEP,\n"
                    + "	fone,\n"
                    + "	REGISTRO data_cadastro\n"
                    + "FROM\n"
                    + "	SIGCAD\n"
                    + "WHERE\n"
                    + " FILIAL = " + getLojaOrigem() + "\n"
                    + "	and TIPO = 'FORNECEDOR'\n"
                    + "ORDER BY 1"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj_cpf"));
                    imp.setIe_rg(rst.getString("ie_rg"));
                    imp.setAtivo(rst.getBoolean("ativo"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("estado"));
                    imp.setCep(rst.getString("cep"));
                    imp.setTel_principal(rst.getString("fone"));
                    imp.setDatacadastro(rst.getDate("data_cadastro"));

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
                    "SELECT\n"
                    + "	CODIGO id_produto,\n"
                    + "	FORNECEDOR id_fornecedor,\n"
                    + "	CODPRODFORNEC codexterno,\n"
                    + "	QTDEPOREMBALAGEM qtd_embalagem\n"
                    + "FROM\n"
                    + "	CTPROD_CPRITEM\n"
                    + "WHERE\n"
                    + "	CODPRODFORNEC IS NOT NULL\n"
                    + "	AND CODPRODFORNEC <> ''\n"
                    + "ORDER BY 1"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rst.getString("id_produto"));
                    imp.setIdFornecedor(rst.getString("id_fornecedor"));
                    imp.setCodigoExterno(rst.getString("codexterno"));
                    imp.setQtdEmbalagem(rst.getDouble("qtd_embalagem"));

                    result.add(imp);
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
                    + "	CODIGO codmerc1,\n"
                    + "	NOME descmerc1,\n"
                    + "	CODIGO codmerc2,\n"
                    + "	NOME descmerc2,\n"
                    + "	CODIGO codmerc3,\n"
                    + "	NOME descmerc3\n"
                    + "FROM\n"
                    + "	CTSETOR c\n"
                    + "ORDER\n"
                    + "	BY 1,3,5"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setMerc1ID(rst.getString("codmerc1"));
                    imp.setMerc1Descricao(Utils.acertarTexto(rst.getString("descmerc1")));
                    imp.setMerc2ID(rst.getString("codmerc2"));
                    imp.setMerc2Descricao(Utils.acertarTexto(rst.getString("descmerc2")));
                    imp.setMerc3ID(rst.getString("codmerc3"));
                    imp.setMerc3Descricao(Utils.acertarTexto(rst.getString("descmerc3")));

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
                    + "	codigo id,\n"
                    + "	nome,\n"
                    + "	cnpj,\n"
                    + "	CASE\n"
                    + "	  WHEN RG IS NULL THEN IE\n"
                    + "	  ELSE rg\n"
                    + "	END rg_ie,\n"
                    + "	ender endereco,\n"
                    + "	numero,\n"
                    + "	compl,\n"
                    + "	bairro,\n"
                    + "	cidade,	\n"
                    + "	estado,\n"
                    + "	cep,\n"
                    + "	celular,\n"
                    + "	email,\n"
                    + "	BLOQUEIO,\n"
                    + "	CASE COALESCE(INATIVO,0) WHEN 0 THEN 1 ELSE 0 END ativo,\n"
                    + "	DATA data_cadastro,\n"
                    + "	LIMITE,\n"
                    + "	PROFISSAO\n"
                    + "FROM\n"
                    + "	sigcad\n"
                    + "WHERE filial = " + getLojaOrigem() + "\n"
                    + "	and TIPO = 'CLIENTE'\n"
                    + "ORDER BY 1"
            )) {
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rs.getString("id"));
                    imp.setRazao(rs.getString("nome"));
                    imp.setFantasia(imp.getRazao());
                    imp.setCnpj(rs.getString("cnpj"));
                    imp.setInscricaoestadual(rs.getString("rg_ie"));

                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("compl"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("estado"));
                    imp.setCep(rs.getString("cep"));

                    imp.setCelular(rs.getString("celular"));
                    imp.setEmail(rs.getString("email"));

                    imp.setBloqueado(rs.getBoolean("bloqueio"));
                    imp.setAtivo(rs.getBoolean("ativo"));
                    imp.setDataCadastro(rs.getDate("data_cadastro"));
                    imp.setValorLimite(rs.getDouble("limite"));
                    imp.setCargo(rs.getString("profissao"));

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
                    + "	ID,\n"
                    + "	DCTO numerocupom,\n"
                    + "	VALOR,\n"
                    + "	CLIFOR id_cliente,\n"
                    + "	EMISSAO data_emissao,\n"
                    + "	VCTO_ data_vencimento,\n"
                    + "	HIST_ obs\n"
                    + "FROM\n"
                    + "	SIGFLU_LIQUIDO\n"
                    + "WHERE\n"
                    + " FILIAL = " + getLojaOrigem() + " AND \n"
                    + "	HISTORICO LIKE 'VDA PZO%' AND\n"
                    + "	DTPGTO IS NULL\n"
                    + "ORDER BY EMISSAO, ID"
            )) {
                while (rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();

                    imp.setId(rs.getString("id"));
                    imp.setNumeroCupom(Utils.formataNumero(rs.getString("numerocupom")));
                    imp.setValor(rs.getDouble("valor"));
                    imp.setIdCliente(rs.getString("id_cliente"));
                    imp.setDataEmissao(rs.getDate("data_emissao"));
                    imp.setDataVencimento(rs.getDate("data_vencimento"));
                    imp.setObservacao(rs.getString("obs"));

                    result.add(imp);
                }
            }
        }
        return result;
    }
}
