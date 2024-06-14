# Dancerizer

This mod originally was made for the [ModFest: Carnival](https://modfest.net/carnival), make sure to check it out!

***

### About

With this mod, players can perform taunts and whole dances with new accessories.

### Making custom dances / taunts

Because the animations and items triggering the animations are **all data driven**, custom dances and taunts are possible without actually writing any code.

In order to create a new animation, first download the **[Player.bbmodel](./Player.bbmodel)** file and open it with [Blockbench](https://www.blockbench.net/). Inside the program, switch to the `Animations` tab _(upper right corner)_ and crate a new animation there.

If the animation is done, export it over `Animation -> Export Animations...` to a JSON file. The JSON file needs to be loaded over a Resource Pack. An example can be viewed in the mods [resources folder](./src/client/resources).

For the item triggering the animation, ... _(WIP)_

***

#### License

<sup>This mod is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, version 3 of the License only.</br></br>This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the [GNU Lesser General Public License](/COPYING.md) for more details.</sup>