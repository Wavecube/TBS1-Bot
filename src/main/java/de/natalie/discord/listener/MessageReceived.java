package de.natalie.discord.listener;

import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageReceived extends ListenerAdapter {

  @Override
  public void onPrivateMessageReceived( PrivateMessageReceivedEvent e ) {
    System.out.println( e.getMessage() );
  }


  //@Override
  //public void onPrivateMessageReceived( @NotNull PrivateMessageReceivedEvent e ) {
  //  System.out.println( e.getMessage().getContentRaw() );
  //  String msg = e.getMessage().getContentRaw();
  //  System.out.println("'" + msg.substring( 1 ) + "' : " + msg.startsWith( "!" ));
  //  if ( msg.startsWith( "!" ) ) {
  //    String command = msg.substring( 1 );
  //    switch ( command ) {
  //      case "help" -> e.getChannel().sendMessage( e.getAuthor().getName() ).queue();
  //      case "command1" -> System.out.println("Test");
  //      default -> e.getChannel().sendMessage( command );
  //    }
  //  }
  //}
}
