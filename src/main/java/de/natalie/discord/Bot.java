package de.natalie.discord;

import de.natalie.discord.listener.MessageReceived;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import javax.security.auth.login.LoginException;
import java.util.Properties;

public class Bot {

  private JDA jda;

  public Bot( Properties p )  {
    JDABuilder builder = JDABuilder.createDefault( p.getProperty( "token" ) );
    try {
      jda = builder.build();
    }
    catch ( LoginException e ) {
      e.printStackTrace();
    }
    addListeners();
  }

  private void addListeners(){
    jda.addEventListener( new MessageReceived() );
  }
}
