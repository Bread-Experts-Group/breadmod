package org.bread_experts_group.breadmod.util.handlers.exp

// todo so we need an item handler capable of containing IItemHandlers in each "side" of the block,
//  but they all need to be able to access the same slot index, basically "exposing" that slot to that specific side only.
//  Then there's the situation where we want more than one slot being accessible on a side, how do we tell those slots
//  are able to input or output? Then there's the problem of reassigning slots to sides, how do you change that dynamically?
class SidedItemHandler