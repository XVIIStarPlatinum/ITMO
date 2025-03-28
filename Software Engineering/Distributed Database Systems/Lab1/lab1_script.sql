set custom.table_name = :'custom_table_name';
set custom.table_schema = ':custom_table_schema';
set custom.table_databs = ':custom_table_databs';

do
$$
    declare
        schema_name    text;
        tab_name       text;
        databs_name    text;
        column_record  record;
        table_id       oid;
        column_number  int;
        my_column_name text;
        column_type    text;
        column_constr  text;
        result         text;
    begin
        tab_name := current_setting('custom.table_name', true);
        
        begin
            if position('.' in tab_name) > 0 then

                if array_length(string_to_array(tab_name, '.'), 1) = 2 then
		    databs_name := NULL;
                    schema_name := split_part(tab_name, '.', 1);
                    tab_name := split_part(tab_name, '.', 2);
                elsif array_length(string_to_array(tab_name, '.'), 1) = 3 then
                    databs_name := split_part(tab_name, '.', 1);
                    schema_name := split_part(tab_name, '.', 2);
                    tab_name := split_part(tab_name, '.', 3);
                end if;
            end if;
        exception
            when others then
                databs_name := NULL;
                schema_name := NULL;
                tab_name := NULL;
        end;

        if tab_name is null or tab_name = '' then
            raise exception 'Имя таблицы не задана.';
        end if;
        if schema_name is null or schema_name = '' then
            execute 'select current_schema()' into schema_name;
        end if;
        if databs_name is null or databs_name = '' then
            execute 'select current_database()' into databs_name;
        end if;

        select c.oid into table_id from pg_catalog.pg_class c
        join pg_catalog.pg_namespace n on c.relnamespace = n.oid
        where c.relname = tab_name and n.nspname = schema_name;

        if table_id is null then
            raise exception 'Таблицы %.%.% нет.', databs_name, schema_name, tab_name;
        end if;

        raise notice 'Таблица: %.%.%', databs_name, schema_name, tab_name;
        raise notice 'No. Имя таблицы    Атрибуты';
        raise notice '--- -------------  ----------------------------------------------';

        for column_record in 
            select attnum, attname, atttypid
            from pg_catalog.pg_attribute 
            where attrelid = table_id and attnum > 0
            order by attnum
        loop
                column_number := column_record.attnum;
                my_column_name := column_record.attname;
                select typname into column_type 
                from pg_catalog.pg_type 
                where oid = column_record.atttypid;

                select string_agg(
                    format('"%s" References %s.%s.%s(%s)', conname, databs_name, n.nspname, confrelid::regclass, confkey[1]),
                    E'\n'
                )
                into column_constr
                from pg_constraint con
                join pg_catalog.pg_namespace n on con.connamespace = n.oid
                where con.conrelid = table_id and con.contype = 'f'
                and column_record.attnum = any(con.conkey);

                select format('%-3s %-14s %-8s %-2s %s', column_number, my_column_name, 'Type', ':', column_type) 
                into result;
                raise notice '%', result;

                if column_constr is not null then
                    select format('%-18s %-8s %-2s %s', ' ', 'Constr', ':', column_constr) into result;
                    raise notice '%', result;
                end if;
        end loop;
    end;
$$ LANGUAGE plpgsql;