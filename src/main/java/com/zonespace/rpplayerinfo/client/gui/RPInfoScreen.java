package com.zonespace.rpplayerinfo.client.gui;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.List;

import org.joml.Matrix4f;
import org.joml.Quaternionf;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.zonespace.rpplayerinfo.RPPlayerInfo;
import com.zonespace.rpplayerinfo.client.ClientPlayerRPData;
import com.zonespace.rpplayerinfo.data.EPlayerGender;
import com.zonespace.rpplayerinfo.data.EPlayerPermission;
import com.zonespace.rpplayerinfo.data.PlayerRPData;
import com.zonespace.rpplayerinfo.data.PlayerRPDataProvider;
import com.zonespace.rpplayerinfo.networking.ModMessages;
import com.zonespace.rpplayerinfo.networking.packet.PlayerDataSyncC2SPacket;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
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
        this.isOwner = isOwner;
        recalcImageWidth();
    }

    public void onClose() {
        saveEditBoxInputs();
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
        
       /*  descriptionCloseButton = addRenderableWidget(new Button(
            leftPos + 180,
            topPos + 174,
            34,
            20,
            Component.translatable("gui.rpplayerinfo.rp_info_screen.button.close"),
            this::onDescCloseButtonPress
        ));
        descriptionCloseButton.visible = false;*/
        descriptionCloseButton = addRenderableWidget(
            Button.builder(
                Component.translatable("gui.rpplayerinfo.rp_info_screen.button.close"), 
                this::onDescCloseButtonPress
            )
            .pos(leftPos + 180, topPos + 174)
            .size(34, 20)
            .build()
        );
        descriptionCloseButton.visible = false;

        if(this.isOwner) {
            permissionToKillButton = addRenderableWidget(
                Button.builder(
                    convertPermissionEnumToButton(ClientPlayerRPData.getPermissionToKill()),
                    this::onPermissionToKillButtonPress
                )
                .pos(leftPos + 106, topPos + 145)
                .size(34, 20)
                .build()
            );

            permissionToKillButton = addRenderableWidget(
                Button.builder(
                    convertPermissionEnumToButton(ClientPlayerRPData.getPermissionToMaim()),
                    this::onPermissionToMaimButtonPress
                )
                .pos(leftPos + 106, topPos + 120)
                .size(34, 20)
                .build()
            );

            permissionToKillButton = addRenderableWidget(
                Button.builder(
                    convertGenderEnumToButton(ClientPlayerRPData.getGender()),
                    this::onGenderButtonPress
                )
                .pos(leftPos + 50, topPos + 70)
                .size(48, 20)
                .build()
            );

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
            descriptionEditBox.setMaxLength(700);
            descriptionEditBox.setValue(ClientPlayerRPData.getDescription());
        } else {
            descriptionViewButton = addRenderableWidget(
                Button.builder(
                    Component.translatable("gui.rpplayerinfo.rp_info_screen.button.view"),
                    this::onDescOpenButtonPress
                )
                .pos(leftPos + 6, topPos + 108)
                .size(100, 20)
                .build()
            );
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
            saveEditBoxInputs();
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
            saveEditBoxInputs();
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
            saveEditBoxInputs();
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
    public void render(@SuppressWarnings("null") GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        renderBackground(graphics);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        if(isViewingDescription) {
            RenderSystem.setShaderTexture(0, DESC_TEXTURE);
            graphics.blit(DESC_TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        } else if(isOwner) {
            RenderSystem.setShaderTexture(0, MAIN_SELF_TEXTURE);
            graphics.blit(MAIN_SELF_TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        } else {
            RenderSystem.setShaderTexture(0, MAIN_OTHER_TEXTURE);
            graphics.blit(MAIN_OTHER_TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        }
         
        super.render(graphics, mouseX, mouseY, partialTicks);

        PlayerRPData rpData = targetPlayer.getCapability(PlayerRPDataProvider.PLAYER_RP_DATA).resolve().get();

        if(!isViewingDescription) {
            graphics.drawString(font, Component.literal(targetPlayer.getName().getString() + Component.translatable("gui." + RPPlayerInfo.MODID + ".rp_info_screen.string.title").getString()), this.leftPos + 8, this.topPos + 6, 0x2e2d2d, false);

            if(isOwner) {
                graphics.drawString(font, Component.translatable("gui." + RPPlayerInfo.MODID + ".rp_info_screen.string.name").getVisualOrderText(), this.leftPos + 5, this.topPos + 25, 0x404040, false);
            } else {
                graphics.drawString(font, Component.literal(rpData.getName()).withStyle(ChatFormatting.BOLD), this.leftPos + 10, this.topPos + 35, 0x404040, false);
            }
            
            if(isOwner) {
                graphics.drawString(font, Component.translatable("gui." + RPPlayerInfo.MODID + ".rp_info_screen.string.race").getVisualOrderText(), this.leftPos + 5, this.topPos + 50, 0x404040, false);
            } else {
                graphics.drawString(font, Component.literal(rpData.getRace()).withStyle(ChatFormatting.BOLD), this.leftPos + 10, this.topPos + 60, 0x404040, false);
            }

            if(isOwner) {
                graphics.drawString(font, Component.translatable("gui." + RPPlayerInfo.MODID + ".rp_info_screen.string.gender").getVisualOrderText(), this.leftPos + 5, this.topPos + 75, 0x404040, false);
            } else {
                graphics.drawString(font, Component.literal(convertGenderEnumToButton(rpData.getGender()).getString() + "  " + String.valueOf(rpData.getHeightFeet()) + "'" + String.valueOf(rpData.getHeightInches()) + "\"").withStyle(ChatFormatting.BOLD), this.leftPos + 10, this.topPos + 85, 0x404040, false);
            }
        
            if(isOwner) {
                graphics.drawString(font, Component.translatable("gui." + RPPlayerInfo.MODID + ".rp_info_screen.string.height").getVisualOrderText(), this.leftPos + 5, this.topPos + 100, 0x404040, false);
            }
            
            if(isOwner) {
                graphics.drawString(font, "'", this.leftPos + 63, this.topPos + 96, 0x404040, false);
                graphics.drawString(font, "\"", this.leftPos + 93, this.topPos + 96, 0x404040, false);
            }

            if(isOwner) {
                graphics.drawString(font, Component.translatable("gui." + RPPlayerInfo.MODID + ".rp_info_screen.string.ptm").getVisualOrderText(), this.leftPos + 5, this.topPos + 125, 0x404040, false);
                graphics.drawString(font, Component.translatable("gui." + RPPlayerInfo.MODID + ".rp_info_screen.string.ptk").getVisualOrderText(), this.leftPos + 5, this.topPos + 150, 0x404040, false);
            }
            if(!isOwner) {
                graphics.drawString(font, Component.translatable("gui." + RPPlayerInfo.MODID + ".rp_info_screen.string.ptm.short"), this.leftPos + 155, this.topPos + 95, 0x404040, false);
                graphics.drawString(font, Component.translatable("gui." + RPPlayerInfo.MODID + ".rp_info_screen.string.ptk.short"), this.leftPos + 155, this.topPos + 115, 0x404040, false); 
                graphics.drawString(font, convertPermissionEnumToButton(rpData.getPermissionToMaim()), this.leftPos + 185, this.topPos + 95, 0xbf1313, false);
                graphics.drawString(font, convertPermissionEnumToButton(rpData.getPermissionToKill()), this.leftPos + 185, this.topPos + 115, 0xbf1313, false);
            }

            if(isOwner) {
                graphics.drawString(font, Component.translatable("gui." + RPPlayerInfo.MODID + ".rp_info_screen.string.desc").getVisualOrderText(), this.leftPos + 5, this.topPos + 177, 0x404040, false);
            }
            renderPlayer(graphics, leftPos + 180, topPos + 84, 32, (float)(leftPos + 180) - mouseX, (float)(topPos + 78 - 50) - mouseY, targetPlayer);
       
        } else {
            if(isOwner) {
                graphics.drawString(font, Component.literal(ClientPlayerRPData.getName() + Component.translatable("gui.rpplayerinfo.rp_info_screen.string.desc.title").getString()), this.leftPos + 8, this.topPos + 6, 0x2e2d2d, false);
            } else {
                graphics.drawString(font, Component.literal(rpData.getName() + Component.translatable("gui.rpplayerinfo.rp_info_screen.string.desc.title").getString()), this.leftPos + 8, this.topPos + 6, 0x2e2d2d, false);
                int n = 0;
                for(FormattedText text : splitDescriptionString(rpData)) {
                    graphics.drawString(font, Component.literal(text.getString()), this.leftPos + 6, this.topPos + 24 + n, 0x404040, false);
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

    private List<FormattedText> splitDescriptionString(PlayerRPData rpData) {
        return font.getSplitter().splitLines(rpData.getDescription(), 208, Style.EMPTY);
    }

    public static void renderPlayer(GuiGraphics graphics, int p_98851_, int p_98852_, int p_98853_, float p_98854_, float p_98855_, LivingEntity p_98856_) {
        float f = (float)Math.atan((double)(p_98854_ / 40.0F));
        float f1 = (float)Math.atan((double)(p_98855_ / 40.0F));
        renderPlayerFollowsAngle(graphics, p_98851_, p_98852_, p_98853_, f, f1, p_98856_);
    }

    public static void renderPlayerFollowsAngle(GuiGraphics pGuiGraphics, int pX, int pY, int pScale, float angleXComponent, float angleYComponent, LivingEntity pEntity) {
        float f = angleXComponent;
        float f1 = angleYComponent;
        Quaternionf quaternionf = (new Quaternionf()).rotateZ((float)Math.PI);
        Quaternionf quaternionf1 = (new Quaternionf()).rotateX(f1 * 20.0F * ((float)Math.PI / 180F));
        quaternionf.mul(quaternionf1);
        float f2 = pEntity.yBodyRot;
        float f3 = pEntity.getYRot();
        float f4 = pEntity.getXRot();
        float f5 = pEntity.yHeadRotO;
        float f6 = pEntity.yHeadRot;
        pEntity.yBodyRot = 180.0F + f * 20.0F;
        pEntity.setYRot(180.0F + f * 40.0F);
        pEntity.setXRot(-f1 * 20.0F);
        pEntity.yHeadRot = pEntity.getYRot();
        pEntity.yHeadRotO = pEntity.getYRot();
        renderPlayer(pGuiGraphics, pX, pY, pScale, quaternionf, quaternionf1, pEntity);
        pEntity.yBodyRot = f2;
        pEntity.setYRot(f3);
        pEntity.setXRot(f4);
        pEntity.yHeadRotO = f5;
        pEntity.yHeadRot = f6;
   }

   @SuppressWarnings("deprecation")
    public static void renderPlayer(GuiGraphics pGuiGraphics, int pX, int pY, int pScale, Quaternionf pPose, Quaternionf pCameraOrientation, LivingEntity pEntity) {
        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate((double)pX, (double)pY, 50.0D);
        pGuiGraphics.pose().mulPoseMatrix((new Matrix4f()).scaling((float)pScale, (float)pScale, (float)(-pScale)));
        pGuiGraphics.pose().mulPose(pPose);
        Lighting.setupForEntityInInventory();
        EntityRenderDispatcher entityrenderdispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        if (pCameraOrientation != null) {
            pCameraOrientation.conjugate();
            entityrenderdispatcher.overrideCameraOrientation(pCameraOrientation);
        }

        entityrenderdispatcher.setRenderShadow(false);
        RenderSystem.runAsFancy(() -> {
            entityrenderdispatcher.render(pEntity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, pGuiGraphics.pose(), pGuiGraphics.bufferSource(), 15728880);
        });
        pGuiGraphics.flush();
        entityrenderdispatcher.setRenderShadow(true);
        pGuiGraphics.pose().popPose();
        Lighting.setupFor3DItems();
   }

    @SuppressWarnings("null")
    private void recalcImageWidth() {
        imageWidth = 220;
        imageHeight = (isOwner || isViewingDescription) ? 200 : 136;
        if(minecraft != null) {
            init(minecraft, minecraft.getWindow().getGuiScaledWidth(), minecraft.getWindow().getGuiScaledHeight());
        }
    }

    private void saveEditBoxInputs() {
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
        ModMessages.sendToServer(new PlayerDataSyncC2SPacket(ClientPlayerRPData.getPermissionToKill(), ClientPlayerRPData.getPermissionToMaim(), ClientPlayerRPData.getGender(), ClientPlayerRPData.getHeightInches(), ClientPlayerRPData.getHeightFeet(), ClientPlayerRPData.getDescription(), ClientPlayerRPData.getName(), ClientPlayerRPData.getRace()));
    }

}
