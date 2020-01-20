/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import vrimplantacao.classe.ConexaoMySQL;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.vo.importacao.MercadologicoIMP;

/**
 *
 * @author Importacao
 */
public class SiitDAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "Siit";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                //OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.FAMILIA,
                OpcaoProduto.FAMILIA_PRODUTO,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                OpcaoProduto.QTD_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.PESO_BRUTO,
                OpcaoProduto.PESO_LIQUIDO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.TROCA,
                OpcaoProduto.MARGEM,
                OpcaoProduto.CUSTO,
                OpcaoProduto.PRECO,
                OpcaoProduto.ATIVO,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.NATUREZA_RECEITA,
                OpcaoProduto.ICMS,
                OpcaoProduto.ATACADO,
                OpcaoProduto.PAUTA_FISCAL,
                OpcaoProduto.PAUTA_FISCAL_PRODUTO,
                OpcaoProduto.SUGESTAO_COTACAO,
                OpcaoProduto.COMPRADOR,
                OpcaoProduto.COMPRADOR_PRODUTO,
                OpcaoProduto.OFERTA
        ));
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
            "select \n"
            + "  codigo,\n"
            + "  nomefantasia as nome\n"
            + "from filial"
            + "order by 1"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("codigo"), rst.getString("nome")));
                }
            }
        }

        return result;
    }
    
    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "  codigo,\n"
                    + "  descricao,\n"
                    + "  nivel\n"
                    + "from departamento\n"
                    + "order by codigo"
            )) {
                while (rst.next()) {
                    
                    MercadologicoIMP imp = new MercadologicoIMP();
                    
                    if (rst.getString("codigo").contains(".")) {

                        String merc = rst.getString("codigo") != null ? rst.getString("codigo") : "";
                        String[] cods = merc.split("\\.");

                        for (int i = 0; i < cods.length; i++) {

                            switch (i) {
                                case 0:
                                    imp.setMerc1ID(cods[i]);
                                    imp.setMerc1Descricao(rst.getString("descricao"));
                                    break;
                                case 1:
                                    imp.setMerc2ID(cods[i]);
                                    imp.setMerc2Descricao(rst.getString("descricao"));
                                    break;
                            }
                        }

                        imp.setMerc3ID("1");
                        imp.setMerc3Descricao("descricao");
                        
                    } else {                        
                        imp.setMerc1ID(rst.getString("codigo"));
                        imp.setMerc1Descricao(rst.getString("descricao"));
                    }
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
/*
    

select 
  codigo,
  codigopai,
  descricao,
  nivel
from departamento
order by codigo;

select * from item

select * from item


select 
  p.codigo as id,
  ean.codigobarras as ean,
  p.descricao as descricaocompleta,
  p.descricaocupom as descricaoreduzida,
  p.ncm as ncm,
  p.cest as cest,
  p.tributacaoicms_codigo as id_icms,
  p.receitapiscofins as naturezareceita,
  p.datacadastro as datacadastro,
  p.unidademedida_codigo as tipoembalagem,
  iv.departamento_codigo as mercadologico,
  case p.excluido when 1 then 0 else 1 end situacaocadastro,
  e.quantidade as estoque,
  e.estoqueminimo,
  e.estoquemaximo,
  pr.margemideal as margem,
  pr.precovenda,
  p.tributacaopiscofins_codigo as cst_pis,  
  pc.codigo as codigo_pis,
  pc.descricao as descricao_pis,
  pc2.cst as cst_piscofins,
  i.codigo as codigo_icms,
  i.descricao as descricao_icms,
  i2.cstcsosn as cst_icms,
  i2.picms as aliquota_icms,
  i2.predbc as reducao_icms  
from item p
left join itemvenda iv on iv.item_codigo = p.codigo
left join itemestoque e on e.item_codigo = p.codigo
left join itemunidadepreco pr on pr.item_codigo = p.codigo
left join itemunidadecodigobarras ean on ean.item_codigo = p.codigo
inner join tributacaopiscofins pc on pc.codigo = p.tributacaopiscofins_codigo
inner join tributacaopiscofinsitem pc2 on pc2.tributacaopiscofins_codigo = pc.codigo
  and pc2.uf = 'GO'
  and pc2.cfop = 5102
inner join tributacaoicms i on i.codigo = p.tributacaoicms_codigo
inner join tributacaoicmsitem i2 on i2.tributacaoicms_codigo = i.codigo
  and i2.uf = 'GO'
  and i2.cfop = 5102
order by p.codigo;

select * from grupotelaitem
select distinct(secao_codigo) from itemvenda
select * from itemvenda
select * from departamento
select * from secao where codigo in ('001', '010', '011')

select 
  pc.codigo as codigo_pis,
  pc.descricao as descricao_pis,
  pc2.cst as cst_pis
from tributacaopiscofins pc
inner join tributacaopiscofinsitem pc2 on pc2.tributacaopiscofins_codigo = pc.codigo
and pc2.uf = 'GO'
and pc2.cfop = 5102


select
  i.codigo as codigo_icms,
  i.descricao as descricao_icms,
  i2.cstcsosn as cst_icms,
  i2.picms as aliquota_icms,
  i2.predbc as reducao_icms
from tributacaoicms i
inner join tributacaoicmsitem i2 on i2.tributacaoicms_codigo = i.codigo
and i2.uf = 'GO'
and i2.cfop = 5102


select  
  p.codigo as id,
  p.nomerazao as razao,
  pj.nomefantasia as fantasia,
  pj.cnpj,
  pj.ie,
  p.tipopessoa as tipopessoa,
  p.datacadastro,
  pe.endereco_codigo,
  e.logradouro as endereco,
  e.numero,
  e.complemento,
  e.uf_codigo as uf_ibge,
  e.cidade_codigo as municipio_ibge,
  e.cep,
  b.nome as bairro,
  b.ceppadrao,
  c.cep as cep2,
  cid.nome as municipio,
  uf.nome as uf,
  tel.numero as telefone 
from participante p
left join participantepj pj on pj.participante_codigo = p.codigo
left join participanteendereco pe on pe.participante_codigo = p.codigo
left join endereco e on e.codigo = pe.endereco_codigo
left join bairro b on b.codigo = e.bairro_codigo
left join cep c on c.bairro_codigo = b.codigo
left join cidade cid on cid.codigo = e.cidade_codigo
left join uf on uf.codigo = e.uf_codigo
left join participantetelefone tel on tel.participante_codigo = p.codigo
where p.fornecedor = 1
order by p.codigo

select * from participantetelefone

select 
  pe.participante_codigo,
  pe.endereco_codigo,
  e.logradouro as endereco,
  e.numero,
  e.complemento,
  e.uf_codigo as uf_ibge,
  e.cidade_codigo as municipio_ibge,
  e.cep,
  b.nome as bairro,
  b.ceppadrao,
  c.cep as cep2,
  cid.nome as municipio,
  uf.nome as uf
from participanteendereco pe
left join endereco e on e.codigo = pe.endereco_codigo
left join bairro b on b.codigo = e.bairro_codigo
left join cep c on c.bairro_codigo = b.codigo
left join cidade cid on cid.codigo = e.cidade_codigo
left join uf on uf.codigo = e.uf_codigo


select 
  participante_codigo as id_fornecedor,
  email
from participanteemails


select 
  participante_codigo as id_fornecedor,
  item_codigo as id_produto,
  codigoitemfornecedor as codigoexterno
from itemcodigofornecedor

    
clientes
    
select  
  p.codigo as id,
  p.nomerazao as razao,
  pf.estadocivil,
  pf.cpf,
  pf.rg,
  pf.siglaemissorrg,
  pf.uf_emissorrg,
  pf.dataexpedicao,
  pf.datanascimento,
  pf.filiacaopai as pai,
  pf.filiacaomae as mae,
  p.tipopessoa as tipopessoa,
  p.datacadastro,
  pe.endereco_codigo,
  e.logradouro as endereco,
  e.numero,
  e.complemento,
  e.uf_codigo as uf_ibge,
  e.cidade_codigo as municipio_ibge,
  e.cep,
  b.nome as bairro,
  b.ceppadrao,
  c.cep as cep2,
  cid.nome as municipio,
  uf.nome as uf,
  tel.numero as telefone,
  ema.email,
  pc.bloqueado,
  pc.limitecredito,
  pc.diavencimentofatura
from participante p
left join participantepf pf on pf.participante_codigo = p.codigo
left join participanteendereco pe on pe.participante_codigo = p.codigo
left join endereco e on e.codigo = pe.endereco_codigo
left join bairro b on b.codigo = e.bairro_codigo
left join cep c on c.bairro_codigo = b.codigo
left join cidade cid on cid.codigo = e.cidade_codigo
left join uf on uf.codigo = e.uf_codigo
left join participantetelefone tel on tel.participante_codigo = p.codigo
left join participantecliente pc on pc.participante_codigo = p.codigo
left join participanteemails ema on ema.participante_codigo = p.codigo
where p.cliente = 1
order by p.codigo
    

select * from endereco
select * from bairro
select * from participantefornecedor
select * from cep
select * from cidade
select * from uf


select * from participantepj
select * from endereco

SELECT DISTINCT
  Table_schema,
  Table_name,
  Column_name,
  Data_type
FROM INFORMATION_SCHEMA.COLUMNS
WHERE upper(COLUMN_NAME) LIKE upper('%participante%')
and Table_schema like '%mais_vo%'
order by
  Table_schema,
  Table_name,
  Column_name
      
    
*/    
}
