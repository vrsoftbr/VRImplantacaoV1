package vrimplantacao2.dao.cadastro.produto;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import vrframework.classe.Conexao;
import vrimplantacao2.parametro.Versao;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.ProdutoAliquotaVO;

public class ProdutoAliquotaDAO {

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
            if (!Versao.menorQue(3, 18, 3)) {
                sql.put("id_aliquotacreditocusto", vo.getAliquotaCredito().getId());
            }
            if (Versao.maiorQue(3, 19, 1, 64)) {
                sql.put("excecao", vo.getExcecao());
            }

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

    public void salvarAliquotaBeneficio(ProdutoAliquotaVO vo) throws Exception {

        if (vo.getId() == 0) {
            System.out.println("Produto Alíquota não localizada!" + " Beneficio ID: " + vo.getBeneficio()
            + " Aliquota ID: " +  vo.getAliquotaDebito().getId());
        } else {
            try (Statement stm = Conexao.createStatement()) {
                SQLBuilder sql = new SQLBuilder();
                sql.setTableName("produtoaliquotabeneficio");
                sql.put("id_produtoaliquota", vo.getId());
                sql.put("id_aliquota", vo.getAliquotaDebito().getId());
                sql.put("id_beneficio", vo.getBeneficio());

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
                    if (!Versao.menorQue(3, 18, 3)) {
                        sql.put("id_aliquotacreditocusto", vo.getAliquotaCredito().getId());
                    }
                } else if (opt.contains(OpcaoProduto.ICMS_FORNECEDOR)) {
                    sql.put("id_aliquotacreditoforaestado", vo.getAliquotaCredito().getId());
                } else if (opt.contains(OpcaoProduto.ICMS_ENTRADA)) {
                    sql.put("id_aliquotacredito", vo.getAliquotaCredito().getId());
                    sql.put("id_aliquotacreditoforaestado", vo.getAliquotaCreditoForaEstado().getId());
                    if (!Versao.menorQue(3, 18, 3)) {
                        sql.put("id_aliquotacreditocusto", vo.getAliquotaCredito().getId());
                    }
                } else if (opt.contains(OpcaoProduto.ICMS_ENTRADA_FORA_ESTADO)) {
                    sql.put("id_aliquotacreditoforaestado", vo.getAliquotaCreditoForaEstado().getId());
                } else if (opt.contains(OpcaoProduto.ICMS_SAIDA)) {
                    sql.put("id_aliquotadebito", vo.getAliquotaDebito().getId());
                    sql.put("id_aliquotadebitoforaestado", vo.getAliquotaDebitoForaEstado().getId());
                    sql.put("id_aliquotadebitoforaestadonf", vo.getAliquotaDebitoForaEstadoNf().getId());
                    sql.put("id_aliquotaconsumidor", vo.getAliquotaConsumidor().getId());
                } else if (opt.contains(OpcaoProduto.ICMS_SAIDA_FORA_ESTADO)) {
                    sql.put("id_aliquotadebitoforaestado", vo.getAliquotaDebitoForaEstado().getId());
                    sql.put("id_aliquotadebitoforaestadonf", vo.getAliquotaDebitoForaEstadoNf().getId());
                } else if (opt.contains(OpcaoProduto.ICMS_CONSUMIDOR)) {
                    sql.put("id_aliquotaconsumidor", vo.getAliquotaConsumidor().getId());
                }
                if (opt.contains(OpcaoProduto.EXCECAO)) {
                    if (Versao.maiorQue(3, 19, 1, 64)) {
                        sql.put("excecao", vo.getExcecao());
                    }
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
                if (!Versao.menorQue(3, 18, 3)) {
                    sql.put("id_aliquotacreditocusto", vo.getAliquotaCredito().getId());
                }
            } else if (opt.contains(OpcaoProduto.ICMS_FORNECEDOR)) {
                sql.put("id_aliquotacredito", vo.getAliquotaCredito().getId());
                sql.put("id_aliquotacreditoforaestado", vo.getAliquotaDebitoForaEstado().getId());
            } else if (opt.contains(OpcaoProduto.ICMS_LOJA)) {
                sql.put("id_aliquotadebito", vo.getAliquotaDebito().getId());
                sql.put("id_aliquotacredito", vo.getAliquotaCredito().getId());
                sql.put("id_aliquotadebitoforaestado", vo.getAliquotaDebitoForaEstado().getId());
                sql.put("id_aliquotacreditoforaestado", vo.getAliquotaCreditoForaEstado().getId());
                sql.put("id_aliquotadebitoforaestadonf", vo.getAliquotaDebitoForaEstadoNf().getId());
                sql.put("id_aliquotaconsumidor", vo.getAliquotaConsumidor().getId());
            } else if (opt.contains(OpcaoProduto.ICMS_ENTRADA)) {
                sql.put("id_aliquotacredito", vo.getAliquotaCredito().getId());
                sql.put("id_aliquotacreditoforaestado", vo.getAliquotaCreditoForaEstado().getId());
                if (!Versao.menorQue(3, 18, 3)) {
                    sql.put("id_aliquotacreditocusto", vo.getAliquotaCredito().getId());
                }
            } else if (opt.contains(OpcaoProduto.ICMS_ENTRADA_FORA_ESTADO)) {
                sql.put("id_aliquotacreditoforaestado", vo.getAliquotaCreditoForaEstado().getId());
            } else if (opt.contains(OpcaoProduto.ICMS_SAIDA)) {
                sql.put("id_aliquotadebito", vo.getAliquotaDebito().getId());
                sql.put("id_aliquotadebitoforaestado", vo.getAliquotaDebitoForaEstado().getId());
                sql.put("id_aliquotadebitoforaestadonf", vo.getAliquotaDebitoForaEstadoNf().getId());
                sql.put("id_aliquotaconsumidor", vo.getAliquotaConsumidor().getId());
            } else if (opt.contains(OpcaoProduto.ICMS_SAIDA_FORA_ESTADO)) {
                sql.put("id_aliquotadebitoforaestado", vo.getAliquotaDebitoForaEstado().getId());
                sql.put("id_aliquotadebitoforaestadonf", vo.getAliquotaDebitoForaEstadoNf().getId());
            } else if (opt.contains(OpcaoProduto.ICMS_CONSUMIDOR)) {
                sql.put("id_aliquotaconsumidor", vo.getAliquotaConsumidor().getId());
            } else if (opt.contains(OpcaoProduto.ICMS_SAIDA_NF)) {
                sql.put("id_aliquotadebito", vo.getAliquotaDebito().getId());
                sql.put("id_aliquotadebitoforaestado", vo.getAliquotaDebitoForaEstado().getId());
                sql.put("id_aliquotadebitoforaestadonf", vo.getAliquotaDebitoForaEstadoNf().getId());
            }
            if (opt.contains(OpcaoProduto.EXCECAO)) {
                if (Versao.maiorQue(3, 19, 1, 64)) {
                    sql.put("excecao", vo.getExcecao());
                }
            }
            if (!sql.isEmpty()) {
                sql.setWhere(
                        "id_produto = " + vo.getProduto().getId() + " and "
                        + "id_estado = " + vo.getEstado().getId());

                stm.execute(sql.getUpdate());
            }
        }
    }

