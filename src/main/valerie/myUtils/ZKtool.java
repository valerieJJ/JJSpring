package valerie.myUtils;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ZKtool {

    @Autowired
    private ZooKeeper zkClient;

    public Stat existes(String path, boolean requireWatch) throws KeeperException, InterruptedException {
        // 判断指定节点是否存在
        System.out.println("\n\nNode(" + path + ") exist\n");
        return zkClient.exists(path,requireWatch);
    }

    public Stat existes(String path, Watcher watcher) throws KeeperException, InterruptedException {
        return zkClient.exists(path,watcher);
    }

    public  String getNodeData(String path,Watcher watcher){
        try {
            Stat stat=new Stat();
            byte[] bytes=zkClient.getData(path,watcher,stat);
            return  new String(bytes);
        }catch (Exception e){
            e.printStackTrace();
            return  null;
        }
    }

    public boolean setNodeData(String path,String value){
        try {
             zkClient.setData(path, value.getBytes(),-1);//.forPath("/super"+path,valu.getBytes());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean createPermNode(String path, String data){
        // 创建持久化节点
        try {
            zkClient.create(path,data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            return true;
        } catch (KeeperException e) {
            e.printStackTrace();
            return false;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deletePermNode(String path){
        // 删除持久化节点
        try {
            zkClient.delete(path,-1);//version为-1则忽略版本检查.否则若版本不同会导致更新操作失败
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) throws KeeperException, InterruptedException {
        ZKtool zKtool = new ZKtool();
        ZookeeperConfig zkconfig = new ZookeeperConfig();
        zkconfig.setZookeeperConfig("localhost", 4000);
        zKtool.zkClient = zkconfig.zkClient();

        String znodePath = "/jj/zookeepers/zk1";
        byte[] data = "My first zookeeper app".getBytes(); // Declare data

        zKtool.existes(znodePath, true);


    }

}
