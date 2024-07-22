package com.zonespace.rpplayerinfo.client.gui;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.zonespace.rpplayerinfo.RPPlayerInfo;
import com.zonespace.rpplayerinfo.client.ClientPlayerRPData;
import com.zonespace.rpplayerinfo.data.EPlayerPermission;
import com.zonespace.rpplayerinfo.data.PlayerRPDataProvider;
//import com.zonespace.rpplayerinfo.networking.ModMessages;
//import com.zonespace.rpplayerinfo.networking.packet.PlayerDataSyncC2SPacket;
import com.zonespace.rpplayerinfo.networking.ModMessages;
import com.zonespace.rpplayerinfo.networking.packet.PlayerDataSyncC2SPacket;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;

public class RPInfoScreen extends Screen {
    private static final Component TITLE = Component.translatable("gui." + RPPlayerInfo.MODID + ".rp_info_screen");

    private static final Component PERMISSION_BUTTON_NO = Component.translatable("gui." + RPPlayerInfo.MODID + ".rp_info_screen.button.permission_button.no").withStyle(ChatFormatting.BOLD);
    private static final Component PERMISSION_BUTTON_ASK = Component.translatable("gui." + RPPlayerInfo.MODID + ".rp_info_screen.button.permission_button.ask").withStyle(ChatFormatting.BOLD);
    private static final Component PERMISSION_BUTTON_YES = Component.translatable("gui." + RPPlayerInfo.MODID + ".rp_info_screen.button.permission_button.yes").withStyle(ChatFormatting.BOLD);

    private static final ResourceLocation TEXTURE = new ResourceLocation(RPPlayerInfo.MODID, "textures/gui/player_menu.png");

    private final int imageWidth;
    private final int imageHeight;

    private Player targetPlayer;
    private int leftPos;
    private int topPos;

    private HeightEditBox feetEditBox;
    private HeightEditBox inchesEditBox;
    private EditBox nameEditBox;

    //@SuppressWarnings("unused")
    //private Button button;

    private boolean isOwner;

    private boolean isViewingDescription;

    public RPInfoScreen(Player targetPlayer, boolean isOwner) {
        super(TITLE);

        this.targetPlayer = targetPlayer;
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.isOwner = false;
    }

