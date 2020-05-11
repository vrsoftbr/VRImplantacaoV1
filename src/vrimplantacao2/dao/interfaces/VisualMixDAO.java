/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.mercadologico.MercadologicoNivelIMP;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoEstadoCivil;
import vrimplantacao2.vo.enums.TipoFornecedor;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Lucas
 */
public class VisualMixDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "VisualMix";
    }

    public List<Estabelecimento> getLojaCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	codigo, \n"
                    + "	descricao\n"
                    + "from dbo.Empresas_CAP\n"
                    + "order by 1"
            )) {
                while (rs.next()) {
                    result.add(new Estabelecimento(rs.getString("codigo"), rs.getString("descricao")));
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
                    "select \n"
                    + "	al.CODIGO as id, \n"
                    + "	al.DESCRICAO as descricao, "
                    + "	al.SITUACAOTRIBUTARIA as cst,\n"
                    + " al.PERCENTUAL as aliquota, \n"
                    + "	al.REDUCAO as reducao \n"
                    + "from dbo.Aliquotas_NF al\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(
                            rst.getString("id"),
                            rst.getString("descricao"),
                            rst.getInt("cst"),
                            rst.getDouble("aliquota"),
                            rst.getDouble("reducao")
                    ));
                }
            }
        }
        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "f.Codigo, "
                    + "f.Descricao "
                    + "from dbo.Grupo_Precos f "
                    + "order by Codigo"
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("Codigo"));
                    imp.setDescricao(rst.getString("Descricao"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoNivelIMP> getMercadologicoPorNivel() throws Exception {
        Map<String, MercadologicoNivelIMP> merc = new LinkedHashMap<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {

            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "Mercadologico1 as merc1, "
                    + "Descricao as descricao \n"
                    + "from dbo.Mercadologicos\n"
                    + "where Nivel = 1\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP imp = new MercadologicoNivelIMP();

                    imp.setId(rst.getString("merc1"));
                    imp.setDescricao(rst.getString("descricao"));

                    merc.put(imp.getId(), imp);
                }
            }

            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "Mercadologico1 as merc1, \n"
                    + "Mercadologico2 as merc2, \n"
                    + "Descricao as descricao \n"
                    + "from dbo.Mercadologicos\n"
                    + "where Nivel = 2\n"
                    + "order by 1, 2"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP merc2 = merc.get(rst.getString("merc1"));
                    if (merc2 != null) {
                        merc2.addFilho(
                                rst.getString("merc2"),
                                rst.getString("descricao")
                        );
                    }
                }
            }

            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	Mercadologico1 as merc1,\n"
                    + "	Mercadologico2 as merc2, \n"
                    + "	Mercadologico3 as merc3, \n"
                    + "	Descricao as descricao \n"
                    + "from dbo.Mercadologicos\n"
                    + "where Nivel = 3\n"
                    + "order by 1, 2, 3"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP merc1 = merc.get(rst.getString("merc1"));
                    if (merc1 != null) {
                        MercadologicoNivelIMP merc2 = merc1.getNiveis().get(rst.getString("merc2"));
                        if (merc2 != null) {
                            merc2.addFilho(
                                    rst.getString("merc3"),
                                    rst.getString("descricao")
                            );
                        }
                    }
                }
            }
        }
        return new ArrayList<>(merc.values());
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	p.Produto_Id as id,\n"
                    + "	ean.Codigo_Automacao,\n"
                    + "	ean.Digito_Automacao,\n"
                    + " p.Peso_Variavel,\n"
                    + " p.Pre_Pesado,\n"
                    + " p.Qtd_Decimal,\n"
                    + " p.ProdutoPai,\n"
                    + "	p.Descricao_Completa as descricaocompleta, \n"
                    + " p.Descricao_Reduzida as descricaoreduzida, \n"
                    + " p.Descricao_Balanca,\n"
                    + "	est.Custo_Ultima_Entrada_Com_Icms as custocomimposto,\n"
                    + " est.Custo_Ultima_Entrada_Sem_Icms as custosemimposto,\n"
                    + "	pre.preco_venda as precovenda,\n"
                    + " p.Margem_Atacado, \n"
                    + " p.Margem_Teorica, \n"
                    + " p.MargemFixa, \n"
                    + " p.Aliquota, \n"
                    + " p.Aliquota_FCP, \n"
                    + " p.Aliquota_Interna, \n"
                    + " p.Aliquota_NF,\n"
                    + " f.Codigo as idfamiliaproduto,\n"
                    + "	p.Mercadologico1, \n"
                    + " p.Mercadologico2, \n"
                    + " p.Mercadologico3, \n"
                    + " p.Mercadologico4, \n"
                    + " p.Mercadologico5, \n"
                    + " p.Situacao as situacaocadastro,\n"
                    + "	p.SituacaoTributaria as csticms, \n"
                    + " est.EstoqueInicial as estoque, \n"
                    + " p.Estoque_Minimo, \n"
                    + " p.Estoque_Maximo, \n"
                    + " p.EspecUnitariaTipo as tipoembalagem, \n"
                    + " p.EspecUnitariaQtde as qtdembalagem,\n"
                    + "	p.TipoProduto, \n"
                    + " p.Codigo_NCM as ncm, \n"
                    + " p.CEST as cest, \n"
                    + " p.TipoCodMercad as tipomercadoria,\n"
                    + "	p.CstPisCofinsEntrada, \n"
                    + " p.CstPisCofinsSaida, \n"
                    + " p.NaturezaReceita,\n"
                    + " p.Fabricante as idfabricante\n"
                    + "from dbo.Produtos p\n"
                    + "left join dbo.Precos_Loja pre on pre.produto_id = p.Produto_Id\n"
                    + "	and pre.loja = " + getLojaOrigem() + " and pre.sequencia = 1\n"
                    + "left join dbo.Produtos_Estoque est on est.Produto_Id = p.Produto_Id\n"
                    + "	and est.Loja = " + getLojaOrigem() + "\n"
                    + "left join dbo.Automacao ean on ean.Produto_Id = p.Produto_Id\n"
                    + "left join dbo.Grupo_Precos_Produtos f on f.Produto_Id = p.Produto_Id\n"
                    + "order by p.Produto_Id"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("Codigo_Automacao") + rst.getString("Digito_Automacao"));

                    if ((rst.getString("Descricao_Balanca") != null)
                            && (!rst.getString("Descricao_Balanca").trim().isEmpty())) {
                        imp.seteBalanca(true);
                    } else {
                        imp.seteBalanca(false);
                    }

                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    imp.setQtdEmbalagemCotacao(rst.getInt("qtdembalagem"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setIdFamiliaProduto(rst.getString("idfamiliaproduto"));
                    imp.setCodMercadologico1(rst.getString("Mercadologico1"));
                    imp.setCodMercadologico2(rst.getString("Mercadologico2"));
                    imp.setCodMercadologico3(rst.getString("Mercadologico3"));
                    imp.setFornecedorFabricante(rst.getString("idfabricante"));
                    imp.setMargem(rst.getDouble("Margem_Teorica"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setEstoqueMinimo(rst.getDouble("Estoque_Minimo"));
                    imp.setEstoqueMaximo(rst.getDouble("Estoque_Maximo"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getString("CstPisCofinsSaida"));
                    imp.setPiscofinsCstCredito(rst.getString("CstPisCofinsEntrada"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("NaturezaReceita"));
                    imp.setIcmsDebitoId(rst.getString("Aliquota_NF"));
                    imp.setIcmsCreditoId(rst.getString("Aliquota_NF"));
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
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	f.Codigo as id,\n"
                    + " f.Tipo,\n"
                    + " tf.Descricao as tipofornecedor,\n"
                    + " f.RazaoSocial as razao,\n"
                    + " f.NomeFantasia as fantasia,\n"
                    + "	f.TipoLogradouro as logradouro,\n"
                    + " f.Endereco,\n"
                    + " f.NumeroEnd as numero,\n"
                    + " f.Complemento,\n"
                    + " f.Bairro,\n"
                    + " f.Cidade as municipio,\n"
                    + " f.Estado as uf,\n"
                    + "	f.Cep,\n"
                    + " f.CxPostal as caixapostal,\n"
                    + " f.Telefone,\n"
                    + " f.Fax,\n"
                    + " f.Telex,\n"
                    + " f.TeleContato,\n"
                    + " f.Contato,\n"
                    + "	f.CGC as cnpj,\n"
                    + " f.InscricaoEstadual as ie,\n"
                    + " f.InscrMunicipal as im,\n"
                    + " f.PrazoEntrega,\n"
                    + " f.DataCadastro,\n"
                    + "	f.CondicaoPagto,\n"
                    + " cp.Descricao as condicaopagamento,\n"
                    + " cp.Qtd_Parcelas,\n"
                    + " f.Observacao,\n"
                    + "	f.Supervisor,\n"
                    + " f.CelSupervisor,\n"
                    + " f.EmailSupervisor,\n"
                    + " f.TelSupervisor,\n"
                    + " f.Email,\n"
                    + " f.Vendedor,\n"
                    + " f.TelVendedor,\n"
                    + " f.CelVendedor,\n"
                    + "	f.EmailVendedor,\n"
                    + " f.Gerente,\n"
                    + " f.TelGerente,\n"
                    + " f.CelGerente,\n"
                    + " f.EmailGerente,\n"
                    + "	f.Situacao,\n"
                    + " f.Status\n"
                    + "from dbo.Fornecedores f\n"
                    + "left join dbo.Condicoes_Pagto cp on cp.Codigo = f.CondicaoPagto\n"
                    + "left join dbo.TipoFornecedor tf on tf.Tipo = f.Tipo\n"
                    + "order by f.Codigo"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("ie"));
                    imp.setInsc_municipal(rst.getString("im"));

                    if ((rst.getString("Endereco") != null)
                            && (!rst.getString("Endereco").trim().isEmpty())) {
                        imp.setEndereco(rst.getString("logradouro") + " " + rst.getString("Endereco"));
                    }

                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("Complemento"));
                    imp.setBairro(rst.getString("Bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("Cep"));
                    imp.setDatacadastro(rst.getDate("DataCadastro"));
                    imp.setTel_principal(rst.getString("Telefone"));
                    imp.setPrazoEntrega(rst.getInt("PrazoEntrega"));
                    imp.setCondicaoPagamento(rst.getInt("CondicaoPagto"));
                    imp.setObservacao(rst.getString("Observacao"));

                    switch (rst.getInt("tipofornecedor")) {
                        case 1:
                            imp.setTipoFornecedor(TipoFornecedor.INDUSTRIA);
                            break;
                        case 2:
                            imp.setTipoFornecedor(TipoFornecedor.DISTRIBUIDOR);
                            break;
                        case 3:
                            imp.setTipoFornecedor(TipoFornecedor.PRODUTORRURAL);
                            break;
                        case 6:
                            imp.setTipoFornecedor(TipoFornecedor.PRESTADOR);
                        default:
                            break;
                    }

                    if ((rst.getString("Email") != null)
                            && (!rst.getString("Email").trim().isEmpty())) {
                        imp.addEmail("EMAIL", rst.getString("Email").toLowerCase(), TipoContato.NFE);
                    }
                    if ((rst.getString("fax") != null)
                            && (!rst.getString("fax").trim().isEmpty())) {
                        imp.addTelefone("FAX", rst.getString("fax"));
                    }
                    if ((rst.getString("Telex") != null)
                            && (!rst.getString("Telex").trim().isEmpty())) {
                        imp.addTelefone("TELEX", rst.getString("Telex"));
                    }
                    if ((rst.getString("TeleContato") != null)
                            && (!rst.getString("TeleContato").trim().isEmpty())) {
                        imp.addTelefone(rst.getString("Contato") == null ? "CONTATO" : rst.getString("Contato"), rst.getString("TeleContato"));
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

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	pf.Fornecedor as idfornecedor,\n"
                    + "	pf.Produto_Id as idproduto,\n"
                    + "	pf.Referencia as codigoexterno,\n"
                    + "	pf.Qtde_Emb as qtdembalagem,\n"
                    + "	pf.Preco_Tabela as custo\n"
                    + "from dbo.Produtos_Fornecedor pf\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rst.getString("idproduto"));
                    imp.setIdFornecedor(rst.getString("idfornecedor"));
                    imp.setCodigoExterno(rst.getString("codigoexterno"));
                    imp.setQtdEmbalagem(rst.getDouble("qtdembalagem"));
                    imp.setCustoTabela(rst.getDouble("custo"));
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
                    "select \n"
                    + "	c.Codigo as id, \n"
                    + "	c.Nome as razao, \n"
                    + "	c.Apelido as fantasia,\n"
                    + "	c.RG, \n"
                    + "	c.CPF, \n"
                    + "	c.IDSexo as sexo,\n"
                    + "	c.DataNascimento,\n"
                    + "	c.EstadoCivil,\n"
                    + "	c.NomeConjuge,\n"
                    + "	c.DataNascimentoConjuge,\n"
                    + "	c.Endereco,\n"
                    + "	c.Numero,\n"
                    + "	c.Complemento,\n"
                    + "	c.Bairro,\n"
                    + "	c.CEP,\n"
                    + "	c.Cidade as municipio,\n"
                    + "	c.Estado as uf,\n"
                    + "	c.Referencia,\n"
                    + "	c.TipoEndereco,\n"
                    + "	c.eMail,\n"
                    + "	c.Empresa,\n"
                    + "	c.DataAdmissao,\n"
                    + "	c.CodigoProfissao,\n"
                    + "	c.TelefoneEmpresa,\n"
                    + "	c.RamalEmpresa,\n"
                    + "	c.DataInclusao as datacadastro,\n"
                    + "	c.Telefone,\n"
                    + "	c.InscEstadual as ie_rg,\n"
                    + "	c.Status,\n"
                    + "	c.LimiteCredito as valorlimite,\n"
                    + "	c.LimiteCheques,\n"
                    + "	c.DescProfissao as cargo,\n"
                    + "	c.Renda as salario,\n"
                    + "	c.EnderecoEntrega,\n"
                    + "	c.NumeroEntrega,\n"
                    + "	c.ComplEntrega,\n"
                    + "	c.BairroEntrega,\n"
                    + "	c.CidadeEntrega as municipioentrega,\n"
                    + "	c.UFEntrega as ufentrega,\n"
                    + "	c.CEPEntrega as cepentrega,\n"
                    + "	c.FoneEntrega as telefoneentrega\n"
                    + "from dbo.Clientes c\n"
                    + "where c.IDLoja = " + getLojaOrigem() + "\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj(rst.getString("CPF"));
                    imp.setInscricaoestadual(rst.getString("RG"));
                    imp.setEndereco(rst.getString("Endereco"));
                    imp.setNumero(rst.getString("Numero"));
                    imp.setComplemento(rst.getString("Complemento"));
                    imp.setBairro(rst.getString("Bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("CEP"));
                    imp.setTelefone(rst.getString("Telefone"));
                    imp.setEmail(rst.getString("eMail") == null ? "" : rst.getString("eMail").toLowerCase());
                    imp.setValorLimite(rst.getDouble("valorlimite"));
                    imp.setPermiteCheque(true);
                    imp.setPermiteCreditoRotativo(true);
                    imp.setDataNascimento(rst.getDate("NomeConjuge"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setNomeConjuge(rst.getString("NomeConjuge"));
                    imp.setCargo(rst.getString("cargo"));
                    imp.setSalario(rst.getDouble("salario"));
                    imp.setEmpresaTelefone(rst.getString("TelefoneEmpresa"));

                    if (rst.getInt("EstadoCivil") == 2) {
                        imp.setEstadoCivil(TipoEstadoCivil.CASADO);
                    } else {
                        imp.setEstadoCivil(TipoEstadoCivil.NAO_INFORMADO);
                    }

                    switch (rst.getInt("sexo")) {
                        case 1:
                            imp.setSexo(TipoSexo.MASCULINO);
                            break;
                        default:
                            imp.setSexo(TipoSexo.FEMININO);
                            break;
                    }

                    result.add(imp);
                }
            }
        }
        return result;
    }
}
