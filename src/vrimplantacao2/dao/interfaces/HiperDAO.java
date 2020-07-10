/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.vo.cadastro.mercadologico.MercadologicoNivelIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author lucasrafael
 */
public class HiperDAO extends InterfaceDAO {

    public String id_loja;

    @Override
    public String getSistema() {
        if ((id_loja != null) && (!id_loja.trim().isEmpty())) {
            return "Hiper" + id_loja;
        } else {
            return "Hiper";
        }
    }

    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> lojas = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select id_filial, razao_social from filial order by id_filial"
            )) {
                while (rs.next()) {
                    lojas.add(new Estabelecimento(rs.getString("id_filial"), rs.getString("razao_social")));
                }
            }
        }
        return lojas;
    }

    @Override
    public List<MercadologicoNivelIMP> getMercadologicoPorNivel() throws Exception {
        Map<String, MercadologicoNivelIMP> merc = new LinkedHashMap<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "id_hierarquia_produto as merc1,\n"
                    + "nome as merc1_descricao\n"
                    + "from hierarquia_produto\n"
                    + "where id_hierarquia_produto_pai is null\n"
                    + "order by cast(id_hierarquia_produto as integer)"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP imp = new MercadologicoNivelIMP();
                    imp.setId(rst.getString("merc1"));
                    imp.setDescricao(rst.getString("merc1_descricao"));
                    merc.put(imp.getId(), imp);
                }
            }

            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "id_hierarquia_produto_pai as merc1,\n"
                    + "sequencia as merc2,\n"
                    + "nome as merc2_descricao\n"
                    + "from hierarquia_produto\n"
                    + "where id_hierarquia_produto_pai is not null\n"
                    + "order by cast(id_hierarquia_produto_pai as integer), "
                    + " cast(sequencia as integer)"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP merc1 = merc.get(rst.getString("merc1"));
                    if (merc1 != null) {
                        merc1.addFilho(
                                rst.getString("merc2"),
                                rst.getString("merc2_descricao")
                        );
                    }
                }
            }

            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "id_hierarquia_produto_pai as merc1,\n"
                    + "sequencia as merc2,\n"
                    + "'1' as merc3,\n"
                    + "nome as merc3_descricao\n"
                    + "from hierarquia_produto\n"
                    + "where id_hierarquia_produto_pai is not null\n"
                    + "order by cast(id_hierarquia_produto_pai as integer), cast(sequencia as integer)"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP merc1 = merc.get(rst.getString("merc1"));
                    if (merc1 != null) {
                        MercadologicoNivelIMP merc2 = merc1.getNiveis().get(rst.getString("merc2"));
                        if (merc2 != null) {
                            merc2.addFilho(
                                    rst.getString("merc3"),
                                    rst.getString("merc3_descricao")
                            );
                        }
                    }
                }
            }
        }
        return new ArrayList<>(merc.values());
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "p.id_produto, \n"
                    + "e.codigo_barras,\n"
                    + "e.sigla_unidade_logistica,\n"
                    + "'1' as qtdembalagem,\n"
                    + "p.nome as descricao, \n"
                    + "p.situacao,\n"
                    + "u.sigla as tipoembalagem,\n"
                    + "p.id_hierarquia_produto as mercadologico,\n"
                    + "cast(p.data_hora_cadastro as date) as datacadastro,\n"
                    + "p.preco_custo,\n"
                    + "p.preco_aquisicao,\n"
                    + "p.preco_venda,\n"
                    + "pis.codigo_situacao_tributaria_pis as cst_pis,\n"
                    + "pis.nome as desc_pis,\n"
                    + "cofins.codigo_situacao_tributaria_cofins as cst_cofins,\n"
                    + "cofins.nome as desc_cofins,\n"
                    + "nat.codigo_natureza_receita as naturezareceita,\n"
                    + "nat2.codigo_natureza_receita as naturezareceita2,\n"
                    + "p.id_ncm as ncm,\n"
                    + "p.codigo_cest as cest,\n"
                    + "p.dias_validade,\n"
                    + "p.markup_varejo,\n"
                    + "p.produto_integrado_balanca as balanca,\n"
                    + "est.quantidade as estoque,\n"
                    + "cst_icms.codigo_situacao_tributaria as cst_icms,\n"
                    + "icm.aliquota_icms as aliq_icms,\n"
                    + "icm.reducao_base_calculo as red_icms\n"
                    + "from produto p\n"
                    + "inner join unidade_medida u on u.id_unidade_medida = p.id_unidade_medida\n"
                    + "left join produto_sinonimo e on e.id_produto = p.id_produto\n"
                    + "left join hierarquia_produto m on m.id_hierarquia_produto = p.id_hierarquia_produto\n"
                    + "left join situacao_tributaria_pis pis on pis.id_situacao_tributaria_pis = p.id_situacao_tributaria_pis\n"
                    + "left join situacao_tributaria_cofins cofins on cofins.id_situacao_tributaria_cofins = p.id_situacao_tributaria_cofins\n"
                    + "left join saldo_estoque est on est.id_produto = p.id_produto\n"
                    + "left join natureza_receita_pis_cofins nat on nat.id_natureza_receita_pis_cofins = p.id_natureza_receita_pis\n"
                    + "left join natureza_receita_pis_cofins nat2 on nat2.id_natureza_receita_pis_cofins = p.id_natureza_receita_cofins\n"
                    + "left join view_hiperpdv_produto_tributacao_icms icm on icm.id_produto = p.id_produto\n"
                    + "left join situacao_tributaria_icms cst_icms on cst_icms.id_situacao_tributaria_icms = icm.id_situacao_tributaria_icms\n"
                    + "	and icm.uf_de = (select c.uf from filial f, cidade c where f.id_cidade = c.id_cidade and codigo_filial = " + getLojaOrigem() + ") and icm.uf_para = (select c.uf from filial f, cidade c where f.id_cidade = c.id_cidade and codigo_filial = " + getLojaOrigem() + ")\n"
                    + "order by id_produto"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id_produto"));
                    imp.setEan(rst.getString("codigo_barras"));
                    imp.seteBalanca(rst.getInt("balanca") == 1);
                    imp.setValidade(rst.getInt("dias_validade"));
                    imp.setDescricaoCompleta(rst.getString("descricao"));
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));

                    String merc = rst.getString("mercadologico") != null ? rst.getString("mercadologico") : "";
                    String[] cods = merc.split("\\.");

                    for (int i = 0; i < cods.length; i++) {
                        switch (i) {
                            case 0:
                                imp.setCodMercadologico1(cods[i]);
                                break;
                            case 1:
                                imp.setCodMercadologico2(cods[i]);
                                break;
                        }
                    }

                    imp.setCodMercadologico3("1");
                    imp.setMargem(rst.getDouble("markup_varejo"));
                    imp.setCustoComImposto(rst.getDouble("preco_custo"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setPrecovenda(rst.getDouble("preco_venda"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getString("cst_pis"));
                    imp.setPiscofinsCstCredito(rst.getString("cst_cofins"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("naturezareceita"));
                    imp.setIcmsCstSaida(rst.getInt("cst_icms"));
                    imp.setIcmsAliqSaida(rst.getDouble("aliq_icms"));
                    imp.setIcmsReducaoSaida(rst.getDouble("red_icms"));
                    imp.setIcmsCstEntrada(rst.getInt("cst_icms"));
                    imp.setIcmsAliqEntrada(rst.getDouble("aliq_icms"));
                    imp.setIcmsReducaoEntrada(rst.getDouble("red_icms"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "f.id_entidade as id,\n"
                    + "f.nome as razao,\n"
                    + "j.nome_fantasia as fantasia,\n"
                    + "j.cnpj,\n"
                    + "j.ie,\n"
                    + "fi.cpf,\n"
                    + "fi.rg,\n"
                    + "cast(f.data_hora_cadastro as date) as datacadastro,\n"
                    + "f.logradouro as endereco,\n"
                    + "f.numero_endereco,\n"
                    + "f.bairro,\n"
                    + "f.complemento,\n"
                    + "f.cep,\n"
                    + "c.id_ibge as ibge_cidade,\n"
                    + "c.nome as cidade,\n"
                    + "c.uf,\n"
                    + "f.site,\n"
                    + "f.observacao,\n"
                    + "f.fone_primario_ddd as ddd1,\n"
                    + "f.fone_primario_numero as telefone1,\n"
                    + "f.fone_primario_nome_contato as contato1,\n"
                    + "f.fone_secundario_ddd as ddd2,\n"
                    + "f.fone_secundario_numero as telefone2,\n"
                    + "f.fone_secundario_nome_contato as contato2,\n"
                    + "f.email,\n"
                    + "f.logradouro_cobranca as endereco_cobranca,\n"
                    + "f.numero_endereco_cobranca as numero_cobranca,\n"
                    + "f.bairro_cobranca,\n"
                    + "f.complemento_cobranca,\n"
                    + "f.cep_cobranca,\n"
                    + "cc.id_ibge as cidade_ibge_cobranca,\n"
                    + "cc.nome as cidade_cobranca, \n"
                    + "cc.uf as uf_cobranca,\n"
                    + "f.inativo,\n"
                    + "f.flag_funcionario,\n"
                    + "f.flag_cliente\n"
                    + "from entidade f \n"
                    + "left join pessoa_juridica j on j.id_entidade = f.id_entidade\n"
                    + "left join pessoa_fisica fi on fi.id_entidade = f.id_entidade\n"
                    + "left join cidade c on c.id_cidade = f.id_cidade\n"
                    + "left join cidade cc on cc.id_cidade = f.id_cidade_cobranca\n"
                    + "where flag_fornecedor = 1"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("ie"));
                    imp.setAtivo(rst.getInt("inativo") == 1);
                    imp.setDatacadastro(rst.getDate("datacadastro"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero_endereco"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setCep(rst.getString("cep"));
                    imp.setIbge_municipio(rst.getInt("ibge_cidade"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("uf"));
                    imp.setObservacao(rst.getString("observacao"));
                    imp.setTel_principal(rst.getString("ddd1") + rst.getString("telefone1"));
                    imp.setCob_endereco(rst.getString("endereco_cobranca"));
                    imp.setCob_numero(rst.getString("numero_cobranca"));
                    imp.setCob_bairro(rst.getString("bairro_cobranca"));
                    imp.setCob_complemento(rst.getString("complemento_cobranca"));
                    imp.setCob_cep(rst.getString("cep_cobranca"));
                    imp.setCob_ibge_municipio(rst.getInt("cidade_ibge_cobranca"));
                    imp.setCob_municipio(rst.getString("cidade_cobranca"));
                    imp.setCob_uf(rst.getString("uf_cobranca"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "id_produto, \n"
                    + "id_entidade as id_fornecedor,\n"
                    + "(cast(id_entidade as varchar)+cast(id_produto as varchar)) as codigoexterno\n"
                    + "from produto_fornecedor"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rst.getString("id_produto"));
                    imp.setIdFornecedor(rst.getString("id_fornecedor"));
                    imp.setCodigoExterno(rst.getString("codigoexterno"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "f.id_entidade as id,\n"
                    + "f.nome as razao,\n"
                    + "j.nome_fantasia as fantasia,\n"
                    + "j.cnpj,\n"
                    + "j.ie,\n"
                    + "fi.cpf,\n"
                    + "fi.rg,\n"
                    + "cast(f.data_hora_cadastro as date) as datacadastro,\n"
                    + "f.logradouro as endereco,\n"
                    + "f.numero_endereco,\n"
                    + "f.bairro,\n"
                    + "f.complemento,\n"
                    + "f.cep,\n"
                    + "c.id_ibge as ibge_cidade,\n"
                    + "c.nome as cidade,\n"
                    + "c.uf,\n"
                    + "f.site,\n"
                    + "f.observacao,\n"
                    + "f.fone_primario_ddd as ddd1,\n"
                    + "f.fone_primario_numero as telefone1,\n"
                    + "f.fone_primario_nome_contato as contato1,\n"
                    + "f.fone_secundario_ddd as ddd2,\n"
                    + "f.fone_secundario_numero as telefone2,\n"
                    + "f.fone_secundario_nome_contato as contato2,\n"
                    + "f.email,\n"
                    + "f.logradouro_cobranca as endereco_cobranca,\n"
                    + "f.numero_endereco_cobranca as numero_cobranca,\n"
                    + "f.bairro_cobranca,\n"
                    + "f.complemento_cobranca,\n"
                    + "f.cep_cobranca,\n"
                    + "cc.id_ibge as cidade_ibge_cobranca,\n"
                    + "cc.nome as cidade_cobranca, \n"
                    + "cc.uf as uf_cobranca,\n"
                    + "f.inativo,\n"
                    + "f.flag_funcionario,\n"
                    + "f.flag_cliente,\n"
                    + "f.limite_credito\n"
                    + "from entidade f \n"
                    + "left join pessoa_juridica j on j.id_entidade = f.id_entidade\n"
                    + "left join pessoa_fisica fi on fi.id_entidade = f.id_entidade\n"
                    + "left join cidade c on c.id_cidade = f.id_cidade\n"
                    + "left join cidade cc on cc.id_cidade = f.id_cidade_cobranca\n"
                    + "where flag_cliente = 1"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setInscricaoestadual(rst.getString("ie"));
                    imp.setAtivo(rst.getInt("inativo") == 1);
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero_endereco"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setCep(rst.getString("cep"));
                    imp.setMunicipioIBGE(rst.getInt("ibge_cidade"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("uf"));
                    imp.setObservacao(rst.getString("observacao"));
                    imp.setTelefone(rst.getString("ddd1") + rst.getString("telefone1"));
                    imp.setCobrancaEndereco(rst.getString("endereco_cobranca"));
                    imp.setCobrancaNumero(rst.getString("numero_cobranca"));
                    imp.setCobrancaBairro(rst.getString("bairro_cobranca"));
                    imp.setCobrancaComplemento(rst.getString("complemento_cobranca"));
                    imp.setCobrancaCep(rst.getString("cep_cobranca"));
                    imp.setCobrancaMunicipioIBGE(rst.getInt("cidade_ibge_cobranca"));
                    imp.setCobrancaMunicipio(rst.getString("cidade_cobranca"));
                    imp.setCobrancaUf(rst.getString("uf_cobranca"));
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
        
        try(Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "SELECT \n" +
                    "	id_documento_receber,\n" +
                    "	id_tipo_documento_financeiro,\n" +
                    "	id_entidade,\n" +
                    "	data_emissao,\n" +
                    "	data_vencimento,\n" +
                    "	data_quitacao,\n" +
                    "	valor,\n" +
                    "	saldo,\n" +
                    "	situacao,\n" +
                    "	numero_documento_receber,\n" +
                    "	descricao,\n" +
                    "	id_entidade_portador,\n" +
                    "	id_centro_lucro\n" +
                    "FROM \n" +
                    "	documento_receber\n" +
                    "where\n" +
                    "	situacao = 1 and \n" +
                    "	id_filial_geracao = " + getLojaOrigem() + " and \n" +
                    "	id_entidade not in (6, 90, 91, 92, 96, 93)")) {
                while(rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    
                    imp.setId(rs.getString("id_documento_receber"));
                    imp.setIdCliente(rs.getString("id_entidade"));
                    imp.setDataEmissao(rs.getDate("data_emissao"));
                    imp.setDataVencimento(rs.getDate("data_vencimento"));
                    imp.setValor(rs.getDouble("saldo"));
                    imp.setNumeroCupom(rs.getString("numero_documento_receber"));
                    imp.setObservacao(rs.getString("descricao"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
