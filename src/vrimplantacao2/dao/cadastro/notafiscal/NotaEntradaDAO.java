package vrimplantacao2.dao.cadastro.notafiscal;

import java.sql.ResultSet;
import java.sql.Statement;
import vrframework.classe.Conexao;

/**
 * Classe responsável por gerenciar a manipulação dos dados das notas de entradas.
 * @author Leandro
 */
public class NotaEntradaDAO {

    public int getTipoNotaEntrada() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select id from tipoentrada where descricao like 'IMPORTADO VR' limit 1"
            )) {
                if (rst.next()) {
                    return rst.getInt("id");
                }                
            }
            try (ResultSet rst = stm.executeQuery(
                    "insert into public.tipoentrada values (\n" +
                    "    (select coalesce(max(id) + 1, 1) from public.tipoentrada),\n" +
                    "    'IMPORTADO VR',\n" +
                    "    'N',\n" +
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
                    "    false,\n" +
                    "    '1',\n" +
                    "    '1',\n" +
                    "    false,\n" +
                    "    null,\n" +
                    "    null,\n" +
                    "    null,\n" +
                    "    false,\n" +
                    "    false,\n" +
                    "    null,\n" +
                    "    1,\n" +
                    "    false,\n" +
                    "    null,\n" +
                    "    null,\n" +
                    "    false,\n" +
                    "    false,\n" +
                    "    false,\n" +
                    "	false,\n" +
                    "    null,\n" +
                    "    false,\n" +
                    "    false,\n" +
                    "    false,\n" +
                    "    null,\n" +
                    "    false,\n" +
                    "    null\n" +
                    ") returning id"
            )) {
                rst.next();
                return rst.getInt("id");
            }
        }
    }
    
}
