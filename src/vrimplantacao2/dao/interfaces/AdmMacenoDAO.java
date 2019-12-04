package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao.classe.ConexaoDBF;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoPagamentoAgrupadoIMP;

/**
 *
 * @author leandro
 */
public class AdmMacenoDAO extends InterfaceDAO {

    public String complemento;
    public String diretorio;
    
    @Override
    public String getSistema() {
        if (complemento == null || "".equals(complemento.trim())) {
            return "Adm(Rio Preto)";
        } else {
            return "Adm(Rio Preto) - " + complemento.trim();
        }
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();        
        
        try (Statement stm = ConexaoDBF.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select * from FILE002"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    
                    imp.setId(rst.getString("CLI01"));
                    imp.setRazao(rst.getString("CLI02"));
                    imp.setCnpj(rst.getString("CLI03"));
                    imp.setNomeConjuge(rst.getString("CLI04"));
                    imp.setEndereco(rst.getString("CLI08"));
                    imp.setCep(rst.getString("CLI09"));
                    imp.setBairro(rst.getString("CLI10"));
                    imp.setUf(rst.getString("CLI12"));
                    imp.setAtivo("S".equals(rst.getString("CLI14")));
                    imp.setTelefone(rst.getString("CLI15"));
                    imp.setInscricaoestadual(rst.getString("CLI16"));
                    imp.setDataNascimento(rst.getDate("CLI17"));
                    imp.setCargo(rst.getString("CLI21"));
                    imp.setValorLimite(rst.getDouble("CLI23"));
                    imp.setDataCadastro(rst.getDate("CLI24"));
                    imp.setCobrancaEndereco(rst.getString("CLI43"));
                    imp.setCobrancaBairro(rst.getString("CLI44"));
                    imp.setCobrancaCep(rst.getString("CLI45"));
                    imp.setCobrancaUf(rst.getString("CLI47"));
                    imp.setObservacao2(rst.getString("CLI58"));
                    imp.setOrgaoemissor(rst.getString("CLI71"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoDBF.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	CCM01 id_cliente,\n" +
                    "	CCM03 datacadastro,\n" +
                    "	CCM04 vencimento,\n" +
                    "	CCM07 valor,\n" +
                    "	CCM09 cupom\n" +
                    "from\n" +
                    "	FILE003\n" +
                    "where\n" +
                    "	CCM08 = 'D'"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(String.format("%s-%s-%s-%s-%s", 
                            rst.getString("id_cliente"),
                            rst.getString("datacadastro"),
                            rst.getString("vencimento"),
                            rst.getString("valor"),
                            rst.getString("cupom")
                    ));
                    imp.setIdCliente(rst.getString("id_cliente"));
                    imp.setDataEmissao(rst.getDate("datacadastro"));
                    imp.setDataVencimento(rst.getDate("vencimento"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setNumeroCupom(rst.getString("cupom"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<CreditoRotativoPagamentoAgrupadoIMP> getCreditoRotativoPagamentoAgrupado() throws Exception {
        List<CreditoRotativoPagamentoAgrupadoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoDBF.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	CCM01 id_cliente,\n" +
                    "	sum(CCM07) valor\n" +
                    "from \n" +
                    "	FILE003\n" +
                    "where\n" +
                    "	CCM08 = 'C'\n" +
                    "group by\n" +
                    "	CCM01"
            )) {
                while (rst.next()) {
                    CreditoRotativoPagamentoAgrupadoIMP imp = new CreditoRotativoPagamentoAgrupadoIMP();
                    
                    imp.setIdCliente(rst.getString("id_cliente"));
                    imp.setValor(rst.getDouble("valor"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    /*
    AS LINHAS ABAIXO SÃO PURA GAMBIARRA, 
    FOI PROVISÓRIO PRA GERAR UM RELATÓRIO PRO BOLT, PARA LISTAR AS CONTAS QUE FORAM BAIXADAS NO SISTEMA ADM
    E QUE ESTÃO EM ABERTO NO VR
    
    
    SE POR ACASO ALGUÉM PODE ME COMUNICAR PRA SABER A LÓGICA KKKKKKKKK
   
    */
    public List<CreditoRotativoIMP> getCreditoRotativoBaixado() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoDBF.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	CCM01 id_cliente,\n"
                    + "	CCM03 datacadastro,\n"
                    + "	CCM04 vencimento,\n"
                    + "	CCM07 valor,\n"
                    + "	CCM09 cupom\n"
                    + "from\n"
                    + "	FILE003\n"
                    + "where\n"
                    + "	CCM08 = 'D'\n"
                    + "and CCM02 = 0\n"
                    + "and CCM02 is not null\n"
                    + "and CCM03 >= '2019-01-01'"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(String.format("%s-%s-%s-%s-%s", 
                            rst.getString("id_cliente"),
                            rst.getString("datacadastro"),
                            rst.getString("vencimento"),
                            rst.getString("valor"),
                            rst.getString("cupom")
                    ));
                    imp.setIdCliente(rst.getString("id_cliente"));
                    imp.setDataEmissao(rst.getDate("datacadastro"));
                    imp.setDataVencimento(rst.getDate("vencimento"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setNumeroCupom(rst.getString("cupom"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }
    
    public void migrarCreditoRotativoBaixado() throws Exception {
        List<CreditoRotativoIMP> imp = new ArrayList<>();
        
        try {
            ProgressBar.setStatus("Carregando dados para importação...Contas receber baixadas ADM...");
            
            imp = getCreditoRotativoBaixado();
            
            ProgressBar.setMaximum(imp.size());
            salvar(imp);
            
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    
    public void salvar(List<CreditoRotativoIMP> vo) throws Exception {        
        StringBuilder sql = null;
        Statement stm = null;
        
        try {
            Conexao.begin();
            
            stm = Conexao.createStatement();
            
            ProgressBar.setStatus("Gravando dados...");
            
            for (CreditoRotativoIMP imp: vo) {
                
                sql = new StringBuilder();
                sql.append("insert into implantacao.contas_baixadas_adm (");
                sql.append("id, id_cliente, datacadastro, vencimento, valor, cupom )");
                sql.append("values (");
                sql.append("'" + imp.getId() + "', ");
                sql.append("'" + imp.getIdCliente() + "', ");
                sql.append("'" + imp.getDataEmissao() + "', ");
                sql.append("'" + imp.getDataVencimento() + "', ");
                sql.append(imp.getValor() + ", ");
                sql.append("'" + imp.getNumeroCupom() + "');");
                
                stm.execute(sql.toString());
                
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
