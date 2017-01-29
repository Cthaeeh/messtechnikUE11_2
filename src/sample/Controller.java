package sample;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

import java.util.function.UnaryOperator;

public class Controller {
    @FXML
    private TextArea output_TextArea;
    @FXML
    private TextField field_RefVolatge;
    @FXML
    private TextField field_Measured_Voltage;
    @FXML
    private TextField field_NumOfBits;

    @FXML
    public void initialize() {
        //TODO disallow non double value input for textFields.
        output_TextArea.setEditable(false);
        initializeBitInputField();
        initializeVoltageInputFields();

    }

    private void initializeVoltageInputFields() {
        //TODO only allow doubles
    }

    private void initializeBitInputField() {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String text = change.getText();

            if (text.matches("[0-9]*")) {
                return change;
            }

            return null;
        };
        TextFormatter<String> textFormatter = new TextFormatter<>(filter);
        field_NumOfBits.setTextFormatter(textFormatter);
    }

    @FXML
    private void onBtnCalcClicked(){
        double U_measured = Double.valueOf(field_Measured_Voltage.getText());
        output_TextArea.clear();
        output_TextArea.appendText("U_LSB (least significant bit) : "+  calculateU_LSB()  +" V" + System.getProperty("line.separator"));
        output_TextArea.appendText( "Vergleichsspannungen der einzelnen Komparatoren:" + System.getProperty("line.separator"));
        double[] compVoltages = calculateCompVoltages();
        for(int i = 1; i< compVoltages.length+1; i++){
            output_TextArea.appendText( "Komparator " + i + " : " +compVoltages[i-1] +" V -> " + ((compVoltages[i-1]>U_measured)? "0":"1")  + System.getProperty("line.separator"));
        }
        output_TextArea.appendText("U_Dig : "+  calculateU_Dig(compVoltages,U_measured)  +" V" + System.getProperty("line.separator"));
        output_TextArea.appendText("Error is: "+  (calculateU_Dig(compVoltages,U_measured)-U_measured)  +" V" + System.getProperty("line.separator"));
    }

    private double calculateU_Dig(double[] compVoltages, double U_measured) {
        for(int i = 0; i < compVoltages.length-1; i++){
            if(U_measured>compVoltages[i]&&U_measured<compVoltages[i+1]){
                return compVoltages[i] + calculateU_LSB()/2;
            }
        }
        if(U_measured>compVoltages[compVoltages.length-1])return compVoltages[compVoltages.length-1]+calculateU_LSB()/2;
        return 0;
    }

    /**
     * Calculates all Comparator Voltages
     * @return an array of comprator voltages.
     */
    private double[] calculateCompVoltages() {
        double U_LSB = calculateU_LSB();
        int numOfBits = Integer.valueOf(field_NumOfBits.getText());
        double[] compVoltages = new double[(int)(Math.pow(2,numOfBits))-1];
        for(int i = 0; i < compVoltages.length; i++){
            compVoltages[i]=(U_LSB)/2+U_LSB*i;
        }
        return compVoltages;
    }

    /**
     * Calculates the voltage for the least significant bit.
     * @return
     */
    private double calculateU_LSB() {
        double U_ref = Double.valueOf(field_RefVolatge.getText());
        int numOfBits = Integer.valueOf(field_NumOfBits.getText());
        double U_LSB = U_ref/(Math.pow(2,numOfBits));
        return U_LSB;
    }

}
