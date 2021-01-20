package de.natalie;

import de.natalie.teamspeak.AfkMover;
import de.natalie.teamspeak.Bot;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class Load {

  public static final List<Bot> ts3Bots = new ArrayList<>();

  public static void main( String[] args ) {
    try {
      runTeamspeakBots();
    }
    catch ( ParserConfigurationException | IOException | SAXException e ) {
      e.printStackTrace();
    }

    // runSchedulers();
  }

  private static void runSchedulers() {
    ScheduledExecutorService afk_scheduler = Executors.newScheduledThreadPool( 1 );
    afk_scheduler.scheduleAtFixedRate( new AfkMover(), 5, 1, TimeUnit.SECONDS );
  }

  private static void runTeamspeakBots()
      throws ParserConfigurationException, IOException, SAXException {

    URL path = Load.class.getClassLoader().getResource( "default_config.xml" );
    File file = new File( path.getFile() );
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document document = builder.parse( file );

    document.getDocumentElement().normalize();

    NodeList teamspeakNode = document.getElementsByTagName( "teamspeak" );

    Node tsNode = teamspeakNode.item( 0 );
    NodeList configs = tsNode.getChildNodes();

    for ( int ci = 0; ci < configs.getLength(); ci++ ) {
      Node node = configs.item( ci );
      if ( node instanceof Element ) {
        Element config = (Element) node;
        String nickname = config.getTagName();
        String host = uniqueNodeContent( config, "host" );
        int port = Integer.parseInt( uniqueNodeContent( config, "port" ) );
        String user = uniqueNodeContent( config, "user" );
        String password = uniqueNodeContent( config, "password" );
        int server_id = Integer.parseInt( uniqueNodeContent( config, "server-id" ) );
        Stream.of( nickname, host, port, user, password ).forEach( System.out::println );

        ts3Bots.add( new Bot( host, port ).login( user, password, server_id ).nickname( nickname ) );
      }
    }
  }

  private static String uniqueNodeContent( Element element, String tag ) {
    return element.getElementsByTagName( tag ).item( 0 ).getTextContent();
  }
}
