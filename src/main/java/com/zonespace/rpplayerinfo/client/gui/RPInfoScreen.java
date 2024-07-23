package com.zonespace.rpplayerinfo.client.gui;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import com.zonespace.rpplayerinfo.RPPlayerInfo;
import com.zonespace.rpplayerinfo.client.ClientPlayerRPData;
import com.zonespace.rpplayerinfo.data.EPlayerPermission;
import com.zonespace.rpplayerinfo.data.PlayerRPDataProvider;
//import com.zonespace.rpplayerinfo.networking.ModMessages;
//import com.zonespace.rpplayerinfo.networking.packet.PlayerDataSyncC2SPacket;
import com.zonespace.rpplayerinfo.networking.ModMessages;
import com.zonespace.rpplayerinfo.networking.packet.PlayerDataSyncC2SPacket;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;

public class RPInfoScreen extends Screen {
    private static final Component TITLE = Component.translatable("gui." + RPPlayerInfo.MODID + ".rp_info_screen");
    private static final Component DESC_TITLE = Component.translatable("gui." + RPPlayerInfo.MODID + ".rp_info_screen.desc_title");

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
    private EditBox descriptionEditBox;

    private Button permissionToKillButton;
    private Button permissionToMaimButton;
    private Button descriptionCloseButton;
    private Button descriptionViewEditButton;


    //@SuppressWarnings("unused")
    //private Button button;

    private boolean isOwner;

    private boolean isViewingDescription;

