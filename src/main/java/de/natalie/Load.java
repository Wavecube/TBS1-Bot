package de.natalie;

import de.natalie.teamspeak.AfkMover;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Load {

  public static void main( String[] args ) {
    runSchedulers();
  }

  private static void runSchedulers(){
    ScheduledExecutorService afk_scheduler = Executors.newScheduledThreadPool(1);
    afk_scheduler.scheduleAtFixedRate( new AfkMover(), 5, 1, TimeUnit.SECONDS );
  }
}
