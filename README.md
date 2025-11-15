# MtS Modding Anniversary 8: Spire Quests
## Preamble
A group project for the eighth anniversary of Mod the Spire. INSERT DESC HERE  
For a full write-up, please see the [Design Doc](https://docs.google.com/document/d/1lMwZQwiQLaizmrpsV3VjMuTTTZaKhzQGk356Cn7uRnE/edit?tab=t.0)  
For a list of contributions, take a look at the [Contributions List](https://docs.google.com/spreadsheets/d/1Vg56thYTilz6elyO7A8KjkEcVMb_n_tnlYp2hvNPCrQ/edit?gid=0#gid=0)
  
## Contributions
Either modargo, Mindbomber or I (erasels) will be reviewing your pull request and suggesting changes to code and/or design and balance if needed to keep the project cohesive. Be aware that even after code is merged, maintainers may need to make bug fixes and balance changes as we get feedback (we'll do our best to consult contributors for balance changes). Collaborating with others to make a contribution is fine.
  
### Technical Guidelines

  
Images unique to your quest should be saved in `anniv8Resources/images/[quest]/`.  
Localization is saved in `anniv8Resources/localization/[langKey]/[quest]/`.  
  
To test your contribution, you can use the following console commands:
* `addquest {questid}`: adds the quest to your quest log (ex: `addquest TestQuest`)
* `spawnquest {slot} {questid}`: adds the quest to the specified slot (0-2) on the quest board if your current room has one (ex: `spawnquest 0 TestQuest`)

**Please make sure to add your interactable to the [Contributions List](https://docs.google.com/spreadsheets/d/1Vg56thYTilz6elyO7A8KjkEcVMb_n_tnlYp2hvNPCrQ/edit?gid=0#gid=0) before your PR. If it's an idea you want to code yourself, you can add it there even without having started coding it.**

### Contribution guidelines
To make suitable content and help the PR process go smoothly, follow these guidelines:
- We expect contributions to be complete (including art) before merging, but it's okay to make a PR while still working on the art

#### Cards, relics, powers, etc.
Cards, relics, powers, patches, and everything else should go in the package you created for your quest.

There are abstract classes that you should extend in the abstracts package: `AbstractSQCard`, `AbstractSQRelic`, and `AbstractSQPower`.
  
### How to make PRs  
To make a contribution, you must have a GitHub account. 
For the specifics of how to fork this repo and then make a pull request, please look at this guide:  
https://docs.github.com/en/get-started/quickstart/contributing-to-projects  
   
I recommend using the GitHub desktop client for this if you have no experience with Github  
https://desktop.github.com/