    public RPInfoScreen(Player targetPlayer, boolean isOwner) {
        super(TITLE);

        this.targetPlayer = targetPlayer;
        this.imageWidth = 220;
        this.imageHeight = 224;
        this.isOwner = true;
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
        if(descriptionEditBox != null) {
            ClientPlayerRPData.setDescription(descriptionEditBox.getValue());
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
        descriptionCloseButton = addRenderableWidget(new Button(
            leftPos + 136,
            topPos + 140,
            34,
            20,
            Component.translatable("gui.rpplayerinfo.rp_info_screen.button.close"),
            this::onDescCloseButtonPress
        ));
        descriptionCloseButton.visible = false;
        /*descriptionEditBox = addRenderableWidget(new EditBox(
            font, 
            this.leftPos + 18, 
            this.topPos + 20, 
            140, 
            80, 
            Component.literal(String.valueOf(ClientPlayerRPData.getName()))));
        descriptionEditBox.setVisible(false);*/
        if(this.isOwner) {
            Component permissionToKillButtonComp = convertPermissionEnumToButton(ClientPlayerRPData.getPermissionToKill());
            permissionToKillButton = addRenderableWidget(new Button(
                leftPos + 106,
                topPos + 135,
                34,
                20,
                permissionToKillButtonComp,
                this::onPermissionToKillButtonPress
            ));
            Component permissionToMaimButtonComp = convertPermissionEnumToButton(ClientPlayerRPData.getPermissionToMaim());
            permissionToMaimButton = addRenderableWidget(new Button(
                leftPos + 106,
                topPos + 110,
                34,
                20,
                permissionToMaimButtonComp,
                this::onPermissionToMaimButtonPress
            ));
            feetEditBox = addRenderableWidget(new HeightEditBox(
                font, 
                this.leftPos + 45, 
                this.topPos + 45, 
                16, 
                16, 
                Component.literal(String.valueOf(ClientPlayerRPData.getHeightFeet()))));
            feetEditBox.setMaxLength(1);
            feetEditBox.setSuggestion("5", true);
            feetEditBox.setValue(String.valueOf(ClientPlayerRPData.getHeightFeet()));
            inchesEditBox = addRenderableWidget(new HeightEditBox(
                font, 
                this.leftPos + 67, 
                this.topPos + 45, 
                24, 
                16, 
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
            /*descriptionViewEditButton = addRenderableWidget(new Button(
                leftPos + 68,
                topPos + 64,
                34,
                20,
                Component.translatable("gui.rpplayerinfo.rp_info_screen.button.edit"),
                this::onDescOpenButtonPress
            ));*/
            descriptionEditBox = addRenderableWidget(new EditBox(
                font, 
                leftPos + 68, 
                topPos + 64, 
                100, 
                20, 
                Component.literal(ClientPlayerRPData.getDescription())));
            descriptionEditBox.setValue(ClientPlayerRPData.getDescription());
            descriptionEditBox.setMaxLength(440); // this should be scaled up if the rp menu is
        } else {
            descriptionViewEditButton = addRenderableWidget(new Button(
                leftPos + 68,
                topPos + 64,
                34,
                20,
                Component.translatable("gui.rpplayerinfo.rp_info_screen.button.view"),
                this::onDescOpenButtonPress
            ));   
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

    private void onDescOpenButtonPress(Button button) {
        isViewingDescription = true;
        if(isOwner) {
            permissionToKillButton.visible = false;
            permissionToMaimButton.visible = false;
            feetEditBox.setVisible(false);
            inchesEditBox.setVisible(false);
            nameEditBox.setVisible(false);
            descriptionEditBox.setVisible(true);
        }
        descriptionCloseButton.visible = true;
        if(descriptionViewEditButton != null) {
            descriptionViewEditButton.visible = false;
        }
    }

    private void onDescCloseButtonPress(Button button) {
        isViewingDescription = false;
        if(isOwner) {
            permissionToKillButton.visible = true;
            permissionToMaimButton.visible = true;
            feetEditBox.setVisible(true);
            inchesEditBox.setVisible(true);
            nameEditBox.setVisible(true);
            descriptionEditBox.setVisible(false);
        }
        descriptionCloseButton.visible = false;
        if(descriptionViewEditButton != null) {
            descriptionViewEditButton.visible = true;
        }
    }

    @Override
    public void render(@Nonnull PoseStack graphics, int mouseX, int mouseY, float partialTicks) {
        renderBackground(graphics);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, TEXTURE);
         
        blit(graphics, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        super.render(graphics, mouseX, mouseY, partialTicks);

        if(!isViewingDescription) {
            font.draw(graphics, TITLE, this.leftPos + 8, this.topPos + 8, 0x2e2d2d);

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
            
            //font.draw(graphics, Component.literal(ClientPlayerRPData.getDescription()).withStyle(ChatFormatting.BOLD), this.leftPos + 45, this.topPos + 30, 0x404040);

            font.draw(graphics, Component.translatable("gui." + RPPlayerInfo.MODID + ".rp_info_screen.string.ptk").getVisualOrderText(), this.leftPos + 5, this.topPos + 140, 0xFF0000);
            font.draw(graphics, Component.translatable("gui." + RPPlayerInfo.MODID + ".rp_info_screen.string.ptm").getVisualOrderText(), this.leftPos + 5, this.topPos + 115, 0xFF0000);
            if(!this.isOwner) {
                font.draw(graphics, convertPermissionEnumToButton(ClientPlayerRPData.getPermissionToKill()), this.leftPos + 106, this.topPos + 140, 0xFF0000);
                font.draw(graphics, convertPermissionEnumToButton(ClientPlayerRPData.getPermissionToMaim()), this.leftPos + 106, this.topPos + 115, 0xFF0000);
            }
            renderPlayer(leftPos + 180, topPos + 78, 32, (float)(leftPos + 180) - mouseX, (float)(topPos + 78 - 50) - mouseY, targetPlayer);
        } else {
            font.draw(graphics, Component.literal(ClientPlayerRPData.getName() + Component.translatable("gui.rpplayerinfo.rp_info_screen.string.desc.title").getString()), this.leftPos + 8, this.topPos + 8, 0x2e2d2d);
            if(!isOwner) {
                int n = 0;
                for(FormattedText text : splitDescriptionString()) {
                    font.draw(graphics, Component.literal(text.getString()), this.leftPos + 6, this.topPos + 24 + n, 0x404040);
                    n += 8;
                }

                //font.draw(graphics, Component.literal(ClientPlayerRPData.getDescription()), this.leftPos + 6, this.topPos + 30, 0x404040);
            }
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

    private List<FormattedText> splitDescriptionString() {
        return font.getSplitter().splitLines(ClientPlayerRPData.getDescription(), 163, Style.EMPTY);
    }

    public static void renderPlayer(int p_98851_, int p_98852_, int p_98853_, float p_98854_, float p_98855_, LivingEntity p_98856_) {
        float f = (float)Math.atan((double)(p_98854_ / 40.0F));
        float f1 = (float)Math.atan((double)(p_98855_ / 40.0F));
        renderPlayerRaw(p_98851_, p_98852_, p_98853_, f, f1, p_98856_);
    }

    @SuppressWarnings("deprecation")
    public static void renderPlayerRaw(int p_98851_, int p_98852_, int p_98853_, float angleXComponent, float angleYComponent, LivingEntity p_98856_) {
        PoseStack posestack = RenderSystem.getModelViewStack();
        posestack.pushPose();
        posestack.translate((double)p_98851_, (double)p_98852_, 1050.0);
        posestack.scale(1.0F, 1.0F, -1.0F);
        RenderSystem.applyModelViewMatrix();
        PoseStack posestack1 = new PoseStack();
        posestack1.translate(0.0, 0.0, 1000.0);
        posestack1.scale((float)p_98853_, (float)p_98853_, (float)p_98853_);
        Quaternion quaternion = Vector3f.ZP.rotationDegrees(180.0F);
        Quaternion quaternion1 = Vector3f.XP.rotationDegrees(angleYComponent * 20.0F);
        quaternion.mul(quaternion1);
        posestack1.mulPose(quaternion);
        float f2 = p_98856_.yBodyRot;
        float f3 = p_98856_.getYRot();
        float f4 = p_98856_.getXRot();
        float f5 = p_98856_.yHeadRotO;
        float f6 = p_98856_.yHeadRot;
        p_98856_.yBodyRot = 180.0F + angleXComponent * 20.0F;
        p_98856_.setYRot(180.0F + angleXComponent * 40.0F);
        p_98856_.setXRot(-angleYComponent * 20.0F);
        p_98856_.yHeadRot = p_98856_.getYRot();
        p_98856_.yHeadRotO = p_98856_.getYRot();
        Lighting.setupForEntityInInventory();
        EntityRenderDispatcher entityrenderdispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        quaternion1.conj();
        entityrenderdispatcher.overrideCameraOrientation(quaternion1);
        entityrenderdispatcher.setRenderShadow(false);
        MultiBufferSource.BufferSource multibuffersource$buffersource = Minecraft.getInstance().renderBuffers().bufferSource();
        RenderSystem.runAsFancy(() -> {
            entityrenderdispatcher.render(p_98856_, 0.0, 0.0, 0.0, 0.0F, 1.0F, posestack1, multibuffersource$buffersource, 15728880);
        });
        multibuffersource$buffersource.endBatch();
        entityrenderdispatcher.setRenderShadow(true);
        p_98856_.yBodyRot = f2;
        p_98856_.setYRot(f3);
        p_98856_.setXRot(f4);
        p_98856_.yHeadRotO = f5;
        p_98856_.yHeadRot = f6;
        posestack.popPose();
        RenderSystem.applyModelViewMatrix();
        Lighting.setupFor3DItems();
    }


   /* @Override
    public boolean isPauseScreen() {
        return false;
    }*/
}
