package eu.pb4.sgui.api.gui;

import com.google.common.collect.Lists;
import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.elements.GuiElementInterface;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;

public class SelectionGui<T> extends SimpleGui {

    protected final List<T> options = Lists.newArrayList();
    protected final BiConsumer<ClickType, T> callback;

    public SelectionGui(ServerPlayerEntity player, Iterable<T> options, BiConsumer<ClickType, T> callback) {
        super(getTypeFromSize((int) options.spliterator().estimateSize()), player, false);
        this.callback = callback;
        this.setTitle(new LiteralText("Select an option:"));
        for (T option : options) {
            this.addSlot(((SelectionAdaptor<T>) SelectionAdaptors.getAdaptor(option.getClass())).toElement(option));
            this.options.add(option);
        }
    }

    public SelectionGui(ServerPlayerEntity player, Iterable<T> options, SelectionAdaptor<T> adaptor, BiConsumer<ClickType, T> callback) {
        super(getTypeFromSize((int) options.spliterator().estimateSize()), player, false);
        this.callback = callback;
        this.setTitle(new LiteralText("Select an option:"));
        for (T option : options) {
            this.addSlot(adaptor.toElement(option));
            this.options.add(option);
        }
    }


    @Override
    public boolean click(int index, ClickType type, SlotActionType action) {
        if (index < this.options.size()) {
            this.callback.accept(type, this.options.get(index));
        }
        return false;
    }

    private static ScreenHandlerType<?> getTypeFromSize(int size) {
        switch ((int) Math.ceil(size / 9f)) {
            case 1:
                return ScreenHandlerType.GENERIC_9X1;
            case 2:
                return ScreenHandlerType.GENERIC_9X2;
            case 3:
                return ScreenHandlerType.GENERIC_9X3;
            case 4:
                return ScreenHandlerType.GENERIC_9X4;
            case 5:
                return ScreenHandlerType.GENERIC_9X5;
            default:
                return ScreenHandlerType.GENERIC_9X6;
        }
    }

    public static class SelectionAdaptors {
        private static final HashMap<Class<?>, SelectionAdaptor<?>> ADAPTORS = new HashMap<>();

        public static SelectionAdaptor<Identifier> IDENTIFIER = register(Identifier.class, input -> new GuiElementBuilder(Items.OAK_SIGN).setName(new LiteralText(StringUtils.capitalize(input.getPath()))).build());
        public static SelectionAdaptor<String> STRING = register(String.class, input -> new GuiElementBuilder(Items.OAK_SIGN).setName(new LiteralText(input)).build());
        public static SelectionAdaptor<Object> OBJECT = register(Object.class, input -> new GuiElementBuilder(Items.SPRUCE_SIGN).setName(new LiteralText(input.toString())).build());

        public static <E> SelectionAdaptor<E> register(Class<E> type, SelectionAdaptor<E> adaptor) {
            ADAPTORS.put(type, adaptor);
            return adaptor;
        }

        public static SelectionAdaptor<?> getAdaptor(Class<?> type) {
            return ADAPTORS.getOrDefault(type, OBJECT);
        }
    }

    public interface SelectionAdaptor<T> {
        GuiElementInterface toElement(T input);
    }

}
