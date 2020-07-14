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
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Importacao
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
            OpcaoProduto.ATACADO,
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

}
