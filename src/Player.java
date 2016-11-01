
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
public class Player {

    private final String mNAME;
    private final int mMAX_LOAD;
    private boolean mHaveKey;
    private final Stack mPREVIOUS_ROOMS;
    private final List<Item> takenItems;
    private final int LOW_HIT_DAMGE = 1;
    private final int MEDIUM_HIT_DAMAGE = 3;
    private final int CRITICAL_HIT_DAMAGE = 5;

    private Room mCurrentRoom;
    private int mLoadLeft;
    private int mDamage;
    private int mHealth;
    private boolean mHasFinalWeapon;

    /**
     * Creates the Player
     *
     * @param name
     * @param health
     * @param maxLoad
     */
    public Player(String name, int health, int maxLoad) {
        mNAME = name;
        mMAX_LOAD = maxLoad;
        mLoadLeft = mMAX_LOAD;
        mPREVIOUS_ROOMS = new Stack();
        takenItems = new ArrayList<>();
        mHaveKey = false;
        mDamage = LOW_HIT_DAMGE;
        mHealth = health;
        mHasFinalWeapon = false;

    }

    /**
     * Gets the players name
     *
     * @return
     */
    public String getPlayerName() {
        return mNAME;
    }

    /**
     * Gets the players max load
     *
     * @return
     */
    public int getPlayerMaxLoad() {
        return mMAX_LOAD;
    }

    /**
     * Gets the current Room of the Player
     *
     * @return
     */
    public Room getCurrentRoom() {
        return mCurrentRoom;
    }

    public Stack getPreviousRooms() {
        return mPREVIOUS_ROOMS;
    }

    /**
     * Sets the current room of the player
     *
     * @param currentRoom
     */
    public void setCurrentRoom(Room currentRoom) {
        mCurrentRoom = currentRoom;
    }

    /**
     * Try to go to one direction. If there is an exit, enter the new room,
     * otherwise print an error message.
     *
     * @param command
     */
    public void goRoom(Command command) {
        if (!command.hasSecondWord()) {
            // if there is no second word, we don't know where to go...
            System.out.println("Go where?");
            return;
        }

        String direction = command.getSecondWord();

        // Try to leave current room.
        Room nextRoom = getCurrentRoom().getExit(direction);

        if (nextRoom == null) {
            System.out.println("There is no door!");
        } else {
            enterNextRoom(nextRoom);
        }
    }

    /**
     * If room isn't locked enter the next room If the room is locked, but we
     * have the key we can enter!
     *
     * @param nextRoom
     */
    private void enterNextRoom(Room nextRoom) {
        //Check if the room is locked and if so, if we have the secret key
        if (nextRoom.isLocked() && !mHaveKey) {
            System.out.println("You do not have the secret key to enter " + nextRoom.getShortDescription() + "!");
        } else {
            if (nextRoom.isLocked() && mHaveKey) {
                System.out.println("You use your secret key to open the door!");
            }
            //Room is accessable and we will remember the last room we visited before entering
            getPreviousRooms().add(getCurrentRoom());
            setCurrentRoom(nextRoom);
            //If Room has a challenge it will be presented, if not we'll just get info about the room
            checkForChallenge();
            checkForMonster();
        }
    }

    /**
     * Player checks the Room for a Monster
     */
    private void checkForMonster() {
        if (mCurrentRoom.hasMonster()) {
            fightMonster();
        }
    }

    /**
     * Checks the next room for a challenge
     */
    private void checkForChallenge() {
        if (mCurrentRoom.hasChallenge()) {
            System.out.println(mCurrentRoom.getChallenge());
            if (mCurrentRoom.isChallengePassed()) {
                System.out.println("Congratulations! you passed the test.");
                System.out.println(getCurrentRoom().getLongDescription());
            } else {
                teleportToBeginning();
            }
        } else {
            System.out.println(getCurrentRoom().getLongDescription());
        }
    }

    /**
     * Teleport the soor looser back to the beginning!
     */
    private void teleportToBeginning() {
        System.out.println("Unfortunately that is wrong... TELEPORTING");
        mCurrentRoom = (Room) mPREVIOUS_ROOMS.firstElement();
        System.out.println(getCurrentRoom().getLongDescription());
    }

