package de.natalie.teamspeak;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.ChannelProperty;
import com.github.theholywaffle.teamspeak3.api.wrapper.Channel;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StreamingChannel implements Runnable {

  private final TS3Api api;
  private final List<Integer> sc = new ArrayList<>();

  public StreamingChannel( TS3Api api ) {
    sc.add( 4 );  // <-- Muss noch übergeben werden.
    sc.add( 5 );  // <-- Muss noch übergeben werden.
    this.api = api;
  }

  @Override
  public void run() {

    api.getChannels().stream()
        .filter( c -> sc.contains( c.getParentChannelId() ) )
        .filter( c -> clientCount( c ) == 0 )
        .forEach( c -> api.deleteChannel( c.getId() ) );

    api.getClients().stream()
        .filter( c -> sc.contains( c.getChannelId() ) )
        .forEach( this::doChannelWork );
  }

  private void doChannelWork( Client c ) {
    Map<ChannelProperty, String> cp = new HashMap<>();

    cp.put( ChannelProperty.CPID, String.valueOf( c.getChannelId() ) );

    int subChannel = api.createChannel( "Hallo", cp );

    api.moveClient( c.getId(), subChannel );

  }

  private long clientCount( Channel c){
    return api.getClients().stream()
        .filter( client -> client.getChannelId() == c.getId() )
        .filter( client -> !client.isServerQueryClient() )
        .count();
  }
}
