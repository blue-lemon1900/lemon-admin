--
-- PostgreSQL database dump
--

-- Dumped from database version 17.4 (Ubuntu 17.4-1.pgdg24.04+2)
-- Dumped by pg_dump version 17.4 (Ubuntu 17.4-1.pgdg24.04+2)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: public; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA public;


--
-- Name: SCHEMA public; Type: COMMENT; Schema: -; Owner: -
--

COMMENT ON SCHEMA public IS 'standard public schema';


--
-- Name: cast_varchar_to_timestamp(character varying); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public.cast_varchar_to_timestamp(character varying) RETURNS timestamp with time zone
    LANGUAGE sql STRICT
    AS $_$
select to_timestamp($1, 'yyyy-mm-dd hh24:mi:ss');
$_$;


SET default_table_access_method = heap;

--
-- Name: sys_config; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.sys_config (
    config_id bigint NOT NULL,
    tenant_id character varying(20) DEFAULT '000000'::character varying,
    config_name character varying(100) DEFAULT ''::character varying,
    config_key character varying(100) DEFAULT ''::character varying,
    config_value character varying(500) DEFAULT ''::character varying,
    config_type character(1) DEFAULT 'N'::bpchar,
    create_dept bigint,
    create_by bigint,
    create_time timestamp without time zone,
    update_by bigint,
    update_time timestamp without time zone,
    remark character varying(500) DEFAULT NULL::character varying
);


--
-- Name: TABLE sys_config; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.sys_config IS '参数配置表';


--
-- Name: COLUMN sys_config.config_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_config.config_id IS '参数主键';


--
-- Name: COLUMN sys_config.tenant_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_config.tenant_id IS '租户编号';


--
-- Name: COLUMN sys_config.config_name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_config.config_name IS '参数名称';


--
-- Name: COLUMN sys_config.config_key; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_config.config_key IS '参数键名';


--
-- Name: COLUMN sys_config.config_value; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_config.config_value IS '参数键值';


--
-- Name: COLUMN sys_config.config_type; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_config.config_type IS '系统内置（Y是 N否）';


--
-- Name: COLUMN sys_config.create_dept; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_config.create_dept IS '创建部门';


--
-- Name: COLUMN sys_config.create_by; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_config.create_by IS '创建者';


--
-- Name: COLUMN sys_config.create_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_config.create_time IS '创建时间';


--
-- Name: COLUMN sys_config.update_by; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_config.update_by IS '更新者';


--
-- Name: COLUMN sys_config.update_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_config.update_time IS '更新时间';


--
-- Name: COLUMN sys_config.remark; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_config.remark IS '备注';


--
-- Name: sys_dept; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.sys_dept (
    dept_id bigint NOT NULL,
    tenant_id character varying(20) DEFAULT '000000'::character varying,
    parent_id bigint DEFAULT 0,
    ancestors character varying(500) DEFAULT ''::character varying,
    dept_name character varying(30) DEFAULT ''::character varying,
    dept_category character varying(100) DEFAULT NULL::character varying,
    order_num integer DEFAULT 0,
    leader bigint,
    phone character varying(11) DEFAULT NULL::character varying,
    email character varying(50) DEFAULT NULL::character varying,
    status character(1) DEFAULT '0'::bpchar,
    del_flag character(1) DEFAULT '0'::bpchar,
    create_dept bigint,
    create_by bigint,
    create_time timestamp without time zone,
    update_by bigint,
    update_time timestamp without time zone
);


--
-- Name: TABLE sys_dept; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.sys_dept IS '部门表';


--
-- Name: COLUMN sys_dept.dept_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_dept.dept_id IS '部门ID';


--
-- Name: COLUMN sys_dept.tenant_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_dept.tenant_id IS '租户编号';


--
-- Name: COLUMN sys_dept.parent_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_dept.parent_id IS '父部门ID';


--
-- Name: COLUMN sys_dept.ancestors; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_dept.ancestors IS '祖级列表';


--
-- Name: COLUMN sys_dept.dept_name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_dept.dept_name IS '部门名称';


--
-- Name: COLUMN sys_dept.dept_category; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_dept.dept_category IS '部门类别编码';


--
-- Name: COLUMN sys_dept.order_num; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_dept.order_num IS '显示顺序';


--
-- Name: COLUMN sys_dept.leader; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_dept.leader IS '负责人';


--
-- Name: COLUMN sys_dept.phone; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_dept.phone IS '联系电话';


--
-- Name: COLUMN sys_dept.email; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_dept.email IS '邮箱';


--
-- Name: COLUMN sys_dept.status; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_dept.status IS '部门状态（0正常 1停用）';


--
-- Name: COLUMN sys_dept.del_flag; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_dept.del_flag IS '删除标志（0代表存在 1代表删除）';


--
-- Name: COLUMN sys_dept.create_dept; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_dept.create_dept IS '创建部门';


--
-- Name: COLUMN sys_dept.create_by; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_dept.create_by IS '创建者';


--
-- Name: COLUMN sys_dept.create_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_dept.create_time IS '创建时间';


--
-- Name: COLUMN sys_dept.update_by; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_dept.update_by IS '更新者';


--
-- Name: COLUMN sys_dept.update_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_dept.update_time IS '更新时间';


--
-- Name: sys_dict_data; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.sys_dict_data (
    dict_code bigint NOT NULL,
    tenant_id character varying(20) DEFAULT '000000'::character varying,
    dict_sort integer DEFAULT 0,
    dict_label character varying(100) DEFAULT ''::character varying,
    dict_value character varying(100) DEFAULT ''::character varying,
    dict_type character varying(100) DEFAULT ''::character varying,
    css_class character varying(100) DEFAULT NULL::character varying,
    list_class character varying(100) DEFAULT NULL::character varying,
    is_default character(1) DEFAULT 'N'::bpchar,
    create_dept bigint,
    create_by bigint,
    create_time timestamp without time zone,
    update_by bigint,
    update_time timestamp without time zone,
    remark character varying(500) DEFAULT NULL::character varying
);


--
-- Name: TABLE sys_dict_data; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.sys_dict_data IS '字典数据表';


--
-- Name: COLUMN sys_dict_data.dict_code; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_dict_data.dict_code IS '字典编码';


--
-- Name: COLUMN sys_dict_data.dict_sort; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_dict_data.dict_sort IS '字典排序';


--
-- Name: COLUMN sys_dict_data.dict_label; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_dict_data.dict_label IS '字典标签';


--
-- Name: COLUMN sys_dict_data.dict_value; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_dict_data.dict_value IS '字典键值';


--
-- Name: COLUMN sys_dict_data.dict_type; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_dict_data.dict_type IS '字典类型';


--
-- Name: COLUMN sys_dict_data.css_class; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_dict_data.css_class IS '样式属性（其他样式扩展）';


--
-- Name: COLUMN sys_dict_data.list_class; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_dict_data.list_class IS '表格回显样式';


--
-- Name: COLUMN sys_dict_data.is_default; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_dict_data.is_default IS '是否默认（Y是 N否）';


--
-- Name: COLUMN sys_dict_data.create_dept; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_dict_data.create_dept IS '创建部门';


--
-- Name: COLUMN sys_dict_data.create_by; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_dict_data.create_by IS '创建者';


--
-- Name: COLUMN sys_dict_data.create_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_dict_data.create_time IS '创建时间';


--
-- Name: COLUMN sys_dict_data.update_by; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_dict_data.update_by IS '更新者';


--
-- Name: COLUMN sys_dict_data.update_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_dict_data.update_time IS '更新时间';


--
-- Name: COLUMN sys_dict_data.remark; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_dict_data.remark IS '备注';


--
-- Name: sys_dict_type; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.sys_dict_type (
    dict_id bigint NOT NULL,
    tenant_id character varying(20) DEFAULT '000000'::character varying,
    dict_name character varying(100) DEFAULT ''::character varying,
    dict_type character varying(100) DEFAULT ''::character varying,
    create_dept bigint,
    create_by bigint,
    create_time timestamp without time zone,
    update_by bigint,
    update_time timestamp without time zone,
    remark character varying(500) DEFAULT NULL::character varying
);


--
-- Name: TABLE sys_dict_type; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.sys_dict_type IS '字典类型表';


--
-- Name: COLUMN sys_dict_type.dict_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_dict_type.dict_id IS '字典主键';


--
-- Name: COLUMN sys_dict_type.tenant_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_dict_type.tenant_id IS '租户编号';


--
-- Name: COLUMN sys_dict_type.dict_name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_dict_type.dict_name IS '字典名称';


--
-- Name: COLUMN sys_dict_type.dict_type; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_dict_type.dict_type IS '字典类型';


--
-- Name: COLUMN sys_dict_type.create_dept; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_dict_type.create_dept IS '创建部门';


--
-- Name: COLUMN sys_dict_type.create_by; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_dict_type.create_by IS '创建者';


--
-- Name: COLUMN sys_dict_type.create_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_dict_type.create_time IS '创建时间';


--
-- Name: COLUMN sys_dict_type.update_by; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_dict_type.update_by IS '更新者';


--
-- Name: COLUMN sys_dict_type.update_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_dict_type.update_time IS '更新时间';


--
-- Name: COLUMN sys_dict_type.remark; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_dict_type.remark IS '备注';


--
-- Name: sys_menu; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.sys_menu (
    menu_id bigint NOT NULL,
    menu_name character varying(50) NOT NULL,
    parent_id bigint DEFAULT 0,
    order_num integer DEFAULT 0,
    path character varying(200) DEFAULT ''::character varying,
    component character varying(255) DEFAULT NULL::character varying,
    query_param character varying(255) DEFAULT NULL::character varying,
    is_frame character(1) DEFAULT '1'::bpchar,
    is_cache character(1) DEFAULT '0'::bpchar,
    menu_type character(1) DEFAULT ''::bpchar,
    visible character(1) DEFAULT '0'::bpchar,
    status character(1) DEFAULT '0'::bpchar,
    perms character varying(100) DEFAULT NULL::character varying,
    icon character varying(100) DEFAULT '#'::character varying,
    create_dept bigint,
    create_by bigint,
    create_time timestamp without time zone,
    update_by bigint,
    update_time timestamp without time zone,
    remark character varying(500) DEFAULT ''::character varying
);


--
-- Name: TABLE sys_menu; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.sys_menu IS '菜单权限表';


--
-- Name: COLUMN sys_menu.menu_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_menu.menu_id IS '菜单ID';


--
-- Name: COLUMN sys_menu.menu_name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_menu.menu_name IS '菜单名称';


--
-- Name: COLUMN sys_menu.parent_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_menu.parent_id IS '父菜单ID';


--
-- Name: COLUMN sys_menu.order_num; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_menu.order_num IS '显示顺序';


--
-- Name: COLUMN sys_menu.path; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_menu.path IS '路由地址';


--
-- Name: COLUMN sys_menu.component; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_menu.component IS '组件路径';


--
-- Name: COLUMN sys_menu.query_param; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_menu.query_param IS '路由参数';


--
-- Name: COLUMN sys_menu.is_frame; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_menu.is_frame IS '是否为外链（0是 1否）';


--
-- Name: COLUMN sys_menu.is_cache; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_menu.is_cache IS '是否缓存（0缓存 1不缓存）';


--
-- Name: COLUMN sys_menu.menu_type; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_menu.menu_type IS '菜单类型（M目录 C菜单 F按钮）';


--
-- Name: COLUMN sys_menu.visible; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_menu.visible IS '显示状态（0显示 1隐藏）';


--
-- Name: COLUMN sys_menu.status; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_menu.status IS '菜单状态（0正常 1停用）';


--
-- Name: COLUMN sys_menu.perms; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_menu.perms IS '权限标识';


--
-- Name: COLUMN sys_menu.icon; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_menu.icon IS '菜单图标';


--
-- Name: COLUMN sys_menu.create_dept; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_menu.create_dept IS '创建部门';


