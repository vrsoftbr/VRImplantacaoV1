package vrimplantacao2.dao.interfaces;

import com.microsoft.sqlserver.jdbc.SQLServerDriver;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;

/**
 *
 * @author Alan
 */
public class NATISistemasDAO extends InterfaceDAO {
    public String v_lojaMesmoId;

    @Override
    public String getSistema() {
        return "NATISistemas";
    }

    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select idCliente cod_empresa, concat(idCliente,' - ',stcliente) descricao from Master"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("cod_empresa"), rst.getString("descricao")));
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	p.idProduto as importId,\n"
                    + "	dtcriacao as dataCadastro,\n"
                    + "	dtultimaalteracao as dataAlteracao,\n"
                    + "	ean.idcodigo as ean,\n"
                    + "	med.stmedida as tipoEmbalagem,\n"
                    + "	flbalanca as eBalanca,\n"
                    + "	nrvalidade as validade,\n"
                    + "	stproduto as descricaoCompleta,\n"
                    + "	stprodutoabreviado as descricaoReduzida,\n"
                    + "	p.idCategoria as codMercadologico1,\n"
                    + "	p.idSubCategoria as codMercadologico2,\n"
                    + "	p.idSubCategoria as codMercadologico3,\n"
                    + "	est.nrquantidade as estoque,\n"
                    + "	vrmargemlucro as margem,\n"
                    + "	vrcusto as custoSemImposto,\n"
                    + "	vrunitario as precovenda,\n"
                    + "	p.flativo as situacaoCadastro,\n"
                    + "	nrncm as ncm,\n"
                    + "	stcest as cest,\n"
                    + "	ali.cstpis as piscofinsCstDebito,\n"
                    + "	ali.cstpis as piscofinsCstCredito,\n"
                    + "	ali.csticms as icmsCstEntrada,\n"
                    + "	ali.aliquotaicms as icmsAliqEntrada,\n"
                    + "	ali.csticms as icmsCstSaida,\n"
                    + "	ali.aliquotaicms as icmsAliqSaida\n"
                    + "from nati2.dbo.prd_produtos p\n"
                    + "	left join nati2.dbo.prd_Codigos ean\n"
                    + "		on ean.idproduto = p.idproduto\n"
                    + "	left join nati2.dbo.prd_Medidas med\n"
                    + "		on p.idmedida = med.idmedida\n"
                    + "	left join nati2.dbo.est_Saldos est\n"
                    + "		on est.idproduto = p.idproduto\n"
                    + "	left join  nati2.dbo.prd_aliquotas ali\n"
                    + "		on p.idaliquota = ali.idaliquota\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("importid"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setDataAlteracao(rst.getDate("dataalteracao"));
                    imp.setEan(rst.getString("ean"));
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    imp.seteBalanca(rst.getBoolean("ebalanca"));
                    imp.setValidade(rst.getInt("validade"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setCodMercadologico1(rst.getString("codMercadologico1"));
                    imp.setCodMercadologico2(rst.getString("codMercadologico2"));
                    imp.setCodMercadologico3("1");
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setSituacaoCadastro(rst.getInt("situacaocadastro"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getString("piscofinsCstDebito"));
                    imp.setPiscofinsCstCredito(rst.getString("piscofinsCstCredito"));
                    imp.setIcmsCstEntrada(rst.getInt("icmsCstEntrada"));
                    imp.setIcmsAliqEntrada(rst.getDouble("icmsAliqEntrada"));
                    imp.setIcmsCstSaida(rst.getInt("icmsCstSaida"));
                    imp.setIcmsAliqSaida(rst.getDouble("icmsAliqSaida"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery("select\n"
                    + "	c.idcliente as id,\n"
                    + "	c.stcpf_cnpj as cnpj,\n"
                    + "	c.strg_ie as inscricaoestadual,\n"
                    + "	c.stcliente as razao,\n"
                    + "	c.stnomefantasia as fantasia,\n"
                    + "	c.flativo as ativo,\n"
                    + "	c.flbloqueado as bloqueado,\n"
                    + "	e.stendereco as endereco,\n"
                    + "	c.nrnumero as numero,\n"
                    + "	c.stcomplemento as complemento,\n"
                    + "	e.stbairro as bairro,\n"
                    + "	e.stcidade as municipio,\n"
                    + "	e.stestado as uf,\n"
                    + "	e.stcep as cep,\n"
                    + "	c.dtnascimento as dataNascimento,\n"
                    + "	c.dtcadastro as dataCadastro,\n"
                    + "	c.stsexo as sexo,\n"
                    + "	c.stempresa as empresa,\n"
                    + "	c.vrlimite as valorLimite,\n"
                    + "	c.stobs as observacao,\n"
                    + "	c.stemail as email,\n"
                    + "	c.vrlimite as limiteCompra\n"
                    + "from nati2.dbo.dlv_Clientes c\n"
                    + "	left join nati2.dbo.dlv_CEPs e\n"
                    + "     on e.idcep = c.idcep"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    
                    imp.setId(rst.getString("id"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setInscricaoestadual(rst.getString("inscricaoestadual"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setAtivo(rst.getBoolean("ativo"));
                    imp.setBloqueado(rst.getBoolean("bloqueado"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setDataNascimento(rst.getDate("datanascimento"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setSexo(rst.getString("sexo"));
                    imp.setEmpresa(rst.getString("empresa"));
                    imp.setValorLimite(rst.getDouble("valorlimite"));
                    imp.setLimiteCompra(rst.getDouble("limitecompra"));
                    imp.setObservacao(rst.getString("observacao"));
                    imp.setEmail(rst.getString("email"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	c.idCategoria as Merc1ID,\n"
                    + "	c.stCategoria as Merc1Descricao,\n"
                    + "	s.idSubCategoria as Merc2ID,\n"
                    + "	s.stSubCategoria as Merc2Descricao\n"
                    + "from nati2.dbo.prd_Categorias c\n"
                    + "	left join nati2.dbo.prd_SubCategorias s\n"
                    + "		on c.idCategoria = s.idCategoria\n"
                    + "order by 1,3"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    
                    imp.setMerc1ID(rst.getString("Merc1ID"));
                    imp.setMerc1Descricao(rst.getString("Merc1Descricao"));
                    imp.setMerc2ID(rst.getString("Merc2ID"));
                    imp.setMerc2Descricao(rst.getString("Merc2Descricao"));
                    imp.setMerc3ID("1");
                    imp.setMerc3Descricao(rst.getString("Merc2Descricao"));
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
