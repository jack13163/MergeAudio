package com.audio.form;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.EventQueue;
import java.awt.Label;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;

import com.audio.main.AudioHelper;

public class MainForm {

	private JFrame frame;
	private TextArea txtInput;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainForm window = new MainForm();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainForm() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame("粤语翻译");
		frame.setBounds(100, 100, 450, 343);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Label label = new Label("Please input what you want to translate:");
		frame.getContentPane().add(label, BorderLayout.NORTH);

		Button btnTranslate = new Button("Translate");
		btnTranslate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// 开辟一个线程，防止MainForm线程阻塞
				Thread thread = new Thread(new Runnable() {

					public void run() {
						String content = txtInput.getText();
						String audioPath = AudioHelper.translate(content);
						// 音频合并到新的文件中
						String filePath = "merged//"
								+ (content.length() > 10 ? content.substring(0, 10) + "..." : content) + ".wav";
						String[] audioPaths = audioPath.split(",");

						try {
							AudioHelper.merge(filePath, audioPaths);
						} catch (IOException e) {
							e.printStackTrace();
						} catch (UnsupportedAudioFileException e) {
							e.printStackTrace();
						}
					}

				});

				thread.start();
			}
		});
		frame.getContentPane().add(btnTranslate, BorderLayout.SOUTH);

		txtInput = new TextArea();
		frame.getContentPane().add(txtInput, BorderLayout.CENTER);
	}

}
