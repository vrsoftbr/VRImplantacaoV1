package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import vrimplantacao.classe.ConexaoDBF;
import vrimplantacao.vo.vrimplantacao.SqlVO;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Importacao
 */
public class DJSystemDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "DJ System";
    }
    
    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                new OpcaoProduto[]{
                    OpcaoProduto.MERCADOLOGICO_PRODUTO,
                    OpcaoProduto.MERCADOLOGICO,
                    OpcaoProduto.FAMILIA_PRODUTO,
                    OpcaoProduto.FAMILIA,
                    OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                    OpcaoProduto.PRODUTOS,
                    OpcaoProduto.EAN,
                    OpcaoProduto.EAN_EM_BRANCO,
                    OpcaoProduto.DATA_CADASTRO,
                    OpcaoProduto.TIPO_EMBALAGEM_EAN,
                    OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                    OpcaoProduto.PESAVEL,
                    OpcaoProduto.VALIDADE,
                    OpcaoProduto.DESC_COMPLETA,
                    OpcaoProduto.DESC_GONDOLA,
                    OpcaoProduto.DESC_REDUZIDA,
                    OpcaoProduto.ESTOQUE_MAXIMO,
                    OpcaoProduto.ESTOQUE_MINIMO,
                    OpcaoProduto.PRECO,
                    OpcaoProduto.CUSTO,
                    OpcaoProduto.CUSTO_COM_IMPOSTO,
                    OpcaoProduto.CUSTO_SEM_IMPOSTO,
                    OpcaoProduto.ESTOQUE,
                    OpcaoProduto.ATIVO,
                    OpcaoProduto.NCM,
                    OpcaoProduto.CEST,
                    OpcaoProduto.PIS_COFINS,
                    OpcaoProduto.NATUREZA_RECEITA,
                    OpcaoProduto.ICMS,
                    OpcaoProduto.PAUTA_FISCAL,
                    OpcaoProduto.PAUTA_FISCAL_PRODUTO,
                    OpcaoProduto.MARGEM,
                    OpcaoProduto.OFERTA
                }
        ));
    }
    
    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();

        result.add(new Estabelecimento("1", "LOJA 01"));
           
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
    
    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoDBF.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	codcfi as id,\n" +
                    "	descricao,\n" +
                    "	icms,\n" +
                    "	cod_contab as cst,\n" +
                    "	fator_base as reducao\n" +
                    "from \n" +
                    "	cfiscal")) {
                while(rs.next()) {
                    result.add(new MapaTributoIMP(
                            rs.getString("id"), 
                            rs.getString("descricao"), 
                            rs.getInt("cst"), 
                            rs.getDouble("icms"), 
                            rs.getDouble("reducao")));
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoDBF.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "SELECT \n" +
                    "  P.CODITE AS ID,\n" +
                    "  P.DESCRICAO AS DESCRICAOCOMPLETA,\n" +
                    "  P.UN AS EMBALAGEM,\n" +
                    "  P.CODCFI AS IDALIQUOTA,\n" +
                    "  F.CSTPIS,\n" +
                    "  P.PESO,\n" +
                    "  P.GTIN_PROD AS EAN,\n" +
                    "  p.REFERENCIA,\n" +
                    "  P.P_CUSTO CUSTOCOMIMPOSTO,\n" +
                    "  P.P_VENDA AS PRECOVENDA,\n" +
                    "  P.LUCRO AS MARGEM,\n" +
                    "  P.QTDEST1 AS ESTOQUE,\n" +
                    "  P.QTDMEDIA AS MEDIA,\n" +
                    "  P.QTDMINIMA AS ESTOQUEMINIMO,\n" +
                    "  P.QTDMAXIMA AS ESTOQUEMAXIMO,\n" +
                    "  P.NBM AS NCM,\n" +
                    "  P.CEST,\n" +
                    "  P.ALTPRECO AS DATACADASTRO\n" +
                    "FROM\n" +
                    " ITENS P \n" +
                    "LEFT JOIN CFISCAL F ON P.CODCFI = F.CODCFI\n" +
                    "ORDER BY \n" +
                    " P.CODITE")) {
                while(rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id") == null ? rs.getString("ean") : rs.getString("id"));
                    imp.setDescricaoCompleta(rs.getString("descricaocompleta"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setEan(rs.getString("ean") == null ? rs.getString("referencia") : rs.getString("ean"));
                    imp.setTipoEmbalagem(rs.getString("embalagem"));
                    
                    if(rs.getString("embalagem") != null && 
                            !"".equals(rs.getString("embalagem")) &&
                                "PS".equals(rs.getString("embalagem").trim())) {
                        imp.seteBalanca(true);
                        
                        String eanBal = rs.getString("referencia");
                        
                        if(eanBal != null && !"".equals(eanBal)) {
                            imp.setEan(eanBal.substring(1, eanBal.length()));
                            imp.setTipoEmbalagem("KG");
                        } else {
                            imp.setEan("-2");
                        }
                    }
                    
                    imp.setDataCadastro(rs.getDate("datacadastro"));
                    imp.setCustoComImposto(rs.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setPrecovenda(rs.getDouble("precovenda"));
                    imp.setMargem(rs.getDouble("margem"));
                    imp.setEstoque(rs.getDouble("estoque"));
                    imp.setEstoqueMaximo(rs.getDouble("estoquemaximo"));
                    imp.setEstoqueMinimo(rs.getDouble("estoqueminimo"));
                    imp.setNcm(rs.getString("ncm"));
                    imp.setCest(rs.getString("cest"));
                    imp.setPiscofinsCstDebito(rs.getString("cstpis"));
                    imp.setIcmsDebitoId(rs.getString("idaliquota"));
                    imp.setIcmsDebitoForaEstadoId(imp.getIcmsDebitoId());
                    imp.setIcmsDebitoForaEstadoNfId(imp.getIcmsDebitoId());
                    imp.setIcmsConsumidorId(imp.getIcmsDebitoId());
                    imp.setIcmsCreditoId(imp.getIcmsDebitoId());
                    imp.setIcmsCreditoForaEstadoId(imp.getIcmsDebitoId());
                    
                    if(imp.getEan() != null && !"".equals(imp.getEan()) && imp.getEan().length() < 7) {
                        imp.setManterEAN(true);
                    }
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoDBF.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	codfor as id,\n" +
                    "	nome as razao,\n" +
                    "	fantasia,\n" +
                    "	data_cadas,\n" +
                    "	endereco,\n" +
                    "	bairro,\n" +
                    "	cidade,\n" +
                    "	complement,\n" +
                    "	end_numero as numero,\n" +
                    "	uf,\n" +
                    "	cep,\n" +
                    "	tel,\n" +
                    "	insc_est as ie,\n" +
                    "	cgc,\n" +
                    "	contato,\n" +
                    "	tel_fax,\n" +
                    "	e_mail,\n" +
                    "	obs\n" +
                    "from\n" +
                    "	fornece")) {
                while(rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setDatacadastro(rs.getDate("data_cadas"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setComplemento(rs.getString("complement"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));
                    imp.setTel_principal(rs.getString("tel"));
                    imp.setIe_rg(rs.getString("ie"));
                    imp.setCnpj_cpf(rs.getString("cgc"));
                    
                    String email = rs.getString("e_mail");
                    
                    if(email != null && !"".equals(email)) {
                        imp.addContato("1", "E-MAIL", null, null, TipoContato.NFE, email);
                    }
                    
                    String contato = rs.getString("contato");
                    
                    if(contato != null && !"".equals(contato)) {
                        imp.addContato("2", contato, null, null, TipoContato.NFE, null);
                    }
                    
                    imp.setObservacao(rs.getString("obs"));
                    
                    result.add(imp);
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
                    "select\n" +
                    "	codcli as id,\n" +
                    "	nome,\n" +
                    "	fantasia,\n" +
                    "	data_cadas as cadastro,\n" +
                    "	nasc_funda as dtnascimento,\n" +
                    "	endere_res as endereco,\n" +
                    "	num_res as numero,\n" +
                    "	compl_res as complemento,\n" +
                    "	bairro_res as bairro,\n" +
                    "	cidade_res as cidade,\n" +
                    "	uf_res as uf,\n" +
                    "	cep_res as cep,\n" +
                    "	tel_res as telefone,\n" +
                    "	rg_inscest as ie,\n" +
                    "	cic_cgc as cpf,\n" +
                    "	pai_contat as pai,\n" +
                    "	mae_tel,\n" +
                    "	val_limite as limite,\n" +
                    "	e_mail as email,\n" +
                    "	obs\n" +
                    "from\n" +
                    "	cliente")) {
                while(rs.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    
                    imp.setId(rs.getString("id"));
                    imp.setRazao(rs.getString("nome"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setDataCadastro(rs.getDate("cadastro"));
                    imp.setDataNascimento(rs.getDate("dtnascimento"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));
                    imp.setTelefone(rs.getString("telefone"));
                    imp.setInscricaoestadual(rs.getString("ie"));
                    imp.setCnpj(rs.getString("cpf"));
                    imp.setNomePai(rs.getString("pai"));
                    imp.setNomeMae(rs.getString("mae_tel"));
                    imp.setValorLimite(rs.getDouble("limite"));
                    imp.setEmail(rs.getString("email"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoDBF.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	f.numfat as id,\n" +
                    "	f.codcli as idcliente,\n" +
                    "	f.data_emis as emissao,\n" +
                    "	d.data_venc as vencimento,\n" +
                    "   d.parcela,\n" +        
                    "	f.total,\n" +
                    "	d.movimento,\n" +
                    "	d.codcai as ecf,\n" +
                    "	d.audit as obs\n" +
                    "from \n" +
                    "	faturas f \n" +
                    "join dpsaida d on f.numfat = d.numfat\n" +
                    "where\n" +
                    "	d.data_pago is null\n" +        
                    "order by\n" +
                    "	d.data_venc")) {
                while(rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    
                    imp.setId(rs.getString("id"));
                    imp.setIdCliente(rs.getString("idcliente"));
                    imp.setParcela(rs.getInt("parcela"));
                    imp.setDataEmissao(rs.getDate("emissao"));
                    imp.setDataVencimento(rs.getDate("vencimento"));
                    imp.setValor(rs.getDouble("total"));
                    imp.setEcf(rs.getString("ecf"));
                    imp.setObservacao(rs.getString("obs"));
                    imp.setNumeroCupom(rs.getString("numfat"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

}
