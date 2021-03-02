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
    Element root = (Element) doc.getElementsByTagName( "config" ).item( 0 );

    processTsConf( root );
    processDcConf( root );

  }

  private static void processTsConf( Element root ) {
    NodeList nl = root.getElementsByTagName( "teamspeak" );
    if ( nl.getLength() != 0 ) {

      Consumer<Element> c = e -> {
        Properties p = new Properties();

        p.put( "type", "teamspeak" );
        p.put( "nickname", e.getTagName() );
        nodesToElements( e.getChildNodes() ).forEach( element -> lp( p, element ) );

        props.add( p );
      };

      nodesToElements( nl.item( 0 ).getChildNodes() ).forEach( c );
    }
    else
      System.out.println( "No Teamspeak bots were created." );
  }

  private static void processDcConf( Element root ) {

    NodeList nl = root.getElementsByTagName( "discord" );

    switch ( nl.getLength() ) {
      case 0 -> System.out.println( "No Discord bots were created." );
      case 1 -> {
        Properties p = new Properties();
        p.put( "type", "discord" );
        Consumer<Element> c = e -> p.put( e.getTagName(), e.getTextContent().strip() );
        if ( nl.getLength() == 1 )
          nodesToElements( nl.item( 0 ).getChildNodes() ).forEach( c );
        else
          throw new IllegalArgumentException( "Only one Discord config is allowed." );
        p.forEach( ( o1, o2 ) -> System.out.println( o1 + " " + o2 ) );
        props.add( p );
      }
      default -> throw new IllegalArgumentException( "There cant be more than one Discord bot." );
    }
  }

  private static void lp( Properties p, Element e ) {
    p.put( e.getTagName(), e.getTextContent().strip() );
  }

  private static List<Element> nodesToElements( NodeList nodes ) {
    List<Element> elements = new ArrayList<>();

    for ( int ci = 0; ci < nodes.getLength(); ci++ ) {
      Node n = nodes.item( ci );
      if ( n instanceof Element )
        elements.add( (Element) n );
    }
    return elements;
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
      case "discord" -> new de.natalie.discord.Bot( p );
      case "teams" -> System.out.println( "hello" );
      default -> throw new IllegalArgumentException(
          p.getProperty( "type" ) + ", is not a valid bot type." );
    }

  }

  public static void run() {

    props.forEach( ConfigProcessor::runBot );

  }

}
