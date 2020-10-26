# **Dream Team Risk Project (SYSC 3110 - Software Design Project)**

### Team Members:
Name | Main Contributions
------------ | -------------
Tony Abou-Zeidan | attacking logic, battling logic, UML/SQ
Anthony Dooley | game setup, map setup, UML/SQ
Ethan Chase | attacking logic, battling logic, moving troops logic, UML/SQ
Kyler Verge | user input queries, attacking conditions, UML/SQ

### Project Description:
>The goal of this team project is to reproduce a simplified version of the classic strategy game RISK.
> Risk project!

### Since-Start Changes:
 - implemented the "hard-coding" of the world map
 - game setup (player input) implemented
 - added functionality for the user input of player names
 - added attacking functionalities
 - main game loop implemented
 - UML documentation updated
 
**Project Progress**

![97%](https://progress-bar.dev/97)

milestone 1 phase

[UML/Sequence Diagrams](https://lucid.app/invitations/accept/fdd00eb0-1f04-4212-8db9-c9dd045a9c40)

### How to Use:
When a user runs the program, they initially will be prompted for how many players they wish to be in the game (as the map is hardcoded).
After entering a valid amount of players (2-6), the user will then be prompted for the names of the players participating in the game.
As soon as the game receives input for all the player names, the game begins! The game shuffles the players to allow a new first player every time.
 
 The first player will be prompted for a decision of commands (attack, worldstate, end).
 
 Attack:
    If the player selects the attack command, they will continuously be prompted for the territory they wish to attack from, and the territory they wish to attack.
    Once the program receives valid input for these variables, the battle will commence, and the attacker will be prompted if they wish to attack or retreat.
    As long as the attacker wishes to attack, the players involved in the battle will then continuously select the amount of die they wish to roll. 
    After each set of rolls, attacking and defending territories will have their units adjusted accordingly.
    If the attacker selects retreat, the battle will end and the players turn will continue until they select to "end".
    
 WorldState:
    If the player selects the worldstate command, they will be shown a list of the entire world that they are currently playing on.
    This shows all territories, who owns them and how many units are on them.
    After this is displayed, the players turn will continue.
    
 End:
    If the player selects to end their turn, the main game loop will pass their turn onto the next player in order.
   
 All of these processes will will continue until the game ends.
   
### Decision Making:
For classes we created :
A Territory class to have a name, with a number of units occupying it, ownership being a Player object and its neighbours. We decided to use a HashMap to implement the neighbours, because the look up time is 0(1).  Instead of a list or Binary Search Tree where searching respectively is 0(n) and 0(logn).
A WorldMap to hold a set of Territory objects and a name for the map. As well as preforming the random set up for the risk game. The set of Territories are implemented using a HashMap because of its quick look up time.
A Player class is to show ownership, with name, and color. As well as a list of territories to be able to check if a Player is still in the game. This is because a player loses when they have 0 territories, this list is implemented with a LinkedList because there will be a fair bit of adds and removes, and LinkedList are more efficient when removing. But, because there will not be many removes in a row between user input the efficiency will not be noticeable. Also in the Player class holds a Boolean active.
Game class holds a WorldMap object and a list of Player Objects. The list of Player objects is implemented using an array list as it is more efficient to traverse and there will be no deletions. But again, this efficiency will not be noticeable. The Game class also hold attack,fortify and end turn functionalities, with the game loop. 
Later we try to remove responsibilities from Game class as it has a lot.
    
### Milestones:
- [x] Milestone 1: A text-based playable version of the game, i.e., players should be able to play the game via the console using the keyboard.
    >A text-based playable version of the game, i.e., players should be able to
     play the game via the console using the keyboard. There should be a command to print
     the state of the map (i.e., which player is in which country and with how many armies), a
     command to decide which country to attack from which country, and a command to pass
     your turn to the next player. Events such as the outcome of an attack, whose turn it is to
     play, the elimination of a player, etc. should be printed to the console when applicable.
     Also required, the UML modeling of the problem domain (class diagrams with complete
     variable and method signatures, and sequence diagrams for important scenarios), detailed
     description of the choice of data structures and relevant operations: you are providing an
     initial design and implementation for the Model part of the MVC. Do not worry about
     any GUI yet.
    - Deliverables: readme file (see explanation below) + code (source + executable in
      a jar file) + UML diagrams + documentation, all in one zip file. 
    - Deadline: Friday Oct 23rd. Weight: 15% of the overall project grade.
    
- [ ] Milestone 2: A GUI-based version of the game using Java Swing components.
    > GUI-based version (now you’re adding the View and the Controller!) of the
      game. Display must be in a JFrame, and user input is via the mouse. You have freedom
      for other GUI decisions. Also required: Unit tests for the Model. 
    - Deliverables: readme file + design + corresponding tests + code + documentation,
      all in one zip file. In particular, document the changes you made to your UML
      and data structures from Milestone 1 and explain why. 
    - Deadline: Monday November 9th. Weight: 20% of the overall project grade.

- [ ] Milestone 3: Addition of army placement, troupe movement phase, etc...
    >  Additional features: bonus army placement + troupe movement phase +
      “AI” player. As per the rules of Risk, at the beginning of a player’s turn, the player
      receives reinforcement armies proportional to the number of countries held (total
      territories divided by 3, with a minimum of 3) and bonus armies for holding whole 
      continents (number depending on the size of the continent, see rules for exact numbers).
      The player must place these reinforcement armies before moving on to the attack phase.
      The troupe movement phase occurs after the attack phase; the player can decide to move
      any number of armies from one country to another “connected” country (“connected”
      meaning that there must be a path between the two countries that only goes through
      countries held by the player making the move). As for the “AI” player: any number of the
      players in the game can be assigned to be an “AI” player. The way the “AI” player plays
      is up to you. One possible way is to first come up with a function that assesses how
      “good” a given state of the board is (in terms of countries held, armies, etc.), that is called
      a utility function. In a game that involves some uncertainty due to dice rolls, one simple
      yet effective AI method is to make it choose the action that maximizes the expected
      utility. 
    - Deliverables: readme file + code + corresponding tests + refined design +
      documentation. The program must work robustly, and the code must be “smellfree” (we will be hunting for smells!). Make sure that you document the changes
      since the last iteration, and the reason for those changes.
    - Deadline: Monday November 23rd. Weight: 30% of the overall project grade
- [ ] Milestone 4: Addition of saving / loading features
    > Two more things: 1- Save/load features. You may use Java Serialization to
     achieve this. 2- Custom maps. The custom map may be defined in XML or JSON format.
     Upon loading of a custom map, the program should be able to reject invalid maps, e.g.
     maps where certain countries or groups of countries are unreachable.
    - Deliverables: readme file + code + tests + documentation. Your project should be
      well packaged, and the program(s) should be easy to install and run.
    - Deadline: Monday December 7th. Weight: 35% of the overall project grade.

> Milestones must contain all necessary files and documentation, even those items that are
  unchanged from previous milestones. Missing files cannot be submitted after the
  deadline, no exceptions. Verify that your submission contains all necessary files (in
  particular, don’t forget to include your source code!) before submitting on cuLearn.
  The “readme” file, listed as a deliverable for each iteration, is typically a short text file
  that lists and describes: the rest of the deliverables, the authors, the changes that were
  made since the previous deliverable, the known issues (known issues are graded less
  severely than undocumented ones!) and the roadmap ahead.
  “Documentation” includes up-to-date UML diagrams, detailed descriptions of design
  decisions made, complete user manuals, and javadoc documentation.

