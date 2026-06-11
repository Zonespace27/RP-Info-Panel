package com.zonespace.rpplayerinfo.client.gui;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.mojang.blaze3d.platform.InputConstants;
import com.zonespace.rpplayerinfo.RPPlayerInfo;
import com.zonespace.rpplayerinfo.client.ClientPlayerRPData;
import com.zonespace.rpplayerinfo.data.EPlayerGender;
import com.zonespace.rpplayerinfo.data.EPlayerPermission;
import com.zonespace.rpplayerinfo.data.ModAttachments;
import com.zonespace.rpplayerinfo.data.PlayerRPData;
import com.zonespace.rpplayerinfo.networking.ModMessages;
import com.zonespace.rpplayerinfo.networking.packet.PlayerDataSyncC2SPacket;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;

public class RPInfoScreen extends Screen {
    private static final Component TITLE = Component.translatable("gui." + RPPlayerInfo.MODID + ".rp_info_screen");

    private static final Component PERMISSION_BUTTON_NO = Component.translatable("gui." + RPPlayerInfo.MODID + ".rp_info_screen.button.permission_button.no").withStyle(ChatFormatting.BOLD);
    private static final Component PERMISSION_BUTTON_ASK = Component.translatable("gui." + RPPlayerInfo.MODID + ".rp_info_screen.button.permission_button.ask").withStyle(ChatFormatting.BOLD);
    private static final Component PERMISSION_BUTTON_YES = Component.translatable("gui." + RPPlayerInfo.MODID + ".rp_info_screen.button.permission_button.yes").withStyle(ChatFormatting.BOLD);

    private static final Component GENDER_BUTTON_MALE = Component.translatable("gui." + RPPlayerInfo.MODID + ".rp_info_screen.button.gender_button.male").withStyle(ChatFormatting.BOLD);
    private static final Component GENDER_BUTTON_FEMALE = Component.translatable("gui." + RPPlayerInfo.MODID + ".rp_info_screen.button.gender_button.female").withStyle(ChatFormatting.BOLD);
    private static final Component GENDER_BUTTON_NEUTER = Component.translatable("gui." + RPPlayerInfo.MODID + ".rp_info_screen.button.gender_button.neuter").withStyle(ChatFormatting.BOLD);

    private static final ResourceLocation MAIN_SELF_TEXTURE = ResourceLocation.fromNamespaceAndPath(RPPlayerInfo.MODID, "textures/gui/player_menu_self.png");
    private static final ResourceLocation MAIN_OTHER_TEXTURE = ResourceLocation.fromNamespaceAndPath(RPPlayerInfo.MODID, "textures/gui/player_menu_other.png");
    private static final ResourceLocation DESC_TEXTURE = ResourceLocation.fromNamespaceAndPath(RPPlayerInfo.MODID, "textures/gui/desc_menu.png");

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

    @Override
    public void onClose() {
        saveEditBoxInputs();
        super.onClose();
    }

    @Override
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

        descriptionCloseButton = addRenderableWidget(Button.builder(
            Component.translatable("gui.rpplayerinfo.rp_info_screen.button.close"),
            this::onDescCloseButtonPress
        ).bounds(leftPos + 180, topPos + 174, 34, 20).build());
        descriptionCloseButton.visible = false;

        if(this.isOwner) {
            permissionToKillButton = addRenderableWidget(Button.builder(
                convertPermissionEnumToButton(ClientPlayerRPData.getPermissionToKill()),
                this::onPermissionToKillButtonPress
            ).bounds(leftPos + 106, topPos + 145, 34, 20).build());

            permissionToMaimButton = addRenderableWidget(Button.builder(
                convertPermissionEnumToButton(ClientPlayerRPData.getPermissionToMaim()),
                this::onPermissionToMaimButtonPress
            ).bounds(leftPos + 106, topPos + 120, 34, 20).build());

            genderButton = addRenderableWidget(Button.builder(
                convertGenderEnumToButton(ClientPlayerRPData.getGender()),
                this::onGenderButtonPress
            ).bounds(leftPos + 50, topPos + 70, 48, 20).build());


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
            descriptionViewButton = addRenderableWidget(Button.builder(
                Component.translatable("gui.rpplayerinfo.rp_info_screen.button.view"),
                this::onDescOpenButtonPress
            ).bounds(leftPos + 6, topPos + 108, 100, 20).build());
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
        ClientPlayerRPData.setPermissionToKill(convertButtonToPermissionEnum(button.getMessage()));
        saveEditBoxInputs();
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
        ClientPlayerRPData.setPermissionToMaim(convertButtonToPermissionEnum(button.getMessage()));
        saveEditBoxInputs();
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
        ClientPlayerRPData.setGender(convertButtonToGenderEnum(button.getMessage()));
        saveEditBoxInputs();
    }

