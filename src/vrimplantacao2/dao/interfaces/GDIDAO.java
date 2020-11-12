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
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.SituacaoCadastro;
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
public class GDIDAO extends InterfaceDAO implements MapaTributoProvider {

    private static final Logger LOG = Logger.getLogger(GDIDAO.class.getName());
    public String complemento = "";
    
    @Override
    public String getSistema() {
        return "GDI" + complemento;
    }
    
    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                new OpcaoProduto[] {
                    OpcaoProduto.MERCADOLOGICO,
                    OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                    OpcaoProduto.MERCADOLOGICO_PRODUTO,
                    OpcaoProduto.FAMILIA,
                    OpcaoProduto.FAMILIA_PRODUTO,
                    OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                    OpcaoProduto.PRODUTOS,
                    OpcaoProduto.EAN,
                    OpcaoProduto.EAN_EM_BRANCO,
                    OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
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
                    OpcaoProduto.ESTOQUE,
                    OpcaoProduto.ATIVO,
                    OpcaoProduto.NCM,
                    OpcaoProduto.CEST,
                    OpcaoProduto.PIS_COFINS,
                    OpcaoProduto.NATUREZA_RECEITA,
                    OpcaoProduto.ICMS,
                    OpcaoProduto.PAUTA_FISCAL,
                    OpcaoProduto.PAUTA_FISCAL_PRODUTO,
                    OpcaoProduto.MARGEM
                }
        ));
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	cod_aliquota id,\n" +
                    "	percentual,\n" +
                    "	descricao\n" +
                    "from \n" +
                    "	aliquota")) {
                while(rs.next()) {
                    result.add(new MapaTributoIMP(rs.getString("percentual").trim(), rs.getString("descricao")));
                }
            }
        }
        return result;
    }
    
    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> lojas = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "    id_empresa id,\n" +
                    "    fantasia\n" +
                    "from\n" +
                    "    tempresa"
            )) {
                while (rs.next()) {
                    lojas.add(new Estabelecimento(rs.getString("id"), rs.getString("fantasia")));
                }
            }
        }
        return lojas;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "	distinct\n" +
                    "	p.cod_marca merc1,\n" +
                    "	m.descricao descmerc1,\n" +
                    "	p.cod_grupo merc2,\n" +
                    "	g.descricao descmerc2,\n" +
                    "	p.cod_subgrupo merc3,\n" +
                    "	s.descricao descmerc3\n" +
                    "from \n" +
                    "	produto p\n" +
                    "join marca m on p.cod_marca = m.cod_marca\n" +
                    "join grupo g on p.cod_grupo = g.cod_grupo\n" +
                    "join subgrupo s on p.cod_subgrupo = s.cod_subgrupo\n" +
                    "order by \n" +
                    "	2, 4, 6")) {
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
        
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "    p.id_produto id,\n" +
                    "    p.descricao,\n" +
                    "    p.desc_resumida descricaoreduzida,\n" +
                    "    ean.codi_barra ean,\n" +
                    "    ean.unid_fator fatorembalagem,\n" +
                    "    ean.peso_bruto,\n" +
                    "    ean.peso_liquido,\n" +
                    "    ean.ativo,\n" +
                    "    ean.excluido,\n" +
                    "    ean.minimo,\n" +
                    "    p.pesavel,\n" +
                    "    p.id_balanca,\n" +
                    "    p.validade,\n" +
                    "    p.unid_venda embalagem,\n" +
                    "    ean.unid_compra embalagemcompra,\n" +
                    "    pp.prec_venda precovenda,\n" +
                    "    pp.marg_venda margem,\n" +
                    "    pp.cust_medio customedio,\n" +
                    "    pp.cust_atual custocomimposto,\n" +
                    "    pp.cust_compra custosemimposto,\n" +
                    "    p.qtde_embalagem qtdembalagem,\n" +
                    "    p.excluido,\n" +
                    "    p.codi_ncm ncm,\n" +
                    "    p.codi_cest cest,\n" +
                    "    p.id_pis pisdebito,\n" +
                    "    p.id_cofins cofinsdebito,\n" +
                    "    p.ent_pis piscredito,\n" +
                    "    p.ent_cofins cofinscredito,\n" +
                    "    p.id_familia,\n" +
                    "    p.id_natreceita naturezareceita,\n" +
                    "    p.cbenef beneficio,\n" +
                    "    pp.id_cst cstdebito,\n" +
                    "    pp.aliq_icms icmsdebito,\n" +
                    "    pp.redu_icms reducaodebito\n" +
                    "from\n" +
                    "    tprodutos p\n" +
                    "left join tprecos pp on p.id_produto = pp.id_produto\n" +
                    "left join tbarras ean on p.id_produto = ean.id_produto")) {
                while(rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setEan(rs.getString("ean"));
                    imp.setTipoEmbalagem(rs.getString("embalagem"));
                    
                    String balanca = rs.getString("pesavel");
                    
                    if(balanca != null && !"".equals(balanca) && "S".equals(balanca.trim())) {
                        imp.seteBalanca(true);
                        long ean = rs.getLong("ean");
                        
                        if(ean != 0 && ean <= 999999) {
                            imp.setEan(imp.getEan().substring(0, imp.getEan().length() - 1));
                        }
                    }
                    
                    imp.setValidade(rs.getInt("validade"));
                    imp.setDescricaoCompleta(Utils.acertarTexto(rs.getString("descricao")));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setDescricaoReduzida(rs.getString("descricaoreduzida"));
                    //imp.setCodMercadologico1(rs.getString("mercadologico1"));
                    //imp.setCodMercadologico2(rs.getString("mercadologico2"));
                    //imp.setCodMercadologico3(rs.getString("mercadologico3"));
                    imp.setCustoComImposto(rs.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rs.getDouble("custosemimposto"));
                    imp.setPrecovenda(rs.getDouble("precovenda"));
                    imp.setMargem(rs.getDouble("margem"));
                    //imp.setEstoqueMaximo(rs.getDouble("estq_maximo"));
                    //imp.setEstoqueMinimo(rs.getDouble("estq_minimo"));
                    //imp.setEstoque(rs.getDouble("estoque"));
                    imp.setSituacaoCadastro("S".equals(rs.getString("ativo").trim()) ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    imp.setPesoBruto(rs.getDouble("peso_bruto"));
                    imp.setPesoLiquido(rs.getDouble("peso_liquido"));
                    //imp.setDataCadastro(rs.getDate("cadastro"));
                    imp.setNcm(rs.getString("ncm"));
                    imp.setCest(rs.getString("cest"));
                    imp.setPiscofinsCstDebito(rs.getString("pisdebito"));
                    
                    imp.setIcmsCstConsumidor(rs.getInt("cstdebito"));
                    imp.setIcmsAliqConsumidor(rs.getDouble("icmsdebito"));
                    imp.setIcmsReducaoSaida(rs.getDouble("reducaodebito"));
                    
                    /*imp.setIcmsDebitoId(rs.getString("aliquota").trim());
                    imp.setIcmsDebitoForaEstadoId(imp.getIcmsDebitoId());
                    imp.setIcmsConsumidorId(imp.getIcmsDebitoId());
                    imp.setIcmsDebitoForaEstadoNfId(imp.getIcmsDebitoId());
                    imp.setIcmsCreditoId(imp.getIcmsDebitoId());
                    imp.setIcmsCreditoForaEstadoId(imp.getIcmsDebitoId());*/
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "    p.id_parceiro id,\n" +
                    "    p.nome,\n" +
                    "    p.fantasia,\n" +
                    "    p.cnpj_cpf,\n" +
                    "    p.ie_rg,\n" +
                    "    p.im,\n" +
                    "    en.cep,\n" +
                    "    en.logradouro,\n" +
                    "    en.numero,\n" +
                    "    en.complemento,\n" +
                    "    en.cidade,\n" +
                    "    en.estado,\n" +
                    "    en.bairro, \n" +
                    "    p.contato,\n" +
                    "    p.celular,\n" +
                    "    p.fone,\n" +
                    "    p.fax,\n" +
                    "    p.salario,\n" +
                    "    p.site,\n" +
                    "    p.empr_telefone,\n" +
                    "    p.empr_cargo,\n" +
                    "    p.empr_trabalho,\n" +
                    "    p.fide_desde,\n" +
                    "    p.limi_credito,\n" +
                    "    p.limi_cheque,\n" +
                    "    p.id_situacao,\n" +
                    "    p.data_cadastro,\n" +
                    "    p.email,\n" +
                    "    p.esta_civil,\n" +
                    "    p.observacao,\n" +
                    "    p.data_nascimento,\n" +
                    "    p.sexo\n" +
                    "from\n" +
                    "    tparceiros p\n" +
                    "left join tenderecos en on p.id_parceiro = en.id_parceiro\n" +
                    "where\n" +
                    "    cliente = 0")) {
                while(rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setAtivo("A".equals(rs.getString("situacao")));
                    imp.setDatacadastro(rs.getDate("cadastro"));
                    imp.setRazao(Utils.acertarTexto(rs.getString("nome")));
                    imp.setFantasia(Utils.acertarTexto(rs.getString("fantasia")));
                    imp.setCnpj_cpf(rs.getString("cpf_cnpj"));
                    imp.setIe_rg(rs.getString("rg_insc"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setCep(rs.getString("cep"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setIbge_municipio(rs.getInt("cidadeibge"));
                    imp.setUf(rs.getString("uf"));
                    imp.setTel_principal(rs.getString("fone"));
                    imp.setObservacao(rs.getString("obs"));
                    
                    String celular = rs.getString("celular"), contato = rs.getString("contato");
                    
                    if(celular != null && !"".equals(celular)) {
                        imp.addContato("1", "CELULAR", null, celular, TipoContato.COMERCIAL, null);
                    }
                    
                    if(contato != null && !"".equals(contato)) {
                        imp.addContato("2", contato, null, null, TipoContato.COMERCIAL, null);
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
        
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "    i.codi_barra,\n" +
                    "    cb.id_produto,\n" +
                    "    max(m.data) data,\n" +
                    "    max(m.id_parceiro) idfornecedor\n" +
                    "from titens i\n" +
                    "left join tmovimento m on m.id_movimento = i.id_movimento and\n" +
                    "    m.tipo = 1\n" +
                    "left join tbarras cb on i.codi_barra = cb.codi_barra\n" +
                    "where\n" +
                    "    m.id_parceiro is not null and\n" +
                    "    cb.id_produto is not null\n" +
                    "group by\n" +
                    "    i.codi_barra,\n" +
                    "    cb.id_produto")) {
                while(rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rs.getString("id_produto"));
                    imp.setIdFornecedor(rs.getString("idfornecedor"));
                    imp.setCodigoExterno(rs.getString("codi_barra"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "    p.id_parceiro id,\n" +
                    "    p.nome,\n" +
                    "    p.fantasia,\n" +
                    "    p.cnpj_cpf,\n" +
                    "    p.ie_rg,\n" +
                    "    p.im,\n" +
                    "    en.cep,\n" +
                    "    en.logradouro,\n" +
                    "    en.numero,\n" +
                    "    en.complemento,\n" +
                    "    en.cidade,\n" +
                    "    en.estado,\n" +
                    "    en.bairro, \n" +
                    "    p.contato,\n" +
                    "    p.celular,\n" +
                    "    p.fone,\n" +
                    "    p.fax,\n" +
                    "    p.salario,\n" +
                    "    p.site,\n" +
                    "    p.empr_telefone,\n" +
                    "    p.empr_cargo,\n" +
                    "    p.empr_trabalho,\n" +
                    "    p.fide_desde,\n" +
                    "    p.limi_credito,\n" +
                    "    p.limi_cheque,\n" +
                    "    p.id_situacao,\n" +
                    "    p.data_cadastro,\n" +
                    "    p.email,\n" +
                    "    p.esta_civil,\n" +
                    "    p.observacao,\n" +
                    "    p.data_nascimento,\n" +
                    "    p.sexo\n" +
                    "from\n" +
                    "    tparceiros p\n" +
                    "left join tenderecos en on p.id_parceiro = en.id_parceiro\n" +
                    "where\n" +
                    "    cliente = -1")) {
                while(rs.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    
                    imp.setId(rs.getString("id"));
                    imp.setAtivo("A".equals(rs.getString("situacao")));
                    imp.setDataCadastro(rs.getDate("cadastro"));
                    imp.setValorLimite(rs.getDouble("limite"));
                    imp.setRazao(Utils.acertarTexto(rs.getString("nome")));
                    imp.setFantasia(Utils.acertarTexto(rs.getString("fantasia")));
                    imp.setCnpj(rs.getString("cpf_cnpj"));
                    imp.setInscricaoestadual(rs.getString("rg_insc"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setCep(rs.getString("cep"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setTelefone(rs.getString("fone"));
                    imp.setObservacao(rs.getString("obs"));
                    imp.setCelular(rs.getString("celular"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "	cr.cod_contas_receber id,\n" +
                    "	cr.cod_cliente idcliente,\n" +
                    "	cr.dt_emissao emissao,\n" +
                    "	cr.dt_vencimento vencimento,\n" +
                    "	cr.parcela,\n" +
                    "	cr.valor,\n" +
                    "	cr.vl_pago,\n" +
                    "	cr.documento,\n" +
                    "	cr.obs\n" +
                    "from\n" +
                    "	contas_receber cr\n" +
                    "where \n" +
                    "	cr.cod_empresa = " + getLojaOrigem() + " and\n" +
                    "	cr.dt_pagamento is null")) {
                while(rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    
                    imp.setId(rs.getString("id"));
                    imp.setIdCliente(rs.getString("idcliente"));
                    imp.setDataEmissao(rs.getDate("emissao"));
                    imp.setDataVencimento(rs.getDate("vencimento"));
                    
                    String parc = rs.getString("parcela");
                    String parcelas[] = parc.split("/");
                    
                    imp.setParcela(Integer.valueOf(parcelas[0]));
                    imp.setValor(rs.getDouble("valor"));
                    imp.setObservacao(rs.getString("obs"));
                    imp.setNumeroCupom(rs.getString("documento"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ContaPagarIMP> getContasPagar() throws Exception {
        List<ContaPagarIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	cp.cod_contas_pagar id,\n" +
                    "	cp.documento,\n" +
                    "	cp.cod_fornecedor idfornecedor,\n" +
                    "	cp.dt_emissao emissao,\n" +
                    "	cp.dt_vencimento vencimento,\n" +
                    "	cp.valor,\n" +
                    "	cp.parcela\n" +
                    "from \n" +
                    "	contas_pagar cp \n" +
                    "where \n" +
                    "	cp.cod_empresa = " + getLojaOrigem() + " and \n" +
                    "	cp.dt_pagamento is null")) {
                while(rs.next()) {
                    ContaPagarIMP imp = new ContaPagarIMP();
                    
                    imp.setId(rs.getString("id"));
                    imp.setNumeroDocumento(rs.getString("documento"));
                    imp.setIdFornecedor(rs.getString("idfornecedor"));
                    imp.setDataEmissao(rs.getDate("emissao"));
                    ContaPagarVencimentoIMP parc = imp.addVencimento(rs.getDate("vencimento"), rs.getDouble("valor"));
                    
                    String parcela = rs.getString("parcela");
                    String pr[] = parcela.split("/");
                    
                    parc.setNumeroParcela(Integer.valueOf(pr[0]));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
