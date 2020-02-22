package adapters;

import exceptions.InvalidHostException;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

class Host{
    public String hostName;
    public int hostPort;
    public String username;
    public String password;
    public Host(String hostName, int hostPort, String username,String password){
        this.hostName = hostName;
        this.hostPort = hostPort;
        this.username = username;
        this.password = password;
    }

    @Override
    public String toString() {
        return String.format("%s:%s@%s:%d",this.username,this.password,this.hostName,this.hostPort);
    }

    protected Host validate() throws InvalidHostException{
        if (hostName == null || username == null || password == null || hostPort > 65535){
            throw new InvalidHostException();
        }
        return this;
    }
}
public class ConnectionInfo {
    public String protocol;
    public int connectionsPerHost = 64;
    public boolean useSsl = true;
    protected List<Host> hosts = new LinkedList<Host>();
    public String database;
    public String queryString;
    public ConnectionInfo addHost(String hostName,int hostPort, String username, String password){
        hosts.add(new Host(hostName,hostPort,username,password));
        return this;
    }


    @Override
    public String toString() {
        String hostsString = this.hosts.stream().map(Host::toString).collect(Collectors.joining(","));
        return String.format("%s://%s/%s",this.protocol,hostsString,(this.queryString != null) ? "?"+this.queryString : "");
    }
}
