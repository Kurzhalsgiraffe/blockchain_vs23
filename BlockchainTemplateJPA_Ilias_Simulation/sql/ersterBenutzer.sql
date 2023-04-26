drop table block purge;

select * from block order by id asc;

update dea.block set dataAsObject = (select dataAsObject from block where id = 3 ) where id = 3;
commit;

