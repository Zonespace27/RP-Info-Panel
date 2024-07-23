package com.zonespace.rpplayerinfo.client.gui;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.List;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import com.zonespace.rpplayerinfo.RPPlayerInfo;
import com.zonespace.rpplayerinfo.client.ClientPlayerRPData;
import com.zonespace.rpplayerinfo.data.EPlayerGender;
import com.zonespace.rpplayerinfo.data.EPlayerPermission;
import com.zonespace.rpplayerinfo.data.PlayerRPDataProvider;
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

    private static final Component PERMISSION_BUTTON_NO = Component.translatable("gui." + RPPlayerInfo.MODID + ".rp_info_screen.button.permission_button.no").withStyle(ChatFormatting.BOLD);
    private static final Component PERMISSION_BUTTON_ASK = Component.translatable("gui." + RPPlayerInfo.MODID + ".rp_info_screen.button.permission_button.ask").withStyle(ChatFormatting.BOLD);
    private static final Component PERMISSION_BUTTON_YES = Component.translatable("gui." + RPPlayerInfo.MODID + ".rp_info_screen.button.permission_button.yes").withStyle(ChatFormatting.BOLD);

    private static final Component GENDER_BUTTON_MALE = Component.translatable("gui." + RPPlayerInfo.MODID + ".rp_info_screen.button.gender_button.male").withStyle(ChatFormatting.BOLD);
    private static final Component GENDER_BUTTON_FEMALE = Component.translatable("gui." + RPPlayerInfo.MODID + ".rp_info_screen.button.gender_button.female").withStyle(ChatFormatting.BOLD);
    private static final Component GENDER_BUTTON_NEUTER = Component.translatable("gui." + RPPlayerInfo.MODID + ".rp_info_screen.button.gender_button.neuter").withStyle(ChatFormatting.BOLD);

    private static final ResourceLocation MAIN_SELF_TEXTURE = new ResourceLocation(RPPlayerInfo.MODID, "textures/gui/player_menu_self.png");
    private static final ResourceLocation MAIN_OTHER_TEXTURE = new ResourceLocation(RPPlayerInfo.MODID, "textures/gui/player_menu_other.png");
    private static final ResourceLocation DESC_TEXTURE = new ResourceLocation(RPPlayerInfo.MODID, "textures/gui/desc_menu.png");

    private int imageWidth;
    private int imageHeight;

    private Player targetPlayer;
    private int leftPos;
    private int topPos;

    private HeightEditBox feetEditBox;
    private HeightEditBox inchesEditBox;
    private EditBox nameEditBox;
    private EditBox descriptionEditBox;
    private EditBox raceEditBox;

    private Button permissionToKillButton;
    private Button permissionToMaimButton;
    private Button descriptionCloseButton;
    private Button descriptionViewButton;
    private Button genderButton;

    private boolean isOwner;

    private boolean isViewingDescription;

    public RPInfoScreen(Player targetPlayer, boolean isOwner) {
        super(TITLE);

        this.targetPlayer = targetPlayer;
        this.isOwner = true;
        recalcImageWidth();
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
        if(raceEditBox != null) {
            ClientPlayerRPData.setRace(raceEditBox.getValue());
        }
        super.onClose();
    }

    @SuppressWarnings("null")
    public boolean keyPressed(int p_97765_, int p_97766_, int p_97767_) {
        InputConstants.Key mouseKey = InputConstants.getKey(p_97765_, p_97766_);
        if (this.minecraft.options.keyInventory.isActiveAndMatches(mouseKey)) {
            if(nameEditBox.isFocused() || descriptionEditBox.isFocused() || raceEditBox.isFocused()) {
                return super.keyPressed(p_97765_, p_97766_, p_97767_);
            }
            this.onClose();
            return true;
        } else {
            return super.keyPressed(p_97765_, p_97766_, p_97767_);
        }
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
        
        descriptionCloseButton = addRenderableWidget(new Button(
            leftPos + 180,
            topPos + 199,
            34,
            20,
            Component.translatable("gui.rpplayerinfo.rp_info_screen.button.close"),
            this::onDescCloseButtonPress
        ));
        descriptionCloseButton.visible = false;

        if(this.isOwner) {
            permissionToKillButton = addRenderableWidget(new Button(
                leftPos + 106,
                topPos + 145,
                34,
                20,
                convertPermissionEnumToButton(ClientPlayerRPData.getPermissionToKill()),
                this::onPermissionToKillButtonPress
            ));

            permissionToMaimButton = addRenderableWidget(new Button(
                leftPos + 106,
                topPos + 120,
                34,
                20,
                convertPermissionEnumToButton(ClientPlayerRPData.getPermissionToMaim()),
                this::onPermissionToMaimButtonPress
            ));

            genderButton = addRenderableWidget(new Button(
                leftPos + 50,
                topPos + 70,
                48,
                20,
                convertGenderEnumToButton(ClientPlayerRPData.getGender()),
                this::onGenderButtonPress
            ));


            nameEditBox = addRenderableWidget(new EditBox(
                font, 
                this.leftPos + 38, 
                this.topPos + 20, 
                110, 
                20, 
                Component.literal(String.valueOf(ClientPlayerRPData.getName()))));
            nameEditBox.setValue(String.valueOf(ClientPlayerRPData.getName()));

            raceEditBox = addRenderableWidget(new EditBox(
                font, 
                this.leftPos + 38, 
                this.topPos + 45, 
                80, 
                20, 
                Component.literal(ClientPlayerRPData.getRace())));
            raceEditBox.setValue(ClientPlayerRPData.getRace());

            feetEditBox = addRenderableWidget(new HeightEditBox(
                font, 
                this.leftPos + 45, 
                this.topPos + 95, 
                16, 
                16, 
                Component.literal(String.valueOf(ClientPlayerRPData.getHeightFeet()))));
            feetEditBox.setMaxLength(1);
            feetEditBox.setSuggestion("5", true);
            feetEditBox.setValue(String.valueOf(ClientPlayerRPData.getHeightFeet()));

            inchesEditBox = addRenderableWidget(new HeightEditBox(
                font, 
                this.leftPos + 67, 
                this.topPos + 95, 
                24, 
                16, 
                Component.literal(String.valueOf(ClientPlayerRPData.getHeightInches()))));
            inchesEditBox.setMaxLength(2);
            inchesEditBox.setSuggestion("9", true);
            inchesEditBox.setValue(String.valueOf(ClientPlayerRPData.getHeightInches()));  

            descriptionEditBox = addRenderableWidget(new EditBox(
                font, 
                leftPos + 68, 
                topPos + 171, 
                100, 
                20, 
                Component.literal(ClientPlayerRPData.getDescription())));
            descriptionEditBox.setMaxLength(800);
            descriptionEditBox.setValue(ClientPlayerRPData.getDescription());
        } else {
            descriptionViewButton = addRenderableWidget(new Button(
                leftPos + 6,
                topPos + 108,
                100,
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
            ClientPlayerRPData.setPermissionToKill(convertButtonToPermissionEnum(button.getMessage()));
            ModMessages.sendToServer(new PlayerDataSyncC2SPacket(ClientPlayerRPData.getPermissionToKill(), ClientPlayerRPData.getPermissionToMaim(), ClientPlayerRPData.getGender(), ClientPlayerRPData.getHeightInches(), ClientPlayerRPData.getHeightFeet(), ClientPlayerRPData.getDescription(), ClientPlayerRPData.getName(), ClientPlayerRPData.getRace()));
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
            ModMessages.sendToServer(new PlayerDataSyncC2SPacket(ClientPlayerRPData.getPermissionToKill(), ClientPlayerRPData.getPermissionToMaim(), ClientPlayerRPData.getGender(), ClientPlayerRPData.getHeightInches(), ClientPlayerRPData.getHeightFeet(), ClientPlayerRPData.getDescription(), ClientPlayerRPData.getName(), ClientPlayerRPData.getRace()));
        });
    }

    private void onGenderButtonPress(Button button) {
        Component buttonMessageComponent = button.getMessage();
        if(buttonMessageComponent == GENDER_BUTTON_MALE) {
            button.setMessage(GENDER_BUTTON_FEMALE);
        } else if(buttonMessageComponent == GENDER_BUTTON_FEMALE) {
            button.setMessage(GENDER_BUTTON_NEUTER);
        } else if(buttonMessageComponent == GENDER_BUTTON_NEUTER) {
            button.setMessage(GENDER_BUTTON_MALE);
        }
        targetPlayer.getCapability(PlayerRPDataProvider.PLAYER_RP_DATA).ifPresent(rpData -> {
            ClientPlayerRPData.setGender(convertButtonToGenderEnum(button.getMessage()));
            ModMessages.sendToServer(new PlayerDataSyncC2SPacket(ClientPlayerRPData.getPermissionToKill(), ClientPlayerRPData.getPermissionToMaim(), ClientPlayerRPData.getGender(), ClientPlayerRPData.getHeightInches(), ClientPlayerRPData.getHeightFeet(), ClientPlayerRPData.getDescription(), ClientPlayerRPData.getName(), ClientPlayerRPData.getRace()));
        });
    }

    private void onDescOpenButtonPress(Button button) {
        isViewingDescription = true;
        recalcImageWidth();
        if(isOwner) {
            permissionToKillButton.visible = false;
            permissionToMaimButton.visible = false;
            genderButton.visible = false;
            feetEditBox.setVisible(false);
            inchesEditBox.setVisible(false);
            nameEditBox.setVisible(false);
            descriptionEditBox.setVisible(true);
            raceEditBox.setVisible(false);
        } else {
            descriptionViewButton.visible = false;
        }
        descriptionCloseButton.visible = true;
    }

    private void onDescCloseButtonPress(Button button) {
        isViewingDescription = false;
        recalcImageWidth();
        if(isOwner) {
            permissionToKillButton.visible = true;
            permissionToMaimButton.visible = true;
            genderButton.visible = true;
            feetEditBox.setVisible(true);
            inchesEditBox.setVisible(true);
            nameEditBox.setVisible(true);
            descriptionEditBox.setVisible(false);
            raceEditBox.setVisible(true);
        } else {
            descriptionViewButton.visible = true;
        }
        descriptionCloseButton.visible = false;
    }

    @Override
    public void render(@Nonnull PoseStack graphics, int mouseX, int mouseY, float partialTicks) {
        renderBackground(graphics);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        if(isViewingDescription) {
            RenderSystem.setShaderTexture(0, DESC_TEXTURE);
        } else if(isOwner) {
            RenderSystem.setShaderTexture(0, MAIN_SELF_TEXTURE);
        } else {
            RenderSystem.setShaderTexture(0, MAIN_OTHER_TEXTURE);
        }
         
        blit(graphics, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        super.render(graphics, mouseX, mouseY, partialTicks);

        if(!isViewingDescription) {
            font.draw(graphics, Component.literal(targetPlayer.getName().getString() + Component.translatable("gui." + RPPlayerInfo.MODID + ".rp_info_screen.string.title").getString()), this.leftPos + 8, this.topPos + 6, 0x2e2d2d);

            if(isOwner) {
                font.draw(graphics, Component.translatable("gui." + RPPlayerInfo.MODID + ".rp_info_screen.string.name").getVisualOrderText(), this.leftPos + 5, this.topPos + 25, 0x404040);
            } else {
                font.draw(graphics, Component.literal(ClientPlayerRPData.getName()).withStyle(ChatFormatting.BOLD), this.leftPos + 10, this.topPos + 25, 0x404040);
            }
            
            if(isOwner) {
                font.draw(graphics, Component.translatable("gui." + RPPlayerInfo.MODID + ".rp_info_screen.string.race").getVisualOrderText(), this.leftPos + 5, this.topPos + 50, 0x404040);
            } else {
                font.draw(graphics, Component.literal(ClientPlayerRPData.getRace()).withStyle(ChatFormatting.BOLD), this.leftPos + 10, this.topPos + 50, 0x404040);
            }

            if(isOwner) {
                font.draw(graphics, Component.translatable("gui." + RPPlayerInfo.MODID + ".rp_info_screen.string.gender").getVisualOrderText(), this.leftPos + 5, this.topPos + 75, 0x404040);
            } else {
                font.draw(graphics, Component.literal(convertGenderEnumToButton(ClientPlayerRPData.getGender()).getString() + "  " + String.valueOf(ClientPlayerRPData.getHeightFeet()) + "'" + String.valueOf(ClientPlayerRPData.getHeightInches()) + "\"").withStyle(ChatFormatting.BOLD), this.leftPos + 10, this.topPos + 75, 0x404040);
            }
        
            if(isOwner) {
                font.draw(graphics, Component.translatable("gui." + RPPlayerInfo.MODID + ".rp_info_screen.string.height").getVisualOrderText(), this.leftPos + 5, this.topPos + 100, 0x404040);
            }
            
            if(isOwner) {
                font.draw(graphics, "'", this.leftPos + 63, this.topPos + 96, 0x404040);
                font.draw(graphics, "\"", this.leftPos + 93, this.topPos + 96, 0x404040);
            }

            if(isOwner) {
                font.draw(graphics, Component.translatable("gui." + RPPlayerInfo.MODID + ".rp_info_screen.string.ptm").getVisualOrderText(), this.leftPos + 5, this.topPos + 125, 0x404040);
                font.draw(graphics, Component.translatable("gui." + RPPlayerInfo.MODID + ".rp_info_screen.string.ptk").getVisualOrderText(), this.leftPos + 5, this.topPos + 150, 0x404040); //0xFF0000
            }
            if(!isOwner) {
                font.draw(graphics, Component.translatable("gui." + RPPlayerInfo.MODID + ".rp_info_screen.string.ptm.short"), this.leftPos + 155, this.topPos + 90, 0x404040);
                font.draw(graphics, Component.translatable("gui." + RPPlayerInfo.MODID + ".rp_info_screen.string.ptk.short"), this.leftPos + 155, this.topPos + 115, 0x404040); //0xFF0000
                font.draw(graphics, convertPermissionEnumToButton(ClientPlayerRPData.getPermissionToMaim()), this.leftPos + 185, this.topPos + 90, 0xbf1313);
                font.draw(graphics, convertPermissionEnumToButton(ClientPlayerRPData.getPermissionToKill()), this.leftPos + 185, this.topPos + 115, 0xbf1313);
            }

            if(isOwner) {
                font.draw(graphics, Component.translatable("gui." + RPPlayerInfo.MODID + ".rp_info_screen.string.desc").getVisualOrderText(), this.leftPos + 5, this.topPos + 177, 0x404040);
            }
            renderPlayer(leftPos + 180, topPos + 78, 32, (float)(leftPos + 180) - mouseX, (float)(topPos + 78 - 50) - mouseY, targetPlayer);
       
        } else {
            font.draw(graphics, Component.literal(ClientPlayerRPData.getName() + Component.translatable("gui.rpplayerinfo.rp_info_screen.string.desc.title").getString()), this.leftPos + 8, this.topPos + 6, 0x2e2d2d);
            if(!isOwner) {
                int n = 0;
                for(FormattedText text : splitDescriptionString()) {
                    font.draw(graphics, Component.literal(text.getString()), this.leftPos + 6, this.topPos + 24 + n, 0x404040);
                    n += 8;
                }
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
            default:
                return PERMISSION_BUTTON_NO;
        }
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

    public static Component convertGenderEnumToButton(EPlayerGender genderEnum) {
        switch(genderEnum) {
            case GENDER_MALE:
                return GENDER_BUTTON_MALE;
            case GENDER_FEMALE:
                return GENDER_BUTTON_FEMALE;
            case GENDER_NEUTER:
            default:
                return GENDER_BUTTON_NEUTER;
        }
    }

    public static EPlayerGender convertButtonToGenderEnum(Component buttonComponent) {
        if(buttonComponent == GENDER_BUTTON_MALE) {
            return EPlayerGender.GENDER_MALE;
        } else if(buttonComponent == GENDER_BUTTON_FEMALE) {
            return EPlayerGender.GENDER_FEMALE;
        } else {
            return EPlayerGender.GENDER_NEUTER;
        }
    }

    private List<FormattedText> splitDescriptionString() {
        return font.getSplitter().splitLines(ClientPlayerRPData.getDescription(), 208, Style.EMPTY);
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


    private void recalcImageWidth() {
        if(isViewingDescription) {
            imageHeight = 225;
            imageWidth = 220;
            return;
        }
        if(isOwner) {
            this.imageWidth = 220;
            this.imageHeight = 200;
        } else {
            this.imageWidth = 220;
            this.imageHeight = 136;
        }
    }

   /* @Override
    public boolean isPauseScreen() {
        return false;
    }*/
}
