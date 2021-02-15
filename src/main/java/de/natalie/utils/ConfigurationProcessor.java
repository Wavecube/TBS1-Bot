package de.natalie.utils;

import de.natalie.Load;
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
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class ConfigurationProcessor {

  public static final List<Bot> ts3Bots = new ArrayList<>();

  public static void runTeamspeakBots()
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

        ts3Bots
            .add( new Bot( host, port ).login( user, password, server_id ).nickname( nickname ) );
      }
    }

    for ( Bot bot : ts3Bots )
      bot.runFeatures();

  }

  private static String uniqueNodeContent( Element element, String tag ) {
    return element.getElementsByTagName( tag ).item( 0 ).getTextContent();
  }

  private static void runBots( Consumer<String[]> f )
      throws URISyntaxException, ParserConfigurationException, IOException, SAXException {

    Path p = Paths.get( Objects.requireNonNull(
        Load.class.getClassLoader().getResource( "default_config.xml" ) ).toURI() );
    Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        .parse( p.toFile() );
    document.getDocumentElement().normalize();

    NodeList nl = document.getElementsByTagName( "teamspeak" ).item( 0 ).getChildNodes();

    List<Node> configs = new ArrayList();
    for ( int i = 0; i < nl.getLength(); i++ )
      configs.add( nl.item( i ) );

    configs.stream().filter( node -> node instanceof Element )
        .map( ConfigurationProcessor::mapToConfig ).forEach( f::accept );

  }

  private static String[] mapToConfig( Node node ) {

    Element config = (Element) node;
    String nickname = config.getTagName();
    String host = uniqueNodeContent( config, "host" );
    String port = uniqueNodeContent( config, "port" );
    String user = uniqueNodeContent( config, "user" );
    String password = uniqueNodeContent( config, "password" );
    String server_id = uniqueNodeContent( config, "server-id" );

    return new String[]{ nickname, host, port, user, password, server_id };

  }
}
