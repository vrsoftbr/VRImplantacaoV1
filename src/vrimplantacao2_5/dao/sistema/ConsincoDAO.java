package vrimplantacao2_5.dao.sistema;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import vrimplantacao2_5.dao.conexao.ConexaoOracle;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CompradorIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2_5.vo.enums.ESistema;

/**
 *
 * @author guilhermegomes
 */
public class ConsincoDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return ESistema.CONSINCO.getNome();
    }
    
    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                OpcaoProduto.QTD_EMBALAGEM_EAN,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                OpcaoProduto.FAMILIA,
                OpcaoProduto.FAMILIA_PRODUTO,
                OpcaoProduto.ATIVO,
                OpcaoProduto.PESO_BRUTO,
                OpcaoProduto.PESO_LIQUIDO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.TROCA,
                OpcaoProduto.MARGEM,
                OpcaoProduto.VENDA_PDV,
                OpcaoProduto.PRECO,
                OpcaoProduto.CUSTO,
                OpcaoProduto.CUSTO_COM_IMPOSTO,
                OpcaoProduto.CUSTO_SEM_IMPOSTO,
                OpcaoProduto.NCM,
                OpcaoProduto.CEST,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.NATUREZA_RECEITA,
                OpcaoProduto.ICMS,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.ATUALIZAR_SOMAR_ESTOQUE,
                OpcaoProduto.OFERTA,
                OpcaoProduto.DESCONTINUADO,
                OpcaoProduto.VOLUME_QTD,
                OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                OpcaoProduto.VENDA_PDV
        ));
    }
    
    @Override
    public Set<OpcaoCliente> getOpcoesDisponiveisCliente() {
        return new HashSet<>(Arrays.asList(
                OpcaoCliente.DADOS,
                OpcaoCliente.ENDERECO,
                OpcaoCliente.CONTATOS,
                OpcaoCliente.DATA_CADASTRO,
                OpcaoCliente.DATA_NASCIMENTO,
                OpcaoCliente.RECEBER_CREDITOROTATIVO,
                OpcaoCliente.RECEBER_CHEQUE));
    }
    
    @Override
    public Set<OpcaoFornecedor> getOpcoesDisponiveisFornecedor() {
        return new HashSet<>(Arrays.asList(
                OpcaoFornecedor.ENDERECO,
                OpcaoFornecedor.DADOS,
                OpcaoFornecedor.CONTATOS,
                OpcaoFornecedor.SITUACAO_CADASTRO,
                OpcaoFornecedor.PRODUTO_FORNECEDOR,
                OpcaoFornecedor.PAGAR_FORNECEDOR));
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoOracle.createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	nrotributacao id,\n" +
                    "	peraliquota icms,\n" +
                    "	pertributado reducao,\n" +
                    "	situacaonf cst\n" +
                    "from \n" +
                    "	consinco.map_tributacaouf mt\n" +
                    "where \n" +
                    "	ufempresa = 'SP' and \n" +
                    "	ufclientefornec = 'SP' and \n" +
                    "	tiptributacao = 'SN' and \n" +
                    "	nroregtributacao = 2 /*lucro real*/ and \n" +
                    "	nrotributacao in (select distinct nrotributacao from consinco.map_famdivisao)")) {
                while(rs.next()) {
                    result.add(new MapaTributoIMP(
                            rs.getString("id"), 
                            rs.getString("cst") + " - " + 
                                rs.getString("icms") + " - " + 
                                    rs.getString("reducao"), 
                            rs.getInt("cst"),
                            rs.getDouble("icms"), 
                            rs.getDouble("reducao")));
                }
            }
        }
        
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoOracle.createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	distinct \n" +
                    "	seqcategorian1 codmercadologico1,\n" +
                    "	categorian1 descmercadologico1,\n" +
                    "	seqcategorian2 codmercadologico2,\n" +
                    "	categorian2 descmercadologico2\n" +
                    "from \n" +
                    "	consinco.etlv_categoria")) {
                while(rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rs.getString("codmercadologico1"));
                    imp.setMerc1Descricao(rs.getString("descmercadologico1"));
                    imp.setMerc2ID(rs.getString("codmercadologico2"));
                    imp.setMerc2Descricao(rs.getString("descmercadologico2"));
                    imp.setMerc3ID("1");
                    imp.setMerc3Descricao(imp.getMerc2Descricao());
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoOracle.createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	seqfamilia id,\n" +
                    "	familia\n" +
                    "from \n" +
                    "	consinco.map_familia")) {
                while(rs.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setDescricao(rs.getString("familia"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoOracle.createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "with \n" +
                    "	custos as \n" +
                    "       (select \n" +
                    "		mc.nroempresa,\n" +
                    "		mc.seqproduto,\n" +
                    "		mc.cmdiavlrnf custonf,\n" +
                    "		mc.vlrtotalvda precovendatotal,\n" +
                    "		mc.vlrcusliquidovda custosemimposto,\n" +
                    "		mc.cmdiacusliquidoemp custocomimposto,\n" +
                    "		mc.vlrcusbrutovda custobruto\n" +
                    "	   from \n" +
                    "		consinco.mrl_custodia mc \n" +
                    "	   where \n" +
                    "		dtaentradasaida = (select \n" +
                    "				     max(dtaentradasaida)\n" +
                    "				from \n" +
                    "                                consinco.mrl_custodia \n" +
                    "				where\n" +
                    "                               seqproduto = mc.seqproduto and \n" +
                    "                               nroempresa = mc.nroempresa))\n" +
                    "select \n" +
                    "	distinct\n" +
                    "	p.seqproduto id,\n" +
                    "	ean.codacesso ean,\n" +
                    "	p.seqfamilia idfamilia,\n" +
                    "	mfv.qtdembalagem qtdembalagemvenda,\n" +
                    "	mfv.embalagem embalagemvenda,\n" +
                    "	mf.qtdembalagem qtdembalagemcompra,\n" +
                    "	mf.embalagem embalagemcompra,\n" +
                    "	p.desccompleta descricaocompleta,\n" +
                    "	p.descreduzida descricaoreduzida,\n" +
                    "	p.descgenerica descricaogondola,\n" +
                    "	p.complemento,\n" +
                    "	cat.seqcategorian1 merc1,\n" +
                    "	cat.seqcategorian2 merc2,\n" +
                    "	p.reffabricante,\n" +
                    "	p.dtahorinclusao cadastro,\n" +
                    "	p.pzovalidademes validade,\n" +
                    "	p.codprodfiscal fiscal,\n" +
                    "	pe.statuscompra situacaocompra,\n" +
                    "	pre.statusvenda situacaovenda,\n" +
                    "	pe.estqmaximoloja estoquemax,\n" +
                    "	pe.estqminimoloja estoquemin,\n" +
                    "	pe.estqloja estoque,\n" +
                    "	mfv.pesobruto,\n" +
                    "	mfv.pesoliquido,\n" +
                    "	pe.estqtroca troca,\n" +
                    "	pe.estqempresa estoquemp,\n" +
                    "	pre.precobasenormal precovenda,\n" +
                    "	pre.precogernormal,\n" +
                    "	case when \n" +
                    "		cu.custosemimposto = 0 then\n" +
                    "	round(\n" +
                    "	 nvl(\n" +
                    "		consinco.fmrl_custoultcomprapiscofins(\n" +
                    "			pre.seqproduto, \n" +
                    "			pre.nroempresa, \n" +
                    "			nvl(consinco.fmad_tipcalcultcompra(pre.nroempresa, 's'), 'l'), \n" +
                    "			trunc(sysdate), \n" +
                    "			nvl(consinco.fmad_tipcalcultcompra(pre.nroempresa, 'c'), 's')), 0\n" +
                    "			) * pre.qtdembalagem, 4) else \n" +
                    "	cu.custosemimposto end custosemimpostotemp,\n" +
                    "	cu.custocomimposto custocomimpostotemp,\n" +
                    "	round(\n" +
                    "	 nvl(\n" +
                    "		consinco.fmrl_custoprodempdata(\n" +
                    "			pre.seqproduto, \n" +
                    "			pre.nroempresa, \n" +
                    "			nvl(consinco.fmad_tipcalcultcompra(pre.nroempresa, 's'), 'l'),\n" +
                    "			trunc(sysdate)\n" +
                    "			) * pre.qtdembalagem, 4)) as custocomimposto,\n" +
                    "	round(\n" +
                    "		consinco.fc5margempreco(\n" +
                    "			pre.seqproduto,\n" +
                    "			pre.nroempresa, \n" +
                    "			pre.nrosegmento, \n" +
                    "			pre.qtdembalagem, \n" +
                    "			pre.vlrcustoliqdiaprecif, \n" +
                    "			decode(pre.precovalidpromoc, \n" +
                    "					0, \n" +
                    "					pre.precovalidnormal, \n" +
                    "					pre.precovalidpromoc)), 2\n" +
                    "			) as margem,\n" +
                    "	fa.codcest cest,\n" +
                    "	fa.codnatrec naturezareceita,\n" +
                    "	fa.pesavel ebalanca,\n" +
                    "	fa.codnbmsh ncm,\n" +
                    "	fa.situacaonfcofins cofinsentrada,\n" +
                    "	fa.situacaonfcofinssai cofinsaida,\n" +
                    "	fa.situacaonfpis pisentrada,\n" +
                    "	fa.situacaonfpissai pisaida,\n" +
                    "	fad.nrotributacao idaliquota,\n" +
                    "	fad.seqcomprador comprador\n" +
                    "from \n" +
                    "	consinco.map_produto p\n" +
                    "join\n" +
                    "	consinco.mad_segmento ms on\n" +
                    "	ms.nrosegmento = 1\n" +
                    "left join\n" +
                    "	consinco.mrl_produtoempresa pe on\n" +
                    "	p.seqproduto = pe.seqproduto\n" +
                    "left join\n" +
                    "	consinco.map_prodcodigo ean on\n" +
                    "	p.seqproduto = ean.seqproduto\n" +
                    "left join\n" +
                    "	consinco.map_famembalagem mfv on\n" +
                    "	ean.qtdembalagem = mfv.qtdembalagem and \n" +
                    "	ean.seqfamilia = mfv.seqfamilia\n" +
                    "left join\n" +
                    "	consinco.mrl_prodempseg pre on\n" +
                    "	p.seqproduto = pre.seqproduto and\n" +
                    "	pre.nroempresa = pe.nroempresa and\n" +
                    "	pre.nrosegmento = ms.nrosegmento\n" +
                    "left join\n" +
                    "	consinco.map_familia fa on\n" +
                    "	p.seqfamilia = fa.seqfamilia\n" +
                    "left join \n" +
                    "	consinco.map_famdivisao fad on\n" +
                    "	fa.seqfamilia = fad.seqfamilia and\n" +
                    "	fad.nrodivisao = ms.nrodivisao\n" +
                    "left join\n" +
                    "	consinco.map_famembalagem mf on\n" +
                    "	fad.seqfamilia = mf.seqfamilia and\n" +
                    "	fad.padraoembcompra = mf.qtdembalagem\n" +
                    "left join\n" +
                    "	consinco.etlv_categoria cat on\n" +
                    "	fa.seqfamilia = cat.seqfamilia\n" +
                    "left join\n" +
                    "	custos cu on pe.nroempresa = cu.nroempresa and\n" +
                    "	pe.seqproduto = cu.seqproduto\n" +
                    "where\n" +
                    "	pe.nroempresa = " + getLojaOrigem())) {
                while(rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setEan(rs.getString("ean"));
                    imp.seteBalanca(rs.getString("ebalanca").equals("S"));
                    imp.setQtdEmbalagem(rs.getInt("qtdembalagemvenda"));
                    imp.setTipoEmbalagem(rs.getString("embalagemvenda"));
                    imp.setIdFamiliaProduto(rs.getString("idfamilia"));
                    imp.setQtdEmbalagemCotacao(rs.getInt("qtdembalagemcompra"));
                    imp.setTipoEmbalagemCotacao(rs.getString("embalagemcompra"));
                    imp.setDescricaoCompleta(rs.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rs.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(rs.getString("descricaogondola"));
                    imp.setCodMercadologico1(rs.getString("merc1"));
                    imp.setCodMercadologico2(rs.getString("merc2"));
                    imp.setCodMercadologico3("1");
                    imp.setDataCadastro(rs.getDate("cadastro"));
                    imp.setValidade(rs.getInt("validade"));
                    imp.setDescontinuado(rs.getString("situacaocompra").equals("I"));
                    imp.setSituacaoCadastro(rs.getString("situacaovenda").equals("A") ? 1 : 0);
                    imp.setEstoqueMaximo(rs.getDouble("estoquemax"));
                    imp.setEstoqueMinimo(rs.getDouble("estoquemin"));
                    imp.setEstoque(rs.getDouble("estoque"));
                    imp.setTroca(rs.getDouble("troca"));
                    imp.setPrecovenda(rs.getDouble("precovenda"));
                    imp.setCustoSemImposto(rs.getDouble("custosemimpostotemp"));
                    imp.setCustoComImposto(rs.getDouble("custocomimpostotemp"));
                    imp.setMargem(rs.getDouble("margem"));
                    imp.setCest(rs.getString("cest"));
                    imp.setNcm(rs.getString("ncm"));
                    imp.setPiscofinsNaturezaReceita(rs.getString("naturezareceita"));
                    imp.setPiscofinsCstDebito(rs.getString("pisaida"));
                    imp.setIcmsDebitoId(rs.getString("idaliquota"));
                    imp.setIcmsDebitoForaEstadoId(imp.getIcmsDebitoId());
                    imp.setIcmsDebitoForaEstadoNfId(imp.getIcmsDebitoId());
                    imp.setIcmsConsumidorId(imp.getIcmsDebitoId());
                    imp.setIcmsCreditoId(imp.getIcmsDebitoId());
                    imp.setIcmsCreditoForaEstadoId(imp.getIcmsDebitoId());
                    imp.setIdComprador(rs.getString("comprador"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }
    
    @Override
    public List<CompradorIMP> getCompradores() throws Exception {
        List<CompradorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n" +
                    "	seqcomprador id,\n" +
                    "	comprador\n" +
                    "from \n" +
                    "	consinco.max_comprador"
            )) {
                while (rst.next()) {
                    result.add(new CompradorIMP(rst.getString("id"), rst.getString("comprador")));
                }
            }
        }

        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoOracle.createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "   forn.seqfornecedor,\n" +
                    "   pes.nomerazao,\n" +
                    "   pes.fantasia,\n" +
                    "   pes.bairro,\n" +
                    "   pes.cep,\n" +
                    "   pes.logradouro endereco,\n" +
                    "   pes.nrologradouro numero,\n" +
                    "   pes.cidade,\n" +
                    "   pes.uf, \n" +
                    "   forn.statusgeral,\n" +
                    "   pes.cmpltologradouro complemento,\n" +
                    "   pes.foneddd1 || pes.fonenro1 fone1,\n" +
                    "   pes.foneddd2 || pes.fonenro2 fone2,\n" +
                    "   pes.foneddd3 || pes.fonenro3 fone3,\n" +
                    "   pes.faxddd || pes.faxnro fax,\n" +
                    "   pes.nrocgccpf || pes.digcgccpf cnpj,\n" +
                    "   pes.inscricaorg ie,\n" +
                    "   pes.email,\n" +
                    "   pes.dtainclusao cadastro,\n" +
                    "   forn.nomecontato,\n" +
                    "   forn.fonecontato,\n" +
                    "   forn.emailcontato,\n" +
                    "   forn.faxcontato,\n" +
                    "   forn.observacao,\n" +
                    "   pes.homepage,\n" +
                    "   pes.emailnfe\n" +
                    "from\n" +
                    "   consinco.maf_fornecedor forn\n" +
                    "inner join \n" +
                    "	consinco.ge_pessoa pes ON forn.seqfornecedor = pes.seqpessoa")) {
                while (rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("seqfornecedor"));
                    imp.setRazao(rs.getString("nomerazao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setCnpj_cpf(rs.getString("cnpj"));
                    imp.setIe_rg(rs.getString("ie"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setCep(rs.getString("cep"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("uf"));
                    imp.setAtivo(rs.getString("statusgeral").equals("A"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setTel_principal(rs.getString("fone1"));
                    
                    String fone2 = rs.getString("fone2");
                    String fone3 = rs.getString("fone3");
                    String fax = rs.getString("fax");
                    
                    if(fone2 != null && !fone2.isEmpty()) {
                        imp.addContato("FONE2", fone2, null, TipoContato.COMERCIAL, null);
                    }
                    
                    if(fone3 != null && !fone3.isEmpty()) {
                        imp.addContato("FONE3", fone3, null, TipoContato.COMERCIAL, null);
                    }
                    
                    if(fax != null && !fax.isEmpty()) {
                        imp.addContato("FAX", fax, null, TipoContato.COMERCIAL, null);
                    }
                    
                    String email = rs.getString("email");
                    
                    if(email != null && !email.isEmpty()) {
                        imp.addContato("EMAIL", null, null, TipoContato.COMERCIAL, email);
                    }
                    
                    imp.setObservacao(rs.getString("observacao"));
                    
                    String contato = rs.getString("nomecontato");
                    
                    if(contato != null && !contato.isEmpty()) {
                        imp.addContato(contato, rs.getString("fonecontato"), null, TipoContato.COMERCIAL, rs.getString("emailcontato"));
                    }
                    
                    addContatoFornecedor(imp);
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }
    
    private void addContatoFornecedor(FornecedorIMP imp) throws Exception {
        try(Statement stm = ConexaoOracle.createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	seqfornecedor,\n" +
                    "	seqforneccontato,\n" +
                    "	nomerazao,\n" +
                    "	fone,\n" +
                    "	celular,\n" +
                    "	fax,\n" +
                    "	email\n" +
                    "from\n" +
                    "	consinco.maf_forneccontato\n" +
                    "where seqfornecedor = " + imp.getImportId())) {
                while(rs.next()) {
                    imp.addContato(rs.getString("nomerazao"), 
                            rs.getString("fone"), 
                            rs.getString("celular"), 
                            TipoContato.COMERCIAL, 
                            rs.getString("email"));
                }
            }
        }
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoOracle.createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	b.seqproduto,\n" +
                    "	d.seqfornecedor,\n" +
                    "	a.nroempresa,\n" +
                    "	f.nomerazao\n" +
                    "from\n" +
                    "	consinco.mrl_prodempresawm a\n" +
                    "join consinco.map_produto b on\n" +
                    "	a.seqproduto = b.seqproduto\n" +
                    "join consinco.map_familia c on\n" +
                    "	c.seqfamilia = b.seqfamilia\n" +
                    "join consinco.map_famfornec d on\n" +
                    "	d.seqfamilia = b.seqfamilia\n" +
                    "join consinco.ge_pessoa f on\n" +
                    "	f.seqpessoa = d.seqfornecedor\n" +
                    "	and not exists (\n" +
                    "	select\n" +
                    "		1\n" +
                    "	from\n" +
                    "		consinco.map_produto z\n" +
                    "	where\n" +
                    "		z.seqprodutosecundario = b.seqproduto)\n" +
                    "where\n" +
                    "	nroempresa = " + getLojaOrigem())) {
                while(rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdFornecedor(rs.getString("seqfornecedor"));
                    imp.setIdProduto(rs.getString("seqproduto"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoOracle.createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "SELECT\n" +
                    "	p.seqpessoa AS id,\n" +
                    "	p.nomerazao AS razao,\n" +
                    "	decode( p.fisicajuridica, 'F', p.nomerazao, p.fantasia ) AS fantasia,\n" +
                    "	gpc.LIMITECREDITO limite,\n" +
                    "	p.cidade,\n" +
                    "	p.seqcidade,\n" +
                    "	p.uf,\n" +
                    "	p.porte,\n" +
                    "	p.atividade,\n" +
                    "	p.cep,\n" +
                    "	p.bairro,\n" +
                    "	p.logradouro,\n" +
                    "	p.nrologradouro as numero,\n" +
                    "	c.dtaultcompra,\n" +
                    "	p.nrocgccpf || lpad(p.digcgccpf, 2, 0) AS nrocpfcnpj,\n" +
                    "	p.dtanascfund AS dtaaniversario,\n" +
                    "	decode(p.fisicajuridica, 'F', 'FISICA', 'J', 'JURIDICA', 'OUTRO') AS fisicajuridica,\n" +
                    "	c.dtacadastro AS dtacadastramento,\n" +
                    "	decode(gpc.situacaocredito, 'L', 'LIBERADO',\n" +
                    "                                'A', 'ANALISE',\n" +
                    "                                'B', 'BLOQUEADO',\n" +
                    "                                'S', 'SUSPENSO',\n" +
                    "                                NULL, NULL,\n" +
                    "                                gpc.situacaocredito) AS statusfinanceiro,\n" +
                    "	fc.saldopontocredito AS scorefinanceiro,\n" +
                    "	CASE\n" +
                    "		WHEN (p.status = 'A'\n" +
                    "		AND p.logradouro IS NOT NULL\n" +
                    "		AND p.nrologradouro IS NOT NULL\n" +
                    "		AND decode( p.fisicajuridica, 'J', 'J', p.sexo) IS NOT NULL\n" +
                    "		AND p.nrocgccpf IS NOT NULL\n" +
                    "		AND p.inscricaorg IS NOT NULL\n" +
                    "		AND p.cidade IS NOT NULL\n" +
                    "		AND p.cep IS NOT NULL\n" +
                    "		AND p.fonenro1 IS NOT NULL\n" +
                    "		AND p.foneddd1 IS NOT NULL\n" +
                    "		AND p.email IS NOT NULL)\n" +
                    "          THEN 1\n" +
                    "		ELSE to_number(NULL)\n" +
                    "	END AS indcadastrocompleto,\n" +
                    "	p.status AS statuspessoa,\n" +
                    "	decode(p.fisicajuridica, 'J', 'J', p.sexo) AS sexo,\n" +
                    "	p.inscricaorg,\n" +
                    "	p.foneddd1,\n" +
                    "	p.fonenro1,\n" +
                    "	p.email,\n" +
                    "	c.statuscliente,\n" +
                    "	fc.PERMCHEQUE,\n" +
                    "	fc.PERMCARTAO,\n" +
                    "	gpc.NOMECONJUGE,\n" +
                    "	gpc.NOMEPAI,\n" +
                    "	gpc.NOMEMAE\n" +
                    "FROM\n" +
                    "	consinco.mrl_cliente c,\n" +
                    "	consinco.ge_pessoa p,\n" +
                    "	consinco.ge_pessoacadastro gpc,\n" +
                    "	consinco.fi_cliente fc\n" +
                    "WHERE\n" +
                    "	p.seqpessoa = c.seqpessoa(+) AND \n" +
                    "	gpc.seqpessoa(+) = c.seqpessoa AND \n" +
                    "	fc.seqpessoa(+) = c.seqpessoa and\n" +
                    "   c.nroempresa = " + getLojaOrigem() + "\n" +        
                    "ORDER BY\n" +
                    "	p.seqpessoa")) {
                while(rs.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    
                    imp.setId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setCnpj(rs.getString("nrocpfcnpj"));
                    imp.setInscricaoestadual(rs.getString("inscricaorg"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setValorLimite(rs.getDouble("limite"));
                    
                    if(imp.getValorLimite() > 99999999) {
                        imp.setValorLimite(0);
                    }
                    
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setEndereco(rs.getString("logradouro"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setDataNascimento(rs.getDate("dtaaniversario"));
                    imp.setDataCadastro(rs.getDate("dtacadastramento"));
                    
                    if(rs.getString("statuspessoa") != null && !rs.getString("statuspessoa").isEmpty()) {
                        imp.setAtivo(rs.getString("statuspessoa").equals("LIBERADO"));
                    }
                    
                    if(rs.getString("statusfinanceiro") != null && 
                            !rs.getString("statusfinanceiro").isEmpty() ) {
                        imp.setBloqueado(rs.getString("statusfinanceiro").equals("LIBERADO"));
                    }
                    
                    if(rs.getString("sexo") != null && !rs.getString("sexo").isEmpty()) {
                        imp.setSexo(rs.getString("sexo").equals("F") ? 
                                TipoSexo.FEMININO : TipoSexo.MASCULINO);
                    }
                    
                    imp.setTelefone(rs.getString("foneddd1") + "" + rs.getString("fonenro1"));
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
        
        try(Statement stm = ConexaoOracle.createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "SELECT\n" +
                    "	(CASE\n" +
                    "		WHEN b.seqtitulo IS NULL THEN 'CHEQUE'\n" +
                    "		ELSE 'TITULO'\n" +
                    "	END) AS tipo,\n" +
                    "	a.nroempresa,\n" +
                    "	l.razaosocial AS nomeempresa,\n" +
                    "	a.nrobanco,\n" +
                    "	j.razaosocial AS nomebanco,\n" +
                    "	a.seqagencia,\n" +
                    "	m.nomeagencia,\n" +
                    "	a.nrotitulo,\n" +
                    "	a.vlroriginal,\n" +
                    "	a.vlrnominal,\n" +
                    "	a.vlrpago,\n" +
                    "	a.vlrnominal - a.vlrpago AS vlremaberto,\n" +
                    "	a.seqpessoa idcliente,\n" +
                    "	a.codespecie,\n" +
                    "	k.descricao AS descespecie,\n" +
                    "	a.serietitulo,\n" +
                    "	a.nroparcela,\n" +
                    "	a.sitjuridica,\n" +
                    "	(CASE\n" +
                    "		WHEN a.obrigdireito = 'd' THEN (\n" +
                    "		SELECT\n" +
                    "			n.situacaocredito\n" +
                    "		FROM\n" +
                    "			consinco.gev_pessoacadastro n\n" +
                    "		WHERE\n" +
                    "			n.seqpessoa = a.seqpessoa)\n" +
                    "		ELSE NULL\n" +
                    "	END) AS sitcredito,\n" +
                    "	a.dtaprogramada,\n" +
                    "	a.dtaemissao,\n" +
                    "	a.dtavencimento,\n" +
                    "	a.dtamovimento,\n" +
                    "	a.dtamovimento - a.dtaemissao AS dtaviagem,\n" +
                    "	a.dtaprogramada - a.dtaemissao AS prazoefetivo,\n" +
                    "	a.seqtitulo,\n" +
                    "	a.nroempresamae,\n" +
                    "	a.obrigdireito,\n" +
                    "	a.nroaltdepositario,\n" +
                    "	a.nrocarga,\n" +
                    "	a.acertadacarga,\n" +
                    "	a.dtacarga,\n" +
                    "	b.codbarra,\n" +
                    "	nvl(b.vlrdesccontrato, 0) AS vlrdesccontrato,\n" +
                    "	b.codigofator,\n" +
                    "	c.seqpessoa || ' - ' || c.nomerazao AS nomerazao,\n" +
                    "	c.fisicajuridica,\n" +
                    "	c.seqcidade,\n" +
                    "	d.seqctacorrente,\n" +
                    "	d.codcarteira,\n" +
                    "	a.situacao,\n" +
                    "	nvl(a.susplib, 'l') susplib,\n" +
                    "	c.nomerazao nomerazaopessoa,\n" +
                    "	consinco.fif_valor_taxa_admcupom(a.seqtitulo,\n" +
                    "	a.vlradministracao,\n" +
                    "	a.taxacupom) vlrtaxas,\n" +
                    "	nvl(b.pctdescfinanc, 0) pctdescfinanc,\n" +
                    "	nvl((SELECT max(fd.percdescfinacordo)\n" +
                    "            FROM consinco.maf_fornecdivisao fd,\n" +
                    "                   consinco.max_empresa me\n" +
                    "            WHERE fd.nrodivisao = me.nrodivisao\n" +
                    "            AND fd.seqfornecedor = a.seqpessoa\n" +
                    "            AND me.nroempresa = a.nroempresa), 0) pctdescacordo,\n" +
                    "	a.seqdepositario,\n" +
                    "	n.descricao depositario\n" +
                    "FROM\n" +
                    "	consinco.fi_titulo a,\n" +
                    "	consinco.fi_compltitulo b,\n" +
                    "	consinco.ge_pessoa c,\n" +
                    "	consinco.fi_titulobco d,\n" +
                    "	consinco.ge_banco j,\n" +
                    "	consinco.fi_especie k,\n" +
                    "	consinco.ge_empresa l,\n" +
                    "	consinco.ge_agencia m,\n" +
                    "	consinco.fi_depositario n\n" +
                    "WHERE\n" +
                    "	a.seqtitulo = b.seqtitulo(+)\n" +
                    "	AND a.nrobanco = j.nrobanco\n" +
                    "	AND a.codespecie = k.codespecie\n" +
                    "	AND a.nroempresamae = k.nroempresamae\n" +
                    "	AND a.nroempresa = l.nroempresa\n" +
                    "	AND a.seqtitulo = d.seqtitulo (+)\n" +
                    "	AND a.seqpessoa = c.seqpessoa\n" +
                    "	AND a.nrobanco = m.nrobanco(+)\n" +
                    "	AND a.seqagencia = m.seqagencia(+)\n" +
                    "	AND a.seqdepositario = n.seqdepositario\n" +
                    "	AND a.abertoquitado = 'A'\n" +
                    "	AND a.nroempresa = " + getLojaOrigem() + "\n" +
                    "	AND a.seqdepositario = 1")) {
                while(rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    
                    imp.setId(rs.getString("seqtitulo"));
                    imp.setNumeroCupom(rs.getString("nrotitulo"));
                    imp.setIdCliente(rs.getString("idcliente"));
                    imp.setDataEmissao(rs.getDate("dtaemissao"));
                    imp.setDataVencimento(rs.getDate("dtavencimento"));
                    imp.setParcela(rs.getInt("nroparcela"));
                    imp.setValor(rs.getDouble("vlremaberto"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ContaPagarIMP> getContasPagar() throws Exception {
        List<ContaPagarIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoOracle.createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "SELECT\n" +
                    "	(CASE\n" +
                    "		WHEN b.seqtitulo IS NULL THEN 'CHEQUE'\n" +
                    "		ELSE 'TITULO'\n" +
                    "	END) AS tipo,\n" +
                    "	a.nroempresa,\n" +
                    "	l.razaosocial AS nomeempresa,\n" +
                    "	a.nrobanco,\n" +
                    "	j.razaosocial AS nomebanco,\n" +
                    "	a.seqagencia,\n" +
                    "	m.nomeagencia,\n" +
                    "	a.nrotitulo,\n" +
                    "	a.vlroriginal,\n" +
                    "	a.vlrnominal,\n" +
                    "	a.vlrpago,\n" +
                    "	a.vlrnominal - a.vlrpago AS vlremaberto,\n" +
                    "	a.seqpessoa,\n" +
                    "	a.codespecie,\n" +
                    "	k.descricao AS descespecie,\n" +
                    "	a.serietitulo,\n" +
                    "	a.nroparcela,\n" +
                    "	a.sitjuridica,\n" +
                    "	(CASE\n" +
                    "		WHEN a.obrigdireito = 'd' THEN (\n" +
                    "		SELECT\n" +
                    "			n.situacaocredito\n" +
                    "		FROM\n" +
                    "			consinco.gev_pessoacadastro n\n" +
                    "		WHERE\n" +
                    "			n.seqpessoa = a.seqpessoa)\n" +
                    "		ELSE NULL\n" +
                    "	END) AS sitcredito,\n" +
                    "	a.dtaprogramada,\n" +
                    "	a.dtaemissao,\n" +
                    "	a.dtavencimento,\n" +
                    "	a.dtamovimento,\n" +
                    "	a.dtamovimento - a.dtaemissao AS dtaviagem,\n" +
                    "	a.dtaprogramada - a.dtaemissao AS prazoefetivo,\n" +
                    "	a.seqtitulo,\n" +
                    "	a.nroempresamae,\n" +
                    "	a.obrigdireito,\n" +
                    "	a.nroaltdepositario,\n" +
                    "	a.nrocarga,\n" +
                    "	a.acertadacarga,\n" +
                    "	a.dtacarga,\n" +
                    "	b.codbarra,\n" +
                    "	nvl(b.vlrdesccontrato, 0) AS vlrdesccontrato,\n" +
                    "	b.codigofator,\n" +
                    "	c.seqpessoa || ' - ' || c.nomerazao AS nomerazao,\n" +
                    "	c.fisicajuridica,\n" +
                    "	c.seqcidade,\n" +
                    "	d.seqctacorrente,\n" +
                    "	d.codcarteira,\n" +
                    "	a.situacao,\n" +
                    "	nvl(a.susplib, 'l') susplib,\n" +
                    "	c.nomerazao nomerazaopessoa,\n" +
                    "	consinco.fif_valor_taxa_admcupom(a.seqtitulo,\n" +
                    "	a.vlradministracao,\n" +
                    "	a.taxacupom) vlrtaxas,\n" +
                    "	nvl(b.pctdescfinanc, 0) pctdescfinanc,\n" +
                    "	nvl((SELECT max(fd.percdescfinacordo)\n" +
                    "            FROM consinco.maf_fornecdivisao fd,\n" +
                    "                   consinco.max_empresa me\n" +
                    "            WHERE fd.nrodivisao = me.nrodivisao\n" +
                    "            AND fd.seqfornecedor = a.seqpessoa\n" +
                    "            AND me.nroempresa = a.nroempresa), 0) pctdescacordo,\n" +
                    "	a.seqdepositario,\n" +
                    "	n.descricao depositario\n" +
                    "FROM\n" +
                    "	consinco.fi_titulo a,\n" +
                    "	consinco.fi_compltitulo b,\n" +
                    "	consinco.ge_pessoa c,\n" +
                    "	consinco.fi_titulobco d,\n" +
                    "	consinco.ge_banco j,\n" +
                    "	consinco.fi_especie k,\n" +
                    "	consinco.ge_empresa l,\n" +
                    "	consinco.ge_agencia m,\n" +
                    "	consinco.fi_depositario n\n" +
                    "WHERE\n" +
                    "	a.seqtitulo = b.seqtitulo (+)\n" +
                    "	AND a.nrobanco = j.nrobanco\n" +
                    "	AND a.codespecie = k.codespecie\n" +
                    "	AND a.nroempresamae = k.nroempresamae\n" +
                    "	AND a.nroempresa = l.nroempresa\n" +
                    "	AND a.seqtitulo = d.seqtitulo (+)\n" +
                    "	AND a.seqpessoa = c.seqpessoa\n" +
                    "	AND a.nrobanco = m.nrobanco (+)\n" +
                    "	AND a.seqagencia = m.seqagencia (+)\n" +
                    "	AND a.seqdepositario = n.seqdepositario\n" +
                    "	AND a.abertoquitado = 'A'\n" +
                    "	AND a.nroempresa = " + getLojaOrigem() + "\n" +
                    "	AND a.seqdepositario = 2")) {
                while(rs.next()) {
                    ContaPagarIMP imp = new ContaPagarIMP();
                    
                    imp.setId(rs.getString("seqtitulo"));
                    imp.setNumeroDocumento(rs.getString("nrotitulo"));
                    imp.setIdFornecedor(rs.getString("seqpessoa"));
                    imp.setDataEmissao(rs.getDate("dtaemissao"));
                    imp.setDataEntrada(rs.getDate("dtamovimento"));
                    
                    imp.addVencimento(
                            rs.getDate("dtavencimento"), 
                            rs.getDouble("vlremaberto"), 
                            rs.getInt("nroparcela"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }
}
