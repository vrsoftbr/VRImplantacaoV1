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
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2_5.dao.conexao.ConexaoFirebird;

/**
 *
 * @author Alan
 */
public class VivaSistemasDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "VivaSistemas";
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
                OpcaoCliente.DATA_NASCIMENTO,
                OpcaoCliente.VENCIMENTO_ROTATIVO,
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
                    + "	TRCA_PK id,\n"
                    + "	REPLACE(REPLACE(TRCA_NOME, 'ALIQ. ',''),'REDUZIDA','RED') descricao,\n"
                    + "	CASE\n"
                    + "		WHEN TRCA_NOME LIKE '%RED%' THEN 20\n"
                    + "		WHEN TRCA_NOME LIKE '%ISENT%' THEN 40\n"
                    + "		WHEN TRCA_NOME LIKE '%RET%' THEN 60\n"
                    + "		ELSE 00\n"
                    + " END cst_saida,\n"
                    + "	CASE \n"
                    + "		WHEN TRCA_NOME LIKE '%03,%' THEN '3'\n"
                    + "		WHEN TRCA_NOME LIKE '%07,%' THEN '7'\n"
                    + "		WHEN TRCA_NOME LIKE '%09,%' THEN '9'\n"
                    + "		WHEN TRCA_NOME LIKE '%12,%' THEN '12'\n"
                    + "		WHEN TRCA_NOME LIKE '%17,%' THEN '17'\n"
                    + "		WHEN TRCA_NOME LIKE '%19,%' THEN '19'\n"
                    + "		WHEN TRCA_NOME LIKE '%25,%' THEN '25'\n"
                    + "		WHEN TRCA_NOME LIKE '%27,%' THEN '27'\n"
                    + "		ELSE '0'\n"
                    + "	END aliq_saida,\n"
                    + "	CASE\n"
                    + "		WHEN TRCA_NOME = 'ALIQ. 12% REDUZIDA PARA 3%' THEN '58.33'\n"
                    + "		WHEN TRCA_NOME = 'ALIQ. 12% REDUZIDA PARA 7%' THEN '25'\n"
                    + "		WHEN TRCA_NOME = 'ALIQ. 17% REDUZIDA PARA 7%' THEN '41.18'\n"
                    + "		WHEN TRCA_NOME = 'ALIQ. 17% REDUZIDA PARA 9%' THEN '52.94'\n"
                    + "		WHEN TRCA_NOME = 'ALIQ. 17% REDUZIDA PARA 12%' THEN '70.59'\n"
                    + "		END red_saida\n"
                    + "FROM\n"
                    + "	FISC_TRIBUTACAO_CATEGORIA"
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(
                            rs.getString("id"),
                            rs.getString("descricao"),
                            rs.getInt("cst_saida"),
                            rs.getDouble("aliq_saida"),
                            rs.getDouble("red_saida"))
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
                    "SELECT DISTINCT \n"
                    + "	m1.GENE_PK merc1,\n"
                    + "	m1.GENE_DESCRICAO descmerc1,\n"
                    + "	m2.EGRU_PK merc2,\n"
                    + "	m2.EGRU_DESCRICAO descmerc2,\n"
                    + "	m3.ESGR_PK merc3,\n"
                    + "	m3.ESGR_DESCRICAO descmerc3\n"
                    + "FROM\n"
                    + "	ESTO_PRODUTOS p\n"
                    + "	JOIN ESTO_GENERO m1 ON p.GENE_PK = m1.GENE_PK\n"
                    + "	JOIN ESTO_GRUPO m2 ON m2.egru_pk = p.egru_pk\n"
                    + "	JOIN ESTO_SUBGRUPO m3 ON m2.EGRU_PK = m3.EGRU_PK\n"
                    + "ORDER BY 1,3,5"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());

                    imp.setMerc1ID(rst.getString("merc1"));
                    imp.setMerc1Descricao(rst.getString("descmerc1"));
                    imp.setMerc2ID(rst.getString("merc2"));
                    imp.setMerc2Descricao(rst.getString("descmerc2"));
                    imp.setMerc3ID(rst.getString("merc3"));
                    imp.setMerc3Descricao(rst.getString("descmerc3"));

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
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	ESFA_PK id_familia,\n"
                    + "	ESFA_DESCRICAO familia\n"
                    + "FROM\n"
                    + "	ESTO_FAMILIA\n"
                    + "ORDER BY ESFA_PK"
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

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	prod_pk id_produto,\n"
                    + "	prod_codigo_unitario ean,\n"
                    + "	ean.unid_fator_conversao qtdembalagem,\n"
                    + "	un.unid_nome_sigla tipo_embalagem\n"
                    + "FROM\n"
                    + "	ESTO_PRODUTOS ean\n"
                    + "	JOIN esto_unidades un ON ean.UNID_PK = un.UNID_PK"
            )) {
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rs.getString("id_produto"));
                    imp.setEan(rs.getString("ean"));
                    imp.setQtdEmbalagem(rs.getInt("qtdembalagem"));
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
                    + "	PROD_PK id,\n"
                    + "	p.PROD_CODIGO_PERSONALIZADO codigo,\n"
                    + "	p.PROD_CODIGO_UNITARIO ean,\n"
                    + "	CASE WHEN p.PROD_BALANCA = 'Sim' THEN 1 ELSE 0 END e_balanca,\n"
                    + "	p.PROD_DT_CADASTRO data_cad,\n"
                    + "	p.PROD_DT_ULT_ALTERACAO data_alt,\n"
                    + "	PROD_DESCRICAO_COMPLETA desc_completa,\n"
                    + "	PROD_DESCRICAO_ABREVIADA desc_reduzida,\n"
                    + "	GENE_PK merc1,\n"
                    + "	EGRU_PK merc2,\n"
                    + "	ESGR_PK merc3,\n"
                    + "	n.ENCM_CODIGO ncm,\n"
                    + "	p.CEST_PK cest,\n"
                    + "	prod_status_registro ativo,\n"
                    + "	UNID_NOME_SIGLA tipo_emb,\n"
                    + "	unid_fator_conversao qtde_emb,\n"
                    + "	p.PROD_PESO_BRUTO peso_bruto,\n"
                    + "	p.PROD_PESO_LIQUIDO peso_liquido,\n"
                    + "	p.PROD_ESTOQUE_MINIMO est_min,\n"
                    + "	p.PROD_ESTOQUE_MAXIMO est_max,\n"
                    + "	p.PROD_ESTOQUE_ATUAL estoque,\n"
                    + "	p.PROD_VLR_CUSTO_FINAL precocusto,\n"
                    + "	p.PROD_VLR_VAREJO precovenda,\n"
                    + "	p.TRCA_PK id_icms,\n"
                    + "	PROD_CST_PIS pis_cofins,\n"
                    + "	p.PROD_CODIGO_NATUREZA_PISCOFINS nat_rec\n"
                    + "FROM\n"
                    + "	ESTO_PRODUTOS p\n"
                    + "LEFT JOIN ESTO_NCM n ON\n"
                    + "	p.ENCM_PK = n.ENCM_PK\n"
                    + "WHERE\n"
                    + "	EMPR_PK = " + getLojaOrigem()
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

                    imp.setDescricaoCompleta(rst.getString("desc_completa"));
                    imp.setDescricaoReduzida(rst.getString("desc_reduzida"));
                    imp.setDescricaoGondola(rst.getString("desc_reduzida"));
                    imp.setTipoEmbalagem(rst.getString("tipo_emb"));
                    imp.setQtdEmbalagem(rst.getInt("qtde_emb"));
                    imp.seteBalanca(rst.getBoolean("e_balanca"));

                    imp.setCustoComImposto(rst.getDouble("precocusto"));
                    imp.setCustoSemImposto(rst.getDouble("precocusto"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));

                    imp.setCodMercadologico1(rst.getString("merc1"));
                    imp.setCodMercadologico2(rst.getString("merc2"));
                    imp.setCodMercadologico3(rst.getString("merc3"));
                    
                    imp.setSituacaoCadastro(rst.getInt("ativo"));
                    imp.setDataCadastro(rst.getDate("data_cad"));
                    imp.setDataAlteracao(rst.getDate("data_alt"));
                    imp.setEstoqueMinimo(rst.getDouble("est_min"));
                    imp.setEstoqueMaximo(rst.getDouble("est_max"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setPesoBruto(rst.getDouble("peso_bruto"));
                    imp.setPesoLiquido(rst.getDouble("peso_liquido"));

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
//                    imp.setPiscofinsCstCredito(rst.getString("piscof_credito"));
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
                    + "	c.CONT_pk id,\n"
                    + "	cont_nome_razao razao,\n"
                    + "	cont_cpf_cnpj cpf_cnpj,\n"
                    + "	cont_ie rg_ie,\n"
                    + "	cont_dt_cadastro data_cad,\n"
                    + "	cont_status_registro ativo,\n"
                    + "	ende_nome_logradouro endereco,\n"
                    + "	ende_complemento complemento,\n"
                    + "	ende_numero numero,\n"
                    + "	ende_bairro bairro,\n"
                    + "	ende_municipio cidade,\n"
                    + "	ende_uf uf,\n"
                    + "	ende_cep cep,\n"
                    + "	REPLACE(t.tele_numero_ddd,'0','')||t.tele_numero_telefone telefone,\n"
                    + "	cont_observacao obs\n"
                    + "FROM\n"
                    + "	CDTR_CONTATOS c\n"
                    + "	LEFT JOIN CDTR_ENDERECOS e ON c.cont_pk = e.cont_pk\n"
                    + "	LEFT JOIN CDTR_TELEFONES t ON c.cont_pk = t.cont_pk\n"
                    + "WHERE\n"
                    + "	cont_tipo_contato LIKE '%For%'"
            //+ "	OR cont_tipo_contato LIKE '%Tran%'"
            )) {
                while (rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("razao"));
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
                    imp.setDatacadastro(rs.getDate("data_cad"));
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
                    "SELECT\n"
                    + "	cont_pk id_fornecedor,\n"
                    + "	prod_pk id_produto,\n"
                    + "	prfo_cod_produto_fornecedor cod_externo,\n"
                    + "	1 qtde_emb\n"
                    + "FROM\n"
                    + "	ESTO_PRODUTOS_FORNECEDOR\n"
                    + "ORDER BY 1,2"
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

    @Override
    public List<ContaPagarIMP> getContasPagar() throws Exception {
        List<ContaPagarIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	paga_pk id,\n"
                    + "	cont_pk id_fornecedor,\n"
                    + "	paga_numero_fiscal documento,\n"
                    + "	paga_dt_emissao emissao,\n"
                    + "	paga_dt_vencimento vencimento,\n"
                    + "	paga_vlr_documento valor,\n"
                    + "	paga_descricao||'-'||paga_numero observacao\n"
                    + "FROM\n"
                    + "	FNCR_PAGAR cp\n"
                    + "WHERE\n"
                    + "	empr_pk = " + getLojaOrigem() + "\n"
                    + "	AND paga_dt_pagamento IS NULL"
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

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	c.CONT_pk id,\n"
                    + "	cont_nome_razao razao,\n"
                    + "	cont_cpf_cnpj cpf_cnpj,\n"
                    + "	CASE WHEN cont_rg = '' THEN cont_ie END rg_ie,\n"
                    + "	ende_nome_logradouro endereco,\n"
                    + "	ende_complemento complemento,\n"
                    + "	ende_numero numero,\n"
                    + "	ende_bairro bairro,\n"
                    + "	ende_municipio cidade,\n"
                    + "	ende_uf uf,\n"
                    + "	ende_cep cep,\n"
                    + "	cont_dt_cadastro data_cad,\n"
                    + "	cont_status_registro ativo,\n"
                    + "	REPLACE(t.tele_numero_ddd,'0','')||t.tele_numero_telefone telefone,\n"
                    + "	cont_observacao obs\n"
                    + "FROM\n"
                    + "	CDTR_CONTATOS c\n"
                    + "	LEFT JOIN CDTR_ENDERECOS e ON c.cont_pk = e.cont_pk\n"
                    + "	LEFT JOIN CDTR_TELEFONES t ON c.cont_pk = t.cont_pk\n"
                    + "WHERE\n"
                    + "	cont_tipo_contato LIKE '%Cli%'"
            )) {
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("razao"));
                    imp.setCnpj(rs.getString("cpf_cnpj"));
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
                    imp.setTelefone(rs.getString("telefone"));
                    imp.setObservacao(rs.getString("obs"));

                    result.add(imp);
                }
            }
        }
        return result;
    }
}
