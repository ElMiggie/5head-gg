package Betting;

import RiotAPI.ChampConsts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class to hold all the bets made during one patch.
 * @author sboughan
 *
 */
public class BettingSession {


  private final String type;

  // Map of champion to List<Bet>
  private final Map<String, List<Bet>> mapOfChampionToBets = new HashMap<>();
  // Map of user ID to List<Bet>
  private final Map<String, List<Bet>> mapOfUserToBets = new HashMap<>();

  /**
   * Resets the entire session by clearing its maps.
   * In practice, this is called after we detect a different Patch.
   */
  public void resetSession() {
    List<String> champions = ChampConsts.getChampNames();
    for (String hero: champions) {
      this.mapOfChampionToBets.replace(hero, new ArrayList<>());
    }

    List<String> users = this.getUsers();
    for (String user: users) {
      this.mapOfUserToBets.replace(user, new ArrayList<>());
    }
  }

  public Map<String, List<Bet>> getMapOfChampionToBets() {
    return this.mapOfChampionToBets;
  }

  /**
   * Submits a bet to the current betting session.
   * @param b The bet to be added
   */
  public void addBet(final Bet b) {
      //adds the bet to the map of bets by champion
      //by mutating the list in the value
    List<Bet> champ = mapOfChampionToBets.get(b.getCategory());
      if (champ != null) {
        champ.add(b);
      } else {
        //if the champion is not a key, put the champion as a key
        //with the new bet as a member of the value list.
    List<Bet> newChamp = new ArrayList<Bet>(List.of(b));
        mapOfChampionToBets.put(b.getCategory(), newChamp);

      }
      //adds the bet to the map of bets by user
      //by mutating the list in the value
    List<Bet> user = mapOfUserToBets.get(b.getUserID());
      if (user != null) {
        user.add(b);
      } else {
        //if the user ID is not a key, put the ID as a key
        //with the new bet as a member of the value list.
    List<Bet> newUser = new ArrayList<Bet>(List.of(b));
        mapOfUserToBets.put(b.getUserID(), newUser);
      }
  }

  /**
   * Method to get the list of bets a user has made.
   * @param id The user to search for
   * @return the list of bets whose user ID matches that 
   * of the given string
   */
  public List<Bet> getBetsFromUserID(String id) {
    return mapOfUserToBets.get(id);
  }

  /**
   * Bet type getter.
   * @return the typeof this bet
   */
  public String getType() {
    return this.type;
  }

  /**
   * Default constructor.
   * @param type the statistic the bets in the session
   * will be made respect to
   */
  public BettingSession(String type, List<String> champions) {
    this.type = type;
    for (String champ : champions) {
      mapOfChampionToBets.put(champ, new ArrayList<>());
    }

  }

  /**
   * Calculates the payouts for all the bets made on a specific champion
   * in the current session.
   * @param result the actual statistic for the champion during the patch
   * @param category The category (i.e. champion) to calculate the payouts for
   */
  public void broadcast(Double result, Double previousResult, String category) {
    Double change = result - previousResult;
    for (Bet b: mapOfChampionToBets.get(category)) {
      b.calculateChange(change);
    }
  }

  
  /**
   * Gets a list of all the users
   * @return a list of all the users in the betting session
   */
  public List<String> getUsers() {
    List<String> users = new ArrayList<>();
    users.addAll(mapOfUserToBets.keySet());
    return users;
  }



}
