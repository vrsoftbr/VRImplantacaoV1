package vrimplantacao2.dao.interfaces;

import java.sql.Connection;
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
import vrimplantacao2.vo.enums.TipoEstadoCivil;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;

/**
 *
 * @author Importacao
 */
public class SolidoDAO extends InterfaceDAO implements MapaTributoProvider {

    private Connection mvcupom;
    private Connection bcodados;
    
    private static final Logger LOG = Logger.getLogger(IntelliCashDAO.class.getName());

    public Connection getMvcupom() {
        return mvcupom;
    }

    public void setMvcupom(Connection mvcupom) {
        this.mvcupom = mvcupom;
    }

    public Connection getBcodados() {
        return bcodados;
    }

    public void setBcodados(Connection bcodados) {
        this.bcodados = bcodados;
    }
    
    @Override
    public String getSistema() {
        return "Solido";
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
        
        try(Statement stm = bcodados.createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "	id_empresa_tributacao id,\n" +
                    "	nome descricao,\n" +
                    "	aliquota,\n" +
                    "	sit_tributaria cst,\n" +
                    "	reducao,\n" +
                    "	aliquota_reduzida\n" +
                    "from\n" +
                    "	empresa_tributacao")) {
                while(rs.next()) {
                    result.add(new MapaTributoIMP(rs.getString("id"), 
                                                rs.getString("descricao"), 
                                                rs.getInt("cst"), 
                                                rs.getDouble("aliquota"), 
                                                rs.getDouble("reducao")));
                }
            }
        }
        return result;
    }
    
    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> lojas = new ArrayList<>();
        try (Statement stm = bcodados.createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n" +
                    "	id_empresa id,\n" +
                    "	NOME_FANTASIA fantasia\n" +
                    "FROM\n" +
                    "	EMPRESA"
            )) {
                while (rs.next()) {
                    lojas.add(new Estabelecimento(rs.getString("id"), rs.getString("fantasia")));
                }
            }
        }
        return lojas;
    }
    
    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();

        try (Statement stm = bcodados.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	id_produto_familia id,\n" +
                    "	nome descricao\n" +
                    "from\n" +
                    "	produto_familia"
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setDescricao(rst.getString("descricao"));
                    
                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        try(Statement stm = bcodados.createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	p.id_produto id,\n" +
                    "	ean.codigo_pdv ean,\n" +
                    "	p.produto descricaocompleta,\n" +
                    "	p.descricao_pdv,\n" +
                    "	p.data_cadastro,\n" +
                    "	p.id_produto_familia idfamilia,\n" +
                    "	pl.estoque,\n" +
                    "	pl.estoque_minimo,\n" +
                    "	pl.troca,\n" +
                    "	pl.preco_medio,\n" +
                    "	pl.preco_compra,\n" +
                    "	pl.preco_custo,\n" +
                    "	pl.preco_venda,\n" +
                    "	pl.preco_nota, \n" +
                    "	pl.margem,\n" +
                    "	p.embalagem,\n" +
                    "	p.unidade,\n" +
                    "	p.unidade_baixa,\n" +
                    "	p.peso_unidade,\n" +
                    "	p.validade,\n" +
                    "	p.produto_ativo,\n" +
                    "	pl.produto_liberado,\n" +
                    "	p.produto_ativo_compra,\n" +
                    "	p.produto_balanca,\n" +
                    "	pct.ncm,\n" +
                    "	pct.cest,\n" +
                    "	pe.codigo piscredito,\n" +
                    "	ps.codigo pisdebito,\n" +
                    "	pl.id_empresa_tributacao idaliquota,\n" +
                    "	p.peso_bruto,\n" +
                    "	p.peso_liquido \n" +
                    "from \n" +
                    "	produto p\n" +
                    "inner join produto_codigo_pdv ean on p.id_produto = ean.id_produto\n" +
                    "inner join produto_loja pl on p.id_produto = pl.id_produto\n" +
                    "left join pis_entrada pe on p.id_pis_entrada = pe.id_pis_entrada\n" +
                    "left join pis_saida ps on p.id_pis_saida = ps.id_pis_saida \n" +
                    "left join produto_carga_tributaria pct on p.id_produto = pct.id_produto\n" +        
                    "where \n" +
                    "	pl.id_empresa = " + getLojaOrigem())) {
                while(rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("id"));
                    imp.setEan(rs.getString("ean"));
                    imp.seteBalanca("S".equals(rs.getString("produto_balanca")));
                    imp.setDescricaoCompleta(Utils.acertarTexto(rs.getString("descricaocompleta")));
                    imp.setDescricaoReduzida(Utils.acertarTexto(rs.getString("descricao_pdv")));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setDataCadastro(rs.getDate("data_cadastro"));
                    imp.setIdFamiliaProduto(rs.getString("idfamilia"));
                    imp.setEstoque(rs.getDouble("estoque"));
                    imp.setPesoBruto(rs.getDouble("peso_bruto"));
                    imp.setPesoLiquido(rs.getDouble("peso_liquido"));
                    imp.setEstoqueMinimo(rs.getDouble("estoque_minimo"));
                    imp.setPrecovenda(rs.getDouble("preco_venda"));
                    imp.setCustoComImposto(rs.getDouble("preco_custo"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setMargem(rs.getDouble("margem"));
                    imp.setTipoEmbalagem(rs.getString("embalagem"));
                    imp.setQtdEmbalagemCotacao(rs.getInt("unidade"));
                    imp.setQtdEmbalagem(rs.getInt("unidade_baixa"));
                    imp.setSituacaoCadastro("N".equals(rs.getString("produto_ativo"))
                            ? SituacaoCadastro.EXCLUIDO : SituacaoCadastro.ATIVO);
                    imp.setNcm(rs.getString("ncm"));
                    imp.setCest(rs.getString("cest"));
                    imp.setPiscofinsCstCredito(rs.getString("piscredito"));
                    imp.setPiscofinsCstDebito(rs.getString("pisdebito"));
                    imp.setIcmsDebitoId(rs.getString("idaliquota"));
                    imp.setIcmsDebitoForaEstadoNfId(imp.getIcmsDebitoId());
                    imp.setIcmsConsumidorId(imp.getIcmsDebitoId());
                    imp.setIcmsCreditoId(imp.getIcmsDebitoId());
                    imp.setIcmsCreditoForaEstadoId(imp.getIcmsDebitoId());
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        
        try(Statement stm = bcodados.createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	f.id_fornecedor id,\n" +
                    "	f.nome,\n" +
                    "	f.fantasia,\n" +
                    "	f.endereco,\n" +
                    "	f.numero,\n" +
                    "	f.bairro,\n" +
                    "	f.cidade,\n" +
                    "	f.estado,\n" +
                    "	f.cep,\n" +
                    "	f.cnpj,\n" +
                    "	f.inscricao_estadual,\n" +
                    "	f.e_mail,\n" +
                    "	f.observacao,\n" +
                    "	f.fornecedor_ativo,\n" +
                    "	f.data_cadastro\n" +
                    "from \n" +
                    "	fornecedor f where f.id_empresa = " + getLojaOrigem())) {
                while(rs.next()) {  
                    FornecedorIMP imp = new FornecedorIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("id"));
                    imp.setRazao(Utils.acertarTexto(rs.getString("nome")));
                    imp.setCnpj_cpf(rs.getString("cnpj"));
                    imp.setIe_rg(rs.getString("inscricao_estadual"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("estado"));
                    imp.setCep(rs.getString("cep"));
                    imp.setObservacao(rs.getString("observacao"));
                    imp.setDatacadastro(rs.getDate("data_cadastro"));
                    imp.setAtivo("S".equals(rs.getString("fornecedor_ativo")));
                    
                    String emailTelefone = rs.getString("e_mail");
                    
                    if(emailTelefone != null && !"".equals(emailTelefone)) {
                        imp.addContato("A", "EMAIL", null, null, TipoContato.NFE, emailTelefone);
                    }
                    
                    int i = 1;
                   
                    try(ResultSet rs1 = stm.executeQuery(
                            "select\n" +
                            "	ft.id_fornecedor idfornecedor,\n" +
                            "	ft.ddd || '' || ft.telefone telefone,\n" +
                            "	ft.contato,\n" +
                            "	ft.e_mail \n" +
                            "from\n" +
                            "	fornecedor_telefone ft where ft.id_fornecedor = " + imp.getImportId())) {
                        while(rs1.next()) {
                            String contato = rs1.getString("contato"),
                                    telefone = rs1.getString("telefone");
                            
                            imp.addContato(String.valueOf(i), contato == null ? "SEM CONTATO" : contato, telefone, null, TipoContato.NFE, rs1.getString("e_mail"));
                            i++;
                        }
                    }
                    
                    try(ResultSet rs2 = stm.executeQuery(
                            "select \n" +
                            "	fv.id_fornecedor,\n" +
                            "	v.telefone1,\n" +
                            "	v.nome contato,\n" +
                            "	v.e_mail \n" +
                            "from \n" +
                            "	fornecedor_vendedores fv\n" +
                            "inner join vendedores v on fv.id_vendedor = v.id_vendedor WHERE fv.id_fornecedor =" + imp.getImportId())) {
                        while(rs2.next()) {
                            String contato = rs2.getString("contato"),
                                    telefone = rs2.getString("telefone1");
                            
                            imp.addContato(String.valueOf(i), contato == null ? "SEM CONTATO" : contato, telefone, null, TipoContato.NFE, rs2.getString("e_mail"));
                            i++;
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
        
        try(Statement stm = bcodados.createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "	pc.id_fornecedor,\n" +
                    "	pc.id_produto,\n" +
                    "	pc.id_produto_codfornecedor id,\n" +
                    "	pc.codigo_fornecedor,\n" +
                    "	coalesce(pc.unidade_baixa, 1) qtdembalagem\n" +
                    "from 	\n" +
                    "	produto_codfornecedor pc\n" +
                    "order by\n" +
                    "	id_fornecedor, id_produto")) {
                while(rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rs.getString("id_produto"));
                    imp.setIdFornecedor(rs.getString("id_fornecedor"));
                    imp.setCodigoExterno(rs.getString("codigo_fornecedor"));
                    imp.setQtdEmbalagem(rs.getDouble("qtdembalagem"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        
        try(Statement stm = bcodados.createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "	c.id_cliente id,\n" +
                    "	c.nome,\n" +
                    "	c.endereco,\n" +
                    "	c.bairro,\n" +
                    "	c.cidade,\n" +
                    "	c.estado,\n" +
                    "	c.numero,\n" +
                    "	c.cep,\n" +
                    "	c.rg,\n" +
                    "	c.cnpj_cpf,\n" +
                    "	c.inscricao_estadual,\n" +
                    "	c.data_cadastro,\n" +
                    "	c.data_nascimento,\n" +
                    "	c.e_mail,\n" +
                    "	c.nome_mae,\n" +
                    "	c.nome_pai,\n" +
                    "	c.salario,\n" +
                    "	c.limite_compra,\n" +
                    "	c.bloqueado,\n" +
                    "	c.cliente_ativo,\n" +
                    "	c.estado_civil,\n" +
                    "	c.id_cliente_grupo,\n" +
                    "	cg.nome grupo,\n" +
                    "	c.id_cliente_status,\n" +
                    "	cv.dia_vencimento\n" +
                    "from\n" +
                    "	cliente c\n" +
                    "left join cliente_vencimento cv\n" +
                    "	on c.id_cliente_vencimento = cv.id_cliente_vencimento\n" +
                    "left join cliente_grupo cg\n" +
                    "	on c.id_cliente_grupo = cg.id_cliente_grupo\n" +
                    "where\n" +
                    "	c.id_empresa = " + getLojaOrigem())) {
                while(rs.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    
                    imp.setId(rs.getString("id"));
                    imp.setRazao(Utils.acertarTexto(rs.getString("nome")));
                    imp.setCnpj(rs.getString("cnpj_cpf"));
                    String rg = rs.getString("rg"), ie = rs.getString("inscricao_estadual");
                    
                    imp.setInscricaoestadual(ie);
                    
                    if(rg != null && !"".equals(rg)) {
                        imp.setInscricaoestadual(rg);
                    }
                    
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setBairro(Utils.acertarTexto(rs.getString("bairro")));
                    imp.setMunicipio(Utils.acertarTexto(rs.getString("cidade")));
                    imp.setUf(rs.getString("estado"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setCep(rs.getString("cep"));
                    imp.setDataCadastro(rs.getDate("data_cadastro"));
                    imp.setDataNascimento(rs.getDate("data_nascimento"));
                    imp.setEmail(rs.getString("e_mail"));
                    imp.setNomeMae(rs.getString("nome_mae"));
                    imp.setNomePai(rs.getString("nome_pai"));
                    imp.setSalario(rs.getDouble("salario"));
                    imp.setValorLimite(rs.getDouble("limite_compra"));
                    imp.setAtivo("S".equals(rs.getString("cliente_ativo")));
                    imp.setBloqueado("S".equals(rs.getString("bloqueado")));
                    imp.setDiaVencimento(rs.getInt("dia_vencimento"));
                    
                    String estCivil = rs.getString("estado_civil");
                    
                    if(estCivil != null && !"".equals(estCivil)) {
                        switch(Utils.acertarTexto(estCivil.toUpperCase().trim())) {
                            case "CASADO": imp.setEstadoCivil(TipoEstadoCivil.CASADO);
                                break;
                            case "AMASIADO": imp.setEstadoCivil(TipoEstadoCivil.AMAZIADO);
                                break;  
                            case "DIVORCIADO": imp.setEstadoCivil(TipoEstadoCivil.DIVORCIADO);
                                break;    
                            case "VIUVO": imp.setEstadoCivil(TipoEstadoCivil.VIUVO);
                                break;    
                            default:
                                imp.setEstadoCivil(TipoEstadoCivil.SOLTEIRO); break;
                        }
                    }
                    
                    int i = 1;
                    try(ResultSet rs2 = stm.executeQuery(
                            "SELECT \n" +
                            "	id_cliente,\n" +
                            "	ddd,\n" +
                            "	telefone,\n" +
                            "	contato\n" +
                            "FROM \n" +
                            "	CLIENTE_TELEFONE where id_cliente = " + imp.getId())) {
                        while(rs2.next()) {
                            
                            String nomeCont = rs2.getString("contato");
                            
                            if(nomeCont == null || "".equals(nomeCont)) {
                                nomeCont = "SEM CONTATO";
                            }
                            
                            imp.addContato(String.valueOf(i), 
                                    nomeCont,
                                    rs2.getString("ddd") + rs2.getString("telefone"), 
                                    null, null);
                            i++;
                        }
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
        
        try(Statement stm = bcodados.createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select  \n" +
                    "	rv.id_receber_vale id, \n" +
                    "	rv.id_cliente idcliente, \n" +
                    "	rv.data_compra emissao, \n" +
                    "	rv.numero_caixa ecf, \n" +
                    "	rv.numero_cupom coo, \n" +
                    "	rv.valor - coalesce(rv.valor_pago, 0) valor,\n" +
                    "	rv.vencimento, \n" +
                    "	rv.observacao,\n" +
                    "	rv.codigo_operador,\n" +
                    "	o2.nome operador,\n" +
                    "	rv.id_usuario,\n" +
                    "	u2.nome usuario\n" +
                    "from \n" +
                    "	receber_vale rv \n" +
                    "left join operador o2 on rv.codigo_operador = o2.codigo_operador\n" +
                    "left join usuario u2 on rv.id_usuario = u2.id_usuario\n" +
                    "where\n" +
                    "	rv.id_empresa = " + getLojaOrigem() + " and\n" +
                    "	rv.historico = 'I'")) {
                while(rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    
                    imp.setId(rs.getString("id"));
                    imp.setIdCliente(rs.getString("idcliente"));
                    imp.setDataEmissao(rs.getDate("emissao"));
                    imp.setDataVencimento(rs.getDate("vencimento"));
                    imp.setEcf(rs.getString("ecf"));
                    imp.setNumeroCupom(rs.getString("coo"));
                    imp.setValor(rs.getDouble("valor"));
                    
                    String dados = String.format("Operador: %s, Usuario: %s", 
                            rs.getString("operador") == null ? "" : rs.getString("operador"),
                            rs.getString("usuario") == null ? "" : rs.getString("usuario"));
                    
                    imp.setObservacao(rs.getString("observacao") + " " + dados);
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ChequeIMP> getCheques() throws Exception {
        List<ChequeIMP> result = new ArrayList<>();
        
        try(Statement stm = bcodados.createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "	rc.id_receber_cheque id,\n" +
                    "	rc.id_cliente,\n" +
                    "	rc.data_compra emissao,\n" +
                    "	rc.vencimento,\n" +
                    "	rc.cpf_cheque, \n" +
                    "	c.CNPJ_CPF,\n" +
                    "	rc.nome_cheque,\n" +
                    "	c.NOME,\n" +
                    "	c.RG,\n" +
                    "	c.INSCRICAO_ESTADUAL,\n" +
                    "	rc.numero_caixa ecf,\n" +
                    "	rc.numero_cupom coo,\n" +
                    "	rc.numero_cheque cheque,\n" +
                    "	rc.agencia,\n" +
                    "	rc.id_banco,\n" +
                    "	rc.conta,\n" +
                    "	rc.valor,\n" +
                    "	rc.juros,\n" +
                    "	rc.desconto\n" +
                    "from\n" +
                    "	receber_cheque rc \n" +
                    "INNER JOIN cliente c ON rc.ID_CLIENTE = c.ID_CLIENTE\n" +
                    "where\n" +
                    "	rc.id_empresa = " + getLojaOrigem() + " and\n" +
                    "	rc.historico = 'I'")) {
                while(rs.next()) {
                    ChequeIMP imp = new ChequeIMP();
                    
                    imp.setId(rs.getString("id"));
                    imp.setNome(rs.getString("nome"));
                    imp.setCpf(rs.getString("cpf_cheque"));
                    
                    String rg = rs.getString("rg");
                    
                    imp.setRg(rs.getString("INSCRICAO_ESTADUAL"));
                    
                    if(rg != null && !"".equals(rg)) {
                        imp.setRg(rg);
                    }
                    
                    imp.setDate(rs.getDate("emissao"));
                    imp.setDataDeposito(rs.getDate("vencimento"));
                    imp.setNumeroCheque(rs.getString("cheque"));
                    imp.setNumeroCupom(rs.getString("coo"));
                    imp.setEcf(rs.getString("ecf"));
                    imp.setValor(rs.getDouble("valor"));
                    imp.setAgencia(rs.getString("agencia"));
                    imp.setBanco(rs.getInt("id_banco"));
                    imp.setConta(rs.getString("conta"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ContaPagarIMP> getContasPagar() throws Exception {
        List<ContaPagarIMP> result = new ArrayList<>();
        
        try(Statement stm = bcodados.createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	pf.id_pagar_financeiro id,\n" +
                    "	p.id_fornecedor idfornecedor,\n" +
                    "	p.nota,\n" +
                    "	p.data_lancamento lancamento,\n" +
                    "	p.data_emissao emissao,\n" +
                    "	pf.vencimento,\n" +
                    "	pf.descontos,\n" +
                    "	p.valor_nota valor,\n" +
                    "	pf.duplicata parcela,\n" +
                    "	pf.valor valorparcela,\n" +
                    "	pf.observacao\n" +
                    "from \n" +
                    "	pagar p\n" +
                    "inner join pagar_financeiro pf on p.id_pagar = pf.id_pagar \n" +
                    "where \n" +
                    "	pf.historico = 'I' and \n" +
                    "	p.id_empresa = " + getLojaOrigem())) {
                while(rs.next()) {
                    ContaPagarIMP imp = new ContaPagarIMP();
                    
                    imp.setId(rs.getString("id"));
                    imp.setIdFornecedor(rs.getString("idfornecedor"));
                    imp.setNumeroDocumento(rs.getString("nota"));
                    imp.setDataEmissao(rs.getDate("emissao"));
                    imp.setDataEntrada(rs.getDate("lancamento"));
                    imp.setObservacao(rs.getString("observacao"));
                    
                    imp.addVencimento(rs.getDate("vencimento"), rs.getDouble("valorparcela"));
                    
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
        return new SolidoDAO.VendaIterator(getLojaOrigem(), dataInicioVenda, dataTerminoVenda, mvcupom);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new SolidoDAO.VendaItemIterator(getLojaOrigem(), dataInicioVenda, dataTerminoVenda, mvcupom);
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
                SimpleDateFormat timestamp = new SimpleDateFormat("hh:mm");
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaIMP();
                        String id = rst.getString("id");
                        if (!uk.add(id)) {
                            LOG.warning("Venda " + id + " já existe na listagem");
                        }
                        next.setId(id);
                        next.setNumeroCupom(Utils.stringToInt(rst.getString("coo")));
                        next.setEcf(Utils.stringToInt(rst.getString("ecf")));
                        next.setData(rst.getDate("data"));
                        String horaInicio = rst.getString("hora");
                        next.setHoraInicio(timestamp.parse(horaInicio));
                        next.setHoraTermino(next.getHoraInicio());
                        next.setSubTotalImpressora(rst.getDouble("valor"));
                    }
                }
            } catch (SQLException | ParseException ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaIterator(String idLojaCliente, Date dataInicio, Date dataTermino, Connection con) throws Exception {
            this.stm = con.createStatement();
            this.sql
                    = "SELECT\n" +
                    "	c.id_mvcupom id,\n" +
                    "	c.data,\n" +
                    "	c.hora,\n" +
                    "	c.maquina ecf,\n" +
                    "	c.numero_cupom coo,\n" +
                    "	c.total_venda valor,\n" +
                    "	c.cancelado,\n" +
                    "	c.chave_acesso,\n" +
                    "	c.id_cliente_identificado idcliente\n" +
                    "from \n" +
                    "	mvcupom c\n" +
                    "INNER JOIN\n" +
                    "	(SELECT \n" +
                    "		max(id_mvcupom) id,\n" +
                    "		NUMERO_CUPOM,\n" +
                    "           data\n" +
                    "	FROM \n" +
                    "		mvcupom\n" +
                    "	GROUP BY\n" +
                    "		NUMERO_CUPOM, data) maxid ON c.ID_MVCUPOM = maxid.id AND\n" +
                    "		c.NUMERO_CUPOM = maxid.numero_cupom and c.data = maxid.data\n" +
                    "WHERE\n" +
                    "	c.ID_EMPRESA = " + idLojaCliente + " AND\n" +
                    "	c.DATA BETWEEN '" + FORMAT.format(dataInicio) + "' AND '" + FORMAT.format(dataTermino) + "'";
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
                        next.setProduto(rst.getString("id_produto"));
                        next.setDescricaoReduzida(rst.getString("descricao"));
                        next.setSequencia(rst.getInt("seq"));
                        next.setQuantidade(rst.getDouble("quantidade"));
                        next.setTotalBruto(rst.getDouble("VALOR_TOTAL"));
                        
                        String cancelado = rst.getString("cancelado");
                        
                        if(cancelado != null && "S".equals(cancelado)) {
                            next.setCancelado(true);
                        }
                        
                        next.setCodigoBarras(rst.getString("ean"));
                        next.setUnidadeMedida(rst.getString("embalagem"));
                        next.setIdAliquota(rst.getInt("idaliquota"));
                        
                    }
                }
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaItemIterator(String idLojaCliente, Date dataInicio, Date dataTermino, Connection con) throws Exception {
            this.stm = con.createStatement();
            this.sql = "select \n" +
                    "	i.ID_MVCUPOM_ITENS id,\n" +
                    "	i.ID_MVCUPOM idvenda,\n" +
                    "	i.ID_PRODUTO,\n" +
                    "	i.CODIGO ean,\n" +
                    "	i.PRODUTO descricao,\n" +
                    "	i.NUMERO_ORDEM seq,\n" +
                    "	i.EMBALAGEM,\n" +
                    "	i.QUANTIDADE,\n" +
                    "	i.VALOR_TOTAL,\n" +
                    "	i.DESCONTO,\n" +
                    "	i.ALIQUOTA,\n" +
                    "	i.ID_EMPRESA_TRIBUTACAO idaliquota,\n" +
                    "	i.CANCELADO\n" +
                    "from\n" +
                    "	mvcupom_itens i\n" +
                    "join mvcupom c on i.id_mvcupom = c.id_mvcupom\n" +
                    "where\n" +
                    "	c.data between '" + VendaIterator.FORMAT.format(dataInicio) + "' and '" + VendaIterator.FORMAT.format(dataTermino) + "' AND \n" +
                    "	c.ID_EMPRESA = " + idLojaCliente;
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
