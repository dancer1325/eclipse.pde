PDE/Target Definitions
======================

Contents
--------

*   [1 Introduction](#Introduction)
*   [2 The Basics](#The-Basics)
*   [3 Examples](#Examples)
*   [4 Known Issues](#Known-Issues)
    *   [4.1 Redownloading of Bundles](#Redownloading-of-Bundles)
*   [5 Links](#Links)

Introduction
------------

* *Target Platform*
  * == critical part | developing -- via -- PDE
  * == what your workspace will be built (== plug-ins) + launched against
  * supports
    * p2 targets
      * allows you,
        * create a target / grab bundles -- from -- remote update sites & repositories
        * add them | your target
  * if you use m2e extension -> you can ALSO use Maven dependencies | your target platform
  * | develop one, VERY complex 

The Basics
----------

* **target platform**
  * == your CURRENTLY ACTIVE bundles
* **Target Definition**
  * == way of determining the plug-ins / add | state
  * ALLOWED, have MULTIPLE target definitions
  * 1! definition -- can be selected as the -- target platform
* **Target Platform Preference Page**
  * manage target platform + your target definitions
    * _Example:_ create & edit Target definitions -- via the -- **Target Definition Content Wizard** 
  * lists ALL target definitions / PDE has access to
  * displays which target definition -- is being used as -- your CURRENT target platform
* ways to create & edit targets
  * | Target Platform Preference Page
  * XML files / ".target" extension
    * 👀recommended one 👀
  * | target definition editor, to edit it
  * | **New Target Definition Wizard**,to create it 

* see [how to use the editors, wizards and preference pages | PDE](https://www.eclipse.org/documentation/)

Examples
--------

* TODO: Where to find them?
* Default definition for Eclipse platform developers
* Downloading a premade target definition file
* Pointing at an install or a folder (the old way)
* Pointing to a simple site, where using default include options works
* Complex site-based target, where using default include options causes error

Known Issues
------------

### Redownloading of Bundles

* TODO:
Each Eclipse Workspace has its own **cache** (aka _bundle pool_) of the target bundles. However on every new workspace the target bundles will be downloaded again.

Future p2 version will consider additional artifact repositories. Once that it is enabled, the bundle pool can be added for the current running Eclipse IDE as well as the PDE target bundle pool for all known “recently used” workspaces. The net effect is that you pick features from one metadata repo and the content, if already local, is just copied. No downloading. Of course, new content is still downloaded as needed.

We are looking at exposing some preferences to allow additional artifact repositories to be listed. Makes sense, just need to put a UI on it.

Sidenote: People have suggested that PDE manage just one artifact repository/bundle pool for all target definitions for all workspaces. This would save disk space for sure but introduces some additional complexity in managing concurrent repo access as well as garbage collection. It would be great but for now, this is the next best thing.
