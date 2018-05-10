package vrimplantacao2.dao.cadastro.produto;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.ProdutoAliquotaVO;

public class ProdutoAliquotaDAO {

    public void salvar(int idLojaVR, Collection<ProdutoAliquotaVO> values) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            for (ProdutoAliquotaVO vo : values) {
                SQLBuilder sql = new SQLBuilder();
                sql.setTableName("produtoaliquota");
                sql.put("id_produto", vo.getProduto().getId());
                sql.put("id_estado", vo.getEstado().getId());
                sql.put("id_aliquotadebito", vo.getAliquotaDebito().getId());
                sql.put("id_aliquotacredito", vo.getAliquotaCredito().getId());
                sql.put("id_aliquotadebitoforaestado", vo.getAliquotaDebitoForaEstado().getId());
                sql.put("id_aliquotacreditoforaestado", vo.getAliquotaCreditoForaEstado().getId());
                sql.put("id_aliquotadebitoforaestadonf", vo.getAliquotaDebitoForaEstadoNf().getId());
                sql.put("id_aliquotaconsumidor", vo.getAliquotaConsumidor().getId());

                sql.getReturning().add("id");

                try (ResultSet rst = stm.executeQuery(
                        sql.getInsert()
                )) {
                    if (rst.next()) {
                        vo.setId(rst.getInt("id"));
                    }
                }
            }
        }
    }

    public void salvar(ProdutoAliquotaVO vo) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            sql.setTableName("produtoaliquota");
            sql.put("id_produto", vo.getProduto().getId());
            sql.put("id_estado", vo.getEstado().getId());
            if (vo.getAliquotaDebito() == null) {
                System.out.println("ID: " + vo.getProduto().getId());
                System.out.println("DESCRICAO: " + vo.getProduto().getDescricaoCompleta());
            }
            sql.put("id_aliquotadebito", vo.getAliquotaDebito().getId());
            sql.put("id_aliquotacredito", vo.getAliquotaCredito().getId());
            sql.put("id_aliquotadebitoforaestado", vo.getAliquotaDebitoForaEstado().getId());
            sql.put("id_aliquotacreditoforaestado", vo.getAliquotaCreditoForaEstado().getId());
            sql.put("id_aliquotadebitoforaestadonf", vo.getAliquotaDebitoForaEstadoNf().getId());
            sql.put("id_aliquotaconsumidor", vo.getAliquotaConsumidor().getId());

            sql.getReturning().add("id");

            try (ResultSet rst = stm.executeQuery(
                    sql.getInsert()
            )) {
                if (rst.next()) {
                    vo.setId(rst.getInt("id"));
                }
            }
        }
    }

    public void atualizar(Collection<ProdutoAliquotaVO> aliquotas, OpcaoProduto... opcoes) throws Exception {
        Set<OpcaoProduto> opt = new LinkedHashSet<>(Arrays.asList(opcoes));
        try (Statement stm = Conexao.createStatement()) {
            for (ProdutoAliquotaVO vo : aliquotas) {
                SQLBuilder sql = new SQLBuilder();
                sql.setTableName("produtoaliquota");
                if (opt.contains(OpcaoProduto.ICMS)) {
                    sql.put("id_aliquotadebito", vo.getAliquotaDebito().getId());
                    sql.put("id_aliquotacredito", vo.getAliquotaCredito().getId());
                    sql.put("id_aliquotadebitoforaestado", vo.getAliquotaDebitoForaEstado().getId());
                    sql.put("id_aliquotacreditoforaestado", vo.getAliquotaCreditoForaEstado().getId());
                    sql.put("id_aliquotadebitoforaestadonf", vo.getAliquotaDebitoForaEstadoNf().getId());
                    sql.put("id_aliquotaconsumidor", vo.getAliquotaConsumidor().getId());
                } else if (opt.contains(OpcaoProduto.ICMS_FORNECEDOR)) {
                    sql.put("id_aliquotacreditoforaestado", vo.getAliquotaCredito().getId());
                }
                if (!sql.isEmpty()) {
                    sql.setWhere(
                            "id_produto = " + vo.getProduto().getId() + " and "
                            + "id_estado = " + vo.getEstado().getId()
                    );
                    stm.execute(sql.getUpdate());
                }
            }
        }
    }

    public void atualizar(ProdutoAliquotaVO vo, Set<OpcaoProduto> opt) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            sql.setTableName("produtoaliquota");

            if (opt.contains(OpcaoProduto.ICMS)) {
                sql.put("id_aliquotadebito", vo.getAliquotaDebito().getId());
                sql.put("id_aliquotacredito", vo.getAliquotaCredito().getId());
                sql.put("id_aliquotadebitoforaestado", vo.getAliquotaDebitoForaEstado().getId());
                sql.put("id_aliquotacreditoforaestado", vo.getAliquotaCreditoForaEstado().getId());
                sql.put("id_aliquotadebitoforaestadonf", vo.getAliquotaDebitoForaEstadoNf().getId());
                sql.put("id_aliquotaconsumidor", vo.getAliquotaConsumidor().getId());
            } else if (opt.contains(OpcaoProduto.ICMS_FORNECEDOR)) {
                sql.put("id_aliquotacredito", vo.getAliquotaCredito().getId());
                sql.put("id_aliquotacreditoforaestado", vo.getAliquotaCreditoFornecedor()); //Caso especifico para importação do Arius (Cliente Ameripan)                
            }
            if (!sql.isEmpty()) {
                sql.setWhere(
                        "id_produto = " + vo.getProduto().getId() + " and "
                        + "id_estado = " + vo.getEstado().getId());

                stm.execute(sql.getUpdate());
            }
        }
    }

    public MultiMap<Integer, Void> getAliquotas() throws Exception {
        MultiMap<Integer, Void> result = new MultiMap<>();

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select id_produto, id_estado from produtoaliquota order by id_produto, id_estado"
            )) {
                while (rst.next()) {
                    result.put(null, rst.getInt("id_produto"), rst.getInt("id_estado"));
                }
            }
        }

        return result;
    }

}
