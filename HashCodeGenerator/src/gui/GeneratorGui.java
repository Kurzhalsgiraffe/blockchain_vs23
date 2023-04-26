package gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import javax.swing.*;

import model.NativeBlock;

public class GeneratorGui {
	// the frame
	private JFrame jFrame = new JFrame("Ein einfacher Hashcode-Generator - J. Röhrle, September 2022");

	// the panels
	private JPanel inputPanel_1 = new JPanel();
	private JPanel inputPanel_2 = new JPanel();
	private JPanel outputPanel_1 = new JPanel();
	private JPanel outputPanel_2 = new JPanel();
	private JPanel outputPanel_3 = new JPanel();
	private JPanel actionPanel = new JPanel();
	private JPanel statusPanel = new JPanel();

	// the components
	// the text labels
	private JLabel hashAlgorithmLabel = new JLabel("Algorithmus: ");
	private JLabel prefixLabel = new JLabel("Prefix: ");
	private JLabel inputLabel = new JLabel("Input: ");
	private JLabel startHashCodeLabel = new JLabel("Start Hash Code: ");
	private JLabel prefixHashCodeLabel = new JLabel("Prefix Hash Code: ");
	private JLabel nonceLabel = new JLabel("Nonce: ");
	private JLabel timeLabel = new JLabel("Zeitverbrauch: ");
	private JLabel msLabel = new JLabel("ms");
	private JLabel statusLabel = new JLabel("Status: ");

	// the text fields
	private JTextField hashAlgorithmTextField = new JTextField(10);
	private JTextField prefixTextField = new JTextField(10);
	private JTextField dataTextField = new JTextField(50);
	private JTextField startHashCodeTextField = new JTextField(50);
	private JTextField prefixHashCodeTextField = new JTextField(50);
	private JTextField nonceTextField = new JTextField(10);
	private JTextField timeTextField = new JTextField(10);
	private JTextField statusTextField = new JTextField(50);

	// the buttons
	private JButton calculateButton = new JButton("Calculate");
	private JButton exitButton = new JButton("Exit");

	public GeneratorGui() {
		// settings
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		jFrame.setSize(d.width / 2, d.height / 2);
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jFrame.setLayout(new GridLayout(0, 1));
		jFrame.setLocationRelativeTo(null);

		inputPanel_1.setLayout(new FlowLayout());
		inputPanel_2.setLayout(new FlowLayout());
		outputPanel_1.setLayout(new FlowLayout());
		outputPanel_2.setLayout(new FlowLayout());
		outputPanel_3.setLayout(new FlowLayout());
		actionPanel.setLayout(new FlowLayout());
		statusPanel.setLayout(new FlowLayout());

		// setting text fields
		startHashCodeTextField.setEditable(false);
		prefixHashCodeTextField.setEditable(false);
		nonceTextField.setEditable(false);
		timeTextField.setEditable(false);
		statusTextField.setEditable(false);

		hashAlgorithmTextField.setText("SHA-1");
		dataTextField.setText("dies ist ein Text");
		prefixTextField.setText("0");

		// adding components to panels

		inputPanel_1.add(hashAlgorithmLabel);
		inputPanel_1.add(hashAlgorithmTextField);
		inputPanel_1.add(prefixLabel);
		inputPanel_1.add(prefixTextField);

		inputPanel_2.add(inputLabel);
		inputPanel_2.add(dataTextField);
		outputPanel_1.add(startHashCodeLabel);
		outputPanel_1.add(startHashCodeTextField);

		outputPanel_2.add(nonceLabel);
		outputPanel_2.add(nonceTextField);
		outputPanel_2.add(timeLabel);
		outputPanel_2.add(timeTextField);
		outputPanel_2.add(msLabel);
		
		outputPanel_3.add(prefixHashCodeLabel);
		outputPanel_3.add(prefixHashCodeTextField);

		statusPanel.add(statusLabel);
		statusPanel.add(statusTextField);

		actionPanel.add(calculateButton);
		actionPanel.add(exitButton);

		// adding panels to frame
		jFrame.add(inputPanel_1);
		jFrame.add(inputPanel_2);
		jFrame.add(outputPanel_1);
		jFrame.add(outputPanel_2);
		jFrame.add(outputPanel_3);
		jFrame.add(actionPanel);
		jFrame.add(statusPanel);

		jFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				System.exit(0);
			}
		});

		exitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		calculateButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// System.out.println("hashAlgorithmTextField.getText()= " + hashAlgorithmTextField.getText());
				// System.out.println("dataTextField.getText()= " + dataTextField.getText());

				statusTextField.setText("");
				try {
					System.gc();
					long startTime = System.currentTimeMillis();
					//System.out.println( "startTime = " + startTime);
					
					NativeBlock nativeBlock = new NativeBlock(hashAlgorithmTextField.getText(), dataTextField.getText(),
							Integer.parseInt(prefixTextField.getText()));

					// System.out.println( "nativeBlock = " + nativeBlock );
					long stopTime = System.currentTimeMillis();
					// System.out.println( "stopTime = " + stopTime);
					// System.out.println( "(stopTime - startTime)) = " + (stopTime - startTime));
					timeTextField.setText(String.format("%d", (stopTime - startTime)));
					
					startHashCodeTextField.setText(nativeBlock.getOriginHash());
					prefixHashCodeTextField.setText(nativeBlock.getHash());
					nonceTextField.setText(String.format("%d", nativeBlock.getNonce()));
					System.gc();
				} catch (NumberFormatException e0) {
					statusTextField.setText("Ganzzahlige Eingabe in Feld 'Prefix'");
				} catch (NegativeArraySizeException e1) {
					statusTextField.setText("Negativer Prefix unzulaessig!!");
				} catch (NoSuchAlgorithmException e1) {
					statusTextField.setText("Unbekannter Hash-Algorithmus!!");
					hashAlgorithmTextField.setText("SHA-1");
				} catch (UnsupportedEncodingException e1) {
					statusTextField.setText("UnsupportedEncodingException!!");
				}
			}
		});

		jFrame.setVisible(true);

	}

}
