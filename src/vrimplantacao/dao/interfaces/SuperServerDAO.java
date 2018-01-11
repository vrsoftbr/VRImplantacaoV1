package vrimplantacao.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import vrframework.classe.ProgressBar;
import vrframework.remote.ItemComboVO;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.ClientePreferencialVO;
import vrimplantacao.vo.vrimplantacao.ReceberCreditoRotativoVO;
import vrimplantacao2.dao.cadastro.local.MunicipioDAO;
import vrimplantacao2.dao.cadastro.venda.VendaHistoricoIMP;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.parametro.Parametros;
import vrimplantacao2.utils.sql.SQLUtils;
import vrimplantacao2.vo.cadastro.local.MunicipioVO;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorContatoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

public class SuperServerDAO extends AbstractIntefaceDao {
    
    private final InterfaceDAO dao = new InterfaceDAO() {        

        @Override
        public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
            List<FamiliaProdutoIMP> result = new ArrayList<>();
        
            try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select id, nomeFamilia from CadProduto.Familia;"
                )) {
                    int cont = 1;
                    while (rst.next()) {
                        FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                        imp.setImportSistema(getSistema());
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportId(rst.getString("id"));
                        imp.setDescricao(rst.getString("nomeFamilia"));
                        result.add(imp);
                    }
                }
            }

            return result;
        }

        @Override
        public List<MercadologicoIMP> getMercadologicos() throws Exception {
            List<MercadologicoIMP> result = new ArrayList<>();
            
            try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select \n" +
                        "	m1.id as codM1, \n" +
                        "	m1.nomeCategoria as descM1, \n" +
                        "	m2.id as codM2, \n" +
                        "	m2.nomeCategoria as descM2, \n" +
                        "	1 codM3, \n" +
                        "	m2.nomeCategoria as descM3 \n" +
                        "from \n" +
                        "	CadProduto.Categoria m1 \n" +
                        "	left join CadProduto.SubCategoria m2 on \n" +
                        "		m2.fkCategoria = m1.id \n" +
                        "order by \n" +
                        "	codM1, \n" +
                        "	codM2 "
                )) {
                    int cont = 1;
                    while (rst.next()) {
                        MercadologicoIMP imp = new MercadologicoIMP();
                        imp.setImportSistema(getSistema());
                        imp.setImportLoja(getLojaOrigem());
                        imp.setMerc1ID(rst.getString("codM1"));
                        imp.setMerc1Descricao(rst.getString("descM1"));
                        imp.setMerc2ID(rst.getString("codM2"));
                        imp.setMerc2Descricao(rst.getString("descM2"));
                        imp.setMerc3ID(rst.getString("codM3"));
                        imp.setMerc3Descricao(rst.getString("descM3"));
                        
                        result.add(imp);
                    }
                }
            }
            
            return result;
        }
        
        

        @Override
        public List<ProdutoIMP> getProdutos() throws Exception {
            List<ProdutoIMP> result = new ArrayList<>();
            
            try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "declare \n" +
                        "	@idLoja varchar,\n" +
                        "	@fkCliente integer;\n" +
                        "\n" +
                        "set @idLoja = " + SQLUtils.stringSQL(getLojaOrigem()) + ";\n" +
                        "set @fkCliente = 1;\n" +
                        "\n" +
                        "select\n" +
                        "	p.id,\n" +
                        "	ean.ean,\n" +
                        "	ean.unidade,\n" +
                        "	p.balanca balanca,\n" +
                        "	p.balancaUnit e_unitario_pesavel,\n" +
                        "	p.nomeProduto descricaocompleta,\n" +
                        "	p.nomeImpressao descricaoreduzida,\n" +
                        "	p.nomeProduto descricaogondola,\n" +
                        "	p.fkCategoria cod_mercadologico1, \n" +
                        "	p.fkSubCategoria cod_mercadologico2, \n" +
                        "	case p.fkFamilia when 0 then null else p.fkFamilia end id_familiaproduto,\n" +
                        "	p.peso pesobruto,\n" +
                        "	p.peso pesoliquido,\n" +
                        "	p.dtUltimaEntrada datacadastro,\n" +
                        "	p.balancaValidade validade,\n" +
                        "	p.margemMinima margem,\n" +
                        "	e.estoqueAtual estoque,\n" +
                        "	e.estoqueMin estoqueminimo,\n" +
                        "	cast(p.custoCaixa / (case when p.tamCaixa < 1 then 1 else p.tamCaixa end) as numeric(10,2)) custocomimposto,\n" +
                        "	cast(p.custoCaixa / (case when p.tamCaixa < 1 then 1 else p.tamCaixa end) as numeric(10,2)) custosemimposto,\n" +
                        "	coalesce(preco.precoAtivo, p.precoVenda) precovenda,\n" +
                        "	p.ativo,\n" +
                        "	p.precoPromo,\n" +
                        "	p.dtInicioPromo,\n" +
                        "	p.dtFimPromo,\n" +
                        "	p.classFiscal ncm,\n" +
                        "	null cest,\n" +
                        "	p.tribPIS piscofins_cst_debito,\n" +
                        "	p.TribPisCofinsEntrada piscofins_cst_credito ,\n" +
                        "	p.naturezaReceitaPisCofins piscofins_natureza_receita,\n" +
                        "	p.tribICMS icms_cst,\n" +
                        "	p.aliqICMS,\n" +
                        "	p.aliqPIS,\n" +
                        "	icms.taxa icms_aliq,\n" +
                        "	icms.aliquotaReduzidaA icms_reducao\n" +
                        "from\n" +
                        "	CadProduto.Produto p\n" +
                        "	left join (select\n" +
                        "			p.id id_produto,\n" +
                        "			p.id ean,\n" +
                        "			p.unidade,\n" +
                        "			1 qtdembalagem\n" +
                        "		from\n" +
                        "			CadProduto.Produto p\n" +
                        "		union\n" +
                        "		select\n" +
                        "			ean.fkProduto id_produto,\n" +
                        "			ean.id ean,\n" +
                        "			p.unidade,\n" +
                        "			case when ean.qtdade < 1 then 1 else ean.qtdade end as unidade\n" +
                        "		from\n" +
                        "			CadProduto.EanAfiliado ean\n" +
                        "			join CadProduto.Produto p on\n" +
                        "				p.id = ean.fkProduto) ean on\n" +
                        "		ean.id_produto = p.id\n" +
                        "	left join MultiLoja.Loja loja on\n" +
                        "		loja.id = @idLoja\n" +
                        "	left join CadProduto.ListaPreco lista on\n" +
                        "		lista.id = loja.fkListaPreco\n" +
                        "	left join CadProduto.ListaPrecoExcecao preco on\n" +
                        "		preco.fkProduto = p.id and\n" +
                        "		preco.fkListaPreco = lista.id\n" +
                        "	left join CadProduto.EstoqueMultiLoja e on\n" +
                        "		e.fkProduto = p.id and\n" +
                        "		e.fkLoja = loja.id and\n" +
                        "		e.fkCliente = @fkCliente\n" +
                        "	left join CadProduto.AliquotaICMS icms on \n" +
                        "		icms.id = p.aliqICMS and\n" +
                        "		icms.fkCliente = @fkCliente"
                )) {
                    int cont = 1;
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                    
                        imp.setImportSistema(getSistema());
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportId(rst.getString("id"));
                        imp.setEan(rst.getString("ean"));
                        imp.setTipoEmbalagem(rst.getString("unidade"));
                        
                        if (rst.getInt("balanca") == 1) {                            
                            String plu = String.valueOf(Utils.stringToLong(rst.getString("ean")));
                            
                            if (plu.startsWith("2") && plu.endsWith("0") && plu.length() == 6) {
                                imp.seteBalanca(true);
                                
                                imp.setEan(plu.substring(1, 5));
                                
                                if (rst.getInt("e_unitario_pesavel") == 1) {
                                    imp.setTipoEmbalagem("UN");
                                } else {
                                    imp.setTipoEmbalagem("KG");
                                }
                            } else {
                                imp.seteBalanca(false);
                            }
                        }
                        
                        imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                        imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                        imp.setDescricaoGondola(rst.getString("descricaogondola"));
                        imp.setCodMercadologico1(rst.getString("cod_mercadologico1"));
                        imp.setCodMercadologico2(rst.getString("cod_mercadologico2"));
                        imp.setIdFamiliaProduto(rst.getString("id_familiaproduto"));
                        imp.setPesoBruto(rst.getDouble("pesobruto"));
                        imp.setPesoLiquido(rst.getDouble("pesoliquido"));
                        imp.setDataCadastro(rst.getDate("datacadastro"));
                        imp.setValidade(rst.getInt("validade"));
                        imp.setMargem(rst.getDouble("margem"));
                        //imp.setEstoqueMaximo(rst.getDouble("estoquemaximo"));
                        imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                        imp.setEstoque(rst.getDouble("estoque"));
                        imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                        imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                        imp.setPrecovenda(rst.getDouble("precovenda"));
                        if (rst.getInt("ativo") == 0) {
                            imp.setSituacaoCadastro(SituacaoCadastro.EXCLUIDO);
                        } else {
                            imp.setSituacaoCadastro(SituacaoCadastro.ATIVO);
                        }
                        imp.setNcm(rst.getString("ncm"));
                        //imp.setCest(rst.getString("cest"));                     
                        
   
                        imp.setPiscofinsCstDebito(Utils.stringToInt(rst.getString("piscofins_cst_debito")));                        
                        imp.setPiscofinsCstCredito(Utils.stringToInt(rst.getString("piscofins_cst_credito")));                        
                        imp.setPiscofinsNaturezaReceita(Utils.stringToInt(rst.getString("piscofins_natureza_receita")));
                        
                        switch (rst.getInt("aliqICMS")) {
                            case 1: {
                                imp.setIcmsCst(60);
                                imp.setIcmsAliq(0);
                                imp.setIcmsReducao(0);
                            }; break;
                            case 2: {
                                imp.setIcmsCst(40);
                                imp.setIcmsAliq(0);
                                imp.setIcmsReducao(0);
                            }; break;
                            default: {
                                imp.setIcmsCst(Utils.stringToInt(rst.getString("icms_cst")));
                                imp.setIcmsAliq(Utils.stringToDouble(rst.getString("icms_aliq")));
                                imp.setIcmsReducao(Utils.stringToDouble(rst.getString("icms_reducao")));
                            }; break;
                        }
                        
                        ProgressBar.setStatus("Convertendo em IMP.... " + cont);
                        cont++;

                        result.add(imp);
                    }
                }
            }
            
            return result;
        }

        @Override
        public List<FornecedorIMP> getFornecedores() throws Exception {
            List<FornecedorIMP> result = new ArrayList<>();
            
            try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
                try (Statement stm2 = ConexaoSqlServer.getConexao().createStatement()) {                
                    try (ResultSet rst = stm.executeQuery(
                            "select\n" +
                            "	c.id,\n" +
                            "	c.razaoSocial,\n" +
                            "	c.nomeFantasia,\n" +
                            "	c.cnpj,\n" +
                            "	c.inscricaoEstadual,\n" +
                            "	c.inscricaoMunicipal,\n" +
                            "	null as suframa,\n" +
                            "	c.ativo,\n" +
                            "	ender.logradouro,\n" +
                            "	ender.numero,\n" +
                            "	ender.complemento,\n" +
                            "	ender.bairro,\n" +
                            "	ender.fkMunicipio,\n" +
                            "	ender.fkUf,\n" +
                            "	ender.cep,\n" +
                            "	c.bloqueado,\n" +
                            "	c.dtCadastro,\n" +
                            "	c.dtNascimento,\n" +
                            "	c.obs\n" +
                            "from\n" +
                            "	Cadastro.Entidade c\n" +
                            "	LEFT join Cadastro.Endereco ender on\n" +
                            "		c.id = ender.fkEntidade and\n" +
                            "		ender.id in (select max(id) id from Cadastro.Endereco group by fkEntidade)\n" +
                            "where \n" +
                            "	isFornecedor = 1 and\n" +
                            "	fkCliente = 1\n" +
                            "order by\n" +
                            "	id"
                    )) {
                        int cont = 1;
                        while (rst.next()) {
                            FornecedorIMP imp = new FornecedorIMP();

                            imp.setImportSistema(getSistema());
                            imp.setImportLoja(getLojaOrigem());
                            imp.setImportId(rst.getString("id"));
                            imp.setRazao(rst.getString("razaoSocial"));
                            imp.setFantasia(rst.getString("nomeFantasia"));
                            imp.setCnpj_cpf(rst.getString("cnpj"));
                            imp.setIe_rg(rst.getString("inscricaoEstadual"));
                            imp.setInsc_municipal(rst.getString("inscricaoMunicipal"));
                            imp.setAtivo(rst.getInt("bloqueado") == 0);

                            imp.setEndereco(rst.getString("logradouro"));
                            imp.setNumero(rst.getString("numero"));
                            imp.setComplemento(rst.getString("complemento"));
                            imp.setBairro(rst.getString("bairro"));
                            imp.setIbge_municipio(rst.getInt("fkMunicipio"));
                            imp.setIbge_uf(rst.getInt("fkUf"));
                            imp.setCep(rst.getString("cep"));

                            imp.setCob_endereco(rst.getString("logradouro"));
                            imp.setCob_numero(rst.getString("numero"));
                            imp.setCob_complemento(rst.getString("complemento"));
                            imp.setCob_bairro(rst.getString("bairro"));
                            imp.setCob_ibge_municipio(rst.getInt("fkMunicipio"));
                            imp.setCob_ibge_uf(rst.getInt("fkUf"));
                            imp.setCob_cep(rst.getString("cep"));


                            imp.setDatacadastro(rst.getTimestamp("dtCadastro"));
                            imp.setObservacao(rst.getString("obs"));                        

                            try (ResultSet rst2 = stm2.executeQuery(
                                    "select\n" +
                                    "	id,\n" +
                                    "	'(FONE) ' + case coalesce(ltrim(rtrim(tipo)), '') \n" +
                                    "		when '' then 'COMERCIAL' \n" +
                                    "		else upper(ltrim(rtrim(tipo))) \n" +
                                    "	end tipo,\n" +
                                    "	numero\n" +
                                    "from \n" +
                                    "	Cadastro.Fone\n" +
                                    "where\n" +
                                    "	fkEntidade = " + imp.getImportId()
                            )) {
                                boolean first = true;
                                while (rst2.next()) {
                                    if (first) {
                                        imp.setTel_principal(rst2.getString("numero"));
                                        first = false;
                                    }
                                    FornecedorContatoIMP contato = imp.getContatos()
                                            .make(getSistema(), 
                                                    getLojaOrigem(), 
                                                    rst2.getString("id")
                                            );
                                    contato.setTipoContato(TipoContato.COMERCIAL);
                                    contato.setImportSistema(getSistema());
                                    contato.setImportLoja(getLojaOrigem());
                                    contato.setImportId(rst2.getString("id"));
                                    contato.setNome(rst2.getString("tipo"));
                                    contato.setTelefone(rst2.getString("numero"));
                                }
                            }

                            try (ResultSet rst2 = stm2.executeQuery(
                                    "select\n" +
                                    "	id,\n" +
                                    "	'(EMAIL) ' + case coalesce(ltrim(rtrim(tipo)), '') \n" +
                                    "		when '' then 'COMERCIAL' \n" +
                                    "		else upper(ltrim(rtrim(tipo))) \n" +
                                    "	end tipo,\n" +
                                    "	endereco email\n" +
                                    "from \n" +
                                    "	Cadastro.Email\n" +
                                    "where\n" +
                                    "	fkEntidade = " + imp.getImportId()
                            )) {
                                while (rst2.next()) {
                                    FornecedorContatoIMP contato = imp.getContatos()
                                            .make(getSistema(), 
                                                    getLojaOrigem(), 
                                                    rst2.getString("id")
                                            );
                                    contato.setTipoContato(TipoContato.COMERCIAL);
                                    contato.setImportSistema(getSistema());
                                    contato.setImportLoja(getLojaOrigem());
                                    contato.setImportId(rst2.getString("id"));
                                    contato.setNome(rst2.getString("tipo"));
                                    contato.setEmail(rst2.getString("email"));
                                }
                            }

                            result.add(imp);
                            
                            ProgressBar.setStatus("Carregando fornecedores..." + cont);
                            cont++;
                        }
                    }
                }
            }
            
            return result;
        }

        @Override
        public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
            List<ProdutoFornecedorIMP> result = new ArrayList<>();
            
            try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select\n" +
                        "	r.fkProduto,\n" +
                        "	r.fkFornecedor,\n" +
                        "	r.sref,\n" +
                        "	r.tamEmb\n" +
                        "from\n" +
                        "	CadProduto.Referencia r\n" +
                        "	join CadProduto.Produto p on\n" +
                        "		r.fkProduto = p.id\n" +
                        "	join Cadastro.Entidade e on\n" +
                        "		e.isFornecedor = 1 and\n" +
                        "		e.id = r.fkFornecedor\n" +
                        "where\n" +
                        "	r.fkCliente = 1"
                )) {
                    while (rst.next()) {
                        ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                        
                        imp.setImportSistema(getSistema());
                        imp.setImportLoja(getLojaOrigem());
                        imp.setIdFornecedor(rst.getString("fkFornecedor"));
                        imp.setIdProduto(rst.getString("fkProduto"));
                        imp.setCodigoExterno(rst.getString("sref"));
                        imp.setQtdEmbalagem(rst.getInt("tamEmb"));
                        
                        result.add(imp);
                    }
                }
            }
            
            return result;
        }

        @Override
        public List<VendaHistoricoIMP> getHistoricoVenda() throws Exception {
            List<VendaHistoricoIMP> result = new ArrayList<>();
        
            try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
                String sql = 
                        "declare @data datetime;\n" +
                        "declare @loja int;\n" +
                        "\n" +
                        "set @data = cast('01/01/" + (Calendar.getInstance().get(Calendar.YEAR) - 2) + " 00:00:00' as datetime);\n" +
                        "set @loja = " + getLojaOrigem() + ";\n" +
                        "\n" +
                        "select\n" +
                        "	venda.fkLoja id_loja,\n" +
                        "	item.fkProduto id_produto,\n" +
                        "	venda.dtFim data,\n" +
                        "	cast(item.vlTotal / (case when coalesce(item.qtdadeReal, item.qtdade) = 0 then 1 else coalesce(item.qtdadeReal, item.qtdade) end) as numeric(18,4)) vlUnidade,\n" +
                        "	item.qtdadeReal quantidade,\n" +
                        "	1 id_comprador,\n" +
                        "	item.vlCustoMedioUnit custocomimposto,\n" +
                        "	item.vlCustoMedioUnit custosemimposto,\n" +
                        "	0 piscofins,\n" +
                        "	0 operacional,\n" +
                        "	0 icmscredito,\n" +
                        "	0 icmsdebito,\n" +
                        "	item.vlTotal valortotal,\n" +
                        "	item.emPromocao oferta,\n" +
                        "	0 perda,\n" +
                        "	item.vlCustoMedioUnit customediosemimposto,\n" +
                        "	item.vlCustoMedioUnit customediocomimposto,\n" +
                        "	0 piscofinscredito,\n" +
                        "	case when venda.coo != 0 then 1 else 0 end cupomfiscal\n" +
                        "from \n" +
                        "	Comercial.ItemVendido item\n" +
                        "	join Comercial.Venda venda on\n" +
                        "		item.fkVenda = venda.id\n" +
                        "where\n" +
                        "	venda.dtFim >= @data and\n" +
                        "	venda.fkLoja = @loja";
                try (ResultSet rst = stm.executeQuery(
                    sql
                )) {
                    int cont = 0, cont2 = 0;
                    while (rst.next()) {
                        
                        VendaHistoricoIMP imp = new VendaHistoricoIMP();
                        
                        imp.setIdProduto(rst.getString("id_produto"));
                        imp.setData(rst.getDate("data"));
                        imp.setPrecoVenda(rst.getDouble("vlUnidade"));
                        imp.setQuantidade(rst.getDouble("quantidade"));
                        imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                        imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                        imp.setValorTotal(rst.getDouble("valortotal"));
                        imp.setOferta(rst.getInt("oferta") == 1);
                        imp.setCupomFiscal(rst.getInt("cupomfiscal") == 1);
                        result.add(imp);
                        
                        cont++;      
                        cont2++;
                        if (cont2 >= 1000) {
                            cont2 = 0;
                            ProgressBar.setStatus("Preenchendo as vendas..." + cont);
                        }
                        
                    }
                }
            }

            return result;
        }
        
        
        
        

        @Override
        public String getSistema() {
            return "SUPER_SERVER";
        }
            
    };

    public InterfaceDAO getDao() {
        return dao;
    }

    @Override
    public List<ItemComboVO> carregarLojasCliente() throws Exception {
        List<ItemComboVO> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select id, descricaoLoja from MultiLoja.Loja order by id"
            )) {
                while (rst.next()) {
                    result.add(new ItemComboVO(rst.getInt("id"), rst.getString("descricaoLoja")));
                }
            }        
        }
        return result;
    } 

    @Override
    public List<ClientePreferencialVO> carregarCliente(int idLojaCliente) throws Exception {
        List<ClientePreferencialVO> vClientePreferencial = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (Statement stm2 = ConexaoSqlServer.getConexao().createStatement()) {
 
                MunicipioDAO municipioDAO = new MunicipioDAO();
                municipioDAO.atualizarMunicipios();
                
                try (ResultSet rst = stm.executeQuery(               
                    "select\n" +
                    "	c.id,\n" +
                    "	c.razaoSocial,\n" +
                    "	c.nomeFantasia,\n" +
                    "	c.cnpj,\n" +
                    "	c.inscricaoEstadual,\n" +
                    "	c.inscricaoMunicipal,\n" +
                    "	null as suframa,\n" +
                    "	c.ativo,\n" +
                    "	ender.logradouro,\n" +
                    "	ender.numero,\n" +
                    "	ender.complemento,\n" +
                    "	ender.bairro,\n" +
                    "	ender.fkMunicipio,\n" +
                    "	ender.fkUf,\n" +
                    "	ender.cep,\n" +
                    "	c.bloqueado,\n" +
                    "	c.dtCadastro,\n" +
                    "	c.dtNascimento,\n" +
                    "	c.obs,\n" +
                    "	c.ativo,\n" +
                    "	cr.liberadoRotativo,\n" +
                    "	cr.vlRotativoTotal,\n" +
                    "	cr.vlRotativoUsado,\n" +
                    "	cr.vlSalario,\n" +
                    "	cr.empresa,\n" +
                    "	cr.cargo,\n" +
                    "	cr.pai,\n" +
                    "	cr.mae,\n" +
                    "	cr.diaVencimento\n" +
                    "from\n" +
                    "	Cadastro.Entidade c\n" +
                    "	LEFT join Cadastro.Endereco ender on\n" +
                    "		c.id = ender.fkEntidade and\n" +
                    "		ender.id in (select max(id) id from Cadastro.Endereco group by fkEntidade)\n" +
                    "	join CRM.Cadastro cr on\n" +
                    "		cr.fkEntidade = c.id\n" +
                    "where \n" +
                    "	c.isCliente = 1 and\n" +
                    "	c.fkCliente = 1\n" +
                    "order by\n" +
                    "	c.id"
                )) {
                    while (rst.next()) {                    
                        ClientePreferencialVO oClientePreferencial = new ClientePreferencialVO();

                        oClientePreferencial.setId(rst.getInt("id"));
                        oClientePreferencial.setCodigoanterior(rst.getInt("id"));
                        oClientePreferencial.setNome(rst.getString("razaoSocial"));
                        oClientePreferencial.setEndereco(rst.getString("logradouro"));
                        oClientePreferencial.setNumero(rst.getString("numero"));
                        oClientePreferencial.setComplemento(rst.getString("complemento"));
                        oClientePreferencial.setBairro(rst.getString("bairro"));
                        MunicipioVO municipio = municipioDAO.getMunicipio(rst.getInt("fkMunicipio"));
                        if (municipio == null) {
                            municipio = Parametros.get().getMunicipioPadrao2();
                        }
                        oClientePreferencial.setId_municipio(municipio.getId());
                        oClientePreferencial.setId_estado(municipio.getEstado().getId());
                        oClientePreferencial.setCep(rst.getString("cep"));
                        oClientePreferencial.setInscricaoestadual(rst.getString("inscricaoEstadual"));
                        oClientePreferencial.setCnpj(rst.getString("cnpj"));
                        if (String.valueOf(oClientePreferencial.getCnpj()).length() < 8) {
                            oClientePreferencial.setCnpj(-1);
                        }
                        oClientePreferencial.setDataresidencia("1990/01/01");
                        oClientePreferencial.setDatacadastro(rst.getDate("dtCadastro"));
                        oClientePreferencial.setBloqueado(rst.getBoolean("bloqueado"));
                        oClientePreferencial.setId_situacaocadastro(rst.getInt("ativo"));
                        oClientePreferencial.setId_situacaocadastro(1);
                        oClientePreferencial.setObservacao("IMPORTADO VR " + rst.getString("obs"));
                        oClientePreferencial.setObservacao2("");
                        oClientePreferencial.setDatanascimento(rst.getDate("dtNascimento"));
                        oClientePreferencial.setId_tipoinscricao(String.valueOf(oClientePreferencial.getCnpj()).length() > 11 ? 0 : 1); 
                        oClientePreferencial.setPermitecreditorotativo(rst.getInt("liberadoRotativo") == 1);
                        oClientePreferencial.setValorlimite(rst.getDouble("vlRotativoTotal"));
                        oClientePreferencial.setSalario(rst.getDouble("vlSalario"));
                        oClientePreferencial.setEmpresa(rst.getString("empresa"));
                        oClientePreferencial.setCargo(rst.getString("cargo"));
                        oClientePreferencial.setNomepai(rst.getString("pai"));
                        oClientePreferencial.setNomemae(rst.getString("mae"));
                        oClientePreferencial.setVencimentocreditorotativo(rst.getInt("diaVencimento"));
                        
                        
                        String observacao2 = "";
                        try (ResultSet rst2 = stm2.executeQuery(
                                "select\n" +
                                "	id,\n" +
                                "	'(FONE) ' + case coalesce(ltrim(rtrim(tipo)), '') \n" +
                                "		when '' then 'COMERCIAL' \n" +
                                "		else upper(ltrim(rtrim(tipo))) \n" +
                                "	end tipo,\n" +
                                "	numero\n" +
                                "from \n" +
                                "	Cadastro.Fone\n" +
                                "where\n" +
                                "	fkEntidade = " + oClientePreferencial.getId()
                        )) {                            
                            boolean isTelefone1Adicionado = false;
                            boolean isTelefone2Adicionado = false;
                            boolean isTelefone3Adicionado = false;
                            boolean isCelularAdicionado = false;
                            
                            while (rst2.next()) {
                                String fone = Utils.formataNumero(rst2.getString("numero"),"");
                                
                                if (fone.length() > 10) {
                                    if (isCelularAdicionado) {
                                        observacao2 += ("".equals(observacao2) ? "" : "\n");
                                        observacao2 += "CELULAR " + rst2.getString("numero");
                                    } else {
                                        isCelularAdicionado = true;
                                        oClientePreferencial.setCelular(rst2.getString("numero"));
                                    }
                                } else {
                                    if (!isTelefone1Adicionado) {
                                        isTelefone1Adicionado = true;
                                        oClientePreferencial.setTelefone(rst2.getString("numero"));
                                    } else if (!isTelefone2Adicionado)  {
                                        isTelefone2Adicionado = true;
                                        oClientePreferencial.setTelefone2(rst2.getString("numero"));
                                    } else if (!isTelefone3Adicionado)  {
                                        isTelefone3Adicionado = true;
                                        oClientePreferencial.setTelefone3(rst2.getString("numero"));
                                    } else {
                                        observacao2 += ("".equals(observacao2) ? "" : "\n");
                                        observacao2 += "TELEFONE " + rst2.getString("numero");
                                    }
                                }
                            }
                        }

                        try (ResultSet rst2 = stm2.executeQuery(
                                "select\n" +
                                "	id,\n" +
                                "	'(EMAIL) ' + case coalesce(ltrim(rtrim(tipo)), '') \n" +
                                "		when '' then 'COMERCIAL' \n" +
                                "		else upper(ltrim(rtrim(tipo))) \n" +
                                "	end tipo,\n" +
                                "	endereco email\n" +
                                "from \n" +
                                "	Cadastro.Email\n" +
                                "where\n" +
                                "	fkEntidade = " + oClientePreferencial.getId()
                        )) {
                            boolean adicionado = false;
                            while (rst2.next()) {
                                if (!adicionado)  {
                                    adicionado = true;
                                    oClientePreferencial.setEmail(rst2.getString("email"));
                                } else {
                                    observacao2 += ("".equals(observacao2) ? "" : "\n");
                                    observacao2 += "EMAIL " + rst2.getString("email");
                                }
                            }
                        }
                        oClientePreferencial.setObservacao2(observacao2);

                        vClientePreferencial.add(oClientePreferencial);
                    }                
                }
            }
        }
        return vClientePreferencial;
    }

    @Override
    public List<ReceberCreditoRotativoVO> carregarReceberCreditoRotativo(int idLojaVR, int idLojaCliente) throws Exception {
        List<ReceberCreditoRotativoVO> result = new ArrayList<>();
        
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try ( ResultSet rst = stm.executeQuery(
                    "select \n" +
                    "	vp.fkLoja,\n" +
                    "	vp.fkEntidade,\n" +
                    "	vp.fkPdv,\n" +
                    "	v.nroECF,\n" +
                    "	e.cnpj,\n" +
                    "	vp.dtVenda,\n" +
                    "	vp.dtVenda + 30 dtVencimento,\n" +
                    "	v.coo,\n" +
                    "	vp.valorVenda,\n" +
                    "	vp.valorPago,\n" +
                    "	vp.valorJuros\n" +
                    "from \n" +
                    "	Comercial.VendaPrazo vp\n" +
                    "	join CRM.Cadastro cd on\n" +
                    "		vp.fkEntidade = cd.fkEntidade\n" +
                    "	join Cadastro.Entidade e on\n" +
                    "		cd.fkEntidade = e.id\n" +
                    "	join Comercial.Venda v on\n" +
                    "		v.id = vp.fkVenda\n" +
                    "WHERE \n" +
                    "	vp.fkLoja = " + idLojaCliente + " and\n" +
                    "	vp.valorVenda > vp.valorPago"
            )) {
                while (rst.next()) {
                    ReceberCreditoRotativoVO rotativo = new ReceberCreditoRotativoVO();

                    rotativo.setId_clientepreferencial(rst.getInt("fkEntidade"));
                    rotativo.setCnpjCliente(Utils.stringToLong(rst.getString("cnpj")));
                    rotativo.setId_loja(idLojaVR);
                    rotativo.setDataemissao(rst.getDate("dtVenda"));
                    rotativo.setNumerocupom(Utils.stringToInt(rst.getString("coo")));
                    rotativo.setValor(rst.getDouble("valorVenda"));
                    rotativo.setValorjuros(rst.getDouble("valorJuros"));
                    rotativo.setValorPago(rst.getDouble("valorPago"));
                    if (rotativo.getValorPago() > 0) {
                        rotativo.setDataPagamento(rst.getDate("dtVenda"));
                    }
                    rotativo.setObservacao("IMPORTADO VR");
                    rotativo.setDatavencimento(rst.getDate("dtVencimento"));
                    rotativo.setEcf(rst.getInt("nroECF"));

                    result.add(rotativo);
                }
            }            
        }
        
        return result;
    }
    
    
}