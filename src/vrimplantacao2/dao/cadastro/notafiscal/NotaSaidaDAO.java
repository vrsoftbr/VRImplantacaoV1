package vrimplantacao2.dao.cadastro.notafiscal;

import java.sql.ResultSet;
import java.sql.Statement;
import vrframework.classe.Conexao;
import vrimplantacao2.vo.cadastro.notafiscal.NotaSaida;
import vrimplantacao2.vo.importacao.NotaFiscalIMP;

/**
 * Classe responsável por gerenciar a manipulação dos dados das notas de saída.
 * @author Leandro
 */
public class NotaSaidaDAO {

    public int getTipoNotaSaida() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select id from tiposaida where descricao like 'IMPORTADO VR'"
            )) {
                if (rst.next()) {
                    return rst.getInt("id");
                }
            }
            try (ResultSet rst = stm.executeQuery(
                    "insert into tiposaida values (\n" +
                    "	(select coalesce(max(id) + 1, 1) from tiposaida),\n" +
                    "    'IMPORTADO VR',\n" +
                    "    false,\n" +
                    "    false,\n" +
                    "    '1',\n" +
                    "    false,\n" +
                    "    false,\n" +
                    "    false,\n" +
                    "    false,\n" +
                    "    false,\n" +
                    "    false,\n" +
                    "    false,\n" +
                    "    false,\n" +
                    "    false,\n" +
                    "    false,\n" +
                    "    false,\n" +
                    "    false,\n" +
                    "    false,\n" +
                    "    false,\n" +
                    "    false,\n" +
                    "    null,\n" +
                    "    'S',\n" +
                    "    false,\n" +
                    "    false,\n" +
                    "    null,\n" +
                    "    null,\n" +
                    "    null,\n" +
                    "    false,\n" +
                    "    false,\n" +
                    "    null,\n" +
                    "    1,\n" +
                    "    false,\n" +
                    "    false,\n" +
                    "    false,\n" +
                    "    false,\n" +
                    "    false,\n" +
                    "    false,\n" +
                    "    null,\n" +
                    "    null,\n" +
                    "    false\n" +
                    ") returning id"
            )) {
                rst.next();
                return rst.getInt("id");
            }
        }
    }

    public void eliminarNota(int id) throws Exception {
        throw new UnsupportedOperationException("Funcao ainda nao suportada.");
    }

    public Integer getNota(NotaFiscalIMP imp) throws Exception {
        throw new UnsupportedOperationException("Funcao ainda nao suportada.");
    }

    public void salvar(NotaSaida ns) throws Exception {
        throw new UnsupportedOperationException("Funcao ainda nao suportada.");
    }

    public void salvarItens(NotaSaida ns) throws Exception {
        throw new UnsupportedOperationException("Funcao ainda nao suportada.");
    }
    
}
