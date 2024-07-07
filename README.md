# Dancerizer

This mod originally was made for the [ModFest: Carnival](https://modfest.net/carnival), make sure to check it out!

***

### About

With this mod, players can perform taunts and whole dances with new accessories.

### Making Custom Dances or Taunts

Because the animations and items triggering the animations are **all data driven**, custom dances and taunts are possible without actually writing any code by following those steps:

1. In order to create a new animation, first download the **[Player.bbmodel](./Player.bbmodel)** file and open it with [Blockbench](https://www.blockbench.net/). Inside the program, switch to the `Animations` tab _(upper right corner)_ and crate a new animation there.

2. If the animation is done, export it over `Animation -> Export Animations...` to a JSON file. The JSON file needs to be loaded over a Resource Pack. An example can be viewed in the mods [resources folder](./src/client/resources).
    > Note: Non-Linear animations currently do not work within this mod, see [Known Missing Features](#known-missing-features). For now, use the [Bakery plugin](https://www.blockbench.net/plugins/bakery) and export the animations after converting the keyframes with that.

3. For the item triggering the animation, any item can be used for that. It only needs to contain the apropiate Component _(`dancerizer:taunt` or `dancerizer:dance`)_. It will behave just like the other taunt or dance items.

### Known Missing Features

Because the used animation format is quite complex and comes in many shapes, not all features have yet been implemented. Here is a list of all known missing features:

- Scale and particle keyframes
- Keyframe `pre`, `post` and `lerp_mode` settings
- Particles in animations
- Molang expression support

***

#### License

<sup>This mod is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, version 3 of the License only.</br></br>This mod is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the [GNU Lesser General Public License](/COPYING.md) for more details.</br></br>You should have received a copy of the GNU Lesser General Public License along with this mod. If not, see http://www.gnu.org/licenses/.</sup>