--
-- Name: COLUMN sys_menu.create_by; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_menu.create_by IS '创建者';


--
-- Name: COLUMN sys_menu.create_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_menu.create_time IS '创建时间';


--
-- Name: COLUMN sys_menu.update_by; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_menu.update_by IS '更新者';


--
-- Name: COLUMN sys_menu.update_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_menu.update_time IS '更新时间';


--
-- Name: COLUMN sys_menu.remark; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_menu.remark IS '备注';


--
-- Name: sys_notice; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.sys_notice (
    notice_id bigint NOT NULL,
    tenant_id character varying(20) DEFAULT '000000'::character varying,
    notice_title character varying(50) NOT NULL,
    notice_type character(1) NOT NULL,
    notice_content text,
    status character(1) DEFAULT '0'::bpchar,
    create_dept bigint,
    create_by bigint,
    create_time timestamp without time zone,
    update_by bigint,
    update_time timestamp without time zone,
    remark character varying(255) DEFAULT NULL::character varying
);


--
-- Name: TABLE sys_notice; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.sys_notice IS '通知公告表';


--
-- Name: COLUMN sys_notice.notice_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_notice.notice_id IS '公告ID';


--
-- Name: COLUMN sys_notice.tenant_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_notice.tenant_id IS '租户编号';


--
-- Name: COLUMN sys_notice.notice_title; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_notice.notice_title IS '公告标题';


--
-- Name: COLUMN sys_notice.notice_type; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_notice.notice_type IS '公告类型（1通知 2公告）';


--
-- Name: COLUMN sys_notice.notice_content; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_notice.notice_content IS '公告内容';


--
-- Name: COLUMN sys_notice.status; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_notice.status IS '公告状态（0正常 1关闭）';


--
-- Name: COLUMN sys_notice.create_dept; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_notice.create_dept IS '创建部门';


--
-- Name: COLUMN sys_notice.create_by; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_notice.create_by IS '创建者';


--
-- Name: COLUMN sys_notice.create_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_notice.create_time IS '创建时间';


--
-- Name: COLUMN sys_notice.update_by; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_notice.update_by IS '更新者';


--
-- Name: COLUMN sys_notice.update_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_notice.update_time IS '更新时间';


--
-- Name: COLUMN sys_notice.remark; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_notice.remark IS '备注';


--
-- Name: sys_oss; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.sys_oss (
    oss_id bigint NOT NULL,
    tenant_id character varying(20) DEFAULT '000000'::character varying,
    file_name character varying(255) DEFAULT ''::character varying NOT NULL,
    original_name character varying(255) DEFAULT ''::character varying NOT NULL,
    file_suffix character varying(10) DEFAULT ''::character varying NOT NULL,
    url character varying(500) DEFAULT ''::character varying NOT NULL,
    ext1 character varying(500) DEFAULT ''::character varying,
    create_dept bigint,
    create_by bigint,
    create_time timestamp without time zone,
    update_by bigint,
    update_time timestamp without time zone,
    service character varying(20) DEFAULT 'minio'::character varying
);


--
-- Name: TABLE sys_oss; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.sys_oss IS 'OSS对象存储表';


--
-- Name: COLUMN sys_oss.oss_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_oss.oss_id IS '对象存储主键';


--
-- Name: COLUMN sys_oss.tenant_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_oss.tenant_id IS '租户编码';


--
-- Name: COLUMN sys_oss.file_name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_oss.file_name IS '文件名';


--
-- Name: COLUMN sys_oss.original_name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_oss.original_name IS '原名';


--
-- Name: COLUMN sys_oss.file_suffix; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_oss.file_suffix IS '文件后缀名';


--
-- Name: COLUMN sys_oss.url; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_oss.url IS 'URL地址';


--
-- Name: COLUMN sys_oss.ext1; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_oss.ext1 IS '扩展字段';


--
-- Name: COLUMN sys_oss.create_dept; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_oss.create_dept IS '创建部门';


--
-- Name: COLUMN sys_oss.create_by; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_oss.create_by IS '上传人';


--
-- Name: COLUMN sys_oss.create_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_oss.create_time IS '创建时间';


--
-- Name: COLUMN sys_oss.update_by; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_oss.update_by IS '更新者';


--
-- Name: COLUMN sys_oss.update_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_oss.update_time IS '更新时间';


--
-- Name: COLUMN sys_oss.service; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_oss.service IS '服务商';


--
-- Name: sys_oss_config; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.sys_oss_config (
    oss_config_id bigint NOT NULL,
    tenant_id character varying(20) DEFAULT '000000'::character varying,
    config_key character varying(20) DEFAULT ''::character varying NOT NULL,
    access_key character varying(255) DEFAULT ''::character varying,
    secret_key character varying(255) DEFAULT ''::character varying,
    bucket_name character varying(255) DEFAULT ''::character varying,
    prefix character varying(255) DEFAULT ''::character varying,
    endpoint character varying(255) DEFAULT ''::character varying,
    domain character varying(255) DEFAULT ''::character varying,
    is_https character(1) DEFAULT 'N'::bpchar,
    region character varying(255) DEFAULT ''::character varying,
    access_policy character(1) DEFAULT '1'::bpchar NOT NULL,
    status character(1) DEFAULT '1'::bpchar,
    ext1 character varying(255) DEFAULT ''::character varying,
    create_dept bigint,
    create_by bigint,
    create_time timestamp without time zone,
    update_by bigint,
    update_time timestamp without time zone,
    remark character varying(500) DEFAULT ''::character varying
);


--
-- Name: TABLE sys_oss_config; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.sys_oss_config IS '对象存储配置表';


--
-- Name: COLUMN sys_oss_config.oss_config_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_oss_config.oss_config_id IS '主键';


--
-- Name: COLUMN sys_oss_config.tenant_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_oss_config.tenant_id IS '租户编码';


--
-- Name: COLUMN sys_oss_config.config_key; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_oss_config.config_key IS '配置key';


--
-- Name: COLUMN sys_oss_config.access_key; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_oss_config.access_key IS 'accessKey';


--
-- Name: COLUMN sys_oss_config.secret_key; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_oss_config.secret_key IS '秘钥';


--
-- Name: COLUMN sys_oss_config.bucket_name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_oss_config.bucket_name IS '桶名称';


--
-- Name: COLUMN sys_oss_config.prefix; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_oss_config.prefix IS '前缀';


--
-- Name: COLUMN sys_oss_config.endpoint; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_oss_config.endpoint IS '访问站点';


--
-- Name: COLUMN sys_oss_config.domain; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_oss_config.domain IS '自定义域名';


--
-- Name: COLUMN sys_oss_config.is_https; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_oss_config.is_https IS '是否https（Y=是,N=否）';


--
-- Name: COLUMN sys_oss_config.region; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_oss_config.region IS '域';


--
-- Name: COLUMN sys_oss_config.access_policy; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_oss_config.access_policy IS '桶权限类型(0=private 1=public 2=custom)';


--
-- Name: COLUMN sys_oss_config.status; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_oss_config.status IS '是否默认（0=是,1=否）';


--
-- Name: COLUMN sys_oss_config.ext1; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_oss_config.ext1 IS '扩展字段';


--
-- Name: COLUMN sys_oss_config.create_dept; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_oss_config.create_dept IS '创建部门';


--
-- Name: COLUMN sys_oss_config.create_by; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_oss_config.create_by IS '创建者';


--
-- Name: COLUMN sys_oss_config.create_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_oss_config.create_time IS '创建时间';


--
-- Name: COLUMN sys_oss_config.update_by; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_oss_config.update_by IS '更新者';


--
-- Name: COLUMN sys_oss_config.update_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_oss_config.update_time IS '更新时间';


--
-- Name: COLUMN sys_oss_config.remark; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_oss_config.remark IS '备注';


--
-- Name: sys_post; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.sys_post (
    post_id bigint NOT NULL,
    tenant_id character varying(20) DEFAULT '000000'::character varying,
    dept_id bigint,
    post_code character varying(64) NOT NULL,
    post_category character varying(100) DEFAULT NULL::character varying,
    post_name character varying(50) NOT NULL,
    post_sort integer NOT NULL,
    status character(1) NOT NULL,
    create_dept bigint,
    create_by bigint,
    create_time timestamp without time zone,
    update_by bigint,
    update_time timestamp without time zone,
    remark character varying(500) DEFAULT NULL::character varying
);


--
-- Name: TABLE sys_post; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.sys_post IS '岗位信息表';


--
-- Name: COLUMN sys_post.post_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_post.post_id IS '岗位ID';


--
-- Name: COLUMN sys_post.tenant_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_post.tenant_id IS '租户编号';


--
-- Name: COLUMN sys_post.dept_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_post.dept_id IS '部门id';


--
-- Name: COLUMN sys_post.post_code; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_post.post_code IS '岗位编码';


--
-- Name: COLUMN sys_post.post_category; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_post.post_category IS '岗位类别编码';


--
-- Name: COLUMN sys_post.post_name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_post.post_name IS '岗位名称';


--
-- Name: COLUMN sys_post.post_sort; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_post.post_sort IS '显示顺序';


--
-- Name: COLUMN sys_post.status; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_post.status IS '状态（0正常 1停用）';


--
-- Name: COLUMN sys_post.create_dept; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_post.create_dept IS '创建部门';


--
-- Name: COLUMN sys_post.create_by; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_post.create_by IS '创建者';


--
-- Name: COLUMN sys_post.create_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_post.create_time IS '创建时间';


--
-- Name: COLUMN sys_post.update_by; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_post.update_by IS '更新者';


--
-- Name: COLUMN sys_post.update_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_post.update_time IS '更新时间';


--
-- Name: COLUMN sys_post.remark; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_post.remark IS '备注';


--
-- Name: sys_role; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.sys_role (
    role_id bigint NOT NULL,
    tenant_id character varying(20) DEFAULT '000000'::character varying,
    role_name character varying(30) NOT NULL,
    role_key character varying(100) NOT NULL,
    role_sort integer NOT NULL,
    data_scope character(1) DEFAULT '1'::bpchar,
    menu_check_strictly boolean DEFAULT true,
    dept_check_strictly boolean DEFAULT true,
    status character(1) NOT NULL,
    del_flag character(1) DEFAULT '0'::bpchar,
    create_dept bigint,
    create_by bigint,
    create_time timestamp without time zone,
    update_by bigint,
    update_time timestamp without time zone,
    remark character varying(500) DEFAULT NULL::character varying
);


--
-- Name: TABLE sys_role; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.sys_role IS '角色信息表';


--
-- Name: COLUMN sys_role.role_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_role.role_id IS '角色ID';


--
-- Name: COLUMN sys_role.tenant_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_role.tenant_id IS '租户编号';


--
-- Name: COLUMN sys_role.role_name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_role.role_name IS '角色名称';


--
-- Name: COLUMN sys_role.role_key; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_role.role_key IS '角色权限字符串';


--
-- Name: COLUMN sys_role.role_sort; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_role.role_sort IS '显示顺序';


--
-- Name: COLUMN sys_role.data_scope; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_role.data_scope IS '数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限 5：仅本人数据权限 6：部门及以下或本人数据权限）';


--
-- Name: COLUMN sys_role.menu_check_strictly; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_role.menu_check_strictly IS '菜单树选择项是否关联显示';


--
-- Name: COLUMN sys_role.dept_check_strictly; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_role.dept_check_strictly IS '部门树选择项是否关联显示';


--
-- Name: COLUMN sys_role.status; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_role.status IS '角色状态（0正常 1停用）';


--
-- Name: COLUMN sys_role.del_flag; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_role.del_flag IS '删除标志（0代表存在 1代表删除）';


--
-- Name: COLUMN sys_role.create_dept; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_role.create_dept IS '创建部门';


--
-- Name: COLUMN sys_role.create_by; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_role.create_by IS '创建者';


--
-- Name: COLUMN sys_role.create_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_role.create_time IS '创建时间';


