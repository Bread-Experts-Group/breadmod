# The Bread Mod (ultimate bread edition)
## Priority todos
  - [ ] <s>Port mod to 1.21</s> (postponed until further notice)
    - remove mixin for dye able armor layers and items and replace with built-in methods
  - [X] Set up secondary mod for Bread Mod Advanced
  - [ ] Document functions and code to make it more understandable
## Standard, Base Mod
- [X] Bread Armor
  - [X] Takes damage every few seconds - water speeds up this process and lava completely destroys whatever it touches
  - [X] Each piece will keep your hunger from going below a certain point (+1 cumulative)
  - [X] Doped bread/armor set
    - [ ] Reinforced bread armor, doesn't dissolve in water. stats comparable to diamond armor, high enchantability
  - [ ] Bread Armor Doping
    - [X] Any piece of the set can be crafted with a potion to change its color and grant that effect to you while it's equipped
      - [X] Effect(s) are only applied with amplification I, further amplification increases the range your effect gets applied to fellow players
      - [ ] Make doped armor effects only target players and non-hostile entities
- [ ] Bread Tools
  - Slightly weaker than stone in durability and strength but has approx 1.5x more speed
  - [X] Pickaxe
  - [X] Shovel
  - [X] Hoe
  - [X] Axe
  - [X] Sword
  - [X] Reinforced Variants of tools
  - [X] Crafting/Smithing recipes for tools
  - [ ] Balance Tool stats against wood/stone, diamond/netherite for reinforced
    - Similar to netherite or diamond, faster than diamond but once again weaker in durability and strength
- [X] Bread Block
  - [X] Mixin for turning burning blocks into another block
  - [X] Edible
  - [X] Custom charcoal block as a result of bread block being burned
  - [ ] Portal block that leads into the bread dimension
- [ ] Heating Element
  - [ ] make it work properly
  - [ ] integrate into heating system
  - [ ] actually make the heating system work (pneumaticcraft compat?)
- [ ] Bread Dimension
  - [X] A dimension composed of bread blocks
  - [X] Nonflammable
  - [ ] Items inside the dimension will vanish from the players inventory when they leave, and come back when they reenter
- [ ] Bread (Dough) Machine for turning flour into dough
  - [ ] If the machine is broken during its operation, flour will be spread everywhere 
  - [X] Flour/Dough
    - [X] Snow-like layer block (Very flammable)
      - Figure out model and loot table datagen for this block (SnowLayerBlock), might have to just roll with manually written json files to start off with
    - [X] Refinement processes
    - [X] Textures
    - [X] Data driven recipe and serializer
    - [ ] Create compat.
      - [ ] Mixing recipe for flour to dough (might already work with the item tags)
    - [ ] Compat with fluid tank-like containers in the bucket slot
  - [ ] Can be heated from the bottom (possible PneumaticCraft Compat?)
    - [ ] Speed can be accelerated from external heat sources
- [X] Wheat Crusher
  - [X] Block model and textures (animated)
  - [X] Menu actually working
  - [X] Recipe type and serializer
  - [X] jei category and recipe
  - [X] gui size fix
### Needs to be done still:
  - [X] Redirect recipes with custom json names to the breadmod folder instead of the minecraft folder
    - [X] Sort recipe types into their own folders (ex. mixing, smithing, block compaction and decompaction)
  - [ ] Textures for tools, weapons, items, blocks
  - [ ] Fix mixin refmaps not generating during mod build
  - [X] Recipe datagen
  - [X] Lang datagen
  - [X] Model datagen
  - [X] Crafting recipes for reinforced bread block (now smithing recipe)
  - [ ] Happy Block Nuclear Inferno
- [X] Bread slices
  - [X] Crafted from using a sword on bread in a crafting table (the sword uses durability)
    - [X] FIX RECIPE TO NOT CONSUME THE SWORD, ONLY TAKE DURABILITY (IMPORTANT)
  - [X] You get 8 bread slices per bread in the recipe
  - [X] Crafted using a cutting board from farmer's delight
- [ ] Recipe Fixes with other mods
  - [ ] Dough compat with create mod dough
  - [ ] Charcoal block compat with mekanism charcoal block
- [ ] Pipes and cables that transport items, fluids, and power (important)
  - [ ] Blockstates or similar connecting system for the model to connect to each-other
- [ ] Fix up base mod generator (not the diesel one)
- [ ] Decoration blocks
  - Pending ideas.

### Tool gun (totally not from gmod)
  - [ ] Recoil and rapid coil spin animation when tool gun is used
    - [ ] timer-like system for the animations
  - [ ] Improve gui overlay, add mouse and key icons
  - [ ] turn IToolGunMode into an api
  - [ ] Tool gun modes
    - fix up and polish existing modes
    - tool gun displaying mode information and image accompanying said mode
# BUGS
  - [ ] items inputted into machines from their sides are voided out of existence
  - [ ] (minor) tool gun anim speeds up when you have multiple tool guns at once
  - [ ] energy capability does not respect energy limit in container
  - [ ] AbstractMachineMenu#getScaledProgress always returns 0
  - [ ] Machine not clearing progress and blockstate upon recipe completion (progress resets when the output item is taken)
  - [ ] (?) capability invalidation occasionally crashes the game (possibly fixed?)
  - [ ] tool gun stacking up/absorbing clicks when not equipped
  - [ ] happy block explosion not actually exploding an area
  - [ ] (Production specific) Tool gun action not triggering
  - [ ] BMExplosion causing crash
---
- [X] Joke item: "the ultimate bread" just gives you creative mode lmao
- [X] "Bread amulet" gives you 1 hunger point every 10 seconds
  - Curios Compat.
- [ ] "Godlike loaf" a monstrous combination of rare materials and a nether star to give you 10 full minutes of positive potion effects and a whole 10 absorption hearts
- [X] "Farmhouse structure" a random house with a farm attached to it with a random amount of bread or wheat in loot chests
- [X] "Bread tools and weapons" tools and weapons have stats comparable to stone tools, can be upgraded to their Reinforced variant for a much stronger / longer lasting item\
---
## Advanced, Machines and stuff
- [ ] Diesel Generator
  - [ ] Custom rendering (BER) / fluid rendering inside of model
  - [ ] Particles
  - [ ] logic
  - [ ] upgrades
    -  [ ] Turbo
    -  [ ] Internal power buffer
    -  [ ] charger
- [ ] Bread Screen
  - WIP
## External Mods to look into (will be their own projects)
- [ ] Lighting mod (colored lighting)
- [ ] "Intrusive" mods (such as, being able to heat up a furnace like w/ the bread machine)
### Other mod compatibility (items crossed off in this list will be removed from the mod when I get around to it)
- [ ] Add recipe integrations with other mods where possible
- [ ] <s>Farmers Delight</s>
- [ ] <s>ProjectE</s>
  - [ ] Add EMC to items
  - [X] Potential Bread-like EMC holder
    - [X] Make texture for item
- [ ] <s>Create</s>
  - [ ] Create recipe generators for the other recipe types
- [ ] Mekanism
  - [ ] Crushing recipes for the food-like items
- [ ] JEI Integration
  - [ ] Recipe Categories
    - Needs generators added to categories
  - [X] Custom recipe support
  - [ ] Item infos
  - [X] Recipe auto-fill support
