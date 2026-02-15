package catgirlroutes.events.impl

import net.minecraftforge.fml.common.eventhandler.Event
import net.minecraft.client.renderer.culling.ICamera
import net.minecraft.entity.Entity
import net.minecraftforge.fml.common.eventhandler.Cancelable

@Cancelable
data class RenderEntityEvent(
    var entity: Entity,
    var camera: ICamera,
    var camX: Double,
    var camY: Double,
    var camZ: Double,
): Event()