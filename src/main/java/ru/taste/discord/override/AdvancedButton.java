package ru.taste.discord.override;

import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.taste.discord.override.enums.EmojiEnum;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AdvancedButton {
    //fields
    private ButtonStyle style;
    private String id;
    private String label;
    private EmojiEnum emoji;
    private String[] values;
    private boolean disabled;

    //builders
    public static AdvancedButton builder() {
        return new AdvancedButton();
    }

    public static AdvancedButton primary() {
        return new AdvancedButton().style(ButtonStyle.PRIMARY);
    }

    public static AdvancedButton success() {
        return new AdvancedButton().style(ButtonStyle.SUCCESS);
    }

    public static AdvancedButton danger() {
        return new AdvancedButton().style(ButtonStyle.DANGER);
    }

    public static AdvancedButton secondary() {
        return new AdvancedButton().style(ButtonStyle.SECONDARY);
    }

    public static AdvancedButton back() {
        return new AdvancedButton().style(ButtonStyle.SECONDARY).label("Back").emoji(EmojiEnum.BACK);
    }

    //setters
    public AdvancedButton style(ButtonStyle style) {
        this.style = style;
        return this;
    }

    public AdvancedButton id(String id) {
        this.id = id;
        return this;
    }

    public AdvancedButton label(String label) {
        this.label = label;
        return this;
    }

    public AdvancedButton emoji(EmojiEnum emoji) {
        this.emoji = emoji;
        return this;
    }

    public AdvancedButton values(String... values) {
        this.values = values;
        return this;
    }

    public AdvancedButton disabled(boolean disabled) {
        this.disabled = disabled;
        return this;
    }

    //build
    public Button build() {
        return Button.of(style, valuesButtonId(), label).withEmoji(nullableEmoji()).withDisabled(disabled);
    }

    public Button build(Emoji emoji) {
        return Button.of(style, valuesButtonId(), label).withEmoji(emoji).withDisabled(disabled);
    }

    //utilities
    private String valuesButtonId() {
        if (values == null) {
            return id;
        }

        StringBuilder buttonId = new StringBuilder(id);

        for (String value : values) {
            buttonId.append("/").append(value);
        }

        return buttonId.toString();
    }

    private Emoji nullableEmoji() {
        return emoji == null ? null : emoji.get();
    }
}
