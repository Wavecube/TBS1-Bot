package de.natalie.discord.listener;

import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class MessageReceived extends ListenerAdapter {

  @Override
  public void onMessageReceived( @NotNull MessageReceivedEvent e ) {
    System.out.println(e.getMessage());
  }

  @Override public void onReady( @NotNull ReadyEvent event ) {
    System.out.println("Hallo");
  }
}
