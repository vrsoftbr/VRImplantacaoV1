/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.vo.cadastro.oferta.SituacaoOferta;
import vrimplantacao2.vo.cadastro.oferta.TipoOfertaVO;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.OfertaIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author lucasrafael
 */
public class SisMouraDAO extends InterfaceDAO {

    private static final Logger LOG = Logger.getLogger(SisMouraDAO.class.getName());

    @Override
    public String getSistema() {
        return "SisMoura";
    }

    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "Codigo, (Fantasia +' - '+CNPJ) Empresa\n"
                    + "from Empresa\n"
                    + "order by Codigo"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("Codigo"), rst.getString("Empresa")));
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + "DISTINCT \n"
                    + "Grupo,\n"
                    + "SubGrupo,\n"
                    + "GRUPO.DESCRICAO AS DESCRICAOGRUPO,\n"
                    + "SUBGRUPO.DESCRICAO AS DESCRICAOSUBGRUPO\n"
                    + "FROM Produto\n"
                    + "INNER JOIN GRUPO_PRODUTO AS GRUPO ON GRUPO.Codigo = Produto.Grupo\n"
                    + "INNER JOIN SubGrupo AS SUBGRUPO ON SUBGRUPO.Codigo = Produto.SubGrupo\n"
                    + "ORDER BY GRUPO,SUBGRUPO"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("Grupo"));
                    imp.setMerc1Descricao(rst.getString("DESCRICAOGRUPO"));
                    imp.setMerc2ID(rst.getString("SubGrupo"));
                    imp.setMerc2Descricao(rst.getString("DESCRICAOSUBGRUPO"));
                    imp.setMerc3ID("1");
                    imp.setMerc3Descricao(imp.getMerc2Descricao());
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "declare @primeirocadastro date;\n"
                    + "select @primeirocadastro = min(p.Data_Cadastro) from produto p\n"
                    + "select\n"
                    + "p.codigo id,\n"
                    + "p.nome descricaocompleta,\n"
                    + "case when ltrim(rtrim(p.Descricao_Reduzida)) = '' then p.nome else p.Descricao_Reduzida end descricaoreduzida,\n"
                    + "p.nome descricaogondola,\n"
                    + "p.status id_situacaocadastral,\n"
                    + "isnull(p.Data_Cadastro, @primeirocadastro) datacadastro,\n"
                    + "p.grupo mercadologico1,\n"
                    + "isnull(p.SubGrupo, 1) mercadologico2,\n"
                    + "1 as mercadologico3,\n"
                    + "p.ncm,\n"
                    + "p.Codigo_CEST cest,\n"
                    + "c.Codigo_CEST,\n"
                    + "p.Margem,\n"
                    + "p.Quantidade qtdEmbalagem,\n"
                    + "p.Codigo_Barra ean,      \n"
                    + "p.balanca e_balanca,\n"
                    + "p.Validade,\n"
                    + "p.Unidade id_tipoembalagem,\n"
                    + "p.Peso_Produto peso_bruto,\n"
                    + "p.Peso_Produto peso_liquido,\n"
                    + "fs.ST_PIS pisconfinssaida,\n"
                    + "fs.ST_PIS_Entrada pisconfisentrada,\n"
                    + "p.Codigo_Incidencia_Monofasica pisconfinsnatureza,\n"
                    + "fs.CST icms_cst,\n"
                    + "fs.Aliquota_ICMS icms_aliquota,\n"
                    + "0 icms_reducao,\n"
                    + "est.Qtde estoque,\n"
                    + "p.Estoque_maximo,\n"
                    + "p.Estoque_minimo,\n"
                    + "p.Preco_Produto preco,\n"
                    + "p.Preco_Custo custo,\n"
                    + "p.Inativo,\n"
                    + "p.Unidade\n"
                    + "from\n"
                    + "Produto p\n"
                    + "left join Produto_Regra_Imposto imp on p.Codigo = imp.Produto\n"
                    + "left join Fiscal_Regra_Imposto fs on imp.Tipo_Regra_Imposto = fs.Tipo_Regra_Imposto\n"
                    + "left join Estoque est on p.Codigo = est.Produto and est.Deposito = 1 \n"
                    + "left join Produto_CEST c on c.Codigo = p.Codigo_CEST\n"
                    + "order by \n"
                    + "p.codigo"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("ean"));
                    imp.seteBalanca((rst.getInt("e_balanca") == 1));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida((rst.getString("descricaoreduzida") == null ? imp.getDescricaoCompleta() : rst.getString("descricaoreduzida")));
                    imp.setDescricaoGondola(rst.getString("descricaogondola"));
                    imp.setCodMercadologico1(rst.getString("mercadologico1"));
                    imp.setCodMercadologico2(rst.getString("mercadologico2"));
                    imp.setCodMercadologico3(rst.getString("mercadologico3"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setSituacaoCadastro(("N".equals(rst.getString("Inativo")) ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO));
                    imp.setTipoEmbalagem(rst.getString("Unidade"));
                    imp.setQtdEmbalagem(rst.getInt("qtdEmbalagem"));
                    imp.setValidade(rst.getInt("Validade"));
                    imp.setPesoBruto(rst.getDouble("peso_bruto"));
                    imp.setPesoLiquido(rst.getDouble("peso_liquido"));
                    imp.setMargem(rst.getDouble("Margem"));
                    imp.setPrecovenda(rst.getDouble("preco"));
                    imp.setCustoComImposto(rst.getDouble("custo"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setEstoqueMinimo(rst.getDouble("Estoque_minimo"));
                    imp.setEstoqueMaximo(rst.getDouble("Estoque_maximo"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("Codigo_CEST"));
                    imp.setPiscofinsCstDebito(rst.getInt("pisconfinssaida"));
                    imp.setPiscofinsCstCredito(rst.getInt("pisconfisentrada"));
                    imp.setPiscofinsNaturezaReceita(Integer.parseInt(Utils.formataNumero(rst.getString("pisconfinsnatureza"))));
                    imp.setIcmsCst(Integer.parseInt(Utils.formataNumero(rst.getString("icms_cst"))));
                    imp.setIcmsAliq(rst.getDouble("icms_aliquota"));
                    imp.setIcmsReducao(rst.getDouble("icms_reducao"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select distinct\n"
                    + "codigo,\n"
                    + "ean,\n"
                    + "unidade\n"
                    + "from\n"
                    + "(select\n"
                    + "codigo,\n"
                    + "codigo_barra ean,\n"
                    + "p.unidade\n"
                    + "from\n"
                    + "produto p\n"
                    + "union\n"
                    + "select\n"
                    + "pb.produto codigo,\n"
                    + "pb.codigo_barra ean,\n"
                    + "p.unidade\n"
                    + "from\n"
                    + "produto_barra pb join produto p on pb.produto = p.codigo) eans\n"
                    + "order by codigo"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("codigo"));
                    imp.setEan(rst.getString("ean"));
                    imp.setTipoEmbalagem(rst.getString("unidade"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	p.Codigo id,\n"
                    + "	p.Data_Cadastro dataCadastro,\n"
                    + "	p.Nome razao,\n"
                    + "	p.Nome_Fantasia fantasia,\n"
                    + "	p.Endereco_Nome endereco,\n"
                    + "	p.Numero,\n"
                    + "	p.Complemento,\n"
                    + "	p.Bairro,\n"
                    + "	cast(cd.Codigo_Cidade_IBGE as integer) id_municipio,\n"
                    + "	cd.Codigo_UF_IBGE id_estado,\n"
                    + "	p.Cep,\n"
                    + "       \n"
                    + "	p.Endereco_Pagamento cob_endereco,\n"
                    + "	0 as cob_numero,	\n"
                    + "	p.Bairro_Pagamento cob_bairro,	\n"
                    + "	cast(cob_cd.Codigo_Cidade_IBGE as integer) cob_id_municipio,\n"
                    + "	cob_cd.Estado cob_id_estado,\n"
                    + "	p.Cep_Pagamento cob_cep,	\n"
                    + "	\n"
                    + "	p.Fone fone1,\n"
                    + "	p.RG inscricaoestadual,\n"
                    + "	p.cpf cnpj,\n"
                    + "	p.Observacao,\n"
                    + "	p.Fone2,\n"
                    + "	p.Fax,\n"
                    + "	coalesce(p.Email, p.email_utilizado, p.Emails_Promocionais) email,\n"
                    + "	p.Contato observacoes,\n"
                    + "	case when p.Inativo = 'N' then 1 else 0 end id_situacaocadastro\n"
                    + "from\n"
                    + "	Fornecedor f\n"
                    + "	join Pessoa p on f.Pessoa = p.Codigo\n"
                    + "	left join Cidade cd on p.Cidade = cd.Codigo\n"
                    + "	left join Cidade cob_cd on p.Cidade = cob_cd.Codigo\n"
                    + "order by\n"
                    + "	p.Codigo"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("inscricaoestadual"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("Numero"));
                    imp.setComplemento(rst.getString("Complemento"));
                    imp.setBairro(rst.getString("Bairro"));
                    imp.setCep(rst.getString("Cep"));
                    imp.setIbge_municipio(rst.getInt("id_municipio"));
                    imp.setIbge_uf(rst.getInt("id_estado"));
                    imp.setDatacadastro(rst.getDate("dataCadastro"));
                    imp.setTel_principal(rst.getString("fone1"));
                    imp.setObservacao(rst.getString("Observacao"));
                    imp.setAtivo((rst.getInt("id_situacaocadastro") == 1));
                    imp.setCob_endereco(rst.getString("cob_endereco"));
                    imp.setCob_numero(rst.getString("cob_numero"));
                    imp.setCob_bairro(rst.getString("cob_bairro"));
                    imp.setCob_ibge_municipio(rst.getInt("cob_id_municipio"));
                    imp.setCob_uf(rst.getString("cob_id_estado"));
                    imp.setCob_cep(rst.getString("cob_cep"));

                    if ((rst.getString("Fone2") != null)
                            && (!rst.getString("Fone2").trim().isEmpty())) {
                        imp.addContato(
                                "1",
                                "TELEFONE 2",
                                rst.getString("Fone2"),
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("Fax") != null)
                            && (!rst.getString("Fax").trim().isEmpty())) {
                        imp.addContato(
                                "2",
                                "FAX",
                                rst.getString("Fax"),
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("email") != null)
                            && (!rst.getString("email").trim().isEmpty())) {
                        imp.addContato(
                                "3",
                                "EMAIL",
                                null,
                                TipoContato.COMERCIAL,
                                rst.getString("email").toLowerCase()
                        );
                    }
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "Produto, \n"
                    + "Fornecedor, \n"
                    + "Data_Carga, \n"
                    + "Codigo_Produto_Fornecedor \n"
                    + "from Produto_Fornecedor"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rst.getString("Produto"));
                    imp.setIdFornecedor(rst.getString("Fornecedor"));
                    imp.setCodigoExterno(rst.getString("Codigo_Produto_Fornecedor"));
                    imp.setDataAlteracao(rst.getDate("Data_Carga"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "p.Codigo id,\n"
                    + "p.Nome,\n"
                    + "p.Endereco,\n"
                    + "p.Numero,\n"
                    + "p.Complemento,\n"
                    + "p.Bairro,\n"
                    + "cd.Estado uf,\n"
                    + "cd.Codigo_UF_IBGE,\n"
                    + "cd.Codigo_Cidade_IBGE id_municipio,\n"
                    + "cd.Cidade,\n"
                    + "p.Cep,\n"
                    + "p.Fone fone1,\n"
                    + "p.RG inscricaoestadual,\n"
                    + "p.cpf cnpj,\n"
                    + "case p.Sexo when 'M' then 2 else 1 end Sexo,\n"
                    + "p.Data_Cadastro datacadastro,\n"
                    + "p.Email,\n"
                    + "p.Limite_Credito limite,\n"
                    + "p.Fax,\n"
                    + "case p.Inativo when 'N' then 1 else 0 end id_situacaocadastro,\n"
                    + "p.Fone2 telefone2,\n"
                    + "p.Observacao,\n"
                    + "p.Data_Nasc datanascimento,\n"
                    + "p.Pai nomePai,\n"
                    + "p.Mae nomeMae,\n"
                    + "p.Empresa,\n"
                    + "p.Fone_Trabalho telEmpresa,\n"
                    + "null as cargo,\n"
                    + "null as enderecoEmpresa,\n"
                    + "0 as salario,\n"
                    + "p.Estado_Civil estadocivil,\n"
                    + "p.Conjuge\n"
                    + "from\n"
                    + "Cliente c\n"
                    + "join Pessoa p on c.Pessoa = p.Codigo\n"
                    + "left join Cidade cd on p.Cidade = cd.Codigo\n"
                    + "order by\n"
                    + "p.Codigo"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("id"));
                    imp.setRazao(rst.getString("Nome"));
                    imp.setFantasia(rst.getString("Nome"));
                    imp.setEndereco(rst.getString("Endereco"));
                    imp.setNumero(rst.getString("Numero"));
                    imp.setComplemento(rst.getString("Complemento"));
                    imp.setBairro(rst.getString("Bairro"));
                    imp.setUf(rst.getString("uf"));
                    imp.setUfIBGE(rst.getInt("Codigo_UF_IBGE"));
                    imp.setMunicipio(rst.getString("Cidade"));
                    imp.setMunicipioIBGE(rst.getInt("id_municipio"));
                    imp.setCep(rst.getString("Cep"));
                    imp.setTelefone(rst.getString("fone1"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setInscricaoestadual(rst.getString("inscricaoestadual"));
                    imp.setSexo((rst.getInt("Sexo") == 1 ? TipoSexo.FEMININO : TipoSexo.MASCULINO));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setEmail(rst.getString("Email") == null ? "" : rst.getString("Email").toLowerCase());
                    imp.setValorLimite(rst.getDouble("limite"));
                    imp.setAtivo((rst.getInt("id_situacaocadastro") == 1));
                    imp.setObservacao(rst.getString("Observacao"));
                    imp.setDataNascimento(rst.getDate("datanascimento"));
                    imp.setNomePai(rst.getString("nomePai"));
                    imp.setNomeMae(rst.getString("nomeMae"));
                    imp.setNomeConjuge(rst.getString("Conjuge"));
                    imp.setCargo(rst.getString("cargo"));
                    imp.setEmpresa(rst.getString("Empresa"));
                    imp.setEmpresaTelefone(rst.getString("telEmpresa"));
                    imp.setEmpresaEndereco(rst.getString("enderecoEmpresa"));
                    imp.setSalario(rst.getDouble("salario"));

                    if ((rst.getString("telefone2") != null)
                            && (!rst.getString("telefone2").trim().isEmpty())) {
                        imp.addContato(
                                "1",
                                "TELEFONE 2",
                                rst.getString("telefone2"),
                                null,
                                null
                        );
                    }
                    if ((rst.getString("Fax") != null)
                            && (!rst.getString("Fax").trim().isEmpty())) {
                        imp.addContato(
                                "2",
                                "FAX",
                                rst.getString("Fax"),
                                null,
                                null
                        );
                    }
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "r.Codigo, \n"
                    + "r.Codigo_Cliente, \n"
                    + "r.Numero_Documento, \n"
                    + "r.Doc_Merc, \n"
                    + "r.N_Doc_Merc, \n"
                    + "r.Doc_Bancario,\n"
                    + "r.N_Doc_Bancario, \n"
                    + "r.Parcela, \n"
                    + "r.Data_Emissao,\n"
                    + "r.Data_Vencimento, \n"
                    + "r.Valor, \n"
                    + "r.Observacao,\n"
                    + "r.Valor_Recebido, \n"
                    + "r.PDV, \n"
                    + "r.Caixa, \n"
                    + "r.Data_Baixa  \n"
                    + "from Contas_Receber r\n"
                    + "where r.Empresa = " + getLojaOrigem() + "\n"
                    + "and r.Data_Baixa is null"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("Codigo"));
                    imp.setIdCliente(rst.getString("Codigo_Cliente"));
                    imp.setNumeroCupom(rst.getString("Numero_Documento"));
                    imp.setParcela(rst.getInt("Parcela"));
                    imp.setDataEmissao(rst.getDate("Data_Emissao"));
                    imp.setDataVencimento(rst.getDate("Data_Vencimento"));
                    imp.setValor(rst.getDouble("Valor"));
                    imp.setObservacao(rst.getString("Observacao"));
                    imp.setEcf(rst.getString("PDV"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ChequeIMP> getCheques() throws Exception {
        List<ChequeIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "Codigo,\n"
                    + "Nome_Cliente,\n"
                    + "Fone_Cliente,\n"
                    + "CPF,\n"
                    + "Venda, \n"
                    + "Valor_Cheque,\n"
                    + "Banco,\n"
                    + "Cheque,\n"
                    + "Vencimento,\n"
                    + "Observacao,\n"
                    + "Agencia,\n"
                    + "Conta,\n"
                    + "Data_Cadastro\n"
                    + "from Cheques\n"
                    + "where CodEmpresa = " + getLojaOrigem() + "\n"
                    + "and Baixa_Data is null"
            )) {
                while (rst.next()) {
                    ChequeIMP imp = new ChequeIMP();
                    imp.setId(rst.getString("Codigo"));
                    imp.setNome(rst.getString("Nome_Cliente"));
                    imp.setCpf(rst.getString("CPF"));
                    imp.setValor(rst.getDouble("Valor_Cheque"));
                    imp.setBanco(rst.getInt("Banco"));
                    imp.setAgencia(rst.getString("Agencia"));
                    imp.setConta(rst.getString("Conta"));
                    imp.setNumeroCupom(rst.getString("Venda"));
                    imp.setObservacao(rst.getString("Observacao"));
                    imp.setDataHoraAlteracao(rst.getTimestamp("Data_Cadastro"));
                    imp.setAlinea(0);
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<OfertaIMP> getOfertas(Date dataTermino) throws Exception {
        List<OfertaIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "Codigo,\n"
                    + "Produto,\n"
                    + "Validade_Inicial,\n"
                    + "Validade_Final,\n"
                    + "Preco_Produto,\n"
                    + "Preco_Promocao\n"
                    + "from Produto_Promocao\n"
                    + "where Validade_Final > GETDATE()"
            )) {
                while (rst.next()) {
                    OfertaIMP imp = new OfertaIMP();
                    imp.setIdProduto(rst.getString("Produto"));
                    imp.setDataInicio(rst.getDate("Validade_Inicial"));
                    imp.setDataFim(rst.getDate("Validade_Final"));
                    imp.setPrecoOferta(rst.getDouble("Preco_Promocao"));
                    imp.setSituacaoOferta(SituacaoOferta.ATIVO);
                    imp.setTipoOferta(TipoOfertaVO.CAPA);
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }
}
