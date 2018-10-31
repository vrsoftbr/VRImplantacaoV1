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
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author lucasrafael
 */
public class HiperDAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "Hiper";
    }

    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> lojas = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	loj_codigo id,\n"
                    + "	loj_fantasia nome,\n"
                    + "	loj_cnpj cnpj\n"
                    + "from \n"
                    + "	lojas\n"
                    + "order by\n"
                    + "	loj_codigo"
            )) {
                while (rs.next()) {
                    lojas.add(new Estabelecimento(rs.getString("id"), rs.getString("nome")));
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
                    + "p.nome, \n"
                    + "p.situacao,\n"
                    + "u.sigla as tipoembalagem,\n"
                    + "p.id_hierarquia_produto as cod_mercadologico,\n"
                    + "cast(p.data_hora_cadastro as date) as datacadastro,\n"
                    + "p.preco_custo,\n"
                    + "p.preco_aquisicao,\n"
                    + "p.preco_venda,\n"
                    + "pis.codigo_situacao_tributaria_pis as cst_pis,\n"
                    + "pis.nome as desc_pis,\n"
                    + "cofins.codigo_situacao_tributaria_cofins as cst_cofins,\n"
                    + "cofins.nome as desc_cofins,\n"
                    + "p.id_ncm,\n"
                    + "p.codigo_cest,\n"
                    + "p.dias_validade,\n"
                    + "p.markup_varejo,\n"
                    + "p.produto_integrado_balanca as balanca,\n"
                    + "est.quantidade\n"
                    + "from produto p\n"
                    + "inner join unidade_medida u on u.id_unidade_medida = p.id_unidade_medida\n"
                    + "left join produto_sinonimo e on e.id_produto = p.id_produto\n"
                    + "left join hierarquia_produto m on m.id_hierarquia_produto = p.id_hierarquia_produto\n"
                    + "left join situacao_tributaria_pis pis on pis.id_situacao_tributaria_pis = p.id_situacao_tributaria_pis\n"
                    + "left join situacao_tributaria_cofins cofins on cofins.id_situacao_tributaria_cofins = p.id_situacao_tributaria_cofins\n"
                    + "left join saldo_estoque est on est.id_produto = p.id_produto\n"
                    + "order by id_produto"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                }
            }
        }
        return null;
    }
    
    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "f.id_entidade,\n"
                    + "f.nome as razao,\n"
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
                    + "cc.uf as uf_cobranca,\n"
                    + "f.inativo,\n"
                    + "f.flag_funcionario,\n"
                    + "f.flag_cliente\n"
                    + "from entidade f \n"
                    + "left join cidade c on c.id_cidade = f.id_cidade\n"
                    + "left join cidade cc on cc.id_cidade = f.id_cidade_cobranca\n"
                    + "where tipo_entidade = 2"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                }
            }
        }
        return null;
    }
}
