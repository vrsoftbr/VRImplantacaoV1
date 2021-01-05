package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import vrimplantacao.classe.ConexaoMySQL;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.ContaPagarVencimentoIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;

/**
 *
 * @author Importacao
 */
public class STIDAO extends InterfaceDAO implements MapaTributoProvider {

    private static final Logger LOG = Logger.getLogger(STIDAO.class.getName());
    
    @Override
    public String getSistema() {
        return "STI";
    }
    
    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                new OpcaoProduto[]{
                    OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
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
    
    public List<Estabelecimento> getLojasCliente() throws SQLException {
        List<Estabelecimento> result = new ArrayList<>();
        
        try(Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "SELECT\n" +
                    "  codigo,\n" +
                    "  fantasia\n" +
                    "FROM\n" +
                    "  empresas")) {
                while(rs.next()) {
                    result.add(new Estabelecimento(rs.getString("codigo"), rs.getString("fantasia")));
                }
            }
        }
        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "SELECT\n" +
                    "  codigo,\n" +
                    "  descricao,\n" +
                    "  aliquota,\n" +
                    "  reducao\n" +
                    "FROM\n" +
                    "  aliquotas_icms")) {
                while(rs.next()) {
                    result.add(new MapaTributoIMP(
                            rs.getString("codigo"), 
                            rs.getString("descricao"),
                            0, 
                            rs.getDouble("aliquota"), 
                            rs.getDouble("reducao")));
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "SELECT\n" +
                    "  distinct\n" +
                    "  g.codigo merc1,\n" +
                    "  g.descricao descmerc1,\n" +
                    "  g2.codigo merc2,\n" +
                    "  g2.descricao descmerc2,\n" +
                    "  m.codigo merc3,\n" +
                    "  m.descricao descmerc3\n" +
                    "FROM\n" +
                    "  produtos p\n" +
                    "join grupos g on p.codgrupo = g.codigo\n" +
                    "join grupos2 g2 on p.codgrupo2 = g2.codigo\n" +
                    "join marcas m on p.codmarca = m.codigo")) {
                while(rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rs.getString("merc1"));
                    imp.setMerc1Descricao(rs.getString("descmerc1"));
                    imp.setMerc2ID(rs.getString("merc2"));
                    imp.setMerc2Descricao(rs.getString("descmerc2"));
                    imp.setMerc3ID(rs.getString("merc3"));
                    imp.setMerc3Descricao(rs.getString("descmerc3"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "  p.codpro id,\n" +
                    "  p.descricao,\n" +
                    "  e.codigobarras ean,\n" +
                    "  e.codigobarrasfornecedor,\n" +
                    "  p.unidade,\n" +
                    "  p.qtdporcaixa qtdembalagem,\n" +
                    "  e.precoavista precovenda,\n" +
                    "  e.precoatacado,\n" +
                    "  e.margemlucro margem,\n" +
                    "  e.precocusto,\n" +
                    "  e.quantidade estoque,\n" +
                    "  e.estminimo estoquemin,\n" +
                    "  p.pesoliq,\n" +
                    "  p.pesobruto,\n" +
                    "  p.clasfiscal ncm,\n" +
                    "  p.codigocest cest,\n" +
                    "  p.datacadastro,\n" +
                    "  p.diasvalidade,\n" +
                    "  p.utilizarbalanca,\n" +
                    "  p.exportarbalanca,\n" +
                    "  p.inativo,\n" +
                    "  p.codgrupo merc1,\n" +
                    "  p.codgrupo2 merc2,\n" +
                    "  p.codmarca merc3,\n" +
                    "  p.aliqecf idaliquotaecf,\n" +
                    "  pe.aliqdentroestado idaliquotadebito,\n" +
                    "  p.natreceita,\n" +
                    "  p.aliquotapis,\n" +
                    "  op.descricao operacaopis\n" +
                    "from\n" +
                    "  produtos p\n" +
                    "left join produtos_empresas pe on p.codpro = pe.codprod\n" +
                    "left join estoque e on p.codpro = e.codprod and\n" +
                    "  e.codemp = pe.codemp\n" +
                    "left join grupos_operacoes op on pe.codgrupooperacao = op.codigo\n" +
                    "where\n" +
                    "  pe.codemp = " + getLojaOrigem())) {
                while(rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setDescricaoCompleta(rs.getString("descricao"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setCodMercadologico1(rs.getString("merc1"));
                    imp.setCodMercadologico2(rs.getString("merc2"));
                    imp.setCodMercadologico3(rs.getString("merc3"));
                    
                    long ean = Utils.stringToLong(rs.getString("ean"));
                    
                    if(ean != 0 && ean > 999999) {
                        imp.setEan(rs.getString("ean"));
                    } else {
                        imp.setEan(imp.getImportId());
                    }
                    
                    imp.setTipoEmbalagem(rs.getString("unidade"));
                    imp.setQtdEmbalagem(rs.getInt("qtdembalagem"));
                    imp.setPrecovenda(rs.getDouble("precovenda"));
                    imp.setMargem(rs.getDouble("margem"));
                    imp.setCustoComImposto(rs.getDouble("precocusto"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setEstoque(rs.getDouble("estoque"));
                    imp.setEstoqueMinimo(rs.getDouble("estoquemin"));
                    imp.setPesoLiquido(rs.getDouble("pesoliq"));
                    imp.setPesoBruto(rs.getDouble("pesobruto"));
                    imp.setNcm(rs.getString("ncm"));
                    imp.setCest(rs.getString("cest"));
                    imp.setDataCadastro(rs.getDate("datacadastro"));
                    imp.setValidade(rs.getInt("diasvalidade"));
                    imp.setSituacaoCadastro(rs.getBoolean("inativo") == true ? 0 : 1);
                    
                    imp.setIcmsDebitoId(rs.getString("idaliquotadebito"));
                    imp.setIcmsConsumidorId(imp.getIcmsDebitoId());
                    imp.setIcmsDebitoForaEstadoId(imp.getIcmsDebitoId());
                    imp.setIcmsDebitoForaEstadoNfId(imp.getIcmsDebitoId());
                    
                    imp.setIcmsCreditoId(imp.getIcmsDebitoId());
                    imp.setIcmsCreditoForaEstadoId(imp.getIcmsDebitoId());
                    
                    imp.setPiscofinsNaturezaReceita(rs.getString("natreceita"));
                    double porcentagemPis = rs.getDouble("aliquotapis");
                    
                    imp.setPiscofinsCstDebito(6);
                    if(porcentagemPis > 0) {
                        imp.setPiscofinsCstDebito(1);
                    }
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "  p.codpro id,\n" +
                    "  p.descricao,\n" +
                    "  e.codigobarras ean,\n" +
                    "  e.codigobarrasfornecedor,\n" +
                    "  p.unidade,\n" +
                    "  p.qtdporcaixa qtdembalagem\n" +
                    "from\n" +
                    "  produtos p\n" +
                    "left join produtos_empresas pe on p.codpro = pe.codprod\n" +
                    "left join estoque e on p.codpro = e.codprod and\n" +
                    "  e.codemp = pe.codemp\n" +
                    "where\n" +
                    "  pe.codemp = " + getLojaOrigem())) {
                while(rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setTipoEmbalagem(rs.getString("unidade"));
                    imp.setEan(rs.getString("codigobarrasfornecedor"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "SELECT\n" +
                    "  f.codigo,\n" +
                    "  f.cnpj,\n" +
                    "  f.insc_est ie,\n" +
                    "  f.razao,\n" +
                    "  f.fantasia,\n" +
                    "  f.endereco,\n" +
                    "  f.numero,\n" +
                    "  f.complemento,\n" +
                    "  f.bairro,\n" +
                    "  f.cep,\n" +
                    "  f.cidade,\n" +
                    "  f.uf,\n" +
                    "  f.fone,\n" +
                    "  f.celular,\n" +
                    "  f.fax,\n" +
                    "  f.contato,\n" +
                    "  f.email,\n" +
                    "  f.obs,\n" +
                    "  f.inativo\n" +
                    "FROM\n" +
                    "  fornecedores f")) {
                while(rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("codigo"));
                    imp.setCnpj_cpf(rs.getString("cnpj"));
                    imp.setIe_rg(rs.getString("ie"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setCep(rs.getString("cep"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("uf"));
                    imp.setTel_principal(rs.getString("fone"));
                    
                    String celular = rs.getString("celular"),
                            fax = rs.getString("fax"),
                            contato = rs.getString("contato"),
                            email = rs.getString("email"),
                            obs = rs.getString("obs");
                    
                    if(celular != null && !"".equals(celular)) {
                        imp.addContato("1", "CELULAR", null, celular, TipoContato.NFE, null);
                    }
                    
                    if(fax != null && !"".equals(fax)) {
                        imp.addContato("2", "FAX", fax, null, TipoContato.NFE, null);
                    }
                    
                    if(contato != null && !"".equals(contato)) {
                        imp.addContato("3", contato, null, null, TipoContato.NFE, null);
                    }
                    
                    if(email != null && !"".equals(email)) {
                        imp.addContato("4", "EMAIL", null, null, TipoContato.NFE, email);
                    }
                    
                    if(obs != null && !"".equals(obs)) {
                        imp.setObservacao(obs);
                    }
                    
                    imp.setAtivo(rs.getBoolean("inativo") == false);
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "SELECT\n" +
                    "  p.codproduto idproduto,\n" +
                    "  p.codfornecedor idforn,\n" +
                    "  p.codprodutofornecedor codigoexterno\n" +
                    "FROM\n" +
                    "  prod_fornec p")) {
                while(rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rs.getString("idproduto"));
                    imp.setIdFornecedor(rs.getString("idforn"));
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
        
        try(Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "SELECT\n" +
                    "  c.codigo_cliente id,\n" +
                    "  c.nome,\n" +
                    "  c.fantasia,\n" +
                    "  c.cnpj_cpf cnpj,\n" +
                    "  c.insc_rg ie,\n" +
                    "  c.data,\n" +
                    "  c.logradouro,\n" +
                    "  c.numero,\n" +
                    "  c.complemento,\n" +
                    "  c.bairro,\n" +
                    "  c.cidade,\n" +
                    "  c.uf,\n" +
                    "  c.cep,\n" +
                    "  c.fone,\n" +
                    "  c.celular,\n" +
                    "  c.fax,\n" +
                    "  c.email,\n" +
                    "  c.verifica_credito,\n" +
                    "  c.contato,\n" +
                    "  c.obs,\n" +
                    "  c.bloqueado,\n" +
                    "  c.inativo,\n" +
                    "  c.limitecredito,\n" +
                    "  c.datacadastro,\n" +
                    "  c.diavectomensalidade vencimento,\n" +
                    "  c.sexo,\n" +
                    "  c.estado_civil\n" +
                    "FROM\n" +
                    "  clientes c")) {
                while(rs.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    
                    imp.setId(rs.getString("id"));
                    imp.setRazao(rs.getString("nome"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setCnpj(rs.getString("cnpj"));
                    imp.setInscricaoestadual(rs.getString("ie"));
                    imp.setDataCadastro(rs.getDate("datacadastro"));
                    imp.setDataNascimento(rs.getDate("data"));
                    imp.setEndereco(rs.getString("logradouro"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));
                    imp.setTelefone(rs.getString("fone"));
                    imp.setCelular(rs.getString("celular"));
                    imp.setFax(rs.getString("fax"));
                    imp.setEmail(rs.getString("email"));
                    imp.setValorLimite(rs.getDouble("limitecredito"));
                    
                    if(imp.getValorLimite() > 0) {
                        imp.setPermiteCheque(true);
                        imp.setPermiteCreditoRotativo(true);
                    }
                    
                    String obs = rs.getString("obs"), contato = rs.getString("contato");
                    
                    if(contato != null && !"".equals(contato)) {
                        imp.addContato("1", contato, null, null, null);
                    }
                    
                    if(obs != null && !"".equals(obs)) {
                        imp.setObservacao(obs);
                    }
                    
                    imp.setAtivo(rs.getBoolean("inativo") == false);
                    imp.setDiaVencimento(rs.getInt("vencimento"));
                    
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
                    "SELECT\n" +
                    "  c.codigo id,\n" +
                    "  c.dataemissao emissao,\n" +
                    "  c.valor,\n" +
                    "  c.vencimento,\n" +
                    "  c.parcela,\n" +
                    "  c.codvenda venda,\n" +
                    "  c.numdocto doc,\n" +
                    "  c.clientes_idclientes idcliente,\n" +
                    "  c.codformapagto formapagamento,\n" +
                    "  c.codconta conta,\n" +
                    "  c.multa,\n" +
                    "  c.juros,\n" +
                    "  c.desconto\n" +
                    "FROM\n" +
                    "  cond_pagto c\n" +
                    "where\n" +
                    "  datapagto is null and\n" +
                    "  empresas_idempresas = " + getLojaOrigem() + " and\n" +
                    "  statusvalidotrigger = true\n" +        
                    "order by\n" +
                    "  vencimento")) {
                while(rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    
                    imp.setId(rs.getString("id"));
                    imp.setDataEmissao(rs.getDate("emissao"));
                    imp.setValor(rs.getDouble("valor"));
                    imp.setDataVencimento(rs.getDate("vencimento"));
                    imp.setParcela(rs.getInt("parcela"));
                    imp.setIdCliente(rs.getString("idcliente"));
                    imp.setNumeroCupom(rs.getString("venda"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ContaPagarIMP> getContasPagar() throws Exception {
        List<ContaPagarIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select distinct `cond_pagto_pagar`.`codigo` AS `codigo`,\n" +
                    "         ifnull(`compras`.`numDocto`,`contas_pagar`.`documento`) AS `numdocto`,\n" +
                    "         `contas_pagar`.`codigo` AS `codcontpagar`,\n" +
                    "         `contas_pagar`.`historico` AS `historico`,\n" +
                    "         `cond_pagto_pagar`.`codContasPagar` AS `codcontaspagar`,\n" +
                    "         `cond_pagto_pagar`.`codCompra` AS `codcompra`,\n" +
                    "         `cond_pagto_pagar`.`parcela` AS `parcela`,\n" +
                    "         `cond_pagto_pagar`.`vencimento` AS `vencimento`,\n" +
                    "         `cond_pagto_pagar`.`valor` AS `valor`,\n" +
                    "         `cond_pagto_pagar`.`codFormaPagto` AS `codformapagto`,\n" +
                    "         `cond_pagto_pagar`.`dataPagto` AS `datapagto`,\n" +
                    "         `cond_pagto_pagar`.`juros` AS `juros`,\n" +
                    "         `cond_pagto_pagar`.`multa` AS `multa`,\n" +
                    "         `cond_pagto_pagar`.`pago` AS `pago`,\n" +
                    "         `cond_pagto_pagar`.`valorPagto` AS `valorpagto`,\n" +
                    "         `cond_pagto_pagar`.`desconto` AS `desconto`,\n" +
                    "         ifnull(`contas_pagar`.`codEmp`,`compras`.`codEmp`) AS `codemp`,\n" +
                    "         `formapagto`.`descricao` AS `descformapagto`,\n" +
                    "         ifnull(`fornecedores`.`razao`,`fornecedores_1`.`razao`) AS `forrazao`,\n" +
                    "         ifnull(`fornecedores`.`codigo`,`fornecedores_1`.`codigo`) AS `forcod`,\n" +
                    "         ifnull(`compras`.`dataEmissao`,`contas_pagar`.`dataEmissao`) AS `data`,\n" +
                    "         (to_days(`cond_pagto_pagar`.`vencimento`) - to_days(`cond_pagto_pagar`.`dataPagto`)) AS `atraso`,\n" +
                    "         `plano_contas`.`codigo` AS `codplanoconta`,\n" +
                    "         `plano_contas`.`descricao` AS `descricao`,\n" +
                    "         `cond_pagto_pagar`.`habilitado` AS `habilitado`\n" +
                    "from     (((((`cond_pagto_pagar`\n" +
                    "left join (`compras`\n" +
                    "left join `fornecedores` on((`compras`.`codFor` = `fornecedores`.`codigo`))) on((`cond_pagto_pagar`.`codCompra` = `compras`.`codigo`)))\n" +
                    "left join `contas_pagar` on((`cond_pagto_pagar`.`codContasPagar` = `contas_pagar`.`codigo`)))\n" +
                    "join     `plano_contas`  on(((`cond_pagto_pagar`.`codConta` = `plano_contas`.`codigo`)\n" +
                    "         and (`plano_contas`.`gerarFinanceiro` is true))))\n" +
                    "left join `fornecedores` `fornecedores_1` on((`contas_pagar`.`codFor` = `fornecedores_1`.`codigo`)))\n" +
                    "left join `formapagto`                    on((`formapagto`.`codigo` = `cond_pagto_pagar`.`codFormaPagto`)))\n" +
                    "where    ((`cond_pagto_pagar`.`provisao` is false)\n" +
                    "         and (`cond_pagto_pagar`.`pago` is false)\n" +
                    "         and ((`compras`.`liberado` is true)\n" +
                    "         or (`contas_pagar`.`codigo` > 0)))\n" +
                    "         and ifnull(`contas_pagar`.`codEmp`,`compras`.`codEmp`) = " + getLojaOrigem())) {
                while(rs.next()) {
                    ContaPagarIMP imp = new ContaPagarIMP();
                    
                    imp.setId(rs.getString("codigo"));
                    imp.setNumeroDocumento(rs.getString("numdocto"));
                    imp.setIdFornecedor(rs.getString("forcod"));
                    imp.setDataEmissao(rs.getDate("data"));
                    ContaPagarVencimentoIMP parc = imp.addVencimento(rs.getDate("vencimento"), rs.getDouble("valor"));
                    
                    parc.setNumeroParcela(Utils.stringToInt(rs.getString("parcela"), 1));
                    parc.setObservacao(rs.getString("historico") == null ? "" : rs.getString("historico"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
    
    private Date dataInicioVenda;
    private Date dataTerminoVenda;

    public void setDataInicioVenda(Date dataInicioVenda) {
        this.dataInicioVenda = dataInicioVenda;
    }

    public void setDataTerminoVenda(Date dataTerminoVenda) {
        this.dataTerminoVenda = dataTerminoVenda;
    }

    @Override
    public Iterator<VendaIMP> getVendaIterator() throws Exception {
        return new STIDAO.VendaIterator(getLojaOrigem(), dataInicioVenda, dataTerminoVenda);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new STIDAO.VendaItemIterator(getLojaOrigem(), dataInicioVenda, dataTerminoVenda);
    }
    
    private static class VendaIterator implements Iterator<VendaIMP> {

        public final static SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");

        private Statement stm = ConexaoMySQL.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaIMP next;
        private Set<String> uk = new HashSet<>();

        private void obterNext() {
            try {
                SimpleDateFormat timestampDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                SimpleDateFormat timestamp = new SimpleDateFormat("hh:mm:ss");
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaIMP();
                        String id = rst.getString("codigo");
                        if (!uk.add(id)) {
                            LOG.warning("Venda " + id + " já existe na listagem");
                        }
                        next.setId(id);
                        next.setNumeroCupom(Utils.stringToInt(rst.getString("codigo")));
                        next.setEcf(Utils.stringToInt(rst.getString("ecf")));
                        next.setData(rst.getDate("emissao"));
                        next.setIdClientePreferencial(rst.getString("idcliente"));
                        
                        /*String horaInicio = timestampDate.format(rst.getDate("data_hora"));
                        String horaTermino = timestampDate.format(rst.getDate("data_hora"));
                        next.setHoraInicio(timestamp.parse(horaInicio));
                        next.setHoraTermino(timestamp.parse(horaTermino));*/
                        
                        next.setSubTotalImpressora(rst.getDouble("valortotal"));
                        next.setCpf(rst.getString("cnpj_cpf"));
                        next.setNomeCliente(rst.getString("nome"));
                        String endereco
                                = Utils.acertarTexto(rst.getString("endereco")) + ","
                                + Utils.acertarTexto(rst.getString("numero")) + ","
                                + Utils.acertarTexto(rst.getString("complemento")) + ","
                                + Utils.acertarTexto(rst.getString("bairro")) + ","
                                + Utils.acertarTexto(rst.getString("cidade")) + "-"
                                + Utils.acertarTexto(rst.getString("uf")) + ","
                                + Utils.acertarTexto(rst.getString("cep"));
                        next.setEnderecoCliente(endereco);
                    }
                }
            } catch (SQLException ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                System.out.println(next.getId() + " - " + next.getData());
                throw new RuntimeException(ex);
            }
        }

        public VendaIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {
            this.sql
                    =   "select\n" +
                        "  v.codigo,\n" +
                        "  v.codcli idcliente,\n" +
                        "  v.dataemissao emissao,\n" +
                        "  v.data_hora,\n" +
                        "  v.valortotal,\n" +
                        "  v.desconto,\n" +
                        "  v.status,\n" +
                        "  v.numcaixa ecf,\n" +
                        "  v.cancelada,\n" +
                        "  c.nome,\n" +
                        "  c.cnpj_cpf,\n" +
                        "  c.logradouro endereco,\n" +
                        "  c.numero,\n" +
                        "  c.complemento,\n" +
                        "  c.bairro,\n" +
                        "  c.cidade,\n" +
                        "  c.uf,\n" +
                        "  c.cep\n" +
                        "from\n" +
                        "  vendas v left join clientes c on v.codcli = c.codigo_cliente\n" +
                        "where\n" +
                        "  v.codemp = " + idLojaCliente + " and\n" +
                        "  v.dataemissao between '" + 
                    FORMAT.format(dataInicio) + "' and '" + FORMAT.format(dataTermino) + "'";
            LOG.log(Level.FINE, "SQL da venda: " + sql);
            rst = stm.executeQuery(sql);
        }

        @Override
        public boolean hasNext() {
            obterNext();
            return next != null;
        }

        @Override
        public VendaIMP next() {
            obterNext();
            VendaIMP result = next;
            next = null;
            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }
    }
    
    private static class VendaItemIterator implements Iterator<VendaItemIMP> {

        private Statement stm = ConexaoMySQL.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaItemIMP next;

        private void obterNext() {
            try {
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaItemIMP();

                        next.setId(rst.getString("id"));
                        next.setVenda(rst.getString("idvenda"));
                        next.setSequencia(rst.getInt("sequencia"));
                        next.setProduto(rst.getString("idproduto"));
                        next.setDescricaoReduzida(rst.getString("descricao"));
                        next.setQuantidade(rst.getDouble("quantidade"));
                        next.setTotalBruto(rst.getDouble("total"));
                        next.setValorDesconto(rst.getDouble("desconto"));
                        next.setCancelado(rst.getBoolean("cancelado"));
                        next.setCodigoBarras(rst.getString("codigobarras"));
                        next.setUnidadeMedida(rst.getString("unidade"));

                        String trib = rst.getString("icms");
                        if (trib != null && !"".equals(trib.trim())) {
                            obterAliquota(next, trib);
                        }
                    }
                }
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        /**
         * Método temporario, desenvolver um mapeamento eficiente da tributação.
         *
         * @param item
         * @throws SQLException
         */
        public void obterAliquota(VendaItemIMP item, String icms) throws SQLException {
            
            int cst;
            double aliq;
            switch (icms) {
                case "07":
                    cst = 0;
                    aliq = 7;
                    break;
                case "11":
                    cst = 0;
                    aliq = 11;
                    break;
                case "12":
                    cst = 0;
                    aliq = 12;
                    break;
                case "18":
                    cst = 0;
                    aliq = 18;
                    break;
                case "25":
                    cst = 0;
                    aliq = 25;
                    break;
                case "ST":
                    cst = 60;
                    aliq = 0;
                    break;
                default:
                    cst = 40;
                    aliq = 0;
                    break;
            }
            item.setIcmsCst(cst);
            item.setIcmsAliq(aliq);
        }

        public VendaItemIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {
            this.sql
                    =   "select\n" +
                        "  i.codigo id,\n" +
                        "  i.item sequencia,\n" +
                        "  i.codvenda idvenda,\n" +
                        "  i.codprod idproduto,\n" +
                        "  (select\n" +
                        "    codigobarras\n" +
                        "  from\n" +
                        "    estoque\n" +
                        "  where\n" +
                        "    codprod = i.codprod limit 1) codigobarras,\n" +
                        "  p.unidade,\n" +
                        "  p.descricao,\n" +
                        "  i.quantidade,\n" +
                        "  i.valoravista precovenda,\n" +
                        "  i.valortotalitem total,\n" +
                        "  i.desconto,\n" +
                        "  i.codigoaliquotaecf idaliquota,\n" +
                        "  al.aliquota icms,\n" +
                        "  i.cancelado\n" +
                        "from\n" +
                        "  itens_vendas i join produtos p on i.codprod = p.codpro\n" +
                        "  join vendas v on i.codvenda = v.codigo\n" +
                        "  left join aliquotas_ecf al on i.codigoaliquotaecf = al.codigo\n" +
                        "where\n" +
                        "  v.dataemissao between '" + VendaIterator.FORMAT.format(dataInicio) + "' and '" + VendaIterator.FORMAT.format(dataTermino) + "' and codempresa = " + idLojaCliente;
            LOG.log(Level.FINE, "SQL da venda: " + sql);
            rst = stm.executeQuery(sql);
        }

        @Override
        public boolean hasNext() {
            obterNext();
            return next != null;
        }

        @Override
        public VendaItemIMP next() {
            obterNext();
            VendaItemIMP result = next;
            next = null;
            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }
    }
}
