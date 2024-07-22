package com.zonespace.rpplayerinfo.client.gui;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.zonespace.rpplayerinfo.RPPlayerInfo;
import com.zonespace.rpplayerinfo.data.EPlayerPermission;
import com.zonespace.rpplayerinfo.data.PlayerRPDataProvider;
//import com.zonespace.rpplayerinfo.networking.ModMessages;
//import com.zonespace.rpplayerinfo.networking.packet.PlayerDataSyncC2SPacket;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;

public class RPInfoScreen extends Screen {
    private static final Component TITLE = Component.translatable("gui." + RPPlayerInfo.MODID + ".rp_info_screen");

    private static final Component PERMISSION_BUTTON_NO = Component.translatable("gui." + RPPlayerInfo.MODID + ".rp_info_screen.button.permission_button.no");
    private static final Component PERMISSION_BUTTON_ASK = Component.translatable("gui." + RPPlayerInfo.MODID + ".rp_info_screen.button.permission_button.ask");
    private static final Component PERMISSION_BUTTON_YES = Component.translatable("gui." + RPPlayerInfo.MODID + ".rp_info_screen.button.permission_button.yes");

    private static final ResourceLocation TEXTURE = new ResourceLocation(RPPlayerInfo.MODID, "textures/gui/player_menu.png");

    private final int imageWidth;
    private final int imageHeight;

    private Player targetPlayer;
    private int leftPos;
    private int topPos;

    @SuppressWarnings("unused")
    private Button button;

    public RPInfoScreen(Player targetPlayer) {
        super(TITLE);

        this.targetPlayer = targetPlayer;
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    @SuppressWarnings("null")
    protected void init() {
        super.init();
        
        if(minecraft == null) {
            return;
        }

        Level level = minecraft.level;
        if(level == null) {
            return;
        }

        leftPos = (this.width - imageWidth) / 2;
        topPos = (this.height - imageHeight) / 2;

        targetPlayer.getCapability(PlayerRPDataProvider.PLAYER_RP_DATA).ifPresent(rpData -> {
            Component permissionToKillButton = convertPermissionEnumToButton(rpData.getPermissionToKill());
            button = addRenderableWidget(new Button(
                leftPos + 106,
                topPos + 135,
                34,
                20,
                permissionToKillButton,
                this::onPermissionToKillButtonPress
            ));
            Component permissionToMaimButton = convertPermissionEnumToButton(rpData.getPermissionToMaim());
            button = addRenderableWidget(new Button(
                leftPos + 106,
                topPos + 110,
                34,
                20,
                permissionToMaimButton,
                this::onPermissionToMaimButtonPress
            ));
        });
    }

    private void onPermissionToKillButtonPress(Button button) {
        // I could do this in a smarter way but I cannot be assed atm
        Component buttonMessageComponent = button.getMessage();
        if(buttonMessageComponent == PERMISSION_BUTTON_NO) {
            button.setMessage(PERMISSION_BUTTON_ASK);
        } else if(buttonMessageComponent == PERMISSION_BUTTON_ASK) {
            button.setMessage(PERMISSION_BUTTON_YES);
        } else if(buttonMessageComponent == PERMISSION_BUTTON_YES) {
            button.setMessage(PERMISSION_BUTTON_NO);
        }
        targetPlayer.getCapability(PlayerRPDataProvider.PLAYER_RP_DATA).ifPresent(rpData -> {
            rpData.setPermissionToKill(convertButtonToPermissionEnum(button.getMessage()));
            //ModMessages.sendToServer(new PlayerDataSyncC2SPacket(rpData.getPermissionToKill(), rpData.getPermissionToMaim(), rpData.getHeightInches(), rpData.getHeightFeet(), rpData.getDescription()));
        });
    }

    private void onPermissionToMaimButtonPress(Button button) {
        // I could do this in a smarter way but I cannot be assed atm
        Component buttonMessageComponent = button.getMessage();
        if(buttonMessageComponent == PERMISSION_BUTTON_NO) {
            button.setMessage(PERMISSION_BUTTON_ASK);
        } else if(buttonMessageComponent == PERMISSION_BUTTON_ASK) {
            button.setMessage(PERMISSION_BUTTON_YES);
        } else if(buttonMessageComponent == PERMISSION_BUTTON_YES) {
            button.setMessage(PERMISSION_BUTTON_NO);
        }
        targetPlayer.getCapability(PlayerRPDataProvider.PLAYER_RP_DATA).ifPresent(rpData -> {
            rpData.setPermissionToMaim(convertButtonToPermissionEnum(button.getMessage()));
            //ModMessages.sendToServer(new PlayerDataSyncC2SPacket(rpData.getPermissionToKill(), rpData.getPermissionToMaim(), rpData.getHeightInches(), rpData.getHeightFeet(), rpData.getDescription()));
        });
    }

    @Override
    public void render(@Nonnull PoseStack graphics, int mouseX, int mouseY, float partialTicks) {
        renderBackground(graphics);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, TEXTURE);
         
        blit(graphics, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        super.render(graphics, mouseX, mouseY, partialTicks);
        font.draw(graphics, TITLE, this.leftPos + 8, this.topPos + 8, 0x404040);
        font.draw(graphics, Component.translatable("gui." + RPPlayerInfo.MODID + ".rp_info_screen.string.name").getVisualOrderText(), this.leftPos + 5, this.topPos + 30, 0x404040);
        font.draw(graphics, Component.translatable("gui." + RPPlayerInfo.MODID + ".rp_info_screen.string.height").getVisualOrderText(), this.leftPos + 5, this.topPos + 50, 0x404040);
        font.draw(graphics, Component.translatable("gui." + RPPlayerInfo.MODID + ".rp_info_screen.string.desc").getVisualOrderText(), this.leftPos + 5, this.topPos + 70, 0x404040);
        font.draw(graphics, Component.translatable("gui." + RPPlayerInfo.MODID + ".rp_info_screen.string.ptk").getVisualOrderText(), this.leftPos + 5, this.topPos + 140, 0xFF0000);
        font.draw(graphics, Component.translatable("gui." + RPPlayerInfo.MODID + ".rp_info_screen.string.ptm").getVisualOrderText(), this.leftPos + 5, this.topPos + 115, 0xFF0000);
    }

    public static Component convertPermissionEnumToButton(EPlayerPermission permissionEnum) {
        switch(permissionEnum) {
            case PERMISSION_YES:
                return PERMISSION_BUTTON_YES;
            case PERMISSION_ASK:
                return PERMISSION_BUTTON_ASK;
            case PERMISSION_NO:
                return PERMISSION_BUTTON_NO;
        }
        return null;
    }

    public static EPlayerPermission convertButtonToPermissionEnum(Component buttonComponent) {
        if(buttonComponent == PERMISSION_BUTTON_YES) {
            return EPlayerPermission.PERMISSION_YES;
        } else if(buttonComponent == PERMISSION_BUTTON_ASK) {
            return EPlayerPermission.PERMISSION_ASK;
        } else {
            return EPlayerPermission.PERMISSION_NO;
        }
    }

   /* @Override
    public boolean isPauseScreen() {
        return false;
    }*/
}
