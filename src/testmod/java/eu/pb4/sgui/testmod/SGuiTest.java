package eu.pb4.sgui.testmod;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.context.CommandContext;
import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.elements.*;
import eu.pb4.sgui.api.gui.AnvilInputGui;
import eu.pb4.sgui.api.gui.BookGui;
import eu.pb4.sgui.api.gui.SelectionGui;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.block.Block;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;

import java.util.List;
import java.util.UUID;

import static net.minecraft.server.command.CommandManager.literal;

public class SGuiTest implements ModInitializer {
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(
                literal("test").executes(SGuiTest::test)
            );
            dispatcher.register(
                literal("test2").executes(SGuiTest::test2)
            );
            dispatcher.register(
                    literal("test3").executes(SGuiTest::test3)
            );
            dispatcher.register(
                    literal("test4").executes(SGuiTest::test4)
            );
            dispatcher.register(
                    literal("test5").executes(SGuiTest::test5)
            );
            dispatcher.register(
                    literal("test6").executes(SGuiTest::test6)
            );
        });
    }


    private static int test(CommandContext<ServerCommandSource> objectCommandContext) {
        try {
            ServerPlayerEntity player = objectCommandContext.getSource().getPlayer();
            SimpleGui gui = new SimpleGui(ScreenHandlerType.GENERIC_3X3, player, false) {
                @Override
                public boolean onClick(int index, ClickType type, SlotActionType action, GuiElementInterface element) {
                    this.player.sendMessage(new LiteralText(type.toString()), false);

                    return super.onClick(index, type, action, element);
                }
            };

            gui.setTitle(new LiteralText("Nice"));
            gui.setSlot(0, new GuiElementBuilder(Items.ARROW).setCount(100));
            gui.setSlot(1, new AnimatedGuiElement(new ItemStack[]{
                    new GuiElementBuilder(Items.NETHERITE_PICKAXE).glow().asStack(),
                    new GuiElementBuilder(Items.DIAMOND_PICKAXE).setDamage(100).asStack(),
                    Items.GOLDEN_PICKAXE.getDefaultStack(),
                    Items.IRON_PICKAXE.getDefaultStack(),
                    Items.STONE_PICKAXE.getDefaultStack(),
                    Items.WOODEN_PICKAXE.getDefaultStack()
            }, 10, false, (x, y, z) -> {}));

            gui.setSlot(2, new AnimatedGuiElementBuilder()
                    .setItem(Items.NETHERITE_AXE).saveItemStack()
                    .setItem(Items.DIAMOND_AXE).saveItemStack()
                    .setItem(Items.GOLDEN_AXE).saveItemStack()
                    .setItem(Items.IRON_AXE).saveItemStack()
                    .setItem(Items.STONE_AXE).saveItemStack()
                    .setItem(Items.WOODEN_AXE).saveItemStack()
                    .setInterval(10).setRandom(true)
            );

            for (int x = 3; x < gui.getSize(); x++) {
                ItemStack itemStack = Items.STONE.getDefaultStack();
                itemStack.setCount(x);
                gui.setSlot(x, new GuiElement(itemStack, (index, clickType, actionType) -> {}));
            }

            gui.setSlot(6, new GuiElementBuilder(Items.PLAYER_HEAD)
                    .setSkullOwner(new GameProfile(UUID.fromString("f5a216d9-d660-4996-8d0f-d49053677676"), "patbox"), player.server)
                    .setName(new LiteralText("Patbox's Head"))
                    .glow()
            );

            gui.setSlot(7, new GuiElementBuilder()
                    .setItem(Items.BARRIER)
                    .glow()
                    .setName(new LiteralText("Bye")
                            .setStyle(Style.EMPTY.withItalic(false).withBold(true)))
                    .addLoreLine(new LiteralText("Some lore"))
                    .addLoreLine(new LiteralText("More lore").formatted(Formatting.RED))
                    .setCount(3)
                    .setCallback((index, clickType, actionType) -> {
                        gui.close();
                    })
            );

            gui.setSlot(8, new GuiElementBuilder()
                    .setItem(Items.TNT)
                    .glow()
                    .setName(new LiteralText("Test :)")
                            .setStyle(Style.EMPTY.withItalic(false).withBold(true)))
                    .addLoreLine(new LiteralText("Some lore"))
                    .addLoreLine(new LiteralText("More lore").formatted(Formatting.RED))
                    .setCount(1)
                    .setCallback((index, clickType, actionType) -> {
                        player.sendMessage(new LiteralText("derg "), false);
                        ItemStack item = gui.getSlot(index).getItemStack();
                        if (clickType == ClickType.MOUSE_LEFT) {
                            item.setCount(item.getCount() == 1 ? item.getCount() : item.getCount() - 1);
                        } else if (clickType == ClickType.MOUSE_RIGHT) {
                            item.setCount(item.getCount() + 1);
                        }
                        ((GuiElement) gui.getSlot(index)).setItemStack(item);

                        if (item.getCount() <= player.getEnderChestInventory().size()) {
                            gui.setSlotRedirect(4, new Slot(player.getEnderChestInventory(), item.getCount() - 1, 0, 0));
                        }
                    })
            );
            gui.setSlotRedirect(4, new Slot(player.getEnderChestInventory(), 0, 0,0));

            gui.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static int test2(CommandContext<ServerCommandSource> objectCommandContext) {
        try {
            ServerPlayerEntity player = objectCommandContext.getSource().getPlayer();
            AnvilInputGui gui = new AnvilInputGui(player, true) {
                @Override
                public void onClose() {
                    player.sendMessage(new LiteralText(this.getInput()), false);
                }
            };

            gui.setTitle(new LiteralText("Nice"));
            gui.setSlot(1, new GuiElement(Items.DIAMOND_AXE.getDefaultStack(), (index, clickType, actionType) -> {
                ItemStack item = gui.getSlot(index).getItemStack();
                if (clickType == ClickType.MOUSE_LEFT) {
                    item.setCount(item.getCount() == 1 ? item.getCount() : item.getCount() - 1);
                } else if (clickType == ClickType.MOUSE_RIGHT) {
                    item.setCount(item.getCount() + 1);
                }
                ((GuiElement) gui.getSlot(index)).setItemStack(item);
            }));

            gui.setSlot(2, new GuiElement(Items.SLIME_BALL.getDefaultStack(), (index, clickType, actionType) -> {
                player.sendMessage(new LiteralText(gui.getInput()), false);
            }));

            gui.open();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static int test3(CommandContext<ServerCommandSource> objectCommandContext) {
        try {
            ServerPlayerEntity player = objectCommandContext.getSource().getPlayer();
            BookGui gui = new BookGui(player, player.getMainHandStack());
            gui.open();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static int test4(CommandContext<ServerCommandSource> objectCommandContext) {
        try {
            ServerPlayerEntity player = objectCommandContext.getSource().getPlayer();
            SimpleGui gui = new SimpleGui(ScreenHandlerType.GENERIC_3X3, player, false) {
                @Override
                public void onClose() {
                    super.onClose();

                    SimpleGui gui = new SimpleGui(ScreenHandlerType.GENERIC_9X1, player, false);
                    gui.setTitle(new LiteralText("If you can take it, it's broken"));
                    gui.setSlot(0, new GuiElementBuilder(Items.DIAMOND, 5));
                    gui.open();
                }
            };

            gui.setSlot(0, new GuiElementBuilder(Items.BARRIER, 8).setCallback((x, y, z) -> gui.close()));

            gui.setTitle(new LiteralText("Close gui to test switching"));
            gui.open();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static int test5(CommandContext<ServerCommandSource> objectCommandContext) {
        try {
            ServerPlayerEntity player = objectCommandContext.getSource().getPlayer();
            SimpleGui gui = new SimpleGui(ScreenHandlerType.CRAFTING, player, false) {
                @Override
                public void onCraftRequest(Identifier recipeId, boolean shift) {
                    super.onCraftRequest(recipeId, shift);
                    this.player.sendMessage(new LiteralText(recipeId.toString() + " - " + shift), false);
                }
            };

            gui.setTitle(new LiteralText("Click recipes!"));
            gui.open();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Register a global selection adaptor for SelectionGui
    private static final SelectionGui.SelectionAdaptor<Block> BLOCK_ADAPTOR = SelectionGui.SelectionAdaptors.register(Block.class, input -> new GuiElementBuilder(input.asItem()).glow().build());

    // Create a non-global selection adaptor for SelectionGui
    private static final SelectionGui.SelectionAdaptor<LivingEntity> PLAYER_ADAPTOR = input -> {
        if (input instanceof ServerPlayerEntity) {
            return new GuiElementBuilder(Items.PLAYER_HEAD).setSkullOwner(((ServerPlayerEntity) input).getGameProfile(), ((ServerPlayerEntity) input).server).setName((MutableText) input.getName()).build();
        }

        Item item;
        if (input instanceof SkeletonEntity) {
            item = Items.SKELETON_SKULL;
        } else if (input instanceof ZombieEntity) {
            item = Items.ZOMBIE_HEAD;
        } else if (input instanceof CreeperEntity) {
            item = Items.CREEPER_HEAD;
        } else {
            item = Items.PLAYER_HEAD;
        }

        return new GuiElementBuilder(item).setName((MutableText) input.getName()).build();
    };

    private static int test6(CommandContext<ServerCommandSource> objectCommandContext) {
        try {
            ServerPlayerEntity player = objectCommandContext.getSource().getPlayer();
            List<LivingEntity> players = objectCommandContext.getSource().getMinecraftServer().getOverworld().getEntitiesIncludingUngeneratedChunks(LivingEntity.class, player.getBoundingBox().expand(50));

            SelectionGui<LivingEntity> gui = new SelectionGui<>(player, players, PLAYER_ADAPTOR, (clickType, selection) -> {
                player.teleport(selection.getX(), selection.getY(), selection.getZ());
                player.sendMessage(new LiteralText("Whoosh!"), false);
            });

            gui.setTitle(new LiteralText("Select a mob to teleport to:"));
            gui.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
