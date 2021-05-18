package com.sample.jenkinsHelper.util;

public interface Params {

    // Default parameters
    public static final String ENVIRONMENT = "environment";
    public static final String CONN_STRING = "connection_string";
    public static final String OPERATION = "oper";
    public static final String ENABLE_CONSOLE_LOGS = "enable_console_logs";

    // Params for Operations with Storage
    public static final String CONTAINER_NAME = "container";
    public static final String FILE_NAME = "file";

    // Params for Operations with DNS
    public static final String TENANT_ID = "tenant_id";
    public static final String SECRET_KEY = "secret_key";
    public static final String CLIENT_ID = "client_id";
    public static final String SUBSCRIPTION_ID = "subscription_id";

    public static final String RG_NAME = "resource_group_name";
    public static final String DNS_ZONE_NAME = "dns_zone_name";
    public static final String ANAME = "a_name";
    public static final String IPADDRESS = "ip_address";
    public static final String TTL = "ttl";

}