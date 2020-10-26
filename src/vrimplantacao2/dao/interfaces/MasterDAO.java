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
public class MasterDAO extends InterfaceDAO implements MapaTributoProvider {

    private static final Logger LOG = Logger.getLogger(MasterDAO.class.getName());
    public String complemento = "";
    
    @Override
    public String getSistema() {
        return "Master" + complemento;
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
                    "select \n" +
                    "	cod_empresa id,\n" +
                    "	nome_fantasia fantasia\n" +
                    "from \n" +
                    "	empresa"
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
        Set<String> eanValidoBalanca = new HashSet<>();
        
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            
            try(ResultSet rs1 = stm.executeQuery(
                    "select\n" +
                    "	p.cod_produto id,\n" +
                    "	pe.cod_barra ean\n" +
                    "from\n" +
                    "	produto p\n" +
                    "left join produto_custo pc on p.cod_produto = pc.cod_produto\n" +
                    "left join produto_codbarra pe on p.cod_produto = pe.cod_produto\n" +
                    "where\n" +
                    "	pc.cod_empresa = " + getLojaOrigem() + " and\n" +
                    "	p.sn_balanca = 'S'")) {
                while(rs1.next()) {
                    eanValidoBalanca.add(rs1.getString("id"));
                }
            }
            
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "	p.cod_produto id,\n" +
                    "	p.cod_externo,\n" +
                    "	--p.cod_balanca,\n" +
                    "	pe.cod_barra ean,\n" +
                    "	p.sn_balanca balanca,\n" +
                    "	pc.validade,\n" +
                    "	p.descricao,\n" +
                    "	p.cod_marca mercadologico1,\n" +
                    "	p.cod_grupo mercadologico2,\n" +
                    "	p.cod_subgrupo mercadologico3,\n" +
                    "	pc.vl_custo custo,\n" +
                    "	pc.vl_avista precovenda,\n" +
                    "	pc.vl_prazo,\n" +
                    "	pc.estq_maximo,\n" +
                    "	pc.estq_minimo,\n" +
                    "	pc.qtde_estq estoque,\n" +
                    "	pc.qtde_estq_disp estdisponivel,\n" +
                    "	pc.ativo_inativo situacao,\n" +
                    "	p.unidade,\n" +
                    "	p.peso_bruto,\n" +
                    "	p.peso_liquido,\n" +
                    "	p.dh_inclusao cadastro,\n" +
                    "	cf.ncm,\n" +
                    "   p.cod_class_fiscal_cest,\n" +        
                    "	pce.pe_aliquota aliquota,\n" +
                    "	pc.margem_lucro margem,\n" +
                    "	pc.cst_cofins,\n" +
                    "	pc.cst_pis\n" +
                    "from\n" +
                    "	produto p\n" +
                    "left join class_fiscal cf on p.cod_class_fiscal = cf.cod_class_fiscal \n" +
                    "left join produto_custo pc on p.cod_produto = pc.cod_produto\n" +
                    "left join produto_codbarra pe on p.cod_produto = pe.cod_produto\n" +
                    "left join produto_custo_estado pce on p.cod_produto = pce.cod_produto and\n" +
                    "    pc.cod_empresa = pce.cod_empresa\n" +        
                    "where\n" +
                    "	pc.cod_empresa = " + getLojaOrigem())) {
                while(rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setEan(rs.getString("ean"));
                    imp.setTipoEmbalagem(rs.getString("unidade"));
                    
                    String balanca = rs.getString("balanca");
                    
                    if(balanca != null && !"".equals(balanca) && "S".equals(balanca.trim())) {
                        imp.seteBalanca(true);
                        imp.setEan(imp.getImportId());
                    }
                    
                    imp.setValidade(rs.getInt("validade"));
                    imp.setDescricaoCompleta(Utils.acertarTexto(rs.getString("descricao")));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setCodMercadologico1(rs.getString("mercadologico1"));
                    imp.setCodMercadologico2(rs.getString("mercadologico2"));
                    imp.setCodMercadologico3(rs.getString("mercadologico3"));
                    imp.setCustoComImposto(rs.getDouble("custo"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setPrecovenda(rs.getDouble("precovenda"));
                    imp.setMargem(rs.getDouble("margem"));
                    imp.setEstoqueMaximo(rs.getDouble("estq_maximo"));
                    imp.setEstoqueMinimo(rs.getDouble("estq_minimo"));
                    imp.setEstoque(rs.getDouble("estoque"));
                    imp.setSituacaoCadastro("A".equals(rs.getString("situacao")) ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    imp.setPesoBruto(rs.getDouble("peso_bruto"));
                    imp.setPesoLiquido(rs.getDouble("peso_liquido"));
                    imp.setDataCadastro(rs.getDate("cadastro"));
                    imp.setNcm(rs.getString("ncm"));
                    imp.setPiscofinsCstDebito(rs.getString("cst_cofins"));
                    
                    imp.setIcmsDebitoId(rs.getString("aliquota").trim());
                    imp.setIcmsDebitoForaEstadoId(imp.getIcmsDebitoId());
                    imp.setIcmsConsumidorId(imp.getIcmsDebitoId());
                    imp.setIcmsDebitoForaEstadoNfId(imp.getIcmsDebitoId());
                    imp.setIcmsCreditoId(imp.getIcmsDebitoId());
                    imp.setIcmsCreditoForaEstadoId(imp.getIcmsDebitoId());
                    
                    if(eanValidoBalanca.contains(imp.getImportId()) && 
                            imp.getEan() != null && 
                                !"".equals(imp.getEan()) && imp.getEan().length() > 6) {
                        continue;
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
        
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	p.cod_pessoa id,\n" +
                    "	p.ativo_inativo situacao,\n" +
                    "	f.dh_inclusao cadastro,\n" +
                    "	f.contato,\n" +
                    "	p.nome,\n" +
                    "	p.fantasia,\n" +
                    "	p.cpf_cnpj,\n" +
                    "	p.rg_insc,\n" +
                    "	p.bairro,\n" +
                    "	c.cep,\n" +
                    "	p.complemento,\n" +
                    "	p.endereco,\n" +
                    "	p.numero,\n" +
                    "	p.referencia,\n" +
                    "	c.nome cidade,\n" +
                    "	c.cod_ibge cidadeibge,\n" +
                    "	e.COD_ESTADO uf,\n" +
                    "	p.celular,\n" +
                    "	p.celular2,\n" +
                    "	p.fax,\n" +
                    "	p.fone,\n" +
                    "	p.obs\n" +
                    "from \n" +
                    "	pessoa p \n" +
                    "join fornecedor f on f.cod_pessoa = p.cod_pessoa\n" +
                    "left join cidade c on p.cod_cidade = c.cod_cidade\n" +
                    "LEFT JOIN estado e ON c.COD_ESTADO = e.COD_ESTADO \n" +
                    "order by \n" +
                    "	p.cod_pessoa")) {
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
                    "select \n" +
                    "	cod_produto idproduto,\n" +
                    "	cod_fornecedor idfornecedor,\n" +
                    "	referencia\n" +
                    "from \n" +
                    "	produto_referencia pr")) {
                while(rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rs.getString("idproduto"));
                    imp.setIdFornecedor(rs.getString("idfornecedor"));
                    imp.setCodigoExterno(rs.getString("referencia"));
                    
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
                    "select \n" +
                    "	p.cod_pessoa id,\n" +
                    "	p.ativo_inativo situacao,\n" +
                    "	p.DT_CADASTRO cadastro,\n" +
                    "	c.contato,\n" +
                    "	c.limite,\n" +
                    "	p.nome,\n" +
                    "	p.fantasia,\n" +
                    "	p.cpf_cnpj,\n" +
                    "	p.rg_insc,\n" +
                    "	p.bairro,\n" +
                    "	p.cep,\n" +
                    "	p.complemento,\n" +
                    "	p.endereco,\n" +
                    "	p.numero,\n" +
                    "	p.referencia,\n" +
                    "	ci.nome cidade,\n" +
                    "	p.cod_cidade,\n" +
                    "	p.celular,\n" +
                    "	p.celular2,\n" +
                    "	p.fax,\n" +
                    "	p.fone,\n" +
                    "	p.obs,\n" +
                    "	c.nome_mae,\n" +
                    "	c.nome_pai,\n" +
                    "	c.estado_civil,\n" +
                    "	p.dt_nasc\n" +
                    "from \n" +
                    "	pessoa p \n" +
                    "join cliente c on c.cod_pessoa = p.cod_pessoa\n" +
                    "left join cidade ci on p.cod_cidade = ci.cod_cidade\n" +
                    "order by \n" +
                    "	p.cod_pessoa")) {
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
        return new MasterDAO.VendaIterator(getLojaOrigem(), dataInicioVenda, dataTerminoVenda);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new MasterDAO.VendaItemIterator(getLojaOrigem(), dataInicioVenda, dataTerminoVenda);
    }
    
    private static class VendaIterator implements Iterator<VendaIMP> {

        public final static SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");

        private Statement stm;
        private ResultSet rst;
        private String sql;
        private VendaIMP next;
        private Set<String> uk = new HashSet<>();

        private void obterNext() {
            try {
                SimpleDateFormat timestampDate = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat timestamp = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaIMP();
                        String id = rst.getString("id");
                        if (!uk.add(id)) {
                            LOG.warning("Venda " + id + " já existe na listagem");
                        }
                        next.setId(id);
                        next.setNumeroCupom(Utils.stringToInt(rst.getString("documento")));
                        next.setEcf(Utils.stringToInt(rst.getString("ecf")));
                        next.setData(rst.getDate("emissao"));
                        
                        String horaInicio = rst.getString("hora");
                        
                        next.setHoraInicio(timestamp.parse(horaInicio));
                        next.setHoraTermino(next.getHoraInicio());
                        next.setSubTotalImpressora(rst.getDouble("total"));
                        next.setIdClientePreferencial(rst.getString("idcliente"));
                    }
                }
            } catch (SQLException | ParseException ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {
            this.stm = ConexaoFirebird.getConexao().createStatement();
            this.sql = "select \n" +
                    "	v.cod_venda id,\n" +
                    "	v.documento,\n" +
                    "	v.dt_emissao emissao,\n" +
                    "	v.cod_cliente idcliente,\n" +
                    "	v.vl_produto valor,\n" +
                    "	v.vl_total total,\n" +
                    "	v.dh_inclusao hora,\n" +
                    "	v.cod_sat ecf\n" +
                    "from\n" +
                    "	venda v\n" +
                    "where\n" +
                    "	v.cod_empresa = " + idLojaCliente + " and\n" +
                    "	v.dt_emissao between '" + FORMAT.format(dataInicio) + "' AND '" + FORMAT.format(dataTermino) + "' and v.sn_sat_enviado = 'S'";
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

        private Statement stm;
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
                        next.setProduto(rst.getString("idproduto"));
                        next.setDescricaoReduzida(rst.getString("descricao"));
                        next.setQuantidade(rst.getDouble("qtde"));
                        next.setTotalBruto(rst.getDouble("vl_total"));
                        
                        /*String cancelado = rst.getString("cancelado");
                        
                        if(cancelado != null && "S".equals(cancelado)) {
                            next.setCancelado(true);
                        }*/
                        
                        next.setCodigoBarras(rst.getString("ean"));
                        next.setUnidadeMedida(rst.getString("unidade"));
                        
                        String icms = rst.getString("aliq_icms");
                        
                        if(icms != null && !"".equals(icms)) {
                            switch(icms.trim()) {
                                case "4,50": next.setIcmsAliq(4.50);
                                    next.setIcmsCst(0);
                                    next.setIcmsReduzido(0);
                                    break;
                                case "7,00": next.setIcmsAliq(7);
                                    next.setIcmsCst(0);
                                    next.setIcmsReduzido(0);
                                    break;
                                case "11,00": next.setIcmsAliq(11);
                                    next.setIcmsCst(0);
                                    next.setIcmsReduzido(0);
                                    break;
                                case "11,60": next.setIcmsAliq(11.60);
                                    next.setIcmsCst(0);
                                    next.setIcmsReduzido(0);
                                    break;
                                case "12,00": next.setIcmsAliq(12);
                                    next.setIcmsCst(0);
                                    next.setIcmsReduzido(0);
                                    break;
                                case "14,76": next.setIcmsAliq(14.76);
                                    next.setIcmsCst(0);
                                    next.setIcmsReduzido(0);
                                    break;
                                case "18,00": next.setIcmsAliq(18);
                                    next.setIcmsCst(0);
                                    next.setIcmsReduzido(0);
                                    break;
                                case "F": next.setIcmsAliq(0);
                                    next.setIcmsCst(60);
                                    next.setIcmsReduzido(0);
                                    break;
                                default: next.setIcmsAliq(0);
                                    next.setIcmsCst(40);
                                    next.setIcmsReduzido(0);
                                    break;

                            }
                        }
                        
                    }
                }
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaItemIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {
            this.stm = ConexaoFirebird.getConexao().createStatement();
            this.sql = "select \n" +
                    "	vp.cod_venda_produto id,\n" +
                    "	vp.cod_venda idvenda,\n" +
                    "	vp.cod_produto idproduto,\n" +
                    "	p.descricao,\n" +
                    "	(select first 1 cod_barra from produto_codbarra where cod_produto = p.cod_produto) ean,\n" +
                    "	p.unidade,\n" +
                    "	vp.qtde,\n" +
                    "	vp.vl_desconto,\n" +
                    "	vp.vl_icms,\n" +
                    "	vp.aliq_icms,\n" +
                    "	vp.vl_unitario,\n" +
                    "	vp.vl_total,\n" +
                    "	vp.vl_total_bruto,\n" +
                    "   vp.SN_IMPRESSO cancelado\n" +
                    "from \n" +
                    "	venda_produto vp\n" +
                    "inner join venda v on vp.cod_venda = v.cod_venda\n" +
                    "inner join produto p on vp.cod_produto = p.cod_produto\n" +
                    "where" +
                    "	v.dt_emissao between '" + MasterDAO.VendaIterator.FORMAT.format(dataInicio) + "' and '" + MasterDAO.VendaIterator.FORMAT.format(dataTermino) + "' AND \n" +
                    "	v.cod_empresa = " + idLojaCliente;
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
