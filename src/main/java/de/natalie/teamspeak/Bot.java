package de.natalie.teamspeak;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.TS3Config;
import com.github.theholywaffle.teamspeak3.TS3Query;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Bot {

  private final TS3Config config = new TS3Config();
  private final TS3Query query = new TS3Query( config );
  private final TS3Api api = query.getApi();

  public Bot( String host, int port ) {
    config.setHost( host );
    config.setQueryPort( port );
  }

  public Bot login( String username, String password, int vServerId ) {
    query.connect();
    api.login( username, password );
    api.selectVirtualServerById( vServerId );
    return this;
  }

  public Bot login( String password, int vServerId ) {
    api.login( "serveradmin", password );
    api.selectVirtualServerById( vServerId );
    return this;
  }

  public Bot nickname( String nickname ) {
    api.setNickname( nickname );
    return this;
  }

  public TS3Api api() {
    return api;
  }

  public void runFeatures() {
    runSchedulers();
  }

  private void runSchedulers() {
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool( 1 );
    //scheduler.scheduleAtFixedRate( new AfkMover( api ), 0, 2, TimeUnit.SECONDS );
    //scheduler.scheduleAtFixedRate( new BotMover( api ), 0, 2, TimeUnit.SECONDS );
    scheduler.scheduleAtFixedRate( new StreamingChannel( api ), 0, 2, TimeUnit.SECONDS );


  }

}
