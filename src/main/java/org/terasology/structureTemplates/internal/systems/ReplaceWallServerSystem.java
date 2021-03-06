/*
 * Copyright 2016 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.structureTemplates.internal.systems;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.math.Region3i;
import org.terasology.math.geom.Vector3i;
import org.terasology.registry.In;
import org.terasology.structureTemplates.components.WallPreviewComponent;
import org.terasology.structureTemplates.internal.components.ReplaceWallItemComponent;
import org.terasology.structureTemplates.internal.events.ReplaceBlocksRequest;
import org.terasology.world.WorldProvider;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockManager;
import org.terasology.world.block.entity.placement.PlaceBlocks;

import java.util.HashMap;
import java.util.Map;

/**
 * Server system for the item with the component {@link ReplaceWallItemComponent}.
 */
@RegisterSystem(RegisterMode.AUTHORITY)
public class ReplaceWallServerSystem extends BaseComponentSystem {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReplaceWallServerSystem.class);

    @In
    private BlockManager blockManager;

    @In
    private WorldProvider worldProvider;

    @ReceiveEvent
    public void onReplaceBlocksRequest(ReplaceBlocksRequest event, EntityRef item,
                                       ReplaceWallItemComponent component, WallPreviewComponent previewComponent) {
        if (previewComponent.wallRegions == null) {
            LOGGER.info("Can't replace blocks as requested as the wallRegions field was null");
            return;
        }
        Block block = blockManager.getBlock(component.blockUri);
        Map<Vector3i, Block> map = new HashMap<>();
        for (Region3i region: previewComponent.wallRegions) {
            for (Vector3i v:region) {
                map.put(new Vector3i(v), block);
            }
        }
        PlaceBlocks placeBlocks = new PlaceBlocks(map, item.getOwner());
        worldProvider.getWorldEntity().send(placeBlocks);
    }

}
