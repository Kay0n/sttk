package online.refract.client.gui.modals;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import online.refract.client.ClientActionHandler;
import online.refract.client.gui.Modal.ButtonData;
import online.refract.client.gui.Modal;

public class TimerModal extends Modal {

    private EditBox timeInput;
    private boolean isUpdating = false; 

    public TimerModal(ClientActionHandler actionHandler) {
        super(actionHandler, "Timer", 140, 170, 5, 10);
    }

    @Override
    public void init(int screenWidth, int screenHeight) {


        super.init(screenWidth, screenHeight);

        // --- Add Preset Buttons ---
        addButtonRow(
            new ButtonData(Component.literal("1m"), () -> actionHandler.setTimer(60)),
            new ButtonData(Component.literal("2m"), () -> actionHandler.setTimer(60 * 2))
        );
        addButtonRow(
            new ButtonData(Component.literal("3m"), () -> actionHandler.setTimer(60 * 3)),
            new ButtonData(Component.literal("4m"), () -> actionHandler.setTimer(60 * 4))
        );
        addButtonRow(
            new ButtonData(Component.literal("5m"), () -> actionHandler.setTimer(60 * 5)),
            new ButtonData(Component.literal("6m"), () -> actionHandler.setTimer(60 * 6))
        );

        addSpacerRow();

        EditBox editBox = createEditbox("Time", 5);
        editBox.setResponder(this::onTimeInputChanged);
        this.timeInput = editBox;
        addEditBoxRow(editBox);

        // addSpacerRow();
        
        addButtonRow(new ButtonData(Component.literal("Set Custom Time"), this::setCustomTime));
    }


    private void setCustomTime() {
        String input = timeInput.getValue();
        int seconds = parseTime(input);
        
        if (seconds > 0) {
            this.actionHandler.setTimer(seconds);
            this.closeModal();
        } else {
            this.timeInput.setTextColor(0xFFFFFFFF);
        }
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
            this.timeInput.setValue(formatted);
            this.timeInput.moveCursorTo(formatted.length(), false); 
        }

        this.timeInput.setTextColor(0xFFFFFFFF);

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
            this.timeInput.setValue("");
            this.timeInput.setTextColor(0xFFFFFFFF); 
            this.timeInput.setFocused(true); 
        }
    }
}