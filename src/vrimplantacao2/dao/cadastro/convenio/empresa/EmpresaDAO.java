package vrimplantacao2.dao.cadastro.convenio.empresa;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedHashSet;
import java.util.Set;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.convenio.empresa.ConvenioEmpresaVO;

/**
 *
 * @author Leandro
 */
public class EmpresaDAO {

    public Set<Long> getCnpjExistentes() throws Exception {
        Set<Long> result = new LinkedHashSet<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select cnpj from empresa order by cnpj"
            )) {
                while (rst.next()) {
                    result.add(rst.getLong("cnpj"));
                }
            }
        }
        return result;        
    }

    public void gravarEmpresa(ConvenioEmpresaVO vo) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            sql.setTableName("empresa");
            sql.put("id", vo.getId());
            sql.put("razaoSocial", vo.getRazaoSocial());
            sql.put("endereco", vo.getEndereco());
            sql.put("bairro", vo.getBairro());
            sql.put("id_municipio", vo.getId_municipio());
            sql.put("telefone", vo.getTelefone());
            sql.put("cep", vo.getCep());
            sql.put("inscricaoEstadual", vo.getInscricaoEstadual());
            sql.put("cnpj", vo.getCnpj());
            sql.put("dataInicio", vo.getDataInicio());
            sql.put("dataTermino", vo.getDataTermino());
            sql.put("id_situacaocadastro", vo.getSituacaoCadastro().getId());
            sql.put("id_tipoinscricao", vo.getTipoInscricao().getId());
            sql.put("renovacaoAutomatica", vo.isRenovacaoAutomatica());
            sql.put("percentualDesconto", vo.getPercentualDesconto());
            sql.put("diaPagamento", vo.getDiaPagamento());
            sql.put("bloqueado", vo.isBloqueado());
            sql.put("dataDesbloqueio", vo.getDataBloqueio());
            sql.put("id_estado", vo.getId_estado());
            sql.put("diaInicioRenovacao", vo.getDiaInicioRenovacao());
            sql.put("diaTerminoRenovacao", vo.getDiaTerminoRenovacao());
            sql.put("tipoTerminoRenovacao", vo.getTipoTerminoRenovacao().getId());
            sql.put("dataBloqueio", vo.getDataBloqueio());
            sql.put("observacao", vo.getObservacao());
            sql.put("numero", vo.getNumero());
            sql.put("complemento", vo.getComplemento());
            sql.put("id_contaContabilFiscalPassivo", vo.getId_contaContabilFiscalPassivo(), 0);
            sql.put("id_contaContabilFiscalAtivo", vo.getId_contaContabilFiscalAtivo(), 0);

            stm.execute(sql.getInsert());
        }
    }
    
}