    public void onClose() {
        if(feetEditBox != null) {
            String inputStr = feetEditBox.getValue();
            if(inputStr != "") {
                ClientPlayerRPData.setHeightFeet(Integer.parseInt(feetEditBox.getValue()));
            }
        }
        if(inchesEditBox != null) {
            String inputStr = inchesEditBox.getValue();
            if(inputStr != "") {
                ClientPlayerRPData.setHeightInches(Integer.parseInt(inchesEditBox.getValue()));
            }      
        }
        if(nameEditBox != null) {
            ClientPlayerRPData.setName(nameEditBox.getValue());
        }
        super.onClose();
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

        /*targetPlayer.getCapability(PlayerRPDataProvider.PLAYER_RP_DATA).ifPresent(rpData -> {
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
        });*/
        if(this.isOwner) {
            Component permissionToKillButton = convertPermissionEnumToButton(ClientPlayerRPData.getPermissionToKill());
            addRenderableWidget(new Button(
                leftPos + 106,
                topPos + 135,
                34,
                20,
                permissionToKillButton,
                this::onPermissionToKillButtonPress
            ));
            Component permissionToMaimButton = convertPermissionEnumToButton(ClientPlayerRPData.getPermissionToMaim());
            addRenderableWidget(new Button(
                leftPos + 106,
                topPos + 110,
                34,
                20,
                permissionToMaimButton,
                this::onPermissionToMaimButtonPress
            ));
            feetEditBox = addRenderableWidget(new HeightEditBox(
                font, 
                this.leftPos + 45, 
                this.topPos + 45, 
                16, 
                20, 
                Component.literal(String.valueOf(ClientPlayerRPData.getHeightFeet()))));
            feetEditBox.setMaxLength(1);
            feetEditBox.setSuggestion("5", true);
            feetEditBox.setValue(String.valueOf(ClientPlayerRPData.getHeightFeet()));
            inchesEditBox = addRenderableWidget(new HeightEditBox(
                font, 
                this.leftPos + 67, 
                this.topPos + 45, 
                24, 
                20, 
                Component.literal(String.valueOf(ClientPlayerRPData.getHeightInches()))));
            inchesEditBox.setMaxLength(2);
            inchesEditBox.setSuggestion("9", true);
            inchesEditBox.setValue(String.valueOf(ClientPlayerRPData.getHeightInches()));
            nameEditBox = addRenderableWidget(new EditBox(
                font, 
                this.leftPos + 38, 
                this.topPos + 20, 
                120, 
                20, 
                Component.literal(String.valueOf(ClientPlayerRPData.getName()))));
            nameEditBox.setValue(String.valueOf(ClientPlayerRPData.getName()));            
        }
        
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
            //rpData.setPermissionToKill(convertButtonToPermissionEnum(button.getMessage()));
            ClientPlayerRPData.setPermissionToKill(convertButtonToPermissionEnum(button.getMessage()));
            //ModMessages.sendToServer(new PlayerDataSyncC2SPacket(rpData.getPermissionToKill(), rpData.getPermissionToMaim(), rpData.getHeightInches(), rpData.getHeightFeet(), rpData.getDescription()));
            ModMessages.sendToServer(new PlayerDataSyncC2SPacket(ClientPlayerRPData.getPermissionToKill(), ClientPlayerRPData.getPermissionToMaim(), ClientPlayerRPData.getHeightInches(), ClientPlayerRPData.getHeightFeet(), ClientPlayerRPData.getDescription(), ClientPlayerRPData.getName()));
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
            ClientPlayerRPData.setPermissionToMaim(convertButtonToPermissionEnum(button.getMessage()));
            //ModMessages.sendToServer(new PlayerDataSyncC2SPacket(rpData.getPermissionToKill(), rpData.getPermissionToMaim(), rpData.getHeightInches(), rpData.getHeightFeet(), rpData.getDescription()));
            ModMessages.sendToServer(new PlayerDataSyncC2SPacket(ClientPlayerRPData.getPermissionToKill(), ClientPlayerRPData.getPermissionToMaim(), ClientPlayerRPData.getHeightInches(), ClientPlayerRPData.getHeightFeet(), ClientPlayerRPData.getDescription(), ClientPlayerRPData.getName()));
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

        font.draw(graphics, Component.translatable("gui." + RPPlayerInfo.MODID + ".rp_info_screen.string.name").getVisualOrderText(), this.leftPos + 5, this.topPos + 25, 0x404040);
        if(!this.isOwner) {
            font.draw(graphics, Component.literal(ClientPlayerRPData.getName()).withStyle(ChatFormatting.BOLD), this.leftPos + 40, this.topPos + 25, 0x404040);
        }

        font.draw(graphics, Component.translatable("gui." + RPPlayerInfo.MODID + ".rp_info_screen.string.height").getVisualOrderText(), this.leftPos + 5, this.topPos + 50, 0x404040);
        if(!this.isOwner) {
            font.draw(graphics, Component.literal(String.valueOf(ClientPlayerRPData.getHeightFeet()) + "'" + String.valueOf(ClientPlayerRPData.getHeightInches()) + "\"").withStyle(ChatFormatting.BOLD), this.leftPos + 45, this.topPos + 50, 0x404040);
        } else {
            font.draw(graphics, "'", this.leftPos + 63, this.topPos + 46, 0x404040);
            font.draw(graphics, "\"", this.leftPos + 93, this.topPos + 46, 0x404040);
        }
        
        font.draw(graphics, Component.translatable("gui." + RPPlayerInfo.MODID + ".rp_info_screen.string.desc").getVisualOrderText(), this.leftPos + 5, this.topPos + 70, 0x404040);
        // implement desc viewing later
        
        //font.draw(graphics, Component.literal(ClientPlayerRPData.getDescription()).withStyle(ChatFormatting.BOLD), this.leftPos + 45, this.topPos + 30, 0x404040);

        font.draw(graphics, Component.translatable("gui." + RPPlayerInfo.MODID + ".rp_info_screen.string.ptk").getVisualOrderText(), this.leftPos + 5, this.topPos + 140, 0xFF0000);
        font.draw(graphics, Component.translatable("gui." + RPPlayerInfo.MODID + ".rp_info_screen.string.ptm").getVisualOrderText(), this.leftPos + 5, this.topPos + 115, 0xFF0000);
        if(!this.isOwner) {
            font.draw(graphics, convertPermissionEnumToButton(ClientPlayerRPData.getPermissionToKill()), this.leftPos + 106, this.topPos + 140, 0xFF0000);
            font.draw(graphics, convertPermissionEnumToButton(ClientPlayerRPData.getPermissionToMaim()), this.leftPos + 106, this.topPos + 115, 0xFF0000);
        }
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
