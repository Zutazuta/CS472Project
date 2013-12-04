import java.awt.Color;
import java.awt.event.ActionEvent;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import javax.swing.BorderFactory;
import javax.swing.border.Border;

 
public class WordPredictionPanel extends JFrame implements DocumentListener {
     
    private JTextField textField;
    private JLabel fieldLabel;
    private JLabel predictionLabel;
	
	private WordNetwork net1;
	private WordNetwork net2;
	
	public WordPredictionPanel(WordNetwork network1, WordNetwork network2){
		net1 = network1;
		net2 = network2;
	
		setTitle("Word Prediction");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		
		textField = new JTextField(10);		
		Border border = BorderFactory.createLineBorder(Color.GRAY, 1);
        textField.setBorder(border);
		textField.getDocument().addDocumentListener(this);
		
		fieldLabel = new JLabel("Say Something:");
		predictionLabel = new JLabel("");
		
		JPanel panel = new JPanel();
		panel.add(fieldLabel);
		panel.add(textField);
		panel.add(predictionLabel);
		
		add(panel);
		pack();
		setSize(350, 120);
		setVisible(true);
    }
	
    private void predictNextWord() {
		predictionLabel.setText(net1.predictNextWord(textField.getText(), 4) + " | " + net2.predictNextWord(textField.getText(), 4));
    }
	
    public void insertUpdate(DocumentEvent event) {
		predictNextWord();
    }
	
    public void removeUpdate(DocumentEvent event) {
		predictNextWord();
    }
     
    public void changedUpdate(DocumentEvent ev) {
		predictNextWord();
    }
    
}