    private void onDescOpenButtonPress(Button button) {
        isViewingDescription = true;
        recalcImageWidth();
        if(isOwner) {
            permissionToKillButton.visible = false;
            permissionToMaimButton.visible = false;
            genderButton.visible = false;
            feetEditBox.visible = false;
            inchesEditBox.visible = false;
            nameEditBox.visible = false;
            descriptionEditBox.visible = true;
            raceEditBox.visible = false;
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
            feetEditBox.visible = true;
            inchesEditBox.visible = true;
            nameEditBox.visible = true;
            descriptionEditBox.visible = false;
            raceEditBox.visible = true;
        } else {
            descriptionViewButton.visible = true;
        }
        descriptionCloseButton.visible = false;
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.renderBackground(graphics, mouseX, mouseY, partialTicks);

        ResourceLocation texture;
        if(isViewingDescription) {
            texture = DESC_TEXTURE;
        } else if(isOwner) {
            texture = MAIN_SELF_TEXTURE;
        } else {
            texture = MAIN_OTHER_TEXTURE;
        }

        graphics.blit(texture, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        // renders the background (including our panel texture) and all widgets
        super.render(graphics, mouseX, mouseY, partialTicks);

        PlayerRPData rpData = targetPlayer.getData(ModAttachments.PLAYER_RP_DATA);

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
                graphics.drawString(font, Component.translatable("gui." + RPPlayerInfo.MODID + ".rp_info_screen.string.ptk").getVisualOrderText(), this.leftPos + 5, this.topPos + 150, 0x404040, false); //0xFF0000
            }
            if(!isOwner) {
                graphics.drawString(font, Component.translatable("gui." + RPPlayerInfo.MODID + ".rp_info_screen.string.ptm.short"), this.leftPos + 155, this.topPos + 95, 0x404040, false);
                graphics.drawString(font, Component.translatable("gui." + RPPlayerInfo.MODID + ".rp_info_screen.string.ptk.short"), this.leftPos + 155, this.topPos + 115, 0x404040, false); //0xFF0000
                graphics.drawString(font, convertPermissionEnumToButton(rpData.getPermissionToMaim()), this.leftPos + 185, this.topPos + 95, 0xbf1313, false);
                graphics.drawString(font, convertPermissionEnumToButton(rpData.getPermissionToKill()), this.leftPos + 185, this.topPos + 115, 0xbf1313, false);
            }

            if(isOwner) {
                graphics.drawString(font, Component.translatable("gui." + RPPlayerInfo.MODID + ".rp_info_screen.string.desc").getVisualOrderText(), this.leftPos + 5, this.topPos + 177, 0x404040, false);
            }

            InventoryScreen.renderEntityInInventoryFollowsMouse(graphics, leftPos + 150, topPos + 20, leftPos + 210, topPos + 90, 32, 0.0625F, mouseX, mouseY, targetPlayer);

        } else {
            if(isOwner) {
                graphics.drawString(font, Component.literal(ClientPlayerRPData.getName() + Component.translatable("gui.rpplayerinfo.rp_info_screen.string.desc.title").getString()), this.leftPos + 8, this.topPos + 6, 0x2e2d2d, false);
            } else {
                graphics.drawString(font, Component.literal(rpData.getName() + Component.translatable("gui.rpplayerinfo.rp_info_screen.string.desc.title").getString()), this.leftPos + 8, this.topPos + 6, 0x2e2d2d, false);
                int n = 0;
                for(FormattedText text : splitDescriptionString(rpData)) {
                    graphics.drawString(font, text.getString(), this.leftPos + 6, this.topPos + 24 + n, 0x404040, false);
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
            if(!inputStr.isEmpty()) {
                ClientPlayerRPData.setHeightFeet(Integer.parseInt(feetEditBox.getValue()));
            }
        }
        if(inchesEditBox != null) {
            String inputStr = inchesEditBox.getValue();
            if(!inputStr.isEmpty()) {
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