--
-- Name: COLUMN sys_role.update_by; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_role.update_by IS '更新者';


--
-- Name: COLUMN sys_role.update_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_role.update_time IS '更新时间';


--
-- Name: COLUMN sys_role.remark; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_role.remark IS '备注';


--
-- Name: sys_role_dept; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.sys_role_dept (
    role_id bigint NOT NULL,
    dept_id bigint NOT NULL
);


--
-- Name: TABLE sys_role_dept; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.sys_role_dept IS '角色和部门关联表';


--
-- Name: COLUMN sys_role_dept.role_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_role_dept.role_id IS '角色ID';


--
-- Name: COLUMN sys_role_dept.dept_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_role_dept.dept_id IS '部门ID';


--
-- Name: sys_role_menu; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.sys_role_menu (
    role_id bigint NOT NULL,
    menu_id bigint NOT NULL
);


--
-- Name: TABLE sys_role_menu; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.sys_role_menu IS '角色和菜单关联表';


--
-- Name: COLUMN sys_role_menu.role_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_role_menu.role_id IS '角色ID';


--
-- Name: COLUMN sys_role_menu.menu_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_role_menu.menu_id IS '菜单ID';


--
-- Name: sys_social; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.sys_social (
    id bigint NOT NULL,
    user_id bigint NOT NULL,
    tenant_id character varying(20) DEFAULT '000000'::character varying,
    auth_id character varying(255) NOT NULL,
    source character varying(255) NOT NULL,
    open_id character varying(255) DEFAULT NULL::character varying,
    user_name character varying(30) NOT NULL,
    nick_name character varying(30) DEFAULT ''::character varying,
    email character varying(255) DEFAULT ''::character varying,
    avatar character varying(500) DEFAULT ''::character varying,
    access_token character varying(2000) NOT NULL,
    expire_in bigint,
    refresh_token character varying(2000) DEFAULT NULL::character varying,
    access_code character varying(255) DEFAULT NULL::character varying,
    union_id character varying(255) DEFAULT NULL::character varying,
    scope character varying(255) DEFAULT NULL::character varying,
    token_type character varying(255) DEFAULT NULL::character varying,
    id_token character varying(2000) DEFAULT NULL::character varying,
    mac_algorithm character varying(255) DEFAULT NULL::character varying,
    mac_key character varying(255) DEFAULT NULL::character varying,
    code character varying(255) DEFAULT NULL::character varying,
    oauth_token character varying(255) DEFAULT NULL::character varying,
    oauth_token_secret character varying(255) DEFAULT NULL::character varying,
    create_dept bigint,
    create_by bigint,
    create_time timestamp without time zone,
    update_by bigint,
    update_time timestamp without time zone,
    del_flag character(1) DEFAULT '0'::bpchar
);


--
-- Name: TABLE sys_social; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.sys_social IS '社会化关系表';


--
-- Name: COLUMN sys_social.id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_social.id IS '主键';


--
-- Name: COLUMN sys_social.user_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_social.user_id IS '用户ID';


--
-- Name: COLUMN sys_social.tenant_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_social.tenant_id IS '租户id';


--
-- Name: COLUMN sys_social.auth_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_social.auth_id IS '平台+平台唯一id';


--
-- Name: COLUMN sys_social.source; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_social.source IS '用户来源';


--
-- Name: COLUMN sys_social.open_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_social.open_id IS '平台编号唯一id';


--
-- Name: COLUMN sys_social.user_name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_social.user_name IS '登录账号';


--
-- Name: COLUMN sys_social.nick_name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_social.nick_name IS '用户昵称';


--
-- Name: COLUMN sys_social.email; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_social.email IS '用户邮箱';


--
-- Name: COLUMN sys_social.avatar; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_social.avatar IS '头像地址';


--
-- Name: COLUMN sys_social.access_token; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_social.access_token IS '用户的授权令牌';


--
-- Name: COLUMN sys_social.expire_in; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_social.expire_in IS '用户的授权令牌的有效期，部分平台可能没有';


--
-- Name: COLUMN sys_social.refresh_token; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_social.refresh_token IS '刷新令牌，部分平台可能没有';


--
-- Name: COLUMN sys_social.access_code; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_social.access_code IS '平台的授权信息，部分平台可能没有';


--
-- Name: COLUMN sys_social.union_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_social.union_id IS '用户的 unionid';


--
-- Name: COLUMN sys_social.scope; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_social.scope IS '授予的权限，部分平台可能没有';


--
-- Name: COLUMN sys_social.token_type; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_social.token_type IS '个别平台的授权信息，部分平台可能没有';


--
-- Name: COLUMN sys_social.id_token; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_social.id_token IS 'id token，部分平台可能没有';


--
-- Name: COLUMN sys_social.mac_algorithm; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_social.mac_algorithm IS '小米平台用户的附带属性，部分平台可能没有';


--
-- Name: COLUMN sys_social.mac_key; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_social.mac_key IS '小米平台用户的附带属性，部分平台可能没有';


--
-- Name: COLUMN sys_social.code; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_social.code IS '用户的授权code，部分平台可能没有';


--
-- Name: COLUMN sys_social.oauth_token; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_social.oauth_token IS 'Twitter平台用户的附带属性，部分平台可能没有';


--
-- Name: COLUMN sys_social.oauth_token_secret; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_social.oauth_token_secret IS 'Twitter平台用户的附带属性，部分平台可能没有';


--
-- Name: COLUMN sys_social.create_dept; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_social.create_dept IS '创建部门';


--
-- Name: COLUMN sys_social.create_by; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_social.create_by IS '创建者';


--
-- Name: COLUMN sys_social.create_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_social.create_time IS '创建时间';


--
-- Name: COLUMN sys_social.update_by; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_social.update_by IS '更新者';


--
-- Name: COLUMN sys_social.update_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_social.update_time IS '更新时间';


--
-- Name: COLUMN sys_social.del_flag; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_social.del_flag IS '删除标志（0代表存在 1代表删除）';


--
-- Name: sys_tenant; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.sys_tenant (
    id bigint NOT NULL,
    tenant_id character varying(20) NOT NULL,
    contact_user_name character varying(20) DEFAULT NULL::character varying,
    contact_phone character varying(20) DEFAULT NULL::character varying,
    company_name character varying(30) DEFAULT NULL::character varying,
    license_number character varying(30) DEFAULT NULL::character varying,
    address character varying(200) DEFAULT NULL::character varying,
    intro character varying(200) DEFAULT NULL::character varying,
    domain character varying(200) DEFAULT NULL::character varying,
    remark character varying(200) DEFAULT NULL::character varying,
    package_id bigint,
    expire_time timestamp without time zone,
    account_count integer DEFAULT '-1'::integer,
    status character(1) DEFAULT '0'::bpchar,
    del_flag character(1) DEFAULT '0'::bpchar,
    create_dept bigint,
    create_by bigint,
    create_time timestamp without time zone,
    update_by bigint,
    update_time timestamp without time zone
);


--
-- Name: TABLE sys_tenant; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.sys_tenant IS '租户表';


--
-- Name: COLUMN sys_tenant.tenant_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_tenant.tenant_id IS '租户编号';


--
-- Name: COLUMN sys_tenant.contact_phone; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_tenant.contact_phone IS '联系电话';


--
-- Name: COLUMN sys_tenant.company_name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_tenant.company_name IS '联系人';


--
-- Name: COLUMN sys_tenant.license_number; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_tenant.license_number IS '统一社会信用代码';


--
-- Name: COLUMN sys_tenant.address; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_tenant.address IS '地址';


--
-- Name: COLUMN sys_tenant.intro; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_tenant.intro IS '企业简介';


--
-- Name: COLUMN sys_tenant.domain; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_tenant.domain IS '域名';


--
-- Name: COLUMN sys_tenant.remark; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_tenant.remark IS '备注';


--
-- Name: COLUMN sys_tenant.package_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_tenant.package_id IS '租户套餐编号';


--
-- Name: COLUMN sys_tenant.expire_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_tenant.expire_time IS '过期时间';


--
-- Name: COLUMN sys_tenant.account_count; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_tenant.account_count IS '用户数量（-1不限制）';


--
-- Name: COLUMN sys_tenant.status; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_tenant.status IS '租户状态（0正常 1停用）';


--
-- Name: COLUMN sys_tenant.del_flag; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_tenant.del_flag IS '删除标志（0代表存在 1代表删除）';


--
-- Name: COLUMN sys_tenant.create_dept; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_tenant.create_dept IS '创建部门';


--
-- Name: COLUMN sys_tenant.create_by; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_tenant.create_by IS '创建者';


--
-- Name: COLUMN sys_tenant.create_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_tenant.create_time IS '创建时间';


--
-- Name: COLUMN sys_tenant.update_by; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_tenant.update_by IS '更新者';


--
-- Name: COLUMN sys_tenant.update_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_tenant.update_time IS '更新时间';


--
-- Name: sys_tenant_package; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.sys_tenant_package (
    package_id bigint NOT NULL,
    package_name character varying(20) DEFAULT ''::character varying,
    menu_ids character varying(3000) DEFAULT ''::character varying,
    remark character varying(200) DEFAULT ''::character varying,
    menu_check_strictly boolean DEFAULT true,
    status character(1) DEFAULT '0'::bpchar,
    del_flag character(1) DEFAULT '0'::bpchar,
    create_dept bigint,
    create_by bigint,
    create_time timestamp without time zone,
    update_by bigint,
    update_time timestamp without time zone
);


--
-- Name: TABLE sys_tenant_package; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.sys_tenant_package IS '租户套餐表';


--
-- Name: COLUMN sys_tenant_package.package_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_tenant_package.package_id IS '租户套餐id';


--
-- Name: COLUMN sys_tenant_package.package_name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_tenant_package.package_name IS '套餐名称';


--
-- Name: COLUMN sys_tenant_package.menu_ids; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_tenant_package.menu_ids IS '关联菜单id';


--
-- Name: COLUMN sys_tenant_package.remark; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_tenant_package.remark IS '备注';


--
-- Name: COLUMN sys_tenant_package.status; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_tenant_package.status IS '状态（0正常 1停用）';


--
-- Name: COLUMN sys_tenant_package.del_flag; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_tenant_package.del_flag IS '删除标志（0代表存在 1代表删除）';


--
-- Name: COLUMN sys_tenant_package.create_dept; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_tenant_package.create_dept IS '创建部门';


--
-- Name: COLUMN sys_tenant_package.create_by; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_tenant_package.create_by IS '创建者';


--
-- Name: COLUMN sys_tenant_package.create_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_tenant_package.create_time IS '创建时间';


--
-- Name: COLUMN sys_tenant_package.update_by; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_tenant_package.update_by IS '更新者';


--
-- Name: COLUMN sys_tenant_package.update_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_tenant_package.update_time IS '更新时间';


--
-- Name: sys_user; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.sys_user (
    user_id bigint NOT NULL,
    tenant_id character varying(20) DEFAULT '000000'::character varying,
    dept_id bigint,
    user_name character varying(30) NOT NULL,
    nick_name character varying(30) NOT NULL,
    user_type character varying(10) DEFAULT 'sys_user'::character varying,
    email character varying(50) DEFAULT ''::character varying,
    phonenumber character varying(11) DEFAULT ''::character varying,
    sex character(1) DEFAULT '0'::bpchar,
    avatar bigint,
    password character varying(100) DEFAULT ''::character varying,
    status character(1) DEFAULT '0'::bpchar,
    del_flag character(1) DEFAULT '0'::bpchar,
    login_ip character varying(128) DEFAULT ''::character varying,
    login_date timestamp without time zone,
    create_dept bigint,
    create_by bigint,
    create_time timestamp without time zone,
    update_by bigint,
    update_time timestamp without time zone,
    remark character varying(500) DEFAULT NULL::character varying
);


--
-- Name: TABLE sys_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.sys_user IS '用户信息表';


--
-- Name: COLUMN sys_user.user_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_user.user_id IS '用户ID';