    /**
     * Player picks up the item if it is not too heavy
     *
     * @param itemName
     */
    public void takeItem(String itemName) {
        for (Item mItem : getCurrentRoom().getItems()) {
            if (mItem.getItemName().equals(itemName)) {
                if (mLoadLeft >= mItem.getItemWeight()) {
                    addItemToInventory(itemName, mItem);
                    checkForSecretKey(mItem);
                    checkForWeapon();
                    inventoryStatus();
                    break;
                } else {
                    System.out.println("Sorry you don't have enough space to take " + mItem.getItemName());
                }
            }
        }
    }

    /**
     * Adds current item to the players inventory and reduces the total weigh
     * the player can hold
     *
     * @param itemName
     * @param mItem
     */
    private void addItemToInventory(String itemName, Item mItem) {
        System.out.println("Took item " + itemName);
        takenItems.add(mItem);
        mLoadLeft -= mItem.getItemWeight();
        getCurrentRoom().getItems().remove(mItem);
    }

    /**
     * Checks if the player picked up the secret key!
     *
     * @param mItem
     */
    private void checkForSecretKey(Item mItem) {
        if (mItem.getItemName().equals("secretKey")) {
            System.out.println("You found the secret key and should now look for the locked door!");
            setSecretKey();
        }
    }

    /**
     * Drops the item to the mCurrentRoom
     *
     * @param itemName
     */
    public void dropItem(String itemName) {
        for (Item mItem : takenItems) {
            if (mItem.getItemName().equals(itemName)) {
                System.out.println("Dropped item " + itemName);
                takenItems.remove(mItem);
                mLoadLeft += mItem.getItemWeight();
                getCurrentRoom().getItems().add(mItem);
                inventoryStatus();
                break;
            }
        }
    }

    /**
     * Prints information about the players inventory status
     */
    public void inventoryStatus() {
        if (takenItems.isEmpty()) {
            System.out.println("You don't currently hold any items");
        } else {
            System.out.println("You're currently holding " + getTakenItemsAsString());
        }
        System.out.println("Of your maximum capacity of " + mMAX_LOAD + " Kg" + " you have " + mLoadLeft + " Kg left!");
    }

    /**
     * Takes a look around the room and reports back the exits!
     */
    public void look() {
        System.out.println(getCurrentRoom().getLongDescription());
    }

    /**
     * Sends the player back to the previous room
     */
    public void goBack() {
        if (!getPreviousRooms().isEmpty()) {
            setCurrentRoom((Room) getPreviousRooms().lastElement());
            getPreviousRooms().remove(getPreviousRooms().lastElement());
            System.out.println(getCurrentRoom().getLongDescription());
        } else {
            System.out.println("You're at the beginning!");
        }
    }

    /**
     * Gets a String of the items the player currently holds
     */
    private String getTakenItemsAsString() {
        String allItems = "";
        for (Item item : takenItems) {
            allItems += item.getItemName() + " ";
        }
        return allItems;
    }

    /**
     * Updates the player to have the secret key!
     */
    private void setSecretKey() {
        mHaveKey = true;
    }

    /**
     * Fight the monster
     */
    public void fightMonster() {
        System.out.println("\nAaaaaarh there is a monster in here!!!");
        checkForLastBossEncounter();
        boolean monsterIsAlive = true;
        System.out.println("\nCombat vs " + mCurrentRoom.getMonster().get(0).getName() + " begins!");
        while (monsterIsAlive) {
            hitMonster();
            monsterIsAlive = isMonsterStillAlive();
            if (monsterIsAlive) {
                System.out.println(mCurrentRoom.getMonster().get(0).damagePlayer());
                mHealth -= mCurrentRoom.getMonster().get(0).getDamage();
                if (mHealth <= 0) {
                    System.out.println(mCurrentRoom.getMonster().get(0).getName() + " killed you...");
                    Game.gameOver();
                    break;
                } else {
                    System.out.println("You now only have " + mHealth + " health left!");
                }
            } else {
                System.out.println("You have slayed " + mCurrentRoom.getMonster().get(0).getName() + " congratulations!");
                checkBossKill();
                mCurrentRoom.getMonster().remove(0);
            }
        }
    }

