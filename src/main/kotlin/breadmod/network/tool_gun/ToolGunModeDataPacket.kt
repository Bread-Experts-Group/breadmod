package breadmod.network.tool_gun

import breadmod.datagen.tool_gun.ModToolGunModeDataLoader
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceLocation
import net.minecraftforge.network.NetworkEvent
import java.util.function.Supplier

class ToolGunModeDataPacket(val location: ResourceLocation, val data: ByteArray, val done: Boolean) {
    companion object {
        fun encodeBuf(input: ToolGunModeDataPacket, buffer: FriendlyByteBuf) {
            buffer.writeResourceLocation(input.location).writeByteArray(input.data).writeBoolean(input.done) }
        fun decodeBuf(input: FriendlyByteBuf): ToolGunModeDataPacket =
            ToolGunModeDataPacket(input.readResourceLocation(), input.readByteArray(), input.readBoolean())

        fun handle(input: ToolGunModeDataPacket, ctx: Supplier<NetworkEvent.Context>) = ctx.get().let {
            it.enqueueWork {
                if(it.sender == null) {
                    toLoad[input.location] = gson.fromJson(input.data.decodeToString(), JsonObject::class.java)
                    if(input.done) {
                        ModToolGunModeDataLoader.load(toLoad)
                        ModToolGunModeDataLoader.loadKeys()
                    }
                }
            }
            it.packetHandled = true
        }

        private val gson = Gson()
        private val toLoad: MutableMap<ResourceLocation, JsonElement> = mutableMapOf()
    }
}