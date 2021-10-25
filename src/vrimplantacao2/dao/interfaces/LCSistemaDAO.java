package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoMySQL;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.vo.enums.TipoEstadoCivil;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;

/**
 *
 * @author Leandro
 */
public class LCSistemaDAO extends InterfaceDAO {

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select id, fantasia from empresa e"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("id"), rst.getString("fantasia")));
                }
            }
        }

        return result;
    }

    @Override
    public String getSistema() {
        return "LC Sistema";
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	c.id, \n"
                    + "	c.cpf_cnpj,\n"
                    + "	c.ie,\n"
                    + "	c.im,\n"
                    + "	c.nome,\n"
                    + "	c.rg,\n"
                    + "	c.razao_social,\n"
                    + "	c.endereco,\n"
                    + "	c.numero,\n"
                    + "	c.referencia,\n"
                    + "	c.cep,\n"
                    + "	c.bairro,\n"
                    + "	c2.nome cidade,\n"
                    + "	e.nome estado,\n"
                    + "	c.telefone,\n"
                    + "	c.tel_comercial,\n"
                    + "	c.fax,\n"
                    + "	c.limite_credito,\n"
                    + "	c.ativo,\n"
                    + "	c.data_cadastro,\n"
                    + "	c.email_adi,\n"
                    + "	c.apelido_adi,\n"
                    + "	c.nascimento_adi,\n"
                    + "	c.sexo_adi,\n"
                    + "	c.estcivil_adi,\n"
                    + "	c.pai_adi,\n"
                    + "	c.mae_adi\n"
                    + "from \n"
                    + "	cliente c\n"
                    + "left join cidades c2 on c.id_cidade = c2.id \n"
                    + "left join estados e on c.id_estado = e.id"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rst.getString("id"));
                    imp.setCnpj(rst.getString("cpf_cnpj"));
                    imp.setInscricaoestadual(rst.getString("ie"));

                    if (imp.getInscricaoestadual().trim().isEmpty()) {
                        imp.setInscricaoestadual(rst.getString("rg"));
                    }

                    imp.setRazao(rst.getString("nome"));
                    imp.setFantasia(rst.getString("apelido_adi"));
                    imp.setAtivo(rst.getBoolean("ativo"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setComplemento(rst.getString("referencia"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("estado"));
                    imp.setCep(rst.getString("cep"));

                    String estCiv = Utils.acertarTexto(rst.getString("estcivil_adi"));
                    String sexo = Utils.acertarTexto(rst.getString("sexo_adi"));

                    if (sexo != null && !sexo.isEmpty() && sexo.equals("Feminino")) {
                        imp.setSexo(TipoSexo.FEMININO);
                    } else {
                        imp.setSexo(TipoSexo.MASCULINO);
                    }

                    if (estCiv != null && !estCiv.isEmpty()
                            && estCiv.equals("Casado")) {
                        imp.setEstadoCivil(TipoEstadoCivil.CASADO);
                    } else if (estCiv != null && !estCiv.isEmpty()
                            && estCiv.equals("Solteiro")) {
                        imp.setEstadoCivil(TipoEstadoCivil.SOLTEIRO);
                    } else if (estCiv != null && !estCiv.isEmpty()
                            && estCiv.equals("Viuvo")) {
                        imp.setEstadoCivil(TipoEstadoCivil.VIUVO);
                    }

                    imp.setDataCadastro(rst.getDate("data_cadastro"));
                    imp.setDataNascimento(rst.getDate("nascimento_adi"));
                    imp.setValorLimite(rst.getDouble("limite_credito"));
                    if (imp.getValorLimite() > 0) {
                        imp.setPermiteCheque(true);
                        imp.setPermiteCreditoRotativo(true);
                    }

                    imp.setNomePai(rst.getString("pai_adi"));
                    imp.setNomeMae(rst.getString("mae_adi"));
                    imp.setTelefone(rst.getString("telefone"));
                    imp.setEmail(rst.getString("email_adi"));
                    imp.setFax(rst.getString("fax"));
                    imp.addCelular("TEL COMERCIAL", rst.getString("tel_comercial"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	r.id,\n" +
                    "	r.id_cliente,\n" +
                    "	r.documento,\n" +
                    "	r.emissao,\n" +
                    "	r.vencimento,\n" +
                    "	r.valor_original + r.juros_rec total,\n" +
                    "	r.valor_arec,\n" +
                    "	r.valor_rec, \n" +
                    "	r.parcela \n" +
                    "from \n" +
                    "	receber r \n" +
                    "where \n" +
                    "	r.id_empresa = " + getLojaOrigem() + " and \n" +
                    "	r.data_rec is null\n" +
                    "order by \n" +
                    "	r.emissao")) {
                while (rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    
                    imp.setId(rs.getString("id"));
                    imp.setIdCliente(rs.getString("id_cliente"));
                    imp.setNumeroCupom(rs.getString("documento"));
                    imp.setDataEmissao(rs.getDate("emissao"));
                    imp.setDataVencimento(rs.getDate("vencimento"));
                    imp.setValor(rs.getDouble("total"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

}
