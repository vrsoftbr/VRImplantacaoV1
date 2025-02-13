package vrimplantacao2_5.dao.copias;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import vrframework.classe.Conexao;
import vrframework.classe.Util;

/**
 *
 * @author Wesley
 */
public class CopiaEntreLojasDao {

    public void copiaInfoProdutos(Integer lojaOrigem, Integer lojaDestino, List<String> listaDeOpcoes) throws Exception {

        StringBuilder sql = new StringBuilder("UPDATE produtocomplemento a SET ");
        sql.append(String.join(", ", listaDeOpcoes));
        sql.append(" FROM (SELECT id_produto, precovenda, precodiaseguinte, custocomimposto, custosemimposto, margem, margemminima, margemmaxima, id_situacaocadastro, estoque, estoquemaximo, estoqueminimo  FROM produtocomplemento WHERE id_loja = ?) b ");
        sql.append("WHERE a.id_produto = b.id_produto AND a.id_loja = ?");

        try (PreparedStatement pst = Conexao.prepareStatement(sql.toString())) {

            pst.setInt(1, lojaOrigem);
            pst.setInt(2, lojaDestino);

            int numeroAtualizado = pst.executeUpdate();

            Util.exibirMensagem("Atualizado um total de " + numeroAtualizado + " produtos na loja " + lojaDestino, "Número de atualizações");
        } catch (Exception e) {
            throw new RuntimeException("Erro ao tentar copiar as informações de um loja para outra.", e);
        }
    }

    public void copiaPromocoes(Integer lojaOrigem, Integer lojaDestino) throws SQLException {

        try (Statement stm = Conexao.createStatement()) {
            stm.execute(copiaPromocaoScript(lojaOrigem, lojaDestino));
            stm.execute(copiaPromocaoItemScript(lojaOrigem, lojaDestino));
            stm.execute(copiaPromocaoFinalizadoraScript(lojaOrigem, lojaDestino));
            stm.execute(copiaPromocaoDescontoScript(lojaOrigem, lojaDestino));

            Util.exibirMensagem("Copiado as promoçoes da loja " + lojaOrigem + " para a loja " +lojaDestino, "Confirmação de copia");
        } catch (Exception e) {
            throw new RuntimeException("Erro ao tentar copiar as promoções de um loja para outra.", e);
        }
    }

    private String copiaPromocaoScript(Integer lojaOrigem, Integer lojaDestino) {

        String sql
                = "insert into promocao (id,id_loja,descricao,datainicio,datatermino,pontuacao,quantidade,qtdcupom,id_situacaocadastro,id_tipopromocao,\n"
                + "valor,controle,id_tipopercentualvalor,id_tipoquantidade,aplicatodos,cupom,valordesconto,valorreferenteitenslista,verificaprodutosauditados,\n"
                + "datalimiteresgatecupom,id_tipopercentualvalordesconto,valorpaga,desconsideraritem,qtdlimite,somenteclubevantagens,diasexpiracao,\n"
                + "utilizaquantidadeproporcional,desconsideraprodutoemoferta) \n"
                + "select (select max(id) from promocao) + row_number() over(), \n"
                + "" + lojaDestino + ",descricao,datainicio,datatermino,pontuacao,quantidade,qtdcupom,id_situacaocadastro,id_tipopromocao,valor,controle,id_tipopercentualvalor,\n"
                + "id_tipoquantidade,aplicatodos,cupom,valordesconto,valorreferenteitenslista,verificaprodutosauditados,datalimiteresgatecupom,id_tipopercentualvalordesconto,\n"
                + "valorpaga,desconsideraritem,qtdlimite,somenteclubevantagens,diasexpiracao,utilizaquantidadeproporcional,desconsideraprodutoemoferta\n"
                + "from promocao where id_loja = " + lojaOrigem + "\n"
                + "and datatermino >= now();";

        return sql;
    }

    public String copiaPromocaoItemScript(Integer lojaOrigem, Integer lojaDestino) {
        String sql
                = "INSERT INTO promocaoitem (id_promocao, id_produto, precovenda) \n"
                + "SELECT p2.id, pi.id_produto, pi.precovenda \n"
                + "FROM promocao p \n"
                + "JOIN promocaoitem pi ON p.id = pi.id_promocao \n"
                + "JOIN promocao p2 ON p.descricao = p2.descricao \n"
                + "AND p2.id_loja = " + lojaDestino + " \n"
                + "WHERE p.id_loja = " + lojaOrigem + " \n"
                + "AND p.datatermino >= NOW() \n"
                + "AND p2.id NOT IN ( \n"
                + "SELECT p.id_promocao FROM promocaoitem p \n"
                + "JOIN promocao p2 ON p2.id = p.id_promocao AND p2.id_loja = " + lojaDestino + ")";
        return sql;
    }

    public String copiaPromocaoFinalizadoraScript(Integer lojaOrigem, Integer lojaDestino) {
        String sql
                = "insert into promocaofinalizadora (id_promocao, id_finalizadora)\n"
                + "select\n"
                + "p3.id ,\n"
                + "p2.id_finalizadora \n"
                + "from promocao p \n"
                + "join promocaofinalizadora p2 on p2.id_promocao = p.id \n"
                + "join promocao p3 on p3.descricao = p.descricao and p3.id_loja = " + lojaDestino + "\n"
                + "where p.id_loja = " + lojaOrigem + "\n"
                + "and p.datatermino >= now();";
        return sql;
    }

    public String copiaPromocaoDescontoScript(Integer lojaOrigem, Integer lojaDestino) {
        String sql
                = "insert into promocaodesconto  (id_promocao, id_produto, desconto,qtdelimite) \n"
                + " select \n"
                + " p2.id,\n"
                + " pd.id_produto,\n"
                + " pd.desconto,\n"
                + " pd.qtdelimite\n"
                + " from promocao p\n"
                + " join promocaodesconto pd on p.id = pd.id_promocao \n"
                + " join promocao p2 on p.descricao = p2.descricao and p2.id_loja =  " + lojaDestino + "\n"
                + " where \n"
                + " p.id_loja = " + lojaOrigem + "\n"
                + " and p.datatermino >= now();";
        return sql;
    }
}
