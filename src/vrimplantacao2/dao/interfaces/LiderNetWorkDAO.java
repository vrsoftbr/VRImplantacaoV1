/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Lucas
 */
public class LiderNetWorkDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "LiderNetWork";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                new OpcaoProduto[]{
                    OpcaoProduto.IMPORTAR_MANTER_BALANCA,
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
                    OpcaoProduto.MARGEM,
                    OpcaoProduto.OFERTA,
                    OpcaoProduto.MAPA_TRIBUTACAO
                }
        ));
    }

    public List<Estabelecimento> getLojaCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select e.cod_emp as id, e.razao_emp as razao from empresa e"
            )) {
                while (rs.next()) {
                    result.add(new Estabelecimento(rs.getString("id"), rs.getString("razao")));
                }
            }
        }
        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select distinct\n"
                    + "    cod_grp as id,\n"
                    + "    nome_grp as descricao,\n"
                    + "    cst as cst,\n"
                    + "    aliquota_grp as aliquota\n"
                    + "from grupo_icms\n"
                    + "where cod_grp in (select icms_cont_est from produto)\n"
                    + "union all\n"
                    + "select\n"
                    + "    cod_grp as id,\n"
                    + "    nome_grp as descricao,\n"
                    + "    cst as cst,\n"
                    + "    aliquota_grp as aliquota\n"
                    + "from grupo_icms\n"
                    + "where cod_grp in (select icms_cont_fora from produto)\n"
                    + "union all\n"
                    + "select\n"
                    + "    cod_grp as id,\n"
                    + "    nome_grp as descricao,\n"
                    + "    cst as cst,\n"
                    + "    aliquota_grp as aliquota\n"
                    + "from grupo_icms\n"
                    + "where cod_grp in (select icms_cf_est from produto)\n"
                    + "union all\n"
                    + "select\n"
                    + "    cod_grp as id,\n"
                    + "    nome_grp as descricao,\n"
                    + "    cst as cst,\n"
                    + "    aliquota_grp as aliquota\n"
                    + "from grupo_icms\n"
                    + "where cod_grp in (select icms_cf_fora from produto)"
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(rs.getString("id"), rs.getString("descricao")));
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        ProdutoIMP imp = null;
        int cont = 0;
        try {

            try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select\n"
                        + "  p.cod_pro,\n"
                        + "  p.codigo_barra_pro as codigobarras,\n"
                        + "  case p.produto_pesado_pro when 'N' then 0 else 1 end balanca,\n"
                        + "  us.descricao as tipoembalagem,\n"
                        + "  p.dias_validade_pro as validade,\n"
                        + "  p.nome_pro as descricaocompleta,\n"
                        + "  p.desc_cupom as descricaoreduzida,\n"
                        + "  ue.descricao as tipoembalagem_cotacao,\n"
                        + "  p.quant_unidade_entrada as qtdembalagem_cotacao,\n"
                        + "  p.cst_pis,\n"
                        + "  p.cst_cofins,\n"
                        + "  p.natureza_operacao as naturezareceita,\n"
                        + "  p.cod_ncm as ncm,\n"
                        + "  p.cest as cest,\n"
                        + "  p.valor_pro as precovenda,\n"
                        + "  p.valor_atacado as precoatacado,\n"
                        + "  p.preco_custo as custo,\n"
                        + "  est.estoque,\n"
                        + "  p.estoque_minimo_pro as estoqueminimo,\n"
                        + "  case p.ativo_pro when 'S' then 1 else 0 end ativo,\n"
                        + "  p.margem_lucro_pro as margem,\n"
                        + "  icms_est.cod_grp as icms_cod_est,\n"
                        + "  icms_est.nome_grp as icms_nome_est,\n"
                        + "  icms_est.cst as icms_cst_est,\n"
                        + "  icms_est.aliquota_grp as icms_aliq_est,\n"
                        + "  icms_fora.cod_grp as icms_cod_fora,\n"
                        + "  icms_fora.nome_grp as icms_nome_fora,\n"
                        + "  icms_fora.aliquota_grp as icms_aliq_fora,\n"
                        + "  icms_cf_est.cod_grp as icms_cod_cf_est,\n"
                        + "  icms_cf_est.nome_grp as icms_nome_cf_est,\n"
                        + "  icms_cf_est.aliquota_grp as icms_aliq_cf_est,\n"
                        + "  icms_cf_fora.cod_grp as icms_cod_cf_fora,\n"
                        + "  icms_cf_fora.nome_grp as icms_nome_cf_fora,\n"
                        + "  icms_cf_fora.aliquota_grp as icms_aliq_cf_fora,\n"
                        + "  p.icms as icms\n"
                        + "from produto p\n"
                        + "left join unidade_medida us on us.codigo = p.codigo_unidade_saida\n"
                        + "left join unidade_medida ue on ue.codigo = p.codigo_unidade_entrada\n"
                        + "left join grupo_icms icms_est on icms_est.cod_grp = p.icms_cont_est\n"
                        + "left join grupo_icms icms_fora on icms_fora.cod_grp = p.icms_cont_fora\n"
                        + "left join grupo_icms icms_cf_est on icms_cf_est.cod_grp = p.icms_cf_est\n"
                        + "left join grupo_icms icms_cf_fora on icms_cf_fora.cod_grp = p.icms_cf_fora\n"
                        + "left join estoque est on est.cod_pro = p.cod_produto_estoque\n"
                        + "    and est.cod_emp = " + getLojaOrigem() + "\n"
                        + "order by p.cod_pro"
                )) {
                    while (rst.next()) {
                        imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("cod_pro"));
                        
                        if ("231".equals(imp.getImportId())) {
                            System.out.println("Aqui");
                        }
                        
                        imp.setEan(rst.getString("codigobarras"));
                        imp.seteBalanca(rst.getInt("balanca") == 1);
                        imp.setValidade(rst.getInt("validade"));
                        imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                        imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                        imp.setDescricaoGondola(imp.getDescricaoCompleta());
                        imp.setTipoEmbalagemCotacao(rst.getString("qtdembalagem_cotacao"));
                        imp.setQtdEmbalagemCotacao(rst.getInt("qtdembalagem_cotacao"));
                        imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                        imp.setSituacaoCadastro(rst.getInt("ativo"));
                        imp.setMargem(rst.getDouble("margem"));
                        imp.setCustoComImposto(rst.getDouble("custo"));
                        imp.setCustoSemImposto(rst.getDouble("custo"));
                        imp.setPrecovenda(rst.getDouble("precovenda"));
                        imp.setEstoque(rst.getDouble("estoque"));
                        imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                        imp.setNcm(rst.getString("ncm"));
                        imp.setCest(rst.getString("cest"));
                        imp.setPiscofinsCstDebito(rst.getString("cst_pis"));
                        imp.setPiscofinsCstCredito(rst.getString("cst_cofins"));
                        imp.setPiscofinsNaturezaReceita(rst.getString("naturezareceita"));
                        imp.setIcmsDebitoId(rst.getString("icms_cod_cf_est"));
                        imp.setIcmsDebitoForaEstadoId(rst.getString("icms_cod_cf_fora"));
                        imp.setIcmsDebitoForaEstadoNfId(rst.getString("icms_cod_cf_fora"));
                        imp.setIcmsCreditoId(rst.getString("icms_cod_est"));
                        imp.setIcmsCreditoForaEstadoId(rst.getString("icms_cod_fora"));
                        result.add(imp);

                        cont++;
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println(cont);
            ex.printStackTrace();

        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    f.cod_for as id,\n"
                    + "    f.razao_for as razao,\n"
                    + "    f.end_for as endereco,\n"
                    + "    f.num_for as numero,\n"
                    + "    f.bai_for as bairro,\n"
                    + "    f.cid_for as cidade,\n"
                    + "    f.codigo_ibge as cidade_ibge,\n"
                    + "    f.cep_for as cep,\n"
                    + "    f.est_for as estado,\n"
                    + "    f.cnpj_for as cnpj,\n"
                    + "    f.insc_for as ie_rg,\n"
                    + "    f.tel_for as telefone,\n"
                    + "    f.fax_for as fax,\n"
                    + "    f.email_for as email,\n"
                    + "    f.contato_for as contato\n"
                    + "from fornecedor f\n"
                    + "order by f.cod_for"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(imp.getRazao());
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("ie_rg"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("estado"));
                    imp.setCep(rst.getString("cep"));
                    imp.setIbge_municipio(rst.getInt("cidade_ibge"));
                    imp.setTel_principal(rst.getString("telefone"));

                    if ((rst.getString("fax") != null)
                            && (!rst.getString("fax").trim().isEmpty())) {
                        imp.addTelefone("FAX", rst.getString("fax"));
                    }

                    if ((rst.getString("email") != null)
                            && (!rst.getString("email").trim().isEmpty())) {
                        imp.addEmail("EMAIL", rst.getString("email"), TipoContato.NFE);
                    }

                    result.add(imp);
                }
            }
        }
        return result;
    }
}
