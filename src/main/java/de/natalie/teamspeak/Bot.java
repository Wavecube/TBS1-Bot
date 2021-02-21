package de.natalie.teamspeak;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.TS3Config;
import com.github.theholywaffle.teamspeak3.TS3Query;

import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Bot {

  private final TS3Config config = new TS3Config();
  private final TS3Query query = new TS3Query( config );
  private final TS3Api api = query.getApi();
  private final Properties p;

  public Bot( Properties p ) {
    this.p = p;
    config.setHost( p.getProperty( "host" ) );
    config.setQueryPort( Integer.parseInt( p.getProperty( "port" ) ) );

    login();
    run();
  }

  private void login() {
    query.connect();
    api.login( p.getProperty( "user" ), p.getProperty( "password" ) );
    api.selectVirtualServerById( Integer.parseInt( p.getProperty( "vServerId" ) ) );
    api.setNickname( p.getProperty( "nickname" ) );
    p.put( "defaultChannel", String.valueOf( api.whoAmI().getChannelId()));
  }

  private void run() {
    int t = Integer.parseInt( p.getProperty( "schedulerTiming" ) );
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool( 3 );

    if ( p.getProperty( "afkMoverEnabled" ).equalsIgnoreCase( "true" ) )
      scheduler.scheduleAtFixedRate( new AfkMover( api, p ), 0, t, TimeUnit.SECONDS );
    if ( p.getProperty( "botMoverEnabled" ).equalsIgnoreCase( "true" ) )
      scheduler.scheduleAtFixedRate( new BotMover( api, p ), 0, t, TimeUnit.SECONDS );
    if ( p.getProperty( "streamingChannelEnabled" ).equalsIgnoreCase( "true" ) )
      scheduler.scheduleAtFixedRate( new StreamingChannel( api, p ), 0, t, TimeUnit.SECONDS );
  }

}
