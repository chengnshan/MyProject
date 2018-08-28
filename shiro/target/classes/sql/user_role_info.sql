create table if not exists user_role_info (
	 id serial not null,
	 roleId varchar(50) not null,
	 username VARCHAR(50) not null,
	 enableIs smallint default 1 ,
	 create_user VARCHAR(50),
	 create_time timestamp ,
	 update_user VARCHAR(50),
	 update_time timestamp ,
	constraint user_role_info_primary_key_id primary key (id)
);

--序列授权
grant all on user_role_info_id_seq to chengxp;

--表说明
comment on table user_role_info is '用户角色对应关系表';
--表字段说明
comment on column user_role_info.id is '主键Id';
comment on column user_role_info.roleId is '角色id';
comment on column user_role_info.username is '角色名';
comment on column user_role_info.enableIs is '是否有效(1有效,0无效)';
