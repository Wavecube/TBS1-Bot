package de.natalie.teamspeak;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;

import java.util.Arrays;
import java.util.Properties;

public class BotMover implements Runnable {

  private final TS3Api api;
  private final int channel;
  private final int group;

  public BotMover( TS3Api api, Properties p ) {
    this.api = api;
    channel = Integer.parseInt( p.getProperty( "botChannel" ) );
    group = Integer.parseInt( p.getProperty( "botGroup" ) );
  }

  @Override
  public void run() {
    api.getClients().stream()
        .filter( c -> c.getChannelId() != channel )
        .filter( c -> c.getServerGroups().length == 1 )
        .filter( c -> Arrays.stream( c.getServerGroups() ).findFirst().getAsInt() == group )
        .filter( this::botIsAlone )
        .forEach( c -> api.moveClient( c.getId(), channel ) );

  }

  private boolean botIsAlone( Client bot ) {
    return api.getClients().stream()
        .filter( c -> !c.isServerQueryClient() )
        .filter( c -> c.getChannelId() == bot.getChannelId() )
        .count() == 1;
  }

}

