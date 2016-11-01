
/**
 * This class is the main class of the "World of Zuul" application. "World of
 * Zuul" is a very simple, text based adventure game. Users can walk around some
 * scenery. That's all. It should really be extended to make it more
 * interesting!
 *
 * To play this game, create an instance of this class and call the "play"
 * method.
 *
 * This main class creates and initialises all the others: it creates all rooms,
 * creates the parser and starts the game. It also evaluates and executes the
 * commands that the parser returns.
 *
 */
/**
 *
 * @author Adamino
 */
class Game {

    private final Parser mParser;
    private final Player player;
    private final String GAME_TITLE = "Adamino's magical console adventure!";
    private final String QUIT_MESSAGE = "Thank you for playing... Looser";
    private static final String UNKNOWN = "I don't know what you mean...";
    private static final String NORTH = "north";
    private static final String SOUTH = "south";
    private static final String EAST = "east";
    private static final String WEST = "west";

    /**
     * Create the game and initialise its internal map.
     */
    public Game() {
        player = new Player("Adam", 10);
        createRooms();
        mParser = new Parser();
    }

    /**
     * Create all the rooms and link their exits together.
     */
    private void createRooms() {
        Room casleEntrance, castleMainHall, wineCellar, dungeon, tower, tortureRoom, creepyBedroom, courtRoom, sewars, attic, kithen, laboratory, ballRoom, secretPassage, lavatory;

        // create the rooms
        casleEntrance = new Room("the main entrance to the castle", false);
        castleMainHall = new Room("a castle main hall", false);
        wineCellar = new Room("the wine cellar", false);
        dungeon = new Room("a dungeon", false);
        tower = new Room("stairs to the tower", true);
        tortureRoom = new Room("a dark torture room full of instruments used in the popular movie 'Fifty Shades of Grey'", false);
        creepyBedroom = new Room("a creepy bedroom riveting of lust and desire, but also despair...", false);
        courtRoom = new Room("a courtroom. The place of judgement. Truth will out!", false);
        sewars = new Room("a dank sewar filled with moist and a flowing river", false);
        attic = new Room("a mysterious attic with weird sound coming from the back of the room", false);
        kithen = new Room("a huge kithen which smells awefully from... blood!?", false);
        laboratory = new Room("a scary laboratory containing a huge table with a monster lying on it!", false);
        ballRoom = new Room("a spectacular vast room with a reminiscens of people dancing from another age", false);
        secretPassage = new Room("a hidden passage, which is quite narrow", true);
        lavatory = new Room("a very old lavatory for women. When you enter the room you hear a sobbing cry from a long forgotte ghost named 'Moaning Myrtle'", false);

        // initialise room exits
        //TODO ALH: Redo and add exits
        casleEntrance.setExit(EAST, castleMainHall);
        casleEntrance.setExit(SOUTH, dungeon);
        casleEntrance.setExit(WEST, wineCellar);

        castleMainHall.setExit(WEST, casleEntrance);

        wineCellar.setExit(EAST, casleEntrance);

        dungeon.setExit(NORTH, casleEntrance);
        dungeon.setExit(EAST, tower);

        tower.setExit(WEST, dungeon);

        // Add items to the rooms
        //TODO ALH: Redo and add items
        casleEntrance.addItem("wase1", "a beautiful wase", 2);
        casleEntrance.addItem("wase2", "a small very ugly wase", 1);

        castleMainHall.addItem("carpet1", "a huge carpet on the floor with a dragon on", 3);
        castleMainHall.addItem("wase3", "a huge blue wase with red dragon flames on", 2);

        wineCellar.addItem("wineRack", "a winerack with 200 different wines in", 150);
        wineCellar.addItem("secretKey", "a mysterious key that might grant you access to a certain location", 0);

        dungeon.addItem("skeleton", "a skeleton hanging from the ceiling", 1);

        tower.addItem("chandelier", "an Old dusty chandelier", 50);

        // start game at the entrance
        player.setCurrentRoom(casleEntrance);
    }

    /**
     * Main play routine. Loops until end of play.
     */
    public void play() {
        printWelcome();

        // Enter the main command loop.  Here we repeatedly read commands and
        // execute them until the game is over.
        boolean finished = false;
        while (!finished) {
            Command command = mParser.getCommand();
            finished = processCommand(command);
        }
        System.out.println(QUIT_MESSAGE);
    }

    /**
     * Print out the opening message for the player.
     */
    private void printWelcome() {
        System.out.println();
        System.out.println("Welcome to " + GAME_TITLE);
        System.out.println(GAME_TITLE + " is a new, incredibly exciting discovery game.");
        System.out.println("Type 'help' if you need help.");
        System.out.println();
        System.out.println(player.getCurrentRoom().getLongDescription());
    }

    /**
     * Given a command, process (that is: execute) the command. If this command
     * ends the game, true is returned, otherwise false is returned.
     */
    private boolean processCommand(Command command) {
        boolean wantToQuit = false;

        if (command.isUnknown()) {
            System.out.println(UNKNOWN);
            return false;
        }

        CommandWord commandWord = command.getCommandWord();
        String secondWord = command.getSecondWord();

        switch (commandWord) {
            case UNKNOWN:
                System.out.println("I don't know what you mean...");
                break;
            case HELP:
                printHelp();
                break;
            case GO:
                player.goRoom(command);
                break;
            case QUIT:
                wantToQuit = quit(command);
                break;
            case LOOK:
                player.look();
                break;
            case BACK:
                player.goBack();
                break;
            case TAKE:
                player.takeItem(secondWord);
                break;
            case DROP:
                player.dropItem(secondWord);
                break;
            case ITEMS:
                player.inventoryStatus();
                break;
            default:
                break;
        }
        return wantToQuit;
    }

    // implementations of user commands:
    /**
     * Print out some help information. Here we print some stupid, cryptic
     * message and a list of the command words.
     */
    private void printHelp() {
        System.out.println("You are lost. You are alone. You wander");
        System.out.println("around at the castle.");
        System.out.println();
        System.out.println("Your command words are:");
        System.out.println(mParser.showCommands());
    }

    /**
     * "Quit" was entered. Check the rest of the command to see whether we
     * really quit the game. Return true, if this command quits the game, false
     * otherwise.
     */
    private boolean quit(Command command) {
        if (command.hasSecondWord()) {
            System.out.println("Quit what?");
            return false;
        } else {
            return true;  // signal that we want to quit
        }
    }
}
