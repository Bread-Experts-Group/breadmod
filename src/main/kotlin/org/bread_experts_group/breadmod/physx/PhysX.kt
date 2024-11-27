package org.bread_experts_group.breadmod.physx

import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Player
import physx.PxTopLevelFunctions
import physx.common.*
import physx.geometry.PxBoxGeometry
import physx.physics.PxFilterData
import physx.physics.PxSceneDesc
import physx.physics.PxShapeFlagEnum
import physx.physics.PxShapeFlags
import physx.support.PxPvdInstrumentationFlagEnum
import physx.support.PxPvdInstrumentationFlags

@Suppress("UNUSED")
object PhysX {
    @JvmStatic
    fun runTest(version: Int, player: Player) {
        // create PhysX foundation object
        val allocator = PxDefaultAllocator()
        val errorCb = PxDefaultErrorCallback()
        val foundation = PxTopLevelFunctions.CreateFoundation(version, allocator, errorCb)

        // PhysX debugging
        val pvd = PxTopLevelFunctions.CreatePvd(foundation)
        val transport = PxTopLevelFunctions.DefaultPvdSocketTransportCreate("localhost", 5425, 10000)
        pvd.connect(transport, PxPvdInstrumentationFlags(PxPvdInstrumentationFlagEnum.eALL.value.toByte()))

        // create PhysX main physics object
        val tolerances = PxTolerancesScale()
        val physics = PxTopLevelFunctions.CreatePhysics(version, foundation, tolerances, pvd)

        // create the CPU dispatcher, can be shared among multiple scenes
        val numThreads = Runtime.getRuntime().availableProcessors()
        val cpuDispatcher = PxTopLevelFunctions.DefaultCpuDispatcherCreate(numThreads)

        // create a physics scene
        val tmpVec = PxVec3(0f, -9.81f, 0f)
        val sceneDesc = PxSceneDesc(tolerances)
        sceneDesc.gravity = tmpVec
        sceneDesc.cpuDispatcher = cpuDispatcher
        sceneDesc.filterShader = PxTopLevelFunctions.DefaultFilterShader()
        val scene = physics.createScene(sceneDesc)

        // create a default material
        val material = physics.createMaterial(0.5f, 0.5f, 0.5f)

        // create default simulation shape flags
        val shapeFlags =
            PxShapeFlags((PxShapeFlagEnum.eSCENE_QUERY_SHAPE.value or PxShapeFlagEnum.eSIMULATION_SHAPE.value).toByte())

        // create a few temporary objects used during setup
        val tmpPose = PxTransform(PxIDENTITYEnum.PxIdentity)
        val tmpFilterData = PxFilterData(1, 1, 0, 0)

        // create a large static box with size 20x1x20 as ground
        val groundGeometry = PxBoxGeometry(10f, 0.5f, 10f) // PxBoxGeometry uses half-sizes
        val groundShape = physics.createShape(groundGeometry, material, true, shapeFlags)
        val ground = physics.createRigidStatic(tmpPose)
        groundShape.simulationFilterData = tmpFilterData
        ground.attachShape(groundShape)
        scene.addActor(ground)

        // create a small dynamic box with size 1x1x1, which will fall on the ground
        tmpVec.setX(0f)
        tmpVec.setY(5f)
        tmpVec.setZ(0f)
        tmpPose.p = tmpVec
        val boxGeometry = PxBoxGeometry(0.5f, 0.5f, 0.5f) // PxBoxGeometry uses half-sizes
        val boxShape = physics.createShape(boxGeometry, material, true, shapeFlags)
        val box = physics.createRigidDynamic(tmpPose)
        boxShape.simulationFilterData = tmpFilterData
        box.attachShape(boxShape)
        scene.addActor(box)

        // clean up temp objects
        groundGeometry.destroy()
        boxGeometry.destroy()
        tmpFilterData.destroy()
        tmpPose.destroy()
        tmpVec.destroy()
        shapeFlags.destroy()
        sceneDesc.destroy()
        tolerances.destroy()

        // the box starts at a height of 5
        var boxHeight = box.globalPose.p.y
        player.sendSystemMessage(Component.literal("Box height: $boxHeight == 5"))

        // run physics simulation
        for (i in 0..500) {
            scene.simulate(1f / 60f)
            scene.fetchResults(true)

            boxHeight = box.globalPose.p.y
            if (i % 10 == 0) {
                player.sendSystemMessage(Component.literal("Step $i: h = $boxHeight"))
            }
        }


        // box should rest on the ground
        player.sendSystemMessage(Component.literal("Box height: $boxHeight == 1"))

        // cleanup stuff
        scene.removeActor(ground)
        ground.release()
        groundShape.release()

        scene.removeActor(box)
        box.release()
        boxShape.release()

        scene.release()
        material.release()
        physics.release()
        pvd.release()
        transport.release()
        foundation.release()
        errorCb.destroy()
        allocator.destroy()
    }
}