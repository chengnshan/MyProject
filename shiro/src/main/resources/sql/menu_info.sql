create table if not exists menu_info (
	 id serial not null,
	 menuId VARCHAR(60) not null,
	 menuName varchar(50) ,
	 menuUrl varchar(255) not NULL ,
	 sort int ,
	 classStyle varchar(50),
	 remark varchar(255) ,
	 parentMenuId int ,
	 enableIs smallint default 1 ,
	 menuLevel int ,
	constraint menu_info_primary_key_id primary key (id)
);

--序列授权
grant all on menu_info_id_seq to chengxp;

--表说明
comment on table menu_info is '菜单信息表';
--表字段说明
comment on column menu_info.id is '主键Id';
comment on column menu_info.menuId is '菜单id';
comment on column menu_info.menuName is '菜单名';
comment on column menu_info.menuUrl is '菜单路径';
comment on column menu_info.sort is '菜单序号';
comment on column menu_info.remark is '菜单备注';
comment on column menu_info.parentMenuId is '父菜单id';
comment on column menu_info.enableIs is '是否有效(1有效,0无效)';
comment on column menu_info.menuLevel is '菜单级别';


--添加索引
create index index_menu_info_menuId on menu_info(menuId);
--添加唯一约束
alter table menu_info add CONSTRAINT unique_menu_info_menuId UNIQUE(menuId);

INSERT INTO menu_info"("menuid", "menuname", "menuurl", "remark")
VALUES ( 'onlieUserList', '在线用户列表查询操作', 'template/onlieUserList.html', '');
