package de.natalie.teamspeak;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.ChannelProperty;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;

import java.util.*;

public class StreamingChannel implements Runnable {

  private final TS3Api api;
  private final List<Integer> sc = new ArrayList<>();
  private final Properties p;

  public StreamingChannel( TS3Api api, Properties p ) {
    for ( String s : p.getProperty( "streamingChannel" ).split( "," ) )
      sc.add( Integer.parseInt( s.strip() ) );
    this.api = api;
    this.p = p;
  }

  @Override
  public void run() {
    api.getClients().stream()
        .filter( c -> sc.contains( c.getChannelId() ) )
        .forEach( this::doChannelWork );
  }

  private void doChannelWork( Client c ) {
    Map<ChannelProperty, String> cp = new HashMap<>();

    cp.put( ChannelProperty.CPID, String.valueOf( c.getChannelId() ) );
    cp.put( ChannelProperty.CHANNEL_FLAG_TEMPORARY, "1" );

    String channelName = p.getProperty( "streamingChannelName" )
        .replace( "%name%", c.getNickname() );
    int subChannel = api.createChannel( channelName , cp );

    api.moveClient( c.getId(), subChannel );
    api.moveClient( api.whoAmI().getId(), Integer.parseInt( p.getProperty( "defaultChannel" ) ) );

  }

}
