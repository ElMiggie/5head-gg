package RiotAPI;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RiotAPI {
  /**
   * NOTE: Format of value is {Winrate, pickrate, banrate}.
   */
  private static final Map<String, List<Double>> mapOfChampToWinPickBan = new HashMap<>();

  /**
   *
   * @return a map of String -> String, i.e. chogath -> https://static.u.gg/assets/lol/riot_static/10.8.1/img/splash/Chogath_0.jpg
   */

  private static final Map<String, List<String>> mapOfChampToImageURL = new HashMap<>();

  public static Map<String, List<String>> getMapOfChampToImageURL() {
    return mapOfChampToImageURL;
  }

  /**
   *
   * @return a map of String: champname _> List of WR PR BR: doubles.
   */
  public static Map<String, List<Double>> getMapOfChampToWinPickBan() {
    return mapOfChampToWinPickBan;
  }

  /**
   * Overloaded method that takes in a champ name and updates map for that champ.
   * Mostly for debugging purposes.
   * @param champname - Original string of champ name - i.e. Jarvan IV not jarvaniv, Kai'sa not kaisa.
   */
  public static void updateMapOfChamps(String champname) {
    try {
      // We get name into appropriate format, i.e. Jarvan IV --> jarvaniv, cho'gath --> chogath
      String urlFriendlyName = urlFriendlyName(champname);
      // We visit the site and parse the rates
      Document document = Jsoup.connect("https://u.gg/lol/champions/" + urlFriendlyName + "/build").get();
      //System.out.println(document.title());
      Elements price = document.select(".value:contains(%)");
      //Construct the list to be used as value
      List<Double> listOfWrPrBr = new ArrayList<>();
      for (org.jsoup.nodes.Element element : price) {
        Double rate = Double.parseDouble(element.text().replace("%", ""));
        listOfWrPrBr.add(rate);
      }
      // NOTE: Original champname gets put into map. i.e. Jarvan IV NOT jarvaniv
      mapOfChampToWinPickBan.put(champname, listOfWrPrBr);

      //gets the links to the splash image and the icon of the champions
      List<String> splashIconList = new ArrayList<>();
      Elements background = document.getElementsByClass("champion-profile-container");
      String style = background.get(0).attr("style");
      String splash = style.substring(style.indexOf("https://"), style.indexOf(".jpg") + 4);
      String icon = document.getElementsByClass("champion-image").attr("src");
      splashIconList.add(icon);
      splashIconList.add(splash);
      mapOfChampToImageURL.put(urlFriendlyName, splashIconList);

    } catch (IOException | NullPointerException e) {
      System.out.println("Error connecting to u.gg for: " + champname);
    } catch (NumberFormatException e) {
      System.out.println("Couldn't parse rate for " + champname);
    }
  }

  /**
   * Overloaded method that takes no argument, and updates map of champ for every champion.
   */
  public static void updateMapOfChamps() {
    for (String champname : ChampConsts.getChampNames()) {
      updateMapOfChamps(champname);
      cleanupChampInMaps(champname);
    }
  }

  /**
   * Checks if anything's value is null in any map, and sets it to a non-null value if it is;
   * NOTE: Meant to be called after updateMapOfChamps
   * @param champname The champion to be "cleaned up."
   */
  protected static void cleanupChampInMaps(String champname) {
    String urlName = urlFriendlyName(champname);
    //default icon and splash
    String icon;
    if (getIconByName(champname) == null) {
      icon = "";
    } else {
      icon = getIconByName(champname);
    }

    String splash;
    if (getSplashByName(champname) == null) {
      splash = "";
    } else {
      splash = getSplashByName(champname);
    }

    List<Double> defaultWrPrBr = new ArrayList<>();
    defaultWrPrBr.add(50.0);
    defaultWrPrBr.add(5.0);
    defaultWrPrBr.add(5.0);

    List<Double> wrPrBr;
    if (getMapOfChampToWinPickBan().get(champname) == null) {
      wrPrBr = defaultWrPrBr;
    } else {
      wrPrBr = getMapOfChampToWinPickBan().get(champname);
    }

    List<String> iconSplashList = new ArrayList<>();
    iconSplashList.add(icon);
    iconSplashList.add(splash);

    mapOfChampToImageURL.put(urlName, iconSplashList);
    mapOfChampToWinPickBan.put(urlName, wrPrBr);
  }

  /**
   * Gets a champion's icon.
   * @param champname - Champ name for which to get Icon for: its a .png
   *
   * @return String denoting icon url
   * For instance: Ashe -> https://static.u.gg/assets/lol/riot_static/10.8.1/img/champion/Ashe.png
   */
  public static String getIconByName(String champname) {
    String urlFriendlyName = urlFriendlyName(champname);
    List<String> list = mapOfChampToImageURL.get(urlFriendlyName);
    if (list == null) {
      return "";
    } else {
      return list.get(0);
    }
  }

  /**
   * Gets a champion's splash image.
   * @param champname - Champ name for which to get Splash for: its a .jpg
   *
   * @return String denoting icon url
   * For instance: Cho'gath -> https://static.u.gg/assets/lol/riot_static/10.8.1/img/splash/Chogath_0.jpg
   */
  public static String getSplashByName(String champname) {
    String urlFriendlyName = urlFriendlyName(champname);
    List<String> list = mapOfChampToImageURL.get(urlFriendlyName);
    if (list == null) {
      return "";
    } else {
      return list.get(1);
    }
  }

  /**
   * Changes a champion's name to a URL friendly name by making all letters lowercase
   * and removing all apostrophes and spaces
   * @param champname The name to be replaced.
   * @return A name to be used in URLs.
   */
  static String urlFriendlyName(String champname) {
    String urlFriendlyName = champname.toLowerCase().replace("'", "");
    urlFriendlyName = urlFriendlyName.replace(" ", "");
    return urlFriendlyName;
  }
}