# MtS Modding Anniversary 8: Spire Quests
## Preamble
A group project for the eighth anniversary of Mod the Spire. This mod supplies the player with Quests that they can pick at the start of the run and in shops.  
For a full write-up, please see the [Design Doc](https://docs.google.com/document/d/1lMwZQwiQLaizmrpsV3VjMuTTTZaKhzQGk356Cn7uRnE/edit?tab=t.0)  
For a list of contributions, take a look at the [Contributions List](https://docs.google.com/spreadsheets/d/1Vg56thYTilz6elyO7A8KjkEcVMb_n_tnlYp2hvNPCrQ/edit?gid=0#gid=0)
  
## Contributions
⚠️ **Due to how we're planning to use [squash merges](https://docs.github.com/en/pull-requests/collaborating-with-pull-requests/incorporating-changes-from-a-pull-request/about-pull-request-merges#squash-and-merge-your-commits) this time around, we recommend making branches in your own repository instead of using your `main` branch for PRs.** ⚠️

Either modargo, Mindbomber or I (erasels) will be reviewing your pull request and suggesting changes to code and/or design and balance if needed to keep the project cohesive. Be aware that even after code is merged, maintainers may need to make bug fixes and balance changes as we get feedback (we'll do our best to consult contributors for balance changes). Collaborating with others to make a contribution is fine.
  
### Technical Guidelines
Make a directory with your username under the `quests` directory; this is where all your code will go -- all the quests you make, their supporting relics/cards/potions/etc., any patches specific to your quests, and any utilities (that are not generally applicable) or other logic your quests need.

To make a quest, make a class that extends `AbstractQuest` (take a look over it to understand what methods it defines). Quests interact with the game using _triggers_, which define when your quest should update its status (because the player made or lost progress) or do something else to interact with the game. There are a number of built-in triggers, such as entering a room, changing your deck, playing cards, etc. You can see all implemented triggers in `patches/QuestTriggers.java`. Feel free to implement new triggers if they are needed.    

Your quest also needs to set its reward by calling `addReward`. There are several reward types defined in `QuestReward` that should cover most quests (look through that class for what's available).

Relics/cards/monsters/powers you make should extend the appropriate class in the `abstracts` folder.

Images unique to your quests should be saved in `anniv8Resources/images/[username]/`.  
Localization is saved in `anniv8Resources/localization/[langKey]/[username]/`.  
  
To test your contribution, you can use the following console commands:
* `addquest {questid}`: adds the quest to your quest log (ex: `addquest TestQuest`)
* `spawnquest {slot} {questid}`: adds the quest to the specified slot (0-2) on the quest board if your current room has one (ex: `spawnquest 0 TestQuest`)
* You can also instantly remove a quest by right-clicking it in debug mode

**Please make sure to add your quest to the [Contributions List](https://docs.google.com/spreadsheets/d/1Vg56thYTilz6elyO7A8KjkEcVMb_n_tnlYp2hvNPCrQ/edit?gid=0#gid=0) before your PR. If it's an idea you want to code yourself, you can add it there even without having started coding it.**

### Contribution guidelines
To make suitable content and help the PR process go smoothly, follow these guidelines:
* Quests should be reasonable to accomplish in a normal run. Requiring the player to make different decisions than usual or take risks is fine; requiring unusual luck (like finding specific cards) or things only some characters can do is not.
* Quests should be a small addition to the run. Being a consideration throughout the run is fine, but quests shouldn’t dominate runs. (See the Challenge Quest guidelines in the [design doc](https://docs.google.com/document/d/1lMwZQwiQLaizmrpsV3VjMuTTTZaKhzQGk356Cn7uRnE/edit?tab=t.0) for an exception to this).
* Quests should give small rewards. The quest system will make the game easier, so even small rewards will be significant and attractive. (See below for more on rewards, including some exceptions to this.)
* Quests should be about Slay the Spire gameplay. Having visuals associated with a quest or quest reward is fine, but rewards should not be purely cosmetic.
* For categorizing quests, here are guidelines (but don’t worry too much since it’s easy to adjust this based on playtesting):
     * A short quest should typically take less than an act to complete. Anything else (any quest that typically takes an act or more) is a long quest.
     * An easy quest can be achieved with only minor strategy adjustment or a small risk. A hard quest requires significant strategy adjustments or a big risk. Normal quests are in between.
* There's room for a range of quest complexities, from very simple to moderately complex. However:
     * It’s better to try to keep quests simpler. Too many complex quests creates information overload and risks having the quests dominate the run.
     * People wanting to make complex quests should consider Challenge Quests (see the [design doc](https://docs.google.com/document/d/1lMwZQwiQLaizmrpsV3VjMuTTTZaKhzQGk356Cn7uRnE/edit?tab=t.0)).
* We expect contributions to be complete (including art) before merging, but it's okay to make a PR while still working on the art

#### Cards, relics, powers, etc.
Cards, relics, powers, patches, and everything else should go in the package you created for your quest.

There are abstract classes that you should extend in the abstracts package: `AbstractSQCard`, `AbstractSQRelic`, and `AbstractSQPower`.
  
### How to make PRs  
To make a contribution, you must have a GitHub account. 
For the specifics of how to fork this repo and then make a pull request, please look at this guide:  
https://docs.github.com/en/get-started/quickstart/contributing-to-projects  
   
I recommend using the GitHub desktop client for this if you have no experience with Github  
https://desktop.github.com/
