# Space Survivor


Space Survivor is a simple android game where the goal is to survive as long as possible while evading alien spaceships.

The game is developed in Kotlin using the KorGE (https://korge.org/) as game engine and embedded into an android app.
 


# Features

- Leaderboard listing the global Top 100 players
- Signed in player's (via Google Authentication) scores get saved to a Firebase Realtime-Database
- Players can track their score history on the statistics page
- Statistics page has zoom out functionality
- Specific data points in statistics page can be clicked on to display information about it
- App notifies user he needs to log in if he is not logged in and tries to view his statistics
- Alternative navigation methods: NavDrawer, menu items dynamically adjust depending which fragment the user currently sees and depending on if the user is logged in or not 
- Custom transition animations between fragments
- Custom animation for revealing and hiding toolbar
- Custom created backgrounds for main menu, statistics page, and leaderboard 

**Game specific features:**
- Infinitely repeating background
- Healthbar which follows player and displays current health
- Collision with enemies causes player to slow down and plays a damage sound
- Timer which displays game time 
- Current waver and amount of enemies in this wave gets displayed
- Spaceship of player looks more and more damaged as players health is decreased
- Supports portrait and landscape mode
- Different enemy types: standard (moves straight to it's goal) and hunters (move to it's goal and try to catch player)
- Enemy waves get automatically generated
- Different stages as the game progresses: 
	1. **First stage:** Standard enemies
	2. **Second stage:** 
			- Standard enemies and hunter enemies
			- New music stars to signal the player the next stage started
	3. **Third stage:**  
			- Standard enemies and hunter enemies
			- New music stars to signal the player the next stage started
			- enemies have increased speed 


# Credits Game Assets (Incomplete)


			------------------------------	
	
	Void - Main Ship (1.0)

	Commissioned from: Baldur (https://twitter.com/the__baldur)
	Distributed by Foozle (www.foozle.io)

			------------------------------

	License: (Creative Commons Zero, CC0)
	http://creativecommons.org/publicdomain/zero/1.0/

	This content is free to use and modify for all projects, including commercial projects.
	Attribution not required.  If you would like to support, consider a voluntary donation.

			------------------------------

	Donate:   https://foozlecc.itch.io/
	Patreon:  https://www.patreon.com/bePatron?u=48464594

	Follow on YouTube and Twitter for updates:
	https://www.youtube.com/c/FoozleCC
	http://twitter.com/FoozleCC
