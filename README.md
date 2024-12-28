# SkyParticles [![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0) [![Discord](https://img.shields.io/discord/418432278113550337.svg?logo=discord&logoWidth=18&colorB=7289DA)](https://discordapp.com/invite/eHBxk5q)

SkyParticles is a highly customizable plugin that allows users to create custom particle displays inside bounded
regions.
The plugin is lightweight and spawns particles only for users who have toggled their displays on! It comes with an
automatic config checker that will disable any misconfigured particle displays (for when users try to make changes
directly to the config rather than the in-game creator) to prevent any errors from appearing in the console. It'll
even warn the user when a setup is misconfigured, and automatically disable it as well!
---

### Plugin Setup

1. To setup the plugin, first stop the server, install the jar and boot up your server. The plugin is ready to go!
2. To create your region, begin by selecting the two bounding corners where the particles should be active. This is done
   using /sp pos1 and /sp pos2. The plugin will automatically check if both corners are selected, and will ensure
   they're valid locations.
3. To create your particle display, use the command /sp add <location name> <particle> <distance> [count] [speed]. The
   location name, particle, and distance are required while count and speed are optional.
    1. If you use WorldGuard regions and already have a cuboid area set up, you can use an alternate command to create a
       particle display within that region's bounds. Use the command /sp
       worldguard <region name> <particle> <distance> [count] [speed]. The region name, particle, and distance are
       required while count and speed are optional.

---

### Variable Description

- **location name**: The name that is stored for this display in the plugin internally. Can be any string value.
    - **region name**: The WorldGuard region name that you want to generate a particle display in. **Note, you must be
      in the same world as the region you are trying to modify!**
- **particle**: The particle type to set the display as. You can use the tab feature to automatically see all valid
  particles. If you try to use one that is not on the list, it will disable the particle location and tell you to fix
  it. Must be a valid Enum particle.
- **distance**: The distance decides how far away the player can see the particles. **SkyParticles works by spawning
  particles around the player**. This allows you to set, for example, a relatively short distance of 16. Because the
  particle spawns around the player, as long as they are _inside the region_ they will see particles **16 blocks out in
  every direction** (see images below)
- **count**: The count is how many particles to spawn each time. If left blank, the plugin can automatically determine
  what the best count would be depending on the distance declared above. It uses the equation `10*(distance/5)^2` to
  create an optimal number of particles for the given distance. Specifying an extremely high count for a small view
  distance may cause client side lag (see images below)
- **speed**: The speed is how slow or fast the particles should be moving around the player. If left blank, the default
  value is 0.5, but it can be made slower or faster later on in the setup process.

---

### Plugin Commands

`<required> [optional]`

* `/sp help` permission: skyparticles.help
* `/sp pos1` permission: skyparticles.admin.add
* `/sp pos2` permission: skyparticles.admin.add
* `/sp add <location name> <particle> <distance> [count] [speed]` permission: skyparticles.admin.add
* `/sp worldguard <region name> <particle> <distance> [count] [speed]` permission: skyparticles.admin.add
* `/sp remove <location name>` permission: skyparticles.admin.remove
* `/sp set <location name> <variable> <new value>` permission: skyparticles.admin.set
    * `/sp set <location name> bounds`
    * `/sp set <location name> count <new particle count>`
    * `/sp set <location name> distance <new distance>`
    * `/sp set <location name> name <new location name>`
    * `/sp set <location name> particle <new particle type>`
    * `/sp set <location name> speed <new particle speed>`
* `/sp toggle` permission: skyparticles.player.toggle
* `/sp enable <location name>` permission: skyparticles.admin.enabledisable
* `/sp disable <location name>` permission: skyparticles.admin.enabledisable
* `/sp reload` permission: skyparticles.admin.reload

---

![image 1](https://i.imgur.com/OrlqjFf.png)
_This can be generated with_ `/sp add lobby END_ROD 16 100 .05`

![image 2](https://i.imgur.com/JtqrfVI.png)
_This shows the distance variable. This is done with a distance of 2, allowing the particles to spawn around the player
in a 2 block radius. This is how the plugin ensures it is lag-friendly; the server doesn't receive the particles, only
the player does!_

---

### Contact Us

If you have a problem please create a ticket and include the error (if there was one). Feel free to join the Discord
Server linked above! I'm super active there and tend to respond faster on it.

---

### Donation Link

If you appreciate our plugins and support, consider donating and showin' us some love <3

[![ko-fi](https://www.ko-fi.com/img/githubbutton_sm.svg)](https://ko-fi.com/O4O425D12)

---