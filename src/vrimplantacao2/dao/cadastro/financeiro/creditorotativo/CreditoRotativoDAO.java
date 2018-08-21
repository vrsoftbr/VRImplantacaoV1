package vrimplantacao2.dao.cadastro.financeiro.creditorotativo;

import java.sql.ResultSet;
import java.sql.Statement;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.cliente.rotativo.CreditoRotativoVO;

/**
 *
 * @author Leandro
 */
public class CreditoRotativoDAO {

    public void gravarRotativo(CreditoRotativoVO cred) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            sql.setTableName("recebercreditorotativo");
            sql.put("id_loja", cred.getId_loja());
            sql.put("dataemissao", cred.getDataEmissao());
            sql.put("numerocupom", cred.getNumeroCupom());
            sql.put("ecf", cred.getEcf());
            sql.put("valor", cred.getValor());
            sql.put("lancamentomanual", false);
            sql.put("observacao", cred.getObservacao());
            sql.put("id_situacaorecebercreditorotativo", cred.getSituacaoCreditoRotativo().getID());
            sql.put("id_clientepreferencial", cred.getId_clientePreferencial());
            sql.put("datavencimento", cred.getDataVencimento());
            sql.put("matricula", cred.getMatricula());
            sql.put("parcela", cred.getParcela());
            sql.put("valorjuros", cred.getValorJuros());
            sql.put("id_boleto", cred.getId_boleto(), 0);
            sql.put("id_tipolocalcobranca", cred.getId_tipoLocalCobranca());
            sql.put("valormulta", cred.getValorMulta());
            sql.put("justificativa", cred.getJustificativa());
            sql.put("exportado", false);
            sql.put("datahoraalteracao", cred.getDataHoraAlteracao());
            sql.put("nomedependente", cred.getNomeDependente());
            sql.put("cpfdependente", cred.getCpfDependente(), 0);
            sql.put("dataexportacao", cred.getDataExportacao());
            sql.getReturning().add("id");
            try (ResultSet rst = stm.executeQuery(
                    sql.getInsert()
            )) {
                rst.next();
                cred.setId(rst.getInt("id"));
            }
        }
    }

    public void verificarBaixado(int id) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(
                "do $$\n" +
                "declare\n" +
                "	v_credrot integer = " + id + ";\n" +
                "	v_valor recebercreditorotativo.valor%type;\n" +
                "	v_pago recebercreditorotativoitem.valor%type;\n" +
                "	v_situacao recebercreditorotativo.id_situacaorecebercreditorotativo%type;\n" +
                "begin\n" +
                "	select \n" +
                "		cred.valor,\n" +
                "		cred.id_situacaorecebercreditorotativo,\n" +
                "		coalesce((select sum(valor) from recebercreditorotativoitem where id_recebercreditorotativo = cred.id),0) total\n" +
                "	from \n" +
                "		recebercreditorotativo cred\n" +
                "	where \n" +
                "		cred.id = v_credrot\n" +
                "	into\n" +
                "		v_valor,\n" +
                "		v_situacao,\n" +
                "		v_pago;\n" +
                "\n" +
                "	if (not v_valor is null) then\n" +
                "		--Se o rotativo estiver em aberto e se o total pago \n" +
                "		--for maior ou igual ao total do rotativo, coloca como baixado.\n" +
                "		if (v_pago >= v_valor and v_situacao = 0) then\n" +
                "			update recebercreditorotativo set id_situacaorecebercreditorotativo = 1 where id = v_credrot;\n" +                
                "		end if;\n" +
                "	end if;\n" +
                "end;\n" +
                "$$;"
            );
        }
    } 
    
    public void excluirCreditoRotativoCheque(int id_loja) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(
                    "begin;\n" +
                    "do $$\n" +
                    "declare idLoja integer;\n" +
                    "begin\n" +
                    "	idLoja = " + id_loja + "; \n" +
                    "	delete from \n" +
                    "		recebercreditorotativo \n" +
                    "	where \n" +
                    "		id in\n" +
                    "			(select\n" +
                    "				r.id\n" +
                    "			from\n" +
                    "				recebercreditorotativo r \n" +
                    "			inner join\n" +
                    "				implantacao.codant_recebercreditorotativo imp on imp.codigoatual = r.id) and\n" +
                    "		id_loja = idLoja;\n" +
                    "	alter sequence recebercreditorotativo_id_seq restart with 1;\n" +
                    "	delete from implantacao.codant_recebercreditorotativo;\n" +
                    "\n" +
                    "	delete from\n" +
                    "		recebercheque\n" +
                    "	where\n" +
                    "		id in\n" +
                    "			(select\n" +
                    "				ch.id\n" +
                    "			from\n" +
                    "				recebercheque ch\n" +
                    "			inner join\n" +
                    "				implantacao.codant_recebercheque imp on imp.codigoatual = ch.id) and\n" +
                    "		id_loja = idLoja;\n" +
                    "	alter sequence recebercheque_id_seq restart with 1;\n" +
                    "	delete from implantacao.codant_recebercheque;\n" +
                    "end\n" +
                    "$$;\n" +
                    "commit;");
            }
    }
}
