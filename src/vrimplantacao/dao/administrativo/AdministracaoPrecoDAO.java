package vrimplantacao.dao.administrativo;

import java.sql.Statement;
import vrimplantacao.dao.DataProcessamentoDAO;
import vrimplantacao.dao.cadastro.KitDAO;
import vrimplantacao.vo.administrativo.AdministracaoPrecoVO;
import vrframework.classe.Conexao;
import vrframework.classe.Util;

public class AdministracaoPrecoDAO {

    public void adicionar(AdministracaoPrecoVO i_administracao) throws Exception {
        Statement stm = null;
        StringBuilder sql = null;

        try {
            Conexao.begin();
            stm = Conexao.createStatement();

            if (new KitDAO().verificar(i_administracao.idProduto) > 0) {
                Conexao.commit();
                return;
            }

            sql = new StringBuilder();
            sql.append("INSERT INTO administracaopreco (id_loja, id_notaentrada, id_transferenciaentrada, id_pedido, id_tipoentrada, id_fornecedor,");
            sql.append(" id_produto, qtdembalagem, quantidade, id_aliquota, custocomimposto, custosemimposto, id_tipoadministracaopreco, data, administrado) VALUES (");
            sql.append(i_administracao.idLoja + ", ");
            sql.append((i_administracao.idNotaEntrada == -1 ? "NULL" : i_administracao.idNotaEntrada) + ", ");
            sql.append((i_administracao.idTransferenciaEntrada == -1 ? "NULL" : i_administracao.idTransferenciaEntrada) + ", ");
            sql.append((i_administracao.idPedido == -1 ? "NULL" : i_administracao.idPedido) + ", ");
            sql.append((i_administracao.idTipoEntrada == -1 ? "NULL" : i_administracao.idTipoEntrada) + ", ");
            sql.append(i_administracao.idFornecedor + ", ");
            sql.append(i_administracao.idProduto + ", ");
            sql.append(i_administracao.qtdEmbalagem + ", ");
            sql.append(i_administracao.quantidade + ", ");
            sql.append(i_administracao.idAliquota + ", ");
            sql.append(i_administracao.custoComImposto + ", ");
            sql.append(i_administracao.custoSemImposto + ", ");
            sql.append(i_administracao.idTipoAdministracaoPreco + ", ");
            sql.append("'" + Util.formatDataBanco(new DataProcessamentoDAO().get()) + "', ");
            sql.append("FALSE)");

            stm.execute(sql.toString());

            stm.close();
            Conexao.commit();

        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }
}
