create table if not exists role_menu_info (
	 id serial not null,
	 roleId varchar(50) not null,
	 menuId VARCHAR(128) ,
	 enableIs smallint default 1 ,
	constraint role_menu_info_primary_key_id primary key (id)
);

--序列授权
grant all on role_menu_info_id_seq to chengxp;

--表说明
comment on table role_menu_info is '角色菜单对应表';
--表字段说明
comment on column role_menu_info.id is '主键Id';
comment on column role_menu_info.roleId is '角色id';
comment on column role_menu_info.menuId is '菜单id';
comment on column role_menu_info.enableIs is '是否有效(1有效,0无效)';


--添加索引
create index index_role_menu_info_roleId on role_menu_info(roleId);

insert into role_menu_info (roleId,menuId) values ('user','blog');
insert into role_menu_info (roleId,menuId) values ('user','signup');
insert into role_menu_info (roleId,menuId) values ('user','register');
insert into role_menu_info (roleId,menuId) values ('admin','timeline');
insert into role_menu_info (roleId,menuId) values ('admin','forms');
insert into role_menu_info (roleId,menuId) values ('admin','typography');
insert into role_menu_info (roleId,menuId) values ('admin','bootstrap-elements');
insert into role_menu_info (roleId,menuId) values ('admin','bootstrap-grid');
insert into role_menu_info (roleId,menuId) values ('admin','onlieUserList');