--
-- Name: COLUMN sys_user.tenant_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_user.tenant_id IS '租户编号';


--
-- Name: COLUMN sys_user.dept_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_user.dept_id IS '部门ID';


--
-- Name: COLUMN sys_user.user_name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_user.user_name IS '用户账号';


--
-- Name: COLUMN sys_user.nick_name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_user.nick_name IS '用户昵称';


--
-- Name: COLUMN sys_user.user_type; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_user.user_type IS '用户类型（sys_user系统用户）';


--
-- Name: COLUMN sys_user.email; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_user.email IS '用户邮箱';


--
-- Name: COLUMN sys_user.phonenumber; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_user.phonenumber IS '手机号码';


--
-- Name: COLUMN sys_user.sex; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_user.sex IS '用户性别（0男 1女 2未知）';


--
-- Name: COLUMN sys_user.avatar; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_user.avatar IS '头像地址';


--
-- Name: COLUMN sys_user.password; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_user.password IS '密码';


--
-- Name: COLUMN sys_user.status; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_user.status IS '账号状态（0正常 1停用）';


--
-- Name: COLUMN sys_user.del_flag; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_user.del_flag IS '删除标志（0代表存在 1代表删除）';


--
-- Name: COLUMN sys_user.login_ip; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_user.login_ip IS '最后登陆IP';


--
-- Name: COLUMN sys_user.login_date; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_user.login_date IS '最后登陆时间';


--
-- Name: COLUMN sys_user.create_dept; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_user.create_dept IS '创建部门';


--
-- Name: COLUMN sys_user.create_by; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_user.create_by IS '创建者';


--
-- Name: COLUMN sys_user.create_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_user.create_time IS '创建时间';


--
-- Name: COLUMN sys_user.update_by; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_user.update_by IS '更新者';


--
-- Name: COLUMN sys_user.update_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_user.update_time IS '更新时间';


--
-- Name: COLUMN sys_user.remark; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_user.remark IS '备注';


--
-- Name: sys_user_post; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.sys_user_post (
    user_id bigint NOT NULL,
    post_id bigint NOT NULL
);


--
-- Name: TABLE sys_user_post; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.sys_user_post IS '用户与岗位关联表';


--
-- Name: COLUMN sys_user_post.user_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_user_post.user_id IS '用户ID';


--
-- Name: COLUMN sys_user_post.post_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_user_post.post_id IS '岗位ID';


--
-- Name: sys_user_role; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.sys_user_role (
    user_id bigint NOT NULL,
    role_id bigint NOT NULL
);


--
-- Name: TABLE sys_user_role; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.sys_user_role IS '用户和角色关联表';


--
-- Name: COLUMN sys_user_role.user_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_user_role.user_id IS '用户ID';


--
-- Name: COLUMN sys_user_role.role_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.sys_user_role.role_id IS '角色ID';


--
-- Data for Name: sys_config; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.sys_config VALUES (1, '000000', '主框架页-默认皮肤样式名称', 'sys.index.skinName', 'skin-blue', 'Y', 103, 1, '2026-03-17 13:37:18.421606', NULL, NULL, '蓝色 skin-blue、绿色 skin-green、紫色 skin-purple、红色 skin-red、黄色 skin-yellow');
INSERT INTO public.sys_config VALUES (2, '000000', '用户管理-账号初始密码', 'sys.user.initPassword', '123456', 'Y', 103, 1, '2026-03-17 13:37:18.426372', NULL, NULL, '初始化密码 123456');
INSERT INTO public.sys_config VALUES (3, '000000', '主框架页-侧边栏主题', 'sys.index.sideTheme', 'theme-dark', 'Y', 103, 1, '2026-03-17 13:37:18.430582', NULL, NULL, '深色主题theme-dark，浅色主题theme-light');
INSERT INTO public.sys_config VALUES (5, '000000', '账号自助-是否开启用户注册功能', 'sys.account.registerUser', 'false', 'Y', 103, 1, '2026-03-17 13:37:18.434822', NULL, NULL, '是否开启注册用户功能（true开启，false关闭）');
INSERT INTO public.sys_config VALUES (11, '000000', 'OSS预览列表资源开关', 'sys.oss.previewListResource', 'true', 'Y', 103, 1, '2026-03-17 13:37:18.439', NULL, NULL, 'true:开启, false:关闭');


--
-- Data for Name: sys_dept; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.sys_dept VALUES (100, '000000', 0, '0', 'XXX科技', NULL, 0, NULL, '15888888888', 'xxx@qq.com', '0', '0', 103, 1, '2026-03-17 13:37:15.902009', NULL, NULL);
INSERT INTO public.sys_dept VALUES (101, '000000', 100, '0,100', '深圳总公司', NULL, 1, NULL, '15888888888', 'xxx@qq.com', '0', '0', 103, 1, '2026-03-17 13:37:15.908055', NULL, NULL);
INSERT INTO public.sys_dept VALUES (102, '000000', 100, '0,100', '长沙分公司', NULL, 2, NULL, '15888888888', 'xxx@qq.com', '0', '0', 103, 1, '2026-03-17 13:37:15.913578', NULL, NULL);
INSERT INTO public.sys_dept VALUES (103, '000000', 101, '0,100,101', '研发部门', NULL, 1, 1, '15888888888', 'xxx@qq.com', '0', '0', 103, 1, '2026-03-17 13:37:15.919246', NULL, NULL);
INSERT INTO public.sys_dept VALUES (104, '000000', 101, '0,100,101', '市场部门', NULL, 2, NULL, '15888888888', 'xxx@qq.com', '0', '0', 103, 1, '2026-03-17 13:37:15.924895', NULL, NULL);
INSERT INTO public.sys_dept VALUES (105, '000000', 101, '0,100,101', '测试部门', NULL, 3, NULL, '15888888888', 'xxx@qq.com', '0', '0', 103, 1, '2026-03-17 13:37:15.930043', NULL, NULL);
INSERT INTO public.sys_dept VALUES (106, '000000', 101, '0,100,101', '财务部门', NULL, 4, NULL, '15888888888', 'xxx@qq.com', '0', '0', 103, 1, '2026-03-17 13:37:15.935886', NULL, NULL);
INSERT INTO public.sys_dept VALUES (107, '000000', 101, '0,100,101', '运维部门', NULL, 5, NULL, '15888888888', 'xxx@qq.com', '0', '0', 103, 1, '2026-03-17 13:37:15.941201', NULL, NULL);
INSERT INTO public.sys_dept VALUES (108, '000000', 102, '0,100,102', '市场部门', NULL, 1, NULL, '15888888888', 'xxx@qq.com', '0', '0', 103, 1, '2026-03-17 13:37:15.94639', NULL, NULL);
INSERT INTO public.sys_dept VALUES (109, '000000', 102, '0,100,102', '财务部门', NULL, 2, NULL, '15888888888', 'xxx@qq.com', '0', '0', 103, 1, '2026-03-17 13:37:15.952342', NULL, NULL);


--
-- Data for Name: sys_dict_data; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.sys_dict_data VALUES (1, '000000', 1, '男', '0', 'sys_user_sex', '', '', 'Y', 103, 1, '2026-03-17 13:37:18.192292', NULL, NULL, '性别男');
INSERT INTO public.sys_dict_data VALUES (2, '000000', 2, '女', '1', 'sys_user_sex', '', '', 'N', 103, 1, '2026-03-17 13:37:18.197329', NULL, NULL, '性别女');
INSERT INTO public.sys_dict_data VALUES (3, '000000', 3, '未知', '2', 'sys_user_sex', '', '', 'N', 103, 1, '2026-03-17 13:37:18.201701', NULL, NULL, '性别未知');
INSERT INTO public.sys_dict_data VALUES (4, '000000', 1, '显示', '0', 'sys_show_hide', '', 'primary', 'Y', 103, 1, '2026-03-17 13:37:18.206281', NULL, NULL, '显示菜单');
INSERT INTO public.sys_dict_data VALUES (5, '000000', 2, '隐藏', '1', 'sys_show_hide', '', 'danger', 'N', 103, 1, '2026-03-17 13:37:18.210474', NULL, NULL, '隐藏菜单');
INSERT INTO public.sys_dict_data VALUES (6, '000000', 1, '正常', '0', 'sys_normal_disable', '', 'primary', 'Y', 103, 1, '2026-03-17 13:37:18.214585', NULL, NULL, '正常状态');
INSERT INTO public.sys_dict_data VALUES (7, '000000', 2, '停用', '1', 'sys_normal_disable', '', 'danger', 'N', 103, 1, '2026-03-17 13:37:18.218765', NULL, NULL, '停用状态');
INSERT INTO public.sys_dict_data VALUES (12, '000000', 1, '是', 'Y', 'sys_yes_no', '', 'primary', 'Y', 103, 1, '2026-03-17 13:37:18.223408', NULL, NULL, '系统默认是');
INSERT INTO public.sys_dict_data VALUES (13, '000000', 2, '否', 'N', 'sys_yes_no', '', 'danger', 'N', 103, 1, '2026-03-17 13:37:18.227672', NULL, NULL, '系统默认否');
INSERT INTO public.sys_dict_data VALUES (14, '000000', 1, '通知', '1', 'sys_notice_type', '', 'warning', 'Y', 103, 1, '2026-03-17 13:37:18.231919', NULL, NULL, '通知');
INSERT INTO public.sys_dict_data VALUES (15, '000000', 2, '公告', '2', 'sys_notice_type', '', 'success', 'N', 103, 1, '2026-03-17 13:37:18.236556', NULL, NULL, '公告');
INSERT INTO public.sys_dict_data VALUES (16, '000000', 1, '正常', '0', 'sys_notice_status', '', 'primary', 'Y', 103, 1, '2026-03-17 13:37:18.241358', NULL, NULL, '正常状态');
INSERT INTO public.sys_dict_data VALUES (17, '000000', 2, '关闭', '1', 'sys_notice_status', '', 'danger', 'N', 103, 1, '2026-03-17 13:37:18.264352', NULL, NULL, '关闭状态');
INSERT INTO public.sys_dict_data VALUES (29, '000000', 99, '其他', '0', 'sys_oper_type', '', 'info', 'N', 103, 1, '2026-03-17 13:37:18.2691', NULL, NULL, '其他操作');
INSERT INTO public.sys_dict_data VALUES (18, '000000', 1, '新增', '1', 'sys_oper_type', '', 'info', 'N', 103, 1, '2026-03-17 13:37:18.273815', NULL, NULL, '新增操作');
INSERT INTO public.sys_dict_data VALUES (19, '000000', 2, '修改', '2', 'sys_oper_type', '', 'info', 'N', 103, 1, '2026-03-17 13:37:18.278463', NULL, NULL, '修改操作');
INSERT INTO public.sys_dict_data VALUES (20, '000000', 3, '删除', '3', 'sys_oper_type', '', 'danger', 'N', 103, 1, '2026-03-17 13:37:18.283574', NULL, NULL, '删除操作');
INSERT INTO public.sys_dict_data VALUES (21, '000000', 4, '授权', '4', 'sys_oper_type', '', 'primary', 'N', 103, 1, '2026-03-17 13:37:18.288731', NULL, NULL, '授权操作');
INSERT INTO public.sys_dict_data VALUES (22, '000000', 5, '导出', '5', 'sys_oper_type', '', 'warning', 'N', 103, 1, '2026-03-17 13:37:18.293594', NULL, NULL, '导出操作');
INSERT INTO public.sys_dict_data VALUES (23, '000000', 6, '导入', '6', 'sys_oper_type', '', 'warning', 'N', 103, 1, '2026-03-17 13:37:18.298704', NULL, NULL, '导入操作');
INSERT INTO public.sys_dict_data VALUES (24, '000000', 7, '强退', '7', 'sys_oper_type', '', 'danger', 'N', 103, 1, '2026-03-17 13:37:18.303183', NULL, NULL, '强退操作');
INSERT INTO public.sys_dict_data VALUES (25, '000000', 8, '生成代码', '8', 'sys_oper_type', '', 'warning', 'N', 103, 1, '2026-03-17 13:37:18.307874', NULL, NULL, '生成操作');
INSERT INTO public.sys_dict_data VALUES (26, '000000', 9, '清空数据', '9', 'sys_oper_type', '', 'danger', 'N', 103, 1, '2026-03-17 13:37:18.312615', NULL, NULL, '清空操作');
INSERT INTO public.sys_dict_data VALUES (27, '000000', 1, '成功', '0', 'sys_common_status', '', 'primary', 'N', 103, 1, '2026-03-17 13:37:18.316937', NULL, NULL, '正常状态');
INSERT INTO public.sys_dict_data VALUES (28, '000000', 2, '失败', '1', 'sys_common_status', '', 'danger', 'N', 103, 1, '2026-03-17 13:37:18.321057', NULL, NULL, '停用状态');
INSERT INTO public.sys_dict_data VALUES (30, '000000', 0, '密码认证', 'password', 'sys_grant_type', '', 'default', 'N', 103, 1, '2026-03-17 13:37:18.325533', NULL, NULL, '密码认证');
INSERT INTO public.sys_dict_data VALUES (31, '000000', 0, '短信认证', 'sms', 'sys_grant_type', '', 'default', 'N', 103, 1, '2026-03-17 13:37:18.329686', NULL, NULL, '短信认证');
INSERT INTO public.sys_dict_data VALUES (32, '000000', 0, '邮件认证', 'email', 'sys_grant_type', '', 'default', 'N', 103, 1, '2026-03-17 13:37:18.33372', NULL, NULL, '邮件认证');
INSERT INTO public.sys_dict_data VALUES (33, '000000', 0, '小程序认证', 'xcx', 'sys_grant_type', '', 'default', 'N', 103, 1, '2026-03-17 13:37:18.337874', NULL, NULL, '小程序认证');
INSERT INTO public.sys_dict_data VALUES (34, '000000', 0, '三方登录认证', 'social', 'sys_grant_type', '', 'default', 'N', 103, 1, '2026-03-17 13:37:18.342279', NULL, NULL, '三方登录认证');
INSERT INTO public.sys_dict_data VALUES (35, '000000', 0, 'PC', 'pc', 'sys_device_type', '', 'default', 'N', 103, 1, '2026-03-17 13:37:18.34653', NULL, NULL, 'PC');
INSERT INTO public.sys_dict_data VALUES (36, '000000', 0, '安卓', 'android', 'sys_device_type', '', 'default', 'N', 103, 1, '2026-03-17 13:37:18.351119', NULL, NULL, '安卓');
INSERT INTO public.sys_dict_data VALUES (37, '000000', 0, 'iOS', 'ios', 'sys_device_type', '', 'default', 'N', 103, 1, '2026-03-17 13:37:18.355755', NULL, NULL, 'iOS');
INSERT INTO public.sys_dict_data VALUES (38, '000000', 0, '小程序', 'xcx', 'sys_device_type', '', 'default', 'N', 103, 1, '2026-03-17 13:37:18.360098', NULL, NULL, '小程序');


