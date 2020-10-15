# **Dream Team Risk Project (SYSC 3110 - Software Design Project)**

### Team Members:
* Tony Abou-Zeidan
* Anthony Dooley
* Ethan Chase
* Kyler Verge

### Project Description:
>The goal of this team project is to reproduce a simplified version of the classic strategy game RISK.
> Risk project!


**Project Progress**

![5%](https://progress-bar.dev/5)

planning phase

### Milestones:
- [ ] ####Milestone 1: A text-based playable version of the game, i.e., players should be able to play the game via the console using the keyboard.
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
    
- [ ] ####Milestone 2: A GUI-based version of the game using Java Swing components.
    > GUI-based version (now you’re adding the View and the Controller!) of the
      game. Display must be in a JFrame, and user input is via the mouse. You have freedom
      for other GUI decisions. Also required: Unit tests for the Model. 
    - Deliverables: readme file + design + corresponding tests + code + documentation,
      all in one zip file. In particular, document the changes you made to your UML
      and data structures from Milestone 1 and explain why. 
    - Deadline: Monday November 9th. Weight: 20% of the overall project grade.

- [ ] ####Milestone 3: Addition of army placement, troupe movement phase, etc...
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
- [ ] ####Milestone 4: Addition of saving / loading features
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

