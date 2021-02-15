package de.natalie.teamspeak;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class AfkMover implements Runnable {

  private final TS3Api api;
  private static final HashMap<Integer, Integer> moved = new HashMap<>();
  private static final HashMap<Client, Long> afk = new HashMap<>();

  public AfkMover( TS3Api api ) {
    this.api = api;
  }

  @Override
  public void run() {

    int afk_channel = 2;
    int afk_millis = 2000;
    List<Client> clients = api.getClients().stream()
        .filter( c -> !c.isServerQueryClient() )
        .collect( Collectors.toList() );

    afk.keySet().stream().forEach( System.out::println );
    // Add clients to a list if they are muted.
    clients.stream().filter( Client::isOutputMuted )
        .forEach( c -> afk.put( c, System.currentTimeMillis() ) );

    //remove them if they are not.
    clients.stream().filter( c -> !c.isOutputMuted() || c.getChannelId() == afk_channel )
        .forEach( afk::remove );

    // Move all clients that are AFK for a certain time.
    for ( Client c : afk.keySet() )
      if ( c.getChannelId() != afk_channel && (afk.get( c ) + afk_millis) >= System
          .currentTimeMillis() ) {
        moved.put( c.getId(), c.getChannelId() );
        afk.remove( c );
        api.moveClient( c.getId(), afk_channel );
        moved.forEach( (( client, integer ) -> System.out.println( client )) );
      }

    // Move clients back if they are not AFK anymore.
    api.getClients().stream()
        .filter( c -> moved.containsKey( c.getId() ) && !c.isOutputMuted() )
        .forEach( c -> {
          api.moveClient( c.getId(), moved.get( c.getId() ) );
          moved.remove( c.getId() );
        } );
  }
}
