package vrimplantacao2.dao.cadastro.financeiro.contaspagar;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.financeiro.PagarFornecedorVO;
import vrimplantacao2.vo.cadastro.financeiro.ReceberDevolucaoVO;

/**
 *
 * @author Leandro
 */
public class PagarFornecedorDAO {

    public void gravar(PagarFornecedorVO fat) throws Exception {

        SQLBuilder sql = new SQLBuilder();
        sql.setSchema("public");
        sql.setTableName("pagarfornecedor");
        sql.put("id_loja", fat.getId_loja());
        sql.put("id_fornecedor", fat.getId_fornecedor());
        sql.put("id_tipoentrada", fat.getId_tipoentrada());
        sql.put("numerodocumento", fat.getNumerodocumento());
        sql.put("dataentrada", fat.getDataentrada());
        sql.put("dataemissao", fat.getDataemissao());
        sql.put("valor", fat.getValor());
        sql.put("id_notadespesa", fat.getId_notadespesa(), -1);
        sql.put("id_notaentrada", fat.getId_notaentrada(), -1);
        sql.put("id_transferenciaentrada", fat.getId_transferenciaentrada(), -1);
        sql.put("id_pagaroutrasdespesas", fat.getId_pagaroutrasdespesas(), -1);
        sql.put("id_geracaoretencaotributo", fat.getId_geracaoretencaotributo(), -1);
        sql.put("id_escritasaldo", fat.getId_escritasaldo(), -1);
        sql.getReturning().add("id");

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet qr = stm.executeQuery(sql.getInsert())) {
                qr.next();
                fat.setId(qr.getInt("id"));
            }
        }

    }

    public MultiMap<String, PagarFornecedorVO> getPagarFornecedores(int idLoja, int idFornecedor, int numeroDocumento, Date dataemissao) throws Exception {
        MultiMap<String, PagarFornecedorVO> result = new MultiMap<>();

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "id_loja,\n"
                    + "id_fornecedor,\n"
                    + "dataemissao,\n"
                    + "numerodocumento,\n"
                    + "valor\n"
                    + "from pagarfornecedor\n"
                    + "where id_loja = " + idLoja + "\n"
                    + "and id_fornecedor = " + idFornecedor + "\n"
                    + "and numerodocumento = " + numeroDocumento + "\n"
                    + "and dataemissao = '" + dataemissao + "'"
            )) {
                while (rst.next()) {
                    PagarFornecedorVO vo = new PagarFornecedorVO();
                    vo.setId_fornecedor(rst.getInt("id_fornecedor"));
                    vo.setDataemissao(rst.getDate("dataemissao"));
                    vo.setNumerodocumento(rst.getInt("numerodocumento"));
                    vo.setValor(rst.getDouble("valor"));
                    result.put(
                            vo,
                            String.valueOf(vo.getId_loja()),
                            String.valueOf(vo.getId_fornecedor()),
                            String.valueOf(vo.getNumerodocumento()),
                            String.valueOf(vo.getDataemissao())
                    );
                }
            }
        }
        return result;
    }

    public void gravarIdPagarFornecedorDuplicado(List<PagarFornecedorVO> v_list, int idLoja) throws Exception {
        Statement stm = null;
        StringBuilder sql = null;
        ResultSet rst = null;
        try {
            Conexao.begin();
            stm = Conexao.createStatement();
            ProgressBar.setMaximum(v_list.size());
            ProgressBar.setStatus("Gravando Id Pagar Fornecedor Duplicado...");
            for (PagarFornecedorVO i_list : v_list) {

                rst = stm.executeQuery(
                        "select \n"
                        + "	p.id,\n"
                        + "     P.id_loja, \n"        
                        + "	p.id_fornecedor, \n"
                        + "	p.numerodocumento,\n"
                        + "	p.dataentrada, \n"
                        + "	p.dataemissao,\n"
                        + "	i.datavencimento,\n"
                        + "	p.valor\n"
                        + "	from pagarfornecedor p\n"
                        + "	inner join pagarfornecedorparcela i on i.id_pagarfornecedor = p.id\n"
                        + "where p.id_loja = " + idLoja + "\n"
                        + "and p.id_fornecedor = " + i_list.getId_fornecedor() + " \n"
                        + "and p.numerodocumento = " + i_list.getNumerodocumento() + "\n"
                        + "and p.dataentrada = '" + i_list.getDataentrada() + "' \n"
                        + "and p.dataemissao = '" + i_list.getDataemissao() + "'\n"
                        + "and i.datavencimento = '" + i_list.getDatavencimento() + "'\n"
                        + "and p.valor = " + i_list.getValor() + " \n"
                        + "and observacao like '%IMPORTADO VR%' \n"
                        + "and id_situacaopagarfornecedorparcela = 0");

                if (rst.next()) {
                    stm.execute("insert into implantacao.id_pagarfornecedor(id, id_loja) "
                            + "values ("
                            + rst.getInt("id") + ", "
                            + rst.getInt("id_loja") + ");");
                }
                ProgressBar.next();
            }

            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }
}
