drop sequence block_seq;
			
create sequence block_seq start with 1;

drop table block purge;

 
 create table Block (
    id integer primary key,              
    dataAsobject blob,
    previousHash varchar2(255) not null,
    hash varchar2(255) not null, 
    insertDate date not null,             
    userId varchar2(255) not null,       
    instanceId varchar2(32) not null,    
    nonce integer not null,
    hashAlgorithm varchar2(20) not null,
    codeBase varchar2(20)not null,
    prefix integer not null
 );

grant all on block to public;

select * from block order by id asc;

-- Sabotage!
-- 1. Fall: Hashcodes manipulieren
-- update block set hash = (select hash from block where id = 4 ) where id = 3;
-- commit;

-- 2. Fall: Datum manipulieren
-- Test, welche Bloecke identische Daten haben ( Rueckgabewert "0 entspricht "identisch" )
select a.id
from block a
where  0 in (select dbms_lob.compare( a.dataAsObject , dataAsObject )
    from block
   where id != a.id );

select a.id, dataAsObject, dbms_lob.getlength(dataAsObject)
from block a order by 1 asc;

update block set dataAsObject = (select dataAsObject from block where id = 4 ) where id = 3;
commit;
