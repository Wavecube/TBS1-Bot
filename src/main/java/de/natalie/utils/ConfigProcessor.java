package de.natalie.utils;

import de.natalie.teamspeak.Bot;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;

public class ConfigProcessor {

  private static final List<Properties> props = new ArrayList<>();
  private static final Path p = Path.of( "config.xml" );

  public static void createDefaults() {
    if ( !Files.exists( p ) ) {
      try ( InputStream is = ConfigProcessor.class.getClassLoader()
          .getResourceAsStream( "default_config.xml" ) ) {
        Files.createFile( p );
        Files.write( p, is.readAllBytes() );
      }
      catch ( IOException e ) {
        e.printStackTrace();
      }
    }
  }

  public static void readConfigs() {

    Document doc = document();
    if ( doc == null )
      throw new NullPointerException( "The config file does not exist." );

    doc.getDocumentElement().normalize();

    processTsConf( doc.getElementsByTagName( "teamspeak" ) );
    //processDcConf( doc.getElementsByTagName( "discord" ) );
    //processTeamsConf( doc.getElementsByTagName( "teams" ) );

  }

  private static void processTsConf( NodeList nl ) {

    NodeList nodes = nl.item( 0 ).getChildNodes();
    List<Element> bots = new ArrayList<>();

    for ( int ci = 0; ci < nodes.getLength(); ci++ ) {
      Node n = nodes.item( ci );
      if ( n instanceof Element )
        bots.add( (Element) n );
    }

    Consumer<Element> c = e -> {
      Properties p = new Properties();

      p.put( "type", "teamspeak" );
      p.put( "nickname", e.getTagName() );

      loadProp( p, e, "host" );
      loadProp( p, e, "port" );
      loadProp( p, e, "user" );
      loadProp( p, e, "password" );
      loadProp( p, e, "vServerId" );
      loadProp( p, e, "schedulerTiming" );

      loadProp( p, e, "afkMoverEnabled" );
      loadProp( p, e, "afkChannel" );
      loadProp( p, e, "afkTime" );

      loadProp( p, e, "botMoverEnabled" );
      loadProp( p, e, "botChannel" );
      loadProp( p, e, "botGroup" );

      loadProp( p, e, "streamingChannelEnabled" );
      loadProp( p, e, "streamingChannelName" );
      loadProp( p, e, "streamingChannel" );

      props.add( p );
    };

    bots.forEach( c );

  }

  private static void loadProp( Properties p, Element e, String tag ) {
    p.put( tag, e.getElementsByTagName( tag ).item( 0 ).getTextContent().strip() );
  }

  private static void processDcConf( NodeList nl ) {
    if ( nl.getLength() != 1 )
      throw new IllegalArgumentException( "Only one discord config is allowed." );

  }

  private static void processTeamsConf( NodeList nl ) {
    if ( nl.getLength() != 1 )
      throw new IllegalArgumentException( "Only one Teams config is allowed." );

  }

  private static Document document() {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    Document document = null;
    try {
      DocumentBuilder builder = factory.newDocumentBuilder();
      document = builder.parse( p.toFile() );
    }
    catch ( ParserConfigurationException | SAXException | IOException e ) {
      e.printStackTrace();
    }
    return document;
  }

  private static void runBot( Properties p ) {
    switch ( p.getProperty( "type" ) ) {
      case "teamspeak" -> new Bot( p );
      case "discord" -> System.out.println( "ho" );
      case "teams" -> System.out.println( "hello" );
      default -> throw new IllegalArgumentException(
          p.getProperty( "type" ) + ", is not a valid bot type." );
    }
  }

  public static void run() {
    props.forEach( ConfigProcessor::runBot );
  }

}
