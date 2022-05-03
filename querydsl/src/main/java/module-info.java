module it.sisal.digital.phoenix.utils.querydsl{
    exports it.sisal.digital.phoenix.utils.querydsl;

    opens it.sisal.digital.phoenix.utils.querydsl to spring.core;

    requires org.slf4j;
    requires querydsl.core;
    requires querydsl.sql;
    requires spring.context;
    requires spring.tx;
}