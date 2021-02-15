package de.natalie;

import de.natalie.utils.ConfigurationProcessor;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class Load {

  public static void main( String[] args ) {
    try {
      ConfigurationProcessor.runTeamspeakBots();
    }
    catch ( ParserConfigurationException | IOException | SAXException e ) {
      e.printStackTrace();
    }
  }

}
