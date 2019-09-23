/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import vrimplantacao.classe.ConexaoDBF;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author lucasrafael
 */
public class RootacDAO extends InterfaceDAO {

    private SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat fmt2 = new SimpleDateFormat("dd/MM/yy");
    private static final Logger LOG = Logger.getLogger(RootacDAO.class.getName());

    @Override
    public String getSistema() {
        return "Rootac";
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoDBF.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + " COALESCE(GRUC03SETO,0) cod_m1,\n"
                    + " COALESCE(GRUC03GRUP,0) cod_m2,\n"
                    + " COALESCE(GRUC03SUBG,0) cod_m3,\n"
                    + " COALESCE(GRUC03FAMI,0) cod_m4,\n"
                    + " COALESCE(GRUC03SUBF,0) cod_m5,\n"
                    + " GRUC35DESC desc_m\n"
                    + "FROM RC001GRU\n"
                    + "ORDER BY\n"
                    + " GRUC03SETO, GRUC03GRUP,\n"
                    + " GRUC03SUBG, GRUC03FAMI,\n"
                    + " GRUC03SUBF"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("cod_m1"));
                    imp.setMerc1Descricao(rst.getString("desc_m"));
                    imp.setMerc2ID(rst.getString("cod_m2"));
                    imp.setMerc2Descricao(imp.getMerc1Descricao());
                    imp.setMerc3ID(rst.getString("cod_m3"));
                    imp.setMerc3Descricao(imp.getMerc1Descricao());
                    imp.setMerc4ID(rst.getString("cod_m4"));
                    imp.setMerc4Descricao(imp.getMerc1Descricao());
                    imp.setMerc5ID(rst.getString("cod_m5"));
                    imp.setMerc5Descricao(imp.getMerc5Descricao());
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoDBF.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + " CODIGOPLU as id,\n"
                    + " ESTC03TIPO as unidade,\n"
                    + " ESTC35DESC as descricaocompleta,\n"
                    + " ESTC13CODI as ean,\n"
                    + " ESTC17RESU as descricaoresumida,\n"
                    + " PROCCODNCM as ncm,\n"
                    + " ESTC03SETO as merc1,\n"
                    + " ESTC03GRUP as merc2,\n"
                    + " ESTC03SUBG as merc3,\n"
                    + " ESTC03FAMI as merc4,\n"
                    + " ESTC03SUBF as merc5,\n"
                    + " ESTNO5MRGE as margem,\n"
                    + " VENDAATUA as precovenda,\n"
                    + " PRODVALIDA as validade,"
                    + " ESTN10MINI as estoqueminimo,\n"
                    + " ESTN10MAXI as estoquemaximo,\n"
                    + "FROM RC003EST\n"
                    + "WHERE ESTC200LOJ = " + getLojaOrigem()
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("ean"));
                    imp.setValidade(rst.getInt("validade"));
                    imp.setTipoEmbalagem(rst.getString("unidade"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoresumida"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setCodMercadologico1(rst.getString("merc1"));
                    imp.setCodMercadologico2(rst.getString("merc2"));
                    imp.setCodMercadologico3(rst.getString("merc3"));
                    imp.setCodMercadologico4(rst.getString("merc4"));
                    imp.setCodMercadologico5(rst.getString("merc5"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setEstoqueMaximo(rst.getDouble("estoquemaximo"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoDBF.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + " CODIGOPLU as id,\n"
                    + " ESTC13CODI as ean,\n"
                    + " EMBALVENDA as qtdembalagem\n"
                    + "FROM RC077EAN\n"
                    + "UNION ALL\n"
                    + "SELECT\n"
                    + " CODIGOPLU as id,\n"
                    + " DUNCCODBAR AS ean,\n"
                    + " '1' as qtdembalagem\n"
                    + "FROM RC003DUN"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("ean"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoDBF.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + " f.CODIFABRIC as id,\n"
                    + " f.FORC35RAZA as razao\n,"
                    + " f.FORC10APEL as fantasia,\n"
                    + " f.FORC15CGC as cnpj,\n"
                    + " f.FORC19INSC as ie_rg,\n"
                    + " f.FORC35ENDE as endereco,\n"
                    + " f.FORC20BAIR as bairro,\n"
                    + " f.FORC20CIDA as municipio,\n"
                    + " f.FORC20ESTA as uf,\n"
                    + " f.FORC25FONE as telefone,\n"
                    + " f.FORC10FAX as fax,\n"
                    + " f.FORCMAILTO as email,\n"
                    + " f.FORC400OBS1 as observacao\n"
                    + "FROM RC008FOR f\n"
                    + "ORDER BY f.CODIFABRIC"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("ie_rg"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setCep(rst.getString("cep"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setUf(rst.getString("uf"));
                    imp.setTel_principal(rst.getString("telefone"));
                    imp.setObservacao(rst.getString("observacao"));

                    if ((rst.getString("FORCMAILTO") != null)
                            && (!rst.getString("FORCMAILTO").trim().isEmpty())) {
                        imp.addContato(
                                "EMAIL",
                                null,
                                null,
                                TipoContato.NFE,
                                rst.getString("FORCMAILTO").toLowerCase()
                        );
                    }

                    try (ResultSet rst2 = stm.executeQuery(
                            "SELECT\n"
                            + " (C.DDD+C.TELEFONE) as telefone\n"
                            + "FROM RCTELTIP C\n"
                            + "WHERE C.CODIGO = " + imp.getImportId()
                    )) {
                        while (rst2.next()) {

                            imp.addContato(
                                    "TELEFONE",
                                    rst2.getString("telefone"),
                                    null,
                                    TipoContato.COMERCIAL,
                                    null
                            );
                        }
                    }
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoDBF.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + " CODIGOPLU as idproduto,\n"
                    + " CODIFABRIC as idfornecedor,\n"
                    + " ESTC08REFE as codigoexterno,\n"
                    + " ESTN04QEMB as qtdembalagem\n"
                    + "FROM RC113REF"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rst.getString("idproduto"));
                    imp.setIdFornecedor(rst.getString("idfornecedor"));
                    imp.setCodigoExterno(rst.getString("codigoexterno"));
                    imp.setQtdEmbalagem(rst.getDouble("qtdembalagem"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoDBF.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + " CLIC05CLIE as id,\n"
                    + " CLIC35NOME as razao,\n"
                    + " CLIC10APEL as fantasia,\n"
                    + " CLIC15CGC as cnpj,\n"
                    + " CLIC19RG as ie_rg,\n"
                    + " CLIC35ENDE as endereco,\n"
                    + " CLIC20BAIR as bairro,\n"
                    + " CLIC20CIDA as nunicipio,\n"
                    + " CLIC02ESTA as uf,\n"
                    + " CLIC08CEP as cep,\n"
                    + " CLIC18FONE as telefone,\n"
                    + " CLIC11FAX as fax,\n"
                    + " CLIC40OBS1 as observacao,\n"
                    + " EMPRESNOME as empresa,\n"
                    + " EMPRESENDE as empresaendereco,\n"
                    + " EMPRESESTA as empresauf,\n"
                    + " EMPRESCIDA as empresamunicipio,\n"
                    + " EMPRESBAIR as empresabairro,\n"
                    + " EMPRESCEP as empresacep,\n"
                    + " EMPRESFONE as empresatelefone,\n"
                    + " EMPRESFAX as empresafax,\n"
                    + " CLIN14SALA as salario,\n"
                    + " CLINLIMDES as limite,\n"
                    + " CLINLIMCON as limiteconvenio,\n"
                    + " ESTADOCOVI as estadocovil\n"
                    + "FROM RC042CLI\n"
                    + "ORDER BY CLIC05CLIE"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setInscricaoestadual(rst.getString("ie_rg"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setCep(rst.getString("cep"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setUf(rst.getString("uf"));
                    imp.setTelefone(rst.getString("telefone"));
                    imp.setFax(rst.getString("fax"));
                    imp.setObservacao(rst.getString("observacao"));
                    imp.setEmpresa(rst.getString("empresa"));
                    imp.setEmpresaEndereco(rst.getString("empresaendereco"));
                    imp.setEmpresaBairro(rst.getString("empresabairro"));
                    imp.setEmpresaCep(rst.getString("empresacep"));
                    imp.setEmpresaMunicipio(rst.getString("empresamunicipio"));
                    imp.setEmpresaUf(rst.getString("empresauf"));
                    imp.setEmpresaTelefone(rst.getString("empresatelefone"));
                    imp.setSalario(rst.getDouble("salario"));
                    imp.setValorLimite(rst.getDouble("limite"));
                    imp.setPermiteCheque(true);
                    imp.setPermiteCreditoRotativo(true);
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
