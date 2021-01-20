package de.natalie.teamspeak;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.TS3Config;
import com.github.theholywaffle.teamspeak3.TS3Query;

public class Bot {

  private final TS3Config config = new TS3Config();
  private final TS3Query query = new TS3Query( config );
  private final TS3Api api = query.getApi();

  public Bot( String host, int port ) {
    config.setHost( host );
    config.setQueryPort( port );
  }

  public void login( String username, String password, int v_server_id ){
    query.connect();
    api.login( username, password );
    api.selectVirtualServerById( v_server_id );
  }

  public void login( String password , int v_server_id){
    api.login( "serveradmin", password );
    api.selectVirtualServerById( v_server_id );
  }

  public TS3Api api(){
    return api;
  }

}
