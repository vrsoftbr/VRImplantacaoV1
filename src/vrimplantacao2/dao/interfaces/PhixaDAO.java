package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.OfertaIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Importacao
 */
public class PhixaDAO extends InterfaceDAO {

    public boolean importarFuncionario = false;
    
    @Override
    public String getSistema() {
        return "Phixa";
    }
    
    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                new OpcaoProduto[]{
                    OpcaoProduto.MERCADOLOGICO_PRODUTO,
                    OpcaoProduto.MERCADOLOGICO_POR_NIVEL,
                    OpcaoProduto.MERCADOLOGICO,
                    OpcaoProduto.MANTER_CODIGO_MERCADOLOGICO,
                    OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
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
                    OpcaoProduto.OFERTA,
                    OpcaoProduto.MAPA_TRIBUTACAO,
                    OpcaoProduto.FABRICANTE,
                    OpcaoProduto.ASSOCIADO,
                    OpcaoProduto.COMPRADOR,
                    OpcaoProduto.COMPRADOR_PRODUTO,
                    OpcaoProduto.RECEITA,
                    OpcaoProduto.RECEITA_BALANCA,
                    OpcaoProduto.NUMERO_PARCELA,
                    OpcaoProduto.TECLA_ASSOCIADA
                }
        ));
    }
    
    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT \n" +
                    "	codigo_loja id,\n" +
                    "	nome_fantasia_loja fantasia\n" +
                    "FROM \n" +
                    "	LOJA"
            )) {
                while (rs.next()) {
                    result.add(new Estabelecimento(rs.getString("id"), rs.getString("fantasia")));
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	pr.codigo_produto id,\n" +
                    "	pr.descricao_produto,\n" +
                    "	pr.descricao_consulta_produto,\n" +
                    "	pr.descricao_curta_produto,\n" +
                    "	pr.descricao_longa_produto,\n" +
                    "	pr.pesavel_produto balanca,\n" +
                    "	pr.codigo_barra_produto ean,\n" +
                    "	pr.referencia_fornecedor_produto referencia,\n" +
                    "	pr.data_cadastro_produto cadastro,\n" +
                    "	UNIDADE.descricao_unidade,\n" +
                    "	UNIDADE.quantidade_unidade,\n" +
                    "	pr.familia_produto,\n" +
                    "	pr.status_produto situacaocadastro,\n" +
                    "	pr.peso_bruto_produto,\n" +
                    "	pr.peso_liquido_produto,\n" +
                    "	el.estoque_el estoque,\n" +
                    "	el.estoque_maximo_el estoquemax,\n" +
                    "	el.estoque_minimo_el estoquemin,\n" +
                    "	pr.preco_custo_produto custocomimposto,\n" +
                    "	pr.preco_custo_liquido_produto custosemimposto,\n" +
                    "	pr.custo_medio_produto customedio,\n" +
                    "	pr.custo_contabil_produto custocontabil,\n" +
                    "	pr.preco1_produto precovenda,\n" +
                    "	round(pr.lucro1_produto, 2) margem,\n" +
                    "	pr.classificacao_fiscal_produto ncm,\n" +
                    "	cf.cest_cf cest,\n" +
                    "	pr.cst_cofins_produto,\n" +
                    "	pr.cst_pis_produto,\n" +
                    "	cf.codigo_cf,\n" +
                    "	cf.classificacao_fiscal_cf,\n" +
                    "	cf.descricao_cf,\n" +
                    "	pr.situacao_tributaria_produto cst,\n" +
                    "	cf.substituicao_tributaria_cf,\n" +
                    "	cf.imposto_importacao_cf,\n" +
                    "	cf.icms_reducao_importacao_cf icms_reducao,\n" +
                    "	cf.aliquota_icms_padrao_cf icms_debito,\n" +
                    "	cf.mva_cf,\n" +
                    "	cf.credito_presumido_cf,\n" +
                    "	cf.mvast_cf\n" +
                    "from\n" +
                    "	produto pr\n" +
                    "join estoque_loja el on pr.codigo_produto = el.produto_el\n" +
                    "left join UNIDADE on\n" +
                    "	(pr.unidade_produto = UNIDADE.codigo_unidade)\n" +
                    "left join CLASSIFICACAO_FISCAL cf on\n" +
                    "	(pr.classificacao_fiscal_produto = cf.codigo_cf)\n" +
                    "where\n" +
                    "	el.loja_el = " + getLojaOrigem() + "\n" +
                    "order by \n" +
                    "	pr.codigo_produto")) {
                while(rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("id"));
                    imp.setDescricaoCompleta(rs.getString("descricao_produto"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setEan(rs.getString("ean"));
                    imp.setTipoEmbalagem(rs.getString("descricao_unidade"));
                    
                    if(rs.getString("balanca") != null 
                            && !"".equals(rs.getString("balanca"))
                                && "1".equals(rs.getString("balanca").trim())) {
                        imp.setEan(rs.getString("referencia"));
                        imp.seteBalanca(true);
                    } else if(rs.getString("ean") == null || "".equals(rs.getString("ean").trim())) {
                        if(rs.getString("referencia") != null && !"".equals(rs.getString("referencia"))) {
                            if(rs.getString("referencia").length() < 7) {
                                imp.setEan(rs.getString("referencia"));
                                imp.setManterEAN(true);
                            }
                        }
                    }
                    
                    imp.setSituacaoCadastro(rs.getInt("situacaocadastro"));
                    imp.setDataCadastro(rs.getDate("cadastro"));
                    imp.setPesoBruto(rs.getDouble("peso_bruto_produto"));
                    imp.setPesoLiquido(rs.getDouble("peso_liquido_produto"));
                    imp.setEstoque(rs.getDouble("estoque"));
                    imp.setEstoqueMaximo(rs.getDouble("estoquemax"));
                    imp.setEstoqueMinimo(rs.getDouble("estoquemin"));
                    imp.setCustoComImposto(rs.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rs.getDouble("custosemimposto"));
                    imp.setCustoMedio(rs.getDouble("customedio"));
                    imp.setPrecovenda(rs.getDouble("precovenda"));
                    imp.setMargem(rs.getDouble("margem"));
                    imp.setNcm(rs.getString("ncm"));
                    imp.setCest(rs.getString("cest"));
                    imp.setPiscofinsCstDebito(rs.getString("cst_cofins_produto"));
                    
                    int cst;
                    double reducao, icms;
                    
                    cst = Utils.stringToInt(rs.getString("cst"), 0);
                    reducao = Utils.stringToDouble(rs.getString("icms_reducao"));
                    icms = Utils.stringToDouble(rs.getString("icms_debito"));
                    
                    if(cst == 40) {
                        imp.setIcmsAliqConsumidor(0);
                        imp.setIcmsReducaoConsumidor(0);
                        imp.setIcmsCstConsumidor(40);
                        
                        imp.setIcmsAliqSaida(imp.getIcmsAliqConsumidor());
                        imp.setIcmsReducaoSaida(imp.getIcmsReducaoSaida());
                        imp.setIcmsCstSaida(imp.getIcmsCstConsumidor());
                        
                        imp.setIcmsAliqEntrada(imp.getIcmsAliqConsumidor());
                        imp.setIcmsReducaoEntrada(imp.getIcmsReducaoEntrada());
                        imp.setIcmsCstEntrada(imp.getIcmsCstConsumidor());
                    } else if(cst == 60) {
                        imp.setIcmsAliqConsumidor(0);
                        imp.setIcmsReducaoConsumidor(0);
                        imp.setIcmsCstConsumidor(60);
                        
                        imp.setIcmsAliqSaida(imp.getIcmsAliqConsumidor());
                        imp.setIcmsReducaoSaida(imp.getIcmsReducaoSaida());
                        imp.setIcmsCstSaida(imp.getIcmsCstConsumidor());
                        
                        imp.setIcmsAliqEntrada(imp.getIcmsAliqConsumidor());
                        imp.setIcmsReducaoEntrada(imp.getIcmsReducaoEntrada());
                        imp.setIcmsCstEntrada(imp.getIcmsCstConsumidor());
                    } else if(cst == 20){
                        imp.setIcmsAliqConsumidor(icms);
                        imp.setIcmsReducaoConsumidor(reducao);
                        imp.setIcmsCstConsumidor(20);
                        
                        imp.setIcmsAliqSaida(imp.getIcmsAliqConsumidor());
                        imp.setIcmsReducaoSaida(imp.getIcmsReducaoSaida());
                        imp.setIcmsCstSaida(imp.getIcmsCstConsumidor());
                        
                        imp.setIcmsAliqEntrada(imp.getIcmsAliqConsumidor());
                        imp.setIcmsReducaoEntrada(imp.getIcmsReducaoEntrada());
                        imp.setIcmsCstEntrada(imp.getIcmsCstConsumidor());
                    } else if(cst == 0) {
                        imp.setIcmsAliqConsumidor(icms);
                        imp.setIcmsReducaoConsumidor(reducao);
                        imp.setIcmsCstConsumidor(0);
                        
                        imp.setIcmsAliqSaida(imp.getIcmsAliqConsumidor());
                        imp.setIcmsReducaoSaida(imp.getIcmsReducaoSaida());
                        imp.setIcmsCstSaida(imp.getIcmsCstConsumidor());
                        
                        imp.setIcmsAliqEntrada(imp.getIcmsAliqConsumidor());
                        imp.setIcmsReducaoEntrada(imp.getIcmsReducaoEntrada());
                        imp.setIcmsCstEntrada(imp.getIcmsCstConsumidor());
                    } else if(cst == 10) {
                        imp.setIcmsAliqConsumidor(0);
                        imp.setIcmsReducaoConsumidor(0);
                        imp.setIcmsCstConsumidor(60);
                        
                        imp.setIcmsAliqSaida(imp.getIcmsAliqConsumidor());
                        imp.setIcmsReducaoSaida(imp.getIcmsReducaoSaida());
                        imp.setIcmsCstSaida(imp.getIcmsCstConsumidor());
                        
                        imp.setIcmsAliqEntrada(imp.getIcmsAliqConsumidor());
                        imp.setIcmsReducaoEntrada(imp.getIcmsReducaoEntrada());
                        imp.setIcmsCstEntrada(imp.getIcmsCstConsumidor());
                    } else if (cst == 41) {
                        imp.setIcmsAliqConsumidor(0);
                        imp.setIcmsReducaoConsumidor(0);
                        imp.setIcmsCstConsumidor(41);
                        
                        imp.setIcmsAliqSaida(imp.getIcmsAliqConsumidor());
                        imp.setIcmsReducaoSaida(imp.getIcmsReducaoSaida());
                        imp.setIcmsCstSaida(imp.getIcmsCstConsumidor());
                        
                        imp.setIcmsAliqEntrada(imp.getIcmsAliqConsumidor());
                        imp.setIcmsReducaoEntrada(imp.getIcmsReducaoEntrada());
                        imp.setIcmsCstEntrada(imp.getIcmsCstConsumidor());
                    } else if (cst == 50){
                        imp.setIcmsAliqConsumidor(0);
                        imp.setIcmsReducaoConsumidor(0);
                        imp.setIcmsCstConsumidor(50);
                        
                        imp.setIcmsAliqSaida(imp.getIcmsAliqConsumidor());
                        imp.setIcmsReducaoSaida(imp.getIcmsReducaoSaida());
                        imp.setIcmsCstSaida(imp.getIcmsCstConsumidor());
                        
                        imp.setIcmsAliqEntrada(imp.getIcmsAliqConsumidor());
                        imp.setIcmsReducaoEntrada(imp.getIcmsReducaoEntrada());
                        imp.setIcmsCstEntrada(imp.getIcmsCstConsumidor());
                    } else if (cst == 90){
                        imp.setIcmsAliqConsumidor(0);
                        imp.setIcmsReducaoConsumidor(0);
                        imp.setIcmsCstConsumidor(90);
                        
                        imp.setIcmsAliqSaida(imp.getIcmsAliqConsumidor());
                        imp.setIcmsReducaoSaida(imp.getIcmsReducaoSaida());
                        imp.setIcmsCstSaida(imp.getIcmsCstConsumidor());
                        
                        imp.setIcmsAliqEntrada(imp.getIcmsAliqConsumidor());
                        imp.setIcmsReducaoEntrada(imp.getIcmsReducaoEntrada());
                        imp.setIcmsCstEntrada(imp.getIcmsCstConsumidor());
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
        
        try(Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	codigo_produto id,\n" +
                    "	codigo_barra_produto ean,\n" +
                    "	u.descricao_unidade unidade\n" +
                    "from\n" +
                    "	produto p\n" +
                    "left join unidade u on p.unidade_produto = u.codigo_unidade\n" +
                    "where \n" +
                    "	codigo_barra_produto != ''")) {
                while(rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("id"));
                    imp.setEan(rs.getString("ean"));
                    imp.setTipoEmbalagem(rs.getString("unidade"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	produto_rpf idproduto,\n" +
                    "	fornecedor_rpf idfornecedor,\n" +
                    "	referencia_rpf codigoexterno,\n" +
                    "	fator_entrada_rpf qtdembalagem \n" +
                    "from \n" +
                    "	REF_FORNECEDOR_PRODUTO")) {
                while(rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setIdProduto(rs.getString("idproduto"));
                    imp.setIdFornecedor(rs.getString("idfornecedor"));
                    imp.setCodigoExterno(rs.getString("codigoexterno"));
                    imp.setQtdEmbalagem(rs.getDouble("qtdembalagem"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	f.codigo_fornecedor id,\n" +
                    "	f.razao_social_fornecedor razao,\n" +
                    "	f.nome_fantasia_fornecedor fantasia,\n" +
                    "	f.cnpj_fornecedor cnpj,\n" +
                    "	f.inscricao_fornecedor ie,\n" +
                    "	f.telefone_fornecedor telefone,\n" +
                    "	f.fax_fornecedor fax,\n" +
                    "	f.email_fornecedor email,\n" +
                    "	f.site_fornecedor site,\n" +
                    "	f.data_cadastro_fornecedor cadastro,\n" +
                    "	f.status_fornecedor situacao,\n" +
                    "	e.rua endereco,\n" +
                    "	e.numero,\n" +
                    "	e.complemento,\n" +
                    "	e.cep,\n" +
                    "	e.bairro,\n" +
                    "	e.cidade,\n" +
                    "	e.estado,\n" +
                    "	e.ponto_referencia,\n" +
                    "	cont.nome_contato,\n" +
                    "	cont.cargo_contato\n" +
                    "from \n" +
                    "	fornecedor f\n" +
                    "left join endereco e on f.endereco_fornecedor = e.codigo_endereco\n" +
                    "left join contato_fornecedor cont on f.codigo_fornecedor = cont.codigo_fornecedor\n" +
                    "where \n" +
                    "	f.codigo_fornecedor > 0")) {
                while(rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setCnpj_cpf(rs.getString("cnpj"));
                    imp.setIe_rg(rs.getString("ie"));
                    
                    String tel = rs.getString("telefone"), 
                            email = rs.getString("email"),
                            contato = rs.getString("nome_contato");
                    
                    if(tel != null && !"".equals(tel)) {
                        String tels[] = new String[3];
                        tels = tel.split("/");
                        for(int i = 0; i < tels.length; i++) {
                            if(email != null && !"".equals("email")) {
                                imp.addContato(String.valueOf(i), "TELEFONE" + i+1, tels[i].trim(), null, TipoContato.NFE, email);
                            } else {
                                imp.addContato(String.valueOf(i), "TELEFONE" + i+1, tels[i].trim(), null, TipoContato.NFE, null);
                            }
                        }
                    }
                    
                    if(contato != null && !"".equals(contato)) {
                        imp.addContato("1", contato, null, null, TipoContato.NFE, rs.getString("cargo_contato") == null ? "" : rs.getString("cargo_contato"));
                    }
                    
                    imp.setDatacadastro(rs.getDate("cadastro"));
                    imp.setAtivo(rs.getInt("situacao") == 1 ? true : false);
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setCep(rs.getString("cep"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("estado"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<OfertaIMP> getOfertas(Date dataTermino) throws Exception {
        List<OfertaIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	ofe.codigo_promocao,\n" +
                    "	ofe.data_inicio_promocao datainicio,\n" +
                    "	ofe.data_fim_promocao datafim,\n" +
                    "	ofei.codigo_produto idproduto,\n" +
                    "	ofei.codigo_barra_produto ean,\n" +
                    "	ofei.preco_item_promocao precooferta,\n" +
                    "	ofei.preco1_produto precovenda\n" +
                    "from \n" +
                    "	promocao_completa ofe\n" +
                    "inner join item_promocao_completo ofei \n" +
                    "	on ofe.codigo_promocao = ofei.codigo_promocao\n" +
                    "where ofe.data_fim_promocao > convert(datetime, convert(char(10), GETDATE(), 102))\n" +
                    "order by\n" +
                    "	ofe.data_fim_promocao")) {
                while(rs.next()) {
                    OfertaIMP imp = new OfertaIMP();
                    
                    imp.setIdProduto(rs.getString("idproduto"));
                    imp.setDataInicio(rs.getDate("datainicio"));
                    imp.setDataFim(rs.getDate("datafim"));
                    imp.setPrecoNormal(rs.getDouble("precovenda"));
                    imp.setPrecoOferta(rs.getDouble("precooferta"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        String sql = "";
        
        if(importarFuncionario) {
            sql = "select \n" +
                    "	cast(cl.codigo_cliente as varchar) id,\n" +
                    "	cl.razao_social_cliente razao,\n" +
                    "	cl.nome_fantasia_cliente fantasia,\n" +
                    "	cl.cnpj_cliente cnpj,\n" +
                    "	cl.inscricao_cliente ie,\n" +
                    "	cl.inscricao_municipal_cliente im,\n" +
                    "	en.rua,\n" +
                    "	en.bairro,\n" +
                    "	en.numero,\n" +
                    "	en.cep,\n" +
                    "	en.cidade,\n" +
                    "	en.estado,\n" +
                    "	en.complemento,\n" +
                    "	enc.rua ruacobranca,\n" +
                    "	enc.bairro bairrocobranca,\n" +
                    "	enc.numero numerocobranca,\n" +
                    "	enc.cep cepcobranca,\n" +
                    "	enc.cidade cidadecobranca,\n" +
                    "	enc.estado estadocobranca,\n" +
                    "	enc.complemento complementocobranca,\n" +
                    "	cl.telefone_cliente telefone,\n" +
                    "	cl.fax_cliente fax,\n" +
                    "	cl.email_cliente email,\n" +
                    "	cl.data_cadastro_cliente cadastro,\n" +
                    "	cl.nascimento_cliente nascimento,\n" +
                    "	cl.sexo_cliente sexo,\n" +
                    "	cl.estado_civil_cliente estadocivil,\n" +
                    "	cl.status_cliente situacao,\n" +
                    "	cl.limite_credito_cliente limite,\n" +
                    "	cl.contato_cliente contato,\n" +
                    "	cc.nome_contato,\n" +
                    "	cc.cargo_contato\n" +
                    "from \n" +
                    "	cliente cl\n" +
                    "left join endereco en on cl.endereco_cliente = en.codigo_endereco\n" +
                    "left join endereco enc on cl.endereco_cobranca_cliente = enc.codigo_endereco\n" +
                    "left join contato_cliente cc on cl.codigo_cliente = cc.codigo_cliente\n" +
                    "where\n" +
                    "	cl.codigo_cliente > 0\n" +
                    "union all\n" +
                    "select\n" +
                    "	'FUN' + cast(fun.codigo_funcionario as varchar) id,\n" +
                    "	fun.nome_funcionario razao,\n" +
                    "	fun.apelido_funcionario fantasia,\n" +
                    "	fun.cpf_funcionario cnpj,\n" +
                    "	fun.identidade_funcionario ie,\n" +
                    "	'' im,\n" +
                    "	e.rua,\n" +
                    "	e.bairro,\n" +
                    "	e.numero,\n" +
                    "	e.cep,\n" +
                    "	e.cidade,\n" +
                    "	e.estado,\n" +
                    "	e.complemento,\n" +
                    "	'' ruacobranca,\n" +
                    "	'' bairrocobranca,\n" +
                    "	'' numerocobranca,\n" +
                    "	'' cepcobranca,\n" +
                    "	'' cidadecobranca,\n" +
                    "	'' estadocobranca,\n" +
                    "	'' complementocobranca,\n" +
                    "	fun.telefone_funcionario telefone,\n" +
                    "	'' fax,\n" +
                    "	'' email,\n" +
                    "	fun.data_cadastro_funcionario cadastro,\n" +
                    "	fun.data_nascimento_funcionario nascimento,\n" +
                    "	fun.sexo_funcionario sexo,\n" +
                    "	fun.estado_civil_funcionario estadocivil,\n" +
                    "	case when \n" +
                    "		fun.situacao_funcionario = 0 then 1\n" +
                    "	else 0 end situacao,\n" +
                    "	fun.limite_credito_funcionario limite,\n" +
                    "	'' contato,\n" +
                    "	'' nome_contato,\n" +
                    "	'' cargo_contato\n" +
                    "from\n" +
                    "	funcionario fun\n" +
                    "left join endereco e on fun.codigo_endereco_funcionario = e.codigo_endereco\n" +
                    "where \n" +
                    "	fun.codigo_funcionario > 0";
        } else {
            sql = "select \n" +
                    "	cl.codigo_cliente id,\n" +
                    "	cl.razao_social_cliente razao,\n" +
                    "	cl.nome_fantasia_cliente fantasia,\n" +
                    "	cl.cnpj_cliente cnpj,\n" +
                    "	cl.inscricao_cliente ie,\n" +
                    "	cl.inscricao_municipal_cliente im,\n" +
                    "	en.rua,\n" +
                    "	en.bairro,\n" +
                    "	en.numero,\n" +
                    "	en.cep,\n" +
                    "	en.cidade,\n" +
                    "	en.estado,\n" +
                    "	en.complemento,\n" +
                    "	enc.rua ruacobranca,\n" +
                    "	enc.bairro bairrocobranca,\n" +
                    "	enc.numero numerocobranca,\n" +
                    "	enc.cep cepcobranca,\n" +
                    "	enc.cidade cidadecobranca,\n" +
                    "	enc.estado estadocobranca,\n" +
                    "	enc.complemento complementocobranca,\n" +
                    "	cl.telefone_cliente telefone,\n" +
                    "	cl.fax_cliente fax,\n" +
                    "	cl.email_cliente email,\n" +
                    "	cl.data_cadastro_cliente cadastro,\n" +
                    "	cl.nascimento_cliente nascimento,\n" +
                    "	cl.sexo_cliente sexo,\n" +
                    "	cl.estado_civil_cliente estadocivil,\n" +
                    "	cl.status_cliente situacao,\n" +
                    "	cl.limite_credito_cliente limite,\n" +
                    "	cl.contato_cliente contato,\n" +
                    "	cc.nome_contato,\n" +
                    "	cc.cargo_contato\n" +
                    "from \n" +
                    "	cliente cl\n" +
                    "left join endereco en on cl.endereco_cliente = en.codigo_endereco\n" +
                    "left join endereco enc on cl.endereco_cobranca_cliente = enc.codigo_endereco\n" +
                    "left join contato_cliente cc on cl.codigo_cliente = cc.codigo_cliente\n" +
                    "where \n" +
                    "	cl.codigo_cliente > 0";
        }
        
        try(Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(sql)) {
                while(rs.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    
                    imp.setId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setCnpj(rs.getString("cnpj"));
                    imp.setInscricaoestadual(rs.getString("ie"));
                    imp.setInscricaoMunicipal(rs.getString("im"));
                    imp.setEndereco(rs.getString("rua"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setCep(rs.getString("cep"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("estado"));
                    imp.setComplemento(rs.getString("complemento"));
                    
                    //End Cobrança
                    imp.setCobrancaEndereco(rs.getString("ruacobranca"));
                    imp.setCobrancaBairro(rs.getString("bairrocobranca"));
                    imp.setCobrancaNumero(rs.getString("numerocobranca"));
                    imp.setCobrancaCep(rs.getString("cepcobranca"));
                    imp.setCobrancaMunicipio(rs.getString("cidadecobranca"));
                    imp.setCobrancaUf(rs.getString("estadocobranca"));
                    imp.setCobrancaComplemento(rs.getString("complementocobranca"));
                    
                    imp.setTelefone(rs.getString("telefone"));
                    imp.setFax(rs.getString("fax"));
                    imp.setEmail(rs.getString("email"));
                    imp.setDataCadastro(rs.getDate("cadastro"));
                    imp.setDataNascimento(rs.getDate("nascimento"));
                    imp.setSexo("M".equals(rs.getString("sexo")) ? TipoSexo.MASCULINO : TipoSexo.FEMININO);
                    imp.setAtivo(rs.getInt("situacao") == 1 ? true : false);
                    imp.setValorLimite(rs.getDouble("limite"));
                    
                    String cont = rs.getString("nome_contato");
                    
                    if(cont != null && !"".equals(cont)) {
                        imp.addContato("1", cont, null, null, null);
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
        
        try(Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	c.codigo_conta id,\n" +
                    "	c.data_conta data,\n" +
                    "	c.data_vencimento_conta vencimento,\n" +
                    "	c.numero_nota_conta nota,\n" +
                    "	c.valor_conta valor,\n" +
                    "	c.obs_conta,\n" +
                    "	c.descricao_conta,\n" +
                    "	c.terminal_conta caixa,\n" +
                    "	c.saldo_pendente_conta saldo,\n" +
                    "	c.cliente_conta,\n" +
                    "	c.funcionario_conta\n" +
                    "from\n" +
                    "	conta c\n" +
                    "where \n" +
                    "	c.loja_conta = " + getLojaOrigem() + " and \n" +
                    "	c.operacao_conta > 0 and \n" +
                    "	c.saldo_pendente_conta > 0 and\n" +
                    "	--c.data_vencimento_conta between '2020-06-01' and '2020-06-30' and \n" +
                    "	c.cliente_conta > 0 or c.funcionario_conta > 0\n" +
                    "order by \n" +
                    "	c.data_conta")) {
                while(rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    
                    imp.setId(rs.getString("id"));
                    imp.setIdCliente(rs.getString("cliente_conta"));
                    imp.setDataEmissao(rs.getDate("data"));
                    imp.setDataVencimento(rs.getDate("vencimento"));
                    imp.setNumeroCupom(rs.getString("nota"));
                    imp.setValor(rs.getDouble("saldo"));
                    imp.setEcf(rs.getString("caixa"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