--
-- Data for Name: sys_dict_type; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.sys_dict_type VALUES (1, '000000', '用户性别', 'sys_user_sex', 103, 1, '2026-03-17 13:37:18.07062', NULL, NULL, '用户性别列表');
INSERT INTO public.sys_dict_type VALUES (2, '000000', '菜单状态', 'sys_show_hide', 103, 1, '2026-03-17 13:37:18.07535', NULL, NULL, '菜单状态列表');
INSERT INTO public.sys_dict_type VALUES (3, '000000', '系统开关', 'sys_normal_disable', 103, 1, '2026-03-17 13:37:18.079801', NULL, NULL, '系统开关列表');
INSERT INTO public.sys_dict_type VALUES (6, '000000', '系统是否', 'sys_yes_no', 103, 1, '2026-03-17 13:37:18.084549', NULL, NULL, '系统是否列表');
INSERT INTO public.sys_dict_type VALUES (7, '000000', '通知类型', 'sys_notice_type', 103, 1, '2026-03-17 13:37:18.089058', NULL, NULL, '通知类型列表');
INSERT INTO public.sys_dict_type VALUES (8, '000000', '通知状态', 'sys_notice_status', 103, 1, '2026-03-17 13:37:18.093823', NULL, NULL, '通知状态列表');
INSERT INTO public.sys_dict_type VALUES (9, '000000', '操作类型', 'sys_oper_type', 103, 1, '2026-03-17 13:37:18.098168', NULL, NULL, '操作类型列表');
INSERT INTO public.sys_dict_type VALUES (10, '000000', '系统状态', 'sys_common_status', 103, 1, '2026-03-17 13:37:18.102607', NULL, NULL, '登录状态列表');
INSERT INTO public.sys_dict_type VALUES (11, '000000', '授权类型', 'sys_grant_type', 103, 1, '2026-03-17 13:37:18.10756', NULL, NULL, '认证授权类型');
INSERT INTO public.sys_dict_type VALUES (12, '000000', '设备类型', 'sys_device_type', 103, 1, '2026-03-17 13:37:18.112042', NULL, NULL, '客户端设备类型');


