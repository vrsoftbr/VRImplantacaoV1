/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Lucas
 */
public class LiderNetWorkDAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "LiderNetWork";
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "  p.cod_pro as id,\n"
                    + "  p.codigo_barra_pro as codigobarras,\n"
                    + "  p.produto_pesado_pro as balanca,\n"
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
                    + "  p.estoque_minimo_pro as estoqueminimo,\n"
                    + "  p.ativo_pro as ativo,\n"
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
                    + "order by p.cod_pro"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                }
            }
        }
        return null;
    }
}
