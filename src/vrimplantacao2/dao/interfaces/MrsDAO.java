/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoPostgres;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ConveniadoIMP;
import vrimplantacao2.vo.importacao.ConvenioEmpresaIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author lucasrafael
 */
public class MrsDAO extends InterfaceDAO implements MapaTributoProvider {

    public String compl;

    public void setCompl(String compl) {
        this.compl = compl;
    }
    
    @Override
    public String getSistema() {
        if (compl == null || compl.trim().equals("")) {
            return "Mrs";
        } else {
            return "Mrs - " + compl;
        }
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select loja, nome_fantasia from parametros order by loja"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("loja"), rst.getString("nome_fantasia")));
                }
            }
        }
        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select codigo, nome, sugestao_cst as cst from icms order by codigo"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(
                            rst.getString("codigo"),
                            rst.getString("nome") + " CST. " + rst.getString("cst")));
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "m1.codigo as merc1, \n"
                    + "m1.nome as desc_merc1,\n"
                    + "m2.codigo as merc2,\n"
                    + "m2.nome as desc_merc2,\n"
                    + "m3.codigo as merc3,\n"
                    + "m3.nome as desc_merc3\n"
                    + "from grupos m1\n"
                    + "inner join familias m2 on m2.grupo = m1.codigo and m2.status = 'A'\n"
                    + "inner join sub_familias m3 on m3.familia = m2.codigo and m3.status = 'A'\n"
                    + "where m1.status = 'A'\n"
                    + "order by m1.codigo, m2.codigo"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("merc1"));
                    imp.setMerc1Descricao(rst.getString("desc_merc1"));
                    imp.setMerc2ID(rst.getString("merc2"));
                    imp.setMerc2Descricao(rst.getString("desc_merc2"));
                    imp.setMerc3ID(rst.getString("merc3"));
                    imp.setMerc3Descricao(rst.getString("desc_merc3"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "p.codigo,\n"
                    + "p.codigo_barras,\n"
                    + "case p.setorbalanca when 'PESAVEL' then 'S' else 'F' end balanca,\n"
                    + "p.validade,\n"
                    + "p.descricao,\n"
                    + "p.descricao_reduzida,\n"
                    + "p.embalagem1 as qtdembalagem,\n"
                    + "p.embalagem2 as tipoembalagem,\n"
                    + "p.data_alteracao as datacadastro,\n"
                    + "p.grupo,\n"
                    + "p.familia,\n"
                    + "p.sub_familia,\n"
                    + "p.estoque,\n"
                    + "p.estoquemin,\n"
                    + "p.margem,\n"
                    + "p.margem_fixa,\n"
                    + "p.precovenda,\n"
                    + "p.preco_custo_un_nf as custo,\n"
                    + "p.produto_inativo, \n"
                    + "p.cst_pis_entrada,\n"
                    + "p.cst_cofins_entrada,\n"
                    + "p.cst_pis_saida,\n"
                    + "p.cst_cofins_saida,\n"
                    + "p.codigo_natureza_receita,\n"
                    + "p.ncm,\n"
                    + "p.cest,\n"
                    + "icm.nome as desc_icms_s,\n"
                    + "icm.sugestao_cst as cst_icms_s,\n"
                    + "icm.icms as aliq_icms_s,\n"
                    + "icm.percentual_rdbc as red_icms_s,\n"
                    + "icm_e.nome as desc_icms_e,\n"
                    + "icm_e.sugestao_cst as cst_icms_e,\n"
                    + "icm_e.valor as aliq_icms_e,\n"
                    + "icm_e.percentual_rdbc as red_icms_e\n"
                    + "from produtos p\n"
                    + "left join icms icm on icm.codigo = p.icms\n"
                    + "left join icms_entrada icm_e on icm_e.codigo = p.icms_entrada\n"
                    + "order by p.codigo"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("codigo"));
                    imp.setEan(rst.getString("codigo_barras"));
                    imp.seteBalanca("S".equals(rst.getString("balanca")));
                    imp.setValidade(rst.getInt("validade"));
                    imp.setDescricaoCompleta(rst.getString("descricao"));
                    imp.setDescricaoReduzida(rst.getString("descricao_reduzida"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setCodMercadologico1(rst.getString("grupo"));
                    imp.setCodMercadologico2(rst.getString("familia"));
                    imp.setCodMercadologico3(rst.getString("sub_familia"));
                    imp.setMargem(rst.getDouble("margem_fixa"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setCustoComImposto(rst.getDouble("custo"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setEstoqueMinimo(rst.getDouble("estoquemin"));
                    imp.setSituacaoCadastro("N".equals(rst.getString("produto_inativo")) ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getInt("cst_pis_saida"));
                    imp.setPiscofinsCstCredito(rst.getInt("cst_pis_entrada"));
                    imp.setPiscofinsNaturezaReceita(rst.getInt("codigo_natureza_receita"));
                    imp.setIcmsCstSaida(rst.getInt("cst_icms_s"));
                    imp.setIcmsAliqSaida(rst.getDouble("aliq_icms_s"));
                    imp.setIcmsReducaoSaida(rst.getDouble("red_icms_s"));
                    imp.setIcmsCstEntrada(rst.getInt("cst_icms_e"));
                    imp.setIcmsAliqEntrada(rst.getDouble("aliq_icms_e"));
                    imp.setIcmsReducaoEntrada(rst.getDouble("red_icms_e"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "codigo,\n"
                    + "nome_fantasia,\n"
                    + "razao_social,\n"
                    + "telefone,\n"
                    + "endereco,\n"
                    + "numero,\n"
                    + "complemento,\n"
                    + "bairro,\n"
                    + "cidade,\n"
                    + "codigo_municipio,\n"
                    + "estado,\n"
                    + "cpf, \n"
                    + "rg,\n"
                    + "cep,\n"
                    + "observacao,\n"
                    + "fax,\n"
                    + "celular,\n"
                    + "email_nfe,\n"
                    + "email_vendedor,\n"
                    + "data_alteracao as datacadastro,\n"
                    + "status\n"
                    + "from fornecedores\n"
                    + "order by codigo"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("codigo"));
                    imp.setRazao(rst.getString("razao_social"));
                    imp.setFantasia(rst.getString("nome_fantasia"));
                    imp.setCnpj_cpf(rst.getString("cpf"));
                    imp.setIe_rg(rst.getString("rg"));
                    imp.setDatacadastro(rst.getDate("datacadastro"));
                    imp.setAtivo("A".equals(rst.getString("status")));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setIbge_municipio(rst.getInt("codigo_municipio"));
                    imp.setUf(rst.getString("estado"));

                    if (!"  .   -   ".equals(rst.getString("cep"))) {
                        imp.setCep(rst.getString("cep"));
                    }
                    if (!"(  )    -    ".equals(rst.getString("telefone"))) {
                        imp.setTel_principal(Utils.formataNumero(rst.getString("telefone")));
                    }

                    imp.setObservacao(rst.getString("observacao"));

                    if (!"(  )    -    ".equals(rst.getString("fax"))) {
                        imp.addContato(
                                "FAX",
                                Utils.formataNumero(rst.getString("fax")),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if (!"(  )    -    ".equals(rst.getString("celular"))) {
                        imp.addContato(
                                "CELULAR",
                                Utils.formataNumero(rst.getString("celular")),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("email_nfe") != null)
                            && (!rst.getString("email_nfe").trim().isEmpty())) {
                        imp.addContato(
                                "EMAIL NFE",
                                null,
                                null,
                                TipoContato.NFE,
                                rst.getString("email_nfe").toLowerCase()
                        );
                    }
                    if ((rst.getString("email_vendedor") != null)
                            && (!rst.getString("email_vendedor").trim().isEmpty())) {
                        imp.addContato(
                                "EMAIL VENDEDOR",
                                null,
                                null,
                                TipoContato.COMERCIAL,
                                rst.getString("email_vendedor").toLowerCase()
                        );
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

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "codigo,\n"
                    + "codigo_cliente,\n"
                    + "nome,\n"
                    + "endereco,\n"
                    + "numero,\n"
                    + "complemento,\n"
                    + "bairro,\n"
                    + "cidade,\n"
                    + "codigo_municipio,\n"
                    + "estado,\n"
                    + "cep,\n"
                    + "cpf,\n"
                    + "rg,\n"
                    + "inscricao_estadual,\n"
                    + "telefone,\n"
                    + "estado_civil,\n"
                    + "datanas,\n"
                    + "sexo,\n"
                    + "limite,\n"
                    + "observacoes,\n"
                    + "datacadastro,\n"
                    + "celular,\n"
                    + "email,\n"
                    + "dia_emissao_fatura,\n"
                    + "dia_fechamento_fatura,\n"
                    + "dia_vencimento,\n"
                    + "inativo\n"
                    + "from clientes\n"
                    + "where cod_convenio = 0\n"
                    + "order by codigo"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("codigo"));
                    imp.setRazao(rst.getString("nome"));
                    imp.setFantasia(imp.getRazao());
                    imp.setCnpj(rst.getString("cpf"));

                    if ((rst.getString("cpf") != null)
                            && (!rst.getString("cpf").trim().isEmpty())) {
                        imp.setInscricaoestadual(rst.getString("cpf"));
                    } else {
                        imp.setInscricaoestadual(rst.getString("inscricao_estadual"));
                    }

                    imp.setInscricaoestadual(rst.getString("inscricao_estadual"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));

                    if (!"  .   -   ".equals(rst.getString("cep"))) {
                        imp.setCep(rst.getString("cep"));
                    }

                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setMunicipioIBGE(rst.getInt("codigo_municipio"));
                    imp.setUf(rst.getString("estado"));

                    if (!"(  )    -    ".equals(rst.getString("telefone"))) {
                        imp.setTelefone(Utils.formataNumero(rst.getString("telefone")));
                    }
                    if (!"(  )    -    ".equals(rst.getString("celular"))) {
                        imp.setCelular(Utils.formataNumero(rst.getString("telefone")));
                    }

                    imp.setEmail(rst.getString("email"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setDataNascimento(rst.getDate("datanas"));
                    imp.setSexo("Masculino".equals(rst.getString("Sexo")) ? TipoSexo.MASCULINO : TipoSexo.FEMININO);
                    imp.setValorLimite(rst.getDouble("limite"));
                    imp.setObservacao(rst.getString("observacoes"));
                    imp.setAtivo("N".equals(rst.getString("inativo")));
                    imp.setDiaVencimento(rst.getInt("dia_vencimento"));
                    imp.setPermiteCheque(true);
                    imp.setPermiteCreditoRotativo(true);
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "cliente,\n"
                    + "data as emissao,\n"
                    + "datadeposito as vencimento,\n"
                    + "caixa,\n"
                    + "cupom,\n"
                    + "valorcompra,\n"
                    + "valordebito,\n"
                    + "observacoes,\n"
                    + "lancamento\n"
                    + "from comprascliente \n"
                    + "where statuscompra = 'D'\n"
                    + "and loja = " + getLojaOrigem()
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("lancamento") + "-" + rst.getString("cliente"));
                    imp.setIdCliente(rst.getString("cliente"));
                    imp.setDataEmissao(rst.getDate("emissao"));
                    imp.setDataVencimento(rst.getDate("vencimento"));
                    imp.setValor(rst.getDouble("valordebito"));
                    imp.setNumeroCupom(rst.getString("cupom"));
                    imp.setEcf(rst.getString("caixa"));
                    imp.setObservacao(rst.getString("observacoes"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ConvenioEmpresaIMP> getConvenioEmpresa() throws Exception {
        List<ConvenioEmpresaIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "codigo,\n"
                    + "nome,\n"
                    + "endereco,\n"
                    + "numero,\n"
                    + "complemento,\n"
                    + "bairro,\n"
                    + "cidade,\n"
                    + "estado,\n"
                    + "cep,\n"
                    + "telefone,\n"
                    + "fax,\n"
                    + "cnpj,\n"
                    + "ie,\n"
                    + "contato,\n"
                    + "desconto,\n"
                    + "dia_fechamento,\n"
                    + "dia_emissao,\n"
                    + "inativo,\n"
                    + "observacoes,\n"
                    + "baixar_debitos_automaticamente\n"
                    + "from convenios order by codigo"
            )) {
                while (rst.next()) {
                    ConvenioEmpresaIMP imp = new ConvenioEmpresaIMP();
                    imp.setId(rst.getString("codigo"));
                    imp.setRazao(rst.getString("nome"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setInscricaoEstadual(rst.getString("ie"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    if (!"  .   -   ".equals(rst.getString("cep"))) {
                        imp.setCep(rst.getString("cep"));
                    }
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("estado"));
                    if (!"(  )    -    ".equals(rst.getString("telefone"))) {
                        imp.setTelefone(Utils.formataNumero(rst.getString("telefone")));
                    }
                    imp.setSituacaoCadastro("N".equals(rst.getString("inativo")) ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    imp.setDesconto(rst.getDouble("desconto"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ConveniadoIMP> getConveniado() throws Exception {
        List<ConveniadoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "codigo,\n"
                    + "codigo_cliente,\n"
                    + "nome,\n"
                    + "endereco,\n"
                    + "numero,\n"
                    + "complemento,\n"
                    + "bairro,\n"
                    + "cidade,\n"
                    + "codigo_municipio,\n"
                    + "estado,\n"
                    + "cep,\n"
                    + "cpf,\n"
                    + "rg,\n"
                    + "inscricao_estadual,\n"
                    + "telefone,\n"
                    + "estado_civil,\n"
                    + "datanas,\n"
                    + "sexo,\n"
                    + "limite,\n"
                    + "observacoes,\n"
                    + "datacadastro,\n"
                    + "celular,\n"
                    + "email,\n"
                    + "dia_emissao_fatura,\n"
                    + "dia_fechamento_fatura,\n"
                    + "dia_vencimento,\n"
                    + "inativo,\n"
                    + "cod_convenio\n"
                    + "from clientes\n"
                    + "where cod_convenio <> 0\n"
                    + "order by codigo"
            )) {
                while (rst.next()) {
                    ConveniadoIMP imp = new ConveniadoIMP();
                    imp.setId(rst.getString("codigo"));
                    imp.setIdEmpresa(rst.getString("cod_convenio"));
                    imp.setNome(rst.getString("nome"));
                    imp.setCnpj(rst.getString("cpf"));
                    imp.setSituacaoCadastro("N".equals(rst.getString("inativo")) ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    imp.setConvenioLimite(rst.getDouble("limite"));
                    imp.setObservacao(rst.getString("observacoes"));
                    imp.setLojaCadastro(getLojaOrigem());
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