--
-- Data for Name: sys_menu; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.sys_menu VALUES (109, '在线用户', 2, 1, 'online', 'monitor/online/index', '', '1', '0', 'C', '0', '0', 'monitor:online:list', 'online', 103, 1, '2026-03-17 13:37:16.603421', NULL, NULL, '在线用户菜单');
INSERT INTO public.sys_menu VALUES (130, '分配用户', 1, 2, 'role-auth/user/:roleId', 'system/role/authUser', '', '1', '1', 'C', '1', '0', 'system:role:edit', '#', 103, 1, '2026-03-17 13:37:16.66914', NULL, NULL, '/system/role');
INSERT INTO public.sys_menu VALUES (131, '分配角色', 1, 1, 'user-auth/role/:userId', 'system/user/authRole', '', '1', '1', 'C', '1', '0', 'system:user:edit', '#', 103, 1, '2026-03-17 13:37:16.692649', NULL, NULL, '/system/user');
INSERT INTO public.sys_menu VALUES (132, '字典数据', 1, 6, 'dict-data/index/:dictId', 'system/dict/data', '', '1', '1', 'C', '1', '0', 'system:dict:list', '#', 103, 1, '2026-03-17 13:37:16.699683', NULL, NULL, '/system/dict');
INSERT INTO public.sys_menu VALUES (133, '文件配置管理', 1, 10, 'oss-config/index', 'system/oss/config', '', '1', '1', 'C', '1', '0', 'system:ossConfig:list', '#', 103, 1, '2026-03-17 13:37:16.705616', NULL, NULL, '/system/oss');
INSERT INTO public.sys_menu VALUES (500, '操作日志', 108, 1, 'operlog', 'monitor/operlog/index', '', '1', '0', 'C', '0', '0', 'monitor:operlog:list', 'form', 103, 1, '2026-03-17 13:37:16.729949', NULL, NULL, '操作日志菜单');
INSERT INTO public.sys_menu VALUES (501, '登录日志', 108, 2, 'logininfor', 'monitor/logininfor/index', '', '1', '0', 'C', '0', '0', 'monitor:logininfor:list', 'logininfor', 103, 1, '2026-03-17 13:37:16.736153', NULL, NULL, '登录日志菜单');
INSERT INTO public.sys_menu VALUES (1001, '用户查询', 100, 1, '', '', '', '1', '0', 'F', '0', '0', 'system:user:query', '#', 103, 1, '2026-03-17 13:37:16.741953', NULL, NULL, '');
INSERT INTO public.sys_menu VALUES (1002, '用户新增', 100, 2, '', '', '', '1', '0', 'F', '0', '0', 'system:user:add', '#', 103, 1, '2026-03-17 13:37:16.747155', NULL, NULL, '');
INSERT INTO public.sys_menu VALUES (1003, '用户修改', 100, 3, '', '', '', '1', '0', 'F', '0', '0', 'system:user:edit', '#', 103, 1, '2026-03-17 13:37:16.7524', NULL, NULL, '');
INSERT INTO public.sys_menu VALUES (1004, '用户删除', 100, 4, '', '', '', '1', '0', 'F', '0', '0', 'system:user:remove', '#', 103, 1, '2026-03-17 13:37:16.757701', NULL, NULL, '');
INSERT INTO public.sys_menu VALUES (1005, '用户导出', 100, 5, '', '', '', '1', '0', 'F', '0', '0', 'system:user:export', '#', 103, 1, '2026-03-17 13:37:16.763663', NULL, NULL, '');
INSERT INTO public.sys_menu VALUES (1006, '用户导入', 100, 6, '', '', '', '1', '0', 'F', '0', '0', 'system:user:import', '#', 103, 1, '2026-03-17 13:37:16.768436', NULL, NULL, '');
INSERT INTO public.sys_menu VALUES (1007, '重置密码', 100, 7, '', '', '', '1', '0', 'F', '0', '0', 'system:user:resetPwd', '#', 103, 1, '2026-03-17 13:37:16.77385', NULL, NULL, '');
INSERT INTO public.sys_menu VALUES (1008, '角色查询', 101, 1, '', '', '', '1', '0', 'F', '0', '0', 'system:role:query', '#', 103, 1, '2026-03-17 13:37:16.778886', NULL, NULL, '');
INSERT INTO public.sys_menu VALUES (1009, '角色新增', 101, 2, '', '', '', '1', '0', 'F', '0', '0', 'system:role:add', '#', 103, 1, '2026-03-17 13:37:16.784219', NULL, NULL, '');
INSERT INTO public.sys_menu VALUES (1010, '角色修改', 101, 3, '', '', '', '1', '0', 'F', '0', '0', 'system:role:edit', '#', 103, 1, '2026-03-17 13:37:16.789148', NULL, NULL, '');
INSERT INTO public.sys_menu VALUES (1011, '角色删除', 101, 4, '', '', '', '1', '0', 'F', '0', '0', 'system:role:remove', '#', 103, 1, '2026-03-17 13:37:16.794195', NULL, NULL, '');
INSERT INTO public.sys_menu VALUES (1012, '角色导出', 101, 5, '', '', '', '1', '0', 'F', '0', '0', 'system:role:export', '#', 103, 1, '2026-03-17 13:37:16.798696', NULL, NULL, '');
INSERT INTO public.sys_menu VALUES (1013, '菜单查询', 102, 1, '', '', '', '1', '0', 'F', '0', '0', 'system:menu:query', '#', 103, 1, '2026-03-17 13:37:16.804083', NULL, NULL, '');
INSERT INTO public.sys_menu VALUES (1014, '菜单新增', 102, 2, '', '', '', '1', '0', 'F', '0', '0', 'system:menu:add', '#', 103, 1, '2026-03-17 13:37:16.809397', NULL, NULL, '');
INSERT INTO public.sys_menu VALUES (1015, '菜单修改', 102, 3, '', '', '', '1', '0', 'F', '0', '0', 'system:menu:edit', '#', 103, 1, '2026-03-17 13:37:16.815098', NULL, NULL, '');
INSERT INTO public.sys_menu VALUES (1016, '菜单删除', 102, 4, '', '', '', '1', '0', 'F', '0', '0', 'system:menu:remove', '#', 103, 1, '2026-03-17 13:37:16.820553', NULL, NULL, '');
INSERT INTO public.sys_menu VALUES (1017, '部门查询', 103, 1, '', '', '', '1', '0', 'F', '0', '0', 'system:dept:query', '#', 103, 1, '2026-03-17 13:37:16.825741', NULL, NULL, '');
INSERT INTO public.sys_menu VALUES (1018, '部门新增', 103, 2, '', '', '', '1', '0', 'F', '0', '0', 'system:dept:add', '#', 103, 1, '2026-03-17 13:37:16.830556', NULL, NULL, '');
INSERT INTO public.sys_menu VALUES (108, '日志管理', 1, 9, 'log', '', '', '1', '0', 'M', '1', '1', '', 'log', 103, 1, '2026-03-17 13:37:16.596921', 1, '2026-03-28 15:39:28.436', '日志管理菜单');
INSERT INTO public.sys_menu VALUES (2, '系统监控', 0, 3, 'monitor', '', '', '1', '0', 'M', '1', '1', '', 'monitor', 103, 1, '2026-03-17 13:37:16.510851', 1, '2026-03-28 15:44:03.578', '系统监控目录');
INSERT INTO public.sys_menu VALUES (123, '客户端管理', 1, 11, 'client', 'system/client/index', '', '1', '0', 'C', '1', '1', 'system:client:list', 'international', 103, 1, '2026-03-17 13:37:16.655946', 1, '2026-03-28 15:44:17.117', '客户端管理菜单');
INSERT INTO public.sys_menu VALUES (1, '系统管理', 0, 1, 'system', '', '', '1', '0', 'M', '0', '0', '', 'iconfont:xitongguanli', 103, 1, '2026-03-17 13:37:16.491746', 1, '2026-03-29 17:32:42.308', '系统管理目录');
INSERT INTO public.sys_menu VALUES (100, '用户管理', 1, 1, 'user', 'system/user/index', '', '1', '0', 'C', '0', '0', 'system:user:list', 'iconfont:yonghuguanli', 103, 1, '2026-03-17 13:37:16.544062', 1, '2026-03-29 17:33:27.958', '用户管理菜单');
INSERT INTO public.sys_menu VALUES (101, '角色管理', 1, 2, 'role', 'system/role/index', '', '1', '0', 'C', '0', '0', 'system:role:list', 'iconfont:jiaoseguanli', 103, 1, '2026-03-17 13:37:16.550467', 1, '2026-03-29 17:33:46.302', '角色管理菜单');
INSERT INTO public.sys_menu VALUES (103, '部门管理', 1, 4, 'dept', 'system/dept/index', '', '1', '0', 'C', '0', '0', 'system:dept:list', 'iconfont:bumenguanli', 103, 1, '2026-03-17 13:37:16.56318', 1, '2026-03-29 17:37:39.629', '部门管理菜单');
INSERT INTO public.sys_menu VALUES (102, '菜单管理', 1, 3, 'menu', 'system/menu/index', '', '1', '0', 'C', '0', '0', 'system:menu:list', 'iconfont:caidanguanli', 103, 1, '2026-03-17 13:37:16.556899', 1, '2026-03-29 17:37:15.38', '菜单管理菜单');
INSERT INTO public.sys_menu VALUES (104, '岗位管理', 1, 5, 'post', 'system/post/index', '', '1', '0', 'C', '0', '0', 'system:post:list', 'iconfont:mti-gangweiguanli', 103, 1, '2026-03-17 13:37:16.569731', 1, '2026-03-29 17:38:17.413', '岗位管理菜单');
INSERT INTO public.sys_menu VALUES (105, '字典管理', 1, 6, 'dict', 'system/dict/index', '', '1', '0', 'C', '0', '0', 'system:dict:list', 'iconfont:zidianguanli', 103, 1, '2026-03-17 13:37:16.57612', 1, '2026-03-29 17:38:45.518', '字典管理菜单');
INSERT INTO public.sys_menu VALUES (106, '参数设置', 1, 7, 'config', 'system/config/index', '', '1', '0', 'C', '0', '0', 'system:config:list', 'iconfont:canshuguanli', 103, 1, '2026-03-17 13:37:16.584113', 1, '2026-03-29 17:39:27.992', '参数设置菜单');
INSERT INTO public.sys_menu VALUES (107, '通知公告', 1, 8, 'notice', 'system/notice/index', '', '1', '0', 'C', '0', '0', 'system:notice:list', 'iconfont:a-tongzhigonggao', 103, 1, '2026-03-17 13:37:16.590949', 1, '2026-03-29 17:39:45.04', '通知公告菜单');
INSERT INTO public.sys_menu VALUES (118, '文件管理', 1, 10, 'oss', 'system/oss/index', '', '1', '0', 'C', '0', '0', 'system:oss:list', 'iconfont:wenjianguanli', 103, 1, '2026-03-17 13:37:16.718086', 1, '2026-03-29 17:40:16.773', '文件管理菜单');
INSERT INTO public.sys_menu VALUES (121, '租户管理', 6, 1, 'tenant', 'system/tenant/index', '', '1', '0', 'C', '0', '0', 'system:tenant:list', 'iconfont:zuhuguanli', 103, 1, '2026-03-17 13:37:16.641363', 1, '2026-03-29 17:42:03.595', '租户管理菜单');
INSERT INTO public.sys_menu VALUES (122, '租户套餐管理', 6, 2, 'tenantPackage', 'system/tenantPackage/index', '', '1', '0', 'C', '0', '0', 'system:tenantPackage:list', 'iconfont:zuhutaocan', 103, 1, '2026-03-17 13:37:16.649828', 1, '2026-03-29 17:42:21.514', '租户套餐管理菜单');
INSERT INTO public.sys_menu VALUES (6, '租户管理', 0, 2, 'tenant', '', '', '1', '0', 'M', '0', '0', '', 'iconfont:xitongzuhuguanli-01', 103, 1, '2026-03-17 13:37:16.499941', 1, '2026-03-29 17:50:32.338', '租户管理目录');
INSERT INTO public.sys_menu VALUES (1019, '部门修改', 103, 3, '', '', '', '1', '0', 'F', '0', '0', 'system:dept:edit', '#', 103, 1, '2026-03-17 13:37:16.835375', NULL, NULL, '');
INSERT INTO public.sys_menu VALUES (1020, '部门删除', 103, 4, '', '', '', '1', '0', 'F', '0', '0', 'system:dept:remove', '#', 103, 1, '2026-03-17 13:37:16.839802', NULL, NULL, '');
INSERT INTO public.sys_menu VALUES (1021, '岗位查询', 104, 1, '', '', '', '1', '0', 'F', '0', '0', 'system:post:query', '#', 103, 1, '2026-03-17 13:37:16.844643', NULL, NULL, '');
INSERT INTO public.sys_menu VALUES (1022, '岗位新增', 104, 2, '', '', '', '1', '0', 'F', '0', '0', 'system:post:add', '#', 103, 1, '2026-03-17 13:37:16.849994', NULL, NULL, '');
INSERT INTO public.sys_menu VALUES (1023, '岗位修改', 104, 3, '', '', '', '1', '0', 'F', '0', '0', 'system:post:edit', '#', 103, 1, '2026-03-17 13:37:16.855605', NULL, NULL, '');
INSERT INTO public.sys_menu VALUES (1024, '岗位删除', 104, 4, '', '', '', '1', '0', 'F', '0', '0', 'system:post:remove', '#', 103, 1, '2026-03-17 13:37:16.860495', NULL, NULL, '');
INSERT INTO public.sys_menu VALUES (1025, '岗位导出', 104, 5, '', '', '', '1', '0', 'F', '0', '0', 'system:post:export', '#', 103, 1, '2026-03-17 13:37:16.865714', NULL, NULL, '');
INSERT INTO public.sys_menu VALUES (1026, '字典查询', 105, 1, '#', '', '', '1', '0', 'F', '0', '0', 'system:dict:query', '#', 103, 1, '2026-03-17 13:37:16.871002', NULL, NULL, '');
INSERT INTO public.sys_menu VALUES (1027, '字典新增', 105, 2, '#', '', '', '1', '0', 'F', '0', '0', 'system:dict:add', '#', 103, 1, '2026-03-17 13:37:16.875811', NULL, NULL, '');
INSERT INTO public.sys_menu VALUES (1028, '字典修改', 105, 3, '#', '', '', '1', '0', 'F', '0', '0', 'system:dict:edit', '#', 103, 1, '2026-03-17 13:37:16.880085', NULL, NULL, '');
INSERT INTO public.sys_menu VALUES (1029, '字典删除', 105, 4, '#', '', '', '1', '0', 'F', '0', '0', 'system:dict:remove', '#', 103, 1, '2026-03-17 13:37:16.884834', NULL, NULL, '');
INSERT INTO public.sys_menu VALUES (1030, '字典导出', 105, 5, '#', '', '', '1', '0', 'F', '0', '0', 'system:dict:export', '#', 103, 1, '2026-03-17 13:37:16.890364', NULL, NULL, '');
INSERT INTO public.sys_menu VALUES (1031, '参数查询', 106, 1, '#', '', '', '1', '0', 'F', '0', '0', 'system:config:query', '#', 103, 1, '2026-03-17 13:37:16.895428', NULL, NULL, '');
INSERT INTO public.sys_menu VALUES (1032, '参数新增', 106, 2, '#', '', '', '1', '0', 'F', '0', '0', 'system:config:add', '#', 103, 1, '2026-03-17 13:37:16.9006', NULL, NULL, '');
INSERT INTO public.sys_menu VALUES (1033, '参数修改', 106, 3, '#', '', '', '1', '0', 'F', '0', '0', 'system:config:edit', '#', 103, 1, '2026-03-17 13:37:16.906029', NULL, NULL, '');
INSERT INTO public.sys_menu VALUES (1034, '参数删除', 106, 4, '#', '', '', '1', '0', 'F', '0', '0', 'system:config:remove', '#', 103, 1, '2026-03-17 13:37:16.912009', NULL, NULL, '');
INSERT INTO public.sys_menu VALUES (1035, '参数导出', 106, 5, '#', '', '', '1', '0', 'F', '0', '0', 'system:config:export', '#', 103, 1, '2026-03-17 13:37:16.917333', NULL, NULL, '');
INSERT INTO public.sys_menu VALUES (1036, '公告查询', 107, 1, '#', '', '', '1', '0', 'F', '0', '0', 'system:notice:query', '#', 103, 1, '2026-03-17 13:37:16.922657', NULL, NULL, '');
INSERT INTO public.sys_menu VALUES (1037, '公告新增', 107, 2, '#', '', '', '1', '0', 'F', '0', '0', 'system:notice:add', '#', 103, 1, '2026-03-17 13:37:16.927834', NULL, NULL, '');
INSERT INTO public.sys_menu VALUES (1038, '公告修改', 107, 3, '#', '', '', '1', '0', 'F', '0', '0', 'system:notice:edit', '#', 103, 1, '2026-03-17 13:37:16.933649', NULL, NULL, '');
INSERT INTO public.sys_menu VALUES (1039, '公告删除', 107, 4, '#', '', '', '1', '0', 'F', '0', '0', 'system:notice:remove', '#', 103, 1, '2026-03-17 13:37:16.938607', NULL, NULL, '');
INSERT INTO public.sys_menu VALUES (1040, '操作查询', 500, 1, '#', '', '', '1', '0', 'F', '0', '0', 'monitor:operlog:query', '#', 103, 1, '2026-03-17 13:37:16.943915', NULL, NULL, '');
INSERT INTO public.sys_menu VALUES (1041, '操作删除', 500, 2, '#', '', '', '1', '0', 'F', '0', '0', 'monitor:operlog:remove', '#', 103, 1, '2026-03-17 13:37:16.949021', NULL, NULL, '');
INSERT INTO public.sys_menu VALUES (1042, '日志导出', 500, 4, '#', '', '', '1', '0', 'F', '0', '0', 'monitor:operlog:export', '#', 103, 1, '2026-03-17 13:37:16.954004', NULL, NULL, '');
INSERT INTO public.sys_menu VALUES (1043, '登录查询', 501, 1, '#', '', '', '1', '0', 'F', '0', '0', 'monitor:logininfor:query', '#', 103, 1, '2026-03-17 13:37:16.9591', NULL, NULL, '');
INSERT INTO public.sys_menu VALUES (1044, '登录删除', 501, 2, '#', '', '', '1', '0', 'F', '0', '0', 'monitor:logininfor:remove', '#', 103, 1, '2026-03-17 13:37:16.964571', NULL, NULL, '');
INSERT INTO public.sys_menu VALUES (1045, '日志导出', 501, 3, '#', '', '', '1', '0', 'F', '0', '0', 'monitor:logininfor:export', '#', 103, 1, '2026-03-17 13:37:16.969775', NULL, NULL, '');
INSERT INTO public.sys_menu VALUES (1050, '账户解锁', 501, 4, '#', '', '', '1', '0', 'F', '0', '0', 'monitor:logininfor:unlock', '#', 103, 1, '2026-03-17 13:37:16.975319', NULL, NULL, '');
INSERT INTO public.sys_menu VALUES (1046, '在线查询', 109, 1, '#', '', '', '1', '0', 'F', '0', '0', 'monitor:online:query', '#', 103, 1, '2026-03-17 13:37:16.980492', NULL, NULL, '');
INSERT INTO public.sys_menu VALUES (1047, '批量强退', 109, 2, '#', '', '', '1', '0', 'F', '0', '0', 'monitor:online:batchLogout', '#', 103, 1, '2026-03-17 13:37:16.986328', NULL, NULL, '');
INSERT INTO public.sys_menu VALUES (1048, '单条强退', 109, 3, '#', '', '', '1', '0', 'F', '0', '0', 'monitor:online:forceLogout', '#', 103, 1, '2026-03-17 13:37:16.991494', NULL, NULL, '');
INSERT INTO public.sys_menu VALUES (1600, '文件查询', 118, 1, '#', '', '', '1', '0', 'F', '0', '0', 'system:oss:query', '#', 103, 1, '2026-03-17 13:37:17.026598', NULL, NULL, '');
INSERT INTO public.sys_menu VALUES (1601, '文件上传', 118, 2, '#', '', '', '1', '0', 'F', '0', '0', 'system:oss:upload', '#', 103, 1, '2026-03-17 13:37:17.031532', NULL, NULL, '');
INSERT INTO public.sys_menu VALUES (1602, '文件下载', 118, 3, '#', '', '', '1', '0', 'F', '0', '0', 'system:oss:download', '#', 103, 1, '2026-03-17 13:37:17.037302', NULL, NULL, '');
INSERT INTO public.sys_menu VALUES (1603, '文件删除', 118, 4, '#', '', '', '1', '0', 'F', '0', '0', 'system:oss:remove', '#', 103, 1, '2026-03-17 13:37:17.042603', NULL, NULL, '');
INSERT INTO public.sys_menu VALUES (1620, '配置列表', 118, 5, '#', '', '', '1', '0', 'F', '0', '0', 'system:ossConfig:list', '#', 103, 1, '2026-03-17 13:37:17.047589', NULL, NULL, '');
INSERT INTO public.sys_menu VALUES (1621, '配置添加', 118, 6, '#', '', '', '1', '0', 'F', '0', '0', 'system:ossConfig:add', '#', 103, 1, '2026-03-17 13:37:17.052135', NULL, NULL, '');
INSERT INTO public.sys_menu VALUES (1622, '配置编辑', 118, 6, '#', '', '', '1', '0', 'F', '0', '0', 'system:ossConfig:edit', '#', 103, 1, '2026-03-17 13:37:17.05712', NULL, NULL, '');
INSERT INTO public.sys_menu VALUES (1623, '配置删除', 118, 6, '#', '', '', '1', '0', 'F', '0', '0', 'system:ossConfig:remove', '#', 103, 1, '2026-03-17 13:37:17.062267', NULL, NULL, '');
INSERT INTO public.sys_menu VALUES (1606, '租户查询', 121, 1, '#', '', '', '1', '0', 'F', '0', '0', 'system:tenant:query', '#', 103, 1, '2026-03-17 13:37:17.067054', NULL, NULL, '');
INSERT INTO public.sys_menu VALUES (1607, '租户新增', 121, 2, '#', '', '', '1', '0', 'F', '0', '0', 'system:tenant:add', '#', 103, 1, '2026-03-17 13:37:17.071813', NULL, NULL, '');
INSERT INTO public.sys_menu VALUES (1608, '租户修改', 121, 3, '#', '', '', '1', '0', 'F', '0', '0', 'system:tenant:edit', '#', 103, 1, '2026-03-17 13:37:17.076524', NULL, NULL, '');
INSERT INTO public.sys_menu VALUES (1609, '租户删除', 121, 4, '#', '', '', '1', '0', 'F', '0', '0', 'system:tenant:remove', '#', 103, 1, '2026-03-17 13:37:17.081683', NULL, NULL, '');
INSERT INTO public.sys_menu VALUES (1610, '租户导出', 121, 5, '#', '', '', '1', '0', 'F', '0', '0', 'system:tenant:export', '#', 103, 1, '2026-03-17 13:37:17.086496', NULL, NULL, '');
INSERT INTO public.sys_menu VALUES (1611, '租户套餐查询', 122, 1, '#', '', '', '1', '0', 'F', '0', '0', 'system:tenantPackage:query', '#', 103, 1, '2026-03-17 13:37:17.109697', NULL, NULL, '');
INSERT INTO public.sys_menu VALUES (1612, '租户套餐新增', 122, 2, '#', '', '', '1', '0', 'F', '0', '0', 'system:tenantPackage:add', '#', 103, 1, '2026-03-17 13:37:17.114908', NULL, NULL, '');
INSERT INTO public.sys_menu VALUES (1613, '租户套餐修改', 122, 3, '#', '', '', '1', '0', 'F', '0', '0', 'system:tenantPackage:edit', '#', 103, 1, '2026-03-17 13:37:17.119641', NULL, NULL, '');
INSERT INTO public.sys_menu VALUES (1614, '租户套餐删除', 122, 4, '#', '', '', '1', '0', 'F', '0', '0', 'system:tenantPackage:remove', '#', 103, 1, '2026-03-17 13:37:17.124627', NULL, NULL, '');
INSERT INTO public.sys_menu VALUES (1615, '租户套餐导出', 122, 5, '#', '', '', '1', '0', 'F', '0', '0', 'system:tenantPackage:export', '#', 103, 1, '2026-03-17 13:37:17.129221', NULL, NULL, '');
INSERT INTO public.sys_menu VALUES (1061, '客户端管理查询', 123, 1, '#', '', '', '1', '0', 'F', '0', '0', 'system:client:query', '#', 103, 1, '2026-03-17 13:37:17.134448', NULL, NULL, '');
INSERT INTO public.sys_menu VALUES (1062, '客户端管理新增', 123, 2, '#', '', '', '1', '0', 'F', '0', '0', 'system:client:add', '#', 103, 1, '2026-03-17 13:37:17.139591', NULL, NULL, '');
INSERT INTO public.sys_menu VALUES (1063, '客户端管理修改', 123, 3, '#', '', '', '1', '0', 'F', '0', '0', 'system:client:edit', '#', 103, 1, '2026-03-17 13:37:17.144729', NULL, NULL, '');
INSERT INTO public.sys_menu VALUES (1064, '客户端管理删除', 123, 4, '#', '', '', '1', '0', 'F', '0', '0', 'system:client:remove', '#', 103, 1, '2026-03-17 13:37:17.149831', NULL, NULL, '');
INSERT INTO public.sys_menu VALUES (1065, '客户端管理导出', 123, 5, '#', '', '', '1', '0', 'F', '0', '0', 'system:client:export', '#', 103, 1, '2026-03-17 13:37:17.154821', NULL, NULL, '');


