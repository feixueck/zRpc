package com.zuosh.rpc.common;

import jdk.nashorn.internal.objects.annotations.SpecializedFunction;
import org.apache.curator.utils.ZKPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zuoshuai on 2018/3/8.
 */
public class ZConstants {
    public static final String CONNECTION_INFO = "192.168.200.129:2181";
    //    public static final String CONNECTION_INFO = "59.110.240.159:2181";
    public static final String ZRPC_PARENT_PATH = "/zRpc";
    public static final String ZRPC_TEST_PATH = "/zt";
    public static final String VERSION = "v1.0";
    public static final String RPC_ROOT_PATH = ZKPaths.makePath(ZConstants.ZRPC_PARENT_PATH, "my");
    public static final String PROVIDER_ROLE = "provider";
    public static final int SESSION_TIMEOUT = 5000;
    //transport

    public static final String PROVIDER_HOST = "localhost";
    public static final int PROVIDER_PORT = 2222;
    //logger
    private static final String logger_name = "zRpc";
    public static final Logger LOGGER = LoggerFactory.getLogger(logger_name);
}
