package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoDBF;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.vo.importacao.ClienteIMP;

/**
 *
 * @author Importacao
 */
public class DtComDAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "DTCOM";
    }
    
    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try(Statement stm = ConexaoDBF.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n"
                      + "codloja,\n"
                      + "nomeloja\n"
                  + "from\n"
                      + "lojas")) {
                while(rs.next()) {
                    result.add(new Estabelecimento(rs.getString("codloja"), rs.getString("nomeloja")));
                }
            }
        }
        return result;
    }
    
    @Override
   public List<ClienteIMP> getClientes() throws Exception {
       List<ClienteIMP> result = new ArrayList<>();
       try(Statement stm = ConexaoDBF.getConexao().createStatement()) {
           try(ResultSet rs = stm.executeQuery(
                   "select \n" +
                    "	codigo,\n" +
                    "	cpf,\n" +
                    "   inscricao,\n" +
                    "	identidade,\n" +
                    "	nome1,\n" +
                    "	endereco,\n" +
                    "	bairro,\n" +
                    "	cidade,\n" +
                    "	estado,\n" +
                    "	cep,\n" +
                    "	telefone,\n" +
                    "	fonetrab,\n" +
                    "	limite,\n" +
                    "	situacao,\n" +
                    "	observacao,\n" +
                    "	data_cad,\n" +
                    "	motivo,\n" +
                    "	filiacao,\n" +
                    "	nascimento,\n" +
                    "	cod_mun,\n" +
                    "	cod_uf,\n" +
                    "	coment1,\n" +
                    "   coment2 \n" +       
                   "from\n" +
                    "	clientes\n" +
                   "order by\n" +
                    "	codigo")) {
               while(rs.next()) {
                   ClienteIMP imp = new ClienteIMP();
                   imp.setId(rs.getString("codigo"));
                   imp.setCnpj(rs.getString("cpf").substring(2, rs.getString("cpf").length()));
                   if((!"".equals(rs.getString("identidade"))) && !"000000000000".equals(rs.getString("identidade"))){
                       imp.setInscricaoestadual("identidade");
                   } else if ((!"".equals(rs.getString("inscricao"))) && (!"00000000000000".equals(rs.getString("inscricao")))){
                       imp.setInscricaoestadual("inscricao");
                   } else {
                       imp.setInscricaoestadual("ISENTO");
                   }
                   imp.setInscricaoestadual(rs.getString("inscricao"));
                   imp.setRazao(rs.getString("nome1"));
                   imp.setEndereco(rs.getString("endereco"));
                   imp.setBairro(rs.getString("bairro"));
                   imp.setMunicipio(rs.getString("cidade"));
                   imp.setMunicipioIBGE(rs.getInt("cod_mun"));
                   imp.setUf(rs.getString("estado"));
                   imp.setUfIBGE(rs.getInt("cod_uf"));
                   imp.setCep(rs.getString("cep"));
                   imp.setTelefone(rs.getString("telefone"));
                   if(!"".equals(rs.getString("fonetrab"))){
                        imp.addContato("1", "Tel. Trabalho", rs.getString("fonetrab"), null, null);
                   }
                   imp.setValorLimite(rs.getDouble("limite"));
                   if((rs.getInt("situacao")) == 1) {
                       imp.setAtivo(true);
                   } else {
                       imp.setAtivo(false);
                   }
                   String obs2;
                   String obs3;
                   if(!"".equals(rs.getString("coment1"))) {
                       obs2 = "Obs2: " + rs.getString("coment1");
                   } else {
                       obs2 = ""; 
                   }
                   if(!"".equals(rs.getString("coment2"))) {
                       obs3 = "Obs3: " + rs.getString("coment2");
                   } else {
                       obs3 = ""; 
                   }
                   if(!"".equals(rs.getString("coment3"))){
                        imp.setObservacao2(rs.getString("coment3"));
                   }
                   imp.setObservacao(rs.getString("observacao") + obs2 + obs3);
                   imp.setDataCadastro(rs.getDate("data_cad"));
                   imp.setDataNascimento(rs.getDate("nascimento"));
                   
                   result.add(imp);
               }
           }
       }
       
       return result;
   }
}