--
-- Data for Name: sys_notice; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.sys_notice VALUES (1, '000000', '温馨提醒：2018-07-01 新版本发布啦', '2', '新版本内容', '0', 103, 1, '2026-03-17 13:37:18.576372', NULL, NULL, '管理员');


--
-- Data for Name: sys_oss; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.sys_oss VALUES (2038258920954376194, '000000', '2026/03/29/b90dad7b65704c5d8b73fbddd168b926.png', 'logo3.png', '.png', 'http://192.168.129.146:9000/lemon-boot/2026/03/29/b90dad7b65704c5d8b73fbddd168b926.png', '{"bizType":null,"contentType":"application/octet-stream","fileSize":892218,"isTemp":null,"md5":null,"refId":null,"refType":null,"remark":null,"source":null,"tags":null,"uploadIp":null}', NULL, 1, '2026-03-29 22:16:10.432', 1, '2026-03-29 22:16:10.432', 'minio');


--
-- Data for Name: sys_oss_config; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.sys_oss_config VALUES (2, '000000', 'qiniu', 'XXXXXXXXXXXXXXX', 'XXXXXXXXXXXXXXX', 'ruoyi', '', 's3-cn-north-1.qiniucs.com', '', 'N', '', '1', '1', '', 103, 1, '2026-03-17 13:37:19.055396', 1, '2026-03-17 13:37:19.055396', NULL);
INSERT INTO public.sys_oss_config VALUES (3, '000000', 'aliyun', 'XXXXXXXXXXXXXXX', 'XXXXXXXXXXXXXXX', 'ruoyi', '', 'oss-cn-beijing.aliyuncs.com', '', 'N', '', '1', '1', '', 103, 1, '2026-03-17 13:37:19.06046', 1, '2026-03-17 13:37:19.06046', NULL);
INSERT INTO public.sys_oss_config VALUES (4, '000000', 'qcloud', 'XXXXXXXXXXXXXXX', 'XXXXXXXXXXXXXXX', 'ruoyi-1240000000', '', 'cos.ap-beijing.myqcloud.com', '', 'N', 'ap-beijing', '1', '1', '', 103, 1, '2026-03-17 13:37:19.065058', 1, '2026-03-17 13:37:19.065058', NULL);
INSERT INTO public.sys_oss_config VALUES (5, '000000', 'image', 'ruoyi', 'ruoyi123', 'ruoyi', 'image', '127.0.0.1:9000', '', 'N', '', '1', '1', '', 103, 1, '2026-03-17 13:37:19.070336', 1, '2026-03-17 13:37:19.070336', NULL);
INSERT INTO public.sys_oss_config VALUES (1, '000000', 'minio', '3sMjQylkcs7pgS1BPuNk', 'OPmgWYfUJOYbzgysKjpFyDtZdkWmgf4QaRRwdyBj', 'lemon-boot', '', '192.168.129.146:9000', '', 'N', '', '0', '0', '', 103, 1, '2026-03-17 13:37:19.033461', 1, '2026-03-29 22:19:19.494', 'minioadmin/minioadmin123');


--
-- Data for Name: sys_post; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.sys_post VALUES (1, '000000', 103, 'ceo', NULL, '董事长', 1, '0', 103, 1, '2026-03-17 13:37:16.17257', NULL, NULL, '');
INSERT INTO public.sys_post VALUES (2, '000000', 100, 'se', NULL, '项目经理', 2, '0', 103, 1, '2026-03-17 13:37:16.178437', NULL, NULL, '');
INSERT INTO public.sys_post VALUES (3, '000000', 100, 'hr', NULL, '人力资源', 3, '0', 103, 1, '2026-03-17 13:37:16.184041', NULL, NULL, '');
INSERT INTO public.sys_post VALUES (4, '000000', 100, 'user', NULL, '普通员工', 4, '0', 103, 1, '2026-03-17 13:37:16.18961', NULL, NULL, '');


--
-- Data for Name: sys_role; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.sys_role VALUES (1, '000000', '超级管理员', 'superadmin', 1, '1', true, true, '0', '0', 103, 1, '2026-03-17 13:37:16.330994', NULL, NULL, '超级管理员');
INSERT INTO public.sys_role VALUES (3, '000000', '本部门及以下', 'test1', 3, '4', true, true, '0', '0', 103, 1, '2026-03-17 13:37:16.338323', NULL, NULL, '');
INSERT INTO public.sys_role VALUES (4, '000000', '仅本人', 'test2', 4, '5', true, true, '0', '0', 103, 1, '2026-03-17 13:37:16.345891', NULL, NULL, '');


