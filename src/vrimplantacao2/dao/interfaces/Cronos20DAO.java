package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vrimplantacao.classe.ConexaoPostgres;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Importacao
 * Sistema WEB - Foi desenvolvido por backup enviado pela software house
 */
public class Cronos20DAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "Cronos20";
    }
    
    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet(Arrays.asList(new OpcaoProduto[]{
            OpcaoProduto.IMPORTAR_MANTER_BALANCA,
            OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
            OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
            OpcaoProduto.MERCADOLOGICO,
            OpcaoProduto.MERCADOLOGICO_PRODUTO,
            OpcaoProduto.PRODUTOS,
            OpcaoProduto.ATIVO,
            OpcaoProduto.DESC_COMPLETA,
            OpcaoProduto.DESC_GONDOLA,
            OpcaoProduto.DESC_REDUZIDA,
            OpcaoProduto.DATA_CADASTRO,
            OpcaoProduto.EAN,
            OpcaoProduto.EAN_EM_BRANCO,
            OpcaoProduto.TIPO_EMBALAGEM_EAN,
            OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
            OpcaoProduto.CUSTO,
            OpcaoProduto.CUSTO_COM_IMPOSTO,
            OpcaoProduto.CUSTO_SEM_IMPOSTO,
            OpcaoProduto.MARGEM,
            OpcaoProduto.PRECO,
            OpcaoProduto.ESTOQUE_MAXIMO,
            OpcaoProduto.ESTOQUE_MINIMO,
            OpcaoProduto.ESTOQUE,
            OpcaoProduto.PESAVEL,
            OpcaoProduto.NCM,
            OpcaoProduto.CEST,
            OpcaoProduto.ICMS,
            OpcaoProduto.PIS_COFINS,
            OpcaoProduto.NATUREZA_RECEITA,
            OpcaoProduto.VALIDADE
        }));
    }
    
    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try(Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "	id,\n" +
                    "	nome_fantasia\n" +
                    "from\n" +
                    "	tioferreira.empresa \n" +
                    "order by\n" +
                    "	id")) {
                while(rs.next()) {
                    result.add(new Estabelecimento(rs.getString("id"), rs.getString("nome_fantasia")));
                }
            }
        }
        return result;
    }
    
    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	pg.id codmercadologico1,\n" +
                    "	coalesce(pg.nome, ps.nome) descmercadologico1,\n" +
                    "	ps.id codmercadologico2,\n" +
                    "	coalesce(ps.nome, pg.nome) descmercadologico2\n" +
                    "from \n" +
                    "	tioferreira.produto_grupo pg\n" +
                    "join\n" +
                    "	tioferreira.produto_subgrupo ps on pg.id = ps.id_grupo\n" +
                    "order by 	\n" +
                    "	pg.id, ps.id")) {
                while (rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setMerc1ID(rs.getString("codmercadologico1"));
                    imp.setMerc1Descricao(rs.getString("descmercadologico1"));
                    imp.setMerc2ID(rs.getString("codmercadologico2"));
                    imp.setMerc2Descricao(rs.getString("descmercadologico2"));
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
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	p.id,\n" +
                    "	u.sigla as unidade,\n" +
                    "	p.gtin as ean,\n" +
                    "	p.codigo_balanca,\n" +
                    "	p.codigo_interno,\n" +
                    "	p.dias_validade validade,\n" +
                    "	p.nome as descricaocompleta,\n" +
                    "	p.descricao_pdv,\n" +
                    "	pg.id mercadologico1,\n" +
                    "	p.id_subgrupo mercadologico2,\n" +
                    "	1 mercadologico3,\n" +
                    "	p.custo_unitario custosemimposto,\n" +
                    "	p.valor_compra custocomimposto,\n" +
                    "	p.markup margem,\n" +
                    "	p.valor_venda as precovenda,\n" +
                    "	pe.quantidade_estoque estoque,\n" +
                    "	p.estoque_maximo,\n" +
                    "	p.estoque_minimo,\n" +
                    "	p.data_cadastro,\n" +
                    "	p.peso,\n" +
                    "	p.ncm as ncm,\n" +
                    "	p.cest,\n" +
                    "	p.excluido,\n" +
                    "	p.inativo,\n" +
                    "	al.aliquota as icms,\n" +
                    "	al.cst_b as cst,\n" +
                    "	al.porcento_bc as reducao\n" +
                    "from \n" +
                    "	tioferreira.produto p\n" +
                    "join tioferreira.empresa_produto pe on pe.id_empresa = " + getLojaOrigem() + " and\n" +
                    "	pe.id_produto = p.id \n" +
                    "left join tioferreira.unidade_produto u on p.id_unidade_produto = u.id\n" +
                    "left join tioferreira.tribut_icms_uf al on p.id_grupo_tributario = al.id_tribut_grupo_tributario \n" +
                    "left join tioferreira.produto_subgrupo ps on p.id_subgrupo = ps.id \n" +
                    "left join tioferreira.produto_grupo pg on ps.id_grupo = pg.id \n" +
                    "where \n" +
                    "	al.id_tribut_operacao_fiscal = 1\n" +
                    "order by \n" +
                    "	p.id"
            )) {
                Map<Integer, ProdutoBalancaVO> balanca = new ProdutoBalancaDAO().getProdutosBalanca();
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("id"));

                    imp.setTipoEmbalagem(rs.getString("unidade"));
                    imp.setSituacaoCadastro(rs.getString("inativo").equals("S") ? 
                            SituacaoCadastro.EXCLUIDO : 
                                SituacaoCadastro.ATIVO);
                    imp.setDescricaoCompleta(rs.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rs.getString("descricao_pdv"));
                    imp.setDescricaoGondola(rs.getString("descricaocompleta"));
                    imp.setDataCadastro(rs.getDate("data_cadastro"));
                    imp.setEan(rs.getString("ean"));
                    imp.setValidade(rs.getInt("validade"));
                    
                    String codigoBal = rs.getString("codigo_balanca");
                    if(codigoBal != null && 
                            !"".equals(codigoBal) &&
                                codigoBal.length() < 7) {
                        imp.seteBalanca(true);
                        imp.setEan(codigoBal);
                    }
                    
                    imp.setPrecovenda(rs.getDouble("precovenda"));
                    imp.setCustoComImposto(rs.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rs.getDouble("custosemimposto"));
                    imp.setMargem(rs.getDouble("margem"));
                    imp.setEstoqueMinimo(rs.getDouble("estoque_minimo"));
                    imp.setEstoqueMaximo(rs.getDouble("estoque_maximo"));
                    imp.setEstoque(rs.getDouble("estoque"));
                    imp.setIcmsCst(rs.getInt("cst"));
                    imp.setIcmsAliqSaida(rs.getDouble("icms"));
                    imp.setIcmsAliqConsumidor(rs.getDouble("icms"));
                    imp.setIcmsAliqEntrada(rs.getDouble("icms"));
                    imp.setIcmsAliqSaidaForaEstado(rs.getDouble("icms"));
                    imp.setIcmsAliqSaidaForaEstadoNF(rs.getDouble("icms"));
                    
                    imp.setNcm(rs.getString("ncm"));
                    imp.setCest(rs.getString("cest"));
                    imp.setCodMercadologico1(rs.getString("mercadologico1"));
                    imp.setCodMercadologico2(rs.getString("mercadologico2"));
                    imp.setCodMercadologico3(rs.getString("mercadologico3"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	p.id,\n" +
                    "	u.sigla as unidade,\n" +
                    "	p.gtin as ean\n" +
                    "from \n" +
                    "	tioferreira.produto p\n" +
                    "join tioferreira.empresa_produto pe on pe.id_empresa = 1 and \n" +
                    "	pe.id_produto = p.id \n" +
                    "left join tioferreira.unidade_produto u on p.id_unidade_produto = u.id\n" +
                    "where \n" +
                    "	p.gtin is not null and p.gtin != ''\n" +
                    "union all \n" +
                    "select \n" +
                    "	p.id,\n" +
                    "	u.sigla as unidade,\n" +
                    "	p.codigo_interno ean\n" +
                    "from \n" +
                    "	tioferreira.produto p\n" +
                    "join tioferreira.empresa_produto pe on pe.id_empresa = 1 and \n" +
                    "	pe.id_produto = p.id \n" +
                    "left join tioferreira.unidade_produto u on p.id_unidade_produto = u.id\n" +
                    "where \n" +
                    "	p.codigo_interno is not null and p.codigo_interno != ''")) {
                while(rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("id"));
                    imp.setEan(rs.getString("ean"));
                    imp.setTipoEmbalagem(rs.getString("unidade"));
                    
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
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n" +
                    "    f.id,\n" +
                    "    f.id_pessoa,\n" +
                    "    f.id_atividade_for_cli,\n" +
                    "    f.id_situacao_for_cli,\n" +
                    "    f.desde,\n" +
                    "    f.optante_simples_nacional,\n" +
                    "    f.localizacao,\n" +
                    "    f.data_cadastro,\n" +
                    "    f.sofre_retencao,\n" +
                    "    f.cheque_nominal_a,\n" +
                    "    f.observacao,\n" +
                    "    f.conta_remetente,\n" +
                    "    f.prazo_medio_entrega,\n" +
                    "    f.gera_faturamento,\n" +
                    "    f.num_dias_primeiro_vencimento,\n" +
                    "    f.num_dias_intervalo,\n" +
                    "    f.quantidade_parcelas,\n" +
                    "    e.logradouro,\n" +
                    "    e.numero,\n" +
                    "    e.complemento,\n" +
                    "    e.bairro,\n" +
                    "    e.cidade,\n" +
                    "    e.cep,\n" +
                    "    e.municipio_ibge,\n" +
                    "    e.uf,\n" +
                    "    e.fone,\n" +
                    "    p.nome,\n" +
                    "    p.tipo,\n" +
                    "    p.email,\n" +
                    "    p.site,\n" +
                    "    pf.cpf AS cpf_cnpj\n" +
                    "   FROM tioferreira.pessoa p\n" +
                    "     JOIN tioferreira.pessoa_fisica pf ON pf.id_pessoa = p.id\n" +
                    "     JOIN tioferreira.fornecedor f ON f.id_pessoa = p.id\n" +
                    "     JOIN tioferreira.pessoa_endereco e ON e.id_pessoa = p.id\n" +
                    "  WHERE p.fornecedor = 'S'::bpchar AND e.principal = 'S'::bpchar\n" +
                    "UNION\n" +
                    " SELECT\n" +
                    " 	 f.id,\n" +
                    "    f.id_pessoa,\n" +
                    "    f.id_atividade_for_cli,\n" +
                    "    f.id_situacao_for_cli,\n" +
                    "    f.desde,\n" +
                    "    f.optante_simples_nacional,\n" +
                    "    f.localizacao,\n" +
                    "    f.data_cadastro,\n" +
                    "    f.sofre_retencao,\n" +
                    "    f.cheque_nominal_a,\n" +
                    "    f.observacao,\n" +
                    "    f.conta_remetente,\n" +
                    "    f.prazo_medio_entrega,\n" +
                    "    f.gera_faturamento,\n" +
                    "    f.num_dias_primeiro_vencimento,\n" +
                    "    f.num_dias_intervalo,\n" +
                    "    f.quantidade_parcelas,\n" +
                    "    e.logradouro,\n" +
                    "    e.numero,\n" +
                    "    e.complemento,\n" +
                    "    e.bairro,\n" +
                    "    e.cidade,\n" +
                    "    e.cep,\n" +
                    "    e.municipio_ibge,\n" +
                    "    e.uf,\n" +
                    "    e.fone,\n" +
                    "    p.nome,\n" +
                    "    p.tipo,\n" +
                    "    p.email,\n" +
                    "    p.site,\n" +
                    "    pj.cnpj AS cpf_cnpj\n" +
                    "   FROM tioferreira.pessoa p\n" +
                    "     JOIN tioferreira.pessoa_juridica pj ON pj.id_pessoa = p.id\n" +
                    "     JOIN tioferreira.fornecedor f ON f.id_pessoa = p.id\n" +
                    "     JOIN tioferreira.pessoa_endereco e ON e.id_pessoa = p.id\n" +
                    "  WHERE p.fornecedor = 'S'::bpchar AND e.principal = 'S'::bpchar;")) {
                while (rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("id"));
                    imp.setRazao(rs.getString("nome"));
                    imp.setFantasia(rs.getString("nome"));
                    imp.setCnpj_cpf(rs.getString("cpf_cnpj"));
                    imp.setEndereco(rs.getString("logradouro"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setIbge_municipio(rs.getInt("municipio_ibge"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));
                    imp.setTel_principal(rs.getString("fone"));
                    if ((rs.getString("email") != null)
                            && (!"".equals(rs.getString("email")))) {
                        imp.addContato("Email", null, null, TipoContato.COMERCIAL, rs.getString("email"));
                    }
                    imp.setDatacadastro(rs.getDate("data_cadastro"));
                    imp.setAtivo((rs.getInt("id_situacao_for_cli") == 1));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "	id_fornecedor,\n" +
                    "	id_produto,\n" +
                    "	codigo_fornecedor_produto codigoexterno\n" +
                    "from \n" +
                    "	tioferreira.fornecedor_produto")) {
                while(rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setIdProduto(rs.getString("id_produto"));
                    imp.setIdFornecedor(rs.getString("id_fornecedor"));
                    imp.setCodigoExterno(rs.getString("codigoexterno"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "SELECT c.id,\n" +
                    "    c.id_operacao_fiscal,\n" +
                    "    c.id_pessoa,\n" +
                    "    c.id_atividade_for_cli,\n" +
                    "    c.id_situacao_for_cli,\n" +
                    "    empresa.id AS id_empresa,\n" +
                    "    c.desde,\n" +
                    "    c.data_cadastro,\n" +
                    "    c.observacao,\n" +
                    "    c.conta_tomador,\n" +
                    "    c.gera_financeiro,\n" +
                    "    c.indicador_preco,\n" +
                    "    c.porcento_desconto,\n" +
                    "    c.forma_desconto,\n" +
                    "    c.limite_credito,\n" +
                    "    c.tipo_frete,\n" +
                    "    e.logradouro,\n" +
                    "    e.numero,\n" +
                    "    e.complemento,\n" +
                    "    e.bairro,\n" +
                    "    e.cidade,\n" +
                    "    e.cep,\n" +
                    "    e.municipio_ibge,\n" +
                    "    e.uf,\n" +
                    "    e.fone,\n" +
                    "    p.nome,\n" +
                    "    p.tipo,\n" +
                    "    p.email,\n" +
                    "    p.site,\n" +
                    "    pf.cpf AS cpf_cnpj,\n" +
                    "    pf.rg AS rg_ie,\n" +
                    "    empresa.razao_social AS empresa_razao_social,\n" +
                    "    empresa.nome_fantasia AS empresa_nome_fantasia,\n" +
                    "    empresa.cnpj AS empresa_cnpj,\n" +
                    "    empresa.inscricao_estadual AS empresa_inscricao_estadual,\n" +
                    "    empresa.imagem_logotipo AS empresa_imagem_logotipo,\n" +
                    "    empresa_endereco.logradouro AS empresa_endereco_logradouro,\n" +
                    "    empresa_endereco.numero AS empresa_endereco_numero,\n" +
                    "    empresa_endereco.complemento AS empresa_endereco_complemento,\n" +
                    "    empresa_endereco.bairro AS empresa_endereco_bairro,\n" +
                    "    empresa_endereco.cidade AS empresa_endereco_cidade,\n" +
                    "    empresa_endereco.cep AS empresa_endereco_cep,\n" +
                    "    empresa_endereco.fone AS empresa_endereco_fone,\n" +
                    "    empresa_endereco.uf AS empresa_endereco_uf,\n" +
                    "    empresa.email AS empresa_email\n" +
                    "   FROM tioferreira.pessoa p\n" +
                    "     JOIN tioferreira.pessoa_fisica pf ON pf.id_pessoa = p.id\n" +
                    "     JOIN tioferreira.cliente c ON c.id_pessoa = p.id\n" +
                    "     JOIN tioferreira.pessoa_endereco e ON e.id_pessoa = p.id\n" +
                    "     JOIN tioferreira.empresa_pessoa ep ON ep.id_pessoa = p.id\n" +
                    "     JOIN tioferreira.empresa empresa ON ep.id_empresa = empresa.id\n" +
                    "     JOIN tioferreira.empresa_endereco empresa_endereco ON empresa_endereco.id_empresa = empresa.id\n" +
                    "  WHERE empresa_endereco.principal = 'S'::bpchar AND p.cliente = 'S'::bpchar AND e.principal = 'S'::bpchar and \n" +
                    "  	empresa.id = " + getLojaOrigem() + "\n" +
                    "UNION\n" +
                    " SELECT c.id,\n" +
                    "    c.id_operacao_fiscal,\n" +
                    "    c.id_pessoa,\n" +
                    "    c.id_atividade_for_cli,\n" +
                    "    c.id_situacao_for_cli,\n" +
                    "    empresa.id AS id_empresa,\n" +
                    "    c.desde,\n" +
                    "    c.data_cadastro,\n" +
                    "    c.observacao,\n" +
                    "    c.conta_tomador,\n" +
                    "    c.gera_financeiro,\n" +
                    "    c.indicador_preco,\n" +
                    "    c.porcento_desconto,\n" +
                    "    c.forma_desconto,\n" +
                    "    c.limite_credito,\n" +
                    "    c.tipo_frete,\n" +
                    "    e.logradouro,\n" +
                    "    e.numero,\n" +
                    "    e.complemento,\n" +
                    "    e.bairro,\n" +
                    "    e.cidade,\n" +
                    "    e.cep,\n" +
                    "    e.municipio_ibge,\n" +
                    "    e.uf,\n" +
                    "    e.fone,\n" +
                    "    p.nome,\n" +
                    "    p.tipo,\n" +
                    "    p.email,\n" +
                    "    p.site,\n" +
                    "    pj.cnpj AS cpf_cnpj,\n" +
                    "    pj.inscricao_estadual AS rg_ie,\n" +
                    "    empresa.razao_social AS empresa_razao_social,\n" +
                    "    empresa.nome_fantasia AS empresa_nome_fantasia,\n" +
                    "    empresa.cnpj AS empresa_cnpj,\n" +
                    "    empresa.inscricao_estadual AS empresa_inscricao_estadual,\n" +
                    "    empresa.imagem_logotipo AS empresa_imagem_logotipo,\n" +
                    "    empresa_endereco.logradouro AS empresa_endereco_logradouro,\n" +
                    "    empresa_endereco.numero AS empresa_endereco_numero,\n" +
                    "    empresa_endereco.complemento AS empresa_endereco_complemento,\n" +
                    "    empresa_endereco.bairro AS empresa_endereco_bairro,\n" +
                    "    empresa_endereco.cidade AS empresa_endereco_cidade,\n" +
                    "    empresa_endereco.cep AS empresa_endereco_cep,\n" +
                    "    empresa_endereco.fone AS empresa_endereco_fone,\n" +
                    "    empresa_endereco.uf AS empresa_endereco_uf,\n" +
                    "    empresa.email AS empresa_email\n" +
                    " FROM tioferreira.pessoa p\n" +
                    "     JOIN tioferreira.pessoa_juridica pj ON pj.id_pessoa = p.id\n" +
                    "     JOIN tioferreira.cliente c ON c.id_pessoa = p.id\n" +
                    "     JOIN tioferreira.pessoa_endereco e ON e.id_pessoa = p.id\n" +
                    "     JOIN tioferreira.empresa_pessoa ep ON ep.id_pessoa = p.id\n" +
                    "     JOIN tioferreira.empresa empresa ON ep.id_empresa = empresa.id\n" +
                    "     JOIN tioferreira.empresa_endereco empresa_endereco ON empresa_endereco.id_empresa = empresa.id\n" +
                    "  WHERE empresa_endereco.principal = 'S'::bpchar AND p.cliente = 'S'::bpchar AND e.principal = 'S'::bpchar and \n" +
                    "  	empresa.id = " + getLojaOrigem())) {
                while(rs.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    
                    imp.setId(rs.getString("id"));
                    imp.setRazao(rs.getString("nome"));
                    imp.setCnpj(rs.getString("cpf_cnpj"));
                    imp.setInscricaoestadual(rs.getString("rg_ie"));
                    imp.setFantasia(rs.getString("nome"));
                    imp.setAtivo(rs.getInt("id_situacao_for_cli") == 1);
                    imp.setDataCadastro(rs.getDate("desde"));
                    imp.setObservacao(rs.getString("observacao") != null ? rs.getString("observacao") : "");
                    if(rs.getString("gera_financeiro") != null && rs.getString("gera_financeiro").equals("S")) {
                        imp.setPermiteCreditoRotativo(true);
                        imp.setPermiteCheque(true);
                    }
                    imp.setValorLimite(rs.getDouble("limite_credito"));
                    imp.setEndereco(rs.getString("logradouro"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setCep(rs.getString("cep"));
                    imp.setMunicipioIBGE(rs.getString("municipio_ibge"));
                    imp.setUf(rs.getString("uf"));
                    imp.setTelefone(rs.getString("fone"));
                    imp.setEmail(rs.getString("email"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "SELECT \n" +
                    "	 lr.id,\n" +
                    "    c.id AS id_cliente,\n" +
                    "    lr.id_empresa,\n" +
                    "    p.nome,\n" +
                    "    pf.cpf AS cpf_cnpj,\n" +
                    "    lr.data_lancamento,\n" +
                    "    lr.valor_a_receber AS valor_lancamento,\n" +
                    "    lr.quantidade_parcela,\n" +
                    "    lr.numero_documento,\n" +
                    "    pr.numero_parcela,\n" +
                    "    pr.data_vencimento,\n" +
                    "    pr.valor AS valor_parcela,\n" +
                    "    pr.taxa_juro,\n" +
                    "    pr.valor_juro,\n" +
                    "    pr.taxa_multa,\n" +
                    "    pr.valor_multa,\n" +
                    "    pr.taxa_desconto,\n" +
                    "    pr.valor_desconto,\n" +
                    "    ( SELECT COALESCE(sum(fin_parcela_recebimento.valor_recebido), 0::numeric) AS \"coalesce\"\n" +
                    "           FROM tioferreira.fin_parcela_recebimento\n" +
                    "          WHERE fin_parcela_recebimento.id_fin_parcela_receber = pr.id) AS valor_recebido,\n" +
                    "    ( SELECT COALESCE(sum(fin_parcela_recebimento.valor_juro), 0::numeric) AS \"coalesce\"\n" +
                    "           FROM tioferreira.fin_parcela_recebimento\n" +
                    "          WHERE fin_parcela_recebimento.id_fin_parcela_receber = pr.id) AS juros_recebido,\n" +
                    "    ( SELECT COALESCE(sum(fin_parcela_recebimento.valor_multa), 0::numeric) AS \"coalesce\"\n" +
                    "           FROM tioferreira.fin_parcela_recebimento\n" +
                    "          WHERE fin_parcela_recebimento.id_fin_parcela_receber = pr.id) AS multa_recebido,\n" +
                    "    s.id AS id_status_parcela,\n" +
                    "    s.situacao AS situacao_parcela,\n" +
                    "    s.descricao AS descricao_situacao_parcela,\n" +
                    "    cc.id AS id_conta_caixa,\n" +
                    "    cc.nome AS nome_conta_caixa,\n" +
                    "    cc.limite_cobranca_juro,\n" +
                    "    pr.id AS id_parcela_receber,\n" +
                    "    doc.sigla_documento\n" +
                    "   FROM tioferreira.fin_lancamento_receber lr\n" +
                    "     JOIN tioferreira.fin_parcela_receber pr ON pr.id_fin_lancamento_receber = lr.id\n" +
                    "     JOIN tioferreira.fin_status_parcela s ON pr.id_fin_status_parcela = s.id\n" +
                    "     JOIN tioferreira.conta_caixa cc ON pr.id_conta_caixa = cc.id\n" +
                    "     JOIN tioferreira.fin_documento_origem doc ON lr.id_fin_documento_origem = doc.id\n" +
                    "     JOIN tioferreira.cliente c ON lr.id_cliente = c.id\n" +
                    "     JOIN tioferreira.pessoa p ON c.id_pessoa = p.id\n" +
                    "     JOIN tioferreira.pessoa_fisica pf ON pf.id_pessoa = p.id\n" +
                    "   where \n" +
                    "   	s.situacao = '01' and \n" +
                    "   	lr.id_empresa = " + getLojaOrigem() + "\n" +
                    "UNION\n" +
                    " SELECT lr.id,\n" +
                    "    c.id AS id_cliente,\n" +
                    "    lr.id_empresa,\n" +
                    "    p.nome,\n" +
                    "    pj.cnpj AS cpf_cnpj,\n" +
                    "    lr.data_lancamento,\n" +
                    "    lr.valor_a_receber AS valor_lancamento,\n" +
                    "    lr.quantidade_parcela,\n" +
                    "    lr.numero_documento,\n" +
                    "    pr.numero_parcela,\n" +
                    "    pr.data_vencimento,\n" +
                    "    pr.valor AS valor_parcela,\n" +
                    "    pr.taxa_juro,\n" +
                    "    pr.valor_juro,\n" +
                    "    pr.taxa_multa,\n" +
                    "    pr.valor_multa,\n" +
                    "    pr.taxa_desconto,\n" +
                    "    pr.valor_desconto,\n" +
                    "    ( SELECT COALESCE(sum(fin_parcela_recebimento.valor_recebido), 0::numeric) AS \"coalesce\"\n" +
                    "           FROM tioferreira.fin_parcela_recebimento\n" +
                    "          WHERE fin_parcela_recebimento.id_fin_parcela_receber = pr.id) AS valor_recebido,\n" +
                    "    ( SELECT COALESCE(sum(fin_parcela_recebimento.valor_juro), 0::numeric) AS \"coalesce\"\n" +
                    "           FROM tioferreira.fin_parcela_recebimento\n" +
                    "          WHERE fin_parcela_recebimento.id_fin_parcela_receber = pr.id) AS juros_recebido,\n" +
                    "    ( SELECT COALESCE(sum(fin_parcela_recebimento.valor_multa), 0::numeric) AS \"coalesce\"\n" +
                    "           FROM tioferreira.fin_parcela_recebimento\n" +
                    "          WHERE fin_parcela_recebimento.id_fin_parcela_receber = pr.id) AS multa_recebido,\n" +
                    "    s.id AS id_status_parcela,\n" +
                    "    s.situacao AS situacao_parcela,\n" +
                    "    s.descricao AS descricao_situacao_parcela,\n" +
                    "    cc.id AS id_conta_caixa,\n" +
                    "    cc.nome AS nome_conta_caixa,\n" +
                    "    cc.limite_cobranca_juro,\n" +
                    "    pr.id AS id_parcela_receber,\n" +
                    "    doc.sigla_documento\n" +
                    "   FROM tioferreira.fin_lancamento_receber lr\n" +
                    "     JOIN tioferreira.fin_parcela_receber pr ON pr.id_fin_lancamento_receber = lr.id\n" +
                    "     JOIN tioferreira.fin_status_parcela s ON pr.id_fin_status_parcela = s.id\n" +
                    "     JOIN tioferreira.conta_caixa cc ON pr.id_conta_caixa = cc.id\n" +
                    "     JOIN tioferreira.fin_documento_origem doc ON lr.id_fin_documento_origem = doc.id\n" +
                    "     JOIN tioferreira.cliente c ON lr.id_cliente = c.id\n" +
                    "     JOIN tioferreira.pessoa p ON c.id_pessoa = p.id\n" +
                    "     JOIN tioferreira.pessoa_juridica pj ON pj.id_pessoa = p.id\n" +
                    "   where \n" +
                    "   	s.situacao = '01' and \n" +
                    "   	lr.id_empresa = " + getLojaOrigem())) {
                while(rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    
                    imp.setId(rs.getString("id"));
                    imp.setIdCliente(rs.getString("id_cliente"));
                    imp.setCnpjCliente(rs.getString("cpf_cnpj"));
                    imp.setDataEmissao(rs.getDate("data_lancamento"));
                    imp.setParcela(rs.getInt("numero_parcela"));
                    imp.setValor(rs.getDouble("valor_parcela"));
                    imp.setDataVencimento(rs.getDate("data_vencimento"));
                    imp.setNumeroCupom(rs.getString("numero_documento"));
                    imp.setObservacao(rs.getString("numero_documento"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
