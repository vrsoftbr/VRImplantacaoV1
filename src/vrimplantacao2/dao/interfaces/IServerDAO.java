package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoMySQL;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/*
 * @author Alan
 */
public class IServerDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "IServer";
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	  Codigo empcod,\n"
                    + "	  NomeFantasia empnome\n"
                    + "from tbl_loja "
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getInt("empcod") + "", rst.getString("empnome")));
                }
            }
        }
        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	 Cod_Classe codigo,\n"
                    + "	 Descricao_Classe descricao,\n"
                    + "	 Cst_Classe cst,\n"
                    + "	 Ecf_Aliquota_Classe aliquota,\n"
                    + "	 Nota_Reducao_Classe reducao\n"
                    + "from tbl_classe"
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(rs.getString("codigo"),
                            rs.getString("descricao"),
                            rs.getInt("cst"),
                            Utils.stringToDouble(rs.getString("aliquota")),
                            Utils.stringToDouble(rs.getString("reducao"))));
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> Result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	 g.Cod_Grupo m1,\n"
                    + "	 Descr_Grupo m1desc,\n"
                    + "	 sg.Cod_SubGrupo m2,\n"
                    + "	 sg.Descr_SubGrupo m2desc,\n"
                    + "	 sg.Cod_SubGrupo m3,\n"
                    + "	 sg.Descr_SubGrupo m3desc\n"
                    + "from tbl_grupo g\n"
                    + "  join tbl_subgrupo sg on g.Cod_Grupo = sg.Cod_Grupo"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("m1"));
                    imp.setMerc1Descricao(rst.getString("m1desc"));
                    imp.setMerc2ID(rst.getString("m2"));
                    imp.setMerc2Descricao(rst.getString("m2desc"));
                    imp.setMerc3ID(rst.getString("m3"));
                    imp.setMerc3Descricao(rst.getString("m3desc"));

                    Result.add(imp);
                }
            }
        }
        return Result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> Result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	Codigo_Prod id,\n"
                    + "	CodBarra_Prod ean,\n"
                    + "	Unidade_Prod unidade,\n"
                    + "	Descr_Prod descricaocompleta,\n"
                    + "	Descr_Reduz_Prod descricaoreduzida,\n"
                    + "	Descr_Reduz_Prod descricaogondola,\n"
                    + "	cst_classe cstsaida,\n"
                    + "	replace(ecf_aliquota_classe,',','.') aliqsaida,\n"
                    + "	nota_reducao_classe redsaida,\n"
                    + "	cst_classe cstconsumidor,\n"
                    + "	replace(ecf_aliquota_classe,',','.') aliqconsumidor,\n"
                    + "	nota_reducao_classe redconsumidor,\n"
                    + "	Cod_Grupo_Prod merc1,\n"
                    + "	Cod_Subgrupo_Prod merc2,\n"
                    + "	Cod_Subgrupo_Prod merc3,\n"
                    + "	Preco_Prod precovenda,\n"
                    + "	Custo_Prod custocomimposto,\n"
                    + "	Custo_SN_Prod custosemimposto,\n"
                    + "	Margem_Prod margem,\n"
                    + "	Estoque_Prod estoque,\n"
                    + "	Estoque_Min_Prod estoquemin,\n"
                    + "	Estoque_Max_Prod estoquemax,\n"
                    + "	Ultima_Alteracao_Prod dataalteracao,\n"
                    + "	Quantidade_Embalagem_Prod qtdembalagem,\n"
                    + "	Ncm_Prod ncm,\n"
                    + "	Cest_Prod cest,\n"
                  //+ "	Validade_Balanca_Prod validade,\n"
                    + "	case when Servico_Prod = 'B' then 'S' else 'N' end balanca,\n"
                    + "	case when Pesavel_Prod = 'N' then 0 else 1 end pesavel,\n"
                    + "	case when Status_Prod = 'A' then 1 else 0 end situacaocadastro\n"
                    + "from\n"
                    + "	tbl_produto p\n"
                    + "join tbl_classe icm on icm.Cod_Classe = p.Classe_Prod"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("ean"));
                    imp.setTipoEmbalagem(Utils.acertarTexto(rst.getString("unidade")));
                    
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(imp.getDescricaoReduzida());
                    
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    
                    imp.setIcmsCstSaida(rst.getInt("cstsaida"));
                    imp.setIcmsAliqSaida(rst.getDouble("aliqsaida"));
                    imp.setIcmsReducaoSaida(Utils.stringToDouble(rst.getString("redsaida")));
                    imp.setIcmsCstConsumidor(rst.getInt("cstsaida"));
                    imp.setIcmsAliqConsumidor(rst.getDouble("aliqsaida"));
                    imp.setIcmsReducaoConsumidor(Utils.stringToDouble(rst.getString("redsaida")));
                    
                    imp.setCodMercadologico1(rst.getString("merc1"));
                    imp.setCodMercadologico2(rst.getString("merc2"));
                    imp.setCodMercadologico3(rst.getString("merc3"));
                    
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setMargem(rst.getDouble("margem"));
                    
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setEstoqueMinimo(rst.getDouble("estoquemin"));
                    imp.setEstoqueMaximo(rst.getDouble("estoquemax"));
                    
                    imp.setDataAlteracao(rst.getDate("dataalteracao"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    
                    imp.setSituacaoCadastro(rst.getInt("situacaocadastro") == 1 ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    imp.seteBalanca("S".equals(rst.getString("balanca")));
                                     
                    Result.add(imp);
                }
            }
        }
        return Result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> Result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	Cod_Fornecedor id,\n"
                    + "	Cpf_Cnpj cnpj,\n"
                    + "	Rg_Ie ie,\n"
                    + "	Im,\n"
                    + "	razao,\n"
                    + "	fantasia,\n"
                    + "	Endereco,\n"
                    + "	Numero,\n"
                    + "	complemento,\n"
                    + "	Bairro,\n"
                    + "	Municipio,\n"
                    + "	uf,\n"
                    + "	Cep,\n"
                    + "	Telefone,\n"
                    + "	Telefone_Alt tel2,\n"
                    + "	Telefone_Alt2 tel3,\n"
                    + "	email,\n"
                    + "	observacao,\n"
                    + "	case when Status_Fornecedor = 'A' then 1 else 0 end situacao\n"
                    + "from\n"
                    + "	tbl_fornecedor"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("ie"));
                    imp.setInsc_municipal(rst.getString("im"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));

                    imp.setTel_principal(rst.getString("telefone"));

                    if ((rst.getString("tel2") != null)
                            && (!rst.getString("tel2").trim().isEmpty())) {
                        imp.addContato(
                                "1",
                                "TELEFONE 2",
                                rst.getString("tel2"),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("tel3") != null)
                            && (!rst.getString("tel3").trim().isEmpty())) {
                        imp.addContato(
                                "2",
                                "TELEFONE 3",
                                rst.getString("tel3"),
                                null,
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
                                null,
                                TipoContato.COMERCIAL,
                                rst.getString("email").toLowerCase()
                        );
                    }
                    imp.setObservacao(rst.getString("observacao"));
                    imp.setAtivo(rst.getBoolean("situacao"));

                    Result.add(imp);
                }
            }
        }
        return Result;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> Result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	  cod_fornecedor fornecedor,\n"
                    + "	  cod_produto_loja produto,\n"
                    + "	  cod_produto_fornecedor codexterno\n"
                    + "from tbl_produto_fornecedor\n"
                    + "	  order by 1,2"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdFornecedor(rst.getString("fornecedor"));
                    imp.setIdProduto(rst.getString("produto"));
                    imp.setCodigoExterno(rst.getString("codexterno"));

                    Result.add(imp);
                }
            }
        }
        return Result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> Result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	Cod_Cliente id,\n"
                    + "	Cpf_Cnpj cnpj,\n"
                    + "	Rg_Ie ie,\n"
                    + "	Nome razao,\n"
                    + "	Nome_Fantasia fantasia,\n"
                    + "	Endereco,\n"
                    + "	Numero,\n"
                    + "	complemento,\n"
                    + "	Bairro,\n"
                    + "	Cidade,\n"
                    + "	Estado,\n"
                    + "	Cep,\n"
                    + "	Endereco_Cobranca endcobranca,\n"
                    + "	Numero_Cobranca numcobranca,\n"
                    + "	Telefone,\n"
                    + "	Celular,\n"
                    + "	Email,\n"
                    + "	Dt_Nascimento nascimento,\n"
                    + "	trabalho_local empresa,\n"
                    + "	profissao cargo,\n"
                    + "	salario,\n"
                    + "	observacao,\n"
                    + "	lim_cv1 limite,\n"
                    + "	Ref_Conj_Nome cjnome,\n"
                    + "	Ref_Conj_Cpf cjcpf,\n"
                    + "	Ref_Conj_Nasc cjnascimento,\n"
                    + "	Data_Cadastro cadastro,\n"
                    + "	case when Status_Cliente = 'A' then 1 else 0 end situacao\n"
                    + "from tbl_cliente"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("id"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setInscricaoestadual(Utils.acertarTexto(rst.getString("ie")));
                    imp.setRazao(Utils.acertarTexto(rst.getString("razao")));
                    imp.setFantasia(Utils.acertarTexto(rst.getString("fantasia")));
                    imp.setEndereco(Utils.acertarTexto(rst.getString("endereco")));
                    imp.setNumero(Utils.acertarTexto(rst.getString("numero")));
                    imp.setComplemento(Utils.acertarTexto(rst.getString("complemento")));
                    imp.setBairro(Utils.acertarTexto(rst.getString("bairro")));
                    imp.setMunicipio(Utils.acertarTexto(rst.getString("cidade")));
                    imp.setUf(Utils.acertarTexto(rst.getString("estado")));
                    imp.setCep(rst.getString("cep"));

                    imp.setCobrancaEndereco(Utils.acertarTexto(rst.getString("endcobranca")));
                    imp.setCobrancaNumero(Utils.acertarTexto(rst.getString("numcobranca")));

                    imp.setTelefone(Utils.formataNumero(rst.getString("telefone")));
                    imp.setCelular(Utils.formataNumero(rst.getString("celular")));
                    if ((rst.getString("email") != null)
                            && (!rst.getString("email").trim().isEmpty())) {
                        imp.setEmail(Utils.acertarTexto(rst.getString("email")).toLowerCase());
                    } else {
                        imp.setEmail("");
                    }
                    imp.setDataNascimento(rst.getDate("nascimento"));

                    imp.setEmpresa(Utils.acertarTexto(rst.getString("empresa")));
                    imp.setCargo(Utils.acertarTexto(rst.getString("cargo")));
                    imp.setSalario(rst.getDouble("salario"));

                    imp.setObservacao(Utils.acertarTexto(rst.getString("observacao")));
                    imp.setValorLimite(rst.getDouble("limite"));

                    imp.setNomeConjuge(Utils.acertarTexto(rst.getString("cjnome")));
                    imp.setCpfConjuge(Utils.acertarTexto(rst.getString("cjcpf")));
                    imp.setDataNascimentoConjuge(rst.getDate("cjnascimento"));
                    imp.setDataCadastro(rst.getDate("cadastro"));
                    imp.setAtivo(rst.getBoolean("situacao"));

                    Result.add(imp);
                }
            }
        }
        return Result;
    }

    /*
     @Override
     public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
     List<CreditoRotativoIMP> vResult = new ArrayList<>();
     java.sql.Date dtEmissao, dtVencimento;
     try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
     try (ResultSet rst = stm.executeQuery(
     "select\n"
     + "recseq, reccod, rectitulo, reccli,\n"
     + "recemiss, recvenci, recobs, recvalor,\n"
     + "recvalpag, recparc\n"
     + "from tsl.tsm003\n"
     + "where recbaixa <> 'S'\n"
     + "and recvenci <> '000-00-00'\n"
     + "order by recvenci desc"
     )) {
     while (rst.next()) {
     CreditoRotativoIMP imp = new CreditoRotativoIMP();
     imp.setId(rst.getString("recseq") + "-"
     + rst.getString("reccli") + "-"
     + getLojaOrigem());
     imp.setIdCliente(rst.getString("reccli"));

     if ((rst.getString("rectitulo") != null)
     && (!rst.getString("rectitulo").trim().isEmpty())) {
     if (!Utils.encontrouLetraCampoNumerico(rst.getString("rectitulo"))) {
     imp.setNumeroCupom(rst.getString("rectitulo").trim());
     }
     }

     if ("0000-00-00".equals(rst.getString("recemiss"))) {
     dtEmissao = new Date(new java.util.Date().getTime());
     imp.setDataEmissao(dtEmissao);
     } else {
     imp.setDataEmissao(rst.getDate("recemiss"));
     }
     if ("0000-00-00".equals(rst.getString("recvenci"))) {
     dtVencimento = new Date(new java.util.Date().getTime());
     imp.setDataVencimento(dtVencimento);
     } else {
     imp.setDataVencimento(rst.getDate("recvenci"));
     }

     imp.setValor(rst.getDouble("recvalor"));
     imp.setParcela(rst.getInt("recparc"));
     imp.setObservacao(rst.getString("recobs") + " TITULO "
     + rst.getString("rectitulo") + " RECCOD " + rst.getString("reccod"));
     vResult.add(imp);
     }
     }
     }
     return vResult;
     }

   
     @Override
     public List<ChequeIMP> getCheques() throws Exception {
     List<ChequeIMP> vResult = new ArrayList<>();
     try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
     try (ResultSet rst = stm.executeQuery(
     "select\n"
     + "seq, ag, cc, dcc, numero, valor,\n"
     + "nome, banco, documento, pre, fone\n"
     + "from tsl.tsm004 "
     + "where pre <> '0000-00-00'"
     )) {
     while (rst.next()) {
     ChequeIMP imp = new ChequeIMP();
     imp.setId(rst.getString("seq"));
     imp.setAgencia(rst.getString("ag"));
     imp.setConta(rst.getString("cc") + rst.getString("dcc"));
     imp.setNumeroCheque(rst.getString("numero"));
     imp.setValor(rst.getDouble("valor"));
     imp.setNome(rst.getString("nome"));
     imp.setBanco(Integer.parseInt(Utils.formataNumero(rst.getString("banco"))));
     imp.setCpf(rst.getString("documento"));
     imp.setTelefone(rst.getString("fone"));
     imp.setDate(rst.getDate("pre"));
     imp.setDataDeposito(rst.getDate("pre"));
     vResult.add(imp);
     }
     }
     }
     return vResult;
     }*/
}
