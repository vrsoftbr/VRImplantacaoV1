/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.relatorios.relatoriosDAO;

import java.sql.Statement;
import vrframework.classe.Conexao;

/**
 *
 * @author Michael
 */
public class ExecutaSpedDAO {

    public void executaSped() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(
                    "insert into sped.produtoalteracao(id_produto, datainicial, datafinal, descricaoanterior, id_produtoanterior)\n"
                    + "with alt as (select \n"
                    + "	ca.codigoatual, \n"
                    + "	now() data1, \n"
                    + "	now() data2, \n"
                    + "	substring(descricao,1,60) descricao, \n"
                    + "	nullif(trim(regexp_replace(ca.impid,'[^0-9]','','g')),'')::bigint idanterior\n"
                    + "	--,ca.impid\n"
                    + "	--,length(trim(regexp_replace(ca.impid,'^[0-9]','',''))) tamanho\n"
                    + "from \n"
                    + "	implantacao.codant_produto ca \n"
                    + "where \n"
                    + "	trim(ca.impid) similar to '[0-9]+'\n"
                    + "	and length(trim(regexp_replace(ca.impid,'[^0-9]','','g'))) < 19\n"
                    + "	and not ca.codigoatual in (select distinct id_produto from sped.produtoalteracao))\n"
                    + "select\n"
                    + "	*\n"
                    + "from alt \n"
                    + "where\n"
                    + "	idanterior != codigoatual "
            );
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
