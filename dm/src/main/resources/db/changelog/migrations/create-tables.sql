create table client
(
    id              bigint not null primary key,
    created_at      timestamp with time zone default now() not null,
    modified_at     timestamp with time zone default now() not null,
    created_by      varchar NOT NULL DEFAULT 'none',
    modified_by     varchar NOT NULL DEFAULT 'none',
    first_name      varchar NOT NULL,
    last_name       varchar NOT NULL,
    patronymic      varchar,
    birth_date      bigint not null,
    address         varchar,
    passport_number varchar(6) not null,
    passport_series varchar(4) not null ,
    inn             varchar(12) not null,
    unique (passport_series, passport_number, inn)
);

create table account
(
    number      varchar(20) not null unique primary key,
    created_at  timestamp with time zone default now() not null,
    modified_at timestamp with time zone default now() not null,
    created_by  varchar NOT NULL DEFAULT 'none',
    modified_by varchar NOT NULL DEFAULT 'none',
    client_id   bigint not null references client,
    type        varchar(10) not null constraint account_type_check check ((type)::text = ANY
                   ((ARRAY ['PAYMENT'::character varying, 'BUDGET'::character varying, 'TRANSIT'::character varying, 'OVERDRAFT'::character varying])::text[])),
    currency    varchar(3) not null constraint account_currency_check check ((currency)::text = 'RUR'::text),
    status      varchar(10) not null constraint account_status_check check ((status)::text = ANY
                   ((ARRAY ['INACTIVE'::character varying, 'ACTIVE'::character varying, 'LOCKED'::character varying, 'CLOSED'::character varying])::text[])),
    open_date   bigint not null,
    close_date  bigint,
    deferment   integer
);

create table account_balance
(
    account_number varchar(20) not null primary key references account,
    created_at     timestamp with time zone default now() not null,
    modified_at    timestamp with time zone default now() not null,
    created_by     varchar NOT NULL DEFAULT 'none',
    modified_by    varchar NOT NULL DEFAULT 'none',
    balance_date   bigint not null,
    amount         numeric not null
);

create table contact
(
    id          bigint not null primary key,
    created_at     timestamp with time zone default now() not null,
    modified_at    timestamp with time zone default now() not null,
    created_by     varchar NOT NULL DEFAULT 'none',
    modified_by    varchar NOT NULL DEFAULT 'none',
    client_id   bigint not null references client,
    type        varchar(5) not null constraint contact_type_check check ((type)::text = ANY ((ARRAY ['PHONE'::character varying, 'EMAIL'::character varying])::text[])),
    value       varchar not null unique
);

create table operation
(
    id             bigint not null primary key,
    created_at     timestamp with time zone default now() not null,
    modified_at    timestamp with time zone default now() not null,
    created_by     varchar NOT NULL DEFAULT 'none',
    modified_by    varchar NOT NULL DEFAULT 'none',
    type           varchar(7) constraint operation_type_check check ((type)::text = ANY ((ARRAY ['RECEIPT'::character varying, 'EXPENSE'::character varying])::text[])),
    account_number varchar(20) not null references account,
    oper_date      bigint not null,
    amount         numeric not null,
    description    varchar
);

create index operation_account_number_ind on operation (account_number);

create sequence CLIENT_ID_SEQ;
create sequence CONTACT_ID_SEQ;
create sequence OPERATION_ID_SEQ;