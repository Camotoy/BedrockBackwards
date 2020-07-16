package com.github.doctormacc.common.protocols.forwards;

import com.github.doctormacc.common.PlayerSession;
import com.github.doctormacc.common.protocols.BasePacketHandler;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.data.inventory.ContainerId;
import com.nukkitx.protocol.bedrock.data.inventory.ItemData;
import com.nukkitx.protocol.bedrock.packet.CraftingDataPacket;
import com.nukkitx.protocol.bedrock.packet.CreativeContentPacket;
import com.nukkitx.protocol.bedrock.packet.InventoryContentPacket;
import com.nukkitx.protocol.bedrock.packet.StartGamePacket;

public class v390_to_v407_ForwardsPacketHandler extends ForwardsPacketHandler {

    @Override
    public boolean translate(PlayerSession session, BedrockPacket packet, boolean upstream, int translatorIndex) {

        if (StartGamePacket.class.equals(packet.getClass())) {
            ((StartGamePacket) packet).setInventoriesServerAuthoritative(false);
        }

        else if (InventoryContentPacket.class.equals(packet.getClass())) {
            if (((InventoryContentPacket) packet).getContainerId() == ContainerId.CREATIVE) {
                CreativeContentPacket contentPacket = new CreativeContentPacket();
                int i = 1;
                for (ItemData content : ((InventoryContentPacket) packet).getContents()) {
                    contentPacket.getEntries().put(i, content);
                    i++;
                }
                BasePacketHandler.translatePacket(session, contentPacket, upstream, translatorIndex);
                return false;
            }
        }

        return true;
    }
}
