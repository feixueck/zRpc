package com.zuosh.rpc.samples;

import com.zuosh.rpc.common.ZConstants;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;
import org.apache.curator.utils.CloseableUtils;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.KeeperException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;

/**
 * Created by yanan.sun on 16-10-19.
 */
public class TreeCacheExample {
    private static final String PATH = "/zRpc/my/TmpDemo";
    private static final TestingServer server = buildTestServer();
    private static CuratorFramework client = CuratorFrameworkFactory.newClient(ZConstants.CONNECTION_INFO
            , new ExponentialBackoffRetry(1000, 3));
    private static TreeCache treeCache = new TreeCache(client, PATH);

    private static TestingServer buildTestServer() {
        try {
            return new TestingServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        start();
        try {
            processCommands(client, treeCache);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void start() {
        try {
            client.start();
            treeCache.start();
        } catch (Exception e) {
            System.out.println("error occurs");
        }
    }

    private static void addListener(TreeCache cache) {
        // a PathChildrenCacheListener is optional. Here, it's used just to log changes
        TreeCacheListener treeCacheListener = new TreeCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, TreeCacheEvent event) throws Exception {
                switch (event.getType()) {
                    case NODE_ADDED:
                        System.out.println("Node add " + ZKPaths.getNodeFromPath(event.getData().getPath()));
                        break;
                    case NODE_REMOVED:
                        System.out.println("Node removed " + ZKPaths.getNodeFromPath(event.getData().getPath()));
                        break;
                    case NODE_UPDATED:
                        System.out.println("Node updated " + ZKPaths.getNodeFromPath(event.getData().getPath()));
                        break;

                }
            }
        };
        cache.getListenable().addListener(treeCacheListener);
    }

    public void destroy() {
        CloseableUtils.closeQuietly(treeCache);
        CloseableUtils.closeQuietly(client);
        CloseableUtils.closeQuietly(server);
    }

    private static void processCommands(CuratorFramework client, TreeCache cache) throws Exception {
        // More scaffolding that does a simple command line processor

        printHelp();

        try {
            addListener(cache);

            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            boolean done = false;
            while (!done) {
                System.out.print("> ");

                String line = in.readLine();
                if (line == null) {
                    break;
                }

                String command = line.trim();
                String[] parts = command.split("\\s");
                if (parts.length == 0) {
                    continue;
                }
                String operation = parts[0];
                String args[] = Arrays.copyOfRange(parts, 1, parts.length);

                if (operation.equalsIgnoreCase("help") || operation.equalsIgnoreCase("?")) {
                    printHelp();
                } else if (operation.equalsIgnoreCase("q") || operation.equalsIgnoreCase("quit")) {
                    done = true;
                } else if (operation.equals("set")) {
                    setValue(client, command, args);
                } else if (operation.equals("remove")) {
                    remove(client, command, args);
                } else if (operation.equals("list")) {
                    //list(cache);
                }

                Thread.sleep(1000); // just to allow the console output to catch up
            }
        } finally {

        }
    }

 /*   private static void list(TreeCache cache, String path) {
        if (cache.getCurrentData(path) == null) {
            System.out.println("* empty *");
        } else {
            for (ChildData data : cache.getCurrentData(path)) {
                System.out.println(data.getPath() + " = " + new String(data.getData()));
            }
        }
    }*/

    private static void remove(CuratorFramework client, String command, String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("syntax error (expected remove <path>): " + command);
            return;
        }

        String name = args[0];
        /*if (name.contains("/")) {
            System.err.println("Invalid node name" + name);
            return;
        }
        String path = ZKPaths.makePath(PATH, name);*/
        String path = PATH + "/" + name;

        try {
            client.delete().forPath(path);
        } catch (KeeperException.NoNodeException e) {
            // ignore
        }
    }

    private static void setValue(CuratorFramework client, String command, String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("syntax error (expected set <path> <value>): " + command);
            return;
        }

        String name = args[0];
        /*if (name.contains("/")) {
            System.err.println("Invalid node name" + name);
            return;
        }
        String path = ZKPaths.makePath(PATH, name);*/
        String path = PATH + "/" + name;

        byte[] bytes = args[1].getBytes();
        try {
            if (client.checkExists().forPath(path) != null) {
                System.out.println("path " + path + "exist");
            } else {
                client.create().creatingParentsIfNeeded().forPath(path, bytes);
            }
//            client.setData().forPath(path, bytes);
        } catch (KeeperException.NoNodeException e) {
            // client.create().creatingParentsIfNeeded().forPath(path, bytes);
        }
    }

    private static void printHelp() {
        System.out.println("An example of using PathChildrenCache. This example is driven by entering commands at the prompt:\n");
        System.out.println("set <name> <value>: Adds or updates a node with the given name");
        System.out.println("remove <name>: Deletes the node with the given name");
        System.out.println("list: List the nodes/values in the cache");
        System.out.println("quit: Quit the example");
        System.out.println();
    }
}