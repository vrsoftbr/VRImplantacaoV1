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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import vrimplantacao.classe.ConexaoAccess;
import vrimplantacao.classe.ConexaoDBF;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao.vo.vrimplantacao.SqlVO;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.vo.cadastro.mercadologico.MercadologicoNivelIMP;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoEstadoCivil;
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

    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoDBF.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "LOJC05CODI as codigo,\n"
                    + "LOJC10APEL as nome\n"
                    + "from\n"
                    + "RC002LOJ")) {
                while (rs.next()) {
                    result.add(new Estabelecimento(rs.getString("codigo"), rs.getString("nome")));
                }
            }
        }
        return result;
    }
    
    @Override
    public List<MercadologicoNivelIMP> getMercadologicoPorNivel() throws Exception {
        Map<String, MercadologicoNivelIMP> merc = new LinkedHashMap<>();
        try (Statement stm = ConexaoDBF.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(                    
                    "SELECT \n"
                    + " COALESCE(GRUC03SETO,0) COD_M1,\n"
                    + " GRUC35DESC DESC_M\n"
                    + " FROM RC001GRU\n"
                    + "WHERE COALESCE(GRUC03GRUP,0) = 0\n"
                    + "  AND COALESCE(GRUC03SUBG,0) = 0\n"
                    + "  AND COALESCE(GRUC03FAMI,0) = 0\n"
                    + "  AND COALESCE(GRUC03SUBF,0) = 0\n"
                    + "ORDER BY\n"
                    + " GRUC03SETO"                    
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP imp = new MercadologicoNivelIMP();
                    imp.setId(rst.getString("COD_M1"));
                    imp.setDescricao(rst.getString("DESC_M"));
                    merc.put(imp.getId(), imp);
                }
            }

            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + " COALESCE(GRUC03SETO,0) COD_M1,\n"
                    + " COALESCE(GRUC03GRUP,0) COD_M2,\n"
                    + " GRUC35DESC DESC_M\n"
                    + " FROM RC001GRU\n"
                    + "WHERE COALESCE(GRUC03GRUP,0) > 0\n"
                    + "  AND COALESCE(GRUC03SUBG,0) = 0\n"
                    + "  AND COALESCE(GRUC03FAMI,0) = 0\n"
                    + "  AND COALESCE(GRUC03SUBF,0) = 0\n"
                    + "ORDER BY\n"
                    + " GRUC03SETO, GRUC03GRUP"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP merc1 = merc.get(rst.getString("COD_M1"));
                    if (merc1 != null) {
                        merc1.addFilho(
                                rst.getString("COD_M2"),
                                rst.getString("DESC_M")
                        );
                    }
                }
            }

            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + " COALESCE(GRUC03SETO,0) COD_M1,\n"
                    + " COALESCE(GRUC03GRUP,0) COD_M2,\n"
                    + " COALESCE(GRUC03SUBG,0) COD_M3,\n"
                    + " GRUC35DESC DESC_M\n"
                    + "FROM RC001GRU\n"
                    + "WHERE COALESCE(GRUC03GRUP,0) > 0\n"
                    + "  AND COALESCE(GRUC03SUBG,0) > 0\n"
                    + "  AND COALESCE(GRUC03FAMI,0) = 0\n"
                    + "  AND COALESCE(GRUC03SUBF,0) = 0\n"
                    + "ORDER BY\n"
                    + " GRUC03SETO, GRUC03GRUP,\n"
                    + " GRUC03SUBG"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP merc1 = merc.get(rst.getString("COD_M1"));
                    if (merc1 != null) {
                        MercadologicoNivelIMP merc2 = merc1.getNiveis().get(rst.getString("COD_M2"));
                        if (merc2 != null) {
                            merc2.addFilho(
                                    rst.getString("COD_M3"),
                                    rst.getString("DESC_M")
                            );
                        }
                    }
                }
            }
            
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + " COALESCE(GRUC03SETO,0) COD_M1,\n"
                    + " COALESCE(GRUC03GRUP,0) COD_M2,\n"
                    + " COALESCE(GRUC03SUBG,0) COD_M3,\n"
                    + " COALESCE(GRUC03FAMI,0) COD_M4,\n"
                    + " GRUC35DESC DESC_M\n"
                    + "FROM RC001GRU\n"
                    + "WHERE COALESCE(GRUC03GRUP,0) > 0\n"
                    + "  AND COALESCE(GRUC03SUBG,0) > 0\n"
                    + "  AND COALESCE(GRUC03FAMI,0) > 0\n"
                    + "  AND COALESCE(GRUC03SUBF,0) = 0\n"
                    + "ORDER BY\n"
                    + " GRUC03SETO, GRUC03GRUP,\n"
                    + " GRUC03SUBG, GRUC03FAMI"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP merc1 = merc.get(rst.getString("COD_M1"));
                    if (merc1 != null) {
                        MercadologicoNivelIMP merc2 = merc1.getNiveis().get(rst.getString("COD_M2"));
                        if (merc2 != null) {
                            MercadologicoNivelIMP merc3 = merc2.getNiveis().get(rst.getString("COD_M3"));
                            if (merc3 != null) {
                                merc3.addFilho(
                                        rst.getString("COD_M4"),
                                        rst.getString("DESC_M")
                                );
                            }
                        }
                    }
                }
            }

            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + " COALESCE(GRUC03SETO,0) COD_M1,\n"
                    + " COALESCE(GRUC03GRUP,0) COD_M2,\n"
                    + " COALESCE(GRUC03SUBG,0) COD_M3,\n"
                    + " COALESCE(GRUC03FAMI,0) COD_M4,\n"
                    + " COALESCE(GRUC03SUBF,0) COD_M5,\n"
                    + " GRUC35DESC DESC_M\n"
                    + "FROM RC001GRU\n"
                    + "WHERE COALESCE(GRUC03GRUP,0) > 0\n"
                    + "  AND COALESCE(GRUC03SUBG,0) > 0\n"
                    + "  AND COALESCE(GRUC03FAMI,0) > 0\n"
                    + "  AND COALESCE(GRUC03SUBF,0) > 0\n"
                    + "ORDER BY\n"
                    + " GRUC03SETO, GRUC03GRUP,\n"
                    + " GRUC03SUBG, GRUC03FAMI,\n"
                    + " GRUC03SUBF"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP merc1 = merc.get(rst.getString("COD_M1"));
                    if (merc1 != null) {
                        MercadologicoNivelIMP merc2 = merc1.getNiveis().get(rst.getString("COD_M2"));
                        if (merc2 != null) {
                            MercadologicoNivelIMP merc3 = merc2.getNiveis().get(rst.getString("COD_M3"));
                            if (merc3 != null) {
                                MercadologicoNivelIMP merc4 = merc3.getNiveis().get(rst.getString("COD_M4"));
                                if (merc4 != null) {
                                    merc4.addFilho(
                                            rst.getString("COD_M5"),
                                            rst.getString("DESC_M")
                                    );
                                }
                            }
                        }
                    }
                }
            }            
        }
        return new ArrayList<>(merc.values());
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoDBF.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + " CODIGOPLU as id,\n"
                    + " ESTC01PESO as balanca,\n"
                    + " ESTC03TIPO as unidade,\n"
                    + " ESTN03QTDE as qtdembalagem,\n"
                    + " ESTC35DESC as descricaocompleta,\n"
                    + " ESTC13CODI as ean,\n"
                    + " ESTC17RESU as descricaoresumida,\n"
                    + " PROCCODNCM as ncm,\n"
                    + " ESTC03SETO as merc1,\n"
                    + " ESTC03GRUP as merc2,\n"
                    + " ESTC03SUBG as merc3,\n"
                    + " ESTC03FAMI as merc4,\n"
                    + " ESTC03SUBF as merc5,\n"
                    + " ESTN05MRGE as margem,\n"
                    + " VENDAATUA as precovenda,\n"
                    + " PRODVALIDA as validade,"
                    + " ESTN10MINI as estoqueminimo,\n"
                    + " ESTN10MAXI as estoquemaximo\n"
                    + "FROM RC003EST "
                    + "ORDER BY CODIGOPLU"
            )) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
                while (rst.next()) {

                    if ((rst.getString("id") != null)
                            && (!rst.getString("id").trim().isEmpty())) {

                        ProdutoIMP imp = new ProdutoIMP();
                        ProdutoBalancaVO produtoBalanca;

                        String codigoBalanca = rst.getString("id").substring(0, rst.getString("id").trim().length() - 1);

                        long codigoProduto;
                        codigoProduto = Long.parseLong(codigoBalanca);
                        if (codigoProduto <= Integer.MAX_VALUE) {
                            produtoBalanca = produtosBalanca.get((int) codigoProduto);
                        } else {
                            produtoBalanca = null;
                        }

                        if (produtoBalanca != null) {
                            imp.seteBalanca(true);
                            imp.setValidade(produtoBalanca.getValidade() > 1 ? produtoBalanca.getValidade() : rst.getInt("validade"));
                        } else {
                            rst.getInt("validade");
                            imp.seteBalanca(false);
                        }

                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("id"));
                        //imp.seteBalanca("S".equals(rst.getString("balanca")));

                        if (imp.isBalanca()) {
                            imp.setEan(codigoBalanca);
                        } else {
                            imp.setEan(rst.getString("ean"));
                        }
                        //imp.setValidade(rst.getInt("validade"));
                        imp.setTipoEmbalagem(rst.getString("unidade"));
                        imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
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
                    + " CODIFABRIC AS ID, "
                    + " FORC35RAZA AS RAZAO, "
                    + " FORC10APEL AS FANTASIA, "
                    + " FORC15CGC AS CNPJ, "
                    + " FORC19INSC AS IE_RG, "
                    + " FORC35ENDE AS ENDERECO, "
                    + " FORC20BAIR AS BAIRRO, "
                    + " FORC20CIDA AS MUNICIPIO, "
                    + " FORC08CEP AS CEP, "
                    + " FORC02ESTA AS UF, "
                    + " FORC25FONE AS TELEFONE, "
                    + " FORC11FAX AS FAX, "
                    + " FORCMAILTO AS EMAIL, "
                    + " FORC40OBS1 AS OBSERVACAO "
                    + "FROM RC008FOR "
                    + "ORDER BY CODIFABRIC"
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
                    + " CLIC05CLIE AS ID,\n"
                    + " CLIC35NOME AS RAZAO,\n"
                    + " CLIC10APEL AS FANTASIA,\n"
                    + " CLIC15CGC AS CNPJ,\n"
                    + " CLIC19RG AS IE_RG,\n"
                    + " CLIC35ENDE AS ENDERECO,\n"
                    + " CLIC20BAIR AS BAIRRO,\n"
                    + " CLIC20CIDA AS MUNICIPIO,\n"
                    + " CLIC02ESTA AS UF,\n"
                    + " CLIC08CEP AS CEP,\n"
                    + " CLIC18FONE AS TELEFONE,\n"
                    + " CLIC11FAX AS FAX,\n"
                    + " CLIC40OBS1 AS OBSERVACAO,\n"
                    + " EMPRESNOME AS EMPRESA,\n"
                    + " EMPRESENDE AS EMPRESAENDERECO,\n"
                    + " EMPRESESTA AS EMPRESAUF,\n"
                    + " EMPRESCIDA AS EMPRESAMUNICIPIO,\n"
                    + " EMPRESBAIR AS EMPRESABAIRRO,\n"
                    + " EMPRESACEP AS EMPRESACEP,\n"
                    + " EMPRESFONE AS EMPRESATELEFONE,\n"
                    + " EMPRESAFAX AS EMPRESAFAX,\n"
                    + " CLIN14SALA AS SALARIO,\n"
                    + " CLINLIMDES AS LIMITE,\n"
                    + " CLINLIMCON AS LIMITECONVENIO,\n"
                    + " ESTADOCIVI AS ESTADOCIVIL\n"
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
                    imp.setValorLimite(rst.getDouble("LIMITECONVENIO"));
                                        
                    if ((rst.getString("ESTADOCIVIL") != null)
                            && (!rst.getString("ESTADOCIVIL").trim().isEmpty())) {
                        
                        if ("S".equals(rst.getString("ESTADOCIVIL"))) {
                            imp.setEstadoCivil(TipoEstadoCivil.SOLTEIRO);
                        } else if ("C".equals(rst.getString("ESTADOCIVIL"))) {
                            imp.setEstadoCivil(TipoEstadoCivil.CASADO);
                        } else if ("V".equals(rst.getString("ESTADOCIVIL"))) {
                            imp.setEstadoCivil(TipoEstadoCivil.VIUVO);
                        } else if ("D".equals(rst.getString("ESTADOCIVIL"))) {
                            imp.setEstadoCivil(TipoEstadoCivil.DIVORCIADO);
                        } else {
                            imp.setEstadoCivil(TipoEstadoCivil.NAO_INFORMADO);
                        }
                    } else {
                        imp.setEstadoCivil(TipoEstadoCivil.NAO_INFORMADO);
                    }
                    
                    imp.setPermiteCheque(true);
                    imp.setPermiteCreditoRotativo(true);
                    result.add(imp);
                }
            }
        }
        return result;
    }
    
    public SqlVO consultar(String i_sql) throws Exception {
        Statement stm = null;
        ResultSet rst = null;

        stm = ConexaoDBF.getConexao().createStatement();
        rst = stm.executeQuery(i_sql);

        SqlVO oSql = new SqlVO();

        for (int i = 1; i <= rst.getMetaData().getColumnCount(); i++) {
            oSql.vHeader.add(rst.getMetaData().getColumnName(i));
        }

        while (rst.next()) {
            List<String> vColuna = new ArrayList();

            for (int i = 1; i <= rst.getMetaData().getColumnCount(); i++) {
                vColuna.add(rst.getString(i));
            }

            oSql.vConsulta.add(vColuna);
        }

        stm.close();

        return oSql;
    }

    public String executar(String i_sql) throws Exception {
        Statement stm = null;

        try {
            stm = ConexaoAccess.getConexao().createStatement();
            int result = stm.executeUpdate(i_sql);
            stm.close();
            return "Executado com sucesso: " + result + " registros afetados.";
        } catch (Exception ex) {
            throw ex;
        }
    }
    
}
