package de.natalie.teamspeak;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;

import java.util.Arrays;

public class BotMover implements Runnable {

  private final TS3Api api;
  private final int botChannel = 3; // <-- Muss noch übergeben werden.
  private final int botGroup = 9;   // <-- Muss noch übergeben werden.

  public BotMover( TS3Api api ) {
    this.api = api;
  }

  @Override
  public void run() {

    api.getClients().stream()
        .filter( c -> c.getChannelId() != botChannel )
        .filter( c -> c.getServerGroups().length == 1 )
        .filter( c -> Arrays.stream( c.getServerGroups() ).findFirst().getAsInt() == botGroup )
        .filter( this::botIsAlone )
        .forEach( c -> api.moveClient( c.getId(), botChannel ) );

  }

  private boolean botIsAlone( Client bot ) {
    return api.getClients().stream()
        .filter( c -> !c.isServerQueryClient() )
        .filter( c -> c.getChannelId() == bot.getChannelId() )
        .count() == 1;
  }

}

