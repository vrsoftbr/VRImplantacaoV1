package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import vrimplantacao.classe.ConexaoAccess;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.OpcaoFiscal;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.OfertaIMP;
import vrimplantacao2.vo.importacao.PautaFiscalIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Importacao
 */
public class LBSoftwareDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "LB Software";
    }
    
    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.FAMILIA,
                OpcaoProduto.FAMILIA_PRODUTO,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.QTD_EMBALAGEM_EAN,
                OpcaoProduto.ATIVO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.ESTOQUE_MAXIMO,
                OpcaoProduto.ESTOQUE_MINIMO,
                OpcaoProduto.PRECO,
                OpcaoProduto.CUSTO,
                OpcaoProduto.MARGEM,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.PESO_BRUTO,
                OpcaoProduto.PESO_LIQUIDO,
                OpcaoProduto.NCM,
                OpcaoProduto.CEST,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.NATUREZA_RECEITA,
                OpcaoProduto.PAUTA_FISCAL,
                OpcaoProduto.ICMS,
                OpcaoProduto.PAUTA_FISCAL_PRODUTO,
                OpcaoProduto.OFERTA
        ));
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoAccess.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "  codigo,\n" +
                    "  descricao,\n" +
                    "  st as cst,\n" +
                    "  aicm as icms,\n" +
                    "  bcr as reducao\n" +
                    "from \n" +
                    "  tributacao")) {
                while(rs.next()) {
                    result.add(new MapaTributoIMP(
                            rs.getString("codigo"), 
                            rs.getString("descricao"), 
                            rs.getInt("cst"), 
                            rs.getDouble("icms"), 
                            rs.getDouble("reducao")));
                }
            }
        }
        
        return result;
    }
  
    public List<Estabelecimento> getLojaCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        
        try (Statement stm = ConexaoAccess.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n" +
                    "   codigo,\n" +
                    "   nome\n" +
                    "from\n" +
                    "   empresa"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("codigo"), rst.getString("nome")));
                }
            }
        }
        
        return result;
    }
    
    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoAccess.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "   nome as codmerc1,\n" +
                    "   nome as descmerc1\n" +
                    "from\n" +
                    "   secao"
            )) {
                while (rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());                   
                    imp.setMerc1ID(rs.getString("codmerc1"));
                    imp.setMerc1Descricao(rs.getString("descmerc1"));
                    imp.setMerc2ID(imp.getMerc1ID());
                    imp.setMerc2Descricao(imp.getMerc1Descricao());
                    imp.setMerc3ID(imp.getMerc1ID());
                    imp.setMerc3Descricao(imp.getMerc1Descricao());
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoAccess.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "   p.codigo as id,\n" +
                    "   p.nome as descricaocompleta,\n" +
                    "   p.etqdescricao as descricaogondola,\n" +
                    "   p.nome_reduz as descricaoreduzida,\n" +
                    "   p.balanca,\n" +
                    "   p.balancavalidade as validade,\n" +        
                    "   p.ean,\n" +
                    "   p.unidade,\n" +
                    "   p.secao as merc1,\n" +
                    "   p.datacad,\n" +
                    "   p.embalagem as qtdembalagem,\n" +
                    "   p.custoat as custoanterior,\n" +
                    "   p.preco,\n" +
                    "   p.lucro0,\n" +
                    "   p.margem,\n" +
                    "   p.ml as margemliq,\n" +
                    "   p.e as estoque,\n" +
                    "   p.icms,\n" +
                    "   p.icme,\n" +
                    "   p.codpiscofins as piscofins,\n" +
                    "   p.piscofinsnatrec as naturezareceita,\n" +
                    "   p.ncm,\n" +
                    "   p.cest,\n" +
                    "   p.bloqueadoparavenda as bloqueado\n" +        
                    "from\n" +
                    "   produtos p\n" +
                    "left join piscofins pc on p.codpiscofins = pc.codigo\n" +
                    "order by\n" +
                    "   p.codigo")) {
                while(rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(String.valueOf(rs.getInt("id")));
                    imp.setEan(String.valueOf(rs.getLong("ean")));
                    imp.seteBalanca(rs.getInt("balanca") > 0);
                    imp.setDescricaoCompleta(Utils.acertarTexto(rs.getString("descricaocompleta")));
                    imp.setDescricaoReduzida(Utils.acertarTexto(rs.getString("descricaoreduzida")));
                    imp.setDescricaoGondola(Utils.acertarTexto(rs.getString("descricaogondola")));
                    imp.setCodMercadologico1(rs.getString("merc1"));
                    imp.setCodMercadologico2(imp.getCodMercadologico1());
                    imp.setCodMercadologico3(imp.getCodMercadologico1());
                    imp.setTipoEmbalagem(rs.getString("unidade"));
                    imp.setCustoComImposto(rs.getDouble("custoanterior"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setPrecovenda(rs.getDouble("preco"));
                    imp.setEstoque(rs.getDouble("estoque"));
                    
                    String icms = rs.getString("icms");
                    
                    if(icms != null && !"".equals(icms)) {
                        switch(icms.trim()) {
                            case "FF":
                                imp.setIcmsAliqSaida(0);
                                imp.setIcmsCstSaida(60);
                                imp.setIcmsReducaoSaida(0);
                                break;
                            case "II":
                                imp.setIcmsAliqSaida(0);
                                imp.setIcmsCstSaida(40);
                                imp.setIcmsReducaoSaida(0);
                                break;
                            case "T01":
                                imp.setIcmsAliqSaida(7);
                                imp.setIcmsCstSaida(0);
                                imp.setIcmsReducaoSaida(0);
                                break;
                            case "T02":
                                imp.setIcmsAliqSaida(12);
                                imp.setIcmsCstSaida(0);
                                imp.setIcmsReducaoSaida(0);
                                break;
                            case "T03":
                                imp.setIcmsAliqSaida(18);
                                imp.setIcmsCstSaida(0);
                                imp.setIcmsReducaoSaida(0);
                                break;
                            case "T04":
                                imp.setIcmsAliqSaida(25);
                                imp.setIcmsCstSaida(0);
                                imp.setIcmsReducaoSaida(0);
                                break;
                            default:    
                                imp.setIcmsAliqSaida(0);
                                imp.setIcmsCstSaida(40);
                                imp.setIcmsReducaoSaida(0);
                                break;
                        }
                    }
                    
                    imp.setIcmsAliqConsumidor(imp.getIcmsAliqSaida());
                    imp.setIcmsCstConsumidor(imp.getIcmsCstSaida());
                    imp.setIcmsReducaoConsumidor(imp.getIcmsReducaoSaida());
                    
                    imp.setIcmsAliqSaidaForaEstado(imp.getIcmsAliqSaida());
                    imp.setIcmsCstSaidaForaEstado(imp.getIcmsCstSaida());
                    imp.setIcmsReducaoSaidaForaEstado(imp.getIcmsReducaoSaida());
                    
                    imp.setIcmsAliqSaidaForaEstadoNF(imp.getIcmsAliqSaida());
                    imp.setIcmsCstSaidaForaEstadoNF(imp.getIcmsCstSaida());
                    imp.setIcmsReducaoSaidaForaEstadoNF(imp.getIcmsReducaoSaida());
                    
                    imp.setIcmsAliqEntrada(imp.getIcmsAliqSaida());
                    imp.setIcmsCstEntrada(imp.getIcmsCstEntrada());
                    imp.setIcmsReducaoEntrada(imp.getIcmsReducaoEntrada());
                    
                    imp.setIcmsAliqEntradaForaEstado(imp.getIcmsAliqEntrada());
                    imp.setIcmsCstEntradaForaEstado(imp.getIcmsCstEntradaForaEstado());
                    imp.setIcmsReducaoEntradaForaEstado(imp.getIcmsReducaoEntradaForaEstado());
                    
                    imp.setSituacaoCadastro(rs.getBoolean("bloqueado") == true ? 0 : 1);
                    imp.setDataCadastro(rs.getDate("datacad"));
                    imp.setValidade(rs.getInt("validade"));
                    imp.setNcm(rs.getString("ncm"));
                    imp.setMargem(rs.getDouble("margem"));
                    imp.setPiscofinsCstDebito(rs.getString("piscofins"));
                    imp.setPiscofinsNaturezaReceita(rs.getString("naturezareceita"));
                    imp.setCest(rs.getString("cest"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoAccess.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "   codigo as id,\n" +
                    "   nome,\n" +
                    "   fantasia,\n" +
                    "   cgc,\n" +
                    "   ie,\n" +
                    "   bairro,\n" +
                    "   endereco,\n" +
                    "   numero, \n" +
                    "   cidade,\n" +
                    "   estado,\n" +
                    "   cep,\n" +
                    "   fone1,\n" +
                    "   fone2,\n" +
                    "   fone3,\n" +
                    "   celular,\n" +
                    "   fax,\n" +
                    "   contato,\n" +
                    "   obs,\n" +
                    "   email\n" +
                    "from\n" +
                    "   fornecedores")) {
                while(rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("id"));
                    imp.setRazao(Utils.acertarTexto(rs.getString("nome")));
                    imp.setFantasia(Utils.acertarTexto(rs.getString("fantasia")));
                    imp.setTel_principal(rs.getString("fone1"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("estado"));
                    imp.setCep(rs.getString("cep"));
                    imp.setCnpj_cpf(rs.getString("cgc"));
                    imp.setIe_rg(rs.getString("ie"));
                    
                    String fax = rs.getString("fax"), 
                            celular = rs.getString("celular"),
                            email = rs.getString("email"),
                            contato = rs.getString("contato"),
                            fone3 = rs.getString("fon3");
                    
                    if(fax != null && !"".equals(fax)) {
                        imp.addContato("1", "FAX", fax, null, TipoContato.NFE, null);
                    }
                    
                    if(celular != null && !"".equals(celular)) {
                        imp.addContato("2", "CELULAR", null, celular, TipoContato.NFE, null);
                    }
                    
                    if(email != null && !"".equals(email)) {
                        imp.addContato("3", rs.getString("contato"), null, null, TipoContato.NFE, email);
                    }
                    
                    if(contato != null && !"".equals(contato)) {
                        imp.addContato("4", contato, null, null, TipoContato.NFE, null);
                    }
                    
                    if(fone3 != null && !"".equals(fone3)) {
                        imp.addContato("5", "FONE3", fone3, null, TipoContato.NFE, null);
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
        
        try(Statement stm = ConexaoAccess.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "  codigo as idproduto,\n" +
                    "  codfor as idfornecedor,\n" +
                    "  codigoexterno,\n" +
                    "  embalagem \n" +
                    "from \n" +
                    "  CodigosExternos")) {
                while(rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rs.getString("idproduto"));
                    imp.setIdFornecedor(rs.getString("idfornecedor"));
                    imp.setCodigoExterno(rs.getString("codigoexterno"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoAccess.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "  codigo,\n" +
                    "  nome,\n" +
                    "  rg,\n" +
                    "  cpf,\n" +
                    "  datanasc,\n" +
                    "  endereco,\n" +
                    "  numero,\n" +
                    "  cidade,\n" +
                    "  referencia,\n" +
                    "  bairro,\n" +
                    "  cep,\n" +
                    "  uf,\n" +
                    "  telefone,\n" +
                    "  limite,\n" +
                    "  motivo,\n" +
                    "  salario,\n" +
                    "  empresa,\n" +
                    "  cargo,\n" +
                    "  foner,\n" +
                    "  email,\n" +
                    "  pai,\n" +
                    "  mae,\n" +
                    "  conjuge,\n" +
                    "  datareg,\n" +
                    "  inativo\n" +
                    "from\n" +
                    "  clientes")) {
                while(rs.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    
                    imp.setId(rs.getString("codigo"));
                    imp.setRazao(rs.getString("nome"));
                    imp.setInscricaoestadual(rs.getString("rg"));
                    imp.setCnpj(rs.getString("cpf"));
                    imp.setDataNascimento(rs.getDate("datanasc"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setComplemento(rs.getString("referencia"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setCep(rs.getString("cep"));
                    imp.setUf(rs.getString("uf"));
                    imp.setTelefone(rs.getString("telefone"));
                    imp.setValorLimite(rs.getDouble("limite"));
                    imp.setObservacao(rs.getString("motivo"));
                    imp.setSalario(rs.getDouble("salario"));
                    imp.setEmpresa(rs.getString("empresa"));
                    imp.setCargo(rs.getString("cargo"));
                    imp.setTelefone(rs.getString("foner"));
                    imp.setEmail(rs.getString("email"));
                    imp.setNomePai(rs.getString("pai"));
                    imp.setNomeMae(rs.getString("mae"));
                    imp.setNomeConjuge(rs.getString("conjuge"));
                    imp.setDataCadastro(rs.getDate("datareg"));
                    imp.setAtivo(rs.getBoolean("inativo") == false);
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoAccess.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "   numero,\n" +
                    "   data,\n" +
                    "   ecf,\n" +
                    "   historico,\n" +
                    "   vencimento,\n" +
                    "   referencia,\n" +
                    "   codigo as idcliente,\n" +
                    "   movimento - valorr as valor \n" +
                    "from \n" +
                    "   crr \n" +
                    "where \n" +
                    "  loja = " + getLojaOrigem() + " and \n" +
                    "  valorr < movimento\n" +
                    "  order by data")) {
                while(rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    
                    imp.setId(rs.getString("numero"));
                    imp.setNumeroCupom(imp.getId());
                    imp.setDataEmissao(rs.getDate("data"));
                    imp.setEcf(rs.getString("ecf"));
                    imp.setObservacao(rs.getString("historico"));
                    imp.setDataVencimento(rs.getDate("vencimento"));
                    imp.setIdCliente(rs.getString("idcliente"));
                    imp.setValor(rs.getDouble("valor"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }
}
