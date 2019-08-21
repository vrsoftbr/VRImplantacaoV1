/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.interfaces;

/**
 *
 * @author lucasrafael
 */
public class HerculesIntCashDAO {
 
/*
    
select 
f.Fil_CodEmp,
f.Fil_CodFil,
f.Fil_NomFan
from dbo.IntFil f
inner join dbo.IntEmp e on e.Emp_CodEmp = f.Fil_CodEmp
order by f.Fil_CodEmp, f.Fil_CodFil

select 
g.Grp_CodGrp, g.Grp_DesGrp,
sg.Sgp_CodSub, sg.Sgp_DesSub
from dbo.IntGrp g
left join dbo.IntSgp sg on sg.Sgp_CodGrp = g.Grp_CodGrp
order by g.Grp_CodGrp, sg.Sgp_CodSub;

select
p.Prd_CodPrd as id,
p.Prd_CodBar as barras,
p.Prd_TipVen as balanca,
p.Prd_DesPrd as descricao,
p.Prd_CodUnd as unidade,
p.Prd_PesLiq as pesoliquido,
p.Prd_PesBru as pesobruto,
p.Prd_SitPrd as sitiacaocadastro,
p.Prd_DatAtu as datacadastro,
p.Prd_PrdNcm as ncm,
p.Prd_CodCes as cest,
p.Prd_CodGrp as merc1,
p.Prd_CodSub as merc2,
pr.Pvp_PreVen as precovenda
from dbo.IntPrd p
left join dbo.IntPvp pr on pr.Pvp_CodPrd = p.Prd_CodPrd
where p.Prd_CodEmp = 1;

select 
f.For_CicFor as id,
f.For_CicFor as cnpj,
f.For_IntEst as inscruicaoestadual,
f.For_NomFor as razao,
f.For_NomFan as fantasia,
f.For_EndFor as endereco,
f.For_EndNum as numero,
f.For_BaiFor as bairro,
f.For_CidFor as municipio,
f.For_CodMun as municipio_ibge,
f.For_EstFor as uf,
f.For_CepFor as cep,
f.For_FonFor as telefone,
f.For_FaxFor as fax,
f.For_CelFor as celular,
f.For_EmaFor as email
from dbo.IntFor f
where f.For_CodEmp = 1
order by f.For_CicFor

select 
Fpr_CodPrd as idproduto,
Fpr_CicFor as idfornecedor,
Fpr_CodFor as codigoexterno,
Fpr_UltEnt as dataalteracao,
Fpr_UltCus as custotabela
from dbo.IntFpr

select
c.Cli_CicCli as id,
c.Cli_CicCli as cnpj,
c.Cli_NomCli as razao,
c.Cli_NomFan as fantasia,
c.Cli_EndCli as endereco,
c.Cli_EndNum as numero,
c.Cli_BaiCli as bairro,
c.Cli_CidCli as municipio,
c.Cli_CodMun as municipio_ibge,
c.Cli_EstCli as uf,
c.Cli_CepCli as cep,
c.Cli_FonCli as telefone,
c.Cli_FaxCli as fax,
c.Cli_CelCli as celular,
c.Cli_EmaCli as email,
c.Cli_DatCad as datacadastro,
c.Cli_LimCre as valorlimite,
c.Cli_StaCli as situacaocadastro
from dbo.IntCli c
order by c.Cli_CicCli;

select * from dbo.IntCmv    
*/    
}
