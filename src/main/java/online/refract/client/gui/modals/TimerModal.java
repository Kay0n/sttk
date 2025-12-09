package online.refract.client.gui.modals;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import online.refract.client.ClientActionHandler;

public class TimerModal extends Modal {

    private TextFieldWidget timeInput;
    private boolean isUpdating = false; 

    public TimerModal(ClientActionHandler actionHandler) {
        super(actionHandler, "Timer", 140, 160);
    }

    @Override
    public void init(int screenWidth, int screenHeight) {

        modalMarginX = 8;
        modalMarginY = 8;
        elementMarginX = 4;
        elementMarginY = 4;
        super.init(screenWidth, screenHeight);

        // --- Add Preset Buttons ---
        addButtonRow(
            createButtonDef(Text.literal("1m"), () -> actionHandler.setTimer(60)),
            createButtonDef(Text.literal("2m"), () -> actionHandler.setTimer(60 * 2))
        );

        addButtonRow(
            createButtonDef(Text.literal("3m"), () -> actionHandler.setTimer(60 * 3)),
            createButtonDef(Text.literal("4m"), () -> actionHandler.setTimer(60 * 4))
        );
        addButtonRow(
            createButtonDef(Text.literal("5m"), () -> actionHandler.setTimer(60 * 5)),
            createButtonDef(Text.literal("6m"), () -> actionHandler.setTimer(60 * 6))
        );

        addVerticalSpacer(1);

        TextFieldWidget tf = new TextFieldWidget(
            MinecraftClient.getInstance().textRenderer,
            0, 0, 0, 0, Text.literal("Time")
        );

        tf.setMaxLength(5);
        tf.setChangedListener(this::onTimeInputChanged);
        this.timeInput = tf;
        addTextFieldRow(tf);

  
        addVerticalSpacer(1);
        
        addButtonRow(createButtonDef(Text.literal("Set Custom Time"), () -> {
            String input = timeInput.getText();
            int seconds = parseTime(input);
            
            if (seconds > 0) {
                this.actionHandler.setTimer(seconds);
                this.closeModal();
            } else {
                this.timeInput.setEditableColor(0xFFFFFFFF);
            }
        }));
    }


    private void onTimeInputChanged(String newText) {
        if (isUpdating) return; 
        isUpdating = true;

        String raw = newText.replaceAll("[^0-9]", "");

        if (raw.length() > 4) {
            raw = raw.substring(0, 4);
        }

        String formatted = raw;
        
        if (raw.length() >= 3) {
            String minutes = raw.substring(0, raw.length() - 2);
            String seconds = raw.substring(raw.length() - 2);
            formatted = minutes + ":" + seconds;
        }

        if (!formatted.equals(newText)) {
            this.timeInput.setText(formatted);
            this.timeInput.setCursor(formatted.length(), false); 
        }

        this.timeInput.setEditableColor(0xFFFFFFFF);

        isUpdating = false;
    }


    /**
     * parse time formats:
     * "5:00" -> 300 seconds
     * "20:17" -> 1217 seconds
     * "13" -> 13 seconds
     */
    private int parseTime(String input) {
        if (input == null || input.isEmpty()) return -1;
        input = input.trim();

        try {
            if (input.contains(":")) {
                String[] parts = input.split(":");
                if (parts.length == 2) {
                    String minStr = parts[0].isEmpty() ? "0" : parts[0];
                    String secStr = parts[1].isEmpty() ? "0" : parts[1];
                    
                    int min = Integer.parseInt(minStr);
                    int sec = Integer.parseInt(secStr);
                    return (min * 60) + sec;
                }
            } else {
                return Integer.parseInt(input);
            }
        } 
        catch (NumberFormatException e) {       
        }
        return -1;
    }
    
    @Override
    public void openModal() {
        super.openModal();
        if(this.timeInput != null) {
            this.timeInput.setText("");
            this.timeInput.setEditableColor(0xFFFFFFFF); 
            this.timeInput.setFocused(true); 
        }
    }
}