--
-- Data for Name: sys_role_dept; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: sys_role_menu; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.sys_role_menu VALUES (3, 1);
INSERT INTO public.sys_role_menu VALUES (3, 100);
INSERT INTO public.sys_role_menu VALUES (3, 101);
INSERT INTO public.sys_role_menu VALUES (3, 102);
INSERT INTO public.sys_role_menu VALUES (3, 103);
INSERT INTO public.sys_role_menu VALUES (3, 104);
INSERT INTO public.sys_role_menu VALUES (3, 105);
INSERT INTO public.sys_role_menu VALUES (3, 106);
INSERT INTO public.sys_role_menu VALUES (3, 107);
INSERT INTO public.sys_role_menu VALUES (3, 108);
INSERT INTO public.sys_role_menu VALUES (3, 118);
INSERT INTO public.sys_role_menu VALUES (3, 123);
INSERT INTO public.sys_role_menu VALUES (3, 130);
INSERT INTO public.sys_role_menu VALUES (3, 131);
INSERT INTO public.sys_role_menu VALUES (3, 132);
INSERT INTO public.sys_role_menu VALUES (3, 133);
INSERT INTO public.sys_role_menu VALUES (3, 500);
INSERT INTO public.sys_role_menu VALUES (3, 501);
INSERT INTO public.sys_role_menu VALUES (3, 1001);
INSERT INTO public.sys_role_menu VALUES (3, 1002);
INSERT INTO public.sys_role_menu VALUES (3, 1003);
INSERT INTO public.sys_role_menu VALUES (3, 1004);
INSERT INTO public.sys_role_menu VALUES (3, 1005);
INSERT INTO public.sys_role_menu VALUES (3, 1006);
INSERT INTO public.sys_role_menu VALUES (3, 1007);
INSERT INTO public.sys_role_menu VALUES (3, 1008);
INSERT INTO public.sys_role_menu VALUES (3, 1009);
INSERT INTO public.sys_role_menu VALUES (3, 1010);
INSERT INTO public.sys_role_menu VALUES (3, 1011);
INSERT INTO public.sys_role_menu VALUES (3, 1012);
INSERT INTO public.sys_role_menu VALUES (3, 1013);
INSERT INTO public.sys_role_menu VALUES (3, 1014);
INSERT INTO public.sys_role_menu VALUES (3, 1015);
INSERT INTO public.sys_role_menu VALUES (3, 1016);
INSERT INTO public.sys_role_menu VALUES (3, 1017);
INSERT INTO public.sys_role_menu VALUES (3, 1018);
INSERT INTO public.sys_role_menu VALUES (3, 1019);
INSERT INTO public.sys_role_menu VALUES (3, 1020);
INSERT INTO public.sys_role_menu VALUES (3, 1021);
INSERT INTO public.sys_role_menu VALUES (3, 1022);
INSERT INTO public.sys_role_menu VALUES (3, 1023);
INSERT INTO public.sys_role_menu VALUES (3, 1024);
INSERT INTO public.sys_role_menu VALUES (3, 1025);
INSERT INTO public.sys_role_menu VALUES (3, 1026);
INSERT INTO public.sys_role_menu VALUES (3, 1027);
INSERT INTO public.sys_role_menu VALUES (3, 1028);
INSERT INTO public.sys_role_menu VALUES (3, 1029);
INSERT INTO public.sys_role_menu VALUES (3, 1030);
INSERT INTO public.sys_role_menu VALUES (3, 1031);
INSERT INTO public.sys_role_menu VALUES (3, 1032);
INSERT INTO public.sys_role_menu VALUES (3, 1033);
INSERT INTO public.sys_role_menu VALUES (3, 1034);
INSERT INTO public.sys_role_menu VALUES (3, 1035);
INSERT INTO public.sys_role_menu VALUES (3, 1036);
INSERT INTO public.sys_role_menu VALUES (3, 1037);
INSERT INTO public.sys_role_menu VALUES (3, 1038);
INSERT INTO public.sys_role_menu VALUES (3, 1039);
INSERT INTO public.sys_role_menu VALUES (3, 1040);
INSERT INTO public.sys_role_menu VALUES (3, 1041);
INSERT INTO public.sys_role_menu VALUES (3, 1042);
INSERT INTO public.sys_role_menu VALUES (3, 1043);
INSERT INTO public.sys_role_menu VALUES (3, 1044);
INSERT INTO public.sys_role_menu VALUES (3, 1045);
INSERT INTO public.sys_role_menu VALUES (3, 1050);
INSERT INTO public.sys_role_menu VALUES (3, 1061);
INSERT INTO public.sys_role_menu VALUES (3, 1062);
INSERT INTO public.sys_role_menu VALUES (3, 1063);
INSERT INTO public.sys_role_menu VALUES (3, 1064);
INSERT INTO public.sys_role_menu VALUES (3, 1065);
INSERT INTO public.sys_role_menu VALUES (3, 1600);
INSERT INTO public.sys_role_menu VALUES (3, 1601);
INSERT INTO public.sys_role_menu VALUES (3, 1602);
INSERT INTO public.sys_role_menu VALUES (3, 1603);
INSERT INTO public.sys_role_menu VALUES (3, 1620);
INSERT INTO public.sys_role_menu VALUES (3, 1621);
INSERT INTO public.sys_role_menu VALUES (3, 1622);
INSERT INTO public.sys_role_menu VALUES (3, 1623);
INSERT INTO public.sys_role_menu VALUES (3, 11616);
INSERT INTO public.sys_role_menu VALUES (3, 11618);
INSERT INTO public.sys_role_menu VALUES (3, 11619);
INSERT INTO public.sys_role_menu VALUES (3, 11622);
INSERT INTO public.sys_role_menu VALUES (3, 11623);
INSERT INTO public.sys_role_menu VALUES (3, 11629);
INSERT INTO public.sys_role_menu VALUES (3, 11632);
INSERT INTO public.sys_role_menu VALUES (3, 11633);
INSERT INTO public.sys_role_menu VALUES (3, 11638);
INSERT INTO public.sys_role_menu VALUES (3, 11639);
INSERT INTO public.sys_role_menu VALUES (3, 11640);
INSERT INTO public.sys_role_menu VALUES (3, 11641);
INSERT INTO public.sys_role_menu VALUES (3, 11642);
INSERT INTO public.sys_role_menu VALUES (3, 11643);
INSERT INTO public.sys_role_menu VALUES (3, 11701);


--
-- Data for Name: sys_social; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: sys_tenant; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.sys_tenant VALUES (1, '000000', '管理组', '15888888888', 'XXX有限公司', NULL, NULL, '多租户通用后台管理管理系统', NULL, NULL, NULL, NULL, -1, '0', '0', 103, 1, '2026-03-17 13:37:15.693633', NULL, NULL);


--
-- Data for Name: sys_tenant_package; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: sys_user; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.sys_user VALUES (4, '000000', 102, 'test1', '仅本人 密码666666', 'sys_user', '', '', '0', NULL, '{bcrypt}$2a$10$p8wTmgZbb544RwcfZxD8HuhEYN/82dfTq2mpmn5pCz3x.IiMc.4Qi', '0', '0', '127.0.0.1', '2026-03-17 13:37:16.088076', 103, 1, '2026-03-17 13:37:16.088076', 4, '2026-03-17 13:37:16.088076', NULL);
INSERT INTO public.sys_user VALUES (3, '000000', 108, 'test', '本部门及以下 密码666666', 'sys_user', '', '', '0', NULL, '{bcrypt}$2a$10$p8wTmgZbb544RwcfZxD8HuhEYN/82dfTq2mpmn5pCz3x.IiMc.4Qi', '0', '0', '127.0.0.1', '2026-03-17 13:37:16.082508', 103, 1, '2026-03-17 13:37:16.082508', 3, '2026-03-17 13:37:16.082508', NULL);
INSERT INTO public.sys_user VALUES (1, '000000', 103, 'admin', 'Lemon', 'sys_user', 'lemon@163.com', '15888888888', '0', 2038258920954376194, '{bcrypt}$2a$10$ZJI0xpE43BOllx.z9xIXDuhJzdOBn6rWa3G0C/5ViZeR8SlB9Zv6m', '0', '0', '127.0.0.1', '2026-03-17 13:37:16.076486', 103, 1, '2026-03-17 13:37:16.076486', NULL, NULL, '管理员');


--
-- Data for Name: sys_user_post; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.sys_user_post VALUES (1, 1);


--
-- Data for Name: sys_user_role; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.sys_user_role VALUES (1, 1);
INSERT INTO public.sys_user_role VALUES (3, 3);
INSERT INTO public.sys_user_role VALUES (4, 4);


--
-- Name: sys_social pk_sys_social; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.sys_social
    ADD CONSTRAINT pk_sys_social PRIMARY KEY (id);


--
-- Name: sys_tenant pk_sys_tenant; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.sys_tenant
    ADD CONSTRAINT pk_sys_tenant PRIMARY KEY (id);


--
-- Name: sys_tenant_package pk_sys_tenant_package; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.sys_tenant_package
    ADD CONSTRAINT pk_sys_tenant_package PRIMARY KEY (package_id);


--
-- Name: sys_config sys_config_pk; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.sys_config
    ADD CONSTRAINT sys_config_pk PRIMARY KEY (config_id);


--
-- Name: sys_dept sys_dept_pk; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.sys_dept
    ADD CONSTRAINT sys_dept_pk PRIMARY KEY (dept_id);


--
-- Name: sys_dict_data sys_dict_data_pk; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.sys_dict_data
    ADD CONSTRAINT sys_dict_data_pk PRIMARY KEY (dict_code);


--
-- Name: sys_dict_type sys_dict_type_pk; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.sys_dict_type
    ADD CONSTRAINT sys_dict_type_pk PRIMARY KEY (dict_id);


--
-- Name: sys_menu sys_menu_pk; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.sys_menu
    ADD CONSTRAINT sys_menu_pk PRIMARY KEY (menu_id);


--
-- Name: sys_notice sys_notice_pk; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.sys_notice
    ADD CONSTRAINT sys_notice_pk PRIMARY KEY (notice_id);


--
-- Name: sys_oss_config sys_oss_config_pk; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.sys_oss_config
    ADD CONSTRAINT sys_oss_config_pk PRIMARY KEY (oss_config_id);


--
-- Name: sys_oss sys_oss_pk; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.sys_oss
    ADD CONSTRAINT sys_oss_pk PRIMARY KEY (oss_id);


--
-- Name: sys_post sys_post_pk; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.sys_post
    ADD CONSTRAINT sys_post_pk PRIMARY KEY (post_id);


--
-- Name: sys_role_dept sys_role_dept_pk; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.sys_role_dept
    ADD CONSTRAINT sys_role_dept_pk PRIMARY KEY (role_id, dept_id);


--
-- Name: sys_role_menu sys_role_menu_pk; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.sys_role_menu
    ADD CONSTRAINT sys_role_menu_pk PRIMARY KEY (role_id, menu_id);


--
-- Name: sys_role sys_role_pk; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.sys_role
    ADD CONSTRAINT sys_role_pk PRIMARY KEY (role_id);


--
-- Name: sys_user sys_user_pk; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.sys_user
    ADD CONSTRAINT sys_user_pk PRIMARY KEY (user_id);


--
-- Name: sys_user_post sys_user_post_pk; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.sys_user_post
    ADD CONSTRAINT sys_user_post_pk PRIMARY KEY (user_id, post_id);


--
-- Name: sys_user_role sys_user_role_pk; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.sys_user_role
    ADD CONSTRAINT sys_user_role_pk PRIMARY KEY (user_id, role_id);


--
-- Name: sys_dict_type_index1; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX sys_dict_type_index1 ON public.sys_dict_type USING btree (tenant_id, dict_type);


--
-- PostgreSQL database dump complete
--

