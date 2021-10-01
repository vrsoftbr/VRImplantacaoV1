package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoMySQL;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.vo.enums.TipoEstadoCivil;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;

/**
 *
 * @author Leandro
 */
public class EmporioDAO extends InterfaceDAO {
    
    private boolean clienteSomentePontos = false;

    @Override
    public String getSistema() {
        return "Emporio";
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n" +
                    "  c.customer_key id,\n" +
                    "  cpf.customer_sku_id cnpj,\n" +
                    "  ie.customer_sku_id ie,\n" +
                    "  c.customer_ie,\n" +
                    "  c.customer_name razao,\n" +
                    "  c.customer_name2 fantasia,\n" +
                    "  case st.customer_status when 5 then 1 else 0 end bloqueado,\n" +
                    "  c.customer_address endereco,\n" +
                    "  c.customer_comple complemento,\n" +
                    "  c.customer_neig bairro,\n" +
                    "  c.customer_city municipio,\n" +
                    "  c.customer_state uf,\n" +
                    "  c.customer_zip cep,\n" +
                    "  c.customer_civil_status estado_civil,\n" +
                    "  c.customer_birthday datanascimento,\n" +
                    "  c.customer_date_inc datacadastro,\n" +
                    "  c.customer_gender sexo,\n" +
                    "  c.customer_job_name empresa,\n" +
                    "  c.customer_job_address empresa_endereco,\n" +
                    "  c.customer_job_comple empresa_complemento,\n" +
                    "  c.customer_job_neig empresa_bairro,\n" +
                    "  c.customer_job_city empresa_cidade,\n" +
                    "  c.customer_job_state empresa_uf,\n" +
                    "  c.customer_job_zip empresa_cep,\n" +
                    "  c.customer_job_phone empresa_telefone,\n" +
                    "  c.customer_job_date dataadmissao,\n" +
                    "  c.customer_job_title cargo,\n" +
                    "  st.customer_limit limite,\n" +
                    "  st.customer_amount_left,\n" +
                    "  c.customer_spouse_name nome_conjuge,\n" +
                    "  c.customer_fathers_name nomepai,\n" +
                    "  c.customer_mothers_name nomemae,\n" +
                    "  c.customer_phone1 telefone1,\n" +
                    "  c.customer_phone2 telefone2,\n" +
                    "  c.customer_type,\n" +
                    "  c.customer_email email\n" +
                    "FROM\n" +
                    "  customer c\n" +
                    "  join customer_status st on c.customer_key = st.customer_key\n" +
                    "  left join customer_sku cpf on\n" +
                    "    c.customer_key = cpf.customer_key\n" +
                    "    and cpf.customer_sku_type_key in (1,2,3,4,5)\n" +
                    "    and cpf.customer_sku_id > 999999\n" +
                    "  left join customer_sku ie on\n" +
                    "    c.customer_key = ie.customer_key\n" +
                    "    and ie.customer_sku_type_key in (8)\n" +
                    (isClienteSomentePontos() ? "where st.customer_points > 0\n" : "") +                
                    "order by\n" +
                    "  id"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    
                    imp.setId(rst.getString("id"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setInscricaoestadual(rst.getString("ie"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setBloqueado(rst.getBoolean("bloqueado"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setEstadoCivil(TipoEstadoCivil.NAO_INFORMADO);
                    imp.setDataNascimento(rst.getDate("datanascimento"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setSexo("F".equals(rst.getString("sexo")) ? TipoSexo.FEMININO : TipoSexo.MASCULINO);
                    imp.setEmpresa(rst.getString("empresa"));
                    imp.setEmpresaEndereco(rst.getString("empresa_endereco"));
                    imp.setEmpresaComplemento(rst.getString("empresa_complemento"));
                    imp.setEmpresaBairro(rst.getString("empresa_bairro"));
                    imp.setEmpresaMunicipio(rst.getString("empresa_cidade"));
                    imp.setEmpresaUf(rst.getString("empresa_uf"));
                    imp.setEmpresaCep(rst.getString("empresa_cep"));
                    imp.setEmpresaTelefone(rst.getString("empresa_telefone"));
                    imp.setDataAdmissao(rst.getDate("dataadmissao"));
                    imp.setCargo(rst.getString("cargo"));
                    imp.setValorLimite(rst.getDouble("limite"));
                    imp.setObservacao("Valor restante de credito " + rst.getString("customer_amount_left"));
                    imp.setNomeConjuge(rst.getString("nome_conjuge"));
                    imp.setNomePai(rst.getString("nomepai"));
                    imp.setNomeMae(rst.getString("nomemae"));
                    imp.setTelefone(rst.getString("telefone1"));
                    imp.setCelular(rst.getString("telefone2"));
                    imp.setEmail(rst.getString("email"));
                    if (rst.getInt("customer_type") == 1) {
                        imp.setPermiteCheque(false);
                        imp.setPermiteCreditoRotativo(true);
                    } else if (rst.getInt("customer_type") == 2) {
                        imp.setPermiteCheque(true);
                        imp.setPermiteCreditoRotativo(false);
                    }
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n" +
                    "  c.customer_key,\n" +
                    "  c.store_key,\n" +
                    "  c.pos_number ecf,\n" +
                    "  c.ticket_number cupom,\n" +
                    "  c.start_time dataemissao,\n" +
                    "  c.summary_amount valor\n" +
                    "FROM\n" +
                    "  customer_summary c\n" +
                    "  join customer cl on c.customer_key = cl.customer_key\n" +
                    "where\n" +
                    "  cl.customer_type = 1 and c.summary_status = 1 and c.store_key = " + getLojaOrigem()
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    
                    imp.setId(
                            rst.getString("customer_key") + "-" +
                            rst.getString("store_key") + "-" +
                            rst.getString("ecf") + "-" +
                            rst.getString("cupom") + "-" +
                            rst.getString("dataemissao")
                    );
                    imp.setDataEmissao(rst.getDate("dataemissao"));
                    imp.setDataVencimento(rst.getDate("dataemissao"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setEcf(rst.getString("ecf"));
                    imp.setIdCliente(rst.getString("customer_key"));
                    imp.setNumeroCupom(rst.getString("cupom"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ChequeIMP> getCheques() throws Exception {
        List<ChequeIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n" +
                    "  c.customer_key,\n" +
                    "  c.store_key,\n" +
                    "  c.pos_number ecf,\n" +
                    "  c.ticket_number cupom,\n" +
                    "  c.start_time dataemissao,\n" +
                    "  c.summary_bank banco,\n" +
                    "  c.summary_branch agencia,\n" +
                    "  c.summary_account conta,\n" +
                    "  c.summary_check cheque,\n" +
                    "  c.summary_amount valor,\n" +
                    "  c.summary_date_check datadeposito,\n" +
                    "  c.summary_alinea alinea,\n" +
                    "  cl.customer_phone1 telefone,\n" +
                    "  cl.customer_name nome,\n" +
                    "  cpf.customer_sku_id cnpj\n" +
                    "FROM\n" +
                    "  customer_summary c\n" +
                    "  join customer cl on c.customer_key = cl.customer_key\n" +
                    "  left join customer_sku cpf on\n" +
                    "    cl.customer_key = cpf.customer_key\n" +
                    "    and cpf.customer_sku_type_key in (1,2,3,4,5)\n" +
                    "    and cpf.customer_sku_id > 999999\n" +
                    "where\n" +
                    "  cl.customer_type = 2 and c.summary_status = 1 and c.store_key = " + getLojaOrigem()
            )) {
                while (rst.next()) {
                    ChequeIMP imp = new ChequeIMP();
                    
                    imp.setId(
                            rst.getString("customer_key") + "-" +
                            rst.getString("store_key") + "-" +
                            rst.getString("ecf") + "-" +
                            rst.getString("cupom") + "-" +
                            rst.getString("dataemissao")
                    );
                    imp.setEcf(rst.getString("ecf"));
                    imp.setNumeroCupom(rst.getString("cupom"));
                    imp.setDate(rst.getDate("dataemissao"));
                    imp.setDataDeposito(rst.getDate("datadeposito"));
                    imp.setBanco(rst.getInt("banco"));
                    imp.setAgencia(rst.getString("agencia"));
                    imp.setConta(rst.getString("conta"));
                    imp.setNumeroCheque(rst.getString("cheque"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setAlinea(rst.getInt("alinea"));
                    imp.setTelefone(rst.getString("telefone"));
                    imp.setNome(rst.getString("nome"));
                    imp.setCpf(rst.getString("cnpj"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT store_key, store_name FROM store s order by store_key"
            )) {
                while (rst.next()) {
                    result.add(
                            new Estabelecimento(
                                    rst.getString("store_key"),
                                    rst.getString("store_name")
                            )
                    );
                }
            }
        }
        
        return result;
    }

    public void setClienteSomentePontos(boolean clienteSomentePontos) {
        this.clienteSomentePontos = clienteSomentePontos;
    }

    public boolean isClienteSomentePontos() {
        return clienteSomentePontos;
    }
}