    public void atualizarBeneficio(ProdutoAliquotaVO vo) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();

            sql.setTableName("produtoaliquotabeneficio");

            sql.put("id_aliquota", vo.getAliquotaDebito().getId());
            sql.put("id_beneficio", vo.getBeneficio());

            if (!sql.isEmpty()) {
                sql.setWhere(
                        "id_produtoaliquota = " + vo.getId());

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

    public int getProdutoAliquotaByProduto(int idProduto) throws Exception {
        int result = 0;

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select id from produtoaliquota where id_produto = " + idProduto
            )) {
                while (rst.next()) {
                    result = rst.getInt("id");
                }
            }
        }

        return result;
    }

    public int getBeneficio(String beneficio) throws Exception {
        int idBeneficio = 0;
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select id from codigobeneficiocst where codigo = '" + beneficio + "'"
            )) {
                if (rst.next()) {
                    idBeneficio = rst.getInt("id");
                }

            }
        }
        return idBeneficio;
    }

    public int getProdutoAliquotaBeneficio(int idProdutoAliquota) throws Exception {
        int id = 0;
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "   id\n"
                    + "from\n"
                    + "produtoaliquotabeneficio\n"
                    + "where\n"
                    + "id_produtoaliquota = " + idProdutoAliquota
            )) {
                if (rst.next()) {
                    id = rst.getInt("id");
                }
            }
        }
        return id;
    }

}
