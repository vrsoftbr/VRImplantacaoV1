package vrimplantacao2.dao.interfaces;

import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoMySQL;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Importacao
 */
public class GTechDAO extends InterfaceDAO implements MapaTributoProvider {

    /*
     Para localizar a senha do banco de dados do GTech ou G3 Informática,
     localizar a classe GTechEncriptDAO, passar o texto encriptografado no método Main 
     para retornar a senha do atual banco de dados.
     */
    @Override
    public String getSistema() {
        return "GTech";
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT \n"
                    + "	 aliquota id,\n"
                    + "    descricao\n"
                    + "FROM \n"
                    + "	aliquotas_icms\n"
                    + "order by\n"
                    + "	2")) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(rs.getString("id"), rs.getString("descricao")));
                }
            }
        }
        return result;
    }
    
    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try(Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select 1 id, 'Mercado e Sacolao da Economia' razao")) {
                while(rs.next()) {
                    result.add(new Estabelecimento(rs.getString("id"), rs.getString("razao")));
                }
            }
        }
        return result;
    }
    
    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try(Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "	 p.id,\n" +
                    "    p.codigo_interno,\n" +
                    "    p.ean,\n" +
                    "    p.descricao_pdv descricaocompleta,\n" +
                    "    p.id_grupo,\n" +
                    "    p.valor_custo,\n" +
                    "    p.valor_venda,\n" +
                    "    p.data_cadastro,\n" +
                    "    p.estoque_max,\n" +
                    "    p.estoque_min,\n" +
                    "    p.qtd_estoque estoque,\n" +
                    "    un.nome unidade,\n" +
                    "    p.ncm,\n" +
                    "    p.aliquota_icms_dentro icmsdebito,\n" +
                    "    p.cod_cst_dentro cstdebito,\n" +
                    "    p.reducao_bc_dentro icmsreducaodebito,\n" +
                    "    coalesce(p.balanca_integrada, 0) isBalanca,\n" +
                    "    p.excluido,\n" +
                    "    p.desativado,\n" +
                    "    p.cod_nat_rec naturezareceita,\n" +
                    "    p.cest,\n" +
                    "    ce.cst cofinsdebito,\n" +
                    "    cs.cst cofinscredito,\n" +
                    "    p.ECF_ICMS_ST idaliquota\n" +
                    "from\n" +
                    "	produto p\n" +
                    "left join unidade_produto un on (p.id_unidade_produto = un.id)\n" +
                    "left join grupocofins ce on (p.id_grupo_pis_saida = ce.id)\n" +
                    "left join grupocofins cs on (p.id_grupo_pis_entrada = cs.id)\n" +
                    "order by\n" +
                    "	p.id")) {
                while(rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("id"));
                    imp.setEan(rs.getString("ean"));
                    imp.setDescricaoCompleta(rs.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setCustoComImposto(rs.getDouble("valor_custo"));
                    imp.setCustoSemImposto(rs.getDouble("valor_custo"));
                    imp.setPrecovenda(rs.getDouble("valor_venda"));
                    imp.setDataCadastro(rs.getDate("data_cadastro"));
                    imp.setEstoqueMaximo(rs.getDouble("estoque_max"));
                    imp.setEstoqueMinimo(rs.getDouble("estoque_min"));
                    imp.setEstoque(rs.getDouble("estoque"));
                    imp.setTipoEmbalagem(rs.getString("unidade"));
                    imp.setNcm(rs.getString("ncm"));
                    if(rs.getInt("isBalanca") == 1) {
                        imp.seteBalanca(true);
                        imp.setEan(imp.getImportId());
                    }
                    imp.setSituacaoCadastro(rs.getInt("desativado") == 1 ? SituacaoCadastro.EXCLUIDO : SituacaoCadastro.ATIVO);
                    imp.setPiscofinsNaturezaReceita(rs.getString("naturezareceita"));
                    imp.setCest(rs.getString("cest"));
                    imp.setPiscofinsCstCredito(rs.getString("cofinscredito"));
                    imp.setPiscofinsCstDebito(rs.getString("cofinsdebito"));
                    imp.setIcmsDebitoId(rs.getString("idaliquota"));
                    imp.setIcmsCreditoId(rs.getString("idaliquota"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
    
    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        try(Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "	 id_fornecedor id,\n" +
                    "    razao_social razao,\n" +
                    "    nome_fantasia fantasia,\n" +
                    "    bairro,\n" +
                    "    cep,\n" +
                    "    municipio,\n" +
                    "    codigo_municipio municipioibge,\n" +
                    "    complemento,\n" +
                    "    numero,\n" +
                    "    endereco,\n" +
                    "    uf,\n" +
                    "    numero_documento cnpj,\n" +
                    "    ie,\n" +
                    "    contato,\n" +
                    "    email,\n" +
                    "    fax,\n" +
                    "    telefone,\n" +
                    "    ativo,\n" +
                    "    data_cadastro,\n" +
                    "    excluido\n" +
                    "from\n" +
                    "	fornecedor\n" +
                    "order by\n" +
                    "	id_fornecedor")) {
                while(rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setCep(rs.getString("cep"));
                    imp.setMunicipio(rs.getString("municipio"));
                    imp.setIbge_municipio(rs.getInt("municipioibge"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCnpj_cpf(rs.getString("cnpj"));
                    imp.setIe_rg(rs.getString("ie"));
                    if((rs.getString("contato")) != null &&(!"".equals(rs.getString("contato")))) {
                        imp.addContato("1", rs.getString("contato"), null, null, TipoContato.COMERCIAL, null);
                    }
                    if((rs.getString("email")) != null &&(!"".equals(rs.getString("email")))) {
                        imp.addContato("2", null, null, null, TipoContato.COMERCIAL, rs.getString("email"));
                    }
                    if((rs.getString("fax")) != null &&(!"".equals(rs.getString("fax")))) {
                        imp.addContato("3", null, rs.getString("fax"), null, TipoContato.COMERCIAL, null);
                    }
                    imp.setTel_principal(rs.getString("telefone"));
                    imp.setAtivo(rs.getInt("ativo") == 1);
                    imp.setDatacadastro(rs.getDate("data_cadastro"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
    
    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();
        try(Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "	 id_produto,\n" +
                    "    id_fornecedor,\n" +
                    "    codigo_produto_fornecedor codigoexterno\n" +
                    "from\n" +
                    "	fornecedor_produto\n" +
                    "order by\n" +
                    "	id_fornecedor, id_produto")) {
                while(rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
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
        try(Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "    id,\n" +
                    "    nome,\n" +
                    "    nome_fantasia,\n" +
                    "    cpf_cnpj,\n" +
                    "    cpf,\n" +
                    "    inscricao_estadual,\n" +
                    "    rg,\n" +
                    "    endereco,\n" +
                    "    bairro,\n" +
                    "    municipio,\n" +
                    "    cod_municipio,\n" +
                    "    complemento,\n" +
                    "    numero,\n" +
                    "    estado,\n" +
                    "    cep,\n" +
                    "    telefone,\n" +
                    "    celular,\n" +
                    "    observacao,\n" +
                    "    limite,\n" +
                    "    data_nasc,\n" +
                    "    nome_mae,\n" +
                    "    nome_pai,\n" +
                    "    data_cadastro,\n" +
                    "    excluido\n" +
                    "from\n" +
                    "	cliente\n" +
                    "order by\n" +
                    "	id")) {
                while(rs.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rs.getString("id"));
                    imp.setRazao(rs.getString("nome"));
                    imp.setFantasia(rs.getString("nome_fantasia"));
                    imp.setCnpj(rs.getString("cpf_cnpj"));
                    if(rs.getString("inscricao_estadual") == null && "".equals(rs.getString("inscricao_estadual"))) {
                        imp.setInscricaoestadual(rs.getString("rg"));
                    } else {
                        imp.setInscricaoestadual(rs.getString("inscricao_estadual"));
                    }
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("municipio"));
                    imp.setMunicipioIBGE(rs.getInt("cod_municipio"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setUf(rs.getString("estado"));
                    imp.setCep(rs.getString("cep"));
                    imp.setTelefone(rs.getString("telefone"));
                    imp.setCelular(rs.getString("celular"));
                    imp.setObservacao(rs.getString("observacao"));
                    imp.setValorLimite(rs.getDouble("limite"));
                    imp.setDataNascimento(rs.getDate("data_nasc"));
                    imp.setDataCadastro(rs.getDate("data_cadastro"));
                    imp.setAtivo(rs.getInt("excluido") == 1);
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
    
    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();
        try(Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "	 cr.id_contas_receber id,\n" +
                    "    cr.data_emissao emissao,\n" +
                    "    pcr.data_vencimento_parcelas_receber vencimento,\n" +
                    "    pcr.parcelas,\n" +
                    "    pcr.valor,\n" +
                    "    cr.id_cliente,\n" +
                    "    cr.documento,\n" +
                    "    cr.referente observacao\n" +
                    "from\n" +
                    "	contas_receber cr\n" +
                    "join parcelas_receber pcr on (cr.id_contas_receber = pcr.id_contas_receber)\n" +
                    "where\n" +
                    "	pcr.pago = 'NAO'\n" +
                    "order by\n" +
                    "	pcr.data_vencimento_parcelas_receber")) {
                while(rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rs.getString("id"));
                    imp.setDataEmissao(rs.getDate("emissao"));
                    imp.setDataVencimento(rs.getDate("vencimento"));
                    imp.setParcela(rs.getInt("parcelas"));
                    imp.setValor(rs.getDouble("valor"));
                    imp.setIdCliente(rs.getString("id_cliente"));
                    imp.setNumeroCupom(rs.getString("documento"));
                    imp.setObservacao(rs.getString("observacao"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
