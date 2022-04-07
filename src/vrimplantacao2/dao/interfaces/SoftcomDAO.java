package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vr.core.parametro.versao.Versao;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.ProdutoAnteriorDAO;
import vrimplantacao2.dao.cadastro.produto.ProdutoAutomacaoDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.utils.MathUtils;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Leandro
 */
public class SoftcomDAO extends InterfaceDAO implements MapaTributoProvider {

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select    \n"
                    + "    	Registro id,    \n"
                    + "    	cast(l.Registro as varchar(10)) + ' - ' + l.CGC + ' - ' + l.Fantasia descricao    \n"
                    + "    from     \n"
                    + "    	Integrar_Lojas l    \n"
                    + "    order by    \n"
                    + "    	1"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("id"), rst.getString("descricao")));
                }
            }
        }

        return result;
    }

    @Override
    public String getSistema() {
        return "Softcom";
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	p.[Código da Mercadoria] id,\n"
                    + "	p.DataCadastro,\n"
                    + "	case\n"
                    + "	when p.Balanca = 1 and p.Medida = 'KG' then 'SEM GTIN' else ean.ean end ean,\n"
                    + "	1 qtdEmbalagem,\n"
                    + "	p.Medida unidade,\n"
                    + "	p.Balanca as e_balanca,--p.Balanca,\n"
                    + "	p.validadedias validade,\n"
                    + "	p.Mercadoria descricaocompleta,\n"
                    + "	p.Grupo cod_mercadologico1,\n"
                    + "	p.SubGrupo cod_mercadologico2,\n"
                    + "	p.Peso,\n"
                    + "	p.[Estoque Mínimo] estoqueminimo,\n"
                    + "	p.[Unidades em Estoque] estoque,\n"
                    + "	p.[Margem Lucro] margem,\n"
                    + "	round(p.[Preço C], 2, 1) custocomimposto,\n"
                    + "	p.[Preço Compra] custosemimposto,\n"
                    + "	p.[Preço de Venda] precovenda,\n"
                    + "	case p.Desativado when 1 then 0 else 1 end situacaocadastro,\n"
                    + "	p.NCM ncm,\n"
                    + "	nullif(rtrim(ltrim(coalesce(p.cCEST,''))),'') cest,\n"
                    + "	pisdebito.CSTPIS piscofins_debito,\n"
                    + "	piscredito.CSTPIS piscofins_credito,\n"
                    + "	p.NaturezaReceita piscofins_naturezareceita,\n"
                    + "	p.Situação icms_id,\n"
                    + "	p.VendaA preco_atacado\n"
                    + "from\n"
                    + "	[Cadastro de Mercadorias] p\n"
                    + "join (\n"
                    + "		select\n"
                    + "			[Código da Mercadoria] id,\n"
                    + "			PAF_Codigo ean\n"
                    + "		from\n"
                    + "			[Cadastro de Mercadorias]\n"
                    + "		where\n"
                    + "			not PAF_Codigo is null\n"
                    + "		union\n"
                    + "		select\n"
                    + "			[Código da Mercadoria] id,\n"
                    + "			[Cód Barra] ean\n"
                    + "		from\n"
                    + "			[Cadastro de Mercadorias]\n"
                    + "		where\n"
                    + "			not [Cód Barra] is null\n"
                    + "	) ean on p.[Código da Mercadoria] = ean.id\n"
                    + "	left join NFe_PIS pisdebito on\n"
                    + "		pisdebito.ID = p.TipoPIS\n"
                    + "	left join NFe_PIS piscredito on\n"
                    + "		piscredito.ID = p.TipoPISEntrada\n"
                    + "where\n"
                    + "	not p.Mercadoria is null \n"
                    + "	ORDER BY 1"
            /*"select\n" +
                    "	p.[Código da Mercadoria] id,\n" +
                    "	p.DataCadastro,\n" +
                    "	ean.ean ean,\n" +
                    "	1 qtdEmbalagem,\n" +
                    "	p.Medida unidade,\n" +
                    "	p.Balanca as e_balanca,--p.Balanca,\n" +
                    "	p.validadedias validade,\n" +
                    "	p.Mercadoria descricaocompleta,\n" +
                    "	p.Grupo cod_mercadologico1,\n" +
                    "	p.SubGrupo cod_mercadologico2,\n" +
                    "	p.Peso,\n" +
                    "	p.[Estoque Mínimo] estoqueminimo,\n" +
                    "	p.[Unidades em Estoque] estoque,\n" +
                    "	p.[Margem Lucro] margem,\n" +
                    "	round(p.[Preço C], 2, 1) custocomimposto,\n" +
                    "	p.[Preço Compra] custosemimposto,\n" +
                    "	p.[Preço de Venda] precovenda,\n" +
                    "	case p.Desativado when 1 then 0 else 1 end situacaocadastro,\n" +
                    "	p.NCM ncm,\n" +
                    "	nullif(rtrim(ltrim(coalesce(p.cCEST,''))),'') cest,\n" +
                    "	pisdebito.CSTPIS piscofins_debito,\n" +
                    "	piscredito.CSTPIS piscofins_credito,\n" +
                    "	p.NaturezaReceita piscofins_naturezareceita,\n" +
                    "	p.Situação icms_id,\n" +
                    "	p.VendaA preco_atacado\n" +
                    //"	cast(p.CST as varchar(10)) + '-' + cast(p.NFCe_Aliquota as varchar(10)) icms_id\n" +                    
                    "from\n" +
                    "	[Cadastro de Mercadorias] p\n" +
                    "join (\n" +
                    "		select\n" +
                    "			[Código da Mercadoria] id,\n" +
                    "			PAF_Codigo ean\n" +
                    "		from\n" +
                    "			[Cadastro de Mercadorias]\n" +
                    "		where\n" +
                    "			not PAF_Codigo is null\n" +
                    "		union\n" +
                    "		select\n" +
                    "			[Código da Mercadoria] id,\n" +
                    "			[Cód Barra] ean\n" +
                    "		from\n" +
                    "			[Cadastro de Mercadorias]\n" +
                    "		where\n" +
                    "			not [Cód Barra] is null\n" +
                    "	) ean on p.[Código da Mercadoria] = ean.id\n" +
                    "	left join NFe_PIS pisdebito on\n" +
                    "		pisdebito.ID = p.TipoPIS\n" +
                    "	left join NFe_PIS piscredito on\n" +
                    "		piscredito.ID = p.TipoPISEntrada\n" +
                    "where\n" +
                    "	not p.Mercadoria is null"*/
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setDataCadastro(rst.getDate("DataCadastro"));
                    imp.setEan(rst.getString("ean"));
                    imp.setQtdEmbalagem(rst.getInt("qtdEmbalagem"));
                    imp.setTipoEmbalagem(rst.getString("unidade"));
                    imp.seteBalanca(rst.getBoolean("e_balanca"));
                    imp.setValidade(rst.getInt("validade"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoGondola(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaocompleta"));
                    imp.setCodMercadologico1(rst.getString("cod_mercadologico1"));
                    imp.setCodMercadologico2(rst.getString("cod_mercadologico2"));
                    imp.setPesoBruto(rst.getDouble("Peso"));
                    imp.setPesoLiquido(rst.getDouble("Peso"));
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setSituacaoCadastro(rst.getInt("situacaocadastro") == 0 ? SituacaoCadastro.EXCLUIDO : SituacaoCadastro.ATIVO);
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getString("piscofins_debito"));
                    imp.setPiscofinsCstCredito(rst.getString("piscofins_credito"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("piscofins_naturezareceita"));
                    imp.setIcmsCreditoId(rst.getString("icms_id"));
                    imp.setIcmsDebitoId(rst.getString("icms_id"));
                    imp.setAtacadoPreco(rst.getDouble("preco_atacado"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select distinct Situação id from [Cadastro de Mercadorias] order by 1"
            )) {
                while (rst.next()) {
                    result.add(
                            new MapaTributoIMP(rst.getString("id"), rst.getString("id"))
                    );
                }
            }
        }

        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	f.[Código do Fornecedor] id,\n"
                    + "	f.Fornecedor razao,\n"
                    + "	f.Fantasia,\n"
                    + "	f.[CGC/CPF] cnpj,\n"
                    + "	f.[Insc Estadual] ie,\n"
                    + "	f.InscricaoMunicipal inscmun,\n"
                    + "	f.[Endereço] endereco,\n"
                    + //"	f.Num numero,\n" +
                    "	null numero,\n"
                    + "	f.Bairro,\n"
                    + "	f.Cidade,\n"
                    + "	f.UF,\n"
                    + "	f.cCidade ibge_mun,\n"
                    + "	f.CEP,\n"
                    + "	f.Fone1,\n"
                    + "	coalesce(f.Fone2,'') fone2,\n"
                    + "	coalesce(f.Fones,'') fones,\n"
                    + "	f.DataCadastro,\n"
                    + "	f.Observações obs,\n"
                    + "	f.[E-mail] email\n"
                    + "from\n"
                    + "	Fornecedores f\n"
                    + "order by\n"
                    + "	f.[Código do Fornecedor]"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("Fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("ie"));
                    imp.setInsc_municipal(rst.getString("inscmun"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setBairro(rst.getString("Bairro"));
                    imp.setMunicipio(rst.getString("Cidade"));
                    imp.setUf(rst.getString("UF"));
                    imp.setIbge_municipio(rst.getInt("ibge_mun"));
                    imp.setCep(rst.getString("CEP"));
                    imp.setTel_principal(rst.getString("Fone1"));
                    imp.setDatacadastro(rst.getDate("DataCadastro"));
                    imp.setObservacao(rst.getString("obs"));

                    String fone2 = Utils.formataNumero(rst.getString("fone2"));
                    if (!"".equals(fone2)) {
                        imp.addContato("F2", "FONE2", fone2, "", TipoContato.COMERCIAL, null);
                    }
                    String fones = Utils.formataNumero(rst.getString("fones"));
                    if (!"".equals(fones)) {
                        imp.addContato("F3", "FONE3", fones, "", TipoContato.COMERCIAL, null);
                    }
                    String email = Utils.acertarTexto(rst.getString("email"));
                    if (!"".equals(email)) {
                        imp.addContato("EMAIL", "EMAIL", "", "", TipoContato.COMERCIAL, email);
                    }

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	c.[Código do Cliente] id,\n"
                    + "	c.CGC cnpj,\n"
                    + "	c.[Inscrição Estadual] inscricaoestadual,\n"
                    + "	c.[Razão Social] razao,\n"
                    + "	c.[Nome do Cliente] fantasia,\n"
                    + "	case when c.Desativado = 0 then 1 else 0 end ativo,\n"
                    + "	coalesce(c.[Bloquear Cliente], 0) bloquear,\n"
                    + "	c.Endereço endereco,\n"
                    + "	c.Num numero,\n"
                    + "	c.[Ponto de Referência] complemento,\n"
                    + "	c.Bairro bairro,\n"
                    + "	c.Cidade municipio,\n"
                    + "	c.UF uf,\n"
                    + "	c.cCidade municipio_ibge,\n"
                    + "	c.CEP cep,\n"
                    + "	c.Datanasc datanascimento,\n"
                    + "	c.DataCadastro datacadastro,\n"
                    + "	c.LocalTrabalho empresa,\n"
                    + "	c.Renda salario,\n"
                    + "	c.[Limite Crédito] limite,\n"
                    + "	c.Conjuge,\n"
                    + "	c.Pai,\n"
                    + "	c.Mae,\n"
                    + "	c.[Observações] obs,\n"
                    + "	c.RG,\n"
                    + "	c.[Fone Resid] fone1,\n"
                    + "	c.fonecob cob_telefone,\n"
                    + "	c.endcob cob_endereco,\n"
                    + "	c.bairrocob cob_bairro,\n"
                    + "	c.cidcob cob_cidade,\n"
                    + "	c.ufcob cob_uf,\n"
                    + "	c.cepcob cob_cep\n"
                    + "from\n"
                    + "	[Cadastro de Clientes] c\n"
                    + "order by\n"
                    + "	id"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rst.getString("id"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setInscricaoestadual(rst.getString("inscricaoestadual"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setAtivo(rst.getBoolean("ativo"));
                    imp.setBloqueado(rst.getBoolean("bloquear"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setUf(rst.getString("uf"));
                    imp.setMunicipioIBGE(rst.getInt("municipio_ibge"));
                    imp.setCep(rst.getString("cep"));
                    imp.setDataNascimento(rst.getDate("datanascimento"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setEmpresa(rst.getString("empresa"));
                    imp.setSalario(rst.getDouble("salario"));
                    imp.setValorLimite(rst.getDouble("limite"));
                    imp.setNomeConjuge(rst.getString("Conjuge"));
                    imp.setNomePai(rst.getString("Pai"));
                    imp.setNomeMae(rst.getString("Mae"));
                    imp.setObservacao2(rst.getString("obs"));
                    imp.setObservacao(rst.getString("RG"));
                    imp.setTelefone(rst.getString("fone1"));
                    imp.setCobrancaTelefone(rst.getString("cob_telefone"));
                    imp.setCobrancaEndereco(rst.getString("cob_endereco"));
                    imp.setCobrancaBairro(rst.getString("cob_bairro"));
                    imp.setCobrancaMunicipio(rst.getString("cob_cidade"));
                    imp.setCobrancaUf(rst.getString("cob_uf"));
                    imp.setCobrancaCep(rst.getString("cob_cep"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    public void importarAtacadoPorEAN(int lojaVR) throws Exception {
        ProgressBar.setStatus("Preparando para gravar atacado...");
        Versao versao = Versao.createFromConnectionInterface(Conexao.getConexao());
        Map<String, Integer> anteriores = new ProdutoAnteriorDAO().getAnteriores(getSistema(), getLojaOrigem());
        Map<Long, Integer> eans = new ProdutoAutomacaoDAO().getEansCadastrados();
        Set<Long> atac = new ProdutoAutomacaoDAO().getEansCadastradosAtacado(lojaVR);

        Conexao.begin();
        try {
            List<ProdutoIMP> prods = getProdutos();
            ProgressBar.setStatus("Gravando atacado...");
            ProgressBar.setMaximum(prods.size());
            for (ProdutoIMP imp : prods) {
                Integer id = anteriores.get(imp.getImportId());

                if (id != null) {
                    if (!atac.contains(id.longValue())) {
                        double precoAtacado = imp.getAtacadoPreco();
                        double precoVenda;
                        int qtd = 10;//imp.getQtdEmbalagem();
                        long ean = id.longValue();

                        try (Statement stm = Conexao.createStatement()) {

                            try (ResultSet rst = stm.executeQuery(
                                    "select precovenda from produtocomplemento where id_loja = " + lojaVR + " and id_produto = " + id
                            )) {
                                rst.next();
                                precoVenda = rst.getDouble("precovenda");
                            }

                            if (!eans.containsKey(ean)) {
                                stm.execute("insert into produtoautomacao (id_produto, codigobarras, qtdembalagem, id_tipoembalagem, pesobruto, dun14) values (" + id + ", " + ean + ", " + qtd + ", 0, 0, false)");
                                eans.put(ean, id);
                            }
                            if (precoVenda != precoAtacado) {
                                double desconto = MathUtils.round(100 - ((imp.getAtacadoPreco() / (imp.getPrecovenda() == 0 ? 1 : imp.getPrecovenda())) * 100), 2);
                                if (versao.igualOuMenorQue(3, 18)) {
                                    stm.execute("insert into produtoautomacaoloja (codigobarras, precovenda, id_loja) values (" + ean + ", " + precoAtacado + ", " + lojaVR + ")");
                                    stm.execute("insert into produtoautomacaodesconto (codigobarras, id_loja, desconto) values (" + ean + ", " + lojaVR + ", " + desconto + ")");
                                } else {
                                    stm.execute("insert into produtoautomacaodesconto (codigobarras, id_loja, desconto, descontodiaanterior, descontodiaseguinte, dataultimodesconto) values (" + ean + ", " + lojaVR + ", " + String.format("%.2f", desconto) + ", 0, " + String.format("%.2f", desconto) + ", now())");
                                }
                                atac.add(ean);
                            }
                        }
                    }
                }

                ProgressBar.next();
            }

            Conexao.commit();
        } catch (Exception e) {
            Conexao.rollback();
            throw e;
        }
    }

}
