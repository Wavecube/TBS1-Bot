package de.natalie;

import de.natalie.utils.ConfigProcessor;

public class Load {

  public static void main( String[] args ) {
    ConfigProcessor.createDefaults();
    ConfigProcessor.readConfigs();
    ConfigProcessor.run();
  }

}
