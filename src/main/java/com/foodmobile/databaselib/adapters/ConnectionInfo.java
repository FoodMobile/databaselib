package com.foodmobile.databaselib.adapters;

import com.foodmobile.databaselib.exceptions.InvalidHostException;

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

    /**
     * Represents the type of protocol section of the typical connection URL (mongodb,mysql, etc)
     */
    public String protocol;

    /**
     * Number of concurrent connections available for the adapter to use. (Connection Pooling Feature)
     */
    public int connectionsPerHost = 64;

    /**
     * Specifies whether the adapter should try to connect to the database using ssl
     */
    public boolean useSsl = true;

    protected List<Host> hosts = new LinkedList<Host>();

    /**
     * Primary database (schema) name to be used by the adapter
     */
    public String database;

    /**
     * Contains the query of the connection URL. This will differ per adapter.
     */
    public String queryString;

    /**
     * To support cluster database servers, multiple hosts may be added. Note if a non-cluster enabled adapter is
     * in use, the first host will be used to connect. There must be at least 1 host added for a successful connection.
     * @param hostName Host address for the database server
     * @param hostPort Port the database server is running on
     * @param username Username used to authenticate with the database
     * @param password Password used to authenticate with the database
     * @return Returns `this`, used for chaining. ex new ConnectionInfo().addHost(...) creates a connectionInfo instance
     * and adds a host.
     */
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
