package de.natalie.teamspeak;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;

import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class AfkMover implements Runnable {

  private final TS3Api api;
  private static final HashMap<Integer, Integer> moved = new HashMap<>();
  private static final HashMap<Client, Long> afk = new HashMap<>();
  private final int channel;
  private final int time;

  public AfkMover( TS3Api api, Properties p ) {
    this.api = api;
    channel = Integer.parseInt( p.getProperty( "afkChannel" ));
    time = Integer.parseInt( p.getProperty( "afkTime" ));
  }

  @Override
  public void run() {

    List<Client> clients = api.getClients().stream()
        .filter( c -> !c.isServerQueryClient() )
        .collect( Collectors.toList() );

    clients.stream()
        .filter( Client::isOutputMuted )
        .forEach( c -> afk.put( c, System.currentTimeMillis() ) );

    clients.stream()
        .filter( c -> !c.isOutputMuted() || c.getChannelId() == channel )
        .forEach( afk::remove );

    for ( Client c : afk.keySet() )
      if ( c.getChannelId() != channel && (afk.get( c ) + time) >= System
          .currentTimeMillis() ) {
        moved.put( c.getId(), c.getChannelId() );
        afk.remove( c );
        api.moveClient( c.getId(), channel );
      }

    api.getClients().stream()
        .filter( c -> moved.containsKey( c.getId() ) && !c.isOutputMuted() )
        .forEach( c -> {
          api.moveClient( c.getId(), moved.get( c.getId() ) );
          moved.remove( c.getId() );
        } );
  }
}