    /**
     * Check if killed boss was last boss or mini boss
     */
    private void checkBossKill() {
        if (mCurrentRoom.getMonster().get(0).getName().equals(Game.getLAST_BOSS())) {
            System.out.println("\nYou have now found the princess and she is so happy that you saved her, that she promises to marry you!");
            Game.win();
        } else {
            mCurrentRoom.getItems().add(mCurrentRoom.getMonster().get(0).getLoot());
            System.out.println("\nWhile hitting the floor " + mCurrentRoom.getMonster().get(0).getName() + " dropped "
                    + mCurrentRoom.getMonster().get(0).getLoot().getItemName() + "!" + "\nThe inscription on this weapon reads:\n"
                    + mCurrentRoom.getMonster().get(0).getLoot().getItemDescription());
        }
    }

    /**
     * Checks if we're facing the last boss
     */
    private void checkForLastBossEncounter() {
        if (mCurrentRoom.getMonster().get(0).getName().equals(Game.getLAST_BOSS())) {
            System.out.println("You found the final boss " + Game.getLAST_BOSS());
            boolean haveFinalItem = false;
            for (Item takenItem : takenItems) {
                if (takenItem.getItemName().equals(Game.getFINAL_WEAPON())) {
                    haveFinalItem = true;
                }
            }
            if (haveFinalItem) {
                System.out.println("Good thing we picked up " + Game.getFINAL_WEAPON() + "!");
                System.out.println("Now we can use it to kill " + Game.getLAST_BOSS());
            } else {
                System.out.println("Sorry you must have The Ancient Sword of Dracula to enter this fight!");
                System.out.println("You will now be teleported away to safety... TELEPORTING");
                mCurrentRoom = (Room) mPREVIOUS_ROOMS.firstElement();
            }
        }
    }

    /**
     * Player attacks the monster
     *
     * @param monsterIsAlive
     */
    private void hitMonster() {
        Random rand = new Random();
        int hitChance = rand.nextInt(3) + 1;
        switch (hitChance) {
            case 1:
                mDamage += LOW_HIT_DAMGE;
                break;
            case 2:
                mDamage += MEDIUM_HIT_DAMAGE;
                break;
            case 3:
                mDamage += CRITICAL_HIT_DAMAGE;
                break;
            default:
                break;
        }
        System.out.println("You strike " + mCurrentRoom.getMonster().get(0).getName() + " with a devastating hit for " + mDamage + " points!");
        mCurrentRoom.getMonster().get(0).takeDamage(mDamage);
        if (mCurrentRoom.getMonster().get(0).getHealth() > 0) {
            System.out.println("Monster now only has " + mCurrentRoom.getMonster().get(0).getHealth() + " health left!");
        } else {
            System.out.println("That did it!");
        }
    }

    /**
     * Checks if the monster is alive
     */
    private boolean isMonsterStillAlive() {
        boolean isMonsterAlive = true;
        if (mCurrentRoom.getMonster().get(0).isMonsterDead()) {
            isMonsterAlive = false;
        }
        return isMonsterAlive;
    }

    /**
     * Checkif the player has a weapon
     */
    private boolean checkForWeapon() {
        boolean isWeapon = false;
        for (Item takenItem : takenItems) {
            if (takenItem instanceof Weapon) {
                isWeapon = true;
                if (takenItem.getItemName().equals(Game.getFINAL_WEAPON())) {
                    mHasFinalWeapon = true;
                }
                System.out.println("You picked up a weapon!");
                increasePlayerDamage(((Weapon) takenItem).getWeaponDamage());
            }
        }
        return isWeapon;
    }

    private void increasePlayerDamage(int weaponDamage) {
        mDamage += weaponDamage;
        System.out.println("Your total damage is now " + mDamage + "!");
    }
}
